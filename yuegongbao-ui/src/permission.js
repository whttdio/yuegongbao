import { ElMessage } from 'element-plus'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import router from './router'
import useLockStore from '@/store/modules/lock'
import usePermissionStore from '@/store/modules/permission'
import useSettingsStore from '@/store/modules/settings'
import useUserStore from '@/store/modules/user'
import { getToken } from '@/utils/auth'
import { isRelogin } from '@/utils/request'
import { resolveYgbDocumentRouteAlias } from '@/utils/routeAlias'
import { isHttp, isPathMatch } from '@/utils/validate'
import {
  getActivePortalCode,
  isOfficialPortalPath,
  isOfficialPortalStandalone,
  isPortalLoginPath,
  OFFICIAL_PORTAL_QUERY,
  resolvePortalRedirect,
  resolveOfficialPortalRoute,
  resolvePortalLoginPath,
  setActivePortalCode,
  syncPortalBranding
} from '@/utils/portal'

NProgress.configure({ showSpinner: false })

const whiteList = [
  '/login',
  '/login/**',
  '/register',
  '/portal/official',
  '/officialSite',
  '/portal/ygb-official',
  '/ygbOfficialSite',
  '/portal/content/**',
  '/portal/job/**'
]

function isWhiteList(path) {
  return whiteList.some(pattern => isPathMatch(pattern, path))
}

router.beforeEach(async (to, from) => {
  NProgress.start()
  syncPortalBranding(to)
  useSettingsStore().syncPortalSettings()

  if (from.path && isOfficialPortalPath(to.path) && !isOfficialPortalStandalone(to.query)) {
    NProgress.done()
    return {
      path: resolveOfficialPortalRoute(getActivePortalCode(to)).route,
      query: OFFICIAL_PORTAL_QUERY,
      replace: true
    }
  }

  if (getToken()) {
    if (to.meta.title) {
      useSettingsStore().setTitle(to.meta.title)
    }

    const isLock = useLockStore().isLock
    const userStore = useUserStore()

    if (isPortalLoginPath(to.path)) {
      NProgress.done()
      return { path: '/' }
    }

    if (isWhiteList(to.path)) {
      return true
    }

    if (isLock && to.path !== '/lock') {
      NProgress.done()
      return { path: '/lock' }
    }

    if (!isLock && to.path === '/lock') {
      NProgress.done()
      return { path: '/' }
    }

    const ygbDocumentRouteAlias = resolveYgbDocumentRouteAlias(to)
    if (ygbDocumentRouteAlias) {
      NProgress.done()
      return ygbDocumentRouteAlias
    }

    if (userStore.roles.length === 0) {
      isRelogin.show = true
      try {
        await userStore.getInfo()
        isRelogin.show = false
        const redirectedPortalCode = resolvePortalRedirect({
          currentPortalCode: getActivePortalCode(to),
          allowedPortalCodes: userStore.allowedPortalCodes,
          defaultPortalCode: userStore.effectivePortalCode || userStore.defaultPortalCode,
          fallbackRoles: userStore.roles
        })
        if (redirectedPortalCode) {
          setActivePortalCode(redirectedPortalCode)
        }
        const accessRoutes = await usePermissionStore().generateRoutes()
        accessRoutes.forEach(route => {
          if (!isHttp(route.path)) {
            router.addRoute(route)
          }
        })
        if (redirectedPortalCode) {
          return { path: '/index', replace: true }
        }
        return { ...to, replace: true }
      } catch (error) {
        await userStore.logOut()
        const message = typeof error === 'string'
          ? error
          : (error?.message || error?.msg || '登录状态加载失败，请重新登录')
        ElMessage.error(message)
        return { path: resolvePortalLoginPath(to) }
      }
    }

    const redirectedPortalCode = resolvePortalRedirect({
      currentPortalCode: getActivePortalCode(to),
      allowedPortalCodes: userStore.allowedPortalCodes,
      defaultPortalCode: userStore.effectivePortalCode || userStore.defaultPortalCode,
      fallbackRoles: userStore.roles
    })
    if (redirectedPortalCode) {
      setActivePortalCode(redirectedPortalCode)
      if (typeof window !== 'undefined') {
        window.location.replace('/index')
        NProgress.done()
        return false
      }
      NProgress.done()
      return { path: '/index', replace: true }
    }

    return true
  }

  if (isWhiteList(to.path)) {
    return true
  }

  NProgress.done()
  return `${resolvePortalLoginPath(to)}?redirect=${encodeURIComponent(to.fullPath)}`
})

router.afterEach(() => {
  NProgress.done()
})
