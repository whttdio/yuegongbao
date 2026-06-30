-- YGB + AZB PC 合同备案三级核心功能迁移

create table if not exists t_contract_action_log (
  log_id        bigint(20) not null auto_increment comment '日志ID',
  contract_id   bigint(20) default null comment '合同ID',
  contract_no   varchar(64) default '' comment '合同编号',
  template_id   bigint(20) default null comment '模板ID',
  action_type   varchar(64) not null comment '动作类型',
  action_status varchar(32) default '' comment '动作状态',
  action_result varchar(500) default '' comment '动作结果',
  portal_scope  varchar(16) not null default 'ygb' comment '来源门户',
  warning_id    bigint(20) default null comment '关联预警ID',
  action_time   datetime default null comment '动作时间',
  remark        varchar(500) default '' comment '备注',
  create_by     varchar(64) default '' comment '创建者',
  create_time   datetime default null comment '创建时间',
  update_by     varchar(64) default '' comment '更新者',
  update_time   datetime default null comment '更新时间',
  primary key (log_id),
  key idx_contract_action_contract (contract_id, action_type),
  key idx_contract_action_template (template_id, action_type),
  key idx_contract_action_portal (portal_scope, action_type)
) engine=innodb default charset=utf8mb4 comment='合同备案动作日志';

set @review_by_exists := (
  select count(1) from information_schema.columns
  where table_schema = database() and table_name = 't_contract_template' and column_name = 'review_by'
);
set @sql := if(@review_by_exists = 0,
  'alter table t_contract_template add column review_by varchar(64) default '''' comment ''审核人'' after content_text',
  'select 1');
prepare stmt from @sql; execute stmt; deallocate prepare stmt;

set @review_time_exists := (
  select count(1) from information_schema.columns
  where table_schema = database() and table_name = 't_contract_template' and column_name = 'review_time'
);
set @sql := if(@review_time_exists = 0,
  'alter table t_contract_template add column review_time datetime default null comment ''审核时间'' after review_by',
  'select 1');
prepare stmt from @sql; execute stmt; deallocate prepare stmt;

set @review_remark_exists := (
  select count(1) from information_schema.columns
  where table_schema = database() and table_name = 't_contract_template' and column_name = 'review_remark'
);
set @sql := if(@review_remark_exists = 0,
  'alter table t_contract_template add column review_remark varchar(500) default '''' comment ''审核意见'' after review_time',
  'select 1');
prepare stmt from @sql; execute stmt; deallocate prepare stmt;

update t_contract_template
set review_status = 'pending'
where del_flag = '0'
  and (review_status is null or review_status = '');

-- YGB 合同备案菜单收口到真实业务入口
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
values
(9105300, '合同备案', 0, 4, 'contract-filing', null, '', '', 1, 0, 'M', '0', '0', 'ygb', '', 'documentation', 'admin', sysdate(), 'admin', sysdate(), 'phase53 YGB合同备案模块'),
(9105301, '合同列表', 9105300, 1, 'contract', 'ygb/contract/index', '', 'YgbContract', 1, 0, 'C', '0', '0', 'ygb', 'ygb:contract:list', 'form', 'admin', sysdate(), 'admin', sysdate(), 'phase53 YGB合同列表'),
(9105302, '合同到期提醒', 9105300, 2, 'contractExpiry', 'ygb/contract/expiry/index', '', 'YgbContractExpiry', 1, 0, 'C', '0', '0', 'ygb', 'ygb:contract:list', 'timer', 'admin', sysdate(), 'admin', sysdate(), 'phase53 YGB合同到期提醒'),
(9105303, '未备案合同预警', 9105300, 3, 'unfiledWarning', 'ygb/contract/unfiled/index', '', 'YgbContractUnfiled', 1, 0, 'C', '0', '0', 'ygb', 'ygb:contract:list', 'warning', 'admin', sysdate(), 'admin', sysdate(), 'phase53 YGB未备案合同预警'),
(9105304, '合同模板库', 9105300, 4, 'contractTemplate', 'ygb/contractTemplate/index', '', 'YgbContractTemplate', 1, 0, 'C', '0', '0', 'ygb', 'ygb:contractTemplate:list', 'documentation', 'admin', sysdate(), 'admin', sysdate(), 'phase53 YGB合同模板库')
on duplicate key update menu_name = values(menu_name), parent_id = values(parent_id), order_num = values(order_num), path = values(path), component = values(component), route_name = values(route_name), portal_scope = values(portal_scope), perms = values(perms), icon = values(icon), update_by = 'admin', update_time = sysdate(), remark = values(remark);

