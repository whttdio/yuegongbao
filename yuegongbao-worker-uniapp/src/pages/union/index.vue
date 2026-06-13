<template>
  <view class="worker-page union-page">
    <view class="union-hero worker-card">
      <view class="union-hero__content">
        <view class="union-hero__kicker">工会服务</view>
        <view class="union-hero__title">维权咨询、合同查阅和工会通知</view>
        <view class="union-hero__desc">
          常用服务已放在首页，遇到合同、欠薪、工时等问题，可以先从这里进入。
        </view>
      </view>
      <view class="union-hotline" @click="callHotline">
        <view>
          <view class="union-hotline__label">服务热线</view>
          <view class="union-hotline__value">{{ safeHotlineText }}</view>
        </view>
        <view class="union-hotline__action">拨打</view>
      </view>
    </view>

    <view class="entry-grid union-grid">
      <view
        v-for="item in displayQuickActions"
        :key="item.key"
        class="entry-item"
        @click="openAction(item)"
      >
        <view class="entry-item__mark">{{ item.mark }}</view>
        <view class="entry-item__content">
          <view class="entry-item__label">{{ item.label }}</view>
          <view class="entry-item__hint">{{ item.hint }}</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view>
          <view class="worker-title">维权案例</view>
          <view class="section-desc">参考真实处理路径，快速判断下一步材料。</view>
        </view>
      </view>
      <view v-if="displayCases.length">
        <view v-for="item in displayCases" :key="item.caseKey" class="list-row" @click="openCase(item)">
          <view class="list-row__title">{{ item.title }}</view>
          <view class="list-row__subtitle">{{ item.summary }}</view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">暂无维权案例</view>
        <view class="worker-empty__desc">案例同步后会在这里展示。需要咨询时可以先进入法律咨询或拨打工会热线。</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view>
          <view class="worker-title">工会通知</view>
          <view class="section-desc">查看近期工会服务、政策和活动安排。</view>
        </view>
      </view>
      <view v-if="displayNotices.length">
        <view v-for="item in displayNotices" :key="item.noticeKey" class="list-row" @click="openNotice(item)">
          <view class="list-row__title">{{ item.title }}</view>
          <view class="list-row__subtitle">{{ item.summary }}</view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">暂无工会通知</view>
        <view class="worker-empty__desc">通知发布后会同步到这里，当前可先使用合同查阅和维权咨询入口。</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view>
          <view class="worker-title">集体合同查阅</view>
          <view class="section-desc">查看公开集体合同，也可以进入个人劳动合同。</view>
        </view>
        <view class="more-link" @click="openContracts">全部合同</view>
      </view>
      <view v-if="displayContracts.length">
        <view v-for="item in displayContracts" :key="item.contractKey" class="list-row" @click="openContract(item)">
          <view class="list-row__title">{{ item.title }}</view>
          <view class="list-row__subtitle">{{ item.enterpriseName }} / {{ item.period }}</view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无可查阅合同</view>
        <view class="worker-empty__desc">集体合同暂未同步时，可以先查看我的劳动合同，或继续关注工会通知。</view>
        <view class="union-empty-actions">
          <button class="worker-button worker-button--secondary" @click="goLaborContracts">我的合同</button>
          <button class="worker-button" @click="openContracts">查看合同页</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getLaborContractList, getUnionCases, getUnionNotices, getUnionServiceHome } from '../../api/worker'
import { normalizeWorkerJumpTarget, openWorkerJumpTarget } from '../../utils/worker-jump'

const home = ref({})
const cases = ref([])
const notices = ref([])
const contracts = ref([])
const laborContracts = ref([])
const unionLastLoadedAt = ref('')
const unionLastActionAt = ref('')
const unionLastMessage = ref('')
const fallbackHotlineNumber = '12351'
const fallbackHotlineText = '工会法律服务热线 12351'

