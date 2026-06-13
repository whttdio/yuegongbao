import router from '@/router'
import { isResolvedRouteAvailable } from '@/utils/portal'
import { resolveDefaultStatReportRoute, resolveStatReportRoutePath } from '@/views/statReport/reportConfigs'

function sanitizeRouteQuery(query = {}) {
  return Object.fromEntries(
    Object.entries(query).filter(([, value]) => value !== undefined && value !== null && value !== '')
  )
}

const MODULE_LABELS = Object.freeze({
  aiReport: 'AI报告',
  aqInsurance: '安责险',
  attendanceMonthly: '考勤归集',
  attendanceRaw: '考勤上报',
  contract: '合同备案',
  creditScore: '信用评价',
  device: '设备管理',
  enterprise: '企业管理',
  heightWorkReport: '高处作业',
  injuryEvent: '工伤事件',
  person: '人员管理',
  preventionFund: '事故预防资金',
  socialBaseCompare: '社保基数比对',
  socialPayment: '社保缴费',
  statReport: '统计月报',
  statReportWarning: '预警治理月报',
  statReportInjury: '工伤发生率月报',
  statReportSalary: '工资发放月报',
  statReportSocialTax: '社保税务联动月报',
  taxCompare: '个税比对',
  uninsuredList: '漏保清单',
  warning: '预警中心'
})

export function resolvePortalModuleLabel(moduleCode) {
  if (!moduleCode) {
    return '模块'
  }
  return MODULE_LABELS[moduleCode] || moduleCode
}

export function buildPortalModulePath(portalCode, moduleCode) {
  if (!portalCode || !moduleCode) {
    return ''
  }
  if (moduleCode === 'statReport') {
    return resolveDefaultStatReportRoute(portalCode)
  }
  if (moduleCode === 'statReportWarning') {
    return resolveStatReportRoutePath(portalCode, 'warning')
  }
  if (moduleCode === 'statReportInjury') {
    return resolveStatReportRoutePath(portalCode, 'injury')
  }
  if (moduleCode === 'statReportSalary') {
    return resolveStatReportRoutePath(portalCode, 'salary')
  }
  if (moduleCode === 'statReportSocialTax') {
    return resolveStatReportRoutePath(portalCode, 'socialTax')
  }
  return `/${portalCode}/${moduleCode}`
}

function isActionRouteAvailable(path, query = {}) {
  if (!path) {
    return false
  }
  return isResolvedRouteAvailable(router.resolve({ path, query }))
}

function resolvePortalActionPath(item = {}, portalCode, moduleCode) {
  return item.path || item.routePath || item.route || buildPortalModulePath(portalCode, moduleCode)
}

export function decoratePortalExplanationItems(items = [], options = {}) {
  const {
    portalCode,
    panelTitle = '门户解释',
    panelDescription = '',
    sourceTitle = `${panelTitle}来源条件`
  } = options

  return (items || []).map((item, index) => {
    const summary = item.summary || item.explanationSummary || panelDescription || ''
    const moduleCode = item.recommendModule || item.recommendedModule || item.moduleCode || item.evidenceModule || item.evidenceSourceModule
    const moduleLabel = resolvePortalModuleLabel(moduleCode)
    const baseSourceLabel = item.sourceLabel || panelTitle
    const sourceLabel = item.dimensionName && baseSourceLabel && !baseSourceLabel.includes(item.dimensionName)
      ? `${baseSourceLabel} / ${item.dimensionName}`
      : (baseSourceLabel || `${panelTitle} / ${item.dimensionName || moduleLabel}`)
    const sourceDescription = item.sourceDescription || summary
    const path = resolvePortalActionPath(item, portalCode, moduleCode)
    const query = sanitizeRouteQuery({
      ...(item.defaultQuery || {}),
      ...(item.query || {}),
      wbSourceLabel: sourceLabel,
      wbSourceDescription: sourceDescription,
      wbSourceTitle: sourceTitle
    })

    const action = path && isActionRouteAvailable(path, query)
      ? { path, query }
      : null

    return {
      ...item,
      key: item.key || `${moduleCode || 'explanation'}-${index}`,
      summary,
      explanationSummary: item.explanationSummary || summary,
      moduleCode,
      moduleLabel,
      recommendModule: item.recommendModule || item.recommendedModule || moduleCode,
      recommendedModule: item.recommendedModule || item.recommendModule || moduleCode,
      evidenceModule: item.evidenceModule || item.evidenceSourceModule || '',
      evidenceSourceModule: item.evidenceSourceModule || item.evidenceModule || '',
      targetValue: item.targetValue !== undefined && item.targetValue !== null ? item.targetValue : item.thresholdValue,
      thresholdValue: item.thresholdValue !== undefined && item.thresholdValue !== null ? item.thresholdValue : item.targetValue,
      sourceLabel,
      sourceDescription,
      actionText: item.actionText || (action?.path ? `进入${moduleLabel}` : ''),
      action
    }
  })
}

export function resolvePortalExplanationSummary(items = [], fallback = '') {
  const matchedItem = (items || []).find(item => item && (item.summary || item.explanationSummary))
  const firstSummary = matchedItem ? (matchedItem.summary || matchedItem.explanationSummary) : ''
  return firstSummary || fallback
}

export function openPortalExplanationAction(routerInstance, action) {
  if (!routerInstance || !action?.path) {
    return false
  }
  routerInstance.push({
    path: action.path,
    query: action.query
  })
  return true
}
