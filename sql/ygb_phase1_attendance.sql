-- 粤工保一期考勤上报/考勤归集模块初始化脚本
-- 导入顺序：
-- 1. yuegongbao_20260417.sql
-- 2. ygb_phase1_enterprise_person.sql
-- 3. ygb_phase1_contract.sql
-- 4. 本脚本
create table if not exists t_attendance_raw (
  attendance_id              bigint(20)      not null auto_increment,
  attendance_no              varchar(64)     not null,
  contract_id                bigint(20)      not null,
  contract_no                varchar(64)     default '',
  dispatch_enterprise_id     bigint(20)      not null,
  dispatch_enterprise_name   varchar(100)    default '',
  employer_enterprise_id     bigint(20)      not null,
  employer_enterprise_name   varchar(100)    default '',
  person_id                  bigint(20)      not null,
  person_name                varchar(30)     default '',
  id_card                    varchar(18)     default '',
  region_code                varchar(6)      not null,
  attendance_date            date            not null,
  clock_in_time              datetime,
  clock_out_time             datetime,
  shift_name                 varchar(64)     default '',
  attendance_hours           decimal(8,2)    default 0.00,
  overtime_hours             decimal(8,2)    default 0.00,
  attendance_status          char(1)         default '1',
  source_type                char(1)         default '1',
  collect_status             char(1)         default '0',
  att_check                  char(1)         default '1',
  device_code                varchar(64)     default '',
  anomaly_remark             varchar(255)    default '',
  del_flag                   char(1)         default '0',
  create_by                  varchar(64)     default '',
  create_time                datetime,
  update_by                  varchar(64)     default '',
  update_time                datetime,
  remark                     varchar(500)    default null,
  primary key (attendance_id),
  unique key uk_attendance_no (attendance_no),
  key idx_attendance_month (attendance_date),
  key idx_attendance_contract (contract_id),
  key idx_attendance_person (person_id),
  key idx_attendance_collect (collect_status)
) engine=innodb auto_increment=30000;

create table if not exists t_attendance_monthly (
  monthly_id                 bigint(20)      not null auto_increment,
  stat_month                 varchar(7)      not null,
  contract_id                bigint(20)      not null,
  contract_no                varchar(64)     default '',
  dispatch_enterprise_id     bigint(20)      not null,
  dispatch_enterprise_name   varchar(100)    default '',
  employer_enterprise_id     bigint(20)      not null,
  employer_enterprise_name   varchar(100)    default '',
  person_id                  bigint(20)      not null,
  person_name                varchar(30)     default '',
  id_card                    varchar(18)     default '',
  region_code                varchar(6)      not null,
  attendance_days            int(11)         default 0,
  absence_days               int(11)         default 0,
  late_days                  int(11)         default 0,
  early_leave_days           int(11)         default 0,
  overtime_hours             decimal(8,2)    default 0.00,
  total_hours                decimal(10,2)   default 0.00,
  att_check                  char(1)         default '1',
  summary_status             char(1)         default '1',
  last_attendance_date       date,
  del_flag                   char(1)         default '0',
  create_by                  varchar(64)     default '',
  create_time                datetime,
  update_by                  varchar(64)     default '',
  update_time                datetime,
  remark                     varchar(500)    default null,
  primary key (monthly_id),
  unique key uk_month_contract_person (stat_month, contract_id, person_id),
  key idx_month_enterprise (dispatch_enterprise_id),
  key idx_month_person (person_id),
  key idx_month_status (summary_status)
) engine=innodb auto_increment=40000;

update sys_menu
set parent_id = 2020,
    order_num = 2
where menu_id = 2004;
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2004, '考勤上报', 2020, 2, 'attendanceRaw', 'ygb/attendanceRaw/index', '', '', 1, 0, 'C', '0', '0', 'ygb:attendanceRaw:list', 'clock', 'admin', sysdate(), '', null, '考勤上报菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = 2004);

update sys_menu set menu_name = '考勤查询' where menu_id = 2400;
update sys_menu set menu_name = '考勤新增' where menu_id = 2401;
update sys_menu set menu_name = '考勤修改' where menu_id = 2402;
update sys_menu set menu_name = '考勤删除' where menu_id = 2403;
update sys_menu set menu_name = '考勤导出' where menu_id = 2404;

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2400, '考勤查询', 2004, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:attendanceRaw:query', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2400);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2401, '考勤新增', 2004, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:attendanceRaw:add', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2401);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2402, '考勤修改', 2004, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:attendanceRaw:edit', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2402);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2403, '考勤删除', 2004, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:attendanceRaw:remove', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2403);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2404, '考勤导出', 2004, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:attendanceRaw:export', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2404);

update sys_menu
set parent_id = 2020,
    order_num = 3
where menu_id = 2005;
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2005, '考勤归集', 2020, 3, 'attendanceMonthly', 'ygb/attendanceMonthly/index', '', '', 1, 0, 'C', '0', '0', 'ygb:attendanceMonthly:list', 'histogram', 'admin', sysdate(), '', null, '考勤归集菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = 2005);

update sys_menu set menu_name = '归集查询' where menu_id = 2500;
update sys_menu set menu_name = '归集导出' where menu_id = 2501;
update sys_menu set menu_name = '执行归集' where menu_id = 2502;

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2500, '归集查询', 2005, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:attendanceMonthly:query', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2500);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2501, '归集导出', 2005, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:attendanceMonthly:export', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2501);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2502, '执行归集', 2005, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:attendanceMonthly:aggregate', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2502);

insert into sys_role_menu (role_id, menu_id)
select 2, 2020 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2020);
insert into sys_role_menu (role_id, menu_id)
select 2, 2004 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2004);
insert into sys_role_menu (role_id, menu_id)
select 2, 2400 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2400);
insert into sys_role_menu (role_id, menu_id)
select 2, 2401 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2401);
insert into sys_role_menu (role_id, menu_id)
select 2, 2402 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2402);
insert into sys_role_menu (role_id, menu_id)
select 2, 2403 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2403);
insert into sys_role_menu (role_id, menu_id)
select 2, 2404 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2404);
insert into sys_role_menu (role_id, menu_id)
select 2, 2005 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2005);
insert into sys_role_menu (role_id, menu_id)
select 2, 2500 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2500);
insert into sys_role_menu (role_id, menu_id)
select 2, 2501 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2501);
insert into sys_role_menu (role_id, menu_id)
select 2, 2502 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2502);



