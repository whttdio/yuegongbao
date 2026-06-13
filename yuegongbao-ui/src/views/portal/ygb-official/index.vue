<template>
  <div class="official-site">
    <header class="site-header">
      <div class="site-container site-header__inner">
        <div class="brand-block">
          <div class="brand-shield">粤</div>
          <div class="brand-copy">
            <h1>{{ siteConfig.title }}</h1>
            <p>{{ siteConfig.subtitle }}</p>
          </div>
        </div>
        <PortalSearchPanel
          portal-code="ygb"
          :nav-items="navItems"
          :search-content="searchContent"
          :search-jobs="searchJobs"
          @scroll-section="handleSearchSection"
        />
      </div>
      <div class="site-nav">
        <div class="site-container">
          <ul class="nav-list">
            <li
              v-for="item in navItems"
              :key="item.key"
              :class="{ active: item.key === activeNav }"
            >
              <button type="button" @click="scrollToSection(item.key)">{{ item.label }}</button>
            </li>
          </ul>
        </div>
      </div>
    </header>

    <main class="site-main">
      <div class="site-container">
        <!-- 首页 -->
        <section :ref="(el) => setSectionRef('home', el)" class="hero-slider" data-nav-key="home">
          <article
            v-for="(slide, index) in heroSlides"
            :key="slide.title"
            class="hero-slide"
            :class="{ 'is-active': index === activeSlide }"
            :style="{ background: slide.background }"
          >
            <div class="hero-slide__content">
              <span class="hero-slide__tag">{{ slide.tag }}</span>
              <h2>{{ slide.title }}</h2>
              <p>{{ slide.summary }}</p>
              <div class="hero-slide__meta">
                <span>{{ slide.date }}</span>
                <span>{{ slide.meta }}</span>
              </div>
            </div>
          </article>
          <div class="hero-slider__dots">
            <button
              v-for="(slide, index) in heroSlides"
              :key="slide.title"
              type="button"
              :class="{ active: index === activeSlide }"
              @click="setActiveSlide(index)"
            />
          </div>
        </section>

        <section class="content-grid">
          <div :ref="(el) => setSectionRef('intro', el)" class="news-panel site-section" data-nav-key="intro">
            <div class="section-heading">
              <span class="section-heading__bar" />
              <div>
                <h3>平台简介</h3>
                <p>平台定位、核心价值与政策依据</p>
              </div>
            </div>
            <div class="intro-card">
              <h4>{{ intro.title || '省级用工保障与智能监管门户' }}</h4>
              <p>{{ intro.summary }}</p>
              <div class="intro-pillars">
                <div v-for="item in introPillars" :key="item.label" class="intro-pillar">
                  <strong>{{ item.value }}</strong>
                  <span>{{ item.label }}</span>
                </div>
              </div>
            </div>
          </div>

          <aside class="quick-panel">
            <div class="site-section">
              <div class="section-heading section-heading--compact">
                <span class="section-heading__bar" />
                <div>
                  <h3>快速入口</h3>
                  <p>登录、小程序与 APP 下载</p>
                </div>
              </div>
              <div class="quick-entry-grid">
                <component
                  :is="item.actionKey || item.action ? 'button' : 'a'"
                  v-for="item in quickEntries"
                  :key="item.label"
                  class="quick-entry-card"
                  :href="item.actionKey || item.action ? undefined : item.href"
                  :type="item.actionKey || item.action ? 'button' : undefined"
                  :target="!item.actionKey && !item.action && item.external ? '_blank' : undefined"
                  :rel="!item.actionKey && !item.action && item.external ? 'noopener noreferrer' : undefined"
                  @click="handleQuickEntry(item)"
                >
                  <span class="quick-entry-card__icon">{{ item.icon }}</span>
                  <strong>{{ item.label }}</strong>
                  <em>{{ item.desc }}</em>
                </component>
              </div>
            </div>
          </aside>
        </section>

        <section class="site-section">
          <div class="section-heading">
            <span class="section-heading__bar" />
            <div>
              <h3>核心数据看板</h3>
              <p>覆盖企业数、参保人数、预警闭环率（脱敏展示）</p>
            </div>
          </div>
          <div class="stats-grid">
            <article v-for="item in statsCards" :key="item.label" class="stats-card">
              <strong>{{ item.value }}</strong>
              <span>{{ item.label }}</span>
              <em>{{ item.trend }}</em>
            </article>
          </div>
          <div class="chart-card">
            <div ref="trendChartRef" class="trend-chart" />
            <p>近12个月参保扩面率与预警闭环率趋势</p>
          </div>
        </section>

        <section class="content-grid">
          <div :ref="(el) => setSectionRef('news', el)" class="site-section" data-nav-key="news">
            <div class="section-heading">
              <span class="section-heading__bar" />
              <div>
                <h3>最新动态</h3>
                <p>政策发布、平台公告、行业新闻</p>
              </div>
            </div>
            <div class="card-stack">
              <article v-for="item in newsList" :key="item.contentId || item.title" class="news-card is-clickable" @click="openContent(item)">
                <span class="news-card__tag">{{ item.category }}</span>
                <h4>{{ item.title }}</h4>
                <p>{{ item.summary }}</p>
                <div class="news-card__meta">
                  <span>{{ item.date }}</span>
                  <span>{{ item.source }}</span>
                </div>
              </article>
            </div>
          </div>

          <div class="site-section">
            <div class="section-heading section-heading--compact">
              <span class="section-heading__bar" />
              <div>
                <h3>招聘专区</h3>
                <p>热门岗位推荐与企业招聘入口</p>
              </div>
            </div>
            <div class="card-stack">
              <article v-for="item in hotJobs" :key="item.jobId || item.title" class="job-card is-clickable" @click="openJob(item)">
                <h4>{{ item.title }}</h4>
                <p class="job-card__company">{{ item.company }}</p>
                <div class="job-card__tags">
                  <span>{{ item.salary }}</span>
                  <span>{{ item.location }}</span>
                </div>
              </article>
              <button type="button" class="job-more-btn" @click="scrollToSection('recruitment')">查看更多岗位 →</button>
            </div>
          </div>
        </section>

        <!-- 政策法规 -->
        <section :ref="(el) => setSectionRef('policy', el)" class="site-section" data-nav-key="policy">
          <div class="section-heading">
            <span class="section-heading__bar" />
            <div>
              <h3>政策法规</h3>
              <p>国家政策、广东省政策与政策解读</p>
            </div>
          </div>
          <div class="policy-tabs">
            <button
              v-for="tab in policyTabs"
              :key="tab.key"
              type="button"
              :class="{ active: activePolicyTab === tab.key }"
              @click="activePolicyTab = tab.key"
            >{{ tab.label }}</button>
          </div>
          <div class="policy-grid">
            <article v-for="item in activePolicyList" :key="item.contentId || item.title" class="policy-card is-clickable" @click="openContent(item)">
              <span class="policy-card__type">{{ item.type }}</span>
              <h4>{{ item.title }}</h4>
              <p>{{ item.summary }}</p>
              <span class="policy-card__date">{{ item.date }}</span>
            </article>
          </div>
        </section>

        <!-- 解决方案 -->
        <section :ref="(el) => setSectionRef('solution', el)" class="site-section" data-nav-key="solution">
          <div class="section-heading">
            <span class="section-heading__bar" />
            <div>
              <h3>解决方案</h3>
              <p>劳务派遣监管、高危岗位保障与数据融合监管</p>
            </div>
          </div>
          <div class="solution-grid">
            <article v-for="item in solutions" :key="item.contentId || item.title" class="solution-card is-clickable" @click="openContent(item)">
              <span class="solution-card__icon">{{ item.icon }}</span>
              <h4>{{ item.title }}</h4>
              <p>{{ item.summary }}</p>
              <ul class="solution-card__features">
                <li v-for="feature in item.features" :key="feature">{{ feature }}</li>
              </ul>
            </article>
          </div>
        </section>

        <!-- 工会服务 -->
        <section :ref="(el) => setSectionRef('union', el)" class="site-section" data-nav-key="union">
          <div class="section-heading">
            <span class="section-heading__bar" />
            <div>
              <h3>工会服务</h3>
              <p>工会介绍、维权指南与集体协商案例</p>
            </div>
          </div>
          <div class="union-grid">
            <article class="union-card union-card--intro">
              <h4>{{ unionData.introTitle || '工会组织架构' }}</h4>
              <p>{{ unionData.introSummary }}</p>
              <div class="union-contact">
                <span v-if="unionData.hotline">服务热线：{{ unionData.hotline }}</span>
                <span v-if="unionData.legalAid">法律援助：{{ unionData.legalAid }}</span>
              </div>
            </article>
            <article v-for="item in unionGuides" :key="item.contentId || item.title" class="union-card is-clickable" @click="openContent(item)">
              <span class="union-card__type">{{ item.type }}</span>
              <h4>{{ item.title }}</h4>
              <p>{{ item.summary }}</p>
            </article>
          </div>
        </section>

        <!-- 招聘市场 -->
        <section :ref="(el) => setSectionRef('recruitment', el)" class="site-section" data-nav-key="recruitment">
          <div class="section-heading">
            <span class="section-heading__bar" />
            <div>
              <h3>招聘市场</h3>
              <p>岗位搜索、入驻企业展示与求职指南</p>
            </div>
          </div>
          <div class="recruitment-toolbar">
            <input v-model.trim="jobKeyword" type="text" placeholder="搜索职位名称">
            <select v-model="jobLocation">
              <option value="">全部地区</option>
              <option v-for="loc in jobLocations" :key="loc" :value="loc">{{ loc }}</option>
            </select>
            <select v-model="jobSalary">
              <option value="">薪资范围</option>
              <option v-for="sal in jobSalaries" :key="sal" :value="sal">{{ sal }}</option>
            </select>
          </div>
          <div class="recruitment-grid">
            <article v-for="item in filteredJobs" :key="item.jobId || item.title + item.company" class="recruitment-card is-clickable" @click="openJob(item)">
              <h4>{{ item.title }}</h4>
              <p class="recruitment-card__company">{{ item.company }}</p>
              <div class="recruitment-card__meta">
                <span>{{ item.salary }}</span>
                <span>{{ item.location }}</span>
              </div>
            </article>
          </div>
          <div class="enterprise-strip">
            <h4>入驻企业</h4>
            <div class="enterprise-logos">
              <span v-for="ent in enterprises" :key="ent">{{ ent }}</span>
            </div>
          </div>
          <div class="guide-tips">
            <article v-for="tip in jobGuides" :key="tip.contentId || tip.title" class="guide-tip-card is-clickable" @click="openContent(tip)">
              <h4>{{ tip.title }}</h4>
              <p>{{ tip.summary }}</p>
            </article>
          </div>
        </section>

        <!-- 服务指南 -->
        <section :ref="(el) => setSectionRef('guide', el)" class="site-section" data-nav-key="guide">
          <div class="section-heading">
            <span class="section-heading__bar" />
            <div>
              <h3>服务指南</h3>
              <p>企业入驻、劳动者使用与监管端操作指南</p>
            </div>
          </div>
          <div class="guide-grid">
            <article v-for="item in serviceGuides" :key="item.contentId || item.title" class="guide-card is-clickable" @click="openContent(item)">
              <span class="guide-card__icon">{{ item.icon }}</span>
              <h4>{{ item.title }}</h4>
              <p>{{ item.summary }}</p>
              <ul>
                <li v-for="step in item.steps" :key="step">{{ step }}</li>
              </ul>
            </article>
          </div>
        </section>

        <!-- 关于我们 -->
        <section :ref="(el) => setSectionRef('about', el)" class="site-section" data-nav-key="about">
          <div class="section-heading">
            <span class="section-heading__bar" />
            <div>
              <h3>关于我们</h3>
              <p>建设背景、合作单位与联系方式</p>
            </div>
          </div>
          <div class="about-grid">
            <article v-for="item in aboutCards" :key="item.contentId || item.title" class="about-card is-clickable" @click="openContent(item)">
              <h4>{{ item.title }}</h4>
              <p>{{ item.summary }}</p>
            </article>
          </div>
          <div class="partner-grid">
            <article v-for="item in partners" :key="item.name" class="partner-card">
              <span class="partner-card__icon">{{ item.icon }}</span>
              <h4>{{ item.name }}</h4>
              <p>{{ item.role }}</p>
            </article>
          </div>
          <div class="contact-panel">
            <div class="contact-panel__main">
              <h4>{{ contactInfo.contactTitle }}</h4>
              <p>{{ contactInfo.contactSummary }}</p>
            </div>
            <div class="contact-list">
              <span>客服电话：{{ contactInfo.phone }}</span>
              <span>邮箱：{{ contactInfo.email }}</span>
              <span>地址：{{ contactInfo.address }}</span>
            </div>
          </div>
        </section>

        <!-- 下载中心 -->
        <section :ref="(el) => setSectionRef('download', el)" class="site-section" data-nav-key="download">
          <div class="section-heading">
            <span class="section-heading__bar" />
            <div>
              <h3>下载中心</h3>
              <p>小程序二维码、APP 下载与操作手册</p>
            </div>
          </div>
          <div class="download-section-grid">
            <div class="qrcode-grid">
              <article v-for="item in miniPrograms" :key="item.contentId || item.title" class="qrcode-card is-clickable" @click="handleDownload(item)">
                <div class="qrcode-placeholder">
                  <img v-if="item.coverUrl" :src="resolvePortalAssetUrl(item.coverUrl)" alt="">
                  <span v-else>{{ item.code }}</span>
                </div>
                <h4>{{ item.title }}</h4>
                <p>{{ item.desc }}</p>
              </article>
            </div>
            <div class="app-download-grid">
              <article v-for="item in appDownloads" :key="item.contentId || item.title" class="app-card">
                <span class="app-card__icon">{{ item.icon }}</span>
                <div>
                  <h4>{{ item.title }}</h4>
                  <p>{{ item.desc }}</p>
                </div>
                <button type="button" @click="handleDownload(item)">下载</button>
              </article>
            </div>
          </div>
          <div class="download-grid">
            <article v-for="item in manuals" :key="item.contentId || item.title" class="download-card is-clickable" @click="handleDownload(item)">
              <div>
                <h4>{{ item.title }}</h4>
                <p>{{ item.summary }}</p>
              </div>
              <span>{{ item.type }}</span>
            </article>
          </div>
        </section>
      </div>
    </main>

    <footer class="site-footer">
      <div class="site-container footer-grid">
        <div>
          <h4>粤工保平台</h4>
          <p>用工保障与智能监管平台</p>
          <p>劳务派遣·高危防控、合规用工·扩面减损</p>
        </div>
        <div>
          <h4>相关链接</h4>
          <a href="javascript:void(0)">广东省人民政府</a>
          <a href="javascript:void(0)">广东省人力资源和社会保障厅</a>
          <a href="javascript:void(0)">广东省总工会</a>
        </div>
        <div>
          <h4>服务支持</h4>
          <a href="javascript:void(0)" @click.prevent="scrollToSection('guide')">企业入驻指南</a>
          <a href="javascript:void(0)" @click.prevent="scrollToSection('download')">操作手册下载</a>
          <a href="javascript:void(0)" @click.prevent="scrollToSection('about')">联系我们</a>
        </div>
      </div>
      <div class="site-container footer-copy">
        © {{ footerCopyright }}
      </div>
    </footer>
  </div>
