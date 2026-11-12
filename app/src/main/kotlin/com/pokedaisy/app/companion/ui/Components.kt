package com.pokedaisy.app.companion.ui

import com.pokedaisy.app.companion.i18n.tr
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokedaisy.app.companion.data.TypeMatchup
import com.pokedaisy.app.companion.ui.theme.AppColors
import com.pokedaisy.app.companion.ui.theme.pixelFontFamily

/** Where each type's tag starts in the games' sheet, in 8x8 tiles from its top left, 16 to a row (sMenuInfoIcons). */
private val TYPE_TAG_TILES = mapOf(
    "Normal" to 0x20, "Fire" to 0x24, "Water" to 0x28, "Grass" to 0x2C, "Electric" to 0x40, "Rock" to 0x44, "Ground" to 0x48,
    "Ice" to 0x4C, "Flying" to 0x60, "Fighting" to 0x64, "Ghost" to 0x68, "Bug" to 0x6C, "Poison" to 0x80, "Psychic" to 0x84,
    "Steel" to 0x88, "Dark" to 0x8C, "Dragon" to 0xA0, "???" to 0xA4,
)
private const val TYPE_TAG_W = 32
private const val TYPE_TAG_H = 12
/** Each this much of a badge's font size is one more pixel of tag per GBA pixel: 12 sp 1x, a move card's 16 sp 2x. */
private const val TYPE_TAG_SP = 8f

/** The running game's sheet of type tags, read from its ROM: the original FireRed / Emerald engines in English (the
 * words are in the picture, so a hack's own types or another language keep the drawn badge). Null = draw it. */
@Composable
private fun rememberTypeTags(): androidx.compose.ui.graphics.ImageBitmap? {
    val game = com.pokedaisy.app.companion.data.activeGame
    val path = when {
        com.pokedaisy.app.companion.data.romLanguage != 'E' -> null
        com.pokedaisy.app.companion.data.runningFireRedHack != null || com.pokedaisy.app.companion.data.runningEmeraldHack != null -> null
        game == com.pokedaisy.app.companion.data.GameKind.FIRERED -> com.pokedaisy.app.companion.data.RomArt.TYPE_TAGS_FIRERED
        game == com.pokedaisy.app.companion.data.GameKind.EMERALD -> com.pokedaisy.app.companion.data.RomArt.TYPE_TAGS_EMERALD
        else -> null
    } ?: return null
    val context = androidx.compose.ui.platform.LocalContext.current
    val generation = rememberArtGeneration()
    return androidx.compose.runtime.remember(path, generation) {
        GameArt.get(context, path)?.let { it.asImageBitmap() }
    }
}

@Composable
fun TypeBadge(type: String, modifier: Modifier = Modifier, fontSize: TextUnit = 15.sp) {
    MoveTypeBadge(type, modifier, fontSize, iconSize = (fontSize.value * 0.95f).dp)
}

@Composable
fun MoveTypeBadge(type: String, modifier: Modifier = Modifier, fontSize: TextUnit = 14.sp, iconSize: Dp = 14.dp) {
    val color = AppColors.typeColors[type] ?: AppColors.muted
    val icon = typeIconRes(type)
    Box(
        modifier = modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(50))
            .background(color)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            icon?.let {
                // Fit keeps the glyph's own aspect (water and electric are tall).
                Image(
                    painter = painterResource(it), contentDescription = null,
                    modifier = Modifier.size(iconSize), contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(Color.White),
                )
            }
            Text(
                com.pokedaisy.app.companion.data.typeLabel(type),
                color = Color.White, fontSize = fontSize, fontFamily = pixelFontFamily(), maxLines = 1,
            )
        }
    }
}

@Composable
fun MoveTypeIcon(type: String, modifier: Modifier = Modifier, size: Dp = 16.dp) {
    val color = AppColors.typeColors[type] ?: AppColors.muted
    val icon = typeIconRes(type)
    Box(
        modifier = modifier
            .size(size)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(size / 5))
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        if (icon != null) {
            Image(
                painter = painterResource(icon), contentDescription = com.pokedaisy.app.companion.data.typeLabel(type),
                modifier = Modifier.size(size * 0.9f), contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Color.White),
            )
        } else {
            Text(com.pokedaisy.app.companion.data.typeLabel(type), color = Color.White, fontSize = 8.sp, fontFamily = pixelFontFamily(), maxLines = 1)
        }
    }
}

