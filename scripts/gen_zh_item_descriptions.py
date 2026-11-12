#!/usr/bin/env python3
"""Generate Chinese item descriptions for the Gen 3 bag from PokeAPI.

    python scripts/gen_zh_item_descriptions.py \
        --item-names PATH/item_names.csv \
        --item-indices PATH/item_game_indices.csv \
        --flavor PATH/item_flavor_text.csv [--check]

The input files are PokeAPI's data/v2/csv:
  item_names.csv        (item id -> language -> name)
  item_game_indices.csv (item id -> generation -> in-game index)
  item_flavor_text.csv  (item id -> version group -> language -> flavor text)

PokeAPI's Chinese (zh-Hans) flavor text starts with the 7th generation; there
is no zh-Hans row for the 3rd generation itself. The earliest zh-Hans text an
item has is used (usually still the same wording for a Gen 3 item), verified
against the English name the running ROM's table uses.

TMs and HMs have no PokeAPI item entries: their sentence names the move they
teach. A few key items and the mail have no Chinese flavor text anywhere in
PokeAPI: they get a short manual sentence, verified by their English name.
Unused item slots stay empty.

The generated table is `ChineseItemDescriptionsGen.kt`; each row is
"Chinese description\\u0001English name".
"""
import argparse
import csv
import io
import re
import unicodedata
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
ITEM_NAMES_KT = ROOT / "app/src/main/kotlin/com/pokedaisy/app/companion/data/ChineseItemNamesGen.kt"
MOVE_NAMES_KT = ROOT / "app/src/main/kotlin/com/pokedaisy/app/companion/data/ChineseMoveNamesGen.kt"
OUT = ROOT / "app/src/main/kotlin/com/pokedaisy/app/companion/data/ChineseItemDescriptionsGen.kt"

# Gen 3's TMs and HMs, in item order (pret's FOREACH_TMHM).
TM_MOVES = [
    "Focus Punch", "Dragon Claw", "Water Pulse", "Calm Mind", "Roar", "Toxic", "Hail", "Bulk Up",
    "Bullet Seed", "Hidden Power", "Sunny Day", "Taunt", "Ice Beam", "Blizzard", "Hyper Beam",
    "Light Screen", "Protect", "Rain Dance", "Giga Drain", "Safeguard", "Frustration", "Solar Beam",
    "Iron Tail", "Thunderbolt", "Thunder", "Earthquake", "Return", "Dig", "Psychic", "Shadow Ball",
    "Brick Break", "Double Team", "Reflect", "Shock Wave", "Flamethrower", "Sludge Bomb", "Sandstorm",
    "Fire Blast", "Rock Tomb", "Aerial Ace", "Torment", "Facade", "Secret Power", "Rest", "Attract",
    "Thief", "Steel Wing", "Skill Swap", "Snatch", "Overheat",
]
HM_MOVES = ["Cut", "Fly", "Surf", "Strength", "Flash", "Rock Smash", "Waterfall", "Dive"]

# PokeAPI has no zh-Hans flavor text for these Gen 3 key items / mail.
MANUAL = {
    "Orange Mail": "可以夹在信件中，让宝可梦携带。",
    "Harbor Mail": "可以夹在信件中，让宝可梦携带。",
    "Glitter Mail": "可以夹在信件中，让宝可梦携带。",
    "Mech Mail": "可以夹在信件中，让宝可梦携带。",
    "Wood Mail": "可以夹在信件中，让宝可梦携带。",
    "Wave Mail": "可以夹在信件中，让宝可梦携带。",
    "Bead Mail": "可以夹在信件中，让宝可梦携带。",
    "Shadow Mail": "可以夹在信件中，让宝可梦携带。",
    "Tropic Mail": "可以夹在信件中，让宝可梦携带。",
    "Dream Mail": "可以夹在信件中，让宝可梦携带。",
    "Fab Mail": "可以夹在信件中，让宝可梦携带。",
    "Retro Mail": "可以夹在信件中，让宝可梦携带。",
    "Devon Goods": "得文公司委托送交的重要物品。",
    "Basement Key": "可以打开新紫堇入口的钥匙。",
    "Pokéblock Case": "用来存放宝可方块。",
    "Rm. 1 Key": "可以打开海紫堇1号客房的钥匙。",
    "Rm. 2 Key": "可以打开海紫堇2号客房的钥匙。",
    "Rm. 4 Key": "可以打开海紫堇4号客房的钥匙。",
    "Rm. 6 Key": "可以打开海紫堇6号客房的钥匙。",
    "Bike Voucher": "可以兑换自行车的兑换券。",
    "Bicycle": "折叠式自行车，按 B 键可以加速。",
    "Fame Checker": "可以查看名人信息的装置。",
    "Berry Pouch": "用来存放树果的袋子。",
    "Teachy TV": "可以观看宝可梦教学节目的电视。",
    "Tri-Pass": "可以搭乘前往三座岛屿的船票。",
    "Rainbow Pass": "可以搭乘前往七之岛的船票。",
    "MysticTicket": "可以前往肚脐岩的船票。",
    "AuroraTicket": "可以前往诞生之岛的船票。",
    "Powder Jar": "用来收集树果粉末的瓶子。",
    "Ruby": "得文公司制造的红色宝石。",
    "Sapphire": "得文公司制造的蓝色宝石。",
    "Magma Emblem": "熔岩队的标志。",
    "Old Sea Map": "可以前往远洋小岛的古老海图。",
    "Stick": "大葱。携带后容易击中要害。",
    "X Defend": "战斗中使用，提高宝可梦的防御。",
    "X Special": "战斗中使用，提高宝可梦的特攻。",
}


