import request from '@/utils/request'

export function listExpansionCollection(query) {
  return request({
    url: '/ygb/expansion/collection/list',
    method: 'get',
    params: query
  })
}

export function getExpansionCollectionSummary(query) {
  return request({
    url: '/ygb/expansion/collection/summary',
    method: 'get',
    params: query
  })
}

export function exportExpansionCollection(query) {
  return request({
    url: '/ygb/expansion/collection/export',
    method: 'post',
    data: query,
    responseType: 'blob'
  })
}

export function handleExpansionCollection(listId, data) {
  return request({
    url: `/ygb/expansion/collection/handle/${listId}`,
    method: 'post',
    data
  })
}