</template>

<script setup>
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import PortalSearchPanel from '../components/PortalSearchPanel.vue'
import { usePortalHome } from '../usePortalHome'
import { openPortalContent, openPortalDownload, openPortalJob, resolvePortalAssetUrl } from '@/utils/portalNavigation'

const router = useRouter()

const {
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
} = usePortalHome('ygb')

const navItems = [
  { key: 'home', label: '首页' },
  { key: 'policy', label: '政策法规' },
  { key: 'solution', label: '解决方案' },
  { key: 'union', label: '工会服务' },
  { key: 'recruitment', label: '招聘市场' },
  { key: 'guide', label: '服务指南' },
  { key: 'about', label: '关于我们' },
  { key: 'download', label: '下载中心' }
]

const policyTabs = [
  { key: 'national', label: '国家政策' },
  { key: 'guangdong', label: '广东省政策' },
  { key: 'interpretation', label: '政策解读' }
]

const unionGuides = computed(() => unionData.value.guides || [])
const introPillars = computed(() => intro.value.pillars || [])

const sectionRefs = {
  home: null,
  intro: null,
  news: null,
  policy: null,
  solution: null,
  union: null,
  recruitment: null,
  guide: null,
  about: null,
  download: null
}

const activeNav = ref('home')
const activeSlide = ref(0)
const activePolicyTab = ref('national')
const jobKeyword = ref('')
const jobLocation = ref('')
const jobSalary = ref('')
const trendChartRef = ref(null)

