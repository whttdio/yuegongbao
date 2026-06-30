set names utf8mb4;

-- =============================================================================
-- Phase 48: YGB/AZB 验收测试账号（默认密码统一 admin123）
-- bcrypt: $2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2
-- =============================================================================
-- 执行前提：yuegongbao_20260417.sql + ygb_gd_test_seed.sql + ygb_phase8/10 + ygb_compare_dual_portal_accounts.sql
-- 劳动者端另需：docker/mysql/init/02-worker-demo-account.sql 或 tmp/sql-runner/seed-worker-data.js

-- 1) 角色门户范围兜底
update sys_role set allowed_portal_scope = 'both', default_portal_code = 'ygb' where role_id in (1, 2, 101, 102, 103, 104, 105, 106);
update sys_role set allowed_portal_scope = 'azb', default_portal_code = 'azb' where role_id in (107, 108, 109);

-- 2) 单角色验收账号（900110~900119，可重复执行）
insert into sys_user (
  user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password,
  status, del_flag, login_ip, login_date, pwd_update_date, create_by, create_time, update_by, update_time, remark
) values
(
  900110, 110, 'ygb_hrss', '人社监管验收', '00', 'ygb.hrss@yuegongbao.local', '13990000110', '1', '',
  '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2',
  '0', '0', '', null, sysdate(), 'admin', sysdate(), '', null,
  '默认密码 admin123；门户 YGB；角色 106 人社监管员'
),
(
  900111, 110, 'azb_emergency', '应急监管验收', '00', 'azb.emergency@yuegongbao.local', '13990000111', '1', '',
  '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2',
  '0', '0', '', null, sysdate(), 'admin', sysdate(), '', null,
  '默认密码 admin123；门户 AZB；角色 107 应急监管员'
),
(
  900112, 110, 'azb_insurer', '保险协同验收', '00', 'azb.insurer@yuegongbao.local', '13990000112', '1', '',
  '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2',
  '0', '0', '', null, sysdate(), 'admin', sysdate(), '', null,
  '默认密码 admin123；门户 AZB；角色 108 保险公司'
),
(
  900113, 110, 'azb_bank', '银行协同验收', '00', 'azb.bank@yuegongbao.local', '13990000113', '1', '',
  '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2',
  '0', '0', '', null, sysdate(), 'admin', sysdate(), '', null,
  '默认密码 admin123；门户 AZB；角色 109 银行'
),
(
  900114, 110, 'ygb_province', '省级监管验收', '00', 'ygb.province@yuegongbao.local', '13990000114', '1', '',
  '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2',
  '0', '0', '', null, sysdate(), 'admin', sysdate(), '', null,
  '默认密码 admin123；门户 YGB；角色 101 省级监管员'
),
(
  900115, 111, 'ygb_city', '市级监管验收', '00', 'ygb.city@yuegongbao.local', '13990000115', '1', '',
  '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2',
  '0', '0', '', null, sysdate(), 'admin', sysdate(), '', null,
  '默认密码 admin123；门户 YGB；角色 102 市级监管员'
),
(
  900116, 114, 'ygb_county', '区县监管验收', '00', 'ygb.county@yuegongbao.local', '13990000116', '1', '',
  '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2',
  '0', '0', '', null, sysdate(), 'admin', sysdate(), '', null,
  '默认密码 admin123；门户 YGB；角色 103 区县监管员'
),
(
  900117, 118, 'ygb_ent_admin', '企业管理员验收', '00', 'ygb.ent.admin@yuegongbao.local', '13990000117', '1', '',
  '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2',
  '0', '0', '', null, sysdate(), 'admin', sysdate(), '', null,
  '默认密码 admin123；门户 YGB；角色 104 企业管理员'
),
(
  900118, 118, 'ygb_ent_op', '企业经办验收', '00', 'ygb.ent.op@yuegongbao.local', '13990000118', '2', '',
  '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2',
  '0', '0', '', null, sysdate(), 'admin', sysdate(), '', null,
  '默认密码 admin123；门户 YGB；角色 105 企业经办员'
)
on duplicate key update
  dept_id = values(dept_id),
  nick_name = values(nick_name),
  password = values(password),
  status = values(status),
  remark = values(remark);

-- 3) 用户-角色绑定
delete from sys_user_role where user_id between 900110 and 900118;

insert into sys_user_role (user_id, role_id) values
  (900110, 106),
  (900111, 107),
  (900112, 108),
  (900113, 109),
  (900114, 101),
  (900115, 102),
  (900116, 103),
  (900117, 104),
  (900118, 105);

-- 4) 劳动者端 sys_user（绑定 t_person 10001 赵志成）
insert into sys_user (
  user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password,
  status, del_flag, login_ip, login_date, pwd_update_date, create_by, create_time, update_by, update_time, remark
)
select
  910001, 118, '13700010001', '赵志成', '00', 'worker-demo@yuegongbao.local', '13700010001', '1', '',
  '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2',
  '0', '0', '', null, sysdate(), 'admin', sysdate(), '', null,
  '劳动者端密码登录；默认密码 admin123'
from dual
where exists (select 1 from t_person where person_id = 10001)
  and not exists (select 1 from sys_user where user_name = '13700010001');

insert ignore into sys_user_role (user_id, role_id) values (910001, 2);

-- 5) 双门户全功能对比账号（与 ygb_compare_dual_portal_accounts.sql 一致，此处做 upsert 不删除）
insert into sys_user (
  user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password,
  status, del_flag, login_ip, login_date, pwd_update_date, create_by, create_time, update_by, update_time, remark
) values
(
  900101, 103, 'ygb_compare_full', '粤工保全功能对比', '00', 'ygb.compare.full@yuegongbao.local', '13990010101', '1', '',
  '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2',
  '0', '0', '', null, sysdate(), 'admin', sysdate(), '', null,
  '默认密码 admin123；YGB；角色 104+106'
),
(
  900102, 103, 'azb_compare_full', '安责保全功能对比', '00', 'azb.compare.full@yuegongbao.local', '13990010202', '1', '',
  '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2',
  '0', '0', '', null, sysdate(), 'admin', sysdate(), '', null,
  '默认密码 admin123；AZB；角色 107'
)
on duplicate key update
  password = values(password),
  nick_name = values(nick_name),
  remark = values(remark);

delete from sys_user_role where user_id in (900101, 900102);
insert into sys_user_role (user_id, role_id) values
  (900101, 104),
  (900101, 106),
  (900102, 107);

-- 核验：
-- select u.user_name, u.nick_name, group_concat(r.role_name order by r.role_id) roles,
--        group_concat(distinct r.default_portal_code) portals
-- from sys_user u
-- join sys_user_role ur on u.user_id = ur.user_id
-- join sys_role r on ur.role_id = r.role_id
-- where u.user_name in ('admin','ygb_hrss','azb_emergency','azb_insurer','azb_bank',
--                       'ygb_compare_full','azb_compare_full','13700010001')
-- group by u.user_id, u.user_name, u.nick_name;
