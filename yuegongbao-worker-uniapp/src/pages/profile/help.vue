<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="worker-title">帮助中心</view>
      <view v-if="quickActions.length" class="quick-grid">
        <view v-for="item in quickActions" :key="item.key" class="quick-item" @click="openQuickAction(item)">
          <view class="quick-item__title">{{ item.label }}</view>
          <view class="quick-item__summary">{{ item.summary }}</view>
        </view>
      </view>
      <view v-if="rows.length">
        <view v-for="item in rows" :key="item.articleKey" class="list-row" @click="openDetail(item)">
          <view>
            <view class="list-row__title">{{ item.title }}</view>
            <view class="list-row__subtitle">{{ item.summary }}</view>
          </view>
          <view class="worker-tag">{{ item.category }}</view>
        </view>
      </view>
      <view v-else class="worker-empty">暂无帮助内容</view>
    </view>

    <view class="worker-card">
      <view class="worker-title">服务承接</view>
      <view v-if="serviceCards.length">
        <view v-for="item in serviceCards" :key="item.key" class="service-panel">
          <view class="service-panel__title">{{ item.title }}</view>
          <view class="service-panel__subtitle">{{ item.subtitle }}</view>
          <view class="service-panel__desc">{{ item.summary }}</view>
          <view class="service-actions">
            <button class="worker-button" @click="openServiceCard(item)">{{ item.actionLabel }}</button>
          </view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">热门问题</view>
        <view class="worker-tag">{{ faqRows.length }} 条</view>
      </view>
      <view v-if="faqRows.length">
        <view v-for="item in faqRows" :key="item.faqKey" class="list-row" @click="openFaq(item)">
          <view>
            <view class="list-row__title">{{ item.title }}</view>
            <view class="list-row__subtitle">{{ item.summary }}</view>
          </view>
          <view class="worker-tag">{{ item.category }}</view>
        </view>
      </view>
      <view v-else class="worker-empty">暂无热门问题</view>
    </view>

    <view class="worker-card">
      <view class="worker-title">意见反馈</view>
      <input v-model="feedback.title" class="form-input" placeholder="反馈标题" />
      <textarea v-model="feedback.content" class="form-textarea" placeholder="请输入问题或建议" />
      <input v-model="feedback.contactMobile" class="form-input" placeholder="联系电话（选填）" />
      <button class="worker-button" @click="submitFeedback">提交反馈</button>
    </view>
  </view>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { createFeedback, getHelpList, getLegalHotline } from '../../api/worker'
import { normalizeWorkerJumpTarget, openWorkerJumpTarget } from '../../utils/worker-jump'

const EXPECTED_HELP_CHAIN_PAGES = ['帮助中心', '帮助详情', '热门问题', '服务承接', '意见反馈']
const rows = ref([])
const hotline = ref({})
const quickActions = ref([])
const serviceCards = ref([])
const faqRows = ref([])
const fallbackHotlineNumber = '12351'
const helpLastLoadedAt = ref('')
const helpLastSubmittedAt = ref('')
const helpLastActionAt = ref('')
const helpLastMessage = ref('')
const feedback = reactive({
  title: '',
  content: '',
  contactMobile: ''
})
const helpSummaryText = computed(() => {
  return `帮助 ${rows.value.length} 条 / 快捷 ${quickActions.value.length} 个 / FAQ ${faqRows.value.length} 条 / 服务卡 ${serviceCards.value.length} 张`
})
const helpFeedbackSummaryText = computed(() => {
  const completedCount = [feedback.title, feedback.content, feedback.contactMobile]
    .filter((item) => String(item || '').trim()).length
  return `${completedCount}/3 项`
})
const latestHelpItemText = computed(() => {
  if (!rows.value.length) {
    return '暂无帮助内容'
  }
  const latest = rows.value[0] || {}
  return `${latest.title || '-'} / ${latest.category || '-'}`
})
const helpChainCoverageText = computed(() => EXPECTED_HELP_CHAIN_PAGES.join(' / '))
const helpConsistencyText = computed(() => {
  if (!rows.value.length && !faqRows.value.length && !serviceCards.value.length) {
    return '当前无帮助、FAQ 和服务卡，需结合真实接口复核'
  }
  return `帮助 ${rows.value.length} 条 / FAQ ${faqRows.value.length} 条 / 服务卡 ${serviceCards.value.length} 张`
})
const helpSnapshotText = computed(() => {
  return [
    '## 帮助中心验收摘要',
    `- 链路核对：${helpChainCoverageText.value}`,
    `- 最近联动：${helpLastActionAt.value || '-'}`,
    `- 最近加载：${helpLastLoadedAt.value || '-'}`,
    `- 最近反馈：${helpLastSubmittedAt.value || '-'}`,
    `- 数据概览：${helpSummaryText.value}`,
    `- 反馈完整度：${helpFeedbackSummaryText.value}`,
    `- 最近一条帮助：${latestHelpItemText.value}`,
    `- 服务核对：${helpConsistencyText.value}`,
    `- 说明：${helpLastMessage.value || '-'}`,
    '- 链路关联：帮助中心 / 热门问题 / 服务承接 / 意见反馈'
  ].join('\n')
})

