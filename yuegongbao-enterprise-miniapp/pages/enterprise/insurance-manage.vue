<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">保险管理</view>
      <view class="worker-subtitle">先承接工伤保险和安责险概览，后续再对接投保、续保和委托流转。</view>
      <view v-if="tipText" class="worker-subtitle worker-subtitle--progress">{{ tipText }}</view>
    </view>

    <view class="worker-card">
      <view class="status-grid">
        <view class="status-item" v-for="item in summary" :key="item.label">
          <view class="status-item__label">{{ item.label }}</view>
          <view class="status-item__value">{{ item.value }}</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="record-row" v-for="item in actions" :key="item.id">
        <view>
          <view class="record-row__title">{{ item.title }}</view>
          <view class="record-row__subtitle">{{ item.desc }}</view>
        </view>
        <view class="worker-tag" :class="item.className">{{ item.type }}</view>
      </view>
      <view class="worker-button-row">
        <button class="worker-button" :disabled="submitting" @click="handleAction('renew')">
          {{ submitting ? '处理中...' : '发起续保' }}
        </button>
        <button class="worker-button worker-button--secondary" :disabled="submitting" @click="handleAction('repair')">
          补缴提醒
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getInsuranceManageDashboard, submitInsuranceAction } from '../../api/enterprise'

const summary = ref([])
const actions = ref([])
const submitting = ref(false)
const tip = ref('')

const tipText = computed(() => tip.value)

function notify(message) {
  uni.showToast({ title: message, icon: 'none' })
}

async function loadData() {
  const data = await getInsuranceManageDashboard()
  summary.value = Array.isArray(data.summary) ? data.summary : []
  actions.value = Array.isArray(data.actions) ? data.actions : []
  tip.value = ''
}

async function handleAction(actionType) {
  submitting.value = true
  try {
    const result = await submitInsuranceAction({ actionType })
    notify(result.message || '保险处理已提交')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>