let slideTimer = null
let trendChart = null
let jobFilterTimer = null

const activePolicyList = computed(() => policyData.value[activePolicyTab.value] || [])

const jobLocations = computed(() => [...new Set(allJobs.value.map(item => item.location).filter(Boolean))])
const jobSalaries = computed(() => [...new Set(allJobs.value.map(item => item.salary).filter(Boolean))])

const filteredJobs = computed(() => {
  return allJobs.value.filter(job => {
    const matchKeyword = !jobKeyword.value || job.title.includes(jobKeyword.value) || job.company.includes(jobKeyword.value)
    const matchLocation = !jobLocation.value || job.location === jobLocation.value
    const matchSalary = !jobSalary.value || job.salary === jobSalary.value
    return matchKeyword && matchLocation && matchSalary
  })
})

const setSectionRef = (key, element) => {
  sectionRefs[key] = element
}

const handleQuickEntry = (item) => {
  if (item.actionKey) {
    scrollToSection(item.actionKey)
  }
}

const setActiveSlide = (index) => {
  activeSlide.value = index
  restartSlideTimer()
}

const startSlideTimer = () => {
  stopSlideTimer()
  if (!heroSlides.value.length) {
    return
  }
  slideTimer = window.setInterval(() => {
    activeSlide.value = (activeSlide.value + 1) % heroSlides.value.length
  }, 5000)
}

