import router from '@/router'
import { isResolvedRouteAvailable } from '@/utils/portal'
import { resolveDefaultStatReportRoute, resolveStatReportRoutePath } from '@/views/statReport/reportConfigs'

const MODULE_PATHS = Object.freeze({
  enterprise: '/ygb-foundation/enterprise',
  person: '/ygb-foundation/person',
  contract: '/ygb-compliance/contract',
  salaryBatch: '/ygb-compliance/salaryBatch',
  device: '/ygb-safety/device',
  warning: '/ygb-warning/warning',
  aqInsurance: '/ygb-aqins/aqInsurance',
  creditScore: '/ygb-credit/creditScore',
  statReport: resolveDefaultStatReportRoute('ygb'),
  statReportInjury: resolveStatReportRoutePath('ygb', 'injury'),
  statReportWarning: resolveStatReportRoutePath('ygb', 'warning'),
  statReportSalary: resolveStatReportRoutePath('ygb', 'salary'),
  statReportSocialTax: resolveStatReportRoutePath('ygb', 'socialTax'),
  heightWorkReport: '/ygb-techdefense/heightWorkReport'
})

function sanitizeQuery(query = {}) {
  return Object.fromEntries(
    Object.entries(query).filter(([, value]) => value !== undefined && value !== null && value !== '')
  )
}

const WORKBENCH_META_FIELDS = Object.freeze([
  'wbSourceLabel',
  'wbSourceDescription',
  'wbSourceTitle'
])

function hasPermission(proxy, permission) {
  if (!permission) {
    return true
  }
  return Boolean(proxy?.$auth?.hasPermi?.(permission))
}

function hasRouteAccess(path, query = {}) {
  if (!path) {
    return false
  }
  return isResolvedRouteAvailable(router.resolve({ path, query }))
}

function createLink({ key, title, desc, pathKey, path, permission, query = {}, disabled = false, badge = '' }) {
  return {
    key,
    title,
    desc,
    path: path || MODULE_PATHS[pathKey] || '',
    permission,
    query: sanitizeQuery(query),
    disabled,
    badge
  }
}

function filterLinks(proxy, links = []) {
  return links.filter(link => link && link.path && hasPermission(proxy, link.permission) && hasRouteAccess(link.path, link.query))
}

export function openWorkbenchLink(proxy, link) {
  if (!link?.path || link.disabled || !hasRouteAccess(link.path, link.query)) {
    return false
  }
  proxy?.$tab?.openPage(link.title, link.path, link.query)
  return true
}

export function applyWorkbenchRouteQuery(routeQuery, queryParams, fields = []) {
  if (!routeQuery || !queryParams || !Array.isArray(fields)) {
    return false
  }
  let changed = false
  fields.forEach(field => {
    const rawValue = routeQuery[field]
    const value = Array.isArray(rawValue) ? rawValue[0] : rawValue
    if (value !== undefined && value !== null && value !== '') {
      queryParams[field] = value
      changed = true
    }
  })
  return changed
}

export function getWorkbenchRouteQuery(routeQuery, fields = []) {
  if (!routeQuery || !Array.isArray(fields) || !fields.length) {
    return {}
  }
  const picked = {}
  fields.forEach(field => {
    const rawValue = routeQuery[field]
    const value = Array.isArray(rawValue) ? rawValue[0] : rawValue
    if (value !== undefined && value !== null && value !== '') {
      picked[field] = value
    }
  })
  return sanitizeQuery(picked)
}

export function stripWorkbenchRouteQuery(routeQuery, fields = []) {
  if (!routeQuery) {
    return {}
  }
  const nextQuery = { ...routeQuery }
  ;[...(fields || []), ...WORKBENCH_META_FIELDS].forEach(field => {
    delete nextQuery[field]
  })
  return sanitizeQuery(nextQuery)
}

