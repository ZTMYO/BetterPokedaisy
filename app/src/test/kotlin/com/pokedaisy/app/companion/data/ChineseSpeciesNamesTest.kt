package com.pokedaisy.app.companion.data

import com.pokedaisy.app.companion.i18n.L10n
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class ChineseSpeciesNamesTest {
    @After fun reset() = L10n.apply("EN", null)

    @Test fun `unverified game data keeps the original and is marked only in Chinese`() {
        L10n.apply("ZH_CN", null)
        assertEquals("未译：A ROM description.\nSecond line.", untranslatedGameText("A ROM description.\nSecond line."))
        // Already Chinese: there is nothing to mark.
        assertEquals("改版效果", untranslatedGameText("改版效果"))
        assertEquals("", untranslatedGameText(""))
        assertEquals("  ", untranslatedGameText("  "))
        L10n.apply("EN", null)
        assertEquals("A ROM description.", untranslatedGameText("A ROM description."))
    }

    @Test fun `national number and source name both have to match`() {
        L10n.apply("ZH_CN", null)
        assertEquals("妙蛙种子", dexSpeciesLabel(1, "BULBASAUR"))
        assertEquals("皮卡丘", dexSpeciesLabel(25, "PIKACHU"))
        assertEquals("皮卡丘", dexSpeciesLabel(25, "ピカチュウ"))
        assertEquals("负电拍拍", dexSpeciesLabel(312, "MINUN"))
        assertEquals("缠红鹤", dexSpeciesLabel(973, "FLAMIGO"))
        assertEquals("铁辙迹", dexSpeciesLabel(990, "IRON TREADS"))
        assertEquals("厄诡椪", dexSpeciesLabel(1017, "OGERPON"))
        assertEquals("未译：PIKACHU", dexSpeciesLabel(1, "PIKACHU"))
        assertEquals("未译：CUSTOM", dexSpeciesLabel(25, "CUSTOM"))
        assertEquals("未译：改版专属", dexSpeciesLabel(25, "改版专属"))
        assertEquals("未译：皮卡丘", dexSpeciesLabel(1, "皮卡丘"))
        assertEquals("皮卡丘", dexSpeciesLabel(25, "皮卡丘"))
        assertEquals("未译：CUSTOM", dexSpeciesLabel(9999, "CUSTOM"))
        assertEquals("", dexSpeciesLabel(25, ""))
        L10n.apply("EN", null)
        assertEquals("PIKACHU", dexSpeciesLabel(25, "PIKACHU"))
    }

    @Test fun `a key item read from the ROM still uses its Chinese name`() {
        L10n.apply("ZH_CN", null)
        assertEquals("熔岩标志", displayItemName(375, "MAGMA EMBLEM"))
        assertEquals("熔岩标志", displayItemName(375, "MAGMAEMBLEM"))
        assertEquals("熔岩标志", displayItemName(375, "熔岩标志"))
        L10n.apply("EN", null)
        assertEquals("MAGMA EMBLEM", displayItemName(375, "MAGMA EMBLEM"))
    }
}
