<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page enterprise-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">打卡协同</view>
      <view class="worker-subtitle">{{ trainingLockReason }}</view>
      <view class="hero-stat-grid">
        <view class="hero-stat">
          <view class="hero-stat__value">{{ todayCheckInText }}</view>
          <view class="hero-stat__label">上班</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ todayCheckOutText }}</view>
          <view class="hero-stat__label">下班</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ monthAttendanceDays }}</view>
          <view class="hero-stat__label">本月出勤</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="enterprise-notice enterprise-notice--readonly">
        <view class="enterprise-notice__head">
          <view class="enterprise-notice__title">企业侧只读参考</view>
          <view class="worker-tag worker-tag--info">员工协同视图</view>
        </view>
        <view class="enterprise-notice__desc">打卡依赖员工本人定位、现场拍照和培训解锁状态，企业端不再代员工执行上班或下班打卡。</view>
        <view class="enterprise-notice__reason">当前保留月历和当天记录查看，企业如需补充说明，请引导员工本人完成培训并进入个人端打卡。</view>
        <view class="enterprise-notice__actions">
          <button class="worker-button worker-button--secondary" @click="goTraining">查看培训</button>
          <button class="worker-button" @click="openPage('/pages/attendance/detail?date=' + todayDateText)">查看当日明细</button>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="worker-title">当月考勤月历</view>
      <view v-if="days.length">
        <view v-for="item in days" :key="item.attendanceId || item.date" class="attendance-row" @click="openDayDetail(item)">
          <view>
            <view class="attendance-row__date">{{ item.date }}</view>
            <view class="attendance-row__time">{{ item.clockInTime || '-' }} / {{ item.clockOutTime || '-' }}</view>
          </view>
          <view class="worker-tag">{{ resolveAttendanceStatusText(item.status || item.attendanceStatus) }}</view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无考勤记录</view>
        <view class="worker-empty__desc">员工本人完成打卡后，企业端可在这里查看当天和月度考勤结果。</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getAttendanceMonthly, getTrainingProgress } from '../../api/enterprise-service'
import { openPage } from '../../utils/navigation'

const days = ref([])
const trainingProgress = ref({})

const todayDateText = computed(() => {
  const now = new Date()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  return `${now.getFullYear()}-${month}-${day}`
})
const todayRecord = computed(() => days.value.find((item) => item.date === todayDateText.value) || null)
const todayCheckInText = computed(() => todayRecord.value?.clockInTime || '-')
const todayCheckOutText = computed(() => todayRecord.value?.clockOutTime || '-')
const monthAttendanceDays = computed(() => days.value.filter((item) => item.clockInTime || item.clockOutTime).length)
const trainingLockReason = computed(() => {
  const total = Number(trainingProgress.value?.total || 10)
  const completed = Number(trainingProgress.value?.completed || 0)
  return trainingProgress.value?.lockReason || `当前仅完成 ${completed}/${total}，打卡仍需员工本人完成培训后处理。`
})

function resolveAttendanceStatusText(value) {
  const text = String(value || '')
  if (text.includes('正常')) {
    return '正常'
  }
  if (text.includes('迟到')) {
    return '迟到'
  }
  if (text.includes('缺卡')) {
    return '缺卡'
  }
  return text || '待同步'
}

async function loadData() {
  const month = todayDateText.value.slice(0, 7)
  const [attendance, progress] = await Promise.all([
    getAttendanceMonthly(month),
    getTrainingProgress().catch(() => ({}))
  ])
  days.value = attendance?.days || []
  trainingProgress.value = progress || {}
}

function openDayDetail(item) {
  if (!item?.date) {
    return
  }
  openPage(`/pages/attendance/detail?date=${item.date}`)
}

function goTraining() {
  openPage('/pages/training/index')
}

onShow(() => {
  loadData().catch((error) => {
    uni.showToast({ title: error.message || '加载考勤失败', icon: 'none' })
  })
})
</script>
