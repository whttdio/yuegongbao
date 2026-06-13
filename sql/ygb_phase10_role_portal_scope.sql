SET NAMES utf8mb4;

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

UPDATE sys_role
SET allowed_portal_scope = 'both'
WHERE allowed_portal_scope IS NULL
   OR TRIM(allowed_portal_scope) = '';

UPDATE sys_role
SET default_portal_code = 'ygb'
WHERE default_portal_code IS NULL
   OR TRIM(default_portal_code) = '';

UPDATE sys_role
SET allowed_portal_scope = 'ygb',
    default_portal_code = 'ygb'
WHERE role_id IN (1, 2, 101, 102, 103, 106);

UPDATE sys_role
SET allowed_portal_scope = 'both',
    default_portal_code = 'ygb'
WHERE role_id IN (104, 105);

UPDATE sys_role
SET allowed_portal_scope = 'azb',
    default_portal_code = 'azb'
WHERE role_id IN (107, 108, 109);
