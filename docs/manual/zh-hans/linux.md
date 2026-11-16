---
title: Linux、Steam Deck 和掌机（beta）
summary: 在 Steam Deck、Linux 桌面、TrimUI 和 PortMaster 掌机上，或作为你自己前端中的 libretro 核心，下载、安装并游玩 PokeDaisy Linux 测试版。
section: start
order: 90
source_sha256: 1208056b5ca5c81a4004cb4e99be20bd46fb041e3fd577bee073a3e5a462556c
keywords: [linux, steam deck, steamos, 游戏模式, 桌面模式, 桌面, 掌机, trimui, brick, smart pro, crossmix, nextui, nxredux, portmaster, rocknix, knulli, muos, gkd, retroarch, libretro, 核心, aarch64, arm, x86_64, beta, 安装, 下载, 解压, 快捷键, 菜单, sdl]
---

适用于 Linux 的 PokeDaisy 在 Steam Deck、Linux 桌面和 Linux 掌机上，提供与 libretro 核心相同的助手、HUD、快捷菜单和 `EXPAND`，可游玩你的 Game Boy、Game Boy Color 和 Game Boy Advance 游戏。它读取你自己的 ROM 和存档，从不附带游戏。

初次使用？[在 Linux、Steam Deck 和掌机上安装](linux-install.md)提供应用支持的每种语言版本，带你一步步完成下载和安装。

