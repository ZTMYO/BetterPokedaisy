# -*- coding: utf-8 -*-
"""生成 mgba_dump 命令：把一只合成的妙蛙种子(Lv5)注入 gPlayerParty[0] 并置 count=1，
用于在没有存档的情况下验证英文版 RAM 地址在汉化 ROM 中是否不变。"""
import struct, tempfile
from pathlib import Path

PARTY = 0x02024284      # FireRed rev0 / RR 4.1 的 gPlayerParty
COUNT = 0x02024029      # gPlayerPartyCount
BAG = 0x0203988C        # gBagPockets 指针数组

pid, otid = 0x00000000, 0x00001234
key = pid ^ otid

# 解密态 48 字节（order 0: growth 在前）
dec = bytearray(48)
struct.pack_into("<H", dec, 0, 1)          # species = Bulbasaur
struct.pack_into("<HHHH", dec, 12, 33, 45, 0, 0)   # moves: TACKLE, GROWL
dec[20:24] = bytes([35, 40, 0, 0])         # pp
checksum = sum(struct.unpack("<24H", bytes(dec))) & 0xFFFF

words = {}
words[0] = pid
words[1] = otid
# nickname/language/otname/markings = 0
words[7] = checksum                         # 0x1C
for i in range(12):
    d = struct.unpack_from("<I", dec, i * 4)[0]
    words[8 + i] = d ^ key                  # 0x20.. 加密区
# 明文 party 数据 0x50+
words[20] = 0                               # status
words[21] = 5 | (20 << 16)                  # level=5 @0x54, hp=20 @0x56
words[22] = 20                              # maxHp=20 @0x58

lines = ["wait 150"]
for off in sorted(words):
    lines.append(f"poke32 0x{PARTY + off * 4:X} 0x{words[off]:08X}")
lines.append(f"poke8 0x{COUNT:X} 1")
lines.append("wait 10")
for i in range(5):
    lines.append(f"peek32 0x{BAG + i * 4:X}")
lines.append("shot /work/out/inject.ppm")
lines.append("dump /work/out")
out = Path(tempfile.gettempdir()) / "rrshot" / "cmds2.txt"
out.parent.mkdir(parents=True, exist_ok=True)
out.write_text("\n".join(lines) + "\n", encoding="ascii")
print(f"checksum=0x{checksum:04X} key=0x{key:08X} 命令数={len(lines)} -> {out}")
