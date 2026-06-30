import path from 'node:path'
import {
  getPlaywrightDependencyPath,
  hasInstalledChromium,
  pathExists,
  playwrightSkillDir,
  readUiPackageJson,
  resolveKaneExecutable,
  runCommand
} from './skill-common.mjs'

const failures = []
const warnings = []
const packageJson = readUiPackageJson()
const scripts = packageJson.scripts || {}

checkUiScripts()
checkPlaywrightSkill()
checkKaneCli()

if (warnings.length) {
  console.log('Warnings:')
  warnings.forEach(item => console.log(`- ${item}`))
}

if (failures.length) {
  console.error('PC skill preflight failed:')
  failures.forEach(item => console.error(`- ${item}`))
  process.exit(1)
}

console.log('PC skill preflight passed: yuegongbao-ui scripts, playwright-skill, and kane-cli are ready.')

function checkUiScripts() {
  const requiredScripts = ['dev', 'dev:ygb', 'dev:azb', 'build:ygb', 'build:azb', 'preview']
  for (const name of requiredScripts) {
    if (!scripts[name]) {
      failures.push(`package.json is missing required script "${name}".`)
    }
  }
}

function checkPlaywrightSkill() {
  if (!pathExists(playwrightSkillDir)) {
    failures.push(`playwright-skill was not found at ${playwrightSkillDir}. Reinstall the global skill first.`)
    return
  }

  if (!pathExists(path.join(playwrightSkillDir, 'run.js'))) {
    failures.push(`playwright-skill is incomplete at ${playwrightSkillDir}. Expected run.js to exist.`)
  }

  if (!pathExists(getPlaywrightDependencyPath())) {
    failures.push('playwright-skill is installed but Playwright dependencies are missing. Run "npm.cmd run skill:pc:playwright:setup".')
    return
  }

  if (!hasInstalledChromium()) {
    failures.push('playwright-skill dependencies exist but Chromium is not installed. Run "npm.cmd run skill:pc:playwright:setup".')
  }
}

function checkKaneCli() {
  const kaneExecutable = resolveKaneExecutable()
  if (!kaneExecutable) {
    failures.push('kane-cli is not installed. Run "npm.cmd install -g @testmuai/kane-cli" and then "kane-cli login".')
    return
  }

  const whoami = runCommand(kaneExecutable, ['whoami'])
  if (whoami.status !== 0) {
    warnings.push(`Detected kane-cli executable at ${kaneExecutable}.`)
    failures.push('kane-cli is installed but not ready for authenticated use. Run "kane-cli login" and retry.')
    if (whoami.stderr || whoami.stdout) {
      const message = [whoami.stdout, whoami.stderr].filter(Boolean).join('\n').trim()
      if (message) {
        failures.push(`kane-cli whoami output: ${message}`)
      }
    }
  }
}
