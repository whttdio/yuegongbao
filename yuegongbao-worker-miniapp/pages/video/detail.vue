<template>
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">{{ detail.title || '视频详情' }}</view>
      <view class="worker-subtitle">{{ detail.desc || '-' }}</view>
      <view class="hero-stat-grid">
        <view class="hero-stat">
          <view class="hero-stat__value">{{ detail.durationText || '00:00' }}</view>
          <view class="hero-stat__label">时长</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ watchedSeconds }}</view>
          <view class="hero-stat__label">已看秒数</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ detail.completed ? '已完成' : '学习中' }}</view>
          <view class="hero-stat__label">学习状态</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">视频播放</view>
      </view>
      <view class="detail-meta">
        <view class="worker-tag">时长 {{ detail.durationText || '00:00' }}</view>
        <view class="worker-tag">已看 {{ watchedSeconds }} 秒</view>
      </view>
      <view class="worker-subtitle source-text">{{ detail.sourceText || '' }}</view>
      <view v-if="detail.videoUrl" class="video-player">
        <video
          :src="detail.videoUrl"
          :poster="detail.posterUrl || ''"
          controls
          show-center-play-btn
          enable-progress-gesture
          object-fit="contain"
          @loadedmetadata="handleLoadedMetadata"
          @timeupdate="handleTimeUpdate"
          @pause="handlePause"
          @ended="handleEnded"
          @error="handleVideoError"
        />
      </view>
      <view v-else class="video-player">
        <view class="video-player__cover video-player__empty">
          当前视频暂不可播放，已切换为文字摘要学习模式。
        </view>
      </view>
      <view v-if="keyPoints.length" class="summary-panel">
        <view class="summary-panel__title">关键学习点</view>
        <view v-for="(item, index) in keyPoints" :key="`${item}-${index}`" class="summary-panel__content">
          {{ index + 1 }}. {{ item }}
        </view>
      </view>
      <view v-if="fallbackTips.length" class="summary-panel">
        <view class="summary-panel__title">学习提示</view>
        <view v-for="(item, index) in fallbackTips" :key="`${item}-${index}`" class="summary-panel__content">
          {{ index + 1 }}. {{ item }}
        </view>
      </view>
      <view class="progress-actions">
        <button class="worker-button worker-button--secondary" @click="saveCurrentProgress">保存当前进度</button>
        <button class="worker-button" @click="markCompleted">
          {{ detail.completed ? '已学完' : '标记学完' }}
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad, onUnload } from '@dcloudio/uni-app'
import { getVideoDetail, saveVideoProgress } from '../../api/worker'

const detail = ref({})
const videoDuration = ref(0)
const saving = ref(false)
const lastSavedSeconds = ref(0)
const videoDetailLastLoadedAt = ref('')
const videoDetailLastSavedAt = ref('')
const videoDetailLastMessage = ref('')

const watchedSeconds = computed(() => detail.value.watchedSeconds || 0)
const keyPoints = computed(() => detail.value.keyPoints || [])
const fallbackTips = computed(() => detail.value.fallbackTips || [])
const watchSummaryText = computed(() => {
  return `${watchedSeconds.value} 秒 / ${detail.value.durationText || '00:00'} / ${detail.value.completed ? '已完成' : '学习中'}`
})
const videoDetailConsistencyText = computed(() => {
  if (detail.value.videoUrl) {
    return `${detail.value.completed ? '视频已完成' : '视频可播放'} / 要点 ${keyPoints.value.length} 条`
  }
  return `视频不可播 / 已切换文字摘要 ${keyPoints.value.length + fallbackTips.value.length} 条`
})
const videoProgressTraceText = computed(() => {
  return `最近保存 ${videoDetailLastSavedAt.value || '-'} / 已看 ${watchedSeconds.value} 秒 / 已保存 ${lastSavedSeconds.value} 秒`
})
const videoDetailSnapshotText = computed(() => {
  return [
    '## 视频详情验收摘要',
    `- 最近加载：${videoDetailLastLoadedAt.value || '-'}`,
    `- 最近保存：${videoDetailLastSavedAt.value || '-'}`,
    `- 观看摘要：${watchSummaryText.value}`,
    `- 视频来源：${detail.value.sourceText || '-'}`,
    `- 摘要条目：要点 ${keyPoints.value.length} 条 / 提示 ${fallbackTips.value.length} 条`,
    `- 详情核对：${videoDetailConsistencyText.value}`,
    `- 进度链路：${videoProgressTraceText.value}`,
    `- 说明：${videoDetailLastMessage.value || '-'}`,
    '- 链路关联：首页工伤预防视频 / 视频列表 / 视频详情 / AI 培训'
  ].join('\n')
})

