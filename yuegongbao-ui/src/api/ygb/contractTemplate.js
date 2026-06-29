import request from '@/utils/request'

export function listContractTemplate(query) {
  return request({
    url: '/ygb/contract/template/list',
    method: 'get',
    params: query
  })
}

export function getContractTemplate(templateId) {
  return request({
    url: '/ygb/contract/template/' + templateId,
    method: 'get'
  })
}

export function optionselectContractTemplate(query) {
  return request({
    url: '/ygb/contract/template/optionselect',
    method: 'get',
    params: query
  })
}

export function addContractTemplate(data) {
  return request({
    url: '/ygb/contract/template/add',
    method: 'post',
    data
  })
}

export function updateContractTemplate(data) {
  return request({
    url: '/ygb/contract/template/edit',
    method: 'put',
    data
  })
}

export function changeContractTemplateStatus(templateId, data) {
  return request({
    url: '/ygb/contract/template/status/' + templateId,
    method: 'put',
    data
  })
}

export function delContractTemplate(templateIds) {
  return request({
    url: '/ygb/contract/template/remove/' + templateIds,
    method: 'delete'
  })
}

export function submitContractTemplate(templateId, data = {}) {
  return request({
    url: '/ygb/contract/template/' + templateId + '/submit',
    method: 'post',
    data
  })
}

export function reviewContractTemplate(templateId, data) {
  return request({
    url: '/ygb/contract/template/' + templateId + '/review',
    method: 'post',
    data
  })
}
