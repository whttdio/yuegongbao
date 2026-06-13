import request from '@/utils/request'

export function listPreventionFund(query) {
  return request({
    url: '/ygb/preventionFund/list',
    method: 'get',
    params: query
  })
}

export function getPreventionFundSummary(query) {
  return request({
    url: '/ygb/preventionFund/summary',
    method: 'get',
    params: query
  })
}

export function getPreventionFund(fundId) {
  return request({
    url: '/ygb/preventionFund/' + fundId,
    method: 'get'
  })
}

export function updatePreventionFund(data) {
  return request({
    url: '/ygb/preventionFund',
    method: 'put',
    data
  })
}
