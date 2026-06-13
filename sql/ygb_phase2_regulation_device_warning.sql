-- 粤工保二期：监管联动线 + 设备工伤 + 预警治理
-- 导入顺序：
-- 1. yuegongbao_20260417.sql
-- 2. ygb_phase1_enterprise_person.sql
-- 3. ygb_phase1_contract.sql
-- 4. ygb_phase1_attendance.sql
-- 5. ygb_phase1_salary.sql
-- 6. 本脚本
create table if not exists t_social_payment (
  payment_id           bigint(20)      not null auto_increment,
  stat_month           varchar(7)      not null,
  enterprise_id        bigint(20)      not null,
  enterprise_name      varchar(100)    default '',
  person_id            bigint(20)      not null,
  person_name          varchar(30)     default '',
  id_card              varchar(18)     default '',
  region_code          varchar(6)      default '',
  base_amount          decimal(14,2)   default 0.00,
  paid_amount          decimal(14,2)   default 0.00,
  payment_status       char(1)         default '1',
  source_serial_no     varchar(64)     default '',
  source_status        varchar(32)     default '',
  source_message       varchar(255)    default '',
  callback_time        datetime,
  raw_payload          text,
  del_flag             char(1)         default '0',
  create_by            varchar(64)     default '',
  create_time          datetime,
  update_by            varchar(64)     default '',
  update_time          datetime,
  remark               varchar(500)    default null,
  primary key (payment_id),
  key idx_social_payment_month (stat_month),
  key idx_social_payment_enterprise (enterprise_id),
  key idx_social_payment_person (person_id)
) engine=innodb auto_increment=70000;

create table if not exists t_social_base_compare (
  compare_id           bigint(20)      not null auto_increment,
  stat_month           varchar(7)      not null,
  enterprise_id        bigint(20)      not null,
  enterprise_name      varchar(100)    default '',
  person_id            bigint(20)      not null,
  person_name          varchar(30)     default '',
  id_card              varchar(18)     default '',
  region_code          varchar(6)      default '',
  salary_amount        decimal(14,2)   default 0.00,
  social_base_amount   decimal(14,2)   default 0.00,
  diff_ratio           decimal(8,2)    default 0.00,
  compare_result       char(1)         default '1',
  warning_status       char(1)         default '0',
  del_flag             char(1)         default '0',
  create_by            varchar(64)     default '',
  create_time          datetime,
  update_by            varchar(64)     default '',
  update_time          datetime,
  remark               varchar(500)    default null,
  primary key (compare_id),
  key idx_social_compare_month (stat_month),
  key idx_social_compare_enterprise (enterprise_id),
  key idx_social_compare_person (person_id)
) engine=innodb auto_increment=71000;

create table if not exists t_tax_compare (
  compare_id           bigint(20)      not null auto_increment,
  stat_month           varchar(7)      not null,
  enterprise_id        bigint(20)      not null,
  enterprise_name      varchar(100)    default '',
  person_id            bigint(20)      not null,
  person_name          varchar(30)     default '',
  id_card              varchar(18)     default '',
  region_code          varchar(6)      default '',
  salary_amount        decimal(14,2)   default 0.00,
  declared_amount      decimal(14,2)   default 0.00,
  diff_ratio           decimal(8,2)    default 0.00,
  compare_result       char(1)         default '1',
  warning_status       char(1)         default '0',
  source_serial_no     varchar(64)     default '',
  source_status        varchar(32)     default '',
  source_message       varchar(255)    default '',
  callback_time        datetime,
  raw_payload          text,
  del_flag             char(1)         default '0',
  create_by            varchar(64)     default '',
  create_time          datetime,
  update_by            varchar(64)     default '',
  update_time          datetime,
  remark               varchar(500)    default null,
  primary key (compare_id),
  key idx_tax_compare_month (stat_month),
  key idx_tax_compare_enterprise (enterprise_id),
  key idx_tax_compare_person (person_id)
) engine=innodb auto_increment=72000;

