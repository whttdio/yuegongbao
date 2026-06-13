import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const uiRoot = path.resolve(__dirname, '..')
const repoRoot = path.resolve(uiRoot, '..')

const frontendChecks = [
  {
    file: 'src/utils/portalExplanation.js',
    checks: [
      { type: 'includes', token: 'buildPortalModulePath(portalCode, moduleCode)', label: 'portal explanation keeps portal-specific module path resolution' },
      { type: 'includes', token: '...(item.defaultQuery || {}),', label: 'portal explanation preserves backend defaultQuery payload' },
      { type: 'includes', token: 'wbSourceLabel: sourceLabel', label: 'portal explanation forwards wbSourceLabel' },
      { type: 'includes', token: 'wbSourceDescription: sourceDescription', label: 'portal explanation forwards wbSourceDescription' }
    ]
  },
  {
    file: 'src/views/index.vue',
    checks: [
      { type: 'includes', token: "portalDashboard.value.azbExplanation || []", label: 'home binds azb explanation source' },
      { type: 'includes', token: "portalDashboard.value.ygbExplanation || []", label: 'home binds ygb explanation source' },
      { type: 'includes', token: "const portalExplanationTitle = computed(() => portal.value.code === 'azb' ? '6.1 治理解释' : '530.1 办理链解释')", label: 'home keeps portal-specific explanation title' },
      { type: 'includes', token: 'query.wbSourceLabel = actionItem.sourceLabel', label: 'home quick action drilldown forwards wbSourceLabel' },
      { type: 'includes', token: 'query.wbSourceDescription = actionItem.sourceDescription', label: 'home quick action drilldown forwards wbSourceDescription' }
    ]
  },
  {
    file: 'src/views/ygb/cockpit/index.vue',
    checks: [
      { type: 'includes', token: "dashboardData.value.ygbExplanation || []", label: 'ygb cockpit binds ygb explanation source' },
      { type: 'includes', token: 'buildAggregateWorkflowSteps(portalQueueSections.value, portalExplanationItems.value)', label: 'ygb cockpit builds workflow from portal aggregates' },
      { type: 'includes', token: 'resolvedSelectedFocusOverview', label: 'ygb cockpit renders aggregate-first overview' },
      { type: 'excludes', token: "dashboardData.value.azbExplanation || []", label: 'ygb cockpit avoids azb explanation source' }
    ]
  },
  {
    file: 'src/views/azb/cockpit/index.vue',
    checks: [
      { type: 'includes', token: "dashboardData.value.azbExplanation || []", label: 'azb cockpit binds azb explanation source' },
      { type: 'includes', token: 'resolvedSelectedFocusOverview', label: 'azb cockpit renders aggregate-first overview' },
      { type: 'includes', token: 'resolvedRecommendedModules', label: 'azb cockpit renders aggregate-first follow-up modules' },
      { type: 'includes', token: 'resolvedCurrentFocusActionSummary', label: 'azb cockpit renders aggregate-first action summary' },
      { type: 'includes', token: 'resolvedCurrentFocusActionTags', label: 'azb cockpit renders aggregate-first action tags' },
      { type: 'excludes', token: "dashboardData.value.ygbExplanation || []", label: 'azb cockpit avoids ygb explanation source' }
    ]
  },
  {
    file: 'src/views/ygb/statReport/index.vue',
    checks: [
      { type: 'includes', token: "summaryData.value.ygbExplanation || []", label: 'ygb statReport binds ygb explanation source' },
      { type: 'includes', token: 'openPortalExplanationAction', label: 'ygb statReport keeps explanation drilldown handler' },
      { type: 'includes', token: 'function clearWorkbenchContext()', label: 'ygb statReport supports source clearback' },
      { type: 'includes', token: 'stripWorkbenchRouteQuery(route.query, statReportWorkbenchFields)', label: 'ygb statReport clears workbench query with known fields' },
      { type: 'excludes', token: "summaryData.value.azbExplanation || []", label: 'ygb statReport avoids azb explanation source' }
    ]
  },
  {
    file: 'src/views/azb/statReport/index.vue',
    checks: [
      { type: 'includes', token: "summaryData.value.azbExplanation || []", label: 'azb statReport binds azb explanation source' },
      { type: 'includes', token: 'openPortalExplanationAction', label: 'azb statReport keeps explanation drilldown handler' },
      { type: 'includes', token: 'function clearWorkbenchContext()', label: 'azb statReport supports source clearback' },
      { type: 'includes', token: 'stripWorkbenchRouteQuery(route.query, statReportWorkbenchFields)', label: 'azb statReport clears workbench query with known fields' },
      { type: 'excludes', token: "summaryData.value.ygbExplanation || []", label: 'azb statReport avoids ygb explanation source' }
    ]
  },
  {
    file: 'src/views/ygb/aiReport/index.vue',
    checks: [
      { type: 'includes', token: 'dashboard.ygbExplanation || []', label: 'ygb aiReport binds ygb explanation source' },
      { type: 'includes', token: 'openPortalExplanationAction', label: 'ygb aiReport keeps explanation drilldown handler' },
      { type: 'includes', token: 'function clearWorkbenchContext()', label: 'ygb aiReport supports source clearback' },
      { type: 'includes', token: 'stripWorkbenchRouteQuery(route.query, aiReportWorkbenchFields)', label: 'ygb aiReport clears workbench query with known fields' },
      { type: 'excludes', token: 'dashboard.azbExplanation || []', label: 'ygb aiReport avoids azb explanation source' }
    ]
  },
  {
    file: 'src/views/azb/aiReport/index.vue',
    checks: [
      { type: 'includes', token: 'dashboard.azbExplanation || []', label: 'azb aiReport binds azb explanation source' },
      { type: 'includes', token: 'openPortalExplanationAction', label: 'azb aiReport keeps explanation drilldown handler' },
      { type: 'includes', token: 'function clearWorkbenchContext()', label: 'azb aiReport supports source clearback' },
      { type: 'includes', token: 'stripWorkbenchRouteQuery(route.query, aiReportWorkbenchFields)', label: 'azb aiReport clears workbench query with known fields' },
      { type: 'excludes', token: 'dashboard.ygbExplanation || []', label: 'azb aiReport avoids ygb explanation source' }
    ]
  },
  {
    file: 'src/views/ygb/creditScore/index.vue',
    checks: [
      { type: 'includes', token: "summaryData.value.ygbExplanation || []", label: 'ygb creditScore binds ygb explanation source' },
      { type: 'includes', token: 'openPortalExplanationAction', label: 'ygb creditScore keeps explanation drilldown handler' },
      { type: 'includes', token: 'function clearWorkbenchContext()', label: 'ygb creditScore supports source clearback' },
      { type: 'includes', token: 'stripWorkbenchRouteQuery(route.query, creditScoreWorkbenchFields)', label: 'ygb creditScore clears workbench query with known fields' },
      { type: 'excludes', token: "summaryData.value.azbExplanation || []", label: 'ygb creditScore avoids azb explanation source' }
    ]
  },
  {
    file: 'src/views/azb/creditScore/index.vue',
    checks: [
      { type: 'includes', token: "summaryData.value.azbExplanation || []", label: 'azb creditScore binds azb explanation source' },
      { type: 'includes', token: 'openPortalExplanationAction', label: 'azb creditScore keeps explanation drilldown handler' },
      { type: 'includes', token: 'function clearWorkbenchContext()', label: 'azb creditScore supports source clearback' },
      { type: 'includes', token: 'stripWorkbenchRouteQuery(route.query, creditScoreWorkbenchFields)', label: 'azb creditScore clears workbench query with known fields' },
      { type: 'excludes', token: "summaryData.value.ygbExplanation || []", label: 'azb creditScore avoids ygb explanation source' }
    ]
  }
]

