<template>
  <view class="screen-page screen-shell">
    <view class="screen-layout">
      <ScreenSidebar current-path="/pages/login/index" />

      <view class="screen-layout__content">
        <view class="screen-stack">
          <view class="screen-topbar">
            <view class="screen-brand">
              <text class="screen-overline">FACE AUTH</text>
              <text class="screen-title">刷脸识别登录</text>
              <text class="screen-desc">联调快照：登录页统一走 `/app/screen/face-auth/mock`，成功后写入真实 token 与账号上下文。</text>
            </view>
            <text class="screen-meta-pill">{{ stateLabel }}</text>
          </view>

          <view class="screen-card screen-auth-box">
            <view class="screen-avatar-ring">
              <view class="screen-auth-avatar" />
            </view>
            <text class="screen-title">{{ titleText }}</text>
            <text class="screen-desc">{{ descText }}</text>
            <text v-if="tipText" class="screen-section-subtitle">{{ tipText }}</text>
          </view>

          <view class="screen-card">
            <text class="screen-section-title">识别流程</text>
            <text class="screen-section-subtitle">识别中、成功、失败三态均有明确反馈，管理员可返回待机页或进入设备管理。</text>
            <view class="screen-button-row">
              <view class="screen-button screen-button-primary" @click="handleIdentify('running')">开始识别</view>
              <view class="screen-button" @click="handleIdentify('success')">模拟成功</view>
              <view class="screen-button" @click="handleIdentify('failed')">模拟失败</view>
              <view class="screen-button" @click="goStandby">返回待机</view>
            </view>
          </view>

          <view class="screen-card">
            <text class="screen-section-title">状态说明</text>
            <view class="screen-list">
              <view class="screen-list-item">
                <view>
                  <text class="screen-list-title">识别链路</text>
                  <text class="screen-list-desc">成功后会持久化登录 token、登录方式和登录账号，再跳转到服务目录页。</text>
                </view>
                <text class="screen-status screen-status-success">真实回执</text>
              </view>
              <view class="screen-list-item">
                <view>
                  <text class="screen-list-title">硬件能力</text>
                  <text class="screen-list-desc">本轮允许摄像头与算法降级，但必须保留统一提示与接口位。</text>
                </view>
                <text class="screen-status screen-status-warn">允许降级</text>
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
import { computed, ref } from 'vue'
import ScreenSidebar from '../../components/ScreenSidebar.vue'
import { runFaceAuthMock } from '../../api/screen'
import { openPage, safeHideTabBar } from '../../utils/navigation'
import { setWorkerLoginAccount, setWorkerLoginType, setWorkerToken } from '../../utils/request'

const state = ref('idle')
const title = ref('请站在识别区中央，准备进行刷脸登录')
const desc = ref('登录成功后可进入考勤、工资社保个税、维权服务、岗位服务和培训通知等主链页面。')
const busy = ref(false)
const tip = ref('管理员可在设备页查看摄像头、网络与动作回执状态。')

const titleText = computed(() => title.value)
const descText = computed(() => desc.value)
const tipText = computed(() => tip.value)

const stateLabel = computed(() => {
  if (busy.value || state.value === 'running') return '识别中'
  if (state.value === 'success') return '识别成功'
  if (state.value === 'failed') return '识别失败'
  return '待识别'
})

async function handleIdentify(result) {
  if (busy.value) {
    return
  }
  busy.value = true
  try {
    const response = await runFaceAuthMock({ result })
    state.value = response.state || result
    title.value = response.title || title.value
    desc.value = response.desc || desc.value
    tip.value = response.state === 'running' ? '请保持正对屏幕，避免遮挡面部。' : '识别状态已由后端返回，可继续执行下一步。'

    if (response.state === 'success' && response.token) {
      setWorkerToken(response.token)
      setWorkerLoginType(response.loginType || 'worker-face')
      setWorkerLoginAccount(response.loginAccount || 'screen-face-auth')
    }

    if (response.state === 'success' && response.nextAction) {
      setTimeout(() => {
        openPage(response.nextAction)
      }, 500)
    }
  } catch (error) {
    state.value = 'failed'
    title.value = '识别失败，请稍后重试'
    desc.value = error?.message || '服务屏登录请求失败'
    tip.value = '如需排查，请前往设备页查看网络与设备状态。'
  } finally {
    busy.value = false
  }
}

function goStandby() {
  openPage('/pages/standby/index')
}

onShow(() => {
  safeHideTabBar()
})
</script>
