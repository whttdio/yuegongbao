<template>
  <el-dialog
    v-model="visible"
    title="驾驶舱配置"
    width="560px"
    class="cockpit-config-dialog"
    append-to-body
    destroy-on-close
  >
    <el-form label-width="120px">
      <el-form-item label="显示模块">
        <el-checkbox-group v-model="draft.visiblePanels">
          <el-checkbox v-for="item in panelOptions" :key="item.key" :label="item.key">{{ item.label }}</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="指标卡片">
        <el-checkbox-group v-model="draft.visibleMetrics">
          <el-checkbox v-for="item in metricOptions" :key="item.key" :label="item.key">{{ item.label }}</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="地图中心经度">
        <el-input-number v-model="draft.mapCenterLng" :precision="4" :step="0.01" :min="109" :max="118" />
      </el-form-item>
      <el-form-item label="地图中心纬度">
        <el-input-number v-model="draft.mapCenterLat" :precision="4" :step="0.01" :min="20" :max="26" />
      </el-form-item>
      <el-form-item label="地图缩放">
        <el-input-number v-model="draft.mapZoom" :min="4" :max="14" />
      </el-form-item>
      <el-form-item label="自动刷新">
        <el-radio-group v-model="draft.refreshSeconds">
          <el-radio v-for="item in refreshOptions" :key="item.value" :value="item.value">{{ item.label }}</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="emit('save')">保存并生效</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed } from 'vue'
import { COCKPIT_PANEL_OPTIONS, REFRESH_INTERVAL_OPTIONS } from '@/views/cockpit/useCockpitConfig'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  draft: { type: Object, required: true },
  metricOptions: { type: Array, default: () => [] }
})

const emit = defineEmits(['update:modelValue', 'save'])

const visible = computed({
  get: () => props.modelValue,
  set: value => emit('update:modelValue', value)
})

const panelOptions = COCKPIT_PANEL_OPTIONS
const refreshOptions = REFRESH_INTERVAL_OPTIONS
</script>

<style scoped lang="scss">
.cockpit-config-dialog :deep(.el-checkbox) {
  margin-right: 14px;
  margin-bottom: 8px;
}
</style>
