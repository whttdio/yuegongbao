<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">上传归档</view>
      <view class="worker-subtitle">集中查看本人上传归档记录，可按分类筛选并继续发起拍照上传。</view>
      <view class="hero-stat-grid">
        <view class="hero-stat">
          <view class="hero-stat__value">{{ records.length }}</view>
          <view class="hero-stat__label">归档条数</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ selectedCategory.label }}</view>
          <view class="hero-stat__label">当前分类</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ categoryOptions.length }}</view>
          <view class="hero-stat__label">可选分类</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">筛选与操作</view>
      </view>
      <view class="form-field">
        <view class="form-field__label">归档分类</view>
        <picker class="form-picker" :range="categoryOptions" range-key="label" @change="handleCategoryChange">
          <view class="form-picker__text">{{ selectedCategory.label }}</view>
        </picker>
      </view>
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
          <view>
            <view class="record-row__title">{{ item.categoryName || selectedCategory.label }}</view>
            <view class="record-row__subtitle">{{ item.originalFilename || item.fileName || '-' }}</view>
            <view class="record-row__subtitle">{{ item.createTime || '-' }}</view>
            <view class="record-row__subtitle">{{ item.fileUrl || '-' }}</view>
          </view>
          <view class="record-row__actions">
            <view class="record-row__link" @click="copyRecord(item)">复制地址</view>
            <view class="record-row__link" @click="openComplaint(item)">带入投诉</view>
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
import { getWorkerUploadRecordList } from '../../api/enterprise-service'

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

.action-row {
  display: flex;
  gap: 20rpx;
  margin-top: 20rpx;
}

.action-row button {
  flex: 1;
}

.record-row__actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12rpx;
  flex-shrink: 0;
}

.worker-empty__actions button {
  flex: 1;
}
</style>
