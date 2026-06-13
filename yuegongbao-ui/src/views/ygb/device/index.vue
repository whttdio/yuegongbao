<template>
  <div class="app-container ygb-page ygb-device-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-device-page__eyebrow">设备办理台账</p>
        <h1 class="ygb-device-page__title">设备接入与授权工作台</h1>
        <p class="ygb-device-page__desc">
          面向企业管理员、现场负责人和监管经办，统一维护设备建档、人员授权、在线回写和异常留痕。
          当前重点不是做现场态势看板，而是把设备接入、授权核验、心跳回写和 AI 事件纳入一条可追溯的办理链路。
        </p>
      </div>
      <div class="ygb-device-page__tip">
        设备授权继续联动 `EmergencyCertClient + SocialClient` Stub 口径，锁机、解锁、心跳和 AI 事件当前只回写日志，不接真实 MQTT。
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
      <div class="ygb-device-tag-list" style="margin-top: 10px;">
        <el-tag v-for="item in workbenchContext.tags" :key="item.key" effect="plain">{{ item.label }}：{{ item.value }}</el-tag>
      </div>
    </el-alert>

    <div class="ygb-device-page__summary">
      <div v-for="item in resolvedSummaryCards" :key="item.key" class="ygb-device-card" :class="item.cardClass">
        <div class="ygb-device-card__label">{{ item.label }}</div>
        <div class="ygb-device-card__value">
          {{ item.value }}
          <span class="ygb-device-card__unit">{{ item.unit }}</span>
        </div>
        <div class="ygb-device-card__note">{{ item.note }}</div>
      </div>
    </div>

    <el-card class="ygb-device-panel" shadow="never" style="margin-bottom: 16px;">
      <template #header>
        <div class="ygb-device-panel__head">
          <div class="ygb-device-panel__title">设备子视图</div>
          <div class="ygb-device-panel__desc">从统一设备台账直接下钻考勤设备、芯片设备、AI 设备、物联卡、芯片库存、电子围栏和拆卸报警。</div>
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
      :title="`${readOnlyRoleLabel}：当前页面仅保留设备台账、日志联动和导出`"
      :description="readOnlyRoleDescription || '设备建档、锁机、解锁、授权、心跳和 AI 事件入口已自动隐藏。'"
      type="info"
      :closable="false"
      show-icon
      style="margin-bottom: 16px;"
    />

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
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['ygb:device:add']">新增设备</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['ygb:device:edit']">修改设备</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['ygb:device:remove']">删除设备</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:device:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card" shadow="never">
      <el-table v-loading="loading" :data="deviceList" @selection-change="handleSelectionChange" @row-click="handleRowClick">
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

    <el-card class="table-card ygb-device-panel" shadow="never">
      <template #header>
        <div class="ygb-device-panel__head ygb-device-panel__head--between">
          <div>
            <div class="ygb-device-panel__title">{{ currentDevice ? currentDevice.deviceName : '日志联动' }}</div>
            <div class="ygb-device-panel__desc">
              {{ currentDevice ? `${currentDevice.deviceCode} / ${currentDevice.enterpriseName || '-'} / ${formatRegionName(currentDevice.regionCode, '-')}` : '点击设备行后，联动查看当前设备的指令日志和设备事件。' }}
            </div>
          </div>
          <div v-if="currentDevice" class="ygb-device-panel__extra">当前日志范围：最近 5 条 Stub 回写记录</div>
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
        <div class="ygb-device-form">
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

