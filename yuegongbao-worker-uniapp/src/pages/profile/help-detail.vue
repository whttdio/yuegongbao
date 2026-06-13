<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="worker-title">{{ detail.title || '-' }}</view>
      <view class="worker-tag">{{ detail.category || '-' }}</view>
      <view v-for="(item, index) in paragraphs" :key="index" class="article-paragraph">
        {{ index + 1 }}. {{ item }}
      </view>
      <button v-if="actionTarget?.path" class="worker-button detail-action" @click="openAction">
        {{ detail.actionLabel || actionTarget.actionLabel || '去处理' }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getHelpDetail } from '../../api/worker'
import { normalizeWorkerJumpTarget, openWorkerJumpTarget } from '../../utils/worker-jump'

const detail = ref({})
const helpDetailLastLoadedAt = ref('')
const helpDetailLastActionAt = ref('')
const helpDetailLastMessage = ref('')

const actionTarget = computed(() => normalizeWorkerJumpTarget(detail.value))
const paragraphs = computed(() => detail.value?.paragraphs || [])
const helpDetailSummaryText = computed(() => {
  return `${detail.value.title || '-'} / ${detail.value.category || '-'} / ${paragraphs.value.length} 段`
})
const helpDetailConsistencyText = computed(() => {
  if (actionTarget.value?.path) {
    return `正文 ${paragraphs.value.length} 段 / 可承接到 ${actionTarget.value.path}`
  }
  return `正文 ${paragraphs.value.length} 段 / 当前无承接动作`
})
const helpDetailSnapshotText = computed(() => {
  return [
    '## 帮助详情验收摘要',
    `- 最近加载：${helpDetailLastLoadedAt.value || '-'}`,
    `- 最近动作：${helpDetailLastActionAt.value || '-'}`,
    `- 内容摘要：${helpDetailSummaryText.value}`,
    `- 承接动作：${actionTarget.value?.path ? (detail.value.actionLabel || actionTarget.value.actionLabel || '去处理') : '无'}`,
    `- 详情核对：${helpDetailConsistencyText.value}`,
    `- 说明：${helpDetailLastMessage.value || '-'}`,
    '- 链路关联：帮助中心 / 帮助详情 / 承接入口'
  ].join('\n')
})

async function loadData(articleKey) {
  if (!articleKey) {
    uni.showToast({ title: '缺少帮助标识', icon: 'none' })
    return
  }
  try {
    detail.value = await getHelpDetail(articleKey)
    helpDetailLastLoadedAt.value = new Date().toLocaleString()
    helpDetailLastMessage.value = '帮助详情已加载，可核对正文段落和承接动作'
  } catch (error) {
    helpDetailLastLoadedAt.value = new Date().toLocaleString()
    helpDetailLastMessage.value = error.message || '加载帮助详情失败'
    uni.showToast({ title: error.message || '加载帮助详情失败', icon: 'none' })
  }
}

function openAction() {
  if (!actionTarget.value?.path) {
    return
  }
  helpDetailLastActionAt.value = new Date().toLocaleString()
  helpDetailLastMessage.value = `已打开承接动作：${detail.value.actionLabel || actionTarget.value.actionLabel || actionTarget.value.path}`
  openWorkerJumpTarget(actionTarget.value)
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
  loadData(options?.articleKey)
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

.article-paragraph {
  margin-top: 20rpx;
  font-size: 28rpx;
  line-height: 1.7;
  color: #36506b;
}

.detail-action {
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
  line-height: 1.7;
  color: #16324f;
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
