<template>
  <view class="worker-page worker-page--tab worker-page--screen">
    <view class="home-hero worker-card worker-hero">
      <view class="home-head">
        <view>
          <view class="home-platform">{{ home.platformLabel || '广东省用工保障监测平台 v6.1' }}</view>
          <view class="home-title">{{ home.brandTitle || '阳光劳务' }}</view>
          <view class="worker-subtitle">{{ home.enterpriseName || '未绑定用工单位' }}</view>
        </view>
        <view class="home-head__actions">
          <view class="home-head__action home-head__action--notice" @click="goNoticeList">
            <text>通知</text>
            <text v-if="hasUnreadNotice" class="home-head__badge">{{ unreadNoticeText }}</text>
          </view>
          <view class="home-head__action" @click="goSettings">设置</view>
          <view class="worker-tag">{{ home.weekText || '-' }}</view>
        </view>
      </view>
      <view class="home-date">{{ home.dateText || '-' }}</view>
      <view class="home-worker-card">
        <view>
          <view class="home-worker-card__name">{{ home.workerCard?.personNameMasked || '劳动者用户' }}</view>
          <view class="home-worker-card__meta">
            {{ home.workerCard?.jobType || '-' }} / {{ home.workerCard?.workerType || '-' }}
          </view>
        </view>
        <view class="home-worker-card__qr">
          <image
            v-if="home.workerCard?.qrCodeUrl"
            class="home-worker-card__qr-image"
            :src="home.workerCard.qrCodeUrl"
            mode="aspectFit"
          />
          <view class="home-worker-card__qr-text">{{ home.workerCard?.qrCodeText || '-' }}</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">本月培训进度</view>
        <view class="worker-tag">{{ progressText }}</view>
      </view>
      <view class="worker-subtitle">
        {{ home.clockRuleTip || '完成每月 10 道安全培训题后，才能解锁打卡和工资查询。' }}
      </view>
      <view class="home-action-row worker-button-row">
        <button class="worker-button" @click="goCheckIn">上班打卡</button>
        <button class="worker-button worker-button--secondary" @click="goCheckOut">下班打卡</button>
      </view>
      <view class="home-training-link" @click="goTraining">去完成培训</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">功能入口</view>
      </view>
      <view class="entry-grid">
        <view
          v-for="item in home.quickEntries || []"
          :key="item.key"
          class="entry-item"
          :class="{ 'entry-item--locked': item.locked }"
          @click="openEntry(item)"
        >
          <view class="entry-item__icon" :class="'entry-item__icon--' + getEntryIcon(item).tone">
            <text class="entry-item__glyph">{{ getEntryIcon(item).glyph }}</text>
          </view>
          <view class="entry-item__label">{{ item.label }}</view>
          <view v-if="item.locked" class="entry-item__tip">需先完成培训</view>
        </view>
      </view>
    </view>
    <view v-if="(home.moreEntries || []).length" class="worker-card">
      <view class="section-head">
        <view class="worker-title">更多功能</view>
      </view>
      <view class="entry-grid">
        <view
          v-for="item in home.moreEntries || []"
          :key="item.key"
          class="entry-item"
          @click="openEntry(item)"
        >
          <view class="entry-item__icon" :class="'entry-item__icon--' + getEntryIcon(item).tone">
            <text class="entry-item__glyph">{{ getEntryIcon(item).glyph }}</text>
          </view>
          <view class="entry-item__label">{{ item.label }}</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">福利活动</view>
      </view>
      <view class="home-activity-title">{{ home.activityCard?.title || '-' }}</view>
      <view class="worker-subtitle">{{ home.activityCard?.subtitle || '-' }}</view>
      <button class="worker-button" @click="openActivity">{{ home.activityCard?.buttonText || '立即参与' }}</button>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">重要通知</view>
        <view class="notice-link" @click="goNoticeList">
          {{ hasUnreadNotice ? `全部 / 未读 ${unreadNoticeText}` : '全部' }}
        </view>
      </view>
      <view v-if="(home.noticeList || []).length">
        <view v-for="item in home.noticeList" :key="item.noticeId" class="notice-row" @click="openNotice(item)">
          <view class="notice-row__title">{{ item.title }}</view>
          <view class="notice-row__meta">{{ item.isRead ? '已读' : '未读' }}</view>
        </view>
      </view>
      <view v-else class="worker-empty">暂无通知</view>
    </view>

    <view v-if="(home.recommendCards || []).length" class="worker-card">
      <view class="section-head">
        <view class="worker-title">精选推荐</view>
      </view>
      <view
        v-for="item in home.recommendCards || []"
        :key="item.key"
        class="recommend-row"
        @click="openRecommend(item)"
      >
        <view class="recommend-row__title">{{ item.title }}</view>
        <view class="recommend-row__desc">{{ item.desc }}</view>
      </view>
    </view>

    <view v-if="(home.videoCards || []).length" class="worker-card">
      <view class="section-head">
        <view class="worker-title">工伤预防知识培训视频</view>
        <view class="notice-link" @click="goVideoList">全部</view>
      </view>
      <view
        v-for="item in home.videoCards || []"
        :key="item.videoKey || item.path"
        class="recommend-row"
        @click="openVideo(item)"
      >
        <view class="recommend-row__title">{{ item.title }}</view>
        <view class="recommend-row__desc">建议时长 {{ item.duration || item.durationText || '00:00' }}</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getWorkerHome } from '../../api/worker'
