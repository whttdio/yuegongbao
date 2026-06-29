<template>
  <div class="app-container azb-page azb-height-work-workbench">
    <section class="gov-page-header azb-page__header">
      <div>
        <p class="azb-page__eyebrow">高危作业治理</p>
        <h1 class="azb-page__title">{{ roleTitle }}</h1>
        <p class="azb-page__desc">
          {{ roleDescription }}
          当前仍按“只报备、不审批”落地，前端已按应急监管、保险协同、银行协同和企业现场负责人的使用路径重组。
        </p>
      </div>
      <div class="azb-page__tip">
        <div class="azb-page__tip-item">当前视角：{{ roleBadge }}</div>
        <div class="azb-page__tip-item">优先关注：进行中作业、异常证书、第三方导入来源和结束留痕缺失。</div>
        <div class="azb-page__tip-item">小程序和 App 入口预留，当前仅提供 PC 与 Stub 接口联调。</div>
      </div>
    </section>

    <el-alert
      v-if="isReadOnlyRole"
      class="azb-role-alert"
      type="info"
      :closable="false"
      :title="readOnlyAlertTitle"
      :description="readOnlyAlertDescription"
    />

    <el-alert
      v-if="workbenchContext"
      class="azb-workbench-alert"
      type="info"
      :closable="false"
      show-icon
    >
      <template #title>
        <div class="azb-workbench-alert__title">
          <span>{{ workbenchContext.title }}</span>
          <el-button link type="primary" @click="clearWorkbenchContext">清空来源条件</el-button>
        </div>
      </template>
      <div class="azb-workbench-alert__desc">
        <strong>{{ workbenchContext.sourceLabel }}</strong>
        <span>{{ workbenchContext.description }}</span>
      </div>
      <div class="azb-tag-list">
        <el-tag v-for="item in workbenchContext.tags" :key="item.key" effect="plain">{{ item.label }}：{{ item.value }}</el-tag>
      </div>
    </el-alert>

    <div class="azb-summary-grid">
      <div v-for="item in resolvedSummaryCards" :key="item.key" class="azb-summary-card" :class="item.cardClass">
        <div class="azb-summary-card__label">{{ item.label }}</div>
        <div class="azb-summary-card__value">
          {{ item.value }}
          <span class="azb-summary-card__unit">{{ item.unit }}</span>
        </div>
        <div class="azb-summary-card__note">{{ item.note }}</div>
      </div>
    </div>


    <el-card class="search-card azb-search-card" shadow="never">
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

    <el-card class="toolbar-card azb-toolbar-card" shadow="never">
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

    <el-card class="table-card azb-table-card" shadow="never">
      <template #header>
        <div class="azb-card-head azb-card-head--between">
          <div>
            <div class="azb-card-head__title">高危作业治理台账</div>
            <div class="azb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="azb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
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
            <el-button
              v-if="!isReadOnlyRole"
              link
              type="primary"
              icon="Edit"
              @click.stop="handleUpdate(scope.row)"
              :disabled="scope.row.reportStatus === '1'"
              v-hasPermi="['ygb:heightWorkReport:edit']"
            >
              修改
            </el-button>
            <el-button
              v-if="!isReadOnlyRole"
              link
              type="primary"
              icon="CircleCheck"
              @click.stop="handleFinish(scope.row)"
              :disabled="scope.row.reportStatus === '1'"
              v-hasPermi="['ygb:heightWorkReport:finish']"
            >
              结束
            </el-button>
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

            <el-table-column label="操作" fixed="right" width="90" align="center">
              <template #default="scope">
                <el-button v-if="!isReadOnlyRole" link type="danger" icon="Delete" @click="removeWorker(scope.$index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确定</el-button>
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
          <el-button type="primary" @click="submitFinish">确定</el-button>
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
          <el-descriptions-item label="当前视角重点" :span="2">{{ detailFocusText }}</el-descriptions-item>
          <el-descriptions-item label="安全措施" :span="2">{{ (detail.safetyMeasures || []).join('、') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="来源模式">{{ sourceModeLabel(detail.sourceMode) }}</el-descriptions-item>
          <el-descriptions-item label="来源平台">{{ detail.sourcePlatform || '-' }}</el-descriptions-item>
          <el-descriptions-item label="实际结束时间" :span="2">{{ parseTime(detail.actualEndTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="detail-block">
          <h3>治理提示</h3>
          <div class="azb-tag-list">
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

<script setup name="AzbHeightWorkReport">
import { computed, ref, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import useUserStore from '@/store/modules/user'
import { decoratePortalExplanationItems, openPortalExplanationAction, resolvePortalExplanationSummary } from '@/utils/portalExplanation'
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'
import {
  buildAzbDetailHintTags,
  certStatusLabel,
  certStatusOptions,
  focusQueue,
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
  summaryCard,
  useHeightWorkReportPage
} from '@/views/heightWorkReport/useHeightWorkReportPage'
import { useRoleViewMode } from '@/utils/roleView'
import { useAuthorizedRegionOptions } from '@/utils/regionScope'
import { parseTime } from '@/utils/yuegongbao'

const userStore = useUserStore()
const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const regionOptions = useAuthorizedRegionOptions(allRegionOptions)
const router = useRouter()
const { isReadOnlyRole, isInsurerRole, isBankRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const activeFocusKey = ref('')

const {
  proxy,
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
  exportFilePrefix: 'azb_height_work_report',
  canMutate: () => !isReadOnlyRole.value,
  onBlockedAction: (actionLabel) => {
    proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}仅保留摘要、详情、凭证和导出，不能${actionLabel}`)
  },
  getCurrentList: () => visibleReportList.value,
  afterList: () => {
    if (!activeFocusKey.value || !focusQueues.value.some(item => item.key === activeFocusKey.value)) {
      activeFocusKey.value = focusQueues.value[0]?.key || ''
    }
  }
})

const roleView = computed(() => {
  if (isBankRole.value) return 'bank'
  if (isInsurerRole.value) return 'insurer'
  const roles = userStore.roles || []
  if (roles.includes('ygb_enterprise_admin') || roles.includes('ygb_enterprise_operator')) {
    return 'site-enterprise'
  }
  return 'emergency'
})

const roleBadge = computed(() => {
  if (roleView.value === 'bank') return '银行只读协同'
  if (roleView.value === 'insurer') return '保险只读协同'
  if (roleView.value === 'site-enterprise') return '企业现场负责人'
  return '应急监管处置'
})

const roleTitle = computed(() => {
  if (roleView.value === 'bank') return '高危作业与区域信用风险复核台账'
  if (roleView.value === 'insurer') return '高危作业风险与凭证复核协同台账'
  if (roleView.value === 'site-enterprise') return '现场高处作业与结束留痕工作台'
  return '高处作业在途治理与闭环处置工作台'
})

const roleDescription = computed(() => {
  if (roleView.value === 'bank') {
    return '围绕进行中作业、异常证书和导入来源做只读复核，用于辅助区域信用与风险对象判断。'
  }
  if (roleView.value === 'insurer') {
    return '围绕异常证书、第三方来源和结束凭证完整度做风险协同复核，优先识别会影响事故预防服务的高危对象。'
  }
  if (roleView.value === 'site-enterprise') {
    return '围绕进行中作业、结束留痕和证书补录组织现场负责人工作流，重点是快速补证、补照片和结束登记。'
  }
  return '围绕进行中作业、异常证书、超时未结束和导入来源组织应急监管工作流，优先压降现场高危作业风险。'
})

function buildHeightWorkExplanationQuery(extraQuery = {}) {
  return {
    enterpriseId: queryParams.value.enterpriseId,
    regionCode: queryParams.value.regionCode,
    reportStatus: queryParams.value.reportStatus,
    certValidStatus: queryParams.value.certValidStatus,
    ...extraQuery
  }
}

const summaryCards = computed(() => {
  if (roleView.value === 'bank') {
    return [
      summaryCard('total', '报备记录', summaryData.value.totalCount ?? total.value, '条', '当前筛选条件下的高处作业总量。', ''),
      summaryCard('active', '进行中作业', summaryData.value.activeCount ?? 0, '条', '帮助判断区域在途高危作业密度。', 'azb-summary-card--warning'),
      summaryCard('invalid', '异常证书人次', summaryData.value.invalidWorkerCount ?? 0, '人', '用于识别信用协同需重点关注的风险人次。', 'azb-summary-card--danger'),
      summaryCard('imported', '导入来源', summaryData.value.importedCount ?? 0, '条', '反映第三方和导入链路带来的风险对象规模。', '')
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      summaryCard('active', '进行中作业', summaryData.value.activeCount ?? 0, '条', '仍处于在途状态、最值得持续跟踪的作业对象。', 'azb-summary-card--warning'),
      summaryCard('invalid', '异常证书人次', summaryData.value.invalidWorkerCount ?? 0, '人', '证书异常更适合优先联动事故预防服务。', 'azb-summary-card--danger'),
      summaryCard('imported', '导入来源', summaryData.value.importedCount ?? 0, '条', '第三方来源越多，越需要复核来源质量和闭环责任。', ''),
      summaryCard('finished', '已结束作业', summaryData.value.finishedCount ?? 0, '条', '观察当前作业闭环和结束回写稳定度。', 'azb-summary-card--success')
    ]
  }
  if (roleView.value === 'site-enterprise') {
    return [
      summaryCard('active', '进行中作业', summaryData.value.activeCount ?? 0, '条', '现场负责人优先盯住仍未结束的作业对象。', 'azb-summary-card--warning'),
      summaryCard('finished', '已结束作业', summaryData.value.finishedCount ?? 0, '条', '已回写结束留痕的作业记录。', 'azb-summary-card--success'),
      summaryCard('partial', '部分失效报备', summaryData.value.partialInvalidCount ?? 0, '条', '说明现场仍有部分人员证书需要补核。', 'azb-summary-card--danger'),
      summaryCard('allInvalid', '全部失效报备', summaryData.value.allInvalidCount ?? 0, '条', '需要优先回到现场补证书或重新组织作业。', '')
    ]
  }
  return [
    summaryCard('total', '报备记录', summaryData.value.totalCount ?? total.value, '条', '当前筛选条件下的高处作业报备总量。', ''),
    summaryCard('active', '进行中作业', summaryData.value.activeCount ?? 0, '条', '尚未做结束登记、仍需持续跟踪的作业记录。', 'azb-summary-card--warning'),
    summaryCard('invalid', '异常证书人次', summaryData.value.invalidWorkerCount ?? 0, '人', '优先压降证书异常带来的现场高危风险。', 'azb-summary-card--danger'),
    summaryCard('finished', '已结束作业', summaryData.value.finishedCount ?? 0, '条', '已完成结束登记并回写现场佐证的作业记录。', 'azb-summary-card--success')
  ]
})
const resolvedSummaryCards = computed(() => summaryCards.value.map((item, index) => ({
  ...item,
  note: resolveExplanationFirstText(item.note, resolvePortalExplanationItem(index))
})))

const focusQueues = computed(() => {
  if (roleView.value === 'bank') {
    return [
      focusQueue('active', '进行中高危作业', summaryData.value.activeCount ?? 0, '条', '先看当前区域是否存在较多在途作业对象。', '查看在途对象'),
      focusQueue('invalid', '异常证书人次', summaryData.value.invalidWorkerCount ?? 0, '人', '异常证书更适合联动信用和风险复核。', '查看异常证书'),
      focusQueue('imported', '导入与第三方来源', summaryData.value.importedCount ?? 0, '条', '导入来源越多，越需要确认责任归属与闭环稳定度。', '查看导入来源')
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      focusQueue('invalid', '异常证书作业', summaryData.value.invalidWorkerCount ?? 0, '人', '证书异常更容易影响事故预防服务与风险判断。', '查看异常作业'),
      focusQueue('active', '进行中作业', summaryData.value.activeCount ?? 0, '条', '在途作业越多，越值得跟踪后续闭环。', '查看在途对象'),
      focusQueue('imported', '导入与第三方来源', summaryData.value.importedCount ?? 0, '条', '第三方来源对象更值得复核凭证和结束回写完整度。', '查看来源链路')
    ]
  }
  if (roleView.value === 'site-enterprise') {
    return [
      focusQueue('active', '待结束作业', summaryData.value.activeCount ?? 0, '条', '先把仍在进行中的作业对象找出来。', '进入结束登记'),
      focusQueue('partial', '部分失效报备', summaryData.value.partialInvalidCount ?? 0, '条', '这类报备说明现场还有人员证书待补核。', '补证书信息'),
      focusQueue('allInvalid', '全部失效报备', summaryData.value.allInvalidCount ?? 0, '条', '需要立即回到现场核实人员与作业资格。', '现场复核')
    ]
  }
  return [
    focusQueue('active', '进行中高危作业', summaryData.value.activeCount ?? 0, '条', '应急侧先锁定仍在途的高危作业对象。', '进入在途治理'),
    focusQueue('invalid', '异常证书人次', summaryData.value.invalidWorkerCount ?? 0, '人', '证书异常说明现场准入链路存在明显风险。', '查看证书风险'),
    focusQueue('allInvalid', '全部失效报备', summaryData.value.allInvalidCount ?? 0, '条', '全部失效更适合优先升级核查。', '查看重点报备'),
    focusQueue('imported', '导入与第三方来源', summaryData.value.importedCount ?? 0, '条', '第三方来源对象更需要核对来源平台与结束责任。', '查看来源链路')
  ]
})
const resolvedFocusQueues = computed(() => focusQueues.value.map((item, index) => {
  const explanation = resolvePortalExplanationItem(index)
  return {
    ...item,
    desc: resolveExplanationFirstText(item.desc, explanation),
    actionText: resolveExplanationFirstActionText(item.actionText, explanation)
  }
}))

const fallbackPortalExplanations = computed(() => focusQueues.value.map(item => ({
  key: item.key,
  dimensionName: item.title,
  currentValue: item.count,
  targetValue: '-',
  summary: item.desc,
  evidenceModule: 'heightWorkReport',
  recommendModule: 'heightWorkReport',
  defaultQuery: buildHeightWorkExplanationQuery({ focusKey: item.key }),
  sourceLabel: '6.1 高处作业治理解释',
  sourceDescription: item.desc,
  actionText: item.actionText
})))

const portalExplanations = computed(() => {
  const aggregated = Array.isArray(summaryData.value.azbExplanation) ? summaryData.value.azbExplanation : []
  if (aggregated.length) {
    const focusKeys = new Set(focusQueues.value.map(item => item.key))
    const filtered = aggregated.filter(item => focusKeys.has(item.key))
    return filtered.length ? filtered : aggregated
  }
  return fallbackPortalExplanations.value
})

const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'azb',
  panelTitle: '6.1 高处作业治理解释',
  panelDescription: '高处作业解释统一按当前安责保门户来源条件输出。'
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

function resolveExplanationFirstActionText(fallback, explanation) {
  if (!explanation) {
    return fallback
  }
  return explanation.actionLabel || explanation.actionText || explanation.targetLabel || explanation.moduleLabel || fallback
}

function buildExplanationFirstTags(tags = []) {
  const extras = []
  const label = buildPortalExplanationLabel(leadingPortalExplanation.value)
  if (label) {
    extras.push({ label: `6.1主解释：${label}`, type: 'warning' })
  }
  if (portalExplanationSummary.value) {
    extras.push({ label: portalExplanationSummary.value, type: 'success' })
  }
  return [...extras, ...tags].slice(0, 4)
}

const activeFocus = computed(() => focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0] || null)
const visibleReportList = computed(() => prioritizeFocusRows(reportList.value, row => matchReportFocus(row, activeFocus.value?.key)))
const heightWorkWorkbenchFields = ['enterpriseId', 'regionCode', 'focusKey']

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: heightWorkWorkbenchFields,
  sourceLabel: '企业工作台',
  title: '当前高处作业台账沿用了工作台来源条件',
  description: '当前列表已按企业或行政区划锁定，适合直接继续跟进报备详情、结束登记和凭证核验。',
  fieldLabels: {
    enterpriseId: '企业',
    regionCode: '行政区划',
    focusKey: '焦点队列'
  },
  fieldFormatters: {
    enterpriseId: value => {
      const matched = enterpriseOptions.value.find(item => String(item.enterpriseId) === String(value))
      return matched ? matched.enterpriseName : value
    },
    regionCode: value => regionNameMap[value] || value,
    focusKey: value => heightWorkReportFocusLabel(value)
  }
}))

const focusTableHint = computed(() => {
  if (!activeFocus.value) {
    return '按当前筛选条件显示高处作业台账。'
  }
  return `当前焦点为“${activeFocus.value.title}”，已把对应对象优先排到表格前列。`
})

const selectedReportOverview = computed(() => {
  if (!currentReport.value) {
    return [
      { label: '当前焦点', value: activeFocus.value?.title || '-' },
      { label: '焦点数量', value: `${activeFocus.value?.count ?? 0}${activeFocus.value?.unit || ''}` },
      { label: '当前区域', value: regionNameMap[queryParams.value.regionCode] || queryParams.value.regionCode || '全部区域' },
      { label: '下一步', value: activeFocus.value?.actionText || '-' }
    ]
  }
  return [
    { label: '作业单位', value: currentReport.value.enterpriseName || '-' },
    { label: '风险状态', value: `${reportStatusLabel(currentReport.value.reportStatus)} / ${certStatusLabel(currentReport.value.certValidStatus)}` },
    { label: '作业时段', value: `${parseTime(currentReport.value.startTime, '{m}-{d} {h}:{i}') || '-'} 至 ${parseTime(currentReport.value.endTime, '{m}-{d} {h}:{i}') || '-'}` },
    { label: '人数/来源', value: `${currentReport.value.workerCount || 0} / ${sourceModeLabel(currentReport.value.sourceMode)}` }
  ]
})

const primaryReportAction = computed(() => {
  if (!currentReport.value) {
    return { label: activeFocus.value?.actionText || '查看详情', action: 'detail' }
  }
  if (roleView.value === 'bank') {
    return { label: '查看详情', action: 'detail' }
  }
  if (roleView.value === 'insurer') {
    return (String(currentReport.value.certValidStatus || '') !== '0' || isImportedSource(currentReport.value.sourceMode))
      ? { label: '查看凭证', action: 'voucher' }
      : { label: '查看详情', action: 'detail' }
  }
  if (roleView.value === 'site-enterprise') {
    if (String(currentReport.value.reportStatus || '') !== '1') {
      return { label: '进入结束登记', action: 'finish' }
    }
    if (String(currentReport.value.certValidStatus || '') !== '0') {
      return { label: '修改报备', action: 'edit' }
    }
    return { label: '查看凭证', action: 'voucher' }
  }
  if (String(currentReport.value.certValidStatus || '') === '2') {
    return { label: '查看详情', action: 'detail' }
  }
  if (String(currentReport.value.reportStatus || '') !== '1') {
    return { label: '进入结束登记', action: 'finish' }
  }
  return { label: '查看凭证', action: 'voucher' }
})

const currentReportActionSummary = computed(() => {
  if (!currentReport.value) {
    return activeFocus.value ? `当前已按“${activeFocus.value.title}”重排台账，建议先选中前列对象再处理。` : '当前暂无高危作业对象。'
  }
  if (roleView.value === 'bank') {
    return '当前角色只做只读协同，重点看进行中作业、异常证书和来源责任链路。'
  }
  if (roleView.value === 'insurer') {
    return '当前角色重点复核异常证书、凭证完整度和第三方来源责任。'
  }
  if (String(currentReport.value.reportStatus || '') !== '1') {
    return '该作业仍未结束，建议优先确认现场是否具备结束条件，再补照片和凭证闭环。'
  }
  if (String(currentReport.value.certValidStatus || '') !== '0') {
    return '该报备存在证书异常，建议先回看人员核验结果，再决定是否修改报备。'
  }
  if (isImportedSource(currentReport.value.sourceMode)) {
    return '该报备来自导入或第三方来源，建议优先核对来源责任链路和结束回写是否完整。'
  }
  return '该报备已具备基础闭环条件，建议继续查看详情和凭证，确认现场留痕是否完整。'
})
const resolvedCurrentReportActionSummary = computed(() => resolveExplanationFirstText(
  currentReportActionSummary.value,
  leadingPortalExplanation.value
))

const currentReportActionTags = computed(() => {
  const tags = []
  if (!currentReport.value) {
    if (activeFocus.value) {
      tags.push({ label: `当前焦点：${activeFocus.value.title}`, type: 'info' })
      tags.push({ label: `优先动作：${activeFocus.value.actionText}`, type: 'warning' })
    }
    return tags
  }
  if (String(currentReport.value.reportStatus || '') === '0') {
    tags.push({ label: '仍在进行中，建议先确认结束条件', type: 'warning' })
  }
  if (String(currentReport.value.certValidStatus || '') === '1') {
    tags.push({ label: '部分人员证书异常，建议优先补核人员信息', type: 'warning' })
  }
  if (String(currentReport.value.certValidStatus || '') === '2') {
    tags.push({ label: '全部失效，建议优先升级现场核查', type: 'danger' })
  }
  if (isImportedSource(currentReport.value.sourceMode)) {
    tags.push({ label: '导入或第三方来源，需核对责任链路', type: 'info' })
  }
  if (String(currentReport.value.reportStatus || '') === '1') {
    tags.push({ label: '已结束，建议继续核对照片与凭证', type: 'success' })
  }
  if (!tags.length) {
    tags.push({ label: '当前报备状态稳定，可继续查看详情与凭证', type: 'success' })
  }
  return tags
})
const resolvedCurrentReportActionTags = computed(() => buildExplanationFirstTags(currentReportActionTags.value))
const resolvedPrimaryReportAction = computed(() => ({
  ...primaryReportAction.value,
  label: resolveExplanationFirstActionText(primaryReportAction.value.label, leadingPortalExplanation.value)
}))

const workflowSteps = computed(() => {
  if (roleView.value === 'bank') {
    return [
      { label: '先看进行中与异常证书', desc: '先确认当前区域是否存在较多在途高危作业和异常证书对象。' },
      { label: '再看来源链路', desc: '通过导入和第三方来源判断风险对象是否来自重点协同链路。' },
      { label: '最后回到信用与报表协同', desc: '必要时再进入企业、信用和统计页做联合复核。' }
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      { label: '先看异常证书与在途作业', desc: '证书异常和进行中作业更值得优先纳入事故预防服务。' },
      { label: '再看凭证和来源模式', desc: '确认是否已经形成电子凭证以及来源链路是否稳定。' },
      { label: '最后回到详情复核', desc: '通过详情和作业人员核验结果决定是否继续协同。' }
    ]
  }
  if (roleView.value === 'site-enterprise') {
    return [
      { label: '先锁定待结束作业', desc: '优先把仍在进行中的作业对象找出来。' },
      { label: '再补证书与结束照片', desc: '部分失效和全部失效报备要先补证，再补现场结束佐证。' },
      { label: '最后回到凭证和结束回写', desc: '确认凭证可查、结束已回写后再退出现场链路。' }
    ]
  }
  return [
    { label: '先锁定在途高危作业', desc: '应急侧优先处理仍在进行中的高危作业对象。' },
    { label: '再看证书与来源异常', desc: '证书异常和第三方来源更值得进入重点核查。' },
    { label: '最后回到结束与凭证闭环', desc: '通过详情、凭证和结束登记压实现场闭环时效。' }
  ]
})
const resolvedWorkflowSteps = computed(() => workflowSteps.value.map((item, index) => ({
  ...item,
  desc: resolveExplanationFirstText(item.desc, resolvePortalExplanationItem(index))
})))

const hintTags = computed(() => {
  const tags = []
  if (Number(summaryData.value.activeCount || 0) > 0) {
    tags.push({ label: `当前有 ${summaryData.value.activeCount || 0} 条进行中作业需要持续跟踪`, type: 'warning' })
  }
  if (Number(summaryData.value.invalidWorkerCount || 0) > 0) {
    tags.push({ label: `异常证书人次 ${summaryData.value.invalidWorkerCount || 0}，建议优先查看核验结果`, type: 'danger' })
  }
  if (Number(summaryData.value.importedCount || 0) > 0) {
    tags.push({ label: `导入与第三方来源 ${summaryData.value.importedCount || 0} 条，建议复核来源责任链路`, type: 'info' })
  }
  if (Number(summaryData.value.allInvalidCount || 0) > 0 && roleView.value !== 'bank') {
    tags.push({ label: `全部失效报备 ${summaryData.value.allInvalidCount || 0} 条，适合优先升级核查`, type: 'danger' })
  }
  if (Number(summaryData.value.finishedCount || 0) > 0 && roleView.value === 'site-enterprise') {
    tags.push({ label: `已结束作业 ${summaryData.value.finishedCount || 0} 条，建议继续核对结束照片和凭证`, type: 'success' })
  }
  return tags
})
const resolvedHintTags = computed(() => buildExplanationFirstTags(hintTags.value))

const detailFocusText = computed(() => {
  if (roleView.value === 'bank') {
    return '重点看进行中作业、异常证书和导入来源，用于辅助区域信用与风险对象复核。'
  }
  if (roleView.value === 'insurer') {
    return '重点看异常证书、作业人员核验结果和电子凭证是否完整，用于判断事故预防协同重点。'
  }
  if (roleView.value === 'site-enterprise') {
    return '重点看哪些人员证书还需补录、作业是否可结束、结束照片和凭证是否已经补齐。'
  }
  return '重点看进行中状态、证书异常、来源模式和结束留痕是否完整，必要时优先升级核查。'
})

const detailHintTags = computed(() => buildAzbDetailHintTags(detail.value, voucher.value))
const resolvedDetailHintTags = computed(() => buildExplanationFirstTags(detailHintTags.value))
const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}：当前仅保留报备摘要、详情、凭证和导出`)
const readOnlyAlertDescription = computed(() => `${readOnlyRoleDescription.value} 当前页面聚焦在岗作业、证书风险和结束回写复核，不展示报备录入和结束登记动作。`)

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  syncCurrentReport()
}

function handlePortalExplanationAction(action) {
  if (action?.path === route.path) {
    applyHeightWorkWorkbenchQuery(action.query || {})
  }
  openPortalExplanationAction(router, action)
}

function handlePrimaryReportAction() {
  if (!currentReport.value) {
    return
  }
  switch (primaryReportAction.value.action) {
    case 'voucher':
      openVoucher(currentReport.value)
      break
    case 'finish':
      if (isReadOnlyRole.value) {
        proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}仅保留摘要、详情、凭证和导出，不能结束报备`)
        return
      }
      handleFinish(currentReport.value)
      break
    case 'edit':
      if (isReadOnlyRole.value) {
        proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}仅保留摘要、详情、凭证和导出，不能修改报备`)
        return
      }
      handleUpdate(currentReport.value)
      break
    default:
      openDetail(currentReport.value)
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
    title: roleTitle.value || '?????????',
    description: roleDescription.value || '?????????????????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: resolvedFocusQueues.value,
    selection: [...selectedReportOverview.value, { label: '??????', value: resolvedCurrentReportActionSummary.value }],
    workflow: resolvedWorkflowSteps.value,
    hints: [...resolvedCurrentReportActionTags.value, ...resolvedHintTags.value].slice(0, 6)
  })
})

}

function clearWorkbenchContext() {
  activeFocusKey.value = ''
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
    regionCode: undefined,
    enterpriseId: undefined,
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

applyWorkbenchRouteQuery(route.query, queryParams.value, heightWorkWorkbenchFields)
activeFocusKey.value = resolveHeightWorkReportFocusKey(route.query.focusKey)
initialize()

function resolveHeightWorkReportFocusKey(value) {
  const normalized = typeof value === 'string' ? value.trim() : ''
  return focusQueues.value.some(item => item.key === normalized) ? normalized : ''
}

function heightWorkReportFocusLabel(value) {
  return focusQueues.value.find(item => item.key === value)?.title || value || '-'
}
</script>

<style scoped lang="scss">
.azb-workbench-alert {
  margin-bottom: 16px;
}

.azb-workbench-alert__title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.azb-workbench-alert__desc {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 12px;
  margin-bottom: 12px;
  color: #47636d;
  line-height: 1.7;
  font-size: 13px;
}

.azb-workbench-alert__desc strong {
  color: #0b6b78;
}

.azb-page__tip {
  display: grid;
  gap: 6px;
  max-width: 460px;
  color: #526173;
  font-size: 13px;
  line-height: 1.7;
}

.azb-role-alert {
  margin-bottom: 18px;
}

.azb-summary-grid,
.azb-focus-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.azb-summary-grid {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.azb-summary-card,
.azb-focus-card {
  border: 1px solid #dbe7f3;
  border-radius: 16px;
  background: #fff;
}

.azb-summary-card {
  padding: 18px 20px;
}

.azb-summary-card__label,
.azb-source-item__label {
  color: #64748b;
  font-size: 13px;
}

.azb-summary-card__value {
  margin-top: 10px;
  color: #0f172a;
  font-size: 28px;
  font-weight: 700;
}

.azb-summary-card__unit {
  margin-left: 4px;
  color: #64748b;
  font-size: 13px;
}

.azb-summary-card__note {
  margin-top: 10px;
  color: #475569;
  line-height: 1.7;
  font-size: 13px;
}

.azb-summary-card--warning {
  background: linear-gradient(180deg, #fff 0%, #fff9ef 100%);
}

.azb-summary-card--danger {
  background: linear-gradient(180deg, #fff 0%, #fff2f2 100%);
}

.azb-summary-card--success {
  background: linear-gradient(180deg, #fff 0%, #f2fbf5 100%);
}

.azb-focus-card :deep(.el-card__header) {
  padding: 18px 20px 0;
  border-bottom: none;
}

.azb-focus-card :deep(.el-card__body) {
  padding: 18px 20px 20px;
}

.azb-card-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.azb-card-head__title {
  color: #0f172a;
  font-size: 18px;
  font-weight: 700;
}

.azb-card-head__desc {
  margin-top: 4px;
  color: #64748b;
  font-size: 13px;
  line-height: 1.7;
}

.azb-focus-list,
.azb-source-list,
.azb-pipeline-list {
  display: grid;
  gap: 12px;
}

.azb-focus-list--single {
  gap: 10px;
}

.azb-focus-queue,
.azb-source-item,
.azb-pipeline-item {
  border: 1px solid #dbe7f3;
  border-radius: 14px;
  background: #f8fbff;
}

.azb-focus-queue {
  width: 100%;
  padding: 14px 16px;
  display: flex;
  justify-content: space-between;
  gap: 14px;
  text-align: left;
  cursor: pointer;
}

.azb-focus-queue.is-active {
  border-color: #2b6cb0;
  background: #edf5ff;
}

.azb-focus-queue__main strong,
.azb-pipeline-item__body strong {
  color: #0f172a;
}

.azb-focus-queue__main p,
.azb-pipeline-item__body p {
  margin: 6px 0 0;
  color: #475569;
  font-size: 13px;
  line-height: 1.7;
}

.azb-focus-queue__side {
  display: grid;
  justify-items: end;
  align-content: center;
  gap: 4px;
}

.azb-focus-queue__count {
  color: #0f172a;
  font-weight: 700;
}

.azb-focus-queue__action {
  color: #2563eb;
  font-size: 12px;
}

.azb-source-item {
  padding: 14px 16px;
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.azb-source-item__value {
  color: #0f172a;
  font-weight: 600;
  text-align: right;
}

.azb-recommend-panel {
  margin-top: 16px;
  padding: 14px 16px;
  border: 1px solid #dbe7f3;
  border-radius: 10px;
  background: #f8fbff;
}

.azb-recommend-panel__title {
  color: #0f172a;
  font-size: 13px;
  font-weight: 600;
}

.azb-recommend-panel__summary {
  margin-top: 8px;
  color: #475569;
  font-size: 13px;
  line-height: 1.7;
}

.azb-focus-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 16px;
}

.azb-pipeline-item {
  display: grid;
  grid-template-columns: 42px minmax(0, 1fr);
  gap: 14px;
  padding: 16px;
}

.azb-pipeline-item__index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 34px;
  border-radius: 10px;
  background: #edf5ff;
  color: #2563eb;
  font-weight: 700;
}

.azb-tag-list {
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
  .azb-summary-grid,
  .azb-focus-grid {
    grid-template-columns: 1fr;
  }
}
</style>
