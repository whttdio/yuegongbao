import fs from 'node:fs'
import os from 'node:os'
import path from 'node:path'
import {
  parseFlagValue,
  pathExists,
  playwrightSkillDir,
  printCommandFailure,
  runCommand,
  uiRoot
} from './skill-common.mjs'

const providedUrl = parseFlagValue('--url')
const username = parseFlagValue('--username') || process.env.YGB_PC_USERNAME || 'admin'
const password = parseFlagValue('--password') || process.env.YGB_PC_PASSWORD || 'admin123'
const offset = Number(parseFlagValue('--offset') || process.env.YGB_PC_SCAN_OFFSET || 0)
const limit = Number(parseFlagValue('--limit') || process.env.YGB_PC_SCAN_LIMIT || 200)

if (!pathExists(playwrightSkillDir)) {
  console.error(`playwright-skill was not found at ${playwrightSkillDir}.`)
  process.exit(1)
}

const targetUrl = providedUrl || detectTargetUrl()
if (!targetUrl) {
  console.error('No PC URL was provided and no local dev server was detected. Start "npm.cmd run dev:ygb" or pass "--url <pc-url>".')
  process.exit(1)
}

const resultPath = path.join(os.tmpdir(), `ygb-pc-runtime-routes-${Date.now()}.json`)
const scriptPath = path.join(os.tmpdir(), `playwright-test-ygb-runtime-routes-${Date.now()}.js`)
fs.writeFileSync(scriptPath, buildRuntimeScanScript({ targetUrl, username, password, offset, limit, resultPath }), 'utf8')

console.log(`Running PC runtime route scan against ${targetUrl} (offset=${offset}, limit=${limit})`)

const result = runCommand(process.execPath, [path.join(playwrightSkillDir, 'run.js'), scriptPath], {
  cwd: uiRoot,
  stdio: 'inherit'
})

try {
  fs.unlinkSync(scriptPath)
} catch {}

if (result.status !== 0) {
  printCommandFailure(result)
  process.exit(result.status ?? 1)
}

const scan = JSON.parse(fs.readFileSync(resultPath, 'utf8'))
const failed = scan.results.filter(item => item.fail)
if (failed.length) {
  console.error(`[verify-pc-runtime-routes] failed: ${failed.length}/${scan.results.length} routes`)
  failed.slice(0, 20).forEach(item => {
    console.error(`- ${item.path} ${item.title}: ${summarizeFailure(item)}`)
  })
  console.error(`Full result: ${resultPath}`)
  process.exit(1)
}

console.log(`[verify-pc-runtime-routes] ok: ${scan.results.length}/${scan.totalRoutes} routes checked. Result: ${resultPath}`)

function detectTargetUrl() {
  const detection = runCommand(process.execPath, [
    '-e',
    "require('./lib/helpers').detectDevServers().then(servers => console.log(JSON.stringify(servers)))"
  ], {
    cwd: playwrightSkillDir
  })

  if (detection.status !== 0) {
    return null
  }

  const jsonLine = (detection.stdout || '')
    .split(/\r?\n/)
    .map(line => line.trim())
    .filter(Boolean)
    .reverse()
    .find(line => line.startsWith('[') && line.endsWith(']'))
  if (!jsonLine) {
    return null
  }

  const servers = JSON.parse(jsonLine)
  if (!Array.isArray(servers) || !servers.length) {
    return null
  }
  return servers.find(item => item.endsWith(':5173')) || servers.find(item => item.endsWith(':4173')) || servers[0]
}

function summarizeFailure(item) {
  if (item.navError) return item.navError
  if (item.apiFailures.length) return `api ${item.apiFailures[0].status} ${item.apiFailures[0].url}`
  if (item.consoleErrors.length) return item.consoleErrors[0]
  if (item.hasNotFoundPage) return 'not found page'
  if (item.appMainWeak) return 'main content is empty'
  return 'unknown failure'
}

