const fs = require('fs')
const http = require('http')
const https = require('https')
const path = require('path')

const DEFAULT_BASE_URL = 'http://127.0.0.1:8080'
const DEFAULT_ACCOUNT = 'gzentadmin'
const DEFAULT_PASSWORD = 'admin123'
const DEFAULT_TIMEOUT_MS = 8000

const smokeCases = [
  { name: 'home', method: 'GET', path: '/app/enterprise/home/dashboard', expectText: ['广州南粤'] },
  { name: 'workbench', method: 'GET', path: '/app/enterprise/workbench/dashboard' },
  { name: 'peopleLedger', method: 'GET', path: '/app/enterprise/people/ledger' },
  { name: 'deviceLedger', method: 'GET', path: '/app/enterprise/device/ledger' },
  { name: 'salaryDashboard', method: 'GET', path: '/app/enterprise/salary/dashboard' },
  { name: 'operationApproval', method: 'GET', path: '/app/enterprise/operation-approval/dashboard' },
  { name: 'insuranceDashboard', method: 'GET', path: '/app/enterprise/insurance/dashboard' },
  { name: 'trainingDashboard', method: 'GET', path: '/app/enterprise/training/dashboard' },
  { name: 'jobPublishDraft', method: 'GET', path: '/app/enterprise/job-publish/draft' }
]

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
  if (!fs.existsSync(filePath)) {
    return {}
  }
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

function resolveBaseUrl(args) {
  if (args['base-url'] || process.env.LOCAL_WORKER_API_BASE_URL) {
    return normalizeBaseUrl(args['base-url'] || process.env.LOCAL_WORKER_API_BASE_URL)
  }
  const rootDir = path.resolve(__dirname, '..')
  const localEnvPath = path.join(rootDir, '.env.local')
  const exampleEnvPath = path.join(rootDir, '.env.example')
  const config = parseEnvFile(fs.existsSync(localEnvPath) ? localEnvPath : exampleEnvPath)
  return normalizeBaseUrl(config.VITE_WORKER_API_BASE_URL || DEFAULT_BASE_URL)
}

function buildUrl(baseUrl, requestPath) {
  const parsed = new URL(baseUrl)
  const [pathname, search = ''] = requestPath.split('?')
  parsed.pathname = `${parsed.pathname.replace(/\/+$/, '')}/${pathname.replace(/^\/+/, '')}`
  parsed.search = search ? `?${search}` : ''
  parsed.hash = ''
  return parsed.toString()
}

function parseJson(rawBody) {
  try {
    return JSON.parse(rawBody)
  } catch (error) {
    return null
  }
}

function requestUrl(url, options = {}) {
  const method = options.method || 'GET'
  const timeoutMs = Number(options.timeout || DEFAULT_TIMEOUT_MS)
  const body = options.body ? JSON.stringify(options.body) : null
  const headers = {
    accept: 'application/json',
    ...(options.headers || {})
  }
  if (body) {
    headers['content-type'] = 'application/json'
    headers['content-length'] = Buffer.byteLength(body)
  }

  return new Promise((resolve, reject) => {
    const parsed = new URL(url)
    const client = parsed.protocol === 'https:' ? https : http
    const request = client.request(url, { method, timeout: timeoutMs, headers }, (response) => {
      const chunks = []
      response.on('data', (chunk) => chunks.push(chunk))
      response.on('end', () => {
        const rawBody = Buffer.concat(chunks).toString('utf8')
        resolve({
          statusCode: response.statusCode || 0,
          contentType: response.headers['content-type'] || '',
          bodyLength: Buffer.byteLength(rawBody),
          body: rawBody,
          json: parseJson(rawBody)
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

function validateResponse(testCase, response) {
  const failures = []
  if (response.statusCode !== 200) {
    failures.push(`HTTP ${response.statusCode}`)
  }
  if (!response.json) {
    failures.push('response is not JSON')
  } else if (Object.prototype.hasOwnProperty.call(response.json, 'code') && Number(response.json.code) !== 200) {
    failures.push(`business code ${response.json.code}: ${response.json.msg || 'no message'}`)
  }
  for (const expectedText of testCase.expectText || []) {
    if (!response.body.includes(expectedText)) {
      failures.push(`missing expected text: ${expectedText}`)
    }
  }
  return failures
}

function payloadKeys(response) {
  if (!response.json || typeof response.json !== 'object') {
    return '-'
  }
  const data = response.json.data
  if (data && typeof data === 'object' && !Array.isArray(data)) {
    return Object.keys(data).slice(0, 8).join(',') || '-'
  }
  return Object.keys(response.json).slice(0, 8).join(',') || '-'
}

async function main() {
  const args = parseArgs(process.argv.slice(2))
  const baseUrl = resolveBaseUrl(args)
  const account = String(args.account || process.env.LOCAL_WORKER_ACCOUNT || DEFAULT_ACCOUNT).trim()
  const password = String(args.password || process.env.LOCAL_WORKER_PASSWORD || DEFAULT_PASSWORD).trim()
  const timeout = Number(args.timeout || DEFAULT_TIMEOUT_MS)

  if (!baseUrl) {
    console.error('Local smoke failed:')
    console.error('- baseUrl is empty')
    process.exitCode = 1
    return
  }

  const loginResponse = await requestUrl(buildUrl(baseUrl, '/app/enterprise/auth/login'), {
    method: 'POST',
    timeout,
    body: { username: account, password }
  })
  const loginFailures = validateResponse({ name: 'login' }, loginResponse)
  const token = loginResponse.json && loginResponse.json.token

  console.log('Local smoke summary:')
  console.log(`- baseUrl: ${baseUrl}`)
  console.log(`- account: ${account}`)
  console.log(`- login: HTTP ${loginResponse.statusCode}, code ${loginResponse.json ? loginResponse.json.code : '-'}`)

  if (loginFailures.length || !token) {
    console.error('Local smoke failed:')
    for (const failure of loginFailures) {
      console.error(`- login: ${failure}`)
    }
    if (!token) {
      console.error('- login: response token is empty')
    }
    process.exitCode = 1
    return
  }

  const results = []
  for (const testCase of smokeCases) {
    try {
      const response = await requestUrl(buildUrl(baseUrl, testCase.path), {
        method: testCase.method,
        timeout,
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      const failures = validateResponse(testCase, response)
      results.push({
        name: testCase.name,
        statusCode: response.statusCode,
        businessCode: response.json && response.json.code,
        bodyLength: response.bodyLength,
        keys: payloadKeys(response),
        failures
      })
    } catch (error) {
      results.push({
        name: testCase.name,
        statusCode: 0,
        businessCode: '-',
        bodyLength: 0,
        keys: '-',
        failures: [error.message]
      })
    }
  }

  for (const result of results) {
    const status = result.failures.length ? 'FAIL' : 'PASS'
    console.log(`- ${status} ${result.name}: HTTP ${result.statusCode}, code ${result.businessCode}, ${result.bodyLength} bytes, keys [${result.keys}]`)
    for (const failure of result.failures) {
      console.log(`  reason: ${failure}`)
    }
  }

  const failed = results.filter((result) => result.failures.length)
  if (failed.length) {
    console.error(`Local smoke failed: ${failed.length}/${results.length} cases failed.`)
    process.exitCode = 1
    return
  }

  console.log(`Local smoke passed: ${results.length}/${results.length} read-only cases passed.`)
}

main().catch((error) => {
  console.error(error)
  process.exit(1)
})
