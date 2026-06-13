import {
  getBaseUrl,
  getBaseUrlSource,
  getBuildMode,
  getWorkerLoginAccount,
  getWorkerProfileExportSnapshot,
  WORKER_API_BASE_URL_STORAGE_KEY
} from './request'

const WORKER_PUSH_EVENT_KEY = 'worker_push_event_history'
const WORKER_PUSH_DIAGNOSTICS_KEY = 'worker_push_diagnostics'
const WORKER_PENDING_JUMP_KEY = 'worker_pending_jump_target'
const WORKER_JUMP_DIAGNOSTICS_KEY = 'worker_jump_diagnostics'
const WORKER_CAMERA_DIAGNOSTICS_KEY = 'worker_camera_diagnostics'
const WORKER_COMPLAINT_DIAGNOSTICS_KEY = 'worker_complaint_diagnostics'
const WORKER_ATTENDANCE_DIAGNOSTICS_KEY = 'worker_attendance_diagnostics'
const WORKER_ATTENDANCE_QUEUE_KEY = 'worker_attendance_offline_queue'
const WORKER_JOB_MAP_DIAGNOSTICS_KEY = 'worker_job_map_diagnostics'
const WORKER_API_CONNECTIVITY_SNAPSHOT_KEY = 'worker_api_connectivity_snapshot'
const WORKER_ACCEPTANCE_OPERATOR_KEY = 'worker_acceptance_operator'
const ACCEPTANCE_OVERVIEW_KEYS = [
  WORKER_PUSH_EVENT_KEY,
  WORKER_PUSH_DIAGNOSTICS_KEY,
  WORKER_PENDING_JUMP_KEY,
  WORKER_JUMP_DIAGNOSTICS_KEY,
  WORKER_CAMERA_DIAGNOSTICS_KEY,
  WORKER_COMPLAINT_DIAGNOSTICS_KEY,
  WORKER_ATTENDANCE_DIAGNOSTICS_KEY,
  WORKER_ATTENDANCE_QUEUE_KEY,
  WORKER_JOB_MAP_DIAGNOSTICS_KEY,
  WORKER_API_CONNECTIVITY_SNAPSHOT_KEY
]

const CAMERA_CATEGORY_LABELS = {
  0: '投诉证据',
  1: '工伤证据',
  2: '隐患留痕'
}

function readStorage(key, fallback) {
  const value = uni.getStorageSync(key)
  if (value === '' || value === undefined || value === null) {
    return fallback
  }
  return value
}

function normalizeSourceLabel(source) {
  if (source === 'runtime') {
    return '设置页运行时地址'
  }
  if (source === 'env') {
    return '构建环境变量'
  }
  return '默认地址'
}

function formatBuildModeLabel(mode) {
  const normalized = String(mode || '').trim().toLowerCase()
  return normalized || 'production'
}

