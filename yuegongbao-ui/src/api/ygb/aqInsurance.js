import request from '@/utils/request'

export function listAqInsurance(query) {
  return request({
    url: '/ygb/aqInsurance/list',
    method: 'get',
    params: query
  })
}

export function getAqInsuranceSummary(query) {
  return request({
    url: '/ygb/aqInsurance/summary',
    method: 'get',
    params: query
  })
}

export function getAqInsurance(policyId) {
  return request({
    url: `/ygb/aqInsurance/${policyId}`,
    method: 'get'
  })
}

export function syncAqInsurance(data) {
  return request({
    url: '/ygb/aqInsurance/sync',
    method: 'post',
    data
  })
}

export function listAqInsuranceClaim(query) {
  return request({
    url: '/ygb/aqInsurance/claim/list',
    method: 'get',
    params: query
  })
}

export function getAqInsuranceClaimSummary(query) {
  return request({
    url: '/ygb/aqInsurance/claim/summary',
    method: 'get',
    params: query
  })
}

export function getAqInsuranceClaim(recordId) {
  return request({
    url: `/ygb/aqInsurance/claim/${recordId}`,
    method: 'get'
  })
}

export function addAqInsuranceClaim(data) {
  return request({
    url: '/ygb/aqInsurance/claim',
    method: 'post',
    data
  })
}

export function updateAqInsuranceClaim(data) {
  return request({
    url: '/ygb/aqInsurance/claim',
    method: 'put',
    data
  })
}

export function delAqInsuranceClaim(recordIds) {
  return request({
    url: `/ygb/aqInsurance/claim/${recordIds}`,
    method: 'delete'
  })
}
