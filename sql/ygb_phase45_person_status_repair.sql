set names utf8mb4;

-- 仿真种子中 employment_status=2 不在业务字典内，统一归并为离岗(1)
update t_person
set employment_status = '1'
where employment_status = '2'
  and del_flag = '0';
