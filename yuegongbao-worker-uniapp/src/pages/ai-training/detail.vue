<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="worker-title">{{ detail.title || '工伤AI培训' }}</view>
      <view class="worker-subtitle">{{ detail.subtitle || '-' }}</view>
      <view class="worker-subtitle">适用人员：{{ detail.workerName || '-' }} / {{ detail.jobType || '-' }}</view>
    </view>

    <view class="worker-card">
      <view class="worker-title">智能提问</view>
      <view v-for="(item, index) in detail.questionList || []" :key="index" class="content-row">
        {{ index + 1 }}. {{ item }}
      </view>
    </view>

    <view class="worker-card">
      <view class="worker-title">典型案例</view>
      <view v-for="(item, index) in detail.caseList || []" :key="index" class="content-row">
        {{ index + 1 }}. {{ item }}
      </view>
    </view>

    <view class="worker-card">
      <view class="worker-title">行动建议</view>
      <view v-for="(item, index) in detail.actionTips || []" :key="index" class="content-row">
        {{ index + 1 }}. {{ item }}
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getAiTrainingDetail } from '../../api/worker'

const detail = ref({})
const aiTrainingLastLoadedAt = ref('')
const aiTrainingLastMessage = ref('')

const aiTrainingSummaryText = computed(() => {
  return `${detail.value.title || '工伤AI培训'} / ${detail.value.workerName || '-'} / ${detail.value.jobType || '-'}`
})
const aiTrainingDataSummaryText = computed(() => {
  return `问题 ${(detail.value.questionList || []).length} 条 / 案例 ${(detail.value.caseList || []).length} 条 / 建议 ${(detail.value.actionTips || []).length} 条`
})
const aiTrainingSnapshotText = computed(() => {
  return [
    '## AI培训验收摘要',
    `- 最近加载：${aiTrainingLastLoadedAt.value || '-'}`,
    `- 培训摘要：${aiTrainingSummaryText.value}`,
    `- 数据概览：${aiTrainingDataSummaryText.value}`,
    `- 说明：${aiTrainingLastMessage.value || '-'}`,
    '- 链路关联：视频列表 / AI培训详情 / 本月培训'
  ].join('\n')
})

async function loadData() {
  try {
    detail.value = await getAiTrainingDetail()
    aiTrainingLastLoadedAt.value = new Date().toLocaleString()
    aiTrainingLastMessage.value = 'AI培训详情已加载，可核对问题、案例和建议'
  } catch (error) {
    aiTrainingLastLoadedAt.value = new Date().toLocaleString()
    aiTrainingLastMessage.value = error.message || '加载AI培训失败'
    uni.showToast({ title: error.message || '加载AI培训失败', icon: 'none' })
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
