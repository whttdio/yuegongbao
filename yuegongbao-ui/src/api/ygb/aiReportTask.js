import request from '@/utils/request'

export function listAiReportTask(query) {
  return request({
    url: '/ygb/aiReport/task/list',
    method: 'get',
    params: query
  })
}

export function getAiReportTaskSummary(query) {
  return request({
    url: '/ygb/aiReport/task/summary',
    method: 'get',
    params: query
  })
}

export function getAiReportTask(taskId) {
  return request({
    url: '/ygb/aiReport/task/' + taskId,
    method: 'get'
  })
}

export function addAiReportTask(data) {
  return request({
    url: '/ygb/aiReport/task/add',
    method: 'post',
    data
  })
}

export function updateAiReportTask(data) {
  return request({
    url: '/ygb/aiReport/task/edit',
    method: 'put',
    data
  })
}

export function updateAiReportTaskStatus(taskId, data) {
  return request({
    url: '/ygb/aiReport/task/status/' + taskId,
    method: 'put',
    data
  })
}

export function delAiReportTask(taskIds) {
  return request({
    url: '/ygb/aiReport/task/remove/' + taskIds,
    method: 'delete'
  })
}
