<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="worker-title">附近岗位</view>
      <view class="worker-subtitle">{{ sourceDescription }}</view>
      <input
        v-model="keyword"
        class="form-input"
        placeholder="请输入岗位、企业或工种"
        @confirm="loadNearbyJobs"
      />
      <view class="search-actions">
        <button class="worker-button" @click="loadNearbyJobs">刷新附近岗位</button>
        <button class="worker-button worker-button--secondary" @click="relocate">重新定位</button>
      </view>
      <scroll-view v-if="jobTypeOptions.length" class="filter-scroll" scroll-x>
        <view class="filter-row">
          <view
            class="filter-chip"
            :class="{ 'filter-chip--active': !selectedJobType }"
            @click="selectJobType('')"
          >
            全部工种
          </view>
          <view
            v-for="item in jobTypeOptions"
            :key="item"
            class="filter-chip"
            :class="{ 'filter-chip--active': selectedJobType === item }"
            @click="selectJobType(item)"
          >
            {{ item }}
          </view>
        </view>
      </scroll-view>
      <scroll-view class="filter-scroll" scroll-x>
        <view class="filter-row">
          <view
            v-for="item in salaryOptions"
            :key="item.label"
            class="filter-chip"
            :class="{ 'filter-chip--active': selectedSalaryLabel === item.label }"
            @click="selectSalary(item)"
          >
            {{ item.label }}
          </view>
        </view>
      </scroll-view>
      <view class="radius-row">
        <view
          v-for="item in radiusOptions"
          :key="item"
          class="radius-chip"
          :class="{ 'radius-chip--active': radiusKm === item }"
          @click="changeRadius(item)"
        >
          {{ item }}km
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">岗位地图</view>
        <view class="worker-tag">{{ jobs.length }} 条</view>
      </view>
      <map
        class="job-map"
        :latitude="centerLatitude"
        :longitude="centerLongitude"
        :markers="markers"
        :scale="12"
        :show-location="locationAvailable"
        @markertap="handleMarkerTap"
      />
    </view>
    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">附近岗位列表</view>
        <view class="worker-tag">{{ radiusKm }}km 内</view>
      </view>
      <view v-if="jobs.length">
        <view v-for="item in jobs" :key="item.jobId" class="job-row" @click="openDetail(item)">
          <view class="job-row__title">{{ item.title }}</view>
          <view class="job-row__meta">{{ item.enterpriseName || '-' }}</view>
          <view class="job-row__meta">{{ item.jobType || '-' }} / {{ item.workAddress || '-' }}</view>
          <view class="job-row__footer">
            <text class="job-row__salary">{{ item.salaryText || '面议' }}</text>
            <text class="worker-tag">{{ item.distanceText || '-' }}</text>
          </view>
        </view>
      </view>
      <view v-else class="worker-empty">当前范围内暂无岗位</view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getNearbyJobList, getNearbyJobMapConfig } from '../../api/worker'

const JOB_MAP_DIAGNOSTICS_KEY = 'worker_job_map_diagnostics'

const runtimePlatform = ref('')
const keyword = ref('')
const jobs = ref([])
const markers = ref([])
const radiusOptions = ref([3, 5, 10, 20, 50])
const radiusKm = ref(20)
const centerLatitude = ref(23.12911)
const centerLongitude = ref(113.264385)
const locationAvailable = ref(false)
const latitude = ref()
const longitude = ref()
const sourceDesc = ref('')
const jobTypeOptions = ref([])
const salaryOptions = ref([{ label: '不限薪资', salaryMin: undefined, salaryMax: undefined }])
const selectedJobType = ref('')
const selectedSalaryMin = ref(undefined)
const selectedSalaryMax = ref(undefined)
const selectedSalaryLabel = ref('不限薪资')
const locationLastAt = ref('')
const locationSuccess = ref(null)
const locationPermissionStatus = ref('')
const locationErrorCode = ref('')
const locationMessage = ref('')
const lastMapConfigAt = ref('')
const lastMapConfigStatus = ref('')
const lastMapConfigCenter = ref('')
const lastMapConfigMessage = ref('')
const lastNearbyLoadAt = ref('')
const lastNearbyStatus = ref('')
const lastNearbyMarkerCount = ref(0)
const lastNearbyJobCount = ref(0)
const lastNearbyRadiusKm = ref(0)
const lastNearbyKeyword = ref('')
const lastSelectedJobType = ref('')
const lastSelectedSalaryLabel = ref('')
const lastNearbyMessage = ref('')
const lastApiMessage = ref('')
const lastActionAt = ref('')

