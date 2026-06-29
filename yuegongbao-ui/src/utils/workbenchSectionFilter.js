const CARD_SELECTOR = [
  '.ygb-focus-card',
  '.azb-focus-card',
  '.ygb-device-panel',
  '.azb-device-panel',
  '.cockpit-screen .screen-panel',
  '.ygb-cockpit .panel',
  '.azb-cockpit .card',
  '.portal-explanation-card'
].join(', ')

const GRID_SELECTOR = [
  '.ygb-focus-grid',
  '.azb-focus-grid',
  '.ygb-device-page__grid',
  '.azb-device-page__grid',
  '.cockpit-screen .metric-grid',
  '.ygb-cockpit .command-grid',
  '.ygb-cockpit .ops-grid',
  '.ygb-cockpit .module-grid',
  '.azb-cockpit .focus-grid',
  '.azb-cockpit .module-grid'
].join(', ')

const TITLE_SELECTOR = [
  '.ygb-card-head__title',
  '.azb-card-head__title',
  '.ygb-device-panel__title',
  '.azb-device-panel__title',
  '.panel-title',
  '.card-title',
  '.portal-explanation-card__title'
].join(', ')

const HIDDEN_CARD_ATTRIBUTE = 'data-workbench-section-hidden'
const HIDDEN_GRID_ATTRIBUTE = 'data-workbench-grid-hidden'
const VISIBLE_COUNT_ATTRIBUTE = 'data-workbench-visible-count'

const TITLE_PATTERNS = [
  /办理承接焦点/,
  /^办理焦点$/,
  /焦点队列$/,
  /^当前选中/,
  /^当前焦点/,
  /(办理路径|承接路径|治理路径|工作路径)$/,
  /^治理步骤$/,
  /(办理提示|治理提示|风险提示|工作提示|主数据提示|报备提示)$/,
  /^530\.1\b/
]

function normalizeTitleText(text = '') {
  return text.replace(/\s+/g, '').trim()
}

function shouldHideWorkbenchCard(title) {
  const normalizedTitle = normalizeTitleText(title)
  return TITLE_PATTERNS.some((pattern) => pattern.test(normalizedTitle))
}

function resolveRootElement(root) {
  if (!root) {
    return document
  }

  return root
}

function queryAll(root, selector) {
  if (root instanceof Document) {
    return root.querySelectorAll(selector)
  }

  return root.querySelectorAll(selector)
}

function matchesCard(child) {
  return child instanceof Element && child.matches(CARD_SELECTOR)
}

function syncWorkbenchCards(root) {
  const cards = Array.from(queryAll(root, CARD_SELECTOR))

  cards.forEach((card) => {
    const titleElement = card.querySelector(TITLE_SELECTOR)
    const shouldHide = shouldHideWorkbenchCard(titleElement?.textContent || '')

    if (shouldHide) {
      card.setAttribute(HIDDEN_CARD_ATTRIBUTE, 'true')
      return
    }

    card.removeAttribute(HIDDEN_CARD_ATTRIBUTE)
  })
}

function syncWorkbenchGrids(root) {
  const grids = Array.from(queryAll(root, GRID_SELECTOR))

  grids.forEach((grid) => {
    const childCards = Array.from(grid.children).filter(matchesCard)

    if (!childCards.length) {
      grid.removeAttribute(HIDDEN_GRID_ATTRIBUTE)
      grid.removeAttribute(VISIBLE_COUNT_ATTRIBUTE)
      return
    }

    const visibleCount = childCards.filter((child) => child.getAttribute(HIDDEN_CARD_ATTRIBUTE) !== 'true').length

    grid.setAttribute(VISIBLE_COUNT_ATTRIBUTE, String(visibleCount))

    if (visibleCount === 0) {
      grid.setAttribute(HIDDEN_GRID_ATTRIBUTE, 'true')
      return
    }

    grid.removeAttribute(HIDDEN_GRID_ATTRIBUTE)
  })
}

export function syncWorkbenchSectionFilter(root) {
  if (typeof document === 'undefined') {
    return
  }

  const resolvedRoot = resolveRootElement(root)
  syncWorkbenchCards(resolvedRoot)
  syncWorkbenchGrids(resolvedRoot)
}

export function startWorkbenchSectionFilter(root) {
  if (typeof window === 'undefined' || typeof MutationObserver === 'undefined') {
    return () => {}
  }

  const resolvedRoot = resolveRootElement(root)
  const observeTarget = resolvedRoot instanceof Document ? resolvedRoot.body : resolvedRoot

  if (!observeTarget) {
    return () => {}
  }

  let rafId = 0

  const scheduleSync = () => {
    if (rafId) {
      return
    }

    rafId = window.requestAnimationFrame(() => {
      rafId = 0
      syncWorkbenchSectionFilter(resolvedRoot)
    })
  }

  scheduleSync()

  const observer = new MutationObserver(() => {
    scheduleSync()
  })

  observer.observe(observeTarget, {
    childList: true,
    subtree: true,
    characterData: true
  })

  return () => {
    observer.disconnect()

    if (rafId) {
      window.cancelAnimationFrame(rafId)
    }
  }
}
