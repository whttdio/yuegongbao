<template>
  <div class="official-site">
    <header class="site-header">
      <div class="site-container site-header__inner">
        <div class="brand-block">
          <div class="brand-shield">安</div>
          <div class="brand-copy">
            <h1>{{ siteConfig.title }}</h1>
            <p>{{ siteConfig.subtitle }}</p>
          </div>
        </div>
        <PortalSearchPanel
          portal-code="azb"
          :nav-items="navItems"
          :search-content="searchContent"
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
                <h3>平台介绍</h3>
                <p>围绕安责险、设备智控、工伤预防与风险减量形成统一监管入口</p>
              </div>
            </div>
            <div class="intro-card">
              <h4>{{ intro.title || '省级安责保监管门户' }}</h4>
              <p>{{ intro.summary }}</p>
              <div class="intro-metrics">
                <div v-for="item in introPillars" :key="item.label" class="intro-metric">
                  <strong>{{ item.value }}</strong>
                  <span>{{ item.label }}</span>
                </div>
              </div>
            </div>
          </div>

          <aside class="quick-panel">
            <div :ref="(el) => setSectionRef('news', el)" class="site-section" data-nav-key="news">
              <div class="section-heading section-heading--compact">
                <span class="section-heading__bar" />
                <div>
                  <h3>新闻中心</h3>
                  <p>要闻速览</p>
                </div>
              </div>
              <div class="card-stack">
                <article v-for="item in newsList" :key="item.contentId || item.title" class="news-card is-clickable" @click="openContent(item)">
                  <h4>{{ item.title }}</h4>
                  <p>{{ item.summary }}</p>
                  <div class="news-card__meta">
                    <span>{{ item.date }}</span>
                    <span>{{ item.source }}</span>
                  </div>
                </article>
              </div>
            </div>
          </aside>
        </section>

        <section :ref="(el) => setSectionRef('policy', el)" class="site-section" data-nav-key="policy">
          <div class="section-heading">
            <span class="section-heading__bar" />
            <div>
              <h3>政策法规</h3>
              <p>围绕安全生产、安责险、工伤预防和数字监管的制度依据</p>
            </div>
          </div>
          <div class="policy-grid">
            <article v-for="item in policyList" :key="item.contentId || item.title" class="policy-card is-clickable" @click="openContent(item)">
              <span class="policy-card__type">{{ item.type }}</span>
              <h4>{{ item.title }}</h4>
              <p>{{ item.summary }}</p>
              <span class="policy-card__date">{{ item.date }}</span>
            </article>
          </div>
        </section>

        <section :ref="(el) => setSectionRef('solution', el)" class="site-section" data-nav-key="solution">
          <div class="section-heading">
            <span class="section-heading__bar" />
            <div>
              <h3>解决方案</h3>
              <p>保持参考稿的栏目结构，突出监管、设备、保险和风险画像四条主线</p>
            </div>
          </div>
          <div class="solution-grid">
            <article v-for="item in solutions" :key="item.contentId || item.title" class="solution-card is-clickable" @click="openContent(item)">
              <span class="solution-card__icon">{{ item.icon }}</span>
              <h4>{{ item.title }}</h4>
              <p>{{ item.summary }}</p>
            </article>
          </div>
        </section>

        <section :ref="(el) => setSectionRef('data', el)" class="site-section" data-nav-key="data">
          <div class="section-heading">
            <span class="section-heading__bar" />
            <div>
              <h3>数据开放</h3>
              <p>展示监管数据概览与趋势分析，页面布局尽量贴近参考稿</p>
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
            <p>近12个月安责险覆盖率与设备在线率趋势</p>
          </div>
        </section>

        <section :ref="(el) => setSectionRef('download', el)" class="site-section" data-nav-key="download">
          <div class="section-heading">
            <span class="section-heading__bar" />
            <div>
              <h3>下载专区</h3>
              <p>操作手册、接入指南与制度模板统一发布</p>
            </div>
          </div>
          <div class="download-grid">
            <article v-for="item in downloadItems" :key="item.contentId || item.title" class="download-card is-clickable" @click="handleDownload(item)">
              <div>
                <h4>{{ item.title }}</h4>
                <p>{{ item.summary }}</p>
              </div>
              <span>{{ item.type }}</span>
            </article>
          </div>
        </section>

        <section :ref="(el) => setSectionRef('contact', el)" class="site-section" data-nav-key="contact">
          <div class="section-heading">
            <span class="section-heading__bar" />
            <div>
              <h3>联系我们</h3>
              <p>保留参考稿中的政务门户式底部联系信息布局</p>
            </div>
          </div>
          <div class="contact-panel">
            <div class="contact-panel__main">
              <h4>{{ contactInfo.contactTitle }}</h4>
              <p>{{ contactInfo.contactSummary }}</p>
            </div>
            <div class="contact-list">
              <span>电话：{{ contactInfo.phone }}</span>
              <span>邮箱：{{ contactInfo.email }}</span>
              <span>地址：{{ contactInfo.address }}</span>
            </div>
          </div>
        </section>
      </div>
    </main>

    <footer class="site-footer">
      <div class="site-container footer-grid">
        <div>
          <h4>安责保平台</h4>
          <p>广东省安全生产风险防范AI监管平台</p>
          <p>安责险覆盖 · 设备智控 · 风险减量 · 隐患闭环</p>
        </div>
        <div>
          <h4>相关链接</h4>
          <a href="javascript:void(0)">广东省人民政府</a>
          <a href="javascript:void(0)">广东省应急管理厅</a>
          <a href="javascript:void(0)">国家金融监督管理总局广东监管局</a>
        </div>
        <div>
          <h4>技术支持</h4>
          <a href="javascript:void(0)">平台操作手册</a>
          <a href="javascript:void(0)">设备接入指南</a>
          <a href="javascript:void(0)">常见问题</a>
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
import { openPortalContent, openPortalDownload, resolvePortalAssetUrl } from '@/utils/portalNavigation'

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
  downloadItems,
  contactInfo,
  footerCopyright,
  loadHome,
  searchContent
} = usePortalHome('azb')

