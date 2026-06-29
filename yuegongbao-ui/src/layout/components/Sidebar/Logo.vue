<template>
  <div class="sidebar-logo-container" :class="[{ collapse }, `portal-${portal.code}`]">
    <router-link class="sidebar-logo-link" to="/">
      <svg class="sidebar-logo-svg" viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg">
        <rect width="36" height="36" rx="10" :fill="portal.themeColor" />
        <path d="M10 14.5L18 10l8 4.5v7L18 26l-8-4.5v-7Z" stroke="#fff" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" fill="none" />
        <circle cx="18" cy="18" r="3" fill="#fff" opacity=".9" />
      </svg>
      <div v-show="!collapse" class="sidebar-copy">
        <h1 class="sidebar-title">{{ portal.shortTitle }}</h1>
        <span class="sidebar-subtitle">{{ portal.navSubtitle }}</span>
      </div>
    </router-link>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { getActivePortalConfig } from '@/utils/portal'

defineProps({
  collapse: { type: Boolean, required: true }
})

const route = useRoute()
const portal = computed(() => getActivePortalConfig(route))
</script>

<style lang="scss" scoped>
.sidebar-logo-container {
  height: var(--layout-sidebar-logo-height, 60px);
  display: flex;
  align-items: center;
  padding: 0 16px;
  background: #0f1119;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);

  .sidebar-logo-link {
    display: flex;
    align-items: center;
    gap: 12px;
    height: 100%;
    width: 100%;
    text-decoration: none;
  }

  .sidebar-logo-svg {
    width: 36px;
    height: 36px;
    flex-shrink: 0;
    filter: drop-shadow(0 2px 8px rgba(15, 94, 168, 0.3));
  }

  .sidebar-copy {
    display: flex;
    flex-direction: column;
    gap: 2px;
    min-width: 0;
  }

  .sidebar-title {
    margin: 0;
    font-size: 17px;
    font-weight: 700;
    color: #fff;
    line-height: 1;
    white-space: nowrap;
  }

  .sidebar-subtitle {
    font-size: 11px;
    line-height: 1.2;
    color: rgba(255, 255, 255, 0.6);
    white-space: nowrap;
  }

  &.portal-azb .sidebar-logo-svg {
    filter: drop-shadow(0 2px 8px rgba(11, 107, 120, 0.35));
  }

  &.collapse {
    padding: 0;
    justify-content: center;

    .sidebar-logo-link {
      justify-content: center;
      gap: 0;
    }
  }
}
</style>
