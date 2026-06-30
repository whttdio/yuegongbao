<template>
  <div class="app-container recruit-page">
    <section class="recruit-hero">
      <div>
        <p class="recruit-hero__eyebrow">便民服务运营台</p>
        <h1 class="recruit-hero__title">招聘用工市场运营</h1>
        <p class="recruit-hero__desc">
          复用工人端岗位数据源，管理岗位发布、企业招聘信息、薪资地点和移动端投递入口，确保 PC 与移动端找工作一致。
        </p>
      </div>
      <div class="recruit-hero__meta">
        <span>移动端入口</span>
        <strong>/pages/job/list / /pages/job/detail</strong>
      </div>
    </section>

    <div class="recruit-stat-grid">
      <div v-for="item in stats" :key="item.key" class="recruit-stat">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
        <small>{{ item.hint }}</small>
      </div>
    </div>

    <el-card shadow="never" class="recruit-panel">
      <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch" label-width="88px">
        <el-form-item label="岗位名称" prop="title">
          <el-input v-model="queryParams.title" placeholder="请输入岗位名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="企业名称" prop="enterpriseName">
          <el-input v-model="queryParams.enterpriseName" placeholder="请输入企业名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="岗位类型" prop="jobType">
          <el-input v-model="queryParams.jobType" placeholder="请输入岗位类型" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" clearable style="width: 120px">
            <el-option label="发布" value="0" />
            <el-option label="停用/待审" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="recruit-panel">
      <template #header>
        <div class="recruit-table-head">
          <div>
            <strong>岗位运营台账</strong>
            <span>岗位发布后由移动端找工作、附近岗位和投递入口统一消费</span>
          </div>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
        </div>
      </template>

      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['ygb:workerJob:add']">新增岗位</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['ygb:workerJob:edit']">修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['ygb:workerJob:remove']">删除</el-button>
        </el-col>
      </el-row>

      <el-table v-loading="loading" :data="jobList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="岗位ID" prop="jobId" width="90" />
        <el-table-column label="岗位信息" min-width="260">
          <template #default="{ row }">
            <div class="main-cell">
              <strong>{{ row.title || '-' }}</strong>
              <span>{{ row.jobType || '-' }} / {{ row.salaryText || formatSalary(row) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="企业" prop="enterpriseName" min-width="180" show-overflow-tooltip />
        <el-table-column label="地点" prop="workAddress" min-width="180" show-overflow-tooltip />
        <el-table-column label="招聘人数" prop="recruitCount" width="100" align="center" />
        <el-table-column label="联系人" width="150">
          <template #default="{ row }">{{ row.contactName || '-' }} {{ row.contactMobile || '' }}</template>
        </el-table-column>
        <el-table-column label="移动端投递" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'info'">{{ row.status === '0' ? '开放' : '关闭' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发布时间" prop="publishTime" width="170" />
        <el-table-column label="操作" width="230" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" icon="View" @click="handleDetail(row)">详情</el-button>
            <el-button link type="primary" icon="Link" @click="previewMobile(row)">移动端预览</el-button>
            <el-button link type="primary" icon="Edit" @click="handleUpdate(row)" v-hasPermi="['ygb:workerJob:edit']">修改</el-button>
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

    <el-dialog :title="dialogTitle" v-model="open" width="860px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="岗位名称" prop="title">
              <el-input v-model="form.title" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="企业名称" prop="enterpriseName">
              <el-input v-model="form.enterpriseName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位类型" prop="jobType">
              <el-input v-model="form.jobType" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工作地点" prop="workAddress">
              <el-input v-model="form.workAddress" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="最低薪资" prop="salaryMin">
              <el-input-number v-model="form.salaryMin" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="最高薪资" prop="salaryMax">
              <el-input-number v-model="form.salaryMax" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="薪资文案" prop="salaryText">
              <el-input v-model="form.salaryText" placeholder="如 7000-9000 元/月" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="招聘人数" prop="recruitCount">
              <el-input-number v-model="form.recruitCount" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系人" prop="contactName">
              <el-input v-model="form.contactName" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系电话" prop="contactMobile">
              <el-input v-model="form.contactMobile" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位经度" prop="longitude">
              <el-input-number v-model="form.longitude" :precision="6" :step="0.000001" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位纬度" prop="latitude">
              <el-input-number v-model="form.latitude" :precision="6" :step="0.000001" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio value="0">发布到移动端</el-radio>
                <el-radio value="1">停用/待审核</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发布时间" prop="publishTime">
              <el-date-picker v-model="form.publishTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="岗位描述" prop="description">
              <el-input v-model="form.description" type="textarea" :rows="3" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="任职要求" prop="requirementText">
              <el-input v-model="form.requirementText" type="textarea" :rows="3" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="cancel">取消</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailOpen" title="岗位详情" width="780px" append-to-body>
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="岗位ID">{{ detail.jobId }}</el-descriptions-item>
          <el-descriptions-item label="移动端状态">{{ detail.status === '0' ? '开放投递' : '关闭' }}</el-descriptions-item>
          <el-descriptions-item label="岗位名称">{{ detail.title || '-' }}</el-descriptions-item>
          <el-descriptions-item label="企业">{{ detail.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="岗位类型">{{ detail.jobType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="薪资">{{ detail.salaryText || formatSalary(detail) }}</el-descriptions-item>
          <el-descriptions-item label="工作地点" :span="2">{{ detail.workAddress || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ detail.contactName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ detail.contactMobile || '-' }}</el-descriptions-item>
          <el-descriptions-item label="岗位描述" :span="2">{{ detail.description || '-' }}</el-descriptions-item>
          <el-descriptions-item label="任职要求" :span="2">{{ detail.requirementText || '-' }}</el-descriptions-item>
          <el-descriptions-item label="移动端路径" :span="2">/pages/job/detail?jobId={{ detail.jobId }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="YgbCitizenServiceRecruitMarket">
import { computed, getCurrentInstance, reactive, ref, toRefs } from 'vue'
import {
  addCitizenWorkerJob,
  delCitizenWorkerJob,
  getCitizenWorkerJob,
  listCitizenWorkerJob,
  updateCitizenWorkerJob
} from '@/api/ygb/citizenService'

const { proxy } = getCurrentInstance()
const loading = ref(false)
const showSearch = ref(true)
const jobList = ref([])
const total = ref(0)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const open = ref(false)
const detailOpen = ref(false)
const detail = ref(null)
const dialogTitle = ref('')

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    title: undefined,
    enterpriseName: undefined,
    jobType: undefined,
    status: undefined
  },
  form: {},
  rules: {
    title: [{ required: true, message: '岗位名称不能为空', trigger: 'blur' }],
    enterpriseName: [{ required: true, message: '企业名称不能为空', trigger: 'blur' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

const stats = computed(() => {
  const rows = jobList.value || []
  const openCount = rows.filter(item => item.status === '0').length
  const enterpriseCount = new Set(rows.map(item => item.enterpriseName).filter(Boolean)).size
  const geoCount = rows.filter(item => item.longitude && item.latitude).length
  return [
    { key: 'total', label: '岗位总数', value: total.value, hint: '当前筛选范围' },
    { key: 'open', label: '开放投递', value: openCount, hint: '当前页已发布' },
    { key: 'enterprise', label: '招聘企业', value: enterpriseCount, hint: '当前页去重' },
    { key: 'geo', label: '带定位岗位', value: geoCount, hint: '可用于附近岗位' }
  ]
})

function getList() {
  loading.value = true
  listCitizenWorkerJob(queryParams.value).then(response => {
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
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.jobId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function reset() {
  form.value = {
    jobId: undefined,
    enterpriseId: undefined,
    enterpriseName: '',
    title: '',
    jobType: '',
    workAddress: '',
    longitude: undefined,
    latitude: undefined,
    salaryMin: undefined,
    salaryMax: undefined,
    salaryText: '',
    recruitCount: 1,
    contactName: '',
    contactMobile: '',
    description: '',
    requirementText: '',
    status: '0',
    publishTime: undefined
  }
  proxy.resetForm('formRef')
}

function handleAdd() {
  reset()
  open.value = true
  dialogTitle.value = '新增招聘岗位'
}

function handleUpdate(row) {
  reset()
  const jobId = row?.jobId || ids.value[0]
  getCitizenWorkerJob(jobId).then(response => {
    form.value = response.data || {}
    open.value = true
    dialogTitle.value = '修改招聘岗位'
  })
}

function submitForm() {
  proxy.$refs.formRef.validate(valid => {
    if (!valid) return
    const request = form.value.jobId ? updateCitizenWorkerJob(form.value) : addCitizenWorkerJob(form.value)
    request.then(() => {
      proxy.$modal.msgSuccess(form.value.jobId ? '修改成功' : '新增成功')
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const jobIds = row?.jobId || ids.value
  proxy.$modal.confirm(`是否确认删除招聘岗位编号为 "${jobIds}" 的数据项？`).then(() => {
    return delCitizenWorkerJob(jobIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

function cancel() {
  open.value = false
  reset()
}

function handleDetail(row) {
  getCitizenWorkerJob(row.jobId).then(response => {
    detail.value = response.data || row
    detailOpen.value = true
  })
}

function previewMobile(row) {
  getCitizenWorkerJob(row.jobId).then(response => {
    detail.value = response.data || row
    detailOpen.value = true
  })
}

function formatSalary(row) {
  if (row?.salaryText) return row.salaryText
  if (row?.salaryMin != null && row?.salaryMax != null) return `${row.salaryMin}-${row.salaryMax}`
  return '面议'
}

reset()
getList()
</script>

<style scoped>
.recruit-page {
  background: #f6f8fb;
}

.recruit-hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
  margin-bottom: 16px;
  padding: 24px 28px;
  border-radius: 8px;
  color: #fff;
  background: linear-gradient(135deg, #1e3a8a 0%, #0f766e 100%);
}

.recruit-hero__eyebrow {
  margin: 0 0 8px;
  font-size: 13px;
  opacity: 0.82;
}

.recruit-hero__title {
  margin: 0;
  font-size: 26px;
  font-weight: 700;
}

.recruit-hero__desc {
  max-width: 760px;
  margin: 10px 0 0;
  font-size: 14px;
  line-height: 1.7;
  opacity: 0.92;
}

.recruit-hero__meta {
  min-width: 240px;
  padding: 16px 18px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.14);
}

.recruit-hero__meta span,
.recruit-hero__meta strong {
  display: block;
}

.recruit-hero__meta span {
  margin-bottom: 8px;
  font-size: 12px;
  opacity: 0.82;
}

.recruit-stat-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.recruit-stat,
.recruit-panel {
  border: none;
  border-radius: 8px;
  background: #fff;
}

.recruit-stat {
  padding: 18px 20px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05);
}

.recruit-stat span,
.recruit-stat small {
  display: block;
  color: #64748b;
}

.recruit-stat strong {
  display: block;
  margin: 10px 0 8px;
  color: #0f172a;
  font-size: 28px;
  line-height: 1;
}

.recruit-panel {
  margin-bottom: 16px;
}

.recruit-table-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.recruit-table-head strong,
.recruit-table-head span {
  display: block;
}

.recruit-table-head span {
  margin-top: 4px;
  color: #64748b;
  font-size: 13px;
}

.main-cell {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.main-cell strong {
  color: #1f2937;
}

.main-cell span {
  color: #64748b;
  font-size: 12px;
}

@media (max-width: 1200px) {
  .recruit-stat-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .recruit-hero,
  .recruit-table-head {
    flex-direction: column;
    align-items: flex-start;
  }

  .recruit-hero__meta {
    width: 100%;
  }

  .recruit-stat-grid {
    grid-template-columns: 1fr;
  }
}
</style>
