package com.pokedaisy.app.companion.ui

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.resources.ScreenOrientation
import com.pokedaisy.app.companion.data.SnapshotView
import com.pokedaisy.app.companion.data.withMovesVs
import com.pokedaisy.app.companion.i18n.AppLanguage
import com.pokedaisy.app.companion.i18n.L10n
import com.pokedaisy.app.companion.ui.theme.AppTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * The companion's main screens in each non-English language - for checking
 * that translations fit (German runs longest) and Japanese renders in the
 * pixel font. Shots are named `..._<screen>_<LANG>.png`:
 *
 *     ./gradlew :app:recordPaparazziDebug --tests '*LocalizedScreenshotTest'
 */
@RunWith(Parameterized::class)
class LocalizedScreenshotTest(private val lang: AppLanguage) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun languages() = AppLanguage.entries.filter { it != AppLanguage.EN }
    }

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5.copy(
            screenWidth = 1240, screenHeight = 1080, orientation = ScreenOrientation.LANDSCAPE,
        ),
        theme = "android:Theme.Material.NoActionBar",
    )

    @Before
    fun setUp() {
        CompanionScreenshotTest().game()
        L10n.apply(lang.code, null)
    }

    @After
    fun tearDown() = L10n.apply("EN", null)

    private fun tab(name: String) = paparazzi.snapshot(lang.code) {
        CompanionScreen(SampleCompanion.snapshot, SampleCompanion.Slots, SampleCompanion.Settings(), initialTab = name)
    }

    private fun chineseDocs(slug: String? = null, search: String = "") {
        if (lang != AppLanguage.ZH_CN) return
        val root = generateSequence(java.io.File("").absoluteFile) { it.parentFile }.first { java.io.File(it, "settings.gradle.kts").isFile }
        val manual = com.pokedaisy.app.DocsApi.parse(java.io.File(root, "app/src/main/assets/docs-zh-hans.json").readText(Charsets.UTF_8))
        val source = object : DocsSource {
            override fun cached() = manual
            override fun refresh() = com.pokedaisy.app.DocsStore.Refresh.Unchanged
            override fun image(manual: com.pokedaisy.app.DocsApi.Manual, image: com.pokedaisy.app.DocsApi.Block.Image): android.graphics.Bitmap? = null
        }
        val ui = DocsUiState().apply { this.manual = manual; loadedLanguage = lang; started = true; query = search; slug?.let(::open) }
        paparazzi.snapshot(lang.code) { AppTheme { DocsReader(ui, rememberGbaTextMetrics(), source) } }
    }

    @Test fun chineseManual() = chineseDocs("welcome")
    @Test fun chineseInstallManual() = chineseDocs("install")
    @Test fun chineseGuideManual() = chineseDocs("guide")
    @Test fun chineseDexManual() = chineseDocs("dex")
    @Test fun chinesePartyManual() = chineseDocs("party")
    @Test fun chineseBagManual() = chineseDocs("bag")
    @Test fun chineseBattleManual() = chineseDocs("battle")
    @Test fun chineseCheatsManual() = chineseDocs("cheats")
    @Test fun chineseNuzlockeManual() = chineseDocs("nuzlocke")
    @Test fun chineseAchievementsManual() = chineseDocs("achievements")
    @Test fun chineseSpeedManual() = chineseDocs("fast-forward")
    @Test fun chineseSavesManual() = chineseDocs("saves")
    @Test fun chineseHudManual() = chineseDocs("hud")
    @Test fun chineseDevicesManual() = chineseDocs("devices")
    @Test fun chineseLinuxManual() = chineseDocs("linux")
    @Test fun chineseTroubleshootingManual() = chineseDocs("troubleshooting")
    @Test fun chineseSupportedGamesManual() = chineseDocs("supported-games")
    @Test fun chineseLibraryManual() = chineseDocs("library")
    @Test fun chineseScreenManual() = chineseDocs("screen")
    @Test fun chineseCompanionManual() = chineseDocs("companion")
    @Test fun chineseControlsManual() = chineseDocs("controls")
    @Test fun chineseSettingsReferenceManual() = chineseDocs("settings-reference")
    @Test fun chineseMapManual() = chineseDocs("map")
    @Test fun chineseTrainerManual() = chineseDocs("trainer-card")
    @Test fun chineseManualSearch() = chineseDocs(search = "助手")

    @Test fun chineseGameBagChips() {
        if (lang != AppLanguage.ZH_CN) return
        val previous = com.pokedaisy.app.companion.data.activeGame
        val labels = try {
            listOf(com.pokedaisy.app.companion.data.GameKind.UNBOUND, com.pokedaisy.app.companion.data.GameKind.ROWE,
                com.pokedaisy.app.companion.data.GameKind.MARIOMON, com.pokedaisy.app.companion.data.GameKind.CRYSTAL).map {
                com.pokedaisy.app.companion.data.activeGame = it; companionTabLabel("ITEMS")
            }
        } finally { com.pokedaisy.app.companion.data.activeGame = previous }
        paparazzi.snapshot(lang.code) {
            AppTheme {
                androidx.compose.foundation.layout.Row {
                    labels.forEach { label -> TabChip(label, selected = false, modifier = androidx.compose.ui.Modifier.weight(1f)) {} }
                }
            }
        }
    }

    @Test fun chineseGen1BattleButtons() {
        if (lang != AppLanguage.ZH_CN) return
        val previous = com.pokedaisy.app.companion.data.activeGame
        com.pokedaisy.app.companion.data.activeGame = com.pokedaisy.app.companion.data.GameKind.CRYSTAL
        try {
            paparazzi.snapshot(lang.code) {
                AppTheme { Gen1ActionButtons(null, null, true, null, androidx.compose.ui.Modifier.fillMaxSize()) }
            }
        } finally { com.pokedaisy.app.companion.data.activeGame = previous }
    }

    @Test fun settings() = tab("SETTINGS")
    @Test fun party() = tab("PARTY")
    @Test fun items() = tab("ITEMS")
    @Test fun dex() = tab("DEX")
    @Test fun dexNameRows() = paparazzi.snapshot(lang.code) {
        AppTheme {
            androidx.compose.foundation.layout.Column(modifier = androidx.compose.ui.Modifier.padding(start = 4.dp)) {
                val m = rememberGbaTextMetrics()
                DexRow(1, 1, 1, caught = true, seen = true, m = m, small = m) {}
                DexRow(25, 25, 1, caught = true, seen = true, m = m, small = m) {}
                GbaText(com.pokedaisy.app.companion.data.dexSpeciesLabel(1017, "OGERPON"),
                    androidx.compose.ui.graphics.Color.Black, androidx.compose.ui.graphics.Color.LightGray, m)
                GbaText(com.pokedaisy.app.companion.data.dexSpeciesLabel(1, "改版专属"),
                    androidx.compose.ui.graphics.Color.Black, androidx.compose.ui.graphics.Color.LightGray, m)
            }
        }
    }
    @Test fun guide() = tab("GUIDE")
    @Test fun guideGen2() = paparazzi.snapshot(lang.code) {
        AppTheme {
            val entry = com.pokedaisy.app.companion.data.GuideEntry(
                "Why did the POKéMON on this route change?",
                "The game's clock has three times of day: morning from 4 AM, day from 10 AM and night from 6 PM. Grass and caves have a list for each, and HERE shows the one for the time your game's clock says.",
                "Look at the clock.",
            )
            val section = com.pokedaisy.app.companion.data.GuideSection("TIPS", listOf(entry))
            val page = com.pokedaisy.app.companion.data.GuidePage("TIPS", listOf(section))
            val source = GuideSource(com.pokedaisy.app.companion.data.GameGuide(listOf(page)), null, null, null)
            val ui = GuideUiState().apply { this.page = page.title; open = key(page, section, entry); level = 2 }
            GuideScreen(source, ui, SampleCompanion.snapshot)
        }
    }
    @Test fun map() = tab("MAP")
    @Test fun states() = tab("STATES")
    @Test fun more() = tab("MORE")

    /** Battle INFO against an Onix with the game in this language: its types in the game's words (GitHub #40). */
    @Test fun battleInfoTypes() {
        com.pokedaisy.app.companion.data.romLanguage = lang.romLetter ?: 'E'   // no game is in Portuguese
        try {
            paparazzi.snapshot(lang.code) {
                AppTheme {
                    val party = SampleCompanion.snapshot.party
                    val mu = com.pokedaisy.app.companion.data.typeMatchups(
                        com.pokedaisy.app.companion.data.typeIdOf("Rock"), com.pokedaisy.app.companion.data.typeIdOf("Ground"),
                    )
                    val onix = party[1].copy(
                        name = "ONIX", types = listOf("Rock", "Ground"),
                        weaknesses = mu.weaknesses, resistances = mu.resistances, immunities = mu.immunities,
                    )
                    BattleInfoScreen(listOf(party[0].withMovesVs(onix)), listOf(onix), isDouble = false, showHints = true, onBack = {}, onShowSuggestions = {})
                }
            }
        } finally {
            com.pokedaisy.app.companion.data.romLanguage = 'E'
        }
    }

    /** The dex page's EVOLVE (EEVEE: stones, friendship by day / at night) and MOVES (BULBASAUR: level-up, TM / HM). */
    @Test fun dexEvolve() = dexSection(133, DexSection.EVOLVE)
    @Test fun dexMoves() = dexSection(1, DexSection.MOVES)

    private fun dexSection(entry: Int, section: DexSection) {
        val dex = SampleCompanion.snapshot.pokedex!!
        val g = com.pokedaisy.app.companion.data.GUIDE_TABLES_FIRERED_REV1
        val species = com.pokedaisy.app.companion.data.PokedexSource.speciesFor(dex.tables, entry)
        com.pokedaisy.app.companion.data.PokedexSource.entry(dex.tables, entry)
        com.pokedaisy.app.companion.data.PokedexSource.frontSprite(dex.tables, species)
        com.pokedaisy.app.companion.data.DexDetails.family(dex.tables, species)?.forEach {
            com.pokedaisy.app.companion.data.DecompIconSource.get(it.from); com.pokedaisy.app.companion.data.DecompIconSource.get(it.to)
        }
        com.pokedaisy.app.companion.data.DexDetails.levelUp(dex.tables, g, species)
        com.pokedaisy.app.companion.data.DexDetails.teachable(dex.tables, g, species)
        paparazzi.snapshot(lang.code) {
            AppTheme {
                val ui = DexUiState(androidx.compose.foundation.lazy.rememberLazyListState())
                ui.open = entry
                ui.section = section
                PokedexEntryScreen(dex, ui, entry, guide = g)
            }
        }
    }

    @Test
    fun partyStats() = paparazzi.snapshot(lang.code) {
        AppTheme { MonDetailScreen(SampleCompanion.snapshot.party, 0, {}, {}, initialShowStats = true) }
    }

    @Test
    fun partyMoves() = paparazzi.snapshot(lang.code) {
        AppTheme { MonDetailScreen(SampleCompanion.snapshot.party, 0, {}, {}) }
    }

    @Test
    fun battleInfo() = paparazzi.snapshot(lang.code) {
        AppTheme {
            val party = SampleCompanion.snapshot.party
            BattleInfoScreen(
                listOf(party[0].withMovesVs(party[1])), listOf(party[1]), isDouble = false, showHints = true,
                onBack = {}, onShowSuggestions = {}, onShowStats = {},
            )
        }
    }

    @Test
    fun battleStats() = paparazzi.snapshot(lang.code) {
        AppTheme {
            val party = SampleCompanion.snapshot.party
            BattleStatsScreen(listOf(party[0]), listOf(party[1]), "FOE", onBack = {})
        }
    }

    /** The LANGUAGE row - "(LANGUAGE)" beside the word, so it's findable from any language - in the settings look. */
    /** WHICH GAME IS THIS? - its message, LIKELY tags and NONE OF THESE must fit in every language. */
    @Test fun gamePicker() = paparazzi.snapshot(lang.code) {
        CompanionScreen(
            SnapshotView(
                connected = false, error = "unrecognized FireRed-based ROM hack", unsupported = true,
                gamePicker = SamplePicker.picker(miss = com.pokedaisy.app.companion.data.BestEffort.Miss.NO_PARTY),
            ),
            SampleCompanion.Slots, SampleCompanion.Settings(), askForSupport = {}, gamePick = SamplePicker.actions,
        )
    }

    /** NOT SUPPORTED after NONE OF THESE, both buttons side by side: the longest labels must still fit. */
    @Test fun gamePickerDeclined() = paparazzi.snapshot(lang.code) {
        CompanionScreen(
            SnapshotView(connected = false, error = "unrecognized FireRed-based ROM hack", unsupported = true, gamePicker = SamplePicker.picker(declined = true)),
            SampleCompanion.Slots, SampleCompanion.Settings(), askForSupport = {}, gamePick = SamplePicker.actions,
        )
    }

    @Test fun languageRow() = languageShot(picker = false)

    /** Its picker: each language in its own name. */
    @Test fun languagePicker() = languageShot(picker = true)

    private fun languageShot(picker: Boolean) = paparazzi.snapshot(lang.code) {
        AppTheme {
            val m = rememberGbaTextMetrics()
            androidx.compose.foundation.layout.Box(androidx.compose.ui.Modifier.fillMaxSize()) {
                OptionListWindow(m, androidx.compose.ui.Modifier.fillMaxSize()) {
                    GroupedRows(
                        listOf(
                            groupTitle("COMPANION"),
                            SettingRow("LANGUAGE", L10n.settingLabel(com.pokedaisy.app.companion.i18n.LANGUAGE_AUTO)) {},
                            SettingRow("TAB BAR", "5 TABS") {},
                        ),
                        m, cursor = -1, onClick = {},
                    )
                }
                if (picker) {
                    val opts = L10n.options
                    OptionSelector("LANGUAGE", opts.map { it.second }, opts.first().second, { it }, m, onPick = {}, onDismiss = {})
                }
            }
        }
    }

    @Test
    fun suggestions() = paparazzi.snapshot(lang.code) {
        AppTheme {
            val party = SampleCompanion.snapshot.party
            SuggestionsScreen(party, foe = party[1], onBack = {}, onShowInfo = {}, active = listOf(party[0]))
        }
    }
}
