/**
 * Batch-replace legacy blue palette with teal design tokens in pages/*.vue
 */
const fs = require('fs')
const path = require('path')

const PAGES_DIR = path.join(__dirname, '..', 'pages')

const REPLACEMENTS = [
  ['#1f6fd6', '#0f766e'],
  ['#1e6fcd', '#0f766e'],
  ['#0d3155', '#0c4a44'],
  ['#0f6d8f', '#0f766e'],
  ['#16745f', '#14b8a6'],
  ['#16324f', '#122d42'],
  ['#7890aa', '#607789'],
  ['#58738f', '#536b7d'],
  ['#57748f', '#536b7d'],
  ['#36506b', '#183247'],
  ['#6d8298', '#627789'],
  ['#7b8ea5', '#627789'],
  ['#f5f8fc', '#f7fbfc'],
  ['#edf2f7', '#e4edf2'],
  ['#eef4fb', '#e6f2ef'],
  ['#eef5ff', '#e6f2ef'],
  ['#eef6ff', '#e8f0fb'],
  ['#eaf3ff', '#e8f0fb'],
  ['#eef3f8', '#f4f8fa'],
  ['#e9eef5', '#e4edf2'],
  ['#e7f4f1', '#e6f2ef'],
  ['color="#1f6fd6"', 'color="#0f766e"']
]

function walk(dir, files = []) {
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name)
    if (entry.isDirectory()) walk(full, files)
    else if (entry.name.endsWith('.vue')) files.push(full)
  }
  return files
}

let changed = 0
walk(PAGES_DIR).forEach((file) => {
  let content = fs.readFileSync(file, 'utf8')
  const original = content
  REPLACEMENTS.forEach(([from, to]) => {
    content = content.split(from).join(to)
  })
  if (content !== original) {
    fs.writeFileSync(file, content, 'utf8')
    changed++
    console.log('updated colors:', path.relative(PAGES_DIR, file))
  }
})

console.log(`Color migration done. ${changed} files updated.`)
