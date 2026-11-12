package com.pokedaisy.app.companion.data

import com.pokedaisy.app.companion.i18n.AppLanguage
import com.pokedaisy.app.companion.i18n.L10n

/**
 * Translate only when both the National Dex number and the ROM's name agree with
 * a PokeAPI name for that species. A hack may reuse a dex number for its own
 * species: matching the number alone would silently label it as something else.
 * Used only for Pokédex species names, never for player-assigned nicknames.
 */
fun dexSpeciesLabel(national: Int, original: String): String {
    if (L10n.language != AppLanguage.ZH_CN || original.isBlank()) return original
    val entry = chineseSpeciesNames.getOrNull(national)?.split('|')
    if (entry != null && entry.any { it.equals(original.trim(), ignoreCase = true) }) return entry.first()
    return "未译：$original"
}

/** ROM data without a verified translation stays verbatim and is explicitly marked in Chinese.
 * Apply only to game-owned data, never to a player's nickname or custom text. */
fun untranslatedGameText(original: String): String =
    if (L10n.language == AppLanguage.ZH_CN && original.isNotBlank() &&
        original.none { it in '\u3400'..'\u9fff' }) "未译：$original" else original
