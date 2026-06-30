-- YGB PC 菜单按文档 23 模块全面重构脚本
-- Generated automatically
-- Time: 2026-06-27 20:53:48

SET NAMES utf8mb4;

-- 1. Backup current sys_menu
CREATE TABLE IF NOT EXISTS sys_menu_backup_pre_restructure_v2 AS SELECT * FROM sys_menu;

-- 2. Insert 23 document module directories
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
  (7001, '驾驶舱', 0, 1, 'cockpit', NULL, '', '', 1, 0, 'M', '0', '0', 'ygb', '', 'dashboard', 'admin', NOW(), 'admin', NOW(), '文档模块：驾驶舱'),
  (7002, '预警中心', 0, 2, 'warning-center', NULL, '', '', 1, 0, 'M', '0', '0', 'ygb', '', 'bell', 'admin', NOW(), 'admin', NOW(), '文档模块：预警中心'),
  (7003, 'AI监测报告', 0, 3, 'ai-report', NULL, '', '', 1, 0, 'M', '0', '0', 'ygb', '', 'data-analysis', 'admin', NOW(), 'admin', NOW(), '文档模块：AI监测报告'),
  (7004, '合同备案', 0, 4, 'contract-filing', NULL, '', '', 1, 0, 'M', '0', '0', 'ygb', '', 'document', 'admin', NOW(), 'admin', NOW(), '文档模块：合同备案'),
  (7005, '用工考勤', 0, 5, 'attendance', NULL, '', '', 1, 0, 'M', '0', '0', 'ygb', '', 'calendar', 'admin', NOW(), 'admin', NOW(), '文档模块：用工考勤'),
  (7006, '工资监管', 0, 6, 'salary-supervision', NULL, '', '', 1, 0, 'M', '0', '0', 'ygb', '', 'money', 'admin', NOW(), 'admin', NOW(), '文档模块：工资监管'),
  (7007, '社保监管', 0, 7, 'social-insurance', NULL, '', '', 1, 0, 'M', '0', '0', 'ygb', '', 'first-aid-kit', 'admin', NOW(), 'admin', NOW(), '文档模块：社保监管'),
  (7008, '工伤监管', 0, 8, 'injury-supervision', NULL, '', '', 1, 0, 'M', '0', '0', 'ygb', '', 'warning', 'admin', NOW(), 'admin', NOW(), '文档模块：工伤监管'),
  (7009, '专项治理', 0, 9, 'special-rectification', NULL, '', '', 1, 0, 'M', '0', '0', 'ygb', '', 'files', 'admin', NOW(), 'admin', NOW(), '文档模块：专项治理'),
  (7010, '税务监管', 0, 10, 'tax-supervision', NULL, '', '', 1, 0, 'M', '0', '0', 'ygb', '', 'coin', 'admin', NOW(), 'admin', NOW(), '文档模块：税务监管'),
  (7011, '安责险管理', 0, 11, 'aq-insurance', NULL, '', '', 1, 0, 'M', '0', '0', 'ygb', '', 'umbrella', 'admin', NOW(), 'admin', NOW(), '文档模块：安责险管理'),
  (7012, '设备管理', 0, 12, 'device-management', NULL, '', '', 1, 0, 'M', '0', '0', 'ygb', '', 'cpu', 'admin', NOW(), 'admin', NOW(), '文档模块：设备管理'),
  (7013, '扩面减损', 0, 13, 'expansion-reduction', NULL, '', '', 1, 0, 'M', '0', '0', 'ygb', '', 'trend-charts', 'admin', NOW(), 'admin', NOW(), '文档模块：扩面减损'),
  (7014, '信用评价', 0, 14, 'credit-evaluation', NULL, '', '', 1, 0, 'M', '0', '0', 'ygb', '', 'medal', 'admin', NOW(), 'admin', NOW(), '文档模块：信用评价'),
  (7015, '统计报表', 0, 15, 'statistical-report', NULL, '', '', 1, 0, 'M', '0', '0', 'ygb', '', 'histogram', 'admin', NOW(), 'admin', NOW(), '文档模块：统计报表'),
  (7016, '人员管理', 0, 16, 'personnel-management', NULL, '', '', 1, 0, 'M', '0', '0', 'ygb', '', 'user', 'admin', NOW(), 'admin', NOW(), '文档模块：人员管理'),
  (7017, '单位管理', 0, 17, 'enterprise-management', NULL, '', '', 1, 0, 'M', '0', '0', 'ygb', '', 'office-building', 'admin', NOW(), 'admin', NOW(), '文档模块：单位管理'),
  (7018, '系统管理', 0, 18, 'system-management', NULL, '', '', 1, 0, 'M', '0', '0', 'ygb', '', 'setting', 'admin', NOW(), 'admin', NOW(), '文档模块：系统管理'),
  (7019, '运营后台', 0, 19, 'operation-backend', NULL, '', '', 1, 0, 'M', '0', '0', 'ygb', '', 'shopping-bag', 'admin', NOW(), 'admin', NOW(), '文档模块：运营后台'),
  (7020, '企业后台', 0, 20, 'enterprise-portal', NULL, '', '', 1, 0, 'M', '0', '0', 'ygb', '', 'briefcase', 'admin', NOW(), 'admin', NOW(), '文档模块：企业后台'),
  (7021, '便民服务', 0, 21, 'citizen-service', NULL, '', '', 1, 0, 'M', '0', '0', 'ygb', '', 'service', 'admin', NOW(), 'admin', NOW(), '文档模块：便民服务'),
  (7022, '新业态监管', 0, 22, 'newform-regulation', NULL, '', '', 1, 0, 'M', '0', '0', 'ygb', '', 'cloudy', 'admin', NOW(), 'admin', NOW(), '文档模块：新业态监管'),
  (7023, '职业病监管', 0, 23, 'occupational-disease', NULL, '', '', 1, 0, 'M', '0', '0', 'ygb', '', 'first-aid-kit', 'admin', NOW(), 'admin', NOW(), '文档模块：职业病监管')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  order_num = VALUES(order_num),
  path = VALUES(path),
  icon = VALUES(icon),
  visible = VALUES(visible),
  status = VALUES(status),
  remark = VALUES(remark);

