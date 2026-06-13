import request from '@/utils/request'

export function listUninsuredList(query) {
  return request({
    url: '/ygb/expansion/uninsured/list',
    method: 'get',
    params: query
  })
}

export function getUninsuredListSummary(query) {
  return request({
    url: '/ygb/expansion/uninsured/summary',
    method: 'get',
    params: query
  })
}

export function generateUninsuredList(data) {
  return request({
    url: '/ygb/expansion/uninsured/generate',
    method: 'post',
    data
  })
}

export function handleUninsured(listId, data) {
  return request({
    url: '/ygb/expansion/uninsured/handle/' + listId,
    method: 'post',
    data
  })
}
