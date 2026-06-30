<template>
  <view class="screen-sidebar">
    <view class="screen-sidebar__rail">
      <view
        v-for="item in navItems"
        :key="item.path"
        class="screen-sidebar__item"
        :class="{ 'screen-sidebar__item--active': item.path === currentPath }"
        @click="handleClick(item.path)"
      >
        <image class="screen-sidebar__icon" :src="item.icon" mode="aspectFit" />
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'
import { openPage } from '../utils/navigation'

const props = defineProps({
  currentPath: {
    type: String,
    default: ''
  }
})

const navItems = computed(() => [
  {
    path: '/pages/standby/index',
    icon: props.currentPath === '/pages/standby/index'
      ? '/static/tabbar/home-active.png'
      : '/static/tabbar/home.png'
  },
  {
    path: '/pages/main/index',
    icon: props.currentPath === '/pages/main/index'
      ? '/static/tabbar/workbench-active.png'
      : '/static/tabbar/workbench.png'
  },
  {
    path: '/pages/device/index',
    icon: props.currentPath === '/pages/device/index'
      ? '/static/tabbar/profile-active.png'
      : '/static/tabbar/profile.png'
  }
])

function handleClick(path) {
  if (!path || path === props.currentPath) {
    return
  }
  openPage(path)
}
</script>
