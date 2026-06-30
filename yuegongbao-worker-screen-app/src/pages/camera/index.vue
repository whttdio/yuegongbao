<template>
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">拍照上传</view>
      <view class="worker-subtitle">
        用于现场留痕、隐患上报和工伤证据采集。上传成功后可直接带入投诉举报。
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">发起上传</view>
      </view>
      <view class="form-field">
        <view class="form-field__label">上传分类</view>
        <picker class="form-picker" :range="categoryOptions" range-key="label" @change="handleCategoryChange">
          <view class="form-picker__text">{{ selectedCategory.label }}</view>
        </picker>
      </view>
      <view class="camera-actions">
        <button class="worker-button worker-button--secondary" @click="chooseImage('camera')">拍照取证</button>
        <button class="worker-button worker-button--secondary" @click="chooseImage('album')">相册选择</button>
      </view>
      <view class="camera-actions">
        <button class="worker-button" :disabled="!filePath || uploading" @click="submitUpload">
          {{ uploading ? '上传中...' : '上传图片' }}
        </button>
      </view>
    </view>

    <view v-if="filePath" class="worker-card">
      <view class="worker-title">本地预览</view>
      <image :src="filePath" class="camera-preview" mode="aspectFill" />
    </view>

    <view v-if="uploadResult.url" class="worker-card">
      <view class="worker-title">上传结果</view>
      <view class="worker-subtitle">文件地址：{{ uploadResult.url }}</view>
      <view class="worker-subtitle">上传分类：{{ selectedCategory.label }}</view>
      <view class="camera-actions">
        <button class="worker-button worker-button--secondary" @click="copyUrl">复制地址</button>
        <button class="worker-button" @click="goComplaint">带入投诉举报</button>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">上传记录</view>
        <view class="worker-tag">{{ records.length }} 条</view>
      </view>
      <view v-if="records.length">
        <view v-for="item in records" :key="item.uploadId" class="record-row">
          <view>
            <view class="record-row__title">{{ item.categoryName }}</view>
            <view class="record-row__subtitle">{{ item.originalFilename || item.fileName || item.fileUrl }}</view>
            <view class="record-row__subtitle">{{ item.createTime || '-' }}</view>
          </view>
          <view class="record-row__link" @click.stop="copyRecord(item)">复制地址</view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无上传记录</view>
        <view class="worker-empty__desc">
          可先拍照保存现场证据或隐患图片，上传后可继续带入投诉举报或后续处置链路。
        </view>
        <view class="camera-actions camera-actions--empty">
          <button class="worker-button worker-button--secondary" @click="chooseImage('camera')">去拍照上传</button>
          <button class="worker-button" @click="goComplaintList">查看投诉举报</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { createWorkerUploadRecord, getWorkerUploadRecordList, uploadWorkerImage } from '../../api/screen'

const MAX_UPLOAD_SIZE = 2 * 1024 * 1024
const CAMERA_DIAGNOSTICS_KEY = 'worker_camera_diagnostics'

const runtimePlatform = ref('')
const filePath = ref('')
const uploading = ref(false)
const records = ref([])
const fileSize = ref(0)
const contentType = ref('image/*')
const lastPickAt = ref('')
const lastPickSource = ref('')
const lastPickPermissionStatus = ref('')
const lastPickErrorCode = ref('')
const lastPickMessage = ref('')
const lastUploadAt = ref('')
const lastUploadStatus = ref('')
const lastUploadResponseCode = ref(0)
const lastUploadResponseSummary = ref('')
const lastUploadMessage = ref('')
const lastRecordLoadAt = ref('')
const lastRecordLoadStatus = ref('')
const lastRecordLoadMessage = ref('')
const lastRecordCount = ref(0)
const lastComplaintForwardAt = ref('')
const lastComplaintForwardUrl = ref('')
const lastActionAt = ref('')
const categoryOptions = [
  { code: 'complaint', label: '投诉证据' },
  { code: 'injury', label: '工伤证据' },
  { code: 'hazard', label: '隐患留痕' }
]
const selectedCategoryIndex = ref(0)
const uploadResult = reactive({
  url: '',
  originalFilename: '',
  fileName: '',
  fileSize: 0
})

