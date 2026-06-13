import request from '@/utils/request'

export function getYgbHomeAggregate(query) {
  return request({
    url: '/ygb/home/ygb/aggregate',
    method: 'get',
    params: query
  })
}

export function getAzbHomeAggregate(query) {
  return request({
    url: '/ygb/home/azb/aggregate',
    method: 'get',
    params: query
  })
}
