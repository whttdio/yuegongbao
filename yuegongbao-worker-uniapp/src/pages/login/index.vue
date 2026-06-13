<template>
  <view class="worker-page login-page">
    <view class="login-hero worker-card">
      <view class="login-brand">粤工保阳光劳务</view>
      <view class="worker-subtitle">
        面向劳动者的考勤、工资、社保、个税、培训与维权服务入口
      </view>
    </view>

    <view class="worker-card login-form">
      <view class="login-tabs">
        <view
          class="login-tab"
          :class="{ 'login-tab--active': loginMode === 'password' }"
          @click="switchMode('password')"
        >
          账号登录
        </view>
        <view
          class="login-tab"
          :class="{ 'login-tab--active': loginMode === 'sms' }"
          @click="switchMode('sms')"
        >
          短信登录
        </view>
      </view>

      <template v-if="loginMode === 'password'">
        <view class="worker-title">账号登录</view>
        <input v-model="passwordForm.username" class="form-input" placeholder="请输入账号或手机号" />
        <input v-model="passwordForm.password" class="form-input" password placeholder="请输入密码" />
        <button class="worker-button" :disabled="loading" @click="handlePasswordLogin">
          {{ loading ? '登录中...' : '登录并进入首页' }}
        </button>
      </template>

      <template v-else>
        <view class="worker-title">短信验证码登录</view>
        <input v-model="smsForm.mobile" class="form-input" placeholder="请输入手机号" />
        <view class="sms-row">
          <input v-model="smsForm.code" class="form-input sms-row__input" placeholder="请输入验证码" />
          <button
            class="worker-button worker-button--secondary sms-row__button"
            :disabled="smsSending || countdown > 0"
            @click="handleSendSmsCode"
          >
            {{ countdown > 0 ? `${countdown}s后重发` : (smsSending ? '发送中...' : '获取验证码') }}
          </button>
        </view>
        <view v-if="smsTip" class="sms-tip">{{ smsTip }}</view>
        <button class="worker-button" :disabled="loading" @click="handleSmsLogin">
          {{ loading ? '登录中...' : '短信登录并进入首页' }}
        </button>
      </template>
    </view>

  </view>
</template>

