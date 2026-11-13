package com.pokedaisy.app.companion.data

import com.pokedaisy.app.companion.i18n.L10n
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.Normalizer
import java.util.Locale

/** The bundled Chinese item descriptions are matched to the item table by their English names. */
class ChineseItemDescriptionsTest {
    @After fun reset() = L10n.apply("EN", null)

    private fun key(s: String) =
        Normalizer.normalize(s, Normalizer.Form.NFD).uppercase(Locale.ROOT).filter { it in 'A'..'Z' || it in '0'..'9' }

    @Test
    fun `every description's English name is the item table's`() {
        val problems = mutableListOf<String>()
        chineseItemDescriptions.forEachIndexed { id, entry ->
            val en = entry.substringAfter('\u0001', "").trim()
            val name = itemNames[id]
            if (en.isNotEmpty() && name != null && key(en) != key(name)) problems += "$id: $name vs $en"
        }
        assertTrue(problems.joinToString("\n"), problems.isEmpty())
    }

    @Test
    fun `Paralyze Heal and the room keys keep their Chinese descriptions`() {
        L10n.apply("ZH_CN", null)
        assertTrue(chineseItemDescription(18).orEmpty().contains("麻痹"))
        for (id in 281..284) assertTrue("item $id", chineseItemDescription(id) != null)
    }
}
