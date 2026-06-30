<template>
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">重要通知</view>
      <view class="worker-subtitle">查看平台公告与个人消息</view>
      <view class="hero-stat-grid">
        <view class="hero-stat">
          <view class="hero-stat__value">{{ rows.length }}</view>
          <view class="hero-stat__label">全部通知</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ unreadCount }}</view>
          <view class="hero-stat__label">未读</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ readCount }}</view>
          <view class="hero-stat__label">已读</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">通知列表</view>
      </view>
      <view v-if="rows.length">
        <view v-for="item in rows" :key="item.noticeId" class="list-row" @click="openDetail(item)">
          <view class="list-row__main">
            <view class="list-row__meta">
              <view class="worker-tag worker-tag--info">{{ resolveNoticeType(item) }}</view>
              <view v-if="item.sourceLabel" class="list-row__source">{{ item.sourceLabel }}</view>
            </view>
            <view class="list-row__title">{{ item.title }}</view>
            <view class="list-row__subtitle">{{ item.summary || '-' }}</view>
          </view>
          <view class="worker-tag">{{ item.readFlag || item.isRead ? '已读' : '未读' }}</view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无通知</view>
        <view class="worker-empty__desc">
          平台公告暂未下发时，可先查看帮助中心、工会服务或法律讲座内容。
        </view>
        <view class="notice-empty-actions">
          <button class="worker-button worker-button--secondary" @click="goHelp">帮助中心</button>
          <button class="worker-button" @click="goUnion">工会服务</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getNoticeList } from '../../api/worker'

const rows = ref([])
const readCount = computed(() => rows.value.filter((item) => item.readFlag || item.isRead).length)
const unreadCount = computed(() => Math.max(rows.value.length - readCount.value, 0))

async function loadData() {
  try {
    const data = await getNoticeList()
    rows.value = data?.rows || []
  } catch (error) {
    uni.showToast({ title: error.message || '加载通知失败', icon: 'none' })
  }
}

function openDetail(item) {
  if (!item?.noticeId) {
    return
  }
  item.readFlag = true
  item.isRead = true
  uni.navigateTo({ url: `/pages/notice/detail?noticeId=${item.noticeId}` })
}

function goHelp() {
  uni.navigateTo({ url: '/pages/profile/help' })
}

function goUnion() {
  uni.navigateTo({ url: '/pages/union/index' })
}

function resolveNoticeType(item) {
  if (Number(item?.noticeId || 0) < 0) {
    return '个人消息'
  }
  return '平台公告'
}

onShow(loadData)
</script>

<style lang="scss">
.list-row__main {
  flex: 1;
  min-width: 0;
}

.list-row__meta {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 10rpx;
}

.notice-empty-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 18rpx;
}

.notice-empty-actions button {
  flex: 1;
}
</style>
