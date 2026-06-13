set names utf8mb4;

create table if not exists t_height_work_report (
  report_id               bigint(20)      not null auto_increment comment '报备ID',
  report_no               varchar(32)     not null comment '报备编号',
  enterprise_id           bigint(20)      default 0 comment '企业ID（个人为0）',
  enterprise_name         varchar(100)    default '' comment '作业单位/个人',
  reporter_type           char(1)         default '1' comment '报备主体类型（1企业 2个人 3第三方导入）',
  applicant_name          varchar(30)     default '' comment '填报人姓名',
  applicant_phone         varchar(20)     default '' comment '填报人联系电话',
  work_location           varchar(500)    not null comment '作业地点',
  longitude               decimal(10,6)   default null comment '经度',
  latitude                decimal(10,6)   default null comment '纬度',
  start_time              datetime        not null comment '计划开始时间',
  end_time                datetime        not null comment '计划结束时间',
  work_height_m           int(11)         default 0 comment '作业高度（米）',
  worker_count            int(11)         default 0 comment '作业人数',
  guardian_name           varchar(30)     default '' comment '监护人姓名',
  guardian_phone          varchar(20)     default '' comment '监护人电话',
  safety_measures_json    text comment '安全措施JSON',
  cert_valid_status       char(1)         default '0' comment '证书核验状态（0全部有效 1部分失效 2全部失效）',
  cert_valid_count        int(11)         default 0 comment '有效证书人数',
  cert_invalid_count      int(11)         default 0 comment '无效证书人数',
  report_status           char(1)         default '0' comment '报备状态（0已报备 1已结束）',
  source_mode             varchar(32)     default 'PC' comment '来源模式',
  source_platform         varchar(64)     default '' comment '来源平台',
  source_serial_no        varchar(64)     default '' comment '来源流水号',
  voucher_token           varchar(64)     default '' comment '电子凭证令牌',
  actual_end_time         datetime        default null comment '实际结束时间',
  end_photo_url           varchar(500)    default '' comment '结束确认照片',
  del_flag                char(1)         default '0' comment '删除标识（0存在 2删除）',
  create_by               varchar(64)     default '' comment '创建者',
  create_time             datetime        default null comment '创建时间',
  update_by               varchar(64)     default '' comment '更新者',
  update_time             datetime        default null comment '更新时间',
  remark                  varchar(500)    default null comment '备注',
  primary key (report_id),
  unique key uk_height_work_report_no (report_no),
  key idx_height_work_enterprise (enterprise_id),
  key idx_height_work_region_time (start_time, report_status),
  key idx_height_work_source (source_mode, source_platform),
  key idx_height_work_status (report_status, cert_valid_status)
) engine=innodb auto_increment=102000 default charset=utf8mb4 comment='高处作业申报报备';

create table if not exists t_height_work_report_worker (
  row_id                  bigint(20)      not null auto_increment comment '明细ID',
  report_id               bigint(20)      not null comment '报备ID',
  worker_name             varchar(30)     default '' comment '作业人员姓名',
  id_card                 varchar(18)     default '' comment '身份证号',
  cert_no                 varchar(50)     default '' comment '高处作业证号',
  cert_photo_url          varchar(500)    default '' comment '证书照片',
  cert_valid_status       char(1)         default '0' comment '证书状态（0有效 1失效）',
  cert_valid_message      varchar(255)    default '' comment '证书核验结果',
  sort_order              int(11)         default 0 comment '排序号',
  primary key (row_id),
  key idx_height_worker_report (report_id)
) engine=innodb auto_increment=201000 default charset=utf8mb4 comment='高处作业作业人员明细';

update sys_menu
set menu_name = '技术防范',
    parent_id = 0,
    order_num = 17,
    path = 'ygb-techdefense',
    component = null,
    perms = '',
    icon = 'monitor',
    remark = '粤工保技术防范目录'
where menu_id = 4040;

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 4040, '技术防范', 0, 17, 'ygb-techdefense', null, '', '', 1, 0, 'M', '0', '0', '', 'monitor', 'admin', sysdate(), '', null, '粤工保技术防范目录'
from dual where not exists (select 1 from sys_menu where menu_id = 4040);

