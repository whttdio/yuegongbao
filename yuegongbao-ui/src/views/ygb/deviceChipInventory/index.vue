<template>
  <device-subledger-page :config="config" />
</template>

<script setup>
import DeviceSubledgerPage from '@/views/ygb/shared/DeviceSubledgerPage.vue'
import { getDeviceChipInventory, getDeviceChipInventorySummary, listDeviceChipInventory } from '@/api/ygb/device'

const config = {
  title: '芯片库存',
  eyebrow: '设备子台账',
  description: '查看芯片编号、授权状态、库存归属和出入库办理情况。',
  permPrefix: 'ygb:device',
  filePrefix: 'device_chip_inventory',
  routeQueryFields: ['regionCode', 'enterpriseId', 'deviceCode', 'deviceName', 'deviceStatus', 'authStatus'],
  listApi: query => listDeviceChipInventory(query),
  summaryApi: query => getDeviceChipInventorySummary(query),
  detailApi: id => getDeviceChipInventory(id),
  exportUrl: 'ygb/device/chipInventory/export',
  columns: [
    { label: '设备编码', prop: 'deviceCode', width: 160 },
    { label: '设备名称', prop: 'deviceName', minWidth: 180 },
    { label: '芯片ID', prop: 'chipId', width: 160 },
    { label: '所属企业', prop: 'enterpriseName', minWidth: 180 },
    { label: '授权状态', prop: 'authStatus', width: 110, type: 'authStatus' },
    { label: '设备状态', prop: 'deviceStatus', width: 110, type: 'deviceStatus' }
  ]
}
</script>
