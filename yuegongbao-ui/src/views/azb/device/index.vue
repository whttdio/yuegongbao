<template>
  <div class="app-container azb-page azb-device-page">
    <section class="gov-page-header azb-page__header">
      <div>
        <p class="azb-device-page__eyebrow">设备治理中台</p>
        <h1 class="azb-device-page__title">{{ roleTitle }}</h1>
        <p class="azb-device-page__desc">
          {{ roleDescription }}
          当前页面保留统一设备台账、远程指令和事件日志链路，但前端已按应急监管、现场负责人、保险和银行只读协同重排焦点对象和操作顺序。
        </p>
      </div>
      <div class="azb-device-page__tips">
        <div class="azb-device-page__tip">当前视角：{{ roleBadge }}</div>
        <div class="azb-device-page__tip">重点动作：先锁定离线、故障、未授权设备，再回到授权、心跳和 AI 事件闭环。</div>
        <div class="azb-device-page__tip">设备接入继续走 Stub，不接真实 MQTT、设备云和摄像头平台。</div>
      </div>
    </section>

    <div class="azb-device-page__summary">
      <div v-for="item in resolvedSummaryCards" :key="item.key" class="azb-device-card" :class="item.cardClass">
        <div class="azb-device-card__label">{{ item.label }}</div>
        <div class="azb-device-card__value">
          {{ item.value }}
          <span class="azb-device-card__unit">{{ item.unit }}</span>
        </div>
        <div class="azb-device-card__note">{{ item.note }}</div>
      </div>
    </div>

    <el-card class="azb-device-panel" shadow="never" style="margin-bottom: 16px;">
      <template #header>
        <div class="azb-device-panel__head">
          <div class="azb-device-panel__title">设备子视图</div>
          <div class="azb-device-panel__desc">从统一设备台账直接下钻考勤设备、芯片设备、AI 设备、物联卡、芯片库存、电子围栏和拆卸报警。</div>
        </div>
      </template>
      <div class="azb-link-grid">
        <button
          v-for="entry in submoduleEntries"
          :key="entry.key"
          type="button"
          class="azb-link-item"
          @click="openSubmodule(entry)"
        >
          <div class="azb-link-item__title">{{ entry.title }}</div>
          <div class="azb-link-item__desc">{{ entry.desc }}</div>
          <div class="azb-link-item__action">{{ entry.actionText }}</div>
        </button>
      </div>
    </el-card>

    <el-alert
      v-if="isReadOnlyRole"
      :title="readOnlyAlertTitle"
      :description="readOnlyAlertDescription"
      type="info"
      :closable="false"
      show-icon
      style="margin-bottom: 16px;"
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
      <div class="azb-device-tag-list">
        <el-tag v-for="item in workbenchContext.tags" :key="item.key" effect="plain">{{ item.label }}：{{ item.value }}</el-tag>
      </div>
    </el-alert>

    <el-card class="search-card" shadow="never">
      <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch">
        <el-form-item label="区域">
          <el-select v-model="queryParams.regionCode" clearable style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="设备编码">
          <el-input v-model="queryParams.deviceCode" clearable style="width: 180px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="设备名称">
          <el-input v-model="queryParams.deviceName" clearable style="width: 180px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="企业">
          <el-select v-model="queryParams.enterpriseId" clearable filterable style="width: 220px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="设备类型">
          <el-select v-model="queryParams.deviceType" clearable style="width: 160px">
            <el-option v-for="item in deviceTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="设备状态">
          <el-select v-model="queryParams.deviceStatus" clearable style="width: 160px">
            <el-option v-for="item in deviceStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="授权状态">
          <el-select v-model="queryParams.authStatus" clearable style="width: 160px">
            <el-option v-for="item in authStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['ygb:device:add']">新增</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['ygb:device:edit']">修改</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['ygb:device:remove']">删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:device:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="azb-device-panel__head azb-device-panel__head--between">
          <div>
            <div class="azb-device-panel__title">设备治理台账</div>
            <div class="azb-device-panel__desc">{{ focusTableHint }}</div>
          </div>
          <div class="azb-device-panel__extra">当前总量 {{ total }} 台</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="visibleDeviceList" @selection-change="handleSelectionChange" @row-click="handleRowClick">
        <el-table-column v-if="!isReadOnlyRole" type="selection" width="55" align="center" />
        <el-table-column label="ID" prop="deviceId" width="90" />
        <el-table-column label="设备编码" prop="deviceCode" width="160" />
        <el-table-column label="设备名称" prop="deviceName" min-width="180" />
        <el-table-column label="设备类型" width="110">
          <template #default="scope">
            <dict-tag :options="deviceTypeOptions" :value="scope.row.deviceType" />
          </template>
        </el-table-column>
        <el-table-column label="企业" prop="enterpriseName" min-width="200" />
        <el-table-column label="区域" width="140">
          <template #default="scope">{{ formatRegionName(scope.row.regionCode, '-') }}</template>
        </el-table-column>
        <el-table-column label="芯片ID" prop="chipId" width="130" />
        <el-table-column label="设备状态" width="110">
          <template #default="scope">
            <dict-tag :options="deviceStatusOptions" :value="scope.row.deviceStatus" />
          </template>
        </el-table-column>
        <el-table-column label="授权状态" width="110">
          <template #default="scope">
            <dict-tag :options="authStatusOptions" :value="scope.row.authStatus" />
          </template>
        </el-table-column>
        <el-table-column label="最后心跳" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.lastHeartbeat, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="安装位置" prop="installLocation" min-width="180" show-overflow-tooltip />
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" align="center" :width="isReadOnlyRole ? 100 : 360" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button v-if="isReadOnlyRole" link type="info" icon="View" @click.stop="handleRowClick(scope.row)">详情</el-button>
            <template v-else>
              <el-button link type="primary" icon="Edit" @click.stop="handleUpdate(scope.row)" v-hasPermi="['ygb:device:edit']">修改</el-button>
              <el-button link type="primary" icon="Lock" @click.stop="handleLock(scope.row)" v-hasPermi="['ygb:device:lock']">锁机</el-button>
              <el-button link type="primary" icon="Unlock" @click.stop="handleUnlock(scope.row)" v-hasPermi="['ygb:device:unlock']">解锁</el-button>
              <el-button link type="primary" icon="Checked" @click.stop="openAuthorizeDialog(scope.row)" v-hasPermi="['ygb:device:authorize']">授权</el-button>
              <el-button link type="primary" icon="Connection" @click.stop="openHeartbeatDialog(scope.row)" v-hasPermi="['ygb:device:heartbeat']">心跳</el-button>
              <el-button link type="primary" icon="VideoCamera" @click.stop="openAiDialog(scope.row)" v-hasPermi="['ygb:device:aiEvent']">AI事件</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <el-card class="table-card azb-device-panel" shadow="never">
      <template #header>
        <div class="azb-device-panel__head azb-device-panel__head--between">
          <div>
            <div class="azb-device-panel__title">{{ currentDevice ? currentDevice.deviceName : '日志联动' }}</div>
            <div class="azb-device-panel__desc">
              {{ currentDevice ? `${currentDevice.deviceCode} / ${currentDevice.enterpriseName || '-'} / ${formatRegionName(currentDevice.regionCode, '-')}` : '点击设备行后，联动查看当前设备的指令日志和设备事件。' }}
            </div>
          </div>
          <div v-if="currentDevice" class="azb-device-panel__extra">{{ tracePanelHint }}</div>
        </div>
      </template>

      <el-tabs v-model="activeTraceTab">
        <el-tab-pane label="指令日志" name="command">
          <el-table :data="commandLogList" empty-text="暂无指令日志">
            <el-table-column label="日志ID" prop="logId" width="90" />
            <el-table-column label="指令类型" width="100">
              <template #default="scope">
                <dict-tag :options="commandTypeOptions" :value="scope.row.commandType" />
              </template>
            </el-table-column>
            <el-table-column label="执行结果" width="100">
              <template #default="scope">
                <dict-tag :options="commandResultOptions" :value="scope.row.commandResult" />
              </template>
            </el-table-column>
            <el-table-column label="结果说明" prop="resultMessage" min-width="180" show-overflow-tooltip />
            <el-table-column label="来源状态" prop="sourceStatus" width="100" />
            <el-table-column label="来源消息" prop="sourceMessage" min-width="180" show-overflow-tooltip />
            <el-table-column label="回调时间" width="180">
              <template #default="scope">
                <span>{{ parseTime(scope.row.callbackTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作人" prop="operatorName" width="100" />
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="设备事件" name="event">
          <el-table :data="deviceEventList" empty-text="暂无设备事件">
            <el-table-column label="事件ID" prop="eventId" width="90" />
            <el-table-column label="事件类型" width="100">
              <template #default="scope">
                <dict-tag :options="eventTypeOptions" :value="scope.row.eventType" />
              </template>
            </el-table-column>
            <el-table-column label="事件编码" prop="eventCode" width="130" />
            <el-table-column label="事件内容" prop="eventContent" min-width="220" show-overflow-tooltip />
            <el-table-column label="事件状态" width="100">
              <template #default="scope">
                <dict-tag :options="eventStatusOptions" :value="scope.row.eventStatus" />
              </template>
            </el-table-column>
            <el-table-column label="来源消息" prop="sourceMessage" min-width="160" show-overflow-tooltip />
            <el-table-column label="事件时间" width="180">
              <template #default="scope">
                <span>{{ parseTime(scope.row.eventTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog :title="title" v-model="open" width="860px" append-to-body>
      <el-form ref="deviceRef" :model="form" :rules="rules" label-width="110px">
        <div class="azb-device-form">
          <el-form-item label="设备编码" prop="deviceCode">
            <el-input v-model="form.deviceCode" placeholder="请输入设备编码" />
          </el-form-item>
          <el-form-item label="设备名称" prop="deviceName">
            <el-input v-model="form.deviceName" placeholder="请输入设备名称" />
          </el-form-item>
          <el-form-item label="设备类型" prop="deviceType">
            <el-select v-model="form.deviceType" placeholder="请选择设备类型">
              <el-option v-for="item in deviceTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="所属企业" prop="enterpriseId">
            <el-select v-model="form.enterpriseId" filterable placeholder="请选择所属企业">
              <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
            </el-select>
          </el-form-item>
          <el-form-item label="芯片ID" prop="chipId">
            <el-input v-model="form.chipId" placeholder="请输入芯片ID" />
          </el-form-item>
          <el-form-item label="物联卡号" prop="simCardNo">
            <el-input v-model="form.simCardNo" placeholder="请输入物联卡号" />
          </el-form-item>
          <el-form-item label="设备状态" prop="deviceStatus">
            <el-select v-model="form.deviceStatus" placeholder="请选择设备状态">
              <el-option v-for="item in deviceStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="授权状态" prop="authStatus">
            <el-select v-model="form.authStatus" placeholder="请选择授权状态">
              <el-option v-for="item in authStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="安装位置" prop="installLocation">
            <el-input v-model="form.installLocation" placeholder="请输入安装位置" />
          </el-form-item>
          <el-form-item label="固件版本" prop="firmwareVersion">
            <el-input v-model="form.firmwareVersion" placeholder="请输入固件版本" />
          </el-form-item>
        </div>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入设备备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确定</el-button>
          <el-button @click="cancel">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="设备授权" v-model="authorizeOpen" width="560px" append-to-body>
      <el-form ref="authorizeRef" :model="authorizeForm" :rules="authorizeRules" label-width="100px">
        <el-form-item label="设备">
          <el-input :model-value="authorizeDeviceName" disabled />
        </el-form-item>
        <el-form-item label="人员" prop="personId">
          <el-select v-model="authorizeForm.personId" filterable placeholder="请选择授权人员">
            <el-option v-for="item in personOptions" :key="item.personId" :label="`${item.personName} / ${item.enterpriseName || ''}`" :value="item.personId" />
          </el-select>
        </el-form-item>
        <el-form-item label="证书编号" prop="certNo">
          <el-input v-model="authorizeForm.certNo" placeholder="可选，留空则走 Stub 默认判定" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitAuthorize">确定</el-button>
          <el-button @click="authorizeOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="模拟心跳" v-model="heartbeatOpen" width="520px" append-to-body>
      <el-form ref="heartbeatRef" :model="heartbeatForm" label-width="100px">
        <el-form-item label="设备">
          <el-input :model-value="traceDeviceName" disabled />
        </el-form-item>
        <el-form-item label="设备状态" prop="heartbeatStatus">
          <el-select v-model="heartbeatForm.heartbeatStatus" placeholder="请选择心跳后状态">
            <el-option v-for="item in heartbeatStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitHeartbeat">确定</el-button>
          <el-button @click="heartbeatOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="模拟 AI 事件" v-model="aiOpen" width="620px" append-to-body>
      <el-form ref="aiRef" :model="aiForm" label-width="100px">
        <el-form-item label="设备">
          <el-input :model-value="traceDeviceName" disabled />
        </el-form-item>
        <el-form-item label="事件编码" prop="eventCode">
          <el-input v-model="aiForm.eventCode" placeholder="例如：AI_ALERT / PPE_MISSING" />
        </el-form-item>
        <el-form-item label="事件内容" prop="eventContent">
          <el-input v-model="aiForm.eventContent" type="textarea" :rows="3" placeholder="请输入 AI 识别内容" />
        </el-form-item>
        <el-form-item label="证据链接" prop="evidenceUrl">
          <el-input v-model="aiForm.evidenceUrl" placeholder="请输入图片或视频链接" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitAiEvent">确定</el-button>
          <el-button @click="aiOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="AzbDevice">
  import { computed, getCurrentInstance, watchEffect } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import useUserStore from '@/store/modules/user'
import { decoratePortalExplanationItems, openPortalExplanationAction, resolvePortalExplanationSummary } from '@/utils/portalExplanation'
import { useRoleViewMode } from '@/utils/roleView'
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'
import { buildDeviceSubviewEntries, openDeviceSubview } from '@/views/device/subviews'
import {
  authStatusOptions,
  commandResultOptions,
  commandTypeOptions,
  deviceStatusOptions,
  deviceTypeOptions,
  eventStatusOptions,
  eventTypeOptions,
  formatRegionName,
  heartbeatStatusOptions,
  useDevicePage,
  valueOrDefault
} from '@/views/device/useDevicePage'

  const route = useRoute()
  const router = useRouter()
  const { proxy } = getCurrentInstance()
  const { setPageGuide } = useWorkbenchAssist()
  const userStore = useUserStore()
const { isReadOnlyRole, isInsurerRole, isBankRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const deviceWorkbenchFields = ['enterpriseId', 'regionCode']
const portalExplanations = computed(() => summaryData.value.azbExplanation || [])
const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'azb',
  panelTitle: '6.1 治理解释',
  panelDescription: '设备风险、授权异常和治理建议统一来自门户解释聚合接口。'
}))

function handlePortalExplanationAction(action) {
  openPortalExplanationAction(router, action)
}

function blockReadOnlyAction(actionLabel) {
  const prefix = readOnlyRoleLabel.value || '当前角色'
  window?.$modal
  return `${prefix}当前仅保留摘要、日志和导出，不能${actionLabel}`
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
    return label
  }
  return fallback || ''
}

const page = useDevicePage({
  exportFilePrefix: 'azb_device',
  canMutate: () => !isReadOnlyRole.value,
  onBlockedAction: actionLabel => {
    const message = blockReadOnlyAction(actionLabel)
    const modal = window?.$modal
    if (modal?.msgWarning) {
      modal.msgWarning(message)
      return
    }
  },
  immediate: false
})

const {
  deviceList,
  enterpriseOptions,
  personOptions,
  commandLogList,
  deviceEventList,
  summaryData,
  open,
  authorizeOpen,
  heartbeatOpen,
  aiOpen,
  loading,
  showSearch,
  single,
  multiple,
  total,
  title,
  currentDevice,
  activeTraceTab,
  queryParams,
  form,
  rules,
  authorizeForm,
  authorizeRules,
  heartbeatForm,
  aiForm,
  regionOptions,
  authorizeDeviceName,
  traceDeviceName,
  cancel,
  getList,
  handleAdd,
  handleDelete,
  handleExport,
  handleLock,
  handleQuery,
  handleRowClick,
  handleSelectionChange,
  handleUnlock,
  handleUpdate,
  openAiDialog,
  openAuthorizeDialog,
  openHeartbeatDialog,
  resetQuery: pageResetQuery,
  submitAiEvent,
  submitAuthorize,
  submitForm,
  submitHeartbeat,
  init
} = page

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
  if (roleView.value === 'site-enterprise') return '现场设备负责人'
  return '应急监管处置'
})

const roleTitle = computed(() => {
  if (roleView.value === 'bank') return '设备风险与区域态势复核台账'
  if (roleView.value === 'insurer') return '设备感知风险与事故预防协同台账'
  if (roleView.value === 'site-enterprise') return '现场设备与授权处置工作台'
  return '设备安全与现场联动工作台'
})

const roleDescription = computed(() => {
  if (roleView.value === 'bank') {
    return '围绕区域离线设备、故障设备和设备风险密度做只读复核，用于辅助信用协同判断。'
  }
  if (roleView.value === 'insurer') {
    return '围绕 AI 摄像头、故障设备和事件来源识别事故预防服务重点，不承接现场指令动作。'
  }
  if (roleView.value === 'site-enterprise') {
    return '围绕设备在线、授权通过率和现场异常事件组织现场负责人工作流，重点是快速补授权、补心跳和核查 AI 风险。'
  }
  return '围绕离线设备、授权拒绝、锁机故障和 AI 事件组织应急监管工作流，优先压降现场感知缺口。'
})

function summaryCard(key, label, value, unit, note, cardClass = '') {
  return { key, label, value: value ?? 0, unit, note, cardClass }
}

function focusQueue(key, title, count, unit, desc, actionText) {
  return { key, title, count: count ?? 0, unit, desc, actionText }
}

const offlineCount = computed(() => {
  const totalCount = Number(summaryData.value.totalCount || 0)
  const onlineCount = Number(summaryData.value.onlineCount || 0)
  return Math.max(totalCount - onlineCount, 0)
})

const summaryCards = computed(() => {
  if (roleView.value === 'bank') {
    return [
      summaryCard('total', '设备总量', summaryData.value.totalCount, '台', '当前筛选条件下纳入台账的设备总量。'),
      summaryCard('fault', '故障/锁定', summaryData.value.lockedOrFaultCount, '台', '用于判断区域现场设备风险密度。', 'azb-device-card--danger'),
      summaryCard('unauthorized', '未授权设备', summaryData.value.unauthorizedCount, '台', '用于判断现场设备准入完整度。', 'azb-device-card--warning'),
      summaryCard('online', '在线设备', summaryData.value.onlineCount, '台', '作为区域设备稳定度参考。', 'azb-device-card--success')
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      summaryCard('aiCamera', 'AI摄像头', summaryData.value.aiCameraCount, '台', 'AI 设备越集中，越适合优先开展事故预防服务。'),
      summaryCard('fault', '故障/锁定', summaryData.value.lockedOrFaultCount, '台', '故障与锁机设备更可能削弱风险感知。', 'azb-device-card--danger'),
      summaryCard('authDenied', '授权拒绝', summaryData.value.authDeniedCount, '台', '说明证书核验或授权链路存在阻塞。', 'azb-device-card--warning'),
      summaryCard('online', '在线设备', summaryData.value.onlineCount, '台', '用于判断现场在线感知覆盖面。', 'azb-device-card--success')
    ]
  }
  if (roleView.value === 'site-enterprise') {
    return [
      summaryCard('online', '在线设备', summaryData.value.onlineCount, '台', '现场负责人优先看还能正常上传状态的设备。', 'azb-device-card--success'),
      summaryCard('unauthorized', '未授权设备', summaryData.value.unauthorizedCount, '台', '优先补授权，避免人员无法进场或设备无法回写。', 'azb-device-card--warning'),
      summaryCard('authDenied', '授权拒绝', summaryData.value.authDeniedCount, '台', '被拒设备需要回到人员和证书重新核验。', 'azb-device-card--danger'),
      summaryCard('aiCamera', 'AI摄像头', summaryData.value.aiCameraCount, '台', '现场 AI 设备是作业风险核查的第一入口。')
    ]
  }
  return [
    summaryCard('total', '设备总量', summaryData.value.totalCount, '台', '当前筛选条件下的设备总量。'),
    summaryCard('online', '在线设备', summaryData.value.onlineCount, '台', '当前筛选条件下处于在线状态的设备数量。', 'azb-device-card--success'),
    summaryCard('fault', '故障/锁定', summaryData.value.lockedOrFaultCount, '台', '优先核实锁机与故障设备状态。', 'azb-device-card--warning'),
    summaryCard('authDenied', '授权拒绝', summaryData.value.authDeniedCount, '台', '需要补证书核验或人员授权闭环。', 'azb-device-card--danger')
  ]
})

const resolvedSummaryCards = computed(() => summaryCards.value.map((item, index) => ({
  ...item,
  note: resolveExplanationFirstText(item.note, resolvePortalExplanationItem(index))
})))

const focusQueues = computed(() => {
  if (roleView.value === 'bank') {
    return [
      focusQueue('fault', '故障与锁定设备', summaryData.value.lockedOrFaultCount, '台', '先看现场感知是否存在明显缺口。', '查看故障对象'),
      focusQueue('unauthorized', '未授权设备', summaryData.value.unauthorizedCount, '台', '用于判断区域设备准入完整度。', '查看准入情况'),
      focusQueue('offline', '离线设备缺口', offlineCount.value, '台', '离线越多，区域设备治理稳定度越弱。', '查看离线对象')
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      focusQueue('ai', 'AI 感知设备', summaryData.value.aiCameraCount, '台', '优先看能承接事故预防服务的 AI 设备分布。', '查看 AI 设备'),
      focusQueue('fault', '故障与锁定设备', summaryData.value.lockedOrFaultCount, '台', '故障设备更值得联动事故预防治理。', '查看故障设备'),
      focusQueue('authDenied', '授权拒绝设备', summaryData.value.authDeniedCount, '台', '授权被拒说明现场准入或证书存在风险。', '查看授权结果')
    ]
  }
  if (roleView.value === 'site-enterprise') {
    return [
      focusQueue('unauthorized', '未授权设备', summaryData.value.unauthorizedCount, '台', '先把不能正常授权的设备找出来。', '进入授权处理'),
      focusQueue('fault', '故障与锁定设备', summaryData.value.lockedOrFaultCount, '台', '这些设备最容易阻断现场回写和作业核查。', '查看故障状态'),
      focusQueue('ai', 'AI 事件感知设备', summaryData.value.aiCameraCount, '台', '现场负责人需要优先核查 AI 设备的异常回写。', '查看 AI 设备')
    ]
  }
  return [
    focusQueue('fault', '故障与锁定设备', summaryData.value.lockedOrFaultCount, '台', '故障和锁机设备优先纳入现场处置链路。', '进入设备处置'),
    focusQueue('authDenied', '授权拒绝设备', summaryData.value.authDeniedCount, '台', '授权被拒说明准入链路存在明显风险。', '查看授权拒绝'),
    focusQueue('unauthorized', '未授权设备', summaryData.value.unauthorizedCount, '台', '未授权设备越多，现场感知越不完整。', '进入授权链路'),
    focusQueue('offline', '离线设备', offlineCount.value, '台', '需要持续压降离线和失联设备缺口。', '查看离线对象')
  ]
})

const activeFocusKey = computed({
  get: () => queryParams.value.__focusKey || focusQueues.value[0]?.key || '',
  set: value => {
    queryParams.value.__focusKey = value
  }
})

const resolvedFocusQueues = computed(() => focusQueues.value.map((item, index) => ({
  ...item,
  value: `${item.count ?? 0}${item.unit || ''}`,
  tip: resolveExplanationFirstText(item.desc, resolvePortalExplanationItem(index))
})))

const activeFocus = computed(() => {
  if (!focusQueues.value.length) {
    return null
  }
  return focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0]
})

