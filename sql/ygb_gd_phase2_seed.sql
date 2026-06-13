-- 粤工保二期广东测试数据
-- 依赖前置：
-- 1. ygb_phase1_enterprise_person.sql
-- 2. ygb_phase1_contract.sql
-- 3. ygb_phase1_attendance.sql
-- 4. ygb_phase1_salary.sql
-- 5. ygb_phase2_regulation_device_warning.sql

replace into t_warning_rule
    (rule_id, rule_name, warn_level, source_module, condition_text, push_targets, timeout_minutes, upgrade_level, rule_status, create_by, create_time, remark)
values
    (81001, '社保基数差异预警', '2', 'SOCIAL', 'diff_ratio > 20', 'district_regulator,enterprise_admin', 240, '3', '1', 'admin', sysdate(), '工资实发与社保基数差异超过20%'),
    (81002, '个税收入差异预警', '2', 'TAX', 'diff_ratio > 10', 'district_regulator,enterprise_admin', 240, '3', '1', 'admin', sysdate(), '工资实发与个税申报差异超过10%'),
    (81003, '设备授权失败预警', '3', 'DEVICE', 'cert_valid = false or insured = false', 'district_regulator,enterprise_admin', 120, '3', '1', 'admin', sysdate(), '高危作业设备授权失败'),
    (81004, '漏保识别预警', '2', 'EXPANSION', 'tax_exists = true and social_exists = false', 'district_regulator,enterprise_admin', 180, '3', '1', 'admin', sysdate(), '税务有申报但社保无参保记录'),
    (81005, '用工比例预警', '3', 'SPECIAL', 'dispatch_ratio > 15', 'district_regulator,enterprise_admin', 180, '3', '1', 'admin', sysdate(), '派遣比例超过法定阈值'),
    (81006, '设备AI事件预警', '2', 'DEVICE', 'event_code in (AI_ALERT,PPE_MISSING)', 'district_regulator,enterprise_admin', 60, '3', '1', 'admin', sysdate(), '设备识别到异常作业行为'),
    (81007, '工伤超期预警', '2', 'INJURY', 'approval_deadline < current_date', 'district_regulator,enterprise_admin', 120, '3', '1', 'admin', sysdate(), '工伤事件超期未处理');

replace into t_social_payment
    (payment_id, stat_month, enterprise_id, enterprise_name, person_id, person_name, id_card, region_code, base_amount, paid_amount, payment_status,
     source_serial_no, source_status, source_message, callback_time, raw_payload, create_by, create_time, remark)
values
    (70001, '2026-05', 1001, '广州南粤人力资源有限公司', 10001, '赵志成', '440106199001010011', '440106', 5900.00, 1416.00, '1',
     'SOC-202605-0001', 'SUCCESS', '社保 Stub 同步成功', sysdate(), '{"mode":"stub","insured":true}', 'admin', sysdate(), '广州白云项目参保正常'),
    (70002, '2026-05', 1001, '广州南粤人力资源有限公司', 10002, '梁宇峰', '440106199202020022', '440106', 5000.00, 1200.00, '1',
     'SOC-202605-0002', 'SUCCESS', '社保 Stub 同步成功', sysdate(), '{"mode":"stub","insured":true}', 'admin', sysdate(), '工资偏高需基数比对'),
    (70003, '2026-05', 1002, '深圳鹏城机电工程有限公司', 10005, '刘海鹏', '440305199105050055', '440305', 7200.00, 1728.00, '1',
     'SOC-202605-0003', 'SUCCESS', '社保 Stub 同步成功', sysdate(), '{"mode":"stub","insured":true}', 'admin', sysdate(), '深圳项目参保正常');

replace into t_social_base_compare
    (compare_id, stat_month, enterprise_id, enterprise_name, person_id, person_name, id_card, region_code, salary_amount, social_base_amount, diff_ratio,
     compare_result, warning_status, create_by, create_time, remark)
values
    (71001, '2026-05', 1001, '广州南粤人力资源有限公司', 10001, '赵志成', '440106199001010011', '440106', 6000.00, 5900.00, 1.69,
     '1', '0', 'admin', sysdate(), '差异正常'),
    (71002, '2026-05', 1001, '广州南粤人力资源有限公司', 10002, '梁宇峰', '440106199202020022', '440106', 6800.00, 5000.00, 36.00,
     '2', '1', 'admin', sysdate(), '差异超过20%，已触发预警'),
    (71003, '2026-05', 1002, '深圳鹏城机电工程有限公司', 10005, '刘海鹏', '440305199105050055', '440305', 7200.00, 7200.00, 0.00,
     '1', '0', 'admin', sysdate(), '差异正常');

