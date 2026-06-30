<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">{{ detail.title || '工伤AI培训' }}</view>
      <view class="worker-subtitle">{{ detail.subtitle || '-' }}</view>
      <view class="worker-subtitle">适用人员：{{ detail.workerName || '-' }} / {{ detail.jobType || '-' }}</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">智能提问</view>
      </view>
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
import { getAiTrainingDetail } from '../../api/enterprise-service'

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
.content-row {
  margin-top: 20rpx;
  font-size: 26rpx;
  line-height: 1.7;
  color: #183247;
}
</style>
