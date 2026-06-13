<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="worker-title">{{ detail.title || '-' }}</view>
      <view class="worker-subtitle">发布时间：{{ detail.publishTime || '-' }}</view>
      <view v-if="detail.noticeId" class="worker-subtitle">消息类型：{{ noticeTypeText }}</view>
      <view v-if="detail.sourceLabel" class="worker-subtitle notice-source">来源：{{ detail.sourceLabel }}</view>
      <view class="detail-block">{{ detail.content || '-' }}</view>
      <button v-if="actionButtonText" class="worker-button notice-action" @click="openNoticeAction">
        {{ actionButtonText }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getNoticeDetail, markNoticeRead } from '../../api/worker'
import { normalizeWorkerJumpTarget, openWorkerJumpTarget } from '../../utils/worker-jump'

const detail = ref({})
const noticeDetailLastLoadedAt = ref('')
const noticeDetailLastActionAt = ref('')
const noticeDetailLastMessage = ref('')

const actionTarget = computed(() => {
  const rawPayload = detail.value?.payload && typeof detail.value.payload === 'object' ? detail.value.payload : {}
  const path =
    detail.value?.jumpPath ||
    detail.value?.path ||
    detail.value?.url ||
    detail.value?.routePath ||
    detail.value?.pagePath ||
    rawPayload.jumpPath ||
    rawPayload.path ||
    rawPayload.url ||
    rawPayload.routePath ||
    rawPayload.pagePath
  if (!path) {
    return null
  }
  return normalizeWorkerJumpTarget({
    ...rawPayload,
    jumpPath: path,
    jumpQuery: detail.value?.jumpQuery ?? detail.value?.query ?? detail.value?.params ?? rawPayload.jumpQuery ?? rawPayload.query ?? rawPayload.params,
    actionLabel: detail.value?.actionLabel || rawPayload.actionLabel || rawPayload.actionText || rawPayload.jumpLabel || rawPayload.label || '',
    sourceLabel: detail.value?.sourceLabel || rawPayload.sourceLabel || rawPayload.sourceText || ''
  })
})

const actionButtonText = computed(() => {
  if (!actionTarget.value?.path) {
    return ''
  }
  return detail.value.actionLabel || actionTarget.value.actionLabel || '去处理'
})

const noticeTypeText = computed(() => {
  if (Number(detail.value?.noticeId || 0) < 0) {
    return '个人消息'
  }
  return '平台公告'
})

const noticeSummaryText = computed(() => {
  return `${detail.value.title || '-'} / ${noticeTypeText.value} / ${detail.value.sourceLabel || '-'}`
})
const noticeReadbackText = computed(() => {
  return detail.value.readFlag || detail.value.isRead ? '已尝试回写已读状态' : '待校验已读回写'
})
const noticeJumpConsistencyText = computed(() => {
  if (actionTarget.value?.path) {
    return `可跳转 ${actionTarget.value.path} / 按钮 ${actionButtonText.value || '去处理'}`
  }
  return '无承接动作，需核对是否为纯公告通知'
})
const noticeDetailLinkageText = computed(() => {
  return '通知列表 / 通知详情 / 已读回写 / 目标承接页'
})

const noticeDetailSnapshotText = computed(() => {
  return [
    '## 通知详情验收摘要',
    `- 最近加载：${noticeDetailLastLoadedAt.value || '-'}`,
    `- 最近动作：${noticeDetailLastActionAt.value || '-'}`,
    `- 通知摘要：${noticeSummaryText.value}`,
    `- 已读回写：${noticeReadbackText.value}`,
    `- 跳转动作：${actionButtonText.value || '无'}`,
    `- 详情核对：${noticeJumpConsistencyText.value}`,
    `- 链路关联：${noticeDetailLinkageText.value}`,
    `- 说明：${noticeDetailLastMessage.value || '-'}`,
    '- 链路关联：通知列表 / 通知详情 / 跳转承接页'
  ].join('\n')
})

async function loadData(noticeId) {
  if (!noticeId) {
    uni.showToast({ title: '通知参数缺失', icon: 'none' })
    return
  }
  try {
    const [noticeDetail] = await Promise.all([
      getNoticeDetail(noticeId),
      markNoticeRead(noticeId).catch(() => null)
    ])
    detail.value = {
      ...(noticeDetail || {}),
      readFlag: true,
      isRead: true
    }
    noticeDetailLastLoadedAt.value = new Date().toLocaleString()
    noticeDetailLastMessage.value = '通知详情已加载，并已尝试回写已读状态'
  } catch (error) {
    noticeDetailLastLoadedAt.value = new Date().toLocaleString()
    noticeDetailLastMessage.value = error.message || '加载通知详情失败'
    uni.showToast({ title: error.message || '加载通知详情失败', icon: 'none' })
  }
}

function openNoticeAction() {
  if (!actionTarget.value?.path) {
    return
  }
  const opened = openWorkerJumpTarget(actionTarget.value)
  noticeDetailLastActionAt.value = new Date().toLocaleString()
  noticeDetailLastMessage.value = opened
    ? `已执行通知跳转：${actionButtonText.value || actionTarget.value.path}`
    : '通知跳转地址无效'
  if (!opened) {
    uni.showToast({ title: '通知跳转地址无效', icon: 'none' })
  }
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
  loadData(options?.noticeId)
})
</script>

<style lang="scss">
.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 18rpx;
}

.section-head--sub {
  margin-top: 20rpx;
}

.worker-title--small {
  font-size: 28rpx;
}

.detail-block {
  margin-top: 24rpx;
  padding: 24rpx;
  border-radius: 18rpx;
  background: #f5f8fc;
  font-size: 28rpx;
  color: #16324f;
  line-height: 1.7;
  white-space: pre-wrap;
}

.notice-source {
  margin-top: 8rpx;
}

.notice-action {
  margin-top: 24rpx;
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
  margin-top: 20rpx;
  padding: 20rpx 24rpx;
  border-radius: 18rpx;
  background: #f5f8fc;
}

.result-block__label {
  font-size: 24rpx;
  color: #5f7893;
}

.result-block__value {
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 1.8;
  color: #36506b;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
