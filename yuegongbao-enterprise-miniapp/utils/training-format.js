export function formatTrainingMonth(month) {
  const text = String(month || '').trim()
  if (text.length === 6) {
    return `${text.slice(0, 4)}年${text.slice(4, 6)}月`
  }
  return text || '-'
}

export function courseProgressPercent(item) {
  const total = Number(item?.totalSeconds || 0)
  const studied = Number(item?.studiedSeconds || 0)
  if (!total) {
    return 0
  }
  return Math.min(Math.round((studied / total) * 100), 100)
}

export function courseProgressPercentText(item) {
  return `${courseProgressPercent(item)}%`
}
