import axios from 'axios'
import { ElLoading, ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import { saveAs } from 'file-saver'
import cache from '@/plugins/cache'
import { getToken } from '@/utils/auth'
import errorCode from '@/utils/errorCode'
import { blobValidate, tansParams } from '@/utils/yuegongbao'
import useUserStore from '@/store/modules/user'
import { getPortalRequestHeaders, resolvePortalLoginPath } from '@/utils/portal'

let downloadLoadingInstance
export let isRelogin = { show: false }

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'

const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 10000
})

service.interceptors.request.use(
  config => {
    const headers = config.headers || {}
    const isToken = headers.isToken === false
    const isRepeatSubmit = headers.repeatSubmit === false
    const interval = headers.interval || 1000

    if (getToken() && !isToken) {
      headers.Authorization = `Bearer ${getToken()}`
    }

    Object.assign(headers, getPortalRequestHeaders())
    config.headers = headers

    if (config.method === 'get' && config.params) {
      let url = config.url + '?' + tansParams(config.params)
      url = url.slice(0, -1)
      config.params = {}
      config.url = url
    }

    if (!isRepeatSubmit && (config.method === 'post' || config.method === 'put')) {
      const requestObj = {
        url: config.url,
        data: typeof config.data === 'object' ? JSON.stringify(config.data) : config.data,
        time: Date.now()
      }
      const requestSize = Object.keys(JSON.stringify(requestObj)).length
      const limitSize = 5 * 1024 * 1024
      if (requestSize >= limitSize) {
        console.warn(`[${config.url}]: 请求数据大小超出 5M 限制，跳过重复提交校验。`)
        return config
      }

      const sessionObj = cache.session.getJSON('sessionObj')
      if (!sessionObj) {
        cache.session.setJSON('sessionObj', requestObj)
      } else {
        const samePayload = sessionObj.data === requestObj.data
        const sameUrl = sessionObj.url === requestObj.url
        const withinInterval = requestObj.time - sessionObj.time < interval
        if (samePayload && sameUrl && withinInterval) {
          const message = '数据正在处理中，请勿重复提交。'
          console.warn(`[${sessionObj.url}]: ${message}`)
          return Promise.reject(new Error(message))
        }
        cache.session.setJSON('sessionObj', requestObj)
      }
    }

    return config
  },
  error => Promise.reject(error)
)

service.interceptors.response.use(
  res => {
    const code = res.data.code || 200
    const msg = errorCode[code] || res.data.msg || errorCode.default
    const isHtmlResponse = typeof res.data === 'string' && /<!DOCTYPE html>/i.test(res.data)
    if (isHtmlResponse) {
      const proxyHint = '接口返回了页面内容，请确认已启动后端服务，并使用 npm run dev:ygb 开发或配置 /prod-api 代理。'
      ElMessage({ message: proxyHint, type: 'error', duration: 5 * 1000 })
      return Promise.reject(new Error(proxyHint))
    }

    if (res.request.responseType === 'blob' || res.request.responseType === 'arraybuffer') {
      return res.data
    }

    if (code === 401) {
      if (!isRelogin.show) {
        isRelogin.show = true
        ElMessageBox.confirm('登录状态已过期，您可以留在当前页面，或重新登录。', '系统提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          isRelogin.show = false
          useUserStore().logOut().then(() => {
            location.href = resolvePortalLoginPath()
          })
        }).catch(() => {
          isRelogin.show = false
        })
      }
      return Promise.reject('登录状态已失效，请重新登录。')
    }

    if (code === 500) {
      ElMessage({ message: msg, type: 'error' })
      return Promise.reject(new Error(msg))
    }

    if (code === 601) {
      ElMessage({ message: msg, type: 'warning' })
      return Promise.reject(new Error(msg))
    }

    if (code !== 200) {
      const notice = msg || errorCode.default
      ElNotification.error({ title: notice })
      return Promise.reject(new Error(notice))
    }

    return Promise.resolve(res.data)
  },
  error => {
    let { message } = error
    if (message === 'Network Error') {
      message = '后端接口连接异常'
    } else if (message.includes('timeout')) {
      message = '系统接口请求超时'
    } else if (message.includes('Request failed with status code')) {
      message = `系统接口 ${message.slice(-3)} 异常`
    }
    ElMessage({ message, type: 'error', duration: 5 * 1000 })
    return Promise.reject(error)
  }
)

export function download(url, params, filename, config) {
  downloadLoadingInstance = ElLoading.service({
    text: '正在下载数据，请稍候',
    background: 'rgba(0, 0, 0, 0.7)'
  })

  return service.post(url, params, {
    transformRequest: [requestParams => tansParams(requestParams)],
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    responseType: 'blob',
    ...config
  }).then(async data => {
    const isBlob = blobValidate(data)
    if (isBlob) {
      saveAs(new Blob([data]), filename)
    } else {
      const resText = await data.text()
      const rspObj = JSON.parse(resText)
      const errMsg = errorCode[rspObj.code] || rspObj.msg || errorCode.default
      ElMessage.error(errMsg)
    }
    downloadLoadingInstance.close()
  }).catch(error => {
    console.error(error)
    ElMessage.error('下载文件出现错误，请联系管理员。')
    downloadLoadingInstance.close()
  })
}

export default service
