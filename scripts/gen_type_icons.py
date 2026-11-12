#!/usr/bin/env python3
"""Convert PokeIdle's SVG type symbols into Android VectorDrawables.

    python scripts/gen_type_icons.py --source /path/to/PokeIdle/src/index.html

Reads the icon-type-* <symbol> elements PokeIdle draws its move types with,
and writes app/src/main/res/drawable/type_<english>.xml. The SVGs use
currentColor; the generated drawables fill white and are tinted by Compose.

Every drawable is normalised to one square viewport and one optical size:
the source viewBoxes are tight and their aspect ratios run from 0.61 (water)
to 1.37 (dark), so drawing them as-is makes the narrow ones look small and
squeezed beside the round ones. Scaling each glyph by sqrt(width*height)
gives every icon the same visual mass, with a small uniform margin.
"""
import argparse
import math
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
OUT = ROOT / "app/src/main/res/drawable"
# The common glyph "mass" (sqrt of the viewBox area) every icon is scaled to,
# and the square viewport around it. The longest side stays inside the
# viewport; the margin left over depends on how elongated the glyph is.
TARGET_MASS = 200.0
VIEWPORT = 260.0
NAMES = {
    "一般": "normal", "格斗": "fighting", "飞行": "flying", "毒": "poison",
    "地面": "ground", "岩石": "rock", "虫": "bug", "幽灵": "ghost",
    "钢": "steel", "火": "fire", "水": "water", "草": "grass",
    "电": "electric", "超能": "psychic", "冰": "ice", "龙": "dragon",
    "恶": "dark", "妖精": "fairy",
}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--source", required=True, type=Path)
    parser.add_argument("--out", type=Path, default=OUT)
    args = parser.parse_args()
    html = args.source.read_text(encoding="utf-8")
    symbols = re.findall(r'<symbol id="icon-type-([^"]+)" viewBox="([^"]+)">(.*?)</symbol>', html, re.S)
    args.out.mkdir(parents=True, exist_ok=True)
    written = 0
    for zh, viewbox, body in symbols:
        if zh not in NAMES:
            continue
        minx, miny, width, height = (float(x) for x in viewbox.replace(",", " ").split())
        paths = []
        for tag in re.findall(r"<path\b[^>]*/>", body):
            d = re.search(r'\bd="([^"]+)"', tag)
            if not d:
                continue
            rule = re.search(r'fill-rule="([^"]+)"', tag)
            d = re.sub(r"\s+", " ", d.group(1)).strip()
            fill_type = ' android:fillType="evenOdd"' if rule and rule.group(1) == "evenodd" else ""
            paths.append(f'        <path android:fillColor="#FFFFFFFF"{fill_type} android:pathData="{d}"/>')
        for tag in re.findall(r"<circle\b[^>]*/>", body):
            cx = float(re.search(r'\bcx="([^"]+)"', tag).group(1))
            cy = float(re.search(r'\bcy="([^"]+)"', tag).group(1))
            r = float(re.search(r'\br="([^"]+)"', tag).group(1))
            d = f"M{cx-r:g},{cy:g} a{r:g},{r:g} 0 1,0 {2*r:g},0 a{r:g},{r:g} 0 1,0 {-2*r:g},0 Z"
            paths.append(f'        <path android:fillColor="#FFFFFFFF" android:pathData="{d}"/>')
        if not paths:
            continue
        # Same visual mass for every type, centred in a square viewport.
        scale = TARGET_MASS / math.sqrt(width * height)
        tx = (VIEWPORT - width * scale) / 2 - minx * scale
        ty = (VIEWPORT - height * scale) / 2 - miny * scale
        xml = [
            '<?xml version="1.0" encoding="utf-8"?>',
            '<vector xmlns:android="http://schemas.android.com/apk/res/android"',
            '    android:width="24dp" android:height="24dp"',
            f'    android:viewportWidth="{VIEWPORT:g}" android:viewportHeight="{VIEWPORT:g}">',
            f'    <group android:scaleX="{scale:.6f}" android:scaleY="{scale:.6f}"',
            f'        android:translateX="{tx:.6f}" android:translateY="{ty:.6f}">',
            *paths,
            '    </group>',
            '</vector>',
            '',
        ]
        (args.out / f"type_{NAMES[zh]}.xml").write_text("\n".join(xml), encoding="utf-8")
        written += 1
    print(f"Wrote {written} type icons to {args.out}")


if __name__ == "__main__":
    main()
