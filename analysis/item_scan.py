# -*- coding: utf-8 -*-
"""在 ROM 中扫描"顺序 ID 物品表"指纹，定位 gItems 实际地址。
vanilla/CFRU: 44 字节条目，+0x0E 处 u16 == 条目序号。
ER: 40 字节条目，+4 处 u16 == 序号。"""
import struct, sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
ROMS = ROOT / "roms"

def scan(rom, stride, id_off, need=24):
    size = len(rom)
    hits = []
    # 只在 4 对齐上扫；步进用 stride（表本身连续）
    for start in range(0, size - stride * need, 4):
        n = 0
        for i in range(need):
            o = start + i * stride + id_off
            if struct.unpack_from("<H", rom, o)[0] != i:
                break
            n += 1
        if n == need:
            hits.append(start + 0x08000000)
            # 跳过本表内部，避免每个条目重复报
    return hits

def main():
    targets = [
        ("team_rocket_cn", "西班牙火箭队*/**/*.gba", [(44, 0x0E)]),
        ("elite_redux_cn", "ER*/**/*.gba", [(40, 4), (44, 0x0E)]),
        ("mercury_cn", "水银*/**/*.gba", [(44, 0x0E), (40, 4)]),
    ]
    for key, glob, layouts in targets:
        rom = list(ROMS.glob(glob))[0].read_bytes()
        print(f"[{key}] {len(rom)//1024//1024}MB")
        for stride, id_off in layouts:
            hits = scan(rom, stride, id_off)
            print(f"  stride={stride} idOff=0x{id_off:X}: {len(hits)} 处命中", [hex(h) for h in hits[:8]])

if __name__ == "__main__":
    main()
