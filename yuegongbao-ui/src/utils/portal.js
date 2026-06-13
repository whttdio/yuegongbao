import { PORTAL_CODES, PORTAL_CONFIGS, PORTAL_STORAGE_KEY } from '@/config/portal'

export const YGB_OFFICIAL_PORTAL_ROUTE = '/portal/ygb-official'
export const YGB_OFFICIAL_PORTAL_ALIAS = '/ygbOfficialSite'
export const AZB_OFFICIAL_PORTAL_ROUTE = '/portal/official'
export const AZB_OFFICIAL_PORTAL_ALIAS = '/officialSite'
export const OFFICIAL_PORTAL_ROUTE = AZB_OFFICIAL_PORTAL_ROUTE
export const OFFICIAL_PORTAL_ALIAS = AZB_OFFICIAL_PORTAL_ALIAS
export const OFFICIAL_PORTAL_QUERY = Object.freeze({
  standalone: '1'
})

const OFFICIAL_PORTAL_ROUTES = Object.freeze({
  [PORTAL_CODES.ygb]: Object.freeze({
    route: YGB_OFFICIAL_PORTAL_ROUTE,
    alias: YGB_OFFICIAL_PORTAL_ALIAS
  }),
  [PORTAL_CODES.azb]: Object.freeze({
    route: AZB_OFFICIAL_PORTAL_ROUTE,
    alias: AZB_OFFICIAL_PORTAL_ALIAS
  })
})

const PORTAL_LOGIN_PREFIX = '/login/'
const DEFAULT_PORTAL_CODE = import.meta.env.VITE_PORTAL_CODE || PORTAL_CODES.ygb
export const AZB_ONLY_ROLE_KEYS = Object.freeze([
  'ygb_emergency_supervisor',
  'ygb_insurer',
  'ygb_bank'
])
export const FIXED_ROLE_PORTAL_RULES = Object.freeze({
  '1': Object.freeze({ allowedPortalScope: PORTAL_CODES.ygb, defaultPortalCode: PORTAL_CODES.ygb }),
  '2': Object.freeze({ allowedPortalScope: PORTAL_CODES.ygb, defaultPortalCode: PORTAL_CODES.ygb }),
  '101': Object.freeze({ allowedPortalScope: PORTAL_CODES.ygb, defaultPortalCode: PORTAL_CODES.ygb }),
  '102': Object.freeze({ allowedPortalScope: PORTAL_CODES.ygb, defaultPortalCode: PORTAL_CODES.ygb }),
  '103': Object.freeze({ allowedPortalScope: PORTAL_CODES.ygb, defaultPortalCode: PORTAL_CODES.ygb }),
  '104': Object.freeze({ allowedPortalScope: 'both', defaultPortalCode: PORTAL_CODES.ygb }),
  '105': Object.freeze({ allowedPortalScope: 'both', defaultPortalCode: PORTAL_CODES.ygb }),
  '106': Object.freeze({ allowedPortalScope: PORTAL_CODES.ygb, defaultPortalCode: PORTAL_CODES.ygb }),
  '107': Object.freeze({ allowedPortalScope: PORTAL_CODES.azb, defaultPortalCode: PORTAL_CODES.azb }),
  '108': Object.freeze({ allowedPortalScope: PORTAL_CODES.azb, defaultPortalCode: PORTAL_CODES.azb, readOnly: true }),
  '109': Object.freeze({ allowedPortalScope: PORTAL_CODES.azb, defaultPortalCode: PORTAL_CODES.azb, readOnly: true }),
  admin: Object.freeze({ allowedPortalScope: PORTAL_CODES.ygb, defaultPortalCode: PORTAL_CODES.ygb }),
  common: Object.freeze({ allowedPortalScope: PORTAL_CODES.ygb, defaultPortalCode: PORTAL_CODES.ygb }),
  ygb_province_supervisor: Object.freeze({ allowedPortalScope: PORTAL_CODES.ygb, defaultPortalCode: PORTAL_CODES.ygb }),
  ygb_city_supervisor: Object.freeze({ allowedPortalScope: PORTAL_CODES.ygb, defaultPortalCode: PORTAL_CODES.ygb }),
  ygb_county_supervisor: Object.freeze({ allowedPortalScope: PORTAL_CODES.ygb, defaultPortalCode: PORTAL_CODES.ygb }),
  ygb_enterprise_admin: Object.freeze({ allowedPortalScope: 'both', defaultPortalCode: PORTAL_CODES.ygb }),
  ygb_enterprise_operator: Object.freeze({ allowedPortalScope: 'both', defaultPortalCode: PORTAL_CODES.ygb }),
  ygb_hrss_supervisor: Object.freeze({ allowedPortalScope: PORTAL_CODES.ygb, defaultPortalCode: PORTAL_CODES.ygb }),
  ygb_emergency_supervisor: Object.freeze({ allowedPortalScope: PORTAL_CODES.azb, defaultPortalCode: PORTAL_CODES.azb }),
  ygb_insurer: Object.freeze({ allowedPortalScope: PORTAL_CODES.azb, defaultPortalCode: PORTAL_CODES.azb, readOnly: true }),
  ygb_bank: Object.freeze({ allowedPortalScope: PORTAL_CODES.azb, defaultPortalCode: PORTAL_CODES.azb, readOnly: true })
})
const ROLE_DEFAULT_PORTAL_MAP = Object.freeze(Object.fromEntries(
  Object.entries(FIXED_ROLE_PORTAL_RULES).map(([roleKey, rule]) => [roleKey, rule.defaultPortalCode])
))
const ROLE_DEFAULT_PORTAL_PRIORITY = Object.freeze([
  PORTAL_CODES.ygb,
  PORTAL_CODES.azb
])
const OFFICIAL_PORTAL_PATHS = new Set([
  YGB_OFFICIAL_PORTAL_ROUTE,
  YGB_OFFICIAL_PORTAL_ALIAS,
  'ygbOfficialSite',
  AZB_OFFICIAL_PORTAL_ROUTE,
  AZB_OFFICIAL_PORTAL_ALIAS,
  'officialSite'
])

