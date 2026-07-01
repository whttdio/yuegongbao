export const WORKER_API_BASE_URL_STORAGE_KEY = 'worker_api_base_url'
export const DEFAULT_BASE_URL = 'http://127.0.0.1:8080'
export const DEFAULT_BUILD_MODE = 'production'
export const WORKER_TOKEN_STORAGE_KEY = 'worker_token'
export const WORKER_LOGIN_ACCOUNT_STORAGE_KEY = 'worker_login_account'
export const WORKER_LOGIN_TYPE_STORAGE_KEY = 'worker_login_type'
export const WORKER_PROFILE_EXPORT_STORAGE_KEY = 'worker_profile_export_snapshot'

function normalizeBaseUrl(value) {
  const normalized = String(value || '').trim()
  if (!normalized) {
    return ''
  }
  return normalized.replace(/\/+$/, '')
}

function hasStorageValue(value) {
  return value !== '' && value !== undefined && value !== null
}

function normalizeStorageValue(value) {
  if (typeof value === 'string') {
    return String(value).trim()
  }
  return value
}

function encodeStorageScopePart(value) {
  return encodeURIComponent(String(value || '').trim().toLowerCase()).replace(/%/g, '_')
}

export function getBaseUrl() {
  const runtimeBaseUrl = normalizeBaseUrl(uni.getStorageSync(WORKER_API_BASE_URL_STORAGE_KEY))
  if (runtimeBaseUrl) {
    return runtimeBaseUrl
  }
  const envBaseUrl = normalizeBaseUrl(import.meta.env.VITE_WORKER_API_BASE_URL)
  if (envBaseUrl) {
    return envBaseUrl
  }
  return DEFAULT_BASE_URL
}

export function getBuildMode() {
  const mode = String(import.meta.env.MODE || '').trim()
  return mode || DEFAULT_BUILD_MODE
}

export function getWorkerStorageScope() {
  return `${getBuildMode()}::${normalizeBaseUrl(getBaseUrl()) || DEFAULT_BASE_URL}`
}

export function getWorkerScopedStorageKey(baseKey) {
  return `${baseKey}__${encodeStorageScopePart(getWorkerStorageScope())}`
}

export function getWorkerScopedStorage(baseKey, fallback = '') {
  const scopedKey = getWorkerScopedStorageKey(baseKey)
  const scopedValue = uni.getStorageSync(scopedKey)
  if (hasStorageValue(scopedValue)) {
    return scopedValue
  }
  const legacyValue = uni.getStorageSync(baseKey)
  if (hasStorageValue(legacyValue)) {
    uni.setStorageSync(scopedKey, legacyValue)
    return legacyValue
  }
  return fallback
}

export function setWorkerScopedStorage(baseKey, value) {
  const normalizedValue = normalizeStorageValue(value)
  const scopedKey = getWorkerScopedStorageKey(baseKey)
  if (!hasStorageValue(normalizedValue)) {
    uni.removeStorageSync(scopedKey)
    uni.removeStorageSync(baseKey)
    return normalizedValue
  }
  uni.setStorageSync(scopedKey, normalizedValue)
  uni.removeStorageSync(baseKey)
  return normalizedValue
}

export function removeWorkerScopedStorage(baseKey) {
  uni.removeStorageSync(getWorkerScopedStorageKey(baseKey))
  uni.removeStorageSync(baseKey)
}

export function getBaseUrlSource() {
  const runtimeBaseUrl = normalizeBaseUrl(uni.getStorageSync(WORKER_API_BASE_URL_STORAGE_KEY))
  if (runtimeBaseUrl) {
    return 'runtime'
  }
  const envBaseUrl = normalizeBaseUrl(import.meta.env.VITE_WORKER_API_BASE_URL)
  if (envBaseUrl) {
    return 'env'
  }
  return 'default'
}

export function setBaseUrl(value) {
  const normalized = normalizeBaseUrl(value)
  if (!normalized) {
    uni.removeStorageSync(WORKER_API_BASE_URL_STORAGE_KEY)
    return ''
  }
  uni.setStorageSync(WORKER_API_BASE_URL_STORAGE_KEY, normalized)
  return normalized
}

export const BASE_URL = getBaseUrl()

export function getToken() {
  return getWorkerScopedStorage(WORKER_TOKEN_STORAGE_KEY, '') || ''
}

