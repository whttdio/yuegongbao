<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page enterprise-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">保险管理</view>
      <view class="worker-subtitle">展示真实参保覆盖和安责险状态。续保、补缴只登记企业办理请求，不伪装成保单已流转完成。</view>
    </view>

    <view class="worker-card">
      <view class="enterprise-kpi-grid">
        <view v-for="item in summary" :key="item.label" class="enterprise-kpi">
          <view class="enterprise-kpi__label">{{ item.label }}</view>
          <view class="enterprise-kpi__value">{{ item.value }}</view>
          <view class="enterprise-kpi__desc">企业保险汇总指标</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="enterprise-notice">
        <view class="enterprise-notice__head">
          <view class="enterprise-notice__title">办理边界说明</view>
          <view class="worker-tag worker-tag--warning">登记流转</view>
        </view>
        <view class="enterprise-notice__desc">企业端可发起续保、补缴和核验请求，但后续办理仍需后台继续流转。</view>
        <view class="enterprise-notice__reason">前端只反馈“已登记，待后台处理”，不展示误导性的已投保或已补缴成功状态。</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="enterprise-list-head">
        <view>
          <view class="worker-title">待跟进事项</view>
          <view class="enterprise-list-head__meta">动作清单按真实摘要展示，后续只登记请求。</view>
        </view>
        <view class="worker-tag">{{ actions.length }} 项</view>
      </view>
      <view v-for="item in actions" :key="item.id" class="record-row">
        <view class="record-row__main">
          <view class="record-row__title">{{ item.title }}</view>
          <view class="record-row__subtitle">{{ item.desc }}</view>
        </view>
        <view class="record-row__aside">
          <view class="worker-tag" :class="item.className">{{ item.type }}</view>
        </view>
      </view>
      <view v-if="!actions.length" class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无待跟进保险事项</view>
        <view class="worker-empty__desc">已保留企业空态，后续由真实参保和保单状态继续回写。</view>
      </view>
      <view class="worker-button-row">
        <button class="worker-button" :disabled="submitting" @click="handleAction('renew', '续保请求')">
          {{ submitting ? '处理中...' : '发起续保' }}
        </button>
        <button class="worker-button worker-button--secondary" :disabled="submitting" @click="handleAction('repair', '补缴请求')">
          补缴提醒
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
import { onMounted, ref } from 'vue'
import { getInsuranceManageDashboard, submitInsuranceAction } from '../../api/enterprise'

const summary = ref([])
const actions = ref([])
const submitting = ref(false)
const lastAction = ref({ title: '', message: '' })

function notify(message) {
  uni.showToast({ title: message, icon: 'none' })
}

async function loadData() {
  const data = await getInsuranceManageDashboard()
  summary.value = Array.isArray(data.summary) ? data.summary : []
  actions.value = Array.isArray(data.actions) ? data.actions : []
}

async function handleAction(actionType, title) {
  submitting.value = true
  try {
    const result = await submitInsuranceAction({ actionType })
    lastAction.value = {
      title: `${title}已登记`,
      message: result.message || '已发起办理请求，待后台继续流转。'
    }
    notify(result.message || '保险办理请求已登记')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadData().catch((error) => {
    notify(error.message || '加载保险数据失败')
  })
})
</script>
