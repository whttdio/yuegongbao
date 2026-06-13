<template>
  <view class="worker-page">
    <view class="worker-card">
      <view class="worker-title">设置</view>
      <view class="worker-subtitle settings-hint">
        这里集中管理业务通知偏好、推送联调留痕和接口环境切换。
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">推送状态</view>
        <view class="worker-tag" :class="pushRegistered ? 'worker-tag--success' : 'worker-tag--warning'">
          {{ pushRegistered ? '已登记' : '未登记' }}
        </view>
      </view>
      <view class="status-grid">
        <view class="status-item">
          <view class="status-item__label">业务通知</view>
          <view class="status-item__value">{{ notifyEnabled ? '已开启' : '已关闭' }}</view>
        </view>
        <view class="status-item">
          <view class="status-item__label">系统通知</view>
          <view class="status-item__value">{{ permissionLabel }}</view>
        </view>
        <view class="status-item">
          <view class="status-item__label">推送平台</view>
          <view class="status-item__value">{{ pushPlatformText }}</view>
        </view>
        <view class="status-item">
          <view class="status-item__label">ClientId 摘要</view>
          <view class="status-item__value">{{ pushClientIdMasked || '-' }}</view>
        </view>
      </view>
      <view class="worker-subtitle">{{ pushStatusText }}</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">网关环境</view>
        <view class="worker-tag" :class="pushGatewayReady ? 'worker-tag--success' : 'worker-tag--warning'">
          {{ pushGatewayReady ? '可联调' : '待补充' }}
        </view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">网关启用</view>
        <view class="detail-row__value">{{ pushGatewayEnabled ? '已启用' : '未启用' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">网关地址</view>
        <view class="detail-row__value">{{ pushGatewayUrlConfigured ? '已配置' : '未配置' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">Provider</view>
        <view class="detail-row__value">{{ pushGatewayProvider || 'custom' }}</view>
      </view>
      <view class="worker-subtitle">{{ pushGatewayStatusText }}</view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">推送本地快照</view>
        <view class="worker-tag" :class="pushDiagnosticsTagClass">{{ pushDiagnosticsStatusText }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">本地 ClientId</view>
        <view class="detail-row__value">{{ localPushClientId || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">最近登记尝试</view>
        <view class="detail-row__value">{{ pushDiagnosticsLastAttemptAt || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">最近登记结果</view>
        <view class="detail-row__value">{{ pushDiagnosticsResultText }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">待消费落页</view>
        <view class="detail-row__value">{{ pendingJumpTargetSummary }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">最近写入待跳转</view>
        <view class="detail-row__value">{{ jumpDiagnosticsLastSavedStatus }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">最近消费结果</view>
        <view class="detail-row__value">{{ jumpDiagnosticsLastConsumedStatus }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">最近打开结果</view>
        <view class="detail-row__value">{{ jumpDiagnosticsLastOpenedStatus }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">本地事件数</view>
        <view class="detail-row__value">{{ pushEventCount }} 条</view>
      </view>
      <view v-if="pushDiagnosticsLastMessage" class="result-block">
        <view class="result-block__label">登记说明</view>
        <view class="result-block__value">{{ pushDiagnosticsLastMessage }}</view>
      </view>
      <view v-if="pendingJumpTargetDetail" class="result-block">
        <view class="result-block__label">待跳转详情</view>
        <view class="result-block__value">{{ pendingJumpTargetDetail }}</view>
      </view>
      <view v-if="jumpDiagnosticsLastMessage" class="result-block">
        <view class="result-block__label">跳转链说明</view>
        <view class="result-block__value">{{ jumpDiagnosticsLastMessage }}</view>
      </view>
      <view class="settings-action-row">
        <button class="worker-button worker-button--secondary" :disabled="!pendingJumpTargetPath" @click="openPendingJumpTargetManually">
          手动打开落页
        </button>
        <button class="worker-button worker-button--secondary" :disabled="pushRegistering" @click="triggerPushRegistration">
          {{ pushRegistering ? '登记中' : '重新登记推送' }}
        </button>
        <button class="worker-button worker-button--secondary" :disabled="!pendingJumpTargetPath" @click="clearPendingJumpTargetSnapshot">
          清空待跳转
        </button>
        <button class="worker-button" @click="clearPushDiagnosticsSnapshot">清空本地快照</button>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">接口环境</view>
        <view class="worker-tag worker-tag--info">可切换</view>
      </view>
      <input
        v-model="apiBaseUrlInput"
        class="settings-input"
        placeholder="请输入后端接口地址，例如 http://192.168.1.8:8080"
      />
      <view class="worker-subtitle">当前生效地址：{{ resolvedApiBaseUrl }}</view>
      <view class="worker-subtitle">当前来源：{{ resolvedApiBaseUrlSourceLabel }}</view>
      <view v-if="apiConnectivityStatusText" class="worker-subtitle api-status-text">{{ apiConnectivityStatusText }}</view>
      <view class="settings-action-row">
        <button class="worker-button" @click="saveApiBaseUrl">保存接口地址</button>
        <button class="worker-button worker-button--secondary" @click="resetApiBaseUrl">恢复默认</button>
      </view>
      <view class="settings-action-row">
        <button class="worker-button worker-button--secondary" :disabled="apiConnectivityTesting" @click="testApiBaseUrl">
          {{ apiConnectivityTesting ? '检测中' : '检测接口连通性' }}
        </button>
        <button class="worker-button" :disabled="!canSaveTestedApiBaseUrl" @click="saveTestedApiBaseUrl">
          保存检测地址
        </button>
      </view>
    </view>
    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">验收基础信息</view>
        <view class="worker-tag worker-tag--info">导出自动带出</view>
      </view>
      <input
        v-model="acceptanceOperatorInput"
        class="settings-input"
        maxlength="20"
        placeholder="请输入联调人姓名"
      />
      <view class="worker-subtitle">当前联调人：{{ acceptanceOperatorDisplay }}</view>
      <view class="settings-action-row">
        <button class="worker-button" @click="saveAcceptanceOperator">保存联调人</button>
        <button class="worker-button worker-button--secondary" :disabled="!acceptanceOperatorInput.trim()" @click="clearAcceptanceOperator">
          清空联调人
        </button>
      </view>
    </view>
    <view v-if="lastPushTestVisible" class="worker-card">
      <view class="section-head">
        <view class="worker-title">最近一次测试</view>
        <view class="worker-tag" :class="lastPushTestSuccess ? 'worker-tag--success' : 'worker-tag--warning'">
          {{ lastPushTestSuccess ? '已触发' : '失败' }}
        </view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">测试时间</view>
        <view class="detail-row__value">{{ lastPushTestAt || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">TraceId</view>
        <view class="detail-row__value">{{ lastPushTestTraceId || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">目标页面</view>
        <view class="detail-row__value">{{ lastPushTestTargetPath || '-' }}</view>
      </view>
      <view class="detail-row">
        <view class="detail-row__label">来源标签</view>
        <view class="detail-row__value">{{ lastPushTestSourceLabel || '-' }}</view>
      </view>
      <view v-if="lastPushTestTargetQueryText" class="result-block">
        <view class="result-block__label">目标参数</view>
        <view class="result-block__value">{{ lastPushTestTargetQueryText }}</view>
      </view>
      <view v-if="lastPushTestResponseBody" class="result-block">
        <view class="result-block__label">网关响应</view>
        <view class="result-block__value">{{ lastPushTestResponseBody }}</view>
      </view>
      <view class="settings-action-row">
        <button class="worker-button worker-button--secondary" :disabled="!lastPushTestTargetPath" @click="openLastPushTestTarget">
          打开测试目标
        </button>
      </view>
      <view class="worker-subtitle">{{ lastPushTestStatusText }}</view>
    </view>

    <view v-if="serverPushTestHistory.length" class="worker-card">
      <view class="section-head">
        <view class="worker-title">服务端测试记录</view>
        <view class="worker-tag worker-tag--info">{{ serverPushTestHistory.length }} 条</view>
      </view>
      <view
        v-for="item in serverPushTestHistory"
        :key="item.recordId || item.traceId"
        class="history-row"
      >
        <view class="history-row__head">
          <view class="history-row__title">{{ item.traceId || '未返回 traceId' }}</view>
          <view
            class="worker-tag"
            :class="String(item.testStatus || '').toUpperCase() === 'SUCCESS' ? 'worker-tag--success' : 'worker-tag--warning'"
          >
            {{ String(item.testStatus || '').toUpperCase() === 'SUCCESS' ? '成功' : '失败' }}
          </view>
        </view>
        <view class="history-row__meta">{{ item.testedAt || '-' }}</view>
        <view class="history-row__detail">目标页面：{{ item.targetPath || '-' }}</view>
        <view class="history-row__detail">来源标签：{{ item.sourceLabel || '-' }}</view>
        <view v-if="item.targetQueryText" class="history-row__detail">参数：{{ item.targetQueryText }}</view>
        <view class="history-row__actions">
          <view class="record-row__link" @click.stop="openServerPushHistoryTarget(item)">打开目标</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">最近推送事件</view>
        <view v-if="recentPushEvents.length" class="clear-action" @click="clearPushEventHistory">清空</view>
      </view>
      <view v-if="recentPushEvents.length">
        <view
          v-for="item in recentPushEvents"
          :key="item.id"
          class="history-row"
        >
          <view class="history-row__head">
            <view class="history-row__title">{{ item.title || '未命名推送' }}</view>
            <view class="worker-tag" :class="item.type === 'click' ? 'worker-tag--success' : 'worker-tag--info'">
              {{ item.type === 'click' ? '点击' : '接收' }}
            </view>
          </view>
          <view class="history-row__meta">{{ item.occurredAt || '-' }}</view>
          <view v-if="item.content" class="history-row__detail">{{ item.content }}</view>
          <view class="history-row__detail">落页：{{ item.targetPath || '未解析' }}</view>
          <view v-if="item.targetQueryText" class="history-row__detail">参数：{{ item.targetQueryText }}</view>
          <view v-if="item.sourceLabel" class="history-row__detail">来源：{{ item.sourceLabel }}</view>
          <view class="history-row__actions">
            <view class="record-row__link" @click.stop="openRecentPushEventTarget(item)">打开目标</view>
          </view>
        </view>
      </view>
      <view v-else class="worker-empty">
        当前还没有推送接收或点击记录。
      </view>
    </view>
    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">联调结果记录</view>
        <view class="clear-action" @click="copyText(acceptanceResultDraftText, '联调结果已复制')">复制</view>
      </view>
      <view class="worker-subtitle">{{ acceptanceResultSummaryText }}</view>
      <view
        v-for="item in acceptanceResultItems"
        :key="item.key"
        class="history-row"
      >
        <view class="history-row__head">
          <view class="history-row__title">{{ item.label }}</view>
          <view class="worker-tag" :class="resolveAcceptanceResultTagClass(item.status)">{{ item.statusLabel }}</view>
        </view>
        <view class="history-row__meta">{{ item.updatedAt || '尚未记录结果' }}</view>
        <view class="settings-action-row settings-action-row--wrap settings-action-row--compact">
          <button class="worker-button worker-button--secondary" @click="setAcceptanceResult(item.key, 'pending')">待验收</button>
          <button class="worker-button worker-button--secondary" @click="setAcceptanceResult(item.key, 'passed')">已通过</button>
          <button class="worker-button worker-button--secondary" @click="setAcceptanceResult(item.key, 'failed')">失败</button>
          <button class="worker-button worker-button--secondary" @click="setAcceptanceResult(item.key, 'blocked')">阻塞</button>
        </view>
        <textarea
          v-model="acceptanceResultNotes[item.key]"
          class="settings-textarea"
          maxlength="200"
          placeholder="记录失败现象、阻塞原因或验收备注"
        />
        <input
          v-model="acceptanceResultVerificationTerminals[item.key]"
          class="settings-input settings-input--compact"
          maxlength="60"
          placeholder="验证端：App / H5 / mp-weixin"
        />
        <input
          v-model="acceptanceResultEvidenceSources[item.key]"
          class="settings-input settings-input--compact"
          maxlength="100"
          placeholder="证据来源：截图 / 录屏 / 日志 / 接口返回"
        />
        <view class="history-row__actions">
          <view class="record-row__link" @click.stop="saveAcceptanceResultNote(item)">保存记录</view>
          <view class="record-row__link" @click.stop="openAcceptanceResultPage(item)">打开页面</view>
        </view>
      </view>
      <view class="settings-action-row settings-action-row--wrap">
        <button class="worker-button worker-button--secondary" @click="copyText(acceptanceResultDraftText, '联调结果已复制')">
          复制结果记录
        </button>
        <button class="worker-button worker-button--secondary" @click="copyText(acceptanceTemplateSummaryText, '验收模板总结已复制')">
          复制模板总结
        </button>
        <button class="worker-button" @click="clearAcceptanceResults">
          清空结果记录
        </button>
      </view>
      <view class="result-block">
        <view class="result-block__label">结果草稿</view>
        <view class="result-block__value">{{ acceptanceResultDraftText }}</view>
      </view>
      <view class="result-block">
        <view class="result-block__label">模板总结</view>
        <view class="result-block__value">{{ acceptanceTemplateSummaryText }}</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">联调前置检查</view>
        <view class="clear-action" @click="copyText(acceptancePrerequisiteDraftText, '前置检查已复制')">复制</view>
      </view>
      <view class="worker-subtitle">{{ acceptancePrerequisiteSummaryText }}</view>
      <view
        v-for="item in acceptancePrerequisiteItems"
        :key="item.key"
        class="history-row"
      >
        <view class="history-row__head">
          <view class="history-row__title">{{ item.label }}</view>
          <view class="worker-tag" :class="resolveAcceptancePrerequisiteTagClass(item.status)">{{ item.status === 'passed' ? '已就绪' : item.statusLabel }}</view>
        </view>
        <view class="history-row__meta">{{ item.updatedAt || '尚未记录前置状态' }}</view>
        <view class="history-row__detail">{{ item.detail }}</view>
        <view class="settings-action-row settings-action-row--wrap settings-action-row--compact">
          <button class="worker-button worker-button--secondary" @click="setAcceptancePrerequisite(item.key, 'pending')">未开始</button>
          <button class="worker-button worker-button--secondary" @click="setAcceptancePrerequisite(item.key, 'in_progress')">进行中</button>
          <button class="worker-button worker-button--secondary" @click="setAcceptancePrerequisite(item.key, 'blocked')">阻塞</button>
          <button class="worker-button worker-button--secondary" @click="setAcceptancePrerequisite(item.key, 'passed')">已就绪</button>
        </view>
        <textarea
          v-model="acceptancePrerequisiteNotes[item.key]"
          class="settings-textarea"
          maxlength="200"
          placeholder="记录环境地址、设备、账号、域名、网段或阻塞说明"
        />
        <view class="history-row__actions">
          <view class="record-row__link" @click.stop="saveAcceptancePrerequisiteNote(item)">保存备注</view>
          <view class="record-row__link" @click.stop="openAcceptancePrerequisitePage(item)">打开页面</view>
        </view>
      </view>
      <view class="settings-action-row settings-action-row--wrap">
        <button class="worker-button worker-button--secondary" @click="copyText(acceptancePrerequisiteDraftText, '前置检查已复制')">
          复制前置检查
        </button>
        <button class="worker-button" @click="clearAcceptancePrerequisites">
          清空前置检查
        </button>
      </view>
      <view class="result-block">
        <view class="result-block__label">前置检查草稿</view>
        <view class="result-block__value">{{ acceptancePrerequisiteDraftText }}</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">执行状态面板</view>
        <view class="clear-action" @click="copyText(acceptancePhaseDraftText, '执行状态已复制')">复制</view>
      </view>
      <view class="worker-subtitle">{{ acceptancePhaseSummaryText }}</view>
      <view
        v-for="item in acceptancePhaseItems"
        :key="item.key"
        class="history-row"
      >
        <view class="history-row__head">
          <view class="history-row__title">{{ item.label }}</view>
          <view class="worker-tag" :class="resolveAcceptancePhaseTagClass(item.status)">{{ item.statusLabel }}</view>
        </view>
        <view class="history-row__meta">{{ item.updatedAt || '尚未记录阶段状态' }}</view>
        <view class="history-row__detail">{{ item.detail }}</view>
        <view class="settings-action-row settings-action-row--wrap settings-action-row--compact">
          <button class="worker-button worker-button--secondary" @click="setAcceptancePhase(item.key, 'pending')">未开始</button>
          <button class="worker-button worker-button--secondary" @click="setAcceptancePhase(item.key, 'in_progress')">进行中</button>
          <button class="worker-button worker-button--secondary" @click="setAcceptancePhase(item.key, 'blocked')">阻塞</button>
          <button class="worker-button worker-button--secondary" @click="setAcceptancePhase(item.key, 'passed')">已完成</button>
        </view>
        <textarea
          v-model="acceptancePhaseNotes[item.key]"
          class="settings-textarea"
          maxlength="200"
          placeholder="记录当前阶段卡点、真机现象或联调备注"
        />
        <view class="history-row__actions">
          <view class="record-row__link" @click.stop="saveAcceptancePhaseNote(item)">保存备注</view>
          <view class="record-row__link" @click.stop="openAcceptancePhasePage(item)">打开页面</view>
        </view>
      </view>
      <view class="settings-action-row settings-action-row--wrap">
        <button class="worker-button worker-button--secondary" @click="copyText(acceptancePhaseDraftText, '执行状态已复制')">
          复制执行状态
        </button>
        <button class="worker-button" @click="clearAcceptancePhases">
          清空阶段状态
        </button>
      </view>
      <view class="result-block">
        <view class="result-block__label">阶段草稿</view>
        <view class="result-block__value">{{ acceptancePhaseDraftText }}</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">接口联调清单</view>
        <view class="clear-action" @click="copyText(acceptanceApiDraftText, '接口清单已复制')">复制</view>
      </view>
      <view class="worker-subtitle">{{ acceptanceApiSummaryText }}</view>
      <view
        v-for="item in acceptanceApiItems"
        :key="item.key"
        class="history-row"
      >
        <view class="history-row__head">
          <view class="history-row__title">{{ item.label }}</view>
          <view class="worker-tag" :class="resolveAcceptanceApiTagClass(item.status)">{{ item.statusLabel }}</view>
        </view>
        <view class="history-row__meta">{{ item.updatedAt || '尚未记录接口联调状态' }}</view>
        <view class="history-row__detail">{{ item.detail }}</view>
        <view
          v-for="endpoint in item.endpoints"
          :key="endpoint"
          class="history-row__detail"
        >
          {{ endpoint }}
        </view>
        <view class="settings-action-row settings-action-row--wrap settings-action-row--compact">
          <button class="worker-button worker-button--secondary" @click="setAcceptanceApi(item.key, 'pending')">未开始</button>
          <button class="worker-button worker-button--secondary" @click="setAcceptanceApi(item.key, 'in_progress')">进行中</button>
          <button class="worker-button worker-button--secondary" @click="setAcceptanceApi(item.key, 'blocked')">阻塞</button>
          <button class="worker-button worker-button--secondary" @click="setAcceptanceApi(item.key, 'passed')">已完成</button>
        </view>
        <textarea
          v-model="acceptanceApiNotes[item.key]"
          class="settings-textarea"
          maxlength="200"
          placeholder="记录接口返回、字段问题、空态问题或联调备注"
        />
        <view class="history-row__actions">
          <view class="record-row__link" @click.stop="saveAcceptanceApiNote(item)">保存备注</view>
          <view class="record-row__link" @click.stop="openAcceptanceApiPage(item)">打开页面</view>
        </view>
      </view>
      <view class="settings-action-row settings-action-row--wrap">
        <button class="worker-button worker-button--secondary" @click="copyText(acceptanceApiDraftText, '接口清单已复制')">
          复制接口清单
        </button>
        <button class="worker-button" @click="clearAcceptanceApis">
          清空接口清单
        </button>
      </view>
      <view class="result-block">
        <view class="result-block__label">接口草稿</view>
        <view class="result-block__value">{{ acceptanceApiDraftText }}</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">问题记录</view>
        <view class="clear-action" @click="copyText(acceptanceIssueDraftText, '问题记录已复制')">复制</view>
      </view>
      <view class="worker-subtitle">{{ acceptanceIssueSummaryText }}</view>
      <view
        v-for="item in acceptanceIssueItems"
        :key="item.id"
        class="history-row"
      >
        <view class="history-row__head">
          <view class="history-row__title">问题 {{ item.id }}</view>
          <view class="worker-tag worker-tag--info">{{ item.updatedAt || '未保存' }}</view>
        </view>
        <input
          :value="item.module"
          class="settings-input settings-input--compact"
          placeholder="模块，如：首页 / 推送 / 打卡 / 求职"
          @input="updateAcceptanceIssueField(item.id - 1, 'module', $event.detail.value)"
        />
        <textarea
          :value="item.phenomenon"
          class="settings-textarea"
          maxlength="300"
          placeholder="现象：描述具体异常、错误提示或结果偏差"
          @input="updateAcceptanceIssueField(item.id - 1, 'phenomenon', $event.detail.value)"
        />
        <textarea
          :value="item.reproduceCondition"
          class="settings-textarea"
          maxlength="240"
          placeholder="复现条件：设备、账号、步骤、数据条件"
          @input="updateAcceptanceIssueField(item.id - 1, 'reproduceCondition', $event.detail.value)"
        />
        <textarea
          :value="item.preliminaryJudgment"
          class="settings-textarea"
          maxlength="240"
          placeholder="初步判断：前端 / 接口 / 数据 / 配置 / 真机权限"
          @input="updateAcceptanceIssueField(item.id - 1, 'preliminaryJudgment', $event.detail.value)"
        />
        <input
          :value="item.status"
          class="settings-input settings-input--compact"
          placeholder="处理状态，如：待排查 / 已修复待回归 / 阻塞"
          @input="updateAcceptanceIssueField(item.id - 1, 'status', $event.detail.value)"
        />
        <view class="history-row__actions">
          <view class="record-row__link" @click.stop="saveAcceptanceIssue(item.id - 1)">保存问题</view>
        </view>
      </view>
      <view class="settings-action-row settings-action-row--wrap">
        <button class="worker-button worker-button--secondary" @click="copyText(acceptanceIssueDraftText, '问题记录已复制')">
          复制问题记录
        </button>
        <button class="worker-button worker-button--secondary" @click="copyText(acceptanceFormalDraftText, '正式验收片段已复制')">
          复制正式片段
        </button>
        <button class="worker-button" @click="clearAcceptanceIssues">
          清空问题记录
        </button>
      </view>
      <view class="result-block">
        <view class="result-block__label">问题草稿</view>
        <view class="result-block__value">{{ acceptanceIssueDraftText }}</view>
      </view>
      <view class="result-block">
        <view class="result-block__label">正式验收片段</view>
        <view class="result-block__value">{{ acceptanceFormalDraftText }}</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head">
        <view class="worker-title">证据采集清单</view>
        <view class="clear-action" @click="copyText(acceptanceEvidenceDraftText, '证据草稿已复制')">复制</view>
      </view>
      <view class="worker-subtitle">{{ acceptanceEvidenceSummaryText }}</view>
      <view
        v-for="(item, index) in acceptanceEvidenceItems"
        :key="item.id"
        class="history-row"
      >
        <view class="history-row__head">
          <view class="history-row__title">{{ item.label }}</view>
          <view class="worker-tag worker-tag--info">{{ item.updatedAt || '未保存' }}</view>
        </view>
        <view class="history-row__detail">{{ item.expected }}</view>
        <textarea
          :value="item.pageEvidence"
          class="settings-textarea"
          maxlength="300"
          placeholder="页面证据：截图页面、关键结果、定位说明"
          @input="updateAcceptanceEvidenceField(index, 'pageEvidence', $event.detail.value)"
        />
        <textarea
          :value="item.apiEvidence"
          class="settings-textarea"
          maxlength="300"
          placeholder="接口证据：返回摘要、关键字段、判定结果"
          @input="updateAcceptanceEvidenceField(index, 'apiEvidence', $event.detail.value)"
        />
        <input
          :value="item.serverLogTime"
          class="settings-input settings-input--compact"
          maxlength="80"
          placeholder="服务端日志时间：2026-06-10 10:22:31"
          @input="updateAcceptanceEvidenceField(index, 'serverLogTime', $event.detail.value)"
        />
        <input
          :value="item.issueTicket"
          class="settings-input settings-input--compact"
          maxlength="80"
          placeholder="问题单编号：FIX-12 / BLOCK-03"
          @input="updateAcceptanceEvidenceField(index, 'issueTicket', $event.detail.value)"
        />
        <textarea
          :value="item.note"
          class="settings-textarea"
          maxlength="200"
          placeholder="备注：环境差异、验收结论、补充说明"
          @input="updateAcceptanceEvidenceField(index, 'note', $event.detail.value)"
        />
        <view class="history-row__actions">
          <view class="record-row__link" @click.stop="saveAcceptanceEvidence(index)">保存</view>
        </view>
      </view>
      <view class="settings-action-row settings-action-row--wrap">
        <button class="worker-button worker-button--secondary" @click="copyText(acceptanceEvidenceDraftText, '证据草稿已复制')">
          复制证据草稿
        </button>
        <button class="worker-button" @click="clearAcceptanceEvidence">
          清空证据清单
        </button>
      </view>
      <view class="result-block">
        <view class="result-block__label">证据草稿</view>
        <view class="result-block__value">{{ acceptanceEvidenceDraftText }}</view>
      </view>
    </view>
    <view class="worker-card">
      <view class="settings-list">
        <view class="settings-row" @click="goAccountSecurity">账号安全</view>
        <view class="settings-row settings-row--switch">
          <view class="settings-row__main">
            <view class="settings-row__title">业务通知偏好</view>
            <view class="settings-row__desc">控制平台业务提醒是否继续发送到当前账号。</view>
          </view>
          <switch :checked="notifyEnabled" color="#1f6fd6" @change="toggleNotify" />
        </view>
        <view class="settings-row" @click="openSystemNotificationSetting">
          <view class="settings-row__main">
            <view class="settings-row__title">系统通知权限</view>
            <view class="settings-row__desc">{{ permissionHint }}</view>
          </view>
          <view class="worker-tag">{{ permissionLabel }}</view>
        </view>
        <view class="settings-row" @click="triggerPushTest">
          <view class="settings-row__main">
            <view class="settings-row__title">发送测试推送</view>
            <view class="settings-row__desc">调用服务端推送网关，验证送达和点击跳转链路。</view>
          </view>
          <view class="worker-tag">{{ pushTesting ? '发送中' : '测试' }}</view>
        </view>
        <view class="settings-row" @click="clearCache">清理缓存</view>
        <view class="settings-row" @click="goPrivacy">隐私协议</view>
        <view class="settings-row settings-row--danger" @click="logout">退出登录</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getWorkerSettings, saveWorkerSettings, sendWorkerPushTest } from '../../api/worker'
import {
  DEFAULT_BASE_URL,
  WORKER_API_BASE_URL_STORAGE_KEY,
  clearCurrentWorkerAuthState,
  getBaseUrl,
  getBuildMode,
  getBaseUrlSource,
  getWorkerSessionSnapshot,
  restoreWorkerSessionSnapshot,
  setBaseUrl
} from '../../utils/request'
import {
  buildWorkerAcceptanceDetailedDraftText,
  buildWorkerAcceptanceOverviewText,
  clearWorkerAcceptanceDiagnostics,
  getWorkerAcceptanceOverview
} from '../../utils/acceptance-overview'
import {
  buildWorkerAcceptanceApiDraft,
  buildWorkerAcceptanceApiSummary,
  buildWorkerAcceptanceFormalDraft,
  buildWorkerAcceptancePrerequisiteDraft,
  buildWorkerAcceptancePrerequisiteSummary,
  buildWorkerAcceptancePhaseDraft,
  buildWorkerAcceptancePhaseSummary,
  buildWorkerAcceptanceEvidenceDraft,
  buildWorkerAcceptanceEvidenceSummary,
  buildWorkerAcceptanceIssueDraft,
  buildWorkerAcceptanceIssueSummary,
  buildWorkerAcceptanceResultDraft,
  buildWorkerAcceptanceResultSummary,
  buildWorkerAcceptanceTemplateSummaryDraft,
  clearWorkerAcceptanceLocalRecords,
  clearWorkerAcceptanceApiItems,
  clearWorkerAcceptanceEvidenceItems,
  clearWorkerAcceptancePrerequisiteItems,
  clearWorkerAcceptancePhaseItems,
  clearWorkerAcceptanceIssueItems,
  clearWorkerAcceptanceResultItems,
  getWorkerAcceptanceApiItems,
  getWorkerAcceptanceEvidenceItems,
  getWorkerAcceptancePrerequisiteItems,
  getWorkerAcceptancePhaseItems,
  getWorkerAcceptanceIssueItems,
  getWorkerAcceptanceResultItems,
  setWorkerAcceptanceApiNote,
  setWorkerAcceptanceApiStatus,
  setWorkerAcceptanceEvidenceItem,
  setWorkerAcceptancePrerequisiteNote,
  setWorkerAcceptancePrerequisiteStatus,
  setWorkerAcceptancePhaseNote,
  setWorkerAcceptancePhaseStatus,
  setWorkerAcceptanceIssueItem,
  setWorkerAcceptanceResultNote,
  setWorkerAcceptanceResultStatus
} from '../../utils/acceptance-results'
import {
  clearWorkerPushDiagnostics,
  clearRecentWorkerPushEvents,
  getPushClientIdSafe,
  getRecentWorkerPushEvents,
  getWorkerPushDiagnostics,
  syncWorkerPushRegistration,
  resolveNotificationPermission
} from '../../utils/push'
import {
  clearPendingWorkerJumpTarget,
  clearWorkerJumpDiagnostics,
  getWorkerJumpDiagnostics,
  normalizeWorkerJumpTarget,
  openWorkerJumpTarget,
  readPendingWorkerJumpTarget
} from '../../utils/worker-jump'
import { openPage } from '../../utils/navigation'

const WORKER_API_CONNECTIVITY_SNAPSHOT_KEY = 'worker_api_connectivity_snapshot'
const WORKER_ACCEPTANCE_OPERATOR_KEY = 'worker_acceptance_operator'

const notifyEnabled = ref(true)
const loading = ref(false)
const pushTesting = ref(false)
const pushRegistering = ref(false)
const notificationPermission = ref('unknown')
const pushRegistered = ref(false)
const pushClientIdMasked = ref('')
const pushPlatform = ref('')
const pushSyncTime = ref('')
const pushStatusText = ref('当前仅保存业务通知偏好，待 App 真机登录后完成推送登记。')
const pushGatewayEnabled = ref(false)
const pushGatewayUrlConfigured = ref(false)
const pushGatewayProvider = ref('custom')
const pushGatewayReady = ref(false)
const pushGatewayStatusText = ref('服务端推送网关未启用，发送测试推送会直接失败。')

const apiBaseUrlInput = ref('')
const apiConnectivityTesting = ref(false)
const apiConnectivityStatusText = ref('')
const lastSuccessfulApiBaseUrl = ref('')
const apiConnectivityLastTestAt = ref('')
const apiConnectivityLastTestUrl = ref('')
const apiConnectivityLastStatusCode = ref('')
const apiConnectivityLastMessage = ref('')
const apiConnectivityLastTestSuccess = ref(null)

const lastPushTestVisible = ref(false)
const lastPushTestSuccess = ref(false)
const lastPushTestAt = ref('')
const lastPushTestTraceId = ref('')
const lastPushTestTargetPath = ref('')
const lastPushTestTargetQueryText = ref('')
const lastPushTestStatusText = ref('')
const lastPushTestResponseBody = ref('')
const lastPushTestSourceLabel = ref('')

const serverPushTestHistory = ref([])
const recentPushEvents = ref([])
const localPushClientId = ref('')
const pushDiagnosticsLastAttemptAt = ref('')
const pushDiagnosticsLastResult = ref('not_started')
const pushDiagnosticsLastMessage = ref('')
const pushDiagnosticsRequestPayload = ref('')
const pushEventCount = ref(0)
const pendingJumpTargetSummary = ref('无')
const pendingJumpTargetDetail = ref('')
const pendingJumpTargetPath = ref('')
const pendingJumpTargetQueryText = ref('')
const jumpDiagnosticsLastSavedStatus = ref('未写入')
const jumpDiagnosticsLastConsumedStatus = ref('未消费')
const jumpDiagnosticsLastOpenedStatus = ref('未打开')
const jumpDiagnosticsLastMessage = ref('')
const acceptanceOperatorInput = ref('')
const deviceAcceptanceOverview = ref(getWorkerAcceptanceOverview())
const acceptancePrerequisiteItems = ref(getWorkerAcceptancePrerequisiteItems())
const acceptancePrerequisiteNotes = ref({})
const acceptanceApiItems = ref(getWorkerAcceptanceApiItems())
const acceptanceApiNotes = ref({})
const acceptancePhaseItems = ref(getWorkerAcceptancePhaseItems())
const acceptancePhaseNotes = ref({})
const acceptanceIssueItems = ref(getWorkerAcceptanceIssueItems())
const acceptanceEvidenceItems = ref(getWorkerAcceptanceEvidenceItems())
const acceptanceResultItems = ref(getWorkerAcceptanceResultItems())
const acceptanceResultNotes = ref({})
const acceptanceResultVerificationTerminals = ref({})
const acceptanceResultEvidenceSources = ref({})

const permissionLabel = computed(() => {
  if (notificationPermission.value === 'authorized') {
    return '已授权'
  }
  if (notificationPermission.value === 'denied') {
    return '已关闭'
  }
  if (notificationPermission.value === 'not determined') {
    return '未确认'
  }
  if (notificationPermission.value === 'config error') {
    return '未配置'
  }
  return '暂不支持'
})

const permissionHint = computed(() => {
  if (notificationPermission.value === 'authorized') {
    return '系统通知权限已开启，若仍未收到提醒，请继续核对业务通知偏好。'
  }
  if (notificationPermission.value === 'denied') {
    return '系统通知权限已关闭，可前往系统授权页重新开启。'
  }
  if (notificationPermission.value === 'not determined') {
    return '系统尚未完成通知授权确认，可点击检查当前权限。'
  }
  if (notificationPermission.value === 'config error') {
    return '当前运行环境未正确配置系统通知能力，需要在原生端继续补齐。'
  }
  return '当前 H5 或运行环境暂不支持检测系统通知权限。'
})

const pushPlatformText = computed(() => {
  if (!pushPlatform.value) {
    return '待上报'
  }
  if (pushPlatform.value === 'app-plus') {
    return 'App'
  }
  if (pushPlatform.value === 'h5') {
    return 'H5'
  }
  return pushPlatform.value
})
const pushDiagnosticsStatusText = computed(() => {
  if (pushDiagnosticsLastResult.value === 'success') {
    return '登记成功'
  }
  if (pushDiagnosticsLastResult.value === 'failed') {
    return '登记失败'
  }
  if (pushDiagnosticsLastResult.value === 'missing_client_id') {
    return '缺少 ClientId'
  }
  if (pushDiagnosticsLastResult.value === 'skipped') {
    return '已跳过'
  }
  return '未登记'
})
const pushDiagnosticsTagClass = computed(() => {
  if (pushDiagnosticsLastResult.value === 'success') {
    return 'worker-tag--success'
  }
  if (['failed', 'missing_client_id'].includes(pushDiagnosticsLastResult.value)) {
    return 'worker-tag--warning'
  }
  return 'worker-tag--info'
})
const pushDiagnosticsResultText = computed(() => {
  const parts = [pushDiagnosticsStatusText.value]
  if (pushDiagnosticsLastAttemptAt.value) {
    parts.push(pushDiagnosticsLastAttemptAt.value)
  }
  return parts.join(' / ')
})

const latestPushEvent = computed(() => recentPushEvents.value[0] || null)
const resolvedApiBaseUrl = computed(() => getBaseUrl())
const resolvedApiBaseUrlSourceLabel = computed(() => {
  const source = getBaseUrlSource()
  if (source === 'runtime') {
    return '设置页运行时地址'
  }
  if (source === 'env') {
    return '构建环境变量'
  }
  return '默认地址'
})
const canSaveTestedApiBaseUrl = computed(() => !!lastSuccessfulApiBaseUrl.value && lastSuccessfulApiBaseUrl.value !== getBaseUrl())
const apiConnectivityLastTestStatusText = computed(() => {
  if (apiConnectivityLastTestSuccess.value === true) {
    return '已通过'
  }
  if (apiConnectivityLastTestSuccess.value === false) {
    return '待整改'
  }
  return '未检测'
})
const apiConnectivityLastTestLabel = computed(() => {
  if (apiConnectivityLastTestSuccess.value === true) {
    return apiConnectivityLastStatusCode.value ? `PASS (${apiConnectivityLastStatusCode.value})` : 'PASS'
  }
  if (apiConnectivityLastTestSuccess.value === false) {
    return apiConnectivityLastStatusCode.value ? `FAIL (${apiConnectivityLastStatusCode.value})` : 'FAIL'
  }
  return 'NOT_TESTED'
})
const currentBuildMode = computed(() => {
  const mode = String(getBuildMode() || '').trim().toLowerCase()
  return mode || 'production'
})
const apiEnvironmentSnapshotText = computed(() => {
  return [
    `baseUrl=${resolvedApiBaseUrl.value}`,
    `buildMode=${currentBuildMode.value}`,
    `source=${resolvedApiBaseUrlSourceLabel.value}`,
    `input=${normalizeApiBaseUrlInput() || '-'}`,
    `lastTestAt=${apiConnectivityLastTestAt.value || '-'}`,
    `lastTestResult=${apiConnectivityLastTestLabel.value}`,
    `lastTestUrl=${apiConnectivityLastTestUrl.value || '-'}`,
    `candidateUrl=${lastSuccessfulApiBaseUrl.value || '-'}`,
    `lastMessage=${apiConnectivityLastMessage.value || apiConnectivityStatusText.value || '-'}`
  ].join('\n')
})
const devicePushSummaryText = computed(() => {
  const overview = deviceAcceptanceOverview.value.push
  return `${overview.lastStatus}${overview.lastAttemptAt ? ` / ${overview.lastAttemptAt}` : ''}${overview.pendingTargetPath ? ` / ${overview.pendingTargetPath}` : ''}`
})
const deviceCameraSummaryText = computed(() => {
  const overview = deviceAcceptanceOverview.value.camera
  return `${overview.lastUploadStatus || '未上传'}${overview.lastUploadAt ? ` / ${overview.lastUploadAt}` : ''} / 记录 ${overview.lastRecordCount} 条 / 带入 ${overview.lastComplaintForwardAt ? '已透传' : '未透传'}`
})
const deviceComplaintSummaryText = computed(() => {
  const overview = deviceAcceptanceOverview.value.complaint
  return `附件 ${overview.attachmentCount} 项 / 记录 ${overview.rowCount} 条${overview.lastSubmittedAt ? ` / ${overview.lastSubmittedAt}` : ''}`
})
const deviceAttendanceSummaryText = computed(() => {
  const overview = deviceAcceptanceOverview.value.attendance
  return `${overview.lastActionType || '-'} / ${overview.lastActionResult || '-'} / 队列 ${overview.offlineQueueCount} 条${overview.lastSyncTrigger ? ` / ${overview.lastSyncTrigger === 'manual' ? '手动补传' : '自动补传'}` : ''}`
})
const deviceMapSummaryText = computed(() => {
  const overview = deviceAcceptanceOverview.value.map
  const status = overview.lastLocationSuccess === true ? '定位成功' : overview.lastLocationSuccess === false ? '定位降级' : '未获取'
  return `${status}${overview.lastLocationAt ? ` / ${overview.lastLocationAt}` : ''}`
})
const deviceApiSummaryText = computed(() => {
  const overview = deviceAcceptanceOverview.value.api
  return `${overview.baseUrl} / mode=${overview.buildMode || '-'} / ${overview.lastStatus}${overview.lastTestAt ? ` / ${overview.lastTestAt}` : ''}`
})
const acceptanceOperatorDisplay = computed(() => acceptanceOperatorInput.value.trim() || '-')
const deviceAcceptanceOverviewText = computed(() => buildWorkerAcceptanceOverviewText(deviceAcceptanceOverview.value))
const deviceAcceptanceDraftText = computed(() => buildWorkerAcceptanceDetailedDraftText(deviceAcceptanceOverview.value))
const acceptancePrerequisiteSummaryText = computed(() => buildWorkerAcceptancePrerequisiteSummary(acceptancePrerequisiteItems.value))
const acceptancePrerequisiteDraftText = computed(() => buildWorkerAcceptancePrerequisiteDraft(acceptancePrerequisiteItems.value))
const acceptanceApiSummaryText = computed(() => buildWorkerAcceptanceApiSummary(acceptanceApiItems.value))
const acceptanceApiDraftText = computed(() => buildWorkerAcceptanceApiDraft(acceptanceApiItems.value))
const acceptancePhaseSummaryText = computed(() => buildWorkerAcceptancePhaseSummary(acceptancePhaseItems.value))
const acceptancePhaseDraftText = computed(() => buildWorkerAcceptancePhaseDraft(acceptancePhaseItems.value))
const acceptanceIssueSummaryText = computed(() => buildWorkerAcceptanceIssueSummary(acceptanceIssueItems.value))
const acceptanceIssueDraftText = computed(() => buildWorkerAcceptanceIssueDraft(acceptanceIssueItems.value))
const acceptanceEvidenceSummaryText = computed(() => buildWorkerAcceptanceEvidenceSummary(acceptanceEvidenceItems.value))
const acceptanceEvidenceDraftText = computed(() => buildWorkerAcceptanceEvidenceDraft(acceptanceEvidenceItems.value))
const acceptanceResultSummaryText = computed(() => buildWorkerAcceptanceResultSummary(acceptanceResultItems.value))
const acceptanceResultDraftText = computed(() => buildWorkerAcceptanceResultDraft(acceptanceResultItems.value))
const acceptanceTemplateSummaryText = computed(() => buildWorkerAcceptanceTemplateSummaryDraft(acceptanceResultItems.value))
const acceptanceFormalDraftText = computed(() =>
  buildWorkerAcceptanceFormalDraft(
    acceptanceIssueItems.value,
    acceptanceResultItems.value,
    acceptancePhaseItems.value,
    acceptanceApiItems.value,
    acceptancePrerequisiteItems.value,
    acceptanceEvidenceItems.value
  )
)
const acceptanceFullExportText = computed(() => {
  return [
    deviceAcceptanceOverviewText.value,
    '',
    deviceAcceptanceDraftText.value,
    '',
    acceptancePrerequisiteDraftText.value,
    '',
    acceptancePhaseDraftText.value,
    '',
    acceptanceApiDraftText.value,
    '',
    acceptanceIssueDraftText.value,
    '',
    acceptanceEvidenceDraftText.value,
    '',
    acceptanceResultDraftText.value,
    '',
    acceptanceFormalDraftText.value
  ].join('\n')
})

const acceptanceSnapshotText = computed(() => {
  return [
    `业务通知：${notifyEnabled.value ? '已开启' : '已关闭'}`,
    `系统通知：${permissionLabel.value}`,
    `服务端登记：${pushRegistered.value ? '已完成' : '待完成'}`,
    `本地登记：${pushDiagnosticsStatusText.value}`,
    `推送平台：${pushPlatformText.value}`,
    `构建模式：${currentBuildMode.value}`,
    `网关环境：${pushGatewayReady.value ? '可联调' : '待补充'}`,
    `API 检测：${apiConnectivityLastTestLabel.value}`,
    `最近事件：${latestPushEvent.value ? `${latestPushEvent.value.type} / ${latestPushEvent.value.occurredAt || '-'}` : '暂无'}`
  ].join('\n')
})

const acceptanceDraftText = computed(() => {
  return [
    '## 推送联调记录',
    `- 业务通知偏好：${notifyEnabled.value ? '开启' : '关闭'}`,
    `- 系统通知权限：${permissionLabel.value}`,
    `- 服务端推送登记：${pushRegistered.value ? '已完成' : '未完成'}`,
    `- 本地推送登记：${pushDiagnosticsStatusText.value}`,
    `- 推送平台：${pushPlatformText.value}`,
    `- 构建模式：${currentBuildMode.value}`,
    `- 本地 ClientId：${localPushClientId.value || '-'}`,
    `- 待消费落页：${pendingJumpTargetSummary.value}`,
    `- 最近消费结果：${jumpDiagnosticsLastConsumedStatus.value}`,
    `- 网关环境：${pushGatewayReady.value ? '可联调' : '待补充'}`,
    `- API 检测：${apiConnectivityLastTestLabel.value}`,
    `- API 地址：${resolvedApiBaseUrl.value}`,
    `- API 构建模式：${currentBuildMode.value}`,
    `- 最近一次测试：${lastPushTestVisible.value ? (lastPushTestSuccess.value ? '成功触发' : '触发失败') : '暂无'}`,
    `- TraceId：${lastPushTestTraceId.value || '-'}`,
    `- 最近推送事件：${latestPushEvent.value ? `${latestPushEvent.value.title || '未命名'} / ${latestPushEvent.value.occurredAt || '-'}` : '暂无'}`,
    `- 本地事件数：${pushEventCount.value} 条`
  ].join('\n')
})

function stringifyValue(value) {
  if (value === null || value === undefined || value === '') {
    return ''
  }
  if (typeof value === 'string') {
    return value
  }
  try {
    return JSON.stringify(value, null, 2)
  } catch (error) {
    return String(value)
  }
}

function parseStringifiedValue(value) {
  if (!value || typeof value !== 'string') {
    return value || null
  }
  try {
    return JSON.parse(value)
  } catch (error) {
    return value
  }
}

function openResolvedPushTarget(target, successTitle, afterOpen) {
  if (!target?.path) {
    uni.showToast({ title: '当前没有可打开的落页', icon: 'none' })
    return
  }
  const opened = openWorkerJumpTarget(target)
  if (!opened) {
    uni.showToast({ title: '落页打开失败', icon: 'none' })
    return
  }
  if (typeof afterOpen === 'function') {
    afterOpen()
  }
  uni.showToast({ title: successTitle, icon: 'none' })
}

function resolvePushTestTarget(record) {
  const rawPayload = record?.payload && typeof record.payload === 'object' ? record.payload : {}
  const path =
    record?.targetPath ||
    rawPayload.jumpPath ||
    rawPayload.path ||
    rawPayload.url ||
    rawPayload.routePath ||
    rawPayload.pagePath
  if (!path) {
    return { path: '', query: null, sourceLabel: record?.sourceLabel || rawPayload.sourceLabel || '' }
  }
  const normalizedTarget = normalizeWorkerJumpTarget({
    ...rawPayload,
    jumpPath: path,
    jumpQuery: record?.targetQuery ?? record?.targetQueryText ?? rawPayload.jumpQuery ?? rawPayload.query ?? rawPayload.params,
    sourceLabel: record?.sourceLabel || rawPayload.sourceLabel || rawPayload.sourceText || ''
  })
  return {
    path: normalizedTarget?.path || '',
    query: normalizedTarget?.query ?? null,
    sourceLabel: record?.sourceLabel || normalizedTarget?.sourceLabel || ''
  }
}

function refreshDeviceAcceptanceOverview() {
  deviceAcceptanceOverview.value = getWorkerAcceptanceOverview()
}

function refreshAcceptanceOperator() {
  acceptanceOperatorInput.value = String(uni.getStorageSync(WORKER_ACCEPTANCE_OPERATOR_KEY) || '').trim()
}

function refreshAcceptancePrerequisiteItems() {
  acceptancePrerequisiteItems.value = getWorkerAcceptancePrerequisiteItems()
  acceptancePrerequisiteNotes.value = acceptancePrerequisiteItems.value.reduce((result, item) => {
    result[item.key] = item.note || ''
    return result
  }, {})
}

function refreshAcceptanceApiItems() {
  acceptanceApiItems.value = getWorkerAcceptanceApiItems()
  acceptanceApiNotes.value = acceptanceApiItems.value.reduce((result, item) => {
    result[item.key] = item.note || ''
    return result
  }, {})
}

function refreshAcceptancePhaseItems() {
  acceptancePhaseItems.value = getWorkerAcceptancePhaseItems()
  acceptancePhaseNotes.value = acceptancePhaseItems.value.reduce((result, item) => {
    result[item.key] = item.note || ''
    return result
  }, {})
}

function refreshAcceptanceResultItems() {
  acceptanceResultItems.value = getWorkerAcceptanceResultItems()
  acceptanceResultNotes.value = acceptanceResultItems.value.reduce((result, item) => {
    result[item.key] = item.note || ''
    return result
  }, {})
  acceptanceResultVerificationTerminals.value = acceptanceResultItems.value.reduce((result, item) => {
    result[item.key] = item.verificationTerminal || ''
    return result
  }, {})
  acceptanceResultEvidenceSources.value = acceptanceResultItems.value.reduce((result, item) => {
    result[item.key] = item.evidenceSource || ''
    return result
  }, {})
}

function refreshAcceptanceIssueItems() {
  acceptanceIssueItems.value = getWorkerAcceptanceIssueItems()
}

function refreshAcceptanceEvidenceItems() {
  acceptanceEvidenceItems.value = getWorkerAcceptanceEvidenceItems()
}

function resolveAcceptancePhaseTagClass(status) {
  if (status === 'passed') {
    return 'worker-tag--success'
  }
  if (status === 'blocked') {
    return 'worker-tag--danger'
  }
  if (status === 'in_progress') {
    return 'worker-tag--warning'
  }
  return 'worker-tag--info'
}

function resolveAcceptancePrerequisiteTagClass(status) {
  return resolveAcceptancePhaseTagClass(status)
}

function resolveAcceptanceApiTagClass(status) {
  return resolveAcceptancePhaseTagClass(status)
}

function resolveAcceptanceResultTagClass(status) {
  if (status === 'passed') {
    return 'worker-tag--success'
  }
  if (status === 'failed') {
    return 'worker-tag--warning'
  }
  if (status === 'blocked') {
    return 'worker-tag--danger'
  }
  return 'worker-tag--info'
}

function applyLastServerPushTest(record) {
  lastPushTestVisible.value = !!record
  if (!record) {
    lastPushTestSuccess.value = false
    lastPushTestAt.value = ''
    lastPushTestTraceId.value = ''
    lastPushTestTargetPath.value = ''
    lastPushTestTargetQueryText.value = ''
    lastPushTestStatusText.value = ''
    lastPushTestResponseBody.value = ''
    lastPushTestSourceLabel.value = ''
    return
  }
  const resolvedTarget = resolvePushTestTarget(record)
  lastPushTestSuccess.value = String(record.testStatus || '').toUpperCase() === 'SUCCESS'
  lastPushTestAt.value = record.testedAt || ''
  lastPushTestTraceId.value = record.traceId || ''
  lastPushTestTargetPath.value = resolvedTarget.path || ''
  lastPushTestTargetQueryText.value = stringifyValue(resolvedTarget.query ?? record.targetQueryText)
  lastPushTestStatusText.value = record.testStatusText || record.statusMessage || ''
  lastPushTestResponseBody.value = stringifyValue(record.responseBody)
  lastPushTestSourceLabel.value = resolvedTarget.sourceLabel || ''
}

function applyServerPushTestHistory(records) {
  if (!Array.isArray(records) || !records.length) {
    serverPushTestHistory.value = []
    return
  }
  serverPushTestHistory.value = records.map((item) => {
    const resolvedTarget = resolvePushTestTarget(item)
    return {
      ...item,
      targetPath: resolvedTarget.path || item.targetPath || '',
      sourceLabel: resolvedTarget.sourceLabel || item.sourceLabel || '',
      targetQueryText: stringifyValue(resolvedTarget.query ?? item.targetQueryText),
      responseBody: stringifyValue(item.responseBody)
    }
  })
}

function loadNotificationPermission() {
  notificationPermission.value = resolveNotificationPermission()
}

async function refreshPushDiagnostics() {
  localPushClientId.value = await getPushClientIdSafe()
  const diagnostics = getWorkerPushDiagnostics()
  const jumpDiagnostics = getWorkerJumpDiagnostics()
  pushDiagnosticsLastAttemptAt.value = diagnostics.lastAttemptAt || ''
  pushDiagnosticsLastResult.value = diagnostics.lastResult || 'not_started'
  pushDiagnosticsLastMessage.value = diagnostics.lastMessage || ''
  pushDiagnosticsRequestPayload.value = stringifyValue(diagnostics.requestPayload)
  if (!pushDiagnosticsLastMessage.value && pushDiagnosticsRequestPayload.value) {
    pushDiagnosticsLastMessage.value = pushDiagnosticsRequestPayload.value
  }
  pushEventCount.value = getRecentWorkerPushEvents().length
  jumpDiagnosticsLastSavedStatus.value = [jumpDiagnostics.lastSavedResult || '未写入', jumpDiagnostics.lastSavedAt || '']
    .filter(Boolean)
    .join(' / ') || '未写入'
  jumpDiagnosticsLastConsumedStatus.value = [jumpDiagnostics.lastConsumedResult || '未消费', jumpDiagnostics.lastConsumedAt || '']
    .filter(Boolean)
    .join(' / ') || '未消费'
  jumpDiagnosticsLastOpenedStatus.value = [jumpDiagnostics.lastOpenedResult || '未打开', jumpDiagnostics.lastOpenedAt || '']
    .filter(Boolean)
    .join(' / ') || '未打开'
  jumpDiagnosticsLastMessage.value =
    jumpDiagnostics.lastConsumedMessage || jumpDiagnostics.lastOpenedMessage || jumpDiagnostics.lastSavedMessage || ''
  const pendingTarget = readPendingWorkerJumpTarget()
  if (pendingTarget?.path) {
    pendingJumpTargetSummary.value = `${pendingTarget.path}${pendingTarget.savedAt ? ` / ${pendingTarget.savedAt}` : ''}`
    pendingJumpTargetPath.value = pendingTarget.path || ''
    pendingJumpTargetQueryText.value = stringifyValue(pendingTarget.query || {})
    pendingJumpTargetDetail.value = stringifyValue({
      path: pendingTarget.path,
      query: pendingTarget.query || {},
      sourceLabel: pendingTarget.sourceLabel || '',
      actionLabel: pendingTarget.actionLabel || '',
      savedAt: pendingTarget.savedAt || ''
    })
    refreshDeviceAcceptanceOverview()
    return
  }
  pendingJumpTargetSummary.value = '无'
  pendingJumpTargetPath.value = ''
  pendingJumpTargetQueryText.value = ''
  pendingJumpTargetDetail.value = ''
  refreshDeviceAcceptanceOverview()
}

async function clearPendingJumpTargetSnapshot() {
  clearPendingWorkerJumpTarget()
  await refreshPushDiagnostics()
  uni.showToast({ title: '待跳转目标已清空', icon: 'none' })
}

function openPendingJumpTargetManually() {
  const pendingTarget = normalizeWorkerJumpTarget({
    jumpPath: pendingJumpTargetPath.value,
    jumpQuery: parseStringifiedValue(pendingJumpTargetQueryText.value)
  })
  openResolvedPushTarget(pendingTarget, '已打开待消费落页', async () => {
    clearPendingWorkerJumpTarget()
    await refreshPushDiagnostics()
  })
}

function openLastPushTestTarget() {
  const target = normalizeWorkerJumpTarget({
    jumpPath: lastPushTestTargetPath.value,
    jumpQuery: parseStringifiedValue(lastPushTestTargetQueryText.value),
    sourceLabel: lastPushTestSourceLabel.value
  })
  openResolvedPushTarget(target, '已打开测试目标')
}

function openServerPushHistoryTarget(item) {
  const target = normalizeWorkerJumpTarget({
    jumpPath: item?.targetPath,
    jumpQuery: parseStringifiedValue(item?.targetQueryText),
    sourceLabel: item?.sourceLabel
  })
  openResolvedPushTarget(target, '已打开历史测试目标')
}

function openRecentPushEventTarget(item) {
  const target = normalizeWorkerJumpTarget({
    jumpPath: item?.targetPath,
    jumpQuery: parseStringifiedValue(item?.targetQueryText),
    sourceLabel: item?.sourceLabel,
    actionLabel: item?.actionLabel
  })
  openResolvedPushTarget(target, '已打开推送目标')
}

function normalizeApiBaseUrlInput() {
  return String(apiBaseUrlInput.value || '').trim().replace(/\/+$/, '')
}

function persistApiConnectivitySnapshot() {
  uni.setStorageSync(WORKER_API_CONNECTIVITY_SNAPSHOT_KEY, {
    apiConnectivityStatusText: apiConnectivityStatusText.value,
    lastSuccessfulApiBaseUrl: lastSuccessfulApiBaseUrl.value,
    apiConnectivityLastTestAt: apiConnectivityLastTestAt.value,
    apiConnectivityLastTestUrl: apiConnectivityLastTestUrl.value,
    apiConnectivityLastStatusCode: apiConnectivityLastStatusCode.value,
    apiConnectivityLastMessage: apiConnectivityLastMessage.value,
    apiConnectivityLastTestSuccess: apiConnectivityLastTestSuccess.value
  })
}

function restoreApiConnectivitySnapshot() {
  const snapshot = uni.getStorageSync(WORKER_API_CONNECTIVITY_SNAPSHOT_KEY) || {}
  apiConnectivityStatusText.value = snapshot.apiConnectivityStatusText || ''
  lastSuccessfulApiBaseUrl.value = snapshot.lastSuccessfulApiBaseUrl || ''
  apiConnectivityLastTestAt.value = snapshot.apiConnectivityLastTestAt || ''
  apiConnectivityLastTestUrl.value = snapshot.apiConnectivityLastTestUrl || ''
  apiConnectivityLastStatusCode.value = snapshot.apiConnectivityLastStatusCode || ''
  apiConnectivityLastMessage.value = snapshot.apiConnectivityLastMessage || ''
  apiConnectivityLastTestSuccess.value =
    typeof snapshot.apiConnectivityLastTestSuccess === 'boolean' ? snapshot.apiConnectivityLastTestSuccess : null
}

function resetApiConnectivitySnapshot() {
  apiConnectivityStatusText.value = ''
  apiConnectivityLastTestAt.value = ''
  apiConnectivityLastTestUrl.value = ''
  apiConnectivityLastStatusCode.value = ''
  apiConnectivityLastMessage.value = ''
  apiConnectivityLastTestSuccess.value = null
  lastSuccessfulApiBaseUrl.value = ''
  uni.removeStorageSync(WORKER_API_CONNECTIVITY_SNAPSHOT_KEY)
}

async function loadSettings() {
  try {
    const data = await getWorkerSettings()
    notifyEnabled.value = data?.notifyEnabled !== undefined ? !!data.notifyEnabled : true
    pushRegistered.value = !!data?.pushRegistered
    pushClientIdMasked.value = data?.pushClientIdMasked || ''
    pushPlatform.value = data?.pushPlatform || ''
    pushSyncTime.value = data?.pushSyncTime && data.pushSyncTime !== '-' ? data.pushSyncTime : ''
    pushStatusText.value = data?.pushStatusText || '当前仅保存业务通知偏好，待 App 真机登录后完成推送登记。'
    pushGatewayEnabled.value = !!data?.pushGatewayEnabled
    pushGatewayUrlConfigured.value = !!data?.pushGatewayUrlConfigured
    pushGatewayProvider.value = data?.pushGatewayProvider || 'custom'
    pushGatewayReady.value = !!data?.pushGatewayReady
    pushGatewayStatusText.value = data?.pushGatewayStatusText || '服务端推送网关未启用，发送测试推送会直接失败。'
    applyLastServerPushTest(data?.lastServerPushTest)
    applyServerPushTestHistory(data?.lastServerPushTests)
    recentPushEvents.value = getRecentWorkerPushEvents()
    apiBaseUrlInput.value = getBaseUrl()
    restoreApiConnectivitySnapshot()
  } catch (error) {
    uni.showToast({ title: error.message || '加载设置失败', icon: 'none' })
  }
}

async function refreshSettingsSnapshot() {
  await loadSettings()
  loadNotificationPermission()
  await refreshPushDiagnostics()
  refreshAcceptanceOperator()
  refreshDeviceAcceptanceOverview()
  refreshAcceptancePrerequisiteItems()
  refreshAcceptanceApiItems()
  refreshAcceptancePhaseItems()
  refreshAcceptanceIssueItems()
  refreshAcceptanceEvidenceItems()
  refreshAcceptanceResultItems()
}

async function toggleNotify(event) {
  const nextValue = !!event.detail.value
  if (loading.value) {
    return
  }
  loading.value = true
  try {
    const data = await saveWorkerSettings({ notifyEnabled: nextValue })
    notifyEnabled.value = data?.notifyEnabled !== undefined ? !!data.notifyEnabled : nextValue
    pushRegistered.value = !!data?.pushRegistered
    pushClientIdMasked.value = data?.pushClientIdMasked || pushClientIdMasked.value
    pushPlatform.value = data?.pushPlatform || pushPlatform.value
    pushSyncTime.value = data?.pushSyncTime && data.pushSyncTime !== '-' ? data.pushSyncTime : pushSyncTime.value
    pushStatusText.value = data?.pushStatusText || pushStatusText.value
    pushGatewayEnabled.value = !!data?.pushGatewayEnabled
    pushGatewayUrlConfigured.value = !!data?.pushGatewayUrlConfigured
    pushGatewayProvider.value = data?.pushGatewayProvider || pushGatewayProvider.value
    pushGatewayReady.value = !!data?.pushGatewayReady
    pushGatewayStatusText.value = data?.pushGatewayStatusText || pushGatewayStatusText.value
    uni.showToast({ title: notifyEnabled.value ? '已开启业务通知' : '已关闭业务通知', icon: 'none' })
  } catch (error) {
    notifyEnabled.value = !nextValue
    uni.showToast({ title: error.message || '保存设置失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

function clearCache() {
  const sessionSnapshot = getWorkerSessionSnapshot()
  const workerApiBaseUrl = uni.getStorageSync(WORKER_API_BASE_URL_STORAGE_KEY)
  uni.clearStorageSync()
  if (workerApiBaseUrl) {
    uni.setStorageSync(WORKER_API_BASE_URL_STORAGE_KEY, workerApiBaseUrl)
  }
  restoreWorkerSessionSnapshot(sessionSnapshot)
  uni.showToast({ title: '缓存已清理', icon: 'none' })
  recentPushEvents.value = getRecentWorkerPushEvents()
  apiBaseUrlInput.value = getBaseUrl()
  resetApiConnectivitySnapshot()
  refreshPushDiagnostics()
  refreshDeviceAcceptanceOverview()
}

function saveApiBaseUrl() {
  const nextValue = setBaseUrl(normalizeApiBaseUrlInput())
  apiBaseUrlInput.value = nextValue || DEFAULT_BASE_URL
  apiConnectivityStatusText.value = ''
  persistApiConnectivitySnapshot()
  uni.showToast({ title: nextValue ? '接口地址已保存' : '已恢复默认地址', icon: 'none' })
}

function resetApiBaseUrl() {
  setBaseUrl('')
  apiBaseUrlInput.value = DEFAULT_BASE_URL
  resetApiConnectivitySnapshot()
  uni.showToast({ title: '已恢复默认地址', icon: 'none' })
}

async function testApiBaseUrl() {
  if (apiConnectivityTesting.value) {
    return
  }
  const targetBaseUrl = normalizeApiBaseUrlInput() || getBaseUrl()
  if (!targetBaseUrl) {
    uni.showToast({ title: '请输入接口地址', icon: 'none' })
    return
  }
  apiConnectivityTesting.value = true
  apiConnectivityStatusText.value = ''
  apiConnectivityLastTestAt.value = new Date().toLocaleString()
  apiConnectivityLastTestUrl.value = targetBaseUrl
  apiConnectivityLastStatusCode.value = ''
  apiConnectivityLastMessage.value = ''
  apiConnectivityLastTestSuccess.value = null
  try {
    const result = await new Promise((resolve, reject) => {
      uni.request({
        url: `${targetBaseUrl}/captchaImage`,
        method: 'GET',
        timeout: 8000,
        success: (res) => {
          if (res.statusCode === 200) {
            resolve(res)
            return
          }
          reject(new Error(`接口可达但状态码异常：${res.statusCode}`))
        },
        fail: (error) => reject(error)
      })
    })
    apiConnectivityLastTestAt.value = new Date().toLocaleString()
    apiConnectivityLastStatusCode.value = String(result.statusCode || '')
    apiConnectivityLastMessage.value = `status=${result.statusCode}`
    apiConnectivityLastTestSuccess.value = true
    apiConnectivityStatusText.value = `最近检测：连通成功 ${apiConnectivityLastTestAt.value} / 状态码 ${result.statusCode}`
    lastSuccessfulApiBaseUrl.value = targetBaseUrl
    persistApiConnectivitySnapshot()
    uni.showToast({ title: '接口连通成功', icon: 'none' })
  } catch (error) {
    const errorMessage = error.message || error.errMsg || '请求失败'
    apiConnectivityLastTestAt.value = new Date().toLocaleString()
    apiConnectivityLastTestUrl.value = targetBaseUrl
    apiConnectivityLastStatusCode.value = ''
    apiConnectivityLastMessage.value = errorMessage
    apiConnectivityLastTestSuccess.value = false
    apiConnectivityStatusText.value = `最近检测：连通失败 ${apiConnectivityLastTestAt.value} / ${errorMessage}`
    lastSuccessfulApiBaseUrl.value = ''
    persistApiConnectivitySnapshot()
    uni.showToast({ title: errorMessage || '接口连通失败', icon: 'none' })
  } finally {
    apiConnectivityTesting.value = false
  }
}

function saveTestedApiBaseUrl() {
  if (!lastSuccessfulApiBaseUrl.value) {
    uni.showToast({ title: '请先检测成功后再保存', icon: 'none' })
    return
  }
  const nextValue = setBaseUrl(lastSuccessfulApiBaseUrl.value)
  apiBaseUrlInput.value = nextValue || DEFAULT_BASE_URL
  persistApiConnectivitySnapshot()
  uni.showToast({ title: '已保存检测通过的接口地址', icon: 'none' })
}

function openSystemNotificationSetting() {
  if (typeof uni.openAppAuthorizeSetting === 'function') {
    uni.openAppAuthorizeSetting({
      success: () => {
        loadNotificationPermission()
      },
      fail: () => {
        uni.showToast({ title: '当前环境暂不支持打开系统通知权限页', icon: 'none' })
      }
    })
    return
  }
  uni.showToast({ title: '当前环境暂不支持打开系统通知权限页', icon: 'none' })
}

async function triggerPushTest() {
  if (pushTesting.value) {
    return
  }
  pushTesting.value = true
  try {
    const data = await sendWorkerPushTest()
    pushRegistered.value = !!data?.pushRegistered || pushRegistered.value
    pushClientIdMasked.value = data?.pushClientIdMasked || pushClientIdMasked.value
    pushPlatform.value = data?.pushPlatform || pushPlatform.value
    pushStatusText.value = data?.pushStatusText || pushStatusText.value
    pushGatewayEnabled.value = !!data?.pushGatewayEnabled || pushGatewayEnabled.value
    pushGatewayUrlConfigured.value = !!data?.pushGatewayUrlConfigured || pushGatewayUrlConfigured.value
    pushGatewayProvider.value = data?.pushGatewayProvider || pushGatewayProvider.value
    pushGatewayReady.value = !!data?.pushGatewayReady || pushGatewayReady.value
    pushGatewayStatusText.value = data?.pushGatewayStatusText || pushGatewayStatusText.value

    lastPushTestVisible.value = true
    lastPushTestSuccess.value = true
    lastPushTestAt.value = data?.testedAt || ''
    lastPushTestTraceId.value = data?.traceId || ''
    const resolvedTarget = resolvePushTestTarget(data)
    lastPushTestTargetPath.value = resolvedTarget.path || ''
    lastPushTestTargetQueryText.value = stringifyValue(resolvedTarget.query)
    lastPushTestStatusText.value = data?.testStatusText || '服务端推送网关已受理本次测试请求，请在真机上确认送达和点击落页。'
    lastPushTestResponseBody.value = stringifyValue(data?.responseBody)
    lastPushTestSourceLabel.value = resolvedTarget.sourceLabel || ''

    await refreshSettingsSnapshot()
    uni.showModal({
      title: '测试推送已触发',
      content: stringifyValue(data?.responseBody) || '已调用服务端推送网关，请在真机上确认是否收到消息。',
      showCancel: false
    })
  } catch (error) {
    lastPushTestVisible.value = true
    lastPushTestSuccess.value = false
    lastPushTestAt.value = new Date().toLocaleString()
    lastPushTestTraceId.value = ''
    lastPushTestTargetPath.value = ''
    lastPushTestTargetQueryText.value = ''
    lastPushTestStatusText.value = error.message || '测试推送发送失败'
    lastPushTestResponseBody.value = ''
    lastPushTestSourceLabel.value = ''
    uni.showToast({ title: error.message || '测试推送发送失败', icon: 'none' })
  } finally {
    pushTesting.value = false
  }
}

async function triggerPushRegistration() {
  if (pushRegistering.value) {
    return
  }
  pushRegistering.value = true
  try {
    await syncWorkerPushRegistration()
    await refreshSettingsSnapshot()
    uni.showToast({ title: '推送登记已刷新', icon: 'none' })
  } catch (error) {
    await refreshPushDiagnostics()
    uni.showToast({ title: error.message || '推送登记失败', icon: 'none' })
  } finally {
    pushRegistering.value = false
  }
}

function clearPushEventHistory() {
  clearRecentWorkerPushEvents()
  recentPushEvents.value = []
  pushEventCount.value = 0
  refreshDeviceAcceptanceOverview()
  uni.showToast({ title: '推送事件已清空', icon: 'none' })
}

function clearPushDiagnosticsSnapshot() {
  clearWorkerPushDiagnostics()
  clearWorkerJumpDiagnostics()
  refreshPushDiagnostics()
  refreshDeviceAcceptanceOverview()
  uni.showToast({ title: '本地推送快照已清空', icon: 'none' })
}

async function clearAllAcceptanceDiagnostics() {
  clearWorkerAcceptanceDiagnostics()
  recentPushEvents.value = []
  serverPushTestHistory.value = []
  lastPushTestVisible.value = false
  await refreshSettingsSnapshot()
  uni.showToast({ title: '联调数据已清空', icon: 'none' })
}

async function clearAllAcceptanceArtifacts() {
  clearWorkerAcceptanceDiagnostics()
  clearWorkerAcceptanceLocalRecords()
  clearRecentWorkerPushEvents()
  clearWorkerPushDiagnostics()
  clearWorkerJumpDiagnostics()
  recentPushEvents.value = []
  serverPushTestHistory.value = []
  lastPushTestVisible.value = false
  await refreshSettingsSnapshot()
  uni.showToast({ title: '全部联调留痕已清空', icon: 'none' })
}

function saveAcceptanceOperator() {
  const operator = acceptanceOperatorInput.value.trim()
  if (!operator) {
    uni.showToast({ title: '请输入联调人姓名', icon: 'none' })
    return
  }
  uni.setStorageSync(WORKER_ACCEPTANCE_OPERATOR_KEY, operator)
  refreshAcceptanceOperator()
  refreshDeviceAcceptanceOverview()
  uni.showToast({ title: '联调人已保存', icon: 'none' })
}

function clearAcceptanceOperator() {
  uni.removeStorageSync(WORKER_ACCEPTANCE_OPERATOR_KEY)
  refreshAcceptanceOperator()
  refreshDeviceAcceptanceOverview()
  uni.showToast({ title: '联调人已清空', icon: 'none' })
}

function setAcceptanceApi(key, status) {
  acceptanceApiItems.value = setWorkerAcceptanceApiStatus(key, status)
  refreshAcceptanceApiItems()
  uni.showToast({ title: '接口清单状态已记录', icon: 'none' })
}

function setAcceptancePrerequisite(key, status) {
  acceptancePrerequisiteItems.value = setWorkerAcceptancePrerequisiteStatus(key, status)
  refreshAcceptancePrerequisiteItems()
  uni.showToast({ title: '前置检查状态已记录', icon: 'none' })
}

function saveAcceptancePrerequisiteNote(item) {
  acceptancePrerequisiteItems.value = setWorkerAcceptancePrerequisiteNote(item.key, acceptancePrerequisiteNotes.value[item.key] || '')
  refreshAcceptancePrerequisiteItems()
  uni.showToast({ title: '前置检查备注已保存', icon: 'none' })
}

function clearAcceptancePrerequisites() {
  clearWorkerAcceptancePrerequisiteItems()
  refreshAcceptancePrerequisiteItems()
  uni.showToast({ title: '前置检查已清空', icon: 'none' })
}

function saveAcceptanceApiNote(item) {
  acceptanceApiItems.value = setWorkerAcceptanceApiNote(item.key, acceptanceApiNotes.value[item.key] || '')
  refreshAcceptanceApiItems()
  uni.showToast({ title: '接口清单备注已保存', icon: 'none' })
}

function clearAcceptanceApis() {
  clearWorkerAcceptanceApiItems()
  refreshAcceptanceApiItems()
  uni.showToast({ title: '接口清单已清空', icon: 'none' })
}

function setAcceptancePhase(key, status) {
  acceptancePhaseItems.value = setWorkerAcceptancePhaseStatus(key, status)
  refreshAcceptancePhaseItems()
  uni.showToast({ title: '阶段状态已记录', icon: 'none' })
}

function saveAcceptancePhaseNote(item) {
  acceptancePhaseItems.value = setWorkerAcceptancePhaseNote(item.key, acceptancePhaseNotes.value[item.key] || '')
  refreshAcceptancePhaseItems()
  uni.showToast({ title: '阶段备注已保存', icon: 'none' })
}

function clearAcceptancePhases() {
  clearWorkerAcceptancePhaseItems()
  refreshAcceptancePhaseItems()
  uni.showToast({ title: '阶段状态已清空', icon: 'none' })
}

function updateAcceptanceIssueField(index, field, value) {
  acceptanceIssueItems.value = acceptanceIssueItems.value.map((item, currentIndex) => {
    if (currentIndex !== index) {
      return item
    }
    return {
      ...item,
      [field]: value
    }
  })
}

function saveAcceptanceIssue(index) {
  const item = acceptanceIssueItems.value[index]
  if (!item) {
    uni.showToast({ title: '当前没有可保存的问题项', icon: 'none' })
    return
  }
  acceptanceIssueItems.value = setWorkerAcceptanceIssueItem(index, {
    module: item.module,
    phenomenon: item.phenomenon,
    reproduceCondition: item.reproduceCondition,
    preliminaryJudgment: item.preliminaryJudgment,
    status: item.status
  })
  refreshAcceptanceIssueItems()
  uni.showToast({ title: '问题记录已保存', icon: 'none' })
}

function updateAcceptanceEvidenceField(index, field, value) {
  acceptanceEvidenceItems.value = acceptanceEvidenceItems.value.map((item, currentIndex) => {
    if (currentIndex !== index) {
      return item
    }
    return {
      ...item,
      [field]: value
    }
  })
}

function saveAcceptanceEvidence(index) {
  const item = acceptanceEvidenceItems.value[index]
  if (!item) {
    uni.showToast({ title: '当前没有可保存的证据项', icon: 'none' })
    return
  }
  acceptanceEvidenceItems.value = setWorkerAcceptanceEvidenceItem(index, {
    pageEvidence: item.pageEvidence,
    apiEvidence: item.apiEvidence,
    serverLogTime: item.serverLogTime,
    issueTicket: item.issueTicket,
    note: item.note
  })
  refreshAcceptanceEvidenceItems()
  uni.showToast({ title: '证据记录已保存', icon: 'none' })
}

function clearAcceptanceEvidence() {
  clearWorkerAcceptanceEvidenceItems()
  refreshAcceptanceEvidenceItems()
  uni.showToast({ title: '证据清单已清空', icon: 'none' })
}

function setAcceptanceResult(key, status) {
  acceptanceResultItems.value = setWorkerAcceptanceResultStatus(key, status)
  refreshAcceptanceResultItems()
  uni.showToast({ title: '联调结果已记录', icon: 'none' })
}

function saveAcceptanceResultNote(item) {
  acceptanceResultItems.value = setWorkerAcceptanceResultNote(item.key, {
    note: acceptanceResultNotes.value[item.key] || '',
    verificationTerminal: acceptanceResultVerificationTerminals.value[item.key] || '',
    evidenceSource: acceptanceResultEvidenceSources.value[item.key] || ''
  })
  refreshAcceptanceResultItems()
  uni.showToast({ title: '结果记录已保存', icon: 'none' })
}

function clearAcceptanceResults() {
  clearWorkerAcceptanceResultItems()
  refreshAcceptanceResultItems()
  uni.showToast({ title: '结果记录已清空', icon: 'none' })
}

function clearAcceptanceIssues() {
  clearWorkerAcceptanceIssueItems()
  refreshAcceptanceIssueItems()
  uni.showToast({ title: '问题记录已清空', icon: 'none' })
}

function copyText(content, successTitle) {
  if (!content) {
    uni.showToast({ title: '暂无可复制内容', icon: 'none' })
    return
  }
  if (typeof uni.setClipboardData === 'function') {
    uni.setClipboardData({
      data: content,
      success: () => {
        uni.showToast({ title: successTitle, icon: 'none' })
      },
      fail: () => {
        uni.showToast({ title: '复制失败，请改用截图', icon: 'none' })
      }
    })
    return
  }
  uni.showToast({ title: '当前环境不支持复制，请改用截图', icon: 'none' })
}

function goAccountSecurity() {
  uni.navigateTo({ url: '/pages/profile/account-security' })
}

function goPrivacy() {
  uni.navigateTo({ url: '/pages/profile/privacy' })
}

function openPushDiagnosticsPage() {
  uni.navigateTo({ url: '/pages/profile/settings' })
}

function openCameraDiagnosticsPage() {
  uni.navigateTo({ url: '/pages/camera/index' })
}

function openComplaintDiagnosticsPage() {
  uni.navigateTo({ url: '/pages/complaint/index' })
}

function openAttendanceDiagnosticsPage() {
  uni.navigateTo({ url: '/pages/attendance/checkin' })
}

function openMapDiagnosticsPage() {
  uni.navigateTo({ url: '/pages/job/map' })
}

function openAcceptanceResultPage(item) {
  if (!item?.pagePath) {
    uni.showToast({ title: '当前未配置目标页面', icon: 'none' })
    return
  }
  openPage(item.pagePath)
}

function openAcceptancePhasePage(item) {
  if (!item?.pagePath) {
    uni.showToast({ title: '当前未配置目标页面', icon: 'none' })
    return
  }
  openPage(item.pagePath)
}

function openAcceptanceApiPage(item) {
  if (!item?.pagePath) {
    uni.showToast({ title: '当前未配置目标页面', icon: 'none' })
    return
  }
  openPage(item.pagePath)
}

function openAcceptancePrerequisitePage(item) {
  if (!item?.pagePath) {
    uni.showToast({ title: '当前未配置目标页面', icon: 'none' })
    return
  }
  openPage(item.pagePath)
}

function logout() {
  clearCurrentWorkerAuthState()
  resetApiConnectivitySnapshot()
  uni.reLaunch({ url: '/pages/login/index' })
}

onShow(async () => {
  await refreshSettingsSnapshot()
})
</script>

<style lang="scss">
.worker-card + .worker-card {
  margin-top: 24rpx;
}

.settings-hint {
  margin-top: 12rpx;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 18rpx;
}

.section-head--sub {
  margin-top: 20rpx;
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18rpx;
  margin-bottom: 18rpx;
}

.settings-input {
  width: 100%;
  margin-top: 12rpx;
  padding: 20rpx 24rpx;
  border-radius: 18rpx;
  background: #f5f8fc;
  box-sizing: border-box;
  font-size: 28rpx;
  color: #16324f;
}

.settings-input--compact {
  padding-top: 18rpx;
  padding-bottom: 18rpx;
  font-size: 24rpx;
}

.settings-action-row {
  display: flex;
  gap: 18rpx;
  margin-top: 20rpx;
}

.settings-action-row--wrap {
  flex-wrap: wrap;
}

.settings-action-row--compact {
  margin-top: 14rpx;
}

.settings-textarea {
  width: 100%;
  min-height: 112rpx;
  margin-top: 14rpx;
  padding: 18rpx 20rpx;
  border-radius: 18rpx;
  background: #f5f8fc;
  box-sizing: border-box;
  font-size: 24rpx;
  color: #16324f;
  line-height: 1.6;
}

.history-row__actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 12rpx;
}

.settings-action-row button {
  flex: 1;
}

.api-status-text {
  margin-top: 12rpx;
  color: #58738f;
}

.status-item {
  padding: 20rpx;
  border-radius: 18rpx;
  background: #f5f8fc;
}

.status-item__label {
  font-size: 22rpx;
  color: #7890aa;
}

.status-item__value {
  margin-top: 10rpx;
  font-size: 28rpx;
  font-weight: 600;
  color: #16324f;
  word-break: break-all;
}

.detail-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
  padding: 18rpx 0;
  border-bottom: 1rpx solid #edf2f7;
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-row__label {
  font-size: 26rpx;
  color: #5f7893;
}

.detail-row__value {
  flex: 1;
  text-align: right;
  font-size: 26rpx;
  color: #16324f;
  line-height: 1.6;
  word-break: break-all;
}

.history-row {
  padding: 22rpx 0;
  border-bottom: 1rpx solid #edf2f7;
}

.history-row:last-child {
  border-bottom: none;
}

.history-row__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
}

.history-row__title {
  flex: 1;
  font-size: 28rpx;
  font-weight: 600;
  color: #16324f;
  word-break: break-all;
}

.history-row__meta {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #7890aa;
}

.history-row__detail {
  margin-top: 8rpx;
  font-size: 24rpx;
  line-height: 1.6;
  color: #58738f;
  word-break: break-all;
}

.result-block {
  margin-top: 16rpx;
  padding: 22rpx 24rpx;
  border-radius: 20rpx;
  background: #f5f8fc;
}

.result-block__label {
  font-size: 22rpx;
  color: #7890aa;
}

.result-block__value {
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: #16324f;
  white-space: pre-wrap;
  word-break: break-all;
}

.settings-list {
  margin-top: 8rpx;
}

.settings-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #edf2f7;
  font-size: 28rpx;
  color: #16324f;
}

.settings-row:last-child {
  border-bottom: none;
}

.settings-row--switch {
  align-items: flex-start;
}

.settings-row__main {
  flex: 1;
}

.settings-row__title {
  font-size: 28rpx;
  color: #16324f;
}

.settings-row__desc {
  margin-top: 10rpx;
  font-size: 22rpx;
  line-height: 1.6;
  color: #7890aa;
}

.settings-row--danger {
  color: #dc3545;
}

.clear-action {
  font-size: 24rpx;
  color: #1f6fd6;
}

.worker-tag--success {
  background: #e9f8ef;
  color: #1f8b4d;
}

.worker-tag--warning {
  background: #fff3e6;
  color: #c77418;
}

.worker-tag--danger {
  background: #fdecec;
  color: #c24141;
}

.worker-tag--info {
  background: #eef6ff;
  color: #1f6fd6;
}
</style>
