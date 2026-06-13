set names utf8mb4;

create table if not exists t_newform_worker (
  worker_record_id        bigint(20)      not null auto_increment comment '记录ID',
  stat_month              varchar(7)      not null comment '统计月份',
  enterprise_id           bigint(20)      not null comment '企业ID',
  enterprise_name         varchar(100)    default '' comment '企业名称',
  person_id               bigint(20)      not null comment '人员ID',
  person_name             varchar(30)     default '' comment '人员姓名',
  id_card                 varchar(18)     default '' comment '身份证号',
  region_code             varchar(6)      default '' comment '区域编码',
  platform_name           varchar(100)    default '' comment '平台企业',
  employment_type         varchar(50)     default '' comment '从业类型',
  insurance_status        char(1)         default '0' comment '参保状态',
  injury_insurance_status char(1)         default '0' comment '职业伤害参保状态',
  monthly_income          decimal(10,2)   default 0.00 comment '月收入',
  warning_status          char(1)         default '0' comment '预警状态',
  source_serial_no        varchar(64)     default '' comment '来源流水号',
  source_status           varchar(32)     default '' comment '来源状态',
  source_message          varchar(255)    default '' comment '来源消息',
  callback_time           datetime        default null comment '回调时间',
  raw_payload             longtext comment '原始报文快照',
  del_flag                char(1)         default '0' comment '删除标识（0存在 2删除）',
  create_by               varchar(64)     default '' comment '创建者',
  create_time             datetime        default null comment '创建时间',
  update_by               varchar(64)     default '' comment '更新者',
  update_time             datetime        default null comment '更新时间',
  remark                  varchar(500)    default null comment '备注',
  primary key (worker_record_id),
  key idx_newform_scope (stat_month, enterprise_id),
  key idx_newform_person (person_id),
  key idx_newform_region (region_code)
) engine=innodb auto_increment=100000 default charset=utf8mb4 comment='新业态人员库';

create table if not exists t_occupation_monitor (
  monitor_id                bigint(20)      not null auto_increment comment '监测ID',
  stat_month                varchar(7)      not null comment '统计月份',
  region_code               varchar(6)      default '' comment '区域编码',
  industry_type             varchar(50)     default '' comment '行业类型',
  enterprise_count          int(11)         default 0 comment '企业数量',
  worker_count              int(11)         default 0 comment '从业人数',
  case_count                int(11)         default 0 comment '发病人数',
  high_risk_enterprise_count int(11)        default 0 comment '高风险企业数',
  incidence_rate            decimal(8,2)    default 0.00 comment '千人发病率',
  warning_level             char(1)         default '0' comment '预警级别',
  warning_status            char(1)         default '0' comment '预警状态',
  source_channel            varchar(50)     default '' comment '来源渠道',
  source_serial_no          varchar(64)     default '' comment '来源流水号',
  source_status             varchar(32)     default '' comment '来源状态',
  source_message            varchar(255)    default '' comment '来源消息',
  callback_time             datetime        default null comment '回调时间',
  raw_payload               longtext comment '原始报文快照',
  del_flag                  char(1)         default '0' comment '删除标识（0存在 2删除）',
  create_by                 varchar(64)     default '' comment '创建者',
  create_time               datetime        default null comment '创建时间',
  update_by                 varchar(64)     default '' comment '更新者',
  update_time               datetime        default null comment '更新时间',
  remark                    varchar(500)    default null comment '备注',
  primary key (monitor_id),
  key idx_occupation_scope (stat_month, region_code),
  key idx_occupation_industry (industry_type)
) engine=innodb auto_increment=101000 default charset=utf8mb4 comment='职业病监测';

update sys_menu
set menu_name = '新业态监管',
    parent_id = 0,
    order_num = 15,
    path = 'ygb-newform',
    component = null,
    perms = '',
    icon = 'peoples',
    remark = '粤工保新业态监管目录'
