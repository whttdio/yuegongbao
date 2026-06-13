-- 粤工保广东合同备案测试数据
-- 使用前提：
-- 1. 已执行 ygb_phase1_enterprise_person.sql
-- 2. 已执行 ygb_gd_test_seed.sql
-- 3. 已执行 ygb_phase1_contract.sql

insert into sys_role_menu (role_id, menu_id)
select 101, 2020 from dual where not exists (select 1 from sys_role_menu where role_id = 101 and menu_id = 2020);
insert into sys_role_menu (role_id, menu_id)
select 101, 2003 from dual where not exists (select 1 from sys_role_menu where role_id = 101 and menu_id = 2003);
insert into sys_role_menu (role_id, menu_id)
select 101, 2300 from dual where not exists (select 1 from sys_role_menu where role_id = 101 and menu_id = 2300);
insert into sys_role_menu (role_id, menu_id)
select 101, 2304 from dual where not exists (select 1 from sys_role_menu where role_id = 101 and menu_id = 2304);

insert into sys_role_menu (role_id, menu_id)
select 102, 2020 from dual where not exists (select 1 from sys_role_menu where role_id = 102 and menu_id = 2020);
insert into sys_role_menu (role_id, menu_id)
select 102, 2003 from dual where not exists (select 1 from sys_role_menu where role_id = 102 and menu_id = 2003);
insert into sys_role_menu (role_id, menu_id)
select 102, 2300 from dual where not exists (select 1 from sys_role_menu where role_id = 102 and menu_id = 2300);
insert into sys_role_menu (role_id, menu_id)
select 102, 2304 from dual where not exists (select 1 from sys_role_menu where role_id = 102 and menu_id = 2304);

insert into sys_role_menu (role_id, menu_id)
select 103, 2020 from dual where not exists (select 1 from sys_role_menu where role_id = 103 and menu_id = 2020);
insert into sys_role_menu (role_id, menu_id)
select 103, 2003 from dual where not exists (select 1 from sys_role_menu where role_id = 103 and menu_id = 2003);
insert into sys_role_menu (role_id, menu_id)
select 103, 2300 from dual where not exists (select 1 from sys_role_menu where role_id = 103 and menu_id = 2300);
insert into sys_role_menu (role_id, menu_id)
select 103, 2304 from dual where not exists (select 1 from sys_role_menu where role_id = 103 and menu_id = 2304);

insert into sys_role_menu (role_id, menu_id)
select 104, 2020 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2020);
insert into sys_role_menu (role_id, menu_id)
select 104, 2003 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2003);
insert into sys_role_menu (role_id, menu_id)
select 104, 2300 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2300);
insert into sys_role_menu (role_id, menu_id)
select 104, 2301 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2301);
insert into sys_role_menu (role_id, menu_id)
select 104, 2302 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2302);
insert into sys_role_menu (role_id, menu_id)
select 104, 2303 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2303);
insert into sys_role_menu (role_id, menu_id)
select 104, 2304 from dual where not exists (select 1 from sys_role_menu where role_id = 104 and menu_id = 2304);

insert into sys_role_menu (role_id, menu_id)
select 105, 2020 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2020);
insert into sys_role_menu (role_id, menu_id)
select 105, 2003 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2003);
insert into sys_role_menu (role_id, menu_id)
select 105, 2300 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2300);
insert into sys_role_menu (role_id, menu_id)
select 105, 2301 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2301);
insert into sys_role_menu (role_id, menu_id)
select 105, 2302 from dual where not exists (select 1 from sys_role_menu where role_id = 105 and menu_id = 2302);

insert into t_labor_contract (
  contract_id, contract_no, dispatch_enterprise_id, dispatch_enterprise_name,
  employer_enterprise_id, employer_enterprise_name, person_id, person_name,
  id_card, region_code, contract_type, contract_status, sign_date, start_date, end_date,
  monthly_wage, filing_no, filing_time, ocr_status, clause_check_status,
  blockchain_hash, contract_file_url, del_flag, create_by, create_time, update_by, update_time, remark
)
select 20001, 'YGB-HT-2026-GZ-0001', 1001, '广州南粤人力资源有限公司',
       1002, '深圳鹏城机电工程有限公司', 10001, '赵志成',
       '440106199001010011', '440106', '1', '2', '2026-01-03', '2026-01-05', '2026-12-31',
       9800.00, 'BA-440106-20260001', '2026-01-04 10:30:00', '1', '1',
       '0xgzygbcontract0001', 'https://oss.yuegongbao.local/contract/YGB-HT-2026-GZ-0001.pdf', '0', 'admin', sysdate(), '', null, '广州已备案合同'
from dual where not exists (select 1 from t_labor_contract where contract_no = 'YGB-HT-2026-GZ-0001');

