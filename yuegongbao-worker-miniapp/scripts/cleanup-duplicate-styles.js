/**
 * Remove page-scoped styles now provided by worker-ui.scss
 */
const fs = require('fs')
const path = require('path')

const PAGES_DIR = path.join(__dirname, '..', 'pages')

const STYLE_BLOCK_PATTERNS = [
  /\.filter-scroll\s*\{[\s\S]*?\}\s*/g,
  /\.filter-row\s*\{[\s\S]*?\}\s*/g,
  /\.filter-chip\s*\{[\s\S]*?\}\s*/g,
  /\.filter-chip--active\s*\{[\s\S]*?\}\s*/g,
  /\.filter-head\s*\{[\s\S]*?\}\s*/g,
  /\.search-actions\s*\{[\s\S]*?\}\s*/g,
  /\.summary-grid\s*\{[\s\S]*?\}\s*/g,
  /\.summary-item\s*\{[\s\S]*?\}\s*/g,
  /\.summary-item__label\s*\{[\s\S]*?\}\s*/g,
  /\.summary-item__value\s*\{[\s\S]*?\}\s*/g,
  /\.detail-grid\s*\{[\s\S]*?\}\s*/g,
  /\.detail-item\s*\{[\s\S]*?\}\s*/g,
  /\.detail-item__label\s*\{[\s\S]*?\}\s*/g,
  /\.detail-item__value\s*\{[\s\S]*?\}\s*/g,
  /\.record-row\s*\{[\s\S]*?\}\s*/g,
  /\.record-row:last-child\s*\{[\s\S]*?\}\s*/g,
  /\.record-row__title\s*\{[\s\S]*?\}\s*/g,
  /\.record-row__subtitle\s*\{[\s\S]*?\}\s*/g,
  /\.record-row__link\s*\{[\s\S]*?\}\s*/g,
  /\.job-row\s*\{[\s\S]*?\}\s*/g,
  /\.job-row:last-child\s*\{[\s\S]*?\}\s*/g,
  /\.job-row__title\s*\{[\s\S]*?\}\s*/g,
  /\.job-row__meta\s*\{[\s\S]*?\}\s*/g,
  /\.job-row__footer\s*\{[\s\S]*?\}\s*/g,
  /\.job-row__salary\s*\{[\s\S]*?\}\s*/g,
  /\.status-grid\s*\{[\s\S]*?\}\s*/g,
  /\.status-item\s*\{[\s\S]*?\}\s*/g,
  /\.status-item__label\s*\{[\s\S]*?\}\s*/g,
  /\.status-item__value\s*\{[\s\S]*?\}\s*/g,
  /\.detail-block\s*\{[\s\S]*?\}\s*/g,
  /\.detail-block__title\s*\{[\s\S]*?\}\s*/g,
  /\.detail-block__content\s*\{[\s\S]*?\}\s*/g,
  /\.worker-tag--success\s*\{[\s\S]*?\}\s*/g,
  /\.worker-tag--warning\s*\{[\s\S]*?\}\s*/g,
  /\.worker-tag--info\s*\{[\s\S]*?\}\s*/g,
  /\.hotline\s*\{[\s\S]*?\}\s*/g
]

function walk(dir, files = []) {
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name)
    if (entry.isDirectory()) walk(full, files)
    else if (entry.name.endsWith('.vue')) files.push(full)
  }
  return files
}

function cleanupStyleBlock(styleContent) {
  let next = styleContent
  let prev = ''
  while (next !== prev) {
    prev = next
    STYLE_BLOCK_PATTERNS.forEach((pattern) => {
      next = next.replace(pattern, '')
    })
  }
  return next.replace(/\n{3,}/g, '\n\n').trim()
}

let changed = 0
walk(PAGES_DIR).forEach((file) => {
  let content = fs.readFileSync(file, 'utf8')
  const styleMatch = content.match(/<style[^>]*lang="scss"[^>]*scoped[^>]*>([\s\S]*?)<\/style>/)
  if (!styleMatch) return

  const cleaned = cleanupStyleBlock(styleMatch[1])
  if (cleaned === styleMatch[1].trim()) return

  const replacement = styleMatch[0].replace(styleMatch[1], cleaned ? `\n${cleaned}\n` : '\n')
  const nextContent = content.replace(styleMatch[0], cleaned ? replacement : '')
  if (nextContent !== content) {
    fs.writeFileSync(file, nextContent, 'utf8')
    changed++
    console.log('cleaned styles:', path.relative(PAGES_DIR, file))
  }
})

console.log(`Style cleanup done. ${changed} files updated.`)
