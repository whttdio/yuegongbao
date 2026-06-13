SET NAMES utf8mb4;

SET @portal_scope_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'sys_menu'
      AND COLUMN_NAME = 'portal_scope'
);

SET @portal_scope_sql := IF(
    @portal_scope_exists = 0,
    "ALTER TABLE sys_menu ADD COLUMN portal_scope varchar(16) NOT NULL DEFAULT 'both' COMMENT '前端归属(ygb/azb/both)' AFTER status",
    'SELECT 1'
);

PREPARE stmt_portal_scope FROM @portal_scope_sql;
EXECUTE stmt_portal_scope;
DEALLOCATE PREPARE stmt_portal_scope;

UPDATE sys_menu
SET portal_scope = 'both'
WHERE portal_scope IS NULL
   OR portal_scope = '';

UPDATE sys_menu
SET portal_scope = 'azb'
WHERE menu_id IN (4);

UPDATE sys_menu
SET portal_scope = 'ygb'
WHERE menu_id IN (
    2020, 2003, 2004, 2005, 2006, 2007,
    2008, 2009, 2010, 2012, 2013,
    4000, 4001, 4020, 4021,
    2300, 2301, 2302, 2303, 2304,
    2400, 2401, 2402, 2403, 2404,
    2500, 2501, 2502,
    2600, 2601, 2602, 2603, 2604, 2605, 2606, 2607,
    2700, 2701, 2702, 2703,
    2800, 2801, 2802,
    2900, 2901, 2902,
    3000, 3001, 3002, 3003,
    3200, 3201, 3202,
    3300, 3301, 3302,
    4005, 4006, 4007,
    4025, 4026, 4027
);

UPDATE sys_menu
SET portal_scope = 'both'
WHERE menu_id IN (
    2000, 2001, 2002,
    2040, 2011,
    2060, 2014, 2015, 2016,
    2080, 2017, 2018,
    3900, 3901, 3910, 3911,
    3920, 3921, 3930, 3931, 3932,
    3940, 3941, 3942, 3950, 3951, 3952, 3953, 3954, 3955, 3956, 3957,
    3960, 3961, 3962, 3965, 3966, 3967,
    3980, 3981, 3985, 3986, 3987,
    4040, 4041, 4045, 4046, 4047, 4048, 4049, 4050, 4051
);
