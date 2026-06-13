-- 粤工保广东测试数据种子脚本
-- 使用前提：
-- 1. 已导入 yuegongbao_20260417.sql
-- 2. 已执行 ygb_phase1_enterprise_person.sql
--
-- 说明：
-- - 新增广东监管组织、企业组织、测试角色、测试用户
-- - 新增广东企业与人员测试样本
-- - 所有新增测试账号默认密码均为：admin123

-- ----------------------------
-- 1、广东测试组织
-- ----------------------------
insert into sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time)
select 110, 100, '0,100', '广东省监管中心', 10, '省级联调负责人', '13800000010', 'gd-supervisor@yuegongbao.local', '0', '0', 'admin', sysdate(), '', null
from dual where not exists (select 1 from sys_dept where dept_id = 110);

insert into sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time)
select 111, 110, '0,100,110', '广州市监管中心', 11, '广州联调负责人', '13800000011', 'gz-supervisor@yuegongbao.local', '0', '0', 'admin', sysdate(), '', null
from dual where not exists (select 1 from sys_dept where dept_id = 111);

insert into sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time)
select 112, 110, '0,100,110', '深圳市监管中心', 12, '深圳联调负责人', '13800000012', 'sz-supervisor@yuegongbao.local', '0', '0', 'admin', sysdate(), '', null
from dual where not exists (select 1 from sys_dept where dept_id = 112);

insert into sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time)
select 113, 110, '0,100,110', '佛山市监管中心', 13, '佛山联调负责人', '13800000013', 'fs-supervisor@yuegongbao.local', '0', '0', 'admin', sysdate(), '', null
from dual where not exists (select 1 from sys_dept where dept_id = 113);

insert into sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time)
select 114, 111, '0,100,110,111', '广州市天河区监管组', 1, '天河区联调负责人', '13800000014', 'th-inspector@yuegongbao.local', '0', '0', 'admin', sysdate(), '', null
from dual where not exists (select 1 from sys_dept where dept_id = 114);

insert into sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time)
select 115, 112, '0,100,110,112', '深圳市南山区监管组', 1, '南山区联调负责人', '13800000015', 'ns-inspector@yuegongbao.local', '0', '0', 'admin', sysdate(), '', null
from dual where not exists (select 1 from sys_dept where dept_id = 115);

insert into sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time)
select 116, 113, '0,100,110,113', '佛山市顺德区监管组', 1, '顺德区联调负责人', '13800000016', 'sd-inspector@yuegongbao.local', '0', '0', 'admin', sysdate(), '', null
from dual where not exists (select 1 from sys_dept where dept_id = 116);

insert into sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time)
select 118, 114, '0,100,110,111,114', '广州南粤人力资源有限公司', 10, '企业管理员', '13800000118', 'contact@nanyue-hr.local', '0', '0', 'admin', sysdate(), '', null
from dual where not exists (select 1 from sys_dept where dept_id = 118);

insert into sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time)
select 119, 115, '0,100,110,112,115', '深圳鹏城机电工程有限公司', 10, '企业管理员', '13800000119', 'contact@pengcheng-me.local', '0', '0', 'admin', sysdate(), '', null
from dual where not exists (select 1 from sys_dept where dept_id = 119);

insert into sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time)
select 120, 116, '0,100,110,113,116', '佛山顺德智造服务有限公司', 10, '企业管理员', '13800000120', 'contact@shunde-smart.local', '0', '0', 'admin', sysdate(), '', null
from dual where not exists (select 1 from sys_dept where dept_id = 120);

-- ----------------------------
-- 2、测试角色
-- ----------------------------
insert into sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark)
select 101, '省级监管员', 'ygb_province_supervisor', 10, '4', 1, 1, '0', '0', 'admin', sysdate(), '', null, '广东省级联调用监管角色'
from dual where not exists (select 1 from sys_role where role_id = 101);

insert into sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark)
select 102, '市级监管员', 'ygb_city_supervisor', 11, '4', 1, 1, '0', '0', 'admin', sysdate(), '', null, '广东市级联调用监管角色'
from dual where not exists (select 1 from sys_role where role_id = 102);

insert into sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark)
select 103, '区县监管员', 'ygb_county_supervisor', 12, '4', 1, 1, '0', '0', 'admin', sysdate(), '', null, '广东区县联调用监管角色'
from dual where not exists (select 1 from sys_role where role_id = 103);

