<template>
  <div class="cockpit-section-header" :class="{ 'cockpit-section-header--compact': compact }">
    <div class="cockpit-section-header__main">
      <span
        v-if="dot"
        class="cockpit-section-header__dot"
        :class="dotTone ? `cockpit-section-header__dot--${dotTone}` : ''"
      />
      <div class="cockpit-section-header__titles">
        <span v-if="eyebrow" class="cockpit-section-header__eyebrow">{{ eyebrow }}</span>
        <strong class="cockpit-section-header__title">{{ title }}</strong>
        <span v-if="subtitle" class="cockpit-section-header__subtitle">{{ subtitle }}</span>
      </div>
    </div>
    <div class="cockpit-section-header__side">
      <span v-if="extra" class="cockpit-section-header__extra">{{ extra }}</span>
      <slot />
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, default: "" },
  subtitle: { type: String, default: "" },
  eyebrow: { type: String, default: "" },
  extra: { type: String, default: "" },
  dot: { type: Boolean, default: true },
  dotTone: { type: String, default: "" },
  compact: { type: Boolean, default: false },
})
</script>

<style scoped lang="scss">
.cockpit-section-header {
  position: relative;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  padding-bottom: 10px;
  margin-bottom: 10px;
}

.cockpit-section-header::after {
  content: "";
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 1px;
  background: linear-gradient(90deg, rgba(0, 229, 255, 0.82), rgba(0, 229, 255, 0.1));
}

.cockpit-section-header--compact {
  padding-bottom: 8px;
  margin-bottom: 8px;
}

.cockpit-section-header__main {
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.cockpit-section-header__dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
  background: #00e5ff;
  box-shadow: 0 0 10px rgba(0, 229, 255, 0.48);
}

.cockpit-section-header__dot--danger {
  background: #ff4d4f;
  box-shadow: 0 0 12px rgba(255, 77, 79, 0.52);
}

.cockpit-section-header__dot--warning {
  background: #ffcc00;
  box-shadow: 0 0 12px rgba(255, 204, 0, 0.48);
}

.cockpit-section-header__dot--green {
  background: #00ffc8;
  box-shadow: 0 0 12px rgba(0, 255, 200, 0.48);
}

.cockpit-section-header__titles {
  min-width: 0;
  display: grid;
  gap: 2px;
}

.cockpit-section-header__eyebrow {
  color: rgba(149, 240, 249, 0.76);
  font-size: 10px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
}

.cockpit-section-header__title {
  color: #effcff;
  font-size: 16px;
  font-weight: 700;
  line-height: 1.2;
}

.cockpit-section-header__subtitle {
  color: rgba(203, 227, 233, 0.68);
  font-size: 11px;
  line-height: 1.4;
}

.cockpit-section-header__side {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
  flex-shrink: 0;
}

.cockpit-section-header__extra {
  padding: 4px 8px;
  border: 1px solid rgba(0, 229, 255, 0.18);
  background: rgba(0, 229, 255, 0.08);
  color: rgba(214, 238, 244, 0.78);
  font-size: 11px;
  white-space: nowrap;
}
</style>
