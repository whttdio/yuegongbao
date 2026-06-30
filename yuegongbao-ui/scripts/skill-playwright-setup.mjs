import {
  hasInstalledChromium,
  npmCommand,
  pathExists,
  playwrightSkillDir,
  printCommandFailure,
  runCommand
} from './skill-common.mjs'
import path from 'node:path'

if (!pathExists(playwrightSkillDir)) {
  console.error(`playwright-skill was not found at ${playwrightSkillDir}.`)
  process.exit(1)
}

if (hasInstalledChromium()) {
  console.log('playwright-skill is already ready. Chromium is installed.')
  process.exit(0)
}

console.log(`Setting up playwright-skill from ${playwrightSkillDir} ...`)

const result = runCommand(npmCommand, ['run', 'setup'], {
  cwd: playwrightSkillDir,
  stdio: 'pipe'
})

if (result.status !== 0) {
  printCommandFailure(result)
  const lockPath = path.join(process.env.LOCALAPPDATA || '', 'ms-playwright', '__dirlock')
  if (pathExists(lockPath)) {
    console.error(`Playwright browser install is blocked by an active lock: ${lockPath}`)
    console.error('Wait for the other Playwright install to finish, or remove the lock if it is stale.')
  }
  process.exit(result.status ?? 1)
}

console.log('playwright-skill setup completed.')
