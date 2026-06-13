import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getPortalHome, listPortalJobs, searchPortalContent } from '@/api/portal/public'
import { resolvePortalLoginPath } from '@/utils/portal'

const YGB_GRADIENTS = [
  'linear-gradient(135deg, #0a3d7a 0%, #0f5ea8 58%, #2b7fd4 100%)',
  'linear-gradient(135deg, #0c4589 0%, #1568b5 52%, #2a82cc 100%)',
  'linear-gradient(135deg, #0e4a92 0%, #1a6cb8 55%, #3589d0 100%)'
]

const AZB_GRADIENTS = [
  'linear-gradient(135deg, #0f2b3d 0%, #244c72 58%, #3d6f94 100%)',
  'linear-gradient(135deg, #163a57 0%, #21557f 52%, #2d7aa5 100%)',
  'linear-gradient(135deg, #1d405d 0%, #355b7a 55%, #4f7ea4 100%)'
]

const DEFAULT_SITE = {
  ygb: {
    title: '粤工保 · 用工保障与智能监管平台',
    subtitle: '劳务派遣·高危防控、合规用工·扩面减损',
    contactTitle: '粤工保平台服务支持',
    phone: '020-12333',
    email: 'ygb@guangdong.gov.cn',
    address: '广州市越秀区教育路 88 号',
    copyright: '© 2026 广东省人力资源和社会保障厅 版权所有 粤ICP备12345678号'
  },
  azb: {
    title: '安责保 · 广东省安全生产风险防范AI监管平台',
    subtitle: '安责险覆盖 · 设备智控 · 风险减量 · 隐患闭环',
    contactTitle: '安责保平台服务支持',
    phone: '020-83312345',
    email: 'abj@guangdong.gov.cn',
    address: '广州市越秀区建设大马路19号',
    copyright: '© 2026 广东省应急管理厅 版权所有 粤ICP备12345678号'
  }
}

