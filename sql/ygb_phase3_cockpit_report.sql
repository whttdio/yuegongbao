set names utf8mb4;

create table if not exists t_cockpit_snapshot (
  snapshot_id               bigint(20)      not null auto_increment comment '快照ID',
  stat_date                 date            not null comment '统计日期',
  region_code               varchar(6)      not null comment '区域编码',
  dispatch_company_count    int(11)         default 0 comment '派遣企业数',
  employer_count            int(11)         default 0 comment '用工单位数',
  dispatched_worker_count   int(11)         default 0 comment '派遣员工数',
  high_risk_enterprise_count int(11)        default 0 comment '高危企业数',
  insurance_rate            decimal(8,2)    default 0.00 comment '工伤参保率',
  aq_insurance_rate         decimal(8,2)    default 0.00 comment '安责险覆盖率',
  today_warning_count       int(11)         default 0 comment '当日预警数',
  expand_completion_rate    decimal(8,2)    default 0.00 comment '扩面完成率',
  new_injury_rate           decimal(8,2)    default 0.00 comment '新业态职业伤害发生率',
  online_device_count       int(11)         default 0 comment '在线设备数',
  pending_warning_count     int(11)         default 0 comment '待处置预警数',
  overdue_injury_count      int(11)         default 0 comment '超期工伤事件数',
  source_mode               varchar(16)     default 'stub' comment '来源模式',
  create_by                 varchar(64)     default '' comment '创建者',
  create_time               datetime        default null comment '创建时间',
  update_by                 varchar(64)     default '' comment '更新者',
  update_time               datetime        default null comment '更新时间',
  remark                    varchar(500)    default null comment '备注',
  primary key (snapshot_id),
  unique key uk_cockpit_snapshot_scope (stat_date, region_code),
  key idx_cockpit_snapshot_region (region_code)
) engine=innodb auto_increment=90000 default charset=utf8mb4 comment='驾驶舱指标快照表';

create table if not exists t_cockpit_map_feature (
  feature_id                bigint(20)      not null auto_increment comment '要素ID',
  stat_date                 date            not null comment '统计日期',
  region_code               varchar(6)      not null comment '区域编码',
  feature_type              varchar(20)     default 'ENTERPRISE' comment '要素类型',
  feature_name              varchar(100)    default '' comment '要素名称',
  geometry_type             varchar(20)     default 'Point' comment '几何类型',
  geometry_json             text comment '几何坐标JSON',
  feature_status            char(1)         default '1' comment '状态（0离线 1在线 2预警）',
  source_mode               varchar(16)     default 'stub' comment '来源模式',
  properties_json           text comment '属性JSON',
  sort_no                   int(11)         default 0 comment '排序号',
  create_by                 varchar(64)     default '' comment '创建者',
  create_time               datetime        default null comment '创建时间',
  update_by                 varchar(64)     default '' comment '更新者',
  update_time               datetime        default null comment '更新时间',
  remark                    varchar(500)    default null comment '备注',
  primary key (feature_id),
  key idx_cockpit_map_scope (stat_date, region_code)
) engine=innodb auto_increment=90500 default charset=utf8mb4 comment='驾驶舱地图要素表';

create table if not exists t_stat_report (
  report_id                 bigint(20)      not null auto_increment comment '报表ID',
  report_code               varchar(32)     not null comment '报表编码',
  report_name               varchar(100)    not null comment '报表名称',
  stat_month                varchar(7)      not null comment '统计月份',
  region_code               varchar(6)      not null comment '区域编码',
  report_status             char(1)         default '1' comment '状态（0草稿 1已生成 2已归档）',
  metric_count              int(11)         default 0 comment '核心数量',
  metric_amount             decimal(16,2)   default 0.00 comment '核心数值',
  metric_rate               decimal(10,2)   default 0.00 comment '核心比率',
  report_summary            varchar(500)    default '' comment '摘要',
  attachment_url            varchar(255)    default '' comment '附件地址',
  source_mode               varchar(16)     default 'stub' comment '来源模式',
  generated_time            datetime        default null comment '生成时间',
  del_flag                  char(1)         default '0' comment '删除标志（0存在 2删除）',
  create_by                 varchar(64)     default '' comment '创建者',
  create_time               datetime        default null comment '创建时间',
  update_by                 varchar(64)     default '' comment '更新者',
  update_time               datetime        default null comment '更新时间',
  remark                    varchar(500)    default null comment '备注',
  primary key (report_id),
  unique key uk_stat_report_scope (report_code, stat_month, region_code),
  key idx_stat_report_month (stat_month),
  key idx_stat_report_region (region_code)
) engine=innodb auto_increment=91000 default charset=utf8mb4 comment='统计报表主表';

