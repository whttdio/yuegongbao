<template>
  <div class="navbar" :class="[`nav${settingsStore.navType}`, `portal-${portal.code}`]">
    <hamburger
      id="hamburger-container"
      :is-active="appStore.sidebar.opened"
      class="hamburger-container"
      @toggleClick="toggleSideBar"
    />
    <div v-if="settingsStore.navType === 1" class="platform-identity">
      <span class="platform-title">{{ portal.shortTitle }}</span>
      <span class="platform-subtitle">{{ portal.navSubtitle }}</span>
    </div>
    <breadcrumb v-if="settingsStore.navType === 1" id="breadcrumb-container" class="breadcrumb-container" />
    <top-nav v-if="settingsStore.navType === 2" id="topmenu-container" class="topmenu-container" />
    <template v-if="settingsStore.navType === 3">
      <logo v-show="settingsStore.sidebarLogo" :collapse="false" />
      <top-bar id="topbar-container" class="topbar-container" />
    </template>

    <div class="right-menu">
      <template v-if="appStore.device !== 'mobile'">
        <header-search id="header-search" class="right-menu-item" />
        <screenfull id="screenfull" class="right-menu-item hover-effect" />
        <el-tooltip content="消息通知" effect="dark" placement="bottom">
          <header-notice id="header-notice" class="right-menu-item hover-effect" />
        </el-tooltip>
      </template>

      <el-dropdown
        class="avatar-container right-menu-item hover-effect"
        trigger="hover"
        @command="handleCommand"
      >
        <div class="avatar-wrapper">
          <img :src="userStore.avatar" class="user-avatar" />
          <span class="user-nickname">{{ userStore.nickName }}</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <router-link to="/user/profile">
              <el-dropdown-item>个人中心</el-dropdown-item>
            </router-link>
            <el-dropdown-item command="lockScreen">
              <span>锁定屏幕</span>
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <span>退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { ElMessageBox } from 'element-plus'
import Breadcrumb from '@/components/Breadcrumb'
import Hamburger from '@/components/Hamburger'
import HeaderSearch from '@/components/HeaderSearch'
import Screenfull from '@/components/Screenfull'
import useAppStore from '@/store/modules/app'
import useLockStore from '@/store/modules/lock'
import useSettingsStore from '@/store/modules/settings'
import useUserStore from '@/store/modules/user'
import { getActivePortalConfig, resolvePortalLoginPath } from '@/utils/portal'
import HeaderNotice from './HeaderNotice'
import Logo from './Sidebar/Logo.vue'
import TopBar from './TopBar'
import TopNav from './TopNav'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()
const lockStore = useLockStore()
const settingsStore = useSettingsStore()
const portal = computed(() => getActivePortalConfig(route))

function toggleSideBar() {
  appStore.toggleSideBar()
}

function handleCommand(command) {
  if (command === 'lockScreen') {
    lockScreen()
  }
  if (command === 'logout') {
    logout()
  }
}

function logout() {
  ElMessageBox.confirm('确定注销并退出系统吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logOut().then(() => {
      location.href = resolvePortalLoginPath()
    })
  }).catch(() => {})
}

function lockScreen() {
  lockStore.lockScreen(route.fullPath)
  router.push('/lock')
}
</script>

<style lang="scss" scoped>
.navbar {
  display: flex;
  align-items: center;
  height: var(--layout-header-height, 60px);
  padding: 0 24px 0 16px;
  background: #fff;
  color: #1e293b;
  border-bottom: 1px solid #edf2f7;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.03);

  :deep(.hamburger-container),
  :deep(.breadcrumb-container),
  :deep(.topmenu-container),
  :deep(.topbar-container) {
    color: inherit;
  }

  :deep(.hamburger-container) {
    width: 36px;
    height: 36px;
    margin-right: 8px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;

    &:hover {
      background: #f1f5f9;
    }
  }

  :deep(.breadcrumb-container .el-breadcrumb__inner),
  :deep(.breadcrumb-container .el-breadcrumb__inner a),
  :deep(.breadcrumb-container .el-breadcrumb__separator) {
    color: #94a3b8 !important;
    font-weight: 400;
    font-size: 13px;
  }

  :deep(.breadcrumb-container .el-breadcrumb__inner a:hover) {
    color: var(--portal-primary) !important;
  }
}

.platform-identity {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 1px;
  min-width: 160px;
  padding: 0 16px 0 8px;
  margin-right: 16px;
  height: 100%;
  border-right: 1px solid #edf2f7;
}

.platform-title {
  font-size: 15px;
  font-weight: 700;
  line-height: 1.3;
  color: #0f172a;
}

.platform-subtitle {
  font-size: 11px;
  color: #94a3b8;
  letter-spacing: 0.05em;
}

.right-menu {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-left: auto;
  color: #475569;
}

.right-menu-item {
  color: inherit;
}

.hover-effect {
  border-radius: 8px;
  padding: 4px;
  transition: background-color 0.15s ease;

  &:hover {
    background: #f1f5f9;
  }
}

.avatar-container {
  display: flex;
  align-items: center;
  margin-left: 8px;
  cursor: pointer;
}

.avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  display: block;
  flex-shrink: 0;
}

.user-nickname {
  font-size: 13px;
  font-weight: 500;
  color: #475569;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.navbar.portal-azb {
  :deep(.breadcrumb-container .el-breadcrumb__inner a:hover) {
    color: #0b6b78 !important;
  }
}
</style>
