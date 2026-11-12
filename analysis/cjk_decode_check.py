#!/usr/bin/env python3
"""Mirror Gen3Text.decode's CJK branch in Python and dump item descriptions
from a Chinese ROM, to validate CjkTables + the greedy decoder off-device.

Usage: python3 analysis/cjk_decode_check.py <rom.gba> [--items-base 0x08AC4B80] [--count 40]
"""
import re, sys

KT = "app/src/main/kotlin/com/pokedaisy/app/companion/data/CjkTables.kt"

def load_pairs():
    src = open(KT, encoding="utf-8").read()
    body = src.split("ELITE_REDUX_CN = intArrayOf(", 1)[1].split(")", 1)[0]
    vals = [int(x, 16) for x in re.findall(r"0x([0-9A-Fa-f]+)", body)]
    pairs = sorted((v >> 16, v & 0xFFFF) for v in vals)  # (code, unicode)
    return pairs

PAIRS = load_pairs()
CODES = [c for c, _ in PAIRS]
import bisect
def cjk_char(code):
    i = bisect.bisect_left(CODES, code)
    if i < len(CODES) and CODES[i] == code:
        return PAIRS[i][1]
    return None

# Mirror Gen3Text.table (western fallback)
TABLE = [0xFFFD] * 256
def setr(b, s):
    for i, ch in enumerate(s):
        TABLE[b + i] = ord(ch)
TABLE[0x00] = ord(' '); TABLE[0x1B] = ord('é')
for i in range(10): TABLE[0xA1 + i] = ord('0') + i
for b, ch in {0xAB:'!',0xAC:'?',0xAD:'.',0xAE:'-',0xB0:'…',0xB1:'“',0xB2:'”',0xB3:'‘',
              0xB4:'’',0xB5:'♂',0xB6:'♀',0xB8:',',0xBA:'/',0xF0:':',0x2D:'&',0x2E:'+',
              0x5B:'%',0x5C:'(',0x5D:')',0x2A:0x00B0,0x35:'=',0x36:';',0x2B:0x00AA,
              0x51:0x00BF,0x52:0x00A1,0x5A:'Í',0x68:'â',0x6F:'í',0x85:'<',0x86:'>',
              0xAF:0x00B7,0xB9:0x00D7}.items(): TABLE[b] = ord(ch) if isinstance(ch, str) else ch
setr(0xBB, "ABCDEFGHIJKLMNOPQRSTUVWXYZ"); setr(0xD5, "abcdefghijklmnopqrstuvwxyz")
for i, ch in enumerate("ÀÁÂÇÈÉÊËÌ ÎÏÒÓÔŒÙÚÛÑßàá çèéêëì îïòóôœùúûñ"):
    if ch != ' ': TABLE[0x01 + i] = ord(ch)
for i, ch in enumerate("ÄÖÜäöü"): TABLE[0xF1 + i] = ord(ch)

FC_ARGS = [0,1,1,1,3,1,1,0,1,0,0,2,1,1,1,0,2,1,1,1,1,0,0,0,0]

def decode(data, off, maxn=200):
    out = []
    end = min(len(data), off + maxn)
    i = off
    while i < end:
        c = data[i]
        if c == 0xFF: break
        if c == 0xFC and i + 1 < end:
            i += 2 + (FC_ARGS[data[i+1]] if data[i+1] < len(FC_ARGS) else 0); continue
        if c == 0xFD: i += 2; continue
        if c in (0xFE, 0xFA, 0xFB): out.append(' '); i += 1; continue
        if i + 1 < end:
            ch = cjk_char((c << 8) | data[i+1])
            if ch is not None:
                out.append(chr(ch)); i += 2; continue
        ch = cjk_char(c)
        out.append(chr(ch) if ch is not None else chr(TABLE[c]))
        i += 1
    return "".join(out)

def main():
    rom_path = sys.argv[1]
    items_base = 0x08AC4B80
    count = 60
    if "--items-base" in sys.argv: items_base = int(sys.argv[sys.argv.index("--items-base")+1], 16)
    if "--count" in sys.argv: count = int(sys.argv[sys.argv.index("--count")+1])
    rom = open(rom_path, "rb").read()
    items_base -= 0x08000000  # ROM pointer -> file offset
    def ptr_off(p):
        return p - 0x08000000 if 0x08000000 <= p < 0x0A000000 else None
    n2 = sum(1 for c in CODES if c >= 0x100)
    print(f"charmap: {len(PAIRS)} entries, {n2} two-byte, {len(PAIRS)-n2} single-byte")
    ok = bad = 0
    for idx in range(1, count + 1):
        ent = items_base + idx * 40
        desc = int.from_bytes(rom[ent + 0x10:ent + 0x14], "little")
        o = ptr_off(desc)
        if o is None or o >= len(rom):
            print(f"[{idx:3}] desc ptr {desc:#010x} invalid"); bad += 1; continue
        text = decode(rom, o)
        # rough quality: count CJK chars
        cjk_n = sum(1 for ch in text if '一' <= ch <= '鿿' or 0xFF00 <= ord(ch) <= 0xFFEF)
        flag = "OK " if cjk_n >= 2 else "???"
        if cjk_n >= 2: ok += 1
        else: bad += 1
        print(f"[{idx:3}] {flag} {text[:60]}")
    print(f"\nOK {ok} / suspicious {bad} (of {count})")

if __name__ == "__main__":
    main()
