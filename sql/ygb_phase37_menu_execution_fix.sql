SET NAMES utf8mb4;

SET @azb_device_parent_exists := (
  SELECT COUNT(*)
  FROM sys_menu
  WHERE menu_id = 5520
);

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`, route_name,
  is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  5520, '设备管理', 2060, 99, 'device-azb', 'azb/device/index', '', 'AzbDevice',
  1, 0, 'C', '0', '0', 'azb', 'ygb:device:list', 'build',
  'admin', SYSDATE(), 'admin', SYSDATE(), 'AZB 设备管理父菜单'
FROM dual
WHERE @azb_device_parent_exists = 0;

UPDATE sys_menu
SET menu_name = '设备管理',
    parent_id = 2060,
    order_num = 1,
    path = 'device',
    component = 'ygb/device/index',
    route_name = 'YgbDevice',
    is_frame = 1,
    is_cache = 0,
    menu_type = 'C',
    visible = '0',
    status = '0',
    portal_scope = 'ygb',
    perms = 'ygb:device:list',
    icon = 'build',
    update_by = 'admin',
    update_time = SYSDATE(),
    remark = 'YGB 设备管理父菜单'
WHERE menu_id = 2014;

UPDATE sys_menu
SET parent_id = 5520,
    portal_scope = 'azb',
    update_by = 'admin',
    update_time = SYSDATE()
WHERE menu_id BETWEEN 5521 AND 5527;

UPDATE sys_menu
SET parent_id = 5520,
    portal_scope = 'azb',
    update_by = 'admin',
    update_time = SYSDATE()
WHERE menu_id BETWEEN 5530 AND 5539;

INSERT INTO sys_role_menu (role_id, menu_id, portal_scope)
SELECT r.role_id, 5520, 'azb'
FROM sys_role r
WHERE r.role_key IN ('admin', 'ygb_emergency_supervisor', 'ygb_insurer', 'ygb_bank', 'ygb_enterprise_admin', 'ygb_enterprise_operator')
ON DUPLICATE KEY UPDATE portal_scope = VALUES(portal_scope);

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`, route_name,
  is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
