// The newest beta at build time (never in the browser: it reads the release's files): releases.js' list, that tag's
// compat.json and ROM_FEATURES.md from GitHub, and what it reads on top of this build's games (beta.js).
import { rawReleases, REPO } from './releases.js';
import { betaAdditions, pickBeta } from './beta.js';
import { parseRomFeatures } from './romFeatures.js';
import compat from '../data/compat.json' with { type: 'json' };

/** The beta's ROM_FEATURES.md, parsed; null when its shape is one this build can't read. */
function features(md) {
  try { return parseRomFeatures(md); } catch { return null; }
}

let beta;

/**
 * The newest beta (a pre-release newer than the newest release) and what it supports on top of this build's
 * compat.json, read from that tag's compat.json on GitHub (beta.js): { version, url, apk, add, features } (features:
 * that tag's docs/ROM_FEATURES.md, parsed). Null when there's none or it can't be read: the site then lists the
 * release's games alone.
 */
export async function getBeta() {
  if (beta !== undefined) return beta;
  beta = null;
  const raw = await rawReleases();
  const pick = raw && pickBeta(raw);
  if (!pick) return beta;
  try {
    const res = await fetch(`https://raw.githubusercontent.com/${REPO}/${pick.tag_name}/website/src/data/compat.json`, {
      headers: { 'User-Agent': 'pokedaisy-website' },
    });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const version = pick.tag_name.replace(/^v/, '');
    const apk = pick.assets.find((a) => a.name.endsWith('.apk'));
    // Its ROM_FEATURES.md too, for the beta games' details window (the release's has no rows for them).
    const doc = await fetch(`https://raw.githubusercontent.com/${REPO}/${pick.tag_name}/docs/ROM_FEATURES.md`, {
      headers: { 'User-Agent': 'pokedaisy-website' },
    });
    beta = {
      version,
      url: pick.html_url,
      apk: apk && { url: apk.browser_download_url, size: apk.size },
      add: betaAdditions(compat, await res.json(), version),
      features: doc.ok ? features(await doc.text()) : null,
    };
  } catch (e) {
    console.warn(`[beta] couldn't read the beta's games (${pick.tag_name}): ${e.message}`);
  }
  return beta;
}

