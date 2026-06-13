import request from '@/utils/request'

export function listWorkerComplaint(query) {
  return request({
    url: '/ygb/worker/message/manage/complaint/list',
    method: 'get',
    params: query
  })
}

export function getWorkerComplaint(complaintId) {
  return request({
    url: `/ygb/worker/message/manage/complaint/${complaintId}`,
    method: 'get'
  })
}

export function updateWorkerComplaintStatus(complaintId, data) {
  return request({
    url: `/ygb/worker/message/complaint/status/${complaintId}`,
    method: 'post',
    data
  })
}

export function listWorkerLegalConsult(query) {
  return request({
    url: '/ygb/worker/message/manage/legal-consult/list',
    method: 'get',
    params: query
  })
}

export function getWorkerLegalConsult(consultId) {
  return request({
    url: `/ygb/worker/message/manage/legal-consult/${consultId}`,
    method: 'get'
  })
}

export function updateWorkerLegalConsultStatus(consultId, data) {
  return request({
    url: `/ygb/worker/message/legal-consult/status/${consultId}`,
    method: 'post',
    data
  })
}

export function listWorkerFeedback(query) {
  return request({
    url: '/ygb/worker/profile/manage/feedback/list',
    method: 'get',
    params: query
  })
}

export function getWorkerFeedback(feedbackId) {
  return request({
    url: `/ygb/worker/profile/manage/feedback/${feedbackId}`,
    method: 'get'
  })
}

export function listWorkerUploadRecord(query) {
  return request({
    url: '/ygb/worker/profile/manage/upload-record/list',
    method: 'get',
    params: query
  })
}

export function getWorkerUploadRecord(uploadId) {
  return request({
    url: `/ygb/worker/profile/manage/upload-record/${uploadId}`,
    method: 'get'
  })
}
