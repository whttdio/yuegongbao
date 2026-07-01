import { computed, getCurrentInstance, nextTick, onActivated, onBeforeUnmount, onDeactivated, onMounted, reactive, ref, toRefs } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { authorizedDefaultRegionCode } from '@/utils/regionScope'
import { gdRegionNameMap, gdRegionOptions } from '@/utils/regionName'
import { normalizeRouteTarget } from '@/utils/routeAlias'

export const cockpitRegionOptions = gdRegionOptions

export const cockpitRegionNameMap = gdRegionNameMap

export const cockpitDayOptions = [
  { label: '最近 7 天', value: 7 },
  { label: '最近 14 天', value: 14 },
  { label: '最近 30 天', value: 30 }
]

export function currentDate() {
  const date = new Date()
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

export function currentMonth() {
  const date = new Date()
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`
}

export function formatNumber(value, digits = 2) {
  const number = Number(value ?? 0)
  return Number.isNaN(number) ? '0.00' : number.toFixed(digits)
}

export function valueOrDefault(value, fallback = 0) {
  const number = Number(value ?? fallback ?? 0)
  return Number.isNaN(number) ? Number(fallback || 0) : number
}

export function formatPercentText(value) {
  return `${formatNumber(value, 1)}%`
}

export function formatAmountText(value) {
  return `${formatNumber(value, 0)} 元`
}

export function featureTypeLabel(value) {
  if (value === 'ENTERPRISE') return '企业'
  if (value === 'DEVICE') return '设备'
  if (value === 'FENCE') return '围栏'
  if (value === 'STATION') return '站点'
  return value || '-'
}

export function featureStatusLabel(value) {
  if (value === '1') return '在线'
  if (value === '2') return '预警'
  if (value === '0') return '离线'
  return value || '-'
}

export function coordinateText(geometry = {}) {
  if (geometry.type === 'Point') {
    const coordinates = geometry.coordinates || []
    return `${coordinates[0] || '-'}, ${coordinates[1] || '-'}`
  }
  if (geometry.type === 'Polygon') {
    const coordinates = geometry.coordinates?.[0] || []
    return coordinates.length ? `${coordinates.length} 个围栏点` : '-'
  }
  return '-'
}

export function distributionName(value) {
  if (value === 'SOCIAL') return '社保联动'
  if (value === 'TAX') return '个税联动'
  if (value === 'EXPANSION') return '扩面联动'
  if (value === 'SPECIAL') return '专项治理'
  if (value === 'DEVICE') return '设备 / AI'
  if (value === 'INJURY') return '工伤事故'
  return value || '-'
}

export function formatTrendDate(value) {
  if (!value) {
    return '-'
  }
  const text = String(value)
  return text.includes('-') ? text.slice(5, 10) : text
}

export function buildBounds(features) {
  const coordinates = features
    .map(item => item.geometry?.coordinates || [])
    .filter(item => Array.isArray(item) && item.length === 2)

  if (!coordinates.length) {
    return { minLng: 112.8, maxLng: 114.3, minLat: 22.2, maxLat: 23.3 }
  }

  const lngs = coordinates.map(item => Number(item[0]))
  const lats = coordinates.map(item => Number(item[1]))
  return {
    minLng: Math.min(...lngs) - 0.1,
    maxLng: Math.max(...lngs) + 0.1,
    minLat: Math.min(...lats) - 0.1,
    maxLat: Math.max(...lats) + 0.1
  }
}

export function normalizeColorCode(value) {
  const text = String(value || '').toUpperCase()
  if (text === 'RED' || text === '红' || text === '红码') return 'RED'
  if (text === 'YELLOW' || text === '黄' || text === '黄码') return 'YELLOW'
  if (text === 'GREEN' || text === '绿' || text === '绿码') return 'GREEN'
  return text || 'GREEN'
}

export function colorCodeTone(value) {
  const code = normalizeColorCode(value)
  if (code === 'RED') return 'red'
  if (code === 'YELLOW') return 'yellow'
  return 'green'
}

export function colorCodeLabel(value) {
  const code = normalizeColorCode(value)
  if (code === 'RED') return '红码'
  if (code === 'YELLOW') return '黄码'
  if (code === 'GREEN') return '绿码'
  return value || '--'
}

export function filterFeaturesByBounds(features, bounds) {
  if (!bounds) return features
  return features.filter(item => {
    const coordinates = item.geometry?.coordinates || []
    if (!Array.isArray(coordinates) || coordinates.length < 2) return false
    const lng = Number(coordinates[0])
    const lat = Number(coordinates[1])
    return lng >= bounds.minLng && lng <= bounds.maxLng && lat >= bounds.minLat && lat <= bounds.maxLat
  })
}

function formatCellPercent(value) {
  if (value === undefined || value === null || value === '') return '--'
  const number = Number(value)
  return Number.isNaN(number) ? '--' : `${number.toFixed(1)}%`
}

function formatCellCount(value) {
  if (value === undefined || value === null || value === '') return '--'
  const number = Number(value)
  return Number.isNaN(number) ? '--' : String(number)
}

export function sortByKeyOrder(items, keys = [], fallbackSorter) {
  if (!Array.isArray(items)) {
    return []
  }
  if (!Array.isArray(keys) || !keys.length) {
    return [...items].sort(fallbackSorter)
  }
  const orderMap = new Map(keys.map((key, index) => [key, index]))
  return [...items].sort((left, right) => {
    const leftIndex = orderMap.has(left.key) ? orderMap.get(left.key) : Number.MAX_SAFE_INTEGER
    const rightIndex = orderMap.has(right.key) ? orderMap.get(right.key) : Number.MAX_SAFE_INTEGER
    if (leftIndex !== rightIndex) {
      return leftIndex - rightIndex
    }
    if (typeof fallbackSorter === 'function') {
      return fallbackSorter(left, right)
    }
    return 0
  })
}

function createDefaultQueryParams(defaultRegionCode) {
  return {
    regionCode: defaultRegionCode,
    statDate: currentDate(),
    statMonth: currentMonth(),
    days: 7
  }
}

export function useCockpitPage(options = {}) {
  const {
    fetchDashboard,
    exportFilePrefix = 'cockpit',
    defaultRegionCode = '440000',
    distributionLabelFormatter = item => item.dimensionName || item.dimensionCode || '-',
    trendColors = ['#1d4ed8', '#059669', '#f59e0b'],
    trendSeries = [],
    distributionColor = '#1d4ed8',
    pointColors = {
      ENTERPRISE: '#1d4ed8',
      DEVICE: '#059669',
      FENCE: '#f59e0b'
    },
    createTrendChartOption,
    createDistributionChartOption,
    createMapChartOption,
    mapConfigRef = null,
    onMapViewChange = null
  } = options

  const { proxy } = getCurrentInstance()
  const router = useRouter()
  const authorizedRegionCode = authorizedDefaultRegionCode(defaultRegionCode)

  const loading = ref(false)
  const dashboardData = ref({})
  const trendChartRef = ref(null)
  const distributionChartRef = ref(null)
  const mapChartRef = ref(null)

  let trendChartInstance
  let distributionChartInstance
  let mapChartInstance
  let georoamHandler = null

  const mapViewBounds = ref(null)

  const data = reactive({
    queryParams: createDefaultQueryParams(authorizedRegionCode)
  })

  const { queryParams } = toRefs(data)

  const indicators = computed(() => dashboardData.value.indicators || {})
  const trendList = computed(() => dashboardData.value.trend || [])
  const distributionList = computed(() => dashboardData.value.distribution || [])
  const featureCollection = computed(() => dashboardData.value.map || { features: [] })

  const enterpriseFeatures = computed(() => (featureCollection.value.features || []).filter(item => {
    const type = item.properties?.featureType
    return type === 'ENTERPRISE' && item.geometry?.type === 'Point'
  }))

  const enterpriseTableRows = computed(() => enterpriseFeatures.value.map(item => {
    const geometry = item.geometry || {}
    const properties = item.properties || {}
    const colorCode = normalizeColorCode(properties.colorCode || properties.riskLevel || properties.risk)
    const tone = colorCodeTone(colorCode)
    const coordinates = geometry.coordinates || []
    return {
      id: item.id,
      enterpriseName: properties.featureName || properties.enterpriseName || '-',
      colorCode,
      riskTone: tone,
      riskLabel: colorCodeLabel(colorCode),
      regionName: cockpitRegionNameMap[properties.regionCode] || properties.regionName || properties.regionCode || '-',
      insuranceRateText: formatCellPercent(properties.insuranceRate),
      violationCountText: formatCellCount(properties.violationCount),
      deviceCountText: formatCellCount(properties.deviceCount),
      warningStatusText: properties.warningStatusText
        || (properties.warningStatus === '1' || properties.featureStatus === '2' ? '预警中' : '正常'),
      coordinates,
      lng: Number(coordinates[0] || 0),
      lat: Number(coordinates[1] || 0),
      raw: properties
    }
  }))

  const visibleEnterpriseRows = computed(() => {
    const rows = filterFeaturesByBounds(
      enterpriseTableRows.value.map(item => ({ geometry: { coordinates: item.coordinates }, ...item })),
      mapViewBounds.value
    ).map(item => ({
      id: item.id,
      enterpriseName: item.enterpriseName,
      colorCode: item.colorCode,
      riskTone: item.riskTone,
      riskLabel: item.riskLabel,
      regionName: item.regionName,
      insuranceRateText: item.insuranceRateText,
      violationCountText: item.violationCountText,
      deviceCountText: item.deviceCountText,
      warningStatusText: item.warningStatusText,
      coordinates: item.coordinates,
      lng: item.lng,
      lat: item.lat,
      raw: item.raw
    }))
    return rows.length ? rows : enterpriseTableRows.value
  })

  const featureTableList = computed(() => enterpriseTableRows.value.map(item => ({
    id: item.id,
    featureName: item.enterpriseName,
    featureType: '企业',
    regionName: item.regionName,
    featureStatus: item.warningStatusText,
    geometryType: 'Point',
    coordinateText: item.coordinates.length ? `${item.coordinates[0]}, ${item.coordinates[1]}` : '-'
  })))

  function handleQuery() {
    loadDashboard()
  }

  function resetQuery() {
    queryParams.value = createDefaultQueryParams(authorizedRegionCode)
    loadDashboard()
  }

  function handleExport() {
    proxy.download('ygb/cockpit/export', { ...queryParams.value }, `${exportFilePrefix}_${Date.now()}.xlsx`)
  }

  function sanitizeRouteQuery(query = {}) {
    return Object.fromEntries(
      Object.entries(query).filter(([, value]) => value !== undefined && value !== null && value !== '')
    )
  }

  function openModule(target) {
    if (!target) {
      return
    }
    const normalizedTarget = normalizeRouteTarget(target)
    if (typeof normalizedTarget === 'string') {
      router.push(normalizedTarget)
      return
    }
    if (!normalizedTarget?.path) {
      return
    }
    router.push({
      path: normalizedTarget.path,
      query: sanitizeRouteQuery(normalizedTarget.query || {})
    })
  }

  function createPointSeries(featureType, name, color, features) {
    return {
      name,
      type: 'scatter',
      symbolSize: 16,
      itemStyle: { color },
      data: features
        .filter(item => item.properties?.featureType === featureType)
        .map(item => ({
          name: item.properties?.featureName || name,
          value: item.geometry?.coordinates || [0, 0],
          featureType: name
        }))
    }
  }

  function renderTrendChart() {
    if (!trendChartRef.value) {
      return
    }
    if (!trendChartInstance) {
      trendChartInstance = echarts.init(trendChartRef.value)
    }

    const option = typeof createTrendChartOption === 'function'
      ? createTrendChartOption({
          trendList: trendList.value,
          trendColors,
          trendSeries,
          formatTrendDate
        })
      : {
          color: trendColors,
          tooltip: { trigger: 'axis' },
          legend: { top: 0, textStyle: { color: '#475569' } },
          grid: { left: 44, right: 52, top: 48, bottom: 32 },
          xAxis: {
            type: 'category',
            data: trendList.value.map(item => formatTrendDate(item.statDate)),
            axisLine: { lineStyle: { color: '#cbd5e1' } },
            axisLabel: { color: '#64748b' }
          },
          yAxis: [
            {
              type: 'value',
              name: '数量',
              minInterval: 1,
              axisLine: { show: false },
              splitLine: { lineStyle: { color: '#e2e8f0' } },
              axisLabel: { color: '#64748b' }
            },
            {
              type: 'value',
              name: '比率',
              axisLabel: { formatter: '{value}%', color: '#64748b' },
              splitLine: { show: false }
            }
          ],
          series: trendSeries.map(item => {
            const series = {
              name: item.name,
              type: item.type || 'line',
              data: trendList.value.map(row => Number(row[item.dataKey] || 0)),
              yAxisIndex: item.yAxisIndex || 0
            }
            if (series.type === 'bar') {
              series.barMaxWidth = item.barMaxWidth || 24
            } else {
              series.smooth = item.smooth !== false
            }
            return series
          })
        }

    trendChartInstance.setOption(option, true)
  }

  function renderDistributionChart() {
    if (!distributionChartRef.value) {
      return
    }
    if (!distributionChartInstance) {
      distributionChartInstance = echarts.init(distributionChartRef.value)
    }

    const option = typeof createDistributionChartOption === 'function'
      ? createDistributionChartOption({
          distributionList: distributionList.value,
          distributionColor,
          distributionLabelFormatter
        })
      : {
          color: [distributionColor],
          tooltip: { trigger: 'axis' },
          grid: { left: 46, right: 18, top: 24, bottom: 48 },
          xAxis: {
            type: 'category',
            axisLabel: { color: '#64748b', interval: 0, rotate: 18 },
            axisLine: { lineStyle: { color: '#cbd5e1' } },
            data: distributionList.value.map(item => distributionLabelFormatter(item))
          },
          yAxis: {
            type: 'value',
            minInterval: 1,
            axisLabel: { color: '#64748b' },
            splitLine: { lineStyle: { color: '#e2e8f0' } }
          },
          series: [
            {
              type: 'bar',
              barMaxWidth: 36,
              data: distributionList.value.map(item => ({
                value: item.metricCount || 0,
                label: `${item.metricRate || 0}%`
              })),
              label: {
                show: true,
                position: 'top',
                color: '#475569',
                formatter: params => params.data.label
              }
            }
          ]
        }

    distributionChartInstance.setOption(option, true)
  }

  function updateMapViewBoundsFromChart() {
    if (!mapChartInstance) {
      mapViewBounds.value = null
      return
    }
    const model = mapChartInstance.getModel().getComponent('geo', 0)
    if (!model?.coordinateSystem) {
      mapViewBounds.value = null
      return
    }
    const rect = model.coordinateSystem.getBoundingRect()
    const sw = mapChartInstance.convertFromPixel({ geoIndex: 0 }, [rect.x, rect.y + rect.height])
    const ne = mapChartInstance.convertFromPixel({ geoIndex: 0 }, [rect.x + rect.width, rect.y])
    if (!sw || !ne) {
      mapViewBounds.value = null
      return
    }
    mapViewBounds.value = {
      minLng: Math.min(sw[0], ne[0]),
      maxLng: Math.max(sw[0], ne[0]),
      minLat: Math.min(sw[1], ne[1]),
      maxLat: Math.max(sw[1], ne[1])
    }
    if (typeof onMapViewChange === 'function') {
      onMapViewChange(mapViewBounds.value)
    }
  }

  function bindMapGeoroam() {
    if (!mapChartInstance) return
    if (georoamHandler) {
      mapChartInstance.off('georoam', georoamHandler)
    }
    georoamHandler = () => updateMapViewBoundsFromChart()
    mapChartInstance.on('georoam', georoamHandler)
  }

  function highlightMapPoint(row = {}) {
    if (!mapChartInstance || !row.lng || !row.lat) return
    const zoom = mapConfigRef?.value?.zoom ?? 1.2
    mapChartInstance.setOption({
      geo: {
        center: [row.lng, row.lat],
        zoom
      }
    })
    updateMapViewBoundsFromChart()
    const enterpriseSeriesIndex = mapChartInstance.getOption()?.series?.findIndex(item => item.name === '企业点位')
    if (enterpriseSeriesIndex >= 0) {
      const series = mapChartInstance.getOption().series[enterpriseSeriesIndex]
      const dataIndex = (series.data || []).findIndex(item => {
        const value = item.value || []
        return Number(value[0]) === Number(row.lng) && Number(value[1]) === Number(row.lat)
      })
      if (dataIndex >= 0) {
        mapChartInstance.dispatchAction({ type: 'showTip', seriesIndex: enterpriseSeriesIndex, dataIndex })
        mapChartInstance.dispatchAction({ type: 'highlight', seriesIndex: enterpriseSeriesIndex, dataIndex })
      }
    }
  }

  function renderMapChart() {
    if (!mapChartRef.value) {
      return
    }
    if (!mapChartInstance) {
      mapChartInstance = echarts.init(mapChartRef.value)
      bindMapGeoroam()
    }

    const features = featureCollection.value.features || []
    const pointFeatures = features.filter(item => item.geometry?.type === 'Point')
    const polygonFeatures = features.filter(item => item.geometry?.type === 'Polygon')
    const pointBounds = buildBounds(pointFeatures)

    const pointSeries = [
      createPointSeries('ENTERPRISE', '企业点位', pointColors.ENTERPRISE, pointFeatures),
      createPointSeries('DEVICE', '设备点位', pointColors.DEVICE, pointFeatures),
      createPointSeries('FENCE', '围栏标记', pointColors.FENCE, pointFeatures)
    ].filter(item => item.data.length > 0)

    const polygonSeries = polygonFeatures.map(item => ({
      name: item.properties?.featureName || '电子围栏',
      type: 'line',
      showSymbol: false,
      lineStyle: { color: pointColors.FENCE, width: 2, type: 'dashed' },
      data: (item.geometry.coordinates?.[0] || []).map(coord => [coord[0], coord[1]])
    }))

    const option = typeof createMapChartOption === 'function'
      ? createMapChartOption({
          features,
          pointFeatures,
          polygonFeatures,
          pointBounds,
          pointSeries,
          polygonSeries,
          pointColors
        })
      : {
          tooltip: {
            trigger: 'item',
            formatter: params => {
              if (params.seriesType === 'line') {
                return `${params.seriesName}<br/>围栏边界`
              }
              const chartData = params.data || {}
              return `${chartData.name}<br/>${chartData.featureType}<br/>经纬度：${chartData.value[0]}, ${chartData.value[1]}`
            }
          },
          legend: { top: 0, textStyle: { color: '#475569' } },
          grid: { left: 42, right: 18, top: 42, bottom: 30 },
          xAxis: {
            type: 'value',
            min: pointBounds.minLng,
            max: pointBounds.maxLng,
            axisLabel: { color: '#64748b' },
            splitLine: { lineStyle: { color: '#eef2ff' } }
          },
          yAxis: {
            type: 'value',
            min: pointBounds.minLat,
            max: pointBounds.maxLat,
            axisLabel: { color: '#64748b' },
            splitLine: { lineStyle: { color: '#eef2ff' } }
          },
          series: [...pointSeries, ...polygonSeries]
        }

    mapChartInstance.setOption(option, true)
    nextTick(() => updateMapViewBoundsFromChart())
  }

  function resizeCharts() {
    trendChartInstance?.resize()
    distributionChartInstance?.resize()
    mapChartInstance?.resize()
  }

  function disposeCharts() {
    if (mapChartInstance && georoamHandler) {
      mapChartInstance.off('georoam', georoamHandler)
    }
    trendChartInstance?.dispose()
    distributionChartInstance?.dispose()
    mapChartInstance?.dispose()
    trendChartInstance = null
    distributionChartInstance = null
    mapChartInstance = null
    georoamHandler = null
  }

  function renderCharts() {
    renderTrendChart()
    renderDistributionChart()
    renderMapChart()
    resizeCharts()
  }

  function renderChartsOnNextFrame() {
    return nextTick(() => new Promise(resolve => {
      window.requestAnimationFrame(() => {
        renderCharts()
        resolve()
      })
    }))
  }

  function loadDashboard() {
    if (typeof fetchDashboard !== 'function') {
      return Promise.resolve()
    }
    loading.value = true
    return fetchDashboard(queryParams.value).then(response => {
      dashboardData.value = response.data || {}
      return renderChartsOnNextFrame()
    }).finally(() => {
      loading.value = false
    })
  }

  onMounted(() => {
    loadDashboard()
    window.addEventListener('resize', resizeCharts)
  })

  onActivated(() => {
    if (!dashboardData.value || !Object.keys(dashboardData.value).length) {
      loadDashboard()
      return
    }
    renderChartsOnNextFrame()
  })

  onDeactivated(() => {
    disposeCharts()
  })

  onBeforeUnmount(() => {
    window.removeEventListener('resize', resizeCharts)
    disposeCharts()
  })

  return {
    loading,
    queryParams,
    dashboardData,
    indicators,
    trendList,
    distributionList,
    featureCollection,
    featureTableList,
    enterpriseTableRows,
    visibleEnterpriseRows,
    mapViewBounds,
    trendChartRef,
    distributionChartRef,
    mapChartRef,
    handleQuery,
    resetQuery,
    handleExport,
    openModule,
    loadDashboard,
    highlightMapPoint,
    updateMapViewBoundsFromChart
  }
}
