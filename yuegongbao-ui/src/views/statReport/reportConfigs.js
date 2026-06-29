import { formatMetricRate, statReportRegionNameMap } from '@/views/statReport/useStatReportPage'

export const STAT_REPORT_TYPE_KEYS = Object.freeze({
  injury: 'INJURY_RATE',
  warning: 'WARNING_OVERVIEW',
  salary: 'SALARY_PAYMENT',
  socialTax: 'SOCIAL_TAX',
  employment: 'EMPLOYMENT',
  attendance: 'ATTENDANCE',
  social: 'SOCIAL',
  tax: 'TAX',
  aqInsurance: 'AQ_INSURANCE',
  newform: 'NEWFORM',
  occupation: 'OCCUPATION',
  union: 'UNION_SUPERVISION',
  custom: 'CUSTOM',
  device: 'DEVICE_STATS',
  expansion: 'EXPANSION_REDUCTION',
  rectification: 'SPECIAL_RECTIFICATION'
})

export const statReportSourceModuleOptions = [
  { label: '社保监管', value: 'SOCIAL' },
  { label: '税务监管', value: 'TAX' },
  { label: '扩面减损', value: 'EXPANSION' },
  { label: '专项治理', value: 'SPECIAL' },
  { label: '设备预警', value: 'DEVICE' },
  { label: '工伤监管', value: 'INJURY' },
  { label: '安责险', value: 'AQINS' },
  { label: '新业态', value: 'NEWFORM' },
  { label: '职业病', value: 'OCCUPATION' },
  { label: '工会监督', value: 'UNION' }
]

export const statReportRiskCategoryOptions = [
  { label: '社保基数异常', value: 'SOCIAL_ABNORMAL' },
  { label: '个税比对异常', value: 'TAX_ABNORMAL' },
  { label: '漏保清单', value: 'UNINSURED' },
  { label: '用工比例黄警', value: 'EMPLOYMENT_YELLOW' },
  { label: '用工比例红警', value: 'EMPLOYMENT_RED' },
  { label: '假外包疑似', value: 'FAKE_OUTSOURCING' },
  { label: '安责险理赔监控', value: 'AQ_CLAIM' },
  { label: '新业态职业伤害', value: 'NEWFORM_INJURY' },
  { label: '职业病预警', value: 'OCCUPATION_WARNING' },
  { label: '工会投诉协同', value: 'UNION_CASE' }
]

const portalMeta = Object.freeze({
  ygb: {
    eyebrow: '统计月报',
    surfaceTitle: '用工保障月报台账',
    description: '按固定月报类型拆分查询、生成、导出与打印链路。',
    primaryText: '#0f5ea8',
    accent: 'rgba(15, 94, 168, 0.10)',
    soft: '#f4f8fc'
  },
  azb: {
    eyebrow: '治理月报',
    surfaceTitle: '安责保治理月报',
    description: '围绕治理、压降和联动复核拆分独立月报入口。',
    primaryText: '#0b6b78',
    accent: 'rgba(11, 107, 120, 0.10)',
    soft: '#f3fafb'
  }
})

function regionCell(row) {
  return statReportRegionNameMap[row.regionCode] || row.regionCode || '-'
}

function timeCell(row, { parseTime }) {
  return row.generatedTime ? parseTime(row.generatedTime, '{y}-{m}-{d} {h}:{i}:{s}') : '-'
}

function rateCell(row) {
  return formatMetricRate(row.metricRate, row.reportCode)
}

function createTableColumns(metricCountLabel, metricAmountLabel, metricRateLabel) {
  return [
    { key: 'statMonth', label: '统计月份', prop: 'statMonth', width: 110 },
    { key: 'reportName', label: '月报名称', prop: 'reportName', minWidth: 160 },
    { key: 'metricCount', label: metricCountLabel, prop: 'metricCount', width: 110 },
    { key: 'metricAmount', label: metricAmountLabel, prop: 'metricAmount', width: 120 },
    { key: 'metricRate', label: metricRateLabel, width: 110, formatter: row => rateCell(row) },
    { key: 'reportStatus', label: '状态', prop: 'reportStatus', width: 100, type: 'status' },
    { key: 'generatedTime', label: '生成时间', width: 180, formatter: (row, helpers) => timeCell(row, helpers) }
  ]
}

