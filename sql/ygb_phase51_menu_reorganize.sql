set names utf8mb4;

-- ygb_phase51_menu_reorganize.sql
-- 菜单一二级目录整理 + 缺失菜单补全 + 无效图标修正（与 sql/sys_menu.sql 同步）
-- 执行后请重新登录或清 Redis 菜单缓存

-- 1) 补全缺失二级菜单
insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4301, '运营总览', 4300, 1, 'overview', 'ygb/operation/overview/index', '', 'YgbOperationOverview',
  1, 0, 'C', '0', '0', 'ygb:operation:overview', 'dashboard',
  'admin', sysdate(), 'admin', sysdate(), '运营后台总览', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4301);

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4302, '企业入驻审核', 4300, 2, 'enterpriseReview', 'ygb/operation/enterpriseReview/index', '', 'YgbOperationEnterpriseReview',
  1, 0, 'C', '0', '0', 'ygb:operationEnterpriseReview:list', 'checkbox',
  'admin', sysdate(), 'admin', sysdate(), '企业入驻审核台账', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4302);

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5885, '信用评价', 0, 19, 'ygb-credit', null, '', '',
  1, 0, 'M', '0', '0', '', 'chart',
  'admin', sysdate(), 'admin', sysdate(), '粤工保信用评价目录（整合版）', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 5885);

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5827, '订阅管理', 3940, 4, 'aiReportSubscription', 'ygb/aiReportSubscription/index', '', 'YgbAiReportSubscription',
  1, 0, 'C', '0', '0', 'ygb:aiReportSubscription:list', 'email',
  'admin', sysdate(), 'admin', sysdate(), 'YGB AI订阅管理', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 5827);

-- 2) 隐藏重复信用目录，子菜单归并
update sys_menu set visible = '1', path = 'ygb-credit-legacy', remark = '已合并至5885信用评价目录（隐藏）' where menu_id = 3980;
update sys_menu set parent_id = 5885, order_num = 1, portal_scope = 'ygb' where menu_id = 3981;
update sys_menu set parent_id = 5885, order_num = 2 where menu_id = 5880;
update sys_menu set parent_id = 5885, order_num = 3 where menu_id = 5881;
update sys_menu set parent_id = 5885, order_num = 4 where menu_id = 5882;

-- 3) 门户/招聘归入运营后台
update sys_menu set parent_id = 4300, order_num = 5, menu_type = 'C', path = 'portalContent', remark = '粤工保官网CMS内容管理' where menu_id = 4100;
update sys_menu set parent_id = 4300, order_num = 7, menu_type = 'C', path = 'workerJob', remark = '官网招聘市场岗位维护' where menu_id = 4200;

-- 4) 基础主数据 / 技术防范：子台账提升为二级
update sys_menu set parent_id = 2000, order_num = 3 where menu_id = 4490;
update sys_menu set parent_id = 2000, order_num = 4 where menu_id = 4491;
update sys_menu set parent_id = 2000, order_num = 5 where menu_id = 4492;
update sys_menu set parent_id = 2000, order_num = 6 where menu_id = 4493;
update sys_menu set parent_id = 2000, order_num = 7 where menu_id = 4494;
update sys_menu set parent_id = 2000, order_num = 8 where menu_id = 4495;
update sys_menu set parent_id = 2000, order_num = 9 where menu_id = 4533;
update sys_menu set parent_id = 2000, order_num = 10 where menu_id = 4534;
update sys_menu set parent_id = 2000, order_num = 11 where menu_id = 4535;
update sys_menu set parent_id = 2000, order_num = 12 where menu_id = 4536;

update sys_menu set parent_id = 4040, order_num = 3 where menu_id = 4526;
update sys_menu set parent_id = 4040, order_num = 4 where menu_id = 4527;
update sys_menu set parent_id = 4040, order_num = 5 where menu_id = 4528;
update sys_menu set parent_id = 4040, order_num = 6 where menu_id = 4529;
update sys_menu set parent_id = 4040, order_num = 7 where menu_id = 4530;
update sys_menu set parent_id = 4040, order_num = 8 where menu_id = 4531;
update sys_menu set parent_id = 4040, order_num = 9 where menu_id = 4532;

-- 5) AI 监测报告目录
update sys_menu set parent_id = 3940, order_num = 1 where menu_id = 5741;
update sys_menu set parent_id = 3940, order_num = 2, icon = 'tool' where menu_id = 5742;
update sys_menu set parent_id = 3940, order_num = 4 where menu_id = 5827;
update sys_menu set parent_id = 3940, order_num = 5 where menu_id = 4014;
update sys_menu set parent_id = 5827, order_num = 1 where menu_id = 4022;
update sys_menu set parent_id = 5827, order_num = 2 where menu_id = 4023;
update sys_menu set parent_id = 5827, order_num = 3 where menu_id = 4024;

