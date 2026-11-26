package com.pokedaisy.app

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import com.pokedaisy.app.companion.i18n.AppLanguage
import com.pokedaisy.app.companion.i18n.L10n
import com.pokedaisy.app.companion.ui.DocsSource
import okio.Path.Companion.toOkioPath
import java.io.File

/**
 * The manual source both screens read. English and Simplified Chinese keep separate caches and URLs; the
 * Chinese one is seeded from the bundled manual so HELP works offline on first use, and the source picks
 * the language at each call (the LANGUAGE row can change while the app runs).
 */
class StoreDocsSource(context: Context) : DocsSource {
    private val app = context.applicationContext
    private fun chinese() = L10n.language == AppLanguage.ZH_CN
    private fun cache() = DocsStore.cache(app, chinese())
    private fun decode(bytes: ByteArray) = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()

    override fun cached() = cache().cached()
    override fun refresh() = cache().refresh(if (chinese()) DocsApi.ZH_URL else DocsApi.URL)
    override fun image(manual: DocsApi.Manual, image: DocsApi.Block.Image) = cache().image(manual, image)?.let(::decode)
}

/** Compatibility entry points used by the library's background prefetch. */
object DocsStore {
    internal fun cache(context: Context, chinese: Boolean = false) = DocsCache.at(
        if (chinese) chineseDir(context).toOkioPath() else (context.applicationContext.filesDir.toOkioPath() / "docs"),
        BuildConfig.VERSION_NAME,
    )

    /** The bundled Chinese manual, copied into its cache once so HELP reads it before any download. */
    private fun chineseDir(context: Context): File {
        val dir = File(context.applicationContext.filesDir, "docs-zh-hans")
        val manual = File(dir, "docs.json")
        if (!manual.isFile) runCatching {
            dir.mkdirs()
            context.assets.open("docs-zh-hans.json").use { input -> manual.outputStream().use { input.copyTo(it) } }
        }
        return dir
    }

    fun prefetch(context: Context) {
        cache(context).prefetch()
        cache(context, chinese = true).prefetch()
    }
}
