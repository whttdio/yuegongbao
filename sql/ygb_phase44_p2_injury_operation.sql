set names utf8mb4;

-- =============================================================================
-- Phase 44: P2 injury monitoring sub-domains + operation device maintenance
-- =============================================================================

-- 1) Injury monitoring menus under 工伤与预防 (2060)
insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
) values
  (5951, '工伤人员监测', 2060, 4, 'injuryPersonMonitor', 'ygb/injuryPersonMonitor/index', '', 'YgbInjuryPersonMonitor', 1, 0, 'C', '0', '0', 'ygb:injuryPersonMonitor:list', 'user', 'admin', sysdate(), 'admin', sysdate(), '8.5.1 工伤人员情况监测', 'ygb'),
  (5952, '单位工伤预防监测', 2060, 5, 'injuryEmployerMonitor', 'ygb/injuryEmployerMonitor/index', '', 'YgbInjuryEmployerMonitor', 1, 0, 'C', '0', '0', 'ygb:injuryEmployerMonitor:list', 'office-building', 'admin', sysdate(), 'admin', sysdate(), '8.5.2 用人单位工伤预防监测', 'ygb'),
  (5953, '地区工伤预防监测', 2060, 6, 'injuryRegionMonitor', 'ygb/injuryRegionMonitor/index', '', 'YgbInjuryRegionMonitor', 1, 0, 'C', '0', '0', 'ygb:injuryRegionMonitor:list', 'location', 'admin', sysdate(), 'admin', sysdate(), '8.5.3 地区工伤预防情况监测', 'ygb'),
  (5954, '职业病危害监测', 2060, 7, 'injuryOccHazardMonitor', 'ygb/injuryOccHazardMonitor/index', '', 'YgbInjuryOccHazardMonitor', 1, 0, 'C', '0', '0', 'ygb:injuryOccHazardMonitor:list', 'first-aid-kit', 'admin', sysdate(), 'admin', sysdate(), '8.5.4 职业病监测', 'ygb'),
  (5955, '新业态伤害监测', 2060, 8, 'injuryNewformHazardMonitor', 'ygb/injuryNewformHazardMonitor/index', '', 'YgbInjuryNewformHazardMonitor', 1, 0, 'C', '0', '0', 'ygb:injuryNewformHazardMonitor:list', 'bicycle', 'admin', sysdate(), 'admin', sysdate(), '8.5.5 新就业形态职业伤害监测', 'ygb'),
  (5956, '工伤事故预警', 2060, 9, 'injuryAccidentWarning', 'ygb/injuryAccidentWarning/index', '', 'YgbInjuryAccidentWarning', 1, 0, 'C', '0', '0', 'ygb:injuryAccidentWarning:list', 'bell', 'admin', sysdate(), 'admin', sysdate(), '8.5.6 工伤事故预警', 'ygb')
on duplicate key update
  menu_name = values(menu_name),
  parent_id = values(parent_id),
  order_num = values(order_num),
  path = values(path),
  component = values(component),
  route_name = values(route_name),
  perms = values(perms),
  icon = values(icon),
  portal_scope = values(portal_scope),
  remark = values(remark);

