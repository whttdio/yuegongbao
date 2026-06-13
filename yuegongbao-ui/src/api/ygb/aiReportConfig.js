import request from '@/utils/request'

export function listAiReportConfig(query) {
  return request({
    url: '/ygb/aiReport/config/list',
    method: 'get',
    params: query
  })
}

export function getAiReportConfigSummary(query) {
  return request({
    url: '/ygb/aiReport/config/summary',
    method: 'get',
    params: query
  })
}

export function getAiReportConfig(configId) {
  return request({
    url: '/ygb/aiReport/config/' + configId,
    method: 'get'
  })
}

export function getCurrentAiReportConfig(query) {
  return request({
    url: '/ygb/aiReport/config/current',
    method: 'get',
    params: query
  })
}

export function addAiReportConfig(data) {
  return request({
    url: '/ygb/aiReport/config',
    method: 'post',
    data
  })
}

export function updateAiReportConfig(data) {
  return request({
    url: '/ygb/aiReport/config',
    method: 'put',
    data
  })
}

export function activateAiReportConfig(configId) {
  return request({
    url: '/ygb/aiReport/config/' + configId + '/activate',
    method: 'put'
  })
}
