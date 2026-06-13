import request from '@/utils/request'

export function listWarningRule(query) {
  return request({
    url: '/ygb/warning/rule/list',
    method: 'get',
    params: query
  })
}

export function getWarningRuleSummary(query) {
  return request({
    url: '/ygb/warning/rule/summary',
    method: 'get',
    params: query
  })
}

export function getWarningRule(ruleId) {
  return request({
    url: '/ygb/warning/rule/' + ruleId,
    method: 'get'
  })
}

export function addWarningRule(data) {
  return request({
    url: '/ygb/warning/rule',
    method: 'post',
    data
  })
}

export function updateWarningRule(data) {
  return request({
    url: '/ygb/warning/rule',
    method: 'put',
    data
  })
}

export function delWarningRule(ruleId) {
  return request({
    url: '/ygb/warning/rule/' + ruleId,
    method: 'delete'
  })
}