const selectedCategory = computed(() => categoryOptions[selectedCategoryIndex.value] || categoryOptions[0])
const cameraPlatformText = computed(() => formatPlatform(runtimePlatform.value))
const pickSummaryText = computed(() => {
  const parts = []
  if (lastPickSource.value) {
    parts.push(lastPickSource.value)
  }
  if (lastPickAt.value) {
    parts.push(lastPickAt.value)
  }
  return parts.join(' / ') || '未取图'
})
const pickPermissionText = computed(() => {
  const parts = [formatPermissionStatus(lastPickPermissionStatus.value)]
  if (lastPickErrorCode.value) {
    parts.push(lastPickErrorCode.value)
  }
  return parts.join(' / ')
})
const fileSummaryText = computed(() => {
  if (!filePath.value && !fileSize.value) {
    return '暂无本地图片'
  }
  return [formatFileSize(fileSize.value), contentType.value || 'image/*'].filter(Boolean).join(' / ')
})
const uploadSummaryText = computed(() => {
  const parts = []
  if (lastUploadStatus.value) {
    parts.push(lastUploadStatus.value)
  }
  if (lastUploadAt.value) {
    parts.push(lastUploadAt.value)
  }
  return parts.join(' / ') || '未上传'
})
const uploadResponseText = computed(() => {
  const parts = []
  if (lastUploadResponseCode.value) {
    parts.push(`HTTP ${lastUploadResponseCode.value}`)
  }
  if (lastUploadResponseSummary.value) {
    parts.push(lastUploadResponseSummary.value)
  }
  return parts.join(' / ') || '暂无响应摘要'
})
const recordSummaryText = computed(() => {
  const parts = [`当前分类 ${records.value.length} 条`]
  if (lastRecordLoadStatus.value) {
    parts.push(lastRecordLoadStatus.value)
  }
  if (lastRecordLoadAt.value) {
    parts.push(`最近回查 ${lastRecordLoadAt.value}`)
  }
  return parts.join(' / ')
})
const complaintForwardText = computed(() => {
  if (!lastComplaintForwardAt.value && !lastComplaintForwardUrl.value) {
    return '未带入投诉'
  }
  const parts = []
  if (lastComplaintForwardAt.value) {
    parts.push(lastComplaintForwardAt.value)
  }
  if (lastComplaintForwardUrl.value) {
    parts.push('已带入附件')
  }
  return parts.join(' / ')
})
const cameraChainConsistencyText = computed(() => {
  if (uploadResult.url && records.value.length) {
    return '图片已上传并可在记录区回查，当前可继续复制地址或带入投诉举报'
  }
  if (filePath.value && !uploadResult.url) {
    return '已选本地图片，待完成上传后再核对归档回查与投诉带入'
  }
  return '需串联取图、上传、归档回查和投诉带入四步链路，并在真机核对权限与返回结构'
})
const cameraSnapshotText = computed(() => {
  return [
    '## 拍照上传联调摘要',
    `- 运行平台：${cameraPlatformText.value}`,
    `- 最近联动：${lastActionAt.value || '-'}`,
    `- 上传分类：${selectedCategory.value.label}`,
    `- 最近取图：${pickSummaryText.value}`,
    `- 权限判定：${pickPermissionText.value}`,
    `- 文件信息：${fileSummaryText.value}`,
    `- 最近上传：${uploadSummaryText.value}`,
    `- 上传响应：${uploadResponseText.value}`,
    `- 上传说明：${lastUploadMessage.value || '-'}`,
    `- 记录回查：${recordSummaryText.value}`,
    `- 回查说明：${lastRecordLoadMessage.value || '-'}`,
    `- 投诉带入：${complaintForwardText.value}`,
    `- 链路一致性：${cameraChainConsistencyText.value}`,
    `- 最近回查数量：${lastRecordCount.value} 条`,
    `- 最新文件地址：${uploadResult.url || '-'}`,
    '- 链路关联：拍照上传 / 上传归档 / 投诉举报 / 上传记录'
  ].join('\n')
})

function persistCameraDiagnostics() {
  uni.setStorageSync(CAMERA_DIAGNOSTICS_KEY, {
    runtimePlatform: runtimePlatform.value,
    selectedCategoryIndex: selectedCategoryIndex.value,
    filePath: filePath.value,
    fileSize: fileSize.value,
    contentType: contentType.value,
    lastPickAt: lastPickAt.value,
    lastPickSource: lastPickSource.value,
    lastPickPermissionStatus: lastPickPermissionStatus.value,
    lastPickErrorCode: lastPickErrorCode.value,
    lastPickMessage: lastPickMessage.value,
    lastUploadAt: lastUploadAt.value,
    lastUploadStatus: lastUploadStatus.value,
    lastUploadResponseCode: lastUploadResponseCode.value,
    lastUploadResponseSummary: lastUploadResponseSummary.value,
    lastUploadMessage: lastUploadMessage.value,
    lastRecordLoadAt: lastRecordLoadAt.value,
    lastRecordLoadStatus: lastRecordLoadStatus.value,
    lastRecordLoadMessage: lastRecordLoadMessage.value,
    lastRecordCount: lastRecordCount.value,
    lastComplaintForwardAt: lastComplaintForwardAt.value,
    lastComplaintForwardUrl: lastComplaintForwardUrl.value,
    lastActionAt: lastActionAt.value,
    uploadResult: {
      url: uploadResult.url,
      originalFilename: uploadResult.originalFilename,
      fileName: uploadResult.fileName,
      fileSize: uploadResult.fileSize
    }
  })
}

