<template>
  <component
    :is="homeWorkbenchComponent"
    v-bind="homeWorkbenchViewProps"
    :class="`portal-${portal.code}`"
    @open-action="openAction"
  />
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { decoratePortalExplanationItems } from '@/utils/portalExplanation'
import router from '@/router'
import { getActivePortalConfig } from '@/utils/portal'
import { isEmbeddedCockpitOverviewPath, openCockpitScreen, resolveCockpitScreenMode } from '@/utils/cockpitScreen'
import useUserStore from '@/store/modules/user'
import usePermissionStore from '@/store/modules/permission'
import { getYgbHomeAggregate, getAzbHomeAggregate } from '@/api/ygb/home'
import { getYgbCockpitDashboard, getAzbCockpitDashboard } from '@/api/ygb/cockpit'
import { createHomeBlueprints, resolveHomeRoleProfileConfig } from '@/views/homeWorkbenchConfig'
import YgbHomeWorkbench from '@/views/home/YgbHomeWorkbench.vue'
import AzbHomeWorkbench from '@/views/home/AzbHomeWorkbench.vue'

const ALL_PERMISSION = '*:*:*'
const HOME_WORKBENCH_COMPONENTS = Object.freeze({
  ygb: YgbHomeWorkbench,
  azb: AzbHomeWorkbench
})

const HOME_SUMMARY_FALLBACKS = Object.freeze({
  ygb: '围绕 530.1 办理链展示合同、考勤、工资、社保税务、预警闭环和统计归档重点，帮助经办角色先看待办、再下钻处理。',
  azb: '围绕 6.1 治理链展示风险排序、保险协同、设备安全、预警处置和区域治理重点，帮助监管协同角色先看风险、再复核闭环。'
})

const ROLE_LABELS = Object.freeze({
  admin: '超级管理员',
  ygb_enterprise_admin: '企业管理员',
  ygb_enterprise_operator: '企业经办员',
  ygb_hrss_supervisor: '人社监管员',
  ygb_emergency_supervisor: '应急监管员',
  ygb_insurer: '保险机构',
  ygb_bank: '银行协同'
})

const HOME_BLUEPRINTS = createHomeBlueprints(action)

const route = useRoute()
const portal = computed(() => getActivePortalConfig(route))
const workbench = computed(() => portal.value.workbench)
const blueprint = computed(() => HOME_BLUEPRINTS[portal.value.code] || HOME_BLUEPRINTS.ygb)

const userStore = useUserStore()
const permissionStore = usePermissionStore()

const homeLoading = ref(false)
const homeError = ref('')
const homeData = ref(createEmptyHomeData())
const portalDashboard = computed(() => portal.value.code === 'azb'
  ? (homeData.value.azbDashboard || {})
  : (homeData.value.ygbDashboard || {}))
const normalizedHomeView = computed(() => buildNormalizedHomeView(portalDashboard.value))

const routeIndex = computed(() => {
  permissionStore.routes
  const ignoredKeys = new Set(['', 'index', 'login', 'register', 'redirect', '404', '401', 'officialSite', 'ygbOfficialSite'])
  const index = new Map()

  router.getRoutes().forEach(route => {
    const normalizedPath = normalizeRoutePath(route.path)
    if (!normalizedPath || normalizedPath.includes('/:')) {
      return
    }
    const segments = normalizedPath.split('/').filter(Boolean)
    collectRouteKeys(route, segments)
      .filter(key => !ignoredKeys.has(String(key).toLowerCase()))
      .forEach(key => {
        const current = index.get(key)
        if (!current || segments.length < current.depth) {
          index.set(key, {
            path: normalizedPath,
            depth: segments.length
          })
        }
      })
  })

  return index
})

const metricCards = computed(() => normalizedHomeView.value.summaryCards)

const focusPanels = computed(() => normalizedHomeView.value.focusPanels)

const homeQueueSections = computed(() => normalizedHomeView.value.queueSections)

