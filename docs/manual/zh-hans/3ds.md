---
title: 在 Nintendo 3DS 上安装
summary: 一步步把 PokeDaisy 自制程序装到 3DS 上，添加游戏和存档，并开启声音。
section: start
order: 80
source_sha256: 419646b1398b9a81a526a1aa02e189bfcd306b30bc613560f51339dd8ae6c5c1
keywords: [3ds, new 3ds, 2ds, nintendo 3ds, 自制程序, homebrew launcher, 3dsx, luma, luma3ds, rosalina, cfw, 自定义固件, sd 卡, dspfirm, dsp 固件, 没有声音, 安装 3ds]
---

PokeDaisy 也能作为自制程序（homebrew）应用在 Nintendo 3DS 上运行：游戏在上屏运行，助手位于触摸屏（下屏），并拥有自己的游戏库、存档编辑器以及存档之间的交换功能。本页将带你从一台已安装自定义固件的 3DS 开始，直到第一次进入游戏。

> [!NOTE]
> 3DS 版本是早期预览版，专为 **New Nintendo 3DS、New 3DS XL 和 New 2DS XL** 打造。初代 3DS、3DS XL 或 2DS 也许能启动它，但运行可能较慢。目前其菜单仍为英文。

![3DS 上的 PokeDaisy 游戏库：上屏显示选中的游戏，触摸屏显示游戏列表](shots/3ds-library.png "游戏库：上方是光标所在的游戏，下方是你的游戏")

## 你需要什么

