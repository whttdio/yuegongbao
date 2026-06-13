<template>
  <div
    class="login-page"
    :class="`portal-${portal.code}`"
    :style="{
      '--portal-primary': portal.themeColor,
      '--portal-surface': portal.themeSurface,
      '--portal-accent': portal.themeAccent
    }"
  >
    <div class="login-bg">
      <div class="login-bg__shape login-bg__shape--1" />
      <div class="login-bg__shape login-bg__shape--2" />
      <div class="login-bg__shape login-bg__shape--3" />
    </div>

    <div class="login-shell">
      <section class="login-side">
        <div class="login-side__inner">
          <div class="login-badge">
            <span class="login-badge__dot" />
            {{ portal.login.badge }}
          </div>
          <h1 class="login-title">{{ portal.appTitle }}</h1>
          <p class="login-summary">{{ portal.login.summary }}</p>
          <ul class="login-points">
            <li v-for="item in portal.login.features" :key="item">{{ item }}</li>
          </ul>

          <div class="login-helper">
            <div class="login-helper__header">
              <span class="login-helper__label">{{ portal.login.helperTitle }}</span>
              <router-link class="login-helper__switch" :to="alternatePortalPath">
                切换到{{ alternatePortal.shortTitle }}
              </router-link>
            </div>
            <div class="login-helper__chips">
              <span v-for="item in portal.login.helperItems" :key="item" class="login-helper__chip">{{ item }}</span>
            </div>
          </div>
        </div>
      </section>

      <section class="login-panel">
        <div class="panel-header">
          <h2>{{ portal.shortTitle }}账号登录</h2>
          <p>请输入您的系统账号信息</p>
        </div>

        <el-form ref="loginRef" :model="loginForm" :rules="loginRules" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              type="text"
              size="large"
              auto-complete="off"
              placeholder="请输入账号"
            >
              <template #prefix>
                <svg-icon icon-class="user" class="input-icon" />
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              size="large"
              show-password
              auto-complete="off"
              placeholder="请输入密码"
              @keyup.enter="handleLogin"
            >
              <template #prefix>
                <svg-icon icon-class="password" class="input-icon" />
              </template>
            </el-input>
          </el-form-item>

          <el-form-item v-if="captchaEnabled" prop="code" class="captcha-row">
            <el-input
              v-model="loginForm.code"
              size="large"
              auto-complete="off"
              placeholder="请输入验证码"
              @keyup.enter="handleLogin"
            >
              <template #prefix>
                <svg-icon icon-class="validCode" class="input-icon" />
              </template>
            </el-input>
            <div class="login-code" @click="getCode">
              <img :src="codeUrl" class="login-code-img" />
            </div>
          </el-form-item>

          <div class="login-options">
            <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
            <router-link v-if="register" class="link-type" to="/register">立即注册</router-link>
          </div>

          <el-button
            :loading="loading"
            size="large"
            type="primary"
            class="submit-btn"
            @click.prevent="handleLogin"
          >
            <span v-if="!loading">登录</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form>

        <p class="panel-tip">
          当前为{{ portal.shortTitle }}前端入口，菜单、首页、驾驶舱和统计口径将按{{ portal.navSubtitle }}加载。
        </p>
      </section>
    </div>

    <div class="login-footer">
      <span>{{ portal.footerContent }}</span>
    </div>
  </div>
</template>

<script setup>
import Cookies from 'js-cookie'
import { decrypt, encrypt } from '@/utils/jsencrypt'
import { getCodeImg } from '@/api/login'
import useUserStore from '@/store/modules/user'
import {
  getActivePortalConfig,
  getPortalConfig,
  resolvePortalHomePath,
  resolvePortalLoginPath,
  setActivePortalCode,
  syncPortalBranding
} from '@/utils/portal'

const userStore = useUserStore()
const route = useRoute()
const router = useRouter()
const { proxy } = getCurrentInstance()

const portal = computed(() => getActivePortalConfig(route))
const alternatePortal = computed(() => getPortalConfig(portal.value.code === 'ygb' ? 'azb' : 'ygb'))
const alternatePortalPath = computed(() => resolvePortalLoginPath(alternatePortal.value.code))

