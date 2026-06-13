import assert from 'assert'
import fs from 'node:fs'
import path from 'node:path'
import vm from 'node:vm'
import { fileURLToPath } from 'node:url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const uiRoot = path.resolve(__dirname, '..')

const failures = []

try {
  verifyPortalExplanationDecoration()
  verifyHomeQuickActionBehavior()
  verifyCockpitActionBehavior()
  verifyWorkbenchContextLifecycle()
} catch (error) {
  failures.push(error instanceof Error ? error.message : String(error))
}

if (failures.length) {
  console.error('Portal runtime behavior verification failed:')
  failures.forEach(item => console.error(`- ${item}`))
  process.exit(1)
}

console.log('Portal runtime behavior verification passed: explanation, home, cockpit, workbench.')

function verifyPortalExplanationDecoration() {
  const source = readSource('src/utils/portalExplanation.js')
  assertIncludes(source, [
    'export function buildPortalModulePath(portalCode, moduleCode) {',
    'wbSourceLabel: sourceLabel',
    'wbSourceDescription: sourceDescription',
    'wbSourceTitle: sourceTitle'
  ], 'src/utils/portalExplanation.js')

  const bundle = loadFunctionBundle(source, [
    'sanitizeRouteQuery',
    'buildPortalModulePath',
    'resolvePortalActionPath',
    'decoratePortalExplanationItems'
  ], `
    function resolvePortalModuleLabel(moduleCode) {
      return moduleCode || 'module'
    }
    function isActionRouteAvailable() {
      return true
    }
  `)

  const ygbItems = bundle.decoratePortalExplanationItems([
    {
      dimensionName: '办理月报',
      recommendModule: 'statReport',
      defaultQuery: { regionCode: '440100' },
      query: { enterpriseId: 'E-01', month: '2026-05' },
      sourceLabel: '530.1 办理链解释',
      sourceDescription: '按办理月报下钻'
    }
  ], {
    portalCode: 'ygb',
    panelTitle: '530.1 办理链解释',
    sourceTitle: '解释来源条件'
  })

  assert.equal(ygbItems.length, 1, 'portalExplanation: expected one ygb explanation item')
  assert.equal(ygbItems[0].action.path, '/ygb/statReport', 'portalExplanation: ygb explanation should resolve to /ygb module path')
  assert.equal(ygbItems[0].action.query.regionCode, '440100', 'portalExplanation: ygb explanation should preserve defaultQuery fields')
  assert.equal(ygbItems[0].action.query.enterpriseId, 'E-01', 'portalExplanation: ygb explanation should preserve item query fields')
  assert.equal(ygbItems[0].action.query.wbSourceTitle, '解释来源条件', 'portalExplanation: ygb explanation should stamp source title')
  assert.match(ygbItems[0].action.query.wbSourceLabel, /办理月报/, 'portalExplanation: ygb explanation should append dimension name into source label')

  const azbItems = bundle.decoratePortalExplanationItems([
    {
      dimensionName: '风险分层',
      recommendModule: 'creditScore',
      defaultQuery: { regionCode: '440300' },
      query: { enterpriseId: 'R-99' },
      sourceLabel: '6.1 治理解释'
    }
  ], {
    portalCode: 'azb',
    panelTitle: '6.1 治理解释',
    panelDescription: '按风险分层继续下钻',
    sourceTitle: '治理来源条件'
  })

  assert.equal(azbItems.length, 1, 'portalExplanation: expected one azb explanation item')
  assert.equal(azbItems[0].action.path, '/azb/creditScore', 'portalExplanation: azb explanation should resolve to /azb module path')
  assert.equal(azbItems[0].action.query.enterpriseId, 'R-99', 'portalExplanation: azb explanation should keep enterprise query')
  assert.equal(azbItems[0].action.query.wbSourceTitle, '治理来源条件', 'portalExplanation: azb explanation should stamp custom source title')
  assert.equal(bundle.buildPortalModulePath('azb', 'warning'), '/azb/warning', 'portalExplanation: buildPortalModulePath should stay portal scoped')
}