const activePlaybooks = computed(() => {
  const matches = blueprint.value.playbooks.filter(playbook => !playbook.fallback && matchesPlaybook(playbook))
  const baseList = matches.length ? matches : blueprint.value.playbooks.filter(playbook => playbook.fallback)
  return baseList.slice(0, 3).map(enrichPlaybook)
})
const portalExplanations = computed(() => portal.value.code === 'azb'
  ? (portalDashboard.value.azbExplanation || [])
  : (portalDashboard.value.ygbExplanation || []))
const portalExplanationTitle = computed(() => portal.value.code === 'azb' ? '6.1 治理解释' : '530.1 办理链解释')
const portalExplanationDescription = computed(() => portal.value.code === 'azb'
  ? '首页风险对象、治理排序和主下钻入口统一来自安责保解释聚合接口。'
  : '首页办理摘要、重点说明和主下钻入口统一来自粤工保解释聚合接口。')
const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: portal.value.code,
  panelTitle: portalExplanationTitle.value,
  panelDescription: portalExplanationDescription.value
}))

const homeRoleProfile = computed(() => resolveHomeRoleProfileConfig({
  portalCode: portal.value.code,
  hasRole,
  isFinanceHomepageView
}))

const homeSummaryText = computed(() => normalizedHomeView.value.homeSummary)

const currentRoleLabels = computed(() => {
  const labels = userStore.roles
    .map(role => ROLE_LABELS[role])
    .filter(Boolean)

  if (!labels.length) {
    return activePlaybooks.value.map(item => item.title).slice(0, 3)
  }

  return Array.from(new Set(labels)).slice(0, 4)
})

const workspaceSectionTitle = computed(() => homeRoleProfile.value.workspaceTitle)
const workspaceSectionHint = computed(() => homeRoleProfile.value.workspaceHint)
const queueSectionTitle = computed(() => homeRoleProfile.value.queueTitle)
const queueSectionHint = computed(() => homeRoleProfile.value.queueHint)
const homeWorkbenchComponent = computed(() => HOME_WORKBENCH_COMPONENTS[portal.value.code] || YgbHomeWorkbench)
const homeWorkbenchViewProps = computed(() => ({
  appTitle: portal.value.appTitle,
  workbench: workbench.value,
  homeLoading: homeLoading.value,
  currentRoleLabels: currentRoleLabels.value,
  homeSummaryText: homeSummaryText.value,
  homeError: homeError.value,
  metricCards: metricCards.value,
  portalExplanationTitle: portalExplanationTitle.value,
  portalExplanationDescription: portalExplanationDescription.value,
  portalExplanationItems: portalExplanationItems.value,
  workspaceSectionTitle: workspaceSectionTitle.value,
  workspaceSectionHint: workspaceSectionHint.value,
  availableQuickSections: availableQuickSections.value,
  activePlaybooks: activePlaybooks.value,
  focusPanels: focusPanels.value,
  queueSectionTitle: queueSectionTitle.value,
  queueSectionHint: queueSectionHint.value,
  homeQueueSections: homeQueueSections.value
}))

const availableQuickSections = computed(() => normalizedHomeView.value.quickSections)

watch(
  () => [portal.value.code, userStore.permissions.join('|'), userStore.roles.join('|')],
  () => {
    loadHomeData()
  },
  { immediate: true }
)

function action(moduleCode, label, desc, badge, footer) {
  return {
    key: moduleCode,
    moduleCode,
    label,
    desc,
    badge,
    footer,
    query: undefined
  }
}

function createEmptyHomeData() {
  return {
    ygbDashboard: {},
    azbDashboard: {}
  }
}

function hasPermission(permission) {
  const permissions = userStore.permissions || []
  return userStore.roles.includes('admin') || permissions.includes(ALL_PERMISSION) || permissions.includes(permission)
}

function requestIfPermi(permission, requestFactory) {
  if (!hasPermission(permission)) {
    return Promise.resolve({ data: {} })
  }
  return requestFactory()
}

