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

if (!pathExists(playwrightSkillDir)) {
  console.error(`playwright-skill was not found at ${playwrightSkillDir}.`)
  process.exit(1)
}

const targetUrl = providedUrl || detectTargetUrl()
if (!targetUrl) {
  console.error('No PC URL was provided and no local dev server was detected. Start "npm.cmd run dev:ygb" or pass "--url <pc-url>".')
  process.exit(1)
}

const scriptPath = path.join(os.tmpdir(), `playwright-test-ygb-ui-${Date.now()}.js`)
const screenshotPath = path.join(os.tmpdir(), `playwright-smoke-${Date.now()}.png`)

fs.writeFileSync(scriptPath, buildSmokeScript(targetUrl, screenshotPath), 'utf8')

console.log(`Running Playwright smoke test against ${targetUrl}`)

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

console.log(`Playwright smoke test completed. Screenshot: ${screenshotPath}`)

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

  const lines = (detection.stdout || '')
    .split(/\r?\n/)
    .map(line => line.trim())
    .filter(Boolean)
  const jsonLine = [...lines].reverse().find(line => line.startsWith('[') && line.endsWith(']'))
  if (!jsonLine) {
    return null
  }

  const servers = JSON.parse(jsonLine)
  if (!Array.isArray(servers) || servers.length === 0) {
    return null
  }

  const preferredPorts = ['5173', '4173']
  for (const port of preferredPorts) {
    const match = servers.find(item => item.endsWith(`:${port}`))
    if (match) {
      return match
    }
  }

  return servers[0]
}

function buildSmokeScript(targetUrl, screenshotPath) {
  return `
const { chromium } = require('playwright');

const TARGET_URL = ${JSON.stringify(targetUrl)};
const SCREENSHOT_PATH = ${JSON.stringify(screenshotPath)};

(async () => {
  const browser = await chromium.launch({ headless: false, slowMo: 50 });
  const page = await browser.newPage({ viewport: { width: 1440, height: 900 } });

  try {
    await page.goto(TARGET_URL, { waitUntil: 'domcontentloaded', timeout: 20000 });
    await page.waitForLoadState('networkidle', { timeout: 10000 }).catch(() => {});
    const title = await page.title();
    console.log('Loaded title:', title || '(empty title)');
    await page.screenshot({ path: SCREENSHOT_PATH, fullPage: true });
    console.log('Screenshot saved to', SCREENSHOT_PATH);
  } finally {
    await browser.close();
  }
})();
`.trim()
}
