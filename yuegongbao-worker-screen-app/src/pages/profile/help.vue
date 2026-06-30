<template>
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">帮助中心</view>
      <view class="worker-subtitle">常见问题、服务指引与意见反馈</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">快捷入口</view>
      </view>
      <view v-if="quickActions.length" class="quick-grid">
        <view v-for="item in quickActions" :key="item.key" class="quick-item" @click="openQuickAction(item)">
          <view class="entry-item__icon" :class="'entry-item__icon--' + getEntryIcon(item).tone">
            <text class="entry-item__glyph">{{ getEntryIcon(item).glyph }}</text>
          </view>
          <view class="quick-item__main">
            <view class="quick-item__title">{{ item.label }}</view>
            <view class="quick-item__summary">{{ item.summary }}</view>
          </view>
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
      <view class="form-field">
        <view class="form-field__label">反馈标题</view>
        <input v-model="feedback.title" class="form-input" placeholder="反馈标题" />
      </view>
      <view class="form-field">
        <view class="form-field__label">问题或建议</view>
        <textarea v-model="feedback.content" class="form-textarea" placeholder="请输入问题或建议" />
      </view>
      <view class="form-field">
        <view class="form-field__label">联系电话（选填）</view>
        <input v-model="feedback.contactMobile" class="form-input" placeholder="联系电话（选填）" />
      </view>
      <button class="worker-button" @click="submitFeedback">提交反馈</button>
    </view>
  </view>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { createFeedback, getHelpList, getLegalHotline } from '../../api/screen'
import { normalizeWorkerJumpTarget, openWorkerJumpTarget } from '../../utils/worker-jump'
import { resolveEntryIcon } from '../../utils/entry-icon'

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

function getEntryIcon(item) {
  return resolveEntryIcon(item)
}

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
.service-actions button {
  flex: 1;
}
</style>