private enum class EffectKind { SUPER, NORMAL, WEAK, NONE }

private fun effectKind(pct: Int): EffectKind = when {
    pct == 0 -> EffectKind.NONE
    pct > 100 -> EffectKind.SUPER
    pct < 100 -> EffectKind.WEAK
    else -> EffectKind.NORMAL
}

private fun effectLabel(kind: EffectKind): String = when (kind) {
    EffectKind.SUPER -> "SUPER EFFECTIVE"
    EffectKind.NORMAL -> "NORMAL EFFECT"
    EffectKind.WEAK -> "NOT VERY EFFECTIVE"
    EffectKind.NONE -> "NO EFFECT"
}

@Composable
private fun EffectIcon(kind: EffectKind, color: Color, modifier: Modifier) {
    Canvas(modifier) {
        val s = size.minDimension
        val w = s * 0.14f
        when (kind) {
            EffectKind.SUPER -> {
                drawCircle(color, radius = s * 0.38f, style = Stroke(w))
                drawCircle(color, radius = s * 0.18f, style = Stroke(w))
            }
            EffectKind.NORMAL -> drawCircle(color, radius = s * 0.34f, style = Stroke(w))
            EffectKind.WEAK -> {
                val p = Path().apply {
                    moveTo(s * 0.5f, s * 0.10f)
                    lineTo(s * 0.90f, s * 0.84f)
                    lineTo(s * 0.10f, s * 0.84f)
                    close()
                }
                drawPath(p, color, style = Stroke(w))
            }
            EffectKind.NONE -> {
                val d = s * 0.20f
                drawLine(color, Offset(d, d), Offset(s - d, s - d), strokeWidth = w)
                drawLine(color, Offset(s - d, d), Offset(d, s - d), strokeWidth = w)
            }
        }
    }
}

/** Official-style effectiveness: an icon (◎ ○ △ ×) and its Chinese words, no badge behind it.
 *  [lethal] replaces the words with 致命 and turns both icon and text red. */
@Composable
fun EffectLabel(pct: Int, modifier: Modifier = Modifier, fontSize: TextUnit = 10.sp, lethal: Boolean = false) {
    val kind = effectKind(pct)
    val color = if (lethal) Color(0xFFE53935) else when (kind) {
        EffectKind.SUPER -> Color(0xFF3F9B3A)
        EffectKind.NORMAL -> OptionColors.label
        EffectKind.WEAK -> Color(0xFFC05A2A)
        EffectKind.NONE -> OptionColors.muted
    }
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        EffectIcon(kind, color, Modifier.size((fontSize.value * 0.95f).dp))
        Text(tr(if (lethal) "LETHAL" else effectLabel(kind)), color = color, fontSize = fontSize, fontFamily = pixelFontFamily(), maxLines = 1)
    }
}

@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    if (status.isEmpty()) return
    Box(
        modifier = modifier
            .clip(PixelRoundedShape(4.dp))
            .background(AppColors.statusColor(status))
            .padding(horizontal = 5.dp, vertical = 2.dp),
    ) {
        Text(com.pokedaisy.app.companion.data.statusText(status), color = Color.White, fontSize = 10.sp, fontFamily = pixelFontFamily())
    }
}

@Composable
fun MultiplierChip(label: String, pct: Int, modifier: Modifier = Modifier, fontSize: TextUnit = 10.sp) {
    val bg = AppColors.multiplierColor(pct)
    val fg = if (pct == 0) Color(0xFFF2B8BD) else Color.White
    Box(
        modifier = modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 7.dp, vertical = 2.dp),
    ) {
        Text(label, color = fg, fontSize = fontSize, fontFamily = pixelFontFamily(), maxLines = 1)
    }
}

/** "35" for a damaging move, "—" for a status move (power 0 in the ROM's own
 * move data, same convention every mainline Pokémon game uses on its own move
 * info screen). */
fun powerLabel(power: Int): String = if (power > 0) power.toString() else "—"

