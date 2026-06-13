<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>简历管理</span>
          <span class="card-header__tip">复用 WorkerResume 底座，提供运营视角的查询、详情和导出。</span>
        </div>
      </template>

      <div class="summary-grid">
        <div v-for="item in summaryCards" :key="item.key" class="summary-card">
          <div class="summary-card__label">{{ item.label }}</div>
          <div class="summary-card__value">{{ item.value }}</div>
        </div>
      </div>

      <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch">
        <el-form-item label="姓名">
          <el-input v-model="queryParams.personName" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="queryParams.mobile" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="工种">
          <el-input v-model="queryParams.jobType" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="期望岗位">
          <el-input v-model="queryParams.expectedJob" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:operationResume:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table v-loading="loading" :data="resumeList">
        <el-table-column label="简历ID" prop="resumeId" width="90" />
        <el-table-column label="姓名" prop="personName" width="120" />
        <el-table-column label="手机号" prop="mobile" width="140" />
        <el-table-column label="工种" prop="jobType" width="120" />
        <el-table-column label="期望岗位" prop="expectedJob" min-width="180" show-overflow-tooltip />
        <el-table-column label="期望城市" prop="expectedCity" width="120" />
        <el-table-column label="期望薪资" prop="expectedSalary" width="120" />
        <el-table-column label="技能标签" prop="skillTags" min-width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="90" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleView(scope.row)" v-hasPermi="['ygb:operationResume:query']">详情</el-button>
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

    <el-dialog title="简历详情" v-model="detailOpen" width="760px">
      <el-descriptions v-if="detail" :column="2" border>
        <el-descriptions-item label="简历ID">{{ detail.resumeId }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ detail.personName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detail.mobile || '-' }}</el-descriptions-item>
        <el-descriptions-item label="工种">{{ detail.jobType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="期望岗位">{{ detail.expectedJob || '-' }}</el-descriptions-item>
        <el-descriptions-item label="期望城市">{{ detail.expectedCity || '-' }}</el-descriptions-item>
        <el-descriptions-item label="期望薪资">{{ detail.expectedSalary || '-' }}</el-descriptions-item>
        <el-descriptions-item label="技能标签" :span="2">{{ detail.skillTags || '-' }}</el-descriptions-item>
        <el-descriptions-item label="证书说明" :span="2">{{ detail.certificateText || '-' }}</el-descriptions-item>
        <el-descriptions-item label="个人简介" :span="2">{{ detail.intro || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, getCurrentInstance, reactive, ref, toRefs } from 'vue'
import { getOperationResume, listOperationResume } from '@/api/ygb/operation'

const { proxy } = getCurrentInstance()
const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const resumeList = ref([])
const detail = ref(null)
const detailOpen = ref(false)

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    personName: undefined,
    mobile: undefined,
    jobType: undefined,
    expectedJob: undefined
  }
})

const { queryParams } = toRefs(data)

const summaryCards = computed(() => {
  const cityCount = new Set(resumeList.value.map(item => item.expectedCity).filter(Boolean)).size
  const skillTagCount = resumeList.value.filter(item => item.skillTags).length
  const certificateCount = resumeList.value.filter(item => item.certificateText).length
  return [
    { key: 'total', label: '简历总量', value: total.value },
    { key: 'city', label: '期望城市数', value: cityCount },
    { key: 'skill', label: '带技能标签', value: skillTagCount },
    { key: 'certificate', label: '带证书说明', value: certificateCount }
  ]
})

function getList() {
  loading.value = true
  listOperationResume(queryParams.value).then(response => {
    resumeList.value = response.rows || []
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
    personName: undefined,
    mobile: undefined,
    jobType: undefined,
    expectedJob: undefined
  }
  getList()
}

function handleView(row) {
  getOperationResume(row.resumeId).then(response => {
    detail.value = response.data || null
    detailOpen.value = true
  })
}

function handleExport() {
  proxy.download('ygb/operation/resume/export', { ...queryParams.value }, `operation_resume_${Date.now()}.xlsx`)
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
