<template>
  <div class="app-container enterprise-portal-module">
    <el-card shadow="never">
      <template #header>
        <div class="page-head">
          <div>
            <div class="page-title">{{ currentModule.title }}</div>
            <div class="page-desc">{{ currentModule.description }}</div>
          </div>
          <el-tag effect="plain">{{ currentModule.tag }}</el-tag>
        </div>
      </template>

      <el-alert
        v-if="loadError"
        class="enterprise-load-alert"
        :title="loadError"
        type="warning"
        show-icon
        :closable="false"
      />

      <div class="summary-grid">
        <div v-for="item in summaryCards" :key="item.label" class="summary-card">
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
        </div>
      </div>

      <div class="feature-panel">
        <div class="feature-title">核心功能</div>
        <div class="feature-list">
          <div v-for="item in currentModule.features" :key="item" class="feature-item">
            <el-icon><Check /></el-icon>
            <span>{{ item }}</span>
          </div>
        </div>
      </div>

      <div v-if="currentModule.operations?.length" class="operation-panel">
        <div class="operation-panel__head">
          <div>
            <div class="operation-panel__title">模块办理</div>
            <div class="operation-panel__desc">{{ operationTip }}</div>
          </div>
          <el-tag v-if="selectableModule" type="info" effect="plain">已选 {{ selectedRows.length }} 项</el-tag>
        </div>
        <div class="operation-buttons">
          <el-button
            v-for="operation in currentModule.operations"
            :key="operation.key"
            :type="operation.type || 'primary'"
            :plain="operation.plain"
            :loading="actionLoading === operation.key"
            @click="runOperation(operation)"
          >
            {{ operation.label }}
          </el-button>
        </div>
      </div>

      <el-form v-if="moduleKey === 'recruit'" :model="jobForm" class="job-form" label-width="88px">
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="岗位名称">
              <el-input v-model="jobForm.title" placeholder="请输入岗位名称" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="工作地点">
              <el-input v-model="jobForm.location" placeholder="请输入工作地点" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="薪资范围">
              <el-input v-model="jobForm.salary" placeholder="如 8000-12000" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="岗位要求">
              <el-input v-model="jobForm.requirement" placeholder="请输入岗位要求" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <div class="action-grid">
        <button v-for="item in currentModule.actions" :key="item.title" type="button" class="action-card" @click="openAction(item)">
          <strong>{{ item.title }}</strong>
          <p>{{ item.desc }}</p>
          <span>{{ item.action }}</span>
        </button>
      </div>

      <el-table
        v-if="tableRows.length"
        :data="tableRows"
        class="module-table"
        size="default"
        row-key="id"
        @selection-change="handleSelectionChange"
      >
        <el-table-column v-if="selectableModule" type="selection" width="48" />
        <el-table-column
          v-for="column in currentModule.columns"
          :key="column.prop"
          :label="column.label"
          :prop="column.prop"
          :width="column.width"
          :min-width="column.minWidth"
          :align="column.align"
          show-overflow-tooltip
        >
          <template #default="scope">
            <span>{{ formatColumnValue(scope.row, column) }}</span>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else class="module-empty" description="当前模块暂无待展示业务记录" />
    </el-card>
  </div>
</template>

<script setup>
import { Check } from '@element-plus/icons-vue'
import { computed, getCurrentInstance, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  exportEnterpriseDeviceLedger,
  exportEnterpriseOperationLedger,
  exportEnterprisePeopleLedger,
  getEnterpriseHomeDashboard,
  getEnterpriseInsuranceDashboard,
  getEnterpriseJobPublishDraft,
  getEnterpriseOperationDashboard,
  getEnterpriseSalaryDashboard,
  getEnterpriseTrainingDashboard,
  getEnterpriseWorkbenchDashboard,
  importEnterpriseSalaryDraft,
  listEnterpriseDeviceLedger,
  listEnterprisePeopleLedger,
  saveEnterpriseJobPublishDraft,
  saveEnterpriseTrainingPlanDraft,
  submitEnterpriseDeviceAction,
  submitEnterpriseInsuranceAction,
  submitEnterpriseJobPublish,
  submitEnterpriseOperationApproval,
  submitEnterprisePeopleAction,
  submitEnterpriseSalaryConfirm,
  submitEnterpriseTrainingAction
} from '@/api/ygb/enterprisePortal'

