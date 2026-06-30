<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">{{ detail.title || '常见问题' }}</view>
      <view class="worker-subtitle">{{ detail.category || '-' }}</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">问题解答</view>
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
import { getLegalFaqDetail } from '../../api/enterprise-service'

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
.article-paragraph {
  margin-top: 20rpx;
  font-size: 28rpx;
  line-height: 1.7;
  color: #183247;
}
</style>
