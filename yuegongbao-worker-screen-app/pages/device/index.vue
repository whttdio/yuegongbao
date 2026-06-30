<template>
  <view class="screen-page screen-shell">
    <view class="screen-layout">
      <ScreenSidebar current-path="/pages/device/index" />

      <view class="screen-layout__content">
        <view class="screen-stack">
          <view class="screen-topbar">
            <view class="screen-brand">
              <text class="screen-overline">DEVICE OPS PANEL</text>
              <text class="screen-title">设备运维面板</text>
              <text class="screen-desc">联调快照：设备页统一展示诊断状态、配置参数和设备动作回执，重启/关机/导出日志均走真实接口。</text>
            </view>
            <text class="screen-meta-pill">管理员入口</text>
          </view>

          <view class="screen-device-layout">
            <view class="screen-device-sidebar">
              <view class="screen-card">
                <text class="screen-section-title">状态诊断</text>
                <text class="screen-section-subtitle">{{ tipsText }}</text>
                <view class="screen-list">
                  <view class="screen-list-item" v-for="item in diagnostics" :key="item.title">
                    <view>
                      <text class="screen-list-title">{{ item.title }}</text>
                      <text class="screen-list-desc">{{ item.desc }}</text>
                    </view>
                    <text class="screen-status" :class="item.className">{{ item.status }}</text>
                  </view>
                </view>
              </view>

              <view class="screen-card">
                <text class="screen-section-title">动作回执</text>
                <text class="screen-section-subtitle">服务屏设备动作执行后统一展示回执文案，便于大厅值守人员确认。</text>
                <view class="screen-panel">
                  <text class="screen-panel-title">{{ feedbackTitle }}</text>
                  <text class="screen-panel-desc">{{ feedbackText }}</text>
                </view>
              </view>
            </view>

            <view class="screen-card">
              <text class="screen-section-title">参数配置</text>
              <text class="screen-section-subtitle">Wi-Fi、工作时段、人脸阈值和设备编号可直接保存，字段回执来自后端动作记录。</text>
              <view class="screen-config-grid">
                <view class="screen-config-item">
                  <text class="screen-config-label">Wi-Fi 名称</text>
                  <input v-model="form.wifiName" class="screen-config-input" />
                </view>
                <view class="screen-config-item">
                  <text class="screen-config-label">工作时段</text>
                  <input v-model="form.workTime" class="screen-config-input" />
                </view>
                <view class="screen-config-item">
                  <text class="screen-config-label">人脸阈值</text>
                  <input v-model="form.faceThreshold" class="screen-config-input" />
                </view>
                <view class="screen-config-item">
                  <text class="screen-config-label">设备编号</text>
                  <input v-model="form.deviceCode" class="screen-config-input" />
                </view>
              </view>

              <view class="screen-divider" />

              <view class="screen-button-row">
                <view class="screen-button screen-button-primary" @click="handleSaveConfig">{{ saving ? '保存中' : '保存配置' }}</view>
                <view class="screen-button" @click="handleRestart">{{ restarting ? '处理中' : '重启设备' }}</view>
                <view class="screen-button screen-button-danger" @click="handleShutdown">{{ shuttingDown ? '处理中' : '设备关机' }}</view>
                <view class="screen-button" @click="handleExportLog">{{ exporting ? '处理中' : '导出日志' }}</view>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import ScreenSidebar from '../../components/ScreenSidebar.vue'
import {
  exportDeviceLogPlaceholder,
  getScreenDeviceDashboard,
  restartDevicePlaceholder,
  saveDeviceConfigDraft,
  shutdownDevicePlaceholder
} from '../../api/screen'
import { safeHideTabBar } from '../../utils/navigation'

const diagnostics = ref([])
const tips = ref([])
const form = reactive({
  wifiName: '',
  workTime: '',
  faceThreshold: '',
  deviceCode: ''
})

const saving = ref(false)
const restarting = ref(false)
const shuttingDown = ref(false)
const exporting = ref(false)
const feedbackTitle = ref('等待操作')
const feedbackText = ref('尚未执行设备动作。执行保存、重启、关机或导出日志后将在此显示最新回执。')

const tipsText = computed(() => {
  if (!tips.value.length) {
    return '设备状态、参数和动作回执已切换到服务屏后端接口。'
  }
  return tips.value.join(' ')
})

function notify(message) {
  uni.showToast({ title: message, icon: 'none' })
}

function setFeedback(title, message) {
  feedbackTitle.value = title
  feedbackText.value = message
}

async function loadData() {
  const data = await getScreenDeviceDashboard()
  diagnostics.value = Array.isArray(data.diagnostics) ? data.diagnostics : []
  tips.value = Array.isArray(data.tips) ? data.tips : []
  Object.assign(form, data.form || {})
}

async function handleSaveConfig() {
  saving.value = true
  try {
    const result = await saveDeviceConfigDraft({ ...form })
    const message = result.message || '设备配置已保存'
    setFeedback('配置保存完成', message)
    notify(message)
  } finally {
    saving.value = false
  }
}

async function handleRestart() {
  restarting.value = true
  try {
    const result = await restartDevicePlaceholder()
    const message = result.message || '设备重启指令已登记'
    setFeedback('重启回执', message)
    notify(message)
  } finally {
    restarting.value = false
  }
}

async function handleShutdown() {
  shuttingDown.value = true
  try {
    const result = await shutdownDevicePlaceholder()
    const message = result.message || '设备关机指令已登记'
    setFeedback('关机回执', message)
    notify(message)
  } finally {
    shuttingDown.value = false
  }
}

async function handleExportLog() {
  exporting.value = true
  try {
    const result = await exportDeviceLogPlaceholder()
    const message = result.message || '设备日志导出任务已登记'
    setFeedback('日志导出回执', message)
    notify(message)
  } finally {
    exporting.value = false
  }
}

onMounted(() => {
  loadData()
})

onShow(() => {
  safeHideTabBar()
})
</script>

<style lang="scss">
.screen-config-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20rpx;
  margin-bottom: 24rpx;
}

.screen-config-item {
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}

.screen-config-label {
  font-size: 24rpx;
  color: $screen-subtext;
}

.screen-config-input {
  min-height: 88rpx;
  padding: 0 22rpx;
  border-radius: 20rpx;
  background: rgba(248, 251, 255, 0.96);
  border: 2rpx solid $screen-border;
  font-size: 28rpx;
  color: $screen-text;
}

.screen-button-danger {
  background: linear-gradient(135deg, #f06b6b 0%, #d94848 100%);
  color: #ffffff;
}
</style>
