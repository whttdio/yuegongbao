-- 粤工保广东考勤上报/考勤归集测试数据
-- 使用前提：
-- 1. 已执行 ygb_phase1_enterprise_person.sql
-- 2. 已执行 ygb_gd_test_seed.sql
-- 3. 已执行 ygb_phase1_contract.sql
-- 4. 已执行 ygb_gd_contract_seed.sql
-- 5. 已执行 ygb_phase1_attendance.sql

insert into sys_role_menu (role_id, menu_id)
select 101, 2020 from dual where not exists (select 1 from sys_role_menu where role_id = 101 and menu_id = 2020);
insert into sys_role_menu (role_id, menu_id)
select 101, 2004 from dual where not exists (select 1 from sys_role_menu where role_id = 101 and menu_id = 2004);
insert into sys_role_menu (role_id, menu_id)
select 101, 2400 from dual where not exists (select 1 from sys_role_menu where role_id = 101 and menu_id = 2400);
insert into sys_role_menu (role_id, menu_id)
select 101, 2404 from dual where not exists (select 1 from sys_role_menu where role_id = 101 and menu_id = 2404);
insert into sys_role_menu (role_id, menu_id)
select 101, 2005 from dual where not exists (select 1 from sys_role_menu where role_id = 101 and menu_id = 2005);
insert into sys_role_menu (role_id, menu_id)
select 101, 2500 from dual where not exists (select 1 from sys_role_menu where role_id = 101 and menu_id = 2500);
insert into sys_role_menu (role_id, menu_id)
select 101, 2501 from dual where not exists (select 1 from sys_role_menu where role_id = 101 and menu_id = 2501);

insert into sys_role_menu (role_id, menu_id)
select 102, 2020 from dual where not exists (select 1 from sys_role_menu where role_id = 102 and menu_id = 2020);
insert into sys_role_menu (role_id, menu_id)
select 102, 2004 from dual where not exists (select 1 from sys_role_menu where role_id = 102 and menu_id = 2004);
insert into sys_role_menu (role_id, menu_id)
select 102, 2400 from dual where not exists (select 1 from sys_role_menu where role_id = 102 and menu_id = 2400);
insert into sys_role_menu (role_id, menu_id)
select 102, 2404 from dual where not exists (select 1 from sys_role_menu where role_id = 102 and menu_id = 2404);
insert into sys_role_menu (role_id, menu_id)
select 102, 2005 from dual where not exists (select 1 from sys_role_menu where role_id = 102 and menu_id = 2005);
insert into sys_role_menu (role_id, menu_id)
select 102, 2500 from dual where not exists (select 1 from sys_role_menu where role_id = 102 and menu_id = 2500);
insert into sys_role_menu (role_id, menu_id)
select 102, 2501 from dual where not exists (select 1 from sys_role_menu where role_id = 102 and menu_id = 2501);

insert into sys_role_menu (role_id, menu_id)
select 103, 2020 from dual where not exists (select 1 from sys_role_menu where role_id = 103 and menu_id = 2020);
insert into sys_role_menu (role_id, menu_id)
select 103, 2004 from dual where not exists (select 1 from sys_role_menu where role_id = 103 and menu_id = 2004);
insert into sys_role_menu (role_id, menu_id)
select 103, 2400 from dual where not exists (select 1 from sys_role_menu where role_id = 103 and menu_id = 2400);
insert into sys_role_menu (role_id, menu_id)
select 103, 2404 from dual where not exists (select 1 from sys_role_menu where role_id = 103 and menu_id = 2404);
insert into sys_role_menu (role_id, menu_id)
select 103, 2005 from dual where not exists (select 1 from sys_role_menu where role_id = 103 and menu_id = 2005);
insert into sys_role_menu (role_id, menu_id)
select 103, 2500 from dual where not exists (select 1 from sys_role_menu where role_id = 103 and menu_id = 2500);
insert into sys_role_menu (role_id, menu_id)
select 103, 2501 from dual where not exists (select 1 from sys_role_menu where role_id = 103 and menu_id = 2501);

