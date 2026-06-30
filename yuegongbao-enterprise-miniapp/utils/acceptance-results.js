import {
  getBaseUrl,
  getBaseUrlSource,
  getBuildMode,
  getWorkerLoginAccount,
  getWorkerProfileExportSnapshot
} from './request'
import { getWorkerAcceptanceOverview } from './acceptance-overview'

const WORKER_ACCEPTANCE_RESULTS_KEY = 'worker_acceptance_results'
const WORKER_ACCEPTANCE_ISSUES_KEY = 'worker_acceptance_issues'
const WORKER_ACCEPTANCE_PHASES_KEY = 'worker_acceptance_phases'
const WORKER_ACCEPTANCE_APIS_KEY = 'worker_acceptance_apis'
const WORKER_ACCEPTANCE_PREREQUISITES_KEY = 'worker_acceptance_prerequisites'
const WORKER_ACCEPTANCE_EVIDENCE_KEY = 'worker_acceptance_evidence_items'
const WORKER_ACCEPTANCE_OPERATOR_KEY = 'worker_acceptance_operator'
const DEFAULT_ACCEPTANCE_ISSUE_COUNT = 3

const DEFAULT_ACCEPTANCE_ITEMS = [
  { key: 'environment', label: '接口环境', pagePath: '/pages/profile/settings' },
  { key: 'push', label: '推送链', pagePath: '/pages/profile/settings' },
  { key: 'camera', label: '拍照上传', pagePath: '/pages/camera/index' },
  { key: 'map', label: '地图定位', pagePath: '/pages/job/map' },
  { key: 'attendance', label: '打卡补传', pagePath: '/pages/attendance/checkin' },
  { key: 'home', label: '首页主链', pagePath: '/pages/home/index' },
  { key: 'trainingSalary', label: '培训工资联动', pagePath: '/pages/training/index' },
  { key: 'jobResume', label: '求职简历联动', pagePath: '/pages/job/list' },
  { key: 'complaintLegal', label: '投诉法律联动', pagePath: '/pages/complaint/index' },
  { key: 'noticeActivityVideo', label: '通知活动视频', pagePath: '/pages/notice/list' },
  { key: 'unionContract', label: '工会合同服务', pagePath: '/pages/union/index' }
]

const STATUS_META = {
  pending: { label: '待验收', sort: 0 },
  passed: { label: '已通过', sort: 1 },
  failed: { label: '失败', sort: 2 },
  blocked: { label: '阻塞', sort: 3 }
}

const TEMPLATE_SUMMARY_SECTIONS = [
  { label: '环境配置', itemKeys: ['environment'] },
  { label: '推送与消息', itemKeys: ['push'] },
  { label: '拍照上传', itemKeys: ['camera'] },
  { label: '地图定位', itemKeys: ['map'] },
  { label: '首页主链', itemKeys: ['home'] },
  { label: '培训与打卡', itemKeys: ['trainingSalary', 'attendance'] },
  { label: '工资社保个税', itemKeys: ['trainingSalary'] },
  { label: '求职与简历合同', itemKeys: ['jobResume', 'unionContract'] },
  { label: '投诉与法律咨询', itemKeys: ['complaintLegal'] },
  { label: '活动通知视频工会', itemKeys: ['noticeActivityVideo', 'unionContract'] }
]

const BUSINESS_ACCEPTANCE_FORMAL_SECTIONS = [
  {
    title: '首页主链与工作台验收',
    resultKeys: ['home'],
    phaseKeys: ['home_spec_check', 'workbench_aggregation_check'],
    apiKeys: ['api_home_workbench']
  },
  {
    title: '培训工资联动验收',
    resultKeys: ['trainingSalary', 'attendance'],
    phaseKeys: ['training_guard_check', 'attendance_offline_sync'],
    apiKeys: ['api_training_attendance_salary']
  },
  {
    title: '求职简历联动验收',
    resultKeys: ['jobResume', 'map'],
    phaseKeys: ['job_resume_check'],
    apiKeys: ['api_job_resume']
  },
  {
    title: '工会合同服务验收',
    resultKeys: ['unionContract'],
    phaseKeys: ['union_contract_check'],
    apiKeys: ['api_union_profile_service']
  },
  {
    title: '通知活动视频验收',
    resultKeys: ['noticeActivityVideo'],
    phaseKeys: ['notice_activity_video_check'],
    apiKeys: ['api_notice_content']
  },
  {
    title: '投诉法律联动验收',
    resultKeys: ['complaintLegal'],
    phaseKeys: ['complaint_legal_check'],
    apiKeys: ['api_complaint_legal']
  },
  {
    title: '社保个税验收',
    resultKeys: [],
    phaseKeys: ['social_tax_check'],
    apiKeys: ['api_training_attendance_salary']
  },
  {
    title: '我的资料服务验收',
    resultKeys: [],
    phaseKeys: ['profile_service_check'],
    apiKeys: ['api_union_profile_service']
  }
]

const DEFAULT_ACCEPTANCE_EVIDENCE_ITEMS = [
  {
    id: 'environment',
    label: '接口环境设置页截图',
    expected: '设置页当前接口地址、地址来源、检测结果'
  },
  {
    id: 'home_workbench',
    label: '首页与工作台截图',
    expected: '首页主链、工作台聚合、九宫格与摘要'
  },
  {
    id: 'training_attendance',
    label: '培训与打卡截图',
    expected: '培训进度、答题/课程、打卡与补传'
  },
  {
    id: 'salary_social_tax',
    label: '工资社保个税截图',
    expected: '工资、社保、个税列表或详情关键页'
  },
  {
    id: 'job_resume',
    label: '求职地图与投递截图',
    expected: '地图 marker、岗位详情、投递记录、简历状态'
  },
  {
    id: 'camera_complaint',
    label: '拍照上传与投诉截图',
    expected: '拍照上传结果、投诉附件带入、投诉摘要'
  },
  {
    id: 'notice_video_union',
    label: '活动视频工会截图',
    expected: '通知、活动、视频、工会与合同关键页'
  },
  {
    id: 'push',
    label: '推送通知栏与落页截图',
    expected: '通知栏消息、点击落页、待跳转消费结果'
  }
]

