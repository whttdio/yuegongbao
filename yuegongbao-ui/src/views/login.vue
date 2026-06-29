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
    <div class="login-backdrop" aria-hidden="true">
      <span class="login-backdrop__grid" />
      <span class="login-backdrop__beam login-backdrop__beam--left" />
      <span class="login-backdrop__beam login-backdrop__beam--right" />
    </div>

    <main class="login-card">
      <section class="brand-section">
        <div class="brand-section__main">
          <div class="brand-lockup">
            <div class="brand-shield">
              <span>{{ brandProfile.mark }}</span>
            </div>
            <div>
              <h1>{{ brandProfile.name }}</h1>
              <p>{{ brandProfile.tagline }}</p>
            </div>
          </div>

          <div class="brand-slogan">
            <p v-for="line in brandProfile.slogan" :key="line">{{ line }}</p>
            <small>{{ brandProfile.subline }}</small>
          </div>

          <nav class="brand-switch" aria-label="系统入口切换">
            <span class="brand-switch__pill is-active">{{ brandProfile.name }}</span>
            <router-link class="brand-switch__pill" :to="alternatePortalPath">
              {{ alternateBrandProfile.name }}
            </router-link>
          </nav>
        </div>

        <footer class="brand-footer">
          <p>{{ brandProfile.owner }}</p>
          <span>{{ brandProfile.version }}</span>
        </footer>
      </section>

      <section class="form-section">
        <div class="form-header">
          <p>{{ portal.login.badge }}</p>
          <h2>{{ brandProfile.name }}后台登录</h2>
          <span>请输入您的系统账号信息</span>
        </div>

        <el-form ref="loginRef" :model="loginForm" :rules="loginRules" class="login-form">
          <el-form-item prop="username">
            <label class="field-label">账号</label>
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
            <label class="field-label">密码</label>
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
            <label class="field-label">验证码</label>
            <div class="captcha-field">
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
              <button class="login-code" type="button" title="刷新验证码" @click="getCode">
                <img :src="codeUrl" class="login-code-img" alt="验证码" />
              </button>
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

        <div class="role-quick">
          <span v-for="item in brandProfile.roles" :key="item">{{ item }}</span>
        </div>

        <p class="panel-tip">
          当前为{{ brandProfile.name }}前端入口，菜单、首页、驾驶舱和统计口径将按{{ portal.navSubtitle }}加载。
        </p>
      </section>
    </main>

    <div class="login-footer">
      <span>{{ brandProfile.owner }}</span>
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
const brandProfile = computed(() => getLoginBrandProfile(portal.value.code))
const alternateBrandProfile = computed(() => getLoginBrandProfile(alternatePortal.value.code))

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

function getLoginBrandProfile(code) {
  if (code === 'azb') {
    return {
      mark: '安',
      name: '安责保',
      tagline: '广东省安全生产责任险监管平台',
      slogan: ['安全治理 · 风险闭环', '设备在线 · 隐患清零'],
      subline: '保险服务 · 技术防范 · 现场安全治理',
      owner: '© 2026 广东省应急管理厅',
      version: '版本 V3.0 · 政务云安全接入',
      roles: ['应急监管', '保险机构', '企业安全员', '设备运维']
    }
  }

  return {
    mark: '粤',
    name: '粤工保',
    tagline: '广东省用工保障AI监管平台',
    slogan: ['阳光劳务 · 高危防控', '合规用工 · 扩面减损'],
    subline: '数字化 · 智能化 · 全链条用工保障监管',
    owner: '© 2026 广东省人力资源和社会保障厅',
    version: '版本 V3.0 · 政务云安全接入',
    roles: ['监管部门', '企业管理员', '项目负责人', '财务经办']
  }
}

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
  min-height: 100vh;
  padding: 40px;
  overflow: hidden;
  align-items: center;
  justify-content: center;
  color: #e6f2fb;
  background: #081728;
  font-family: Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.login-backdrop {
  position: fixed;
  inset: 0;
  z-index: 0;
  background:
    linear-gradient(120deg, rgba(8, 23, 40, 0.98) 0%, rgba(9, 36, 58, 0.96) 48%, rgba(7, 56, 65, 0.94) 100%),
    linear-gradient(180deg, rgba(255, 217, 102, 0.08), transparent 42%);
}