- **一台已安装自定义固件（Luma3DS）和 Homebrew Launcher 的 3DS。** 如果你的主机还没有这些，请按照 [3ds.hacks.guide](https://3ds.hacks.guide/) 操作。这是社区维护的指南，会针对每种主机和系统版本持续更新，本页所需的准备工作它都会完成。完成后请回到这里。
- **SD 卡**，以及一台能读取它的电脑（如果你不想取出卡，也可以在 3DS 上使用 FTP 应用）。
- **你自己的游戏。** PokeDaisy 不附带游戏。请使用从自己拥有的卡带中提取的 ROM：`.gba`、`.gb` 或 `.gbc` 文件。

## 第 1 步：获取应用

1. 在电脑上打开 [PokeDaisy 发行版页面](https://github.com/lidor30/pokedaisy/releases)。
2. 在提供 3DS 文件的最新发行版下，下载 `pokedaisy.3dsx`。

## 第 2 步：复制到 SD 卡

1. 关闭 3DS，把它的 SD 卡插入电脑。
2. 打开卡根目录下的 `3ds` 文件夹。安装自定义固件时会创建它；如果没有，请自行创建。
3. 将 `pokedaisy.3dsx` 复制进去，使卡上出现 `sdmc:/3ds/pokedaisy.3dsx`。

`sdmc:/` 是 3DS 对 SD 卡根目录的称呼：在电脑上，`sdmc:/3ds/` 就是卡上的 `3ds` 文件夹。

## 第 3 步：添加游戏和存档

1. 在卡根目录创建一个名为 `pokedaisy` 的文件夹，并在其中创建名为 `roms` 的文件夹：`sdmc:/pokedaisy/roms/`。（如果你愿意先启动一次，PokeDaisy 首次启动时会自动创建这两个文件夹。）
2. 将游戏复制到 `roms`。请直接放在该文件夹中，不要放进各自的子文件夹，并先解压：3DS 上的 PokeDaisy 只读取未压缩的 `.gba`、`.gb` 和 `.gbc` 文件。
3. 要使用其他模拟器的存档？把它放到 `roms` 中游戏旁边，名称与游戏相同，并以 `.sav` 结尾：`Pokemon Emerald.gba` 会读取 `Pokemon Emerald.sav`。RetroArch 的存档以 `.srm` 结尾：请将其重命名为 `.sav`。同时也在电脑上保留一份存档副本，以防万一。

如果没有存档，游戏会从头开始；你在游戏中第一次保存时，PokeDaisy 会在同一文件夹中生成 `.sav`。

## 第 4 步：开启声音

只有在主机的音频固件被复制到 SD 卡后，3DS 自制程序才能播放声音，这是一次性步骤：

1. 将 SD 卡插回并打开 3DS，按住 **L**，然后按十字键的 **下** 和 **SELECT**，打开 Rosalina 菜单。
2. 选择 **Miscellaneous options**，然后选择 **Dump DSP firmware**。
3. 按 **B** 直到菜单关闭。

这会写入 `sdmc:/3ds/dspfirm.cdc`。没有它，游戏会静音运行。如果你之前为其他应用做过这一步，就无需再做。

## 第 5 步：启动 PokeDaisy

1. 打开 Homebrew Launcher。在使用 3ds.hacks.guide 设置的主机上，可从下载游玩（Download Play）应用打开它。
2. 在列表中选择 **PokeDaisy**。

游戏库会打开：触摸屏列出你的游戏，上屏显示光标所在的游戏；玩过之后，还会显示你的玩家、游玩时间和队伍。用十字键移动或点击游戏，然后按 **A** 开始游玩。

![没有游戏时游戏库的首次启动：上屏提示将游戏放入 sdmc:/pokedaisy/roms/](shots/3ds-library-empty.png "还没有游戏：游戏库会告诉你该放在哪里")

如果游戏库显示 **No games yet**，请检查游戏是否直接位于 `sdmc:/pokedaisy/roms/` 中，并以 `.gba`、`.gb` 或 `.gbc` 结尾。

## 熟悉操作

| 按键 | 作用 |
| --- | --- |
| **A** | 游玩光标所在的游戏 |
| **L** / **R** | 切换游戏库视图：`ALL`、`RECENT`、`GBA`、`GB`、`HACKS` |
| **SELECT** | 下一页：游戏、`SAVE EDITOR`、`TRADE SAVES`、`SETTINGS` |
| **START** | 关闭 PokeDaisy 并返回 Homebrew Launcher |

游戏中，GBA 的按键对应 3DS 本身的按键（摇杆可用作十字键），触摸屏则是助手。还有几项：

| 按键 | 作用 |
| --- | --- |
| **Y** | 快捷菜单：电脑、`FLY`、自行车、即时存档、快进等 |
| **X** | 在场景中，把按键交给助手（再次按 **X** 交还给游戏） |
| **ZR** | 按住时快进（New 3DS） |
| **ZL** | 助手的上一个标签页（New 3DS） |

要退出游戏，请使用助手 `SETTINGS` 底部的 `CLOSE GAME`。按 **HOME** 键或合上主机也可以。

![3DS 上游戏库的 SETTINGS 页面：VIEW、SORT、THEME、DARK MODE、SCREEN、ASPECT 以及声音](shots/3ds-settings.png "游戏库的 SETTINGS：适用于所有游戏的外观和屏幕设置")

## 哪些游戏可以玩

任何 Game Boy、Game Boy Color 或 Game Boy Advance 游戏都可以运行。助手可以读取 PokeDaisy 在 Android 上支持的游戏：FireRed、LeafGreen、Ruby、Sapphire、Emerald、Red、Blue、Yellow、Gold、Silver 和 Crystal，以及许多 ROM 改版（完整列表见[支持的游戏](supported-games.md)）。3DS 版本无法运行 DS 游戏。

## SD 卡上的文件位置

| 路径 | 内容 |
| --- | --- |
| `sdmc:/3ds/pokedaisy.3dsx` | 应用 |
| `sdmc:/3ds/dspfirm.cdc` | 音频固件（第 4 步） |
| `sdmc:/pokedaisy/roms/` | 你的游戏，以及每个游戏旁边的 `.sav` |
| `sdmc:/pokedaisy/roms/pokedaisy-backups/` | 每次启动前各存档的副本，每个游戏保留最新的 10 份 |
| `sdmc:/pokedaisy/states/` | 即时存档 |
| `sdmc:/pokedaisy/cheats/` | 金手指，每个游戏一个文件 |
| `sdmc:/pokedaisy/settings.ini` | 你的设置 |
| `sdmc:/pokedaisy/log.txt` | 上次运行的记录：报告问题时请附上它 |

## 更新

下载较新的 `pokedaisy.3dsx`，并覆盖 `sdmc:/3ds/` 中的旧文件。你的游戏、存档、即时存档和设置都位于 `sdmc:/pokedaisy/`，会保持原样。

## 遇到问题时

- **没有声音：** 缺少 `sdmc:/3ds/dspfirm.cdc`。请执行第 4 步。
- **Homebrew Launcher 中没有 PokeDaisy：** 文件必须位于 `sdmc:/3ds/` 中，并以 `.3dsx` 结尾。
- **游戏没有列出：** 它必须直接放在 `sdmc:/pokedaisy/roms/` 中，不能放在其中的子文件夹里，也不能是压缩文件。
- **我的存档没有加载：** 存档需要使用与游戏完全相同的名称，并以 `.sav` 结尾。每次启动都会先把存档复制到 `sdmc:/pokedaisy/roms/pokedaisy-backups/`，因此需要时可以在那里找到较旧的副本。
- **运行缓慢：** 初代 3DS 或 2DS 比 New 型号慢。在游戏库的 `SETTINGS` 中，将 `SCREEN` 设为 `PIXEL 1x`，这是最轻量的模式。

仍然无法解决？[告诉我们](https://pokedaisy.com/feedback/#bug)，并附上 `sdmc:/pokedaisy/log.txt`。
