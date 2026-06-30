import request from '@/utils/request'

/**
 * 企业后台 - 首页仪表盘数据
 */
export function getEnterpriseHomeDashboard() {
  return request({
    url: '/app/enterprise/home/dashboard',
    method: 'get'
  })
}

/**
 * 企业后台 - 工作台仪表盘数据
 */
export function getEnterpriseWorkbenchDashboard() {
  return request({
    url: '/app/enterprise/workbench/dashboard',
    method: 'get'
  })
}

/**
 * 企业后台 - 人员台账
 */
export function listEnterprisePeopleLedger(query) {
  return request({
    url: '/app/enterprise/people/ledger',
    method: 'get',
    params: query
  })
}

export function submitEnterprisePeopleAction(data) {
  return request({
    url: '/app/enterprise/people/action',
    method: 'post',
    data
  })
}

export function exportEnterprisePeopleLedger(data) {
  return request({
    url: '/app/enterprise/people/export',
    method: 'post',
    data
  })
}

/**
 * 企业后台 - 设备台账
 */
export function listEnterpriseDeviceLedger(query) {
  return request({
    url: '/app/enterprise/device/ledger',
    method: 'get',
    params: query
  })
}

export function submitEnterpriseDeviceAction(data) {
  return request({
    url: '/app/enterprise/device/action',
    method: 'post',
    data
  })
}

export function exportEnterpriseDeviceLedger(data) {
  return request({
    url: '/app/enterprise/device/export',
    method: 'post',
    data
  })
}

/**
 * 企业后台 - 工资仪表盘
 */
export function getEnterpriseSalaryDashboard() {
  return request({
    url: '/app/enterprise/salary/dashboard',
    method: 'get'
  })
}

export function submitEnterpriseSalaryConfirm(data) {
  return request({
    url: '/app/enterprise/salary/confirm',
    method: 'post',
    data
  })
}

export function importEnterpriseSalaryDraft(data) {
  return request({
    url: '/app/enterprise/salary/import-draft',
    method: 'post',
    data
  })
}

/**
 * 企业后台 - 作业审批仪表盘
 */
export function getEnterpriseOperationDashboard() {
  return request({
    url: '/app/enterprise/operation-approval/dashboard',
    method: 'get'
  })
}

export function submitEnterpriseOperationApproval(data) {
  return request({
    url: '/app/enterprise/operation-approval/submit',
    method: 'post',
    data
  })
}

export function exportEnterpriseOperationLedger() {
  return request({
    url: '/app/enterprise/operation-approval/export',
    method: 'post'
  })
}

/**
 * 企业后台 - 保险仪表盘
 */
export function getEnterpriseInsuranceDashboard() {
  return request({
    url: '/app/enterprise/insurance/dashboard',
    method: 'get'
  })
}

export function submitEnterpriseInsuranceAction(data) {
  return request({
    url: '/app/enterprise/insurance/action',
    method: 'post',
    data
  })
}

/**
 * 企业后台 - 培训仪表盘
 */
export function getEnterpriseTrainingDashboard() {
  return request({
    url: '/app/enterprise/training/dashboard',
    method: 'get'
  })
}

export function saveEnterpriseTrainingPlanDraft(data) {
  return request({
    url: '/app/enterprise/training/save-draft',
    method: 'post',
    data
  })
}

export function submitEnterpriseTrainingAction(data) {
  return request({
    url: '/app/enterprise/training/action',
    method: 'post',
    data
  })
}

/**
 * 企业后台 - 招聘发布草稿
 */
export function getEnterpriseJobPublishDraft() {
  return request({
    url: '/app/enterprise/job-publish/draft',
    method: 'get'
  })
}

export function saveEnterpriseJobPublishDraft(data) {
  return request({
    url: '/app/enterprise/job-publish/save-draft',
    method: 'post',
    data
  })
}

export function submitEnterpriseJobPublish(data) {
  return request({
    url: '/app/enterprise/job-publish/submit',
    method: 'post',
    data
  })
}
