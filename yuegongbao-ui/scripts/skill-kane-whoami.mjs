import { resolveKaneExecutable, runCommand } from './skill-common.mjs'

const kaneExecutable = resolveKaneExecutable()
if (!kaneExecutable) {
  console.error('kane-cli is not installed. Run "npm.cmd install -g @testmuai/kane-cli" first.')
  process.exit(1)
}

const result = runCommand(kaneExecutable, ['whoami'], {
  env: {
    ...process.env,
    KANE_CLI_USER_AGENT: process.env.KANE_CLI_USER_AGENT || 'codex'
  },
  stdio: 'inherit'
})

if (result.status !== 0) {
  console.error('kane-cli is installed but not authenticated. Run "kane-cli login" and retry.')
  process.exit(result.status ?? 1)
}
