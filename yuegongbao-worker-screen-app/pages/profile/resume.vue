<template>
  <view class="worker-page resume-page">
    <view class="resume-hero worker-card worker-hero">
      <view class="resume-hero__top">
        <view>
          <view class="resume-hero__name">{{ form.personName || '劳动者用户' }}</view>
          <view class="resume-hero__meta">{{ form.mobile || '-' }} · {{ form.jobType || '未填写工种' }}</view>
        </view>
        <view class="worker-tag" :class="resumeComplete ? 'worker-tag--success' : 'worker-tag--warning'">
          {{ resumeComplete ? '可投递' : '待完善' }}
        </view>
      </view>
      <view class="resume-hero__hint">{{ resumeGateText }}</view>
    </view>

    <view class="worker-card resume-form-card">
      <view class="section-head">
        <view class="worker-title">求职意向</view>
      </view>

      <view class="form-stack">
        <view class="form-field">
          <view class="form-field__label">期望岗位 <text class="form-field__required">*</text></view>
          <input v-model="form.expectedJob" class="form-input" placeholder="如：焊工、装配工" />
        </view>
        <view class="form-field">
          <view class="form-field__label">期望城市</view>
          <input v-model="form.expectedCity" class="form-input" placeholder="如：广州、深圳" />
        </view>
        <view class="form-field">
          <view class="form-field__label">期望薪资</view>
          <input v-model="form.expectedSalary" class="form-input" placeholder="如：9000-12000" />
        </view>
        <view class="form-field">
          <view class="form-field__label">技能标签</view>
          <input v-model="form.skillTags" class="form-input" placeholder="多个技能用逗号分隔" />
        </view>
      </view>

      <view class="form-section">
        <view class="form-field">
          <view class="form-field__label">个人介绍 <text class="form-field__required">*</text></view>
          <textarea
            v-model="form.intro"
            class="form-textarea"
            placeholder="简要介绍工作经验、擅长工种、持证情况与求职诉求"
          />
        </view>
      </view>

      <view class="form-section">
        <view class="section-head section-head--compact">
          <view>
            <view class="worker-title worker-title--small">证书管理</view>
            <view class="form-field__hint">维护证书名称、编号、发证单位与到期日期</view>
          </view>
          <view class="worker-tag worker-tag--info">{{ certificateCountText }}</view>
        </view>

        <view v-if="form.certificateList.length" class="certificate-list">
          <view
            v-for="(item, index) in form.certificateList"
            :key="`cert-${index}`"
            class="certificate-card"
          >
            <view class="certificate-card__head">
              <view class="certificate-card__index">证书 {{ index + 1 }}</view>
              <view class="certificate-card__remove" @click="removeCertificate(index)">删除</view>
            </view>
            <view class="form-stack certificate-card__fields">
              <view class="form-field">
                <view class="form-field__label">证书名称</view>
                <input v-model="item.certificateName" class="form-input form-input--nested" placeholder="如：焊工特种作业证" />
              </view>
              <view class="form-field">
                <view class="form-field__label">证书编号</view>
                <input v-model="item.certificateNo" class="form-input form-input--nested" placeholder="请输入证书编号" />
              </view>
              <view class="form-field">
                <view class="form-field__label">发证单位</view>
                <input v-model="item.issuer" class="form-input form-input--nested" placeholder="请输入发证单位" />
              </view>
              <view class="form-field">
                <view class="form-field__label">到期日期</view>
                <input v-model="item.expireDate" class="form-input form-input--nested" placeholder="如 2027-12-31" />
              </view>
            </view>
          </view>
        </view>

        <view v-else class="worker-empty worker-empty--inline resume-cert-empty">
          <view class="worker-empty__title">暂未添加证书</view>
          <view class="worker-empty__desc">如有特种作业证、高处作业证等，可在此补充。</view>
        </view>

        <button class="worker-button worker-button--secondary resume-add-cert" @click="addCertificate">
          新增证书
        </button>
      </view>

      <button class="worker-button resume-submit" @click="submitResume">保存简历</button>
    </view>

    <view class="worker-card resume-preview-card">
      <view class="section-head">
        <view class="worker-title">简历预览</view>
      </view>
      <view class="preview-grid">
        <view class="preview-item">
          <view class="preview-item__label">姓名</view>
          <view class="preview-item__value">{{ form.personName || '-' }}</view>
        </view>
        <view class="preview-item">
          <view class="preview-item__label">手机号</view>
          <view class="preview-item__value">{{ form.mobile || '-' }}</view>
        </view>
        <view class="preview-item">
          <view class="preview-item__label">工种</view>
          <view class="preview-item__value">{{ form.jobType || '-' }}</view>
        </view>
        <view class="preview-item">
          <view class="preview-item__label">证书状态</view>
          <view class="preview-item__value">{{ form.certificateStatusText || certificateCountText }}</view>
        </view>
      </view>
      <view class="preview-block">
        <view class="preview-block__label">期望岗位</view>
        <view class="preview-block__value">{{ form.expectedJob || '-' }}</view>
      </view>
      <view class="preview-block">
        <view class="preview-block__label">期望城市 / 薪资</view>
        <view class="preview-block__value">
          {{ form.expectedCity || '-' }} · {{ form.expectedSalary || '-' }}
        </view>
      </view>
      <view class="preview-block">
        <view class="preview-block__label">技能标签</view>
        <view class="preview-block__value">{{ form.skillTags || '-' }}</view>
      </view>
      <view class="preview-block">
        <view class="preview-block__label">个人介绍</view>
        <view class="preview-block__value preview-block__value--multiline">{{ form.intro || '-' }}</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getResumeDetail, saveResume } from '../../api/screen'

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
  return missing.length ? `投递仍缺：${missing.join('、')}` : '已满足当前投递必填项'
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
@import '../../styles/worker-ui.scss';