const loginForm = ref({
  username: 'admin',
  password: 'admin123',
  rememberMe: false,
  code: '',
  uuid: ''
})

const loginRules = {
  username: [{ required: true, trigger: 'blur', message: '请输入您的账号' }],
  password: [{ required: true, trigger: 'blur', message: '请输入您的密码' }],
  code: [{ required: true, trigger: 'change', message: '请输入验证码' }]
}

const codeUrl = ref('')
const loading = ref(false)
const captchaEnabled = ref(true)
const register = ref(false)
const redirect = ref(undefined)

watch(
  () => route.fullPath,
  () => {
    setActivePortalCode(route.params.portalCode)
    syncPortalBranding(route)
    redirect.value = route.query?.redirect
  },
  { immediate: true }
)

function handleLogin() {
  proxy.$refs.loginRef.validate(valid => {
    if (!valid) {
      return
    }

    loading.value = true
    if (loginForm.value.rememberMe) {
      Cookies.set('username', loginForm.value.username, { expires: 30 })
      Cookies.set('password', encrypt(loginForm.value.password), { expires: 30 })
      Cookies.set('rememberMe', loginForm.value.rememberMe, { expires: 30 })
    } else {
      Cookies.remove('username')
      Cookies.remove('password')
      Cookies.remove('rememberMe')
    }

    userStore.login(loginForm.value).then(() => {
      const otherQueryParams = Object.keys(route.query || {}).reduce((accumulator, key) => {
        if (key !== 'redirect') {
          accumulator[key] = route.query[key]
        }
        return accumulator
      }, {})
      return router.push({
        path: redirect.value || resolvePortalHomePath(),
        query: otherQueryParams
      })
    }).catch(() => {
      if (captchaEnabled.value) {
        getCode()
      }
    }).finally(() => {
      loading.value = false
    })
  })
}

function getCode() {
  getCodeImg().then(res => {
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled
    if (captchaEnabled.value) {
      codeUrl.value = `data:image/gif;base64,${res.img}`
      loginForm.value.uuid = res.uuid
    }
  })
}

function getCookie() {
  const username = Cookies.get('username')
  const password = Cookies.get('password')
  const rememberMe = Cookies.get('rememberMe')
  loginForm.value = {
    username: username === undefined ? loginForm.value.username : username,
    password: password === undefined ? loginForm.value.password : decrypt(password),
    rememberMe: rememberMe === undefined ? false : Boolean(rememberMe),
    code: '',
    uuid: ''
  }
}

getCode()
getCookie()
</script>

<style lang="scss" scoped>
.login-page {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 40px 24px 60px;
  overflow: hidden;
}

.login-bg {
  position: fixed;
  inset: 0;
  background:
    radial-gradient(ellipse 80% 60% at 30% 20%, color-mix(in srgb, var(--portal-primary) 12%, transparent), transparent 60%),
    radial-gradient(ellipse 60% 50% at 70% 80%, color-mix(in srgb, var(--portal-primary) 10%, transparent), transparent 60%),
    linear-gradient(180deg, #f8fafc 0%, #f0f4f9 50%, #e8eef5 100%);
  z-index: 0;
}

.login-bg__shape {
  position: absolute;
  border-radius: 999px;
  filter: blur(2px);
  opacity: 0.9;
}

.login-bg__shape--1 {
  width: 280px;
  height: 280px;
  top: -120px;
  right: 8%;
  background: color-mix(in srgb, var(--portal-primary) 16%, transparent);
}

.login-bg__shape--2 {
  width: 220px;
  height: 220px;
  bottom: -100px;
  left: 12%;
  background: color-mix(in srgb, var(--portal-primary) 12%, transparent);
}

.login-bg__shape--3 {
  width: 120px;
  height: 120px;
  top: 18%;
  left: 8%;
  background: color-mix(in srgb, var(--portal-primary) 18%, transparent);
}

.login-shell {
  position: relative;
  z-index: 1;
  width: min(1180px, 100%);
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(360px, 420px);
  border-radius: 24px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 30px 60px rgba(15, 23, 42, 0.12);
  border: 1px solid rgba(226, 232, 240, 0.9);
}

.login-side {
  background:
    linear-gradient(160deg, color-mix(in srgb, var(--portal-primary) 96%, #ffffff 4%) 0%, color-mix(in srgb, var(--portal-primary) 78%, #08142b 22%) 100%);
  color: #fff;
}

.login-side__inner {
  height: 100%;
  padding: 54px 54px 44px;
  display: flex;
  flex-direction: column;
}

.login-badge {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  width: fit-content;
  padding: 7px 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.12);
  font-size: 13px;
  letter-spacing: 0.04em;
}

.login-badge__dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #fff;
  box-shadow: 0 0 0 5px rgba(255, 255, 255, 0.12);
}

.login-title {
  margin: 28px 0 18px;
  font-size: 42px;
  line-height: 1.15;
  font-weight: 700;
}

.login-summary {
  margin: 0 0 26px;
  color: rgba(255, 255, 255, 0.82);
  font-size: 15px;
  line-height: 1.9;
  max-width: 620px;
}

.login-points {
  display: grid;
  gap: 12px;
  padding: 0;
  margin: 0;
  list-style: none;
}

.login-points li {
  position: relative;
  padding-left: 18px;
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.75;
}

.login-points li::before {
  content: '';
  position: absolute;
  left: 0;
  top: 11px;
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.92);
}