const navItems = [
  { key: 'home', label: '首页' },
  { key: 'intro', label: '平台介绍' },
  { key: 'news', label: '新闻中心' },
  { key: 'policy', label: '政策法规' },
  { key: 'solution', label: '解决方案' },
  { key: 'data', label: '数据开放' },
  { key: 'download', label: '下载专区' },
  { key: 'contact', label: '联系我们' }
]

const introPillars = computed(() => intro.value.pillars || [])
const policyList = computed(() => {
  const data = policyData.value || {}
  return ['national', 'guangdong', 'interpretation']
    .flatMap(key => data[key] || [])
})

const sectionRefs = {
  home: null,
  intro: null,
  news: null,
  policy: null,
  solution: null,
  data: null,
  download: null,
  contact: null
}

const activeNav = ref('home')
const activeSlide = ref(0)
const trendChartRef = ref(null)

let slideTimer = null
let trendChart = null

const setSectionRef = (key, element) => {
  sectionRefs[key] = element
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

const handleSearchSection = (sectionKey) => {
  scrollToSection(sectionKey)
}

const openContent = (item) => {
  if (!item?.contentId) {
    ElMessage.info('该内容暂未配置详情')
    return
  }
  openPortalContent(router, item.contentId, { portalCode: 'azb' })
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
    .map((item) => {
      const element = sectionRefs[item.key]
      if (!element) {
        return null
      }
      const rect = element.getBoundingClientRect()
      return {
        key: item.key,
        top: rect.top
      }
    })
    .filter(Boolean)

  const current = entries.find((item) => item.top >= 0 && item.top <= 260)
  if (current) {
    activeNav.value = current.key
    return
  }

  const fallback = [...entries].reverse().find((item) => item.top < 260)
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
  const insuranceRates = trendData.value.expandRates?.length
    ? trendData.value.expandRates
    : [72, 74, 76, 79, 82, 85, 87, 89, 91, 92, 93, 94]
  const deviceRates = trendData.value.closeRates?.length
    ? trendData.value.closeRates
    : [80, 82, 83, 85, 86, 88, 89, 90, 91, 92, 93, 94]
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
      min: 50,
      axisLabel: { color: '#56738f' },
      splitLine: { lineStyle: { color: '#e7edf3' } }
    },
    series: [
      {
        name: '安责险覆盖率 (%)',
        type: 'line',
        smooth: true,
        symbolSize: 8,
        itemStyle: { color: '#0f2b3d' },
        lineStyle: { width: 3, color: '#0f2b3d' },
        data: insuranceRates
      },
      {
        name: '设备在线率 (%)',
        type: 'bar',
        barWidth: '30%',
        itemStyle: { color: '#2c7da0', borderRadius: [6, 6, 0, 0] },
        data: deviceRates
      }
    ]
  })
}

const handleResize = () => {
  trendChart?.resize()
  updateActiveNavByScroll()
}

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
  window.removeEventListener('resize', handleResize)
  window.removeEventListener('scroll', updateActiveNavByScroll)
  trendChart?.dispose()
  trendChart = null
})
</script>