-- 3. Move existing functional menus to new document module parents
UPDATE sys_menu SET parent_id = 7001, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 3901;
UPDATE sys_menu SET parent_id = 7001, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4008;
UPDATE sys_menu SET parent_id = 7001, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6071;
UPDATE sys_menu SET parent_id = 7002, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 2017;
UPDATE sys_menu SET parent_id = 7002, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 2018;
UPDATE sys_menu SET parent_id = 7002, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6061;
UPDATE sys_menu SET parent_id = 7003, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5741;
UPDATE sys_menu SET parent_id = 7003, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5742;
UPDATE sys_menu SET parent_id = 7003, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4014;
UPDATE sys_menu SET parent_id = 7003, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5827;
UPDATE sys_menu SET parent_id = 7004, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 2003;
UPDATE sys_menu SET parent_id = 7005, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 2004;
UPDATE sys_menu SET parent_id = 7005, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 2005;
UPDATE sys_menu SET parent_id = 7006, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 2006;
UPDATE sys_menu SET parent_id = 7006, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 2007;
UPDATE sys_menu SET parent_id = 7007, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 2008;
UPDATE sys_menu SET parent_id = 7007, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 2009;
UPDATE sys_menu SET parent_id = 7007, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5870;
UPDATE sys_menu SET parent_id = 7007, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5871;
UPDATE sys_menu SET parent_id = 7008, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 2015;
UPDATE sys_menu SET parent_id = 7008, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 2016;
UPDATE sys_menu SET parent_id = 7008, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5951;
UPDATE sys_menu SET parent_id = 7008, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5952;
UPDATE sys_menu SET parent_id = 7008, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5953;
UPDATE sys_menu SET parent_id = 7008, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5954;
UPDATE sys_menu SET parent_id = 7008, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5955;
UPDATE sys_menu SET parent_id = 7008, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5956;
UPDATE sys_menu SET parent_id = 7008, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6051;
UPDATE sys_menu SET parent_id = 7008, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6052;
UPDATE sys_menu SET parent_id = 7008, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6053;
UPDATE sys_menu SET parent_id = 7008, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6054;
UPDATE sys_menu SET parent_id = 7009, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 2012;
UPDATE sys_menu SET parent_id = 7009, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5875;
UPDATE sys_menu SET parent_id = 7009, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 2013;
UPDATE sys_menu SET parent_id = 7009, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5876;
UPDATE sys_menu SET parent_id = 7010, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 2010;
UPDATE sys_menu SET parent_id = 7010, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5872;
UPDATE sys_menu SET parent_id = 7010, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5873;
UPDATE sys_menu SET parent_id = 7011, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5761;
UPDATE sys_menu SET parent_id = 7011, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4460;
UPDATE sys_menu SET parent_id = 7011, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5762;
UPDATE sys_menu SET parent_id = 7012, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 2014;
UPDATE sys_menu SET parent_id = 7012, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4526;
UPDATE sys_menu SET parent_id = 7012, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4527;
UPDATE sys_menu SET parent_id = 7012, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4528;
UPDATE sys_menu SET parent_id = 7012, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4529;
UPDATE sys_menu SET parent_id = 7012, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4530;
UPDATE sys_menu SET parent_id = 7012, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4531;
UPDATE sys_menu SET parent_id = 7012, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4532;
UPDATE sys_menu SET parent_id = 7012, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5975;
UPDATE sys_menu SET parent_id = 7012, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5976;
UPDATE sys_menu SET parent_id = 7013, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 2011;
UPDATE sys_menu SET parent_id = 7013, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5874;
UPDATE sys_menu SET parent_id = 7014, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 3981;
UPDATE sys_menu SET parent_id = 7014, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5880;
UPDATE sys_menu SET parent_id = 7014, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5881;
UPDATE sys_menu SET parent_id = 7014, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5882;
UPDATE sys_menu SET parent_id = 7015, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 3921;
UPDATE sys_menu SET parent_id = 7015, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 3922;
UPDATE sys_menu SET parent_id = 7015, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 3923;
UPDATE sys_menu SET parent_id = 7015, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 3924;
UPDATE sys_menu SET parent_id = 7015, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5830;
UPDATE sys_menu SET parent_id = 7015, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5831;
UPDATE sys_menu SET parent_id = 7015, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5832;
UPDATE sys_menu SET parent_id = 7015, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5833;
UPDATE sys_menu SET parent_id = 7015, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5834;
UPDATE sys_menu SET parent_id = 7015, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5835;
UPDATE sys_menu SET parent_id = 7015, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5836;
UPDATE sys_menu SET parent_id = 7016, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 2002;
UPDATE sys_menu SET parent_id = 7016, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4490;
UPDATE sys_menu SET parent_id = 7016, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4491;
UPDATE sys_menu SET parent_id = 7016, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4492;
UPDATE sys_menu SET parent_id = 7016, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4493;
UPDATE sys_menu SET parent_id = 7016, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4494;
UPDATE sys_menu SET parent_id = 7016, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4495;
UPDATE sys_menu SET parent_id = 7017, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 2001;
UPDATE sys_menu SET parent_id = 7017, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4533;
UPDATE sys_menu SET parent_id = 7017, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4534;
UPDATE sys_menu SET parent_id = 7017, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4535;
UPDATE sys_menu SET parent_id = 7017, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4536;
UPDATE sys_menu SET parent_id = 7017, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6151;
UPDATE sys_menu SET parent_id = 7017, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6152;
UPDATE sys_menu SET parent_id = 7017, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6154;
UPDATE sys_menu SET parent_id = 7017, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6155;
UPDATE sys_menu SET parent_id = 7017, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6156;
UPDATE sys_menu SET parent_id = 7017, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6157;
UPDATE sys_menu SET parent_id = 7019, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4301;
UPDATE sys_menu SET parent_id = 7019, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4302;
UPDATE sys_menu SET parent_id = 7019, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4303;
UPDATE sys_menu SET parent_id = 7019, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4304;
UPDATE sys_menu SET parent_id = 7019, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4305;
UPDATE sys_menu SET parent_id = 7019, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4306;
UPDATE sys_menu SET parent_id = 7019, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4100;
UPDATE sys_menu SET parent_id = 7019, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4200;
UPDATE sys_menu SET parent_id = 7019, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5801;
UPDATE sys_menu SET parent_id = 7019, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5802;
UPDATE sys_menu SET parent_id = 7019, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5803;
UPDATE sys_menu SET parent_id = 7019, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5804;
UPDATE sys_menu SET parent_id = 7019, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5805;
UPDATE sys_menu SET parent_id = 7019, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6072;
UPDATE sys_menu SET parent_id = 7019, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6073;
UPDATE sys_menu SET parent_id = 7019, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5971;
UPDATE sys_menu SET parent_id = 7019, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5972;
UPDATE sys_menu SET parent_id = 7019, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5973;
UPDATE sys_menu SET parent_id = 7019, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5974;
UPDATE sys_menu SET parent_id = 7019, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 5977;
UPDATE sys_menu SET parent_id = 7020, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6171;
UPDATE sys_menu SET parent_id = 7020, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6172;
UPDATE sys_menu SET parent_id = 7020, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6173;
UPDATE sys_menu SET parent_id = 7020, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6174;
UPDATE sys_menu SET parent_id = 7020, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6175;
UPDATE sys_menu SET parent_id = 7020, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6176;
UPDATE sys_menu SET parent_id = 7020, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6177;
UPDATE sys_menu SET parent_id = 7020, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6178;
UPDATE sys_menu SET parent_id = 7020, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6179;
UPDATE sys_menu SET parent_id = 7020, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6180;
UPDATE sys_menu SET parent_id = 7021, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6191;
UPDATE sys_menu SET parent_id = 7021, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6192;
UPDATE sys_menu SET parent_id = 7021, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6193;
UPDATE sys_menu SET parent_id = 7021, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6194;
UPDATE sys_menu SET parent_id = 7021, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6195;
UPDATE sys_menu SET parent_id = 7021, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 6196;
UPDATE sys_menu SET parent_id = 7022, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4001;
UPDATE sys_menu SET parent_id = 7022, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4470;
UPDATE sys_menu SET parent_id = 7022, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4471;
UPDATE sys_menu SET parent_id = 7022, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4472;
UPDATE sys_menu SET parent_id = 7023, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4021;
UPDATE sys_menu SET parent_id = 7023, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4482;
UPDATE sys_menu SET parent_id = 7023, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = 4483;

