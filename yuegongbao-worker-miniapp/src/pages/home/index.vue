<template>
  <view class="worker-page worker-page--tab worker-page--screen">
    <view class="worker-card worker-hero">
      <view class="home-head">
        <view>
          <view class="home-platform">{{ home.platformLabel || '广东省用工保障服务平台' }}</view>
          <view class="home-title">{{ home.brandTitle || '阳光劳务' }}</view>
          <view class="worker-subtitle">{{ home.enterpriseName || '当前岗位已绑定企业' }}</view>
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
          <view class="home-worker-card__meta">{{ home.workerCard?.jobType || '-' }} / {{ home.workerCard?.workerType || '-' }}</view>
        </view>
        <view class="home-worker-card__qr">
          <view class="home-worker-card__qr-text">{{ home.workerCard?.qrCodeText || '身份码待生成' }}</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">培训与打卡</view>
        <view class="worker-tag">{{ progressText }}</view>
      </view>
      <view class="worker-subtitle">{{ home.clockRuleTip || '完成培训后可解锁考勤、工资等核心服务。' }}</view>
      <view class="home-action-row worker-button-row">
        <button class="worker-button" @click="goCheckIn">上班打卡</button>
        <button class="worker-button worker-button--secondary" @click="goCheckOut">下班打卡</button>
      </view>
      <view class="home-training-link" @click="goTraining">去完成培训</view>
    </view>

    <view class="worker-card">
      <view class="section-head"><view class="worker-title">核心服务</view></view>
      <view class="entry-grid">
        <view v-for="item in home.quickEntries || []" :key="item.key" class="entry-item" :class="{ 'entry-item--locked': item.locked }" @click="openEntry(item)">
          <view class="entry-item__icon" :class="'entry-item__icon--' + getEntryIcon(item).tone"><text class="entry-item__glyph">{{ getEntryIcon(item).glyph }}</text></view>
          <view class="entry-item__label">{{ item.label }}</view>
          <view v-if="item.locked" class="entry-item__tip">需先完成培训</view>
        </view>
      </view>
    </view>

    <view class="worker-card" v-if="(home.moreEntries || []).length">
      <view class="section-head"><view class="worker-title">更多服务</view></view>
      <view class="entry-grid">
        <view v-for="item in home.moreEntries || []" :key="item.key" class="entry-item" @click="openEntry(item)">
          <view class="entry-item__icon" :class="'entry-item__icon--' + getEntryIcon(item).tone"><text class="entry-item__glyph">{{ getEntryIcon(item).glyph }}</text></view>
          <view class="entry-item__label">{{ item.label }}</view>
        </view>
      </view>
    </view>

    <view class="worker-card" v-if="home.activityCard">
      <view class="section-head"><view class="worker-title">福利活动</view></view>
      <view class="home-activity-title">{{ home.activityCard?.title || '-' }}</view>
      <view class="worker-subtitle">{{ home.activityCard?.subtitle || '-' }}</view>
      <button class="worker-button" @click="openActivity">{{ home.activityCard?.buttonText || '立即参与' }}</button>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">重要通知</view>
        <view class="notice-link" @click="goNoticeList">{{ hasUnreadNotice ? `全部 / 未读 ${unreadNoticeText}` : '全部' }}</view>
      </view>
      <view v-if="(home.noticeList || []).length">
        <view v-for="item in home.noticeList" :key="item.noticeId" class="notice-row" @click="openNotice(item)">
          <view class="notice-row__title">{{ item.title }}</view>
          <view class="notice-row__meta">{{ item.isRead ? '已读' : '未读' }}</view>
        </view>
      </view>
      <view v-else class="worker-empty">暂无通知</view>
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

const home = reactive({})
const homeLastMessage = ref('')
const unreadNoticeCount = computed(() => Number(home.unreadNoticeCount || 0))
const hasUnreadNotice = computed(() => unreadNoticeCount.value > 0)
const unreadNoticeText = computed(() => (unreadNoticeCount.value > 99 ? '99+' : String(unreadNoticeCount.value || 0)))
const progressText = computed(() => {
  const progress = home.trainingProgress || {}
  return `${progress.completed || 0}/${progress.total || 10}`
})

function getEntryIcon(item) {
  return resolveEntryIcon(item)
}

async function loadHome() {
  try {
    Object.assign(home, await getWorkerHome())
    homeLastMessage.value = '首页已加载'
  } catch (error) {
    homeLastMessage.value = error.message || '首页加载失败'
  }
}

function goCheckIn() {
  openPage('/pages/attendance/checkin')
}

function goCheckOut() {
  openPage('/pages/attendance/checkin?action=checkOut')
}

function goTraining() {
  openPage('/pages/training/index')
}

function goNoticeList() {
  openPage('/pages/notice/list')
}

function goSettings() {
  openPage('/pages/profile/settings')
}

function openEntry(item) {
  if (item.locked) {
    uni.showToast({ title: item.lockReason || '请先完成培训', icon: 'none' })
    return
  }
  const target = normalizeWorkerJumpTarget(item)
  if (target?.path) {
    openWorkerJumpTarget(target)
    return
  }
  openPage(item.path)
}

function openActivity() {
  openPage('/pages/activity/detail')
}

function openNotice(item) {
  openPage(`/pages/notice/detail?noticeId=${item.noticeId}`)
}

onShow(loadHome)
</script>