const route = useRoute()
const router = useRouter()
const { proxy } = getCurrentInstance()
const dashboard = ref({})
const rows = ref([])
const loadingKey = ref('')
const loadError = ref('')
const selectedRows = ref([])
const actionLoading = ref('')
const jobForm = ref({
  title: '',
  location: '',
  salary: '',
  requirement: ''
})

const moduleConfigs = {
  person: {
    tag: '人员自主管控',
    title: '企业人员管理',
    description: '管理员工花名册、入离职、证件和参保核验，承接企业人员自主管控。',
    features: ['员工花名册查询', '入离职状态核验', '证件到期提醒', '参保状态跟踪'],
    loader: listEnterprisePeopleLedger,
    rows: data => data.list || [],
    columns: [
      { label: '姓名', prop: 'name' },
      { label: '岗位', prop: 'job' },
      { label: '状态', prop: 'status' },
      { label: '证件', prop: 'certificate' },
      { label: '参保', prop: 'insurance' }
    ],
    summary: data => [
      { label: '人员总数', value: (data.list || []).length },
      { label: '在职人员', value: (data.list || []).filter(item => item.status === '在职').length },
      { label: '证件临期', value: (data.list || []).filter(item => item.tag === '临期').length },
      { label: '待处理', value: (data.list || []).filter(item => item.tag !== '正常').length }
    ],
    operations: [
      { key: 'certificate-remind', label: '证件到期提醒', action: submitEnterprisePeopleAction, payload: () => selectedPayload('personIds', 'certificate-remind'), requiresSelection: true },
      { key: 'insurance-check', label: '参保核验登记', action: submitEnterprisePeopleAction, payload: () => selectedPayload('personIds', 'insurance-check'), requiresSelection: true },
      { key: 'person-export', label: '导出花名册', type: 'success', plain: true, action: exportEnterprisePeopleLedger, payload: () => ({ actionType: 'person-export' }) }
    ],
    actions: [
      { title: '进入人员档案', desc: '继续维护从业人员基础档案。', action: '打开', to: '/enterprise-portal/person' },
      { title: '特证管理', desc: '查看证件续期、过期和审核状态。', action: '打开', to: '/enterprise-portal/person' }
    ]
  },
  device: {
    tag: '设备自主管控',
    title: '企业设备管理',
    description: '查看本企业设备台账、在线状态、故障报修和物联卡运维入口。',
    features: ['设备台账查询', '在线状态核验', '故障报修跟踪', '物联卡管理'],
    loader: listEnterpriseDeviceLedger,
    rows: data => data.list || [],
    columns: [
      { label: '设备名称', prop: 'name' },
      { label: '设备编码', prop: 'code' },
      { label: '安装位置', prop: 'location' },
      { label: '状态', prop: 'status' }
    ],
    summary: data => [
      { label: '设备总数', value: (data.list || []).length },
      { label: '在线设备', value: (data.list || []).filter(item => item.status === '在线').length },
      { label: '异常设备', value: (data.list || []).filter(item => item.status !== '在线').length },
      { label: '待运维', value: (data.list || []).filter(item => ['锁定', '故障'].includes(item.status)).length }
    ],
    operations: [
      { key: 'authorize', label: '设备授权', action: submitEnterpriseDeviceAction, payload: () => selectedPayload('deviceIds', 'authorize'), requiresSelection: true },
      { key: 'repair', label: '故障报修', action: submitEnterpriseDeviceAction, payload: () => selectedPayload('deviceIds', 'repair'), requiresSelection: true },
      { key: 'device-export', label: '导出台账', type: 'success', plain: true, action: exportEnterpriseDeviceLedger, payload: () => ({ actionType: 'device-export' }) }
    ],
    actions: [
      { title: '设备台账', desc: '查看设备编码、位置和状态。', action: '打开', to: '/enterprise-portal/device' },
      { title: '物联卡管理', desc: '维护物联卡套餐、月租和缴费状态。', action: '打开', to: '/enterprise-portal/device' }
    ]
  },
  salary: {
    tag: '工资代发',
    title: '企业工资管理',
    description: '对企业工资批次进行确认、导入、代发状态查看和异常跟踪。',
    features: ['工资批次确认', '明细导入', '代发状态跟踪', '异常批次处理'],
    loader: getEnterpriseSalaryDashboard,
    rows: data => data.batches || [],
    columns: [
      { label: '月份', prop: 'month' },
      { label: '批次', prop: 'title' },
      { label: '人数', prop: 'people' },
      { label: '应发工资', prop: 'amount', width: 120, align: 'right' },
      { label: '状态', prop: 'status' }
    ],
    summary: data => data.summary || [],
    operations: [
      { key: 'salary-confirm', label: '确认并提交代发', action: submitEnterpriseSalaryConfirm, payload: () => selectedPayload('batchIds', 'salary-confirm'), requiresSelection: true },
      { key: 'salary-import', label: '登记导入草稿', type: 'success', plain: true, action: importEnterpriseSalaryDraft, payload: () => ({ actionType: 'salary-import', batchMonth: currentMonthText() }) }
    ],
    actions: [
      { title: '工资发放监控', desc: '进入企业工资批次页面查看确认、代发与导入草稿。', action: '打开', to: '/enterprise-portal/salary' },
      { title: '监管账户监控', desc: '查看工资到账、回写和拖欠预警。', action: '打开', to: '/enterprise-portal/salary' }
    ]
  },
  work: {
    tag: '高危作业审批',
    title: '企业作业管理',
    description: '对企业高危作业申请、审批、违规记录和作业台账进行闭环处理。',
    features: ['作业申请审批', '现场责任人核验', '违规记录留痕', '作业台账导出'],
    loader: getEnterpriseOperationDashboard,
    rows: data => data.list || [],
    columns: [
      { label: '作业事项', prop: 'title' },
      { label: '地点', prop: 'location' },
      { label: '时间', prop: 'time' },
      { label: '负责人', prop: 'guardian' },
      { label: '状态', prop: 'status' }
    ],
    summary: data => [
      { label: '作业记录', value: (data.list || []).length },
      { label: '待审批', value: (data.list || []).filter(item => item.status === '待审批').length },
      { label: '处理中', value: (data.list || []).filter(item => item.status === '处理中').length },
      { label: '已办结', value: (data.list || []).filter(item => item.status === '已办结').length }
    ],
    operations: [
      { key: 'approved', label: '审批通过', action: submitEnterpriseOperationApproval, payload: () => selectedPayload('approvalIds', 'operation-approval', { decision: 'approved' }), requiresSelection: true },
      { key: 'rework', label: '退回整改', type: 'warning', plain: true, action: submitEnterpriseOperationApproval, payload: () => selectedPayload('approvalIds', 'operation-approval', { decision: 'rework' }), requiresSelection: true },
      { key: 'operation-export', label: '导出作业台账', type: 'success', plain: true, action: exportEnterpriseOperationLedger }
    ],
    actions: [
      { title: '高危作业报备', desc: '进入高危作业报备和审批记录。', action: '打开', to: '/enterprise-portal/work' },
      { title: '工伤事件管理', desc: '联动查看作业相关工伤事件。', action: '打开', to: '/enterprise-portal/work' }
    ]
  },
  insurance: {
    tag: '保险投保续保',
    title: '企业保险管理',
    description: '查看企业工伤保险、安责险投保、续保和事故预防协同事项。',
    features: ['投保状态核验', '续保提醒', '事故预防服务', '保单风险跟踪'],
    loader: getEnterpriseInsuranceDashboard,
    rows: data => data.actions || [],
    columns: [
      { label: '事项', prop: 'title' },
      { label: '说明', prop: 'desc' },
      { label: '类型', prop: 'type' }
    ],
    summary: data => data.summary || [],
    operations: [
      { key: 'renew', label: '续保跟进登记', action: submitEnterpriseInsuranceAction, payload: () => ({ actionType: 'renew' }) },
      { key: 'insurance-check', label: '参保补缴办理', type: 'success', plain: true, action: submitEnterpriseInsuranceAction, payload: () => ({ actionType: 'insurance-check' }) }
    ],
    actions: [
      { title: '投保监管', desc: '查看高危企业投保和到期情况。', action: '打开', to: '/enterprise-portal/insurance' },
      { title: '事故预防服务', desc: '处理事故预防服务审批验收。', action: '打开', to: '/enterprise-portal/insurance' }
    ]
  },
  training: {
    tag: '员工培训管理',
    title: '企业培训管理',
    description: '管理企业培训计划、学习进度、考试证明和督办事项。',
    features: ['培训计划维护', '学习进度跟踪', '考试证书核验', '培训效果评估'],
    loader: getEnterpriseTrainingDashboard,
    rows: data => data.plans || [],
    columns: [
      { label: '培训计划', prop: 'title' },
      { label: '说明', prop: 'desc' },
      { label: '进度', prop: 'progress' }
    ],
    summary: data => data.summary || [],
    operations: [
      { key: 'training-draft', label: '保存培训计划草稿', action: saveEnterpriseTrainingPlanDraft, payload: () => ({ title: `${currentMonthText()}企业培训计划`, source: 'pc-enterprise-portal' }) },
      { key: 'remind', label: '培训督办', type: 'warning', plain: true, action: submitEnterpriseTrainingAction, payload: () => selectedPayload('planIds', 'remind'), requiresSelection: true },
      { key: 'publish-exam', label: '补发考试证明', type: 'success', plain: true, action: submitEnterpriseTrainingAction, payload: () => selectedPayload('planIds', 'publish-exam'), requiresSelection: true }
    ],
    actions: [
      { title: '培训计划', desc: '进入工伤预防培训管理。', action: '打开', to: '/enterprise-portal/training' },
      { title: '课程学时', desc: '维护培训课程与学时。', action: '打开', to: '/enterprise-portal/training' }
    ]
  },
  recruit: {
    tag: '招聘管理',
    title: '企业招聘管理',
    description: '发布岗位、维护岗位草稿、查看简历投递和招聘市场入口。',
    features: ['岗位草稿维护', '岗位发布审核', '简历查看', '招聘市场联动'],
    loader: getEnterpriseJobPublishDraft,
    rows: data => data.drafts || [],
    columns: [
      { label: '岗位', prop: 'title' },
      { label: '薪资', prop: 'salary' },
      { label: '要求', prop: 'requirement' },
      { label: '状态', prop: 'status' }
    ],
    summary: data => [
      { label: '草稿数', value: (data.drafts || []).length },
      { label: '待发布', value: (data.drafts || []).filter(item => item.status !== '已发布').length },
      { label: '招聘入口', value: '可用' },
      { label: '联动审核', value: '已接入' }
    ],
    operations: [
      { key: 'job-draft', label: '保存岗位草稿', action: saveEnterpriseJobPublishDraft, payload: () => ({ ...jobForm.value, source: 'pc-enterprise-portal' }), validate: validateJobForm },
      { key: 'job-submit', label: '提交岗位审核', type: 'success', plain: true, action: submitEnterpriseJobPublish, payload: () => ({ ...jobForm.value, source: 'pc-enterprise-portal' }), validate: validateJobForm }
    ],
    actions: [
      { title: '岗位审核', desc: '查看岗位审核和发布状态。', action: '打开', to: '/enterprise-portal/recruit' },
      { title: '招聘市场', desc: '进入便民招聘用工市场。', action: '打开', to: '/enterprise-portal/recruit' }
    ]
  },
  finance: {
    tag: '工资监管',
    title: '企业财务管理',
    description: '围绕工资确认、监管账户和物联卡运维形成闭环办理入口。',
    features: ['工资确认提交', '监管账户监控', '物联卡费用查看', '财务联动处理'],
    loader: getEnterpriseSalaryDashboard,
    rows: data => data.batches || [],
    columns: [
      { label: '月份', prop: 'month' },
      { label: '批次', prop: 'title' },
      { label: '人数', prop: 'people' },
      { label: '应发工资', prop: 'amount', width: 120, align: 'right' },
      { label: '状态', prop: 'status' }
    ],
    summary: data => data.summary || [],
    operations: [
      { key: 'finance-confirm', label: '确认工资', action: submitEnterpriseSalaryConfirm, payload: () => selectedPayload('batchIds', 'salary-confirm'), requiresSelection: true },
      { key: 'finance-import', label: '导入草稿', type: 'success', plain: true, action: importEnterpriseSalaryDraft, payload: () => ({ actionType: 'finance-import', batchMonth: currentMonthText() }) }
    ],
    actions: [
      { title: '工资发放监控', desc: '进入企业工资批次页面查看确认、代发与回写。', action: '打开', to: '/enterprise-portal/finance' },
      { title: '监管账户监控', desc: '查看工资到账、回写和拖欠预警。', action: '打开', to: '/enterprise-portal/finance' },
      { title: '物联卡管理', desc: '维护物联卡费用记录。', action: '打开', to: '/enterprise-portal/finance' }
    ]
  },
  credit: {
    tag: '信用风险自查',
    title: '企业信用报告',
    description: '查看企业信用评分、扣分整改、信用报告导出和风险自查入口。',
    features: ['信用评分查看', '扣分整改跟踪', '风险建议核验', '信用报告导出'],
    loader: getEnterpriseHomeDashboard,
    rows: data => data.warnings || [],
    columns: [
      { label: '事项', prop: 'title' },
      { label: '说明', prop: 'desc' },
      { label: '时间', prop: 'time' }
    ],
    summary: data => [
      { label: '健康度', value: data.hero?.healthScore || '--' },
      { label: '待处理预警', value: data.hero?.warningCount || 0 },
      { label: '待办任务', value: data.hero?.taskCount || 0 },
      { label: '待跟进事项', value: data.hero?.todoCount || 0 }
    ],
    actions: [
      { title: '信用总览', desc: '查看企业信用评分、等级和画像。', action: '打开', to: '/enterprise-portal/credit' },
      { title: '信用报告导出', desc: '导出企业信用报告。', action: '打开', to: '/enterprise-portal/credit' }
    ]
  }
}

