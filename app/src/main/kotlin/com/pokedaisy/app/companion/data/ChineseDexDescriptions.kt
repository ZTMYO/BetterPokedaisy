package com.pokedaisy.app.companion.data

import com.pokedaisy.app.companion.i18n.AppLanguage
import com.pokedaisy.app.companion.i18n.L10n
import java.text.Normalizer

private fun dexKey(s: String): String =
    Normalizer.normalize(s, Normalizer.Form.NFD).uppercase().filter { it in 'A'..'Z' || it in '0'..'9' }

/**
 * [national]'s Chinese Pokédex text, when the ROM's [english] description still matches the bundled Emerald one:
 * the same check the Chinese names use, so a hack or another language keeps its own text.
 */
fun chineseDexDescription(national: Int, english: String): String? {
    if (L10n.language != AppLanguage.ZH_CN || national <= 0 || english.isBlank()) return null
    val entry = chineseDexDescriptions.getOrNull(national)?.takeIf { it.isNotBlank() } ?: return null
    val sep = entry.indexOf('\u0001')
    if (sep < 0) return null
    val zh = entry.substring(0, sep).trim()
    val en = entry.substring(sep + 1).trim()
    if (zh.isEmpty() || en.isEmpty()) return null
    return zh.takeIf { dexKey(en) == dexKey(english) }
}