> [!WARNING]
> 这是测试版（beta）。Android 应用仍是功能完整的 PokeDaisy，下文的某些设备尚未经过测试。如果出现问题，请在 [GitHub](https://github.com/lidor30/pokedaisy/issues) 上提交 issue，并说明你使用的是哪个文件和哪台设备。

## 下载哪个文件

每个发行版都包含这些文件。请选择适用于你的设备的文件。

| 你的设备 | 下载 |
| --- | --- |
| Steam Deck 或 Linux 电脑（x86_64） | `pokedaisy-linux-x86_64.zip` |
| TrimUI 掌机，作为应用 | `pokedaisy-linux-aarch64.zip` |
| 使用 PortMaster 的掌机（ROCKNIX、Knulli、muOS 等） | `pokedaisy-portmaster-aarch64.zip` |
| 运行 NXRedux 的 TrimUI Brick | `PokeDaisy-NXRedux.zip` |
| 电脑上你自己的前端或 RetroArch | `pokedaisy-libretro-x86_64.zip` |
| ARM 掌机上你自己的前端或 RetroArch | `pokedaisy-libretro-aarch64.zip` |

前三个是 PokeDaisy 程序及其旁边的三个核心。后三个只有核心，供加载 libretro 核心的前端使用。

## Steam Deck 和 Linux 电脑

`pokedaisy-linux-x86_64.zip` 可在 SteamOS 上运行，也可在装有 glibc 2.35 或更新版本和 SDL2 的 Linux 电脑上运行（SteamOS 和大多数桌面系统都具备这两者）。其 `pokedaisy-linux-x86_64` 文件夹中包含程序（`pokedaisy-linux`）、GBA 核心（`pokedaisy_gba_libretro.so`）、Game Boy 核心（`pokedaisy_gbc_libretro.so`）、DS 核心（`pokedaisy_ds_libretro.so`）以及各自的许可证。

1. 将 zip 解压到主文件夹中的某个位置。在 Steam Deck 上，请在桌面模式下操作，例如解压到主文件夹中的 `PokeDaisy` 文件夹。
2. 将游戏放入 `pokedaisy-linux-x86_64` 内的 `roms` 文件夹（它会在首次启动后出现，也可以自行创建）。
3. 在该文件夹的终端中使用 `./pokedaisy-linux --data .` 启动（不指定游戏：会显示游戏列表）。

`--data` 指定的文件夹是 PokeDaisy 保存 `roms`、`saves`、`states` 和设置的位置。不指定 `--data` 时，它会使用启动时所在的文件夹，因此快捷方式应始终传入该参数。

### 游戏模式

在桌面模式下打开 Steam，选择 游戏 > 添加非 Steam 游戏到我的库，浏览到 `pokedaisy-linux` 并添加它。Steam 会在其自己的文件夹中启动它，因此不需要启动选项。在游戏模式下，PokeDaisy 会自动填满整个屏幕，Deck 的按键、触摸屏和声音都与其他地方一样正常工作。Steam 的屏幕键盘不应在启动时打开；如果它打开了，说明你使用的是较旧的版本，请下载最新版本。

## Linux 掌机

`pokedaisy-linux-aarch64.zip` 是作为 TrimUI 应用构建的程序，面向 CrossMix 或 NextUI 等固件，但也应该能在其他地方运行。其 `pokedaisy-linux-aarch64` 文件夹包含程序、全部三个核心、`launch.sh`（固件运行的文件）、`config.json` 和 `icon.png`（菜单项的名称和图标）。在 CrossMix 上，将该文件夹复制到 SD 卡的 `Apps` 文件夹中。

在 [plorpOS](https://github.com/thwonp/plorpos) 以及 [spruceOS](https://github.com/spruceUI/spruceOS) 的每夜构建上，Game Boy Advance、Game Boy 和 Game Boy Color 核心已经预装，因此在那里不需要这些文件：将 PokeDaisy 选为游戏的核心即可。

该应用把所有内容保存在自己的文件夹中：将游戏放入其中的 `roms` 文件夹。它以全屏运行，并且在 CrossMix 上会在游玩时提高 CPU 的最低频率，退出时恢复。

> [!NOTE]
> 程序本身尚未作为独立应用运行过（其核心已经运行过）。可能会有些不完善之处，请告诉我们实际体验。

## PortMaster 版本

`pokedaisy-portmaster-aarch64.zip` 是一个尚未发布的 PortMaster 移植版：`PokeDaisy.sh` 与 `pokedaisy` 文件夹并排。将两者复制到固件的 `ports` 文件夹中，然后从 Ports 列表启动 PokeDaisy。它已在运行 ROCKNIX 的 GKD 350H 上试过。

- **你的游戏：** 游戏列表会显示 `ports/pokedaisy/roms` 以及固件自己的 `gba`、`gb`、`gbc`、`nds`、`Game Boy Advance`、`Game Boy Color`、`Game Boy` 和 `Nintendo DS` 文件夹，只要它们存在于 `ports` 旁边。
- **菜单：** 按 Menu 或 Guide 键。同时按 SELECT 和 START 会退出移植版，与任何 PortMaster 游戏一样。
- PortMaster 会把设备的按键告诉 PokeDaisy，因此无需设置即可使用。

## 使用 NXRedux 的 TrimUI Brick

`PokeDaisy-NXRedux.zip` 在运行 NXRedux 的 Brick 上，将全部三个核心整合到一个 PokeDaisy 合集中。将其解压到 SD 卡根目录。将游戏（`.gb`、`.gbc`、`.gba` 和 `.nds`，未压缩）放入 `Roms/PokeDaisy (PDAI)`；启动器位于 `Emus/tg5040/PDAI.pak`。GBA 游戏使用 GBA 核心运行，Game Boy 和 Game Boy Color 游戏使用 Game Boy 核心运行，DS 游戏使用 DS 核心运行。NXRedux 会通过自己的菜单管理存档、即时存档、快进、着色器和继续游戏。请为每个游戏使用不同的文件名，因为 NXRedux 仅按文件名命名存档。

### NXRedux 合集与 Platinum

在 Platinum（USA，修订版 1）中，L2 打开助手，按住 L2 打开快捷菜单。将 `SETTINGS` > `DS LAYOUT` 设为 `ONE SCREEN`，即可在整个屏幕上游玩，触摸屏和菜单会在游戏需要时浮现。R2 可手动切换到下屏或上屏。在 `ONE SCREEN` 或 `PICTURE IN PICTURE` 下，按住 SELECT 再按 Y 可显示或隐藏浮动的触摸屏。SELECT + 十字键移动触控笔，SELECT + A 点击。

在 DS 游戏的图鉴条目中，左、右会切换到你看过的上一条和下一条条目，到达两端后会循环。

在助手设置中开启 `BATTLE STRIP`，即可用十字键选择战斗指令。A 选择指令或招式，B 从招式列表返回。招式卡片显示游戏当前的 PP 和最大 PP。背包和队伍会在游戏自己的浮动面板中打开。双打使用游戏原生的战斗操作。

## 供自己前端使用的核心

`pokedaisy-libretro-x86_64.zip` 和 `pokedaisy-libretro-aarch64.zip` 在与 zip 同名的文件夹中包含三个核心、各自的许可证和 `SOURCE.txt`（代码来源）：

| 核心 | 可游玩 | 基于 |
| --- | --- | --- |
| `pokedaisy_gba_libretro.so` | Game Boy Advance | armGBA |
| `pokedaisy_gbc_libretro.so` | Game Boy、Game Boy Color 和 Game Boy Advance | mGBA |
| `pokedaisy_ds_libretro.so` | Nintendo DS | DSperate |

ARM 核心需要 glibc 2.33 或更新版本，TrimUI Brick 也包含在内；电脑核心需要 glibc 2.35。在 RetroArch 中，将核心复制到 RetroArch 的 cores 文件夹，并用它加载游戏：它会显示为 PokeDaisy（DS 核心显示为 PokeDaisy DS）。如果你要把核心转交给别人，请将许可证一并保留。

助手会将设置保存在前端的存档文件夹中：Game Boy 和 GBA 核心为 `pokedaisy/settings.ini`，DS 核心为 `pokedaisy-ds/settings.ini`。

### 作为启动器内的核心

在 plorpOS 上，PokeDaisy 作为启动器自带模拟器的核心运行。存档与 mGBA 核心使用的是同一个文件，即时存档、快进、倒退、着色器和退出游戏都由启动器管理。在 Game Boy 货架的 Core 行中选择 PokeDaisy。

在这种形式下，快捷菜单（Y）保留 PC、FLY、驱虫喷雾、BIKE、WILD ENCOUNTERS 和 COMPANION（在英文版 FireRed 和 Emerald 中还有 TAKE OFF），并增加一个把光标移到 HUD 上的 HUD 方块；菜单打开时游戏会暂停。X 打开助手。SETTINGS 只列出核心自身实现的功能：ASPECT、HUD、QUICK MENU、FLY STYLE、FLIGHT AREA 和 FLY ON（存在 TAKE OFF 的地方），三行按键设置以及 TAB BAR。在 QUICK MENU BUTTON、COMPANION BUTTON 和 HUD CURSOR BUTTON 上按 A 时，它们会依次在 X、Y、L2 和 R2 之间切换（光标对应的选项还有 NONE）；已被其他操作占用的按键会移给当前操作。L2 和 R2 在每个货架上都能到达该核心（对于其他 Game Boy Advance 核心，启动器仍会将其拦下）。

在 Polished Crystal 3.2.3（普通版本）中，该核心还提供 `EXPAND` 和 `ZOOM OUT`，快捷菜单会包含 PC、FLY、驱虫喷雾、BIKE 和 WILD ENCOUNTERS，每一项都通过游戏自己的菜单完成。战斗中，X 会打开助手的 BATTLE 标签页：你在那里选择的指令或招式会在游戏中执行。

在助手的 BAG 中，按左会移动到口袋标签（当前所在的口袋有轮廓；如果道具说明已打开，同一按键会先关闭说明），按上、下选择一个口袋，`A` 打开它；按右或 `B` 返回列表。

### 启动器中的画面

核心会按屏幕调整画面大小，使游戏始终以整数倍比例显示。在 Brick（1024x768）上，`EXPAND` 会以 4 倍绘制游戏，并将地图重建到屏幕边缘，`ZOOM OUT` 则为 3 倍，因此能看到更多地图。`ORIGINAL` 只显示游戏本身。助手和快捷菜单始终使用 3 倍画面。

`AUTO ZOOM IN`（设置）的工作方式与应用中相同：选择 `ZOOM OUT` 后，除普通场景以外的内容（菜单、START 菜单、战斗、文本框）都会切换到 `EXPAND` 画面，回到场景时再缩回去。建筑内部属于普通场景，因此在那里不会放大。关闭 `AUTO ZOOM IN` 后，菜单中也会保持缩小状态。

快进会像正常游玩一样重建地图，因此画面到边缘都保持清晰。设备性能跟不上时，偶尔会有一帧重复显示上一幅画面。读取即时存档后，以及整个倒退过程中，边缘之外的地图会立即出现。倒退会保持你选择的画面比例，因此 `ZOOM OUT` 在倒退时仍保持缩小状态。

## 添加游戏

程序的游戏列表会显示数据文件夹中 `roms` 文件夹内的 `.gb`、`.gbc`、`.gba` 和 `.nds` 文件（在 PortMaster 上，还会显示上文提到的固件文件夹）。用十字键和 A 选择游戏。请先解压游戏。

## 迁移已有存档

程序会将游戏的存档保存在数据文件夹的 `saves` 文件夹中，名称是完整的 ROM 文件名加上 `.sav`：例如 `FireRed.gba` 对应 `saves/FireRed.gba.sav`。这是游戏自身的电池存档，因此其他模拟器的存档在重命名后即可使用，包括 RetroArch 的 `.srm`。请先在别处保留一份旧存档的副本。

游戏打开前，PokeDaisy 会将其存档备份到 `saves/pokedaisy-backups`，并保留最新的 10 份副本。即时存档位于 `states` 中，并且属于创建它的核心：来自其他模拟器或另一个核心的 GBA 即时存档无法加载。

## 按键操作

程序使用你手柄的布局：右侧按键作为游戏的 A，底部按键作为 B，与任天堂手柄一致。如果你的设备上感觉相反，可在菜单中将 `A/B BUTTONS` 设为 `SWAPPED`。游玩过程中可以插拔手柄。

键盘操作：方向键移动，Z 为 A，X 为 B，Enter 为 START，Backspace 为 SELECT，Q 和 E 为 L 和 R，Tab 为 X，F 为 Y。按住空格快进，按住 R 倒退，F2 保存即时存档，F4 读取即时存档，Escape 打开菜单。触摸屏或鼠标可以操作助手和 HUD。

助手和快捷菜单在各处的用法都与核心中相同：X 打开[助手](companion.md)，Y 在游戏画面上打开快捷菜单（PC、FLY、驱虫喷雾、BIKE、WILD ENCOUNTERS、COMPANION），两个按键都可以在助手的 `SETTINGS` 中更改。在英文版 FireRed 和 Emerald 中，快捷菜单还有 `TAKE OFF`，可以骑着宝可梦的鸟[飞越地图](map.md#flying-over-the-map)（助手的 `MAP` 也有 `TAKE OFF` 按钮）。在那里，`FLY` 会让你飞越地图前往所选城镇，除非助手 `SETTINGS` 中的 `FLY STYLE` 设为 `CLASSIC`，即游戏自己的方式。`FLY ON` 会把鸟换成热气球、直升机或飞艇。`FLIGHT AREA` 会将飞行限制在[已经去过的地方](map.md#where-you-can-fly)（`VISITED`，默认值），或允许飞往 `ANYWHERE`。地点列表按每个存档分别保存在 `saves/pokedaisy/flight-area` 中。在英文版 FireRed、LeafGreen、Ruby、Sapphire 和 Emerald 中，快捷菜单还有 [`FLASH`](controls.md#the-quick-menu)，在队伍中有宝可梦学会该招式后可以照亮黑暗的洞穴；在不暗的地方会变灰。[HUD](hud.md) 会在场景上显示队伍和小地图，而 [EXPAND](screen.md#expand-more-of-the-map) 则是助手 `SETTINGS` 中的 `ASPECT`，`ZOOM OUT` 可以显示更多地图。快捷菜单的方块会跟随你的 HUD 外观，使用两种色调，当前选中的方块带橙色轮廓；HUD 面板带有战斗风格的阴影，每只队伍宝可梦都有生命值条和状态标签的空间。

在 DS 游戏中，DS 拥有全部面键和肩键，因此 L2 打开助手，R2 切换屏幕，游戏会在一个屏幕上开始。DS HUD 拥有与 GBA 游戏相同的 PC、BAG、WILD 和 COMPANION 按钮，并在游戏显示文字时隐藏。其 `SETTINGS` 中的 `HUD` 会打开与 GBA 游戏相同的 HUD 页面，包含 `LOOK`、`SIZE`（`SMALL`、`NORMAL` 或 `LARGE`）以及显示哪些部分；快捷菜单的 `HUD` 方块会把光标移到 HUD 上。

## 菜单

同时按 START 和 SELECT（或 Guide 或 Menu 键，或 Escape），在游戏画面上打开菜单。菜单打开时游戏会暂停。

| 行 | 作用 |
| --- | --- |
| `RESUME` | 返回游戏。 |
| `SAVE STATE`、`LOAD STATE` | 该游戏的一个即时存档。`LOAD STATE` 会先询问。此版本之前创建的 Game Boy Advance 即时存档无法加载。 |
| `HOTKEYS` | 快进、倒退和即时存档的按键（见下文）。 |
| `REWIND` | 设为 `ON` 时保留最近约 20 秒用于倒退。 |
| `FF SPEED` | 快进速度：`MAX`，或 2x 至 8x。 |
| `LCD FILTER` | 类似掌机屏幕的纹理。 |
| `A/B BUTTONS` | `NORMAL` 或 `SWAPPED`，适用于 A、B 按键相反的设备。 |
| `QUIT` | 离开游戏（从列表中选择游戏时为 `QUIT TO GAME LIST`）。会先询问。 |

### 快捷键

快捷键在按住 SELECT 时生效，因此其余时间这些按键仍属于游戏。`HOTKEYS` 可以为以下操作各分配一个按键：

- `FAST FORWARD`：按住 SELECT 和该按键时，游戏快速运行。初始为 R2。
- `REWIND`：在 `REWIND` 设为 `ON` 时，按住 SELECT 和该按键，游戏会倒退运行。初始为 L2。
- `SAVE STATE` 和 `LOAD STATE`：按住 SELECT 时各按一次。初始未分配。
- `MODIFIER`：使用快捷键时按住的按键。初始为 SELECT。按 START 或某个快捷键按键，即可改用该键，或按 SELECT 将其关闭；关闭后，每个快捷键只需按自己的按键，而该按键永远不会传给游戏。

单独短按 SELECT 后松开，按键仍会传给游戏。

选择一行，然后按下想要的按键。B 取消，SELECT 会关闭该快捷键。L1、R1、L2、R2、X、Y、左右摇杆按下和背键都可以作为快捷键；A、B、START、SELECT 和十字键仍属于游戏。已被其他快捷键使用的按键会移到新的快捷键上，而 `MODIFIER` 不能同时作为某个快捷键的按键。

DS 游戏有自己的快捷键，初始同样是 SELECT + R2 和 SELECT + L2。关闭 `MODIFIER` 后，它们会改为 R3 和 L3，因为单独按 R2 和 L2 是 DS 的屏幕和助手按键。在 Steam Deck 上，只有当 Steam 将其传递过来时，背键才能到达 PokeDaisy。

## 面向高级用户

程序会从环境变量读取一些设置：

- `POCKET_FULLSCREEN=1` 填满屏幕。
- `POCKET_WINDOW=1280x800` 以该尺寸打开窗口，用于在电脑上试玩掌机屏幕。
- `POCKET_ROM_DIRS` 向游戏列表添加文件夹，用冒号分隔。
- `POCKET_REWIND_MB` 设置倒退可使用的内存量（初始为 32）。
- `POCKET_CORE_DIR` 从另一个文件夹加载核心。

要自行构建，请参阅源代码仓库中的 `linux/README.md`。

## 尚未测试的内容

- 更多设备
- ROCKNIX 以外的 PortMaster 固件
- 在 TrimUI 掌机上用程序运行 DS 游戏
