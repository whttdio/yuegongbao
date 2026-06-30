<template>
  <div class="risk-ranking-panel" :class="`risk-ranking-panel--${layout}`">
    <div v-if="layout !== 'chart'" class="risk-ranking-panel__table-wrap panel-body scrollable">
      <table v-if="tableRows.length" class="risk-table">
        <thead>
          <tr>
            <th>排名</th>
            <th>企业名称</th>
            <th>码</th>
            <th>参保率</th>
            <th>违规</th>
            <th>事故率</th>
            <th>风险值</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in tableRows" :key="row.key" @click="row.action && emit('open-module', row.action)">
            <td class="risk-table__rank">#{{ row.rank }}</td>
            <td class="risk-table__name">{{ row.enterpriseName }}</td>
            <td>
              <span :class="['risk-badge', row.tone]">{{ row.badgeLabel }}</span>
            </td>
            <td>{{ row.insuranceRateText }}</td>
            <td :class="{ 'risk-table__warn': row.violationCount > 7 }">{{ row.violationCount }}次</td>
            <td>{{ row.accidentRateText }}</td>
            <td>
              <span :class="['risk-table__score', `risk-table__score--${row.riskLevel}`]">{{ row.riskScore }}</span>
              <span class="risk-bar-wrap">
                <span class="risk-bar-fill" :class="row.riskLevel" :style="{ width: `${row.riskScore}%` }" />
              </span>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-else class="cockpit-empty risk-ranking-panel__empty">
        <div class="cockpit-empty__icon" />
        <div class="cockpit-empty__title">暂无红黄绿码排行数据</div>
        <div class="cockpit-empty__desc">企业赋码完成后将在此展示风险分层与 TOP 排行</div>
      </div>
    </div>
    <div v-if="layout !== 'table'" ref="chartRef" class="risk-ranking-panel__chart" />
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from "vue"
import * as echarts from "echarts"

const props = defineProps({
  creditSummary: { type: Object, default: () => ({}) },
  rankingList: { type: Array, default: () => [] },
  colors: { type: Object, default: () => ({}) },
  layout: {
    type: String,
    default: "full",
    validator: (value) => ["full", "table", "chart"].includes(value),
  },
})

const emit = defineEmits(["open-module"])

const chartRef = ref(null)
let chartInstance = null

const RISK_COLORS = Object.freeze({
  red: "#ff4d4f",
  yellow: "#ffcc00",
  green: "#00ffc8",
})

const donutData = computed(() => {
  const red = Number(props.creditSummary?.redCount || 0)
  const yellow = Number(props.creditSummary?.yellowCount || 0)
  const total = Number(props.creditSummary?.totalCount || 0)
  const green = Math.max(total - red - yellow, 0)
  return [
    { name: "红码 · 高危", value: red, itemStyle: { color: RISK_COLORS.red } },
    { name: "黄码 · 预警", value: yellow, itemStyle: { color: RISK_COLORS.yellow } },
    { name: "绿码 · 正常", value: green, itemStyle: { color: RISK_COLORS.green } },
  ]
})

const tableRows = computed(() =>
  props.rankingList.slice(0, 15).map((item, index) => {
    const colorCode = String(item.colorCode || "").toUpperCase()
    const tone = colorCode === "RED" ? "red" : colorCode === "YELLOW" ? "yellow" : "green"
    const insuranceRate = Number(item.insuranceRate ?? item.socialTaxScore ?? 0)
    const violationCount = Number(item.violationCount ?? 0)
    const accidentRate = Number(item.accidentRate ?? item.safetyScore ?? 0)
    const riskScore = Math.min(
      100,
      Math.max(
        12,
        tone === "red" ? 72 : tone === "yellow" ? 48 : 24,
        100 - Number(item.totalScore ?? 60),
      ),
    )
    const riskLevel = riskScore >= 58 ? "high" : riskScore >= 32 ? "mid" : "low"
    return {
      key: item.scoreId || item.enterpriseId || index,
      rank: item.rankNo || index + 1,
      enterpriseName: item.enterpriseName || "-",
      badgeLabel: tone === "red" ? "红" : tone === "yellow" ? "黄" : "绿",
      tone,
      insuranceRateText: insuranceRate ? `${insuranceRate.toFixed(1)}%` : "--",
      violationCount,
      accidentRateText: accidentRate ? `${accidentRate.toFixed(2)}%` : "--",
      riskScore,
      riskLevel,
      action: { path: "/credit/score", query: { enterpriseId: item.enterpriseId, colorCode } },
    }
  }),
)

