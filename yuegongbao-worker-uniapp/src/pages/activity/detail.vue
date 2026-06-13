<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="worker-title">{{ detail.title || '福利活动' }}</view>
      <view class="worker-subtitle">{{ detail.subtitle || '-' }}</view>
      <view class="section-head">
        <view class="worker-title worker-title--small">活动规则</view>
        <view class="more-link" @click="openJoinList">参与记录</view>
      </view>
      <view v-for="(rule, index) in ruleList" :key="index" class="rule-row">
        {{ index + 1 }}. {{ rule }}
      </view>
      <button
        v-if="detail.externalUrl"
        class="worker-button worker-button--secondary"
        @click="openExternalActivity"
      >
        查看活动 H5
      </button>
      <button class="worker-button" :disabled="detail.joined || joining" @click="handleJoin">
        {{ detail.joined ? '已报名' : '立即参与' }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { getActivityDetail, joinActivity } from '../../api/worker'

const EXPECTED_ACTIVITY_CHAIN_PAGES = ['首页福利活动', '活动详情', '参与记录', '活动外部H5']
const detail = ref({})
const joining = ref(false)
const activityLastLoadedAt = ref('')
const activityLastJoinedAt = ref('')
const activityLastActionAt = ref('')
const activityLastMessage = ref('')

const ruleList = computed(() => {
  if (Array.isArray(detail.value?.ruleList) && detail.value.ruleList.length) {
    return detail.value.ruleList
  }
  return ['请关注活动详情页后续更新。']
})
const activityStatusSummaryText = computed(() => {
  if (detail.value.joined) {
    return '已报名'
  }
  if (detail.value.activityKey) {
    return '可参与'
  }
  return '待加载'
})
const activityChainCoverageText = computed(() => EXPECTED_ACTIVITY_CHAIN_PAGES.join(' / '))
const activityConfigConsistencyText = computed(() => {
  return [
    ruleList.value.length ? `规则 ${ruleList.value.length} 条` : '规则待补',
    detail.value.externalUrl ? 'H5 已配置' : 'H5 未配置',
    detail.value.joined ? '状态已报名' : '状态可参与'
  ].join(' / ')
})
const activityLinkageText = computed(() => {
  if (detail.value.joined) {
    return '活动已报名，需去参与记录页验收状态回写'
  }
  return '需串联活动详情、参与记录和外部 H5 配置'
})
const activitySnapshotText = computed(() => {
  return [
    '## 活动验收摘要',
    `- 链路核对：${activityChainCoverageText.value}`,
    `- 最近联动：${activityLastActionAt.value || '-'}`,
    `- 最近加载：${activityLastLoadedAt.value || '-'}`,
    `- 最近参与：${activityLastJoinedAt.value || '-'}`,
    `- 活动名称：${detail.value.title || '福利活动'}`,
    `- 活动状态：${activityStatusSummaryText.value}`,
    `- 规则数量：${ruleList.value.length} 条`,
    `- 外链 H5：${detail.value.externalUrl ? '已配置' : '未配置'}`,
    `- 详情核对：${activityConfigConsistencyText.value}`,
    `- 参与联动：${activityLinkageText.value}`,
    `- 说明：${activityLastMessage.value || '-'}`,
    '- 链路关联：首页福利活动 / 活动详情 / 参与记录 / 外部 H5'
  ].join('\n')
})

async function loadData() {
  try {
    detail.value = await getActivityDetail()
    activityLastLoadedAt.value = new Date().toLocaleString()
    activityLastMessage.value = detail.value.joined
      ? '活动详情已加载，当前账号已报名'
      : '活动详情已加载，可继续参与或查看参与记录'
  } catch (error) {
    activityLastLoadedAt.value = new Date().toLocaleString()
    activityLastMessage.value = error.message || '加载活动失败'
    uni.showToast({ title: error.message || '加载活动失败', icon: 'none' })
  }
}

async function handleJoin() {
  if (!detail.value.activityKey || detail.value.joined || joining.value) {
    return
  }
  joining.value = true
  try {
    activityLastJoinedAt.value = new Date().toLocaleString()
    activityLastActionAt.value = activityLastJoinedAt.value
    const result = await joinActivity(detail.value.activityKey)
    detail.value.joined = !!result?.joined
    activityLastMessage.value = '活动报名成功，可到参与记录页回查'
    uni.showToast({ title: '报名成功', icon: 'none' })
  } catch (error) {
    activityLastJoinedAt.value = new Date().toLocaleString()
    activityLastMessage.value = error.message || '报名失败'
    uni.showToast({ title: error.message || '报名失败', icon: 'none' })
  } finally {
    joining.value = false
  }
}

function openJoinList() {
  activityLastActionAt.value = new Date().toLocaleString()
  activityLastMessage.value = '已前往参与记录页，待核对活动报名状态回写'
  uni.navigateTo({ url: '/pages/activity/join-list' })
}

function openExternalActivity() {
  if (!detail.value.externalUrl) {
    return
  }
  activityLastActionAt.value = new Date().toLocaleString()
  activityLastMessage.value = '已打开活动外部 H5 页面'
  const title = encodeURIComponent(detail.value.title || '福利活动')
  const url = encodeURIComponent(detail.value.externalUrl)
  uni.navigateTo({ url: `/pages/activity/webview?title=${title}&url=${url}` })
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

onLoad(() => {
  uni.setNavigationBarTitle({ title: '福利活动' })
})

onShow(loadData)
</script>

<style lang="scss">
.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 28rpx;
}

.worker-title--small {
  font-size: 28rpx;
}

.more-link {
  font-size: 24rpx;
  color: #1f6fd6;
}

.rule-row {
  margin-top: 20rpx;
  font-size: 26rpx;
  line-height: 1.7;
  color: #36506b;
}

.worker-button {
  margin-top: 28rpx;
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

.section-head--sub {
  margin-top: 20rpx;
}

.clear-action {
  font-size: 24rpx;
  color: #1f6fd6;
}
</style>
