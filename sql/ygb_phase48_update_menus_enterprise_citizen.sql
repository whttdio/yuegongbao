-- 更新 YGB 企业后台与便民服务菜单，指向前端新实现的页面
-- 执行对象：本地 yuegongbao 数据库 sys_menu 表

SET NAMES utf8mb4;

-- 1. 企业仪表盘指向新 dashboard 页面
UPDATE sys_menu
SET component = 'ygb/enterprisePortal/dashboard/index',
    remark = '20.1 企业仪表盘（新）',
    update_time = NOW()
WHERE menu_id = 6171;

-- 2. 便民服务子页面指向新的展示页面
UPDATE sys_menu
SET component = 'ygb/citizenService/warmMap/index',
    query = '',
    remark = '21.1 暖新地图（展示页）',
    update_time = NOW()
WHERE menu_id = 6192;

UPDATE sys_menu
SET component = 'ygb/citizenService/trainingCourse/index',
    query = '',
    remark = '21.2 培训课程（展示页）',
    update_time = NOW()
WHERE menu_id = 6193;

UPDATE sys_menu
SET component = 'ygb/citizenService/lawLibrary/index',
    query = '',
    remark = '21.3 法规库（展示页）',
    update_time = NOW()
WHERE menu_id = 6194;

UPDATE sys_menu
SET component = 'ygb/citizenService/mutualHelp/index',
    query = '',
    remark = '21.4 互助圈（展示页）',
    update_time = NOW()
WHERE menu_id = 6195;

UPDATE sys_menu
SET component = 'ygb/citizenService/recruitMarket/index',
    query = '',
    remark = '21.5 招聘用工市场（展示页）',
    update_time = NOW()
WHERE menu_id = 6196;