const backendChecks = [
  {
    file: 'yuegongbao-business/src/main/java/com/yuegongbao/ygb/cockpit/service/impl/YgbCockpitServiceImpl.java',
    checks: [
      { type: 'includes', token: 'dashboard.setWorkflowSteps(buildYgbWorkflowSteps(ygbQueueSections, ygbExplanation));', label: 'cockpit service wires ygb workflow from ygb aggregate data' },
      { type: 'includes', token: 'dashboard.setYgbExplanation(ygbExplanation);', label: 'cockpit service sets ygb explanation payload' },
      { type: 'includes', token: 'dashboard.setWorkflowSteps(buildAzbWorkflowSteps(roleView));', label: 'cockpit service wires azb workflow from azb role view' },
      { type: 'includes', token: 'dashboard.setAzbExplanation(buildAzbHomeExplanation(dashboard, roleView, dashboardRegion, queryMonth));', label: 'cockpit service sets azb explanation payload' }
    ],
    methodChecks: [
      {
        methodName: 'buildYgbHomeExplanation',
        requiredTokens: ['explanationItem(', 'return list;'],
        label: 'cockpit service keeps dedicated ygb home explanation builder'
      },
      {
        methodName: 'buildAzbHomeExplanation',
        requiredTokens: ['if ("insurer".equals(roleView))', 'if ("bank".equals(roleView))', 'return buildAzbInsurerHomeExplanation(dashboard, regionCode, statMonth);'],
        label: 'cockpit service keeps dedicated azb role-aware home explanation builder'
      }
    ]
  },
  {
    file: 'yuegongbao-business/src/main/java/com/yuegongbao/ygb/credit/service/impl/YgbCreditScoreServiceImpl.java',
    checks: [
      { type: 'includes', token: 'summary.setYgbExplanation(buildYgbExplanation(creditScore, summary));', label: 'credit score service sets ygb explanation payload' },
      { type: 'includes', token: 'summary.setAzbExplanation(buildAzbExplanation(creditScore, summary));', label: 'credit score service sets azb explanation payload' },
      { type: 'includes', token: 'private List<Map<String, Object>> buildYgbExplanation(YgbCreditScore creditScore, YgbCreditScoreSummary summary)', label: 'credit score service defines ygb explanation builder' },
      { type: 'includes', token: 'private List<Map<String, Object>> buildAzbExplanation(YgbCreditScore creditScore, YgbCreditScoreSummary summary)', label: 'credit score service defines azb explanation builder' }
    ]
  },
  {
    file: 'yuegongbao-business/src/main/java/com/yuegongbao/ygb/report/service/impl/YgbStatReportServiceImpl.java',
    checks: [
      { type: 'includes', token: 'summary.setYgbExplanation(buildYgbExplanation(report, summary));', label: 'stat report service sets ygb explanation payload' },
      { type: 'includes', token: 'summary.setAzbExplanation(buildAzbExplanation(report, summary));', label: 'stat report service sets azb explanation payload' },
      { type: 'includes', token: 'private List<Map<String, Object>> buildYgbExplanation(YgbStatReport report, YgbStatReportSummary summary)', label: 'stat report service defines ygb explanation builder' },
      { type: 'includes', token: 'private List<Map<String, Object>> buildAzbExplanation(YgbStatReport report, YgbStatReportSummary summary)', label: 'stat report service defines azb explanation builder' }
    ]
  },
  {
    file: 'yuegongbao-business/src/main/java/com/yuegongbao/ygb/aireport/service/impl/YgbAiReportServiceImpl.java',
    checks: [
      { type: 'includes', token: 'dashboard.setYgbExplanation(buildYgbExplanation(activeReport, activeItems, dashboard));', label: 'ai report service sets ygb explanation payload' },
      { type: 'includes', token: 'dashboard.setAzbExplanation(buildAzbExplanation(activeReport, activeItems, dashboard));', label: 'ai report service sets azb explanation payload' },
      { type: 'includes', token: 'private List<Map<String, Object>> buildYgbExplanation(YgbAiReport activeReport, List<YgbAiReportItem> activeItems,', label: 'ai report service defines ygb explanation builder' },
      { type: 'includes', token: 'private List<Map<String, Object>> buildAzbExplanation(YgbAiReport activeReport, List<YgbAiReportItem> activeItems,', label: 'ai report service defines azb explanation builder' }
    ]
  }
]

