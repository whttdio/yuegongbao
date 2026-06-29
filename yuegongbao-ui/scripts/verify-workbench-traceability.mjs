import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const uiRoot = path.resolve(__dirname, '..')

const fileChecks = [
  {
    file: 'src/utils/portalExplanation.js',
    includes: [
      'wbSourceLabel: sourceLabel',
      'wbSourceDescription: sourceDescription',
      'wbSourceTitle: sourceTitle',
      '...(item.defaultQuery || {}),',
      '...(item.query || {}),'
    ]
  },
  {
    file: 'src/views/index.vue',
    includes: [
      'function buildDashboardQuickSections(actionsSource = [], dashboard = {}) {',
      'const mergedQuery = enrichWorkbenchQuery(actionItem)',
      'query.wbSourceLabel = actionItem.sourceLabel',
      'query.wbSourceDescription = actionItem.sourceDescription',
      'query.wbSourceTitle = `${actionItem.label}',
      'query: sanitizeRouteQuery(actionItem.query)'
    ]
  },
  {
    file: 'src/views/cockpit/CyberCockpitScreen.vue',
    includes: [
      'openModule',
      'useWorkbenchAssist',
      'setPageGuide'
    ]
  }
]

const workbenchPageChecks = [
  {
    file: 'src/views/ygb/creditScore/index.vue',
    fieldsToken: "const creditScoreWorkbenchFields = ['enterpriseId', 'regionCode']",
    applyToken: 'applyWorkbenchRouteQuery(route.query, ',
    clearToken: 'query: stripWorkbenchRouteQuery(route.query, creditScoreWorkbenchFields)'
  },
  {
    file: 'src/views/azb/creditScore/index.vue',
    fieldsToken: "const creditScoreWorkbenchFields = ['enterpriseId', 'regionCode']",
    applyToken: 'applyWorkbenchRouteQuery(route.query, ',
    clearToken: 'query: stripWorkbenchRouteQuery(route.query, creditScoreWorkbenchFields)'
  },
  {
    file: 'src/views/ygb/statReport/index.vue',
    fieldsToken: "const statReportWorkbenchFields = ['regionCode']",
    applyToken: 'applyWorkbenchRouteQuery(route.query, ',
    clearToken: 'query: stripWorkbenchRouteQuery(route.query, statReportWorkbenchFields)'
  },
  {
    file: 'src/views/azb/statReport/index.vue',
    fieldsToken: "const statReportWorkbenchFields = ['regionCode']",
    applyToken: 'applyWorkbenchRouteQuery(route.query, ',
    clearToken: 'query: stripWorkbenchRouteQuery(route.query, statReportWorkbenchFields)'
  },
  {
    file: 'src/views/ygb/aiReport/index.vue',
    fieldsToken: "const aiReportWorkbenchFields = ['regionCode']",
    applyToken: 'applyWorkbenchRouteQuery(route.query, ',
    clearToken: 'query: stripWorkbenchRouteQuery(route.query, aiReportWorkbenchFields)'
  },
  {
    file: 'src/views/azb/aiReport/index.vue',
    fieldsToken: "const aiReportWorkbenchFields = ['regionCode']",
    applyToken: 'applyWorkbenchRouteQuery(route.query, ',
    clearToken: 'query: stripWorkbenchRouteQuery(route.query, aiReportWorkbenchFields)',
    extraIncludes: [
      'applyWorkbenchRouteQuery(route.query, aiReportInitialQuery, aiReportWorkbenchFields)'
    ]
  }
]

const failures = []

for (const spec of fileChecks) {
  const source = readSource(spec.file)
  for (const token of spec.includes) {
    if (!source.includes(token)) {
      failures.push(`${spec.file}: missing token ${JSON.stringify(token)}`)
    }
  }
}

for (const spec of workbenchPageChecks) {
  const source = readSource(spec.file)
  if (!source.includes(spec.fieldsToken)) {
    failures.push(`${spec.file}: missing workbench fields token ${JSON.stringify(spec.fieldsToken)}`)
  }
  if (!source.includes('buildWorkbenchContext(route.query')) {
    failures.push(`${spec.file}: missing workbench context builder`)
  }
  if (!source.includes('function clearWorkbenchContext()')) {
    failures.push(`${spec.file}: missing clearWorkbenchContext handler`)
  }
  const applyCount = countOccurrences(source, spec.applyToken)
  if (applyCount < 2) {
    failures.push(`${spec.file}: expected at least 2 occurrences of ${JSON.stringify(spec.applyToken)}, found ${applyCount}`)
  }
  if (!source.includes(spec.clearToken)) {
    failures.push(`${spec.file}: missing clearback token ${JSON.stringify(spec.clearToken)}`)
  }
  for (const token of spec.extraIncludes || []) {
    if (!source.includes(token)) {
      failures.push(`${spec.file}: missing token ${JSON.stringify(token)}`)
    }
  }
}

if (failures.length) {
  console.error('Workbench traceability verification failed:')
  failures.forEach(item => console.error(`- ${item}`))
  process.exit(1)
}

console.log(
  `Workbench traceability verification passed: ${fileChecks.length} source specs, ` +
  `${workbenchPageChecks.length} target page specs.`
)

function readSource(relativeFile) {
  return fs.readFileSync(path.join(uiRoot, relativeFile), 'utf8')
}

function countOccurrences(source, token) {
  let count = 0
  let index = 0
  while (index >= 0) {
    index = source.indexOf(token, index)
    if (index >= 0) {
      count += 1
      index += token.length
    }
  }
  return count
}
