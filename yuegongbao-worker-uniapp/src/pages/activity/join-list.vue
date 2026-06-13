<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="worker-title">活动参与记录</view>
      <view class="worker-subtitle">查看福利活动报名状态、处理进度和最近参与情况。</view>
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
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getActivityJoinList } from '../../api/worker'

const EXPECTED_ACTIVITY_JOIN_CHAIN_PAGES = ['首页福利活动', '活动详情', '参与记录']
const rows = ref([])
const joinListLastLoadedAt = ref('')
const joinListLastMessage = ref('')
const activityJoinChainCoverageText = computed(() => EXPECTED_ACTIVITY_JOIN_CHAIN_PAGES.join(' / '))

const joinStatusSummaryText = computed(() => {
  if (!rows.value.length) {
    return '暂无参与记录'
  }
  const counter = rows.value.reduce((result, item) => {
    const key = item?.statusText || '未标记'
    result[key] = (result[key] || 0) + 1
    return result
  }, {})
  return Object.entries(counter)
    .map(([key, count]) => `${key} ${count} 条`)
    .join(' / ')
})

const latestJoinSummaryText = computed(() => {
  if (!rows.value.length) {
    return '暂无参与记录'
  }
  const latest = rows.value[0] || {}
  return `${latest.activityTitle || '福利活动'} / ${latest.joinTime || '-'} / ${latest.statusText || '已报名'}`
})
const activityJoinConsistencyText = computed(() => {
  if (!rows.value.length) {
    return '当前无参与记录，需结合活动详情真实报名动作验收'
  }
  const successCount = rows.value.filter((item) => String(item?.statusText || '').includes('报名') || String(item?.statusText || '').includes('成功')).length
  return `记录 ${rows.value.length} 条 / 已报名或成功 ${successCount} 条`
})

const joinListSnapshotText = computed(() => {
  return [
    '## 参与记录验收摘要',
    `- 链路核对：${activityJoinChainCoverageText.value}`,
    `- 最近加载：${joinListLastLoadedAt.value || '-'}`,
    `- 参与数量：${rows.value.length} 条`,
    `- 状态分布：${joinStatusSummaryText.value}`,
    `- 最近一条：${latestJoinSummaryText.value}`,
    `- 状态核对：${activityJoinConsistencyText.value}`,
    `- 说明：${joinListLastMessage.value || '-'}`,
    '- 链路关联：首页福利活动 / 活动详情 / 参与记录'
  ].join('\n')
})

async function loadData() {
  try {
    const data = await getActivityJoinList()
    rows.value = data?.rows || []
    joinListLastLoadedAt.value = new Date().toLocaleString()
    joinListLastMessage.value = rows.value.length
      ? `参与记录已加载，共 ${rows.value.length} 条`
      : '当前暂无参与记录'
  } catch (error) {
    joinListLastLoadedAt.value = new Date().toLocaleString()
    joinListLastMessage.value = error.message || '加载参与记录失败'
    uni.showToast({ title: error.message || '加载参与记录失败', icon: 'none' })
  }
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

.section-head--sub {
  margin-top: 20rpx;
}

.worker-title--small {
  font-size: 28rpx;
}

.list-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #edf2f7;
}

.list-row:last-child {
  border-bottom: none;
}

.list-row__title {
  font-size: 28rpx;
  font-weight: 600;
  color: #16324f;
}

.list-row__subtitle {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #7890aa;
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
  line-height: 1.7;
  color: #16324f;
}

.result-block {
  margin-top: 20rpx;
  padding: 20rpx 24rpx;
  border-radius: 18rpx;
  background: #f5f8fc;
}

.result-block__label {
  font-size: 24rpx;
  color: #5f7893;
}

.result-block__value {
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 1.8;
  color: #36506b;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
