<template>
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">外出作业申请</view>
      <view class="worker-subtitle">申请、附件和审批都已接入真实后端回执。</view>
    </view>

    <view class="worker-card">
      <view class="form-stack">
        <view class="form-field"><view class="form-field__label">作业地点</view><input v-model="form.location" class="form-input" placeholder="请输入作业地点" /></view>
        <view class="form-field"><view class="form-field__label">作业时间</view><input v-model="form.schedule" class="form-input" placeholder="例如 2026-06-25 08:00 - 18:00" /></view>
        <view class="form-field"><view class="form-field__label">监护人</view><input v-model="form.guardian" class="form-input" placeholder="请输入监护人姓名/电话" /></view>
        <view class="form-field"><view class="form-field__label">风险说明</view><textarea v-model="form.summary" class="form-textarea" placeholder="填写作业内容、风险点和防护措施" /></view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head"><view class="worker-title">现场附件</view></view>
      <view class="upload-zone" @click="handleUpload">
        <view class="upload-zone__title">{{ uploading ? '处理中...' : '上传现场照片' }}</view>
        <view class="upload-zone__desc">先登记，再补拍照上传。</view>
      </view>
      <view v-if="attachments.length" class="attachment-list">
        <view v-for="item in attachments" :key="item" class="attachment-row">
          <view class="attachment-row__text">{{ item }}</view>
          <view class="attachment-row__action" @click.stop="removeAttachment(item)">删除</view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--inline">暂未上传现场附件。</view>
    </view>

    <view class="worker-card">
      <view class="worker-button-row">
        <button class="worker-button" :disabled="!canSubmit || submitting" @click="handleSubmit">{{ submitting ? '提交中...' : '提交申请' }}</button>
      </view>
      <view v-if="submitMessage" class="result-block">
        <view class="result-block__label">提交结果</view>
        <view class="result-block__value">{{ submitMessage }}</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { getOutworkApplyDraft, saveOutworkAttachment, submitOutworkApply } from '../../api/high-risk'

const form = reactive({ location: '', schedule: '', guardian: '', summary: '' })
const attachments = ref([])
const uploading = ref(false)
const submitting = ref(false)
const submitMessage = ref('')
const canSubmit = computed(() => form.location && form.schedule && form.guardian && form.summary)

async function loadData() {
  try {
    const data = await getOutworkApplyDraft()
    Object.assign(form, data.form || {})
    attachments.value = Array.isArray(data.attachments) ? data.attachments : []
  } catch (error) {
    uni.showToast({ title: error.message || '加载申请草稿失败', icon: 'none' })
  }
}

async function handleUpload() {
  uploading.value = true
  try {
    const result = await saveOutworkAttachment({ location: form.location })
    attachments.value = [...attachments.value, result.attachmentName]
    uni.showToast({ title: result.message || '附件已保存', icon: 'none' })
  } catch (error) {
    uni.showToast({ title: error.message || '上传附件失败', icon: 'none' })
  } finally {
    uploading.value = false
  }
}

function removeAttachment(item) {
  attachments.value = attachments.value.filter((current) => current !== item)
}

async function handleSubmit() {
  submitting.value = true
  try {
    const result = await submitOutworkApply({ ...form, attachments: attachments.value })
    submitMessage.value = result.message || ''
    uni.showToast({ title: result.message || '已提交', icon: 'none' })
  } catch (error) {
    submitMessage.value = error.message || ''
    uni.showToast({ title: error.message || '提交申请失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}

onMounted(loadData)
</script>
