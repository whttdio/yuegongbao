import request from '@/utils/request'

// 查询人员列表
export function listPerson(query) {
  return request({
    url: '/ygb/person/list',
    method: 'get',
    params: query
  })
}

export function getPersonSummary(query) {
  return request({
    url: '/ygb/person/summary',
    method: 'get',
    params: query
  })
}

// 查询人员详细
export function getPerson(personId) {
  return request({
    url: '/ygb/person/' + personId,
    method: 'get'
  })
}

// 查询人员下拉选项
export function optionselectPerson(query) {
  return request({
    url: '/ygb/person/optionselect',
    method: 'get',
    params: query
  })
}

// 新增人员
export function addPerson(data) {
  return request({
    url: '/ygb/person',
    method: 'post',
    data: data
  })
}

// 修改人员
export function updatePerson(data) {
  return request({
    url: '/ygb/person',
    method: 'put',
    data: data
  })
}

// 删除人员
export function delPerson(personId) {
  return request({
    url: '/ygb/person/' + personId,
    method: 'delete'
  })
}

export function listPersonSubmodule(submodule, query) {
  return request({
    url: `/ygb/person/${submodule}/list`,
    method: 'get',
    params: query
  })
}

export function getPersonSubmoduleSummary(submodule, query) {
  return request({
    url: `/ygb/person/${submodule}/summary`,
    method: 'get',
    params: query
  })
}

export function getPersonSubmodule(submodule, recordId) {
  return request({
    url: `/ygb/person/${submodule}/${recordId}`,
    method: 'get'
  })
}

export function addPersonSubmodule(submodule, data) {
  return request({
    url: `/ygb/person/${submodule}`,
    method: 'post',
    data
  })
}

export function updatePersonSubmodule(submodule, data) {
  return request({
    url: `/ygb/person/${submodule}`,
    method: 'put',
    data
  })
}

export function delPersonSubmodule(submodule, recordIds) {
  return request({
    url: `/ygb/person/${submodule}/${recordIds}`,
    method: 'delete'
  })
}
