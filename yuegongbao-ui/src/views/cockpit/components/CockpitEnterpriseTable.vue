<template>
  <div class="enterprise-table" :class="{ 'enterprise-table--compact': compact }">
    <div class="enterprise-table__toolbar">
      <div class="enterprise-table__summary">
        <span class="enterprise-table__count">可视范围 {{ filteredRows.length }} 条</span>
        <span class="enterprise-table__hint">点击企业行可联动地图高亮点位</span>
      </div>

      <div class="enterprise-table__toolbar-actions">
        <el-select v-model="riskFilter" class="enterprise-table__filter cockpit-control" placeholder="风险等级" clearable>
          <el-option label="全部" value="" />
          <el-option label="红码 / 高风险" value="RED" />
          <el-option label="黄码 / 预警" value="YELLOW" />
          <el-option label="绿码 / 正常" value="GREEN" />
        </el-select>
      </div>
    </div>

    <div class="enterprise-table__scroll">
      <table class="enterprise-table__grid">
        <thead>
          <tr>
            <th>企业名称</th>
            <th>风险等级</th>
            <th>区域</th>
            <th>参保率</th>
            <th>违规次数</th>
            <th>设备数量</th>
            <th>当前预警状态</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in pagedRows" :key="row.id" :class="rowRowClass(row)" @click="emit('row-click', row)">
            <td class="enterprise-table__name-cell">
              <strong>{{ row.enterpriseName }}</strong>
            </td>
            <td>
              <span :class="['cockpit-risk-tag', `cockpit-risk-tag--${row.riskTone}`]">{{ row.riskLabel }}</span>
            </td>
            <td>{{ row.regionName }}</td>
            <td>{{ row.insuranceRateText }}</td>
            <td>{{ row.violationCountText }}</td>
            <td>{{ row.deviceCountText }}</td>
            <td>{{ row.warningStatusText }}</td>
          </tr>
          <tr v-if="!pagedRows.length">
            <td colspan="7">
              <div class="cockpit-empty enterprise-table__empty">
                <div class="cockpit-empty__icon" />
                <div class="cockpit-empty__title">当前视野范围内暂无企业点位</div>
                <div class="cockpit-empty__desc">调整地图缩放或切换区域后，表格会自动联动当前视野内的企业明细。</div>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="enterprise-table__pager">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        class="cockpit-pagination"
        :total="filteredRows.length"
        :page-sizes="[5, 10, 20]"
        layout="total, sizes, prev, pager, next"
        background
        small
      />
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from "vue"

const props = defineProps({
  rows: { type: Array, default: () => [] },
  compact: { type: Boolean, default: false },
})

const emit = defineEmits(["row-click"])

const riskFilter = ref("")
const pageNum = ref(1)
const pageSize = ref(10)

const filteredRows = computed(() => {
  if (!riskFilter.value) return props.rows
  return props.rows.filter((item) => item.colorCode === riskFilter.value)
})

const pagedRows = computed(() => {
  const start = (pageNum.value - 1) * pageSize.value
  return filteredRows.value.slice(start, start + pageSize.value)
})

watch(
  () => [props.rows.length, riskFilter.value],
  () => {
    pageNum.value = 1
  },
)

function rowRowClass(row) {
  return `enterprise-table__row enterprise-table__row--${row.riskTone}`
}
</script>

<style scoped lang="scss">
.enterprise-table {
  display: grid;
  gap: 8px;
  min-height: 0;
  height: 100%;
  grid-template-rows: auto minmax(0, 1fr) auto;
}

.enterprise-table__toolbar {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  padding-bottom: 2px;
}

.enterprise-table__summary {
  display: grid;
  gap: 4px;
  min-width: 0;
}

.enterprise-table__count {
  color: rgba(220, 240, 245, 0.88);
  font-size: 12px;
  font-weight: 600;
}

.enterprise-table__hint {
  color: rgba(155, 212, 227, 0.68);
  font-size: 11px;
  line-height: 1.3;
}

.enterprise-table__toolbar-actions {
  flex-shrink: 0;
}

.enterprise-table__filter {
  width: 150px;
}

.enterprise-table__scroll {
  overflow: auto;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(4, 14, 28, 0.28);
  backdrop-filter: blur(16px) saturate(165%);
  -webkit-backdrop-filter: blur(16px) saturate(165%);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.06);
  min-height: 0;
  height: 100%;
}

