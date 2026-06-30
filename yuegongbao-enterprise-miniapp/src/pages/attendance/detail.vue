<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="section-head">
        <view class="worker-title">{{ detail.date || '-' }} 考勤详情</view>
        <view class="worker-tag">{{ statusText }}</view>
      </view>
      <view class="worker-subtitle">考勤编号：{{ detail.attendanceNo || '-' }}</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">打卡信息</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">考勤状态</view>
        <view class="detail-row__value">{{ statusText }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">考勤来源</view>
        <view class="detail-row__value">{{ sourceTypeText }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">上班打卡</view>
        <view class="detail-row__value">{{ detail.clockInTime || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">下班打卡</view>
        <view class="detail-row__value">{{ detail.clockOutTime || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">出勤时长</view>
        <view class="detail-row__value">{{ detail.attendanceHours || 0 }} 小时</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">异常备注</view>
      </view>
      <view class="detail-block">{{ detail.anomalyRemark || '当天无异常备注。' }}</view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getAttendanceDay } from '../../api/enterprise-service'

const detail = ref({})
const attendanceLastLoadedAt = ref('')
const attendanceLastMessage = ref('')

const statusText = computed(() => resolveAttendanceStatusText(detail.value.attendanceStatus))
const sourceTypeText = computed(() => resolveSourceTypeText(detail.value.sourceType))
const attendanceSummaryText = computed(() => {
  return `${detail.value.date || '-'} / ${statusText.value} / ${sourceTypeText.value} / ${detail.value.attendanceHours || 0} 小时`
})
const attendanceTimeSummaryText = computed(() => {
  return `${detail.value.clockInTime || '-'} -> ${detail.value.clockOutTime || '-'}`
})
const attendanceSnapshotText = computed(() => {
  return [
    '## 考勤详情验收摘要',
    `- 最近加载：${attendanceLastLoadedAt.value || '-'}`,
    `- 考勤摘要：${attendanceSummaryText.value}`,
    `- 打卡时间：${attendanceTimeSummaryText.value}`,
    `- 异常备注：${detail.value.anomalyRemark || '无'}`,
    `- 说明：${attendanceLastMessage.value || '-'}`,
    '- 链路关联：考勤打卡 / 月度考勤列表 / 单日考勤详情'
  ].join('\n')
})

async function loadData(date) {
  if (!date) {
    uni.showToast({ title: '缺少考勤日期', icon: 'none' })
    return
  }
  try {
    detail.value = await getAttendanceDay(date)
    attendanceLastLoadedAt.value = new Date().toLocaleString()
    attendanceLastMessage.value = '单日考勤详情已加载，可核对打卡和异常备注'
  } catch (error) {
    attendanceLastLoadedAt.value = new Date().toLocaleString()
    attendanceLastMessage.value = error.message || '加载考勤详情失败'
    uni.showToast({ title: error.message || '加载考勤详情失败', icon: 'none' })
  }
}

function resolveAttendanceStatusText(status) {
  if (status === '2') return '迟到'
  if (status === '3') return '早退'
  if (status === '4') return '缺卡'
  if (status === '5') return '迟到且早退'
  if (status === '1') return '正常'
  return status || '-'
}

function resolveSourceTypeText(sourceType) {
  if (sourceType === '3') return 'APP 打卡'
  if (sourceType === '2') return '设备采集'
  if (sourceType === '1') return '人工录入'
  return sourceType || '-'
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
  loadData(options?.date)
})
</script>
