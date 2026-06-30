-- Phase56: PC menu final alignment with the 2026-06-26 document.
-- Goal:
--   1. Show only the document-defined PC first-level and second-level menu tree for the YGB PC portal.
--   2. Keep every displayed menu icon backed by an existing local SVG icon.
--   3. Route every second-level menu to an existing frontend component so frontend/backend routing stays usable.
--   4. Hide legacy root menu shells without deleting business permissions or tables.

set names utf8mb4;

set @phase56_sys_menu_portal_exists := (
  select count(1)
  from information_schema.columns
  where table_schema = database()
    and table_name = 'sys_menu'
    and column_name = 'portal_scope'
);
set @phase56_sys_menu_portal_sql := if(
  @phase56_sys_menu_portal_exists = 0,
  "alter table sys_menu add column portal_scope varchar(16) not null default 'both' comment '门户范围(ygb/azb/both)' after status",
  "select 1"
);
prepare stmt_phase56_sys_menu_portal from @phase56_sys_menu_portal_sql;
execute stmt_phase56_sys_menu_portal;
deallocate prepare stmt_phase56_sys_menu_portal;

set @phase56_role_menu_portal_exists := (
  select count(1)
  from information_schema.columns
  where table_schema = database()
    and table_name = 'sys_role_menu'
    and column_name = 'portal_scope'
);
set @phase56_role_menu_portal_sql := if(
  @phase56_role_menu_portal_exists = 0,
  "alter table sys_role_menu add column portal_scope varchar(16) not null default 'both' comment '角色菜单授权前端范围(ygb/azb/both)' after menu_id",
  "select 1"
);
prepare stmt_phase56_role_menu_portal from @phase56_role_menu_portal_sql;
execute stmt_phase56_role_menu_portal;
deallocate prepare stmt_phase56_role_menu_portal;

set @phase56_role_allowed_portal_exists := (
  select count(1)
  from information_schema.columns
  where table_schema = database()
    and table_name = 'sys_role'
    and column_name = 'allowed_portal_scope'
);
set @phase56_role_allowed_portal_sql := if(
  @phase56_role_allowed_portal_exists = 0,
  "alter table sys_role add column allowed_portal_scope varchar(16) not null default 'both' comment '允许前端范围(ygb/azb/both)' after role_sort",
  "select 1"
);
prepare stmt_phase56_role_allowed_portal from @phase56_role_allowed_portal_sql;
execute stmt_phase56_role_allowed_portal;
deallocate prepare stmt_phase56_role_allowed_portal;

create table if not exists sys_menu_bak_phase56_pc_document_final like sys_menu;
create table if not exists sys_role_menu_bak_phase56_pc_document_final like sys_role_menu;

insert ignore into sys_menu_bak_phase56_pc_document_final
select *
from sys_menu
where menu_type in ('M', 'C')
  and (
    parent_id = 0
    or menu_id between 9205601 and 9205844
    or parent_id between 9205601 and 9205623
    or menu_id in (7001,7002,7003,7004,7005,7006,7007,7008,7009,7010,7011,7012,7013,7014,7015,7016,7017,7018,7019,7020,7021,7022,7023,9105300)
  );

insert ignore into sys_role_menu_bak_phase56_pc_document_final
select rm.*
from sys_role_menu rm
join sys_menu m on m.menu_id = rm.menu_id
where m.menu_type in ('M', 'C')
  and (
    m.parent_id = 0
    or m.menu_id between 9205601 and 9205844
    or m.parent_id between 9205601 and 9205623
    or m.menu_id in (7001,7002,7003,7004,7005,7006,7007,7008,7009,7010,7011,7012,7013,7014,7015,7016,7017,7018,7019,7020,7021,7022,7023,9105300)
  );

drop temporary table if exists tmp_phase56_doc_menu;
create temporary table tmp_phase56_doc_menu (
  menu_id bigint not null primary key,
  menu_name varchar(80) not null,
  parent_id bigint not null,
  order_num int not null,
  path varchar(200) not null,
  component varchar(255) null,
  route_query varchar(255) not null default '',
  route_name varchar(120) not null,
  menu_type char(1) not null,
  perms varchar(100) not null default '',
  icon varchar(64) not null,
  remark varchar(255) not null
);

drop temporary table if exists tmp_phase56_doc_menu_button;
create temporary table tmp_phase56_doc_menu_button (
  menu_id bigint not null primary key,
  menu_name varchar(64) not null,
  parent_id bigint not null,
  order_num int not null,
  perms varchar(128) not null,
  icon varchar(64) not null default '#'
);

insert into tmp_phase56_doc_menu
  (menu_id, menu_name, parent_id, order_num, path, component, route_query, route_name, menu_type, perms, icon, remark)
