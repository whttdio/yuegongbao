<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="worker-title">法律公益讲座</view>
      <view v-if="rows.length">
        <view v-for="item in rows" :key="item.articleKey" class="list-row" @click="openDetail(item)">
          <view>
            <view class="list-row__title">{{ item.title }}</view>
            <view class="list-row__subtitle">{{ item.summary || '-' }}</view>
          </view>
          <view class="worker-tag">{{ item.category || '-' }}</view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无讲座内容</view>
        <view class="worker-empty__desc">
          可先查看常见法律问题，或进入工会服务页获取维权讲座、案例和协同通知。
        </view>
        <view class="article-empty-actions">
          <button class="worker-button worker-button--secondary" @click="goLegalFaq">查看法律咨询</button>
          <button class="worker-button" @click="goUnion">工会服务</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getLegalArticleList } from '../../api/worker'

const rows = ref([])
const articleListLastLoadedAt = ref('')
const articleListLastActionAt = ref('')
const articleListLastMessage = ref('')

const latestArticleSummaryText = computed(() => {
  if (!rows.value.length) {
    return '暂无讲座内容'
  }
  const latest = rows.value[0] || {}
  return `${latest.title || '-'} / ${latest.category || '-'}`
})

const articleListSnapshotText = computed(() => {
  return [
    '## 讲座列表验收摘要',
    `- 最近加载：${articleListLastLoadedAt.value || '-'}`,
    `- 最近打开：${articleListLastActionAt.value || '-'}`,
    `- 讲座数量：${rows.value.length} 条`,
    `- 最近一条：${latestArticleSummaryText.value}`,
    `- 说明：${articleListLastMessage.value || '-'}`,
    '- 链路关联：法律公益讲座 / 讲座详情 / 法律咨询 / 工会服务'
  ].join('\n')
})

async function loadData() {
  try {
    const data = await getLegalArticleList()
    rows.value = data?.rows || []
    articleListLastLoadedAt.value = new Date().toLocaleString()
    articleListLastMessage.value = rows.value.length
      ? `讲座列表已加载，共 ${rows.value.length} 条`
      : '当前暂无讲座内容'
  } catch (error) {
    articleListLastLoadedAt.value = new Date().toLocaleString()
    articleListLastMessage.value = error.message || '加载讲座失败'
    uni.showToast({ title: error.message || '加载讲座失败', icon: 'none' })
  }
}

function openDetail(item) {
  if (!item?.articleKey) {
    return
  }
  articleListLastActionAt.value = new Date().toLocaleString()
  articleListLastMessage.value = `已打开讲座详情：${item.title || '-'}`
  uni.navigateTo({ url: `/pages/legal/article-detail?articleKey=${item.articleKey}` })
}

function goLegalFaq() {
  articleListLastActionAt.value = new Date().toLocaleString()
  articleListLastMessage.value = '已前往法律咨询页面'
  uni.navigateTo({ url: '/pages/legal/index' })
}

function goUnion() {
  articleListLastActionAt.value = new Date().toLocaleString()
  articleListLastMessage.value = '已前往工会服务页面'
  uni.navigateTo({ url: '/pages/union/index' })
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
  padding: 22rpx 0;
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

.article-empty-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 18rpx;
}

.article-empty-actions button {
  flex: 1;
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