<script setup>
import { computed, onUnmounted, reactive, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import {
  sendWorkerSmsCode,
  setWorkerLoginAccount,
  setWorkerLoginType,
  setWorkerToken,
  workerLogin,
  workerSmsLogin
} from '../../utils/request'
import { syncWorkerPushRegistration } from '../../utils/push'
import { consumePendingWorkerJumpTarget, getWorkerJumpDiagnostics, readPendingWorkerJumpTarget } from '../../utils/worker-jump'

const loginMode = ref('password')
const loading = ref(false)
const smsSending = ref(false)
const countdown = ref(0)
const smsTip = ref('')
const loginLastActionAt = ref('')
const loginLastMessage = ref('')
const jumpDiagnosticsSummary = ref('未消费')

const passwordForm = reactive({
  username: '',
  password: ''
})

const smsForm = reactive({
  mobile: '',
  code: ''
})

function persistWorkerLoginAccount(account) {
  setWorkerLoginAccount(account)
}

let countdownTimer = null

const pendingJumpSummaryText = computed(() => {
  const pending = readPendingWorkerJumpTarget()
  if (!pending?.path) {
    return '无'
  }
  return `${pending.path}${pending.savedAt ? ` / ${pending.savedAt}` : ''}`
})
const loginSmsSummaryText = computed(() => {
  return countdown.value > 0 ? `倒计时 ${countdown.value}s` : (smsTip.value || '未发送验证码')
})
const loginSnapshotText = computed(() => {
  return [
    '## 登录验收摘要',
    `- 当前模式：${loginMode.value === 'password' ? '账号登录' : '短信登录'}`,
    `- 最近动作：${loginLastActionAt.value || '-'}`,
    `- 短信状态：${loginSmsSummaryText.value}`,
    `- 待跳转：${pendingJumpSummaryText.value}`,
    `- 跳转链状态：${jumpDiagnosticsSummary.value}`,
    `- 说明：${loginLastMessage.value || '-'}`,
    '- 链路关联：登录 / 短信验证码 / push 注册 / 待跳转消费'
  ].join('\n')
})

function refreshJumpDiagnosticsSummary() {
  const diagnostics = getWorkerJumpDiagnostics()
  jumpDiagnosticsSummary.value = [
    diagnostics.lastConsumedResult || '未消费',
    diagnostics.lastConsumedAt || diagnostics.lastSavedAt || ''
  ]
    .filter(Boolean)
    .join(' / ') || '未消费'
}

function switchMode(mode) {
  loginMode.value = mode
  loginLastActionAt.value = new Date().toLocaleString()
  loginLastMessage.value = mode === 'password' ? '已切换到账号登录' : '已切换到短信登录'
}

function startCountdown(seconds = 60) {
  countdown.value = seconds
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
  countdownTimer = setInterval(() => {
    if (countdown.value <= 1) {
      clearInterval(countdownTimer)
      countdownTimer = null
      countdown.value = 0
      return
    }
    countdown.value -= 1
  }, 1000)
}

async function handlePasswordLogin() {
  if (!passwordForm.username || !passwordForm.password) {
    loginLastActionAt.value = new Date().toLocaleString()
    loginLastMessage.value = '账号或密码未填写完整，前端已拦截提交'
    uni.showToast({ title: '请输入账号和密码', icon: 'none' })
    return
  }
  loading.value = true
  loginLastActionAt.value = new Date().toLocaleString()
  try {
    const data = await workerLogin(passwordForm)
    setWorkerToken(data.token)
    setWorkerLoginType(data.loginType || 'worker-password')
    persistWorkerLoginAccount(passwordForm.username)
    await syncWorkerPushRegistration().catch(() => null)
    loginLastMessage.value = '账号登录成功，已写入 token 并尝试 push 注册'
    if (consumePendingWorkerJumpTarget()) {
      refreshJumpDiagnosticsSummary()
      loginLastMessage.value = '账号登录成功，已消费待跳转目标'
      return
    }
    refreshJumpDiagnosticsSummary()
    uni.switchTab({ url: '/pages/home/index' })
  } catch (error) {
    loginLastMessage.value = error.message || '登录失败'
    uni.showToast({ title: error.message || '登录失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

async function handleSendSmsCode() {
  if (!/^1\d{10}$/.test(smsForm.mobile || '')) {
    loginLastActionAt.value = new Date().toLocaleString()
    loginLastMessage.value = '手机号格式不正确，前端已拦截发送验证码'
    uni.showToast({ title: '请输入正确手机号', icon: 'none' })
    return
  }
  smsSending.value = true
  loginLastActionAt.value = new Date().toLocaleString()
  try {
    const data = await sendWorkerSmsCode({ mobile: smsForm.mobile })
    smsTip.value = data?.message || '验证码已发送'
    loginLastMessage.value = `验证码已发送至 ${smsForm.mobile}`
    uni.showToast({ title: '验证码已发送', icon: 'none' })
    startCountdown(60)
  } catch (error) {
    loginLastMessage.value = error.message || '发送失败'
    uni.showToast({ title: error.message || '发送失败', icon: 'none' })
  } finally {
    smsSending.value = false
  }
}

async function handleSmsLogin() {
  if (!/^1\d{10}$/.test(smsForm.mobile || '') || !smsForm.code) {
    loginLastActionAt.value = new Date().toLocaleString()
    loginLastMessage.value = '手机号或验证码未填写完整，前端已拦截提交'
    uni.showToast({ title: '请输入手机号和验证码', icon: 'none' })
    return
  }
  loading.value = true
  loginLastActionAt.value = new Date().toLocaleString()
  try {
    const data = await workerSmsLogin(smsForm)
    setWorkerToken(data.token)
    setWorkerLoginType(data.loginType || 'worker-sms')
    persistWorkerLoginAccount(smsForm.mobile)
    await syncWorkerPushRegistration().catch(() => null)
    loginLastMessage.value = '短信登录成功，已写入 token 并尝试 push 注册'
    if (consumePendingWorkerJumpTarget()) {
      refreshJumpDiagnosticsSummary()
      loginLastMessage.value = '短信登录成功，已消费待跳转目标'
      return
    }
    refreshJumpDiagnosticsSummary()
    uni.switchTab({ url: '/pages/home/index' })
  } catch (error) {
    loginLastMessage.value = error.message || '登录失败'
    uni.showToast({ title: error.message || '登录失败', icon: 'none' })
  } finally {
    loading.value = false
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

onLoad((options) => {
  if (options?.mode === 'sms' || options?.mode === 'password') {
    loginMode.value = options.mode
  }
  if (options?.mobile) {
    smsForm.mobile = decodeURIComponent(options.mobile)
  }
  if (options?.username) {
    passwordForm.username = decodeURIComponent(options.username)
  }
  loginLastActionAt.value = new Date().toLocaleString()
  loginLastMessage.value = '登录页已打开，可核对模式切换、短信发送和待跳转状态'
  refreshJumpDiagnosticsSummary()
})

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
})
</script>

<style lang="scss">
.login-page {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
  justify-content: center;
}

.login-hero {
  background: linear-gradient(145deg, #0f4078 0%, #1976d2 70%, #54a7f4 100%);
  color: #fff;
}

.login-brand {
  font-size: 42rpx;
  font-weight: 700;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.login-tabs {
  display: flex;
  gap: 16rpx;
}

.login-tab {
  flex: 1;
  padding: 20rpx 0;
  text-align: center;
  border-radius: 18rpx;
  background: #eef4fb;
  font-size: 28rpx;
  color: #54708e;
}

.login-tab--active {
  background: #1f6fd6;
  color: #fff;
  font-weight: 600;
}

.form-input {
  height: 84rpx;
  padding: 0 24rpx;
  border-radius: 18rpx;
  background: #f5f8fc;
  font-size: 28rpx;
  color: #16324f;
  box-sizing: border-box;
}

.sms-row {
  display: flex;
  gap: 16rpx;
  align-items: center;
}

.sms-row__input {
  flex: 1;
}

.sms-row__button {
  width: 220rpx;
  min-width: 220rpx;
  padding: 0;
}

.sms-tip {
  font-size: 24rpx;
  color: #1f6fd6;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 18rpx;
}

.section-head--sub {
  margin-top: 20rpx;
}

.worker-title--small {
  font-size: 28rpx;
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
  line-height: 1.7;
  color: #16324f;
  word-break: break-all;
}

.result-block {
  margin-top: 20rpx;
  padding: 20rpx 24rpx;
  border-radius: 18rpx;
  background: #f5f8fc;
}

.result-block__label {
  font-size: 24rpx;
  color: #5f7893;
}

.result-block__value {
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 1.8;
  color: #36506b;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
