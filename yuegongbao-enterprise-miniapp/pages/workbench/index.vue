<template>
  <view class="worker-page worker-page--tab enterprise-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">企业工作台</view>
      <view class="worker-subtitle">按业务域分组的人事、设备、工资、审批、培训和招聘入口。</view>
    </view>
    <view class="worker-card" v-for="section in sections" :key="section.title">
      <view class="section-head">
        <view class="worker-title">{{ section.title }}</view>
        <view class="worker-tag">{{ section.tag }}</view>
      </view>
      <view class="entry-grid entry-grid--two">
        <view v-for="item in section.items" :key="item.label" class="entry-item entry-item--wide" @click="openAction(item)">
          <view class="entry-item__icon" :class="'entry-item__icon--' + item.tone"><text class="entry-item__glyph">{{ item.glyph }}</text></view>
          <view class="entry-item__label">{{ item.label }}</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getEnterpriseWorkbenchDashboard } from '../../api/enterprise'
import { openPage } from '../../utils/navigation'
import { normalizeWorkerJumpTarget, openWorkerJumpTarget } from '../../utils/worker-jump'

// 企业工作台联调快照：/app/enterprise/workbench/dashboard
const sections = ref([])
const tip = ref('')
const tipText = computed(() => tip.value)

async function loadData() {
  const data = await getEnterpriseWorkbenchDashboard()
  sections.value = Array.isArray(data.sections) ? data.sections : []
  tip.value = ''
}

function openAction(item) {
  const target = normalizeWorkerJumpTarget(item) || normalizeWorkerJumpTarget(item?.path)
  if (target && openWorkerJumpTarget(target)) {
    return
  }
  openPage('/pages/home/index')
}

onMounted(loadData)
</script>
