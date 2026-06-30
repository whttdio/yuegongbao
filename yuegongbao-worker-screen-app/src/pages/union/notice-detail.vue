<template>
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">{{ detail.title || '工会通知' }}</view>
      <view class="worker-subtitle">查看工会服务、政策和活动安排</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">通知正文</view>
      </view>
      <view v-for="(item, index) in detail.paragraphs || []" :key="index" class="detail-block__content">
        {{ index + 1 }}. {{ item }}
      </view>
      <view v-if="!(detail.paragraphs || []).length" class="worker-empty">暂无通知内容</view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getUnionNoticeDetail } from '../../api/screen'

const detail = ref({})
const unionNoticeLastLoadedAt = ref('')
const unionNoticeLastMessage = ref('')
const unionNoticeSnapshotText = computed(() => {
  return [
    '## 工会通知验收摘要',
    `- 最近加载：${unionNoticeLastLoadedAt.value || '-'}`,
    `- 通知标题：${detail.value.title || '-'}`,
    `- 正文段落：${(detail.value.paragraphs || []).length} 段`,
    `- 说明：${unionNoticeLastMessage.value || '-'}`,
    '- 链路关联：工会服务 / 工会通知详情'
  ].join('\n')
})

async function loadData(noticeKey) {
  try {
    detail.value = await getUnionNoticeDetail(noticeKey)
    unionNoticeLastLoadedAt.value = new Date().toLocaleString()
    unionNoticeLastMessage.value = '工会通知详情已加载，可核对正文段落'
  } catch (error) {
    unionNoticeLastLoadedAt.value = new Date().toLocaleString()
    unionNoticeLastMessage.value = error.message || '加载工会通知失败'
    uni.showToast({ title: error.message || '加载工会通知失败', icon: 'none' })
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
  loadData(options?.noticeKey)
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
