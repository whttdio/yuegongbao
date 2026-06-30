import path from 'node:path'
import {
  ensureDir,
  kaneTestsDir,
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
  console.error('kane-cli is not authenticated. Run "kane-cli login" before using skill:pc:kane:generate.')
  process.exit(1)
}

const rawArgs = process.argv.slice(2)
const args = [...rawArgs]

if (!args.includes('--agent')) {
  args.push('--agent')
}

if (args.includes('--save') && !args.includes('--out')) {
  ensureDir(kaneTestsDir)
  args.push('--out', path.relative(uiRoot, kaneTestsDir))
}

console.log(`Running kane-cli generate in ${uiRoot}`)

const result = runCommand(kaneExecutable, ['generate', ...args], {
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
