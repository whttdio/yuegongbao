import request from '@/utils/request'

export function listSalaryBank(query) {
  return request({
    url: '/ygb/salary/bank/list',
    method: 'get',
    params: query
  })
}

export function getSalaryBankSummary(query) {
  return request({
    url: '/ygb/salary/bank/summary',
    method: 'get',
    params: query
  })
}

export function exportSalaryBank(query) {
  return request({
    url: '/ygb/salary/bank/export',
    method: 'post',
    data: query,
    responseType: 'blob'
  })
}
