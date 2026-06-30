import request from '@/utils/request'

/**
 * 便民服务 - 门户首页（包含暖新地图、培训课程、法规库、互助圈、招聘用工市场等区块）
 * @param {string} portalCode 门户编码，默认 ygb
 */
export function getCitizenServiceHome(portalCode = 'ygb') {
  return request({
    url: `/open/portal/${portalCode}/home`,
    method: 'get',
    headers: { isToken: false }
  })
}

/**
 * 便民服务 - 搜索门户内容
 * @param {string} portalCode 门户编码
 * @param {string} keyword 关键词
 */
export function searchCitizenServiceContent(portalCode = 'ygb', keyword) {
  return request({
    url: `/open/portal/${portalCode}/search`,
    method: 'get',
    headers: { isToken: false },
    params: { keyword }
  })
}

/**
 * 便民服务 - 招聘岗位列表
 * @param {string} portalCode 门户编码
 * @param {object} params 查询参数 { keyword, location, salary }
 */
export function getCitizenServiceJobs(portalCode = 'ygb', params = {}) {
  return request({
    url: `/open/portal/${portalCode}/jobs`,
    method: 'get',
    headers: { isToken: false },
    params
  })
}

export function listCitizenPortalContent(query) {
  return request({
    url: '/ygb/portal/content/list',
    method: 'get',
    params: query
  })
}

export function getCitizenPortalContent(contentId) {
  return request({
    url: `/ygb/portal/content/${contentId}`,
    method: 'get'
  })
}

export function addCitizenPortalContent(data) {
  return request({
    url: '/ygb/portal/content',
    method: 'post',
    data
  })
}

export function updateCitizenPortalContent(data) {
  return request({
    url: '/ygb/portal/content',
    method: 'put',
    data
  })
}

export function delCitizenPortalContent(contentIds) {
  return request({
    url: `/ygb/portal/content/${contentIds}`,
    method: 'delete'
  })
}

export function listCitizenComplaint(query) {
  return request({
    url: '/ygb/worker/message/manage/complaint/list',
    method: 'get',
    params: query
  })
}

export function getCitizenComplaint(complaintId) {
  return request({
    url: `/ygb/worker/message/manage/complaint/${complaintId}`,
    method: 'get'
  })
}

export function updateCitizenComplaintStatus(complaintId, data) {
  return request({
    url: `/ygb/worker/message/complaint/status/${complaintId}`,
    method: 'post',
    data
  })
}

export function listCitizenLegalConsult(query) {
  return request({
    url: '/ygb/worker/message/manage/legal-consult/list',
    method: 'get',
    params: query
  })
}

export function getCitizenLegalConsult(consultId) {
  return request({
    url: `/ygb/worker/message/manage/legal-consult/${consultId}`,
    method: 'get'
  })
}

export function updateCitizenLegalConsultStatus(consultId, data) {
  return request({
    url: `/ygb/worker/message/legal-consult/status/${consultId}`,
    method: 'post',
    data
  })
}

export function listCitizenWorkerJob(query) {
  return request({
    url: '/ygb/worker/job/list',
    method: 'get',
    params: query
  })
}

export function getCitizenWorkerJob(jobId) {
  return request({
    url: `/ygb/worker/job/${jobId}`,
    method: 'get'
  })
}

export function addCitizenWorkerJob(data) {
  return request({
    url: '/ygb/worker/job',
    method: 'post',
    data
  })
}

export function updateCitizenWorkerJob(data) {
  return request({
    url: '/ygb/worker/job',
    method: 'put',
    data
  })
}

export function delCitizenWorkerJob(jobIds) {
  return request({
    url: `/ygb/worker/job/${jobIds}`,
    method: 'delete'
  })
}
