set names utf8mb4;

-- =============================================================================
-- Phase 45: Enterprise + region data scope (P0/P1)
-- Prerequisites: ygb_gd_test_seed.sql
-- =============================================================================

-- 1) sys_user.enterprise_id
set @col_exists = (
  select count(1) from information_schema.columns
  where table_schema = database() and table_name = 'sys_user' and column_name = 'enterprise_id'
);
set @sql = if(@col_exists = 0,
  'alter table sys_user add column enterprise_id bigint(20) default null comment ''关联企业ID(t_enterprise)'' after dept_id',
  'select ''sys_user.enterprise_id already exists'' as msg'
);
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;

set @idx_exists = (
  select count(1) from information_schema.statistics
  where table_schema = database() and table_name = 'sys_user' and index_name = 'idx_sys_user_enterprise_id'
);
set @sql = if(@idx_exists = 0,
  'create index idx_sys_user_enterprise_id on sys_user (enterprise_id)',
  'select ''idx_sys_user_enterprise_id already exists'' as msg'
);
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;

-- 2) sys_dept.region_code
set @col_exists = (
  select count(1) from information_schema.columns
  where table_schema = database() and table_name = 'sys_dept' and column_name = 'region_code'
);
set @sql = if(@col_exists = 0,
  'alter table sys_dept add column region_code varchar(12) default null comment ''行政区划代码'' after email',
  'select ''sys_dept.region_code already exists'' as msg'
);
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;

-- 3) Backfill dept region_code (replaces hardcoded dept_id mapping)
update sys_dept set region_code = '440000' where dept_id = 110 and (region_code is null or region_code = '');
update sys_dept set region_code = '440100' where dept_id = 111 and (region_code is null or region_code = '440106');
update sys_dept set region_code = '440300' where dept_id = 112 and (region_code is null or region_code = '440305');
update sys_dept set region_code = '440600' where dept_id = 113 and (region_code is null or region_code = '440606');
update sys_dept set region_code = '440106' where dept_id in (114, 118) and (region_code is null or region_code = '');
update sys_dept set region_code = '440305' where dept_id in (115, 119) and (region_code is null or region_code = '');
update sys_dept set region_code = '440606' where dept_id in (116, 120) and (region_code is null or region_code = '');

-- 4) Backfill enterprise users (dept 118/119/120 -> enterprise 1001/1002/1003)
update sys_user set enterprise_id = 1001 where dept_id = 118 and user_name in ('gzentadmin', 'gzentop');
update sys_user set enterprise_id = 1002 where dept_id = 119 and user_name in ('szentadmin', 'szentop');
update sys_user set enterprise_id = 1003 where dept_id = 120 and user_name in ('fsentadmin', 'fsentop');
