package com.pokedaisy.app.companion.data

import com.pokedaisy.app.companion.i18n.AppLanguage
import com.pokedaisy.app.companion.i18n.L10n
import java.text.Normalizer

/**
 * Chinese names for data the ROM owns, for the companion's screens. Every table entry carries the
 * English name it was translated from: a hack may reuse a national number or id for its own
 * creature / move / item, so the Chinese name is shown only when the running game's name matches.
 * Otherwise the ROM's own text is kept (or marked [markUntranslated] in the Pokédex).
 */

private fun nameKey(s: String): String =
    Normalizer.normalize(s, Normalizer.Form.NFD)
        .uppercase()
        .filter { it in 'A'..'Z' || it in '0'..'9' }

private fun entryZh(entry: String?): String? = entry?.substringBefore('|')?.let(::halfWidthAscii)?.takeIf { it.isNotBlank() }

private fun entryEn(entry: String?): String? = entry?.substringAfter('|', "")?.takeIf { it.isNotBlank() }

/** The official Chinese games write digits and Latin letters half-width ("PP单项小补剂", "10号道路"). */
internal fun halfWidthAscii(s: String): String {
    if (s.none { it.code in 0xFF10..0xFF19 || it.code in 0xFF21..0xFF3A || it.code in 0xFF41..0xFF5A }) return s
    return buildString(s.length) {
        for (c in s) append(
            when (c.code) {
                in 0xFF10..0xFF19 -> '0' + (c.code - 0xFF10)
                in 0xFF21..0xFF3A -> 'A' + (c.code - 0xFF21)
                in 0xFF41..0xFF5A -> 'a' + (c.code - 0xFF41)
                else -> c
            },
        )
    }
}

private fun translated(entry: String?, romName: String): String? {
    val zh = entryZh(entry) ?: return null
    if (!romName.readsAsText()) return zh
    val en = entryEn(entry) ?: return null
    return if (nameKey(en) == nameKey(romName)) zh else null
}

/** A move name's key, with the words Gen 6 renamed (the Chinese table uses the modern spellings). */
private fun moveKey(s: String): String = nameKey(s)
    .replace("FAINTATTACK", "FEINTATTACK")
    .replace("HIJUMPKICK", "HIGHJUMPKICK")

private fun translatedMove(entry: String?, romName: String): String? {
    val zh = entryZh(entry) ?: return null
    if (!romName.readsAsText()) return zh
    val en = entryEn(entry) ?: return null
    return if (moveKey(en) == moveKey(romName)) zh else null
}

private fun hasHan(s: String): Boolean = s.any { it in '\u3400'..'\u9fff' }

/** [romanized] game text that has no Chinese translation, marked for the Pokédex only. */
fun markUntranslated(s: String): String =
    if (L10n.language != AppLanguage.ZH_CN || s.isBlank() || hasHan(s)) s else "未译：$s"

private val abilityByName: Map<String, String> by lazy {
    chineseAbilityNames.mapNotNull { entry ->
        entryEn(entry)?.let { nameKey(it) to entryZh(entry)!! }
    }.toMap()
}

private val locationByName: Map<String, String> by lazy {
    (chineseLocationNames.asList() + chineseLocationNamesExtra.asList()).mapNotNull { entry ->
        entryEn(entry)?.let { nameKey(it) to entryZh(entry)!! }
    }.toMap()
}

private val ROUTE_RE = Regex("^ROUTES? (\\d+)(?:-(\\d+))?$")

/** A map section's name in Chinese, verified against the English name the game uses. */
fun displayLocationName(romName: String): String {
    if (L10n.language != AppLanguage.ZH_CN || romName.isBlank()) return romName
    locationByName[nameKey(romName)]?.let { return it }
    ROUTE_RE.find(romName.uppercase())?.let { m ->
        val to = m.groupValues[2]
        return if (to.isEmpty()) "${m.groupValues[1]}号道路" else "${m.groupValues[1]}-${to}号道路"
    }
    return romName
}

/** A region's standard name in Chinese; a hack's own region name stays as it is. */
fun displayRegionName(name: String): String {
    if (L10n.language != AppLanguage.ZH_CN || name.isBlank()) return name
    return when (name.uppercase()) {
        "KANTO" -> "关都"
        "JOHTO" -> "城都"
        "HOENN" -> "丰缘"
        "SINNOH" -> "神奥"
        "UNOVA" -> "合众"
        "KALOS" -> "卡洛斯"
        "ALOLA" -> "阿罗拉"
        "GALAR" -> "伽勒尔"
        "HISUI" -> "洗翠"
        "PALDEA" -> "帕底亚"
        "SEVII" -> "七之岛"
        "SEVII 1-3" -> "七之岛 1-3"
        "SEVII 4-5" -> "七之岛 4-5"
        "SEVII 6-7" -> "七之岛 6-7"
        else -> name
    }
}

private fun genusKey(s: String): String {
    val key = nameKey(s)
    return if (key.endsWith("POKEMON")) key.removeSuffix("POKEMON") else key
}

/** A species' Pokédex category ("Mud Fish") in Chinese, verified against the ROM's own word. */
fun displaySpeciesCategory(national: Int, romCategory: String): String {
    if (L10n.language != AppLanguage.ZH_CN || national <= 0) return romCategory
    val entry = chineseSpeciesCategories.getOrNull(national) ?: return romCategory
    val en = entry.substringAfter('|', "")
    val zh = halfWidthAscii(entry.substringBefore('|'))
    if (zh.isBlank()) return romCategory
    return if (!romCategory.readsAsText() || genusKey(en) == genusKey(romCategory)) zh else romCategory
}

