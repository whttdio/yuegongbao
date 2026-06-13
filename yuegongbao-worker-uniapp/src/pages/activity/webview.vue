<template>
  <view class="worker-page">    <web-view v-if="resolvedUrl" :src="resolvedUrl" />

    <view v-else class="worker-card">
      <view class="worker-empty__title">活动地址不存在</view>
      <view class="worker-empty__desc">当前未读取到有效活动外链，可回到活动详情页重新核对配置。</view>
    </view>
  </view>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const state = reactive({
  title: '福利活动',
  url: ''
})
const webviewLastLoadedAt = ref('')
const webviewLastMessage = ref('')

function resolveExternalUrl(url) {
  if (!url) {
    return ''
  }
  if (/^https?:\/\//i.test(url)) {
    return url
  }
  if (typeof window !== 'undefined' && window.location?.origin) {
    return `${window.location.origin}${url.startsWith('/') ? url : `/${url}`}`
  }
  return url
}

const resolvedUrl = computed(() => resolveExternalUrl(state.url))
const webviewSnapshotText = computed(() => {
  return [
    '## 活动外链验收摘要',
    `- 页面标题：${state.title || '-'}`,
    `- 最近加载：${webviewLastLoadedAt.value || '-'}`,
    `- 原始地址：${state.url || '-'}`,
    `- 解析地址：${resolvedUrl.value || '-'}`,
    `- 说明：${webviewLastMessage.value || '-'}`,
    '- 链路关联：福利活动详情 / 外链 H5'
  ].join('\n')
})

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
  state.title = decodeURIComponent(options?.title || '福利活动')
  state.url = decodeURIComponent(options?.url || '')
  webviewLastLoadedAt.value = new Date().toLocaleString()
  webviewLastMessage.value = state.url ? '活动外链参数已加载，可核对原始地址与解析地址' : '活动地址不存在'
  if (state.title) {
    uni.setNavigationBarTitle({ title: state.title })
  }
  if (!state.url) {
    uni.showToast({ title: '活动地址不存在', icon: 'none' })
  }
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
  word-break: break-all;
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

.worker-empty__title {
  font-size: 28rpx;
  font-weight: 600;
  color: #16324f;
}

.worker-empty__desc {
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: #7890aa;
}
</style>
