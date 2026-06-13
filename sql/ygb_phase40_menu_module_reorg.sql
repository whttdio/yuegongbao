set names utf8mb4;

-- 1) YGB / AZB 一级目录按门户收口，避免继续依赖 both + 前端运行时改写。
update sys_menu
set portal_scope = 'ygb'
where menu_id in (
  2000, 2001, 2002,
  2040, 2011,
  2060, 2015, 2016,
  2080, 2017, 2018,
  3900, 3901,
  3940, 4014,
  3960, 4460,
  3980, 3981,
  4040, 4041
);

update sys_menu
set menu_name = '工伤与预防'
where menu_id = 2060;

update sys_menu
set parent_id = 4040,
    order_num = 2,
    portal_scope = 'ygb'
where menu_id = 2014;

update sys_menu
set parent_id = 2060,
    order_num = 1,
    portal_scope = 'ygb'
where menu_id = 2015;

update sys_menu
set parent_id = 2060,
    order_num = 2,
    portal_scope = 'ygb'
where menu_id = 2016;

update sys_menu
set parent_id = 4040,
    order_num = 1,
    portal_scope = 'ygb'
where menu_id = 4041;

update sys_menu
set menu_name = '统计报表',
    order_num = 9,
    portal_scope = 'azb'
where menu_id = 4920;

update sys_menu
set parent_id = 4920,
    portal_scope = 'azb'
where menu_id in (4921, 4922, 4923, 4924);

-- 2) 修复 YGB 被旧 SQL 冲掉的二级菜单。
insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5741, 'AI报告中心', 3940, 1, 'aiReport', 'ygb/aiReport/index', '', 'YgbAiReport',
  1, 0, 'C', '0', '0', 'ygb:aiReport:list', 'data-analysis',
  'admin', sysdate(), 'admin', sysdate(), 'YGB AI报告中心菜单', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5741);

update sys_menu
set menu_name = 'AI报告中心',
    parent_id = 3940,
    order_num = 1,
    path = 'aiReport',
    component = 'ygb/aiReport/index',
    route_name = 'YgbAiReport',
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'ygb:aiReport:list',
    icon = 'data-analysis',
    portal_scope = 'ygb'
where menu_id = 5741;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5742, '评分模型配置', 3940, 2, 'aiReportConfig', 'ygb/aiReportConfig/index', '', 'YgbAiReportConfig',
  1, 0, 'C', '0', '0', 'ygb:aiReportConfig:list', 'set-up',
  'admin', sysdate(), 'admin', sysdate(), 'YGB AI模型配置菜单', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5742);

update sys_menu
set menu_name = '评分模型配置',
    parent_id = 3940,
    order_num = 2,
    path = 'aiReportConfig',
    component = 'ygb/aiReportConfig/index',
    route_name = 'YgbAiReportConfig',
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'ygb:aiReportConfig:list',
    icon = 'set-up',
    portal_scope = 'ygb'
where menu_id = 5742;

update sys_menu
set parent_id = 3940,
    order_num = 3,
    portal_scope = 'ygb'
where menu_id = 4014;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5761, '投保监管', 3960, 1, 'aqInsurance', 'ygb/aqInsurance/index', '', 'YgbAqInsurance',
  1, 0, 'C', '0', '0', 'ygb:aqInsurance:list', 'wallet',
  'admin', sysdate(), 'admin', sysdate(), 'YGB 安责险投保监管菜单', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5761);

update sys_menu
set menu_name = '投保监管',
    parent_id = 3960,
    order_num = 1,
    path = 'aqInsurance',
    component = 'ygb/aqInsurance/index',
    route_name = 'YgbAqInsurance',
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'ygb:aqInsurance:list',
    icon = 'wallet',
    portal_scope = 'ygb'
where menu_id = 5761;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5762, '事故预防资金池', 3960, 2, 'preventionFund', 'ygb/preventionFund/index', '', 'YgbPreventionFund',
  1, 0, 'C', '0', '0', 'ygb:preventionFund:list', 'money',
  'admin', sysdate(), 'admin', sysdate(), 'YGB 事故预防资金池菜单', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5762);

