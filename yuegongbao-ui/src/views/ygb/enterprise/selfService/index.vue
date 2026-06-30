<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">单位管理</p>
        <h1 class="ygb-page__title">企业自主服务</h1>
        <p class="ygb-page__desc">以企业主数据和同步状态作为自助服务入口监管底账，确保企业信息可查询、可维护、可联动。</p>
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
        <el-form-item label="企业名称" prop="enterpriseName">
          <el-input v-model="queryParams.enterpriseName" placeholder="请输入企业名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="统一信用代码" prop="enterpriseCode">
          <el-input v-model="queryParams.enterpriseCode" placeholder="请输入统一信用代码" clearable style="width: 200px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="企业类型" prop="enterpriseType">
          <el-select v-model="queryParams.enterpriseType" placeholder="请选择企业类型" clearable style="width: 150px">
            <el-option v-for="item in enterpriseTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="同步状态" prop="syncStatus">
          <el-select v-model="queryParams.syncStatus" placeholder="请选择同步状态" clearable style="width: 140px">
            <el-option v-for="item in syncStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
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
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:enterprise:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">企业自主服务台账</div>
            <div class="ygb-card-head__desc">按企业主数据组织的自助服务入口监管数据</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="list">
        <el-table-column label="ID" align="center" prop="enterpriseId" width="80" />
        <el-table-column label="企业名称" align="center" prop="enterpriseName" min-width="220" show-overflow-tooltip />
        <el-table-column label="统一信用代码" align="center" prop="enterpriseCode" width="190" />
        <el-table-column label="企业类型" align="center" prop="enterpriseType" width="120">
          <template #default="scope">
            <dict-tag :options="enterpriseTypeOptions" :value="scope.row.enterpriseType" />
          </template>
        </el-table-column>
        <el-table-column label="联系人" align="center" prop="contactPerson" width="120" />
        <el-table-column label="联系电话" align="center" prop="contactPhone" width="140" />
        <el-table-column label="同步状态" align="center" prop="syncStatus" width="120">
          <template #default="scope">
            <dict-tag :options="syncStatusOptions" :value="scope.row.syncStatus" />
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status" width="100">
          <template #default="scope">
            <dict-tag :options="statusOptions" :value="scope.row.status" />
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

    <page-detail-dialog v-model="detailOpen" title="企业自主服务详情" width="720px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="企业名称" :span="1">{{ detail.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="统一信用代码" :span="1">{{ detail.enterpriseCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="企业类型" :span="1">{{ formatEnterpriseType(detail.enterpriseType) }}</el-descriptions-item>
          <el-descriptions-item label="法定代表人" :span="1">{{ detail.legalPerson || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系人" :span="1">{{ detail.contactPerson || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话" :span="1">{{ detail.contactPhone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="同步状态" :span="1">{{ formatSyncStatus(detail.syncStatus) }}</el-descriptions-item>
          <el-descriptions-item label="状态" :span="1">{{ formatStatus(detail.status) }}</el-descriptions-item>
          <el-descriptions-item label="企业地址" :span="2">{{ detail.address || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbEnterpriseSelfService">
import { ref, onMounted, getCurrentInstance } from 'vue'
import { listEnterprise, getEnterpriseSummary } from '@/api/ygb/enterprise'

function valueOrDefault(value, fallback = 0) {
  return value === undefined || value === null ? fallback : value
}

const loading = ref(false)
const list = ref([])
const total = ref(0)
const showSearch = ref(true)
const detailOpen = ref(false)
const detail = ref(null)
const { proxy } = getCurrentInstance()

const enterpriseTypeOptions = [
  { label: '派遣单位', value: '1' },
  { label: '用工单位', value: '2' },
  { label: '服务机构', value: '3' },
  { label: '监管单位', value: '4' }
]

const syncStatusOptions = [
  { label: '未同步', value: '0' },
  { label: '已同步', value: '1' },
  { label: '同步异常', value: '2' }
]

const statusOptions = [
  { label: '正常', value: '0' },
  { label: '停用', value: '1' }
]

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  enterpriseName: undefined,
  enterpriseCode: undefined,
  enterpriseType: undefined,
  syncStatus: undefined
})

const summaryCards = ref([])

async function getList() {
  loading.value = true
  try {
    const res = await listEnterprise(queryParams.value)
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
    const res = await getEnterpriseSummary(queryParams.value)
    const data = res.data || res || {}
    summaryCards.value = [
      { key: 'totalCount', label: '企业总数', value: valueOrDefault(data.totalCount, 0), unit: '家', note: '当前范围', cardClass: '' },
      { key: 'normalCount', label: '正常企业', value: valueOrDefault(data.normalCount, 0), unit: '家', note: '可服务', cardClass: 'success' },
      { key: 'syncErrorCount', label: '同步异常', value: valueOrDefault(data.syncErrorCount, 0), unit: '家', note: '需处理', cardClass: 'warning' },
      { key: 'disabledCount', label: '停用企业', value: valueOrDefault(data.disabledCount, 0), unit: '家', note: '已停用', cardClass: '' }
    ]
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
    enterpriseName: undefined,
    enterpriseCode: undefined,
    enterpriseType: undefined,
    syncStatus: undefined
  }
  getList()
  getSummary()
}

function openDetail(row) {
  detail.value = row
  detailOpen.value = true
}

function handleExport() {
  proxy.download('ygb/enterprise/export', { ...queryParams.value }, `enterprise_self_service_${Date.now()}.xlsx`)
}

function formatEnterpriseType(value) {
  return enterpriseTypeOptions.find(item => item.value === value)?.label || '-'
}

function formatSyncStatus(value) {
  return syncStatusOptions.find(item => item.value === value)?.label || '-'
}

function formatStatus(value) {
  return statusOptions.find(item => item.value === value)?.label || '-'
}

onMounted(() => {
  getList()
  getSummary()
})
</script>
