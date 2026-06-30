import request from '@/utils/request'

export function listSalaryAccount(query) {
  return request({
    url: '/ygb/salary/account/list',
    method: 'get',
    params: query
  })
}

export function getSalaryAccountSummary(query) {
  return request({
    url: '/ygb/salary/account/summary',
    method: 'get',
    params: query
  })
}

export function exportSalaryAccount(query) {
  return request({
    url: '/ygb/salary/account/export',
    method: 'post',
    data: query,
    responseType: 'blob'
  })
}