replace into t_tax_compare
    (compare_id, stat_month, enterprise_id, enterprise_name, person_id, person_name, id_card, region_code, salary_amount, declared_amount, diff_ratio,
     compare_result, warning_status, source_serial_no, source_status, source_message, callback_time, raw_payload, create_by, create_time, remark)
values
    (72001, '2026-05', 1001, '广州南粤人力资源有限公司', 10001, '赵志成', '440106199001010011', '440106', 6000.00, 6000.00, 0.00,
     '1', '0', 'TAX-202605-0001', 'SUCCESS', '税务 Stub 同步成功', sysdate(), '{"mode":"stub","declared":6000}', 'admin', sysdate(), '申报一致'),
    (72002, '2026-05', 1001, '广州南粤人力资源有限公司', 10003, '陈晓薇', '440106199303030033', '440106', 6500.00, 5200.00, 25.00,
     '2', '1', 'TAX-202605-0002', 'SUCCESS', '税务 Stub 同步成功', sysdate(), '{"mode":"stub","declared":5200}', 'admin', sysdate(), '申报差异超过10%'),
    (72003, '2026-05', 1002, '深圳鹏城机电工程有限公司', 10006, '郑思雨', '440305199206060066', '440305', 7100.00, 6900.00, 2.90,
     '1', '0', 'TAX-202605-0003', 'SUCCESS', '税务 Stub 同步成功', sysdate(), '{"mode":"stub","declared":6900}', 'admin', sysdate(), '申报基本一致');

replace into t_uninsured_list
    (list_id, batch_no, stat_month, list_type, enterprise_id, enterprise_name, person_id, person_name, id_card, region_code, salary_amount,
     detected_reason, disposal_status, warning_status, create_by, create_time, remark)
values
    (73001, 'UN-202605-001', '2026-05', '1', 1001, '广州南粤人力资源有限公司', 10004, '黄耀文', '440106199404040044', '440106', 6200.00,
     '税务 Stub 已申报工资，但社保 Stub 未匹配到参保记录', '0', '1', 'admin', sysdate(), '待现场核查'),
    (73002, 'UN-202605-001', '2026-05', '3', 1002, '深圳鹏城机电工程有限公司', 10007, '许嘉诚', '440305199307070077', '440305', 5800.00,
     '在建工程人员有出勤和工资，但社保名单缺失', '1', '1', 'admin', sysdate(), '已通知企业补录');

replace into t_employment_proportion
    (record_id, stat_month, employer_enterprise_id, employer_enterprise_name, region_code, dispatch_count, formal_count, ratio_value, warning_level, warning_status,
     create_by, create_time, remark)
values
    (74001, '2026-05', 1002, '深圳鹏城机电工程有限公司', '440305', 12, 96, 11.11, '1', '1', 'admin', sysdate(), '派遣比例黄警样例'),
    (74002, '2026-05', 1003, '佛山顺德智造服务有限公司', '440606', 18, 90, 16.67, '2', '1', 'admin', sysdate(), '派遣比例红警样例');

replace into t_fake_outsourcing_record
    (record_id, stat_month, enterprise_id, enterprise_name, region_code, attendance_score, schedule_score, reward_score, training_score, total_score,
     suspected_flag, warning_status, evidence_summary, create_by, create_time, remark)
values
    (75001, '2026-05', 1003, '佛山顺德智造服务有限公司', '440606', 45, 55, 50, 40, 47,
     '1', '1', '班组排班、奖惩和培训均由甲方直接控制，疑似假外包', 'admin', sysdate(), '疑似假外包样例'),
    (75002, '2026-05', 1002, '深圳鹏城机电工程有限公司', '440305', 80, 75, 70, 85, 77,
     '0', '0', '外包管理边界清晰，综合评分正常', 'admin', sysdate(), '正常外包样例');

replace into t_device
    (device_id, device_code, device_name, device_type, enterprise_id, enterprise_name, region_code, chip_id, sim_card_no, device_status, auth_status,
     last_heartbeat, install_location, firmware_version, create_by, create_time, remark)