.login-helper {
  margin-top: auto;
  padding: 24px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(8px);
}

.login-helper__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.login-helper__label {
  font-size: 14px;
  font-weight: 600;
}

.login-helper__switch {
  color: #fff;
  text-decoration: none;
  font-size: 13px;
  opacity: 0.9;
}

.login-helper__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.login-helper__chip {
  display: inline-flex;
  align-items: center;
  min-height: 32px;
  padding: 0 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.14);
  font-size: 13px;
}

.login-panel {
  padding: 44px 42px 38px;
  background: #fff;
}

.panel-header {
  margin-bottom: 26px;
}

.panel-header h2 {
  margin: 0 0 8px;
  font-size: 28px;
  color: #0f172a;
}

.panel-header p,
.panel-tip {
  margin: 0;
  color: #64748b;
  line-height: 1.7;
}

.login-form :deep(.el-input__wrapper) {
  min-height: 46px;
  border-radius: 12px;
  box-shadow: 0 0 0 1px #e2e8f0 inset;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px var(--portal-primary) inset;
}

.input-icon {
  color: #94a3b8;
}

.captcha-row {
  :deep(.el-form-item__content) {
    display: grid;
    grid-template-columns: minmax(0, 1fr) 116px;
    gap: 12px;
    align-items: center;
  }
}

.login-code {
  height: 46px;
  border-radius: 12px;
  border: 1px solid #d8e1ec;
  overflow: hidden;
  cursor: pointer;
  background: #f8fafc;
}

.login-code-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.login-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 22px;
}

.link-type {
  color: var(--portal-primary);
  text-decoration: none;
  font-size: 13px;
}

.submit-btn {
  width: 100%;
  height: 48px;
  border: none;
  border-radius: 12px;
  background: linear-gradient(135deg, var(--portal-primary) 0%, color-mix(in srgb, var(--portal-primary) 72%, #08142b 28%) 100%);
}

.panel-tip {
  margin-top: 20px;
  padding-top: 18px;
  border-top: 1px solid #eef2f7;
  font-size: 13px;
}

.login-footer {
  position: relative;
  z-index: 1;
  margin-top: 22px;
  color: #64748b;
  font-size: 13px;
  text-align: center;
}

@media (max-width: 980px) {
  .login-shell {
    grid-template-columns: 1fr;
  }

  .login-side__inner,
  .login-panel {
    padding: 34px 28px;
  }

  .login-title {
    font-size: 34px;
  }
}

@media (max-width: 640px) {
  .login-page {
    padding: 20px 14px 40px;
  }

  .login-side__inner,
  .login-panel {
    padding: 26px 20px;
  }

  .login-title {
    font-size: 28px;
  }

  .captcha-row :deep(.el-form-item__content) {
    grid-template-columns: 1fr;
  }
}
</style>