def key_name(s):
    return "".join(c for c in unicodedata.normalize("NFD", s).upper() if c.isalnum())


def parse_generated(path):
    """Rows of a generated Array<String> by index (`"中文|English"`)."""
    rows = []
    for line in path.read_text(encoding="utf-8").splitlines():
        m = re.match(r'^\s*"((?:\\.|[^"\\])*)"', line)
        if m:
            rows.append(m.group(1))
    return rows


def csv_rows(path):
    return list(csv.DictReader(io.StringIO(Path(path).read_text(encoding="utf-8-sig"), newline="")))


def clean(text):
    text = text.replace("\u00ad", "").replace("\u200b", "")
    text = re.sub(r"[\r\n\f]+", " ", text)
    text = re.sub(r"\s+", " ", text)
    return text.replace("\u0001", " ").strip()


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--item-names", required=True, help="PokeAPI item_names.csv")
    ap.add_argument("--item-indices", required=True, help="PokeAPI item_game_indices.csv")
    ap.add_argument("--flavor", required=True, help="PokeAPI item_flavor_text.csv")
    ap.add_argument("--out", type=Path, default=OUT)
    ap.add_argument("--check", action="store_true", help="report only, write nothing")
    args = ap.parse_args()

    items = parse_generated(ITEM_NAMES_KT)
    moves = {key_name(name): zh for zh, name in
             (row.split("|", 1) for row in parse_generated(MOVE_NAMES_KT) if "|" in row)}

    gen3_index = {}
    for r in csv_rows(args.item_indices):
        if r["generation_id"] == "3":
            gen3_index[int(r["game_index"])] = int(r["item_id"])

    names = {}
    for r in csv_rows(args.item_names):
        if r["local_language_id"] == "9":
            names.setdefault(key_name(r["name"]), int(r["item_id"]))

    # The earliest zh-Hans text an item has (version group 17 onward in PokeAPI).
    flavor = {}
    for r in csv_rows(args.flavor):
        if r["language_id"] != "12":
            continue
        item = int(r["item_id"])
        version = int(r["version_group_id"])
        if item not in flavor or version < flavor[item][0]:
            flavor[item] = (version, clean(r["flavor_text"]))

    def description(item_id, english):
        m = re.fullmatch(r"TM(\d\d)", english)
        if m:
            n = int(m.group(1))
            if 1 <= n <= len(TM_MOVES):
                zh = moves.get(key_name(TM_MOVES[n - 1]))
                if zh:
                    return f"招式学习器：可让宝可梦学会「{zh}」。"
        m = re.fullmatch(r"HM(\d\d)", english)
        if m:
            n = int(m.group(1))
            if 1 <= n <= len(HM_MOVES):
                zh = moves.get(key_name(HM_MOVES[n - 1]))
                if zh:
                    return f"秘传学习器：可让宝可梦学会「{zh}」。"
        for poke_id in (gen3_index.get(item_id), names.get(key_name(english))):
            if poke_id is not None and poke_id in flavor:
                return flavor[poke_id][1]
        return MANUAL.get(english, "")

    out = [
        "package com.pokedaisy.app.companion.data",
        "",
        "// Generated by scripts/gen_zh_item_descriptions.py.",
        "// Chinese: PokeAPI's earliest zh-Hans item flavor text (PokeAPI/pokeapi, BSD 3-Clause);",
        "// TMs/HMs name the move they teach; a few key items/mail have a short manual sentence.",
        "// Each row: Chinese description\\u0001English name (the English name verifies the ROM's own).",
        "internal val chineseItemDescriptions: Array<String> = arrayOf(",
    ]
    filled = 0
    out.append('    "", // 0: none')
    for i in range(1, len(items)):
        parts = items[i].split("|", 1)
        zh, english = (parts[0], parts[1]) if len(parts) == 2 else ("", "")
        text = "" if not english or english == "???" else description(i, english)
        if text:
            filled += 1
            text = text.replace("\\", "\\\\").replace('"', '\\"').replace("$", "\\$")
            out.append(f'    "{text}\\u0001{english}", // {i}: {english}')
        else:
            out.append(f'    "", // {i}: {english or "unused"}')
    out.append(")")

    print(f"{len(items) - 1} item slots, {filled} descriptions")
    if args.check:
        return
    args.out.write_text("\n".join(out) + "\n", encoding="utf-8")
    print(f"wrote {args.out}")


if __name__ == "__main__":
    main()
