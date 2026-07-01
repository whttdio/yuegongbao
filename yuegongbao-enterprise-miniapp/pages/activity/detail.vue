<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page enterprise-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">{{ detail.title || '福利活动' }}</view>
      <view class="worker-subtitle">{{ detail.subtitle || '-' }}</view>
    </view>

    <view class="worker-card">
      <view class="enterprise-notice enterprise-notice--readonly">
        <view class="enterprise-notice__head">
          <view class="enterprise-notice__title">企业侧只读参考</view>
          <view class="worker-tag worker-tag--info">员工协同视图</view>
        </view>
        <view class="enterprise-notice__desc">活动报名依赖员工本人资格和参与确认，企业端不再代员工完成报名。</view>
        <view class="enterprise-notice__reason">当前仅保留活动规则、外部 H5 和参与记录入口，供企业侧查看活动安排。</view>
        <view class="enterprise-notice__actions">
          <button class="worker-button worker-button--secondary" @click="openJoinList">查看参与记录</button>
          <button v-if="detail.externalUrl" class="worker-button" @click="openExternalActivity">查看活动 H5</button>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title worker-title--small">活动规则</view>
      </view>
      <view v-for="(rule, index) in ruleList" :key="index" class="rule-row">
        {{ index + 1 }}. {{ rule }}
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { getActivityDetail } from '../../api/enterprise-service'

const detail = ref({})

const ruleList = computed(() => {
  if (Array.isArray(detail.value?.ruleList) && detail.value.ruleList.length) {
    return detail.value.ruleList
  }
  return ['请关注活动详情页后续更新。']
})

async function loadData() {
  detail.value = await getActivityDetail()
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

onShow(() => {
  loadData().catch((error) => {
    uni.showToast({ title: error.message || '加载活动失败', icon: 'none' })
  })
})
</script>

<style lang="scss">
.rule-row {
  margin-top: 20rpx;
  font-size: 26rpx;
  line-height: 1.7;
  color: #183247;
}
</style>