const failures = []

for (const spec of frontendChecks) {
  const source = readSource(path.join(uiRoot, spec.file))
  runChecks(source, spec.file, spec.checks, failures)
}

for (const spec of backendChecks) {
  const source = readSource(path.join(repoRoot, spec.file))
  runChecks(source, spec.file, spec.checks, failures)
  for (const methodCheck of spec.methodChecks || []) {
    const body = extractMethodBlock(source, methodCheck.methodName)
    if (!body) {
      failures.push(`${spec.file}: missing method ${methodCheck.methodName}`)
      continue
    }
    for (const token of methodCheck.requiredTokens) {
      if (!body.includes(token)) {
        failures.push(`${spec.file}: ${methodCheck.label}, token ${JSON.stringify(token)} not found in ${methodCheck.methodName}`)
      }
    }
  }
}

if (failures.length) {
  console.error('Portal differentiation verification failed:')
  failures.forEach(item => console.error(`- ${item}`))
  process.exit(1)
}

console.log(
  `Portal differentiation verification passed: ${frontendChecks.length} frontend specs, ` +
  `${backendChecks.length} backend specs.`
)

function readSource(filePath) {
  return fs.readFileSync(filePath, 'utf8')
}

function runChecks(source, fileLabel, checks, failures) {
  for (const check of checks) {
    if (check.type === 'includes' && !source.includes(check.token)) {
      failures.push(`${fileLabel}: missing "${check.label}"`)
    }
    if (check.type === 'excludes' && source.includes(check.token)) {
      failures.push(`${fileLabel}: unexpected "${check.label}"`)
    }
  }
}

function extractMethodBlock(source, methodName) {
  const startPattern = new RegExp(String.raw`(?:private|public|protected)\s+[^\n{;=]*\b${methodName}\s*\(`, 'g')
  const matches = Array.from(source.matchAll(startPattern))
  const startMatch = matches[matches.length - 1]
  if (!startMatch) {
    return ''
  }
  const startIndex = startMatch.index
  const braceStart = source.indexOf('{', startIndex)
  if (braceStart === -1) {
    return ''
  }
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
  return ''
}
