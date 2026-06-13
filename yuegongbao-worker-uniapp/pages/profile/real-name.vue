<template>
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">实名认证</view>
      <view class="worker-subtitle">{{ detail.statusHint || '请补充实名资料并上传身份证件照片。' }}</view>
      <view class="status-panel">
        <view class="status-panel__title">{{ detail.applyStatusText || detail.realNameStatusText || '未提交申请' }}</view>
        <view class="status-panel__desc">
          {{ detail.rejectReason ? `驳回原因：${detail.rejectReason}` : (detail.realNameStatusText || '完成提交后等待审核') }}
        </view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">当前账号</view>
        <view class="detail-row__value">{{ detail.userName || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">最近申请时间</view>
        <view class="detail-row__value">{{ detail.applyTime || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">最近更新时间</view>
        <view class="detail-row__value">{{ detail.lastUpdateTime || '-' }}</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="worker-title">实名资料</view>
      <view class="form-stack">
        <view class="form-field">
          <view class="form-field__label">姓名</view>
          <input
            v-model="form.personName"
            class="form-input"
            placeholder="姓名"
            :disabled="!canEditForm"
            maxlength="20"
          />
        </view>
        <view class="form-field">
          <view class="form-field__label">手机号</view>
          <input
            v-model="form.mobile"
            class="form-input"
            placeholder="手机号"
            :disabled="!canEditForm"
            maxlength="11"
            type="number"
          />
        </view>
        <view class="form-field">
          <view class="form-field__label">身份证号</view>
          <input
            v-model="form.idCard"
            class="form-input"
            placeholder="身份证号"
            :disabled="!canEditForm"
            maxlength="18"
          />
        </view>
      </view>

      <view v-if="validationMessage" class="validation-panel">
        {{ validationMessage }}
      </view>

      <view class="form-section">
        <view class="form-field">
          <view class="form-field__label">身份证人像面</view>
          <view class="upload-zone" @click="chooseImage('front')">
            <view class="upload-zone__icon">{{ uploadingFront ? '…' : '+' }}</view>
            <view class="upload-zone__title">{{ uploadingFront ? '上传中...' : '上传人像面' }}</view>
            <view class="upload-zone__desc">拍摄或选择身份证正面照片</view>
          </view>
          <image v-if="form.idCardFrontUrl" :src="form.idCardFrontUrl" class="face-block__preview" mode="aspectFill" />
        </view>

        <view class="form-field">
          <view class="form-field__label">身份证国徽面</view>
          <view class="upload-zone" @click="chooseImage('back')">
            <view class="upload-zone__icon">{{ uploadingBack ? '…' : '+' }}</view>
            <view class="upload-zone__title">{{ uploadingBack ? '上传中...' : '上传国徽面' }}</view>
            <view class="upload-zone__desc">拍摄或选择身份证反面照片</view>
          </view>
          <image v-if="form.idCardBackUrl" :src="form.idCardBackUrl" class="face-block__preview" mode="aspectFill" />
        </view>

        <view class="form-field">
          <view class="form-field__label">本人免冠照片</view>
          <view class="form-field__hint">本人照需现场拍摄，避免直接从相册选择旧图。</view>
          <view class="upload-zone" @click="chooseImage('selfie')">
            <view class="upload-zone__icon">{{ uploadingSelfie ? '…' : '+' }}</view>
            <view class="upload-zone__title">{{ uploadingSelfie ? '上传中...' : '拍摄并上传本人照' }}</view>
            <view class="upload-zone__desc">请现场拍摄本人免冠照片</view>
          </view>
          <image v-if="form.selfieUrl" :src="form.selfieUrl" class="face-block__preview" mode="aspectFill" />
        </view>
      </view>

      <button class="worker-button" :disabled="submitting || !canSubmit" @click="submitApply">
        {{ submitting ? '提交中...' : submitText }}
      </button>
    </view>

    <view class="worker-card">
      <view class="worker-title">填写提示</view>
      <view v-for="(tip, index) in detail.tips || []" :key="index" class="tip-row">{{ index + 1 }}. {{ tip }}</view>
    </view>
  </view>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getWorkerRealnameDetail, submitWorkerRealname, uploadWorkerImage } from '../../api/worker'

const detail = reactive({})
const form = reactive({
  personName: '',
  mobile: '',
  idCard: '',
  idCardFrontUrl: '',
  idCardBackUrl: '',
  selfieUrl: '',
  sourceModule: 'worker-uniapp'
})

const submitting = ref(false)
const uploadingFront = ref(false)
const uploadingBack = ref(false)
const uploadingSelfie = ref(false)
const realnameLastLoadedAt = ref('')
const realnameLastSubmittedAt = ref('')
const realnameLastActionAt = ref('')
const realnameLastMessage = ref('')
const EXPECTED_REALNAME_CHAIN_PAGES = ['实名认证', '图片上传', '提交审核', '驳回重提']

const MOBILE_PATTERN = /^1\d{10}$/
const IDCARD_PATTERN = /^(?:\d{15}|\d{17}[\dXx])$/
const NAME_PATTERN = /^[\u4e00-\u9fa5A-Za-z\s]{2,20}$/
const MAX_IMAGE_SIZE = 2 * 1024 * 1024

const isVerified = computed(() => !!detail.realNameVerified)
const isPendingApply = computed(() => String(detail.applyStatus || '') === '0')

const canEditForm = computed(() => !isVerified.value && !isPendingApply.value)

const validationMessage = computed(() => {
  if (isVerified.value) {
    return '当前账号已实名认证通过，如需修改资料请联系管理员。'
  }
  if (isPendingApply.value) {
    return '当前已有待审核的实名认证申请，请等待处理结果。'
  }
  const personName = normalizeText(form.personName)
  const mobile = normalizeText(form.mobile)
  const idCard = normalizeIdCard(form.idCard)
  if (!personName || !mobile || !idCard || !form.idCardFrontUrl || !form.idCardBackUrl || !form.selfieUrl) {
    return '请完整填写实名信息并上传身份证、本人的照片材料。'
  }
  if (!NAME_PATTERN.test(personName)) {
    return '姓名需填写 2-20 位中文或英文字符。'
  }
  if (!MOBILE_PATTERN.test(mobile)) {
    return '请输入 11 位手机号。'
  }
  if (!IDCARD_PATTERN.test(idCard)) {
    return '请输入正确的身份证号。'
  }
  return ''
})

const canSubmit = computed(() => !validationMessage.value)
const realnameChainCoverageText = computed(() => EXPECTED_REALNAME_CHAIN_PAGES.join(' / '))

const submitText = computed(() => {
  if (isVerified.value) {
    return '已实名认证'
  }
  if (detail.canResubmit) {
    return detail.applyStatus === '2' ? '重新提交认证' : '提交认证申请'
  }
  if (isPendingApply.value) {
    return '等待审核中'
  }
  return '提交认证申请'
})
const realnameStatusSummaryText = computed(() => detail.applyStatusText || detail.realNameStatusText || '未提交申请')
const realnameFormSummaryText = computed(() => {
  const completedCount = [
    normalizeText(form.personName),
    normalizeText(form.mobile),
    normalizeIdCard(form.idCard),
    form.idCardFrontUrl,
    form.idCardBackUrl,
    form.selfieUrl
  ].filter(Boolean).length
  return `${completedCount}/6 项`
})
const realnameConsistencyText = computed(() => {
  if (isVerified.value) {
    return '已实名通过 / 页面应保持只读'
  }
  if (isPendingApply.value) {
    return '待审核 / 页面应不可重复提交'
  }
  return `${detail.canResubmit ? '可重提' : '首次提交'} / ${realnameFormSummaryText.value}`
})
const realnameSnapshotText = computed(() => {
  return [
    '## 实名认证验收摘要',
    `- 链路核对：${realnameChainCoverageText.value}`,
    `- 最近联动：${realnameLastActionAt.value || '-'}`,
    `- 最近加载：${realnameLastLoadedAt.value || '-'}`,
    `- 最近提交：${realnameLastSubmittedAt.value || '-'}`,
    `- 当前状态：${realnameStatusSummaryText.value}`,
    `- 资料完整度：${realnameFormSummaryText.value}`,
    `- 可编辑可提交：${canEditForm.value ? '可编辑' : '不可编辑'} / ${canSubmit.value ? '可提交' : '不可提交'}`,
    `- 状态核对：${realnameConsistencyText.value}`,
    `- 说明：${realnameLastMessage.value || '-'}`,
    '- 链路关联：实名认证 / 图片上传 / 提交审核 / 驳回重提'
  ].join('\n')
})

function normalizeText(value) {
  return String(value || '').trim()
}

function normalizeIdCard(value) {
  return normalizeText(value).toUpperCase()
}

function syncFormFromDetail() {
  form.personName = detail.personName || ''
  form.mobile = detail.mobile || ''
  form.idCard = detail.idCard || ''
  form.idCardFrontUrl = detail.idCardFrontUrl || ''
  form.idCardBackUrl = detail.idCardBackUrl || ''
  form.selfieUrl = detail.selfieUrl || ''
}

async function loadDetail() {
  try {
    const data = await getWorkerRealnameDetail()
    Object.keys(detail).forEach((key) => delete detail[key])
    Object.assign(detail, data || {})
    syncFormFromDetail()
    realnameLastLoadedAt.value = new Date().toLocaleString()
    realnameLastMessage.value = isVerified.value
      ? '实名认证已通过，页面应保持只读'
      : isPendingApply.value
        ? '实名认证申请待审核，页面应保持不可重复提交'
        : '实名认证资料已加载，可继续填写或重新提交'
  } catch (error) {
    realnameLastLoadedAt.value = new Date().toLocaleString()
    realnameLastMessage.value = error.message || '加载实名认证信息失败'
    uni.showToast({ title: error.message || '加载实名认证信息失败', icon: 'none' })
  }
}

function chooseImageBySource(sourceType) {
  return new Promise((resolve, reject) => {
    uni.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType,
      success: resolve,
      fail: reject
    })
  })
}

async function chooseImage(type) {
  if (!canEditForm.value) {
    uni.showToast({ title: validationMessage.value || '当前不可编辑实名资料', icon: 'none' })
    return
  }
  const loadingRef = type === 'front' ? uploadingFront : type === 'back' ? uploadingBack : uploadingSelfie
  loadingRef.value = true
  try {
    const res = await chooseImageBySource(type === 'selfie' ? ['camera'] : ['camera', 'album'])
    const filePath = res?.tempFilePaths?.[0]
    const fileSize = Number(res?.tempFiles?.[0]?.size || 0)
    if (!filePath) {
      throw new Error('未选择图片')
    }
    if (fileSize > MAX_IMAGE_SIZE) {
      throw new Error('图片需压缩到 2MB 以内后再上传')
    }
    const payload = await uploadWorkerImage(filePath)
    const fileUrl = payload?.url || ''
    if (!fileUrl) {
      throw new Error('上传结果未返回图片地址')
    }
    if (type === 'front') {
      form.idCardFrontUrl = fileUrl
    } else if (type === 'back') {
      form.idCardBackUrl = fileUrl
    } else {
      form.selfieUrl = fileUrl
    }
    realnameLastActionAt.value = new Date().toLocaleString()
    realnameLastMessage.value = `已上传${type === 'front' ? '身份证人像面' : type === 'back' ? '身份证国徽面' : '本人照'}`
    uni.showToast({ title: '图片上传成功', icon: 'none' })
  } catch (error) {
    if (!String(error?.errMsg || '').includes('cancel')) {
      realnameLastActionAt.value = new Date().toLocaleString()
      realnameLastMessage.value = error.message || '图片上传失败'
      uni.showToast({ title: error.message || '图片上传失败', icon: 'none' })
    }
  } finally {
    loadingRef.value = false
  }
}

async function submitApply() {
  if (submitting.value) {
    return
  }
  if (!canSubmit.value) {
    realnameLastSubmittedAt.value = new Date().toLocaleString()
    realnameLastMessage.value = validationMessage.value || '实名认证资料未完成'
    uni.showToast({ title: validationMessage.value || '实名认证资料未完成', icon: 'none' })
    return
  }
  submitting.value = true
  try {
    realnameLastSubmittedAt.value = new Date().toLocaleString()
    realnameLastActionAt.value = realnameLastSubmittedAt.value
    const result = await submitWorkerRealname({
      ...form,
      personName: normalizeText(form.personName),
      mobile: normalizeText(form.mobile),
      idCard: normalizeIdCard(form.idCard)
    })
    realnameLastMessage.value = result?.statusHint || '实名认证申请已提交'
    uni.showToast({ title: result?.statusHint || '实名认证申请已提交', icon: 'none' })
    await loadDetail()
  } catch (error) {
    realnameLastSubmittedAt.value = new Date().toLocaleString()
    realnameLastMessage.value = error.message || '实名认证提交失败'
    uni.showToast({ title: error.message || '实名认证提交失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
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

onShow(loadDetail)
</script>

<style lang="scss">
.status-panel {
  margin-top: 18rpx;
  padding: 22rpx 24rpx;
  border-radius: 20rpx;
  background: linear-gradient(135deg, #e6f2ef 0%, #f8fbff 100%);
}

.status-panel__title {
  font-size: 30rpx;
  font-weight: 700;
  color: #122d42;
}

.status-panel__desc {
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: #536b7d;
}

.validation-panel {
  margin-top: 18rpx;
  padding: 18rpx 20rpx;
  border-radius: 18rpx;
  background: #fff6e8;
  color: #9a5b00;
  font-size: 24rpx;
  line-height: 1.6;
}

.tip-row {
  margin-top: 18rpx;
  font-size: 26rpx;
  line-height: 1.7;
  color: #183247;
}
</style>
