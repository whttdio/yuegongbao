SET NAMES utf8mb4;

SET @portal_scope_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'sys_role_menu'
      AND COLUMN_NAME = 'portal_scope'
);

SET @add_portal_scope_sql := IF(
    @portal_scope_exists = 0,
    "ALTER TABLE sys_role_menu ADD COLUMN portal_scope varchar(16) NOT NULL DEFAULT 'both' COMMENT '角色菜单授权前端范围(ygb/azb/both)' AFTER menu_id",
    'SELECT 1'
);

PREPARE stmt_add_portal_scope FROM @add_portal_scope_sql;
EXECUTE stmt_add_portal_scope;
DEALLOCATE PREPARE stmt_add_portal_scope;

UPDATE sys_role_menu
SET portal_scope = 'both'
WHERE portal_scope IS NULL
   OR TRIM(portal_scope) = '';

SET @primary_key_has_portal_scope := (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'sys_role_menu'
      AND INDEX_NAME = 'PRIMARY'
      AND COLUMN_NAME = 'portal_scope'
);

SET @rebuild_primary_key_sql := IF(
    @primary_key_has_portal_scope = 0,
    "ALTER TABLE sys_role_menu DROP PRIMARY KEY, ADD PRIMARY KEY (role_id, menu_id, portal_scope)",
    'SELECT 1'
);

PREPARE stmt_rebuild_primary_key FROM @rebuild_primary_key_sql;
EXECUTE stmt_rebuild_primary_key;
DEALLOCATE PREPARE stmt_rebuild_primary_key;

SET @idx_role_scope_exists := (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'sys_role_menu'
      AND INDEX_NAME = 'idx_sys_role_menu_role_scope'
);

SET @add_idx_role_scope_sql := IF(
    @idx_role_scope_exists = 0,
    "ALTER TABLE sys_role_menu ADD INDEX idx_sys_role_menu_role_scope (role_id, portal_scope)",
    'SELECT 1'
);

PREPARE stmt_add_idx_role_scope FROM @add_idx_role_scope_sql;
EXECUTE stmt_add_idx_role_scope;
DEALLOCATE PREPARE stmt_add_idx_role_scope;