async function loadHomeData() {
  if (!userStore.roles.length && !userStore.permissions.length) {
    homeData.value = createEmptyHomeData()
    homeError.value = ''
    return
  }

  homeLoading.value = true
  homeError.value = ''

  try {
    const { data, rejectedCount } = portal.value.code === 'azb'
      ? await loadAzbHomeData()
      : await loadYgbHomeData()
    homeData.value = data
    if (rejectedCount > 0) {
      homeError.value = '首页部分聚合数据加载失败，当前仅展示已成功返回的数据。'
    }
  } catch (error) {
    homeData.value = createEmptyHomeData()
    homeError.value = '首页聚合数据加载失败，请稍后重试。'
  } finally {
    homeLoading.value = false
  }
}

async function loadYgbHomeData() {
  const results = await Promise.allSettled([
    requestIfPermi('ygb:cockpit:list', () => getYgbHomeAggregate({})),
    requestIfPermi('ygb:cockpit:list', () => getYgbCockpitDashboard({}))
  ])
  const ygbDashboard = mergeDashboardData(pickResultData(results[1]), pickResultData(results[0]))
  return {
    data: {
      ygbDashboard
    },
    rejectedCount: countRejected(results)
  }
}

async function loadAzbHomeData() {
  const results = await Promise.allSettled([
    requestIfPermi('ygb:cockpit:list', () => getAzbHomeAggregate({})),
    requestIfPermi('ygb:cockpit:list', () => getAzbCockpitDashboard({}))
  ])
  const azbDashboard = mergeDashboardData(pickResultData(results[1]), pickResultData(results[0]))
  return {
    data: {
      azbDashboard
    },
    rejectedCount: countRejected(results)
  }
}

function countRejected(results) {
  return results.filter(item => item.status === 'rejected').length
}

function pickResultData(result) {
  if (result.status !== 'fulfilled') {
    return {}
  }
  return result.value?.data || result.value || {}
}

function pickResultRows(result) {
  if (result.status !== 'fulfilled') {
    return []
  }
  return Array.isArray(result.value?.rows) ? result.value.rows : []
}

function mergeDashboardData(fallbackDashboard = {}, primaryDashboard = {}) {
  const merged = {
    ...fallbackDashboard,
    ...primaryDashboard
  }

  ;[
    'summaryCards',
    'quickSections',
    'quickActions',
    'focusPanels',
    'queueSections',
    'ygbExplanation',
    'azbExplanation'
  ].forEach(key => {
    if (!Array.isArray(primaryDashboard[key]) || primaryDashboard[key].length === 0) {
      merged[key] = Array.isArray(fallbackDashboard[key]) ? fallbackDashboard[key] : primaryDashboard[key]
    }
  })

  return merged
}

function buildNormalizedHomeView(dashboard = {}) {
  return {
    summaryCards: normalizeDashboardMetricCards(dashboard.summaryCards || []),
    quickSections: normalizeDashboardQuickSections(dashboard),
    focusPanels: normalizeDashboardFocusPanels(dashboard.focusPanels || []),
    queueSections: normalizeDashboardQueueSections(dashboard.queueSections || []),
    homeSummary: dashboard.homeSummary || resolveHomeSummaryFallback(portal.value.code)
  }
}

function resolveHomeSummaryFallback(portalCode = 'ygb') {
  return HOME_SUMMARY_FALLBACKS[portalCode] || HOME_SUMMARY_FALLBACKS.ygb
}

function normalizeDashboardQuickSections(dashboard = {}) {
  const quickSections = Array.isArray(dashboard.quickSections) ? dashboard.quickSections : []
  if (quickSections.length) {
    return quickSections
      .filter(section => section && typeof section === 'object')
      .map((section, index) => ({
        ...section,
        key: section.key || `portalSection-${index + 1}`,
        title: section.title || section.sourceLabel || dashboard.sourceLabel || '门户快捷入口',
        desc: section.desc || section.sourceDescription || dashboard.sourceDescription || '',
        actions: (Array.isArray(section.actions) ? section.actions : [])
          .filter(item => item && typeof item === 'object')
          .map(resolveAction)
          .filter(item => Boolean(item?.path))
      }))
      .filter(section => section.actions.length)
  }
  return buildDashboardQuickSections(dashboard.quickActions || [], dashboard)
}