const mapPlatformText = computed(() => formatPlatform(runtimePlatform.value))
const sourceDescription = computed(() => {
  if (sourceDesc.value) {
    return sourceDesc.value
  }
  return locationAvailable.value
    ? '已按当前位置展示附近岗位'
    : '未获取到定位，当前按默认推荐中心展示附近岗位'
})
const locationCoordsText = computed(() => formatCoords(latitude.value, longitude.value))
const centerCoordsText = computed(() => formatCoords(centerLatitude.value, centerLongitude.value))
const locationSummaryText = computed(() => {
  if (locationSuccess.value === true) {
    return locationLastAt.value ? `定位成功 / ${locationLastAt.value}` : '定位成功'
  }
  if (locationSuccess.value === false) {
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
const mapConfigSummaryText = computed(() => {
  const parts = []
  if (lastMapConfigStatus.value) {
    parts.push(lastMapConfigStatus.value)
  }
  if (lastMapConfigAt.value) {
    parts.push(lastMapConfigAt.value)
  }
  if (lastMapConfigCenter.value) {
    parts.push(lastMapConfigCenter.value)
  }
  return parts.join(' / ') || '暂无 map-config 记录'
})
const nearbySummaryText = computed(() => {
  const parts = []
  if (lastNearbyStatus.value) {
    parts.push(lastNearbyStatus.value)
  }
  if (lastNearbyLoadAt.value) {
    parts.push(lastNearbyLoadAt.value)
  }
  if (lastNearbyMarkerCount.value || lastNearbyJobCount.value) {
    parts.push(`marker ${lastNearbyMarkerCount.value} / 列表 ${lastNearbyJobCount.value}`)
  }
  return parts.join(' / ') || '暂无 nearby 记录'
})
const filterSummaryText = computed(() => {
  return [
    `关键词 ${keyword.value || '-'}`,
    `工种 ${selectedJobType.value || '全部'}`,
    `薪资 ${selectedSalaryLabel.value || '不限薪资'}`,
    `半径 ${radiusKm.value}km`
  ].join(' / ')
})
const markerSummaryText = computed(() => `${markers.value.length} / ${jobs.value.length}`)
const jobMapConsistencyText = computed(() => {
  if (!jobs.value.length) {
    return '当前地图无岗位，需继续核对岗位列表空态、筛选条件和简历引导是否同口径'
  }
  return `地图 marker ${markers.value.length} / 列表 ${jobs.value.length}，需与岗位列表、岗位详情和投递记录保持同批数据`
})
const mapSnapshotText = computed(() => {
  return [
    '## 地图联调摘要',
    `- 运行平台：${mapPlatformText.value}`,
    `- 最近联动：${lastActionAt.value || '-'}`,
    `- 定位状态：${locationSummaryText.value}`,
    `- 权限判定：${locationPermissionText.value}`,
    `- 当前位置：${locationCoordsText.value}`,
    `- 地图中心：${centerCoordsText.value}`,
    `- 来源说明：${sourceDescription.value}`,
    `- map-config：${mapConfigSummaryText.value}`,
    `- nearby：${nearbySummaryText.value}`,
    `- 筛选条件：${filterSummaryText.value}`,
    `- marker / 列表：${markerSummaryText.value}`,
    `- 跨页一致性：${jobMapConsistencyText.value}`,
    `- 接口说明：${lastNearbyMessage.value || lastApiMessage.value || lastMapConfigMessage.value || '-'}`,
    `- 定位说明：${locationMessage.value || '-'}`,
    '- 链路关联：岗位列表 / 附近岗位地图 / 岗位详情 / 投递记录'
  ].join('\n')
})

function persistJobMapDiagnostics() {
  uni.setStorageSync(JOB_MAP_DIAGNOSTICS_KEY, {
    runtimePlatform: runtimePlatform.value,
    locationLastAt: locationLastAt.value,
    locationSuccess: locationSuccess.value,
    locationPermissionStatus: locationPermissionStatus.value,
    locationErrorCode: locationErrorCode.value,
    locationMessage: locationMessage.value,
    lastMapConfigAt: lastMapConfigAt.value,
    lastMapConfigStatus: lastMapConfigStatus.value,
    lastMapConfigCenter: lastMapConfigCenter.value,
    lastMapConfigMessage: lastMapConfigMessage.value,
    lastNearbyLoadAt: lastNearbyLoadAt.value,
    lastNearbyStatus: lastNearbyStatus.value,
    lastNearbyMarkerCount: lastNearbyMarkerCount.value,
    lastNearbyJobCount: lastNearbyJobCount.value,
    lastNearbyRadiusKm: lastNearbyRadiusKm.value,
    lastNearbyKeyword: lastNearbyKeyword.value,
    lastSelectedJobType: lastSelectedJobType.value,
    lastSelectedSalaryLabel: lastSelectedSalaryLabel.value,
    lastNearbyMessage: lastNearbyMessage.value,
    lastApiMessage: lastApiMessage.value,
    lastActionAt: lastActionAt.value
  })
}

function restoreJobMapDiagnostics() {
  const snapshot = uni.getStorageSync(JOB_MAP_DIAGNOSTICS_KEY) || {}
  runtimePlatform.value = snapshot.runtimePlatform || resolveRuntimePlatform()
  locationLastAt.value = snapshot.locationLastAt || ''
  locationSuccess.value = typeof snapshot.locationSuccess === 'boolean' ? snapshot.locationSuccess : null
  locationPermissionStatus.value = snapshot.locationPermissionStatus || ''
  locationErrorCode.value = snapshot.locationErrorCode || ''
  locationMessage.value = snapshot.locationMessage || ''
  lastMapConfigAt.value = snapshot.lastMapConfigAt || ''
  lastMapConfigStatus.value = snapshot.lastMapConfigStatus || ''
  lastMapConfigCenter.value = snapshot.lastMapConfigCenter || ''
  lastMapConfigMessage.value = snapshot.lastMapConfigMessage || ''
  lastNearbyLoadAt.value = snapshot.lastNearbyLoadAt || ''
  lastNearbyStatus.value = snapshot.lastNearbyStatus || ''
  lastNearbyMarkerCount.value = Number(snapshot.lastNearbyMarkerCount || 0)
  lastNearbyJobCount.value = Number(snapshot.lastNearbyJobCount || 0)
  lastNearbyRadiusKm.value = Number(snapshot.lastNearbyRadiusKm || 0)
  lastNearbyKeyword.value = snapshot.lastNearbyKeyword || ''
  lastSelectedJobType.value = snapshot.lastSelectedJobType || ''
  lastSelectedSalaryLabel.value = snapshot.lastSelectedSalaryLabel || ''
  lastNearbyMessage.value = snapshot.lastNearbyMessage || ''
  lastApiMessage.value = snapshot.lastApiMessage || ''
  lastActionAt.value = snapshot.lastActionAt || ''
}

function recordJobMapAction(action, detail) {
  lastActionAt.value = new Date().toLocaleString()
  lastApiMessage.value = detail ? `${action} / ${detail}` : action
  persistJobMapDiagnostics()
}

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
      message: '定位权限被拒绝，已回退默认推荐中心'
    }
  }
  if (lowerErrMsg.includes('cancel')) {
    return {
      permissionStatus: 'cancelled',
      errorCode: 'LOCATION_CANCELLED',
      message: '用户取消了本次定位'
    }
  }
  return {
    permissionStatus: 'failed',
    errorCode: 'LOCATION_FAILED',
    message: errMsg || '定位失败，已回退默认推荐中心'
  }
}

