<template>
  <div class="app-container">
    <div class="summary-grid">
      <div class="summary-card">
        <div class="summary-card__label">订阅总量</div>
        <div class="summary-card__value">{{ summary.totalCount || 0 }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-card__label">启用订阅</div>
        <div class="summary-card__value">{{ summary.activeCount || 0 }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-card__label">月度订阅</div>
        <div class="summary-card__value">{{ summary.monthlyCount || 0 }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-card__label">多通道订阅</div>
        <div class="summary-card__value">{{ summary.multiChannelCount || 0 }}</div>
      </div>
    </div>

    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>AI 报告订阅</span>
          <div class="card-actions">
            <el-button type="primary" @click="handleAdd" v-hasPermi="['ygb:aiReportSubscription:add']">新增订阅</el-button>
            <el-button type="warning" @click="handleExport" v-hasPermi="['ygb:aiReportSubscription:export']">导出</el-button>
          </div>
        </div>
      </template>

      <el-form ref="queryRef" :model="queryParams" :inline="true">
        <el-form-item label="订阅名称">
          <el-input v-model="queryParams.subscriptionName" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="报告类型">
          <el-select v-model="queryParams.reportType" clearable style="width: 160px">
            <el-option v-for="item in reportTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="区域">
          <el-select v-model="queryParams.regionCode" clearable style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="周期">
          <el-select v-model="queryParams.cycleType" clearable style="width: 160px">
            <el-option v-for="item in cycleTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" clearable style="width: 160px">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="subscriptionList">
        <el-table-column label="订阅名称" prop="subscriptionName" min-width="180" />
        <el-table-column label="报告类型" width="120">
          <template #default="scope">{{ reportTypeLabel(scope.row.reportType) }}</template>
        </el-table-column>
        <el-table-column label="区域" prop="regionName" width="140" />
        <el-table-column label="周期" width="120">
          <template #default="scope">{{ cycleTypeLabel(scope.row.cycleType) }}</template>
        </el-table-column>
        <el-table-column label="接收方式" width="160">
          <template #default="scope">{{ receiveTypeLabel(scope.row.receiveType) }}</template>
        </el-table-column>
        <el-table-column label="接收对象" prop="receiver" min-width="180" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '1' ? 'success' : 'info'" effect="plain">{{ scope.row.status === '1' ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="最近发送" prop="lastSendTime" width="170" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['ygb:aiReportSubscription:edit']">修改</el-button>
            <el-button link type="info" @click="handleView(scope.row)" v-hasPermi="['ygb:aiReportSubscription:query']">详情</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)" v-hasPermi="['ygb:aiReportSubscription:remove']">删除</el-button>
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

    <el-dialog v-model="open" :title="title" width="760px">
      <el-form ref="subscriptionRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="订阅名称" prop="subscriptionName">
              <el-input v-model="form.subscriptionName" />
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
            <el-form-item label="订阅周期" prop="cycleType">
              <el-select v-model="form.cycleType">
                <el-option v-for="item in cycleTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="接收方式" prop="receiveType">
              <el-select v-model="form.receiveType">
                <el-option v-for="item in receiveTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="版本范围" prop="versionScope">
              <el-select v-model="form.versionScope">
                <el-option label="当前生效版本" value="current" />
                <el-option label="全部版本" value="all" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status">
                <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最近发送">
              <el-date-picker v-model="form.lastSendTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="接收对象" prop="receiver">
          <el-input v-model="form.receiver" placeholder="如：监管专班 / mobile / email" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="open = false">取消</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailOpen" title="订阅详情" width="720px">
      <el-descriptions :column="2" border v-if="detail">
        <el-descriptions-item label="订阅名称">{{ detail.subscriptionName }}</el-descriptions-item>
        <el-descriptions-item label="报告类型">{{ reportTypeLabel(detail.reportType) }}</el-descriptions-item>
        <el-descriptions-item label="区域">{{ detail.regionName || detail.regionCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="订阅周期">{{ cycleTypeLabel(detail.cycleType) }}</el-descriptions-item>
        <el-descriptions-item label="接收方式">{{ receiveTypeLabel(detail.receiveType) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.status === '1' ? '启用' : '停用' }}</el-descriptions-item>
        <el-descriptions-item label="接收对象" :span="2">{{ detail.receiver || '-' }}</el-descriptions-item>
        <el-descriptions-item label="版本范围">{{ detail.versionScope || '-' }}</el-descriptions-item>
        <el-descriptions-item label="最近发送">{{ detail.lastSendTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup name="YgbAiReportSubscription">
import { computed, getCurrentInstance, onMounted, reactive, ref, toRefs, watchEffect } from 'vue'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import {
  addAiReportSubscription,
  delAiReportSubscription,
  getAiReportSubscription,
  getAiReportSubscriptionSummary,
  listAiReportSubscription,
  updateAiReportSubscription
} from '@/api/ygb/aiReportSubscription'

const { proxy } = getCurrentInstance()
const loading = ref(false)
const total = ref(0)
const subscriptionList = ref([])
const open = ref(false)
const detailOpen = ref(false)
const title = ref('')
const detail = ref(null)
const summary = ref({})
const { setPageGuide } = useWorkbenchAssist()

const regionOptions = [
  { label: '广东省', value: '440000' },
  { label: '广州市天河区', value: '440106' },
  { label: '深圳市南山区', value: '440305' },
  { label: '佛山市顺德区', value: '440606' }
]

const reportTypeOptions = [
  { label: '日报', value: 'DAILY' },
  { label: '周报', value: 'WEEKLY' },
  { label: '月报', value: 'MONTHLY' }
]

const cycleTypeOptions = [
  { label: '每日', value: 'DAILY' },
  { label: '每周', value: 'WEEKLY' },
  { label: '每月', value: 'MONTHLY' }
]

const receiveTypeOptions = [
  { label: '站内消息', value: 'INTERNAL' },
  { label: '短信', value: 'SMS' },
  { label: '邮件', value: 'EMAIL' },
  { label: '站内+邮件', value: 'INTERNAL,EMAIL' }
]

const statusOptions = [
  { label: '停用', value: '0' },
  { label: '启用', value: '1' }
]

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    subscriptionName: undefined,
    reportType: undefined,
    regionCode: undefined,
    cycleType: undefined,
    status: undefined
  },
  form: {},
  rules: {
    subscriptionName: [{ required: true, message: '请输入订阅名称', trigger: 'blur' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

const selectedSubscriptionOverview = computed(() => {
  const record = detail.value || {}
  return [
    { label: '????', value: record.subscriptionName || '-' },
    { label: '????', value: reportTypeLabel(record.reportType) },
    { label: '????', value: cycleTypeLabel(record.cycleType) },
    { label: '????', value: receiveTypeLabel(record.receiveType) }
  ]
})

const subscriptionWorkflow = computed(() => ([
  { label: '??????', desc: '???????????????????????' },
  { label: '??????', desc: '???????????????????????????' },
  { label: '??????', desc: '???????????????????????????' }
]))

const subscriptionHintTags = computed(() => {
  const tags = []
  if (Number(summary.value.activeCount || 0) > 0) {
    tags.push({ label: `??? ${summary.value.activeCount} ??????????`, type: 'success' })
  }
  if (Number(summary.value.monthlyCount || 0) > 0) {
    tags.push({ label: `???? ${summary.value.monthlyCount} ?????????????`, type: 'info' })
  }
  if (Number(summary.value.multiChannelCount || 0) > 0) {
    tags.push({ label: `????? ${summary.value.multiChannelCount} ??????????????`, type: 'warning' })
  }
  return tags
})

function reportTypeLabel(value) {
  return reportTypeOptions.find(item => item.value === value)?.label || (value || '-')
}

function cycleTypeLabel(value) {
  return cycleTypeOptions.find(item => item.value === value)?.label || (value || '-')
}

function receiveTypeLabel(value) {
  return receiveTypeOptions.find(item => item.value === value)?.label || (value || '-')
}

watchEffect(() => {
  setPageGuide({
    title: 'AI ??????',
    description: '????????????????????????????',
    focus: [
      { label: '????', value: summary.value.totalCount || 0, tip: '???????????????', type: 'info' },
      { label: '????', value: summary.value.activeCount || 0, tip: '????????????', type: 'success' },
      { label: '????', value: summary.value.monthlyCount || 0, tip: '????????????', type: 'primary' },
      { label: '?????', value: summary.value.multiChannelCount || 0, tip: '??????????????', type: 'warning' }
    ],
    selection: selectedSubscriptionOverview.value,
    workflow: subscriptionWorkflow.value,
    hints: subscriptionHintTags.value
  })
})

function reset() {
  form.value = {
    subscriptionId: undefined,
    subscriptionName: undefined,
    reportType: 'MONTHLY',
    regionCode: '440000',
    cycleType: 'MONTHLY',
    receiveType: 'INTERNAL',
    receiver: undefined,
    versionScope: 'current',
    status: '1',
    lastSendTime: undefined,
    remark: undefined
  }
  proxy.resetForm('subscriptionRef')
}

function getList() {
  loading.value = true
  return Promise.all([
    listAiReportSubscription(queryParams.value),
    getAiReportSubscriptionSummary(queryParams.value)
  ]).then(([listResponse, summaryResponse]) => {
    subscriptionList.value = listResponse.rows || []
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

function handleAdd() {
  reset()
  open.value = true
  title.value = '新增订阅'
}

function handleUpdate(row) {
  reset()
  getAiReportSubscription(row.subscriptionId).then(response => {
    form.value = response.data || {}
    open.value = true
    title.value = '修改订阅'
  })
}

function handleView(row) {
  getAiReportSubscription(row.subscriptionId).then(response => {
    detail.value = response.data || {}
    detailOpen.value = true
  })
}

function submitForm() {
  proxy.$refs.subscriptionRef.validate(valid => {
    if (!valid) {
      return
    }
    const request = form.value.subscriptionId ? updateAiReportSubscription(form.value) : addAiReportSubscription(form.value)
    request.then(() => {
      proxy.$modal.msgSuccess('保存成功')
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  proxy.$modal.confirm('确认删除该订阅吗？').then(() => {
    return delAiReportSubscription(row.subscriptionId)
  }).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getList()
  })
}

function handleExport() {
  proxy.download('/ygb/aiReport/subscription/export', { ...queryParams.value }, `ai_report_subscription_${Date.now()}.xlsx`)
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
