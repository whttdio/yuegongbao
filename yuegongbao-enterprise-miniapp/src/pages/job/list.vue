<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">找工作</view>
      <view class="worker-subtitle">按工种、薪资和距离筛选附近岗位</view>
    </view>

    <view class="worker-card">
      <view class="form-field">
        <view class="form-field__label">关键词</view>
        <input
          v-model="keyword"
          class="form-input"
          placeholder="请输入岗位、企业或工种"
          @confirm="loadData"
        />
      </view>
      <view class="search-actions">
        <button class="worker-button" @click="loadData">搜索岗位</button>
        <button class="worker-button worker-button--secondary" @click="openMap">附近岗位</button>
      </view>
      <view class="filter-head">
        <view class="worker-subtitle">{{ locationTip }}</view>
        <view class="more-link" @click="relocate">刷新定位</view>
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
      <scroll-view class="filter-scroll" scroll-x>
        <view class="filter-row">
          <view
            v-for="item in radiusOptions"
            :key="item.value"
            class="filter-chip"
            :class="{ 'filter-chip--active': selectedRadiusKm === item.value }"
            @click="selectRadius(item.value)"
          >
            {{ item.label }}
          </view>
        </view>
      </scroll-view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">岗位列表</view>
        <view class="worker-tag">{{ total }} 条</view>
      </view>
      <view v-if="jobs.length">
        <view v-for="item in jobs" :key="item.jobId" class="job-row" @click="openDetail(item)">
          <view class="job-row__title">{{ item.title }}</view>
          <view class="job-row__meta">{{ item.enterpriseName || '-' }}</view>
          <view class="job-row__meta">{{ item.jobType || '-' }} / {{ item.workAddress || '-' }}</view>
          <view class="job-row__footer">
            <text class="job-row__salary">{{ item.salaryText || '面议' }}</text>
            <text class="worker-tag">{{ item.distanceText || (item.applied ? '已投递' : '可投递') }}</text>
          </view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无匹配岗位</view>
        <view class="worker-empty__desc">
          可调整工种、薪资或距离筛选，也可切换到附近岗位地图继续查看周边机会；如条件还不完整，建议先完善简历。
        </view>
        <view class="job-empty-actions">
          <button class="worker-button worker-button--secondary" @click="openMap">附近岗位</button>
          <button class="worker-button" @click="openResume">完善简历</button>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">我的投递</view>
        <view class="more-link" @click="openApplyList">查看全部</view>
      </view>
      <view v-if="applies.length">
        <view v-for="item in applies" :key="item.applyId" class="list-row">
          <view>
            <view class="list-row__title">{{ item.jobTitle || '-' }}</view>
            <view class="list-row__subtitle">{{ item.enterpriseName || '-' }}</view>
          </view>
          <view class="worker-tag">{{ item.statusText || '-' }}</view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无投递记录</view>
        <view class="worker-empty__desc">
          找到合适岗位后可直接投递；如还没开始找岗，建议先完善简历，再返回岗位列表筛选并投递。
        </view>
        <view class="job-empty-actions">
          <button class="worker-button worker-button--secondary" @click="openResume">完善简历</button>
          <button class="worker-button" @click="loadData">继续找岗</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getJobList, getResumeDetail } from '../../api/enterprise-service'

const EXPECTED_JOB_CHAIN_PAGES = ['岗位列表', '岗位详情', '附近岗位地图', '投递记录', '我的简历']

const keyword = ref('')
const total = ref(0)
const jobs = ref([])
const applies = ref([])
const jobTypeOptions = ref([])
const salaryOptions = ref([{ label: '不限薪资', salaryMin: undefined, salaryMax: undefined }])
const radiusOptions = ref([
  { label: '不限距离', value: 0 },
  { label: '3km', value: 3 },
  { label: '5km', value: 5 },
  { label: '10km', value: 10 },
  { label: '20km', value: 20 }
])
const selectedJobType = ref('')
const selectedSalaryMin = ref(undefined)
const selectedSalaryMax = ref(undefined)
const selectedSalaryLabel = ref('不限薪资')
const selectedRadiusKm = ref(0)
const latitude = ref(undefined)
const longitude = ref(undefined)
const locationAvailable = ref(false)
const jobLastLoadedAt = ref('')
const jobLastMessage = ref('')
const resumeExpectedJob = ref('')
const resumeIntro = ref('')
const resumeLoadError = ref('')