async function loadData(videoKey) {
  if (!videoKey) {
    uni.showToast({ title: '缺少视频标识', icon: 'none' })
    return
  }
  try {
    detail.value = await getVideoDetail(videoKey)
    videoDuration.value = Number(detail.value.totalSeconds || 0)
    lastSavedSeconds.value = Number(detail.value.watchedSeconds || 0)
    videoDetailLastLoadedAt.value = new Date().toLocaleString()
    videoDetailLastMessage.value = '视频详情已加载，可核对播放、进度保存和摘要信息'
  } catch (error) {
    videoDetailLastLoadedAt.value = new Date().toLocaleString()
    videoDetailLastMessage.value = error.message || '加载视频详情失败'
    uni.showToast({ title: error.message || '加载视频详情失败', icon: 'none' })
  }
}

function resolveTotalSeconds() {
  return Math.max(Number(videoDuration.value || 0), Number(detail.value.totalSeconds || 0), 0)
}

async function persistProgress(targetSeconds, silent = true) {
  const videoKey = detail.value.videoKey
  const totalSeconds = resolveTotalSeconds()
  const nextSeconds = Math.max(watchedSeconds.value, Math.floor(targetSeconds || 0))
  if (!videoKey || saving.value || nextSeconds <= lastSavedSeconds.value) {
    return
  }
  saving.value = true
  try {
    const result = await saveVideoProgress(videoKey, nextSeconds, totalSeconds)
    detail.value = {
      ...detail.value,
      watchedSeconds: result?.watchedSeconds || nextSeconds,
      totalSeconds: result?.totalSeconds || totalSeconds,
      completed: !!result?.completed
    }
    lastSavedSeconds.value = Number(detail.value.watchedSeconds || nextSeconds)
    videoDuration.value = Number(detail.value.totalSeconds || totalSeconds)
    videoDetailLastSavedAt.value = new Date().toLocaleString()
    videoDetailLastMessage.value = detail.value.completed
      ? '视频进度已保存，当前已完成学习'
      : `视频进度已保存至 ${detail.value.watchedSeconds || nextSeconds} 秒`
    if (!silent) {
      uni.showToast({ title: detail.value.completed ? '已记录完成' : '进度已保存', icon: 'none' })
    }
  } catch (error) {
    videoDetailLastSavedAt.value = new Date().toLocaleString()
    videoDetailLastMessage.value = error.message || '保存进度失败'
    if (!silent) {
      uni.showToast({ title: error.message || '保存进度失败', icon: 'none' })
    }
  } finally {
    saving.value = false
  }
}

function handleLoadedMetadata(event) {
  const duration = Number(event?.detail?.duration || 0)
  if (duration > 0) {
    videoDuration.value = Math.floor(duration)
  }
}

function handleTimeUpdate(event) {
  const currentTime = Math.floor(Number(event?.detail?.currentTime || 0))
  if (currentTime > watchedSeconds.value) {
    detail.value = {
      ...detail.value,
      watchedSeconds: currentTime
    }
  }
  if (currentTime - lastSavedSeconds.value >= 5) {
    persistProgress(currentTime)
  }
}

function handlePause(event) {
  const currentTime = Math.floor(Number(event?.detail?.currentTime || watchedSeconds.value || 0))
  persistProgress(currentTime)
}

function handleEnded(event) {
  const currentTime = Math.floor(Number(event?.detail?.currentTime || resolveTotalSeconds()))
  detail.value = {
    ...detail.value,
    watchedSeconds: Math.max(watchedSeconds.value, currentTime),
    completed: true
  }
  persistProgress(Math.max(currentTime, resolveTotalSeconds()), false)
}

function handleVideoError() {
  detail.value = {
    ...detail.value,
    videoUrl: ''
  }
  videoDetailLastMessage.value = '视频播放失败，已切换为文字摘要学习模式'
  uni.showToast({ title: '视频暂不可播放，已切换文字摘要', icon: 'none' })
}

function saveCurrentProgress() {
  persistProgress(watchedSeconds.value, false)
}

function markCompleted() {
  const totalSeconds = resolveTotalSeconds()
  if (!totalSeconds) {
    uni.showToast({ title: '视频时长未加载完成', icon: 'none' })
    return
  }
  persistProgress(totalSeconds, false)
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
  loadData(options?.videoKey)
})

onUnload(() => {
  persistProgress(watchedSeconds.value)
})
</script>

<style lang="scss">
.detail-meta {
  display: flex;
  gap: 16rpx;
  margin-top: 18rpx;
}

.video-player video {
  width: 100%;
  height: 100%;
}

.video-player__empty {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40rpx 28rpx;
  color: #fff;
  font-size: 24rpx;
  line-height: 1.6;
  text-align: center;
}

.source-text {
  margin-top: 14rpx;
}

.progress-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 28rpx;
}

.progress-actions button {
  flex: 1;
}
</style>
