<template>
  <footer class="cockpit-config-bar">
    <div class="cockpit-config-bar__section cockpit-config-bar__section--panels">
      <span class="cockpit-config-bar__title">驾驶舱配置</span>
      <div class="cockpit-config-bar__checks">
        <label v-for="item in panelOptions" :key="item.key" class="cockpit-config-bar__check">
          <input
            type="checkbox"
            :checked="visiblePanels.includes(item.key)"
            @change="emit('toggle-panel', item.key, $event.target.checked)"
          />
          <span>{{ item.shortLabel || item.label }}</span>
        </label>
      </div>
    </div>

    <div class="cockpit-config-bar__section cockpit-config-bar__section--filters">
      <span class="cockpit-config-bar__label">检索条件</span>

      <div class="cockpit-config-bar__field">
        <span>区域</span>
        <el-select
          :model-value="regionCode"
          class="cockpit-config-bar__select cockpit-control"
          size="small"
          @update:model-value="emit('update:regionCode', $event)"
        >
          <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </div>

      <div class="cockpit-config-bar__field">
        <span>月份</span>
        <el-date-picker
          :model-value="statMonth"
          class="cockpit-config-bar__select cockpit-control cockpit-config-bar__select--month"
          type="month"
          size="small"
          value-format="YYYY-MM"
          format="YYYY-MM"
          @update:model-value="emit('update:statMonth', $event)"
        />
      </div>

      <div class="cockpit-config-bar__field">
        <span>周期</span>
        <el-select
          :model-value="days"
          class="cockpit-config-bar__select cockpit-control cockpit-config-bar__select--short"
          size="small"
          @update:model-value="emit('update:days', $event)"
        >
          <el-option v-for="item in dayOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </div>
    </div>

    <div class="cockpit-config-bar__section cockpit-config-bar__section--actions">
      <div class="cockpit-config-bar__field">
        <span>自动刷新</span>
        <el-select
          :model-value="refreshSeconds"
          class="cockpit-config-bar__select cockpit-config-bar__select--refresh cockpit-control"
          size="small"
          @update:model-value="emit('update:refreshSeconds', $event)"
        >
          <el-option v-for="item in refreshOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </div>

      <button type="button" class="cockpit-config-bar__refresh" @click="emit('refresh')">
        <el-icon class="cockpit-config-bar__refresh-icon"><Refresh /></el-icon>
        立即刷新
      </button>

      <el-button plain size="small" :icon="Setting" @click="emit('open-advanced')">高级</el-button>
      <el-button plain size="small" :icon="Download" @click="emit('export')" v-hasPermi="['ygb:cockpit:export']">导出</el-button>
      <el-button plain size="small" @click="emit('toggle-immersive')">
        {{ isImmersive ? "退出全屏" : "全屏" }}
      </el-button>

      <span class="cockpit-config-bar__refresh-label">{{ lastRefreshLabel }}</span>
    </div>
  </footer>
</template>

<script setup>
import { Download, Refresh, Setting } from "@element-plus/icons-vue"
import { COCKPIT_PANEL_OPTIONS, REFRESH_INTERVAL_OPTIONS } from "@/views/cockpit/useCockpitConfig"

defineProps({
  visiblePanels: { type: Array, default: () => [] },
  regionCode: { type: String, default: "" },
  statMonth: { type: String, default: "" },
  days: { type: Number, default: 7 },
  refreshSeconds: { type: Number, default: 0 },
  regionOptions: { type: Array, default: () => [] },
  dayOptions: { type: Array, default: () => [] },
  lastRefreshLabel: { type: String, default: "" },
  isImmersive: { type: Boolean, default: false },
})

const emit = defineEmits([
  "toggle-panel",
  "update:regionCode",
  "update:statMonth",
  "update:days",
  "update:refreshSeconds",
  "refresh",
  "export",
  "open-advanced",
  "toggle-immersive",
])

const SHORT_LABEL_MAP = Object.freeze({
  "panel-metrics": "大屏指标",
  "panel-risk-ranking": "风险分类",
  "panel-trend": "趋势分析",
  "panel-map": "地图",
  "panel-table": "点位明细",
  "panel-region-summary": "区域态势",
  "panel-warning": "实时预警",
})

const panelOptions = COCKPIT_PANEL_OPTIONS.map((item) => ({
  ...item,
  shortLabel: SHORT_LABEL_MAP[item.key] || item.label,
}))

const refreshOptions = REFRESH_INTERVAL_OPTIONS
</script>

<style scoped lang="scss">
.cockpit-config-bar {
  position: relative;
  z-index: 10;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 4px;
  padding: 6px 10px;
  min-height: 52px;
  flex-shrink: 0;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 10px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.08) 0%, transparent 36%),
    linear-gradient(180deg, rgba(14, 28, 48, 0.38), rgba(8, 18, 34, 0.46));
  backdrop-filter: blur(22px) saturate(168%);
  -webkit-backdrop-filter: blur(22px) saturate(168%);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.12),
    0 -8px 28px rgba(0, 0, 0, 0.24),
    0 0 24px rgba(0, 229, 255, 0.06);
  font-size: 11px;
  color: rgba(190, 216, 228, 0.84);
}

