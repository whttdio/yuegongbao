set names utf8mb4;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4470, '平台企业聚合', 4000, 2, 'newformPlatform', 'ygb/newformPlatform/index', '', 'YgbNewformPlatform',
  1, 0, 'C', '0', '0', 'ygb:newformPlatform:list', 'data-board',
  'admin', sysdate(), 'admin', sysdate(), '新业态平台企业聚合子页', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4470);

insert into sys_menu select 4471, '职业伤害监测', 4000, 3, 'newformInjuryMonitor', 'ygb/newformInjuryMonitor/index', '', 'YgbNewformInjuryMonitor', 1, 0, 'C', '0', '0', 'ygb:newformInjuryMonitor:list', 'warning', 'admin', sysdate(), 'admin', sysdate(), '新业态职业伤害监测子页', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4471);

insert into sys_menu select 4472, '培训管理', 4000, 4, 'newformTraining', 'ygb/newformTraining/index', '', 'YgbNewformTraining', 1, 0, 'C', '0', '0', 'ygb:newformTraining:list', 'reading', 'admin', sysdate(), 'admin', sysdate(), '新业态培训管理台账', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4472);

insert into sys_menu select 4473, '查询', 4470, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:newformPlatform:query', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4473);
insert into sys_menu select 4474, '导出', 4470, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:newformPlatform:export', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4474);

insert into sys_menu select 4475, '查询', 4471, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:newformInjuryMonitor:query', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4475);
insert into sys_menu select 4476, '导出', 4471, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:newformInjuryMonitor:export', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4476);

insert into sys_menu select 4477, '查询', 4472, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:newformTraining:query', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4477);
insert into sys_menu select 4478, '新增', 4472, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:newformTraining:add', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4478);
insert into sys_menu select 4479, '修改', 4472, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:newformTraining:edit', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4479);
insert into sys_menu select 4480, '删除', 4472, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:newformTraining:remove', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4480);
insert into sys_menu select 4481, '导出', 4472, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:newformTraining:export', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4481);

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4482, '职业病预防项目', 4020, 2, 'occupationPrevention', 'ygb/occupationPrevention/index', '', 'YgbOccupationPrevention',
  1, 0, 'C', '0', '0', 'ygb:occupationPrevention:list', 'collection',
  'admin', sysdate(), 'admin', sysdate(), '职业病预防项目台账', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4482);

insert into sys_menu select 4483, '职业健康档案', 4020, 3, 'occupationHealthArchive', 'ygb/occupationHealthArchive/index', '', 'YgbOccupationHealthArchive', 1, 0, 'C', '0', '0', 'ygb:occupationHealthArchive:list', 'folder-opened', 'admin', sysdate(), 'admin', sysdate(), '职业健康档案台账', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4483);

insert into sys_menu select 4484, '查询', 4482, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:occupationPrevention:query', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4484);
insert into sys_menu select 4485, '导出', 4482, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:occupationPrevention:export', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4485);

insert into sys_menu select 4486, '查询', 4483, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:occupationHealthArchive:query', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4486);
insert into sys_menu select 4487, '导出', 4483, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:occupationHealthArchive:export', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4487);

update sys_menu
set portal_scope = 'ygb'
where menu_id in (4470, 4471, 4472, 4473, 4474, 4475, 4476, 4477, 4478, 4479, 4480, 4481, 4482, 4483, 4484, 4485, 4486, 4487);

insert into sys_role_menu (role_id, menu_id, portal_scope)
select r.role_id, m.menu_id, 'ygb'
from sys_role r
join sys_menu m on m.menu_id in (4470, 4471, 4472, 4473, 4474, 4475, 4476, 4477, 4478, 4479, 4480, 4481, 4482, 4483, 4484, 4485, 4486, 4487)
where r.role_key in ('admin', 'ygb_hrss_supervisor', 'ygb_enterprise_admin', 'ygb_enterprise_operator')
on duplicate key update portal_scope = values(portal_scope);

insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, sort_order, source_label,
  payload_json, remark, create_by, create_time, del_flag
)
select
  920001, 'NEWFORM_TRAINING', '灵活用工岗前参保培训', 'training', '2026-06', 'ygb', 'pending', '0', '440106',
  1001, '广州南粤人力资源有限公司', null, '', null, 'NF-TR-202606-001', 1, '新业态监管',
  '{"platformName":"即时配送平台","courseHours":4,"warningLevel":"1","channel":"internal"}',
  '新业态培训管理初始化样例', 'admin', sysdate(), '0'
from dual
where not exists (select 1 from ygb_module_record where record_id = 920001);

insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, sort_order, source_label,
  payload_json, remark, create_by, create_time, del_flag
)
select
  920002, 'OCCUPATION_PREVENTION', '喷涂车间职业病预防专项', 'prevention', '2026-06', 'ygb', 'processing', '0', '440305',
  1002, '深圳鹏城机电工程有限公司', null, '', null, 'OCC-PV-202606-001', 1, '职业病监管',
  '{"industryType":"制造加工","warningLevel":"2","projectStage":"inspection"}',
  '职业病预防项目初始化样例', 'admin', sysdate(), '0'
from dual
where not exists (select 1 from ygb_module_record where record_id = 920002);

insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, sort_order, source_label,
  payload_json, remark, create_by, create_time, del_flag
)
select
  920003, 'OCCUPATION_HEALTH_ARCHIVE', '焊接岗位年度职业健康档案', 'health_archive', '2026-06', 'ygb', 'closed', '0', '440606',
  1003, '佛山顺德智造服务有限公司', null, '', null, 'OCC-HA-202606-001', 1, '职业病监管',
  '{"industryType":"建筑施工","warningLevel":"1","archiveYear":"2026"}',
  '职业健康档案初始化样例', 'admin', sysdate(), '0'
from dual
where not exists (select 1 from ygb_module_record where record_id = 920003);