const DEFAULT_ACCEPTANCE_PHASES = [
  {
    key: 'push_real_device',
    label: '推送真机链路',
    detail: 'ClientId、服务端登记、测试推送、接收留痕、点击落页、未登录落页',
    pagePath: '/pages/profile/settings'
  },
  {
    key: 'camera_upload_real_device',
    label: '拍照上传真机链路',
    detail: '拍照权限、大小限制、上传结构、归档回查、投诉附件透传',
    pagePath: '/pages/camera/index'
  },
  {
    key: 'location_map_real_device',
    label: '定位地图真机链路',
    detail: '定位授权、降级提示、地图中心与 marker、一线打卡经纬度回传',
    pagePath: '/pages/job/map'
  },
  {
    key: 'attendance_offline_sync',
    label: '离线打卡补传链路',
    detail: '断网入队、恢复补传、列表刷新、多条顺序',
    pagePath: '/pages/attendance/checkin'
  },
  {
    key: 'home_spec_check',
    label: '首页说明书口径',
    detail: '首页顺序、九宫格固定项、活动通知推荐视频真实数据',
    pagePath: '/pages/home/index'
  },
  {
    key: 'training_guard_check',
    label: '培训与拦截联动',
    detail: '未满 10 题拦截、培训快照、解锁刷新、前后端同口径',
    pagePath: '/pages/training/index'
  },
  {
    key: 'workbench_aggregation_check',
    label: '工作台聚合验收',
    detail: '培训进度、未读通知、入口分布、锁定入口文案与落页',
    pagePath: '/pages/workbench/index'
  },
  {
    key: 'job_resume_check',
    label: '求职简历联动验收',
    detail: '简历拦截、地图列表详情一致性、投递记录真实数据',
    pagePath: '/pages/job/list'
  },
  {
    key: 'union_contract_check',
    label: '工会合同服务验收',
    detail: '快捷入口、案例通知、集体合同与个人合同双口径',
    pagePath: '/pages/union/index'
  },
  {
    key: 'complaint_legal_check',
    label: '投诉法律咨询验收',
    detail: '投诉、法律咨询、讲座、FAQ 与消息推送联动',
    pagePath: '/pages/complaint/index'
  },
  {
    key: 'notice_activity_video_check',
    label: '通知活动视频验收',
    detail: '通知已读回写、活动参与、视频进度与详情数据一致',
    pagePath: '/pages/notice/list'
  },
  {
    key: 'social_tax_check',
    label: '社保个税验收',
    detail: '年度汇总、状态分布、详情摘要、空态引导与前后一致',
    pagePath: '/pages/social/list'
  },
  {
    key: 'profile_service_check',
    label: '我的资料服务验收',
    detail: '实名认证、帮助中心、保险、积分、隐私协议与动作回写',
    pagePath: '/pages/profile/real-name'
  },
  {
    key: 'api_checklist',
    label: '接口逐项联调清单',
    detail: '/app/worker/home、training、attendance、salary、job、notice、union 等接口逐项打勾',
    pagePath: '/pages/profile/settings'
  }
]

const DEFAULT_ACCEPTANCE_PREREQUISITES = [
  {
    key: 'network_ready',
    label: '真机与网络连通',
    detail: '手机与后端在同网段，接口地址可直连，HTTPS 证书可用或已按测试环境放通',
    pagePath: '/pages/profile/settings'
  },
  {
    key: 'backend_ready',
    label: '后端服务就绪',
    detail: '数据库、Redis、认证接口、上传接口和 app/worker 主链接口可正常访问',
    pagePath: '/pages/profile/settings'
  },
  {
    key: 'app_runtime_ready',
    label: 'App 运行配置就绪',
    detail: '设置页已保存正确 baseUrl，切换环境后已重新登录，pushClientId 已刷新',
    pagePath: '/pages/profile/settings'
  },
  {
    key: 'mp_domain_ready',
    label: '小程序域名配置',
    detail: '微信开发者工具已配置 request 合法域名，或调试阶段已关闭域名校验',
    pagePath: '/pages/profile/settings'
  },
  {
    key: 'env_file_ready',
    label: '.env 环境约定',
    detail: 'test / uat / production 地址约定已明确，正式包构建时不再依赖默认 localhost',
    pagePath: '/pages/profile/settings'
  }
]

const DEFAULT_ACCEPTANCE_API_GROUPS = [
  {
    key: 'api_home_workbench',
    label: '首页与工作台接口',
    detail: '首页、个人信息、工作台聚合与设置明细',
    pagePath: '/pages/home/index',
    endpoints: ['/app/worker/home', '/app/worker/profile', '/app/worker/workbench', '/app/worker/settings/**']
  },
  {
    key: 'api_training_attendance_salary',
    label: '培训考勤工资接口',
    detail: '培训、考勤、工资、社保、个税主链',
    pagePath: '/pages/training/index',
    endpoints: ['/app/worker/training/**', '/app/worker/attendance/**', '/app/worker/salary/**', '/app/worker/social-security/**', '/app/worker/tax/**']
  },
  {
    key: 'api_job_resume',
    label: '求职简历接口',
    detail: '岗位地图、岗位列表详情、投递、简历',
    pagePath: '/pages/job/list',
    endpoints: ['/app/worker/job/**', '/app/worker/resume/**']
  },
  {
    key: 'api_complaint_legal',
    label: '投诉法律接口',
    detail: '投诉举报、法律咨询、讲座、FAQ',
    pagePath: '/pages/complaint/index',
    endpoints: ['/app/worker/complaint/**', '/app/worker/legal-consult/**', '/app/worker/legal-article/**', '/app/worker/legal-faq/**']
  },
  {
    key: 'api_notice_content',
    label: '通知活动视频接口',
    detail: '通知、活动、视频、AI 培训详情',
    pagePath: '/pages/notice/list',
    endpoints: ['/app/worker/notice/**', '/app/worker/activity/**', '/app/worker/video/**', '/app/worker/ai-training/detail']
  },
  {
    key: 'api_union_profile_service',
    label: '工会与我的服务接口',
    detail: '工会、劳动合同、帮助、实名、积分、保险、上传记录',
    pagePath: '/pages/union/index',
    endpoints: ['/app/worker/union-service/**', '/app/worker/labor-contract/**', '/app/worker/help/**', '/app/worker/real-name/**', '/app/worker/points/**', '/app/worker/insurance/security', '/app/worker/upload-record/**']
  },
  {
    key: 'api_structure_risk',
    label: '高风险返回结构接口',
    detail: '首页、工作台、推送测试、地图配置、活动视频、工会首页等返回结构核对',
    pagePath: '/pages/profile/settings',
    endpoints: ['/app/worker/home', '/app/worker/workbench', '/app/worker/settings/detail', '/app/worker/settings/push-test', '/app/worker/job/map-config', '/app/worker/job/nearby', '/app/worker/activity/detail', '/app/worker/video/detail', '/app/worker/union-service/home']
  }
]

const PHASE_STATUS_META = {
  pending: { label: '未开始', sort: 0 },
  in_progress: { label: '进行中', sort: 1 },
  blocked: { label: '阻塞', sort: 2 },
  passed: { label: '已完成', sort: 3 }
}

function readSystemInfo() {
  try {
    return uni.getSystemInfoSync() || {}
  } catch (error) {
    return {}
  }
}

function formatTerminalLabel(platform) {
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
    return 'mp-weixin'
  }
  if (normalized === 'android') {
    return 'Android'
  }
  if (normalized === 'ios') {
    return 'iOS'
  }
  return platform
}

function formatBaseUrlSource(source) {
  if (source === 'runtime') {
    return 'runtime'
  }
  if (source === 'env') {
    return 'env'
  }
  return 'default'
}

function formatBuildMode(mode) {
  const normalized = String(mode || '').trim().toLowerCase()
  return normalized || 'production'
}

function detectBackendEnvironment(baseUrl) {
  const normalized = String(baseUrl || '').toLowerCase()
  if (!normalized) {
    return '未判定'
  }
  if (normalized.includes('127.0.0.1') || normalized.includes('localhost') || normalized.includes('192.168.') || normalized.includes('10.')) {
    return 'dev'
  }
  if (normalized.includes('test')) {
    return 'test'
  }
  if (normalized.includes('uat') || normalized.includes('pre')) {
    return 'uat'
  }
  if (normalized.includes('prod')) {
    return 'prod'
  }
  return '未判定'
}

function readProfileExportSnapshot() {
  const snapshot = getWorkerProfileExportSnapshot()
  return snapshot && typeof snapshot === 'object' ? snapshot : {}
}

