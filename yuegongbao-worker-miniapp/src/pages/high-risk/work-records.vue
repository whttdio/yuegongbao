<template>
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">作业记录</view>
      <view class="worker-subtitle">用于查看高危作业追溯记录。</view>
    </view>

    <view class="worker-card">
      <view class="section-head"><view class="worker-title">记录列表</view></view>
      <view class="filter-row">
        <view v-for="item in filters" :key="item" class="filter-chip" :class="{ 'filter-chip--active': activeFilter === item }" @click="activeFilter = item">{{ item }}</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="record-row" v-for="item in visibleRecords" :key="item.id">
        <view>
          <view class="record-row__title">{{ item.date }} / {{ item.device }}</view>
          <view class="record-row__subtitle">{{ item.summary }}</view>
        </view>
        <view class="worker-tag" :class="statusClass(item.status)">{{ item.status }}</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getHighRiskWorkRecords } from '../../api/high-risk'

const filters = ref(['全部', '正常', '提醒', '违规'])
const activeFilter = ref('全部')
const records = ref([])

const visibleRecords = computed(() => (activeFilter.value === '全部' ? records.value : records.value.filter((item) => item.status === activeFilter.value)))

function statusClass(status) {
  if (status === '正常') return 'worker-tag--success'
  if (status === '提醒') return 'worker-tag--warning'
  return 'worker-tag--danger'
}

async function loadData() {
  const data = await getHighRiskWorkRecords()
  filters.value = Array.isArray(data.filters) ? data.filters : filters.value
  activeFilter.value = data.activeFilter || '全部'
  records.value = Array.isArray(data.list) ? data.list : []
}

onMounted(loadData)
</script>
