import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const uiRoot = path.resolve(__dirname, '..')
const repoRoot = path.resolve(uiRoot, '..')

const portalFile = path.join(uiRoot, 'src', 'utils', 'portal.js')
const rolePortalSqlFile = path.join(repoRoot, 'sql', 'ygb_phase10_role_portal_scope.sql')

const portalSource = fs.readFileSync(portalFile, 'utf8')
const rolePortalSqlSource = fs.readFileSync(rolePortalSqlFile, 'utf8')

const frontendRules = parseFrontendRoleRules(portalSource)
const sqlRules = parseSqlRoleRules(rolePortalSqlSource)

const problems = []

for (const roleId of sortRoleIds(new Set([...Object.keys(frontendRules), ...Object.keys(sqlRules)]))) {
  const frontendRule = frontendRules[roleId]
  const sqlRule = sqlRules[roleId]

  if (!frontendRule) {
    problems.push(`前端缺少 roleId=${roleId} 的固定门户规则，但 SQL 已定义为 ${formatRule(sqlRule)}`)
    continue
  }

  if (!sqlRule) {
    problems.push(`SQL 缺少 roleId=${roleId} 的门户注入规则，但前端已定义为 ${formatRule(frontendRule)}`)
    continue
  }

  if (frontendRule.allowedPortalScope !== sqlRule.allowedPortalScope || frontendRule.defaultPortalCode !== sqlRule.defaultPortalCode) {
    problems.push(
      `roleId=${roleId} 不一致：前端=${formatRule(frontendRule)}，SQL=${formatRule(sqlRule)}`
    )
  }
}

for (const roleId of ['108', '109']) {
  if (!frontendRules[roleId] || !frontendRules[roleId].readOnly) {
    problems.push(`roleId=${roleId} 应保持只读角色，但前端未标记 readOnly=true`)
  }
}

if (problems.length) {
  console.error('角色门户规则校验失败：')
  problems.forEach(problem => console.error(`- ${problem}`))
  process.exit(1)
}

console.log(`角色门户规则校验通过：前端 ${Object.keys(frontendRules).length} 条，SQL ${Object.keys(sqlRules).length} 条。`)

function parseFrontendRoleRules(source) {
  const rules = {}
  const pattern = /'(\d+)':\s*Object\.freeze\(\{\s*allowedPortalScope:\s*([^,]+),\s*defaultPortalCode:\s*([^,}]+)(?:,\s*readOnly:\s*(true|false))?\s*\}\)/g

  for (const match of source.matchAll(pattern)) {
    const [, roleId, allowedScopeToken, defaultPortalToken, readOnlyToken] = match
    rules[roleId] = {
      allowedPortalScope: normalizePortalToken(allowedScopeToken),
      defaultPortalCode: normalizePortalToken(defaultPortalToken),
      readOnly: readOnlyToken === 'true'
    }
  }

  return rules
}

function parseSqlRoleRules(source) {
  const rules = {}
  const pattern = /UPDATE\s+sys_role\s+SET\s+allowed_portal_scope\s*=\s*'([^']+)'\s*,\s*default_portal_code\s*=\s*'([^']+)'\s+WHERE\s+role_id\s+IN\s*\(([^)]+)\);/gms

  for (const match of source.matchAll(pattern)) {
    const [, allowedPortalScope, defaultPortalCode, roleIdsBlock] = match
    const roleIds = roleIdsBlock
      .split(',')
      .map(item => item.trim())
      .filter(Boolean)

    for (const roleId of roleIds) {
      rules[roleId] = {
        allowedPortalScope,
        defaultPortalCode,
        readOnly: false
      }
    }
  }

  return rules
}

function normalizePortalToken(token) {
  const normalized = token.trim()
  if (normalized.includes('PORTAL_CODES.ygb')) {
    return 'ygb'
  }
  if (normalized.includes('PORTAL_CODES.azb')) {
    return 'azb'
  }
  return normalized.replace(/['"]/g, '')
}

function formatRule(rule) {
  return `allowed=${rule.allowedPortalScope}, default=${rule.defaultPortalCode}${rule.readOnly ? ', readOnly=true' : ''}`
}

function sortRoleIds(roleIds) {
  return Array.from(roleIds).sort((left, right) => Number(left) - Number(right))
}
