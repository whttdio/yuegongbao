create table if not exists ygb_worker_complaint (
  complaint_id      bigint primary key auto_increment,
  user_id           bigint not null,
  person_id         bigint not null,
  person_name       varchar(64) default '',
  enterprise_id     bigint,
  enterprise_name   varchar(128) default '',
  complaint_type    varchar(32) not null,
  title             varchar(128) not null,
  content           text,
  contact_mobile    varchar(32) default '',
  anonymous_flag    char(1) default '0',
  sync_union_flag   char(1) default '0',
  attachments       text,
  status            char(1) default '0',
  reply_content     text,
  handle_time_text  varchar(64) default '',
  create_by         varchar(64) default '',
  create_time       datetime,
  update_by         varchar(64) default '',
  update_time       datetime,
  remark            varchar(255) default '',
  del_flag          char(1) default '0'
);

create table if not exists ygb_worker_legal_consult (
  consult_id        bigint primary key auto_increment,
  user_id           bigint not null,
  person_id         bigint not null,
  person_name       varchar(64) default '',
  enterprise_id     bigint,
  enterprise_name   varchar(128) default '',
  consult_type      varchar(32) not null,
  title             varchar(128) not null,
  content           text,
  contact_mobile    varchar(32) default '',
  attachments       text,
  status            char(1) default '0',
  reply_content     text,
  reply_time_text   varchar(64) default '',
  create_by         varchar(64) default '',
  create_time       datetime,
  update_by         varchar(64) default '',
  update_time       datetime,
  remark            varchar(255) default '',
  del_flag          char(1) default '0'
);

-- 站内通知跳转样例
-- 说明：
-- 1. sys_notice.remark 用于承载劳动者端通知/推送统一落页 JSON
-- 2. 推荐字段：jumpPath / jumpQuery / actionLabel / sourceLabel
-- 3. notice_content 放正文解释，remark 只放跳转配置

delete from sys_notice where notice_id in (9001, 9002, 9003, 9004);

insert into sys_notice (
  notice_id, notice_title, notice_type, notice_content, status,
  create_by, create_time, update_by, update_time, remark
) values
(
  9001,
  '培训提醒：本月培训未完成',
  '1',
  '您本月安全培训尚未完成，当前打卡和工资查询仍处于锁定状态，请尽快完成当月培训题目。',
  '0',
  'admin',
  sysdate(),
  '',
  null,
  '{"jumpPath":"/pages/training/index","jumpQuery":{"from":"notice","scene":"month-lock"},"actionLabel":"去完成培训","sourceLabel":"本月培训未完成提醒"}'
),
(
  9002,
  '工资提醒：本月工资已同步',
  '1',
  '您最近一个工资周期的数据已同步到劳动者端，可进入工资查询页面查看应发、实发和扣款明细。',
  '0',
  'admin',
  sysdate(),
  '',
  null,
  '{"jumpPath":"/pages/salary/list","jumpQuery":{"from":"notice"},"actionLabel":"查看工资","sourceLabel":"工资发放提醒"}'
),
(
  9003,
  '投诉结果：工单已有处理进展',
  '1',
  '您提交的投诉举报工单已进入新的处理阶段，请及时查看处理说明、回复内容和后续协同建议。',
  '0',
  'admin',
  sysdate(),
  '',
  null,
  '{"jumpPath":"/pages/complaint/detail","jumpQuery":{"complaintId":"10001","from":"notice"},"actionLabel":"查看处理进度","sourceLabel":"投诉处理结果通知"}'
),
(
  9004,
  '法律咨询：律师已回复',
  '1',
  '您提交的法律咨询已收到回复，请进入详情查看建议内容，并按需要继续补充材料或发起下一步处理。',
  '0',
  'admin',
  sysdate(),
  '',
  null,
  '{"jumpPath":"/pages/legal/detail","jumpQuery":{"consultId":"20001","from":"notice"},"actionLabel":"查看回复","sourceLabel":"法律咨询回复通知"}'
);
