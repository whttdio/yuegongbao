<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page enterprise-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">岗位发布</view>
      <view class="worker-subtitle">岗位草稿和提交都走真实入库。前端补齐校验、结果反馈和最近草稿提示。</view>
    </view>

    <view class="worker-card">
      <view class="enterprise-notice">
        <view class="enterprise-notice__head">
          <view class="enterprise-notice__title">发布说明</view>
          <view class="worker-tag worker-tag--success">真实入库</view>
        </view>
        <view class="enterprise-notice__desc">当前岗位草稿读取、保存和提交流程都直接接真实 `WorkerJobPost` 数据。</view>
        <view class="enterprise-notice__reason">本页只保留企业招聘语义，不再出现简历投递、员工本人求职等 worker 个人表达。</view>
      </view>
      <view class="enterprise-form-tip">最近草稿会自动回填；标题、地点、薪资和要求会在提交前做最小校验。</view>
    </view>

    <view class="worker-card">
      <view class="form-stack">
        <view class="form-field">
          <view class="form-field__label">岗位名称</view>
          <input v-model="form.title" class="form-input" placeholder="请输入岗位名称" />
        </view>
        <view class="form-field">
          <view class="form-field__label">工作地点</view>
          <input v-model="form.location" class="form-input" placeholder="请输入工作地点" />
        </view>
        <view class="form-field">
          <view class="form-field__label">薪资范围</view>
          <input v-model="form.salary" class="form-input" placeholder="例如 7000-9000 元/月" />
        </view>
        <view class="form-field">
          <view class="form-field__label">岗位要求</view>
          <textarea v-model="form.requirement" class="form-textarea" placeholder="请输入岗位要求、证件要求和班组安排" />
        </view>
      </view>
      <view class="worker-button-row">
        <button class="worker-button" :disabled="submitting" @click="handleSubmit">
          {{ submitting ? '提交中...' : '提交岗位' }}
        </button>
        <button class="worker-button worker-button--secondary" :disabled="saving" @click="handleSaveDraft">
          {{ saving ? '保存中...' : '保存草稿' }}
        </button>
      </view>
      <view class="worker-button-row">
        <button class="worker-button worker-button--ghost" @click="fillDemo">填充示例</button>
      </view>
      <view v-if="lastAction.message" class="enterprise-action-feedback">
        <view class="enterprise-action-feedback__title">{{ lastAction.title }}</view>
        <view class="enterprise-action-feedback__desc">{{ lastAction.message }}</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { getJobPublishDraft, saveJobPublishDraft, submitJobPublish } from '../../api/enterprise'

const form = reactive({
  title: '',
  location: '',
  salary: '',
  requirement: ''
})

const saving = ref(false)
const submitting = ref(false)
const lastAction = ref({ title: '', message: '' })

function notify(message) {
  uni.showToast({ title: message, icon: 'none' })
}

async function loadDraft() {
  const data = await getJobPublishDraft()
  Object.assign(form, data.form || {})
}

function fillDemo() {
  form.title = '高空检修技工'
  form.location = '广州黄埔'
  form.salary = '7000-9000 元/月'
  form.requirement = '持高处作业证，2 年以上设备检修经验，能配合班组轮班。'
}

function validateForm() {
  if (!form.title.trim()) {
    return '请先填写岗位名称'
  }
  if (!form.location.trim()) {
    return '请先填写工作地点'
  }
  if (!form.salary.trim()) {
    return '请先填写薪资范围'
  }
  if (!form.requirement.trim()) {
    return '请先填写岗位要求'
  }
  return ''
}

async function handleSaveDraft() {
  const errorText = validateForm()
  if (errorText) {
    notify(errorText)
    return
  }
  saving.value = true
  try {
    const result = await saveJobPublishDraft({ ...form })
    lastAction.value = {
      title: '岗位草稿已保存',
      message: result.message || `草稿已入库：${form.title}`
    }
    notify(result.message || '岗位草稿已保存')
  } finally {
    saving.value = false
  }
}

async function handleSubmit() {
  const errorText = validateForm()
  if (errorText) {
    notify(errorText)
    return
  }
  submitting.value = true
  try {
    const result = await submitJobPublish({ ...form })
    lastAction.value = {
      title: '岗位发布已提交',
      message: result.message || `岗位已提交：${form.title}`
    }
    notify(result.message || '岗位已提交')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadDraft().catch((error) => {
    notify(error.message || '加载岗位草稿失败')
  })
})
</script>