function buildDashboardQuickSections(actionsSource = [], dashboard = {}) {
  const actions = (Array.isArray(actionsSource) ? actionsSource : [])
    .filter(item => item && typeof item === 'object')
    .map(resolveAction)
    .filter(item => Boolean(item?.path))
  if (!actions.length) {
    return []
  }
  const sourceGroups = groupActionsBySource(actions)
  if (sourceGroups.length) {
    return sourceGroups.map((group, index) => ({
      key: group.key || `portalAggregate-${index + 1}`,
      title: group.sourceLabel || dashboard.sourceLabel || '门户快捷入口',
      desc: group.sourceDescription || dashboard.sourceDescription || '',
      actions: group.actions
    }))
  }
  return [{
    key: 'portalAggregate',
    title: dashboard.sourceLabel || '门户快捷入口',
    desc: dashboard.sourceDescription || '',
    actions
  }]
}

function groupActionsBySource(actions = []) {
  const groups = new Map()

  actions.forEach((item, index) => {
    if (!item || typeof item !== 'object') {
      return
    }
    const sourceLabel = item.sourceLabel || ''
    const sourceDescription = item.sourceDescription || ''
    const sourceKey = `${sourceLabel}__${sourceDescription}` || `portalAggregate-${index + 1}`

    if (!groups.has(sourceKey)) {
      groups.set(sourceKey, {
        key: normalizeRouteKey(sourceLabel) || `portalAggregate-${groups.size + 1}`,
        sourceLabel,
        sourceDescription,
        actions: []
      })
    }

    groups.get(sourceKey).actions.push(item)
  })

  return Array.from(groups.values())
}

function normalizeDashboardMetricCards(cards = []) {
  return (Array.isArray(cards) ? cards : [])
    .filter(item => item && typeof item === 'object')
    .map((item, index) => ({
      ...item,
      topline: item.topline || item.label || item.key || `指标 ${index + 1}`,
      value: item.value ?? '--',
      label: item.displayLabel || item.note || item.desc || item.label || '',
      color: item.color || ['#0F5EA8', '#1D7A46', '#B45309', '#7C3AED'][index % 4]
    }))
}

function normalizeDashboardFocusPanels(items = []) {
  return (Array.isArray(items) ? items : []).filter(item => item && typeof item === 'object').map((item, index) => {
    const actionSource = item.action && typeof item.action === 'object' ? item.action : item
    const title = item.title || item.dimensionName || item.label || `焦点 ${index + 1}`
    const desc = item.desc || item.summary || item.sourceDescription || ''
    const moduleCode = item.moduleCode || item.recommendModule || item.evidenceModule || actionSource.moduleCode
    return {
      ...item,
      title,
      desc,
      hint: item.hint || item.sourceLabel || item.evidenceModule || '',
      actionText: item.actionText || item.status || '',
      action: resolveAction({
        ...actionSource,
        key: actionSource.key || item.key || item.dimensionName || item.title || moduleCode || `focus-${index + 1}`,
        moduleCode,
        label: actionSource.label || title,
        desc: actionSource.desc || desc,
        path: actionSource.path || item.path,
        defaultQuery: actionSource.defaultQuery || item.defaultQuery,
        query: actionSource.query || item.query,
        sourceLabel: actionSource.sourceLabel || item.sourceLabel,
        sourceDescription: actionSource.sourceDescription || item.sourceDescription
      })
    }
  }).filter(item => item && (item.title || item.desc || item.action?.path))
}

