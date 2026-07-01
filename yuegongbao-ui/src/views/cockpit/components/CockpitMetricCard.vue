<template>
  <button
    type="button"
    class="cockpit-metric-card"
    :class="[
      tone ? `cockpit-metric-card--${tone}` : '',
      {
        'cockpit-metric-card--hero': hero,
        'cockpit-metric-card--clickable': clickable,
        'cockpit-metric-card--compact': compact
      }
    ]"
    @click="handleClick"
  >
    <span class="cockpit-metric-card__shine" />
    <div class="cockpit-metric-card__head">
      <span class="cockpit-metric-card__label">{{ label }}</span>
      <em v-if="tag">{{ tag }}</em>
    </div>
    <div class="cockpit-metric-card__value">
      <strong>{{ value }}</strong>
      <span v-if="unit">{{ unit }}</span>
    </div>
    <div class="cockpit-metric-card__foot">
      <span class="cockpit-metric-card__delta" :class="deltaTone || 'flat'">{{ deltaText || '环比持平' }}</span>
      <span class="cockpit-metric-card__share">{{ shareText || '' }}</span>
    </div>
  </button>
</template>

<script setup>
const props = defineProps({
  label: { type: String, default: "" },
  value: { type: [String, Number], default: "" },
  unit: { type: String, default: "" },
  tag: { type: String, default: "" },
  tone: { type: String, default: "" },
  deltaTone: { type: String, default: "flat" },
  deltaText: { type: String, default: "" },
  shareText: { type: String, default: "" },
  sparkValues: { type: Array, default: () => [] },
  hero: { type: Boolean, default: false },
  clickable: { type: Boolean, default: true },
  compact: { type: Boolean, default: false },
})

const emit = defineEmits(["click"])

function handleClick() {
  if (!props.clickable) {
    return
  }
  emit("click")
}
</script>

<style scoped lang="scss">
.cockpit-metric-card {
  position: relative;
  display: grid;
  gap: 8px;
  min-height: 104px;
  padding: 10px 12px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.12) 0%, transparent 38%),
    linear-gradient(180deg, rgba(10, 30, 58, 0.36), rgba(4, 16, 32, 0.48));
  backdrop-filter: blur(20px) saturate(168%);
  -webkit-backdrop-filter: blur(20px) saturate(168%);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.14),
    0 8px 28px rgba(0, 0, 0, 0.18);
  color: inherit;
  text-align: left;
  overflow: hidden;
  transition: transform 0.22s ease, border-color 0.22s ease, box-shadow 0.22s ease;
}

.cockpit-metric-card--clickable {
  cursor: pointer;
}

.cockpit-metric-card:hover {
  transform: translateY(-2px);
  border-color: rgba(0, 229, 255, 0.38);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.18),
    0 12px 32px rgba(0, 0, 0, 0.22),
    0 0 28px rgba(0, 229, 255, 0.2);
}

.cockpit-metric-card__shine {
  content: "";
  position: absolute;
  inset: 0;
  background:
    linear-gradient(120deg, rgba(255, 255, 255, 0.16) 0%, transparent 34%),
    linear-gradient(300deg, rgba(0, 229, 255, 0.06) 0%, transparent 42%);
  pointer-events: none;
}

.cockpit-metric-card__head,
.cockpit-metric-card__foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.cockpit-metric-card__label {
  color: rgba(211, 233, 239, 0.76);
  font-size: 11px;
}

.cockpit-metric-card__head em {
  color: rgba(0, 229, 255, 0.8);
  font-size: 10px;
  font-style: normal;
}

.cockpit-metric-card__value {
  display: flex;
  align-items: flex-end;
  gap: 8px;
}

.cockpit-metric-card__value strong {
  color: #ebfcff;
  font-size: clamp(20px, 1.55vw, 28px);
  font-weight: 800;
  line-height: 1;
  text-shadow: 0 0 16px rgba(0, 229, 255, 0.22);
}

.cockpit-metric-card__value span {
  padding-bottom: 2px;
  color: rgba(216, 239, 245, 0.72);
  font-size: 12px;
}

.cockpit-metric-card__delta,
.cockpit-metric-card__share {
  font-size: 10px;
}

.cockpit-metric-card__delta {
  color: rgba(203, 227, 233, 0.78);
}

.cockpit-metric-card__delta.up {
  color: #00ffc8;
}

.cockpit-metric-card__delta.down {
  color: #ff4d4f;
}

.cockpit-metric-card__share {
  color: rgba(196, 220, 227, 0.58);
}

.cockpit-metric-card--hero {
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.06),
    0 0 28px rgba(0, 229, 255, 0.16);
}

.cockpit-metric-card--danger {
  border-color: rgba(255, 77, 79, 0.28);
  background:
    linear-gradient(145deg, rgba(255, 77, 79, 0.08) 0%, transparent 42%),
    linear-gradient(180deg, rgba(42, 12, 18, 0.82), rgba(17, 7, 12, 0.94));
}

.cockpit-metric-card--danger .cockpit-metric-card__value strong {
  color: #ff7375;
  text-shadow: 0 0 20px rgba(255, 77, 79, 0.5);
}

.cockpit-metric-card--amber {
  border-color: rgba(255, 204, 0, 0.24);
  background:
    linear-gradient(145deg, rgba(255, 204, 0, 0.08) 0%, transparent 42%),
    linear-gradient(180deg, rgba(40, 30, 8, 0.82), rgba(16, 12, 4, 0.94));
}

.cockpit-metric-card--amber .cockpit-metric-card__value strong {
  color: #ffd84d;
  text-shadow: 0 0 18px rgba(255, 204, 0, 0.46);
}

.cockpit-metric-card--green .cockpit-metric-card__value strong,
.cockpit-metric-card--lime .cockpit-metric-card__value strong {
  color: #72ffd6;
  text-shadow: 0 0 18px rgba(0, 255, 200, 0.42);
}

.cockpit-metric-card--blue .cockpit-metric-card__value strong,
.cockpit-metric-card--violet .cockpit-metric-card__value strong,
.cockpit-metric-card--cyan .cockpit-metric-card__value strong {
  color: #b6f2ff;
}

.cockpit-metric-card--compact {
  gap: 5px;
  min-height: 76px;
  padding: 8px 10px;
}

.cockpit-metric-card--compact .cockpit-metric-card__head,
.cockpit-metric-card--compact .cockpit-metric-card__foot {
  gap: 6px;
}

.cockpit-metric-card--compact .cockpit-metric-card__label,
.cockpit-metric-card--compact .cockpit-metric-card__delta,
.cockpit-metric-card--compact .cockpit-metric-card__share {
  font-size: 10px;
}

.cockpit-metric-card--compact .cockpit-metric-card__head em {
  font-size: 9px;
}

.cockpit-metric-card--compact .cockpit-metric-card__value {
  gap: 5px;
}

.cockpit-metric-card--compact .cockpit-metric-card__value strong {
  font-size: clamp(17px, 1.2vw, 22px);
}

.cockpit-metric-card--compact .cockpit-metric-card__value span {
  padding-bottom: 1px;
  font-size: 11px;
}

</style>
