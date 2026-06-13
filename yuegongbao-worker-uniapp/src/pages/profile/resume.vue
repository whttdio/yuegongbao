<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="worker-title">我的简历</view>
      <input v-model="form.expectedJob" class="form-input" placeholder="期望岗位" />
      <input v-model="form.expectedCity" class="form-input" placeholder="期望城市" />
      <input v-model="form.expectedSalary" class="form-input" placeholder="期望薪资" />
      <input v-model="form.skillTags" class="form-input" placeholder="技能标签，多个用逗号分隔" />
      <view class="section-head">
        <view class="worker-title worker-title--small">证书管理</view>
        <view class="worker-tag">{{ certificateCountText }}</view>
      </view>
      <view class="worker-subtitle">
        按证书名称、编号、发证单位和到期日期维护，支持继续兼容历史证书文本。
      </view>
      <view
        v-for="(item, index) in form.certificateList"
        :key="`cert-${index}`"
        class="certificate-card"
      >
        <input v-model="item.certificateName" class="form-input form-input--nested" placeholder="证书名称" />
        <input v-model="item.certificateNo" class="form-input form-input--nested" placeholder="证书编号" />
        <input v-model="item.issuer" class="form-input form-input--nested" placeholder="发证单位" />
        <input v-model="item.expireDate" class="form-input form-input--nested" placeholder="到期日期，如 2027-12-31" />
        <view class="certificate-card__actions">
          <view class="certificate-card__remove" @click="removeCertificate(index)">删除证书</view>
        </view>
      </view>
      <view class="certificate-actions">
        <button class="worker-button worker-button--secondary" @click="addCertificate">新增证书</button>
      </view>
      <textarea v-model="form.intro" class="form-textarea" placeholder="个人介绍" />
      <button class="worker-button" @click="submitResume">保存简历</button>
    </view>

    <view class="worker-card">
      <view class="worker-title">{{ form.personName || '-' }}</view>
      <view class="worker-subtitle">{{ form.mobile || '-' }} / {{ form.jobType || '-' }}</view>
      <view class="worker-subtitle">期望岗位：{{ form.expectedJob || '-' }}</view>
      <view class="worker-subtitle">期望城市：{{ form.expectedCity || '-' }}</view>
      <view class="worker-subtitle">期望薪资：{{ form.expectedSalary || '-' }}</view>
      <view class="worker-subtitle">证书状态：{{ form.certificateStatusText || '-' }}</view>
    </view>
  </view>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getResumeDetail, saveResume } from '../../api/worker'

const form = reactive({
  personName: '',
  mobile: '',
  jobType: '',
  expectedJob: '',
  expectedCity: '',
  expectedSalary: '',
  skillTags: '',
  certificateText: '',
  certificateList: [],
  certificateCount: 0,
  certificateStatusText: '',
  intro: ''
})
const resumeLastLoadedAt = ref('')
const resumeLastSavedAt = ref('')
const resumeLastMessage = ref('')

const certificateCountText = computed(() => `${form.certificateList.length || 0} 本`)
const requiredResumeFieldStatus = computed(() => ([
  { label: '期望岗位', ok: !!String(form.expectedJob || '').trim() },
  { label: '个人介绍', ok: !!String(form.intro || '').trim() }
]))
const resumeComplete = computed(() => requiredResumeFieldStatus.value.every((item) => item.ok))
const resumeCompletenessText = computed(() => {
  const finishedCount = requiredResumeFieldStatus.value.filter((item) => item.ok).length
  return `${resumeComplete.value ? '可投递' : '待完善'} / ${finishedCount}/${requiredResumeFieldStatus.value.length}`
})
const resumeGateText = computed(() => {
  const missing = requiredResumeFieldStatus.value.filter((item) => !item.ok).map((item) => item.label)
  return missing.length ? `投递仍缺 ${missing.join('、')}` : '已满足当前前端投递门槛'
})
const resumeFieldSummaryText = computed(() => {
  return [
    form.expectedJob ? '期望岗位已填' : '期望岗位缺失',
    form.expectedCity ? '期望城市已填' : '期望城市缺失',
    form.expectedSalary ? '期望薪资已填' : '期望薪资缺失',
    form.intro ? '个人介绍已填' : '个人介绍缺失'
  ].join(' / ')
})
const resumeSnapshotText = computed(() => {
  return [
    '## 简历验收摘要',
    `- 最近加载：${resumeLastLoadedAt.value || '-'}`,
    `- 最近保存：${resumeLastSavedAt.value || '-'}`,
    `- 简历状态：${resumeCompletenessText.value}`,
    `- 投递前置：${resumeGateText.value}`,
    `- 期望岗位：${form.expectedJob || '-'}`,
    `- 期望城市：${form.expectedCity || '-'}`,
    `- 期望薪资：${form.expectedSalary || '-'}`,
    `- 证书数量：${certificateCountText.value}`,
    `- 字段核对：${resumeFieldSummaryText.value}`,
    `- 说明：${resumeLastMessage.value || '-'}`
  ].join('\n')
})

