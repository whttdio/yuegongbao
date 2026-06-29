<template>
  <device-subledger-page :config="config" />
</template>

<script setup>
import DeviceSubledgerPage from '@/views/ygb/shared/DeviceSubledgerPage.vue'
import { getDeviceSubledgerSummary, listDeviceSubledger } from '@/api/ygb/device'

const viewCode = 'CHIP'
const config = {
  title: '芯片设备',
  eyebrow: '设备子视图',
  description: '聚焦芯片设备、物联卡和授权状态。',
  tip: '聚焦芯片设备的授权、安装、在线和运维办理状态。',
  permPrefix: 'ygb:device',
  filePrefix: 'device_chip',
  defaultQueryParams: { deviceType: '2' },
  routeQueryFields: ['regionCode', 'enterpriseId', 'deviceCode', 'deviceName', 'deviceStatus', 'authStatus'],
  listApi: query => listDeviceSubledger(viewCode, query),
  summaryApi: query => getDeviceSubledgerSummary(viewCode, query),
  exportUrl: 'ygb/device/export',
  columns: [
    { label: '设备编码', prop: 'deviceCode', width: 160 },
    { label: '设备名称', prop: 'deviceName', minWidth: 180 },
    { label: '芯片ID', prop: 'chipId', width: 140 },
    { label: '物联卡号', prop: 'simCardNo', width: 160 },
    { label: '所属企业', prop: 'enterpriseName', minWidth: 180 },
    { label: '设备状态', prop: 'deviceStatus', width: 110, type: 'deviceStatus' },
    { label: '授权状态', prop: 'authStatus', width: 110, type: 'authStatus' }
  ]
}
</script>
