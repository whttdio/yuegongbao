import request from '@/utils/request'

export function getPlatformRuntimeSummary() {
  return request({
    url: '/ygb/platform/runtime/summary',
    method: 'get'
  })
}

export function listPlatformModule(submodule, query) {
  return request({
    url: `/ygb/platform/${submodule}/list`,
    method: 'get',
    params: query
  })
}

export function getPlatformModuleSummary(submodule, query) {
  return request({
    url: `/ygb/platform/${submodule}/summary`,
    method: 'get',
    params: query
  })
}

export function getPlatformModule(submodule, recordId) {
  return request({
    url: `/ygb/platform/${submodule}/${recordId}`,
    method: 'get'
  })
}

export function addPlatformModule(submodule, data) {
  return request({
    url: `/ygb/platform/${submodule}`,
    method: 'post',
    data
  })
}

export function updatePlatformModule(submodule, data) {
  return request({
    url: `/ygb/platform/${submodule}`,
    method: 'put',
    data
  })
}

export function delPlatformModule(submodule, recordIds) {
  return request({
    url: `/ygb/platform/${submodule}/${recordIds}`,
    method: 'delete'
  })
}