import { openPage } from '../../utils/navigation'
import { normalizeWorkerJumpTarget, openWorkerJumpTarget } from '../../utils/worker-jump'
import { resolveEntryIcon } from '../../utils/entry-icon'

const EXPECTED_HOME_SECTIONS = ['头部', '培训打卡', '九宫格', '更多功能', '福利活动', '重要通知', '精选推荐', '工伤预防视频']
const EXPECTED_HOME_ENTRY_LABELS = ['考勤', '工资', '社保', '个税', '培训', '拍照', '找工作', '法律咨询', '投诉举报']

const home = reactive({})
const homeLastLoadedAt = ref('')
const homeLastActionAt = ref('')
const homeLastMessage = ref('')

function getEntryIcon(item) {
  return resolveEntryIcon(item)
}

const progressText = computed(() => {
  const progress = home.trainingProgress || {}
  return `${progress.completed || 0}/${progress.total || 10}`
})

const unreadNoticeCount = computed(() => Number(home.unreadNoticeCount || 0))
const hasUnreadNotice = computed(() => unreadNoticeCount.value > 0)

const unreadNoticeText = computed(() => {
  if (unreadNoticeCount.value > 99) {
    return '99+'
  }
  return String(unreadNoticeCount.value || 0)
})
const homeSectionOrderText = computed(() => EXPECTED_HOME_SECTIONS.join(' / '))
const homeSpecCoverageText = computed(() => {
  const coverage = [
    '头部',
    '培训打卡',
    (home.quickEntries || []).length ? '九宫格' : '',
    (home.moreEntries || []).length ? '更多功能' : '',
    home.activityCard?.title ? '福利活动' : '',
    (home.noticeList || []).length ? '重要通知' : '',
    (home.recommendCards || []).length ? '精选推荐' : '',
    (home.videoCards || []).length ? '工伤预防视频' : ''
  ].filter(Boolean)
  const missing = EXPECTED_HOME_SECTIONS.filter((item) => !coverage.includes(item))
  return missing.length ? `已覆盖 ${coverage.length}/${EXPECTED_HOME_SECTIONS.length} / 缺 ${missing.join('、')}` : '主链结构已齐'
})
const quickEntrySummaryText = computed(() => {
  const entries = home.quickEntries || []
  const lockedCount = entries.filter((item) => item.locked).length
  return `${entries.length} 项 / 锁定 ${lockedCount} 项`
})
const quickEntrySpecText = computed(() => {
  const labels = (home.quickEntries || []).map((item) => String(item.label || '').trim()).filter(Boolean)
  const missing = EXPECTED_HOME_ENTRY_LABELS.filter((item) => !labels.includes(item))
  return missing.length ? `缺 ${missing.join('、')}` : '九宫格口径已齐'
})
const trainingGateSummaryText = computed(() => {
  const parts = [`进度 ${progressText.value}`]
  parts.push(home.canCheckIn ? '已解锁打卡' : '未解锁打卡')
  return parts.join(' / ')
})
const homeTrainingConsistencyText = computed(() => {
  return home.canCheckIn
    ? '培训已完成后，首页打卡、工作台常用服务、工资页和打卡页应同步恢复可进入'
    : '培训未完成时，首页打卡、工作台锁定入口、工资页和打卡页应保持同口径拦截'
})
const contentSectionSummaryText = computed(() => {
  const noticeCount = (home.noticeList || []).length
  const recommendCount = (home.recommendCards || []).length
  const videoCount = (home.videoCards || []).length
  const activityReady = home.activityCard?.title ? '活动已配置' : '活动待补'
  return `${activityReady} / 通知 ${noticeCount} / 推荐 ${recommendCount} / 视频 ${videoCount}`
})
const homeAcceptanceSnapshotText = computed(() => {
  const quickEntryLabels = (home.quickEntries || []).map((item) => `${item.label}${item.locked ? '(锁定)' : ''}`).join('、') || '-'
  const moreEntryLabels = (home.moreEntries || []).map((item) => item.label).join('、') || '-'
  return [
    '## 首页验收摘要',
    `- 最近加载：${homeLastLoadedAt.value || '-'}`,
    `- 最近联动：${homeLastActionAt.value || '-'}`,
    `- 说明书核对：${homeSpecCoverageText.value}`,
    `- 主链顺序：${homeSectionOrderText.value}`,
    `- 平台标题：${home.platformLabel || '-'}`,
    `- 品牌标题：${home.brandTitle || '-'}`,
    `- 企业信息：${home.enterpriseName || '-'}`,
    `- 培训进度：${progressText.value}`,
    `- 打卡解锁：${home.canCheckIn ? '已解锁' : '未解锁'}`,
    `- 解锁一致性：${homeTrainingConsistencyText.value}`,
    `- 九宫格数量：${(home.quickEntries || []).length}`,
    `- 九宫格内容：${quickEntryLabels}`,
    `- 九宫格核对：${quickEntrySpecText.value}`,
    `- 更多功能：${moreEntryLabels}`,
    `- 福利活动：${home.activityCard?.title || '-'}`,
    `- 重要通知：${(home.noticeList || []).length} 条`,
    `- 精选推荐：${(home.recommendCards || []).length} 条`,
    `- 工伤预防视频：${(home.videoCards || []).length} 条`,
    `- 内容摘要：${contentSectionSummaryText.value}`,
    `- 加载说明：${homeLastMessage.value || '-'}`
  ].join('\n')
})

