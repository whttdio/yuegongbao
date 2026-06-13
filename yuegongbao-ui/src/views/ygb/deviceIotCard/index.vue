<template>
  <device-subledger-page :config="config" />
</template>

<script setup>
import DeviceSubledgerPage from '@/views/ygb/shared/DeviceSubledgerPage.vue'
import { getDeviceIotCard, getDeviceIotCardSummary, listDeviceIotCard } from '@/api/ygb/device'

const config = {
  title: '物联卡',
  eyebrow: '设备子台账',
  description: '直接从统一设备底表查看设备物联卡号、在线状态和归属企业。',
  permPrefix: 'ygb:device',
  filePrefix: 'device_iot_card',
  routeQueryFields: ['regionCode', 'enterpriseId', 'deviceCode', 'deviceName', 'deviceStatus', 'authStatus'],
  listApi: query => listDeviceIotCard(query),
  summaryApi: query => getDeviceIotCardSummary(query),
  detailApi: id => getDeviceIotCard(id),
  exportUrl: 'ygb/device/iotCard/export',
  columns: [
    { label: '设备编码', prop: 'deviceCode', width: 160 },
    { label: '设备名称', prop: 'deviceName', minWidth: 180 },
    { label: '物联卡号', prop: 'simCardNo', width: 180 },
    { label: '所属企业', prop: 'enterpriseName', minWidth: 180 },
    { label: '区域', prop: 'regionCode', minWidth: 140, type: 'region' },
    { label: '设备状态', prop: 'deviceStatus', width: 110, type: 'deviceStatus' }
  ]
}
</script>