function normalizeTargetPath(target) {
  let path = ''
  if (typeof target === 'string') {
    path = target
  } else if (target && typeof target === 'object') {
    path = target.path || ''
  }
  if (!path) {
    return ''
  }
  path = path.trim()
  if (!path.startsWith('/')) {
    path = `/${path}`
  }
  return path.replace(/\/{2,}/g, '/').replace(/\/$/, '')
}

export function normalizePortalCode(code) {
  return String(code || '').trim().toLowerCase()
}

export function isKnownPortalCode(code) {
  return Boolean(PORTAL_CONFIGS[normalizePortalCode(code)])
}

function flattenPortalScopes(rawScope) {
  if (rawScope == null || rawScope === '') {
    return []
  }
  if (Array.isArray(rawScope)) {
    return rawScope.flatMap(item => flattenPortalScopes(item))
  }
  return String(rawScope)
    .split(',')
    .map(item => normalizePortalCode(item))
    .filter(Boolean)
}

function getStoredPortalCode() {
  if (typeof window === 'undefined') {
    return ''
  }
  try {
    return normalizePortalCode(window.localStorage.getItem(PORTAL_STORAGE_KEY))
  } catch {
    return ''
  }
}

function getPortalCodeFromPathname(pathname = '') {
  const normalized = normalizeTargetPath(pathname)
  const match = normalized.match(/^\/login\/(ygb|azb)(?:\/|$)/)
  return match ? normalizePortalCode(match[1]) : ''
}

function getPortalCodeFromRoute(route) {
  if (!route || typeof route !== 'object') {
    return ''
  }
  const routeCandidates = [
    route.params?.portalCode,
    route.query?.portalCode,
    route.query?.portal,
    route.meta?.portalCode
  ]
  return resolvePortalCode(...routeCandidates, route.path)
}

export function resolvePortalCode(...candidates) {
  for (const candidate of candidates) {
    if (candidate && typeof candidate === 'object') {
      const routePortal = getPortalCodeFromRoute(candidate)
      if (isKnownPortalCode(routePortal)) {
        return routePortal
      }
    }
    const normalized = normalizePortalCode(candidate)
    if (isKnownPortalCode(normalized)) {
      return normalized
    }
    const fromPath = getPortalCodeFromPathname(typeof candidate === 'string' ? candidate : '')
    if (isKnownPortalCode(fromPath)) {
      return fromPath
    }
  }
  const pathnamePortal = typeof window !== 'undefined' ? getPortalCodeFromPathname(window.location.pathname) : ''
  if (isKnownPortalCode(pathnamePortal)) {
    return pathnamePortal
  }
  const storedPortal = getStoredPortalCode()
  if (isKnownPortalCode(storedPortal)) {
    return storedPortal
  }
  return isKnownPortalCode(DEFAULT_PORTAL_CODE) ? DEFAULT_PORTAL_CODE : PORTAL_CODES.ygb
}

