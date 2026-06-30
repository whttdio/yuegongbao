import {
  addBusinessRecord,
  delBusinessRecord,
  getBusinessRecord,
  getBusinessRecordSummary,
  listBusinessRecord,
  updateBusinessRecord
} from '@/api/ygb/businessRecord'

const moduleTitleMap = {
  aqInsuranceClaim: '安责险赔付率监管',
  creditRepair: '信用修复申请',
  creditRule: '信用评分规则配置',
  creditSanction: '联合惩戒推送',
  deviceGeofence: '电子围栏',
  deviceInstallOrder: '设备安装运维',
  deviceInspectPlan: '设备巡检计划',
  deviceRepairOrder: '设备维修工单',
  enterpriseHighRisk: '高危企业库管理',
  enterpriseRelation: '派遣/用工关联关系',
  enterpriseUnion: '工会管理',
  expansionSubsidy: '参保补贴管理',
  injuryAccidentWarning: '工伤事故预警',
  injuryEmployerMonitor: '用工单位工伤监测',
  injuryNewformHazardMonitor: '新业态伤害监测',
  injuryOccHazardMonitor: '职业病伤害监测',
  injuryPersonMonitor: '工伤人员监测',
  injuryRegionMonitor: '区域工伤监测',
  newformTraining: '新业态培训管理',
  occupationHealthArchive: '职业健康档案',
  occupationPrevention: '职业病预防项目',
  operationChipDispatch: '芯片调度管理',
  operationEnterpriseReview: '企业入驻审核',
  operationJobCategory: '职位分类管理',
  operationMaintenanceStats: '设备运维统计',
  operationMessage: '消息推送管理',
  operationRecruitStats: '招聘数据统计',
  personBlacklist: '黑名单管理',
  personCertificate: '特证管理',
  personExpert: '工伤预防专家库',
  personHighRiskPost: '高危岗位库',
  personRiskPost: '风险岗位库',
  personTraining: '人员培训监督',
  platformBackup: '数据备份与恢复',
  platformDocument: '文档管理',
  platformExchange: '接口与数据交换监管',
  platformSecurityAudit: '安全审计',
  preventionAi: '工伤预防 AI 管控',
  preventionPublicity: '工伤预防宣传',
  preventionTraining: '工伤预防培训',
  socialEnrollment: '参保率统计',
  socialSupplement: '社保补缴跟踪',
  specialRectification: '专项整治台账',
  taxFundFlow: '资金流穿透分析',
  taxInvoice: '发票比对',
  threeNaturePost: '三性岗位审核',
  unionLegalAid: '工会法律援助',
  unionNegotiation: '集体协商',
  unionOrg: '工会组织管理',
  unionPreventionSupervision: '工伤预防监督',
  unionSupervision: '工会监督'
}

