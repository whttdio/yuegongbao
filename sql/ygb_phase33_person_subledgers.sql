set names utf8mb4;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4490, '特证管理', 2002, 20, 'personCertificate', 'ygb/personCertificate/index', '', 'YgbPersonCertificate',
  1, 0, 'C', '0', '0', 'ygb:personCertificate:list', 'tickets',
  'admin', sysdate(), 'admin', sysdate(), '人员特证管理子台账', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4490);

insert into sys_menu select 4491, '黑名单', 2002, 21, 'personBlacklist', 'ygb/personBlacklist/index', '', 'YgbPersonBlacklist', 1, 0, 'C', '0', '0', 'ygb:personBlacklist:list', 'warning-filled', 'admin', sysdate(), 'admin', sysdate(), '人员黑名单子台账', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4491);

insert into sys_menu select 4492, '培训监督', 2002, 22, 'personTraining', 'ygb/personTraining/index', '', 'YgbPersonTraining', 1, 0, 'C', '0', '0', 'ygb:personTraining:list', 'reading', 'admin', sysdate(), 'admin', sysdate(), '人员培训监督子台账', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4492);

insert into sys_menu select 4493, '高危岗位库', 2002, 23, 'personHighRiskPost', 'ygb/personHighRiskPost/index', '', 'YgbPersonHighRiskPost', 1, 0, 'C', '0', '0', 'ygb:personHighRiskPost:list', 'histogram', 'admin', sysdate(), 'admin', sysdate(), '人员高危岗位子台账', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4493);

insert into sys_menu select 4494, '风险岗位库', 2002, 24, 'personRiskPost', 'ygb/personRiskPost/index', '', 'YgbPersonRiskPost', 1, 0, 'C', '0', '0', 'ygb:personRiskPost:list', 'data-line', 'admin', sysdate(), 'admin', sysdate(), '人员风险岗位子台账', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4494);

insert into sys_menu select 4495, '专家库', 2002, 25, 'personExpert', 'ygb/personExpert/index', '', 'YgbPersonExpert', 1, 0, 'C', '0', '0', 'ygb:personExpert:list', 'user', 'admin', sysdate(), 'admin', sysdate(), '人员专家库子台账', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4495);

insert into sys_menu select 4496, '查询', 4490, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personCertificate:query', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4496);
insert into sys_menu select 4497, '新增', 4490, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personCertificate:add', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4497);
insert into sys_menu select 4498, '修改', 4490, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personCertificate:edit', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4498);
insert into sys_menu select 4499, '删除', 4490, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personCertificate:remove', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4499);
insert into sys_menu select 4500, '导出', 4490, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personCertificate:export', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4500);

insert into sys_menu select 4501, '查询', 4491, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personBlacklist:query', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4501);
insert into sys_menu select 4502, '新增', 4491, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personBlacklist:add', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4502);
insert into sys_menu select 4503, '修改', 4491, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personBlacklist:edit', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4503);
insert into sys_menu select 4504, '删除', 4491, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personBlacklist:remove', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4504);
insert into sys_menu select 4505, '导出', 4491, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personBlacklist:export', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4505);

insert into sys_menu select 4506, '查询', 4492, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personTraining:query', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4506);
insert into sys_menu select 4507, '新增', 4492, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personTraining:add', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4507);
insert into sys_menu select 4508, '修改', 4492, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personTraining:edit', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4508);
insert into sys_menu select 4509, '删除', 4492, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personTraining:remove', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4509);
insert into sys_menu select 4510, '导出', 4492, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personTraining:export', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4510);

insert into sys_menu select 4511, '查询', 4493, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personHighRiskPost:query', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4511);
insert into sys_menu select 4512, '新增', 4493, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personHighRiskPost:add', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4512);
insert into sys_menu select 4513, '修改', 4493, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personHighRiskPost:edit', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4513);
insert into sys_menu select 4514, '删除', 4493, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personHighRiskPost:remove', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4514);
insert into sys_menu select 4515, '导出', 4493, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personHighRiskPost:export', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4515);

insert into sys_menu select 4516, '查询', 4494, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personRiskPost:query', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4516);
insert into sys_menu select 4517, '新增', 4494, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personRiskPost:add', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4517);
insert into sys_menu select 4518, '修改', 4494, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personRiskPost:edit', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4518);
insert into sys_menu select 4519, '删除', 4494, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personRiskPost:remove', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4519);
insert into sys_menu select 4520, '导出', 4494, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personRiskPost:export', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4520);

