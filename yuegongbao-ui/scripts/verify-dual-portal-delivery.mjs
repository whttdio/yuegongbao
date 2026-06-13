import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const uiRoot = path.resolve(__dirname, '..')

const fileChecks = [
  {
    file: 'src/utils/portalExplanation.js',
    checks: [
      { type: 'includes', token: 'const baseSourceLabel = item.sourceLabel || panelTitle', label: 'portal explanation preserves backend sourceLabel contract' },
      { type: 'includes', token: 'wbSourceLabel: sourceLabel', label: 'portal explanation forwards sourceLabel into workbench link query' },
      { type: 'includes', token: 'return item.path || item.routePath || item.route || buildPortalModulePath(portalCode, moduleCode)', label: 'portal explanation prefers backend drilldown path contract' }
    ]
  },
  {
    file: 'src/components/PortalExplanationPanel.vue',
    checks: [
      { type: 'includes', token: "item.sourceLabel || '-'", label: 'portal explanation panel renders sourceLabel traceability' }
    ]
  },
  {
    file: 'src/views/index.vue',
    checks: [
      { type: 'includes', token: 'getYgbCockpitDashboard, getAzbCockpitDashboard', label: 'home loads both portal dashboard APIs' },
      { type: 'includes', token: 'HOME_WORKBENCH_COMPONENTS = Object.freeze({', label: 'home defines both portal workbench shells' },
      { type: 'includes', token: "portalDashboard.value.azbExplanation || []", label: 'home consumes azb dashboard explanation data' },
      { type: 'includes', token: "portalDashboard.value.ygbExplanation || []", label: 'home consumes ygb dashboard explanation data' },
      { type: 'includes', token: 'normalizedHomeView.value.summaryCards', label: 'home consumes summary cards from portal aggregation' },
      { type: 'includes', token: 'normalizedHomeView.value.queueSections', label: 'home consumes queue sections from portal aggregation' },
      { type: 'includes', token: 'availableQuickSections', label: 'home exposes portal quick sections' },
      { type: 'includes', token: 'buildDashboardQuickSections(dashboard.quickActions || [], dashboard)', label: 'home quick sections consume portal aggregation source context' },
      { type: 'includes', token: 'const HOME_SUMMARY_FALLBACKS = Object.freeze({', label: 'home defines portal static summary fallbacks' },
      { type: 'includes', token: 'homeSummary: dashboard.homeSummary || resolveHomeSummaryFallback(portal.value.code)', label: 'home summary falls back to portal static aggregate copy' },
      { type: 'includes', token: 'function groupActionsBySource(actions = []) {', label: 'home groups quick actions by backend source context' }
    ]
  },
  {
    file: 'src/views/ygb/cockpit/index.vue',
    checks: [
      { type: 'includes', token: '<portal-explanation-panel', label: 'ygb cockpit renders explanation panel' },
      { type: 'includes', token: "dashboardData.value.ygbExplanation || []", label: 'ygb cockpit consumes ygb explanation payload' },
      { type: 'includes', token: 'handlePortalExplanationAction', label: 'ygb cockpit exposes explanation drilldown handler' },
      { type: 'excludes', token: '6.1 ', label: 'ygb cockpit avoids azb explanation wording' }
    ]
  },
  {
    file: 'src/views/azb/cockpit/index.vue',
    checks: [
      { type: 'includes', token: '<portal-explanation-panel', label: 'azb cockpit renders explanation panel' },
      { type: 'includes', token: "dashboardData.value.azbExplanation || []", label: 'azb cockpit consumes azb explanation payload' },
      { type: 'includes', token: 'handlePortalExplanationAction', label: 'azb cockpit exposes explanation drilldown handler' },
      { type: 'excludes', token: '530.1 ', label: 'azb cockpit avoids ygb explanation wording' }
    ]
  },
  {
    file: 'src/views/ygb/statReport/index.vue',
    checks: [
      { type: 'includes', token: '<portal-explanation-panel', label: 'ygb statReport renders explanation panel' },
      { type: 'includes', token: "summaryData.value.ygbExplanation || []", label: 'ygb statReport consumes ygb explanation payload' },
      { type: 'includes', token: 'buildWorkbenchContext', label: 'ygb statReport supports workbench source context' },
      { type: 'includes', token: 'stripWorkbenchRouteQuery', label: 'ygb statReport can clear workbench source context' },
      { type: 'includes', token: 'function clearWorkbenchContext()', label: 'ygb statReport implements workbench clear handler' },
      { type: 'excludes', token: '6.1 ', label: 'ygb statReport avoids azb explanation wording' }
    ]
  },
  {
    file: 'src/views/ygb/aiReport/index.vue',
    checks: [
      { type: 'includes', token: '<portal-explanation-panel', label: 'ygb aiReport renders explanation panel' },
      { type: 'includes', token: 'dashboard.ygbExplanation || []', label: 'ygb aiReport consumes ygb explanation payload' },
      { type: 'includes', token: 'buildWorkbenchContext', label: 'ygb aiReport supports workbench source context' },
      { type: 'includes', token: 'stripWorkbenchRouteQuery', label: 'ygb aiReport can clear workbench source context' },
      { type: 'includes', token: 'function clearWorkbenchContext()', label: 'ygb aiReport implements workbench clear handler' },
      { type: 'excludes', token: '6.1 ', label: 'ygb aiReport avoids azb explanation wording' }
    ]
  },
  {
    file: 'src/views/azb/statReport/index.vue',
    checks: [
      { type: 'includes', token: '<portal-explanation-panel', label: 'azb statReport renders explanation panel' },
      { type: 'includes', token: "summaryData.value.azbExplanation || []", label: 'azb statReport consumes azb explanation payload' },
      { type: 'includes', token: 'buildWorkbenchContext', label: 'azb statReport supports workbench source context' },
      { type: 'includes', token: 'stripWorkbenchRouteQuery', label: 'azb statReport can clear workbench source context' },
      { type: 'includes', token: 'function clearWorkbenchContext()', label: 'azb statReport implements workbench clear handler' },
      { type: 'excludes', token: '530.1 ', label: 'azb statReport avoids ygb explanation wording' }
    ]
  },
  {
    file: 'src/views/azb/aiReport/index.vue',
    checks: [
      { type: 'includes', token: '<portal-explanation-panel', label: 'azb aiReport renders explanation panel' },
      { type: 'includes', token: 'dashboard.azbExplanation || []', label: 'azb aiReport consumes azb explanation payload' },
      { type: 'includes', token: 'buildWorkbenchContext', label: 'azb aiReport supports workbench source context' },
      { type: 'includes', token: 'stripWorkbenchRouteQuery', label: 'azb aiReport can clear workbench source context' },
      { type: 'includes', token: 'function clearWorkbenchContext()', label: 'azb aiReport implements workbench clear handler' },
      { type: 'excludes', token: '530.1 ', label: 'azb aiReport avoids ygb explanation wording' }
    ]
  },
  {
    file: 'src/views/azb/creditScore/index.vue',
    checks: [
      { type: 'includes', token: '<portal-explanation-panel', label: 'azb creditScore renders explanation panel' },
      { type: 'includes', token: "summaryData.value.azbExplanation || []", label: 'azb creditScore consumes azb explanation payload' },
      { type: 'includes', token: 'buildWorkbenchContext', label: 'azb creditScore supports workbench source context' },
      { type: 'includes', token: 'stripWorkbenchRouteQuery', label: 'azb creditScore can clear workbench source context' },
      { type: 'includes', token: 'function clearWorkbenchContext()', label: 'azb creditScore implements workbench clear handler' },
      { type: 'excludes', token: '530.1 ', label: 'azb creditScore avoids ygb explanation wording' }
    ]
  },
  {
    file: 'src/views/ygb/creditScore/index.vue',
    checks: [
      { type: 'includes', token: '<portal-explanation-panel', label: 'ygb creditScore renders explanation panel' },
      { type: 'includes', token: "summaryData.value.ygbExplanation || []", label: 'ygb creditScore consumes ygb explanation payload' },
      { type: 'includes', token: 'handlePortalExplanationAction', label: 'ygb creditScore exposes explanation drilldown handler' },
      { type: 'excludes', token: '6.1 ', label: 'ygb creditScore avoids azb explanation wording' }
    ]
  },
  {
    file: 'src/views/azb/cockpit/index.vue',
    checks: [
      { type: 'includes', token: "focusKeys: ['creditScore', 'statReport', 'aqInsurance', 'warning', 'device', 'heightWorkReport']", label: 'azb cockpit bank profile exposes collaboration and evidence focus keys' },
      { type: 'includes', token: "quickActionKeys: ['aqInsurance', 'preventionFund', 'creditScore', 'statReport', 'warning', 'device', 'heightWorkReport']", label: 'azb cockpit insurer profile exposes full quick action keys' },
      { type: 'includes', token: "moduleKeys: ['aqInsurance', 'preventionFund', 'creditScore', 'statReport', 'warning', 'device', 'heightWorkReport']", label: 'azb cockpit insurer profile exposes full module keys' }
    ]
  }
]

