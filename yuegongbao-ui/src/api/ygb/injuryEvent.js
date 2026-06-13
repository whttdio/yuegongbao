import request from '@/utils/request'

export function listInjuryEvent(query) {
  return request({
    url: '/ygb/injury/event/list',
    method: 'get',
    params: query
  })
}

export function getInjuryEventSummary(query) {
  return request({
    url: '/ygb/injury/event/summary',
    method: 'get',
    params: query
  })
}

export function getInjuryEventAnalysis(query) {
  return request({
    url: '/ygb/injury/event/analysis',
    method: 'get',
    params: query
  })
}

export function getInjuryEventMonitor(query) {
  return request({
    url: '/ygb/injury/event/monitor',
    method: 'get',
    params: query
  })
}

export function listRecognitionAssist(query) {
  return request({
    url: '/ygb/injury/event/recognitionAssist/list',
    method: 'get',
    params: query
  })
}

export function getRecognitionAssist(eventId) {
  return request({
    url: '/ygb/injury/event/recognitionAssist/' + eventId,
    method: 'get'
  })
}

export function getInjuryEvent(eventId) {
  return request({
    url: '/ygb/injury/event/' + eventId,
    method: 'get'
  })
}

export function addInjuryEvent(data) {
  return request({
    url: '/ygb/injury/event',
    method: 'post',
    data
  })
}

export function updateInjuryEvent(data) {
  return request({
    url: '/ygb/injury/event',
    method: 'put',
    data
  })
}

export function delInjuryEvent(eventId) {
  return request({
    url: '/ygb/injury/event/' + eventId,
    method: 'delete'
  })
}

export function updateInjuryStatus(eventId, data) {
  return request({
    url: '/ygb/injury/event/status/' + eventId,
    method: 'post',
    data
  })
}
