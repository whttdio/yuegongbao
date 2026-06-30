import request from '../utils/request'

export function getEnterpriseHomeDashboard() {
  return request({ url: '/app/enterprise/home/dashboard' })
}

export function getEnterpriseWorkbenchDashboard() {
  return request({ url: '/app/enterprise/workbench/dashboard' })
}

export function getEnterprisePeopleLedger() {
  return request({ url: '/app/enterprise/people/ledger' })
}

export function submitEnterprisePeopleAction(data) {
  return request({
    url: '/app/enterprise/people/action',
    method: 'POST',
    data
  })
}

export function exportEnterprisePeopleLedger(data) {
  return request({
    url: '/app/enterprise/people/export',
    method: 'POST',
    data
  })
}

export function getEnterpriseDeviceLedger() {
  return request({ url: '/app/enterprise/device/ledger' })
}

export function submitEnterpriseDeviceAction(data) {
  return request({
    url: '/app/enterprise/device/action',
    method: 'POST',
    data
  })
}

export function exportEnterpriseDeviceLedger(data) {
  return request({
    url: '/app/enterprise/device/export',
    method: 'POST',
    data
  })
}

export function getSalaryConfirmDashboard() {
  return request({ url: '/app/enterprise/salary/dashboard' })
}

export function submitSalaryConfirm(data) {
  return request({
    url: '/app/enterprise/salary/confirm',
    method: 'POST',
    data
  })
}

export function importSalaryDraft(data) {
  return request({
    url: '/app/enterprise/salary/import-draft',
    method: 'POST',
    data
  })
}

export function getOperationApprovalDashboard() {
  return request({ url: '/app/enterprise/operation-approval/dashboard' })
}

export function submitOperationApproval(data) {
  return request({
    url: '/app/enterprise/operation-approval/submit',
    method: 'POST',
    data
  })
}

export function exportOperationLedger() {
  return request({
    url: '/app/enterprise/operation-approval/export',
    method: 'POST'
  })
}

export function getInsuranceManageDashboard() {
  return request({ url: '/app/enterprise/insurance/dashboard' })
}

export function submitInsuranceAction(data) {
  return request({
    url: '/app/enterprise/insurance/action',
    method: 'POST',
    data
  })
}

export function getTrainingManageDashboard() {
  return request({ url: '/app/enterprise/training/dashboard' })
}

export function saveTrainingPlanDraft(data) {
  return request({
    url: '/app/enterprise/training/save-draft',
    method: 'POST',
    data
  })
}

export function submitTrainingManageAction(data) {
  return request({
    url: '/app/enterprise/training/action',
    method: 'POST',
    data
  })
}

export function getJobPublishDraft() {
  return request({ url: '/app/enterprise/job-publish/draft' })
}

export function saveJobPublishDraft(data) {
  return request({
    url: '/app/enterprise/job-publish/save-draft',
    method: 'POST',
    data
  })
}

export function submitJobPublish(data) {
  return request({
    url: '/app/enterprise/job-publish/submit',
    method: 'POST',
    data
  })
}
