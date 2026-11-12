---
title: 背包
summary: 查看背包各口袋、道具图标和说明、收藏、招式学习器适用对象，以及从下屏使用道具。
section: companion
order: 3
source_sha256: 7ac612c9dd08c4922fe1d17d1dfbf7d5a3989d7283613abf9aaa056f611b9ec2
keywords: [道具, 物品栏, 立方包, 口袋, 招式学习器, 秘传学习器, 重复使用, 说明, 排序, 使用道具, 树果, 重要物品, 星标, 收藏]
---

背包标签页按口袋显示你当前携带的所有物品。进入长洞穴前可以快速清点数量，也可以不翻找游戏菜单就阅读道具说明。

标签名会随游戏变化：大多数游戏显示 `背包`；Red、Blue、Yellow、PureRed 和 PureBlue 显示 `道具`；Gold、Silver、Crystal、Crystal Clear 和 Prism 显示 `背包`；Unbound 显示 `立方包`；R.O.W.E. 显示 `物品栏`；Super Mariomon 显示 `口袋`。HUD 的背包使用同一名称，界面本身也依照各游戏的背包绘制。

对于已识别的无界 2.1.1.1 新汉化版本，立方包会读取游戏中的道具名称。其他版本使用原有名称来源。

## 口袋和排序

口袋像文件夹标签一样排列在左侧。`全部` 一次显示所有物品；其他标签对应游戏自己的口袋，例如道具、球、招式学习器和秘传学习器、树果、重要物品，以及改版增加的口袋。只有有物品的口袋才会显示标签。

每行显示道具名称和持有数量。招式学习器和秘传学习器在编号后显示教授的招式，与游戏中的学习器盒一样。Prism 和 Polished Crystal 的招式学习器不会用完，因此每个都显示数量 1。Polished Crystal 的药品口袋与普通道具一起显示；Dreamstone Mysteries 的药品和战斗道具口袋也一样。Elite Redux 不记录数量，拥有的道具可以反复使用，因此行中只显示名称。

口袋下方的按钮用于更改排序。每次点击依次切换：游戏原有顺序、按名称、按类型或按数量从多到少。

![背包标签页：左侧是口袋标签，右侧是道具列表](shots/companion-bag.png "背包标签页")

## 道具详情

点击道具可在屏幕底部打开说明和图标。点击其他道具可切换，点击同一道具可关闭。

![背包中选中一项道具，底部的框显示其图标和说明](shots/companion-bag-item.png "道具说明")

说明使用 ROM 中游戏自己的文字和语言，因此改版修改过的道具会显示其自己的说明。简体中文下尚未核验的道具说明保留原文并标记 `未译`；使用或给予道具后的应用提示不加此标记。长说明可在详情栏中滚动，右侧细条显示阅读位置。如果这里没有文字，表示应用未能在该 ROM 中找到说明。在 Gold、Silver、Crystal、Crystal Clear、Prism 和 Polished Crystal 中，招式学习器会像游戏自己的口袋一样显示所教授招式的说明。

## 收藏

经常使用的道具，例如常用的回复道具或球，可以加星收藏，方便查找。

打开道具说明，点击说明栏中的星形按钮（有 `使用` 按钮时位于其旁）。道具名称前会出现金星，并在背包默认顺序中移到最前。收藏道具之间仍保持背包原有顺序；无论选择哪种排序，星标都会显示。

背包中至少有一个收藏道具时，`全部` 下方会出现 `收藏` 标签，只列出收藏的物品。再次点击星形按钮可取消收藏；取消最后一个后，标签消失并返回 `全部`。

收藏按游戏分别保存：给一个 ROM 中的道具加星不会影响其他游戏。

## 招式学习器：谁可以学习

选择招式学习器或秘传学习器后，说明框还会显示全队头像，并标注每只宝可梦的状态：

- `可学习`：可以学习该招式。
- `无法学习`：不能学习。
- `已学会`：已经掌握。
- `最佳`：在能够学习的成员中最适合该招式。

头像下方会显示最适合的成员；如果它的招式已满，还会显示建议遗忘的招式。判断依据与宝可梦详情中的 `学习` 页面相同，见[队伍与详细信息](party.md)。

`最佳` 和遗忘建议受设置中的 `对战提示` 控制。关闭提示后仍可看到谁 `可学习`，但不显示推荐。

> [!NOTE]
> 招式学习器的队伍状态行目前适用于原版 FireRed、LeafGreen、Ruby、Sapphire 和 Emerald。

