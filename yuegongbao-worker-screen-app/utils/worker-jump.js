import { openPageTarget } from './navigation'
import { getToken } from './request'

const PENDING_WORKER_JUMP_KEY = 'worker_pending_jump_target'
const WORKER_JUMP_DIAGNOSTICS_KEY = 'worker_jump_diagnostics'

function parseJsonSafe(value) {
  if (typeof value !== 'string') {
    return null
  }
  const trimmedValue = value.trim()
  if (!trimmedValue) {
    return null
  }
  try {
    return JSON.parse(trimmedValue)
  } catch (error) {
    return null
  }
}

function resolveNoticeTarget(payload) {
  const noticeId = payload?.noticeId || payload?.id
  if (!noticeId) {
    return null
  }
  return {
    path: '/pages/notice/detail',
    query: { noticeId }
  }
}

function readWorkerJumpDiagnosticsSnapshot() {
  return uni.getStorageSync(WORKER_JUMP_DIAGNOSTICS_KEY) || {}
}

function writeWorkerJumpDiagnosticsSnapshot(snapshot) {
  uni.setStorageSync(WORKER_JUMP_DIAGNOSTICS_KEY, {
    ...readWorkerJumpDiagnosticsSnapshot(),
    ...snapshot
  })
}

function normalizeQueryValue(query) {
  if (typeof query === 'string') {
    const parsed = parseJsonSafe(query)
    return parsed || query
  }
  return query
}

export function normalizeWorkerJumpTarget(rawTarget) {
  if (!rawTarget) {
    return null
  }
  if (typeof rawTarget === 'string') {
    if (rawTarget.startsWith('/pages/')) {
      return { path: rawTarget }
    }
    const parsed = parseJsonSafe(rawTarget)
    return parsed ? normalizeWorkerJumpTarget(parsed) : null
  }
  if (Array.isArray(rawTarget)) {
    return null
  }

  const nestedPayload = rawTarget.payload || rawTarget.data
  if (nestedPayload && nestedPayload !== rawTarget) {
    const nestedTarget = normalizeWorkerJumpTarget(nestedPayload)
    if (nestedTarget) {
      return nestedTarget
    }
  }

  const path = rawTarget.jumpPath || rawTarget.path || rawTarget.url || rawTarget.routePath || rawTarget.pagePath
  if (typeof path === 'string' && path.startsWith('/pages/')) {
    return {
      path,
      query: normalizeQueryValue(rawTarget.jumpQuery || rawTarget.query || rawTarget.params),
      actionLabel: rawTarget.actionLabel || rawTarget.actionText || rawTarget.jumpLabel || rawTarget.label || '',
      sourceLabel: rawTarget.sourceLabel || rawTarget.sourceText || '',
      savedAt: rawTarget.savedAt || rawTarget.pendingSavedAt || ''
    }
  }

  return resolveNoticeTarget(rawTarget)
}

export function savePendingWorkerJumpTarget(target) {
  const normalizedTarget = normalizeWorkerJumpTarget(target)
  if (!normalizedTarget) {
    writeWorkerJumpDiagnosticsSnapshot({
      lastSavedAt: new Date().toLocaleString(),
      lastSavedResult: 'invalid_target',
      lastSavedMessage: '未解析到有效落页，未写入待跳转目标'
    })
    return false
  }
  const pendingSavedAt = new Date().toLocaleString()
  uni.setStorageSync(PENDING_WORKER_JUMP_KEY, {
    ...normalizedTarget,
    pendingSavedAt
  })
  writeWorkerJumpDiagnosticsSnapshot({
    lastSavedAt: pendingSavedAt,
    lastSavedResult: 'saved',
    lastSavedMessage: '待跳转目标已写入本地缓存',
    lastPendingPath: normalizedTarget.path || '',
    lastPendingQuery: normalizedTarget.query || null,
    lastPendingSourceLabel: normalizedTarget.sourceLabel || '',
    lastPendingActionLabel: normalizedTarget.actionLabel || ''
  })
  return true
}

export function readPendingWorkerJumpTarget() {
  return normalizeWorkerJumpTarget(uni.getStorageSync(PENDING_WORKER_JUMP_KEY))
}

export function clearPendingWorkerJumpTarget() {
  uni.removeStorageSync(PENDING_WORKER_JUMP_KEY)
  writeWorkerJumpDiagnosticsSnapshot({
    lastClearedAt: new Date().toLocaleString(),
    lastPendingPath: '',
    lastPendingQuery: null,
    lastPendingSourceLabel: '',
    lastPendingActionLabel: ''
  })
}