create table if not exists t_uninsured_list (
  list_id              bigint(20)      not null auto_increment,
  batch_no             varchar(32)     not null,
  stat_month           varchar(7)      not null,
  list_type            char(1)         default '1',
  enterprise_id        bigint(20)      not null,
  enterprise_name      varchar(100)    default '',
  person_id            bigint(20)      not null,
  person_name          varchar(30)     default '',
  id_card              varchar(18)     default '',
  region_code          varchar(6)      default '',
  salary_amount        decimal(14,2)   default 0.00,
  detected_reason      varchar(255)    default '',
  disposal_status      char(1)         default '0',
  warning_status       char(1)         default '0',
  del_flag             char(1)         default '0',
  create_by            varchar(64)     default '',
  create_time          datetime,
  update_by            varchar(64)     default '',
  update_time          datetime,
  remark               varchar(500)    default null,
  primary key (list_id),
  key idx_uninsured_month (stat_month),
  key idx_uninsured_batch (batch_no),
  key idx_uninsured_enterprise (enterprise_id),
  key idx_uninsured_person (person_id)
) engine=innodb auto_increment=73000;

create table if not exists t_employment_proportion (
  record_id            bigint(20)      not null auto_increment,
  stat_month           varchar(7)      not null,
  employer_enterprise_id   bigint(20)  not null,
  employer_enterprise_name varchar(100) default '',
  region_code          varchar(6)      default '',
  dispatch_count       int(11)         default 0,
  formal_count         int(11)         default 0,
  ratio_value          decimal(8,2)    default 0.00,
  warning_level        char(1)         default '0',
  warning_status       char(1)         default '0',
  del_flag             char(1)         default '0',
  create_by            varchar(64)     default '',
  create_time          datetime,
  update_by            varchar(64)     default '',
  update_time          datetime,
  remark               varchar(500)    default null,
  primary key (record_id),
  key idx_employment_ratio_month (stat_month),
  key idx_employment_ratio_ent (employer_enterprise_id)
) engine=innodb auto_increment=74000;

create table if not exists t_fake_outsourcing_record (
  record_id            bigint(20)      not null auto_increment,
  stat_month           varchar(7)      not null,
  enterprise_id        bigint(20)      not null,
  enterprise_name      varchar(100)    default '',
  region_code          varchar(6)      default '',
  attendance_score     int(11)         default 0,
  schedule_score       int(11)         default 0,
  reward_score         int(11)         default 0,
  training_score       int(11)         default 0,
  total_score          int(11)         default 0,
  suspected_flag       char(1)         default '0',
  warning_status       char(1)         default '0',
  evidence_summary     varchar(500)    default '',
  del_flag             char(1)         default '0',
  create_by            varchar(64)     default '',
  create_time          datetime,
  update_by            varchar(64)     default '',
  update_time          datetime,
  remark               varchar(500)    default null,
  primary key (record_id),
  key idx_fake_outsourcing_month (stat_month),
  key idx_fake_outsourcing_ent (enterprise_id)
) engine=innodb auto_increment=75000;

create table if not exists t_device (
  device_id            bigint(20)      not null auto_increment,
  device_code          varchar(64)     not null,
  device_name          varchar(100)    default '',
  device_type          char(1)         default '1',
  enterprise_id        bigint(20)      not null,
  enterprise_name      varchar(100)    default '',
  region_code          varchar(6)      default '',
  chip_id              varchar(64)     default '',
  sim_card_no          varchar(32)     default '',
  device_status        char(1)         default '0',
  auth_status          char(1)         default '0',
  last_heartbeat       datetime,
  install_location     varchar(200)    default '',
  firmware_version     varchar(32)     default '',
  del_flag             char(1)         default '0',
  create_by            varchar(64)     default '',
  create_time          datetime,
  update_by            varchar(64)     default '',
  update_time          datetime,
  remark               varchar(500)    default null,
  primary key (device_id),
  unique key uk_device_code (device_code),
  key idx_device_enterprise (enterprise_id),
  key idx_device_status (device_status)
) engine=innodb auto_increment=76000;

create table if not exists t_device_command_log (
  log_id               bigint(20)      not null auto_increment,
  device_id            bigint(20)      not null,
  device_code          varchar(64)     default '',
  command_type         char(1)         default '1',
  command_payload      text,
  command_result       char(1)         default '1',
  result_message       varchar(255)    default '',
  source_serial_no     varchar(64)     default '',
  source_status        varchar(32)     default '',
  source_message       varchar(255)    default '',
  callback_time        datetime,
  raw_payload          text,
  operator_name        varchar(64)     default '',
  create_by            varchar(64)     default '',
  create_time          datetime,
  primary key (log_id),
  key idx_device_command_device (device_id)
) engine=innodb auto_increment=77000;