## 可重复使用招式学习器

通常，招式学习器教授一次招式后就会消耗。在助手 `设置` 的 `游戏玩法` 分组中开启 `可重复使用招式学习器` 后，招式学习器教授招式后会像秘传学习器一样留在背包中。该选项默认 `关闭`。

其他规则仍与游戏相同，包括哪些宝可梦可以学习、需要遗忘招式腾出位置，以及在商店出售。更改立即生效，无需重启；关闭后招式学习器恢复为一次性使用。

> [!NOTE]
> 此选项支持 PokeDaisy 支持的所有语言和发行版本的 FireRed、LeafGreen、Emerald、Ruby 和 Sapphire，以及 Emerald Kaizo、Emerald Legacy、Orange Islands、FireRed Rocket Edition、Ultra Violet 和 Celia's Stupid Romhack。在 Radical Red、Unbound 和 SoulGold 中，招式学习器原本就不会消耗，因此设置显示灰色的 `开启`。Prism 和 Polished Crystal 也会保留招式学习器，Elite Redux 则完全不记录数量。其他游戏显示 `不适用`。

## 从助手使用道具

在许多游戏中，无需打开游戏自己的背包即可使用道具或交给宝可梦携带。可以操作的行带有小球标记，列表下方也会提示。打开道具说明后，说明栏会显示可用按钮：

- 对驱避野生宝可梦的道具点击 `使用`，会像在游戏背包中选择它一样立即使用：背包数量减少一个，游戏照常按步数计时。如果上一次使用的效果仍在，会显示提示且不会消耗道具。
- 对回复道具、状态治疗道具、营养补剂或恢复 PP 的道具点击 `使用`，会要求选择宝可梦；部分恢复 PP 的道具还要求选择招式。无法受益的对象会变暗，与游戏判断“没有效果”一致。
- `给予` 将道具交给宝可梦携带。如果它已经携带其他道具，原道具会放回背包。

要取回携带道具，请在队伍标签页打开该宝可梦的详情并点击 `取下`，见[队伍与详细信息](party.md)。

只有在场景中行走时才能操作。如果打开了菜单、对话或战斗，应用会提示先关闭游戏菜单。需要游戏自身界面的道具，例如提升等级或使宝可梦进化的道具，以及信件，不在此处提供。

> [!NOTE]
> 此功能支持 PokeDaisy 支持的各语言版本的 FireRed、LeafGreen、Ruby、Sapphire 和 Emerald，但日文版 Ruby、Sapphire 和 Emerald 除外；也支持 Unbound、Radical Red、Gaia、Odyssey、Amethyst、Orange Islands、Ultra Violet、FireRed Rocket Edition、Ash Gray、Ghost Grey、Sword and Shield Ultimate Plus、GS Chronicles、Glazed、Emerald Kaizo 和 Recharged Emerald。SoulGold 可对驱避野生宝可梦的道具使用 `使用`。其他游戏请照常在游戏中使用道具，背包标签页会自动更新。

## 随时打开电脑

在 FireRed、LeafGreen、Emerald、Ruby 和 Sapphire 中，背包标签页的排序按钮上方有一个 `电脑` 按钮。小箭头表示它打开的是游戏自身界面，而不是助手页面。点击后会在当前位置打开游戏的电脑，菜单与宝可梦中心的电脑相同：取出、存放和移动宝可梦，并且除 Ruby 和 Sapphire 外还可以移动其道具。按游戏原有方式退出后返回场景。

此功能支持所有语言，以及保留了上述游戏代码的改版：Glazed、Emerald Kaizo、Emerald Legacy、Gaia、Odyssey、Orange Islands、Ultra Violet、Ghost Grey 和 CFRU 系改版（Unbound、Radical Red、Amethyst、Sword and Shield Ultimate Plus）。Heart and Soul、SoulGold、Lazarus 和 Emerald Imperium 也有 `电脑` 按钮，但背包中的 `使用`、`给予` 和 `取下` 暂未全面支持；SoulGold 的驱避道具 `使用` 除外。Game Boy 上的 Red、Blue、Yellow（各语言版本）、PureRed、PureBlue 和 Crystal 也支持；打开的是宝可梦中心的完整电脑，包括存放道具的电脑等其他选项。

DS 上支持 Platinum、HeartGold、SoulSilver 和 Black，打开的同样是宝可梦中心的完整电脑。

与 `使用` 一样，只有在场景中行走时才能打开；如果菜单、对话或战斗正在进行，应用会提示原因。
