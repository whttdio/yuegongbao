import router from '@/router'
import { isResolvedRouteAvailable } from '@/utils/portal'

const LEGACY_YGB_DOCUMENT_ROUTE_ALIASES = Object.freeze({
  '/operation/deviceInspectPlan': '/operation-backend/deviceOperation',
  '/operation/deviceInstallOrder': '/operation-backend/deviceOperation',
  '/operation/deviceRepairOrder': '/operation-backend/deviceOperation',
  '/operation/enterpriseReview': '/operation-backend/enterpriseReview',
  '/operation/jobReview': '/operation-backend/jobReview',
  '/operation/message': '/operation-backend/message',
  '/operation/operationChipDispatch': '/operation-backend/deviceOperation',
  '/operation/operationJobCategory': '/operation-backend/jobCategory',
  '/operation/operationMaintenanceStats': '/operation-backend/deviceOperation',
  '/operation/operationRecruitStats': '/operation-backend/dataAnalysis',
  '/operation/resume': '/operation-backend/resume',
  '/platform/backup': '/system-management/backup',
  '/platform/document': '/system-management/document',
  '/platform/exchange': '/system-management/exchange',
  '/platform/securityAudit': '/system-management/securityAudit',
  '/workerJob': '/citizen-service/recruitMarket',
  '/ygb/aiReport': '/ai-report/report',
  '/ygb/aiReportConfig': '/ai-report/model',
  '/ygb/aiReportTask': '/ai-report/task',
  '/ygb/aqInsurance': '/aq-insurance/insurance',
  '/ygb/citizenService/lawLibrary': '/citizen-service/lawLibrary',
  '/ygb/citizenService/mutualHelp': '/citizen-service/mutualHelp',
  '/ygb/citizenService/recruitMarket': '/citizen-service/recruitMarket',
  '/ygb/citizenService/trainingCourse': '/citizen-service/trainingCourse',
  '/ygb/citizenService/warmMap': '/citizen-service/warmMap',
  '/ygb/cockpitConfig': '/cockpit/config',
  '/ygb/creditScore': '/credit-evaluation/overview',
  '/ygb/device': '/device-management/detail',
  '/ygb/employmentRatio': '/special-rectification/employmentRatio',
  '/ygb/enterprise': '/enterprise-management/employer',
  '/ygb/enterprisePortal/dashboard': '/enterprise-portal/dashboard',
  '/ygb/enterprisePortal/overview': '/enterprise-portal/dashboard',
  '/ygb/cockpitTrend': '/cockpit/trend',
  '/ygb/enterpriseRelation': '/enterprise-management/relation',
  '/ygb/expansion/collectionTracking': '/expansion-reduction/collectionTracking',
  '/ygb/injuryEvent': '/injury-supervision/event',
  '/ygb/portalContent': '/operation-backend/banner',
  '/ygb/salaryArrears': '/salary-supervision/arrears',
  '/ygb/salaryBatch': '/salary-supervision/payment',
  '/ygb/socialBaseCompare': '/social-insurance/baseCompare',
  '/ygb/socialPayment': '/social-insurance/payment',
  '/ygb/taxCompare': '/tax-supervision/personalTax',
  '/ygb/uninsuredList': '/expansion-reduction/uninsured',
  '/ygb/warning': '/warning-center/workOrder',
  '/ygb-aqins/aqInsurance': '/aq-insurance/insurance',
  '/contract': '/contract-filing/contract',
  '/ygb-compliance/contract': '/contract-filing/contract',
  '/ygb-compliance/person': '/personnel-management/person',
  '/ygb-compliance/salaryArrears': '/salary-supervision/arrears',
  '/ygb-compliance/salaryBatch': '/salary-supervision/payment',
  '/ygb-credit/creditScore': '/credit-evaluation/overview',
  '/ygb-foundation/enterpriseDispatch': '/enterprise-management/dispatch',
  '/ygb-foundation/enterpriseEmployer': '/enterprise-management/employer',
  '/ygb-foundation/enterpriseHighRisk': '/enterprise-management/highRisk',
  '/ygb-foundation/enterpriseRegulator': '/enterprise-management/regulator',
  '/ygb-foundation/enterpriseRelation': '/enterprise-management/relation',
  '/ygb-foundation/enterpriseUnion': '/enterprise-management/union',
  '/ygb-foundation/personBlacklist': '/personnel-management/blacklist',
  '/ygb-foundation/personCertificate': '/personnel-management/certificate',
  '/ygb-foundation/personExpert': '/personnel-management/expert',
  '/ygb-foundation/personHighRiskPost': '/personnel-management/highRiskPost',
  '/ygb-foundation/personRiskPost': '/personnel-management/riskPost',
  '/ygb-foundation/personTraining': '/personnel-management/training',
  '/ygb-newform/newformInjuryMonitor': '/newform-regulation/injuryMonitor',
  '/ygb-newform/newformPlatform': '/newform-regulation/platform',
  '/ygb-newform/newformTraining': '/newform-regulation/training',
  '/ygb-occupation/occupationHealthArchive': '/occupational-disease/healthArchive',
  '/ygb-occupation/occupationPrevention': '/occupational-disease/prevention',
  '/ygb-regulation/employmentRatio': '/special-rectification/employmentRatio',
  '/ygb-regulation/socialBaseCompare': '/social-insurance/baseCompare',
  '/ygb-regulation/socialPayment': '/social-insurance/payment',
  '/ygb-regulation/taxCompare': '/tax-supervision/personalTax',
  '/ygb-regulation/uninsuredList': '/expansion-reduction/uninsured',
  '/ygb-report/statReport/socialTax': '/statistical-report/social',
  '/ygb-report/statReport/warning': '/warning-center/stat',
  '/ygb-safety/device': '/device-management/detail',
  '/ygb-safety/deviceAi': '/device-management/ai',
  '/ygb-safety/deviceAttendance': '/attendance/deviceOnline',
  '/ygb-safety/deviceChip': '/device-management/chip',
  '/ygb-safety/deviceChipInventory': '/device-management/chipInventory',
  '/ygb-safety/deviceGeofence': '/device-management/geofence',
  '/ygb-safety/deviceIotCard': '/device-management/iotCard',
  '/ygb-safety/deviceUninstallAlert': '/device-management/uninstallAlert',
  '/ygb-safety/preventionTraining': '/expansion-reduction/preventionTraining',
  '/ygb-techdefense/heightWorkReport': '/enterprise-portal/work'
})

