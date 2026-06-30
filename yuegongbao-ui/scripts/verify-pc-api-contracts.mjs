import fs from 'node:fs'
import path from 'node:path'

const uiRoot = path.resolve(import.meta.dirname, '..')
const repoRoot = path.resolve(uiRoot, '..')
const apiRoot = path.join(uiRoot, 'src', 'api', 'ygb')
const viewsRoot = path.join(uiRoot, 'src', 'views')
const javaRoots = [
  path.join(repoRoot, 'yuegongbao-business', 'src', 'main', 'java', 'com', 'yuegongbao', 'ygb'),
  path.join(repoRoot, 'yuegongbao-admin', 'src', 'main', 'java', 'com', 'yuegongbao', 'web', 'controller')
]

const frontendEndpoints = [
  ...extractRequestEndpoints(),
  ...extractDownloadEndpoints()
]
const backendMappings = extractBackendMappings()
const failures = []

for (const endpoint of frontendEndpoints) {
  if (shouldSkipEndpoint(endpoint)) {
    continue
  }
  if (!matchesBackend(endpoint)) {
    failures.push(endpoint)
  }
}

if (failures.length) {
  console.error('[verify-pc-api-contracts] failed')
  failures.slice(0, 120).forEach(endpoint => {
    console.error(`- missing backend ${endpoint.method.toUpperCase()} ${endpoint.url} (${relative(endpoint.file)}:${endpoint.line}${endpoint.name ? ` ${endpoint.name}` : ''})`)
  })
  if (failures.length > 120) {
    console.error(`- ... ${failures.length - 120} more`)
  }
  process.exit(1)
}

console.log(`[verify-pc-api-contracts] ok: ${frontendEndpoints.length} frontend endpoints, ${backendMappings.length} backend mappings`)

function extractRequestEndpoints() {
  const endpoints = []
  for (const file of walk(apiRoot).filter(file => file.endsWith('.js'))) {
    const text = fs.readFileSync(file, 'utf8')
    const functionRanges = extractFunctionRanges(text)
    const requestPattern = /request\s*\(\s*\{([\s\S]*?)\n\s*\}\s*\)/g
    let match
    while ((match = requestPattern.exec(text)) !== null) {
      const objectText = match[1]
      const urlValue = extractUrlPropertyValue(objectText)
      if (!urlValue) {
        continue
      }
      const methodValue = extractPropertyValue(objectText, 'method')
      const method = normalizeMethod(methodValue || 'get')
      const owner = functionRanges.find(range => match.index >= range.start && match.index <= range.end)
      endpoints.push({
        source: 'request',
        file,
        line: lineNumber(text, match.index),
        name: owner?.name || '',
        method,
        url: normalizeFrontendUrl(urlValue)
      })
    }
  }
  return endpoints
}

