<template>
  <div class="warning-stream" @mouseenter="pauseScroll = true" @mouseleave="pauseScroll = false">
    <div v-if="!items.length" class="cockpit-empty warning-stream__empty">
      <div class="cockpit-empty__icon" />
      <div class="cockpit-empty__title">暂无滚动预警</div>
      <div class="cockpit-empty__desc">系统持续监测中，新增预警将自动进入滚动台账</div>
    </div>
    <ul v-else ref="listRef" class="alert-list">
      <li
        v-for="(item, index) in items"
        :key="`${item.key}-${index}`"
        class="alert-item"
        @click="item.action && emit('open-module', item.action)"
      >
        <span class="alert-level" :class="levelClass(item.tone)">{{ levelSymbol(item.tone) }}</span>
        <div class="alert-content">
          <div class="alert-location">{{ item.location }}</div>
          <div class="alert-desc">{{ item.summary }}</div>
        </div>
        <span class="alert-status" :class="statusClass(item.status)">{{ item.status }}</span>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref, watch } from "vue"

const props = defineProps({
  items: { type: Array, default: () => [] },
})

const emit = defineEmits(["open-module"])

const listRef = ref(null)
const pauseScroll = ref(false)
let scrollTimer = null
let scrollOffset = 0

function levelSymbol(tone) {
  if (tone === "critical") return "Ⅰ"
  if (tone === "warning") return "Ⅱ"
  return "Ⅲ"
}

function levelClass(tone) {
  if (tone === "critical") return "critical"
  if (tone === "warning") return "warning"
  return "info"
}

function statusClass(status) {
  if (status === "待处理") return "pending"
  if (status === "处理中") return "processing"
  if (status === "已办结" || status === "已解决") return "resolved"
  return "processing"
}

function autoScroll() {
  if (!listRef.value || pauseScroll.value || props.items.length <= 1) return
  const el = listRef.value
  const maxScroll = el.scrollHeight - el.clientHeight
  if (maxScroll <= 0) return
  scrollOffset += 0.4
  if (scrollOffset >= maxScroll + 20) scrollOffset = 0
  el.scrollTop = scrollOffset
}

function startScroll() {
  stopScroll()
  scrollTimer = window.setInterval(autoScroll, 90)
}

function stopScroll() {
  if (scrollTimer) {
    clearInterval(scrollTimer)
    scrollTimer = null
  }
}

watch(
  () => props.items.length,
  () => {
    scrollOffset = 0
    if (listRef.value) listRef.value.scrollTop = 0
    startScroll()
  },
)

onMounted(() => {
  startScroll()
})

onBeforeUnmount(() => {
  stopScroll()
})
</script>

<style scoped lang="scss">
.warning-stream {
  overflow: hidden;
  min-height: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.warning-stream__empty {
  width: 100%;
  min-height: 96px;
}

.alert-list {
  list-style: none;
  margin: 0;
  padding: 3px 6px;
  overflow-y: auto;
  height: 100%;
  scrollbar-width: thin;
  scrollbar-color: rgba(255, 255, 255, 0.06) transparent;
}

.alert-list::-webkit-scrollbar {
  width: 2px;
}

.alert-list::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.07);
  border-radius: 10px;
}

.alert-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 7px 8px;
  border-radius: 5px;
  margin-bottom: 3px;
  border: 1px solid transparent;
  cursor: pointer;
  transition: background 0.18s ease, border-color 0.18s ease;
  animation: fadeInUp 0.35s ease-out;
}

.alert-item:hover {
  background: rgba(255, 255, 255, 0.025);
  border-color: rgba(0, 180, 255, 0.14);
}

.alert-level {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: 700;
  color: #fff;
  line-height: 1;
}

.alert-level.critical {
  background: var(--risk-red, #ff4d5a);
  box-shadow: 0 0 8px rgba(255, 77, 90, 0.45);
}

.alert-level.warning {
  background: #ff8c3d;
  box-shadow: 0 0 8px rgba(255, 140, 61, 0.45);
}

.alert-level.info {
  background: #4a8eff;
  box-shadow: 0 0 8px rgba(74, 142, 255, 0.45);
}

.alert-content {
  flex: 1;
  min-width: 0;
}

.alert-location {
  font-size: 11px;
  font-weight: 600;
  color: var(--text-primary, #e6ecf5);
  margin-bottom: 1px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.alert-desc {
  font-size: 10px;
  color: rgba(148, 169, 196, 0.88);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.25;
}

.alert-status {
  font-size: 10px;
  padding: 2px 7px;
  border-radius: 8px;
  flex-shrink: 0;
  font-weight: 500;
  white-space: nowrap;
}

.alert-status.pending {
  background: rgba(255, 140, 61, 0.18);
  color: #ff8c3d;
  border: 1px solid rgba(255, 140, 61, 0.25);
}

.alert-status.processing {
  background: rgba(74, 142, 255, 0.18);
  color: #6aadff;
  border: 1px solid rgba(74, 142, 255, 0.25);
}

.alert-status.resolved {
  background: rgba(61, 220, 132, 0.13);
  color: #3ddc84;
  border: 1px solid rgba(61, 220, 132, 0.2);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
