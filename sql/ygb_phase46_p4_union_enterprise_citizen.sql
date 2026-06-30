set names utf8mb4;

-- =============================================================================
-- Phase 46: P4 union management + enterprise portal + citizen service menus
-- (同步为文档 23 模块结构：工会管理并入单位管理，企业后台/便民服务作为一级目录)
-- =============================================================================


INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6150', '工会管理', '0', '23', 'ygb-union', NULL, '', '', '1', '0', 'M', '1', '0', 'ygb', '', 'peoples', 'admin', '2026-06-16 08:17:31', 'admin', '2026-06-27 14:45:59', '17.7 工会管理目录'
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6151', '工会组织信息', '7017', '1', 'unionOrg', 'ygb/unionOrg/index', '', 'YgbUnionOrg', '1', '0', 'C', '0', '0', 'ygb', 'ygb:unionOrg:list', 'build', 'admin', '2026-06-16 08:17:31', 'admin', '2026-06-27 14:45:59', '17.7.1 工会组织信息管理'
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6152', '工会监督台账', '7017', '2', 'unionSupervision', 'ygb/unionSupervision/index', '', 'YgbUnionSupervision', '1', '0', 'C', '0', '0', 'ygb', 'ygb:unionSupervision:list', 'documentation', 'admin', '2026-06-16 08:17:31', 'admin', '2026-06-27 14:45:59', '17.7.2 工会监督台账'
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6153', '职工投诉处理', '6150', '3', 'unionComplaint', 'ygb/workerComplaint/index', '', 'YgbUnionComplaint', '1', '0', 'C', '0', '0', 'ygb', 'ygb:workerMessage:list', 'message', 'admin', '2026-06-16 08:17:31', 'admin', '2026-06-16 08:17:31', '17.7.3 职工投诉处理（复用劳动者投诉）'
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6154', '法律援助服务', '7017', '4', 'unionLegalAid', 'ygb/unionLegalAid/index', '', 'YgbUnionLegalAid', '1', '0', 'C', '0', '0', 'ygb', 'ygb:unionLegalAid:list', 'guide', 'admin', '2026-06-16 08:17:31', 'admin', '2026-06-27 14:45:59', '17.7.4 法律援助服务'
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6155', '集体协商管理', '7017', '5', 'unionNegotiation', 'ygb/unionNegotiation/index', '', 'YgbUnionNegotiation', '1', '0', 'C', '0', '0', 'ygb', 'ygb:unionNegotiation:list', 'edit', 'admin', '2026-06-16 08:17:31', 'admin', '2026-06-27 14:45:59', '17.7.5 集体协商管理'
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6156', '工伤预防监督', '7017', '6', 'unionPreventionSupervision', 'ygb/unionPreventionSupervision/index', '', 'YgbUnionPreventionSupervision', '1', '0', 'C', '0', '0', 'ygb', 'ygb:unionPreventionSupervision:list', 'bug', 'admin', '2026-06-16 08:17:31', 'admin', '2026-06-27 14:45:59', '17.7.6 工伤预防监督'
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6157', '职工权益宣传', '7017', '7', 'unionRightsPublicity', 'ygb/portalContent/index', '{\"portalCode\":\"ygb\",\"sectionCode\":\"union\"}', 'YgbUnionRightsPublicity', '1', '0', 'C', '0', '0', 'ygb', 'ygb:portalContent:list', 'star', 'admin', '2026-06-16 08:17:31', 'admin', '2026-06-27 14:45:59', '17.7.7 职工权益宣传（复用门户CMS）'
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6170', '企业后台', '0', '32', 'enterprise-portal', NULL, '', '', '1', '0', 'M', '1', '0', 'ygb', '', 'build', 'admin', '2026-06-16 08:17:31', 'admin', '2026-06-27 14:45:59', '20 企业后台目录'
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6171', '企业仪表盘', '7020', '1', 'overview', 'ygb/enterprisePortal/dashboard/index', '', 'YgbEnterprisePortalOverview', '1', '0', 'C', '0', '0', 'ygb', 'ygb:enterprise:list', 'dashboard', 'admin', '2026-06-16 08:17:31', 'admin', '2026-06-27 14:46:46', '20.1 企业仪表盘（新）'
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6172', '企业人员管理', '7020', '2', 'enterprisePerson', 'ygb/person/index', '', 'YgbEnterprisePortalPerson', '1', '0', 'C', '0', '0', 'ygb', 'ygb:person:list', 'user', 'admin', '2026-06-16 08:17:31', 'admin', '2026-06-27 14:45:59', '20.2 企业人员管理'
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6173', '企业设备管理', '7020', '3', 'enterpriseDevice', 'ygb/device/index', '', 'YgbEnterprisePortalDevice', '1', '0', 'C', '0', '0', 'ygb', 'ygb:device:list', 'component', 'admin', '2026-06-16 08:17:31', 'admin', '2026-06-27 14:45:59', '20.3 企业设备管理'
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6174', '企业工资管理', '7020', '4', 'enterpriseSalary', 'ygb/salaryBatch/index', '', 'YgbEnterprisePortalSalary', '1', '0', 'C', '0', '0', 'ygb', 'ygb:salaryBatch:list', 'money', 'admin', '2026-06-16 08:17:31', 'admin', '2026-06-27 14:45:59', '20.4 企业工资管理'
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6175', '企业作业管理', '7020', '5', 'enterpriseWork', 'ygb/heightWorkReport/index', '', 'YgbEnterprisePortalWork', '1', '0', 'C', '0', '0', 'ygb', 'ygb:heightWorkReport:query', 'guide', 'admin', '2026-06-16 08:17:31', 'admin', '2026-06-27 14:45:59', '20.5 企业作业管理'
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6176', '企业保险管理', '7020', '6', 'enterpriseInsurance', 'ygb/aqInsurance/index', '', 'YgbEnterprisePortalInsurance', '1', '0', 'C', '0', '0', 'ygb', 'ygb:aqInsurance:list', 'post', 'admin', '2026-06-16 08:17:31', 'admin', '2026-06-27 14:45:59', '20.6 企业保险管理'
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6177', '企业培训管理', '7020', '7', 'enterpriseTraining', 'ygb/preventionTraining/index', '', 'YgbEnterprisePortalTraining', '1', '0', 'C', '0', '0', 'ygb', 'ygb:preventionTraining:list', 'education', 'admin', '2026-06-16 08:17:31', 'admin', '2026-06-27 14:45:59', '20.7 企业培训管理'
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6178', '企业招聘管理', '7020', '8', 'enterpriseRecruit', 'ygb/workerJob/index', '', 'YgbEnterprisePortalRecruit', '1', '0', 'C', '0', '0', 'ygb', 'ygb:workerJob:list', 'post', 'admin', '2026-06-16 08:17:31', 'admin', '2026-06-27 14:45:59', '20.8 企业招聘管理'
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6179', '企业财务管理', '7020', '9', 'enterpriseFinance', 'ygb/deviceIotCard/index', '', 'YgbEnterprisePortalFinance', '1', '0', 'C', '0', '0', 'ygb', 'ygb:deviceIotCard:list', 'money', 'admin', '2026-06-16 08:17:31', 'admin', '2026-06-27 14:45:59', '20.9 企业财务管理（物联卡）'
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6180', '企业信用报告', '7020', '10', 'enterpriseCredit', 'ygb/creditScore/index', '', 'YgbEnterprisePortalCredit', '1', '0', 'C', '0', '0', 'ygb', 'ygb:creditScore:list', 'chart', 'admin', '2026-06-16 08:17:31', 'admin', '2026-06-27 14:45:59', '20.10 企业信用报告'
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6190', '便民服务', '0', '24', 'citizen-service', NULL, '', '', '1', '0', 'M', '1', '0', 'ygb', '', 'guide', 'admin', '2026-06-16 08:17:31', 'admin', '2026-06-27 14:45:59', '21 便民服务目录'
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6191', '便民服务总览', '7021', '1', 'overview', 'ygb/citizenService/overview/index', '', 'YgbCitizenServiceOverview', '1', '0', 'C', '0', '0', 'ygb', 'ygb:portalContent:list', 'dashboard', 'admin', '2026-06-16 08:17:31', 'admin', '2026-06-27 14:45:59', '便民服务入口总览'
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6192', '暖新地图', '7021', '2', 'warmMap', 'ygb/citizenService/warmMap/index', '', 'YgbCitizenWarmMap', '1', '0', 'C', '0', '0', 'ygb', 'ygb:portalContent:list', 'map-location', 'admin', '2026-06-16 08:17:31', 'admin', '2026-06-27 14:46:46', '21.1 暖新地图（展示页）'
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6193', '培训课程', '7021', '3', 'trainingCourse', 'ygb/citizenService/trainingCourse/index', '', 'YgbCitizenTrainingCourse', '1', '0', 'C', '0', '0', 'ygb', 'ygb:portalContent:list', 'education', 'admin', '2026-06-16 08:17:31', 'admin', '2026-06-27 14:46:46', '21.2 培训课程（展示页）'
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6194', '法规库', '7021', '4', 'lawLibrary', 'ygb/citizenService/lawLibrary/index', '', 'YgbCitizenLawLibrary', '1', '0', 'C', '0', '0', 'ygb', 'ygb:portalContent:list', 'document', 'admin', '2026-06-16 08:17:31', 'admin', '2026-06-27 14:46:46', '21.3 法规库（展示页）'
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6195', '互助圈', '7021', '5', 'mutualHelp', 'ygb/citizenService/mutualHelp/index', '', 'YgbCitizenMutualHelp', '1', '0', 'C', '0', '0', 'ygb', 'ygb:portalContent:list', 'message', 'admin', '2026-06-16 08:17:31', 'admin', '2026-06-27 14:46:46', '21.4 互助圈（展示页）'
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6196', '招聘用工市场', '7021', '6', 'recruitMarket', 'ygb/citizenService/recruitMarket/index', '', 'YgbCitizenRecruitMarket', '1', '0', 'C', '0', '0', 'ygb', 'ygb:portalContent:list', 'post', 'admin', '2026-06-16 08:17:31', 'admin', '2026-06-27 14:46:46', '21.5 招聘用工市场（展示页）'
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);

INSERT INTO `sys_menu` (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark
) VALUES (
  '6200', '查询', '6151', '1', '', '', '', '', '1', '0', 'F', '0', '0', 'ygb', 'ygb:unionOrg:query', '#', 'admin', '2026-06-16 08:17:31', '', NULL, ''
)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark);
