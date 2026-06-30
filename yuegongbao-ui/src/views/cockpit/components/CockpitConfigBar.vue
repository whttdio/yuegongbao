<template>
  <footer class="cockpit-config-bar">
    <span class="cockpit-config-bar__title">驾驶舱配置</span>
    <span class="cockpit-config-bar__divider" />

    <label v-for="item in panelOptions" :key="item.key" class="cockpit-config-bar__check">
      <input
        type="checkbox"
        :checked="visiblePanels.includes(item.key)"
        @change="emit('toggle-panel', item.key, $event.target.checked)"
      />
      {{ item.shortLabel || item.label }}
    </label>

    <span class="cockpit-config-bar__divider" />

    <el-select
      :model-value="regionCode"
      class="cockpit-config-bar__select cockpit-control"
      size="small"
      @update:model-value="emit('update:regionCode', $event)"
    >
      <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
    </el-select>

    <el-date-picker
      :model-value="statMonth"
      class="cockpit-config-bar__select cockpit-control"
      type="month"
      size="small"
      value-format="YYYY-MM"
      format="YYYY-MM"
      @update:model-value="emit('update:statMonth', $event)"
    />

    <el-select
      :model-value="days"
      class="cockpit-config-bar__select cockpit-control cockpit-config-bar__select--short"
      size="small"
      @update:model-value="emit('update:days', $event)"
    >
      <el-option v-for="item in dayOptions" :key="item.value" :label="item.label" :value="item.value" />
    </el-select>

    <button type="button" class="cockpit-config-bar__refresh" @click="emit('refresh')">立即刷新</button>

    <el-button plain size="small" :icon="Setting" @click="emit('open-advanced')">高级</el-button>
    <el-button plain size="small" :icon="Download" @click="emit('export')" v-hasPermi="['ygb:cockpit:export']">导出</el-button>
    <el-button plain size="small" @click="emit('toggle-immersive')">
      {{ isImmersive ? "退出全屏" : "全屏" }}
    </el-button>

    <span class="cockpit-config-bar__refresh-label">{{ lastRefreshLabel }}</span>
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

const panelOptions = COCKPIT_PANEL_OPTIONS.map((item) => ({
  ...item,
  shortLabel: item.label.replace(/面板|流|表格|GIS /g, "").slice(0, 6),
}))
</script>

<style scoped lang="scss">
.cockpit-config-bar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px 12px;
  padding: 6px 12px;
  min-height: 34px;
  flex-shrink: 0;
  border: 1px solid rgba(0, 180, 255, 0.1);
  border-radius: 8px;
  background: rgba(10, 18, 34, 0.9);
  backdrop-filter: blur(8px);
  font-size: 11px;
  color: rgba(148, 169, 196, 0.88);
}

.cockpit-config-bar__title {
  font-weight: 600;
  color: var(--text-primary, #e6ecf5);
  white-space: nowrap;
}

.cockpit-config-bar__divider {
  width: 1px;
  height: 14px;
  background: rgba(0, 180, 255, 0.14);
  flex-shrink: 0;
}

.cockpit-config-bar__check {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  user-select: none;
  white-space: nowrap;
  font-size: 10px;
}

.cockpit-config-bar__check input {
  accent-color: #00d4ff;
  width: 13px;
  height: 13px;
  cursor: pointer;
}

.cockpit-config-bar__select {
  width: 120px;
}

.cockpit-config-bar__select--short {
  width: 108px;
}

.cockpit-config-bar__refresh {
  padding: 4px 12px;
  border: 1px solid rgba(0, 200, 255, 0.22);
  border-radius: 4px;
  background: rgba(0, 180, 255, 0.08);
  color: #00d4ff;
  font-size: 10px;
  cursor: pointer;
  transition: background 0.18s ease, box-shadow 0.18s ease;
}

.cockpit-config-bar__refresh:hover {
  background: rgba(0, 180, 255, 0.18);
  box-shadow: 0 0 10px rgba(0, 180, 255, 0.18);
}

.cockpit-config-bar__refresh-label {
  margin-left: auto;
  font-size: 10px;
  color: rgba(148, 169, 196, 0.72);
  white-space: nowrap;
}
</style>
