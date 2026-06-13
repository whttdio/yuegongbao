<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="worker-title">{{ detail.title || '-' }}</view>
      <view class="worker-subtitle">{{ detail.desc || '-' }}</view>
      <view class="detail-meta">
        <view class="worker-tag">时长 {{ detail.durationText || '00:00' }}</view>
        <view class="worker-tag">已看 {{ watchedSeconds }} 秒</view>
      </view>
      <view class="worker-subtitle source-text">{{ detail.sourceText || '' }}</view>
      <video
        v-if="detail.videoUrl"
        class="video-player"
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
      <view v-else class="video-empty">
        当前视频暂不可播放，已切换为文字摘要学习模式。
      </view>
      <view v-if="keyPoints.length" class="summary-panel">
        <view class="summary-panel__title">关键学习点</view>
        <view v-for="(item, index) in keyPoints" :key="`${item}-${index}`" class="summary-panel__line">
          {{ index + 1 }}. {{ item }}
        </view>
      </view>
      <view v-if="fallbackTips.length" class="summary-panel summary-panel--soft">
        <view class="summary-panel__title">学习提示</view>
        <view v-for="(item, index) in fallbackTips" :key="`${item}-${index}`" class="summary-panel__line">
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

.detail-meta {
  display: flex;
  gap: 16rpx;
  margin-top: 18rpx;
}

.video-player,
.video-empty {
  margin-top: 24rpx;
  border-radius: 24rpx;
  background: #0d3155;
}

.video-player {
  width: 100%;
  height: 420rpx;
}

.video-empty {
  padding: 40rpx 28rpx;
  background: linear-gradient(160deg, #0d3155 0%, #1e6fcd 100%);
  color: #fff;
  font-size: 24rpx;
  line-height: 1.6;
}

.source-text {
  margin-top: 14rpx;
}

.summary-panel {
  margin-top: 22rpx;
  padding: 22rpx 24rpx;
  border-radius: 22rpx;
  background: #f5f8fc;
}

.summary-panel--soft {
  background: #eef5ff;
}

.summary-panel__title {
  font-size: 28rpx;
  font-weight: 700;
  color: #16324f;
}

.summary-panel__line {
  margin-top: 12rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: #58738f;
}

.progress-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 28rpx;
}

.progress-actions button {
  flex: 1;
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