const stopSlideTimer = () => {
  if (slideTimer) {
    window.clearInterval(slideTimer)
    slideTimer = null
  }
}

const restartSlideTimer = () => {
  startSlideTimer()
}

const scrollToSection = (key) => {
  const target = sectionRefs[key]
  if (!target) {
    return
  }
  activeNav.value = key
  target.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

const handleSearchSection = (sectionKey, categoryCode) => {
  if (sectionKey === 'policy' && categoryCode) {
    activePolicyTab.value = categoryCode
  }
  scrollToSection(sectionKey)
}

const openContent = (item) => {
  if (!item?.contentId) {
    ElMessage.info('该内容暂未配置详情')
    return
  }
  openPortalContent(router, item.contentId, { portalCode: 'ygb' })
}

const openJob = (item) => {
  if (!item?.jobId) return
  openPortalJob(router, item.jobId, { portalCode: 'ygb' })
}

const handleDownload = (item) => {
  if (item.linkUrl) {
    openPortalDownload(item.linkUrl)
    return
  }
  if (item.contentId) {
    openContent(item)
  }
}

const updateActiveNavByScroll = () => {
  const entries = navItems
    .map(item => {
      const element = sectionRefs[item.key]
      if (!element) {
        return null
      }
      const rect = element.getBoundingClientRect()
      return { key: item.key, top: rect.top }
    })
    .filter(Boolean)

  const current = entries.find(item => item.top >= 0 && item.top <= 260)
  if (current) {
    activeNav.value = current.key
    return
  }

  const fallback = [...entries].reverse().find(item => item.top < 260)
  if (fallback) {
    activeNav.value = fallback.key
  }
}

const initTrendChart = () => {
  if (!trendChartRef.value) {
    return
  }
  trendChart = echarts.init(trendChartRef.value)
  const months = trendData.value.months?.length
    ? trendData.value.months
    : ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
  const expandRates = trendData.value.expandRates?.length
    ? trendData.value.expandRates
    : [82, 84, 85, 87, 88, 89, 90, 91, 91.5, 92, 92.3, 92.6]
  const closeRates = trendData.value.closeRates?.length
    ? trendData.value.closeRates
    : [88, 89, 90, 91, 92, 93, 94, 94.5, 95, 95.5, 96.2, 96.8]
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { top: 0, textStyle: { color: '#35536d' } },
    grid: { left: '4%', right: '4%', bottom: '6%', containLabel: true },
    xAxis: {
      type: 'category',
      data: months,
      axisLine: { lineStyle: { color: '#9bb3c7' } }
    },
    yAxis: {
      type: 'value',
      min: 80,
      axisLabel: { color: '#56738f' },
      splitLine: { lineStyle: { color: '#e7edf3' } }
    },
    series: [
      {
        name: '参保扩面率 (%)',
        type: 'line',
        smooth: true,
        symbolSize: 8,
        itemStyle: { color: '#0f5ea8' },
        lineStyle: { width: 3, color: '#0f5ea8' },
        data: expandRates
      },
      {
        name: '预警闭环率 (%)',
        type: 'bar',
        barWidth: '30%',
        itemStyle: { color: '#3d8fd4', borderRadius: [6, 6, 0, 0] },
        data: closeRates
      }
    ]
  })
}

const handleResize = () => {
  trendChart?.resize()
  updateActiveNavByScroll()
}

watch([jobKeyword, jobLocation, jobSalary], () => {
  if (jobFilterTimer) {
    window.clearTimeout(jobFilterTimer)
  }
  jobFilterTimer = window.setTimeout(() => {
    reloadJobs({
      keyword: jobKeyword.value || undefined,
      location: jobLocation.value || undefined,
      salary: jobSalary.value || undefined,
      limit: 20
    })
  }, 300)
})

watch(heroSlides, () => {
  activeSlide.value = 0
  restartSlideTimer()
})

onMounted(async () => {
  await loadHome()
  await nextTick()
  initTrendChart()
  startSlideTimer()
  updateActiveNavByScroll()
  window.addEventListener('resize', handleResize)
  window.addEventListener('scroll', updateActiveNavByScroll, { passive: true })
})

onBeforeUnmount(() => {
  stopSlideTimer()
  if (jobFilterTimer) {
    window.clearTimeout(jobFilterTimer)
  }
  window.removeEventListener('resize', handleResize)
  window.removeEventListener('scroll', updateActiveNavByScroll)
  trendChart?.dispose()
  trendChart = null
})
</script>

<style lang="scss" scoped>
$primary: #0f5ea8;
$primary-dark: #0a3d7a;
$text: #18354d;
$text-muted: #5f758a;

.official-site {
  min-height: 100vh;
  background: #f3f6fa;
  color: $text;
}

.site-container {
  width: min(1280px, calc(100% - 48px));
  margin: 0 auto;
}

.site-header {
  background: #ffffff;
  box-shadow: 0 8px 24px rgba(15, 94, 168, 0.08);
}

.site-header__inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 18px 0;
}

.brand-block {
  display: flex;
  align-items: center;
  gap: 14px;
}

.brand-shield {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 52px;
  height: 60px;
  border-radius: 14px 14px 22px 22px;
  background: linear-gradient(180deg, $primary-dark 0%, $primary 100%);
  box-shadow: 0 8px 18px rgba(15, 94, 168, 0.24);
  color: #ffffff;
  font-size: 24px;
  font-weight: 700;
}

.brand-copy h1 {
  margin: 0;
  color: $primary-dark;
  font-size: 26px;
  line-height: 1.2;
}

.brand-copy p {
  margin: 6px 0 0;
  color: #627a91;
  font-size: 13px;
}

