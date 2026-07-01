/** 广东省常用行政区划名称 */
export const gdRegionNameMap = {
  '440000': '广东省',
  '440100': '广州市',
  '440103': '广州市荔湾区',
  '440104': '广州市越秀区',
  '440105': '广州市海珠区',
  '440106': '广州市天河区',
  '440111': '广州市白云区',
  '440112': '广州市黄埔区',
  '440113': '广州市番禺区',
  '440114': '广州市花都区',
  '440115': '广州市南沙区',
  '440117': '广州市从化区',
  '440118': '广州市增城区',
  '440300': '深圳市',
  '440303': '深圳市罗湖区',
  '440304': '深圳市福田区',
  '440305': '深圳市南山区',
  '440306': '深圳市宝安区',
  '440307': '深圳市龙岗区',
  '440308': '深圳市盐田区',
  '440309': '深圳市龙华区',
  '440310': '深圳市坪山区',
  '440311': '深圳市光明区',
  '440600': '佛山市',
  '440604': '佛山市禅城区',
  '440605': '佛山市南海区',
  '440606': '佛山市顺德区',
  '440607': '佛山市三水区',
  '440608': '佛山市高明区',
  '440700': '江门市',
  '441900': '东莞市',
  '442000': '中山市'
}

export const gdRegionOptions = Object.entries(gdRegionNameMap).map(([value, label]) => ({
  value,
  label
}))

export function normalizeRegionCode(code) {
  if (code === undefined || code === null || code === '') {
    return undefined
  }
  return String(code)
}

export function formatRegionName(code, fallback = '全部区域') {
  const normalized = normalizeRegionCode(code)
  if (!normalized) {
    return fallback
  }
  return gdRegionNameMap[normalized] || normalized
}

export function ensureRegionInOptions(options, regionCode) {
  const source = Array.isArray(options) ? options : []
  const normalized = normalizeRegionCode(regionCode)
  if (!normalized || source.some(item => item.value === normalized)) {
    return source
  }
  return [...source, { value: normalized, label: formatRegionName(normalized, normalized) }]
}
