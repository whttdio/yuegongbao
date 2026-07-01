<template>
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">法律咨询</view>
      <view class="worker-subtitle">提交维权咨询、检索常见问题或拨打法律服务热线</view>
      <view class="hotline" @click="callHotline">热线：{{ hotlineText }}</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">发起咨询</view>
      </view>
      <view class="form-stack">
        <view class="form-field">
          <view class="form-field__label">咨询类型</view>
          <picker class="form-picker" :range="typeOptions" @change="handleTypeChange">
            <view class="form-picker__text">{{ typeLabel }}</view>
          </picker>
        </view>
        <view class="form-field">
          <view class="form-field__label">标题</view>
          <input v-model="form.title" class="form-input" placeholder="请输入标题" />
        </view>
        <view class="form-field">
          <view class="form-field__label">问题描述</view>
          <textarea v-model="form.content" class="form-textarea" placeholder="请描述咨询问题" />
        </view>
        <view class="form-field">
          <view class="form-field__label">联系电话</view>
          <input v-model="form.contactMobile" class="form-input" placeholder="请输入联系电话" />
        </view>
      </view>

      <view class="section-head section-head--compact">
        <view class="worker-title worker-title--small">咨询附件</view>
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
      <view v-else class="worker-empty worker-empty--inline">暂无咨询附件</view>

      <button class="worker-button" @click="submitConsult">提交咨询</button>
      <view class="shortcut-actions">
        <button class="worker-button worker-button--secondary" @click="goArticleList">法律讲座</button>
        <button class="worker-button" @click="goUnion">工会服务</button>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">常见法律问题</view>
        <view class="worker-tag">{{ faqRows.length }} 条</view>
      </view>
      <view class="form-field">
        <view class="form-field__label">关键词</view>
        <input
          v-model="faqKeyword"
          class="form-input"
          placeholder="输入关键词检索常见问题"
          @confirm="handleFaqSearch"
        />
      </view>
      <button class="worker-button worker-button--secondary worker-button--compact" @click="handleFaqSearch">
        检索 FAQ
      </button>
      <view v-if="faqRows.length">
        <view v-for="item in faqRows" :key="item.faqKey" class="list-row" @click="openFaq(item)">
          <view>
            <view class="list-row__title">{{ item.title }}</view>
            <view class="list-row__subtitle">{{ item.category || '-' }}</view>
          </view>
          <view class="worker-tag">查看</view>
        </view>
      </view>
      <view v-else class="worker-empty">暂无匹配的常见问题</view>
    </view>

    <view class="worker-card">
      <view class="worker-title">我的咨询</view>
      <view v-if="rows.length">
        <view v-for="item in rows" :key="item.consultId" class="list-row" @click="openDetail(item)">
          <view>
            <view class="list-row__title">{{ item.title }}</view>
            <view class="list-row__subtitle">{{ item.consultType || '-' }}</view>
          </view>
          <view class="worker-tag">{{ item.statusText || '-' }}</view>
        </view>
      </view>
      <view v-else class="worker-empty">暂无咨询记录</view>
    </view>
  </view>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import {
  createLegalConsult,
  createWorkerUploadRecord,
  getLegalConsultList,
  getLegalFaqList,
  getLegalHotline,
  uploadWorkerImage
} from '../../api/worker'
import { sanitizeFaqRows } from '../../utils/faq-sanitize'

const MAX_UPLOAD_SIZE = 2 * 1024 * 1024
const typeOptions = ['欠薪维权', '社保争议', '劳动合同', '工伤赔付', '其他咨询']
const selectedTypeIndex = ref(0)
const rows = ref([])
const faqRows = ref([])
const faqKeyword = ref('')
const hotline = ref({})
const uploading = ref(false)
const fallbackHotlineNumber = '12351'
const fallbackHotlineText = '工会法律服务热线 12351'
const form = reactive({
  consultType: typeOptions[0],
  title: '',
  content: '',
  contactMobile: '',
  attachments: ''
})

