import request from '@/utils/request'

export function listSocialBaseCompare(query) {
  return request({
    url: '/ygb/social/compare/list',
    method: 'get',
    params: query
  })
}

export function getSocialBaseCompareSummary(query) {
  return request({
    url: '/ygb/social/compare/summary',
    method: 'get',
    params: query
  })
}

export function executeSocialBaseCompare(data) {
  return request({
    url: '/ygb/social/compare/execute',
    method: 'post',
    data
  })
}
