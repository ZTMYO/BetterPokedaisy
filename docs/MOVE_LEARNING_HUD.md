# MOVE ADVICE（招式替换建议）适配指南

这篇文档记录 HUD 上「忘记 X，学习 Y」卡片（`MOVE ADVICE`）的实现方式，以及把同一功能适配到新游戏 / 新版本时该怎么做。火红（FireRed）的适配是在 2026-11-26 用一台真实掌机上的 Rev 1 零售卡带逐字节验证的。

## 1. 功能链路

```
gamedata/gen3/retail/*.toml       summaryScreen 地址
        ↓  gen_gamedata.py
NativeConfigsGen.kt               每局游戏的 NativeConfig
        ↓
NativeReader.readNativeTelemetry  读取 summary 页面状态
        ↓
MoveLearningWatch                 判断是不是一次新的“选择要忘记的招式”事件
        ↓
Telemetry / SnapshotView           moveLearning = MoveLearningPrompt(partyIndex, move)
        ↓
MoveLearningHudCard               计算建议并画卡片
```

相关文件：

- `app/src/main/kotlin/com/pokedaisy/app/companion/data/MoveLearningWatch.kt`
- `app/src/main/kotlin/com/pokedaisy/app/companion/data/NativeReader.kt`
- `app/src/main/kotlin/com/pokedaisy/app/companion/data/MoveLearning.kt`、`MoveAdvice.kt`
- `app/src/main/kotlin/com/pokedaisy/app/companion/ui/MoveLearningHudCard.kt`
- `app/src/main/kotlin/com/pokedaisy/app/PokeDaisyActivity.kt`（卡片显示条件）
- `gamedata/schema/gen3_config.toml` 的 `summaryScreen` 字段

卡片显示还要满足：`hudOn && !hudUpright && snapshot.moveLearning != null && FieldHudStyle.shows(MOVE_ADVICE)`，并且 `MoveLearningHudCard` 内部要求 `snapshot.pokedex.tables` 和 `guideTables` 能提供学习表（`MoveLearning.available`）。

## 2. 两套现有布局

### 2.1 绿宝石（Emerald）

| 内容 | 地址 / 偏移 |
| --- | --- |
| `sMonSummaryScreen` 指针变量 | `0x0203CF1C` |
| 结构内 `mode` | `+0x40BC`（`PSS_MODE_SELECT_MOVE = 3`） |
| 结构内当前查看的队伍槽位 | `+0x40BE` |
| 结构内新招式 | `+0x40C4` |
| `gPartyMenu` | `0x0203CEC8`，`data1` 在 `+0x0E` |
| `gMoveToLearn` | `0x020244E2` |

绿宝石的页面判断依赖 `mode == 3`，新招式同时可能来自 `gMoveToLearn`（升级）或 `gPartyMenu.data1`（招式学习器 / 教学）。

### 2.2 火红 / 叶绿（FireRed / LeafGreen）

零售火红 Rev 0、Rev 1、叶绿和火红叶绿的语言版共用同一套 EWRAM 布局：

| 内容 | 地址 / 偏移 |
| --- | --- |
| `sMonSummaryScreen` 指针变量 | `0x0203B140` |
| `sLastViewedMonIndex` | `0x0203B16C`（= 指针变量地址 `+0x2C`） |
| 结构内 `mode` | `+0x3208`（`PSS_MODE_SELECT_MOVE = 2`，注意不是绿宝石的 3） |
| 结构内 `lastIndex` | `+0x3210`（队伍数量 - 1） |
| 结构内 `curPageIndex` | `+0x3214`（`PSS_PAGE_MOVES_INFO = 3`） |
| 结构内 `moveIds[]` | 实际从 `+0x3258` 开始 |
| 结构内新招式 `moveIds[4]` | `+0x3260`（`u16`） |
| 结构内 `numMoves` | `+0x3264` |
| 结构内 `selectMoveInputHandlerState` | `+0x3288`（0–5 仍在页面，6 开始退出，7 销毁） |
| 结构内 `savedCallback` | `+0x32F8`（可用于验证结构） |
| 结构内 `monList` | `+0x32F4`（应为 `gPlayerParty = 0x02024284`） |

火红选择“要忘记哪个招式”时：