insert into sys_role_menu (role_id, menu_id)
select 104, 2020 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2020);
insert into sys_role_menu (role_id, menu_id)
select 104, 2004 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2004);
insert into sys_role_menu (role_id, menu_id)
select 104, 2400 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2400);
insert into sys_role_menu (role_id, menu_id)
select 104, 2401 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2401);
insert into sys_role_menu (role_id, menu_id)
select 104, 2402 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2402);
insert into sys_role_menu (role_id, menu_id)
select 104, 2403 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2403);
insert into sys_role_menu (role_id, menu_id)
select 104, 2404 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2404);
insert into sys_role_menu (role_id, menu_id)
select 104, 2005 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2005);
insert into sys_role_menu (role_id, menu_id)
select 104, 2500 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2500);
insert into sys_role_menu (role_id, menu_id)
select 104, 2501 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2501);
insert into sys_role_menu (role_id, menu_id)
select 104, 2502 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2502);

insert into sys_role_menu (role_id, menu_id)
select 105, 2020 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2020);
insert into sys_role_menu (role_id, menu_id)
select 105, 2004 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2004);
insert into sys_role_menu (role_id, menu_id)
select 105, 2400 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2400);
insert into sys_role_menu (role_id, menu_id)
select 105, 2401 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2401);
insert into sys_role_menu (role_id, menu_id)
select 105, 2402 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2402);
insert into sys_role_menu (role_id, menu_id)
select 105, 2404 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2404);
insert into sys_role_menu (role_id, menu_id)
select 105, 2005 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2005);
insert into sys_role_menu (role_id, menu_id)
select 105, 2500 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2500);
insert into sys_role_menu (role_id, menu_id)
select 105, 2501 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2501);

insert into t_attendance_raw (
  attendance_id, attendance_no, contract_id, contract_no,
  dispatch_enterprise_id, dispatch_enterprise_name, employer_enterprise_id, employer_enterprise_name,
  person_id, person_name, id_card, region_code, attendance_date, clock_in_time, clock_out_time,
  shift_name, attendance_hours, overtime_hours, attendance_status, source_type, collect_status, att_check,
  device_code, anomaly_remark, del_flag, create_by, create_time, update_by, update_time, remark
)
select 30001, 'ATT-20260105-0001', 20001, 'YGB-HT-2026-GZ-0001',
       1001, '广州南粤人力资源有限公司', 1002, '深圳鹏城机电工程有限公司',
       10001, '赵志成', '440106199001010011', '440106', '2026-01-05', '2026-01-05 08:28:00', '2026-01-05 18:05:00',
       '白班', 8.00, 0.00, '1', '1', '1', '1',
       'GZ-GATE-01', '', '0', 'admin', sysdate(), '', null, '广州闸机正常打卡'
from dual where not exists (select 1 from t_attendance_raw where attendance_id = 30001);

insert into t_attendance_raw (
  attendance_id, attendance_no, contract_id, contract_no,
  dispatch_enterprise_id, dispatch_enterprise_name, employer_enterprise_id, employer_enterprise_name,
  person_id, person_name, id_card, region_code, attendance_date, clock_in_time, clock_out_time,
  shift_name, attendance_hours, overtime_hours, attendance_status, source_type, collect_status, att_check,
  device_code, anomaly_remark, del_flag, create_by, create_time, update_by, update_time, remark
)
select 30002, 'ATT-20260106-0001', 20001, 'YGB-HT-2026-GZ-0001',
       1001, '广州南粤人力资源有限公司', 1002, '深圳鹏城机电工程有限公司',
       10001, '赵志成', '440106199001010011', '440106', '2026-01-06', '2026-01-06 08:45:00', '2026-01-06 18:06:00',
       '白班', 8.00, 0.00, '2', '2', '1', '1',
       'GZ-FACE-03', '上班迟到15分钟', '0', 'admin', sysdate(), '', null, '广州人脸迟到打卡'
from dual where not exists (select 1 from t_attendance_raw where attendance_id = 30002);

