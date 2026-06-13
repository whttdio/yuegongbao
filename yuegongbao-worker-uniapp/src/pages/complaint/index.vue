<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="worker-title">发起投诉举报</view>
      <picker class="form-picker" :range="typeOptions" @change="handleTypeChange">
        <view class="form-picker__text">{{ typeLabel }}</view>
      </picker>
      <input v-model="form.title" class="form-input" placeholder="请输入标题" />
      <textarea v-model="form.content" class="form-textarea" placeholder="请描述问题经过" />
      <input v-model="form.contactMobile" class="form-input" placeholder="请输入联系电话" />

      <view class="section-head section-head--compact">
        <view class="worker-title worker-title--small">证据附件</view>
        <view class="more-link" @click="goCamera">拍照归档</view>
      </view>
      <view class="attachment-actions">
        <button class="worker-button worker-button--secondary" :disabled="uploading" @click="chooseEvidence">
          {{ uploading ? '上传中...' : '选择图片并上传' }}
        </button>
      </view>
      <view v-if="attachmentUrls.length" class="attachment-list">
        <view v-for="(item, index) in attachmentUrls" :key="`${item}-${index}`" class="attachment-row">
          <view class="attachment-row__text">{{ item }}</view>
          <view class="attachment-row__action" @click="removeAttachment(index)">移除</view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--inline">
        <view class="worker-empty__title">当前暂无证据附件</view>
        <view class="worker-empty__desc">
          可先去拍照留存工资、社保、工伤或现场隐患证据，再回到当前投诉单继续补齐材料。
        </view>
      </view>

      <view class="switch-row">
        <text>匿名提交</text>
        <switch :checked="form.anonymous" @change="form.anonymous = $event.detail.value" />
      </view>
      <view class="switch-row">
        <text>同步工会</text>
        <switch :checked="form.syncUnion" @change="form.syncUnion = $event.detail.value" />
      </view>
      <button class="worker-button" @click="submitComplaint">提交投诉</button>
    </view>

    <view class="worker-card">
      <view class="worker-title">我的投诉</view>
      <view v-if="rows.length">
        <view v-for="item in rows" :key="item.complaintId" class="list-row" @click="openDetail(item)">
          <view>
            <view class="list-row__title">{{ item.title }}</view>
            <view class="list-row__subtitle">
              {{ item.complaintType || '-' }} / {{ item.syncUnionText || '未同步工会' }}
            </view>
          </view>
          <view class="worker-tag">{{ item.statusText || '-' }}</view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无投诉记录</view>
        <view class="worker-empty__desc">
          如暂未形成正式投诉，可先进入法律咨询梳理问题；如已有现场证据，也可先去拍照归档后再提交。
        </view>
        <view class="attachment-actions attachment-actions--empty">
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
const WORKER_COMPLAINT_DIAGNOSTICS_KEY = 'worker_complaint_diagnostics'
const EXPECTED_COMPLAINT_CHAIN_PAGES = ['投诉举报', '投诉详情', '拍照归档', '上传记录', '法律咨询', '消息中心']

