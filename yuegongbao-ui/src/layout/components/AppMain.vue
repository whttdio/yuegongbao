<template>
  <section class="app-main">
    <page-workbench-assist />
    <router-view v-slot="{ Component, route }">
      <transition name="fade-transform" mode="out-in">
        <keep-alive v-if="!route.meta.link" :include="tagsViewStore.cachedViews">
          <component v-if="Component" :is="Component" :key="route.fullPath || route.path" />
        </keep-alive>
        <component
          v-else-if="Component"
          :is="Component"
          :key="route.fullPath || route.path"
        />
      </transition>
    </router-view>
    <iframe-toggle />
    <copyright />
  </section>
</template>

<script setup>
import copyright from "./Copyright/index"
import iframeToggle from "./IframeToggle/index"
import PageWorkbenchAssist from '@/components/PageWorkbenchAssist/index.vue'
import useTagsViewStore from '@/store/modules/tagsView'

const route = useRoute()
const tagsViewStore = useTagsViewStore()

onMounted(() => {
  addIframe(route)
})

watch(
  () => route.fullPath,
  () => {
    addIframe(route)
  }
)

function addIframe(currentRoute) {
  if (currentRoute?.meta?.link) {
    useTagsViewStore().addIframeView(currentRoute)
  }
}
</script>

<style lang="scss" scoped>
.app-main {
  min-height: calc(100vh - var(--layout-header-height, 60px));
  width: 100%;
  position: relative;
  overflow-x: hidden;
  overflow-y: auto;
  background: var(--page-bg);
}

.app-main:has(.copyright) {
  padding-bottom: 36px;
}
</style>

<style lang="scss">
.main-container:has(.fixed-header) > .app-main {
  overflow-x: hidden;
  overflow-y: auto;
  scrollbar-gutter: stable;
  min-height: 0;
  height: calc(100vh - var(--layout-header-height, 60px));
  margin-top: var(--layout-header-height, 60px);
}

.main-container:has(.fixed-header).hasTagsView > .app-main {
  min-height: calc(100vh - (var(--layout-header-height, 60px) + var(--layout-tags-height, 40px)));
  height: calc(100vh - (var(--layout-header-height, 60px) + var(--layout-tags-height, 40px)));
  margin-top: calc(var(--layout-header-height, 60px) + var(--layout-tags-height, 40px));
}

@media screen and (max-width: 991px) {
  .main-container:has(.fixed-header) > .app-main {
    padding-bottom: max(60px, calc(constant(safe-area-inset-bottom) + 40px));
    padding-bottom: max(60px, calc(env(safe-area-inset-bottom) + 40px));
    overscroll-behavior-y: none;
  }
}

@supports (-webkit-touch-callout: none) {
  @media screen and (max-width: 991px) {
    .main-container:has(.fixed-header) > .app-main {
      padding-bottom: max(17px, calc(constant(safe-area-inset-bottom) + 10px));
      padding-bottom: max(17px, calc(env(safe-area-inset-bottom) + 10px));
      height: calc(100svh - var(--layout-header-height, 60px));
      height: calc(100dvh - var(--layout-header-height, 60px));
    }

    .main-container:has(.fixed-header).hasTagsView > .app-main {
      height: calc(100svh - (var(--layout-header-height, 60px) + var(--layout-tags-height, 40px)));
      height: calc(100dvh - (var(--layout-header-height, 60px) + var(--layout-tags-height, 40px)));
    }
  }
}

::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background-color: var(--page-bg);
}

::-webkit-scrollbar-thumb {
  background-color: var(--panel-border-strong);
  border-radius: 3px;
}

.app-main:has(.cockpit-screen) {
  padding: 0 !important;
  overflow: hidden !important;
  background: #020913 !important;
}

.app-main:has(.cockpit-screen) .page-workbench-assist {
  display: none !important;
}

.app-main:has(.cockpit-screen) .copyright {
  display: none !important;
}
</style>
