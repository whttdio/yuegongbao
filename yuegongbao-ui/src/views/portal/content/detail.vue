<template>
  <div class="portal-detail">
    <header class="portal-detail__header">
      <div class="portal-detail__container">
        <button type="button" class="portal-detail__back" @click="goBack">← 返回官网</button>
        <span class="portal-detail__badge">{{ sectionLabel }}</span>
        <h1>{{ detail.title }}</h1>
        <div class="portal-detail__meta">
          <span v-if="detail.source">来源：{{ detail.source }}</span>
          <span v-if="publishTimeText">发布时间：{{ publishTimeText }}</span>
        </div>
      </div>
    </header>
    <main class="portal-detail__main">
      <div class="portal-detail__container">
        <p v-if="showSummaryBlock" class="portal-detail__summary">{{ detail.summary }}</p>
        <div v-if="categoryLabel" class="portal-detail__category">{{ categoryLabel }}</div>
        <div v-if="detail.coverUrl" class="portal-detail__cover">
          <img :src="coverUrl" alt="">
        </div>
        <div class="portal-detail__content" v-html="contentHtml" />
        <div v-if="detail.linkUrl" class="portal-detail__actions">
          <button type="button" class="portal-detail__download" @click="openDownload">下载附件</button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPortalContentDetail } from '@/api/portal/public'
import { openPortalDownload, PORTAL_SECTION_LABELS, resolvePortalAssetUrl } from '@/utils/portalNavigation'
import { resolveOfficialPortalRoute } from '@/utils/portal'

const route = useRoute()
const router = useRouter()
const detail = ref({})
const loading = ref(true)

const portalCode = computed(() => route.query.portalCode || 'ygb')
const sectionLabel = computed(() => PORTAL_SECTION_LABELS[detail.value.sectionCode] || '内容详情')
const coverUrl = computed(() => resolvePortalAssetUrl(detail.value.coverUrl))
const categoryLabel = computed(() => detail.value.extra?.categoryLabel || detail.value.extra?.typeLabel || '')
const publishTimeText = computed(() => {
  if (!detail.value.publishTime) return ''
  return String(detail.value.publishTime).slice(0, 10)
})
const showSummaryBlock = computed(() => {
  const summary = (detail.value.summary || '').trim()
  const content = (detail.value.content || '').trim()
  return Boolean(summary && content && summary !== content)
})
const contentHtml = computed(() => {
  const text = (detail.value.content || '').trim()
  const summary = (detail.value.summary || '').trim()
  const body = text || summary
  if (!body) {
    return '<p class="portal-detail__empty">暂无正文内容，请在后台「门户内容管理」中补充正文。</p>'
  }
  if (body.includes('<')) {
    return body
  }
  return `<p>${body.replace(/\n/g, '</p><p>')}</p>`
})

const goBack = () => {
  const { route } = resolveOfficialPortalRoute(portalCode.value)
  router.push({ path: route, query: { standalone: '1' } })
}

const openDownload = () => {
  openPortalDownload(detail.value.linkUrl)
}

onMounted(async () => {
  try {
    const res = await getPortalContentDetail(route.params.contentId)
    if (!res.data || !res.data.title) {
      ElMessage.error('内容不存在或已下线')
      goBack()
      return
    }
    detail.value = res.data
  } catch (error) {
    console.error(error)
    ElMessage.error('内容加载失败')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped lang="scss">
.portal-detail {
  min-height: 100vh;
  background: #f4f7fb;
  color: #18354d;

  &__container {
    width: min(960px, calc(100% - 32px));
    margin: 0 auto;
  }

  &__header {
    padding: 28px 0 24px;
    background: linear-gradient(135deg, #0a3d7a 0%, #0f5ea8 100%);
    color: #fff;
  }

  &__back {
    margin-bottom: 16px;
    padding: 0;
    border: none;
    background: transparent;
    color: rgba(255, 255, 255, 0.88);
    cursor: pointer;
  }

  &__badge {
    display: inline-block;
    margin-bottom: 10px;
    padding: 4px 10px;
    border-radius: 999px;
    background: rgba(255, 255, 255, 0.16);
    font-size: 12px;
  }

  h1 {
    margin: 0;
    font-size: 28px;
    line-height: 1.4;
  }

  &__meta {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    margin-top: 12px;
    color: rgba(255, 255, 255, 0.82);
    font-size: 13px;
  }

  &__main {
    padding: 28px 0 48px;
  }

  &__summary {
    margin: 0 0 20px;
    padding: 16px 18px;
    border-left: 4px solid #0f5ea8;
    background: #fff;
    border-radius: 8px;
    line-height: 1.7;
  }

  &__cover {
    margin-bottom: 20px;

    img {
      max-width: 220px;
      border-radius: 8px;
      background: #fff;
      padding: 8px;
    }
  }

  &__category {
    display: inline-block;
    margin-bottom: 16px;
    padding: 4px 10px;
    border-radius: 999px;
    background: #e8f2fb;
    color: #0f5ea8;
    font-size: 12px;
  }

  &__content {
    padding: 24px;
    background: #fff;
    border-radius: 12px;
    line-height: 1.8;

    :deep(p) {
      margin: 0 0 12px;
    }

    :deep(img) {
      max-width: 100%;
      height: auto;
      border-radius: 8px;
    }

    :deep(ul),
    :deep(ol) {
      margin: 0 0 12px;
      padding-left: 1.5em;
    }

    :deep(.portal-detail__empty) {
      color: #5f758a;
    }
  }

  &__actions {
    margin-top: 20px;
  }

  &__download {
    padding: 10px 18px;
    border: none;
    border-radius: 8px;
    background: #0f5ea8;
    color: #fff;
    cursor: pointer;
  }
}
</style>
