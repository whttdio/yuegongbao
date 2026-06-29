<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">人员主数据</p>
        <h1 class="ygb-page__title">人员主数据办理台账</h1>
        <p class="ygb-page__desc">
          统一维护从业人员归属、身份、工种、持证和参保状态，作为合同备案、实名制考勤、工资核验和漏保比对的人员主表。
          当前页面按“先看结构风险，再锁定当前人员，再进入录入或复核”的企业办理视角重组，减少企业管理员在多个模块之间反复跳转。
        </p>
      </div>
      <div class="ygb-table-tip">
        人员归属和区域编码与所属企业同步。当前页面聚焦 PC 办理链路，实名、人证比对、证书核验和社保碰撞均以当前人员信息为依据。
      </div>
    </section>

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

    <el-card class="ygb-focus-card ygb-link-card" shadow="never">
      <template #header>
        <div class="ygb-card-head">
          <div>
            <div class="ygb-card-head__title">关联办理入口</div>
            <div class="ygb-card-head__desc">围绕当前人员直接进入下一步业务模块，减少在合同、工资、设备和报备之间来回筛选。</div>
          </div>
        </div>
      </template>
      <div class="ygb-link-grid">
        <button
          v-for="link in workbenchLinks"
          :key="link.key"
          type="button"
          class="ygb-link-item"
          :disabled="link.disabled"
          @click="handleOpenWorkbenchLink(link)"
        >
          <div class="ygb-link-item__title">{{ link.title }}</div>
          <div class="ygb-link-item__desc">{{ link.desc }}</div>
          <div class="ygb-link-item__action">{{ link.disabled ? '请先选择人员' : '进入模块' }}</div>
        </button>
      </div>
    </el-card>

    <el-card class="ygb-focus-card ygb-link-card" shadow="never">
      <template #header>
        <div class="ygb-card-head">
          <div>
            <div class="ygb-card-head__title">人员子台账</div>
            <div class="ygb-card-head__desc">从人员主台账直接下钻特证、黑名单、培训监督、高危岗位、风险岗位和专家库，并带入当前筛选条件。</div>
          </div>
        </div>
      </template>
      <div class="ygb-link-grid">
        <button
          v-for="entry in submoduleEntries"
          :key="entry.key"
          type="button"
          class="ygb-link-item"
          @click="openSubmodule(entry)"
        >
          <div class="ygb-link-item__title">{{ entry.title }}</div>
          <div class="ygb-link-item__desc">{{ entry.desc }}</div>
          <div class="ygb-link-item__action">{{ entry.actionText }}</div>
        </button>
      </div>
    </el-card>

    <el-alert
      v-if="isReadOnlyRole"
      :title="`${readOnlyRoleLabel}：当前页面仅保留查看、详情和导出`"
      :description="readOnlyRoleDescription || '当前角色不展示新增、修改和删除动作。'"
      type="info"
      :closable="false"
      show-icon
      style="margin-bottom: 18px"
    />

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

    <el-card class="search-card ygb-search-card" shadow="never">
      <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch">
        <el-form-item label="区域">
          <el-select v-model="queryParams.regionCode" clearable style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属企业" prop="enterpriseId">
          <el-select
            v-model="queryParams.enterpriseId"
            placeholder="请选择所属企业"
            :clearable="!enterpriseFilterLocked"
            :disabled="enterpriseFilterLocked"
            filterable
            style="width: 220px"
          >
            <el-option
              v-for="item in enterpriseOptions"
              :key="item.enterpriseId"
              :label="item.enterpriseName"
              :value="item.enterpriseId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="姓名" prop="personName">
          <el-input
            v-model="queryParams.personName"
            placeholder="请输入姓名"
            clearable
            style="width: 180px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input
            v-model="queryParams.idCard"
            placeholder="请输入身份证号"
            clearable
            style="width: 220px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="用工类型" prop="workerType">
          <el-select v-model="queryParams.workerType" placeholder="请选择用工类型" clearable style="width: 150px">
            <el-option v-for="item in workerTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="持证状态" prop="certStatus">
          <el-select v-model="queryParams.certStatus" placeholder="请选择持证状态" clearable style="width: 150px">
            <el-option v-for="item in certStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="参保状态" prop="insuranceStatus">
          <el-select v-model="queryParams.insuranceStatus" placeholder="请选择参保状态" clearable style="width: 150px">
            <el-option v-for="item in insuranceStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="在岗状态" prop="employmentStatus">
          <el-select v-model="queryParams.employmentStatus" placeholder="请选择在岗状态" clearable style="width: 150px">
            <el-option v-for="item in employmentStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
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
          <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['ygb:person:add']">新增</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['ygb:person:edit']">修改</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['ygb:person:remove']">删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:person:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="personList"
        @selection-change="handleSelectionChange"
        @row-click="handleRowClick"
      >
        <el-table-column v-if="!isReadOnlyRole" type="selection" width="55" align="center" />
        <el-table-column label="人员ID" prop="personId" width="90" />
        <el-table-column label="姓名" prop="personName" width="120" />
        <el-table-column label="身份证号" min-width="180">
          <template #default="scope">
            <span>{{ maskIdCard(scope.row.idCard) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="所属企业" prop="enterpriseName" min-width="220" />
        <el-table-column label="区域" width="140">
          <template #default="scope">
            <span>{{ formatRegionName(scope.row.regionCode) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="用工类型" prop="workerType" width="110">
          <template #default="scope">
            <dict-tag :options="workerTypeOptions" :value="scope.row.workerType" />
          </template>
        </el-table-column>
        <el-table-column label="工种" prop="jobType" width="140" />
        <el-table-column label="持证状态" prop="certStatus" width="110">
          <template #default="scope">
            <dict-tag :options="certStatusOptions" :value="scope.row.certStatus" />
          </template>
        </el-table-column>
        <el-table-column label="参保状态" prop="insuranceStatus" width="110">
          <template #default="scope">
            <dict-tag :options="insuranceStatusOptions" :value="scope.row.insuranceStatus" />
          </template>
        </el-table-column>
        <el-table-column label="在岗状态" prop="employmentStatus" width="110">
          <template #default="scope">
            <dict-tag :options="employmentStatusOptions" :value="scope.row.employmentStatus" />
          </template>
        </el-table-column>
        <el-table-column label="联系电话" prop="mobile" width="130" />
        <el-table-column label="入场日期" width="120">
          <template #default="scope">
            <span>{{ parseTime(scope.row.entryDate, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" align="center" :width="isReadOnlyRole ? 90 : 220" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
            <el-button v-if="!isReadOnlyRole" link type="primary" icon="Edit" @click.stop="handleUpdate(scope.row)" v-hasPermi="['ygb:person:edit']">修改</el-button>
            <el-button v-if="!isReadOnlyRole" link type="danger" icon="Delete" @click.stop="handleDelete(scope.row)" v-hasPermi="['ygb:person:remove']">删除</el-button>
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

    <el-dialog :title="title" v-model="open" width="820px" append-to-body>
      <el-form ref="personRef" :model="form" :rules="rules" label-width="110px">
        <div class="ygb-panel-grid">
          <el-form-item label="所属企业" prop="enterpriseId">
            <el-select
              v-model="form.enterpriseId"
              placeholder="请选择所属企业"
              :clearable="!enterpriseFilterLocked"
              :disabled="enterpriseFilterLocked"
              filterable
              style="width: 100%"
              @change="handleEnterpriseChange"
            >
              <el-option
                v-for="item in enterpriseOptions"
                :key="item.enterpriseId"
                :label="item.enterpriseName"
                :value="item.enterpriseId"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="区域编码" prop="regionCode">
            <el-input v-model="form.regionCode" placeholder="随所属企业带出" />
          </el-form-item>
          <el-form-item label="姓名" prop="personName">
            <el-input v-model="form.personName" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="身份证号" prop="idCard">
            <el-input v-model="form.idCard" placeholder="请输入身份证号" />
          </el-form-item>
          <el-form-item label="联系电话" prop="mobile">
            <el-input v-model="form.mobile" placeholder="请输入联系电话" />
          </el-form-item>
          <el-form-item label="用工类型" prop="workerType">
            <el-select v-model="form.workerType" placeholder="请选择用工类型">
              <el-option v-for="item in workerTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="工种" prop="jobType">
            <el-input v-model="form.jobType" placeholder="请输入工种" />
          </el-form-item>
          <el-form-item label="持证状态" prop="certStatus">
            <el-radio-group v-model="form.certStatus">
              <el-radio v-for="item in certStatusOptions" :key="item.value" :label="item.value">{{ item.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="参保状态" prop="insuranceStatus">
            <el-radio-group v-model="form.insuranceStatus">
              <el-radio v-for="item in insuranceStatusOptions" :key="item.value" :label="item.value">{{ item.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="在岗状态" prop="employmentStatus">
            <el-radio-group v-model="form.employmentStatus">
              <el-radio v-for="item in employmentStatusOptions" :key="item.value" :label="item.value">{{ item.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="入场日期" prop="entryDate">
            <el-date-picker
              v-model="form.entryDate"
              type="date"
              placeholder="请选择入场日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="离场日期" prop="leaveDate">
            <el-date-picker
              v-model="form.leaveDate"
              type="date"
              placeholder="请选择离场日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </div>
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

    <page-detail-dialog v-model="detailOpen" title="人员详情" width="720px">
      <template v-if="detailPerson">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="姓名">{{ detailPerson.personName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="所属企业">{{ detailPerson.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="区域">{{ formatRegionName(detailPerson.regionCode) }}</el-descriptions-item>
          <el-descriptions-item label="用工类型">{{ optionLabel(workerTypeOptions, detailPerson.workerType) }}</el-descriptions-item>
          <el-descriptions-item label="持证状态">{{ optionLabel(certStatusOptions, detailPerson.certStatus) }}</el-descriptions-item>
          <el-descriptions-item label="参保状态">{{ optionLabel(insuranceStatusOptions, detailPerson.insuranceStatus) }}</el-descriptions-item>
          <el-descriptions-item label="在岗状态">{{ optionLabel(employmentStatusOptions, detailPerson.employmentStatus) }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ detailPerson.mobile || '-' }}</el-descriptions-item>
          <el-descriptions-item label="身份证号" :span="2">{{ detailPerson.idCard || '-' }}</el-descriptions-item>
          <el-descriptions-item label="工种">{{ detailPerson.jobType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="入场日期">{{ parseTime(detailPerson.entryDate, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="离场日期">{{ parseTime(detailPerson.leaveDate, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detailPerson.remark || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="ygb-detail-block">
          <h3>办理提示</h3>
          <div class="ygb-tag-list">
            <el-tag v-for="item in resolvedDetailHintTags" :key="item.label" :type="item.type" effect="plain">{{ item.label }}</el-tag>
          </div>
        </div>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbPerson">
  import { computed, getCurrentInstance, watchEffect } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import { useRoleViewMode } from '@/utils/roleView'
import { decoratePortalExplanationItems, openPortalExplanationAction, resolvePortalExplanationSummary } from '@/utils/portalExplanation'
import { applyLockedEnterpriseQuery } from '@/utils/enterpriseScope'
import { applyWorkbenchRouteQuery, buildPersonWorkbenchLinks, buildWorkbenchContext, openWorkbenchLink, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'
import {
  certStatusOptions,
  employmentStatusOptions,
  formatRegionName,
  insuranceStatusOptions,
  maskIdCard,
  optionLabel,
  usePersonPage,
  workerTypeOptions
} from '@/views/person/usePersonPage'

  const { proxy } = getCurrentInstance()
  const route = useRoute()
  const router = useRouter()
  const { setPageGuide } = useWorkbenchAssist()
  const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const personWorkbenchFields = ['enterpriseId', 'regionCode', 'workerType', 'certStatus', 'insuranceStatus', 'employmentStatus']
const personInitialQuery = {}
applyWorkbenchRouteQuery(route.query, personInitialQuery, personWorkbenchFields)

const {
  showSearch,
  loading,
  total,
  open,
  detailOpen,
  title,
  personList,
  enterpriseOptions,
  enterpriseFilterLocked,
  currentPerson,
  detailPerson,
  summaryData,
  single,
  multiple,
  queryParams,
  form,
  rules,
  regionOptions,
  getList,
  cancel,
  handleQuery,
  handleSelectionChange,
  handleRowClick,
  handleAdd,
  handleUpdate,
  openDetail,
  submitForm,
  handleDelete,
  handleExport,
  handleEnterpriseChange,
  scopedQueryParams
} = usePersonPage({
  exportFilePrefix: 'ygb_person',
  initialQueryParams: personInitialQuery,
  canMutate: () => !isReadOnlyRole.value,
  onBlockedAction: blockReadOnlyAction
})

function buildPersonExplanationQuery(extraQuery = {}) {
  const scoped = scopedQueryParams()
  return {
    enterpriseId: scoped.enterpriseId,
    regionCode: scoped.regionCode,
    ...extraQuery
  }
}

const fallbackPortalExplanations = computed(() => ([
  {
    key: 'onPost',
    dimensionName: '在岗人员',
    currentValue: summaryData.value.onPostCount != null ? summaryData.value.onPostCount : 0,
    targetValue: '持续完整',
    summary: '先固定在岗人员范围，再进入合同、考勤和工资办理，避免后续链路人员范围漂移。',
    evidenceModule: 'person',
    recommendModule: 'person',
    defaultQuery: buildPersonExplanationQuery({ employmentStatus: '0' }),
    sourceLabel: '530.1 人员主数据解释',
    sourceDescription: '从人员主数据继续核对在岗承接范围。'
  },
  {
    key: 'certRisk',
    dimensionName: '证书风险',
    currentValue: summaryData.value.certRiskCount != null ? summaryData.value.certRiskCount : 0,
    targetValue: '0',
    summary: '证书临期或过期会直接阻断后续授权、报备和重点岗位办理，应优先回看风险人员。',
    evidenceModule: 'person',
    recommendModule: 'person',
    defaultQuery: buildPersonExplanationQuery({ certStatus: '3' }),
    sourceLabel: '530.1 人员主数据解释',
    sourceDescription: '从人员主数据继续核查证书风险对象。'
  },
  {
    key: 'uninsured',
    dimensionName: '未参保人员',
    currentValue: summaryData.value.uninsuredCount != null ? summaryData.value.uninsuredCount : 0,
    targetValue: '0',
    summary: '未参保人员会影响工资、工伤和扩面减损链路，应先锁定对象再进入后续办理。',
    evidenceModule: 'person',
    recommendModule: 'person',
    defaultQuery: buildPersonExplanationQuery({ insuranceStatus: '0' }),
    sourceLabel: '530.1 人员主数据解释',
    sourceDescription: '从人员主数据继续核查未参保对象。'
  },
  {
    key: 'dispatch',
    dimensionName: '派遣结构',
    currentValue: summaryData.value.dispatchWorkerCount != null ? summaryData.value.dispatchWorkerCount : 0,
    targetValue: '持续核实',
    summary: '派遣工结构决定合同备案和工资监管承接方式，应先固定人员类型口径。',
    evidenceModule: 'person',
    recommendModule: 'person',
    defaultQuery: buildPersonExplanationQuery({ workerType: '1' }),
    sourceLabel: '530.1 人员主数据解释',
    sourceDescription: '从人员主数据继续核对派遣工结构。'
  }
]))

const portalExplanations = computed(() => {
  const aggregated = Array.isArray(summaryData.value.ygbExplanation) ? summaryData.value.ygbExplanation : []
  return aggregated.length ? aggregated : fallbackPortalExplanations.value
})

const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'ygb',
  panelTitle: '530.1 办理链说明',
  panelDescription: '人员主数据页面按办理链解释在岗、证书风险、未参保和派遣结构。'
}))

function handlePortalExplanationAction(action) {
  openPortalExplanationAction(router, action)
}

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

const summaryCards = computed(() => ([
  {
    key: 'total',
    label: '人员台账总量',
    value: summaryData.value.totalCount != null ? summaryData.value.totalCount : total.value,
    unit: '人',
    note: '当前筛选条件下可承接合同、考勤、工资和监管联动的人员主数据总量。',
    cardClass: ''
  },
  {
    key: 'onPost',
    label: '在岗人员',
    value: summaryData.value.onPostCount != null ? summaryData.value.onPostCount : 0,
    unit: '人',
    note: '当前筛选条件下仍在岗、可继续参与考勤汇聚和工资发放的人员数量。',
    cardClass: 'ygb-summary-card--success'
  },
  {
    key: 'certRisk',
    label: '证书临期/过期',
    value: summaryData.value.certRiskCount != null ? summaryData.value.certRiskCount : 0,
    unit: '人',
    note: '进入高风险工种办理前应优先补齐证书状态，避免后续报备和授权卡住。',
    cardClass: 'ygb-summary-card--warning'
  },
  {
    key: 'uninsured',
    label: '未参保人员',
    value: summaryData.value.uninsuredCount != null ? summaryData.value.uninsuredCount : 0,
    unit: '人',
    note: '进入工资和工伤保障链路前应优先核对参保状态，避免形成漏保风险。',
    cardClass: 'ygb-summary-card--primary'
  }
]))

const resolvedSummaryCards = computed(() => summaryCards.value.map((item, index) => ({
  ...item,
  note: resolveExplanationFirstText(item.note, resolvePortalExplanationItem(index))
})))

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: personWorkbenchFields,
  sourceLabel: '对象工作台',
  title: '当前人员台账页沿用了工作台来源条件',
  description: '列表已按工作台带入的所属企业与行政区划锁定，可直接继续处理该对象范围内的人员台账。',
  fieldLabels: {
    enterpriseId: '所属企业',
    regionCode: '行政区划',
    workerType: '用工类型',
    certStatus: '持证状态',
    insuranceStatus: '参保状态',
    employmentStatus: '在岗状态'
  },
  fieldFormatters: {
    enterpriseId: value => {
      const matched = enterpriseOptions.value.find(item => String(item.enterpriseId) === String(value))
      return matched ? matched.enterpriseName : value
    },
    regionCode: value => formatRegionName(value, value),
    workerType: value => optionLabel(workerTypeOptions, value, value),
    certStatus: value => optionLabel(certStatusOptions, value, value),
    insuranceStatus: value => optionLabel(insuranceStatusOptions, value, value),
    employmentStatus: value => optionLabel(employmentStatusOptions, value, value)
  }
}))

const focusItems = computed(() => ([
  { label: '当前区域', value: formatRegionName(queryParams.value.regionCode) },
  { label: '覆盖企业', value: `${summaryData.value.enterpriseCount != null ? summaryData.value.enterpriseCount : 0} 家` },
  { label: '派遣工人数', value: `${summaryData.value.dispatchWorkerCount != null ? summaryData.value.dispatchWorkerCount : 0} 人` },
  { label: '外包工人数', value: `${summaryData.value.outsourcingWorkerCount != null ? summaryData.value.outsourcingWorkerCount : 0} 人` }
]))

const resolvedFocusItems = computed(() => focusItems.value.map((item, index) => ({
  ...item,
  tip: resolveExplanationFirstText('', resolvePortalExplanationItem(index))
})))

const selectedPersonOverview = computed(() => {
  if (!currentPerson.value) {
    return [
      { label: '所属企业', value: '-' },
      { label: '持证状态', value: '-' },
      { label: '参保状态', value: '-' },
      { label: '在岗/工种', value: '-' }
    ]
  }
  return [
    { label: '所属企业', value: currentPerson.value.enterpriseName || '-' },
    { label: '持证状态', value: optionLabel(certStatusOptions, currentPerson.value.certStatus) },
    { label: '参保状态', value: optionLabel(insuranceStatusOptions, currentPerson.value.insuranceStatus) },
    {
      label: '在岗/工种',
      value: `${optionLabel(employmentStatusOptions, currentPerson.value.employmentStatus)} / ${currentPerson.value.jobType || '-'}`
    }
  ]
})

const businessSteps = computed(() => ([
  {
    label: '先补人员归属',
    desc: '先确认所属企业和区域，让人员主体能够正确挂到企业办理链。'
  },
  {
    label: '核对身份与证书',
    desc: '补齐身份证、联系方式、工种和持证状态，减少合同备案和高风险岗位校验返工。'
  },
  {
    label: '承接考勤与工资',
    desc: '人员主数据稳定后，再进入实名制考勤、月度汇聚和工资核验。'
  },
  {
    label: '联动参保与预警',
    desc: '最后承接社保漏保、税务碰撞和预警闭环，形成完整人员办理台账。'
  }
]))

const personHintTags = computed(() => buildHintTags(currentPerson.value))
const detailHintTags = computed(() => buildHintTags(detailPerson.value))
const resolvedBusinessSteps = computed(() => businessSteps.value.map((item, index) => {
  const explanation = resolvePortalExplanationItem(index)
  return {
    ...item,
    label: explanation?.dimensionName || explanation?.moduleLabel || explanation?.moduleCode || item.label,
    desc: resolveExplanationFirstText(item.desc, explanation)
  }
}))
const resolvedPersonHintTags = computed(() => {
  const tags = [...personHintTags.value]
  const extras = []
  const label = buildPortalExplanationLabel(leadingPortalExplanation.value)
  if (label) {
    extras.push({ label: `530.1主解释：${label}`, type: 'warning' })
  }
  if (portalExplanationSummary.value) {
    extras.push({ label: portalExplanationSummary.value, type: 'success' })
  }
  return [...extras, ...tags].slice(0, 4)
})
  const resolvedDetailHintTags = computed(() => {
  const tags = [...detailHintTags.value]
  const extras = []
  const label = buildPortalExplanationLabel(leadingPortalExplanation.value)
  if (label) {
    extras.push({ label: `530.1主解释：${label}`, type: 'warning' })
  }
  if (portalExplanationSummary.value) {
    extras.push({ label: portalExplanationSummary.value, type: 'success' })
  }
    return [...extras, ...tags].slice(0, 4)
  })
  watchEffect(() => {
    setPageGuide({
      title: '人员主数据办理台账',
      description: '统一查看当前页的承接焦点、当前选中、办理路径与办理提示。',
      portalExplanation: portalExplanationItems.value,
      focus: resolvedFocusItems.value,
      selection: selectedPersonOverview.value,
      workflow: resolvedBusinessSteps.value,
      hints: resolvedPersonHintTags.value
    })
  })
  const workbenchLinks = computed(() => buildPersonWorkbenchLinks({
  proxy,
  person: currentPerson.value,
  portalCode: 'ygb'
}))

const submoduleEntries = computed(() => ([
  {
    key: 'certificate',
    title: '特证管理',
    desc: '维护证书台账、复审节点和证件闭环。',
    actionText: '进入特证台账',
    path: '/personnel-management/certificate',
    query: buildPersonSubmoduleQuery()
  },
  {
    key: 'blacklist',
    title: '黑名单',
    desc: '承接违规对象、限制入场和协同处置记录。',
    actionText: '进入黑名单台账',
    path: '/personnel-management/blacklist',
    query: buildPersonSubmoduleQuery()
  },
  {
    key: 'training',
    title: '培训监督',
    desc: '维护培训主题、学时留痕和抽查整改状态。',
    actionText: '进入培训台账',
    path: '/personnel-management/training',
    query: buildPersonSubmoduleQuery()
  },
  {
    key: 'highRiskPost',
    title: '高危岗位库',
    desc: '下钻查看高危岗位对象、风险来源和审查状态。',
    actionText: '进入高危岗位库',
    path: '/personnel-management/highRiskPost',
    query: buildPersonSubmoduleQuery()
  },
  {
    key: 'riskPost',
    title: '风险岗位库',
    desc: '维护一般风险岗位、适用企业和风险级别。',
    actionText: '进入风险岗位库',
    path: '/personnel-management/riskPost',
    query: buildPersonSubmoduleQuery()
  },
  {
    key: 'expert',
    title: '专家库',
    desc: '沉淀专家信息、专业方向和参与记录。',
    actionText: '进入专家库',
    path: '/personnel-management/expert',
    query: buildPersonSubmoduleQuery()
  }
]))

function buildPersonSubmoduleQuery(extraQuery = {}) {
  const scoped = scopedQueryParams()
  return Object.fromEntries(Object.entries({
    regionCode: scoped.regionCode,
    enterpriseId: currentPerson.value?.enterpriseId || scoped.enterpriseId,
    personName: currentPerson.value?.personName || scoped.personName,
    ...extraQuery
  }).filter(([, value]) => value !== undefined && value !== null && value !== ''))
}

function openSubmodule(entry) {
  proxy?.$tab?.openPage?.(entry.title, entry.path, entry.query)
}

function resetQuery() {
  proxy.resetForm('queryRef')
  Object.assign(queryParams.value, applyLockedEnterpriseQuery({
    pageNum: 1,
    pageSize: 10,
    regionCode: undefined,
    enterpriseId: undefined,
    personName: undefined,
    idCard: undefined,
    workerType: undefined,
    certStatus: undefined,
    insuranceStatus: undefined,
    employmentStatus: undefined
  }))
  applyWorkbenchRouteQuery(route.query, queryParams.value, personWorkbenchFields)
  getList()
}

function clearWorkbenchContext() {
  Object.assign(queryParams.value, applyLockedEnterpriseQuery({
    pageNum: 1,
    enterpriseId: undefined,
    regionCode: undefined,
    workerType: undefined,
    certStatus: undefined,
    insuranceStatus: undefined,
    employmentStatus: undefined
  }))
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, personWorkbenchFields)
  })
  getList()
}

function blockReadOnlyAction(actionLabel) {
  if (!isReadOnlyRole.value) {
    return false
  }
  proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}当前仅保留查看、详情和导出，不能${actionLabel}`)
  return true
}

function handleOpenWorkbenchLink(link) {
  if (openWorkbenchLink(proxy, link)) {
    return
  }
  proxy.$modal.msgWarning('请先选择人员，再进入关联办理模块')
}

function buildHintTags(person) {
  if (!person) {
    return [{ label: '未选中人员，可先在列表中选择办理对象', type: 'info' }]
  }
  const tags = []
  if (person.enterpriseId == null) {
    tags.push({ label: '缺少所属企业，无法承接合同和考勤链路', type: 'danger' })
  }
  if (person.insuranceStatus === '0') {
    tags.push({ label: '未参保，建议先核对工伤保障状态', type: 'warning' })
  }
  if (person.certStatus === '2' || person.certStatus === '3') {
    tags.push({ label: '证书临期或过期，进入特殊作业和授权前需补齐', type: 'warning' })
  }
  if (person.employmentStatus === '1') {
    tags.push({ label: '当前为离岗状态，不建议继续纳入考勤和工资链路', type: 'info' })
  }
  if (!person.mobile) {
    tags.push({ label: '联系方式缺失，影响企业侧办理联络', type: 'warning' })
  }
  if (person.workerType === '1') {
    tags.push({ label: '派遣工，优先关联派遣协议和合同备案', type: 'success' })
  }
  if (!tags.length) {
    tags.push({ label: '当前信息完整，可直接承接后续业务办理', type: 'success' })
  }
  return tags
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
  gap: 8px;
  margin-bottom: 10px;
  color: #5f6f80;
  line-height: 1.7;
}

.ygb-workbench-alert__desc strong {
  color: #15304b;
}

.ygb-summary-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.ygb-summary-card {
  padding: 18px 20px;
  border: 1px solid #dbe5f0;
  border-radius: 16px;
  background: #fff;
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
  background: linear-gradient(180deg, #ffffff 0%, #f2f7fd 100%);
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

.ygb-tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.ygb-link-card {
  margin-bottom: 16px;
}

.ygb-link-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.ygb-link-item {
  padding: 16px;
  border: 1px solid #dde7f1;
  border-radius: 14px;
  background: #f8fbfd;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
}

.ygb-link-item:hover:not(:disabled) {
  border-color: #9db8d6;
  box-shadow: 0 8px 20px rgba(15, 94, 168, 0.08);
  transform: translateY(-1px);
}

.ygb-link-item:disabled {
  cursor: not-allowed;
  opacity: 0.62;
}

.ygb-link-item__title {
  color: #13243a;
  font-size: 14px;
  font-weight: 700;
}

.ygb-link-item__desc {
  margin-top: 8px;
  color: #5f6f80;
  font-size: 13px;
  line-height: 1.7;
}

.ygb-link-item__action {
  margin-top: 12px;
  color: #0f5ea8;
  font-size: 12px;
  font-weight: 600;
}

.ygb-detail-block {
  margin-top: 20px;
}

.ygb-detail-block h3 {
  margin: 0 0 12px;
  color: #1f2d3d;
  font-size: 16px;
}

@media (max-width: 1200px) {
  .ygb-summary-grid,
  .ygb-link-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .ygb-summary-grid,
  .ygb-link-grid {
    grid-template-columns: 1fr;
  }
}
</style>
