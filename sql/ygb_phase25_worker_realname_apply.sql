create table if not exists ygb_worker_realname_apply (
  apply_id           bigint not null auto_increment,
  user_id            bigint not null,
  person_id          bigint default null,
  person_name        varchar(64) not null default '',
  mobile             varchar(32) not null default '',
  id_card            varchar(32) not null default '',
  apply_status       char(1) not null default '0' comment '0待审核 1已通过 2已驳回',
  reject_reason      varchar(500) default '',
  id_card_front_url  varchar(500) not null default '',
  id_card_back_url   varchar(500) not null default '',
  selfie_url         varchar(500) not null default '',
  source_module      varchar(64) default 'worker-uniapp',
  create_by          varchar(64) default '',
  create_time        datetime default current_timestamp,
  update_by          varchar(64) default '',
  update_time        datetime default current_timestamp on update current_timestamp,
  remark             varchar(500) default '',
  del_flag           char(1) default '0',
  primary key (apply_id)
) engine=innodb default charset=utf8mb4 comment='劳动者实名认证申请';

create index idx_worker_realname_user_time on ygb_worker_realname_apply(user_id, apply_id desc);
