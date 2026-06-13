set names utf8mb4;

create table if not exists t_credit_score (
  score_id               bigint(20)      not null auto_increment comment '评分ID',
  stat_month             varchar(7)      not null comment '统计月份',
  enterprise_id          bigint(20)      not null comment '企业ID',
  enterprise_name        varchar(100)    default '' comment '企业名称',
  region_code            varchar(6)      default '' comment '区域编码',
  enterprise_type        char(1)         default '' comment '企业类型',
  contract_score         decimal(8,2)    default 0.00 comment '合同备案得分',
  attendance_score       decimal(8,2)    default 0.00 comment '考勤归集得分',
  salary_score           decimal(8,2)    default 0.00 comment '工资发放得分',
  social_tax_score       decimal(8,2)    default 0.00 comment '社税合规得分',
  safety_score           decimal(8,2)    default 0.00 comment '安全保障得分',
  governance_score       decimal(8,2)    default 0.00 comment '预警治理得分',
  total_score            decimal(8,2)    default 0.00 comment '信用总分',
  credit_level           char(1)         default 'C' comment '信用等级',
  color_code             varchar(16)     default 'YELLOW' comment '红黄绿码',
  rank_no                int(11)         default 0 comment '排名',
  factor_json            text comment '因素明细JSON',
  summary_text           varchar(500)    default '' comment '摘要',
  warning_status         char(1)         default '0' comment '预警状态（0未预警 1已预警）',
  source_mode            varchar(16)     default 'internal' comment '来源模式',
  evaluate_time          datetime        default null comment '评估时间',
  del_flag               char(1)         default '0' comment '删除标识（0存在 2删除）',
  create_by              varchar(64)     default '' comment '创建者',
  create_time            datetime        default null comment '创建时间',
  update_by              varchar(64)     default '' comment '更新者',
  update_time            datetime        default null comment '更新时间',
  remark                 varchar(500)    default null comment '备注',
  primary key (score_id),
  unique key uk_credit_score_scope (stat_month, enterprise_id),
  key idx_credit_score_region (region_code),
  key idx_credit_score_level (credit_level),
  key idx_credit_score_color (color_code)
) engine=innodb auto_increment=98000 default charset=utf8mb4 comment='企业信用评分表';

update sys_menu
set menu_name = '信用评价',
    parent_id = 0,
    order_num = 14,
    path = 'ygb-credit',
    icon = 'chart',
    remark = '粤工保信用评价目录'
where menu_id = 3980;

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3980, '信用评价', 0, 14, 'ygb-credit', null, '', '', 1, 0, 'M', '0', '0', '', 'chart', 'admin', sysdate(), '', null, '粤工保信用评价目录'
from dual where not exists (select 1 from sys_menu where menu_id = 3980);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3981, '企业信用评分', 3980, 1, 'creditScore', 'ygb/creditScore/index', '', '', 1, 0, 'C', '0', '0', 'ygb:creditScore:list', 'chart', 'admin', sysdate(), '', null, '企业信用评分菜单'
from dual where not exists (select 1 from sys_menu where menu_id = 3981);

insert into sys_menu
select 3985, '评分查询', 3981, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:creditScore:query', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3985);

insert into sys_menu
select 3986, '生成评分', 3981, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:creditScore:generate', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3986);

insert into sys_menu
select 3987, '评分导出', 3981, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:creditScore:export', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3987);

insert ignore into sys_role_menu (role_id, menu_id)
select r.role_id, m.menu_id
from sys_role r
join sys_menu m on m.menu_id in (3980, 3981, 3985, 3986, 3987)
where r.role_id in (1, 2, 101, 102, 103, 104, 105);
