const fs = require('fs')
const http = require('http')
const https = require('https')
const path = require('path')

const DEFAULT_BASE_URL = 'http://127.0.0.1:8080'
const DEFAULT_WORKER_ACCOUNT = '13700010001'
const DEFAULT_TIMEOUT_MS = 8000

function parseArgs(argv) {
  const args = {}
  for (let index = 0; index < argv.length; index += 1) {
    const item = argv[index]
    if (!item.startsWith('--')) {
      continue
    }
    const [rawKey, rawValue] = item.slice(2).split('=')
    const key = rawKey.trim()
    if (rawValue !== undefined) {
      args[key] = rawValue
      continue
    }
    const next = argv[index + 1]
    if (next && !next.startsWith('--')) {
      args[key] = next
      index += 1
    } else {
      args[key] = true
    }
  }
  return args
}

function parseEnvFile(filePath) {
  const content = fs.readFileSync(filePath, 'utf8')
  const result = {}
  for (const line of content.split(/\r?\n/)) {
    const trimmed = line.trim()
    if (!trimmed || trimmed.startsWith('#')) {
      continue
    }
    const equalIndex = trimmed.indexOf('=')
    if (equalIndex === -1) {
      continue
    }
    const key = trimmed.slice(0, equalIndex).trim()
    let value = trimmed.slice(equalIndex + 1).trim()
    if ((value.startsWith('"') && value.endsWith('"')) || (value.startsWith("'") && value.endsWith("'"))) {
      value = value.slice(1, -1)
    }
    result[key] = value
  }
  return result
}

function normalizeBaseUrl(value) {
  return String(value || '').trim().replace(/\/+$/, '')
}

function buildUrl(baseUrl, pathname) {
  const parsed = new URL(baseUrl)
  parsed.pathname = `${parsed.pathname.replace(/\/+$/, '')}/${pathname.replace(/^\/+/, '')}`
  parsed.search = ''
  parsed.hash = ''
  return parsed.toString()
}

function requestUrl(url, options = {}) {
  const method = options.method || 'GET'
  const timeoutMs = Number(options.timeout || DEFAULT_TIMEOUT_MS)
  const body = options.body ? JSON.stringify(options.body) : null

  return new Promise((resolve, reject) => {
    const parsed = new URL(url)
    const client = parsed.protocol === 'https:' ? https : http
    const request = client.request(url, {
      method,
      timeout: timeoutMs,
      headers: body
        ? {
            'content-type': 'application/json',
            'content-length': Buffer.byteLength(body)
          }
        : undefined
    }, (response) => {
      const chunks = []
      response.on('data', (chunk) => chunks.push(chunk))
      response.on('end', () => {
        const rawBody = Buffer.concat(chunks).toString('utf8')
        resolve({
          statusCode: response.statusCode || 0,
          contentType: response.headers['content-type'] || '',
          bodyLength: Buffer.byteLength(rawBody),
          json: parseJson(rawBody),
          bodyPreview: rawBody.slice(0, 300)
        })
      })
    })
    request.on('timeout', () => {
      request.destroy(new Error(`request timeout after ${timeoutMs}ms`))
    })
    request.on('error', reject)
    if (body) {
      request.write(body)
    }
    request.end()
  })
}

function parseJson(rawBody) {
  try {
    return JSON.parse(rawBody)
  } catch (error) {
    return null
  }
}

function validateAjaxResult(probe, label, failures) {
  if (!probe || !probe.json || !Object.prototype.hasOwnProperty.call(probe.json, 'code')) {
    return
  }
  if (Number(probe.json.code) !== 200) {
    failures.push(`${label} returned business code ${probe.json.code}: ${probe.json.msg || probe.bodyPreview}`)
  }
}

function resolveConfig(args) {
  const rootDir = path.resolve(__dirname, '..')
  const explicitConfigPath = args.config || process.env.LOCAL_CONFIG_PATH
  const explicitBaseUrl = args['base-url'] || process.env.LOCAL_WORKER_API_BASE_URL
  const defaultEnvPath = path.join(rootDir, '.env.example')
  const localEnvPath = path.join(rootDir, '.env.local')

  if (explicitBaseUrl) {
    return {
      source: '--base-url/LOCAL_WORKER_API_BASE_URL',
      baseUrl: normalizeBaseUrl(explicitBaseUrl)
    }
  }

  if (explicitConfigPath) {
    const configPath = path.resolve(String(explicitConfigPath))
    if (!fs.existsSync(configPath)) {
      return {
        source: configPath,
        baseUrl: '',
        failures: [`config file does not exist: ${configPath}`]
      }
    }
    const config = parseEnvFile(configPath)
    return {
      source: configPath,
      baseUrl: normalizeBaseUrl(config.VITE_WORKER_API_BASE_URL)
    }
  }

  const autoConfigPath = fs.existsSync(localEnvPath) ? localEnvPath : defaultEnvPath
  const config = fs.existsSync(autoConfigPath) ? parseEnvFile(autoConfigPath) : {}
  return {
    source: autoConfigPath,
    baseUrl: normalizeBaseUrl(config.VITE_WORKER_API_BASE_URL || DEFAULT_BASE_URL)
  }
}

