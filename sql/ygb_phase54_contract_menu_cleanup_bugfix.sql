-- Phase54: clean legacy contract filing menus and keep phase53 YGB/AZB contract menus.
-- Execute after ygb_phase53_contract_filing_dual_portal.sql.

create table if not exists sys_menu_bak_phase54_contract_old like sys_menu;
insert ignore into sys_menu_bak_phase54_contract_old
select *
from sys_menu
where menu_id in (
  2003, 2300, 2301, 2302, 2303, 2304,
  3980, 3981, 3982, 3983, 3984, 3985, 3986,
  7004, 8005, 8006
);

create table if not exists sys_role_menu_bak_phase54_contract_old like sys_role_menu;
insert ignore into sys_role_menu_bak_phase54_contract_old
select *
from sys_role_menu
where menu_id in (
  2003, 2300, 2301, 2302, 2303, 2304,
  3980, 3981, 3982, 3983, 3984, 3985, 3986,
  7004, 8005, 8006
);

delete from sys_role_menu
where menu_id in (
  2003, 2300, 2301, 2302, 2303, 2304,
  3980, 3981, 3982, 3983, 3984, 3985, 3986,
  7004, 8005, 8006
);

delete from sys_menu
where menu_id in (
  2003, 2300, 2301, 2302, 2303, 2304,
  3980, 3981, 3982, 3983, 3984, 3985, 3986,
  7004, 8005, 8006
);

insert into sys_role_menu (role_id, menu_id, portal_scope)
select r.role_id, m.menu_id, m.portal_scope
from sys_role r
join sys_menu m on m.menu_id between 9105300 and 9105448
where r.del_flag = '0'
  and (r.role_key = 'admin' or r.role_key like '%admin%' or r.allowed_portal_scope in ('both', m.portal_scope))
on duplicate key update portal_scope = values(portal_scope);

select count(1) as old_contract_menu_count
from sys_menu
where menu_id in (
  2003, 2300, 2301, 2302, 2303, 2304,
  3980, 3981, 3982, 3983, 3984, 3985, 3986,
  7004, 8005, 8006
);

select count(1) as phase53_contract_menu_count
from sys_menu
where menu_id between 9105300 and 9105448
  and (
    menu_id in (9105300, 9105301, 9105302, 9105303, 9105304,
                9105400, 9105401, 9105402, 9105403, 9105404)
    or parent_id in (9105301, 9105302, 9105303, 9105304,
                     9105401, 9105402, 9105403, 9105404)
  );
