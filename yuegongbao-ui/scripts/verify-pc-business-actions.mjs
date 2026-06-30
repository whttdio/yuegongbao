import fs from 'node:fs'
import path from 'node:path'

const uiRoot = path.resolve(import.meta.dirname, '..')
const repoRoot = path.resolve(uiRoot, '..')
const viewsRoot = path.join(uiRoot, 'src', 'views', 'ygb')

const failures = [
  ...findEmptyClickHandlers(),
  ...findDetachedSuccessMessages()
]

if (failures.length) {
  console.error('[verify-pc-business-actions] failed')
  failures.slice(0, 120).forEach(item => console.error(`- ${item}`))
  if (failures.length > 120) {
    console.error(`- ... ${failures.length - 120} more`)
  }
  process.exit(1)
}

console.log(`[verify-pc-business-actions] ok: ${walk(viewsRoot).filter(file => file.endsWith('.vue')).length} PC business views checked`)

function findEmptyClickHandlers() {
  const problems = []

  for (const file of walk(viewsRoot).filter(file => file.endsWith('.vue'))) {
    const text = fs.readFileSync(file, 'utf8')
    const functionRanges = extractFunctionRanges(text)
    const clickHandlers = extractClickHandlerNames(text)

    for (const handlerName of clickHandlers) {
      const range = functionRanges.find(item => item.name === handlerName)
      if (!range) {
        continue
      }
      const normalizedBody = normalizeBody(text.slice(range.bodyStart + 1, range.end - 1))
      if (!normalizedBody) {
        problems.push(`${relative(file)}:${lineNumber(text, range.start)} empty @click handler ${handlerName}`)
      }
    }
  }

  return problems
}

function findDetachedSuccessMessages() {
  const problems = []
  const successPattern = /(ElMessage\.success|(?:proxy\.)?\$modal\.msgSuccess|proxy\.\$modal\.msgSuccess)/
  const realActionPattern = /\b(?:request|axios|fetch)\s*\(|proxy\.download\s*\(|operation\.action\s*\(|\b[a-zA-Z_$][\w$]*(?:Api|Action|Confirm|Review|Status|Submit|Delete|Remove|Add|Update|Save|Sync|Generate|Calculate|Compare|Process|Dispose|Handle|Mark|Lock|Unlock|Authorize|Account|Callback|Import|Export)\s*\(|\b(?:add|update|del|delete|get|list|sync|generate|submit|review|mark|confirm|batch|save|create|approve|reject|lock|unlock|authorize|compare|calculate|account|callback|import)[A-Z][\w$]*\s*\(/

  for (const file of walk(viewsRoot).filter(file => file.endsWith('.vue') || file.endsWith('.js'))) {
    const text = fs.readFileSync(file, 'utf8')
    const functionRanges = extractFunctionRanges(text)

    for (const range of functionRanges) {
      const body = text.slice(range.bodyStart + 1, range.end - 1)
      if (!successPattern.test(body)) {
        continue
      }
      const bodyWithoutMessageCalls = body.replace(/(?:ElMessage|(?:proxy\.)?\$modal|proxy\.\$modal)\.(?:success|msgSuccess|warning|msgWarning|error|msgError|confirm|prompt)\s*\([^)]*\)/g, '')
      if (!realActionPattern.test(bodyWithoutMessageCalls)) {
        problems.push(`${relative(file)}:${lineNumber(text, range.start)} success message is not tied to a backend action in ${range.name}`)
      }
    }
  }

  return problems
}

function extractClickHandlerNames(text) {
  const names = new Set()
  const clickPattern = /@click(?:\.stop|\.prevent|\.self|\.capture|\.once|\.passive)*="([^"]+)"/g
  let match
  while ((match = clickPattern.exec(text)) !== null) {
    const expression = match[1].trim()
    const handlerMatch = expression.match(/^([A-Za-z_$][\w$]*)\s*(?:\(|$)/)
    if (handlerMatch && !isLocalUiOnlyHandler(handlerMatch[1])) {
      names.add(handlerMatch[1])
    }
  }
  return names
}

function isLocalUiOnlyHandler(name) {
  return [
    'cancel',
    'resetQuery',
    'handleQuery',
    'getList',
    'refreshAll',
    'clearWorkbenchContext'
  ].includes(name)
}

function extractFunctionRanges(text) {
  const ranges = []
  const patterns = [
    /(?:async\s+)?function\s+([A-Za-z_$][\w$]*)\s*\([^)]*\)\s*\{/g,
    /const\s+([A-Za-z_$][\w$]*)\s*=\s*(?:async\s*)?\([^)]*\)\s*=>\s*\{/g
  ]

  for (const pattern of patterns) {
    let match
    while ((match = pattern.exec(text)) !== null) {
      const bodyStart = text.indexOf('{', pattern.lastIndex - 1)
      const end = findMatchingBrace(text, bodyStart)
      ranges.push({
        name: match[1],
        start: match.index,
        bodyStart,
        end
      })
    }
  }

  return ranges
}

function findMatchingBrace(text, openIndex) {
  let depth = 0
  let quote = ''
  let escaped = false

  for (let index = openIndex; index < text.length; index += 1) {
    const char = text[index]
    if (quote) {
      if (escaped) {
        escaped = false
      } else if (char === '\\') {
        escaped = true
      } else if (char === quote) {
        quote = ''
      }
      continue
    }
    if (char === '"' || char === "'" || char === '`') {
      quote = char
      continue
    }
    if (char === '{') {
      depth += 1
    } else if (char === '}') {
      depth -= 1
      if (depth === 0) {
        return index + 1
      }
    }
  }

  return text.length
}

function normalizeBody(body) {
  return String(body || '')
    .replace(/\/\*[\s\S]*?\*\//g, '')
    .replace(/\/\/.*$/gm, '')
    .trim()
}

function walk(dir, files = []) {
  if (!fs.existsSync(dir)) return files
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name)
    if (entry.isDirectory()) walk(full, files)
    else if (entry.isFile()) files.push(full)
  }
  return files
}

function lineNumber(text, index) {
  return text.slice(0, index).split(/\r?\n/).length
}

function relative(file) {
  return path.relative(repoRoot, file).replace(/\\/g, '/')
}
