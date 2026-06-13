SET NAMES utf8mb4;

-- 粤工保官网菜单（portal_scope = ygb，与安责保官网 menu_id=4 对称）
INSERT INTO sys_menu (
    menu_id, menu_name, parent_id, order_num, path, component, route_name,
    is_frame, is_cache, menu_type, visible, status, perms, icon,
    create_by, create_time, remark, portal_scope
)
SELECT
    5, '粤工保官网', 0, 5, 'ygbOfficialSite', 'portal/ygb-official/index', 'YgbOfficialSite',
    0, 0, 'C', '0', '0', '', 'guide',
    'admin', NOW(), '粤工保官网入口（新窗口打开）', 'ygb'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 5);

-- 为常用角色授权粤工保官网菜单
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 5 FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 5);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 2, 5 FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 2 AND menu_id = 5);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 101, 5 FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 101 AND menu_id = 5);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 102, 5 FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 102 AND menu_id = 5);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 103, 5 FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 103 AND menu_id = 5);