const locationTip = computed(() => {
  if (locationAvailable.value) {
    return selectedRadiusKm.value
      ? `已按当前位置 ${selectedRadiusKm.value}km 内筛选岗位`
      : '已获取当前位置，可按距离筛选岗位'
  }
  return '未获取到定位，当前距离筛选将按默认推荐中心计算'
})
const jobChainCoverageText = computed(() => EXPECTED_JOB_CHAIN_PAGES.join(' / '))
const resumeReadinessText = computed(() => {
  const missingFields = []
  if (!String(resumeExpectedJob.value || '').trim()) {
    missingFields.push('期望岗位')
  }
  if (!String(resumeIntro.value || '').trim()) {
    missingFields.push('个人介绍')
  }
  if (!missingFields.length) {
    return `可投递 / ${resumeExpectedJob.value || '已完善'}`
  }
  return `待完善 / 缺少${missingFields.join('、')}`
})
const locationSummaryText = computed(() => {
  if (!locationAvailable.value || latitude.value === undefined || longitude.value === undefined) {
    return '未定位，列表按默认推荐中心计算'
  }
  return `已定位 / ${formatCoordinate(latitude.value)}, ${formatCoordinate(longitude.value)}`
})
const jobFilterSummaryText = computed(() => {
  return [
    keyword.value ? `关键词 ${keyword.value}` : '关键词不限',
    selectedJobType.value ? `工种 ${selectedJobType.value}` : '工种不限',
    selectedSalaryLabel.value || '不限薪资',
    selectedRadiusKm.value ? `${selectedRadiusKm.value}km 内` : '不限距离'
  ].join(' / ')
})
const jobListSummaryText = computed(() => `岗位 ${total.value} 条 / 首页投递 ${applies.value.length} 条`)
const jobConsistencyText = computed(() => {
  const appliedInList = jobs.value.filter((item) => item.applied).length
  if (!jobs.value.length && !applies.value.length) {
    return '当前无岗位且无投递，需结合筛选和真实数据复核'
  }
  if (!jobs.value.length && applies.value.length) {
    return `当前筛选无岗位 / 首页投递摘要 ${applies.value.length} 条`
  }
  if (appliedInList > applies.value.length) {
    return `列表已投递标记 ${appliedInList} 条，大于首页投递摘要 ${applies.value.length} 条`
  }
  if (resumeReadinessText.value.startsWith('待完善') && appliedInList) {
    return `简历待完善，但列表已有 ${appliedInList} 条已投递标记`
  }
  return `列表已投递标记 ${appliedInList} 条 / 首页投递摘要 ${applies.value.length} 条`
})
const jobSnapshotText = computed(() => {
  return [
    '## 求职链验收摘要',
    `- 链路核对：${jobChainCoverageText.value}`,
    `- 最近加载：${jobLastLoadedAt.value || '-'}`,
    `- 定位状态：${locationSummaryText.value}`,
    `- 筛选条件：${jobFilterSummaryText.value}`,
    `- 简历状态：${resumeReadinessText.value}`,
    `- 岗位与投递：${jobListSummaryText.value}`,
    `- 跨页一致性：${jobConsistencyText.value}`,
    `- 说明：${jobLastMessage.value || '-'}`,
    '- 链路范围：岗位列表 / 岗位详情 / 附近岗位地图 / 投递记录'
  ].join('\n')
})

function formatCoordinate(value) {
  const numericValue = Number(value)
  if (!Number.isFinite(numericValue)) {
    return '-'
  }
  return numericValue.toFixed(4)
}

function resolveLocation() {
  return new Promise((resolve) => {
    uni.getLocation({
      type: 'gcj02',
      success: (res) => {
        latitude.value = res.latitude
        longitude.value = res.longitude
        locationAvailable.value = true
        resolve(true)
      },
      fail: () => {
        latitude.value = undefined
        longitude.value = undefined
        locationAvailable.value = false
        resolve(false)
      }
    })
  })
}