const defaultQuickActions = [
  { key: 'rights', mark: '权', label: '我要维权', hint: '欠薪、工时、社保问题', path: '/pages/legal/index' },
  { key: 'consult', mark: '询', label: '法律咨询', hint: '先看指引，再提交诉求', path: '/pages/legal/index' },
  { key: 'contracts', mark: '合', label: '集体合同', hint: '查看公开合同清单', path: '/pages/union/contracts' },
  { key: 'hotline', mark: '线', label: '工会热线', hint: '一键拨打 12351', action: 'hotline' }
]

const fallbackCases = [
  {
    caseKey: 'fallback-rights-case',
    title: '制造业集体协商案例',
    summary: '通过集体协商确定工资增长机制和工时安排，保障派遣员工合法权益。',
    isFallback: true
  }
]

const fallbackNotices = [
  {
    noticeKey: 'fallback-union-notice',
    title: '工会服务指引',
    summary: '如需法律咨询、合同查阅或维权协助，可通过工会服务入口提交需求。',
    isFallback: true
  }
]

const safeHotlineText = computed(() => {
  return safeText(home.value.hotline && home.value.hotline.displayText, fallbackHotlineText)
})

const displayQuickActions = computed(() => {
  const actions = Array.isArray(home.value.quickActions) ? home.value.quickActions : []
  const sanitizedActions = actions
    .map(sanitizeAction)
    .filter((item) => item.label && (item.path || item.action))

  return sanitizedActions.length ? sanitizedActions : defaultQuickActions
})

const displayCases = computed(() => {
  const sanitizedCases = cases.value
    .map(sanitizeCase)
    .filter((item) => item.title && item.summary)

  return sanitizedCases.length ? sanitizedCases : fallbackCases
})

const displayNotices = computed(() => {
  const sanitizedNotices = notices.value
    .map(sanitizeNotice)
    .filter((item) => item.title && item.summary)

  return sanitizedNotices.length ? sanitizedNotices : fallbackNotices
})

const displayContracts = computed(() => {
  return contracts.value
    .map(sanitizeContract)
    .filter((item) => item.title)
})

function isBadText(value) {
  if (value === null || value === undefined) {
    return true
  }
  const text = String(value).trim()
  if (!text) {
    return true
  }
  const questionCount = (text.match(/\?/g) || []).length
  const questionRatio = questionCount / text.length
  const mojibakePattern = /[�锟鈥鈮]|(?:宸ヤ|鏈嶅|妗堜|閫氱|闆嗕|鍚堝|鐑|涓|鍙|褰撳|鏆傛|绾匡|椤?\/)/
  return questionRatio > 0.25 || mojibakePattern.test(text)
}

function safeText(value, fallback) {
  return isBadText(value) ? fallback : String(value).trim()
}

function sanitizeAction(item = {}, index = 0) {
  const fallback = defaultQuickActions[index % defaultQuickActions.length]
  const target = normalizeWorkerJumpTarget(item)
  return {
    ...fallback,
    ...item,
    key: safeText(item.key || item.id || fallback.key, fallback.key),
    mark: safeText(item.mark || fallback.mark, fallback.mark),
    label: safeText(item.label || item.name || item.title, fallback.label),
    hint: safeText(item.hint || item.description || item.summary, fallback.hint),
    path: target && target.path ? target.path : item.path || fallback.path,
    query: target && target.query ? target.query : item.query,
    action: item.action || fallback.action || ''
  }
}

function sanitizeCase(item = {}, index = 0) {
  return {
    ...item,
    caseKey: item.caseKey || item.id || `case-${index}`,
    title: safeText(item.title || item.name, `维权案例 ${index + 1}`),
    summary: safeText(item.summary || item.description || item.content, '查看案例详情，了解维权材料和处理流程。')
  }
}

function sanitizeNotice(item = {}, index = 0) {
  return {
    ...item,
    noticeKey: item.noticeKey || item.id || `notice-${index}`,
    title: safeText(item.title || item.name, `工会通知 ${index + 1}`),
    summary: safeText(item.summary || item.description || item.content, '查看通知详情，了解近期工会服务安排。')
  }
}

