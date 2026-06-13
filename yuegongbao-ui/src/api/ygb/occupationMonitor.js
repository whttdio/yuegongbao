import request from '@/utils/request'

export function listOccupationMonitor(query) {
  return request({
    url: '/ygb/occupation/monitor/list',
    method: 'get',
    params: query
  })
}

export function getOccupationMonitorSummary(query) {
  return request({
    url: '/ygb/occupation/monitor/summary',
    method: 'get',
    params: query
  })
}

export function listOccupationPrevention(query) {
  return request({
    url: '/ygb/occupation/prevention/list',
    method: 'get',
    params: query
  })
}

export function getOccupationPreventionSummary(query) {
  return request({
    url: '/ygb/occupation/prevention/summary',
    method: 'get',
    params: query
  })
}

export function getOccupationPrevention(recordId) {
  return request({
    url: `/ygb/occupation/prevention/${recordId}`,
    method: 'get'
  })
}

export function listOccupationHealthArchive(query) {
  return request({
    url: '/ygb/occupation/healthArchive/list',
    method: 'get',
    params: query
  })
}

export function getOccupationHealthArchiveSummary(query) {
  return request({
    url: '/ygb/occupation/healthArchive/summary',
    method: 'get',
    params: query
  })
}

export function getOccupationHealthArchive(recordId) {
  return request({
    url: `/ygb/occupation/healthArchive/${recordId}`,
    method: 'get'
  })
}

export function syncOccupationMonitor(data) {
  return request({
    url: '/ygb/occupation/monitor/sync',
    method: 'post',
    data
  })
}
