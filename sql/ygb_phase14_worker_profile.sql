create table if not exists ygb_worker_resume (
  resume_id          bigint primary key auto_increment,
  user_id            bigint not null,
  person_id          bigint not null,
  person_name        varchar(64) default '',
  mobile             varchar(32) default '',
  job_type           varchar(64) default '',
  expected_job       varchar(128) default '',
  expected_city      varchar(128) default '',
  expected_salary    varchar(64) default '',
  skill_tags         varchar(255) default '',
  certificate_text   varchar(255) default '',
  intro              text,
  create_by          varchar(64) default '',
  create_time        datetime,
  update_by          varchar(64) default '',
  update_time        datetime,
  remark             varchar(255) default '',
  del_flag           char(1) default '0'
);

create table if not exists ygb_worker_feedback (
  feedback_id        bigint primary key auto_increment,
  user_id            bigint not null,
  person_id          bigint not null,
  person_name        varchar(64) default '',
  mobile             varchar(32) default '',
  title              varchar(128) not null,
  content            text,
  contact_mobile     varchar(32) default '',
  status             char(1) default '0',
  create_by          varchar(64) default '',
  create_time        datetime,
  update_by          varchar(64) default '',
  update_time        datetime,
  remark             varchar(255) default '',
  del_flag           char(1) default '0'
);
