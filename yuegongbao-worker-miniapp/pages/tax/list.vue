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

const data = ref({})
const records = computed(() => data.value.records || [])
const summary = computed(() => data.value.summary || {})
const currentYear = new Date().getFullYear().toString()

async function loadData() {
  try {
    data.value = await getTaxList(currentYear)
  } catch (error) {
    uni.showToast({ title: error.message || '加载个税失败', icon: 'none' })
  }
}

function openDetail(item) {
  if (!item?.taxMonth) {
    return
  }
  uni.navigateTo({ url: `/pages/tax/detail?month=${item.taxMonth}` })
}

function goSalary() {
  uni.navigateTo({ url: '/pages/salary/list' })
}

function goLegal() {
  uni.navigateTo({ url: '/pages/legal/index' })
}

onShow(loadData)
</script>

<style lang="scss">
.worker-empty__actions button {
  flex: 1;
}
</style>