function resolveHotlineNumber(value = {}) {
  const phoneNumber = String(value?.phoneNumber || '').trim()
  if (phoneNumber) {
    return phoneNumber
  }
  const displayText = String(value?.displayText || '')
  const matched = displayText.match(/1\d{4,}/)
  return matched?.[0] || fallbackHotlineNumber
}

async function loadData() {
  try {
    const [data, phone] = await Promise.all([getHelpList(), getLegalHotline()])
    rows.value = data?.rows || []
    hotline.value = data?.hotline || phone || {}
    quickActions.value = data?.quickActions || []
    serviceCards.value = data?.serviceCards || []
    faqRows.value = data?.faqRows || []
    helpLastLoadedAt.value = new Date().toLocaleString()
    helpLastMessage.value = rows.value.length || faqRows.value.length
      ? '帮助中心聚合已加载，可核对帮助条目、FAQ 和服务承接'
      : '当前暂无帮助内容，可等待平台下发或通过反馈入口提交问题'
  } catch (error) {
    helpLastLoadedAt.value = new Date().toLocaleString()
    helpLastMessage.value = error.message || '加载帮助失败'
    uni.showToast({ title: error.message || '加载帮助失败', icon: 'none' })
  }
}

function resolveActionTarget(item) {
  if (!item || typeof item !== 'object') {
    return null
  }
  return normalizeWorkerJumpTarget(item)
}

function openDetail(item) {
  if (!item?.articleKey) {
    return
  }
  helpLastActionAt.value = new Date().toLocaleString()
  helpLastMessage.value = `已打开帮助详情：${item.title || '-'}`
  uni.navigateTo({ url: `/pages/profile/help-detail?articleKey=${item.articleKey}` })
}

function openQuickAction(item) {
  if (item?.key === 'help-feedback') {
    return
  }
  const target = resolveActionTarget(item)
  if (!target?.path) {
    return
  }
  helpLastActionAt.value = new Date().toLocaleString()
  helpLastMessage.value = `已打开快捷帮助：${item.label || target.path}`
  openWorkerJumpTarget(target)
}

async function submitFeedback() {
  if (!feedback.title || !feedback.content) {
    helpLastSubmittedAt.value = new Date().toLocaleString()
    helpLastMessage.value = '反馈标题或内容未填写完整，前端已拦截提交'
    uni.showToast({ title: '请完善反馈内容', icon: 'none' })
    return
  }
  try {
    helpLastSubmittedAt.value = new Date().toLocaleString()
    helpLastActionAt.value = helpLastSubmittedAt.value
    await createFeedback(feedback)
    feedback.title = ''
    feedback.content = ''
    feedback.contactMobile = ''
    helpLastMessage.value = '意见反馈提交成功，可等待平台回访处理'
    uni.showToast({ title: '提交成功', icon: 'none' })
  } catch (error) {
    helpLastSubmittedAt.value = new Date().toLocaleString()
    helpLastMessage.value = error.message || '提交失败'
    uni.showToast({ title: error.message || '提交失败', icon: 'none' })
  }
}

