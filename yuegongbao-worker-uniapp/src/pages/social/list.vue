<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">{{ summary.year || currentYear }} 年度汇总</view>
        <view class="worker-tag">{{ summary.totalMonths || 0 }} 个月</view>
      </view>
      <view class="summary-grid">
        <view class="summary-item">
          <view class="summary-item__label">累计缴费基数</view>
          <view class="summary-item__value">{{ summary.cumulativeBaseAmount ?? '-' }}</view>
        </view>
        <view class="summary-item">
          <view class="summary-item__label">累计个人缴费</view>
          <view class="summary-item__value">{{ summary.cumulativePersonalAmount ?? '-' }}</view>
        </view>
        <view class="summary-item">
          <view class="summary-item__label">累计单位缴费</view>
          <view class="summary-item__value">{{ summary.cumulativeCompanyAmount ?? '-' }}</view>
        </view>
        <view class="summary-item">
          <view class="summary-item__label">累计缴费总额</view>
          <view class="summary-item__value">{{ summary.cumulativePaidAmount ?? '-' }}</view>
        </view>
        <view class="summary-item">
          <view class="summary-item__label">正常月份</view>
          <view class="summary-item__value">{{ summary.normalMonths || 0 }}</view>
        </view>
        <view class="summary-item">
          <view class="summary-item__label">欠费月份</view>
          <view class="summary-item__value">{{ summary.arrearsMonths || 0 }}</view>
        </view>
      </view>
      <view class="worker-subtitle summary-hint">{{ summary.amountHint || '' }}</view>
    </view>

    <view class="worker-card">
      <view class="worker-title">社保列表</view>
      <view v-if="records.length">
        <view v-for="item in records" :key="item.insuredMonth" class="list-row" @click="openDetail(item)">
          <view>
            <view class="list-row__title">{{ item.insuredMonth }}</view>
            <view class="list-row__subtitle">
              缴费基数 {{ item.baseAmount ?? '-' }} / 缴费总额 {{ item.paidAmount ?? '-' }}
            </view>
            <view class="list-row__subtitle">
              个人 {{ item.personalAmount ?? '-' }} / 单位 {{ item.companyAmount ?? '-' }}
            </view>
            <view class="list-row__subtitle">
              {{ item.personalAmountText || '来源已提供拆分金额' }} / 来源状态 {{ item.sourceStatusText || '待同步' }}
            </view>
          </view>
          <view class="worker-tag">{{ item.insuredStatusText || '-' }}</view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无社保记录</view>
        <view class="worker-empty__desc">
          如近期刚入职或参保信息尚未同步，可先核对劳动合同和在岗信息；若长期未显示，可直接发起法律咨询。
        </view>
        <view class="worker-empty__actions">
          <button class="worker-button worker-button--secondary" @click="goContracts">查看合同</button>
          <button class="worker-button" @click="goLegal">法律咨询</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getSocialSecurityList } from '../../api/worker'

