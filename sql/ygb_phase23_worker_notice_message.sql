create table if not exists ygb_worker_notice_message (
  message_id        bigint primary key auto_increment,
  user_id           bigint not null,
  person_id         bigint not null,
  person_name       varchar(64) default '',
  message_type      varchar(32) default 'BUSINESS',
  title             varchar(128) not null,
  summary           varchar(255) default '',
  content           text,
  biz_type          varchar(64) default '',
  biz_id            varchar(64) default '',
  jump_path         varchar(255) default '',
  jump_query_text   text,
  action_label      varchar(64) default '',
  source_label      varchar(64) default '',
  read_flag         char(1) default '0',
  read_time         datetime,
  create_by         varchar(64) default '',
  create_time       datetime,
  update_by         varchar(64) default '',
  update_time       datetime,
  remark            varchar(255) default '',
  del_flag          char(1) default '0'
);

create index idx_worker_notice_user_time on ygb_worker_notice_message(user_id, message_id desc);
create index idx_worker_notice_biz on ygb_worker_notice_message(user_id, biz_type, biz_id);