async function loadHome() {
  try {
    const data = await getWorkerHome()
    Object.assign(home, data || {})
    homeLastLoadedAt.value = new Date().toLocaleString()
    const missingEntries = EXPECTED_HOME_ENTRY_LABELS.filter((item) => !(home.quickEntries || []).some((entry) => entry.label === item))
    homeLastMessage.value = [
      `首页返回 ${(home.quickEntries || []).length} 个功能入口`,
      `通知 ${(home.noticeList || []).length} 条`,
      missingEntries.length ? `缺少 ${missingEntries.join('、')}` : '九宫格口径已齐',
      home.canCheckIn ? '培训完成后可继续核对工资与打卡解锁' : '培训未完成时需继续核对工资与打卡锁定'
    ].join(' / ')
  } catch (error) {
    homeLastLoadedAt.value = new Date().toLocaleString()
    homeLastMessage.value = error.message || '加载首页失败'
    uni.showToast({ title: error.message || '加载首页失败', icon: 'none' })
  }
}

function recordHomeAction(action, detail) {
  homeLastActionAt.value = new Date().toLocaleString()
  homeLastMessage.value = detail ? `${action} / ${detail}` : action
}

function resolveActionTarget(item) {
  if (!item || typeof item !== 'object') {
    return null
  }
  return normalizeWorkerJumpTarget(item)
}

