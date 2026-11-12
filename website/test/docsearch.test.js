// The docs search: the cases in docsearch-cases.json (which the app's DocsSearchTest runs too, so the
// two engines agree), then a few against the real manual.
import { test } from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { buildIndex, search, stem, terms, distance } from '../src/lib/docsearch.js';
import { docsApi, loadDocs, loadChineseDocs } from '../src/lib/docs.js';

const cases = JSON.parse(readFileSync(new URL('./docsearch-cases.json', import.meta.url), 'utf8'));
const key = (h) => h.page + (h.anchor ? `#${h.anchor}` : '');

test('words, stems and typos', () => {
  assert.deepEqual(terms('How do I fast-forward the Pokémon battles?'), ['fast', 'forward', 'pokemon', 'battle']);
  assert.equal(stem('saves'), 'save');
  assert.equal(stem('cheating'), 'cheat');
  assert.equal(distance('shader', 'shdaer', 2), 1);
});

test('shared cases rank as expected', () => {
  const index = buildIndex(cases.docs);
  for (const c of cases.cases) {
    const hits = search(index, c.q);
    if (c.top === null) assert.equal(hits.length, 0, `"${c.q}" should find nothing`);
    else assert.equal(key(hits[0] ?? { page: 'nothing' }), c.top, `"${c.q}"`);
  }
});

test('the real manual answers common questions', () => {
  const index = buildIndex(docsApi(loadDocs(), 'https://pokedaisy.com'));
  for (const [q, page] of cases.manual) {
    const hits = search(index, q, 3).map((h) => h.page);
    assert.ok(hits.includes(page), `"${q}" should find ${page} in its top 3, got ${hits.join(', ')}`);
  }
});


test('Chinese substring search highlights the matching words', () => {
  assert.deepEqual(terms('自动备份存档，Pokémon'), ['自动备份存档', 'pokemon']);
  const hit = search(buildIndex(cases.docs), '备份存档')[0];
  assert.equal(hit.page, 'saves');
  assert.deepEqual(hit.snippet.marks.map(([a, b]) => hit.snippet.text.slice(a, b)), ['备份存档']);
  const overlap = search(buildIndex(cases.docs), '备份 备份存档')[0].snippet;
  assert.deepEqual(overlap.marks.map(([a, b]) => overlap.text.slice(a, b)), ['备份存档']);
});


test('the Chinese welcome page is searchable', () => {
  const index = buildIndex(docsApi(loadChineseDocs(), 'https://pokedaisy.com'));
  assert.equal(search(index, '欢迎使用')[0].page, 'welcome');
  assert.equal(search(index, '初次设置')[0].page, 'install');
  assert.equal(search(index, '安装未知应用')[0].page, 'install');
  assert.equal(search(index, '攻略')[0].page, 'guide');
  assert.equal(search(index, '图鉴')[0].page, 'dex');
  assert.equal(search(index, '队伍与详细信息')[0].page, 'party');
  assert.equal(search(index, '训练家卡')[0].page, 'trainer-card');
  assert.equal(search(index, '背包')[0].page, 'bag');
  assert.equal(search(index, '对战')[0].page, 'battle');
  assert.equal(search(index, '金手指')[0].page, 'cheats');
  assert.equal(search(index, '快进与游戏速度')[0].page, 'fast-forward');
  assert.equal(search(index, '存档、备份和即时存档')[0].page, 'saves');
  assert.equal(search(index, '屏幕与设备')[0].page, 'devices');
  assert.equal(search(index, 'Linux 掌机基础版本')[0].page, 'linux');
  assert.equal(search(index, '故障排查与常见问题')[0].page, 'troubleshooting');
  assert.equal(search(index, '支持的游戏')[0].page, 'supported-games');
  assert.equal(search(index, '游戏库')[0].page, 'library');
  assert.equal(search(index, '屏幕、比例与着色器')[0].page, 'screen');
  assert.equal(search(index, '伴侣面板概览')[0].page, 'companion');
  assert.equal(search(index, '控制、快捷键和触屏')[0].page, 'controls');
  assert.equal(search(index, '设置参考')[0].page, 'settings-reference');
});
