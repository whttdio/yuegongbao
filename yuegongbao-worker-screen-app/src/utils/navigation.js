const TAB_BAR_PAGES = new Set([
  '/pages/standby/index',
  '/pages/main/index',
  '/pages/device/index'
])

function normalizePagePath(path) {
  if (!path) {
    return ''
  }
  return String(path).split('?')[0].replace(/^\//, '')
}

export function getCurrentPagePath() {
  try {
    const pages = typeof getCurrentPages === 'function' ? getCurrentPages() : []
    const current = pages[pages.length - 1]
    return normalizePagePath(current?.route || current?.$page?.fullPath || '')
  } catch {
    return ''
  }
}

export function buildPageUrl(path, query) {
  if (!path) {
    return ''
  }
  if (!query) {
    return path
  }
  if (typeof query === 'string') {
    const trimmedQuery = query.trim().replace(/^\?/, '')
    if (!trimmedQuery) {
      return path
    }
    return `${path}${path.includes('?') ? '&' : '?'}${trimmedQuery}`
  }
  if (typeof query !== 'object') {
    return path
  }
  const queryString = Object.keys(query)
    .filter((key) => query[key] !== undefined && query[key] !== null && query[key] !== '')
    .map((key) => `${encodeURIComponent(key)}=${encodeURIComponent(String(query[key]))}`)
    .join('&')
  if (!queryString) {
    return path
  }
  return `${path}${path.includes('?') ? '&' : '?'}${queryString}`
}

export function openPage(path) {
  if (!path) {
    return
  }
  if (TAB_BAR_PAGES.has(path)) {
    uni.switchTab({ url: path })
    return
  }
  uni.navigateTo({ url: path })
}

export function isTabBarPage(path) {
  return TAB_BAR_PAGES.has(`/${normalizePagePath(path)}`)
}

export function safeHideTabBar() {
  if (typeof window !== 'undefined') {
    return
  }

  if (!isTabBarPage(getCurrentPagePath())) {
    return
  }

  if (typeof uni.hideTabBar !== 'function') {
    return
  }

  try {
    const result = uni.hideTabBar({
      animation: false,
      fail: () => null
    })
    if (result && typeof result.catch === 'function') {
      result.catch(() => null)
    }
  } catch {
    return
  }
}

export function openPageTarget(path, query) {
  if (!path) {
    return
  }
  if (TAB_BAR_PAGES.has(path)) {
    openPage(path)
    return
  }
  openPage(buildPageUrl(path, query))
}
