-- 粤工保一期工资发放/工资核验模块初始化脚本
-- 导入顺序：
-- 1. yuegongbao_20260417.sql
-- 2. ygb_phase1_enterprise_person.sql
-- 3. ygb_phase1_contract.sql
-- 4. ygb_phase1_attendance.sql
-- 5. 本脚本
create table if not exists t_salary_batch (
  batch_id                  bigint(20)      not null auto_increment,
  batch_no                  varchar(64)     not null,
  stat_month                varchar(7)      not null,
  dispatch_enterprise_id    bigint(20)      not null,
  dispatch_enterprise_name  varchar(100)    default '',
  region_code               varchar(6)      not null,
  total_person_count        int(11)         default 0,
  total_payable_amount      decimal(14,2)   default 0.00,
  total_paid_amount         decimal(14,2)   default 0.00,
  account_received_amount   decimal(14,2)   default 0.00,
  batch_status              char(1)         default '1',
  account_status            char(1)         default '0',
  regulator_account_name    varchar(64)     default '',
  regulator_account_no      varchar(64)     default '',
  bank_serial_no            varchar(64)     default '',
  submit_time               datetime,
  paid_time                 datetime,
  del_flag                  char(1)         default '0',
  create_by                 varchar(64)     default '',
  create_time               datetime,
  update_by                 varchar(64)     default '',
  update_time               datetime,
  remark                    varchar(500)    default null,
  primary key (batch_id),
  unique key uk_salary_batch_no (batch_no),
  key idx_salary_batch_month (stat_month),
  key idx_salary_batch_enterprise (dispatch_enterprise_id),
  key idx_salary_batch_status (batch_status)
) engine=innodb auto_increment=50000;

create table if not exists t_salary_detail (
  detail_id                 bigint(20)      not null auto_increment,
  batch_id                  bigint(20)      not null,
  batch_no                  varchar(64)     default '',
  stat_month                varchar(7)      not null,
  monthly_id                bigint(20)      not null,
  contract_id               bigint(20)      not null,
  contract_no               varchar(64)     default '',
  dispatch_enterprise_id    bigint(20)      not null,
  dispatch_enterprise_name  varchar(100)    default '',
  employer_enterprise_id    bigint(20)      not null,
  employer_enterprise_name  varchar(100)    default '',
  person_id                 bigint(20)      not null,
  person_name               varchar(30)     default '',
  id_card                   varchar(18)     default '',
  region_code               varchar(6)      not null,
  attendance_days           int(11)         default 0,
  total_hours               decimal(10,2)   default 0.00,
  att_check                 char(1)         default '1',
  payable_amount            decimal(14,2)   default 0.00,
  deduction_amount          decimal(14,2)   default 0.00,
  net_amount                decimal(14,2)   default 0.00,
  bank_account_name         varchar(64)     default '',
  bank_account_no           varchar(64)     default '',
  pay_status                char(1)         default '0',
  fail_reason               varchar(255)    default '',
  del_flag                  char(1)         default '0',
  create_by                 varchar(64)     default '',
  create_time               datetime,
  update_by                 varchar(64)     default '',
  update_time               datetime,
  remark                    varchar(500)    default null,
  primary key (detail_id),
  key idx_salary_detail_batch (batch_id),
  key idx_salary_detail_person (person_id),
  key idx_salary_detail_status (pay_status)
) engine=innodb auto_increment=60000;

update sys_menu
set parent_id = 2020,
    order_num = 4
where menu_id = 2006;
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2006, '工资批次', 2020, 4, 'salaryBatch', 'ygb/salaryBatch/index', '', '', 1, 0, 'C', '0', '0', 'ygb:salaryBatch:list', 'money', 'admin', sysdate(), '', null, '工资批次菜单'
from dual where not exists (select 1 from sys_menu where menu_id = 2006);

update sys_menu set menu_name = '批次查询' where menu_id = 2600;
update sys_menu set menu_name = '批次新增' where menu_id = 2601;
update sys_menu set menu_name = '批次修改' where menu_id = 2602;
update sys_menu set menu_name = '批次删除' where menu_id = 2603;
update sys_menu set menu_name = '批次导出' where menu_id = 2604;
update sys_menu set menu_name = '生成明细' where menu_id = 2605;
update sys_menu set menu_name = '到账确认' where menu_id = 2606;
update sys_menu set menu_name = '提交发放' where menu_id = 2607;

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2600, '批次查询', 2006, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:salaryBatch:query', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 2600);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2601, '批次新增', 2006, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:salaryBatch:add', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 2601);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2602, '批次修改', 2006, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:salaryBatch:edit', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 2602);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2603, '批次删除', 2006, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:salaryBatch:remove', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 2603);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2604, '批次导出', 2006, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:salaryBatch:export', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 2604);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2605, '生成明细', 2006, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:salaryBatch:generate', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 2605);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2606, '到账确认', 2006, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:salaryBatch:account', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 2606);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2607, '提交发放', 2006, 8, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:salaryBatch:submit', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 2607);

update sys_menu
set parent_id = 2020,
    order_num = 5
where menu_id = 2007;
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2007, '工资明细', 2020, 5, 'salaryDetail', 'ygb/salaryDetail/index', '', '', 1, 0, 'C', '0', '0', 'ygb:salaryDetail:list', 'example', 'admin', sysdate(), '', null, '工资明细菜单'
from dual where not exists (select 1 from sys_menu where menu_id = 2007);

update sys_menu set menu_name = '明细查询' where menu_id = 2700;
update sys_menu set menu_name = '明细修改' where menu_id = 2701;
update sys_menu set menu_name = '明细删除' where menu_id = 2702;
update sys_menu set menu_name = '明细导出' where menu_id = 2703;

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2700, '明细查询', 2007, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:salaryDetail:query', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 2700);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2701, '明细修改', 2007, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:salaryDetail:edit', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 2701);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2702, '明细删除', 2007, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:salaryDetail:remove', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 2702);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2703, '明细导出', 2007, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:salaryDetail:export', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 2703);

insert into sys_role_menu (role_id, menu_id)
select 2, 2020 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2020);
insert into sys_role_menu (role_id, menu_id)
select 2, 2006 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2006);
insert into sys_role_menu (role_id, menu_id)
select 2, 2600 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2600);
insert into sys_role_menu (role_id, menu_id)
select 2, 2601 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2601);
insert into sys_role_menu (role_id, menu_id)
select 2, 2602 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2602);
insert into sys_role_menu (role_id, menu_id)
select 2, 2603 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2603);
insert into sys_role_menu (role_id, menu_id)
select 2, 2604 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2604);
insert into sys_role_menu (role_id, menu_id)
select 2, 2605 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2605);
insert into sys_role_menu (role_id, menu_id)
select 2, 2606 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2606);
insert into sys_role_menu (role_id, menu_id)
select 2, 2607 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2607);
insert into sys_role_menu (role_id, menu_id)
select 2, 2007 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2007);
insert into sys_role_menu (role_id, menu_id)
select 2, 2700 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2700);
insert into sys_role_menu (role_id, menu_id)
select 2, 2701 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2701);
insert into sys_role_menu (role_id, menu_id)
select 2, 2702 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2702);
insert into sys_role_menu (role_id, menu_id)
select 2, 2703 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2703);



