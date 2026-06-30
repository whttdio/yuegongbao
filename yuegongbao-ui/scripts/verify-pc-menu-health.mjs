import fs from 'node:fs'
import path from 'node:path'

const root = path.resolve(import.meta.dirname, '..')
const repoRoot = path.resolve(root, '..')
const viewsRoot = path.join(root, 'src', 'views')
const sqlFiles = [
  path.join(repoRoot, 'sql', 'sys_menu.sql'),
  path.join(repoRoot, 'sql', 'ygb_phase50_menu_restructure_by_document.sql'),
  path.join(repoRoot, 'sql', 'ygb_phase51_menu_reorganize.sql'),
  path.join(repoRoot, 'sql', 'ygb_phase52_pc_business_record_dedicated_tables.sql'),
  path.join(repoRoot, 'sql', 'ygb_phase56_pc_menu_document_final.sql')
].filter(file => fs.existsSync(file))

function walk(dir, files = []) {
  if (!fs.existsSync(dir)) return files
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name)
    if (entry.isDirectory()) walk(full, files)
    else if (entry.isFile()) files.push(full)
  }
  return files
}

function normalizeView(file) {
  return path.relative(viewsRoot, file).replace(/\\/g, '/').replace(/\.vue$/, '')
}

const views = new Set(walk(viewsRoot).filter(file => file.endsWith('.vue')).map(normalizeView))
const viewAliases = new Map([
  ['ygb/cockpit/overview/index', 'ygb/cockpit/index'],
  ['azb/cockpit/overview/index', 'azb/cockpit/index']
])
const sqlText = sqlFiles.map(file => fs.readFileSync(file, 'utf8')).join('\n')
const componentMatches = [...sqlText.matchAll(/'((?:ygb|azb|system)\/[^']+\/index)'/g)].map(match => match[1])
const missingComponents = [...new Set(componentMatches)].filter(component => {
  const alias = viewAliases.get(component)
  return !views.has(component) && !(alias && views.has(alias))
})

