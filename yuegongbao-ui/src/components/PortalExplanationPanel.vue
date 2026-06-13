<template>
  <el-card v-show="shouldRender" class="portal-explanation-card" shadow="never">
    <template #header>
      <div class="portal-explanation-card__head">
        <div>
          <div class="portal-explanation-card__title">{{ title }}</div>
          <div v-if="description" class="portal-explanation-card__desc">{{ description }}</div>
        </div>
      </div>
    </template>
    <div v-if="items.length" class="portal-explanation-list">
      <article
        v-for="item in items"
        :key="item.key || item.dimensionName || item.label"
        class="portal-explanation-item"
        :class="{ 'portal-explanation-item--action': item.action?.path }"
      >
        <div class="portal-explanation-item__top">
          <strong>{{ item.dimensionName || item.label }}</strong>
          <span class="portal-explanation-item__metric">
            {{ item.currentValue ?? '--' }}
            <span class="portal-explanation-item__target">/ {{ item.targetValue ?? '--' }}</span>
          </span>
        </div>
        <p class="portal-explanation-item__summary">{{ item.summary || item.sourceDescription || '-' }}</p>
        <div class="portal-explanation-item__meta">
          <span>来源：{{ item.sourceLabel || '-' }}</span>
          <span>证据：{{ item.evidenceModule || '-' }}</span>
          <span>下钻：{{ item.recommendModule || '-' }}</span>
        </div>
        <div v-if="item.action?.path" class="portal-explanation-item__actions">
          <el-button link type="primary" @click="emit('open-action', item.action, item)">
            {{ item.actionText || `进入${item.moduleLabel || item.recommendModule || '模块'}` }}
          </el-button>
        </div>
      </article>
    </div>
    <div v-else class="portal-explanation-empty">当前暂无可展示的门户解释。</div>
  </el-card>
</template>

<script setup>
import { computed } from 'vue'

defineOptions({
  name: 'PortalExplanationPanel'
})

const emit = defineEmits(['open-action'])

const props = defineProps({
  title: {
    type: String,
    default: '门户解释'
  },
  description: {
    type: String,
    default: ''
  },
  items: {
    type: Array,
    default: () => []
  }
})

const shouldRender = computed(() => !/^530\.1\b/.test(props.title || ''))
</script>

<style scoped>
.portal-explanation-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.portal-explanation-card__title {
  font-size: 16px;
  font-weight: 700;
  color: #17324d;
}

.portal-explanation-card__desc {
  margin-top: 4px;
  font-size: 13px;
  color: #6a7d91;
}

.portal-explanation-list {
  display: grid;
  gap: 12px;
}

.portal-explanation-item {
  padding: 14px 16px;
  border: 1px solid #d9e2ec;
  border-radius: 12px;
  background: linear-gradient(180deg, #ffffff 0%, #f6f9fc 100%);
}

.portal-explanation-item--action {
  border-color: #c8daf1;
}

.portal-explanation-item__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.portal-explanation-item__metric {
  font-size: 14px;
  font-weight: 700;
  color: #0f5ea8;
}

.portal-explanation-item__target {
  color: #7e8ea0;
}

.portal-explanation-item__summary {
  margin: 8px 0;
  line-height: 1.6;
  color: #23384d;
}

.portal-explanation-item__meta {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  font-size: 12px;
  color: #6a7d91;
}

.portal-explanation-item__actions {
  margin-top: 8px;
}

.portal-explanation-empty {
  color: #7e8ea0;
  font-size: 13px;
}
</style>