-- 4. Hide old business-domain top-level directories
UPDATE sys_menu SET visible = '1', update_time = NOW() WHERE menu_id = 2000;
UPDATE sys_menu SET visible = '1', update_time = NOW() WHERE menu_id = 2020;
UPDATE sys_menu SET visible = '1', update_time = NOW() WHERE menu_id = 2040;
UPDATE sys_menu SET visible = '1', update_time = NOW() WHERE menu_id = 2060;
UPDATE sys_menu SET visible = '1', update_time = NOW() WHERE menu_id = 2080;
UPDATE sys_menu SET visible = '1', update_time = NOW() WHERE menu_id = 3900;
UPDATE sys_menu SET visible = '1', update_time = NOW() WHERE menu_id = 3920;
UPDATE sys_menu SET visible = '1', update_time = NOW() WHERE menu_id = 3940;
UPDATE sys_menu SET visible = '1', update_time = NOW() WHERE menu_id = 3960;
UPDATE sys_menu SET visible = '1', update_time = NOW() WHERE menu_id = 4000;
UPDATE sys_menu SET visible = '1', update_time = NOW() WHERE menu_id = 4020;
UPDATE sys_menu SET visible = '1', update_time = NOW() WHERE menu_id = 4040;
UPDATE sys_menu SET visible = '1', update_time = NOW() WHERE menu_id = 4300;
UPDATE sys_menu SET visible = '1', update_time = NOW() WHERE menu_id = 4400;
UPDATE sys_menu SET visible = '1', update_time = NOW() WHERE menu_id = 5885;
UPDATE sys_menu SET visible = '1', update_time = NOW() WHERE menu_id = 6150;
UPDATE sys_menu SET visible = '1', update_time = NOW() WHERE menu_id = 6170;
UPDATE sys_menu SET visible = '1', update_time = NOW() WHERE menu_id = 6190;
UPDATE sys_menu SET visible = '1', update_time = NOW() WHERE menu_id = 3980;

