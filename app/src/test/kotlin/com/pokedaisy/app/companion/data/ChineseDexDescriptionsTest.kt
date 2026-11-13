package com.pokedaisy.app.companion.data

import com.pokedaisy.app.companion.i18n.L10n
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** The bundled Chinese dex text, checked against the Emerald description it was made for. */
class ChineseDexDescriptionsTest {
    @After fun reset() = L10n.apply("EN", null)

    @Test
    fun `an Emerald entry finds its Chinese text`() {
        L10n.apply("ZH_CN", null)
        val bulbasaur = "BULBASAUR can be seen napping in bright\nsunlight. There is a seed on its back.\n" +
            "By soaking up the sun's rays, the seed\ngrows progressively larger."
        assertTrue(chineseDexDescription(1, bulbasaur).orEmpty().contains("种子"))
        assertNull(chineseDexDescription(1, "A different ROM's description."))
    }

    @Test
    fun `the entries the manual translation missed are filled too`() {
        L10n.apply("ZH_CN", null)
        val chikorita = "It waves its leaf around to keep foes\nat bay. However, a sweet fragrance also\n" +
            "wafts from the leaf, creating a friendly\natmosphere that becalms the battlers."
        assertNotNull(chineseDexDescription(152, chikorita))
    }
}
