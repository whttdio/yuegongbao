<template>
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">法律公益讲座</view>
      <view class="worker-subtitle">学习劳动权益知识，了解维权与协商要点</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">讲座列表</view>
        <view class="worker-tag">{{ rows.length }} 条</view>
      </view>
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
import { getLegalArticleList } from '../../api/screen'

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
.article-empty-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 18rpx;
}

.article-empty-actions button {
  flex: 1;
}
</style>
