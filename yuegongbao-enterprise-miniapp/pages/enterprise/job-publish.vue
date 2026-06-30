<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page">
    <view class="worker-card worker-hero">
      <view class="worker-title worker-title--display">岗位发布</view>
      <view class="worker-subtitle">先完成移动端岗位发布表单与状态闭环，后续再对接审核流和正式发布接口。</view>
      <view v-if="tipText" class="worker-subtitle worker-subtitle--progress">{{ tipText }}</view>
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
          <textarea v-model="form.requirement" class="form-textarea" placeholder="请输入岗位要求" />
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
    </view>
  </view>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { getJobPublishDraft, saveJobPublishDraft, submitJobPublish } from '../../api/enterprise'

const form = reactive({
  title: '',
  location: '',
  salary: '',
  requirement: ''
})

const saving = ref(false)
const submitting = ref(false)
const tip = ref('')

const tipText = computed(() => tip.value)

function notify(message) {
  uni.showToast({ title: message, icon: 'none' })
}

async function loadDraft() {
  const data = await getJobPublishDraft()
  Object.assign(form, data.form || {})
  tip.value = ''
}

function fillDemo() {
  form.title = '高空检修技工'
  form.location = '广州黄埔'
  form.salary = '7000-9000 元/月'
  form.requirement = '持高处作业证，2 年以上设备检修经验。'
}

async function handleSaveDraft() {
  saving.value = true
  try {
    const result = await saveJobPublishDraft({ ...form })
    notify(result.message || '岗位草稿已保存')
  } finally {
    saving.value = false
  }
}

async function handleSubmit() {
  if (!form.title || !form.location) {
    notify('请先填写岗位名称和工作地点')
    return
  }
  submitting.value = true
  try {
    const result = await submitJobPublish({ ...form })
    notify(result.message || '岗位已提交')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadDraft()
})
</script>
