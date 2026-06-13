import request from '@/utils/request'

export function listStatReport(query) {
  return request({
    url: '/ygb/report/list',
    method: 'get',
    params: query
  })
}

export function getStatReportSummary(query) {
  return request({
    url: '/ygb/report/summary',
    method: 'get',
    params: query
  })
}

export function getStatReport(reportId) {
  return request({
    url: '/ygb/report/' + reportId,
    method: 'get'
  })
}

export function generateStatReport(data) {
  return request({
    url: '/ygb/report/generate',
    method: 'post',
    data
  })
}