function openActionTarget(item, invalidTitle) {
  const target = resolveActionTarget(item)
  if (!target?.path) {
    if (invalidTitle) {
      uni.showToast({ title: invalidTitle, icon: 'none' })
    }
    return false
  }
  return openWorkerJumpTarget(target)
}

function ensureAttendanceReady() {
  if (!home.canCheckIn) {
    recordHomeAction('首页打卡拦截', home.clockRuleTip || '请先完成培训')
    uni.showToast({ title: home.clockRuleTip || '请先完成培训', icon: 'none' })
    return false
  }
  return true
}

function goCheckIn() {
  if (!ensureAttendanceReady()) {
    return
  }
  recordHomeAction('进入上班打卡', '首页双按钮联动 attendance/checkin?action=checkIn')
  uni.navigateTo({ url: '/pages/attendance/checkin?action=checkIn' })
}

function goCheckOut() {
  if (!ensureAttendanceReady()) {
    return
  }
  recordHomeAction('进入下班打卡', '首页双按钮联动 attendance/checkin?action=checkOut')
  uni.navigateTo({ url: '/pages/attendance/checkin?action=checkOut' })
}

function goTraining() {
  recordHomeAction('进入培训首页', '从首页培训打卡区进入本月培训')
  openPage('/pages/training/index')
}

function openEntry(item) {
  if (!item) {
    return
  }
  if (item.locked) {
    recordHomeAction(`九宫格锁定：${item.label || '-'}`, item.lockReason || '请先完成培训')
    uni.showToast({ title: item.lockReason || '请先完成培训', icon: 'none' })
    return
  }
  if (openActionTarget(item)) {
    recordHomeAction(`打开九宫格：${item.label || '-'}`, item.path || item.jumpPath || item.pagePath || '快捷入口跳转成功')
    return
  }
  recordHomeAction(`九宫格跳转失败：${item.label || '-'}`, '目标地址无效')
  uni.showToast({ title: `${item.label} 跳转地址无效`, icon: 'none' })
}

function openRecommend(item) {
  if (!item) {
    return
  }
  if (openActionTarget(item)) {
    recordHomeAction(`打开推荐内容：${item.title || '-'}`, item.path || item.jumpPath || item.pagePath || '推荐入口跳转成功')
    return
  }
  recordHomeAction(`推荐跳转失败：${item.title || '-'}`, '目标地址无效')
  uni.showToast({ title: `${item.title || '推荐内容'} 跳转地址无效`, icon: 'none' })
}

function openActivity() {
  const externalUrl = home.activityCard?.externalUrl
  if (externalUrl) {
    recordHomeAction(`打开福利活动：${home.activityCard?.title || '-'}`, '外链活动详情')
    const title = encodeURIComponent(home.activityCard?.title || '福利活动')
    const url = encodeURIComponent(externalUrl)
    uni.navigateTo({ url: `/pages/activity/detail?viewMode=external&title=${title}&externalUrl=${url}` })
    return
  }
  if (!home.activityCard) {
    return
  }
  if (openActionTarget(home.activityCard)) {
    recordHomeAction(`打开福利活动：${home.activityCard?.title || '-'}`, home.activityCard?.path || home.activityCard?.jumpPath || home.activityCard?.pagePath || '活动入口跳转成功')
    return
  }
  const jumpUrl = home.activityCard?.jumpUrl
  if (typeof jumpUrl === 'string' && jumpUrl.startsWith('/pages/')) {
    recordHomeAction(`打开福利活动：${home.activityCard?.title || '-'}`, jumpUrl)
    openPage(jumpUrl)
  }
}

