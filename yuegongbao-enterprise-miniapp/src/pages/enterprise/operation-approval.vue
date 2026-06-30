<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">作业审批</view>
      <view class="worker-subtitle">承接外出作业申请、高危授权和现场条件确认，当前先保留前端审批动作与导出台账接口位。</view>
      <view v-if="tipText" class="worker-subtitle worker-subtitle--progress">{{ tipText }}</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">待审批列表</view>
        <view class="worker-tag worker-tag--warning">待选 {{ selectedCount }} 条</view>
      </view>
      <view
        class="record-row record-row--selectable"
        :class="{ 'record-row--selected': isSelected(item.id) }"
        v-for="item in approvals"
        :key="item.id"
        @click="toggleSelection(item.id)"
      >
        <view class="record-row__main">
          <view class="record-row__title">{{ item.title }}</view>
          <view class="record-row__subtitle">{{ item.location }} / {{ item.time }} / 监护人 {{ item.guardian }}</view>
        </view>
        <view class="record-row__aside">
          <view class="worker-tag worker-tag--warning">{{ item.status }}</view>
          <view class="record-row__select-indicator">{{ isSelected(item.id) ? '已选' : '选择' }}</view>
        </view>
      </view>
      <view v-if="!approvals.length" class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前没有待审批记录</view>
        <view class="worker-empty__desc">后续真实审批接口接通后，会回写外出作业申请和高危授权记录。</view>
      </view>
      <view class="worker-button-row">
        <button class="worker-button" :disabled="approving" @click="handleBatchDecision('approved')">
          {{ approving ? '审批中...' : '批量通过' }}
        </button>
        <button class="worker-button worker-button--danger" :disabled="approving" @click="handleBatchDecision('rejected')">
          驳回申请
        </button>
      </view>
      <view class="worker-button-row">
        <button class="worker-button worker-button--secondary" @click="selectAllApprovals">
          全选申请
        </button>
        <button class="worker-button worker-button--ghost" :disabled="exporting" @click="handleExportLedger">
          {{ exporting ? '导出中...' : '导出台账' }}
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { exportOperationLedger, getOperationApprovalDashboard, submitOperationApproval } from '../../api/enterprise'

const approvals = ref([])
const selectedIds = ref([])
const approving = ref(false)
const exporting = ref(false)
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

function selectAllApprovals() {
  selectedIds.value = approvals.value.map((item) => item.id)
}

async function loadData() {
  const data = await getOperationApprovalDashboard()
  approvals.value = Array.isArray(data.list) ? data.list : []
  tip.value = ''
}

async function handleBatchDecision(decision) {
  if (!selectedIds.value.length) {
    notify('请先选择需要审批的作业申请')
    return
  }
  approving.value = true
  try {
    const result = await submitOperationApproval({
      approvalIds: selectedIds.value,
      decision
    })
    notify(result.message || '审批已提交')
  } finally {
    approving.value = false
  }
}

async function handleExportLedger() {
  exporting.value = true
  try {
    const result = await exportOperationLedger()
    notify(result.message || '审批台账已导出')
  } finally {
    exporting.value = false
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
