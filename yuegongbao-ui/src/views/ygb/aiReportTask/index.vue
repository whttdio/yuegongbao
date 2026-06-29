<template>
  <div class="app-container">
    <div class="summary-grid">
      <div class="summary-card">
        <div class="summary-card__label">任务总量</div>
        <div class="summary-card__value">{{ summary.totalCount || 0 }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-card__label">待处理</div>
        <div class="summary-card__value">{{ summary.pendingCount || 0 }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-card__label">处理中</div>
        <div class="summary-card__value">{{ summary.processingCount || 0 }}</div>
      </div>
      <div class="summary-card summary-card--warning">
        <div class="summary-card__label">已逾期</div>
        <div class="summary-card__value">{{ summary.overdueCount || 0 }}</div>
      </div>
    </div>

    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>AI 建议任务</span>
          <div class="card-actions">
            <el-button type="primary" @click="handleAdd" v-hasPermi="['ygb:aiReportTask:add']">新增任务</el-button>
            <el-button type="warning" @click="handleExport" v-hasPermi="['ygb:aiReportTask:export']">导出</el-button>
          </div>
        </div>
      </template>

      <el-form ref="queryRef" :model="queryParams" :inline="true">
        <el-form-item label="任务名称">
          <el-input v-model="queryParams.taskName" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="报告类型">
          <el-select v-model="queryParams.reportType" clearable style="width: 160px">
            <el-option v-for="item in reportTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="任务类型">
          <el-select v-model="queryParams.taskType" clearable style="width: 160px">
            <el-option v-for="item in taskTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="区域">
          <el-select v-model="queryParams.regionCode" clearable style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="风险等级">
          <el-select v-model="queryParams.riskLevel" clearable style="width: 160px">
            <el-option v-for="item in riskLevelOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.handleStatus" clearable style="width: 160px">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="taskList">
        <el-table-column label="任务名称" prop="taskName" min-width="180" show-overflow-tooltip />
        <el-table-column label="报告ID" prop="reportId" width="100" />
        <el-table-column label="报告类型" width="120">
          <template #default="scope">{{ reportTypeLabel(scope.row.reportType) }}</template>
        </el-table-column>
        <el-table-column label="任务类型" width="120">
          <template #default="scope">{{ taskTypeLabel(scope.row.taskType) }}</template>
        </el-table-column>
        <el-table-column label="区域" prop="regionName" width="140" />
        <el-table-column label="风险等级" width="100">
          <template #default="scope">
            <el-tag :type="riskTagType(scope.row.riskLevel)" effect="plain">{{ riskLevelLabel(scope.row.riskLevel) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="接收人" prop="receiveUser" width="120" />
        <el-table-column label="状态" width="120">
          <template #default="scope">
            <el-tag :type="statusTagType(scope.row.handleStatus)" effect="plain">{{ statusLabel(scope.row.handleStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="截止时间" prop="dueDate" width="170" />
        <el-table-column label="建议摘要" prop="suggestionText" min-width="220" show-overflow-tooltip />
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['ygb:aiReportTask:edit']">修改</el-button>
            <el-button link type="info" @click="handleView(scope.row)" v-hasPermi="['ygb:aiReportTask:query']">详情</el-button>
            <el-button link type="success" :disabled="!canChangeStatus(scope.row, 'processing')" @click="changeStatus(scope.row, 'processing')" v-hasPermi="['ygb:aiReportTask:status']">开始处理</el-button>
            <el-button link type="warning" :disabled="!canChangeStatus(scope.row, 'closed')" @click="changeStatus(scope.row, 'closed')" v-hasPermi="['ygb:aiReportTask:status']">办结</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)" v-hasPermi="['ygb:aiReportTask:remove']">删除</el-button>
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

    <el-dialog v-model="open" :title="title" width="860px">
      <el-form ref="taskRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="报告ID" prop="reportId">
              <el-input-number v-model="form.reportId" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务名称" prop="taskName">
              <el-input v-model="form.taskName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务类型" prop="taskType">
              <el-select v-model="form.taskType">
                <el-option v-for="item in taskTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="报告类型" prop="reportType">
              <el-select v-model="form.reportType">
                <el-option v-for="item in reportTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="区域" prop="regionCode">
              <el-select v-model="form.regionCode">
                <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="风险等级" prop="riskLevel">
              <el-select v-model="form.riskLevel">
                <el-option v-for="item in riskLevelOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="接收人" prop="receiveUser">
              <el-input v-model="form.receiveUser" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="接收部门" prop="receiveDept">
              <el-input v-model="form.receiveDept" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="handleStatus">
              <el-select v-model="form.handleStatus">
                <el-option v-for="item in editableStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="截止时间" prop="dueDate">
              <el-date-picker v-model="form.dueDate" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="报告周期" prop="reportPeriod">
          <el-input v-model="form.reportPeriod" />
        </el-form-item>
        <el-form-item label="报告摘要" prop="reportSummary">
          <el-input v-model="form.reportSummary" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="建议动作" prop="suggestionText">
          <el-input v-model="form.suggestionText" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="处置反馈" prop="feedbackText">
          <el-input v-model="form.feedbackText" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="open = false">取消</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailOpen" title="任务详情" width="820px">
      <el-descriptions :column="2" border v-if="detail">
        <el-descriptions-item label="任务名称">{{ detail.taskName }}</el-descriptions-item>
        <el-descriptions-item label="报告ID">{{ detail.reportId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="报告类型">{{ reportTypeLabel(detail.reportType) }}</el-descriptions-item>
        <el-descriptions-item label="任务类型">{{ taskTypeLabel(detail.taskType) }}</el-descriptions-item>
        <el-descriptions-item label="区域">{{ detail.regionName || detail.regionCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="风险等级">{{ riskLevelLabel(detail.riskLevel) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ statusLabel(detail.handleStatus) }}</el-descriptions-item>
        <el-descriptions-item label="截止时间">{{ detail.dueDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="接收人">{{ detail.receiveUser || '-' }}</el-descriptions-item>
        <el-descriptions-item label="接收部门">{{ detail.receiveDept || '-' }}</el-descriptions-item>
        <el-descriptions-item label="报告周期" :span="2">{{ detail.reportPeriod || '-' }}</el-descriptions-item>
        <el-descriptions-item label="报告摘要" :span="2">{{ detail.reportSummary || '-' }}</el-descriptions-item>
        <el-descriptions-item label="建议动作" :span="2">{{ detail.suggestionText || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处置反馈" :span="2">{{ detail.feedbackText || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup name="YgbAiReportTask">
import { computed, getCurrentInstance, onMounted, reactive, ref, toRefs, watchEffect } from 'vue'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import { useRoute } from 'vue-router'
import { getAiReport } from '@/api/ygb/aiReport'
import {
  addAiReportTask,
  delAiReportTask,
  getAiReportTask,
  getAiReportTaskSummary,
  listAiReportTask,
  updateAiReportTask,
  updateAiReportTaskStatus
} from '@/api/ygb/aiReportTask'
import { useAuthorizedRegionOptions } from '@/utils/regionScope'
import { gdRegionOptions } from '@/utils/regionName'

const { proxy } = getCurrentInstance()
const route = useRoute()
const loading = ref(false)
const total = ref(0)
const taskList = ref([])
const open = ref(false)
const detailOpen = ref(false)
const title = ref('')
const detail = ref(null)
const summary = ref({})
const { setPageGuide } = useWorkbenchAssist()

const regionOptions = useAuthorizedRegionOptions(gdRegionOptions)

const reportTypeOptions = [
  { label: '日报', value: 'DAILY' },
  { label: '周报', value: 'WEEKLY' },
  { label: '月报', value: 'MONTHLY' }
]

const taskTypeOptions = [
  { label: '监管任务', value: 'regulate' },
  { label: '复核任务', value: 'review' }
]

const riskLevelOptions = [
  { label: '高风险', value: 'HIGH' },
  { label: '中风险', value: 'MEDIUM' },
  { label: '低风险', value: 'LOW' }
]

const statusOptions = [
  { label: '草稿', value: 'draft' },
  { label: '待处理', value: 'pending' },
  { label: '处理中', value: 'processing' },
  { label: '已办结', value: 'closed' },
  { label: '已驳回', value: 'rejected' },
  { label: '已逾期', value: 'overdue' }
]

const editableStatusOptions = statusOptions.filter(item => ['draft', 'pending'].includes(item.value))

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    taskName: undefined,
    reportType: undefined,
    taskType: undefined,
    regionCode: undefined,
    riskLevel: undefined,
    handleStatus: undefined
  },
  form: {},
  rules: {
    taskName: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
    taskType: [{ required: true, message: '请选择任务类型', trigger: 'change' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

const selectedTaskOverview = computed(() => {
  const record = detail.value || {}
  return [
    { label: '任务名称', value: record.taskName || '-' },
    { label: '报告类型', value: reportTypeLabel(record.reportType) },
    { label: '任务类型', value: taskTypeLabel(record.taskType) },
    { label: '处理状态', value: statusLabel(record.handleStatus) }
  ]
})

const taskWorkflow = computed(() => ([
  { label: '生成建议', desc: '从 AI 监测报告生成监管建议和任务事项' },
  { label: '分派处置', desc: '按区域、风险等级和接收人推进处置闭环' },
  { label: '反馈归档', desc: '记录处理反馈并沉淀整改结果' }
]))

const taskHintTags = computed(() => {
  const tags = []
  if (Number(summary.value.pendingCount || 0) > 0) {
    tags.push({ label: `待处理 ${summary.value.pendingCount} 项建议任务`, type: 'warning' })
  }
  if (Number(summary.value.processingCount || 0) > 0) {
    tags.push({ label: `处理中 ${summary.value.processingCount} 项任务`, type: 'info' })
  }
  if (Number(summary.value.overdueCount || 0) > 0) {
    tags.push({ label: `逾期 ${summary.value.overdueCount} 项任务需要优先跟进`, type: 'danger' })
  }
  return tags
})

function reportTypeLabel(value) {
  return reportTypeOptions.find(item => item.value === value)?.label || (value || '-')
}

function taskTypeLabel(value) {
  return taskTypeOptions.find(item => item.value === value)?.label || (value || '-')
}

function riskLevelLabel(value) {
  return riskLevelOptions.find(item => item.value === value)?.label || (value || '-')
}

function statusLabel(value) {
  return statusOptions.find(item => item.value === value)?.label || (value || '-')
}

function riskTagType(value) {
  if (value === 'HIGH') return 'danger'
  if (value === 'MEDIUM') return 'warning'
  return 'success'
}

function statusTagType(value) {
  if (value === 'closed') return 'success'
  if (value === 'processing') return 'warning'
  if (value === 'overdue' || value === 'rejected') return 'danger'
  return 'info'
}

function canChangeStatus(row, nextStatus) {
  const currentStatus = String(row?.handleStatus || '')
  if (nextStatus === 'processing') {
    return ['draft', 'pending'].includes(currentStatus)
  }
  if (nextStatus === 'closed' || nextStatus === 'rejected') {
    return currentStatus === 'processing'
  }
  return false
}

watchEffect(() => {
  setPageGuide({
    title: 'AI 监测建议任务',
    description: '跟踪 AI 报告生成的监管建议、接收对象、处置状态和反馈结果。',
    focus: [
      { label: '任务总量', value: summary.value.totalCount || 0, tip: '当前筛选范围内的任务数', type: 'info' },
      { label: '待处理', value: summary.value.pendingCount || 0, tip: '尚未开始处置的任务', type: 'warning' },
      { label: '处理中', value: summary.value.processingCount || 0, tip: '正在推进处置的任务', type: 'primary' },
      { label: '已逾期', value: summary.value.overdueCount || 0, tip: '超过截止时间的任务', type: 'danger' }
    ],
    selection: selectedTaskOverview.value,
    workflow: taskWorkflow.value,
    hints: taskHintTags.value
  })
})

function reset() {
  form.value = {
    taskId: undefined,
    reportId: undefined,
    reportType: 'MONTHLY',
    taskType: 'review',
    taskName: undefined,
    regionCode: '440000',
    riskLevel: 'MEDIUM',
    reportPeriod: undefined,
    reportSummary: undefined,
    suggestionText: undefined,
    receiveUser: undefined,
    receiveDept: undefined,
    handleStatus: 'pending',
    dueDate: undefined,
    feedbackText: undefined,
    remark: undefined
  }
  proxy.resetForm('taskRef')
}

function getList() {
  loading.value = true
  return Promise.all([
    listAiReportTask(queryParams.value),
    getAiReportTaskSummary(queryParams.value)
  ]).then(([listResponse, summaryResponse]) => {
    taskList.value = listResponse.rows || []
    total.value = listResponse.total || 0
    summary.value = summaryResponse.data || {}
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

function applyReportPreset(reportId) {
  if (!reportId) {
    return Promise.resolve()
  }
  return getAiReport(reportId).then(response => {
    const report = response.data || {}
    form.value.reportId = report.reportId
    form.value.reportType = report.reportType || form.value.reportType
    form.value.regionCode = report.regionCode || form.value.regionCode
    form.value.riskLevel = report.riskLevel || form.value.riskLevel
    form.value.taskName = form.value.taskName || `AI建议任务-${report.reportId}`
    form.value.reportPeriod = [report.periodStart, report.periodEnd].filter(Boolean).join(' ~ ')
    form.value.reportSummary = report.reportSummary
    form.value.suggestionText = form.value.suggestionText || report.reportSummary
  })
}

function handleAdd() {
  reset()
  const reportId = route.query.reportId ? Number(route.query.reportId) : undefined
  open.value = true
  title.value = reportId ? '从AI报告创建任务' : '新增任务'
  if (reportId) {
    applyReportPreset(reportId)
  }
}

function handleUpdate(row) {
  reset()
  getAiReportTask(row.taskId).then(response => {
    form.value = response.data || {}
    open.value = true
    title.value = '修改任务'
  })
}

function handleView(row) {
  getAiReportTask(row.taskId).then(response => {
    detail.value = response.data || {}
    detailOpen.value = true
  })
}

function submitForm() {
  proxy.$refs.taskRef.validate(valid => {
    if (!valid) {
      return
    }
    const request = form.value.taskId ? updateAiReportTask(form.value) : addAiReportTask(form.value)
    request.then(() => {
      proxy.$modal.msgSuccess('保存成功')
      open.value = false
      getList()
    })
  })
}

function changeStatus(row, status) {
  if (!canChangeStatus(row, status)) {
    proxy.$modal.msgWarning('当前任务状态不支持该操作')
    return
  }
  if (status === 'closed') {
    proxy.$modal.prompt('请输入办结反馈').then(({ value }) => {
      if (!value) {
        proxy.$modal.msgWarning('办结反馈不能为空')
        return
      }
      return updateAiReportTaskStatus(row.taskId, { handleStatus: status, feedbackText: value })
    }).then(response => {
      if (!response) {
        return
      }
      proxy.$modal.msgSuccess('状态已更新')
      getList()
    })
    return
  }
  updateAiReportTaskStatus(row.taskId, { handleStatus: status }).then(() => {
    proxy.$modal.msgSuccess('状态已更新')
    getList()
  })
}

function handleDelete(row) {
  proxy.$modal.confirm('确认删除该任务吗？').then(() => {
    return delAiReportTask(row.taskId)
  }).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getList()
  })
}

function handleExport() {
  proxy.download('/ygb/aiReport/task/export', { ...queryParams.value }, `ai_report_task_${Date.now()}.xlsx`)
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.summary-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.summary-card {
  padding: 18px 20px;
  border: 1px solid #dbe5f0;
  border-radius: 16px;
  background: #fff;
}

.summary-card--warning {
  background: linear-gradient(180deg, #fff 0%, #fff8ef 100%);
}

.summary-card__label {
  color: #6b7d90;
  font-size: 13px;
}

.summary-card__value {
  margin-top: 10px;
  color: #13243a;
  font-size: 28px;
  font-weight: 700;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.card-actions {
  display: flex;
  gap: 12px;
}

@media (max-width: 992px) {
  .summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .summary-grid {
    grid-template-columns: 1fr;
  }
}
</style>