function openServiceCard(item) {
  if (item?.phoneNumber || item?.displayText) {
    helpLastActionAt.value = new Date().toLocaleString()
    helpLastMessage.value = `已呼叫服务承接：${item.displayText || resolveHotlineNumber(item)}`
    uni.makePhoneCall({ phoneNumber: resolveHotlineNumber(item) })
    return
  }
  const target = resolveActionTarget(item)
  if (!target?.path) {
    return
  }
  helpLastActionAt.value = new Date().toLocaleString()
  helpLastMessage.value = `已打开服务承接：${item.title || target.path}`
  openWorkerJumpTarget(target)
}

function openFaq(item) {
  if (!item?.faqKey) {
    return
  }
  helpLastActionAt.value = new Date().toLocaleString()
  helpLastMessage.value = `已打开热门问题：${item.title || '-'}`
  uni.navigateTo({ url: `/pages/legal/faq-detail?faqKey=${item.faqKey}` })
}

function copyText(content, successTitle) {
  if (!content) {
    uni.showToast({ title: '暂无可复制内容', icon: 'none' })
    return
  }
  uni.setClipboardData({
    data: content,
    success: () => uni.showToast({ title: successTitle, icon: 'none' }),
    fail: () => uni.showToast({ title: '复制失败，请改用截图', icon: 'none' })
  })
}

onShow(loadData)
</script>

<style lang="scss">
.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 18rpx;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18rpx;
  margin: 18rpx 0 10rpx;
}

.quick-item {
  padding: 22rpx;
  border-radius: 22rpx;
  background: #f4f8fd;
}

.quick-item__title {
  font-size: 28rpx;
  font-weight: 700;
  color: #16324f;
}

.quick-item__summary {
  margin-top: 10rpx;
  font-size: 22rpx;
  line-height: 1.6;
  color: #7890aa;
}

.list-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 22rpx 0;
  border-bottom: 1rpx solid #edf2f7;
}

.list-row:last-child {
  border-bottom: none;
}

.list-row__title {
  font-size: 28rpx;
  font-weight: 600;
  color: #16324f;
}

.list-row__subtitle {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #7890aa;
}

.service-panel {
  margin-top: 18rpx;
  padding: 24rpx;
  border-radius: 20rpx;
  background: linear-gradient(135deg, #eef5ff 0%, #f8fbff 100%);
}

.service-panel__title {
  font-size: 30rpx;
  font-weight: 700;
  color: #16324f;
}

.service-panel__subtitle {
  margin-top: 8rpx;
  font-size: 24rpx;
  color: #1f6fd6;
}

.service-panel__desc {
  margin-top: 12rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: #58738f;
}

.service-actions {
  display: flex;
  gap: 18rpx;
  margin-top: 22rpx;
}

.service-actions button {
  flex: 1;
}

.form-input,
.form-textarea {
  width: 100%;
  margin-top: 18rpx;
  padding: 20rpx 24rpx;
  border-radius: 18rpx;
  background: #f5f8fc;
  box-sizing: border-box;
  font-size: 28rpx;
  color: #16324f;
}

.form-textarea {
  min-height: 180rpx;
}

.section-head--sub {
  margin-top: 20rpx;
}

.clear-action {
  font-size: 24rpx;
  color: #1f6fd6;
}

.worker-title--small {
  font-size: 28rpx;
}

.detail-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
  padding: 18rpx 0;
  border-bottom: 1rpx solid #edf2f7;
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-row__label {
  font-size: 26rpx;
  color: #5f7893;
}

.detail-row__value {
  flex: 1;
  text-align: right;
  font-size: 26rpx;
  color: #16324f;
  line-height: 1.6;
  word-break: break-all;
}

.result-block {
  margin-top: 16rpx;
  padding: 22rpx 24rpx;
  border-radius: 20rpx;
  background: #f5f8fc;
}

.result-block__label {
  font-size: 22rpx;
  color: #7890aa;
}

.result-block__value {
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: #16324f;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
