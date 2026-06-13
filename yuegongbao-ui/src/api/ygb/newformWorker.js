import request from '@/utils/request'

export function listNewformWorker(query) {
  return request({
    url: '/ygb/newform/worker/list',
    method: 'get',
    params: query
  })
}

export function getNewformWorkerSummary(query) {
  return request({
    url: '/ygb/newform/worker/summary',
    method: 'get',
    params: query
  })
}

export function listNewformPlatform(query) {
  return request({
    url: '/ygb/newform/platform/list',
    method: 'get',
    params: query
  })
}

export function getNewformPlatformSummary(query) {
  return request({
    url: '/ygb/newform/platform/summary',
    method: 'get',
    params: query
  })
}

export function listNewformInjuryMonitor(query) {
  return request({
    url: '/ygb/newform/injuryMonitor/list',
    method: 'get',
    params: query
  })
}

export function getNewformInjuryMonitorSummary(query) {
  return request({
    url: '/ygb/newform/injuryMonitor/summary',
    method: 'get',
    params: query
  })
}

export function listNewformTraining(query) {
  return request({
    url: '/ygb/newform/training/list',
    method: 'get',
    params: query
  })
}

export function getNewformTrainingSummary(query) {
  return request({
    url: '/ygb/newform/training/summary',
    method: 'get',
    params: query
  })
}

export function getNewformTraining(recordId) {
  return request({
    url: `/ygb/newform/training/${recordId}`,
    method: 'get'
  })
}

export function addNewformTraining(data) {
  return request({
    url: '/ygb/newform/training',
    method: 'post',
    data
  })
}

export function updateNewformTraining(data) {
  return request({
    url: '/ygb/newform/training',
    method: 'put',
    data
  })
}

export function delNewformTraining(recordIds) {
  return request({
    url: `/ygb/newform/training/${recordIds}`,
    method: 'delete'
  })
}

export function syncNewformWorker(data) {
  return request({
    url: '/ygb/newform/worker/sync',
    method: 'post',
    data
  })
}
