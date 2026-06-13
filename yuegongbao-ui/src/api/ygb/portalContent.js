import request from '@/utils/request'

export function listPortalContent(query) {
  return request({
    url: '/ygb/portal/content/list',
    method: 'get',
    params: query
  })
}

export function getPortalContent(contentId) {
  return request({
    url: `/ygb/portal/content/${contentId}`,
    method: 'get'
  })
}

export function addPortalContent(data) {
  return request({
    url: '/ygb/portal/content',
    method: 'post',
    data
  })
}

export function updatePortalContent(data) {
  return request({
    url: '/ygb/portal/content',
    method: 'put',
    data
  })
}

export function delPortalContent(contentIds) {
  return request({
    url: `/ygb/portal/content/${contentIds}`,
    method: 'delete'
  })
}
