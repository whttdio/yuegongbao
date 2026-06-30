<template>
  <view class="worker-page complaint-page">
    <view class="worker-card worker-hero complaint-form-card">
      <view class="complaint-form-head">
        <view class="worker-title worker-title--display">投诉举报</view>
        <view class="worker-subtitle">请如实填写问题信息，必要时上传工资、社保或现场证据</view>
      </view>

      <view class="form-stack">
        <view class="form-field">
          <view class="form-field__label">投诉类型</view>
          <picker class="form-picker" :range="typeOptions" @change="handleTypeChange">
            <view class="form-picker__text">{{ typeLabel }}</view>
          </picker>
        </view>

        <view class="form-field">
          <view class="form-field__label">投诉标题</view>
          <input v-model="form.title" class="form-input" placeholder="请简要概括问题，如：3月工资未发放" />
        </view>

        <view class="form-field">
          <view class="form-field__label">问题经过</view>
          <textarea
            v-model="form.content"
            class="form-textarea"
            placeholder="请描述发生时间、地点、涉及金额或人员等关键信息"
          />
        </view>

        <view class="form-field">
          <view class="form-field__label">联系电话</view>
          <input
            v-model="form.contactMobile"
            class="form-input"
            type="number"
            maxlength="11"
            placeholder="便于工作人员联系核实"
          />
        </view>
      </view>

      <view class="form-section">
        <view class="section-head section-head--compact">
          <view>
            <view class="worker-title worker-title--small">证据附件</view>
            <view class="form-field__hint">支持拍照或相册上传，单张不超过 2MB</view>
          </view>
          <view class="more-link" @click="goCamera">拍照归档</view>
        </view>

        <view class="upload-zone" @click="chooseEvidence">
          <view class="upload-zone__icon">{{ uploading ? '…' : '+' }}</view>
          <view class="upload-zone__title">{{ uploading ? '上传中...' : '选择图片并上传' }}</view>
          <view class="upload-zone__desc">可上传工资条、合同、聊天记录或现场照片</view>
        </view>

        <view v-if="attachmentUrls.length" class="attachment-list">
          <view v-for="(item, index) in attachmentUrls" :key="`${item}-${index}`" class="attachment-chip">
            <view class="attachment-chip__index">证据 {{ index + 1 }}</view>
            <view class="attachment-chip__text">{{ formatAttachmentName(item) }}</view>
            <view class="attachment-chip__action" @click.stop="removeAttachment(index)">移除</view>
          </view>
        </view>
        <view v-else class="worker-empty worker-empty--inline complaint-empty">
          <view class="worker-empty__title">当前暂无证据附件</view>
          <view class="worker-empty__desc">
            可先拍照留存工资、社保、工伤或现场隐患证据，再回到当前投诉单继续补齐材料。
          </view>
        </view>
      </view>

      <view class="switch-panel">
        <view class="switch-row">
          <view>
            <view class="switch-row__label">匿名提交</view>
            <view class="switch-row__hint">隐藏个人信息，仅保留必要联系方式</view>
          </view>
          <switch color="#0f766e" :checked="form.anonymous" @change="form.anonymous = $event.detail.value" />
        </view>
        <view class="switch-row">
          <view>
            <view class="switch-row__label">同步工会</view>
            <view class="switch-row__hint">同步至工会服务，便于协同维权</view>
          </view>
          <switch color="#0f766e" :checked="form.syncUnion" @change="form.syncUnion = $event.detail.value" />
        </view>
      </view>

      <button class="worker-button complaint-submit" @click="submitComplaint">提交投诉</button>
    </view>

    <view class="worker-card complaint-list-card">
      <view class="section-head">
        <view class="worker-title">我的投诉</view>
        <view v-if="rows.length" class="worker-tag worker-tag--info">{{ rows.length }} 条</view>
      </view>

      <view v-if="rows.length" class="complaint-list">
        <view
          v-for="item in rows"
          :key="item.complaintId"
          class="complaint-item"
          @click="openDetail(item)"
        >
          <view class="complaint-item__main">
            <view class="complaint-item__title">{{ item.title }}</view>
            <view class="complaint-item__meta">
              {{ item.complaintType || '-' }} · {{ item.syncUnionText || '未同步工会' }}
            </view>
          </view>
          <view class="worker-tag" :class="getStatusTagClass(item.statusText)">
            {{ item.statusText || '-' }}
          </view>
        </view>
      </view>

      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无投诉记录</view>
        <view class="worker-empty__desc">
          如暂未形成正式投诉，可先进入法律咨询梳理问题；如已有现场证据，也可先去拍照归档后再提交。
        </view>
        <view class="complaint-empty-actions">
          <button class="worker-button worker-button--secondary" @click="goLegal">法律咨询</button>
          <button class="worker-button" @click="goCamera">去拍照归档</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { createComplaint, createWorkerUploadRecord, getComplaintList, uploadWorkerImage } from '../../api/worker'

