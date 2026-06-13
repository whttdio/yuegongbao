set names utf8mb4;

create table if not exists t_contract_template (
  template_id            bigint(20)      not null auto_increment,
  template_code          varchar(64)     not null,
  template_name          varchar(100)    not null,
  template_version       varchar(32)     default 'V1.0',
  template_type          char(1)         default '1',
  applicable_scope       varchar(255)    default '',
  review_status          varchar(16)     default 'approved',
  status                 char(1)         default '1',
  region_code            varchar(6)      default '440000',
  template_file_url      varchar(255)    default '',
  content_text           text,
  del_flag               char(1)         default '0',
  create_by              varchar(64)     default '',
  create_time            datetime,
  update_by              varchar(64)     default '',
  update_time            datetime,
  remark                 varchar(500)    default null,
  primary key (template_id),
  unique key uk_contract_template_code (template_code),
  key idx_contract_template_type (template_type),
  key idx_contract_template_region (region_code)
) engine=innodb auto_increment=98000 default charset=utf8mb4 comment='合同模板库';

create table if not exists t_salary_arrears_handle (
  arrears_id             bigint(20)      not null auto_increment,
  batch_id               bigint(20)      not null,
  warning_id             bigint(20)      default null,
  handle_status          varchar(16)     default 'pending',
  follow_user            varchar(64)     default '',
  follow_time            datetime        default null,
  next_follow_time       datetime        default null,
  handle_result          varchar(500)    default '',
  create_by              varchar(64)     default '',
  create_time            datetime        default null,
  update_by              varchar(64)     default '',
  update_time            datetime        default null,
  remark                 varchar(500)    default null,
  primary key (arrears_id),
  unique key uk_salary_arrears_batch (batch_id),
  key idx_salary_arrears_warning (warning_id),
  key idx_salary_arrears_status (handle_status)
) engine=innodb auto_increment=98100 default charset=utf8mb4 comment='工资拖欠处置台账';

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache,
  menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3980, '合同模板库', 2020, 2, 'contractTemplate', 'ygb/contractTemplate/index', '', '', 1, 0, 'C', '0', '0',
  'ygb:contractTemplate:list', 'documentation', 'admin', sysdate(), '', null, '合同模板库'
from dual where not exists (select 1 from sys_menu where menu_id = 3980);

insert into sys_menu
select 3981, '模板查询', 3980, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:contractTemplate:query', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3981);
insert into sys_menu
select 3982, '模板新增', 3980, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:contractTemplate:add', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3982);
insert into sys_menu
select 3983, '模板修改', 3980, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:contractTemplate:edit', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3983);
insert into sys_menu
select 3984, '模板删除', 3980, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:contractTemplate:remove', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3984);
insert into sys_menu
select 3985, '模板导出', 3980, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:contractTemplate:export', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3985);
insert into sys_menu
select 3986, '模板状态', 3980, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:contractTemplate:status', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3986);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache,
  menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3988, '工资拖欠预警', 2020, 6, 'salaryArrears', 'ygb/salaryArrears/index', '', '', 1, 0, 'C', '0', '0',
  'ygb:salaryBatchArrears:list', 'guide', 'admin', sysdate(), '', null, '工资拖欠预警'
from dual where not exists (select 1 from sys_menu where menu_id = 3988);

insert into sys_menu
select 3989, '拖欠查询', 3988, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:salaryBatchArrears:query', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3989);
insert into sys_menu
select 3990, '拖欠处置', 3988, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:salaryBatchArrears:handle', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3990);
insert into sys_menu
select 3991, '拖欠导出', 3988, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:salaryBatchArrears:export', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3991);

insert ignore into sys_role_menu (role_id, menu_id)
select r.role_id, m.menu_id
from sys_role r
join sys_menu m on m.menu_id in (3980, 3981, 3982, 3983, 3984, 3985, 3986, 3988, 3989, 3990, 3991)
where r.role_id in (1, 2, 101, 102, 103, 104, 105);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache,
  menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3992, '用工月报', 3920, 5, 'statReport/employment', 'ygb/statReport/employment/index', '', '', 1, 0, 'C', '0', '0',
  'ygb:statReport:employment:query', 'table', 'admin', sysdate(), '', null, '用工月报'
from dual where not exists (select 1 from sys_menu where menu_id = 3992);
insert into sys_menu
select 3993, '考勤月报', 3920, 6, 'statReport/attendance', 'ygb/statReport/attendance/index', '', '', 1, 0, 'C', '0', '0',
  'ygb:statReport:attendance:query', 'table', 'admin', sysdate(), '', null, '考勤月报'
from dual where not exists (select 1 from sys_menu where menu_id = 3993);
insert into sys_menu
select 3994, '社保月报', 3920, 7, 'statReport/social', 'ygb/statReport/social/index', '', '', 1, 0, 'C', '0', '0',
  'ygb:statReport:social:query', 'table', 'admin', sysdate(), '', null, '社保月报'
from dual where not exists (select 1 from sys_menu where menu_id = 3994);
insert into sys_menu
select 3995, '税务月报', 3920, 8, 'statReport/tax', 'ygb/statReport/tax/index', '', '', 1, 0, 'C', '0', '0',
  'ygb:statReport:tax:query', 'table', 'admin', sysdate(), '', null, '税务月报'
from dual where not exists (select 1 from sys_menu where menu_id = 3995);

insert into sys_menu
select 3996, '查询', 3992, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:employment:query', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3996);
insert into sys_menu
select 3997, '生成', 3992, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:employment:generate', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3997);
insert into sys_menu
select 3998, '导出', 3992, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:employment:export', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3998);
insert into sys_menu
select 3999, '查询', 3993, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:attendance:query', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3999);
insert into sys_menu
select 4000, '生成', 3993, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:attendance:generate', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 4000);
insert into sys_menu
select 4001, '导出', 3993, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:attendance:export', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 4001);
insert into sys_menu
select 4002, '查询', 3994, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:social:query', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 4002);
insert into sys_menu
select 4003, '生成', 3994, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:social:generate', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 4003);
insert into sys_menu
select 4004, '导出', 3994, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:social:export', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 4004);
insert into sys_menu
select 4005, '查询', 3995, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:tax:query', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 4005);
insert into sys_menu
select 4006, '生成', 3995, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:tax:generate', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 4006);
insert into sys_menu
select 4007, '导出', 3995, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:tax:export', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 4007);

insert ignore into sys_role_menu (role_id, menu_id)
select r.role_id, m.menu_id
from sys_role r
join sys_menu m on m.menu_id in (3992, 3993, 3994, 3995, 3996, 3997, 3998, 3999, 4000, 4001, 4002, 4003, 4004, 4005, 4006, 4007)
where r.role_id in (1, 2, 101, 102, 103, 104, 105);