const moduleKey = computed(() => String(route.path || '').split('/').filter(Boolean).pop() || 'person')
const currentModule = computed(() => moduleConfigs[moduleKey.value] || moduleConfigs.person)
const tableRows = computed(() => rows.value)
const selectableModule = computed(() => ['person', 'device', 'salary', 'work', 'insurance', 'training', 'recruit', 'finance', 'credit'].includes(moduleKey.value))
const operationTip = computed(() => selectableModule.value ? '勾选表格记录后可直接办理本模块三级功能。' : '可直接提交本模块办理事项。')
const summaryCards = computed(() => {
  const summary = typeof currentModule.value.summary === 'function' ? currentModule.value.summary(dashboard.value) : []
  return summary.length ? summary : [
    { label: '业务模块', value: currentModule.value.title },
    { label: '核心功能', value: currentModule.value.features.length },
    { label: '办理入口', value: currentModule.value.actions.length },
    { label: '联调状态', value: '可用' }
  ]
})

async function loadModule() {
  const targetKey = moduleKey.value
  loadingKey.value = targetKey
  loadError.value = ''
  try {
    const res = await currentModule.value.loader()
    if (loadingKey.value !== targetKey) return
    dashboard.value = res.data || {}
    if (targetKey === 'recruit' && dashboard.value.form) {
      jobForm.value = {
        title: dashboard.value.form.title || '',
        location: dashboard.value.form.location || '',
        salary: dashboard.value.form.salary || '',
        requirement: dashboard.value.form.requirement || ''
      }
    }
    rows.value = currentModule.value.rows(dashboard.value)
    selectedRows.value = []
  } catch (error) {
    if (loadingKey.value !== targetKey) return
    dashboard.value = {}
    rows.value = []
    selectedRows.value = []
    loadError.value = isUnboundEnterpriseError(error)
      ? '当前账号未绑定企业，企业后台仅展示只读空态。请为账号绑定企业后办理企业侧业务。'
      : '企业后台模块数据加载失败，请稍后重试。'
    if (isUnboundEnterpriseError(error)) {
      console.warn('Enterprise portal module is empty because current account is not bound to an enterprise.')
    } else {
      console.error('Enterprise portal module failed to load:', error)
    }
  } finally {
    if (loadingKey.value === targetKey) {
      loadingKey.value = ''
    }
  }
}

