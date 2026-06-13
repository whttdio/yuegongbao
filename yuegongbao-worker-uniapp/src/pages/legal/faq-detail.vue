<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="worker-title">{{ detail.title || '-' }}</view>
      <view class="worker-tag">{{ detail.category || '-' }}</view>
      <view v-for="(item, index) in paragraphs" :key="index" class="article-paragraph">
        {{ index + 1 }}. {{ item }}
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getLegalFaqDetail } from '../../api/worker'

const detail = ref({})
const faqDetailLastLoadedAt = ref('')
const faqDetailLastMessage = ref('')

const paragraphs = computed(() => detail.value?.paragraphs || [])
const faqDetailSummaryText = computed(() => {
  return `${detail.value.title || '-'} / ${detail.value.category || '-'} / ${paragraphs.value.length} 段`
})
const faqDetailSnapshotText = computed(() => {
  return [
    '## 常见问题验收摘要',
    `- 最近加载：${faqDetailLastLoadedAt.value || '-'}`,
    `- 问题摘要：${faqDetailSummaryText.value}`,
    `- 正文段落：${paragraphs.value.length} 段`,
    `- 说明：${faqDetailLastMessage.value || '-'}`,
    '- 链路关联：法律咨询 / FAQ / FAQ 详情'
  ].join('\n')
})

async function loadData(faqKey) {
  if (!faqKey) {
    uni.showToast({ title: '缺少问题标识', icon: 'none' })
    return
  }
  try {
    detail.value = await getLegalFaqDetail(faqKey)
    faqDetailLastLoadedAt.value = new Date().toLocaleString()
    faqDetailLastMessage.value = '常见问题详情已加载，可核对分类和正文段落'
  } catch (error) {
    faqDetailLastLoadedAt.value = new Date().toLocaleString()
    faqDetailLastMessage.value = error.message || '加载常见问题失败'
    uni.showToast({ title: error.message || '加载常见问题失败', icon: 'none' })
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
  loadData(options?.faqKey)
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
