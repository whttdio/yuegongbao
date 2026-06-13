<template>
  <el-dialog
    v-model="model"
    :title="title"
    :width="dialogWidth"
    append-to-body
    destroy-on-close
    :before-close="beforeClose"
    :class="dialogClass"
  >
    <template v-if="$slots.header" #header>
      <slot name="header" />
    </template>
    <div ref="contentRef" class="page-detail-dialog__content">
      <slot />
    </div>
    <template v-if="$slots.footer" #footer>
      <slot name="footer" />
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, ref, watch } from 'vue'

const props = defineProps({
  title: {
    type: String,
    default: ''
  },
  width: {
    type: String,
    default: '760px'
  },
  beforeClose: {
    type: Function,
    default: undefined
  },
  dialogClass: {
    type: String,
    default: 'page-detail-dialog'
  }
})

const model = defineModel({ type: Boolean, default: false })
const contentRef = ref()
const measuredContentWidth = ref(0)
let resizeObserver

const widthToPx = (value) => {
  if (typeof value !== 'string') return 0
  const trimmed = value.trim()
  if (trimmed.endsWith('px')) {
    return Number.parseInt(trimmed, 10) || 0
  }
  return 0
}

const measureContentWidth = () => {
  if (!contentRef.value) return
  measuredContentWidth.value = Math.ceil(contentRef.value.scrollWidth + 48)
}

const stopObserveContent = () => {
  resizeObserver?.disconnect()
  resizeObserver = undefined
}

const observeContent = () => {
  stopObserveContent()
  if (!contentRef.value) return
  measureContentWidth()
  if (typeof ResizeObserver !== 'undefined') {
    resizeObserver = new ResizeObserver(measureContentWidth)
    resizeObserver.observe(contentRef.value)
  }
}

const dialogWidth = computed(() => {
  const baseWidth = widthToPx(props.width)
  if (baseWidth > 0) {
    const minWidth = Math.max(baseWidth, measuredContentWidth.value)
    return `min(max(${minWidth}px, 76vw), calc(100vw - 48px))`
  }
  return props.width || 'min(960px, calc(100vw - 48px))'
})

watch(model, async (visible) => {
  if (!visible) {
    measuredContentWidth.value = 0
    stopObserveContent()
    return
  }
  await nextTick()
  observeContent()
})

onBeforeUnmount(stopObserveContent)
</script>
