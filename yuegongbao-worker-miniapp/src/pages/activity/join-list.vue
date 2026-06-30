<template>
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">活动参与记录</view>
      <view class="worker-subtitle">查看福利活动报名状态、处理进度和最近参与情况</view>
      <view class="hero-stat-grid">
        <view class="hero-stat">
          <view class="hero-stat__value">{{ rows.length }}</view>
          <view class="hero-stat__label">参与次数</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">参与列表</view>
      </view>
      <view v-if="rows.length">
        <view v-for="item in rows" :key="item.joinId" class="list-row">
          <view>
            <view class="list-row__title">{{ item.activityTitle || '福利活动' }}</view>
            <view class="list-row__subtitle">活动标识：{{ item.activityKey || '-' }}</view>
            <view class="list-row__subtitle">报名时间：{{ item.joinTime || '-' }}</view>
          </view>
          <view class="worker-tag">{{ item.statusText || '已报名' }}</view>
        </view>
      </view>
      <view v-else class="worker-empty">暂无参与记录</view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getActivityJoinList } from '../../api/worker'

const rows = ref([])

async function loadData() {
  try {
    const data = await getActivityJoinList()
    rows.value = data?.rows || []
  } catch (error) {
    uni.showToast({ title: error.message || '加载参与记录失败', icon: 'none' })
  }
}

onShow(loadData)
</script>
