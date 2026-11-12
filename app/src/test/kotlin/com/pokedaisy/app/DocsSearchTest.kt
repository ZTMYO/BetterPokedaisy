package com.pokedaisy.app

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * The docs reader: the API's JSON parses into blocks, and DocsSearch ranks website/test/docsearch-cases.json
 * the way the website's search (docsearch.js, docsearch.test.js) does - the two engines must agree.
 */
class DocsSearchTest {
    private val root = generateSequence(File("").absoluteFile) { it.parentFile }
        .first { File(it, "settings.gradle.kts").isFile }
    private val cases = JSONObject(File(root, "website/test/docsearch-cases.json").readText())
    private val manual = DocsApi.parse(cases.getJSONObject("docs").toString())

    @Test
    fun parsesTheApi() {
        assertEquals(4, manual.pages.size)
        assertEquals(listOf("fast-forward", "saves", "screen", "cheats"), manual.sections.single().pages)
        val ff = manual.page("fast-forward")!!
        assertEquals(DocsApi.Block.Heading(2, "smart-mode", "Smart mode"), ff.blocks[1])
        val saves = manual.page("saves")!!.blocks.last() as DocsApi.Block.Bullets
        assertTrue(!saves.ordered && saves.items.single().single() is DocsApi.Block.Paragraph)
        val table = manual.page("screen")!!.blocks.last() as DocsApi.Block.Table
        assertTrue(table.rows.single().first().single().code)
        assertEquals("warning", (manual.page("cheats")!!.blocks.single() as DocsApi.Block.Note).kind)
        assertEquals("saves", manual.next("fast-forward")?.slug)
        assertEquals(null, manual.previous("fast-forward"))
    }

    @Test
    fun ranksLikeTheWebsite() {
        val search = DocsSearch(manual)
        val list = cases.getJSONArray("cases")
        for (i in 0 until list.length()) {
            val c = list.getJSONObject(i)
            val q = c.getString("q")
            val hits = search.search(q)
            if (c.isNull("top")) assertTrue("\"$q\" should find nothing", hits.isEmpty())
            else assertEquals("\"$q\"", c.getString("top"), hits.firstOrNull()?.let { it.page + (it.anchor?.let { a -> "#$a" } ?: "") })
        }
    }

    @Test
    fun wordsStemsTypos() {
        assertEquals(listOf("fast", "forward", "pokemon", "battle"), DocsSearch.terms("How do I fast-forward the Pokémon battles?"))
        assertEquals("cheat", DocsSearch.stem("cheating"))
        assertEquals(1, DocsSearch.distance("shader", "shdaer", 2))
    }

    @Test
    fun changingManualLanguageClearsTheIndexAndKeepsThePage() {
        val ui = com.pokedaisy.app.companion.ui.DocsUiState("saves")
        ui.loadedLanguage = com.pokedaisy.app.companion.i18n.AppLanguage.EN
        ui.manual = manual
        ui.search = DocsSearch(manual)
        ui.started = true
        ui.selectLanguage(com.pokedaisy.app.companion.i18n.AppLanguage.ZH_CN)
        assertEquals("saves", ui.page)
        assertEquals(null, ui.manual)
        assertEquals(null, ui.search)
        assertEquals(false, ui.started)
        assertEquals(com.pokedaisy.app.companion.i18n.AppLanguage.ZH_CN, ui.loadedLanguage)
    }