function getWorkerAcceptanceBasicInfo() {
  const systemInfo = readSystemInfo()
  const baseUrl = getBaseUrl()
  const profileSnapshot = readProfileExportSnapshot()
  return {
    linkedAt: new Date().toLocaleString(),
    operator: uni.getStorageSync(WORKER_ACCEPTANCE_OPERATOR_KEY) || '-',
    terminal: formatTerminalLabel(systemInfo?.uniPlatform || systemInfo?.platform || ''),
    deviceModel: [systemInfo?.brand, systemInfo?.model].filter(Boolean).join(' ') || systemInfo?.deviceType || '-',
    packageVersion: systemInfo?.appVersion || systemInfo?.version || '-',
    buildMode: formatBuildMode(getBuildMode()),
    backendEnvironment: detectBackendEnvironment(baseUrl),
    baseUrl,
    baseUrlSource: formatBaseUrlSource(getBaseUrlSource()),
    loginAccount: getWorkerLoginAccount() || '-',
    workerName: profileSnapshot.personNameMasked || profileSnapshot.personName || '-',
    enterpriseName: profileSnapshot.enterpriseName || '-'
  }
}

function buildWorkerAcceptanceBasicInfoDraft() {
  const info = getWorkerAcceptanceBasicInfo()
  return [
    '## 2. 基础信息',
    '',
    '| 项目 | 记录 |',
    '| --- | --- |',
    `| 联调时间 | ${info.linkedAt} |`,
    `| 联调人 | ${info.operator} |`,
    `| 平台 | ${info.terminal} |`,
    `| 手机型号/运行容器 | ${info.deviceModel} |`,
    `| 包版本 | ${info.packageVersion} |`,
    `| 构建模式 | ${info.buildMode} |`,
    `| 后端环境 | ${info.backendEnvironment} |`,
    `| 当前接口地址 | ${info.baseUrl} |`,
    `| 地址来源 | ${info.baseUrlSource} |`,
    `| 当前账号 | ${info.loginAccount} |`,
    `| 当前劳动者姓名 | ${info.workerName} |`,
    `| 当前企业 | ${info.enterpriseName} |`
  ].join('\n')
}

function buildWorkerAcceptanceExecutionContextFormalDraft(overview = getWorkerAcceptanceOverview()) {
  return [
    '## 执行上下文快照',
    '',
    '| 项目 | 记录 |',
    '| --- | --- |',
    `| 联调人 | ${normalizeMarkdownCell(overview.execution?.acceptanceOperator || '-')} |`,
    `| 验证端 | ${normalizeMarkdownCell(overview.execution?.verificationTerminal || '-')} |`,
    `| 设备/运行容器 | ${normalizeMarkdownCell(overview.execution?.deviceContainer || '-')} |`,
    `| 系统版本 | ${normalizeMarkdownCell(overview.execution?.systemVersion || '-')} |`,
    `| 构建模式 | ${normalizeMarkdownCell(overview.execution?.buildMode || '-')} |`,
    `| 当前账号 | ${normalizeMarkdownCell(overview.execution?.loginAccount || '-')} |`,
    `| 当前劳动者 | ${normalizeMarkdownCell(overview.execution?.workerName || '-')} |`,
    `| 当前企业 | ${normalizeMarkdownCell(overview.execution?.enterpriseName || '-')} |`
  ].join('\n')
}

function buildWorkerAcceptanceApiEnvironmentFormalDraft(overview = getWorkerAcceptanceOverview()) {
  return [
    '## 接口环境快照',
    '',
    '| 检查项 | 当前记录 |',
    '| --- | --- |',
    `| 当前接口地址 | ${normalizeMarkdownCell(overview.api?.baseUrl || '-')} |`,
    `| 构建模式 | ${normalizeMarkdownCell(overview.api?.buildMode || '-')} |`,
    `| 地址来源 | ${normalizeMarkdownCell(overview.api?.source || '-')} |`,
    `| 运行时地址缓存 | ${normalizeMarkdownCell(overview.api?.runtimeBaseUrl || '-')} |`,
    `| 最近检测时间 | ${normalizeMarkdownCell(overview.api?.lastTestAt || '-')} |`,
    `| 最近检测结果 | ${normalizeMarkdownCell(overview.api?.lastStatus || '-')} |`,
    `| 最近检测地址 | ${normalizeMarkdownCell(overview.api?.lastTestUrl || '-')} |`,
    `| 检测说明 | ${normalizeMarkdownCell(overview.api?.message || '-')} |`
  ].join('\n')
}

function buildWorkerAcceptancePushFormalDraft(overview = getWorkerAcceptanceOverview()) {
  return [
    '## 推送真机链路快照',
    '',
    '| 检查项 | 当前记录 |',
    '| --- | --- |',
    `| 本地登记状态 | ${normalizeMarkdownCell(overview.push?.lastStatus || '-')} |`,
    `| 最近登记时间 | ${normalizeMarkdownCell(overview.push?.lastAttemptAt || '-')} |`,
    `| ClientId | ${normalizeMarkdownCell(overview.push?.clientId || '-')} |`,
    `| 平台/权限 | ${normalizeMarkdownCell(`${overview.push?.platform || '-'} / ${overview.push?.permission || '-'}`)} |`,
    `| 待消费落页 | ${normalizeMarkdownCell(overview.push?.pendingTargetPath || '-')} |`,
    `| 最近写入待跳转 | ${normalizeMarkdownCell(overview.push?.jumpLastSavedAt || '-')} |`,
    `| 最近消费状态 | ${normalizeMarkdownCell(`${overview.push?.jumpLastConsumeStatus || '-'} / ${overview.push?.jumpLastConsumeAt || '-'}`)} |`,
    `| 最近打开结果 | ${normalizeMarkdownCell(`${overview.push?.jumpLastOpenResult || '-'} / ${overview.push?.jumpLastOpenAt || '-'}`)} |`,
    `| 最近推送事件 | ${normalizeMarkdownCell(`${overview.push?.latestEventTitle || '-'} / ${overview.push?.latestEventAt || '-'}`)} |`,
    `| 最近目标页面 | ${normalizeMarkdownCell(overview.push?.latestEventTargetPath || '-')} |`,
    `| 本地事件数量 | ${normalizeMarkdownCell(overview.push?.recentEventCount ?? 0)} |`,
    `| 链路说明 | ${normalizeMarkdownCell(overview.push?.lastMessage || overview.push?.jumpLastMessage || '-')} |`
  ].join('\n')
}

function buildWorkerAcceptanceCameraFormalDraft(overview = getWorkerAcceptanceOverview()) {
  return [
    '## 拍照上传真机链路快照',
    '',
    '| 检查项 | 当前记录 |',
    '| --- | --- |',
    `| 运行平台 | ${normalizeMarkdownCell(overview.camera?.platform || '-')} |`,
    `| 当前分类 | ${normalizeMarkdownCell(overview.camera?.categoryLabel || '-')} |`,
    `| 最近取图来源 | ${normalizeMarkdownCell(`${overview.camera?.lastPickSource || '-'} / ${overview.camera?.lastPickAt || '-'}`)} |`,
    `| 权限判定 | ${normalizeMarkdownCell(`${overview.camera?.pickPermissionLabel || '-'}${overview.camera?.pickErrorCode ? ` / ${overview.camera.pickErrorCode}` : ''}`)} |`,
    `| 最近上传状态 | ${normalizeMarkdownCell(`${overview.camera?.lastUploadStatus || '未上传'} / ${overview.camera?.lastUploadAt || '-'}`)} |`,
    `| 上传响应 | ${normalizeMarkdownCell(overview.camera?.lastUploadResponseSummary || '-')} |`,
    `| 上传记录回查 | ${normalizeMarkdownCell(`${overview.camera?.lastRecordCount ?? 0} 条 / ${overview.camera?.lastRecordLoadAt || '-'}`)} |`,
    `| 回查结果 | ${normalizeMarkdownCell(`${overview.camera?.lastRecordLoadStatus || '-'} / ${overview.camera?.lastRecordLoadMessage || '-'}`)} |`,
    `| 投诉带入 | ${normalizeMarkdownCell(`${overview.camera?.lastComplaintForwardAt || '-'} / ${overview.camera?.lastComplaintForwardUrl || '-'}`)} |`,
    `| 最新文件地址 | ${normalizeMarkdownCell(overview.camera?.latestUrl || '-')} |`,
    `| 链路说明 | ${normalizeMarkdownCell(overview.camera?.lastUploadMessage || overview.camera?.lastRecordLoadMessage || overview.camera?.lastPickMessage || '-')} |`
  ].join('\n')
}

