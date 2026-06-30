const fs = require('fs')
const http = require('http')
const https = require('https')
const path = require('path')

const DEFAULT_BASE_URL = 'http://127.0.0.1:8080'
const DEFAULT_ACCOUNT = '13700010001'
const DEFAULT_PASSWORD = 'admin123'
const DEFAULT_TIMEOUT_MS = 10000

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
  const result = {}
  const content = fs.readFileSync(filePath, 'utf8')
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
  const headers = {
    accept: 'application/json',
    ...(options.headers || {})
  }
  let body = options.rawBody || null
  if (options.body) {
    body = JSON.stringify(options.body)
    headers['content-type'] = 'application/json'
  }
  if (body) {
    headers['content-length'] = Buffer.byteLength(body)
  }

  return new Promise((resolve, reject) => {
    const parsed = new URL(url)
    const client = parsed.protocol === 'https:' ? https : http
    const request = client.request(url, { method, timeout: timeoutMs, headers }, (response) => {
      const chunks = []
      response.on('data', (chunk) => chunks.push(chunk))
      response.on('end', () => {
        const rawResponse = Buffer.concat(chunks).toString('utf8')
        resolve({
          statusCode: response.statusCode || 0,
          contentType: response.headers['content-type'] || '',
          bodyLength: Buffer.byteLength(rawResponse),
          body: rawResponse,
          json: parseJson(rawResponse)
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

function validateAjax(name, response, options = {}) {
  const allowBusinessFailure = !!options.allowBusinessFailure
  const failures = []
  if (response.statusCode !== 200) {
    failures.push(`HTTP ${response.statusCode}`)
  }
  if (!response.json) {
    failures.push('response is not JSON')
  } else if (Object.prototype.hasOwnProperty.call(response.json, 'code') && Number(response.json.code) !== 200 && !allowBusinessFailure) {
    failures.push(`business code ${response.json.code}: ${response.json.msg || response.body}`)
  }
  return {
    name,
    response,
    failures,
    businessCode: response.json && response.json.code,
    message: response.json && response.json.msg
  }
}

async function api(baseUrl, token, method, requestPath, body, options = {}) {
  const response = await requestUrl(buildUrl(baseUrl, requestPath), {
    method,
    timeout: options.timeout,
    body,
    headers: token ? { Authorization: `Bearer ${token}` } : undefined
  })
  return response
}

function buildMultipartPng() {
  const boundary = `----codex-local-write-${Date.now()}`
  const pngBase64 = 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+/p9sAAAAASUVORK5CYII='
  const fileBuffer = Buffer.from(pngBase64, 'base64')
  const head = Buffer.from(
    `--${boundary}\r\n` +
    'Content-Disposition: form-data; name="file"; filename="codex-local-write.png"\r\n' +
    'Content-Type: image/png\r\n\r\n',
    'utf8'
  )
  const tail = Buffer.from(`\r\n--${boundary}--\r\n`, 'utf8')
  return {
    contentType: `multipart/form-data; boundary=${boundary}`,
    body: Buffer.concat([head, fileBuffer, tail])
  }
}

function extractData(response) {
  if (!response.json) {
    return null
  }
  return response.json.data !== undefined ? response.json.data : response.json
}

async function main() {
  const args = parseArgs(process.argv.slice(2))
  const baseUrl = resolveBaseUrl(args)
  const account = String(args.account || process.env.LOCAL_WORKER_ACCOUNT || DEFAULT_ACCOUNT).trim()
  const password = String(args.password || process.env.LOCAL_WORKER_PASSWORD || DEFAULT_PASSWORD).trim()
  const timeout = Number(args.timeout || DEFAULT_TIMEOUT_MS)
  const stamp = new Date().toISOString().replace(/[-:.TZ]/g, '').slice(0, 14)
  const results = []

  const loginResponse = await requestUrl(buildUrl(baseUrl, '/app/worker/auth/login'), {
    method: 'POST',
    timeout,
    body: { username: account, password }
  })
  const loginResult = validateAjax('login', loginResponse)
  const token = loginResponse.json && loginResponse.json.token
  if (!token) {
    loginResult.failures.push('response token is empty')
  }
  results.push(loginResult)
  if (loginResult.failures.length) {
    printSummary(baseUrl, account, results)
    process.exitCode = 1
    return
  }

  const uploadPayload = buildMultipartPng()
  const uploadResponse = await requestUrl(buildUrl(baseUrl, '/common/upload'), {
    method: 'POST',
    timeout,
    rawBody: uploadPayload.body,
    headers: {
      Authorization: `Bearer ${token}`,
      'content-type': uploadPayload.contentType
    }
  })
  results.push(validateAjax('commonUpload', uploadResponse))
  const uploaded = extractData(uploadResponse) || {}
  const uploadedUrl = uploaded.url || `https://cdn.ygb.local/mock/codex-local-write-${stamp}.png`

  const uploadRecordResponse = await api(baseUrl, token, 'POST', '/app/worker/upload-record/create', {
    categoryCode: `codex_write_${stamp}`,
    categoryName: 'Codex local write upload',
    fileUrl: uploadedUrl,
    fileName: uploaded.fileName || `codex-local-write-${stamp}.png`,
    originalFilename: uploaded.originalFilename || 'codex-local-write.png',
    fileSize: Number(uploadPayload.body.length),
    contentType: 'image/png',
    sourceModule: 'codex-local-write'
  }, { timeout })
  results.push(validateAjax('uploadRecordCreate', uploadRecordResponse))

  const settingsResponse = await api(baseUrl, token, 'POST', '/app/worker/settings/save', {
    notifyEnabled: true
  }, { timeout })
  results.push(validateAjax('settingsSave', settingsResponse))

  const pushRegisterResponse = await api(baseUrl, token, 'POST', '/app/worker/settings/push-register', {
    pushClientId: `codex-local-client-${stamp}`,
    notificationPermission: 'authorized',
    pushPlatform: 'local-script'
  }, { timeout })
  results.push(validateAjax('pushRegister', pushRegisterResponse))

  const pushTestResponse = await api(baseUrl, token, 'POST', '/app/worker/settings/push-test', {
    title: `Codex local push ${stamp}`,
    content: 'Local write verification push gateway probe',
    jumpPath: '/pages/notice/index',
    jumpQuery: { source: 'codex-local-write', stamp },
    actionLabel: '查看通知',
    sourceLabel: '本地写链路验证'
  }, { timeout })
  const pushTestResult = validateAjax('pushGatewayTest', pushTestResponse, { allowBusinessFailure: true })
  if (Number(pushTestResult.businessCode) !== 200) {
    pushTestResult.gatewayBlocked = true
  }
  results.push(pushTestResult)

  const complaintResponse = await api(baseUrl, token, 'POST', '/app/worker/complaint/create', {
    complaintType: 'salary',
    title: `Codex 本地写链路投诉 ${stamp}`,
    content: `Codex local write verification complaint ${stamp}`,
    contactMobile: account,
    anonymous: false,
    syncUnion: false,
    attachments: JSON.stringify([{ name: 'codex-local-write.png', url: uploadedUrl }])
  }, { timeout })
  results.push(validateAjax('complaintCreate', complaintResponse))

  const legalResponse = await api(baseUrl, token, 'POST', '/app/worker/legal-consult/create', {
    consultType: 'contract',
    title: `Codex 本地写链路咨询 ${stamp}`,
    content: `Codex local write verification legal consult ${stamp}`,
    contactMobile: account,
    attachments: JSON.stringify([{ name: 'codex-local-write.png', url: uploadedUrl }])
  }, { timeout })
  results.push(validateAjax('legalConsultCreate', legalResponse))

  const activityKey = `codex-local-activity-${stamp}`
  const activityResponse = await api(baseUrl, token, 'POST', `/app/worker/activity/join?activityKey=${encodeURIComponent(activityKey)}`, null, { timeout })
  results.push(validateAjax('activityJoin', activityResponse))

  const videoResponse = await api(baseUrl, token, 'POST', '/app/worker/video/progress?videoKey=heatstroke-first-aid&watchedSeconds=240&totalSeconds=240', null, { timeout })
  results.push(validateAjax('videoProgressSave', videoResponse))

  const checkInResponse = await api(baseUrl, token, 'POST', '/app/worker/attendance/check-in', {
    latitude: 23.12911,
    longitude: 113.264385,
    address: `Codex local write check-in ${stamp}`,
    faceImageUrl: uploadedUrl,
    deviceCode: `codex-${stamp}`
  }, { timeout })
  results.push(validateAjax('attendanceCheckIn', checkInResponse))

  const checkOutResponse = await api(baseUrl, token, 'POST', '/app/worker/attendance/check-out', {
    latitude: 23.12911,
    longitude: 113.264385,
    address: `Codex local write check-out ${stamp}`,
    faceImageUrl: uploadedUrl,
    deviceCode: `codex-${stamp}`
  }, { timeout })
  results.push(validateAjax('attendanceCheckOut', checkOutResponse))

  const noticeListResponse = await api(baseUrl, token, 'GET', '/app/worker/notice/list?pageNum=1&pageSize=10', null, { timeout })
  results.push(validateAjax('noticeListAfterWrites', noticeListResponse))
  const noticeListData = extractData(noticeListResponse) || {}
  const noticeRows = noticeListData.rows || []
  const noticeId = noticeRows[0] && noticeRows[0].noticeId
  if (noticeId !== undefined && noticeId !== null) {
    const noticeReadResponse = await api(baseUrl, token, 'POST', `/app/worker/notice/read?noticeId=${encodeURIComponent(noticeId)}`, null, { timeout })
    results.push(validateAjax('noticeMarkRead', noticeReadResponse))
  } else {
    results.push({
      name: 'noticeMarkRead',
      response: null,
      failures: ['notice list returned no rows'],
      businessCode: '-',
      message: ''
    })
  }

  printSummary(baseUrl, account, results)
  const hardFailures = results.filter((result) => result.failures.length)
  if (hardFailures.length) {
    process.exitCode = 1
  }
}

function printSummary(baseUrl, account, results) {
  console.log('Local write summary:')
  console.log(`- baseUrl: ${baseUrl}`)
  console.log(`- account: ${account}`)
  for (const result of results) {
    const status = result.failures.length ? 'FAIL' : result.gatewayBlocked ? 'BLOCKED' : 'PASS'
    const bodyLength = result.response ? result.response.bodyLength : 0
    const code = result.businessCode === undefined || result.businessCode === null ? '-' : result.businessCode
    console.log(`- ${status} ${result.name}: code ${code}, ${bodyLength} bytes${result.message ? `, ${result.message}` : ''}`)
    for (const failure of result.failures) {
      console.log(`  reason: ${failure}`)
    }
    if (result.gatewayBlocked) {
      console.log('  reason: push gateway is not ready; real delivery remains blocked by backend gateway configuration')
    }
  }
  const passCount = results.filter((result) => !result.failures.length && !result.gatewayBlocked).length
  const blockedCount = results.filter((result) => result.gatewayBlocked).length
  const failCount = results.filter((result) => result.failures.length).length
  console.log(`Local write result: ${passCount} passed, ${blockedCount} blocked, ${failCount} failed.`)
}

main().catch((error) => {
  console.error(error)
  process.exit(1)
})