values
  (9205601,'驾驶舱',0,1,'cockpit',null,'','YgbDocCockpit','M','','dashboard','文档一级模块'),
  (9205602,'预警中心',0,2,'warning-center',null,'','YgbDocWarningCenter','M','','bell','文档一级模块'),
  (9205603,'AI监测报告',0,3,'ai-report',null,'','YgbDocAiReport','M','','chart','文档一级模块'),
  (9205604,'合同备案',0,4,'contract-filing',null,'','YgbDocContractFiling','M','','documentation','文档一级模块'),
  (9205605,'用工考勤',0,5,'attendance',null,'','YgbDocAttendance','M','','time','文档一级模块'),
  (9205606,'工资监管',0,6,'salary-supervision',null,'','YgbDocSalary','M','','money','文档一级模块'),
  (9205607,'社保监管',0,7,'social-insurance',null,'','YgbDocSocialInsurance','M','','peoples','文档一级模块'),
  (9205608,'工伤监管',0,8,'injury-supervision',null,'','YgbDocInjury','M','','skill','文档一级模块'),
  (9205609,'专项治理',0,9,'special-rectification',null,'','YgbDocSpecialRectification','M','','guide','文档一级模块'),
  (9205610,'税务监管',0,10,'tax-supervision',null,'','YgbDocTax','M','','excel','文档一级模块'),
  (9205611,'安责险管理',0,11,'aq-insurance',null,'','YgbDocAqInsurance','M','','skill','文档一级模块'),
  (9205612,'设备管理',0,12,'device-management',null,'','YgbDocDevice','M','','build','文档一级模块'),
  (9205613,'扩面减损',0,13,'expansion-reduction',null,'','YgbDocExpansion','M','','chart','文档一级模块'),
  (9205614,'信用评价',0,14,'credit-evaluation',null,'','YgbDocCredit','M','','star','文档一级模块'),
  (9205615,'统计报表',0,15,'statistical-report',null,'','YgbDocStatReport','M','','table','文档一级模块'),
  (9205616,'人员管理',0,16,'personnel-management',null,'','YgbDocPerson','M','','user','文档一级模块'),
  (9205617,'单位管理',0,17,'enterprise-management',null,'','YgbDocEnterprise','M','','people','文档一级模块'),
  (9205618,'系统管理',0,18,'system-management',null,'','YgbDocSystem','M','','system','文档一级模块'),
  (9205619,'运营后台',0,19,'operation-backend',null,'','YgbDocOperation','M','','shopping','文档一级模块'),
  (9205620,'企业后台',0,20,'enterprise-portal',null,'','YgbDocEnterprisePortal','M','','people','文档一级模块'),
  (9205621,'便民服务',0,21,'citizen-service',null,'','YgbDocCitizenService','M','','link','文档一级模块'),
  (9205622,'新业态监管',0,22,'newform-regulation',null,'','YgbDocNewform','M','','link','文档一级模块'),
  (9205623,'职业病监管',0,23,'occupational-disease',null,'','YgbDocOccupation','M','','skill','文档一级模块'),

  (9205701,'指标总览',9205601,1,'overview','ygb/cockpit/index','','YgbDocCockpitOverview','C','ygb:cockpit:list','dashboard','文档二级模块：核心用工及风险指标统计'),
  (9205702,'地图可视化',9205601,2,'map','ygb/cockpit/map/index','','YgbDocCockpitMap','C','ygb:cockpit:list','monitor','文档二级模块：企业风险热力图与设备分布'),
  (9205703,'实时预警流',9205601,3,'warningStream','ygb/cockpit/warningStream/index','','YgbDocCockpitWarningStream','C','ygb:cockpit:list','bell','文档二级模块：滚动预警状态展示'),
  (9205704,'红黄绿码企业分类',9205601,4,'enterpriseCode','ygb/cockpit/enterpriseCode/index','','YgbDocCockpitEnterpriseCode','C','ygb:cockpit:list','chart','文档二级模块：企业风险分级排名'),
  (9205705,'趋势分析',9205601,5,'trend','ygb/cockpitTrend/index','','YgbDocCockpitTrend','C','ygb:cockpit:list','chart','文档二级模块：工伤/参保/预警趋势分析'),
  (9205706,'驾驶舱配置',9205601,6,'config','ygb/cockpitConfig/index','','YgbDocCockpitConfig','C','ygb:cockpitConfig:list','system','文档二级模块：用户自定义看板配置'),

  (9205707,'预警工单管理',9205602,1,'workOrder','ygb/warning/index','','YgbDocWarningWorkOrder','C','ygb:warning:list','bell','文档二级模块：签收/派发/办结'),
  (9205708,'预警规则配置',9205602,2,'rule','ygb/warningRule/index','','YgbDocWarningRule','C','ygb:warningRule:list','system','文档二级模块：级别/条件/推送规则配置'),
  (9205709,'预警统计分析',9205602,3,'stat','ygb/warningStat/index','','YgbDocWarningStat','C','ygb:warning:list','chart','文档二级模块：级别/区域/处置时效统计'),

  (9205710,'监测报告生成',9205603,1,'report','ygb/aiReport/index','','YgbDocAiReportGenerate','C','ygb:aiReport:list','chart','文档二级模块：日周月年报生成'),
  (9205711,'风险评分与排名',9205603,2,'ranking','ygb/aiReport/ranking/index','','YgbDocAiReportRanking','C','ygb:aiReport:list','star','文档二级模块：0-100分评分及区域行业排名'),
  (9205712,'多维筛选与穿透',9205603,3,'filter','ygb/aiReport/filter/index','','YgbDocAiReportFilter','C','ygb:aiReport:list','search','文档二级模块：多维度筛选穿透'),
  (9205713,'监测建议与任务',9205603,4,'task','ygb/aiReportTask/index','','YgbDocAiReportTask','C','ygb:aiReportTask:list','list','文档二级模块：高风险企业管控建议'),
  (9205714,'报告导出与订阅',9205603,5,'subscription','ygb/aiReportSubscription/index','','YgbDocAiReportSubscription','C','ygb:aiReportSubscription:list','download','文档二级模块：导出与定时订阅'),
  (9205715,'评分模型管理',9205603,6,'model','ygb/aiReportConfig/index','','YgbDocAiReportModel','C','ygb:aiReportConfig:list','system','文档二级模块：评分权重及模型配置'),

  (9205716,'合同列表',9205604,1,'contract','ygb/contract/index','','YgbDocContractList','C','ygb:contract:list','documentation','文档二级模块：劳动/派遣协议查询筛选'),
  (9205717,'合同详情',9205604,2,'detail','ygb/contract/index','','YgbDocContractDetail','C','ygb:contract:list','documentation','文档二级模块：区块链存证合同查看'),
  (9205718,'合同到期提醒',9205604,3,'expiry','ygb/contract/expiry/index','','YgbDocContractExpiry','C','ygb:contract:list','time','文档二级模块：30日合同到期批量提醒'),
  (9205719,'未备案合同预警',9205604,4,'unfiled','ygb/contract/unfiled/index','','YgbDocContractUnfiled','C','ygb:contract:list','bell','文档二级模块：未备案企业人员预警'),
  (9205720,'合同模板库',9205604,5,'template','ygb/contractTemplate/index','','YgbDocContractTemplate','C','ygb:contractTemplate:list','documentation','文档二级模块：标准合同模板上传审核管理'),

  (9205721,'考勤总览',9205605,1,'overview','ygb/attendance/overview/index','','YgbDocAttendanceOverview','C','ygb:attendanceRaw:list','time','文档二级模块：设备状态及实时打卡监控'),
  (9205722,'异常考勤统计',9205605,2,'abnormal','ygb/attendance/abnormal/index','','YgbDocAttendanceAbnormal','C','ygb:attendanceRaw:list','bell','文档二级模块：迟到/早退/缺勤统计'),
  (9205723,'考勤明细查询',9205605,3,'detail','ygb/attendanceRaw/index','','YgbDocAttendanceDetail','C','ygb:attendanceRaw:list','list','文档二级模块：多维度考勤明细检索'),
  (9205724,'设备在线率报表',9205605,4,'deviceOnline','ygb/attendance/deviceOnline/index','','YgbDocAttendanceDeviceOnline','C','ygb:attendanceRaw:list','monitor','文档二级模块：区域设备在线率统计'),

  (9205725,'工资发放监控',9205606,1,'payment','ygb/salary/paymentMonitor/index','','YgbDocSalaryPayment','C','ygb:salaryBatch:list','money','文档二级模块：工资发放进度管控'),
  (9205726,'监管账户监控',9205606,2,'account','ygb/salary/accountMonitor/index','','YgbDocSalaryAccount','C','ygb:salaryBatch:list','money','文档二级模块：监管账户资金流水查询'),
  (9205727,'拖欠工资预警',9205606,3,'arrears','ygb/salary/overdueWarning/index','','YgbDocSalaryArrears','C','ygb:salaryBatchArrears:list','bell','文档二级模块：逾期欠薪预警催办'),
  (9205728,'银行代发结果监控',9205606,4,'bankDistribution','ygb/salary/bankDistribution/index','','YgbDocSalaryBankDistribution','C','ygb:salaryBatch:list','money','文档二级模块：银行代发失败排查与重试'),

  (9205729,'社保缴费监控',9205607,1,'payment','ygb/socialPayment/index','','YgbDocSocialPayment','C','ygb:socialPayment:list','peoples','文档二级模块：企业社保缴费状态核查'),
  (9205730,'基数比对',9205607,2,'baseCompare','ygb/socialBaseCompare/index','','YgbDocSocialBaseCompare','C','ygb:socialBaseCompare:list','chart','文档二级模块：工资与社保基数差异比对'),
  (9205731,'参保率统计',9205607,3,'enrollment','ygb/socialEnrollment/index','','YgbDocSocialEnrollment','C','ygb:socialEnrollment:list','chart','文档二级模块：企业参保率排名'),
  (9205732,'社保补缴跟踪',9205607,4,'supplement','ygb/socialSupplement/index','','YgbDocSocialSupplement','C','ygb:socialSupplement:list','list','文档二级模块：断缴整改及补缴跟踪'),

  (9205733,'工伤事件管理',9205608,1,'event','ygb/injuryEvent/index','','YgbDocInjuryEvent','C','ygb:injuryEvent:list','skill','文档二级模块：工伤登记与材料审核'),
  (9205734,'工伤认定辅助',9205608,2,'recognition','ygb/injury/recognitionAssist/index','','YgbDocInjuryRecognition','C','ygb:injuryEvent:list','time','文档二级模块：30天认定倒计时监控'),
  (9205735,'工伤统计分析',9205608,3,'stat','ygb/injury/statisticalAnalysis/index','','YgbDocInjuryStat','C','ygb:injuryEvent:list','chart','文档二级模块：工伤发生率及等级统计'),
  (9205736,'工伤预防',9205608,4,'prevention','ygb/preventionProject/index','','YgbDocInjuryPrevention','C','ygb:preventionProject:list','guide','文档二级模块：预防项目/培训/AI管控'),
  (9205737,'工伤监测',9205608,5,'monitor','ygb/injuryPersonMonitor/index','','YgbDocInjuryMonitor','C','ygb:injuryPersonMonitor:list','monitor','文档二级模块：人员及企业职业病新业态伤害监测'),

  (9205738,'用工比例监控',9205609,1,'employmentRatio','ygb/employmentRatio/index','','YgbDocEmploymentRatio','C','ygb:employmentRatio:list','chart','文档二级模块：劳务派遣用工比例预警'),
  (9205739,'三性岗位审核',9205609,2,'threeNaturePost','ygb/threeNaturePost/index','','YgbDocThreeNaturePost','C','ygb:threeNaturePost:list','guide','文档二级模块：临时性辅助性岗位合规审核'),
  (9205740,'假外包识别',9205609,3,'fakeOutsourcing','ygb/fakeOutsourcing/index','','YgbDocFakeOutsourcing','C','ygb:fakeOutsourcing:list','search','文档二级模块：AI识别疑似假外包合同'),
  (9205741,'专项整治台账',9205609,4,'ledger','ygb/specialRectification/index','','YgbDocSpecialLedger','C','ygb:specialRectification:list','list','文档二级模块：问题整改跟踪及台账生成'),

  (9205742,'个税比对',9205610,1,'personalTax','ygb/taxCompare/index','','YgbDocTaxCompare','C','ygb:taxCompare:list','excel','文档二级模块：工资与个税申报差异比对'),
  (9205743,'发票比对',9205610,2,'invoice','ygb/taxInvoice/index','','YgbDocTaxInvoice','C','ygb:taxInvoice:list','excel','文档二级模块：开票与工资总额偏离分析'),
  (9205744,'资金流穿透分析',9205610,3,'fundFlow','ygb/taxFundFlow/index','','YgbDocTaxFundFlow','C','ygb:taxFundFlow:list','chart','文档二级模块：空壳过账企业识别'),
  (9205745,'税务预警处置',9205610,4,'warning','ygb/taxCompare/index','','YgbDocTaxWarning','C','ygb:taxCompare:list','bell','文档二级模块：税务异常核查追缴'),
  (9205746,'追缴税款统计',9205610,5,'recoveryStat','ygb/statReport/tax/index','','YgbDocTaxRecoveryStat','C','ygb:statReport:tax:query','table','文档二级模块：税款及滞纳金统计'),

  (9205747,'投保监管',9205611,1,'insurance','ygb/aqInsurance/index','','YgbDocAqInsurance','C','ygb:aqInsurance:list','skill','文档二级模块：高危企业安责险投保到期管控'),
  (9205748,'事故预防服务',9205611,2,'preventionService','ygb/aqInsurance/preventionService/index','','YgbDocAqPreventionService','C','ygb:aqInsurance:list','guide','文档二级模块：事故预防服务审批验收'),
  (9205749,'赔付率监控',9205611,3,'claim','ygb/aqInsuranceClaim/index','','YgbDocAqClaim','C','ygb:aqInsuranceClaim:list','chart','文档二级模块：行业企业赔付率统计'),
  (9205750,'资金池管理',9205611,4,'fund','ygb/preventionFund/index','','YgbDocAqFund','C','ygb:preventionFund:list','money','文档二级模块：事故预防资金专户管控'),

  (9205751,'阳光劳务屏管理',9205612,1,'sunlightScreen','ygb/device/sunlightScreen/index','','YgbDocDeviceSunlightScreen','C','ygb:device:list','monitor','文档二级模块：阳光劳务屏注册运维'),
  (9205752,'千机万码芯片设备管理',9205612,2,'chip','ygb/deviceChip/index','','YgbDocDeviceChip','C','ygb:device:list','build','文档二级模块：芯片设备注册运维'),
  (9205753,'AI智能监控设备管理',9205612,3,'ai','ygb/deviceAi/index','','YgbDocDeviceAi','C','ygb:device:list','monitor','文档二级模块：AI设备抓拍与状态管理'),
  (9205754,'设备台账',9205612,4,'ledger','ygb/device/ledger/index','','YgbDocDeviceLedger','C','ygb:device:list','list','文档二级模块：设备状态及日志查询'),
  (9205755,'设备详情',9205612,5,'detail','ygb/device/index','','YgbDocDeviceDetail','C','ygb:device:list','build','文档二级模块：设备远程锁机/解锁及详情'),
  (9205756,'拆卸报警处理',9205612,6,'uninstallAlert','ygb/deviceUninstallAlert/index','','YgbDocDeviceUninstallAlert','C','ygb:device:list','bell','文档二级模块：设备拆卸告警处置'),
  (9205757,'批量操作',9205612,7,'batch','ygb/device/batchOperation/index','','YgbDocDeviceBatch','C','ygb:device:list','tool','文档二级模块：批量设备运维'),
  (9205758,'物联卡管理',9205612,8,'iotCard','ygb/deviceIotCard/index','','YgbDocDeviceIotCard','C','ygb:device:list','link','文档二级模块：物联卡管控'),
  (9205759,'芯片库存',9205612,9,'chipInventory','ygb/deviceChipInventory/index','','YgbDocDeviceChipInventory','C','ygb:device:list','list','文档二级模块：芯片库存管控'),
  (9205760,'电子围栏',9205612,10,'geofence','ygb/deviceGeofence/index','','YgbDocDeviceGeofence','C','ygb:deviceGeofence:list','monitor','文档二级模块：越界告警与轨迹回放'),

  (9205761,'数据碰撞与漏保清单',9205613,1,'uninsured','ygb/uninsuredList/index','','YgbDocExpansionUninsured','C','ygb:uninsuredList:list','search','文档二级模块：多数据比对生成漏保清单'),
  (9205762,'催缴跟踪',9205613,2,'collectionTracking','ygb/expansion/collectionTracking/index','','YgbDocExpansionCollection','C','ygb:uninsuredList:list','phone','文档二级模块：漏保补缴督办'),
  (9205763,'参保补贴管理',9205613,3,'subsidy','ygb/expansionSubsidy/index','','YgbDocExpansionSubsidy','C','ygb:expansionSubsidy:list','money','文档二级模块：新增参保补贴审核拨付'),
  (9205764,'工伤预防培训管理',9205613,4,'preventionTraining','ygb/expansion/preventionTraining/index','','YgbDocExpansionPreventionTraining','C','ygb:preventionProject:list','education','文档二级模块：培训计划落地'),
  (9205765,'培训课程与学时管理',9205613,5,'courseHours','ygb/expansion/courseHours/index','','YgbDocExpansionCourseHours','C','ygb:preventionProject:list','time','文档二级模块：课程学时管控'),
  (9205766,'培训效果评估',9205613,6,'trainingEvaluation','ygb/expansion/trainingEvaluation/index','','YgbDocExpansionTrainingEvaluation','C','ygb:preventionProject:list','chart','文档二级模块：培训前后风险数据评估'),

  (9205767,'信用总览',9205614,1,'overview','ygb/credit/overview/index','','YgbDocCreditOverview','C','ygb:creditScore:list','star','文档二级模块：企业信用等级分布统计'),
  (9205768,'企业信用档案',9205614,2,'archive','ygb/credit/enterpriseArchive/index','','YgbDocCreditArchive','C','ygb:creditScore:list','documentation','文档二级模块：扣分及整改档案查询'),
  (9205769,'信用评分规则配置',9205614,3,'rule','ygb/creditRule/index','','YgbDocCreditRule','C','ygb:creditRule:list','system','文档二级模块：评分规则配置'),
  (9205770,'联合惩戒推送',9205614,4,'sanction','ygb/creditSanction/index','','YgbDocCreditSanction','C','ygb:creditSanction:list','bell','文档二级模块：失信企业联合惩戒推送'),
  (9205771,'信用修复申请',9205614,5,'repair','ygb/creditRepair/index','','YgbDocCreditRepair','C','ygb:creditRepair:list','edit','文档二级模块：信用修复审核'),
  (9205772,'信用报告导出',9205614,6,'reportExport','ygb/credit/reportExport/index','','YgbDocCreditReportExport','C','ygb:creditScore:list','download','文档二级模块：信用报表导出'),

  (9205773,'用工',9205615,1,'employment','ygb/statReport/employment/index','','YgbDocStatEmployment','C','ygb:statReport:employment:query','table','文档二级模块：用工统计报表'),
  (9205774,'工资',9205615,2,'salary','ygb/statReport/salary/index','','YgbDocStatSalary','C','ygb:statReport:salary:query','money','文档二级模块：工资统计报表'),
  (9205775,'社保',9205615,3,'social','ygb/statReport/social/index','','YgbDocStatSocial','C','ygb:statReport:social:query','peoples','文档二级模块：社保统计报表'),
  (9205776,'税务',9205615,4,'tax','ygb/statReport/tax/index','','YgbDocStatTax','C','ygb:statReport:tax:query','excel','文档二级模块：税务统计报表'),
  (9205777,'专项整治',9205615,5,'rectification','ygb/statReport/rectification/index','','YgbDocStatRectification','C','ygb:statReport:rectification:query','guide','文档二级模块：专项整治报表'),
  (9205778,'考勤',9205615,6,'attendance','ygb/statReport/attendance/index','','YgbDocStatAttendance','C','ygb:statReport:attendance:query','time','文档二级模块：考勤报表'),
  (9205779,'工伤',9205615,7,'injury','ygb/statReport/injury/index','','YgbDocStatInjury','C','ygb:statReport:injury:query','skill','文档二级模块：工伤报表'),
  (9205780,'安责险',9205615,8,'aqInsurance','ygb/statReport/aqInsurance/index','','YgbDocStatAqInsurance','C','ygb:statReport:aqInsurance:query','skill','文档二级模块：安责险报表'),
  (9205781,'设备',9205615,9,'device','ygb/statReport/device/index','','YgbDocStatDevice','C','ygb:statReport:device:query','build','文档二级模块：设备报表'),
  (9205782,'扩面减损',9205615,10,'expansion','ygb/statReport/expansion/index','','YgbDocStatExpansion','C','ygb:statReport:expansion:query','chart','文档二级模块：扩面减损报表'),
  (9205783,'新业态',9205615,11,'newform','ygb/statReport/newform/index','','YgbDocStatNewform','C','ygb:statReport:newform:query','link','文档二级模块：新业态报表'),
  (9205784,'职业病',9205615,12,'occupation','ygb/statReport/occupation/index','','YgbDocStatOccupation','C','ygb:statReport:occupation:query','skill','文档二级模块：职业病报表'),
  (9205785,'工会监督报表',9205615,13,'union','ygb/statReport/union/index','','YgbDocStatUnion','C','ygb:statReport:union:query','people','文档二级模块：工会监督报表'),
  (9205786,'自定义报表',9205615,14,'custom','ygb/statReport/custom/index','','YgbDocStatCustom','C','ygb:statReport:custom:query','edit','文档二级模块：自定义报表'),

  (9205787,'从业人员档案',9205616,1,'person','ygb/person/index','','YgbDocPersonArchive','C','ygb:person:list','user','文档二级模块：员工信息脱敏管理'),
  (9205788,'特证管理',9205616,2,'certificate','ygb/personCertificate/index','','YgbDocPersonCertificate','C','ygb:personCertificate:list','documentation','文档二级模块：特种证件续期审核'),
  (9205789,'黑名单管理',9205616,3,'blacklist','ygb/personBlacklist/index','','YgbDocPersonBlacklist','C','ygb:personBlacklist:list','bell','文档二级模块：失信人员黑名单管控'),
  (9205790,'人员培训监督',9205616,4,'training','ygb/personTraining/index','','YgbDocPersonTraining','C','ygb:personTraining:list','education','文档二级模块：人员培训进度监督'),
  (9205791,'高危岗位库',9205616,5,'highRiskPost','ygb/personHighRiskPost/index','','YgbDocPersonHighRiskPost','C','ygb:personHighRiskPost:list','skill','文档二级模块：高危岗位台账'),
  (9205792,'风险岗位库',9205616,6,'riskPost','ygb/personRiskPost/index','','YgbDocPersonRiskPost','C','ygb:personRiskPost:list','chart','文档二级模块：风险岗位台账'),
  (9205793,'工伤预防专家库',9205616,7,'expert','ygb/personExpert/index','','YgbDocPersonExpert','C','ygb:personExpert:list','people','文档二级模块：专家资源管理'),
  (9205794,'新业态人员库',9205616,8,'newform','ygb/person/newform/index','','YgbDocPersonNewform','C','ygb:person:list','link','文档二级模块：新业态从业人员管控'),

  (9205795,'主管监管单位管理',9205617,1,'regulator','ygb/enterpriseRegulator/index','','YgbDocEnterpriseRegulator','C','ygb:enterprise:list','people','文档二级模块：监管主体维护'),
  (9205796,'劳务派遣公司管理',9205617,2,'dispatch','ygb/enterpriseDispatch/index','','YgbDocEnterpriseDispatch','C','ygb:enterprise:list','people','文档二级模块：派遣主体维护'),
  (9205797,'用工单位管理',9205617,3,'employer','ygb/enterpriseEmployer/index','','YgbDocEnterpriseEmployer','C','ygb:enterprise:list','people','文档二级模块：用工主体维护'),
  (9205798,'高危企业库管理',9205617,4,'highRisk','ygb/enterpriseHighRisk/index','','YgbDocEnterpriseHighRisk','C','ygb:enterpriseHighRisk:list','bell','文档二级模块：高危企业库维护'),
  (9205799,'派遣/用工关联关系',9205617,5,'relation','ygb/enterprise/relation/index','','YgbDocEnterpriseRelation','C','ygb:enterprise:list','link','文档二级模块：用工关联图谱'),
  (9205800,'新业态平台企业管理',9205617,6,'newform','ygb/enterprise/newform/index','','YgbDocEnterpriseNewform','C','ygb:enterprise:list','link','文档二级模块：平台企业合规管控'),
  (9205801,'工会管理',9205617,7,'union','ygb/enterpriseUnion/index','','YgbDocEnterpriseUnion','C','ygb:enterpriseUnion:list','people','文档二级模块：工会维权及监督管理'),
  (9205802,'企业自主服务',9205617,8,'selfService','ygb/enterprise/selfService/index','','YgbDocEnterpriseSelfService','C','ygb:enterprise:list','link','文档二级模块：企业自主后台服务入口'),

  (9205803,'组织架构管理',9205618,1,'dept','system/dept/index','','YgbDocSystemDept','C','system:dept:list','tree','文档二级模块：四级组织架构维护'),
  (9205804,'用户与角色权限',9205618,2,'role','system/role/index','','YgbDocSystemRole','C','system:role:list','peoples','文档二级模块：RBAC权限配置'),
  (9205805,'系统参数配置',9205618,3,'config','system/config/index','','YgbDocSystemConfig','C','system:config:list','system','文档二级模块：系统阈值参数设置'),
  (9205806,'数据字典',9205618,4,'dict','system/dict/index','','YgbDocSystemDict','C','system:dict:list','dict','文档二级模块：枚举数据维护'),
  (9205807,'通知公告管理',9205618,5,'notice','system/notice/index','','YgbDocSystemNotice','C','system:notice:list','message','文档二级模块：公告推送'),
  (9205808,'文档管理',9205618,6,'document','ygb/platform/document/index','','YgbDocSystemDocument','C','ygb:platformDocument:list','documentation','文档二级模块：资料管理'),
  (9205809,'接口与数据交换监管',9205618,7,'exchange','ygb/platform/exchange/index','','YgbDocSystemExchange','C','ygb:platformExchange:list','link','文档二级模块：接口监控'),
  (9205810,'运行监控',9205618,8,'runtime','ygb/platform/overview/index','','YgbDocSystemRuntime','C','ygb:platformRuntime:query','monitor','文档二级模块：系统运维'),
  (9205811,'安全审计',9205618,9,'securityAudit','ygb/platform/securityAudit/index','','YgbDocSystemSecurityAudit','C','ygb:platformSecurityAudit:list','lock','文档二级模块：操作日志审计'),
  (9205812,'数据备份与恢复',9205618,10,'backup','ygb/platform/backup/index','','YgbDocSystemBackup','C','ygb:platformBackup:list','download','文档二级模块：数据备份恢复'),
  (9205813,'用户管理',9205618,11,'user','system/user/index','','YgbDocSystemUser','C','system:user:list','user','文档二级模块：用户管理'),

  (9205814,'运营数据总览',9205619,1,'overview','ygb/operation/overview/index','','YgbDocOperationOverview','C','ygb:operation:overview','dashboard','文档二级模块：平台运营数据统计'),
  (9205815,'招聘岗位审核',9205619,2,'jobReview','ygb/operation/jobReview/index','','YgbDocOperationJobReview','C','ygb:operationJobReview:list','job','文档二级模块：招聘信息审核'),
  (9205816,'企业入驻审核',9205619,3,'enterpriseReview','ygb/operation/enterpriseReview/index','','YgbDocOperationEnterpriseReview','C','ygb:operationEnterpriseReview:list','people','文档二级模块：企业入驻审核'),
  (9205817,'简历管理',9205619,4,'resume','ygb/operation/resume/index','','YgbDocOperationResume','C','ygb:operationResume:list','user','文档二级模块：简历资源管理'),
  (9205818,'职位分类管理',9205619,5,'jobCategory','ygb/operation/operationJobCategory/index','','YgbDocOperationJobCategory','C','ygb:operationJobCategory:list','job','文档二级模块：职位分类管理'),
  (9205819,'广告/轮播图管理',9205619,6,'banner','ygb/portalContent/index','{"sectionCode":"banner"}','YgbDocOperationBanner','C','ygb:portalContent:list','component','文档二级模块：平台页面配置'),
  (9205820,'数据统计与分析',9205619,7,'dataAnalysis','ygb/operation/dataAnalysis/index','','YgbDocOperationDataAnalysis','C','ygb:operationRecruitStats:list','chart','文档二级模块：招聘数据分析'),
  (9205821,'消息推送管理',9205619,8,'message','ygb/operation/message/index','','YgbDocOperationMessage','C','ygb:operationMessage:list','message','文档二级模块：消息推送'),
  (9205822,'设备安装运维',9205619,9,'deviceOperation','ygb/operation/deviceInstallOrder/index','','YgbDocOperationDevice','C','ygb:deviceInstallOrder:list','build','文档二级模块：设备运维工单管理'),

  (9205823,'企业仪表盘',9205620,1,'dashboard','ygb/enterprisePortal/dashboard/index','','YgbDocEnterpriseDashboard','C','ygb:enterprisePortal:dashboard','dashboard','文档二级模块：企业合规数据总览'),
  (9205824,'企业人员管理',9205620,2,'person','ygb/enterprisePortal/overview/index','','YgbDocEnterprisePerson','C','ygb:enterprisePortal:person','user','文档二级模块：企业人员自主管控'),
  (9205825,'企业设备管理',9205620,3,'device','ygb/enterprisePortal/overview/index','','YgbDocEnterpriseDevice','C','ygb:enterprisePortal:device','build','文档二级模块：企业设备自主管控'),
  (9205826,'企业工资管理',9205620,4,'salary','ygb/enterprisePortal/overview/index','','YgbDocEnterpriseSalary','C','ygb:enterprisePortal:salary','money','文档二级模块：工资代发'),
  (9205827,'企业作业管理',9205620,5,'work','ygb/enterprisePortal/overview/index','','YgbDocEnterpriseWork','C','ygb:enterprisePortal:work','tool','文档二级模块：高危作业审批'),
  (9205828,'企业保险管理',9205620,6,'insurance','ygb/enterprisePortal/overview/index','','YgbDocEnterpriseInsurance','C','ygb:enterprisePortal:insurance','skill','文档二级模块：保险投保续保'),
  (9205829,'企业培训管理',9205620,7,'training','ygb/enterprisePortal/overview/index','','YgbDocEnterpriseTraining','C','ygb:enterprisePortal:training','education','文档二级模块：员工培训管理'),
  (9205830,'企业招聘管理',9205620,8,'recruit','ygb/enterprisePortal/overview/index','','YgbDocEnterpriseRecruit','C','ygb:enterprisePortal:recruit','job','文档二级模块：招聘管理'),
  (9205831,'企业财务管理',9205620,9,'finance','ygb/enterprisePortal/overview/index','','YgbDocEnterpriseFinance','C','ygb:enterprisePortal:finance','money','文档二级模块：企业财务管理'),
  (9205832,'企业信用报告',9205620,10,'credit','ygb/enterprisePortal/overview/index','','YgbDocEnterpriseCredit','C','ygb:enterprisePortal:credit','star','文档二级模块：企业信用风险自查'),

  (9205833,'暖新地图',9205621,1,'warmMap','ygb/citizenService/warmMap/index','','YgbDocCitizenWarmMap','C','ygb:citizenService:list','monitor','文档二级模块：服务站点地图导航'),
  (9205834,'培训课程',9205621,2,'trainingCourse','ygb/citizenService/trainingCourse/index','','YgbDocCitizenTrainingCourse','C','ygb:citizenService:list','education','文档二级模块：线上安全技能培训'),
  (9205835,'法规库',9205621,3,'lawLibrary','ygb/citizenService/lawLibrary/index','','YgbDocCitizenLawLibrary','C','ygb:citizenService:list','documentation','文档二级模块：劳动法规查询'),
  (9205836,'互助圈',9205621,4,'mutualHelp','ygb/citizenService/mutualHelp/index','','YgbDocCitizenMutualHelp','C','ygb:citizenService:list','people','文档二级模块：员工问答互助'),
  (9205837,'招聘用工市场',9205621,5,'recruitMarket','ygb/citizenService/recruitMarket/index','','YgbDocCitizenRecruitMarket','C','ygb:citizenService:list','job','文档二级模块：岗位求职投递'),

  (9205838,'新业态人员库',9205622,1,'worker','ygb/newformWorker/index','','YgbDocNewformWorker','C','ygb:newformWorker:list','user','文档二级模块：新业态从业人员台账'),
  (9205839,'平台企业管理',9205622,2,'platform','ygb/newformPlatform/index','','YgbDocNewformPlatform','C','ygb:newformPlatform:list','people','文档二级模块：平台企业合规管控'),
  (9205840,'职业伤害监测',9205622,3,'injuryMonitor','ygb/newformInjuryMonitor/index','','YgbDocNewformInjuryMonitor','C','ygb:newformInjuryMonitor:list','monitor','文档二级模块：职业伤害数据统计'),
  (9205841,'新业态培训管理',9205622,4,'training','ygb/newformTraining/index','','YgbDocNewformTraining','C','ygb:newformTraining:list','education','文档二级模块：新业态专项安全培训'),

  (9205842,'职业病监测',9205623,1,'monitor','ygb/occupationMonitor/index','','YgbDocOccupationMonitor','C','ygb:occupationMonitor:list','monitor','文档二级模块：职业病发病数据统计'),
  (9205843,'职业病预防项目',9205623,2,'prevention','ygb/occupationPrevention/index','','YgbDocOccupationPrevention','C','ygb:occupationPrevention:list','guide','文档二级模块：专项预防项目申报验收'),
  (9205844,'职业健康档案',9205623,3,'healthArchive','ygb/occupationHealthArchive/index','','YgbDocOccupationHealthArchive','C','ygb:occupationHealthArchive:list','documentation','文档二级模块：体检健康档案及复查提醒');

