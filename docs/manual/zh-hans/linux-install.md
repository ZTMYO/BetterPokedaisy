---
title: 在 Linux、Steam Deck 和掌机上安装
summary: 选择正确的下载文件，在 Steam Deck、Linux 电脑或 Linux 掌机（或 RetroArch 中的核心）上安装 PokeDaisy，添加游戏并迁移存档。
section: start
order: 85
source_sha256: 4846702e1b5ad2c287c910679202292d4a91749006dad8c49da96fae77340f42
keywords: [linux 安装, linux, steam deck, steamos, 游戏模式, 非 steam 游戏, trimui, brick, smart pro, crossmix, nextui, nxredux, portmaster, rocknix, knulli, muos, plorpos, spruceos, retroarch, libretro, 核心, 解压, glibc, sdl2, chmod, 权限不足]
---

PokeDaisy 也能在 Linux 上运行：在 Steam Deck、Linux 电脑以及 TrimUI 等 Linux 掌机上，既可以作为独立程序运行，也可以作为 libretro 核心加载到 RetroArch 或掌机的前端中。无论哪种方式，你都会得到同样的助手、HUD、快捷菜单和 `EXPAND`。本页带你从下载开始，直到第一次进入游戏；开始游玩后，按键操作、菜单及其他内容请参阅 [Linux、Steam Deck 和掌机](linux.md)。

