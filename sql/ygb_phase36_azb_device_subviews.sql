SET NAMES utf8mb4;

SET @azb_device_parent_id := (
  SELECT menu_id
  FROM sys_menu
  WHERE menu_type IN ('M', 'C')
    AND portal_scope IN ('azb', 'both')
    AND (
      component = 'azb/device/index'
      OR component = 'ygb/device/index'
      OR route_name = 'AzbDevice'
      OR path = 'device'
    )
  ORDER BY CASE WHEN portal_scope = 'azb' THEN 0 ELSE 1 END, menu_id
  LIMIT 1
);

SET @azb_device_root_id := (
  SELECT menu_id
  FROM sys_menu
  WHERE menu_type = 'M'
    AND portal_scope IN ('azb', 'both')
    AND (
      menu_name = '设备工坊'
      OR path = 'deviceWorkshop'
      OR path = 'ygb-safety'
      OR path = 'azb-safety'
    )
  ORDER BY CASE WHEN portal_scope = 'azb' THEN 0 ELSE 1 END, menu_id
  LIMIT 1
);

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`, route_name,
  is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  5520, '设备管理', COALESCE(@azb_device_root_id, 0), 1, 'device', 'azb/device/index', '', 'AzbDevice',
  1, 0, 'C', '0', '0', 'azb', 'ygb:device:list', 'build',
  'admin', SYSDATE(), 'admin', SYSDATE(), 'AZB 设备管理菜单'
FROM dual
WHERE @azb_device_parent_id IS NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 5520);

SET @azb_device_parent_id := COALESCE(@azb_device_parent_id, 5520);

UPDATE sys_menu
SET parent_id = COALESCE(@azb_device_root_id, parent_id),
    order_num = 1,
    path = 'device',
    component = 'azb/device/index',
    route_name = 'AzbDevice',
    menu_type = 'C',
    visible = '0',
    status = '0',
    portal_scope = 'azb',
    perms = 'ygb:device:list',
    icon = 'build',
    update_by = 'admin',
    update_time = SYSDATE(),
    remark = 'AZB 设备管理菜单'
WHERE menu_id = @azb_device_parent_id;

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`, route_name,
  is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
VALUES
  (5521, '考勤设备', @azb_device_parent_id, 20, 'deviceAttendance', 'azb/deviceAttendance/index', '', 'AzbDeviceAttendance', 1, 0, 'C', '0', '0', 'azb', 'ygb:device:list', 'monitor', 'admin', SYSDATE(), 'admin', SYSDATE(), 'AZB 设备考勤子视图'),
  (5522, '芯片设备', @azb_device_parent_id, 21, 'deviceChip', 'azb/deviceChip/index', '', 'AzbDeviceChip', 1, 0, 'C', '0', '0', 'azb', 'ygb:device:list', 'cpu', 'admin', SYSDATE(), 'admin', SYSDATE(), 'AZB 设备芯片子视图'),
  (5523, 'AI设备', @azb_device_parent_id, 22, 'deviceAi', 'azb/deviceAi/index', '', 'AzbDeviceAi', 1, 0, 'C', '0', '0', 'azb', 'ygb:device:list', 'video-camera', 'admin', SYSDATE(), 'admin', SYSDATE(), 'AZB 设备AI子视图'),
  (5524, '物联卡', @azb_device_parent_id, 23, 'deviceIotCard', 'azb/deviceIotCard/index', '', 'AzbDeviceIotCard', 1, 0, 'C', '0', '0', 'azb', 'ygb:device:list', 'connection', 'admin', SYSDATE(), 'admin', SYSDATE(), 'AZB 设备物联卡子台账'),
  (5525, '芯片库存', @azb_device_parent_id, 24, 'deviceChipInventory', 'azb/deviceChipInventory/index', '', 'AzbDeviceChipInventory', 1, 0, 'C', '0', '0', 'azb', 'ygb:device:list', 'box', 'admin', SYSDATE(), 'admin', SYSDATE(), 'AZB 设备芯片库存子台账'),
  (5526, '电子围栏', @azb_device_parent_id, 25, 'deviceGeofence', 'azb/deviceGeofence/index', '', 'AzbDeviceGeofence', 1, 0, 'C', '0', '0', 'azb', 'ygb:device:list', 'map-location', 'admin', SYSDATE(), 'admin', SYSDATE(), 'AZB 设备电子围栏子台账'),
  (5527, '拆卸报警', @azb_device_parent_id, 26, 'deviceUninstallAlert', 'azb/deviceUninstallAlert/index', '', 'AzbDeviceUninstallAlert', 1, 0, 'C', '0', '0', 'azb', 'ygb:device:list', 'bell', 'admin', SYSDATE(), 'admin', SYSDATE(), 'AZB 设备拆卸报警子台账')
ON DUPLICATE KEY UPDATE
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  route_name = VALUES(route_name),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  update_by = 'admin',
  update_time = SYSDATE(),
  remark = VALUES(remark);

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`, route_name,
  is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
VALUES
  (5530, '设备查询', @azb_device_parent_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'ygb:device:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (5531, '设备新增', @azb_device_parent_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'ygb:device:add', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (5532, '设备修改', @azb_device_parent_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'ygb:device:edit', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (5533, '设备删除', @azb_device_parent_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'ygb:device:remove', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (5534, '设备导出', @azb_device_parent_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'ygb:device:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (5535, '锁机', @azb_device_parent_id, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'ygb:device:lock', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (5536, '解锁', @azb_device_parent_id, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'ygb:device:unlock', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (5537, '授权', @azb_device_parent_id, 8, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'ygb:device:authorize', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (5538, '模拟心跳', @azb_device_parent_id, 9, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'ygb:device:heartbeat', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (5539, '模拟AI事件', @azb_device_parent_id, 10, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'ygb:device:aiEvent', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), '')
ON DUPLICATE KEY UPDATE
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  update_by = 'admin',
  update_time = SYSDATE();

INSERT INTO sys_role_menu (role_id, menu_id, portal_scope)
SELECT r.role_id, m.menu_id, 'azb'
FROM sys_role r
JOIN sys_menu m ON m.menu_id IN (
  @azb_device_parent_id,
  5521, 5522, 5523, 5524, 5525, 5526, 5527,
  5530, 5531, 5532, 5533, 5534, 5535, 5536, 5537, 5538, 5539
)
WHERE r.role_key IN ('admin', 'ygb_emergency_supervisor', 'ygb_insurer', 'ygb_bank', 'ygb_enterprise_admin', 'ygb_enterprise_operator')
ON DUPLICATE KEY UPDATE portal_scope = VALUES(portal_scope);
