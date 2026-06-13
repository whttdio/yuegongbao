set names utf8mb4;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4526, '考勤设备', 2014, 20, 'deviceAttendance', 'ygb/deviceAttendance/index', '', 'YgbDeviceAttendance',
  1, 0, 'C', '0', '0', 'ygb:device:list', 'monitor',
  'admin', sysdate(), 'admin', sysdate(), '设备考勤子视图', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4526);

insert into sys_menu select 4527, '芯片设备', 2014, 21, 'deviceChip', 'ygb/deviceChip/index', '', 'YgbDeviceChip', 1, 0, 'C', '0', '0', 'ygb:device:list', 'cpu', 'admin', sysdate(), 'admin', sysdate(), '设备芯片子视图', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4527);

insert into sys_menu select 4528, 'AI设备', 2014, 22, 'deviceAi', 'ygb/deviceAi/index', '', 'YgbDeviceAi', 1, 0, 'C', '0', '0', 'ygb:device:list', 'video-camera', 'admin', sysdate(), 'admin', sysdate(), '设备AI子视图', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4528);

insert into sys_menu select 4529, '物联卡', 2014, 23, 'deviceIotCard', 'ygb/deviceIotCard/index', '', 'YgbDeviceIotCard', 1, 0, 'C', '0', '0', 'ygb:device:list', 'connection', 'admin', sysdate(), 'admin', sysdate(), '设备物联卡子台账', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4529);

insert into sys_menu select 4530, '芯片库存', 2014, 24, 'deviceChipInventory', 'ygb/deviceChipInventory/index', '', 'YgbDeviceChipInventory', 1, 0, 'C', '0', '0', 'ygb:device:list', 'box', 'admin', sysdate(), 'admin', sysdate(), '设备芯片库存子台账', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4530);

insert into sys_menu select 4531, '电子围栏', 2014, 25, 'deviceGeofence', 'ygb/deviceGeofence/index', '', 'YgbDeviceGeofence', 1, 0, 'C', '0', '0', 'ygb:device:list', 'map-location', 'admin', sysdate(), 'admin', sysdate(), '设备电子围栏子台账', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4531);

insert into sys_menu select 4532, '拆卸报警', 2014, 26, 'deviceUninstallAlert', 'ygb/deviceUninstallAlert/index', '', 'YgbDeviceUninstallAlert', 1, 0, 'C', '0', '0', 'ygb:device:list', 'bell', 'admin', sysdate(), 'admin', sysdate(), '设备拆卸报警子台账', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4532);

update sys_menu
set portal_scope = 'ygb'
where menu_id between 4526 and 4532;

insert into sys_role_menu (role_id, menu_id, portal_scope)
select r.role_id, m.menu_id, 'ygb'
from sys_role r
join sys_menu m on m.menu_id between 4526 and 4532
where r.role_key in ('admin', 'ygb_hrss_supervisor', 'ygb_enterprise_admin', 'ygb_enterprise_operator')
on duplicate key update portal_scope = values(portal_scope);

insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, sort_order, source_label,
  payload_json, remark, create_by, create_time, del_flag
)
select
  920201, 'DEVICE_GEOFENCE', '南山高危作业围栏', 'geofence', '2026-06', 'ygb', 'processing', '0', '440305',
  1002, '深圳鹏城机电工程有限公司', null, '', null, 'DG-FENCE-202606-001', 1, '设备管理',
  '{"fenceType":"polygon","center":"113.9448,22.5428","warningLevel":"1"}',
  '设备电子围栏初始化样例', 'admin', sysdate(), '0'
from dual
where not exists (select 1 from ygb_module_record where record_id = 920201);

insert into t_device_event (
  event_id, device_id, device_code, enterprise_id, enterprise_name, region_code, event_type, event_code, event_content,
  evidence_url, event_status, source_serial_no, source_status, source_message, callback_time, raw_payload, event_time,
  create_by, create_time
)
select
  78003, 76001, 'GD-DEV-001', 1001, '广州南粤人力资源有限公司', '440106', '2', 'UNINSTALL_ALERT', '设备外壳拆卸报警，已触发现场复核',
  'https://stub.local/evidence/device_uninstall.jpg', '0', 'DEV-EVT-0003', 'SUCCESS', '设备拆卸报警 Stub 回写成功',
  date_sub(sysdate(), interval 30 minute), '{"mode":"stub","eventCode":"UNINSTALL_ALERT"}',
  date_sub(sysdate(), interval 30 minute), 'admin', sysdate()
from dual
where not exists (select 1 from t_device_event where event_id = 78003);