.login-backdrop__grid {
  position: absolute;
  inset: 0;
  opacity: 0.2;
  background-image:
    linear-gradient(rgba(137, 194, 217, 0.14) 1px, transparent 1px),
    linear-gradient(90deg, rgba(137, 194, 217, 0.14) 1px, transparent 1px);
  background-size: 56px 56px;
  mask-image: linear-gradient(90deg, transparent 0%, #000 18%, #000 82%, transparent 100%);
}

.login-backdrop__beam {
  position: absolute;
  width: 38vw;
  height: 120vh;
  top: -10vh;
  opacity: 0.16;
  transform: rotate(18deg);
  background: linear-gradient(90deg, transparent, rgba(255, 217, 102, 0.42), transparent);
}

.login-backdrop__beam--left {
  left: -18vw;
}

.login-backdrop__beam--right {
  right: -18vw;
  background: linear-gradient(90deg, transparent, rgba(44, 125, 160, 0.56), transparent);
}

.login-card {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: minmax(0, 1.16fr) minmax(360px, 0.9fr);
  width: min(1280px, 100%);
  max-height: 820px;
  min-height: 640px;
  aspect-ratio: 16 / 9;
  overflow: hidden;
  border: 1px solid rgba(137, 194, 217, 0.22);
  border-radius: 48px;
  background: linear-gradient(145deg, rgba(255, 255, 255, 0.08), rgba(255, 255, 255, 0.02));
  box-shadow:
    0 44px 88px rgba(0, 0, 0, 0.46),
    inset 0 1px 0 rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(18px);
}

.brand-section,
.form-section {
  position: relative;
  z-index: 1;
}

.brand-section {
  display: flex;
  flex-direction: column;
  padding: 56px 52px 44px 58px;
}

.brand-section__main {
  display: flex;
  min-height: 0;
  flex: 1;
  flex-direction: column;
  justify-content: flex-start;
  padding-top: 54px;
}

.brand-lockup {
  display: flex;
  gap: 18px;
  align-items: center;
}

.brand-shield {
  position: relative;
  display: grid;
  width: 66px;
  height: 76px;
  flex: 0 0 auto;
  place-items: center;
  border: 1px solid rgba(255, 217, 102, 0.42);
  border-radius: 20px 20px 28px 28px;
  color: #fff7d1;
  background: linear-gradient(145deg, #2c7da0, #174a68);
  box-shadow: 0 16px 34px rgba(0, 0, 0, 0.34);
}

.brand-shield::after {
  position: absolute;
  inset: 9px;
  content: '';
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 14px 14px 21px 21px;
}

.brand-shield span {
  font-size: 30px;
  font-weight: 800;
  line-height: 1;
}

.brand-lockup h1 {
  margin: 0;
  color: #fff;
  font-size: 44px;
  font-weight: 800;
  line-height: 1.05;
}

.brand-lockup p {
  margin: 8px 0 0;
  color: #a8c9dc;
  font-size: 15px;
}

.brand-slogan {
  width: min(520px, 100%);
  margin-top: 34px;
  padding: 20px 24px;
  border-left: 4px solid #ffd966;
  border-radius: 18px;
  background: rgba(44, 125, 160, 0.14);
}

.brand-slogan p {
  margin: 0;
  color: #ffe28b;
  font-size: 21px;
  font-weight: 700;
  line-height: 1.42;
}

.brand-slogan small {
  display: block;
  margin-top: 8px;
  color: #bdd7e8;
  font-size: 13px;
}

.brand-switch {
  display: inline-flex;
  width: fit-content;
  margin-top: 32px;
  padding: 5px;
  gap: 6px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.06);
}

.brand-switch__pill {
  display: inline-flex;
  min-height: 32px;
  align-items: center;
  justify-content: center;
  padding: 0 20px;
  border-radius: 999px;
  color: #91afc2;
  font-size: 13px;
  font-weight: 600;
  text-decoration: none;
  transition: color 0.2s ease, background 0.2s ease;
}

.brand-switch__pill:hover {
  color: #edf8ff;
}

.brand-switch__pill.is-active {
  color: #ffe28b;
  background: rgba(44, 125, 160, 0.34);
}

.brand-footer {
  padding-top: 24px;
  border-top: 1px solid rgba(137, 194, 217, 0.15);
}

.brand-footer p {
  margin: 0;
  color: #6694af;
  font-size: 12px;
}

.brand-footer span {
  display: inline-flex;
  margin-top: 8px;
  padding: 3px 14px;
  border-radius: 999px;
  color: #6f9db7;
  background: rgba(255, 255, 255, 0.05);
  font-size: 11px;
}

.form-section {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 54px 56px 44px 42px;
  border-left: 1px solid rgba(255, 255, 255, 0.05);
  background: rgba(0, 0, 0, 0.16);
  backdrop-filter: blur(10px);
}

.form-header {
  margin-bottom: 30px;
}

.form-header p {
  margin: 0 0 10px;
  color: #8fb2c7;
  font-size: 13px;
}

.form-header h2 {
  margin: 0;
  color: #fff;
  font-size: 28px;
  font-weight: 750;
}

.form-header span {
  display: block;
  margin-top: 8px;
  color: #8ba5b5;
  font-size: 14px;
}

.login-form :deep(.el-form-item) {
  display: block;
  margin-bottom: 20px;
}

.login-form :deep(.el-form-item__content) {
  display: block;
}

.field-label {
  display: block;
  margin-bottom: 7px;
  color: #cde5f5;
  font-size: 13px;
  font-weight: 600;
  line-height: 1.2;
}

.login-form :deep(.el-input__wrapper) {
  min-height: 52px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.06);
  box-shadow: 0 0 0 1px rgba(44, 125, 160, 0.28) inset;
  transition: box-shadow 0.2s ease, background 0.2s ease;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  background: rgba(255, 255, 255, 0.08);
  box-shadow: 0 0 0 1px #ffd966 inset, 0 0 0 3px rgba(255, 217, 102, 0.08);
}