.search-bar {
  display: flex;
  align-items: center;
  min-width: 320px;
  padding: 5px 6px 5px 16px;
  border-radius: 32px;
  background: #f2f6fb;
}

.search-bar input {
  flex: 1;
  border: none;
  background: transparent;
  color: $text;
  font-size: 14px;
  outline: none;
}

.search-bar button {
  height: 38px;
  padding: 0 20px;
  border: none;
  border-radius: 22px;
  background: $primary;
  color: #ffffff;
  font-size: 14px;
  cursor: pointer;
}

.site-nav {
  background: $primary;
}

.nav-list {
  display: flex;
  gap: 36px;
  align-items: center;
  margin: 0;
  padding: 0;
  overflow-x: auto;
  list-style: none;
}

.nav-list li {
  flex: 0 0 auto;
}

.nav-list button {
  padding: 14px 0 12px;
  border: none;
  border-bottom: 3px solid transparent;
  background: transparent;
  color: #ffffff;
  font-size: 15px;
  white-space: nowrap;
  cursor: pointer;
}

.nav-list li.active button {
  border-bottom-color: #ffd966;
}

.site-main {
  padding: 28px 0 40px;
}

.hero-slider {
  position: relative;
  height: 480px;
  overflow: hidden;
  border-radius: 18px;
  box-shadow: 0 20px 40px rgba(15, 94, 168, 0.16);
}

.hero-slide {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: flex-end;
  padding: 36px;
  opacity: 0;
  transition: opacity 0.35s ease;
}

.hero-slide.is-active {
  opacity: 1;
}

.hero-slide__content {
  width: min(720px, 100%);
  padding: 24px 28px;
  border-radius: 18px;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0.08) 0%, rgba(4, 18, 31, 0.52) 100%);
  color: #ffffff;
}

.hero-slide__tag {
  display: inline-flex;
  margin-bottom: 14px;
  padding: 5px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.14);
  font-size: 12px;
  letter-spacing: 0.08em;
}

.hero-slide__content h2 {
  margin: 0 0 12px;
  font-size: 32px;
  line-height: 1.3;
}

.hero-slide__content p {
  margin: 0;
  font-size: 15px;
  line-height: 1.8;
  color: rgba(255, 255, 255, 0.9);
}

.hero-slide__meta {
  display: flex;
  gap: 20px;
  margin-top: 16px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.78);
}

.hero-slider__dots {
  position: absolute;
  right: 26px;
  bottom: 24px;
  display: flex;
  gap: 10px;
}

.hero-slider__dots button {
  width: 12px;
  height: 12px;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.42);
  cursor: pointer;
}

.hero-slider__dots button.active {
  background: #ffd966;
}

.content-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.8fr) minmax(300px, 1fr);
  gap: 26px;
  margin-top: 28px;
}

.site-section {
  scroll-margin-top: 110px;
}

.section-heading {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  margin-bottom: 18px;
}

.section-heading__bar {
  width: 5px;
  height: 46px;
  border-radius: 999px;
  background: $primary;
}

.section-heading h3 {
  margin: 0;
  color: #16354e;
  font-size: 25px;
}

.section-heading p {
  margin: 6px 0 0;
  color: #6b8196;
  font-size: 14px;
}

.section-heading--compact h3 {
  font-size: 22px;
}

.intro-card,
.news-card,
.policy-card,
.solution-card,
.stats-card,
.chart-card,
.download-card,
.contact-panel,
.quick-entry-card,
.job-card,
.union-card,
.recruitment-card,
.guide-card,
.about-card,
.partner-card,
.qrcode-card,
.app-card,
.guide-tip-card {
  border: 1px solid #e5edf4;
  border-radius: 18px;
  background: #ffffff;
  box-shadow: 0 8px 18px rgba(15, 94, 168, 0.05);
}

.intro-card {
  padding: 24px 28px;
}

.intro-card h4 {
  margin: 0 0 12px;
  font-size: 22px;
  color: $text;
}

.intro-card p {
  margin: 0;
  color: $text-muted;
  line-height: 1.9;
}

.intro-pillars {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  margin-top: 24px;
}