- `ShowSelectMovePokemonSummaryScreen()` 把模式设为 2，页面设为 3；
- 要学习的新招式写在 `moveIds[4]`（`+0x3260`），升级、学习器、教学都走这里；
- 当前队伍槽位在 `sLastViewedMonIndex`；
- 因此火红不需要再读 `gMoveToLearn` 或 `gPartyMenu.data1`。

注意：`struct PokemonSummaryScreenData` 的源码注释里 `moveTypes` / `moveIds` 标注为 `0x3250` / `0x325A`，但真实零售 Rev 1 数据是 `0x324E` / `0x3258`。**偏移要以真机内存为准，不要只信注释。**

## 3. 这次踩到的坑（最重要）

`NativeReader` 里原来的读取长度只到新招式：

```kotlin
// 错误：0x3260 - 0x3208 + 2 = 0x5A 字节
val raw = client.readCoreMemory(summaryPtr + 0x3208, 0x5A)
val inputState = raw[0x3288 - 0x3208]  // 0x80 >= 0x5A，越界！
```

越界异常被外层的 `runCatching { ... }.getOrNull()` 静默吞掉，`summary` 永远是 `null`，表现就是「页面明明开着，卡片永远不出现」。

修复是读到 `inputState` 为止：

```kotlin
val raw = client.readCoreMemory(
    summaryPtr + FRLG_SUMMARY_MODE_OFF,
    FRLG_SUMMARY_INPUT_STATE_OFF - FRLG_SUMMARY_MODE_OFF + 1,
)
```

类似地，以后加字段时一定要让读取长度覆盖到最后一个要访问的偏移，并尽量避免用大范围 `runCatching` 吞掉编程错误。

## 4. 页面生命周期与收起条件

火红的 `state3270` 在 MOVES_INFO 页面保持 0（选择招式页面由 `Task_HandleInput_SelectMove` 处理，不走 `Task_InputHandler_Info`），所以不能用它判断页面是否打开。当前实现用：

```kotlin
mode == 2
page == 3
inputState < 6
newMove > 0
monIndex in 0 until party.size
```

收起时机：

- 按 B 退回普通招式页：`curPageIndex` 从 3 变成 2 → 条件失败；
- 选好招式确认：`selectMoveInputHandlerState` 变成 6 → 条件失败；
- 完全退出 / 屏幕销毁：`sMonSummaryScreen` 指针变量变成 0（实测），同时上面的条件也会失败。

只判断 `mode == 2` 不靠谱：结构体在页面退出后可能仍然保留，必须同时看页面 / 输入状态。

## 5. 真机抓内存流程

项目里已经有不需要重启游戏的调试入口：

```bash
adb -s <serial> shell mkdir -p /storage/emulated/0/Android/data/com.ztmyo.betterpokedaisy/files/fixtures/<key>
adb -s <serial> shell am start \
  -n com.ztmyo.betterpokedaisy/com.pokedaisy.app.PokeDaisyActivity \
  --es dumpFixture /storage/emulated/0/Android/data/com.ztmyo.betterpokedaisy/files/fixtures/<key>
```

下一次采样会把完整 EWRAM（256 KiB）+ IWRAM（32 KiB）写进去。然后用 adb pull 下来分析：

- EWRAM 基址 `0x02000000`，IWRAM 基址 `0x03000000`；
- 先读 `summaryScreen` 地址里的指针；
- 验证指针落在 EWRAM、4 字节对齐；
- 用 `+0x32F4 == gPlayerParty (0x02024284)`、`+0x32F8` 是 ROM 指针（`0x08xxxxxx`）来确认不是碰巧命中的结构；
- 再解 `mode / page / input / newMove / monIndex`。

### Windows / Git Bash 注意

- `adb` 是 Windows 程序，给它的本地路径要用 `D:/...`；用 `MSYS2_ARG_CONV_EXCL='*'` 时它不会再把 `/d/...` 转成 Windows 路径。
- Python 也是 Windows 版，读文件用 `D:/tmp/...`，不要用 `/d/tmp/...`。
- 抓完后把 `ewram.bin` / `iwram.bin` 放在仓库外；不要提交真实存档内存。

## 6. JDI 实时检查

排查「内存对但 UI 没有」时，直接读运行中的对象比反复重装快：

```bash
adb -s <serial> forward tcp:8700 jdwp:<pid>
javac --add-modules jdk.jdi Probe.java
java --add-modules jdk.jdi Probe
```

