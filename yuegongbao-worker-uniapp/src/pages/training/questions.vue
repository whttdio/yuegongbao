<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">培训答题</view>
        <view class="worker-tag">{{ progressText }}</view>
      </view>
      <view class="worker-subtitle">每月完成 10 道安全题后，才能解锁打卡和工资查询。</view>
    </view>

    <view class="worker-card">
      <view v-if="questions.length">
        <view v-for="item in questions" :key="item.questionId" class="question-card">
          <view class="question-card__title">{{ item.title }}</view>
          <view class="question-card__status">{{ item.completed ? '已完成' : '待作答' }}</view>
          <view class="question-options">
            <button
              v-for="(option, index) in normalizeOptions(item.options)"
              :key="`${item.questionId}-${index}`"
              class="question-option"
              :disabled="item.completed"
              @click="handleAnswer(item, index)"
            >
              {{ option }}
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
import { answerTrainingQuestion, getTrainingProgress, getTrainingQuestions } from '../../api/worker'

const progress = ref({})
const questions = ref([])
const questionLastLoadedAt = ref('')
const questionLastAnsweredAt = ref('')
const questionLastActionAt = ref('')
const questionLastMessage = ref('')

const progressText = computed(() => `${progress.value.completed || 0}/${progress.value.total || 10}`)
const questionSummaryText = computed(() => {
  const completedCount = questions.value.filter((item) => item.completed).length
  return `题目 ${questions.value.length} 道 / 已完成 ${completedCount} 道 / 待作答 ${Math.max(questions.value.length - completedCount, 0)} 道`
})
const questionUnlockConsistencyText = computed(() => {
  const total = Number(progress.value.total || 10)
  const completed = Number(progress.value.completed || 0)
  return completed >= total
    ? '答题完成后，应同步刷新首页打卡、工作台锁定入口、工资页和打卡页的解锁状态'
    : `当前仅完成 ${completed}/${total}，工资页和打卡页仍应保持服务端校验拦截`
})
const questionSnapshotText = computed(() => {
  return [
    '## 答题验收摘要',
    `- 最近加载：${questionLastLoadedAt.value || '-'}`,
    `- 最近答题：${questionLastAnsweredAt.value || '-'}`,
    `- 最近联动：${questionLastActionAt.value || '-'}`,
    `- 当前进度：${progressText.value}`,
    `- 题目概览：${questionSummaryText.value}`,
    `- 联动核对：${questionUnlockConsistencyText.value}`,
    `- 说明：${questionLastMessage.value || '-'}`,
    '- 链路关联：培训首页 / 培训答题 / 工资与打卡解锁'
  ].join('\n')
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

async function loadData() {
  try {
    progress.value = await getTrainingProgress()
    questions.value = await getTrainingQuestions()
    questionLastLoadedAt.value = new Date().toLocaleString()
    questionLastMessage.value = questions.value.length
      ? '答题列表已加载，可核对进度和题目完成状态'
      : '当前暂无可答题目'
  } catch (error) {
    questionLastLoadedAt.value = new Date().toLocaleString()
    questionLastMessage.value = error.message || '加载答题失败'
    uni.showToast({ title: error.message || '加载答题失败', icon: 'none' })
  }
}

function recordQuestionAction(action, detail) {
  questionLastActionAt.value = new Date().toLocaleString()
  questionLastMessage.value = detail ? `${action} / ${detail}` : action
}

async function handleAnswer(item, index) {
  try {
    const result = await answerTrainingQuestion({
      questionId: item.questionId,
      answerIndex: index
    })
    questionLastAnsweredAt.value = new Date().toLocaleString()
    recordQuestionAction(
      result?.correct ? `答题正确：${item.title || '-'}` : `答题错误：${item.title || '-'}`,
      `第 ${index + 1} 个选项 / 提交后需核对工资与打卡解锁刷新`
    )
    uni.showToast({ title: result?.correct ? '回答正确' : '回答错误', icon: 'none' })
    await loadData()
  } catch (error) {
    questionLastAnsweredAt.value = new Date().toLocaleString()
    recordQuestionAction('答题提交失败', error.message || '提交失败')
    uni.showToast({ title: error.message || '提交失败', icon: 'none' })
  }
}

function goCourses() {
  recordQuestionAction('前往培训首页', '从答题页返回培训首页继续查看课程')
  uni.navigateTo({ url: '/pages/training/index' })
}

function goTrainingHome() {
  recordQuestionAction('返回培训首页', '从答题页返回培训首页')
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
.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
}

.section-head--sub {
  margin-top: 20rpx;
}

.worker-title--small {
  font-size: 28rpx;
}

.question-card {
  padding: 22rpx 0;
  border-bottom: 1rpx solid #edf2f7;
}

.question-card:last-child {
  border-bottom: none;
}

.question-card__title {
  font-size: 28rpx;
  font-weight: 600;
  color: #16324f;
  line-height: 1.6;
}

.question-card__status {
  margin-top: 10rpx;
  font-size: 22rpx;
  color: #7890aa;
}

.question-options {
  display: flex;
  flex-direction: column;
  gap: 14rpx;
  margin-top: 18rpx;
}

.question-option {
  min-height: 76rpx;
  padding: 18rpx 22rpx;
  border: none;
  border-radius: 18rpx;
  background: #f4f8fd;
  color: #16324f;
  font-size: 26rpx;
  text-align: left;
}

.worker-empty--panel {
  padding: 24rpx 0;
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

.worker-empty__actions {
  display: flex;
  gap: 18rpx;
  margin-top: 22rpx;
}

.worker-empty__actions button {
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
