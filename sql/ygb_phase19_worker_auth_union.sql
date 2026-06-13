alter table ygb_worker_complaint
  add column if not exists sync_union_flag char(1) default '0' after anonymous_flag;
