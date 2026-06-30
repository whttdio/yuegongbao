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
} from '../../api/screen'

const MAX_UPLOAD_SIZE = 2 * 1024 * 1024
const EXPECTED_LEGAL_CHAIN_PAGES = ['法律咨询', '咨询详情', 'FAQ详情', '法律讲座', '讲座详情', '工会服务', '消息中心']

const typeOptions = ['欠薪维权', '社保争议', '劳动合同', '工伤赔付', '其他咨询']
const selectedTypeIndex = ref(0)
const rows = ref([])
const faqRows = ref([])
const faqKeyword = ref('')
const hotline = ref({})
const uploading = ref(false)
const legalLastLoadedAt = ref('')
const legalLastAttachmentAt = ref('')
const legalLastSubmittedAt = ref('')
const legalLastFaqSearchAt = ref('')
const legalLastActionAt = ref('')
const legalLastMessage = ref('')
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
const legalChainCoverageText = computed(() => EXPECTED_LEGAL_CHAIN_PAGES.join(' / '))
const legalFormSummaryText = computed(() => {
  return [
    form.consultType || '未选类型',
    `附件 ${attachmentUrls.value.length} 个`,
    faqKeyword.value ? `FAQ 关键词 ${faqKeyword.value}` : 'FAQ 未筛词'
  ].join(' / ')
})
const legalStatusSummaryText = computed(() => {
  if (!rows.value.length) {
    return '暂无咨询记录'
  }
  const statusMap = rows.value.reduce((result, item) => {
    const key = item?.statusText || '未知状态'
    result[key] = (result[key] || 0) + 1
    return result
  }, {})
  return Object.keys(statusMap).map((key) => `${key} ${statusMap[key]} 条`).join(' / ')
})
const legalDataSummaryText = computed(() => {
  return `咨询 ${rows.value.length} 条 / FAQ ${faqRows.value.length} 条 / ${legalStatusSummaryText.value}`
})
const legalFaqConsistencyText = computed(() => {
  if (!faqRows.value.length && !faqKeyword.value) {
    return '当前未命中 FAQ，需用真实关键词验证默认 FAQ 返回'
  }
  if (!faqRows.value.length && faqKeyword.value) {
    return `关键词 ${faqKeyword.value} 暂无匹配结果`
  }
  return `${faqKeyword.value ? `关键词 ${faqKeyword.value}` : '默认 FAQ'} / 返回 ${faqRows.value.length} 条`
})
const latestConsultSummaryText = computed(() => {
  if (!rows.value.length) {
    return '暂无记录'
  }
  const latest = rows.value[0] || {}
  return `${latest.title || '-'} / ${latest.statusText || '-'} / ${latest.consultType || '-'}`
})
const legalConsultConsistencyText = computed(() => {
  if (!rows.value.length) {
    return `暂无咨询记录 / 可拨打 ${hotlineNumber.value} 或先检索 FAQ`
  }
  return `咨询 ${rows.value.length} 条 / 最近一条 ${latestConsultSummaryText.value}`
})
const legalLinkageText = computed(() => {
  if (String(legalLastMessage.value || '').includes('消息中心')) {
    return '已提示消息中心与推送联动，剩余真机送达和详情落页验收'
  }
  return 'FAQ 可下钻，讲座和工会服务可协同，提交后需验收消息中心与推送'
})
const legalSnapshotText = computed(() => {
  return [
    '## 法律咨询验收摘要',
    `- 链路核对：${legalChainCoverageText.value}`,
    `- 最近联动：${legalLastActionAt.value || '-'}`,
    `- 最近加载：${legalLastLoadedAt.value || '-'}`,
    `- 最近传附件：${legalLastAttachmentAt.value || '-'}`,
    `- 最近提交：${legalLastSubmittedAt.value || '-'}`,
    `- 最近 FAQ 检索：${legalLastFaqSearchAt.value || '-'}`,
    `- 当前表单：${legalFormSummaryText.value}`,
    `- FAQ 一致性：${legalFaqConsistencyText.value}`,
    `- 咨询与 FAQ：${legalDataSummaryText.value}`,
    `- 咨询 / 热线：${legalConsultConsistencyText.value}`,
    `- 最近一条咨询：${latestConsultSummaryText.value}`,
    `- 服务热线：${hotlineText.value}`,
    `- 下钻联动：${legalLinkageText.value}`,
    `- 说明：${legalLastMessage.value || '-'}`,
    '- 链路关联：法律咨询 / FAQ / 工会热线 / 上传归档'
  ].join('\n')
})

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
    legalLastAttachmentAt.value = new Date().toLocaleString()
    legalLastActionAt.value = legalLastAttachmentAt.value
    legalLastMessage.value = '法律咨询附件上传成功，已写入上传归档并回填到当前咨询单'
    uni.showToast({ title: '附件上传成功', icon: 'none' })
  } catch (error) {
    if (error?.errMsg?.includes('cancel')) {
      return
    }
    legalLastAttachmentAt.value = new Date().toLocaleString()
    legalLastActionAt.value = legalLastAttachmentAt.value
    legalLastMessage.value = error.message || '附件上传失败'
    uni.showToast({ title: error.message || '附件上传失败', icon: 'none' })
  } finally {
    uploading.value = false
  }
}

