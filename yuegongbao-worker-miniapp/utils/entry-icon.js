const ENTRY_ICON_PRESETS = {
  attendance: { glyph: '勤', tone: 'teal' },
  salary: { glyph: '薪', tone: 'gold' },
  social: { glyph: '保', tone: 'blue' },
  tax: { glyph: '税', tone: 'violet' },
  training: { glyph: '训', tone: 'green' },
  camera: { glyph: '拍', tone: 'cyan' },
  job: { glyph: '职', tone: 'orange' },
  legal: { glyph: '法', tone: 'indigo' },
  complaint: { glyph: '诉', tone: 'rose' },
  resume: { glyph: '历', tone: 'blue' },
  contract: { glyph: '合', tone: 'teal' },
  security: { glyph: '险', tone: 'green' },
  union: { glyph: '会', tone: 'gold' },
  points: { glyph: '积', tone: 'orange' },
  help: { glyph: '助', tone: 'cyan' },
  settings: { glyph: '设', tone: 'slate' },
  notice: { glyph: '知', tone: 'blue' },
  video: { glyph: '课', tone: 'violet' },
  activity: { glyph: '惠', tone: 'rose' },
  profile: { glyph: '我', tone: 'teal' },
  lecture: { glyph: '讲', tone: 'indigo' },
  ai: { glyph: 'AI', tone: 'violet' },
  apply: { glyph: '投', tone: 'blue' },
  default: { glyph: '务', tone: 'teal' }
}

const LABEL_PRESET_KEYS = {
  考勤: 'attendance',
  工资: 'salary',
  社保: 'social',
  个税: 'tax',
  培训: 'training',
  拍照: 'camera',
  找工作: 'job',
  法律咨询: 'legal',
  投诉举报: 'complaint',
  我的简历: 'resume',
  我的合同: 'contract',
  保险保障: 'security',
  工会服务: 'union',
  积分商城: 'points',
  帮助中心: 'help',
  设置: 'settings',
  消息中心: 'notice',
  重要通知: 'notice',
  上传归档: 'camera',
  实名认证: 'security',
  工伤预防视频: 'video',
  福利活动: 'activity',
  集体合同查阅: 'contract',
  维权咨询: 'legal',
  合同查阅: 'contract',
  公益讲座: 'lecture',
  AI培训: 'ai',
  预防视频: 'video',
  个人中心: 'profile',
  投递记录: 'apply'
}

const KEY_PRESET_KEYS = {
  attendance: 'attendance',
  checkin: 'attendance',
  salary: 'salary',
  social: 'social',
  tax: 'tax',
  training: 'training',
  camera: 'camera',
  job: 'job',
  legal: 'legal',
  complaint: 'complaint',
  resume: 'resume',
  contract: 'contract',
  laborContract: 'contract',
  security: 'security',
  union: 'union',
  points: 'points',
  help: 'help',
  settings: 'settings',
  notice: 'notice',
  profile: 'profile',
  lecture: 'lecture',
  aitraining: 'ai',
  video: 'video',
  activity: 'activity',
  apply: 'apply'
}

export function resolveEntryIcon(item = {}) {
  const label = String(item.label || '').trim()
  const key = String(item.key || '').trim().replace(/[-_]/g, '').toLowerCase()

  const presetKey =
    LABEL_PRESET_KEYS[label] ||
    KEY_PRESET_KEYS[key] ||
    Object.entries(KEY_PRESET_KEYS).find(([part]) => key.includes(part))?.[1] ||
    'default'

  const preset = ENTRY_ICON_PRESETS[presetKey] || ENTRY_ICON_PRESETS.default
  const glyph = preset.glyph || label.charAt(0) || ENTRY_ICON_PRESETS.default.glyph

  return {
    glyph,
    tone: preset.tone || 'teal',
    compact: glyph.length > 1
  }
}