.cockpit-config-bar__section {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
  flex-shrink: 0;
}

.cockpit-config-bar__section--panels {
  flex: 1 1 auto;
  min-width: 0;
  padding-right: 8px;
  border-right: 1px solid rgba(0, 180, 255, 0.1);
}

.cockpit-config-bar__section--filters {
  padding: 0 8px;
  border-right: 1px solid rgba(0, 180, 255, 0.1);
}

.cockpit-config-bar__section--actions {
  flex-shrink: 0;
  gap: 6px;
  margin-left: auto;
}

.cockpit-config-bar__title,
.cockpit-config-bar__label {
  flex-shrink: 0;
  font-weight: 600;
  color: #e8fbff;
  white-space: nowrap;
}

.cockpit-config-bar__checks {
  min-width: 0;
  flex: 1;
  display: flex;
  flex-wrap: nowrap;
  align-items: center;
  gap: 4px;
  overflow-x: auto;
  scrollbar-width: none;
}

.cockpit-config-bar__checks::-webkit-scrollbar {
  display: none;
}

.cockpit-config-bar__check {
  display: inline-flex;
  flex-shrink: 0;
  align-items: center;
  gap: 3px;
  padding: 2px 4px;
  border: 1px solid rgba(0, 229, 255, 0.08);
  background: rgba(0, 229, 255, 0.03);
  cursor: pointer;
  user-select: none;
  white-space: nowrap;
  font-size: 11px;
  color: rgba(205, 230, 236, 0.82);
}

.cockpit-config-bar__check input {
  accent-color: #00d4ff;
  width: 12px;
  height: 12px;
  cursor: pointer;
}

.cockpit-config-bar__field {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  white-space: nowrap;
}

.cockpit-config-bar__field > span {
  color: rgba(155, 211, 227, 0.74);
  font-size: 11px;
}

.cockpit-config-bar__select {
  width: 108px;
}

.cockpit-config-bar__select--short {
  width: 92px;
}

.cockpit-config-bar__select--month {
  width: 118px;
}

.cockpit-config-bar__select--refresh {
  width: 112px;
}

.cockpit-config-bar__refresh {
  display: inline-flex;
  flex-shrink: 0;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border: 1px solid rgba(0, 200, 255, 0.32);
  border-radius: 4px;
  background: rgba(0, 180, 255, 0.14);
  color: #00dfff;
  font-size: 11px;
  cursor: pointer;
  transition: background 0.18s ease, box-shadow 0.18s ease;
}

.cockpit-config-bar__refresh-icon {
  font-size: 13px;
}

.cockpit-config-bar__refresh:hover {
  background: rgba(0, 180, 255, 0.22);
  box-shadow: 0 0 10px rgba(0, 180, 255, 0.18);
}

.cockpit-config-bar__refresh-label {
  flex-shrink: 0;
  font-size: 10px;
  color: rgba(148, 169, 196, 0.72);
  white-space: nowrap;
}

.cockpit-config-bar__section--actions :deep(.el-button) {
  margin-left: 0;
}

.cockpit-config-bar :deep(.el-input__wrapper),
.cockpit-config-bar :deep(.el-select__wrapper) {
  background: rgba(6, 20, 38, 0.42) !important;
  backdrop-filter: blur(12px) saturate(165%);
  -webkit-backdrop-filter: blur(12px) saturate(165%);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.1),
    inset 0 0 0 1px rgba(0, 200, 255, 0.22) !important;
}

.cockpit-config-bar :deep(.el-input__inner),
.cockpit-config-bar :deep(.el-select__selected-item),
.cockpit-config-bar :deep(.el-range-input) {
  color: #e8fbff !important;
  font-weight: 600;
  -webkit-text-fill-color: #e8fbff;
}

.cockpit-config-bar :deep(.el-select__placeholder) {
  color: rgba(180, 214, 224, 0.76) !important;
  font-weight: 400;
  -webkit-text-fill-color: rgba(180, 214, 224, 0.76);
}

.cockpit-config-bar :deep(.el-input__prefix),
.cockpit-config-bar :deep(.el-input__suffix),
.cockpit-config-bar :deep(.el-select__caret),
.cockpit-config-bar :deep(.el-select__icon) {
  color: rgba(0, 223, 255, 0.88);
}

@media (max-width: 1680px) {
  .cockpit-config-bar__refresh-label {
    display: none;
  }
}

@media (max-width: 1440px) {
  .cockpit-config-bar {
    overflow-x: auto;
    scrollbar-width: none;
  }

  .cockpit-config-bar::-webkit-scrollbar {
    display: none;
  }
}
</style>