export function buildWorkbenchContext(routeQuery, options = {}) {
  const {
    fields = [],
    fieldLabels = {},
    fieldFormatters = {},
    sourceLabel,
    title,
    description
  } = options
  const filters = getWorkbenchRouteQuery(routeQuery, fields)
  const entries = Object.entries(filters)
  if (!entries.length) {
    return null
  }
  const routeMeta = getWorkbenchRouteQuery(routeQuery, WORKBENCH_META_FIELDS)
  return {
    sourceLabel: routeMeta.wbSourceLabel || sourceLabel || inferWorkbenchSource(filters),
    title: routeMeta.wbSourceTitle || title || '已按工作台上下文带入筛选条件',
    description: routeMeta.wbSourceDescription || description || '当前页面保留了上游工作台带入的业务范围，便于继续处理。',
    filters,
    tags: entries.map(([key, value]) => ({
      key,
      label: fieldLabels[key] || key,
      value: typeof fieldFormatters[key] === 'function' ? fieldFormatters[key](value, filters) : value
    }))
  }
}

function inferWorkbenchSource(filters = {}) {
  if (filters.personId) {
    return '人员工作台'
  }
  if (filters.dispatchEnterpriseId || filters.enterpriseId || filters.enterpriseName) {
    return '企业工作台'
  }
  if (filters.regionCode) {
    return '区域工作台'
  }
  return '业务工作台'
}

export function buildEnterpriseWorkbenchLinks({ proxy, enterprise, portalCode = 'ygb', roleView = 'ygb' } = {}) {
  const enterpriseId = enterprise?.enterpriseId
  const regionCode = enterprise?.regionCode
  const enterpriseType = String(enterprise?.enterpriseType || '')
  const hasEnterprise = Boolean(enterpriseId)
  const isDispatch = enterpriseType === '1'

  const disabled = !hasEnterprise
  const commonEnterpriseLinks = [
    createLink({
      key: 'person',
      title: '人员管理',
      desc: '联动查看当前企业下的人员归属、持证和参保底册。',
      pathKey: 'person',
      permission: 'ygb:person:query',
      query: { enterpriseId, regionCode },
      disabled
    }),
    createLink({
      key: 'device',
      title: '设备管理',
      desc: '继续核对设备建档、人员授权和在线回写记录。',
      pathKey: 'device',
      permission: 'ygb:device:query',
      query: { enterpriseId, regionCode },
      disabled
    }),
    createLink({
      key: 'warning',
      title: '预警中心',
      desc: '查看当前企业对应的预警工单和闭环进度。',
      pathKey: 'warning',
      permission: 'ygb:warning:query',
      query: { enterpriseId, regionCode },
      disabled
    }),
    createLink({
      key: 'heightWorkReport',
      title: '高处作业报备',
      desc: '回看当前企业的高处作业报备、结束登记和凭证留痕。',
      pathKey: 'heightWorkReport',
      permission: 'ygb:heightWorkReport:query',
      query: { enterpriseId, regionCode },
      disabled
    })
  ]

  if (portalCode === 'azb') {
    if (roleView === 'bank') {
      return filterLinks(proxy, [
        createLink({
          key: 'salaryBatch',
          title: '工资批次',
          desc: '从企业维度回看工资监管主链和代发批次。',
          pathKey: 'salaryBatch',
          permission: 'ygb:salaryBatch:query',
          query: { dispatchEnterpriseId: isDispatch ? enterpriseId : undefined },
          disabled: !isDispatch
        }),
        createLink({
          key: 'creditScore',
          title: '信用评价',
          desc: '联动查看企业信用评分和风险画像结果。',
          pathKey: 'creditScore',
          permission: 'ygb:creditScore:query',
          query: { enterpriseId, regionCode },
          disabled
        }),
        createLink({
          key: 'statReport',
          title: '统计报表',
          desc: '直接进入统计报表继续做区域和企业只读协同。',
          path: resolveStatReportRoutePath('azb', 'warning'),
          permission: 'azb:statReport:warning:query',
          query: { regionCode },
          disabled: !regionCode
        })
      ])
    }

    if (roleView === 'insurer') {
      return filterLinks(proxy, [
        createLink({
          key: 'aqInsurance',
          title: '安责险管理',
          desc: '联动查看保单、预防资金和协同服务归属。',
          pathKey: 'aqInsurance',
          permission: 'ygb:aqInsurance:query',
          query: { enterpriseId, regionCode },
          disabled
        }),
        createLink({
          key: 'creditScore',
          title: '信用评价',
          desc: '核对企业信用结果是否影响保险协同判断。',
          pathKey: 'creditScore',
          permission: 'ygb:creditScore:query',
          query: { enterpriseId, regionCode },
          disabled
        }),
        createLink({
          key: 'statReport',
          title: '统计报表',
          desc: '继续回看区域统计口径和只读协同结果。',
          path: resolveStatReportRoutePath('azb', 'warning'),
          permission: 'azb:statReport:warning:query',
          query: { regionCode },
          disabled: !regionCode
        }),
        createLink({
          key: 'heightWorkReport',
          title: '高处作业报备',
          desc: '排查当前企业高危作业主体是否挂载准确。',
          pathKey: 'heightWorkReport',
          permission: 'ygb:heightWorkReport:query',
          query: { enterpriseId, regionCode },
          disabled
        })
      ])
    }

    return filterLinks(proxy, [
      commonEnterpriseLinks[0],
      commonEnterpriseLinks[1],
      commonEnterpriseLinks[2],
      commonEnterpriseLinks[3],
      createLink({
        key: 'aqInsurance',
        title: '安责险管理',
        desc: '结合企业主体继续核查保单和预防资金联动。',
        pathKey: 'aqInsurance',
        permission: 'ygb:aqInsurance:query',
        query: { enterpriseId, regionCode },
        disabled
      })
    ])
  }

  return filterLinks(proxy, [
    commonEnterpriseLinks[0],
    isDispatch
      ? createLink({
          key: 'contract',
          title: '合同备案',
          desc: '直接承接当前派遣主体下的合同备案与协议补录。',
          pathKey: 'contract',
          permission: 'ygb:contract:query',
          query: { dispatchEnterpriseId: enterpriseId },
          disabled
        })
      : null,
    isDispatch
      ? createLink({
          key: 'salaryBatch',
          title: '工资批次',
          desc: '继续推进当前派遣主体对应的工资监管批次。',
          pathKey: 'salaryBatch',
          permission: 'ygb:salaryBatch:query',
          query: { dispatchEnterpriseId: enterpriseId },
          disabled
        })
      : null,
    commonEnterpriseLinks[1],
    commonEnterpriseLinks[2],
    commonEnterpriseLinks[3]
  ])
}

