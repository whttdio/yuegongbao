/**
 * 从页面 Vue 文件中移除已迁移至 styles/worker-ui.scss 的重复样式
 */
const fs = require('fs')
const path = require('path')

const PAGES_DIR = path.join(__dirname, '..', 'pages')

const GLOBAL_SELECTORS = [
  '.section-head',
  '.section-head--sub',
  '.section-head--compact',
  '.detail-row',
  '.detail-row:last-child',
  '.detail-row__label',
  '.detail-row__value',
  '.result-block',
  '.result-block__label',
  '.result-block__value',
  '.clear-action',
  '.notice-link',
  '.more-link',
  '.list-row',
  '.list-row:last-child',
  '.list-row__title',
  '.list-row__subtitle',
  '.settings-list',
  '.settings-row',
  '.settings-row:last-child',
  '.settings-row--danger',
  '.settings-row__tag',
  '.worker-tag--notice',
  '.worker-title--small',
  '.entry-grid',
  '.entry-grid--two',
  '.entry-item',
  '.entry-item--wide',
  '.entry-item--locked',
  '.entry-item__label',
  '.entry-item__tip',
  '.quick-grid',
  '.quick-item',
  '.quick-item__title',
  '.quick-item__summary',
  '.hero-stat-grid',
  '.hero-stat',
  '.hero-stat__value',
  '.hero-stat__label',
  '.service-panel',
  '.service-panel__title',
  '.service-panel__subtitle',
  '.service-panel__desc',
  '.service-actions',
  '.profile-grid',
  '.profile-item',
  '.profile-item__label',
  '.profile-item__value',
  '.profile-item__value--small',
  '.worker-empty--panel',
  '.worker-empty--inline',
  '.worker-empty__title',
  '.worker-empty__desc',
  '.worker-empty__actions',
  '.form-input',
  '.form-textarea',
  '.form-picker',
  '.form-picker__text',
  '.switch-row',
  '.sms-row',
  '.sms-row__input',
  '.sms-row__button',
  '.sms-tip',
  '.login-tabs',
  '.login-tab',
  '.login-tab--active',
  '.attachment-actions',
  '.attachment-actions--empty',
  '.attachment-list',
  '.attachment-row',
  '.attachment-row:last-child',
  '.attachment-row__text',
  '.attachment-row__action',
  '.recommend-row',
  '.recommend-row:last-child',
  '.recommend-row__title',
  '.recommend-row__desc',
  '.notice-row',
  '.notice-row:last-child',
  '.notice-row__title',
  '.notice-row__meta'
]

function escapeRegExp(value) {
  return value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
}

function removeRule(css, selector) {
  const pattern = new RegExp(`${escapeRegExp(selector)}\\s*\\{[^}]*\\}\\s*`, 'g')
  return css.replace(pattern, '')
}

function stripGlobalRules(css) {
  let next = css
  GLOBAL_SELECTORS.forEach((selector) => {
    next = removeRule(next, selector)
  })
  return next.replace(/\n{3,}/g, '\n\n').trim()
}

function processFile(filePath) {
  const source = fs.readFileSync(filePath, 'utf8')
  const styleMatch = source.match(/<style[^>]*lang="scss"[^>]*>([\s\S]*?)<\/style>/)
  if (!styleMatch) {
    return false
  }

  const originalCss = styleMatch[1]
  const cleanedCss = stripGlobalRules(originalCss)
  if (cleanedCss === originalCss.trim()) {
    return false
  }

  let updated = source
  if (!cleanedCss) {
    updated = source.replace(/\n<style lang="scss">[\s\S]*?<\/style>\n?/, '\n')
  } else {
    updated = source.replace(
      /<style lang="scss">[\s\S]*?<\/style>/,
      `<style lang="scss">\n${cleanedCss}\n</style>`
    )
  }

  fs.writeFileSync(filePath, updated, 'utf8')
  return true
}

function walk(dir) {
  let changed = 0
  fs.readdirSync(dir, { withFileTypes: true }).forEach((entry) => {
    const fullPath = path.join(dir, entry.name)
    if (entry.isDirectory()) {
      changed += walk(fullPath)
    } else if (entry.name.endsWith('.vue')) {
      if (processFile(fullPath)) {
        changed += 1
        console.log('cleaned:', path.relative(PAGES_DIR, fullPath))
      }
    }
  })
  return changed
}

const total = walk(PAGES_DIR)
console.log(`Done. Cleaned ${total} files.`)