insert into tmp_phase56_doc_menu_button (menu_id, menu_name, parent_id, order_num, perms, icon)
select menu_id * 10 + action_order,
       action_name,
       menu_id,
       action_order,
       concat(substring_index(perms, ':', 2), ':', action_code),
       '#'
from tmp_phase56_doc_menu
join (
  select 1 as action_order, '查询' as action_name, 'query' as action_code union all
  select 2, '新增', 'add' union all
  select 3, '修改', 'edit' union all
  select 4, '删除', 'remove' union all
  select 5, '导出', 'export'
) action_list
where menu_type = 'C'
  and (perms like 'ygb:%:list' or perms like 'system:%:list')
  and (component like 'ygb/%' or component like 'system/%')
  and component not like 'ygb/enterprisePortal/%'
  and component not like 'ygb/citizenService/%';

insert ignore into tmp_phase56_doc_menu_button (menu_id, menu_name, parent_id, order_num, perms, icon)
values
  (92057076,'处置',9205707,6,'ygb:warning:handle','#'),
  (92057106,'生成',9205710,6,'ygb:aiReport:generate','#'),
  (92057126,'订阅',9205712,6,'ygb:aiReportSubscription:list','#'),
  (92057136,'状态调整',9205713,6,'ygb:aiReportTask:status','#'),
  (92057156,'启用模型',9205715,6,'ygb:aiReportConfig:activate','#'),
  (92057286,'回写',9205728,6,'ygb:salaryBatch:submit','#'),
  (92057287,'导出',9205728,7,'ygb:salaryBatch:export','#'),
  (92057296,'同步',9205729,6,'ygb:socialPayment:sync','#'),
  (92057306,'执行比对',9205730,6,'ygb:socialBaseCompare:compare','#'),
  (92057336,'流程流转',9205733,6,'ygb:injuryEvent:flow','#'),
  (92057386,'测算',9205738,6,'ygb:employmentRatio:calculate','#'),
  (92057406,'识别分析',9205740,6,'ygb:fakeOutsourcing:analyze','#'),
  (92057426,'同步',9205742,6,'ygb:taxCompare:sync','#'),
  (92057427,'执行比对',9205742,7,'ygb:taxCompare:compare','#'),
  (92057456,'同步',9205745,6,'ygb:taxCompare:sync','#'),
  (92057457,'执行比对',9205745,7,'ygb:taxCompare:compare','#'),
  (92057476,'同步',9205747,6,'ygb:aqInsurance:sync','#'),
  (92057486,'导出',9205748,6,'ygb:preventionProject:export','#'),
  (92057556,'锁机',9205755,6,'ygb:device:lock','#'),
  (92057557,'解锁',9205755,7,'ygb:device:unlock','#'),
  (92057558,'授权',9205755,8,'ygb:device:authorize','#'),
  (92057559,'心跳',9205755,9,'ygb:device:heartbeat','#'),
  (92057560,'AI事件',9205755,10,'ygb:device:aiEvent','#'),
  (92057576,'锁机',9205757,6,'ygb:device:lock','#'),
  (92057577,'解锁',9205757,7,'ygb:device:unlock','#'),
  (92057578,'授权',9205757,8,'ygb:device:authorize','#'),
  (92057616,'生成',9205761,6,'ygb:uninsuredList:generate','#'),
  (92057617,'处置',9205761,7,'ygb:uninsuredList:handle','#'),
  (92057646,'导出',9205764,6,'ygb:preventionProject:export','#'),
  (92057656,'导出',9205765,6,'ygb:preventionProject:export','#'),
  (92057666,'导出',9205766,6,'ygb:preventionProject:export','#'),
  (92057726,'生成',9205772,6,'ygb:creditScore:generate','#'),
  (92057906,'流程处理',9205790,6,'ygb:workerMessage:flow','#'),
  (92057946,'导出',9205794,6,'ygb:newformWorker:export','#'),
  (92058006,'导出',9205800,6,'ygb:newformPlatform:export','#'),
  (92058386,'同步',9205838,6,'ygb:newformWorker:sync','#'),
  (92058426,'同步',9205842,6,'ygb:occupationMonitor:sync','#'),
  (92058136,'导入',9205813,6,'system:user:import','#'),
  (92058137,'重置密码',9205813,7,'system:user:resetPwd','#'),
  (92057466,'生成',9205746,6,'ygb:statReport:tax:generate','#'),
  (92057467,'导出',9205746,7,'ygb:statReport:tax:export','#'),
  (92057468,'打印',9205746,8,'ygb:statReport:tax:print','#'),
  (92057736,'生成',9205773,6,'ygb:statReport:employment:generate','#'),
  (92057737,'导出',9205773,7,'ygb:statReport:employment:export','#'),
  (92057738,'打印',9205773,8,'ygb:statReport:employment:print','#'),
  (92057746,'生成',9205774,6,'ygb:statReport:salary:generate','#'),
  (92057747,'导出',9205774,7,'ygb:statReport:salary:export','#'),
  (92057748,'打印',9205774,8,'ygb:statReport:salary:print','#'),
  (92057756,'生成',9205775,6,'ygb:statReport:social:generate','#'),
  (92057757,'导出',9205775,7,'ygb:statReport:social:export','#'),
  (92057758,'打印',9205775,8,'ygb:statReport:social:print','#'),
  (92057766,'生成',9205776,6,'ygb:statReport:tax:generate','#'),
  (92057767,'导出',9205776,7,'ygb:statReport:tax:export','#'),
  (92057768,'打印',9205776,8,'ygb:statReport:tax:print','#'),
  (92057776,'生成',9205777,6,'ygb:statReport:rectification:generate','#'),
  (92057777,'导出',9205777,7,'ygb:statReport:rectification:export','#'),
  (92057778,'打印',9205777,8,'ygb:statReport:rectification:print','#'),
  (92057786,'生成',9205778,6,'ygb:statReport:attendance:generate','#'),
  (92057787,'导出',9205778,7,'ygb:statReport:attendance:export','#'),
  (92057788,'打印',9205778,8,'ygb:statReport:attendance:print','#'),
  (92057796,'生成',9205779,6,'ygb:statReport:injury:generate','#'),
  (92057797,'导出',9205779,7,'ygb:statReport:injury:export','#'),
  (92057798,'打印',9205779,8,'ygb:statReport:injury:print','#'),
  (92057806,'生成',9205780,6,'ygb:statReport:aqInsurance:generate','#'),
  (92057807,'导出',9205780,7,'ygb:statReport:aqInsurance:export','#'),
  (92057808,'打印',9205780,8,'ygb:statReport:aqInsurance:print','#'),
  (92057816,'生成',9205781,6,'ygb:statReport:device:generate','#'),
  (92057817,'导出',9205781,7,'ygb:statReport:device:export','#'),
  (92057818,'打印',9205781,8,'ygb:statReport:device:print','#'),
  (92057826,'生成',9205782,6,'ygb:statReport:expansion:generate','#'),
  (92057827,'导出',9205782,7,'ygb:statReport:expansion:export','#'),
  (92057828,'打印',9205782,8,'ygb:statReport:expansion:print','#'),
  (92057836,'生成',9205783,6,'ygb:statReport:newform:generate','#'),
  (92057837,'导出',9205783,7,'ygb:statReport:newform:export','#'),
  (92057838,'打印',9205783,8,'ygb:statReport:newform:print','#'),
  (92057846,'生成',9205784,6,'ygb:statReport:occupation:generate','#'),
  (92057847,'导出',9205784,7,'ygb:statReport:occupation:export','#'),
  (92057848,'打印',9205784,8,'ygb:statReport:occupation:print','#'),
  (92057856,'生成',9205785,6,'ygb:statReport:union:generate','#'),
  (92057857,'导出',9205785,7,'ygb:statReport:union:export','#'),
  (92057858,'打印',9205785,8,'ygb:statReport:union:print','#'),
  (92057866,'生成',9205786,6,'ygb:statReport:custom:generate','#'),
  (92057867,'导出',9205786,7,'ygb:statReport:custom:export','#'),
  (92057868,'打印',9205786,8,'ygb:statReport:custom:print','#'),
  (92058226,'维修查询',9205822,6,'ygb:deviceRepairOrder:list','#'),
  (92058227,'维修详情',9205822,7,'ygb:deviceRepairOrder:query','#'),
  (92058228,'维修新增',9205822,8,'ygb:deviceRepairOrder:add','#'),
  (92058229,'维修修改',9205822,9,'ygb:deviceRepairOrder:edit','#'),
  (92058230,'维修删除',9205822,10,'ygb:deviceRepairOrder:remove','#'),
  (92058231,'维修导出',9205822,11,'ygb:deviceRepairOrder:export','#'),
  (92058232,'巡检查询',9205822,12,'ygb:deviceInspectPlan:list','#'),
  (92058233,'巡检详情',9205822,13,'ygb:deviceInspectPlan:query','#'),
  (92058234,'巡检新增',9205822,14,'ygb:deviceInspectPlan:add','#'),
  (92058235,'巡检修改',9205822,15,'ygb:deviceInspectPlan:edit','#'),
  (92058236,'巡检删除',9205822,16,'ygb:deviceInspectPlan:remove','#'),
  (92058237,'巡检导出',9205822,17,'ygb:deviceInspectPlan:export','#'),
  (92058238,'统计查询',9205822,18,'ygb:operationMaintenanceStats:list','#'),
  (92058239,'统计详情',9205822,19,'ygb:operationMaintenanceStats:query','#'),
  (92058240,'统计新增',9205822,20,'ygb:operationMaintenanceStats:add','#'),
  (92058241,'统计修改',9205822,21,'ygb:operationMaintenanceStats:edit','#'),
  (92058242,'统计删除',9205822,22,'ygb:operationMaintenanceStats:remove','#'),
  (92058243,'统计导出',9205822,23,'ygb:operationMaintenanceStats:export','#');