.login-form :deep(.el-input__inner) {
  color: #0f172a;
  font-size: 15px;
  font-weight: 500;
}

.login-form :deep(.el-input__inner::placeholder) {
  color: #94a3b8;
}

.login-form :deep(.el-input__password) {
  color: #7da8c2;
}

.login-form :deep(input:-webkit-autofill),
.login-form :deep(input:-webkit-autofill:hover),
.login-form :deep(input:-webkit-autofill:focus) {
  -webkit-box-shadow: 0 0 0 1000px #ffffff inset !important;
  -webkit-text-fill-color: #0f172a !important;
  caret-color: #0f172a;
}

.login-form :deep(.el-form-item__error) {
  padding-top: 4px;
  color: #ffd966;
}

.input-icon {
  color: #77a7c2;
}

.captcha-field {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 132px;
  gap: 12px;
  align-items: center;
}

.login-code {
  height: 52px;
  padding: 0;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid rgba(44, 125, 160, 0.26);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.08);
}

.login-code-img {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.login-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 4px 0 24px;
}

.login-options :deep(.el-checkbox__label) {
  color: #b9d0e5;
  font-size: 13px;
}

.login-options :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  border-color: #2c7da0;
  background-color: #2c7da0;
}

.link-type {
  color: #8bbdd7;
  text-decoration: none;
  font-size: 13px;
}

.link-type:hover {
  color: #ffd966;
}

