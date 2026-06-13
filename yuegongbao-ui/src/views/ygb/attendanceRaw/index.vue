<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">考勤采集主链路</p>
        <h1 class="ygb-page__title">考勤上报办理台账</h1>
        <p class="ygb-page__desc">
          统一承接闸机、人脸、APP、人工补录和第三方来源的原始考勤记录，面向企业管理员和经办优先处理未归集、异常考勤、校验失败和人工补录对象，
          为后续月度归集、工资核验和发薪批次提供可直接承接的原始底账。
        </p>
      </div>
      <div class="ygb-table-tip">
        当前版本支持原始考勤录入、校验、补录和归集标记，后续可继续对接设备实时回传、异常联动预警和自动归集任务。
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

    <div class="ygb-analysis-grid">
      <div v-for="item in attendanceOverviewCards" :key="item.key" class="ygb-summary-card ygb-analysis-card" :class="item.cardClass">
        <div class="ygb-summary-card__label">{{ item.label }}</div>
        <div class="ygb-summary-card__value">
          {{ item.value }}
          <span class="ygb-summary-card__unit">{{ item.unit }}</span>
        </div>
        <div class="ygb-summary-card__note">{{ item.note }}</div>
      </div>
    </div>

    <div class="ygb-analysis-grid">
      <el-card class="ygb-focus-card ygb-analysis-panel" shadow="never">
        <template #header>
          <div class="ygb-card-head">
            <div class="ygb-card-head__title">区域异常概览</div>
            <div class="ygb-card-head__desc">按当前筛选条件查看原始考勤异常最集中的区域，点击后直接回写区域筛选。</div>
          </div>
        </template>
        <div v-if="regionOverviewItems.length" class="ygb-analysis-list">
          <button
            v-for="item in regionOverviewItems"
            :key="item.dimensionKey"
            type="button"
            class="ygb-analysis-item"
            @click="applyOverviewFilter('regionCode', item.dimensionKey)"
          >
            <span class="ygb-analysis-item__label">{{ item.dimensionLabel }}</span>
            <span class="ygb-analysis-item__value">{{ item.dimensionCount }}</span>
          </button>
        </div>
        <el-empty v-else description="暂无区域统计" :image-size="56" />
      </el-card>

      <el-card class="ygb-focus-card ygb-analysis-panel" shadow="never">
        <template #header>
          <div class="ygb-card-head">
            <div class="ygb-card-head__title">来源结构</div>
            <div class="ygb-card-head__desc">按采集来源快速识别闸机、人脸、APP、补录和第三方来源分布。</div>
          </div>
        </template>
        <div v-if="sourceOverviewItems.length" class="ygb-analysis-list">
          <button
            v-for="item in sourceOverviewItems"
            :key="item.dimensionKey"
            type="button"
            class="ygb-analysis-item"
            @click="applyOverviewFilter('sourceType', item.dimensionKey)"
          >
            <span class="ygb-analysis-item__label">{{ item.dimensionLabel }}</span>
            <span class="ygb-analysis-item__value">{{ item.dimensionCount }}</span>
          </button>
        </div>
        <el-empty v-else description="暂无来源统计" :image-size="56" />
      </el-card>
    </div>


    <el-card class="search-card ygb-search-card" shadow="never">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
        <el-form-item label="考勤编号" prop="attendanceNo">
          <el-input
            v-model="queryParams.attendanceNo"
            placeholder="请输入考勤编号"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="关联合同" prop="contractId">
          <el-select v-model="queryParams.contractId" placeholder="请选择关联合同" clearable filterable style="width: 240px">
            <el-option
              v-for="item in contractOptions"
              :key="item.contractId"
              :label="formatContractOptionLabel(item)"
              :value="item.contractId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="考勤状态" prop="attendanceStatus">
          <el-select v-model="queryParams.attendanceStatus" placeholder="请选择考勤状态" clearable style="width: 150px">
            <el-option v-for="item in attendanceStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="归集状态" prop="collectStatus">
          <el-select v-model="queryParams.collectStatus" placeholder="请选择归集状态" clearable style="width: 150px">
            <el-option v-for="item in collectStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="上报来源" prop="sourceType">
          <el-select v-model="queryParams.sourceType" placeholder="请选择上报来源" clearable style="width: 150px">
            <el-option v-for="item in sourceTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="校验状态" prop="attCheck">
          <el-select v-model="queryParams.attCheck" placeholder="请选择校验状态" clearable style="width: 140px">
            <el-option v-for="item in attCheckOptions" :key="item.value" :label="item.label" :value="item.value" />
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
            v-hasPermi="['ygb:attendanceRaw:add']"
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
            v-hasPermi="['ygb:attendanceRaw:edit']"
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
            v-hasPermi="['ygb:attendanceRaw:remove']"
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
            v-hasPermi="['ygb:attendanceRaw:export']"
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
            <div class="ygb-card-head__title">原始考勤台账</div>
            <div class="ygb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="visibleAttendanceRawList" @selection-change="handleSelectionChange" @row-click="handleRowClick">
        <el-table-column v-if="!isReadOnlyRole" type="selection" width="55" align="center" />
        <el-table-column label="考勤ID" align="center" prop="attendanceId" width="90" />
        <el-table-column label="考勤编号" align="center" prop="attendanceNo" width="180" />
        <el-table-column label="劳动者" align="center" prop="personName" width="120" />
        <el-table-column label="合同编号" align="center" prop="contractNo" width="180" />
        <el-table-column label="派遣单位" align="center" prop="dispatchEnterpriseName" min-width="220" />
        <el-table-column label="考勤日期" align="center" prop="attendanceDate" width="120">
          <template #default="scope">
            <span>{{ parseTime(scope.row.attendanceDate, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="班次" align="center" prop="shiftName" width="120" />
        <el-table-column label="上班打卡" align="center" prop="clockInTime" width="170">
          <template #default="scope">
            <span>{{ parseTime(scope.row.clockInTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="下班打卡" align="center" prop="clockOutTime" width="170">
          <template #default="scope">
            <span>{{ parseTime(scope.row.clockOutTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="出勤工时" align="center" prop="attendanceHours" width="100">
          <template #default="scope">
            <span>{{ formatDecimal(scope.row.attendanceHours) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="加班工时" align="center" prop="overtimeHours" width="100">
          <template #default="scope">
            <span>{{ formatDecimal(scope.row.overtimeHours) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="考勤状态" align="center" prop="attendanceStatus" width="100">
          <template #default="scope">
            <dict-tag :options="attendanceStatusOptions" :value="scope.row.attendanceStatus" />
          </template>
        </el-table-column>
        <el-table-column label="上报来源" align="center" prop="sourceType" width="100">
          <template #default="scope">
            <dict-tag :options="sourceTypeOptions" :value="scope.row.sourceType" />
          </template>
        </el-table-column>
        <el-table-column label="归集状态" align="center" prop="collectStatus" width="100">
          <template #default="scope">
            <dict-tag :options="collectStatusOptions" :value="scope.row.collectStatus" />
          </template>
        </el-table-column>
        <el-table-column label="工资校验" align="center" prop="attCheck" width="100">
          <template #default="scope">
            <dict-tag :options="attCheckOptions" :value="scope.row.attCheck" />
          </template>
        </el-table-column>
        <el-table-column label="设备编码" align="center" prop="deviceCode" width="150" />
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" align="center" :width="isReadOnlyRole ? 90 : 240" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
            <template v-if="!isReadOnlyRole">
              <el-button link type="primary" icon="Edit" @click.stop="handleUpdate(scope.row)" v-hasPermi="['ygb:attendanceRaw:edit']">修改</el-button>
              <el-button link type="danger" icon="Delete" @click.stop="handleDelete(scope.row)" v-hasPermi="['ygb:attendanceRaw:remove']">删除</el-button>
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
      <el-form ref="attendanceRawRef" :model="form" :rules="rules" label-width="110px">
        <div class="ygb-panel-grid">
          <el-form-item label="考勤编号" prop="attendanceNo">
            <el-input v-model="form.attendanceNo" placeholder="请输入考勤编号" />
          </el-form-item>
          <el-form-item label="关联合同" prop="contractId">
            <el-select v-model="form.contractId" placeholder="请选择关联合同" filterable>
              <el-option
                v-for="item in contractOptions"
                :key="item.contractId"
                :label="formatContractOptionLabel(item)"
                :value="item.contractId"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="考勤日期" prop="attendanceDate">
            <el-date-picker v-model="form.attendanceDate" type="date" placeholder="请选择考勤日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 100%" />
          </el-form-item>
          <el-form-item label="班次" prop="shiftName">
            <el-input v-model="form.shiftName" placeholder="请输入班次名称" />
          </el-form-item>
          <el-form-item label="上班打卡" prop="clockInTime">
            <el-date-picker v-model="form.clockInTime" type="datetime" placeholder="请选择上班打卡时间" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
          </el-form-item>
          <el-form-item label="下班打卡" prop="clockOutTime">
            <el-date-picker v-model="form.clockOutTime" type="datetime" placeholder="请选择下班打卡时间" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
          </el-form-item>
          <el-form-item label="出勤工时" prop="attendanceHours">
            <el-input-number v-model="form.attendanceHours" :min="0" :precision="2" :step="0.5" style="width: 100%" />
          </el-form-item>
          <el-form-item label="加班工时" prop="overtimeHours">
            <el-input-number v-model="form.overtimeHours" :min="0" :precision="2" :step="0.5" style="width: 100%" />
          </el-form-item>
          <el-form-item label="考勤状态" prop="attendanceStatus">
            <el-select v-model="form.attendanceStatus" placeholder="请选择考勤状态">
              <el-option v-for="item in attendanceStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="上报来源" prop="sourceType">
            <el-select v-model="form.sourceType" placeholder="请选择上报来源">
              <el-option v-for="item in sourceTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="归集状态" prop="collectStatus">
            <el-radio-group v-model="form.collectStatus">
              <el-radio v-for="item in collectStatusOptions" :key="item.value" :value="item.value">{{ item.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="工资校验" prop="attCheck">
            <el-radio-group v-model="form.attCheck">
              <el-radio v-for="item in attCheckOptions" :key="item.value" :value="item.value">{{ item.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </div>
        <el-form-item label="设备编码" prop="deviceCode">
          <el-input v-model="form.deviceCode" placeholder="请输入设备编码" />
        </el-form-item>
        <el-form-item label="异常说明" prop="anomalyRemark">
          <el-input v-model="form.anomalyRemark" type="textarea" :rows="3" placeholder="请输入异常说明" />
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

    <page-detail-dialog v-model="detailOpen" title="原始考勤详情" width="760px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="考勤编号">{{ detail.attendanceNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="考勤日期">{{ parseTime(detail.attendanceDate, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="劳动者">{{ detail.personName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ maskIdCard(detail.idCard) || '-' }}</el-descriptions-item>
          <el-descriptions-item label="合同编号">{{ detail.contractNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="派遣单位">{{ detail.dispatchEnterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="用工单位">{{ detail.employerEnterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="班次">{{ detail.shiftName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="考勤状态">
            <dict-tag :options="attendanceStatusOptions" :value="detail.attendanceStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="上报来源">
            <dict-tag :options="sourceTypeOptions" :value="detail.sourceType" />
          </el-descriptions-item>
          <el-descriptions-item label="归集状态">
            <dict-tag :options="collectStatusOptions" :value="detail.collectStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="工资校验">
            <dict-tag :options="attCheckOptions" :value="detail.attCheck" />
          </el-descriptions-item>
          <el-descriptions-item label="上班打卡">{{ parseTime(detail.clockInTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="下班打卡">{{ parseTime(detail.clockOutTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="出勤工时">{{ formatDecimal(detail.attendanceHours) }} 小时</el-descriptions-item>
          <el-descriptions-item label="加班工时">{{ formatDecimal(detail.overtimeHours) }} 小时</el-descriptions-item>
          <el-descriptions-item label="设备编码" :span="2">{{ detail.deviceCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="异常说明" :span="2">{{ detail.anomalyRemark || '-' }}</el-descriptions-item>
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

<script setup name="YgbAttendanceRaw">
import { computed, getCurrentInstance, nextTick, reactive, ref, toRefs, watch, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import { addAttendanceRaw, delAttendanceRaw, getAttendanceRaw, getAttendanceRawOverview, getAttendanceRawSummary, listAttendanceRaw, updateAttendanceRaw } from '@/api/ygb/attendanceRaw'
import { optionselectContract } from '@/api/ygb/contract'
import { decoratePortalExplanationItems, openPortalExplanationAction, resolvePortalExplanationSummary } from '@/utils/portalExplanation'
import { useRoleViewMode } from '@/utils/roleView'
import { parseTime } from '@/utils/yuegongbao'
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'
import { formatRegionName } from '@/views/warning/useWarningPage'

const { proxy } = getCurrentInstance()
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const router = useRouter()

const attendanceRawWorkbenchFilterFields = ['attendanceNo', 'contractId', 'attendanceStatus', 'collectStatus', 'sourceType', 'attCheck']
const attendanceRawWorkbenchRouteFields = [...attendanceRawWorkbenchFilterFields, 'focusKey']

const attendanceStatusOptions = [
  { label: '缺勤', value: '0' },
  { label: '正常', value: '1' },
  { label: '迟到', value: '2' },
  { label: '早退', value: '3' },
  { label: '加班', value: '4' },
  { label: '异常', value: '5' }
]

const sourceTypeOptions = [
  { label: '闸机', value: '1' },
  { label: '人脸', value: '2' },
  { label: 'APP', value: '3' },
  { label: '补录', value: '4' },
  { label: '第三方', value: '5' }
]

const collectStatusOptions = [
  { label: '未归集', value: '0' },
  { label: '已归集', value: '1' }
]

const attCheckOptions = [
  { label: '不通过', value: '0' },
  { label: '通过', value: '1' }
]

const attendanceRawList = ref([])
const contractOptions = ref([])
const summaryData = ref({})
const overviewData = ref({})
const currentAttendance = ref(undefined)
const detail = ref(undefined)
const ids = ref([])
const attendanceNos = ref([])
const open = ref(false)
const detailOpen = ref(false)
const loading = ref(true)
const showSearch = ref(true)
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
    attendanceNo: undefined,
    contractId: undefined,
    attendanceStatus: undefined,
    collectStatus: undefined,
    sourceType: undefined,
    attCheck: undefined
  },
  rules: {
    attendanceNo: [{ required: true, message: '考勤编号不能为空', trigger: 'blur' }],
    contractId: [{ required: true, message: '关联合同不能为空', trigger: 'change' }],
    attendanceDate: [{ required: true, message: '考勤日期不能为空', trigger: 'change' }],
    attendanceStatus: [{ required: true, message: '考勤状态不能为空', trigger: 'change' }],
    sourceType: [{ required: true, message: '上报来源不能为空', trigger: 'change' }]
  }
})

const { form, queryParams, rules } = toRefs(data)

function buildAttendanceRawExplanationQuery(extraQuery = {}) {
  return {
    attendanceNo: queryParams.value.attendanceNo,
    contractId: queryParams.value.contractId,
    attendanceStatus: queryParams.value.attendanceStatus,
    collectStatus: queryParams.value.collectStatus,
    sourceType: queryParams.value.sourceType,
    attCheck: queryParams.value.attCheck,
    ...extraQuery
  }
}

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: attendanceRawWorkbenchFilterFields,
  sourceLabel: '企业工作台',
  title: '当前原始考勤页沿用了工作台来源条件',
  description: '列表已按首页工作台或解释面板带入的业务范围聚焦，便于继续处理考勤上报和归集承接。',
  fieldLabels: {
    attendanceNo: '考勤编号',
    contractId: '关联合同',
    attendanceStatus: '考勤状态',
    collectStatus: '归集状态',
    sourceType: '上报来源',
    attCheck: '校验状态'
  },
  fieldFormatters: {
    contractId: value => contractLabel(value) || value,
    attendanceStatus: value => attendanceStatusLabel(value),
    collectStatus: value => collectStatusLabel(value),
    sourceType: value => sourceTypeLabel(value),
    attCheck: value => attCheckLabel(value)
  }
}))

function buildBaseFocusQueues() {
  const rows = attendanceRawList.value || []
  return [
    {
      key: 'uncollected',
      title: '未归集记录',
      desc: '未归集原始考勤应在月度归集前先补齐，避免后续工时和工资链路缺底账。',
      count: summaryData.value.uncollectedCount != null ? summaryData.value.uncollectedCount : rows.filter(item => isUncollectedAttendance(item)).length,
      unit: '条',
      actionText: rows.some(item => isUncollectedAttendance(item)) ? '优先补录归集' : '归集平稳'
    },
    {
      key: 'abnormal',
      title: '异常考勤对象',
      desc: '缺勤、迟到、早退和异常对象应优先回查现场打卡和班次口径，避免异常被直接带入月归集。',
      count: summaryData.value.abnormalCount != null ? summaryData.value.abnormalCount : rows.filter(item => isAttendanceAbnormal(item)).length,
      unit: '条',
      actionText: rows.some(item => isAttendanceAbnormal(item)) ? '优先回查异常' : '异常可控'
    },
    {
      key: 'checkFailed',
      title: '校验失败记录',
      desc: '工资校验未通过对象应先核对工时、合同和归集口径，再决定是否继续流转。',
      count: summaryData.value.checkFailedCount != null ? summaryData.value.checkFailedCount : rows.filter(item => isCheckFailedAttendance(item)).length,
      unit: '条',
      actionText: rows.some(item => isCheckFailedAttendance(item)) ? '优先复核校验' : '校验平稳'
    },
    {
      key: 'manualFill',
      title: '人工补录记录',
      desc: '人工补录对象进入月归集前应保留佐证材料和来源说明，避免后续对账无法追溯。',
      count: summaryData.value.manualFillCount != null ? summaryData.value.manualFillCount : rows.filter(item => isManualFillAttendance(item)).length,
      unit: '条',
      actionText: rows.some(item => isManualFillAttendance(item)) ? '补齐补录依据' : '补录可追溯'
    },
    {
      key: 'collected',
      title: '已归集记录',
      desc: '已归集原始考勤可直接供月度工时汇总和工资明细承接使用，重点保持链路顺畅。',
      count: summaryData.value.collectedCount != null ? summaryData.value.collectedCount : rows.filter(item => isCollectedAttendance(item)).length,
      unit: '条',
      actionText: rows.some(item => isCollectedAttendance(item)) ? '推进月归集承接' : '归集待形成'
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
  evidenceModule: 'attendanceRaw',
  recommendModule: 'attendanceRaw',
  defaultQuery: buildAttendanceRawExplanationQuery({ focusKey: item.key }),
  sourceLabel: '530.1 原始考勤办理解释',
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
    }
  })

  return Array.from(fallbackMap.values())
})

const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'ygb',
  panelTitle: '530.1 原始考勤办理解释',
  panelDescription: '当前解释项按 530.1 办理链口径展示原始考勤采集、校验、补录和归集重点。'
}))

const portalExplanationSummary = computed(() => resolvePortalExplanationSummary(portalExplanationItems.value, ''))
const leadingPortalExplanation = computed(() => portalExplanationItems.value[0] || null)

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
      label: '原始考勤总量',
      value: summaryData.value.totalCount != null ? summaryData.value.totalCount : total.value,
      unit: '条',
      note: '当前筛选范围内纳入原始考勤办理链路的记录总量。',
      cardClass: ''
    },
    {
      key: 'uncollected',
      label: '未归集待办',
      value: valueOrDefault(summaryData.value.uncollectedCount, 0),
      unit: '条',
      note: '未归集原始考勤应优先补齐，避免月归集和工资核验缺少原始底账。',
      cardClass: 'ygb-summary-card--warning'
    },
    {
      key: 'abnormal',
      label: '异常考勤回查',
      value: valueOrDefault(summaryData.value.abnormalCount, 0),
      unit: '条',
      note: '异常考勤对象需要先回查打卡、班次和现场口径，再决定是否继续归集。',
      cardClass: 'ygb-summary-card--primary'
    },
    {
      key: 'checkFailed',
      label: '校验失败记录',
      value: valueOrDefault(summaryData.value.checkFailedCount, 0),
      unit: '条',
      note: '工资校验失败对象应优先复核工时和合同口径，避免后续发薪链路被拖住。',
      cardClass: 'ygb-summary-card--success'
    }
  ]

  return cards.map(item => ({
    ...item,
    note: resolveExplanationFirstText(item.note, resolvePortalExplanationItem(item.key))
  }))
})

const attendanceOverviewCards = computed(() => ([
  {
    key: 'deviceOnlineRate',
    label: '考勤设备在线率',
    value: formatRate(overviewData.value.deviceOnlineRate),
    unit: '%',
    note: `在线 ${valueOrDefault(overviewData.value.onlineDeviceCount, 0)} 台，离线 ${valueOrDefault(overviewData.value.offlineDeviceCount, 0)} 台。`,
    cardClass: 'ygb-summary-card--primary'
  },
  {
    key: 'manualFill',
    label: '人工补录占比',
    value: formatRate(ratioValue(overviewData.value.manualFillCount, overviewData.value.totalCount)),
    unit: '%',
    note: '人工补录占比较高时，应优先核查设备采集覆盖和现场补录依据。',
    cardClass: 'ygb-summary-card--warning'
  }
]))

const regionOverviewItems = computed(() => normalizeOverviewItems(overviewData.value.regionStats, 'regionCode'))
const sourceOverviewItems = computed(() => normalizeOverviewItems(overviewData.value.sourceStats, 'sourceType'))

const resolvedFocusQueues = computed(() => baseFocusQueues.value.map(item => {
  const explanation = resolvePortalExplanationItem(item.key)
  return {
    ...item,
    title: explanation?.dimensionName || item.title,
    desc: resolveExplanationFirstText(item.desc, explanation),
    actionText: resolveExplanationFirstActionText(item.actionText, explanation)
  }
}))

function resolveAttendanceFocusKey(value) {
  const normalized = typeof value === 'string' ? value.trim() : ''
  return resolvedFocusQueues.value.some(item => item.key === normalized) ? normalized : ''
}

const activeFocus = computed(() => resolvedFocusQueues.value.find(item => item.key === activeFocusKey.value) || resolvedFocusQueues.value[0] || null)
const visibleAttendanceRawList = computed(() => prioritizeFocusRows(attendanceRawList.value, row => matchAttendanceRawFocus(row, activeFocus.value?.key)))

const focusTableHint = computed(() => {
  if (!activeFocus.value) {
    return '当前按查询条件展示原始考勤台账。'
  }
  return `${activeFocus.value.title}，系统已将对应记录前置，便于优先处理最影响月归集和工资核验的考勤对象。`
})

const selectedAttendanceOverview = computed(() => {
  if (!currentAttendance.value) {
    return [
      { label: '劳动者', value: '-' },
      { label: '合同/日期', value: '-' },
      { label: '考勤/归集', value: '-' },
      { label: '工时/校验', value: '-' }
    ]
  }

  return [
    { label: '劳动者', value: currentAttendance.value.personName || '-' },
    { label: '合同/日期', value: `${currentAttendance.value.contractNo || '-'} / ${parseTime(currentAttendance.value.attendanceDate, '{y}-{m}-{d}') || '-'}` },
    { label: '考勤/归集', value: `${attendanceStatusLabel(currentAttendance.value.attendanceStatus)} / ${collectStatusLabel(currentAttendance.value.collectStatus)}` },
    { label: '工时/校验', value: `${formatDecimal(currentAttendance.value.attendanceHours)} 小时 / ${attCheckLabel(currentAttendance.value.attCheck)}` }
  ]
})

function resolveAttendanceObjectFocusKey(row) {
  if (!row) {
    return activeFocus.value?.key || ''
  }
  if (isUncollectedAttendance(row)) {
    return 'uncollected'
  }
  if (isCheckFailedAttendance(row)) {
    return 'checkFailed'
  }
  if (isAttendanceAbnormal(row)) {
    return 'abnormal'
  }
  if (isManualFillAttendance(row)) {
    return 'manualFill'
  }
  if (isCollectedAttendance(row)) {
    return 'collected'
  }
  return activeFocus.value?.key || ''
}

const currentPortalExplanation = computed(() => resolvePortalExplanationItem(resolveAttendanceObjectFocusKey(currentAttendance.value) || 0))
const detailPortalExplanation = computed(() => resolvePortalExplanationItem(resolveAttendanceObjectFocusKey(detail.value) || 0))

const primaryAttendanceAction = computed(() => {
  if (!currentAttendance.value) {
    return { label: '选择待办对象', action: 'detail' }
  }
  if (isReadOnlyRole.value) {
    return { label: '查看详情', action: 'detail' }
  }
  if (
    isUncollectedAttendance(currentAttendance.value) ||
    isCheckFailedAttendance(currentAttendance.value) ||
    isAttendanceAbnormal(currentAttendance.value) ||
    isManualFillAttendance(currentAttendance.value)
  ) {
    return { label: '修改考勤', action: 'edit' }
  }
  return { label: '查看详情', action: 'detail' }
})

const currentAttendanceActionSummary = computed(() => {
  if (!currentAttendance.value) {
    return '先从左侧焦点队列选择重点考勤对象，再联动查看当前办理建议。'
  }
  if (isUncollectedAttendance(currentAttendance.value)) {
    return '该记录当前仍未归集，建议先补齐工时、打卡时间和异常说明，避免月归集阶段缺少原始底账。'
  }
  if (isCheckFailedAttendance(currentAttendance.value)) {
    return '该记录工资校验未通过，建议优先回查合同口径、考勤日期和工时后再继续流转。'
  }
  if (isAttendanceAbnormal(currentAttendance.value)) {
    return '该记录存在缺勤、迟到、早退或异常状态，建议先核实现场打卡和班次说明，再决定是否补录或归集。'
  }
  if (isManualFillAttendance(currentAttendance.value)) {
    return '该记录为人工补录来源，建议保留补录依据和异常说明后再进入后续月归集链路。'
  }
  if (isCollectedAttendance(currentAttendance.value)) {
    return '该记录已完成归集，可直接承接月度工时汇总和工资明细生成。'
  }
  return '该记录当前信息完整，可继续沿原始考勤链路推进后续处理。'
})

function buildHintTags(item) {
  if (!item) {
    return [{ label: '未选中原始考勤对象，可先在列表中选择待办理记录', type: 'info' }]
  }
  const tags = []
  if (isUncollectedAttendance(item)) {
    tags.push({ label: '当前未归集，建议优先补齐后推进月归集', type: 'warning' })
  }
  if (isAttendanceAbnormal(item)) {
    tags.push({ label: '存在异常考勤状态，建议先回查打卡和班次口径', type: 'warning' })
  }
  if (isCheckFailedAttendance(item)) {
    tags.push({ label: '工资校验不通过，建议优先复核工时和合同口径', type: 'danger' })
  }
  if (isManualFillAttendance(item)) {
    tags.push({ label: '人工补录记录，进入归集前应保留来源和佐证材料', type: 'info' })
  }
  if (isCollectedAttendance(item)) {
    tags.push({ label: '当前已归集，可直接承接月归集和工资链路', type: 'success' })
  }
  if (!item.clockInTime || !item.clockOutTime) {
    tags.push({ label: '打卡时间不完整，建议补齐上下班时间', type: 'warning' })
  }
  if (!tags.length) {
    tags.push({ label: '当前原始考勤信息完整，可继续进入后续归集链路', type: 'success' })
  }
  return tags
}

const currentAttendanceActionTags = computed(() => buildHintTags(currentAttendance.value))
const attendanceHintTags = computed(() => buildHintTags(currentAttendance.value))
const detailHintTags = computed(() => buildHintTags(detail.value))

function buildExplanationFirstTags(tags = [], explanation = currentPortalExplanation.value) {
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

const resolvedCurrentAttendanceActionSummary = computed(() => resolveExplanationFirstText(
  currentAttendanceActionSummary.value,
  currentPortalExplanation.value
))

const resolvedCurrentAttendanceActionTags = computed(() => buildExplanationFirstTags(currentAttendanceActionTags.value, currentPortalExplanation.value))
const resolvedAttendanceHintTags = computed(() => buildExplanationFirstTags(attendanceHintTags.value, currentPortalExplanation.value))
const resolvedDetailHintTags = computed(() => buildExplanationFirstTags(detailHintTags.value, detailPortalExplanation.value))

const resolvedPrimaryAttendanceAction = computed(() => ({
  ...primaryAttendanceAction.value,
  label: resolveExplanationFirstActionText(primaryAttendanceAction.value.label, currentPortalExplanation.value)
}))

const workflowSteps = computed(() => ([
  {
    label: '锁定合同与采集日期',
    desc: '先确认原始考勤对应的合同、人员和采集日期，避免后续月归集把考勤挂错主体。'
  },
  {
    label: '回收原始打卡记录',
    desc: '统一承接闸机、人脸、APP、第三方和补录来源的原始打卡，形成后续归集的唯一底账。'
  },
  {
    label: '核查异常与校验失败',
    desc: '优先消化缺勤、迟到、早退和工资校验不通过对象，避免异常被直接传导到工资链路。'
  },
  {
    label: '保留人工补录依据',
    desc: '人工补录对象进入月归集前应补齐来源说明、异常备注和佐证材料，确保后续可追溯。'
  },
  {
    label: '推进月归集承接',
    desc: '已归集原始考勤应直接供月度工时汇总和工资明细使用，不再重复人工整理。'
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
    listAttendanceRaw(queryParams.value),
    getAttendanceRawSummary(buildSummaryQuery()),
    getAttendanceRawOverview(buildSummaryQuery())
  ]).then(([listResponse, summaryResponse, overviewResponse]) => {
    attendanceRawList.value = listResponse.rows || []
    total.value = listResponse.total || 0
    summaryData.value = summaryResponse.data || {}
    overviewData.value = overviewResponse.data || {}
    syncCurrentAttendance()
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

function buildSummaryQuery() {
  return {
    attendanceNo: queryParams.value.attendanceNo,
    contractId: queryParams.value.contractId,
    attendanceStatus: queryParams.value.attendanceStatus,
    collectStatus: queryParams.value.collectStatus,
    sourceType: queryParams.value.sourceType,
    attCheck: queryParams.value.attCheck
  }
}

function syncCurrentAttendance() {
  if (currentAttendance.value) {
    const matched = visibleAttendanceRawList.value.find(item => String(item.attendanceId) === String(currentAttendance.value.attendanceId))
    if (matched) {
      currentAttendance.value = matched
      return
    }
  }
  currentAttendance.value = visibleAttendanceRawList.value.length > 0 ? visibleAttendanceRawList.value[0] : undefined
}

function loadContractOptions() {
  optionselectContract().then(response => {
    contractOptions.value = response.data || []
  })
}

function reset() {
  form.value = {
    attendanceId: undefined,
    attendanceNo: undefined,
    contractId: undefined,
    attendanceDate: undefined,
    shiftName: undefined,
    clockInTime: undefined,
    clockOutTime: undefined,
    attendanceHours: undefined,
    overtimeHours: 0,
    attendanceStatus: '1',
    sourceType: '1',
    collectStatus: '0',
    attCheck: '1',
    deviceCode: undefined,
    anomalyRemark: undefined,
    remark: undefined
  }
  proxy.resetForm('attendanceRawRef')
}

function cancel() {
  open.value = false
  reset()
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
    attendanceNo: undefined,
    contractId: undefined,
    attendanceStatus: undefined,
    collectStatus: undefined,
    sourceType: undefined,
    attCheck: undefined,
    dispatchEnterpriseId: undefined,
    regionCode: undefined
  })
  applyWorkbenchRouteQuery(route.query, queryParams.value, attendanceRawWorkbenchFilterFields)
  activeFocusKey.value = resolveAttendanceFocusKey(route.query.focusKey)
  getList()

watchEffect(() => {
  setPageGuide({
    title: '??????' || '??????',
    description: '?????????????????????????????????' || '?????????????????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: resolvedFocusQueues.value,
    selection: [...selectedAttendanceOverview.value, { label: '??????', value: currentAttendanceActionSummary.value }],
    workflow: resolvedWorkflowSteps.value,
    hints: [...currentAttendanceActionTags.value].slice(0, 6)
  })
})

}

function clearWorkbenchContext() {
  proxy.resetForm('queryRef')
  activeFocusKey.value = ''
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    attendanceNo: undefined,
    contractId: undefined,
    attendanceStatus: undefined,
    collectStatus: undefined,
    sourceType: undefined,
    attCheck: undefined,
    dispatchEnterpriseId: undefined,
    regionCode: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, attendanceRawWorkbenchRouteFields)
  })
  getList()
}

function applyAttendanceRawWorkbenchQuery(routeQuery = {}) {
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    attendanceNo: undefined,
    contractId: undefined,
    attendanceStatus: undefined,
    collectStatus: undefined,
    sourceType: undefined,
    attCheck: undefined,
    dispatchEnterpriseId: undefined,
    regionCode: undefined
  })
  applyWorkbenchRouteQuery(routeQuery, queryParams.value, attendanceRawWorkbenchFilterFields)
  activeFocusKey.value = resolveAttendanceFocusKey(routeQuery.focusKey)
  currentAttendance.value = undefined
  syncCurrentAttendance()
  getList()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.attendanceId)
  attendanceNos.value = selection.map(item => item.attendanceNo)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleRowClick(row) {
  currentAttendance.value = row
}

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  syncCurrentAttendance()
}

function handlePortalExplanationAction(action) {
  if (action?.path === route.path) {
    applyAttendanceRawWorkbenchQuery(action.query || {})
    return
  }
  openPortalExplanationAction(router, action)
}

function handleAdd() {
  if (blockReadOnlyAction('新增原始考勤')) {
    return
  }
  reset()
  open.value = true
  title.value = '新增原始考勤'
}

function handleUpdate(row) {
  if (blockReadOnlyAction('修改原始考勤')) {
    return
  }
  reset()
  const attendanceId = row?.attendanceId || ids.value[0]
  if (!attendanceId) {
    return
  }
  getAttendanceRaw(attendanceId).then(response => {
    form.value = response.data || {}
    currentAttendance.value = response.data || currentAttendance.value
    open.value = true
    title.value = '修改原始考勤'
  })
}

function openDetail(row) {
  if (!row?.attendanceId) {
    return
  }
  currentAttendance.value = row
  getAttendanceRaw(row.attendanceId).then(response => {
    detail.value = response.data
    detailOpen.value = true
  })
}

function submitForm() {
  proxy.$refs.attendanceRawRef.validate(valid => {
    if (!valid) {
      return
    }
    if (form.value.attendanceId != null) {
      updateAttendanceRaw(form.value).then(() => {
        proxy.$modal.msgSuccess('修改成功')
        open.value = false
        getList()
        refreshDetailIfMatched(form.value.attendanceId)
      })
      return
    }
    addAttendanceRaw(form.value).then(() => {
      proxy.$modal.msgSuccess('新增成功')
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  if (blockReadOnlyAction('删除原始考勤')) {
    return
  }
  const attendanceIds = row?.attendanceId || ids.value
  const names = row?.attendanceNo || attendanceNos.value.join('、')
  proxy.$modal.confirm(`是否确认删除原始考勤“${names}”？`).then(function() {
    return delAttendanceRaw(attendanceIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

function refreshDetailIfMatched(attendanceId) {
  if (detailOpen.value && detail.value && String(detail.value.attendanceId) === String(attendanceId)) {
    getAttendanceRaw(attendanceId).then(response => {
      detail.value = response.data
    })
  }
}

function handlePrimaryAttendanceAction() {
  if (!currentAttendance.value) {
    return
  }
  if (primaryAttendanceAction.value.action === 'edit') {
    handleUpdate(currentAttendance.value)
    return
  }
  openDetail(currentAttendance.value)
}

function handleExport() {
  proxy.download('ygb/attendance/raw/export', {
    ...queryParams.value
  }, `attendance_raw_${new Date().getTime()}.xlsx`)
}

function applyOverviewFilter(field, value) {
  queryParams.value.pageNum = 1
  queryParams.value[field] = value || undefined
  getList()
}

function blockReadOnlyAction(actionLabel) {
  if (!isReadOnlyRole.value) {
    return false
  }
  proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}仅保留查看和导出能力，不能${actionLabel}`)
  return true
}

function isUncollectedAttendance(item) {
  return String(item?.collectStatus) === '0'
}

function isCollectedAttendance(item) {
  return String(item?.collectStatus) === '1'
}

function isAttendanceAbnormal(item) {
  return ['0', '2', '3', '5'].includes(String(item?.attendanceStatus))
}

function isManualFillAttendance(item) {
  return String(item?.sourceType) === '4'
}

function isCheckFailedAttendance(item) {
  return String(item?.attCheck) === '0'
}

function matchAttendanceRawFocus(row, focusKey) {
  if (!row || !focusKey) {
    return false
  }
  if (focusKey === 'uncollected') {
    return isUncollectedAttendance(row)
  }
  if (focusKey === 'abnormal') {
    return isAttendanceAbnormal(row)
  }
  if (focusKey === 'checkFailed') {
    return isCheckFailedAttendance(row)
  }
  if (focusKey === 'manualFill') {
    return isManualFillAttendance(row)
  }
  if (focusKey === 'collected') {
    return isCollectedAttendance(row)
  }
  return false
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

function formatContractOptionLabel(item) {
  if (!item) {
    return '-'
  }
  const parts = [item.contractNo, item.personName].filter(Boolean)
  return parts.length ? parts.join(' / ') : String(item.contractId || '-')
}

function contractLabel(value) {
  const matched = contractOptions.value.find(item => String(item.contractId) === String(value))
  return matched ? formatContractOptionLabel(matched) : ''
}

function attendanceStatusLabel(value) {
  const matched = attendanceStatusOptions.find(item => String(item.value) === String(value))
  return matched ? matched.label : '-'
}

function sourceTypeLabel(value) {
  const matched = sourceTypeOptions.find(item => String(item.value) === String(value))
  return matched ? matched.label : '-'
}

function collectStatusLabel(value) {
  const matched = collectStatusOptions.find(item => String(item.value) === String(value))
  return matched ? matched.label : '-'
}

function attCheckLabel(value) {
  const matched = attCheckOptions.find(item => String(item.value) === String(value))
  return matched ? matched.label : '-'
}

function normalizeOverviewItems(items = [], field) {
  return (Array.isArray(items) ? items : []).map(item => ({
    ...item,
    dimensionLabel: resolveOverviewLabel(field, item.dimensionKey, item.dimensionLabel),
    dimensionCount: Number(item.dimensionCount || 0)
  }))
}

function resolveOverviewLabel(field, value, fallback) {
  if (field === 'regionCode') {
    return formatRegionName(value, fallback || value || '-')
  }
  if (field === 'sourceType') {
    return sourceTypeLabel(value || fallback)
  }
  return fallback || value || '-'
}

function valueOrDefault(value, fallback = 0) {
  return value != null ? value : fallback
}

function formatDecimal(value) {
  if (value == null || value === '') {
    return '0.00'
  }
  const amount = Number(value)
  if (Number.isNaN(amount)) {
    return String(value)
  }
  return amount.toFixed(2)
}

function ratioValue(numerator, denominator) {
  if (!denominator) {
    return 0
  }
  return (Number(numerator || 0) * 100) / Number(denominator)
}

function formatRate(value) {
  if (value == null || value === '') {
    return '0.00'
  }
  const amount = Number(value)
  if (Number.isNaN(amount)) {
    return '0.00'
  }
  return amount.toFixed(2)
}

function maskIdCard(idCard) {
  if (!idCard || idCard.length < 8) {
    return idCard
  }
  return `${idCard.slice(0, 4)}********${idCard.slice(-4)}`
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

applyWorkbenchRouteQuery(route.query, queryParams.value, attendanceRawWorkbenchFilterFields)
activeFocusKey.value = resolveAttendanceFocusKey(route.query.focusKey)
loadContractOptions()
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
.ygb-analysis-grid,
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

.ygb-analysis-card {
  min-height: 154px;
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

.ygb-analysis-list {
  display: grid;
  gap: 10px;
}

.ygb-analysis-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #e0e8f0;
  border-radius: 12px;
  background: #f7fafc;
  color: #13243a;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: left;
}

.ygb-analysis-item:hover {
  border-color: #0f5ea8;
  background: #eef5ff;
}

.ygb-analysis-item__label {
  color: #425568;
  line-height: 1.6;
}

.ygb-analysis-item__value {
  color: #13243a;
  font-size: 18px;
  font-weight: 700;
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
  .ygb-analysis-grid,
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
