/**
 * Generate minimal tabBar PNG icons (81x81) for uni-app.
 * Uses only Node.js stdlib — no external dependencies.
 */
const fs = require('fs')
const path = require('path')
const zlib = require('zlib')

const SIZE = 81
const OUT_DIR = path.join(__dirname, '..', 'static', 'tabbar')

const CRC_TABLE = (() => {
  const table = new Uint32Array(256)
  for (let i = 0; i < 256; i++) {
    let c = i
    for (let k = 0; k < 8; k++) {
      c = c & 1 ? 0xedb88320 ^ (c >>> 1) : c >>> 1
    }
    table[i] = c >>> 0
  }
  return table
})()

function crc32(buf) {
  let crc = 0xffffffff
  for (let i = 0; i < buf.length; i++) {
    crc = CRC_TABLE[(crc ^ buf[i]) & 0xff] ^ (crc >>> 8)
  }
  return (crc ^ 0xffffffff) >>> 0
}

function writeChunk(type, data) {
  const typeBuf = Buffer.from(type, 'ascii')
  const len = Buffer.alloc(4)
  len.writeUInt32BE(data.length, 0)
  const crcBuf = Buffer.alloc(4)
  const crcData = Buffer.concat([typeBuf, data])
  crcBuf.writeUInt32BE(crc32(crcData), 0)
  return Buffer.concat([len, typeBuf, data, crcBuf])
}

function createPng(pixels) {
  const rowSize = 1 + SIZE * 4
  const raw = Buffer.alloc(rowSize * SIZE)
  for (let y = 0; y < SIZE; y++) {
    const rowStart = y * rowSize
    raw[rowStart] = 0
    for (let x = 0; x < SIZE; x++) {
      const i = (y * SIZE + x) * 4
      raw[rowStart + 1 + x * 4] = pixels[i]
      raw[rowStart + 1 + x * 4 + 1] = pixels[i + 1]
      raw[rowStart + 1 + x * 4 + 2] = pixels[i + 2]
      raw[rowStart + 1 + x * 4 + 3] = pixels[i + 3]
    }
  }
  const compressed = zlib.deflateSync(raw, { level: 9 })
  const ihdr = Buffer.alloc(13)
  ihdr.writeUInt32BE(SIZE, 0)
  ihdr.writeUInt32BE(SIZE, 4)
  ihdr[8] = 8
  ihdr[9] = 6
  ihdr[10] = 0
  ihdr[11] = 0
  ihdr[12] = 0
  return Buffer.concat([
    Buffer.from([137, 80, 78, 71, 13, 10, 26, 10]),
    writeChunk('IHDR', ihdr),
    writeChunk('IDAT', compressed),
    writeChunk('IEND', Buffer.alloc(0))
  ])
}

function setPixel(pixels, x, y, r, g, b, a = 255) {
  if (x < 0 || y < 0 || x >= SIZE || y >= SIZE) return
  const i = (y * SIZE + x) * 4
  pixels[i] = r
  pixels[i + 1] = g
  pixels[i + 2] = b
  pixels[i + 3] = a
}

function fillRect(pixels, x0, y0, w, h, r, g, b, a = 255) {
  for (let y = y0; y < y0 + h; y++) {
    for (let x = x0; x < x0 + w; x++) {
      setPixel(pixels, x, y, r, g, b, a)
    }
  }
}

function fillCircle(pixels, cx, cy, radius, r, g, b, a = 255) {
  for (let y = cy - radius; y <= cy + radius; y++) {
    for (let x = cx - radius; x <= cx + radius; x++) {
      const dx = x - cx
      const dy = y - cy
      if (dx * dx + dy * dy <= radius * radius) {
        setPixel(pixels, x, y, r, g, b, a)
      }
    }
  }
}

function drawHome(pixels, color) {
  const [r, g, b] = color
  fillRect(pixels, 30, 38, 22, 18, r, g, b)
  for (let y = 18; y <= 42; y++) {
    const t = (y - 18) / 24
    const half = Math.round(24 * (1 - t))
    for (let x = 40 - half; x <= 40 + half; x++) {
      setPixel(pixels, x, y, r, g, b)
    }
  }
  fillRect(pixels, 36, 46, 10, 12, 255, 255, 255, 220)
}

function drawWorkbench(pixels, color) {
  const [r, g, b] = color
  const cells = [
    [24, 24, 14, 14],
    [42, 24, 14, 14],
    [24, 42, 14, 14],
    [42, 42, 14, 14]
  ]
  cells.forEach(([x, y, w, h]) => fillRect(pixels, x, y, w, h, r, g, b, 255))
}

function drawProfile(pixels, color) {
  const [r, g, b] = color
  fillCircle(pixels, 40, 30, 10, r, g, b)
  fillRect(pixels, 26, 44, 28, 18, r, g, b)
  for (let y = 44; y < 62; y++) {
    const t = (y - 44) / 18
    const inset = Math.round(6 * t)
    for (let x = 26 + inset; x < 54 - inset; x++) {
      setPixel(pixels, x, y, r, g, b)
    }
  }
}

const ICONS = [
  { name: 'home', draw: drawHome },
  { name: 'workbench', draw: drawWorkbench },
  { name: 'profile', draw: drawProfile }
]

const COLOR_NORMAL = [138, 163, 178]
const COLOR_ACTIVE = [15, 118, 110]

fs.mkdirSync(OUT_DIR, { recursive: true })

ICONS.forEach(({ name, draw }) => {
  ;[
    { suffix: '', color: COLOR_NORMAL },
    { suffix: '-active', color: COLOR_ACTIVE }
  ].forEach(({ suffix, color }) => {
    const pixels = Buffer.alloc(SIZE * SIZE * 4, 0)
    draw(pixels, color)
    const file = path.join(OUT_DIR, `${name}${suffix}.png`)
    fs.writeFileSync(file, createPng(pixels))
    console.log('created', file)
  })
})

console.log('TabBar icons generated in static/tabbar/')
