#!/usr/bin/env python3
"""Check Chinese guide-table coverage and placeholders.

    python scripts/check_zh_guides.py [--require-all]

Identical English keys shared by guide tables use the first available Chinese
translation, then extra guide and app tables, exactly like trGuide(). This checks the
five existing guide translation tables, not English-only game guides.
"""
from pathlib import Path
import re
import sys

import check_translations as app

GUIDES = app.I18N / 'guide'
ORDER = ('FireRed', 'Emerald', 'RubySapphire', 'Unbound', 'Quetzal')
START = re.compile(r'^\s*' + app.STR + r'\s+to\s+Tr\(')
ZH = re.compile(r',\s*zh\s*=\s*' + app.STR + r'\s*\),\s*$')
EXTRA = re.compile(r'^\s*' + app.STR + r'\s+to\s+' + app.STR + r'\s*,\s*$')


def main():
    if hasattr(sys.stdout, 'reconfigure'):
        sys.stdout.reconfigure(errors='backslashreplace')
    texts = {}
    tables = {}
    errors = []
    for area in ORDER:
        path = GUIDES / f'GuideText{area}.kt'
        keys = []
        for number, line in enumerate(path.read_text(encoding='utf-8').splitlines(), 1):
            match = START.match(line)
            if not match:
                continue
            key = match.group(1)
            keys.append(key)
            found = ZH.search(line)
            if found:
                value = found.group(1)
                if not value.strip() or sorted(app.PH.findall(key)) != sorted(app.PH.findall(value)):
                    errors.append(f'{path.name}:{number}: empty Chinese or different placeholders: {key!r}')
                texts.setdefault(key, value)
        tables[area] = keys
    extras = app.I18N / 'GuideZhExtra.kt'
    seen = set()
    for number, line in enumerate(extras.read_text(encoding='utf-8').splitlines(), 1):
        found = EXTRA.match(line)
        if not found:
            continue
        key, value = found.groups()
        if key in seen or not value.strip() or sorted(app.PH.findall(key)) != sorted(app.PH.findall(value)):
            errors.append(f'{extras.name}:{number}: duplicate key, empty Chinese or different placeholders: {key!r}')
        seen.add(key)
        texts.setdefault(key, value)
    app_tables, _ = app.tables()
    for key, (_, values) in app_tables.items():
        if values[-1] is not None:
            texts.setdefault(key, values[-1])
    for area, keys in tables.items():
        missing = [key for key in keys if key not in texts]
        print(f'{area}: {len(keys)-len(missing)}/{len(keys)} translated')
        if '--require-all' in sys.argv:
            errors.extend(f'{area}: missing Chinese {key!r}' for key in missing)
    for error in errors:
        print(error)
    print(f'{len(errors)} problems')
    return bool(errors)


if __name__ == '__main__':
    raise SystemExit(main())
