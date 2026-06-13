import request from '@/utils/request'

export function listEmploymentRatio(query) {
  return request({
    url: '/ygb/special/employmentRatio/list',
    method: 'get',
    params: query
  })
}

export function getEmploymentRatioSummary(query) {
  return request({
    url: '/ygb/special/employmentRatio/summary',
    method: 'get',
    params: query
  })
}

export function calculateEmploymentRatio(data) {
  return request({
    url: '/ygb/special/employmentRatio/calculate',
    method: 'post',
    data
  })
}
