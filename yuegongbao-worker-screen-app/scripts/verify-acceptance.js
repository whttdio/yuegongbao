const fs = require('fs')
const path = require('path')

const projectRoot = path.resolve(__dirname, '..')
const pagesRoot = path.join(projectRoot, 'pages')
const srcPagesRoot = path.join(projectRoot, 'src', 'pages')
const pagesJsonPath = path.join(projectRoot, 'pages.json')
const authStorageKeys = [
  'worker_token',
  'worker_login_type',
  'worker_login_account',
  'worker_profile_export_snapshot'
]
const authStorageAllowedFiles = new Set([
  'utils/request.js',
  'src/utils/request.js'
])
const mirroredSourcePairs = [
  { sourceRoot: 'api', mirrorRoot: 'src/api' },
  { sourceRoot: 'utils', mirrorRoot: 'src/utils' }
]
const snapshotPattern = /验收快照|联调快照|接口联调快照|推送本地快照|\/app\/screen\/\*\*/
const snapshotRequiredFiles = new Set([
  'standby/index.vue',
  'login/index.vue',
  'main/index.vue',
  'device/index.vue',
  'attendance/checkin.vue',
  'attendance/detail.vue',
  'salary/list.vue',
  'salary/detail.vue',
  'social/list.vue',
  'social/detail.vue',
  'tax/list.vue',
  'tax/detail.vue',
  'complaint/index.vue',
  'complaint/detail.vue',
  'job/list.vue',
  'job/detail.vue',
  'job/map.vue',
  'training/history-detail.vue',
  'profile/settings.vue'
])
const guardedPageChecks = [
  {
    file: 'home/index.vue',
    snippets: ['normalizeWorkerJumpTarget', 'openWorkerJumpTarget']
  },
  {
    file: 'workbench/index.vue',
    snippets: ['normalizeWorkerJumpTarget', 'openWorkerJumpTarget']
  },
  {
    file: 'union/index.vue',
    snippets: ['normalizeWorkerJumpTarget', 'openWorkerJumpTarget']
  },
  {
    file: 'profile/help.vue',
    snippets: ['normalizeWorkerJumpTarget', 'openWorkerJumpTarget']
  },
  {
    file: 'profile/help-detail.vue',
    snippets: ['normalizeWorkerJumpTarget', 'openWorkerJumpTarget']
  },
  {
    file: 'notice/detail.vue',
    snippets: ['normalizeWorkerJumpTarget', 'openWorkerJumpTarget']
  },
  {
    file: 'login/index.vue',
    snippets: ['runFaceAuthMock']
  },
  {
    file: 'profile/settings.vue',
    snippets: ['normalizeWorkerJumpTarget', 'readPendingWorkerJumpTarget']
  },
  {
    file: 'standby/index.vue',
    snippets: ['验收快照']
  },
  {
    file: 'main/index.vue',
    snippets: ['验收快照']
  },
  {
    file: 'device/index.vue',
    snippets: ['联调快照']
  }
]

function walkVueFiles(rootDir, currentDir = rootDir) {
  const files = []
  const entries = fs.readdirSync(currentDir, { withFileTypes: true })
  for (const entry of entries) {
    const fullPath = path.join(currentDir, entry.name)
    if (entry.isDirectory()) {
      files.push(...walkVueFiles(rootDir, fullPath))
      continue
    }
    if (!entry.isFile() || path.extname(entry.name) !== '.vue') {
      continue
    }
    files.push(path.relative(rootDir, fullPath).replace(/\\/g, '/'))
  }
  return files.sort()
}

function walkSourceFiles(rootDir, currentDir = rootDir) {
  const files = []
  const entries = fs.readdirSync(currentDir, { withFileTypes: true })
  for (const entry of entries) {
    const fullPath = path.join(currentDir, entry.name)
    if (entry.isDirectory()) {
      files.push(...walkSourceFiles(rootDir, fullPath))
      continue
    }
    if (!entry.isFile() || !['.js', '.vue'].includes(path.extname(entry.name))) {
      continue
    }
    files.push(path.relative(rootDir, fullPath).replace(/\\/g, '/'))
  }
  return files.sort()
}

