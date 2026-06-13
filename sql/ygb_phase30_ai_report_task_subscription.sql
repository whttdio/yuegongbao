set names utf8mb4;

create table if not exists t_ai_report_task (
  task_id                 bigint(20)      not null auto_increment,
  report_id               bigint(20)      default null,
  report_type             varchar(16)     default '',
  task_type               varchar(16)     default 'review',
  task_name               varchar(100)    not null,
  region_code             varchar(6)      default '440000',
  risk_level              varchar(16)     default '',
  report_period           varchar(32)     default '',
  report_summary          varchar(500)    default '',
  suggestion_text         varchar(500)    default '',
  receive_user            varchar(64)     default '',
  receive_dept            varchar(100)    default '',
  handle_status           varchar(16)     default 'pending',
  due_date                datetime        default null,
  feedback_text           varchar(500)    default '',
  source_mode             varchar(16)     default 'manual',
  del_flag                char(1)         default '0',
  create_by               varchar(64)     default '',
  create_time             datetime        default null,
  update_by               varchar(64)     default '',
  update_time             datetime        default null,
  remark                  varchar(500)    default null,
  primary key (task_id),
  key idx_ai_report_task_report (report_id),
  key idx_ai_report_task_region (region_code),
  key idx_ai_report_task_status (handle_status),
  key idx_ai_report_task_due (due_date)
) engine=innodb auto_increment=98300 default charset=utf8mb4 comment='AI报告建议任务';

create table if not exists t_ai_report_subscription (
  subscription_id         bigint(20)      not null auto_increment,
  subscription_name       varchar(100)    not null,
  report_type             varchar(16)     default 'MONTHLY',
  region_code             varchar(6)      default '440000',
  cycle_type              varchar(16)     default 'MONTHLY',
  receive_type            varchar(32)     default 'INTERNAL',
  receiver                varchar(255)    default '',
  version_scope           varchar(32)     default 'current',
  status                  char(1)         default '1',
  last_send_time          datetime        default null,
  source_mode             varchar(16)     default 'manual',
  del_flag                char(1)         default '0',
  create_by               varchar(64)     default '',
  create_time             datetime        default null,
  update_by               varchar(64)     default '',
  update_time             datetime        default null,
  remark                  varchar(500)    default null,
  primary key (subscription_id),
  key idx_ai_report_sub_region (region_code),
  key idx_ai_report_sub_status (status),
  key idx_ai_report_sub_cycle (cycle_type)
) engine=innodb auto_increment=98400 default charset=utf8mb4 comment='AI报告订阅';

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4014, '建议任务', 3940, 3, 'aiReportTask', 'ygb/aiReportTask/index', '', '',
  1, 0, 'C', '0', '0', 'ygb:aiReportTask:list', 'guide',
  'admin', sysdate(), '', null, 'AI报告建议任务台账', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4014);

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4015, '任务查询', 4014, 1, '', '', '', '',
  1, 0, 'F', '0', '0', 'ygb:aiReportTask:query', '#',
  'admin', sysdate(), '', null, '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4015);

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4016, '任务新增', 4014, 2, '', '', '', '',
  1, 0, 'F', '0', '0', 'ygb:aiReportTask:add', '#',
  'admin', sysdate(), '', null, '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4016);

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4017, '任务修改', 4014, 3, '', '', '', '',
  1, 0, 'F', '0', '0', 'ygb:aiReportTask:edit', '#',
  'admin', sysdate(), '', null, '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4017);

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4018, '任务删除', 4014, 4, '', '', '', '',
  1, 0, 'F', '0', '0', 'ygb:aiReportTask:remove', '#',
  'admin', sysdate(), '', null, '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4018);

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4019, '任务导出', 4014, 5, '', '', '', '',
  1, 0, 'F', '0', '0', 'ygb:aiReportTask:export', '#',
  'admin', sysdate(), '', null, '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4019);

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4020, '任务状态', 4014, 6, '', '', '', '',
  1, 0, 'F', '0', '0', 'ygb:aiReportTask:status', '#',
  'admin', sysdate(), '', null, '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4020);

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4021, '订阅管理', 3940, 4, 'aiReportSubscription', 'ygb/aiReportSubscription/index', '', '',
  1, 0, 'C', '0', '0', 'ygb:aiReportSubscription:list', 'message',
  'admin', sysdate(), '', null, 'AI报告订阅管理', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4021);

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4022, '订阅查询', 4021, 1, '', '', '', '',
  1, 0, 'F', '0', '0', 'ygb:aiReportSubscription:query', '#',
  'admin', sysdate(), '', null, '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4022);

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4023, '订阅新增', 4021, 2, '', '', '', '',
  1, 0, 'F', '0', '0', 'ygb:aiReportSubscription:add', '#',
  'admin', sysdate(), '', null, '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4023);

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4024, '订阅修改', 4021, 3, '', '', '', '',
  1, 0, 'F', '0', '0', 'ygb:aiReportSubscription:edit', '#',
  'admin', sysdate(), '', null, '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4024);

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4025, '订阅删除', 4021, 4, '', '', '', '',
  1, 0, 'F', '0', '0', 'ygb:aiReportSubscription:remove', '#',
  'admin', sysdate(), '', null, '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4025);

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4026, '订阅导出', 4021, 5, '', '', '', '',
  1, 0, 'F', '0', '0', 'ygb:aiReportSubscription:export', '#',
  'admin', sysdate(), '', null, '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4026);

update sys_menu
set portal_scope = 'ygb'
where menu_id in (4014, 4015, 4016, 4017, 4018, 4019, 4020, 4021, 4022, 4023, 4024, 4025, 4026);

insert into sys_role_menu (role_id, menu_id, portal_scope)
select r.role_id, m.menu_id, 'ygb'
from sys_role r
join sys_menu m on m.menu_id in (4014, 4015, 4016, 4017, 4018, 4019, 4020, 4021, 4022, 4023, 4024, 4025, 4026)
where r.role_id in (1, 2, 101, 102, 103, 104, 105)
on duplicate key update portal_scope = values(portal_scope);