/** The dex page's "MUD FISH POKéMON" line: "沼鱼宝可梦" in Chinese when the category is known. */
fun displayDexCategoryLine(t: PokedexTables, e: DexEntry): String {
    val zh = displaySpeciesCategory(e.national, e.category)
    return if (zh != e.category) zh + "宝可梦" else dexCategoryLine(t, e.category)
}

/** The bundled English genus for [national], for a ROM whose own category text can't be decoded. */
fun chineseSpeciesCategoryEnglish(national: Int): String? =
    chineseSpeciesCategories.getOrNull(national)
        ?.substringAfter('|', "")?.trim()?.takeIf { it.isNotBlank() }

/** Ability [id]'s Chinese name, for a ROM whose own ability names can't be decoded. */
fun chineseAbilityNameById(id: Int): String? = entryZh(chineseAbilityNames.getOrNull(id))

/** The running game's species name in Chinese when the national number and the ROM's own name agree. */
fun displaySpeciesName(id: Int): String {
    val original = speciesName(id)
    if (L10n.language != AppLanguage.ZH_CN || id <= 0) return original
    val national = PokedexSource.nationalOf(id).takeIf { it > 0 } ?: id
    val entry = chineseSpeciesNames.getOrNull(national) ?: return original
    val names = entry.split('|')
    if (names.size < 2) return original
    val zh = halfWidthAscii(names[0])
    // A translated ROM's own name is not text: the national number still identifies the species.
    if (!original.readsAsText()) return zh
    return if (names.drop(1).any { it.equals(original.trim(), ignoreCase = true) }) zh else original
}

/** A move's name in Chinese, verified against the ROM's own name; the ROM's name otherwise. */
fun displayMoveName(id: Int, romName: String): String {
    if (L10n.language != AppLanguage.ZH_CN || id <= 0) return romName
    return translatedMove(chineseMoveNames.getOrNull(id), romName) ?: romName
}

fun displayMoveName(id: Int): String = displayMoveName(id, lookupMove(id).name)

/** A move's name in the Pokédex: Chinese, or the English name marked 未译. */
fun displayMoveNameMarked(id: Int, romName: String): String {
    val zh = displayMoveName(id, romName)
    return if (zh == romName) markUntranslated(romName) else zh
}

/** An item's Chinese description from the bundled table, when its English name still matches. */
fun chineseItemDescription(id: Int): String? {
    if (L10n.language != AppLanguage.ZH_CN || id <= 0) return null
    return chineseItemDescription(id, gameItemName(id))
}

/** [chineseItemDescription] against a name already read: a Chinese ROM names the item in Chinese, so the
 * bundled Chinese name is what must match there, not the table's English one. */
internal fun chineseItemDescription(id: Int, romName: String): String? {
    if (L10n.language != AppLanguage.ZH_CN || id <= 0) return null
    val entry = chineseItemDescriptions.getOrNull(id)?.takeIf { it.isNotBlank() } ?: return null
    val sep = entry.indexOf('\u0001')
    val zh = halfWidthAscii((if (sep >= 0) entry.substring(0, sep) else entry).trim())
    val en = if (sep >= 0) entry.substring(sep + 1).trim() else ""
    if (zh.isEmpty()) return null
    if (!romName.readsAsText()) return zh
    if (romName.any { it in '\u3400'..'\u9fff' }) {
        val zhName = chineseItemNames.getOrNull(id)?.substringBefore('|')?.let(::halfWidthAscii)?.trim().orEmpty()
        return zh.takeIf { zhName.isNotEmpty() && romName.trim() == zhName }
    }
    if (en.isNotEmpty() && nameKey(en) != nameKey(romName)) return null
    return zh
}

/** An item's name in Chinese, verified against the ROM's own name; the ROM's name otherwise. */
fun displayItemName(id: Int, romName: String): String {
    if (L10n.language != AppLanguage.ZH_CN || id <= 0) return romName
    return translated(chineseItemNames.getOrNull(id), romName) ?: romName
}

fun displayItemName(id: Int): String = displayItemName(id, gameItemName(id))

/** An item's name in the Pokédex: Chinese, or the English name marked 未译. */
fun displayItemNameMarked(id: Int, romName: String): String {
    val zh = displayItemName(id, romName)
    return if (zh == romName) markUntranslated(romName) else zh
}

/** An ability's name in Chinese, verified against the ROM's own name. */
fun displayAbilityName(romName: String): String {
    if (L10n.language != AppLanguage.ZH_CN || romName.isBlank()) return romName
    return abilityByName[nameKey(romName)] ?: romName
}

/** An ability's name in the Pokédex: Chinese, or the English name marked 未译. */
fun displayAbilityNameMarked(romName: String): String {
    val zh = displayAbilityName(romName)
    return if (zh == romName) markUntranslated(romName) else zh
}

/** A nature's standard name in Chinese; a game's own (Monomyth's) natures keep their own. */
fun displayNatureName(nature: Int): String {
    if (L10n.language != AppLanguage.ZH_CN || nature !in chineseNatureNames.indices) return natureName(nature)
    return entryZh(chineseNatureNames[nature]) ?: natureName(nature)
}

/** The status chip's text; the code itself stays English for its colour. */
fun statusText(status: String): String {
    if (L10n.language != AppLanguage.ZH_CN) return status
    return when (status) {
        "SLP" -> "睡"
        "FRZ" -> "冰"
        "BRN" -> "灼"
        "PAR" -> "麻"
        "TOX" -> "剧毒"
        "PSN" -> "毒"
        "FNT" -> "濒死"
        else -> status
    }
}