const functionChecks = [
  {
    file: 'src/views/azb/warningRule/index.vue',
    functionName: 'handlePrimaryRuleAction',
    requiredTokens: ['blockReadOnlyAction', 'handleUpdate'],
    label: 'azb warningRule primary action blocks read-only writes'
  },
  {
    file: 'src/views/azb/injuryEvent/index.vue',
    functionName: 'handlePrimaryEventAction',
    requiredTokens: ['isReadOnlyRole.value', 'blockReadOnlyAction'],
    label: 'azb injuryEvent primary action blocks read-only writes'
  },
  {
    file: 'src/views/azb/warning/index.vue',
    functionName: 'handlePrimaryWarningAction',
    requiredTokens: ['blockReadOnlyAction', 'openHandleDialog'],
    label: 'azb warning primary action blocks read-only writes'
  },
  {
    file: 'src/views/azb/aqInsurance/index.vue',
    functionName: 'handlePrimaryPolicyAction',
    requiredTokens: ['isReadOnlyRole.value', 'syncPolicyContext'],
    label: 'azb aqInsurance primary action blocks read-only writes'
  },
  {
    file: 'src/views/azb/aiReportConfig/index.vue',
    functionName: 'handlePrimaryConfigAction',
    requiredTokens: ['isReadOnlyRole.value', 'handleUpdate', 'handleActivate'],
    label: 'azb aiReportConfig primary action blocks read-only writes'
  },
  {
    file: 'src/views/azb/heightWorkReport/index.vue',
    functionName: 'handlePrimaryReportAction',
    requiredTokens: ['isReadOnlyRole.value', 'handleFinish', 'handleUpdate'],
    label: 'azb heightWorkReport primary action blocks read-only writes'
  },
  {
    file: 'src/views/azb/device/index.vue',
    functionName: 'handlePrimaryDeviceAction',
    requiredTokens: ['blockReadOnlyAction', 'openAuthorizeDialog', 'openHeartbeatDialog'],
    label: 'azb device primary action blocks read-only writes'
  },
  {
    file: 'src/views/azb/uninsuredList/index.vue',
    functionName: 'handlePrimaryAction',
    requiredTokens: ['isReadOnlyRole.value', 'openHandleDialog'],
    label: 'azb uninsuredList primary action blocks read-only writes'
  },
  {
    file: 'src/views/azb/statReport/index.vue',
    functionName: 'handlePrimaryReportAction',
    requiredTokens: ['isReadOnlyRole.value', 'openGenerateDialog'],
    label: 'azb statReport primary action blocks read-only writes'
  },
  {
    file: 'src/views/azb/enterprise/index.vue',
    functionName: 'handlePrimaryEnterpriseAction',
    requiredTokens: ['blockReadOnlyAction', 'handleUpdate'],
    label: 'azb enterprise primary action blocks read-only writes'
  },
  {
    file: 'src/views/azb/creditScore/index.vue',
    functionName: 'handlePrimaryScoreAction',
    requiredTokens: ['isReadOnlyRole.value', 'openGenerateDialog'],
    label: 'azb creditScore primary action blocks read-only writes'
  },
  {
    file: 'src/views/azb/preventionProject/index.vue',
    functionName: 'handlePrimaryProjectAction',
    requiredTokens: ['blockReadOnlyAction', 'handleUpdate'],
    label: 'azb preventionProject primary action blocks read-only writes'
  },
  {
    file: 'src/views/azb/person/index.vue',
    functionName: 'handlePrimaryPersonAction',
    requiredTokens: ['blockReadOnlyAction', 'handleUpdate'],
    label: 'azb person primary action blocks read-only writes'
  },
  {
    file: 'src/views/azb/preventionFund/index.vue',
    functionName: 'handlePrimaryFundAction',
    requiredTokens: ['blockReadOnlyAction', 'openEditDialog'],
    label: 'azb preventionFund primary action blocks read-only writes'
  },
  {
    file: 'src/views/azb/aiReport/index.vue',
    functionName: 'handlePrimaryReportAction',
    requiredTokens: ['isReadOnlyRole.value', 'openGenerateDialog'],
    label: 'azb aiReport primary action blocks read-only writes'
  }
]