function buildRuntimeScanScript(options) {
  return `
const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');

const TARGET_URL = ${JSON.stringify(options.targetUrl)};
const USERNAME = ${JSON.stringify(options.username)};
const PASSWORD = ${JSON.stringify(options.password)};
const OFFSET = ${JSON.stringify(options.offset)};
const LIMIT = ${JSON.stringify(options.limit)};
const RESULT_PATH = ${JSON.stringify(options.resultPath)};

function flattenRoutes(routes, parent = '') {
  const out = [];
  for (const route of routes || []) {
    const rawPath = route.path || '';
    const fullPath = rawPath.startsWith('/') ? rawPath : \`\${parent}/\${rawPath}\`.replace(new RegExp('/+', 'g'), '/');
    const title = route.meta?.title || route.name || rawPath;
    if (route.children?.length) {
      out.push(...flattenRoutes(route.children, fullPath));
    } else if (route.component && !route.hidden && fullPath && !fullPath.includes(':')) {
      out.push({ path: fullPath.replace(new RegExp('/+', 'g'), '/'), title, name: route.name || '' });
    }
  }
  return out;
}

function isMeaningfulRoute(route) {
  const p = route.path;
  return p && !p.includes('http') && !/^\\/(login|register|redirect|profile|index)$/.test(p);
}

function isNotFoundPage(bodyText, mainText) {
  return /页面不存在|找不到页面|您访问的页面不存在/.test(bodyText)
    || /^404(\\s|$)/.test(mainText);
}

(async () => {
  const browser = await chromium.launch({ headless: false, slowMo: 5 });
  const page = await browser.newPage({ viewport: { width: 1440, height: 980 } });
  let routersPayload = null;
  let currentRoute = '';
  let routeApiFailures = [];
  let routeConsoleErrors = [];

  page.on('console', msg => {
    if (msg.type() === 'error') {
      routeConsoleErrors.push(msg.text());
    }
  });
  page.on('pageerror', error => routeConsoleErrors.push(error.message || String(error)));
  page.on('response', async response => {
    const url = response.url();
    if (url.includes('/getRouters')) {
      try { routersPayload = await response.json(); } catch (e) {}
    }
    if (currentRoute && url.includes('/dev-api/') && response.status() >= 500) {
      let body = '';
      try { body = (await response.text()).slice(0, 1000); } catch (e) {}
      routeApiFailures.push({ status: response.status(), url, body });
    }
  });

  await page.goto(\`\${TARGET_URL}/login?portal=ygb\`, { waitUntil: 'networkidle', timeout: 25000 });
  await page.fill('input[type="text"]', USERNAME);
  await page.fill('input[type="password"]', PASSWORD);
  await page.click('button:has-text("登录")');
  await page.waitForURL(url => !String(url).includes('/login'), { timeout: 25000 });
  await page.waitForLoadState('networkidle', { timeout: 12000 }).catch(() => {});

  if (!routersPayload?.data) {
    throw new Error('getRouters payload was not captured');
  }

  const allRoutes = flattenRoutes(routersPayload.data)
    .filter(isMeaningfulRoute)
    .filter((route, index, arr) => arr.findIndex(item => item.path === route.path) === index);
  const routes = allRoutes.slice(OFFSET, OFFSET + LIMIT);
  const results = [];

  for (let i = 0; i < routes.length; i += 1) {
    const route = routes[i];
    currentRoute = route.path;
    routeApiFailures = [];
    routeConsoleErrors = [];
    const started = Date.now();
    let navError = '', bodyText = '', mainText = '', url = '', title = '';
    try {
      await page.goto(\`\${TARGET_URL}\${route.path}\`, { waitUntil: 'domcontentloaded', timeout: 18000 });
      await page.waitForLoadState('networkidle', { timeout: 5000 }).catch(() => {});
      await page.waitForTimeout(400);
      url = page.url();
      title = await page.title();
      bodyText = (await page.locator('body').innerText({ timeout: 2500 }).catch(() => '')).replace(/\\s+/g, ' ').trim();
      mainText = (await page.locator('.app-main, .app-container, main').first().innerText({ timeout: 1200 }).catch(() => '')).replace(/\\s+/g, ' ').trim();
    } catch (error) {
      navError = error.message || String(error);
    }
    const hasNotFoundPage = isNotFoundPage(bodyText, mainText);
    const appMainWeak = mainText.length < 12;
    const consoleErrors = routeConsoleErrors.filter(text => !/Enterprise portal .*not bound to an enterprise/.test(text));
    const fail = Boolean(navError || hasNotFoundPage || routeApiFailures.length || consoleErrors.length || appMainWeak);
    const record = {
      index: OFFSET + i + 1,
      ...route,
      url,
      title,
      elapsedMs: Date.now() - started,
      fail,
      navError,
      hasNotFoundPage,
      appMainWeak,
      mainTextSample: mainText.slice(0, 220),
      bodySample: bodyText.slice(0, 220),
      apiFailures: routeApiFailures,
      consoleErrors: consoleErrors.slice(0, 10)
    };
    results.push(record);
    console.log(\`\${record.index}/\${allRoutes.length} \${fail ? 'FAIL' : 'ok'} \${route.path}\`);
  }

  fs.mkdirSync(path.dirname(RESULT_PATH), { recursive: true });
  fs.writeFileSync(RESULT_PATH, JSON.stringify({ scannedAt: new Date().toISOString(), totalRoutes: allRoutes.length, offset: OFFSET, limit: LIMIT, results }, null, 2), 'utf8');
  await browser.close();
})();
`.trim()
}
