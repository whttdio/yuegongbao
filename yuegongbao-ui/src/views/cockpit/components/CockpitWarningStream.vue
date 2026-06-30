<template>
  <div class="warning-stream">
    <div
      class="warning-stream__track"
      :class="{ 'is-static': loopItems.length <= items.length }"
      :style="{ animationDuration: `${scrollDuration}s` }"
    >
      <button
        v-for="(item, index) in loopItems"
        :key="`${item.key}-${index}`"
        type="button"
        class="warning-item"
        :class="`warning-item--${item.tone}`"
        @click="item.action && emit('open-module', item.action)"
      >
        <span class="warning-item__bar" />
        <div class="warning-item__content">
          <div class="warning-item__top">
            <span class="warning-item__level">{{ item.level }}</span>
            <span class="warning-item__location">{{ item.location }}</span>
            <span class="warning-item__time">{{ item.time }}</span>
          </div>
          <div class="warning-item__summary">{{ item.summary }}</div>
          <div class="warning-item__foot">
            <span>{{ item.status }}</span>
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { ArrowRight } from '@element-plus/icons-vue'

const props = defineProps({
  items: { type: Array, default: () => [] }
})

const emit = defineEmits(['open-module'])

const loopItems = computed(() => (props.items.length > 1 ? [...props.items, ...props.items] : props.items))
const scrollDuration = computed(() => Math.max(16, props.items.length * 4))
</script>

<style scoped lang="scss">
.warning-stream {
  overflow: hidden;
  min-height: 0;
  height: 100%;
}

.warning-stream__track {
  display: grid;
  gap: 10px;
  animation: warning-scroll linear infinite;
}

.warning-stream__track.is-static {
  animation: none;
}

.warning-item {
  position: relative;
  display: flex;
  gap: 10px;
  padding: 0;
  color: inherit;
  text-align: left;
  border: 1px solid rgba(82, 230, 255, 0.1);
  background: linear-gradient(180deg, rgba(17, 42, 68, 0.48), rgba(7, 18, 32, 0.88));
  cursor: pointer;
}

.warning-item__bar {
  width: 4px;
  flex-shrink: 0;
  background: rgba(82, 230, 255, 0.92);
}

.warning-item--critical .warning-item__bar {
  background: rgba(255, 111, 145, 0.94);
}

.warning-item--warning .warning-item__bar {
  background: rgba(255, 190, 98, 0.94);
}

.warning-item--info .warning-item__bar,
.warning-item--normal .warning-item__bar {
  background: rgba(61, 242, 178, 0.94);
}

.warning-item__content {
  display: grid;
  gap: 8px;
  padding: 12px 14px 12px 0;
  flex: 1;
}

.warning-item__top,
.warning-item__foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.warning-item__level {
  display: inline-flex;
  align-items: center;
  padding: 3px 8px;
  font-size: 12px;
  font-weight: 700;
  color: #03111f;
  background: rgba(82, 230, 255, 0.92);
}

.warning-item--critical .warning-item__level {
  background: rgba(255, 111, 145, 0.94);
}

.warning-item--warning .warning-item__level {
  background: rgba(255, 190, 98, 0.94);
}

.warning-item__location,
.warning-item__time,
.warning-item__foot {
  color: rgba(177, 214, 237, 0.72);
  font-size: 12px;
}

.warning-item__summary {
  line-height: 1.65;
  color: #f7fdff;
}

@keyframes warning-scroll {
  0% { transform: translateY(0); }
  100% { transform: translateY(-50%); }
}
</style>