update sys_menu
set menu_name = '事故预防资金池',
    parent_id = 3960,
    order_num = 2,
    path = 'preventionFund',
    component = 'ygb/preventionFund/index',
    route_name = 'YgbPreventionFund',
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'ygb:preventionFund:list',
    icon = 'money',
    portal_scope = 'ygb'
where menu_id = 5762;

update sys_menu
set parent_id = 3960,
    order_num = 3,
    portal_scope = 'ygb'
where menu_id = 4460;

-- 3) 补齐 AZB 一级目录。
insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5600, '治理对象台账', 0, 5, 'azb-foundation', null, '', '',
  1, 0, 'M', '0', '0', '', 'office-building',
  'admin', sysdate(), 'admin', sysdate(), 'AZB 治理对象一级目录', 'azb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5600);

update sys_menu
set menu_name = '治理对象台账',
    parent_id = 0,
    order_num = 5,
    path = 'azb-foundation',
    component = null,
    route_name = '',
    menu_type = 'M',
    visible = '0',
    status = '0',
    perms = '',
    icon = 'office-building',
    portal_scope = 'azb'
where menu_id = 5600;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5610, '风险处置中心', 0, 6, 'azb-warning', null, '', '',
  1, 0, 'M', '0', '0', '', 'warning',
  'admin', sysdate(), 'admin', sysdate(), 'AZB 风险处置一级目录', 'azb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5610);

update sys_menu
set menu_name = '风险处置中心',
    parent_id = 0,
    order_num = 6,
    path = 'azb-warning',
    component = null,
    route_name = '',
    menu_type = 'M',
    visible = '0',
    status = '0',
    perms = '',
    icon = 'warning',
    portal_scope = 'azb'
where menu_id = 5610;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5620, '设备安全治理', 0, 7, 'azb-device', null, '', '',
  1, 0, 'M', '0', '0', '', 'cpu',
  'admin', sysdate(), 'admin', sysdate(), 'AZB 设备治理一级目录', 'azb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5620);

update sys_menu
set menu_name = '设备安全治理',
    parent_id = 0,
    order_num = 7,
    path = 'azb-device',
    component = null,
    route_name = '',
    menu_type = 'M',
    visible = '0',
    status = '0',
    perms = '',
    icon = 'cpu',
    portal_scope = 'azb'
where menu_id = 5620;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5630, '应急驾驶舱', 0, 8, 'azb-cockpit', null, '', '',
  1, 0, 'M', '0', '0', '', 'dashboard',
  'admin', sysdate(), 'admin', sysdate(), 'AZB 驾驶舱一级目录', 'azb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5630);

update sys_menu
set menu_name = '应急驾驶舱',
    parent_id = 0,
    order_num = 8,
    path = 'azb-cockpit',
    component = null,
    route_name = '',
    menu_type = 'M',
    visible = '0',
    status = '0',
    perms = '',
    icon = 'dashboard',
    portal_scope = 'azb'
where menu_id = 5630;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5640, 'AI监测研判', 0, 10, 'azb-ai-report', null, '', '',
  1, 0, 'M', '0', '0', '', 'data-analysis',
  'admin', sysdate(), 'admin', sysdate(), 'AZB AI监测一级目录', 'azb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5640);

update sys_menu
set menu_name = 'AI监测研判',
    parent_id = 0,
    order_num = 10,
    path = 'azb-ai-report',
    component = null,
    route_name = '',
    menu_type = 'M',
    visible = '0',
    status = '0',
    perms = '',
    icon = 'data-analysis',
    portal_scope = 'azb'
where menu_id = 5640;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5650, '安责险协同', 0, 11, 'azb-aqins', null, '', '',
  1, 0, 'M', '0', '0', '', 'money',
  'admin', sysdate(), 'admin', sysdate(), 'AZB 安责险协同一级目录', 'azb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5650);

update sys_menu
set menu_name = '安责险协同',
    parent_id = 0,
    order_num = 11,
    path = 'azb-aqins',
    component = null,
    route_name = '',
    menu_type = 'M',
    visible = '0',
    status = '0',
    perms = '',
    icon = 'money',
    portal_scope = 'azb'