function normalizeDashboardQueueSections(sections = []) {
  return (Array.isArray(sections) ? sections : [])
    .filter(section => section && typeof section === 'object')
    .map((section, sectionIndex) => ({
      ...section,
      key: section.key || `queueSection-${sectionIndex + 1}`,
      title: section.title || `重点队列 ${sectionIndex + 1}`,
      desc: section.desc || '',
      items: (Array.isArray(section.items) ? section.items : [])
        .filter(item => item && typeof item === 'object')
        .map((item, itemIndex) => {
          const actionSource = item.action && typeof item.action === 'object' ? item.action : item
          const title = item.title || item.label || `队列项 ${itemIndex + 1}`
          const desc = item.desc || item.summary || item.sourceDescription || ''
          return {
            ...item,
            key: item.key || `${section.key || `queueSection-${sectionIndex + 1}`}-item-${itemIndex + 1}`,
            title,
            desc,
            status: item.status || item.actionText || '',
            hint: item.hint || item.sourceDescription || '',
            action: resolveAction({
              ...actionSource,
              key: actionSource.key || item.key || `${section.key || `queueSection-${sectionIndex + 1}`}-item-${itemIndex + 1}`,
              label: actionSource.label || title,
              desc: actionSource.desc || desc,
              path: actionSource.path || item.path,
              moduleCode: actionSource.moduleCode || item.moduleCode,
              defaultQuery: actionSource.defaultQuery || item.defaultQuery,
              query: actionSource.query || item.query,
              sourceLabel: actionSource.sourceLabel || item.sourceLabel,
              sourceDescription: actionSource.sourceDescription || item.sourceDescription
            })
          }
        })
        .filter(item => item && (item.title || item.desc || item.action?.path))
    }))
    .filter(section => section.items.length)
}

function enrichPlaybook(playbook) {
  return {
    ...playbook,
    liveSummary: playbook.summary,
    actions: (playbook.focusModules || [])
      .map(moduleCode => resolveAction(action(moduleCode, resolveModuleLabel(moduleCode), '', '推荐入口', '')))
      .filter(item => Boolean(item.path))
  }
}

function matchesPlaybook(playbook) {
  const hasRoleMatch = (playbook.roles || []).some(role => hasRole(role))
  const hasModuleMatch = (playbook.matchModules || []).some(moduleCode => hasModuleAccess(moduleCode))
  return hasRoleMatch || hasModuleMatch
}

function hasRole(role) {
  return userStore.roles.includes('admin') || userStore.roles.includes(role)
}

function isFinanceHomepageView() {
  if (hasRole('ygb_enterprise_admin') || hasRole('ygb_enterprise_operator') || hasRole('ygb_hrss_supervisor')) {
    return false
  }
  const permissions = userStore.permissions || []
  const financePrefixes = ['ygb:salaryBatch', 'ygb:salaryDetail', 'ygb:socialPayment', 'ygb:socialBaseCompare', 'ygb:taxCompare']
  return permissions.some(permission => financePrefixes.some(prefix => permission.startsWith(prefix)))
}

function hasModuleAccess(moduleCode) {
  return Boolean(resolveModulePath(moduleCode))
}

function resolveAction(actionItem) {
  if (!actionItem || typeof actionItem !== 'object') {
    return {
      key: 'invalid-action',
      label: '',
      desc: '',
      path: null,
      query: undefined,
      liveText: ''
    }
  }
  const mergedQuery = enrichWorkbenchQuery(actionItem)
  return {
    ...actionItem,
    path: actionItem.path || resolveModulePath(actionItem.moduleCode),
    query: sanitizeRouteQuery(mergedQuery),
    liveText: actionItem.liveText || actionItem.sourceDescription || actionItem.desc || actionItem.footer || ''
  }
}

function resolveModulePath(moduleCode) {
  return routeIndex.value.get(moduleCode)?.path || null
}

function resolveModuleLabel(moduleCode) {
  for (const group of blueprint.value.quickSections) {
    const matched = group.actions.find(item => item.moduleCode === moduleCode)
    if (matched) {
      return matched.label
    }
  }
  return moduleCode
}

function openAction(actionItem) {
  if (!actionItem?.path) {
    return
  }
  if (isEmbeddedCockpitOverviewPath(actionItem.path)) {
    openCockpitScreen(resolveCockpitScreenMode('', actionItem.path, portal.value.code), router)
    return
  }
  router.push({
    path: actionItem.path,
    query: sanitizeRouteQuery(actionItem.query)
  })
}

