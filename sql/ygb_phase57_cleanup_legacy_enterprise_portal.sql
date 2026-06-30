-- Phase57: cleanup legacy enterprise portal menu shell and duplicates.
-- Scope: remove old enterprise portal menu tree that duplicates the document-defined tree.

set names utf8mb4;

create table if not exists sys_menu_bak_phase57_legacy_enterprise_portal like sys_menu;
create table if not exists sys_role_menu_bak_phase57_legacy_enterprise_portal like sys_role_menu;

drop temporary table if exists tmp_phase57_enterprise_menu_scope;
create temporary table tmp_phase57_enterprise_menu_scope (
  menu_id bigint not null primary key
);

insert ignore into tmp_phase57_enterprise_menu_scope (menu_id)
values
  (6170),
  (6171),
  (6172),
  (6173),
  (6174),
  (6175),
  (6176),
  (6177),
  (6178),
  (6179),
  (6180),
  (7020);

insert ignore into sys_menu_bak_phase57_legacy_enterprise_portal
select m.*
from sys_menu m
join tmp_phase57_enterprise_menu_scope s on s.menu_id = m.menu_id;

insert ignore into sys_role_menu_bak_phase57_legacy_enterprise_portal
select rm.*
from sys_role_menu rm
join tmp_phase57_enterprise_menu_scope s on s.menu_id = rm.menu_id;

delete rm
from sys_role_menu rm
join tmp_phase57_enterprise_menu_scope s on s.menu_id = rm.menu_id;

delete c
from sys_menu c
join tmp_phase57_enterprise_menu_scope s on s.menu_id = c.menu_id;

select count(1) as legacy_enterprise_portal_menu_count
from sys_menu
where menu_id in (6170,6171,6172,6173,6174,6175,6176,6177,6178,6179,6180,7020);
