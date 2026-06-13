export function createHomeBlueprints(action) {
  return Object.freeze({
    ygb: {
      quickSections: [
        {
          key: 'process',
          title: '企业办理',
          desc: '先处理合同、考勤、工资和高处作业，减少跨页往返。',
          actions: [
            action('contract', '合同备案', '用工关系、协议材料与备案闭环。', '高频办理', '适合企业管理员、操作员'),
            action('attendanceRaw', '考勤上报', '原始考勤采集、校验和补录。', '日常录入', '适合工地负责人、操作员'),
            action('attendanceMonthly', '考勤归集', '月度归集结果直接衔接工资批次。', '月度归集', '适合企业管理员、财务'),
            action('salaryBatch', '工资批次', '工资发放、到账确认和回调闭环。', '财务主链', '适合财务经办'),
            action('heightWorkReport', '高处作业', '现场报备、凭证查看和作业完结。', '现场联动', '适合工地负责人')
          ]
        },
        {
          key: 'finance',
          title: '对账整改',
          desc: '把工资、社保、税务和异常整改放进同一条财务闭环。',
          actions: [
            action('salaryDetail', '工资明细', '逐人校验实发金额、失败原因和发放状态。', '明细校验', '适合财务经办'),
            action('socialPayment', '社保缴费', '同步来源、缴费状态和缴费回写联动。', '缴费回写', '适合财务经办'),
            action('socialBaseCompare', '社保基数比对', '识别工资实发与社保基数偏差。', '差异比对', '适合监管、财务'),
            action('taxCompare', '税务监管', '个税申报同步、收入比对和异常核对。', '税务联动', '适合监管、财务')
          ]
        },
        {
          key: 'regulation',
          title: '监管闭环',
          desc: '围绕预警、扩面和统计报表形成监管视角的闭环。',
          actions: [
            action('uninsuredList', '扩面减损', '漏保清单、处置进度和催缴闭环。', '整改闭环', '适合监管、企业管理员'),
            action('warning', '预警中心', '统一预警工单、处置日志和闭环时效。', '风险联动', '适合监管、企业管理员'),
            action('statReport', '社保税务联动月报', '按区域查看社保税务联动风险和治理结果。', '区域态势', '适合监管'),
            action('cockpit', '综合驾驶舱', '先看全局态势，再下钻具体模块。', '总览入口', '适合监管、管理层')
          ]
        }
      ],
      playbooks: [
        {
          key: 'enterprise-admin',
          title: '企业管理员',
          badge: '办理闭环',
          roles: ['ygb_enterprise_admin'],
          summary: '优先打通合同、考勤、工资、社保与高处作业，避免数据断点。',
          focusModules: ['contract', 'attendanceMonthly', 'salaryBatch', 'heightWorkReport'],
          steps: [
            '先核对合同备案和在岗人员是否完整。',
            '再归集考勤、生成工资批次并确认到账。',
            '最后回看社保税务异常和高处作业完结情况。'
          ]
        },
        {
          key: 'enterprise-operator',
          title: '操作员',
          badge: '连续录入',
          roles: ['ygb_enterprise_operator'],
          summary: '围绕录入、修正、提交和完结操作，保证台账持续可用。',
          focusModules: ['contract', 'attendanceRaw', 'heightWorkReport', 'warning'],
          steps: [
            '先补齐合同与考勤原始记录。',
            '再处理现场高处作业报备和结束登记。',
            '最后回到预警中心确认异常是否已回写。'
          ]
        },
        {
          key: 'finance',
          title: '财务经办',
          badge: '金额核对',
          matchModules: ['salaryBatch', 'salaryDetail', 'socialPayment', 'taxCompare'],
          summary: '先确认工资到账，再核对社保税务，再处理差异整改。',
          focusModules: ['salaryBatch', 'salaryDetail', 'socialPayment', 'taxCompare'],
          steps: [
            '先看工资批次是否需要生成明细、到账确认或提交发放。',
            '再逐人核对工资明细与失败原因。',
            '最后联动社保缴费、税务和基数比对异常。'
          ]
        },
        {
          key: 'hrss',
          title: '人社监管员',
          badge: '区域监管',
          roles: ['ygb_hrss_supervisor'],
          summary: '先看区域态势，再盯漏保和预警，最后下钻统计报表。',
          focusModules: ['cockpit', 'uninsuredList', 'warning', 'statReport'],
          steps: [
            '先通过驾驶舱判断本区域异常集中点。',
            '再锁定漏保企业和高频预警对象。',
            '最后通过统计报表验证治理成效。'
          ]
        },
        {
          key: 'ygb-default',
          title: '粤工保工作台',
          badge: '通用视角',
          fallback: true,
          summary: '当前门户以办理闭环和监管抽查为主，默认入口已按粤工保语义重排。',
          focusModules: ['contract', 'attendanceMonthly', 'warning', 'statReport'],
          steps: [
            '先进入当前最常用的办理模块。',
            '再处理预警和整改事项。',
            '最后回到统计和驾驶舱复核全局结果。'
          ]
        }
      ],
      focusPanels: [
        {
          title: '企业办理优先于说明展示',
          desc: '首页先把合同、考勤、工资和高处作业入口前置，不再让企业用户先读架构介绍。'
        },
        {
          title: '财务闭环单独成组',
          desc: '把工资批次、工资明细、社保缴费和税务比对集中呈现，减少财务跨模块跳转。'
        },
        {
          title: '监管入口保留全局视角',
          desc: '驾驶舱、预警中心、扩面减损和统计报表仍作为监管类角色的第一层入口。'
        }
      ]
    },
    azb: {
      quickSections: [
        {
          key: 'risk',
          title: '风险治理',
          desc: '先看总体风险，再看设备、预警和高危作业现场状态。',
          actions: [
            action('cockpit', '安全治理总览', '安责保首页级总览，先看区域态势和重点对象。', '总览入口', '适合监管、管理层'),
            action('warning', '预警中心', '统一处理设备、AI、专项治理和工伤预警。', '闭环时效', '适合监管、现场负责人'),
            action('device', '设备管理', '设备在线、锁机、授权和 AI 事件联动。', '现场设备', '适合设备管理员'),
            action('heightWorkReport', '高处作业', '报备、证书核验、作业结束和电子凭证。', '高危作业', '适合工地负责人')
          ]
        },
        {
          key: 'insurance',
          title: '保险协同',
          desc: '把投保覆盖、资金池和信用评价放在一条安责险协同链路里。',
          actions: [
            action('aqInsurance', '安责险管理', '投保覆盖、保单到期和同步回写。', '投保监管', '适合保险机构、监管'),
            action('preventionFund', '事故预防资金池', '计提、使用、余额和结算动作。', '资金使用', '适合保险机构'),
            action('creditScore', '企业信用评价', '按安全治理表现查看评分和等级。', '风险画像', '适合保险机构、银行'),
            action('statReport', '预警治理月报', '按区域查看治理月报、压降结果和预警闭环。', '区域报表', '适合监管、保险机构')
          ]
        },
        {
          key: 'objects',
          title: '治理对象',
          desc: '围绕企业、人员、事故与项目持续下钻，保持对象台账可追踪。',
          actions: [
            action('enterprise', '企业台账', '先锁定治理对象，再进入企业基础档案。', '对象归属', '适合监管、现场负责人'),
            action('person', '人员台账', '查看持证、在岗、参保和准入风险。', '现场准入', '适合现场负责人'),
            action('injuryEvent', '工伤事件', '跟踪认定进度、待遇申领和超期办结。', '事件处置', '适合监管、保险机构'),
            action('aiReport', 'AI监测报告', '查看高风险对象、排名和整改建议。', '模型输出', '适合监管、运营')
          ]
        }
      ],
      playbooks: [
        {
          key: 'emergency',
          title: '应急监管员',
          badge: '风险处置',
          roles: ['ygb_emergency_supervisor'],
          summary: '先看区域态势，再盯设备与高危作业，最后压实预警和整改闭环。',
          focusModules: ['cockpit', 'device', 'heightWorkReport', 'warning'],
          steps: [
            '先在驾驶舱锁定重点区域和重点企业。',
            '再查看设备在线、高处作业进行中和 AI 风险对象。',
            '最后回到预警中心追踪整改时效。'
          ]
        },
        {
          key: 'insurer',
          title: '保险机构',
          badge: '只读协同',
          roles: ['ygb_insurer'],
          summary: '围绕投保覆盖、资金池和信用评分形成安责险日常协同。',
          focusModules: ['aqInsurance', 'preventionFund', 'creditScore', 'statReport'],
          steps: [
            '先看投保覆盖和保单到期风险。',
            '再看事故预防资金余额和使用动作。',
            '最后结合信用评分与统计报表判断服务重点。'
          ]
        },
        {
          key: 'bank',
          title: '银行协同',
          badge: '只读看板',
          roles: ['ygb_bank'],
          summary: '当前以信用画像和区域报表为主，保持只读协同视角。',
          focusModules: ['creditScore', 'statReport'],
          steps: [
            '先看信用评分和区域分层情况。',
            '再查看区域报表中的重点企业变化。',
            '最后回到信用对象和报表明细做协同复核。'
          ]
        },
        {
          key: 'site-enterprise',
          title: '现场治理负责人',
          badge: '现场联动',
          roles: ['ygb_enterprise_admin', 'ygb_enterprise_operator'],
          matchModules: ['device', 'warning', 'heightWorkReport'],
          summary: '当前门户下，企业侧重点从办理转成现场治理、告警处置和作业闭环。',
          focusModules: ['warning', 'device', 'heightWorkReport', 'enterprise'],
          steps: [
            '先看设备状态和当前预警。',
            '再处理高处作业报备、凭证和结束登记。',
            '最后核对治理对象企业与人员信息。'
          ]
        },
        {
          key: 'azb-default',
          title: '安责保工作台',
          badge: '通用视角',
          fallback: true,
          summary: '当前门户以风险治理和现场处置为主，默认入口已按安责保语义重排。',
          focusModules: ['cockpit', 'warning', 'device', 'aqInsurance'],
          steps: [
            '先进入当前最紧急的风险治理入口。',
            '再处理设备、作业和预警闭环事项。',
            '最后回到投保与报表视角看整体覆盖情况。'
          ]
        }
      ],
      focusPanels: [
        {
          title: '首页先看风险，不先看台账',
          desc: '驾驶舱、预警中心、设备和高处作业被放到第一屏，符合应急侧高频使用顺序。'
        },
        {
          title: '保险协同保持只读聚焦',
          desc: '投保监管、资金池、信用评价和统计报表成组展示，减少保险机构进入复杂录入页。'
        },
        {
          title: '企业侧进入安责保后切换到现场视角',
          desc: '同一企业角色进入 `azb` 时不再先看到合同工资，而是先看到设备、预警和高危作业。'
        }
      ]
    }
  })
}