@Composable
fun HpBar(hp: Int, maxHp: Int, modifier: Modifier = Modifier) {
    val pct = if (maxHp == 0) 0f else (hp.toFloat() / maxHp).coerceIn(0f, 1f)
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Text(tr("HP"), color = AppColors.hpHigh, fontSize = 10.sp, fontFamily = pixelFontFamily())
        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .weight(1f)
                .height(8.dp)
                .clip(PixelRoundedShape(2.dp))
                .background(Color(0xFF2C2418))
                .border(1.dp, AppColors.borderOuter, PixelRoundedShape(2.dp)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(pct)
                    .background(AppColors.hpColor(hp, maxHp)),
            )
        }
        Text("$hp/$maxHp", color = AppColors.muted, fontSize = 12.sp, fontFamily = pixelFontFamily())
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun MatchupChipRow(heading: String, list: List<TypeMatchup>) {
    if (list.isEmpty()) return
    Text(heading, color = AppColors.muted, fontSize = 10.sp, fontFamily = pixelFontFamily(), modifier = Modifier.padding(top = 6.dp, bottom = 2.dp))
    androidx.compose.foundation.layout.FlowRow {
        list.forEach { m ->
            Box(modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)) {
                TypeBadgeWithLabel(m)
            }
        }
    }
}

/** A type badge with its multiplier ("FIRE 2x"). */
/** The move's category beside its [TypeBadge] ([moveKind]); nothing where the game's rule isn't known. */
@Composable
fun MoveKindIcon(mv: com.pokedaisy.app.companion.data.MoveView, m: GbaTextMetrics) {
    val kind = com.pokedaisy.app.companion.data.moveKind(mv) ?: return
    val art = when (kind) {
        com.pokedaisy.app.companion.data.MoveKind.PHYSICAL -> com.pokedaisy.app.R.drawable.move_physical
        com.pokedaisy.app.companion.data.MoveKind.SPECIAL -> com.pokedaisy.app.R.drawable.move_special
        com.pokedaisy.app.companion.data.MoveKind.STATUS -> com.pokedaisy.app.R.drawable.move_status
    }
    val label = com.pokedaisy.app.companion.i18n.tr(kind.label)
    Spacer(Modifier.width(m.u * 3))
    Icon(
        painterResource(art), contentDescription = label, tint = OptionColors.label,
        modifier = Modifier.size(m.u * 12),
    )
}

private val MOVE_KIND_COLORS = mapOf(
    'R' to Color(0xFFC03018), 'O' to Color(0xFFF08030), 'Y' to Color(0xFFF8D030),
    'B' to Color(0xFF2848A8), 'C' to Color(0xFF58A0F0), 'W' to Color(0xFFF0F8FF),
    'G' to Color(0xFF686878), 'L' to Color(0xFFB8B8C8),
)

@Composable
fun TypeBadgeWithLabel(m: TypeMatchup, modifier: Modifier = Modifier) {
    val color = AppColors.typeColors[m.type] ?: AppColors.muted
    val icon = typeIconRes(m.type)
    Box(
        modifier = modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(50))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            icon?.let {
                Image(
                    painter = painterResource(it), contentDescription = null,
                    modifier = Modifier.size(14.dp), contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(Color.White),
                )
            }
            Text(
                "${com.pokedaisy.app.companion.data.typeLabel(m.type)} ${m.label}",
                color = Color.White, fontSize = 15.sp, fontFamily = pixelFontFamily(), maxLines = 1,
            )
        }
    }
}

@Composable
fun PanelBox(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    com.pokedaisy.app.companion.ui.theme.GbaWindow(
        modifier = modifier,
        padding = androidx.compose.foundation.layout.PaddingValues(12.dp),
    ) { content() }
}

/** A rounded rect with its corners cut to a single diagonal step instead of a
 * smooth arc - reads as a chunky pixel-art corner at typical button/badge
 * sizes. Shared by [TypeBadge] and [PlatinumButton] (in BattleControlsScreen)
 * so every pixel-corner element in the app uses the exact same shape. */
class PixelCornerShape(private val step: Dp) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val s = with(density) { step.toPx() }.coerceAtMost(minOf(size.width, size.height) / 2f)
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(s, 0f)
            lineTo(w - s, 0f)
            lineTo(w - s, s)
            lineTo(w, s)
            lineTo(w, h - s)
            lineTo(w - s, h - s)
            lineTo(w - s, h)
            lineTo(s, h)
            lineTo(s, h - s)
            lineTo(0f, h - s)
            lineTo(0f, s)
            lineTo(s, s)
            close()
        }
        return Outline.Generic(path)
    }
}
