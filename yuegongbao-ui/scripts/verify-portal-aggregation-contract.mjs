import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const uiRoot = path.resolve(__dirname, '..')
const repoRoot = path.resolve(uiRoot, '..')

const domainChecks = [
  {
    file: 'yuegongbao-business/src/main/java/com/yuegongbao/ygb/cockpit/domain/YgbWorkbenchDashboard.java',
    tokens: [
      'private List<Map<String, Object>> summaryCards',
      'private List<Map<String, Object>> quickActions',
      'private List<Map<String, Object>> queueSections',
      'private List<Map<String, Object>> ygbExplanation',
      'private List<Map<String, Object>> azbExplanation',
      'private Map<String, Object> defaultQuery',
      'private String sourceLabel',
      'private String sourceDescription'
    ]
  },
  {
    file: 'yuegongbao-business/src/main/java/com/yuegongbao/ygb/cockpit/domain/YgbAzbCockpitDashboard.java',
    tokens: [
      'private List<Map<String, Object>> summaryCards',
      'private List<Map<String, Object>> quickActions',
      'private List<Map<String, Object>> queueSections',
      'private List<Map<String, Object>> azbExplanation',
      'private Map<String, Object> defaultQuery',
      'private String sourceLabel',
      'private String sourceDescription'
    ]
  },
  {
    file: 'yuegongbao-business/src/main/java/com/yuegongbao/ygb/credit/domain/YgbCreditScoreSummary.java',
    tokens: [
      'private List<Map<String, Object>> ygbExplanation',
      'private List<Map<String, Object>> azbExplanation'
    ]
  },
  {
    file: 'yuegongbao-business/src/main/java/com/yuegongbao/ygb/report/domain/YgbStatReportSummary.java',
    tokens: [
      'private List<Map<String, Object>> ygbExplanation',
      'private List<Map<String, Object>> azbExplanation'
    ]
  },
  {
    file: 'yuegongbao-business/src/main/java/com/yuegongbao/ygb/aireport/domain/YgbAiReportDashboard.java',
    tokens: [
      'private List<Map<String, Object>> ygbExplanation',
      'private List<Map<String, Object>> azbExplanation'
    ]
  }
]

const cockpitServiceCheck = {
  file: 'yuegongbao-business/src/main/java/com/yuegongbao/ygb/cockpit/service/impl/YgbCockpitServiceImpl.java',
  dashboardTokens: [
    'dashboard.setSummaryCards(',
    'dashboard.setQuickActions(',
    'dashboard.setQueueSections(',
    'dashboard.setYgbExplanation(',
    'dashboard.setAzbExplanation(',
    'dashboard.setDefaultQuery(',
    'dashboard.setSourceLabel(',
    'dashboard.setSourceDescription('
  ],
  quickActionTokens: [
    'item.put("query", query);',
    'item.put("defaultQuery", query);',
    'item.put("sourceLabel", sourceLabel);',
    'item.put("sourceDescription", sourceDescription);'
  ],
  queueTokens: [
    'item.put("defaultQuery", defaultQuery);',
    'item.put("query", defaultQuery);',
    'item.put("sourceLabel", sourceLabel);',
    'item.put("sourceDescription", sourceDescription);'
  ],
  explanationTokens: [
    'item.put("dimensionName", dimensionName);',
    'item.put("currentValue", currentValue);',
    'item.put("targetValue", targetValue);',
    'item.put("thresholdValue", targetValue);',
    'item.put("summary", summary);',
    'item.put("explanationSummary", summary);',
    'item.put("evidenceModule", evidenceModule);',
    'item.put("evidenceSourceModule", evidenceModule);',
    'item.put("recommendModule", recommendModule);',
    'item.put("recommendedModule", recommendModule);',
    'item.put("defaultQuery", defaultQuery);',
    'item.put("sourceLabel", sourceLabel);',
    'item.put("sourceDescription", sourceDescription);'
  ],
  bankExplanationTokens: [
    'if ("bank".equals(roleView))',
    'explanationItem("信用分层"',
    'explanationItem("区域治理成效"',
    'return list;'
  ],
  portalAggregationTokens: [
    'buildYgbSupplementQuickActions(',
    'appendYgbSupplementQueueSections(',
    'buildEnhancedYgbHomeSummary(',
    'enterpriseReportDraft',
    'monthUninsuredPending',
    'monthWarningPending',
    'buildAzbBankQuickActions(',
    'buildAzbInsurerQuickActions(',
    'focusQueue("aqInsurance", "安责险风险对象"',
    'focusQueue("warning", "待处置预警"',
    'focusQueue("device", "设备异常证据"',
    'focusQueue("heightWorkReport", "进行中高处作业"',
    'focusQueue("preventionFund", "资金协同异常"',
    'queueSection("collaboration"',
    'queueSection("evidence"',
    'queueSection("coverage"',
    'queueSection("insurerGovernance"',
    'buildAzbInsurerHomeExplanation(',
    'resolveAzbRoleHomeSummary(',
    'countDeviceRisk(',
    'countCreditTailRisk(',
    'countFundCoordinationRisk(',
    '首页协同复核',
    '首页风险证据',
    '首页安责险覆盖',
    '首页资金协同',
    'explanationItem("事故预防资金"'
  ]
}