function formatCoords(lat, lng) {
  if (lat === undefined || lng === undefined || lat === null || lng === null) {
    return '-'
  }
  return `${Number(lat).toFixed(6)}, ${Number(lng).toFixed(6)}`
}

async function loadMapConfig() {
  try {
    const data = await getNearbyJobMapConfig(latitude.value, longitude.value)
    centerLatitude.value = data?.centerLatitude || centerLatitude.value
    centerLongitude.value = data?.centerLongitude || centerLongitude.value
    locationAvailable.value = !!data?.locationAvailable
    sourceDesc.value = data?.sourceDescription || ''
    radiusOptions.value = data?.radiusOptions || radiusOptions.value
    radiusKm.value = data?.defaultRadiusKm || radiusKm.value
    jobTypeOptions.value = data?.jobTypeOptions || []
    salaryOptions.value = data?.salaryOptions || salaryOptions.value
    lastMapConfigAt.value = new Date().toLocaleString()
    lastMapConfigStatus.value = '加载成功'
    lastMapConfigCenter.value = formatCoords(data?.centerLatitude, data?.centerLongitude)
    lastMapConfigMessage.value = `map-config 已返回中心 ${lastMapConfigCenter.value}`
    lastApiMessage.value = lastMapConfigMessage.value
    persistJobMapDiagnostics()
  } catch (error) {
    lastMapConfigAt.value = new Date().toLocaleString()
    lastMapConfigStatus.value = '加载失败'
    lastMapConfigCenter.value = centerCoordsText.value
    lastMapConfigMessage.value = error.message || '加载地图配置失败'
    lastApiMessage.value = lastMapConfigMessage.value
    persistJobMapDiagnostics()
    throw error
  }
}

