<template>
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">社保查询</view>
      <view class="worker-subtitle">查看 {{ summary.year || currentYear }} 年度参保与缴费汇总</view>
      <view class="hero-stat-grid">
        <view class="hero-stat">
          <view class="hero-stat__value">{{ summary.totalMonths || 0 }}</view>
          <view class="hero-stat__label">累计月份</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ summary.cumulativePaidAmount ?? '-' }}</view>
          <view class="hero-stat__label">累计缴费</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ summary.arrearsMonths || 0 }}</view>
          <view class="hero-stat__label">欠费月份</view>
        </view>
      </view>
      <view v-if="summary.amountHint" class="worker-subtitle">{{ summary.amountHint }}</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">社保列表</view>
        <view class="worker-tag">{{ records.length }} 条</view>
      </view>
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

const data = ref({})
const records = computed(() => data.value.records || [])
const summary = computed(() => data.value.summary || {})
const currentYear = new Date().getFullYear().toString()

async function loadData() {
  try {
    data.value = await getSocialSecurityList(currentYear)
  } catch (error) {
    uni.showToast({ title: error.message || '加载社保失败', icon: 'none' })
  }
}

function openDetail(item) {
  if (!item?.insuredMonth) {
    return
  }
  uni.navigateTo({ url: `/pages/social/detail?month=${item.insuredMonth}` })
}

function goContracts() {
  uni.navigateTo({ url: '/pages/profile/labor-contracts' })
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
