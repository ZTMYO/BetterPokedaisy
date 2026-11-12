#!/usr/bin/env python3
"""Builds assets/fonts/FusionPixelZH.ttf, the Chinese fallback behind Pixel Operator / PixelMplusJP.

Input: fusion-pixel-12px-monospaced-zh_hans.ttf from https://github.com/TakWolf/fusion-pixel-font
(release asset fusion-pixel-font-12px-monospaced-ttf-v*.zip; SIL Open Font License 1.1,
copied beside the output as LICENSE-FusionPixel.txt).

The source draws on a 12-pixel grid of 1200 units (unitsPerEm 1200). Pixel Operator's pixel
is 100 units at unitsPerEm 1600 - so re-declaring this font's em as 1600 (outlines untouched)
puts both on the same pixel grid: the hanzi come out as tall as PixelMplusJP's kanji, crisp at
every whole GbaTextMetrics scale. Line metrics are copied from Pixel Operator so a line holding
Chinese is no taller than one without. Only GB2312 (level 1 + 2, symbols and hanzi) is kept:
every charmap of the Chinese fan translations seen so far stays inside it, and the subset is
~1 MB where the full 19k-hanzi font would be ~7 MB. Needs fonttools.

    python3 scripts/gen_zh_font.py fusion-pixel-12px-monospaced-zh_hans.ttf
"""
import sys
from pathlib import Path

from fontTools import subset
from fontTools.ttLib import TTFont

ROOT = Path(__file__).resolve().parent.parent
FONTS = ROOT / "app/src/main/assets/fonts"
FAMILY = "PokeDaisy Fusion Pixel ZH"

src, ref = TTFont(sys.argv[1]), TTFont(FONTS / "PixelOperator.ttf")
assert src["head"].unitsPerEm == 1200
src["head"].unitsPerEm = ref["head"].unitsPerEm
src["hhea"].ascent, src["hhea"].descent, src["hhea"].lineGap = ref["hhea"].ascent, ref["hhea"].descent, ref["hhea"].lineGap
o, r = src["OS/2"], ref["OS/2"]
o.sTypoAscender, o.sTypoDescender, o.sTypoLineGap = r.sTypoAscender, r.sTypoDescender, r.sTypoLineGap
o.usWinAscent, o.usWinDescent = r.usWinAscent, r.usWinDescent
# A modified copy gets its own name (OFL's Reserved Font Name clause).
for rec in src["name"].names:
    if rec.nameID in (1, 4, 16):
        rec.string = FAMILY
    elif rec.nameID == 6:
        rec.string = FAMILY.replace(" ", "") + "-Regular"

# GB2312 level 1 + 2 (hanzi and full-width symbols); chars the codec can't encode
# (like 々 U+3005, in the font but outside GB2312) are added explicitly.
keep = set()
for code in range(0x10000):
    try:
        chr(code).encode("gb2312")
        keep.add(code)
    except UnicodeEncodeError:
        pass
keep.update(range(0x3000, 0x3040))   # CJK punctuation 、。「」々〆
keep.update(range(0xFF00, 0xFFEF))   # full-width forms ０－９！？：；
keep.update(ord(c) for c in "—–…·×÷℃‰『』「」〈〉《》椪")  # 厄诡椪 (National Dex 1017)
keep = [c for c in keep if c in src.getBestCmap()]
sub = subset.Subsetter(subset.Options(name_IDs=["*"], name_languages=["*"], notdef_outline=True))
sub.populate(unicodes=keep)
sub.subset(src)
src.save(FONTS / "FusionPixelZH.ttf")
print("wrote", FONTS / "FusionPixelZH.ttf", "glyphs", len(src.getBestCmap()))