.submit-btn {
  width: 100%;
  height: 54px;
  border: none;
  border-radius: 18px;
  color: #fff;
  background: linear-gradient(145deg, #2c7da0, #1f5e7a);
  font-size: 17px;
  font-weight: 700;
  letter-spacing: 0;
  transition: box-shadow 0.2s ease, transform 0.2s ease, filter 0.2s ease;
}

.submit-btn:hover,
.submit-btn:focus {
  filter: brightness(1.06);
  box-shadow: 0 10px 26px rgba(44, 125, 160, 0.3);
  transform: translateY(-1px);
}

.role-quick {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 20px;
}

.role-quick span {
  display: inline-flex;
  min-height: 28px;
  align-items: center;
  padding: 0 13px;
  border: 1px solid rgba(44, 125, 160, 0.16);
  border-radius: 999px;
  color: #8ba5b5;
  background: rgba(255, 255, 255, 0.05);
  font-size: 11px;
}

.panel-tip {
  margin: 22px 0 0;
  padding-top: 18px;
  border-top: 1px solid rgba(137, 194, 217, 0.14);
  color: #7f9bac;
  font-size: 12px;
  line-height: 1.7;
}

.login-footer {
  position: absolute;
  z-index: 1;
  bottom: 18px;
  left: 50%;
  color: rgba(168, 201, 220, 0.58);
  font-size: 12px;
  transform: translateX(-50%);
}

.portal-azb {
  .brand-shield,
  .submit-btn {
    background: linear-gradient(145deg, #0b7f87, #075461);
  }

  .brand-switch__pill.is-active {
    background: rgba(11, 107, 120, 0.34);
  }
}

@media (max-width: 1180px) {
  .login-page {
    padding: 24px;
  }

  .login-card {
    max-height: none;
    min-height: 0;
    border-radius: 36px;
  }

  .brand-section {
    padding: 40px 34px 34px 40px;
  }

  .form-section {
    padding: 40px 40px 34px 34px;
  }

  .brand-lockup h1 {
    font-size: 36px;
  }

  .brand-slogan p {
    font-size: 18px;
  }
}

@media (max-width: 960px) {
  .login-page {
    align-items: flex-start;
    min-height: 100vh;
    overflow: auto;
  }

  .login-card {
    grid-template-columns: 1fr;
    aspect-ratio: auto;
  }

  .brand-section {
    min-height: 300px;
    padding: 30px 32px 24px;
  }

  .brand-section__main {
    justify-content: flex-start;
    padding-top: 0;
  }

  .brand-footer {
    display: none;
  }

  .form-section {
    padding: 28px 32px 32px;
    border-top: 1px solid rgba(255, 255, 255, 0.05);
    border-left: none;
  }
}

@media (max-width: 640px) {
  .login-page {
    padding: 14px;
  }

  .login-card {
    border-radius: 24px;
  }

  .brand-section {
    padding: 22px 20px 18px;
  }

  .brand-lockup {
    gap: 12px;
  }

  .brand-shield {
    width: 50px;
    height: 58px;
    border-radius: 16px 16px 22px 22px;
  }

  .brand-shield span {
    font-size: 24px;
  }

  .brand-lockup h1 {
    font-size: 28px;
  }

  .brand-lockup p,
  .form-header span {
    font-size: 12px;
  }

  .brand-slogan {
    margin-top: 18px;
    padding: 13px 16px;
  }

  .brand-slogan p {
    font-size: 15px;
  }

  .brand-slogan small {
    font-size: 11px;
  }

  .brand-switch {
    margin-top: 18px;
  }

  .brand-switch__pill {
    min-height: 28px;
    padding: 0 14px;
    font-size: 12px;
  }

  .form-section {
    padding: 22px 20px 24px;
  }

  .form-header {
    margin-bottom: 20px;
  }

  .form-header h2 {
    font-size: 22px;
  }

  .login-form :deep(.el-input__wrapper),
  .login-code {
    min-height: 48px;
    height: 48px;
  }

  .captcha-field {
    grid-template-columns: 1fr;
  }

  .login-footer {
    display: none;
  }
}
</style>