update sys_menu
set menu_name = '高处作业申报报备',
    parent_id = 4040,
    order_num = 1,
    path = 'heightWorkReport',
    component = 'ygb/heightWorkReport/index',
    perms = 'ygb:heightWorkReport:query',
    icon = 'guide',
    remark = '高处作业申报报备菜单'
where menu_id = 4041;

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 4041, '高处作业申报报备', 4040, 1, 'heightWorkReport', 'ygb/heightWorkReport/index', '', '', 1, 0, 'C', '0', '0', 'ygb:heightWorkReport:query', 'guide', 'admin', sysdate(), '', null, '高处作业申报报备菜单'
from dual where not exists (select 1 from sys_menu where menu_id = 4041);

update sys_menu set menu_name = '报备查询', parent_id = 4041, order_num = 1, perms = 'ygb:heightWorkReport:query'
where menu_id = 4045;
insert into sys_menu
select 4045, '报备查询', 4041, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:heightWorkReport:query', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 4045);

update sys_menu set menu_name = '报备新增', parent_id = 4041, order_num = 2, perms = 'ygb:heightWorkReport:add'
where menu_id = 4046;
insert into sys_menu
select 4046, '报备新增', 4041, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:heightWorkReport:add', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 4046);

update sys_menu set menu_name = '报备修改', parent_id = 4041, order_num = 3, perms = 'ygb:heightWorkReport:edit'
where menu_id = 4047;
insert into sys_menu
select 4047, '报备修改', 4041, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:heightWorkReport:edit', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 4047);

update sys_menu set menu_name = '作业结束', parent_id = 4041, order_num = 4, perms = 'ygb:heightWorkReport:finish'
where menu_id = 4048;
insert into sys_menu
select 4048, '作业结束', 4041, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:heightWorkReport:finish', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 4048);

update sys_menu set menu_name = '报备导出', parent_id = 4041, order_num = 5, perms = 'ygb:heightWorkReport:export'
where menu_id = 4049;
insert into sys_menu
select 4049, '报备导出', 4041, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:heightWorkReport:export', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 4049);

update sys_menu set menu_name = '数据导入', parent_id = 4041, order_num = 6, perms = 'ygb:heightWorkReport:import'
where menu_id = 4050;
insert into sys_menu
select 4050, '数据导入', 4041, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:heightWorkReport:import', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 4050);

update sys_menu set menu_name = '电子凭证', parent_id = 4041, order_num = 7, perms = 'ygb:heightWorkReport:voucher'
where menu_id = 4051;
insert into sys_menu
select 4051, '电子凭证', 4041, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:heightWorkReport:voucher', '#', 'admin', sysdate(), '', null, ''
from dual where not exists (select 1 from sys_menu where menu_id = 4051);

insert into sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark)
select 106, '人社监管员', 'ygb_hrss_supervisor', 15, '4', 1, 1, '0', '0', 'admin', sysdate(), '', null, '粤工保人社监管角色'
from dual where not exists (select 1 from sys_role where role_id = 106);

insert into sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark)
select 107, '应急监管员', 'ygb_emergency_supervisor', 16, '4', 1, 1, '0', '0', 'admin', sysdate(), '', null, '粤工保应急监管角色'
from dual where not exists (select 1 from sys_role where role_id = 107);

insert into sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark)
select 108, '保险公司', 'ygb_insurer', 17, '3', 1, 1, '0', '0', 'admin', sysdate(), '', null, '粤工保保险机构角色'
from dual where not exists (select 1 from sys_role where role_id = 108);

insert into sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark)
select 109, '银行', 'ygb_bank', 18, '3', 1, 1, '0', '0', 'admin', sysdate(), '', null, '粤工保银行机构角色'
from dual where not exists (select 1 from sys_role where role_id = 109);