.enterprise-table__grid {
  width: 100%;
  min-width: 760px;
  border-collapse: collapse;
  table-layout: fixed;
  font-size: 12px;
}

.enterprise-table__grid th,
.enterprise-table__grid td {
  padding: 9px 10px;
  border-bottom: 1px solid rgba(0, 229, 255, 0.08);
  text-align: left;
  vertical-align: middle;
}

.enterprise-table__grid th:nth-child(1),
.enterprise-table__grid td:nth-child(1) {
  width: 26%;
}

.enterprise-table__grid th:nth-child(2),
.enterprise-table__grid td:nth-child(2) {
  width: 12%;
}

.enterprise-table__grid th:nth-child(3),
.enterprise-table__grid td:nth-child(3) {
  width: 12%;
}

.enterprise-table__grid th:nth-child(4),
.enterprise-table__grid td:nth-child(4),
.enterprise-table__grid th:nth-child(5),
.enterprise-table__grid td:nth-child(5),
.enterprise-table__grid th:nth-child(6),
.enterprise-table__grid td:nth-child(6) {
  width: 11%;
}

.enterprise-table__grid th:nth-child(7),
.enterprise-table__grid td:nth-child(7) {
  width: 17%;
}

.enterprise-table__grid thead th {
  position: sticky;
  top: 0;
  z-index: 2;
  color: #77f5ff;
  background: linear-gradient(180deg, rgba(8, 38, 66, 0.96), rgba(6, 27, 49, 0.96));
  box-shadow: inset 0 -1px 0 rgba(0, 229, 255, 0.12);
  font-weight: 600;
  white-space: nowrap;
}

.enterprise-table__grid tbody td {
  color: rgba(227, 245, 249, 0.88);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.enterprise-table__name-cell strong {
  display: block;
  color: #eefcff;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.enterprise-table__row {
  cursor: pointer;
  transition: background 0.18s ease, box-shadow 0.18s ease;
  border-left: 3px solid transparent;
}

.enterprise-table__row:hover {
  background: rgba(0, 229, 255, 0.09);
  box-shadow: inset 0 0 0 1px rgba(0, 229, 255, 0.1);
}

.enterprise-table__row--red {
  background: rgba(255, 77, 79, 0.1);
  border-left-color: var(--risk-red, #ff4d4f);
  box-shadow: inset 4px 0 12px rgba(255, 77, 79, 0.12);
}

.enterprise-table__row--red:hover {
  background: rgba(255, 77, 79, 0.16);
  box-shadow:
    inset 4px 0 16px rgba(255, 77, 79, 0.18),
    inset 0 0 0 1px rgba(255, 77, 79, 0.14);
}

.enterprise-table__row--yellow {
  background: rgba(255, 204, 0, 0.09);
  border-left-color: var(--risk-yellow, #ffcc00);
  box-shadow: inset 4px 0 12px rgba(255, 204, 0, 0.1);
}

.enterprise-table__row--yellow:hover {
  background: rgba(255, 204, 0, 0.14);
}

.enterprise-table__row--green {
  background: rgba(0, 255, 200, 0.07);
  border-left-color: var(--risk-green, #00ffc8);
  box-shadow: inset 4px 0 12px rgba(0, 255, 200, 0.08);
}

.enterprise-table__row--green:hover {
  background: rgba(0, 255, 200, 0.12);
}

.enterprise-table__empty {
  border: none;
  background: transparent;
  min-height: 96px;
}

.enterprise-table__pager {
  display: flex;
  justify-content: flex-end;
  padding-top: 2px;
}

.enterprise-table__scroll::-webkit-scrollbar {
  width: 4px;
  height: 4px;
}

.enterprise-table__scroll::-webkit-scrollbar-thumb {
  background: rgba(0, 229, 255, 0.4);
}

.enterprise-table--compact .enterprise-table__toolbar {
  align-items: center;
}

.enterprise-table--compact .enterprise-table__hint {
  display: none;
}

.enterprise-table--compact .enterprise-table__grid th,
.enterprise-table--compact .enterprise-table__grid td {
  padding: 6px 8px;
  font-size: 11px;
}

.enterprise-table--compact .enterprise-table__grid thead th {
  font-size: 10px;
}

@media (max-width: 980px) {
  .enterprise-table__toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .enterprise-table__toolbar-actions {
    width: 100%;
  }

  .enterprise-table__filter {
    width: 100%;
  }
}
</style>
