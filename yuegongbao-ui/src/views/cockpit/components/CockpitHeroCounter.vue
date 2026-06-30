<template>
  <div class="hero-counter">
    <div class="hero-counter__bracket hero-counter__bracket--left" />
    <div class="hero-counter__body">
      <div class="hero-counter__label">{{ label }}</div>
      <div class="hero-counter__digits">
        <span v-for="(digit, index) in digitList" :key="`${digit}-${index}`" class="hero-counter__digit">{{ digit }}</span>
        <span class="hero-counter__unit">{{ unit }}</span>
      </div>
      <div class="hero-counter__meta">
        <span v-for="item in metaItems" :key="item.label">{{ item.label }} {{ item.value }}</span>
      </div>
    </div>
    <div class="hero-counter__bracket hero-counter__bracket--right" />
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  label: { type: String, default: '' },
  value: { type: String, default: '0' },
  unit: { type: String, default: '' },
  metaItems: { type: Array, default: () => [] }
})

const digitList = computed(() => String(props.value || '0').replace(/,/g, '').split(''))
</script>

<style scoped lang="scss">
.hero-counter {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.hero-counter__bracket {
  width: 12px;
  height: 52px;
  border: 2px solid rgba(82, 230, 255, 0.55);
  box-shadow: 0 0 14px rgba(82, 230, 255, 0.18);
}

.hero-counter__bracket--left {
  border-right: 0;
}

.hero-counter__bracket--right {
  border-left: 0;
}

.hero-counter__body {
  display: grid;
  justify-items: center;
  gap: 4px;
}

.hero-counter__label {
  color: rgba(177, 214, 237, 0.72);
  font-size: 11px;
  letter-spacing: 0.14em;
}

.hero-counter__digits {
  display: flex;
  align-items: flex-end;
  gap: 4px;
}

.hero-counter__digit {
  min-width: 34px;
  padding: 6px 4px;
  text-align: center;
  font-size: 32px;
  font-weight: 800;
  line-height: 1;
  color: #ebf8ff;
  border: 1px solid rgba(82, 230, 255, 0.28);
  background: linear-gradient(180deg, rgba(20, 55, 82, 0.85), rgba(6, 16, 28, 0.95));
  box-shadow:
    inset 0 0 12px rgba(82, 230, 255, 0.1),
    0 0 16px rgba(82, 230, 255, 0.12);
  text-shadow: 0 0 14px rgba(82, 230, 255, 0.35);
}

.hero-counter__unit {
  margin-left: 2px;
  padding-bottom: 4px;
  font-size: 13px;
  color: rgba(177, 214, 237, 0.72);
}

.hero-counter__meta {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 6px 12px;
  color: rgba(177, 214, 237, 0.65);
  font-size: 11px;
}
</style>