export function usePortalHome(portalCode = 'ygb') {
  const defaults = DEFAULT_SITE[portalCode] || DEFAULT_SITE.ygb
  const gradients = portalCode === 'azb' ? AZB_GRADIENTS : YGB_GRADIENTS

  const loading = ref(true)
  const siteConfig = ref({ title: defaults.title, subtitle: defaults.subtitle })
  const intro = ref({ title: '', summary: '', pillars: [] })
  const heroSlides = ref([])
  const statsCards = ref([])
  const trendData = ref({ months: [], expandRates: [], closeRates: [] })
  const newsList = ref([])
  const policyData = ref({ national: [], guangdong: [], interpretation: [] })
  const solutions = ref([])
  const unionData = ref({ introTitle: '', introSummary: '', guides: [] })
  const serviceGuides = ref([])
  const aboutCards = ref([])
  const partners = ref([])
  const miniPrograms = ref([])
  const appDownloads = ref([])
  const manuals = ref([])
  const downloadItems = ref([])
  const quickEntries = ref([])
  const jobGuides = ref([])
  const hotJobs = ref([])
  const allJobs = ref([])
  const enterprises = ref([])
  const contactInfo = ref({
    contactTitle: defaults.contactTitle,
    contactSummary: '',
    phone: defaults.phone,
    email: defaults.email,
    address: defaults.address
  })
  const footerCopyright = ref(defaults.copyright)

  const buildDownloadItems = (downloads = {}) => {
    const items = []
    ;(downloads.manuals || []).forEach(item => {
      items.push({
        contentId: item.contentId,
        title: item.title,
        summary: item.summary,
        type: item.type || 'PDF',
        linkUrl: item.linkUrl,
        coverUrl: item.coverUrl
      })
    })
    ;(downloads.apps || []).forEach(item => {
      items.push({
        contentId: item.contentId,
        title: item.title,
        summary: item.desc || item.summary,
        type: 'APP',
        linkUrl: item.linkUrl,
        coverUrl: item.coverUrl
      })
    })
    ;(downloads.miniPrograms || []).forEach(item => {
      items.push({
        contentId: item.contentId,
        title: item.title,
        summary: item.desc || item.summary,
        type: item.code || '小程序',
        linkUrl: item.linkUrl,
        coverUrl: item.coverUrl
      })
    })
    return items
  }

  const applyHomeData = (data) => {
    if (!data) return
    siteConfig.value = { ...siteConfig.value, ...data.siteConfig }
    intro.value = data.intro || intro.value
    heroSlides.value = (data.banners || []).map((item, index) => ({
      ...item,
      background: item.background || gradients[index % gradients.length]
    }))
    statsCards.value = data.stats || []
    trendData.value = data.trend || trendData.value
    newsList.value = data.news || []
    policyData.value = data.policies || policyData.value
    solutions.value = data.solutions || []
    unionData.value = {
      introTitle: data.union?.introTitle || '',
      introSummary: data.union?.introSummary || '',
      hotline: data.union?.hotline,
      legalAid: data.union?.legalAid,
      guides: data.union?.guides || []
    }
    serviceGuides.value = data.guides || []
    aboutCards.value = data.about || []
    partners.value = data.partners || []
    miniPrograms.value = (data.downloads?.miniPrograms || []).map(item => ({
      contentId: item.contentId,
      code: item.code || item.title,
      title: item.title,
      desc: item.desc,
      coverUrl: item.coverUrl,
      linkUrl: item.linkUrl
    }))
    appDownloads.value = (data.downloads?.apps || []).map(item => ({
      contentId: item.contentId,
      icon: item.icon || '📱',
      title: item.title,
      desc: item.desc,
      linkUrl: item.linkUrl,
      coverUrl: item.coverUrl
    }))
    manuals.value = (data.downloads?.manuals || []).map(item => ({
      contentId: item.contentId,
      title: item.title,
      summary: item.summary,
      type: item.type,
      linkUrl: item.linkUrl,
      coverUrl: item.coverUrl
    }))
    downloadItems.value = buildDownloadItems(data.downloads)
    jobGuides.value = data.jobGuides || []
    hotJobs.value = data.hotJobs || []
    allJobs.value = data.jobs || []
    enterprises.value = data.enterprises || []
    contactInfo.value = {
      contactTitle: data.siteConfig?.contactTitle || defaults.contactTitle,
      contactSummary: data.siteConfig?.contactSummary || '',
      phone: data.siteConfig?.phone || defaults.phone,
      email: data.siteConfig?.email || defaults.email,
      address: data.siteConfig?.address || defaults.address
    }
    footerCopyright.value = data.siteConfig?.copyright || defaults.copyright
    quickEntries.value = buildQuickEntries(data.quickEntries || [])
  }

  const buildQuickEntries = (entries) => {
    if (!entries.length) {
      return [
        { icon: '🔑', label: '管理端登录', desc: '监管与企业后台', href: resolvePortalLoginPath(portalCode) },
        { icon: '📱', label: '劳动者小程序', desc: '扫码即用', actionKey: 'download' },
        { icon: '📲', label: 'APP 下载', desc: '劳务屏/监管端', actionKey: 'download' }
      ]
    }
    return entries.map(item => {
      if (item.entryType === 'login') {
        return { icon: item.icon, label: item.label, desc: item.desc, href: resolvePortalLoginPath(portalCode) }
      }
      if (item.entryType === 'anchor') {
        return { icon: item.icon, label: item.label, desc: item.desc, actionKey: item.anchor || 'download' }
      }
      if (item.linkUrl) {
        return { icon: item.icon, label: item.label, desc: item.desc, href: item.linkUrl, external: true }
      }
      return { icon: item.icon, label: item.label, desc: item.desc, actionKey: 'download' }
    })
  }

  const loadHome = async () => {
    loading.value = true
    try {
      const res = await getPortalHome(portalCode)
      applyHomeData(res.data)
    } catch (error) {
      console.error(error)
      ElMessage.warning('官网内容加载失败，已显示本地默认内容')
    } finally {
      loading.value = false
    }
  }

  const reloadJobs = async (params = {}) => {
    try {
      const res = await listPortalJobs(portalCode, params)
      allJobs.value = res.data || []
    } catch (error) {
      console.error(error)
    }
  }

  const searchContent = async (keyword) => {
    const res = await searchPortalContent(portalCode, keyword)
    return res.data || []
  }

  const searchJobs = async (keyword, limit = 5) => {
    const res = await listPortalJobs(portalCode, { keyword, limit })
    return res.data || []
  }

  return {
    portalCode,
    loading,
    siteConfig,
    intro,
    heroSlides,
    statsCards,
    trendData,
    newsList,
    policyData,
    solutions,
    unionData,
    serviceGuides,
    aboutCards,
    partners,
    miniPrograms,
    appDownloads,
    manuals,
    downloadItems,
    quickEntries,
    jobGuides,
    hotJobs,
    allJobs,
    enterprises,
    contactInfo,
    footerCopyright,
    loadHome,
    reloadJobs,
    searchContent,
    searchJobs
  }
}
