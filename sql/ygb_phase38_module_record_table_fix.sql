set names utf8mb4;

create table if not exists ygb_module_record (
  record_id bigint not null auto_increment comment '记录ID',
  record_type varchar(64) not null comment '记录类型',
  record_name varchar(128) not null comment '记录名称',
  category_code varchar(64) default '' comment '分类编码',
  stat_month varchar(16) default '' comment '统计月份',
  portal_code varchar(32) default 'ygb' comment '门户编码',
  workflow_status varchar(32) default 'pending' comment '流程状态',
  status char(1) default '0' comment '状态',
  region_code varchar(32) default '' comment '区域编码',
  enterprise_id bigint default null comment '企业ID',
  enterprise_name varchar(128) default '' comment '企业名称',
  person_id bigint default null comment '人员ID',
  person_name varchar(64) default '' comment '人员姓名',
  related_id bigint default null comment '关联ID',
  related_code varchar(64) default '' comment '关联编码',
  sort_order int default 0 comment '排序',
  source_label varchar(64) default '' comment '来源标签',
  payload_json text comment '扩展载荷',
  remark varchar(500) default null comment '备注',
  create_by varchar(64) default '' comment '创建者',
  create_time datetime default current_timestamp comment '创建时间',
  update_by varchar(64) default '' comment '更新者',
  update_time datetime default null comment '更新时间',
  del_flag char(1) default '0' comment '删除标记（0存在 2删除）',
  primary key (record_id),
  key idx_ygb_module_record_type (record_type),
  key idx_ygb_module_record_portal (portal_code),
  key idx_ygb_module_record_region (region_code),
  key idx_ygb_module_record_enterprise (enterprise_id),
  key idx_ygb_module_record_person (person_id),
  key idx_ygb_module_record_month (stat_month),
  key idx_ygb_module_record_del_flag (del_flag)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_unicode_ci comment='粤工保扩展模块记录台账';

insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, sort_order, source_label,
  payload_json, remark, create_by, create_time, del_flag
)
select
  920001, 'NEWFORM_TRAINING', '灵活用工岗前参保培训', 'training', '2026-06', 'ygb', 'pending', '0', '440106',
  1001, '广州南粤人力资源有限公司', null, '', null, 'NF-TR-202606-001', 1, '新业态监管',
  '{"platformName":"即时配送平台","courseHours":4,"warningLevel":"1","channel":"internal"}',
  '新业态培训管理初始化样例', 'admin', sysdate(), '0'
from dual
where not exists (select 1 from ygb_module_record where record_id = 920001);

insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, sort_order, source_label,
  payload_json, remark, create_by, create_time, del_flag
)
select
  920002, 'OCCUPATION_PREVENTION', '喷涂车间职业病预防专项', 'prevention', '2026-06', 'ygb', 'processing', '0', '440305',
  1002, '深圳鹏城机电工程有限公司', null, '', null, 'OCC-PV-202606-001', 1, '职业病监管',
  '{"industryType":"制造加工","warningLevel":"2","projectStage":"inspection"}',
  '职业病预防项目初始化样例', 'admin', sysdate(), '0'
from dual
where not exists (select 1 from ygb_module_record where record_id = 920002);

insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, sort_order, source_label,
  payload_json, remark, create_by, create_time, del_flag
)
select
  920003, 'OCCUPATION_HEALTH_ARCHIVE', '焊接岗位年度职业健康档案', 'health_archive', '2026-06', 'ygb', 'closed', '0', '440606',
  1003, '佛山顺德智造服务有限公司', null, '', null, 'OCC-HA-202606-001', 1, '职业病监管',
  '{"industryType":"建筑施工","warningLevel":"1","archiveYear":"2026"}',
  '职业健康档案初始化样例', 'admin', sysdate(), '0'
from dual
where not exists (select 1 from ygb_module_record where record_id = 920003);

insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, sort_order, source_label,
  payload_json, remark, create_by, create_time, del_flag
)
select
  920101, 'PERSON_CERTIFICATE', '高处作业证年审', 'certificate', '2026-06', 'ygb', 'pending', '0', '440106',
  1001, '广州南粤人力资源有限公司', 10001, '赵志成', null, 'PC-CERT-202606-001', 1, '人员管理',
  '{"certType":"高处作业","certNo":"CERT-440106-001","warningLevel":"1"}',
  '特证管理初始化样例', 'admin', sysdate(), '0'
from dual
where not exists (select 1 from ygb_module_record where record_id = 920101);

insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, sort_order, source_label,
  payload_json, remark, create_by, create_time, del_flag
)
select
  920102, 'PERSON_BLACKLIST', '违规进场限制名单', 'blacklist', '2026-06', 'ygb', 'processing', '0', '440305',
  1002, '深圳鹏城机电工程有限公司', 10005, '刘海鹏', null, 'PC-BL-202606-001', 1, '人员管理',
  '{"reason":"证件过期仍尝试上岗","warningLevel":"2"}',
  '黑名单初始化样例', 'admin', sysdate(), '0'
