-- 粤工保一期合同备案模块初始化脚本
-- 导入顺序：
-- 1. yuegongbao_20260417.sql
-- 2. ygb_phase1_enterprise_person.sql
-- 3. 本脚本
create table if not exists t_labor_contract (
  contract_id               bigint(20)      not null auto_increment,
  contract_no               varchar(64)     not null,
  dispatch_enterprise_id    bigint(20)      not null,
  dispatch_enterprise_name  varchar(100)    default '',
  employer_enterprise_id    bigint(20)      not null,
  employer_enterprise_name  varchar(100)    default '',
  person_id                 bigint(20)      not null,
  person_name               varchar(30)     default '',
  id_card                   varchar(18)     default '',
  region_code               varchar(6)      not null,
  contract_type             char(1)         default '1',
  contract_status           char(1)         default '0',
  sign_date                 date,
  start_date                date,
  end_date                  date,
  monthly_wage              decimal(12,2)   default 0.00,
  filing_no                 varchar(64)     default '',
  filing_time               datetime,
  ocr_status                char(1)         default '0',
  clause_check_status       char(1)         default '0',
  blockchain_hash           varchar(128)    default '',
  contract_file_url         varchar(255)    default '',
  del_flag                  char(1)         default '0',
  create_by                 varchar(64)     default '',
  create_time               datetime,
  update_by                 varchar(64)     default '',
  update_time               datetime,
  remark                    varchar(500)    default null,
  primary key (contract_id),
  unique key uk_contract_no (contract_no),
  key idx_contract_person (person_id),
  key idx_contract_region (region_code),
  key idx_contract_status (contract_status)
) engine=innodb auto_increment=20000;

update sys_menu
set parent_id = 2020,
    order_num = 1
where menu_id = 2003;
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2003, '合同备案', 2020, 1, 'contract', 'ygb/contract/index', '', '', 1, 0, 'C', '0', '0', 'ygb:contract:list', 'form', 'admin', sysdate(), '', null, '合同备案菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = 2003);

update sys_menu set menu_name = '合同查询' where menu_id = 2300;
update sys_menu set menu_name = '合同新增' where menu_id = 2301;
update sys_menu set menu_name = '合同修改' where menu_id = 2302;
update sys_menu set menu_name = '合同删除' where menu_id = 2303;
update sys_menu set menu_name = '合同导出' where menu_id = 2304;

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2300, '合同查询', 2003, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:contract:query', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2300);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2301, '合同新增', 2003, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:contract:add', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2301);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2302, '合同修改', 2003, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:contract:edit', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2302);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2303, '合同删除', 2003, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:contract:remove', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2303);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2304, '合同导出', 2003, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:contract:export', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2304);

insert into sys_role_menu (role_id, menu_id)
select 2, 2020 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2020);
insert into sys_role_menu (role_id, menu_id)
select 2, 2003 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2003);
insert into sys_role_menu (role_id, menu_id)
select 2, 2300 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2300);
insert into sys_role_menu (role_id, menu_id)
select 2, 2301 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2301);
insert into sys_role_menu (role_id, menu_id)
select 2, 2302 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2302);
insert into sys_role_menu (role_id, menu_id)
select 2, 2303 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2303);
insert into sys_role_menu (role_id, menu_id)
select 2, 2304 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2304);


