import { registerWorkerPush } from '../api/enterprise-service'
import { consumePendingWorkerJumpTarget, normalizeWorkerJumpTarget, savePendingWorkerJumpTarget } from './worker-jump'
import { getToken } from './request'

const WORKER_PUSH_EVENT_KEY = 'worker_push_event_history'
const WORKER_PUSH_EVENT_LIMIT = 8
const WORKER_PUSH_DIAGNOSTICS_KEY = 'worker_push_diagnostics'

let pushListenerInitialized = false
let lastPushClickKey = ''
let lastPushClickTime = 0

export function normalizeNotificationPermission(value) {
  if (value === true) {
    return 'authorized'
  }
  if (value === false) {
    return 'denied'
  }
  const normalized = String(value || '').trim().toLowerCase()
  if (!normalized) {
    return 'unknown'
  }
  if (['authorized', 'allow', 'allowed', 'granted', '1', 'true'].includes(normalized)) {
    return 'authorized'
  }
  if (['denied', 'deny', 'forbidden', '0', 'false'].includes(normalized)) {
    return 'denied'
  }
  if (['not determined', 'not_determined', 'undetermined', 'unset'].includes(normalized)) {
    return 'not determined'
  }
  if (['config error', 'config_error'].includes(normalized)) {
    return 'config error'
  }
  return 'unknown'
}

export function resolveNotificationPermission() {
  try {
    if (typeof uni.getAppAuthorizeSetting === 'function') {
      const setting = uni.getAppAuthorizeSetting()
      return normalizeNotificationPermission(setting?.notificationAuthorized)
    }
  } catch (error) {
    return 'unknown'
  }
  return 'unknown'
}

export function resolvePushPlatform() {
  try {
    const systemInfo = uni.getSystemInfoSync()
    return systemInfo?.platform || 'unknown'
  } catch (error) {
    return 'unknown'
  }
}

export function getPushClientIdSafe() {
  return new Promise((resolve) => {
    if (typeof uni.getPushClientId !== 'function') {
      resolve('')
      return
    }
    uni.getPushClientId({
      success: (res) => {
        resolve(res?.cid || res?.clientid || '')
      },
      fail: () => resolve('')
    })
  })
}

function readWorkerPushDiagnosticsSnapshot() {
  return uni.getStorageSync(WORKER_PUSH_DIAGNOSTICS_KEY) || {}
}

function writeWorkerPushDiagnosticsSnapshot(snapshot) {
  uni.setStorageSync(WORKER_PUSH_DIAGNOSTICS_KEY, {
    ...readWorkerPushDiagnosticsSnapshot(),
    ...snapshot
  })
}

export async function syncWorkerPushRegistration() {
  const attemptedAt = new Date().toLocaleString()
  const payload = {
    notificationPermission: resolveNotificationPermission(),
    pushPlatform: resolvePushPlatform()
  }
  const pushClientId = await getPushClientIdSafe()
  if (pushClientId) {
    payload.pushClientId = pushClientId
  }
  writeWorkerPushDiagnosticsSnapshot({
    lastAttemptAt: attemptedAt,
    pushClientId,
    notificationPermission: payload.notificationPermission,
    pushPlatform: payload.pushPlatform,
    requestPayload: payload
  })
  if (!getToken()) {
    writeWorkerPushDiagnosticsSnapshot({
      lastResult: 'skipped',
      lastMessage: '当前未登录，跳过推送登记请求'
    })
    return null
  }
  if (!pushClientId) {
    writeWorkerPushDiagnosticsSnapshot({
      lastResult: 'missing_client_id',
      lastMessage: '未获取到 ClientId，未发起推送登记请求'
    })
    return null
  }
  try {
    const response = await registerWorkerPush(payload)
    writeWorkerPushDiagnosticsSnapshot({
      lastResult: 'success',
      lastMessage: response?.pushStatusText || '推送登记成功',
      responseSnapshot: response
    })
    return response
  } catch (error) {
    writeWorkerPushDiagnosticsSnapshot({
      lastResult: 'failed',
      lastMessage: error?.message || error?.errMsg || '推送登记失败'
    })
    throw error
  }
}