    @Test
    fun bundledChineseManualCanBeReadAndSearched() {
        val json = File(root, "app/src/main/assets/docs-zh-hans.json").readText(Charsets.UTF_8)
        val chinese = DocsApi.parse(json)
        assertEquals("欢迎使用 PokeDaisy", chinese.page("welcome")?.title)
        assertEquals("安装与初次设置", chinese.page("install")?.title)
        assertEquals("攻略", chinese.page("guide")?.title)
        assertEquals("图鉴", chinese.page("dex")?.title)
        assertEquals("队伍与详细信息", chinese.page("party")?.title)
        assertEquals("地图", chinese.page("map")?.title)
        assertEquals("训练家卡", chinese.page("trainer-card")?.title)
        assertEquals("背包", chinese.page("bag")?.title)
        assertEquals("对战", chinese.page("battle")?.title)
        assertEquals("金手指", chinese.page("cheats")?.title)
        assertEquals("NUZLOCKE 挑战", chinese.page("nuzlocke")?.title)
        assertEquals("RetroAchievements 成就", chinese.page("achievements")?.title)
        assertEquals("快进与游戏速度", chinese.page("fast-forward")?.title)
        assertEquals("存档、备份和即时存档", chinese.page("saves")?.title)
        assertEquals("HUD", chinese.page("hud")?.title)
        assertEquals("屏幕与设备", chinese.page("devices")?.title)
        assertEquals("Linux 掌机基础版本", chinese.page("linux")?.title)
        assertEquals("故障排查与常见问题", chinese.page("troubleshooting")?.title)
        assertEquals("支持的游戏", chinese.page("supported-games")?.title)
        assertEquals("游戏库", chinese.page("library")?.title)
        assertEquals("屏幕、比例与着色器", chinese.page("screen")?.title)
        assertEquals("伴侣面板概览", chinese.page("companion")?.title)
        assertEquals("控制、快捷键和触屏", chinese.page("controls")?.title)
        assertEquals("设置参考", chinese.page("settings-reference")?.title)
        assertTrue(chinese.pages.none { it.title.startsWith("未译：") })
        assertEquals("install", DocsSearch(chinese).search("初次设置").first().page)
        assertEquals("guide", DocsSearch(chinese).search("攻略").first().page)
        assertEquals("dex", DocsSearch(chinese).search("图鉴").first().page)
        assertEquals("party", DocsSearch(chinese).search("队伍与详细信息").first().page)
        assertEquals("trainer-card", DocsSearch(chinese).search("训练家卡").first().page)
        assertEquals("bag", DocsSearch(chinese).search("背包").first().page)
        assertEquals("battle", DocsSearch(chinese).search("对战").first().page)
        assertEquals("cheats", DocsSearch(chinese).search("金手指").first().page)
        assertEquals("fast-forward", DocsSearch(chinese).search("快进与游戏速度").first().page)
        assertEquals("saves", DocsSearch(chinese).search("存档、备份和即时存档").first().page)
        assertEquals("devices", DocsSearch(chinese).search("屏幕与设备").first().page)
        assertEquals("linux", DocsSearch(chinese).search("Linux 掌机基础版本").first().page)
        assertEquals("troubleshooting", DocsSearch(chinese).search("故障排查与常见问题").first().page)
        assertEquals("supported-games", DocsSearch(chinese).search("支持的游戏").first().page)
        assertEquals("library", DocsSearch(chinese).search("游戏库").first().page)
        assertEquals("screen", DocsSearch(chinese).search("屏幕、比例与着色器").first().page)
        assertEquals("companion", DocsSearch(chinese).search("伴侣面板概览").first().page)
        assertEquals("controls", DocsSearch(chinese).search("控制、快捷键和触屏").first().page)
        assertEquals("settings-reference", DocsSearch(chinese).search("设置参考").first().page)
    }

    @Test
    fun chineseSubstringSearchHighlightsTheMatch() {
        assertEquals(listOf("自动备份存档", "pokemon"), DocsSearch.terms("自动备份存档，Pokémon"))
        val hit = DocsSearch(manual).search("备份存档").first()
        assertEquals("saves", hit.page)
        assertEquals(listOf("备份存档"), hit.marks.map { hit.snippet.substring(it.first, it.last + 1) })
        val overlap = DocsSearch(manual).search("备份 备份存档").first()
        assertEquals(listOf("备份存档"), overlap.marks.map { overlap.snippet.substring(it.first, it.last + 1) })
    }

    @Test
    fun snippetsMarkTheMatch() {
        val hit = DocsSearch(manual).search("television").first()
        val marked = hit.marks.map { hit.snippet.substring(it.first, it.last + 1) }
        assertEquals(listOf("television"), marked)
    }
}

