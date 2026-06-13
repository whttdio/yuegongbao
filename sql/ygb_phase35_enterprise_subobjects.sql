set names utf8mb4;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4533, '监管单位', 2001, 20, 'enterpriseRegulator', 'ygb/enterpriseRegulator/index', '', 'YgbEnterpriseRegulator',
  1, 0, 'C', '0', '0', 'ygb:enterprise:list', 'office-building',
  'admin', sysdate(), 'admin', sysdate(), '单位管理-监管单位子页', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4533);

insert into sys_menu
select 4534, '劳务派遣公司', 2001, 21, 'enterpriseDispatch', 'ygb/enterpriseDispatch/index', '', 'YgbEnterpriseDispatch',
  1, 0, 'C', '0', '0', 'ygb:enterprise:list', 'connection',
  'admin', sysdate(), 'admin', sysdate(), '单位管理-劳务派遣公司子页', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4534);

insert into sys_menu
select 4535, '用工单位', 2001, 22, 'enterpriseEmployer', 'ygb/enterpriseEmployer/index', '', 'YgbEnterpriseEmployer',
  1, 0, 'C', '0', '0', 'ygb:enterprise:list', 'suitcase',
  'admin', sysdate(), 'admin', sysdate(), '单位管理-用工单位子页', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4535);

insert into sys_menu
select 4536, '高危企业库', 2001, 23, 'enterpriseHighRisk', 'ygb/enterpriseHighRisk/index', '', 'YgbEnterpriseHighRisk',
  1, 0, 'C', '0', '0', 'ygb:enterpriseHighRisk:list', 'warning',
  'admin', sysdate(), 'admin', sysdate(), '单位管理-高危企业库子页', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4536);

insert into sys_menu
select 4537, '派遣用工关联', 2001, 24, 'enterpriseRelation', 'ygb/enterpriseRelation/index', '', 'YgbEnterpriseRelation',
  1, 0, 'C', '0', '0', 'ygb:enterpriseRelation:list', 'share',
  'admin', sysdate(), 'admin', sysdate(), '单位管理-派遣用工关联子页', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4537);

insert into sys_menu
select 4538, '工会管理', 2001, 25, 'enterpriseUnion', 'ygb/enterpriseUnion/index', '', 'YgbEnterpriseUnion',
  1, 0, 'C', '0', '0', 'ygb:enterpriseUnion:list', 'service',
  'admin', sysdate(), 'admin', sysdate(), '单位管理-工会管理子页', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4538);

insert into sys_menu
select 4539, '查询', 4536, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:enterpriseHighRisk:query', '#',
  'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4539);
insert into sys_menu
select 4540, '新增', 4536, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:enterpriseHighRisk:add', '#',
  'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4540);
insert into sys_menu
select 4541, '修改', 4536, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:enterpriseHighRisk:edit', '#',
  'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4541);
insert into sys_menu
select 4542, '删除', 4536, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:enterpriseHighRisk:remove', '#',
  'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4542);
insert into sys_menu
select 4543, '导出', 4536, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:enterpriseHighRisk:export', '#',
  'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4543);

insert into sys_menu
select 4544, '查询', 4537, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:enterpriseRelation:query', '#',
  'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4544);
insert into sys_menu
select 4545, '新增', 4537, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:enterpriseRelation:add', '#',
  'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4545);
insert into sys_menu
select 4546, '修改', 4537, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:enterpriseRelation:edit', '#',
  'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4546);
insert into sys_menu
select 4547, '删除', 4537, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:enterpriseRelation:remove', '#',
  'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4547);
insert into sys_menu
select 4548, '导出', 4537, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:enterpriseRelation:export', '#',
  'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4548);

insert into sys_menu
select 4549, '查询', 4538, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:enterpriseUnion:query', '#',
  'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4549);
insert into sys_menu
select 4550, '新增', 4538, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:enterpriseUnion:add', '#',
  'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4550);
insert into sys_menu
select 4551, '修改', 4538, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:enterpriseUnion:edit', '#',
  'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4551);
insert into sys_menu
select 4552, '删除', 4538, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:enterpriseUnion:remove', '#',
  'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4552);
insert into sys_menu
select 4553, '导出', 4538, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:enterpriseUnion:export', '#',
  'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4553);

update sys_menu
set portal_scope = 'ygb'
where menu_id between 4533 and 4553;

insert into sys_role_menu (role_id, menu_id, portal_scope)
select r.role_id, m.menu_id, 'ygb'
from sys_role r
join sys_menu m on m.menu_id between 4533 and 4553
where r.role_key in ('admin', 'ygb_hrss_supervisor', 'ygb_enterprise_admin', 'ygb_enterprise_operator')
on duplicate key update portal_scope = values(portal_scope);

insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, related_id, related_code, sort_order, source_label, payload_json, remark, create_by,
  create_time, del_flag
)
select
  920301, 'ENTERPRISE_HIGH_RISK', '天河重点高危企业名单', 'high_risk', '2026-06', 'ygb', 'processing', '0', '440106',
  1001, '广州南粤人力资源有限公司', null, 'EH-202606-001', 1, '单位管理',
  '{"riskLevel":"high","warningLevel":"2","riskSource":"现场治理"}', '高危企业库初始化样例', 'admin', sysdate(), '0'
from dual
where not exists (select 1 from ygb_module_record where record_id = 920301);

insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, related_id, related_code, sort_order, source_label, payload_json, remark, create_by,
  create_time, del_flag
)
select
  920302, 'ENTERPRISE_RELATION', '南粤派遣-智造用工协作关系', 'relation', '2026-06', 'ygb', 'pending', '0', '440606',
  1003, '佛山顺德智造服务有限公司', 1001, 'ER-202606-001', 1, '单位管理',
  '{"dispatchEnterpriseId":1001,"dispatchEnterpriseName":"广州南粤人力资源有限公司","employerEnterpriseId":1003,"employerEnterpriseName":"佛山顺德智造服务有限公司"}',
  '派遣用工关联初始化样例', 'admin', sysdate(), '0'
from dual
where not exists (select 1 from ygb_module_record where record_id = 920302);

insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, related_id, related_code, sort_order, source_label, payload_json, remark, create_by,
  create_time, del_flag
)
select
  920303, 'ENTERPRISE_UNION', '南山机电企业工会联合会', 'union', '2026-06', 'ygb', 'closed', '0', '440305',
  1002, '深圳鹏城机电工程有限公司', null, 'EU-202606-001', 1, '单位管理',
  '{"unionLevel":"enterprise","contactChannel":"internal","warningLevel":"0"}', '工会管理初始化样例', 'admin', sysdate(), '0'
from dual
where not exists (select 1 from ygb_module_record where record_id = 920303);
