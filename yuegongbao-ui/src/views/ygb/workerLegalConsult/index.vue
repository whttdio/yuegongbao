<template>
  <div class="app-container page-shell">
    <el-card shadow="never" class="hero-card">
      <div class="hero-card__body">
        <div>
          <div class="hero-card__eyebrow">用工宝 / 劳动者服务</div>
          <div class="hero-card__title">法律咨询处理</div>
          <div class="hero-card__desc">
            统一查看法律咨询记录、工会协同回执和回复进展，支持直接更新回复结果。
          </div>
        </div>
        <div class="hero-card__meta">
          <div class="hero-card__meta-label">当前筛选总数</div>
          <div class="hero-card__meta-value">{{ total }}</div>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="panel-card search-card">
      <el-form
        ref="queryRef"
        :model="queryParams"
        :inline="true"
        v-show="showSearch"
        label-width="88px"
      >
        <el-form-item label="咨询类型" prop="consultType">
          <el-input
            v-model="queryParams.consultType"
            placeholder="请输入咨询类型"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="咨询标题" prop="title">
          <el-input
            v-model="queryParams.title"
            placeholder="请输入咨询标题"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="劳动者" prop="personName">
          <el-input
            v-model="queryParams.personName"
            placeholder="请输入劳动者姓名"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="所属企业" prop="enterpriseName">
          <el-input
            v-model="queryParams.enterpriseName"
            placeholder="请输入企业名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="回复状态" prop="status">
          <el-select v-model="queryParams.status" clearable style="width: 140px">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="overview-grid">
      <div
        v-for="card in overviewCards"
        :key="card.key"
        class="overview-card"
        :class="`overview-card--${card.tone}`"
      >
        <div class="overview-card__label">{{ card.label }}</div>
        <div class="overview-card__value">{{ card.value }}</div>
        <div class="overview-card__hint">{{ card.hint }}</div>
      </div>
    </div>

    <el-card shadow="never" class="panel-card table-card">
      <template #header>
        <div class="table-card__header">
          <div class="table-card__tabs">
            <el-tag
              v-for="filter in statusFilters"
              :key="filter.value"
              class="table-card__tag"
              :effect="queryParams.status === filter.value ? 'dark' : 'plain'"
              :type="queryParams.status === filter.value ? filter.type : 'info'"
              @click="handleStatusFilter(filter.value)"
            >
              {{ filter.label }}
            </el-tag>
          </div>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="consultList"
        :row-class-name="tableRowClassName"
      >
        <el-table-column label="咨询ID" prop="consultId" width="90" align="center" />
        <el-table-column label="咨询信息" min-width="260">
          <template #default="{ row }">
            <div class="main-cell">
              <div class="main-cell__title">{{ row.title || '-' }}</div>
              <div class="main-cell__sub">
                <span>类型：{{ row.consultType || '-' }}</span>
                <span>电话：{{ row.contactMobile || '-' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="劳动者 / 企业" min-width="220">
          <template #default="{ row }">
            <div class="main-cell">
              <div class="main-cell__title">{{ row.personName || '-' }}</div>
              <div class="main-cell__sub">
                <span>企业：{{ row.enterpriseName || '-' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="回复状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="工会协同" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="syncStatusTagType(resolveSyncInfo(row).status)">
              {{ resolveSyncInfo(row).text }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="回复内容" prop="replyContent" min-width="220" show-overflow-tooltip />
        <el-table-column label="提交时间" prop="createTime" width="168" />
        <el-table-column label="回复时间" prop="replyTimeText" width="168" />
        <el-table-column label="操作" width="170" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" icon="View" @click="handleDetail(row)">详情</el-button>
            <el-button
              link
              type="primary"
              icon="Edit"
              v-hasPermi="['ygb:workerMessage:flow']"
              @click="handleProcess(row)"
            >
              处理
            </el-button>
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

    <el-dialog v-model="detailOpen" title="法律咨询详情" width="960px" append-to-body>
      <template v-if="detail">
        <div class="detail-banner">
          <div>
            <div class="detail-banner__title">{{ detail.title || '-' }}</div>
            <div class="detail-banner__sub">
              咨询类型：{{ detail.consultType || '-' }} | 劳动者：{{ detail.personName || '-' }}
            </div>
          </div>
          <div class="detail-banner__tags">
            <el-tag :type="statusTagType(detail.status)">{{ statusLabel(detail.status) }}</el-tag>
            <el-tag :type="syncStatusTagType(detailSyncInfo.status)">{{ detailSyncInfo.text }}</el-tag>
          </div>
        </div>

        <el-row :gutter="16">
          <el-col :span="15">
            <el-card shadow="never" class="detail-section">
              <template #header>
                <div class="detail-section__title">咨询信息</div>
              </template>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="咨询ID">{{ detail.consultId }}</el-descriptions-item>
                <el-descriptions-item label="回复状态">{{ statusLabel(detail.status) }}</el-descriptions-item>
                <el-descriptions-item label="咨询类型">{{ detail.consultType || '-' }}</el-descriptions-item>
                <el-descriptions-item label="咨询标题">{{ detail.title || '-' }}</el-descriptions-item>
                <el-descriptions-item label="劳动者">{{ detail.personName || '-' }}</el-descriptions-item>
                <el-descriptions-item label="联系电话">{{ detail.contactMobile || '-' }}</el-descriptions-item>
                <el-descriptions-item label="所属企业" :span="2">{{ detail.enterpriseName || '-' }}</el-descriptions-item>
                <el-descriptions-item label="咨询内容" :span="2">{{ detail.content || '-' }}</el-descriptions-item>
                <el-descriptions-item label="附件材料" :span="2">{{ detail.attachments || '-' }}</el-descriptions-item>
                <el-descriptions-item label="回复内容" :span="2">{{ detail.replyContent || '-' }}</el-descriptions-item>
                <el-descriptions-item label="回复时间">{{ detail.replyTimeText || '-' }}</el-descriptions-item>
                <el-descriptions-item label="提交时间">{{ detail.createTime || '-' }}</el-descriptions-item>
                <el-descriptions-item label="处理人">{{ detail.updateBy || '-' }}</el-descriptions-item>
                <el-descriptions-item label="最近更新">{{ detail.updateTime || '-' }}</el-descriptions-item>
              </el-descriptions>
            </el-card>
          </el-col>
          <el-col :span="9">
            <el-card shadow="never" class="detail-section">
              <template #header>
                <div class="detail-section__title">工会协同回执</div>
              </template>
              <div class="sync-panel">
                <div class="sync-panel__status">
                  <el-tag :type="syncStatusTagType(detailSyncInfo.status)">
                    {{ detailSyncInfo.text }}
                  </el-tag>
                </div>
                <div class="sync-panel__item">
                  <div class="sync-panel__label">回执信息</div>
                  <div class="sync-panel__value">{{ detailSyncInfo.message }}</div>
                </div>
                <div class="sync-panel__item">
                  <div class="sync-panel__label">工单号</div>
                  <div class="sync-panel__value">{{ detailSyncInfo.ticketNo || '-' }}</div>
                </div>
                <div class="sync-panel__item">
                  <div class="sync-panel__label">外部流水号</div>
                  <div class="sync-panel__value">{{ detailSyncInfo.externalSerialNo || '-' }}</div>
                </div>
                <div class="sync-panel__item">
                  <div class="sync-panel__label">回调时间</div>
                  <div class="sync-panel__value">{{ detailSyncInfo.callbackTime || '-' }}</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </template>
    </el-dialog>

    <el-dialog v-model="processOpen" title="处理法律咨询" width="620px" append-to-body>
      <el-alert
        class="process-alert"
        type="info"
        :closable="false"
        show-icon
        :title="processHintText"
      />
      <el-form ref="processRef" :model="processForm" :rules="rules" label-width="92px">
        <el-form-item label="回复状态" prop="status">
          <el-select v-model="processForm.status" style="width: 100%" @change="handleStatusChange">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="回复时间" prop="handleTimeText">
          <el-input v-model="processForm.handleTimeText" placeholder="例如 2026-06-11 10:30" />
        </el-form-item>
        <el-form-item label="回复内容" prop="replyContent">
          <el-input
            v-model="processForm.replyContent"
            type="textarea"
            :rows="4"
            placeholder="请输入回复内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitProcess">确定</el-button>
        <el-button @click="processOpen = false">取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="YgbWorkerLegalConsult">
import { computed } from 'vue'
import { getWorkerLegalConsult, listWorkerLegalConsult, updateWorkerLegalConsultStatus } from '@/api/ygb/workerMessage'

const { proxy } = getCurrentInstance()

const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const consultList = ref([])
const detail = ref(null)
const detailOpen = ref(false)
const processOpen = ref(false)
const currentConsultId = ref(undefined)

const statusOptions = [
  { label: '待回复', value: '0' },
  { label: '已回复', value: '1' },
  { label: '已办结', value: '2' }
]

const statusFilters = [
  { label: '全部', value: undefined, type: 'info' },
  { label: '待回复', value: '0', type: 'danger' },
  { label: '已回复', value: '1', type: 'warning' },
  { label: '已办结', value: '2', type: 'success' }
]

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
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
    status: [{ required: true, message: '回复状态不能为空', trigger: 'change' }],
    replyContent: [{ required: true, message: '回复内容不能为空', trigger: 'blur' }]
  }
})

const { queryParams, processForm, rules } = toRefs(data)

const overviewCards = computed(() => {
  const rows = consultList.value || []
  const pendingCount = rows.filter(item => item.status === '0').length
  const repliedCount = rows.filter(item => item.status === '1').length
  const closedCount = rows.filter(item => item.status === '2').length
  const syncFollowCount = rows.filter(item => {
    const syncInfo = resolveSyncInfo(item)
    return syncInfo.status === 'PENDING' || syncInfo.status === 'FAIL'
  }).length
  return [
    { key: 'all', label: '筛选结果', value: total.value, hint: '当前条件下的法律咨询记录', tone: 'blue' },
    { key: 'pending', label: '待回复', value: pendingCount, hint: '尚未形成正式回复的咨询', tone: 'red' },
    { key: 'replied', label: '已回复', value: repliedCount, hint: '已回复但还可继续补充处理', tone: 'amber' },
    { key: 'closed', label: '已办结', value: closedCount, hint: '处理闭环已经完成', tone: 'green' },
    { key: 'sync', label: '待跟踪协同', value: syncFollowCount, hint: '工会协同处理中或失败记录', tone: 'slate' }
  ]
})

const detailSyncInfo = computed(() => resolveSyncInfo(detail.value))

const processHintText = computed(() => {
  if (processForm.value.status === '0') {
    return '待回复适合保留在初始状态，通常用于尚未形成明确答复时。'
  }
  if (processForm.value.status === '1') {
    return '已回复表示已给出咨询意见，建议同步补齐回复时间和主要答复内容。'
  }
  return '已办结表示咨询处理闭环完成，建议确认答复内容已经完整可追溯。'
})

function getList() {
  loading.value = true
  listWorkerLegalConsult(queryParams.value).then(response => {
    consultList.value = response.rows || []
    total.value = response.total || 0
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  handleStatusFilter(undefined)
}

function handleStatusFilter(status) {
  queryParams.value.status = status
  handleQuery()
}

function handleDetail(row) {
  getWorkerLegalConsult(row.consultId).then(response => {
    detail.value = response.data
    detailOpen.value = true
  })
}

function handleProcess(row) {
  currentConsultId.value = row.consultId
  processForm.value = {
    status: row.status || '1',
    handleTimeText: row.replyTimeText || '',
    replyContent: row.replyContent || ''
  }
  normalizeProcessForm()
  processOpen.value = true
  nextTick(() => {
    proxy.$refs.processRef?.clearValidate()
  })
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
    if (!valid || !currentConsultId.value) return
    updateWorkerLegalConsultStatus(currentConsultId.value, processForm.value).then(() => {
      proxy.$modal.msgSuccess('法律咨询处理状态已更新')
      processOpen.value = false
      getList()
    })
  })
}

function statusLabel(value) {
  return statusOptions.find(item => item.value === value)?.label || value || '-'
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
  const defaultInfo = {
    status: 'PENDING',
    text: '协同处理中',
    message: '工会协同处理中',
    ticketNo: '',
    externalSerialNo: '',
    callbackTime: ''
  }
  if (!row?.remark) {
    return defaultInfo
  }
  try {
    const remark = JSON.parse(row.remark)
    const unionSync = remark?.unionSync || {}
    const rawStatus = unionSync.syncStatus || 'PENDING'
    const status = rawStatus.toUpperCase()
    return {
      status,
      text: syncStatusText(status),
      message: unionSync.syncMessage || defaultInfo.message,
      ticketNo: unionSync.ticketNo || '',
      externalSerialNo: unionSync.externalSerialNo || '',
      callbackTime: unionSync.callbackTime || ''
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
.page-shell {
  background: #f5f7fb;
}

.hero-card,
.panel-card {
  border: none;
  border-radius: 16px;
}

.hero-card {
  margin-bottom: 16px;
  background: linear-gradient(135deg, #165d59 0%, #25968e 58%, #94e4d7 100%);
}

.hero-card__body {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  align-items: center;
  color: #fff;
}

.hero-card__eyebrow {
  margin-bottom: 10px;
  font-size: 13px;
  letter-spacing: 1px;
  opacity: 0.85;
}

.hero-card__title {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.2;
}

.hero-card__desc {
  margin-top: 12px;
  max-width: 620px;
  font-size: 14px;
  line-height: 1.7;
  opacity: 0.92;
}

.hero-card__meta {
  min-width: 180px;
  padding: 20px 24px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.16);
  backdrop-filter: blur(6px);
}

.hero-card__meta-label {
  font-size: 13px;
  opacity: 0.88;
}

.hero-card__meta-value {
  margin-top: 10px;
  font-size: 34px;
  font-weight: 700;
}

.search-card {
  margin-bottom: 16px;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.overview-card {
  padding: 18px 20px;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.06);
}

.overview-card__label {
  font-size: 13px;
  color: #6b7280;
}

.overview-card__value {
  margin-top: 14px;
  font-size: 28px;
  font-weight: 700;
  line-height: 1;
}

.overview-card__hint {
  margin-top: 12px;
  font-size: 12px;
  line-height: 1.6;
  color: #6b7280;
}

.overview-card--blue {
  border-top: 4px solid #2e86de;
}

.overview-card--red {
  border-top: 4px solid #dc2626;
}

.overview-card--amber {
  border-top: 4px solid #f59e0b;
}

.overview-card--green {
  border-top: 4px solid #16a34a;
}

.overview-card--slate {
  border-top: 4px solid #64748b;
}

.table-card__header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
}

.table-card__tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.table-card__tag {
  cursor: pointer;
}

.main-cell__title {
  font-weight: 600;
  color: #1f2937;
  line-height: 1.5;
}

.main-cell__sub {
  margin-top: 6px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 12px;
  color: #6b7280;
}

.detail-banner {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  margin-bottom: 16px;
  padding: 18px 20px;
  border-radius: 14px;
  background: linear-gradient(135deg, #ecfdf5 0%, #f5fffc 100%);
}

.detail-banner__title {
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
}

.detail-banner__sub {
  margin-top: 8px;
  font-size: 13px;
  color: #64748b;
}

.detail-banner__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.detail-section {
  height: 100%;
}

.detail-section__title {
  font-weight: 600;
  color: #1f2937;
}

.sync-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.sync-panel__status {
  display: flex;
  align-items: center;
}

.sync-panel__item {
  padding: 14px 16px;
  border-radius: 12px;
  background: #f8fafc;
}

.sync-panel__label {
  font-size: 12px;
  color: #64748b;
}

.sync-panel__value {
  margin-top: 8px;
  font-size: 14px;
  line-height: 1.7;
  color: #1f2937;
  word-break: break-all;
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
  .overview-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .hero-card__body,
  .table-card__header,
  .detail-banner {
    flex-direction: column;
    align-items: flex-start;
  }

  .hero-card__meta {
    width: 100%;
  }

  .overview-grid {
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>