function restoreCameraDiagnostics() {
  const snapshot = uni.getStorageSync(CAMERA_DIAGNOSTICS_KEY) || {}
  runtimePlatform.value = snapshot.runtimePlatform || resolveRuntimePlatform()
  selectedCategoryIndex.value = Number(snapshot.selectedCategoryIndex || 0)
  filePath.value = snapshot.filePath || ''
  fileSize.value = Number(snapshot.fileSize || 0)
  contentType.value = snapshot.contentType || 'image/*'
  lastPickAt.value = snapshot.lastPickAt || ''
  lastPickSource.value = snapshot.lastPickSource || ''
  lastPickPermissionStatus.value = snapshot.lastPickPermissionStatus || ''
  lastPickErrorCode.value = snapshot.lastPickErrorCode || ''
  lastPickMessage.value = snapshot.lastPickMessage || ''
  lastUploadAt.value = snapshot.lastUploadAt || ''
  lastUploadStatus.value = snapshot.lastUploadStatus || ''
  lastUploadResponseCode.value = Number(snapshot.lastUploadResponseCode || 0)
  lastUploadResponseSummary.value = snapshot.lastUploadResponseSummary || ''
  lastUploadMessage.value = snapshot.lastUploadMessage || ''
  lastRecordLoadAt.value = snapshot.lastRecordLoadAt || ''
  lastRecordLoadStatus.value = snapshot.lastRecordLoadStatus || ''
  lastRecordLoadMessage.value = snapshot.lastRecordLoadMessage || ''
  lastRecordCount.value = Number(snapshot.lastRecordCount || 0)
  lastComplaintForwardAt.value = snapshot.lastComplaintForwardAt || ''
  lastComplaintForwardUrl.value = snapshot.lastComplaintForwardUrl || ''
  lastActionAt.value = snapshot.lastActionAt || ''
  uploadResult.url = snapshot.uploadResult?.url || ''
  uploadResult.originalFilename = snapshot.uploadResult?.originalFilename || ''
  uploadResult.fileName = snapshot.uploadResult?.fileName || ''
  uploadResult.fileSize = Number(snapshot.uploadResult?.fileSize || 0)
}

function recordCameraAction(action, detail) {
  lastActionAt.value = new Date().toLocaleString()
  const message = detail ? `${action} / ${detail}` : action
  lastUploadMessage.value = message
  persistCameraDiagnostics()
}

function resolveRuntimePlatform() {
  try {
    const systemInfo = uni.getSystemInfoSync()
    return systemInfo?.uniPlatform || systemInfo?.platform || ''
  } catch (error) {
    return ''
  }
}

function formatPlatform(platform) {
  const normalized = String(platform || '').toLowerCase()
  if (!normalized) {
    return '未知平台'
  }
  if (normalized === 'app-plus') {
    return 'App'
  }
  if (normalized === 'h5' || normalized === 'web') {
    return 'H5'
  }
  if (normalized === 'mp-weixin') {
    return '微信小程序'
  }
  if (normalized === 'android') {
    return 'Android'
  }
  if (normalized === 'ios') {
    return 'iOS'
  }
  return platform
}

function formatPermissionStatus(status) {
  const normalized = String(status || '').toLowerCase()
  if (!normalized) {
    return '未校验'
  }
  if (normalized === 'granted') {
    return '已授权'
  }
  if (normalized === 'denied') {
    return '权限拒绝'
  }
  if (normalized === 'cancelled') {
    return '用户取消'
  }
  if (normalized === 'failed') {
    return '待排查'
  }
  return status
}

