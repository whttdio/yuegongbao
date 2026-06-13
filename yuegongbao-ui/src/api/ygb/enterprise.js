import request from '@/utils/request'

// 查询企业列表
export function listEnterprise(query) {
  return request({
    url: '/ygb/enterprise/list',
    method: 'get',
    params: query
  })
}

export function getEnterpriseSummary(query) {
  return request({
    url: '/ygb/enterprise/summary',
    method: 'get',
    params: query
  })
}

// 查询企业详细
export function getEnterprise(enterpriseId) {
  return request({
    url: '/ygb/enterprise/' + enterpriseId,
    method: 'get'
  })
}

// 查询企业选项
export function optionselectEnterprise() {
  return request({
    url: '/ygb/enterprise/optionselect',
    method: 'get'
  })
}

// 新增企业
export function addEnterprise(data) {
  return request({
    url: '/ygb/enterprise',
    method: 'post',
    data: data
  })
}

// 修改企业
export function updateEnterprise(data) {
  return request({
    url: '/ygb/enterprise',
    method: 'put',
    data: data
  })
}

// 删除企业
export function delEnterprise(enterpriseId) {
  return request({
    url: '/ygb/enterprise/' + enterpriseId,
    method: 'delete'
  })
}

export function listEnterpriseSubmodule(submodule, query) {
  return request({
    url: `/ygb/enterprise/${submodule}/list`,
    method: 'get',
    params: query
  })
}

export function getEnterpriseSubmoduleSummary(submodule, query) {
  return request({
    url: `/ygb/enterprise/${submodule}/summary`,
    method: 'get',
    params: query
  })
}

export function getEnterpriseSubmodule(submodule, recordId) {
  return request({
    url: `/ygb/enterprise/${submodule}/${recordId}`,
    method: 'get'
  })
}

export function addEnterpriseSubmodule(submodule, data) {
  return request({
    url: `/ygb/enterprise/${submodule}`,
    method: 'post',
    data
  })
}

export function updateEnterpriseSubmodule(submodule, data) {
  return request({
    url: `/ygb/enterprise/${submodule}`,
    method: 'put',
    data
  })
}

export function delEnterpriseSubmodule(submodule, recordIds) {
  return request({
    url: `/ygb/enterprise/${submodule}/${recordIds}`,
    method: 'delete'
  })
}
