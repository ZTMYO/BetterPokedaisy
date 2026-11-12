# -*- coding: utf-8 -*-
"""ROM 摸底：头信息 + GPIO 遮蔽 SHA-1 + 与 Poller.kt 已知版本比对。"""
import hashlib, re, sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
ROM_DIR = ROOT / "roms"
POLLER = ROOT / "app/src/main/kotlin/com/pokedaisy/app/companion/data/Poller.kt"

def masked_sha1(data: bytes) -> str:
    # Poller.sha1HexChunked 的约定：0x080000C4-C9 的 RTC/GPIO 洞置零后再哈希
    b = bytearray(data)
    if len(b) > 0xC9:
        b[0xC4:0xCA] = b"\x00" * 6
    return hashlib.sha1(bytes(b)).hexdigest()

# 解析 Poller.kt 里全部 *_SHA1 常量
src = POLLER.read_text(encoding="utf-8")
known = {}
for m in re.finditer(r'const val (\w+) = "([0-9a-f]{40})"', src):
    known[m.group(2)] = m.group(1)
for m in re.finditer(r'"([0-9a-f]{40})"', src[src.index("RETAIL_SHA1S"):src.index("RETAIL_SHA1S") + 500]):
    known.setdefault(m.group(1), "RETAIL_SHA1S")

print(f"已知版本常量: {len(known)} 个\n")
rows = []
for rom in sorted(ROM_DIR.rglob("*.gba")):
    data = rom.read_bytes()
    title = data[0xA0:0xAC].decode("ascii", "replace").strip("\x00 ")
    code = data[0xAC:0xB0].decode("ascii", "replace")
    rev = data[0xBC]
    ms = masked_sha1(data)
    hit = known.get(ms, None)
    rows.append((rom, len(data), code, rev, ms, hit))
    status = f"== {hit}" if hit else "!! 未识别(新hash)"
    print(f"{rom.relative_to(ROOT)}\n  大小={len(data)/1024/1024:.1f}MB 代码={code} 修订={rev} 标题={title!r}\n  遮蔽SHA1={ms}\n  {status}\n")

sys.exit(0)