function openAction(item) {
  if (item?.to) {
    router.push(item.to)
  }
}

function handleSelectionChange(selection) {
  selectedRows.value = selection
}

function selectedPayload(idKey, actionType, extra = {}) {
  return {
    actionType,
    [idKey]: selectedRows.value.map(item => item.id).filter(Boolean),
    ...extra
  }
}

function formatColumnValue(row, column) {
  const value = row?.[column.prop]
  if (typeof column.formatter === 'function') {
    return column.formatter(value, row)
  }
  return value ?? '-'
}

function currentMonthText() {
  const date = new Date()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  return `${date.getFullYear()}-${month}`
}

function isUnboundEnterpriseError(error) {
  return String(error?.message || error || '').includes('当前账号未绑定企业权限')
}

function validateJobForm() {
  if (!jobForm.value.title || !jobForm.value.location) {
    proxy.$modal.msgWarning('请先填写岗位名称和工作地点。')
    return false
  }
  return true
}

async function runOperation(operation) {
  if (operation.requiresSelection && selectedRows.value.length === 0) {
    proxy.$modal.msgWarning('请先选择要办理的记录。')
    return
  }
  if (operation.validate && !operation.validate()) {
    return
  }
  actionLoading.value = operation.key
  try {
    const payload = operation.payload ? operation.payload() : undefined
    const res = await operation.action(payload)
    proxy.$modal.msgSuccess(res.data?.message || res.msg || '操作成功')
    await loadModule()
  } catch (error) {
    proxy.$modal.msgError(error?.msg || error?.message || '操作失败，请稍后重试')
  } finally {
    actionLoading.value = ''
  }
}

