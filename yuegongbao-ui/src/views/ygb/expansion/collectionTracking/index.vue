<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">扩面减损</p>
        <h1 class="ygb-page__title">催缴跟踪</h1>
        <p class="ygb-page__desc">对未参保或欠费企业进行催缴跟踪，记录催缴过程与结果。</p>
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
          <el-select v-model="queryParams.enterpriseId" placeholder="请选择企业" clearable filterable style="width: 200px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="催缴状态" prop="collectionStatus">
          <el-select v-model="queryParams.collectionStatus" placeholder="请选择催缴状态" clearable style="width: 140px">
            <el-option label="未催缴" value="0" />
            <el-option label="催缴中" value="1" />
            <el-option label="已参保" value="2" />
            <el-option label="催缴失败" value="3" />
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
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:uninsuredList:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">催缴跟踪台账</div>
            <div class="ygb-card-head__desc">跟踪扩面征缴对象、征缴状态、欠费金额和跟进进度。</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
        <el-table-column label="ID" align="center" prop="listId" width="80" />
        <el-table-column label="企业" align="center" prop="enterpriseName" min-width="220" />
        <el-table-column label="漏保人员数" align="center" prop="uninsuredCount" width="110" />
        <el-table-column label="应缴金额" align="center" width="120">
          <template #default="scope">
            <span>{{ formatMoney(scope.row.payableAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="催缴状态" align="center" prop="collectionStatus" width="100">
          <template #default="scope">
            <dict-tag :options="[{ label: '未催缴', value: '0' }, { label: '催缴中', value: '1' }, { label: '已参保', value: '2' }, { label: '催缴失败', value: '3' }]" :value="scope.row.collectionStatus" />
          </template>
        </el-table-column>
        <el-table-column label="最后催缴时间" align="center" width="170">
          <template #default="scope">
            <span>{{ parseTime(scope.row.lastCollectionTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="责任人" align="center" prop="handlerName" width="110" />
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

    <page-detail-dialog v-model="detailOpen" title="催缴跟踪详情" width="720px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="企业" :span="1">{{ detail.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="漏保人员数" :span="1">{{ detail.uninsuredCount || '-' }}</el-descriptions-item>
          <el-descriptions-item label="应缴金额" :span="1">{{ formatMoney(detail.payableAmount) }}</el-descriptions-item>
          <el-descriptions-item label="催缴状态" :span="1">{{ detail.collectionStatus || '-' }}</el-descriptions-item>
          <el-descriptions-item label="最后催缴时间" :span="1">{{ parseTime(detail.lastCollectionTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="责任人" :span="1">{{ detail.handlerName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="催缴记录" :span="2">{{ detail.collectionLog || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbExpansionCollectionTracking">
import { ref, onMounted, getCurrentInstance } from 'vue'
import { listExpansionCollection, getExpansionCollectionSummary } from '@/api/ygb/expansionCollection'
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
  enterpriseId: undefined,
  collectionStatus: undefined
})

const summaryCards = ref([])

async function getList() {
  loading.value = true
  try {
    const res = await listExpansionCollection(queryParams.value)
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
    const res = await getExpansionCollectionSummary(queryParams.value)
    const data = res.data || res || {}
    summaryCards.value = [
      { key: 'totalList', label: '催缴任务', value: valueOrDefault(data.totalList, 0), unit: '条', note: '当前范围', cardClass: '' },
      { key: 'uninsuredTotal', label: '漏保人数', value: valueOrDefault(data.uninsuredTotal, 0), unit: '人', note: '累计', cardClass: 'danger' },
      { key: 'collectionPending', label: '未催缴', value: valueOrDefault(data.collectionPending, 0), unit: '条', note: '需启动', cardClass: 'warning' },
      { key: 'insuredCount', label: '已参保', value: valueOrDefault(data.insuredCount, 0), unit: '条', note: '催缴成功', cardClass: 'success' }
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
  enterpriseId: undefined,
  collectionStatus: undefined
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
  proxy.download('ygb/expansion/collection/export', { ...queryParams.value }, `expansion_collection_tracking_${Date.now()}.xlsx`)
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
