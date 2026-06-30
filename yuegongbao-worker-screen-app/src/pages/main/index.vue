<template>
  <view class="screen-page screen-shell">
    <view class="screen-layout">
      <ScreenSidebar current-path="/pages/main/index" />

      <view class="screen-layout__content">
        <view class="screen-stack">
          <view class="screen-topbar">
            <view class="screen-brand">
              <text class="screen-overline">SERVICE DIRECTORY</text>
              <text class="screen-title">服务目录总览</text>
              <text class="screen-desc">验收快照：主界面摘要改为后端聚合，一级分组固定为打卡、工资社保个税、维权服务、求职就业、培训通知和更多服务。</text>
            </view>
            <text class="screen-meta-pill">{{ workerName }} / {{ identityLabel }}</text>
          </view>

          <view class="screen-hero">
            <text class="screen-overline">TODAY</text>
            <text class="screen-title">今日服务摘要</text>
            <text class="screen-desc">首页摘要、主动作与返回路径均按服务屏语义展示，不再暴露手机端占位说明。</text>
            <view class="screen-hero-kpis">
              <view class="screen-hero-kpi" v-for="item in overview" :key="item.label">
                <text class="screen-hero-kpi-label">{{ item.label }}</text>
                <text class="screen-hero-kpi-value">{{ item.value }}</text>
              </view>
            </view>
          </view>

          <view class="screen-card">
            <text class="screen-section-title">服务分组</text>
            <text class="screen-section-subtitle">{{ tipsText }}</text>
            <view class="screen-grid">
              <view class="screen-panel screen-panel-strong">
                <text class="screen-panel-title">打卡</text>
                <text class="screen-panel-desc">上班打卡、下班打卡与考勤明细统一在考勤链路办理。</text>
                <view class="screen-button-row">
                  <view class="screen-button screen-button-primary" @click="openPage('/pages/attendance/checkin?action=checkIn')">上班打卡</view>
                  <view class="screen-button" @click="openPage('/pages/attendance/detail')">考勤明细</view>
                </view>
              </view>
              <view class="screen-panel">
                <text class="screen-panel-title">工资社保个税</text>
                <text class="screen-panel-desc">工资清单、社保详情和个税明细统一走真实后端查询。</text>
                <view class="screen-button-row">
                  <view class="screen-button" @click="openPage('/pages/salary/list')">工资</view>
                  <view class="screen-button" @click="openPage('/pages/social/list')">社保</view>
                </view>
              </view>
              <view class="screen-panel">
                <text class="screen-panel-title">维权服务</text>
                <text class="screen-panel-desc">投诉举报、法律咨询和工会服务聚合在维权专区。</text>
                <view class="screen-button-row">
                  <view class="screen-button" @click="openPage('/pages/complaint/index')">投诉举报</view>
                  <view class="screen-button" @click="openPage('/pages/legal/index')">法律咨询</view>
                </view>
              </view>
              <view class="screen-panel">
                <text class="screen-panel-title">求职就业</text>
                <text class="screen-panel-desc">岗位清单、岗位详情和附近岗位地图统一从屏端接口输出。</text>
                <view class="screen-button-row">
                  <view class="screen-button" @click="openPage('/pages/job/list')">岗位列表</view>
                  <view class="screen-button" @click="openPage('/pages/job/map')">附近岗位</view>
                </view>
              </view>
              <view class="screen-panel">
                <text class="screen-panel-title">培训通知</text>
                <text class="screen-panel-desc">培训进度、答题、通知公告和视频内容纳入统一入口。</text>
                <view class="screen-button-row">
                  <view class="screen-button" @click="openPage('/pages/training/index')">培训中心</view>
                  <view class="screen-button" @click="openPage('/pages/notice/list')">通知公告</view>
                </view>
              </view>
              <view class="screen-panel">
                <text class="screen-panel-title">更多服务</text>
                <text class="screen-panel-desc">个人资料、设置、活动和视频等二级页面继续按服务屏风格统一。</text>
                <view class="screen-button-row">
                  <view class="screen-button" @click="openPage('/pages/profile/index')">我的服务</view>
                  <view class="screen-button" @click="openPage('/pages/device/index')">设备管理</view>
                </view>
              </view>
            </view>
          </view>

          <view class="screen-card">
            <text class="screen-section-title">快捷入口</text>
            <view class="screen-action-grid screen-action-grid--screen">
              <view v-for="item in actions" :key="item.label" class="screen-action" @click="openPage(item.path)">
                <text class="screen-action-title">{{ item.label }}</text>
                <text class="screen-panel-desc">{{ item.desc }}</text>
                <text class="screen-action-meta">{{ item.meta }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import ScreenSidebar from '../../components/ScreenSidebar.vue'
import { getScreenDashboard } from '../../api/screen'
import { openPage, safeHideTabBar } from '../../utils/navigation'

const workerName = ref('劳动者')
const identityLabel = ref('待核验')
const overview = ref([])
const actions = ref([])
const tips = ref([])

const tipsText = computed(() => {
  if (!tips.value.length) {
    return '服务目录页已切换为服务屏专属布局，主链入口均可直达。'
  }
  return tips.value.join(' ')
})

async function loadData() {
  const data = await getScreenDashboard()
  workerName.value = data.workerName || '劳动者'
  identityLabel.value = data.identityLabel || '待核验'
  overview.value = Array.isArray(data.overview) ? data.overview : []
  actions.value = Array.isArray(data.actions) ? data.actions : []
  tips.value = Array.isArray(data.tips) ? data.tips : []
}

onMounted(() => {
  loadData()
})

onShow(() => {
  safeHideTabBar()
})
</script>