const EXPECTED_SOCIAL_CHAIN_PAGES = ['社保列表', '社保详情', '我的劳动合同', '法律咨询']
const data = ref({})
const records = computed(() => data.value.records || [])
const summary = computed(() => data.value.summary || {})
const currentYear = new Date().getFullYear().toString()
const socialLastLoadedAt = ref('')
const socialLastActionAt = ref('')
const socialLastMessage = ref('')
const socialChainCoverageText = computed(() => EXPECTED_SOCIAL_CHAIN_PAGES.join(' / '))
const socialSummaryText = computed(() => {
  return `${summary.value.year || currentYear} / ${summary.value.totalMonths || 0} 个月 / 欠费 ${summary.value.arrearsMonths || 0} 月`
})
const socialStatusSummaryText = computed(() => {
  if (!records.value.length) {
    return '暂无社保记录'
  }
  const statusMap = records.value.reduce((result, item) => {
    const key = item?.insuredStatusText || '未知状态'
    result[key] = (result[key] || 0) + 1
    return result
  }, {})
  return Object.keys(statusMap).map((key) => `${key} ${statusMap[key]} 月`).join(' / ')
})
const latestSocialRecordText = computed(() => {
  if (!records.value.length) {
    return '暂无记录'
  }
  const latest = records.value[0] || {}
  return `${latest.insuredMonth || '-'} / ${latest.insuredStatusText || '-'} / 总额 ${latest.paidAmount ?? '-'}`
})
const socialConsistencyText = computed(() => {
  if (!records.value.length) {
    return '当前无社保记录，需用真实接口验收空态引导和详情下钻'
  }
  const arrearsCount = records.value.filter((item) => String(item?.insuredStatusText || '').includes('欠')).length
  return `记录 ${records.value.length} 条 / 欠费或异常 ${arrearsCount} 条 / 可下钻详情`
})
const socialSnapshotText = computed(() => {
  return [
    '## 社保验收摘要',
    `- 链路核对：${socialChainCoverageText.value}`,
    `- 最近联动：${socialLastActionAt.value || '-'}`,
    `- 最近加载：${socialLastLoadedAt.value || '-'}`,
    `- 年度汇总：${socialSummaryText.value}`,
    `- 状态分布：${socialStatusSummaryText.value}`,
    `- 最近一条：${latestSocialRecordText.value}`,
    '- 空态引导：劳动合同 / 法律咨询',
    `- 列表核对：${socialConsistencyText.value}`,
    `- 说明：${socialLastMessage.value || '-'}`,
    '- 链路关联：社保列表 / 社保详情 / 我的劳动合同 / 法律咨询'
  ].join('\n')
})

async function loadData() {
  try {
    data.value = await getSocialSecurityList(currentYear)
    socialLastLoadedAt.value = new Date().toLocaleString()
    socialLastMessage.value = records.value.length
      ? `社保记录已加载，共 ${records.value.length} 条`
      : '当前暂无社保记录，可先核对劳动合同或发起法律咨询'
  } catch (error) {
    socialLastLoadedAt.value = new Date().toLocaleString()
    socialLastMessage.value = error.message || '加载社保失败'
    uni.showToast({ title: error.message || '加载社保失败', icon: 'none' })
  }
}

function openDetail(item) {
  if (!item?.insuredMonth) {
    return
  }
  socialLastActionAt.value = new Date().toLocaleString()
  socialLastMessage.value = `已打开社保详情：${item.insuredMonth || '-'}`
  uni.navigateTo({ url: `/pages/social/detail?month=${item.insuredMonth}` })
}

function goContracts() {
  socialLastActionAt.value = new Date().toLocaleString()
  socialLastMessage.value = '已前往我的劳动合同，待补充空态引导验收'
  uni.navigateTo({ url: '/pages/profile/labor-contracts' })
}

function goLegal() {
  socialLastActionAt.value = new Date().toLocaleString()
  socialLastMessage.value = '已前往法律咨询，待补充社保异常核实链路验收'
  uni.navigateTo({ url: '/pages/legal/index' })
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

.summary-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18rpx;
}

.summary-item {
  padding: 20rpx;
  border-radius: 18rpx;
  background: #f5f8fc;
}

.summary-item__label {
  font-size: 22rpx;
  color: #7890aa;
}

.summary-item__value {
  margin-top: 10rpx;
  font-size: 30rpx;
  font-weight: 700;
  color: #16324f;
}

.summary-hint {
  margin-top: 18rpx;
}

.list-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 22rpx 0;
  border-bottom: 1rpx solid #edf2f7;
}

.list-row:last-child {
  border-bottom: none;
}

.list-row__title {
  font-size: 30rpx;
  font-weight: 600;
  color: #16324f;
}

.list-row__subtitle {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #7890aa;
}

.worker-empty--panel {
  padding: 24rpx 0;
}

.worker-empty__title {
  font-size: 28rpx;
  font-weight: 600;
  color: #16324f;
}

.worker-empty__desc {
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: #7890aa;
}

.worker-empty__actions {
  display: flex;
  gap: 18rpx;
  margin-top: 22rpx;
}

.worker-empty__actions button {
  flex: 1;
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

.detail-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
  padding: 18rpx 0;
  border-bottom: 1rpx solid #edf2f7;
}

.detail-row:last-child {
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
