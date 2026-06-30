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
      <view class="section-head">
        <view class="worker-title worker-title--small">基本信息</view>
      </view>
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
          <view class="profile-item__label">安责险有效期</view>
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
        <view class="settings-row" @click="goNoticeList">
          <text>消息中心</text>
          <text v-if="hasUnreadNotice" class="settings-row__tag">未读 {{ unreadNoticeText }}</text>
        </view>
        <view class="settings-row" @click="goRealname">实名认证</view>
        <view class="settings-row" @click="goSecurity">保险保障</view>
        <view class="settings-row" @click="goLaborContracts">我的合同</view>
        <view class="settings-row" @click="goResume">我的简历</view>
        <view class="settings-row" @click="goApplyList">投递记录</view>
        <view class="settings-row" @click="goUploadRecords">上传归档</view>
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
import { computed, reactive, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getWorkerInsuranceSecurity, getWorkerProfile } from '../../api/screen'
import { clearCurrentWorkerAuthState, setWorkerProfileExportSnapshot } from '../../utils/request'

const profile = reactive({})
const security = reactive({})
const profileLastLoadedAt = ref('')
const profileLastMessage = ref('')

const unreadNoticeCount = computed(() => Number(profile.unreadNoticeCount || 0))
const hasUnreadNotice = computed(() => unreadNoticeCount.value > 0)

const unreadNoticeText = computed(() => {
  if (unreadNoticeCount.value > 99) {
    return '99+'
  }
  return String(unreadNoticeCount.value || 0)
})

const avatarInitial = computed(() => {
  const name = profile.personNameMasked || profile.personName || '劳'
  return String(name).charAt(0)
})
const profileSummaryText = computed(() => {
  return `${profile.personNameMasked || profile.personName || '劳动者用户'} / ${profile.enterpriseName || '未绑定企业'} / ${profile.jobType || '-'}`
})
const securitySummaryText = computed(() => {
  return `工伤 ${security.injuryInsuranceStatus || '-'} / 安责险 ${security.aqInsuranceStatus || '-'} / 余额 ${security.aqInsuranceRemainingFund || '-'}`
})
const serviceSummaryText = computed(() => '消息中心 / 实名认证 / 保险保障 / 我的合同 / 我的简历 / 投递记录 / 上传归档 / 积分商城 / 工会服务 / 帮助中心 / 设置')
const noticeSummaryText = computed(() => hasUnreadNotice.value ? `未读 ${unreadNoticeText.value}` : '无未读通知')
const profileSnapshotText = computed(() => {
  return [
    '## 个人中心验收摘要',
    `- 最近加载：${profileLastLoadedAt.value || '-'}`,
    `- 个人资料：${profileSummaryText.value}`,
    `- 保障摘要：${securitySummaryText.value}`,
    `- 服务入口：${serviceSummaryText.value}`,
    `- 消息状态：${noticeSummaryText.value}`,
    `- 说明：${profileLastMessage.value || '-'}`,
    '- 链路关联：个人中心 / 保险保障 / 消息中心 / 我的服务入口'
  ].join('\n')
})

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
    const [profileData, securityData] = await Promise.all([
      getWorkerProfile(),
      getWorkerInsuranceSecurity()
    ])
    Object.assign(profile, profileData || {})
    Object.assign(security, securityData || {})
    persistWorkerProfileExportSnapshot()
    profileLastLoadedAt.value = new Date().toLocaleString()
    profileLastMessage.value = '个人中心与保险摘要已加载，可核对未读消息和服务入口'
  } catch (error) {
    profileLastLoadedAt.value = new Date().toLocaleString()
    profileLastMessage.value = error.message || '加载个人信息失败'
    uni.showToast({ title: error.message || '加载个人信息失败', icon: 'none' })
  }
}

function logout() {
  profileLastMessage.value = '已执行退出登录'
  clearCurrentWorkerAuthState()
  uni.reLaunch({ url: '/pages/login/index' })
}

function goResume() {
  uni.navigateTo({ url: '/pages/profile/resume' })
}

function goRealname() {
  uni.navigateTo({ url: '/pages/profile/real-name' })
}

function goApplyList() {
  uni.navigateTo({ url: '/pages/job/apply-list' })
}

function goUploadRecords() {
  uni.navigateTo({ url: '/pages/profile/upload-records' })
}

function goLaborContracts() {
  uni.navigateTo({ url: '/pages/profile/labor-contracts' })
}

function goHelp() {
  uni.navigateTo({ url: '/pages/profile/help' })
}

function goNoticeList() {
  uni.navigateTo({ url: '/pages/notice/list' })
}

function goPoints() {
  uni.navigateTo({ url: '/pages/profile/points' })
}

function goSecurity() {
  uni.navigateTo({ url: '/pages/profile/security' })
}

function goUnion() {
  uni.navigateTo({ url: '/pages/union/index' })
}

function goSettings() {
  uni.navigateTo({ url: '/pages/profile/settings' })
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

onShow(loadProfile)
</script>

<style lang="scss">
.profile-hero__main {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.profile-hero__avatar {
  flex-shrink: 0;
  width: 108rpx;
  height: 108rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.22);
  border: 3rpx solid rgba(255, 255, 255, 0.35);
  color: #fff;
  font-size: 44rpx;
  font-weight: 750;
  line-height: 108rpx;
  text-align: center;
}

.profile-hero__name {
  font-size: 38rpx;
  font-weight: 800;
  color: #fff;
  line-height: 1.25;
}

.profile-hero__enterprise {
  margin-top: 8rpx;
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.86);
  line-height: 1.5;
}

.profile-hero__tag {
  display: inline-flex;
  margin-top: 14rpx;
  padding: 6rpx 16rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.18);
  color: rgba(255, 255, 255, 0.95);
  font-size: 22rpx;
  font-weight: 650;
}
</style>
