<template>
  <div class="app-container mutual-page">
    <section class="mutual-hero">
      <div>
        <p class="mutual-hero__eyebrow">便民服务运营台</p>
        <h1 class="mutual-hero__title">互助圈权益服务处理</h1>
        <p class="mutual-hero__desc">
          承接移动端投诉举报、法律咨询和工会协同服务，统一查看诉求、处理进度、回复结果和协同回执。
        </p>
      </div>
      <div class="mutual-hero__meta">
        <span>移动端入口</span>
        <strong>/pages/complaint/index / /pages/legal/index / /pages/union/index</strong>
      </div>
    </section>

    <div class="mutual-stat-grid">
      <div v-for="item in stats" :key="item.key" class="mutual-stat">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
        <small>{{ item.hint }}</small>
      </div>
    </div>

    <el-card shadow="never" class="mutual-panel">
      <template #header>
        <div class="mutual-panel__head">
          <el-radio-group v-model="activeType" @change="handleTypeChange">
            <el-radio-button label="complaint">投诉举报</el-radio-button>
            <el-radio-button label="legal">法律咨询</el-radio-button>
          </el-radio-group>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
        </div>
      </template>

      <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch" label-width="88px">
        <el-form-item :label="activeConfig.typeLabel" :prop="activeConfig.typeProp">
          <el-input v-model="queryParams[activeConfig.typeProp]" :placeholder="`请输入${activeConfig.typeLabel}`" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="queryParams.title" placeholder="请输入诉求标题" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="劳动者" prop="personName">
          <el-input v-model="queryParams.personName" placeholder="请输入劳动者姓名" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="企业" prop="enterpriseName">
          <el-input v-model="queryParams.enterpriseName" placeholder="请输入企业名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" clearable style="width: 140px">
            <el-option v-for="item in activeConfig.statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="status-filter">
        <el-tag
          v-for="filter in activeConfig.statusFilters"
          :key="filter.value ?? 'all'"
          class="status-filter__tag"
          :effect="queryParams.status === filter.value ? 'dark' : 'plain'"
          :type="queryParams.status === filter.value ? filter.type : 'info'"
          @click="handleStatusFilter(filter.value)"
        >
          {{ filter.label }}
        </el-tag>
      </div>

      <el-table v-loading="loading" :data="recordList" :row-class-name="tableRowClassName">
        <el-table-column :label="activeConfig.idLabel" :prop="activeConfig.idProp" width="90" align="center" />
        <el-table-column label="诉求信息" min-width="260">
          <template #default="{ row }">
            <div class="main-cell">
              <strong>{{ row.title || '-' }}</strong>
              <span>{{ activeConfig.typeLabel }}：{{ row[activeConfig.typeProp] || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="劳动者 / 企业" min-width="220">
          <template #default="{ row }">
            <div class="main-cell">
              <strong>{{ row.personName || '-' }}</strong>
              <span>电话：{{ row.contactMobile || '-' }}；企业：{{ row.enterpriseName || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="工会协同" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="syncStatusTagType(resolveSyncInfo(row).status)">{{ resolveSyncInfo(row).text }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="activeConfig.replyLabel" prop="replyContent" min-width="220" show-overflow-tooltip />
        <el-table-column label="提交时间" prop="createTime" width="168" />
        <el-table-column :label="activeConfig.timeLabel" :prop="activeConfig.timeProp" width="168" />
        <el-table-column label="操作" width="170" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" icon="View" @click="handleDetail(row)">详情</el-button>
            <el-button link type="primary" icon="Edit" @click="handleProcess(row)" v-hasPermi="['ygb:workerMessage:flow']">处理</el-button>
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

    <el-dialog v-model="detailOpen" :title="`${activeConfig.title}详情`" width="900px" append-to-body>
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item :label="activeConfig.idLabel">{{ detail[activeConfig.idProp] }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ statusLabel(detail.status) }}</el-descriptions-item>
          <el-descriptions-item :label="activeConfig.typeLabel">{{ detail[activeConfig.typeProp] || '-' }}</el-descriptions-item>
          <el-descriptions-item label="标题">{{ detail.title || '-' }}</el-descriptions-item>
          <el-descriptions-item label="劳动者">{{ detail.personName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ detail.contactMobile || '-' }}</el-descriptions-item>
          <el-descriptions-item label="所属企业" :span="2">{{ detail.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="诉求内容" :span="2">{{ detail.content || '-' }}</el-descriptions-item>
          <el-descriptions-item label="附件材料" :span="2">{{ detail.attachments || '-' }}</el-descriptions-item>
          <el-descriptions-item :label="activeConfig.replyLabel" :span="2">{{ detail.replyContent || '-' }}</el-descriptions-item>
          <el-descriptions-item :label="activeConfig.timeLabel">{{ detail[activeConfig.timeProp] || '-' }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ detail.createTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="工会协同">{{ resolveSyncInfo(detail).text }}</el-descriptions-item>
          <el-descriptions-item label="协同回执">{{ resolveSyncInfo(detail).message }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </el-dialog>

    <el-dialog v-model="processOpen" :title="`处理${activeConfig.title}`" width="620px" append-to-body>
      <el-alert class="process-alert" type="info" :closable="false" show-icon :title="activeConfig.processHint" />
      <el-form ref="processRef" :model="processForm" :rules="rules" label-width="92px">
        <el-form-item label="处理状态" prop="status">
          <el-select v-model="processForm.status" style="width: 100%" @change="handleStatusChange">
            <el-option v-for="item in activeConfig.statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item :label="activeConfig.timeLabel" prop="handleTimeText">
          <el-input v-model="processForm.handleTimeText" placeholder="例如 2026-06-29 10:30" />
        </el-form-item>
        <el-form-item :label="activeConfig.replyLabel" prop="replyContent">
          <el-input v-model="processForm.replyContent" type="textarea" :rows="4" placeholder="请输入处理/回复内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitProcess">确定</el-button>
        <el-button @click="processOpen = false">取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="YgbCitizenServiceMutualHelp">
import { computed, getCurrentInstance, reactive, ref, toRefs } from 'vue'
import {
  getCitizenComplaint,
  getCitizenLegalConsult,
  listCitizenComplaint,
  listCitizenLegalConsult,
  updateCitizenComplaintStatus,
  updateCitizenLegalConsultStatus
} from '@/api/ygb/citizenService'

const { proxy } = getCurrentInstance()
const loading = ref(false)
const showSearch = ref(true)
const activeType = ref('complaint')
const recordList = ref([])
const total = ref(0)
const detail = ref(null)
const detailOpen = ref(false)
const processOpen = ref(false)
const currentId = ref(undefined)

const configMap = {
  complaint: {
    title: '投诉举报',
    idLabel: '投诉ID',
    idProp: 'complaintId',
    typeLabel: '投诉类型',
    typeProp: 'complaintType',
    replyLabel: '处理反馈',
    timeLabel: '处理时间',
    timeProp: 'handleTimeText',
    processHint: '投诉处理应补齐办理状态、处理时间和明确反馈内容，便于移动端劳动者查询进度。',
    statusOptions: [
      { label: '待处理', value: '0' },
      { label: '处理中', value: '1' },
      { label: '已办结', value: '2' }
    ],
    statusFilters: [
      { label: '全部', value: undefined, type: 'info' },
      { label: '待处理', value: '0', type: 'danger' },
      { label: '处理中', value: '1', type: 'warning' },
      { label: '已办结', value: '2', type: 'success' }
    ]
  },
  legal: {
    title: '法律咨询',
    idLabel: '咨询ID',
    idProp: 'consultId',
    typeLabel: '咨询类型',
    typeProp: 'consultType',
    replyLabel: '回复内容',
    timeLabel: '回复时间',
    timeProp: 'replyTimeText',
    processHint: '法律咨询处理应形成可追溯答复，必要时结合工会协同回执补充说明。',
    statusOptions: [
      { label: '待回复', value: '0' },
      { label: '已回复', value: '1' },
      { label: '已办结', value: '2' }
    ],
    statusFilters: [
      { label: '全部', value: undefined, type: 'info' },
      { label: '待回复', value: '0', type: 'danger' },
      { label: '已回复', value: '1', type: 'warning' },
      { label: '已办结', value: '2', type: 'success' }
    ]
  }
}

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    complaintType: undefined,
    consultType: undefined,
    title: undefined,
    personName: undefined,
    enterpriseName: undefined,
    status: undefined
  },
  processForm: {
    status: '1',
    handleTimeText: '',
    replyContent: ''
  },
  rules: {
    status: [{ required: true, message: '状态不能为空', trigger: 'change' }],
    replyContent: [{ required: true, message: '处理/回复内容不能为空', trigger: 'blur' }]
  }
})

const { queryParams, processForm, rules } = toRefs(data)
const activeConfig = computed(() => configMap[activeType.value])

const stats = computed(() => {
  const rows = recordList.value || []
  const pending = rows.filter(item => item.status === '0').length
  const processing = rows.filter(item => item.status === '1').length
  const completed = rows.filter(item => item.status === '2').length
  const syncFollow = rows.filter(item => ['PENDING', 'FAIL'].includes(resolveSyncInfo(item).status)).length
  return [
    { key: 'all', label: '筛选总数', value: total.value, hint: activeConfig.value.title },
    { key: 'pending', label: activeType.value === 'complaint' ? '待处理' : '待回复', value: pending, hint: '当前页' },
    { key: 'processing', label: activeType.value === 'complaint' ? '处理中' : '已回复', value: processing, hint: '当前页' },
    { key: 'completed', label: '已办结', value: completed, hint: '当前页' },
    { key: 'sync', label: '协同待跟踪', value: syncFollow, hint: '工会协同处理中或失败' }
  ]
})

function getList() {
  loading.value = true
  const api = activeType.value === 'complaint' ? listCitizenComplaint : listCitizenLegalConsult
  api(queryParams.value).then(response => {
    recordList.value = response.rows || []
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
  queryParams.value.complaintType = undefined
  queryParams.value.consultType = undefined
  queryParams.value.status = undefined
  handleQuery()
}

function handleTypeChange() {
  resetQuery()
}

function handleStatusFilter(status) {
  queryParams.value.status = status
  handleQuery()
}

function handleDetail(row) {
  const id = row[activeConfig.value.idProp]
  const api = activeType.value === 'complaint' ? getCitizenComplaint : getCitizenLegalConsult
  api(id).then(response => {
    detail.value = response.data || row
    detailOpen.value = true
  })
}

function handleProcess(row) {
  currentId.value = row[activeConfig.value.idProp]
  processForm.value = {
    status: row.status || '1',
    handleTimeText: row[activeConfig.value.timeProp] || '',
    replyContent: row.replyContent || ''
  }
  normalizeProcessForm()
  processOpen.value = true
  nextTick(() => proxy.$refs.processRef?.clearValidate())
}

function handleStatusChange() {
  normalizeProcessForm()
}

function normalizeProcessForm() {
  if ((processForm.value.status === '1' || processForm.value.status === '2') && !processForm.value.handleTimeText) {
    processForm.value.handleTimeText = formatNow()
  }
}

function submitProcess() {
  proxy.$refs.processRef.validate(valid => {
    if (!valid || !currentId.value) return
    const api = activeType.value === 'complaint' ? updateCitizenComplaintStatus : updateCitizenLegalConsultStatus
    api(currentId.value, processForm.value).then(() => {
      proxy.$modal.msgSuccess('处理状态已更新')
      processOpen.value = false
      getList()
    })
  })
}

function statusLabel(value) {
  return activeConfig.value.statusOptions.find(item => item.value === value)?.label || value || '-'
}

function statusTagType(value) {
  if (value === '2') return 'success'
  if (value === '1') return 'warning'
  return 'danger'
}

function tableRowClassName({ row }) {
  if (row.status === '0') return 'row-pending'
  if (row.status === '1') return 'row-processing'
  return ''
}

function resolveSyncInfo(row) {
  const defaultStatus = row?.syncUnionFlag === '2' ? 'FAIL' : row?.syncUnionFlag === '1' ? 'SUCCESS' : 'PENDING'
  const defaultInfo = {
    status: defaultStatus,
    text: syncStatusText(defaultStatus),
    message: defaultStatus === 'SUCCESS' ? '工会协同成功' : defaultStatus === 'FAIL' ? '工会协同失败' : '工会协同处理中'
  }
  if (!row?.remark) return defaultInfo
  try {
    const unionSync = JSON.parse(row.remark)?.unionSync || {}
    const status = (unionSync.syncStatus || defaultStatus).toUpperCase()
    return {
      status,
      text: syncStatusText(status),
      message: unionSync.syncMessage || defaultInfo.message
    }
  } catch {
    return defaultInfo
  }
}

function syncStatusText(status) {
  if (status === 'SUCCESS') return '协同成功'
  if (status === 'FAIL') return '协同失败'
  return '协同处理中'
}

function syncStatusTagType(status) {
  if (status === 'SUCCESS') return 'success'
  if (status === 'FAIL') return 'danger'
  return 'warning'
}

function formatNow() {
  const date = new Date()
  const pad = value => String(value).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}

getList()
</script>

<style scoped>
.mutual-page {
  background: #f6f8fb;
}

.mutual-hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
  margin-bottom: 16px;
  padding: 24px 28px;
  border-radius: 8px;
  color: #fff;
  background: linear-gradient(135deg, #7f1d1d 0%, #b45309 100%);
}

.mutual-hero__eyebrow {
  margin: 0 0 8px;
  font-size: 13px;
  opacity: 0.82;
}

.mutual-hero__title {
  margin: 0;
  font-size: 26px;
  font-weight: 700;
}

.mutual-hero__desc {
  max-width: 760px;
  margin: 10px 0 0;
  font-size: 14px;
  line-height: 1.7;
  opacity: 0.92;
}

.mutual-hero__meta {
  min-width: 260px;
  padding: 16px 18px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.14);
}

.mutual-hero__meta span,
.mutual-hero__meta strong {
  display: block;
}

.mutual-hero__meta span {
  margin-bottom: 8px;
  font-size: 12px;
  opacity: 0.82;
}

.mutual-hero__meta strong {
  font-size: 13px;
  word-break: break-all;
}

.mutual-stat-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.mutual-stat,
.mutual-panel {
  border: none;
  border-radius: 8px;
  background: #fff;
}

.mutual-stat {
  padding: 18px 20px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05);
}

.mutual-stat span,
.mutual-stat small {
  display: block;
  color: #64748b;
}

.mutual-stat strong {
  display: block;
  margin: 10px 0 8px;
  color: #0f172a;
  font-size: 28px;
  line-height: 1;
}

.mutual-panel__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.status-filter {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 14px;
}

.status-filter__tag {
  cursor: pointer;
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

.process-alert {
  margin-bottom: 16px;
}

:deep(.el-table .row-pending) {
  --el-table-tr-bg-color: #fff2f0;
}

:deep(.el-table .row-processing) {
  --el-table-tr-bg-color: #fff8eb;
}

@media (max-width: 1200px) {
  .mutual-stat-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .mutual-hero,
  .mutual-panel__head {
    flex-direction: column;
    align-items: flex-start;
  }

  .mutual-hero__meta {
    width: 100%;
  }

  .mutual-stat-grid {
    grid-template-columns: 1fr;
  }
}
</style>
