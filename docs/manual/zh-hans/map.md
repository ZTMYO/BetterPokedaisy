---
title: 地图
summary: 在游戏地区地图上查看当前位置、点击地点查看名称，并通过地点列表查找城镇或道路。
section: companion
order: 5
source_sha256: 97a9564cd19179a1e1b179b55b7e0222add1b2079439b0f3665a3e30ba394dc5
keywords: [城镇地图, 地区地图, 位置, 我在哪里, 地点, 道路, 城市, 飞翔, 七岛, 岛屿, 查找]
---

`地图` 标签页始终打开着游戏的城镇地图。无需从背包中取出地图，就能一眼看到“我在哪里”，也能通过两次点击查找攻略提到的城镇。

已识别的汉化游戏会按该游戏的文字编码读取地图名称。

## 地图上的你

地图沿用游戏自己的绘制方式：显示地区图片、游戏中对应位置的角色头像，以及围绕当前位置闪动的光标。地点名称显示在游戏原有的标签中：FireRed 及相关游戏在左上方显示深色条，Emerald 及相关游戏在右下方显示带边框的窗口。FireRed 风格的游戏还会在该位置有洞穴或其他地点时，用第二条标签显示其名称。

在室内或地下时，头像会像游戏中一样停留在你上次所在的室外位置。

![地图标签页：岛屿地图上显示玩家头像、左上角的地点名称和右上角的地点按钮](shots/companion-map.png "地图标签页")

光标会像游戏中一样在两种大小之间闪动。如果希望它静止，可在设置的 `调整` 中关闭 `地图光标闪动`。

## 点击地点查看名称

点击地图任意位置可将光标移过去并查看地点。标签显示地点名称，光标框住整个地点，因此点击一条较长道路的一端时，整条道路都会高亮。点击海面或空白陆地不会显示名称。

光标不在你的位置时会出现 `我` 按钮。点击该按钮或角色头像，即可将光标移回当前位置。

## 地点：查找城镇或道路

点击 `地点` 可查看游戏地图上的地点列表：

- 顶部的 `当前位置` 将光标移回你的位置。
- `城镇` 按游戏中的顺序排列。
- `道路` 按编号排列。
- `其他地点` 包含洞穴、森林、塔等地点，按字母顺序排列。

选择地点后，地图会将光标移到该处。当[攻略](guide.md)或游戏人物提到你尚未去过的地方时，这是一种快速查找方法。

## 多张地图

部分游戏有多张地图。例如 FireRed 和 LeafGreen 的 Sevii Islands 使用独立地图。这些游戏在 `地点` 旁边显示地区按钮，按钮名称是下一张地图：`SEVII 1-3`、`SEVII 4-5`、`SEVII 6-7`，然后回到 `KANTO`。Gold、Silver、Crystal 和 Crystal Clear 使用 `JOHTO` 与 `KANTO`；Prism 有 `NALJO`、`RIJON`、`JOHTO`、`KANTO`、`SEVII` 和 `TUNOD` 六张；Polished Crystal 使用 `JOHTO`、`KANTO` 和 `ISLANDS`（Orange Islands）。其他游戏的地图以编号区分。点击按钮可依次切换；显示的不是你所在的地图时，点击 `我` 可以返回。

Crystal Clear 允许自定义角色，Prism 可从六名角色中选择，因此地图不绘制角色头像，但光标和地点名称仍会显示你的位置。Polished Crystal 则像游戏自己的地图一样，绘制你在开始时选择的角色外观。

在 `地点` 列表中，不在第一张地图上的地点会标明所属地图。

## 飞往城镇

队伍中有宝可梦学会城镇间移动的招式时，`地点` 下方会出现蓝色按钮。点击后可以查看当前可飞往的城镇，选择目的地后，游戏会像从队伍菜单使用招式一样，以自己的动画带你前往。

游戏原有规则仍然生效：你需要允许使用该招式的徽章，还必须在室外行走，且没有打开菜单或对话。如果不满足条件，按钮会说明原因，而不会执行飞翔。

此功能支持各语言版本的 FireRed、LeafGreen、Emerald、Ruby 和 Sapphire，以及以下改版：Unbound、Radical Red、Ultra Violet、Ghost Grey、Gaia、Odyssey、Amethyst、Sword and Shield Ultimate Plus、Orange Islands、Glazed（第一个地区）、Emerald Legacy、Heart and Soul、SoulGold、Lazarus 和 Emerald Imperium。

Game Boy 平台支持各语言版本的 Red、Blue、Yellow，以及 PureRed、PureBlue 和 Crystal。列表与游戏地图提供的列表一致：Red、Blue、Yellow、PureRed 和 PureBlue 显示所有去过的城镇；Crystal 显示当前地区去过的城镇，到达 Indigo Plateau 后可使用关都的城镇列表。

DS 平台支持 Platinum、HeartGold、SoulSilver 和 Black。Black 不要求徽章：只要有宝可梦学会该招式，所有到达过的城镇都会出现在列表中。

## 哪些游戏显示地图图片

PokeDaisy 不附带游戏美术资源。首次游玩时，应用从你的 ROM 重建地图图片并保存，因此第一次可能需要片刻才能显示。

- **FireRed、LeafGreen、Emerald、Ruby 和 Sapphire** 使用各自地区的地图。
- **许多改版**在 ROM 中绘制了自己的地区，应用会显示对应地图，例如 Unbound、Radical Red、Gaia、Odyssey 等 FireRed 系改版，以及 Lazarus、SoulGold、Heart and Soul、Glazed、Quetzal 和 Wishes of Tomorrow。Quetzal Alpha 0.8.4（及 Moemon Quetzal）只有丰缘地区，显示丰缘地图。
- **保留原版地图的改版**使用 FireRed 或 Emerald 的地图。

如果某个游戏或当前区域没有地图图片，标签页会改用文字显示地点名称和你的位置。

> [!NOTE]
> 少数改版复用原版地图图片，却没有自行存储。如果它们显示文字而不是地图，可先在 PokeDaisy 中运行一次原版 FireRed 或 Emerald。应用生成的图片会与需要它的游戏共享。
