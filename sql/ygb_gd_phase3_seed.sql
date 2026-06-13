set names utf8mb4;

delete from t_stat_report_item where report_id between 91001 and 91010;
delete from t_stat_report where report_id between 91001 and 91010;
delete from t_cockpit_map_feature where feature_id between 90501 and 90520;
delete from t_cockpit_snapshot where snapshot_id between 90001 and 90040;

insert into t_cockpit_snapshot (snapshot_id, stat_date, region_code, dispatch_company_count, employer_count, dispatched_worker_count, high_risk_enterprise_count, insurance_rate, aq_insurance_rate, today_warning_count, expand_completion_rate, new_injury_rate, online_device_count, pending_warning_count, overdue_injury_count, source_mode, create_by, create_time, remark) values
(90001, '2026-05-28', '440000', 1, 1, 5, 2, 78.50, 68.00, 7, 72.30, 1.80, 1, 6, 1, 'stub', 'admin', sysdate(), '广东省驾驶舱测试快照'),
(90002, '2026-05-29', '440000', 1, 1, 5, 2, 79.20, 68.50, 8, 74.80, 1.60, 1, 7, 1, 'stub', 'admin', sysdate(), '广东省驾驶舱测试快照'),
(90003, '2026-05-30', '440000', 1, 1, 5, 2, 80.60, 69.20, 10, 78.10, 1.40, 2, 8, 1, 'stub', 'admin', sysdate(), '广东省驾驶舱测试快照'),
(90004, '2026-05-31', '440000', 1, 1, 5, 2, 81.40, 70.00, 9, 80.50, 1.20, 2, 7, 1, 'stub', 'admin', sysdate(), '广东省驾驶舱测试快照'),
(90005, '2026-06-01', '440000', 1, 1, 5, 2, 82.10, 71.30, 11, 82.20, 1.10, 2, 8, 1, 'stub', 'admin', sysdate(), '广东省驾驶舱测试快照'),
(90006, '2026-06-02', '440000', 1, 1, 5, 2, 83.33, 72.10, 12, 86.70, 0.95, 2, 9, 1, 'stub', 'admin', sysdate(), '广东省驾驶舱测试快照'),
(90007, '2026-06-03', '440000', 1, 1, 5, 2, 84.50, 73.60, 18, 89.80, 0.85, 2, 11, 1, 'stub', 'admin', sysdate(), '广东省驾驶舱测试快照'),
(90011, '2026-05-28', '440106', 1, 0, 2, 1, 88.00, 76.00, 2, 84.50, 0.00, 0, 1, 0, 'stub', 'admin', sysdate(), '广州驾驶舱测试快照'),
(90012, '2026-05-29', '440106', 1, 0, 2, 1, 88.50, 76.50, 2, 85.20, 0.00, 0, 1, 0, 'stub', 'admin', sysdate(), '广州驾驶舱测试快照'),
(90013, '2026-05-30', '440106', 1, 0, 2, 1, 89.20, 77.10, 3, 86.00, 0.00, 1, 1, 0, 'stub', 'admin', sysdate(), '广州驾驶舱测试快照'),
(90014, '2026-05-31', '440106', 1, 0, 2, 1, 90.00, 78.20, 2, 86.80, 0.00, 1, 1, 0, 'stub', 'admin', sysdate(), '广州驾驶舱测试快照'),
(90015, '2026-06-01', '440106', 1, 0, 2, 1, 90.50, 78.60, 3, 88.20, 0.00, 1, 1, 0, 'stub', 'admin', sysdate(), '广州驾驶舱测试快照'),
(90016, '2026-06-02', '440106', 1, 0, 2, 1, 91.00, 79.10, 3, 89.00, 0.00, 1, 1, 0, 'stub', 'admin', sysdate(), '广州驾驶舱测试快照'),
(90017, '2026-06-03', '440106', 1, 0, 2, 1, 91.50, 79.50, 4, 90.00, 0.00, 1, 2, 0, 'stub', 'admin', sysdate(), '广州驾驶舱测试快照'),
(90021, '2026-05-28', '440305', 0, 1, 2, 1, 72.00, 66.00, 3, 62.00, 2.40, 1, 2, 1, 'stub', 'admin', sysdate(), '深圳驾驶舱测试快照'),
(90022, '2026-05-29', '440305', 0, 1, 2, 1, 72.50, 66.50, 3, 63.50, 2.20, 1, 2, 1, 'stub', 'admin', sysdate(), '深圳驾驶舱测试快照'),
(90023, '2026-05-30', '440305', 0, 1, 2, 1, 73.80, 67.10, 4, 66.20, 2.00, 1, 3, 1, 'stub', 'admin', sysdate(), '深圳驾驶舱测试快照'),
(90024, '2026-05-31', '440305', 0, 1, 2, 1, 74.20, 67.80, 4, 68.00, 1.80, 1, 3, 1, 'stub', 'admin', sysdate(), '深圳驾驶舱测试快照'),
(90025, '2026-06-01', '440305', 0, 1, 2, 1, 75.00, 68.20, 5, 70.40, 1.50, 1, 3, 1, 'stub', 'admin', sysdate(), '深圳驾驶舱测试快照'),
(90026, '2026-06-02', '440305', 0, 1, 2, 1, 76.30, 69.00, 6, 73.50, 1.20, 1, 4, 1, 'stub', 'admin', sysdate(), '深圳驾驶舱测试快照'),
(90027, '2026-06-03', '440305', 0, 1, 2, 1, 77.20, 70.10, 8, 76.00, 1.00, 1, 5, 1, 'stub', 'admin', sysdate(), '深圳驾驶舱测试快照'),
(90031, '2026-05-28', '440606', 0, 0, 1, 0, 80.00, 65.00, 2, 70.00, 0.00, 0, 1, 0, 'stub', 'admin', sysdate(), '佛山驾驶舱测试快照'),
(90032, '2026-05-29', '440606', 0, 0, 1, 0, 80.50, 65.60, 3, 71.20, 0.00, 0, 1, 0, 'stub', 'admin', sysdate(), '佛山驾驶舱测试快照'),
(90033, '2026-05-30', '440606', 0, 0, 1, 0, 81.10, 66.00, 3, 72.50, 0.00, 0, 1, 0, 'stub', 'admin', sysdate(), '佛山驾驶舱测试快照'),
(90034, '2026-05-31', '440606', 0, 0, 1, 0, 81.50, 66.40, 3, 74.00, 0.00, 0, 1, 0, 'stub', 'admin', sysdate(), '佛山驾驶舱测试快照'),
(90035, '2026-06-01', '440606', 0, 0, 1, 0, 82.00, 67.10, 3, 75.80, 0.00, 0, 1, 0, 'stub', 'admin', sysdate(), '佛山驾驶舱测试快照'),
(90036, '2026-06-02', '440606', 0, 0, 1, 0, 82.50, 67.90, 3, 77.00, 0.00, 0, 1, 0, 'stub', 'admin', sysdate(), '佛山驾驶舱测试快照'),
(90037, '2026-06-03', '440606', 0, 0, 1, 0, 83.10, 68.20, 4, 78.50, 0.00, 0, 1, 0, 'stub', 'admin', sysdate(), '佛山驾驶舱测试快照');

