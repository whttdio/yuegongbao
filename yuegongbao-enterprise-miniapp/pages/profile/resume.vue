<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page enterprise-page">
    <view class="worker-card worker-hero">
      <view class="section-head">
        <view>
          <view class="worker-title worker-title--display">{{ form.personName || '员工简历' }}</view>
          <view class="worker-subtitle">{{ form.mobile || '-' }} / {{ form.jobType || '未填写工种' }}</view>
        </view>
        <view class="worker-tag" :class="resumeComplete ? 'worker-tag--success' : 'worker-tag--warning'">
          {{ resumeComplete ? '资料较完整' : '待员工完善' }}
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="enterprise-notice enterprise-notice--readonly">
        <view class="enterprise-notice__head">
          <view class="enterprise-notice__title">企业侧只读参考</view>
          <view class="worker-tag worker-tag--info">员工协同视图</view>
        </view>
        <view class="enterprise-notice__desc">简历保存和更新依赖员工本人确认，企业端不再代员工修改求职资料。</view>
        <view class="enterprise-notice__reason">当前仅保留员工简历内容、证书列表和投递准备度查看，便于企业侧核对信息完整度。</view>
        <view class="enterprise-notice__actions">
          <button class="worker-button worker-button--secondary" @click="openPage('/pages/job/list')">查看岗位列表</button>
          <button class="worker-button" @click="openPage('/pages/profile/help')">查看说明</button>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="detail-grid">
        <view class="detail-item">
          <view class="detail-item__label">期望岗位</view>
          <view class="detail-item__value">{{ form.expectedJob || '-' }}</view>
        </view>
        <view class="detail-item">
          <view class="detail-item__label">期望城市</view>
          <view class="detail-item__value">{{ form.expectedCity || '-' }}</view>
        </view>
        <view class="detail-item">
          <view class="detail-item__label">期望薪资</view>
          <view class="detail-item__value">{{ form.expectedSalary || '-' }}</view>
        </view>
        <view class="detail-item">
          <view class="detail-item__label">技能标签</view>
          <view class="detail-item__value">{{ form.skillTags || '-' }}</view>
        </view>
      </view>
      <view class="enterprise-readonly-bar">
        <view class="enterprise-readonly-bar__text">{{ resumeGateText }}</view>
        <view class="worker-tag worker-tag--info">{{ certificateCountText }}</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">个人介绍</view>
      </view>
      <view class="detail-block">
        <view class="detail-block__content">{{ form.intro || '-' }}</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="enterprise-list-head">
        <view>
          <view class="worker-title">证书信息</view>
          <view class="enterprise-list-head__meta">仅作企业查看参考，证书维护仍需员工本人完成。</view>
        </view>
        <view class="worker-tag">{{ certificateCountText }}</view>
      </view>
      <view v-if="form.certificateList.length">
        <view v-for="(item, index) in form.certificateList" :key="`cert-${index}`" class="record-row">
          <view class="record-row__main">
            <view class="record-row__title">{{ item.certificateName || '未命名证书' }}</view>
            <view class="record-row__subtitle">{{ item.certificateNo || '-' }} / {{ item.issuer || '-' }}</view>
            <view class="enterprise-record-meta">
              <view class="enterprise-record-meta__item">到期 {{ item.expireDate || '-' }}</view>
            </view>
          </view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无证书信息</view>
        <view class="worker-empty__desc">如需补充特种作业证、高处作业证等，请引导员工本人在个人端完善。</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, reactive } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getResumeDetail } from '../../api/enterprise-service'
import { openPage } from '../../utils/navigation'

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

const certificateCountText = computed(() => `${form.certificateList.length || 0} 本`)
const requiredResumeFieldStatus = computed(() => ([
  { label: '期望岗位', ok: !!String(form.expectedJob || '').trim() },
  { label: '个人介绍', ok: !!String(form.intro || '').trim() }
]))
const resumeComplete = computed(() => requiredResumeFieldStatus.value.every((item) => item.ok))
const resumeGateText = computed(() => {
  const missing = requiredResumeFieldStatus.value.filter((item) => !item.ok).map((item) => item.label)
  return missing.length ? `投递仍缺：${missing.join('、')}` : '当前已具备基础投递资料'
})

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
  const data = await getResumeDetail()
  Object.assign(form, data || {})
  form.certificateList = normalizeCertificateList(data)
}

onShow(() => {
  loadData().catch((error) => {
    uni.showToast({ title: error.message || '加载简历失败', icon: 'none' })
  })
})
</script>
