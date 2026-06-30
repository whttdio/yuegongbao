import path from 'node:path'
import {
  kaneTestsDir,
  pathExists,
  resolveKaneExecutable,
  runCommand,
  uiRoot
} from './skill-common.mjs'

const kaneExecutable = resolveKaneExecutable()
if (!kaneExecutable) {
  console.error('kane-cli is not installed. Run "npm.cmd install -g @testmuai/kane-cli" first.')
  process.exit(1)
}

const whoami = runCommand(kaneExecutable, ['whoami'])
if (whoami.status !== 0) {
  console.error('kane-cli is not authenticated. Run "kane-cli login" before using skill:pc:kane:testmd.')
  process.exit(1)
}

const rawArgs = process.argv.slice(2)
const testArgIndex = rawArgs.findIndex(arg => !arg.startsWith('-'))

if (testArgIndex === -1) {
  console.error('Missing _test.md path. Example: npm.cmd run skill:pc:kane:testmd -- smoke/login_test.md')
  process.exit(1)
}

const providedPath = rawArgs[testArgIndex]
const resolvedPath = resolveTestPath(providedPath)

if (!pathExists(resolvedPath)) {
  console.error(`Test file not found: ${resolvedPath}`)
  process.exit(1)
}

const args = [...rawArgs]
args[testArgIndex] = path.relative(uiRoot, resolvedPath)
if (!args.includes('--agent')) {
  args.push('--agent')
}

console.log(`Running kane-cli testmd from ${resolvedPath}`)

const result = runCommand(kaneExecutable, ['testmd', 'run', ...args], {
  cwd: uiRoot,
  env: {
    ...process.env,
    KANE_CLI_USER_AGENT: process.env.KANE_CLI_USER_AGENT || 'codex'
  },
  stdio: 'inherit'
})

if (result.status !== 0) {
  process.exit(result.status ?? 1)
}

function resolveTestPath(inputPath) {
  if (path.isAbsolute(inputPath)) {
    return inputPath
  }

  const directPath = path.join(uiRoot, inputPath)
  if (pathExists(directPath)) {
    return directPath
  }

  return path.join(kaneTestsDir, inputPath)
}
