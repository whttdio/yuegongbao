SET NAMES utf8mb4;

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, remark, portal_scope
)
SELECT
  4300, '劳动者活动管理', 0, 14, 'workerActivity', 'ygb/workerActivity/index', 'YgbWorkerActivity',
  1, 0, 'C', '0', '0', 'ygb:workerActivity:list', 'present',
  'admin', NOW(), '劳动者福利活动参与与发放处理', 'ygb'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 4300);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 4300 FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 4300);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 2, 4300 FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 2 AND menu_id = 4300);

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, remark, portal_scope
)
SELECT
  4301, '活动查询', 4300, 1, '#', '', '',
  1, 0, 'F', '0', '0', 'ygb:workerActivity:query', '#',
  'admin', NOW(), '', 'ygb'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 4301);

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, remark, portal_scope
)
SELECT
  4302, '活动处理', 4300, 2, '#', '', '',
  1, 0, 'F', '0', '0', 'ygb:workerActivity:handle', '#',
  'admin', NOW(), '', 'ygb'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 4302);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu WHERE menu_id IN (4301, 4302)
AND NOT EXISTS (SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = 1 AND rm.menu_id = sys_menu.menu_id);