const ROUTE_ALIAS_RULES = Object.freeze([
  {
    pattern: /^\/azb\/warning$/,
    target: '/azb-warning/warning'
  },
  {
    pattern: /^\/azb\/warningRule$/,
    target: '/azb-warning/warningRule'
  },
  {
    pattern: /^\/azb\/warningStat$/,
    target: '/azb-warning/warningStat'
  },
  {
    pattern: /^\/azb\/aqInsurance$/,
    target: '/azb-aqins/aqInsurance'
  },
  {
    pattern: /^\/azb\/aqInsuranceClaim$/,
    target: '/azb-aqins/claim'
  },
  {
    pattern: /^\/azb\/creditScore$/,
    target: '/azb-credit/creditScore'
  },
  {
    pattern: /^\/azb\/heightWorkReport$/,
    target: '/azb-techdefense/heightWorkReport'
  },
  {
    pattern: /^\/azb\/aiReport$/,
    target: '/azb-ai-report/aiReport'
  },
  {
    pattern: /^\/azb\/aiReportConfig$/,
    target: '/azb-ai-report/aiReportConfig'
  },
  {
    pattern: /^\/azb\/aiReportTask$/,
    target: '/azb-ai-report/aiReportTask'
  },
  {
    pattern: /^\/azb\/aiReportSubscription$/,
    target: '/azb-ai-report/aiReportSubscription'
  },
  {
    pattern: /^\/azb-report\/statReport(?:\/.*)?$/,
    target: '/azb/statReport'
  }
])

const ROUTE_COMPATIBILITY_GROUPS = Object.freeze([
  Object.freeze(['/salary-supervision/payment', '/salary-supervision/paymentMonitor']),
  Object.freeze(['/salary-supervision/arrears', '/salary-supervision/overdueWarning']),
  Object.freeze(['/salary-supervision/account', '/salary-supervision/accountMonitor']),
  Object.freeze(['/salary-supervision/bank', '/salary-supervision/bankDistribution'])
])

export { LEGACY_YGB_DOCUMENT_ROUTE_ALIASES }

export function normalizeRoutePath(path = '') {
  const normalized = String(path || '').trim().replace(/\/{2,}/g, '/').replace(/\/$/, '')
  return normalized || '/'
}

function isRoutePathAvailable(path = '') {
  if (!path) {
    return false
  }
  try {
    return isResolvedRouteAvailable(router.resolve({ path }))
  } catch {
    return false
  }
}

function resolveCompatibleRoutePath(path = '') {
  const normalizedPath = normalizeRoutePath(path)
  const matchedGroup = ROUTE_COMPATIBILITY_GROUPS.find(group => group.includes(normalizedPath))
  if (!matchedGroup) {
    return normalizedPath
  }
  if (isRoutePathAvailable(normalizedPath)) {
    return normalizedPath
  }
  return matchedGroup.find(candidate => isRoutePathAvailable(candidate)) || normalizedPath
}

export function resolveRouteAliasPath(path = '') {
  const normalizedPath = normalizeRoutePath(path)
  const mappedPath = LEGACY_YGB_DOCUMENT_ROUTE_ALIASES[normalizedPath]
  if (mappedPath) {
    return resolveCompatibleRoutePath(mappedPath)
  }
  const matchedRule = ROUTE_ALIAS_RULES.find(rule => rule.pattern.test(normalizedPath))
  return resolveCompatibleRoutePath(matchedRule?.target || normalizedPath)
}

export function normalizeRouteTarget(target) {
  if (!target) {
    return null
  }
  if (typeof target === 'string') {
    return resolveRouteAliasPath(target)
  }
  return {
    ...target,
    path: resolveRouteAliasPath(target.path)
  }
}

export function resolveYgbDocumentRouteAlias(route) {
  const normalizedPath = normalizeRoutePath(route?.path)
  const targetPath = resolveRouteAliasPath(normalizedPath)
  if (!targetPath || targetPath === normalizedPath) {
    return null
  }
  return {
    path: targetPath,
    query: { ...(route?.query || {}) },
    hash: route?.hash,
    replace: true
  }
}
