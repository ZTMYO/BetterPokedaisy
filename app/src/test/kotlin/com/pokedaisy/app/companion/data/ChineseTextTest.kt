package com.pokedaisy.app.companion.data

import com.pokedaisy.app.companion.i18n.AppLanguage
import com.pokedaisy.app.companion.i18n.L10n
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** The Chinese data tables' shape and the half-width text the official games print. */
class ChineseTextTest {
    private val fullWidth = Regex("[\uFF10-\uFF19\uFF21-\uFF3A\uFF41-\uFF5A]")

    @After fun reset() = L10n.apply("EN", null)

    @Test fun `junk text is recognised`() {
        assertFalse("?????".readsAsText())
        assertFalse("É".readsAsText())
        assertFalse("\uFFFD\uFFFD".readsAsText())
        assertFalse("".readsAsText())
        assertTrue("Marill".readsAsText())
        assertTrue("小丸".readsAsText())
        assertTrue("abcdefÉ".readsAsText())
    }

    @Test fun `official text uses half-width digits and letters`() {
        assertEquals("PP单项小补剂", halfWidthAscii("ＰＰ单项小补剂"))
        assertEquals("10号道路", halfWidthAscii("１０号道路"))
        assertEquals("（PP）", halfWidthAscii("（ＰＰ）"))
        val tables = listOf(
            chineseSpeciesNames, chineseMoveNames, chineseItemNames, chineseAbilityNames,
            chineseLocationNames, chineseLocationNamesExtra, chineseDexDescriptions, chineseItemDescriptions, chineseSpeciesCategories,
        )
        for (table in tables) for (entry in table) {
            val zh = entry.substringBefore('|')
            assertFalse("full-width in '$zh'", fullWidth.containsMatchIn(halfWidthAscii(zh)))
        }
    }

    @Test fun `every Gen 3 id has its Chinese data`() {
        for (n in 1..386) assertTrue("species $n", chineseSpeciesNames.getOrNull(n)?.isNotBlank() == true)
        for (n in 1..354) assertTrue("move $n", chineseMoveNames.getOrNull(n)?.isNotBlank() == true)
        for (n in 1..376) assertTrue("item $n", chineseItemNames.getOrNull(n)?.isNotBlank() == true)
        for (n in 1..80) assertTrue("ability $n", chineseAbilityNames.getOrNull(n)?.isNotBlank() == true)
        for (n in 1..386) assertTrue("dex $n", chineseDexDescriptions.getOrNull(n)?.contains('\u0001') == true)
    }

    @Test fun `a Chinese item name uses the Chinese description`() {
        L10n.apply("ZH_CN", null)
        assertEquals("与火岩队的标志形状相同的徽章模样的东西。", chineseItemDescription(375, "熔岩标志"))
        assertEquals("与火岩队的标志形状相同的徽章模样的东西。", chineseItemDescription(375, "MAGMA EMBLEM"))
        L10n.apply("EN", null)
        assertNull(chineseItemDescription(375, "熔岩标志"))
    }

    @Test fun `the bag's Magma Emblem description comes through Chinese`() {
        val prior = activeGame
        try {
            activeGame = GameKind.EMERALD
            L10n.apply("ZH_CN", null)
            assertEquals("MAGMA EMBLEM", gameItemName(375))
            assertEquals("与火岩队的标志形状相同的徽章模样的东西。", chineseItemDescription(375))
            assertEquals("与火岩队的标志形状相同的徽章模样的东西。", itemDescription(375))
        } finally {
            activeGame = prior
        }
    }

    @Test fun `the Chinese tables answer on a Chinese screen only`() {
        val english = speciesName(1)
        L10n.apply("EN", null)
        assertEquals(english, displaySpeciesName(1))
        L10n.apply("ZH_CN", null)
        assertEquals("妙蛙种子", displaySpeciesName(1))
        assertEquals("若叶镇", displayLocationName("NEW BARK TOWN"))
        assertEquals("10号道路", displayLocationName("ROUTE 10"))
        assertEquals("12-15号道路", displayLocationName("ROUTES 12-15"))
        L10n.apply("EN", null)
        assertEquals("NEW BARK TOWN", displayLocationName("NEW BARK TOWN"))
        assertEquals(AppLanguage.ZH_CN, AppLanguage.ofLocale(java.util.Locale.SIMPLIFIED_CHINESE))
    }
}