function extractDownloadEndpoints() {
  const endpoints = []
  const roots = [
    path.join(viewsRoot, 'ygb'),
    path.join(viewsRoot, 'device'),
    path.join(viewsRoot, 'aqInsurance'),
    path.join(viewsRoot, 'warning'),
    path.join(viewsRoot, 'creditScore')
  ]
  for (const root of roots) {
    for (const file of walk(root).filter(file => file.endsWith('.vue') || file.endsWith('.js'))) {
      const text = fs.readFileSync(file, 'utf8')
      const downloadPattern = /proxy\.download\s*\(\s*(['"`])([^'"`]+)\1/g
      let match
      while ((match = downloadPattern.exec(text)) !== null) {
        endpoints.push({
          source: 'download',
          file,
          line: lineNumber(text, match.index),
          name: '',
          method: 'post',
          url: normalizeFrontendUrl(match[2])
        })
      }
    }
  }
  return endpoints
}

function extractBackendMappings() {
  const mappings = []
  for (const root of javaRoots) {
    for (const file of walk(root).filter(file => file.endsWith('.java'))) {
      const text = fs.readFileSync(file, 'utf8')
      const classMatch = text.match(/@RequestMapping\s*\(([^\r\n)]*)\)[\s\r\n]*(?:public\s+)?class\s+\w+/)
      const basePaths = classMatch ? extractAnnotationPaths(classMatch[1]) : ['']
      const methodPattern = /@(GetMapping|PostMapping|PutMapping|DeleteMapping|RequestMapping)\s*(?:\(([^\r\n)]*)\))?[^\r\n]*(?:\r?\n\s*@[^\r\n]+)*\r?\n\s*public\s+[^{;=]+?\s+(\w+)\s*\(/g
      let match
      while ((match = methodPattern.exec(text)) !== null) {
        const [, annotation, args = '', methodName] = match
        const methods = annotation === 'RequestMapping' ? extractRequestMappingMethods(args) : [annotationMethod(annotation)]
        const paths = extractAnnotationPaths(args)
        for (const base of basePaths) {
          for (const localPath of paths.length ? paths : ['']) {
            for (const method of methods) {
              mappings.push({
                file,
                line: lineNumber(text, match.index),
                method,
                path: normalizeBackendPath(joinPath(base, localPath)),
                name: methodName
              })
            }
          }
        }
      }
    }
  }
  return dedupeMappings(mappings)
}

function matchesBackend(endpoint) {
  const frontendPath = normalizeComparablePath(endpoint.url)
  return backendMappings.some(mapping => {
    if (mapping.method !== endpoint.method) {
      return false
    }
    return pathTemplateToRegex(mapping.path).test(frontendPath)
  })
}

function shouldSkipEndpoint(endpoint) {
  if (!endpoint.url) {
    return true
  }
  if (!/^\/(?:ygb|app|open|system|monitor)\//.test(endpoint.url)) {
    return true
  }
  return false
}

function extractFunctionRanges(text) {
  const ranges = []
  const pattern = /export\s+function\s+(\w+)\s*\([^)]*\)\s*\{|function\s+(\w+)\s*\([^)]*\)\s*\{|export\s+const\s+(\w+)\s*=\s*(?:async\s*)?\([^)]*\)\s*=>\s*\{/g
  let match
  while ((match = pattern.exec(text)) !== null) {
    const openIndex = text.indexOf('{', match.index)
    const end = findMatchingBrace(text, openIndex)
    ranges.push({ name: match[1] || match[2] || match[3], start: match.index, end })
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
      if (escaped) escaped = false
      else if (char === '\\') escaped = true
      else if (char === quote) quote = ''
      continue
    }
    if (char === '"' || char === "'" || char === '`') {
      quote = char
      continue
    }
    if (char === '{') depth += 1
    else if (char === '}') {
      depth -= 1
      if (depth === 0) return index
    }
  }
  return text.length
}

function extractPropertyValue(objectText, key) {
  const regex = new RegExp(`${key}\\s*:\\s*(['"\`])([\\s\\S]*?)\\1`)
  const match = objectText.match(regex)
  return match ? match[2] : ''
}

function extractUrlPropertyValue(objectText) {
  const match = objectText.match(/url\s*:\s*([^\r\n,]+)/)
  if (!match) {
    return ''
  }
  return expressionToUrlTemplate(match[1])
}

function expressionToUrlTemplate(expression) {
  const pieces = String(expression || '').split(/\s*\+\s*/)
  const routeParts = []
  for (const piece of pieces) {
    const literalMatch = piece.trim().match(/^(['"`])([\s\S]*?)\1$/)
    if (literalMatch) {
      routeParts.push(literalMatch[2])
    } else if (piece.trim()) {
      routeParts.push('{param}')
    }
  }
  return routeParts.join('')
}

function extractAnnotationPaths(args = '') {
  const values = [...args.matchAll(/"([^"]*)"/g)].map(match => match[1])
  const pathLikeValues = values.filter(value => value.startsWith('/') || value === '')
  return pathLikeValues.length ? pathLikeValues : ['']
}

function extractRequestMappingMethods(args = '') {
  const methods = [...args.matchAll(/RequestMethod\.(GET|POST|PUT|DELETE)/g)].map(match => match[1].toLowerCase())
  return methods.length ? methods : ['get', 'post', 'put', 'delete']
}

function annotationMethod(annotation) {
  return annotation.replace('Mapping', '').toLowerCase()
}

function normalizeMethod(method = 'get') {
  return String(method).replace(/[^\w]/g, '').toLowerCase() || 'get'
}

function normalizeFrontendUrl(url) {
  let normalized = String(url || '').trim()
  if (/^\$\{[^}]+}/.test(normalized)) {
    normalized = normalized.replace(/^\$\{[^}]+}\s*/, '')
  }
  normalized = normalized.replace(/\$\{[^}]+}/g, '{param}')
  normalized = normalized.replace(/^`|`$/g, '')
  normalized = normalized.startsWith('/') ? normalized : `/${normalized}`
  normalized = normalized.replace(/\/{2,}/g, '/')
  normalized = normalized.replace(/\/$/, '') || '/'
  return normalized
}

function normalizeBackendPath(value) {
  return normalizeFrontendUrl(value)
}

function normalizeComparablePath(value) {
  return normalizeFrontendUrl(value)
}

function pathTemplateToRegex(template) {
  const pattern = template
    .replace(/\/$/, '')
    .split('/')
    .map(part => {
      if (!part) return ''
      if (/^\{[^}]+}$/.test(part)) return '[^/]+'
      return escapeRegex(part)
    })
    .join('/')
  return new RegExp(`^${pattern || '/'}$`)
}

function joinPath(base, localPath) {
  return `/${String(base || '').replace(/^\/|\/$/g, '')}/${String(localPath || '').replace(/^\/|\/$/g, '')}`
}

function escapeRegex(value) {
  return value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
}

function dedupeMappings(mappings) {
  const seen = new Set()
  return mappings.filter(mapping => {
    const key = `${mapping.method} ${mapping.path}`
    if (seen.has(key)) return false
    seen.add(key)
    return true
  })
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
