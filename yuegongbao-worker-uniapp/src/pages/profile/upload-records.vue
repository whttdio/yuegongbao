<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="worker-title">上传归档</view>
      <view class="worker-subtitle">集中查看本人上传归档记录，可按分类筛选并继续发起拍照上传。</view>
      <picker class="form-picker" :range="categoryOptions" range-key="label" @change="handleCategoryChange">
        <view class="form-picker__text">归档分类：{{ selectedCategory.label }}</view>
      </picker>
      <view class="action-row">
        <button class="worker-button" @click="goCamera">去拍照上传</button>
        <button class="worker-button worker-button--secondary" @click="loadRecords">刷新记录</button>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">归档记录</view>
        <view class="worker-tag">{{ records.length }} 条</view>
      </view>
      <view v-if="records.length">
        <view v-for="item in records" :key="item.uploadId" class="record-row">
          <view class="record-main">
            <view class="record-row__title">{{ item.categoryName || selectedCategory.label }}</view>
            <view class="record-row__subtitle">{{ item.originalFilename || item.fileName || '-' }}</view>
            <view class="record-row__subtitle">{{ item.createTime || '-' }}</view>
            <view class="record-row__subtitle">{{ item.fileUrl || '-' }}</view>
          </view>
          <view class="record-actions">
            <view class="record-action" @click="copyRecord(item)">复制地址</view>
            <view class="record-action" @click="openComplaint(item)">带入投诉</view>
          </view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">当前暂无归档记录</view>
        <view class="worker-empty__desc">可先去拍照上传现场证据，后续再带入投诉举报或法律咨询链路继续处理。</view>
        <view class="worker-empty__actions">
          <button class="worker-button worker-button--secondary" @click="goCamera">去拍照上传</button>
          <button class="worker-button" @click="goComplaint">发起投诉</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getWorkerUploadRecordList } from '../../api/worker'

const categoryOptions = [
  { code: 'complaint', label: '投诉证据' },
  { code: 'injury', label: '工伤证据' },
  { code: 'hazard', label: '隐患留痕' }
]

const selectedCategoryIndex = ref(0)
const records = ref([])
const recordsLastLoadedAt = ref('')
const recordsLastActionAt = ref('')
const recordsLastMessage = ref('')

const selectedCategory = computed(() => categoryOptions[selectedCategoryIndex.value] || categoryOptions[0])
const latestRecordSummaryText = computed(() => {
  if (!records.value.length) {
    return '暂无归档记录'
  }
  const latest = records.value[0] || {}
  return `${latest.categoryName || selectedCategory.value.label} / ${latest.originalFilename || latest.fileName || '-'} / ${latest.createTime || '-'}`
})
const recordLinkageText = computed(() => {
  if (!records.value.length) {
    return '当前无归档记录，需先从拍照上传生成归档，再核对投诉举报或法律咨询的附件回填'
  }
  return '归档记录应可从拍照上传回查，复制地址后可复用，带入投诉后应在投诉页直接看到附件'
})
const recordSnapshotText = computed(() => {
  return [
    '## 上传归档验收摘要',
    `- 最近加载：${recordsLastLoadedAt.value || '-'}`,
    `- 最近联动：${recordsLastActionAt.value || '-'}`,
    `- 当前分类：${selectedCategory.value.label}`,
    `- 记录数量：${records.value.length} 条`,
    `- 最近一条：${latestRecordSummaryText.value}`,
    '- 下钻动作：拍照上传 / 复制地址 / 带入投诉',
    `- 链路一致性：${recordLinkageText.value}`,
    `- 说明：${recordsLastMessage.value || '-'}`,
    '- 链路关联：上传归档 / 拍照上传 / 投诉举报 / 法律咨询'
  ].join('\n')
})

function recordUploadAction(action, detail) {
  recordsLastActionAt.value = new Date().toLocaleString()
  recordsLastMessage.value = detail ? `${action} / ${detail}` : action
}

