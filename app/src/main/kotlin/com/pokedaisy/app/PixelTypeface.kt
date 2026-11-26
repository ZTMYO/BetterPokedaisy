package com.pokedaisy.app

import android.content.Context
import android.graphics.Typeface
import android.graphics.fonts.Font
import android.graphics.fonts.FontFamily as AndroidFontFamily
import android.os.Build
import androidx.compose.ui.text.font.Font as ComposeFont
import androidx.compose.ui.text.font.FontFamily
import com.pokedaisy.app.companion.ui.theme.FontSource
import okio.Path

/**
 * The app's one font: Pixel Operator, with PixelMplusJP (scripts/gen_jp_font.py:
 * the same pixel grid) filling in the kana / kanji it lacks, GalmuriKO
 * (scripts/gen_ko_font.py, likewise) the Hangul and FusionPixelZH
 * (scripts/gen_zh_font.py: same grid again) the hanzi of the Chinese fan
 * translations, glyph by glyph - so CJK stays pixel art and Latin text keeps
 * Pixel Operator. Android 10+ (Typeface.CustomFallbackBuilder); older ones
 * fall back to the system's CJK font.
 *
 * Built once per process and shared by every text on both screens: building it
 * loads the 1 MB PixelMplusJP, the 1.5 MB GalmuriKO and the 1.4 MB FusionPixelZH, and a
 * family per call site (every GbaText) cost ~1 MB and several ms of main thread per
 * text - tens of MB per tab switch, which ran the app out of memory when tabs were
 * switched quickly (v1.1.0).
 */
/** The screens' fonts on Android (companion.ui.theme.FontSource): the families below, from the app's assets. */
class AndroidFonts(context: Context) : FontSource {
    private val app = context.applicationContext
    override fun pixel(): FontFamily = pixelFontFamily(app)
    override fun pixelSmall(): FontFamily = pixelSmallFontFamily(app)
    override fun smooth(bold: Boolean): FontFamily = atkinsonFamily(app, bold)
    override fun reading(): FontFamily = readingFontFamily(app)
    override fun game(file: Path): FontFamily? = gameFontFamily(app, file.toFile())
}

/** The Chinese face first when the Chinese tables are on screen: the same hanzi is drawn differently in Japanese. */
private fun chineseFontFirst(): Boolean =
    com.pokedaisy.app.companion.i18n.L10n.language == com.pokedaisy.app.companion.i18n.AppLanguage.ZH_CN

fun pixelFontFamily(context: Context, preferChinese: Boolean = chineseFontFirst()): FontFamily = synchronized(fontLock) {
    pixelFamilies.getOrPut(preferChinese) { buildPixelFamily(context.applicationContext, preferChinese) }
}

/** SETTINGS > FONT's PIXEL SMALL: PixelSmall.ttf over the same fallbacks as [pixelFontFamily], built once. */
fun pixelSmallFontFamily(context: Context): FontFamily {
    val chinese = chineseFontFirst()
    synchronized(fontLock) { (if (chinese) pixelSmallFamilyZh else pixelSmallFamily)?.let { return it } }
    return synchronized(fontLock) {
        (if (chinese) pixelSmallFamilyZh else pixelSmallFamily) ?: buildSmallFamily(context.applicationContext, chinese).also {
            if (chinese) pixelSmallFamilyZh = it else pixelSmallFamily = it
        }
    }
}

private fun buildSmallFamily(context: Context, chineseFirst: Boolean): FontFamily {
    val plain = { FontFamily(ComposeFont("fonts/PixelSmall.ttf", context.assets)) }
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return plain()
    return runCatching {
        val assets = assetFamilies(context)
        val small = AndroidFontFamily.Builder(Font.Builder(context.assets, "fonts/PixelSmall.ttf").build()).build()
        val builder = Typeface.CustomFallbackBuilder(small)
        // Without the Chinese face in the chain, every hanzi in this font would draw as "?".
        chain(assets, chineseFirst).forEach { builder.addCustomFallback(it) }
        FontFamily(androidx.compose.ui.text.font.Typeface(builder.setSystemFallback("sans-serif").build()))
    }.getOrElse { plain() }
}

@Volatile private var pixelSmallFamily: FontFamily? = null
@Volatile private var pixelSmallFamilyZh: FontFamily? = null

/** PixelMplusJP, GalmuriKO and FusionPixelZH in the order that language's glyphs need. */
private fun chain(assets: AssetFonts, chineseFirst: Boolean): List<AndroidFontFamily> =
    if (chineseFirst) listOf(assets.chinese, assets.japanese, assets.korean)
    else listOf(assets.japanese, assets.korean, assets.chinese)