create table if not exists t_device_event (
  event_id             bigint(20)      not null auto_increment,
  device_id            bigint(20)      not null,
  device_code          varchar(64)     default '',
  enterprise_id        bigint(20)      default null,
  enterprise_name      varchar(100)    default '',
  region_code          varchar(6)      default '',
  event_type           char(1)         default '1',
  event_code           varchar(64)     default '',
  event_content        varchar(500)    default '',
  evidence_url         varchar(255)    default '',
  event_status         char(1)         default '0',
  source_serial_no     varchar(64)     default '',
  source_status        varchar(32)     default '',
  source_message       varchar(255)    default '',
  callback_time        datetime,
  raw_payload          text,
  event_time           datetime,
  create_by            varchar(64)     default '',
  create_time          datetime,
  primary key (event_id),
  key idx_device_event_device (device_id),
  key idx_device_event_type (event_type)
) engine=innodb auto_increment=78000;

create table if not exists t_injury_event (
  event_id             bigint(20)      not null auto_increment,
  person_id            bigint(20)      not null,
  person_name          varchar(30)     default '',
  enterprise_id        bigint(20)      not null,
  enterprise_name      varchar(100)    default '',
  region_code          varchar(6)      default '',
  event_date           date,
  report_time          datetime,
  injury_location      varchar(200)    default '',
  injury_part          varchar(100)    default '',
  diagnosis_url        varchar(255)    default '',
  injury_status        char(1)         default '0',
  approval_deadline    date,
  remaining_days       int(11)         default 0,
  approval_result      varchar(255)    default '',
  warning_status       char(1)         default '0',
  del_flag             char(1)         default '0',
  create_by            varchar(64)     default '',
  create_time          datetime,
  update_by            varchar(64)     default '',
  update_time          datetime,
  remark               varchar(500)    default null,
  primary key (event_id),
  key idx_injury_enterprise (enterprise_id),
  key idx_injury_status (injury_status)
) engine=innodb auto_increment=79000;

create table if not exists t_prevention_project (
  project_id           bigint(20)      not null auto_increment,
  project_name         varchar(200)    not null,
  project_type         char(1)         default '1',
  enterprise_id        bigint(20)      default null,
  enterprise_name      varchar(100)    default '',
  region_code          varchar(6)      default '',
  budget_amount        decimal(14,2)   default 0.00,
  actual_amount        decimal(14,2)   default 0.00,
  start_date           date,
  end_date             date,
  project_status       char(1)         default '0',
  evaluation_score     int(11)         default 0,
  evaluation_report    varchar(255)    default '',
  del_flag             char(1)         default '0',
  create_by            varchar(64)     default '',
  create_time          datetime,
  update_by            varchar(64)     default '',
  update_time          datetime,
  remark               varchar(500)    default null,
  primary key (project_id),
  key idx_prevention_enterprise (enterprise_id),
  key idx_prevention_status (project_status)
) engine=innodb auto_increment=80000;

create table if not exists t_warning_rule (
  rule_id              bigint(20)      not null auto_increment,
  rule_name            varchar(100)    not null,
  warn_level           char(1)         default '2',
  source_module        varchar(50)     default '',
  condition_text       varchar(500)    default '',
  push_targets         varchar(255)    default '',
  timeout_minutes      int(11)         default 240,
  upgrade_level        char(1)         default '3',
  rule_status          char(1)         default '1',
  create_by            varchar(64)     default '',
  create_time          datetime,
  update_by            varchar(64)     default '',
  update_time          datetime,
  remark               varchar(500)    default null,
  primary key (rule_id)
) engine=innodb auto_increment=81000;