.intro-pillar {
  padding: 18px;
  border-radius: 14px;
  background: linear-gradient(180deg, #f8fbff 0%, #eaf3ff 100%);
}

.intro-pillar strong {
  display: block;
  color: $primary;
  font-size: 20px;
}

.intro-pillar span {
  display: block;
  margin-top: 6px;
  color: #6b8196;
  font-size: 13px;
}

.quick-entry-grid {
  display: grid;
  gap: 12px;
}

.quick-entry-card {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 4px 12px;
  width: 100%;
  padding: 16px 18px;
  border: 1px solid #e5edf4;
  text-align: left;
  text-decoration: none;
  color: inherit;
  cursor: pointer;
  transition: border-color 0.2s, box-shadow 0.2s;

  &:hover {
    border-color: $primary;
    box-shadow: 0 8px 20px rgba(15, 94, 168, 0.12);
  }
}

.quick-entry-card__icon {
  grid-row: span 2;
  font-size: 28px;
}

.quick-entry-card strong {
  color: $text;
  font-size: 15px;
}

.quick-entry-card em {
  color: #6b8196;
  font-size: 12px;
  font-style: normal;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 18px;
  margin-top: 6px;
}

.stats-card {
  padding: 24px 20px;
  text-align: center;
}

.stats-card strong {
  display: block;
  color: $primary;
  font-size: 34px;
  line-height: 1.2;
}

.stats-card span {
  display: block;
  margin-top: 10px;
  color: $text-muted;
  font-size: 14px;
}

.stats-card em {
  display: block;
  margin-top: 8px;
  color: #2d7ab0;
  font-style: normal;
  font-size: 13px;
}

.chart-card {
  margin-top: 20px;
  padding: 16px 18px 10px;
}

.trend-chart {
  height: 330px;
}

.chart-card p {
  margin: 8px 0 0;
  color: #6b8196;
  text-align: center;
  font-size: 13px;
}

.card-stack {
  display: grid;
  gap: 16px;
}

.news-card {
  padding: 20px 22px;
}

.news-card__tag {
  display: inline-flex;
  margin-bottom: 8px;
  padding: 3px 10px;
  border-radius: 999px;
  background: #eaf3ff;
  color: $primary;
  font-size: 12px;
}

.news-card h4 {
  margin: 0 0 10px;
  color: $text;
  font-size: 18px;
  line-height: 1.6;
}

.news-card p {
  margin: 0;
  color: $text-muted;
  line-height: 1.8;
  font-size: 14px;
}

.news-card__meta {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 18px;
  padding-top: 14px;
  border-top: 1px solid #eaf0f5;
  color: #72879a;
  font-size: 13px;
}

.job-card {
  padding: 18px 20px;
}

.job-card h4 {
  margin: 0 0 6px;
  font-size: 16px;
  color: $text;
}

.job-card__company {
  margin: 0;
  color: #6b8196;
  font-size: 13px;
}

.job-card__tags {
  display: flex;
  gap: 10px;
  margin-top: 10px;

  span {
    padding: 3px 10px;
    border-radius: 999px;
    background: #eaf3ff;
    color: $primary;
    font-size: 12px;
  }
}

.job-more-btn {
  padding: 12px;
  border: 1px dashed #c5d9ed;
  border-radius: 14px;
  background: #f8fbff;
  color: $primary;
  font-size: 14px;
  cursor: pointer;

  &:hover {
    border-color: $primary;
    background: #eaf3ff;
  }
}

.policy-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 18px;

  button {
    padding: 8px 20px;
    border: 1px solid #d5e3f0;
    border-radius: 999px;
    background: #ffffff;
    color: #5f758a;
    font-size: 14px;
    cursor: pointer;

    &.active {
      border-color: $primary;
      background: $primary;
      color: #ffffff;
    }
  }
}

.policy-grid,
.solution-grid,
.guide-grid,
.about-grid,
.partner-grid,
.download-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 20px;
  margin-top: 6px;
}

.policy-card,
.solution-card,
.guide-card,
.about-card,
.partner-card {
  padding: 22px 24px;
}

.policy-card__type,
.union-card__type {
  display: inline-flex;
  padding: 4px 10px;
  border-radius: 999px;
  background: #eaf3ff;
  color: $primary;
  font-size: 12px;
}

.policy-card h4,
.solution-card h4,
.guide-card h4,
.about-card h4,
.partner-card h4 {
  margin: 14px 0 12px;
  color: $text;
  font-size: 18px;
}

.policy-card p,
.solution-card p,
.guide-card p,
.about-card p,
.partner-card p {
  margin: 0;
  color: $text-muted;
  line-height: 1.8;
  font-size: 14px;
}

.policy-card__date {
  display: block;
  margin-top: 16px;
  color: #73889c;
  font-size: 13px;
}

.solution-card__icon,
.guide-card__icon,
.partner-card__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: #eaf3ff;
  font-size: 24px;
}

.solution-card__features {
  margin: 14px 0 0;
  padding-left: 18px;
  color: #5f758a;
  font-size: 13px;
  line-height: 1.8;
}

.guide-card ul {
  margin: 12px 0 0;
  padding-left: 18px;
  color: #5f758a;
  font-size: 13px;
  line-height: 1.8;
}

.union-grid {
  display: grid;
  grid-template-columns: 1.2fr repeat(3, 1fr);
  gap: 20px;
  margin-top: 6px;
}

.union-card {
  padding: 22px 24px;
}

.union-card--intro {
  grid-row: span 1;
}

.union-contact {
  display: grid;
  gap: 8px;
  margin-top: 16px;
  color: $primary;
  font-size: 14px;
}

.recruitment-toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 18px;

  input,
  select {
    flex: 1;
    padding: 10px 14px;
    border: 1px solid #d5e3f0;
    border-radius: 10px;
    background: #ffffff;
    color: $text;
    font-size: 14px;
    outline: none;

    &:focus {
      border-color: $primary;
    }
  }
}

.recruitment-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
}

.recruitment-card {
  padding: 20px 22px;
}

.recruitment-card h4 {
  margin: 0 0 6px;
  font-size: 17px;
  color: $text;
}

.recruitment-card__company {
  margin: 0;
  color: #6b8196;
  font-size: 13px;
}

.recruitment-card__meta {
  display: flex;
  gap: 10px;
  margin-top: 12px;

  span {
    padding: 3px 10px;
    border-radius: 999px;
    background: #eaf3ff;
    color: $primary;
    font-size: 12px;
  }
}

.enterprise-strip {
  margin-top: 24px;
  padding: 20px 24px;
  border-radius: 18px;
  background: #ffffff;
  border: 1px solid #e5edf4;

  h4 {
    margin: 0 0 14px;
    color: $text;
    font-size: 17px;
  }
}

.enterprise-logos {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;

  span {
    padding: 8px 16px;
    border-radius: 10px;
    background: #f2f7fc;
    color: #4e677d;
    font-size: 13px;
  }
}

.guide-tips {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
  margin-top: 18px;
}

.guide-tip-card {
  padding: 20px 22px;
}

.guide-tip-card h4 {
  margin: 0 0 8px;
  font-size: 16px;
  color: $text;
}

.guide-tip-card p {
  margin: 0;
  color: $text-muted;
  font-size: 14px;
  line-height: 1.7;
}

.download-section-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 20px;
}

.qrcode-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.qrcode-card {
  padding: 22px;
  text-align: center;
}

.qrcode-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 120px;
  height: 120px;
  margin: 0 auto 14px;
  border: 2px dashed #c5d9ed;
  border-radius: 12px;
  background: #f8fbff;
  color: $primary;
  font-size: 14px;
  font-weight: 600;
}

.qrcode-card h4 {
  margin: 0 0 6px;
  font-size: 16px;
  color: $text;
}

.qrcode-card p {
  margin: 0;
  color: $text-muted;
  font-size: 13px;
}