export function openWorkerJumpTarget(target) {
  const normalizedTarget = normalizeWorkerJumpTarget(target)
  if (!normalizedTarget?.path) {
    writeWorkerJumpDiagnosticsSnapshot({
      lastOpenedAt: new Date().toLocaleString(),
      lastOpenedResult: 'missing_path',
      lastOpenedMessage: '未解析到可打开的落页路径'
    })
    return false
  }
  openPageTarget(normalizedTarget.path, normalizedTarget.query)
  writeWorkerJumpDiagnosticsSnapshot({
    lastOpenedAt: new Date().toLocaleString(),
    lastOpenedResult: 'opened',
    lastOpenedMessage: '已执行本地落页跳转',
    lastOpenedPath: normalizedTarget.path,
    lastOpenedQuery: normalizedTarget.query || null,
    lastOpenedSourceLabel: normalizedTarget.sourceLabel || '',
    lastOpenedActionLabel: normalizedTarget.actionLabel || ''
  })
  return true
}

export function consumePendingWorkerJumpTarget() {
  if (!getToken()) {
    writeWorkerJumpDiagnosticsSnapshot({
      lastConsumedAt: new Date().toLocaleString(),
      lastConsumedResult: 'blocked_login',
      lastConsumedMessage: '当前未登录，待跳转目标继续保留'
    })
    return false
  }
  const target = readPendingWorkerJumpTarget()
  if (!target) {
    writeWorkerJumpDiagnosticsSnapshot({
      lastConsumedAt: new Date().toLocaleString(),
      lastConsumedResult: 'empty',
      lastConsumedMessage: '当前没有待消费落页'
    })
    return false
  }
  const opened = openWorkerJumpTarget(target)
  writeWorkerJumpDiagnosticsSnapshot({
    lastConsumedAt: new Date().toLocaleString(),
    lastConsumedResult: opened ? 'opened' : 'open_failed',
    lastConsumedMessage: opened ? '待跳转目标已消费完成' : '待跳转目标打开失败，已继续保留',
    lastConsumedPath: target.path || '',
    lastConsumedQuery: target.query || null,
    lastConsumedSourceLabel: target.sourceLabel || '',
    lastConsumedActionLabel: target.actionLabel || ''
  })
  if (opened) {
    clearPendingWorkerJumpTarget()
  }
  return opened
}

export function getWorkerJumpDiagnostics() {
  const snapshot = readWorkerJumpDiagnosticsSnapshot()
  return {
    lastSavedAt: snapshot.lastSavedAt || '',
    lastSavedResult: snapshot.lastSavedResult || 'not_saved',
    lastSavedMessage: snapshot.lastSavedMessage || '',
    lastPendingPath: snapshot.lastPendingPath || '',
    lastPendingQuery: snapshot.lastPendingQuery || null,
    lastPendingSourceLabel: snapshot.lastPendingSourceLabel || '',
    lastPendingActionLabel: snapshot.lastPendingActionLabel || '',
    lastConsumedAt: snapshot.lastConsumedAt || '',
    lastConsumedResult: snapshot.lastConsumedResult || 'not_consumed',
    lastConsumedMessage: snapshot.lastConsumedMessage || '',
    lastConsumedPath: snapshot.lastConsumedPath || '',
    lastConsumedQuery: snapshot.lastConsumedQuery || null,
    lastConsumedSourceLabel: snapshot.lastConsumedSourceLabel || '',
    lastConsumedActionLabel: snapshot.lastConsumedActionLabel || '',
    lastOpenedAt: snapshot.lastOpenedAt || '',
    lastOpenedResult: snapshot.lastOpenedResult || 'not_opened',
    lastOpenedMessage: snapshot.lastOpenedMessage || '',
    lastOpenedPath: snapshot.lastOpenedPath || '',
    lastOpenedQuery: snapshot.lastOpenedQuery || null,
    lastOpenedSourceLabel: snapshot.lastOpenedSourceLabel || '',
    lastOpenedActionLabel: snapshot.lastOpenedActionLabel || '',
    lastClearedAt: snapshot.lastClearedAt || ''
  }
}

export function clearWorkerJumpDiagnostics() {
  uni.removeStorageSync(WORKER_JUMP_DIAGNOSTICS_KEY)
}
