import request from '@/utils/request'

// 查询合同备案列表
export function listContract(query) {
  return request({
    url: '/ygb/contract/list',
    method: 'get',
    params: query
  })
}

// 查询合同备案汇总
export function getContractSummary(query) {
  return request({
    url: '/ygb/contract/summary',
    method: 'get',
    params: query
  })
}

// 查询合同下拉选项
export function optionselectContract() {
  return request({
    url: '/ygb/contract/optionselect',
    method: 'get'
  })
}

// 查询合同备案详细
export function getContract(contractId) {
  return request({
    url: '/ygb/contract/' + contractId,
    method: 'get'
  })
}

// 新增合同备案
export function addContract(data) {
  return request({
    url: '/ygb/contract',
    method: 'post',
    data: data
  })
}

// 修改合同备案
export function updateContract(data) {
  return request({
    url: '/ygb/contract',
    method: 'put',
    data: data
  })
}

// 删除合同备案
export function delContract(contractId) {
  return request({
    url: '/ygb/contract/' + contractId,
    method: 'delete'
  })
}

export function listContractExpiry(query) {
  return request({
    url: '/ygb/contract/expiry/list',
    method: 'get',
    params: query
  })
}

export function getContractExpirySummary(query) {
  return request({
    url: '/ygb/contract/expiry/summary',
    method: 'get',
    params: query
  })
}

export function remindContractExpiry(data) {
  return request({
    url: '/ygb/contract/expiry/remind',
    method: 'post',
    data
  })
}

export function listContractUnfiled(query) {
  return request({
    url: '/ygb/contract/unfiled/list',
    method: 'get',
    params: query
  })
}

export function getContractUnfiledSummary(query) {
  return request({
    url: '/ygb/contract/unfiled/summary',
    method: 'get',
    params: query
  })
}

export function warnContractUnfiled(data) {
  return request({
    url: '/ygb/contract/unfiled/warn',
    method: 'post',
    data
  })
}
