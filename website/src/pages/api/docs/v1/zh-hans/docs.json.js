// Simplified Chinese manual; the English v1 endpoint remains available to older apps.
import { docsApi, loadChineseDocs } from '../../../../../lib/docs.js';

export function GET({ site }) {
  return new Response(JSON.stringify({ ...docsApi(loadChineseDocs(), site ?? 'https://pokedaisy.com'), locale: 'zh-Hans' }),
    { headers: { 'Content-Type': 'application/json; charset=utf-8' } });
}
