<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page enterprise-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">设备台账</view>
      <view class="worker-subtitle">查看在线状态、故障设备和授权记录。企业端提交的是设备处理登记，不伪造即时闭环。</view>
      <view class="enterprise-hero__meta">
        <view class="worker-tag worker-tag--notice">已选 {{ selectedCount }} 台</view>
        <view class="worker-tag">{{ displayedDevices.length }} 台可见设备</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="enterprise-kpi-grid">
        <view v-for="item in stats" :key="item.label" class="enterprise-kpi">
          <view class="enterprise-kpi__label">{{ item.label }}</view>
          <view class="enterprise-kpi__value">{{ item.value }}</view>
          <view class="enterprise-kpi__desc">{{ item.desc }}</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="enterprise-list-head">
        <view>
          <view class="worker-title">筛选与操作</view>
          <view class="enterprise-list-head__meta">优先筛出离线设备和异常设备，统一登记授权或报修动作。</view>
        </view>
        <view class="worker-tag worker-tag--info">{{ activeFilterLabel }}</view>
      </view>
      <view class="filter-row people-filter-row">
        <view
          v-for="item in filterOptions"
          :key="item.key"
          class="filter-chip"
          :class="{ 'filter-chip--active': item.key === activeFilter }"
          @click="handleFilterChange(item.key)"
        >
          {{ item.label }}
        </view>
      </view>
      <view class="worker-button-row">
        <button class="worker-button worker-button--secondary" @click="selectOfflineDevices">选中离线设备</button>
        <button class="worker-button worker-button--ghost" :disabled="exporting" @click="handleExport">
          {{ exporting ? '导出中...' : '导出台账' }}
        </button>
      </view>
      <view v-if="lastAction.message" class="enterprise-action-feedback">
        <view class="enterprise-action-feedback__title">{{ lastAction.title }}</view>
        <view class="enterprise-action-feedback__desc">{{ lastAction.message }}</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="enterprise-list-head">
        <view>
          <view class="worker-title">设备列表</view>
          <view class="enterprise-list-head__meta">点击条目勾选，设备授权和故障报修将返回最近登记结果。</view>
        </view>
        <view class="worker-tag">{{ displayedDevices.length }} 台</view>
      </view>
      <view
        v-for="item in displayedDevices"
        :key="item.id"
        class="record-row record-row--selectable"
        :class="{ 'record-row--selected': isSelected(item.id) }"
        @click="toggleSelection(item.id)"
      >
        <view class="record-row__main">
          <view class="record-row__title">{{ item.name }}</view>
          <view class="record-row__subtitle">{{ item.code }} / {{ item.location }}</view>
          <view class="enterprise-record-meta">
            <view class="enterprise-record-meta__item">状态 {{ item.status }}</view>
            <view class="enterprise-record-meta__item">{{ isSelected(item.id) ? '已纳入本次处理' : '点击后加入处理' }}</view>
          </view>
        </view>
        <view class="record-row__aside">
          <view class="worker-tag" :class="item.className">{{ item.status }}</view>
          <view class="record-row__select-indicator">{{ isSelected(item.id) ? '已选' : '选择' }}</view>
        </view>
      </view>
      <view v-if="!displayedDevices.length" class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前筛选条件下暂无设备</view>
        <view class="worker-empty__desc">已保留企业空态，后续继续回写真实设备在线和授权状态。</view>
      </view>
      <view class="worker-button-row">
        <button class="worker-button" :disabled="submitting" @click="handleDeviceAction('authorize', '设备授权')">
          {{ submitting ? '处理中...' : '设备授权' }}
        </button>
        <button class="worker-button worker-button--secondary" :disabled="submitting" @click="handleDeviceAction('repair', '故障报修')">
          故障报修
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { exportEnterpriseDeviceLedger, getEnterpriseDeviceLedger, submitEnterpriseDeviceAction } from '../../api/enterprise'

const devices = ref([])
const selectedIds = ref([])
const activeFilter = ref('all')
const submitting = ref(false)
const exporting = ref(false)
const lastAction = ref({ title: '', message: '' })

const filterOptions = [
  { key: 'all', label: '全部' },
  { key: 'online', label: '在线' },
  { key: 'offline', label: '离线' }
]

const displayedDevices = computed(() => {
  if (activeFilter.value === 'online') {
    return devices.value.filter((item) => item.status === '在线')
  }
  if (activeFilter.value === 'offline') {
    return devices.value.filter((item) => item.status === '离线')
  }
  return devices.value
})
const selectedCount = computed(() => selectedIds.value.length)
const activeFilterLabel = computed(() => filterOptions.find((item) => item.key === activeFilter.value)?.label || '全部')
const stats = computed(() => {
  const onlineCount = devices.value.filter((item) => item.status === '在线').length
  const offlineCount = devices.value.filter((item) => item.status === '离线').length
  return [
    { label: '设备总数', value: String(devices.value.length), desc: '企业当前纳入台账的设备数量' },
    { label: '在线设备', value: String(onlineCount), desc: '在线状态由后端台账实时汇总返回' },
    { label: '离线设备', value: String(offlineCount), desc: offlineCount ? '建议优先登记报修或授权排查' : '当前未发现离线设备' }
  ]
})

function notify(message) {
  uni.showToast({ title: message, icon: 'none' })
}

function isSelected(id) {
  return selectedIds.value.includes(id)
}

function toggleSelection(id) {
  if (isSelected(id)) {
    selectedIds.value = selectedIds.value.filter((item) => item !== id)
    return
  }
  selectedIds.value = [...selectedIds.value, id]
}

function handleFilterChange(key) {
  activeFilter.value = key
  selectedIds.value = []
}

function selectOfflineDevices() {
  selectedIds.value = displayedDevices.value.filter((item) => item.status === '离线').map((item) => item.id)
}

async function loadData() {
  const data = await getEnterpriseDeviceLedger()
  devices.value = Array.isArray(data.list) ? data.list : []
}

async function handleDeviceAction(actionType, title) {
  if (!selectedIds.value.length) {
    notify('请先勾选需要处理的设备')
    return
  }
  submitting.value = true
  try {
    const result = await submitEnterpriseDeviceAction({
      actionType,
      deviceIds: selectedIds.value
    })
    lastAction.value = {
      title: `${title}已登记`,
      message: result.message || `已登记 ${selectedIds.value.length} 台设备，待后台继续流转。`
    }
    notify(result.message || '设备处理已提交')
  } finally {
    submitting.value = false
  }
}

async function handleExport() {
  exporting.value = true
  try {
    const result = await exportEnterpriseDeviceLedger({
      filter: activeFilter.value,
      deviceIds: selectedIds.value
    })
    lastAction.value = {
      title: '设备台账导出已登记',
      message: result.message || '导出任务已进入企业台账，可在管理端继续跟进。'
    }
    notify(result.message || '设备台账导出已提交')
  } finally {
    exporting.value = false
  }
}

onMounted(() => {
  loadData().catch((error) => {
    notify(error.message || '加载设备台账失败')
  })
})
</script>

<style lang="scss">
.people-filter-row {
  flex-wrap: wrap;
}

.record-row--selectable {
  cursor: pointer;
}

.record-row--selected {
  background: rgba(15, 118, 110, 0.05);
  border-radius: 20rpx;
  padding-left: 16rpx;
  padding-right: 16rpx;
}

.record-row__main {
  min-width: 0;
  flex: 1;
}

.record-row__aside {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 10rpx;
  flex-shrink: 0;
}

.record-row__select-indicator {
  font-size: 22rpx;
  color: $ygb-primary;
}
</style>