async function loadData() {
  try {
    resumeLoadError.value = ''
    const [data, resumeData] = await Promise.all([
      getJobList({
        keyword: keyword.value,
        jobType: selectedJobType.value,
        salaryMin: selectedSalaryMin.value,
        salaryMax: selectedSalaryMax.value,
        latitude: latitude.value,
        longitude: longitude.value,
        radiusKm: selectedRadiusKm.value || undefined
      }),
      getResumeDetail().catch((error) => {
        resumeLoadError.value = error.message || '简历快照加载失败'
        return null
      })
    ])
    jobs.value = data?.rows || []
    applies.value = data?.applies || []
    total.value = data?.total || 0
    jobTypeOptions.value = data?.jobTypeOptions || []
    salaryOptions.value = data?.salaryOptions || salaryOptions.value
    locationAvailable.value = data?.locationAvailable ?? locationAvailable.value
    resumeExpectedJob.value = resumeData?.expectedJob || ''
    resumeIntro.value = resumeData?.intro || ''
    jobLastLoadedAt.value = new Date().toLocaleString()
    const messageParts = []
    if (!jobs.value.length) {
      messageParts.push('当前筛选下暂无匹配岗位')
    }
    if (!applies.value.length) {
      messageParts.push('当前暂无投递记录')
    }
    if (resumeLoadError.value) {
      messageParts.push(`简历快照异常：${resumeLoadError.value}`)
    } else if (resumeReadinessText.value.startsWith('待完善')) {
      messageParts.push('简历仍未满足当前前端投递校验条件')
    }
    const appliedInList = jobs.value.filter((item) => item.applied).length
    if (appliedInList > applies.value.length) {
      messageParts.push(`列表已投递标记 ${appliedInList} 条，超过首页投递摘要 ${applies.value.length} 条`)
    }
    if (!messageParts.length) {
      messageParts.push('岗位列表、简历快照和首页投递摘要已同步加载')
    }
    jobLastMessage.value = messageParts.join('；')
  } catch (error) {
    jobLastLoadedAt.value = new Date().toLocaleString()
    resumeExpectedJob.value = ''
    resumeIntro.value = ''
    resumeLoadError.value = ''
    jobLastMessage.value = error.message || '加载岗位失败'
    uni.showToast({ title: error.message || '加载岗位失败', icon: 'none' })
  }
}

function selectJobType(value) {
  selectedJobType.value = value
  loadData()
}

function selectSalary(item) {
  selectedSalaryLabel.value = item.label
  selectedSalaryMin.value = item.salaryMin
  selectedSalaryMax.value = item.salaryMax
  loadData()
}

function selectRadius(value) {
  selectedRadiusKm.value = value
  loadData()
}

async function relocate() {
  await resolveLocation()
  await loadData()
}

function openDetail(item) {
  if (!item?.jobId) {
    return
  }
  uni.navigateTo({ url: `/pages/job/detail?jobId=${item.jobId}` })
}

function openApplyList() {
  uni.navigateTo({ url: '/pages/job/apply-list' })
}

function openMap() {
  const query = [
    keyword.value ? `keyword=${encodeURIComponent(keyword.value)}` : '',
    selectedJobType.value ? `jobType=${encodeURIComponent(selectedJobType.value)}` : '',
    selectedSalaryMin.value !== undefined && selectedSalaryMin.value !== null ? `salaryMin=${selectedSalaryMin.value}` : '',
    selectedSalaryMax.value !== undefined && selectedSalaryMax.value !== null ? `salaryMax=${selectedSalaryMax.value}` : '',
    selectedRadiusKm.value ? `radiusKm=${selectedRadiusKm.value}` : ''
  ].filter(Boolean).join('&')
  uni.navigateTo({ url: `/pages/job/map${query ? `?${query}` : ''}` })
}

function openResume() {
  uni.navigateTo({ url: '/pages/profile/resume' })
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

onShow(async () => {
  resumeLoadError.value = ''
  await resolveLocation()
  await loadData()
})
</script>

<style lang="scss">
.job-empty-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 18rpx;
}

.job-empty-actions button {
  flex: 1;
}
</style>
