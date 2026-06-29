-- 粤工保一期主数据初始化脚本
-- 导入顺序：先执行 yuegongbao_20260417.sql，再执行本脚本
set names utf8mb4;

create table if not exists t_enterprise (
  enterprise_id      bigint(20)      not null auto_increment,
  enterprise_name    varchar(100)    not null,
  enterprise_code    varchar(18)     not null,
  region_code        varchar(6)      not null,
  enterprise_type    char(1)         not null,
  legal_person       varchar(30)     default '',
  contact_person     varchar(30)     default '',
  contact_phone      varchar(11)     default '',
  address            varchar(200)    default '',
  established_date   date,
  sync_status        char(1)         default '0',
  status             char(1)         default '0',
  del_flag           char(1)         default '0',
  create_by          varchar(64)     default '',
  create_time        datetime,
  update_by          varchar(64)     default '',
  update_time        datetime,
  remark             varchar(500)    default null,
  primary key (enterprise_id),
  unique key uk_enterprise_code (enterprise_code),
  key idx_enterprise_region (region_code),
  key idx_enterprise_type (enterprise_type)
) engine=innodb auto_increment=1000;

create table if not exists t_person (
  person_id          bigint(20)      not null auto_increment,
  enterprise_id      bigint(20)      not null,
  region_code        varchar(6)      not null,
  person_name        varchar(30)     not null,
  id_card            varchar(18)     not null,
  mobile             varchar(11)     default '',
  worker_type        char(1)         default '1',
  job_type           varchar(50)     default '',
  cert_status        char(1)         default '0',
  insurance_status   char(1)         default '0',
  employment_status  char(1)         default '0',
  entry_date         date,
  leave_date         date,
  del_flag           char(1)         default '0',
  create_by          varchar(64)     default '',
  create_time        datetime,
  update_by          varchar(64)     default '',
  update_time        datetime,
  remark             varchar(500)    default null,
  primary key (person_id),
  unique key uk_person_id_card (id_card),
  key idx_person_enterprise (enterprise_id),
  key idx_person_region (region_code),
  key idx_person_status (employment_status)
) engine=innodb auto_increment=10000;

update sys_menu
set menu_name = '基础主数据',
    parent_id = 0,
    order_num = 5,
    path = 'ygb-foundation',
    icon = 'tree',
    remark = '粤工保基础主数据目录'
where menu_id = 2000;
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2000, '基础主数据', 0, 5, 'ygb-foundation', null, '', '', 1, 0, 'M', '0', '0', '', 'tree', 'admin', sysdate(), '', null, '粤工保基础主数据目录'
from dual
where not exists (select 1 from sys_menu where menu_id = 2000);
update sys_menu
set menu_name = '合规主链',
    parent_id = 0,
    order_num = 6,
    path = 'ygb-compliance',
    icon = 'form',
    remark = '粤工保合规主链目录'
where menu_id = 2020;
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2020, '合规主链', 0, 6, 'ygb-compliance', null, '', '', 1, 0, 'M', '0', '0', '', 'form', 'admin', sysdate(), '', null, '粤工保合规主链目录'
from dual
where not exists (select 1 from sys_menu where menu_id = 2020);

update sys_menu
set parent_id = 2000,
    order_num = 1
where menu_id = 2001;
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2001, '单位管理', 2000, 1, 'enterprise', 'ygb/enterprise/index', '', '', 1, 0, 'C', '0', '0', 'ygb:enterprise:list', 'tree', 'admin', sysdate(), '', null, '单位管理菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = 2001);

update sys_menu
set parent_id = 2000,
    order_num = 2
where menu_id = 2002;
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2002, '人员管理', 2000, 2, 'person', 'ygb/person/index', '', '', 1, 0, 'C', '0', '0', 'ygb:person:list', 'user', 'admin', sysdate(), '', null, '人员管理菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = 2002);

update sys_menu set menu_name = '单位查询' where menu_id = 2100;
update sys_menu set menu_name = '单位新增' where menu_id = 2101;
update sys_menu set menu_name = '单位修改' where menu_id = 2102;
update sys_menu set menu_name = '单位删除' where menu_id = 2103;
update sys_menu set menu_name = '单位导出' where menu_id = 2104;
update sys_menu set menu_name = '人员查询' where menu_id = 2200;
update sys_menu set menu_name = '人员新增' where menu_id = 2201;
update sys_menu set menu_name = '人员修改' where menu_id = 2202;
update sys_menu set menu_name = '人员删除' where menu_id = 2203;
update sys_menu set menu_name = '人员导出' where menu_id = 2204;

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2100, '单位查询', 2001, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:enterprise:query', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2100);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2101, '单位新增', 2001, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:enterprise:add', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2101);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2102, '单位修改', 2001, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:enterprise:edit', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2102);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2103, '单位删除', 2001, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:enterprise:remove', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2103);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2104, '单位导出', 2001, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:enterprise:export', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2104);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2200, '人员查询', 2002, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:person:query', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2200);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2201, '人员新增', 2002, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:person:add', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2201);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2202, '人员修改', 2002, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:person:edit', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2202);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2203, '人员删除', 2002, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:person:remove', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2203);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2204, '人员导出', 2002, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:person:export', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2204);

insert into sys_role_menu (role_id, menu_id)
select 2, 2000 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2000);
insert into sys_role_menu (role_id, menu_id)
select 2, 2020 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2020);
insert into sys_role_menu (role_id, menu_id)
select 2, 2001 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2001);
insert into sys_role_menu (role_id, menu_id)
select 2, 2002 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2002);
insert into sys_role_menu (role_id, menu_id)
select 2, 2100 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2100);
insert into sys_role_menu (role_id, menu_id)
select 2, 2101 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2101);
insert into sys_role_menu (role_id, menu_id)
select 2, 2102 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2102);
insert into sys_role_menu (role_id, menu_id)
select 2, 2103 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2103);
insert into sys_role_menu (role_id, menu_id)
select 2, 2104 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2104);
insert into sys_role_menu (role_id, menu_id)
select 2, 2200 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2200);
insert into sys_role_menu (role_id, menu_id)
select 2, 2201 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2201);
insert into sys_role_menu (role_id, menu_id)
select 2, 2202 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2202);
insert into sys_role_menu (role_id, menu_id)
select 2, 2203 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2203);
insert into sys_role_menu (role_id, menu_id)
select 2, 2204 from dual where not exists (select 1 from sys_role_menu where role_id = 2 and menu_id = 2204);