create table if not exists t_warning (
  warn_id              bigint(20)      not null auto_increment,
  warn_level           char(1)         default '2',
  warn_type            varchar(50)     default '',
  source_module        varchar(50)     default '',
  target_object_id     bigint(20)      default null,
  target_type          char(1)         default '1',
  enterprise_id        bigint(20)      default null,
  enterprise_name      varchar(100)    default '',
  region_code          varchar(6)      default '',
  content              varchar(500)    default '',
  evidence_url         varchar(255)    default '',
  warn_status          char(1)         default '0',
  assign_to            bigint(20)      default null,
  assign_name          varchar(64)     default '',
  create_by            varchar(64)     default '',
  create_time          datetime,
  update_by            varchar(64)     default '',
  update_time          datetime,
  resolve_time         datetime,
  remark               varchar(500)    default null,
  primary key (warn_id),
  key idx_warning_status (warn_status),
  key idx_warning_source (source_module),
  key idx_warning_target (target_object_id)
) engine=innodb auto_increment=82000;

create table if not exists t_warning_handle_log (
  log_id               bigint(20)      not null auto_increment,
  warn_id              bigint(20)      not null,
  action_type          varchar(32)     default '',
  opinion              varchar(500)    default '',
  attachment_urls      varchar(1000)   default '',
  before_status        char(1)         default '',
  after_status         char(1)         default '',
  handler_name         varchar(64)     default '',
  handle_time          datetime,
  create_by            varchar(64)     default '',
  create_time          datetime,
  primary key (log_id),
  key idx_warning_handle_warn (warn_id)
) engine=innodb auto_increment=83000;

update sys_menu
set menu_name = '监管联动',
    parent_id = 0,
    order_num = 7,
    path = 'ygb-regulation',
    icon = 'chart',
    remark = '粤工保监管联动目录'
where menu_id = 2040;

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2040, '监管联动', 0, 7, 'ygb-regulation', null, '', '', 1, 0, 'M', '0', '0', '', 'chart', 'admin', sysdate(), '', null, '粤工保监管联动目录'
from dual where not exists (select 1 from sys_menu where menu_id = 2040);

update sys_menu
set menu_name = '设备工伤',
    parent_id = 0,
    order_num = 8,
    path = 'ygb-safety',
    icon = 'build',
    remark = '粤工保设备工伤目录'
where menu_id = 2060;

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2060, '设备工伤', 0, 8, 'ygb-safety', null, '', '', 1, 0, 'M', '0', '0', '', 'build', 'admin', sysdate(), '', null, '粤工保设备工伤目录'
from dual where not exists (select 1 from sys_menu where menu_id = 2060);

update sys_menu
set menu_name = '预警治理',
    parent_id = 0,
    order_num = 9,
    path = 'ygb-warning',
    icon = 'message',
    remark = '粤工保预警治理目录'
where menu_id = 2080;

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2080, '预警治理', 0, 9, 'ygb-warning', null, '', '', 1, 0, 'M', '0', '0', '', 'message', 'admin', sysdate(), '', null, '粤工保预警治理目录'
from dual where not exists (select 1 from sys_menu where menu_id = 2080);

update sys_menu set parent_id = 2040, order_num = 1 where menu_id = 2008;
update sys_menu set parent_id = 2040, order_num = 2 where menu_id = 2009;
update sys_menu set parent_id = 2040, order_num = 3 where menu_id = 2010;
update sys_menu set parent_id = 2040, order_num = 4 where menu_id = 2011;
update sys_menu set parent_id = 2040, order_num = 5 where menu_id = 2012;
update sys_menu set parent_id = 2040, order_num = 6 where menu_id = 2013;
update sys_menu set parent_id = 2060, order_num = 1 where menu_id = 2014;
update sys_menu set parent_id = 2060, order_num = 2 where menu_id = 2015;
update sys_menu set parent_id = 2060, order_num = 3 where menu_id = 2016;
update sys_menu set parent_id = 2080, order_num = 1 where menu_id = 2017;
update sys_menu set parent_id = 2080, order_num = 2 where menu_id = 2018;

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2008, '社保缴费监控', 2040, 1, 'socialPayment', 'ygb/socialPayment/index', '', '', 1, 0, 'C', '0', '0', 'ygb:socialPayment:list', 'money', 'admin', sysdate(), '', null, '社保缴费监控菜单'
from dual where not exists (select 1 from sys_menu where menu_id = 2008);
insert into sys_menu select 2800, '缴费查询', 2008, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:socialPayment:query', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 2800);
insert into sys_menu select 2801, '模拟同步', 2008, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:socialPayment:sync', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 2801);
insert into sys_menu select 2802, '导出', 2008, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:socialPayment:export', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 2802);

