<template>
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">{{ detail.title || '工会案例' }}</view>
      <view class="worker-subtitle">参考真实处理路径，了解维权材料和流程</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">案例正文</view>
      </view>
      <view v-for="(item, index) in detail.paragraphs || []" :key="index" class="detail-block__content">
        {{ index + 1 }}. {{ item }}
      </view>
      <view v-if="!(detail.paragraphs || []).length" class="worker-empty">暂无案例内容</view>
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
.content-row {
  margin-top: 20rpx;
  font-size: 26rpx;
  line-height: 1.7;
  color: #183247;
}
</style>