-- Hide legacy YGB/common root menus. The old business permissions and data stay untouched.
update sys_menu
set visible = '1',
    update_by = 'phase56',
    update_time = now()
where menu_type in ('M', 'C')
  and parent_id = 0
  and menu_id not in (select menu_id from tmp_phase56_doc_menu where parent_id = 0)
  and lower(ifnull(portal_scope, 'both')) in ('ygb', 'both', 'common');

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`, route_name,
  is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
select
  menu_id, menu_name, parent_id, order_num, path, component, route_query, route_name,
  1, 0, menu_type, '0', '0', 'ygb', perms, icon,
  'phase56', now(), 'phase56', now(), remark
from tmp_phase56_doc_menu
on duplicate key update
  menu_name = values(menu_name),
  parent_id = values(parent_id),
  order_num = values(order_num),
  path = values(path),
  component = values(component),
  `query` = values(`query`),
  route_name = values(route_name),
  is_frame = values(is_frame),
  is_cache = values(is_cache),
  menu_type = values(menu_type),
  visible = values(visible),
  status = values(status),
  portal_scope = values(portal_scope),
  perms = values(perms),
  icon = values(icon),
  update_by = values(update_by),
  update_time = values(update_time),
  remark = values(remark);

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`, route_name,
  is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
select
  menu_id, menu_name, parent_id, order_num, '', '', '', '',
  1, 0, 'F', '0', '0', 'ygb', perms, icon,
  'phase56', now(), 'phase56', now(), '文档最终菜单自动补齐按钮权限'