async function loadNearbyJobs() {
  try {
    const data = await getNearbyJobList({
      keyword: keyword.value,
      jobType: selectedJobType.value,
      salaryMin: selectedSalaryMin.value,
      salaryMax: selectedSalaryMax.value,
      latitude: latitude.value,
      longitude: longitude.value,
      radiusKm: radiusKm.value
    })
    jobs.value = data?.rows || []
    markers.value = data?.markers || []
    centerLatitude.value = data?.centerLatitude || centerLatitude.value
    centerLongitude.value = data?.centerLongitude || centerLongitude.value
    locationAvailable.value = !!data?.locationAvailable
    jobTypeOptions.value = data?.jobTypeOptions || jobTypeOptions.value
    salaryOptions.value = data?.salaryOptions || salaryOptions.value
    lastNearbyLoadAt.value = new Date().toLocaleString()
    lastNearbyStatus.value = '加载成功'
    lastNearbyMarkerCount.value = markers.value.length
    lastNearbyJobCount.value = jobs.value.length
    lastNearbyRadiusKm.value = radiusKm.value
    lastNearbyKeyword.value = keyword.value
    lastSelectedJobType.value = selectedJobType.value
    lastSelectedSalaryLabel.value = selectedSalaryLabel.value
    lastNearbyMessage.value = `nearby 返回 ${markers.value.length} 个 marker，${jobs.value.length} 条岗位`
    lastApiMessage.value = lastNearbyMessage.value
    persistJobMapDiagnostics()
  } catch (error) {
    lastNearbyLoadAt.value = new Date().toLocaleString()
    lastNearbyStatus.value = '加载失败'
    lastNearbyRadiusKm.value = radiusKm.value
    lastNearbyKeyword.value = keyword.value
    lastSelectedJobType.value = selectedJobType.value
    lastSelectedSalaryLabel.value = selectedSalaryLabel.value
    lastNearbyMessage.value = error.message || '加载附近岗位失败'
    lastApiMessage.value = lastNearbyMessage.value
    persistJobMapDiagnostics()
    uni.showToast({ title: error.message || '加载附近岗位失败', icon: 'none' })
  }
}

function resolveLocation() {
  return new Promise((resolve) => {
    uni.getLocation({
      type: 'gcj02',
      success: (res) => {
        latitude.value = res.latitude
        longitude.value = res.longitude
        locationAvailable.value = true
        locationLastAt.value = new Date().toLocaleString()
        locationSuccess.value = true
        locationPermissionStatus.value = 'granted'
        locationErrorCode.value = ''
        locationMessage.value = `已获取定位 ${formatCoords(res.latitude, res.longitude)}`
        persistJobMapDiagnostics()
        resolve(true)
      },
      fail: (error) => {
        const diagnostics = classifyLocationError(error)
        latitude.value = undefined
        longitude.value = undefined
        locationAvailable.value = false
        locationLastAt.value = new Date().toLocaleString()
        locationSuccess.value = false
        locationPermissionStatus.value = diagnostics.permissionStatus
        locationErrorCode.value = diagnostics.errorCode
        locationMessage.value = diagnostics.message
        persistJobMapDiagnostics()
        resolve(false)
      }
    })
  })
}

async function relocate() {
  try {
    recordJobMapAction('重新定位', '刷新定位并重载 map-config 与 nearby')
    await resolveLocation()
    await loadMapConfig()
    await loadNearbyJobs()
  } catch (error) {
    uni.showToast({ title: error.message || '定位失败', icon: 'none' })
  }
}

function selectJobType(value) {
  selectedJobType.value = value
  recordJobMapAction('切换工种筛选', value || '全部工种')
  loadNearbyJobs()
}

function selectSalary(item) {
  selectedSalaryLabel.value = item.label
  selectedSalaryMin.value = item.salaryMin
  selectedSalaryMax.value = item.salaryMax
  recordJobMapAction('切换薪资筛选', item.label || '不限薪资')
  loadNearbyJobs()
}

function changeRadius(value) {
  radiusKm.value = value
  recordJobMapAction('切换范围半径', `${value}km`)
  loadNearbyJobs()
}