insert into sys_menu select 4521, '查询', 4495, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personExpert:query', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4521);
insert into sys_menu select 4522, '新增', 4495, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personExpert:add', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4522);
insert into sys_menu select 4523, '修改', 4495, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personExpert:edit', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4523);
insert into sys_menu select 4524, '删除', 4495, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personExpert:remove', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4524);
insert into sys_menu select 4525, '导出', 4495, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:personExpert:export', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4525);

update sys_menu
set portal_scope = 'ygb'
where menu_id between 4490 and 4525;

insert into sys_role_menu (role_id, menu_id, portal_scope)
select r.role_id, m.menu_id, 'ygb'
from sys_role r
join sys_menu m on m.menu_id between 4490 and 4525
where r.role_key in ('admin', 'ygb_hrss_supervisor', 'ygb_enterprise_admin', 'ygb_enterprise_operator')
on duplicate key update portal_scope = values(portal_scope);

insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, sort_order, source_label,
  payload_json, remark, create_by, create_time, del_flag
)
select
  920101, 'PERSON_CERTIFICATE', '高处作业证年审', 'certificate', '2026-06', 'ygb', 'pending', '0', '440106',
  1001, '广州南粤人力资源有限公司', 10001, '赵志成', null, 'PC-CERT-202606-001', 1, '人员管理',
  '{"certType":"高处作业","certNo":"CERT-440106-001","warningLevel":"1"}',
  '特证管理初始化样例', 'admin', sysdate(), '0'
from dual
where not exists (select 1 from ygb_module_record where record_id = 920101);

insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, sort_order, source_label,
  payload_json, remark, create_by, create_time, del_flag
)
select
  920102, 'PERSON_BLACKLIST', '违规进场限制名单', 'blacklist', '2026-06', 'ygb', 'processing', '0', '440305',
  1002, '深圳鹏城机电工程有限公司', 10005, '刘海鹏', null, 'PC-BL-202606-001', 1, '人员管理',
  '{"reason":"证件过期仍尝试上岗","warningLevel":"2"}',
  '黑名单初始化样例', 'admin', sysdate(), '0'
from dual
where not exists (select 1 from ygb_module_record where record_id = 920102);

insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, sort_order, source_label,
  payload_json, remark, create_by, create_time, del_flag
)
select
  920103, 'PERSON_TRAINING', '焊接班组月度培训抽查', 'training', '2026-06', 'ygb', 'pending', '0', '440606',
  1003, '佛山顺德智造服务有限公司', 10008, '陈文强', null, 'PC-TR-202606-001', 1, '人员管理',
  '{"courseHours":4,"warningLevel":"1","channel":"internal"}',
  '培训监督初始化样例', 'admin', sysdate(), '0'
from dual
where not exists (select 1 from ygb_module_record where record_id = 920103);

insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, sort_order, source_label,
  payload_json, remark, create_by, create_time, del_flag
)
select
  920104, 'PERSON_HIGH_RISK_POST', '高处吊装岗位', 'high_risk_post', '2026-06', 'ygb', 'processing', '0', '440106',
  1001, '广州南粤人力资源有限公司', null, '', null, 'PC-HR-202606-001', 1, '人员管理',
  '{"riskLevel":"high","warningLevel":"2"}',
  '高危岗位初始化样例', 'admin', sysdate(), '0'
from dual
where not exists (select 1 from ygb_module_record where record_id = 920104);

insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, sort_order, source_label,
  payload_json, remark, create_by, create_time, del_flag
)
select
  920105, 'PERSON_RISK_POST', '设备巡检岗位', 'risk_post', '2026-06', 'ygb', 'closed', '0', '440305',
  1002, '深圳鹏城机电工程有限公司', null, '', null, 'PC-RP-202606-001', 1, '人员管理',
  '{"riskLevel":"medium","warningLevel":"1"}',
  '风险岗位初始化样例', 'admin', sysdate(), '0'
from dual
where not exists (select 1 from ygb_module_record where record_id = 920105);

insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, sort_order, source_label,
  payload_json, remark, create_by, create_time, del_flag
)
select
  920106, 'PERSON_EXPERT', '高危作业审查专家池', 'expert', '2026-06', 'ygb', 'pending', '0', '440000',
  null, '', null, '', null, 'PC-EX-202606-001', 1, '人员管理',
  '{"specialty":"高危作业审查","warningLevel":"0"}',
  '专家库初始化样例', 'admin', sysdate(), '0'
from dual
where not exists (select 1 from ygb_module_record where record_id = 920106);