insert into t_attendance_raw (
  attendance_id, attendance_no, contract_id, contract_no,
  dispatch_enterprise_id, dispatch_enterprise_name, employer_enterprise_id, employer_enterprise_name,
  person_id, person_name, id_card, region_code, attendance_date, clock_in_time, clock_out_time,
  shift_name, attendance_hours, overtime_hours, attendance_status, source_type, collect_status, att_check,
  device_code, anomaly_remark, del_flag, create_by, create_time, update_by, update_time, remark
)
select 30003, 'ATT-20260107-0001', 20001, 'YGB-HT-2026-GZ-0001',
       1001, '广州南粤人力资源有限公司', 1002, '深圳鹏城机电工程有限公司',
       10001, '赵志成', '440106199001010011', '440106', '2026-01-07', '2026-01-07 08:30:00', '2026-01-07 20:35:00',
       '白班', 10.00, 2.00, '4', '1', '1', '1',
       'GZ-GATE-01', '', '0', 'admin', sysdate(), '', null, '广州加班考勤'
from dual where not exists (select 1 from t_attendance_raw where attendance_id = 30003);

insert into t_attendance_raw (
  attendance_id, attendance_no, contract_id, contract_no,
  dispatch_enterprise_id, dispatch_enterprise_name, employer_enterprise_id, employer_enterprise_name,
  person_id, person_name, id_card, region_code, attendance_date, clock_in_time, clock_out_time,
  shift_name, attendance_hours, overtime_hours, attendance_status, source_type, collect_status, att_check,
  device_code, anomaly_remark, del_flag, create_by, create_time, update_by, update_time, remark
)
select 30004, 'ATT-20260112-0002', 20002, 'YGB-HT-2026-GZ-0002',
       1001, '广州南粤人力资源有限公司', 1002, '深圳鹏城机电工程有限公司',
       10002, '梁宇峰', '440106199202020022', '440106', '2026-01-12', '2026-01-12 08:31:00', '2026-01-12 18:02:00',
       '白班', 8.00, 0.00, '1', '3', '1', '0',
       'APP-GZ-01', '', '0', 'admin', sysdate(), '', null, '广州APP补录考勤'
from dual where not exists (select 1 from t_attendance_raw where attendance_id = 30004);

insert into t_attendance_raw (
  attendance_id, attendance_no, contract_id, contract_no,
  dispatch_enterprise_id, dispatch_enterprise_name, employer_enterprise_id, employer_enterprise_name,
  person_id, person_name, id_card, region_code, attendance_date, clock_in_time, clock_out_time,
  shift_name, attendance_hours, overtime_hours, attendance_status, source_type, collect_status, att_check,
  device_code, anomaly_remark, del_flag, create_by, create_time, update_by, update_time, remark
)
select 30005, 'ATT-20260113-0002', 20002, 'YGB-HT-2026-GZ-0002',
       1001, '广州南粤人力资源有限公司', 1002, '深圳鹏城机电工程有限公司',
       10002, '梁宇峰', '440106199202020022', '440106', '2026-01-13', null, null,
       '白班', 0.00, 0.00, '0', '4', '1', '0',
       '', '当日缺勤，工资核验不通过', '0', 'admin', sysdate(), '', null, '广州缺勤记录'
from dual where not exists (select 1 from t_attendance_raw where attendance_id = 30005);

insert into t_attendance_raw (
  attendance_id, attendance_no, contract_id, contract_no,
  dispatch_enterprise_id, dispatch_enterprise_name, employer_enterprise_id, employer_enterprise_name,
  person_id, person_name, id_card, region_code, attendance_date, clock_in_time, clock_out_time,
  shift_name, attendance_hours, overtime_hours, attendance_status, source_type, collect_status, att_check,
  device_code, anomaly_remark, del_flag, create_by, create_time, update_by, update_time, remark
)
select 30006, 'ATT-20260203-0003', 20003, 'YGB-HT-2026-SZ-0001',
       1001, '广州南粤人力资源有限公司', 1002, '深圳鹏城机电工程有限公司',
       10006, '郑思雨', '440305199206060066', '440305', '2026-02-03', '2026-02-03 08:18:00', '2026-02-03 18:02:00',
       '白班', 8.00, 0.00, '1', '1', '1', '1',
       'SZ-GATE-02', '', '0', 'admin', sysdate(), '', null, '深圳闸机正常打卡'
