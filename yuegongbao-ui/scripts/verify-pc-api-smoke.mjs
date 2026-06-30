import { parseFlagValue } from './skill-common.mjs'

const baseUrl = normalizeBaseUrl(parseFlagValue('--api') || process.env.YGB_API_BASE_URL || 'http://localhost:8080')
const username = parseFlagValue('--username') || process.env.YGB_PC_USERNAME || 'admin'
const password = parseFlagValue('--password') || process.env.YGB_PC_PASSWORD || 'admin123'
const enterpriseUsername = parseFlagValue('--enterprise-username') || process.env.YGB_ENTERPRISE_USERNAME || 'gzentadmin'
const enterprisePassword = parseFlagValue('--enterprise-password') || process.env.YGB_ENTERPRISE_PASSWORD || 'admin123'

const smokeEndpoints = [
  ['GET', '/getInfo', '登录用户信息'],
  ['GET', '/getRouters', 'PC 菜单路由'],
  ['GET', '/ygb/warning/list?pageNum=1&pageSize=1', '预警工单'],
  ['GET', '/ygb/contract/list?pageNum=1&pageSize=1', '合同备案'],
  ['GET', '/ygb/salary/batch/list?pageNum=1&pageSize=1', '工资批次'],
  ['GET', '/ygb/salary/batch/arrears/list?pageNum=1&pageSize=1', '拖欠工资'],
  ['GET', '/ygb/device/list?pageNum=1&pageSize=1', '设备台账'],
  ['GET', '/ygb/credit/score/list?pageNum=1&pageSize=1', '信用评分'],
  ['GET', '/ygb/report/list?pageNum=1&pageSize=1&reportCode=SALARY_PAYMENT', '统计报表'],
  ['GET', '/ygb/operation/jobReview/list?pageNum=1&pageSize=1', '岗位审核']
]

const enterpriseSmokeEndpoints = [
  ['GET', '/app/enterprise/home/dashboard', '企业后台首页'],
  ['GET', '/app/enterprise/people/ledger?pageNum=1&pageSize=1', '企业人员台账'],
  ['GET', '/app/enterprise/salary/dashboard', '企业工资仪表盘'],
  ['GET', '/open/portal/ygb/home', '便民服务门户']
]

const token = await login(username, password)
const enterpriseToken = await login(enterpriseUsername, enterprisePassword)
const failures = []

for (const [method, url, label] of smokeEndpoints) {
  const result = await request(method, url, token)
  if (!result.ok) {
    failures.push({ method, url, label, ...result })
  }
}

for (const [method, url, label] of enterpriseSmokeEndpoints) {
  const result = await request(method, url, enterpriseToken)
  if (!result.ok) {
    failures.push({ method, url, label, ...result })
  }
}

if (failures.length) {
  console.error('[verify-pc-api-smoke] failed')
  failures.forEach(item => {
    console.error(`- ${item.label}: ${item.method} ${item.url} -> ${item.status || 'ERR'} ${item.message}`)
  })
  process.exit(1)
}

console.log(`[verify-pc-api-smoke] ok: ${smokeEndpoints.length + enterpriseSmokeEndpoints.length} representative PC APIs checked against ${baseUrl}`)

async function login(loginUsername, loginPassword) {
  const response = await fetch(`${baseUrl}/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json;charset=utf-8',
      'X-Portal-Code': 'ygb'
    },
    body: JSON.stringify({
      username: loginUsername,
      password: loginPassword,
      code: '',
      uuid: '',
      portalCode: 'ygb'
    })
  })
  const data = await readJson(response)
  if (!response.ok || data.code !== 200 || !data.token) {
    throw new Error(`Login failed: HTTP ${response.status} ${data.msg || data.message || ''}`)
  }
  return data.token
}

async function request(method, url, token) {
  try {
    const response = await fetch(`${baseUrl}${url}`, {
      method,
      headers: {
        Authorization: `Bearer ${token}`,
        'X-Portal-Code': 'ygb'
      }
    })
    const data = await readJson(response)
    if (!response.ok) {
      return { ok: false, status: response.status, message: data.msg || data.message || response.statusText }
    }
    if (data.code && data.code !== 200) {
      return { ok: false, status: response.status, message: data.msg || data.message || `code ${data.code}` }
    }
    return { ok: true }
  } catch (error) {
    return { ok: false, status: 0, message: error.message || String(error) }
  }
}

async function readJson(response) {
  const text = await response.text()
  if (!text) return {}
  try {
    return JSON.parse(text)
  } catch {
    return { message: text.slice(0, 300) }
  }
}

function normalizeBaseUrl(value) {
  return String(value || '').replace(/\/$/, '')
}