function goNoticeList() {
  recordHomeAction('进入通知列表', '首页重要通知全部入口')
  uni.navigateTo({ url: '/pages/notice/list' })
}

function openNotice(item) {
  recordHomeAction(`打开通知：${item?.title || item?.noticeId || '-'}`, '首页重要通知详情')
  uni.navigateTo({ url: `/pages/notice/detail?noticeId=${item.noticeId}` })
}

function goVideoList() {
  recordHomeAction('进入视频列表', '首页工伤预防视频全部入口')
  uni.navigateTo({ url: '/pages/video/list' })
}

function goSettings() {
  recordHomeAction('进入设置页', '首页头部设置入口')
  uni.navigateTo({ url: '/pages/profile/settings' })
}

function openVideo(item) {
  if (!item) {
    return
  }
  if (openActionTarget(item)) {
    recordHomeAction(`打开视频：${item.title || '-'}`, item.path || item.jumpPath || item.pagePath || '视频入口跳转成功')
    return
  }
  recordHomeAction(`视频跳转失败：${item.title || '-'}`, '目标地址无效')
  uni.showToast({ title: `${item.title || '视频'} 跳转地址无效`, icon: 'none' })
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

onShow(loadHome)
</script>

<style lang="scss">
.home-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18rpx;
}

.home-head__actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: flex-end;
  gap: 12rpx;
}

.home-head__action {
  min-width: 68rpx;
  min-height: 48rpx;
  padding: 10rpx 16rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.18);
  color: #fff;
  font-size: 22rpx;
  font-weight: 650;
  text-align: center;
  transition: background 0.22s ease, transform 0.22s ease;
}

.home-head__action:active {
  transform: scale(0.96);
  background: rgba(255, 255, 255, 0.28);
}

.home-head__action--notice {
  position: relative;
}

.home-head__badge {
  position: absolute;
  top: -10rpx;
  right: -8rpx;
  min-width: 30rpx;
  padding: 2rpx 8rpx;
  border-radius: 999rpx;
  background: #ffd166;
  color: #7a2900;
  font-size: 18rpx;
  font-weight: 700;
  line-height: 1.2;
}

.home-title {
  font-size: 46rpx;
  font-weight: 800;
  letter-spacing: -0.02em;
  line-height: 1.18;
  color: #fff;
}

.home-platform {
  margin-bottom: 10rpx;
  font-size: 22rpx;
  font-weight: 650;
  color: rgba(255, 255, 255, 0.84);
}

.home-date {
  margin-top: 18rpx;
  font-size: 28rpx;
  font-weight: 650;
  color: rgba(255, 255, 255, 0.9);
}

.home-worker-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 24rpx;
  padding: 22rpx 24rpx;
  border-radius: 24rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.12);
}

.home-worker-card__name {
  font-size: 30rpx;
  font-weight: 700;
  color: #fff;
}

.home-worker-card__meta {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.82);
}

.home-worker-card__qr {
  width: 170rpx;
  padding: 10rpx;
  border-radius: 18rpx;
  background: rgba(255, 255, 255, 0.18);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10rpx;
}

.home-worker-card__qr-image {
  width: 130rpx;
  height: 130rpx;
  border-radius: 12rpx;
  background: #fff;
}

.home-worker-card__qr-text {
  color: #fff;
  font-size: 20rpx;
  font-weight: 600;
  text-align: center;
  word-break: break-all;
}

.home-action-row {
  margin-top: 0;
}

.home-activity-title {
  font-size: 30rpx;
  font-weight: 700;
  color: #122d42;
}

.home-activity-title + .worker-subtitle {
  margin-bottom: 22rpx;
}
</style>