const typeOptions = ['欠薪问题', '社保异常', '劳动合同问题', '非法用工问题', '安全隐患线索', '其他']
const selectedTypeIndex = ref(0)
const rows = ref([])
const uploading = ref(false)
const complaintLastLoadedAt = ref('')
const complaintLastSubmittedAt = ref('')
const complaintLastAttachmentAt = ref('')
const complaintLastActionAt = ref('')
const complaintLastMessage = ref('')
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
const complaintChainCoverageText = computed(() => EXPECTED_COMPLAINT_CHAIN_PAGES.join(' / '))
const complaintFormSummaryText = computed(() => {
  return [
    form.complaintType || '未选类型',
    `附件 ${attachmentUrls.value.length} 个`,
    form.anonymous ? '匿名' : '实名',
    form.syncUnion ? '同步工会' : '不同步工会'
  ].join(' / ')
})
const complaintStatusSummaryText = computed(() => {
  if (!rows.value.length) {
    return '暂无投诉记录'
  }
  const statusMap = rows.value.reduce((result, item) => {
    const key = item?.statusText || '未知状态'
    result[key] = (result[key] || 0) + 1
    return result
  }, {})
  return Object.keys(statusMap).map((key) => `${key} ${statusMap[key]} 条`).join(' / ')
})
const latestComplaintSummaryText = computed(() => {
  if (!rows.value.length) {
    return '暂无记录'
  }
  const latest = rows.value[0] || {}
  return `${latest.title || '-'} / ${latest.statusText || '-'} / ${latest.syncUnionText || '未同步工会'}`
})
const complaintAttachmentConsistencyText = computed(() => {
  const latestAttachments = String(rows.value[0]?.attachments || '')
    .split(',')
    .map((item) => item.trim())
    .filter(Boolean)
  if (!attachmentUrls.value.length && !latestAttachments.length) {
    return '当前未带入证据，需结合拍照归档和上传记录联调'
  }
  if (attachmentUrls.value.length && !rows.value.length) {
    return `表单已带入 ${attachmentUrls.value.length} 项证据 / 待提交后验收列表刷新`
  }
  return `表单 ${attachmentUrls.value.length} 项 / 最近投诉 ${latestAttachments.length} 项`
})
const complaintListConsistencyText = computed(() => {
  if (!rows.value.length) {
    return '当前无投诉记录，需用真实提交验证列表刷新与详情下钻'
  }
  const syncUnionCount = rows.value.filter((item) => String(item?.syncUnionText || '').includes('同步')).length
  return `记录 ${rows.value.length} 条 / 同步工会 ${syncUnionCount} 条 / 可下钻投诉详情`
})
const complaintLinkageText = computed(() => {
  if (String(complaintLastMessage.value || '').includes('消息中心')) {
    return '提交结果已提示消息中心与推送联动，剩余真机送达验收'
  }
  if (attachmentUrls.value.length) {
    return '证据已可从拍照归档回填，提交后需继续验收消息和推送落页'
  }
  return '需串联拍照归档、投诉提交、消息中心和推送结果通知'
})
const complaintSnapshotText = computed(() => {
  return [
    '## 投诉链验收摘要',
    `- 链路核对：${complaintChainCoverageText.value}`,
    `- 最近联动：${complaintLastActionAt.value || '-'}`,
    `- 最近加载：${complaintLastLoadedAt.value || '-'}`,
    `- 最近传证据：${complaintLastAttachmentAt.value || '-'}`,
    `- 最近提交：${complaintLastSubmittedAt.value || '-'}`,
    `- 当前表单：${complaintFormSummaryText.value}`,
    `- 附件链路：${complaintAttachmentConsistencyText.value}`,
    `- 状态分布：${complaintStatusSummaryText.value}`,
    `- 最近一条：${latestComplaintSummaryText.value}`,
    `- 列表 / 下钻：${complaintListConsistencyText.value}`,
    `- 消息联动：${complaintLinkageText.value}`,
    `- 说明：${complaintLastMessage.value || '-'}`,
    '- 链路关联：拍照归档 / 投诉举报 / 法律咨询 / 上传归档 / 消息中心'
  ].join('\n')
})

function persistComplaintDiagnostics() {
  uni.setStorageSync(WORKER_COMPLAINT_DIAGNOSTICS_KEY, {
    selectedTypeIndex: selectedTypeIndex.value,
    complaintType: form.complaintType,
    title: form.title,
    content: form.content,
    contactMobile: form.contactMobile,
    attachments: form.attachments,
    anonymous: form.anonymous,
    syncUnion: form.syncUnion,
    complaintLastLoadedAt: complaintLastLoadedAt.value,
    complaintLastSubmittedAt: complaintLastSubmittedAt.value,
    complaintLastAttachmentAt: complaintLastAttachmentAt.value,
    complaintLastActionAt: complaintLastActionAt.value,
    complaintLastMessage: complaintLastMessage.value,
    rowCount: rows.value.length,
    latestComplaint: rows.value[0] || null
  })
}

function restoreComplaintDiagnostics() {
  const snapshot = uni.getStorageSync(WORKER_COMPLAINT_DIAGNOSTICS_KEY) || {}
  selectedTypeIndex.value = Number(snapshot.selectedTypeIndex || 0)
  form.complaintType = snapshot.complaintType || typeOptions[selectedTypeIndex.value] || typeOptions[0]
  form.title = snapshot.title || ''
  form.content = snapshot.content || ''
  form.contactMobile = snapshot.contactMobile || ''
  form.attachments = snapshot.attachments || ''
  form.anonymous = !!snapshot.anonymous
  form.syncUnion = !!snapshot.syncUnion
  complaintLastLoadedAt.value = snapshot.complaintLastLoadedAt || ''
  complaintLastSubmittedAt.value = snapshot.complaintLastSubmittedAt || ''
  complaintLastAttachmentAt.value = snapshot.complaintLastAttachmentAt || ''
  complaintLastActionAt.value = snapshot.complaintLastActionAt || ''
  complaintLastMessage.value = snapshot.complaintLastMessage || ''
}

function recordComplaintAction(action, detail) {
  complaintLastActionAt.value = new Date().toLocaleString()
  complaintLastMessage.value = detail ? `${action} / ${detail}` : action
  persistComplaintDiagnostics()
}

function normalizeAttachments(urls) {
  form.attachments = urls.filter(Boolean).join(',')
  persistComplaintDiagnostics()
}

function handleTypeChange(event) {
  selectedTypeIndex.value = Number(event.detail.value || 0)
  form.complaintType = typeOptions[selectedTypeIndex.value]
  recordComplaintAction('切换投诉类型', form.complaintType)
}

async function loadList(options = {}) {
  try {
    const data = await getComplaintList()
    rows.value = data?.rows || []
    complaintLastLoadedAt.value = new Date().toLocaleString()
    if (!options.preserveMessage) {
      complaintLastMessage.value = rows.value.length
        ? `投诉列表已加载，共 ${rows.value.length} 条记录`
        : '当前暂无投诉记录，可先整理证据或转入法律咨询'
    }
    persistComplaintDiagnostics()
  } catch (error) {
    complaintLastLoadedAt.value = new Date().toLocaleString()
    if (!options.preserveMessage) {
      complaintLastMessage.value = error.message || '加载投诉失败'
    }
    persistComplaintDiagnostics()
    uni.showToast({ title: error.message || '加载投诉失败', icon: 'none' })
  }
}

