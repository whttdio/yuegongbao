import request from '@/utils/request'

// 查询工资批次列表
export function listSalaryBatch(query) {
  return request({
    url: '/ygb/salary/batch/list',
    method: 'get',
    params: query
  })
}

// 查询工资批次汇总
export function getSalaryBatchSummary(query) {
  return request({
    url: '/ygb/salary/batch/summary',
    method: 'get',
    params: query
  })
}

// 查询工资批次下拉
export function optionselectSalaryBatch() {
  return request({
    url: '/ygb/salary/batch/optionselect',
    method: 'get'
  })
}

// 查询工资批次详细
export function getSalaryBatch(batchId) {
  return request({
    url: '/ygb/salary/batch/' + batchId,
    method: 'get'
  })
}

// 新增工资批次
export function addSalaryBatch(data) {
  return request({
    url: '/ygb/salary/batch',
    method: 'post',
    data: data
  })
}

// 修改工资批次
export function updateSalaryBatch(data) {
  return request({
    url: '/ygb/salary/batch',
    method: 'put',
    data: data
  })
}

// 删除工资批次
export function delSalaryBatch(batchId) {
  return request({
    url: '/ygb/salary/batch/' + batchId,
    method: 'delete'
  })
}

// 生成工资明细
export function generateSalaryDetail(batchId) {
  return request({
    url: '/ygb/salary/batch/generate/' + batchId,
    method: 'post'
  })
}

// 确认到账
export function confirmSalaryAccount(batchId, data) {
  return request({
    url: '/ygb/salary/batch/account/' + batchId,
    method: 'post',
    data: data
  })
}

// 提交发放
export function submitSalaryBatch(batchId) {
  return request({
    url: '/ygb/salary/batch/submit/' + batchId,
    method: 'post'
  })
}

// 模拟银行回调
export function simulateSalaryBatchCallback(data) {
  return request({
    url: '/ygb/stub/bank/callback',
    method: 'post',
    data: data
  })
}
