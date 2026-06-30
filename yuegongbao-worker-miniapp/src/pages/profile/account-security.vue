<template>
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">账号安全</view>
      <view class="worker-subtitle">
        查看当前账号安全状态，切换登录方式、重新认证或联系支持处理异常。
      </view>
      <view class="hero-stat-grid">
        <view class="hero-stat">
          <view class="hero-stat__value">{{ detail.realNameStatusText || '-' }}</view>
          <view class="hero-stat__label">实名状态</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ detail.certStatusText || '-' }}</view>
          <view class="hero-stat__label">持证状态</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ loginModeText }}</view>
          <view class="hero-stat__label">登录方式</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">账号信息</view>
      </view>
      <view class="status-panel">
        <view class="status-panel__title">{{ securityStatus.title }}</view>
        <view class="status-panel__desc">{{ securityStatus.desc }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">当前账号</view>
        <view class="detail-row__value">{{ detail.userName || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">姓名</view>
        <view class="detail-row__value">{{ detail.personNameMasked || detail.personName || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">手机号</view>
        <view class="detail-row__value">{{ detail.mobileMasked || detail.mobile || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">身份证</view>
        <view class="detail-row__value">{{ detail.idCardMasked || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">所属企业</view>
        <view class="detail-row__value">{{ detail.enterpriseName || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">实名状态</view>
        <view class="detail-row__value">{{ detail.realNameStatusText || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">持证状态</view>
        <view class="detail-row__value">{{ detail.certStatusText || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">当前登录方式</view>
        <view class="detail-row__value">{{ loginModeText }}</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="worker-title">登录方式管理</view>
      <view class="action-list">
        <view class="action-row" @click="goRealnamePage">去实名认证</view>
        <view class="action-row" @click="copyAccount">复制账号</view>
        <view class="action-row" @click="copyMobile">复制手机号</view>
        <view class="action-row" @click="reAuth('password')">切换账号密码登录</view>
        <view class="action-row" @click="reAuth('sms')">切换短信验证码登录</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="worker-title">设备与缓存</view>
      <view class="action-list">
        <view class="action-row" @click="clearLocalCache">清理本地缓存</view>
        <view class="action-row action-row--danger" @click="logoutCurrentDevice">退出当前设备</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="worker-title">安全支持</view>
      <view class="tip-row">1. 当前支持账号密码登录和短信验证码登录。</view>
      <view class="tip-row">2. 如手机号与实名信息不一致，请联系管理员核对劳动者档案。</view>
      <view class="tip-row">3. 若怀疑账号异常，建议先退出当前设备，再使用正确方式重新认证。</view>
      <view class="service-actions">
        <button class="worker-button worker-button--secondary" @click="goHelpCenter">帮助中心</button>
        <button class="worker-button" @click="callHotline">联系客服</button>
      </view>
      <view class="service-hint">{{ hotlineText }}</view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getLegalHotline, getWorkerProfile } from '../../api/worker'
import {
  WORKER_API_BASE_URL_STORAGE_KEY,
  clearCurrentWorkerAuthState,
  getWorkerLoginType,
  getWorkerSessionSnapshot,
  restoreWorkerSessionSnapshot
} from '../../utils/request'

const detail = ref({})
const hotline = ref({})
const fallbackHotlineNumber = '12351'
const fallbackHotlineText = '工会法律服务热线 12351'

const loginModeText = computed(() => {
  const loginType = getWorkerLoginType() || ''
  if (loginType === 'worker-sms') {
    return '短信验证码登录'
  }
  if (loginType === 'worker-password') {
    return '账号密码登录'
  }
  return '未记录，默认按最近一次认证状态'
})

const securityStatus = computed(() => {
  if (!detail.value?.realNameVerified) {
    return {
      title: '待完成实名认证',
      desc: '当前实名资料未形成完整校验链，请先核对姓名、身份证和实名手机号是否一致。'
    }
  }
  if (detail.value?.certStatus === '2' || detail.value?.certStatus === '3') {
    return {
      title: '证书状态待处理',
      desc: `当前${detail.value?.certStatusText || '证书状态异常'}，建议尽快在简历页补齐证书资料并联系管理员核验。`
    }
  }
  if (!detail.value?.mobile) {
    return {
      title: '待核对账号资料',
      desc: '当前未读取到完整手机号信息，建议联系管理员核对劳动者档案。'
    }
  }
  return {
    title: '账号资料已绑定',
    desc: '可直接使用当前手机号进行短信登录，或使用账号密码重新认证。'
  }
})

const hotlineText = computed(() => hotline.value?.displayText || fallbackHotlineText)

const hotlineNumber = computed(() => {
  const phoneNumber = String(hotline.value?.phoneNumber || '').trim()
  if (phoneNumber) {
    return phoneNumber
  }
  const displayText = String(hotline.value?.displayText || '')
  const matched = displayText.match(/1\d{4,}/)
  return matched?.[0] || fallbackHotlineNumber
})

async function loadData() {
  try {
    const [profileData, hotlineData] = await Promise.all([
      getWorkerProfile(),
      getLegalHotline()
    ])
    detail.value = profileData || {}
    hotline.value = hotlineData || {}
  } catch (error) {
    uni.showToast({ title: error.message || '加载账号安全信息失败', icon: 'none' })
  }
}

function copyText(value, emptyTitle, successTitle) {
  if (!value) {
    uni.showToast({ title: emptyTitle, icon: 'none' })
    return
  }
  uni.setClipboardData({
    data: String(value),
    success: () => uni.showToast({ title: successTitle, icon: 'none' })
  })
}

function copyAccount() {
  copyText(detail.value?.userName, '当前无可复制账号', '账号已复制')
}

function copyMobile() {
  copyText(detail.value?.mobile, '当前无可复制手机号', '手机号已复制')
}

function reAuth(mode) {
  const modeLabel = mode === 'sms' ? '短信验证码登录' : '账号密码登录'
  uni.showModal({
    title: '重新认证',
    content: `将退出当前设备，并跳转到${modeLabel}页面继续登录。`,
    success: (res) => {
      if (!res.confirm) {
        return
      }
      clearCurrentWorkerAuthState()
      const query = [
        `mode=${mode}`,
        detail.value?.mobile ? `mobile=${encodeURIComponent(detail.value.mobile)}` : '',
        detail.value?.userName ? `username=${encodeURIComponent(detail.value.userName)}` : ''
      ].filter(Boolean).join('&')
      uni.reLaunch({ url: `/pages/login/index${query ? `?${query}` : ''}` })
    }
  })
}

function clearLocalCache() {
  const sessionSnapshot = getWorkerSessionSnapshot()
  const workerApiBaseUrl = uni.getStorageSync(WORKER_API_BASE_URL_STORAGE_KEY)
  uni.clearStorageSync()
  if (workerApiBaseUrl) {
    uni.setStorageSync(WORKER_API_BASE_URL_STORAGE_KEY, workerApiBaseUrl)
  }
  restoreWorkerSessionSnapshot(sessionSnapshot)
  uni.showToast({ title: '本地缓存已清理', icon: 'none' })
}

function logoutCurrentDevice() {
  uni.showModal({
    title: '退出当前设备',
    content: '退出后需要重新登录才能继续使用劳动者端服务。',
    success: (res) => {
      if (!res.confirm) {
        return
      }
      clearCurrentWorkerAuthState()
      uni.reLaunch({ url: '/pages/login/index' })
    }
  })
}

function callHotline() {
  uni.makePhoneCall({ phoneNumber: hotlineNumber.value })
}

function goHelpCenter() {
  uni.navigateTo({ url: '/pages/profile/help' })
}

function goRealnamePage() {
  uni.navigateTo({ url: '/pages/profile/real-name' })
}

onShow(loadData)
</script>

<style lang="scss">
.worker-card + .worker-card {
  margin-top: 24rpx;
}

.status-panel {
  margin-top: 18rpx;
  margin-bottom: 12rpx;
  padding: 22rpx 24rpx;
  border-radius: 20rpx;
  background: linear-gradient(135deg, #e6f2ef 0%, #f8fbff 100%);
}

.status-panel__title {
  font-size: 30rpx;
  font-weight: 700;
  color: #122d42;
}

.status-panel__desc {
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: #536b7d;
}

.action-list {
  margin-top: 18rpx;
}

.action-row {
  padding: 24rpx 0;
  border-bottom: 1rpx solid #e4edf2;
  font-size: 28rpx;
  color: #122d42;
}

.action-row:last-child {
  border-bottom: none;
}

.action-row--danger {
  color: #dc3545;
}

.tip-row {
  margin-top: 18rpx;
  font-size: 26rpx;
  line-height: 1.7;
  color: #183247;
}

.service-actions button {
  flex: 1;
}

.service-hint {
  margin-top: 14rpx;
  font-size: 24rpx;
  color: #607789;
}
</style>
