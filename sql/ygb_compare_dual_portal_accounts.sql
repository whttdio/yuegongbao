SET NAMES utf8mb4;

-- 对比账号说明
-- 1. 粤工保全功能账号：默认进入 ygb，覆盖企业办理 + 人社监管高权限业务角色
-- 2. 安责保全功能账号：默认进入 azb，覆盖应急监管高权限业务角色
-- 3. 默认密码统一为：admin123

-- 若角色扩展字段尚未存在，则补齐
SET @allowed_portal_scope_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'sys_role'
      AND COLUMN_NAME = 'allowed_portal_scope'
);

SET @allowed_portal_scope_sql := IF(
    @allowed_portal_scope_exists = 0,
    "ALTER TABLE sys_role ADD COLUMN allowed_portal_scope varchar(16) NOT NULL DEFAULT 'both' COMMENT '允许前端范围(ygb/azb/both)' AFTER role_sort",
    'SELECT 1'
);

PREPARE stmt_allowed_portal_scope FROM @allowed_portal_scope_sql;
EXECUTE stmt_allowed_portal_scope;
DEALLOCATE PREPARE stmt_allowed_portal_scope;

SET @default_portal_code_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'sys_role'
      AND COLUMN_NAME = 'default_portal_code'
);

SET @default_portal_code_sql := IF(
    @default_portal_code_exists = 0,
    "ALTER TABLE sys_role ADD COLUMN default_portal_code varchar(16) NOT NULL DEFAULT 'ygb' COMMENT '默认前端入口(ygb/azb)' AFTER allowed_portal_scope",
    'SELECT 1'
);

PREPARE stmt_default_portal_code FROM @default_portal_code_sql;
EXECUTE stmt_default_portal_code;
DEALLOCATE PREPARE stmt_default_portal_code;

-- 核心角色兜底
INSERT INTO sys_role (
    role_id, role_name, role_key, role_sort, allowed_portal_scope, default_portal_code,
    data_scope, menu_check_strictly, dept_check_strictly, status, del_flag,
    create_by, create_time, update_by, update_time, remark
)
SELECT 104, '企业管理员', 'ygb_enterprise_admin', 13, 'both', 'ygb',
       '3', 1, 1, '0', '0', 'admin', SYSDATE(), '', NULL, '企业侧管理角色'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_id = 104);

INSERT INTO sys_role (
    role_id, role_name, role_key, role_sort, allowed_portal_scope, default_portal_code,
    data_scope, menu_check_strictly, dept_check_strictly, status, del_flag,
    create_by, create_time, update_by, update_time, remark
)
SELECT 105, '企业经办员', 'ygb_enterprise_operator', 14, 'both', 'ygb',
       '3', 1, 1, '0', '0', 'admin', SYSDATE(), '', NULL, '企业侧经办角色'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_id = 105);

INSERT INTO sys_role (
    role_id, role_name, role_key, role_sort, allowed_portal_scope, default_portal_code,
    data_scope, menu_check_strictly, dept_check_strictly, status, del_flag,
    create_by, create_time, update_by, update_time, remark
)
SELECT 106, '人社监管员', 'ygb_hrss_supervisor', 15, 'ygb', 'ygb',
       '4', 1, 1, '0', '0', 'admin', SYSDATE(), '', NULL, '粤工保人社监管角色'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_id = 106);

INSERT INTO sys_role (
    role_id, role_name, role_key, role_sort, allowed_portal_scope, default_portal_code,
    data_scope, menu_check_strictly, dept_check_strictly, status, del_flag,
    create_by, create_time, update_by, update_time, remark
)
SELECT 107, '应急监管员', 'ygb_emergency_supervisor', 16, 'azb', 'azb',
       '4', 1, 1, '0', '0', 'admin', SYSDATE(), '', NULL, '安责保应急监管角色'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_id = 107);

UPDATE sys_role
SET allowed_portal_scope = 'both',
    default_portal_code = 'ygb'
WHERE role_id IN (104, 105);

UPDATE sys_role
SET allowed_portal_scope = 'ygb',
    default_portal_code = 'ygb'
WHERE role_id = 106;

UPDATE sys_role
SET allowed_portal_scope = 'azb',
    default_portal_code = 'azb'
WHERE role_id = 107;

-- 对比账号使用固定 ID，便于重复执行
DELETE FROM sys_user_role WHERE user_id IN (900101, 900102);
DELETE FROM sys_user WHERE user_id IN (900101, 900102);

INSERT INTO sys_user (
    user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password,
    status, del_flag, login_ip, login_date, pwd_update_date, create_by, create_time, update_by, update_time, remark
)
VALUES
(
    900101, 103, 'ygb_compare_full', '粤工保全功能对比账号', '00', 'ygb.compare.full@yuegongbao.local', '13990010101', '1', '',
    '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2',
    '0', '0', '', NULL, SYSDATE(), 'admin', SYSDATE(), '', NULL,
    '默认密码：admin123；默认门户：ygb；角色：104企业管理员 + 106人社监管员'
),
(
    900102, 103, 'azb_compare_full', '安责保全功能对比账号', '00', 'azb.compare.full@yuegongbao.local', '13990010202', '1', '',
    '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2',
    '0', '0', '', NULL, SYSDATE(), 'admin', SYSDATE(), '', NULL,
    '默认密码：admin123；默认门户：azb；角色：107应急监管员'
);

INSERT INTO sys_user_role (user_id, role_id) VALUES (900101, 104);
INSERT INTO sys_user_role (user_id, role_id) VALUES (900101, 106);
INSERT INTO sys_user_role (user_id, role_id) VALUES (900102, 107);

-- 执行后可用以下语句核验
-- SELECT u.user_id, u.user_name, u.nick_name, r.role_key, r.allowed_portal_scope, r.default_portal_code
-- FROM sys_user u
-- LEFT JOIN sys_user_role ur ON u.user_id = ur.user_id
-- LEFT JOIN sys_role r ON ur.role_id = r.role_id
-- WHERE u.user_id IN (900101, 900102)
-- ORDER BY u.user_id, r.role_id;
