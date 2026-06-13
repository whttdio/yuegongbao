import request from '@/utils/request'

export function listTaxCompare(query) {
  return request({
    url: '/ygb/tax/compare/list',
    method: 'get',
    params: query
  })
}

export function getTaxCompareSummary(query) {
  return request({
    url: '/ygb/tax/compare/summary',
    method: 'get',
    params: query
  })
}

export function syncTaxCompare(data) {
  return request({
    url: '/ygb/tax/compare/sync',
    method: 'post',
    data
  })
}

export function executeTaxCompare(data) {
  return request({
    url: '/ygb/tax/compare/execute',
    method: 'post',
    data
  })
}
