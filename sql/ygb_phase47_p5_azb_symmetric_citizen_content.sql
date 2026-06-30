set names utf8mb4;

-- =============================================================================
-- Phase 47: P5 AZB symmetric menus + citizen service CMS seeds
-- =============================================================================

-- 1) AZB stat report menus (6250-6254) under 4920 — mirror YGB P1 additions
insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
) values
  (6250, '新业态治理月报', 4920, 7, 'statReport/newform', 'azb/statReport/newform/index', '', 'AzbStatReportNewform', 1, 0, 'C', '0', '0', 'azb:statReport:newform:query', 'table', 'admin', sysdate(), 'admin', sysdate(), 'AZB 新业态治理月报', 'azb'),
  (6251, '职业病治理月报', 4920, 8, 'statReport/occupation', 'azb/statReport/occupation/index', '', 'AzbStatReportOccupation', 1, 0, 'C', '0', '0', 'azb:statReport:occupation:query', 'table', 'admin', sysdate(), 'admin', sysdate(), 'AZB 职业病治理月报', 'azb'),
  (6252, '工会监督月报', 4920, 9, 'statReport/union', 'azb/statReport/union/index', '', 'AzbStatReportUnion', 1, 0, 'C', '0', '0', 'azb:statReport:union:query', 'table', 'admin', sysdate(), 'admin', sysdate(), 'AZB 工会监督月报', 'azb'),
  (6253, '自定义报表', 4920, 10, 'statReport/custom', 'azb/statReport/custom/index', '', 'AzbStatReportCustom', 1, 0, 'C', '0', '0', 'azb:statReport:custom:query', 'table', 'admin', sysdate(), 'admin', sysdate(), 'AZB 自定义报表', 'azb'),
  (6254, '扩面减损月报', 4920, 11, 'statReport/expansion', 'azb/statReport/expansion/index', '', 'AzbStatReportExpansion', 1, 0, 'C', '0', '0', 'azb:statReport:expansion:query', 'table', 'admin', sysdate(), 'admin', sysdate(), 'AZB 扩面减损月报', 'azb')
on duplicate key update
  menu_name = values(menu_name),
  parent_id = values(parent_id),
  order_num = values(order_num),
  path = values(path),
  component = values(component),
  route_name = values(route_name),
  perms = values(perms),
  portal_scope = values(portal_scope),
  remark = values(remark);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark, portal_scope) values
  (6260, '查询', 6250, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'azb:statReport:newform:query', '#', 'admin', sysdate(), '', 'azb'),
  (6261, '生成', 6250, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'azb:statReport:newform:generate', '#', 'admin', sysdate(), '', 'azb'),
  (6262, '导出', 6250, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'azb:statReport:newform:export', '#', 'admin', sysdate(), '', 'azb'),
  (6263, '查询', 6251, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'azb:statReport:occupation:query', '#', 'admin', sysdate(), '', 'azb'),
  (6264, '生成', 6251, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'azb:statReport:occupation:generate', '#', 'admin', sysdate(), '', 'azb'),
  (6265, '导出', 6251, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'azb:statReport:occupation:export', '#', 'admin', sysdate(), '', 'azb'),
  (6266, '查询', 6252, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'azb:statReport:union:query', '#', 'admin', sysdate(), '', 'azb'),
  (6267, '生成', 6252, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'azb:statReport:union:generate', '#', 'admin', sysdate(), '', 'azb'),
  (6268, '导出', 6252, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'azb:statReport:union:export', '#', 'admin', sysdate(), '', 'azb'),
  (6269, '查询', 6253, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'azb:statReport:custom:query', '#', 'admin', sysdate(), '', 'azb'),
  (6270, '生成', 6253, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'azb:statReport:custom:generate', '#', 'admin', sysdate(), '', 'azb'),
  (6271, '导出', 6253, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'azb:statReport:custom:export', '#', 'admin', sysdate(), '', 'azb'),
  (6272, '查询', 6254, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'azb:statReport:expansion:query', '#', 'admin', sysdate(), '', 'azb'),
  (6273, '生成', 6254, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'azb:statReport:expansion:generate', '#', 'admin', sysdate(), '', 'azb'),
  (6274, '导出', 6254, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'azb:statReport:expansion:export', '#', 'admin', sysdate(), '', 'azb')
on duplicate key update menu_name = values(menu_name), parent_id = values(parent_id), perms = values(perms), portal_scope = values(portal_scope);