from tmp_phase56_doc_menu_button
on duplicate key update
  menu_name = values(menu_name),
  parent_id = values(parent_id),
  order_num = values(order_num),
  path = values(path),
  component = values(component),
  `query` = values(`query`),
  route_name = values(route_name),
  is_frame = values(is_frame),
  is_cache = values(is_cache),
  menu_type = values(menu_type),
  visible = values(visible),
  status = values(status),
  portal_scope = values(portal_scope),
  perms = values(perms),
  icon = values(icon),
  update_by = values(update_by),
  update_time = values(update_time),
  remark = values(remark);

insert into sys_role_menu (role_id, menu_id, portal_scope)
select r.role_id, m.menu_id, 'ygb'
from sys_role r
join tmp_phase56_doc_menu m
where r.status = '0'
  and r.del_flag = '0'
  and (
    r.role_key = 'admin'
    or r.role_key like '%admin%'
    or lower(ifnull(r.allowed_portal_scope, 'both')) in ('ygb', 'both', 'common')
  )
on duplicate key update portal_scope = values(portal_scope);

insert into sys_role_menu (role_id, menu_id, portal_scope)
select r.role_id, b.menu_id, 'ygb'
from sys_role r
join tmp_phase56_doc_menu_button b
where r.status = '0'
  and r.del_flag = '0'
  and (
    r.role_key = 'admin'
    or r.role_key like '%admin%'
    or lower(ifnull(r.allowed_portal_scope, 'both')) in ('ygb', 'both', 'common')
  )
