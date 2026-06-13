<template>
  <div class="portal-module-banner">
    <div class="portal-module-banner__inner">
      <div>
        <span class="portal-module-banner__tag">安责保视图</span>
        <h2>{{ title }}</h2>
        <p>{{ description }}</p>
      </div>
      <div class="portal-module-banner__meta">
        <span>共享后台</span>
        <span>应急侧入口</span>
        <span>{{ scopeLabel }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    required: true
  },
  scopeLabel: {
    type: String,
    default: '统一数据底座'
  }
})
</script>

<style lang="scss" scoped>
.portal-module-banner {
  margin-bottom: 16px;
  padding: 18px 20px;
  border: 1px solid #d8e9ec;
  border-radius: 16px;
  background: linear-gradient(135deg, #f7fbfb 0%, #edf8f8 100%);
}

.portal-module-banner__inner {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.portal-module-banner__tag {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 0 10px;
  margin-bottom: 10px;
  border-radius: 999px;
  background: rgba(11, 107, 120, 0.12);
  color: #0b6b78;
  font-size: 12px;
  font-weight: 600;
}

.portal-module-banner h2 {
  margin: 0 0 8px;
  font-size: 22px;
  color: #0f172a;
}

.portal-module-banner p {
  margin: 0;
  max-width: 760px;
  color: #52707a;
  line-height: 1.75;
}

.portal-module-banner__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.portal-module-banner__meta span {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0 12px;
  border-radius: 999px;
  background: #fff;
  border: 1px solid #d8e9ec;
  color: #0b6b78;
  font-size: 12px;
  white-space: nowrap;
}

@media (max-width: 900px) {
  .portal-module-banner__inner {
    flex-direction: column;
  }
}
</style>
