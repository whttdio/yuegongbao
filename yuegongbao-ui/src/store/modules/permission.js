import auth from '@/plugins/auth'
import ParentView from '@/components/ParentView'
import router, { constantRoutes, dynamicRoutes } from '@/router'
import Layout from '@/layout/index.vue'
import InnerLink from '@/layout/components/InnerLink/index.vue'
import { getRouters } from '@/api/menu'
import { getNormalPath } from '@/utils/yuegongbao'
import { filterPortalRoutes, getActivePortalCode } from '@/utils/portal'

const modules = import.meta.glob('./../../views/**/*.vue')

const LEGACY_VIEW_ALIASES = Object.freeze({
  'ygb-safety/leakList/index': 'ygb/uninsuredList/index',
  'ygb-safety/leakList': 'ygb/uninsuredList/index',
  'ygb-safety/uninsuredList/index': 'ygb/uninsuredList/index',
  'ygb-safety/uninsuredList': 'ygb/uninsuredList/index',
  'ygb-regulation/leakList/index': 'ygb/uninsuredList/index',
  'ygb-regulation/leakList': 'ygb/uninsuredList/index',
  'ygb-regulation/uninsuredList': 'ygb/uninsuredList/index'
})

const LEGACY_VIEW_PREFIX_ALIASES = Object.freeze({
  'ygb-foundation/': 'ygb/',
  'ygb-compliance/': 'ygb/',
  'ygb-regulation/': 'ygb/',
  'ygb-safety/': 'ygb/',
  'ygb-warning/': 'ygb/',
  'ygb-aqins/': 'ygb/',
  'ygb-credit/': 'ygb/',
  'ygb-report/': 'ygb/',
  'ygb-techdefense/': 'ygb/'
})

const usePermissionStore = defineStore('permission', {
  state: () => ({
    routes: [],
    addRoutes: [],
    defaultRoutes: [],
    topbarRouters: [],
    sidebarRouters: []
  }),
  actions: {
    setRoutes(routes) {
      this.addRoutes = routes
      this.routes = constantRoutes.concat(routes)
    },
    setDefaultRoutes(routes) {
      this.defaultRoutes = constantRoutes.concat(routes)
    },
    setTopbarRoutes(routes) {
      this.topbarRouters = routes
    },
    setSidebarRouters(routes) {
      this.sidebarRouters = routes
    },
    generateRoutes() {
      return new Promise((resolve, reject) => {
        getRouters().then(res => {
          const portalCode = getActivePortalCode()
          const rawRoutes = filterPortalRoutes(res.data, portalCode)
          const sdata = JSON.parse(JSON.stringify(rawRoutes))
          const rdata = JSON.parse(JSON.stringify(rawRoutes))
          const defaultData = JSON.parse(JSON.stringify(rawRoutes))

          const sidebarRoutes = filterAsyncRouter(sdata)
          const rewriteRoutes = filterAsyncRouter(rdata, false, true)
          const defaultRoutes = filterAsyncRouter(defaultData)
          const asyncRoutes = filterDynamicRoutes(filterPortalRoutes(dynamicRoutes, portalCode))

          asyncRoutes.forEach(route => {
            router.addRoute(route)
          })

          this.setRoutes(rewriteRoutes)
          this.setSidebarRouters(constantRoutes.concat(sidebarRoutes))
          this.setDefaultRoutes(sidebarRoutes)
          this.setTopbarRoutes(defaultRoutes)
          resolve(rewriteRoutes)
        }).catch(error => {
          reject(error)
        })
      })
    }
  }
})

function ensureAbsolutePath(path) {
  if (!path || path.startsWith('http') || path.startsWith('/')) {
    return path
  }
  return `/${path}`
}

function filterAsyncRouter(asyncRouterMap, lastRouter = false, type = false) {
  return asyncRouterMap.filter(route => {
    route.path = ensureAbsolutePath(getNormalPath(route.path))
    normalizeEmptyContainerComponent(route, lastRouter)

    if (type && route.children) {
      route.children = filterChildren(route.children, route)
    }

    if (route.component) {
      if (route.component === 'Layout') {
        route.component = Layout
      } else if (route.component === 'ParentView') {
        route.component = ParentView
      } else if (route.component === 'InnerLink') {
        route.component = InnerLink
      } else {
        route.component = loadView(route.component)
      }
    }

    if (route.children?.length) {
      route.children = filterAsyncRouter(route.children, route, type)
    } else {
      delete route.children
      delete route.redirect
    }

    return true
  })
}

function normalizeEmptyContainerComponent(route, lastRouter = false) {
  if (route.component) {
    return
  }
  if (!route.children?.length) {
    return
  }
  route.component = lastRouter ? 'ParentView' : 'Layout'
}

function filterChildren(childrenMap, lastRouter = false) {
  let children = []
  childrenMap.forEach(el => {
    el.path = lastRouter
      ? getNormalPath(`${lastRouter.path}/${el.path}`)
      : ensureAbsolutePath(getNormalPath(el.path))
    if (el.children?.length && el.component === 'ParentView') {
      children = children.concat(filterChildren(el.children, el))
    } else {
      children.push(el)
    }
  })
  return children
}

export function filterDynamicRoutes(routes) {
  const result = []
  routes.forEach(route => {
    if (route.permissions) {
      if (auth.hasPermiOr(route.permissions)) {
        result.push(route)
      }
    } else if (route.roles) {
      if (auth.hasRoleOr(route.roles)) {
        result.push(route)
      }
    } else {
      result.push(route)
    }
  })
  return result
}

export const loadView = view => {
  const portalCode = getActivePortalCode()
  const viewCandidates = buildViewCandidates(view, portalCode)

  for (const candidate of viewCandidates) {
    for (const path in modules) {
      const dir = path.split('views/')[1].split('.vue')[0]
      if (dir === candidate) {
        return () => modules[path]()
      }
    }
  }

  if (import.meta.env.DEV) {
    console.warn(`[permission] view component not found: ${view}`, viewCandidates)
  }
  return () => import('@/views/error/404.vue')
}

function normalizeViewName(view = '') {
  return String(view || '').replace(/^\/+/, '').replace(/\.vue$/, '').replace(/\/$/, '')
}

function pushUnique(list, value) {
  const normalized = normalizeViewName(value)
  if (normalized && !list.includes(normalized)) {
    list.push(normalized)
  }
}

function withPortalVariant(view, portalCode) {
  const normalized = normalizeViewName(view)
  if (portalCode === 'azb' && normalized.startsWith('ygb/')) {
    return `azb/${normalized.slice(4)}`
  }
  return ''
}

function buildViewCandidates(view, portalCode) {
  const normalizedView = normalizeViewName(view)
  const candidates = []
  const directAlias = LEGACY_VIEW_ALIASES[normalizedView]

  pushUnique(candidates, withPortalVariant(directAlias, portalCode))
  pushUnique(candidates, directAlias)

  Object.entries(LEGACY_VIEW_PREFIX_ALIASES).forEach(([legacyPrefix, targetPrefix]) => {
    if (normalizedView.startsWith(legacyPrefix)) {
      const mapped = `${targetPrefix}${normalizedView.slice(legacyPrefix.length)}`
      pushUnique(candidates, withPortalVariant(mapped, portalCode))
      pushUnique(candidates, mapped)
    }
  })

  if (normalizedView.startsWith('shared/')) {
    pushUnique(candidates, `${portalCode}/${normalizedView.slice(7)}`)
  }

  pushUnique(candidates, withPortalVariant(normalizedView, portalCode))
  pushUnique(candidates, normalizedView)
  return candidates
}

export default usePermissionStore