where menu_id = 5650;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5660, '风险信用', 0, 12, 'azb-credit', null, '', '',
  1, 0, 'M', '0', '0', '', 'histogram',
  'admin', sysdate(), 'admin', sysdate(), 'AZB 风险信用一级目录', 'azb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5660);

update sys_menu
set menu_name = '风险信用',
    parent_id = 0,
    order_num = 12,
    path = 'azb-credit',
    component = null,
    route_name = '',
    menu_type = 'M',
    visible = '0',
    status = '0',
    perms = '',
    icon = 'histogram',
    portal_scope = 'azb'
where menu_id = 5660;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5670, '技术防范', 0, 13, 'azb-techdefense', null, '', '',
  1, 0, 'M', '0', '0', '', 'guide',
  'admin', sysdate(), 'admin', sysdate(), 'AZB 技术防范一级目录', 'azb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5670);

update sys_menu
set menu_name = '技术防范',
    parent_id = 0,
    order_num = 13,
    path = 'azb-techdefense',
    component = null,
    route_name = '',
    menu_type = 'M',
    visible = '0',
    status = '0',
    perms = '',
    icon = 'guide',
    portal_scope = 'azb'
where menu_id = 5670;

-- 4) 补齐 AZB 二级菜单，并挂到对应门户目录。
insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5601, '单位管理', 5600, 1, 'enterprise', 'azb/enterprise/index', '', 'AzbEnterprise',
  1, 0, 'C', '0', '0', 'ygb:enterprise:list', 'office-building',
  'admin', sysdate(), 'admin', sysdate(), 'AZB 单位管理二级菜单', 'azb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5601);

update sys_menu
set menu_name = '单位管理',
    parent_id = 5600,
    order_num = 1,
    path = 'enterprise',
    component = 'azb/enterprise/index',
    route_name = 'AzbEnterprise',
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'ygb:enterprise:list',
    icon = 'office-building',
    portal_scope = 'azb'
where menu_id = 5601;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5602, '人员管理', 5600, 2, 'person', 'azb/person/index', '', 'AzbPerson',
  1, 0, 'C', '0', '0', 'ygb:person:list', 'user',
  'admin', sysdate(), 'admin', sysdate(), 'AZB 人员管理二级菜单', 'azb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5602);

update sys_menu
set menu_name = '人员管理',
    parent_id = 5600,
    order_num = 2,
    path = 'person',
    component = 'azb/person/index',
    route_name = 'AzbPerson',
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'ygb:person:list',
    icon = 'user',
    portal_scope = 'azb'
where menu_id = 5602;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5611, '预警中心', 5610, 1, 'warning', 'azb/warning/index', '', 'AzbWarning',
  1, 0, 'C', '0', '0', 'ygb:warning:list', 'warning',
  'admin', sysdate(), 'admin', sysdate(), 'AZB 预警中心二级菜单', 'azb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5611);

update sys_menu
set menu_name = '预警中心',
    parent_id = 5610,
    order_num = 1,
    path = 'warning',
    component = 'azb/warning/index',
    route_name = 'AzbWarning',
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'ygb:warning:list',
    icon = 'warning',
    portal_scope = 'azb'
where menu_id = 5611;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5612, '预警规则', 5610, 2, 'warningRule', 'azb/warningRule/index', '', 'AzbWarningRule',
  1, 0, 'C', '0', '0', 'ygb:warningRule:list', 'setting',
  'admin', sysdate(), 'admin', sysdate(), 'AZB 预警规则二级菜单', 'azb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5612);

update sys_menu
set menu_name = '预警规则',
    parent_id = 5610,
    order_num = 2,
    path = 'warningRule',
    component = 'azb/warningRule/index',
    route_name = 'AzbWarningRule',
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'ygb:warningRule:list',
    icon = 'setting',
    portal_scope = 'azb'
where menu_id = 5612;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5613, '漏保清单', 5610, 3, 'uninsuredList', 'azb/uninsuredList/index', '', 'AzbUninsuredList',
  1, 0, 'C', '0', '0', 'ygb:uninsuredList:list', 'document',
  'admin', sysdate(), 'admin', sysdate(), 'AZB 漏保清单二级菜单', 'azb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5613);

