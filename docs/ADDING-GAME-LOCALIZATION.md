# 新增美版游戏的本地化

本仓库的本地化分成两层，彼此独立：

| 层 | 代码/资产 | 作用 |
| --- | --- | --- |
| UI 文案 | `companion/i18n/Tr*.kt` | 设置、按钮、攻略、联机等应用自己的文字 |
| ROM 数据名 | `Chinese*NamesGen.kt` | 宝可梦、招式、道具、特性、性格、地名 |

新增一个美版游戏时，按这两层分别处理。UI 层是全局的，通常不用为单个游戏改；真正要按游戏做的是名字表。

## 1. 先确定 ROM 身份

需要拿到：

- `GameKind`（`EMERALD` / `FIRERED` / `LEAFGREEN` / `RUBY` / `SAPPHIRE` …）
- 文本语言：`'E'`（美版/欧版英文）
- 游戏代码：例如 `BPEE`、`BPRE`
- 完整 ROM 的 SHA-1（PokeDaisy 自己算出的、和 RetroAchievements 一致的哈希）

名字表的校验必须带英文原名，不能只按 ID。

## 2. 名字表

### 数据格式

所有名字表都是 `Array<String>`：

```text
index 0：占位，空字符串
index n："简体中文|English 原名"
```

- `index` 是游戏内部 ID（物种是内部 ID，不是全国图鉴编号；招式/道具是 ROM 表 ID）。
- 运行时只有 ROM 里的英文名和 `English 原名` 对得上才显示中文；对不上保留 ROM 原文。
- 物种表额外带多语言别名（日文、德文等），用于美版之外的 ROM。

### 数据来源

- 物种名、图鉴分类：PokeAPI `pokemon_species_names.csv`（固定提交）。
- 招式、道具、特性、性格、三代地名：PKHeX 的 `zh-Hans` + `en` 文本资源。
- 其他版本：优先用 PokeAPI / PKHeX / 官方后续作品的简中名；没有可靠中文源时保留英文，不要用机翻硬塞。

### 生成与接入

现有生成脚本：

```text
scripts/gen_zh_species.py   # 物种名 + 图鉴分类
scripts/gen_zh_names.py     # 招式/道具/特性/性格/地名
```

生成后，在显示层使用 `displaySpeciesName` / `displayMoveName` / `displayItemName` / `displayAbilityName` / `displayNatureName` / `displayLocationName`。这些函数已经做了 ID + 英文校验和回退。

## 3. 提交前检查

```bash
python scripts/check_translations.py
python scripts/gen_gamedata.py --check
python scripts/gen_nds_ports.py --check
./gradlew :app:assembleDebug
```

新增名称资产后，至少手动跑一次对应生成器的 `--check`，避免提交过期表。