const moduleCoreFeatureMap = {
  aqInsuranceClaim: ['赔付率统计', '异常赔付排查', '企业赔付风险跟踪', '赔付台账导出'],
  creditRepair: ['修复申请登记', '整改材料审核', '信用状态流转', '修复结果归档'],
  creditRule: ['评分指标维护', '权重与扣分规则配置', '规则启停管理', '规则变更留痕'],
  creditSanction: ['失信企业筛查', '联合惩戒推送', '反馈结果跟踪', '惩戒台账导出'],
  deviceGeofence: ['围栏范围登记', '越界告警记录', '轨迹线索回放', '处置结果跟踪'],
  deviceInstallOrder: ['安装工单派发', '上门安装记录', '验收状态流转', '运维结果归档'],
  deviceInspectPlan: ['巡检计划登记', '执行进度跟踪', '异常问题记录', '巡检结果导出'],
  deviceRepairOrder: ['维修工单登记', '故障处置跟踪', '恢复上线记录', '维修结果归档'],
  enterpriseHighRisk: ['高危企业入库', '风险等级维护', '监管责任人记录', '高危台账导出'],
  enterpriseRelation: ['派遣用工关系登记', '关联企业核验', '关系变更记录', '关联图谱台账'],
  enterpriseUnion: ['工会组织登记', '监督事项记录', '维权服务跟踪', '工会台账导出'],
  expansionSubsidy: ['补贴申请登记', '审核拨付跟踪', '补贴金额核验', '绩效结果归档'],
  injuryAccidentWarning: ['事故预警登记', '风险等级研判', '预警处置跟踪', '闭环结果归档'],
  injuryEmployerMonitor: ['用工单位工伤监测', '高发企业识别', '整改任务跟踪', '监测台账导出'],
  injuryNewformHazardMonitor: ['新业态伤害采集', '平台企业风险识别', '伤害趋势跟踪', '监测结果归档'],
  injuryOccHazardMonitor: ['职业病线索采集', '危害岗位识别', '复核处置跟踪', '监测台账导出'],
  injuryPersonMonitor: ['工伤人员监测', '企业风险关联', '处置进度跟踪', '监测结果导出'],
  injuryRegionMonitor: ['区域工伤统计', '高发区域识别', '治理任务跟踪', '区域台账导出'],
  newformTraining: ['专项培训计划', '人员学时跟踪', '考试结果记录', '培训成效归档'],
  occupationHealthArchive: ['体检档案登记', '异常复查提醒', '健康状态跟踪', '档案台账导出'],
  occupationPrevention: ['预防项目申报', '项目验收记录', '整改结果跟踪', '预防台账导出'],
  operationChipDispatch: ['芯片领用登记', '企业分配记录', '库存变动跟踪', '领用台账导出'],
  operationEnterpriseReview: ['企业入驻审核', '资质材料核验', '审核状态流转', '入驻结果归档'],
  operationJobCategory: ['职位分类维护', '薪资范围设置', '工作类型管理', '分类启停记录'],
  operationMaintenanceStats: ['安装统计汇总', '维修统计汇总', '巡检数据分析', '运维报表导出'],
  operationMessage: ['消息内容维护', '推送对象筛选', '发送状态跟踪', '推送结果归档'],
  operationRecruitStats: ['岗位发布统计', '简历投递统计', '匹配成功率分析', '招聘报表导出'],
  personBlacklist: ['黑名单登记', '失信原因记录', '解除申请审核', '黑名单台账导出'],
  personCertificate: ['证件信息登记', '到期续期提醒', '审核状态流转', '证件台账导出'],
  personExpert: ['专家资源登记', '专业领域维护', '服务记录跟踪', '专家库导出'],
  personHighRiskPost: ['高危岗位登记', '岗位风险等级维护', '人员匹配记录', '岗位台账导出'],
  personRiskPost: ['风险岗位登记', '风险因素维护', '整改建议记录', '岗位台账导出'],
  personTraining: ['培训计划登记', '人员学习进度', '考试结果跟踪', '培训台账导出'],
  platformBackup: ['备份任务登记', '恢复演练记录', '异常结果跟踪', '备份台账导出'],
  platformDocument: ['资料文档维护', '接口说明归档', '交付附件管理', '文档台账导出'],
  platformExchange: ['接口调用记录', '数据交换状态', '异常回写补偿', '交换台账导出'],
  platformSecurityAudit: ['操作日志审计', '安全事件登记', '整改结果跟踪', '审计台账导出'],
  preventionAi: ['AI 管控任务登记', '风险识别记录', '处置建议跟踪', '管控结果归档'],
  preventionPublicity: ['宣传内容维护', '发布渠道记录', '覆盖对象统计', '宣传成效归档'],
  preventionTraining: ['培训计划落地', '课程学时记录', '人员参训跟踪', '培训效果评估'],
  socialEnrollment: ['参保率统计', '企业参保排名', '低参保企业识别', '统计台账导出'],
  socialSupplement: ['断缴企业识别', '补缴整改跟踪', '补缴进度记录', '整改台账导出'],
  specialRectification: ['整治问题登记', '整改任务分派', '闭环结果跟踪', '整治台账导出'],
  taxFundFlow: ['资金流向登记', '空壳过账识别', '异常线索处置', '穿透台账导出'],
  taxInvoice: ['发票数据比对', '工资总额偏离分析', '异常企业核查', '比对结果导出'],
  threeNaturePost: ['三性岗位申报', '合规材料审核', '审核结果流转', '岗位台账导出'],
  unionLegalAid: ['法律援助登记', '服务对象维护', '办理进度跟踪', '援助结果归档'],
  unionNegotiation: ['协商事项登记', '参与单位记录', '协商进度跟踪', '结果协议归档'],
  unionOrg: ['工会组织维护', '成员信息登记', '组织状态管理', '组织台账导出'],
  unionPreventionSupervision: ['预防监督事项', '隐患问题登记', '整改进度跟踪', '监督结果归档'],
  unionSupervision: ['监督事项登记', '维权线索跟踪', '处置结果记录', '监督台账导出']
}