function buildWorkerAcceptanceAttendanceFormalDraft(overview = getWorkerAcceptanceOverview()) {
  return [
    '## 考勤打卡真机链路快照',
    '',
    '| 检查项 | 当前记录 |',
    '| --- | --- |',
    `| 运行平台 | ${normalizeMarkdownCell(overview.attendance?.platform || '-')} |`,
    `| 网络状态 | ${normalizeMarkdownCell(overview.attendance?.networkConnected ? '在线' : '离线')} |`,
    `| 最近定位时间 | ${normalizeMarkdownCell(overview.attendance?.lastLocationAt || '-')} |`,
    `| 定位权限 | ${normalizeMarkdownCell(`${overview.attendance?.locationPermissionLabel || '-'}${overview.attendance?.locationErrorCode ? ` / ${overview.attendance.locationErrorCode}` : ''}`)} |`,
    `| 最近坐标 | ${normalizeMarkdownCell(overview.attendance?.lastLocationCoords || '-')} |`,
    `| 最近打卡动作 | ${normalizeMarkdownCell(`${overview.attendance?.lastActionType || '-'} / ${overview.attendance?.lastActionResult || '-'} / ${overview.attendance?.lastActionMode || '-'}`)} |`,
    `| 最近动作时间 | ${normalizeMarkdownCell(overview.attendance?.lastActionAt || '-')} |`,
    `| 离线队列数量 | ${normalizeMarkdownCell(`${overview.attendance?.offlineQueueCount ?? 0} 条`)} |`,
    `| 队列生命周期 | ${normalizeMarkdownCell(`首条 ${overview.attendance?.oldestQueueItem?.createdAt || overview.attendance?.oldestQueueItem?.timeText || '-'} / 末条 ${overview.attendance?.latestQueueItem?.createdAt || overview.attendance?.latestQueueItem?.timeText || '-'}`)} |`,
    `| 最近补传 | ${normalizeMarkdownCell(`${overview.attendance?.lastSyncStatus || '暂无'} / ${overview.attendance?.lastSyncAt || '-'}${overview.attendance?.lastSyncTrigger ? ` / ${overview.attendance.lastSyncTrigger === 'manual' ? '手动触发' : '自动触发'}` : ''}`)} |`,
    `| 补传计数 | ${normalizeMarkdownCell(`已补 ${overview.attendance?.lastSyncProcessedCount ?? 0} / 剩余 ${overview.attendance?.lastSyncRemainingCount ?? 0}`)} |`,
    `| 最近失败项 | ${normalizeMarkdownCell(overview.attendance?.lastSyncFailedItem || '-')} |`,
    `| 链路说明 | ${normalizeMarkdownCell(overview.attendance?.lastActionMessage || overview.attendance?.lastSyncMessage || overview.attendance?.lastLocationMessage || '-')} |`
  ].join('\n')
}

function buildWorkerAcceptanceMapFormalDraft(overview = getWorkerAcceptanceOverview()) {
  const locationStatus =
    overview.map?.lastLocationSuccess === true ? '定位成功' : overview.map?.lastLocationSuccess === false ? '定位降级' : '未获取'
  return [
    '## 地图定位真机链路快照',
    '',
    '| 检查项 | 当前记录 |',
    '| --- | --- |',
    `| 运行平台 | ${normalizeMarkdownCell(overview.map?.platform || '-')} |`,
    `| 定位状态 | ${normalizeMarkdownCell(locationStatus)} |`,
    `| 权限判定 | ${normalizeMarkdownCell(`${overview.map?.locationPermissionLabel || '-'}${overview.map?.locationErrorCode ? ` / ${overview.map.locationErrorCode}` : ''}`)} |`,
    `| 最近定位时间 | ${normalizeMarkdownCell(overview.map?.lastLocationAt || '-')} |`,
    `| map-config | ${normalizeMarkdownCell(`${overview.map?.lastMapConfigStatus || '-'} / ${overview.map?.lastMapConfigAt || '-'} / ${overview.map?.lastMapConfigCenter || '-'}`)} |`,
    `| nearby 加载 | ${normalizeMarkdownCell(`${overview.map?.lastNearbyStatus || '-'} / ${overview.map?.lastNearbyLoadAt || '-'}`)} |`,
    `| marker / 列表数量 | ${normalizeMarkdownCell(`${overview.map?.lastNearbyMarkerCount ?? 0} / ${overview.map?.lastNearbyJobCount ?? 0}`)} |`,
    `| 当前筛选 | ${normalizeMarkdownCell(`关键词 ${overview.map?.lastNearbyKeyword || '-'} / 工种 ${overview.map?.lastSelectedJobType || '全部'} / 薪资 ${overview.map?.lastSelectedSalaryLabel || '不限薪资'} / 半径 ${overview.map?.lastNearbyRadiusKm || '-'}km`)} |`,
    `| 链路说明 | ${normalizeMarkdownCell(overview.map?.lastApiMessage || overview.map?.lastNearbyMessage || overview.map?.lastMapConfigMessage || overview.map?.lastLocationMessage || '-')} |`
  ].join('\n')
}

function readStoredResults() {
  const value = uni.getStorageSync(WORKER_ACCEPTANCE_RESULTS_KEY)
  return value && typeof value === 'object' ? value : {}
}

function writeStoredResults(resultMap) {
  uni.setStorageSync(WORKER_ACCEPTANCE_RESULTS_KEY, resultMap)
}

function readStoredIssues() {
  const value = uni.getStorageSync(WORKER_ACCEPTANCE_ISSUES_KEY)
  return Array.isArray(value) ? value : []
}

function writeStoredIssues(items) {
  uni.setStorageSync(WORKER_ACCEPTANCE_ISSUES_KEY, items)
}

function readStoredEvidenceItems() {
  const value = uni.getStorageSync(WORKER_ACCEPTANCE_EVIDENCE_KEY)
  return Array.isArray(value) ? value : []
}

function writeStoredEvidenceItems(items) {
  uni.setStorageSync(WORKER_ACCEPTANCE_EVIDENCE_KEY, items)
}

function readStoredPhases() {
  const value = uni.getStorageSync(WORKER_ACCEPTANCE_PHASES_KEY)
  return value && typeof value === 'object' ? value : {}
}

function writeStoredPhases(phaseMap) {
  uni.setStorageSync(WORKER_ACCEPTANCE_PHASES_KEY, phaseMap)
}

function readStoredApis() {
  const value = uni.getStorageSync(WORKER_ACCEPTANCE_APIS_KEY)
  return value && typeof value === 'object' ? value : {}
}

function writeStoredApis(apiMap) {
  uni.setStorageSync(WORKER_ACCEPTANCE_APIS_KEY, apiMap)
}

function readStoredPrerequisites() {
  const value = uni.getStorageSync(WORKER_ACCEPTANCE_PREREQUISITES_KEY)
  return value && typeof value === 'object' ? value : {}
}

function writeStoredPrerequisites(prerequisiteMap) {
  uni.setStorageSync(WORKER_ACCEPTANCE_PREREQUISITES_KEY, prerequisiteMap)
}

function normalizeStatus(status) {
  return STATUS_META[status] ? status : 'pending'
}

function normalizePhaseStatus(status) {
  return PHASE_STATUS_META[status] ? status : 'pending'
}