insert ignore into sys_role_menu (role_id, menu_id)
select 101, menu_id from sys_menu where menu_id in (4040, 4041, 4045, 4049);
insert ignore into sys_role_menu (role_id, menu_id)
select 102, menu_id from sys_menu where menu_id in (4040, 4041, 4045, 4049);
insert ignore into sys_role_menu (role_id, menu_id)
select 103, menu_id from sys_menu where menu_id in (4040, 4041, 4045, 4049);

insert ignore into sys_role_menu (role_id, menu_id)
select 104, menu_id from sys_menu where menu_id in (
  2040, 2008, 2800, 2802,
  2009, 2900, 2902,
  2060, 2014, 3400, 3401, 3402, 3403, 3404, 3407,
  2015, 3500, 3501, 3502, 3503, 3504,
  2016, 3600, 3601, 3602, 3603, 3604,
  2012, 3200, 3202,
  2013, 3300, 3302,
  2080, 2017, 3700,
  3960, 3961, 3965, 3967, 3962, 3968,
  3980, 3981, 3985, 3987,
  3940, 3941, 3950, 3951, 3952,
  3920, 3921, 3930, 3931, 3932,
  4040, 4041, 4045, 4046, 4047, 4048, 4049, 4051
);

insert ignore into sys_role_menu (role_id, menu_id)
select 105, menu_id from sys_menu where menu_id in (
  2040, 2008, 2800,
  2009, 2900,
  2060, 2014, 3400, 3401, 3402, 3407,
  2015, 3500, 3501, 3502,
  2016, 3600, 3601, 3602,
  2012, 3200,
  2013, 3300,
  2080, 2017, 3700,
  3960, 3961, 3965, 3962, 3968,
  3980, 3981, 3985,
  3940, 3941, 3950, 3951,
  3920, 3921, 3930, 3931,
  4040, 4041, 4045, 4046, 4047, 4048, 4051
);

insert ignore into sys_role_menu (role_id, menu_id)
select 106, menu_id from sys_menu where menu_id in (
  3900, 3901, 3910, 3911,
  2000, 2001, 2100, 2104, 2002, 2200, 2204,
  2020, 2003, 2300, 2304, 2004, 2400, 2404, 2005, 2500, 2501,
  2006, 2600, 2604, 2007, 2700, 2703,
  2040, 2008, 2800, 2801, 2802, 2009, 2900, 2901, 2902, 2010, 3000, 3001, 3002, 3003,
  2011, 3100, 3101, 3102, 3103, 2012, 3200, 3201, 3202, 2013, 3300, 3301, 3302,
  2060, 2015, 3500, 3503, 3504, 2016, 3600, 3601, 3602, 3604,
  2080, 2017, 3700, 3701, 3702,
  3980, 3981, 3985, 3986, 3987,
  3940, 3941, 3950, 3951, 3952,
  3920, 3921, 3930, 3931, 3932
);

insert ignore into sys_role_menu (role_id, menu_id)
select 107, menu_id from sys_menu where menu_id in (
  3900, 3901, 3910, 3911,
  2000, 2001, 2100, 2104, 2002, 2200, 2204,
  2040, 2011, 3100, 3103,
  2060, 2014, 3400, 3401, 3402, 3403, 3404, 3405, 3406, 3407, 3408, 3409,
  2015, 3500, 3503, 3504, 2016, 3600, 3601, 3602, 3604,
  2080, 2017, 3700, 3701, 3702,
  3960, 3961, 3965, 3966, 3967, 3962, 3968, 3969, 3970,
  3980, 3981, 3985, 3986, 3987,
  3940, 3941, 3950, 3951, 3952,
  3920, 3921, 3930, 3931, 3932,
  4040, 4041, 4045, 4049
);

insert ignore into sys_role_menu (role_id, menu_id)
select 108, menu_id from sys_menu where menu_id in (
  3960, 3961, 3965, 3967, 3962, 3968, 3970,
  3980, 3981, 3985, 3987,
  3920, 3921, 3930, 3932
);

insert ignore into sys_role_menu (role_id, menu_id)
select 109, menu_id from sys_menu where menu_id in (
  2020, 2006, 2600, 2604, 2007, 2700, 2703,
  3980, 3981, 3985, 3987
);