<script setup name="YgbDevice">
import { computed, getCurrentInstance, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import { useRoleViewMode } from '@/utils/roleView'
import { decoratePortalExplanationItems, openPortalExplanationAction, resolvePortalExplanationSummary } from '@/utils/portalExplanation'
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

const { proxy } = getCurrentInstance()
const route = useRoute()
const router = useRouter()
const { setPageGuide } = useWorkbenchAssist()
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()

function blockReadOnlyAction(actionLabel) {
  proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}当前仅支持查看，不能${actionLabel}`)
}

const workflowSteps = [
  { label: '建档接入', desc: '先完成设备编码、所属企业、安装位置和设备类型建档，形成设备接入底账。' },
  { label: '人员授权', desc: '按企业和人员关系发起设备授权，保留证书核验和授权结果回写。' },
  { label: '在线回写', desc: '通过模拟心跳和事件回写确认设备在线、事件上报和状态同步能力。' },
  { label: '异常留痕', desc: '对授权拒绝、离线故障和 AI 异常留痕归档，形成可追溯的设备办理记录。' }
]

const page = useDevicePage({
  exportFilePrefix: 'ygb_device',
  canMutate: () => !isReadOnlyRole.value,
  onBlockedAction: blockReadOnlyAction,
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
  resetQuery: baseResetQuery,
  submitAiEvent,
  submitAuthorize,
  submitForm,
  submitHeartbeat,
  formatDateTime,
  init
} = page

function buildDeviceExplanationQuery(extraQuery = {}) {
  return {
    regionCode: queryParams.value.regionCode,
    enterpriseId: queryParams.value.enterpriseId,
    ...extraQuery
  }
}

const fallbackPortalExplanations = computed(() => ([
  {
    key: 'unauthorized',
    dimensionName: '未授权设备',
    currentValue: valueOrDefault(summaryData.value.unauthorizedCount, 0),
    targetValue: '0',
    summary: '未授权设备无法稳定承接人员和现场数据，应先补授权再进入后续办理链路。',
    evidenceModule: 'device',
    recommendModule: 'device',
    defaultQuery: buildDeviceExplanationQuery({ authStatus: '0' }),
    sourceLabel: '530.1 设备办理解释',
    sourceDescription: '从设备台账继续核查未授权对象。'
  },
  {
    key: 'authDenied',
    dimensionName: '授权拒绝',
    currentValue: valueOrDefault(summaryData.value.authDeniedCount, 0),
    targetValue: '0',
    summary: '授权拒绝会直接中断设备接入链，应优先回看证书校验和授权结果留痕。',
    evidenceModule: 'device',
    recommendModule: 'device',
    defaultQuery: buildDeviceExplanationQuery({ authStatus: '2' }),
    sourceLabel: '530.1 设备办理解释',
    sourceDescription: '从设备台账继续核查授权拒绝对象。'
  },
  {
    key: 'fault',
    dimensionName: '锁定/故障设备',
    currentValue: valueOrDefault(summaryData.value.lockedOrFaultCount, 0),
    targetValue: '0',
    summary: '锁定和故障设备会影响考勤采集、心跳回写和异常留痕，应优先恢复可用状态。',
    evidenceModule: 'device',
    recommendModule: 'device',
    defaultQuery: buildDeviceExplanationQuery({ deviceStatus: '3' }),
    sourceLabel: '530.1 设备办理解释',
    sourceDescription: '从设备台账继续核查锁定或故障对象。'
  },
  {
    key: 'aiCamera',
    dimensionName: 'AI 摄像头',
    currentValue: valueOrDefault(summaryData.value.aiCameraCount, 0),
    targetValue: '持续在线',
    summary: 'AI 摄像头是现场异常留痕的重要采集点，应单独跟踪接入和回写稳定性。',
    evidenceModule: 'device',
    recommendModule: 'device',
    defaultQuery: buildDeviceExplanationQuery({ deviceType: '3' }),
    sourceLabel: '530.1 设备办理解释',
    sourceDescription: '从设备台账继续核查 AI 摄像头接入情况。'
  }
]))

const portalExplanations = computed(() => {
  const aggregated = Array.isArray(summaryData.value.ygbExplanation) ? summaryData.value.ygbExplanation : []
  return aggregated.length ? aggregated : fallbackPortalExplanations.value
})

const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'ygb',
  panelTitle: '530.1 办理链说明',
  panelDescription: '设备页面按办理链解释未授权、授权拒绝、锁定故障和关键设备类型。'
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

const deviceWorkbenchFields = ['enterpriseId', 'personId', 'regionCode', 'deviceType', 'deviceStatus', 'authStatus']

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: deviceWorkbenchFields,
  sourceLabel: '企业/人员工作台',
  title: '当前设备台账沿用了上游工作台筛选',
  description: '已按企业、人员或区域范围锁定设备数据，适合继续处理授权、心跳和异常留痕。',
  fieldLabels: {
    enterpriseId: '所属企业',
    personId: '人员',
    regionCode: '行政区划',
    deviceType: '设备类型',
    deviceStatus: '设备状态',
    authStatus: '授权状态'
  },
  fieldFormatters: {
    enterpriseId: value => {
      const matched = enterpriseOptions.value.find(item => String(item.enterpriseId) === String(value))
      return matched ? matched.enterpriseName : value
    },
    personId: value => {
      const matched = personOptions.value.find(item => String(item.personId) === String(value))
      return matched ? matched.personName : value
    },
    regionCode: value => formatRegionName(value, value),
    deviceType: value => optionLabel(deviceTypeOptions, value, value),
    deviceStatus: value => optionLabel(deviceStatusOptions, value, value),
    authStatus: value => optionLabel(authStatusOptions, value, value)
  }
}))

const summaryCards = computed(() => ([
  {
    key: 'total',
    label: '设备总量',
    value: valueOrDefault(summaryData.value.totalCount, total.value),
    unit: '台',
    note: '当前筛选范围内已建档设备总量。',
    cardClass: ''
  },
  {
    key: 'online',
    label: '在线设备',
    value: valueOrDefault(summaryData.value.onlineCount, 0),
    unit: '台',
    note: '用于快速判断当前设备在线回写是否连续。',
    cardClass: 'ygb-device-card--success'
  },
  {
    key: 'authDenied',
    label: '授权异常',
    value: valueOrDefault(summaryData.value.authDeniedCount, 0),
    unit: '台',
    note: '建议优先回查人员授权和证书核验结果。',
    cardClass: 'ygb-device-card--warning'
  },
  {
    key: 'fault',
    label: '锁定/故障',
    value: valueOrDefault(summaryData.value.lockedOrFaultCount, 0),
    unit: '台',
    note: '需要区分是主动锁机留痕还是设备故障导致离线。',
    cardClass: 'ygb-device-card--primary'
  }
]))

const resolvedSummaryCards = computed(() => summaryCards.value.map((item, index) => ({
  ...item,
  note: resolveExplanationFirstText(item.note, resolvePortalExplanationItem(index))
})))

const focusItems = computed(() => ([
  { label: '当前区域', value: queryParams.value.regionCode ? formatRegionName(queryParams.value.regionCode, '全部区域') : '全部区域' },
  { label: '未授权设备', value: `${valueOrDefault(summaryData.value.unauthorizedCount, 0)} 台` },
  { label: '芯片设备', value: `${valueOrDefault(summaryData.value.chipDeviceCount, 0)} 台` },
  { label: 'AI摄像头', value: `${valueOrDefault(summaryData.value.aiCameraCount, 0)} 台` }
]))

const resolvedFocusItems = computed(() => focusItems.value.map((item, index) => {
  const explanation = resolvePortalExplanationItem(index)
  return {
    ...item,
    tip: explanation?.summary || explanation?.explanationSummary || explanation?.sourceDescription || ''
  }
}))

const selectedDeviceOverview = computed(() => {
  if (!currentDevice.value) {
    return [
      { label: '设备编码', value: '-' },
      { label: '所属企业', value: '-' },
      { label: '授权状态', value: '-' },
      { label: '最近心跳', value: '-' }
    ]
  }
  return [
    { label: '设备编码', value: currentDevice.value.deviceCode || '-' },
    { label: '所属企业', value: currentDevice.value.enterpriseName || '-' },
    { label: '授权状态', value: optionLabel(authStatusOptions, currentDevice.value.authStatus) },
    { label: '最近心跳', value: formatDateTime(currentDevice.value.lastHeartbeat) }
  ]
})

const deviceHintTags = computed(() => buildDeviceHintTags(currentDevice.value))

const resolvedWorkflowSteps = computed(() => {
  const aggregateSteps = portalExplanationItems.value
    .filter(item => item?.summary || item?.explanationSummary || item?.sourceDescription)
    .slice(0, 4)
    .map(item => ({
      label: item.dimensionName || item.moduleLabel || item.moduleCode || '530.1 Explanation',
      desc: item.summary || item.explanationSummary || item.sourceDescription || ''
    }))
  return aggregateSteps.length ? aggregateSteps : workflowSteps
})

const resolvedDeviceHintTags = computed(() => {
  const tags = [...deviceHintTags.value]
  const explanation = leadingPortalExplanation.value
  const label = buildPortalExplanationLabel(explanation)
  if (label) {
    tags.unshift({ label: `530.1主解释：${label}`, type: 'info' })
  } else if (portalExplanationSummary.value) {
    tags.unshift({ label: portalExplanationSummary.value, type: 'info' })
  }
  return tags.slice(0, 4)
})
watchEffect(() => {
  setPageGuide({
    title: '设备接入与授权工作台',
    description: '统一查看当前页的办理焦点、当前选中、办理路径与办理提示。',
    portalExplanation: portalExplanationItems.value,
    focus: resolvedFocusItems.value,
    selection: selectedDeviceOverview.value,
    workflow: resolvedWorkflowSteps.value,
    hints: resolvedDeviceHintTags.value
  })
})

const submoduleEntries = computed(() => buildDeviceSubviewEntries('ygb', () => buildDeviceSubmoduleQuery()))
/*
const submoduleEntriesLegacy = computed(() => ([
  {
    key: 'attendance',
    title: '考勤设备',
    desc: '按考勤设备视角查看在线、授权和接入台账。',
    actionText: '进入考勤设备',
    path: '/ygb-safety/deviceAttendance',
    query: buildDeviceSubmoduleQuery()
  },
  {
    key: 'chip',
    title: '芯片设备',
    desc: '聚焦芯片设备、物联卡和授权状态。',
    actionText: '进入芯片设备',
    path: '/ygb-safety/deviceChip',
    query: buildDeviceSubmoduleQuery()
  },
  {
    key: 'ai',
    title: 'AI 设备',
    desc: '查看 AI 设备在线、授权和异常留痕基础台账。',
    actionText: '进入 AI 设备',
    path: '/ygb-safety/deviceAi',
    query: buildDeviceSubmoduleQuery()
  },
  {
    key: 'iotCard',
    title: '物联卡',
    desc: '查看设备物联卡号、归属企业和在线状态。',
    actionText: '进入物联卡台账',
    path: '/ygb-safety/deviceIotCard',
    query: buildDeviceSubmoduleQuery()
  },
  {
    key: 'chipInventory',
    title: '芯片库存',
    desc: '查看芯片编号、库存归属和授权状态。',
    actionText: '进入芯片库存',
    path: '/ygb-safety/deviceChipInventory',
    query: buildDeviceSubmoduleQuery()
  },
  {
    key: 'geofence',
    title: '电子围栏',
    desc: '维护围栏名称、区域和关联企业范围。',
    actionText: '进入电子围栏',
    path: '/ygb-safety/deviceGeofence',
    query: buildDeviceSubmoduleQuery()
  },
  {
    key: 'uninstallAlert',
    title: '拆卸报警',
    desc: '集中查看拆卸、拆改和围栏破坏相关报警。',
    actionText: '进入拆卸报警',
    path: '/ygb-safety/deviceUninstallAlert',
    query: buildDeviceSubmoduleQuery()
  }
]))
*/

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

function buildDeviceHintTags(device) {
  if (!device) {
    return [{ label: '请选择设备查看办理提示', type: 'info' }]
  }
  const tags = []
  if (String(device.authStatus || '') === '0') {
    tags.push({ label: '当前设备未授权，建议先绑定人员并发起授权核验', type: 'warning' })
  }
  if (String(device.authStatus || '') === '2') {
    tags.push({ label: '授权被拒绝，建议回查证书编号、人员归属和设备类型', type: 'danger' })
  }
  if (String(device.deviceStatus || '') === '2') {
    tags.push({ label: '设备当前处于锁定状态，建议确认是否为现场留痕锁机', type: 'warning' })
  }
  if (String(device.deviceStatus || '') === '3') {
    tags.push({ label: '设备当前处于故障状态，建议补做心跳回写和现场核查', type: 'warning' })
  }
  if (String(device.deviceStatus || '') === '0') {
    tags.push({ label: '设备当前离线，建议核对供电、网络和最近心跳时间', type: 'info' })
  }
  if (!device.lastHeartbeat) {
    tags.push({ label: '缺少最近心跳记录，建议至少保留一次在线回写留痕', type: 'warning' })
  }
  if (String(device.deviceType || '') === '1') {
    tags.push({ label: '考勤设备，建议同步关注采集连续性和考勤归集链路', type: 'success' })
  }
  if (String(device.deviceType || '') === '3') {
    tags.push({ label: 'AI摄像头，建议联动查看近期 AI 事件是否已回写', type: 'info' })
  }
  if (!tags.length) {
    tags.push({ label: '当前设备台账完整，可继续承接授权、心跳和事件留痕', type: 'success' })
  }
  return tags
}

function optionLabel(options, value, fallback = '-') {
  const matched = options.find(item => item.value === value)
  return matched ? matched.label : fallback
}

function resetQuery() {
  baseResetQuery()
  applyWorkbenchRouteQuery(route.query, queryParams.value, deviceWorkbenchFields)
  getList()
}

function clearWorkbenchContext() {
  Object.assign(queryParams.value, {
    pageNum: 1,
    enterpriseId: undefined,
    personId: undefined,
    regionCode: undefined,
    deviceType: undefined,
    deviceStatus: undefined,
    authStatus: undefined
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

.ygb-device-page__summary,
.ygb-device-page__grid {
  display: grid;
  gap: 16px;
  margin-bottom: 16px;
}

.ygb-device-page__summary {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.ygb-device-page__grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.ygb-device-page__eyebrow {
  margin: 0 0 8px;
  color: #0f5ea8;
  font-size: 13px;
  font-weight: 600;
}

.ygb-device-page__title {
  margin: 0;
  color: #13243a;
  font-size: 28px;
}

.ygb-device-page__desc {
  margin: 12px 0 0;
  color: #5f6f80;
  line-height: 1.8;
}

.ygb-device-page__tip {
  border: 1px solid #dce7f3;
  border-radius: 14px;
  background: #f6faff;
  padding: 14px 16px;
  color: #5f6f80;
  line-height: 1.8;
}

.ygb-device-card,
.ygb-device-panel {
  border: 1px solid #dbe5f0;
  border-radius: 16px;
  background: #fff;
}

.ygb-device-card {
  padding: 18px 20px;
}

.ygb-device-card__label {
  color: #627486;
  font-size: 13px;
}

.ygb-device-card__value {
  margin-top: 10px;
  color: #13243a;
  font-size: 28px;
  font-weight: 700;
}

.ygb-device-card__unit {
  margin-left: 4px;
  color: #7b8da1;
  font-size: 13px;
  font-weight: 500;
}

.ygb-device-card__note {
  margin-top: 10px;
  color: #5f6f80;
  font-size: 13px;
  line-height: 1.7;
}

.ygb-device-card--success {
  background: linear-gradient(180deg, #ffffff 0%, #f3fbf5 100%);
}

.ygb-device-card--warning {
  background: linear-gradient(180deg, #ffffff 0%, #fff9ef 100%);
}

.ygb-device-card--primary {
  background: linear-gradient(180deg, #ffffff 0%, #f2f7fd 100%);
}

.ygb-device-panel :deep(.el-card__header) {
  padding: 18px 20px 0;
  border-bottom: none;
}

.ygb-device-panel :deep(.el-card__body) {
  padding: 18px 20px 20px;
}

.ygb-device-panel__head--between {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.ygb-device-panel__title {
  color: #13243a;
  font-size: 18px;
  font-weight: 700;
}

.ygb-device-panel__desc,
.ygb-device-panel__extra {
  margin-top: 4px;
  color: #7b8da1;
  font-size: 13px;
  line-height: 1.7;
}

.ygb-device-kpi-list,
.ygb-device-step-list {
  display: grid;
  gap: 12px;
}

.ygb-device-kpi {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
  border: 1px solid #dde7f1;
  border-radius: 14px;
  background: #f8fbfd;
}

.ygb-device-kpi__label {
  color: #627486;
  font-size: 13px;
}

.ygb-device-kpi__value {
  color: #13243a;
  font-size: 14px;
  font-weight: 600;
  text-align: right;
}

.ygb-device-step {
  display: grid;
  grid-template-columns: 42px minmax(0, 1fr);
  gap: 14px;
  padding: 16px;
  border: 1px solid #dde7f1;
  border-radius: 14px;
  background: #f8fbfd;
}

.ygb-device-step__index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 34px;
  border-radius: 10px;
  background: #edf5fc;
  color: #0f5ea8;
  font-weight: 700;
}

.ygb-device-step__body p {
  margin: 8px 0 0;
  color: #5f6f80;
  font-size: 13px;
  line-height: 1.7;
}

.ygb-device-tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
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

.ygb-link-item:hover {
  border-color: #9db8d6;
  box-shadow: 0 8px 20px rgba(15, 94, 168, 0.08);
  transform: translateY(-1px);
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

.ygb-device-form {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0 16px;
}

@media (max-width: 1200px) {
  .ygb-device-page__summary,
  .ygb-device-page__grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .ygb-link-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 900px) {
  .ygb-device-form {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .ygb-device-page__summary,
  .ygb-device-page__grid {
    grid-template-columns: 1fr;
  }

  .ygb-link-grid {
    grid-template-columns: 1fr;
  }

  .ygb-device-panel__head--between {
    flex-direction: column;
  }
}
</style>
