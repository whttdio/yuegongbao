alter table ygb_worker_job_post
  add column if not exists longitude decimal(10,6) default null comment '岗位经度' after work_address;

alter table ygb_worker_job_post
  add column if not exists latitude decimal(10,6) default null comment '岗位纬度' after longitude;

update ygb_worker_job_post
set longitude = 113.129030,
    latitude = 23.205280
where del_flag = '0'
  and enterprise_id = 1001
  and title = '焊工'
  and (longitude is null or latitude is null);

update ygb_worker_job_post
set longitude = 113.214860,
    latitude = 22.100420
where del_flag = '0'
  and enterprise_id = 1002
  and title = '装配工'
  and (longitude is null or latitude is null);

update ygb_worker_job_post
set longitude = 114.062210,
    latitude = 22.632860
where del_flag = '0'
  and enterprise_id = 1003
  and title = '配送员'
  and (longitude is null or latitude is null);
