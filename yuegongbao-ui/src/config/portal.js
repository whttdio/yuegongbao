export const PORTAL_CODES = Object.freeze({
  ygb: 'ygb',
  azb: 'azb'
})

export const PORTAL_STORAGE_KEY = 'ygb-active-portal'

export const PORTAL_CONFIGS = Object.freeze({
  [PORTAL_CODES.ygb]: {
    code: PORTAL_CODES.ygb,
    appTitle: '粤工保管理平台',
    shortTitle: '粤工保',
    navSubtitle: '人社监管后台',
    footerContent: 'Copyright 2024-2026 粤工保管理平台 All Rights Reserved.',
    themeColor: '#0F5EA8',
    themeSurface: '#EAF3FF',
    themeAccent: '#D6E8FF',
    login: {
      badge: '粤工保统一监管入口',
      summary: '面向人社监管、企业管理员、工地负责人和财务经办的一体化业务工作台，覆盖合同、考勤、工资、社保、工伤预防和扩面减损。',
      features: [
        '合同、考勤、工资、社保、个税、工伤数据统一归集',
        '企业管理员、项目负责人、财务经办按角色进入各自工作台',
        '监管预警、AI监测、高处作业报备共用一套后台能力'
      ],
      helperTitle: '监管重点',
      helperItems: [
        '劳务派遣全链条合规',
        '工伤预防与参保扩面',
        '企业用工风险预警闭环'
      ]
    },
    workbench: {
      eyebrow: '粤工保监管工作台',
      title: '人社侧综合治理总览',
      description: '围绕合同、考勤、工资、社保、工伤预防和扩面减损组织企业高频业务，让企业填报、监管抽查和风险处置在同一套后台闭环。',
      versionTag: '双前端共享后台架构',
      phaseLabel: '当前定位',
      phaseValue: '粤工保主工作台',
      phaseDescription: '承担人社侧 80% 以上核心业务，作为共享后台的主承载前端，后续与安责保前端并行演进。',
      overview: [
        { topline: '核心监管', value: '6 大域', label: '合同/考勤/工资/社保/工伤/扩面', color: '#0F5EA8' },
        { topline: '企业高频', value: '4 类角色', label: '管理员/负责人/操作员/财务', color: '#227A52' },
        { topline: '风险联动', value: '1 套预警中心', label: '与安责保共用预警与AI底座', color: '#D97706' },
        { topline: '新模块', value: '高处作业', label: 'PC占位版已纳入技术防范域', color: '#7C3AED' }
      ],
      modules: [
        { name: '劳动用工合规', desc: '合同备案、考勤归集、工资发放、个税与社保比对闭环', phase: '粤工保专属', tagType: 'primary' },
        { name: '工伤预防', desc: '工伤认定、发生率分析、高危岗位覆盖率、基金效益看板', phase: '粤工保主责', tagType: 'success' },
        { name: '扩面减损', desc: '漏保企业、漏保人员、催缴闭环与参保扩面成效跟踪', phase: '粤工保主责', tagType: 'warning' },
        { name: '技术防范', desc: '与安责保共享 AI 识别、预警中心和高处作业申报报备底座', phase: '双前端共后台', tagType: 'info' }
      ],
      stack: [
        { kicker: '前端壳层', title: '粤工保品牌壳', items: ['粤工保登录页', '粤工保首页', '人社侧菜单树', '粤工保驾驶舱'] },
        { kicker: '共享核心', title: '统一后台能力', items: ['统一接口', '统一权限', '统一字典', '统一预警与AI底座'] }
      ],
      tasks: [
        '企业管理员默认进入合同、考勤、工资、社保、高处作业等高频办理入口。',
        '工地负责人强调现场作业、人员到岗、AI预警和高危作业报备闭环。',
        '财务经办聚焦工资批次、社保缴费、个税碰撞和漏保整改任务。',
        '监管部门通过驾驶舱、统计报表和预警中心追踪区域企业合规情况。'
      ],
      policies: [
        { title: '前台按人社语义组织', desc: '首页、菜单、报表、指标解释全部按人社监管认知排序。' },
        { title: '共性数据只做一次', desc: '企业、人员、预警、AI、设备底座与安责保共享，不复制数据。' },
        { title: '差异业务独立前端承载', desc: '合同、工资、社保、扩面减损只在粤工保前端完整呈现。' }
      ]
    }
  },
  [PORTAL_CODES.azb]: {
    code: PORTAL_CODES.azb,
    appTitle: '安责保管理平台',
    shortTitle: '安责保',
    navSubtitle: '应急监管后台',
    footerContent: 'Copyright 2024-2026 安责保管理平台 All Rights Reserved.',
    themeColor: '#0B6B78',
    themeSurface: '#E6F6F7',
    themeAccent: '#CBEAEC',
    login: {
      badge: '安责保统一监管入口',
      summary: '面向应急监管、保险机构、设备管理和企业现场安全治理的一体化业务工作台，覆盖安责险、设备赋码、电子围栏、技术防范和隐患整改。',
      features: [
        '安责险、设备、AI 识别、预警处置与隐患整改统一组织',
        '应急监管员、保险公司、企业侧角色使用同一后台能力分层展示',
        '与粤工保共享企业、人员、预警和高处作业数据底座'
      ],
      helperTitle: '治理重点',
      helperItems: [
        '安责险合规与资金池',
        '设备赋码在线与远程锁机',
        '高危现场隐患整改闭环'
      ]
    },
    workbench: {
      eyebrow: '安责保监管工作台',
      title: '应急侧安全治理总览',
      description: '围绕安责险、设备安全、技术防范和隐患整改组织监管工作，让现场风险识别、工单处置和企业整改在同一条链路里闭环。',
      versionTag: '双前端共享后台架构',
      phaseLabel: '当前定位',
      phaseValue: '安责保独立工作台',
      phaseDescription: '基于共享后台衍生的第二前端，强调应急部门、保险机构和企业现场安全治理的独立入口与指标口径。',
      overview: [
        { topline: '核心治理', value: '5 大域', label: '安责险/设备/技术防范/预警/整改', color: '#0B6B78' },
        { topline: '现场安全', value: '设备+AI', label: '赋码、围栏、锁机、识别联动', color: '#1D7A46' },
        { topline: '共性底座', value: '1 套后台', label: '企业、人员、预警与高处作业共享', color: '#B45309' },
        { topline: '品牌口径', value: '应急侧', label: '驾驶舱与报表按安责保评分体系展示', color: '#0E7490' }
      ],
      modules: [
        { name: '安责险管理', desc: '投保率、保额达标、服务委托、事故预防服务费全流程监管', phase: '安责保专属', tagType: 'primary' },
        { name: '设备安全', desc: '加芯赋码、在线率、离线时长、远程锁机、电子围栏联动', phase: '安责保主责', tagType: 'success' },
        { name: '隐患整改', desc: 'AI 识别隐患、重大隐患、逾期整改和重复隐患闭环追踪', phase: '安责保主责', tagType: 'warning' },
        { name: '技术防范与高处作业', desc: '与粤工保共用 AI/预警/高处作业底座，但按应急侧口径展示', phase: '双前端共后台', tagType: 'info' }
      ],
      stack: [
        { kicker: '前端壳层', title: '安责保品牌壳', items: ['安责保登录页', '安责保首页', '应急侧菜单树', '安责保驾驶舱'] },
        { kicker: '共享核心', title: '统一后台能力', items: ['统一接口', '统一权限', '统一预警', '统一企业与人员主数据'] }
      ],
      tasks: [
        '应急监管员默认看到设备、技术防范、高处作业、隐患整改和安责险核心入口。',
        '保险公司侧只暴露安责险、信用评价、统计报表等只读视图。',
        '企业侧在安责保前端以现场治理任务、告警处置和整改工单为主。',
        '驾驶舱和评分报告按安责保评分体系展示，避免与粤工保口径混淆。'
      ],
      policies: [
        { title: '前台按应急语义组织', desc: '首页、菜单、报表和评分口径全部按应急监管认知排序。' },
        { title: '共性能力不重复建设', desc: '企业、人员、预警、高处作业和AI识别沿用共享后台数据。' },
        { title: '专属业务独立承载', desc: '安责险、设备安全、隐患整改只在安责保前端完整呈现。' }
      ]
    }
  }
})