insert into sys_menu
select 2009, '社保基数比对', 2040, 2, 'socialBaseCompare', 'ygb/socialBaseCompare/index', '', '', 1, 0, 'C', '0', '0', 'ygb:socialBaseCompare:list', 'histogram', 'admin', sysdate(), '', null, '社保基数比对菜单'
from dual where not exists (select 1 from sys_menu where menu_id = 2009);
insert into sys_menu select 2900, '比对查询', 2009, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:socialBaseCompare:query', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 2900);
insert into sys_menu select 2901, '执行比对', 2009, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:socialBaseCompare:compare', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 2901);
insert into sys_menu select 2902, '导出', 2009, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:socialBaseCompare:export', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 2902);

insert into sys_menu
select 2010, '个税比对', 2040, 3, 'taxCompare', 'ygb/taxCompare/index', '', '', 1, 0, 'C', '0', '0', 'ygb:taxCompare:list', 'example', 'admin', sysdate(), '', null, '个税比对菜单'
from dual where not exists (select 1 from sys_menu where menu_id = 2010);
insert into sys_menu select 3000, '比对查询', 2010, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:taxCompare:query', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3000);
insert into sys_menu select 3001, '模拟同步', 2010, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:taxCompare:sync', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3001);
insert into sys_menu select 3002, '执行比对', 2010, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:taxCompare:compare', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3002);
insert into sys_menu select 3003, '导出', 2010, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:taxCompare:export', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3003);

insert into sys_menu
select 2011, '漏保清单', 2040, 4, 'uninsuredList', 'ygb/uninsuredList/index', '', '', 1, 0, 'C', '0', '0', 'ygb:uninsuredList:list', 'clipboard', 'admin', sysdate(), '', null, '漏保清单菜单'
from dual where not exists (select 1 from sys_menu where menu_id = 2011);
insert into sys_menu select 3100, '清单查询', 2011, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:uninsuredList:query', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3100);
insert into sys_menu select 3101, '生成清单', 2011, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:uninsuredList:generate', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3101);
insert into sys_menu select 3102, '处置更新', 2011, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:uninsuredList:handle', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3102);
insert into sys_menu select 3103, '导出', 2011, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:uninsuredList:export', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3103);

insert into sys_menu
select 2012, '用工比例监控', 2040, 5, 'employmentRatio', 'ygb/employmentRatio/index', '', '', 1, 0, 'C', '0', '0', 'ygb:employmentRatio:list', 'chart', 'admin', sysdate(), '', null, '用工比例监控菜单'
from dual where not exists (select 1 from sys_menu where menu_id = 2012);
insert into sys_menu select 3200, '比例查询', 2012, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:employmentRatio:query', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3200);
insert into sys_menu select 3201, '执行计算', 2012, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:employmentRatio:calculate', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3201);
insert into sys_menu select 3202, '导出', 2012, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:employmentRatio:export', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3202);

insert into sys_menu
select 2013, '假外包识别', 2040, 6, 'fakeOutsourcing', 'ygb/fakeOutsourcing/index', '', '', 1, 0, 'C', '0', '0', 'ygb:fakeOutsourcing:list', 'education', 'admin', sysdate(), '', null, '假外包识别菜单'
from dual where not exists (select 1 from sys_menu where menu_id = 2013);
insert into sys_menu select 3300, '识别查询', 2013, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:fakeOutsourcing:query', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3300);
insert into sys_menu select 3301, '执行识别', 2013, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:fakeOutsourcing:analyze', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3301);
insert into sys_menu select 3302, '导出', 2013, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:fakeOutsourcing:export', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3302);

