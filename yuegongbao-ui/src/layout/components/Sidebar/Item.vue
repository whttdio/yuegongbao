<template>
  <svg-icon :icon-class="resolvedIcon" />
  <span v-if="!collapse" class="menu-title" :title="resolvedTitle">{{ title }}</span>
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
  },
  collapse: {
    type: Boolean,
    default: false
  }
})

const resolvedTitle = computed(() => (props.title && props.title.length > 5 ? props.title : ''))
const resolvedIcon = computed(() => props.icon || 'list')
</script>
