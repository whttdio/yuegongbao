const fs = require('fs')
const http = require('http')
const https = require('https')
const path = require('path')

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

function requestUrl(url, timeoutMs = DEFAULT_TIMEOUT_MS) {
  return new Promise((resolve, reject) => {
    const client = url.startsWith('https://') ? https : http
    const request = client.get(url, { timeout: timeoutMs }, (response) => {
      const chunks = []
      response.on('data', (chunk) => chunks.push(chunk))
      response.on('end', () => {
        resolve({
          statusCode: response.statusCode || 0,
          contentType: response.headers['content-type'] || '',
          bodyLength: Buffer.concat(chunks).length
        })
      })
    })
    request.on('timeout', () => {
      request.destroy(new Error(`request timeout after ${timeoutMs}ms`))
    })
    request.on('error', reject)
  })
}

function validateConfig(configPath, config) {
  const failures = []
  const warnings = []
  const baseUrl = normalizeBaseUrl(config.VITE_WORKER_API_BASE_URL)
  const account = String(config.UAT_WORKER_ACCOUNT || DEFAULT_WORKER_ACCOUNT).trim()
  const loginMethod = String(config.UAT_WORKER_LOGIN_METHOD || '').trim().toLowerCase()

  if (!baseUrl) {
    failures.push('VITE_WORKER_API_BASE_URL is required in the UAT config file')
  } else {
    try {
      const parsed = new URL(baseUrl)
      if (!['http:', 'https:'].includes(parsed.protocol)) {
        failures.push('VITE_WORKER_API_BASE_URL must use http or https')
      }
      if (parsed.hostname.includes('example.com')) {
        failures.push('VITE_WORKER_API_BASE_URL still points to example.com placeholder')
      }
    } catch (error) {
      failures.push('VITE_WORKER_API_BASE_URL is not a valid URL')
    }
  }

  if (!account) {
    failures.push('UAT_WORKER_ACCOUNT is empty; expected a worker account such as 13700010001')
  }
  if (account !== DEFAULT_WORKER_ACCOUNT) {
    warnings.push(`UAT_WORKER_ACCOUNT is ${account}; default seed candidate is ${DEFAULT_WORKER_ACCOUNT}`)
  }

  if (!['password', 'sms'].includes(loginMethod)) {
    failures.push('UAT_WORKER_LOGIN_METHOD must be password or sms')
  }
  if (loginMethod === 'password' && !String(config.UAT_WORKER_PASSWORD || '').trim()) {
    failures.push('UAT_WORKER_PASSWORD is required when UAT_WORKER_LOGIN_METHOD=password')
  }
  if (loginMethod === 'sms') {
    const hasFixedCode = !!String(config.UAT_WORKER_SMS_CODE || '').trim()
    const hasRealSmsChannel = String(config.UAT_WORKER_SMS_CHANNEL || '').trim().toLowerCase() === 'real'
    if (!hasFixedCode && !hasRealSmsChannel) {
      failures.push('UAT_WORKER_SMS_CODE or UAT_WORKER_SMS_CHANNEL=real is required when UAT_WORKER_LOGIN_METHOD=sms')
    }
  }

  return {
    baseUrl,
    account,
    loginMethod,
    configPath,
    failures,
    warnings
  }
}

async function main() {
  const args = parseArgs(process.argv.slice(2))
  const configPathValue = args.config || process.env.UAT_CONFIG_PATH
  const failures = []

  if (!configPathValue) {
    failures.push('UAT config path is required. Pass --config <path> or set UAT_CONFIG_PATH.')
  }

  const configPath = configPathValue ? path.resolve(String(configPathValue)) : ''
  if (configPath && !fs.existsSync(configPath)) {
    failures.push(`UAT config file does not exist: ${configPath}`)
  }
  if (failures.length) {
    console.error('UAT readiness failed:')
    for (const failure of failures) {
      console.error(`- ${failure}`)
    }
    process.exitCode = 1
    return
  }

  const config = parseEnvFile(configPath)
  const result = validateConfig(configPath, config)

  let captchaProbe = null
  if (!result.failures.length && !args['skip-network']) {
    try {
      const captchaUrl = buildUrl(result.baseUrl, '/captchaImage')
      captchaProbe = await requestUrl(captchaUrl, Number(args.timeout || DEFAULT_TIMEOUT_MS))
      if (captchaProbe.statusCode !== 200) {
        result.failures.push(`/captchaImage returned HTTP ${captchaProbe.statusCode}`)
      }
    } catch (error) {
      result.failures.push(`/captchaImage probe failed: ${error.message}`)
    }
  }

  console.log('UAT readiness summary:')
  console.log(`- config: ${result.configPath}`)
  console.log(`- baseUrl: ${result.baseUrl || '-'}`)
  console.log(`- account: ${result.account || '-'}`)
  console.log(`- loginMethod: ${result.loginMethod || '-'}`)
  if (captchaProbe) {
    console.log(`- captchaImage: HTTP ${captchaProbe.statusCode}, ${captchaProbe.contentType || 'unknown content-type'}, ${captchaProbe.bodyLength} bytes`)
  } else if (args['skip-network']) {
    console.log('- captchaImage: skipped')
  }

  if (result.warnings.length) {
    console.warn('Warnings:')
    for (const warning of result.warnings) {
      console.warn(`- ${warning}`)
    }
  }
  if (result.failures.length) {
    console.error('UAT readiness failed:')
    for (const failure of result.failures) {
      console.error(`- ${failure}`)
    }
    process.exitCode = 1
    return
  }

  console.log('UAT readiness passed.')
  console.log('Next: npm run verify:acceptance && npm run build:app:uat, then run the App package on a real device.')
}

main().catch((error) => {
  console.error(error)
  process.exit(1)
})
