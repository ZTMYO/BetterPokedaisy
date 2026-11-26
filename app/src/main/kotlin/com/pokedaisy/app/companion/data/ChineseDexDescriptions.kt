package com.pokedaisy.app.companion.data

import com.pokedaisy.app.companion.i18n.AppLanguage
import com.pokedaisy.app.companion.i18n.L10n

private fun dexKey(s: String): String =
    asciiFold(s).uppercase().filter { it in 'A'..'Z' || it in '0'..'9' }

/**
 * [national]'s Chinese Pokédex text: for a retail Gen 3 cart by national number, otherwise only when the ROM's
 * [english] description still matches the bundled Emerald one - so a hack or another language keeps its own text.
 */
fun chineseDexDescription(national: Int, english: String): String? {
    if (L10n.language != AppLanguage.ZH_CN || national <= 0) return null
    val entry = chineseDexDescriptions.getOrNull(national)?.takeIf { it.isNotBlank() } ?: return null
    val sep = entry.indexOf('\u0001')
    if (sep < 0) return null
    val zh = halfWidthAscii(entry.substring(0, sep).trim())
    val en = entry.substring(sep + 1).trim()
    if (zh.isEmpty()) return null
    if (!english.readsAsText()) return zh
    // A retail Gen 3 cart (FireRed, LeafGreen, Ruby, Sapphire, Emerald): its own entry differs from the bundled
    // Emerald text, but the species is the same, so the national number's Chinese text still applies.
    if (retailGen3Rom) return zh
    if (en.isEmpty()) return null
    return zh.takeIf { dexKey(en) == dexKey(english) }
}
