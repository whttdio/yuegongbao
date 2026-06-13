<template>
  <div ref="rootRef" class="portal-search">
    <input
      v-model.trim="keyword"
      type="text"
      placeholder="请输入关键字"
      @focus="handleFocus"
      @keyup.enter="runSearch"
      @keyup.esc="closePanel"
    >
    <button type="button" @click="runSearch">搜索</button>
    <div v-if="panelVisible" class="portal-search__panel">
      <div v-if="loading" class="portal-search__status">搜索中...</div>
      <template v-else-if="results.length">
        <button
          v-for="item in results"
          :key="item.key"
          type="button"
          class="portal-search__item"
          @click="selectResult(item)"
        >
          <span class="portal-search__tag">{{ item.sectionLabel }}</span>
          <strong>{{ item.title }}</strong>
          <p>{{ item.summary }}</p>
        </button>
      </template>
      <div v-else class="portal-search__status">未找到相关内容，可尝试栏目名称或关键词</div>
    </div>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { openPortalContent, openPortalJob, PORTAL_SECTION_LABELS } from '@/utils/portalNavigation'

const props = defineProps({
  portalCode: { type: String, default: 'ygb' },
  navItems: { type: Array, default: () => [] },
  searchContent: { type: Function, required: true },
  searchJobs: { type: Function, default: null }
})

const emit = defineEmits(['scroll-section'])

const router = useRouter()
const rootRef = ref(null)
const keyword = ref('')
const loading = ref(false)
const panelVisible = ref(false)
const results = ref([])

let debounceTimer = null

const closePanel = () => {
  panelVisible.value = false
}

const handleFocus = () => {
  if (keyword.value) {
    panelVisible.value = true
  }
}

const buildNavResults = (text) => {
  return props.navItems
    .filter(item => item.label.includes(text))
    .map(item => ({
      key: `nav-${item.key}`,
      kind: 'nav',
      sectionKey: item.key,
      sectionLabel: '栏目导航',
      title: item.label,
      summary: `跳转到${item.label}`
    }))
}

const runSearch = async () => {
  if (!keyword.value) {
    ElMessage.info('请输入搜索关键字')
    return
  }

  const navMatches = buildNavResults(keyword.value)
  if (navMatches.length === 1 && navMatches[0].title === keyword.value) {
    emit('scroll-section', navMatches[0].sectionKey)
    closePanel()
    return
  }

  loading.value = true
  panelVisible.value = true
  try {
    const contentRows = await props.searchContent(keyword.value)
    const contentResults = contentRows.map(item => ({
      key: `content-${item.contentId}`,
      kind: 'content',
      contentId: item.contentId,
      sectionKey: mapSectionKey(item.sectionCode),
      sectionLabel: PORTAL_SECTION_LABELS[item.sectionCode] || item.sectionCode,
      title: item.title,
      summary: item.summary,
      categoryCode: item.categoryCode
    }))

    let jobResults = []
    if (props.searchJobs) {
      const jobs = await props.searchJobs(keyword.value)
      jobResults = jobs.map(item => ({
        key: `job-${item.jobId}`,
        kind: 'job',
        jobId: item.jobId,
        sectionKey: 'recruitment',
        sectionLabel: PORTAL_SECTION_LABELS.job,
        title: item.title,
        summary: `${item.company || ''} · ${item.salary || ''} · ${item.location || ''}`.replace(/^ · | · $/g, '')
      }))
    }

    results.value = [...navMatches, ...contentResults, ...jobResults]
  } catch (error) {
    console.error(error)
    results.value = buildNavResults(keyword.value)
  } finally {
    loading.value = false
  }
}

const mapSectionKey = (sectionCode) => {
  const map = {
    news: 'news',
    policy: 'policy',
    solution: 'solution',
    union: 'union',
    guide: 'guide',
    about: 'about',
    download: 'download',
    intro: 'intro',
    banner: 'home'
  }
  return map[sectionCode] || 'home'
}

const selectResult = (item) => {
  if (item.kind === 'nav' || item.kind === 'section') {
    emit('scroll-section', item.sectionKey, item.categoryCode)
    closePanel()
    return
  }
  if (item.kind === 'content' && item.contentId) {
    openPortalContent(router, item.contentId, { portalCode: props.portalCode })
    closePanel()
    return
  }
  if (item.kind === 'job' && item.jobId) {
    openPortalJob(router, item.jobId, { portalCode: props.portalCode })
    closePanel()
  }
}

const handleDocumentClick = (event) => {
  if (!rootRef.value?.contains(event.target)) {
    closePanel()
  }
}

onMounted(() => {
  document.addEventListener('click', handleDocumentClick)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleDocumentClick)
  if (debounceTimer) {
    window.clearTimeout(debounceTimer)
  }
})

defineExpose({ runSearch, closePanel })
</script>

<style scoped lang="scss">
.portal-search {
  position: relative;
  display: flex;
  gap: 10px;
  min-width: 340px;
  padding: 4px;
  border: 1px solid #d8e5ef;
  border-radius: 10px;
  background: #f7fafc;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.9);

  input {
    flex: 1;
    min-width: 0;
    height: 36px;
    padding: 0 12px;
    border: none;
    border-radius: 8px;
    background: transparent;
    color: #18354d;
    font-size: 14px;
    outline: none;

    &::placeholder {
      color: #8298aa;
    }
  }

  button {
    height: 36px;
    padding: 0 18px;
    border: none;
    border-radius: 8px;
    background: #0f5ea8;
    color: #fff;
    font-size: 14px;
    white-space: nowrap;
    cursor: pointer;
    transition: background-color 0.18s ease, box-shadow 0.18s ease;

    &:hover {
      background: #0a4d8f;
      box-shadow: 0 8px 16px rgba(15, 94, 168, 0.18);
    }
  }

  &__panel {
    position: absolute;
    top: calc(100% + 10px);
    left: 0;
    right: 0;
    z-index: 30;
    max-height: 360px;
    overflow: auto;
    border: 1px solid #dfe8f0;
    border-radius: 12px;
    background: #fff;
    box-shadow: 0 18px 44px rgba(15, 45, 74, 0.16);
  }

  &__item {
    display: block;
    width: 100%;
    padding: 13px 14px;
    border: none;
    border-bottom: 1px solid #edf2f7;
    background: #fff;
    text-align: left;
    cursor: pointer;

    &:hover {
      background: #f5f9fd;
    }
  }

  &__tag {
    display: inline-block;
    margin-bottom: 6px;
    padding: 2px 8px;
    border-radius: 6px;
    background: #e8f2fb;
    color: #0f5ea8;
    font-size: 12px;
  }

  &__item strong {
    display: block;
    color: #18354d;
    font-size: 14px;
  }

  &__item p {
    margin: 4px 0 0;
    color: #5f758a;
    font-size: 12px;
    line-height: 1.5;
  }

  &__status {
    padding: 16px;
    color: #5f758a;
    font-size: 13px;
  }
}

@media (max-width: 768px) {
  .portal-search {
    min-width: 0;
    width: 100%;
  }
}
</style>