function createCertificate() {
  return {
    certificateName: '',
    certificateNo: '',
    issuer: '',
    expireDate: ''
  }
}

function normalizeCertificateList(data) {
  if (Array.isArray(data?.certificateList) && data.certificateList.length) {
    return data.certificateList.map((item) => ({
      certificateName: item?.certificateName || '',
      certificateNo: item?.certificateNo || '',
      issuer: item?.issuer || '',
      expireDate: item?.expireDate || ''
    }))
  }
  if (data?.certificateText) {
    try {
      const parsed = JSON.parse(data.certificateText)
      if (Array.isArray(parsed)) {
        return parsed.map((item) => ({
          certificateName: item?.certificateName || '',
          certificateNo: item?.certificateNo || '',
          issuer: item?.issuer || '',
          expireDate: item?.expireDate || ''
        }))
      }
    } catch (error) {
      return []
    }
  }
  return []
}

async function loadData() {
  try {
    const data = await getResumeDetail()
    Object.assign(form, data || {})
    form.certificateList = normalizeCertificateList(data)
    resumeLastLoadedAt.value = new Date().toLocaleString()
    resumeLastMessage.value = resumeComplete.value ? '简历已满足当前前端投递校验条件' : '简历仍缺少必填项，投递可能被拦截'
  } catch (error) {
    resumeLastLoadedAt.value = new Date().toLocaleString()
    resumeLastMessage.value = error.message || '加载简历失败'
    uni.showToast({ title: error.message || '加载简历失败', icon: 'none' })
  }
}

function addCertificate() {
  form.certificateList.push(createCertificate())
}

function removeCertificate(index) {
  form.certificateList.splice(index, 1)
}

async function submitResume() {
  if (!form.expectedJob || !form.intro) {
    resumeLastMessage.value = '缺少期望岗位或个人介绍，前端已拦截保存'
    uni.showToast({ title: '请完善简历信息', icon: 'none' })
    return
  }
  try {
    const data = await saveResume({
      expectedJob: form.expectedJob,
      expectedCity: form.expectedCity,
      expectedSalary: form.expectedSalary,
      skillTags: form.skillTags,
      certificateText: JSON.stringify(
        form.certificateList
          .map((item) => ({
            certificateName: item?.certificateName?.trim?.() || '',
            certificateNo: item?.certificateNo?.trim?.() || '',
            issuer: item?.issuer?.trim?.() || '',
            expireDate: item?.expireDate?.trim?.() || ''
          }))
          .filter((item) => item.certificateName)
      ),
      intro: form.intro
    })
    Object.assign(form, data || {})
    form.certificateList = normalizeCertificateList(data)
    resumeLastSavedAt.value = new Date().toLocaleString()
    resumeLastMessage.value = '简历保存成功，可重新返回岗位页发起投递'
    uni.showToast({ title: '保存成功', icon: 'none' })
  } catch (error) {
    resumeLastSavedAt.value = new Date().toLocaleString()
    resumeLastMessage.value = error.message || '保存失败'
    uni.showToast({ title: error.message || '保存失败', icon: 'none' })
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

onShow(loadData)
</script>

<style lang="scss">
.form-input,
.form-textarea {
  width: 100%;
  margin-top: 18rpx;
  padding: 20rpx 24rpx;
  border-radius: 18rpx;
  background: #f5f8fc;
  box-sizing: border-box;
  font-size: 28rpx;
  color: #16324f;
}

.form-textarea {
  min-height: 180rpx;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 24rpx;
}

.worker-title--small {
  font-size: 28rpx;
}

.certificate-card {
  margin-top: 18rpx;
  padding: 20rpx;
  border-radius: 20rpx;
  background: #f8fbff;
}

.form-input--nested {
  margin-top: 12rpx;
  background: #ffffff;
}

.certificate-card__actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 18rpx;
}

.certificate-card__remove {
  font-size: 24rpx;
  color: #dc3545;
}

.certificate-actions {
  margin-top: 18rpx;
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

.section-head--sub {
  margin-top: 20rpx;
}

.clear-action {
  font-size: 24rpx;
  color: #1f6fd6;
}
</style>
