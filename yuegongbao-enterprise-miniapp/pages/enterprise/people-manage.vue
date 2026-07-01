<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page enterprise-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">人员管理</view>
      <view class="worker-subtitle">统一处理花名册、证件临期和参保核验，动作只落企业台账，不伪装成员工本人办理。</view>
      <view class="enterprise-hero__meta">
        <view class="worker-tag worker-tag--notice">已选 {{ selectedCount }} 人</view>
        <view class="worker-tag">{{ displayedPeople.length }} 条可见记录</view>
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
          <view class="worker-title">筛选与处理</view>
          <view class="enterprise-list-head__meta">按在职、证件临期和待参保快速切换，选中后统一登记企业动作。</view>
        </view>
        <view class="worker-tag worker-tag--info">{{ activeFilterLabel }}</view>
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
      <view class="worker-button-row">
        <button class="worker-button worker-button--secondary" @click="fillPrioritySelection">选中待处理</button>
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
          <view class="worker-title">员工花名册</view>
          <view class="enterprise-list-head__meta">点击条目勾选。证件提醒和参保核验会返回最近登记结果。</view>
        </view>
        <view class="worker-tag">{{ displayedPeople.length }} 条</view>
      </view>
      <view
        v-for="item in displayedPeople"
        :key="item.id"
        class="record-row record-row--selectable"
        :class="{ 'record-row--selected': isSelected(item.id) }"
        @click="toggleSelection(item.id)"
      >
        <view class="record-row__main">
          <view class="record-row__title">{{ item.name }} / {{ item.job }}</view>
          <view class="record-row__subtitle">{{ item.status }} / {{ item.certificate }} / {{ item.insurance }}</view>
          <view class="enterprise-record-meta">
            <view class="enterprise-record-meta__item">状态 {{ item.tag }}</view>
            <view class="enterprise-record-meta__item">{{ isSelected(item.id) ? '已纳入本次处理' : '点击后加入处理' }}</view>
          </view>
        </view>
        <view class="record-row__aside">
          <view class="worker-tag" :class="item.className">{{ item.tag }}</view>
          <view class="record-row__select-indicator">{{ isSelected(item.id) ? '已选' : '选择' }}</view>
        </view>
      </view>
      <view v-if="!displayedPeople.length" class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前筛选条件下暂无人员</view>
        <view class="worker-empty__desc">已保留企业空态，后续由真实人员台账继续回写数据。</view>
      </view>
      <view class="worker-button-row">
        <button class="worker-button" :disabled="submitting" @click="handlePeopleAction('certificate-remind', '证件提醒')">
          {{ submitting ? '处理中...' : '证件提醒' }}
        </button>
        <button class="worker-button worker-button--secondary" :disabled="submitting" @click="handlePeopleAction('insurance-check', '参保核验')">
          参保核验
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { exportEnterprisePeopleLedger, getEnterprisePeopleLedger, submitEnterprisePeopleAction } from '../../api/enterprise'

const filters = ref([])
const activeFilter = ref('all')
const people = ref([])
const selectedIds = ref([])
const submitting = ref(false)
const exporting = ref(false)
const lastAction = ref({ title: '', message: '' })

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
      return String(item.insurance || '').includes('待参保')
    }
    return true
  })
})
const selectedCount = computed(() => selectedIds.value.length)
const activeFilterLabel = computed(() => filters.value.find((item) => item.key === activeFilter.value)?.label || '全部')
const stats = computed(() => {
  const activeCount = people.value.filter((item) => item.status === '在职').length
  const dueCount = people.value.filter((item) => item.tag === '临期').length
  const insurancePendingCount = people.value.filter((item) => String(item.insurance || '').includes('待参保')).length
  return [
    { label: '在岗人数', value: String(activeCount), desc: '当前可参与企业现场作业的人数' },
    { label: '证件临期', value: String(dueCount), desc: dueCount ? '需优先触发提醒或复核' : '当前无临期证件提醒' },
    { label: '待参保', value: String(insurancePendingCount), desc: '企业端仅发起核验登记，不直接代办个人参保' }
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

function fillPrioritySelection() {
  selectedIds.value = displayedPeople.value
    .filter((item) => item.tag !== '正常')
    .map((item) => item.id)
}

async function loadData() {
  const data = await getEnterprisePeopleLedger()
  filters.value = Array.isArray(data.filters) ? data.filters : []
  activeFilter.value = data.activeFilter || 'all'
  people.value = Array.isArray(data.list) ? data.list : []
}

async function handlePeopleAction(actionType, title) {
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
    lastAction.value = {
      title: `${title}已登记`,
      message: result.message || `已登记 ${selectedIds.value.length} 人，待后台继续流转。`
    }
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
    lastAction.value = {
      title: '花名册导出已登记',
      message: result.message || '导出任务已进入企业台账，可在管理端继续跟进。'
    }
    notify(result.message || '花名册导出已提交')
  } finally {
    exporting.value = false
  }
}

onMounted(() => {
  loadData().catch((error) => {
    notify(error.message || '加载人员台账失败')
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
