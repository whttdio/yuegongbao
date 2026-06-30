<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page login-page login-page--screen">
    <view class="login-hero worker-card worker-hero">
      <view class="login-hero__top">
        <view class="login-brand-mark">
          <text class="login-brand-mark__glyph">阳</text>
        </view>
        <view class="login-hero__main">
          <view class="login-platform">广东省用工保障监测平台</view>
          <view class="login-brand worker-title--display">粤工保 · 阳光劳务</view>
          <view class="worker-subtitle">
            考勤、工资、社保、个税、培训与维权，一站服务劳动者
          </view>
        </view>
      </view>
      <view class="login-feature-row">
        <view v-for="item in featureTags" :key="item" class="login-feature">{{ item }}</view>
      </view>
    </view>

    <view class="worker-card login-form-card">
      <view class="login-form-head">
        <view class="worker-title">欢迎登录</view>
        <view class="worker-caption">请使用本人账号或手机号进入平台</view>
      </view>

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

      <view v-if="loginMode === 'password'" class="login-form-body">
        <view class="form-stack">
          <view class="form-field">
            <view class="form-field__label">账号 / 手机号</view>
            <input
              v-model="passwordForm.username"
              class="form-input"
              placeholder="请输入账号或手机号"
              maxlength="32"
            />
          </view>
          <view class="form-field">
            <view class="form-field__label">登录密码</view>
            <input
              v-model="passwordForm.password"
              class="form-input"
              password
              placeholder="请输入密码"
              maxlength="32"
            />
          </view>
        </view>
        <button class="worker-button login-submit" :disabled="loading" @click="handlePasswordLogin">
          {{ loading ? '登录中...' : '立即登录' }}
        </button>
      </view>

      <view v-else class="login-form-body">
        <view class="form-stack">
          <view class="form-field">
            <view class="form-field__label">手机号</view>
            <input
              v-model="smsForm.mobile"
              class="form-input"
              type="number"
              maxlength="11"
              placeholder="请输入 11 位手机号"
            />
          </view>
          <view class="form-field">
            <view class="form-field__label">短信验证码</view>
            <view class="sms-row">
              <input
                v-model="smsForm.code"
                class="form-input sms-row__input"
                type="number"
                maxlength="6"
                placeholder="请输入验证码"
              />
              <button
                class="worker-button worker-button--secondary sms-row__button"
                :disabled="smsSending || countdown > 0"
                @click="handleSendSmsCode"
              >
                {{ countdown > 0 ? `${countdown}s` : (smsSending ? '发送中' : '获取验证码') }}
              </button>
            </view>
            <view v-if="smsTip" class="form-field__hint sms-tip">{{ smsTip }}</view>
          </view>
        </view>
        <button class="worker-button login-submit" :disabled="loading" @click="handleSmsLogin">
          {{ loading ? '登录中...' : '立即登录' }}
        </button>
      </view>

      <view class="login-footer">
        登录即表示同意
        <text class="login-footer__link" @click="goPrivacy">《隐私协议》</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { onUnmounted, reactive, ref } from 'vue'
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

const featureTags = ['考勤打卡', '工资查询', '培训维权']

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

function goPrivacy() {
  uni.navigateTo({ url: '/pages/profile/privacy' })
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
    smsTip.value = data?.message || '验证码已发送，请注意查收'
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
  loginLastMessage.value = '登录页已打开'
  refreshJumpDiagnosticsSummary()
})

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
})
</script>
