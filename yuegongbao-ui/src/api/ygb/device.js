import request from '@/utils/request'

export function listDevice(query) {
  return request({
    url: '/ygb/device/list',
    method: 'get',
    params: query
  })
}

export function getDeviceSummary(query) {
  return request({
    url: '/ygb/device/summary',
    method: 'get',
    params: query
  })
}

export function listDeviceSubledger(viewCode, query) {
  return request({
    url: '/ygb/device/subledger/list',
    method: 'get',
    params: { viewCode, ...(query || {}) }
  })
}

export function getDeviceSubledgerSummary(viewCode, query) {
  return request({
    url: '/ygb/device/subledger/summary',
    method: 'get',
    params: { viewCode, ...(query || {}) }
  })
}

export function getDevice(deviceId) {
  return request({
    url: '/ygb/device/' + deviceId,
    method: 'get'
  })
}

export function addDevice(data) {
  return request({
    url: '/ygb/device',
    method: 'post',
    data
  })
}

export function updateDevice(data) {
  return request({
    url: '/ygb/device',
    method: 'put',
    data
  })
}

export function delDevice(deviceId) {
  return request({
    url: '/ygb/device/' + deviceId,
    method: 'delete'
  })
}

export function listDeviceCommandLog(query) {
  return request({
    url: '/ygb/device/commandLog/list',
    method: 'get',
    params: query
  })
}

export function listDeviceEvent(query) {
  return request({
    url: '/ygb/device/event/list',
    method: 'get',
    params: query
  })
}

export function listDeviceIotCard(query) {
  return request({
    url: '/ygb/device/iotCard/list',
    method: 'get',
    params: query
  })
}

export function getDeviceIotCard(deviceId) {
  return request({
    url: `/ygb/device/iotCard/${deviceId}`,
    method: 'get'
  })
}

export function getDeviceIotCardSummary(query) {
  return request({
    url: '/ygb/device/iotCard/summary',
    method: 'get',
    params: query
  })
}

export function listDeviceChipInventory(query) {
  return request({
    url: '/ygb/device/chipInventory/list',
    method: 'get',
    params: query
  })
}

export function getDeviceChipInventory(deviceId) {
  return request({
    url: `/ygb/device/chipInventory/${deviceId}`,
    method: 'get'
  })
}

export function getDeviceChipInventorySummary(query) {
  return request({
    url: '/ygb/device/chipInventory/summary',
    method: 'get',
    params: query
  })
}

export function listDeviceUninstallAlert(query) {
  return request({
    url: '/ygb/device/uninstallAlert/list',
    method: 'get',
    params: query
  })
}

export function listDeviceGeofence(query) {
  return request({
    url: '/ygb/device/geofence/list',
    method: 'get',
    params: query
  })
}

export function getDeviceGeofenceSummary(query) {
  return request({
    url: '/ygb/device/geofence/summary',
    method: 'get',
    params: query
  })
}

export function getDeviceGeofence(recordId) {
  return request({
    url: `/ygb/device/geofence/${recordId}`,
    method: 'get'
  })
}

export function addDeviceGeofence(data) {
  return request({
    url: '/ygb/device/geofence',
    method: 'post',
    data
  })
}

export function updateDeviceGeofence(data) {
  return request({
    url: '/ygb/device/geofence',
    method: 'put',
    data
  })
}

export function delDeviceGeofence(recordIds) {
  return request({
    url: `/ygb/device/geofence/${recordIds}`,
    method: 'delete'
  })
}

export function lockDevice(deviceId) {
  return request({
    url: '/ygb/device/lock/' + deviceId,
    method: 'post'
  })
}

export function unlockDevice(deviceId) {
  return request({
    url: '/ygb/device/unlock/' + deviceId,
    method: 'post'
  })
}

export function authorizeDevice(data) {
  return request({
    url: '/ygb/device/authorize',
    method: 'post',
    data
  })
}

export function heartbeatDevice(data) {
  return request({
    url: '/ygb/device/heartbeat',
    method: 'post',
    data
  })
}

export function aiEventDevice(data) {
  return request({
    url: '/ygb/device/aiEvent',
    method: 'post',
    data
  })
}

export function batchLockDevice(data) {
  return request({
    url: '/ygb/device/batch/lock',
    method: 'post',
    data
  })
}

export function batchUnlockDevice(data) {
  return request({
    url: '/ygb/device/batch/unlock',
    method: 'post',
    data
  })
}

export function batchAuthorizeDevice(data) {
  return request({
    url: '/ygb/device/batch/authorize',
    method: 'post',
    data
  })
}
