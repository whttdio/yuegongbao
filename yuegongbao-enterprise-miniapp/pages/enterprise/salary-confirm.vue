<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page enterprise-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">工资确认</view>
      <view class="worker-subtitle">工资确认和提交走真实批次流程。导入明细仅登记草稿，不伪装成已完成导入。</view>
      <view class="enterprise-hero__meta">
        <view class="worker-tag worker-tag--notice">已选 {{ selectedCount }} 批</view>
        <view class="worker-tag">{{ batches.length }} 个批次</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="enterprise-kpi-grid">
        <view v-for="item in summary" :key="item.label" class="enterprise-kpi">
          <view class="enterprise-kpi__label">{{ item.label }}</view>
          <view class="enterprise-kpi__value">{{ item.value }}</view>
          <view class="enterprise-kpi__desc">{{ item.desc || '来自后端工资批次汇总' }}</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="enterprise-list-head">
        <view>
          <view class="worker-title">工资批次</view>
          <view class="enterprise-list-head__meta">点击条目勾选，确认提交流程真实落后端，导入明细仅登记为待后台补单。</view>
        </view>
        <view class="worker-tag">{{ batches.length }} 批</view>
      </view>
      <view
        v-for="item in batches"
        :key="item.id"
        class="record-row record-row--selectable"
        :class="{ 'record-row--selected': isSelected(item.id) }"
        @click="toggleSelection(item.id)"
      >
        <view class="record-row__main">
          <view class="record-row__title">{{ item.month }} / {{ item.title }}</view>
          <view class="record-row__subtitle">{{ item.people }} 人 / 总额 {{ item.amount }}</view>
          <view class="enterprise-record-meta">
            <view class="enterprise-record-meta__item">状态 {{ item.status }}</view>
            <view class="enterprise-record-meta__item">{{ isSelected(item.id) ? '已纳入本次提交' : '点击后加入确认' }}</view>
          </view>
        </view>
        <view class="record-row__aside">
          <view class="worker-tag" :class="item.className">{{ item.status }}</view>
          <view class="record-row__select-indicator">{{ isSelected(item.id) ? '已选' : '选择' }}</view>
        </view>
      </view>
      <view v-if="!batches.length" class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无工资批次</view>
        <view class="worker-empty__desc">已保留企业空态，后续继续从真实工资批次回写待确认数据。</view>
      </view>
      <view class="worker-button-row">
        <button class="worker-button" :disabled="submitting" @click="handleSubmitConfirm">
          {{ submitting ? '提交中...' : '确认并提交' }}
        </button>
        <button class="worker-button worker-button--secondary" :disabled="importing" @click="handleImportDraft">
          {{ importing ? '处理中...' : '登记导入草稿' }}
        </button>
      </view>
      <view class="worker-button-row">
        <button class="worker-button worker-button--ghost" @click="selectAllBatches">全选批次</button>
        <button class="worker-button worker-button--secondary" @click="clearSelection">清空选择</button>
      </view>
      <view v-if="lastAction.message" class="enterprise-action-feedback">
        <view class="enterprise-action-feedback__title">{{ lastAction.title }}</view>
        <view class="enterprise-action-feedback__desc">{{ lastAction.message }}</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getSalaryConfirmDashboard, importSalaryDraft, submitSalaryConfirm } from '../../api/enterprise'

const summary = ref([])
const batches = ref([])
const selectedIds = ref([])
const submitting = ref(false)
const importing = ref(false)
const lastAction = ref({ title: '', message: '' })

const selectedCount = computed(() => selectedIds.value.length)

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

function selectAllBatches() {
  selectedIds.value = batches.value.map((item) => item.id)
}

function clearSelection() {
  selectedIds.value = []
}

async function loadData() {
  const data = await getSalaryConfirmDashboard()
  summary.value = Array.isArray(data.summary) ? data.summary : []
  batches.value = Array.isArray(data.batches) ? data.batches : []
}

async function handleSubmitConfirm() {
  if (!selectedIds.value.length) {
    notify('请先选择需要确认的工资批次')
    return
  }
  submitting.value = true
  try {
    const result = await submitSalaryConfirm({
      batchIds: selectedIds.value
    })
    lastAction.value = {
      title: '工资确认已提交',
      message: result.message || `已提交 ${selectedIds.value.length} 个工资批次。`
    }
    notify(result.message || '工资确认已提交')
    await loadData()
  } finally {
    submitting.value = false
  }
}

async function handleImportDraft() {
  importing.value = true
  try {
    const result = await importSalaryDraft({
      source: 'enterprise-h5',
      batchMonth: batches.value.find((item) => selectedIds.value.includes(item.id))?.month || batches.value[0]?.month || ''
    })
    lastAction.value = {
      title: '导入草稿已登记',
      message: result.message || '导入草稿已登记，待后台补单流转。'
    }
    notify(result.message || '导入草稿已登记')
  } finally {
    importing.value = false
  }
}

onMounted(() => {
  loadData().catch((error) => {
    notify(error.message || '加载工资批次失败')
  })
})
</script>

<style lang="scss">
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
