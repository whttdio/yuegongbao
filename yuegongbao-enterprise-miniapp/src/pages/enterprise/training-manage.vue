<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">培训管理</view>
      <view class="worker-subtitle">查看培训计划、未完成人员和课程安排，先沉淀为可联调的培训计划接口位。</view>
      <view v-if="tipText" class="worker-subtitle worker-subtitle--progress">{{ tipText }}</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">培训概览</view>
        <view class="worker-tag worker-tag--info">已选 {{ selectedCount }} 计划</view>
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
        <view class="worker-title">培训计划</view>
        <view class="worker-caption">点击条目可勾选，后续用于督办、保存计划和补发考试任务。</view>
      </view>
      <view
        class="record-row record-row--selectable"
        :class="{ 'record-row--selected': isSelected(item.id) }"
        v-for="item in plans"
        :key="item.id"
        @click="toggleSelection(item.id)"
      >
        <view class="record-row__main">
          <view class="record-row__title">{{ item.title }}</view>
          <view class="record-row__subtitle">{{ item.desc }}</view>
        </view>
        <view class="record-row__aside">
          <view class="worker-tag worker-tag--info">{{ item.progress }}</view>
          <view class="record-row__select-indicator">{{ isSelected(item.id) ? '已选' : '选择' }}</view>
        </view>
      </view>
      <view v-if="!plans.length" class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无培训计划</view>
        <view class="worker-empty__desc">后续真实培训管理接口接通后，会回写计划、进度和考试安排。</view>
      </view>
      <view class="worker-button-row">
        <button class="worker-button" :disabled="saving" @click="handleSaveDraft">
          {{ saving ? '保存中...' : '保存计划草稿' }}
        </button>
        <button class="worker-button worker-button--secondary" :disabled="submitting" @click="handleTrainingAction('remind')">
          督办未完成
        </button>
      </view>
      <view class="worker-button-row">
        <button class="worker-button worker-button--ghost" :disabled="submitting" @click="handleTrainingAction('publish-exam')">
          补发考试任务
        </button>
        <button class="worker-button worker-button--secondary" @click="selectAllPlans">
          全选计划
        </button>
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

function selectAllPlans() {
  selectedIds.value = plans.value.map((item) => item.id)
}

async function loadData() {
  const data = await getTrainingManageDashboard()
  summary.value = Array.isArray(data.summary) ? data.summary : []
  plans.value = Array.isArray(data.plans) ? data.plans : []
  tip.value = ''
}

async function handleSaveDraft() {
  saving.value = true
  try {
    const selectedPlans = plans.value.filter((item) => selectedIds.value.includes(item.id))
    const result = await saveTrainingPlanDraft({
      title: selectedPlans[0]?.title || plans.value[0]?.title || '月度培训计划',
      planIds: selectedPlans.length ? selectedPlans.map((item) => item.id) : plans.value.map((item) => item.id)
    })
    notify(result.message || '培训计划草稿已保存')
  } finally {
    saving.value = false
  }
}

async function handleTrainingAction(actionType) {
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
    notify(result.message || '培训处理已提交')
  } finally {
    submitting.value = false
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
