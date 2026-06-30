<template>
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">个税查询</view>
      <view class="worker-subtitle">查看 {{ summary.year || currentYear }} 年度申报与实缴汇总</view>
      <view class="hero-stat-grid">
        <view class="hero-stat">
          <view class="hero-stat__value">{{ summary.totalMonths || 0 }}</view>
          <view class="hero-stat__label">累计月份</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ summary.cumulativeTax ?? '-' }}</view>
          <view class="hero-stat__label">累计实缴</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ summary.warningMonths || 0 }}</view>
          <view class="hero-stat__label">预警月份</view>
        </view>
      </view>
      <view v-if="summary.amountHint || summary.cumulativeTaxText" class="worker-subtitle">
        {{ summary.amountHint || summary.cumulativeTaxText }}
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">个税列表</view>
        <view class="worker-tag">{{ records.length }} 条</view>
      </view>
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
.worker-empty__actions button {
  flex: 1;
}
</style>