const reportTypeMeta = Object.freeze({
  injury: {
    reportCode: STAT_REPORT_TYPE_KEYS.injury,
    title: '工伤发生率月报',
    description: '聚焦工伤事件、参保样本和区域发生率。',
    printTitle: '工伤发生率月报',
    queryFields: [
      { kind: 'text', key: 'keyword', label: '区域关键字', placeholder: '区域编码或名称' },
      { kind: 'range', label: '工伤事件数', minKey: 'minMetricCount', maxKey: 'maxMetricCount' }
    ],
    summaryMetrics: [
      { key: 'metricCount', label: '工伤事件', unit: '起' },
      { key: 'metricAmount', label: '参保样本', unit: '人' },
      { key: 'metricRate', label: '平均发生率', unit: '%', isRate: true }
    ],
    tableColumns: [
      { key: 'statMonth', label: '统计月份', prop: 'statMonth', width: 110 },
      { key: 'regionCode', label: '区域', width: 150, formatter: row => regionCell(row) },
      { key: 'metricCount', label: '工伤事件数', prop: 'metricCount', width: 110 },
      { key: 'metricAmount', label: '参保样本', prop: 'metricAmount', width: 110 },
      { key: 'metricRate', label: '发生率', width: 110, formatter: row => rateCell(row) },
      { key: 'reportStatus', label: '状态', prop: 'reportStatus', width: 100, type: 'status' },
      { key: 'generatedTime', label: '生成时间', width: 180, formatter: (row, helpers) => timeCell(row, helpers) }
    ]
  },
  warning: {
    reportCode: STAT_REPORT_TYPE_KEYS.warning,
    title: '预警治理月报',
    description: '聚焦预警来源、闭环情况和治理效率。',
    printTitle: '预警治理月报',
    queryFields: [
      { kind: 'select', key: 'sourceModule', label: '来源模块', options: statReportSourceModuleOptions },
      { kind: 'range', label: '闭环率', minKey: 'minMetricRate', maxKey: 'maxMetricRate', suffix: '%' }
    ],
    summaryMetrics: [
      { key: 'metricCount', label: '预警总量', unit: '条' },
      { key: 'metricAmount', label: '闭环数量', unit: '条' },
      { key: 'metricRate', label: '平均闭环率', unit: '%', isRate: true }
    ],
    tableColumns: createTableColumns('预警数量', '闭环数量', '闭环率')
  },
  salary: {
    reportCode: STAT_REPORT_TYPE_KEYS.salary,
    title: '工资发放月报',
    description: '聚焦企业批次、覆盖人数、实发金额和发放成功率。',
    printTitle: '工资发放月报',
    queryFields: [
      { kind: 'text', key: 'keyword', label: '企业关键字', placeholder: '企业名称' },
      { kind: 'text', key: 'batchNo', label: '批次号', placeholder: '批次号关键字' },
      { kind: 'range', label: '成功率', minKey: 'minMetricRate', maxKey: 'maxMetricRate', suffix: '%' }
    ],
    summaryMetrics: [
      { key: 'metricCount', label: '覆盖人数', unit: '人次' },
      { key: 'metricAmount', label: '实发金额', unit: '元' },
      { key: 'metricRate', label: '平均成功率', unit: '%', isRate: true }
    ],
    tableColumns: createTableColumns('覆盖人数', '实发金额', '成功率')
  },
  socialTax: {
    reportCode: STAT_REPORT_TYPE_KEYS.socialTax,
    title: '社保税务联动月报',
    description: '聚焦联动风险分类、风险数量和风险率。',
    printTitle: '社保税务联动月报',
    queryFields: [
      { kind: 'select', key: 'riskCategory', label: '风险分类', options: statReportRiskCategoryOptions },
      { kind: 'range', label: '风险数量', minKey: 'minMetricCount', maxKey: 'maxMetricCount' }
    ],
    summaryMetrics: [
      { key: 'metricCount', label: '风险数量', unit: '条' },
      { key: 'metricAmount', label: '风险汇总', unit: '条' },
      { key: 'metricRate', label: '平均风险率', unit: '%', isRate: true }
    ],
    tableColumns: createTableColumns('风险数量', '风险汇总', '风险率')
  },
  employment: {
    reportCode: STAT_REPORT_TYPE_KEYS.employment,
    title: '用工月报',
    description: '聚焦用工结构、比例风险和企业覆盖。',
    printTitle: '用工月报',
    queryFields: [
      { kind: 'text', key: 'keyword', label: '企业关键字', placeholder: '企业名称' },
      { kind: 'range', label: '用工人数', minKey: 'minMetricCount', maxKey: 'maxMetricCount' }
    ],
    summaryMetrics: [
      { key: 'metricCount', label: '用工人数', unit: '人' },
      { key: 'metricAmount', label: '正式用工', unit: '人' },
      { key: 'metricRate', label: '平均比例', unit: '%', isRate: true }
    ],
    tableColumns: createTableColumns('用工人数', '正式用工', '平均比例')
  },
  attendance: {
    reportCode: STAT_REPORT_TYPE_KEYS.attendance,
    title: '考勤月报',
    description: '聚焦考勤归集、通过率和企业归档情况。',
    printTitle: '考勤月报',
    queryFields: [
      { kind: 'text', key: 'keyword', label: '企业关键字', placeholder: '企业名称' },
      { kind: 'range', label: '通过率', minKey: 'minMetricRate', maxKey: 'maxMetricRate', suffix: '%' }
    ],
    summaryMetrics: [
      { key: 'metricCount', label: '归集人数', unit: '人' },
      { key: 'metricAmount', label: '总工时', unit: '小时' },
      { key: 'metricRate', label: '通过率', unit: '%', isRate: true }
    ],
    tableColumns: createTableColumns('归集人数', '总工时', '通过率')
  },
  social: {
    reportCode: STAT_REPORT_TYPE_KEYS.social,
    title: '社保月报',
    description: '聚焦社保缺费、正常缴纳和企业覆盖。',
    printTitle: '社保月报',
    queryFields: [
      { kind: 'text', key: 'keyword', label: '企业关键字', placeholder: '企业名称' },
      { kind: 'range', label: '正常率', minKey: 'minMetricRate', maxKey: 'maxMetricRate', suffix: '%' }
    ],
    summaryMetrics: [
      { key: 'metricCount', label: '缺费人数', unit: '人' },
      { key: 'metricAmount', label: '缴纳金额', unit: '元' },
      { key: 'metricRate', label: '正常率', unit: '%', isRate: true }
    ],
    tableColumns: createTableColumns('缺费人数', '缴纳金额', '正常率')
  },
  tax: {
    reportCode: STAT_REPORT_TYPE_KEYS.tax,
    title: '税务月报',
    description: '聚焦税务比对、异常率和企业联动结果。',
    printTitle: '税务月报',
    queryFields: [
      { kind: 'text', key: 'keyword', label: '企业关键字', placeholder: '企业名称' },
      { kind: 'range', label: '异常率', minKey: 'minMetricRate', maxKey: 'maxMetricRate', suffix: '%' }
    ],
    summaryMetrics: [
      { key: 'metricCount', label: '比对人数', unit: '人' },
      { key: 'metricAmount', label: '差异金额', unit: '元' },
      { key: 'metricRate', label: '异常率', unit: '%', isRate: true }
    ],
    tableColumns: createTableColumns('比对人数', '差异金额', '异常率')
  },
  aqInsurance: {
    reportCode: STAT_REPORT_TYPE_KEYS.aqInsurance,
    title: '安责险月报',
    description: '聚焦保单、预防费和理赔监测数据。',
    printTitle: '安责险月报',
    queryFields: [
      { kind: 'text', key: 'keyword', label: '企业关键字', placeholder: '企业名称' },
      { kind: 'range', label: '覆盖率', minKey: 'minMetricRate', maxKey: 'maxMetricRate', suffix: '%' }
    ],
    summaryMetrics: [
      { key: 'metricCount', label: '保单数量', unit: '条' },
      { key: 'metricAmount', label: '预防费余额', unit: '元' },
      { key: 'metricRate', label: '覆盖率', unit: '%', isRate: true }
    ],
    tableColumns: createTableColumns('保单数量', '预防费余额', '覆盖率')
  },
  newform: {
    reportCode: STAT_REPORT_TYPE_KEYS.newform,
    title: '新业态月报',
    description: '聚焦新业态人员参保、平台企业和培训情况。',
    printTitle: '新业态月报',
    queryFields: [
      { kind: 'text', key: 'keyword', label: '关键字', placeholder: '平台或企业' },
      { kind: 'range', label: '异常率', minKey: 'minMetricRate', maxKey: 'maxMetricRate', suffix: '%' }
    ],
    summaryMetrics: [
      { key: 'metricCount', label: '人员数量', unit: '人' },
      { key: 'metricAmount', label: '月收入合计', unit: '元' },
      { key: 'metricRate', label: '异常率', unit: '%', isRate: true }
    ],
    tableColumns: createTableColumns('人员数量', '月收入合计', '异常率')
  },
  occupation: {
    reportCode: STAT_REPORT_TYPE_KEYS.occupation,
    title: '职业病月报',
    description: '聚焦职业病发病、高风险企业和预警组合。',
    printTitle: '职业病月报',
    queryFields: [
      { kind: 'text', key: 'keyword', label: '行业关键字', placeholder: '行业或区域' },
      { kind: 'range', label: '预警率', minKey: 'minMetricRate', maxKey: 'maxMetricRate', suffix: '%' }
    ],
    summaryMetrics: [
      { key: 'metricCount', label: '发病人数', unit: '人' },
      { key: 'metricAmount', label: '发病率', unit: '‰' },
      { key: 'metricRate', label: '预警率', unit: '%', isRate: true }
    ],
    tableColumns: createTableColumns('发病人数', '发病率', '预警率')
  },
  union: {
    reportCode: STAT_REPORT_TYPE_KEYS.union,
    title: '工会监督月报',
    description: '聚焦投诉举报、法律咨询和工会联动情况。',
    printTitle: '工会监督月报',
    queryFields: [
      { kind: 'text', key: 'keyword', label: '关键字', placeholder: '标题或企业' },
      { kind: 'range', label: '闭环率', minKey: 'minMetricRate', maxKey: 'maxMetricRate', suffix: '%' }
    ],
    summaryMetrics: [
      { key: 'metricCount', label: '工会事项', unit: '条' },
      { key: 'metricAmount', label: '已处理', unit: '条' },
      { key: 'metricRate', label: '闭环率', unit: '%', isRate: true }
    ],
    tableColumns: createTableColumns('工会事项', '已处理', '闭环率')
  },
  custom: {
    reportCode: STAT_REPORT_TYPE_KEYS.custom,
    title: '自定义报表',
    description: '承载部分自定义组合口径的统计结果。',
    printTitle: '自定义报表',
    queryFields: [
      { kind: 'text', key: 'keyword', label: '关键字', placeholder: '汇总条件' },
      { kind: 'range', label: '数量范围', minKey: 'minMetricCount', maxKey: 'maxMetricCount' }
    ],
    summaryMetrics: [
      { key: 'metricCount', label: '统计数量', unit: '条' },
      { key: 'metricAmount', label: '统计值', unit: '' },
      { key: 'metricRate', label: '统计比率', unit: '%', isRate: true }
    ],
    tableColumns: createTableColumns('统计数量', '统计值', '统计比率')
  },
  device: {
    reportCode: STAT_REPORT_TYPE_KEYS.device,
    title: '设备月报',
    description: '聚焦设备在线率、离线时长与报警排行。',
    printTitle: '设备月报',
    queryFields: [
      { kind: 'text', key: 'keyword', label: '企业关键字', placeholder: '企业名称' },
      { kind: 'range', label: '在线率', minKey: 'minMetricRate', maxKey: 'maxMetricRate', suffix: '%' }
    ],
    summaryMetrics: [
      { key: 'metricCount', label: '设备总数', unit: '台' },
      { key: 'metricAmount', label: '在线设备', unit: '台' },
      { key: 'metricRate', label: '平均在线率', unit: '%', isRate: true }
    ],
    tableColumns: createTableColumns('设备总数', '在线设备', '在线率')
  },
  expansion: {
    reportCode: STAT_REPORT_TYPE_KEYS.expansion,
    title: '扩面减损月报',
    description: '聚焦扩面人数、补缴金额与催缴完成率。',
    printTitle: '扩面减损月报',
    queryFields: [
      { kind: 'text', key: 'keyword', label: '区域关键字', placeholder: '区域编码或名称' },
      { kind: 'range', label: '扩面人数', minKey: 'minMetricCount', maxKey: 'maxMetricCount' }
    ],
    summaryMetrics: [
      { key: 'metricCount', label: '扩面人数', unit: '人' },
      { key: 'metricAmount', label: '补缴金额', unit: '元' },
      { key: 'metricRate', label: '完成率', unit: '%', isRate: true }
    ],
    tableColumns: createTableColumns('扩面人数', '补缴金额', '完成率')
  },
  rectification: {
    reportCode: STAT_REPORT_TYPE_KEYS.rectification,
    title: '专项整治月报',
    description: '汇总专项整治问题、整改进度和闭环结果。',
    printTitle: '专项整治月报',
    queryFields: [
      { kind: 'text', key: 'keyword', label: '整治主题', placeholder: '企业、人员或问题关键字' },
      { kind: 'range', label: '问题数量', minKey: 'minMetricCount', maxKey: 'maxMetricCount' },
      { kind: 'range', label: '整改率', minKey: 'minMetricRate', maxKey: 'maxMetricRate', suffix: '%' }
    ],
    summaryMetrics: [
      { key: 'metricCount', label: '问题数量', unit: '个' },
      { key: 'metricAmount', label: '已闭环', unit: '个' },
      { key: 'metricRate', label: '整改率', unit: '%', isRate: true }
    ],
    tableColumns: createTableColumns('问题数量', '已闭环', '整改率')
  }
})