function removeAttachment(index) {
  const next = [...attachmentUrls.value]
  next.splice(index, 1)
  normalizeAttachments(next)
  recordComplaintAction('移除投诉附件', `剩余 ${attachmentUrls.value.length} 项附件`)
}

function goCamera() {
  recordComplaintAction('前往拍照归档', '待回填投诉证据附件')
  uni.navigateTo({ url: '/pages/camera/index' })
}

function goLegal() {
  recordComplaintAction('前往法律咨询', '可先梳理问题再回到投诉举报提交')
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
    complaintLastAttachmentAt.value = new Date().toLocaleString()
    recordComplaintAction('投诉证据上传成功', '已写入上传归档并回填到当前投诉单')
    uni.showToast({ title: '证据上传成功', icon: 'none' })
  } catch (error) {
    if (error?.errMsg?.includes('cancel')) {
      return
    }
    complaintLastAttachmentAt.value = new Date().toLocaleString()
    recordComplaintAction('投诉证据上传失败', error.message || '证据上传失败')
    uni.showToast({ title: error.message || '证据上传失败', icon: 'none' })
  } finally {
    uploading.value = false
  }
}

async function submitComplaint() {
  if (!form.title || !form.content) {
    complaintLastSubmittedAt.value = new Date().toLocaleString()
    complaintLastMessage.value = '投诉标题或问题经过未填写完整，前端已拦截提交'
    persistComplaintDiagnostics()
    uni.showToast({ title: '请完善投诉信息', icon: 'none' })
    return
  }
  try {
    complaintLastSubmittedAt.value = new Date().toLocaleString()
    const result = await createComplaint(form)
    const submitMessage = result?.pushTriggered
      ? '投诉提交成功，服务端已写入消息中心并触发通知'
      : '投诉提交成功，服务端已受理，可在消息中心查看'
    uni.showToast({
      title: result?.pushTriggered ? '提交成功，已写入消息中心' : '提交成功，可在消息中心查看',
      icon: 'none'
    })
    form.title = ''
    form.content = ''
    form.contactMobile = ''
    form.attachments = ''
    form.anonymous = false
    form.syncUnion = false
    await loadList({ preserveMessage: true })
    recordComplaintAction('投诉提交成功', `${submitMessage}；当前投诉记录 ${rows.value.length} 条`)
  } catch (error) {
    complaintLastSubmittedAt.value = new Date().toLocaleString()
    recordComplaintAction('投诉提交失败', error.message || '提交失败')
    uni.showToast({ title: error.message || '提交失败', icon: 'none' })
  }
}

function openDetail(item) {
  if (!item?.complaintId) {
    return
  }
  recordComplaintAction('下钻投诉详情', item.title || '-')
  uni.navigateTo({ url: `/pages/complaint/detail?complaintId=${item.complaintId}` })
}

onLoad((options) => {
  restoreComplaintDiagnostics()
  if (options?.attachments) {
    const incoming = decodeURIComponent(options.attachments)
    normalizeAttachments([...attachmentUrls.value, incoming])
    recordComplaintAction('回填投诉附件', '已从拍照归档或上传记录带入证据附件')
  }
})

onShow(loadList)

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
</script>

<style lang="scss">
.form-input,
.form-textarea,
.form-picker {
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

.form-picker__text {
  color: #16324f;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
}

.section-head--compact {
  margin-top: 22rpx;
  margin-bottom: 12rpx;
}

.worker-title--small {
  font-size: 28rpx;
}

.more-link {
  font-size: 24rpx;
  color: #1f6fd6;
}

.attachment-actions {
  margin-top: 8rpx;
}

.attachment-actions--empty {
  margin-top: 18rpx;
  display: flex;
  gap: 20rpx;
}

.attachment-actions--empty button {
  flex: 1;
}

.attachment-list {
  margin-top: 16rpx;
}

.attachment-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #edf2f7;
}

.attachment-row:last-child {
  border-bottom: none;
}

.attachment-row__text {
  flex: 1;
  font-size: 22rpx;
  line-height: 1.6;
  color: #7890aa;
  word-break: break-all;
}

.attachment-row__action {
  font-size: 24rpx;
  color: #d9480f;
  white-space: nowrap;
}

.worker-empty--inline {
  margin-top: 10rpx;
}

.switch-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 18rpx 0 24rpx;
  font-size: 28rpx;
  color: #16324f;
}

.list-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 22rpx 0;
  border-bottom: 1rpx solid #edf2f7;
}

.list-row:last-child {
  border-bottom: none;
}

.list-row__title {
  font-size: 30rpx;
  font-weight: 600;
  color: #16324f;
}

.list-row__subtitle {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #7890aa;
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