function matchDeviceFocus(row, focusKey) {
  if (!row || !focusKey) {
    return false
  }
  if (focusKey === 'fault') return ['2', '3'].includes(String(row.deviceStatus || ''))
  if (focusKey === 'unauthorized') return String(row.authStatus || '') === '0'
  if (focusKey === 'authDenied') return String(row.authStatus || '') === '2'
  if (focusKey === 'offline') return String(row.deviceStatus || '') === '0'
  if (focusKey === 'ai') return String(row.deviceType || '') === '3'
  return false
}

const visibleDeviceList = computed(() => {
  if (deviceList.value.length <= 1) {
    return deviceList.value
  }
  const matched = []
  const others = []
  deviceList.value.forEach(row => {
    if (matchDeviceFocus(row, activeFocus.value?.key)) {
      matched.push(row)
      return
    }
    others.push(row)
  })
  return [...matched, ...others]
})

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: deviceWorkbenchFields,
  sourceLabel: '设备工作台',
  title: '当前设备台账沿用了工作台来源条件',
  description: '已按企业或行政区划范围锁定设备数据，适合继续处理授权、在线状态和事件回写。',
  fieldLabels: {
    enterpriseId: '企业',
    regionCode: '行政区划'
  },
  fieldFormatters: {
    enterpriseId: value => {
      const matched = enterpriseOptions.value.find(item => String(item.enterpriseId) === String(value))
      return matched ? matched.enterpriseName : value
    },
    regionCode: value => formatRegionName(value, value)
  }
}))

