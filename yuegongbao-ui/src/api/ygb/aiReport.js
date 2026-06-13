import request from '@/utils/request'

export function listAiReport(query) {
  return request({
    url: '/ygb/aiReport/list',
    method: 'get',
    params: query
  })
}

export function getAiReport(reportId) {
  return request({
    url: '/ygb/aiReport/' + reportId,
    method: 'get'
  })
}

export function getAiReportDashboard(query) {
  return request({
    url: '/ygb/aiReport/dashboard',
    method: 'get',
    params: query
  })
}

export function generateAiReport(data) {
  return request({
    url: '/ygb/aiReport/generate',
    method: 'post',
    data
  })
}