.app-download-grid {
  display: grid;
  gap: 14px;
}

.app-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 20px;

  h4 {
    margin: 0 0 4px;
    font-size: 16px;
    color: $text;
  }

  p {
    margin: 0;
    color: $text-muted;
    font-size: 13px;
  }

  button {
    margin-left: auto;
    padding: 8px 18px;
    border: none;
    border-radius: 8px;
    background: $primary;
    color: #ffffff;
    font-size: 13px;
    cursor: pointer;
    white-space: nowrap;
  }
}

.app-card__icon {
  font-size: 32px;
}

.download-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 22px 24px;
}

.download-card span {
  flex: 0 0 auto;
  padding: 6px 12px;
  border-radius: 999px;
  background: $primary;
  color: #ffffff;
  font-size: 12px;
}

.contact-panel {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 24px 28px;
  margin-top: 20px;
}

.contact-panel__main {
  max-width: 560px;
}

.contact-panel h4 {
  margin: 0 0 12px;
  font-size: 18px;
  color: $text;
}

.contact-panel p {
  margin: 0;
  color: $text-muted;
  line-height: 1.8;
  font-size: 14px;
}

.contact-list {
  display: grid;
  gap: 10px;
  color: #4e677d;
  font-size: 14px;
}

.site-footer {
  margin-top: 18px;
  padding: 42px 0 20px;
  background: #072a52;
  color: #d0dfe9;
}

.footer-grid {
  display: grid;
  grid-template-columns: 1.4fr 1fr 1fr;
  gap: 32px;
}

.footer-grid h4 {
  margin: 0 0 16px;
  color: #ffffff;
  font-size: 17px;
}

.footer-grid p,
.footer-grid a {
  display: block;
  margin: 0 0 10px;
  color: #d0dfe9;
  line-height: 1.8;
  text-decoration: none;
  font-size: 14px;
}

.footer-copy {
  margin-top: 24px;
  padding-top: 22px;
  border-top: 1px solid rgba(208, 223, 233, 0.16);
  text-align: center;
  color: #9db3c3;
  font-size: 13px;
}

@media (max-width: 1100px) {
  .content-grid,
  .policy-grid,
  .solution-grid,
  .guide-grid,
  .about-grid,
  .partner-grid,
  .download-grid,
  .footer-grid,
  .union-grid,
  .recruitment-grid,
  .download-section-grid {
    grid-template-columns: 1fr;
  }

  .stats-grid,
  .intro-pillars,
  .qrcode-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .contact-panel {
    flex-direction: column;
    align-items: flex-start;
  }
}

@media (max-width: 768px) {
  .site-container {
    width: min(100%, calc(100% - 24px));
  }

  .site-header__inner {
    flex-direction: column;
    align-items: stretch;
  }

  .brand-copy h1 {
    font-size: 20px;
  }

  .search-bar {
    min-width: 0;
  }

  .hero-slider {
    height: 360px;
  }

  .hero-slide {
    padding: 18px;
  }

  .hero-slide__content {
    padding: 18px;
  }

  .hero-slide__content h2 {
    font-size: 22px;
  }

  .stats-grid,
  .intro-pillars,
  .qrcode-grid,
  .guide-tips {
    grid-template-columns: 1fr;
  }

  .recruitment-toolbar {
    flex-direction: column;
  }
}

.is-clickable {
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 24px rgba(15, 94, 168, 0.12);
  }
}

.qrcode-placeholder img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.official-site {
  background:
    linear-gradient(180deg, #eef5fb 0%, #f8fbfd 34%, #eef4f8 100%);
  color: $text;
  font-family: "Microsoft YaHei", "PingFang SC", "Helvetica Neue", Arial, sans-serif;
}

.site-container {
  width: min(1280px, calc(100% - 56px));
}

.site-header {
  position: sticky;
  top: 0;
  z-index: 25;
  border-bottom: 1px solid rgba(197, 215, 230, 0.72);
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 10px 28px rgba(15, 94, 168, 0.06);
}

.site-header__inner {
  min-height: 76px;
  padding: 12px 0;
}

.brand-shield {
  width: 50px;
  height: 56px;
  border-radius: 10px 10px 16px 16px;
  background: linear-gradient(180deg, #0a3d7a 0%, #0f5ea8 62%, #16794c 100%);
  box-shadow: 0 10px 20px rgba(15, 94, 168, 0.18);
}

.brand-copy h1 {
  color: $primary-dark;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 0;
}

.brand-copy p {
  color: #607789;
}

.site-nav {
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  background: #0a4f8f;
}

.nav-list {
  gap: 10px;
  scrollbar-width: none;
}

.nav-list::-webkit-scrollbar {
  display: none;
}

.nav-list button {
  position: relative;
  min-height: 48px;
  padding: 0 14px;
  border-bottom: none;
  border-radius: 8px 8px 0 0;
  color: rgba(255, 255, 255, 0.88);
  font-size: 15px;
  transition: background-color 0.18s ease, color 0.18s ease;
}

.nav-list button:hover,
.nav-list li.active button {
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
}

.nav-list li.active button::after {
  position: absolute;
  right: 14px;
  bottom: 0;
  left: 14px;
  height: 3px;
  border-radius: 3px 3px 0 0;
  background: #e8b84e;
  content: "";
}

.site-main {
  padding: 32px 0 52px;
}

.site-section {
  scroll-margin-top: 138px;
}

.hero-slider {
  height: 490px;
  border: 1px solid rgba(255, 255, 255, 0.72);
  border-radius: 12px;
  background: $primary-dark;
  box-shadow: 0 22px 48px rgba(15, 94, 168, 0.14);
}

.hero-slide {
  align-items: center;
  padding: 44px;
}

.hero-slide::before {
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, rgba(5, 26, 52, 0.78) 0%, rgba(5, 34, 64, 0.43) 50%, rgba(5, 34, 64, 0.1) 100%);
  content: "";
}

.hero-slide__content {
  position: relative;
  width: min(700px, 100%);
  padding: 0;
  border-radius: 0;
  background: transparent;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.2);
}