function buildAcceptanceItem(item, resultMap) {
  const stored = resultMap[item.key] || {}
  const status = normalizeStatus(stored.status)
  return {
    ...item,
    status,
    statusLabel: STATUS_META[status].label,
    updatedAt: stored.updatedAt || '',
    note: stored.note || '',
    verificationTerminal: stored.verificationTerminal || '',
    evidenceSource: stored.evidenceSource || ''
  }
}

export function getWorkerAcceptanceResultItems() {
  const resultMap = readStoredResults()
  return DEFAULT_ACCEPTANCE_ITEMS.map((item) => buildAcceptanceItem(item, resultMap)).sort((left, right) => {
    const leftSort = STATUS_META[left.status]?.sort ?? 0
    const rightSort = STATUS_META[right.status]?.sort ?? 0
    if (leftSort !== rightSort) {
      return leftSort - rightSort
    }
    return left.label.localeCompare(right.label, 'zh-Hans-CN')
  })
}

export function setWorkerAcceptanceResultStatus(key, status) {
  const normalizedStatus = normalizeStatus(status)
  const resultMap = readStoredResults()
  resultMap[key] = {
    ...(resultMap[key] || {}),
    status: normalizedStatus,
    updatedAt: new Date().toLocaleString()
  }
  writeStoredResults(resultMap)
  return getWorkerAcceptanceResultItems()
}

export function setWorkerAcceptanceResultNote(key, note) {
  const resultMap = readStoredResults()
  const payload =
    typeof note === 'object' && note !== null
      ? {
          note: String(note.note || '').trim(),
          verificationTerminal: String(note.verificationTerminal || '').trim(),
          evidenceSource: String(note.evidenceSource || '').trim()
        }
      : {
          note: String(note || '').trim(),
          verificationTerminal: String(resultMap[key]?.verificationTerminal || '').trim(),
          evidenceSource: String(resultMap[key]?.evidenceSource || '').trim()
        }
  resultMap[key] = {
    ...(resultMap[key] || {}),
    note: payload.note,
    verificationTerminal: payload.verificationTerminal,
    evidenceSource: payload.evidenceSource,
    updatedAt: new Date().toLocaleString(),
    status: normalizeStatus(resultMap[key]?.status)
  }
  writeStoredResults(resultMap)
  return getWorkerAcceptanceResultItems()
}

export function clearWorkerAcceptanceResultItems() {
  uni.removeStorageSync(WORKER_ACCEPTANCE_RESULTS_KEY)
}

function buildAcceptanceIssueItem(index, stored) {
  return {
    id: index + 1,
    module: stored?.module || '',
    phenomenon: stored?.phenomenon || '',
    reproduceCondition: stored?.reproduceCondition || '',
    preliminaryJudgment: stored?.preliminaryJudgment || '',
    status: stored?.status || '',
    updatedAt: stored?.updatedAt || ''
  }
}

function normalizeAcceptanceIssuePatch(patch = {}) {
  return {
    module: String(patch.module || '').trim(),
    phenomenon: String(patch.phenomenon || '').trim(),
    reproduceCondition: String(patch.reproduceCondition || '').trim(),
    preliminaryJudgment: String(patch.preliminaryJudgment || '').trim(),
    status: String(patch.status || '').trim()
  }
}

export function getWorkerAcceptanceIssueItems() {
  const storedItems = readStoredIssues()
  return Array.from({ length: DEFAULT_ACCEPTANCE_ISSUE_COUNT }, (_, index) => {
    return buildAcceptanceIssueItem(index, storedItems[index] || {})
  })
}

export function setWorkerAcceptanceIssueItem(index, patch) {
  const targetIndex = Number(index)
  if (!Number.isInteger(targetIndex) || targetIndex < 0 || targetIndex >= DEFAULT_ACCEPTANCE_ISSUE_COUNT) {
    return getWorkerAcceptanceIssueItems()
  }
  const storedItems = readStoredIssues()
  const nextItems = Array.from({ length: DEFAULT_ACCEPTANCE_ISSUE_COUNT }, (_, currentIndex) => {
    const currentItem = storedItems[currentIndex] || {}
    if (currentIndex !== targetIndex) {
      return currentItem
    }
    return {
      ...currentItem,
      ...normalizeAcceptanceIssuePatch(patch),
      updatedAt: new Date().toLocaleString()
    }
  })
  writeStoredIssues(nextItems)
  return getWorkerAcceptanceIssueItems()
}

export function clearWorkerAcceptanceIssueItems() {
  uni.removeStorageSync(WORKER_ACCEPTANCE_ISSUES_KEY)
}

function buildAcceptanceEvidenceItem(item, stored) {
  return {
    ...item,
    pageEvidence: stored?.pageEvidence || '',
    apiEvidence: stored?.apiEvidence || '',
    serverLogTime: stored?.serverLogTime || '',
    issueTicket: stored?.issueTicket || '',
    note: stored?.note || '',
    updatedAt: stored?.updatedAt || ''
  }
}

function normalizeAcceptanceEvidencePatch(patch = {}) {
  return {
    pageEvidence: String(patch.pageEvidence || '').trim(),
    apiEvidence: String(patch.apiEvidence || '').trim(),
    serverLogTime: String(patch.serverLogTime || '').trim(),
    issueTicket: String(patch.issueTicket || '').trim(),
    note: String(patch.note || '').trim()
  }
}

export function getWorkerAcceptanceEvidenceItems() {
  const storedItems = readStoredEvidenceItems()
  return DEFAULT_ACCEPTANCE_EVIDENCE_ITEMS.map((item, index) => buildAcceptanceEvidenceItem(item, storedItems[index] || {}))
}

export function setWorkerAcceptanceEvidenceItem(index, patch) {
  const targetIndex = Number(index)
  if (!Number.isInteger(targetIndex) || targetIndex < 0 || targetIndex >= DEFAULT_ACCEPTANCE_EVIDENCE_ITEMS.length) {
    return getWorkerAcceptanceEvidenceItems()
  }
  const storedItems = readStoredEvidenceItems()
  const nextItems = Array.from({ length: DEFAULT_ACCEPTANCE_EVIDENCE_ITEMS.length }, (_, currentIndex) => {
    const currentItem = storedItems[currentIndex] || {}
    if (currentIndex !== targetIndex) {
      return currentItem
    }
    return {
      ...currentItem,
      ...normalizeAcceptanceEvidencePatch(patch),
      updatedAt: new Date().toLocaleString()
    }
  })
  writeStoredEvidenceItems(nextItems)
  return getWorkerAcceptanceEvidenceItems()
}

export function clearWorkerAcceptanceEvidenceItems() {
  uni.removeStorageSync(WORKER_ACCEPTANCE_EVIDENCE_KEY)
}

export function clearWorkerAcceptanceLocalRecords() {
  uni.removeStorageSync(WORKER_ACCEPTANCE_RESULTS_KEY)
  uni.removeStorageSync(WORKER_ACCEPTANCE_ISSUES_KEY)
  uni.removeStorageSync(WORKER_ACCEPTANCE_EVIDENCE_KEY)
  uni.removeStorageSync(WORKER_ACCEPTANCE_PHASES_KEY)
  uni.removeStorageSync(WORKER_ACCEPTANCE_APIS_KEY)
  uni.removeStorageSync(WORKER_ACCEPTANCE_PREREQUISITES_KEY)
  uni.removeStorageSync(WORKER_ACCEPTANCE_OPERATOR_KEY)
}