function renderChart() {
  if (props.layout === "table" || !chartRef.value) return
  if (!chartInstance) chartInstance = echarts.init(chartRef.value)
  const total = donutData.value.reduce((sum, item) => sum + item.value, 0)
  const redCount = Number(props.creditSummary?.redCount || 0)
  chartInstance.setOption(
    {
      tooltip: {
        trigger: "item",
        backgroundColor: "rgba(5, 15, 28, 0.94)",
        borderColor: "rgba(0, 229, 255, 0.28)",
        textStyle: { color: "#e9fcff" },
      },
      legend: {
        bottom: 4,
        icon: "circle",
        itemHeight: 8,
        itemWidth: 8,
        itemGap: 12,
        textStyle: { color: "rgba(207, 230, 236, 0.82)", fontSize: 11 },
      },
      series: [
        {
          type: "pie",
          radius: ["60%", "74%"],
          center: ["50%", "42%"],
          silent: true,
          label: { show: false },
          data: donutData.value.map((item) => ({
            value: Math.max(item.value, 0),
            itemStyle: { color: `${item.itemStyle.color}22` },
          })),
        },
        {
          type: "pie",
          radius: ["48%", "58%"],
          center: ["50%", "42%"],
          label: { color: "#dffcff", formatter: "{d}%", fontSize: 11 },
          itemStyle: {
            borderColor: "rgba(4, 10, 23, 0.96)",
            borderWidth: 2,
            shadowBlur: 14,
            shadowColor: "rgba(0, 229, 255, 0.2)",
          },
          data: total
            ? donutData.value
            : [{ name: "暂无", value: 1, itemStyle: { color: "rgba(0, 229, 255, 0.12)" } }],
        },
      ],
      graphic: [
        {
          type: "text",
          left: "center",
          top: "28%",
          style: {
            text: redCount ? String(redCount) : total ? String(total) : "--",
            fill: redCount ? RISK_COLORS.red : "#ebfbff",
            fontSize: redCount ? 28 : 24,
            fontWeight: 800,
            textShadowBlur: redCount ? 20 : 16,
            textShadowColor: redCount ? "rgba(255, 77, 79, 0.72)" : "rgba(0, 229, 255, 0.55)",
          },
        },
        {
          type: "text",
          left: "center",
          top: "44%",
          style: {
            text: redCount ? "红码企业" : "风险企业总量",
            fill: "rgba(204, 228, 235, 0.72)",
            fontSize: 11,
          },
        },
      ],
    },
    true,
  )
  chartInstance.resize()
}

watch([donutData, () => props.rankingList, () => props.layout], () => nextTick(renderChart), { deep: true })

onMounted(() => {
  renderChart()
  window.addEventListener("resize", renderChart)
})

onBeforeUnmount(() => {
  window.removeEventListener("resize", renderChart)
  chartInstance?.dispose()
  chartInstance = null
})

defineExpose({ resize: renderChart })
</script>

<style scoped lang="scss">
.risk-ranking-panel {
  display: grid;
  gap: 8px;
  flex: 1;
  min-height: 0;
}

.risk-ranking-panel--full {
  grid-template-columns: minmax(0, 1fr);
  grid-template-rows: minmax(0, 1fr) minmax(120px, 0.8fr);
}

.risk-ranking-panel--table {
  grid-template-columns: minmax(0, 1fr);
}

.risk-ranking-panel--chart {
  grid-template-columns: minmax(0, 1fr);
}

.risk-ranking-panel__table-wrap {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
}

.risk-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 11px;
}

.risk-table th {
  padding: 6px 8px;
  text-align: left;
  color: rgba(148, 169, 196, 0.88);
  font-weight: 500;
  font-size: 10px;
  border-bottom: 1px solid rgba(0, 180, 255, 0.1);
  white-space: nowrap;
  position: sticky;
  top: 0;
  background: rgba(10, 18, 34, 0.95);
  z-index: 1;
}

.risk-table td {
  padding: 5px 8px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.025);
  white-space: nowrap;
  color: rgba(148, 169, 196, 0.88);
  cursor: pointer;
}

.risk-table tbody tr:hover {
  background: rgba(255, 255, 255, 0.018);
}

.risk-table__rank {
  font-weight: 700;
  color: rgba(148, 169, 196, 0.72) !important;
}

.risk-table__name {
  color: var(--text-primary, #e6ecf5) !important;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.risk-table__warn {
  color: var(--risk-red, #ff4d5a) !important;
}

.risk-badge {
  display: inline-block;
  width: 22px;
  height: 17px;
  border-radius: 3px;
  text-align: center;
  line-height: 17px;
  font-weight: 700;
  font-size: 10px;
  color: #fff;
}

.risk-badge.red {
  background: #e63946;
  box-shadow: 0 0 6px rgba(230, 57, 70, 0.4);
}

.risk-badge.yellow {
  background: #e8a430;
  box-shadow: 0 0 6px rgba(232, 164, 48, 0.35);
}

.risk-badge.green {
  background: #2a9d5c;
  box-shadow: 0 0 6px rgba(42, 157, 92, 0.35);
}

.risk-table__score {
  font-weight: 700;
}

.risk-table__score--high {
  color: var(--risk-red, #ff4d5a);
}

.risk-table__score--mid {
  color: #ff8c3d;
}

.risk-table__score--low {
  color: #3ddc84;
}

.risk-bar-wrap {
  width: 48px;
  height: 4px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  overflow: hidden;
  display: inline-block;
  vertical-align: middle;
  margin-left: 3px;
}

.risk-bar-fill {
  display: block;
  height: 100%;
  border-radius: 8px;
  transition: width 0.5s ease;
}

.risk-bar-fill.high {
  background: var(--risk-red, #ff4d5a);
}

.risk-bar-fill.mid {
  background: #ff8c3d;
}

.risk-bar-fill.low {
  background: #3ddc84;
}

.risk-ranking-panel__chart {
  flex: 1;
  min-height: 0;
}

.risk-ranking-panel--chart .risk-ranking-panel__chart {
  min-height: 160px;
}

.risk-ranking-panel__empty {
  min-height: 96px;
}
</style>