-- 5. Add missing secondary module placeholder menus
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8000, '地图可视化', 7001, 1, 'mapVisualization', 'ygb/cockpit/map/index', '', 'YgbMapVisualization', 1, 0, 'C', '0', '0', 'ygb', 'ygb:cockpit:list', 'map-location', 'admin', NOW(), 'admin', NOW(), '文档二级模块：地图可视化')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8001, '实时预警流', 7001, 2, 'warningStream', 'ygb/cockpit/warningStream/index', '', 'YgbWarningStream', 1, 0, 'C', '0', '0', 'ygb', 'ygb:cockpit:list', 'bell', 'admin', NOW(), 'admin', NOW(), '文档二级模块：实时预警流')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8002, '红黄绿码企业分类', 7001, 3, 'enterpriseCode', 'ygb/cockpit/enterpriseCode/index', '', 'YgbEnterpriseCode', 1, 0, 'C', '0', '0', 'ygb', 'ygb:cockpit:list', 'medal', 'admin', NOW(), 'admin', NOW(), '文档二级模块：红黄绿码企业分类')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8003, '风险评分与排名', 7003, 1, 'riskScoreRanking', 'ygb/aiReport/ranking/index', '', 'YgbRiskScoreRanking', 1, 0, 'C', '0', '0', 'ygb', 'ygb:aiReport:list', 'trophy', 'admin', NOW(), 'admin', NOW(), '文档二级模块：风险评分与排名')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8004, '多维筛选与穿透', 7003, 2, 'multiFilter', 'ygb/aiReport/filter/index', '', 'YgbMultiFilter', 1, 0, 'C', '0', '0', 'ygb', 'ygb:aiReport:list', 'filter', 'admin', NOW(), 'admin', NOW(), '文档二级模块：多维筛选与穿透')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8005, '合同到期提醒', 7004, 1, 'contractExpiry', 'ygb/contract/expiry/index', '', 'YgbContractExpiry', 1, 0, 'C', '0', '0', 'ygb', 'ygb:contract:list', 'timer', 'admin', NOW(), 'admin', NOW(), '文档二级模块：合同到期提醒')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8006, '未备案合同预警', 7004, 2, 'unfiledWarning', 'ygb/contract/unfiled/index', '', 'YgbUnfiledWarning', 1, 0, 'C', '0', '0', 'ygb', 'ygb:contract:list', 'warning', 'admin', NOW(), 'admin', NOW(), '文档二级模块：未备案合同预警')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8007, '考勤总览', 7005, 1, 'attendanceOverview', 'ygb/attendance/overview/index', '', 'YgbAttendanceOverview', 1, 0, 'C', '0', '0', 'ygb', 'ygb:attendanceRaw:list', 'data-board', 'admin', NOW(), 'admin', NOW(), '文档二级模块：考勤总览')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8008, '异常考勤统计', 7005, 2, 'abnormalStatistics', 'ygb/attendance/abnormal/index', '', 'YgbAbnormalStatistics', 1, 0, 'C', '0', '0', 'ygb', 'ygb:attendanceRaw:list', 'warning', 'admin', NOW(), 'admin', NOW(), '文档二级模块：异常考勤统计')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8009, '设备在线率报表', 7005, 3, 'deviceOnlineRate', 'ygb/attendance/deviceOnline/index', '', 'YgbDeviceOnlineRate', 1, 0, 'C', '0', '0', 'ygb', 'ygb:attendanceRaw:list', 'monitor', 'admin', NOW(), 'admin', NOW(), '文档二级模块：设备在线率报表')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8010, '工资发放监控', 7006, 1, 'paymentMonitor', 'ygb/salary/paymentMonitor/index', '', 'YgbPaymentMonitor', 1, 0, 'C', '0', '0', 'ygb', 'ygb:salaryBatch:list', 'money', 'admin', NOW(), 'admin', NOW(), '文档二级模块：工资发放监控')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8011, '监管账户监控', 7006, 2, 'accountMonitor', 'ygb/salary/accountMonitor/index', '', 'YgbAccountMonitor', 1, 0, 'C', '0', '0', 'ygb', 'ygb:salaryBatch:list', 'bank-card', 'admin', NOW(), 'admin', NOW(), '文档二级模块：监管账户监控')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8012, '拖欠工资预警', 7006, 3, 'overdueWarning', 'ygb/salary/overdueWarning/index', '', 'YgbOverdueWarning', 1, 0, 'C', '0', '0', 'ygb', 'ygb:salaryArrears:list', 'warning', 'admin', NOW(), 'admin', NOW(), '文档二级模块：拖欠工资预警')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8013, '银行代发结果监控', 7006, 4, 'bankDistribution', 'ygb/salary/bankDistribution/index', '', 'YgbBankDistribution', 1, 0, 'C', '0', '0', 'ygb', 'ygb:salaryBatch:list', 'credit-card', 'admin', NOW(), 'admin', NOW(), '文档二级模块：银行代发结果监控')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8014, '工伤认定辅助', 7008, 1, 'recognitionAssist', 'ygb/injury/recognitionAssist/index', '', 'YgbRecognitionAssist', 1, 0, 'C', '0', '0', 'ygb', 'ygb:injuryEvent:list', 'timer', 'admin', NOW(), 'admin', NOW(), '文档二级模块：工伤认定辅助')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8015, '工伤统计分析', 7008, 2, 'statisticalAnalysis', 'ygb/injury/statisticalAnalysis/index', '', 'YgbStatisticalAnalysis', 1, 0, 'C', '0', '0', 'ygb', 'ygb:injuryEvent:list', 'histogram', 'admin', NOW(), 'admin', NOW(), '文档二级模块：工伤统计分析')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8016, '事故预防服务', 7011, 1, 'preventionService', 'ygb/aqInsurance/preventionService/index', '', 'YgbPreventionService', 1, 0, 'C', '0', '0', 'ygb', 'ygb:aqInsurance:list', 'first-aid-kit', 'admin', NOW(), 'admin', NOW(), '文档二级模块：事故预防服务')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8017, '阳光劳务屏管理', 7012, 1, 'sunlightScreen', 'ygb/device/sunlightScreen/index', '', 'YgbSunlightScreen', 1, 0, 'C', '0', '0', 'ygb', 'ygb:device:list', 'monitor', 'admin', NOW(), 'admin', NOW(), '文档二级模块：阳光劳务屏管理')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8018, '设备台账', 7012, 2, 'deviceLedger', 'ygb/device/ledger/index', '', 'YgbDeviceLedger', 1, 0, 'C', '0', '0', 'ygb', 'ygb:device:list', 'notebook', 'admin', NOW(), 'admin', NOW(), '文档二级模块：设备台账')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8019, '批量操作', 7012, 3, 'batchOperation', 'ygb/device/batchOperation/index', '', 'YgbBatchOperation', 1, 0, 'C', '0', '0', 'ygb', 'ygb:device:list', 'operation', 'admin', NOW(), 'admin', NOW(), '文档二级模块：批量操作')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8020, '催缴跟踪', 7013, 1, 'collectionTracking', 'ygb/expansion/collectionTracking/index', '', 'YgbCollectionTracking', 1, 0, 'C', '0', '0', 'ygb', 'ygb:uninsuredList:list', 'phone', 'admin', NOW(), 'admin', NOW(), '文档二级模块：催缴跟踪')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8021, '工伤预防培训管理', 7013, 2, 'preventionTraining', 'ygb/expansion/preventionTraining/index', '', 'YgbPreventionTraining', 1, 0, 'C', '0', '0', 'ygb', 'ygb:uninsuredList:list', 'reading', 'admin', NOW(), 'admin', NOW(), '文档二级模块：工伤预防培训管理')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8022, '培训课程与学时管理', 7013, 3, 'courseHours', 'ygb/expansion/courseHours/index', '', 'YgbCourseHours', 1, 0, 'C', '0', '0', 'ygb', 'ygb:uninsuredList:list', 'timer', 'admin', NOW(), 'admin', NOW(), '文档二级模块：培训课程与学时管理')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8023, '培训效果评估', 7013, 4, 'trainingEvaluation', 'ygb/expansion/trainingEvaluation/index', '', 'YgbTrainingEvaluation', 1, 0, 'C', '0', '0', 'ygb', 'ygb:uninsuredList:list', 'trend-charts', 'admin', NOW(), 'admin', NOW(), '文档二级模块：培训效果评估')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8024, '信用总览', 7014, 1, 'creditOverview', 'ygb/credit/overview/index', '', 'YgbCreditOverview', 1, 0, 'C', '0', '0', 'ygb', 'ygb:creditScore:list', 'data-board', 'admin', NOW(), 'admin', NOW(), '文档二级模块：信用总览')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8025, '企业信用档案', 7014, 2, 'enterpriseArchive', 'ygb/credit/enterpriseArchive/index', '', 'YgbEnterpriseArchive', 1, 0, 'C', '0', '0', 'ygb', 'ygb:creditScore:list', 'folder', 'admin', NOW(), 'admin', NOW(), '文档二级模块：企业信用档案')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8026, '信用报告导出', 7014, 3, 'reportExport', 'ygb/credit/reportExport/index', '', 'YgbReportExport', 1, 0, 'C', '0', '0', 'ygb', 'ygb:creditScore:list', 'download', 'admin', NOW(), 'admin', NOW(), '文档二级模块：信用报告导出')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8027, '用工报表', 7015, 1, 'employmentReport', 'ygb/statReport/employment/index', '', 'YgbEmploymentReport', 1, 0, 'C', '0', '0', 'ygb', 'ygb:statReport:employment:query', 'user-filled', 'admin', NOW(), 'admin', NOW(), '文档二级模块：用工报表')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8028, '考勤报表', 7015, 2, 'attendanceReport', 'ygb/statReport/attendance/index', '', 'YgbAttendanceReport', 1, 0, 'C', '0', '0', 'ygb', 'ygb:statReport:attendance:query', 'calendar', 'admin', NOW(), 'admin', NOW(), '文档二级模块：考勤报表')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8029, '专项整治报表', 7015, 3, 'rectificationReport', 'ygb/statReport/rectification/index', '', 'YgbRectificationReport', 1, 0, 'C', '0', '0', 'ygb', 'ygb:statReport:rectification:query', 'files', 'admin', NOW(), 'admin', NOW(), '文档二级模块：专项整治报表')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8030, '新业态人员库', 7016, 1, 'newformPersonnel', 'ygb/person/newform/index', '', 'YgbNewformPersonnel', 1, 0, 'C', '0', '0', 'ygb', 'ygb:person:list', 'cloudy', 'admin', NOW(), 'admin', NOW(), '文档二级模块：新业态人员库')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8031, '派遣/用工关联关系', 7017, 1, 'dispatchRelation', 'ygb/enterprise/relation/index', '', 'YgbDispatchRelation', 1, 0, 'C', '0', '0', 'ygb', 'ygb:enterprise:list', 'connection', 'admin', NOW(), 'admin', NOW(), '文档二级模块：派遣/用工关联关系')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8032, '新业态平台企业管理', 7017, 2, 'newformEnterprise', 'ygb/enterprise/newform/index', '', 'YgbNewformEnterprise', 1, 0, 'C', '0', '0', 'ygb', 'ygb:enterprise:list', 'cloudy', 'admin', NOW(), 'admin', NOW(), '文档二级模块：新业态平台企业管理')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8033, '企业自主服务', 7017, 3, 'enterpriseSelfService', 'ygb/enterprise/selfService/index', '', 'YgbEnterpriseSelfService', 1, 0, 'C', '0', '0', 'ygb', 'ygb:enterprise:list', 'service', 'admin', NOW(), 'admin', NOW(), '文档二级模块：企业自主服务')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8034, '接口与数据交换监管', 7018, 1, 'interfaceMonitor', 'ygb/platform/exchange/index', '', 'YgbInterfaceMonitor', 1, 0, 'C', '0', '0', 'ygb', 'ygb:platformExchange:list', 'connection', 'admin', NOW(), 'admin', NOW(), '文档二级模块：接口与数据交换监管')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8035, '运行监控', 7018, 2, 'runtimeMonitor', 'ygb/platform/overview/index', '', 'YgbRuntimeMonitor', 1, 0, 'C', '0', '0', 'ygb', 'ygb:platformRuntime:query', 'cpu', 'admin', NOW(), 'admin', NOW(), '文档二级模块：运行监控')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8036, '运营数据总览', 7019, 1, 'operationDataOverview', 'ygb/operation/dataOverview/index', '', 'YgbOperationDataOverview', 1, 0, 'C', '0', '0', 'ygb', 'ygb:operation:overview', 'data-board', 'admin', NOW(), 'admin', NOW(), '文档二级模块：运营数据总览')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (8037, '数据统计与分析', 7019, 2, 'dataAnalysis', 'ygb/operation/dataAnalysis/index', '', 'YgbDataAnalysis', 1, 0, 'C', '0', '0', 'ygb', 'ygb:operationRecruitStats:list', 'data-analysis', 'admin', NOW(), 'admin', NOW(), '文档二级模块：数据统计与分析')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark);
