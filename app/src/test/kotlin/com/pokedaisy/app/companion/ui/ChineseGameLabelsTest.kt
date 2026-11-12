package com.pokedaisy.app.companion.ui

import com.pokedaisy.app.companion.data.GameKind
import com.pokedaisy.app.companion.data.activeGame
import com.pokedaisy.app.companion.i18n.L10n
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class ChineseGameLabelsTest {
    private val originalGame = activeGame
    @After fun reset() { activeGame = originalGame; L10n.apply("EN", null) }

    @Test fun `party names with CJK use a font that can draw them`() {
        assertEquals(true, needsPartyUnicodeFont("铁哑铃"))
        assertEquals(true, needsPartyUnicodeFont("ミニリュウ"))
        assertEquals(false, needsPartyUnicodeFont("Mr. Mime"))
    }

    @Test fun `item descriptions mark game text but preserve action messages`() {
        val item = com.pokedaisy.app.companion.data.ItemView(9999, "CUSTOM ITEM", 1, null, description = "Changed ROM effect.")
        L10n.apply("ZH_CN", null)
        assertEquals("未译：Changed ROM effect.", itemDescriptionText(item, null))
        assertEquals("未译：CUSTOM ITEM", itemDescriptionText(item.copy(description = ""), null))
        assertEquals("使用成功", itemDescriptionText(item, "使用成功"))
        L10n.apply("EN", null)
        assertEquals("Changed ROM effect.", itemDescriptionText(item, null))
    }

    @Test fun `bag names and early battle labels follow Chinese without changing game actions`() {
        L10n.apply("ZH_CN", null)
        val cases = mapOf(GameKind.UNBOUND to "立方包", GameKind.ROWE to "物品栏", GameKind.MARIOMON to "口袋", GameKind.CRYSTAL to "背包", GameKind.RED to "道具")
        for ((game, expected) in cases) {
            activeGame = game
            assertEquals(expected, companionTabLabel("ITEMS"))
            assertEquals(expected, gen1ActionLabel(1, "Pack"))
        }
        assertEquals("战斗", gen1ActionLabel(0, "Fight"))
        assertEquals("宝可梦", gen1ActionLabel(2, "PkMn"))
        assertEquals("逃跑", gen1ActionLabel(3, "Run"))
        assertEquals("custom", gen1ActionLabel(99, "custom"))
        L10n.apply("FR", null)
        activeGame = GameKind.UNBOUND
        assertEquals("CUBE", companionTabLabel("ITEMS"))
        assertEquals("ATTAQUE", gen1ActionLabel(0, "ATTAQUE"))
    }
}
