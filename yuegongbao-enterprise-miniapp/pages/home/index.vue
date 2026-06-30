<template>
  <view class="worker-page worker-page--tab enterprise-page">
    <view class="worker-card worker-hero">
      <view class="section-head">
        <view>
          <view class="worker-title worker-title--display">企业首页</view>
          <view class="worker-subtitle">人员、设备、工资、审批、培训与招聘的移动办公入口。</view>
        </view>
        <view class="worker-tag worker-tag--notice">待办 {{ hero.todoCount }}</view>
      </view>
      <view class="hero-stat-grid">
        <view class="hero-stat"><view class="hero-stat__value">{{ hero.healthScore }}</view><view class="hero-stat__label">健康度</view></view>
        <view class="hero-stat"><view class="hero-stat__value">{{ hero.warningCount }}</view><view class="hero-stat__label">预警</view></view>
        <view class="hero-stat"><view class="hero-stat__value">{{ hero.taskCount }}</view><view class="hero-stat__label">待办任务</view></view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head"><view class="worker-title">快捷入口</view></view>
      <view class="entry-grid entry-grid--two">
        <view v-for="item in quickEntries" :key="item.key" class="entry-item entry-item--wide" @click="openAction(item)">
          <view class="entry-item__icon" :class="'entry-item__icon--' + item.tone"><text class="entry-item__glyph">{{ item.glyph }}</text></view>
          <view class="entry-item__label">{{ item.label }}</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head"><view class="worker-title">核心指标</view></view>
      <view class="summary-grid summary-grid--three">
        <view v-for="item in dataCards" :key="item.label" class="summary-item">
          <view class="summary-item__label">{{ item.label }}</view>
          <view class="summary-item__value">{{ item.value }}</view>
          <view class="summary-item__value--small">{{ item.desc }}</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head"><view class="worker-title">风险提醒</view></view>
      <view class="record-row" v-for="item in warnings" :key="item.id">
        <view>
          <view class="record-row__title">{{ item.title }}</view>
          <view class="record-row__subtitle">{{ item.desc }}</view>
        </view>
        <view class="worker-tag" :class="item.levelClass">{{ item.level }}</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getEnterpriseHomeDashboard } from '../../api/enterprise'
import { openPage } from '../../utils/navigation'
import { normalizeWorkerJumpTarget, openWorkerJumpTarget } from '../../utils/worker-jump'

// 企业首页联调快照：/app/enterprise/home/dashboard
const hero = ref({ healthScore: '--', warningCount: '--', taskCount: '--', todoCount: '--' })
const quickEntries = ref([])
const dataCards = ref([])
const warnings = ref([])
const tip = ref('')
const tipText = computed(() => tip.value)

async function loadData() {
  const data = await getEnterpriseHomeDashboard()
  hero.value = data.hero || hero.value
  quickEntries.value = Array.isArray(data.quickEntries) ? data.quickEntries : []
  dataCards.value = Array.isArray(data.dataCards) ? data.dataCards : []
  warnings.value = Array.isArray(data.warnings) ? data.warnings : []
  tip.value = ''
}

function openAction(item) {
  const target = normalizeWorkerJumpTarget(item) || normalizeWorkerJumpTarget(item?.path)
  if (target && openWorkerJumpTarget(target)) {
    return
  }
  openPage('/pages/workbench/index')
}

onMounted(loadData)
</script>