const failures = []

for (const spec of fileChecks) {
  const source = readSource(spec.file)
  for (const check of spec.checks) {
    if (check.type === 'includes' && !source.includes(check.token)) {
      failures.push(`${spec.file}: missing "${check.label}"`)
    }
    if (check.type === 'excludes' && source.includes(check.token)) {
      failures.push(`${spec.file}: unexpected "${check.label}"`)
    }
  }
}

for (const spec of functionChecks) {
  const source = readSource(spec.file)
  const functionBody = extractFunctionBlock(source, spec.functionName)
  if (!functionBody) {
    failures.push(`${spec.file}: missing function ${spec.functionName}`)
    continue
  }
  for (const token of spec.requiredTokens) {
    if (!functionBody.includes(token)) {
      failures.push(`${spec.file}: ${spec.label}, token "${token}" not found in ${spec.functionName}`)
    }
  }
}

if (failures.length) {
  console.error('Dual portal delivery verification failed:')
  failures.forEach(item => console.error(`- ${item}`))
  process.exit(1)
}

console.log(`Dual portal delivery verification passed: ${fileChecks.length} file specs, ${functionChecks.length} function specs.`)

function readSource(relativeFile) {
  return fs.readFileSync(path.join(uiRoot, relativeFile), 'utf8')
}

function extractFunctionBlock(source, functionName) {
  const startPattern = new RegExp(`function\\s+${functionName}\\s*\\(`)
  const startMatch = startPattern.exec(source)
  if (!startMatch) {
    return ''
  }
  const startIndex = startMatch.index
  const tailSource = source.slice(startIndex)
  const nextFunctionMatch = /\nfunction\s+[A-Za-z0-9_]+\s*\(/.exec(tailSource.slice(1))
  if (!nextFunctionMatch) {
    return tailSource
  }
  return tailSource.slice(0, nextFunctionMatch.index + 1)
}
