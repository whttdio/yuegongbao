<template>
  <div class="risk-ranking-panel">
    <div class="risk-ranking-panel__list">
      <button
        v-for="item in rankingItems"
        :key="item.key"
        type="button"
        class="risk-ranking-item"
        :class="`risk-ranking-item--${item.tone}`"
        @click="item.action && emit('open-module', item.action)"
      >
        <div class="risk-ranking-item__head">
          <span class="risk-ranking-item__rank">TOP {{ item.rank }}</span>
          <span :class="['risk-tag', `risk-tag--${item.tone}`]">{{ item.colorLabel }}</span>
        </div>
        <strong>{{ item.enterpriseName }}</strong>
        <div class="risk-ranking-item__factors">
          <span v-for="factor in item.factors" :key="factor">{{ factor }}</span>
        </div>
      </button>
      <div v-if="!rankingItems.length" class="risk-ranking-panel__empty">暂无红黄绿码排行数据</div>
    </div>
    <div ref="chartRef" class="risk-ranking-panel__chart" />
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  creditSummary: { type: Object, default: () => ({}) },
  rankingList: { type: Array, default: () => [] },
  colors: { type: Object, default: () => ({}) }
})

const emit = defineEmits(['open-module'])

const chartRef = ref(null)
let chartInstance = null

const donutData = computed(() => {
  const red = Number(props.creditSummary?.redCount || 0)
  const yellow = Number(props.creditSummary?.yellowCount || 0)
  const total = Number(props.creditSummary?.totalCount || 0)
  const green = Math.max(total - red - yellow, 0)
  return [
    { name: '红码', value: red, itemStyle: { color: props.colors.danger || '#ff6f91' } },
    { name: '黄码', value: yellow, itemStyle: { color: props.colors.amber || '#ffbe62' } },
    { name: '绿码', value: green, itemStyle: { color: props.colors.green || '#3df2b2' } }
  ]
})

const rankingItems = computed(() => props.rankingList.slice(0, 8).map((item, index) => {
  const colorCode = String(item.colorCode || '').toUpperCase()
  const tone = colorCode === 'RED' ? 'red' : colorCode === 'YELLOW' ? 'yellow' : 'green'
  const factors = []
  if (item.insuranceRate != null || item.socialTaxScore != null) {
    factors.push(`参保率 ${Number(item.insuranceRate ?? item.socialTaxScore).toFixed(1)}%`)
  }
  if (item.codeRate != null || item.governanceScore != null) {
    factors.push(`赋码率 ${Number(item.codeRate ?? item.governanceScore).toFixed(1)}%`)
  }
  if (item.violationCount != null) factors.push(`违规 ${item.violationCount} 次`)
  if (item.accidentRate != null || item.safetyScore != null) {
    factors.push(`事故率 ${Number(item.accidentRate ?? item.safetyScore).toFixed(2)}%`)
  }
  if (!factors.length && item.totalScore != null) factors.push(`信用分 ${item.totalScore}`)
  return {
    key: item.scoreId || item.enterpriseId || index,
    rank: item.rankNo || index + 1,
    enterpriseName: item.enterpriseName || '-',
    colorLabel: tone === 'red' ? '红码' : tone === 'yellow' ? '黄码' : '绿码',
    tone,
    factors,
    action: { path: '/credit/score', query: { enterpriseId: item.enterpriseId, colorCode } }
  }
}))

function renderChart() {
  if (!chartRef.value) return
  if (!chartInstance) chartInstance = echarts.init(chartRef.value)
  const total = donutData.value.reduce((sum, item) => sum + item.value, 0)
  chartInstance.setOption({
    tooltip: {
      trigger: 'item',
      backgroundColor: props.colors.tooltip || 'rgba(4, 12, 22, 0.96)',
      borderColor: props.colors.border || 'rgba(82, 230, 255, 0.18)',
      textStyle: { color: props.colors.text || '#ebf8ff' }
    },
    legend: {
      bottom: 0,
      textStyle: { color: props.colors.muted || 'rgba(177, 214, 237, 0.72)', fontSize: 11 }
    },
    series: [{
      type: 'pie',
      radius: ['54%', '74%'],
      center: ['50%', '42%'],
      label: { color: props.colors.text || '#ebf8ff', formatter: '{d}%' },
      itemStyle: { borderColor: 'rgba(4, 12, 22, 0.96)', borderWidth: 2 },
      data: total ? donutData.value : [{ name: '暂无', value: 1, itemStyle: { color: 'rgba(82, 132, 168, 0.24)' } }]
    }],
    graphic: [{
      type: 'text',
      left: 'center',
      top: '36%',
      style: {
        text: total ? String(total) : '--',
        fill: props.colors.text || '#ebf8ff',
        fontSize: 24,
        fontWeight: 700
      }
    }]
  }, true)
  chartInstance.resize()
}

watch([donutData, () => props.rankingList], () => nextTick(renderChart), { deep: true })

onMounted(() => {
  renderChart()
  window.addEventListener('resize', renderChart)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', renderChart)
  chartInstance?.dispose()
  chartInstance = null
})

defineExpose({ resize: renderChart })
</script>

<style scoped lang="scss">
.risk-ranking-panel {
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(0, 0.95fr);
  gap: 12px;
  min-height: 280px;
}

.risk-ranking-panel__list {
  display: grid;
  gap: 8px;
  max-height: 280px;
  overflow: auto;
}

.risk-ranking-item {
  display: grid;
  gap: 6px;
  padding: 10px 12px;
  color: inherit;
  text-align: left;
  border: 1px solid rgba(82, 230, 255, 0.1);
  background: linear-gradient(180deg, rgba(17, 42, 68, 0.48), rgba(7, 18, 32, 0.88));
  cursor: pointer;
}

.risk-ranking-item__head {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  align-items: center;
}

.risk-ranking-item__rank {
  color: rgba(177, 214, 237, 0.72);
  font-size: 11px;
}

.risk-ranking-item strong {
  font-size: 14px;
}

.risk-ranking-item__factors {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  color: rgba(177, 214, 237, 0.68);
  font-size: 11px;
}

.risk-ranking-item--red {
  box-shadow: inset 3px 0 0 rgba(255, 111, 145, 0.92);
}

.risk-ranking-item--yellow {
  box-shadow: inset 3px 0 0 rgba(255, 190, 98, 0.92);
}

.risk-ranking-item--green {
  box-shadow: inset 3px 0 0 rgba(61, 242, 178, 0.92);
}

.risk-ranking-panel__chart {
  min-height: 280px;
}

.risk-ranking-panel__empty {
  padding: 24px 12px;
  color: rgba(177, 214, 237, 0.6);
  font-size: 12px;
}

.risk-tag {
  display: inline-flex;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
}

.risk-tag--red { color: #03111f; background: rgba(255, 111, 145, 0.94); }
.risk-tag--yellow { color: #03111f; background: rgba(255, 190, 98, 0.94); }
.risk-tag--green { color: #03111f; background: rgba(61, 242, 178, 0.94); }
</style>