-- 2) Operation device maintenance menus under 运营后台 (4300)
insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
) values
  (5971, '设备安装工单', 4300, 12, 'deviceInstallOrder', 'ygb/operation/deviceInstallOrder/index', '', 'YgbDeviceInstallOrder', 1, 0, 'C', '0', '0', 'ygb:deviceInstallOrder:list', 'tool', 'admin', sysdate(), 'admin', sysdate(), '19.9 安装工单管理', 'ygb'),
  (5972, '设备维修工单', 4300, 13, 'deviceRepairOrder', 'ygb/operation/deviceRepairOrder/index', '', 'YgbDeviceRepairOrder', 1, 0, 'C', '0', '0', 'ygb:deviceRepairOrder:list', 'tools', 'admin', sysdate(), 'admin', sysdate(), '19.9 设备维修工单', 'ygb'),
  (5973, '设备巡检计划', 4300, 14, 'deviceInspectPlan', 'ygb/operation/deviceInspectPlan/index', '', 'YgbDeviceInspectPlan', 1, 0, 'C', '0', '0', 'ygb:deviceInspectPlan:list', 'date', 'admin', sysdate(), 'admin', sysdate(), '19.9 设备巡检计划', 'ygb'),
  (5974, '芯片领用台账', 4300, 15, 'operationChipDispatch', 'ygb/operation/operationChipDispatch/index', '', 'YgbOperationChipDispatch', 1, 0, 'C', '0', '0', 'ygb:operationChipDispatch:list', 'cpu', 'admin', sysdate(), 'admin', sysdate(), '19.9 芯片领用', 'ygb'),
  (5975, '芯片库存', 4300, 16, 'operationChipInventory', 'ygb/deviceChipInventory/index', '', 'YgbOperationChipInventory', 1, 0, 'C', '0', '0', 'ygb:deviceChipInventory:list', 'box', 'admin', sysdate(), 'admin', sysdate(), '19.9 芯片库存（复用设备台账）', 'ygb'),
  (5976, '物联卡运维', 4300, 17, 'operationIotCard', 'ygb/deviceIotCard/index', '', 'YgbOperationIotCard', 1, 0, 'C', '0', '0', 'ygb:deviceIotCard:list', 'link', 'admin', sysdate(), 'admin', sysdate(), '19.9 物联卡管理（复用设备台账）', 'ygb'),
  (5977, '运维统计分析', 4300, 18, 'operationMaintenanceStats', 'ygb/operation/operationMaintenanceStats/index', '', 'YgbOperationMaintenanceStats', 1, 0, 'C', '0', '0', 'ygb:operationMaintenanceStats:list', 'data-analysis', 'admin', sysdate(), 'admin', sysdate(), '19.9 运维统计分析', 'ygb')
on duplicate key update
  menu_name = values(menu_name),
  parent_id = values(parent_id),
  order_num = values(order_num),
  path = values(path),
  component = values(component),
  route_name = values(route_name),
  perms = values(perms),
  icon = values(icon),
  portal_scope = values(portal_scope),
  remark = values(remark);

