<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">平台运营</p>
        <h1 class="ygb-page__title">简历管理</h1>
        <p class="ygb-page__desc">基于工人端简历库，提供运营侧查询、详情查看、导出和简历质量标记。</p>
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
        <el-form-item label="姓名" prop="personName">
          <el-input v-model="queryParams.personName" placeholder="请输入姓名" clearable style="width: 160px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="queryParams.mobile" placeholder="请输入手机号" clearable style="width: 160px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="工种" prop="jobType">
          <el-input v-model="queryParams.jobType" placeholder="请输入工种" clearable style="width: 160px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="期望岗位" prop="expectedJob">
          <el-input v-model="queryParams.expectedJob" placeholder="请输入期望岗位" clearable style="width: 180px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="期望城市" prop="expectedCity">
          <el-input v-model="queryParams.expectedCity" placeholder="请输入期望城市" clearable style="width: 160px" @keyup.enter="handleQuery" />
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
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:operationResume:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">简历台账</div>
            <div class="ygb-card-head__desc">集中查看简历资料、技能标签和运营标记</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="resumeList">
        <el-table-column label="简历ID" prop="resumeId" width="90" />
        <el-table-column label="姓名" prop="personName" width="120" />
        <el-table-column label="手机号" prop="mobile" width="140" />
        <el-table-column label="工种" prop="jobType" width="120" show-overflow-tooltip />
        <el-table-column label="期望岗位" prop="expectedJob" min-width="180" show-overflow-tooltip />
        <el-table-column label="期望城市" prop="expectedCity" width="120" />
        <el-table-column label="期望薪资" prop="expectedSalary" width="130" />
        <el-table-column label="技能标签" prop="skillTags" min-width="180" show-overflow-tooltip />
        <el-table-column label="运营标记" min-width="180" show-overflow-tooltip>
          <template #default="scope">
            <el-tag v-if="resolveResumeMark(scope.row.remark).type" :type="resolveResumeMark(scope.row.remark).type">
              {{ resolveResumeMark(scope.row.remark).label }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="更新时间" width="170">
          <template #default="scope">
            <span>{{ parseTime(scope.row.updateTime || scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column class-name="table-fill-column" min-width="1" />
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="scope">
            <el-button link type="primary" icon="View" @click="handleView(scope.row)" v-hasPermi="['ygb:operationResume:query']">详情</el-button>
            <el-button link type="success" icon="CollectionTag" @click="openMark(scope.row)" v-hasPermi="['ygb:operationResume:edit']">标记</el-button>
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

    <page-detail-dialog v-model="detailOpen" title="简历详情" width="820px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="简历ID">{{ detail.resumeId ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="人员ID">{{ detail.personId ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ detail.personName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ detail.mobile || '-' }}</el-descriptions-item>
          <el-descriptions-item label="工种">{{ detail.jobType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="期望岗位">{{ detail.expectedJob || '-' }}</el-descriptions-item>
          <el-descriptions-item label="期望城市">{{ detail.expectedCity || '-' }}</el-descriptions-item>
          <el-descriptions-item label="期望薪资">{{ detail.expectedSalary || '-' }}</el-descriptions-item>
          <el-descriptions-item label="技能标签" :span="2">{{ detail.skillTags || '-' }}</el-descriptions-item>
          <el-descriptions-item label="证书说明" :span="2">{{ detail.certificateText || '-' }}</el-descriptions-item>
          <el-descriptions-item label="个人简介" :span="2">{{ detail.intro || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </page-detail-dialog>

    <el-dialog v-model="markOpen" title="简历运营标记" width="520px" append-to-body>
      <el-form :model="markForm" label-width="90px">
        <el-form-item label="姓名">
          <el-input :model-value="markForm.personName" disabled />
        </el-form-item>
        <el-form-item label="标记类型">
          <el-select v-model="markForm.mark" style="width: 100%">
            <el-option label="优质简历" value="quality" />
            <el-option label="待补充" value="incomplete" />
            <el-option label="暂不推荐" value="blocked" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理备注">
          <el-input v-model="markForm.opinion" type="textarea" :rows="4" placeholder="请输入运营处理备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="markOpen = false">取消</el-button>
        <el-button type="primary" @click="submitMark">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="YgbOperationResume">
import { computed, getCurrentInstance, ref } from 'vue'
import { getOperationResume, listOperationResume, markOperationResume } from '@/api/ygb/operation'
import { parseTime } from '@/utils/yuegongbao'

const { proxy } = getCurrentInstance()
const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const resumeList = ref([])
const detail = ref(null)
const detailOpen = ref(false)
const markOpen = ref(false)
const markForm = ref({
  resumeId: undefined,
  personName: '',
  mark: 'quality',
  opinion: ''
})

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  personName: undefined,
  mobile: undefined,
  jobType: undefined,
  expectedJob: undefined,
  expectedCity: undefined
})

const summaryCards = computed(() => {
  const cityCount = new Set(resumeList.value.map(item => item.expectedCity).filter(Boolean)).size
  const skillTagCount = resumeList.value.filter(item => item.skillTags).length
  const certificateCount = resumeList.value.filter(item => item.certificateText).length
  return [
    { key: 'total', label: '简历总量', value: total.value, unit: '份', note: '当前筛选范围', cardClass: '' },
    { key: 'city', label: '期望城市', value: cityCount, unit: '个', note: '当前页去重', cardClass: '' },
    { key: 'skill', label: '技能标签', value: skillTagCount, unit: '份', note: '当前页已填写', cardClass: 'success' },
    { key: 'certificate', label: '证书说明', value: certificateCount, unit: '份', note: '当前页已填写', cardClass: 'warning' }
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
    expectedJob: undefined,
    expectedCity: undefined
  }
  getList()
}

function handleView(row) {
  getOperationResume(row.resumeId).then(response => {
    detail.value = response.data || null
    detailOpen.value = true
  })
}

function openMark(row) {
  markForm.value = {
    resumeId: row.resumeId,
    personName: row.personName || '',
    mark: resolveResumeMark(row.remark).mark || 'quality',
    opinion: row.remark || ''
  }
  markOpen.value = true
}

function submitMark() {
  markOperationResume(markForm.value.resumeId, {
    mark: markForm.value.mark,
    opinion: markForm.value.opinion
  }).then(() => {
    proxy.$modal.msgSuccess('简历标记已保存')
    markOpen.value = false
    getList()
  })
}

function handleExport() {
  proxy.download('ygb/operation/resume/export', { ...queryParams.value }, `operation_resume_${Date.now()}.xlsx`)
}

function resolveResumeMark(remark) {
  if (!remark) {
    return { label: '', type: '', mark: '' }
  }
  if (remark.includes('优质简历')) {
    return { label: '优质简历', type: 'success', mark: 'quality' }
  }
  if (remark.includes('待补充')) {
    return { label: '待补充', type: 'warning', mark: 'incomplete' }
  }
  if (remark.includes('暂不推荐')) {
    return { label: '暂不推荐', type: 'danger', mark: 'blocked' }
  }
  return { label: '已备注', type: 'info', mark: 'quality' }
}

getList()
</script>
