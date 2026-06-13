-- 粤工保广东工资发放/工资核验测试数据
-- 使用前提：
-- 1. 已执行 ygb_phase1_enterprise_person.sql
-- 2. 已执行 ygb_gd_test_seed.sql
-- 3. 已执行 ygb_phase1_contract.sql
-- 4. 已执行 ygb_gd_contract_seed.sql
-- 5. 已执行 ygb_phase1_attendance.sql
-- 6. 已执行 ygb_gd_attendance_seed.sql
-- 7. 已执行 ygb_phase1_salary.sql

insert into sys_role_menu (role_id, menu_id)
select 101, 2020 from dual where not exists (select 1 from sys_role_menu where role_id = 101 and menu_id = 2020);
insert into sys_role_menu (role_id, menu_id)
select 101, 2006 from dual where not exists (select 1 from sys_role_menu where role_id = 101 and menu_id = 2006);
insert into sys_role_menu (role_id, menu_id)
select 101, 2600 from dual where not exists (select 1 from sys_role_menu where role_id = 101 and menu_id = 2600);
insert into sys_role_menu (role_id, menu_id)
select 101, 2604 from dual where not exists (select 1 from sys_role_menu where role_id = 101 and menu_id = 2604);
insert into sys_role_menu (role_id, menu_id)
select 101, 2007 from dual where not exists (select 1 from sys_role_menu where role_id = 101 and menu_id = 2007);
insert into sys_role_menu (role_id, menu_id)
select 101, 2700 from dual where not exists (select 1 from sys_role_menu where role_id = 101 and menu_id = 2700);
insert into sys_role_menu (role_id, menu_id)
select 101, 2703 from dual where not exists (select 1 from sys_role_menu where role_id = 101 and menu_id = 2703);

insert into sys_role_menu (role_id, menu_id)
select 102, 2020 from dual where not exists (select 1 from sys_role_menu where role_id = 102 and menu_id = 2020);
insert into sys_role_menu (role_id, menu_id)
select 102, 2006 from dual where not exists (select 1 from sys_role_menu where role_id = 102 and menu_id = 2006);
insert into sys_role_menu (role_id, menu_id)
select 102, 2600 from dual where not exists (select 1 from sys_role_menu where role_id = 102 and menu_id = 2600);
insert into sys_role_menu (role_id, menu_id)
select 102, 2604 from dual where not exists (select 1 from sys_role_menu where role_id = 102 and menu_id = 2604);
insert into sys_role_menu (role_id, menu_id)
select 102, 2007 from dual where not exists (select 1 from sys_role_menu where role_id = 102 and menu_id = 2007);
insert into sys_role_menu (role_id, menu_id)
select 102, 2700 from dual where not exists (select 1 from sys_role_menu where role_id = 102 and menu_id = 2700);
insert into sys_role_menu (role_id, menu_id)
select 102, 2703 from dual where not exists (select 1 from sys_role_menu where role_id = 102 and menu_id = 2703);

insert into sys_role_menu (role_id, menu_id)
select 103, 2020 from dual where not exists (select 1 from sys_role_menu where role_id = 103 and menu_id = 2020);
insert into sys_role_menu (role_id, menu_id)
select 103, 2006 from dual where not exists (select 1 from sys_role_menu where role_id = 103 and menu_id = 2006);
insert into sys_role_menu (role_id, menu_id)
select 103, 2600 from dual where not exists (select 1 from sys_role_menu where role_id = 103 and menu_id = 2600);
insert into sys_role_menu (role_id, menu_id)
select 103, 2604 from dual where not exists (select 1 from sys_role_menu where role_id = 103 and menu_id = 2604);
insert into sys_role_menu (role_id, menu_id)
select 103, 2007 from dual where not exists (select 1 from sys_role_menu where role_id = 103 and menu_id = 2007);
insert into sys_role_menu (role_id, menu_id)
select 103, 2700 from dual where not exists (select 1 from sys_role_menu where role_id = 103 and menu_id = 2700);
insert into sys_role_menu (role_id, menu_id)
select 103, 2703 from dual where not exists (select 1 from sys_role_menu where role_id = 103 and menu_id = 2703);

