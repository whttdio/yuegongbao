<template>
  <view class="worker-page">
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
        <view v-for="item in courses" :key="item.courseKey" class="list-row" @click="openCourse(item)">
          <view>
            <view class="list-row__title">{{ item.title }}</view>
            <view class="list-row__subtitle">
              {{ item.summary || '暂无课程摘要' }} 已学 {{ item.studiedSeconds || 0 }} 秒
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
        <view v-for="item in historyRows" :key="item.month" class="list-row">
          <view>
            <view class="list-row__title">{{ item.month }}</view>
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

const progress = ref({})
const questions = ref([])
const courses = ref([])
const historyRows = ref([])
const trainingLastLoadedAt = ref('')
const trainingLastActionAt = ref('')
const trainingLastMessage = ref('')

const progressText = computed(() => `${progress.value.completed || 0}/${progress.value.total || 10}`)
const completedCourseCount = computed(() => courses.value.filter((item) => item.completed).length)
const trainingDataSummaryText = computed(() => {
  return `题目 ${questions.value.length} 题 / 课程 ${courses.value.length} 门 / 历史 ${historyRows.value.length} 条`
})
const trainingCourseSummaryText = computed(() => {
  const completedCount = courses.value.filter((item) => item.completed).length
  return `已完成 ${completedCount} 门 / 学习中 ${Math.max(courses.value.length - completedCount, 0)} 门`
})
const latestTrainingHistoryText = computed(() => {
  if (!historyRows.value.length) {
    return '暂无历史记录'
  }
  const latest = historyRows.value[0] || {}
  return `${latest.month || '-'} / ${latest.completed || 0}/${latest.total || 0} / ${latest.passed ? '已通过' : '进行中'}`
})
const trainingUnlockConsistencyText = computed(() => {
  const total = Number(progress.value.total || 10)
  const completed = Number(progress.value.completed || 0)
  return completed >= total
    ? '培训完成后，应同步解锁首页打卡、工作台常用服务、工资页和打卡页'
    : `当前仅完成 ${completed}/${total}，首页打卡、工作台锁定入口、工资页和打卡页应继续保持拦截`
})
const trainingSnapshotText = computed(() => {
  return [
    '## 培训验收摘要',
    `- 最近加载：${trainingLastLoadedAt.value || '-'}`,
    `- 最近联动：${trainingLastActionAt.value || '-'}`,
    `- 本月进度：${progressText.value}`,
    `- 数据概览：${trainingDataSummaryText.value}`,
    `- 课程完成：${trainingCourseSummaryText.value}`,
    `- 最近历史：${latestTrainingHistoryText.value}`,
    `- 解锁联动：${trainingUnlockConsistencyText.value}`,
    `- 说明：${trainingLastMessage.value || '-'}`,
    '- 链路关联：本月培训 / 答题任务 / 培训课程 / 历史记录 / 首页工作台工资打卡解锁'
  ].join('\n')
})

async function loadData() {
  try {
    progress.value = await getTrainingProgress()
    questions.value = await getTrainingQuestions()
    const [courseData, historyData] = await Promise.all([getTrainingCourses(), getTrainingHistory()])
    courses.value = courseData?.rows || []
    historyRows.value = historyData?.rows || []
    trainingLastLoadedAt.value = new Date().toLocaleString()
    trainingLastMessage.value = progress.value.completed >= (progress.value.total || 10)
      ? '本月培训已完成，可继续核对打卡和工资解锁状态'
      : '本月培训未完成，仍应保持首页、工作台、工资页和打卡页的锁定联动校验'
  } catch (error) {
    trainingLastLoadedAt.value = new Date().toLocaleString()
    trainingLastMessage.value = error.message || '加载培训失败'
    uni.showToast({ title: error.message || '加载培训失败', icon: 'none' })
  }
}

function recordTrainingAction(action, detail) {
  trainingLastActionAt.value = new Date().toLocaleString()
  trainingLastMessage.value = detail ? `${action} / ${detail}` : action
}

function openCourse(item) {
  if (!item?.courseKey) {
    return
  }
  recordTrainingAction(`打开课程：${item.title || '-'}`, `courseKey=${item.courseKey}`)
  uni.navigateTo({ url: `/pages/training/course-detail?courseKey=${item.courseKey}` })
}

function goQuestions() {
  recordTrainingAction('进入培训答题', '核对题目数量、答题动作与解锁进度')
  uni.navigateTo({ url: '/pages/training/questions' })
}

function goHistory() {
  recordTrainingAction('进入学习记录', '核对月度记录、课程记录和通过概览')
  uni.navigateTo({ url: '/pages/training/history' })
}

function goAiTraining() {
  recordTrainingAction('进入 AI 培训', '课程为空时转入 AI 培训补充学习')
  uni.navigateTo({ url: '/pages/ai-training/detail' })
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
