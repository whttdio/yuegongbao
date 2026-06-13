import { computed, readonly, ref, watch } from 'vue'
import { useRoute } from 'vue-router'

const guideState = ref({})
const openState = ref(false)

function normalizeGuideSection(items = [], mapper = item => item) {
  if (!Array.isArray(items)) {
    return []
  }
  return items
    .filter(Boolean)
    .map(mapper)
    .filter(item => item && Object.values(item).some(value => value !== undefined && value !== null && value !== ''))
}

function createEmptyGuide() {
  return {
    title: '',
    description: '',
    portalExplanation: [],
    focus: [],
    selection: [],
    workflow: [],
    hints: []
  }
}

function normalizeGuide(payload = {}) {
  return {
    title: payload.title || '',
    description: payload.description || '',
    portalExplanation: Array.isArray(payload.portalExplanation) ? payload.portalExplanation.filter(Boolean) : [],
    focus: normalizeGuideSection(payload.focus, item => ({
      label: item.label || item.title || '',
      value: item.value ?? item.count ?? '',
      tip: item.tip || item.desc || item.actionText || '',
      type: item.type || ''
    })),
    selection: normalizeGuideSection(payload.selection, item => ({
      label: item.label || '',
      value: item.value ?? ''
    })),
    workflow: normalizeGuideSection(payload.workflow, item => ({
      label: item.label || item.title || '',
      desc: item.desc || item.summary || ''
    })),
    hints: normalizeGuideSection(payload.hints, item => ({
      label: item.label || item.title || '',
      type: item.type || 'info'
    }))
  }
}

function hasGuideContent(guide) {
  return Boolean(
    guide.title
    || guide.description
    || guide.portalExplanation.length
    || guide.focus.length
    || guide.selection.length
    || guide.workflow.length
    || guide.hints.length
  )
}

export function useWorkbenchAssist() {
  const route = useRoute()

  watch(
    () => route.fullPath,
    () => {
      guideState.value = {}
      openState.value = false
    }
  )

  const pageGuide = computed(() => normalizeGuide(guideState.value))
  const visible = computed(() => hasGuideContent(pageGuide.value))

  function setPageGuide(payload = {}) {
    guideState.value = payload || {}
  }

  function clearPageGuide() {
    guideState.value = {}
    openState.value = false
  }

  function openGuide() {
    if (visible.value) {
      openState.value = true
    }
  }

  function closeGuide() {
    openState.value = false
  }

  function toggleGuide() {
    if (!visible.value) {
      return
    }
    openState.value = !openState.value
  }

  return {
    visible,
    open: readonly(openState),
    pageGuide,
    setPageGuide,
    clearPageGuide,
    openGuide,
    closeGuide,
    toggleGuide
  }
}
