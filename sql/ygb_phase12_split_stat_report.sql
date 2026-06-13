SET NAMES utf8mb4;

DELETE FROM sys_role_menu
WHERE menu_id IN (
  3920, 3921, 3922, 3923, 3924,
  3931, 3932, 3933, 3934,
  3941, 3942, 3943, 3944,
  3951, 3952, 3953, 3954,
  3961, 3962, 3963, 3964,
  4920, 4921, 4922, 4923, 4924,
  4931, 4932, 4933, 4934,
  4941, 4942, 4943, 4944,
  4951, 4952, 4953, 4954,
  4961, 4962, 4963, 4964
);

DELETE FROM sys_menu
WHERE menu_id IN (
  3920, 3921, 3922, 3923, 3924,
  3931, 3932, 3933, 3934,
  3941, 3942, 3943, 3944,
  3951, 3952, 3953, 3954,
  3961, 3962, 3963, 3964,
  4920, 4921, 4922, 4923, 4924,
  4931, 4932, 4933, 4934,
  4941, 4942, 4943, 4944,
  4951, 4952, 4953, 4954,
  4961, 4962, 4963, 4964
);

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon,
  create_by, create_time, update_by, update_time, remark
) VALUES
  (3920, '统计报表', 0, 11, 'ygb-report', NULL, '', '', 1, 0, 'M', '0', '0', 'ygb', '', 'table', 'admin', SYSDATE(), 'admin', SYSDATE(), 'YGB 统计报表目录'),
  (3921, '工伤发生率月报', 3920, 1, 'statReport/injury', 'ygb/statReport/injury/index', '', '', 1, 0, 'C', '0', '0', 'ygb', 'ygb:statReport:injury:query', 'table', 'admin', SYSDATE(), 'admin', SYSDATE(), 'YGB 工伤发生率月报'),
  (3922, '预警治理月报', 3920, 2, 'statReport/warning', 'ygb/statReport/warning/index', '', '', 1, 0, 'C', '0', '0', 'ygb', 'ygb:statReport:warning:query', 'table', 'admin', SYSDATE(), 'admin', SYSDATE(), 'YGB 预警治理月报'),
  (3923, '工资发放月报', 3920, 3, 'statReport/salary', 'ygb/statReport/salary/index', '', '', 1, 0, 'C', '0', '0', 'ygb', 'ygb:statReport:salary:query', 'table', 'admin', SYSDATE(), 'admin', SYSDATE(), 'YGB 工资发放月报'),
  (3924, '社保税务联动月报', 3920, 4, 'statReport/socialTax', 'ygb/statReport/socialTax/index', '', '', 1, 0, 'C', '0', '0', 'ygb', 'ygb:statReport:socialTax:query', 'table', 'admin', SYSDATE(), 'admin', SYSDATE(), 'YGB 社保税务联动月报')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark),
  update_by = 'admin',
  update_time = SYSDATE();

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon,
  create_by, create_time, update_by, update_time, remark
) VALUES
  (3931, '查询', 3921, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:statReport:injury:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (3932, '生成', 3921, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:statReport:injury:generate', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (3933, '导出', 3921, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:statReport:injury:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (3934, '打印', 3921, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:statReport:injury:print', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (3941, '查询', 3922, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:statReport:warning:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (3942, '生成', 3922, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:statReport:warning:generate', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (3943, '导出', 3922, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:statReport:warning:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (3944, '打印', 3922, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:statReport:warning:print', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (3951, '查询', 3923, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:statReport:salary:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (3952, '生成', 3923, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:statReport:salary:generate', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (3953, '导出', 3923, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:statReport:salary:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (3954, '打印', 3923, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:statReport:salary:print', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (3961, '查询', 3924, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:statReport:socialTax:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (3962, '生成', 3924, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:statReport:socialTax:generate', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (3963, '导出', 3924, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:statReport:socialTax:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (3964, '打印', 3924, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb', 'ygb:statReport:socialTax:print', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), '')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  update_by = 'admin',
  update_time = SYSDATE();

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon,
  create_by, create_time, update_by, update_time, remark
) VALUES
  (4920, '治理月报', 0, 12, 'azb-report', NULL, '', '', 1, 0, 'M', '0', '0', 'azb', '', 'table', 'admin', SYSDATE(), 'admin', SYSDATE(), 'AZB 治理月报目录'),
  (4921, '工伤发生率月报', 4920, 1, 'statReport/injury', 'azb/statReport/injury/index', '', '', 1, 0, 'C', '0', '0', 'azb', 'azb:statReport:injury:query', 'table', 'admin', SYSDATE(), 'admin', SYSDATE(), 'AZB 工伤发生率月报'),
  (4922, '预警治理月报', 4920, 2, 'statReport/warning', 'azb/statReport/warning/index', '', '', 1, 0, 'C', '0', '0', 'azb', 'azb:statReport:warning:query', 'table', 'admin', SYSDATE(), 'admin', SYSDATE(), 'AZB 预警治理月报'),
  (4923, '工资发放月报', 4920, 3, 'statReport/salary', 'azb/statReport/salary/index', '', '', 1, 0, 'C', '0', '0', 'azb', 'azb:statReport:salary:query', 'table', 'admin', SYSDATE(), 'admin', SYSDATE(), 'AZB 工资发放月报'),
  (4924, '社保税务联动月报', 4920, 4, 'statReport/socialTax', 'azb/statReport/socialTax/index', '', '', 1, 0, 'C', '0', '0', 'azb', 'azb:statReport:socialTax:query', 'table', 'admin', SYSDATE(), 'admin', SYSDATE(), 'AZB 社保税务联动月报')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark),
  update_by = 'admin',
  update_time = SYSDATE();

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon,
  create_by, create_time, update_by, update_time, remark
) VALUES
  (4931, '查询', 4921, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'azb:statReport:injury:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4932, '生成', 4921, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'azb:statReport:injury:generate', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4933, '导出', 4921, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'azb:statReport:injury:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4934, '打印', 4921, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'azb:statReport:injury:print', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4941, '查询', 4922, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'azb:statReport:warning:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4942, '生成', 4922, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'azb:statReport:warning:generate', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4943, '导出', 4922, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'azb:statReport:warning:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4944, '打印', 4922, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'azb:statReport:warning:print', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4951, '查询', 4923, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'azb:statReport:salary:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4952, '生成', 4923, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'azb:statReport:salary:generate', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4953, '导出', 4923, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'azb:statReport:salary:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4954, '打印', 4923, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'azb:statReport:salary:print', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4961, '查询', 4924, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'azb:statReport:socialTax:query', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4962, '生成', 4924, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'azb:statReport:socialTax:generate', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4963, '导出', 4924, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'azb:statReport:socialTax:export', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), ''),
  (4964, '打印', 4924, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'azb', 'azb:statReport:socialTax:print', '#', 'admin', SYSDATE(), 'admin', SYSDATE(), '')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  portal_scope = VALUES(portal_scope),
  perms = VALUES(perms),
  update_by = 'admin',
  update_time = SYSDATE();

INSERT INTO sys_role_menu (role_id, menu_id, portal_scope)
SELECT r.role_id, m.menu_id, 'ygb'
FROM sys_role r
JOIN sys_menu m ON m.menu_id IN (
  3920, 3921, 3922, 3923, 3924,
  3931, 3932, 3933, 3934,
  3941, 3942, 3943, 3944,
  3951, 3952, 3953, 3954,
  3961, 3962, 3963, 3964
)
WHERE r.role_key IN ('admin', 'ygb_hrss_supervisor', 'ygb_enterprise_admin', 'ygb_enterprise_operator')
ON DUPLICATE KEY UPDATE portal_scope = VALUES(portal_scope);

INSERT INTO sys_role_menu (role_id, menu_id, portal_scope)
SELECT r.role_id, m.menu_id, 'azb'
FROM sys_role r
JOIN sys_menu m ON m.menu_id IN (
  4920, 4921, 4922, 4923, 4924,
  4931, 4932, 4933, 4934,
  4941, 4942, 4943, 4944,
  4951, 4952, 4953, 4954,
  4961, 4962, 4963, 4964
)
WHERE r.role_key IN ('admin', 'ygb_emergency_supervisor', 'ygb_insurer', 'ygb_bank', 'ygb_enterprise_admin', 'ygb_enterprise_operator')
ON DUPLICATE KEY UPDATE portal_scope = VALUES(portal_scope);

-- Phase 12 示例月报数据
DELETE FROM t_stat_report_item
WHERE report_id IN (91101, 91102, 91103, 91104);

DELETE FROM t_stat_report
WHERE report_id IN (91101, 91102, 91103, 91104);

INSERT INTO t_stat_report (
  report_id, report_code, report_name, stat_month, region_code, report_status,
  metric_count, metric_amount, metric_rate, report_summary, attachment_url,
  source_mode, generated_time, del_flag, create_by, create_time, update_by, update_time, remark
) VALUES
  (91101, 'INJURY_RATE', '工伤发生率月报', '2026-06', '440106', '1',
   1, 12.00, 83.33, '2026年6月广州天河区工伤事件1起，参保样本12人，工伤发生率83.33‰。', 'stub://phase12/report/injury/440106/2026-06',
   'stub', '2026-06-10 09:00:00', '0', 'admin', SYSDATE(), 'admin', SYSDATE(), 'Phase 12 示例月报数据'),
  (91102, 'WARNING_OVERVIEW', '预警治理月报', '2026-06', '440305', '1',
   14, 11.00, 78.57, '2026年6月深圳南山区累计预警14条，已闭环11条，闭环率78.57%。', 'stub://phase12/report/warning/440305/2026-06',
   'stub', '2026-06-10 09:10:00', '0', 'admin', SYSDATE(), 'admin', SYSDATE(), 'Phase 12 示例月报数据'),
  (91103, 'SALARY_PAYMENT', '工资发放月报', '2026-06', '440606', '1',
   86, 693240.50, 96.51, '2026年6月佛山顺德区工资发放覆盖86人次，实发金额693240.50元，发放成功率96.51%。', 'stub://phase12/report/salary/440606/2026-06',
   'stub', '2026-06-10 09:20:00', '0', 'admin', SYSDATE(), 'admin', SYSDATE(), 'Phase 12 示例月报数据'),
  (91104, 'SOCIAL_TAX', '社保税务联动月报', '2026-06', '441900', '1',
   9, 9.00, 31.03, '2026年6月东莞市社保税务联动发现风险9项，综合风险率31.03%。', 'stub://phase12/report/socialTax/441900/2026-06',
   'stub', '2026-06-10 09:30:00', '0', 'admin', SYSDATE(), 'admin', SYSDATE(), 'Phase 12 示例月报数据');

INSERT INTO t_stat_report_item (
  item_id, report_id, item_category, item_name, item_dimension,
  metric_count, metric_value, metric_rate, sort_no
) VALUES
  (92101, 91101, 'INJURY_RATE', '广州市天河区', '440106', 1, 12.00, 83.33, 1),
  (92102, 91101, 'INJURY_RATE', '重点制造企业样本', 'MANUFACTURING', 8, 8.00, 125.00, 2),
  (92111, 91102, 'WARNING_OVERVIEW', '税务比对预警', 'TAX', 5, 4.00, 80.00, 1),
  (92112, 91102, 'WARNING_OVERVIEW', '设备联网预警', 'DEVICE', 3, 2.00, 66.67, 2),
  (92113, 91102, 'WARNING_OVERVIEW', '工伤异常预警', 'INJURY', 4, 3.00, 75.00, 3),
  (92114, 91102, 'WARNING_OVERVIEW', '社保参保预警', 'SOCIAL', 2, 2.00, 100.00, 4),
  (92121, 91103, 'SALARY_PAYMENT', '顺德智能装备有限公司', 'BATCH-202606-SD01', 38, 302400.50, 97.37, 1),
  (92122, 91103, 'SALARY_PAYMENT', '顺德供应链服务有限公司', 'BATCH-202606-SD02', 48, 390840.00, 95.83, 2),
  (92131, 91104, 'SOCIAL_TAX', '社保基数异常', 'SOCIAL_ABNORMAL', 2, 2.00, 22.22, 1),
  (92132, 91104, 'SOCIAL_TAX', '个税申报异常', 'TAX_ABNORMAL', 3, 3.00, 33.33, 2),
  (92133, 91104, 'SOCIAL_TAX', '未参保用工', 'UNINSURED', 2, 2.00, 22.22, 3),
  (92134, 91104, 'SOCIAL_TAX', '疑似假外包', 'FAKE_OUTSOURCING', 2, 2.00, 22.22, 4);
