<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page enterprise-page">
    <view class="worker-card worker-hero">
      <view class="section-head">
        <view>
          <view class="worker-title">{{ detail.title || '-' }}</view>
          <view class="worker-subtitle">{{ detail.enterpriseName || '-' }}</view>
        </view>
        <view class="worker-tag" :class="detail.applied ? 'worker-tag--success' : 'worker-tag--info'">
          {{ detail.applied ? '员工已投递' : '员工侧可投递' }}
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="enterprise-notice enterprise-notice--readonly">
        <view class="enterprise-notice__head">
          <view class="enterprise-notice__title">企业侧只读参考</view>
          <view class="worker-tag worker-tag--info">求职协同视图</view>
        </view>
        <view class="enterprise-notice__desc">岗位投递依赖员工本人简历、实名状态和求职意向，企业端不再代员工发起投递。</view>
        <view class="enterprise-notice__reason">当前页面保留岗位详情和员工简历准备度提示，便于企业侧解释岗位要求和协同员工完善资料。</view>
        <view class="enterprise-notice__actions">
          <button class="worker-button worker-button--secondary" @click="openResumeReadonly">查看简历协同页</button>
          <button class="worker-button" @click="openPage('/pages/workbench/index')">返回工作台</button>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="detail-grid">
        <view class="detail-item">
          <view class="detail-item__label">工种</view>
          <view class="detail-item__value">{{ detail.jobType || '-' }}</view>
        </view>
        <view class="detail-item">
          <view class="detail-item__label">薪资</view>
          <view class="detail-item__value">{{ detail.salaryText || '面议' }}</view>
        </view>
        <view class="detail-item">
          <view class="detail-item__label">地点</view>
          <view class="detail-item__value">{{ detail.workAddress || '-' }}</view>
        </view>
        <view class="detail-item">
          <view class="detail-item__label">招聘人数</view>
          <view class="detail-item__value">{{ detail.recruitCount || 0 }}</view>
        </view>
      </view>
      <view class="enterprise-readonly-bar">
        <view class="enterprise-readonly-bar__text">员工简历准备度：{{ resumeReadinessText }}</view>
        <view class="worker-tag worker-tag--info">{{ applyStatusSummaryText }}</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">岗位说明</view>
      </view>
      <view class="detail-block">
        <view class="detail-block__content">{{ detail.description || '-' }}</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">任职要求</view>
      </view>
      <view class="detail-block">
        <view class="detail-block__content">{{ detail.requirementText || '-' }}</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">联系方式</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">联系人</view>
        <view class="detail-row__value">{{ detail.contactName || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">联系电话</view>
        <view class="detail-row__value">{{ detail.contactMobile || '-' }}</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getJobDetail, getResumeDetail } from '../../api/enterprise-service'
import { openPage } from '../../utils/navigation'

const detail = ref({})
const jobId = ref()
const resumeExpectedJob = ref('')
const resumeIntro = ref('')

const applyStatusSummaryText = computed(() => {
  if (detail.value.applied) {
    return '员工已投递'
  }
  return '仅员工本人可投递'
})
const resumeReadinessText = computed(() => {
  const missingFields = []
  if (!String(resumeExpectedJob.value || '').trim()) {
    missingFields.push('期望岗位')
  }
  if (!String(resumeIntro.value || '').trim()) {
    missingFields.push('个人介绍')
  }
  if (!missingFields.length) {
    return `已具备基础条件 / ${resumeExpectedJob.value || '已完善'}`
  }
  return `待完善 / 缺少${missingFields.join('、')}`
})

async function loadData() {
  const [detailData, resumeData] = await Promise.all([
    getJobDetail(jobId.value),
    getResumeDetail().catch(() => null)
  ])
  detail.value = detailData || {}
  resumeExpectedJob.value = resumeData?.expectedJob || ''
  resumeIntro.value = resumeData?.intro || ''
}

function openResumeReadonly() {
  openPage('/pages/profile/resume')
}

onLoad((options) => {
  jobId.value = options?.jobId
  loadData().catch((error) => {
    uni.showToast({ title: error.message || '加载岗位详情失败', icon: 'none' })
  })
})
</script>