-- 2) AZB injury prevention sub-domain menus (6275-6277) under 5650 安责险协同
insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
) values
  (6275, '工伤预防宣传', 5650, 5, 'preventionPublicity', 'azb/preventionPublicity/index', '', 'AzbPreventionPublicity', 1, 0, 'C', '0', '0', 'ygb:preventionPublicity:list', 'promotion', 'admin', sysdate(), 'admin', sysdate(), 'AZB 工伤预防宣传（复用统一台账）', 'azb'),
  (6276, '工伤预防培训', 5650, 6, 'preventionTraining', 'azb/preventionTraining/index', '', 'AzbPreventionTraining', 1, 0, 'C', '0', '0', 'ygb:preventionTraining:list', 'reading', 'admin', sysdate(), 'admin', sysdate(), 'AZB 工伤预防培训（复用统一台账）', 'azb'),
  (6277, 'AI工伤预防', 5650, 7, 'preventionAi', 'azb/preventionAi/index', '', 'AzbPreventionAi', 1, 0, 'C', '0', '0', 'ygb:preventionAi:list', 'cpu', 'admin', sysdate(), 'admin', sysdate(), 'AZB AI工伤预防（复用统一台账）', 'azb')
on duplicate key update
  menu_name = values(menu_name),
  parent_id = values(parent_id),
  order_num = values(order_num),
  path = values(path),
  component = values(component),
  route_name = values(route_name),
  perms = values(perms),
  portal_scope = values(portal_scope),
  remark = values(remark);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark, portal_scope)
select 6280 + n.id, n.btn_name, n.parent_id, n.order_num, '', '', '', '', 1, 0, 'F', '0', '0', n.perms, '#', 'admin', sysdate(), '', 'azb'
from (
  select 0 as id, 6275 as parent_id, 1 as order_num, '查询' as btn_name, 'ygb:preventionPublicity:query' as perms union all
  select 1, 6275, 2, '新增', 'ygb:preventionPublicity:add' union all
  select 2, 6275, 3, '修改', 'ygb:preventionPublicity:edit' union all
  select 3, 6275, 4, '删除', 'ygb:preventionPublicity:remove' union all
  select 4, 6275, 5, '导出', 'ygb:preventionPublicity:export' union all
  select 5, 6276, 1, '查询', 'ygb:preventionTraining:query' union all
  select 6, 6276, 2, '新增', 'ygb:preventionTraining:add' union all
  select 7, 6276, 3, '修改', 'ygb:preventionTraining:edit' union all
  select 8, 6276, 4, '删除', 'ygb:preventionTraining:remove' union all
  select 9, 6276, 5, '导出', 'ygb:preventionTraining:export' union all
  select 10, 6277, 1, '查询', 'ygb:preventionAi:query' union all
  select 11, 6277, 2, '新增', 'ygb:preventionAi:add' union all
  select 12, 6277, 3, '修改', 'ygb:preventionAi:edit' union all
  select 13, 6277, 4, '删除', 'ygb:preventionAi:remove' union all
  select 14, 6277, 5, '导出', 'ygb:preventionAi:export'
) n
where not exists (select 1 from sys_menu where menu_id = 6280 + n.id);

-- 3) AZB device maintenance menus (6295-6298) under 5620 设备安全治理
insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
) values
  (6295, '设备安装工单', 5620, 10, 'deviceInstallOrder', 'azb/operation/deviceInstallOrder/index', '', 'AzbDeviceInstallOrder', 1, 0, 'C', '0', '0', 'ygb:deviceInstallOrder:list', 'tool', 'admin', sysdate(), 'admin', sysdate(), 'AZB 设备安装工单（复用统一台账）', 'azb'),
  (6296, '设备维修工单', 5620, 11, 'deviceRepairOrder', 'azb/operation/deviceRepairOrder/index', '', 'AzbDeviceRepairOrder', 1, 0, 'C', '0', '0', 'ygb:deviceRepairOrder:list', 'tools', 'admin', sysdate(), 'admin', sysdate(), 'AZB 设备维修工单（复用统一台账）', 'azb'),
  (6297, '设备巡检计划', 5620, 12, 'deviceInspectPlan', 'azb/operation/deviceInspectPlan/index', '', 'AzbDeviceInspectPlan', 1, 0, 'C', '0', '0', 'ygb:deviceInspectPlan:list', 'date', 'admin', sysdate(), 'admin', sysdate(), 'AZB 设备巡检计划（复用统一台账）', 'azb'),
  (6298, '运维统计分析', 5620, 13, 'operationMaintenanceStats', 'azb/operation/operationMaintenanceStats/index', '', 'AzbOperationMaintenanceStats', 1, 0, 'C', '0', '0', 'ygb:operationMaintenanceStats:list', 'data-analysis', 'admin', sysdate(), 'admin', sysdate(), 'AZB 运维统计分析（复用统一台账）', 'azb')
on duplicate key update
  menu_name = values(menu_name),
  parent_id = values(parent_id),
  order_num = values(order_num),
  path = values(path),
  component = values(component),
  route_name = values(route_name),
  perms = values(perms),
  portal_scope = values(portal_scope),
  remark = values(remark);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark, portal_scope)
