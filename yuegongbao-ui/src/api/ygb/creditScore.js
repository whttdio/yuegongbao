import request from '@/utils/request'

export function listCreditScore(query) {
  return request({
    url: '/ygb/credit/score/list',
    method: 'get',
    params: query
  })
}

export function getCreditScoreSummary(query) {
  return request({
    url: '/ygb/credit/score/summary',
    method: 'get',
    params: query
  })
}

export function getCreditScore(scoreId) {
  return request({
    url: '/ygb/credit/score/' + scoreId,
    method: 'get'
  })
}

export function getEnterpriseCreditScore(enterpriseId) {
  return request({
    url: '/ygb/credit/score/enterprise/' + enterpriseId,
    method: 'get'
  })
}

export function generateCreditScore(data) {
  return request({
    url: '/ygb/credit/score/generate',
    method: 'post',
    data
  })
}