function readFile(filePath) {
  return fs.readFileSync(filePath, 'utf8')
}

function collectPagePaths(pagesConfig) {
  const routes = []
  if (Array.isArray(pagesConfig.pages)) {
    for (const page of pagesConfig.pages) {
      if (page && typeof page.path === 'string') {
        routes.push(`${page.path}.vue`.replace(/^pages\//, ''))
      }
    }
  }
  return routes.sort()
}

function difference(left, right) {
  const rightSet = new Set(right)
  return left.filter((item) => !rightSet.has(item))
}

function collectAuthStorageScopeFailures() {
  const failures = []
  const sourceRoots = ['pages', 'src/pages', 'utils', 'src/utils']
  const directStoragePattern = new RegExp(
    `uni\\.(?:getStorageSync|setStorageSync|removeStorageSync)\\(\\s*['"](?:${authStorageKeys.join('|')})['"]`,
    'g'
  )
  const baseKeyConstantPattern = new RegExp(
    `const\\s+WORKER_(?:TOKEN|LOGIN_TYPE|LOGIN_ACCOUNT|PROFILE_EXPORT)_KEY\\s*=\\s*['"](?:${authStorageKeys.join('|')})['"]`,
    'g'
  )

  for (const sourceRoot of sourceRoots) {
    const absoluteRoot = path.join(projectRoot, sourceRoot)
    if (!fs.existsSync(absoluteRoot)) {
      continue
    }
    for (const relativeFile of walkSourceFiles(absoluteRoot)) {
      const projectFile = `${sourceRoot}/${relativeFile}`
      if (authStorageAllowedFiles.has(projectFile)) {
        continue
      }
      const content = readFile(path.join(absoluteRoot, relativeFile))
      if (directStoragePattern.test(content) || baseKeyConstantPattern.test(content)) {
        failures.push(`登录态隔离残留：${projectFile} 仍直接读写全局登录态 key`)
      }
      directStoragePattern.lastIndex = 0
      baseKeyConstantPattern.lastIndex = 0
    }
  }

  return failures
}

function collectMirroredSourceFailures() {
  const failures = []
  for (const pair of mirroredSourcePairs) {
    const sourceRoot = path.join(projectRoot, pair.sourceRoot)
    const mirrorRoot = path.join(projectRoot, pair.mirrorRoot)
    if (!fs.existsSync(sourceRoot)) {
      failures.push(`mirror source missing: ${pair.sourceRoot}`)
      continue
    }
    if (!fs.existsSync(mirrorRoot)) {
      failures.push(`mirror target missing: ${pair.mirrorRoot}`)
      continue
    }
    const sourceFiles = walkSourceFiles(sourceRoot)
    const mirrorFiles = walkSourceFiles(mirrorRoot)
    const missingInMirror = difference(sourceFiles, mirrorFiles)
    const missingInSource = difference(mirrorFiles, sourceFiles)
    for (const file of missingInMirror) {
      failures.push(`mirror target missing file: ${pair.mirrorRoot}/${file}`)
    }
    for (const file of missingInSource) {
      failures.push(`mirror source missing file: ${pair.sourceRoot}/${file}`)
    }
    for (const file of sourceFiles) {
      if (!mirrorFiles.includes(file)) {
        continue
      }
      const sourceContent = readFile(path.join(sourceRoot, file))
      const mirrorContent = readFile(path.join(mirrorRoot, file))
      if (sourceContent !== mirrorContent && !isReExportMirror(pair, file, mirrorContent)) {
        failures.push(`mirror content mismatch: ${pair.sourceRoot}/${file} vs ${pair.mirrorRoot}/${file}`)
      }
    }
  }
  return failures
}

function isReExportMirror(pair, file, mirrorContent) {
  const mirrorFile = `${pair.mirrorRoot}/${file}`
  const sourceFile = `${pair.sourceRoot}/${file}`
  const relativeImport = path.posix
    .relative(path.posix.dirname(mirrorFile), sourceFile)
    .replace(/\.js$/, '')
  const normalizedImport = relativeImport.startsWith('.') ? relativeImport : `./${relativeImport}`
  return mirrorContent.trim() === `export * from '${normalizedImport}'`
}

function exitWithFailures(failures, warnings = []) {
  if (warnings.length) {
    console.warn('Warnings:')
    for (const warning of warnings) {
      console.warn(`- ${warning}`)
    }
  }
  if (!failures.length) {
    return
  }
  console.error('Verification failed:')
  for (const failure of failures) {
    console.error(`- ${failure}`)
  }
  process.exitCode = 1
}

function main() {
  const failures = []
  const warnings = []

  const pageFiles = walkVueFiles(pagesRoot)
  const srcPageFiles = walkVueFiles(srcPagesRoot)
  const pagesJson = JSON.parse(readFile(pagesJsonPath))
  const declaredRoutes = collectPagePaths(pagesJson)

  const missingInSrc = difference(pageFiles, srcPageFiles)
  const missingInPages = difference(srcPageFiles, pageFiles)
  const missingFromRoutes = difference(pageFiles, declaredRoutes)
  const missingRouteFiles = difference(declaredRoutes, pageFiles)

  if (missingInSrc.length) {
    failures.push(`src/pages 缺少镜像文件：${missingInSrc.join(', ')}`)
  }
  if (missingInPages.length) {
    failures.push(`pages 缺少镜像文件：${missingInPages.join(', ')}`)
  }
  if (missingFromRoutes.length) {
    failures.push(`pages 目录存在但 pages.json 未声明：${missingFromRoutes.join(', ')}`)
  }
  if (missingRouteFiles.length) {
    failures.push(`pages.json 已声明但 pages 目录缺文件：${missingRouteFiles.join(', ')}`)
  }

  for (const relativeFile of pageFiles) {
    const pageFilePath = path.join(pagesRoot, relativeFile)
    const srcFilePath = path.join(srcPagesRoot, relativeFile)
    const pageContent = readFile(pageFilePath)
    const srcContent = readFile(srcFilePath)

    if (pageContent !== srcContent) {
      failures.push(`pages/src 镜像内容不一致：${relativeFile}`)
    }
    if (snapshotRequiredFiles.has(relativeFile) && !snapshotPattern.test(pageContent)) {
      failures.push(`页面缺少验收或联调快照标记：pages/${relativeFile}`)
    }
  }

  for (const check of guardedPageChecks) {
    const filePath = path.join(pagesRoot, check.file)
    if (!fs.existsSync(filePath)) {
      failures.push(`关键页面缺失：pages/${check.file}`)
      continue
    }
    const content = readFile(filePath)
    for (const snippet of check.snippets) {
      if (!content.includes(snippet)) {
        failures.push(`关键兼容逻辑缺失：pages/${check.file} 未包含 ${snippet}`)
      }
    }
  }

  failures.push(...collectMirroredSourceFailures())
  failures.push(...collectAuthStorageScopeFailures())

  if (pageFiles.length !== declaredRoutes.length) {
    warnings.push(`页面文件数 ${pageFiles.length} 与 pages.json 路由数 ${declaredRoutes.length} 不一致，请复核页面声明`)
  }

  console.log('UniApp acceptance verification summary:')
  console.log(`- pages 文件数 ${pageFiles.length}`)
  console.log(`- src/pages 文件数 ${srcPageFiles.length}`)
  console.log(`- pages.json 路由数 ${declaredRoutes.length}`)
  console.log(`- 快照标记检查 ${snapshotRequiredFiles.size - failures.filter((item) => item.includes('页面缺少验收或联调快照标记')).length}/${snapshotRequiredFiles.size}`)
  console.log(`- 关键兼容页检查 ${guardedPageChecks.length} 组`)

  exitWithFailures(failures, warnings)

  if (!failures.length) {
    console.log('Verification passed.')
  }
}

main()
