// The manual (docs/manual) stays drawable by both readers, linked up, dash-free and in step with the app:
// every setting the app has is named somewhere in it. See docs/manual/README.md.
import { test } from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync, readdirSync, mkdtempSync, cpSync, rmSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import { createHash } from 'node:crypto';
import { docsApi, docsDir, loadDocs, loadChineseDocs } from '../src/lib/docs.js';

const dir = docsDir();
const files = readdirSync(dir).filter((f) => f.endsWith('.md'));
const text = (f) => readFileSync(join(dir, f), 'utf8');

test('every page parses, links resolve and screenshots exist', () => {
  const docs = loadDocs();
  assert.ok(docs.pages.length > 0);
  const api = docsApi(docs, 'https://pokedaisy.com/');
  assert.equal(api.site, 'https://pokedaisy.com');
  assert.match(api.rev, /^[0-9a-f]{12}$/);
  for (const p of docs.pages) {
    assert.ok(p.summary.length > 20, `${p.slug}: write a real summary`);
    assert.ok(p.blocks.length > 2, `${p.slug}: a page needs more than a line`);
  }
});

test('no em or en dashes, and no spaced hyphen used as one', () => {
  const bad = [];
  for (const f of files) {
    text(f).split('\n').forEach((line, i) => {
      const prose = line.replace(/`[^`]*`/g, '');
      if (/[–—]/.test(prose) || /\S - \S/.test(prose)) bad.push(`${f}:${i + 1}: ${line.trim()}`);
    });
  }
  assert.deepEqual(bad, [], 'rewrite these with a comma, colon, parentheses or a new sentence');
});

test('every setting the app has is mentioned in the manual', () => {
  // Settings rows' English labels, from the search-words map both Settings screens share.
  const kt = readFileSync(join(dir, '../../app/src/main/kotlin/com/pokedaisy/app/companion/ui/SettingRows.kt'), 'utf8');
  const map = kt.slice(kt.indexOf('val SETTING_KEYWORDS'));
  const labels = [...map.slice(0, map.indexOf('\n)')).matchAll(/^\s*"([^"]+)" to "/gm)].map((m) => m[1]);
  assert.ok(labels.length > 20, 'found the settings');
  const manual = files.filter((f) => f !== 'README.md').map(text).join('\n');
  const missing = labels.filter((l) => !manual.includes(`\`${l}\``));
  assert.deepEqual(missing, [], 'document these settings (write each label in backticks, as the app shows it)');
});

test('the docs never name a real Pokémon, move or item', () => {
  // A few the docs would be most tempted to use; the invented creatures stand in for them.
  const banned = /\b(charmander|pikachu|bulbasaur|squirtle|pidgey|onix|geodude|potion|rare candy|poké ?ball|poke ?ball|repel|ember|thunderbolt)\b/i;
  const bad = [];
  for (const f of files) text(f).split('\n').forEach((line, i) => { if (banned.test(line)) bad.push(`${f}:${i + 1}: ${line.trim()}`); });
  assert.deepEqual(bad, []);
});


test('Chinese manual keeps stable links, marks untranslated pages and matches the offline copy', () => {
  const english = loadDocs();
  const chinese = loadChineseDocs();
  assert.equal(chinese.pages.length, english.pages.length);
  assert.equal(chinese.pages.find((p) => p.slug === 'welcome').title, '欢迎使用 PokeDaisy');
  assert.equal(chinese.pages.find((p) => p.slug === 'install').title, '安装与初次设置');
  assert.equal(chinese.pages.find((p) => p.slug === 'guide').title, '攻略');
  assert.equal(chinese.pages.find((p) => p.slug === 'dex').title, '图鉴');
  assert.equal(chinese.pages.find((p) => p.slug === 'party').title, '队伍与详细信息');
  assert.equal(chinese.pages.find((p) => p.slug === 'map').title, '地图');
  assert.equal(chinese.pages.find((p) => p.slug === 'trainer-card').title, '训练家卡');
  assert.equal(chinese.pages.find((p) => p.slug === 'bag').title, '背包');
  assert.equal(chinese.pages.find((p) => p.slug === 'battle').title, '对战');
  assert.equal(chinese.pages.find((p) => p.slug === 'cheats').title, '金手指');
  assert.equal(chinese.pages.find((p) => p.slug === 'nuzlocke').title, 'NUZLOCKE 挑战');
  assert.equal(chinese.pages.find((p) => p.slug === 'achievements').title, 'RetroAchievements 成就');
  assert.equal(chinese.pages.find((p) => p.slug === 'fast-forward').title, '快进与游戏速度');
  assert.equal(chinese.pages.find((p) => p.slug === 'saves').title, '存档、备份和即时存档');
  assert.equal(chinese.pages.find((p) => p.slug === 'hud').title, 'HUD');
  assert.equal(chinese.pages.find((p) => p.slug === 'devices').title, '屏幕与设备');
  assert.equal(chinese.pages.find((p) => p.slug === 'linux').title, 'Linux 掌机基础版本');
  assert.equal(chinese.pages.find((p) => p.slug === 'troubleshooting').title, '故障排查与常见问题');
  assert.equal(chinese.pages.find((p) => p.slug === 'supported-games').title, '支持的游戏');
  assert.equal(chinese.pages.find((p) => p.slug === 'library').title, '游戏库');
  assert.equal(chinese.pages.find((p) => p.slug === 'screen').title, '屏幕、比例与着色器');
  assert.equal(chinese.pages.find((p) => p.slug === 'companion').title, '伴侣面板概览');
  assert.equal(chinese.pages.find((p) => p.slug === 'controls').title, '控制、快捷键和触屏');
  assert.equal(chinese.pages.find((p) => p.slug === 'settings-reference').title, '设置参考');
  assert.ok(chinese.pages.every((p) => !p.title.startsWith('未译：')));
  for (const page of chinese.pages) {
    assert.doesNotMatch(JSON.stringify(page.blocks), /\*\*/, `${page.slug}: unparsed bold markup`);
    const source = english.pages.find((p) => p.slug === page.slug);
    assert.deepEqual(page.blocks.filter((b) => b.t === 'h2' || b.t === 'h3').map((b) => b.id),
      source.blocks.filter((b) => b.t === 'h2' || b.t === 'h3').map((b) => b.id));
  }
  const offline = JSON.parse(readFileSync(join(dir, '../../app/src/main/assets/docs-zh-hans.json'), 'utf8'));
  assert.deepEqual(offline, { ...docsApi(chinese, 'https://pokedaisy.com'), locale: 'zh-Hans' });
  for (const image of chinese.pages.filter((p) => !p.title.startsWith('未译：')).flatMap((p) => p.blocks.filter((b) => b.t === 'img'))) {
    const bytes = readFileSync(join(dir, '../../app/src/main/assets/docs-zh-hans', image.src.replace(/^\/docs\//, '')));
    assert.equal(createHash('sha1').update(bytes).digest('hex').slice(0, 10), image.v);
  }
  for (const file of readdirSync(join(dir, 'zh-hans')).filter((f) => f.endsWith('.md'))) {
    assert.ok(!chinese.pages.find((p) => p.slug === file.slice(0, -3)).title.startsWith('未译：'), `${file}: source changed`);
  }
});


test('Chinese supported games table preserves hack names and version numbers', () => {
  const rows = (source) => source.split('\n').filter((line) => /^\| (?:Pokémon|Celia|Sword|Emerald|Too Many|Moemon|Super)/.test(line)).map((line) => {
    const [, name, versions] = line.split('|');
    return [name.trim(), [...versions.matchAll(/\d+(?:\.\d+)*(?:[a-z])?/gi)].map((match) => match[0])];
  });
  const english = rows(text('supported-games.md'));
  const chinese = rows(text('zh-hans/supported-games.md'));
  assert.ok(english.length > 20);
  assert.deepEqual(chinese, english);
});


test('missing or stale Chinese manual pages retain marked English text', () => {
  const copy = mkdtempSync(join(tmpdir(), 'pokedaisy-docs-'));
  try {
    cpSync(dir, copy, { recursive: true });
    rmSync(join(copy, 'zh-hans', 'welcome.md'));
    const settings = join(copy, 'zh-hans', 'settings-reference.md');
    writeFileSync(settings, readFileSync(settings, 'utf8').replace(/source_sha256: [a-f0-9]+/, 'source_sha256: stale'));
    const docs = loadChineseDocs(copy);
    for (const slug of ['welcome', 'settings-reference']) {
      const page = docs.pages.find((p) => p.slug === slug);
      assert.match(page.title, /^未译：/);
      assert.match(page.blocks[0].c[0].text, /以下保留英文原文/);
    }
  } finally {
    rmSync(copy, { recursive: true, force: true });
  }
});


test('Chinese settings reference mentions every documented setting label', () => {
  const ktDir = join(dir, '../../app/src/main/kotlin/com/pokedaisy/app/companion/i18n');
  const translated = new Map();
  for (const file of readdirSync(ktDir).filter((f) => /^Tr.*\.kt$/.test(f))) {
    for (const line of readFileSync(join(ktDir, file), 'utf8').split('\n')) {
      const match = line.match(/^\s*"([^"]+)" to Tr\(.*\bzh = "([^"]+)"/);
      if (match) translated.set(match[1], match[2]);
    }
  }
  const keys = [...text('settings-reference.md').matchAll(/^\| `([^`]+)` \|/gm)].map((m) => m[1]);
  const chinese = text('zh-hans/settings-reference.md');
  const missing = [...new Set(keys)].filter((key) => !chinese.includes(translated.get(key) ?? key));
  assert.ok(keys.length > 50);
  assert.deepEqual(missing, []);
});