insert into sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark)
select 104, '企业管理员', 'ygb_enterprise_admin', 13, '3', 1, 1, '0', '0', 'admin', sysdate(), '', null, '企业侧管理角色'
from dual where not exists (select 1 from sys_role where role_id = 104);

insert into sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark)
select 105, '企业经办员', 'ygb_enterprise_operator', 14, '3', 1, 1, '0', '0', 'admin', sysdate(), '', null, '企业侧经办角色'
from dual where not exists (select 1 from sys_role where role_id = 105);

-- ----------------------------
-- 3、角色菜单授权
-- 监管角色：查询、导出
-- 企业管理员：完整权限
-- 企业经办员：查询、新增、修改
-- ----------------------------
insert into sys_role_menu (role_id, menu_id)
select 101, 2000 from dual where not exists (select 1 from sys_role_menu where role_id = 101 and menu_id = 2000);
insert into sys_role_menu (role_id, menu_id)
select 101, 2020 from dual where not exists (select 1 from sys_role_menu where role_id = 101 and menu_id = 2020);
insert into sys_role_menu (role_id, menu_id)
select 101, 2001 from dual where not exists (select 1 from sys_role_menu where role_id = 101 and menu_id = 2001);
insert into sys_role_menu (role_id, menu_id)
select 101, 2002 from dual where not exists (select 1 from sys_role_menu where role_id = 101 and menu_id = 2002);
insert into sys_role_menu (role_id, menu_id)
select 101, 2100 from dual where not exists (select 1 from sys_role_menu where role_id = 101 and menu_id = 2100);
insert into sys_role_menu (role_id, menu_id)
select 101, 2104 from dual where not exists (select 1 from sys_role_menu where role_id = 101 and menu_id = 2104);
insert into sys_role_menu (role_id, menu_id)
select 101, 2200 from dual where not exists (select 1 from sys_role_menu where role_id = 101 and menu_id = 2200);
insert into sys_role_menu (role_id, menu_id)
select 101, 2204 from dual where not exists (select 1 from sys_role_menu where role_id = 101 and menu_id = 2204);

insert into sys_role_menu (role_id, menu_id)
select 102, menu_id from sys_role_menu where role_id = 101
and not exists (select 1 from sys_role_menu s where s.role_id = 102 and s.menu_id = sys_role_menu.menu_id);

insert into sys_role_menu (role_id, menu_id)
select 103, menu_id from sys_role_menu where role_id = 101
and not exists (select 1 from sys_role_menu s where s.role_id = 103 and s.menu_id = sys_role_menu.menu_id);

insert into sys_role_menu (role_id, menu_id)
select 104, menu_id from sys_role_menu where role_id = 2 and menu_id between 2000 and 2204
and not exists (select 1 from sys_role_menu s where s.role_id = 104 and s.menu_id = sys_role_menu.menu_id);

insert into sys_role_menu (role_id, menu_id)
select 105, 2000 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2000);
insert into sys_role_menu (role_id, menu_id)
select 105, 2020 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2020);
insert into sys_role_menu (role_id, menu_id)
select 105, 2001 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2001);
insert into sys_role_menu (role_id, menu_id)
select 105, 2002 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2002);
insert into sys_role_menu (role_id, menu_id)
select 105, 2100 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2100);
insert into sys_role_menu (role_id, menu_id)
select 105, 2101 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2101);
insert into sys_role_menu (role_id, menu_id)
select 105, 2102 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2102);
insert into sys_role_menu (role_id, menu_id)
select 105, 2200 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2200);
insert into sys_role_menu (role_id, menu_id)
select 105, 2201 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2201);
insert into sys_role_menu (role_id, menu_id)
select 105, 2202 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2202);

-- ----------------------------
-- 4、测试用户
-- 默认密码：admin123
-- ----------------------------
insert into sys_user (user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, pwd_update_date, create_by, create_time, update_by, update_time, remark)
select 1001, 110, 'gdprovince', '广东省监管员', '00', 'gdprovince@yuegongbao.local', '13900001001', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '', null, sysdate(), 'admin', sysdate(), '', null, '默认密码：admin123'
from dual where not exists (select 1 from sys_user where user_name = 'gdprovince');

insert into sys_user (user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, pwd_update_date, create_by, create_time, update_by, update_time, remark)
select 1002, 111, 'gzsupervisor', '广州市监管员', '00', 'gzsupervisor@yuegongbao.local', '13900001002', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '', null, sysdate(), 'admin', sysdate(), '', null, '默认密码：admin123'
from dual where not exists (select 1 from sys_user where user_name = 'gzsupervisor');

