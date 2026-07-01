<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page worker-page--tab enterprise-page">
    <view class="worker-card worker-hero">
      <view class="section-head">
        <view>
          <view class="worker-title worker-title--display">{{ enterpriseName }}</view>
          <view class="worker-subtitle">{{ accountTitle }}</view>
        </view>
        <view class="worker-tag worker-tag--notice">{{ enterpriseRole }}</view>
      </view>
      <view class="enterprise-hero__meta">
        <view class="worker-tag">{{ scopeText }}</view>
        <view class="worker-tag worker-tag--info">未读通知 {{ unreadNoticeCount }}</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="enterprise-panel-head">
        <view class="enterprise-panel-head__meta">
          <view class="worker-title">账号与权限</view>
          <view class="worker-caption">展示当前企业账号、管理范围和接口健康状态。</view>
        </view>
      </view>
      <view class="enterprise-overview-grid">
        <view class="enterprise-overview-item" v-for="item in overviewItems" :key="item.label">
          <view class="enterprise-overview-item__label">{{ item.label }}</view>
          <view class="enterprise-overview-item__value">{{ item.value }}</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="enterprise-panel-head">
        <view class="enterprise-panel-head__meta">
          <view class="worker-title">企业主链健康</view>
          <view class="worker-caption">首页、工作台和主链业务接口的实时概览。</view>
        </view>
      </view>
      <view class="enterprise-kpi-grid">
        <view v-for="item in healthCards" :key="item.label" class="enterprise-kpi">
          <view class="enterprise-kpi__label">{{ item.label }}</view>
          <view class="enterprise-kpi__value">{{ item.value }}</view>
          <view class="enterprise-kpi__desc">{{ item.desc }}</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="enterprise-panel-head">
        <view class="enterprise-panel-head__meta">
          <view class="worker-title">管理入口</view>
          <view class="worker-caption">仅保留企业端合理能力，避免落回 worker 个人语义。</view>
        </view>
      </view>
      <view class="enterprise-entry-list">
        <view v-for="item in manageEntries" :key="item.path" class="enterprise-entry-row" @click="openPage(item.path)">
          <view>
            <view class="enterprise-entry-row__title">{{ item.title }}</view>
            <view class="enterprise-entry-row__desc">{{ item.desc }}</view>
          </view>
          <view class="enterprise-entry-row__side">{{ item.tag }}</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="enterprise-notice enterprise-notice--readonly">
        <view class="enterprise-notice__head">
          <view class="enterprise-notice__title">企业服务协同页</view>
          <view class="worker-tag worker-tag--info">只读协同</view>
        </view>
        <view class="enterprise-notice__desc">
          合同、社保、个税、工资、培训学习和员工履历类页面保留查看，但企业端不再触发员工本人办理动作。
        </view>
        <view class="enterprise-notice__reason">
          需要实名、打卡、投递、投诉、积分兑换、活动报名等个人链路的能力，统一改为企业侧说明或只读参考。
        </view>
        <view class="enterprise-notice__actions">
          <button class="worker-button worker-button--secondary" @click="openPage('/pages/workbench/index')">去企业主链</button>
          <button class="worker-button" @click="openPage('/pages/profile/help')">查看帮助</button>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="enterprise-panel-head">
        <view class="enterprise-panel-head__meta">
          <view class="worker-title">系统操作</view>
          <view class="worker-caption">保留企业账号侧的本地环境检测和退出操作。</view>
        </view>
      </view>
      <view class="settings-list">
        <view class="settings-row" @click="openPage('/pages/profile/settings')">系统设置</view>
        <view class="settings-row" @click="clearCache">清理缓存</view>
        <view class="settings-row settings-row--danger settings-row--no-arrow" @click="logout">退出登录</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getEnterpriseHomeDashboard, getEnterpriseWorkbenchDashboard } from '../../api/enterprise'
import { getWorkerProfile } from '../../api/enterprise-service'
import { buildEnterpriseHealthCards, buildEnterpriseScopeText } from '../../utils/enterprise-semantic'
import { openPage } from '../../utils/navigation'
import { clearCurrentWorkerAuthState } from '../../utils/request'

const profile = ref({})
const home = ref({})
const workbench = ref({})

const enterpriseName = computed(() => profile.value.enterpriseName || '企业账号')
const accountTitle = computed(() => {
  const parts = [profile.value.nickName || profile.value.personName || '企业管理员', profile.value.mobileMasked || profile.value.mobile || '']
    .filter(Boolean)
  return parts.join(' / ')
})
const enterpriseRole = computed(() => profile.value.workerType || '企业管理员')
const unreadNoticeCount = computed(() => profile.value.unreadNoticeCount || 0)
const scopeText = computed(() => buildEnterpriseScopeText({
  enterpriseName: profile.value.enterpriseName,
  jobType: profile.value.jobType,
  employmentStatus: profile.value.employmentStatus,
  insuranceStatus: profile.value.insuranceStatus
}))
const overviewItems = computed(() => [
  { label: '企业名称', value: profile.value.enterpriseName || '待同步' },
  { label: '当前账号', value: profile.value.userName || '-' },
  { label: '账号姓名', value: profile.value.nickName || profile.value.personName || '-' },
  { label: '数据范围', value: scopeText.value || '企业范围未识别' },
  { label: '实名状态', value: profile.value.realNameStatusText || '待实名核验' },
  { label: '证件状态', value: profile.value.certStatusText || '待同步' }
])
const healthCards = computed(() => buildEnterpriseHealthCards({
  homeHero: home.value.hero,
  warnings: home.value.warnings,
  quickEntries: home.value.quickEntries,
  sections: workbench.value.sections
}))
const manageEntries = [
  { title: '账号安全', desc: '查看认证状态、登录方式与账号基础信息。', tag: '账号', path: '/pages/profile/account-security' },
  { title: '通知中心', desc: '查看企业账号收到的公告与提醒。', tag: '消息', path: '/pages/notice/list' },
  { title: '上传记录', desc: '查看企业侧已归档的上传材料和图片记录。', tag: '归档', path: '/pages/profile/upload-records' },
  { title: '帮助反馈', desc: '查看 FAQ、帮助文档和企业端说明。', tag: '支持', path: '/pages/profile/help' },
  { title: '系统设置', desc: '保留接口环境、推送设置和本地诊断入口。', tag: '系统', path: '/pages/profile/settings' }
]

async function loadData() {
  const [profileData, homeData, workbenchData] = await Promise.all([
    getWorkerProfile(),
    getEnterpriseHomeDashboard(),
    getEnterpriseWorkbenchDashboard()
  ])
  profile.value = profileData || {}
  home.value = homeData || {}
  workbench.value = workbenchData || {}
}

function clearCache() {
  try {
    uni.clearStorageSync()
    uni.showToast({ title: '缓存已清理', icon: 'none' })
  } catch (error) {
    uni.showToast({ title: error.message || '清理缓存失败', icon: 'none' })
  }
}

function logout() {
  clearCurrentWorkerAuthState()
  uni.reLaunch({ url: '/pages/login/index' })
}

onMounted(() => {
  loadData().catch((error) => {
    uni.showToast({ title: error.message || '加载企业资料失败', icon: 'none' })
  })
})
</script>
