/**
 * 整理 sql/sys_menu.sql：修复一二级目录、补全缺失菜单、映射无效图标到项目已有 SVG。
 */
const fs = require('fs')
const path = require('path')

const SQL_PATH = path.join(__dirname, '..', 'sql', 'sys_menu.sql')
const ICON_DIR = path.join(__dirname, '..', 'yuegongbao-ui', 'src', 'assets', 'icons', 'svg')

const VALID_ICONS = new Set(
  fs.readdirSync(ICON_DIR).filter((f) => f.endsWith('.svg')).map((f) => f.replace(/\.svg$/, ''))
)

/** 无效 icon → 项目内已有 svg 名称 */
const ICON_MAP = {
  operation: 'skill',
  service: 'guide',
  'data-analysis': 'chart',
  'data-board': 'chart',
  'data-line': 'chart',
  'office-building': 'build',
  'set-up': 'tool',
  'warning-filled': 'bug',
  'chat-line-square': 'message',
  present: 'star',
  tools: 'tool',
  connection: 'link',
  wallet: 'money',
  refresh: 'switch',
  picture: 'eye',
  tickets: 'post',
  promotion: 'star',
  collection: 'list',
  'folder-opened': 'documentation',
  reading: 'education',
  histogram: 'chart',
  suitcase: 'shopping',
  briefcase: 'job',
  umbrella: 'guide',
  'map-location': 'guide',
  cpu: 'component',
  'video-camera': 'monitor',
  box: 'zip',
  'first-aid-kit': 'bug',
  warning: 'bell',
  bicycle: 'guide',
  location: 'guide',
  check: 'checkbox',
  setting: 'tool',
  'trend-charts': 'chart',
  upload: 'upload', // ensure kept if exists
}

function escSql(v) {
  if (v === null || v === undefined) return 'NULL'
  if (typeof v === 'number') return String(v)
  return `'${String(v).replace(/'/g, "''")}'`
}

function parseInsert(line) {
  const m = line.match(/^INSERT INTO `sys_menu` VALUES \((.+)\);$/)
  if (!m) return null
  const parts = []
  let cur = ''
  let inStr = false
  for (let i = 0; i < m[1].length; i++) {
    const ch = m[1][i]
    if (ch === "'" && m[1][i - 1] !== '\\') {
      inStr = !inStr
      cur += ch
    } else if (ch === ',' && !inStr) {
      parts.push(cur.trim())
      cur = ''
    } else {
      cur += ch
    }
  }
  if (cur) parts.push(cur.trim())
  if (parts.length !== 21) throw new Error(`字段数异常 menu_id=${parts[0]}: ${parts.length}`)
  const val = (p) => {
    if (p === 'NULL') return null
    if (/^-?\d+$/.test(p)) return Number(p)
    return p.slice(1, -1).replace(/''/g, "'")
  }
  return {
    menu_id: val(parts[0]),
    menu_name: val(parts[1]),
    parent_id: val(parts[2]),
    order_num: val(parts[3]),
    path: val(parts[4]),
    component: val(parts[5]),
    query: val(parts[6]),
    route_name: val(parts[7]),
    is_frame: val(parts[8]),
    is_cache: val(parts[9]),
    menu_type: val(parts[10]),
    visible: val(parts[11]),
    status: val(parts[12]),
    portal_scope: val(parts[13]),
    perms: val(parts[14]),
    icon: val(parts[15]),
    create_by: val(parts[16]),
    create_time: val(parts[17]),
    update_by: val(parts[18]),
    update_time: val(parts[19]),
    remark: val(parts[20]),
  }
}

function formatInsert(r) {
  const cols = [
    r.menu_id, r.menu_name, r.parent_id, r.order_num, r.path, r.component, r.query, r.route_name,
    r.is_frame, r.is_cache, r.menu_type, r.visible, r.status, r.portal_scope, r.perms, r.icon,
    r.create_by, r.create_time, r.update_by, r.update_time, r.remark,
  ]
  return `INSERT INTO \`sys_menu\` VALUES (${cols.map(escSql).join(', ')});`
}

function fixIcon(icon, menuType) {
  if (!icon || icon === '#' || menuType === 'F') return icon || '#'
  if (VALID_ICONS.has(icon)) return icon
  const mapped = ICON_MAP[icon]
  if (mapped && VALID_ICONS.has(mapped)) return mapped
  return 'guide'
}

