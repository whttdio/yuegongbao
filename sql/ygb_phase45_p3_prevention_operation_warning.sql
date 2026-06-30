set names utf8mb4;

-- =============================================================================
-- Phase 45: P3 injury prevention sub-domains + operation gaps + warning/cockpit stats
-- =============================================================================

-- 1) Rename existing prevention project menu for 8.4.1 clarity
update sys_menu
set menu_name = '工伤预防项目',
    remark = '8.4.1 工伤预防项目管理（申报/立项/监督/验收/档案）'
where menu_id = 2016;

-- 2) Injury prevention sub-domain menus under 工伤与预防 (2060)
insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
) values
  (6051, '工伤预防宣传', 2060, 10, 'preventionPublicity', 'ygb/preventionPublicity/index', '', 'YgbPreventionPublicity', 1, 0, 'C', '0', '0', 'ygb:preventionPublicity:list', 'promotion', 'admin', sysdate(), 'admin', sysdate(), '8.4.2 工伤预防宣传', 'ygb'),
  (6052, '工伤预防培训', 2060, 11, 'preventionTraining', 'ygb/preventionTraining/index', '', 'YgbPreventionTraining', 1, 0, 'C', '0', '0', 'ygb:preventionTraining:list', 'reading', 'admin', sysdate(), 'admin', sysdate(), '8.4.3 工伤预防培训', 'ygb'),
  (6053, 'AI工伤预防', 2060, 12, 'preventionAi', 'ygb/preventionAi/index', '', 'YgbPreventionAi', 1, 0, 'C', '0', '0', 'ygb:preventionAi:list', 'cpu', 'admin', sysdate(), 'admin', sysdate(), '8.4.4 AI工伤预防', 'ygb'),
  (6054, '工伤预防费用', 2060, 13, 'preventionFundSafety', 'ygb/preventionFund/index', '', 'YgbPreventionFundSafety', 1, 0, 'C', '0', '0', 'ygb:preventionFund:list', 'money', 'admin', sysdate(), 'admin', sysdate(), '8.4.5 工伤预防费用管理（复用资金池）', 'ygb')
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

-- 3) Warning statistics page under 预警治理 (2080)
insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
) values
  (6061, '预警统计分析', 2080, 3, 'warningStat', 'ygb/warningStat/index', '', 'YgbWarningStat', 1, 0, 'C', '0', '0', 'ygb:warning:list', 'data-analysis', 'admin', sysdate(), 'admin', sysdate(), '2.3 预警统计分析', 'ygb')
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

-- 4) Cockpit trend analysis under 综合驾驶舱 (3900)
insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
) values
  (6071, '趋势分析', 3900, 2, 'cockpitTrend', 'ygb/cockpitTrend/index', '', 'YgbCockpitTrend', 1, 0, 'C', '0', '0', 'ygb:cockpit:list', 'trend-charts', 'admin', sysdate(), 'admin', sysdate(), '驾驶舱趋势分析独立页', 'ygb')
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

-- 5) Operation gaps: job category (19.5) + recruit stats (19.7)
insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
) values
  (6072, '职位分类管理', 4300, 7, 'operationJobCategory', 'ygb/operation/operationJobCategory/index', '', 'YgbOperationJobCategory', 1, 0, 'C', '0', '0', 'ygb:operationJobCategory:list', 'collection', 'admin', sysdate(), 'admin', sysdate(), '19.5 职位分类管理', 'ygb'),
  (6073, '招聘数据统计', 4300, 8, 'operationRecruitStats', 'ygb/operation/operationRecruitStats/index', '', 'YgbOperationRecruitStats', 1, 0, 'C', '0', '0', 'ygb:operationRecruitStats:list', 'data-line', 'admin', sysdate(), 'admin', sysdate(), '19.7 数据统计与分析', 'ygb')
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

-- 6) AZB symmetric entries
insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
) values
  (6081, '预警统计分析', 5610, 3, 'warningStat', 'azb/warningStat/index', '', 'AzbWarningStat', 1, 0, 'C', '0', '0', 'ygb:warning:list', 'data-analysis', 'admin', sysdate(), 'admin', sysdate(), 'AZB 预警统计分析', 'azb'),
  (6082, '趋势分析', 5630, 3, 'cockpitTrend', 'azb/cockpitTrend/index', '', 'AzbCockpitTrend', 1, 0, 'C', '0', '0', 'ygb:cockpit:list', 'trend-charts', 'admin', sysdate(), 'admin', sysdate(), 'AZB 驾驶舱趋势分析', 'azb')
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

