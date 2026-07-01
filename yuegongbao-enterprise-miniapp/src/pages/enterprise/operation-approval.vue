<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page enterprise-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">作业审批</view>
      <view class="worker-subtitle">审批结果真实回写作业记录状态。台账导出仍是登记式闭环，前端不做假成功。</view>
      <view class="enterprise-hero__meta">
        <view class="worker-tag worker-tag--warning">已选 {{ selectedCount }} 条</view>
        <view class="worker-tag">{{ approvals.length }} 条待处理记录</view>
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
          <view class="worker-title">审批列表</view>
          <view class="enterprise-list-head__meta">批量通过和驳回会真实更新审批状态，导出台账仅登记导出任务。</view>
        </view>
        <view class="worker-tag">{{ approvals.length }} 条</view>
      </view>
      <view
        v-for="item in approvals"
        :key="item.id"
        class="record-row record-row--selectable"
        :class="{ 'record-row--selected': isSelected(item.id) }"
        @click="toggleSelection(item.id)"
      >
        <view class="record-row__main">
          <view class="record-row__title">{{ item.title }}</view>
          <view class="record-row__subtitle">{{ item.location }} / {{ item.time }} / 监护人 {{ item.guardian }}</view>
          <view class="enterprise-record-meta">
            <view class="enterprise-record-meta__item">状态 {{ item.status }}</view>
            <view class="enterprise-record-meta__item">{{ isSelected(item.id) ? '已纳入本次审批' : '点击后加入审批' }}</view>
          </view>
        </view>
        <view class="record-row__aside">
          <view class="worker-tag worker-tag--warning">{{ item.status }}</view>
          <view class="record-row__select-indicator">{{ isSelected(item.id) ? '已选' : '选择' }}</view>
        </view>
      </view>
      <view v-if="!approvals.length" class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前没有待审批记录</view>
        <view class="worker-empty__desc">已保留企业空态，后续继续由真实作业申请回写审批台账。</view>
      </view>
      <view class="worker-button-row">
        <button class="worker-button" :disabled="approving" @click="handleBatchDecision('approved', '批量通过')">
          {{ approving ? '审批中...' : '批量通过' }}
        </button>
        <button class="worker-button worker-button--danger" :disabled="approving" @click="handleBatchDecision('rejected', '驳回申请')">
          驳回申请
        </button>
      </view>
      <view class="worker-button-row">
        <button class="worker-button worker-button--secondary" @click="selectAllApprovals">全选申请</button>
        <button class="worker-button worker-button--ghost" :disabled="exporting" @click="handleExportLedger">
          {{ exporting ? '导出中...' : '导出台账' }}
        </button>
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
import { exportOperationLedger, getOperationApprovalDashboard, submitOperationApproval } from '../../api/enterprise'

const approvals = ref([])
const selectedIds = ref([])
const approving = ref(false)
const exporting = ref(false)
const lastAction = ref({ title: '', message: '' })

const selectedCount = computed(() => selectedIds.value.length)
const stats = computed(() => {
  const pendingCount = approvals.value.filter((item) => item.status !== '已通过').length
  return [
    { label: '待审批', value: String(pendingCount), desc: '当前仍需企业管理员处理的作业申请' },
    { label: '全部记录', value: String(approvals.value.length), desc: '本页展示企业端当前可见审批记录' },
    { label: '本次选择', value: String(selectedIds.value.length), desc: '批量审批仅对选中记录生效' }
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

function selectAllApprovals() {
  selectedIds.value = approvals.value.map((item) => item.id)
}

async function loadData() {
  const data = await getOperationApprovalDashboard()
  approvals.value = Array.isArray(data.list) ? data.list : []
}

async function handleBatchDecision(decision, title) {
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
    lastAction.value = {
      title: `${title}已提交`,
      message: result.message || `已提交 ${selectedIds.value.length} 条审批结果。`
    }
    notify(result.message || '审批已提交')
    await loadData()
  } finally {
    approving.value = false
  }
}

async function handleExportLedger() {
  exporting.value = true
  try {
    const result = await exportOperationLedger()
    lastAction.value = {
      title: '审批台账导出已登记',
      message: result.message || '导出任务已进入企业台账，可在管理端继续跟进。'
    }
    notify(result.message || '审批台账已导出')
  } finally {
    exporting.value = false
  }
}

onMounted(() => {
  loadData().catch((error) => {
    notify(error.message || '加载审批台账失败')
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
