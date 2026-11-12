"""Tests for the version audit's source and sample-evidence guards."""
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch

import check_zh_game_inventory as audit


class InventoryGuardsTest(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        root = Path(self.tmp.name)
        self.poller = root / 'Poller.kt'
        self.reference = root / 'supported-games.md'
        self.poller.write_text(audit.POLLER.read_text(encoding='utf-8'), encoding='utf-8')
        self.reference.write_text(audit.REFERENCE.read_text(encoding='utf-8'), encoding='utf-8')
        self.patches = [patch.object(audit, 'POLLER', self.poller), patch.object(audit, 'REFERENCE', self.reference)]
        for item in self.patches:
            item.start()

    def tearDown(self):
        for item in reversed(self.patches):
            item.stop()
        self.tmp.cleanup()

    def test_known_sample_is_not_claimed_as_complete_verification(self):
        result = audit.inventory()
        row = next(line for line in result.splitlines() if 'TEAM_ROCKET_DRAGONSDEN_V1_5_CN_SHA1' in line)
        self.assertIn('匹配', row)
        self.assertIn('仅道具表第 13 项解码验证；其余待核验', row)

    def test_sample_hash_change_requires_new_evidence(self):
        text = self.poller.read_text(encoding='utf-8')
        old = audit.SAMPLES['UNBOUND_V2_1_1_1_CN_SHA1']
        self.poller.write_text(text.replace(old, '0' * 40), encoding='utf-8')
        with self.assertRaises(ValueError):
            audit.inventory()

    def test_missing_supported_constant_is_rejected(self):
        text = self.poller.read_text(encoding='utf-8')
        text = text.replace('val SUPPORTED_HACK_SHA1S = setOf(', 'val SUPPORTED_HACK_SHA1S = setOf(\n            MISSING_V1_SHA1,', 1)
        self.poller.write_text(text, encoding='utf-8')
        with self.assertRaises(ValueError):
            audit.inventory()

    def test_supported_games_manual_change_requires_review(self):
        self.reference.write_text(self.reference.read_text(encoding='utf-8') + '\nchanged\n', encoding='utf-8')
        with self.assertRaises(ValueError):
            audit.inventory()


if __name__ == '__main__':
    unittest.main()