const submoduleEntries = computed(() => buildDeviceSubviewEntries('azb', () => buildDeviceSubmoduleQuery()))

const focusTableHint = computed(() => {
  if (!activeFocus.value) {
    return '按当前筛选条件展示设备台账。'
  }
  return `当前焦点为“${activeFocus.value.title}”，已把对应高风险对象优先排到表格前列。`
})

const selectedDeviceOverview = computed(() => {
  if (currentDevice.value) {
    return [
      { label: '设备编码', value: currentDevice.value.deviceCode || '-' },
      { label: '所属区域', value: formatRegionName(currentDevice.value.regionCode, '-') },
      { label: '授权状态', value: optionLabel(authStatusOptions, currentDevice.value.authStatus) },
      { label: '最近心跳', value: currentDevice.value.lastHeartbeat ? new Date(currentDevice.value.lastHeartbeat).toLocaleString() : '-' }
    ]
  }
  return [
    { label: '当前焦点', value: activeFocus.value?.title || '-' },
    { label: '焦点数量', value: `${activeFocus.value?.count ?? 0}${activeFocus.value?.unit || ''}` },
    { label: '当前区域', value: formatRegionName(queryParams.value.regionCode, '全部区域') },
    { label: '下一步', value: activeFocus.value?.actionText || '-' }
  ]
})

