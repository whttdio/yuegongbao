<template>
  <device-event-page :config="config" />
</template>

<script setup>
import DeviceEventPage from '@/views/ygb/shared/DeviceEventPage.vue'
import { listDeviceUninstallAlert } from '@/api/ygb/device'

const config = {
  title: '拆卸报警',
  eyebrow: '设备子台账',
  description: '集中查看设备拆卸、拆改和围栏破坏相关报警事件，支撑运维核查和处置闭环。',
  permPrefix: 'ygb:device',
  filePrefix: 'device_uninstall_alert',
  listApi: query => listDeviceUninstallAlert(query),
  exportUrl: 'ygb/device/uninstallAlert/export'
}
</script>
