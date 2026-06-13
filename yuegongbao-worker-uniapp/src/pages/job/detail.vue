<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="worker-title">{{ detail.title || '-' }}</view>
      <view class="worker-subtitle">{{ detail.enterpriseName || '-' }}</view>
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
      <view class="detail-block">
        <view class="detail-block__title">岗位说明</view>
        <view class="worker-subtitle">{{ detail.description || '-' }}</view>
      </view>
      <view class="detail-block">
        <view class="detail-block__title">任职要求</view>
        <view class="worker-subtitle">{{ detail.requirementText || '-' }}</view>
      </view>
      <view class="detail-block">
        <view class="detail-block__title">联系方式</view>
        <view class="worker-subtitle">{{ detail.contactName || '-' }} {{ detail.contactMobile || '' }}</view>
      </view>
      <button class="worker-button" :disabled="detail.applied" @click="submitApply">
        {{ detail.applied ? '已投递' : '立即投递' }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { applyJob, getJobDetail, getResumeDetail } from '../../api/worker'

const detail = ref({})
const jobId = ref()
const jobLastLoadedAt = ref('')
const applyLastAttemptAt = ref('')
const applyLastResult = ref('not_started')
const applyLastMessage = ref('')
const resumeExpectedJob = ref('')
const resumeIntro = ref('')
const resumeLoadError = ref('')

const applyStatusSummaryText = computed(() => {
  if (detail.value.applied) {
    return '已投递'
  }
  if (applyLastResult.value === 'success') {
    return '本次投递成功'
  }
  if (applyLastResult.value === 'resume_required') {
    return '简历未完善'
  }
  if (applyLastResult.value === 'failed') {
    return '投递失败'
  }
  return '待投递'
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
    return `可投递 / ${resumeExpectedJob.value || '已完善'}`
  }
  return `待完善 / 缺少${missingFields.join('、')}`
})
const detailConsistencyText = computed(() => {
  const missingFields = []
  if (!detail.value.title) {
    missingFields.push('岗位名称')
  }
  if (!detail.value.enterpriseName) {
    missingFields.push('企业名称')
  }
  if (!detail.value.workAddress) {
    missingFields.push('工作地点')
  }
  if (missingFields.length) {
    return `详情缺少 ${missingFields.join('、')}`
  }
  if (detail.value.applied && applyLastResult.value === 'success') {
    return '详情已回写已投递状态'
  }
  return detail.value.applied ? '详情显示已投递' : '详情可发起投递'
})
const jobApplySnapshotText = computed(() => {
  return [
    '## 投递验收摘要',
    `- 最近加载：${jobLastLoadedAt.value || '-'}`,
    `- 岗位名称：${detail.value.title || '-'}`,
    `- 企业名称：${detail.value.enterpriseName || '-'}`,
    `- 当前状态：${applyStatusSummaryText.value}`,
    `- 简历门槛：${resumeReadinessText.value}`,
    `- 最近投递尝试：${applyLastAttemptAt.value || '-'}`,
    `- 详情核对：${detailConsistencyText.value}`,
    `- 说明：${applyLastMessage.value || '-'}`
  ].join('\n')
})

async function loadData() {
  try {
    resumeLoadError.value = ''
    const [detailData, resumeData] = await Promise.all([
      getJobDetail(jobId.value),
      getResumeDetail().catch((error) => {
        resumeLoadError.value = error.message || '简历快照加载失败'
        return null
      })
    ])
    detail.value = detailData || {}
    resumeExpectedJob.value = resumeData?.expectedJob || ''
    resumeIntro.value = resumeData?.intro || ''
    jobLastLoadedAt.value = new Date().toLocaleString()
    if (resumeLoadError.value) {
      applyLastMessage.value = `详情已加载；简历快照异常：${resumeLoadError.value}`
    } else if (detail.value.applied) {
      applyLastMessage.value = '岗位已存在投递记录'
    } else if (resumeReadinessText.value.startsWith('待完善')) {
      applyLastMessage.value = '岗位详情已加载，但简历仍未满足当前投递校验条件'
    } else {
      applyLastMessage.value = '岗位详情已加载，可直接发起投递'
    }
  } catch (error) {
    jobLastLoadedAt.value = new Date().toLocaleString()
    resumeExpectedJob.value = ''
    resumeIntro.value = ''
    resumeLoadError.value = ''
    applyLastMessage.value = error.message || '加载岗位详情失败'
    uni.showToast({ title: error.message || '加载岗位详情失败', icon: 'none' })
  }
}

async function submitApply() {
  if (detail.value.applied) {
    return
  }
  try {
    applyLastAttemptAt.value = new Date().toLocaleString()
    await applyJob(jobId.value)
    applyLastResult.value = 'success'
    applyLastMessage.value = '服务端已受理本次岗位投递'
    uni.showToast({ title: '投递成功', icon: 'none' })
    await loadData()
  } catch (error) {
    const message = error.message || '投递失败'
    if (message.includes('请先完善简历')) {
      applyLastResult.value = 'resume_required'
      applyLastMessage.value = message
      uni.showModal({
        title: '请先完善简历',
        content: '投递岗位前需要先完善简历信息，是否现在前往填写？',
        confirmText: '去完善',
        success: (res) => {
          if (res.confirm) {
            uni.navigateTo({ url: '/pages/profile/resume' })
          }
        }
      })
      return
    }
    applyLastResult.value = 'failed'
    applyLastMessage.value = message
    uni.showToast({ title: message, icon: 'none' })
  }
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

onLoad((options) => {
  jobId.value = options?.jobId
  loadData()
})
</script>

<style lang="scss">
.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18rpx;
  margin: 24rpx 0;
}

.detail-item {
  padding: 20rpx;
  border-radius: 18rpx;
  background: #f5f8fc;
}

.detail-item__label {
  font-size: 22rpx;
  color: #7890aa;
}

.detail-item__value {
  margin-top: 10rpx;
  font-size: 28rpx;
  font-weight: 700;
  color: #16324f;
}

.detail-block {
  margin-bottom: 22rpx;
}

.detail-block__title {
  margin-bottom: 10rpx;
  font-size: 28rpx;
  font-weight: 700;
  color: #16324f;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 18rpx;
}

.section-head--sub {
  margin-top: 20rpx;
}

.detail-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
  padding: 18rpx 0;
  border-bottom: 1rpx solid #edf2f7;
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-row__label {
  font-size: 26rpx;
  color: #5f7893;
}

.detail-row__value {
  flex: 1;
  text-align: right;
  font-size: 26rpx;
  color: #16324f;
  line-height: 1.6;
  word-break: break-all;
}

.result-block {
  margin-top: 16rpx;
  padding: 22rpx 24rpx;
  border-radius: 20rpx;
  background: #f5f8fc;
}

.result-block__label {
  font-size: 22rpx;
  color: #7890aa;
}

.result-block__value {
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: #16324f;
  white-space: pre-wrap;
  word-break: break-all;
}

.clear-action {
  font-size: 24rpx;
  color: #1f6fd6;
}
</style>
