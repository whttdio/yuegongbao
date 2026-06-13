insert into sys_user (
  user_id,
  dept_id,
  user_name,
  nick_name,
  user_type,
  email,
  phonenumber,
  sex,
  avatar,
  password,
  status,
  del_flag,
  login_ip,
  login_date,
  pwd_update_date,
  create_by,
  create_time,
  update_by,
  update_time,
  remark
)
select
  910001,
  ifnull((select dept_id from sys_dept where dept_id = 118 limit 1), 100),
  '13700010001',
  '赵志成',
  '00',
  'worker-demo@yuegongbao.local',
  '13700010001',
  '1',
  '',
  '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2',
  '0',
  '0',
  '',
  null,
  sysdate(),
  'docker',
  sysdate(),
  '',
  null,
  'Docker demo worker account: admin123'
from dual
where exists (select 1 from t_person where person_id = 10001)
  and not exists (select 1 from sys_user where user_id = 910001 or user_name = '13700010001');

insert into sys_user_role (user_id, role_id)
select 910001, 2
from dual
where exists (select 1 from sys_user where user_id = 910001)
  and not exists (select 1 from sys_user_role where user_id = 910001 and role_id = 2);

