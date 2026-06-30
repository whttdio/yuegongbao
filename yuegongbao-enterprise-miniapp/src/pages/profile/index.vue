<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page worker-page--tab enterprise-page">
    <view class="worker-card worker-hero">
      <view class="profile-hero__main">
        <view class="profile-hero__avatar">企</view>
        <view>
          <view class="profile-hero__name">企业管理员</view>
          <view class="profile-hero__enterprise">广州某高危作业服务有限公司</view>
          <view class="profile-hero__tag">本企业数据范围</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">企业信息</view>
      </view>
      <view class="profile-grid">
        <view class="profile-item">
          <view class="profile-item__label">统一信用代码</view>
          <view class="profile-item__value">91440101MA5XXXXX8</view>
        </view>
        <view class="profile-item">
          <view class="profile-item__label">行业</view>
          <view class="profile-item__value">高危设备运维</view>
        </view>
        <view class="profile-item">
          <view class="profile-item__label">联系人</view>
          <view class="profile-item__value">张经理</view>
        </view>
        <view class="profile-item">
          <view class="profile-item__label">联系电话</view>
          <view class="profile-item__value">138****8000</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">消息与安全</view>
      </view>
      <view class="settings-list">
        <view class="settings-row" @click="toast('系统通知')">系统通知</view>
        <view class="settings-row" @click="toast('预警推送')">预警推送</view>
        <view class="settings-row" @click="toast('任务提醒')">任务提醒</view>
        <view class="settings-row" @click="toast('账号安全')">账号安全</view>
        <view class="settings-row" @click="toast('帮助与反馈')">帮助与反馈</view>
        <view class="settings-row" @click="toast('清理缓存')">清理缓存</view>
        <view class="settings-row settings-row--danger settings-row--no-arrow" @click="toast('退出登录')">退出登录</view>
      </view>
    </view>
  </view>
</template>

<script setup>
function toast(label) {
  uni.showToast({ title: `${label} 已预留入口`, icon: 'none' })
}
</script>