const MAX_UPLOAD_SIZE = 2 * 1024 * 1024
const typeOptions = ['欠薪问题', '社保异常', '劳动合同问题', '非法用工问题', '安全隐患线索', '其他']
const selectedTypeIndex = ref(0)
const rows = ref([])
const uploading = ref(false)
const form = reactive({
  complaintType: typeOptions[0],
  title: '',
  content: '',
  contactMobile: '',
  attachments: '',
  anonymous: false,
  syncUnion: false
})

const typeLabel = computed(() => form.complaintType || '请选择投诉类型')
const attachmentUrls = computed(() => {
  if (!form.attachments) {
    return []
  }
  return form.attachments.split(',').map((item) => item.trim()).filter(Boolean)
})

function formatAttachmentName(url) {
  const text = String(url || '')
  if (!text) {
    return '-'
  }
  const parts = text.split('/')
  const name = parts[parts.length - 1] || text
  return name.length > 28 ? `${name.slice(0, 28)}...` : name
}

function getStatusTagClass(statusText) {
  const text = String(statusText || '')
  if (text.includes('待') || text.includes('处理中') || text.includes('受理')) {
    return 'worker-tag--warning'
  }
  if (text.includes('完成') || text.includes('已结') || text.includes('通过')) {
    return 'worker-tag--success'
  }
  if (text.includes('退') || text.includes('拒') || text.includes('驳回')) {
    return 'worker-tag--danger'
  }
  return 'worker-tag--info'
}

function normalizeAttachments(urls) {
  form.attachments = urls.filter(Boolean).join(',')
}

function handleTypeChange(event) {
  selectedTypeIndex.value = Number(event.detail.value || 0)
  form.complaintType = typeOptions[selectedTypeIndex.value]
}

async function loadList(options = {}) {
  try {
    const data = await getComplaintList()
    rows.value = data?.rows || []
  } catch (error) {
    uni.showToast({ title: error.message || '加载投诉失败', icon: 'none' })
  }
}

function removeAttachment(index) {
  const next = [...attachmentUrls.value]
  next.splice(index, 1)
  normalizeAttachments(next)
}

function goCamera() {
  uni.navigateTo({ url: '/pages/camera/index' })
}

function goLegal() {
  uni.navigateTo({ url: '/pages/legal/index' })
}

function chooseImage() {
  return new Promise((resolve, reject) => {
    uni.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['camera', 'album'],
      success: resolve,
      fail: reject
    })
  })
}

async function chooseEvidence() {
  if (uploading.value) {
    return
  }
  uploading.value = true
  try {
    const res = await chooseImage()
    const filePath = res?.tempFilePaths?.[0]
    const tempFile = res?.tempFiles?.[0] || {}
    if (!filePath) {
      throw new Error('未选择图片')
    }
    if (Number(tempFile.size || 0) > MAX_UPLOAD_SIZE) {
      throw new Error('证据图片需压缩到 2MB 以内后再上传')
    }
    const payload = await uploadWorkerImage(filePath)
    const fileUrl = payload?.url || ''
    await createWorkerUploadRecord({
      categoryCode: 'complaint',
      categoryName: '投诉证据',
      fileUrl,
      fileName: payload?.fileName || '',
      originalFilename: payload?.originalFilename || '',
      fileSize: Number(tempFile.size || 0),
      contentType: tempFile.type || 'image/*',
      sourceModule: 'complaint'
    })
    normalizeAttachments([...attachmentUrls.value, fileUrl])
    uni.showToast({ title: '证据上传成功', icon: 'none' })
  } catch (error) {
    if (error?.errMsg?.includes('cancel')) {
      return
    }
    uni.showToast({ title: error.message || '证据上传失败', icon: 'none' })
  } finally {
    uploading.value = false
  }
}

