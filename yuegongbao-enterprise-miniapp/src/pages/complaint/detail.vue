<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="section-head">
        <view class="worker-title">{{ detail.title || '-' }}</view>
        <view class="worker-tag">{{ detail.statusText || '-' }}</view>
      </view>
      <view class="worker-subtitle">{{ detail.complaintType || '-' }}</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">投诉内容</view>
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
        <view class="detail-row__label">提交方式</view>
        <view class="detail-row__value">{{ detail.anonymousText || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">工会同步</view>
        <view class="detail-row__value">{{ detail.syncUnionText || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">处理反馈</view>
        <view class="detail-row__value">{{ detail.replyContent || '暂无反馈' }}</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">证据附件</view>
        <view class="worker-tag">{{ attachmentUrls.length }} 项</view>
      </view>
      <view v-if="attachmentUrls.length">
        <view v-for="(item, index) in attachmentUrls" :key="`${item}-${index}`" class="attachment-row">
          <view class="attachment-row__text">{{ item }}</view>
          <view class="attachment-row__action" @click="copyAttachment(item)">复制</view>
        </view>
      </view>
      <view v-else class="worker-empty">暂无证据附件</view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getComplaintDetail } from '../../api/enterprise-service'

const detail = ref({})
const complaintDetailLastLoadedAt = ref('')
const complaintDetailLastAttachmentAt = ref('')
const complaintDetailLastMessage = ref('')

const attachmentUrls = computed(() => {
  const attachments = detail.value?.attachments || ''
  if (!attachments) {
    return []
  }
  return String(attachments).split(',').map((item) => item.trim()).filter(Boolean)
})

const complaintSummaryText = computed(() => {
  return `${detail.value.complaintType || '-'} / ${detail.value.statusText || '-'} / ${detail.value.anonymousText || '-'}`
})
const complaintDetailConsistencyText = computed(() => {
  const missingFields = []
  if (!detail.value.title) {
    missingFields.push('标题')
  }
  if (!detail.value.statusText) {
    missingFields.push('状态')
  }
  if (!detail.value.complaintType) {
    missingFields.push('投诉类型')
  }
  if (missingFields.length) {
    return `详情缺少 ${missingFields.join('、')}`
  }
  return `状态 ${detail.value.statusText || '-'} / 工会 ${detail.value.syncUnionText || '-'}`
})
const complaintDetailReplyTraceText = computed(() => {
  if (detail.value.replyContent && attachmentUrls.value.length) {
    return `已反馈 / 附件 ${attachmentUrls.value.length} 项，可核对消息通知和证据取证`
  }
  if (detail.value.replyContent) {
    return '已有处理反馈，需结合消息中心和投诉列表状态复核'
  }
  if (attachmentUrls.value.length) {
    return `待反馈 / 已留证 ${attachmentUrls.value.length} 项`
  }
  return '暂无反馈且无附件，需结合真实投诉数据验收'
})
const complaintDetailLinkageText = computed(() => {
  return '投诉列表 / 投诉详情 / 消息通知 / 附件取证'
})

const complaintDetailSnapshotText = computed(() => {
  return [
    '## 投诉详情验收摘要',
    `- 最近加载：${complaintDetailLastLoadedAt.value || '-'}`,
    `- 最近复制附件：${complaintDetailLastAttachmentAt.value || '-'}`,
    `- 投诉摘要：${complaintSummaryText.value}`,
    `- 附件数量：${attachmentUrls.value.length} 项`,
    `- 详情核对：${complaintDetailConsistencyText.value}`,
    `- 反馈 / 取证：${complaintDetailReplyTraceText.value}`,
    `- 链路关联：${complaintDetailLinkageText.value}`,
    `- 说明：${complaintDetailLastMessage.value || '-'}`,
    '- 链路关联：投诉列表 / 投诉详情 / 附件取证'
  ].join('\n')
})

async function loadData(complaintId) {
  if (!complaintId) {
    uni.showToast({ title: '缺少投诉编号', icon: 'none' })
    return
  }
  try {
    detail.value = await getComplaintDetail(complaintId)
    complaintDetailLastLoadedAt.value = new Date().toLocaleString()
    complaintDetailLastMessage.value = '投诉详情已加载，可核对状态、反馈和证据附件'
  } catch (error) {
    complaintDetailLastLoadedAt.value = new Date().toLocaleString()
    complaintDetailLastMessage.value = error.message || '加载投诉详情失败'
    uni.showToast({ title: error.message || '加载投诉详情失败', icon: 'none' })
  }
}

function copyAttachment(url) {
  if (!url) {
    return
  }
  complaintDetailLastAttachmentAt.value = new Date().toLocaleString()
  complaintDetailLastMessage.value = `已复制附件地址：${url}`
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
  loadData(options?.complaintId)
})
</script>