function verifyHomeQuickActionBehavior() {
  const source = readSource('src/views/index.vue')
  assertIncludes(source, [
    'function groupActionsBySource(actions = []) {',
    'function enrichWorkbenchQuery(actionItem = {}) {',
    'query.wbSourceLabel = actionItem.sourceLabel',
    'query.wbSourceDescription = actionItem.sourceDescription',
    'query.wbSourceTitle = `${actionItem.label}'
  ], 'src/views/index.vue')

  const bundle = loadFunctionBundle(source, [
    'sanitizeRouteQuery',
    'normalizeRouteKey',
    'groupActionsBySource',
    'enrichWorkbenchQuery'
  ])

  const mergedQuery = bundle.enrichWorkbenchQuery({
    label: '统计报表',
    defaultQuery: { regionCode: '440100', statMonth: '2026-05' },
    query: { enterpriseId: 'E-02', emptyValue: '' },
    sourceLabel: '首页聚合 / 办理月报',
    sourceDescription: '从首页直接查看办理归档结果'
  })

  assert.equal(mergedQuery.regionCode, '440100', 'home: enrichWorkbenchQuery should keep defaultQuery fields')
  assert.equal(mergedQuery.enterpriseId, 'E-02', 'home: enrichWorkbenchQuery should merge direct query fields')
  assert.equal(mergedQuery.wbSourceLabel, '首页聚合 / 办理月报', 'home: enrichWorkbenchQuery should stamp wbSourceLabel')
  assert.equal(mergedQuery.wbSourceDescription, '从首页直接查看办理归档结果', 'home: enrichWorkbenchQuery should stamp wbSourceDescription')
  assert.match(mergedQuery.wbSourceTitle, /统计报表/, 'home: enrichWorkbenchQuery should derive wbSourceTitle from action label')

  const grouped = bundle.groupActionsBySource([
    { key: 'a', sourceLabel: '首页聚合 / A', sourceDescription: 'desc-1' },
    { key: 'b', sourceLabel: '首页聚合 / A', sourceDescription: 'desc-1' },
    { key: 'c', sourceLabel: '首页聚合 / B', sourceDescription: 'desc-2' }
  ])

  assert.equal(grouped.length, 2, 'home: groupActionsBySource should group actions by source label and description')
  assert.equal(grouped[0].actions.length, 2, 'home: first source group should contain both matching actions')

  const sanitized = bundle.sanitizeRouteQuery({
    regionCode: '440100',
    statMonth: '2026-05',
    unused: '',
    empty: null
  })
  assertJsonEqual(sanitized, {
    regionCode: '440100',
    statMonth: '2026-05'
  }, 'home: sanitizeRouteQuery should strip empty route query values')
}

function verifyCockpitActionBehavior() {
  verifySingleCockpitActionBehavior(
    'src/views/ygb/cockpit/index.vue',
    '/ygb/warning',
    '驾驶舱队列 / 预警中心'
  )
  verifySingleCockpitActionBehavior(
    'src/views/azb/cockpit/index.vue',
    '/azb/creditScore',
    '驾驶舱焦点 / 风险对象'
  )
}

function verifySingleCockpitActionBehavior(relativeFile, fallbackPath, expectedSourceLabel) {
  const source = readSource(relativeFile)
  assertIncludes(source, [
    'function createCockpitAction(target = {}, fallback = {}) {',
    'query.wbSourceLabel = sourceLabel',
    'query.wbSourceDescription = sourceDescription',
    'query.wbSourceTitle = sourceTitle'
  ], relativeFile)

  const { createCockpitAction } = loadFunctionBundle(source, ['createCockpitAction'])

  const resolved = createCockpitAction({
    label: '预警中心',
    query: { enterpriseId: 'E-03' },
    sourceLabel: expectedSourceLabel,
    sourceDescription: '继续处理当前来源对象'
  }, {
    path: fallbackPath,
    defaultQuery: { regionCode: '440600' }
  })

  assert.equal(resolved.path, fallbackPath, `${relativeFile}: createCockpitAction should inherit fallback path`)
  assert.equal(resolved.query.regionCode, '440600', `${relativeFile}: createCockpitAction should preserve fallback defaultQuery`)
  assert.equal(resolved.query.enterpriseId, 'E-03', `${relativeFile}: createCockpitAction should merge base query`)
  assert.equal(resolved.query.wbSourceLabel, expectedSourceLabel, `${relativeFile}: createCockpitAction should stamp wbSourceLabel`)
  assert.equal(resolved.query.wbSourceDescription, '继续处理当前来源对象', `${relativeFile}: createCockpitAction should stamp wbSourceDescription`)
  assert.match(resolved.query.wbSourceTitle, /预警中心/, `${relativeFile}: createCockpitAction should derive wbSourceTitle from action label`)

  const stringTarget = createCockpitAction(fallbackPath, {
    label: '统计报表',
    defaultQuery: { regionCode: '440700' },
    sourceLabel: '驾驶舱快捷入口 / 统计报表',
    sourceDescription: '回到统计报表继续复核'
  })

  assert.equal(stringTarget.path, fallbackPath, `${relativeFile}: createCockpitAction should accept string target paths`)
  assert.equal(stringTarget.query.regionCode, '440700', `${relativeFile}: string target should keep fallback defaultQuery`)
  assert.match(stringTarget.query.wbSourceTitle, /统计报表/, `${relativeFile}: string target should also stamp wbSourceTitle`)
}