function classifyChooseImageError(error) {
  const errMsg = String(error?.errMsg || '')
  const lowerErrMsg = errMsg.toLowerCase()
  if (lowerErrMsg.includes('cancel')) {
    return {
      permissionStatus: 'cancelled',
      errorCode: 'CHOOSE_CANCELLED',
      message: '用户取消了本次取图操作'
    }
  }
  if (
    lowerErrMsg.includes('auth deny') ||
    lowerErrMsg.includes('permission deny') ||
    lowerErrMsg.includes('no permission') ||
    lowerErrMsg.includes('authorize no response')
  ) {
    return {
      permissionStatus: 'denied',
      errorCode: 'MEDIA_PERMISSION_DENIED',
      message: '相机或相册权限被拒绝，请在系统设置中开启后重试'
    }
  }
  return {
    permissionStatus: 'failed',
    errorCode: 'MEDIA_PICK_FAILED',
    message: errMsg || '选择图片失败'
  }
}

function buildUploadResponseSummary(payload) {
  const parts = []
  if (payload?._httpStatusCode) {
    parts.push(`HTTP ${payload._httpStatusCode}`)
  }
  if (payload?.code !== undefined && payload?.code !== null) {
    parts.push(`业务码 ${payload.code}`)
  }
  if (payload?.url) {
    parts.push('已返回 url')
  }
  if (payload?.fileName || payload?.originalFilename) {
    parts.push('已返回文件名')
  }
  if (payload?._rawResponseLength) {
    parts.push(`响应 ${payload._rawResponseLength} 字符`)
  }
  return parts.join(' / ') || '结构待确认'
}

function formatFileSize(value) {
  const size = Number(value || 0)
  if (!size) {
    return '0 B'
  }
  if (size < 1024) {
    return `${size} B`
  }
  if (size < 1024 * 1024) {
    return `${(size / 1024).toFixed(1)} KB`
  }
  return `${(size / (1024 * 1024)).toFixed(2)} MB`
}

function resetUploadResult() {
  uploadResult.url = ''
  uploadResult.originalFilename = ''
  uploadResult.fileName = ''
  uploadResult.fileSize = 0
}

function handleCategoryChange(event) {
  selectedCategoryIndex.value = Number(event.detail.value || 0)
  recordCameraAction('切换上传分类', selectedCategory.value.label)
  loadRecords()
}

function chooseImage(source) {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: [source],
    success: (res) => {
      const nextFileSize = Number(res.tempFiles?.[0]?.size || 0)
      lastPickAt.value = new Date().toLocaleString()
      lastPickSource.value = source === 'camera' ? '拍照取证' : '相册选择'
      lastPickPermissionStatus.value = 'granted'
      lastPickErrorCode.value = ''
      if (nextFileSize > MAX_UPLOAD_SIZE) {
        lastPickMessage.value = `图片大小 ${formatFileSize(nextFileSize)}，超过 2MB 限制`
        recordCameraAction('取图超限', lastPickMessage.value)
        persistCameraDiagnostics()
        uni.showToast({ title: '图片需压缩到 2MB 以内后再上传', icon: 'none' })
        return
      }
      filePath.value = res.tempFilePaths?.[0] || ''
      fileSize.value = nextFileSize
      contentType.value = res.tempFiles?.[0]?.type || 'image/*'
      lastPickMessage.value = `已选择 ${lastPickSource.value} 图片，大小 ${formatFileSize(nextFileSize)}`
      resetUploadResult()
      recordCameraAction('已选择图片', lastPickMessage.value)
      persistCameraDiagnostics()
    },
    fail: (error) => {
      const diagnostics = classifyChooseImageError(error)
      lastPickAt.value = new Date().toLocaleString()
      lastPickSource.value = source === 'camera' ? '拍照取证' : '相册选择'
      lastPickPermissionStatus.value = diagnostics.permissionStatus
      lastPickErrorCode.value = diagnostics.errorCode
      lastPickMessage.value = diagnostics.message
      recordCameraAction('取图失败', diagnostics.message)
      persistCameraDiagnostics()
      if (diagnostics.permissionStatus === 'cancelled') {
        return
      }
      uni.showToast({
        title: diagnostics.permissionStatus === 'denied' ? '请开启相机或相册权限' : '选择图片失败',
        icon: 'none'
      })
    }
  })
}