-- 3) Button permissions (5980-6029)
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark, portal_scope)
select 5980 + n.id, n.btn_name, n.parent_id, n.order_num, '', '', '', '', 1, 0, 'F', '0', '0', n.perms, '#', 'admin', sysdate(), '', 'ygb'
from (
  select 0 as id, 5951 as parent_id, 1 as order_num, '查询' as btn_name, 'ygb:injuryPersonMonitor:query' as perms union all
  select 1, 5951, 2, '新增', 'ygb:injuryPersonMonitor:add' union all
  select 2, 5951, 3, '修改', 'ygb:injuryPersonMonitor:edit' union all
  select 3, 5951, 4, '删除', 'ygb:injuryPersonMonitor:remove' union all
  select 4, 5951, 5, '导出', 'ygb:injuryPersonMonitor:export' union all
  select 5, 5952, 1, '查询', 'ygb:injuryEmployerMonitor:query' union all
  select 6, 5952, 2, '新增', 'ygb:injuryEmployerMonitor:add' union all
  select 7, 5952, 3, '修改', 'ygb:injuryEmployerMonitor:edit' union all
  select 8, 5952, 4, '删除', 'ygb:injuryEmployerMonitor:remove' union all
  select 9, 5952, 5, '导出', 'ygb:injuryEmployerMonitor:export' union all
  select 10, 5953, 1, '查询', 'ygb:injuryRegionMonitor:query' union all
  select 11, 5953, 2, '新增', 'ygb:injuryRegionMonitor:add' union all
  select 12, 5953, 3, '修改', 'ygb:injuryRegionMonitor:edit' union all
  select 13, 5953, 4, '删除', 'ygb:injuryRegionMonitor:remove' union all
  select 14, 5953, 5, '导出', 'ygb:injuryRegionMonitor:export' union all
  select 15, 5954, 1, '查询', 'ygb:injuryOccHazardMonitor:query' union all
  select 16, 5954, 2, '新增', 'ygb:injuryOccHazardMonitor:add' union all
  select 17, 5954, 3, '修改', 'ygb:injuryOccHazardMonitor:edit' union all
  select 18, 5954, 4, '删除', 'ygb:injuryOccHazardMonitor:remove' union all
  select 19, 5954, 5, '导出', 'ygb:injuryOccHazardMonitor:export' union all
  select 20, 5955, 1, '查询', 'ygb:injuryNewformHazardMonitor:query' union all
  select 21, 5955, 2, '新增', 'ygb:injuryNewformHazardMonitor:add' union all
  select 22, 5955, 3, '修改', 'ygb:injuryNewformHazardMonitor:edit' union all
  select 23, 5955, 4, '删除', 'ygb:injuryNewformHazardMonitor:remove' union all
  select 24, 5955, 5, '导出', 'ygb:injuryNewformHazardMonitor:export' union all
  select 25, 5956, 1, '查询', 'ygb:injuryAccidentWarning:query' union all
  select 26, 5956, 2, '新增', 'ygb:injuryAccidentWarning:add' union all
  select 27, 5956, 3, '修改', 'ygb:injuryAccidentWarning:edit' union all
  select 28, 5956, 4, '删除', 'ygb:injuryAccidentWarning:remove' union all
  select 29, 5956, 5, '导出', 'ygb:injuryAccidentWarning:export' union all
  select 30, 5971, 1, '查询', 'ygb:deviceInstallOrder:query' union all
  select 31, 5971, 2, '新增', 'ygb:deviceInstallOrder:add' union all
  select 32, 5971, 3, '修改', 'ygb:deviceInstallOrder:edit' union all
  select 33, 5971, 4, '删除', 'ygb:deviceInstallOrder:remove' union all
  select 34, 5971, 5, '导出', 'ygb:deviceInstallOrder:export' union all
  select 35, 5972, 1, '查询', 'ygb:deviceRepairOrder:query' union all
  select 36, 5972, 2, '新增', 'ygb:deviceRepairOrder:add' union all
  select 37, 5972, 3, '修改', 'ygb:deviceRepairOrder:edit' union all
  select 38, 5972, 4, '删除', 'ygb:deviceRepairOrder:remove' union all
  select 39, 5972, 5, '导出', 'ygb:deviceRepairOrder:export' union all
  select 40, 5973, 1, '查询', 'ygb:deviceInspectPlan:query' union all
  select 41, 5973, 2, '新增', 'ygb:deviceInspectPlan:add' union all
  select 42, 5973, 3, '修改', 'ygb:deviceInspectPlan:edit' union all
  select 43, 5973, 4, '删除', 'ygb:deviceInspectPlan:remove' union all
  select 44, 5973, 5, '导出', 'ygb:deviceInspectPlan:export' union all
  select 45, 5974, 1, '查询', 'ygb:operationChipDispatch:query' union all
  select 46, 5974, 2, '新增', 'ygb:operationChipDispatch:add' union all
  select 47, 5974, 3, '修改', 'ygb:operationChipDispatch:edit' union all
  select 48, 5974, 4, '删除', 'ygb:operationChipDispatch:remove' union all
  select 49, 5974, 5, '导出', 'ygb:operationChipDispatch:export' union all
  select 50, 5977, 1, '查询', 'ygb:operationMaintenanceStats:query' union all
  select 51, 5977, 2, '新增', 'ygb:operationMaintenanceStats:add' union all
  select 52, 5977, 3, '修改', 'ygb:operationMaintenanceStats:edit' union all
  select 53, 5977, 4, '删除', 'ygb:operationMaintenanceStats:remove' union all
  select 54, 5977, 5, '导出', 'ygb:operationMaintenanceStats:export'
) n
where not exists (select 1 from sys_menu where menu_id = 5980 + n.id);

-- 4) Role bindings
insert ignore into sys_role_menu (role_id, menu_id)
select r.role_id, m.menu_id
from sys_role r
join sys_menu m on m.menu_id in (
  5951, 5952, 5953, 5954, 5955, 5956,
  5971, 5972, 5973, 5974, 5975, 5976, 5977
)
where r.role_id in (1, 2, 101, 102, 103, 104, 105);

insert ignore into sys_role_menu (role_id, menu_id)
select r.role_id, m.menu_id
from sys_role r
join sys_menu m on m.menu_id between 5980 and 6034
where r.role_id in (1, 2, 101, 102, 103, 104, 105);