可以读：

- `com.pokedaisy.app.PokeDaisyActivity` 实例的 `hudOn` / `hudUpright` / `hudStyle`；
- `TelemetryStore._snapshot` 里的 `SnapshotView`（`moveLearning`、`game`、`inBattle`、`party`、`pokedex`、`guideTables`）；
- `TelemetrySampler.nativeCfg.summaryScreen`、`activeGame`、`retailGen3Rom`；
- `MoveLearningWatch` 的 `started / lastLevelMove / summaryWasUp / onScreen`。

还可以在 `NativeReaderKt.readNativeTelemetry` 的源码行上打断点；例如断在 `val levelMove = ...` 能看到 `summaryPtr` / `summary`，断在火红分支能看到 `mode / page / raw`。

注意：

- JDI 挂在 Dalvik 上**不能可靠地 invoke 方法**，以读字段 / 断点局部变量为主；
- 每次 attach 后一定要 `resume()` 再 `dispose()`，否则游戏会卡住。

## 7. 适配一个新游戏 / 新版本的步骤

1. **确认游戏身份**：`activeGame`、`romCode/romRev`、`activeNativeConfig` 是否指向正确的零售配置。
2. **打开目标页面**（进入“要忘记哪个招式”），用 `dumpFixture` 抓一份内存；再抓一份页面关闭后的内存做对比。
3. **找 `sMonSummaryScreen` 指针变量**：
   - 在 EWRAM 里找指向结构的指针；
   - 用 `monList == gPlayerParty`、`savedCallback` 是 ROM 指针、`mode/page` 合理来验证；
   - 在 ROM 的字面量池里统计该 EWRAM 地址出现次数，通常页面代码会多次加载它。
4. **确认结构偏移**：以反编译源码为起点，但用真机数据的 `moveTypes` / `moveIds` 序列（例如已知的 5 个招式 ID）反推真实偏移。注释和实际偏移可能差 2 字节。
5. **确认页面状态**：优先找 `curPageIndex` / `inputState` 这种“页面专属”字段；不要只依赖 `mode`，因为结构体可能比屏幕活得久。
6. **加配置**：
   - 只在**零售** config 上写 `summaryScreen`；
   - 不要写在 hack 共用的 base config 上；
   - 如果有语言版 / 移植版继承该 config，且它们的 EWRAM 布局未验证，显式写 `summaryScreen = 0`；
   - 生成代码：`PYTHONPATH=<tomli shim> python scripts/gen_gamedata.py`，提交 `NativeConfigsGen.kt` 和 `3ds/core/pd_configs_gen.h`。
7. **加读取分支**：
   - 用 `activeGame` 或 config 里的布局标记区分结构；
   - 读取长度覆盖所有要访问的字段；
   - 对指针做 EWRAM 范围、对齐、`monList` / callback 校验。
8. **验证**：页面出现 → 卡片出现；按 B 返回 → 卡片消失；确认替换 → 卡片消失；普通摘要页 → 不应出现。

## 8. 快速自查清单

- [ ] `summaryPtr` 是有效的 EWRAM 指针，`monList == gPlayerParty`；
- [ ] `mode` / `page` 的值与反编译源码一致；
- [ ] 新招式在预期的字段里，且是 1..0x7FF 的合法招式；
- [ ] `monIndex` 在队伍范围内；
- [ ] `raw` 读取长度覆盖 `inputState`（火红是 `0x81`，不是 `0x5A`）；
- [ ] `activeGame` 与 config 对得上（叶绿、火红改版也会是 `FIRERED`）；
- [ ] HUD 开着、没有竖屏、`MOVE_ADVICE` 没有被隐藏；
- [ ] `snapshot.pokedex.tables` / `guideTables` 非空，`MoveLearning.available` 为 true；
- [ ] 退出页面后 `moveLearning` 回到 `null`。

## 9. 参考

- 火红 Rev 1 零售 ROM 的 SHA1：`dd5945db9b930750cb39d00c84da8571feebf417`（与 pret/pokefirered 的 `firered_rev1` 一致）。
- 抓包日期：2026-11-26；实测页面：Five Isle Meadow，MEOWTH 学习 FAINT ATTACK（move 185）。
- 绿宝石地址和字段的原始注释：`gamedata/gen3/retail/emerald.toml` 的 `emerald_retail`。
