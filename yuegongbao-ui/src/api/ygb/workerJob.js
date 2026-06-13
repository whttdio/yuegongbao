import request from '@/utils/request'

export function listWorkerJob(query) {
  return request({
    url: '/ygb/worker/job/list',
    method: 'get',
    params: query
  })
}

export function getWorkerJob(jobId) {
  return request({
    url: `/ygb/worker/job/${jobId}`,
    method: 'get'
  })
}

export function addWorkerJob(data) {
  return request({
    url: '/ygb/worker/job',
    method: 'post',
    data
  })
}

export function updateWorkerJob(data) {
  return request({
    url: '/ygb/worker/job',
    method: 'put',
    data
  })
}

export function delWorkerJob(jobIds) {
  return request({
    url: `/ygb/worker/job/${jobIds}`,
    method: 'delete'
  })
}
