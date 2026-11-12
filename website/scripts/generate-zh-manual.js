// Run from website/: update the app's offline Chinese copy from the same source as the API.
import { copyFileSync, mkdirSync, writeFileSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';
import { docsApi, loadChineseDocs } from '../src/lib/docs.js';
const docs = loadChineseDocs();
const output = new URL('../../app/src/main/assets/docs-zh-hans.json', import.meta.url);
writeFileSync(output, JSON.stringify({ ...docsApi(docs, 'https://pokedaisy.com'), locale: 'zh-Hans' }) + '\n');

// Bundle illustrations for translated pages as well, so the first offline visit is complete.
const assetDir = fileURLToPath(new URL('../../app/src/main/assets/docs-zh-hans/', import.meta.url));
const manualDir = fileURLToPath(new URL('../../docs/manual/', import.meta.url));
function images(blocks) {
  return blocks.flatMap((b) => b.t === 'img' ? [b] : b.t === 'note' ? images(b.c) :
    (b.t === 'ul' || b.t === 'ol') ? b.items.flatMap(images) : []);
}
for (const page of docs.pages.filter((p) => !p.title.startsWith('未译：'))) for (const image of images(page.blocks)) {
  const relative = image.src.replace(/^\/docs\//, '');
  const target = join(assetDir, relative);
  mkdirSync(dirname(target), { recursive: true });
  copyFileSync(join(manualDir, relative), target);
}
