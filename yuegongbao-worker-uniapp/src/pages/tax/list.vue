<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">{{ summary.year || currentYear }} 年度累计</view>
        <view class="worker-tag">{{ summary.totalMonths || 0 }} 个月</view>
      </view>
      <view class="summary-grid">
        <view class="summary-item">
          <view class="summary-item__label">累计申报收入</view>
          <view class="summary-item__value">{{ summary.cumulativeIncome || 0 }}</view>
        </view>
        <view class="summary-item">
          <view class="summary-item__label">累计工资实发</view>
          <view class="summary-item__value">{{ summary.cumulativeSalary || 0 }}</view>
        </view>
        <view class="summary-item">
          <view class="summary-item__label">累计实缴个税</view>
          <view class="summary-item__value">{{ summary.cumulativeTax ?? '-' }}</view>
        </view>
        <view class="summary-item">
          <view class="summary-item__label">异常月份</view>
          <view class="summary-item__value">{{ summary.abnormalMonths || 0 }}</view>
        </view>
        <view class="summary-item">
          <view class="summary-item__label">预警月份</view>
          <view class="summary-item__value">{{ summary.warningMonths || 0 }}</view>
        </view>
      </view>
      <view class="worker-subtitle summary-hint">
        {{ summary.amountHint || summary.cumulativeTaxText || '' }}
      </view>
    </view>

    <view class="worker-card">
      <view class="worker-title">个税列表</view>
      <view v-if="records.length">
        <view v-for="item in records" :key="item.taxMonth" class="list-row" @click="openDetail(item)">
          <view>
            <view class="list-row__title">{{ item.taxMonth }}</view>
            <view class="list-row__subtitle">
              申报收入 {{ item.incomeAmount ?? '-' }} / 工资实发 {{ item.salaryAmount ?? '-' }}
            </view>
            <view class="list-row__subtitle">
              实缴个税 {{ item.taxAmount ?? '-' }} / {{ item.taxAmountText || '来源已回写税额' }}
            </view>
            <view class="list-row__subtitle">
              预警状态 {{ item.warningStatusText || '未预警' }} / 差异 {{ item.diffRatio || 0 }}
            </view>
          </view>
          <view class="worker-tag">{{ item.declareStatusText || '-' }}</view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无个税记录</view>
        <view class="worker-empty__desc">
          可先对照工资记录和劳动合同确认用工状态；如税务信息长期未同步，可发起法律咨询继续核实。
        </view>
        <view class="worker-empty__actions">
          <button class="worker-button worker-button--secondary" @click="goSalary">查看工资</button>
          <button class="worker-button" @click="goLegal">法律咨询</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getTaxList } from '../../api/worker'

const EXPECTED_TAX_CHAIN_PAGES = ['个税列表', '个税详情', '工资查询', '法律咨询']
const data = ref({})
const records = computed(() => data.value.records || [])
const summary = computed(() => data.value.summary || {})
const currentYear = new Date().getFullYear().toString()
const taxLastLoadedAt = ref('')
const taxLastActionAt = ref('')
const taxLastMessage = ref('')
const taxChainCoverageText = computed(() => EXPECTED_TAX_CHAIN_PAGES.join(' / '))
const taxSummaryText = computed(() => {
  return `${summary.value.year || currentYear} / ${summary.value.totalMonths || 0} 个月 / 预警 ${summary.value.warningMonths || 0} 月`
})
const taxStatusSummaryText = computed(() => {
  if (!records.value.length) {
    return '暂无个税记录'
  }
  const statusMap = records.value.reduce((result, item) => {
    const key = item?.declareStatusText || '未知状态'
    result[key] = (result[key] || 0) + 1
    return result
  }, {})
  return Object.keys(statusMap).map((key) => `${key} ${statusMap[key]} 月`).join(' / ')
})
const latestTaxRecordText = computed(() => {
  if (!records.value.length) {
    return '暂无记录'
  }
  const latest = records.value[0] || {}
  return `${latest.taxMonth || '-'} / ${latest.declareStatusText || '-'} / 税额 ${latest.taxAmount ?? '-'}`
})
const taxConsistencyText = computed(() => {
  if (!records.value.length) {
    return '当前无个税记录，需用真实接口验收空态引导和详情下钻'
  }
  const warningCount = records.value.filter((item) => String(item?.warningStatusText || '').includes('预警')).length
  return `记录 ${records.value.length} 条 / 预警 ${warningCount} 条 / 可下钻详情`
})
const taxSnapshotText = computed(() => {
  return [
    '## 个税验收摘要',
    `- 链路核对：${taxChainCoverageText.value}`,
    `- 最近联动：${taxLastActionAt.value || '-'}`,
    `- 最近加载：${taxLastLoadedAt.value || '-'}`,
    `- 年度汇总：${taxSummaryText.value}`,
    `- 状态分布：${taxStatusSummaryText.value}`,
    `- 最近一条：${latestTaxRecordText.value}`,
    '- 空态引导：工资查询 / 法律咨询',
    `- 列表核对：${taxConsistencyText.value}`,
    `- 说明：${taxLastMessage.value || '-'}`,
    '- 链路关联：个税列表 / 个税详情 / 工资查询 / 法律咨询'
  ].join('\n')
})

async function loadData() {
  try {
    data.value = await getTaxList(currentYear)
    taxLastLoadedAt.value = new Date().toLocaleString()
    taxLastMessage.value = records.value.length
      ? `个税记录已加载，共 ${records.value.length} 条`
      : '当前暂无个税记录，可先查看工资或发起法律咨询'
  } catch (error) {
    taxLastLoadedAt.value = new Date().toLocaleString()
    taxLastMessage.value = error.message || '加载个税失败'
    uni.showToast({ title: error.message || '加载个税失败', icon: 'none' })
  }
}

function openDetail(item) {
  if (!item?.taxMonth) {
    return
  }
  taxLastActionAt.value = new Date().toLocaleString()
  taxLastMessage.value = `已打开个税详情：${item.taxMonth || '-'}`
  uni.navigateTo({ url: `/pages/tax/detail?month=${item.taxMonth}` })
}

function goSalary() {
  taxLastActionAt.value = new Date().toLocaleString()
  taxLastMessage.value = '已前往工资查询，待补充个税空态引导验收'
  uni.navigateTo({ url: '/pages/salary/list' })
}

function goLegal() {
  taxLastActionAt.value = new Date().toLocaleString()
  taxLastMessage.value = '已前往法律咨询，待补充税务异常核实链路验收'
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