function buildAcceptancePhaseItem(item, phaseMap) {
  const stored = phaseMap[item.key] || {}
  const status = normalizePhaseStatus(stored.status)
  return {
    ...item,
    status,
    statusLabel: PHASE_STATUS_META[status].label,
    updatedAt: stored.updatedAt || '',
    note: stored.note || ''
  }
}

export function getWorkerAcceptancePhaseItems() {
  const phaseMap = readStoredPhases()
  return DEFAULT_ACCEPTANCE_PHASES.map((item) => buildAcceptancePhaseItem(item, phaseMap)).sort((left, right) => {
    const leftSort = PHASE_STATUS_META[left.status]?.sort ?? 0
    const rightSort = PHASE_STATUS_META[right.status]?.sort ?? 0
    if (leftSort !== rightSort) {
      return leftSort - rightSort
    }
    return left.label.localeCompare(right.label, 'zh-Hans-CN')
  })
}

export function setWorkerAcceptancePhaseStatus(key, status) {
  const normalizedStatus = normalizePhaseStatus(status)
  const phaseMap = readStoredPhases()
  phaseMap[key] = {
    ...(phaseMap[key] || {}),
    status: normalizedStatus,
    updatedAt: new Date().toLocaleString()
  }
  writeStoredPhases(phaseMap)
  return getWorkerAcceptancePhaseItems()
}

export function setWorkerAcceptancePhaseNote(key, note) {
  const phaseMap = readStoredPhases()
  phaseMap[key] = {
    ...(phaseMap[key] || {}),
    note: String(note || '').trim(),
    updatedAt: new Date().toLocaleString(),
    status: normalizePhaseStatus(phaseMap[key]?.status)
  }
  writeStoredPhases(phaseMap)
  return getWorkerAcceptancePhaseItems()
}

export function clearWorkerAcceptancePhaseItems() {
  uni.removeStorageSync(WORKER_ACCEPTANCE_PHASES_KEY)
}

function buildAcceptancePrerequisiteItem(item, prerequisiteMap) {
  const stored = prerequisiteMap[item.key] || {}
  const status = normalizePhaseStatus(stored.status)
  return {
    ...item,
    status,
    statusLabel: PHASE_STATUS_META[status].label,
    updatedAt: stored.updatedAt || '',
    note: stored.note || ''
  }
}

export function getWorkerAcceptancePrerequisiteItems() {
  const prerequisiteMap = readStoredPrerequisites()
  return DEFAULT_ACCEPTANCE_PREREQUISITES.map((item) => buildAcceptancePrerequisiteItem(item, prerequisiteMap)).sort((left, right) => {
    const leftSort = PHASE_STATUS_META[left.status]?.sort ?? 0
    const rightSort = PHASE_STATUS_META[right.status]?.sort ?? 0
    if (leftSort !== rightSort) {
      return leftSort - rightSort
    }
    return left.label.localeCompare(right.label, 'zh-Hans-CN')
  })
}

export function setWorkerAcceptancePrerequisiteStatus(key, status) {
  const normalizedStatus = normalizePhaseStatus(status)
  const prerequisiteMap = readStoredPrerequisites()
  prerequisiteMap[key] = {
    ...(prerequisiteMap[key] || {}),
    status: normalizedStatus,
    updatedAt: new Date().toLocaleString()
  }
  writeStoredPrerequisites(prerequisiteMap)
  return getWorkerAcceptancePrerequisiteItems()
}

export function setWorkerAcceptancePrerequisiteNote(key, note) {
  const prerequisiteMap = readStoredPrerequisites()
  prerequisiteMap[key] = {
    ...(prerequisiteMap[key] || {}),
    note: String(note || '').trim(),
    updatedAt: new Date().toLocaleString(),
    status: normalizePhaseStatus(prerequisiteMap[key]?.status)
  }
  writeStoredPrerequisites(prerequisiteMap)
  return getWorkerAcceptancePrerequisiteItems()
}

export function clearWorkerAcceptancePrerequisiteItems() {
  uni.removeStorageSync(WORKER_ACCEPTANCE_PREREQUISITES_KEY)
}

function buildAcceptanceApiItem(item, apiMap) {
  const stored = apiMap[item.key] || {}
  const status = normalizePhaseStatus(stored.status)
  return {
    ...item,
    status,
    statusLabel: PHASE_STATUS_META[status].label,
    updatedAt: stored.updatedAt || '',
    note: stored.note || ''
  }
}

export function getWorkerAcceptanceApiItems() {
  const apiMap = readStoredApis()
  return DEFAULT_ACCEPTANCE_API_GROUPS.map((item) => buildAcceptanceApiItem(item, apiMap)).sort((left, right) => {
    const leftSort = PHASE_STATUS_META[left.status]?.sort ?? 0
    const rightSort = PHASE_STATUS_META[right.status]?.sort ?? 0
    if (leftSort !== rightSort) {
      return leftSort - rightSort
    }
    return left.label.localeCompare(right.label, 'zh-Hans-CN')
  })
}

export function setWorkerAcceptanceApiStatus(key, status) {
  const normalizedStatus = normalizePhaseStatus(status)
  const apiMap = readStoredApis()
  apiMap[key] = {
    ...(apiMap[key] || {}),
    status: normalizedStatus,
    updatedAt: new Date().toLocaleString()
  }
  writeStoredApis(apiMap)
  return getWorkerAcceptanceApiItems()
}

export function setWorkerAcceptanceApiNote(key, note) {
  const apiMap = readStoredApis()
  apiMap[key] = {
    ...(apiMap[key] || {}),
    note: String(note || '').trim(),
    updatedAt: new Date().toLocaleString(),
    status: normalizePhaseStatus(apiMap[key]?.status)
  }
  writeStoredApis(apiMap)
  return getWorkerAcceptanceApiItems()
}

export function clearWorkerAcceptanceApiItems() {
  uni.removeStorageSync(WORKER_ACCEPTANCE_APIS_KEY)
}

export function buildWorkerAcceptanceApiSummary(items = getWorkerAcceptanceApiItems()) {
  const counter = items.reduce(
    (result, item) => {
      result[item.status] = (result[item.status] || 0) + 1
      return result
    },
    { pending: 0, in_progress: 0, blocked: 0, passed: 0 }
  )
  return `未开始 ${counter.pending} / 进行中 ${counter.in_progress} / 阻塞 ${counter.blocked} / 已完成 ${counter.passed}`
}

export function buildWorkerAcceptanceApiDraft(items = getWorkerAcceptanceApiItems()) {
  return [
    '## 接口联调清单',
    `- 汇总：${buildWorkerAcceptanceApiSummary(items)}`,
    ...items.map((item) => {
      const noteText = item.note ? ` / 备注：${item.note}` : ''
      return [`- ${item.label}：${item.statusLabel}${item.updatedAt ? ` / ${item.updatedAt}` : ''} / ${item.detail}${noteText}`, ...item.endpoints.map((endpoint) => `  - ${endpoint}`)].join('\n')
    })
  ].join('\n')
}

export function buildWorkerAcceptancePhaseSummary(items = getWorkerAcceptancePhaseItems()) {
  const counter = items.reduce(
    (result, item) => {
      result[item.status] = (result[item.status] || 0) + 1
      return result
    },
    { pending: 0, in_progress: 0, blocked: 0, passed: 0 }
  )
  return `未开始 ${counter.pending} / 进行中 ${counter.in_progress} / 阻塞 ${counter.blocked} / 已完成 ${counter.passed}`
}

export function buildWorkerAcceptancePhaseDraft(items = getWorkerAcceptancePhaseItems()) {
  return [
    '## 真机联调执行状态',
    `- 汇总：${buildWorkerAcceptancePhaseSummary(items)}`,
    ...items.map((item) => {
      const noteText = item.note ? ` / 备注：${item.note}` : ''
      return `- ${item.label}：${item.statusLabel}${item.updatedAt ? ` / ${item.updatedAt}` : ''} / ${item.detail}${noteText}`
    })
  ].join('\n')
}

