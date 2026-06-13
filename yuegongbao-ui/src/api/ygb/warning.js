import request from '@/utils/request'

export function listWarning(query) {
  return request({
    url: '/ygb/warning/list',
    method: 'get',
    params: query
  })
}

export function getWarningSummary(query) {
  return request({
    url: '/ygb/warning/summary',
    method: 'get',
    params: query
  })
}

export function getWarningAnalysis(query) {
  return request({
    url: '/ygb/warning/analysis',
    method: 'get',
    params: query
  })
}

export function getWarning(warnId) {
  return request({
    url: '/ygb/warning/' + warnId,
    method: 'get'
  })
}

export function handleWarning(warnId, data) {
  return request({
    url: '/ygb/warning/handle/' + warnId,
    method: 'post',
    data
  })
}
