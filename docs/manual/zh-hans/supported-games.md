---
title: 支持的游戏
summary: 了解伴侣面板可读取的游戏、ROM 检查方法，以及遇到未知改版或版本时的处理方式。
section: help
order: 1
source_sha256: a5dd31cfa1843cd555fa29e4c11a5a3456382fbe22455850ff12f540bf135870
keywords: [nds, nintendo ds, platinum, heartgold, soulsilver, black, unova, 第五世代, purered, pureblue, purergb, 兼容, 支持, 改版, 版本, sha1, sha-1, 校验, 不支持, 尽力匹配, 部分支持, 请求]
---

PokeDaisy 可以运行任何 GBA、Game Boy 或 DS 游戏。“支持”指下屏的伴侣面板：要显示队伍、背包、地图和战斗，它必须准确知道你正在运行哪个游戏以及数据存储位置。未知游戏仍可在上屏正常运行；伴侣面板会显示提示，无法显示队伍。

## 支持哪些游戏

**Game Boy Advance 原版游戏：**

- Pokémon FireRed、LeafGreen、Emerald、Ruby 和 Sapphire。
- 英语（美国／欧洲）、西班牙语、德语、法语、意大利语和日语发行版的所有修订版本。
- 英语 FireRed 和 Emerald 即使来自稍作修改的合集也算支持：例如删去或填充文件末尾的空白，或修补以在烧录卡运行（改变存档代码，使其无需原始闪存芯片也能存档，经常伴随改写“内置电池电量耗尽”的提示）。对游戏本身的任何修改都会使其成为伴侣面板不认识的改版。

**Game Boy 和 Game Boy Color：**

- Pokémon Red 和 Blue：美国／欧洲发行版，以游戏自己的风格显示，即 Super Game Boy 上的颜色。
- PureRed 和 PureBlue：Red 和 Blue 的 PureRGB 改版，版本 2.7.6，以游戏自己的风格显示（默认 OG 设置的颜色），包含其自己的属性、招式、道具、地点和图鉴。原版 Red 或 Blue 的存档也可使用，但需先由游戏更新：选择 CONTINUE 并回答问题。
- Pokémon Yellow：美国／欧洲、德语、法语、意大利语和西班牙语发行版，以游戏自己的风格显示。德语、法语、意大利语和西班牙语版本显示其自身的名称和图鉴文本。这些游戏本身没有道具描述，因此仅英语版显示描述。
- Pokémon Gold 和 Silver：美国／欧洲发行版，以游戏自己的风格显示。
- Pokémon Crystal：美国发行版、美国／欧洲修订版 1 及澳大利亚发行版，以游戏自己的风格显示。
- Pokémon Crystal Clear：Crystal 的开放世界改版，版本 2.5.11 和 2.6.0，每个版本的 Standard Colors 和 Color Filter 构建版均受支持，并以游戏自己的风格显示。
- Pokémon Prism：基于 Crystal、拥有自有地区的改版，版本 0.95 build 254（Hotfix 5），以游戏自己的风格显示，并有自己的图鉴。
- Polished Crystal：版本 3.2.0 至 3.2.3，各版本的普通版和 faithful 版（及其调试版）均受支持，以游戏自己的风格显示，并有自己的图鉴。faithful 版保留原始属性和招式，伴侣面板也会按此显示。

**Nintendo DS：**

- Pokémon Platinum：美国发行版，修订版 1。
- Pokémon HeartGold：美国发行版。
- Pokémon SoulSilver：美国发行版。
- Pokémon Black：美国／欧洲发行版。

