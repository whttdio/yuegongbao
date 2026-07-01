<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page enterprise-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">投诉举报协同</view>
      <view class="worker-subtitle">企业端保留投诉记录查看和证据归档说明；员工本人发起投诉的动作已关闭。</view>
      <view class="enterprise-hero__meta">
        <view class="worker-tag worker-tag--warning">{{ rows.length }} 条历史记录</view>
        <view class="worker-tag">证据归档请走上传记录</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="enterprise-notice enterprise-notice--readonly">
        <view class="enterprise-notice__head">
          <view class="enterprise-notice__title">企业侧只读参考</view>
          <view class="worker-tag worker-tag--info">员工协同视图</view>
        </view>
        <view class="enterprise-notice__desc">投诉提交依赖员工本人陈述、实名信息和证据材料，企业端不再代员工发起投诉。</view>
        <view class="enterprise-notice__reason">当前仅保留历史投诉查看、拍照归档入口和法律咨询跳转，用于企业内部协同与解释处理进度。</view>
        <view class="enterprise-notice__actions">
          <button class="worker-button worker-button--secondary" @click="goLegal">法律咨询</button>
          <button class="worker-button" @click="goCamera">拍照归档</button>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="enterprise-list-head">
        <view>
          <view class="worker-title">历史投诉</view>
          <view class="enterprise-list-head__meta">仅作企业协同参考，不允许继续从企业端提交新的个人投诉。</view>
        </view>
        <view class="worker-tag">{{ rows.length }} 条</view>
      </view>
      <view v-if="rows.length" class="complaint-list">
        <view v-for="item in rows" :key="item.complaintId" class="complaint-item" @click="openDetail(item)">
          <view class="complaint-item__main">
            <view class="complaint-item__title">{{ item.title }}</view>
            <view class="complaint-item__meta">{{ item.complaintType || '-' }} / {{ item.syncUnionText || '未同步工会' }}</view>
          </view>
          <view class="worker-tag" :class="getStatusTagClass(item.statusText)">{{ item.statusText || '-' }}</view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无投诉记录</view>
        <view class="worker-empty__desc">员工本人提交过投诉后，企业端可在这里查看处理状态和工会协同结果。</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getComplaintList } from '../../api/enterprise-service'

const rows = ref([])

function getStatusTagClass(statusText) {
  const text = String(statusText || '')
  if (text.includes('待') || text.includes('处理中') || text.includes('受理')) {
    return 'worker-tag--warning'
  }
  if (text.includes('完成') || text.includes('已结') || text.includes('通过')) {
    return 'worker-tag--success'
  }
  if (text.includes('退') || text.includes('驳') || text.includes('失败')) {
    return 'worker-tag--danger'
  }
  return 'worker-tag--info'
}

async function loadList() {
  const result = await getComplaintList()
  rows.value = result?.rows || []
}

function openDetail(item) {
  if (!item?.complaintId) {
    return
  }
  uni.navigateTo({ url: `/pages/complaint/detail?complaintId=${item.complaintId}` })
}

function goLegal() {
  uni.navigateTo({ url: '/pages/legal/index' })
}

function goCamera() {
  uni.navigateTo({ url: '/pages/camera/index' })
}

onShow(() => {
  loadList().catch((error) => {
    uni.showToast({ title: error.message || '加载投诉记录失败', icon: 'none' })
  })
})
</script>
