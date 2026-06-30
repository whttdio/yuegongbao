function normalizeText(value = '') {
  return String(value || '').toLowerCase()
}

function extractText(route = {}, parentRoute = {}) {
  const title = route.meta?.title || route.title || ''
  const path = route.path || ''
  const parentTitle = parentRoute.meta?.title || parentRoute.title || ''
  const parentPath = parentRoute.path || ''
  return normalizeText(`${title} ${path} ${parentTitle} ${parentPath}`)
}

function resolveEnterprisePortalIcon(text = '') {
  if (text.includes('dashboard') || text.includes('仪表') || text.includes('cockpit')) return 'dashboard'
  if (text.includes('person') || text.includes('人员')) return 'user'
  if (text.includes('device') || text.includes('设备')) return 'build'
  if (text.includes('salary') || text.includes('工资') || text.includes('财务')) return 'money'
  if (text.includes('work') || text.includes('作业')) return 'tool'
  if (text.includes('insurance') || text.includes('保险') || text.includes('社保')) return 'skill'
  if (text.includes('training') || text.includes('培训')) return 'education'
  if (text.includes('recruit') || text.includes('招聘')) return 'job'
  if (text.includes('credit') || text.includes('信用')) return 'star'
  return 'people'
}

export function resolveMenuIcon(route = {}, parentRoute = {}) {
  const icon = route.meta?.icon || route.icon || parentRoute.meta?.icon || parentRoute.icon
  if (icon && icon !== '#') {
    return icon
  }

  const text = extractText(route, parentRoute)
  if (text.includes('enterprise-portal') || text.includes('企业后台')) {
    return resolveEnterprisePortalIcon(text)
  }
  if (text.includes('系统')) return 'system'
  if (text.includes('驾驶舱') || text.includes('cockpit') || text.includes('首页')) return 'dashboard'
  if (text.includes('监控') || text.includes('monitor')) return 'monitor'
  if (text.includes('预警') || text.includes('warning')) return 'bell'
  if (text.includes('工具') || text.includes('tool')) return 'tool'
  if (text.includes('报告') || text.includes('report')) return 'chart'
  if (text.includes('合同') || text.includes('备案') || text.includes('contract')) return 'documentation'
  if (text.includes('考勤') || text.includes('attendance')) return 'time'
  if (text.includes('工资') || text.includes('salary')) return 'money'
  if (text.includes('社保') || text.includes('social')) return 'peoples'
  if (text.includes('工伤') || text.includes('injury')) return 'skill'
  if (text.includes('平台') || text.includes('portal')) return 'link'
  if (text.includes('企业') || text.includes('enterprise')) return 'people'
  if (text.includes('人员') || text.includes('person')) return 'user'
  if (text.includes('设备') || text.includes('device')) return 'build'
  if (text.includes('招聘') || text.includes('recruit') || text.includes('岗位')) return 'job'
  if (text.includes('信用') || text.includes('credit')) return 'star'
  return 'list'
}

export default resolveMenuIcon
