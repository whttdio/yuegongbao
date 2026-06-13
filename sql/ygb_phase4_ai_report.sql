set names utf8mb4;

create table if not exists t_ai_report_config (
  config_id               bigint(20)      not null auto_increment comment '配置ID',
  region_code             varchar(6)      not null comment '区域编码',
  version                 varchar(32)     not null comment '版本号',
  config_status           char(1)         default '1' comment '状态（0停用 1启用）',
  dimension_weights       text comment '维度权重JSON',
  target_values           text comment '目标值JSON',
  effective_date          date            default null comment '生效日期',
  source_mode             varchar(16)     default 'stub' comment '来源模式',
  create_by               varchar(64)     default '' comment '创建者',
  create_time             datetime        default null comment '创建时间',
  update_by               varchar(64)     default '' comment '更新者',
  update_time             datetime        default null comment '更新时间',
  remark                  varchar(500)    default null comment '备注',
  primary key (config_id),
  key idx_ai_report_cfg_region (region_code),
  key idx_ai_report_cfg_status (config_status),
  key idx_ai_report_cfg_effective (effective_date)
) engine=innodb auto_increment=93000 default charset=utf8mb4 comment='AI监测报告评分模型配置表';

create table if not exists t_ai_report (
  report_id               bigint(20)      not null auto_increment comment '报告ID',
  report_type             varchar(16)     not null comment '报告类型',
  region_code             varchar(6)      not null comment '区域编码',
  period_start            date            not null comment '统计开始日期',
  period_end              date            not null comment '统计结束日期',
  enterprise_type         varchar(16)     default 'ALL' comment '企业类型',
  selected_dimensions     varchar(64)     default 'A,B,C,D,E' comment '评分维度',
  total_score             decimal(6,2)    default 0.00 comment '综合得分',
  risk_level              varchar(16)     default 'LOW' comment '风险等级',
  ranking_no              int(11)         default 1 comment '区域排名',
  config_version          varchar(32)     default '' comment '模型版本',
  report_summary          varchar(500)    default '' comment '结论摘要',
  report_pdf_url          varchar(255)    default '' comment 'PDF地址',
  report_html             longtext comment '报告HTML',
  source_mode             varchar(16)     default 'stub' comment '来源模式',
  generated_time          datetime        default null comment '生成时间',
  del_flag                char(1)         default '0' comment '删除标识（0存在 2删除）',
  create_by               varchar(64)     default '' comment '创建者',
  create_time             datetime        default null comment '创建时间',
  update_by               varchar(64)     default '' comment '更新者',
  update_time             datetime        default null comment '更新时间',
  remark                  varchar(500)    default null comment '备注',
  primary key (report_id),
  unique key uk_ai_report_scope (report_type, region_code, period_start, period_end, enterprise_type),
  key idx_ai_report_region (region_code),
  key idx_ai_report_score (total_score),
  key idx_ai_report_generated (generated_time)
) engine=innodb auto_increment=94000 default charset=utf8mb4 comment='AI监测报告表';

create table if not exists t_ai_report_item (
  item_id                 bigint(20)      not null auto_increment comment '明细ID',
  report_id               bigint(20)      not null comment '报告ID',
  dimension_code          varchar(8)      not null comment '维度编码',
  dimension_name          varchar(50)     default '' comment '维度名称',
  metric_label            varchar(50)     default '' comment '指标名称',
  metric_value            decimal(10,2)   default 0.00 comment '指标值',
  target_value            decimal(10,2)   default 0.00 comment '目标值',
  dimension_weight        decimal(10,2)   default 0.00 comment '维度权重',
  dimension_score         decimal(10,2)   default 0.00 comment '维度得分',
  risk_level              varchar(16)     default 'LOW' comment '风险等级',
  suggestion_text         varchar(500)    default '' comment '整改建议',
  detail_json             text comment '明细JSON',
  sort_no                 int(11)         default 0 comment '排序号',
  primary key (item_id),
  key idx_ai_report_item_report (report_id)
) engine=innodb auto_increment=95000 default charset=utf8mb4 comment='AI监测报告明细表';

update sys_menu
set menu_name = 'AI监测报告',
    parent_id = 0,
    order_num = 12,
    path = 'ygb-ai-report',
    icon = 'education',
    remark = '粤工保AI监测报告目录'
where menu_id = 3940;

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3940, 'AI监测报告', 0, 12, 'ygb-ai-report', null, '', '', 1, 0, 'M', '0', '0', '', 'education', 'admin', sysdate(), '', null, '粤工保AI监测报告目录'
from dual where not exists (select 1 from sys_menu where menu_id = 3940);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3941, 'AI报告中心', 3940, 1, 'aiReport', 'ygb/aiReport/index', '', '', 1, 0, 'C', '0', '0', 'ygb:aiReport:list', 'chart', 'admin', sysdate(), '', null, 'AI监测报告菜单'
from dual where not exists (select 1 from sys_menu where menu_id = 3941);
insert into sys_menu
select 3950, '报告查询', 3941, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:aiReport:query', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3950);
insert into sys_menu
select 3951, '生成报告', 3941, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:aiReport:generate', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3951);
insert into sys_menu
select 3952, '导出', 3941, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:aiReport:export', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3952);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3942, '评分模型配置', 3940, 2, 'aiReportConfig', 'ygb/aiReportConfig/index', '', '', 1, 0, 'C', '0', '0', 'ygb:aiReportConfig:list', 'dict', 'admin', sysdate(), '', null, 'AI评分模型菜单'
from dual where not exists (select 1 from sys_menu where menu_id = 3942);
insert into sys_menu
select 3953, '配置查询', 3942, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:aiReportConfig:query', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3953);
insert into sys_menu
select 3954, '配置新增', 3942, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:aiReportConfig:add', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3954);
insert into sys_menu
select 3955, '配置修改', 3942, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:aiReportConfig:edit', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3955);
insert into sys_menu
select 3956, '启用版本', 3942, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:aiReportConfig:activate', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3956);
insert into sys_menu
select 3957, '导出', 3942, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:aiReportConfig:export', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3957);

insert ignore into sys_role_menu (role_id, menu_id)
select r.role_id, m.menu_id
from sys_role r
join sys_menu m on m.menu_id in (3940, 3941, 3942, 3950, 3951, 3952, 3953, 3954, 3955, 3956, 3957)
where r.role_id in (1, 2, 101, 102, 103, 104, 105);
