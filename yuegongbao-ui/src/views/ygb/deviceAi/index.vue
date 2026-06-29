<template>
  <device-subledger-page :config="config" />
</template>

<script setup>
import DeviceSubledgerPage from '@/views/ygb/shared/DeviceSubledgerPage.vue'
import { getDeviceSubledgerSummary, listDeviceSubledger } from '@/api/ygb/device'

const viewCode = 'AI'
const config = {
  title: 'AI 设备',
  eyebrow: '设备子视图',
  description: '聚焦 AI 设备采集、在线、授权和异常留痕。',
  tip: '聚焦 AI 智能监测设备的接入、在线、授权和预警联动状态。',
  permPrefix: 'ygb:device',
  filePrefix: 'device_ai',
  defaultQueryParams: { deviceType: '3' },
  routeQueryFields: ['regionCode', 'enterpriseId', 'deviceCode', 'deviceName', 'deviceStatus', 'authStatus'],
  listApi: query => listDeviceSubledger(viewCode, query),
  summaryApi: query => getDeviceSubledgerSummary(viewCode, query),
  exportUrl: 'ygb/device/export',
  columns: [
    { label: '设备编码', prop: 'deviceCode', width: 160 },
    { label: '设备名称', prop: 'deviceName', minWidth: 180 },
    { label: '所属企业', prop: 'enterpriseName', minWidth: 180 },
    { label: '区域', prop: 'regionCode', minWidth: 140, type: 'region' },
    { label: '设备状态', prop: 'deviceStatus', width: 110, type: 'deviceStatus' },
    { label: '授权状态', prop: 'authStatus', width: 110, type: 'authStatus' },
    { label: '最后心跳', prop: 'lastHeartbeat', width: 170, type: 'time' }
  ]
}
</script>
