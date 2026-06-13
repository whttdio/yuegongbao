import request from '@/utils/request'

export function listFakeOutsourcing(query) {
  return request({
    url: '/ygb/special/fakeOutsourcing/list',
    method: 'get',
    params: query
  })
}

export function getFakeOutsourcingSummary(query) {
  return request({
    url: '/ygb/special/fakeOutsourcing/summary',
    method: 'get',
    params: query
  })
}

export function analyzeFakeOutsourcing(data) {
  return request({
    url: '/ygb/special/fakeOutsourcing/analyze',
    method: 'post',
    data
  })
}