function formatPlatformLabel(platform) {
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

function formatApiStatus(snapshot) {
  if (snapshot?.apiConnectivityLastTestSuccess === true) {
    return `PASS${snapshot.apiConnectivityLastStatusCode ? ` (${snapshot.apiConnectivityLastStatusCode})` : ''}`
  }
  if (snapshot?.apiConnectivityLastTestSuccess === false) {
    return `FAIL${snapshot.apiConnectivityLastStatusCode ? ` (${snapshot.apiConnectivityLastStatusCode})` : ''}`
  }
  return 'NOT_TESTED'
}

function formatPushStatus(snapshot) {
  if (snapshot?.lastResult === 'success') {
    return '登记成功'
  }
  if (snapshot?.lastResult === 'failed') {
    return '登记失败'
  }
  if (snapshot?.lastResult === 'missing_client_id') {
    return '缺少 ClientId'
  }
  if (snapshot?.lastResult === 'skipped') {
    return '已跳过'
  }
  return '未登记'
}

function formatJumpConsumeStatus(snapshot) {
  if (snapshot?.lastConsumedResult === 'opened') {
    return '已消费'
  }
  if (snapshot?.lastConsumedResult === 'blocked_login') {
    return '待登录消费'
  }
  if (snapshot?.lastConsumedResult === 'open_failed') {
    return '消费失败'
  }
  if (snapshot?.lastConsumedResult === 'empty') {
    return '无待消费目标'
  }
  return '未消费'
}

function formatCameraCategory(snapshot) {
  const index = Number(snapshot?.selectedCategoryIndex ?? 0)
  return CAMERA_CATEGORY_LABELS[index] || CAMERA_CATEGORY_LABELS[0]
}

function readSystemInfo() {
  try {
    return uni.getSystemInfoSync() || {}
  } catch (error) {
    return {}
  }
}

function resolveVerificationTerminal(systemInfo = {}) {
  const platform = systemInfo?.uniPlatform || systemInfo?.platform || ''
  return formatPlatformLabel(platform)
}

function resolveDeviceContainer(systemInfo = {}) {
  const brand = String(systemInfo?.brand || '').trim()
  const model = String(systemInfo?.model || '').trim()
  const deviceText = [brand, model].filter(Boolean).join(' ')
  if (deviceText) {
    return deviceText
  }
  if (systemInfo?.deviceType) {
    return String(systemInfo.deviceType)
  }
  const platform = resolveVerificationTerminal(systemInfo)
  return platform || '未知容器'
}

function resolveSystemVersion(systemInfo = {}) {
  const system = String(systemInfo?.system || '').trim()
  if (system) {
    return system
  }
  const parts = [systemInfo?.osName, systemInfo?.osVersion].filter(Boolean)
  if (parts.length) {
    return parts.join(' ')
  }
  return '未知系统'
}

function readProfileExportSnapshot() {
  const snapshot = getWorkerProfileExportSnapshot()
  return snapshot && typeof snapshot === 'object' ? snapshot : {}
}

export function getWorkerAcceptanceOverview() {
  const systemInfo = readSystemInfo()
  const profileSnapshot = readProfileExportSnapshot()
  const apiSnapshot = readStorage(WORKER_API_CONNECTIVITY_SNAPSHOT_KEY, {}) || {}
  const pushSnapshot = readStorage(WORKER_PUSH_DIAGNOSTICS_KEY, {}) || {}
  const pendingJump = readStorage(WORKER_PENDING_JUMP_KEY, {}) || {}
  const jumpSnapshot = readStorage(WORKER_JUMP_DIAGNOSTICS_KEY, {}) || {}
  const pushEvents = readStorage(WORKER_PUSH_EVENT_KEY, []) || []
  const cameraSnapshot = readStorage(WORKER_CAMERA_DIAGNOSTICS_KEY, {}) || {}
  const complaintSnapshot = readStorage(WORKER_COMPLAINT_DIAGNOSTICS_KEY, {}) || {}
  const attendanceSnapshot = readStorage(WORKER_ATTENDANCE_DIAGNOSTICS_KEY, {}) || {}
  const offlineQueue = readStorage(WORKER_ATTENDANCE_QUEUE_KEY, []) || []
  const mapSnapshot = readStorage(WORKER_JOB_MAP_DIAGNOSTICS_KEY, {}) || {}
  const runtimeApiBaseUrl = readStorage(WORKER_API_BASE_URL_STORAGE_KEY, '') || ''
  const buildMode = formatBuildModeLabel(getBuildMode())

  const latestPushEvent = Array.isArray(pushEvents) && pushEvents.length ? pushEvents[0] : null

  return {
    execution: {
      verificationTerminal: resolveVerificationTerminal(systemInfo),
      deviceContainer: resolveDeviceContainer(systemInfo),
      systemVersion: resolveSystemVersion(systemInfo),
      buildMode,
      apiBaseUrl: getBaseUrl(),
      apiSource: normalizeSourceLabel(getBaseUrlSource()),
      acceptanceOperator: readStorage(WORKER_ACCEPTANCE_OPERATOR_KEY, '') || '',
      loginAccount: getWorkerLoginAccount() || '',
      workerName: profileSnapshot.personNameMasked || profileSnapshot.personName || '',
      enterpriseName: profileSnapshot.enterpriseName || ''
    },
    api: {
      baseUrl: getBaseUrl(),
      buildMode,
      source: normalizeSourceLabel(getBaseUrlSource()),
      runtimeBaseUrl: runtimeApiBaseUrl || '',
      lastTestAt: apiSnapshot.apiConnectivityLastTestAt || '',
      lastTestUrl: apiSnapshot.apiConnectivityLastTestUrl || '',
      lastStatus: formatApiStatus(apiSnapshot),
      message: apiSnapshot.apiConnectivityLastMessage || apiSnapshot.apiConnectivityStatusText || ''
    },
    push: {
      lastAttemptAt: pushSnapshot.lastAttemptAt || '',
      lastStatus: formatPushStatus(pushSnapshot),
      lastMessage: pushSnapshot.lastMessage || '',
      clientId: pushSnapshot.pushClientId || '',
      platform: pushSnapshot.pushPlatform || '',
      permission: pushSnapshot.notificationPermission || '',
      pendingTargetPath: pendingJump.path || '',
      pendingTargetSavedAt: pendingJump.pendingSavedAt || pendingJump.savedAt || '',
      jumpLastSavedAt: jumpSnapshot.lastSavedAt || '',
      jumpLastConsumeAt: jumpSnapshot.lastConsumedAt || '',
      jumpLastConsumeStatus: formatJumpConsumeStatus(jumpSnapshot),
      jumpLastOpenAt: jumpSnapshot.lastOpenedAt || '',
      jumpLastOpenResult: jumpSnapshot.lastOpenedResult || '',
      jumpLastMessage: jumpSnapshot.lastConsumedMessage || jumpSnapshot.lastOpenedMessage || jumpSnapshot.lastSavedMessage || '',
      recentEventCount: Array.isArray(pushEvents) ? pushEvents.length : 0,
      latestEventTitle: latestPushEvent?.title || '',
      latestEventAt: latestPushEvent?.occurredAt || '',
      latestEventTargetPath: latestPushEvent?.targetPath || ''
    },
    camera: {
      platform: formatPlatformLabel(cameraSnapshot.runtimePlatform || ''),
      categoryLabel: formatCameraCategory(cameraSnapshot),
      lastActionAt: cameraSnapshot.lastActionAt || '',
      lastPickAt: cameraSnapshot.lastPickAt || '',
      lastPickSource: cameraSnapshot.lastPickSource || '',
      pickPermissionStatus: cameraSnapshot.lastPickPermissionStatus || '',
      pickPermissionLabel: formatPermissionStatus(cameraSnapshot.lastPickPermissionStatus || ''),
      pickErrorCode: cameraSnapshot.lastPickErrorCode || '',
      lastPickMessage: cameraSnapshot.lastPickMessage || '',
      lastUploadAt: cameraSnapshot.lastUploadAt || '',
      lastUploadStatus: cameraSnapshot.lastUploadStatus || '',
      lastUploadResponseCode: Number(cameraSnapshot.lastUploadResponseCode || 0),
      lastUploadResponseSummary: cameraSnapshot.lastUploadResponseSummary || '',
      lastUploadMessage: cameraSnapshot.lastUploadMessage || '',
      lastRecordLoadAt: cameraSnapshot.lastRecordLoadAt || '',
      lastRecordLoadStatus: cameraSnapshot.lastRecordLoadStatus || '',
      lastRecordLoadMessage: cameraSnapshot.lastRecordLoadMessage || '',
      lastRecordCount: Number(cameraSnapshot.lastRecordCount || 0),
      lastComplaintForwardAt: cameraSnapshot.lastComplaintForwardAt || '',
      lastComplaintForwardUrl: cameraSnapshot.lastComplaintForwardUrl || '',
      latestUrl: cameraSnapshot.uploadResult?.url || ''
    },
    complaint: {
      lastLoadedAt: complaintSnapshot.complaintLastLoadedAt || '',
      lastSubmittedAt: complaintSnapshot.complaintLastSubmittedAt || '',
      lastAttachmentAt: complaintSnapshot.complaintLastAttachmentAt || '',
      lastActionAt: complaintSnapshot.complaintLastActionAt || '',
      lastMessage: complaintSnapshot.complaintLastMessage || '',
      complaintType: complaintSnapshot.complaintType || '',
      attachmentCount: String(complaintSnapshot.attachments || '')
        .split(',')
        .map((item) => item.trim())
        .filter(Boolean).length,
      rowCount: Number(complaintSnapshot.rowCount || 0),
      latestTitle: complaintSnapshot.latestComplaint?.title || '',
      latestStatus: complaintSnapshot.latestComplaint?.statusText || '',
      latestSyncUnionText: complaintSnapshot.latestComplaint?.syncUnionText || ''
    },
    attendance: {
      platform: formatPlatformLabel(attendanceSnapshot.runtimePlatform || ''),
      networkConnected: attendanceSnapshot.networkConnected !== false,
      lastLocationAt: attendanceSnapshot.locationLastAt || '',
      lastLocationSuccess: attendanceSnapshot.locationLastSuccess,
      locationPermissionStatus: attendanceSnapshot.locationPermissionStatus || '',
      locationPermissionLabel: formatPermissionStatus(attendanceSnapshot.locationPermissionStatus || ''),
      locationErrorCode: attendanceSnapshot.locationErrorCode || '',
      lastLocationCoords: attendanceSnapshot.locationCoordsText || '',
      lastLocationMessage: attendanceSnapshot.locationErrorText || '',
      lastActionAt: attendanceSnapshot.lastActionAt || '',
      lastActionType: attendanceSnapshot.lastActionType || '',
      lastActionResult: attendanceSnapshot.lastActionResult || '',
      lastActionMode: attendanceSnapshot.lastActionMode || '',
      lastActionMessage: attendanceSnapshot.lastActionMessage || '',
      lastSyncAt: attendanceSnapshot.lastSyncAt || '',
      lastSyncStatus: attendanceSnapshot.lastSyncStatus || '',
      lastSyncMessage: attendanceSnapshot.lastSyncMessage || '',
      lastSyncTrigger: attendanceSnapshot.lastSyncTrigger || '',
      lastSyncProcessedCount: Number(attendanceSnapshot.lastSyncProcessedCount || 0),
      lastSyncRemainingCount: Number(attendanceSnapshot.lastSyncRemainingCount || 0),
      lastSyncFailedItem: attendanceSnapshot.lastSyncFailedItem || '',
      syncHistoryCount: Array.isArray(attendanceSnapshot.syncHistory) ? attendanceSnapshot.syncHistory.length : 0,
      offlineQueueCount: Array.isArray(offlineQueue) ? offlineQueue.length : 0,
      oldestQueueItem: Array.isArray(offlineQueue) && offlineQueue.length ? offlineQueue[0] : null,
      latestQueueItem: Array.isArray(offlineQueue) && offlineQueue.length ? offlineQueue[offlineQueue.length - 1] : null
    },
    map: {
      platform: formatPlatformLabel(mapSnapshot.runtimePlatform || ''),
      lastLocationAt: mapSnapshot.locationLastAt || '',
      lastLocationSuccess: mapSnapshot.locationSuccess,
      locationPermissionStatus: mapSnapshot.locationPermissionStatus || '',
      locationPermissionLabel: formatPermissionStatus(mapSnapshot.locationPermissionStatus || ''),
      locationErrorCode: mapSnapshot.locationErrorCode || '',
      lastLocationMessage: mapSnapshot.locationMessage || '',
      lastMapConfigAt: mapSnapshot.lastMapConfigAt || '',
      lastMapConfigStatus: mapSnapshot.lastMapConfigStatus || '',
      lastMapConfigCenter: mapSnapshot.lastMapConfigCenter || '',
      lastMapConfigMessage: mapSnapshot.lastMapConfigMessage || '',
      lastNearbyLoadAt: mapSnapshot.lastNearbyLoadAt || '',
      lastNearbyStatus: mapSnapshot.lastNearbyStatus || '',
      lastNearbyMarkerCount: Number(mapSnapshot.lastNearbyMarkerCount || 0),
      lastNearbyJobCount: Number(mapSnapshot.lastNearbyJobCount || 0),
      lastNearbyRadiusKm: Number(mapSnapshot.lastNearbyRadiusKm || 0),
      lastNearbyKeyword: mapSnapshot.lastNearbyKeyword || '',
      lastSelectedJobType: mapSnapshot.lastSelectedJobType || '',
      lastSelectedSalaryLabel: mapSnapshot.lastSelectedSalaryLabel || '',
      lastNearbyMessage: mapSnapshot.lastNearbyMessage || '',
      lastApiMessage: mapSnapshot.lastApiMessage || ''
    }
  }
}

export function buildWorkerAcceptanceOverviewText(overview = getWorkerAcceptanceOverview()) {
  return [
    '## 劳动者端真机联调总览',
    `- 联调人：${overview.execution.acceptanceOperator || '-'}`,
    `- 验证端：${overview.execution.verificationTerminal || '-'}`,
    `- 设备/容器：${overview.execution.deviceContainer || '-'}`,
    `- 系统版本：${overview.execution.systemVersion || '-'}`,
    `- 构建模式：${overview.execution.buildMode || '-'}`,
    `- 当前账号：${overview.execution.loginAccount || '-'}`,
    `- 劳动者/企业：${overview.execution.workerName || '-'} / ${overview.execution.enterpriseName || '-'}`,
    `- 接口环境：${overview.api.baseUrl} / ${overview.api.source} / mode=${overview.api.buildMode || '-'}`,
    `- API 检测：${overview.api.lastStatus}${overview.api.lastTestAt ? ` / ${overview.api.lastTestAt}` : ''}`,
    `- 推送登记：${overview.push.lastStatus}${overview.push.lastAttemptAt ? ` / ${overview.push.lastAttemptAt}` : ''}`,
    `- 推送待消费落页：${overview.push.pendingTargetPath || '-'}`,
    `- 推送消费状态：${overview.push.jumpLastConsumeStatus}${overview.push.jumpLastConsumeAt ? ` / ${overview.push.jumpLastConsumeAt}` : ''}`,
    `- 推送最近事件：${overview.push.latestEventTitle || '-'}${overview.push.latestEventAt ? ` / ${overview.push.latestEventAt}` : ''}`,
    `- 拍照权限：${overview.camera.platform} / ${overview.camera.pickPermissionLabel}`,
    `- 拍照上传：${overview.camera.lastUploadStatus || '未上传'}${overview.camera.lastUploadAt ? ` / ${overview.camera.lastUploadAt}` : ''}`,
    `- 拍照记录回查：${overview.camera.lastRecordCount} 条${overview.camera.lastRecordLoadAt ? ` / ${overview.camera.lastRecordLoadAt}` : ''}`,
    `- 投诉承接：附件 ${overview.complaint.attachmentCount} 项 / 记录 ${overview.complaint.rowCount} 条${overview.complaint.lastSubmittedAt ? ` / ${overview.complaint.lastSubmittedAt}` : ''}`,
    `- 打卡动作：${overview.attendance.lastActionType || '-'} / ${overview.attendance.lastActionResult || '-'} / ${overview.attendance.lastActionMode || '-'}`,
    `- 打卡权限：${overview.attendance.platform} / ${overview.attendance.locationPermissionLabel}`,
    `- 离线补传：${overview.attendance.lastSyncStatus || '暂无'} / 队列 ${overview.attendance.offlineQueueCount} 条${overview.attendance.lastSyncTrigger ? ` / ${overview.attendance.lastSyncTrigger === 'manual' ? '手动' : '自动'}` : ''}`,
    `- 地图定位：${overview.map.lastLocationSuccess === true ? '定位成功' : overview.map.lastLocationSuccess === false ? '定位降级' : '未获取'}${overview.map.lastLocationAt ? ` / ${overview.map.lastLocationAt}` : ''} / ${overview.map.locationPermissionLabel}`,
    `- 地图接口：map-config ${overview.map.lastMapConfigAt || '-'} / nearby ${overview.map.lastNearbyLoadAt || '-'}`,
    `- 说明：${overview.api.message || overview.push.lastMessage || overview.camera.lastUploadMessage || overview.attendance.lastSyncMessage || overview.map.lastApiMessage || '-'}`
  ].join('\n')
}

export function buildWorkerAcceptanceDetailedDraftText(overview = getWorkerAcceptanceOverview()) {
  const locationStatus =
    overview.map.lastLocationSuccess === true ? '定位成功' : overview.map.lastLocationSuccess === false ? '定位降级' : '未获取'
  return [
    '## 劳动者端真机联调验收草稿',
    '',
    '### 1. 执行上下文',
    `- 联调人：${overview.execution.acceptanceOperator || '-'}`,
    `- 验证端：${overview.execution.verificationTerminal || '-'}`,
    `- 设备/运行容器：${overview.execution.deviceContainer || '-'}`,
    `- 系统版本：${overview.execution.systemVersion || '-'}`,
    `- 构建模式：${overview.execution.buildMode || '-'}`,
    `- 当前账号：${overview.execution.loginAccount || '-'}`,
    `- 当前劳动者：${overview.execution.workerName || '-'}`,
    `- 当前企业：${overview.execution.enterpriseName || '-'}`,
    `- 当前接口环境：${overview.execution.apiBaseUrl || '-'} / ${overview.execution.apiSource || '-'}`,
    '',
    '### 2. 接口环境',
    `- 生效地址：${overview.api.baseUrl}`,
    `- 构建模式：${overview.api.buildMode || '-'}`,
    `- 地址来源：${overview.api.source}`,
    `- 最近检测：${overview.api.lastTestAt || '-'}`,
    `- 检测结果：${overview.api.lastStatus}`,
    `- 检测地址：${overview.api.lastTestUrl || '-'}`,
    `- 检测说明：${overview.api.message || '-'}`,
    '',
    '### 3. 推送链路',
    `- 本地登记：${overview.push.lastStatus}`,
    `- 最近登记：${overview.push.lastAttemptAt || '-'}`,
    `- ClientId：${overview.push.clientId || '-'}`,
    `- 平台 / 权限：${overview.push.platform || '-'} / ${overview.push.permission || '-'}`,
    `- 待消费落页：${overview.push.pendingTargetPath || '-'}`,
    `- 待跳转最近写入：${overview.push.jumpLastSavedAt || '-'}`,
    `- 待跳转最近消费：${overview.push.jumpLastConsumeStatus} / ${overview.push.jumpLastConsumeAt || '-'}`,
    `- 待跳转最近打开：${overview.push.jumpLastOpenAt || '-'}`,
    `- 最近事件：${overview.push.latestEventTitle || '-'} / ${overview.push.latestEventAt || '-'}`,
    `- 最近目标：${overview.push.latestEventTargetPath || '-'}`,
    `- 推送说明：${overview.push.lastMessage || overview.push.jumpLastMessage || '-'}`,
    '',
    '### 4. 拍照上传',
    `- 运行平台：${overview.camera.platform}`,
    `- 当前分类：${overview.camera.categoryLabel}`,
    `- 最近取图：${overview.camera.lastPickSource || '-'} / ${overview.camera.lastPickAt || '-'}`,
    `- 权限判定：${overview.camera.pickPermissionLabel}${overview.camera.pickErrorCode ? ` / ${overview.camera.pickErrorCode}` : ''}`,
    `- 最近上传：${overview.camera.lastUploadStatus || '未上传'} / ${overview.camera.lastUploadAt || '-'}`,
    `- 上传响应：${overview.camera.lastUploadResponseSummary || '-'}`,
    `- 记录回查：${overview.camera.lastRecordCount} 条 / ${overview.camera.lastRecordLoadAt || '-'}`,
    `- 回查结果：${overview.camera.lastRecordLoadStatus || '-'}`,
    `- 投诉带入：${overview.camera.lastComplaintForwardAt || '-'} / ${overview.camera.lastComplaintForwardUrl || '-'}`,
    `- 最新文件：${overview.camera.latestUrl || '-'}`,
    `- 上传说明：${overview.camera.lastUploadMessage || overview.camera.lastRecordLoadMessage || overview.camera.lastPickMessage || '-'}`,
    '',
    '### 5. 投诉承接',
    `- 最近联动：${overview.complaint.lastActionAt || '-'}`,
    `- 最近加载：${overview.complaint.lastLoadedAt || '-'}`,
    `- 最近传证据：${overview.complaint.lastAttachmentAt || '-'}`,
    `- 最近提交：${overview.complaint.lastSubmittedAt || '-'}`,
    `- 当前类型：${overview.complaint.complaintType || '-'}`,
    `- 当前附件：${overview.complaint.attachmentCount} 项`,
    `- 当前记录：${overview.complaint.rowCount} 条`,
    `- 最近一条：${overview.complaint.latestTitle || '-'} / ${overview.complaint.latestStatus || '-'} / ${overview.complaint.latestSyncUnionText || '-'}`,
    `- 投诉说明：${overview.complaint.lastMessage || '-'}`,
    '',
    '### 6. 考勤打卡',
    `- 运行平台：${overview.attendance.platform}`,
    `- 网络状态：${overview.attendance.networkConnected ? '在线' : '离线'}`,
    `- 最近定位：${overview.attendance.lastLocationAt || '-'}`,
    `- 定位权限：${overview.attendance.locationPermissionLabel}${overview.attendance.locationErrorCode ? ` / ${overview.attendance.locationErrorCode}` : ''}`,
    `- 最近坐标：${overview.attendance.lastLocationCoords || '-'}`,
    `- 最近动作：${overview.attendance.lastActionType || '-'} / ${overview.attendance.lastActionResult || '-'} / ${overview.attendance.lastActionMode || '-'}`,
    `- 最近动作时间：${overview.attendance.lastActionAt || '-'}`,
    `- 队列生命周期：首条 ${overview.attendance.oldestQueueItem?.createdAt || overview.attendance.oldestQueueItem?.timeText || '-'} / 末条 ${overview.attendance.latestQueueItem?.createdAt || overview.attendance.latestQueueItem?.timeText || '-'}`,
    `- 离线补传：${overview.attendance.lastSyncStatus || '暂无'} / ${overview.attendance.lastSyncAt || '-'}${overview.attendance.lastSyncTrigger ? ` / ${overview.attendance.lastSyncTrigger === 'manual' ? '手动触发' : '自动触发'}` : ''}`,
    `- 补传计数：已补 ${overview.attendance.lastSyncProcessedCount} / 剩余 ${overview.attendance.lastSyncRemainingCount}`,
    `- 队列数量：${overview.attendance.offlineQueueCount} 条`,
    `- 最近失败项：${overview.attendance.lastSyncFailedItem || '-'}`,
    `- 打卡说明：${overview.attendance.lastActionMessage || overview.attendance.lastSyncMessage || overview.attendance.lastLocationMessage || '-'}`,
    '',
    '### 7. 地图定位',
    `- 运行平台：${overview.map.platform}`,
    `- 定位状态：${locationStatus}`,
    `- 权限判定：${overview.map.locationPermissionLabel}${overview.map.locationErrorCode ? ` / ${overview.map.locationErrorCode}` : ''}`,
    `- 最近定位时间：${overview.map.lastLocationAt || '-'}`,
    `- map-config：${overview.map.lastMapConfigStatus || '-'} / ${overview.map.lastMapConfigAt || '-'} / ${overview.map.lastMapConfigCenter || '-'}`,
    `- nearby：${overview.map.lastNearbyStatus || '-'} / ${overview.map.lastNearbyLoadAt || '-'} / marker ${overview.map.lastNearbyMarkerCount} / 列表 ${overview.map.lastNearbyJobCount}`,
    `- 当前筛选：关键词 ${overview.map.lastNearbyKeyword || '-'} / 工种 ${overview.map.lastSelectedJobType || '全部'} / 薪资 ${overview.map.lastSelectedSalaryLabel || '不限薪资'} / 半径 ${overview.map.lastNearbyRadiusKm || '-'}km`,
    `- 地图说明：${overview.map.lastApiMessage || overview.map.lastNearbyMessage || overview.map.lastMapConfigMessage || overview.map.lastLocationMessage || '-'}`,
    '',
    '### 8. 结论',
    '- 当前代码侧联调快照已可本地留痕，剩余重点为真机送达、真实接口返回、权限弹窗和业务数据一致性验收。'
  ].join('\n')
}

export function clearWorkerAcceptanceDiagnostics() {
  ACCEPTANCE_OVERVIEW_KEYS.forEach((key) => {
    uni.removeStorageSync(key)
  })
}
