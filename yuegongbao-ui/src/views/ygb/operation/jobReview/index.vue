<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>岗位审核</span>
          <span class="card-header__tip">复用 workerJob 主对象，补运营审核视角的查询、详情和导出。</span>
        </div>
      </template>

      <div class="summary-grid">
        <div v-for="item in summaryCards" :key="item.key" class="summary-card">
          <div class="summary-card__label">{{ item.label }}</div>
          <div class="summary-card__value">{{ item.value }}</div>
        </div>
      </div>

      <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch">
        <el-form-item label="企业名称">
          <el-input v-model="queryParams.enterpriseName" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="岗位名称">
          <el-input v-model="queryParams.title" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="岗位类型">
          <el-input v-model="queryParams.jobType" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态">
          <el-input v-model="queryParams.status" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:operationJobReview:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table v-loading="loading" :data="jobList">
        <el-table-column label="岗位ID" prop="jobId" width="90" />
        <el-table-column label="企业名称" prop="enterpriseName" min-width="180" show-overflow-tooltip />
        <el-table-column label="岗位名称" prop="title" min-width="180" show-overflow-tooltip />
        <el-table-column label="岗位类型" prop="jobType" width="120" />
        <el-table-column label="工作地点" prop="workAddress" min-width="180" show-overflow-tooltip />
        <el-table-column label="薪资" prop="salaryText" width="140" />
        <el-table-column label="招聘人数" prop="recruitCount" width="100" />
        <el-table-column label="联系人" prop="contactName" width="100" />
        <el-table-column label="状态" prop="status" width="100" />
        <el-table-column label="发布时间" width="170">
          <template #default="scope">
            <span>{{ parseTime(scope.row.publishTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="90" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleView(scope.row)" v-hasPermi="['ygb:operationJobReview:query']">详情</el-button>
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

    <el-dialog title="岗位详情" v-model="detailOpen" width="760px">
      <el-descriptions v-if="detail" :column="2" border>
        <el-descriptions-item label="岗位ID">{{ detail.jobId }}</el-descriptions-item>
        <el-descriptions-item label="企业名称">{{ detail.enterpriseName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="岗位名称">{{ detail.title || '-' }}</el-descriptions-item>
        <el-descriptions-item label="岗位类型">{{ detail.jobType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="薪资范围">{{ detail.salaryText || '-' }}</el-descriptions-item>
        <el-descriptions-item label="招聘人数">{{ detail.recruitCount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ detail.contactName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detail.contactMobile || '-' }}</el-descriptions-item>
        <el-descriptions-item label="工作地点" :span="2">{{ detail.workAddress || '-' }}</el-descriptions-item>
        <el-descriptions-item label="岗位描述" :span="2">{{ detail.description || '-' }}</el-descriptions-item>
        <el-descriptions-item label="任职要求" :span="2">{{ detail.requirementText || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, getCurrentInstance, reactive, ref, toRefs } from 'vue'
import { getOperationJobReview, listOperationJobReview } from '@/api/ygb/operation'

const { proxy } = getCurrentInstance()
const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const jobList = ref([])
const detail = ref(null)
const detailOpen = ref(false)

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    enterpriseName: undefined,
    title: undefined,
    jobType: undefined,
    status: undefined
  }
})

const { queryParams } = toRefs(data)

const summaryCards = computed(() => {
  const publishedCount = jobList.value.filter(item => item.publishTime).length
  const enterpriseCount = new Set(jobList.value.map(item => item.enterpriseName).filter(Boolean)).size
  const coordinateCount = jobList.value.filter(item => item.longitude && item.latitude).length
  return [
    { key: 'total', label: '岗位总量', value: total.value },
    { key: 'published', label: '已发布时间', value: publishedCount },
    { key: 'enterprise', label: '涉及企业', value: enterpriseCount },
    { key: 'geo', label: '带坐标岗位', value: coordinateCount }
  ]
})

function getList() {
  loading.value = true
  listOperationJobReview(queryParams.value).then(response => {
    jobList.value = response.rows || []
    total.value = response.total || 0
  }).finally(() => {
    loading.value = false
  })
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    enterpriseName: undefined,
    title: undefined,
    jobType: undefined,
    status: undefined
  }
  getList()
}

function handleView(row) {
  getOperationJobReview(row.jobId).then(response => {
    detail.value = response.data || null
    detailOpen.value = true
  })
}

function handleExport() {
  proxy.download('ygb/operation/jobReview/export', { ...queryParams.value }, `operation_job_review_${Date.now()}.xlsx`)
}

getList()
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.card-header__tip {
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.summary-card {
  padding: 14px 16px;
  border-radius: 10px;
  background: linear-gradient(135deg, #f6f9fc 0%, #eef4fb 100%);
}

.summary-card__label {
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.summary-card__value {
  margin-top: 8px;
  font-size: 24px;
  font-weight: 600;
  color: #123b67;
}
</style>
