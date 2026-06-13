<template>
  <div v-if="!item.hidden">
    <template v-if="isSingleMenu">
      <app-link v-if="singleMenu.meta" :to="singleMenuPath">
        <el-menu-item :index="singleMenuIndex" :class="{ 'submenu-title-noDropdown': !isNest }">
          <item :icon="singleMenu.meta.icon || (item.meta && item.meta.icon)" :title="singleMenu.meta.title" />
        </el-menu-item>
      </app-link>
    </template>

    <el-sub-menu v-else ref="subMenu" :index="resolvePath(item.path)">
      <template v-if="item.meta" #title>
        <item :icon="item.meta && item.meta.icon" :title="item.meta.title" />
      </template>

      <sidebar-item
        v-for="(child, index) in item.children"
        :key="child.path + index"
        :is-nest="true"
        :item="child"
        :base-path="resolvePath(child.path)"
        class="nest-menu"
      />
    </el-sub-menu>
  </div>
</template>

<script setup>
import { isExternal } from '@/utils/validate'
import AppLink from './Link'
import Item from './Item'
import { getNormalPath } from '@/utils/yuegongbao'
import { isOfficialPortalPath } from '@/utils/portal'

const props = defineProps({
  // route object
  item: {
    type: Object,
    required: true
  },
  isNest: {
    type: Boolean,
    default: false
  },
  basePath: {
    type: String,
    default: ''
  }
})

const visibleChildren = computed(() => (props.item.children || []).filter(child => !child.hidden))

const singleMenu = computed(() => {
  if (visibleChildren.value.length === 1) {
    return visibleChildren.value[0]
  }
  if (visibleChildren.value.length === 0) {
    return { ...props.item, path: '', noShowingChildren: true }
  }
  return null
})

const isSingleMenu = computed(() => {
  if (props.item.alwaysShow || !singleMenu.value) {
    return false
  }
  return !singleMenu.value.children || singleMenu.value.noShowingChildren
})

const singleMenuPath = computed(() => {
  if (!singleMenu.value) {
    return ''
  }
  return resolvePath(singleMenu.value.path, singleMenu.value.query)
})

const singleMenuIndex = computed(() => {
  if (!singleMenu.value) {
    return ''
  }
  return resolvePath(singleMenu.value.path)
})

function resolvePath(routePath, routeQuery) {
  if (isOfficialPortalPath(routePath)) {
    return routePath
  }
  if (isExternal(routePath)) {
    return routePath
  }
  if (isExternal(props.basePath)) {
    return props.basePath
  }
  if (routeQuery) {
    let query = JSON.parse(routeQuery)
    return { path: getNormalPath(props.basePath + '/' + routePath), query: query }
  }
  return getNormalPath(props.basePath + '/' + routePath)
}

</script>

