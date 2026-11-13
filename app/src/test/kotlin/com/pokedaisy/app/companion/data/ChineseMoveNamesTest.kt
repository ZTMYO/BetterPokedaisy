package com.pokedaisy.app.companion.data

import com.pokedaisy.app.companion.i18n.L10n
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

/** Gen 3 move names still find their Chinese text where later generations respelled them. */
class ChineseMoveNamesTest {
    @After fun reset() = L10n.apply("EN", null)

    @Test
    fun `Faint Attack and Hi Jump Kick keep their Chinese names`() {
        L10n.apply("ZH_CN", null)
        assertEquals("出奇一击", displayMoveName(185, "Faint Attack"))
        assertEquals("出奇一击", displayMoveName(185, "Feint Attack"))
        assertEquals("飞膝踢", displayMoveName(136, "Hi Jump Kick"))
        assertEquals("飞膝踢", displayMoveName(136, "High Jump Kick"))
    }
}