from dual where not exists (select 1 from t_attendance_raw where attendance_id = 30006);

insert into t_attendance_raw (
  attendance_id, attendance_no, contract_id, contract_no,
  dispatch_enterprise_id, dispatch_enterprise_name, employer_enterprise_id, employer_enterprise_name,
  person_id, person_name, id_card, region_code, attendance_date, clock_in_time, clock_out_time,
  shift_name, attendance_hours, overtime_hours, attendance_status, source_type, collect_status, att_check,
  device_code, anomaly_remark, del_flag, create_by, create_time, update_by, update_time, remark
)
select 30007, 'ATT-20260204-0003', 20003, 'YGB-HT-2026-SZ-0001',
       1001, '广州南粤人力资源有限公司', 1002, '深圳鹏城机电工程有限公司',
       10006, '郑思雨', '440305199206060066', '440305', '2026-02-04', '2026-02-04 08:20:00', '2026-02-04 19:25:00',
       '白班', 9.00, 1.00, '4', '2', '1', '1',
       'SZ-FACE-02', '', '0', 'admin', sysdate(), '', null, '深圳加班考勤'
from dual where not exists (select 1 from t_attendance_raw where attendance_id = 30007);

insert into t_attendance_raw (
  attendance_id, attendance_no, contract_id, contract_no,
  dispatch_enterprise_id, dispatch_enterprise_name, employer_enterprise_id, employer_enterprise_name,
  person_id, person_name, id_card, region_code, attendance_date, clock_in_time, clock_out_time,
  shift_name, attendance_hours, overtime_hours, attendance_status, source_type, collect_status, att_check,
  device_code, anomaly_remark, del_flag, create_by, create_time, update_by, update_time, remark
)
select 30008, 'ATT-20260216-0004', 20004, 'YGB-HT-2026-SZ-0002',
       1001, '广州南粤人力资源有限公司', 1002, '深圳鹏城机电工程有限公司',
       10007, '许嘉诚', '440305199307070077', '440305', '2026-02-16', '2026-02-16 08:34:00', '2026-02-16 17:20:00',
       '白班', 7.50, 0.00, '3', '5', '1', '0',
       'SZ-THIRD-09', '第三方回传存在早退', '0', 'admin', sysdate(), '', null, '深圳第三方早退记录'
from dual where not exists (select 1 from t_attendance_raw where attendance_id = 30008);

insert into t_attendance_raw (
  attendance_id, attendance_no, contract_id, contract_no,
  dispatch_enterprise_id, dispatch_enterprise_name, employer_enterprise_id, employer_enterprise_name,
  person_id, person_name, id_card, region_code, attendance_date, clock_in_time, clock_out_time,
  shift_name, attendance_hours, overtime_hours, attendance_status, source_type, collect_status, att_check,
  device_code, anomaly_remark, del_flag, create_by, create_time, update_by, update_time, remark
)
select 30009, 'ATT-20260217-0004', 20004, 'YGB-HT-2026-SZ-0002',
       1001, '广州南粤人力资源有限公司', 1002, '深圳鹏城机电工程有限公司',
       10007, '许嘉诚', '440305199307070077', '440305', '2026-02-17', '2026-02-17 08:29:00', '2026-02-17 18:01:00',
       '白班', 8.00, 0.00, '1', '5', '1', '0',
       'SZ-THIRD-09', '', '0', 'admin', sysdate(), '', null, '深圳第三方正常记录'
from dual where not exists (select 1 from t_attendance_raw where attendance_id = 30009);

