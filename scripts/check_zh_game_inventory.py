#!/usr/bin/env python3
"""Keep the version-level Chinese game-data audit in step with Poller's supported GBA hacks.

This records the 2026-11-01 local sample audit, not ongoing sample availability or translation verification.
Run without arguments to regenerate, or use --check to compare without writing.
"""
import argparse
import hashlib
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
POLLER = ROOT / 'app/src/main/kotlin/com/pokedaisy/app/companion/data/Poller.kt'
OUTPUT = ROOT / 'docs/zh-cn-game-inventory.md'
REFERENCE = ROOT / 'docs/manual/supported-games.md'
REFERENCE_SHA256 = 'a5dd31cfa1843cd555fa29e4c11a5a3456382fbe22455850ff12f540bf135870'
# These are the hashes actually observed in the dated local sample audit.
SAMPLES = {
    'UNBOUND_V2_1_1_1_CN_SHA1': '40104331f1899b23cd5b6cee9612b775e6bb4234',
    'RADICAL_RED_V4_1_CN_SHA1': 'f1772137ce263cf4357eecfc431b888b37e34369',
    'MERCURY_V1_33_CN_SHA1': '2d0d2b269199b90564e141518ad0e44cb2ada04f',
    'QUETZAL_V9_0_ALPHA_CN_SHA1': 'a905bb8712f5d934b40aca14e29939f578402bda',
    'TEAM_ROCKET_DRAGONSDEN_V1_5_CN_SHA1': '4ea5b01ee2feda127c230a0d2f80e1aa2b3b81b0',
}


def inventory():
    reference = REFERENCE.read_text(encoding='utf-8').encode('utf-8')
    if hashlib.sha256(reference).hexdigest() != REFERENCE_SHA256:
        raise ValueError('supported games manual changed; review the other game versions')
    source = POLLER.read_text(encoding='utf-8')
    marker = 'val SUPPORTED_HACK_SHA1S = setOf('
    if source.count(marker) != 1:
        raise ValueError('cannot identify supported hack set')
    block = source.split(marker, 1)[1].split('\n        )', 1)[0]
    keys = re.findall(r'\b[A-Za-z0-9_]+_SHA1\b', block)
    constants = dict(re.findall(r'const val\s+(\w+_SHA1)\s*=\s*"([0-9a-f]{40})"', source))
    if not keys or len(set(keys)) != len(keys) or set(keys) - constants.keys() or not SAMPLES.keys() <= set(keys) or any(constants[key] != value for key, value in SAMPLES.items()):
        raise ValueError('supported hack IDs, hashes or sample references need review')
    lines = [
        '# 简体中文游戏资料版本清单', '',
        '此清单登记版本层面的样本和核验缺口，不表示游戏资料已经翻译或逐项验证。版本标识和哈希来自 `Poller.kt` 的 `SUPPORTED_HACK_SHA1S`，由 `scripts/check_zh_game_inventory.py` 检查；哈希是项目用于识别配置的 SHA-1。其他游戏范围与受支持游戏手册的固定源文件版本关联，手册变化时需重新审核。', '',
        '样本列仅记录 2026-11-01 的本地只读盘点，不记录 ROM 文件名或路径，也不表示之后的样本状态。哈希匹配只能确认样本身份，不能验证资料翻译或游戏效果。所有版本的物种、招式、道具、特性、属性、地点和描述都仍需按条目、来源和版本逐项核验。', '',
        '图鉴物种表的来源和范围见 [覆盖清单](zh-cn-coverage.md)；编号与原名称同时匹配后才会应用。尚未核验的图鉴正文、特性和图鉴招式列表保留原文并标记 `未译`。其他运行时显示路径的标记尚未全部实现。', '',
        '## GBA 改版配置', '',
        '| 代码中的版本标识 | 配置 SHA-1 | 本次样本盘点 | 资料核验 |',
        '| --- | --- | --- | --- |',
    ]
    for key in keys:
        sample = '匹配' if key in SAMPLES else '未发现匹配样本'
        verification = '仅道具表第 13 项解码验证；其余待核验' if key == 'TEAM_ROCKET_DRAGONSDEN_V1_5_CN_SHA1' else '尚未逐项核验'
        lines.append(f'| `{key}` | `{constants[key]}` | {sample} | {verification} |')
    lines += [
        '', '## 其他受支持游戏', '',
        '以下版本范围以 [支持游戏手册](manual/supported-games.md) 为准。本次 GBA 样本盘点没有提供这些版本的逐项资料验证；它们的版本识别机制并不都使用上表的改版哈希。', '',
        '| 游戏及版本范围 | 样本及资料核验 |', '| --- | --- |',
        '| FireRed、LeafGreen、Emerald、Ruby、Sapphire：手册列出的语言和修订版 | 未进行逐版本、逐条资料核验 |',
        '| Red、Blue：美国 / 欧洲版；PureRed、PureBlue 2.7.6 | 未进行逐版本、逐条资料核验 |',
        '| Yellow：美国 / 欧洲、德语、法语、意大利语、西班牙语 | 未进行逐版本、逐条资料核验 |',
        '| Gold、Silver：美国 / 欧洲版 | 未进行逐版本、逐条资料核验 |',
        '| Crystal：美国、美国 / 欧洲修订版 1、澳大利亚版 | 未进行逐版本、逐条资料核验 |',
        '| Crystal Clear 2.5.11 / 2.6.0：Standard Colors 和 Color Filter | 未进行逐版本、逐条资料核验 |',
        '| Prism 0.95 build 254（Hotfix 5） | 未进行逐版本、逐条资料核验 |',
        '| Polished Crystal 3.2.0–3.2.3：普通、faithful 及调试构建 | 未进行逐版本、逐条资料核验 |',
        '| Platinum 美国修订版 1、HeartGold / SoulSilver 美国版、Black 美国 / 欧洲版 | 未进行逐版本、逐条资料核验 |', '',
        '本次还发现 Elite Redux 2.65 beta2 中文调试样本与 `CjkTables.kt` 的字表哈希匹配；没有找到对应的运行配置哈希，不能把字表匹配当成支持或资料核验。未知 ROM、尽力匹配结果和未列出的未来版本不应继承表中版本的核验状态。', '',
        '## 更新方式', '',
        '在仓库根目录运行 `python scripts/check_zh_game_inventory.py --check` 检查版本清单。运行不带参数的命令会按当前配置重建表格；更新样本和逐项核验状态时，先记录可追溯来源、适用版本和验证证据，再修改生成脚本。此清单不包含 ROM、用户存档或个人文件路径。', '',
    ]
    return '\n'.join(lines)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--check', action='store_true')
    args = parser.parse_args()
    content = inventory()
    if args.check:
        if not OUTPUT.is_file() or OUTPUT.read_text(encoding='utf-8') != content:
            raise SystemExit('Chinese game inventory is stale')
        print('Chinese game version inventory is up to date')
    else:
        OUTPUT.write_text(content, encoding='utf-8', newline='\n')
        print('Wrote Chinese game version inventory')


if __name__ == '__main__':
    main()
