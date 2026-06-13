import { OFFICIAL_PORTAL_QUERY } from '@/utils/portal'
import { isExternal } from '@/utils/validate'

const API_BASE = import.meta.env.VITE_APP_BASE_API || ''

/** 将 CMS 中保存的 /profile/... 路径转为可访问的完整地址 */
export function resolvePortalAssetUrl(url) {
  if (!url) return ''
  const value = String(url).trim()
  if (!value) return ''
  if (isExternal(value)) return value
  if (value.startsWith(API_BASE)) return value
  if (value.startsWith('/dev-api') || value.startsWith('/prod-api')) return value
  return `${API_BASE}${value.startsWith('/') ? value : `/${value}`}`
}

export function buildPortalStandaloneQuery(extra = {}) {
  return { ...OFFICIAL_PORTAL_QUERY, ...extra }
}

export function openPortalContent(router, contentId, query = {}) {
  router.push({
    path: `/portal/content/${contentId}`,
    query: buildPortalStandaloneQuery(query)
  })
}

export function openPortalJob(router, jobId, query = {}) {
  router.push({
    path: `/portal/job/${jobId}`,
    query: buildPortalStandaloneQuery(query)
  })
}

export function openPortalDownload(linkUrl) {
  const target = resolvePortalAssetUrl(linkUrl)
  if (!target) return
  window.open(target, '_blank', 'noopener,noreferrer')
}

export const PORTAL_SECTION_LABELS = {
  site_config: '站点配置',
  banner: '轮播图',
  intro: '平台简介',
  news: '最新动态',
  policy: '政策法规',
  solution: '解决方案',
  union: '工会服务',
  guide: '服务指南',
  about: '关于我们',
  partner: '合作单位',
  download: '下载中心',
  quick_entry: '快速入口',
  job_guide: '求职指南',
  job: '招聘岗位'
}