const moduleFieldLabelMap = {
  aqInsuranceClaim: { amount: '赔付金额', quantity: '赔付件数', contentText: '赔付异常说明', handleResult: '核查/处置结果', ownerName: '监管责任人', eventTime: '赔付发生时间', sourceLabel: '赔付数据来源' },
  creditRepair: { amount: '修复影响分值', quantity: '整改材料数', contentText: '修复申请说明', handleResult: '审核/修复结果', ownerName: '审核人', eventTime: '申请时间', sourceLabel: '信用来源' },
  creditRule: { amount: '规则权重', quantity: '扣分分值', contentText: '评分规则说明', handleResult: '规则启停/变更结果', ownerName: '维护人', eventTime: '生效时间', sourceLabel: '规则来源' },
  creditSanction: { amount: '惩戒影响分值', quantity: '推送部门数', contentText: '失信惩戒说明', handleResult: '反馈/解除结果', ownerName: '跟进人', eventTime: '推送时间', sourceLabel: '失信来源' },
  deviceGeofence: { amount: '围栏半径(米)', quantity: '关联设备数', contentText: '围栏范围说明', handleResult: '越界处置结果', ownerName: '运维责任人', eventTime: '生效时间', sourceLabel: '定位来源' },
  deviceInstallOrder: { amount: '安装费用', quantity: '安装设备数', contentText: '安装要求', handleResult: '安装/验收结果', ownerName: '安装负责人', eventTime: '预约安装时间', sourceLabel: '工单来源' },
  deviceInspectPlan: { amount: '巡检预算', quantity: '巡检设备数', contentText: '巡检计划内容', handleResult: '巡检发现与整改', ownerName: '巡检负责人', eventTime: '计划巡检时间', sourceLabel: '计划来源' },
  deviceRepairOrder: { amount: '维修费用', quantity: '维修设备数', contentText: '故障描述', handleResult: '维修结果', ownerName: '维修负责人', eventTime: '报修时间', sourceLabel: '报修来源' },
  enterpriseHighRisk: { amount: '风险评分', quantity: '隐患数量', contentText: '高危原因', handleResult: '监管处置结果', ownerName: '监管责任人', eventTime: '入库时间', sourceLabel: '风险来源' },
  enterpriseRelation: { amount: '关联用工规模', quantity: '关联人员数', contentText: '派遣/用工关系说明', handleResult: '核验结果', ownerName: '核验人', eventTime: '关系生效时间', sourceLabel: '关系来源' },
  enterpriseUnion: { amount: '服务经费', quantity: '覆盖职工数', contentText: '工会事项说明', handleResult: '服务/监督结果', ownerName: '工会负责人', eventTime: '办理时间', sourceLabel: '事项来源' },
  expansionSubsidy: { amount: '补贴金额', quantity: '补贴人数', contentText: '补贴申请说明', handleResult: '审核/拨付结果', ownerName: '审核人', eventTime: '申请时间', sourceLabel: '补贴来源' },
  injuryAccidentWarning: { amount: '预警指数', quantity: '关联事故数', contentText: '预警原因', handleResult: '预警处置结果', ownerName: '处置责任人', eventTime: '预警时间', sourceLabel: '预警来源' },
  injuryEmployerMonitor: { amount: '工伤发生率(%)', quantity: '工伤人数', contentText: '用工单位监测说明', handleResult: '整改跟踪结果', ownerName: '监管责任人', eventTime: '监测时间', sourceLabel: '监测来源' },
  injuryNewformHazardMonitor: { amount: '伤害发生率(%)', quantity: '伤害人数', contentText: '新业态伤害说明', handleResult: '风险处置结果', ownerName: '跟进人', eventTime: '监测时间', sourceLabel: '平台数据来源' },
  injuryOccHazardMonitor: { amount: '危害暴露人数', quantity: '疑似病例数', contentText: '职业病危害说明', handleResult: '复核/处置结果', ownerName: '跟进人', eventTime: '监测时间', sourceLabel: '线索来源' },
  injuryPersonMonitor: { amount: '工伤赔付金额', quantity: '复发/跟踪次数', contentText: '人员工伤情况', handleResult: '跟踪处置结果', ownerName: '跟进人', eventTime: '工伤发生时间', sourceLabel: '工伤数据来源' },
  injuryRegionMonitor: { amount: '区域工伤率(%)', quantity: '工伤事件数', contentText: '区域监测说明', handleResult: '治理结果', ownerName: '区域责任人', eventTime: '统计时间', sourceLabel: '统计来源' },
  newformTraining: { amount: '培训费用', quantity: '参训人数', contentText: '培训计划内容', handleResult: '考试/评估结果', ownerName: '培训负责人', eventTime: '培训时间', sourceLabel: '培训来源' },
  occupationHealthArchive: { amount: '体检费用', quantity: '异常指标数', contentText: '健康档案说明', handleResult: '复查/干预结果', ownerName: '档案责任人', eventTime: '体检时间', sourceLabel: '体检来源' },
  occupationPrevention: { amount: '项目预算', quantity: '覆盖岗位数', contentText: '职业病预防内容', handleResult: '验收/整改结果', ownerName: '项目负责人', eventTime: '项目周期时间', sourceLabel: '项目来源' },
  operationChipDispatch: { amount: '芯片成本', quantity: '芯片数量', contentText: '调度/分配说明', handleResult: '领用/回收结果', ownerName: '调度负责人', eventTime: '调度时间', sourceLabel: '库存来源' },
  operationEnterpriseReview: { amount: '注册资本', quantity: '材料数量', contentText: '入驻申请说明', handleResult: '审核结果', ownerName: '审核人', eventTime: '申请时间', sourceLabel: '申请来源' },
  operationJobCategory: { amount: '薪资参考值', quantity: '岗位数量', contentText: '职位分类说明', handleResult: '分类启停结果', ownerName: '维护人', eventTime: '更新时间', sourceLabel: '分类来源' },
  operationMaintenanceStats: { amount: '运维费用', quantity: '运维工单数', contentText: '运维统计说明', handleResult: '统计分析结论', ownerName: '统计人', eventTime: '统计时间', sourceLabel: '统计来源' },
  operationMessage: { amount: '预计触达人数', quantity: '推送对象数', contentText: '消息内容', handleResult: '发送结果', ownerName: '发送人', eventTime: '发送时间', sourceLabel: '消息来源' },
  operationRecruitStats: { amount: '平均薪资', quantity: '招聘岗位数', contentText: '招聘统计说明', handleResult: '统计分析结论', ownerName: '统计人', eventTime: '统计时间', sourceLabel: '招聘来源' },
  personBlacklist: { amount: '失信影响分值', quantity: '关联事项数', contentText: '黑名单原因', handleResult: '解除/处置结果', ownerName: '审核人', eventTime: '列入时间', sourceLabel: '失信来源' },
  personCertificate: { amount: '证件费用', quantity: '证件数量', contentText: '证件信息说明', handleResult: '审核/续期结果', ownerName: '审核人', eventTime: '到期时间', sourceLabel: '证件来源' },
  personExpert: { amount: '服务费用', quantity: '服务次数', contentText: '专家领域/资质说明', handleResult: '服务评价结果', ownerName: '联系人', eventTime: '入库时间', sourceLabel: '专家来源' },
  personHighRiskPost: { amount: '岗位风险评分', quantity: '在岗人数', contentText: '高危岗位说明', handleResult: '防控措施结果', ownerName: '岗位责任人', eventTime: '评估时间', sourceLabel: '岗位来源' },
  personRiskPost: { amount: '岗位风险评分', quantity: '在岗人数', contentText: '风险因素说明', handleResult: '整改建议结果', ownerName: '岗位责任人', eventTime: '评估时间', sourceLabel: '岗位来源' },
  personTraining: { amount: '培训费用', quantity: '参训人数', contentText: '培训内容', handleResult: '培训考核结果', ownerName: '培训负责人', eventTime: '培训时间', sourceLabel: '培训来源' },
  platformBackup: { amount: '备份容量(GB)', quantity: '备份文件数', contentText: '备份任务说明', handleResult: '备份/恢复结果', ownerName: '运维责任人', eventTime: '备份时间', sourceLabel: '数据来源' },
  platformDocument: { amount: '文件大小(MB)', quantity: '附件数量', contentText: '文档内容说明', handleResult: '归档/发布结果', ownerName: '文档维护人', eventTime: '归档时间', sourceLabel: '文档来源' },
  platformExchange: { amount: '交换数据量', quantity: '接口调用次数', contentText: '接口/数据交换说明', handleResult: '交换结果', ownerName: '接口负责人', eventTime: '交换时间', sourceLabel: '接口来源' },
  platformSecurityAudit: { amount: '风险评分', quantity: '异常次数', contentText: '安全审计说明', handleResult: '整改/处置结果', ownerName: '安全责任人', eventTime: '审计时间', sourceLabel: '审计来源' },
  preventionAi: { amount: 'AI风险评分', quantity: '识别隐患数', contentText: 'AI管控说明', handleResult: '处置建议/结果', ownerName: '管控责任人', eventTime: '识别时间', sourceLabel: 'AI来源' },
  preventionPublicity: { amount: '宣传费用', quantity: '覆盖人数', contentText: '宣传内容', handleResult: '宣传成效', ownerName: '宣传负责人', eventTime: '发布时间', sourceLabel: '宣传来源' },
  preventionTraining: { amount: '培训预算', quantity: '参训人数', contentText: '工伤预防培训内容', handleResult: '培训评估结果', ownerName: '培训负责人', eventTime: '培训时间', sourceLabel: '培训来源' },
  specialRectification: { amount: '整改预算', quantity: '问题数量', contentText: '整治问题说明', handleResult: '闭环整改结果', ownerName: '整改责任人', eventTime: '整改期限', sourceLabel: '问题来源' },
  taxFundFlow: { amount: '异常资金金额', quantity: '关联交易数', contentText: '资金流线索说明', handleResult: '穿透核查结果', ownerName: '核查人', eventTime: '交易时间', sourceLabel: '资金来源' },
  taxInvoice: { amount: '异常发票金额', quantity: '发票数量', contentText: '发票比对说明', handleResult: '核查处理结果', ownerName: '核查人', eventTime: '开票时间', sourceLabel: '税票来源' },
  threeNaturePost: { amount: '岗位薪资', quantity: '用工人数', contentText: '三性岗位申报说明', handleResult: '审核结果', ownerName: '审核人', eventTime: '申报时间', sourceLabel: '申报来源' },
  unionLegalAid: { amount: '援助金额', quantity: '服务人数', contentText: '法律援助诉求', handleResult: '办理结果', ownerName: '援助负责人', eventTime: '受理时间', sourceLabel: '诉求来源' },
  unionNegotiation: { amount: '协商金额', quantity: '参与人数', contentText: '协商事项说明', handleResult: '协商结果', ownerName: '协商负责人', eventTime: '协商时间', sourceLabel: '事项来源' },
  unionOrg: { amount: '工会经费', quantity: '成员人数', contentText: '组织建设说明', handleResult: '组织状态结果', ownerName: '工会负责人', eventTime: '成立/更新时间', sourceLabel: '组织来源' },
  unionPreventionSupervision: { amount: '隐患风险评分', quantity: '隐患数量', contentText: '预防监督事项说明', handleResult: '整改监督结果', ownerName: '监督责任人', eventTime: '监督时间', sourceLabel: '监督来源' },
  unionSupervision: { amount: '维权金额', quantity: '涉及人数', contentText: '监督/维权线索说明', handleResult: '处置结果', ownerName: '监督责任人', eventTime: '受理时间', sourceLabel: '线索来源' }
}