function handleMarkerTap(event) {
  const markerId = event?.detail?.markerId
  const matchedMarker = markers.value.find((item) => String(item?.id ?? item?.markerId ?? '') === String(markerId ?? ''))
  const resolvedMarkerId = matchedMarker?.id ?? matchedMarker?.markerId ?? markerId
  const job = jobs.value.find((item) => String(item?.markerId ?? item?.id ?? '') === String(resolvedMarkerId ?? ''))
  if (job) {
    recordJobMapAction(`点击地图 marker：${job.title || '-'}`, `jobId=${job.jobId || '-'} / 进入岗位详情`)
    openDetail(job)
  }
}

function openDetail(item) {
  if (!item?.jobId) {
    return
  }
  recordJobMapAction(`打开岗位详情：${item.title || '-'}`, `jobId=${item.jobId} / 需与岗位列表和投递记录保持一致`)
  uni.navigateTo({ url: `/pages/job/detail?jobId=${item.jobId}` })
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

onLoad(async (options) => {
  restoreJobMapDiagnostics()
  runtimePlatform.value = runtimePlatform.value || resolveRuntimePlatform()
  keyword.value = decodeURIComponent(options?.keyword || '')
  selectedJobType.value = decodeURIComponent(options?.jobType || '')
  selectedSalaryMin.value = options?.salaryMin ? Number(options.salaryMin) : undefined
  selectedSalaryMax.value = options?.salaryMax ? Number(options.salaryMax) : undefined
  radiusKm.value = options?.radiusKm ? Number(options.radiusKm) : radiusKm.value
  if (options?.keyword || options?.jobType || options?.radiusKm || options?.salaryMin || options?.salaryMax) {
    recordJobMapAction('承接岗位列表筛选', '已从岗位列表透传关键词、工种、薪资或范围条件')
  }
  await resolveLocation()
  try {
    await loadMapConfig()
  } catch (error) {
    uni.showToast({ title: error.message || '加载地图配置失败', icon: 'none' })
  }
  if (selectedSalaryMin.value !== undefined || selectedSalaryMax.value !== undefined) {
    const matched = salaryOptions.value.find((item) => item.salaryMin === selectedSalaryMin.value && item.salaryMax === selectedSalaryMax.value)
    selectedSalaryLabel.value = matched?.label || selectedSalaryLabel.value
  }
  await loadNearbyJobs()
})
</script>

<style lang="scss">
.form-input {
  width: 100%;
  margin-top: 18rpx;
  padding: 20rpx 24rpx;
  border-radius: 18rpx;
  background: #f5f8fc;
  box-sizing: border-box;
  font-size: 28rpx;
  color: #16324f;
}

.search-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 18rpx;
}

.search-actions button {
  flex: 1;
}

.filter-scroll {
  width: 100%;
  white-space: nowrap;
  margin-top: 14rpx;
}

.filter-row {
  display: inline-flex;
  gap: 12rpx;
}

.filter-chip,
.radius-chip {
  padding: 10rpx 18rpx;
  border-radius: 999rpx;
  background: #eef4fb;
  color: #57748f;
  font-size: 22rpx;
}

.filter-chip--active,
.radius-chip--active {
  background: #1f6fd6;
  color: #ffffff;
}

.radius-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-top: 18rpx;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 18rpx;
}

.section-head--sub {
  margin-top: 20rpx;
}

.job-map {
  width: 100%;
  height: 520rpx;
  border-radius: 20rpx;
  overflow: hidden;
}

.job-row {
  padding: 24rpx 0;
  border-bottom: 1rpx solid #edf2f7;
}

.job-row:last-child {
  border-bottom: none;
}

.job-row__title {
  font-size: 30rpx;
  font-weight: 700;
  color: #16324f;
}

.job-row__meta {
  margin-top: 8rpx;
  font-size: 24rpx;
  color: #7890aa;
}

.job-row__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 18rpx;
}

.job-row__salary {
  font-size: 28rpx;
  color: #d66b1f;
  font-weight: 700;
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
  color: #16324f;
  line-height: 1.6;
  word-break: break-all;
}

.result-block {
  margin-top: 16rpx;
  padding: 22rpx 24rpx;
  border-radius: 20rpx;
  background: #f5f8fc;
}

.result-block__label {
  font-size: 22rpx;
  color: #7890aa;
}

.result-block__value {
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: #16324f;
  white-space: pre-wrap;
  word-break: break-all;
}

.clear-action {
  font-size: 24rpx;
  color: #1f6fd6;
}
</style>
