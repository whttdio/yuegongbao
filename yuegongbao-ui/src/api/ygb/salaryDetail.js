import request from '@/utils/request'

// 查询工资明细列表
export function listSalaryDetail(query) {
  return request({
    url: '/ygb/salary/detail/list',
    method: 'get',
    params: query
  })
}

// 查询工资明细汇总
export function getSalaryDetailSummary(query) {
  return request({
    url: '/ygb/salary/detail/summary',
    method: 'get',
    params: query
  })
}

// 查询工资明细详细
export function getSalaryDetail(detailId) {
  return request({
    url: '/ygb/salary/detail/' + detailId,
    method: 'get'
  })
}

// 修改工资明细
export function updateSalaryDetail(data) {
  return request({
    url: '/ygb/salary/detail',
    method: 'put',
    data: data
  })
}

// 删除工资明细
export function delSalaryDetail(detailId) {
  return request({
    url: '/ygb/salary/detail/' + detailId,
    method: 'delete'
  })
}
