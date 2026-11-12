package com.pokedaisy.app.companion.i18n

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.util.Locale

/** The tables cover every tr / tk literal in the app (scripts/check_translations.py, on the JVM). */
class TranslationsTest {
    // Possessive runs ([^"\\]++ ... *+): a plain (?:a|b)* recurses once per character in Java's
    // regex engine, and the generated tables' long literals overflowed the test worker's stack.
    private val call = Regex("""\bt[rk]\(\s*"((?:[^"\\]++|\\.)*+)"""")
    // String literals first, so a "*/*" inside one isn't taken for a comment.
    private val token = Regex(""""(?:[^"\\\n]++|\\.)*+"|/\*.*?\*/|//[^\n]*""", RegexOption.DOT_MATCHES_ALL)
    private val placeholder = Regex("""\{\d+\}""")

    @After fun reset() = L10n.apply("EN", null)

    @Test
    fun `every tr and tk literal has every language`() {
        val src = File("src/main/kotlin")
        assertTrue("run from the app module", src.isDirectory)
        val problems = mutableListOf<String>()
        src.walkTopDown().filter { it.extension == "kt" && !(it.parentFile.name == "i18n" && it.name.startsWith("Tr")) }.forEach { f ->
            val text = token.replace(f.readText()) { m -> if (m.value.startsWith('"')) m.value else "\n".repeat(m.value.count { it == '\n' }) }
            call.findAll(text).forEach { m ->
                // The literal as written in source -> the string at runtime (the map's key).
                val key = m.groupValues[1].replace("\\\"", "\"").replace("\\$", "$").replace("\\\\", "\\")
                if (key !in translations) problems += "${f.name}: no translation for \"$key\""
            }
        }
        // One copy per key: a second table's would silently win the merge.
        val tables = allTranslationTables()
        tables.flatMap { it.keys }.groupingBy { it }.eachCount().filter { it.value > 1 }.keys
            .forEach { problems += "\"$it\" is in more than one table" }
        translations.forEach { (key, t) ->
            val want = placeholder.findAll(key).map { it.value }.sorted().toList()
            AppLanguage.entries.drop(1).forEach { lang ->
                val s = t.of(lang).orEmpty()
                if (s.isBlank()) {
                    // Simplified Chinese is being filled in: a missing string shows English (tr's fallback).
                    if (lang != AppLanguage.ZH_CN) problems += "\"$key\": empty ${lang.code}"
                } else if (placeholder.findAll(s).map { it.value }.sorted().toList() != want) problems += "\"$key\": ${lang.code} placeholders"
            }
        }
        assertTrue(problems.joinToString("\n"), problems.isEmpty())
    }

    @Test
    fun `auto follows the ROM, then the device`() {
        L10n.apply(LANGUAGE_AUTO, "BPRF", Locale.GERMAN)
        assertEquals(AppLanguage.FR, L10n.language)
        L10n.apply(LANGUAGE_AUTO, "AXVJ", Locale.GERMAN)
        assertEquals(AppLanguage.JA, L10n.language)
        // Library: no ROM.
        L10n.apply(LANGUAGE_AUTO, null, Locale.GERMAN)
        assertEquals(AppLanguage.DE, L10n.language)
        // Portuguese by region (GitHub #61): Brazil's, else Portugal's. No ROM is in Portuguese, so an English
        // game shows the device's Portuguese too; a game in another language keeps its own.
        L10n.apply(LANGUAGE_AUTO, null, Locale.forLanguageTag("pt-BR"))
        assertEquals(AppLanguage.BR, L10n.language)
        L10n.apply(LANGUAGE_AUTO, null, Locale.forLanguageTag("pt-PT"))
        assertEquals(AppLanguage.PT, L10n.language)
        L10n.apply(LANGUAGE_AUTO, "BPRE", Locale.forLanguageTag("pt-BR"))
        assertEquals(AppLanguage.BR, L10n.language)
        L10n.apply(LANGUAGE_AUTO, "BPRF", Locale.forLanguageTag("pt-PT"))
        assertEquals(AppLanguage.FR, L10n.language)
        L10n.apply(LANGUAGE_AUTO, "BPRE", Locale.GERMAN)
        assertEquals(AppLanguage.EN, L10n.language)
        // Korean: the device's, a Korean DS cart's (IPKK: HeartGold), and a language the app lacks is English.
        L10n.apply(LANGUAGE_AUTO, null, Locale.forLanguageTag("ko-KR"))
        assertEquals(AppLanguage.KO, L10n.language)
        L10n.apply(LANGUAGE_AUTO, "IPKK", Locale.GERMAN)
        assertEquals(AppLanguage.KO, L10n.language)
        L10n.apply(LANGUAGE_AUTO, null, Locale.forLanguageTag("nl-NL"))
        assertEquals(AppLanguage.EN, L10n.language)
        // Simplified Chinese follows the device over English, but not other ROM languages.
        L10n.apply(LANGUAGE_AUTO, null, Locale.forLanguageTag("zh-Hans-CN"))
        assertEquals(AppLanguage.ZH_CN, L10n.language)
        L10n.apply(LANGUAGE_AUTO, "BPRE", Locale.forLanguageTag("zh-CN"))
        assertEquals(AppLanguage.ZH_CN, L10n.language)
        assertEquals("取消", tr("CANCEL"))
        L10n.apply(LANGUAGE_AUTO, "BPRF", Locale.forLanguageTag("zh-CN"))
        assertEquals(AppLanguage.FR, L10n.language)
        L10n.apply(LANGUAGE_AUTO, null, Locale.forLanguageTag("zh-Hant-TW"))
        assertEquals(AppLanguage.EN, L10n.language)
        L10n.apply("ZH_CN", "BPRF", Locale.FRENCH)
        assertEquals(AppLanguage.ZH_CN, L10n.language)
        assertEquals("自动", tr("AUTO"))
        assertEquals("{0} + {1}", tr("{0} + {1}"))
        // An override wins over the ROM.
        L10n.apply("IT", "BPRE")
        assertEquals(AppLanguage.IT, L10n.language)
        assertEquals("ANNULLA", tr("CANCEL"))
        assertEquals("not a key", tr("not a key"))
        // A context after "|" picks its own translation and never shows in English.
        assertEquals("AVANTI", tr("NEXT|continue"))
        L10n.apply("EN", null)
        assertEquals("NEXT", tr("NEXT|continue"))
    }

    @Test
    fun `core simplified Chinese translations preserve placeholders`() {
        trCore.forEach { (key, translation) ->
            val zh = translation.zh.orEmpty()
            assertTrue("missing Chinese for $key", zh.isNotBlank())
            assertEquals("Chinese placeholders for $key",
                placeholder.findAll(key).map { it.value }.sorted().toList(),
                placeholder.findAll(zh).map { it.value }.sorted().toList())
        }
    }

    @Test
    fun `emulator errors keep file names and exception details`() {
        L10n.apply("ZH_CN", null)
        assertEquals("上一个游戏仍在关闭，请重试", tr("The previous game is still closing, try again"))
        assertEquals("mGBA 无法加载 我的游戏.gba", tr("mGBA could not load {0}", "我的游戏.gba"))
        assertEquals("模拟器已停止：I/O error", tr("The emulator stopped: {0}", "I/O error"))
        L10n.apply("EN", null)
        assertEquals("mGBA could not load game.gba", tr("mGBA could not load {0}", "game.gba"))
    }

    @Test
    fun placeholders() {
        L10n.apply("EN", null, Locale.ENGLISH)
        assertEquals("AUTO (ENGLISH)", L10n.settingLabel(LANGUAGE_AUTO))
        assertEquals("3 + 4", tr("{0} + {1}", 3, 4))
    }
}