const primaryDeviceAction = computed(() => {
  if (!currentDevice.value) {
    return { label: activeFocus.value?.actionText || '查看指令日志', action: 'command' }
  }
  if (isReadOnlyRole.value) {
    if (roleView.value === 'bank') return { label: '查看设备事件', action: 'event' }
    if (roleView.value === 'insurer') return { label: '查看 AI 事件', action: 'event' }
    return { label: '查看指令日志', action: 'command' }
  }
  if (String(currentDevice.value.authStatus || '') === '0') return { label: '进入授权', action: 'authorize' }
  if (String(currentDevice.value.authStatus || '') === '2') return { label: '重新授权', action: 'authorize' }
  if (['0', '3'].includes(String(currentDevice.value.deviceStatus || ''))) return { label: '模拟心跳回写', action: 'heartbeat' }
  if (String(currentDevice.value.deviceStatus || '') === '2') return { label: '执行解锁', action: 'unlock' }
  if (String(currentDevice.value.deviceType || '') === '3') return { label: '上报 AI 事件', action: 'ai' }
  return { label: '查看指令日志', action: 'command' }
})

const currentDeviceActionSummary = computed(() => {
  if (!currentDevice.value) {
    return activeFocus.value ? `当前已按“${activeFocus.value.title}”重排台账，建议先点选前列对象，再进入日志或指令闭环。` : '当前暂无设备对象。'
  }
  if (isReadOnlyRole.value) {
    if (roleView.value === 'bank') {
      return '当前角色只做只读协同，重点看区域设备风险密度、离线缺口和事件回写，不承接远程指令。'
    }
    return '当前角色只做只读协同，重点看 AI 事件、故障锁机和授权异常是否已经形成稳定回写。'
  }
  if (String(currentDevice.value.authStatus || '') === '0') {
    return '该设备仍未授权，优先补人员授权和证书信息，再继续确认现场回写是否恢复。'
  }
  if (String(currentDevice.value.authStatus || '') === '2') {
    return '该设备授权被拒，优先回到人员、证书和企业准入信息做复核，再重新发起授权。'
  }
  if (['0', '3'].includes(String(currentDevice.value.deviceStatus || ''))) {
    return '该设备当前离线或故障，优先通过心跳回写确认是否恢复在线，再查看事件日志是否继续异常。'
  }
  if (String(currentDevice.value.deviceType || '') === '3') {
    return '该设备为 AI 摄像头，优先核对最近 AI 事件和现场回写，再决定是否继续上报风险对象。'
  }
  return '该设备当前已具备基础在线条件，建议继续回看指令日志和设备事件，确认现场链路是否稳定。'
})

