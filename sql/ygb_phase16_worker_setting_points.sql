create table if not exists ygb_worker_setting (
  setting_id         bigint primary key auto_increment,
  user_id            bigint not null,
  person_id          bigint not null,
  notify_enabled     char(1) default '1',
  push_client_id     varchar(128) default '',
  notification_permission varchar(32) default '',
  push_platform      varchar(32) default '',
  create_by          varchar(64) default '',
  create_time        datetime,
  update_by          varchar(64) default '',
  update_time        datetime,
  remark             varchar(255) default '',
  del_flag           char(1) default '0',
  unique key uk_worker_setting_user (user_id)
);

create table if not exists ygb_worker_point_ledger (
  ledger_id          bigint primary key auto_increment,
  user_id            bigint not null,
  person_id          bigint not null,
  person_name        varchar(64) default '',
  change_type        varchar(16) default 'IN',
  title              varchar(128) not null,
  summary            varchar(255) default '',
  score_delta        decimal(10,2) default 0.00,
  balance_after      decimal(10,2) default 0.00,
  create_by          varchar(64) default '',
  create_time        datetime,
  update_by          varchar(64) default '',
  update_time        datetime,
  remark             varchar(255) default '',
  del_flag           char(1) default '0',
  key idx_worker_point_user (user_id)
);