async function loadFaqs(options = {}) {
  const faqs = await getLegalFaqList(faqKeyword.value.trim())
  faqRows.value = faqs?.rows || []
  legalLastFaqSearchAt.value = new Date().toLocaleString()
  legalLastActionAt.value = legalLastFaqSearchAt.value
  if (!options.preserveMessage) {
    legalLastMessage.value = faqRows.value.length
      ? `FAQ 检索完成，共 ${faqRows.value.length} 条匹配结果`
      : 'FAQ 检索完成，当前无匹配结果'
  }
}

async function loadData(options = {}) {
  try {
    const [consults, phone] = await Promise.all([getLegalConsultList(), getLegalHotline()])
    rows.value = consults?.rows || []
    hotline.value = phone || {}
    legalLastLoadedAt.value = new Date().toLocaleString()
    await loadFaqs({ preserveMessage: true })
    if (!options.preserveMessage) {
      legalLastMessage.value = rows.value.length
        ? `咨询列表已加载，共 ${rows.value.length} 条记录`
        : '当前暂无咨询记录，可先用 FAQ 检索或拨打热线'
    }
  } catch (error) {
    legalLastLoadedAt.value = new Date().toLocaleString()
    if (!options.preserveMessage) {
      legalLastMessage.value = error.message || '加载咨询失败'
    }
    uni.showToast({ title: error.message || '加载咨询失败', icon: 'none' })
  }
}

async function handleFaqSearch() {
  try {
    await loadFaqs()
  } catch (error) {
    legalLastFaqSearchAt.value = new Date().toLocaleString()
    legalLastMessage.value = error.message || 'FAQ 检索失败'
    uni.showToast({ title: error.message || 'FAQ 检索失败', icon: 'none' })
  }
}

async function submitConsult() {
  if (!form.title || !form.content) {
    legalLastSubmittedAt.value = new Date().toLocaleString()
    legalLastMessage.value = '咨询标题或问题描述未填写完整，前端已拦截提交'
    uni.showToast({ title: '请完善咨询信息', icon: 'none' })
    return
  }
  try {
    legalLastSubmittedAt.value = new Date().toLocaleString()
    legalLastActionAt.value = legalLastSubmittedAt.value
    const result = await createLegalConsult(form)
    const submitMessage = result?.pushTriggered
      ? '法律咨询提交成功，服务端已写入消息中心并触发通知'
      : '法律咨询提交成功，服务端已受理，可在消息中心查看'
    uni.showToast({
      title: result?.pushTriggered ? '提交成功，已写入消息中心' : '提交成功，可在消息中心查看',
      icon: 'none'
    })
    form.title = ''
    form.content = ''
    form.contactMobile = ''
    form.attachments = ''
    await loadData({ preserveMessage: true })
    legalLastMessage.value = `${submitMessage}；当前咨询记录 ${rows.value.length} 条`
  } catch (error) {
    legalLastSubmittedAt.value = new Date().toLocaleString()
    legalLastMessage.value = error.message || '提交失败'
    uni.showToast({ title: error.message || '提交失败', icon: 'none' })
  }
}

function openDetail(item) {
  if (!item?.consultId) {
    return
  }
  legalLastActionAt.value = new Date().toLocaleString()
  legalLastMessage.value = `已打开咨询详情：${item.title || '-'}`
  uni.navigateTo({ url: `/pages/legal/detail?consultId=${item.consultId}` })
}

function openFaq(item) {
  if (!item?.faqKey) {
    return
  }
  legalLastActionAt.value = new Date().toLocaleString()
  legalLastMessage.value = `已打开 FAQ 详情：${item.title || '-'}`
  uni.navigateTo({ url: `/pages/legal/faq-detail?faqKey=${item.faqKey}` })
}

function callHotline() {
  legalLastActionAt.value = new Date().toLocaleString()
  legalLastMessage.value = `已尝试拨打服务热线：${hotlineNumber.value}`
  uni.makePhoneCall({
    phoneNumber: hotlineNumber.value
  })
}

function goArticleList() {
  legalLastActionAt.value = new Date().toLocaleString()
  legalLastMessage.value = '已前往法律公益讲座列表'
  uni.navigateTo({ url: '/pages/legal/article-list' })
}

function goUnion() {
  legalLastActionAt.value = new Date().toLocaleString()
  legalLastMessage.value = '已前往工会服务页面'
  uni.navigateTo({ url: '/pages/union/index' })
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

onShow(loadData)
</script>