const currentDeviceActionTags = computed(() => {
  const tags = []
  if (!currentDevice.value) {
    if (activeFocus.value) {
      tags.push({ label: `当前焦点：${activeFocus.value.title}`, type: 'info' })
      tags.push({ label: `优先动作：${activeFocus.value.actionText}`, type: 'warning' })
    }
    return tags
  }
  if (String(currentDevice.value.deviceStatus || '') === '0') {
    tags.push({ label: '设备离线，建议先看事件日志确认失联原因', type: 'warning' })
  }
  if (String(currentDevice.value.deviceStatus || '') === '2') {
    tags.push({ label: '设备已锁定，需要确认是否应执行解锁', type: 'danger' })
  }
  if (String(currentDevice.value.deviceStatus || '') === '3') {
    tags.push({ label: '设备故障，建议优先做心跳回写复核', type: 'danger' })
  }
  if (String(currentDevice.value.authStatus || '') === '0') {
    tags.push({ label: '未授权，建议先补授权链路', type: 'warning' })
  }
  if (String(currentDevice.value.authStatus || '') === '2') {
    tags.push({ label: '授权拒绝，建议复核证书和人员信息', type: 'danger' })
  }
  if (String(currentDevice.value.deviceType || '') === '3') {
    tags.push({ label: 'AI设备，事件回写优先级高于普通日志', type: 'info' })
  }
  if (!tags.length) {
    tags.push({ label: '当前设备状态稳定，可回看最近指令与事件记录', type: 'success' })
  }
  return tags
})