select 6300 + n.id, n.btn_name, n.parent_id, n.order_num, '', '', '', '', 1, 0, 'F', '0', '0', n.perms, '#', 'admin', sysdate(), '', 'azb'
from (
  select 0 as id, 6295 as parent_id, 1 as order_num, '查询' as btn_name, 'ygb:deviceInstallOrder:query' as perms union all
  select 1, 6295, 2, '新增', 'ygb:deviceInstallOrder:add' union all
  select 2, 6295, 3, '修改', 'ygb:deviceInstallOrder:edit' union all
  select 3, 6295, 4, '删除', 'ygb:deviceInstallOrder:remove' union all
  select 4, 6295, 5, '导出', 'ygb:deviceInstallOrder:export' union all
  select 5, 6296, 1, '查询', 'ygb:deviceRepairOrder:query' union all
  select 6, 6296, 2, '新增', 'ygb:deviceRepairOrder:add' union all
  select 7, 6296, 3, '修改', 'ygb:deviceRepairOrder:edit' union all
  select 8, 6296, 4, '删除', 'ygb:deviceRepairOrder:remove' union all
  select 9, 6296, 5, '导出', 'ygb:deviceRepairOrder:export' union all
  select 10, 6297, 1, '查询', 'ygb:deviceInspectPlan:query' union all
  select 11, 6297, 2, '新增', 'ygb:deviceInspectPlan:add' union all
  select 12, 6297, 3, '修改', 'ygb:deviceInspectPlan:edit' union all
  select 13, 6297, 4, '删除', 'ygb:deviceInspectPlan:remove' union all
  select 14, 6297, 5, '导出', 'ygb:deviceInspectPlan:export' union all
  select 15, 6298, 1, '查询', 'ygb:operationMaintenanceStats:query' union all
  select 16, 6298, 2, '新增', 'ygb:operationMaintenanceStats:add' union all
  select 17, 6298, 3, '修改', 'ygb:operationMaintenanceStats:edit' union all
  select 18, 6298, 4, '删除', 'ygb:operationMaintenanceStats:remove' union all
  select 19, 6298, 5, '导出', 'ygb:operationMaintenanceStats:export'
) n
where not exists (select 1 from sys_menu where menu_id = 6300 + n.id);

-- 4) Role bindings (AZB roles)
insert ignore into sys_role_menu (role_id, menu_id)
select r.role_id, m.menu_id
from sys_role r
join sys_menu m on m.menu_id in (
  6250, 6251, 6252, 6253, 6254,
  6260, 6261, 6262, 6263, 6264, 6265, 6266, 6267, 6268, 6269, 6270, 6271, 6272, 6273, 6274,
  6275, 6276, 6277,
  6295, 6296, 6297, 6298
)
where r.role_id in (1, 2, 107, 108, 109);

insert ignore into sys_role_menu (role_id, menu_id)
select r.role_id, m.menu_id
from sys_role r
join sys_menu m on m.menu_id between 6280 and 6294
where r.role_id in (1, 2, 107, 108, 109);

insert ignore into sys_role_menu (role_id, menu_id)
select r.role_id, m.menu_id
from sys_role r
join sys_menu m on m.menu_id between 6300 and 6319
where r.role_id in (1, 2, 107, 108, 109);

-- 5) Citizen service CMS seeds (21.x 便民服务栏目)
delete from ygb_portal_content
where portal_code = 'ygb'
  and section_code in ('warm_map', 'training_course', 'law_library', 'mutual_help', 'recruit_market')
  and create_by = 'citizen_seed';

