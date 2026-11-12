#!/usr/bin/env python3
"""Builds CjkTables.kt: the Chinese fan-translations' charmaps as Kotlin IntArrays.

Input: a 汉化组's charmap.txt (PRET format, `'字' = XXXX`, XXXX a 1- or 2-byte hex code).
Output: app/src/main/kotlin/com/pokedaisy/app/companion/data/CjkTables.kt with one sorted
intArrayOf(code<<16 | unicode) per ROM plus a sha1 -> table registry Gen3Text decodes with
(greedy: a 2-byte big-endian code first, then the charmap's own 1-byte codes, then the
western table). Multi-char entries (PK, the arrow names, ...) are control glyphs, not text -
skipped. Run after adding a translation's charmap.txt:

    python3 scripts/gen_cjk_tables.py <key> <sha1> <charmap.txt> [<key> <sha1> <charmap.txt> ...]
"""
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
OUT = ROOT / "app/src/main/kotlin/com/pokedaisy/app/companion/data/CjkTables.kt"

# This Unbound build shares the Elite Redux charmap: five live party names and
# its first 33 item names have been checked against the ROM and game screen.
# The alias is tied to the complete masked ROM hash, never the shared header.
ALIASES = {"d5a391ad7527495324e9acd88535a9a5bcd1a1b3": ["40104331f1899b23cd5b6cee9612b775e6bb4234"]}

ENTRY = re.compile(r"^'(.?)'\s*=\s*([0-9A-Fa-f]{2,4})\s*(?:@.*)?$")

def parse(path):
    pairs = {}
    for line in Path(path).read_text(encoding="utf-8").splitlines():
        m = ENTRY.match(line.strip())
        if not m:
            continue
        ch, code = m.group(1), int(m.group(2), 16)
        if len(ch) != 1 or code > 0xFFFF:
            continue
        if code in pairs and pairs[code] != ch:
            print(f"  警告: 码位 0x{code:04X} 重复 ({pairs[code]} / {ch}), 取后出现的")
        pairs[code] = ch
    return sorted((code << 16) | ord(ch) for code, ch in pairs.items())

def main():
    args = sys.argv[1:]
    assert len(args) % 3 == 0 and args, "usage: gen_cjk_tables.py <key> <sha1> <charmap.txt> ..."
    tables = []
    for i in range(0, len(args), 3):
        key, sha, path = args[i], args[i + 1], args[i + 2]
        pairs = parse(path)
        one_byte = sum(1 for p in pairs if p >> 16 <= 0xFF)
        two_byte = len(pairs) - one_byte
        print(f"{key}: {len(pairs)} 条目 (1字节 {one_byte}, 2字节 {two_byte})")
        tables.append((key, sha, pairs))

    lines = [
        "package com.pokedaisy.app.companion.data",
        "",
        "/**",
        " * Chinese fan-translation charmaps (scripts/gen_cjk_tables.py from each 汉化组's charmap.txt):",
        " * sorted intArrayOf(code << 16 | unicode). Gen3Text decodes greedily - 2-byte big-endian code",
        " * first, then the charmap's own 1-byte codes, then the western table.",
        " */",
        "object CjkTables {",
    ]
    for key, sha, pairs in tables:
        # 码位 >= 0x8000 时 code<<16 超出 Int 上限，写成负的十进制（位模式相同，
        # 解码器用 ushr 16 / and 0xFFFF 取值，按码位排序的二分查找不受影响）。
        def lit(p: int) -> str:
            return f"0x{p:08X}" if p < 0x80000000 else str(p - 0x100000000)
        body = ",\n    ".join(", ".join(lit(p) for p in pairs[j:j + 8]) for j in range(0, len(pairs), 8))
        lines.append(f"    val {key.upper()} = intArrayOf(\n    {body},\n    )\n")
    lines.append("    /** The masked-ROM sha1 -> its charmap; Gen3Text.cjk at detection. */")
    lines.append("    fun forSha(sha1: String?): IntArray? = when (sha1) {")
    for key, sha, _ in tables:
        aliases = ALIASES.get(sha, [])
        for alias in aliases:
            lines.append(f'        "{sha}",')
            sha = alias
        lines.append(f'        "{sha}" -> {key.upper()}')
    lines.append("        else -> null")
    lines.append("    }")
    lines.append("}")
    OUT.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print("wrote", OUT)

if __name__ == "__main__":
    main()