const iconRoot = path.join(root, 'src', 'assets', 'icons', 'svg')
const localIcons = new Set(walk(iconRoot).filter(file => file.endsWith('.svg')).map(file => path.basename(file, '.svg')))
const phase56File = path.join(repoRoot, 'sql', 'ygb_phase56_pc_menu_document_final.sql')
const phase56Text = fs.existsSync(phase56File) ? fs.readFileSync(phase56File, 'utf8') : ''
const phase56Rows = [...phase56Text.matchAll(/^\s*\((9205\d{3}),.*$/gm)].map(match => match[0])
const phase56MenuRows = phase56Rows.map(parsePhase56MenuRow)
const phase56Icons = phase56MenuRows
  .map(row => String(row.icon || '').trim())
  .filter(icon => icon && icon !== '#')
const missingPhase56Icons = [...new Set(phase56Icons)].filter(icon => !localIcons.has(icon))
const blankPhase56Icons = phase56MenuRows.filter(row => {
  const icon = String(row.icon || '').trim()
  return !icon || icon === '#'
})
const menuTreeProblems = validatePhase56MenuTree(phase56MenuRows)
const directQuestionMarkProblems = findDirectQuestionMarkText(phase56MenuRows)
const legacyRouteAliasProblems = validateLegacyYgbDocumentRouteAliases(phase56MenuRows)
const internalRouteReferenceProblems = validateYgbInternalRouteReferences(phase56MenuRows)
const implementationCopyProblems = findUserVisibleImplementationCopy()

const businessPageText = walk(path.join(viewsRoot, 'ygb'))
  .filter(file => file.endsWith('.vue'))
  .filter(file => !file.endsWith(path.join('shared', 'ModuleRecordPage.vue')))
  .filter(file => !file.endsWith(path.join('shared', 'ledgerPageConfig.js')))
  .map(file => fs.readFileSync(file, 'utf8'))
  .join('\n')
const businessModules = [...businessPageText.matchAll(/module:\s*'([^']+)'/g)].map(match => match[1])
const oldLedgerReferences = [...businessPageText.matchAll(/ModuleRecordPage|createLedgerPageConfig|recordName'/g)].map(match => match[0])

const enumFile = path.join(repoRoot, 'yuegongbao-business', 'src', 'main', 'java', 'com', 'yuegongbao', 'ygb', 'extension', 'domain', 'YgbBusinessRecordModule.java')
const enumText = fs.existsSync(enumFile) ? fs.readFileSync(enumFile, 'utf8') : ''
const missingBackendModules = businessModules.filter(module => !enumText.includes(`"${module}"`))

const permissionProblems = []
for (const module of businessModules) {
  const prefixes = ['list', 'query', 'add', 'edit', 'remove', 'export']
  for (const suffix of prefixes) {
    if (!sqlText.includes(`ygb:${module}:${suffix}`)) {
      permissionProblems.push(`ygb:${module}:${suffix}`)
    }
  }
}

const failures = [
  ...missingComponents.map(item => `missing component: ${item}`),
  ...missingPhase56Icons.map(item => `missing phase56 svg icon: ${item}`),
  ...blankPhase56Icons.map(item => `blank phase56 icon row: ${item.menuId} ${item.menuName}`),
  ...menuTreeProblems.map(item => `phase56 document menu mismatch: ${item}`),
  ...directQuestionMarkProblems.map(item => `direct question-mark text: ${item}`),
  ...implementationCopyProblems.map(item => `user-visible implementation copy: ${item}`),
  ...legacyRouteAliasProblems.map(item => `legacy route alias mismatch: ${item}`),
  ...internalRouteReferenceProblems.map(item => `uncovered ygb internal route reference: ${item}`),
  ...missingBackendModules.map(item => `missing backend module: ${item}`),
  ...permissionProblems.map(item => `missing permission SQL: ${item}`),
  ...oldLedgerReferences.map(item => `old ledger reference: ${item}`)
]

if (failures.length) {
  console.error('[verify-pc-menu-health] failed')
  failures.slice(0, 120).forEach(item => console.error(`- ${item}`))
  if (failures.length > 120) {
    console.error(`- ... ${failures.length - 120} more`)
  }
  process.exit(1)
}

console.log(`[verify-pc-menu-health] ok: ${componentMatches.length} menu components, ${businessModules.length} business modules`)

function parsePhase56MenuRow(row) {
  const values = []
  let current = ''
  let inString = false
  let quoteWasString = false
  const text = row.trim().replace(/[,;]$/, '').replace(/^\(/, '').replace(/\)$/, '')

  for (let index = 0; index < text.length; index += 1) {
    const char = text[index]
    if (char === "'") {
      if (inString && text[index + 1] === "'") {
        current += "'"
        index += 1
      } else {
        inString = !inString
        quoteWasString = true
      }
      continue
    }
    if (char === ',' && !inString) {
      values.push(normalizeSqlValue(current, quoteWasString))
      current = ''
      quoteWasString = false
      continue
    }
    current += char
  }
  values.push(normalizeSqlValue(current, quoteWasString))

  return {
    menuId: Number(values[0]),
    menuName: values[1],
    parentId: Number(values[2]),
    orderNum: Number(values[3]),
    path: values[4],
    component: values[5],
    routeName: values[7],
    menuType: values[8],
    perms: values[9],
    icon: values[10],
    remark: values[11]
  }
}

function normalizeSqlValue(value, wasString) {
  const trimmed = value.trim()
  if (!wasString && /^null$/i.test(trimmed)) return null
  return wasString ? value : trimmed
}

function findDirectQuestionMarkText(rows) {
  const ignoredQuestionMarkLines = /setPageGuide|title:|description:|selection:|businessNameLabel|businessNamePlaceholder|defaultSourceLabel/
  const components = [...new Set(rows
    .filter(row => row.menuType === 'C' && row.component && row.component.startsWith('ygb/'))
    .map(row => row.component))]
  const problems = []

  for (const component of components) {
    const file = path.join(viewsRoot, `${component}.vue`)
    if (!fs.existsSync(file)) {
      continue
    }
    fs.readFileSync(file, 'utf8').split(/\r?\n/).forEach((line, index) => {
      if (/\?{4}/.test(line) && !ignoredQuestionMarkLines.test(line)) {
        problems.push(`${component}.vue:${index + 1}: ${line.trim()}`)
      }
    })
  }

  return problems
}

function findUserVisibleImplementationCopy() {
  const ignoredFiles = /(?:^|\/)(cockpitConfig|platform\/exchange)\//
  const ignoredLinePatterns = [
    /panelDescription:\s*'[^']*接口/,
    /path:\s*'\/system-management\/exchange'/,
    /title:\s*'接口与数据交换/,
    /desc:\s*'[^']*接口说明/,
    /sourceModeLabel/,
    /nonStub/,
    /sourceMode/,
    /\/\/ /
  ]
  const bannedPatterns = [
    /复用(?:统一|现有|上游|主表|企业主表|.*接口|.*底表|.*底座)/,
    /不(?:拆|新增|引入|另造|单独建设|额外建设)/,
    /第二套/,
    /\b(?:Stub|stub|Mock|mock)\b/,
    /接口返回/,
    /字段来自.*接口/,
    /来源于.*接口/,
    /底表拆出/,
    /后台接口/,
    /外部评分引擎/
  ]
  const problems = []
  const files = [
    ...walk(path.join(viewsRoot, 'ygb')),
    ...[
      path.join(viewsRoot, 'device', 'useDevicePage.js'),
      path.join(viewsRoot, 'aqInsurance', 'useAqInsurancePage.js'),
      path.join(viewsRoot, 'creditScore', 'useCreditScorePage.js'),
      path.join(viewsRoot, 'socialPayment', 'useSocialPaymentPage.js'),
      path.join(viewsRoot, 'taxCompare', 'useTaxComparePage.js')
    ].filter(file => fs.existsSync(file))
  ].filter(file => file.endsWith('.vue') || file.endsWith('.js'))

  for (const file of files) {
    const relative = normalizeView(file)
    if (ignoredFiles.test(relative)) {
      continue
    }
    fs.readFileSync(file, 'utf8').split(/\r?\n/).forEach((line, index) => {
      if (ignoredLinePatterns.some(pattern => pattern.test(line))) {
        return
      }
      if (bannedPatterns.some(pattern => pattern.test(line))) {
        problems.push(`${relative}:${index + 1}: ${line.trim()}`)
      }
    })
  }

  return problems
}

function validateLegacyYgbDocumentRouteAliases(rows) {
  const docRoutes = buildPhase56DocumentRoutes(rows)
  const aliasEntries = parseLegacyYgbDocumentRouteAliases()
  const problems = []

  if (!aliasEntries.length) {
    problems.push('permission.js has no LEGACY_YGB_DOCUMENT_ROUTE_ALIASES entries')
    return problems
  }

  for (const [legacyPath, targetPath] of aliasEntries) {
    if (!docRoutes.has(normalizeRoutePath(targetPath))) {
      problems.push(`${legacyPath} -> ${targetPath} is not a phase56 document route`)
    }
  }

  return problems
}

function validateYgbInternalRouteReferences(rows) {
  const docRoutes = buildPhase56DocumentRoutes(rows)
  const aliasEntries = parseLegacyYgbDocumentRouteAliases()
  const aliasSources = new Set(aliasEntries.map(([legacyPath]) => normalizeRoutePath(legacyPath)))
  const allowedRoutes = new Set([
    '/',
    '/index',
    '/login',
    '/redirect',
    '/register',
    ...docRoutes,
    ...aliasSources
  ])
  const ygbViewFiles = walk(path.join(viewsRoot, 'ygb'))
    .filter(file => file.endsWith('.vue') || file.endsWith('.js'))
  const problems = []
  const routeReferencePattern = /(?:path|to|redirect)\s*:\s*['"](\/[^'"]+)['"]|<router-link[^>]+to=["'](\/[^"']+)["']|\$router\.push\(['"](\/[^'"]+)['"]\)/g

  for (const file of ygbViewFiles) {
    const content = fs.readFileSync(file, 'utf8')
    let match
    while ((match = routeReferencePattern.exec(content)) !== null) {
      const routePath = normalizeRoutePath(match[1] || match[2] || match[3])
      if (!routePath || routePath.includes('${') || routePath.startsWith('/portal/')) {
        continue
      }
      const covered = allowedRoutes.has(routePath) ||
        [...docRoutes].some(docRoute => routePath.startsWith(`${docRoute}/`))
      if (!covered) {
        problems.push(`${normalizeView(file)}: ${routePath}`)
      }
    }
  }

  return [...new Set(problems)]
}

function buildPhase56DocumentRoutes(rows) {
  const topRowsById = new Map(rows
    .filter(row => row.parentId === 0)
    .map(row => [row.menuId, row]))
  return new Set(rows
    .filter(row => row.menuType === 'C')
    .map(row => {
      const parentRow = topRowsById.get(row.parentId)
      return parentRow ? normalizeRoutePath(`/${parentRow.path}/${row.path}`) : ''
    })
    .filter(Boolean))
}

function parseLegacyYgbDocumentRouteAliases() {
  const permissionFile = path.join(root, 'src', 'permission.js')
  if (!fs.existsSync(permissionFile)) {
    return []
  }
  const permissionText = fs.readFileSync(permissionFile, 'utf8')
  const aliasBlock = permissionText.match(/const LEGACY_YGB_DOCUMENT_ROUTE_ALIASES = Object\.freeze\(\{([\s\S]*?)\}\)/)
  if (!aliasBlock) {
    return []
  }
  return [...aliasBlock[1].matchAll(/'([^']+)':\s*'([^']+)'/g)]
    .map(match => [normalizeRoutePath(match[1]), normalizeRoutePath(match[2])])
}

function normalizeRoutePath(routePath = '') {
  const normalized = String(routePath || '').trim().replace(/\/{2,}/g, '/').replace(/\/$/, '')
  return normalized || '/'
}

function validatePhase56MenuTree(rows) {
  const expectedTree = [
    ['驾驶舱', ['指标总览', '地图可视化', '实时预警流', '红黄绿码企业分类', '趋势分析', '驾驶舱配置']],
    ['预警中心', ['预警工单管理', '预警规则配置', '预警统计分析']],
    ['AI监测报告', ['监测报告生成', '风险评分与排名', '多维筛选与穿透', '监测建议与任务', '报告导出与订阅', '评分模型管理']],
    ['合同备案', ['合同列表', '合同详情', '合同到期提醒', '未备案合同预警', '合同模板库']],
    ['用工考勤', ['考勤总览', '异常考勤统计', '考勤明细查询', '设备在线率报表']],
    ['工资监管', ['工资发放监控', '监管账户监控', '拖欠工资预警', '银行代发结果监控']],
    ['社保监管', ['社保缴费监控', '基数比对', '参保率统计', '社保补缴跟踪']],
    ['工伤监管', ['工伤事件管理', '工伤认定辅助', '工伤统计分析', '工伤预防', '工伤监测']],
    ['专项治理', ['用工比例监控', '三性岗位审核', '假外包识别', '专项整治台账']],
    ['税务监管', ['个税比对', '发票比对', '资金流穿透分析', '税务预警处置', '追缴税款统计']],
    ['安责险管理', ['投保监管', '事故预防服务', '赔付率监控', '资金池管理']],
    ['设备管理', ['阳光劳务屏管理', '千机万码芯片设备管理', 'AI智能监控设备管理', '设备台账', '设备详情', '拆卸报警处理', '批量操作', '物联卡管理', '芯片库存', '电子围栏']],
    ['扩面减损', ['数据碰撞与漏保清单', '催缴跟踪', '参保补贴管理', '工伤预防培训管理', '培训课程与学时管理', '培训效果评估']],
    ['信用评价', ['信用总览', '企业信用档案', '信用评分规则配置', '联合惩戒推送', '信用修复申请', '信用报告导出']],
    ['统计报表', ['用工', '工资', '社保', '税务', '专项整治', '考勤', '工伤', '安责险', '设备', '扩面减损', '新业态', '职业病', '工会监督报表', '自定义报表']],
    ['人员管理', ['从业人员档案', '特证管理', '黑名单管理', '人员培训监督', '高危岗位库', '风险岗位库', '工伤预防专家库', '新业态人员库']],
    ['单位管理', ['主管监管单位管理', '劳务派遣公司管理', '用工单位管理', '高危企业库管理', '派遣/用工关联关系', '新业态平台企业管理', '工会管理', '企业自主服务']],
    ['系统管理', ['组织架构管理', '用户与角色权限', '系统参数配置', '数据字典', '通知公告管理', '文档管理', '接口与数据交换监管', '运行监控', '安全审计', '数据备份与恢复', '用户管理']],
    ['运营后台', ['运营数据总览', '招聘岗位审核', '企业入驻审核', '简历管理', '职位分类管理', '广告/轮播图管理', '数据统计与分析', '消息推送管理', '设备安装运维']],
    ['企业后台', ['企业仪表盘', '企业人员管理', '企业设备管理', '企业工资管理', '企业作业管理', '企业保险管理', '企业培训管理', '企业招聘管理', '企业财务管理', '企业信用报告']],
    ['便民服务', ['暖新地图', '培训课程', '法规库', '互助圈', '招聘用工市场']],
    ['新业态监管', ['新业态人员库', '平台企业管理', '职业伤害监测', '新业态培训管理']],
    ['职业病监管', ['职业病监测', '职业病预防项目', '职业健康档案']]
  ]
  const problems = []
  const topRows = rows.filter(row => row.parentId === 0).sort((left, right) => left.orderNum - right.orderNum)
  const secondRows = rows.filter(row => row.parentId !== 0)

  if (topRows.length !== expectedTree.length) {
    problems.push(`expected ${expectedTree.length} top menus but found ${topRows.length}`)
  }
  if (secondRows.length !== expectedTree.reduce((count, [, children]) => count + children.length, 0)) {
    problems.push(`expected 144 second menus but found ${secondRows.length}`)
  }

  expectedTree.forEach(([expectedTopName, expectedChildren], index) => {
    const topRow = topRows[index]
    if (!topRow) {
      problems.push(`missing top menu at order ${index + 1}: ${expectedTopName}`)
      return
    }
    if (topRow.menuName !== expectedTopName) {
      problems.push(`top order ${index + 1} expected ${expectedTopName} but found ${topRow.menuName}`)
    }
    if (topRow.menuType !== 'M') {
      problems.push(`${topRow.menuName} should be M but found ${topRow.menuType}`)
    }
    const actualChildren = secondRows
      .filter(row => row.parentId === topRow.menuId)
      .sort((left, right) => left.orderNum - right.orderNum)
      .map(row => row.menuName)
    if (actualChildren.join('\u0000') !== expectedChildren.join('\u0000')) {
      problems.push(`${expectedTopName} children expected [${expectedChildren.join(', ')}] but found [${actualChildren.join(', ')}]`)
    }
  })

  for (const row of secondRows) {
    if (row.menuType !== 'C') {
      problems.push(`${row.menuName} should be C but found ${row.menuType}`)
    }
    if (!row.component) {
      problems.push(`${row.menuName} has no component`)
    }
  }

  return problems
}
