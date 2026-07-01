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
      <button type="button" class="cockpit-config-bar__refresh" @click="emit('refresh')">立即刷新</button>
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
import { Download, Setting } from "@element-plus/icons-vue"
import { COCKPIT_PANEL_OPTIONS } from "@/views/cockpit/useCockpitConfig"

defineProps({
  visiblePanels: { type: Array, default: () => [] },
  regionCode: { type: String, default: "" },
  statMonth: { type: String, default: "" },
  days: { type: Number, default: 7 },
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
</script>

<style scoped lang="scss">
.cockpit-config-bar {
  display: grid;
  grid-template-columns: minmax(0, 1.7fr) minmax(360px, 1.15fr) auto;
  gap: 10px;
  padding: 8px 12px;
  min-height: 56px;
  flex-shrink: 0;
  border: 1px solid rgba(0, 180, 255, 0.12);
  border-radius: 10px;
  background:
    linear-gradient(180deg, rgba(14, 24, 40, 0.94), rgba(8, 16, 30, 0.92));
  backdrop-filter: blur(10px);
  font-size: 11px;
  color: rgba(190, 216, 228, 0.84);
}

.cockpit-config-bar__section {
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.cockpit-config-bar__section--panels {
  flex-wrap: nowrap;
  overflow: hidden;
  padding-right: 10px;
  border-right: 1px solid rgba(0, 180, 255, 0.1);
}

.cockpit-config-bar__section--filters {
  padding-right: 10px;
  border-right: 1px solid rgba(0, 180, 255, 0.1);
}

.cockpit-config-bar__section--actions {
  justify-content: flex-end;
  gap: 8px;
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
  gap: 6px;
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
  gap: 4px;
  padding: 3px 5px;
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
  width: 13px;
  height: 13px;
  cursor: pointer;
}

.cockpit-config-bar__field {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
}

.cockpit-config-bar__field > span {
  color: rgba(155, 211, 227, 0.74);
  font-size: 11px;
}

.cockpit-config-bar__select {
  width: 120px;
}

.cockpit-config-bar__select--short {
  width: 104px;
}

.cockpit-config-bar__select--month {
  width: 132px;
}

.cockpit-config-bar__refresh {
  padding: 4px 12px;
  border: 1px solid rgba(0, 200, 255, 0.24);
  border-radius: 4px;
  background: rgba(0, 180, 255, 0.1);
  color: #00dfff;
  font-size: 11px;
  cursor: pointer;
  transition: background 0.18s ease, box-shadow 0.18s ease;
}

.cockpit-config-bar__refresh:hover {
  background: rgba(0, 180, 255, 0.18);
  box-shadow: 0 0 10px rgba(0, 180, 255, 0.18);
}

.cockpit-config-bar__refresh-label {
  margin-left: 4px;
  font-size: 10px;
  color: rgba(148, 169, 196, 0.72);
  white-space: nowrap;
}

@media (max-width: 1600px) {
  .cockpit-config-bar {
    grid-template-columns: 1fr;
  }

  .cockpit-config-bar__section--panels,
  .cockpit-config-bar__section--filters {
    padding-right: 0;
    border-right: none;
  }

  .cockpit-config-bar__section--actions {
    justify-content: flex-start;
    flex-wrap: wrap;
  }
}

@media (max-width: 980px) {
  .cockpit-config-bar__section {
    flex-wrap: wrap;
  }
}
</style>
