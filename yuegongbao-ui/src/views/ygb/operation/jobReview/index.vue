<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">平台运营</p>
        <h1 class="ygb-page__title">岗位审核</h1>
        <p class="ygb-page__desc">基于工人端岗位发布数据，提供运营侧岗位查询、详情查看、审核发布和驳回处理。</p>
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
      <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch">
        <el-form-item label="企业名称" prop="enterpriseName">
          <el-input v-model="queryParams.enterpriseName" placeholder="请输入企业名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="岗位名称" prop="title">
          <el-input v-model="queryParams.title" placeholder="请输入岗位名称" clearable style="width: 180px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="岗位类型" prop="jobType">
          <el-input v-model="queryParams.jobType" placeholder="请输入岗位类型" clearable style="width: 160px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 140px">
            <el-option label="已发布" value="0" />
            <el-option label="待审核/驳回" value="1" />
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
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:operationJobReview:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">岗位发布台账</div>
            <div class="ygb-card-head__desc">展示岗位发布、审核状态和办理结果</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="jobList">
        <el-table-column label="岗位ID" prop="jobId" width="90" />
        <el-table-column label="企业名称" prop="enterpriseName" min-width="180" show-overflow-tooltip />
        <el-table-column label="岗位名称" prop="title" min-width="180" show-overflow-tooltip />
        <el-table-column label="岗位类型" prop="jobType" width="120" />
        <el-table-column label="工作地点" prop="workAddress" min-width="180" show-overflow-tooltip />
        <el-table-column label="薪资" prop="salaryText" width="140" />
        <el-table-column label="招聘人数" prop="recruitCount" width="100" />
        <el-table-column label="联系人" prop="contactName" width="110" />
        <el-table-column label="状态" prop="status" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'warning'">{{ formatStatus(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发布时间" width="170">
          <template #default="scope">
            <span>{{ parseTime(scope.row.publishTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column class-name="table-fill-column" min-width="1" />
        <el-table-column label="操作" width="190" fixed="right" align="center">
          <template #default="scope">
            <el-button link type="primary" icon="View" @click="handleView(scope.row)" v-hasPermi="['ygb:operationJobReview:query']">详情</el-button>
            <el-button link type="success" icon="CircleCheck" @click="openReview(scope.row, '0')" v-hasPermi="['ygb:operationJobReview:edit']">通过</el-button>
            <el-button link type="danger" icon="CircleClose" @click="openReview(scope.row, '1')" v-hasPermi="['ygb:operationJobReview:edit']">驳回</el-button>
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

    <page-detail-dialog v-model="detailOpen" title="岗位详情" width="820px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="岗位ID">{{ detail.jobId ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="企业名称">{{ detail.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="岗位名称">{{ detail.title || '-' }}</el-descriptions-item>
          <el-descriptions-item label="岗位类型">{{ detail.jobType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="薪资范围">{{ detail.salaryText || '-' }}</el-descriptions-item>
          <el-descriptions-item label="招聘人数">{{ detail.recruitCount ?? 0 }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ detail.contactName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ detail.contactMobile || '-' }}</el-descriptions-item>
          <el-descriptions-item label="经度">{{ detail.longitude ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="纬度">{{ detail.latitude ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ formatStatus(detail.status) }}</el-descriptions-item>
          <el-descriptions-item label="发布时间">{{ parseTime(detail.publishTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="工作地点" :span="2">{{ detail.workAddress || '-' }}</el-descriptions-item>
          <el-descriptions-item label="岗位描述" :span="2">{{ detail.description || '-' }}</el-descriptions-item>
          <el-descriptions-item label="任职要求" :span="2">{{ detail.requirementText || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </page-detail-dialog>

    <el-dialog v-model="reviewOpen" :title="reviewForm.status === '0' ? '通过岗位审核' : '驳回岗位审核'" width="520px" append-to-body>
      <el-form ref="reviewRef" :model="reviewForm" label-width="90px">
        <el-form-item label="岗位名称">
          <el-input :model-value="reviewForm.title" disabled />
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input v-model="reviewForm.opinion" type="textarea" :rows="4" placeholder="请输入审核意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewOpen = false">取消</el-button>
        <el-button type="primary" @click="submitReview">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="YgbOperationJobReview">
import { computed, getCurrentInstance, ref } from 'vue'
import { getOperationJobReview, listOperationJobReview, reviewOperationJob } from '@/api/ygb/operation'
import { parseTime } from '@/utils/yuegongbao'

const { proxy } = getCurrentInstance()
const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const jobList = ref([])
const detail = ref(null)
const detailOpen = ref(false)
const reviewOpen = ref(false)
const reviewForm = ref({
  jobId: undefined,
  title: '',
  status: '0',
  opinion: ''
})

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  enterpriseName: undefined,
  title: undefined,
  jobType: undefined,
  status: undefined
})

const summaryCards = computed(() => {
  const publishedCount = jobList.value.filter(item => item.publishTime).length
  const enterpriseCount = new Set(jobList.value.map(item => item.enterpriseName).filter(Boolean)).size
  const coordinateCount = jobList.value.filter(item => item.longitude && item.latitude).length
  return [
    { key: 'total', label: '岗位总量', value: total.value, unit: '个', note: '当前筛选范围', cardClass: '' },
    { key: 'published', label: '已发布岗位', value: publishedCount, unit: '个', note: '当前页', cardClass: 'success' },
    { key: 'enterprise', label: '涉及企业', value: enterpriseCount, unit: '家', note: '当前页去重', cardClass: '' },
    { key: 'geo', label: '带坐标岗位', value: coordinateCount, unit: '个', note: '当前页', cardClass: 'warning' }
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

function openReview(row, status) {
  reviewForm.value = {
    jobId: row.jobId,
    title: row.title || '',
    status,
    opinion: status === '0' ? '审核通过，允许发布。' : '审核驳回，请补充或修正岗位信息。'
  }
  reviewOpen.value = true
}

function submitReview() {
  reviewOperationJob(reviewForm.value.jobId, {
    status: reviewForm.value.status,
    opinion: reviewForm.value.opinion
  }).then(() => {
    proxy.$modal.msgSuccess('岗位审核已处理')
    reviewOpen.value = false
    getList()
  })
}

function handleExport() {
  proxy.download('ygb/operation/jobReview/export', { ...queryParams.value }, `operation_job_review_${Date.now()}.xlsx`)
}

function formatStatus(status) {
  return status === '0' ? '已发布' : '待审核/驳回'
}

getList()
</script>
