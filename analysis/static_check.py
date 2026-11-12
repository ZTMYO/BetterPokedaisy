# -*- coding: utf-8 -*-
"""静态校验：在汉化版 ROM 中解析英文版 NativeConfig 记录的 ROM 表地址。
若物品表（ID 连续 + 描述指针落在 ROM 内）、精灵图标表、道具图标表均能解析，
说明汉化 ROM 的数据/代码布局与英文版一致（纯翻译未移动表），
其 RAM 全局地址随之与英文版配置兼容。"""
import struct, sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
ROMS = ROOT / "roms"

BASE = 0x08000000
def off(a): return a - BASE
def u16(b, o): return struct.unpack_from("<H", b, off(o))[0]
def u32(b, o): return struct.unpack_from("<I", b, off(o))[0]

def find_rom(sub):
    hits = list(ROMS.glob(sub))
    return hits[0] if hits else None

# (key, rom glob, item table base/stride/descOff/idOff/idCount, icon table, pal indices, item icon table)
GAMES = [
    ("unbound_cn", "无界*/**/*.gba", (0x08876200, 44, 0x14, 0x0E, 377),
     (0x083D37A0, "ptr"), (0x083D3E80,), (0x083D4294,)),
    ("radical_red_cn", "激进红*/**/*.gba", (0x093C0000, 44, 0x14, 0x0E, 377),
     (0x097FE6CC, "ptr"), (0x097FE164,), (0x093C8100,)),
    ("quetzal_cn", "格查尔*/**/*.gba", (0x091E0594, 0x1C, 0x0C, -1, 60),
     (0x0857BCA8, "ptr"), (0x0857C388,), (0x08614410,)),
    ("team_rocket_cn", "西班牙火箭队*/**/*.gba", (0x08C833B0, 44, 0x14, 0x0E, 923),
     (0x0857BCA8, "ptr"), (0x0857C388,), (0x08614410,)),
    ("elite_redux_cn", "ER*/**/*.gba", (0x08AC6B24, 40, 0x10, 4, 440),
     (0x08E31FA0, "ptr"), (0x08E37A50,), (0x08AF1FF0,)),
    ("mercury_cn", "水银*/**/*.gba", (0x083DB028, 44, 0x14, 0x0E, 377),
     (0x083D37A0, "ptr"), (0x083D3E80,), (0x083D4294,)),
]

def in_rom(ptr, size):
    return 0x08000000 <= ptr < 0x08000000 + size

def check_items(rom, size, base, stride, desc_off, id_off, count):
    ids_ok, ptr_ok, ptrs = 0, 0, set()
    n = min(count, 120)
    for i in range(n):
        e = base + i * stride
        if off(e) + stride > size: break
        if id_off >= 0 and u16(rom, e + id_off) == i:
            ids_ok += 1
        p = u32(rom, e + desc_off)
        ptrs.add(p)
        if in_rom(p, size): ptr_ok += 1
    return ids_ok, ptr_ok, n, len(ptrs)

def check_ptr_table(rom, size, base, n=24):
    ok = 0
    for i in range(n):
        o = base + i * 4
        if off(o) + 4 > size: break
        if in_rom(u32(rom, o), size): ok += 1
    return ok

def check_pal_idx(rom, size, base, n=40, maxv=16):
    ok = 0
    for i in range(n):
        o = base + i
        if off(o) >= size: break
        if rom[off(o)] < maxv: ok += 1
    return ok

def main():
    only = sys.argv[1:] or None
    for key, glob, items, icons, pals, item_icons in GAMES:
        if only and key not in only: continue
        rom_path = find_rom(glob)
        if not rom_path:
            print(f"[{key}] 未找到 ROM"); continue
        rom = rom_path.read_bytes()
        size = len(rom)
        print(f"[{key}] {rom_path.name} ({size//1024//1024}MB)")
        base, stride, desc_off, id_off, cnt = items
        ids_ok, ptr_ok, n, distinct = check_items(rom, size, base, stride, desc_off, id_off, cnt)
        id_msg = f"ID连续 {ids_ok}/{n}" if id_off >= 0 else f"无ID字段"
        print(f"  物品表 @0x{base:X}: {id_msg}, 描述指针在ROM内 {ptr_ok}/{n}, 不重复指针 {distinct}")
        for label, (tbase, kind) in [("精灵图标", icons)]:
            ok = check_ptr_table(rom, size, tbase)
            print(f"  {label}表 @0x{tbase:X}: {ok}/24 指针有效")
        for pbase in pals:
            ok = check_pal_idx(rom, size, pbase)
            print(f"  图标调色板索引 @0x{pbase:X}: {ok}/40 合法")
        for ibase in item_icons:
            ok = check_ptr_table(rom, size, ibase)
            print(f"  道具图标表 @0x{ibase:X}: {ok}/24 指针有效")

if __name__ == "__main__":
    main()
