<template>
  <view class="worker-page">
    <view v-if="loading" class="worker-card worker-empty worker-empty--panel">
      <view class="worker-empty__title">培训数据加载中...</view>
    </view>

    <template v-else>
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">安全培训</view>
      <view class="worker-subtitle">
        每月需完成 10 道安全知识题，完成后才可解锁打卡和工资查询。
      </view>
      <view class="hero-stat-grid">
        <view class="hero-stat">
          <view class="hero-stat__value">{{ progressText }}</view>
          <view class="hero-stat__label">本月进度</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ questions.length }}</view>
          <view class="hero-stat__label">待答题目</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ completedCourseCount }}</view>
          <view class="hero-stat__label">已完成课程</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">答题任务</view>
        <view class="worker-tag">{{ questions.length }} 题</view>
      </view>
      <view class="worker-subtitle">进入独立答题页，完成本月 10 题培训任务。</view>
      <button class="worker-button" @click="goQuestions">进入答题</button>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">培训课程</view>
        <view class="worker-tag">{{ courses.length }} 门</view>
      </view>
      <view v-if="courses.length">
        <view v-for="item in courses" :key="item.courseKey" class="course-row" @click="openCourse(item)">
          <image
            v-if="resolveCourseCoverUrl(item) && !brokenCoverKeys[item.courseKey]"
            :src="resolveCourseCoverUrl(item)"
            mode="aspectFill"
            class="course-row__cover"
            @error="handleCoverError(item)"
          />
          <view v-else class="course-row__cover course-row__cover--placeholder">课程</view>
          <view class="course-row__body">
            <view class="list-row__title">{{ item.title }}</view>
            <view class="list-row__subtitle">
              {{ item.summary || '暂无课程摘要' }}
            </view>
            <view class="course-row__meta">
              <text>已学 {{ item.studiedSeconds || 0 }} 秒</text>
              <text>{{ courseProgressPercentText(item) }}</text>
            </view>
            <view class="course-row__bar">
              <view class="course-row__fill" :style="{ width: courseProgressPercentText(item) }" />
            </view>
          </view>
          <view class="worker-tag">{{ item.completed ? '已完成' : (item.durationText || '继续学习') }}</view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无可学课程</view>
        <view class="worker-empty__desc">
          可先完成本月答题任务，或进入工伤 AI 培训查看平台提供的学习建议。
        </view>
        <view class="worker-empty__actions">
          <button class="worker-button worker-button--secondary" @click="goAiTraining">查看 AI 培训</button>
          <button class="worker-button" @click="goQuestions">进入答题</button>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">历史记录</view>
        <view class="notice-link" @click="goHistory">全部</view>
      </view>
      <view v-if="historyRows.length">
        <view v-for="item in historyRows" :key="item.month" class="list-row" @click="openHistoryDetail(item)">
          <view>
            <view class="list-row__title">{{ item.monthLabel || formatTrainingMonth(item.month) }}</view>
            <view class="list-row__subtitle">完成 {{ item.completed || 0 }}/{{ item.total || 0 }}</view>
          </view>
          <view class="worker-tag">{{ item.passed ? '已通过' : '进行中' }}</view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无历史记录</view>
        <view class="worker-empty__desc">
          完成本月培训题目或课程学习后，这里会显示每月完成进度和通过情况。
        </view>
      </view>
    </view>
    </template>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import {
  getTrainingCourses,
  getTrainingHistory,
  getTrainingProgress,
  getTrainingQuestions
} from '../../api/worker'
import { courseProgressPercentText, formatTrainingMonth } from '../../utils/training-format'
import { normalizeCourseMediaList, resolveCourseCoverUrl } from '../../utils/training-media'

const progress = ref({})
const questions = ref([])
const courses = ref([])
const historyRows = ref([])
const loading = ref(true)
const brokenCoverKeys = ref({})

const progressText = computed(() => `${progress.value.completed || 0}/${progress.value.total || 10}`)
const completedCourseCount = computed(() => courses.value.filter((item) => item.completed).length)

async function loadData() {
  loading.value = true
  try {
    progress.value = await getTrainingProgress()
    questions.value = await getTrainingQuestions()
    const [courseData, historyData] = await Promise.all([getTrainingCourses(), getTrainingHistory()])
    courses.value = normalizeCourseMediaList(courseData?.rows || [])
    historyRows.value = historyData?.rows || []
  } catch (error) {
    uni.showToast({ title: error.message || '加载培训失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

function openCourse(item) {
  if (!item?.courseKey) {
    return
  }
  uni.navigateTo({ url: `/pages/training/course-detail?courseKey=${item.courseKey}` })
}

function goQuestions() {
  uni.navigateTo({ url: '/pages/training/questions' })
}

function goHistory() {
  uni.navigateTo({ url: '/pages/training/history' })
}

function openHistoryDetail(item) {
  if (!item?.month) {
    return
  }
  uni.navigateTo({ url: `/pages/training/history-detail?month=${item.month}` })
}

function handleCoverError(item) {
  if (!item?.courseKey) {
    return
  }
  brokenCoverKeys.value = {
    ...brokenCoverKeys.value,
    [item.courseKey]: true
  }
}

function goAiTraining() {
  uni.navigateTo({ url: '/pages/ai-training/detail' })
}

onShow(loadData)
</script>

<style lang="scss">
.worker-empty__actions button {
  flex: 1;
}

.course-row {
  display: flex;
  align-items: flex-start;
  gap: 20rpx;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #edf2f7;
}

.course-row:last-child {
  border-bottom: none;
}

.course-row__cover {
  width: 128rpx;
  height: 128rpx;
  border-radius: 16rpx;
  flex-shrink: 0;
}

.course-row__cover--placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0f766e, #14b8a6);
  color: #fff;
  font-size: 24rpx;
}

.course-row__body {
  flex: 1;
  min-width: 0;
}

.course-row__meta {
  display: flex;
  justify-content: space-between;
  gap: 12rpx;
  margin-top: 10rpx;
  font-size: 22rpx;
  color: #5f7893;
}

.course-row__bar {
  height: 8rpx;
  margin-top: 12rpx;
  border-radius: 999rpx;
  background: #edf2f7;
  overflow: hidden;
}

.course-row__fill {
  height: 100%;
  border-radius: 999rpx;
  background: linear-gradient(90deg, #0f766e, #14b8a6);
}
</style>
