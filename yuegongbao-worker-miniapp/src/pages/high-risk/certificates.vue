<template>
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">证件管理</view>
      <view class="worker-subtitle">证件状态、续期和上传都走真实后端回执。</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">证件状态</view>
        <view class="worker-tag worker-tag--warning">{{ expiringCount }} 个临期</view>
      </view>
      <view v-if="certificates.length">
        <view class="list-row" v-for="item in certificates" :key="item.id">
          <view>
            <view class="list-row__title">{{ item.name }}</view>
            <view class="list-row__subtitle">{{ item.code }} / 到期 {{ item.expireAt }}</view>
          </view>
          <view class="settings-row__tag" :class="tagClass(item.status)">{{ item.status }}</view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">当前暂无证件记录</view>
    </view>

    <view class="worker-card">
      <view class="worker-button-row">
        <button class="worker-button" :disabled="submitting || !renewableIds.length" @click="handleRenew">{{ submitting ? '处理中...' : '续期申请' }}</button>
        <button class="worker-button worker-button--secondary" :disabled="submitting" @click="handleUpload">{{ submitting ? '处理中...' : '上传新证' }}</button>
      </view>
      <view v-if="actionMessage" class="result-block">
        <view class="result-block__label">操作反馈</view>
        <view class="result-block__value">{{ actionMessage }}</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getHighRiskCertificates, submitCertificateRenew, submitCertificateUpload } from '../../api/high-risk'

const certificates = ref([])
const actionMessage = ref('')
const submitting = ref(false)
const expiringCount = computed(() => certificates.value.filter((item) => item.status !== '有效').length)
const renewableIds = computed(() => certificates.value.filter((item) => item.status !== '有效').map((item) => item.id))

function tagClass(status) {
  if (status === '有效') return 'worker-tag--success'
  if (status === '临期') return 'worker-tag--warning'
  return 'worker-tag--danger'
}

async function loadData() {
  try {
    const data = await getHighRiskCertificates()
    certificates.value = Array.isArray(data.list) ? data.list : []
  } catch (error) {
    actionMessage.value = error.message || '加载证件信息失败'
    uni.showToast({ title: actionMessage.value, icon: 'none' })
  }
}

async function handleRenew() {
  if (!renewableIds.value.length) {
    uni.showToast({ title: '当前没有需要续期的证件', icon: 'none' })
    return
  }
  submitting.value = true
  try {
    const result = await submitCertificateRenew({ certificateIds: renewableIds.value })
    actionMessage.value = result.message || '续期申请已提交'
    uni.showToast({ title: actionMessage.value, icon: 'none' })
    await loadData()
  } catch (error) {
    actionMessage.value = error.message || '续期申请失败'
    uni.showToast({ title: actionMessage.value, icon: 'none' })
  } finally {
    submitting.value = false
  }
}

async function handleUpload() {
  submitting.value = true
  try {
    const result = await submitCertificateUpload({ source: 'worker-miniapp' })
    actionMessage.value = result.message || '证件上传已登记'
    uni.showToast({ title: actionMessage.value, icon: 'none' })
    await loadData()
  } catch (error) {
    actionMessage.value = error.message || '证件上传失败'
    uni.showToast({ title: actionMessage.value, icon: 'none' })
  } finally {
    submitting.value = false
  }
}

onMounted(loadData)
</script>