export function resolveStatReportRoutePath(portalCode, typeKey) {
  const portalSegment = portalCode === 'azb' ? 'azb-report' : 'ygb-report'
  return `/${portalSegment}/statReport/${typeKey}`
}

export function resolveDefaultStatReportTypeKey(portalCode) {
  return portalCode === 'azb' ? 'warning' : 'salary'
}

export function resolveDefaultStatReportRoute(portalCode) {
  return resolveStatReportRoutePath(portalCode, resolveDefaultStatReportTypeKey(portalCode))
}

export function resolveStatReportTypeKeyByReportCode(reportCode) {
  return Object.keys(reportTypeMeta).find(key => reportTypeMeta[key].reportCode === reportCode) || 'salary'
}

export function resolveStatReportPageConfig(portalCode = 'ygb', typeKey = 'salary') {
  const portal = portalMeta[portalCode] || portalMeta.ygb
  const report = reportTypeMeta[typeKey] || reportTypeMeta.salary
  const permissionPrefix = `${portalCode}:statReport:${typeKey}`

  return {
    portalCode,
    typeKey,
    reportCode: report.reportCode,
    title: report.title,
    description: report.description,
    eyebrow: portal.eyebrow,
    surfaceTitle: portal.surfaceTitle,
    portalDescription: portal.description,
    primaryText: portal.primaryText,
    accent: portal.accent,
    soft: portal.soft,
    queryFields: report.queryFields,
    tableColumns: report.tableColumns,
    summaryMetrics: report.summaryMetrics,
    permissionPrefix,
    printTitle: `${portal.surfaceTitle} - ${report.printTitle}`,
    routePath: resolveStatReportRoutePath(portalCode, typeKey)
  }
}
