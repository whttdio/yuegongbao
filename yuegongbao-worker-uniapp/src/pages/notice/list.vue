<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="worker-title">重要通知</view>
      <view v-if="rows.length">
        <view v-for="item in rows" :key="item.noticeId" class="list-row" @click="openDetail(item)">
          <view class="list-row__main">
            <view class="list-row__meta">
              <view class="worker-tag worker-tag--light">{{ resolveNoticeType(item) }}</view>
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
const noticeChainCoverageText = computed(() => EXPECTED_NOTICE_CHAIN_PAGES.join(' / '))
const noticeReadSummaryText = computed(() => {
  const readCount = rows.value.filter((item) => item.readFlag || item.isRead).length
  const unreadCount = Math.max(rows.value.length - readCount, 0)
  return `已读 ${readCount} 条 / 未读 ${unreadCount} 条`
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
  const readCount = rows.value.filter((item) => item.readFlag || item.isRead).length
  if (!rows.value.length) {
    return '当前无通知，需结合真实接口验收来源分布和已读回写'
  }
  if (!readCount) {
    return `当前 ${rows.value.length} 条均未读，需下钻详情验证已读回写`
  }
  return `已读 ${readCount} 条 / 剩余未读 ${Math.max(rows.value.length - readCount, 0)} 条`
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
.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 18rpx;
}

.list-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 22rpx 0;
  border-bottom: 1rpx solid #edf2f7;
  gap: 20rpx;
}

.list-row:last-child {
  border-bottom: none;
}

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

.list-row__source {
  font-size: 22rpx;
  color: #1f6fd6;
}

.worker-tag--light {
  color: #1f6fd6;
  background: #eaf3ff;
}

.notice-empty-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 18rpx;
}

.notice-empty-actions button {
  flex: 1;
}

.section-head--sub {
  margin-top: 20rpx;
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

.clear-action {
  font-size: 24rpx;
  color: #1f6fd6;
}
</style>
