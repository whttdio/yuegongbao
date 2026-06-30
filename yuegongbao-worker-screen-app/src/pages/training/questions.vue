<template>
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="section-head">
        <view class="worker-title worker-title--display">培训答题</view>
        <view class="worker-tag">{{ progressText }}</view>
      </view>
      <view class="worker-subtitle">每月完成 10 道安全题后，才能解锁打卡和工资查询。</view>
      <view class="question-progress" aria-hidden="true">
        <view class="question-progress__fill" :style="{ width: progressPercentText }" />
      </view>
      <view class="question-progress__meta">
        <text>{{ questionSummaryText }}</text>
      </view>
    </view>

    <view class="worker-card question-list-card">
      <view v-if="questions.length">
        <view
          v-for="item in questions"
          :key="item.questionId"
          class="question-card"
          :class="{ 'question-card--completed': item.completed }"
        >
          <view class="question-card__head">
            <view class="question-card__title">{{ item.title }}</view>
            <view
              class="question-card__status"
              :class="item.completed ? 'question-card__status--done' : 'question-card__status--todo'"
            >
              {{ item.completed ? '已完成' : '待作答' }}
            </view>
          </view>
          <view class="question-options">
            <button
              v-for="(option, index) in normalizeOptions(item.options)"
              :key="`${item.questionId}-${index}`"
              class="question-option"
              :class="{ 'question-option--disabled': item.completed }"
              :disabled="item.completed"
              @click="handleAnswer(item, index)"
            >
              <text class="question-option__index">{{ optionIndexLabel(index) }}</text>
              <text class="question-option__text">{{ option }}</text>
            </button>
          </view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无可答题目</view>
        <view class="worker-empty__desc">
          可先查看培训课程或返回培训首页确认本月进度；若题目已完成，工资和打卡解锁会随进度更新。
        </view>
        <view class="worker-empty__actions">
          <button class="worker-button worker-button--secondary" @click="goCourses">查看培训课程</button>
          <button class="worker-button" @click="goTrainingHome">返回培训首页</button>
        </view>
      </view>
    </view>

  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { answerTrainingQuestion, getTrainingProgress, getTrainingQuestions } from '../../api/screen'

const progress = ref({})
const questions = ref([])

const progressText = computed(() => `${progress.value.completed || 0}/${progress.value.total || 10}`)
const progressPercentText = computed(() => {
  const total = Number(progress.value.total || 10)
  const completed = Number(progress.value.completed || 0)
  if (!total) {
    return '0%'
  }
  return `${Math.min(Math.max((completed / total) * 100, 0), 100)}%`
})
const questionSummaryText = computed(() => {
  const completedCount = questions.value.filter((item) => item.completed).length
  return `题目 ${questions.value.length} 道 / 已完成 ${completedCount} 道 / 待作答 ${Math.max(questions.value.length - completedCount, 0)} 道`
})

function normalizeOptions(options) {
  if (!Array.isArray(options)) {
    return []
  }
  return options.map((option) => {
    if (typeof option === 'string') {
      return option
    }
    return option?.label || option?.text || option?.content || '-'
  })
}

function optionIndexLabel(index) {
  return String.fromCharCode(65 + index)
}

async function loadData() {
  try {
    progress.value = await getTrainingProgress()
    questions.value = await getTrainingQuestions()
  } catch (error) {
    uni.showToast({ title: error.message || '加载答题失败', icon: 'none' })
  }
}

async function handleAnswer(item, index) {
  try {
    const result = await answerTrainingQuestion({
      questionId: item.questionId,
      answerIndex: index
    })
    uni.showToast({ title: result?.correct ? '回答正确' : '回答错误', icon: 'none' })
    await loadData()
  } catch (error) {
    uni.showToast({ title: error.message || '提交失败', icon: 'none' })
  }
}

function goCourses() {
  uni.navigateTo({ url: '/pages/training/index' })
}

function goTrainingHome() {
  uni.navigateTo({ url: '/pages/training/index' })
}

onShow(loadData)
</script>

<style lang="scss">
.worker-hero .worker-tag {
  background: rgba(255, 255, 255, 0.18);
  color: #fff;
}

.question-progress {
  height: 14rpx;
  margin-top: 24rpx;
  overflow: hidden;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.22);
}

.question-progress__fill {
  height: 100%;
  border-radius: inherit;
  background: #ffd166;
  transition: width 180ms ease-out;
}

.question-progress__meta {
  margin-top: 14rpx;
  color: rgba(255, 255, 255, 0.88);
  font-size: 23rpx;
  line-height: 1.5;
}

.question-list-card {
  padding: 0;
  overflow: hidden;
}

.question-card {
  padding: 30rpx;
  border-bottom: 1rpx solid #e4edf2;
}

.question-card:last-child {
  border-bottom: none;
}

.question-card--completed {
  background: #fbfdfd;
}

.question-card__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18rpx;
}

.question-card__title {
  flex: 1;
  font-size: 30rpx;
  font-weight: 750;
  color: #122d42;
  line-height: 1.5;
}

.question-card__status {
  flex-shrink: 0;
  min-height: 42rpx;
  padding: 7rpx 16rpx;
  border-radius: 999rpx;
  font-size: 22rpx;
  font-weight: 650;
  line-height: 1.2;
}

.question-card__status--done {
  background: #e8f5ea;
  color: #1f7a3f;
}

.question-card__status--todo {
  background: #fff2cc;
  color: #8a5a00;
}

.question-options {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
  margin-top: 22rpx;
}

.question-option {
  display: flex;
  align-items: center;
  width: 100%;
  min-height: 88rpx;
  margin: 0;
  padding: 18rpx 20rpx;
  border: 1rpx solid #dbe8ed;
  border-radius: 18rpx;
  background: #f8fbfc;
  color: #183247;
  font-size: 27rpx;
  line-height: 1.45;
  text-align: left;
  box-sizing: border-box;
}

.question-option::after {
  border: none;
}

.question-option:active {
  background: #edf7f4;
  border-color: #9bcac1;
}

.question-option--disabled {
  background: #f3f6f7;
  color: #536b7d;
}

.question-option__index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 46rpx;
  height: 46rpx;
  margin-right: 18rpx;
  border-radius: 50%;
  background: #e6f2ef;
  color: #0b6b64;
  font-size: 23rpx;
  font-weight: 750;
}

.question-option--disabled .question-option__index {
  background: #e1e8ec;
  color: #607789;
}

.question-option__text {
  flex: 1;
  white-space: normal;
  word-break: break-word;
}

.worker-empty__actions button {
  flex: 1;
}

@media (prefers-reduced-motion: reduce) {
  .question-progress__fill {
    transition: none;
  }
}
</style>