async function submitComplaint() {
  if (!form.title || !form.content) {
    uni.showToast({ title: '请完善投诉信息', icon: 'none' })
    return
  }
  try {
    const result = await createComplaint(form)
    uni.showToast({
      title: result?.message || '投诉已提交',
      icon: 'none'
    })
    form.title = ''
    form.content = ''
    form.contactMobile = ''
    form.attachments = ''
    form.anonymous = false
    form.syncUnion = false
    await loadList({ preserveMessage: true })
  } catch (error) {
    uni.showToast({ title: error.message || '提交失败', icon: 'none' })
  }
}

function openDetail(item) {
  if (!item?.complaintId) {
    return
  }
  uni.navigateTo({ url: `/pages/complaint/detail?complaintId=${item.complaintId}` })
}

onLoad((options) => {
  if (options?.attachments) {
    const incoming = decodeURIComponent(options.attachments)
    normalizeAttachments([...attachmentUrls.value, incoming])
  }
})

onShow(loadList)
</script>

<style lang="scss">
.complaint-form-head {
  margin-bottom: 28rpx;
}

.complaint-form-head .worker-subtitle {
  margin-top: 12rpx;
}

.complaint-empty {
  margin-top: 16rpx;
  padding: 28rpx 16rpx;
  border-radius: 18rpx;
  background: $ygb-surface-muted;
}

.complaint-submit {
  width: 100%;
  margin-top: 32rpx;
}

.switch-row__label {
  font-size: 28rpx;
  font-weight: 650;
  color: $ygb-text-body;
}

.switch-row__hint {
  margin-top: 6rpx;
  font-size: 22rpx;
  color: $ygb-text-tertiary;
  line-height: 1.5;
}

.attachment-list {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
  margin-top: 16rpx;
}

.attachment-chip {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 18rpx 20rpx;
  border-radius: 18rpx;
  background: #ffffff;
  border: 1rpx solid $ygb-border-light;
}

.attachment-chip__index {
  flex-shrink: 0;
  padding: 6rpx 12rpx;
  border-radius: 999rpx;
  background: $ygb-primary-soft;
  color: #0b6b64;
  font-size: 20rpx;
  font-weight: 650;
}

.attachment-chip__text {
  flex: 1;
  min-width: 0;
  font-size: 22rpx;
  color: $ygb-text-secondary;
  line-height: 1.5;
  word-break: break-all;
}

.attachment-chip__action {
  flex-shrink: 0;
  font-size: 24rpx;
  color: $ygb-danger;
}

.complaint-list {
  display: flex;
  flex-direction: column;
  gap: 14rpx;
}

.complaint-item {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
  padding: 22rpx 20rpx;
  border-radius: 20rpx;
  background: linear-gradient(180deg, #ffffff 0%, #f7fafb 100%);
  border: 1rpx solid $ygb-border-light;
  box-shadow: 0 4rpx 12rpx rgba(24, 50, 71, 0.04);
  transition: transform 0.22s ease, box-shadow 0.22s ease;
}

.complaint-item:active {
  transform: scale(0.99);
  box-shadow: 0 2rpx 8rpx rgba(24, 50, 71, 0.05);
}

.complaint-item__main {
  flex: 1;
  min-width: 0;
}

.complaint-item__title {
  font-size: 28rpx;
  font-weight: 650;
  color: $ygb-text-body;
  line-height: 1.45;
}

.complaint-item__meta {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: $ygb-text-tertiary;
  line-height: 1.5;
}

.complaint-empty-actions {
  display: flex;
  gap: 16rpx;
  margin-top: 24rpx;
}

.complaint-empty-actions .worker-button {
  flex: 1;
}
</style>