insert into t_attendance_raw (
  attendance_id, attendance_no, contract_id, contract_no,
  dispatch_enterprise_id, dispatch_enterprise_name, employer_enterprise_id, employer_enterprise_name,
  person_id, person_name, id_card, region_code, attendance_date, clock_in_time, clock_out_time,
  shift_name, attendance_hours, overtime_hours, attendance_status, source_type, collect_status, att_check,
  device_code, anomaly_remark, del_flag, create_by, create_time, update_by, update_time, remark
)
select 30010, 'ATT-20250310-0005', 20005, 'YGB-HT-2025-FS-0001',
       1001, '广州南粤人力资源有限公司', 1003, '佛山顺德智造服务有限公司',
       10012, '彭若琳', '440606199412121212', '440606', '2025-03-10', '2025-03-10 08:26:00', '2025-03-10 18:00:00',
       '白班', 8.00, 0.00, '1', '1', '1', '1',
       'FS-GATE-05', '', '0', 'admin', sysdate(), '', null, '佛山正常打卡'
from dual where not exists (select 1 from t_attendance_raw where attendance_id = 30010);

insert into t_attendance_raw (
  attendance_id, attendance_no, contract_id, contract_no,
  dispatch_enterprise_id, dispatch_enterprise_name, employer_enterprise_id, employer_enterprise_name,
  person_id, person_name, id_card, region_code, attendance_date, clock_in_time, clock_out_time,
  shift_name, attendance_hours, overtime_hours, attendance_status, source_type, collect_status, att_check,
  device_code, anomaly_remark, del_flag, create_by, create_time, update_by, update_time, remark
)
select 30011, 'ATT-20260601-0001', 20001, 'YGB-HT-2026-GZ-0001',
       1001, '广州南粤人力资源有限公司', 1002, '深圳鹏城机电工程有限公司',
       10001, '赵志成', '440106199001010011', '440106', '2026-06-01', '2026-06-01 08:27:00', '2026-06-01 18:03:00',
       '白班', 8.00, 0.00, '1', '1', '0', '1',
       'GZ-GATE-01', '', '0', 'admin', sysdate(), '', null, '待归集测试数据'
from dual where not exists (select 1 from t_attendance_raw where attendance_id = 30011);

insert into t_attendance_raw (
  attendance_id, attendance_no, contract_id, contract_no,
  dispatch_enterprise_id, dispatch_enterprise_name, employer_enterprise_id, employer_enterprise_name,
  person_id, person_name, id_card, region_code, attendance_date, clock_in_time, clock_out_time,
  shift_name, attendance_hours, overtime_hours, attendance_status, source_type, collect_status, att_check,
  device_code, anomaly_remark, del_flag, create_by, create_time, update_by, update_time, remark
)
select 30012, 'ATT-20260601-0003', 20003, 'YGB-HT-2026-SZ-0001',
       1001, '广州南粤人力资源有限公司', 1002, '深圳鹏城机电工程有限公司',
       10006, '郑思雨', '440305199206060066', '440305', '2026-06-01', '2026-06-01 08:20:00', '2026-06-01 18:06:00',
       '白班', 8.00, 0.00, '1', '2', '0', '1',
       'SZ-FACE-02', '', '0', 'admin', sysdate(), '', null, '待归集深圳测试数据'
from dual where not exists (select 1 from t_attendance_raw where attendance_id = 30012);

insert into t_attendance_monthly (
  monthly_id, stat_month, contract_id, contract_no,
  dispatch_enterprise_id, dispatch_enterprise_name, employer_enterprise_id, employer_enterprise_name,
  person_id, person_name, id_card, region_code, attendance_days, absence_days, late_days, early_leave_days,
  overtime_hours, total_hours, att_check, summary_status, last_attendance_date,
  del_flag, create_by, create_time, update_by, update_time, remark
)
select 40001, '2026-01', 20001, 'YGB-HT-2026-GZ-0001',
       1001, '广州南粤人力资源有限公司', 1002, '深圳鹏城机电工程有限公司',
       10001, '赵志成', '440106199001010011', '440106', 3, 0, 1, 0,
       2.00, 26.00, '1', '3', '2026-01-07',
       '0', 'admin', sysdate(), '', null, '广州1月已核验归集'
