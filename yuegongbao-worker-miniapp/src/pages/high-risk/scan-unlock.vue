<template>
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">扫码开机</view>
      <view class="worker-subtitle">高危岗位在完成刷脸与资质核验前，不能直接进入开机申请。</view>
      <view class="hero-stat-grid">
        <view class="hero-stat"><view class="hero-stat__value">{{ form.deviceCode || '--' }}</view><view class="hero-stat__label">设备编号</view></view>
        <view class="hero-stat"><view class="hero-stat__value">{{ form.faceVerified ? '已通过' : '待校验' }}</view><view class="hero-stat__label">人脸校验</view></view>
        <view class="hero-stat"><view class="hero-stat__value">{{ readyCount }}/3</view><view class="hero-stat__label">开机条件</view></view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head"><view class="worker-title">核验信息</view></view>
      <view class="form-stack">
        <view class="form-field">
          <view class="form-field__label">设备编号</view>
          <input v-model="form.deviceCode" class="form-input" placeholder="请输入设备编号或扫码回填" />
        </view>
        <view class="worker-button-row">
          <button class="worker-button" :disabled="scanning" @click="handleScan">{{ scanning ? '处理中...' : '扫码获取' }}</button>
          <button class="worker-button worker-button--secondary" :disabled="verifying" @click="handleFaceVerify">{{ verifying ? '校验中...' : '刷脸核验' }}</button>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head"><view class="worker-title">资格摘要</view></view>
      <view class="status-grid">
        <view class="status-item"><view class="status-item__label">特种作业证</view><view class="status-item__value">{{ form.certificateStatus }}</view></view>
        <view class="status-item"><view class="status-item__label">工伤保险</view><view class="status-item__value">{{ form.injuryInsuranceStatus }}</view></view>
        <view class="status-item"><view class="status-item__label">安责险</view><view class="status-item__value">{{ form.aqInsuranceStatus }}</view></view>
        <view class="status-item"><view class="status-item__label">开机结论</view><view class="status-item__value">{{ canSubmit ? '可提交' : '需补齐' }}</view></view>
      </view>
      <view v-if="tips.length" class="summary-panel">
        <view v-for="(item, index) in tips" :key="`${item}-${index}`" class="summary-panel__content">{{ item }}</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head"><view class="worker-title">提交申请</view></view>
      <view class="worker-button-row">
        <button class="worker-button" :disabled="!canSubmit || submitting" @click="handleSubmit">{{ submitting ? '提交中...' : '提交开机申请' }}</button>
        <button class="worker-button worker-button--ghost" @click="resetForm">重置</button>
      </view>
      <view v-if="resultMessage" class="result-block">
        <view class="result-block__label">处理结果</view>
        <view class="result-block__value">{{ resultMessage }}</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { getHighRiskUnlockDashboard, submitHighRiskUnlock, verifyHighRiskFace, verifyHighRiskScan } from '../../api/high-risk'

const PENDING_STATUS = '待校验'
const form = reactive({
  deviceCode: '',
  faceVerified: false,
  certificateStatus: PENDING_STATUS,
  injuryInsuranceStatus: PENDING_STATUS,
  aqInsuranceStatus: PENDING_STATUS
})
const tips = ref([])
const resultMessage = ref('')
const scanning = ref(false)
const verifying = ref(false)
const submitting = ref(false)
const qualificationReady = computed(() => (
  form.certificateStatus !== PENDING_STATUS
  && form.injuryInsuranceStatus !== PENDING_STATUS
  && form.aqInsuranceStatus !== PENDING_STATUS
))
const readyCount = computed(() => Number(Boolean(form.deviceCode)) + Number(Boolean(form.faceVerified)) + Number(qualificationReady.value))
const canSubmit = computed(() => Boolean(form.deviceCode) && form.faceVerified && qualificationReady.value)

async function loadData() {
  try {
    const data = await getHighRiskUnlockDashboard()
    Object.assign(form, data.form || {})
    tips.value = Array.isArray(data.tips) ? data.tips : []
    resultMessage.value = data.summary || ''
  } catch (error) {
    resultMessage.value = error.message || '加载开机信息失败'
    uni.showToast({ title: resultMessage.value, icon: 'none' })
  }
}

async function handleScan() {
  scanning.value = true
  try {
    const result = await verifyHighRiskScan({ deviceCode: form.deviceCode })
    form.deviceCode = result.deviceCode || form.deviceCode
    resultMessage.value = result.message || ''
    uni.showToast({ title: result.message || '扫码成功', icon: 'none' })
  } catch (error) {
    resultMessage.value = error.message || '扫码失败'
    uni.showToast({ title: resultMessage.value, icon: 'none' })
  } finally {
    scanning.value = false
  }
}

async function handleFaceVerify() {
  verifying.value = true
  try {
    const result = await verifyHighRiskFace({ deviceCode: form.deviceCode })
    form.faceVerified = !!result.faceVerified
    form.certificateStatus = result.certificateStatus || form.certificateStatus
    form.injuryInsuranceStatus = result.injuryInsuranceStatus || form.injuryInsuranceStatus
    form.aqInsuranceStatus = result.aqInsuranceStatus || form.aqInsuranceStatus
    resultMessage.value = result.message || ''
    uni.showToast({ title: result.message || '核验通过', icon: 'none' })
  } catch (error) {
    resultMessage.value = error.message || '核验失败'
    uni.showToast({ title: resultMessage.value, icon: 'none' })
  } finally {
    verifying.value = false
  }
}

async function handleSubmit() {
  submitting.value = true
  try {
    const result = await submitHighRiskUnlock({ ...form })
    resultMessage.value = result.message || ''
    uni.showToast({ title: result.message || '已提交', icon: 'none' })
  } catch (error) {
    resultMessage.value = error.message || '提交失败'
    uni.showToast({ title: resultMessage.value, icon: 'none' })
  } finally {
    submitting.value = false
  }
}

function resetForm() {
  form.deviceCode = ''
  form.faceVerified = false
  form.certificateStatus = PENDING_STATUS
  form.injuryInsuranceStatus = PENDING_STATUS
  form.aqInsuranceStatus = PENDING_STATUS
  resultMessage.value = ''
}

onMounted(loadData)
</script>