function applyRules(menus) {
  const byId = new Map(menus.map((m) => [m.menu_id, m]))

  const ensure = (row) => {
    if (!byId.has(row.menu_id)) {
      byId.set(row.menu_id, row)
      menus.push(row)
    } else {
      Object.assign(byId.get(row.menu_id), row)
    }
  }

  // 补全缺失的一级/二级菜单
  ensure({
    menu_id: 4301,
    menu_name: '运营总览',
    parent_id: 4300,
    order_num: 1,
    path: 'overview',
    component: 'ygb/operation/overview/index',
    query: '',
    route_name: 'YgbOperationOverview',
    is_frame: 1,
    is_cache: 0,
    menu_type: 'C',
    visible: '0',
    status: '0',
    portal_scope: 'ygb',
    perms: 'ygb:operation:overview',
    icon: 'dashboard',
    create_by: 'admin',
    create_time: '2026-06-12 11:28:31',
    update_by: 'admin',
    update_time: '2026-06-12 11:29:27',
    remark: '运营后台总览',
  })

  ensure({
    menu_id: 4302,
    menu_name: '企业入驻审核',
    parent_id: 4300,
    order_num: 2,
    path: 'enterpriseReview',
    component: 'ygb/operation/enterpriseReview/index',
    query: '',
    route_name: 'YgbOperationEnterpriseReview',
    is_frame: 1,
    is_cache: 0,
    menu_type: 'C',
    visible: '0',
    status: '0',
    portal_scope: 'ygb',
    perms: 'ygb:operationEnterpriseReview:list',
    icon: 'checkbox',
    create_by: 'admin',
    create_time: '2026-06-12 11:28:31',
    update_by: 'admin',
    update_time: '2026-06-12 11:29:27',
    remark: '企业入驻审核台账',
  })

  ensure({
    menu_id: 5885,
    menu_name: '信用评价',
    parent_id: 0,
    order_num: 14,
    path: 'ygb-credit',
    component: null,
    query: '',
    route_name: '',
    is_frame: 1,
    is_cache: 0,
    menu_type: 'M',
    visible: '0',
    status: '0',
    portal_scope: 'ygb',
    perms: '',
    icon: 'chart',
    create_by: 'admin',
    create_time: '2026-06-16 06:44:32',
    update_by: 'admin',
    update_time: '2026-06-16 06:44:32',
    remark: '粤工保信用评价目录（整合版）',
  })

  ensure({
    menu_id: 5827,
    menu_name: '订阅管理',
    parent_id: 3940,
    order_num: 4,
    path: 'aiReportSubscription',
    component: 'ygb/aiReportSubscription/index',
    query: '',
    route_name: 'YgbAiReportSubscription',
    is_frame: 1,
    is_cache: 0,
    menu_type: 'C',
    visible: '0',
    status: '0',
    portal_scope: 'ygb',
    perms: 'ygb:aiReportSubscription:list',
    icon: 'email',
    create_by: 'admin',
    create_time: '2026-06-16 06:44:32',
    update_by: 'admin',
    update_time: '2026-06-16 06:44:32',
    remark: 'YGB AI订阅管理',
  })

  const set = (id, patch) => {
    const m = byId.get(id)
    if (m) Object.assign(m, patch)
  }

  // 隐藏重复的旧信用评价目录，子菜单归并到 5885
  set(3980, { visible: '1', path: 'ygb-credit-legacy', remark: '已合并至5885信用评价目录（隐藏）' })
  set(3981, { parent_id: 5885, order_num: 1, portal_scope: 'ygb' })
  set(5880, { parent_id: 5885, order_num: 2 })
  set(5881, { parent_id: 5885, order_num: 3 })
  set(5882, { parent_id: 5885, order_num: 4 })

  // 门户/招聘归入运营后台
  set(4100, {
    parent_id: 4300,
    order_num: 5,
    menu_type: 'C',
    path: 'portalContent',
    remark: '粤工保官网CMS内容管理',
  })
  set(4200, {
    parent_id: 4300,
    order_num: 7,
    menu_type: 'C',
    path: 'workerJob',
    remark: '官网招聘市场岗位维护',
  })

  // 基础主数据：人员/单位子台账提升为二级
  ;[4490, 4491, 4492, 4493, 4494, 4495].forEach((id, i) => {
    set(id, { parent_id: 2000, order_num: 3 + i })
  })
  ;[4533, 4534, 4535, 4536].forEach((id, i) => {
    set(id, { parent_id: 2000, order_num: 9 + i })
  })

  // 设备子视图提升为技术防范二级
  ;[4526, 4527, 4528, 4529, 4530, 4531, 4532].forEach((id, i) => {
    set(id, { parent_id: 4040, order_num: 3 + i })
  })

  // AI 订阅按钮挂到正确父菜单
  ;[4022, 4023, 4024].forEach((id, i) => {
    set(id, { parent_id: 5827, order_num: i + 1 })
  })

  set(4014, { parent_id: 3940, order_num: 5 })
  set(5741, { parent_id: 3940, order_num: 1 })
  set(5742, { parent_id: 3940, order_num: 2, icon: 'tool' })

  // 运营后台二级排序
  const opOrder = {
    4301: 1,
    4302: 2,
    4303: 3,
    4304: 4,
    4100: 5,
    4305: 6,
    4200: 7,
    6072: 8,
    6073: 9,
    4306: 10,
    5801: 11,
    5802: 12,
    5803: 13,
    5804: 14,
    5805: 15,
    5971: 16,
    5972: 17,
    5973: 18,
    5974: 19,
    5975: 20,
    5976: 21,
    5977: 22,
  }
  Object.entries(opOrder).forEach(([id, order]) => set(Number(id), { parent_id: 4300, order_num: order }))

  // 粤工保一级目录排序
  const ygbTopOrder = {
    5: 5,
    2000: 10,
    2020: 11,
    2040: 12,
    2060: 13,
    2080: 14,
    3900: 15,
    3920: 16,
    3940: 17,
    3960: 18,
    5885: 19,
    4000: 20,
    4020: 21,
    4040: 22,
    6150: 23,
    6190: 24,
    4300: 30,
    4400: 31,
    6170: 32,
  }
  Object.entries(ygbTopOrder).forEach(([id, order]) => {
    const m = byId.get(Number(id))
    if (m && m.parent_id === 0 && m.portal_scope !== 'azb') set(Number(id), { order_num: order })
  })

  // 安责保一级目录排序
  const azbTopOrder = {
    4: 4,
    5600: 10,
    5610: 11,
    5620: 12,
    5630: 13,
    4920: 14,
    5640: 15,
    5650: 16,
    5660: 17,
    5670: 18,
  }
  Object.entries(azbTopOrder).forEach(([id, order]) => {
    const m = byId.get(Number(id))
    if (m && m.parent_id === 0 && m.portal_scope === 'azb') set(Number(id), { order_num: order })
  })

  set(4300, { icon: 'skill' })
  set(6190, { icon: 'guide' })
  set(4400, { remark: '平台治理目录' })
  set(4303, { remark: '岗位审核台账' })
  set(4304, { remark: '简历管理台账' })
  set(4305, { remark: '广告轮播内容管理' })
  set(4306, { remark: '运营消息台账' })
  set(2014, { remark: 'YGB 设备管理菜单' })

  // 图标修正（M/C 菜单）
  for (const m of byId.values()) {
    m.icon = fixIcon(m.icon, m.menu_type)
  }

  return [...byId.values()].sort((a, b) => a.menu_id - b.menu_id)
}