export function resolveHomeRoleProfileConfig({ portalCode, hasRole, isFinanceHomepageView }) {
  if (portalCode === 'azb') {
    if (hasRole('ygb_bank')) {
      return {
        workspaceTitle: '只读协同入口',
        workspaceHint: '首页先保留信用评价、区域报表和少量下钻入口，不把银行协同角色带进复杂治理页。',
        queueTitle: '银行协同重点对象',
        queueHint: '优先暴露信用红码对象和区域报表，不让只读角色先面对设备指令、维护和治理录入动作。',
        sectionOrder: ['insurance', 'objects', 'risk']
      }
    }

    if (hasRole('ygb_insurer')) {
      return {
        workspaceTitle: '保险协同入口',
        workspaceHint: '首页先聚焦保单、资金池、信用和报表，再按需要下钻风险治理对象。',
        queueTitle: '保险协同待复核对象',
        queueHint: '优先暴露风险保单、低余额资金池和红码企业，让保险协同先锁定保单与资金风险。',
        sectionOrder: ['insurance', 'objects', 'risk']
      }
    }

    return {
      workspaceTitle: '风险治理入口',
      workspaceHint: '首页先完成风险分流和对象锁定，再进入驾驶舱、设备、高处作业和预警页做纵深治理。',
      queueTitle: '风险对象与待办队列',
      queueHint: '按当前门户和角色权限聚合真实风险列表，把最该处置的对象直接放到首页，而不是只展示汇总数。',
      sectionOrder: ['risk', 'objects', 'insurance']
    }
  }

  if (hasRole('ygb_hrss_supervisor')) {
    return {
      workspaceTitle: '监管入口',
      workspaceHint: '首页先做区域异常分流和监管对象锁定，再进入驾驶舱、报表和具体台账做纵深复核。',
      queueTitle: '区域监管重点对象',
      queueHint: '优先暴露漏保、预警、社税异常和月报归档对象，让监管员先锁定区域异常再下钻具体企业。',
      sectionOrder: ['regulation', 'finance', 'process']
    }
  }

  if (hasRole('ygb_enterprise_operator')) {
    return {
      workspaceTitle: '经办录入入口',
      workspaceHint: '首页先收口录入、修正、结束登记和回写链路，不让操作员先落到图表和监管总览。',
      queueTitle: '经办待录入与待回写队列',
      queueHint: '优先暴露合同、考勤、高处作业和预警回写对象，让操作员先完成连续录入和结束登记。',
      sectionOrder: ['process', 'regulation', 'finance']
    }
  }

  if (isFinanceHomepageView()) {
    return {
      workspaceTitle: '财务整改入口',
      workspaceHint: '首页先聚焦工资、社保、税务和归档动作，再按需要进入驾驶舱做综合诊断。',
      queueTitle: '财务异常与对账队列',
      queueHint: '优先暴露工资失败、社保欠费、基数异常和个税差异对象，减少财务跨页筛选成本。',
      sectionOrder: ['finance', 'process', 'regulation']
    }
  }

  if (hasRole('ygb_enterprise_admin')) {
    return {
      workspaceTitle: '企业办理入口',
      workspaceHint: '首页先完成合同、考勤、工资、社税和高处作业的入口分流，再进入驾驶舱看纵深诊断。',
      queueTitle: '企业当前卡点与待办',
      queueHint: '优先暴露会阻断合同、考勤、工资、社税和高处作业闭环的对象，方便企业管理员直接统筹。',
      sectionOrder: ['process', 'finance', 'regulation']
    }
  }

  return {
    workspaceTitle: '高频模块入口',
    workspaceHint: '只显示当前门户可见且当前账号可进入的高频模块，首页优先做入口分流，驾驶舱负责纵深诊断。',
    queueTitle: '重点对象与待办队列',
    queueHint: '按当前门户和角色权限聚合真实列表，把最该处理的对象直接放到首页，而不是只展示汇总数。',
    sectionOrder: ['process', 'finance', 'regulation']
  }
}