function isBrokenText(value) {
  return typeof value !== 'string' ||
    value.trim() === '' ||
    /\?{2,}/.test(value) ||
    /[锟介敓闁告瑧鎸ч柟鍝勨槈闁哄倻绮欑紒鈧€瑰憡瀵煎ù婊勫瀣偨闁轰線鎯勯柤鍗炵暆閻庣]/.test(value)
}

function createFieldPlaceholders(labels) {
  return Object.fromEntries(Object.entries(labels).map(([field, label]) => {
    return [field, `请输入${label}`]
  }))
}

export function createBusinessRecordPageConfig(options) {
  const {
    module,
    title,
    description,
    businessNameLabel = '业务名称',
    businessNamePlaceholder = '请输入业务名称',
    fieldLabels = {},
    fieldPlaceholders = {},
    coreFeatures,
    defaultBusinessType = module,
    defaultSourceLabel = '业务办理',
    filters = ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'businessName', 'workflowStatus', 'status']
  } = options

  const permPrefix = `ygb:${module}`
  const resolvedTitle = isBrokenText(title) ? (moduleTitleMap[module] || module) : title
  const resolvedDescription = isBrokenText(description)
    ? `围绕${resolvedTitle}开展查询、办理、处置跟踪和台账导出，支撑业务闭环管理。`
    : description
  const resolvedBusinessNameLabel = isBrokenText(businessNameLabel) ? '业务名称' : businessNameLabel
  const resolvedBusinessNamePlaceholder = isBrokenText(businessNamePlaceholder) ? `请输入${resolvedTitle}名称` : businessNamePlaceholder
  const resolvedDefaultSourceLabel = isBrokenText(defaultSourceLabel) ? resolvedTitle : defaultSourceLabel
  const resolvedCoreFeatures = Array.isArray(coreFeatures) && coreFeatures.length
    ? coreFeatures
    : (moduleCoreFeatureMap[module] || ['查询筛选', '办理记录维护', '处置结果跟踪', '台账导出'])
  const resolvedFieldLabels = {
    ...(moduleFieldLabelMap[module] || {}),
    ...fieldLabels
  }
  const resolvedFieldPlaceholders = {
    ...createFieldPlaceholders(resolvedFieldLabels),
    ...fieldPlaceholders
  }

  return {
    title: resolvedTitle,
    description: resolvedDescription,
    module,
    permPrefix,
    filePrefix: `business_${module}`,
    defaultBusinessType,
    defaultSourceLabel: resolvedDefaultSourceLabel,
    businessNameLabel: resolvedBusinessNameLabel,
    businessNamePlaceholder: resolvedBusinessNamePlaceholder,
    fieldLabels: resolvedFieldLabels,
    fieldPlaceholders: resolvedFieldPlaceholders,
    coreFeatures: resolvedCoreFeatures,
    filters,
    routeQueryFields: filters,
    listApi: query => listBusinessRecord(module, query),
    summaryApi: query => getBusinessRecordSummary(module, query),
    detailApi: id => getBusinessRecord(module, id),
    addApi: data => addBusinessRecord(module, data),
    updateApi: data => updateBusinessRecord(module, data),
    deleteApi: ids => delBusinessRecord(module, ids),
    exportUrl: `ygb/${module}/export`
  }
}