update sys_menu
set menu_name = '漏保清单',
    parent_id = 5610,
    order_num = 3,
    path = 'uninsuredList',
    component = 'azb/uninsuredList/index',
    route_name = 'AzbUninsuredList',
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'ygb:uninsuredList:list',
    icon = 'document',
    portal_scope = 'azb'
where menu_id = 5613;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5614, '工伤事件', 5610, 4, 'injuryEvent', 'azb/injuryEvent/index', '', 'AzbInjuryEvent',
  1, 0, 'C', '0', '0', 'ygb:injuryEvent:list', 'first-aid-kit',
  'admin', sysdate(), 'admin', sysdate(), 'AZB 工伤事件二级菜单', 'azb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5614);

update sys_menu
set menu_name = '工伤事件',
    parent_id = 5610,
    order_num = 4,
    path = 'injuryEvent',
    component = 'azb/injuryEvent/index',
    route_name = 'AzbInjuryEvent',
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'ygb:injuryEvent:list',
    icon = 'first-aid-kit',
    portal_scope = 'azb'
where menu_id = 5614;

update sys_menu
set parent_id = 5620,
    order_num = 1,
    portal_scope = 'azb'
where menu_id = 5520;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5631, '驾驶舱总览', 5630, 1, 'cockpit', 'azb/cockpit/index', '', 'AzbCockpit',
  1, 0, 'C', '0', '0', 'ygb:cockpit:list', 'dashboard',
  'admin', sysdate(), 'admin', sysdate(), 'AZB 驾驶舱二级菜单', 'azb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5631);

update sys_menu
set menu_name = '驾驶舱总览',
    parent_id = 5630,
    order_num = 1,
    path = 'cockpit',
    component = 'azb/cockpit/index',
    route_name = 'AzbCockpit',
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'ygb:cockpit:list',
    icon = 'dashboard',
    portal_scope = 'azb'
where menu_id = 5631;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5641, 'AI报告中心', 5640, 1, 'aiReport', 'azb/aiReport/index', '', 'AzbAiReport',
  1, 0, 'C', '0', '0', 'ygb:aiReport:list', 'data-analysis',
  'admin', sysdate(), 'admin', sysdate(), 'AZB AI报告中心二级菜单', 'azb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5641);

update sys_menu
set menu_name = 'AI报告中心',
    parent_id = 5640,
    order_num = 1,
    path = 'aiReport',
    component = 'azb/aiReport/index',
    route_name = 'AzbAiReport',
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'ygb:aiReport:list',
    icon = 'data-analysis',
    portal_scope = 'azb'
where menu_id = 5641;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5642, '模型配置', 5640, 2, 'aiReportConfig', 'azb/aiReportConfig/index', '', 'AzbAiReportConfig',
  1, 0, 'C', '0', '0', 'ygb:aiReportConfig:list', 'set-up',
  'admin', sysdate(), 'admin', sysdate(), 'AZB AI模型配置二级菜单', 'azb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5642);

update sys_menu
set menu_name = '模型配置',
    parent_id = 5640,
    order_num = 2,
    path = 'aiReportConfig',
    component = 'azb/aiReportConfig/index',
    route_name = 'AzbAiReportConfig',
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'ygb:aiReportConfig:list',
    icon = 'set-up',
    portal_scope = 'azb'
where menu_id = 5642;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5651, '投保监管', 5650, 1, 'aqInsurance', 'azb/aqInsurance/index', '', 'AzbAqInsurance',
  1, 0, 'C', '0', '0', 'ygb:aqInsurance:list', 'wallet',
  'admin', sysdate(), 'admin', sysdate(), 'AZB 投保监管二级菜单', 'azb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5651);

update sys_menu
set menu_name = '投保监管',
    parent_id = 5650,
    order_num = 1,
    path = 'aqInsurance',
    component = 'azb/aqInsurance/index',
    route_name = 'AzbAqInsurance',
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'ygb:aqInsurance:list',
    icon = 'wallet',
    portal_scope = 'azb'
