<template>
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">{{ detail.title || '讲座详情' }}</view>
      <view class="worker-subtitle">{{ detail.category || '-' }}</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">讲座内容</view>
      </view>
      <view v-for="(item, index) in paragraphs" :key="index" class="detail-block__content">
        {{ index + 1 }}. {{ item }}
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getLegalArticleDetail } from '../../api/screen'

const detail = ref({})
const articleDetailLastLoadedAt = ref('')
const articleDetailLastMessage = ref('')

const paragraphs = computed(() => detail.value?.paragraphs || [])
const articleDetailSummaryText = computed(() => {
  return `${detail.value.title || '-'} / ${detail.value.category || '-'} / ${paragraphs.value.length} 段`
})
const articleDetailSnapshotText = computed(() => {
  return [
    '## 讲座详情验收摘要',
    `- 最近加载：${articleDetailLastLoadedAt.value || '-'}`,
    `- 内容摘要：${articleDetailSummaryText.value}`,
    `- 正文段落：${paragraphs.value.length} 段`,
    `- 说明：${articleDetailLastMessage.value || '-'}`,
    '- 链路关联：法律公益讲座 / 讲座详情'
  ].join('\n')
})

async function loadData(articleKey) {
  if (!articleKey) {
    uni.showToast({ title: '缺少文章标识', icon: 'none' })
    return
  }
  try {
    detail.value = await getLegalArticleDetail(articleKey)
    articleDetailLastLoadedAt.value = new Date().toLocaleString()
    articleDetailLastMessage.value = '讲座详情已加载，可核对分类和正文段落'
  } catch (error) {
    articleDetailLastLoadedAt.value = new Date().toLocaleString()
    articleDetailLastMessage.value = error.message || '加载讲座详情失败'
    uni.showToast({ title: error.message || '加载讲座详情失败', icon: 'none' })
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
  loadData(options?.articleKey)
})
</script>

<style lang="scss">
.article-paragraph {
  margin-top: 20rpx;
  font-size: 28rpx;
  line-height: 1.7;
  color: #183247;
}
</style>
