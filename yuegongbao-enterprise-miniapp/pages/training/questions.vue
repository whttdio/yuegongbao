<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page enterprise-page">
    <view class="worker-card worker-hero">
      <view class="section-head">
        <view class="worker-title worker-title--display">培训答题协同</view>
        <view class="worker-tag">{{ progressText }}</view>
      </view>
      <view class="worker-subtitle">企业端可查看本月答题进度和题目内容，但不再代员工提交答案。</view>
    </view>

    <view class="worker-card">
      <view class="enterprise-notice enterprise-notice--readonly">
        <view class="enterprise-notice__head">
          <view class="enterprise-notice__title">企业侧只读参考</view>
          <view class="worker-tag worker-tag--info">员工协同视图</view>
        </view>
        <view class="enterprise-notice__desc">答题动作会直接影响员工培训解锁、打卡和工资查询状态，企业端不再代员工作答。</view>
        <view class="enterprise-notice__reason">当前保留题目查看和进度展示，用于企业提醒员工完成本月培训任务。</view>
        <view class="enterprise-notice__actions">
          <button class="worker-button worker-button--secondary" @click="goCourses">查看培训课程</button>
          <button class="worker-button" @click="goTrainingHome">返回培训首页</button>
        </view>
      </view>
    </view>

    <view class="worker-card question-list-card">
      <view v-if="questions.length">
        <view v-for="item in questions" :key="item.questionId" class="question-card" :class="{ 'question-card--completed': item.completed }">
          <view class="question-card__head">
            <view class="question-card__title">{{ item.title }}</view>
            <view class="question-card__status" :class="item.completed ? 'question-card__status--done' : 'question-card__status--todo'">
              {{ item.completed ? '已完成' : '待作答' }}
            </view>
          </view>
          <view class="question-options">
            <view v-for="(option, index) in normalizeOptions(item.options)" :key="`${item.questionId}-${index}`" class="question-option question-option--disabled">
              <text class="question-option__index">{{ optionIndexLabel(index) }}</text>
              <text class="question-option__text">{{ option }}</text>
            </view>
          </view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无可答题目</view>
        <view class="worker-empty__desc">员工完成题目后，企业端会在这里看到最新进度和解锁结果。</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getTrainingProgress, getTrainingQuestions } from '../../api/enterprise-service'

const progress = ref({})
const questions = ref([])

const progressText = computed(() => `${progress.value.completed || 0}/${progress.value.total || 10}`)

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
  progress.value = await getTrainingProgress()
  questions.value = await getTrainingQuestions()
}

function goCourses() {
  uni.navigateTo({ url: '/pages/training/index' })
}

function goTrainingHome() {
  uni.navigateTo({ url: '/pages/training/index' })
}

onShow(() => {
  loadData().catch((error) => {
    uni.showToast({ title: error.message || '加载答题失败', icon: 'none' })
  })
})
</script>

<style lang="scss">
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
  padding: 18rpx 20rpx;
  border: 1rpx solid #dbe8ed;
  border-radius: 18rpx;
  background: #f3f6f7;
  color: #536b7d;
  font-size: 27rpx;
  line-height: 1.45;
  text-align: left;
  box-sizing: border-box;
}

.question-option__index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 46rpx;
}

.question-option__text {
  min-width: 0;
  flex: 1;
}
</style>