where menu_id = 5651;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5652, '事故预防资金池', 5650, 2, 'preventionFund', 'azb/preventionFund/index', '', 'AzbPreventionFund',
  1, 0, 'C', '0', '0', 'ygb:preventionFund:list', 'money',
  'admin', sysdate(), 'admin', sysdate(), 'AZB 事故预防资金池二级菜单', 'azb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5652);

update sys_menu
set menu_name = '事故预防资金池',
    parent_id = 5650,
    order_num = 2,
    path = 'preventionFund',
    component = 'azb/preventionFund/index',
    route_name = 'AzbPreventionFund',
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'ygb:preventionFund:list',
    icon = 'money',
    portal_scope = 'azb'
where menu_id = 5652;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5653, '预防项目', 5650, 3, 'preventionProject', 'azb/preventionProject/index', '', 'AzbPreventionProject',
  1, 0, 'C', '0', '0', 'ygb:preventionProject:list', 'collection',
  'admin', sysdate(), 'admin', sysdate(), 'AZB 预防项目二级菜单', 'azb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5653);

update sys_menu
set menu_name = '预防项目',
    parent_id = 5650,
    order_num = 3,
    path = 'preventionProject',
    component = 'azb/preventionProject/index',
    route_name = 'AzbPreventionProject',
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'ygb:preventionProject:list',
    icon = 'collection',
    portal_scope = 'azb'
where menu_id = 5653;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5661, '企业信用评分', 5660, 1, 'creditScore', 'azb/creditScore/index', '', 'AzbCreditScore',
  1, 0, 'C', '0', '0', 'ygb:creditScore:list', 'histogram',
  'admin', sysdate(), 'admin', sysdate(), 'AZB 信用评分二级菜单', 'azb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5661);

update sys_menu
set menu_name = '企业信用评分',
    parent_id = 5660,
    order_num = 1,
    path = 'creditScore',
    component = 'azb/creditScore/index',
    route_name = 'AzbCreditScore',
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'ygb:creditScore:list',
    icon = 'histogram',
    portal_scope = 'azb'
where menu_id = 5661;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5671, '高处作业申报报备', 5670, 1, 'heightWorkReport', 'azb/heightWorkReport/index', '', 'AzbHeightWorkReport',
  1, 0, 'C', '0', '0', 'ygb:heightWorkReport:query', 'guide',
  'admin', sysdate(), 'admin', sysdate(), 'AZB 高处作业二级菜单', 'azb'
from dual
where not exists (select 1 from sys_menu where menu_id = 5671);

update sys_menu
set menu_name = '高处作业申报报备',
    parent_id = 5670,
    order_num = 1,
    path = 'heightWorkReport',
    component = 'azb/heightWorkReport/index',
    route_name = 'AzbHeightWorkReport',
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'ygb:heightWorkReport:query',
    icon = 'guide',
    portal_scope = 'azb'
where menu_id = 5671;

-- 5) 角色授权：YGB 新增二级菜单。
insert into sys_role_menu (role_id, menu_id, portal_scope)
select r.role_id, m.menu_id, 'ygb'
from sys_role r
join sys_menu m on m.menu_id in (5741, 5742, 5761, 5762)
where r.role_key in ('admin', 'ygb_hrss_supervisor', 'ygb_enterprise_admin', 'ygb_enterprise_operator')
on duplicate key update portal_scope = values(portal_scope);

-- 6) 角色授权：AZB 一级/二级菜单。
insert into sys_role_menu (role_id, menu_id, portal_scope)
select r.role_id, m.menu_id, 'azb'
from sys_role r
join sys_menu m on m.menu_id in (
  5600, 5601, 5602,
  5610, 5611, 5612, 5613, 5614,
  5620, 5520, 5521, 5522, 5523, 5524, 5525, 5526, 5527,
  5630, 5631,
  4920, 4921, 4922, 4923, 4924,
  5640, 5641, 5642,
  5650, 5651, 5652, 5653,
  5660, 5661,
  5670, 5671
)
where r.role_key in ('admin', 'ygb_emergency_supervisor', 'ygb_insurer', 'ygb_bank', 'ygb_enterprise_admin', 'ygb_enterprise_operator')
on duplicate key update portal_scope = values(portal_scope);
