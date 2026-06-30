<template>
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">投递记录</view>
      <view class="worker-subtitle">查看岗位投递进度和企业反馈状态</view>
      <view class="hero-stat-grid">
        <view class="hero-stat">
          <view class="hero-stat__value">{{ rows.length }}</view>
          <view class="hero-stat__label">投递总数</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ pendingCount }}</view>
          <view class="hero-stat__label">待反馈</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ repliedCount }}</view>
          <view class="hero-stat__label">有反馈</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">投递列表</view>
      </view>
      <view v-if="rows.length">
        <view v-for="item in rows" :key="item.applyId" class="list-row" @click="openDetail(item)">
          <view>
            <view class="list-row__title">{{ item.jobTitle || '-' }}</view>
            <view class="list-row__subtitle">{{ item.enterpriseName || '-' }}</view>
            <view class="list-row__subtitle">投递时间：{{ item.applyTime || '-' }}</view>
          </view>
          <view class="worker-tag">{{ item.statusText || '已投递' }}</view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无投递记录</view>
        <view class="worker-empty__desc">
          可先去岗位列表继续找工作，或完善个人简历后再发起投递，提高企业反馈效率。
        </view>
        <view class="worker-empty__actions">
          <button class="worker-button worker-button--secondary" @click="goJobList">查看岗位</button>
          <button class="worker-button" @click="goResume">完善简历</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getJobApplyList, getResumeDetail } from '../../api/screen'

const rows = ref([])
const pendingCount = computed(() => rows.value.filter((item) => !item.statusText || item.statusText === '已投递').length)
const repliedCount = computed(() => Math.max(rows.value.length - pendingCount.value, 0))
const applyLastLoadedAt = ref('')
const applyLastMessage = ref('')
const resumeExpectedJob = ref('')
const resumeIntro = ref('')
const resumeLoadError = ref('')

const resumeReadinessText = computed(() => {
  const missingFields = []
  if (!String(resumeExpectedJob.value || '').trim()) {
    missingFields.push('期望岗位')
  }
  if (!String(resumeIntro.value || '').trim()) {
    missingFields.push('个人介绍')
  }
  if (!missingFields.length) {
    return `可投递 / ${resumeExpectedJob.value || '已完善'}`
  }
  return `待完善 / 缺少${missingFields.join('、')}`
})
const applyStatusSummaryText = computed(() => {
  if (!rows.value.length) {
    return '暂无投递记录'
  }
  const statusMap = rows.value.reduce((result, item) => {
    const key = item?.statusText || '已投递'
    result[key] = (result[key] || 0) + 1
    return result
  }, {})
  return Object.keys(statusMap).map((key) => `${key} ${statusMap[key]} 条`).join(' / ')
})
const latestApplySummaryText = computed(() => {
  if (!rows.value.length) {
    return '暂无记录'
  }
  const latest = rows.value[0] || {}
  return `${latest.jobTitle || '-'} / ${latest.statusText || '已投递'} / ${latest.applyTime || '-'}`
})
const applyConsistencyText = computed(() => {
  if (!rows.value.length) {
    return resumeReadinessText.value.startsWith('待完善') ? '当前无投递，且简历待完善' : '当前无投递记录'
  }
  const latest = rows.value[0] || {}
  if (resumeReadinessText.value.startsWith('待完善')) {
    return `已有投递记录 ${rows.value.length} 条 / 当前简历状态 ${resumeReadinessText.value}`
  }
  return `最近一条 ${latest.jobTitle || '-'} / ${latest.statusText || '已投递'}`
})
const applySnapshotText = computed(() => {
  return [
    '## 投递记录验收摘要',
    `- 最近加载：${applyLastLoadedAt.value || '-'}`,
    `- 简历状态：${resumeReadinessText.value}`,
    `- 投递总数：${rows.value.length} 条`,
    `- 状态分布：${applyStatusSummaryText.value}`,
    `- 最近一条：${latestApplySummaryText.value}`,
    `- 链路一致性：${applyConsistencyText.value}`,
    `- 说明：${applyLastMessage.value || '-'}`,
    '- 链路关联：岗位列表 / 岗位详情 / 我的简历 / 投递记录'
  ].join('\n')
})

async function loadData() {
  try {
    resumeLoadError.value = ''
    const [data, resumeData] = await Promise.all([
      getJobApplyList(),
      getResumeDetail().catch((error) => {
        resumeLoadError.value = error.message || '简历快照加载失败'
        return null
      })
    ])
    rows.value = data?.rows || []
    resumeExpectedJob.value = resumeData?.expectedJob || ''
    resumeIntro.value = resumeData?.intro || ''
    applyLastLoadedAt.value = new Date().toLocaleString()
    const messageParts = []
    if (!rows.value.length) {
      messageParts.push('当前暂无投递记录')
    }
    if (resumeLoadError.value) {
      messageParts.push(`简历快照异常：${resumeLoadError.value}`)
    } else if (resumeReadinessText.value.startsWith('待完善')) {
      messageParts.push('简历仍未满足当前投递校验条件')
    }
    if (rows.value.length && resumeReadinessText.value.startsWith('待完善')) {
      messageParts.push(`已有 ${rows.value.length} 条投递记录，需与历史简历状态联调核对`)
    }
    if (!messageParts.length) {
      messageParts.push('投递记录和简历状态已同步加载，可回查详情页状态')
    }
    applyLastMessage.value = messageParts.join('；')
  } catch (error) {
    applyLastLoadedAt.value = new Date().toLocaleString()
    rows.value = []
    resumeExpectedJob.value = ''
    resumeIntro.value = ''
    resumeLoadError.value = ''
    applyLastMessage.value = error.message || '加载投递记录失败'
    uni.showToast({ title: error.message || '加载投递记录失败', icon: 'none' })
  }
}

function openDetail(item) {
  if (!item?.jobId) {
    return
  }
  uni.navigateTo({ url: `/pages/job/detail?jobId=${item.jobId}` })
}

function goJobList() {
  uni.navigateTo({ url: '/pages/job/list' })
}

function goResume() {
  uni.navigateTo({ url: '/pages/profile/resume' })
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

onShow(() => {
  resumeLoadError.value = ''
  loadData()
})
</script>

<style lang="scss">
.worker-empty__actions button {
  flex: 1;
}
</style>
