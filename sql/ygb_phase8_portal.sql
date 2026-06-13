SET NAMES utf8mb4;

UPDATE sys_menu
SET menu_name = '安责保官网',
    path = 'officialSite',
    component = 'portal/official/index',
    route_name = 'OfficialSite',
    is_frame = 0,
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = '',
    icon = 'guide',
    update_by = 'admin',
    update_time = NOW(),
    remark = '安责保官网入口（新窗口打开）'
WHERE menu_id = 4;