on duplicate key update portal_scope = values(portal_scope);

-- Keep the old phase53 contract menu callable by direct route only; it must not be a second visible root.
update sys_menu
set visible = '1',
    update_by = 'phase56',
    update_time = now()
where menu_id = 9105300
  and portal_scope = 'ygb';

set @phase56_cockpit_source_mode_exists := (
  select count(1)
  from information_schema.columns
  where table_schema = database()
    and table_name = 'ygb_cockpit_config'
    and column_name = 'source_mode'
);
set @phase56_cockpit_source_mode_sql := if(
  @phase56_cockpit_source_mode_exists = 0,
  "select 1",
  "update ygb_cockpit_config set source_mode = 'SYSTEM', update_by = 'phase56', update_time = now() where lower(ifnull(source_mode, '')) = 'stub'"
);
prepare stmt_phase56_cockpit_source_mode from @phase56_cockpit_source_mode_sql;
execute stmt_phase56_cockpit_source_mode;
deallocate prepare stmt_phase56_cockpit_source_mode;

-- Verification result sets. Expected: 23, 144, 0, 0.
select count(1) as phase56_visible_top_menu_count
from sys_menu
where parent_id = 0
  and menu_type = 'M'
  and visible = '0'
  and status = '0'
  and portal_scope = 'ygb'
  and menu_id between 9205601 and 9205623;

select count(1) as phase56_visible_second_menu_count
from sys_menu
where parent_id between 9205601 and 9205623
  and menu_type = 'C'
  and visible = '0'
  and status = '0'
  and portal_scope = 'ygb'
  and menu_id between 9205701 and 9205844;

select count(1) as phase56_blank_visible_icon_count
from sys_menu
where menu_id between 9205601 and 9205844
  and visible = '0'
  and status = '0'
  and (icon is null or trim(icon) = '' or icon = '#');

select count(1) as phase56_extra_visible_ygb_root_count
from sys_menu
where parent_id = 0
  and menu_type in ('M', 'C')
  and visible = '0'
  and status = '0'
  and lower(ifnull(portal_scope, 'both')) in ('ygb', 'both', 'common')
  and menu_id not between 9205601 and 9205623;
