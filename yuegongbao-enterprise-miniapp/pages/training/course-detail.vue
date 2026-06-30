<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page">
    <view v-if="loading" class="worker-card worker-empty worker-empty--panel">
      <view class="worker-empty__title">课程加载中...</view>
    </view>

    <template v-else-if="loadError">
      <view class="worker-card worker-empty worker-empty--panel">
        <view class="worker-empty__title">课程加载失败</view>
        <view class="worker-empty__desc">{{ loadError }}</view>
        <view class="worker-empty__actions">
          <button class="worker-button" @click="retryLoad">重新加载</button>
        </view>
      </view>
    </template>

    <template v-else>
      <view class="worker-card worker-hero">
        <view class="worker-title worker-title--display">{{ detail.title || '培训课程' }}</view>
        <view class="worker-subtitle">{{ detail.summary || '暂无课程摘要' }}</view>
        <view class="hero-stat-grid">
          <view class="hero-stat">
            <view class="hero-stat__value">{{ detail.durationText || '00:00' }}</view>
            <view class="hero-stat__label">课程时长</view>
          </view>
          <view class="hero-stat">
            <view class="hero-stat__value">{{ studiedSeconds }}</view>
            <view class="hero-stat__label">已学秒数</view>
          </view>
          <view class="hero-stat">
            <view class="hero-stat__value">{{ detail.completed ? '已完成' : '学习中' }}</view>
            <view class="hero-stat__label">学习状态</view>
          </view>
        </view>
      </view>

      <view class="worker-card">
        <view class="section-head">
          <view class="worker-title">课程内容</view>
        </view>
        <view class="course-progress">
          <view class="course-progress__meta">
            <text>学习进度 {{ progressPercentText }}</text>
            <text>{{ studiedSeconds }} / {{ detail.totalSeconds || 0 }} 秒</text>
          </view>
          <view class="course-progress__bar">
            <view class="course-progress__fill" :style="{ width: progressPercentText }" />
          </view>
        </view>
        <view v-if="detail.coverUrl && !playableVideoUrl" class="course-cover">
          <image :src="detail.coverUrl" mode="aspectFill" class="course-cover__image" />
        </view>
        <view v-if="playableVideoUrl" class="video-player">
          <video
            :id="`course-video-${detail.courseKey || 'default'}`"
            :key="playableVideoUrl"
            :src="playableVideoUrl"
            :poster="detail.posterUrl || detail.coverUrl || ''"
            controls
            show-center-play-btn
            show-play-btn
            enable-play-gesture
            enable-progress-gesture
            playsinline
            webkit-playsinline
            x5-video-player-type="h5"
            object-fit="contain"
            @loadedmetadata="handleLoadedMetadata"
            @timeupdate="handleTimeUpdate"
            @pause="handlePause"
            @ended="handleEnded"
            @error="handleVideoError"
          />
        </view>
        <view v-else-if="!detail.contentHtml" class="video-player">
          <view class="video-player__cover video-player__empty">
            当前课程暂无视频，已切换为文字学习模式。
          </view>
        </view>
        <view class="worker-subtitle source-text">{{ detail.sourceText || '' }}</view>
        <view v-if="detail.contentHtml" class="content-panel">
          <rich-text :nodes="detail.contentHtml" />
        </view>
        <view v-if="keyPoints.length" class="summary-panel">
          <view class="summary-panel__title">关键学习点</view>
          <view v-for="(item, index) in keyPoints" :key="`${item}-${index}`" class="summary-panel__content">
            {{ index + 1 }}. {{ item }}
          </view>
        </view>
        <view v-if="fallbackTips.length && !playableVideoUrl" class="summary-panel">
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
    </template>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad, onUnload } from '@dcloudio/uni-app'
import { getTrainingCourseDetail, saveTrainingStudyProgress } from '../../api/enterprise-service'
import { courseProgressPercent } from '../../utils/training-format'
import { normalizeCourseMedia } from '../../utils/training-media'

const detail = ref({})
const courseKeyRef = ref('')
const loading = ref(true)
const loadError = ref('')
const saving = ref(false)
const lastSavedSeconds = ref(0)
const mediaDuration = ref(0)
const videoErrorCount = ref(0)

const studiedSeconds = computed(() => Number(detail.value.studiedSeconds || 0))
const keyPoints = computed(() => detail.value.keyPoints || detail.value.outlineList || [])
const fallbackTips = computed(() => detail.value.fallbackTips || [])
const progressPercentText = computed(() => `${courseProgressPercent(detail.value)}%`)
const playableVideoUrl = computed(() => (videoErrorCount.value >= 2 ? '' : detail.value.videoUrl || ''))

