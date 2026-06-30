<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">设备台账</view>
      <view class="worker-subtitle">集中查看考勤设备、高危设备和在线状态，后续统一对接企业设备授权与回写接口。</view>
      <view v-if="tipText" class="worker-subtitle worker-subtitle--progress">{{ tipText }}</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">筛选与操作</view>
        <view class="worker-tag">{{ selectedCount }} 已勾选</view>
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
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">设备列表</view>
        <view class="worker-caption">点击条目可勾选，优先处理离线设备和授权记录。</view>
      </view>
      <view
        class="record-row record-row--selectable"
        :class="{ 'record-row--selected': isSelected(item.id) }"
        v-for="item in displayedDevices"
        :key="item.id"
        @click="toggleSelection(item.id)"
      >
        <view class="record-row__main">
          <view class="record-row__title">{{ item.name }}</view>
          <view class="record-row__subtitle">{{ item.code }} / {{ item.location }}</view>
        </view>
        <view class="record-row__aside">
          <view class="worker-tag" :class="item.className">{{ item.status }}</view>
          <view class="record-row__select-indicator">{{ isSelected(item.id) ? '已选' : '选择' }}</view>
        </view>
      </view>
      <view v-if="!displayedDevices.length" class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前筛选条件下暂无设备</view>
        <view class="worker-empty__desc">后续真实设备台账接口接通后，会回写在线状态和授权记录。</view>
      </view>
      <view class="worker-button-row">
        <button class="worker-button" :disabled="submitting" @click="handleDeviceAction('authorize')">
          {{ submitting ? '处理中...' : '授权记录' }}
        </button>
        <button class="worker-button worker-button--secondary" :disabled="submitting" @click="handleDeviceAction('repair')">
          故障报修
        </button>
      </view>
      <view class="worker-button-row">
        <button class="worker-button worker-button--ghost" :disabled="exporting" @click="handleExport">
          {{ exporting ? '导出中...' : '导出台账' }}
        </button>
        <button class="worker-button worker-button--secondary" @click="selectOfflineDevices">
          一键选择离线
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import {
  exportEnterpriseDeviceLedger,
  getEnterpriseDeviceLedger,
  submitEnterpriseDeviceAction
} from '../../api/enterprise'

const devices = ref([])
const selectedIds = ref([])
const activeFilter = ref('all')
const submitting = ref(false)
const exporting = ref(false)
const tip = ref('')

const filterOptions = [
  { key: 'all', label: '全部' },
  { key: 'online', label: '在线' },
  { key: 'offline', label: '离线' }
]

const tipText = computed(() => tip.value)
const selectedCount = computed(() => selectedIds.value.length)

const displayedDevices = computed(() => {
  if (activeFilter.value === 'online') {
    return devices.value.filter((item) => item.status === '在线')
  }
  if (activeFilter.value === 'offline') {
    return devices.value.filter((item) => item.status === '离线')
  }
  return devices.value
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
  selectedIds.value = displayedDevices.value
    .filter((item) => item.status === '离线')
    .map((item) => item.id)
}

async function loadData() {
  const data = await getEnterpriseDeviceLedger()
  devices.value = Array.isArray(data.list) ? data.list : []
  tip.value = ''
}

async function handleDeviceAction(actionType) {
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
    notify(result.message || '设备台账导出已提交')
  } finally {
    exporting.value = false
  }
}

onMounted(() => {
  loadData()
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