/** A game's own font file ([file], e.g. Yellow's from Gen1Art) over Pixel Operator for anything it lacks. */
fun gameFontFamily(context: Context, file: java.io.File, preferChinese: Boolean = chineseFontFirst()): FontFamily? {
    val stamp = file.absolutePath + ":" + file.length() + ":" + file.lastModified() + ":" + preferChinese
    synchronized(fontLock) { gameFamilies[stamp]?.let { return it } }
    val family = runCatching {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return@runCatching FontFamily(ComposeFont(file))
        val assets = assetFamilies(context.applicationContext)
        val builder = Typeface.CustomFallbackBuilder(AndroidFontFamily.Builder(Font.Builder(file).build()).build())
        if (preferChinese) {
            builder.addCustomFallback(assets.chinese)
                .addCustomFallback(assets.japanese)
                .addCustomFallback(assets.korean)
                .addCustomFallback(assets.pixel)
        } else {
            builder.addCustomFallback(assets.pixel)
                .addCustomFallback(assets.japanese)
                .addCustomFallback(assets.korean)
                .addCustomFallback(assets.chinese)
        }
        val typeface = builder.setSystemFallback("sans-serif").build()
        FontFamily(androidx.compose.ui.text.font.Typeface(typeface))
    }.getOrNull() ?: return null
    synchronized(fontLock) { gameFamilies[stamp] = family }
    return family
}

private val fontLock = Any()
private val pixelFamilies = HashMap<Boolean, FontFamily>()
private var assetFonts: AssetFonts? = null
// A game font per file version (a rescan rewrites it); a handful at most.
private val gameFamilies = HashMap<String, FontFamily>()

private class AssetFonts(
    val pixel: AndroidFontFamily,
    val japanese: AndroidFontFamily,
    val korean: AndroidFontFamily,
    val chinese: AndroidFontFamily,
)

/** Pixel Operator, PixelMplusJP, GalmuriKO and FusionPixelZH as platform families, loaded once (Android 10+ only). */
private fun assetFamilies(context: Context): AssetFonts = synchronized(fontLock) {
    assetFonts ?: run {
        fun family(path: String) = AndroidFontFamily.Builder(Font.Builder(context.assets, path).build()).build()
        AssetFonts(
            family("fonts/PixelOperator.ttf"),
            family("fonts/PixelMplusJP.ttf"),
            family("fonts/GalmuriKO.ttf"),
            family("fonts/FusionPixelZH.ttf"),
        ).also { assetFonts = it }
    }
}

private fun buildPixelFamily(context: Context, preferChinese: Boolean): FontFamily {
    val plain = { FontFamily(ComposeFont("fonts/PixelOperator.ttf", context.assets)) }
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return plain()
    return runCatching {
        val assets = assetFamilies(context)
        val builder = Typeface.CustomFallbackBuilder(assets.pixel)
        if (preferChinese) {
            builder.addCustomFallback(assets.chinese)
                .addCustomFallback(assets.japanese)
                .addCustomFallback(assets.korean)
        } else {
            builder.addCustomFallback(assets.japanese)
                .addCustomFallback(assets.korean)
                .addCustomFallback(assets.chinese)
        }
        val typeface = builder.setSystemFallback("sans-serif").build()
        FontFamily(androidx.compose.ui.text.font.Typeface(typeface))
    }.getOrElse { plain() }
}

/**
 * The docs reader's text font: Atkinson Hyperlegible (regular, bold, italic). Long reading in Pixel Operator
 * is tiring, so the manual's paragraphs use this; its headings and the app's own labels stay pixel art.
 * Built once per process, like [pixelFontFamily].
 */
fun readingFontFamily(context: Context): FontFamily {
    readingFamily?.let { return it }
    return synchronized(fontLock) {
        readingFamily ?: run {
            val assets = context.applicationContext.assets
            FontFamily(
                ComposeFont("fonts/AtkinsonHyperlegible-Regular.ttf", assets),
                ComposeFont("fonts/AtkinsonHyperlegible-Bold.ttf", assets, androidx.compose.ui.text.font.FontWeight.Bold),
                ComposeFont("fonts/AtkinsonHyperlegible-Italic.ttf", assets, style = androidx.compose.ui.text.font.FontStyle.Italic),
            ).also { readingFamily = it }
        }
    }
}

@Volatile private var readingFamily: FontFamily? = null

/** Atkinson Hyperlegible alone, regular or bold (SETTINGS > FONT's SMOOTH / SMOOTH BOLD), one per process like the others. */
fun atkinsonFamily(context: Context, bold: Boolean): FontFamily {
    (if (bold) atkinsonBold else atkinsonRegular)?.let { return it }
    return synchronized(fontLock) {
        val assets = context.applicationContext.assets
        if (bold) atkinsonBold ?: FontFamily(ComposeFont("fonts/AtkinsonHyperlegible-Bold.ttf", assets)).also { atkinsonBold = it }
        else atkinsonRegular ?: FontFamily(ComposeFont("fonts/AtkinsonHyperlegible-Regular.ttf", assets)).also { atkinsonRegular = it }
    }
}

@Volatile private var atkinsonRegular: FontFamily? = null
@Volatile private var atkinsonBold: FontFamily? = null
