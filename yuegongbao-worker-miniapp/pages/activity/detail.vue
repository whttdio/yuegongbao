<template>
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">{{ detail.title || '福利活动' }}</view>
      <view class="worker-subtitle">{{ detail.subtitle || '-' }}</view>
    </view>

    <view class="worker-card">
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

const detail = ref({})
const joining = ref(false)

const ruleList = computed(() => {
  if (Array.isArray(detail.value?.ruleList) && detail.value.ruleList.length) {
    return detail.value.ruleList
  }
  return ['请关注活动详情页后续更新。']
})

async function loadData() {
  try {
    detail.value = await getActivityDetail()
  } catch (error) {
    uni.showToast({ title: error.message || '加载活动失败', icon: 'none' })
  }
}

async function handleJoin() {
  if (!detail.value.activityKey || detail.value.joined || joining.value) {
    return
  }
  joining.value = true
  try {
    const result = await joinActivity(detail.value.activityKey)
    detail.value.joined = !!result?.joined
    uni.showToast({ title: result?.message || '报名成功', icon: 'none' })
  } catch (error) {
    uni.showToast({ title: error.message || '报名失败', icon: 'none' })
  } finally {
    joining.value = false
  }
}

function openJoinList() {
  uni.navigateTo({ url: '/pages/activity/join-list' })
}

function openExternalActivity() {
  if (!detail.value.externalUrl) {
    return
  }
  const title = encodeURIComponent(detail.value.title || '福利活动')
  const url = encodeURIComponent(detail.value.externalUrl)
  uni.navigateTo({ url: `/pages/activity/webview?title=${title}&url=${url}` })
}

onLoad(() => {
  uni.setNavigationBarTitle({ title: '福利活动' })
})

onShow(loadData)
</script>

<style lang="scss">
.rule-row {
  margin-top: 20rpx;
  font-size: 26rpx;
  line-height: 1.7;
  color: #183247;
}

.worker-button {
  margin-top: 28rpx;
}
</style>
