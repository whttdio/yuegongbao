<template>
  <view class="screen-page screen-shell">
    <view class="screen-layout">
      <ScreenSidebar current-path="/pages/standby/index" />

      <view class="screen-layout__content">
        <view class="screen-stack">
          <view class="screen-topbar">
            <view class="screen-brand">
              <text class="screen-overline">GOVERNMENT SERVICE HALL</text>
              <text class="screen-title">粤工保服务大厅</text>
              <text class="screen-desc">验收快照：服务屏待机页已切换为大厅欢迎页，保留刷脸登录、投诉求助、岗位服务和设备管理四个主入口。</text>
            </view>
            <text class="screen-meta-pill">{{ currentTime }}</text>
          </view>

          <view class="screen-hero">
            <text class="screen-overline">WELCOME</text>
            <text class="screen-title">欢迎使用粤工保全链路服务屏</text>
            <text class="screen-desc">请根据屏幕指引完成刷脸登录、权益服务、岗位查询和设备维护。大厅屏强调大按钮、高可读和少层级操作。</text>
            <view class="screen-hero-kpis">
              <view class="screen-hero-kpi">
                <text class="screen-hero-kpi-label">设备状态</text>
                <text class="screen-hero-kpi-value">在线</text>
              </view>
              <view class="screen-hero-kpi">
                <text class="screen-hero-kpi-label">今日服务</text>
                <text class="screen-hero-kpi-value">联调可用</text>
              </view>
              <view class="screen-hero-kpi">
                <text class="screen-hero-kpi-label">支持范围</text>
                <text class="screen-hero-kpi-value">考勤 工资 维权 就业</text>
              </view>
            </view>
          </view>

          <view class="screen-card">
            <text class="screen-section-title">大厅主入口</text>
            <text class="screen-section-subtitle">接口联调快照：待机页入口统一指向真实页面路由，不再展示手机化占位入口。</text>
            <view class="screen-action-grid screen-action-grid--screen">
              <view class="screen-action screen-action-primary" @click="openPage('/pages/login/index')">
                <text class="screen-action-title">刷脸登录</text>
                <text class="screen-panel-desc">进入识别页，完成登录后进入服务目录。</text>
                <text class="screen-action-meta">主入口</text>
              </view>
              <view class="screen-action" @click="openPage('/pages/complaint/index')">
                <text class="screen-action-title">投诉求助</text>
                <text class="screen-panel-desc">提交投诉举报，进入维权服务链路。</text>
                <text class="screen-action-meta">权益服务</text>
              </view>
              <view class="screen-action" @click="openPage('/pages/job/list')">
                <text class="screen-action-title">岗位服务</text>
                <text class="screen-panel-desc">查看岗位列表、岗位详情和附近岗位。</text>
                <text class="screen-action-meta">就业服务</text>
              </view>
              <view class="screen-action" @click="openPage('/pages/device/index')">
                <text class="screen-action-title">设备管理</text>
                <text class="screen-panel-desc">查看设备状态、配置参数和动作回执。</text>
                <text class="screen-action-meta">运维入口</text>
              </view>
            </view>
          </view>

          <view class="screen-card">
            <text class="screen-section-title">服务说明</text>
            <view class="screen-list">
              <view class="screen-list-item">
                <view>
                  <text class="screen-list-title">主链联调</text>
                  <text class="screen-list-desc">登录、考勤、工资、社保、个税、投诉和岗位服务已纳入服务屏正式路由。</text>
                </view>
                <text class="screen-status screen-status-success">已接通</text>
              </view>
              <view class="screen-list-item">
                <view>
                  <text class="screen-list-title">硬件能力</text>
                  <text class="screen-list-desc">摄像头和设备网关允许降级，但页面与接口回执必须可用。</text>
                </view>
                <text class="screen-status screen-status-warn">可降级</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { onShow } from '@dcloudio/uni-app'
import { ref } from 'vue'
import ScreenSidebar from '../../components/ScreenSidebar.vue'
import { safeHideTabBar } from '../../utils/navigation'
import { openPage } from '../../utils/navigation'

const currentTime = ref('')

function updateTime() {
  currentTime.value = new Date().toLocaleString()
}

onShow(() => {
  safeHideTabBar()
  updateTime()
})
</script>
