<template>
  <view class="worker-page worker-page--tab worker-page--screen">
    <view class="profile-hero worker-card worker-hero">
      <view class="profile-hero__main">
        <view class="profile-hero__avatar">{{ avatarInitial }}</view>
        <view class="profile-hero__info">
          <view class="profile-hero__name">{{ profile.personNameMasked || profile.personName || '劳动者用户' }}</view>
          <view class="profile-hero__enterprise">{{ profile.enterpriseName || '未绑定企业' }}</view>
          <view v-if="profile.jobType" class="profile-hero__tag">{{ profile.jobType }}</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head"><view class="worker-title worker-title--small">基本信息</view></view>
      <view class="profile-grid">
        <view class="profile-item">
          <view class="profile-item__label">手机号</view>
          <view class="profile-item__value">{{ profile.mobileMasked || profile.mobile || '-' }}</view>
        </view>
        <view class="profile-item">
          <view class="profile-item__label">身份证</view>
          <view class="profile-item__value">{{ profile.idCardMasked || '-' }}</view>
        </view>
        <view class="profile-item">
          <view class="profile-item__label">工种</view>
          <view class="profile-item__value">{{ profile.jobType || '-' }}</view>
        </view>
        <view class="profile-item">
          <view class="profile-item__label">参保状态</view>
          <view class="profile-item__value">{{ profile.insuranceStatus || '-' }}</view>
        </view>
      </view>
    </view>

    <view class="worker-card worker-card--pressable" @click="goSecurity">
      <view class="section-head">
        <view class="worker-title">保险保障</view>
        <view class="worker-tag">查看详情</view>
      </view>
      <view class="profile-grid">
        <view class="profile-item">
          <view class="profile-item__label">工伤保险</view>
          <view class="profile-item__value">{{ security.injuryInsuranceStatus || '-' }}</view>
        </view>
        <view class="profile-item">
          <view class="profile-item__label">安责险状态</view>
          <view class="profile-item__value">{{ security.aqInsuranceStatus || '-' }}</view>
        </view>
        <view class="profile-item">
          <view class="profile-item__label">有效期</view>
          <view class="profile-item__value profile-item__value--small">{{ security.aqInsurancePeriod || '-' }}</view>
        </view>
        <view class="profile-item">
          <view class="profile-item__label">预防资金余额</view>
          <view class="profile-item__value">{{ security.aqInsuranceRemainingFund || '-' }}</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">我的服务</view>
        <view v-if="hasUnreadNotice" class="worker-tag worker-tag--notice">未读 {{ unreadNoticeText }}</view>
      </view>
      <view class="settings-list">
        <view class="settings-row" @click="goNoticeList">消息中心</view>
        <view class="settings-row" @click="goRealname">实名认证</view>
        <view class="settings-row" @click="goSecurity">保险保障</view>
        <view class="settings-row" @click="goLaborContracts">我的合同</view>
        <view class="settings-row" @click="goResume">我的简历</view>
        <view class="settings-row" @click="goApplyList">投递记录</view>
        <view class="settings-row" @click="goUploadRecords">上传记录</view>
        <view class="settings-row" @click="goPoints">积分商城</view>
        <view class="settings-row" @click="goUnion">工会服务</view>
        <view class="settings-row" @click="goHelp">帮助中心</view>
        <view class="settings-row" @click="goSettings">设置</view>
        <view class="settings-row settings-row--danger settings-row--no-arrow" @click="logout">退出登录</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, reactive } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getWorkerInsuranceSecurity, getWorkerProfile } from '../../api/worker'
import { clearCurrentWorkerAuthState, setWorkerProfileExportSnapshot } from '../../utils/request'

const profile = reactive({})
const security = reactive({})
const unreadNoticeCount = computed(() => Number(profile.unreadNoticeCount || 0))
const hasUnreadNotice = computed(() => unreadNoticeCount.value > 0)
const unreadNoticeText = computed(() => (unreadNoticeCount.value > 99 ? '99+' : String(unreadNoticeCount.value || 0)))
const avatarInitial = computed(() => String(profile.personNameMasked || profile.personName || '劳').charAt(0))

function persistWorkerProfileExportSnapshot() {
  setWorkerProfileExportSnapshot({
    personName: profile.personName || '',
    personNameMasked: profile.personNameMasked || '',
    enterpriseName: profile.enterpriseName || '',
    mobile: profile.mobile || '',
    mobileMasked: profile.mobileMasked || ''
  })
}

async function loadProfile() {
  try {
    const [profileData, securityData] = await Promise.all([getWorkerProfile(), getWorkerInsuranceSecurity()])
    Object.assign(profile, profileData || {})
    Object.assign(security, securityData || {})
    persistWorkerProfileExportSnapshot()
  } catch (error) {
    uni.showToast({ title: error.message || '加载失败', icon: 'none' })
  }
}

function logout() {
  clearCurrentWorkerAuthState()
  uni.reLaunch({ url: '/pages/login/index' })
}

function goResume() { uni.navigateTo({ url: '/pages/profile/resume' }) }
function goRealname() { uni.navigateTo({ url: '/pages/profile/real-name' }) }
function goApplyList() { uni.navigateTo({ url: '/pages/job/apply-list' }) }
function goUploadRecords() { uni.navigateTo({ url: '/pages/profile/upload-records' }) }
function goLaborContracts() { uni.navigateTo({ url: '/pages/profile/labor-contracts' }) }
function goHelp() { uni.navigateTo({ url: '/pages/profile/help' }) }
function goNoticeList() { uni.navigateTo({ url: '/pages/notice/list' }) }
function goPoints() { uni.navigateTo({ url: '/pages/profile/points' }) }
function goSecurity() { uni.navigateTo({ url: '/pages/profile/security' }) }
function goUnion() { uni.navigateTo({ url: '/pages/union/index' }) }
function goSettings() { uni.navigateTo({ url: '/pages/profile/settings' }) }

onShow(loadProfile)
</script>
