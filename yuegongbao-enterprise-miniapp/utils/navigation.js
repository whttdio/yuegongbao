const TAB_BAR_PAGES = new Set([
  '/pages/home/index',
  '/pages/workbench/index',
  '/pages/profile/index'
])

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
  return TAB_BAR_PAGES.has(path)
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
