<template>
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">工伤预防视频</view>
      <view class="worker-subtitle">观看安全培训视频，积累工伤预防知识</view>
      <view class="hero-stat-grid">
        <view class="hero-stat">
          <view class="hero-stat__value">{{ total }}</view>
          <view class="hero-stat__label">视频总数</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ completedCount }}</view>
          <view class="hero-stat__label">已完成</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ inProgressCount }}</view>
          <view class="hero-stat__label">学习中</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">视频列表</view>
        <view class="worker-tag">{{ total }} 条</view>
      </view>
      <view v-if="rows.length">
        <view v-for="item in rows" :key="item.videoKey" class="list-row" @click="openDetail(item)">
          <view>
            <view class="list-row__title">{{ item.title }}</view>
            <view class="list-row__subtitle">
              时长 {{ item.durationText || '00:00' }} / 已看 {{ item.watchedSeconds || 0 }} 秒
            </view>
          </view>
          <view class="worker-tag">{{ item.completed ? '已完成' : '继续学习' }}</view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无可播放视频</view>
        <view class="worker-empty__desc">
          可先进入 AI 培训或本月答题任务，完成基础学习后再回到视频列表继续查看。
        </view>
        <view class="worker-empty__actions">
          <button class="worker-button worker-button--secondary" @click="goAiTraining">查看 AI 培训</button>
          <button class="worker-button" @click="goTraining">进入本月培训</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getVideoList } from '../../api/screen'

const EXPECTED_VIDEO_CHAIN_PAGES = ['首页工伤预防视频', '视频列表', '视频详情', 'AI 培训', '本月培训']
const rows = ref([])
const total = ref(0)
const videoLastLoadedAt = ref('')
const videoLastActionAt = ref('')
const videoLastMessage = ref('')
const videoChainCoverageText = computed(() => EXPECTED_VIDEO_CHAIN_PAGES.join(' / '))
const completedCount = computed(() => rows.value.filter((item) => item.completed).length)
const inProgressCount = computed(() => Math.max(rows.value.length - completedCount.value, 0))
const videoProgressSummaryText = computed(() => {
  return `已完成 ${completedCount.value} 条 / 学习中 ${inProgressCount.value} 条`
})
const latestVideoSummaryText = computed(() => {
  if (!rows.value.length) {
    return '暂无视频'
  }
  const latest = rows.value[0] || {}
  return `${latest.title || '-'} / ${latest.durationText || '00:00'} / ${latest.completed ? '已完成' : '继续学习'}`
})
const videoConsistencyText = computed(() => {
  if (!rows.value.length) {
    return '当前无视频，需用真实学习进度验收空态引导'
  }
  const watchedCount = rows.value.filter((item) => Number(item?.watchedSeconds || 0) > 0).length
  return `总数 ${total.value} 条 / 已产生学习进度 ${watchedCount} 条`
})
const videoSnapshotText = computed(() => {
  return [
    '## 视频验收摘要',
    `- 链路核对：${videoChainCoverageText.value}`,
    `- 最近联动：${videoLastActionAt.value || '-'}`,
    `- 最近加载：${videoLastLoadedAt.value || '-'}`,
    `- 视频总数：${total.value} 条`,
    `- 完成进度：${videoProgressSummaryText.value}`,
    `- 最近一条：${latestVideoSummaryText.value}`,
    '- 空态引导：AI 培训 / 本月培训',
    `- 列表核对：${videoConsistencyText.value}`,
    `- 说明：${videoLastMessage.value || '-'}`,
    '- 链路关联：首页工伤预防视频 / 视频列表 / 视频详情 / AI 培训'
  ].join('\n')
})

async function loadData() {
  try {
    const data = await getVideoList()
    rows.value = data?.rows || []
    total.value = data?.total || rows.value.length
    videoLastLoadedAt.value = new Date().toLocaleString()
    videoLastMessage.value = rows.value.length
      ? `视频列表已加载，共 ${total.value} 条`
      : '当前暂无视频，可先进入 AI 培训或本月培训'
  } catch (error) {
    videoLastLoadedAt.value = new Date().toLocaleString()
    videoLastMessage.value = error.message || '加载视频失败'
    uni.showToast({ title: error.message || '加载视频失败', icon: 'none' })
  }
}

function openDetail(item) {
  if (!item?.videoKey) {
    return
  }
  videoLastActionAt.value = new Date().toLocaleString()
  videoLastMessage.value = `已打开视频详情：${item.title || '-'}`
  uni.navigateTo({ url: `/pages/video/detail?videoKey=${item.videoKey}` })
}

function goAiTraining() {
  videoLastActionAt.value = new Date().toLocaleString()
  videoLastMessage.value = '已前往 AI 培训，待补充空态引导验收'
  uni.navigateTo({ url: '/pages/ai-training/detail' })
}

function goTraining() {
  videoLastActionAt.value = new Date().toLocaleString()
  videoLastMessage.value = '已前往本月培训，待补充空态引导验收'
  uni.navigateTo({ url: '/pages/training/index' })
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
.worker-empty__actions button {
  flex: 1;
}
</style>