const typeLabel = computed(() => form.consultType || '请选择咨询类型')
const attachmentUrls = computed(() => {
  if (!form.attachments) {
    return []
  }
  return form.attachments.split(',').map((item) => item.trim()).filter(Boolean)
})

const hotlineText = computed(() => hotline.value?.displayText || fallbackHotlineText)

const hotlineNumber = computed(() => {
  const phoneNumber = String(hotline.value?.phoneNumber || '').trim()
  if (phoneNumber) {
    return phoneNumber
  }
  const displayText = String(hotline.value?.displayText || '')
  const matched = displayText.match(/1\d{4,}/)
  return matched?.[0] || fallbackHotlineNumber
})

function normalizeAttachments(urls) {
  form.attachments = urls.filter(Boolean).join(',')
}

function handleTypeChange(event) {
  selectedTypeIndex.value = Number(event.detail.value || 0)
  form.consultType = typeOptions[selectedTypeIndex.value]
}

function removeAttachment(index) {
  const next = [...attachmentUrls.value]
  next.splice(index, 1)
  normalizeAttachments(next)
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
      throw new Error('咨询附件需压缩到 2MB 以内后再上传')
    }
    const payload = await uploadWorkerImage(filePath)
    const fileUrl = payload?.url || ''
    await createWorkerUploadRecord({
      categoryCode: 'legal-consult',
      categoryName: '法律咨询附件',
      fileUrl,
      fileName: payload?.fileName || '',
      originalFilename: payload?.originalFilename || '',
      fileSize: Number(tempFile.size || 0),
      contentType: tempFile.type || 'image/*',
      sourceModule: 'legal-consult'
    })
    normalizeAttachments([...attachmentUrls.value, fileUrl])
    uni.showToast({ title: '附件已上传', icon: 'none' })
  } catch (error) {
    if (error?.errMsg?.includes('cancel')) {
      return
    }
    uni.showToast({ title: error.message || '附件上传失败', icon: 'none' })
  } finally {
    uploading.value = false
  }
}

async function loadFaqs() {
  const faqs = await getLegalFaqList(faqKeyword.value.trim())
  faqRows.value = sanitizeFaqRows(faqs?.rows || [])
}

async function loadData() {
  try {
    const [consults, phone] = await Promise.all([getLegalConsultList(), getLegalHotline()])
    rows.value = consults?.rows || []
    hotline.value = phone || {}
    await loadFaqs()
  } catch (error) {
    uni.showToast({ title: error.message || '加载咨询失败', icon: 'none' })
  }
}

async function handleFaqSearch() {
  try {
    await loadFaqs()
    if (!faqRows.value.length) {
      uni.showToast({ title: '当前暂无匹配的常见问题', icon: 'none' })
    }
  } catch (error) {
    uni.showToast({ title: error.message || 'FAQ 检索失败', icon: 'none' })
  }
}

async function submitConsult() {
  if (!form.title || !form.content) {
    uni.showToast({ title: '请完善咨询信息', icon: 'none' })
    return
  }
  try {
    const result = await createLegalConsult(form)
    uni.showToast({
      title: result?.message || '咨询已提交',
      icon: 'none'
    })
    form.title = ''
    form.content = ''
    form.contactMobile = ''
    form.attachments = ''
    await loadData()
  } catch (error) {
    uni.showToast({ title: error.message || '提交失败', icon: 'none' })
  }
}

function openDetail(item) {
  if (!item?.consultId) {
    return
  }
  uni.navigateTo({ url: `/pages/legal/detail?consultId=${item.consultId}` })
}

function openFaq(item) {
  if (!item?.faqKey) {
    return
  }
  uni.navigateTo({ url: `/pages/legal/faq-detail?faqKey=${item.faqKey}` })
}

function callHotline() {
  uni.makePhoneCall({
    phoneNumber: hotlineNumber.value
  })
}

function goArticleList() {
  uni.navigateTo({ url: '/pages/legal/article-list' })
}

function goUnion() {
  uni.navigateTo({ url: '/pages/union/index' })
}

onShow(loadData)
</script>