-- 5) module_record seed samples
insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921020, 'INJURY_PERSON_MONITOR', '高处坠落轻伤案例监测', 'person', '2026-06', 'ygb', 'closed', '0', '440106',
  1002, '深圳鹏城机电工程有限公司', 10009, '何俊贤', 'IPM-202606-001', 1, '工伤监测',
  '{"injuryPart":"下肢","accidentType":"fall","disabilityLevel":"10"}',
  '工伤人员监测样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921020);

insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921021, 'INJURY_EMPLOYER_MONITOR', '制造行业用人单位工伤预防监测', 'employer', '2026-06', 'ygb', 'processing', '0', '440600',
  1003, '佛山顺德智造服务有限公司', 'IEM-202606-001', 1, '工伤监测',
  '{"insuredCount":860,"injuryCount":3,"fatalityCount":0,"injuryRate":3.49}',
  '用人单位监测样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921021);

insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921022, 'INJURY_REGION_MONITOR', '广州市工伤预防监测月报', 'region', '2026-06', 'ygb', 'closed', '0', '440100',
  'IRM-202606-001', 1, '工伤监测',
  '{"premiumIncome":12800000,"preventionExpense":960000,"projectCount":18,"regionInjuryRate":2.8}',
  '地区监测样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921022);

insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921023, 'INJURY_OCC_HAZARD_MONITOR', '噪声聋疑似职业病监测', 'occupation', '2026-06', 'ygb', 'pending', '0', '440300',
  1002, '深圳鹏城机电工程有限公司', 'IOH-202606-001', 1, '工伤监测',
  '{"diseaseType":"noise_deafness","caseCount":2,"industry":"制造"}',
  '职业病危害监测样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921023);

insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921024, 'INJURY_NEWFORM_HAZARD_MONITOR', '即时配送平台职业伤害监测', 'newform', '2026-06', 'ygb', 'processing', '0', '440100',
  1001, '广州南粤人力资源有限公司', 'INH-202606-001', 1, '工伤监测',
  '{"workerCount":1280,"injuryCount":6,"insuranceRate":91.2}',
  '新业态伤害监测样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921024);

insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921025, 'INJURY_ACCIDENT_WARNING', '夏季高温时段工伤风险预警', 'warning', '2026-06', 'ygb', 'pending', '0', '440000',
  'IAW-202606-001', 1, '工伤监测',
  '{"riskLevel":"high","timeWindow":"14:00-17:00","focusPost":"户外作业"}',
  '工伤事故预警样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921025);

insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921030, 'DEVICE_INSTALL_ORDER', '阳光劳务屏安装派单-天河项目', 'install', '2026-06', 'ygb', 'processing', '0', '440106',
  1001, '广州南粤人力资源有限公司', 'DIO-202606-001', 1, '设备安装运维',
  '{"deviceType":"attendance","installer":"运维一组","appointmentDate":"2026-06-18"}',
  '安装工单样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921030);

insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921031, 'DEVICE_REPAIR_ORDER', '芯片设备离线维修工单', 'repair', '2026-06', 'ygb', 'pending', '0', '440300',
  1002, '深圳鹏城机电工程有限公司', 'DRO-202606-001', 1, '设备安装运维',
  '{"faultType":"offline","priority":"high"}',
  '维修工单样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921031);

insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921032, 'DEVICE_INSPECT_PLAN', '6月珠三角设备巡检计划', 'inspect', '2026-06', 'ygb', 'closed', '0', '440000',
  'DIP-202606-001', 1, '设备安装运维',
  '{"planCycle":"monthly","teamCount":6,"coverageRate":92.5}',
  '巡检计划样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921032);

insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921033, 'OPERATION_CHIP_DISPATCH', '芯片批次领用-202606-A', 'chip_dispatch', '2026-06', 'ygb', 'closed', '0', '440100',
  1004, '广州安通劳务派遣有限公司', 'OCD-202606-001', 1, '设备安装运维',
  '{"batchNo":"CHIP-202606-A","quantity":120}',
  '芯片领用样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921033);

insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921034, 'OPERATION_MAINTENANCE_STATS', '6月全省设备运维统计', 'maintenance_stats', '2026-06', 'ygb', 'closed', '0', '440000',
  'OMS-202606-001', 1, '设备安装运维',
  '{"installCount":86,"repairCount":24,"inspectCount":132,"onlineRate":94.8}',
  '运维统计样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921034);
