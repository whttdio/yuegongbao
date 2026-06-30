<template>
  <div class="enterprise-table">
    <div class="enterprise-table__toolbar">
      <span class="enterprise-table__count">可视范围 {{ filteredRows.length }} 条</span>
      <el-select v-model="riskFilter" class="enterprise-table__filter" placeholder="风险等级" clearable>
        <el-option label="全部" value="" />
        <el-option label="红码" value="RED" />
        <el-option label="黄码" value="YELLOW" />
        <el-option label="绿码" value="GREEN" />
      </el-select>
    </div>
    <div class="enterprise-table__scroll">
      <table class="enterprise-table__grid">
        <thead>
          <tr>
            <th>企业名称</th>
            <th>风险码等级</th>
            <th>区域</th>
            <th>参保率</th>
            <th>违规次数</th>
            <th>设备数量</th>
            <th>当前预警状态</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="row in pagedRows"
            :key="row.id"
            :class="rowRowClass(row)"
            @click="emit('row-click', row)"
          >
            <td>{{ row.enterpriseName }}</td>
            <td><span :class="['risk-tag', `risk-tag--${row.riskTone}`]">{{ row.riskLabel }}</span></td>
            <td>{{ row.regionName }}</td>
            <td>{{ row.insuranceRateText }}</td>
            <td>{{ row.violationCountText }}</td>
            <td>{{ row.deviceCountText }}</td>
            <td>{{ row.warningStatusText }}</td>
          </tr>
          <tr v-if="!pagedRows.length">
            <td colspan="7" class="enterprise-table__empty">当前可视范围内暂无企业点位</td>
          </tr>
        </tbody>
      </table>
    </div>
    <div class="enterprise-table__pager">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
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
import { computed, ref, watch } from 'vue'

const props = defineProps({
  rows: { type: Array, default: () => [] }
})

const emit = defineEmits(['row-click'])

const riskFilter = ref('')
const pageNum = ref(1)
const pageSize = ref(10)

const filteredRows = computed(() => {
  if (!riskFilter.value) return props.rows
  return props.rows.filter(item => item.colorCode === riskFilter.value)
})

const pagedRows = computed(() => {
  const start = (pageNum.value - 1) * pageSize.value
  return filteredRows.value.slice(start, start + pageSize.value)
})

watch(() => [props.rows.length, riskFilter.value], () => {
  pageNum.value = 1
})

function rowRowClass(row) {
  return `enterprise-table__row enterprise-table__row--${row.riskTone}`
}
</script>

<style scoped lang="scss">
.enterprise-table {
  display: grid;
  gap: 10px;
  min-height: 220px;
}

.enterprise-table__toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.enterprise-table__count {
  color: rgba(177, 214, 237, 0.72);
  font-size: 12px;
}

.enterprise-table__filter {
  width: 140px;
}

.enterprise-table__scroll {
  overflow: auto;
  border: 1px solid rgba(82, 230, 255, 0.12);
}

.enterprise-table__grid {
  width: 100%;
  min-width: 760px;
  border-collapse: collapse;
  font-size: 12px;
}

.enterprise-table__grid th,
.enterprise-table__grid td {
  padding: 10px 12px;
  border-bottom: 1px solid rgba(82, 230, 255, 0.08);
  text-align: left;
  white-space: nowrap;
}

.enterprise-table__grid th {
  color: rgba(177, 214, 237, 0.72);
  background: rgba(6, 17, 31, 0.88);
  font-weight: 600;
}

.enterprise-table__row {
  cursor: pointer;
  background: rgba(7, 18, 32, 0.72);
  transition: background 0.18s ease;
}

.enterprise-table__row:hover {
  background: rgba(17, 42, 68, 0.72);
}

.enterprise-table__row--red {
  box-shadow: inset 3px 0 0 rgba(255, 111, 145, 0.92);
}

.enterprise-table__row--yellow {
  box-shadow: inset 3px 0 0 rgba(255, 190, 98, 0.92);
}

.enterprise-table__row--green {
  box-shadow: inset 3px 0 0 rgba(61, 242, 178, 0.92);
}

.enterprise-table__empty {
  text-align: center;
  color: rgba(177, 214, 237, 0.6);
}

.risk-tag {
  display: inline-flex;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
}

.risk-tag--red {
  color: #03111f;
  background: rgba(255, 111, 145, 0.94);
}

.risk-tag--yellow {
  color: #03111f;
  background: rgba(255, 190, 98, 0.94);
}

.risk-tag--green {
  color: #03111f;
  background: rgba(61, 242, 178, 0.94);
}

.risk-tag--normal {
  color: #ebf8ff;
  background: rgba(82, 230, 255, 0.24);
}

.enterprise-table__pager {
  display: flex;
  justify-content: flex-end;
}

.enterprise-table :deep(.el-pagination.is-background .btn-next),
.enterprise-table :deep(.el-pagination.is-background .btn-prev),
.enterprise-table :deep(.el-pagination.is-background .el-pager li) {
  background: rgba(6, 17, 31, 0.88);
  color: #ebf8ff;
}
</style>
