set names utf8mb4;

delete from t_credit_score where score_id between 98001 and 98010;

insert into t_credit_score (
  score_id, stat_month, enterprise_id, enterprise_name, region_code, enterprise_type,
  contract_score, attendance_score, salary_score, social_tax_score, safety_score, governance_score,
  total_score, credit_level, color_code, rank_no, factor_json, summary_text, warning_status,
  source_mode, evaluate_time, del_flag, create_by, create_time, update_by, update_time, remark
) values
(98001, '2026-06', 1001, '广州南粤人力资源有限公司', '440106', '1',
  100.00, 96.00, 98.00, 92.00, 95.00, 90.00,
  95.00, 'A', 'GREEN', 1,
  '{"contract":{"name":"合同备案","score":100.00,"metricLabel":"备案率","metricValue":100.00,"targetValue":100.00},"attendance":{"name":"考勤归集","score":96.00,"metricLabel":"校验通过率","metricValue":96.00,"targetValue":95.00},"salary":{"name":"工资发放","score":98.00,"metricLabel":"发放成功率","metricValue":98.00,"targetValue":98.00},"socialTax":{"name":"社税合规","score":92.00,"metricLabel":"综合合规率","metricValue":92.00,"targetValue":90.00},"safety":{"name":"安全保障","score":95.00,"metricLabel":"安全保障评分","metricValue":95.00,"targetValue":100.00},"governance":{"name":"预警治理","score":90.00,"metricLabel":"闭环率","metricValue":90.00,"targetValue":90.00}}',
  '总分 95.00，信用等级 A，需重点关注 预警治理、社税合规。', '0', 'seed', '2026-06-03 18:00:00', '0', 'admin', '2026-06-03 18:00:00', 'admin', '2026-06-03 18:00:00', '广州优质企业信用样例'),
(98002, '2026-06', 1002, '深圳鹏城机电工程有限公司', '440305', '2',
  90.00, 89.00, 92.00, 78.00, 70.00, 72.00,
  82.30, 'B', 'GREEN', 2,
  '{"contract":{"name":"合同备案","score":90.00,"metricLabel":"备案率","metricValue":90.00,"targetValue":100.00},"attendance":{"name":"考勤归集","score":89.00,"metricLabel":"校验通过率","metricValue":89.00,"targetValue":95.00},"salary":{"name":"工资发放","score":92.00,"metricLabel":"发放成功率","metricValue":92.00,"targetValue":98.00},"socialTax":{"name":"社税合规","score":78.00,"metricLabel":"综合合规率","metricValue":78.00,"targetValue":90.00},"safety":{"name":"安全保障","score":70.00,"metricLabel":"安全保障评分","metricValue":70.00,"targetValue":100.00},"governance":{"name":"预警治理","score":72.00,"metricLabel":"闭环率","metricValue":72.00,"targetValue":90.00}}',
  '总分 82.30，信用等级 B，需重点关注 安全保障、预警治理。', '0', 'seed', '2026-06-03 18:05:00', '0', 'admin', '2026-06-03 18:05:00', 'admin', '2026-06-03 18:05:00', '深圳中优企业信用样例'),
(98003, '2026-06', 1003, '佛山顺德智造服务有限公司', '440606', '3',
  65.00, 63.00, 60.00, 55.00, 42.00, 58.00,
  56.65, 'D', 'RED', 3,
  '{"contract":{"name":"合同备案","score":65.00,"metricLabel":"备案率","metricValue":65.00,"targetValue":100.00},"attendance":{"name":"考勤归集","score":63.00,"metricLabel":"校验通过率","metricValue":63.00,"targetValue":95.00},"salary":{"name":"工资发放","score":60.00,"metricLabel":"发放成功率","metricValue":60.00,"targetValue":98.00},"socialTax":{"name":"社税合规","score":55.00,"metricLabel":"综合合规率","metricValue":55.00,"targetValue":90.00},"safety":{"name":"安全保障","score":42.00,"metricLabel":"安全保障评分","metricValue":42.00,"targetValue":100.00},"governance":{"name":"预警治理","score":58.00,"metricLabel":"闭环率","metricValue":58.00,"targetValue":90.00}}',
  '总分 56.65，信用等级 D，需重点关注 安全保障、社税合规。', '1', 'seed', '2026-06-03 18:10:00', '0', 'admin', '2026-06-03 18:10:00', 'admin', '2026-06-03 18:10:00', '佛山重点整治企业信用样例');
