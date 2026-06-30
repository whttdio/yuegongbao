import request from '../utils/request'

export function getHighRiskUnlockDashboard() {
  return request({ url: '/app/worker/high-risk/unlock/dashboard' })
}

export function verifyHighRiskScan(data = {}) {
  return request({
    url: '/app/worker/high-risk/unlock/scan-verify',
    method: 'POST',
    data
  })
}

export function verifyHighRiskFace(data = {}) {
  return request({
    url: '/app/worker/high-risk/unlock/face-verify',
    method: 'POST',
    data
  })
}

export function submitHighRiskUnlock(data) {
  return request({
    url: '/app/worker/high-risk/unlock/submit',
    method: 'POST',
    data
  })
}

export function getOutworkApplyDraft() {
  return request({ url: '/app/worker/high-risk/outwork/draft' })
}

export function saveOutworkAttachment(data = {}) {
  return request({
    url: '/app/worker/high-risk/outwork/attachment',
    method: 'POST',
    data
  })
}

export function submitOutworkApply(data) {
  return request({
    url: '/app/worker/high-risk/outwork/submit',
    method: 'POST',
    data
  })
}

export function getHighRiskCertificates() {
  return request({ url: '/app/worker/high-risk/certificates' })
}

export function submitCertificateRenew(data = {}) {
  return request({
    url: '/app/worker/high-risk/certificates/renew',
    method: 'POST',
    data
  })
}

export function submitCertificateUpload(data = {}) {
  return request({
    url: '/app/worker/high-risk/certificates/upload',
    method: 'POST',
    data
  })
}

export function getHighRiskWorkRecords() {
  return request({ url: '/app/worker/high-risk/work-records' })
}
