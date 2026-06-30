<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">人员管理</view>
      <view class="worker-subtitle">承接员工花名册、入离场登记、证件管理和参保核验，先统一收口到企业台账接口层。</view>
      <view v-if="tipText" class="worker-subtitle worker-subtitle--progress">{{ tipText }}</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">筛选视图</view>
        <view class="worker-tag">{{ selectedCount }} 已勾选</view>
      </view>
      <view class="filter-row people-filter-row">
        <view
          v-for="item in filters"
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
        <view class="worker-title">员工花名册</view>
        <view class="worker-caption">点击条目可勾选，优先处理证件临期和待参保人员。</view>
      </view>
      <view
        class="record-row record-row--selectable"
        :class="{ 'record-row--selected': isSelected(item.id) }"
        v-for="item in displayedPeople"
        :key="item.id"
        @click="toggleSelection(item.id)"
      >
        <view class="record-row__main">
          <view class="record-row__title">{{ item.name }} / {{ item.job }}</view>
          <view class="record-row__subtitle">{{ item.status }} / {{ item.certificate }} / {{ item.insurance }}</view>
        </view>
        <view class="record-row__aside">
          <view class="worker-tag" :class="item.className">{{ item.tag }}</view>
          <view class="record-row__select-indicator">{{ isSelected(item.id) ? '已选' : '选择' }}</view>
        </view>
      </view>
      <view v-if="!displayedPeople.length" class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前筛选条件下暂无人员</view>
        <view class="worker-empty__desc">可切换到其他筛选条件，或等待后续真实接口回写人员台账。</view>
      </view>
      <view class="worker-button-row">
        <button class="worker-button" :disabled="submitting" @click="handlePeopleAction('certificate-remind')">
          {{ submitting ? '处理中...' : '证件提醒' }}
        </button>
        <button class="worker-button worker-button--secondary" :disabled="submitting" @click="handlePeopleAction('insurance-check')">
          参保核验
        </button>
      </view>
      <view class="worker-button-row">
        <button class="worker-button worker-button--ghost" :disabled="exporting" @click="handleExport">
          {{ exporting ? '导出中...' : '导出花名册' }}
        </button>
        <button class="worker-button worker-button--secondary" @click="fillDemoSelection">
          一键选择待处理
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import {
  exportEnterprisePeopleLedger,
  getEnterprisePeopleLedger,
  submitEnterprisePeopleAction
} from '../../api/enterprise'

const filters = ref([])
const activeFilter = ref('all')
const people = ref([])
const selectedIds = ref([])
const submitting = ref(false)
const exporting = ref(false)
const tip = ref('')

const tipText = computed(() => tip.value)
const selectedCount = computed(() => selectedIds.value.length)

const displayedPeople = computed(() => {
  if (activeFilter.value === 'all') {
    return people.value
  }
  return people.value.filter((item) => {
    if (activeFilter.value === 'active') {
      return item.status === '在职'
    }
    if (activeFilter.value === 'certificateDue') {
      return item.tag === '临期'
    }
    if (activeFilter.value === 'insurancePending') {
      return item.insurance.includes('待参保')
    }
    return true
  })
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

function fillDemoSelection() {
  selectedIds.value = displayedPeople.value
    .filter((item) => item.tag !== '正常')
    .map((item) => item.id)
}

async function loadData() {
  const data = await getEnterprisePeopleLedger()
  filters.value = Array.isArray(data.filters) ? data.filters : []
  activeFilter.value = data.activeFilter || 'all'
  people.value = Array.isArray(data.list) ? data.list : []
  tip.value = ''
}

async function handlePeopleAction(actionType) {
  if (!selectedIds.value.length) {
    notify('请先勾选需要处理的人员')
    return
  }
  submitting.value = true
  try {
    const result = await submitEnterprisePeopleAction({
      actionType,
      personIds: selectedIds.value
    })
    notify(result.message || '人员处理已提交')
  } finally {
    submitting.value = false
  }
}

async function handleExport() {
  exporting.value = true
  try {
    const result = await exportEnterprisePeopleLedger({
      filter: activeFilter.value,
      personIds: selectedIds.value
    })
    notify(result.message || '花名册导出已提交')
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
