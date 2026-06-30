<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">信用评价</p>
        <h1 class="ygb-page__title">信用报告导出</h1>
        <p class="ygb-page__desc">基于企业信用评分生成和导出信用报告，支持按企业、月份和等级筛选。</p>
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
        <el-form-item label="企业" prop="enterpriseId">
          <el-select v-model="queryParams.enterpriseId" placeholder="请选择企业" clearable filterable style="width: 220px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="统计月份" prop="statMonth">
          <el-date-picker v-model="queryParams.statMonth" type="month" placeholder="请选择统计月份" format="YYYY-MM" value-format="YYYY-MM" style="width: 160px" />
        </el-form-item>
        <el-form-item label="信用等级" prop="creditLevel">
          <el-select v-model="queryParams.creditLevel" placeholder="请选择等级" clearable style="width: 140px">
            <el-option label="A" value="A" />
            <el-option label="B" value="B" />
            <el-option label="C" value="C" />
            <el-option label="D" value="D" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="toolbar-card ygb-toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col :span="1.5">
          <el-button type="primary" plain icon="Document" @click="generateReport" v-hasPermi="['ygb:creditScore:generate']">生成评分</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:creditScore:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="refreshAll" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">信用报告导出台账</div>
            <div class="ygb-card-head__desc">导出数据覆盖当前筛选范围内的信用评分结果</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="ID" align="center" prop="scoreId" width="80" />
        <el-table-column label="企业" align="center" prop="enterpriseName" min-width="220" show-overflow-tooltip />
        <el-table-column label="统计月份" align="center" prop="statMonth" width="110" />
        <el-table-column label="总分" align="center" prop="totalScore" width="100" />
        <el-table-column label="信用等级" align="center" prop="creditLevel" width="100" />
        <el-table-column label="红黄绿码" align="center" prop="colorCode" width="110" />
        <el-table-column label="评估时间" align="center" width="170">
          <template #default="scope">
            <span>{{ parseTime(scope.row.evaluateTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</span>
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

    <page-detail-dialog v-model="detailOpen" title="信用报告详情" width="820px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="企业">{{ detail.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="统计月份">{{ detail.statMonth || '-' }}</el-descriptions-item>
          <el-descriptions-item label="总分">{{ detail.totalScore ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="信用等级">{{ detail.creditLevel || '-' }}</el-descriptions-item>
          <el-descriptions-item label="红黄绿码">{{ detail.colorCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="预警状态">{{ detail.warningStatus === '1' ? '已预警' : '未预警' }}</el-descriptions-item>
          <el-descriptions-item label="评估时间">{{ parseTime(detail.evaluateTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ parseTime(detail.updateTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="报告摘要" :span="2">{{ detail.summaryText || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbCreditReportExport">
import { ref, onMounted, getCurrentInstance } from 'vue'
import { listCreditScore, getCreditScoreSummary, generateCreditScore } from '@/api/ygb/creditScore'
import { parseTime } from '@/utils/yuegongbao'
import { optionselectEnterprise } from '@/api/ygb/enterprise'
import { ElMessage } from 'element-plus'

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
const { proxy } = getCurrentInstance()
const ids = ref([])

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  enterpriseId: undefined,
  statMonth: undefined,
  creditLevel: undefined
})

const summaryCards = ref([])

async function getList() {
  loading.value = true
  try {
    const res = await listCreditScore(queryParams.value)
    list.value = res.rows || res.data || []
    total.value = res.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function generateReport() {
  const targetRows = ids.value.length ? list.value.filter(item => ids.value.includes(item.scoreId)) : []
  const enterpriseIds = Array.from(new Set(targetRows.map(item => item.enterpriseId).filter(Boolean)))
  const baseRequest = {
    statMonth: queryParams.value.statMonth || new Date().toISOString().slice(0, 7),
    regionCode: queryParams.value.regionCode
  }
  if (queryParams.value.enterpriseId) {
    await generateCreditScore({ ...baseRequest, enterpriseId: queryParams.value.enterpriseId })
  } else if (enterpriseIds.length) {
    for (const enterpriseId of enterpriseIds) {
      await generateCreditScore({ ...baseRequest, enterpriseId })
    }
  } else {
    await generateCreditScore(baseRequest)
  }
  ElMessage.success('信用评分生成完成')
  ids.value = []
  refreshAll()
}

async function getSummary() {
  try {
    const res = await getCreditScoreSummary(queryParams.value)
    const data = res.data || res || {}
    summaryCards.value = [
      { key: 'totalCount', label: '可选企业', value: valueOrDefault(data.totalCount, total.value), unit: '家', note: '当前筛选范围', cardClass: '' },
      { key: 'selectedCount', label: '已选择', value: ids.value.length, unit: '家', note: '待生成或导出', cardClass: 'primary' },
      { key: 'averageScore', label: '平均信用分', value: valueOrDefault(data.averageScore, 0), unit: '分', note: '当前筛选范围', cardClass: '' },
      { key: 'riskCount', label: '风险档案', value: Number(data.redCount || 0) + Number(data.yellowCount || 0) + Number(data.dCount || 0), unit: '家', note: '重点关注', cardClass: 'danger' }
    ]
  } catch (e) {
    console.error(e)
  }
}

function refreshAll() {
  getList()
  getSummary()
}

function handleQuery() {
  queryParams.value.pageNum = 1
  refreshAll()
}

function resetQuery() {
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    enterpriseId: undefined,
    statMonth: undefined,
    creditLevel: undefined
  }
  refreshAll()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.scoreId).filter(Boolean)
  getSummary()
}

function openDetail(row) {
  detail.value = row
  detailOpen.value = true
}

function handleExport() {
  proxy.download('ygb/credit/score/export', { ...queryParams.value }, `credit_report_export_${Date.now()}.xlsx`)
}

async function loadEnterpriseOptions() {
  try {
    const res = await optionselectEnterprise()
    enterpriseOptions.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  loadEnterpriseOptions()
  refreshAll()
})
</script>