insert into t_labor_contract (
  contract_id, contract_no, dispatch_enterprise_id, dispatch_enterprise_name,
  employer_enterprise_id, employer_enterprise_name, person_id, person_name,
  id_card, region_code, contract_type, contract_status, sign_date, start_date, end_date,
  monthly_wage, filing_no, filing_time, ocr_status, clause_check_status,
  blockchain_hash, contract_file_url, del_flag, create_by, create_time, update_by, update_time, remark
)
select 20002, 'YGB-HT-2026-GZ-0002', 1001, '广州南粤人力资源有限公司',
       1002, '深圳鹏城机电工程有限公司', 10002, '梁宇峰',
       '440106199202020022', '440106', '1', '1', '2026-01-10', '2026-01-12', '2026-12-31',
       10500.00, '', null, '1', '1',
       '0xgzygbcontract0002', 'https://oss.yuegongbao.local/contract/YGB-HT-2026-GZ-0002.pdf', '0', 'admin', sysdate(), '', null, '广州待备案合同'
from dual where not exists (select 1 from t_labor_contract where contract_no = 'YGB-HT-2026-GZ-0002');

insert into t_labor_contract (
  contract_id, contract_no, dispatch_enterprise_id, dispatch_enterprise_name,
  employer_enterprise_id, employer_enterprise_name, person_id, person_name,
  id_card, region_code, contract_type, contract_status, sign_date, start_date, end_date,
  monthly_wage, filing_no, filing_time, ocr_status, clause_check_status,
  blockchain_hash, contract_file_url, del_flag, create_by, create_time, update_by, update_time, remark
)
select 20003, 'YGB-HT-2026-SZ-0001', 1001, '广州南粤人力资源有限公司',
       1002, '深圳鹏城机电工程有限公司', 10006, '郑思雨',
       '440305199206060066', '440305', '1', '2', '2026-02-01', '2026-02-03', '2027-02-02',
       11200.00, 'BA-440305-20260008', '2026-02-02 14:20:00', '1', '1',
       '0xszygbcontract0001', 'https://oss.yuegongbao.local/contract/YGB-HT-2026-SZ-0001.pdf', '0', 'admin', sysdate(), '', null, '深圳已备案合同'
from dual where not exists (select 1 from t_labor_contract where contract_no = 'YGB-HT-2026-SZ-0001');

insert into t_labor_contract (
  contract_id, contract_no, dispatch_enterprise_id, dispatch_enterprise_name,
  employer_enterprise_id, employer_enterprise_name, person_id, person_name,
  id_card, region_code, contract_type, contract_status, sign_date, start_date, end_date,
  monthly_wage, filing_no, filing_time, ocr_status, clause_check_status,
  blockchain_hash, contract_file_url, del_flag, create_by, create_time, update_by, update_time, remark
)
select 20004, 'YGB-HT-2026-SZ-0002', 1001, '广州南粤人力资源有限公司',
       1002, '深圳鹏城机电工程有限公司', 10007, '许嘉诚',
       '440305199307070077', '440305', '1', '3', '2026-02-10', '2026-02-16', '2026-12-31',
       8900.00, '', null, '2', '2',
       '', 'https://oss.yuegongbao.local/contract/YGB-HT-2026-SZ-0002.pdf', '0', 'admin', sysdate(), '', null, '深圳驳回合同，OCR失败且条款缺失'
from dual where not exists (select 1 from t_labor_contract where contract_no = 'YGB-HT-2026-SZ-0002');

insert into t_labor_contract (
  contract_id, contract_no, dispatch_enterprise_id, dispatch_enterprise_name,
  employer_enterprise_id, employer_enterprise_name, person_id, person_name,
  id_card, region_code, contract_type, contract_status, sign_date, start_date, end_date,
  monthly_wage, filing_no, filing_time, ocr_status, clause_check_status,
  blockchain_hash, contract_file_url, del_flag, create_by, create_time, update_by, update_time, remark
)
select 20005, 'YGB-HT-2025-FS-0001', 1001, '广州南粤人力资源有限公司',
       1003, '佛山顺德智造服务有限公司', 10012, '彭若琳',
       '440606199412121212', '440606', '2', '4', '2025-03-01', '2025-03-10', '2026-03-09',
       9300.00, 'BA-440606-20250012', '2025-03-08 09:00:00', '1', '1',
       '0xfsygbcontract0001', 'https://oss.yuegongbao.local/contract/YGB-HT-2025-FS-0001.pdf', '0', 'admin', sysdate(), '', null, '佛山已到期合同'
from dual where not exists (select 1 from t_labor_contract where contract_no = 'YGB-HT-2025-FS-0001');