insert into ygb_portal_content (
  portal_code, section_code, category_code, title, summary, content, cover_url, link_url, source_name,
  publish_time, sort_order, status, extra_json, create_by, create_time, del_flag
) values
(
  'ygb', 'warm_map', 'station-1', '天河区暖新驿站（体育西）',
  '提供休息、饮水、充电、应急药箱等基础服务，面向新业态劳动者开放。',
  '驿站位于体育西路地铁口附近，工作日 9:00-21:00 开放，可现场登记使用。',
  '', '', 'citizen_seed',
  sysdate(), 10, '0',
  '{"address":"广州市天河区体育西路","lat":23.132,"lng":113.321,"serviceHours":"9:00-21:00","tags":["休息","充电","应急药箱"]}',
  'citizen_seed', sysdate(), '0'
),
(
  'ygb', 'warm_map', 'station-2', '番禺区暖新驿站（市桥）',
  '面向快递、外卖、网约车等群体提供临时休息与政策咨询指引。',
  '驿站提供政策宣传折页、休息座椅和免费 Wi-Fi，周末照常开放。',
  '', '', 'citizen_seed',
  sysdate(), 20, '0',
  '{"address":"广州市番禺区市桥街道","lat":22.937,"lng":113.384,"serviceHours":"8:30-20:30","tags":["政策咨询","Wi-Fi","休息"]}',
  'citizen_seed', sysdate(), '0'
),
(
  'ygb', 'training_course', 'course-public-1', '新业态劳动者权益保护入门',
  '介绍劳动合同、工资支付、社保参保等基础权益知识。',
  '课程适合新入职劳动者快速了解基础权益与维权渠道。',
  '', '', 'citizen_seed',
  sysdate(), 10, '0',
  '{"durationMinutes":45,"level":"入门","instructor":"市总工会讲师团"}',
  'citizen_seed', sysdate(), '0'
),
(
  'ygb', 'training_course', 'course-public-2', '工伤预防与现场留痕实务',
  '结合真实案例讲解班前检查、隐患上报和工伤报案留痕要点。',
  '课程强调现场可执行动作，适合一线作业人员复训。',
  '', '', 'citizen_seed',
  sysdate(), 20, '0',
  '{"durationMinutes":60,"level":"进阶","instructor":"市应急管理局讲师"}',
  'citizen_seed', sysdate(), '0'
),
(
  'ygb', 'law_library', 'law-1', '广东省工伤保险条例（节选）',
  '工伤认定、待遇项目和申请流程相关条款摘要。',
  '供劳动者和基层工会快速查阅工伤认定与待遇申请依据。',
  '', '', 'citizen_seed',
  sysdate(), 10, '0',
  '{"docType":"regulation","effectiveDate":"2024-01-01"}',
  'citizen_seed', sysdate(), '0'
),
(
  'ygb', 'law_library', 'law-2', '劳动合同法（维权要点）',
  '围绕合同签订、变更、解除和工资支付等高频争议点整理。',
  '帮助劳动者理解合同条款与常见维权路径。',
  '', '', 'citizen_seed',
  sysdate(), 20, '0',
  '{"docType":"regulation","effectiveDate":"2013-07-01"}',
  'citizen_seed', sysdate(), '0'
),
(
  'ygb', 'mutual_help', 'topic-1', '高温作业防护经验分享',
  '一线工友分享防暑降温、补水轮换和异常停工经验。',
  '互助帖仅供经验交流，遇紧急风险请先停工并上报。',
  '', '', 'citizen_seed',
  sysdate(), 10, '0',
  '{"topicType":"experience","replyCount":12}',
  'citizen_seed', sysdate(), '0'
),
(
  'ygb', 'mutual_help', 'topic-2', '工伤报案材料清单互助',
  '汇总病历、考勤、现场照片和证人信息等常见材料清单。',
  '可在评论区补充各地实际办理差异，便于新工友参考。',
  '', '', 'citizen_seed',
  sysdate(), 20, '0',
  '{"topicType":"checklist","replyCount":8}',
  'citizen_seed', sysdate(), '0'
),
(
  'ygb', 'recruit_market', 'job-1', '仓储分拣员（天河）',
  '月结岗位，提供岗前安全培训和工伤保险保障说明。',
  '岗位要求：18-45 岁，能适应站立作业；提供岗前培训与考勤设备支持。',
  '', '', 'citizen_seed',
  sysdate(), 10, '0',
  '{"salaryRange":"5000-6500","regionCode":"440106","employerType":"dispatch"}',
  'citizen_seed', sysdate(), '0'
),
(
  'ygb', 'recruit_market', 'job-2', '设备运维辅助岗（番禺）',
  '协助设备巡检与现场留痕，适合有制造业经验的劳动者。',
  '岗位强调安全培训和持证上岗，入职前需完成平台基础培训。',
  '', '', 'citizen_seed',
  sysdate(), 20, '0',
  '{"salaryRange":"5500-7000","regionCode":"440113","employerType":"enterprise"}',
  'citizen_seed', sysdate(), '0'
);

-- 6) module_record seed samples for AZB prevention/device ops visibility
insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921060, 'PREVENTION_PUBLICITY', '6月安责险协同预防宣传', 'publicity', '2026-06', 'azb', 'closed', '0', '440100',
  1001, '广州南粤人力资源有限公司', 'PP-AZB-202606-001', 1, '安责险协同',
  '{"channel":"offline","audienceCount":320}',
  'AZB 预防宣传样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921060);

insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921061, 'DEVICE_INSTALL_ORDER', '番禺项目设备批量安装', 'install', '2026-06', 'azb', 'processing', '0', '440113',
  1003, '佛山顺德智造服务有限公司', 'DIO-AZB-202606-001', 1, '设备安全治理',
  '{"deviceCount":18,"plannedDate":"2026-06-18"}',
  'AZB 安装工单样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921061);