> [!WARNING]
> Linux 版本是测试版（beta）。目前其菜单为英文，本页提到的部分设备尚未经过测试。如果出现问题，请[告诉我们](https://pokedaisy.com/feedback/#bug)你使用的是哪个文件和哪台设备。

## 你需要什么

- **一台 Steam Deck、Linux 电脑（x86_64）或 64 位 ARM Linux 掌机。** 在电脑上：需要装有 glibc 2.35 或更新版本以及 SDL2 的 Linux（SteamOS、Ubuntu 22.04 及更新版本和大多数当前桌面系统都具备这两者）。在掌机上：需要 glibc 2.33 或更新版本，TrimUI Brick 也包含在内。
- **一种把文件复制到设备上的方法：** 设备本身，或把它的 SD 卡插入电脑。
- **你自己的游戏。** PokeDaisy 不附带游戏。请使用从自己拥有的卡带中提取的 ROM：`.gba`、`.gb`、`.gbc` 或 `.nds` 文件，且需解压。

## 下载哪个文件

打开 [PokeDaisy 发行版页面](https://github.com/lidor30/pokedaisy/releases)，在提供 Linux 文件的最新发行版下，下载适用于你的设备的文件：

| 你的设备 | 下载 |
| --- | --- |
| Steam Deck 或 Linux 电脑 | `pokedaisy-linux-x86_64.zip` |
| TrimUI 掌机（CrossMix、NextUI），作为应用 | `pokedaisy-linux-aarch64.zip` |
| 使用 PortMaster 的掌机（ROCKNIX、Knulli、muOS 等） | `pokedaisy-portmaster-aarch64.zip` |
| 运行 NXRedux 的 TrimUI Brick | `PokeDaisy-NXRedux.zip` |
| 电脑上的 RetroArch 或其他前端 | `pokedaisy-libretro-x86_64.zip` |
| ARM 掌机上的 RetroArch 或其他前端 | `pokedaisy-libretro-aarch64.zip` |

前三个包含 PokeDaisy 程序及其旁边的三个核心：一个用于 Game Boy Advance，一个用于 Game Boy 和 Game Boy Color，一个用于 DS。最后两个只有核心。然后按照本页中适用于你的设备的部分操作。

## Steam Deck 和 Linux 电脑

1. 将 `pokedaisy-linux-x86_64.zip` 解压到主文件夹中的某个位置，例如解压到一个 `PokeDaisy` 文件夹。在 Steam Deck 上，请在桌面模式下操作。你会得到一个 `pokedaisy-linux-x86_64` 文件夹，其中包含程序 `pokedaisy-linux` 和三个核心。
2. 在该文件夹中创建一个名为 `roms` 的文件夹，并将游戏复制进去。（如果你愿意先启动一次，PokeDaisy 首次启动时会自动创建它。）
3. 在 `pokedaisy-linux-x86_64` 文件夹中打开终端，并使用 `./pokedaisy-linux --data .` 启动它。

游戏列表会打开：用十字键（或方向键）选择游戏，然后按 A（键盘上为 Z）。`--data .` 告诉 PokeDaisy 把 `roms`、`saves`、`states` 和设置保存在该文件夹中；你自己创建的快捷方式应把该文件夹的完整路径传给 `--data`。

### 在游戏模式中游玩

1. 在桌面模式下打开 Steam，选择 **游戏** > **添加非 Steam 游戏到我的库**。
2. 点击 **浏览**，进入 `pokedaisy-linux-x86_64` 文件夹，选择 `pokedaisy-linux` 并添加。
3. 返回游戏模式，从游戏库中启动 PokeDaisy。

Steam 会在其自己的文件夹中启动它，因此不需要启动选项。在游戏模式下，它会填满屏幕，Deck 的按键、触摸屏和声音都照常工作。

## TrimUI 掌机

`pokedaisy-linux-aarch64.zip` 是作为 TrimUI 应用构建的 PokeDaisy，适用于 CrossMix 和 NextUI。

1. 在电脑上解压该 zip。你会得到一个 `pokedaisy-linux-aarch64` 文件夹。
2. 将 SD 卡插入电脑，把该文件夹复制到卡上的 `Apps` 文件夹中（在 CrossMix 上）。
3. 将游戏复制到其中的 `roms` 文件夹（如果不存在，请创建它）。
4. 将卡插回，从 Apps 菜单启动 PokeDaisy。

该应用把所有内容保存在自己的文件夹中，并以全屏运行。在 CrossMix 上，它会在游玩时提高 CPU 的最低频率，并在退出时恢复。

> [!NOTE]
> 该程序尚未在 TrimUI 上作为独立应用运行过（其核心已经运行过）。可能会有些不完善之处，请告诉我们实际体验。

## 使用 PortMaster 的掌机

1. 在电脑上解压 `pokedaisy-portmaster-aarch64.zip`。其中有 `PokeDaisy.sh` 和一个 `pokedaisy` 文件夹。
2. 将两者复制到 SD 卡上固件的 `ports` 文件夹中。
3. 游戏可以留在原处：游戏列表会显示 `ports/pokedaisy/roms` 以及固件自己的 `gba`、`gb`、`gbc` 和 `nds` 文件夹（还有名为 `Game Boy Advance`、`Game Boy Color`、`Game Boy` 和 `Nintendo DS` 的文件夹）。
4. 从 Ports 列表启动 PokeDaisy。

PortMaster 会把设备的按键告诉 PokeDaisy，因此无需设置即可使用。Menu 或 Guide 键会打开 PokeDaisy 的菜单，同时按 SELECT 和 START 退出，与其他移植游戏一样。它已在运行 ROCKNIX 的 GKD 350H 上玩过。

## 使用 NXRedux 的 TrimUI Brick

1. 将 `PokeDaisy-NXRedux.zip` 解压到 SD 卡根目录。它会添加 `Emus/tg5040/PDAI.pak` 和 `Roms/PokeDaisy (PDAI)` 文件夹。
2. 将游戏（`.gba`、`.gb`、`.gbc` 和 `.nds`，已解压）放入 `Roms/PokeDaisy (PDAI)`。
3. 从 NXRedux 中的 PokeDaisy 合集启动它们。

NXRedux 会通过自己的菜单管理存档、即时存档、快进、着色器和继续游戏。请为每个游戏使用不同的文件名，因为 NXRedux 仅按文件名命名存档。

## RetroArch 和其他前端

1. 解压 `pokedaisy-libretro-x86_64.zip`（电脑）或 `pokedaisy-libretro-aarch64.zip`（ARM 掌机）。其文件夹中包含三个核心、各自的许可证和 `SOURCE.txt`：
   - `pokedaisy_gba_libretro.so` 用于游玩 Game Boy Advance 游戏。
   - `pokedaisy_gbc_libretro.so` 用于游玩 Game Boy 和 Game Boy Color 游戏（也可游玩 Game Boy Advance）。
   - `pokedaisy_ds_libretro.so` 用于游玩 DS 游戏。
2. 将核心复制到前端的 cores 文件夹中。RetroArch 会在 **Settings** > **Directory** > **Cores** 中显示该位置。
3. 使用 PokeDaisy 核心加载游戏（DS 核心会显示为 PokeDaisy DS）。

你的前端会像处理任何核心一样管理存档、即时存档和快进。助手的设置保存在前端的存档文件夹中：Game Boy 和 Game Boy Advance 核心为 `pokedaisy/settings.ini`，DS 核心为 `pokedaisy-ds/settings.ini`。如果你要把核心转交给别人，请将许可证一并保留。

### 已预装：plorpOS 和 spruceOS

在 [plorpOS](https://github.com/thwonp/plorpos) 以及 [spruceOS](https://github.com/spruceUI/spruceOS) 的每夜构建中，Game Boy、Game Boy Color 和 Game Boy Advance 核心已经预装，因此无需下载：将 PokeDaisy 选为游戏的核心即可（在 plorpOS 上，位于 Game Boy 货架的 Core 行）。

## 迁移你的存档

在 PokeDaisy 程序（前三个下载文件）中，游戏的存档位于与 `roms` 同级的 `saves` 文件夹中，名称是完整的 ROM 文件名加上 `.sav`：例如 `FireRed.gba` 对应 `saves/FireRed.gba.sav`。将其他模拟器的存档用该名称复制到那里即可使用，包括 RetroArch 的 `.srm`。请先在别处保留一份旧存档的副本。

游戏打开前，PokeDaisy 会将其存档复制到 `saves/pokedaisy-backups`，并保留最新的 10 份副本，因此需要时可以在那里找到较旧的副本。其他模拟器的即时存档无法加载。

在 RetroArch、NXRedux 和其他前端中，存档是前端自己的文件，保存在它一贯使用的位置。

## 更新

下载较新的 zip，并将其解压覆盖旧文件夹（在 NXRedux 上，再次解压到 SD 卡根目录）。你的游戏、存档、即时存档和设置不在 zip 中，因此会保持原样。在前端中，将新核心复制覆盖旧核心。

## 遇到问题时

- **启动时提示 Permission denied：** 解压工具丢失了程序的运行权限。在该文件夹中运行 `chmod +x pokedaisy-linux`。
- **出现提到 `GLIBC_2.35` 或 `libSDL2` 的错误：** 你的 Linux 比 PokeDaisy 要求的版本旧，或者没有安装 SDL2。请从你的发行版安装 SDL2（在 Ubuntu 上运行 `sudo apt install libsdl2-2.0-0`），或尝试更新的系统。
- **游戏没有列出：** 它必须直接位于 `roms` 中（不能在其中的子文件夹里），已解压，并以 `.gba`、`.gb`、`.gbc` 或 `.nds` 结尾。
- **A 和 B 反了：** 打开菜单（同时按 START 和 SELECT，或按 Guide 或 Menu 键），将 `A/B BUTTONS` 设为 `SWAPPED`。
- **我的存档没有加载：** 存档需要使用 ROM 的完整文件名并加上 `.sav`，放在 `saves` 中。请检查 `saves/pokedaisy-backups` 中是否有较旧的副本。
- **手柄没有反应：** SDL 还不认识它。请将一个包含其映射的 `gamecontrollerdb.txt` 放入数据文件夹（与 `roms` 相邻）。

仍然无法解决？[告诉我们](https://pokedaisy.com/feedback/#bug)你使用的是哪台设备和哪个文件。从终端启动时，PokeDaisy 会在那里输出运行信息；TrimUI 应用会写入 `launch.log`，PortMaster 移植版会写入 `log.txt`，两者都在各自的文件夹中。
