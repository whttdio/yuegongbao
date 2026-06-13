import request from '@/utils/request'

export function listSocialPayment(query) {
  return request({
    url: '/ygb/social/payment/list',
    method: 'get',
    params: query
  })
}

export function getSocialPaymentSummary(query) {
  return request({
    url: '/ygb/social/payment/summary',
    method: 'get',
    params: query
  })
}

export function getSocialPayment(paymentId) {
  return request({
    url: '/ygb/social/payment/' + paymentId,
    method: 'get'
  })
}

export function syncSocialPayment(data) {
  return request({
    url: '/ygb/social/payment/sync',
    method: 'post',
    data
  })
}