export function buildWorkerAcceptancePrerequisiteSummary(items = getWorkerAcceptancePrerequisiteItems()) {
  const counter = items.reduce(
    (result, item) => {
      result[item.status] = (result[item.status] || 0) + 1
      return result
    },
    { pending: 0, in_progress: 0, blocked: 0, passed: 0 }
  )
  return `未开始 ${counter.pending} / 进行中 ${counter.in_progress} / 阻塞 ${counter.blocked} / 已就绪 ${counter.passed}`
}

export function buildWorkerAcceptancePrerequisiteDraft(items = getWorkerAcceptancePrerequisiteItems()) {
  return [
    '## 联调前置检查清单',
    `- 汇总：${buildWorkerAcceptancePrerequisiteSummary(items)}`,
    ...items.map((item) => {
      const noteText = item.note ? ` / 备注：${item.note}` : ''
      const statusLabel = item.status === 'passed' ? '已就绪' : item.statusLabel
      return `- ${item.label}：${statusLabel}${item.updatedAt ? ` / ${item.updatedAt}` : ''} / ${item.detail}${noteText}`
    })
  ].join('\n')
}

export function buildWorkerAcceptanceIssueSummary(items = getWorkerAcceptanceIssueItems()) {
  const filledCount = items.filter((item) => {
    return [item.module, item.phenomenon, item.reproduceCondition, item.preliminaryJudgment, item.status].some(Boolean)
  }).length
  return `已记录 ${filledCount} / ${items.length}`
}

export function buildWorkerAcceptanceIssueDraft(items = getWorkerAcceptanceIssueItems()) {
  return [
    '## 12. 问题记录',
    '',
    '| 编号 | 模块 | 现象 | 复现条件 | 初步判断 | 处理状态 |',
    '| --- | --- | --- | --- | --- | --- |',
    ...items.map((item) => {
      return `| ${item.id} | ${item.module || ''} | ${item.phenomenon || ''} | ${item.reproduceCondition || ''} | ${item.preliminaryJudgment || ''} | ${item.status || ''} |`
    })
  ].join('\n')
}

export function buildWorkerAcceptanceEvidenceSummary(items = getWorkerAcceptanceEvidenceItems()) {
  const filledCount = items.filter((item) => {
    return [item.pageEvidence, item.apiEvidence, item.serverLogTime, item.issueTicket, item.note].some(Boolean)
  }).length
  return `已记录 ${filledCount} / ${items.length}`
}

export function buildWorkerAcceptanceEvidenceDraft(items = getWorkerAcceptanceEvidenceItems()) {
  return [
    '## 证据采集清单',
    `- 汇总：${buildWorkerAcceptanceEvidenceSummary(items)}`,
    ...items.map((item) => {
      const parts = [
        `- ${item.label}`,
        `  - 预期：${item.expected}`,
        `  - 页面证据：${item.pageEvidence || '-'}`,
        `  - 接口返回：${item.apiEvidence || '-'}`,
        `  - 服务端日志时间点：${item.serverLogTime || '-'}`,
        `  - 问题单/阻塞编号：${item.issueTicket || '-'}`,
        `  - 备注：${item.note || '-'}`,
        `  - 更新时间：${item.updatedAt || '-'}`
      ]
      return parts.join('\n')
    })
  ].join('\n')
}

function normalizeMarkdownCell(value) {
  const normalized = String(value || '')
    .replace(/\|/g, ' / ')
    .replace(/\r?\n/g, ' <br> ')
    .trim()
  return normalized || '-'
}

function buildFormalRecordText(item) {
  const parts = []
  if (item.updatedAt) {
    parts.push(item.updatedAt)
  }
  if (item.note) {
    parts.push(`备注：${item.note}`)
  }
  return parts.join(' / ') || '-'
}

function buildAcceptanceResultFormalRecordText(item) {
  const parts = []
  if (item.updatedAt) {
    parts.push(item.updatedAt)
  }
  if (item.verificationTerminal) {
    parts.push(`验证端：${item.verificationTerminal}`)
  }
  if (item.evidenceSource) {
    parts.push(`证据：${item.evidenceSource}`)
  }
  if (item.note) {
    parts.push(`备注：${item.note}`)
  }
  return parts.join(' / ') || '-'
}

function buildWorkerAcceptanceBusinessFormalDraft(
  section,
  resultMap,
  phaseMap,
  apiMap
) {
  const rows = []

  section.resultKeys.forEach((key) => {
    const item = resultMap[key]
    if (!item) {
      return
    }
    rows.push([
      '结果记录',
      item.label,
      '模块结果与证据',
      buildAcceptanceResultFormalRecordText(item),
      item.statusLabel
    ])
  })

  section.phaseKeys.forEach((key) => {
    const item = phaseMap[key]
    if (!item) {
      return
    }
    rows.push([
      '阶段状态',
      item.label,
      item.detail,
      buildFormalRecordText(item),
      item.statusLabel
    ])
  })

  section.apiKeys.forEach((key) => {
    const item = apiMap[key]
    if (!item) {
      return
    }
    rows.push([
      '接口分组',
      item.label,
      item.endpoints?.length ? item.endpoints.join('；') : item.detail,
      buildFormalRecordText(item),
      item.statusLabel
    ])
  })

  return [
    `## ${section.title}`,
    '',
    '| 维度 | 名称 | 验收目标/覆盖范围 | 当前记录 | 状态 |',
    '| --- | --- | --- | --- | --- |',
    ...rows.map((row) => {
      return `| ${normalizeMarkdownCell(row[0])} | ${normalizeMarkdownCell(row[1])} | ${normalizeMarkdownCell(row[2])} | ${normalizeMarkdownCell(row[3])} | ${normalizeMarkdownCell(row[4])} |`
    })
  ].join('\n')
}

function buildWorkerAcceptanceEvidenceFormalDraft(items = getWorkerAcceptanceEvidenceItems()) {
  return [
    '## 附：证据采集清单',
    '',
    '| 证据项 | 预期 | 页面证据 | 接口返回 | 服务端日志时间点 | 问题单/阻塞编号 | 备注 | 更新时间 |',
    '| --- | --- | --- | --- | --- | --- | --- | --- |',
    ...items.map((item) => {
      return `| ${normalizeMarkdownCell(item.label)} | ${normalizeMarkdownCell(item.expected)} | ${normalizeMarkdownCell(item.pageEvidence)} | ${normalizeMarkdownCell(item.apiEvidence)} | ${normalizeMarkdownCell(item.serverLogTime)} | ${normalizeMarkdownCell(item.issueTicket)} | ${normalizeMarkdownCell(item.note)} | ${normalizeMarkdownCell(item.updatedAt)} |`
    })
  ].join('\n')
}

function buildWorkerAcceptancePrerequisiteFormalDraft(items = getWorkerAcceptancePrerequisiteItems()) {
  return [
    '## 3. 联调前置检查',
    '',
    '| 检查项 | 预期 | 实际/备注 | 是否通过 |',
    '| --- | --- | --- | --- |',
    ...items.map((item) => {
      const statusLabel = item.status === 'passed' ? '已就绪' : item.statusLabel
      return `| ${normalizeMarkdownCell(item.label)} | ${normalizeMarkdownCell(item.detail)} | ${normalizeMarkdownCell(buildFormalRecordText(item))} | ${normalizeMarkdownCell(statusLabel)} |`
    })
  ].join('\n')
}

