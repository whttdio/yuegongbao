<template>
  <view class="worker-page worker-page--tab">
    <view class="workbench-hero worker-card worker-hero">
      <view class="section-head">
        <view>
          <view class="worker-title">工作台</view>
          <view class="worker-subtitle">{{ workbench.heroSummary || defaultHeroSummary }}</view>
        </view>
        <view v-if="hasUnreadNotice" class="worker-tag worker-tag--notice">未读 {{ unreadNoticeText }}</view>
      </view>

      <view class="hero-stat-grid">
        <view class="hero-stat">
          <view class="hero-stat__value">{{ trainingProgressText }}</view>
          <view class="hero-stat__label">本月培训</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ hasUnreadNotice ? unreadNoticeText : '0' }}</view>
          <view class="hero-stat__label">通知待看</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ (workbench.quickActions || []).length }}</view>
          <view class="hero-stat__label">快捷协同</view>
        </view>
      </view>

      <view class="worker-subtitle workbench-lock-tip">{{ workbench.clockRuleTip || defaultClockRuleTip }}</view>
    </view>

    <view v-if="(workbench.quickActions || []).length" class="worker-card">
      <view class="section-head">
        <view class="worker-title">快捷协同</view>
      </view>
      <view class="quick-grid">
        <view
          v-for="item in workbench.quickActions || []"
          :key="item.key"
          class="quick-item"
          @click="openQuickAction(item)"
        >
          <view class="entry-item__icon" :class="'entry-item__icon--' + getEntryIcon(item).tone">
            <text class="entry-item__glyph">{{ getEntryIcon(item).glyph }}</text>
          </view>
          <view class="quick-item__main">
            <view class="quick-item__title">{{ item.label }}</view>
            <view class="quick-item__summary">{{ item.summary }}</view>
          </view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">常用服务</view>
      </view>
      <view class="entry-grid">
        <view
          v-for="item in workbench.commonEntries || []"
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

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">维权协同</view>
      </view>
      <view class="entry-grid entry-grid--two">
        <view
          v-for="item in workbench.rightsEntries || []"
          :key="item.key"
          class="entry-item entry-item--wide"
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
        <view class="worker-title">内容服务</view>
      </view>
      <view class="entry-grid entry-grid--two">
        <view
          v-for="item in workbench.contentEntries || []"
          :key="item.key"
          class="entry-item entry-item--wide"
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
        <view class="worker-title">个人服务</view>
      </view>
      <view class="entry-grid">
        <view
          v-for="item in workbench.personalEntries || []"
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

    <view v-if="(workbench.serviceCards || []).length" class="worker-card">
      <view class="section-head">
        <view class="worker-title">服务承接</view>
      </view>
      <view v-for="item in workbench.serviceCards || []" :key="item.key" class="service-panel">
        <view class="service-panel__title">{{ item.title }}</view>
        <view class="service-panel__subtitle">{{ item.subtitle }}</view>
        <view class="service-panel__desc">{{ item.summary }}</view>
        <view class="service-actions">
          <button class="worker-button" @click="openServiceCard(item)">{{ item.actionLabel }}</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getWorkerWorkbench } from '../../api/worker'
import { openPage } from '../../utils/navigation'
import { normalizeWorkerJumpTarget, openWorkerJumpTarget } from '../../utils/worker-jump'
import { resolveEntryIcon } from '../../utils/entry-icon'

const defaultClockRuleTip = '完成每月 10 道安全培训题后，才能解锁打卡和工资查询。'
const defaultHeroSummary = '集中进入培训、维权、内容和个人服务能力。'
const EXPECTED_WORKBENCH_GROUPS = ['快捷协同', '常用服务', '维权协同', '内容服务', '个人服务']

const workbench = reactive({})
const workbenchLastLoadedAt = ref('')
const workbenchLastActionAt = ref('')
const workbenchLastMessage = ref('')

function getEntryIcon(item) {
  return resolveEntryIcon(item)
}

