<template>
  <el-dialog
    v-model="visible"
    title="驾驶舱配置"
    width="720px"
    class="cockpit-config-dialog"
    append-to-body
    destroy-on-close
    align-center
  >
    <div class="config-shell">
      <div class="config-intro">
        <h3>布局与地图参数</h3>
        <p>按当前驾驶舱视角保存模块显隐、地图视角和自动刷新节奏。</p>
      </div>

      <el-form label-position="top" class="config-form">
        <div class="config-grid">
          <section class="config-section">
            <div class="config-section__title">显示内容</div>
            <el-form-item label="面板开关">
              <el-checkbox-group v-model="draft.visiblePanels" class="config-check-group">
                <el-checkbox v-for="item in panelOptions" :key="item.key" :label="item.key">{{ item.label }}</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            <el-form-item label="指标卡片">
              <el-checkbox-group v-model="draft.visibleMetrics" class="config-check-group">
                <el-checkbox v-for="item in metricOptions" :key="item.key" :label="item.key">{{ item.label }}</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </section>

          <section class="config-section">
            <div class="config-section__title">地图设置</div>
            <div class="config-map-grid">
              <el-form-item label="中心经度">
                <el-input-number v-model="draft.mapCenterLng" :precision="4" :step="0.01" :min="109" :max="118" />
              </el-form-item>
              <el-form-item label="中心纬度">
                <el-input-number v-model="draft.mapCenterLat" :precision="4" :step="0.01" :min="20" :max="26" />
              </el-form-item>
            </div>
            <el-form-item label="地图缩放">
              <el-input-number v-model="draft.mapZoom" :min="4" :max="14" />
            </el-form-item>
            <el-form-item label="自动刷新">
              <el-radio-group v-model="draft.refreshSeconds" class="config-radio-group">
                <el-radio v-for="item in refreshOptions" :key="item.value" :value="item.value">{{ item.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </section>
        </div>
      </el-form>
    </div>

    <template #footer>
      <div class="config-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="emit('save')">保存并生效</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed } from "vue"
import { COCKPIT_PANEL_OPTIONS, REFRESH_INTERVAL_OPTIONS } from "@/views/cockpit/useCockpitConfig"

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  draft: { type: Object, required: true },
  metricOptions: { type: Array, default: () => [] },
})

const emit = defineEmits(["update:modelValue", "save"])

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit("update:modelValue", value),
})

const panelOptions = COCKPIT_PANEL_OPTIONS
const refreshOptions = REFRESH_INTERVAL_OPTIONS
</script>

<style scoped lang="scss">
.cockpit-config-dialog :deep(.el-dialog) {
  overflow: hidden;
  border: 1px solid rgba(0, 229, 255, 0.22);
  background:
    linear-gradient(180deg, rgba(7, 24, 46, 0.96), rgba(4, 14, 30, 0.98)),
    radial-gradient(circle at 50% 0%, rgba(0, 229, 255, 0.08), transparent 46%);
  box-shadow: 0 0 36px rgba(0, 229, 255, 0.16);
}

.cockpit-config-dialog :deep(.el-dialog__header) {
  margin: 0;
  padding: 16px 20px 12px;
  border-bottom: 1px solid rgba(0, 229, 255, 0.12);
  background: linear-gradient(180deg, rgba(0, 229, 255, 0.08), rgba(0, 229, 255, 0.02));
}

.cockpit-config-dialog :deep(.el-dialog__title) {
  color: #ebfbff;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 0.04em;
}

.cockpit-config-dialog :deep(.el-dialog__body) {
  padding: 18px 20px 10px;
  color: rgba(207, 228, 233, 0.82);
}

.cockpit-config-dialog :deep(.el-dialog__footer) {
  padding: 0 20px 18px;
}

.cockpit-config-dialog :deep(.el-form-item) {
  margin-bottom: 16px;
}

.cockpit-config-dialog :deep(.el-form-item__label),
.cockpit-config-dialog :deep(.el-checkbox__label),
.cockpit-config-dialog :deep(.el-radio__label) {
  color: #ebfbff;
}

.cockpit-config-dialog :deep(.el-form-item__label) {
  padding-bottom: 8px;
  font-size: 12px;
}

.cockpit-config-dialog :deep(.el-checkbox),
.cockpit-config-dialog :deep(.el-radio) {
  margin-right: 12px;
  margin-bottom: 8px;
}

.cockpit-config-dialog :deep(.el-checkbox__input.is-checked .el-checkbox__inner),
.cockpit-config-dialog :deep(.el-radio__input.is-checked .el-radio__inner) {
  border-color: #00e5ff;
  background: #00e5ff;
}

.cockpit-config-dialog :deep(.el-checkbox__input.is-checked + .el-checkbox__label),
.cockpit-config-dialog :deep(.el-radio__input.is-checked + .el-radio__label) {
  color: #00ffc8;
}

.cockpit-config-dialog :deep(.el-input-number) {
  width: 100%;
}

.cockpit-config-dialog :deep(.el-input-number__decrease),
.cockpit-config-dialog :deep(.el-input-number__increase),
.cockpit-config-dialog :deep(.el-input__wrapper),
.cockpit-config-dialog :deep(.el-input-number__input-wrap) {
  background: rgba(5, 19, 36, 0.92);
  box-shadow: inset 0 0 0 1px rgba(0, 229, 255, 0.18);
  color: #ecfcff;
}

.cockpit-config-dialog :deep(.el-input-number__decrease),
.cockpit-config-dialog :deep(.el-input-number__increase) {
  color: rgba(215, 241, 248, 0.86);
}

.cockpit-config-dialog :deep(.el-button) {
  min-width: 96px;
}

.cockpit-config-dialog :deep(.el-button--default) {
  border-color: rgba(0, 229, 255, 0.18);
  background: rgba(0, 229, 255, 0.04);
  color: #dffaff;
}

.cockpit-config-dialog :deep(.el-button--primary) {
  border-color: rgba(0, 229, 255, 0.5);
  background: linear-gradient(90deg, rgba(0, 151, 183, 0.92), rgba(0, 229, 255, 0.92));
  color: #04131f;
}

.config-shell {
  display: grid;
  gap: 16px;
}

.config-intro {
  padding: 12px 14px;
  border: 1px solid rgba(0, 229, 255, 0.12);
  background: linear-gradient(180deg, rgba(8, 29, 54, 0.74), rgba(5, 18, 34, 0.9));
}

.config-intro h3 {
  margin: 0 0 4px;
  color: #ebfbff;
  font-size: 15px;
  font-weight: 700;
}

.config-intro p {
  margin: 0;
  color: rgba(205, 226, 232, 0.72);
  font-size: 12px;
  line-height: 1.5;
}

.config-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.08fr) minmax(0, 0.92fr);
  gap: 14px;
}

.config-section {
  padding: 14px;
  border: 1px solid rgba(0, 229, 255, 0.12);
  background: linear-gradient(180deg, rgba(8, 29, 54, 0.76), rgba(5, 18, 34, 0.92));
}

.config-section__title {
  margin-bottom: 14px;
  color: #00e5ff;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.06em;
}

.config-check-group,
.config-radio-group {
  display: flex;
  flex-wrap: wrap;
}

.config-map-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 12px;
}

.config-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 900px) {
  .config-grid,
  .config-map-grid {
    grid-template-columns: 1fr;
  }
}
</style>