-- 6) 运营后台二级排序
update sys_menu set parent_id = 4300, order_num = 1 where menu_id = 4301;
update sys_menu set parent_id = 4300, order_num = 2 where menu_id = 4302;
update sys_menu set parent_id = 4300, order_num = 3 where menu_id = 4303;
update sys_menu set parent_id = 4300, order_num = 4 where menu_id = 4304;
update sys_menu set parent_id = 4300, order_num = 6 where menu_id = 4305;
update sys_menu set parent_id = 4300, order_num = 10 where menu_id = 4306;
update sys_menu set parent_id = 4300, order_num = 8 where menu_id = 6072;
update sys_menu set parent_id = 4300, order_num = 9 where menu_id = 6073;
update sys_menu set parent_id = 4300, order_num = 11 where menu_id = 5801;
update sys_menu set parent_id = 4300, order_num = 12 where menu_id = 5802;
update sys_menu set parent_id = 4300, order_num = 13 where menu_id = 5803;
update sys_menu set parent_id = 4300, order_num = 14 where menu_id = 5804;
update sys_menu set parent_id = 4300, order_num = 15 where menu_id = 5805;
update sys_menu set parent_id = 4300, order_num = 16 where menu_id = 5971;
update sys_menu set parent_id = 4300, order_num = 17 where menu_id = 5972;
update sys_menu set parent_id = 4300, order_num = 18 where menu_id = 5973;
update sys_menu set parent_id = 4300, order_num = 19 where menu_id = 5974;
update sys_menu set parent_id = 4300, order_num = 20 where menu_id = 5975;
update sys_menu set parent_id = 4300, order_num = 21 where menu_id = 5976;
update sys_menu set parent_id = 4300, order_num = 22 where menu_id = 5977;

-- 7) 一级目录排序（粤工保 / 安责保）
update sys_menu set order_num = 10 where menu_id = 2000;
update sys_menu set order_num = 11 where menu_id = 2020;
update sys_menu set order_num = 12 where menu_id = 2040;
update sys_menu set order_num = 13 where menu_id = 2060;
update sys_menu set order_num = 14 where menu_id = 2080;
update sys_menu set order_num = 15 where menu_id = 3900;
update sys_menu set order_num = 16 where menu_id = 3920;
update sys_menu set order_num = 17 where menu_id = 3940;
update sys_menu set order_num = 18 where menu_id = 3960;
update sys_menu set order_num = 19 where menu_id = 5885;
update sys_menu set order_num = 20 where menu_id = 4000;
update sys_menu set order_num = 21 where menu_id = 4020;
update sys_menu set order_num = 22 where menu_id = 4040;
update sys_menu set order_num = 23 where menu_id = 6150;
update sys_menu set order_num = 24 where menu_id = 6190;
update sys_menu set order_num = 30 where menu_id = 4300;
update sys_menu set order_num = 31 where menu_id = 4400;
update sys_menu set order_num = 32 where menu_id = 6170;

update sys_menu set order_num = 10 where menu_id = 5600;
update sys_menu set order_num = 11 where menu_id = 5610;
update sys_menu set order_num = 12 where menu_id = 5620;
update sys_menu set order_num = 13 where menu_id = 5630;
update sys_menu set order_num = 14 where menu_id = 4920;
update sys_menu set order_num = 15 where menu_id = 5640;
update sys_menu set order_num = 16 where menu_id = 5650;
update sys_menu set order_num = 17 where menu_id = 5660;
update sys_menu set order_num = 18 where menu_id = 5670;

-- 8) 无效图标批量修正（映射到 yuegongbao-ui/src/assets/icons/svg 已有图标）
update sys_menu set icon = 'skill' where menu_id = 4300;
update sys_menu set icon = 'guide' where menu_id = 6190;
update sys_menu set icon = 'tool' where menu_id in (5742, 5642);
update sys_menu set icon = 'chart' where icon in ('data-analysis', 'data-board', 'data-line', 'histogram', 'trend-charts') and menu_type in ('M', 'C');
update sys_menu set icon = 'build' where icon = 'office-building' and menu_type in ('M', 'C');
update sys_menu set icon = 'checkbox' where icon = 'check' and menu_type in ('M', 'C');
update sys_menu set icon = 'link' where icon = 'connection' and menu_type in ('M', 'C');
update sys_menu set icon = 'money' where icon = 'wallet' and menu_type in ('M', 'C');
update sys_menu set icon = 'switch' where icon = 'refresh' and menu_type in ('M', 'C');
update sys_menu set icon = 'eye' where icon = 'picture' and menu_type in ('M', 'C');
update sys_menu set icon = 'post' where icon in ('tickets', 'briefcase') and menu_type in ('M', 'C');
update sys_menu set icon = 'star' where icon in ('promotion', 'present') and menu_type in ('M', 'C');
update sys_menu set icon = 'list' where icon = 'collection' and menu_type in ('M', 'C');
update sys_menu set icon = 'documentation' where icon = 'folder-opened' and menu_type in ('M', 'C');
update sys_menu set icon = 'education' where icon = 'reading' and menu_type in ('M', 'C');
update sys_menu set icon = 'bug' where icon in ('warning-filled', 'first-aid-kit') and menu_type in ('M', 'C');
update sys_menu set icon = 'bell' where icon = 'warning' and menu_type in ('M', 'C');
update sys_menu set icon = 'component' where icon = 'cpu' and menu_type in ('M', 'C');
update sys_menu set icon = 'monitor' where icon = 'video-camera' and menu_type in ('M', 'C');
update sys_menu set icon = 'zip' where icon = 'box' and menu_type in ('M', 'C');
update sys_menu set icon = 'shopping' where icon = 'suitcase' and menu_type in ('M', 'C');
update sys_menu set icon = 'job' where icon = 'briefcase' and menu_type in ('M', 'C');
update sys_menu set icon = 'message' where icon = 'chat-line-square' and menu_type in ('M', 'C');
update sys_menu set icon = 'tool' where icon = 'tools' and menu_type in ('M', 'C');

-- 9) 管理员角色补授权（新增菜单）
insert ignore into sys_role_menu (role_id, menu_id)
select r.role_id, m.menu_id
from sys_role r
join sys_menu m on m.menu_id in (4301, 4302, 5827, 5885)
where r.role_id in (1, 2, 101, 102, 103, 104, 105);