.hero-slide__tag {
  margin-bottom: 16px;
  border: 1px solid rgba(255, 255, 255, 0.28);
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.1);
  letter-spacing: 0;
}

.hero-slide__content h2 {
  max-width: 18em;
  font-size: 35px;
  font-weight: 700;
  line-height: 1.26;
}

.hero-slide__content p {
  max-width: 58em;
  font-size: 16px;
  line-height: 1.9;
}

.hero-slide__meta {
  flex-wrap: wrap;
}

.hero-slider__dots {
  right: 28px;
  bottom: 26px;
  padding: 6px;
  border-radius: 999px;
  background: rgba(5, 26, 52, 0.34);
}

.hero-slider__dots button {
  width: 18px;
  height: 6px;
  border-radius: 999px;
  transition: width 0.18s ease, background-color 0.18s ease;
}

.hero-slider__dots button.active {
  width: 32px;
  background: #e8b84e;
}

.content-grid {
  gap: 24px;
  margin-top: 28px;
}

.section-heading {
  margin: 36px 0 18px;
}

.content-grid .section-heading {
  margin-top: 0;
}

.section-heading__bar {
  width: 4px;
  height: 42px;
  border-radius: 4px;
  background: $primary;
}

.section-heading h3 {
  color: #143550;
  font-size: 24px;
  font-weight: 700;
}

.section-heading p {
  color: #647b8d;
}

.intro-card,
.news-card,
.policy-card,
.solution-card,
.stats-card,
.chart-card,
.download-card,
.contact-panel,
.quick-entry-card,
.job-card,
.union-card,
.recruitment-card,
.guide-card,
.about-card,
.partner-card,
.qrcode-card,
.app-card,
.guide-tip-card {
  border-color: #dde8f0;
  border-radius: 12px;
  background: #ffffff;
  box-shadow: 0 12px 28px rgba(15, 94, 168, 0.06);
}

.intro-card,
.policy-card,
.solution-card,
.guide-card,
.about-card,
.partner-card,
.union-card,
.recruitment-card,
.guide-tip-card,
.qrcode-card,
.download-card,
.contact-panel {
  padding: 24px;
}

.intro-pillar,
.policy-card__type,
.union-card__type,
.solution-card__icon,
.guide-card__icon,
.partner-card__icon,
.job-card__tags span,
.recruitment-card__meta span {
  border-radius: 8px;
  background: #edf5fb;
  color: $primary;
}

.intro-pillar {
  background: linear-gradient(180deg, #f7fbff 0%, #edf5fb 100%);
}

.intro-pillar strong,
.stats-card strong {
  color: $primary;
}

.quick-entry-card {
  align-items: center;
  min-height: 82px;
}

.quick-entry-card:hover,
.job-more-btn:hover,
.policy-tabs button.active {
  border-color: $primary;
}

.quick-entry-card__icon,
.app-card__icon {
  line-height: 1;
}

.stats-card {
  padding: 24px 18px;
}

.stats-card em {
  color: #16794c;
}

.policy-tabs {
  flex-wrap: wrap;
  gap: 10px;
}

.policy-tabs button {
  border-radius: 8px;
  transition: border-color 0.18s ease, background-color 0.18s ease, color 0.18s ease;
}

.policy-tabs button.active {
  background: $primary;
}

.enterprise-strip {
  border-color: #dde8f0;
  border-radius: 12px;
  background: #ffffff;
  box-shadow: 0 12px 28px rgba(15, 94, 168, 0.05);
}

.enterprise-logos span,
.qrcode-placeholder,
.job-more-btn {
  border-radius: 8px;
}

.app-card button,
.download-card span {
  border-radius: 8px;
  background: $primary;
}

.contact-panel {
  margin-top: 22px;
}

.site-footer {
  margin-top: 12px;
  background: #072a52;
}

.is-clickable {
  transition: border-color 0.18s ease, transform 0.18s ease, box-shadow 0.18s ease;
}

.is-clickable:hover {
  border-color: rgba(15, 94, 168, 0.44);
  box-shadow: 0 16px 32px rgba(15, 94, 168, 0.1);
}

@media (prefers-reduced-motion: reduce) {
  .hero-slide,
  .hero-slider__dots button,
  .nav-list button,
  .policy-tabs button,
  .is-clickable {
    transition: none;
  }

  .is-clickable:hover {
    transform: none;
  }
}

@media (max-width: 1100px) {
  .site-header {
    position: relative;
  }

  .site-section {
    scroll-margin-top: 24px;
  }
}

@media (max-width: 768px) {
  .site-container {
    width: min(100%, calc(100% - 28px));
  }

  .site-header__inner {
    min-height: auto;
    gap: 14px;
  }

  .brand-shield {
    width: 44px;
    height: 50px;
  }

  .brand-copy h1 {
    font-size: 20px;
  }

  .nav-list {
    gap: 4px;
    padding-bottom: 1px;
  }

  .nav-list button {
    min-height: 44px;
    padding: 0 12px;
    font-size: 14px;
  }

  .site-main {
    padding-top: 18px;
  }

  .hero-slider {
    height: 390px;
    border-radius: 10px;
  }

  .hero-slide {
    align-items: flex-end;
    padding: 22px;
  }

  .hero-slide::before {
    background: linear-gradient(180deg, rgba(5, 26, 52, 0.16) 0%, rgba(5, 26, 52, 0.82) 100%);
  }

  .hero-slide__content h2 {
    font-size: 24px;
  }

  .hero-slide__content p {
    font-size: 14px;
    line-height: 1.75;
  }

  .hero-slider__dots {
    right: 18px;
    bottom: 16px;
  }

  .section-heading {
    margin-top: 28px;
  }

  .intro-card,
  .policy-card,
  .solution-card,
  .guide-card,
  .about-card,
  .partner-card,
  .union-card,
  .recruitment-card,
  .guide-tip-card,
  .qrcode-card,
  .download-card,
  .contact-panel {
    padding: 20px;
  }
}
</style>