export function setActivePortalCode(code, persist = true) {
  const resolvedCode = resolvePortalCode(code)
  if (!persist || typeof window === 'undefined') {
    return resolvedCode
  }
  try {
    window.localStorage.setItem(PORTAL_STORAGE_KEY, resolvedCode)
  } catch {
    // Ignore storage failures and continue with runtime portal state.
  }
  return resolvedCode
}

export function getPortalConfig(code) {
  return PORTAL_CONFIGS[resolvePortalCode(code)]
}

export function getActivePortalCode(route) {
  return route ? getPortalCodeFromRoute(route) : resolvePortalCode()
}

export function getActivePortalConfig(route) {
  return getPortalConfig(getActivePortalCode(route))
}

function normalizeRoleKeys(roles = []) {
  if (!Array.isArray(roles)) {
    return []
  }
  return roles
    .map(role => String(role || '').trim())
    .filter(Boolean)
}

function resolveFixedRolePortalRuleByKey(roleKey = '') {
  const normalizedRoleKey = String(roleKey || '').trim()
  return normalizedRoleKey ? FIXED_ROLE_PORTAL_RULES[normalizedRoleKey] || null : null
}

export function resolveFixedRolePortalRule(role = {}) {
  if (role == null) {
    return null
  }
  if (typeof role === 'string' || typeof role === 'number') {
    return resolveFixedRolePortalRuleByKey(role)
  }
  return resolveFixedRolePortalRuleByKey(role.roleId) || resolveFixedRolePortalRuleByKey(role.roleKey)
}

export function isAzbOnlyRoleSet(roles = []) {
  const roleKeys = normalizeRoleKeys(roles)
  if (!roleKeys.length) {
    return false
  }
  const hasAzbOnlyRole = roleKeys.some(role => ROLE_DEFAULT_PORTAL_MAP[role] === PORTAL_CODES.azb)
  if (!hasAzbOnlyRole) {
    return false
  }
  return roleKeys.every(role => role === 'ROLE_DEFAULT' || ROLE_DEFAULT_PORTAL_MAP[role] === PORTAL_CODES.azb)
}

export function resolveDefaultPortalCodeForRoles(roles = [], currentPortalCode = resolvePortalCode()) {
  const normalizedPortalCode = resolvePortalCode(currentPortalCode)
  const matchedPortals = []
  normalizeRoleKeys(roles).forEach(role => {
    const mappedPortalCode = ROLE_DEFAULT_PORTAL_MAP[role]
    if (mappedPortalCode && !matchedPortals.includes(mappedPortalCode)) {
      matchedPortals.push(mappedPortalCode)
    }
  })
  if (!matchedPortals.length) {
    return ''
  }
  if (matchedPortals.length > 1 && matchedPortals.includes(normalizedPortalCode)) {
    return normalizedPortalCode
  }
  return ROLE_DEFAULT_PORTAL_PRIORITY.find(code => matchedPortals.includes(code)) || matchedPortals[0]
}

export function resolvePortalRedirectForRoles(roles = [], currentPortalCode = resolvePortalCode()) {
  const normalizedPortalCode = resolvePortalCode(currentPortalCode)
  const resolvedDefaultPortalCode = resolveDefaultPortalCodeForRoles(roles, normalizedPortalCode)
  if (resolvedDefaultPortalCode && normalizedPortalCode !== resolvedDefaultPortalCode) {
    return resolvedDefaultPortalCode
  }
  return ''
}

function normalizePortalCodeList(portalCodes = []) {
  return flattenPortalScopes(portalCodes)
    .filter(code => isKnownPortalCode(code))
}

