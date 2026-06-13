<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">合规主链路</p>
        <h1 class="ygb-page__title">合同备案办理台账</h1>
        <p class="ygb-page__desc">
          统一维护派遣单位、用工单位与劳动者之间的合同备案关系，面向企业管理员和经办优先解决待备案、OCR 异常、条款缺失和到期续签问题，
          为后续考勤归集和工资监管提供可直接承接的主数据依据。
        </p>
      </div>
      <div class="ygb-table-tip">
        当前版本先支撑备案主数据维护和联调样本闭环，已预留 OCR、条款校验、备案编号和区块链存证字段。
      </div>
    </section>

    <el-alert
      v-if="workbenchContext"
      class="ygb-workbench-alert"
      type="info"
      :closable="false"
      show-icon
    >
      <template #title>
        <div class="ygb-workbench-alert__title">
          <span>{{ workbenchContext.title }}</span>
          <el-button link type="primary" @click="clearWorkbenchContext">清空来源条件</el-button>
        </div>
      </template>
      <div class="ygb-workbench-alert__desc">
        <strong>{{ workbenchContext.sourceLabel }}</strong>
        <span>{{ workbenchContext.description }}</span>
      </div>
      <div class="ygb-tag-list" style="margin-top: 10px;">
        <el-tag v-for="item in workbenchContext.tags" :key="item.key" effect="plain">{{ item.label }}：{{ item.value }}</el-tag>
      </div>
    </el-alert>

    <div class="ygb-summary-grid">
      <div v-for="item in resolvedSummaryCards" :key="item.key" class="ygb-summary-card" :class="item.cardClass">
        <div class="ygb-summary-card__label">{{ item.label }}</div>
        <div class="ygb-summary-card__value">
          {{ item.value }}
          <span class="ygb-summary-card__unit">{{ item.unit }}</span>
        </div>
        <div class="ygb-summary-card__note">{{ item.note }}</div>
      </div>
    </div>


    <el-card class="search-card ygb-search-card" shadow="never">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
        <el-form-item label="合同编号" prop="contractNo">
          <el-input
            v-model="queryParams.contractNo"
            placeholder="请输入合同编号"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="派遣单位" prop="dispatchEnterpriseId">
          <el-select v-model="queryParams.dispatchEnterpriseId" placeholder="请选择派遣单位" clearable filterable style="width: 220px">
            <el-option
              v-for="item in enterpriseOptions"
              :key="item.enterpriseId"
              :label="item.enterpriseName"
              :value="item.enterpriseId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="劳动者" prop="personId">
          <el-select v-model="queryParams.personId" placeholder="请选择劳动者" clearable filterable style="width: 200px">
            <el-option
              v-for="item in personOptions"
              :key="item.personId"
              :label="formatPersonOptionLabel(item)"
              :value="item.personId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备案状态" prop="contractStatus">
          <el-select v-model="queryParams.contractStatus" placeholder="请选择备案状态" clearable style="width: 150px">
            <el-option
              v-for="item in contractStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="OCR状态" prop="ocrStatus">
          <el-select v-model="queryParams.ocrStatus" placeholder="请选择 OCR 状态" clearable style="width: 150px">
            <el-option
              v-for="item in ocrStatusOptions"
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

    <el-card class="toolbar-card ygb-toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button
            type="primary"
            plain
            icon="Plus"
            @click="handleAdd"
            v-hasPermi="['ygb:contract:add']"
          >
            新增
          </el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button
            type="success"
            plain
            icon="Edit"
            :disabled="single"
            @click="handleUpdate()"
            v-hasPermi="['ygb:contract:edit']"
          >
            修改
          </el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button
            type="danger"
            plain
            icon="Delete"
            :disabled="multiple"
            @click="handleDelete()"
            v-hasPermi="['ygb:contract:remove']"
          >
            删除
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="warning"
            plain
            icon="Download"
            @click="handleExport"
            v-hasPermi="['ygb:contract:export']"
          >
            导出
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">合同备案台账</div>
            <div class="ygb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="visibleContractList" @selection-change="handleSelectionChange" @row-click="handleRowClick">
        <el-table-column v-if="!isReadOnlyRole" type="selection" width="55" align="center" />
        <el-table-column label="合同ID" align="center" prop="contractId" width="90" />
        <el-table-column label="合同编号" align="center" prop="contractNo" width="180" />
        <el-table-column label="劳动者" align="center" prop="personName" width="120" />
        <el-table-column label="派遣单位" align="center" prop="dispatchEnterpriseName" min-width="220" />
        <el-table-column label="用工单位" align="center" prop="employerEnterpriseName" min-width="220" />
        <el-table-column label="合同类型" align="center" prop="contractType" width="110">
          <template #default="scope">
            <dict-tag :options="contractTypeOptions" :value="scope.row.contractType" />
          </template>
        </el-table-column>
        <el-table-column label="备案状态" align="center" prop="contractStatus" width="110">
          <template #default="scope">
            <dict-tag :options="contractStatusOptions" :value="scope.row.contractStatus" />
          </template>
        </el-table-column>
        <el-table-column label="月工资标准" align="center" prop="monthlyWage" width="120" />
        <el-table-column label="起止日期" align="center" min-width="200">
          <template #default="scope">
            <span>{{ parseTime(scope.row.startDate, '{y}-{m}-{d}') }} 至 {{ parseTime(scope.row.endDate, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="OCR状态" align="center" prop="ocrStatus" width="100">
          <template #default="scope">
            <dict-tag :options="ocrStatusOptions" :value="scope.row.ocrStatus" />
          </template>
        </el-table-column>
        <el-table-column label="条款校验" align="center" prop="clauseCheckStatus" width="100">
          <template #default="scope">
            <dict-tag :options="clauseCheckStatusOptions" :value="scope.row.clauseCheckStatus" />
          </template>
        </el-table-column>
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" align="center" :width="isReadOnlyRole ? 90 : 240" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
            <template v-if="!isReadOnlyRole">
              <el-button link type="primary" icon="Edit" @click.stop="handleUpdate(scope.row)" v-hasPermi="['ygb:contract:edit']">修改</el-button>
              <el-button link type="danger" icon="Delete" @click.stop="handleDelete(scope.row)" v-hasPermi="['ygb:contract:remove']">删除</el-button>
            </template>
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

    <el-dialog :title="title" v-model="open" width="860px" append-to-body>
      <el-form ref="contractRef" :model="form" :rules="rules" label-width="110px">
        <div class="ygb-panel-grid">
          <el-form-item label="合同编号" prop="contractNo">
            <el-input v-model="form.contractNo" placeholder="请输入合同编号" />
          </el-form-item>
          <el-form-item label="合同类型" prop="contractType">
            <el-select v-model="form.contractType" placeholder="请选择合同类型" @change="handleContractTypeChange">
              <el-option v-for="item in contractTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="模板库引用" prop="templateId">
            <el-select
              v-model="form.templateId"
              clearable
              filterable
              placeholder="请选择合同模板"
              @change="handleTemplateChange"
            >
              <el-option
                v-for="item in templateOptions"
                :key="item.templateId"
                :label="`${item.templateName || '-'} / ${item.templateVersion || '-'}`"
                :value="item.templateId"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="派遣单位" prop="dispatchEnterpriseId">
            <el-select v-model="form.dispatchEnterpriseId" placeholder="请选择派遣单位" filterable @change="handleDispatchChange">
              <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
            </el-select>
          </el-form-item>
          <el-form-item label="用工单位" prop="employerEnterpriseId">
            <el-select v-model="form.employerEnterpriseId" placeholder="请选择用工单位" filterable>
              <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
            </el-select>
          </el-form-item>
          <el-form-item label="劳动者" prop="personId">
            <el-select v-model="form.personId" placeholder="请选择劳动者" filterable>
              <el-option
                v-for="item in formPersonOptions"
                :key="item.personId"
                :label="formatPersonOptionLabel(item)"
                :value="item.personId"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="备案状态" prop="contractStatus">
            <el-select v-model="form.contractStatus" placeholder="请选择备案状态">
              <el-option v-for="item in contractStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="签订日期" prop="signDate">
            <el-date-picker v-model="form.signDate" type="date" placeholder="请选择签订日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 100%" />
          </el-form-item>
          <el-form-item label="生效日期" prop="startDate">
            <el-date-picker v-model="form.startDate" type="date" placeholder="请选择生效日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 100%" />
          </el-form-item>
          <el-form-item label="终止日期" prop="endDate">
            <el-date-picker v-model="form.endDate" type="date" placeholder="请选择终止日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 100%" />
          </el-form-item>
          <el-form-item label="月工资标准" prop="monthlyWage">
            <el-input-number v-model="form.monthlyWage" :min="0" :precision="2" :step="100" style="width: 100%" />
          </el-form-item>
          <el-form-item label="备案编号" prop="filingNo">
            <el-input v-model="form.filingNo" placeholder="请输入备案编号" />
          </el-form-item>
          <el-form-item label="备案时间" prop="filingTime">
            <el-date-picker v-model="form.filingTime" type="datetime" placeholder="请选择备案时间" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
          </el-form-item>
          <el-form-item label="OCR状态" prop="ocrStatus">
            <el-radio-group v-model="form.ocrStatus">
              <el-radio v-for="item in ocrStatusOptions" :key="item.value" :value="item.value">{{ item.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="条款校验" prop="clauseCheckStatus">
            <el-radio-group v-model="form.clauseCheckStatus">
              <el-radio v-for="item in clauseCheckStatusOptions" :key="item.value" :value="item.value">{{ item.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </div>
        <el-form-item label="模板适配提示">
          <el-input v-model="resolvedTemplateHint" type="textarea" :rows="2" readonly />
        </el-form-item>
        <el-form-item label="区块链哈希" prop="blockchainHash">
          <el-input v-model="form.blockchainHash" placeholder="保存后可自动回填，也可手动补录" />
        </el-form-item>
        <el-form-item label="合同文件地址" prop="contractFileUrl">
          <div class="ygb-upload-field">
            <file-upload
              v-model="form.contractFileUrl"
              :limit="1"
              :file-size="20"
              :file-type="['pdf', 'doc', 'docx', 'jpg', 'jpeg', 'png']"
            />
            <div class="ygb-upload-field__tip">
              保存后将基于原件自动尝试 OCR 识别和区块链存证，已有结果不会被强制覆盖。
            </div>
          </div>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确定</el-button>
          <el-button @click="cancel">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <page-detail-dialog v-model="detailOpen" title="合同备案详情" width="820px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="合同编号">{{ detail.contractNo }}</el-descriptions-item>
          <el-descriptions-item label="合同类型">
            <dict-tag :options="contractTypeOptions" :value="detail.contractType" />
          </el-descriptions-item>
          <el-descriptions-item label="劳动者">{{ detail.personName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ maskIdCard(detail.idCard) || '-' }}</el-descriptions-item>
          <el-descriptions-item label="派遣单位">{{ detail.dispatchEnterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="用工单位">{{ detail.employerEnterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备案状态">
            <dict-tag :options="contractStatusOptions" :value="detail.contractStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="月工资标准">{{ formatMoney(detail.monthlyWage) }}</el-descriptions-item>
          <el-descriptions-item label="签订日期">{{ parseTime(detail.signDate, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备案时间">{{ parseTime(detail.filingTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="生效日期">{{ parseTime(detail.startDate, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="终止日期">{{ parseTime(detail.endDate, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="OCR状态">
            <dict-tag :options="ocrStatusOptions" :value="detail.ocrStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="条款校验">
            <dict-tag :options="clauseCheckStatusOptions" :value="detail.clauseCheckStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="备案编号">{{ detail.filingNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="区块链哈希">{{ detail.blockchainHash || '-' }}</el-descriptions-item>
          <el-descriptions-item label="合同文件地址" :span="2">{{ detail.contractFileUrl || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="detail-block">
          <h3>办理提示</h3>
          <div class="ygb-tag-list">
            <el-tag v-for="item in resolvedDetailHintTags" :key="item.label" :type="item.type" effect="plain">{{ item.label }}</el-tag>
          </div>
        </div>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbContract">
import { useRoute, useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import FileUpload from '@/components/FileUpload/index.vue'
import { addContract, delContract, getContract, getContractSummary, listContract, updateContract } from '@/api/ygb/contract'
import { optionselectContractTemplate } from '@/api/ygb/contractTemplate'
import { optionselectEnterprise } from '@/api/ygb/enterprise'
import { optionselectPerson } from '@/api/ygb/person'
import { decoratePortalExplanationItems, openPortalExplanationAction, resolvePortalExplanationSummary } from '@/utils/portalExplanation'
import { useRoleViewMode } from '@/utils/roleView'
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'

const { proxy } = getCurrentInstance()
const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const router = useRouter()
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()

const contractTypeOptions = [
  { label: '劳动合同', value: '1' },
  { label: '派遣协议', value: '2' },
  { label: '用工协议', value: '3' }
]

const contractStatusOptions = [
  { label: '草稿', value: '0' },
  { label: '待备案', value: '1' },
  { label: '已备案', value: '2' },
  { label: '驳回', value: '3' },
  { label: '已到期', value: '4' },
  { label: '已解除', value: '5' }
]

const ocrStatusOptions = [
  { label: '未识别', value: '0' },
  { label: '识别成功', value: '1' },
  { label: '识别失败', value: '2' }
]

const clauseCheckStatusOptions = [
  { label: '未校验', value: '0' },
  { label: '通过', value: '1' },
  { label: '缺失', value: '2' }
]

const contractList = ref([])
const enterpriseOptions = ref([])
const personOptions = ref([])
const formPersonOptions = ref([])
const templateOptions = ref([])
const summaryData = ref({})
const currentContract = ref(undefined)
const detail = ref(undefined)
const open = ref(false)
const detailOpen = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const contractNos = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref('')
const activeFocusKey = ref('')

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    contractNo: undefined,
    dispatchEnterpriseId: undefined,
    personId: undefined,
    contractStatus: undefined,
    ocrStatus: undefined
  },
  rules: {
    contractNo: [{ required: true, message: '合同编号不能为空', trigger: 'blur' }],
    dispatchEnterpriseId: [{ required: true, message: '派遣单位不能为空', trigger: 'change' }],
    employerEnterpriseId: [{ required: true, message: '用工单位不能为空', trigger: 'change' }],
    personId: [{ required: true, message: '劳动者不能为空', trigger: 'change' }],
    contractType: [{ required: true, message: '合同类型不能为空', trigger: 'change' }],
    contractStatus: [{ required: true, message: '备案状态不能为空', trigger: 'change' }]
  }
})

const { queryParams, form, rules } = toRefs(data)
const contractWorkbenchFilterFields = ['dispatchEnterpriseId', 'personId']
const contractWorkbenchRouteFields = [...contractWorkbenchFilterFields, 'focusKey']

function buildSummaryQuery() {
  return {
    contractNo: queryParams.value.contractNo,
    dispatchEnterpriseId: queryParams.value.dispatchEnterpriseId,
    personId: queryParams.value.personId,
    contractStatus: queryParams.value.contractStatus,
    ocrStatus: queryParams.value.ocrStatus
  }
}

function buildContractExplanationQuery(extraQuery = {}) {
  return {
    ...buildSummaryQuery(),
    ...extraQuery
  }
}

function formatPersonOptionLabel(item) {
  if (!item) {
    return '-'
  }
  const name = item.personName || '-'
  const suffix = item.enterpriseName || maskIdCard(item.idCard) || ''
  return suffix ? `${name} / ${suffix}` : name
}

function templateTypeLabel(value) {
  return contractTypeOptions.find(item => item.value === value)?.label || '-'
}

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: contractWorkbenchFilterFields,
  sourceLabel: '企业/人员工作台',
  title: '当前合同台账沿用了上游工作台筛选',
  description: '已按企业或人员范围锁定合同数据，适合继续处理备案、OCR 和条款缺失问题。',
  fieldLabels: {
    dispatchEnterpriseId: '派遣单位',
    personId: '劳动者'
  },
  fieldFormatters: {
    dispatchEnterpriseId: value => enterpriseName(value) || value,
    personId: value => {
      const matched = personOptions.value.find(item => String(item.personId) === String(value))
      return matched ? matched.personName : value
    }
  }
}))

function resolvePortalExplanationItem(keyOrIndex = 0) {
  if (typeof keyOrIndex === 'string' && keyOrIndex.trim()) {
    const matched = portalExplanationItems.value.find(item => item.key === keyOrIndex.trim())
    return matched || leadingPortalExplanation.value || null
  }
  return portalExplanationItems.value[keyOrIndex] || leadingPortalExplanation.value || null
}

function buildPortalExplanationLabel(item) {
  if (!item) {
    return ''
  }
  const parts = [item.dimensionName, item.moduleLabel || item.moduleCode].filter(Boolean)
  return Array.from(new Set(parts)).join(' / ')
}

function resolveExplanationFirstText(fallback, explanation) {
  const label = buildPortalExplanationLabel(explanation)
  const summary = explanation?.summary || explanation?.explanationSummary || explanation?.sourceDescription || ''
  if (label && summary) {
    return `${label}: ${summary}`
  }
  if (summary) {
    return summary
  }
  return fallback
}

function resolveExplanationFirstActionText(fallback, explanation) {
  return explanation?.actionText || explanation?.recommendAction || fallback
}

const resolvedSummaryCards = computed(() => {
  const cards = [
    {
      key: 'total',
      label: '合同总量',
      value: summaryData.value.totalCount != null ? summaryData.value.totalCount : total.value,
      unit: '份',
      note: '当前筛选范围内纳入合同备案链路的合同记录总量。',
      cardClass: ''
    },
    {
      key: 'unfiled',
      label: '未备案预警',
      value: valueOrDefault(summaryData.value.unfiledCount, valueOrDefault(summaryData.value.pendingCount, 0)),
      unit: '份',
      note: '草稿和待备案合同应优先补齐备案编号、备案时间和主体字段，避免备案链路积压。',
      cardClass: 'ygb-summary-card--warning'
    },
    {
      key: 'expiringSoon',
      label: '30天内到期提醒',
      value: valueOrDefault(summaryData.value.expiringSoonCount, 0),
      unit: '份',
      note: '建议提前梳理 30 天内到期合同，确认续签、归档或替换安排。',
      cardClass: 'ygb-summary-card--primary'
    },
    {
      key: 'filed',
      label: '已备案合同',
      value: valueOrDefault(summaryData.value.filedCount, 0),
      unit: '份',
      note: '已完成备案留痕，可直接承接考勤和工资链路的合同。',
      cardClass: 'ygb-summary-card--success'
    }
  ]

  return cards.map(item => ({
    ...item,
    note: resolveExplanationFirstText(item.note, resolvePortalExplanationItem(item.key))
  }))
})

function buildBaseFocusQueues() {
  const rows = contractList.value || []
  return [
    {
      key: 'unfiled',
      title: '未备案预警',
      desc: '草稿和待备案合同应优先补齐备案编号、备案时间和主体字段，避免合同主链路在备案环节积压。',
      count: summaryData.value.unfiledCount != null ? summaryData.value.unfiledCount : rows.filter(item => isUnfiledContract(item)).length,
      unit: '份',
      actionText: rows.some(item => isUnfiledContract(item)) ? '优先补备案' : '待办平稳'
    },
    {
      key: 'expiringSoon',
      title: '合同到期提醒',
      desc: '优先梳理 30 天内到期合同，提前确认续签、归档或替换安排，避免影响后续用工与工资链路。',
      count: summaryData.value.expiringSoonCount != null ? summaryData.value.expiringSoonCount : rows.filter(item => isContractExpiringSoon(item)).length,
      unit: '份',
      actionText: rows.some(item => isContractExpiringSoon(item)) ? '优先确认续签' : '到期平稳'
    },
    {
      key: 'rejected',
      title: '驳回合同重新流转',
      desc: '驳回对象应先回查主体关系、期限和工资标准，再重新进入备案闭环。',
      count: summaryData.value.rejectedCount != null ? summaryData.value.rejectedCount : rows.filter(item => isRejectedContract(item)).length,
      unit: '份',
      actionText: rows.some(item => isRejectedContract(item)) ? '回查后重提' : '驳回可控'
    },
    {
      key: 'ocrFailed',
      title: 'OCR 异常合同回查',
      desc: '优先处置 OCR 识别失败对象，避免关键信息缺失继续传导到考勤和工资环节。',
      count: summaryData.value.ocrFailedCount != null ? summaryData.value.ocrFailedCount : rows.filter(item => isOcrFailedContract(item)).length,
      unit: '份',
      actionText: rows.some(item => isOcrFailedContract(item)) ? '优先补识别' : '识别平稳'
    },
    {
      key: 'clauseMissingOrExpired',
      title: '条款缺失与已到期合同',
      desc: '条款缺失和已到期对象适合集中补录、续签和归档处理。',
      count: valueOrDefault(summaryData.value.clauseMissingCount, 0) + valueOrDefault(summaryData.value.expiredCount, 0),
      unit: '份',
      actionText: rows.some(item => isClauseMissingOrExpiredContract(item)) ? '集中处理续签' : '归档平稳'
    }
  ]
}

const baseFocusQueues = computed(() => buildBaseFocusQueues())

const fallbackPortalExplanations = computed(() => baseFocusQueues.value.map(item => ({
  key: item.key,
  dimensionName: item.title,
  currentValue: item.count,
  targetValue: '-',
  summary: item.desc,
  evidenceModule: 'contract',
  recommendModule: 'contract',
  defaultQuery: buildContractExplanationQuery({ focusKey: item.key }),
  sourceLabel: '530.1 合同备案办理解释',
  sourceDescription: item.desc,
  actionText: item.actionText
})))

const portalExplanations = computed(() => {
  const fallbackMap = new Map(fallbackPortalExplanations.value.map(item => [item.key, item]))
  const remoteList = Array.isArray(summaryData.value.ygbExplanation) ? summaryData.value.ygbExplanation : []

  remoteList.forEach(item => {
    const key = item?.key || item?.focusKey
    if (key && fallbackMap.has(key)) {
      fallbackMap.set(key, {
        ...fallbackMap.get(key),
        ...item
      })
      return
    }
    if (key) {
      fallbackMap.set(key, item)
      return
    }
    fallbackMap.set(`remote-${fallbackMap.size + 1}`, item)
  })

  return Array.from(fallbackMap.values())
})

const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'ygb',
  panelTitle: '530.1 合同备案办理解释',
  panelDescription: '当前解释项按 530.1 办理链口径展示合同备案、OCR 和条款补录重点。'
}))

const portalExplanationSummary = computed(() => resolvePortalExplanationSummary(portalExplanationItems.value, ''))
const leadingPortalExplanation = computed(() => portalExplanationItems.value[0] || null)
const resolvedFocusQueues = computed(() => baseFocusQueues.value.map(item => {
  const explanation = resolvePortalExplanationItem(item.key)
  return {
    ...item,
    title: explanation?.dimensionName || item.title,
    desc: resolveExplanationFirstText(item.desc, explanation),
    actionText: resolveExplanationFirstActionText(item.actionText, explanation)
  }
}))
const activeFocus = computed(() => resolvedFocusQueues.value.find(item => item.key === activeFocusKey.value) || resolvedFocusQueues.value[0] || null)
const activePortalExplanation = computed(() => resolvePortalExplanationItem(activeFocus.value?.key || 0))
const visibleContractList = computed(() => prioritizeFocusRows(contractList.value, row => matchContractFocus(row, activeFocus.value?.key)))

const focusTableHint = computed(() => {
  if (!activeFocus.value) {
    return '当前按查询条件展示合同备案台账。'
  }
  return `${activeFocus.value.title}，系统已将对应合同前置，便于优先处理最影响主链路流转的对象。`
})

const selectedContractOverview = computed(() => {
  if (!currentContract.value) {
    return [
      { label: '劳动者', value: '-' },
      { label: '派遣 -> 用工', value: '-' },
      { label: '备案状态', value: '-' },
      { label: 'OCR/条款', value: '-' }
    ]
  }

  return [
    { label: '劳动者', value: currentContract.value.personName || '-' },
    {
      label: '派遣 -> 用工',
      value: `${currentContract.value.dispatchEnterpriseName || '-'} -> ${currentContract.value.employerEnterpriseName || '-'}`
    },
    { label: '备案状态', value: contractStatusLabel(currentContract.value.contractStatus) },
    {
      label: 'OCR/条款',
      value: `${ocrStatusLabel(currentContract.value.ocrStatus)} / ${clauseStatusLabel(currentContract.value.clauseCheckStatus)}`
    }
  ]
})

const selectedTemplate = computed(() => templateOptions.value.find(item => item.templateId === form.value.templateId) || null)

const resolvedTemplateHint = computed(() => {
  if (!selectedTemplate.value) {
    return '未引用模板，可直接填写合同主数据；如模板库存在同类型标准模板，建议优先引用。'
  }
  return [
    `模板：${selectedTemplate.value.templateName || '-'}`,
    `版本：${selectedTemplate.value.templateVersion || '-'}`,
    `类型：${templateTypeLabel(selectedTemplate.value.templateType)}`,
    `范围：${selectedTemplate.value.applicableScope || '未配置'}`
  ].join('；')
})

const primaryContractAction = computed(() => {
  if (!currentContract.value) {
    return { label: '选择待办对象', action: 'detail' }
  }
  if (isReadOnlyRole.value) {
    return { label: '查看详情', action: 'detail' }
  }
  if (
    isUnfiledContract(currentContract.value)
    || isRejectedContract(currentContract.value)
    || isOcrFailedContract(currentContract.value)
    || isClauseMissingContract(currentContract.value)
  ) {
    return { label: '修改合同', action: 'edit' }
  }
  return { label: '查看详情', action: 'detail' }
})

const currentContractActionSummary = computed(() => {
  if (!currentContract.value) {
    return '先从左侧焦点队列选择重点合同，再联动查看当前办理建议。'
  }
  if (isUnfiledContract(currentContract.value)) {
    return '该合同仍处于未备案阶段，建议先补齐备案编号、备案时间和主体字段，再推动进入正式备案状态。'
  }
  if (isContractExpiringSoon(currentContract.value)) {
    return '该合同将在 30 天内到期，建议尽快确认续签、归档或替换安排，避免影响后续用工与工资链路。'
  }
  if (isRejectedContract(currentContract.value)) {
    return '该合同已被驳回，建议优先回查主体关系、期限和工资标准后重新提报。'
  }
  if (isOcrFailedContract(currentContract.value)) {
    return '该合同 OCR 识别失败，建议先回查原件或手工补录关键信息，避免下游继续引用不完整字段。'
  }
  if (isClauseMissingContract(currentContract.value)) {
    return '该合同条款存在缺失，建议优先补齐缺项或更换有效文本，再作为主链路底账使用。'
  }
  if (isExpiredOrReleasedContract(currentContract.value)) {
    return '该合同已到期或解除，更适合优先核对续签、归档或替换合同对象。'
  }
  return '该合同当前备案信息相对完整，可继续承接考勤上报和工资监管链路。'
})

function buildHintTags(contract) {
  if (!contract) {
    return [{ label: '未选中合同，可先在列表中选择待处理对象', type: 'info' }]
  }

  const tags = []
  if (isUnfiledContract(contract)) {
    tags.push({ label: '当前未备案，建议先补齐备案编号、时间和校验结果', type: 'warning' })
  }
  if (isContractExpiringSoon(contract)) {
    tags.push({ label: '合同将在30天内到期，建议优先确认续签或归档安排', type: 'warning' })
  }
  if (isRejectedContract(contract)) {
    tags.push({ label: '当前已驳回，建议回查主体关系、期限和工资标准后重新提报', type: 'danger' })
  }
  if (isExpiredOrReleasedContract(contract)) {
    tags.push({ label: '当前合同已到期或解除，建议核对续签或归档状态', type: 'info' })
  }
  if (isOcrFailedContract(contract)) {
    tags.push({ label: 'OCR 识别失败，建议优先复核原件或改人工补录', type: 'warning' })
  }
  if (isClauseMissingContract(contract)) {
    tags.push({ label: '条款存在缺失，可能影响后续工资与用工合规判断', type: 'warning' })
  }
  if (String(contract.contractStatus) === '2') {
    tags.push({ label: '当前已备案，可直接承接考勤上报和工资链路', type: 'success' })
  }
  if (!contract.monthlyWage) {
    tags.push({ label: '月工资标准未填写，后续工资链路可能缺少基准', type: 'warning' })
  }
  if (!tags.length) {
    tags.push({ label: '当前合同信息完整，可继续进入后续办理链路', type: 'success' })
  }
  return tags
}

const currentContractActionTags = computed(() => buildHintTags(currentContract.value))
const contractHintTags = computed(() => buildHintTags(currentContract.value))
const detailHintTags = computed(() => buildHintTags(detail.value))

function buildExplanationFirstTags(tags = [], explanation = activePortalExplanation.value) {
  const extras = []
  const label = buildPortalExplanationLabel(explanation)
  const summary = explanation?.summary || explanation?.explanationSummary || explanation?.sourceDescription || portalExplanationSummary.value

  if (label) {
    extras.push({ label: `530.1 ${label}`, type: 'warning' })
  }
  if (summary) {
    extras.push({ label: summary, type: 'success' })
  }
  return [...extras, ...tags].slice(0, 4)
}

const resolvedCurrentContractActionSummary = computed(() => resolveExplanationFirstText(
  currentContractActionSummary.value,
  activePortalExplanation.value
))

const resolvedCurrentContractActionTags = computed(() => buildExplanationFirstTags(currentContractActionTags.value, activePortalExplanation.value))
const resolvedContractHintTags = computed(() => buildExplanationFirstTags(contractHintTags.value, activePortalExplanation.value))
const resolvedDetailHintTags = computed(() => buildExplanationFirstTags(detailHintTags.value, activePortalExplanation.value))

const resolvedPrimaryContractAction = computed(() => ({
  ...primaryContractAction.value,
  label: resolveExplanationFirstActionText(primaryContractAction.value.label, activePortalExplanation.value)
}))

const workflowSteps = computed(() => ([
  {
    label: '确认主体关系',
    desc: '先确认派遣单位、用工单位和劳动者归属，避免后续考勤和工资链路挂错主体。'
  },
  {
    label: '补齐期限和工资标准',
    desc: '补录签订、生效、终止日期和月工资标准，确保合同具备进入发薪链路的基础字段。'
  },
  {
    label: '处理 OCR 与条款校验',
    desc: '先解决 OCR 失败和条款缺失，再进入正式备案留痕，避免合同质量问题下沉到后续模块。'
  },
  {
    label: '完成备案编号与时间留痕',
    desc: '备案成功后补齐备案编号、备案时间和链上哈希，形成完整归档记录。'
  },
  {
    label: '承接考勤与工资链路',
    desc: '已备案合同应直接供考勤上报、月度归集和工资批次生成使用，避免主链中断。'
  }
]))

const resolvedWorkflowSteps = computed(() => workflowSteps.value.map((item, index) => {
  const explanation = resolvePortalExplanationItem(index)
  return {
    ...item,
    label: explanation?.dimensionName || explanation?.moduleLabel || explanation?.moduleCode || item.label,
    desc: resolveExplanationFirstText(item.desc, explanation)
  }
}))

function getList() {
  loading.value = true
  Promise.all([
    listContract(queryParams.value),
    getContractSummary(buildSummaryQuery())
  ]).then(([listResponse, summaryResponse]) => {
    contractList.value = listResponse.rows || []
    total.value = listResponse.total || 0
    summaryData.value = summaryResponse.data || {}
    syncCurrentContract()
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

function syncCurrentContract() {
  if (currentContract.value) {
    const matched = visibleContractList.value.find(item => String(item.contractId) === String(currentContract.value.contractId))
    if (matched) {
      currentContract.value = matched
      return
    }
  }
  currentContract.value = visibleContractList.value.length > 0 ? visibleContractList.value[0] : undefined
}

function loadEnterpriseOptions() {
  optionselectEnterprise().then(response => {
    enterpriseOptions.value = response.data || []
  })
}

function loadPersonOptions(query = {}) {
  optionselectPerson(query).then(response => {
    const list = response.data || []
    if (!query.enterpriseId) {
      personOptions.value = list
    }
    formPersonOptions.value = list
  })
}

function loadTemplateOptions(templateType = form.value.contractType) {
  return optionselectContractTemplate({ templateType }).then(response => {
    templateOptions.value = response.data || []
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    contractId: undefined,
    contractNo: undefined,
    dispatchEnterpriseId: undefined,
    employerEnterpriseId: undefined,
    personId: undefined,
    contractType: '1',
    templateId: undefined,
    contractStatus: '1',
    signDate: undefined,
    startDate: undefined,
    endDate: undefined,
    monthlyWage: undefined,
    filingNo: undefined,
    filingTime: undefined,
    ocrStatus: '0',
    clauseCheckStatus: '0',
    blockchainHash: undefined,
    contractFileUrl: undefined,
    remark: undefined
  }
  formPersonOptions.value = personOptions.value
  templateOptions.value = []
  proxy.resetForm('contractRef')
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    contractNo: undefined,
    dispatchEnterpriseId: undefined,
    personId: undefined,
    contractStatus: undefined,
    ocrStatus: undefined
  })
  applyWorkbenchRouteQuery(route.query, queryParams.value, contractWorkbenchFilterFields)
  activeFocusKey.value = resolveContractFocusKey(route.query.focusKey)
  getList()

watchEffect(() => {
  setPageGuide({
    title: '????????' || '????????',
    description: '?????????????????????????????????' || '?????????????????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: resolvedFocusQueues.value,
    selection: [...selectedContractOverview.value, { label: '??????', value: resolvedCurrentContractActionSummary.value }],
    workflow: resolvedWorkflowSteps.value,
    hints: [...resolvedCurrentContractActionTags.value, ...resolvedContractHintTags.value].slice(0, 6)
  })
})

}

function clearWorkbenchContext() {
  activeFocusKey.value = ''
  Object.assign(queryParams.value, {
    pageNum: 1,
    dispatchEnterpriseId: undefined,
    personId: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, contractWorkbenchRouteFields)
  })
  getList()
}

function applyContractWorkbenchQuery(routeQuery = {}) {
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    contractNo: undefined,
    dispatchEnterpriseId: undefined,
    personId: undefined,
    contractStatus: undefined,
    ocrStatus: undefined
  })
  applyWorkbenchRouteQuery(routeQuery, queryParams.value, contractWorkbenchFilterFields)
  activeFocusKey.value = resolveContractFocusKey(routeQuery.focusKey)
  currentContract.value = undefined
  syncCurrentContract()
  getList()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.contractId)
  contractNos.value = selection.map(item => item.contractNo)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleRowClick(row) {
  currentContract.value = row
}

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  syncCurrentContract()
}

function handlePortalExplanationAction(action) {
  if (action?.path === route.path) {
    applyContractWorkbenchQuery(action.query || {})
    return
  }
  openPortalExplanationAction(router, action)
}

function handleAdd() {
  if (blockReadOnlyAction('新增合同备案')) {
    return
  }
  reset()
  loadTemplateOptions()
  open.value = true
  title.value = '新增合同备案'
}

function handleUpdate(row) {
  if (blockReadOnlyAction('修改合同备案')) {
    return
  }
  reset()
  const contractId = row?.contractId || ids.value[0]
  if (!contractId) {
    return
  }
  getContract(contractId).then(response => {
    form.value = response.data || {}
    form.value.templateId = undefined
    currentContract.value = response.data || currentContract.value
    open.value = true
    title.value = '修改合同备案'
    loadTemplateOptions(form.value.contractType)
    if (form.value.dispatchEnterpriseId) {
      loadPersonOptions({ enterpriseId: form.value.dispatchEnterpriseId })
    }
  })
}

function openDetail(row) {
  if (!row?.contractId) {
    return
  }
  currentContract.value = row
  getContract(row.contractId).then(response => {
    detail.value = response.data
    detailOpen.value = true
  })
}

function handleDispatchChange(value) {
  form.value.personId = undefined
  if (value) {
    loadPersonOptions({ enterpriseId: value })
    return
  }
  formPersonOptions.value = personOptions.value
}

function handleContractTypeChange(value) {
  form.value.templateId = undefined
  if (!value) {
    templateOptions.value = []
    return
  }
  loadTemplateOptions(value)
}

function handleTemplateChange(templateId) {
  const template = templateOptions.value.find(item => item.templateId === templateId)
  if (!template) {
    return
  }
  if (!form.value.contractType && template.templateType) {
    form.value.contractType = template.templateType
  }
  const hint = `[模板引用] ${template.templateName || ''} ${template.templateVersion || ''} ${template.applicableScope || ''}`.trim()
  if (hint && !(form.value.remark || '').includes(hint)) {
    form.value.remark = form.value.remark ? `${form.value.remark}\n${hint}` : hint
  }
}

function submitForm() {
  proxy.$refs.contractRef.validate(valid => {
    if (!valid) {
      return
    }
    if (!form.value.contractFileUrl) {
      proxy.$modal.msgWarning('请先上传合同原件')
      return
    }
    if (form.value.contractId != undefined) {
      updateContract(form.value).then(() => {
        proxy.$modal.msgSuccess('修改成功')
        open.value = false
        getList()
        refreshDetailIfMatched(form.value.contractId)
      })
      return
    }
    addContract(form.value).then(() => {
      proxy.$modal.msgSuccess('新增成功')
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  if (blockReadOnlyAction('删除合同备案')) {
    return
  }
  const contractIds = row?.contractId || ids.value
  const names = row?.contractNo || contractNos.value.join('、')
  proxy.$modal.confirm(`是否确认删除合同“${names}”的数据项？`).then(function() {
    return delContract(contractIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

function refreshDetailIfMatched(contractId) {
  if (detailOpen.value && detail.value && String(detail.value.contractId) === String(contractId)) {
    getContract(contractId).then(response => {
      detail.value = response.data
    })
  }
}

function handlePrimaryContractAction() {
  if (!currentContract.value) {
    return
  }
  if (primaryContractAction.value.action === 'edit') {
    handleUpdate(currentContract.value)
    return
  }
  openDetail(currentContract.value)
}

function handleExport() {
  proxy.download('ygb/contract/export', {
    ...queryParams.value
  }, `contract_${new Date().getTime()}.xlsx`)
}

function blockReadOnlyAction(actionLabel) {
  if (!isReadOnlyRole.value) {
    return false
  }
  proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}仅保留查看和导出能力，不能${actionLabel}`)
  return true
}

function isUnfiledContract(contract) {
  return ['0', '1'].includes(String(contract?.contractStatus))
}

function isRejectedContract(contract) {
  return String(contract?.contractStatus) === '3'
}

function isExpiredOrReleasedContract(contract) {
  return ['4', '5'].includes(String(contract?.contractStatus))
}

function isOcrFailedContract(contract) {
  return String(contract?.ocrStatus) === '2'
}

function isClauseMissingContract(contract) {
  return String(contract?.clauseCheckStatus) === '2'
}

function isClauseMissingOrExpiredContract(contract) {
  return isClauseMissingContract(contract) || isExpiredOrReleasedContract(contract)
}

function matchContractFocus(row, focusKey) {
  if (!row || !focusKey) {
    return false
  }
  if (focusKey === 'pending' || focusKey === 'unfiled') {
    return isUnfiledContract(row)
  }
  if (focusKey === 'expiringSoon') {
    return isContractExpiringSoon(row)
  }
  if (focusKey === 'rejected') {
    return isRejectedContract(row)
  }
  if (focusKey === 'ocrFailed') {
    return isOcrFailedContract(row)
  }
  if (focusKey === 'clauseMissingOrExpired') {
    return isClauseMissingOrExpiredContract(row)
  }
  return false
}

function isContractExpiringSoon(contract) {
  if (!contract?.endDate || isExpiredOrReleasedContract(contract)) {
    return false
  }
  const endTime = new Date(contract.endDate).getTime()
  if (Number.isNaN(endTime)) {
    return false
  }
  const now = new Date()
  now.setHours(0, 0, 0, 0)
  if (endTime < now.getTime()) {
    return false
  }
  const limit = new Date(now)
  limit.setDate(limit.getDate() + 30)
  return endTime <= limit.getTime()
}

function prioritizeFocusRows(rows, predicate) {
  const matched = []
  const rest = []
  ;(rows || []).forEach(item => {
    if (predicate(item)) {
      matched.push(item)
    } else {
      rest.push(item)
    }
  })
  return [...matched, ...rest]
}

function contractStatusLabel(value) {
  const matched = contractStatusOptions.find(item => String(item.value) === String(value))
  return matched ? matched.label : '-'
}

function ocrStatusLabel(value) {
  const matched = ocrStatusOptions.find(item => String(item.value) === String(value))
  return matched ? matched.label : '-'
}

function clauseStatusLabel(value) {
  const matched = clauseCheckStatusOptions.find(item => String(item.value) === String(value))
  return matched ? matched.label : '-'
}

function enterpriseName(enterpriseId) {
  const matched = enterpriseOptions.value.find(item => String(item.enterpriseId) === String(enterpriseId))
  return matched ? matched.enterpriseName : ''
}

function valueOrDefault(value, fallback = 0) {
  return value != null ? value : fallback
}

function formatMoney(value) {
  if (value == null || value === '') {
    return '0.00'
  }
  const amount = Number(value)
  if (Number.isNaN(amount)) {
    return String(value)
  }
  return amount.toFixed(2)
}

function maskIdCard(idCard) {
  if (!idCard || idCard.length < 8) {
    return idCard
  }
  return `${idCard.slice(0, 4)}********${idCard.slice(-4)}`
}

function resolveContractFocusKey(value) {
  const normalized = typeof value === 'string' ? value.trim() : ''
  return resolvedFocusQueues.value.some(item => item.key === normalized) ? normalized : ''
}

watch(resolvedFocusQueues, queues => {
  if (!queues.length) {
    activeFocusKey.value = ''
    return
  }
  if (!queues.find(item => item.key === activeFocusKey.value)) {
    activeFocusKey.value = queues[0].key
  }
}, { immediate: true })

applyWorkbenchRouteQuery(route.query, queryParams.value, contractWorkbenchFilterFields)
activeFocusKey.value = resolveContractFocusKey(route.query.focusKey)
loadEnterpriseOptions()
loadPersonOptions()
reset()
getList()
</script>

<style scoped lang="scss">
.ygb-workbench-alert {
  margin-bottom: 16px;
}

.ygb-workbench-alert__title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.ygb-workbench-alert__desc {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  color: #5f6f80;
  line-height: 1.7;
}

.ygb-summary-grid,
.ygb-focus-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.ygb-summary-grid {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.ygb-summary-card,
.ygb-focus-card {
  border: 1px solid #dbe5f0;
  border-radius: 16px;
  background: #fff;
}

.ygb-summary-card {
  padding: 18px 20px;
}

.ygb-summary-card__label {
  color: #627486;
  font-size: 13px;
}

.ygb-summary-card__value {
  margin-top: 10px;
  color: #13243a;
  font-size: 28px;
  font-weight: 700;
}

.ygb-summary-card__unit {
  margin-left: 4px;
  font-size: 13px;
  font-weight: 500;
  color: #7b8da1;
}

.ygb-summary-card__note {
  margin-top: 10px;
  color: #5f6f80;
  line-height: 1.7;
  font-size: 13px;
}

.ygb-summary-card--success {
  background: linear-gradient(180deg, #ffffff 0%, #f3fbf5 100%);
}

.ygb-summary-card--warning {
  background: linear-gradient(180deg, #ffffff 0%, #fff9ef 100%);
}

.ygb-summary-card--primary {
  background: linear-gradient(180deg, #ffffff 0%, #f2f7ff 100%);
}

.ygb-card-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.ygb-card-head--between {
  justify-content: space-between;
}

.ygb-card-head__title {
  color: #15304b;
  font-size: 16px;
  font-weight: 700;
}

.ygb-card-head__desc {
  margin-top: 6px;
  color: #627486;
  line-height: 1.7;
  font-size: 13px;
}

.ygb-focus-list,
.ygb-source-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.ygb-focus-list--single {
  grid-template-columns: minmax(0, 1fr);
}

.ygb-source-item {
  padding: 14px 16px;
  border-radius: 12px;
  background: #f6f9fc;
  border: 1px solid #e2eaf2;
}

.ygb-source-item__label {
  color: #708397;
  font-size: 12px;
}

.ygb-source-item__value {
  margin-top: 8px;
  color: #17324d;
  font-size: 15px;
  font-weight: 600;
  line-height: 1.6;
}

.ygb-focus-queue {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  width: 100%;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid #e2eaf2;
  background: #f6f9fc;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s ease;
}

.ygb-focus-queue:hover,
.ygb-focus-queue.is-active {
  border-color: #1f5aa6;
  background: #eef5ff;
}

.ygb-focus-queue__main strong {
  color: #17324d;
  font-size: 15px;
}

.ygb-focus-queue__main p {
  margin: 6px 0 0;
  color: #627486;
  line-height: 1.7;
  font-size: 13px;
}

.ygb-focus-queue__side {
  display: flex;
  min-width: 120px;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
}

.ygb-focus-queue__count {
  color: #15304b;
  font-size: 20px;
  font-weight: 700;
}

.ygb-focus-queue__action {
  color: #1f5aa6;
  font-size: 12px;
}

.ygb-pipeline-list {
  display: grid;
  gap: 12px;
}

.ygb-pipeline-item {
  display: flex;
  gap: 14px;
  padding: 14px 16px;
  border-radius: 12px;
  background: #f7f9fc;
  border: 1px solid #e0e8f0;
}

.ygb-pipeline-item__index {
  width: 34px;
  height: 34px;
  line-height: 34px;
  border-radius: 10px;
  background: #1f5aa6;
  color: #fff;
  text-align: center;
  font-size: 13px;
  font-weight: 700;
  flex-shrink: 0;
}

.ygb-pipeline-item__body strong {
  color: #183552;
  font-size: 14px;
}

.ygb-pipeline-item__body p {
  margin: 6px 0 0;
  color: #627486;
  line-height: 1.7;
  font-size: 13px;
}

.ygb-tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.ygb-recommend-panel {
  margin-top: 16px;
  padding: 14px 16px;
  border-radius: 12px;
  background: #f6f9fc;
  border: 1px solid #e2eaf2;
}

.ygb-recommend-panel__title {
  color: #15304b;
  font-weight: 700;
}

.ygb-recommend-panel__summary {
  margin: 8px 0 0;
  color: #627486;
  line-height: 1.7;
  font-size: 13px;
}

.ygb-focus-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 16px;
}

.ygb-panel-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 16px;
}

.ygb-upload-field {
  width: 100%;
}

.ygb-upload-field__tip {
  margin-top: 8px;
  color: #627486;
  line-height: 1.6;
  font-size: 12px;
}

.detail-block {
  margin-top: 20px;
}

.detail-block h3 {
  margin: 0 0 12px;
  color: #15304b;
  font-size: 15px;
}

@media (max-width: 1200px) {
  .ygb-summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .ygb-summary-grid,
  .ygb-focus-grid,
  .ygb-source-list,
  .ygb-panel-grid {
    grid-template-columns: minmax(0, 1fr);
  }

  .ygb-focus-queue {
    flex-direction: column;
    align-items: flex-start;
  }

  .ygb-focus-queue__side {
    min-width: 0;
    align-items: flex-start;
  }
}
</style>
