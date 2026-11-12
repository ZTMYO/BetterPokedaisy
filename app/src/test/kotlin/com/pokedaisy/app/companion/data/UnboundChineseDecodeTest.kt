package com.pokedaisy.app.companion.data

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

/** Names matched against the original party screen of this exact ROM build. */
class UnboundChineseDecodeTest {
    private val sha = "40104331f1899b23cd5b6cee9612b775e6bb4234"
    @After fun reset() { Gen3Text.cjk = null; UnboundChineseBattleNames.use(false); com.pokedaisy.app.companion.i18n.L10n.apply("EN", null) }
    private fun bytes(hex: String) = hex.split(" ").map { it.toInt(16).toByte() }.toByteArray()
    @Test fun `known party names use the same Chinese encoding`() {
        Gen3Text.cjk = CjkTables.forSha(sha)
        assertSame(CjkTables.ELITE_REDUX_CN, Gen3Text.cjk)
        for ((hex, expected) in listOf(
            "0d 04 0e af 08 b8 ff" to "铁哑铃",
            "09 6c 09 d9 01 b6 ff" to "迷你冰",
            "0e 92 0d 17 11 0b ff" to "雪童子",
            "09 b1 0d e6 08 79 0c 04 ff" to "幕下力士",
            "04 c9 01 67 0b 14 ff" to "海豹球",
        )) {
            val encoded = bytes(hex)
            assertEquals(expected, Gen3Text.decode(encoded))
            // The production party reader validates the language byte and nickname field.
            val mon = ByteArray(100)
            encoded.copyInto(mon, 8)
            mon[0x12] = 2
            assertEquals(expected, gen3Nickname(mon, 0, VANILLA_PARTY_MON))
        }
    }
    @Test fun `ROM item names are enabled only for the selected version and matching item id`() {
        val previousReader = PokedexSource.reader
        val rom = ByteArray(44 * 6)
        bytes("07 5b 08 bc 0b 14 ff").copyInto(rom, 4 * 44)
        rom[4 * 44 + 14] = 4
        PokedexSource.reader = RomFileReader(rom)
        try {
            Gen3Text.cjk = CjkTables.forSha(sha)
            val table = vanillaItems(0x08000000, count = 6)
            RomItemText.use(table)
            assertEquals(null, RomItemText.name(4))
            RomItemText.use(table, chineseNames = true)
            assertEquals("精灵球", RomItemText.name(4))
            assertEquals(null, RomItemText.name(5))
            assertEquals(null, RomItemText.name(6))
            RomItemText.use(null)
            assertEquals(null, RomItemText.name(4))
        } finally {
            RomItemText.use(null)
            PokedexSource.reader = previousReader
        }
    }