export function resolvePortalRedirect({
  currentPortalCode = resolvePortalCode(),
  allowedPortalCodes = [],
  defaultPortalCode = '',
  fallbackRoles = []
} = {}) {
  const normalizedPortalCode = resolvePortalCode(currentPortalCode)
  const normalizedAllowedPortalCodes = normalizePortalCodeList(allowedPortalCodes)
  const normalizedDefaultPortalCode = normalizePortalCode(defaultPortalCode)
  if (normalizedAllowedPortalCodes.length) {
    if (normalizedAllowedPortalCodes.includes(normalizedPortalCode)) {
      return ''
    }
    if (normalizedAllowedPortalCodes.includes(normalizedDefaultPortalCode)) {
      return normalizedDefaultPortalCode
    }
    return normalizedAllowedPortalCodes[0]
  }
  if (normalizedDefaultPortalCode) {
    return normalizedDefaultPortalCode === normalizedPortalCode ? '' : normalizedDefaultPortalCode
  }
  return resolvePortalRedirectForRoles(fallbackRoles, normalizedPortalCode)
}

export function isPortalLoginPath(target) {
  const path = normalizeTargetPath(target)
  return path === '/login' || path.startsWith(PORTAL_LOGIN_PREFIX)
}

export function resolvePortalLoginPath(code) {
  return `${PORTAL_LOGIN_PREFIX}${resolvePortalCode(code)}`
}

export function resolvePortalHomePath() {
  return '/index'
}

export function syncPortalBranding(route) {
  const portal = getActivePortalConfig(route)
  setActivePortalCode(portal.code)
  if (typeof document !== 'undefined') {
    document.title = portal.appTitle
    document.documentElement.dataset.portal = portal.code
    document.documentElement.style.setProperty('--portal-primary', portal.themeColor)
    document.documentElement.style.setProperty('--portal-surface', portal.themeSurface)
    document.documentElement.style.setProperty('--portal-accent', portal.themeAccent)
  }
  return portal
}

function extractPortalScopes(route) {
  return [
    route?.portalCode,
    route?.portalCodes,
    route?.portalScope,
    route?.meta?.portalCode,
    route?.meta?.portalCodes,
    route?.meta?.portalScope
  ].flatMap(item => flattenPortalScopes(item))
}

export function isRouteAllowedForPortal(route, portalCode = resolvePortalCode()) {
  const scopes = extractPortalScopes(route)
  if (!scopes.length) {
    return true
  }
  return scopes.some(scope => ['all', 'common', 'both'].includes(scope) || scope === portalCode)
}

export function filterPortalRoutes(routes, portalCode = resolvePortalCode()) {
  if (!Array.isArray(routes)) {
    return []
  }
  return routes
    .filter(route => isRouteAllowedForPortal(route, portalCode))
    .map(route => {
      const nextRoute = { ...route }
      if (Array.isArray(route.children) && route.children.length) {
        nextRoute.children = filterPortalRoutes(route.children, portalCode)
      }
      return nextRoute
    })
}

export function isResolvedRouteAvailable(resolvedRoute) {
  const matched = resolvedRoute?.matched || []
  if (!matched.length) {
    return false
  }
  return !matched.every(record => record.path === '/:pathMatch(.*)*')
}

export function getPortalRequestHeaders() {
  return {
    'X-Portal-Code': resolvePortalCode()
  }
}

export function isOfficialPortalPath(target) {
  return OFFICIAL_PORTAL_PATHS.has(normalizeTargetPath(target))
}

export function resolveOfficialPortalRoute(portalCode = resolvePortalCode()) {
  const resolvedCode = resolvePortalCode(portalCode)
  return OFFICIAL_PORTAL_ROUTES[resolvedCode] || OFFICIAL_PORTAL_ROUTES[PORTAL_CODES.azb]
}

export function resolveOfficialPortalHref(router, portalCode = resolvePortalCode()) {
  const { route } = resolveOfficialPortalRoute(portalCode)
  return router.resolve({
    path: route,
    query: OFFICIAL_PORTAL_QUERY
  }).href
}

export function isOfficialPortalStandalone(query = {}) {
  return String(query.standalone || '') === OFFICIAL_PORTAL_QUERY.standalone
}

export function openOfficialPortal(router, portalCode = resolvePortalCode()) {
  const portalWindow = window.open(resolveOfficialPortalHref(router, portalCode), '_blank', 'noopener,noreferrer')
  if (portalWindow) {
    portalWindow.opener = null
  }
}