insert into t_cockpit_map_feature (feature_id, stat_date, region_code, feature_type, feature_name, geometry_type, geometry_json, feature_status, source_mode, properties_json, sort_no, create_by, create_time, remark) values
(90501, '2026-06-03', '440106', 'ENTERPRISE', '广州南粤人力资源有限公司', 'Point', '[113.3619,23.1291]', '1', 'stub', '{"featureType":"ENTERPRISE","featureName":"广州南粤人力资源有限公司","regionCode":"440106","enterpriseId":1001}', 1, 'admin', sysdate(), '广州企业点位'),
(90502, '2026-06-03', '440305', 'ENTERPRISE', '深圳鹏城机电工程有限公司', 'Point', '[113.9437,22.5416]', '1', 'stub', '{"featureType":"ENTERPRISE","featureName":"深圳鹏城机电工程有限公司","regionCode":"440305","enterpriseId":1002}', 2, 'admin', sysdate(), '深圳企业点位'),
(90503, '2026-06-03', '440606', 'ENTERPRISE', '佛山顺德智造服务有限公司', 'Point', '[113.1214,22.8054]', '1', 'stub', '{"featureType":"ENTERPRISE","featureName":"佛山顺德智造服务有限公司","regionCode":"440606","enterpriseId":1003}', 3, 'admin', sysdate(), '佛山企业点位'),
(90504, '2026-06-03', '440305', 'DEVICE', '南山焊接作业设备', 'Point', '[113.9472,22.5441]', '1', 'stub', '{"featureType":"DEVICE","featureName":"南山焊接作业设备","regionCode":"440305","deviceCode":"DVC-440305-01"}', 4, 'admin', sysdate(), '深圳设备点位'),
(90505, '2026-06-03', '440106', 'DEVICE', '天河考勤采集终端', 'Point', '[113.3650,23.1260]', '0', 'stub', '{"featureType":"DEVICE","featureName":"天河考勤采集终端","regionCode":"440106","deviceCode":"DVC-440106-01"}', 5, 'admin', sysdate(), '广州设备点位'),
(90506, '2026-06-03', '440606', 'DEVICE', '顺德AI巡检设备', 'Point', '[113.1261,22.8128]', '2', 'stub', '{"featureType":"DEVICE","featureName":"顺德AI巡检设备","regionCode":"440606","deviceCode":"DVC-440606-01"}', 6, 'admin', sysdate(), '佛山设备点位'),
(90507, '2026-06-03', '440305', 'FENCE', '南山高危作业围栏', 'Polygon', '[[[113.9405,22.5398],[113.9498,22.5398],[113.9498,22.5458],[113.9405,22.5458],[113.9405,22.5398]]]', '2', 'stub', '{"featureType":"FENCE","featureName":"南山高危作业围栏","regionCode":"440305"}', 7, 'admin', sysdate(), '深圳围栏面域');

