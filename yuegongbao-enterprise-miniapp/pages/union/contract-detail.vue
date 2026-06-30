<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">{{ detail.title || '集体合同' }}</view>
      <view class="worker-subtitle">{{ detail.enterpriseName || '-' }} / {{ detail.period || '-' }}</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">合同条款</view>
      </view>
      <view v-for="(item, index) in detail.clauses || []" :key="index" class="detail-block__content">
        {{ index + 1 }}. {{ item }}
      </view>
      <view v-if="!(detail.clauses || []).length" class="worker-empty">暂无条款内容</view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getUnionContractDetail } from '../../api/enterprise-service'

const detail = ref({})
const unionContractLastLoadedAt = ref('')
const unionContractLastMessage = ref('')
const unionContractSummaryText = computed(() => {
  return `${detail.value.title || '-'} / ${detail.value.enterpriseName || '-'} / ${detail.value.period || '-'}`
})
const unionContractSnapshotText = computed(() => {
  return [
    '## 集体合同验收摘要',
    `- 最近加载：${unionContractLastLoadedAt.value || '-'}`,
    `- 合同摘要：${unionContractSummaryText.value}`,
    `- 条款数量：${(detail.value.clauses || []).length} 条`,
    `- 说明：${unionContractLastMessage.value || '-'}`,
    '- 链路关联：工会服务 / 集体合同详情'
  ].join('\n')
})

async function loadData(contractKey) {
  try {
    detail.value = await getUnionContractDetail(contractKey)
    unionContractLastLoadedAt.value = new Date().toLocaleString()
    unionContractLastMessage.value = '集体合同详情已加载，可核对企业、期限和条款'
  } catch (error) {
    unionContractLastLoadedAt.value = new Date().toLocaleString()
    unionContractLastMessage.value = error.message || '加载合同详情失败'
    uni.showToast({ title: error.message || '加载合同详情失败', icon: 'none' })
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
  loadData(options?.contractKey)
})
</script>

<style lang="scss">
.detail-meta {
  margin-top: 10rpx;
  font-size: 22rpx;
  color: #607789;
}

.content-row {
  margin-top: 20rpx;
  font-size: 26rpx;
  line-height: 1.7;
  color: #183247;
}
</style>
