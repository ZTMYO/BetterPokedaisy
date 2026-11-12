package com.pokedaisy.app.companion.data

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assume.assumeTrue
import java.io.File
import java.security.MessageDigest
import org.junit.Test

/**
 * 《宝可梦 火箭队》v3.04 汉化版（pokeapi，西火）的自定义动态码表：从 gMovesInfo 招式名与
 * veekun 官方中文译名对齐投票 + 物种/道具名收割逆向推导（charmap 来源
 * roms/西班牙火箭队汉化v3.04/汉化文本/charmap.txt）。该 ROM 的字符串是小端双字节码
 * （超音蝠 = 32 3f 42 25 4f 18），表内存的是解码器大端贪心读取的交换码。
 */
class TeamRocketCnDecodeTest {
    private val table = CjkTables.forSha("4ea5b01ee2feda127c230a0d2f80e1aa2b3b81b0")!!

    @After fun tearDown() {
        Gen3Text.cjk = null
    }

    private fun decode(hex: String): String {
        Gen3Text.cjk = table
        return Gen3Text.decode(hex.split(" ").map { it.toInt(16).toByte() }.toByteArray())
    }

    @Test
    fun `registered by the masked ROM sha1`() {
        assertSame(CjkTables.TEAM_ROCKET_CN, table)
        assertNull(CjkTables.forSha("4ea5b01ee2feda127c230a0d2f80e1aa2b3b81b1"))
        assertNull(CjkTables.forSha(null))
        // Elite Redux 的表不受影响
        assertSame(CjkTables.ELITE_REDUX_CN, CjkTables.forSha("d5a391ad7527495324e9acd88535a9a5bcd1a1b3"))
    }

    @Test
    fun `species move and item names decode from their little-endian bytes`() {
        // 超音蝠（存档里验证过的昵称字节）
        assertEquals("超音蝠", decode("32 3f 42 25 4f 18 ff"))
        // 守住 / 吸取（gMovesInfo 条目 181 / 70 的招式名指针目标）
        assertEquals("守住", decode("3e be 43 c1 ff"))
        assertEquals("吸取", decode("40 83 3d b2 ff"))
        // 不满意（改名界面的五个按键之一）
        assertEquals("不满意", decode("31 d7 3b a5 42 13 ff"))
        // 道具：讲究头带 / 神奇糖果（收割对齐的官方译名）
        assertEquals("讲究头带", decode("39 55 39 c0 3f d0 32 e9 ff"))
        assertEquals("神奇糖果", decode("3e 70 3d 32 3f 79 38 04 ff"))
        // 物种：大比鸟 / 毒贝比 / 时拉比（8631=比 的三个独立证据）
        assertEquals("大比鸟", decode("32 e4 31 86 3c 68 ff"))
        assertEquals("毒贝比", decode("33 84 31 72 31 86 ff"))
        assertEquals("时拉比", decode("3e 8e 3a 83 31 86 ff"))
        // 镇名结尾 + 句号（开场白「满目疮痍。」，实机截图核对过）
        assertEquals("满目疮痍。", decode("3b a5 3c 36 32 a0 4e 3d 2f 30 ff"))
        assertEquals("三圣鸟", decode("3e 1e 3e 82 3c 68 ff"))
        // 感叹号收尾（对白结尾码位，全 ROM 54 处串尾一致）
        assertEquals("可以！", decode("3a 38 42 05 2f 35 ff"))
    }

    /** Optional user-supplied ROM; no game files or private paths are included in the repository. */
    @Test
    fun `known Chinese ROM item name decodes from its actual item table`() {
        val dir = System.getenv("POKEDAISY_SAMPLE_DIR")?.let(::File)
        assumeTrue("set POKEDAISY_SAMPLE_DIR to test local samples", dir?.isDirectory == true)
        val sha = "4ea5b01ee2feda127c230a0d2f80e1aa2b3b81b0"
        fun digest(file: File): String = file.inputStream().use { input ->
            val md = MessageDigest.getInstance("SHA-1")
            val buffer = ByteArray(64 * 1024)
            while (true) {
                val n = input.read(buffer)
                if (n < 0) break
                md.update(buffer, 0, n)
            }
            md.digest().joinToString("") { "%02x".format(it) }
        }
        val rom = dir!!.walkTopDown().firstOrNull { it.isFile && it.extension.equals("gba", true) && digest(it) == sha }
        assumeTrue("the known Chinese Team Rocket sample is not present", rom != null)
        val bytes = rom!!.readBytes()
        val entry = 0x008cc594 + 13 * 44 // item #13 in this exact build
        val ptr = (0..3).fold(0L) { value, i -> value or ((bytes[entry + i].toLong() and 0xff) shl (i * 8)) }
        val offset = (ptr - 0x08000000L).toInt()
        require(offset >= 0 && offset + 32 <= bytes.size) { "invalid item-name pointer" }
        Gen3Text.cjk = table
        assertEquals("重复球", Gen3Text.decode(bytes.copyOfRange(offset, offset + 32)))
    }

    @Test
    fun `the first thirty move names decode`() {
        // gMovesInfo 前 30 条（全国编号 1 起），与官方中文译名逐一对齐的锚点
        assertEquals("拍击", decode("3c a2 38 c5 ff"))
        assertEquals("空手劈", decode("3a 44 3e bc 3c da ff"))
        assertEquals("连环巴掌", decode("3a e0 38 85 31 2d 43 2a ff"))
        assertEquals("连续拳", decode("3a e0 41 54 3d be ff"))
        assertEquals("百万吨重拳", decode("31 39 40 1b 33 9c 43 9a 3d be ff"))
        assertEquals("聚宝功", decode("39 dc 31 64 37 9f ff"))
        assertEquals("火焰拳", decode("38 be 41 a0 3d be ff"))
        assertEquals("冰冻拳", decode("31 b7 33 79 3d be ff"))
        assertEquals("雷电拳", decode("3a ad 33 4f 3d be ff"))
        assertEquals("抓", decode("43 c5 ff"))
        assertEquals("夹住", decode("39 0c 43 c1 ff"))
        assertEquals("断头钳", decode("33 95 3f d0 3d 59 ff"))
        assertEquals("旋风刀", decode("41 59 37 1b 33 15 ff"))
        assertEquals("剑舞", decode("39 3d 40 6f ff"))
        assertEquals("居合斩", decode("39 d4 38 36 43 1a ff"))
        assertEquals("起风", decode("3d 3c 37 1b ff"))
        assertEquals("力膀攻击", decode("32 74 31 52 37 9e 38 c5 ff"))
        assertEquals("吹飞", decode("32 a6 33 ed ff"))
        assertEquals("飞翔", decode("33 ed 40 d6 ff"))
        assertEquals("绑紧", decode("31 53 99 97 ff"))
        assertEquals("摔打", decode("3e e8 32 e3 ff"))
        assertEquals("藤鞭", decode("3f 8b 31 9c ff"))
        assertEquals("踩踏", decode("31 e4 3f 56 ff"))
        assertEquals("二连踢", decode("33 c4 3a e0 3f 91 ff"))
        assertEquals("百万吨重踢", decode("31 39 40 1b 33 9c 43 9a 3f 91 ff"))
        assertEquals("飞踢", decode("33 ed 3f 91 ff"))
        assertEquals("回旋踢", decode("38 a6 41 59 3f 91 ff"))
        assertEquals("泼沙", decode("3d 0f 3e 32 ff"))
        assertEquals("头锤", decode("3f d0 32 a9 ff"))
        assertEquals("角撞", decode("39 6a 43 d2 ff"))
    }
}
