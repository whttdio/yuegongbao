import request from '@/utils/request'

export function listSalaryArrears(query) {
  return request({
    url: '/ygb/salary/batch/arrears/list',
    method: 'get',
    params: query
  })
}

export function getSalaryArrears(batchId) {
  return request({
    url: '/ygb/salary/batch/arrears/' + batchId,
    method: 'get'
  })
}

export function handleSalaryArrears(data) {
  return request({
    url: '/ygb/salary/batch/arrears/handle',
    method: 'put',
    data
  })
}
