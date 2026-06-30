<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">工资确认</view>
      <view class="worker-subtitle">保留企业端工资批次确认、明细导入和后续支付申请接口位，本轮先做前端可联调闭环。</view>
      <view v-if="tipText" class="worker-subtitle worker-subtitle--progress">{{ tipText }}</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">批次概览</view>
        <view class="worker-tag worker-tag--notice">已选 {{ selectedCount }} 批</view>
      </view>
      <view class="summary-grid summary-grid--three">
        <view class="summary-item" v-for="item in summary" :key="item.label">
          <view class="summary-item__label">{{ item.label }}</view>
          <view class="summary-item__value">{{ item.value }}</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">工资批次</view>
        <view class="worker-caption">点击条目可勾选，统一提交确认或导入补充明细。</view>
      </view>
      <view
        class="record-row record-row--selectable"
        :class="{ 'record-row--selected': isSelected(item.id) }"
        v-for="item in batches"
        :key="item.id"
        @click="toggleSelection(item.id)"
      >
        <view class="record-row__main">
          <view class="record-row__title">{{ item.month }} / {{ item.title }}</view>
          <view class="record-row__subtitle">{{ item.people }} 人 / 总额 {{ item.amount }}</view>
        </view>
        <view class="record-row__aside">
          <view class="worker-tag" :class="item.className">{{ item.status }}</view>
          <view class="record-row__select-indicator">{{ isSelected(item.id) ? '已选' : '选择' }}</view>
        </view>
      </view>
      <view v-if="!batches.length" class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无工资批次</view>
        <view class="worker-empty__desc">后续真实工资聚合接口接通后，会回写待确认和待复核批次。</view>
      </view>
      <view class="worker-button-row">
        <button class="worker-button" :disabled="submitting" @click="handleSubmitConfirm">
          {{ submitting ? '提交中...' : '提交确认' }}
        </button>
        <button class="worker-button worker-button--secondary" :disabled="importing" @click="handleImportDraft">
          {{ importing ? '处理中...' : '导入明细' }}
        </button>
      </view>
      <view class="worker-button-row">
        <button class="worker-button worker-button--ghost" @click="selectAllBatches">
          全选批次
        </button>
        <button class="worker-button worker-button--secondary" @click="clearSelection">
          清空选择
        </button>
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
const loading = ref(false)
const submitting = ref(false)
const importing = ref(false)
const tip = ref('')

const tipText = computed(() => tip.value)
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
  loading.value = true
  try {
    const data = await getSalaryConfirmDashboard()
    summary.value = Array.isArray(data.summary) ? data.summary : []
    batches.value = Array.isArray(data.batches) ? data.batches : []
    tip.value = ''
  } finally {
    loading.value = false
  }
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
    notify(result.message || '工资确认已提交')
  } finally {
    submitting.value = false
  }
}

async function handleImportDraft() {
  importing.value = true
  try {
    const result = await importSalaryDraft({
      source: 'frontend-placeholder',
      batchMonth: batches.value.find((item) => selectedIds.value.includes(item.id))?.month || batches.value[0]?.month || ''
    })
    notify(result.message || '导入入口已保留')
  } finally {
    importing.value = false
  }
}

onMounted(() => {
  loadData()
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
