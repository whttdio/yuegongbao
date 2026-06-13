set names utf8mb4;

delete from t_newform_worker where worker_record_id between 100001 and 100010;
delete from t_occupation_monitor where monitor_id between 101001 and 101010;
delete from t_person where person_id in (10017, 10018);
delete from t_warning where warn_id between 86001 and 86004;

insert into t_person (
  person_id, enterprise_id, region_code, person_name, id_card, mobile, worker_type, job_type,
  cert_status, insurance_status, employment_status, entry_date, leave_date, del_flag,
  create_by, create_time, update_by, update_time, remark
) values
(10017, 1001, '440106', '梁敏仪', '440106199505150062', '13700010017', '4', '平台众包',
  '1', '0', '0', '2026-03-15', null, '0', 'admin', sysdate(), '', null, '广州新业态测试人员'),
(10018, 1003, '440606', '陈梓航', '440606199309090071', '13700010018', '4', '网约司机',
  '1', '2', '0', '2026-02-20', null, '0', 'admin', sysdate(), '', null, '佛山新业态测试人员');

insert into t_newform_worker (
  worker_record_id, stat_month, enterprise_id, enterprise_name, person_id, person_name, id_card, region_code,
  platform_name, employment_type, insurance_status, injury_insurance_status, monthly_income, warning_status,
  source_serial_no, source_status, source_message, callback_time, raw_payload, del_flag,
  create_by, create_time, update_by, update_time, remark
) values
(100001, '2026-06', 1002, '深圳鹏城机电工程有限公司', 10008, '邓雅琪', '440305199408080088', '440305',
  '粤运配送平台', '配送骑手', '2', '1', 7420.00, '0',
  'NF-10008', 'SUCCESS', '新业态平台 Stub 拉取成功', '2026-06-03 20:00:00', '{"platform":"粤运配送平台","personId":10008}', '0',
  'admin', '2026-06-03 20:00:00', 'admin', '2026-06-03 20:00:00', '深圳新业态已参保样例'),
(100002, '2026-06', 1001, '广州南粤人力资源有限公司', 10017, '梁敏仪', '440106199505150062', '440106',
  '南粤灵工平台', '平台零工', '0', '0', 6980.00, '1',
  'NF-10017', 'SUCCESS', '新业态平台 Stub 拉取成功', '2026-06-03 20:05:00', '{"platform":"南粤灵工平台","personId":10017}', '0',
  'admin', '2026-06-03 20:05:00', 'admin', '2026-06-03 20:05:00', '广州新业态未参保样例'),
(100003, '2026-06', 1003, '佛山顺德智造服务有限公司', 10018, '陈梓航', '440606199309090071', '440606',
  '湾区众包平台', '网约司机', '2', '2', 8150.00, '1',
  'NF-10018', 'SUCCESS', '新业态平台 Stub 拉取成功', '2026-06-03 20:10:00', '{"platform":"湾区众包平台","personId":10018}', '0',
  'admin', '2026-06-03 20:10:00', 'admin', '2026-06-03 20:10:00', '佛山新业态停保样例');

insert into t_occupation_monitor (
  monitor_id, stat_month, region_code, industry_type, enterprise_count, worker_count, case_count,
  high_risk_enterprise_count, incidence_rate, warning_level, warning_status, source_channel,
  source_serial_no, source_status, source_message, callback_time, raw_payload, del_flag,
  create_by, create_time, update_by, update_time, remark
) values
(101001, '2026-06', '440106', '建筑施工', 24, 1860, 3, 4, 1.61, '1', '1', '卫健 Stub',
  'OC-440106', 'SUCCESS', '卫健职业病监测 Stub 拉取成功', '2026-06-03 20:15:00', '{"regionCode":"440106","industryType":"建筑施工"}', '0',
  'admin', '2026-06-03 20:15:00', 'admin', '2026-06-03 20:15:00', '广州建筑施工监测样例'),
(101002, '2026-06', '440305', '制造加工', 18, 1420, 5, 6, 3.52, '2', '1', '卫健 Stub',
  'OC-440305', 'SUCCESS', '卫健职业病监测 Stub 拉取成功', '2026-06-03 20:20:00', '{"regionCode":"440305","industryType":"制造加工"}', '0',
  'admin', '2026-06-03 20:20:00', 'admin', '2026-06-03 20:20:00', '深圳制造加工红警样例'),
(101003, '2026-06', '440606', '平台配送', 12, 980, 4, 5, 4.08, '2', '1', '卫健 Stub',
  'OC-440606', 'SUCCESS', '卫健职业病监测 Stub 拉取成功', '2026-06-03 20:25:00', '{"regionCode":"440606","industryType":"平台配送"}', '0',
  'admin', '2026-06-03 20:25:00', 'admin', '2026-06-03 20:25:00', '佛山平台配送红警样例');

insert into t_warning (
  warn_id, warn_level, warn_type, source_module, target_object_id, target_type, enterprise_id, enterprise_name,
  region_code, content, evidence_url, warn_status, assign_to, assign_name, create_by, create_time,
  update_by, update_time, resolve_time, remark
) values
(86001, '3', 'NEWFORM_INJURY_INSURANCE', 'NEWFORM', 10017, '3', 1001, '广州南粤人力资源有限公司',
  '440106', '新业态人员职业伤害参保异常，人员：梁敏仪，平台：南粤灵工平台。', '', '0', null, '', 'admin', '2026-06-03 20:30:00',
  'admin', '2026-06-03 20:30:00', null, '广州新业态未参保预警'),
(86002, '2', 'NEWFORM_INJURY_INSURANCE', 'NEWFORM', 10018, '3', 1003, '佛山顺德智造服务有限公司',
  '440606', '新业态人员职业伤害参保异常，人员：陈梓航，平台：湾区众包平台。', '', '1', null, '巡检专员', 'admin', '2026-06-03 20:35:00',
  'admin', '2026-06-03 20:40:00', null, '佛山新业态停保预警'),
(86003, '3', 'OCCUPATION_MONITOR', 'OCCUPATION', 101002, '4', null, '',
  '440305', '职业病监测异常，区域：深圳市南山区，行业：制造加工，发病人数：5。', '', '0', null, '', 'admin', '2026-06-03 20:45:00',
  'admin', '2026-06-03 20:45:00', null, '深圳职业病监测红警'),
(86004, '3', 'OCCUPATION_MONITOR', 'OCCUPATION', 101003, '4', null, '',
  '440606', '职业病监测异常，区域：佛山市顺德区，行业：平台配送，发病人数：4。', '', '0', null, '', 'admin', '2026-06-03 20:50:00',
  'admin', '2026-06-03 20:50:00', null, '佛山职业病监测红警');
