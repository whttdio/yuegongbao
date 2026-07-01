import { computed, ref } from "vue"
import { getCurrentCockpitConfig } from "@/api/ygb/cockpitConfig"
import useUserStore from "@/store/modules/user"

export const COCKPIT_PANEL_OPTIONS = [
  { key: "panel-metrics", label: "驾驶舱大屏" },
  { key: "panel-risk-ranking", label: "红黄绿码企业分类" },
  { key: "panel-trend", label: "趋势分析" },
  { key: "panel-map", label: "地图可视化" },
  { key: "panel-table", label: "点位联动明细" },
  { key: "panel-region-summary", label: "区域监管态势摘要" },
  { key: "panel-warning", label: "实时预警流" },
]

export const DEFAULT_COCKPIT_PREFS = {
  mapCenterLng: 113.28,
  mapCenterLat: 23.13,
  mapZoom: 7,
  refreshSeconds: 0,
  visiblePanels: COCKPIT_PANEL_OPTIONS.map((item) => item.key),
  visibleMetrics: [],
}

const REQUIRED_VISIBLE_PANELS = ["panel-map"]

export const REFRESH_INTERVAL_OPTIONS = [
  { label: "30 秒", value: 30 },
  { label: "60 秒", value: 60 },
  { label: "5 分钟", value: 300 },
  { label: "关闭自动刷新", value: 0 },
]

function storageKey(mode) {
  const userStore = useUserStore()
  const userId = userStore.id || "guest"
  return `cockpit-prefs:${mode}:${userId}`
}

function normalizeVisiblePanels(value) {
  const panels = Array.isArray(value) && value.length ? value.filter(Boolean) : [...DEFAULT_COCKPIT_PREFS.visiblePanels]

  for (const key of REQUIRED_VISIBLE_PANELS) {
    if (!panels.includes(key)) {
      panels.unshift(key)
    }
  }

  return [...new Set(panels)]
}

function mergePrefs(serverConfig = {}, localPrefs = {}) {
  const visibleMetrics = localPrefs.visibleMetrics?.length ? localPrefs.visibleMetrics : DEFAULT_COCKPIT_PREFS.visibleMetrics

  return {
    mapCenterLng: Number(localPrefs.mapCenterLng ?? serverConfig.mapCenterLng ?? DEFAULT_COCKPIT_PREFS.mapCenterLng),
    mapCenterLat: Number(localPrefs.mapCenterLat ?? serverConfig.mapCenterLat ?? DEFAULT_COCKPIT_PREFS.mapCenterLat),
    mapZoom: Number(localPrefs.mapZoom ?? serverConfig.mapZoom ?? DEFAULT_COCKPIT_PREFS.mapZoom),
    refreshSeconds: localPrefs.refreshSeconds ?? serverConfig.refreshSeconds ?? DEFAULT_COCKPIT_PREFS.refreshSeconds,
    visiblePanels: normalizeVisiblePanels(localPrefs.visiblePanels),
    visibleMetrics,
  }
}

export function useCockpitConfig(modeRef) {
  const prefs = ref({ ...DEFAULT_COCKPIT_PREFS })
  const dialogVisible = ref(false)
  const draftPrefs = ref({ ...DEFAULT_COCKPIT_PREFS })
  const loading = ref(false)

  const mapConfig = computed(() => ({
    center: [prefs.value.mapCenterLng, prefs.value.mapCenterLat],
    zoom: prefs.value.mapZoom,
  }))

  function readLocalPrefs(mode) {
    try {
      const raw = localStorage.getItem(storageKey(mode))
      return raw ? JSON.parse(raw) : {}
    } catch {
      return {}
    }
  }

  function saveLocalPrefs(mode, value) {
    localStorage.setItem(storageKey(mode), JSON.stringify(value))
  }

  function isPanelVisible(key) {
    return prefs.value.visiblePanels.includes(key)
  }

  function isMetricVisible(key) {
    const visible = prefs.value.visibleMetrics
    if (!visible?.length) return true
    return visible.includes(key)
  }

  async function loadConfig(regionCode) {
    loading.value = true
    try {
      const mode = typeof modeRef === "function" ? modeRef() : modeRef?.value || modeRef
      const response = await getCurrentCockpitConfig({ regionCode })
      const serverConfig = response.data || {}
      const localPrefs = readLocalPrefs(mode)
      prefs.value = mergePrefs(serverConfig, localPrefs)
      draftPrefs.value = {
        ...prefs.value,
        visiblePanels: [...prefs.value.visiblePanels],
        visibleMetrics: [...(prefs.value.visibleMetrics || [])],
      }
    } finally {
      loading.value = false
    }
  }

  function openDialog() {
    draftPrefs.value = {
      ...prefs.value,
      visiblePanels: [...prefs.value.visiblePanels],
      visibleMetrics: [...(prefs.value.visibleMetrics || [])],
    }
    dialogVisible.value = true
  }

  function applyDraft(metricOptions = []) {
    const mode = typeof modeRef === "function" ? modeRef() : modeRef?.value || modeRef
    prefs.value = {
      ...draftPrefs.value,
      visiblePanels: normalizeVisiblePanels(draftPrefs.value.visiblePanels),
      visibleMetrics: draftPrefs.value.visibleMetrics?.length ? [...draftPrefs.value.visibleMetrics] : metricOptions.map((item) => item.key),
    }
    saveLocalPrefs(mode, prefs.value)
    dialogVisible.value = false
  }

  function togglePanel(key, visible) {
    const panels = new Set(normalizeVisiblePanels(prefs.value.visiblePanels))
    if (visible) panels.add(key)
    else if (!REQUIRED_VISIBLE_PANELS.includes(key)) panels.delete(key)
    prefs.value.visiblePanels = normalizeVisiblePanels([...panels])
    const mode = typeof modeRef === "function" ? modeRef() : modeRef?.value || modeRef
    saveLocalPrefs(mode, prefs.value)
  }

  return {
    prefs,
    draftPrefs,
    dialogVisible,
    loading,
    mapConfig,
    loadConfig,
    openDialog,
    applyDraft,
    togglePanel,
    isPanelVisible,
    isMetricVisible,
  }
}