const workflowSteps = computed(() => {
  if (roleView.value === 'bank') {
    return [
      { label: '先看故障与离线规模', desc: '用故障、锁定和离线设备判断区域风险密度。' },
      { label: '再看未授权缺口', desc: '通过未授权设备判断现场准入是否完整。' },
      { label: '最后回到区域协同', desc: '必要时再联动企业、信用和报表页面做复核。' }
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      { label: '先看 AI 设备覆盖', desc: 'AI 设备越集中，越适合优先开展事故预防服务。' },
      { label: '再看故障与授权异常', desc: '故障和授权拒绝说明现场感知和准入链路存在风险。' },
      { label: '最后回到日志复核', desc: '通过事件日志和指令日志判断是否需要继续协同。' }
    ]
  }
  if (roleView.value === 'site-enterprise') {
    return [
      { label: '先补授权', desc: '优先消化未授权和授权拒绝设备。' },
      { label: '再补在线回写', desc: '通过心跳和设备状态把离线、故障设备拉回可用状态。' },
      { label: '最后核查 AI 事件', desc: '回到事件日志确认现场是否还存在持续异常。' }
    ]
  }
  return [
    { label: '先锁定故障与离线设备', desc: '现场感知缺口优先于普通台账维护。' },
    { label: '再看授权链路', desc: '授权拒绝和未授权设备是准入风险的直接入口。' },
    { label: '最后进入指令与事件闭环', desc: '结合锁机、解锁、心跳和 AI 事件继续压实处理结果。' }
  ]
})

const resolvedWorkflowSteps = computed(() => {
  const aggregateSteps = portalExplanationItems.value
    .filter(item => item?.summary || item?.explanationSummary || item?.sourceDescription)
    .slice(0, 4)
    .map(item => ({
      label: item.dimensionName || item.moduleLabel || item.moduleCode || '6.1 Explanation',
      desc: item.summary || item.explanationSummary || item.sourceDescription || ''
    }))
  return aggregateSteps.length
    ? aggregateSteps
    : workflowSteps.value.map((item, index) => ({
        ...item,
        desc: resolveExplanationFirstText(item.desc, resolvePortalExplanationItem(index))
      }))
})

