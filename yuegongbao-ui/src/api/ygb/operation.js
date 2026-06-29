import request from '@/utils/request'

export function getOperationOverview() {
  return request({
    url: '/ygb/operation/overview',
    method: 'get'
  })
}

export function listOperationModule(submodule, query) {
  return request({
    url: `/ygb/operation/${submodule}/list`,
    method: 'get',
    params: query
  })
}

export function getOperationModuleSummary(submodule, query) {
  return request({
    url: `/ygb/operation/${submodule}/summary`,
    method: 'get',
    params: query
  })
}

export function getOperationModule(submodule, recordId) {
  return request({
    url: `/ygb/operation/${submodule}/${recordId}`,
    method: 'get'
  })
}

export function addOperationModule(submodule, data) {
  return request({
    url: `/ygb/operation/${submodule}`,
    method: 'post',
    data
  })
}

export function updateOperationModule(submodule, data) {
  return request({
    url: `/ygb/operation/${submodule}`,
    method: 'put',
    data
  })
}

export function delOperationModule(submodule, recordIds) {
  return request({
    url: `/ygb/operation/${submodule}/${recordIds}`,
    method: 'delete'
  })
}

export function listOperationJobReview(query) {
  return request({
    url: '/ygb/operation/jobReview/list',
    method: 'get',
    params: query
  })
}

export function getOperationJobReview(jobId) {
  return request({
    url: `/ygb/operation/jobReview/${jobId}`,
    method: 'get'
  })
}

export function reviewOperationJob(jobId, data) {
  return request({
    url: `/ygb/operation/jobReview/${jobId}/review`,
    method: 'put',
    data
  })
}

export function listOperationResume(query) {
  return request({
    url: '/ygb/operation/resume/list',
    method: 'get',
    params: query
  })
}

export function getOperationResume(resumeId) {
  return request({
    url: `/ygb/operation/resume/${resumeId}`,
    method: 'get'
  })
}

export function markOperationResume(resumeId, data) {
  return request({
    url: `/ygb/operation/resume/${resumeId}/mark`,
    method: 'put',
    data
  })
}