insert into sys_role_menu (role_id, menu_id)
select 104, 2020 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2020);
insert into sys_role_menu (role_id, menu_id)
select 104, 2006 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2006);
insert into sys_role_menu (role_id, menu_id)
select 104, 2600 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2600);
insert into sys_role_menu (role_id, menu_id)
select 104, 2601 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2601);
insert into sys_role_menu (role_id, menu_id)
select 104, 2602 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2602);
insert into sys_role_menu (role_id, menu_id)
select 104, 2604 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2604);
insert into sys_role_menu (role_id, menu_id)
select 104, 2605 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2605);
insert into sys_role_menu (role_id, menu_id)
select 104, 2606 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2606);
insert into sys_role_menu (role_id, menu_id)
select 104, 2607 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2607);
insert into sys_role_menu (role_id, menu_id)
select 104, 2007 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2007);
insert into sys_role_menu (role_id, menu_id)
select 104, 2700 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2700);
insert into sys_role_menu (role_id, menu_id)
select 104, 2701 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2701);
insert into sys_role_menu (role_id, menu_id)
select 104, 2702 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2702);
insert into sys_role_menu (role_id, menu_id)
select 104, 2703 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2703);

insert into sys_role_menu (role_id, menu_id)
select 105, 2020 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2020);
insert into sys_role_menu (role_id, menu_id)
select 105, 2006 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2006);
insert into sys_role_menu (role_id, menu_id)
select 105, 2600 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2600);
insert into sys_role_menu (role_id, menu_id)
select 105, 2601 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2601);
insert into sys_role_menu (role_id, menu_id)
select 105, 2602 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2602);
insert into sys_role_menu (role_id, menu_id)
select 105, 2604 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2604);
insert into sys_role_menu (role_id, menu_id)
select 105, 2605 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2605);
insert into sys_role_menu (role_id, menu_id)
select 105, 2007 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2007);
insert into sys_role_menu (role_id, menu_id)
select 105, 2700 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2700);
insert into sys_role_menu (role_id, menu_id)
select 105, 2701 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2701);
insert into sys_role_menu (role_id, menu_id)
select 105, 2703 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2703);

insert into t_salary_batch (
  batch_id, batch_no, stat_month, dispatch_enterprise_id, dispatch_enterprise_name, region_code,
  total_person_count, total_payable_amount, total_paid_amount, account_received_amount,
  batch_status, account_status, regulator_account_name, regulator_account_no, bank_serial_no,
  submit_time, paid_time, del_flag, create_by, create_time, update_by, update_time, remark
)
select 50001, 'PAY-2026-01-GZ-01', '2026-01', 1001, '广州南粤人力资源有限公司', '440106',
       2, 17868.97, 9497.13, 20000.00,
       '6', '1', '粤工保广州监管账户', '6214830000001001', 'BK2026010001',
       '2026-01-31 16:00:00', '2026-01-31 18:00:00', '0', 'admin', sysdate(), '', null, '广州1月工资已发放批次'
from dual where not exists (select 1 from t_salary_batch where batch_id = 50001);

insert into t_salary_batch (
  batch_id, batch_no, stat_month, dispatch_enterprise_id, dispatch_enterprise_name, region_code,
  total_person_count, total_payable_amount, total_paid_amount, account_received_amount,
  batch_status, account_status, regulator_account_name, regulator_account_no, bank_serial_no,
  submit_time, paid_time, del_flag, create_by, create_time, update_by, update_time, remark
)
select 50002, 'PAY-2026-02-SZ-01', '2026-02', 1001, '广州南粤人力资源有限公司', '440305',
       2, 18936.21, 11200.00, 15000.00,
       '4', '1', '粤工保深圳监管账户', '6214830000001002', 'BK2026020008',
       null, null, '0', 'admin', sysdate(), '', null, '深圳2月待代发批次'
from dual where not exists (select 1 from t_salary_batch where batch_id = 50002);

insert into t_salary_batch (
  batch_id, batch_no, stat_month, dispatch_enterprise_id, dispatch_enterprise_name, region_code,
  total_person_count, total_payable_amount, total_paid_amount, account_received_amount,
  batch_status, account_status, regulator_account_name, regulator_account_no, bank_serial_no,
  submit_time, paid_time, del_flag, create_by, create_time, update_by, update_time, remark
)
select 50003, 'PAY-2026-06-GD-01', '2026-06', 1001, '广州南粤人力资源有限公司', '440106',
       0, 0.00, 0.00, 0.00,
       '1', '0', '粤工保广东监管账户', '6214830000001003', '',
       null, null, '0', 'admin', sysdate(), '', null, '待生成工资明细测试批次'
from dual where not exists (select 1 from t_salary_batch where batch_id = 50003);

