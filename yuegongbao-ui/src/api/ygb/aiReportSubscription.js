import request from '@/utils/request'

export function listAiReportSubscription(query) {
  return request({
    url: '/ygb/aiReport/subscription/list',
    method: 'get',
    params: query
  })
}

export function getAiReportSubscriptionSummary(query) {
  return request({
    url: '/ygb/aiReport/subscription/summary',
    method: 'get',
    params: query
  })
}

export function getAiReportSubscription(subscriptionId) {
  return request({
    url: '/ygb/aiReport/subscription/' + subscriptionId,
    method: 'get'
  })
}

export function addAiReportSubscription(data) {
  return request({
    url: '/ygb/aiReport/subscription/add',
    method: 'post',
    data
  })
}

export function updateAiReportSubscription(data) {
  return request({
    url: '/ygb/aiReport/subscription/edit',
    method: 'put',
    data
  })
}

export function delAiReportSubscription(subscriptionIds) {
  return request({
    url: '/ygb/aiReport/subscription/remove/' + subscriptionIds,
    method: 'delete'
  })
}