.resume-hero__top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
}

.resume-hero__name {
  font-size: 38rpx;
  font-weight: 800;
  color: #fff;
  line-height: 1.25;
}

.resume-hero__meta {
  margin-top: 8rpx;
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.86);
}

.resume-hero__hint {
  margin-top: 18rpx;
  padding: 14rpx 18rpx;
  border-radius: 16rpx;
  background: rgba(255, 255, 255, 0.14);
  color: rgba(255, 255, 255, 0.92);
  font-size: 24rpx;
  line-height: 1.55;
}

.form-field__required {
  color: #ffd166;
}

.certificate-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.certificate-card {
  padding: 22rpx 20rpx;
  border-radius: 20rpx;
  background: linear-gradient(180deg, #f8fcfb 0%, #f2f7f8 100%);
  border: 1rpx solid $ygb-border-light;
}

.certificate-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16rpx;
}

.certificate-card__index {
  font-size: 26rpx;
  font-weight: 650;
  color: $ygb-text-body;
}

.certificate-card__remove {
  font-size: 24rpx;
  color: $ygb-danger;
}

.certificate-card__fields {
  gap: 16rpx;
}

.form-input--nested {
  background: #ffffff;
}

.resume-cert-empty {
  margin-bottom: 16rpx;
  padding: 28rpx 16rpx;
  border-radius: 18rpx;
  background: $ygb-surface-muted;
}

.resume-add-cert {
  width: 100%;
  margin-top: 16rpx;
}

.resume-submit {
  width: 100%;
  margin-top: 32rpx;
}

.preview-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14rpx;
}

.preview-item {
  padding: 18rpx 16rpx;
  border-radius: 16rpx;
  background: $ygb-surface-muted;
  border: 1rpx solid $ygb-border-light;
}

.preview-item__label {
  font-size: 22rpx;
  color: $ygb-text-tertiary;
}

.preview-item__value {
  margin-top: 8rpx;
  font-size: 26rpx;
  font-weight: 650;
  color: $ygb-text-body;
  line-height: 1.45;
  word-break: break-all;
}

.preview-block {
  margin-top: 16rpx;
  padding: 18rpx 20rpx;
  border-radius: 16rpx;
  background: linear-gradient(180deg, #ffffff 0%, #f7fafb 100%);
  border: 1rpx solid $ygb-border-light;
}

.preview-block__label {
  font-size: 22rpx;
  color: $ygb-text-tertiary;
}

.preview-block__value {
  margin-top: 8rpx;
  font-size: 26rpx;
  color: $ygb-text-body;
  line-height: 1.55;
  word-break: break-all;
}

.preview-block__value--multiline {
  white-space: pre-wrap;
}
</style>