insert into t_stat_report (report_id, report_code, report_name, stat_month, region_code, report_status, metric_count, metric_amount, metric_rate, report_summary, attachment_url, source_mode, generated_time, del_flag, create_by, create_time, update_by, update_time, remark) values
(91001, 'WARNING_OVERVIEW', '预警治理月报', '2026-06', '440000', '1', 18, 5.00, 27.78, '本月预警 18 条，闭环处置 5 条，闭环率 27.78%', 'stub://report/warning_overview/2026-06', 'stub', '2026-06-03 09:20:00', '0', 'admin', sysdate(), 'admin', sysdate(), '广东预警治理月报'),
(91002, 'SOCIAL_TAX', '社保税务联动月报', '2026-05', '440000', '1', 10, 10.00, 44.20, '监管联动风险项 10 条，综合风险率 44.20%', 'stub://report/social_tax/2026-05', 'stub', '2026-06-02 18:10:00', '0', 'admin', sysdate(), 'admin', sysdate(), '广东社保税务联动月报'),
(91003, 'SALARY_PAYMENT', '工资发放月报', '2026-06', '440000', '1', 4, 12680.00, 75.00, '本月发薪覆盖 4 人次，实发金额 12680.00 元，成功率 75.00%', 'stub://report/salary_payment/2026-06', 'stub', '2026-06-03 09:40:00', '0', 'admin', sysdate(), 'admin', sysdate(), '广东工资发放月报'),
(91004, 'INJURY_RATE', '工伤发生率月报', '2026-06', '440000', '1', 2, 7.00, 285.71, '本月工伤事件 2 起，参保样本 7 人，工伤发生率 285.71‰', 'stub://report/injury_rate/2026-06', 'stub', '2026-06-03 10:00:00', '0', 'admin', sysdate(), 'admin', sysdate(), '广东工伤发生率月报');

insert into t_stat_report_item (item_id, report_id, item_category, item_name, item_dimension, metric_count, metric_value, metric_rate, sort_no) values
(92001, 91001, 'WARNING_OVERVIEW', '税务监管', 'TAX', 10, 2.00, 20.00, 1),
(92002, 91001, 'WARNING_OVERVIEW', '设备预警', 'DEVICE', 3, 1.00, 33.33, 2),
(92003, 91001, 'WARNING_OVERVIEW', '工伤监管', 'INJURY', 2, 1.00, 50.00, 3),
(92004, 91001, 'WARNING_OVERVIEW', '社保监管', 'SOCIAL', 1, 0.00, 0.00, 4),
(92005, 91001, 'WARNING_OVERVIEW', '扩面减损', 'EXPANSION', 1, 0.00, 0.00, 5),
(92006, 91001, 'WARNING_OVERVIEW', '专项治理', 'SPECIAL', 1, 1.00, 100.00, 6),
(92011, 91002, 'SOCIAL_TAX', '社保基数异常', 'SOCIAL_ABNORMAL', 1, 1.00, 33.33, 1),
(92012, 91002, 'SOCIAL_TAX', '个税比对异常', 'TAX_ABNORMAL', 4, 4.00, 66.67, 2),
(92013, 91002, 'SOCIAL_TAX', '漏保清单', 'UNINSURED', 2, 2.00, 33.33, 3),
(92014, 91002, 'SOCIAL_TAX', '用工比例黄警', 'EMPLOYMENT_YELLOW', 1, 1.00, 50.00, 4),
(92015, 91002, 'SOCIAL_TAX', '用工比例红警', 'EMPLOYMENT_RED', 1, 1.00, 50.00, 5),
(92016, 91002, 'SOCIAL_TAX', '假外包疑似', 'FAKE_OUTSOURCING', 1, 1.00, 50.00, 6),
(92021, 91003, 'SALARY_PAYMENT', '广州南粤人力资源有限公司', 'BATCH-202606-A', 2, 5680.00, 100.00, 1),
(92022, 91003, 'SALARY_PAYMENT', '深圳鹏城机电工程有限公司', 'BATCH-202606-B', 2, 7000.00, 50.00, 2),
(92031, 91004, 'INJURY_RATE', '深圳市南山区', '440305', 1, 3.00, 333.33, 1),
(92032, 91004, 'INJURY_RATE', '佛山市顺德区', '440606', 1, 4.00, 250.00, 2);
