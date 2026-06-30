<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">人员管理</p>
        <h1 class="ygb-page__title">新业态人员库</h1>
        <p class="ygb-page__desc">管理网约车、外卖、快递等新业态从业人员档案与监测数据。</p>
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
        <el-form-item label="姓名" prop="personName">
          <el-input v-model="queryParams.personName" placeholder="请输入姓名" clearable style="width: 150px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="queryParams.mobile" placeholder="请输入手机号" clearable style="width: 160px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="所属平台" prop="platformId">
          <el-select v-model="queryParams.platformId" placeholder="请选择所属平台" clearable style="width: 160px">
            <el-option label="平台A" value="1" />
            <el-option label="平台B" value="2" />
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
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:newformWorker:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">新业态人员库台账</div>
            <div class="ygb-card-head__desc">汇总新业态从业人员身份、岗位、参保和培训记录。</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
        <el-table-column label="ID" align="center" prop="workerId" width="80" />
        <el-table-column label="姓名" align="center" prop="personName" width="120" />
        <el-table-column label="手机号" align="center" prop="mobile" width="140" />
        <el-table-column label="所属平台" align="center" prop="platformName" min-width="160" />
        <el-table-column label="岗位类型" align="center" prop="jobType" width="120" />
        <el-table-column label="参保状态" align="center" prop="insuranceStatus" width="100">
          <template #default="scope">
            <dict-tag :options="[{ label: '已参保', value: '1' }, { label: '未参保', value: '0' }]" :value="scope.row.insuranceStatus" />
          </template>
        </el-table-column>
        <el-table-column label="更新时间" align="center" width="170">
          <template #default="scope">
            <span>{{ parseTime(scope.row.updateTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
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

    <page-detail-dialog v-model="detailOpen" title="新业态人员库详情" width="720px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="姓名" :span="1">{{ detail.personName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="手机号" :span="1">{{ detail.mobile || '-' }}</el-descriptions-item>
          <el-descriptions-item label="所属平台" :span="1">{{ detail.platformName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="岗位类型" :span="1">{{ detail.jobType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="参保状态" :span="1">{{ detail.insuranceStatus || '-' }}</el-descriptions-item>
          <el-descriptions-item label="更新时间" :span="1">{{ parseTime(detail.updateTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="工作记录" :span="2">{{ detail.workRecord || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbPersonNewform">
import { ref, onMounted, getCurrentInstance } from 'vue'
import { listNewformWorker, getNewformWorkerSummary } from '@/api/ygb/newformWorker'
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
const { proxy } = getCurrentInstance()

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  personName: undefined,
  mobile: undefined,
  platformId: undefined
})

const summaryCards = ref([])

async function getList() {
  loading.value = true
  try {
    const res = await listNewformWorker(queryParams.value)
    list.value = res.rows || res.data || []
    total.value = res.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}



async function getSummary() {
  try {
    const res = await getNewformWorkerSummary(queryParams.value)
    const data = res.data || res || {}
    summaryCards.value = [
      { key: 'totalWorker', label: '人员总数', value: valueOrDefault(data.totalWorker, 0), unit: '人', note: '当前范围', cardClass: '' },
      { key: 'insuredCount', label: '已参保', value: valueOrDefault(data.insuredCount, 0), unit: '人', note: '合规', cardClass: 'success' },
      { key: 'uninsuredCount', label: '未参保', value: valueOrDefault(data.uninsuredCount, 0), unit: '人', note: '需扩面', cardClass: 'danger' },
      { key: 'platformCount', label: '覆盖平台', value: valueOrDefault(data.platformCount, 0), unit: '家', note: '接入数', cardClass: '' }
    ].filter(Boolean)
  } catch (e) {
    console.error(e)
  }
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
  getSummary()
}

function resetQuery() {
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
  personName: undefined,
  mobile: undefined,
  platformId: undefined
  }
  getList()
  getSummary()
}

function handleSelectionChange(selection) {
}

function openDetail(row) {
  detail.value = row
  detailOpen.value = true
}

function handleExport() {
  proxy.download('ygb/newform/worker/export', { ...queryParams.value }, `person_newform_${Date.now()}.xlsx`)
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
