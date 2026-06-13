import request from '@/utils/request'

export function getPortalHome(portalCode = 'ygb') {
  return request({
    url: `/open/portal/${portalCode}/home`,
    headers: { isToken: false },
    method: 'get'
  })
}

export function searchPortalContent(portalCode, keyword) {
  return request({
    url: `/open/portal/${portalCode}/search`,
    headers: { isToken: false },
    method: 'get',
    params: { keyword }
  })
}

export function listPortalJobs(portalCode, params) {
  return request({
    url: `/open/portal/${portalCode}/jobs`,
    headers: { isToken: false },
    method: 'get',
    params
  })
}

export function getPortalContentDetail(contentId) {
  return request({
    url: `/open/portal/content/${contentId}`,
    headers: { isToken: false },
    method: 'get'
  })
}

export function getPortalJobDetail(jobId) {
  return request({
    url: `/open/portal/jobs/${jobId}`,
    headers: { isToken: false },
    method: 'get'
  })
}