where menu_id = 4000;

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 4000, '新业态监管', 0, 15, 'ygb-newform', null, '', '', 1, 0, 'M', '0', '0', '', 'peoples', 'admin', sysdate(), '', null, '粤工保新业态监管目录'
from dual where not exists (select 1 from sys_menu where menu_id = 4000);

update sys_menu
set menu_name = '新业态人员库',
    parent_id = 4000,
    order_num = 1,
    path = 'newformWorker',
    component = 'ygb/newformWorker/index',
    perms = 'ygb:newformWorker:list',
    icon = 'user',
    remark = '新业态人员库菜单'
where menu_id = 4001;

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 4001, '新业态人员库', 4000, 1, 'newformWorker', 'ygb/newformWorker/index', '', '', 1, 0, 'C', '0', '0', 'ygb:newformWorker:list', 'user', 'admin', sysdate(), '', null, '新业态人员库菜单'
from dual where not exists (select 1 from sys_menu where menu_id = 4001);

update sys_menu set menu_name = '新业态查询', parent_id = 4001, order_num = 1, perms = 'ygb:newformWorker:query'
where menu_id = 4005;
insert into sys_menu
select 4005, '新业态查询', 4001, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:newformWorker:query', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 4005);

update sys_menu set menu_name = '新业态同步', parent_id = 4001, order_num = 2, perms = 'ygb:newformWorker:sync'
where menu_id = 4006;
insert into sys_menu
select 4006, '新业态同步', 4001, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:newformWorker:sync', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 4006);

update sys_menu set menu_name = '新业态导出', parent_id = 4001, order_num = 3, perms = 'ygb:newformWorker:export'
where menu_id = 4007;
insert into sys_menu
select 4007, '新业态导出', 4001, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:newformWorker:export', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 4007);

update sys_menu
set menu_name = '职业病监管',
    parent_id = 0,
    order_num = 16,
    path = 'ygb-occupation',
    component = null,
    perms = '',
    icon = 'chart',
    remark = '粤工保职业病监管目录'
where menu_id = 4020;

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 4020, '职业病监管', 0, 16, 'ygb-occupation', null, '', '', 1, 0, 'M', '0', '0', '', 'chart', 'admin', sysdate(), '', null, '粤工保职业病监管目录'
from dual where not exists (select 1 from sys_menu where menu_id = 4020);

update sys_menu
set menu_name = '职业病监测',
    parent_id = 4020,
    order_num = 1,
    path = 'occupationMonitor',
    component = 'ygb/occupationMonitor/index',
    perms = 'ygb:occupationMonitor:list',
    icon = 'build',
    remark = '职业病监测菜单'
where menu_id = 4021;

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 4021, '职业病监测', 4020, 1, 'occupationMonitor', 'ygb/occupationMonitor/index', '', '', 1, 0, 'C', '0', '0', 'ygb:occupationMonitor:list', 'build', 'admin', sysdate(), '', null, '职业病监测菜单'
from dual where not exists (select 1 from sys_menu where menu_id = 4021);

update sys_menu set menu_name = '职业病查询', parent_id = 4021, order_num = 1, perms = 'ygb:occupationMonitor:query'
where menu_id = 4025;
insert into sys_menu
select 4025, '职业病查询', 4021, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:occupationMonitor:query', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 4025);

update sys_menu set menu_name = '职业病同步', parent_id = 4021, order_num = 2, perms = 'ygb:occupationMonitor:sync'
where menu_id = 4026;
insert into sys_menu
select 4026, '职业病同步', 4021, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:occupationMonitor:sync', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 4026);

update sys_menu set menu_name = '职业病导出', parent_id = 4021, order_num = 3, perms = 'ygb:occupationMonitor:export'
where menu_id = 4027;
insert into sys_menu
select 4027, '职业病导出', 4021, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:occupationMonitor:export', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 4027);

insert ignore into sys_role_menu (role_id, menu_id)
select r.role_id, m.menu_id
from sys_role r
join sys_menu m on m.menu_id in (4000, 4001, 4005, 4006, 4007, 4020, 4021, 4025, 4026, 4027)
where r.role_id in (1, 2, 101, 102, 103, 104, 105);