    @Test fun `known ROM item names match the verified version`() {
        val dir = System.getenv("POKEDAISY_SAMPLE_DIR")?.let { java.io.File(it) }
        org.junit.Assume.assumeTrue("set POKEDAISY_SAMPLE_DIR to test local samples", dir?.isDirectory == true)
        val file = dir!!.walkTopDown().firstOrNull { f ->
            if (!f.isFile || !f.extension.equals("gba", true)) false else {
                val digest = java.security.MessageDigest.getInstance("SHA-1")
                f.inputStream().use { input ->
                    val buffer = ByteArray(65536)
                    while (true) { val n = input.read(buffer); if (n < 0) break; digest.update(buffer, 0, n) }
                }
                digest.digest().joinToString("") { "%02x".format(it) } == sha
            }
        }
        org.junit.Assume.assumeTrue("matching Unbound Chinese sample is not present", file != null)
        val previousReader = PokedexSource.reader
        try {
            val bytes = file!!.readBytes()
            PokedexSource.reader = RomFileReader(bytes)
            // The map may load before the Poller sets the process-wide table.
            Gen3Text.cjk = null
            val map = RomRegionMap.read(RomRegionMap.BytesRom(bytes), CjkTables.forSha(sha))!!
            assertEquals("冻土高原", map.sections[0x58]?.name)
            assertEquals("贝林镇", map.sections[0x59]?.name)
            assertEquals(null, Gen3Text.cjk)
            Gen3Text.cjk = CjkTables.forSha(sha)
            UnboundChineseBattleNames.use(true)
            assertEquals("铁哑铃", UnboundChineseBattleNames.species(398))
            assertEquals("冰宝", UnboundChineseBattleNames.species(820))
            assertEquals("撞击", UnboundChineseBattleNames.move(33))
            assertEquals("闪光", UnboundChineseBattleNames.move(148))
            assertEquals("金属撞击", UnboundChineseBattleNames.move(499))
            com.pokedaisy.app.companion.i18n.L10n.apply("ZH_CN", null)
            PokedexSource.romChanged()
            val dex = PokedexSource.entry(POKEDEX_UNBOUND, 41)!!
            org.junit.Assert.assertTrue(dex.description, dex.description.contains("飞行的时候"))
            assertEquals(dex.description, UnboundChineseBattleNames.dexOriginal(dex.description))
            assertEquals("精神力", UnboundChineseBattleNames.dexOriginal(dex.abilities.first()))
            assertEquals("未译：Untranslated", UnboundChineseBattleNames.dexOriginal("Untranslated"))
            assertEquals("未译：中文�", UnboundChineseBattleNames.dexOriginal("中文�"))
            UnboundChineseBattleNames.use(false)
            assertEquals("未译：精神力", UnboundChineseBattleNames.dexOriginal("精神力"))
            UnboundChineseBattleNames.use(true)

            val priorGame = activeGame
            try {
                activeGame = GameKind.UNBOUND
                val empty = BattleMon(0, 0, 0, 0, 0, 0, 0, intArrayOf(), intArrayOf())
                val player = BattleMon(398, 17, 8, 14, 33, 45, 0, intArrayOf(33, 148, 499, 0), intArrayOf(32, 20, 32, 0))
                val opponent = BattleMon(820, 10, 15, 255, 6, 32, 0, intArrayOf(), intArrayOf())
                val battlers = MutableList(4) { empty }
                battlers[BATTLE_POS_PLAYER_LEFT] = player
                battlers[BATTLE_POS_OPPONENT_LEFT] = opponent
                val mon = Mon(398, 17, 33, 45, 0, intArrayOf(33, 148, 499, 0), intArrayOf(32, 20, 32, 0), nickname = "我的昵称")
                val snapshot = buildSnapshotView(Telemetry(0, true, false, 0, 0, 0, 0, 0, 0, 1, listOf(mon), battlers, 0, emptyList()))
                assertEquals("我的昵称", snapshot.battlePlayer.single().name)
                assertEquals("冰宝", snapshot.battleOpponent.single().name)
                assertEquals(listOf("撞击", "闪光", "金属撞击"), snapshot.battlePlayer.single().moves.map { it.name })
            } finally { activeGame = priorGame }

            assertEquals(null, UnboundChineseBattleNames.species(1294))
            assertEquals(null, UnboundChineseBattleNames.move(923))
            UnboundChineseBattleNames.use(false)
            assertEquals(null, UnboundChineseBattleNames.move(33))
            RomItemText.use(vanillaItems(0x08876200), chineseNames = true)
            val expected = listOf("大师球", "高级球", "超级球", "精灵球", "狩猎球", "捕网球", "潜水球", "巢穴球", "重复球", "计时球", "豪华球", "纪念球", "伤药", "解毒药", "灼伤药", "解冻药", "解眠药", "解麻药", "全复药", "全满药", "厉害伤药", "好伤药", "万灵药", "活力碎片", "活力块", "美味之水", "劲爽汽水", "果汁牛奶", "哞哞鲜奶", "元气粉", "元气根", "万能粉", "复活草")
            expected.forEachIndexed { index, name -> assertEquals("item ${index + 1}", name, RomItemText.name(index + 1)) }
        } finally {
            RomItemText.use(null)
            PokedexSource.reader = previousReader
        }
    }

    @Test fun `battle nickname is retained only for an unambiguous party match`() {
        val b = BattleMon(398, 17, 0, 0, 33, 45, 0, intArrayOf(), intArrayOf())
        val mon = Mon(398, 17, 33, 45, 0, intArrayOf(), intArrayOf(), nickname = "我的昵称")
        assertEquals("我的昵称", battleNickname(b, listOf(mon)))
        assertEquals(null, battleNickname(b, listOf(mon, mon.copy(nickname = "另一只"))))
        assertEquals(null, battleNickname(b, listOf(mon.copy(hp = 30))))
    }

    @Test fun `item name from the Chinese ROM decodes`() {
        Gen3Text.cjk = CjkTables.forSha(sha)
        assertEquals("精灵球", Gen3Text.decode(bytes("07 5b 08 bc 0b 14 ff")))
    }
}