insert into sys_user (user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, pwd_update_date, create_by, create_time, update_by, update_time, remark)
select 1003, 112, 'szsupervisor', '深圳市监管员', '00', 'szsupervisor@yuegongbao.local', '13900001003', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '', null, sysdate(), 'admin', sysdate(), '', null, '默认密码：admin123'
from dual where not exists (select 1 from sys_user where user_name = 'szsupervisor');

insert into sys_user (user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, pwd_update_date, create_by, create_time, update_by, update_time, remark)
select 1004, 115, 'nsinspector', '南山区监管员', '00', 'nsinspector@yuegongbao.local', '13900001004', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '', null, sysdate(), 'admin', sysdate(), '', null, '默认密码：admin123'
from dual where not exists (select 1 from sys_user where user_name = 'nsinspector');

insert into sys_user (user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, pwd_update_date, create_by, create_time, update_by, update_time, remark)
select 1005, 116, 'sdinspector', '顺德区监管员', '00', 'sdinspector@yuegongbao.local', '13900001005', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '', null, sysdate(), 'admin', sysdate(), '', null, '默认密码：admin123'
from dual where not exists (select 1 from sys_user where user_name = 'sdinspector');

insert into sys_user (user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, pwd_update_date, create_by, create_time, update_by, update_time, remark)
select 1006, 118, 'gzentadmin', '广州南粤企业管理员', '00', 'gzentadmin@yuegongbao.local', '13900001006', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '', null, sysdate(), 'admin', sysdate(), '', null, '默认密码：admin123'
from dual where not exists (select 1 from sys_user where user_name = 'gzentadmin');

insert into sys_user (user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, pwd_update_date, create_by, create_time, update_by, update_time, remark)
select 1007, 118, 'gzentop', '广州南粤企业经办员', '00', 'gzentop@yuegongbao.local', '13900001007', '2', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '', null, sysdate(), 'admin', sysdate(), '', null, '默认密码：admin123'
from dual where not exists (select 1 from sys_user where user_name = 'gzentop');

insert into sys_user (user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, pwd_update_date, create_by, create_time, update_by, update_time, remark)
select 1008, 119, 'szentadmin', '深圳鹏城企业管理员', '00', 'szentadmin@yuegongbao.local', '13900001008', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '', null, sysdate(), 'admin', sysdate(), '', null, '默认密码：admin123'
from dual where not exists (select 1 from sys_user where user_name = 'szentadmin');

insert into sys_user (user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, pwd_update_date, create_by, create_time, update_by, update_time, remark)
select 1009, 119, 'szentop', '深圳鹏城企业经办员', '00', 'szentop@yuegongbao.local', '13900001009', '2', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '', null, sysdate(), 'admin', sysdate(), '', null, '默认密码：admin123'
from dual where not exists (select 1 from sys_user where user_name = 'szentop');

insert into sys_user (user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, pwd_update_date, create_by, create_time, update_by, update_time, remark)
select 1010, 120, 'fsentadmin', '佛山顺德企业管理员', '00', 'fsentadmin@yuegongbao.local', '13900001010', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '', null, sysdate(), 'admin', sysdate(), '', null, '默认密码：admin123'
from dual where not exists (select 1 from sys_user where user_name = 'fsentadmin');

insert into sys_user_role (user_id, role_id)
select 1001, 101 from dual where not exists (select 1 from sys_user_role where user_id = 1001 and role_id = 101);
insert into sys_user_role (user_id, role_id)
select 1002, 102 from dual where not exists (select 1 from sys_user_role where user_id = 1002 and role_id = 102);
insert into sys_user_role (user_id, role_id)
select 1003, 102 from dual where not exists (select 1 from sys_user_role where user_id = 1003 and role_id = 102);
insert into sys_user_role (user_id, role_id)
select 1004, 103 from dual where not exists (select 1 from sys_user_role where user_id = 1004 and role_id = 103);
insert into sys_user_role (user_id, role_id)
select 1005, 103 from dual where not exists (select 1 from sys_user_role where user_id = 1005 and role_id = 103);
insert into sys_user_role (user_id, role_id)
select 1006, 104 from dual where not exists (select 1 from sys_user_role where user_id = 1006 and role_id = 104);
insert into sys_user_role (user_id, role_id)
select 1007, 105 from dual where not exists (select 1 from sys_user_role where user_id = 1007 and role_id = 105);
insert into sys_user_role (user_id, role_id)
select 1008, 104 from dual where not exists (select 1 from sys_user_role where user_id = 1008 and role_id = 104);
insert into sys_user_role (user_id, role_id)
select 1009, 105 from dual where not exists (select 1 from sys_user_role where user_id = 1009 and role_id = 105);
insert into sys_user_role (user_id, role_id)
select 1010, 104 from dual where not exists (select 1 from sys_user_role where user_id = 1010 and role_id = 104);