export function buildPersonWorkbenchLinks({ proxy, person, portalCode = 'ygb', roleView = 'ygb' } = {}) {
  const personId = person?.personId
  const enterpriseId = person?.enterpriseId
  const regionCode = person?.regionCode
  const workerType = String(person?.workerType || '')
  const hasPerson = Boolean(personId)
  const hasEnterprise = Boolean(enterpriseId)

  const personDisabled = !hasPerson
  const enterpriseDisabled = !hasEnterprise

  if (portalCode === 'azb') {
    if (roleView === 'bank') {
      return filterLinks(proxy, [
        createLink({
          key: 'salaryBatch',
          title: '工资批次',
          desc: '从当前人员归属继续回看工资监管批次。',
          pathKey: 'salaryBatch',
          permission: 'ygb:salaryBatch:query',
          query: { dispatchEnterpriseId: workerType === '1' ? enterpriseId : undefined },
          disabled: workerType !== '1' || enterpriseDisabled
        }),
        createLink({
          key: 'creditScore',
          title: '信用评价',
          desc: '联动查看所属企业信用评分和画像。',
          pathKey: 'creditScore',
          permission: 'ygb:creditScore:query',
          query: { enterpriseId, regionCode },
          disabled: enterpriseDisabled
        }),
        createLink({
          key: 'statReport',
          title: '统计报表',
          desc: '继续回看区域口径下的人员和企业统计结果。',
          path: resolveStatReportRoutePath('azb', 'warning'),
          permission: 'azb:statReport:warning:query',
          query: { regionCode },
          disabled: !regionCode
        })
      ])
    }

    if (roleView === 'insurer') {
      return filterLinks(proxy, [
        createLink({
          key: 'aqInsurance',
          title: '安责险管理',
          desc: '回看当前人员所属企业的安责险投保与覆盖情况。',
          pathKey: 'aqInsurance',
          permission: 'ygb:aqInsurance:query',
          query: { enterpriseId, regionCode },
          disabled: enterpriseDisabled
        }),
        createLink({
          key: 'heightWorkReport',
          title: '高处作业报备',
          desc: '核查当前人员所在企业的高处作业报备链路。',
          pathKey: 'heightWorkReport',
          permission: 'ygb:heightWorkReport:query',
          query: { enterpriseId, regionCode },
          disabled: enterpriseDisabled
        }),
        createLink({
          key: 'creditScore',
          title: '信用评价',
          desc: '联动查看所属企业信用结果是否影响保险协同。',
          pathKey: 'creditScore',
          permission: 'ygb:creditScore:query',
          query: { enterpriseId, regionCode },
          disabled: enterpriseDisabled
        })
      ])
    }

    return filterLinks(proxy, [
      createLink({
        key: 'device',
        title: '设备管理',
        desc: '继续核对当前人员的设备授权和现场准入记录。',
        pathKey: 'device',
        permission: 'ygb:device:query',
        query: { enterpriseId, personId, regionCode },
        disabled: personDisabled
      }),
      createLink({
        key: 'heightWorkReport',
        title: '高处作业报备',
        desc: '继续回看高处作业报备和现场作业留痕。',
        pathKey: 'heightWorkReport',
        permission: 'ygb:heightWorkReport:query',
        query: { enterpriseId, regionCode },
        disabled: enterpriseDisabled
      }),
      createLink({
        key: 'warning',
        title: '预警中心',
        desc: '联动查看所属企业相关预警工单和处置进度。',
        pathKey: 'warning',
        permission: 'ygb:warning:query',
        query: { enterpriseId, regionCode },
        disabled: enterpriseDisabled
      })
    ])
  }

  return filterLinks(proxy, [
    createLink({
      key: 'contract',
      title: '合同备案',
      desc: '直接查看当前人员对应的合同备案和协议归属。',
      pathKey: 'contract',
      permission: 'ygb:contract:query',
      query: { personId },
      disabled: personDisabled
    }),
    workerType === '1'
      ? createLink({
          key: 'salaryBatch',
          title: '工资批次',
          desc: '回看当前派遣人员所属主体的工资监管批次。',
          pathKey: 'salaryBatch',
          permission: 'ygb:salaryBatch:query',
          query: { dispatchEnterpriseId: enterpriseId },
          disabled: enterpriseDisabled
        })
      : null,
    createLink({
      key: 'device',
      title: '设备管理',
      desc: '核对当前人员设备授权、证书校验和现场准入情况。',
      pathKey: 'device',
      permission: 'ygb:device:query',
      query: { enterpriseId, personId, regionCode },
      disabled: personDisabled
    }),
    createLink({
      key: 'warning',
      title: '预警中心',
      desc: '联动查看所属企业下的预警工单与整改闭环。',
      pathKey: 'warning',
      permission: 'ygb:warning:query',
      query: { enterpriseId, regionCode },
      disabled: enterpriseDisabled
    }),
    createLink({
      key: 'heightWorkReport',
      title: '高处作业报备',
      desc: '继续回看所属企业高处作业报备和凭证留痕。',
      pathKey: 'heightWorkReport',
      permission: 'ygb:heightWorkReport:query',
      query: { enterpriseId, regionCode },
      disabled: enterpriseDisabled
    })
  ])
}
