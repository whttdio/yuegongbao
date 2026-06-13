-- 修复运行环境中仍残留为问号的菜单标题

update sys_menu
set menu_name = '工伤与预防',
    update_by = 'admin',
    update_time = sysdate(),
    remark = '阶段42修复：工伤与预防目录标题'
where menu_id = 2060;

update sys_menu
set menu_name = '统计报表',
    update_by = 'admin',
    update_time = sysdate(),
    remark = '阶段42修复：统计报表目录标题'
where menu_id = 4920;
