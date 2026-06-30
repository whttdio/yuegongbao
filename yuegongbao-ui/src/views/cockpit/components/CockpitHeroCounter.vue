<template>
  <div class="hero-counter">
    <div class="hero-counter__frame hero-counter__frame--left" />
    <div class="hero-counter__body">
      <div class="hero-counter__label">{{ label }}</div>
      <div class="hero-counter__value">
        <transition-group name="hero-digit" tag="div" class="hero-counter__digits">
          <span v-for="digit in digits" :key="digit.key" class="hero-counter__digit">{{ digit.char }}</span>
        </transition-group>
        <span class="hero-counter__unit">{{ unit }}</span>
      </div>
      <div class="hero-counter__meta">
        <div
          v-for="item in metaItems"
          :key="item.label"
          class="hero-counter__meta-item"
          :class="item.tone ? `hero-counter__meta-item--${item.tone}` : ''"
        >
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
        </div>
      </div>
    </div>
    <div class="hero-counter__frame hero-counter__frame--right" />
  </div>
</template>

<script setup>
import { computed } from "vue"

const props = defineProps({
  label: { type: String, default: "" },
  value: { type: String, default: "0" },
  unit: { type: String, default: "" },
  metaItems: { type: Array, default: () => [] },
})

const digits = computed(() =>
  String(props.value || "0")
    .split("")
    .map((char, index) => ({ char, key: `${index}-${char}` })),
)
</script>

<style scoped lang="scss">
.hero-counter {
  display: grid;
  grid-template-columns: 12px minmax(0, 1fr) 12px;
  align-items: stretch;
  width: 100%;
  min-height: 72px;
  gap: 10px;
}

.hero-counter__frame {
  position: relative;
  border: 1px solid rgba(0, 229, 255, 0.7);
  background: linear-gradient(180deg, rgba(0, 229, 255, 0.16), rgba(0, 229, 255, 0.02));
  box-shadow: 0 0 18px rgba(0, 229, 255, 0.2);
}

.hero-counter__frame::before,
.hero-counter__frame::after {
  content: "";
  position: absolute;
  width: 10px;
  height: 1px;
  background: rgba(0, 229, 255, 0.9);
  box-shadow: 0 0 12px rgba(0, 229, 255, 0.8);
}

.hero-counter__frame::before {
  top: 0;
}

.hero-counter__frame::after {
  bottom: 0;
}

.hero-counter__frame--left {
  border-right: 0;
}

.hero-counter__frame--left::before,
.hero-counter__frame--left::after {
  left: 0;
}

.hero-counter__frame--right {
  border-left: 0;
}

.hero-counter__frame--right::before,
.hero-counter__frame--right::after {
  right: 0;
}

.hero-counter__body {
  display: grid;
  align-content: center;
  justify-items: start;
  gap: 6px;
  min-width: 0;
}

.hero-counter__label {
  color: rgba(208, 238, 246, 0.72);
  font-size: 11px;
  letter-spacing: 0.12em;
}

.hero-counter__value {
  display: flex;
  align-items: flex-end;
  gap: 6px;
}

.hero-counter__digits {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.hero-counter__digit {
  min-width: 18px;
  color: var(--risk-green, #00ffc8);
  font-size: clamp(24px, 1.8vw, 32px);
  font-weight: 800;
  line-height: 1;
  text-align: center;
  text-shadow:
    0 0 16px rgba(0, 229, 255, 0.65),
    0 0 30px rgba(0, 255, 200, 0.28);
}

.hero-counter__unit {
  padding-bottom: 3px;
  color: rgba(216, 238, 245, 0.76);
  font-size: 13px;
}

.hero-counter__meta {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
  gap: 4px 10px;
}

.hero-counter__meta-item {
  display: inline-flex;
  align-items: baseline;
  gap: 4px;
  padding: 2px 6px;
  color: rgba(192, 220, 228, 0.72);
  font-size: 10px;
  border: 1px solid transparent;
  transition: border-color 0.2s ease, background 0.2s ease;
}

.hero-counter__meta-item strong {
  color: #dffcff;
  font-size: 11px;
  font-weight: 600;
}

.hero-counter__meta-item--danger {
  border-color: rgba(255, 77, 79, 0.28);
  background: rgba(255, 77, 79, 0.1);
}

.hero-counter__meta-item--danger strong {
  color: var(--risk-red, #ff4d4f);
  font-size: 13px;
  font-weight: 800;
  text-shadow: 0 0 12px var(--risk-red-glow, rgba(255, 77, 79, 0.62));
}

.hero-counter__meta-item--warning strong {
  color: var(--risk-yellow, #ffcc00);
  text-shadow: 0 0 10px var(--risk-yellow-glow, rgba(255, 204, 0, 0.52));
}

.hero-counter__meta-item--green strong {
  color: var(--risk-green, #00ffc8);
  text-shadow: 0 0 10px var(--risk-green-glow, rgba(0, 255, 200, 0.48));
}

.hero-counter__meta-item--cyan strong {
  color: var(--cockpit-cyan, #00e5ff);
  text-shadow: 0 0 10px rgba(0, 229, 255, 0.45);
}

.hero-digit-enter-active,
.hero-digit-leave-active {
  transition: transform 0.24s ease, opacity 0.24s ease;
}

.hero-digit-enter-from,
.hero-digit-leave-to {
  opacity: 0.1;
  transform: translateY(8px);
}
</style>