function buildWorkerAcceptancePhaseFormalDraft(items = getWorkerAcceptancePhaseItems()) {
  return [
    '## 4. 执行状态摘要',
    '',
    '| 阶段 | 验收目标 | 当前记录 | 状态 |',
    '| --- | --- | --- | --- |',
    ...items.map((item) => {
      return `| ${normalizeMarkdownCell(item.label)} | ${normalizeMarkdownCell(item.detail)} | ${normalizeMarkdownCell(buildFormalRecordText(item))} | ${normalizeMarkdownCell(item.statusLabel)} |`
    })
  ].join('\n')
}

function buildWorkerAcceptanceApiFormalDraft(items = getWorkerAcceptanceApiItems()) {
  return [
    '## 5. 接口联调摘要',
    '',
    '| 接口分组 | 覆盖范围 | 当前记录 | 状态 |',
    '| --- | --- | --- | --- |',
    ...items.map((item) => {
      const endpointText = item.endpoints?.length ? item.endpoints.join('；') : item.detail
      return `| ${normalizeMarkdownCell(item.label)} | ${normalizeMarkdownCell(endpointText)} | ${normalizeMarkdownCell(buildFormalRecordText(item))} | ${normalizeMarkdownCell(item.statusLabel)} |`
    })
  ].join('\n')
}

function buildWorkerAcceptanceResultFormalTable(items = getWorkerAcceptanceResultItems()) {
  return [
    '## 11. 联调结果记录',
    '',
    '| 模块 | 状态 | 验证端 | 证据来源 | 备注 | 更新时间 |',
    '| --- | --- | --- | --- | --- | --- |',
    ...items.map((item) => {
      return `| ${normalizeMarkdownCell(item.label)} | ${normalizeMarkdownCell(item.statusLabel)} | ${normalizeMarkdownCell(item.verificationTerminal)} | ${normalizeMarkdownCell(item.evidenceSource)} | ${normalizeMarkdownCell(item.note)} | ${normalizeMarkdownCell(item.updatedAt)} |`
    })
  ].join('\n')
}

export function buildWorkerAcceptanceResultSummary(items = getWorkerAcceptanceResultItems()) {
  const counter = items.reduce(
    (result, item) => {
      result[item.status] = (result[item.status] || 0) + 1
      return result
    },
    { pending: 0, passed: 0, failed: 0, blocked: 0 }
  )
  return `待验收 ${counter.pending} / 已通过 ${counter.passed} / 失败 ${counter.failed} / 阻塞 ${counter.blocked}`
}

export function buildWorkerAcceptanceResultDraft(items = getWorkerAcceptanceResultItems()) {
  return [
    '## 劳动者端真机联调结果记录',
    `- 汇总：${buildWorkerAcceptanceResultSummary(items)}`,
    ...items.map((item) => {
      const noteText = item.note ? ` / 备注：${item.note}` : ''
      const terminalText = item.verificationTerminal ? ` / 验证端：${item.verificationTerminal}` : ''
      const evidenceText = item.evidenceSource ? ` / 证据：${item.evidenceSource}` : ''
      return `- ${item.label}：${item.statusLabel}${item.updatedAt ? ` / ${item.updatedAt}` : ''}${terminalText}${evidenceText}${noteText}`
    })
  ].join('\n')
}

function buildAcceptanceItemMap(items) {
  return items.reduce((result, item) => {
    result[item.key] = item
    return result
  }, {})
}

function resolveTemplateSectionItems(section, itemMap) {
  return section.itemKeys.map((key) => itemMap[key]).filter(Boolean)
}

function resolveTemplateSectionResult(sectionItems) {
  if (!sectionItems.length) {
    return '不通过'
  }
  return sectionItems.every((item) => item.status === 'passed') ? '通过' : '不通过'
}

function buildTemplateSectionEvidenceText(sectionItems) {
  if (!sectionItems.length) {
    return '暂无记录'
  }
  return sectionItems
    .map((item) => {
      const parts = [`${item.label}：${item.statusLabel}`]
      if (item.updatedAt) {
        parts.push(item.updatedAt)
      }
      if (item.verificationTerminal) {
        parts.push(`验证端：${item.verificationTerminal}`)
      }
      if (item.evidenceSource) {
        parts.push(`证据：${item.evidenceSource}`)
      }
      if (item.note) {
        parts.push(`备注：${item.note}`)
      }
      return parts.join(' / ')
    })
    .join('；')
}

export function buildWorkerAcceptanceTemplateSummaryDraft(items = getWorkerAcceptanceResultItems()) {
  const itemMap = buildAcceptanceItemMap(items)
  const sections = TEMPLATE_SUMMARY_SECTIONS.map((section) => {
    const sectionItems = resolveTemplateSectionItems(section, itemMap)
    return {
      ...section,
      result: resolveTemplateSectionResult(sectionItems),
      evidenceText: buildTemplateSectionEvidenceText(sectionItems)
    }
  })
  const finalConclusion = sections.every((section) => section.result === '通过') ? '可验收' : '需继续整改'

  return [
    '## 13. 总结',
    '',
    '| 项目 | 结果 |',
    '| --- | --- |',
    ...sections.map((section) => `| ${section.label} | ${section.result} |`),
    `| 最终结论 | ${finalConclusion} |`,
    '',
    '### 结果说明',
    ...sections.map((section) => `- ${section.label}：${section.result}；依据：${section.evidenceText}`),
    `- 最终结论：${finalConclusion}`
  ].join('\n')
}

export function buildWorkerAcceptanceFormalDraft(
  issueItems = getWorkerAcceptanceIssueItems(),
  resultItems = getWorkerAcceptanceResultItems(),
  phaseItems = getWorkerAcceptancePhaseItems(),
  apiItems = getWorkerAcceptanceApiItems(),
  prerequisiteItems = getWorkerAcceptancePrerequisiteItems(),
  evidenceItems = getWorkerAcceptanceEvidenceItems()
) {
  const overview = getWorkerAcceptanceOverview()
  const resultMap = buildAcceptanceItemMap(resultItems)
  const phaseMap = buildAcceptanceItemMap(phaseItems)
  const apiMap = buildAcceptanceItemMap(apiItems)
  return [
    buildWorkerAcceptanceBasicInfoDraft(),
    '',
    buildWorkerAcceptanceExecutionContextFormalDraft(overview),
    '',
    buildWorkerAcceptanceApiEnvironmentFormalDraft(overview),
    '',
    buildWorkerAcceptancePushFormalDraft(overview),
    '',
    buildWorkerAcceptanceCameraFormalDraft(overview),
    '',
    buildWorkerAcceptanceAttendanceFormalDraft(overview),
    '',
    buildWorkerAcceptanceMapFormalDraft(overview),
    '',
    ...BUSINESS_ACCEPTANCE_FORMAL_SECTIONS.flatMap((section) => [
      buildWorkerAcceptanceBusinessFormalDraft(section, resultMap, phaseMap, apiMap),
      ''
    ]),
    '',
    buildWorkerAcceptancePrerequisiteFormalDraft(prerequisiteItems),
    '',
    buildWorkerAcceptancePhaseFormalDraft(phaseItems),
    '',
    buildWorkerAcceptanceApiFormalDraft(apiItems),
    '',
    buildWorkerAcceptanceIssueDraft(issueItems),
    '',
    buildWorkerAcceptanceEvidenceFormalDraft(evidenceItems),
    '',
    buildWorkerAcceptanceResultFormalTable(resultItems),
    '',
    buildWorkerAcceptanceTemplateSummaryDraft(resultItems),
    '',
    '## 附：联调结果摘要',
    `- 汇总：${buildWorkerAcceptanceResultSummary(resultItems)}`
  ].join('\n')
}