function main() {
  const raw = fs.readFileSync(SQL_PATH, 'utf8')
  const lines = raw.split(/\r?\n/)
  const head = []
  const tail = []
  const menus = []
  let inRecords = false
  let afterRecords = false

  for (const line of lines) {
    if (line.startsWith('INSERT INTO `sys_menu` VALUES')) {
      inRecords = true
      menus.push(parseInsert(line))
    } else if (inRecords && !afterRecords) {
      if (line.trim() === '' || line.startsWith('SET FOREIGN_KEY_CHECKS')) {
        afterRecords = true
        tail.push(line)
      } else {
        menus.push(parseInsert(line))
      }
    } else if (!inRecords) {
      head.push(line)
    } else {
      tail.push(line)
    }
  }

  // 修复 DDL 注释乱码
  for (let i = 0; i < head.length; i++) {
    if (head[i].includes('portal_scope') && head[i].includes('??????')) {
      head[i] = head[i].replace('??????(ygb/azb/both)', '门户范围(ygb/azb/both)')
    }
  }

  const fixed = applyRules(menus)
  const out = [
    ...head,
    ...fixed.map(formatInsert),
    ...tail.filter((l, idx) => idx > 0 || l.trim() !== ''),
  ].join('\n')
  fs.writeFileSync(SQL_PATH, out.endsWith('\n') ? out : out + '\n', 'utf8')

  const invalid = new Set()
  for (const m of fixed) {
    if (m.menu_type !== 'F' && m.icon && m.icon !== '#' && !VALID_ICONS.has(m.icon)) {
      invalid.add(m.icon)
    }
  }
  console.log(`已整理 ${fixed.length} 条菜单记录`)
  if (invalid.size) {
    console.warn('仍可能存在无效图标:', [...invalid].join(', '))
  } else {
    console.log('所有目录/菜单图标均已映射到项目 SVG')
  }
}

main()
