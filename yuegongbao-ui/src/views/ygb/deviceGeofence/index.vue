<template>
  <module-record-page :config="config" />
</template>

<script setup>
import ModuleRecordPage from '@/views/ygb/shared/ModuleRecordPage.vue'
import {
  addDeviceGeofence,
  delDeviceGeofence,
  getDeviceGeofence,
  getDeviceGeofenceSummary,
  listDeviceGeofence,
  updateDeviceGeofence
} from '@/api/ygb/device'

const config = {
  title: '电子围栏',
  description: '以扩展台账维护围栏名称、区域范围、关联企业和启停状态。',
  permPrefix: 'ygb:device',
  filePrefix: 'device_geofence',
  defaultCategoryCode: 'geofence',
  defaultSourceLabel: '设备管理',
  recordNameLabel: '围栏名称',
  recordNamePlaceholder: '请输入围栏名称',
  filters: ['regionCode', 'enterpriseId', 'recordName', 'workflowStatus', 'status'],
  routeQueryFields: ['regionCode', 'enterpriseId'],
  listApi: query => listDeviceGeofence(query),
  summaryApi: query => getDeviceGeofenceSummary(query),
  detailApi: id => getDeviceGeofence(id),
  addApi: data => addDeviceGeofence(data),
  updateApi: data => updateDeviceGeofence(data),
  deleteApi: ids => delDeviceGeofence(ids),
  exportUrl: 'ygb/device/geofence/export'
}
</script>
