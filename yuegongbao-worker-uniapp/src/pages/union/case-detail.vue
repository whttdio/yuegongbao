<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="worker-title">{{ detail.title || '-' }}</view>
      <view v-for="(item, index) in detail.paragraphs || []" :key="index" class="content-row">
        {{ index + 1 }}. {{ item }}
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getUnionCaseDetail } from '../../api/worker'

const detail = ref({})
const unionCaseLastLoadedAt = ref('')
const unionCaseLastMessage = ref('')
const unionCaseSnapshotText = computed(() => {
  return [
    '## 工会案例验收摘要',
    `- 最近加载：${unionCaseLastLoadedAt.value || '-'}`,
    `- 案例标题：${detail.value.title || '-'}`,
    `- 正文段落：${(detail.value.paragraphs || []).length} 段`,
    `- 说明：${unionCaseLastMessage.value || '-'}`,
    '- 链路关联：工会服务 / 案例详情'
  ].join('\n')
})

async function loadData(caseKey) {
  try {
    detail.value = await getUnionCaseDetail(caseKey)
    unionCaseLastLoadedAt.value = new Date().toLocaleString()
    unionCaseLastMessage.value = '工会案例详情已加载，可核对正文段落'
  } catch (error) {
    unionCaseLastLoadedAt.value = new Date().toLocaleString()
    unionCaseLastMessage.value = error.message || '加载工会案例失败'
    uni.showToast({ title: error.message || '加载工会案例失败', icon: 'none' })
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
  loadData(options?.caseKey)
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

.content-row {
  margin-top: 20rpx;
  font-size: 26rpx;
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
