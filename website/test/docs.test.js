// The manual (docs/manual) stays drawable by both readers, linked up, dash-free and in step with the app:
// every setting the app has is named somewhere in it. See docs/manual/README.md.
import { test } from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync, readdirSync, mkdtempSync, cpSync, rmSync, writeFileSync, existsSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import { createHash } from 'node:crypto';
import { docsApi, docsDir, loadDocs, loadChineseDocs, loadTranslations } from '../src/lib/docs.js';
import { PREFIXED } from '../src/i18n/index.js';
import { staleInstallGuides } from '../scripts/install-guides.mjs';

const dir = docsDir();
const english = readdirSync(dir).filter((f) => f.endsWith('.md'));
// The translated pages (docs/manual/translations/<lang>/<slug>.md) follow the same writing rules.
const tdir = join(dir, 'translations');
const translated = existsSync(tdir)
  ? readdirSync(tdir).flatMap((l) => readdirSync(join(tdir, l)).filter((f) => f.endsWith('.md')).map((f) => `translations/${l}/${f}`))
  : [];
const files = [...english, ...translated];
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
  const manual = english.filter((f) => f !== 'README.md').map(text).join('\n');
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
  // A page 1.5.1 added or changed shows its English text, marked 未译, until its zh-hans copy is updated.
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
});


test('Chinese supported games table preserves hack names and version numbers', () => {
  const rows = (source) => source.split('\n').filter((line) => /^\| (?:Pokémon|Celia|Sword|Emerald|Too Many|Moemon|Super)/.test(line)).map((line) => {
    const [, name, versions] = line.split('|');
    return [name.trim(), [...versions.matchAll(/\d+(?:\.\d+)*(?:[a-z])?/gi)].map((match) => match[0])];
  });
  const english = new Map(rows(text('supported-games.md')));
  const chinese = rows(text('zh-hans/supported-games.md'));
  assert.ok(english.size > 20);
  // New rows arrive in English first; every row the Chinese table carries must use a real name and its versions.
  const bad = chinese.filter(([name, versions]) => !english.has(name) || !versions.every((v) => english.get(name).includes(v)));
  assert.deepEqual(bad, []);
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
      const match = line.match(/^\s*"([^"]+)" to Tr\(.*zh = "([^"]+)"/);
      if (match) translated.set(match[1], match[2]);
    }
  }
  const keys = [...text('settings-reference.md').matchAll(/^\| `([^`]+)` \|/gm)].map((m) => m[1]);
  const chinese = text('zh-hans/settings-reference.md');
  // A setting 1.5.1 added has no Chinese name yet; every one that does is in the table.
  const missing = [...new Set(keys)].filter((key) => translated.has(key) && !chinese.includes(translated.get(key)));
  assert.ok(keys.length > 50);
  assert.deepEqual(missing, []);
});


test('translated pages parse, match an English page and keep its screenshots', () => {
  const docs = loadDocs();
  const { pages, bySlug } = loadTranslations(docs, undefined, PREFIXED);
  for (const p of pages) {
    const en = docs.pages.find((e) => e.slug === p.slug);
    const shots = (b) => b.flatMap((x) => (x.t === 'img' ? [x.src] : x.items ? x.items.flatMap(shots) : x.c && x.t === 'note' ? shots(x.c) : []));
    assert.deepEqual(shots(p.blocks), shots(en.blocks), `${p.lang}/${p.slug}.md: the same screenshots, in the same order, as ${p.slug}.md`);
    assert.equal(p.blocks.filter((b) => b.t === 'h2').length, en.blocks.filter((b) => b.t === 'h2').length, `${p.lang}/${p.slug}.md: the same sections as ${p.slug}.md`);
  }
  // A page translated at all comes in every language the site has.
  for (const [slug, langs] of bySlug) assert.deepEqual([...langs].sort(), [...PREFIXED].sort(), `${slug}.md's translations`);
});

test('the install guides on GitHub (3ds/INSTALL*.md, linux/INSTALL*.md) match the manual', () => {
  assert.deepEqual(staleInstallGuides(), [], 'run node website/scripts/install-guides.mjs');
});
