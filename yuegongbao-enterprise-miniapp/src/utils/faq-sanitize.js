const QUESTION_ONLY_RE = /^[\s?？]+$/

function normalizeText(value) {
  return typeof value === 'string' ? value.trim() : ''
}

export function sanitizeQuestionPlaceholder(value, fallback = '') {
  const text = normalizeText(value)
  if (!text) {
    return fallback
  }
  if (QUESTION_ONLY_RE.test(text)) {
    return fallback
  }
  return text
}

export function sanitizeFaqRow(item = {}, index = 0) {
  return {
    ...item,
    title: sanitizeQuestionPlaceholder(item.title, `常见问题 ${index + 1}`),
    summary: sanitizeQuestionPlaceholder(item.summary, '内容待补充'),
    category: sanitizeQuestionPlaceholder(item.category, '说明')
  }
}

export function sanitizeFaqRows(rows = []) {
  if (!Array.isArray(rows)) {
    return []
  }
  return rows.map((item, index) => sanitizeFaqRow(item, index))
}

export function sanitizeFaqDetail(detail = {}) {
  const paragraphs = Array.isArray(detail?.paragraphs)
    ? detail.paragraphs
        .map((item) => sanitizeQuestionPlaceholder(item, ''))
        .filter(Boolean)
    : []

  return {
    ...detail,
    title: sanitizeQuestionPlaceholder(detail?.title, '常见问题'),
    summary: sanitizeQuestionPlaceholder(detail?.summary, paragraphs[0] || '内容待补充'),
    category: sanitizeQuestionPlaceholder(detail?.category, '说明'),
    paragraphs: paragraphs.length ? paragraphs : ['内容待补充，待后台补齐。']
  }
}