VALUES
  (4471, '职业伤害监测', 4000, 3, 'newformInjuryMonitor', 'ygb/newformInjuryMonitor/index', '', 'YgbNewformInjuryMonitor', 1, 0, 'C', '0', '0', 'ygb', 'ygb:newformInjuryMonitor:list', 'warning', 'admin', SYSDATE(), 'admin', SYSDATE(), '新业态职业伤害监测'),
  (4472, '培训管理', 4000, 4, 'newformTraining', 'ygb/newformTraining/index', '', 'YgbNewformTraining', 1, 0, 'C', '0', '0', 'ygb', 'ygb:newformTraining:list', 'reading', 'admin', SYSDATE(), 'admin', SYSDATE(), '新业态培训管理'),
  (4473, '查询', 4470, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:newformPlatform:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4474, '导出', 4470, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:newformPlatform:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4475, '查询', 4471, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:newformInjuryMonitor:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4476, '导出', 4471, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:newformInjuryMonitor:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4477, '查询', 4472, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:newformTraining:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4478, '新增', 4472, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:newformTraining:add', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4479, '修改', 4472, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:newformTraining:edit', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4480, '删除', 4472, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:newformTraining:remove', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4481, '导出', 4472, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:newformTraining:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4482, '职业病预防项目', 4020, 2, 'occupationPrevention', 'ygb/occupationPrevention/index', '', 'YgbOccupationPrevention', 1, 0, 'C', '0', '0', 'ygb', 'ygb:occupationPrevention:list', 'collection', 'admin', SYSDATE(), 'admin', SYSDATE(), '职业病预防项目'),
  (4483, '职业健康档案', 4020, 3, 'occupationHealthArchive', 'ygb/occupationHealthArchive/index', '', 'YgbOccupationHealthArchive', 1, 0, 'C', '0', '0', 'ygb', 'ygb:occupationHealthArchive:list', 'folder-opened', 'admin', SYSDATE(), 'admin', SYSDATE(), '职业健康档案'),
  (4484, '查询', 4482, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:occupationPrevention:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4485, '导出', 4482, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:occupationPrevention:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4486, '查询', 4483, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:occupationHealthArchive:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4487, '导出', 4483, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:occupationHealthArchive:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4491, '黑名单', 2002, 21, 'personBlacklist', 'ygb/personBlacklist/index', '', 'YgbPersonBlacklist', 1, 0, 'C', '0', '0', 'ygb', 'ygb:personBlacklist:list', 'warning-filled', 'admin', SYSDATE(), 'admin', SYSDATE(), '人员黑名单'),
  (4492, '培训监督', 2002, 22, 'personTraining', 'ygb/personTraining/index', '', 'YgbPersonTraining', 1, 0, 'C', '0', '0', 'ygb', 'ygb:personTraining:list', 'reading', 'admin', SYSDATE(), 'admin', SYSDATE(), '人员培训监督'),
  (4493, '高危岗位库', 2002, 23, 'personHighRiskPost', 'ygb/personHighRiskPost/index', '', 'YgbPersonHighRiskPost', 1, 0, 'C', '0', '0', 'ygb', 'ygb:personHighRiskPost:list', 'histogram', 'admin', SYSDATE(), 'admin', SYSDATE(), '人员高危岗位库'),
  (4494, '风险岗位库', 2002, 24, 'personRiskPost', 'ygb/personRiskPost/index', '', 'YgbPersonRiskPost', 1, 0, 'C', '0', '0', 'ygb', 'ygb:personRiskPost:list', 'data-line', 'admin', SYSDATE(), 'admin', SYSDATE(), '人员风险岗位库'),
  (4495, '专家库', 2002, 25, 'personExpert', 'ygb/personExpert/index', '', 'YgbPersonExpert', 1, 0, 'C', '0', '0', 'ygb', 'ygb:personExpert:list', 'user', 'admin', SYSDATE(), 'admin', SYSDATE(), '人员专家库'),
  (4496, '查询', 4490, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personCertificate:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4497, '新增', 4490, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personCertificate:add', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4498, '修改', 4490, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personCertificate:edit', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4499, '删除', 4490, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personCertificate:remove', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4500, '导出', 4490, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personCertificate:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4501, '查询', 4491, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personBlacklist:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4502, '新增', 4491, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personBlacklist:add', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4503, '修改', 4491, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personBlacklist:edit', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4504, '删除', 4491, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personBlacklist:remove', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4505, '导出', 4491, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personBlacklist:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4506, '查询', 4492, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personTraining:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4507, '新增', 4492, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personTraining:add', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4508, '修改', 4492, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personTraining:edit', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4509, '删除', 4492, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personTraining:remove', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4510, '导出', 4492, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personTraining:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4511, '查询', 4493, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personHighRiskPost:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4512, '新增', 4493, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personHighRiskPost:add', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4513, '修改', 4493, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personHighRiskPost:edit', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4514, '删除', 4493, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personHighRiskPost:remove', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4515, '导出', 4493, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personHighRiskPost:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4516, '查询', 4494, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personRiskPost:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4517, '新增', 4494, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personRiskPost:add', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4518, '修改', 4494, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personRiskPost:edit', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4519, '删除', 4494, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personRiskPost:remove', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4520, '导出', 4494, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personRiskPost:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4521, '查询', 4495, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personExpert:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4522, '新增', 4495, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personExpert:add', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4523, '修改', 4495, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personExpert:edit', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4524, '删除', 4495, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personExpert:remove', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4525, '导出', 4495, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:personExpert:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4527, '芯片设备', 2014, 21, 'deviceChip', 'ygb/deviceChip/index', '', 'YgbDeviceChip', 1, 0, 'C', '0', '0', 'ygb', 'ygb:device:list', 'cpu', 'admin', SYSDATE(), 'admin', SYSDATE(), '设备芯片子视图'),
  (4528, 'AI设备', 2014, 22, 'deviceAi', 'ygb/deviceAi/index', '', 'YgbDeviceAi', 1, 0, 'C', '0', '0', 'ygb', 'ygb:device:list', 'video-camera', 'admin', SYSDATE(), 'admin', SYSDATE(), '设备AI子视图'),
  (4529, '物联卡', 2014, 23, 'deviceIotCard', 'ygb/deviceIotCard/index', '', 'YgbDeviceIotCard', 1, 0, 'C', '0', '0', 'ygb', 'ygb:device:list', 'connection', 'admin', SYSDATE(), 'admin', SYSDATE(), '设备物联卡子台账'),
  (4530, '芯片库存', 2014, 24, 'deviceChipInventory', 'ygb/deviceChipInventory/index', '', 'YgbDeviceChipInventory', 1, 0, 'C', '0', '0', 'ygb', 'ygb:device:list', 'box', 'admin', SYSDATE(), 'admin', SYSDATE(), '设备芯片库存子台账'),
  (4531, '电子围栏', 2014, 25, 'deviceGeofence', 'ygb/deviceGeofence/index', '', 'YgbDeviceGeofence', 1, 0, 'C', '0', '0', 'ygb', 'ygb:device:list', 'map-location', 'admin', SYSDATE(), 'admin', SYSDATE(), '设备电子围栏子台账'),
  (4532, '拆卸报警', 2014, 26, 'deviceUninstallAlert', 'ygb/deviceUninstallAlert/index', '', 'YgbDeviceUninstallAlert', 1, 0, 'C', '0', '0', 'ygb', 'ygb:device:list', 'bell', 'admin', SYSDATE(), 'admin', SYSDATE(), '设备拆卸报警子台账'),
  (4534, '劳务派遣公司', 2001, 21, 'enterpriseDispatch', 'ygb/enterpriseDispatch/index', '', 'YgbEnterpriseDispatch', 1, 0, 'C', '0', '0', 'ygb', 'ygb:enterprise:list', 'connection', 'admin', SYSDATE(), 'admin', SYSDATE(), '单位管理-劳务派遣公司'),
  (4535, '用工单位', 2001, 22, 'enterpriseEmployer', 'ygb/enterpriseEmployer/index', '', 'YgbEnterpriseEmployer', 1, 0, 'C', '0', '0', 'ygb', 'ygb:enterprise:list', 'suitcase', 'admin', SYSDATE(), 'admin', SYSDATE(), '单位管理-用工单位'),
  (4536, '高危企业库', 2001, 23, 'enterpriseHighRisk', 'ygb/enterpriseHighRisk/index', '', 'YgbEnterpriseHighRisk', 1, 0, 'C', '0', '0', 'ygb', 'ygb:enterpriseHighRisk:list', 'warning', 'admin', SYSDATE(), 'admin', SYSDATE(), '单位管理-高危企业库')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
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

INSERT INTO sys_role_menu (role_id, menu_id, portal_scope)
SELECT r.role_id, m.menu_id, 'ygb'
FROM sys_role r
JOIN sys_menu m ON m.menu_id IN (
  2014, 3400, 3401, 3402, 3403, 3404, 3405, 3406, 3407, 3408, 3409,
  4470, 4471, 4472, 4473, 4474, 4475, 4476, 4477, 4478, 4479, 4480, 4481, 4482, 4483, 4484, 4485, 4486, 4487,
  4490, 4491, 4492, 4493, 4494, 4495, 4496, 4497, 4498, 4499, 4500, 4501, 4502, 4503, 4504, 4505,
  4506, 4507, 4508, 4509, 4510, 4511, 4512, 4513, 4514, 4515, 4516, 4517, 4518, 4519, 4520, 4521, 4522, 4523, 4524, 4525,
  4526, 4527, 4528, 4529, 4530, 4531, 4532, 4533, 4534, 4535, 4536
)
WHERE r.role_key IN ('admin', 'ygb_hrss_supervisor', 'ygb_enterprise_admin', 'ygb_enterprise_operator', 'ygb_emergency_supervisor')
ON DUPLICATE KEY UPDATE portal_scope = VALUES(portal_scope);

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`, route_name,
  is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
VALUES
  (4303, '岗位审核', 4300, 3, 'jobReview', 'ygb/operation/jobReview/index', '', 'YgbOperationJobReview', 1, 0, 'C', '0', '0', 'ygb', 'ygb:operationJobReview:list', 'tickets', 'admin', SYSDATE(), 'admin', SYSDATE(), '岗位审核台账'),
  (4304, '简历管理', 4300, 4, 'resume', 'ygb/operation/resume/index', '', 'YgbOperationResume', 1, 0, 'C', '0', '0', 'ygb', 'ygb:operationResume:list', 'user', 'admin', SYSDATE(), 'admin', SYSDATE(), '简历管理台账'),
  (4305, '广告轮播', 4300, 5, 'banner', 'ygb/portalContent/index', '{"portalCode":"ygb","sectionCode":"banner"}', 'YgbOperationBanner', 1, 0, 'C', '0', '0', 'ygb', 'ygb:portalContent:list', 'picture', 'admin', SYSDATE(), 'admin', SYSDATE(), '广告轮播内容管理'),
  (4306, '消息推送', 4300, 6, 'message', 'ygb/operation/message/index', '', 'YgbOperationMessage', 1, 0, 'C', '0', '0', 'ygb', 'ygb:operationMessage:list', 'message', 'admin', SYSDATE(), 'admin', SYSDATE(), '运营消息台账'),
  (4311, '查询', 4302, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:operationEnterpriseReview:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4312, '新增', 4302, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:operationEnterpriseReview:add', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4313, '修改', 4302, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:operationEnterpriseReview:edit', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4314, '删除', 4302, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:operationEnterpriseReview:remove', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4315, '导出', 4302, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:operationEnterpriseReview:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4321, '查询', 4303, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:operationJobReview:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4322, '导出', 4303, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:operationJobReview:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4331, '查询', 4304, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:operationResume:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4332, '导出', 4304, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:operationResume:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4341, '查询', 4306, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:operationMessage:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4342, '新增', 4306, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:operationMessage:add', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4343, '修改', 4306, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:operationMessage:edit', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4344, '删除', 4306, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:operationMessage:remove', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4345, '导出', 4306, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:operationMessage:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4400, '平台治理', 0, 31, 'platform', '', '', '', 1, 0, 'M', '0', '0', 'ygb', '', 'monitor', 'admin', SYSDATE(), 'admin', SYSDATE(), '平台治理菜单'),
  (4401, '治理总览', 4400, 1, 'overview', 'ygb/platform/overview/index', '', 'YgbPlatformOverview', 1, 0, 'C', '0', '0', 'ygb', 'ygb:platformRuntime:query', 'dashboard', 'admin', SYSDATE(), 'admin', SYSDATE(), '平台治理总览'),
  (4402, '文档管理', 4400, 2, 'document', 'ygb/platform/document/index', '', 'YgbPlatformDocument', 1, 0, 'C', '0', '0', 'ygb', 'ygb:platformDocument:list', 'documentation', 'admin', SYSDATE(), 'admin', SYSDATE(), '文档管理台账'),
  (4403, '交换监控', 4400, 3, 'exchange', 'ygb/platform/exchange/index', '', 'YgbPlatformExchange', 1, 0, 'C', '0', '0', 'ygb', 'ygb:platformExchange:list', 'connection', 'admin', SYSDATE(), 'admin', SYSDATE(), '交换监控台账'),
  (4404, '安全审计', 4400, 4, 'securityAudit', 'ygb/platform/securityAudit/index', '', 'YgbPlatformSecurityAudit', 1, 0, 'C', '0', '0', 'ygb', 'ygb:platformSecurityAudit:list', 'lock', 'admin', SYSDATE(), 'admin', SYSDATE(), '安全审计台账'),
  (4405, '备份恢复', 4400, 5, 'backup', 'ygb/platform/backup/index', '', 'YgbPlatformBackup', 1, 0, 'C', '0', '0', 'ygb', 'ygb:platformBackup:list', 'refresh', 'admin', SYSDATE(), 'admin', SYSDATE(), '备份恢复台账'),
  (4411, '查询', 4402, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:platformDocument:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4412, '新增', 4402, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:platformDocument:add', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4413, '修改', 4402, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:platformDocument:edit', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4414, '删除', 4402, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:platformDocument:remove', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4415, '导出', 4402, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:platformDocument:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4421, '查询', 4403, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:platformExchange:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4422, '新增', 4403, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:platformExchange:add', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4423, '修改', 4403, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:platformExchange:edit', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4424, '删除', 4403, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:platformExchange:remove', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4425, '导出', 4403, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:platformExchange:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4431, '查询', 4404, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:platformSecurityAudit:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4432, '新增', 4404, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:platformSecurityAudit:add', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4433, '修改', 4404, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:platformSecurityAudit:edit', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4434, '删除', 4404, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:platformSecurityAudit:remove', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4435, '导出', 4404, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:platformSecurityAudit:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4441, '查询', 4405, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:platformBackup:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4442, '新增', 4405, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:platformBackup:add', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4443, '修改', 4405, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:platformBackup:edit', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4444, '删除', 4405, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:platformBackup:remove', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4445, '导出', 4405, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:platformBackup:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4460, '赔付率监控', 3960, 4, 'claim', 'ygb/aqInsuranceClaim/index', '', 'YgbAqInsuranceClaim', 1, 0, 'C', '0', '0', 'ygb', 'ygb:aqInsuranceClaim:list', 'data-analysis', 'admin', SYSDATE(), 'admin', SYSDATE(), '安责险赔付率监控'),
  (4461, '查询', 4460, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:aqInsuranceClaim:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4462, '新增', 4460, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:aqInsuranceClaim:add', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4463, '修改', 4460, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:aqInsuranceClaim:edit', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4464, '删除', 4460, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:aqInsuranceClaim:remove', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4465, '导出', 4460, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:aqInsuranceClaim:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), '')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
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

INSERT INTO sys_role_menu (role_id, menu_id, portal_scope)
SELECT r.role_id, m.menu_id, 'ygb'
FROM sys_role r
JOIN sys_menu m ON m.menu_id IN (
  4300, 4301, 4302, 4303, 4304, 4305, 4306,
  4311, 4312, 4313, 4314, 4315,
  4321, 4322, 4331, 4332,
  4341, 4342, 4343, 4344, 4345,
  4400, 4401, 4402, 4403, 4404, 4405,
  4411, 4412, 4413, 4414, 4415,
  4421, 4422, 4423, 4424, 4425,
  4431, 4432, 4433, 4434, 4435,
  4441, 4442, 4443, 4444, 4445,
  4460, 4461, 4462, 4463, 4464, 4465
)
WHERE r.role_key IN ('admin', 'ygb_hrss_supervisor', 'ygb_emergency_supervisor')
ON DUPLICATE KEY UPDATE portal_scope = VALUES(portal_scope);
