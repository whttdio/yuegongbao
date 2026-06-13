import request from '@/utils/request'

export function getYgbCockpitDashboard(query) {
  return request({
    url: '/ygb/cockpit/ygb/dashboard',
    method: 'get',
    params: query
  })
}

export function getCockpitIndicators(query) {
  return request({
    url: '/ygb/cockpit/indicators',
    method: 'get',
    params: query
  })
}

export function listCockpitTrend(query) {
  return request({
    url: '/ygb/cockpit/trend',
    method: 'get',
    params: query
  })
}

export function listCockpitDistribution(query) {
  return request({
    url: '/ygb/cockpit/distribution',
    method: 'get',
    params: query
  })
}

export function getCockpitMap(query) {
  return request({
    url: '/ygb/cockpit/map',
    method: 'get',
    params: query
  })
}

export function getAzbCockpitDashboard(query) {
  return request({
    url: '/ygb/cockpit/azb/dashboard',
    method: 'get',
    params: query
  })
}
