<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">运营后台</p>
        <h1 class="ygb-page__title">运营数据总览</h1>
        <p class="ygb-page__desc">总览平台运营核心数据，包括企业入驻、招聘、简历、设备等关键指标。</p>
      </div>
    </section>

    <div class="ygb-summary-grid">
      <div v-for="item in summaryCards" :key="item.key" class="ygb-summary-card" :class="item.cardClass">
        <div class="ygb-summary-card__label">{{ item.label }}</div>
        <div class="ygb-summary-card__value">
          {{ item.value }}
          <span class="ygb-summary-card__unit">{{ item.unit }}</span>
        </div>
        <div class="ygb-summary-card__note">{{ item.note }}</div>
      </div>
    </div>

    <el-card class="search-card ygb-search-card" shadow="never">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">

        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="toolbar-card ygb-toolbar-card" shadow="never">
      <el-row :gutter="10">
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">运营数据总览台账</div>
            <div class="ygb-card-head__desc">汇聚平台运营、企业入驻、岗位发布和服务办理等核心指标。</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
        <el-table-column label="指标名称" align="center" prop="indicatorName" min-width="200" />
        <el-table-column label="指标值" align="center" prop="indicatorValue" width="150" />
        <el-table-column label="环比" align="center" prop="momRate" width="120" />
        <el-table-column label="同比" align="center" prop="yoyRate" width="120" />
        <el-table-column label="统计时间" align="center" width="170">
          <template #default="scope">
            <span>{{ parseTime(scope.row.statTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column class-name="table-fill-column" min-width="1" />
        <el-table-column label="操作" fixed="right" align="center" width="120" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <page-detail-dialog v-model="detailOpen" title="运营数据总览详情" width="720px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="指标名称" :span="1">{{ detail.indicatorName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="指标值" :span="1">{{ detail.indicatorValue || '-' }}</el-descriptions-item>
          <el-descriptions-item label="环比" :span="1">{{ detail.momRate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="同比" :span="1">{{ detail.yoyRate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="统计时间" :span="1">{{ parseTime(detail.statTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="指标说明" :span="2">{{ detail.indicatorDesc || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbOperationDataOverview">
import { ref, onMounted } from 'vue'
import { getOperationOverview } from '@/api/ygb/operation'
import { parseTime } from '@/utils/yuegongbao'
import { optionselectEnterprise } from '@/api/ygb/enterprise'

function formatMoney(value) {
  if (value === undefined || value === null || value === '') return '-'
  return '¥ ' + Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}
function valueOrDefault(value, fallback = 0) {
  return value === undefined || value === null ? fallback : value
}

const loading = ref(false)
const list = ref([])
const total = ref(0)
const showSearch = ref(true)
const detailOpen = ref(false)
const detail = ref(null)
const enterpriseOptions = ref([])

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,

})

const summaryCards = ref([])

async function getList() {
  loading.value = true
  try {
    const data = await loadOverview()
    list.value = buildIndicatorRows(data)
    total.value = list.value.length
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function getSummary() {
  try {
    const data = await loadOverview()
    summaryCards.value = [
      { key: 'enterpriseReview', label: '企业入驻审核', value: valueOrDefault(data.enterpriseReviewSummary?.totalCount, 0), unit: '条', note: '待办/已办总量', cardClass: '' },
      { key: 'message', label: '消息推送', value: valueOrDefault(data.messageSummary?.totalCount, 0), unit: '条', note: '运营消息总量', cardClass: '' },
      { key: 'jobCount', label: '招聘岗位', value: valueOrDefault(data.jobCount, 0), unit: '个', note: '平台岗位', cardClass: '' },
      { key: 'resumeCount', label: '简历数量', value: valueOrDefault(data.resumeCount, 0), unit: '份', note: '平台简历', cardClass: '' }
    ].filter(Boolean)
  } catch (e) {
    console.error(e)
  }
}

async function loadOverview() {
  const res = await getOperationOverview()
  return res.data || res || {}
}

function buildIndicatorRows(data) {
  const now = new Date()
  return [
    buildIndicatorRow('enterpriseReview', '企业入驻审核', data.enterpriseReviewSummary?.totalCount, '条', '企业入驻申请审核总量', data.enterpriseReviewSummary, now),
    buildIndicatorRow('message', '消息推送', data.messageSummary?.totalCount, '条', '运营消息推送记录总量', data.messageSummary, now),
    buildIndicatorRow('jobCount', '招聘岗位', data.jobCount, '个', '当前平台招聘岗位总量', null, now),
    buildIndicatorRow('resumeCount', '简历数量', data.resumeCount, '份', '当前平台简历总量', null, now)
  ]
}

function buildIndicatorRow(key, name, value, unit, desc, summary, statTime) {
  const count = valueOrDefault(value, 0)
  const pendingCount = valueOrDefault(summary?.pendingCount, 0)
  const finishCount = valueOrDefault(summary?.finishCount, 0)
  return {
    indicatorKey: key,
    indicatorName: name,
    indicatorValue: `${count}${unit}`,
    momRate: summary ? `待办 ${pendingCount}` : '-',
    yoyRate: summary ? `完成 ${finishCount}` : '-',
    statTime,
    indicatorDesc: desc
  }
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,

  }
  getList()
}

function handleSelectionChange(selection) {
}

function openDetail(row) {
  detail.value = row
  detailOpen.value = true
}

async function loadEnterpriseOptions() {
  try {
    const res = await optionselectEnterprise()
    enterpriseOptions.value = res.data || []
  } catch (e) {}
}

onMounted(() => {
  loadEnterpriseOptions()
  getList()
  getSummary()
})
</script>
