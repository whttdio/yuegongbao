<template>
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="section-head">
        <view class="worker-title">{{ detail.title || '-' }}</view>
        <view class="worker-tag">{{ detail.statusText || '-' }}</view>
      </view>
      <view class="worker-subtitle">{{ detail.consultType || '-' }}</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">咨询内容</view>
      </view>
      <view class="detail-block">{{ detail.content || '-' }}</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">处理进度</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">处理状态</view>
        <view class="detail-row__value">{{ detail.statusText || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">律师回复</view>
        <view class="detail-row__value">{{ detail.replyContent || '暂无回复' }}</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">咨询附件</view>
        <view class="worker-tag">{{ attachmentUrls.length }} 项</view>
      </view>
      <view v-if="attachmentUrls.length">
        <view v-for="(item, index) in attachmentUrls" :key="`${item}-${index}`" class="attachment-row">
          <view class="attachment-row__text">{{ item }}</view>
          <view class="attachment-row__action" @click="copyAttachment(item)">复制</view>
        </view>
      </view>
      <view v-else class="worker-empty">暂无咨询附件</view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getLegalConsultDetail } from '../../api/screen'

const detail = ref({})
const legalDetailLastLoadedAt = ref('')
const legalDetailLastActionAt = ref('')
const legalDetailLastMessage = ref('')

const attachmentUrls = computed(() => {
  const attachments = detail.value?.attachments || ''
  if (!attachments) {
    return []
  }
  return String(attachments).split(',').map((item) => item.trim()).filter(Boolean)
})

const legalDetailSummaryText = computed(() => {
  return `${detail.value.consultType || '-'} / ${detail.value.statusText || '-'} / ${detail.value.replyContent ? '有回复' : '暂无回复'}`
})
const legalDetailConsistencyText = computed(() => {
  const missingFields = []
  if (!detail.value.title) {
    missingFields.push('标题')
  }
  if (!detail.value.consultType) {
    missingFields.push('咨询类型')
  }
  if (!detail.value.statusText) {
    missingFields.push('状态')
  }
  if (missingFields.length) {
    return `详情缺少 ${missingFields.join('、')}`
  }
  return `${detail.value.statusText || '-'} / ${detail.value.replyContent ? '已回复' : '待回复'}`
})
const legalDetailReplyTraceText = computed(() => {
  if (detail.value.replyContent && attachmentUrls.value.length) {
    return `律师已回复 / 附件 ${attachmentUrls.value.length} 项，可核对消息通知与详情状态`
  }
  if (detail.value.replyContent) {
    return '已有律师回复，需结合咨询列表状态和消息通知复核'
  }
  if (attachmentUrls.value.length) {
    return `待回复 / 已提交 ${attachmentUrls.value.length} 项附件`
  }
  return '暂无回复且无附件，需结合真实咨询数据验收'
})
const legalDetailLinkageText = computed(() => {
  return '法律咨询 / 咨询详情 / 消息通知 / 附件取证'
})

const legalDetailSnapshotText = computed(() => {
  return [
    '## 咨询详情验收摘要',
    `- 最近加载：${legalDetailLastLoadedAt.value || '-'}`,
    `- 最近复制附件：${legalDetailLastActionAt.value || '-'}`,
    `- 咨询摘要：${legalDetailSummaryText.value}`,
    `- 附件数量：${attachmentUrls.value.length} 项`,
    `- 详情核对：${legalDetailConsistencyText.value}`,
    `- 回复链路：${legalDetailReplyTraceText.value}`,
    `- 链路关联：${legalDetailLinkageText.value}`,
    `- 说明：${legalDetailLastMessage.value || '-'}`,
    '- 链路关联：法律咨询 / 咨询详情 / 附件取证'
  ].join('\n')
})

async function loadData(consultId) {
  if (!consultId) {
    uni.showToast({ title: '缺少咨询编号', icon: 'none' })
    return
  }
  try {
    detail.value = await getLegalConsultDetail(consultId)
    legalDetailLastLoadedAt.value = new Date().toLocaleString()
    legalDetailLastMessage.value = '咨询详情已加载，可核对状态、回复和附件'
  } catch (error) {
    legalDetailLastLoadedAt.value = new Date().toLocaleString()
    legalDetailLastMessage.value = error.message || '加载咨询详情失败'
    uni.showToast({ title: error.message || '加载咨询详情失败', icon: 'none' })
  }
}

function copyAttachment(url) {
  if (!url) {
    return
  }
  legalDetailLastActionAt.value = new Date().toLocaleString()
  legalDetailLastMessage.value = `已复制附件地址：${url}`
  uni.setClipboardData({
    data: url,
    success: () => uni.showToast({ title: '已复制附件地址', icon: 'none' })
  })
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

onLoad((options) => {
  loadData(options?.consultId)
})
</script>