-- 7) Button permissions (6100-6149)
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark, portal_scope)
select 6100 + n.id, n.btn_name, n.parent_id, n.order_num, '', '', '', '', 1, 0, 'F', '0', '0', n.perms, '#', 'admin', sysdate(), '', 'ygb'
from (
  select 0 as id, 6051 as parent_id, 1 as order_num, '查询' as btn_name, 'ygb:preventionPublicity:query' as perms union all
  select 1, 6051, 2, '新增', 'ygb:preventionPublicity:add' union all
  select 2, 6051, 3, '修改', 'ygb:preventionPublicity:edit' union all
  select 3, 6051, 4, '删除', 'ygb:preventionPublicity:remove' union all
  select 4, 6051, 5, '导出', 'ygb:preventionPublicity:export' union all
  select 5, 6052, 1, '查询', 'ygb:preventionTraining:query' union all
  select 6, 6052, 2, '新增', 'ygb:preventionTraining:add' union all
  select 7, 6052, 3, '修改', 'ygb:preventionTraining:edit' union all
  select 8, 6052, 4, '删除', 'ygb:preventionTraining:remove' union all
  select 9, 6052, 5, '导出', 'ygb:preventionTraining:export' union all
  select 10, 6053, 1, '查询', 'ygb:preventionAi:query' union all
  select 11, 6053, 2, '新增', 'ygb:preventionAi:add' union all
  select 12, 6053, 3, '修改', 'ygb:preventionAi:edit' union all
  select 13, 6053, 4, '删除', 'ygb:preventionAi:remove' union all
  select 14, 6053, 5, '导出', 'ygb:preventionAi:export' union all
  select 15, 6072, 1, '查询', 'ygb:operationJobCategory:query' union all
  select 16, 6072, 2, '新增', 'ygb:operationJobCategory:add' union all
  select 17, 6072, 3, '修改', 'ygb:operationJobCategory:edit' union all
  select 18, 6072, 4, '删除', 'ygb:operationJobCategory:remove' union all
  select 19, 6072, 5, '导出', 'ygb:operationJobCategory:export' union all
  select 20, 6073, 1, '查询', 'ygb:operationRecruitStats:query' union all
  select 21, 6073, 2, '新增', 'ygb:operationRecruitStats:add' union all
  select 22, 6073, 3, '修改', 'ygb:operationRecruitStats:edit' union all
  select 23, 6073, 4, '删除', 'ygb:operationRecruitStats:remove' union all
  select 24, 6073, 5, '导出', 'ygb:operationRecruitStats:export'
) n
where not exists (select 1 from sys_menu where menu_id = 6100 + n.id);

-- 8) Role bindings
insert ignore into sys_role_menu (role_id, menu_id)
select r.role_id, m.menu_id
from sys_role r
join sys_menu m on m.menu_id in (
  6051, 6052, 6053, 6054, 6061, 6071, 6072, 6073, 6081, 6082
)
where r.role_id in (1, 2, 101, 102, 103, 104, 105);

insert ignore into sys_role_menu (role_id, menu_id)
select r.role_id, m.menu_id
from sys_role r
join sys_menu m on m.menu_id between 6100 and 6124
where r.role_id in (1, 2, 101, 102, 103, 104, 105);

-- 9) module_record seed samples
insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921040, 'PREVENTION_PUBLICITY', '2026年安全生产月主题宣传', 'publicity', '2026-06', 'ygb', 'closed', '0', '440100',
  1001, '广州南粤人力资源有限公司', 'PP-202606-001', 1, '工伤预防',
  '{"activityType":"safety_month","materialCount":18,"caseCount":6}',
  '工伤预防宣传样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921040);

insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921041, 'PREVENTION_TRAINING', '高处作业重点岗位培训', 'training', '2026-06', 'ygb', 'processing', '0', '440106',
  1002, '深圳鹏城机电工程有限公司', 10009, '何俊贤', 'PT-202606-001', 1, '工伤预防',
  '{"courseHours":8,"passRate":92.5,"postType":"height_work"}',
  '工伤预防培训样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921041);

insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921042, 'PREVENTION_AI', '制造行业工伤风险智能评审', 'ai', '2026-06', 'ygb', 'pending', '0', '440600',
  1003, '佛山顺德智造服务有限公司', 'PA-202606-001', 1, '工伤预防',
  '{"modelVersion":"v2.1","riskScore":78,"reviewStatus":"pending"}',
  'AI工伤预防样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921042);

insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921043, 'OPERATION_JOB_CATEGORY', '电工/焊工/普工分类字典', 'job_category', '2026-06', 'ygb', 'closed', '0', '440000',
  'OJC-202606-001', 1, '运营后台',
  '{"categoryCount":36,"salaryRange":"3000-12000","workType":"full_time"}',
  '职位分类样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921043);

insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921044, 'OPERATION_RECRUIT_STATS', '6月招聘市场运营统计', 'recruit_stats', '2026-06', 'ygb', 'closed', '0', '440000',
  'ORS-202606-001', 1, '运营后台',
  '{"jobPublishCount":1280,"resumeCount":3560,"matchRate":34.6,"userGrowthRate":8.2}',
  '招聘数据统计样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921044);
