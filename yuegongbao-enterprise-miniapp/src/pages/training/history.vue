<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page">
    <view v-if="loading" class="worker-card worker-empty worker-empty--panel">
      <view class="worker-empty__title">记录加载中...</view>
    </view>

    <template v-else>
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">学习记录</view>
      <view class="worker-subtitle">查看近月培训完成情况和课程学习进度</view>
      <view class="hero-stat-grid">
        <view class="hero-stat">
          <view class="hero-stat__value">{{ historyRows.length }}</view>
          <view class="hero-stat__label">记录月份</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ passedMonthCount }}</view>
          <view class="hero-stat__label">已通过</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ courseRows.length }}</view>
          <view class="hero-stat__label">课程记录</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">月度完成记录</view>
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
        <view class="worker-empty__title">当前暂无学习记录</view>
        <view class="worker-empty__desc">
          先进入本月培训完成答题或课程学习，平台会自动累计到学习记录中。
        </view>
        <view class="worker-empty__actions">
          <button class="worker-button" @click="goTraining">进入本月培训</button>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="worker-title">课程学习进度</view>
      <view v-if="courses.length">
        <view v-for="item in courses" :key="item.courseKey" class="list-row" @click="openCourse(item)">
          <view>
            <view class="list-row__title">{{ item.title }}</view>
            <view class="list-row__subtitle">已学 {{ item.studiedSeconds || 0 }} 秒 / {{ item.durationText || '-' }}</view>
          </view>
          <view class="worker-tag">{{ item.completed ? '已完成' : '继续学习' }}</view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无课程记录</view>
        <view class="worker-empty__desc">
          课程学习开始后，这里会显示每门课程的学习进度和完成状态。
        </view>
      </view>
    </view>
    </template>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getTrainingCourses, getTrainingHistory } from '../../api/enterprise-service'
import { formatTrainingMonth } from '../../utils/training-format'

const historyRows = ref([])
const courseRows = computed(() => courses.value)
const passedMonthCount = computed(() => historyRows.value.filter((item) => item.passed).length)
const courses = ref([])
const loading = ref(true)
const historyLastLoadedAt = ref('')
const historyLastActionAt = ref('')
const historyLastMessage = ref('')

const historyProgressSummaryText = computed(() => {
  const passedCount = historyRows.value.filter((item) => item.passed).length
  const completedCourseCount = courses.value.filter((item) => item.completed).length
  return `通过 ${passedCount}/${historyRows.value.length || 0} / 课程完成 ${completedCourseCount}/${courses.value.length || 0}`
})

const latestHistorySummaryText = computed(() => {
  if (!historyRows.value.length) {
    return '暂无学习记录'
  }
  const latest = historyRows.value[0] || {}
  return `${latest.month || '-'} / ${latest.completed || 0}/${latest.total || 0} / ${latest.passed ? '已通过' : '进行中'}`
})
const historyLinkageSummaryText = computed(() => {
  const completedCourseCount = courses.value.filter((item) => item.completed).length
  return `月度记录 ${historyRows.value.length} 条 / 课程完成 ${completedCourseCount}/${courses.value.length || 0} / 可回查培训首页与课程详情`
})

const historySnapshotText = computed(() => {
  return [
    '## 学习记录验收摘要',
    `- 最近加载：${historyLastLoadedAt.value || '-'}`,
    `- 最近联动：${historyLastActionAt.value || '-'}`,
    `- 月度记录：${historyRows.value.length} 条`,
    `- 课程记录：${courses.value.length} 门`,
    `- 通过概览：${historyProgressSummaryText.value}`,
    `- 最近一条：${latestHistorySummaryText.value}`,
    `- 链路核对：${historyLinkageSummaryText.value}`,
    `- 说明：${historyLastMessage.value || '-'}`,
    '- 链路关联：本月培训 / 学习记录 / 课程详情'
  ].join('\n')
})

async function loadData() {
  loading.value = true
  try {
    const [historyData, courseData] = await Promise.all([getTrainingHistory(), getTrainingCourses()])
    historyRows.value = historyData?.rows || []
    courses.value = courseData?.rows || []
    historyLastLoadedAt.value = new Date().toLocaleString()
    historyLastMessage.value = historyRows.value.length || courses.value.length
      ? '学习记录已加载，可回看每月完成度和课程进度'
      : '当前暂无学习记录，可先进入本月培训完成学习'
  } catch (error) {
    historyLastLoadedAt.value = new Date().toLocaleString()
    historyLastMessage.value = error.message || '加载记录失败'
    uni.showToast({ title: error.message || '加载记录失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

function recordHistoryAction(action, detail) {
  historyLastActionAt.value = new Date().toLocaleString()
  historyLastMessage.value = detail ? `${action} / ${detail}` : action
}

function openCourse(item) {
  if (!item?.courseKey) {
    return
  }
  recordHistoryAction(`打开课程详情：${item.title || '-'}`, `courseKey=${item.courseKey}`)
  uni.navigateTo({ url: `/pages/training/course-detail?courseKey=${item.courseKey}` })
}

function openHistoryDetail(item) {
  if (!item?.month) {
    return
  }
  recordHistoryAction(`打开月度记录：${item.monthLabel || item.month}`, `month=${item.month}`)
  uni.navigateTo({ url: `/pages/training/history-detail?month=${item.month}` })
}

function goTraining() {
  recordHistoryAction('前往本月培训', '从学习记录返回培训首页')
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