async function submitUpload() {
  if (!filePath.value) {
    uni.showToast({ title: '请先选择图片', icon: 'none' })
    return
  }
  if (fileSize.value > MAX_UPLOAD_SIZE) {
    uni.showToast({ title: '图片需压缩到 2MB 以内后再上传', icon: 'none' })
    return
  }
  uploading.value = true
  try {
    const payload = await uploadWorkerImage(filePath.value)
    uploadResult.url = payload?.url || ''
    uploadResult.originalFilename = payload?.originalFilename || ''
    uploadResult.fileName = payload?.fileName || ''
    uploadResult.fileSize = fileSize.value
    lastUploadAt.value = new Date().toLocaleString()
    lastUploadResponseCode.value = Number(payload?._httpStatusCode || 0)
    lastUploadResponseSummary.value = buildUploadResponseSummary(payload)
    try {
      await createWorkerUploadRecord({
        categoryCode: selectedCategory.value.code,
        categoryName: selectedCategory.value.label,
        fileUrl: payload?.url || '',
        fileName: payload?.fileName || '',
        originalFilename: payload?.originalFilename || '',
        fileSize: fileSize.value,
        contentType: contentType.value,
        sourceModule: 'camera'
      })
      lastUploadStatus.value = '上传成功'
      lastUploadMessage.value = `已写入上传记录，文件 ${payload?.originalFilename || payload?.fileName || '-'}`
      recordCameraAction('上传并归档成功', lastUploadMessage.value)
      persistCameraDiagnostics()
      await loadRecords()
      uni.showToast({ title: '上传成功', icon: 'none' })
    } catch (archiveError) {
      lastUploadStatus.value = '归档失败'
      lastUploadMessage.value = `上传成功，但归档写入失败：${archiveError.message || '请稍后重试'}`
      recordCameraAction('上传成功但归档失败', lastUploadMessage.value)
      persistCameraDiagnostics()
      uni.showToast({ title: '归档写入失败', icon: 'none' })
    }
  } catch (error) {
    lastUploadAt.value = new Date().toLocaleString()
    lastUploadStatus.value = '上传失败'
    lastUploadResponseCode.value = Number(error?.httpStatusCode || 0)
    lastUploadResponseSummary.value = error?.responseCode ? `业务码 ${error.responseCode}` : ''
    lastUploadMessage.value = error.message || '上传失败'
    recordCameraAction('上传失败', lastUploadMessage.value)
    persistCameraDiagnostics()
    uni.showToast({ title: error.message || '上传失败', icon: 'none' })
  } finally {
    uploading.value = false
  }
}

function copyUrl() {
  if (!uploadResult.url) {
    return
  }
  recordCameraAction('复制上传地址', uploadResult.originalFilename || uploadResult.fileName || uploadResult.url)
  copyText(uploadResult.url, '已复制地址')
}

function goComplaint() {
  if (!uploadResult.url) {
    return
  }
  lastComplaintForwardAt.value = new Date().toLocaleString()
  lastComplaintForwardUrl.value = uploadResult.url
  recordCameraAction('带入投诉举报', '已透传当前上传地址到投诉页')
  persistCameraDiagnostics()
  uni.navigateTo({ url: `/pages/complaint/index?attachments=${encodeURIComponent(uploadResult.url)}` })
}

function goComplaintList() {
  recordCameraAction('前往投诉举报页', '待核对附件回填与投诉提交链路')
  uni.navigateTo({ url: '/pages/complaint/index' })
}

async function loadRecords() {
  try {
    const data = await getWorkerUploadRecordList(selectedCategory.value.code)
    records.value = data?.rows || []
    lastRecordLoadAt.value = new Date().toLocaleString()
    lastRecordCount.value = records.value.length
    lastRecordLoadStatus.value = '回查成功'
    lastRecordLoadMessage.value = records.value.length ? `已回查到 ${records.value.length} 条记录` : '当前分类暂无归档记录'
    persistCameraDiagnostics()
  } catch (error) {
    lastRecordLoadAt.value = new Date().toLocaleString()
    lastRecordLoadStatus.value = '回查失败'
    lastRecordLoadMessage.value = error.message || '加载上传记录失败'
    persistCameraDiagnostics()
    uni.showToast({ title: error.message || '加载上传记录失败', icon: 'none' })
  }
}

function copyRecord(item) {
  if (!item?.fileUrl) {
    return
  }
  recordCameraAction('复制归档记录地址', item.originalFilename || item.fileName || item.fileUrl)
  copyText(item.fileUrl, '已复制地址')
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
  restoreCameraDiagnostics()
  runtimePlatform.value = runtimePlatform.value || resolveRuntimePlatform()
  const categoryCode = options?.categoryCode
  if (!categoryCode) {
    return
  }
  const matchedIndex = categoryOptions.findIndex((item) => item.code === categoryCode)
  if (matchedIndex >= 0) {
    selectedCategoryIndex.value = matchedIndex
  }
})

onShow(loadRecords)
</script>

<style lang="scss">
.camera-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 24rpx;
}

.camera-actions button {
  flex: 1;
}

.camera-actions--empty {
  margin-top: 18rpx;
}

.camera-preview {
  width: 100%;
  height: 360rpx;
  margin-top: 20rpx;
  border-radius: 24rpx;
  background: #f4f8fa;
}

</style>
