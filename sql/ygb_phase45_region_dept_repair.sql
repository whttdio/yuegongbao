set names utf8mb4;

-- 修复市级监管部门 region_code 误设为区县码，导致企业下拉/人员范围过窄
update sys_dept set region_code = '440100' where dept_id = 111;
update sys_dept set region_code = '440300' where dept_id = 112;
update sys_dept set region_code = '440600' where dept_id = 113;

-- 补全广州区域测试企业（status=0 才会出现在下拉选项）
insert into t_enterprise (enterprise_id, enterprise_name, enterprise_code, region_code, enterprise_type, legal_person, contact_person, contact_phone, address, established_date, sync_status, status, del_flag, create_by, create_time, update_by, update_time, remark)
select 1004, '广州天河建设集团有限公司', '91440106TEST00004', '440106', '2', '林国栋', '张慧敏', '13811110004', '广东省广州市天河区黄埔大道西88号', '2017-05-20', '1', '0', '0', 'admin', sysdate(), '', null, '广东测试用工单位'
from dual where not exists (select 1 from t_enterprise where enterprise_code = '91440106TEST00004');

insert into t_enterprise (enterprise_id, enterprise_name, enterprise_code, region_code, enterprise_type, legal_person, contact_person, contact_phone, address, established_date, sync_status, status, del_flag, create_by, create_time, update_by, update_time, remark)
select 1005, '广州越秀物业服务有限公司', '91440104TEST00005', '440104', '3', '吴国华', '李婉清', '13811110005', '广东省广州市越秀区中山一路102号', '2016-09-11', '1', '0', '0', 'admin', sysdate(), '', null, '广东测试服务机构'
from dual where not exists (select 1 from t_enterprise where enterprise_code = '91440104TEST00005');

insert into t_enterprise (enterprise_id, enterprise_name, enterprise_code, region_code, enterprise_type, legal_person, contact_person, contact_phone, address, established_date, sync_status, status, del_flag, create_by, create_time, update_by, update_time, remark)
select 1006, '广州白云物流科技有限公司', '91440111TEST00006', '440111', '1', '陈海涛', '黄晓彤', '13811110006', '广东省广州市白云区机场路168号', '2021-02-08', '1', '0', '0', 'admin', sysdate(), '', null, '广东测试派遣单位'
from dual where not exists (select 1 from t_enterprise where enterprise_code = '91440111TEST00006');