const trainingProgressText = computed(() => {
  const progress = workbench.trainingProgress || {}
  return `${progress.completed || 0}/${progress.total || 10}`
})

const unreadNoticeCount = computed(() => Number(workbench.unreadNoticeCount || 0))
const hasUnreadNotice = computed(() => unreadNoticeCount.value > 0)

const unreadNoticeText = computed(() => {
  if (unreadNoticeCount.value > 99) {
    return '99+'
  }
  return String(unreadNoticeCount.value || 0)
})
const workbenchSpecCoverageText = computed(() => {
  const coverage = [
    (workbench.quickActions || []).length ? '快捷协同' : '',
    (workbench.commonEntries || []).length ? '常用服务' : '',
    (workbench.rightsEntries || []).length ? '维权协同' : '',
    (workbench.contentEntries || []).length ? '内容服务' : '',
    (workbench.personalEntries || []).length ? '个人服务' : ''
  ].filter(Boolean)
  const missing = EXPECTED_WORKBENCH_GROUPS.filter((item) => !coverage.includes(item))
  return missing.length ? `已覆盖 ${coverage.length}/${EXPECTED_WORKBENCH_GROUPS.length} / 缺 ${missing.join('、')}` : '工作台分区已齐'
})
const workbenchHeroSummaryText = computed(() => `培训 ${trainingProgressText.value} / 未读通知 ${hasUnreadNotice.value ? unreadNoticeText.value : '0'}`)
const workbenchEntrySummaryText = computed(() => {
  return [
    `快捷 ${workbench.quickActions?.length || 0}`,
    `常用 ${workbench.commonEntries?.length || 0}`,
    `维权 ${workbench.rightsEntries?.length || 0}`,
    `内容 ${workbench.contentEntries?.length || 0}`,
    `个人 ${workbench.personalEntries?.length || 0}`
  ].join(' / ')
})
const workbenchLockedSummaryText = computed(() => {
  const lockedCount = (workbench.commonEntries || []).filter((item) => item.locked).length
  return `${lockedCount} 个`
})
const workbenchServiceSummaryText = computed(() => `${workbench.serviceCards?.length || 0} 张服务卡`)
const workbenchQuickActionSpecText = computed(() => {
  const actions = (workbench.quickActions || []).map((item) => item.label || item.key).filter(Boolean)
  return actions.length ? actions.join('、') : '当前无快捷协同'
})
const workbenchLockConsistencyText = computed(() => {
  const lockedCount = (workbench.commonEntries || []).filter((item) => item.locked).length
  return lockedCount
    ? '培训未完成时，工作台锁定入口文案应与首页打卡拦截、工资页和打卡页保持一致'
    : '培训完成后，工作台常用服务应与首页、工资页、打卡页同步恢复可进入'
})
const workbenchSnapshotText = computed(() => {
  return [
    '## 工作台验收摘要',
    `- 最近加载：${workbenchLastLoadedAt.value || '-'}`,
    `- 最近联动：${workbenchLastActionAt.value || '-'}`,
    `- 结构核对：${workbenchSpecCoverageText.value}`,
    `- 培训与通知：${workbenchHeroSummaryText.value}`,
    `- 入口分布：${workbenchEntrySummaryText.value}`,
    `- 锁定入口：${workbenchLockedSummaryText.value}`,
    `- 服务承接：${workbenchServiceSummaryText.value}`,
    `- 快捷协同：${workbenchQuickActionSpecText.value}`,
    `- 锁定一致性：${workbenchLockConsistencyText.value}`,
    `- 说明：${workbenchLastMessage.value || '-'}`,
    '- 链路关联：工作台 / 常用服务 / 维权协同 / 内容服务 / 个人服务'
  ].join('\n')
})