export function setWorkerToken(token) {
  return setWorkerScopedStorage(WORKER_TOKEN_STORAGE_KEY, token)
}

export function removeWorkerToken() {
  removeWorkerScopedStorage(WORKER_TOKEN_STORAGE_KEY)
}

export function getWorkerLoginAccount() {
  return getWorkerScopedStorage(WORKER_LOGIN_ACCOUNT_STORAGE_KEY, '') || ''
}

export function setWorkerLoginAccount(account) {
  return setWorkerScopedStorage(WORKER_LOGIN_ACCOUNT_STORAGE_KEY, account)
}

export function removeWorkerLoginAccount() {
  removeWorkerScopedStorage(WORKER_LOGIN_ACCOUNT_STORAGE_KEY)
}

export function getWorkerLoginType() {
  return getWorkerScopedStorage(WORKER_LOGIN_TYPE_STORAGE_KEY, '') || ''
}

export function setWorkerLoginType(loginType) {
  return setWorkerScopedStorage(WORKER_LOGIN_TYPE_STORAGE_KEY, loginType)
}

export function removeWorkerLoginType() {
  removeWorkerScopedStorage(WORKER_LOGIN_TYPE_STORAGE_KEY)
}

export function getWorkerProfileExportSnapshot() {
  return getWorkerScopedStorage(WORKER_PROFILE_EXPORT_STORAGE_KEY, {}) || {}
}

export function setWorkerProfileExportSnapshot(snapshot) {
  return setWorkerScopedStorage(WORKER_PROFILE_EXPORT_STORAGE_KEY, snapshot)
}

export function removeWorkerProfileExportSnapshot() {
  removeWorkerScopedStorage(WORKER_PROFILE_EXPORT_STORAGE_KEY)
}

export function getWorkerSessionSnapshot() {
  return {
    token: getToken(),
    loginType: getWorkerLoginType(),
    loginAccount: getWorkerLoginAccount(),
    profileExportSnapshot: getWorkerProfileExportSnapshot()
  }
}

export function restoreWorkerSessionSnapshot(snapshot = {}) {
  if (hasStorageValue(snapshot.token)) {
    setWorkerToken(snapshot.token)
  }
  if (hasStorageValue(snapshot.loginType)) {
    setWorkerLoginType(snapshot.loginType)
  }
  if (hasStorageValue(snapshot.loginAccount)) {
    setWorkerLoginAccount(snapshot.loginAccount)
  }
  if (hasStorageValue(snapshot.profileExportSnapshot)) {
    setWorkerProfileExportSnapshot(snapshot.profileExportSnapshot)
  }
}

export function clearCurrentWorkerAuthState() {
  removeWorkerToken()
  removeWorkerLoginType()
  removeWorkerLoginAccount()
  removeWorkerProfileExportSnapshot()
}

function request(options) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${getBaseUrl()}${options.url}`,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        Authorization: getToken() ? `Bearer ${getToken()}` : '',
        'Content-Type': options.contentType || 'application/json',
        ...(options.header || {})
      },
      success: (res) => {
        const payload = res.data || {}
        if (payload.code === 200) {
          resolve(payload.data !== undefined ? payload.data : payload)
          return
        }
        if (payload.code === 401) {
          clearCurrentWorkerAuthState()
          uni.reLaunch({ url: '/pages/login/index' })
          return
        }
        reject(new Error(payload.msg || '请求失败'))
      },
      fail: (err) => reject(err)
    })
  })
}

export function enterpriseLogin(data) {
  return request({
    url: '/app/enterprise/auth/login',
    method: 'POST',
    data
  })
}

export function sendEnterpriseSmsCode(data) {
  return request({
    url: '/app/enterprise/auth/send-sms-code',
    method: 'POST',
    data
  })
}

export function enterpriseSmsLogin(data) {
  return request({
    url: '/app/enterprise/auth/sms-login',
    method: 'POST',
    data
  })
}

/** @deprecated 企业端请使用 enterpriseLogin */
export function workerLogin(data) {
  return enterpriseLogin(data)
}

/** @deprecated 企业端请使用 sendEnterpriseSmsCode */
export function sendWorkerSmsCode(data) {
  return sendEnterpriseSmsCode(data)
}

/** @deprecated 企业端请使用 enterpriseSmsLogin */
export function workerSmsLogin(data) {
  return enterpriseSmsLogin(data)
}

export default request
