# BetterPokedaisy

> 基于 [lidor30/PokeDaisy](https://github.com/lidor30/pokedaisy) 的个人修改分支，由
> [ZTMYO](https://github.com/ZTMYO) 维护。简体中文半成品来自
> [PKM-Bai/pokedaisy](https://github.com/PKM-Bai/pokedaisy)。
> 这是个人修改版本，不是 PokeDaisy 官方发布。

BetterPokedaisy 基于 PokeDaisy：一款面向双屏 / 单屏安卓掌机的 Game Boy、Game Boy Color、GBA 和
Nintendo DS 播放器，游戏旁边带实时伴侣：队伍、背包、地图、对战克制、图鉴和攻略。
本仓库保留原项目的核心功能，并在此基础上完成了简体中文本地化和一批界面调整。

**游戏 ROM 不包含在内。** 请使用你自己合法转储的 ROM。

## 我基于原项目做了什么

### 1. 简体中文界面

- 补齐设置、TOOLS、联机、图鉴、攻略、手册和网站文档的简体中文。
- `GAMES / TOOLS / SETTINGS` 侧栏、设置项、联机页面、攻略文本都已中文化。
- 保留上游 1.5.0.1 的韩语支持：`KO` 和 `ZH_CN` 两套语言并存，互不覆盖。
- 设置页底栏只保留原作者与 ZTMYO 署名，去掉赞助、Discord、GitHub 链接和绿色 `NEW` 徽章。

### 2. 游戏数据中文名

在原项目“从 ROM 读名字”的基础上，增加了一层按 **ID + 英文原名** 校验的中文名表：

- 宝可梦名、招式名、道具名、特性名、性格名、三代地图地名。
- 只有 ROM 里的英文名和表里的别名一致时才显示中文；改版自定义内容保持原文，不会被错翻。
- 三代丰缘地区使用“内部物种 ID → 全国图鉴编号”映射，刺尾虫、莲帽小童等名字正确显示。
- 背包、队伍、对战、PC 盒子、图鉴、攻略、地图、状态栏都已接入。

### 3. 属性与对战界面

- 属性徽章使用 PokeIdle 的 18 个属性图标 + 中文属性名，玩家侧和对手侧一致，字号加大。
- 招式分类（物理 / 特殊 / 变化）使用 PokeIdle 的分类图标。
- 效果提示统一为官方风格：`◎ 效果绝佳`、`○ 有效`、`△ 效果不好`、`× 没有效果`；
  玩家侧和对手侧使用同一套图标 + 文字，不再出现单独一个 `1x` 徽章。
- 弱点和抵抗改为圆角胶囊卡片，并使用更鲜明的属性配色。

### 4. 图鉴界面

- 图鉴分类文本中文化，例如 `MUD FISH Pokémon` → `沼鱼宝可梦`。
- 未见过 / 未捕获的宝可梦显示为黑色剪影，名字显示 `?`，且不能点进详情；已见过的保持正常图标与名字。

### 5. 伴侣显示修复

- 背包道具名走统一中文查表（例如 `POTION` → `伤药`）。
- 野生对战中“对手”的宝可梦名已中文化。
- 攻略里首领队伍的宝可梦名、招式、道具、属性都已中文化。
- 进化链、攻略进化页、Nuzlocke、HUD、PC 盒子里的名字统一走同一套显示函数。

### 6. 对战辅助

- 信息面板保留原作者的对手招式，放在弱点 / 抵抗 / 免疫一栏的下面。
- 设置里的「对手个体值」开启时，信息面板同时紧凑显示 6 项个体值；
  个体值 31 显示为金色 `V`。关闭开关则信息面板和 STATS 页都不显示。
- 招式效果旁若预计可以击倒对手，符号和「致命」文字一起变红（与「效果不好」区分）。
  「致命」按最低随机伤害估算，不计特性、道具、天气、光墙等额外修正。
- 图鉴未发现的宝可梦显示 `???`；攻略 HERE 与 HUD 的野生宝可梦列表不再隐藏未发现的名字和图标。
- 三代道具描述优先显示中文：PokeAPI 的简中说明、TM/HM 按所教招式生成；
  名称校验不通过时回退游戏原文。

### 7. 即时存档

- 点击槽位图片不再读取存档，只有槽位下方的「保存 / 读取」按钮会操作，
  避免误触读取旧存档。
- 长按已保存的槽位，图片会隐藏并显示「删除」；第一次点击变为「确认删除？」，
  第二次点击才删除，连同缩略图、撤销副本和伴侣缓存一起清理。
  点击删除按钮以外的空白处会取消并恢复图片显示。

## 构建

需要：

- JDK 17
- Android SDK 34
- Android NDK `28.2.13676358`
- CMake `3.22.1`

初始化 native 子模块：

```bash
git submodule update --init --depth 1 \
  third_party/mgba third_party/rcheevos third_party/melonds
```

构建 Debug APK：

```bash
./gradlew :app:assembleDebug
```

产物：

```text
app/build/outputs/apk/debug/app-debug.apk
```

构建 Release APK：

```bash
./gradlew :app:assembleRelease
```

产物：

```text
app/build/outputs/apk/release/app-release-unsigned.apk
```

仓库没有配置发布签名；需要本地安装时，用 `apksigner` 以自己的证书签名即可。

常用检查：

```bash
python scripts/check_translations.py
python scripts/gen_gamedata.py --check
python scripts/gen_nds_ports.py --check
```

## 数据与生成脚本

- `app/src/main/kotlin/com/pokedaisy/app/companion/i18n/`：界面翻译表。
- `ChineseSpeciesNamesGen.kt`、`ChineseSpeciesCategoriesGen.kt`：
  宝可梦名称与图鉴分类，来自 PokeAPI 固定提交。
- `ChineseMoveNamesGen.kt`、`ChineseItemNamesGen.kt`、
  `ChineseAbilityNamesGen.kt`、`ChineseLocationNamesGen.kt`：
  招式 / 道具 / 特性 / 三代地名，来自 PKHeX 的简中与英文文本资源。
- `scripts/gen_zh_species.py`、`scripts/gen_zh_names.py`：上述表的生成脚本。
- `scripts/gen_zh_item_descriptions.py` + `ChineseItemDescriptionsGen.kt`：
  三代道具描述（PokeAPI 简中道具说明；TM/HM 由所教招式生成）。
- `scripts/gen_type_icons.py` + `app/src/main/res/drawable/type_*.xml`：
  PokeIdle 类型图标转换成的 Android VectorDrawable。

## 分支

- `main`：上游 1.5.0.1 的镜像，不直接开发。
- `dev`：BetterPokedaisy 的完整开发分支，包含简体中文界面、游戏数据中文名和全部魔改。
- `pr/upstream`：从上游 `main` 拉出的干净分支，只包含通用、适合向原作者提交 PR 的改动；
  目前有「即时存档防误触/删除」和「设置开启时在对战信息面板内联显示对手个体值」。

## 来源与许可

- **原项目**：[PokeDaisy by Lidor Itzhari](https://github.com/lidor30/pokedaisy)，GNU GPL v3。
  本项目保留原项目 NOTICE 要求的
  **"Based on PokeDaisy by Lidor Itzhari - https://github.com/lidor30/pokedaisy"** 署名。
- **简体中文半成品**：[PKM-Bai/pokedaisy](https://github.com/PKM-Bai/pokedaisy)。
- **类型图标**：[PokeIdle](https://github.com/ZTMYO/PokeIdle)，作者 ZTMYO。
- **数据**：PokeAPI（物种名、图鉴分类、道具说明，BSD 3-Clause）；
  PKHeX（招式、道具、特性、性格、三代地名，GPL 3.0）。
- **运行核心**：mGBA、melonDS、rcheevos、pret 反编译工程及各自贡献者。
  完整第三方许可证见 [NOTICE](NOTICE)，应用内位于 设置 > LICENSES。

本项目是同人项目，与任天堂、Game Freak、Creatures 或宝可梦公司无关；
宝可梦及相关名称属于其各自权利人。ROM 改版属于其创作者。
