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

- 设置、TOOLS、联机、图鉴、攻略、手册和网站文档都已中文化。
- 补齐上游 1.5.1 的新界面：FLY 风格、DS 核心与触屏、挑战图标、改版提示等。
- 韩语和简体中文并存，互不覆盖。
- 设置页底栏只保留原作者和 ZTMYO 的署名，去掉赞助、Discord、GitHub 链接和 `NEW` 徽章。

### 2. 游戏数据中文名

- 宝可梦、招式、道具、特性、性格和地图地名都显示中文，背包、队伍、对战、PC 盒子、图鉴、攻略、地图和状态栏通用；进化链、Nuzlocke 和 HUD 里的名字也一样。地图地名覆盖关都、城都、丰缘和七之岛等。
- 只翻译确认过的内容：改版自定义的名字、道具和说明保持原文，不会被错翻。
- 中文汉化版 ROM 即使哈希不认识，也按零售数据结构识别；ROM 自己的文本是乱码时，用内置中文兜底。
- 全角数字和字母统一成官方写法，例如 `10号道路`、`PP单项小补剂`。
- 三代旧拼写和后世新拼写都能对应，不会因为改名退回英文。
- 图鉴说明提供 386 只中文描述；攻略页面的文本也补齐中文；改版或其他语言保持原文。

### 3. 属性与对战界面

- 属性徽章换成内置图标和中文名，双方一致，字号更大。
- 招式分类用物理 / 特殊 / 变化图标。
- 效果提示统一为「效果绝佳 / 有效 / 效果不好 / 没有效果」。
- 弱点和抵抗改成圆角卡片，配色更醒目。
- 开启「一键使用招式」时，招式卡片按游戏自己的排版显示：上排名字和效果，下排属性、威力和 PP。

### 4. 图鉴界面

- 图鉴分类中文化，例如 `MUD FISH Pokémon` → `沼鱼宝可梦`。
- 未见过或未捕获的宝可梦显示黑色剪影和 `?`，不能打开详情；已见过的正常显示。

### 5. 对战辅助

- 信息面板保留对手招式，并显示弱点 / 抵抗 / 免疫。
- 开启「对手个体值」后显示 6 项个体值，31 显示金色 `V`。
- 预计能击倒对手时，「致命」会变红；按最低随机伤害估算。
- 未发现的宝可梦在攻略 HERE 和 HUD 野生列表里显示 `???`。
- 三代道具描述优先中文；机制与后世不同的道具按三代说明，例如 `EXP. SHARE` 是携带生效，不是开关式学习装置。

### 6. 即时存档

- 只有「保存 / 读取」按钮会读取存档，点槽位图片不会误触。
- 长按已保存的槽位可以删除：第一次点「删除」变成「确认删除？」，第二次才真正删除；点空白处取消。

### 7. 成就与队伍

- 顶层设置和下屏 `SETTINGS` 都有 RetroAchievements 通知开关：成就弹窗和进度提示可分别关闭；关闭后解锁音效也会静音，成就仍然照常解锁。
- 队伍详情页的四个招式可以拖拽排序，PP 和 PP Up 会一起移动；战斗中或游戏菜单打开时不能调整，目前支持 GBA 三代。
- 获得新道具或数量增加时，游戏画面左下角会弹出小卡片，显示图标、`OBTAINED`、名字和 `×N`，随后自动淡出；使用或卖出不会提示，读档也不会误报。

伴侣里还有几处方便浏览的改动：浏览 PC 盒子时，选中右侧列表里的宝可梦后再点一次会打开它的图鉴，返回后仍停在原处；对战信息面板里我方宝可梦显示对应物种的中文名；背包按名称排序时使用中文名，数量和类型排序的同名次序也一致；能力值页在「性格」「觉醒力量」旁显示这只宝可梦的特性。

游戏进入「要忘记哪个招式」的页面时，游戏画面上方右侧会弹出「招式替换推荐」小卡片，只显示一行「忘记 X 学习 Y」，两个招式名按各自的属性颜色显示；离开游戏页面后自动消失。

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

产物在 `app/build/outputs/apk/debug/app-debug.apk`。

构建 Release APK：

```bash
./gradlew :app:assembleRelease
```

产物在 `app/build/outputs/apk/release/app-release-unsigned.apk`。
仓库没有配置发布签名；需要本地安装时，用 `apksigner` 以自己的证书签名即可。

## 分支

- `main`：上游版本镜像。
- `dev`：BetterPokedaisy 的完整开发分支。
- `pr/*`：准备提交给上游的独立改动。

## 来源与许可

- **原项目**：[PokeDaisy by Lidor Itzhari](https://github.com/lidor30/pokedaisy)，GNU GPL v3。
  本项目保留原项目 NOTICE 要求的
  **"Based on PokeDaisy by Lidor Itzhari - https://github.com/lidor30/pokedaisy"** 署名。
- **简体中文半成品**：[PKM-Bai/pokedaisy](https://github.com/PKM-Bai/pokedaisy)。
- **数据**：PokeAPI（物种名、图鉴分类、道具说明，BSD 3-Clause）；
  PKHeX（招式、道具、特性、性格、三代地名，GPL 3.0）。
- **运行核心**：mGBA、armGBA（libretro GBA 核心）、melonDS、rcheevos、pret 反编译工程及各自贡献者。
  完整第三方许可证见 [NOTICE](NOTICE)，应用内位于 设置 > LICENSES。

本项目是同人项目，与任天堂、Game Freak、Creatures 或宝可梦公司无关；
宝可梦及相关名称属于其各自权利人。ROM 改版属于其创作者。