-- AZB 合同备案协同菜单
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
values
(9105400, '合同备案协同', 0, 4, 'contract-filing', null, '', '', 1, 0, 'M', '0', '0', 'azb', '', 'documentation', 'admin', sysdate(), 'admin', sysdate(), 'phase53 AZB合同备案协同模块'),
(9105401, '合同列表', 9105400, 1, 'contract', 'azb/contract/index', '', 'AzbContract', 1, 0, 'C', '0', '0', 'azb', 'azb:contract:list', 'form', 'admin', sysdate(), 'admin', sysdate(), 'phase53 AZB合同列表'),
(9105402, '合同到期提醒', 9105400, 2, 'contractExpiry', 'azb/contract/expiry/index', '', 'AzbContractExpiry', 1, 0, 'C', '0', '0', 'azb', 'azb:contract:list', 'timer', 'admin', sysdate(), 'admin', sysdate(), 'phase53 AZB合同到期提醒'),
(9105403, '未备案合同预警', 9105400, 3, 'unfiledWarning', 'azb/contract/unfiled/index', '', 'AzbContractUnfiled', 1, 0, 'C', '0', '0', 'azb', 'azb:contract:list', 'warning', 'admin', sysdate(), 'admin', sysdate(), 'phase53 AZB未备案合同预警'),
(9105404, '合同模板库', 9105400, 4, 'contractTemplate', 'azb/contractTemplate/index', '', 'AzbContractTemplate', 1, 0, 'C', '0', '0', 'azb', 'azb:contractTemplate:list', 'documentation', 'admin', sysdate(), 'admin', sysdate(), 'phase53 AZB合同模板库')
on duplicate key update menu_name = values(menu_name), parent_id = values(parent_id), order_num = values(order_num), path = values(path), component = values(component), route_name = values(route_name), portal_scope = values(portal_scope), perms = values(perms), icon = values(icon), update_by = 'admin', update_time = sysdate(), remark = values(remark);

-- YGB 按钮权限
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
values
(9105311, '合同查询', 9105301, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:contract:query', '#', 'admin', sysdate(), 'admin', sysdate(), 'phase53'),
(9105312, '合同新增', 9105301, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:contract:add', '#', 'admin', sysdate(), 'admin', sysdate(), 'phase53'),
(9105313, '合同修改', 9105301, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:contract:edit', '#', 'admin', sysdate(), 'admin', sysdate(), 'phase53'),
(9105314, '合同删除', 9105301, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:contract:remove', '#', 'admin', sysdate(), 'admin', sysdate(), 'phase53'),
(9105315, '合同导出', 9105301, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:contract:export', '#', 'admin', sysdate(), 'admin', sysdate(), 'phase53'),
(9105316, '存证查看', 9105301, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:contract:evidence', '#', 'admin', sysdate(), 'admin', sysdate(), 'phase53'),
(9105321, '批量提醒', 9105302, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:contract:remind', '#', 'admin', sysdate(), 'admin', sysdate(), 'phase53'),
(9105331, '生成预警', 9105303, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:contract:warn', '#', 'admin', sysdate(), 'admin', sysdate(), 'phase53'),
(9105341, '模板查询', 9105304, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:contractTemplate:query', '#', 'admin', sysdate(), 'admin', sysdate(), 'phase53'),
(9105342, '模板新增', 9105304, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:contractTemplate:add', '#', 'admin', sysdate(), 'admin', sysdate(), 'phase53'),
(9105343, '模板修改', 9105304, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:contractTemplate:edit', '#', 'admin', sysdate(), 'admin', sysdate(), 'phase53'),
(9105344, '模板删除', 9105304, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:contractTemplate:remove', '#', 'admin', sysdate(), 'admin', sysdate(), 'phase53'),
(9105345, '模板导出', 9105304, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:contractTemplate:export', '#', 'admin', sysdate(), 'admin', sysdate(), 'phase53'),
(9105346, '模板状态', 9105304, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:contractTemplate:status', '#', 'admin', sysdate(), 'admin', sysdate(), 'phase53'),
(9105347, '提交审核', 9105304, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:contractTemplate:submit', '#', 'admin', sysdate(), 'admin', sysdate(), 'phase53'),
(9105348, '模板审核', 9105304, 8, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:contractTemplate:review', '#', 'admin', sysdate(), 'admin', sysdate(), 'phase53')
on duplicate key update menu_name = values(menu_name), parent_id = values(parent_id), perms = values(perms), portal_scope = values(portal_scope), update_time = sysdate();

-- AZB 按钮权限
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
values
(9105411, '合同查询', 9105401, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'azb:contract:query', '#', 'admin', sysdate(), 'admin', sysdate(), 'phase53'),
(9105415, '合同导出', 9105401, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'azb:contract:export', '#', 'admin', sysdate(), 'admin', sysdate(), 'phase53'),
(9105416, '存证查看', 9105401, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'azb:contract:evidence', '#', 'admin', sysdate(), 'admin', sysdate(), 'phase53'),
(9105421, '协同提醒', 9105402, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'azb:contract:remind', '#', 'admin', sysdate(), 'admin', sysdate(), 'phase53'),
(9105431, '风险核验', 9105403, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'azb:contract:warn', '#', 'admin', sysdate(), 'admin', sysdate(), 'phase53'),
(9105441, '模板查询', 9105404, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'azb:contractTemplate:query', '#', 'admin', sysdate(), 'admin', sysdate(), 'phase53'),
(9105445, '模板导出', 9105404, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'azb:contractTemplate:export', '#', 'admin', sysdate(), 'admin', sysdate(), 'phase53'),
(9105448, '模板审核协同', 9105404, 8, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'azb:contractTemplate:review', '#', 'admin', sysdate(), 'admin', sysdate(), 'phase53')
on duplicate key update menu_name = values(menu_name), parent_id = values(parent_id), perms = values(perms), portal_scope = values(portal_scope), update_time = sysdate();

insert into sys_role_menu (role_id, menu_id, portal_scope)
select r.role_id, m.menu_id, m.portal_scope
from sys_role r
join sys_menu m on m.menu_id between 9105300 and 9105448
where r.del_flag = '0'
  and (r.role_key = 'admin' or r.role_key like '%admin%' or r.allowed_portal_scope in ('both', m.portal_scope))
on duplicate key update portal_scope = values(portal_scope);
