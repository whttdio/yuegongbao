-- Phase55: cleanup legacy PC menu entries after business menu restructure.
-- Scope: sys_menu and sys_role_menu only. Business tables are not touched.

set names utf8mb4;

create table if not exists sys_menu_bak_phase55_legacy_cleanup like sys_menu;
create table if not exists sys_role_menu_bak_phase55_legacy_cleanup like sys_role_menu;

drop temporary table if exists tmp_phase55_menu_scope;
create temporary table tmp_phase55_menu_scope (
  menu_id bigint not null primary key
);

insert ignore into tmp_phase55_menu_scope (menu_id)
select menu_id
from sys_menu
where menu_id in (
  -- Old contract filing and template entries.
  2003, 2300, 2301, 2302, 2303, 2304,
  3980, 3981, 3982, 3983, 3984, 3985, 3986,
  7004, 8005, 8006,
  -- Old top-level directory shells.
  2000, 2020, 2040, 2060, 2080,
  3900, 3920, 3940, 3960,
  4000, 4020, 4040, 4300, 4400,
  5885, 6150, 6170, 6190,
  -- Visible orphan menus to be re-parented.
  4041, 4401, 4402, 4403, 4404, 4405, 6153,
  -- Duplicate visible business entries to be removed.
  8030, 5975
);

insert ignore into tmp_phase55_menu_scope (menu_id)
select menu_id
from sys_menu
where parent_id in (8030, 5975);

insert ignore into sys_menu_bak_phase55_legacy_cleanup
select m.*
from sys_menu m
join tmp_phase55_menu_scope s on s.menu_id = m.menu_id;

insert ignore into sys_role_menu_bak_phase55_legacy_cleanup
select rm.*
from sys_role_menu rm
join tmp_phase55_menu_scope s on s.menu_id = rm.menu_id;

-- Move visible business pages out of legacy hidden shells before deleting shells.
update sys_menu
set parent_id = 7012,
    order_num = 14,
    portal_scope = 'ygb',
    update_by = 'phase55',
    update_time = now()
where menu_id = 4041;

update sys_menu
set parent_id = 7018,
    order_num = case menu_id
      when 4401 then 1
      when 4402 then 2
      when 4403 then 3
      when 4404 then 4
      when 4405 then 5
      else order_num
    end,
    portal_scope = 'ygb',
    update_by = 'phase55',
    update_time = now()
where menu_id in (4401, 4402, 4403, 4404, 4405);

update sys_menu
set parent_id = 7021,
    order_num = 7,
    portal_scope = 'ygb',
    update_by = 'phase55',
    update_time = now()
where menu_id = 6153;

drop temporary table if exists tmp_phase55_delete_menu;
create temporary table tmp_phase55_delete_menu (
  menu_id bigint not null primary key
);

insert ignore into tmp_phase55_delete_menu (menu_id)
select menu_id
from sys_menu
where menu_id in (
  -- Old contract filing and template entries.
  2003, 2300, 2301, 2302, 2303, 2304,
  3980, 3981, 3982, 3983, 3984, 3985, 3986,
  7004, 8005, 8006,
  -- Duplicate visible entries. Keep 4001/newformWorker and 4530/deviceChipInventory.
  8030, 5975
);

insert ignore into tmp_phase55_delete_menu (menu_id)
select menu_id
from sys_menu
where parent_id in (8030, 5975);

delete rm
from sys_role_menu rm
join tmp_phase55_delete_menu d on d.menu_id = rm.menu_id;

delete m
from sys_menu m
join tmp_phase55_delete_menu d on d.menu_id = m.menu_id;

drop temporary table if exists tmp_phase55_shell_menu;
create temporary table tmp_phase55_shell_menu (
  menu_id bigint not null primary key
);

insert ignore into tmp_phase55_shell_menu (menu_id)
select menu_id
from sys_menu
where menu_id in (
  2000, 2020, 2040, 2060, 2080,
  3900, 3920, 3940, 3960,
  4000, 4020, 4040, 4300, 4400,
  5885, 6150, 6170, 6190
)
and not exists (
  select 1
  from sys_menu c
  where c.parent_id = sys_menu.menu_id
);

delete rm
from sys_role_menu rm
join tmp_phase55_shell_menu d on d.menu_id = rm.menu_id;

delete m
from sys_menu m
join tmp_phase55_shell_menu d on d.menu_id = m.menu_id;

-- Keep phase53 dual-portal contract filing menus authorized for portal-compatible roles.
insert into sys_role_menu (role_id, menu_id, portal_scope)
select r.role_id, m.menu_id, m.portal_scope
from sys_role r
join sys_menu m on m.menu_id between 9105300 and 9105448
where r.del_flag = '0'
  and (
    r.role_key = 'admin'
    or r.role_key like '%admin%'
    or r.allowed_portal_scope in ('both', m.portal_scope)
  )
on duplicate key update portal_scope = values(portal_scope);

-- Verification result sets.
select count(1) as old_contract_menu_count
from sys_menu
where menu_id in (
  2003, 2300, 2301, 2302, 2303, 2304,
  3980, 3981, 3982, 3983, 3984, 3985, 3986,
  7004, 8005, 8006
);

select count(1) as phase53_contract_menu_count
from sys_menu
where menu_id between 9105300 and 9105448;

select count(1) as visible_menu_under_hidden_parent_count
from sys_menu c
join sys_menu p on p.menu_id = c.parent_id
where c.visible = '0'
  and c.status = '0'
  and (p.visible <> '0' or p.status <> '0');

select count(1) as duplicate_visible_c_menu_group_count
from (
  select coalesce(portal_scope, '') as portal_scope, menu_name
  from sys_menu
  where menu_type = 'C'
    and visible = '0'
    and status = '0'
  group by coalesce(portal_scope, ''), menu_name
  having count(1) > 1
) d;

select count(1) as visible_legacy_shell_count
from sys_menu
where menu_id in (
  2000, 2020, 2040, 2060, 2080,
  3900, 3920, 3940, 3960,
  4000, 4020, 4040, 4300, 4400,
  5885, 6150, 6170, 6190
)
and visible = '0'
and status = '0';
