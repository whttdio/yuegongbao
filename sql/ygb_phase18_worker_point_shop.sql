create table if not exists ygb_worker_point_goods (
  goods_id            bigint primary key auto_increment,
  goods_key           varchar(64) not null,
  goods_name          varchar(128) not null,
  goods_desc          varchar(255) default '',
  required_score      decimal(10,2) default 0.00,
  goods_type          varchar(32) default 'coupon',
  status              char(1) default '1',
  stock_count         int default 0,
  sort_num            int default 0,
  create_by           varchar(64) default '',
  create_time         datetime,
  update_by           varchar(64) default '',
  update_time         datetime,
  remark              varchar(255) default '',
  del_flag            char(1) default '0',
  unique key uk_worker_point_goods_key (goods_key)
);

create table if not exists ygb_worker_point_exchange (
  exchange_id         bigint primary key auto_increment,
  user_id             bigint not null,
  person_id           bigint not null,
  person_name         varchar(64) default '',
  goods_key           varchar(64) not null,
  goods_name          varchar(128) default '',
  goods_type          varchar(32) default 'coupon',
  score_cost          decimal(10,2) default 0.00,
  exchange_status     char(1) default '1',
  delivery_remark     varchar(255) default '',
  create_by           varchar(64) default '',
  create_time         datetime,
  update_by           varchar(64) default '',
  update_time         datetime,
  remark              varchar(255) default '',
  del_flag            char(1) default '0',
  key idx_worker_point_exchange_user (user_id)
);
