import request from '@/utils/request'

export function listHeightWorkReport(query) {
  return request({
    url: '/ygb/heightWork/report/list',
    method: 'get',
    params: query
  })
}

export function getHeightWorkReportSummary(query) {
  return request({
    url: '/ygb/heightWork/report/summary',
    method: 'get',
    params: query
  })
}

export function getHeightWorkReport(reportId) {
  return request({
    url: '/ygb/heightWork/report/' + reportId,
    method: 'get'
  })
}

export function addHeightWorkReport(data) {
  return request({
    url: '/ygb/heightWork/report',
    method: 'post',
    data
  })
}

export function updateHeightWorkReport(data) {
  return request({
    url: '/ygb/heightWork/report',
    method: 'put',
    data
  })
}

export function finishHeightWorkReport(reportId, data) {
  return request({
    url: '/ygb/heightWork/report/finish/' + reportId,
    method: 'post',
    data
  })
}

export function getHeightWorkVoucher(reportId) {
  return request({
    url: '/ygb/heightWork/report/voucher/' + reportId,
    method: 'get'
  })
}
