import request from '@/utils/request'

export function listWorkerActivity(query) {
  return request({
    url: '/ygb/worker/activity/manage/list',
    method: 'get',
    params: query
  })
}

export function getWorkerActivity(joinId) {
  return request({
    url: `/ygb/worker/activity/manage/${joinId}`,
    method: 'get'
  })
}

export function updateWorkerActivityStatus(joinId, data) {
  return request({
    url: `/ygb/worker/activity/manage/status/${joinId}`,
    method: 'post',
    data
  })
}