async function loadData() {
  try {
    const data = await getWorkerWorkbench()
    Object.assign(workbench, data || {})
    workbenchLastLoadedAt.value = new Date().toLocaleString()
    const missingGroups = EXPECTED_WORKBENCH_GROUPS.filter((item) => {
      if (item === '快捷协同') return !(workbench.quickActions || []).length
      if (item === '常用服务') return !(workbench.commonEntries || []).length
      if (item === '维权协同') return !(workbench.rightsEntries || []).length
      if (item === '内容服务') return !(workbench.contentEntries || []).length
      if (item === '个人服务') return !(workbench.personalEntries || []).length
      return false
    })
    workbenchLastMessage.value = missingGroups.length
      ? `工作台聚合已加载，待补分区：${missingGroups.join('、')}`
      : '工作台聚合已加载，可核对培训锁定、通知数量、服务入口与首页/工资/打卡的一致性'
  } catch (error) {
    workbenchLastLoadedAt.value = new Date().toLocaleString()
    workbenchLastMessage.value = error.message || '加载工作台失败'
    uni.showToast({ title: error.message || '加载工作台失败', icon: 'none' })
  }
}

function recordWorkbenchAction(action, detail) {
  workbenchLastActionAt.value = new Date().toLocaleString()
  workbenchLastMessage.value = detail ? `${action} / ${detail}` : action
}

function resolveActionTarget(item) {
  if (!item || typeof item !== 'object') {
    return null
  }
  return normalizeWorkerJumpTarget(item)
}

function openActionTarget(item) {
  const target = resolveActionTarget(item)
  if (!target?.path) {
    return false
  }
  return openWorkerJumpTarget(target)
}

function openEntry(item) {
  if (!item) {
    return
  }
  if (item.locked) {
    recordWorkbenchAction(`工作台锁定：${item.label || '-'}`, item.lockReason || '请先完成培训')
    uni.showToast({ title: item.lockReason || '请先完成培训', icon: 'none' })
    return
  }
  if (openActionTarget(item)) {
    recordWorkbenchAction(`打开工作台入口：${item.label || '-'}`, item.path || item.jumpPath || item.pagePath || '入口跳转成功')
    return
  }
  recordWorkbenchAction(`工作台跳转失败：${item.label || '-'}`, '目标地址无效')
  uni.showToast({ title: workbenchLastMessage.value, icon: 'none' })
}

function openQuickAction(item) {
  if (!item) {
    return
  }
  if (item.key === 'help-feedback') {
    recordWorkbenchAction('打开快捷协同：帮助中心', '/pages/profile/help')
    openPage('/pages/profile/help')
    return
  }
  if (!item) {
    return
  }
  if (openActionTarget(item)) {
    recordWorkbenchAction(`打开快捷协同：${item.label || '-'}`, item.path || item.jumpPath || item.pagePath || '快捷协同跳转成功')
    return
  }
  recordWorkbenchAction(`快捷协同跳转失败：${item.label || '-'}`, '目标地址无效')
  uni.showToast({ title: workbenchLastMessage.value, icon: 'none' })
}

function openServiceCard(item) {
  if (!item) {
    return
  }
  if (item.phoneNumber || item.displayText) {
    const phoneNumber = resolveHotlineNumber(item)
    recordWorkbenchAction(`呼叫服务热线：${item.displayText || phoneNumber}`, phoneNumber)
    uni.makePhoneCall({ phoneNumber })
    return
  }
  if (!item) {
    return
  }
  if (openActionTarget(item)) {
    recordWorkbenchAction(`打开服务承接：${item.title || '-'}`, item.path || item.jumpPath || item.pagePath || '服务承接跳转成功')
    return
  }
  recordWorkbenchAction(`服务承接跳转失败：${item.title || '-'}`, '目标地址无效')
  uni.showToast({ title: workbenchLastMessage.value, icon: 'none' })
}

function resolveHotlineNumber(value = {}) {
  const phoneNumber = String(value?.phoneNumber || '').trim()
  if (phoneNumber) {
    return phoneNumber
  }
  const displayText = String(value?.displayText || '')
  const matched = displayText.match(/1\d{4,}/)
  return matched?.[0] || '12351'
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

onShow(loadData)
</script>

<style lang="scss">
.workbench-lock-tip {
  margin-top: 20rpx;
  color: rgba(255, 255, 255, 0.88);
}
</style>
