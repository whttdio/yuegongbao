<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">技术防范</p>
        <h1 class="ygb-page__title">高处作业申报报备台账</h1>
        <p class="ygb-page__desc">
          当前仅提供 PC 办理入口，严格按“只报备、不审批”落地。
          App 与小程序入口预留，证书核验、导入和电子凭证均按 Stub 联调。
        </p>
      </div>
      <div class="ygb-page__tip">
        <div>企业管理员和经办员在这里完成报备录入、结束登记、凭证查看和导出。</div>
        <div>证书异常不会阻断提交，但会在详情和台账中保留风险标记。</div>
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
      <div class="ygb-tag-list">
        <el-tag v-for="item in workbenchContext.tags" :key="item.key" effect="plain">{{ item.label }}：{{ item.value }}</el-tag>
      </div>
    </el-alert>

    <el-alert
      v-if="isReadOnlyRole"
      class="ygb-workbench-alert"
      type="warning"
      :closable="false"
      :title="readOnlyAlertTitle"
      :description="readOnlyAlertDescription"
      show-icon
    />

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
      <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch">
        <el-form-item label="报备编号">
          <el-input v-model="queryParams.reportNo" clearable style="width: 180px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="行政区划">
          <el-select v-model="queryParams.regionCode" clearable style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="作业单位">
          <el-select v-model="queryParams.enterpriseId" clearable filterable style="width: 220px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="报备状态">
          <el-select v-model="queryParams.reportStatus" clearable style="width: 140px">
            <el-option v-for="item in reportStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="证书状态">
          <el-select v-model="queryParams.certValidStatus" clearable style="width: 140px">
            <el-option v-for="item in certStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="queryParams.startTimeBegin" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 200px" />
        </el-form-item>
        <el-form-item label="至">
          <el-date-picker v-model="queryParams.startTimeEnd" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="toolbar-card ygb-toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['ygb:heightWorkReport:add']">新增报备</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:heightWorkReport:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
        <el-table v-loading="loading" :data="visibleReportList" @row-click="handleRowClick">
        <el-table-column label="报备编号" prop="reportNo" min-width="180" />
        <el-table-column label="作业单位/个人" prop="enterpriseName" min-width="180" />
        <el-table-column label="作业地点" prop="workLocation" min-width="220" show-overflow-tooltip />
        <el-table-column label="计划时段" min-width="220">
          <template #default="scope">
            {{ parseTime(scope.row.startTime, '{y}-{m}-{d} {h}:{i}') }} 至 {{ parseTime(scope.row.endTime, '{y}-{m}-{d} {h}:{i}') }}
          </template>
        </el-table-column>
        <el-table-column label="高度(米)" prop="workHeightM" width="100" />
        <el-table-column label="人数" prop="workerCount" width="80" />
        <el-table-column label="监护人" prop="guardianName" width="110" />
        <el-table-column label="证书状态" width="110">
          <template #default="scope">
            <dict-tag :options="certStatusOptions" :value="scope.row.certValidStatus" />
          </template>
        </el-table-column>
        <el-table-column label="报备状态" width="110">
          <template #default="scope">
            <dict-tag :options="reportStatusOptions" :value="scope.row.reportStatus" />
          </template>
        </el-table-column>
        <el-table-column label="来源" width="110">
          <template #default="scope">{{ sourceModeLabel(scope.row.sourceMode) }}</template>
        </el-table-column>
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" align="center" width="300" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
            <el-button link type="primary" icon="Tickets" @click.stop="openVoucher(scope.row)" v-hasPermi="['ygb:heightWorkReport:voucher']">凭证</el-button>
            <el-button v-if="!isReadOnlyRole" link type="primary" icon="Edit" @click.stop="handleUpdate(scope.row)" :disabled="scope.row.reportStatus === '1'" v-hasPermi="['ygb:heightWorkReport:edit']">修改</el-button>
            <el-button v-if="!isReadOnlyRole" link type="primary" icon="CircleCheck" @click.stop="handleFinish(scope.row)" :disabled="scope.row.reportStatus === '1'" v-hasPermi="['ygb:heightWorkReport:finish']">结束</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <el-dialog :title="title" v-model="open" width="1080px" append-to-body>
      <el-form ref="reportRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="报备主体" prop="reporterType">
              <el-radio-group v-model="form.reporterType" @change="handleReporterTypeChange">
                <el-radio v-for="item in reporterTypeOptions" :key="item.value" :label="item.value">{{ item.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col v-if="form.reporterType === '1'" :span="8">
            <el-form-item label="作业单位" prop="enterpriseId">
              <el-select v-model="form.enterpriseId" clearable filterable @change="handleEnterpriseChange">
                <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col v-else :span="8">
            <el-form-item label="作业单位/个人" prop="enterpriseName">
              <el-input v-model="form.enterpriseName" placeholder="请输入个人姓名或个体工商户名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="填报人" prop="applicantName">
              <el-input v-model="form.applicantName" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系电话" prop="applicantPhone">
              <el-input v-model="form.applicantPhone" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="作业地点" prop="workLocation">
              <el-input v-model="form.workLocation" placeholder="请输入具体地点" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="经度" prop="longitude">
              <el-input v-model="form.longitude" placeholder="如 113.952700" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="纬度" prop="latitude">
              <el-input v-model="form.latitude" placeholder="如 22.540300" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="计划开始" prop="startTime">
              <el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="计划结束" prop="endTime">
              <el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="作业高度(米)" prop="workHeightM">
              <el-input-number v-model="form.workHeightM" :min="2" :precision="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="作业人数">
              <el-input :model-value="form.workerList.length" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="监护人姓名" prop="guardianName">
              <el-input v-model="form.guardianName" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="监护人电话" prop="guardianPhone">
              <el-input v-model="form.guardianPhone" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="安全措施" prop="safetyMeasures">
              <el-checkbox-group v-model="form.safetyMeasures">
                <el-checkbox v-for="item in safetyMeasureOptions" :key="item" :label="item">{{ item }}</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="特殊说明">
              <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="可选填写现场说明" />
            </el-form-item>
          </el-col>
        </el-row>

        <div class="worker-editor">
          <div class="worker-editor__header">
            <h3>作业人员清单</h3>
            <el-button v-if="!isReadOnlyRole" type="primary" plain icon="Plus" @click="addWorker">新增人员</el-button>
          </div>
          <el-table :data="form.workerList" border>
            <el-table-column label="姓名" min-width="120">
              <template #default="scope">
                <el-input v-model="scope.row.workerName" placeholder="姓名" />
              </template>
            </el-table-column>
            <el-table-column label="身份证号" min-width="200">
              <template #default="scope">
                <el-input v-model="scope.row.idCard" placeholder="身份证号" />
              </template>
            </el-table-column>
            <el-table-column label="证书编号" min-width="180">
              <template #default="scope">
                <el-input v-model="scope.row.certNo" placeholder="证书编号" />
              </template>
            </el-table-column>
            <el-table-column label="证书照片" min-width="180">
              <template #default="scope">
                <ImageUpload v-model="scope.row.certPhotoUrl" :limit="1" :drag="false" />
              </template>
            </el-table-column>
            <el-table-column class-name="table-fill-column" min-width="1" />

            <el-table-column v-if="!isReadOnlyRole" label="操作" fixed="right" width="90" align="center">
              <template #default="scope">
                <el-button link type="danger" icon="Delete" @click="removeWorker(scope.$index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button v-if="!isReadOnlyRole" type="primary" @click="submitForm">确定</el-button>
          <el-button @click="open = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="作业结束" v-model="finishOpen" width="520px" append-to-body>
      <el-form ref="finishRef" :model="finishForm" :rules="finishRules" label-width="110px">
        <el-form-item label="实际结束时间" prop="actualEndTime">
          <el-date-picker v-model="finishForm.actualEndTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="完成确认照片">
          <ImageUpload v-model="finishForm.endPhotoUrl" :limit="1" :drag="false" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button v-if="!isReadOnlyRole" type="primary" @click="submitFinish">确定</el-button>
          <el-button @click="finishOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <page-detail-dialog v-model="detailOpen" title="报备详情" width="860px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="报备编号">{{ detail.reportNo }}</el-descriptions-item>
          <el-descriptions-item label="报备状态">
            <dict-tag :options="reportStatusOptions" :value="detail.reportStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="作业单位/个人">{{ detail.enterpriseName }}</el-descriptions-item>
          <el-descriptions-item label="报备主体">
            <dict-tag :options="reporterTypeOptions" :value="detail.reporterType" />
          </el-descriptions-item>
          <el-descriptions-item label="填报人">{{ detail.applicantName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ detail.applicantPhone }}</el-descriptions-item>
          <el-descriptions-item label="作业地点" :span="2">{{ detail.workLocation }}</el-descriptions-item>
          <el-descriptions-item label="计划时段" :span="2">
            {{ parseTime(detail.startTime, '{y}-{m}-{d} {h}:{i}:{s}') }} 至 {{ parseTime(detail.endTime, '{y}-{m}-{d} {h}:{i}:{s}') }}
          </el-descriptions-item>
          <el-descriptions-item label="作业高度">{{ detail.workHeightM }} 米</el-descriptions-item>
          <el-descriptions-item label="作业人数">{{ detail.workerCount }}</el-descriptions-item>
          <el-descriptions-item label="监护人">{{ detail.guardianName }}</el-descriptions-item>
          <el-descriptions-item label="监护人电话">{{ detail.guardianPhone }}</el-descriptions-item>
          <el-descriptions-item label="证书状态">
            <dict-tag :options="certStatusOptions" :value="detail.certValidStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="核验结果">{{ detail.certValidCount }} 有效 / {{ detail.certInvalidCount }} 异常</el-descriptions-item>
          <el-descriptions-item label="安全措施" :span="2">{{ (detail.safetyMeasures || []).join('、') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="来源模式">{{ sourceModeLabel(detail.sourceMode) }}</el-descriptions-item>
          <el-descriptions-item label="来源平台">{{ detail.sourcePlatform || '-' }}</el-descriptions-item>
          <el-descriptions-item label="实际结束时间" :span="2">{{ parseTime(detail.actualEndTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="detail-block">
          <h3>办理提示</h3>
          <div class="ygb-tag-list">
            <el-tag v-for="item in resolvedDetailHintTags" :key="item.label" :type="item.type" effect="plain">{{ item.label }}</el-tag>
          </div>
        </div>

        <div class="detail-block">
          <h3>作业人员</h3>
          <el-table :data="detail.workerList || []" border>
            <el-table-column label="姓名" prop="workerName" min-width="120" />
            <el-table-column label="身份证号" prop="idCard" min-width="180" />
            <el-table-column label="证书编号" prop="certNo" min-width="160" />
            <el-table-column label="核验状态" width="110">
              <template #default="scope">
                <el-tag :type="scope.row.certValidStatus === '0' ? 'success' : 'danger'">
                  {{ scope.row.certValidStatus === '0' ? '有效' : '异常' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="核验说明" prop="certValidMessage" min-width="180" show-overflow-tooltip />
          </el-table>
        </div>

        <div class="detail-block" v-if="voucher">
          <h3>电子凭证</h3>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="凭证令牌">{{ voucher.voucherToken }}</el-descriptions-item>
            <el-descriptions-item label="二维码文本">{{ voucher.qrText }}</el-descriptions-item>
            <el-descriptions-item label="凭证地址">{{ voucher.voucherUrl }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbHeightWorkReport">
import { computed, getCurrentInstance, ref, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import { decoratePortalExplanationItems, openPortalExplanationAction, resolvePortalExplanationSummary } from '@/utils/portalExplanation'
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'
import { useRoleViewMode } from '@/utils/roleView'
import { useAuthorizedRegionOptions } from '@/utils/regionScope'
import {
  buildBaseHintTags,
  certStatusLabel,
  certStatusOptions,
  isImportedSource,
  matchReportFocus,
  prioritizeFocusRows,
  regionNameMap,
  regionOptions as allRegionOptions,
  reporterTypeOptions,
  reportStatusLabel,
  reportStatusOptions,
  safetyMeasureOptions,
  sourceModeLabel,
  useHeightWorkReportPage
} from '@/views/heightWorkReport/useHeightWorkReportPage'

const { proxy } = getCurrentInstance()
const regionOptions = useAuthorizedRegionOptions(allRegionOptions)
const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const router = useRouter()
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const activeFocusKey = ref('')
const {
  showSearch,
  loading,
  total,
  open,
  finishOpen,
  detailOpen,
  title,
  reportList,
  enterpriseOptions,
  summaryData,
  detail,
  voucher,
  currentReport,
  queryParams,
  form,
  rules,
  finishForm,
  finishRules,
  getList,
  syncCurrentReport,
  handleQuery,
  handleRowClick,
  handleAdd,
  handleUpdate,
  openDetail,
  openVoucher,
  handleReporterTypeChange,
  handleEnterpriseChange,
  addWorker,
  removeWorker,
  submitForm,
  handleFinish,
  submitFinish,
  handleExport,
  initialize
} = useHeightWorkReportPage({
  exportFilePrefix: 'ygb_height_work_report',
  canMutate: () => !isReadOnlyRole.value,
  getCurrentList: () => visibleReportList.value,
  afterList: () => {
    syncActiveHeightWorkFocus()
  },
  onBlockedAction: actionLabel => {
    proxy?.$modal?.msgWarning?.(`${readOnlyRoleLabel.value}仅保留高处作业查看、凭证和导出，不能${actionLabel}`)
  }
})

const heightWorkWorkbenchFields = ['enterpriseId', 'regionCode', 'focusKey']

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: heightWorkWorkbenchFields,
  sourceLabel: '企业工作台',
  title: '当前高处作业台账沿用了工作台来源条件',
  description: '当前列表已按企业或行政区划锁定，适合直接继续跟进报备详情、结束登记和凭证核验。',
  fieldLabels: {
    enterpriseId: '企业',
    regionCode: '行政区划'
  },
  fieldFormatters: {
    enterpriseId: value => {
      const matched = enterpriseOptions.value.find(item => String(item.enterpriseId) === String(value))
      return matched ? matched.enterpriseName : value
    },
    regionCode: value => regionNameMap[value] || value
  }
}))

function buildHeightWorkExplanationQuery(extraQuery = {}) {
  return {
    enterpriseId: queryParams.value.enterpriseId,
    regionCode: queryParams.value.regionCode,
    reportStatus: queryParams.value.reportStatus,
    certValidStatus: queryParams.value.certValidStatus,
    ...extraQuery
  }
}

const summaryCards = computed(() => ([
  {
    key: 'total',
    label: '报备记录',
    value: summaryData.value.totalCount ?? total.value,
    unit: '条',
    note: '当前筛选条件下的高处作业报备总量。',
    cardClass: ''
  },
  {
    key: 'active',
    label: '待结束作业',
    value: summaryData.value.activeCount ?? 0,
    unit: '条',
    note: '已完成报备但尚未结束登记的记录。',
    cardClass: 'ygb-summary-card--warning'
  },
  {
    key: 'finished',
    label: '已结束作业',
    value: summaryData.value.finishedCount ?? 0,
    unit: '条',
    note: '已完成结束登记并可直接归档的记录。',
    cardClass: 'ygb-summary-card--success'
  },
  {
    key: 'invalid',
    label: '异常证书人次',
    value: summaryData.value.invalidWorkerCount ?? 0,
    unit: '人',
    note: '证书异常不会阻断提交，但需要后续核查。',
    cardClass: 'ygb-summary-card--primary'
  }
]))

const resolvedSummaryCards = computed(() => summaryCards.value.map((item, index) => ({
  ...item,
  note: resolveExplanationFirstText(item.note, resolvePortalExplanationItem(index))
})))

const focusQueues = computed(() => ([
  {
    key: 'active',
    title: 'Active Reports',
    desc: 'Prioritize reports that still need finish registration.',
    count: summaryData.value.activeCount ?? 0,
    unit: 'items',
    actionText: 'Review active work'
  },
  {
    key: 'invalid',
    title: 'Invalid Certificates',
    desc: 'Review reports that contain certificate exceptions.',
    count: summaryData.value.invalidWorkerCount ?? 0,
    unit: 'people',
    actionText: 'Check certificates'
  },
  {
    key: 'allInvalid',
    title: 'All Invalid Reports',
    desc: 'Escalate reports where every certificate is invalid.',
    count: summaryData.value.allInvalidCount ?? 0,
    unit: 'items',
    actionText: 'Escalate review'
  },
  {
    key: 'imported',
    title: 'Imported Sources',
    desc: 'Verify imported or third-party sourced reports first.',
    count: summaryData.value.importedCount ?? 0,
    unit: 'items',
    actionText: 'Check source chain'
  }
]))

const fallbackPortalExplanations = computed(() => focusQueues.value.map(item => ({
  key: item.key,
  dimensionName: item.title,
  currentValue: item.count,
  targetValue: '-',
  summary: item.desc,
  evidenceModule: 'heightWorkReport',
  recommendModule: 'heightWorkReport',
  defaultQuery: buildHeightWorkExplanationQuery({ focusKey: item.key }),
  sourceLabel: '530.1 高处作业办理解释',
  sourceDescription: item.desc,
  actionText: item.actionText
})))

const portalExplanations = computed(() => {
  const aggregated = Array.isArray(summaryData.value.ygbExplanation) ? summaryData.value.ygbExplanation : []
  if (aggregated.length) {
    const focusKeys = new Set(focusQueues.value.map(item => item.key))
    const filtered = aggregated.filter(item => focusKeys.has(item.key))
    return filtered.length ? filtered : aggregated
  }
  return fallbackPortalExplanations.value
})

const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'ygb',
  panelTitle: '530.1 高处作业办理解释',
  panelDescription: '高处作业解释统一按当前粤工保门户来源条件输出。'
}))

const portalExplanationSummary = computed(() => resolvePortalExplanationSummary(portalExplanationItems.value, ''))
const leadingPortalExplanation = computed(() => portalExplanationItems.value[0] || null)

function resolvePortalExplanationItem(index = 0) {
  return portalExplanationItems.value[index] || leadingPortalExplanation.value || null
}

function buildPortalExplanationLabel(item) {
  if (!item) return ''
  const parts = [item.dimensionName, item.moduleLabel || item.moduleCode].filter(Boolean)
  return Array.from(new Set(parts)).join(' / ')
}

function resolveExplanationFirstText(fallback, explanation) {
  const label = buildPortalExplanationLabel(explanation)
  const summary = explanation?.summary || explanation?.explanationSummary || explanation?.sourceDescription || ''
  if (label && summary) {
    return `${label}：${summary}`
  }
  if (summary) {
    return summary
  }
  if (label) {
    return `${label}优先`
  }
  return fallback
}

function buildExplanationFirstTags(tags = []) {
  const extras = []
  const label = buildPortalExplanationLabel(leadingPortalExplanation.value)
  if (label) {
    extras.push({ label: `530.1主解释：${label}`, type: 'warning' })
  }
  if (portalExplanationSummary.value) {
    extras.push({ label: portalExplanationSummary.value, type: 'success' })
  }
  return [...extras, ...tags].slice(0, 4)
}

const activeFocus = computed(() => focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0] || null)
const visibleReportList = computed(() => prioritizeFocusRows(reportList.value, row => matchReportFocus(row, activeFocus.value?.key)))

const focusItems = computed(() => ([
  { label: '当前区域', value: regionNameMap[queryParams.value.regionCode] || queryParams.value.regionCode || '全部区域' },
  { label: '导入来源', value: `${summaryData.value.importedCount ?? 0} 条` },
  { label: '全部失效报备', value: `${summaryData.value.allInvalidCount ?? 0} 条` },
  { label: '部分失效报备', value: `${summaryData.value.partialInvalidCount ?? 0} 条` }
]))

const resolvedFocusItems = computed(() => focusItems.value.map((item, index) => {
  const explanation = resolvePortalExplanationItem(index)
  return {
    ...item,
    label: explanation?.dimensionName || item.label,
    tip: resolveExplanationFirstText('', explanation)
  }
}))

const selectedReportOverview = computed(() => {
  if (!currentReport.value) {
    return [
      { label: '作业单位', value: '-' },
      { label: '报备状态', value: '-' },
      { label: '证书状态', value: '-' },
      { label: '人数/来源', value: '-' }
    ]
  }
  return [
    { label: '作业单位', value: currentReport.value.enterpriseName || '-' },
    { label: '报备状态', value: reportStatusLabel(currentReport.value.reportStatus) },
    { label: '证书状态', value: certStatusLabel(currentReport.value.certValidStatus) },
    { label: '人数/来源', value: `${currentReport.value.workerCount || 0} / ${sourceModeLabel(currentReport.value.sourceMode)}` }
  ]
})

const workflowSteps = computed(() => ([
  { label: '报备主体录入', desc: '先确认企业或个人主体、作业地点、时段和监护信息。' },
  { label: '人员与证书留痕', desc: '补录作业人员、证书编号和照片，保留逐人核验结果。' },
  { label: '生成凭证并作业', desc: '报备成功后获取电子凭证，用于现场核查和联调展示。' },
  { label: '结束登记与归档', desc: '作业完成后回写结束时间和确认照片，形成闭环台账。' }
]))

const reportHintTags = computed(() => buildBaseHintTags(currentReport.value))
const detailHintTags = computed(() => buildBaseHintTags(detail.value))
const resolvedWorkflowSteps = computed(() => workflowSteps.value.map((item, index) => {
  const explanation = resolvePortalExplanationItem(index)
  return {
    ...item,
    label: explanation?.dimensionName || explanation?.moduleLabel || explanation?.moduleCode || item.label,
    desc: resolveExplanationFirstText(item.desc, explanation)
  }
}))
const resolvedReportHintTags = computed(() => buildExplanationFirstTags(reportHintTags.value))
const resolvedDetailHintTags = computed(() => buildExplanationFirstTags(detailHintTags.value))
const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}仅保留高处作业查看、凭证和导出`)
const readOnlyAlertDescription = computed(() => `${readOnlyRoleDescription.value || ''} 当前页面仍会展示报备摘要、详情和来源条件，但不开放新增、修改、结束和人员编辑动作。`.trim())

function syncActiveHeightWorkFocus() {
  if (!activeFocusKey.value || !focusQueues.value.some(item => item.key === activeFocusKey.value)) {
    activeFocusKey.value = focusQueues.value[0]?.key || ''
  }
}

function resetQuery() {
  proxy.resetForm('queryRef')
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    reportNo: undefined,
    regionCode: undefined,
    enterpriseId: undefined,
    reportStatus: undefined,
    certValidStatus: undefined,
    startTimeBegin: undefined,
    startTimeEnd: undefined
  })
  applyWorkbenchRouteQuery(route.query, queryParams.value, heightWorkWorkbenchFields)
  activeFocusKey.value = resolveHeightWorkReportFocusKey(route.query.focusKey)
  getList()

watchEffect(() => {
  setPageGuide({
    title: '????????' || '????????',
    description: '????????????????????????????' || '????????????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: focusQueues.value,
    selection: selectedReportOverview.value,
    workflow: resolvedWorkflowSteps.value,
    hints: []
  })
})

}

function clearWorkbenchContext() {
  activeFocusKey.value = ''
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    reportNo: undefined,
    enterpriseId: undefined,
    regionCode: undefined,
    reportStatus: undefined,
    certValidStatus: undefined,
    startTimeBegin: undefined,
    startTimeEnd: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, heightWorkWorkbenchFields)
  })
  currentReport.value = undefined
  syncCurrentReport()
  getList()
}

function applyHeightWorkWorkbenchQuery(routeQuery = {}) {
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    reportNo: undefined,
    enterpriseId: undefined,
    regionCode: undefined,
    reportStatus: undefined,
    certValidStatus: undefined,
    startTimeBegin: undefined,
    startTimeEnd: undefined
  })
  applyWorkbenchRouteQuery(routeQuery, queryParams.value, heightWorkWorkbenchFields)
  activeFocusKey.value = resolveHeightWorkReportFocusKey(routeQuery.focusKey)
  currentReport.value = undefined
  syncCurrentReport()
  getList()
}

function handlePortalExplanationAction(action) {
  if (action?.path === route.path) {
    applyHeightWorkWorkbenchQuery(action.query || {})
  }
  openPortalExplanationAction(router, action)
}

applyWorkbenchRouteQuery(route.query, queryParams.value, heightWorkWorkbenchFields)
activeFocusKey.value = resolveHeightWorkReportFocusKey(route.query.focusKey)
initialize()

function resolveHeightWorkReportFocusKey(value) {
  const normalized = typeof value === 'string' ? value.trim() : ''
  return focusQueues.value.some(item => item.key === normalized) ? normalized : ''
}
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
  gap: 8px 12px;
  margin-bottom: 12px;
  color: #5f6f80;
  line-height: 1.7;
  font-size: 13px;
}

.ygb-workbench-alert__desc strong {
  color: #0f5ea8;
}

.ygb-page__tip {
  max-width: 420px;
  color: #5f6f80;
  line-height: 1.8;
  font-size: 13px;
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

.ygb-summary-card__label,
.ygb-focus-item__label {
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
  color: #7b8da1;
  font-size: 13px;
}

.ygb-summary-card__note {
  margin-top: 10px;
  color: #5f6f80;
  line-height: 1.7;
  font-size: 13px;
}

.ygb-summary-card--success {
  background: linear-gradient(180deg, #fff 0%, #f3fbf5 100%);
}

.ygb-summary-card--warning {
  background: linear-gradient(180deg, #fff 0%, #fff9ef 100%);
}

.ygb-summary-card--primary {
  background: linear-gradient(180deg, #fff 0%, #f2f7fd 100%);
}

.ygb-focus-card :deep(.el-card__header) {
  padding: 18px 20px 0;
  border-bottom: none;
}

.ygb-focus-card :deep(.el-card__body) {
  padding: 18px 20px 20px;
}

.ygb-card-head__title {
  color: #13243a;
  font-size: 18px;
  font-weight: 700;
}

.ygb-card-head__desc {
  margin-top: 4px;
  color: #7b8da1;
  font-size: 13px;
  line-height: 1.7;
}

.ygb-focus-list,
.ygb-pipeline-list {
  display: grid;
  gap: 12px;
}

.ygb-focus-item,
.ygb-pipeline-item {
  border: 1px solid #dde7f1;
  border-radius: 14px;
  background: #f8fbfd;
}

.ygb-focus-item {
  padding: 14px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.ygb-focus-item__value {
  color: #13243a;
  font-size: 14px;
}

.ygb-pipeline-item {
  display: grid;
  grid-template-columns: 42px minmax(0, 1fr);
  gap: 14px;
  padding: 16px;
}

.ygb-pipeline-item__index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 34px;
  border-radius: 10px;
  background: #edf5fc;
  color: #0f5ea8;
  font-weight: 700;
}

.ygb-pipeline-item__body strong {
  color: #13243a;
}

.ygb-pipeline-item__body p {
  margin: 8px 0 0;
  color: #5f6f80;
  font-size: 13px;
  line-height: 1.7;
}

.ygb-tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.worker-editor {
  margin-top: 8px;
}

.worker-editor__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.worker-editor__header h3,
.detail-block h3 {
  margin: 0;
  color: #1f2d3d;
  font-size: 16px;
}

.detail-block {
  margin-top: 20px;
}

@media (max-width: 1200px) {
  .ygb-summary-grid,
  .ygb-focus-grid {
    grid-template-columns: 1fr;
  }
}
</style>