insert into t_salary_detail (
  detail_id, batch_id, batch_no, stat_month, monthly_id, contract_id, contract_no,
  dispatch_enterprise_id, dispatch_enterprise_name, employer_enterprise_id, employer_enterprise_name,
  person_id, person_name, id_card, region_code, attendance_days, total_hours, att_check,
  payable_amount, deduction_amount, net_amount, bank_account_name, bank_account_no, pay_status, fail_reason,
  del_flag, create_by, create_time, update_by, update_time, remark
)
select 60001, 50001, 'PAY-2026-01-GZ-01', '2026-01', 40001, 20001, 'YGB-HT-2026-GZ-0001',
       1001, '广州南粤人力资源有限公司', 1002, '深圳鹏城机电工程有限公司',
       10001, '赵志成', '440106199001010011', '440106', 3, 26.00, '1',
       1466.67, 0.00, 1466.67, '赵志成', '622202199001010011', '2', '',
       '0', 'admin', sysdate(), '', null, '广州1月工资明细'
from dual where not exists (select 1 from t_salary_detail where detail_id = 60001);

insert into t_salary_detail (
  detail_id, batch_id, batch_no, stat_month, monthly_id, contract_id, contract_no,
  dispatch_enterprise_id, dispatch_enterprise_name, employer_enterprise_id, employer_enterprise_name,
  person_id, person_name, id_card, region_code, attendance_days, total_hours, att_check,
  payable_amount, deduction_amount, net_amount, bank_account_name, bank_account_no, pay_status, fail_reason,
  del_flag, create_by, create_time, update_by, update_time, remark
)
select 60002, 50001, 'PAY-2026-01-GZ-01', '2026-01', 40002, 20002, 'YGB-HT-2026-GZ-0002',
       1001, '广州南粤人力资源有限公司', 1002, '深圳鹏城机电工程有限公司',
       10002, '梁宇峰', '440106199202020022', '440106', 1, 8.00, '0',
       482.76, 482.76, 0.00, '梁宇峰', '622202199202020022', '3', '考勤核验未通过，禁止代发',
       '0', 'admin', sysdate(), '', null, '广州1月核验失败工资明细'
from dual where not exists (select 1 from t_salary_detail where detail_id = 60002);

insert into t_salary_detail (
  detail_id, batch_id, batch_no, stat_month, monthly_id, contract_id, contract_no,
  dispatch_enterprise_id, dispatch_enterprise_name, employer_enterprise_id, employer_enterprise_name,
  person_id, person_name, id_card, region_code, attendance_days, total_hours, att_check,
  payable_amount, deduction_amount, net_amount, bank_account_name, bank_account_no, pay_status, fail_reason,
  del_flag, create_by, create_time, update_by, update_time, remark
)
select 60003, 50002, 'PAY-2026-02-SZ-01', '2026-02', 40003, 20003, 'YGB-HT-2026-SZ-0001',
       1001, '广州南粤人力资源有限公司', 1002, '深圳鹏城机电工程有限公司',
       10006, '郑思雨', '440305199206060066', '440305', 2, 17.00, '1',
       1094.25, 0.00, 1094.25, '郑思雨', '622202199206060066', '0', '',
       '0', 'admin', sysdate(), '', null, '深圳2月待发放工资明细'
from dual where not exists (select 1 from t_salary_detail where detail_id = 60003);

insert into t_salary_detail (
  detail_id, batch_id, batch_no, stat_month, monthly_id, contract_id, contract_no,
  dispatch_enterprise_id, dispatch_enterprise_name, employer_enterprise_id, employer_enterprise_name,
  person_id, person_name, id_card, region_code, attendance_days, total_hours, att_check,
  payable_amount, deduction_amount, net_amount, bank_account_name, bank_account_no, pay_status, fail_reason,
  del_flag, create_by, create_time, update_by, update_time, remark
)
select 60004, 50002, 'PAY-2026-02-SZ-01', '2026-02', 40004, 20004, 'YGB-HT-2026-SZ-0002',
       1001, '广州南粤人力资源有限公司', 1002, '深圳鹏城机电工程有限公司',
       10007, '许嘉诚', '440305199307070077', '440305', 2, 15.50, '0',
       792.24, 792.24, 0.00, '许嘉诚', '622202199307070077', '3', '考勤核验未通过，禁止代发',
       '0', 'admin', sysdate(), '', null, '深圳2月核验失败工资明细'
from dual where not exists (select 1 from t_salary_detail where detail_id = 60004);
