# -*- coding: utf-8 -*-
"""用英文版 NativeConfig 的地址验证汉化版夹具：队伍能否正常解码。
Gen 3 struct Pokemon (100B) 校验和验证 + 等级/HP 明文区 + party count。"""
import struct, sys
from pathlib import Path

# pokefirered 的 substruct 顺序表 (personality % 24)：0=growth 1=attack 2=effort 3=misc
ORDER = [
    [0,1,2,3],[0,1,3,2],[0,2,1,3],[0,3,1,2],[0,2,3,1],[0,3,2,1],
    [1,0,2,3],[1,0,3,2],[2,0,1,3],[3,0,1,2],[2,0,3,1],[3,0,2,1],
    [1,2,0,3],[1,3,0,2],[2,1,0,3],[3,1,0,2],[2,3,0,1],[3,2,0,1],
    [1,2,3,0],[1,3,2,0],[2,1,3,0],[3,1,2,0],[2,3,1,0],[3,2,1,0],
]

def u16(b, o): return struct.unpack_from("<H", b, o)[0]
def u32(b, o): return struct.unpack_from("<I", b, o)[0]

def decode_mon(ewram, addr):
    m = ewram[addr:addr + 100]
    if len(m) < 100: return None
    pid, otid = u32(m, 0), u32(m, 4)
    checksum_stored = u16(m, 0x1C)
    key = pid ^ otid
    dec = bytearray(48)
    for i in range(12):
        struct.pack_into("<I", dec, i * 4, u32(m, 0x20 + i * 4) ^ key)
    checksum_calc = sum(struct.unpack_from("<12H", dec, 0)) & 0xFFFF
    if checksum_calc != checksum_stored:
        return None
    order = ORDER[pid % 24]
    gi = order.index(0) * 12
    species, held = u16(dec, gi), u16(dec, gi + 2)
    level = m[0x54]
    hp, maxhp = u16(m, 0x56), u16(m, 0x58)
    return dict(species=species, held=held, level=level, hp=hp, maxhp=maxhp, egg=bool(m[0x13] & 4))

def main(fx_dir, party_addr=0x02024284, count_addr=0x02024029, sb1ptr_addr=0x03005008):
    fx = Path(fx_dir)
    ewram = (fx / "ewram.bin").read_bytes()
    iwram = (fx / "iwram.bin").read_bytes()
    count = ewram[count_addr - 0x02000000]
    print(f"party count @0x{count_addr:X} = {count}")
    ok = 0
    for i in range(6):
        d = decode_mon(ewram, party_addr - 0x02000000 + i * 100)
        # 全零结构会平凡地通过校验和：只有非零结果才算数
        if d is None or (d["species"] == 0 and d["level"] == 0 and d["maxhp"] == 0):
            print(f"  [{i}] 无有效数据")
        else:
            ok += 1
            print(f"  [{i}] species=0x{d['species']:04X} held=0x{d['held']:04X} Lv{d['level']} HP {d['hp']}/{d['maxhp']}{' EGG' if d['egg'] else ''}")
    # SaveBlock1 指针（IWRAM）→ EWRAM 目标合理性
    sb1 = u32(iwram, sb1ptr_addr - 0x03000000)
    in_range = 0x02000000 <= sb1 < 0x02040000
    print(f"saveBlock1Ptr @0x{sb1ptr_addr:X} = 0x{sb1:08X} {'(EWRAM范围内)' if in_range else '(异常!)'}")
    print("=> RAM 与英文版一致（至少队伍/计数/存档指针）" if (ok == count and count >= 1 and in_range) else "=> 需要逐字段重新找地址")
    return ok

if __name__ == "__main__":
    main(sys.argv[1])