values
    (76001, 'GD-DEV-001', '广州南粤人力闸机01', '1', 1001, '广州南粤人力资源有限公司', '440106', 'CHIP-001', '898600100000000001', '1', '1',
     sysdate(), '广州市白云区项目一号门', 'v1.0.0', 'admin', sysdate(), '测试考勤闸机'),
    (76002, 'GD-DEV-002', '深圳鹏城AI摄像头01', '3', 1002, '深圳鹏城机电工程有限公司', '440305', 'CHIP-002', '898600100000000002', '0', '0',
     date_sub(sysdate(), interval 1 day), '深圳市龙岗区焊接作业面', 'v2.1.3', 'admin', sysdate(), '测试AI摄像头'),
    (76003, 'GD-DEV-003', '佛山顺德芯片终端01', '2', 1003, '佛山顺德智造服务有限公司', '440606', 'CHIP-003', '898600100000000003', '2', '2',
     sysdate(), '佛山市南海区钢构车间', 'v3.0.1', 'admin', sysdate(), '测试芯片终端');

replace into t_device_command_log
    (log_id, device_id, device_code, command_type, command_payload, command_result, result_message, source_serial_no, source_status, source_message,
     callback_time, raw_payload, operator_name, create_by, create_time)
values
    (77001, 76003, 'GD-DEV-003', '3', '{"deviceCode":"GD-DEV-003","action":"AUTHORIZE"}', '2', '人员未参保，授权拒绝',
     'DEV-CMD-0001', 'REJECT', 'EmergencyCert 通过，Social 参保校验失败', date_sub(sysdate(), interval 1 day), '{"mode":"stub","authorized":false}', 'admin', 'admin', sysdate()),
    (77002, 76001, 'GD-DEV-001', '1', '{"deviceCode":"GD-DEV-001","action":"LOCK"}', '1', '设备锁机',
     'DEV-CMD-0002', 'SUCCESS', '设备网关 Stub 已记录锁机指令', date_sub(sysdate(), interval 3 hour), '{"mode":"stub","locked":true}', 'admin', 'admin', sysdate());

replace into t_device_event
    (event_id, device_id, device_code, enterprise_id, enterprise_name, region_code, event_type, event_code, event_content, evidence_url, event_status,
     source_serial_no, source_status, source_message, callback_time, raw_payload, event_time, create_by, create_time)
values
    (78001, 76002, 'GD-DEV-002', 1002, '深圳鹏城机电工程有限公司', '440305', '2', 'PPE_MISSING', 'AI识别到未佩戴护目镜',
     'https://stub.local/evidence/ppe_missing.jpg', '0', 'DEV-EVT-0001', 'SUCCESS', 'AI 事件 Stub 回写成功', date_sub(sysdate(), interval 2 hour),
     '{"mode":"stub","eventCode":"PPE_MISSING"}', date_sub(sysdate(), interval 2 hour), 'admin', sysdate()),
    (78002, 76003, 'GD-DEV-003', 1003, '佛山顺德智造服务有限公司', '440606', '3', 'AUTH_DENIED', '设备授权失败：人员未参保',
     '', '1', 'DEV-EVT-0002', 'REJECT', '授权事件 Stub 回写成功', date_sub(sysdate(), interval 1 day),
     '{"mode":"stub","eventCode":"AUTH_DENIED"}', date_sub(sysdate(), interval 1 day), 'admin', sysdate());

replace into t_injury_event
    (event_id, person_id, person_name, enterprise_id, enterprise_name, region_code, event_date, report_time, injury_location, injury_part, diagnosis_url,
     injury_status, approval_deadline, remaining_days, approval_result, warning_status, create_by, create_time, remark)
values
    (79001, 10001, '赵志成', 1001, '广州南粤人力资源有限公司', '440106', date_sub(curdate(), interval 6 day), date_sub(sysdate(), interval 5 day),
     '广州市白云区施工平台', '左手', '', '1', date_add(curdate(), interval 3 day), 3, '', '0', 'admin', sysdate(), '工伤认定处理中'),
    (79002, 10005, '刘海鹏', 1002, '深圳鹏城机电工程有限公司', '440305', date_sub(curdate(), interval 20 day), date_sub(sysdate(), interval 18 day),
     '深圳市龙岗区焊接作业面', '腰部', '', '1', date_sub(curdate(), interval 2 day), -2, '', '1', 'admin', sysdate(), '已超期待预警');

