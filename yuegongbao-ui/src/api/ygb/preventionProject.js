import request from '@/utils/request'

export function listPreventionProject(query) {
  return request({
    url: '/ygb/injury/prevention/list',
    method: 'get',
    params: query
  })
}

export function getPreventionProjectSummary(query) {
  return request({
    url: '/ygb/injury/prevention/summary',
    method: 'get',
    params: query
  })
}

export function getPreventionProject(projectId) {
  return request({
    url: '/ygb/injury/prevention/' + projectId,
    method: 'get'
  })
}

export function addPreventionProject(data) {
  return request({
    url: '/ygb/injury/prevention',
    method: 'post',
    data
  })
}

export function updatePreventionProject(data) {
  return request({
    url: '/ygb/injury/prevention',
    method: 'put',
    data
  })
}

export function delPreventionProject(projectId) {
  return request({
    url: '/ygb/injury/prevention/' + projectId,
    method: 'delete'
  })
}
