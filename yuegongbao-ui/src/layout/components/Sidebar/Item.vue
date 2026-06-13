<template>
  <svg-icon v-if="icon" :icon-class="icon" />
  <span class="menu-title" :title="resolvedTitle">{{ title }}</span>
</template>

<script setup>
const props = defineProps({
  icon: {
    type: String,
    default: ''
  },
  title: {
    type: String,
    default: ''
  }
})

const resolvedTitle = computed(() => (props.title && props.title.length > 5 ? props.title : ''))
</script>
