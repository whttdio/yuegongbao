set names utf8mb4;

create table if not exists t_aq_insurance (
  policy_id               bigint(20)      not null auto_increment comment '保单ID',
  stat_month              varchar(7)      not null comment '统计月份',
  enterprise_id           bigint(20)      not null comment '企业ID',
  enterprise_name         varchar(100)    default '' comment '企业名称',
  region_code             varchar(6)      default '' comment '区域编码',
  insurer_name            varchar(100)    default '' comment '保险机构',
  policy_no               varchar(64)     not null comment '保单号',
  premium                 decimal(16,2)   default 0.00 comment '保费',
  start_date              date            default null comment '起保日期',
  end_date                date            default null comment '止保日期',
  policy_status           char(1)         default '1' comment '状态（0未生效 1有效 2即将到期 3已过期）',
  insured_person_count    int(11)         default 0 comment '参保人数',
  prevention_fund_ratio   decimal(8,2)    default 0.00 comment '预防费比例',
  prevention_fund_amount  decimal(16,2)   default 0.00 comment '预防费计提金额',
  used_fund_amount        decimal(16,2)   default 0.00 comment '已使用金额',
  remaining_fund_amount   decimal(16,2)   default 0.00 comment '可用余额',
  source_serial_no        varchar(64)     default '' comment '外部流水号',
  source_status           varchar(32)     default '' comment '来源状态',
  source_message          varchar(255)    default '' comment '来源消息',
  callback_time           datetime        default null comment '回调时间',
  raw_payload             text comment '原始报文',
  del_flag                char(1)         default '0' comment '删除标识（0存在 2删除）',
  create_by               varchar(64)     default '' comment '创建者',
  create_time             datetime        default null comment '创建时间',
  update_by               varchar(64)     default '' comment '更新者',
  update_time             datetime        default null comment '更新时间',
  remark                  varchar(500)    default null comment '备注',
  primary key (policy_id),
  unique key uk_aq_insurance_scope (stat_month, enterprise_id),
  unique key uk_aq_policy_no (policy_no),
  key idx_aq_insurance_status (policy_status),
  key idx_aq_insurance_region (region_code)
) engine=innodb auto_increment=96000 default charset=utf8mb4 comment='安责险投保监管表';

create table if not exists t_prevention_fund (
  fund_id                 bigint(20)      not null auto_increment comment '资金池ID',
  policy_id               bigint(20)      not null comment '保单ID',
  stat_month              varchar(7)      not null comment '统计月份',
  enterprise_id           bigint(20)      not null comment '企业ID',
  enterprise_name         varchar(100)    default '' comment '企业名称',
  region_code             varchar(6)      default '' comment '区域编码',
  accrued_amount          decimal(16,2)   default 0.00 comment '计提金额',
  used_amount             decimal(16,2)   default 0.00 comment '已使用金额',
  remaining_amount        decimal(16,2)   default 0.00 comment '可用余额',
  fund_status             char(1)         default '1' comment '资金状态（0待计提 1可使用 2使用中 3已核销）',
  usage_purpose           varchar(255)    default '' comment '使用用途',
  evidence_url            varchar(255)    default '' comment '凭证地址',
  last_settle_time        datetime        default null comment '最近结算时间',
  source_mode             varchar(16)     default 'stub' comment '来源模式',
  del_flag                char(1)         default '0' comment '删除标识（0存在 2删除）',
  create_by               varchar(64)     default '' comment '创建者',
  create_time             datetime        default null comment '创建时间',
  update_by               varchar(64)     default '' comment '更新者',
  update_time             datetime        default null comment '更新时间',
  remark                  varchar(500)    default null comment '备注',
  primary key (fund_id),
  unique key uk_prevention_fund_scope (policy_id, stat_month),
  key idx_prevention_fund_status (fund_status),
  key idx_prevention_fund_region (region_code)
) engine=innodb auto_increment=97000 default charset=utf8mb4 comment='事故预防资金池表';

update sys_menu
set menu_name = '安责险管理',
    parent_id = 0,
    order_num = 13,
    path = 'ygb-aqins',
    icon = 'money',
    remark = '粤工保安责险管理目录'
where menu_id = 3960;

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3960, '安责险管理', 0, 13, 'ygb-aqins', null, '', '', 1, 0, 'M', '0', '0', '', 'money', 'admin', sysdate(), '', null, '粤工保安责险管理目录'
from dual where not exists (select 1 from sys_menu where menu_id = 3960);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3961, '投保监管', 3960, 1, 'aqInsurance', 'ygb/aqInsurance/index', '', '', 1, 0, 'C', '0', '0', 'ygb:aqInsurance:list', 'money', 'admin', sysdate(), '', null, '安责险投保监管菜单'
from dual where not exists (select 1 from sys_menu where menu_id = 3961);
insert into sys_menu
select 3965, '保单查询', 3961, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:aqInsurance:query', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3965);
insert into sys_menu
select 3966, '模拟同步', 3961, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:aqInsurance:sync', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3966);
insert into sys_menu
select 3967, '导出', 3961, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:aqInsurance:export', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3967);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 3962, '事故预防资金池', 3960, 2, 'preventionFund', 'ygb/preventionFund/index', '', '', 1, 0, 'C', '0', '0', 'ygb:preventionFund:list', 'wallet', 'admin', sysdate(), '', null, '事故预防资金池菜单'
from dual where not exists (select 1 from sys_menu where menu_id = 3962);
insert into sys_menu
select 3968, '资金查询', 3962, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:preventionFund:query', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3968);
insert into sys_menu
select 3969, '资金修改', 3962, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:preventionFund:edit', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3969);
insert into sys_menu
select 3970, '导出', 3962, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:preventionFund:export', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 3970);

insert ignore into sys_role_menu (role_id, menu_id)
select r.role_id, m.menu_id
from sys_role r
join sys_menu m on m.menu_id in (3960, 3961, 3962, 3965, 3966, 3967, 3968, 3969, 3970)
where r.role_id in (1, 2, 101, 102, 103, 104, 105);