replace into t_prevention_project
    (project_id, project_name, project_type, enterprise_id, enterprise_name, region_code, budget_amount, actual_amount, start_date, end_date,
     project_status, evaluation_score, evaluation_report, create_by, create_time, remark)
values
    (80001, '广州焊接安全培训提升计划', '2', 1001, '广州南粤人力资源有限公司', '440106', 120000.00, 45000.00, curdate(), date_add(curdate(), interval 30 day),
     '2', 82, '季度培训执行中，待验收评估', 'admin', sysdate(), '测试预防培训项目');

replace into t_warning
    (warn_id, warn_level, warn_type, source_module, target_object_id, target_type, enterprise_id, enterprise_name, region_code, content, evidence_url,
     warn_status, assign_to, assign_name, create_by, create_time, update_by, update_time, resolve_time, remark)
values
    (82001, '2', 'SOCIAL_BASE_ABNORMAL', 'SOCIAL', 10002, '3', 1001, '广州南粤人力资源有限公司', '440106',
     '梁宇峰社保基数与工资差异超过20%，请核查申报基数。', '', '0', null, '', 'admin', sysdate(), '', null, null, '来自社保基数比对'),
    (82002, '2', 'TAX_INCOME_ABNORMAL', 'TAX', 10003, '3', 1001, '广州南粤人力资源有限公司', '440106',
     '陈晓薇工资实发与个税申报收入差异超过10%，已转人工核查。', '', '1', null, 'admin', 'admin', date_sub(sysdate(), interval 3 hour), 'admin', date_sub(sysdate(), interval 2 hour), null, '来自个税比对'),
    (82003, '2', 'UNINSURED_PERSON', 'EXPANSION', 10004, '3', 1001, '广州南粤人力资源有限公司', '440106',
     '黄耀文存在应参未参风险，请尽快核查并补录参保。', '', '0', null, '', 'admin', sysdate(), '', null, null, '来自漏保清单'),
    (82004, '3', 'DEVICE_AUTHORIZE_FAIL', 'DEVICE', 76003, '2', 1003, '佛山顺德智造服务有限公司', '440606',
     '设备 GD-DEV-003 授权失败，原因：人员未参保。', '', '2', null, 'admin', 'admin', date_sub(sysdate(), interval 1 day), 'admin', date_sub(sysdate(), interval 1 day), date_sub(sysdate(), interval 23 hour), '来自设备授权'),
    (82005, '2', 'INJURY_OVERDUE', 'INJURY', 79002, '3', 1002, '深圳鹏城机电工程有限公司', '440305',
     '工伤事件超期未处理，人员：刘海鹏。', '', '0', null, '', 'admin', sysdate(), '', null, null, '来自工伤监管'),
    (82006, '3', 'EMPLOYMENT_RATIO_HIGH', 'SPECIAL', 1003, '1', 1003, '佛山顺德智造服务有限公司', '440606',
     '佛山顺德智造服务有限公司派遣比例达到16.67%，已触发红警。', '', '4', null, 'admin', 'admin', date_sub(sysdate(), interval 4 hour), 'admin', date_sub(sysdate(), interval 2 hour), null, '来自用工比例监控'),
    (82007, '2', 'DEVICE_AI_EVENT', 'DEVICE', 76002, '2', 1002, '深圳鹏城机电工程有限公司', '440305',
     '设备 GD-DEV-002 识别到未佩戴护目镜，请现场复核。', 'https://stub.local/evidence/ppe_missing.jpg', '0', null, '', 'admin', sysdate(), '', null, null, '来自设备AI事件');

replace into t_warning_handle_log
    (log_id, warn_id, action_type, opinion, attachment_urls, before_status, after_status, handler_name, handle_time, create_by, create_time)
values
    (83001, 82002, 'PROCESS', '已派发给企业管理员核查申报口径差异。', '', '0', '1', 'admin', date_sub(sysdate(), interval 2 hour), 'admin', sysdate()),
    (83002, 82004, 'CLOSE', '现场核查完成，企业已更换授权人员并重新授权。', 'https://stub.local/evidence/device_auth_close.pdf', '1', '2', 'admin', date_sub(sysdate(), interval 23 hour), 'admin', sysdate()),
    (83003, 82006, 'UPGRADE', '派遣比例连续超阈值，升级区级监管复核。', '', '1', '4', 'admin', date_sub(sysdate(), interval 2 hour), 'admin', sysdate());