function readWorkerPushEvents() {
  const events = uni.getStorageSync(WORKER_PUSH_EVENT_KEY)
  return Array.isArray(events) ? events : []
}

function writeWorkerPushEvents(events) {
  uni.setStorageSync(WORKER_PUSH_EVENT_KEY, events)
}

function stringifyPushMeta(value) {
  if (value === null || value === undefined || value === '') {
    return ''
  }
  if (typeof value === 'string') {
    return value
  }
  try {
    return JSON.stringify(value)
  } catch (error) {
    return String(value)
  }
}

function recordWorkerPushEvent(type, message, target = null) {
  const currentEvents = readWorkerPushEvents()
  const event = {
    id: `${type}-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`,
    type,
    occurredAt: new Date().toLocaleString(),
    title: message?.data?.title || message?.title || '未命名推送',
    content: message?.data?.content || message?.content || '',
    targetPath: target?.path || '',
    targetQueryText: stringifyPushMeta(target?.query),
    sourceLabel: target?.sourceLabel || message?.data?.payload?.sourceLabel || message?.data?.sourceLabel || '',
    actionLabel: target?.actionLabel || message?.data?.payload?.actionLabel || message?.data?.actionLabel || ''
  }
  currentEvents.unshift(event)
  writeWorkerPushEvents(currentEvents.slice(0, WORKER_PUSH_EVENT_LIMIT))
}

export function getRecentWorkerPushEvents() {
  return readWorkerPushEvents()
}

export function clearRecentWorkerPushEvents() {
  uni.removeStorageSync(WORKER_PUSH_EVENT_KEY)
}

export function getWorkerPushDiagnostics() {
  const snapshot = readWorkerPushDiagnosticsSnapshot()
  return {
    lastAttemptAt: snapshot.lastAttemptAt || '',
    lastResult: snapshot.lastResult || 'not_started',
    lastMessage: snapshot.lastMessage || '',
    pushClientId: snapshot.pushClientId || '',
    notificationPermission: snapshot.notificationPermission || '',
    pushPlatform: snapshot.pushPlatform || '',
    requestPayload: snapshot.requestPayload || null,
    responseSnapshot: snapshot.responseSnapshot || null
  }
}

export function clearWorkerPushDiagnostics() {
  uni.removeStorageSync(WORKER_PUSH_DIAGNOSTICS_KEY)
}

function isDuplicatePushClick(target) {
  const targetKey = JSON.stringify(target || {})
  const now = Date.now()
  if (targetKey && targetKey === lastPushClickKey && now - lastPushClickTime < 1500) {
    return true
  }
  lastPushClickKey = targetKey
  lastPushClickTime = now
  return false
}

function handlePushClickMessage(message) {
  const target = normalizeWorkerJumpTarget(message?.data?.payload || message?.data)
  if (!target || isDuplicatePushClick(target)) {
    return
  }
  recordWorkerPushEvent('click', message, target)
  savePendingWorkerJumpTarget(target)
  if (!getToken()) {
    uni.reLaunch({ url: '/pages/login/index' })
    return
  }
  setTimeout(() => {
    consumePendingWorkerJumpTarget()
  }, 80)
}

function handlePushReceiveMessage(message) {
  const title = message?.data?.title || '收到新通知'
  if (!title) {
    return
  }
  const target = normalizeWorkerJumpTarget(message?.data?.payload || message?.data)
  recordWorkerPushEvent('receive', message, target)
  uni.showToast({ title, icon: 'none' })
}

export function initWorkerPushListener() {
  if (pushListenerInitialized || typeof uni.onPushMessage !== 'function') {
    return
  }
  pushListenerInitialized = true
  uni.onPushMessage((message) => {
    if (message?.type === 'click') {
      handlePushClickMessage(message)
      return
    }
    if (message?.type === 'receive') {
      handlePushReceiveMessage(message)
    }
  })
}
