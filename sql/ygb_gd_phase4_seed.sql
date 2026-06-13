set names utf8mb4;

delete from t_ai_report_item where report_id between 94001 and 94010;
delete from t_ai_report where report_id between 94001 and 94010;
delete from t_ai_report_config where config_id between 93001 and 93010;

insert into t_ai_report_config (config_id, region_code, version, config_status, dimension_weights, target_values, effective_date, source_mode, create_by, create_time, update_by, update_time, remark) values
(93001, '440000', 'V2026.06-GD', '1', '{"A":25,"B":20,"C":20,"D":20,"E":15}', '{"contractRate":100,"attendanceRate":95,"paySuccessRate":98,"onlineRate":95,"injuryRate":2.5,"warningCloseRate":90}', '2026-06-01', 'stub', 'admin', '2026-06-03 14:00:00', 'admin', '2026-06-03 14:00:00', '广东省AI评分模型'),
(93002, '440106', 'V2026.06-TH', '1', '{"A":20,"B":20,"C":20,"D":25,"E":15}', '{"contractRate":100,"attendanceRate":95,"paySuccessRate":98,"onlineRate":96,"injuryRate":2.2,"warningCloseRate":92}', '2026-06-01', 'stub', 'admin', '2026-06-03 14:05:00', 'admin', '2026-06-03 14:05:00', '天河区AI评分模型'),
(93003, '440305', 'V2026.06-NS', '1', '{"A":22,"B":25,"C":18,"D":20,"E":15}', '{"contractRate":100,"attendanceRate":96,"paySuccessRate":98,"onlineRate":96,"injuryRate":2.0,"warningCloseRate":93}', '2026-06-01', 'stub', 'admin', '2026-06-03 14:10:00', 'admin', '2026-06-03 14:10:00', '南山区AI评分模型'),
(93004, '440606', 'V2026.06-SD', '1', '{"A":25,"B":18,"C":20,"D":22,"E":15}', '{"contractRate":100,"attendanceRate":94,"paySuccessRate":97,"onlineRate":94,"injuryRate":3.0,"warningCloseRate":88}', '2026-06-01', 'stub', 'admin', '2026-06-03 14:15:00', 'admin', '2026-06-03 14:15:00', '顺德区AI评分模型');

insert into t_ai_report (report_id, report_type, region_code, period_start, period_end, enterprise_type, selected_dimensions, total_score, risk_level, ranking_no, config_version, report_summary, report_pdf_url, report_html, source_mode, generated_time, del_flag, create_by, create_time, update_by, update_time, remark) values
(94001, 'MONTHLY', '440000', '2026-06-01', '2026-06-30', 'ALL', 'A,B,C,D,E', 88.40, 'LOW', 1, 'V2026.06-GD', '综合得分 88.40，风险等级低风险，区域排名第 1，重点关注：工资发放合规、预警闭环治理。', 'stub://ai-report/monthly/440000/2026-06-01_2026-06-30.pdf', '<section><h3>广东省AI监测报告</h3><p>综合得分 88.40，建议继续补齐工资失败回盘和预警闭环动作。</p></section>', 'stub', '2026-06-03 15:00:00', '0', 'admin', '2026-06-03 15:00:00', 'admin', '2026-06-03 15:00:00', '广东省AI监测月报'),
(94002, 'MONTHLY', '440106', '2026-06-01', '2026-06-30', 'ALL', 'A,B,C,D,E', 82.75, 'MEDIUM', 2, 'V2026.06-TH', '综合得分 82.75，风险等级中风险，区域排名第 2，重点关注：设备工伤安全、考勤归集合规。', 'stub://ai-report/monthly/440106/2026-06-01_2026-06-30.pdf', '<section><h3>天河区AI监测报告</h3><p>设备在线率和考勤校验通过率仍需继续提升。</p></section>', 'stub', '2026-06-03 15:10:00', '0', 'admin', '2026-06-03 15:10:00', 'admin', '2026-06-03 15:10:00', '天河区AI监测月报'),
(94003, 'MONTHLY', '440305', '2026-06-01', '2026-06-30', 'ALL', 'A,B,C,D,E', 79.43, 'MEDIUM', 3, 'V2026.06-NS', '综合得分 79.43，风险等级中风险，区域排名第 3，重点关注：设备工伤安全、考勤归集合规。', 'stub://ai-report/monthly/440305/2026-06-01_2026-06-30.pdf', '<section><h3>南山区AI监测报告</h3><p>考勤准确率和设备安全评分形成主要短板。</p></section>', 'stub', '2026-06-03 15:20:00', '0', 'admin', '2026-06-03 15:20:00', 'admin', '2026-06-03 15:20:00', '南山区AI监测月报'),
(94004, 'MONTHLY', '440606', '2026-06-01', '2026-06-30', 'ALL', 'A,B,C,D,E', 68.77, 'HIGH', 4, 'V2026.06-SD', '综合得分 68.77，风险等级高风险，区域排名第 4，重点关注：设备工伤安全、预警闭环治理。', 'stub://ai-report/monthly/440606/2026-06-01_2026-06-30.pdf', '<section><h3>顺德区AI监测报告</h3><p>建议优先处理离线设备、工伤防控和长期未闭环预警。</p></section>', 'stub', '2026-06-03 15:30:00', '0', 'admin', '2026-06-03 15:30:00', 'admin', '2026-06-03 15:30:00', '顺德区AI监测月报');