function handleCategoryChange(event) {
  selectedCategoryIndex.value = Number(event.detail.value || 0)
  recordUploadAction('切换归档分类', selectedCategory.value.label)
  loadRecords()
}

async function loadRecords() {
  try {
    const data = await getWorkerUploadRecordList(selectedCategory.value.code)
    records.value = data?.rows || []
    recordsLastLoadedAt.value = new Date().toLocaleString()
    recordsLastMessage.value = records.value.length
      ? `归档记录已加载，共 ${records.value.length} 条，可继续核对拍照归档与投诉附件回填`
      : '当前分类暂无归档记录，可先去拍照上传'
  } catch (error) {
    recordsLastLoadedAt.value = new Date().toLocaleString()
    recordsLastMessage.value = error.message || '加载归档记录失败'
    uni.showToast({ title: error.message || '加载归档记录失败', icon: 'none' })
  }
}

function goCamera() {
  recordUploadAction('前往拍照上传', `分类 ${selectedCategory.value.label}`)
  uni.navigateTo({
    url: `/pages/camera/index?categoryCode=${encodeURIComponent(selectedCategory.value.code)}`
  })
}

function copyRecord(item) {
  if (!item?.fileUrl) {
    uni.showToast({ title: '当前记录无可复制地址', icon: 'none' })
    return
  }
  recordUploadAction('复制归档地址', item.originalFilename || item.fileName || '-')
  uni.setClipboardData({
    data: item.fileUrl,
    success: () => uni.showToast({ title: '已复制地址', icon: 'none' })
  })
}

function openComplaint(item) {
  if (!item?.fileUrl) {
    uni.showToast({ title: '当前记录无可带入附件', icon: 'none' })
    return
  }
  recordUploadAction('带入投诉附件', item.originalFilename || item.fileName || '-')
  uni.navigateTo({
    url: `/pages/complaint/index?attachments=${encodeURIComponent(item.fileUrl)}`
  })
}

function goComplaint() {
  recordUploadAction('前往投诉举报', '待核对投诉页附件回填与提交链路')
  uni.navigateTo({ url: '/pages/complaint/index' })
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

onShow(loadRecords)
</script>

<style lang="scss">
.worker-card + .worker-card {
  margin-top: 24rpx;
}

.form-picker {
  width: 100%;
  margin-top: 18rpx;
  padding: 20rpx 24rpx;
  border-radius: 18rpx;
  background: #f5f8fc;
  box-sizing: border-box;
}

.form-picker__text {
  font-size: 28rpx;
  color: #16324f;
}

.action-row {
  display: flex;
  gap: 20rpx;
  margin-top: 20rpx;
}

.action-row button {
  flex: 1;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 16rpx;
}

.record-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
  padding: 22rpx 0;
  border-bottom: 1rpx solid #edf2f7;
}

.record-row:last-child {
  border-bottom: none;
}

.record-main {
  flex: 1;
  min-width: 0;
}

.record-row__title {
  font-size: 28rpx;
  font-weight: 600;
  color: #16324f;
}

.record-row__subtitle {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #7890aa;
  line-height: 1.6;
  word-break: break-all;
}

.record-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12rpx;
}

.record-action {
  font-size: 24rpx;
  color: #1f6fd6;
  white-space: nowrap;
}

.worker-empty--panel {
  padding: 24rpx 0;
}

.worker-empty__title {
  font-size: 28rpx;
  font-weight: 600;
  color: #16324f;
}

.worker-empty__desc {
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: #7890aa;
}

.worker-empty__actions {
  display: flex;
  gap: 18rpx;
  margin-top: 22rpx;
}

.worker-empty__actions button {
  flex: 1;
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

.section-head--sub {
  margin-top: 20rpx;
}

.clear-action {
  font-size: 24rpx;
  color: #1f6fd6;
}

.worker-title--small {
  font-size: 28rpx;
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
</style>