from dual
where not exists (select 1 from ygb_module_record where record_id = 920102);

insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, sort_order, source_label,
  payload_json, remark, create_by, create_time, del_flag
)
select
  920103, 'PERSON_TRAINING', '焊接班组月度培训抽查', 'training', '2026-06', 'ygb', 'pending', '0', '440606',
  1003, '佛山顺德智造服务有限公司', 10008, '陈文强', null, 'PC-TR-202606-001', 1, '人员管理',
  '{"courseHours":4,"warningLevel":"1","channel":"internal"}',
  '培训监督初始化样例', 'admin', sysdate(), '0'
from dual
where not exists (select 1 from ygb_module_record where record_id = 920103);

insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, sort_order, source_label,
  payload_json, remark, create_by, create_time, del_flag
)
select
  920104, 'PERSON_HIGH_RISK_POST', '高处吊装岗位', 'high_risk_post', '2026-06', 'ygb', 'processing', '0', '440106',
  1001, '广州南粤人力资源有限公司', null, '', null, 'PC-HR-202606-001', 1, '人员管理',
  '{"riskLevel":"high","warningLevel":"2"}',
  '高危岗位初始化样例', 'admin', sysdate(), '0'
from dual
where not exists (select 1 from ygb_module_record where record_id = 920104);

insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, sort_order, source_label,
  payload_json, remark, create_by, create_time, del_flag
)
select
  920105, 'PERSON_RISK_POST', '设备巡检岗位', 'risk_post', '2026-06', 'ygb', 'closed', '0', '440305',
  1002, '深圳鹏城机电工程有限公司', null, '', null, 'PC-RP-202606-001', 1, '人员管理',
  '{"riskLevel":"medium","warningLevel":"1"}',
  '风险岗位初始化样例', 'admin', sysdate(), '0'
from dual
where not exists (select 1 from ygb_module_record where record_id = 920105);

insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, sort_order, source_label,
  payload_json, remark, create_by, create_time, del_flag
)
select
  920106, 'PERSON_EXPERT', '高危作业审查专家池', 'expert', '2026-06', 'ygb', 'pending', '0', '440000',
  null, '', null, '', null, 'PC-EX-202606-001', 1, '人员管理',
  '{"specialty":"高危作业审查","warningLevel":"0"}',
  '专家库初始化样例', 'admin', sysdate(), '0'
from dual
where not exists (select 1 from ygb_module_record where record_id = 920106);

insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, sort_order, source_label,
  payload_json, remark, create_by, create_time, del_flag
)
select
  920201, 'DEVICE_GEOFENCE', '南山高危作业围栏', 'geofence', '2026-06', 'ygb', 'processing', '0', '440305',
  1002, '深圳鹏城机电工程有限公司', null, '', null, 'DG-FENCE-202606-001', 1, '设备管理',
  '{"fenceType":"polygon","center":"113.9448,22.5428","warningLevel":"1"}',
  '设备电子围栏初始化样例', 'admin', sysdate(), '0'
from dual
where not exists (select 1 from ygb_module_record where record_id = 920201);

insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, sort_order, source_label,
  payload_json, remark, create_by, create_time, del_flag
)
select
  920301, 'ENTERPRISE_HIGH_RISK', '天河重点高危企业名单', 'high_risk', '2026-06', 'ygb', 'processing', '0', '440106',
  1001, '广州南粤人力资源有限公司', null, '', null, 'EH-202606-001', 1, '单位管理',
  '{"riskLevel":"high","warningLevel":"2","riskSource":"现场治理"}',
  '高危企业库初始化样例', 'admin', sysdate(), '0'
from dual
where not exists (select 1 from ygb_module_record where record_id = 920301);

insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, sort_order, source_label,
  payload_json, remark, create_by, create_time, del_flag
)
select
  920302, 'ENTERPRISE_RELATION', '南粤派遣-智造用工协作关系', 'relation', '2026-06', 'ygb', 'pending', '0', '440606',
  1003, '佛山顺德智造服务有限公司', null, '', 1001, 'ER-202606-001', 1, '单位管理',
  '{"dispatchEnterpriseId":1001,"dispatchEnterpriseName":"广州南粤人力资源有限公司","employerEnterpriseId":1003,"employerEnterpriseName":"佛山顺德智造服务有限公司"}',
  '派遣用工关联初始化样例', 'admin', sysdate(), '0'
from dual
where not exists (select 1 from ygb_module_record where record_id = 920302);

insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, sort_order, source_label,
  payload_json, remark, create_by, create_time, del_flag
)
select
  920303, 'ENTERPRISE_UNION', '南山机电企业工会联合会', 'union', '2026-06', 'ygb', 'closed', '0', '440305',
  1002, '深圳鹏城机电工程有限公司', null, '', null, 'EU-202606-001', 1, '单位管理',
  '{"unionLevel":"enterprise","contactChannel":"internal","warningLevel":"0"}',
  '工会管理初始化样例', 'admin', sysdate(), '0'
from dual
where not exists (select 1 from ygb_module_record where record_id = 920303);
