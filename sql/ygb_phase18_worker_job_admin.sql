SET NAMES utf8mb4;

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, remark, portal_scope
)
SELECT
  4200, '招聘岗位管理', 0, 13, 'workerJob', 'ygb/workerJob/index', 'YgbWorkerJob',
  1, 0, 'C', '0', '0', 'ygb:workerJob:list', 'peoples',
  'admin', NOW(), '官网招聘市场岗位维护', 'ygb'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 4200);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 4200 FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 4200);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 2, 4200 FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 2 AND menu_id = 4200);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark, portal_scope)
SELECT 4201, '招聘岗位查询', 4200, 1, '#', '', '', 1, 0, 'F', '0', '0', 'ygb:workerJob:query', '#', 'admin', NOW(), '', 'ygb'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 4201);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark, portal_scope)
SELECT 4202, '招聘岗位新增', 4200, 2, '#', '', '', 1, 0, 'F', '0', '0', 'ygb:workerJob:add', '#', 'admin', NOW(), '', 'ygb'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 4202);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark, portal_scope)
SELECT 4203, '招聘岗位修改', 4200, 3, '#', '', '', 1, 0, 'F', '0', '0', 'ygb:workerJob:edit', '#', 'admin', NOW(), '', 'ygb'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 4203);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark, portal_scope)
SELECT 4204, '招聘岗位删除', 4200, 4, '#', '', '', 1, 0, 'F', '0', '0', 'ygb:workerJob:remove', '#', 'admin', NOW(), '', 'ygb'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 4204);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu WHERE menu_id IN (4201, 4202, 4203, 4204)
AND NOT EXISTS (SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = 1 AND rm.menu_id = sys_menu.menu_id);