function validateBaseUrl(baseUrl) {
  const failures = []
  const warnings = []

  if (!baseUrl) {
    failures.push('VITE_WORKER_API_BASE_URL is empty')
    return { failures, warnings, parsed: null }
  }

  let parsed = null
  try {
    parsed = new URL(baseUrl)
  } catch (error) {
    failures.push('VITE_WORKER_API_BASE_URL is not a valid URL')
    return { failures, warnings, parsed: null }
  }

  if (!['http:', 'https:'].includes(parsed.protocol)) {
    failures.push('VITE_WORKER_API_BASE_URL must use http or https')
  }
  if (parsed.hostname.includes('example.com')) {
    failures.push('VITE_WORKER_API_BASE_URL still points to example.com placeholder')
  }
  if (['127.0.0.1', 'localhost'].includes(parsed.hostname)) {
    warnings.push('127.0.0.1/localhost works for local H5 on this machine; real devices need the PC LAN IP instead')
  }

  return { failures, warnings, parsed }
}

function formatProbe(probe) {
  if (!probe) {
    return 'not run'
  }
  return `HTTP ${probe.statusCode}, ${probe.contentType || 'unknown content-type'}, ${probe.bodyLength} bytes`
}

async function main() {
  const args = parseArgs(process.argv.slice(2))
  const timeout = Number(args.timeout || DEFAULT_TIMEOUT_MS)
  const account = String(args.account || process.env.LOCAL_WORKER_ACCOUNT || DEFAULT_WORKER_ACCOUNT).trim()
  const config = resolveConfig(args)
  const failures = [...(config.failures || [])]
  const warnings = []

  const baseUrlCheck = validateBaseUrl(config.baseUrl)
  failures.push(...baseUrlCheck.failures)
  warnings.push(...baseUrlCheck.warnings)

  let captchaProbe = null
  let smsProbe = null

  if (!failures.length && !args['skip-network']) {
    try {
      captchaProbe = await requestUrl(buildUrl(config.baseUrl, '/captchaImage'), { timeout })
      if (captchaProbe.statusCode !== 200) {
        failures.push(`/captchaImage returned HTTP ${captchaProbe.statusCode}`)
      }
      validateAjaxResult(captchaProbe, '/captchaImage', failures)
    } catch (error) {
      failures.push(`/captchaImage probe failed: ${error.message}`)
    }
  }

  if (!failures.length && args['probe-auth'] && !args['skip-network']) {
    try {
      smsProbe = await requestUrl(buildUrl(config.baseUrl, '/app/worker/auth/send-sms-code'), {
        method: 'POST',
        timeout,
        body: { mobile: account }
      })
      if (smsProbe.statusCode !== 200) {
        failures.push(`/app/worker/auth/send-sms-code returned HTTP ${smsProbe.statusCode}`)
      }
      validateAjaxResult(smsProbe, '/app/worker/auth/send-sms-code', failures)
    } catch (error) {
      failures.push(`/app/worker/auth/send-sms-code probe failed: ${error.message}`)
    }
  }

  console.log('Local readiness summary:')
  console.log(`- configSource: ${config.source}`)
  console.log(`- baseUrl: ${config.baseUrl || '-'}`)
  console.log(`- account: ${account || '-'}`)
  console.log(`- captchaImage: ${args['skip-network'] ? 'skipped' : formatProbe(captchaProbe)}`)
  if (args['probe-auth']) {
    console.log(`- sendSmsCode: ${args['skip-network'] ? 'skipped' : formatProbe(smsProbe)}`)
    if (smsProbe && smsProbe.bodyPreview) {
      console.log(`- sendSmsCodePreview: ${smsProbe.bodyPreview.replace(/\s+/g, ' ')}`)
    }
  } else {
    console.log('- sendSmsCode: skipped; pass --probe-auth to check worker account binding')
  }

  warnings.push('SMS login cannot be completed automatically because local WorkerAuthController generates a random Redis-backed code')
  warnings.push('Password login requires a sys_user for 13700010001 with a known password; worker business seed alone is not enough')

  if (warnings.length) {
    console.warn('Warnings:')
    for (const warning of warnings) {
      console.warn(`- ${warning}`)
    }
  }

  if (failures.length) {
    console.error('Local readiness failed:')
    for (const failure of failures) {
      console.error(`- ${failure}`)
    }
    console.error('Current local blockers are expected if the Java backend, MySQL, or Redis is not running.')
    process.exitCode = 1
    return
  }

  console.log('Local readiness passed.')
  console.log('Next: npm run verify:acceptance, then start H5/App against this baseUrl for manual local backend checks.')
}

main().catch((error) => {
  console.error(error)
  process.exit(1)
})