function sanitizeRouteQuery(query) {
  if (!query || typeof query !== 'object') {
    return undefined
  }
  const entries = Object.entries(query).filter(([, value]) => value !== undefined && value !== null && value !== '')
  if (!entries.length) {
    return undefined
  }
  return Object.fromEntries(entries)
}

function enrichWorkbenchQuery(actionItem = {}) {
  const query = {
    ...(actionItem.defaultQuery || {}),
    ...(actionItem.query || {})
  }
  if (actionItem.sourceLabel) {
    query.wbSourceLabel = actionItem.sourceLabel
  }
  if (actionItem.sourceDescription) {
    query.wbSourceDescription = actionItem.sourceDescription
  }
  if (actionItem.label) {
    query.wbSourceTitle = `${actionItem.label}来源条件`
  }
  return query
}

/* function formatAqInsuranceStatus(value) {
  const statusMap = {
    '0': '鏈敓鏁?,
    '1': '鏈夋晥',
    '2': '鍗冲皢鍒版湡',
    '3': '宸茶繃鏈?
  }
  return statusMap[String(value ?? '')] || '--'
}

function formatPreventionFundStatus(value) {
  const statusMap = {
    '0': '寰呰鎻?,
    '1': '鍙娇鐢?,
    '2': '浣跨敤涓?,
    '3': '宸叉牳閿€'
  }
  return statusMap[String(value ?? '')] || '--'
}

function formatPreventionFundRisk(row) {
  return isLowBalanceRisk(row.accruedAmount, row.remainingAmount) ? '浣庝綑棰濋闄? : '浣欓姝ｅ父'
}

function formatCreditScoreStatus(row) {
  const colorMap = {
    RED: '绾㈢爜',
    YELLOW: '榛勭爜',
    GREEN: '缁跨爜'
  }
  const colorLabel = colorMap[row.colorCode] || row.colorCode || '--'
  return `${row.creditLevel || '--'} / ${colorLabel}`
}

function formatStatReportStatus(value) {
  const statusMap = {
    '0': '鑽夌',
    '1': '宸茬敓鎴?,
    '2': '宸插綊妗?
  }
  return statusMap[String(value ?? '')] || '--'
}

function lookupLabel(group, value) {
  if (value === undefined || value === null || value === '') {
    return '--'
  }
  return STATUS_LABELS[group]?.[value] || `${value}`
}

*/

function normalizeRoutePath(path) {
  if (!path) {
    return ''
  }
  return path.startsWith('/') ? path : `/${path}`
}

function collectRouteKeys(route, segments) {
  const keys = new Set()
  const scopedSegments = segments.filter(segment => !['ygb', 'azb'].includes(segment))

  scopedSegments.forEach(segment => {
    const normalized = normalizeRouteKey(segment)
    if (normalized) {
      keys.add(normalized)
    }
  })

  const routeName = normalizeRouteKey(route.name)
  if (routeName) {
    keys.add(routeName)
  }

  const activeMenu = route.meta?.activeMenu
  if (activeMenu) {
    normalizeRoutePath(activeMenu)
      .split('/')
      .filter(Boolean)
      .map(normalizeRouteKey)
      .filter(Boolean)
      .forEach(key => keys.add(key))
  }

  return Array.from(keys)
}

function normalizeRouteKey(value) {
  if (!value) {
    return ''
  }
  const lastSegment = String(value)
    .trim()
    .replace(/^\/+|\/+$/g, '')
    .split('/')
    .pop()
  return lastSegment
    ? lastSegment.replace(/\?.*$/, '').replace(/:.*/, '')
    : ''
}

function hasMeaningfulValue(...values) {
  return values.some(value => value !== undefined && value !== null)
}

function formatMetricValue(value) {
  if (value === undefined || value === null || value === '') {
    return '--'
  }
  return `${value}`
}

function formatPercent(value) {
  if (value === undefined || value === null || value === '') {
    return '--'
  }
  const number = Number(value)
  if (Number.isNaN(number)) {
    return '--'
  }
  return `${number.toFixed(1)}%`
}
</script>