伴侣面板读取这些游戏的队伍、背包、地图、图鉴和攻略，图片、图鉴页面和地图从卡带读取。Platinum 和 Black 的战斗显示在 `对战` 标签中；HeartGold 和 SoulSilver 暂不显示。Black 的 `地图` 使用游戏自己的城镇地图：如游戏所示，已到达城镇为蓝色，其余为灰色。`队伍`、`背包` 和 `图鉴` 使用各游戏自己的配色，例如 Black 深蓝网格上的切角格位和绿色图鉴网格，以及 Platinum 石板色条纹上的蓝色格位。DS 游戏如何使用屏幕，请参见[屏幕与设备](devices.md#ds-games)。

**ROM 改版**及其受支持的版本：

| 改版 | 版本 |
| --- | --- |
| Pokémon Unbound | 2.1.1.1，及其法语翻译版 |
| Pokémon Radical Red | 4.1 |
| Pokémon Gaia | 3.2 |
| Pokémon Odyssey | 4.1.1 |
| Pokémon Heart and Soul | 2.0.6 |
| Pokémon Amethyst | 1.3.0 和 1.4.1 |
| Celia's Stupid Romhack | 1.1.4 |
| Pokémon Orange Islands | Beta 5.7，及一个较早的测试版 |
| Pokémon Ultra Violet | 1.22 |
| Pokémon FireRed Rocket Edition | 唯一发布版 |
| Pokémon Ash Gray | 4.5.3，及其 Fork Bug Fix 1.0 构建版 |
| Sword and Shield Ultimate Plus | 1.2.1.2，及其三个补丁 |
| Pokémon GS Chronicles | 2.7.6 |
| Pokémon Recharged Yellow | 1.8.0、1.9.0、1.9.4 和 1.9.7 |
| Pokémon Lazarus | 2.0 |
| Emerald Seaglass | 3.0 |
| Too Many Types 2 | 1.5.2 |
| Pokémon Glazed | 9.2.0 |
| Emerald Imperium | 1.3.1 |
| Pokémon Emerald Kaizo | 2.0 |
| Pokémon Emerald Legacy | 1.1.4 |
| Pokémon Quetzal | 英语和西班牙语 Alpha 9 v1、英语 Alpha 9 v0、西班牙语 Alpha 9 v0、英语和西班牙语 Alpha 0.8.4 |
| Moemon Quetzal | 英语和西班牙语 Alpha 0.8.4 Hotfix |
| Super Mariomon | 1.2.1 至 1.3、1.5.1 和 1.5.2 |
| Pokémon Recharged Emerald | 2.2.5 和 2.2.8，及 Rebalanced 2.2.8 |
| Pokémon Elite Redux | 2.65.3b 和 2.65.4.1b |
| Pokémon Team Rocket Edition: Dragonsden Version | 1.5 (START 菜单显示版本) |
| Pokémon Wishes of Tomorrow | 1.2.6 |
| Pokémon Dreamstone Mysteries | 唯一发布版（游戏不显示版本） |
| Emerald Rogue | 2.2.1-EX |
| Pokémon SoulGold | 1.1.4，及 1.2 的三个构建版 |
| Pokémon R.O.W.E. | 2.1.9.1 Experimental |

并非每个游戏都已有所有标签。一些改版只有攻略的实时页面，少数没有攻略；许多改版的战斗面板尚未在每种战斗中检查。逐标签的完整表格见[兼容性页面](https://pokedaisy.com/compatibility/)及 [README](https://github.com/lidor30/pokedaisy#supported-games)。

## 支持针对精确版本

PokeDaisy 通过 ROM 的 SHA-1 校验值（每个字节的指纹）识别游戏。因此“Radical Red”指 Radical Red 4.1，即该版本补丁生成的精确文件。改版较旧或较新的版本可能改变内存位置，使用错误的映射读取会显示错误名称、图像或乱码。

版本之所以重要：

- 自行打补丁时，请确保在正确的基础 ROM 上使用正确补丁（大多数改版需要干净、未修改的美版 FireRed 或 Emerald）。
- 如果改版版本比列表更新，在加入支持前可能无法识别。[尽力匹配](#best-effort-for-games-pokedaisy-doesnt-know)仍可能读取。

## 游玩前检查 ROM

网站的 [ROM 检查工具](https://pokedaisy.com/compatibility/)会告诉你文件是否受支持。选择 ROM 后，它直接在浏览器中运行与应用相同的检查。**ROM 不会离开你的设备**，不会上传任何文件。

在应用中，游戏库将伴侣面板无法读取的游戏标记为 `不支持`，只能部分读取的标记为 `部分支持`。长按游戏打开 `信息` 可查看更多内容。

## 未知游戏的尽力匹配

运行 PokeDaisy 不认识、但文件头表明其基于 FireRed、LeafGreen、Emerald、Ruby 或 Sapphire 的 ROM 时，伴侣面板仍会尝试读取。游玩期间，它自动尝试按各个已知游戏读取，并将物种、招式和道具名称、地图及队伍等结果与 ROM 对照。

后续取决于匹配程度：

- **明确匹配。** 受支持游戏的不同构建版随后可得到完整支持。与受支持游戏共享内存布局的改版可使用队伍、背包、地图和战斗。未通过检查的内容（图鉴、攻略、道具描述、图像）会关闭，宁可不显示图像，也不显示错误图像。游戏库将其标记为 `部分支持`。
- **获得队伍前已有可能的匹配。** 伴侣面板先采用最佳猜测，能读取宝可梦后再确认。
- **没有明显结果。** 伴侣面板会询问你（见下文）。

匹配结果会针对该 ROM 保存。要撤销它，在游戏库长按游戏，打开 `信息` 并点击 `清除匹配`。

## 这是哪款游戏？

当多个游戏可能匹配或没有明显匹配时，伴侣面板会用“这是哪款游戏？”取代队伍显示。可能的选项排在前面并标记为“可能”。请选择该 ROM 对应的游戏，或改版所基于的原版游戏。

- 如果不确定，可以继续游玩：队伍中有宝可梦后，PokeDaisy 会自动再次检查。
- 选择 `以上都不是` 表示 ROM 不属于这些游戏，伴侣面板随后显示 `不支持` 页面。
- 选择后，PokeDaisy 在能读取队伍时会检查选择。如果不匹配，“游戏识别错误？”提示允许你重新选择或保留。

可以随时重新打开选择器：

- 伴侣面板：`设置` > `运行模式`（位于顶部 `游戏` 分组，仅对以此方式读取的 ROM 显示）。
- 游戏库：长按游戏，打开 `信息` 并点击 `选择游戏`。
- 在匹配提示中选择“游戏识别错误？”。

## 不支持与请求支持

伴侣面板完全无法读取游戏时，除 `设置` 外的每个标签都会显示 `不支持` 页面。游戏本身仍在上屏正常运行，只是无法使用伴侣面板。

希望获得支持？点击该页面或游戏库中游戏 `信息` 里的 `请求支持`，会打开[反馈页面](https://pokedaisy.com/feedback/)的游戏请求表单，预填文件名、游戏代码、大小和 SHA-1，便于找到正确版本。无需 GitHub 账号，也不会发送 ROM 本身。

每个请求都会发布到 GitHub 的 [Game requests](https://github.com/lidor30/pokedaisy/discussions/categories/game-requests)。可以为想要的游戏投票；需求最高的游戏会帮助决定后续支持内容。

> [!TIP]
> 请求前请先用 [ROM 检查工具](https://pokedaisy.com/compatibility/)检查。如果受支持的改版只是版本不同，获取列表中的版本是最快的解决方法。

## 分享尽力匹配结果（可选）

尽力匹配找到 ROM 的匹配项后，提示可能会询问是否分享。分享有助于未来版本正式支持该 ROM，完全由你决定：只有点击 `分享` 才会发送，选择“不用了”不会发送。

提示列出了全部发送内容：ROM 的 SHA-1、大小、游戏代码和修订版本，按哪个游戏读取及匹配程度，以及应用版本。不含文件名、账号或设备详情。数据发送到 PokeDaisy 自己的数据库，无法通过应用或网站读取。如果离线，会在下次启动应用时发送。