insert into t_ai_report_item (item_id, report_id, dimension_code, dimension_name, metric_label, metric_value, target_value, dimension_weight, dimension_score, risk_level, suggestion_text, detail_json, sort_no) values
(95001, 94001, 'A', '合同备案合规', '备案率', 92.00, 100.00, 25.00, 92.00, 'LOW', '持续保持备案完整率并抽样复核到期续签。', '{"filedCount":23,"totalCount":25}', 1),
(95002, 94001, 'B', '考勤归集合规', '校验通过率', 83.60, 95.00, 20.00, 88.00, 'MEDIUM', '复核未通过 att_check 的归集记录。', '{"passedCount":46,"totalCount":55}', 2),
(95003, 94001, 'C', '工资发放合规', '发放成功率', 84.28, 98.00, 20.00, 86.00, 'MEDIUM', '重点清理银行失败回盘和补发记录。', '{"successCount":59,"totalCount":70}', 3),
(95004, 94001, 'D', '设备工伤安全', '设备在线率', 91.00, 95.00, 20.00, 90.00, 'LOW', '继续保持高危设备在线巡检和工伤预警联动。', '{"onlineCount":91,"deviceTotal":100,"injuryRate":2.2}', 4),
(95005, 94001, 'E', '预警闭环治理', '闭环率', 75.60, 90.00, 15.00, 84.00, 'MEDIUM', '加快长期未闭环预警工单处置。', '{"closedCount":34,"totalCount":45}', 5),
(95006, 94002, 'A', '合同备案合规', '备案率', 90.00, 100.00, 20.00, 90.00, 'LOW', '继续跟踪合同补录和到期续签。', '{"filedCount":18,"totalCount":20}', 1),
(95007, 94002, 'B', '考勤归集合规', '校验通过率', 76.00, 95.00, 20.00, 80.00, 'MEDIUM', '提高考勤归集校验通过率。', '{"passedCount":19,"totalCount":25}', 2),
(95008, 94002, 'C', '工资发放合规', '发放成功率', 80.36, 98.00, 20.00, 82.00, 'MEDIUM', '压降工资失败补发占比。', '{"successCount":45,"totalCount":56}', 3),
(95009, 94002, 'D', '设备工伤安全', '设备在线率', 82.00, 96.00, 25.00, 79.00, 'MEDIUM', '优先处置离线设备和授权异常设备。', '{"onlineCount":41,"deviceTotal":50,"injuryRate":2.6}', 4),
(95010, 94002, 'E', '预警闭环治理', '闭环率', 77.28, 92.00, 15.00, 84.00, 'MEDIUM', '提升辖区预警闭环和误报回写速度。', '{"closedCount":17,"totalCount":22}', 5),
(95011, 94003, 'A', '合同备案合规', '备案率', 84.00, 100.00, 22.00, 84.00, 'MEDIUM', '补齐外包类合同的备案和证据材料。', '{"filedCount":21,"totalCount":25}', 1),
(95012, 94003, 'B', '考勤归集合规', '校验通过率', 74.88, 96.00, 25.00, 78.00, 'MEDIUM', '提升考勤校验通过率和异常修复效率。', '{"passedCount":31,"totalCount":41}', 2),
(95013, 94003, 'C', '工资发放合规', '发放成功率', 78.40, 98.00, 18.00, 80.00, 'MEDIUM', '减少工资回盘失败和重复提交。', '{"successCount":49,"totalCount":63}', 3),
(95014, 94003, 'D', '设备工伤安全', '设备在线率', 79.00, 96.00, 20.00, 76.00, 'MEDIUM', '补齐高危设备在线率并压降工伤风险。', '{"onlineCount":79,"deviceTotal":100,"injuryRate":2.8}', 4),
(95015, 94003, 'E', '预警闭环治理', '闭环率', 73.47, 93.00, 15.00, 79.00, 'MEDIUM', '加强重点企业预警的闭环督办。', '{"closedCount":18,"totalCount":24}', 5),
(95016, 94004, 'A', '合同备案合规', '备案率', 72.00, 100.00, 25.00, 72.00, 'HIGH', '优先清理未备案和到期未续签合同。', '{"filedCount":18,"totalCount":25}', 1),
(95017, 94004, 'B', '考勤归集合规', '校验通过率', 64.86, 94.00, 18.00, 69.00, 'HIGH', '补录缺失考勤并修正异常归集。', '{"passedCount":24,"totalCount":37}', 2),
(95018, 94004, 'C', '工资发放合规', '发放成功率', 67.90, 97.00, 20.00, 70.00, 'HIGH', '重点核查工资失败原因和延迟补发。', '{"successCount":38,"totalCount":56}', 3),
(95019, 94004, 'D', '设备工伤安全', '设备在线率', 61.10, 94.00, 22.00, 65.00, 'HIGH', '先恢复离线设备，再压降高危岗位工伤事件。', '{"onlineCount":11,"deviceTotal":18,"injuryRate":4.1}', 4),
(95020, 94004, 'E', '预警闭环治理', '闭环率', 58.96, 88.00, 15.00, 67.00, 'HIGH', '建立超时预警专班，加快闭环处置。', '{"closedCount":13,"totalCount":22}', 5);