function sanitizeContract(item = {}, index = 0) {
  return {
    ...item,
    contractKey: item.contractKey || item.id || `contract-${index}`,
    title: safeText(item.title || item.contractName || item.name, '集体合同'),
    enterpriseName: safeText(item.enterpriseName || item.companyName, '企业名称待同步'),
    period: safeText(item.period || item.validPeriod || item.effectiveDate, '有效期以合同详情为准')
  }
}

function resolveHotlineNumber(value = {}) {
  const phoneNumber = String((value && value.phoneNumber) || '').trim()
  if (phoneNumber) {
    return phoneNumber
  }
  const displayText = String((value && value.displayText) || '')
  const matched = displayText.match(/1\d{4,}/)
  return (matched && matched[0]) || fallbackHotlineNumber
}

async function loadData() {
  try {
    const [homeResult, caseResult, noticeResult, laborResult] = await Promise.allSettled([
      getUnionServiceHome(),
      getUnionCases(),
      getUnionNotices(),
      getLaborContractList()
    ])
    if (homeResult.status !== 'fulfilled') {
      throw homeResult.reason
    }
    home.value = homeResult.value || {}
    cases.value = caseResult.status === 'fulfilled' ? caseResult.value?.rows || [] : []
    notices.value = noticeResult.status === 'fulfilled' ? noticeResult.value?.rows || [] : []
    laborContracts.value = laborResult.status === 'fulfilled' ? laborResult.value?.rows || [] : []
    contracts.value = Array.isArray(home.value.contractList) ? home.value.contractList : []
    unionLastLoadedAt.value = new Date().toLocaleString()

    const messageParts = []
    if (caseResult.status !== 'fulfilled') {
      messageParts.push('案例加载异常')
    }
    if (noticeResult.status !== 'fulfilled') {
      messageParts.push('通知加载异常')
    }
    if (laborResult.status !== 'fulfilled') {
      messageParts.push('我的劳动合同加载异常')
    }
    if (!contracts.value.length) {
      messageParts.push('当前未返回集体合同预览')
    }
    unionLastMessage.value = messageParts.length ? messageParts.join('，') : '工会服务数据已加载'
  } catch (error) {
    unionLastLoadedAt.value = new Date().toLocaleString()
    home.value = {}
    cases.value = []
    notices.value = []
    contracts.value = []
    laborContracts.value = []
    unionLastMessage.value = error.message || '加载工会服务失败'
    uni.showToast({ title: unionLastMessage.value, icon: 'none' })
  }
}

function openAction(item) {
  if (item.action === 'hotline') {
    callHotline()
    return
  }
  const target = normalizeWorkerJumpTarget(item)
  if (!target?.path) {
    return
  }
  unionLastActionAt.value = new Date().toLocaleString()
  unionLastMessage.value = `已打开工会快捷入口：${item.label || target.path}`
  openWorkerJumpTarget(target)
}

function openCase(item) {
  if (item.isFallback) {
    return
  }
  unionLastActionAt.value = new Date().toLocaleString()
  unionLastMessage.value = `已打开维权案例：${item.title || '-'}`
  uni.navigateTo({ url: `/pages/union/case-detail?caseKey=${item.caseKey}` })
}

function openNotice(item) {
  if (item.isFallback) {
    return
  }
  unionLastActionAt.value = new Date().toLocaleString()
  unionLastMessage.value = `已打开工会通知：${item.title || '-'}`
  uni.navigateTo({ url: `/pages/union/notice-detail?noticeKey=${item.noticeKey}` })
}

function openContract(item) {
  unionLastActionAt.value = new Date().toLocaleString()
  unionLastMessage.value = `已打开集体合同详情：${item.title || '-'}`
  uni.navigateTo({ url: `/pages/union/contract-detail?contractKey=${item.contractKey}` })
}

function openContracts() {
  unionLastActionAt.value = new Date().toLocaleString()
  unionLastMessage.value = '已前往集体合同列表页'
  uni.navigateTo({ url: '/pages/union/contracts' })
}

