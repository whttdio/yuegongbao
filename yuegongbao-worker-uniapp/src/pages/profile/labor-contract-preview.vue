<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="worker-title">{{ detail.previewTitle || '合同摘要预览' }}</view>
      <view class="worker-subtitle">
        {{ detail.title || '-' }} / {{ detail.contractStatusText || '-' }}
      </view>
    </view>

    <view v-for="(section, index) in sections" :key="`${section.title}-${index}`" class="worker-card preview-card">
      <view class="worker-title worker-title--small">{{ section.title }}</view>
      <view v-for="(line, lineIndex) in section.lines || []" :key="`${section.title}-${lineIndex}`" class="preview-line">
        {{ line }}
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getLaborContractDetail } from '../../api/worker'

const detail = ref({})
const contractPreviewLastLoadedAt = ref('')
const contractPreviewLastMessage = ref('')
const sections = computed(() => detail.value.previewSections || [])
const contractPreviewSnapshotText = computed(() => {
  return [
    '## 合同摘要验收摘要',
    `- 最近加载：${contractPreviewLastLoadedAt.value || '-'}`,
    `- 摘要标题：${detail.value.previewTitle || '合同摘要预览'}`,
    `- 合同状态：${detail.value.contractStatusText || '-'}`,
    `- 章节数量：${sections.value.length} 章`,
    `- 说明：${contractPreviewLastMessage.value || '-'}`,
    '- 链路关联：合同详情 / 合同摘要预览'
  ].join('\n')
})

async function loadData(contractId) {
  try {
    detail.value = await getLaborContractDetail(contractId)
    contractPreviewLastLoadedAt.value = new Date().toLocaleString()
    contractPreviewLastMessage.value = '合同摘要已加载，可核对章节数量和预览内容'
  } catch (error) {
    contractPreviewLastLoadedAt.value = new Date().toLocaleString()
    contractPreviewLastMessage.value = error.message || '加载合同摘要失败'
    uni.showToast({ title: error.message || '加载合同摘要失败', icon: 'none' })
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
  loadData(options?.contractId)
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

.preview-card {
  margin-top: 20rpx;
}

.worker-title--small {
  font-size: 30rpx;
}

.preview-line {
  margin-top: 16rpx;
  padding: 18rpx 20rpx;
  border-radius: 18rpx;
  background: #f5f8fc;
  font-size: 26rpx;
  line-height: 1.7;
  color: #16324f;
  word-break: break-all;
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
