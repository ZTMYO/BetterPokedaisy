package com.pokedaisy.app.companion.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** The move verdict's two lethal tiers: guaranteed on the lowest damage roll, or only on the highest. */
class FaintRiskTest {
    private fun stats(vararg values: Int) = MonStats(List(6) { 0 }, List(6) { 0 }, 0, values.toList())

    @Test fun `a higher roll only is maybe lethal`() {
        val prior = activeGame
        try {
            activeGame = GameKind.EMERALD
            val attacker = MonView(
                282, "Gardevoir", 34, 109, 109, "", listOf("Psychic"), null,
                moves = listOf(MoveView("Psychic", "Psychic", 10, 90, id = 94)),
                stats = stats(109, 56, 55, 85, 99, 91),
            )
            val luvdisc = MonView(
                370, "Luvdisc", 34, 74, 74, "", listOf("Water"), null,
                stats = stats(74, 35, 40, 71, 33, 48),
            )
            val vs = attacker.withMovesVs(luvdisc).moves.single().vs.single()
            assertTrue(vs.maybeLethal)
            assertFalse(vs.lethal)

            val frail = attacker.withMovesVs(luvdisc.copy(hp = 50)).moves.single().vs.single()
            assertTrue(frail.lethal)
            assertFalse(frail.maybeLethal)

            val healthy = attacker.withMovesVs(luvdisc.copy(hp = 100)).moves.single().vs.single()
            assertFalse(healthy.lethal)
            assertFalse(healthy.maybeLethal)
        } finally {
            activeGame = prior
        }
    }
}
