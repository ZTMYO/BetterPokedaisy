package com.pokedaisy.app.companion.ui

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.resources.Density
import com.android.resources.ScreenOrientation
import com.pokedaisy.app.DocsApi
import com.pokedaisy.app.DocsStore
import com.pokedaisy.app.companion.i18n.AppLanguage
import com.pokedaisy.app.companion.i18n.L10n
import com.pokedaisy.app.companion.ui.theme.AppTheme
import java.io.File
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/** Chinese UI at known screen sizes; RG DS density is an assumption until measured on a device. */
@RunWith(Parameterized::class)
class ChineseDeviceScreenshotTest(private val device: String, private val config: DeviceConfig, private val window: Boolean) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun devices() = listOf(
            arrayOf("Thor", DeviceConfig.PIXEL_5.copy(screenWidth = 1240, screenHeight = 1080, orientation = ScreenOrientation.LANDSCAPE), false),
            arrayOf("DuoLite", DeviceConfig.PIXEL_5.copy(screenWidth = 1280, screenHeight = 960, orientation = ScreenOrientation.LANDSCAPE), false),
            arrayOf("RgDs", DeviceConfig.PIXEL_5.copy(screenWidth = 640, screenHeight = 480, density = Density.HIGH, xdpi = 200, ydpi = 200, orientation = ScreenOrientation.LANDSCAPE), false),
            arrayOf("Single", DeviceConfig.PIXEL_5.copy(screenWidth = 1920, screenHeight = 1080, density = Density.create(369), orientation = ScreenOrientation.LANDSCAPE), true),
        )
    }

    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = config, theme = "android:Theme.Material.NoActionBar")

    @Before fun setUp() {
        CompanionScreenshotTest().game()
        L10n.apply(AppLanguage.ZH_CN.code, null)
    }

    @After fun tearDown() = L10n.apply("EN", null)

    private fun shot(content: @Composable () -> Unit) = paparazzi.snapshot(device) {
        if (window) {
            Box(Modifier.fillMaxSize().background(Color(0xFF3A5A4A))) {
                SidePanelHost(window = true, onClose = {}, compactText = true, content = content)
            }
        } else content()
    }

    private fun tab(name: String) = shot {
        CompanionScreen(SampleCompanion.snapshot, SampleCompanion.Slots, SampleCompanion.Settings(), initialTab = name)
    }

    @Test fun settings() = tab("SETTINGS")
    @Test fun party() = tab("PARTY")
    @Test fun guide() = tab("GUIDE")
    @Test fun chineseBattle() = shot { AppTheme {
        val party = SampleCompanion.snapshot.party
        val moves = listOf(
            com.pokedaisy.app.companion.data.MoveView("撞击", "Normal", 32, 40, id = 33),
            com.pokedaisy.app.companion.data.MoveView("闪光", "Normal", 20, 0, id = 148),
            com.pokedaisy.app.companion.data.MoveView("金属撞击", "Steel", 32, 40, id = 499),
        )
        val player = party[0].copy(name = "铁哑铃", nickname = null, moves = moves)
        val foe = party[1].copy(name = "冰宝", nickname = null)
        BattleInfoScreen(listOf(player), listOf(foe), isDouble = false, showHints = true, onBack = {}, onShowSuggestions = {})
    } }


    @Test fun chinesePartyNames() = shot { AppTheme {
        val party = SampleCompanion.snapshot.party.take(3).mapIndexed { index, mon ->
            mon.copy(nickname = listOf("铁哑铃", "迷你冰", "幕下力士")[index])
        }
        PartyGrid(party, selected = 0, onSlotClick = {})
    } }


    private fun exampleDex() = com.pokedaisy.app.companion.data.DexEntry(
        national = 25, species = 25, category = "MOUSE", heightDm = 4, weightHg = 60,
        description = "ROM-specific description. This effect may differ between game versions.",
        type1 = 0, type2 = 0, baseStats = listOf(35, 55, 40, 50, 50, 90),
        abilities = listOf("STATIC", "CUSTOM ABILITY"), hiddenAbility = "HIDDEN ABILITY",
        catchRate = 190, genderRatio = 127, eggGroups = emptyList(),
    )

    private fun dexInfo(entry: com.pokedaisy.app.companion.data.DexEntry) = shot { AppTheme {
        val m = rememberGbaTextMetrics()
        Box(Modifier.fillMaxSize().padding(12.dp)) {
            DexInfoSection(entry, m, m, Modifier.fillMaxWidth(0.58f).fillMaxHeight())
        }
    } }

    @Test fun verifiedChineseDexInfo() {
        com.pokedaisy.app.companion.data.UnboundChineseBattleNames.use(true)
        try { dexInfo(exampleDex().copy(description = "飞行的时候一直从嘴里发出超声波。", abilities = listOf("精神力"), hiddenAbility = null)) }
        finally { com.pokedaisy.app.companion.data.UnboundChineseBattleNames.use(false) }
    }

    @Test fun verifiedChineseDexNames() {
        val names = com.pokedaisy.app.companion.data.UnboundChineseBattleNames
        val oldReader = com.pokedaisy.app.companion.data.PokedexSource.reader
        com.pokedaisy.app.companion.data.PokedexSource.reader = object : com.pokedaisy.app.companion.data.MemoryReader {
            override fun readCoreMemory(addr: Long, size: Int): ByteArray {
                val raw = when (addr) {
                    0x08A40A10L + 33L * 13 -> byteArrayOf(0x10, 0xe1.toByte(), 0x05, 0x84.toByte(), 0xff.toByte())
                    0x0966A98CL + 398L * 11 -> byteArrayOf(0x0d, 0x04, 0x0e, 0xaf.toByte(), 0x08, 0xb8.toByte(), 0xff.toByte())
                    else -> error("unexpected ROM read")
                }
                return ByteArray(size) { if (it < raw.size) raw[it] else 0 }
            }
        }
        names.use(true)
        names.species(398)
        names.move(33)
        try { shot { AppTheme {
            val m = rememberGbaTextMetrics(1f)
            Column(Modifier.fillMaxWidth(0.58f).fillMaxHeight().padding(12.dp)) {
                FamilyMon(398, 374, 398, m, {}, Modifier.fillMaxWidth())
                MoveLine("等级 100", 33, m)
            }
        } } } finally { names.use(false); com.pokedaisy.app.companion.data.PokedexSource.reader = oldReader }
    }

    @Test fun unverifiedDexInfo() = dexInfo(exampleDex())

    // Isolate the lower rows so the small screen captures both ability labels and their values.
    @Test fun unverifiedDexAbilities() = dexInfo(exampleDex().copy(description = "", baseStats = emptyList()))

    @Test fun unverifiedDexMove() = shot { AppTheme {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxWidth(0.58f).fillMaxHeight().padding(12.dp)) {
                val small = rememberGbaTextMetrics(1f)
                MoveLine("等级 100", 225, small)
                MoveLine("", 0, small)
            }
        }
    } }

    @Test fun unverifiedItemDescription() = shot { AppTheme {
        val item = com.pokedaisy.app.companion.data.ItemView(
            9999, "CUSTOM ITEM", 1, null,
            description = "This ROM-specific item has a long description. Its effect may differ between versions. " +
                "The original text must remain readable while the action buttons stay reachable.",
        )
        val actions = object : com.pokedaisy.app.companion.ItemUse {
            override fun canUse(itemId: Int) = true
            override fun canGive(itemId: Int) = true
            override fun use(itemId: Int, done: (com.pokedaisy.app.companion.data.ItemUseOutcome) -> Unit) = Unit
        }
        Box(Modifier.fillMaxSize()) {
            ItemsScreen(listOf(item), modifier = Modifier.fillMaxWidth().fillMaxHeight(0.84f), itemUse = actions, initialItemId = item.itemId)
        }
    } }

    @Test fun manual() {
        val root = generateSequence(File("").absoluteFile) { it.parentFile }.first { File(it, "settings.gradle.kts").isFile }
        val manual = DocsApi.parse(File(root, "app/src/main/assets/docs-zh-hans.json").readText(Charsets.UTF_8))
        val source = object : DocsSource {
            override fun cached() = manual
            override fun refresh() = DocsStore.Refresh.Unchanged
            override fun image(manual: DocsApi.Manual, image: DocsApi.Block.Image): Bitmap? = null
        }
        val ui = DocsUiState().apply { this.manual = manual; loadedLanguage = AppLanguage.ZH_CN; started = true; open("settings-reference") }
        shot { AppTheme { DocsReader(ui, rememberGbaTextMetrics(), source) } }
    }
}
