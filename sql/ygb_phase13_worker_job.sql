create table if not exists ygb_worker_job_post (
  job_id             bigint primary key auto_increment,
  enterprise_id      bigint,
  enterprise_name    varchar(128) default '',
  title              varchar(128) not null,
  job_type           varchar(64) default '',
  work_address       varchar(255) default '',
  longitude          decimal(10,6),
  latitude           decimal(10,6),
  salary_min         decimal(12,2),
  salary_max         decimal(12,2),
  salary_text        varchar(64) default '',
  recruit_count      int default 1,
  contact_name       varchar(64) default '',
  contact_mobile     varchar(32) default '',
  description        text,
  requirement_text   text,
  status             char(1) default '0',
  publish_time       datetime,
  create_by          varchar(64) default '',
  create_time        datetime,
  update_by          varchar(64) default '',
  update_time        datetime,
  remark             varchar(255) default '',
  del_flag           char(1) default '0'
);

create table if not exists ygb_worker_job_apply (
  apply_id           bigint primary key auto_increment,
  job_id             bigint not null,
  user_id            bigint not null,
  person_id          bigint not null,
  person_name        varchar(64) default '',
  mobile             varchar(32) default '',
  status             char(1) default '0',
  apply_time         datetime,
  create_by          varchar(64) default '',
  create_time        datetime,
  update_by          varchar(64) default '',
  update_time        datetime,
  remark             varchar(255) default '',
  del_flag           char(1) default '0'
);

insert into ygb_worker_job_post (
  enterprise_id, enterprise_name, title, job_type, work_address, longitude, latitude, salary_min, salary_max, salary_text,
  recruit_count, contact_name, contact_mobile, description, requirement_text, status, publish_time, create_by, create_time, del_flag
) values
  (1001, '广东安联劳务有限公司', '焊工', '焊接作业', '佛山市南海区里水镇', 113.129030, 23.205280, 7000.00, 9000.00, '7000-9000 元/月', 5,
   '陈老师', '13800001111', '负责车间焊接、设备点检和工位清理。', '需有焊工经验，能适应倒班。', '0', sysdate(), 'system', sysdate(), '0'),
  (1002, '珠海阳光制造服务中心', '装配工', '装配作业', '珠海市金湾区平沙镇', 113.214860, 22.100420, 5500.00, 6800.00, '5500-6800 元/月', 12,
   '李主管', '13800002222', '负责装配、巡检和工序记录。', '有制造业经验优先，接受培训上岗。', '0', sysdate(), 'system', sysdate(), '0'),
  (1003, '深圳新业态配送合作站', '配送员', '新业态用工', '深圳市龙岗区坂田街道', 114.062210, 22.632860, 6000.00, 10000.00, '6000-10000 元/月', 20,
   '王站长', '13800003333', '负责区域配送与客户签收。', '会使用智能手机，身体健康。', '0', sysdate(), 'system', sysdate(), '0');
