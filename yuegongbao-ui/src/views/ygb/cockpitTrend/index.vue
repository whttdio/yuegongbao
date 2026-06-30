<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">综合驾驶舱</p>
        <h1 class="ygb-page__title">驾驶舱趋势分析</h1>
        <p class="ygb-page__desc">
          独立查看预警、合规与扩面等核心指标的时间趋势与来源分布，支撑月度复盘与区域对比。
        </p>
      </div>
      <div class="ygb-page__actions">
        <el-button type="primary" plain @click="router.push('/cockpit/overview')">返回驾驶舱总览</el-button>
      </div>
    </section>

    <el-form :inline="true" class="ygb-filter-form">
      <el-form-item label="区域">
        <el-select v-model="queryParams.regionCode" style="width: 180px">
          <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="统计日期">
        <el-date-picker v-model="queryParams.statDate" type="date" value-format="YYYY-MM-DD" style="width: 180px" />
      </el-form-item>
      <el-form-item label="统计月份">
        <el-date-picker v-model="queryParams.statMonth" type="month" value-format="YYYY-MM" style="width: 180px" />
      </el-form-item>
      <el-form-item label="趋势天数">
        <el-select v-model="queryParams.days" style="width: 140px">
          <el-option v-for="item in dayOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="handleQuery">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
        <el-button type="warning" plain v-hasPermi="['ygb:cockpit:export']" @click="handleExport">导出趋势</el-button>
      </el-form-item>
    </el-form>

    <div class="chart-grid">
      <el-card shadow="never">
        <template #header><strong>核心指标趋势</strong></template>
        <div ref="trendChartRef" class="chart-surface" />
      </el-card>
      <el-card shadow="never">
        <template #header><strong>预警来源分布</strong></template>
        <div ref="distributionChartRef" class="chart-surface" />
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { getYgbCockpitDashboard } from '@/api/ygb/cockpit'
import { useAuthorizedRegionOptions } from '@/utils/regionScope'
import {
  cockpitDayOptions as dayOptions,
  cockpitRegionOptions,
  distributionName,
  useCockpitPage
} from '@/views/cockpit/useCockpitPage'

const router = useRouter()
const regionOptions = useAuthorizedRegionOptions(cockpitRegionOptions)

const {
  queryParams,
  trendChartRef,
  distributionChartRef,
  loading,
  handleQuery,
  resetQuery,
  handleExport
} = useCockpitPage({
  fetchDashboard: getYgbCockpitDashboard,
  exportFilePrefix: 'ygb_cockpit_trend',
  trendSeries: [
    { name: '当日预警', type: 'bar', dataKey: 'todayWarningCount', yAxisIndex: 0, barMaxWidth: 24 },
    { name: '工伤参保率', type: 'line', dataKey: 'insuranceRate', yAxisIndex: 1 },
    { name: '扩面完成率', type: 'line', dataKey: 'expandCompletionRate', yAxisIndex: 1 }
  ],
  distributionLabelFormatter: item => distributionName(item.dimensionCode)
})
</script>

<style scoped>
.ygb-page__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
}

.chart-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.chart-surface {
  width: 100%;
  height: 360px;
}

@media (max-width: 1200px) {
  .chart-grid {
    grid-template-columns: 1fr;
  }
}
</style>
