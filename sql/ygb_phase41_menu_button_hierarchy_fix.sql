set names utf8mb4;

-- 1) 修复 phase40 执行后写坏的中文菜单名。
update sys_menu
set menu_name = case menu_id
  when 5600 then '治理对象台账'
  when 5601 then '单位管理'
  when 5602 then '人员管理'
  when 5610 then '风险处置中心'
  when 5611 then '预警中心'
  when 5612 then '预警规则'
  when 5613 then '漏保清单'
  when 5614 then '工伤事件'
  when 5620 then '设备安全治理'
  when 5630 then '应急驾驶舱'
  when 5631 then '驾驶舱总览'
  when 5640 then 'AI监测研判'
  when 5641 then 'AI报告中心'
  when 5642 then '模型配置'
  when 5650 then '安责险协同'
  when 5651 then '投保监管'
  when 5652 then '事故预防资金池'
  when 5653 then '预防项目'
  when 5660 then '风险信用'
  when 5661 then '企业信用评分'
  when 5670 then '技术防范'
  when 5671 then '高处作业申报报备'
  when 5741 then 'AI报告中心'
  when 5742 then '评分模型配置'
  when 5761 then '投保监管'
  when 5762 then '事故预防资金池'
  else menu_name
end
where menu_id in (
  5600, 5601, 5602,
  5610, 5611, 5612, 5613, 5614,
  5620,
  5630, 5631,
  5640, 5641, 5642,
  5650, 5651, 5652, 5653,
  5660, 5661,
  5670, 5671,
  5741, 5742, 5761, 5762
);

-- 2) 修复 AI 菜单按钮层级。旧 3951-3954 已被统计报表占用，改用新按钮 ID。
update sys_menu
set parent_id = 5741,
    order_num = 1,
    menu_name = '报告查询',
    menu_type = 'F',
    perms = 'ygb:aiReport:query',
    portal_scope = 'ygb'
where menu_id = 3950;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5772, '生成报告', 5741, 2, '', '', '', '',
  1, 0, 'F', '0', '0', 'ygb:aiReport:generate', '#',
  'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5772);

update sys_menu
set parent_id = 5741,
    order_num = 2,
    menu_name = '生成报告',
    menu_type = 'F',
    perms = 'ygb:aiReport:generate',
    portal_scope = 'ygb'
where menu_id = 5772;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5773, '导出', 5741, 3, '', '', '', '',
  1, 0, 'F', '0', '0', 'ygb:aiReport:export', '#',
  'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5773);

update sys_menu
set parent_id = 5741,
    order_num = 3,
    menu_name = '导出',
    menu_type = 'F',
    perms = 'ygb:aiReport:export',
    portal_scope = 'ygb'
where menu_id = 5773;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5774, '配置查询', 5742, 1, '', '', '', '',
  1, 0, 'F', '0', '0', 'ygb:aiReportConfig:query', '#',
  'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5774);

update sys_menu
set parent_id = 5742,
    order_num = 1,
    menu_name = '配置查询',
    menu_type = 'F',
    perms = 'ygb:aiReportConfig:query',
    portal_scope = 'ygb'
where menu_id = 5774;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5775, '配置新增', 5742, 2, '', '', '', '',
  1, 0, 'F', '0', '0', 'ygb:aiReportConfig:add', '#',
  'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5775);

update sys_menu
set parent_id = 5742,
    order_num = 2,
    menu_name = '配置新增',
    menu_type = 'F',
    perms = 'ygb:aiReportConfig:add',
    portal_scope = 'ygb'
where menu_id = 5775;

update sys_menu
set parent_id = 5742,
    order_num = 3,
    menu_name = '配置修改',
    menu_type = 'F',
    perms = 'ygb:aiReportConfig:edit',
    portal_scope = 'ygb'
where menu_id = 3955;

update sys_menu
set parent_id = 5742,
    order_num = 4,
    menu_name = '启用版本',
    menu_type = 'F',
    perms = 'ygb:aiReportConfig:activate',
    portal_scope = 'ygb'
where menu_id = 3956;

update sys_menu
set parent_id = 5742,
    order_num = 5,
    menu_name = '导出',
    menu_type = 'F',
    perms = 'ygb:aiReportConfig:export',
    portal_scope = 'ygb'
where menu_id = 3957;

-- 3) 修复安责险 / 资金池按钮层级。
update sys_menu
set parent_id = 5761,
    order_num = 1,
    menu_name = '保单查询',
    menu_type = 'F',
    perms = 'ygb:aqInsurance:query',
    portal_scope = 'ygb'
where menu_id = 3965;

update sys_menu
set parent_id = 5761,
    order_num = 2,
    menu_name = '模拟同步',
    menu_type = 'F',
    perms = 'ygb:aqInsurance:sync',
    portal_scope = 'ygb'
where menu_id = 3966;

update sys_menu
set parent_id = 5761,
    order_num = 3,
    menu_name = '导出',
    menu_type = 'F',
    perms = 'ygb:aqInsurance:export',
    portal_scope = 'ygb'
where menu_id = 3967;

update sys_menu
set parent_id = 5762,
    order_num = 1,
    menu_name = '资金查询',
    menu_type = 'F',
    perms = 'ygb:preventionFund:query',
    portal_scope = 'ygb'
where menu_id = 3968;

update sys_menu
set parent_id = 5762,
    order_num = 2,
    menu_name = '资金修改',
    menu_type = 'F',
    perms = 'ygb:preventionFund:edit',
    portal_scope = 'ygb'
where menu_id = 3969;

update sys_menu
set parent_id = 5762,
    order_num = 3,
    menu_name = '导出',
    menu_type = 'F',
    perms = 'ygb:preventionFund:export',
    portal_scope = 'ygb'
where menu_id = 3970;

-- 4) 新增按钮授权。
insert into sys_role_menu (role_id, menu_id, portal_scope)
select r.role_id, m.menu_id, 'ygb'
from sys_role r
join sys_menu m on m.menu_id in (5772, 5773, 5774, 5775)
where r.role_key in ('admin', 'ygb_hrss_supervisor', 'ygb_enterprise_admin', 'ygb_enterprise_operator')
on duplicate key update portal_scope = values(portal_scope);