function goLaborContracts() {
  unionLastActionAt.value = new Date().toLocaleString()
  unionLastMessage.value = '已前往我的劳动合同页'
  uni.navigateTo({ url: '/pages/profile/labor-contracts' })
}

function callHotline() {
  const phoneNumber = resolveHotlineNumber(home.value.hotline)
  unionLastActionAt.value = new Date().toLocaleString()
  unionLastMessage.value = `已尝试拨打服务热线：${phoneNumber}`
  uni.makePhoneCall({ phoneNumber })
}

onShow(loadData)
</script>

<style lang="scss">
.union-page {
  padding-bottom: 40rpx;
}

.union-hero {
  position: relative;
  overflow: hidden;
  padding: 34rpx;
  color: #ffffff;
  background:
    radial-gradient(circle at 90% 0%, rgba(255, 255, 255, 0.22), transparent 34%),
    linear-gradient(135deg, #0f6d8f 0%, #16745f 100%);
}

.union-hero::after {
  content: '';
  position: absolute;
  right: -70rpx;
  bottom: -100rpx;
  width: 260rpx;
  height: 260rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.12);
}

.union-hero__content {
  position: relative;
  z-index: 1;
}

.union-hero__kicker {
  font-size: 24rpx;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.78);
}

.union-hero__title {
  max-width: 520rpx;
  margin-top: 12rpx;
  font-size: 40rpx;
  line-height: 1.28;
  font-weight: 800;
  letter-spacing: -0.02em;
}

.union-hero__desc {
  max-width: 560rpx;
  margin-top: 14rpx;
  font-size: 25rpx;
  line-height: 1.65;
  color: rgba(255, 255, 255, 0.82);
}

.union-hotline {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-top: 28rpx;
  padding: 22rpx 24rpx;
  border-radius: 20rpx;
  background: rgba(255, 255, 255, 0.14);
}

.union-hotline__label {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.74);
}

.union-hotline__value {
  margin-top: 6rpx;
  font-size: 29rpx;
  font-weight: 800;
  color: #ffffff;
}

.union-hotline__action {
  flex-shrink: 0;
  padding: 12rpx 22rpx;
  border-radius: 999rpx;
  background: #ffffff;
  color: #0f6d8f;
  font-size: 24rpx;
  font-weight: 800;
}

.worker-card + .worker-card,
.union-grid + .worker-card {
  margin-top: 24rpx;
}

.section-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 18rpx;
}

.section-desc {
  margin-top: 8rpx;
  font-size: 23rpx;
  line-height: 1.5;
  color: #6d8298;
}

.union-grid {
  margin-top: 24rpx;
}

.entry-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18rpx;
}

.entry-item {
  min-height: 132rpx;
  padding: 22rpx;
  border-radius: 22rpx;
  background: #ffffff;
  display: flex;
  align-items: center;
  gap: 18rpx;
}

.entry-item__mark {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 58rpx;
  height: 58rpx;
  border-radius: 18rpx;
  background: #e7f4f1;
  color: #16745f;
  font-size: 27rpx;
  font-weight: 800;
}

.entry-item__content {
  min-width: 0;
}

.entry-item__label {
  font-size: 27rpx;
  color: #16324f;
  font-weight: 800;
}

.entry-item__hint {
  margin-top: 8rpx;
  font-size: 21rpx;
  line-height: 1.35;
  color: #6d8298;
}

.more-link {
  flex-shrink: 0;
  padding-top: 4rpx;
  font-size: 24rpx;
  font-weight: 700;
  color: #16745f;
}

.list-row {
  padding: 24rpx 0;
  border-bottom: 1rpx solid #edf2f7;
}

.list-row:last-child {
  border-bottom: none;
}

.list-row__title {
  font-size: 28rpx;
  font-weight: 800;
  color: #16324f;
}

.list-row__subtitle {
  margin-top: 9rpx;
  font-size: 23rpx;
  color: #60758c;
  line-height: 1.65;
}

.union-empty-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 20rpx;
}

.union-empty-actions button {
  flex: 1;
}
</style>
