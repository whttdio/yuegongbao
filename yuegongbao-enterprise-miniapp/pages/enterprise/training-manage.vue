<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page enterprise-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">培训管理</view>
      <view class="worker-subtitle">培训概览和计划草稿展示真实数据。督办和补发考试仅登记企业动作，不伪装成培训平台已执行。</view>
      <view class="enterprise-hero__meta">
        <view class="worker-tag worker-tag--info">已选 {{ selectedCount }} 个计划</view>
        <view class="worker-tag">{{ plans.length }} 个计划</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="enterprise-kpi-grid">
        <view v-for="item in summary" :key="item.label" class="enterprise-kpi">
          <view class="enterprise-kpi__label">{{ item.label }}</view>
          <view class="enterprise-kpi__value">{{ item.value }}</view>
          <view class="enterprise-kpi__desc">企业培训汇总指标</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="enterprise-notice">
        <view class="enterprise-notice__head">
          <view class="enterprise-notice__title">办理边界说明</view>
          <view class="worker-tag worker-tag--warning">登记流转</view>
        </view>
        <view class="enterprise-notice__desc">保存计划、督办未完成和补发考试任务都会返回登记结果，但不代表培训系统已即时执行。</view>
        <view class="enterprise-notice__reason">企业端只保留可落地的计划草稿和动作登记，后续仍需后台继续流转。</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="enterprise-list-head">
        <view>
          <view class="worker-title">培训计划</view>
          <view class="enterprise-list-head__meta">点击条目勾选，培训动作只回写企业登记状态。</view>
        </view>
        <view class="worker-tag">{{ plans.length }} 项</view>
      </view>
      <view
        v-for="item in plans"
        :key="item.id"
        class="record-row record-row--selectable"
        :class="{ 'record-row--selected': isSelected(item.id) }"
        @click="toggleSelection(item.id)"
      >
        <view class="record-row__main">
          <view class="record-row__title">{{ item.title }}</view>
          <view class="record-row__subtitle">{{ item.desc }}</view>
          <view class="enterprise-record-meta">
            <view class="enterprise-record-meta__item">进度 {{ item.progress }}</view>
            <view class="enterprise-record-meta__item">{{ isSelected(item.id) ? '已纳入本次处理' : '点击后加入处理' }}</view>
          </view>
        </view>
        <view class="record-row__aside">
          <view class="worker-tag worker-tag--info">{{ item.progress }}</view>
          <view class="record-row__select-indicator">{{ isSelected(item.id) ? '已选' : '选择' }}</view>
        </view>
      </view>
      <view v-if="!plans.length" class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无培训计划</view>
        <view class="worker-empty__desc">已保留企业空态，后续继续由真实计划和动作记录回写。</view>
      </view>
      <view class="worker-button-row">
        <button class="worker-button" :disabled="saving" @click="handleSaveDraft">
          {{ saving ? '保存中...' : '保存计划草稿' }}
        </button>
        <button class="worker-button worker-button--secondary" :disabled="submitting" @click="handleTrainingAction('remind', '督办提醒')">
          督办未完成
        </button>
      </view>
      <view class="worker-button-row">
        <button class="worker-button worker-button--ghost" :disabled="submitting" @click="handleTrainingAction('publish-exam', '补发考试任务')">
          补发考试任务
        </button>
        <button class="worker-button worker-button--secondary" @click="selectAllPlans">全选计划</button>
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
import { getTrainingManageDashboard, saveTrainingPlanDraft, submitTrainingManageAction } from '../../api/enterprise'

const summary = ref([])
const plans = ref([])
const selectedIds = ref([])
const saving = ref(false)
const submitting = ref(false)
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

function selectAllPlans() {
  selectedIds.value = plans.value.map((item) => item.id)
}

async function loadData() {
  const data = await getTrainingManageDashboard()
  summary.value = Array.isArray(data.summary) ? data.summary : []
  plans.value = Array.isArray(data.plans) ? data.plans : []
}

async function handleSaveDraft() {
  saving.value = true
  try {
    const selectedPlans = plans.value.filter((item) => selectedIds.value.includes(item.id))
    const result = await saveTrainingPlanDraft({
      title: selectedPlans[0]?.title || plans.value[0]?.title || '月度培训计划',
      planIds: selectedPlans.length ? selectedPlans.map((item) => item.id) : plans.value.map((item) => item.id)
    })
    lastAction.value = {
      title: '培训计划草稿已保存',
      message: result.message || '培训计划草稿已登记，待后台继续完善。'
    }
    notify(result.message || '培训计划草稿已保存')
  } finally {
    saving.value = false
  }
}

async function handleTrainingAction(actionType, title) {
  if (!selectedIds.value.length) {
    notify('请先选择需要处理的培训计划')
    return
  }
  submitting.value = true
  try {
    const result = await submitTrainingManageAction({
      actionType,
      planIds: selectedIds.value
    })
    lastAction.value = {
      title: `${title}已登记`,
      message: result.message || '培训动作已登记，待后台继续流转。'
    }
    notify(result.message || '培训处理请求已提交')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadData().catch((error) => {
    notify(error.message || '加载培训数据失败')
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