const explanationServiceChecks = [
  {
    file: 'yuegongbao-business/src/main/java/com/yuegongbao/ygb/credit/service/impl/YgbCreditScoreServiceImpl.java',
    sourceLabels: ['530.1 办理链解释', '6.1 风险分层解释']
  },
  {
    file: 'yuegongbao-business/src/main/java/com/yuegongbao/ygb/report/service/impl/YgbStatReportServiceImpl.java',
    sourceLabels: ['530.1 办理链解释', '6.1 治理解释']
  },
  {
    file: 'yuegongbao-business/src/main/java/com/yuegongbao/ygb/aireport/service/impl/YgbAiReportServiceImpl.java',
    sourceLabels: ['530.1 业务影响解释', '6.1 隐患压降解释']
  }
]

const failures = []

for (const spec of domainChecks) {
  const source = readSource(spec.file)
  assertIncludes(source, spec.tokens, spec.file, failures)
}

{
  const source = readSource(cockpitServiceCheck.file)
  assertIncludes(source, cockpitServiceCheck.dashboardTokens, cockpitServiceCheck.file, failures)
  assertIncludes(source, cockpitServiceCheck.portalAggregationTokens, cockpitServiceCheck.file, failures)
  assertMethodTokens(source, 'quickAction', cockpitServiceCheck.quickActionTokens, cockpitServiceCheck.file, failures)
  assertMethodTokens(source, 'queueItem', cockpitServiceCheck.queueTokens, cockpitServiceCheck.file, failures)
  assertMethodTokens(source, 'explanationItem', cockpitServiceCheck.explanationTokens, cockpitServiceCheck.file, failures)
  assertMethodTokens(source, 'buildAzbHomeExplanation', cockpitServiceCheck.bankExplanationTokens, cockpitServiceCheck.file, failures)
}

for (const spec of explanationServiceChecks) {
  const source = readSource(spec.file)
  assertMethodTokens(source, 'explanationItem', [
    'item.put("dimensionName", dimensionName);',
    'item.put("currentValue", currentValue);',
    'item.put("targetValue", targetValue);',
    'item.put("thresholdValue", targetValue);',
    'item.put("summary", summary);',
    'item.put("explanationSummary", summary);',
    'item.put("evidenceModule", evidenceModule);',
    'item.put("evidenceSourceModule", evidenceModule);',
    'item.put("recommendModule", recommendModule);',
    'item.put("recommendedModule", recommendModule);',
    'item.put("defaultQuery", defaultQuery);',
    'item.put("sourceLabel", sourceLabel);',
    'item.put("sourceDescription", summary);'
  ], spec.file, failures)
  assertIncludes(source, spec.sourceLabels, spec.file, failures)
}

if (failures.length) {
  console.error('Portal aggregation contract verification failed:')
  failures.forEach(item => console.error(`- ${item}`))
  process.exit(1)
}

console.log(
  `Portal aggregation contract verification passed: ${domainChecks.length} domain specs, ` +
  `${1 + explanationServiceChecks.length} service specs.`
)

function readSource(relativeFile) {
  return fs.readFileSync(path.join(repoRoot, relativeFile), 'utf8')
}

function assertIncludes(source, tokens, label, failures) {
  for (const token of tokens) {
    if (!source.includes(token)) {
      failures.push(`${label}: missing token ${JSON.stringify(token)}`)
    }
  }
}

function assertMethodTokens(source, methodName, tokens, label, failures) {
  const methodBody = extractMethodBlock(source, methodName)
  if (!methodBody) {
    failures.push(`${label}: missing method ${methodName}`)
    return
  }
  assertIncludes(methodBody, tokens, `${label}#${methodName}`, failures)
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
