import { getNormalPath } from '@/utils/yuegongbao'

export const COCKPIT_SCREEN_PATHS = Object.freeze({
  ygb: '/cockpit-screen/ygb',
  azb: '/cockpit-screen/azb',
})

const COCKPIT_OVERVIEW_COMPONENTS = new Set([
  'ygb/cockpit/index',
  'azb/cockpit/index',
  'ygb/cockpit/overview/index',
  'azb/cockpit/overview/index',
])

export function resolveCockpitScreenMode(component = '', path = '', portalCode = 'ygb') {
  const normalizedComponent = String(component || '').replace(/^\/+/, '').replace(/\.vue$/, '')
  const normalizedPath = getNormalPath(String(path || ''))

  if (normalizedComponent.startsWith('azb/') || normalizedPath.includes('/azb/cockpit')) {
    return 'azb'
  }
  if (normalizedComponent.startsWith('ygb/') || normalizedPath.includes('/ygb/cockpit') || normalizedPath.includes('/cockpit')) {
    return 'ygb'
  }
  return portalCode === 'azb' ? 'azb' : 'ygb'
}

export function resolveCockpitScreenPath(mode = 'ygb') {
  return COCKPIT_SCREEN_PATHS[mode] || COCKPIT_SCREEN_PATHS.ygb
}

export function isCockpitScreenPath(path = '') {
  const normalized = getNormalPath(String(path || ''))
  return normalized === COCKPIT_SCREEN_PATHS.ygb || normalized === COCKPIT_SCREEN_PATHS.azb
}

export function isCockpitOverviewComponent(component = '') {
  const normalized = String(component || '').replace(/^\/+/, '').replace(/\.vue$/, '')
  return COCKPIT_OVERVIEW_COMPONENTS.has(normalized)
}

export function isEmbeddedCockpitOverviewPath(path = '') {
  const normalized = getNormalPath(String(path || ''))
  if (isCockpitScreenPath(normalized)) {
    return false
  }
  return /\/cockpit(\/overview)?\/?$/.test(normalized)
    || normalized.endsWith('/cockpit/index')
    || normalized.endsWith('/cockpit/overview')
}

export function applyCockpitScreenMenuMeta(route, portalCode = 'ygb') {
  if (!route) {
    return route
  }
  const component = route.component || ''
  const path = route.path || ''
  const title = String(route?.meta?.title || route?.name || '')

  const isOverview = title === '驾驶舱大屏'
    || title === '指标总览'
    || title === '驾驶舱总览'
    || isCockpitOverviewComponent(component)
    || isEmbeddedCockpitOverviewPath(path)

  if (!isOverview) {
    return route
  }

  const mode = resolveCockpitScreenMode(component, path, portalCode)
  route.meta = {
    ...(route.meta || {}),
    title: '驾驶舱大屏',
    openInNewWindow: true,
    cockpitScreenPath: resolveCockpitScreenPath(mode),
  }
  return route
}

export function openCockpitScreen(mode = 'ygb', router) {
  const path = resolveCockpitScreenPath(mode)
  if (router) {
    window.open(router.resolve(path).href, '_blank', 'noopener,noreferrer')
    return
  }
  window.open(path, '_blank', 'noopener,noreferrer')
}

export function interceptEmbeddedCockpitNavigation(to, from, router) {
  if (!router || !isEmbeddedCockpitOverviewPath(to.path)) {
    return null
  }

  const mode = resolveCockpitScreenMode('', to.path)
  openCockpitScreen(mode, router)

  if (from?.path && from.path !== to.path) {
    return false
  }

  return { path: '/index', replace: true }
}
