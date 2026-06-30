<template>
  <!-- ??????????????? /app/screen/** -->
  <view class="worker-page worker-page--screen-detail">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">今日打卡</view>
      <view class="worker-subtitle">{{ trainingLocked ? trainingLockReason : (trainingProgressText || '定位和考勤记录将写入平台') }}</view>
      <view v-if="!trainingLocked" class="hero-stat-grid">
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

    <view class="worker-card screen-detail-card screen-detail-card--wide">
      <view v-if="trainingLocked" class="worker-lock-panel">
        <view class="worker-lock-panel__title">本月培训未完成，暂不可打卡</view>
        <view class="worker-lock-panel__desc">{{ trainingLockReason }}</view>
        <button class="worker-button worker-button--secondary" @click="goTraining">去完成培训</button>
      </view>

      <view class="face-block">
        <view class="face-block__head">
          <view class="worker-title worker-title--small">人脸留痕（可选）</view>
          <view v-if="faceImageUrl" class="face-block__clear" @click="clearFaceImage">重新拍摄</view>
        </view>
        <view class="worker-subtitle">
          可先拍摄现场人脸图片，与定位和考勤记录一起上传，便于后续复核。
        </view>
        <view class="face-block__actions">
          <button class="worker-button worker-button--secondary" :disabled="faceUploading" @click="captureFaceImage">
            {{ faceUploading ? '上传中...' : (faceImageUrl ? '重新拍摄' : '拍摄人脸') }}
          </button>
        </view>
        <image v-if="faceImageUrl" :src="faceImageUrl" class="face-block__preview" mode="aspectFill" />
      </view>

      <view class="attendance-actions">
        <button class="worker-button" :disabled="trainingLocked" @click="handleCheckIn">上班打卡</button>
        <button class="worker-button worker-button--secondary" :disabled="trainingLocked" @click="handleCheckOut">下班打卡</button>
      </view>
    </view>

    <view v-if="pendingQueue.length" class="worker-card screen-detail-card">
      <view class="section-head">
        <view class="worker-title">待补传记录</view>
        <view class="section-head__actions">
          <view class="worker-tag">{{ pendingQueue.length }} 条</view>
          <view class="clear-action" @click="flushOfflineQueue({ manual: true })">
            {{ syncing ? '补传中...' : '立即补传' }}
          </view>
        </view>
      </view>
      <view v-for="item in pendingQueue" :key="item.id" class="attendance-row attendance-row--top">
        <view class="attendance-row__main">
          <view class="attendance-row__date">{{ item.label }} #{{ item.sequenceNo }}</view>
          <view class="attendance-row__time">{{ item.timeText || '-' }}</view>
          <view class="history-row__detail">{{ formatPendingQueueItemText(item) }}</view>
          <view v-if="item.lastAttemptMessage" class="history-row__detail">{{ item.lastAttemptMessage }}</view>
        </view>
        <view class="worker-tag">{{ formatQueueAttemptStatusText(item.lastAttemptResult) }}</view>
      </view>
    </view>
    <view v-if="syncHistory.length" class="worker-card screen-detail-card">
      <view class="section-head">
        <view class="worker-title">最近补传记录</view>
        <view class="worker-tag worker-tag--info">{{ syncHistory.length }} 条</view>
      </view>
      <view v-for="item in syncHistory" :key="item.id" class="history-row">
        <view class="history-row__head">
          <view class="history-row__title">{{ item.status }}</view>
          <view class="worker-tag" :class="item.success ? 'worker-tag--success' : 'worker-tag--warning'">
            {{ item.success ? '成功' : '待处理' }}
          </view>
        </view>
        <view class="history-row__meta">{{ item.at }}</view>
        <view class="history-row__detail">{{ item.message }}</view>
      </view>
    </view>

    <view class="worker-card screen-detail-card">
      <view class="worker-title">当月考勤月历</view>
      <view v-if="days.length">
        <view
          v-for="item in days"
          :key="item.attendanceId || item.date"
          class="attendance-row"
          @click="openDayDetail(item)"
        >
          <view>
            <view class="attendance-row__date">{{ item.date }}</view>
            <view class="attendance-row__time">{{ item.clockInTime || '-' }} / {{ item.clockOutTime || '-' }}</view>
          </view>
          <view class="worker-tag">{{ resolveAttendanceStatusText(item.status || item.attendanceStatus) }}</view>
        </view>
      </view>
      <view v-else class="worker-empty">本月暂无考勤记录</view>
    </view>
  </view>
</template>

<script setup>
import { computed, onUnmounted, ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { checkIn, checkOut, getAttendanceMonthly, getTrainingProgress, uploadWorkerImage } from '../../api/screen'

const OFFLINE_QUEUE_KEY = 'worker_attendance_offline_queue'
const ATTENDANCE_DIAGNOSTICS_KEY = 'worker_attendance_diagnostics'
const MAX_UPLOAD_SIZE = 2 * 1024 * 1024
const SYNC_HISTORY_LIMIT = 12

const runtimePlatform = ref('')
const days = ref([])
const pendingQueue = ref([])
const syncing = ref(false)
const pageAction = ref('')
const autoActionHandled = ref(false)
const faceUploading = ref(false)
const faceImageUrl = ref('')
const trainingProgress = ref({})
const networkConnected = ref(true)
const locationLastAt = ref('')
const locationLastSuccess = ref(null)
const locationPermissionStatus = ref('')
const locationErrorCode = ref('')
const locationCoordsText = ref('')
const locationErrorText = ref('')
const lastActionAt = ref('')
const lastActionType = ref('')
const lastActionResult = ref('')
const lastActionMode = ref('')
const lastActionMessage = ref('')
const lastSyncAt = ref('')
const lastSyncStatus = ref('')
const lastSyncMessage = ref('')
const lastSyncTrigger = ref('')
const lastSyncProcessedCount = ref(0)
const lastSyncRemainingCount = ref(0)
const lastSyncFailedItem = ref('')
const syncHistory = ref([])

function formatQueueAttemptStatusText(result) {
  if (result === 'success') {
    return '补传成功'
  }
  if (result === 'syncing') {
    return '补传中'
  }
  if (result === 'failed') {
    return '补传失败'
  }
  if (result === 'waiting_network') {
    return '待网络恢复'
  }
  return '未尝试'
}

function normalizeQueueItem(item = {}, index = 0) {
  const sequenceNo = Number(item.sequenceNo || index + 1)
  const attemptCount = Number(item.attemptCount || 0)
  return {
    id: item.id || `offline-${Date.now()}-${index}`,
    type: item.type || 'CHECK_IN',
    payload: item.payload || {},
    label: item.label || (item.type === 'CHECK_OUT' ? '下班打卡' : '上班打卡'),
    timeText: item.timeText || item.createdAt || '',
    createdAt: item.createdAt || item.timeText || '',
    sequenceNo: Number.isFinite(sequenceNo) && sequenceNo > 0 ? sequenceNo : index + 1,
    attemptCount: Number.isFinite(attemptCount) && attemptCount >= 0 ? attemptCount : 0,
    lastAttemptAt: item.lastAttemptAt || '',
    lastAttemptResult: item.lastAttemptResult || 'pending',
    lastAttemptMessage: item.lastAttemptMessage || ''
  }
}

function formatPendingQueueItemText(item) {
  const normalizedItem = normalizeQueueItem(item)
  const parts = [
    `#${normalizedItem.sequenceNo}`,
    normalizedItem.type === 'CHECK_OUT' ? '下班' : '上班',
    `尝试 ${normalizedItem.attemptCount} 次`,
    `最近${formatQueueAttemptStatusText(normalizedItem.lastAttemptResult)}`
  ]
  if (normalizedItem.lastAttemptAt) {
    parts.push(normalizedItem.lastAttemptAt)
  }
  return parts.join(' / ')
}

const trainingLocked = computed(() => {
  if (trainingProgress.value?.needComplete === true) {
    return true
  }
  const total = Number(trainingProgress.value?.total || 10)
  const completed = Number(trainingProgress.value?.completed || 0)
  return total > 0 && completed < total
})

const trainingProgressText = computed(() => {
  const total = Number(trainingProgress.value?.total || 10)
  const completed = Number(trainingProgress.value?.completed || 0)
  if (!total) {
    return ''
  }
  return `${completed}/${total}`
})

const trainingLockReason = computed(() => {
  const total = Number(trainingProgress.value?.total || 10)
  const completed = Number(trainingProgress.value?.completed || 0)
  return trainingProgress.value?.lockReason || `当前仅完成 ${completed}/${total} 题，请先完成本月安全培训后再打卡。`
})

const todayDateText = computed(() => {
  const now = new Date()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  return `${now.getFullYear()}-${month}-${day}`
})

const todayRecord = computed(() => {
  return days.value.find((item) => item.date === todayDateText.value) || null
})

const todayCheckInText = computed(() => todayRecord.value?.clockInTime || '-')
const todayCheckOutText = computed(() => todayRecord.value?.clockOutTime || '-')
const monthAttendanceDays = computed(() => {
  return days.value.filter((item) => item.clockInTime || item.clockOutTime).length
})

const networkStatusText = computed(() => (networkConnected.value ? '在线' : '离线'))
const attendancePlatformText = computed(() => formatPlatform(runtimePlatform.value))

const locationStatusText = computed(() => {
  if (locationLastSuccess.value === true) {
    return locationLastAt.value ? `定位成功 / ${locationLastAt.value}` : '定位成功'
  }
  if (locationLastSuccess.value === false) {
    return locationLastAt.value ? `定位降级 / ${locationLastAt.value}` : '定位降级'
  }
  return '未获取'
})
const locationPermissionText = computed(() => {
  const parts = [formatPermissionStatus(locationPermissionStatus.value)]
  if (locationErrorCode.value) {
    parts.push(locationErrorCode.value)
  }
  return parts.join(' / ')
})

const lastActionSummaryText = computed(() => {
  const parts = []
  if (lastActionType.value) {
    parts.push(lastActionType.value)
  }
  if (lastActionResult.value) {
    parts.push(lastActionResult.value)
  }
  if (lastActionMode.value) {
    parts.push(lastActionMode.value)
  }
  if (lastActionAt.value) {
    parts.push(lastActionAt.value)
  }
  return parts.join(' / ') || '-'
})

const syncSummaryText = computed(() => {
  const parts = []
  if (lastSyncStatus.value) {
    parts.push(lastSyncStatus.value)
  }
  if (lastSyncAt.value) {
    parts.push(lastSyncAt.value)
  }
  if (lastSyncTrigger.value) {
    parts.push(lastSyncTrigger.value === 'manual' ? '手动触发' : '自动触发')
  }
  if (lastSyncProcessedCount.value || lastSyncRemainingCount.value) {
    parts.push(`已补 ${lastSyncProcessedCount.value} / 剩余 ${lastSyncRemainingCount.value}`)
  }
  return parts.join(' / ') || '暂无补传记录'
})
const queueLifecycleText = computed(() => {
  if (!pendingQueue.value.length) {
    return '当前无离线队列'
  }
  const firstItem = pendingQueue.value[0]
  const lastItem = pendingQueue.value[pendingQueue.value.length - 1]
  return [
    `${pendingQueue.value.length} 条`,
    `首条 ${firstItem?.createdAt || firstItem?.timeText || '-'}`,
    `末条 ${lastItem?.createdAt || lastItem?.timeText || '-'}`
  ].join(' / ')
})

const attendanceSnapshotText = computed(() => {
  const queueLines = pendingQueue.value.map((item) => {
    return `- 队列项 #${item.sequenceNo}：${item.label} / ${formatQueueAttemptStatusText(item.lastAttemptResult)} / 尝试 ${item.attemptCount} 次${item.lastAttemptAt ? ` / ${item.lastAttemptAt}` : ''}${item.lastAttemptMessage ? ` / ${item.lastAttemptMessage}` : ''}`
  })
  return [
    '## 打卡联调摘要',
    `- 运行平台：${attendancePlatformText.value}`,
    `- 当前网络：${networkStatusText.value}`,
    `- 待补传队列：${pendingQueue.value.length} 条`,
    `- 最近定位：${locationStatusText.value}`,
    `- 定位权限：${locationPermissionText.value}`,
    `- 最近坐标：${locationCoordsText.value || '-'}`,
    `- 定位说明：${locationErrorText.value || '-'}`,
    `- 最近动作：${lastActionSummaryText.value}`,
    `- 动作说明：${lastActionMessage.value || '-'}`,
    `- 队列生命周期：${queueLifecycleText.value}`,
    `- 最近补传：${syncSummaryText.value}`,
    `- 补传说明：${lastSyncMessage.value || '-'}`,
    `- 最近失败队列项：${lastSyncFailedItem.value || '-'}`
  ]
    .concat(queueLines.length ? queueLines : ['- 队列项：暂无'])
    .join('\n')
})

function resolveRuntimePlatform() {
  try {
    const systemInfo = uni.getSystemInfoSync()
    return systemInfo?.uniPlatform || systemInfo?.platform || ''
  } catch (error) {
    return ''
  }
}

function formatPlatform(platform) {
  const normalized = String(platform || '').toLowerCase()
  if (!normalized) {
    return '未知平台'
  }
  if (normalized === 'app-plus') {
    return 'App'
  }
  if (normalized === 'h5' || normalized === 'web') {
    return 'H5'
  }
  if (normalized === 'mp-weixin') {
    return '微信小程序'
  }
  if (normalized === 'android') {
    return 'Android'
  }
  if (normalized === 'ios') {
    return 'iOS'
  }
  return platform
}

function formatPermissionStatus(status) {
  const normalized = String(status || '').toLowerCase()
  if (!normalized) {
    return '未校验'
  }
  if (normalized === 'granted') {
    return '已授权'
  }
  if (normalized === 'denied') {
    return '权限拒绝'
  }
  if (normalized === 'cancelled') {
    return '用户取消'
  }
  if (normalized === 'failed') {
    return '待排查'
  }
  return status
}

function classifyLocationError(error) {
  const errMsg = String(error?.errMsg || '')
  const lowerErrMsg = errMsg.toLowerCase()
  if (lowerErrMsg.includes('auth deny') || lowerErrMsg.includes('permission deny') || lowerErrMsg.includes('no permission')) {
    return {
      permissionStatus: 'denied',
      errorCode: 'LOCATION_PERMISSION_DENIED',
      message: '定位权限被拒绝，已按无定位信息提交'
    }
  }
  if (lowerErrMsg.includes('cancel')) {
    return {
      permissionStatus: 'cancelled',
      errorCode: 'LOCATION_CANCELLED',
      message: '用户取消了本次定位，已按无定位信息提交'
    }
  }
  return {
    permissionStatus: 'failed',
    errorCode: 'LOCATION_FAILED',
    message: errMsg || '定位失败，已按无定位信息提交'
  }
}

async function loadMonthly() {
  const month = new Date().toISOString().slice(0, 7)
  try {
    const data = await getAttendanceMonthly(month)
    days.value = data?.days || []
  } catch (error) {
    uni.showToast({ title: error.message || '加载考勤失败', icon: 'none' })
  }
}

async function loadTrainingProgress() {
  try {
    trainingProgress.value = await getTrainingProgress()
  } catch (error) {
    trainingProgress.value = {}
  }
}

function resolveDeviceCode() {
  try {
    const info = uni.getSystemInfoSync()
    const rawCode = `${info.platform || 'app'}-${info.brand || ''}-${info.model || ''}`.replace(/\s+/g, '')
    return rawCode.slice(0, 64) || 'worker-app'
  } catch (error) {
    return 'worker-app'
  }
}

function persistAttendanceDiagnostics() {
  uni.setStorageSync(ATTENDANCE_DIAGNOSTICS_KEY, {
    runtimePlatform: runtimePlatform.value,
    networkConnected: networkConnected.value,
    locationLastAt: locationLastAt.value,
    locationLastSuccess: locationLastSuccess.value,
    locationPermissionStatus: locationPermissionStatus.value,
    locationErrorCode: locationErrorCode.value,
    locationCoordsText: locationCoordsText.value,
    locationErrorText: locationErrorText.value,
    lastActionAt: lastActionAt.value,
    lastActionType: lastActionType.value,
    lastActionResult: lastActionResult.value,
    lastActionMode: lastActionMode.value,
    lastActionMessage: lastActionMessage.value,
    lastSyncAt: lastSyncAt.value,
    lastSyncStatus: lastSyncStatus.value,
    lastSyncMessage: lastSyncMessage.value,
    lastSyncTrigger: lastSyncTrigger.value,
    lastSyncProcessedCount: lastSyncProcessedCount.value,
    lastSyncRemainingCount: lastSyncRemainingCount.value,
    lastSyncFailedItem: lastSyncFailedItem.value,
    syncHistory: syncHistory.value,
    faceImageUrl: faceImageUrl.value
  })
}

function restoreAttendanceDiagnostics() {
  const snapshot = uni.getStorageSync(ATTENDANCE_DIAGNOSTICS_KEY) || {}
  runtimePlatform.value = snapshot.runtimePlatform || resolveRuntimePlatform()
  networkConnected.value = snapshot.networkConnected !== false
  locationLastAt.value = snapshot.locationLastAt || ''
  locationLastSuccess.value = typeof snapshot.locationLastSuccess === 'boolean' ? snapshot.locationLastSuccess : null
  locationPermissionStatus.value = snapshot.locationPermissionStatus || ''
  locationErrorCode.value = snapshot.locationErrorCode || ''
  locationCoordsText.value = snapshot.locationCoordsText || ''
  locationErrorText.value = snapshot.locationErrorText || ''
  lastActionAt.value = snapshot.lastActionAt || ''
  lastActionType.value = snapshot.lastActionType || ''
  lastActionResult.value = snapshot.lastActionResult || ''
  lastActionMode.value = snapshot.lastActionMode || ''
  lastActionMessage.value = snapshot.lastActionMessage || ''
  lastSyncAt.value = snapshot.lastSyncAt || ''
  lastSyncStatus.value = snapshot.lastSyncStatus || ''
  lastSyncMessage.value = snapshot.lastSyncMessage || ''
  lastSyncTrigger.value = snapshot.lastSyncTrigger || ''
  lastSyncProcessedCount.value = Number(snapshot.lastSyncProcessedCount || 0)
  lastSyncRemainingCount.value = Number(snapshot.lastSyncRemainingCount || 0)
  lastSyncFailedItem.value = snapshot.lastSyncFailedItem || ''
  syncHistory.value = Array.isArray(snapshot.syncHistory) ? snapshot.syncHistory : []
  faceImageUrl.value = snapshot.faceImageUrl || ''
}

function recordLocationSnapshot(success, location = null, message = '', permissionStatus = '', errorCode = '') {
  locationLastAt.value = new Date().toLocaleString()
  locationLastSuccess.value = success
  locationPermissionStatus.value = permissionStatus
  locationErrorCode.value = errorCode
  locationCoordsText.value =
    location?.latitude !== undefined && location?.longitude !== undefined
      ? `${Number(location.latitude).toFixed(6)}, ${Number(location.longitude).toFixed(6)}`
      : ''
  locationErrorText.value = message || ''
  persistAttendanceDiagnostics()
}

function recordAttendanceAction(type, result, mode, message = '') {
  lastActionAt.value = new Date().toLocaleString()
  lastActionType.value = type
  lastActionResult.value = result
  lastActionMode.value = mode
  lastActionMessage.value = message
  persistAttendanceDiagnostics()
}

function appendSyncHistory(success, status, message, meta = {}) {
  syncHistory.value = [
    {
      id: `${Date.now()}-${Math.random().toString(36).slice(2, 8)}`,
      at: new Date().toLocaleString(),
      success,
      status,
      message,
      trigger: meta.trigger || '',
      processedCount: Number(meta.processedCount || 0),
      remainingCount: Number(meta.remainingCount || 0),
      failedItem: meta.failedItem || ''
    },
    ...syncHistory.value
  ].slice(0, SYNC_HISTORY_LIMIT)
  persistAttendanceDiagnostics()
}

function recordSyncSnapshot(success, status, message, meta = {}) {
  lastSyncAt.value = new Date().toLocaleString()
  lastSyncStatus.value = status
  lastSyncMessage.value = message
  lastSyncTrigger.value = meta.trigger || ''
  lastSyncProcessedCount.value = Number(meta.processedCount || 0)
  lastSyncRemainingCount.value = Number(meta.remainingCount || 0)
  lastSyncFailedItem.value = meta.failedItem || ''
  appendSyncHistory(success, status, message, meta)
}

function refreshNetworkStatus() {
  if (typeof uni.getNetworkType !== 'function') {
    return
  }
  uni.getNetworkType({
    success: (res) => {
      networkConnected.value = res.networkType !== 'none'
      persistAttendanceDiagnostics()
    }
  })
}

function getLocationPayload() {
  return new Promise((resolve) => {
    uni.getLocation({
      type: 'gcj02',
      success: (res) => {
        recordLocationSnapshot(true, res, '', 'granted', '')
        resolve({
          latitude: res.latitude,
          longitude: res.longitude,
          deviceCode: resolveDeviceCode(),
          faceImageUrl: faceImageUrl.value || undefined
        })
      },
      fail: (error) => {
        const diagnostics = classifyLocationError(error)
        recordLocationSnapshot(false, null, diagnostics.message, diagnostics.permissionStatus, diagnostics.errorCode)
        resolve({
          deviceCode: resolveDeviceCode(),
          faceImageUrl: faceImageUrl.value || undefined
        })
      }
    })
  })
}

async function captureFaceImage() {
  faceUploading.value = true
  try {
    const chooseResult = await new Promise((resolve, reject) => {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['camera'],
        success: resolve,
        fail: reject
      })
    })
    const filePath = chooseResult?.tempFilePaths?.[0]
    const tempFile = chooseResult?.tempFiles?.[0] || {}
    if (!filePath) {
      throw new Error('未拍摄到人脸图片')
    }
    if (Number(tempFile.size || 0) > MAX_UPLOAD_SIZE) {
      throw new Error('人脸图片需压缩到 2MB 以内后再上传')
    }
    const uploadResult = await uploadWorkerImage(filePath)
    faceImageUrl.value = uploadResult?.url || ''
    persistAttendanceDiagnostics()
    uni.showToast({ title: '人脸图片已上传', icon: 'none' })
  } catch (error) {
    if (error?.errMsg?.includes('cancel')) {
      return
    }
    uni.showToast({ title: error.message || '人脸图片上传失败', icon: 'none' })
  } finally {
    faceUploading.value = false
  }
}

function clearFaceImage() {
  faceImageUrl.value = ''
  persistAttendanceDiagnostics()
}

function loadQueue() {
  const storedQueue = uni.getStorageSync(OFFLINE_QUEUE_KEY) || []
  pendingQueue.value = Array.isArray(storedQueue) ? storedQueue.map((item, index) => normalizeQueueItem(item, index)) : []
}

function saveQueue(queue) {
  const normalizedQueue = Array.isArray(queue) ? queue.map((item, index) => normalizeQueueItem(item, index)) : []
  pendingQueue.value = normalizedQueue
  uni.setStorageSync(OFFLINE_QUEUE_KEY, normalizedQueue)
  persistAttendanceDiagnostics()
}

function enqueueOfflineRecord(type, payload) {
  const queue = [...pendingQueue.value]
  const label = type === 'CHECK_IN' ? '上班打卡' : '下班打卡'
  queue.push({
    id: `${type}-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`,
    type,
    payload,
    label,
    timeText: new Date().toLocaleString(),
    createdAt: new Date().toLocaleString(),
    sequenceNo: queue.length + 1,
    attemptCount: 0,
    lastAttemptAt: '',
    lastAttemptResult: 'pending',
    lastAttemptMessage: ''
  })
  saveQueue(queue)
  recordAttendanceAction(label, '已离线缓存', 'offline', `待补传队列 ${queue.length} 条`)
}

function isOfflineError(error) {
  const message = `${error?.message || error?.errMsg || error || ''}`.toLowerCase()
  return message.includes('request:fail') || message.includes('network') || message.includes('timeout') || message.includes('fail')
}

async function flushOfflineQueue({ manual = false } = {}) {
  if (syncing.value || !pendingQueue.value.length) {
    if (manual && !pendingQueue.value.length) {
      uni.showToast({ title: '当前没有待补传记录', icon: 'none' })
    }
    return
  }
  syncing.value = true
  try {
    const trigger = manual ? 'manual' : 'auto'
    const currentQueue = pendingQueue.value.map((item, index) => normalizeQueueItem(item, index))
    const workingQueue = currentQueue.map((item) => ({ ...item }))
    saveQueue(workingQueue)
    let processedCount = 0
    for (let index = 0; index < workingQueue.length; index += 1) {
      const item = workingQueue[index]
      item.attemptCount += 1
      item.lastAttemptAt = new Date().toLocaleString()
      item.lastAttemptResult = 'syncing'
      item.lastAttemptMessage = '正在尝试补传'
      saveQueue(workingQueue)
      try {
        if (item.type === 'CHECK_IN') {
          await checkIn(item.payload || {})
        } else {
          await checkOut(item.payload || {})
        }
        item.lastAttemptResult = 'success'
        item.lastAttemptMessage = '补传成功'
        appendSyncHistory(
          true,
          `队列项 #${item.sequenceNo} 补传成功`,
          `${item.label} / 第 ${item.attemptCount} 次尝试`,
          { trigger, processedCount: processedCount + 1, remainingCount: workingQueue.length - index - 1, failedItem: '' }
        )
        processedCount += 1
      } catch (error) {
        const failedMessage = error?.message || error?.errMsg || '补传失败'
        item.lastAttemptResult = isOfflineError(error) ? 'waiting_network' : 'failed'
        item.lastAttemptMessage = failedMessage
        const nextQueue = workingQueue.slice(index).map((restItem, restIndex) => {
          return normalizeQueueItem(
            {
              ...restItem,
              sequenceNo: restIndex + 1
            },
            restIndex
          )
        })
        saveQueue(nextQueue)
        recordSyncSnapshot(
          false,
          isOfflineError(error) ? '待网络恢复' : '补传失败',
          `已补传 ${processedCount} 条，剩余 ${nextQueue.length} 条。${failedMessage}`,
          {
            trigger,
            processedCount,
            remainingCount: nextQueue.length,
            failedItem: `#${item.sequenceNo} ${item.label}`
          }
        )
        if (manual) {
          uni.showToast({ title: failedMessage, icon: 'none' })
        }
        return
      }
    }
    saveQueue([])
    await loadMonthly()
    recordSyncSnapshot(true, '补传成功', `已补传 ${processedCount} 条离线打卡记录`, {
      trigger,
      processedCount,
      remainingCount: 0,
      failedItem: ''
    })
    if (manual || processedCount > 0) {
      uni.showToast({ title: '离线打卡已补传', icon: 'none' })
    }
  } finally {
    syncing.value = false
  }
}

async function handleCheckIn() {
  if (trainingLocked.value) {
    uni.showToast({ title: trainingLockReason.value, icon: 'none' })
    return
  }
  const payload = await getLocationPayload()
  try {
    const data = await checkIn(payload)
    recordAttendanceAction('上班打卡', '提交成功', 'online', data?.time ? `服务时间 ${data.time}` : '服务端已受理')
    uni.showToast({ title: `打卡成功 ${data?.time || ''}`.trim(), icon: 'none' })
    await loadMonthly()
  } catch (error) {
    if (isOfflineError(error)) {
      enqueueOfflineRecord('CHECK_IN', payload)
      uni.showToast({ title: '当前离线，已缓存上班打卡', icon: 'none' })
      return
    }
    recordAttendanceAction('上班打卡', '提交失败', 'online', error.message || '打卡失败')
    uni.showToast({ title: error.message || '打卡失败', icon: 'none' })
  }
}

async function handleCheckOut() {
  if (trainingLocked.value) {
    uni.showToast({ title: trainingLockReason.value, icon: 'none' })
    return
  }
  const payload = await getLocationPayload()
  try {
    const data = await checkOut(payload)
    recordAttendanceAction('下班打卡', '提交成功', 'online', data?.time ? `服务时间 ${data.time}` : '服务端已受理')
    uni.showToast({ title: `下班打卡成功 ${data?.time || ''}`.trim(), icon: 'none' })
    await loadMonthly()
  } catch (error) {
    if (isOfflineError(error)) {
      enqueueOfflineRecord('CHECK_OUT', payload)
      uni.showToast({ title: '当前离线，已缓存下班打卡', icon: 'none' })
      return
    }
    recordAttendanceAction('下班打卡', '提交失败', 'online', error.message || '打卡失败')
    uni.showToast({ title: error.message || '打卡失败', icon: 'none' })
  }
}

function handleNetworkChange(status) {
  networkConnected.value = !!status?.isConnected
  persistAttendanceDiagnostics()
  if (status?.isConnected) {
    flushOfflineQueue()
  }
}

function resolveAttendanceStatusText(status) {
  if (status === '2') {
    return '迟到'
  }
  if (status === '3') {
    return '早退'
  }
  if (status === '4') {
    return '缺卡'
  }
  if (status === '5') {
    return '迟到且早退'
  }
  if (status === '1') {
    return '正常'
  }
  return status || '-'
}

function openDayDetail(item) {
  if (!item?.date) {
    return
  }
  uni.navigateTo({ url: `/pages/attendance/detail?date=${item.date}` })
}

function goTraining() {
  uni.navigateTo({ url: '/pages/training/index' })
}

function copyText(content, successTitle) {
  if (!content) {
    uni.showToast({ title: '暂无可复制内容', icon: 'none' })
    return
  }
  if (typeof uni.setClipboardData !== 'function') {
    uni.showToast({ title: '当前环境不支持复制，请改用截图', icon: 'none' })
    return
  }
  uni.setClipboardData({
    data: content,
    success: () => {
      uni.showToast({ title: successTitle, icon: 'none' })
    },
    fail: () => {
      uni.showToast({ title: '复制失败，请改用截图', icon: 'none' })
    }
  })
}

async function runAutoActionIfNeeded() {
  if (autoActionHandled.value || !pageAction.value) {
    return
  }
  if (trainingLocked.value) {
    uni.showToast({ title: trainingLockReason.value, icon: 'none' })
    return
  }
  autoActionHandled.value = true
  if (pageAction.value === 'checkOut') {
    await handleCheckOut()
    return
  }
  await handleCheckIn()
}

onLoad((options) => {
  runtimePlatform.value = resolveRuntimePlatform()
  pageAction.value = options?.action || ''
  autoActionHandled.value = false
})

onShow(async () => {
  restoreAttendanceDiagnostics()
  loadQueue()
  refreshNetworkStatus()
  await loadTrainingProgress()
  await loadMonthly()
  await flushOfflineQueue()
  await runAutoActionIfNeeded()
})

uni.onNetworkStatusChange(handleNetworkChange)

onUnmounted(() => {
  if (typeof uni.offNetworkStatusChange === 'function') {
    uni.offNetworkStatusChange(handleNetworkChange)
  }
})
</script>
