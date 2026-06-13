<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="worker-title">保险保障</view>
      <view class="worker-subtitle">{{ detail.enterpriseName || '未绑定企业' }}</view>
      <view class="security-grid">
        <view class="security-item">
          <view class="security-item__label">工伤保险状态</view>
          <view class="security-item__value">{{ detail.injuryInsuranceStatus || '-' }}</view>
        </view>
        <view class="security-item">
          <view class="security-item__label">安责险状态</view>
          <view class="security-item__value">{{ detail.aqInsuranceStatus || '-' }}</view>
        </view>
        <view class="security-item">
          <view class="security-item__label">安责险保费</view>
          <view class="security-item__value">{{ detail.aqInsurancePremium || '-' }}</view>
        </view>
        <view class="security-item">
          <view class="security-item__label">覆盖人数</view>
          <view class="security-item__value">{{ detail.aqInsuranceProtectedCount ?? '-' }}</view>
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
import { getWorkerInsuranceSecurity } from '../../api/worker'

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
.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 18rpx;
}

.worker-card + .worker-card {
  margin-top: 24rpx;
}

.security-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18rpx;
  margin-top: 20rpx;
}

.security-item {
  padding: 20rpx;
  border-radius: 18rpx;
  background: #f5f8fc;
}

.security-item__label {
  font-size: 22rpx;
  color: #7890aa;
}

.security-item__value {
  margin-top: 10rpx;
  font-size: 28rpx;
  font-weight: 600;
  color: #16324f;
}

.detail-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
  padding: 18rpx 0;
  border-bottom: 1rpx solid #edf2f7;
}

.detail-row:last-of-type {
  border-bottom: none;
}

.detail-row__label {
  font-size: 26rpx;
  color: #5f7893;
}

.detail-row__value {
  flex: 1;
  text-align: right;
  font-size: 26rpx;
  color: #16324f;
  line-height: 1.6;
  word-break: break-all;
}

.security-hint {
  margin-top: 20rpx;
}

.coverage-detail-list {
  margin-top: 20rpx;
}

.section-head--sub {
  margin-top: 20rpx;
}

.clear-action {
  font-size: 24rpx;
  color: #1f6fd6;
}

.worker-title--small {
  font-size: 28rpx;
}

.result-block {
  margin-top: 16rpx;
  padding: 22rpx 24rpx;
  border-radius: 20rpx;
  background: #f5f8fc;
}

.result-block__label {
  font-size: 22rpx;
  color: #7890aa;
}

.result-block__value {
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: #16324f;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
