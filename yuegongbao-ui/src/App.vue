<template>
  <router-view />
</template>

<script setup>
import useSettingsStore from '@/store/modules/settings'
import { handleThemeStyle } from '@/utils/theme'
import { startWorkbenchSectionFilter, syncWorkbenchSectionFilter } from '@/utils/workbenchSectionFilter'

let stopWorkbenchSectionFilter = () => {}

onMounted(() => {
  nextTick(() => {
    handleThemeStyle(useSettingsStore().theme)
    syncWorkbenchSectionFilter()
    stopWorkbenchSectionFilter = startWorkbenchSectionFilter()
  })
})

onBeforeUnmount(() => {
  stopWorkbenchSectionFilter()
})
</script>
