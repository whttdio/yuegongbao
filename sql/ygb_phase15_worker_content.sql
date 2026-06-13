create table if not exists ygb_worker_activity_join (
  join_id             bigint primary key auto_increment,
  activity_key        varchar(64) not null,
  user_id             bigint not null,
  person_id           bigint not null,
  person_name         varchar(64) default '',
  mobile              varchar(32) default '',
  status              char(1) default '0',
  create_by           varchar(64) default '',
  create_time         datetime,
  update_by           varchar(64) default '',
  update_time         datetime,
  remark              varchar(255) default '',
  del_flag            char(1) default '0'
);

create table if not exists ygb_worker_video_progress (
  progress_id         bigint primary key auto_increment,
  video_key           varchar(64) not null,
  user_id             bigint not null,
  person_id           bigint not null,
  watched_seconds     int default 0,
  total_seconds       int default 0,
  completed_flag      char(1) default '0',
  create_time         datetime,
  update_time         datetime,
  del_flag            char(1) default '0',
  unique key uk_video_user (video_key, user_id)
);
