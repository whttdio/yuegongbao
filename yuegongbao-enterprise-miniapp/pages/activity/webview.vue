<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
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

