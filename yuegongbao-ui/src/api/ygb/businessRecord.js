import request from '@/utils/request'

export function listBusinessRecord(module, query) {
  return request({
    url: `/ygb/${module}/list`,
    method: 'get',
    params: query
  })
}

export function getBusinessRecordSummary(module, query) {
  return request({
    url: `/ygb/${module}/summary`,
    method: 'get',
    params: query
  })
}

export function getBusinessRecord(module, businessId) {
  return request({
    url: `/ygb/${module}/${businessId}`,
    method: 'get'
  })
}

export function addBusinessRecord(module, data) {
  return request({
    url: `/ygb/${module}`,
    method: 'post',
    data
  })
}

export function updateBusinessRecord(module, data) {
  return request({
    url: `/ygb/${module}`,
    method: 'put',
    data
  })
}

export function delBusinessRecord(module, businessIds) {
  return request({
    url: `/ygb/${module}/${businessIds}`,
    method: 'delete'
  })
}