-- ----------------------------
-- 5、广东企业测试数据
-- ----------------------------
insert into t_enterprise (enterprise_id, enterprise_name, enterprise_code, region_code, enterprise_type, legal_person, contact_person, contact_phone, address, established_date, sync_status, status, del_flag, create_by, create_time, update_by, update_time, remark)
select 1001, '广州南粤人力资源有限公司', '91440106TEST00001', '440106', '1', '陈建南', '李晓岚', '13811110001', '广东省广州市天河区软件路18号', '2018-03-15', '1', '0', '0', 'admin', sysdate(), '', null, '广东测试派遣单位'
from dual where not exists (select 1 from t_enterprise where enterprise_code = '91440106TEST00001');

insert into t_enterprise (enterprise_id, enterprise_name, enterprise_code, region_code, enterprise_type, legal_person, contact_person, contact_phone, address, established_date, sync_status, status, del_flag, create_by, create_time, update_by, update_time, remark)
select 1002, '深圳鹏城机电工程有限公司', '91440305TEST00002', '440305', '2', '王志鹏', '周敏仪', '13811110002', '广东省深圳市南山区高新南一道66号', '2019-07-08', '1', '0', '0', 'admin', sysdate(), '', null, '广东测试用工单位'
from dual where not exists (select 1 from t_enterprise where enterprise_code = '91440305TEST00002');

insert into t_enterprise (enterprise_id, enterprise_name, enterprise_code, region_code, enterprise_type, legal_person, contact_person, contact_phone, address, established_date, sync_status, status, del_flag, create_by, create_time, update_by, update_time, remark)
select 1003, '佛山顺德智造服务有限公司', '91440606TEST00003', '440606', '3', '何嘉盛', '梁海彤', '13811110003', '广东省佛山市顺德区德胜东路28号', '2020-11-21', '0', '0', '0', 'admin', sysdate(), '', null, '广东测试服务机构'
from dual where not exists (select 1 from t_enterprise where enterprise_code = '91440606TEST00003');

-- ----------------------------
-- 6、广东人员测试数据
-- ----------------------------
insert into t_person (person_id, enterprise_id, region_code, person_name, id_card, mobile, worker_type, job_type, cert_status, insurance_status, employment_status, entry_date, leave_date, del_flag, create_by, create_time, update_by, update_time, remark)
select 10001, 1001, '440106', '赵志成', '440106199001010011', '13700010001', '1', '焊工', '1', '1', '0', '2026-01-05', null, '0', 'admin', sysdate(), '', null, '广州测试人员'
from dual where not exists (select 1 from t_person where id_card = '440106199001010011');

insert into t_person (person_id, enterprise_id, region_code, person_name, id_card, mobile, worker_type, job_type, cert_status, insurance_status, employment_status, entry_date, leave_date, del_flag, create_by, create_time, update_by, update_time, remark)
select 10002, 1001, '440106', '梁宇峰', '440106199202020022', '13700010002', '1', '电工', '2', '1', '0', '2026-01-12', null, '0', 'admin', sysdate(), '', null, '广州测试人员'
from dual where not exists (select 1 from t_person where id_card = '440106199202020022');

insert into t_person (person_id, enterprise_id, region_code, person_name, id_card, mobile, worker_type, job_type, cert_status, insurance_status, employment_status, entry_date, leave_date, del_flag, create_by, create_time, update_by, update_time, remark)
select 10003, 1001, '440106', '陈晓薇', '440106199303030033', '13700010003', '2', '劳资专员', '0', '1', '0', '2026-02-01', null, '0', 'admin', sysdate(), '', null, '广州测试人员'
from dual where not exists (select 1 from t_person where id_card = '440106199303030033');

insert into t_person (person_id, enterprise_id, region_code, person_name, id_card, mobile, worker_type, job_type, cert_status, insurance_status, employment_status, entry_date, leave_date, del_flag, create_by, create_time, update_by, update_time, remark)
select 10004, 1001, '440106', '黄耀文', '440106199404040044', '13700010004', '3', '起重工', '3', '0', '1', '2025-12-11', '2026-04-30', '0', 'admin', sysdate(), '', null, '广州离岗测试人员'
from dual where not exists (select 1 from t_person where id_card = '440106199404040044');