watch(() => route.path, loadModule)
onMounted(loadModule)
</script>

<style scoped>
.page-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.page-title {
  color: var(--el-text-color-primary);
  font-size: 18px;
  font-weight: 600;
}

.page-desc {
  margin-top: 6px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.summary-grid,
.action-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
}

.summary-grid {
  margin-bottom: 16px;
}

.enterprise-load-alert {
  margin-bottom: 16px;
}

.summary-card,
.action-card {
  border: 1px solid #e3ebf3;
  border-radius: 6px;
  background: #fff;
}

.summary-card {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 14px 16px;
}

.summary-card span,
.action-card p,
.action-card span {
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.summary-card strong {
  color: #123b67;
  font-size: 24px;
  font-weight: 600;
}

.feature-panel {
  margin-bottom: 16px;
  padding: 12px 14px;
  border: 1px solid #e3ebf3;
  border-radius: 6px;
  background: #fbfdff;
}

.feature-title {
  margin-bottom: 10px;
  color: var(--el-text-color-primary);
  font-size: 14px;
  font-weight: 600;
}

.feature-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 8px 14px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 6px;
  min-width: 0;
  color: var(--el-text-color-regular);
  font-size: 13px;
  line-height: 20px;
}

.feature-item .el-icon {
  flex: 0 0 auto;
  color: #1f7a4d;
}

.feature-item span {
  min-width: 0;
  overflow-wrap: anywhere;
}

.operation-panel,
.job-form {
  margin-bottom: 16px;
  padding: 14px 16px;
  border: 1px solid #dce8f5;
  border-radius: 6px;
  background: #f8fbff;
}

.operation-panel__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.operation-panel__title {
  color: var(--el-text-color-primary);
  font-size: 14px;
  font-weight: 600;
}

.operation-panel__desc {
  margin-top: 4px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.operation-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.job-form :deep(.el-form-item) {
  margin-bottom: 10px;
}

.job-form :deep(.el-form-item:last-child) {
  margin-bottom: 0;
}

.action-card {
  display: flex;
  min-height: 126px;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
  padding: 16px;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.action-card:hover {
  border-color: var(--el-color-primary-light-5);
  box-shadow: 0 8px 24px rgba(15, 94, 168, 0.08);
}

.action-card strong {
  color: var(--el-text-color-primary);
  font-size: 15px;
}

.action-card p {
  margin: 0;
  line-height: 1.5;
}

.action-card span {
  margin-top: auto;
  color: var(--el-color-primary);
}

.module-table,
.module-empty {
  margin-top: 16px;
}
</style>