from dual where not exists (select 1 from t_attendance_monthly where monthly_id = 40001);

insert into t_attendance_monthly (
  monthly_id, stat_month, contract_id, contract_no,
  dispatch_enterprise_id, dispatch_enterprise_name, employer_enterprise_id, employer_enterprise_name,
  person_id, person_name, id_card, region_code, attendance_days, absence_days, late_days, early_leave_days,
  overtime_hours, total_hours, att_check, summary_status, last_attendance_date,
  del_flag, create_by, create_time, update_by, update_time, remark
)
select 40002, '2026-01', 20002, 'YGB-HT-2026-GZ-0002',
       1001, '广州南粤人力资源有限公司', 1002, '深圳鹏城机电工程有限公司',
       10002, '梁宇峰', '440106199202020022', '440106', 1, 1, 0, 0,
       0.00, 8.00, '0', '2', '2026-01-13',
       '0', 'admin', sysdate(), '', null, '广州1月待核验归集'
from dual where not exists (select 1 from t_attendance_monthly where monthly_id = 40002);

insert into t_attendance_monthly (
  monthly_id, stat_month, contract_id, contract_no,
  dispatch_enterprise_id, dispatch_enterprise_name, employer_enterprise_id, employer_enterprise_name,
  person_id, person_name, id_card, region_code, attendance_days, absence_days, late_days, early_leave_days,
  overtime_hours, total_hours, att_check, summary_status, last_attendance_date,
  del_flag, create_by, create_time, update_by, update_time, remark
)
select 40003, '2026-02', 20003, 'YGB-HT-2026-SZ-0001',
       1001, '广州南粤人力资源有限公司', 1002, '深圳鹏城机电工程有限公司',
       10006, '郑思雨', '440305199206060066', '440305', 2, 0, 0, 0,
       1.00, 17.00, '1', '4', '2026-02-04',
       '0', 'admin', sysdate(), '', null, '深圳2月已发薪归集'
from dual where not exists (select 1 from t_attendance_monthly where monthly_id = 40003);

insert into t_attendance_monthly (
  monthly_id, stat_month, contract_id, contract_no,
  dispatch_enterprise_id, dispatch_enterprise_name, employer_enterprise_id, employer_enterprise_name,
  person_id, person_name, id_card, region_code, attendance_days, absence_days, late_days, early_leave_days,
  overtime_hours, total_hours, att_check, summary_status, last_attendance_date,
  del_flag, create_by, create_time, update_by, update_time, remark
)
select 40004, '2026-02', 20004, 'YGB-HT-2026-SZ-0002',
       1001, '广州南粤人力资源有限公司', 1002, '深圳鹏城机电工程有限公司',
       10007, '许嘉诚', '440305199307070077', '440305', 2, 0, 0, 1,
       0.00, 15.50, '0', '2', '2026-02-17',
       '0', 'admin', sysdate(), '', null, '深圳2月待核验归集'
from dual where not exists (select 1 from t_attendance_monthly where monthly_id = 40004);

insert into t_attendance_monthly (
  monthly_id, stat_month, contract_id, contract_no,
  dispatch_enterprise_id, dispatch_enterprise_name, employer_enterprise_id, employer_enterprise_name,
  person_id, person_name, id_card, region_code, attendance_days, absence_days, late_days, early_leave_days,
  overtime_hours, total_hours, att_check, summary_status, last_attendance_date,
  del_flag, create_by, create_time, update_by, update_time, remark
)
select 40005, '2025-03', 20005, 'YGB-HT-2025-FS-0001',
       1001, '广州南粤人力资源有限公司', 1003, '佛山顺德智造服务有限公司',
       10012, '彭若琳', '440606199412121212', '440606', 1, 0, 0, 0,
       0.00, 8.00, '1', '4', '2025-03-10',
       '0', 'admin', sysdate(), '', null, '佛山3月已发薪归集'
from dual where not exists (select 1 from t_attendance_monthly where monthly_id = 40005);