const deviceHintTags = computed(() => {
  const tags = []
  if (Number(summaryData.value.lockedOrFaultCount || 0) > 0) {
    tags.push({ label: `当前有 ${summaryData.value.lockedOrFaultCount || 0} 台故障/锁定设备需要优先核实`, type: 'danger' })
  }
  if (Number(summaryData.value.unauthorizedCount || 0) > 0) {
    tags.push({ label: `仍有 ${summaryData.value.unauthorizedCount || 0} 台未授权设备影响现场准入`, type: 'warning' })
  }
  if (Number(summaryData.value.authDeniedCount || 0) > 0) {
    tags.push({ label: `已有 ${summaryData.value.authDeniedCount || 0} 台设备授权被拒，建议复核证书和人员`, type: 'warning' })
  }
  if (Number(summaryData.value.aiCameraCount || 0) > 0 && roleView.value !== 'bank') {
    tags.push({ label: `AI 摄像头 ${summaryData.value.aiCameraCount || 0} 台，适合优先查看事件回写`, type: 'info' })
  }
  return tags
})

const tracePanelHint = computed(() => {
  if (!currentDevice.value) {
    return ''
  }
  return `当前日志范围：最近 5 条 Stub 回写记录，默认优先定位到${activeTraceTab.value === 'event' ? '设备事件' : '指令日志'}。`
})

