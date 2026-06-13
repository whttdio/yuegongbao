import request from '@/utils/request'

export function listCockpitConfig(query) {
  return request({
    url: '/ygb/cockpit/config/list',
    method: 'get',
    params: query
  })
}

export function getCockpitConfigSummary(query) {
  return request({
    url: '/ygb/cockpit/config/summary',
    method: 'get',
    params: query
  })
}

export function getCockpitConfig(configId) {
  return request({
    url: '/ygb/cockpit/config/' + configId,
    method: 'get'
  })
}

export function getCurrentCockpitConfig(query) {
  return request({
    url: '/ygb/cockpit/config/current',
    method: 'get',
    params: query
  })
}

export function addCockpitConfig(data) {
  return request({
    url: '/ygb/cockpit/config/add',
    method: 'post',
    data
  })
}

export function updateCockpitConfig(data) {
  return request({
    url: '/ygb/cockpit/config/edit',
    method: 'put',
    data
  })
}

export function delCockpitConfig(configIds) {
  return request({
    url: '/ygb/cockpit/config/remove/' + configIds,
    method: 'delete'
  })
}
