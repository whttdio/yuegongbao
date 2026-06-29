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

const EXPECTED_NOTICE_CHAIN_PAGES = ['首页重要通知', '通知列表', '通知详情', '消息已读回写', '目标承接页']
const rows = ref([])
const noticeLastLoadedAt = ref('')
const noticeLastActionAt = ref('')
const noticeLastMessage = ref('')
const readCount = computed(() => rows.value.filter((item) => item.readFlag || item.isRead).length)
const unreadCount = computed(() => Math.max(rows.value.length - readCount.value, 0))
const noticeChainCoverageText = computed(() => EXPECTED_NOTICE_CHAIN_PAGES.join(' / '))
const noticeReadSummaryText = computed(() => {
  return `已读 ${readCount.value} 条 / 未读 ${unreadCount.value} 条`
})
const noticeSourceSummaryText = computed(() => {
  if (!rows.value.length) {
    return '暂无通知来源'
  }
  const sourceMap = rows.value.reduce((result, item) => {
    const key = resolveNoticeType(item)
    result[key] = (result[key] || 0) + 1
    return result
  }, {})
  return Object.keys(sourceMap).map((key) => `${key} ${sourceMap[key]} 条`).join(' / ')
})
const latestNoticeSummaryText = computed(() => {
  if (!rows.value.length) {
    return '暂无通知'
  }
  const latest = rows.value[0] || {}
  return `${latest.title || '-'} / ${resolveNoticeType(latest)} / ${latest.readFlag || latest.isRead ? '已读' : '未读'}`
})
const noticeConsistencyText = computed(() => {
  if (!rows.value.length) {
    return '当前无通知，需结合真实接口验收来源分布和已读回写'
  }
  if (!readCount.value) {
    return `当前 ${rows.value.length} 条均未读，需下钻详情验证已读回写`
  }
  return `已读 ${readCount.value} 条 / 剩余未读 ${unreadCount.value} 条`
})
const noticeLinkageText = computed(() => {
  if (String(noticeLastMessage.value || '').includes('通知详情')) {
    return '已下钻通知详情，需继续验收详情页已读回写和目标承接页'
  }
  return '需串联首页重要通知、通知详情、已读回写和跳转承接页'
})
const noticeSnapshotText = computed(() => {
  return [
    '## 通知验收摘要',
    `- 链路核对：${noticeChainCoverageText.value}`,
    `- 最近联动：${noticeLastActionAt.value || '-'}`,
    `- 最近加载：${noticeLastLoadedAt.value || '-'}`,
    `- 通知总数：${rows.value.length} 条`,
    `- 已读未读：${noticeReadSummaryText.value}`,
    `- 来源分布：${noticeSourceSummaryText.value}`,
    `- 最近一条：${latestNoticeSummaryText.value}`,
    `- 列表 / 已读：${noticeConsistencyText.value}`,
    `- 下钻联动：${noticeLinkageText.value}`,
    `- 说明：${noticeLastMessage.value || '-'}`,
    '- 链路关联：首页重要通知 / 通知列表 / 通知详情 / 消息已读'
  ].join('\n')
})

async function loadData() {
  try {
    const data = await getNoticeList()
    rows.value = data?.rows || []
    noticeLastLoadedAt.value = new Date().toLocaleString()
    noticeLastMessage.value = rows.value.length
      ? `通知列表已加载，共 ${rows.value.length} 条`
      : '当前暂无通知，可先查看帮助中心或工会服务'
  } catch (error) {
    noticeLastLoadedAt.value = new Date().toLocaleString()
    noticeLastMessage.value = error.message || '加载通知失败'
    uni.showToast({ title: error.message || '加载通知失败', icon: 'none' })
  }
}

function openDetail(item) {
  if (!item?.noticeId) {
    return
  }
  item.readFlag = true
  item.isRead = true
  noticeLastActionAt.value = new Date().toLocaleString()
  noticeLastMessage.value = `已打开通知详情：${item.title || '-'}`
  uni.navigateTo({ url: `/pages/notice/detail?noticeId=${item.noticeId}` })
}

function goHelp() {
  noticeLastActionAt.value = new Date().toLocaleString()
  noticeLastMessage.value = '已前往帮助中心，待补充通知空态引导验收'
  uni.navigateTo({ url: '/pages/profile/help' })
}

function goUnion() {
  noticeLastActionAt.value = new Date().toLocaleString()
  noticeLastMessage.value = '已前往工会服务，待补充通知空态引导验收'
  uni.navigateTo({ url: '/pages/union/index' })
}

function resolveNoticeType(item) {
  if (Number(item?.noticeId || 0) < 0) {
    return '个人消息'
  }
  return '平台公告'
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
