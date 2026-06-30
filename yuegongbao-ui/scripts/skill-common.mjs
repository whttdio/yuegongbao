import fs from 'node:fs'
import os from 'node:os'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { spawnSync } from 'node:child_process'

const __dirname = path.dirname(fileURLToPath(import.meta.url))

export const uiRoot = path.resolve(__dirname, '..')
export const npmCommand = process.platform === 'win32' ? 'npm.cmd' : 'npm'
export const skillRoot = path.join(os.homedir(), '.codex', 'skills')
export const playwrightSkillDir = path.join(skillRoot, 'playwright-skill')
export const kaneContextDir = path.join(uiRoot, '.testmuai')
export const kaneTestsDir = path.join(kaneContextDir, 'tests')

export function readUiPackageJson() {
  return JSON.parse(fs.readFileSync(path.join(uiRoot, 'package.json'), 'utf8'))
}

export function ensureDir(dirPath) {
  fs.mkdirSync(dirPath, { recursive: true })
}

export function pathExists(targetPath) {
  return fs.existsSync(targetPath)
}

export function runCommand(command, args = [], options = {}) {
  const result = spawnSync(command, args, {
    cwd: options.cwd ?? uiRoot,
    env: options.env ?? process.env,
    stdio: options.stdio ?? 'pipe',
    encoding: 'utf8',
    shell: options.shell ?? false
  })

  return result
}

export function resolveKaneExecutable() {
  const lookupCommand = process.platform === 'win32' ? 'where.exe' : 'which'
  const lookup = runCommand(lookupCommand, ['kane-cli'])
  if (lookup.status === 0) {
    const firstLine = (lookup.stdout || '')
      .split(/\r?\n/)
      .map(line => line.trim())
      .find(Boolean)
    if (firstLine) {
      return firstLine
    }
  }
  return null
}

export function getPlaywrightDependencyPath() {
  return path.join(playwrightSkillDir, 'node_modules', 'playwright')
}

export function getPlaywrightBrowserCacheDir() {
  const localAppData = process.env.LOCALAPPDATA
  if (localAppData) {
    return path.join(localAppData, 'ms-playwright')
  }
  return path.join(os.homedir(), 'AppData', 'Local', 'ms-playwright')
}

export function hasInstalledChromium() {
  const cacheDir = getPlaywrightBrowserCacheDir()
  if (!pathExists(cacheDir)) {
    return false
  }
  return fs.readdirSync(cacheDir).some(entry => entry.startsWith('chromium-'))
}

export function parseFlagValue(flagName, argv = process.argv.slice(2)) {
  const index = argv.indexOf(flagName)
  if (index >= 0 && index + 1 < argv.length) {
    return argv[index + 1]
  }
  return null
}

export function stripFlag(flagName, argv = process.argv.slice(2)) {
  const result = []
  for (let index = 0; index < argv.length; index += 1) {
    if (argv[index] === flagName) {
      index += 1
      continue
    }
    result.push(argv[index])
  }
  return result
}

export function printCommandFailure(result) {
  if (result.stdout) {
    process.stdout.write(result.stdout)
  }
  if (result.stderr) {
    process.stderr.write(result.stderr)
  }
}