create table if not exists t_stat_report_item (
  item_id                   bigint(20)      not null auto_increment comment '明细项ID',
  report_id                 bigint(20)      not null comment '报表ID',
  item_category             varchar(32)     default '' comment '分类',
  item_name                 varchar(100)    default '' comment '名称',
  item_dimension            varchar(100)    default '' comment '维度编码',
  metric_count              int(11)         default 0 comment '数量',
  metric_value              decimal(16,2)   default 0.00 comment '数值',
  metric_rate               decimal(10,2)   default 0.00 comment '比率',
  sort_no                   int(11)         default 0 comment '排序号',
  primary key (item_id),
  key idx_stat_report_item_report (report_id)
) engine=innodb auto_increment=92000 default charset=utf8mb4 comment='统计报表明细表';

update sys_menu
set menu_name = '综合驾驶舱',
    parent_id = 0,
    order_num = 10,
    path = 'ygb-cockpit',
    icon = 'dashboard',
    remark = '粤工保综合驾驶舱目录'
where menu_id = 3900;

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3900, '综合驾驶舱', 0, 10, 'ygb-cockpit', null, '', '', 1, 0, 'M', '0', '0', '', 'dashboard', 'admin', sysdate(), '', null, '粤工保综合驾驶舱目录'
from dual where not exists (select 1 from sys_menu where menu_id = 3900);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3901, '驾驶舱总览', 3900, 1, 'cockpit', 'ygb/cockpit/index', '', '', 1, 0, 'C', '0', '0', 'ygb:cockpit:list', 'dashboard', 'admin', sysdate(), '', null, '综合驾驶舱菜单'
from dual where not exists (select 1 from sys_menu where menu_id = 3901);
insert into sys_menu
select 3910, '查询', 3901, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:cockpit:query', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3910);
insert into sys_menu
select 3911, '导出', 3901, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:cockpit:export', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3911);

update sys_menu
set menu_name = '统计报表',
    parent_id = 0,
    order_num = 11,
    path = 'ygb-report',
    icon = 'table',
    remark = '粤工保统计报表目录'
where menu_id = 3920;

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3920, '统计报表', 0, 11, 'ygb-report', null, '', '', 1, 0, 'M', '0', '0', '', 'table', 'admin', sysdate(), '', null, '粤工保统计报表目录'
from dual where not exists (select 1 from sys_menu where menu_id = 3920);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3921, '统计月报', 3920, 1, 'statReport', 'ygb/statReport/index', '', '', 1, 0, 'C', '0', '0', 'ygb:statReport:list', 'table', 'admin', sysdate(), '', null, '统计报表菜单'
from dual where not exists (select 1 from sys_menu where menu_id = 3921);
insert into sys_menu
select 3930, '查询', 3921, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:query', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3930);
insert into sys_menu
select 3931, '生成报表', 3921, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:generate', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3931);
insert into sys_menu
select 3932, '导出', 3921, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:export', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3932);

insert ignore into sys_role_menu (role_id, menu_id)
select r.role_id, m.menu_id
from sys_role r
join sys_menu m on m.menu_id in (3900, 3901, 3910, 3911, 3920, 3921, 3930, 3931, 3932)
where r.role_id in (1, 2, 101, 102, 103, 104, 105);