insert into t_person (person_id, enterprise_id, region_code, person_name, id_card, mobile, worker_type, job_type, cert_status, insurance_status, employment_status, entry_date, leave_date, del_flag, create_by, create_time, update_by, update_time, remark)
select 10005, 1002, '440305', '刘海鹏', '440305199105050055', '13700010005', '2', '机修工', '1', '1', '0', '2026-01-18', null, '0', 'admin', sysdate(), '', null, '深圳测试人员'
from dual where not exists (select 1 from t_person where id_card = '440305199105050055');

insert into t_person (person_id, enterprise_id, region_code, person_name, id_card, mobile, worker_type, job_type, cert_status, insurance_status, employment_status, entry_date, leave_date, del_flag, create_by, create_time, update_by, update_time, remark)
select 10006, 1002, '440305', '郑思雨', '440305199206060066', '13700010006', '1', '焊工', '1', '1', '0', '2026-02-03', null, '0', 'admin', sysdate(), '', null, '深圳测试人员'
from dual where not exists (select 1 from t_person where id_card = '440305199206060066');

insert into t_person (person_id, enterprise_id, region_code, person_name, id_card, mobile, worker_type, job_type, cert_status, insurance_status, employment_status, entry_date, leave_date, del_flag, create_by, create_time, update_by, update_time, remark)
select 10007, 1002, '440305', '许嘉诚', '440305199307070077', '13700010007', '1', '叉车工', '2', '0', '0', '2026-02-16', null, '0', 'admin', sysdate(), '', null, '深圳漏保测试人员'
from dual where not exists (select 1 from t_person where id_card = '440305199307070077');

insert into t_person (person_id, enterprise_id, region_code, person_name, id_card, mobile, worker_type, job_type, cert_status, insurance_status, employment_status, entry_date, leave_date, del_flag, create_by, create_time, update_by, update_time, remark)
select 10008, 1002, '440305', '邓雅琪', '440305199408080088', '13700010008', '4', '配送员', '0', '2', '0', '2026-03-01', null, '0', 'admin', sysdate(), '', null, '深圳新业态测试人员'
from dual where not exists (select 1 from t_person where id_card = '440305199408080088');

insert into t_person (person_id, enterprise_id, region_code, person_name, id_card, mobile, worker_type, job_type, cert_status, insurance_status, employment_status, entry_date, leave_date, del_flag, create_by, create_time, update_by, update_time, remark)
select 10009, 1003, '440606', '何俊贤', '440606199109090099', '13700010009', '2', '设备巡检员', '1', '1', '0', '2026-01-09', null, '0', 'admin', sysdate(), '', null, '佛山测试人员'
from dual where not exists (select 1 from t_person where id_card = '440606199109090099');

insert into t_person (person_id, enterprise_id, region_code, person_name, id_card, mobile, worker_type, job_type, cert_status, insurance_status, employment_status, entry_date, leave_date, del_flag, create_by, create_time, update_by, update_time, remark)
select 10010, 1003, '440606', '罗嘉雯', '440606199210101010', '13700010010', '2', '安全员', '1', '1', '0', '2026-01-28', null, '0', 'admin', sysdate(), '', null, '佛山测试人员'
from dual where not exists (select 1 from t_person where id_card = '440606199210101010');

insert into t_person (person_id, enterprise_id, region_code, person_name, id_card, mobile, worker_type, job_type, cert_status, insurance_status, employment_status, entry_date, leave_date, del_flag, create_by, create_time, update_by, update_time, remark)
select 10011, 1003, '440606', '苏志宏', '440606199311111111', '13700010011', '3', '喷涂工', '3', '0', '1', '2025-11-16', '2026-03-12', '0', 'admin', sysdate(), '', null, '佛山离岗测试人员'
from dual where not exists (select 1 from t_person where id_card = '440606199311111111');

insert into t_person (person_id, enterprise_id, region_code, person_name, id_card, mobile, worker_type, job_type, cert_status, insurance_status, employment_status, entry_date, leave_date, del_flag, create_by, create_time, update_by, update_time, remark)
select 10012, 1003, '440606', '彭若琳', '440606199412121212', '13700010012', '1', '电焊工', '2', '1', '0', '2026-03-10', null, '0', 'admin', sysdate(), '', null, '佛山测试人员'
from dual where not exists (select 1 from t_person where id_card = '440606199412121212');