insert into sys_menu
select 2014, '设备管理', 2060, 1, 'device', 'ygb/device/index', '', '', 1, 0, 'C', '0', '0', 'ygb:device:list', 'build', 'admin', sysdate(), '', null, '设备管理菜单'
from dual where not exists (select 1 from sys_menu where menu_id = 2014);
insert into sys_menu select 3400, '设备查询', 2014, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:device:query', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3400);
insert into sys_menu select 3401, '设备新增', 2014, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:device:add', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3401);
insert into sys_menu select 3402, '设备修改', 2014, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:device:edit', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3402);
insert into sys_menu select 3403, '设备删除', 2014, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:device:remove', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3403);
insert into sys_menu select 3404, '设备导出', 2014, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:device:export', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3404);
insert into sys_menu select 3405, '锁机', 2014, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:device:lock', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3405);
insert into sys_menu select 3406, '解锁', 2014, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:device:unlock', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3406);
insert into sys_menu select 3407, '授权', 2014, 8, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:device:authorize', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3407);
insert into sys_menu select 3408, '模拟心跳', 2014, 9, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:device:heartbeat', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3408);
insert into sys_menu select 3409, '模拟AI事件', 2014, 10, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:device:aiEvent', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3409);

insert into sys_menu
select 2015, '工伤事件', 2060, 2, 'injuryEvent', 'ygb/injuryEvent/index', '', '', 1, 0, 'C', '0', '0', 'ygb:injuryEvent:list', 'bug', 'admin', sysdate(), '', null, '工伤事件菜单'
from dual where not exists (select 1 from sys_menu where menu_id = 2015);
insert into sys_menu select 3500, '事件查询', 2015, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:injuryEvent:query', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3500);
insert into sys_menu select 3501, '事件新增', 2015, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:injuryEvent:add', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3501);
insert into sys_menu select 3502, '事件修改', 2015, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:injuryEvent:edit', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3502);
insert into sys_menu select 3503, '导出', 2015, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:injuryEvent:export', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3503);
insert into sys_menu select 3504, '状态流转', 2015, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:injuryEvent:flow', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3504);

insert into sys_menu
select 2016, '预防项目', 2060, 3, 'preventionProject', 'ygb/preventionProject/index', '', '', 1, 0, 'C', '0', '0', 'ygb:preventionProject:list', 'nested', 'admin', sysdate(), '', null, '预防项目菜单'
from dual where not exists (select 1 from sys_menu where menu_id = 2016);
insert into sys_menu select 3600, '项目查询', 2016, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:preventionProject:query', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3600);
insert into sys_menu select 3601, '项目新增', 2016, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:preventionProject:add', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3601);
insert into sys_menu select 3602, '项目修改', 2016, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:preventionProject:edit', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3602);
insert into sys_menu select 3603, '项目删除', 2016, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:preventionProject:remove', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3603);
insert into sys_menu select 3604, '导出', 2016, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:preventionProject:export', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3604);

insert into sys_menu
select 2017, '预警中心', 2080, 1, 'warning', 'ygb/warning/index', '', '', 1, 0, 'C', '0', '0', 'ygb:warning:list', 'message', 'admin', sysdate(), '', null, '预警中心菜单'
from dual where not exists (select 1 from sys_menu where menu_id = 2017);
insert into sys_menu select 3700, '预警查询', 2017, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:warning:query', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3700);
insert into sys_menu select 3701, '预警处置', 2017, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:warning:handle', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3701);
insert into sys_menu select 3702, '导出', 2017, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:warning:export', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3702);

insert into sys_menu
select 2018, '预警规则', 2080, 2, 'warningRule', 'ygb/warningRule/index', '', '', 1, 0, 'C', '0', '0', 'ygb:warningRule:list', 'dict', 'admin', sysdate(), '', null, '预警规则菜单'
from dual where not exists (select 1 from sys_menu where menu_id = 2018);
insert into sys_menu select 3800, '规则查询', 2018, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:warningRule:query', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3800);
insert into sys_menu select 3801, '规则新增', 2018, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:warningRule:add', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3801);
insert into sys_menu select 3802, '规则修改', 2018, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:warningRule:edit', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3802);
insert into sys_menu select 3803, '规则删除', 2018, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:warningRule:remove', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3803);
insert into sys_menu select 3804, '导出', 2018, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:warningRule:export', '#', 'admin', sysdate(), '', null, '' from dual where not exists (select 1 from sys_menu where menu_id = 3804);

insert ignore into sys_role_menu (role_id, menu_id)
select 2, menu_id from sys_menu where menu_id in (2040, 2060, 2080);
insert ignore into sys_role_menu (role_id, menu_id)
select 2, menu_id from sys_menu where menu_id between 2008 and 2018;
insert ignore into sys_role_menu (role_id, menu_id)
select 2, menu_id from sys_menu where menu_id between 2800 and 3804;













