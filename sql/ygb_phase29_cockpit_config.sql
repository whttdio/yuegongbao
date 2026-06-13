set names utf8mb4;

create table if not exists t_cockpit_config (
  config_id               bigint(20)      not null auto_increment,
  config_code             varchar(64)     not null,
  config_name             varchar(100)    not null,
  status                  char(1)         default '1',
  region_code             varchar(6)      not null default '440000',
  default_region_code     varchar(6)      default '440000',
  summary_card_config     text,
  focus_queue_config      text,
  rotate_seconds          int(11)         default 0,
  refresh_seconds         int(11)         default 30,
  map_center_lng          decimal(10,6)   default null,
  map_center_lat          decimal(10,6)   default null,
  map_zoom                int(11)         default 11,
  default_focus_key       varchar(64)     default '',
  source_mode             varchar(16)     default 'manual',
  del_flag                char(1)         default '0',
  create_by               varchar(64)     default '',
  create_time             datetime        default null,
  update_by               varchar(64)     default '',
  update_time             datetime        default null,
  remark                  varchar(500)    default null,
  primary key (config_id),
  unique key uk_cockpit_config_code (config_code),
  key idx_cockpit_config_region (region_code),
  key idx_cockpit_config_status (status)
) engine=innodb auto_increment=98200 default charset=utf8mb4 comment='驾驶舱配置';

insert into t_cockpit_config (
  config_id, config_code, config_name, status, region_code, default_region_code,
  summary_card_config, focus_queue_config, rotate_seconds, refresh_seconds,
  map_center_lng, map_center_lat, map_zoom, default_focus_key, source_mode,
  del_flag, create_by, create_time, update_by, update_time, remark
)
select
  98200, 'DEFAULT-COCKPIT', '省级默认配置', '1', '440000', '440000',
  '[]', '[]', 0, 30,
  113.266530, 23.132191, 11, '', 'seed',
  '0', 'admin', sysdate(), 'admin', sysdate(), 'P0 驾驶舱默认配置'
from dual
where not exists (
  select 1 from t_cockpit_config where config_code = 'DEFAULT-COCKPIT' and del_flag = '0'
);

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4008, '驾驶舱配置', 3900, 2, 'cockpitConfig', 'ygb/cockpitConfig/index', '', '',
  1, 0, 'C', '0', '0', 'ygb:cockpitConfig:list', 'setting',
  'admin', sysdate(), '', null, 'P0 驾驶舱配置台账', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4008);

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4009, '查询', 4008, 1, '', '', '', '',
  1, 0, 'F', '0', '0', 'ygb:cockpitConfig:query', '#',
  'admin', sysdate(), '', null, '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4009);

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4010, '新增', 4008, 2, '', '', '', '',
  1, 0, 'F', '0', '0', 'ygb:cockpitConfig:add', '#',
  'admin', sysdate(), '', null, '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4010);

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4011, '修改', 4008, 3, '', '', '', '',
  1, 0, 'F', '0', '0', 'ygb:cockpitConfig:edit', '#',
  'admin', sysdate(), '', null, '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4011);

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4012, '删除', 4008, 4, '', '', '', '',
  1, 0, 'F', '0', '0', 'ygb:cockpitConfig:remove', '#',
  'admin', sysdate(), '', null, '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4012);

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4013, '导出', 4008, 5, '', '', '', '',
  1, 0, 'F', '0', '0', 'ygb:cockpitConfig:export', '#',
  'admin', sysdate(), '', null, '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4013);

update sys_menu
set portal_scope = 'ygb'
where menu_id in (4008, 4009, 4010, 4011, 4012, 4013);

insert into sys_role_menu (role_id, menu_id, portal_scope)
select r.role_id, m.menu_id, 'ygb'
from sys_role r
join sys_menu m on m.menu_id in (4008, 4009, 4010, 4011, 4012, 4013)
where r.role_id in (1, 2, 101, 102, 103, 104, 105)
on duplicate key update portal_scope = values(portal_scope);