const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}：当前仅保留设备摘要、日志联动和导出`)
const readOnlyAlertDescription = computed(() => {
  if (isBankRole.value) {
    return `${readOnlyRoleDescription.value} 当前页面聚焦区域设备风险密度和离线缺口复核。`
  }
  if (isInsurerRole.value) {
    return `${readOnlyRoleDescription.value} 当前页面聚焦 AI 设备、故障设备和授权异常复核。`
  }
  return `${readOnlyRoleDescription.value} 当前页面聚焦设备在线状态、授权结果和最近事件复核。`
})

watchEffect(() => {
  setPageGuide({
    title: roleTitle.value || '设备治理中台',
    description: roleDescription.value || '统一查看当前页的角色焦点、当前选中、治理路径与风险提示。',
    portalExplanation: portalExplanationItems.value,
    focus: resolvedFocusQueues.value,
    selection: [
      ...selectedDeviceOverview.value,
      { label: '当前处置建议', value: currentDeviceActionSummary.value }
    ],
    workflow: resolvedWorkflowSteps.value,
    hints: [...currentDeviceActionTags.value, ...deviceHintTags.value].slice(0, 6)
  })
})

function optionLabel(options, value, fallback = '-') {
  const matched = options.find(item => item.value === value)
  return matched ? matched.label : fallback
}

function focusCurrentTrace(tab) {
  activeTraceTab.value = tab
}

function buildDeviceSubmoduleQuery(extraQuery = {}) {
  return Object.fromEntries(Object.entries({
    regionCode: queryParams.value.regionCode,
    enterpriseId: currentDevice.value?.enterpriseId || queryParams.value.enterpriseId,
    deviceCode: currentDevice.value?.deviceCode || queryParams.value.deviceCode,
    deviceName: currentDevice.value?.deviceName || queryParams.value.deviceName,
    deviceStatus: queryParams.value.deviceStatus,
    authStatus: queryParams.value.authStatus,
    ...extraQuery
  }).filter(([, value]) => value !== undefined && value !== null && value !== ''))
}

function openSubmodule(entry) {
  openDeviceSubview(proxy, entry)
}

function handlePrimaryDeviceAction() {
  if (!currentDevice.value) {
    return
  }
  const action = primaryDeviceAction.value.action
  if (action === 'authorize') {
    if (isReadOnlyRole.value) {
      const message = blockReadOnlyAction('执行设备授权')
      const modal = window?.$modal
      if (modal?.msgWarning) {
        modal.msgWarning(message)
      } else {
        proxy?.$modal?.msgWarning?.(message)
      }
      return
    }
    openAuthorizeDialog(currentDevice.value)
    return
  }
  if (action === 'heartbeat') {
    if (isReadOnlyRole.value) {
      const message = blockReadOnlyAction('执行心跳回写')
      const modal = window?.$modal
      if (modal?.msgWarning) {
        modal.msgWarning(message)
      } else {
        proxy?.$modal?.msgWarning?.(message)
      }
      return
    }
    openHeartbeatDialog(currentDevice.value)
    return
  }
  if (action === 'ai') {
    openAiDialog(currentDevice.value)
    return
  }
  if (action === 'unlock') {
    if (isReadOnlyRole.value) {
      const message = blockReadOnlyAction('执行设备解锁')
      const modal = window?.$modal
      if (modal?.msgWarning) {
        modal.msgWarning(message)
      } else {
        proxy?.$modal?.msgWarning?.(message)
      }
      return
    }
    handleUnlock(currentDevice.value)
    return
  }
  focusCurrentTrace(action === 'event' ? 'event' : 'command')
}

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
}

function resetQuery() {
  pageResetQuery()
  applyWorkbenchRouteQuery(route.query, queryParams.value, deviceWorkbenchFields)
  getList()
}

function clearWorkbenchContext() {
  Object.assign(queryParams.value, {
    pageNum: 1,
    enterpriseId: undefined,
    regionCode: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, deviceWorkbenchFields)
  })
  getList()
}

applyWorkbenchRouteQuery(route.query, queryParams.value, deviceWorkbenchFields)
init()
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

.azb-device-page__summary,
.azb-device-page__grid {
  display: grid;
  gap: 16px;
  margin-bottom: 16px;
}

.azb-device-page__summary {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.azb-device-page__grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.azb-device-page__eyebrow {
  margin: 0 0 8px;
  color: #0b6b78;
  font-size: 13px;
  font-weight: 600;
}

.azb-device-page__title {
  margin: 0;
  color: #0f172a;
  font-size: 28px;
}

.azb-device-page__desc {
  margin: 12px 0 0;
  color: #475569;
  line-height: 1.8;
}

.azb-device-page__tips {
  display: grid;
  gap: 10px;
}

.azb-device-page__tip {
  border: 1px solid #d9edf1;
  border-radius: 14px;
  background: #f3fcfd;
  padding: 12px 14px;
  color: #476a72;
  line-height: 1.7;
}

.azb-device-card,
.azb-device-panel {
  border: 1px solid #dbe7f3;
  border-radius: 16px;
  background: #fff;
}

.azb-device-card {
  padding: 18px 20px;
}

.azb-device-card__label {
  color: #64748b;
  font-size: 13px;
}

.azb-device-card__value {
  margin-top: 10px;
  color: #0f172a;
  font-size: 28px;
  font-weight: 700;
}

.azb-device-card__unit {
  margin-left: 4px;
  color: #64748b;
  font-size: 13px;
  font-weight: 500;
}

.azb-device-card__note {
  margin-top: 10px;
  color: #64748b;
  font-size: 13px;
  line-height: 1.7;
}

.azb-device-card--success {
  background: linear-gradient(180deg, #ffffff 0%, #f1fbf5 100%);
}

.azb-device-card--warning {
  background: linear-gradient(180deg, #ffffff 0%, #fff9ef 100%);
}

.azb-device-card--danger {
  background: linear-gradient(180deg, #ffffff 0%, #fff3f3 100%);
}

.azb-device-panel :deep(.el-card__header) {
  padding: 18px 20px 0;
  border-bottom: none;
}

.azb-device-panel :deep(.el-card__body) {
  padding: 18px 20px 20px;
}

.azb-device-panel__head--between {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.azb-device-panel__title {
  color: #0f172a;
  font-size: 18px;
  font-weight: 700;
}

.azb-device-panel__desc,
.azb-device-panel__extra {
  margin-top: 4px;
  color: #64748b;
  font-size: 13px;
  line-height: 1.7;
}

.azb-device-focus-list,
.azb-device-kpi-list,
.azb-device-step-list {
  display: grid;
  gap: 12px;
}

.azb-device-focus {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  width: 100%;
  padding: 14px 16px;
  border: 1px solid #dbe7f3;
  border-radius: 12px;
  background: #f8fbff;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.azb-device-focus:hover,
.azb-device-focus.is-active {
  border-color: #0b6b78;
  box-shadow: 0 10px 24px rgba(11, 107, 120, 0.08);
}

.azb-device-focus__main strong {
  display: block;
  color: #0f172a;
}

.azb-device-focus__main p {
  margin: 8px 0 0;
  color: #64748b;
  font-size: 13px;
  line-height: 1.7;
}

.azb-device-focus__side {
  min-width: 120px;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: center;
  gap: 8px;
}

.azb-device-focus__count {
  color: #0b6b78;
  font-size: 20px;
  font-weight: 700;
}

.azb-device-focus__action {
  color: #1d4ed8;
  font-size: 12px;
}

.azb-device-kpi {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
  border: 1px solid #dbe7f3;
  border-radius: 12px;
  background: #f8fbff;
}

.azb-device-kpi__label {
  color: #64748b;
  font-size: 13px;
}

.azb-device-kpi__value {
  color: #0f172a;
  font-size: 14px;
  font-weight: 600;
  text-align: right;
}

.azb-device-recommend {
  margin-top: 16px;
  padding: 14px 16px;
  border: 1px solid #dbe7f3;
  border-radius: 12px;
  background: #f8fbff;
}

.azb-device-recommend__title {
  color: #0f172a;
  font-size: 13px;
  font-weight: 600;
}

.azb-device-recommend__summary {
  margin-top: 8px;
  color: #475569;
  font-size: 13px;
  line-height: 1.7;
}

.azb-device-actions,
.azb-device-tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.azb-link-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.azb-link-item {
  padding: 16px;
  border: 1px solid #dbe7f3;
  border-radius: 14px;
  background: #f8fbff;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
}

.azb-link-item:hover {
  border-color: #69aeb8;
  box-shadow: 0 8px 20px rgba(11, 107, 120, 0.08);
  transform: translateY(-1px);
}

.azb-link-item__title {
  color: #0f172a;
  font-size: 14px;
  font-weight: 700;
}

.azb-link-item__desc {
  margin-top: 8px;
  color: #64748b;
  font-size: 13px;
  line-height: 1.7;
}

.azb-link-item__action {
  margin-top: 12px;
  color: #0b6b78;
  font-size: 12px;
  font-weight: 600;
}

.azb-device-actions {
  margin-top: 16px;
}

.azb-device-step {
  display: grid;
  grid-template-columns: 42px minmax(0, 1fr);
  gap: 14px;
  padding: 16px;
  border: 1px solid #dbe7f3;
  border-radius: 12px;
  background: #f8fbff;
}

.azb-device-step__index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 34px;
  border-radius: 10px;
  background: #def2f4;
  color: #0b6b78;
  font-weight: 700;
}

.azb-device-step__body p {
  margin: 8px 0 0;
  color: #64748b;
  font-size: 13px;
  line-height: 1.7;
}

.azb-device-form {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0 16px;
}

@media (max-width: 1200px) {
  .azb-device-page__summary,
  .azb-device-page__grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .azb-link-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 900px) {
  .azb-device-form {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .azb-device-page__summary,
  .azb-device-page__grid {
    grid-template-columns: 1fr;
  }

  .azb-link-grid {
    grid-template-columns: 1fr;
  }

  .azb-device-panel__head--between,
  .azb-device-focus {
    flex-direction: column;
  }

  .azb-device-focus__side {
    align-items: flex-start;
  }
}
</style>