function verifyWorkbenchContextLifecycle() {
  const source = readSource('src/utils/workbenchLink.js')
  assertIncludes(source, [
    'const WORKBENCH_META_FIELDS = Object.freeze([',
    "'wbSourceLabel'",
    "'wbSourceDescription'",
    "'wbSourceTitle'",
    'export function applyWorkbenchRouteQuery(routeQuery, queryParams, fields = []) {',
    'export function stripWorkbenchRouteQuery(routeQuery, fields = []) {',
    'export function buildWorkbenchContext(routeQuery, options = {}) {'
  ], 'src/utils/workbenchLink.js')

  const bundle = loadFunctionBundle(source, [
    'sanitizeQuery',
    'applyWorkbenchRouteQuery',
    'getWorkbenchRouteQuery',
    'stripWorkbenchRouteQuery',
    'buildWorkbenchContext',
    'inferWorkbenchSource'
  ], `
    const WORKBENCH_META_FIELDS = Object.freeze([
      'wbSourceLabel',
      'wbSourceDescription',
      'wbSourceTitle'
    ])
  `)

  const queryParams = { status: 'todo' }
  const changed = bundle.applyWorkbenchRouteQuery({
    enterpriseId: 'E-04',
    regionCode: '440800',
    ignored: 'x',
    wbSourceLabel: '首页解释 / 办理月报'
  }, queryParams, ['enterpriseId', 'regionCode'])

  assert.equal(changed, true, 'workbenchLink: applyWorkbenchRouteQuery should report changes when source fields are present')
  assertJsonEqual(queryParams, {
    status: 'todo',
    enterpriseId: 'E-04',
    regionCode: '440800'
  }, 'workbenchLink: applyWorkbenchRouteQuery should project only tracked business fields')

  const context = bundle.buildWorkbenchContext({
    enterpriseId: 'E-04',
    regionCode: '440800',
    wbSourceLabel: '首页解释 / 办理月报',
    wbSourceDescription: '按办理月报继续追溯',
    wbSourceTitle: '办理月报来源条件'
  }, {
    fields: ['enterpriseId', 'regionCode'],
    fieldLabels: { enterpriseId: '企业', regionCode: '区域' }
  })

  assert.equal(context.sourceLabel, '首页解释 / 办理月报', 'workbenchLink: buildWorkbenchContext should prefer wbSourceLabel from route query')
  assert.equal(context.title, '办理月报来源条件', 'workbenchLink: buildWorkbenchContext should prefer wbSourceTitle from route query')
  assert.equal(context.description, '按办理月报继续追溯', 'workbenchLink: buildWorkbenchContext should prefer wbSourceDescription from route query')
  assert.equal(context.tags.length, 2, 'workbenchLink: buildWorkbenchContext should expose tracked source tags')
  assert.equal(context.tags[0].label, '企业', 'workbenchLink: buildWorkbenchContext should map field labels')

  const cleared = bundle.stripWorkbenchRouteQuery({
    enterpriseId: 'E-04',
    regionCode: '440800',
    status: 'todo',
    wbSourceLabel: '首页解释 / 办理月报',
    wbSourceDescription: '按办理月报继续追溯',
    wbSourceTitle: '办理月报来源条件'
  }, ['enterpriseId', 'regionCode'])

  assertJsonEqual(cleared, {
    status: 'todo'
  }, 'workbenchLink: stripWorkbenchRouteQuery should clear source business fields and workbench metadata only')
}

function readSource(relativeFile) {
  return fs.readFileSync(path.join(uiRoot, relativeFile), 'utf8')
}

function assertIncludes(source, tokens, label) {
  for (const token of tokens) {
    assert.ok(source.includes(token), `${label}: missing token ${JSON.stringify(token)}`)
  }
}

function assertJsonEqual(actual, expected, message) {
  assert.equal(JSON.stringify(actual), JSON.stringify(expected), message)
}

function loadFunctionBundle(source, functionNames, prelude = '') {
  const script = [
    prelude,
    ...functionNames.map(name => stripExportKeyword(extractFunctionBlock(source, name)))
  ].join('\n\n')

  const context = vm.createContext({})
  vm.runInContext(script, context, { timeout: 1000 })

  return Object.fromEntries(
    functionNames.map(name => [name, context[name]])
  )
}

function stripExportKeyword(block) {
  return block.replace(/^export\s+/m, '')
}

function extractFunctionBlock(source, functionName) {
  const startPattern = new RegExp(String.raw`(?:export\s+)?function\s+${functionName}\s*\(`)
  const startMatch = startPattern.exec(source)
  assert.ok(startMatch, `missing function ${functionName}`)
  const startIndex = startMatch.index
  const paramsStart = source.indexOf('(', startIndex)
  assert.ok(paramsStart >= 0, `missing parameter list for ${functionName}`)
  let paramsDepth = 0
  let braceStart = -1
  for (let index = paramsStart; index < source.length; index += 1) {
    const char = source[index]
    if (char === '(') {
      paramsDepth += 1
    } else if (char === ')') {
      paramsDepth -= 1
      if (paramsDepth === 0) {
        braceStart = source.indexOf('{', index)
        break
      }
    }
  }
  assert.ok(braceStart >= 0, `missing function body for ${functionName}`)

  let depth = 0
  for (let index = braceStart; index < source.length; index += 1) {
    const char = source[index]
    if (char === '{') {
      depth += 1
    } else if (char === '}') {
      depth -= 1
      if (depth === 0) {
        return source.slice(startIndex, index + 1)
      }
    }
  }

  assert.fail(`unterminated function block for ${functionName}`)
}
