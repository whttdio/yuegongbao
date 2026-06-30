import request from '@/utils/request'

export function listLedgerSubmodule(submodule, query) {
  return request({
    url: `/ygb/ledger/${submodule}/list`,
    method: 'get',
    params: query
  })
}

export function getLedgerSubmoduleSummary(submodule, query) {
  return request({
    url: `/ygb/ledger/${submodule}/summary`,
    method: 'get',
    params: query
  })
}

export function getLedgerSubmodule(submodule, recordId) {
  return request({
    url: `/ygb/ledger/${submodule}/${recordId}`,
    method: 'get'
  })
}

export function addLedgerSubmodule(submodule, data) {
  return request({
    url: `/ygb/ledger/${submodule}`,
    method: 'post',
    data
  })
}

export function updateLedgerSubmodule(submodule, data) {
  return request({
    url: `/ygb/ledger/${submodule}`,
    method: 'put',
    data
  })
}

export function delLedgerSubmodule(submodule, recordIds) {
  return request({
    url: `/ygb/ledger/${submodule}/${recordIds}`,
    method: 'delete'
  })
}