async function loadData(courseKey) {
  if (!courseKey) {
    loading.value = false
    loadError.value = '缺少课程标识'
    uni.showToast({ title: '缺少课程标识', icon: 'none' })
    return
  }
  courseKeyRef.value = courseKey
  loading.value = true
  loadError.value = ''
  try {
    const rawDetail = await getTrainingCourseDetail(courseKey)
    detail.value = normalizeCourseMedia(rawDetail)
    videoErrorCount.value = 0
    lastSavedSeconds.value = Number(detail.value.studiedSeconds || 0)
    mediaDuration.value = Number(detail.value.totalSeconds || 0)
  } catch (error) {
    loadError.value = error.message || '加载课程失败'
    uni.showToast({ title: loadError.value, icon: 'none' })
  } finally {
    loading.value = false
  }
}

function retryLoad() {
  loadData(courseKeyRef.value)
}

function resolveTotalSeconds() {
  return Math.max(Number(mediaDuration.value || 0), Number(detail.value.totalSeconds || 0), 0)
}

async function persistProgress(targetSeconds, silent = true) {
  const courseKey = detail.value.courseKey
  const totalSeconds = resolveTotalSeconds()
  const nextSeconds = Math.max(studiedSeconds.value, Math.floor(targetSeconds || 0))
  if (!courseKey || saving.value || nextSeconds <= lastSavedSeconds.value) {
    return
  }
  saving.value = true
  try {
    const result = await saveTrainingStudyProgress(courseKey, nextSeconds)
    detail.value = {
      ...detail.value,
      studiedSeconds: result?.studiedSeconds || nextSeconds,
      totalSeconds: result?.totalSeconds || totalSeconds,
      completed: !!result?.completed
    }
    lastSavedSeconds.value = Number(detail.value.studiedSeconds || nextSeconds)
    mediaDuration.value = Number(detail.value.totalSeconds || totalSeconds)
    if (!silent) {
      uni.showToast({ title: detail.value.completed ? '课程已完成' : '学习进度已保存', icon: 'none' })
    }
  } catch (error) {
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
    mediaDuration.value = Math.floor(duration)
  }
}

function handleTimeUpdate(event) {
  const currentTime = Math.floor(Number(event?.detail?.currentTime || 0))
  if (currentTime > studiedSeconds.value) {
    detail.value = {
      ...detail.value,
      studiedSeconds: currentTime
    }
  }
  if (currentTime - lastSavedSeconds.value >= 5) {
    persistProgress(currentTime)
  }
}

function handlePause(event) {
  const currentTime = Math.floor(Number(event?.detail?.currentTime || studiedSeconds.value || 0))
  persistProgress(currentTime)
}

function handleEnded() {
  const totalSeconds = resolveTotalSeconds()
  detail.value = {
    ...detail.value,
    studiedSeconds: Math.max(studiedSeconds.value, totalSeconds),
    completed: true
  }
  persistProgress(Math.max(studiedSeconds.value, totalSeconds), false)
}

function handleVideoError() {
  videoErrorCount.value += 1
  if (videoErrorCount.value === 1) {
    const fallback = normalizeCourseMedia({ ...detail.value, videoUrl: '' })
    if (fallback.videoUrl && fallback.videoUrl !== detail.value.videoUrl) {
      detail.value = fallback
      return
    }
  }
  detail.value = {
    ...detail.value,
    videoUrl: ''
  }
  uni.showToast({ title: '视频暂不可播放，已切换文字学习', icon: 'none' })
}

function saveCurrentProgress() {
  persistProgress(studiedSeconds.value, false)
}

function markCompleted() {
  const totalSeconds = resolveTotalSeconds()
  if (!totalSeconds) {
    uni.showToast({ title: '课程时长未加载完成', icon: 'none' })
    return
  }
  persistProgress(totalSeconds, false)
}

onLoad((options) => {
  loadData(options?.courseKey)
})

onUnload(() => {
  persistProgress(studiedSeconds.value)
})
</script>

<style lang="scss">
.course-progress {
  margin-top: 8rpx;
}

.course-progress__meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  font-size: 24rpx;
  color: #5f7893;
}

.course-progress__bar {
  height: 12rpx;
  margin-top: 14rpx;
  border-radius: 999rpx;
  background: #edf2f7;
  overflow: hidden;
}

.course-progress__fill {
  height: 100%;
  border-radius: 999rpx;
  background: linear-gradient(90deg, #0f766e, #14b8a6);
}

.course-cover {
  margin-top: 24rpx;
  border-radius: 18rpx;
  overflow: hidden;
}

.course-cover__image {
  width: 100%;
  height: 320rpx;
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

.content-panel {
  margin-top: 24rpx;
  font-size: 26rpx;
  line-height: 1.8;
  color: #183247;
}

.progress-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 28rpx;
}

.progress-actions button {
  flex: 1;
}

.worker-empty__actions button {
  flex: 1;
}
</style>