<style lang="scss" scoped>
.official-site {
  min-height: 100vh;
  background: #f3f6fa;
  color: #18354d;
}

.site-container {
  width: min(1280px, calc(100% - 48px));
  margin: 0 auto;
}

.site-header {
  background: #ffffff;
  box-shadow: 0 8px 24px rgba(12, 41, 67, 0.08);
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
  background: linear-gradient(180deg, #0f2b3d 0%, #163c56 100%);
  box-shadow: 0 8px 18px rgba(15, 43, 61, 0.24);
  color: #ffffff;
  font-size: 24px;
  font-weight: 700;
}

.brand-copy h1 {
  margin: 0;
  color: #0f2b3d;
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
  color: #18354d;
  font-size: 14px;
  outline: none;
}

.search-bar button {
  height: 38px;
  padding: 0 20px;
  border: none;
  border-radius: 22px;
  background: #0f2b3d;
  color: #ffffff;
  font-size: 14px;
  cursor: pointer;
}

.site-nav {
  background: #0f2b3d;
}

.nav-list {
  display: flex;
  gap: 44px;
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
  font-size: 16px;
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
  height: 520px;
  overflow: hidden;
  border-radius: 18px;
  box-shadow: 0 20px 40px rgba(15, 43, 61, 0.16);
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
  font-size: 34px;
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
  grid-template-columns: minmax(0, 1.8fr) minmax(320px, 1fr);
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
  background: #0f2b3d;
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
.contact-panel {
  border: 1px solid #e5edf4;
  border-radius: 18px;
  background: #ffffff;
  box-shadow: 0 8px 18px rgba(15, 43, 61, 0.05);
}

.intro-card {
  padding: 24px 28px;
}

.intro-card h4 {
  margin: 0 0 12px;
  font-size: 22px;
  color: #18354d;
}

.intro-card p {
  margin: 0;
  color: #5f758a;
  line-height: 1.9;
}

.intro-metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  margin-top: 24px;
}

.intro-metric {
  padding: 18px;
  border-radius: 14px;
  background: linear-gradient(180deg, #f8fbff 0%, #eef5fb 100%);
}

.intro-metric strong {
  display: block;
  color: #0f2b3d;
  font-size: 28px;
}

.intro-metric span {
  display: block;
  margin-top: 6px;
  color: #6b8196;
  font-size: 13px;
}

.card-stack {
  display: grid;
  gap: 16px;
}

.news-card {
  padding: 20px 22px;
}

.news-card h4 {
  margin: 0 0 10px;
  color: #18354d;
  font-size: 18px;
  line-height: 1.6;
}

.news-card p {
  margin: 0;
  color: #5f758a;
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

.policy-grid,
.solution-grid,
.download-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 20px;
}

.policy-grid,
.solution-grid,
.stats-grid,
.download-grid,
.contact-panel {
  margin-top: 6px;
}

.policy-card,
.solution-card,
.download-card {
  padding: 22px 24px;
}

.policy-card__type {
  display: inline-flex;
  padding: 4px 10px;
  border-radius: 999px;
  background: #edf4fb;
  color: #2d628b;
  font-size: 12px;
}

.policy-card h4,
.solution-card h4,
.download-card h4,
.contact-panel h4 {
  margin: 14px 0 12px;
  color: #18354d;
  font-size: 18px;
}

.policy-card p,
.solution-card p,
.download-card p,
.contact-panel p {
  margin: 0;
  color: #5f758a;
  line-height: 1.8;
  font-size: 14px;
}

.policy-card__date {
  display: block;
  margin-top: 16px;
  color: #73889c;
  font-size: 13px;
}

.solution-card__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: #edf4fb;
  font-size: 24px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 18px;
}

.stats-card {
  padding: 24px 20px;
  text-align: center;
}

.stats-card strong {
  display: block;
  color: #0f2b3d;
  font-size: 34px;
  line-height: 1.2;
}

.stats-card span {
  display: block;
  margin-top: 10px;
  color: #5f758a;
  font-size: 14px;
}

.stats-card em {
  display: block;
  margin-top: 8px;
  color: #2d628b;
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

.download-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.download-card span {
  flex: 0 0 auto;
  padding: 6px 12px;
  border-radius: 999px;
  background: #0f2b3d;
  color: #ffffff;
  font-size: 12px;
}

.contact-panel {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 24px 28px;
}

.contact-panel__main {
  max-width: 560px;
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
  background: #0a1e2a;
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
  .download-grid,
  .footer-grid {
    grid-template-columns: 1fr;
  }

  .stats-grid,
  .intro-metrics {
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
    font-size: 24px;
  }

  .stats-grid,
  .intro-metrics {
    grid-template-columns: 1fr;
  }
}

.is-clickable {
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 24px rgba(15, 43, 61, 0.12);
  }
}

.official-site {
  background:
    linear-gradient(180deg, #eef4f8 0%, #f7fafc 36%, #eef3f7 100%);
  color: #173247;
  font-family: "Microsoft YaHei", "PingFang SC", "Helvetica Neue", Arial, sans-serif;
}

.site-container {
  width: min(1280px, calc(100% - 56px));
}

.site-header {
  position: sticky;
  top: 0;
  z-index: 25;
  border-bottom: 1px solid rgba(196, 211, 223, 0.72);
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 10px 28px rgba(13, 41, 61, 0.06);
}

.site-header__inner {
  min-height: 76px;
  padding: 12px 0;
}

.brand-shield {
  width: 50px;
  height: 56px;
  border-radius: 10px 10px 16px 16px;
  background: linear-gradient(180deg, #092f47 0%, #17606c 100%);
  box-shadow: 0 10px 20px rgba(9, 47, 71, 0.18);
}

.brand-copy h1 {
  color: #082f49;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 0;
}

.brand-copy p {
  color: #607789;
}

.site-header :deep(.portal-search button) {
  background: #17606c;
}

.site-header :deep(.portal-search button:hover) {
  background: #0f4e59;
}

.site-nav {
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  background: #0b2d42;
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
  color: rgba(255, 255, 255, 0.86);
  font-size: 15px;
  transition: background-color 0.18s ease, color 0.18s ease;
}

.nav-list button:hover,
.nav-list li.active button {
  background: rgba(255, 255, 255, 0.08);
  color: #ffffff;
}

.nav-list li.active button::after {
  position: absolute;
  right: 14px;
  bottom: 0;
  left: 14px;
  height: 3px;
  border-radius: 3px 3px 0 0;
  background: #e7b84f;
  content: "";
}

.site-main {
  padding: 32px 0 52px;
}

.site-section {
  scroll-margin-top: 138px;
}

.hero-slider {
  height: 500px;
  border: 1px solid rgba(255, 255, 255, 0.72);
  border-radius: 12px;
  background: #0b2d42;
  box-shadow: 0 22px 48px rgba(12, 43, 64, 0.14);
}

.hero-slide {
  align-items: center;
  padding: 44px;
}

.hero-slide::before {
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, rgba(5, 23, 37, 0.78) 0%, rgba(5, 23, 37, 0.42) 48%, rgba(5, 23, 37, 0.12) 100%);
  content: "";
}

.hero-slide__content {
  position: relative;
  width: min(680px, 100%);
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
  font-size: 36px;
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
  background: rgba(6, 28, 45, 0.36);
}

.hero-slider__dots button {
  width: 18px;
  height: 6px;
  border-radius: 999px;
  transition: width 0.18s ease, background-color 0.18s ease;
}

.hero-slider__dots button.active {
  width: 32px;
  background: #e7b84f;
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
  background: #17606c;
}

.section-heading h3 {
  color: #132f45;
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
.contact-panel {
  border-color: #dde8f0;
  border-radius: 12px;
  background: #ffffff;
  box-shadow: 0 12px 28px rgba(13, 41, 61, 0.06);
}

.intro-card,
.policy-card,
.solution-card,
.download-card,
.contact-panel {
  padding: 24px;
}

.intro-metric,
.policy-card__type,
.solution-card__icon {
  border-radius: 8px;
  background: #edf5f7;
  color: #17606c;
}

.intro-metric strong,
.stats-card strong {
  color: #0b4d5a;
}

.stats-card {
  padding: 24px 18px;
}

.stats-card em {
  color: #17606c;
}

.download-card span {
  border-radius: 6px;
  background: #17606c;
}

.contact-panel {
  margin-top: 22px;
}

.site-footer {
  margin-top: 12px;
  background: #082536;
}

.is-clickable {
  transition: border-color 0.18s ease, transform 0.18s ease, box-shadow 0.18s ease;
}

.is-clickable:hover {
  border-color: rgba(23, 96, 108, 0.44);
  box-shadow: 0 16px 32px rgba(13, 41, 61, 0.1);
}

@media (prefers-reduced-motion: reduce) {
  .hero-slide,
  .hero-slider__dots button,
  .nav-list button,
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
    background: linear-gradient(180deg, rgba(5, 23, 37, 0.18) 0%, rgba(5, 23, 37, 0.82) 100%);
  }

  .hero-slide__content h2 {
    font-size: 25px;
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
  .download-card,
  .contact-panel {
    padding: 20px;
  }
}
</style>
