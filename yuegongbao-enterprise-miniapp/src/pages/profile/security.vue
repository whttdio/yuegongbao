<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">保险保障</view>
      <view class="worker-subtitle">{{ detail.enterpriseName || '未绑定企业' }}</view>
      <view class="hero-stat-grid">
        <view class="hero-stat">
          <view class="hero-stat__value">{{ detail.injuryInsuranceStatus || '-' }}</view>
          <view class="hero-stat__label">工伤保险</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ detail.aqInsuranceStatus || '-' }}</view>
          <view class="hero-stat__label">安责险</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ detail.coveredCount || '-' }}</view>
          <view class="hero-stat__label">覆盖人数</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">保障详情</view>
      </view>
      <view class="profile-grid">
        <view class="profile-item">
          <view class="profile-item__label">工伤保险状态</view>
          <view class="profile-item__value">{{ detail.injuryInsuranceStatus || '-' }}</view>
        </view>
        <view class="profile-item">
          <view class="profile-item__label">安责险状态</view>
          <view class="profile-item__value">{{ detail.aqInsuranceStatus || '-' }}</view>
        </view>
        <view class="profile-item">
          <view class="profile-item__label">安责险保费</view>
          <view class="profile-item__value">{{ detail.aqInsurancePremium || '-' }}</view>
        </view>
        <view class="profile-item">
          <view class="profile-item__label">覆盖人数</view>
          <view class="profile-item__value">{{ detail.aqInsuranceProtectedCount ?? '-' }}</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="worker-title">保单信息</view>
      <view class="detail-row">
        <view class="detail-row__label">承保范围</view>
        <view class="detail-row__value">{{ detail.aqInsuranceCoverageAmount || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">保障同步</view>
        <view class="detail-row__value">{{ detail.aqInsuranceCoverageSyncStatusText || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">来源说明</view>
        <view class="detail-row__value">{{ detail.aqInsuranceCoverageSource || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">有效期</view>
        <view class="detail-row__value">{{ detail.aqInsurancePeriod || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">承保机构</view>
        <view class="detail-row__value">{{ detail.aqInsuranceInsurer || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">保单号</view>
        <view class="detail-row__value">{{ detail.aqInsurancePolicyNo || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">剩余天数</view>
        <view class="detail-row__value">{{ detail.aqInsuranceExpireInDays ?? '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">数据状态</view>
        <view class="detail-row__value">{{ detail.aqInsuranceSourceStatusText || '-' }}</view>
      </view>
      <view class="worker-subtitle security-hint">{{ detail.aqInsuranceSourceMessage || '' }}</view>
      <view v-if="coverageDetails.length" class="coverage-detail-list">
        <view class="worker-title worker-title--small">保障明细</view>
        <view v-for="item in coverageDetails" :key="item.key" class="detail-row">
          <view class="detail-row__label">{{ item.label }}</view>
          <view class="detail-row__value">{{ item.value || '-' }}</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="worker-title">事故预防资金</view>
      <view class="detail-row">
        <view class="detail-row__label">计提比例</view>
        <view class="detail-row__value">{{ detail.aqInsuranceFundRatio || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">计提金额</view>
        <view class="detail-row__value">{{ detail.aqInsuranceFundAmount || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">已使用金额</view>
        <view class="detail-row__value">{{ detail.aqInsuranceUsedFund || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">可用余额</view>
        <view class="detail-row__value">{{ detail.aqInsuranceRemainingFund || '-' }}</view>
      </view>
      <view class="worker-subtitle security-hint">{{ detail.aqInsuranceCoverageHint || '' }}</view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getWorkerInsuranceSecurity } from '../../api/enterprise-service'

const EXPECTED_SECURITY_CHAIN_PAGES = ['保险保障', '保单信息', '事故预防资金']
const detail = ref({})
const coverageDetails = computed(() => detail.value.aqInsuranceCoverageDetail || [])
const securityLastLoadedAt = ref('')
const securityLastMessage = ref('')
const securityChainCoverageText = computed(() => EXPECTED_SECURITY_CHAIN_PAGES.join(' / '))
const securityStatusSummaryText = computed(() => {
  return `${detail.value.enterpriseName || '未绑定企业'} / 工伤 ${detail.value.injuryInsuranceStatus || '-'} / 安责险 ${detail.value.aqInsuranceStatus || '-'}`
})
const securityPolicySummaryText = computed(() => {
  return `${detail.value.aqInsurancePolicyNo || '-'} / ${detail.value.aqInsurancePeriod || '-'} / 剩余 ${detail.value.aqInsuranceExpireInDays ?? '-'} 天`
})
const securityFundSummaryText = computed(() => {
  return `计提 ${detail.value.aqInsuranceFundAmount || '-'} / 已用 ${detail.value.aqInsuranceUsedFund || '-'} / 余额 ${detail.value.aqInsuranceRemainingFund || '-'}`
})
const securityConsistencyText = computed(() => {
  if (!detail.value.enterpriseName) {
    return '未绑定企业或保障数据未同步'
  }
  return `保单 ${detail.value.aqInsurancePolicyNo || '-'} / 明细 ${coverageDetails.value.length} 条 / 数据状态 ${detail.value.aqInsuranceSourceStatusText || '-'}`
})
const securitySnapshotText = computed(() => {
  return [
    '## 保险保障验收摘要',
    `- 链路核对：${securityChainCoverageText.value}`,
    `- 最近加载：${securityLastLoadedAt.value || '-'}`,
    `- 企业与状态：${securityStatusSummaryText.value}`,
    `- 保单摘要：${securityPolicySummaryText.value}`,
    `- 资金摘要：${securityFundSummaryText.value}`,
    `- 保障明细：${coverageDetails.value.length} 条`,
    `- 数据核对：${securityConsistencyText.value}`,
    `- 说明：${securityLastMessage.value || '-'}`,
    '- 链路关联：保险保障 / 保单信息 / 事故预防资金'
  ].join('\n')
})

async function loadData() {
  try {
    detail.value = await getWorkerInsuranceSecurity()
    securityLastLoadedAt.value = new Date().toLocaleString()
    securityLastMessage.value = detail.value.enterpriseName
      ? '保险保障信息已加载，可核对保单与资金摘要'
      : '当前未绑定企业或保障数据未同步'
  } catch (error) {
    securityLastLoadedAt.value = new Date().toLocaleString()
    securityLastMessage.value = error.message || '加载保险保障失败'
    uni.showToast({ title: error.message || '加载保险保障失败', icon: 'none' })
  }
}

function copyText(content, successTitle) {
  if (!content) {
    uni.showToast({ title: '暂无可复制内容', icon: 'none' })
    return
  }
  uni.setClipboardData({
    data: content,
    success: () => uni.showToast({ title: successTitle, icon: 'none' }),
    fail: () => uni.showToast({ title: '复制失败，请改用截图', icon: 'none' })
  })
}

onShow(loadData)
</script>

<style lang="scss">
.worker-card + .worker-card {
  margin-top: 24rpx;
}

.detail-row:last-of-type {
  border-bottom: none;
}

.security-hint {
  margin-top: 20rpx;
}

.coverage-detail-list {
  margin-top: 20rpx;
}
</style>
