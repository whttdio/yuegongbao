set names utf8mb4;

delete from t_prevention_fund where fund_id between 97001 and 97010;
delete from t_aq_insurance where policy_id between 96001 and 96010;

insert into t_aq_insurance (policy_id, stat_month, enterprise_id, enterprise_name, region_code, insurer_name, policy_no, premium, start_date, end_date, policy_status, insured_person_count, prevention_fund_ratio, prevention_fund_amount, used_fund_amount, remaining_fund_amount, source_serial_no, source_status, source_message, callback_time, raw_payload, del_flag, create_by, create_time, update_by, update_time, remark) values
(96001, '2026-06', 1001, '广州南粤人力资源有限公司', '440106', '广东人保财险', 'AQ2026061001', 3280.00, '2026-05-01', '2026-12-31', '1', 12, 15.00, 492.00, 160.00, 332.00, 'AQSTUB96001', 'SUCCESS', '安责险保单 Stub 同步成功', '2026-06-03 16:00:00', '{}', '0', 'admin', '2026-06-03 16:00:00', 'admin', '2026-06-03 16:00:00', '广州有效保单'),
(96002, '2026-06', 1002, '深圳鹏城机电工程有限公司', '440305', '平安财险广东分公司', 'AQ2026061002', 2950.00, '2026-05-01', '2026-06-20', '2', 9, 15.00, 442.50, 390.00, 52.50, 'AQSTUB96002', 'SUCCESS', '安责险保单 Stub 同步成功', '2026-06-03 16:05:00', '{}', '0', 'admin', '2026-06-03 16:05:00', 'admin', '2026-06-03 16:05:00', '深圳即将到期保单'),
(96003, '2026-06', 1003, '佛山顺德智造服务有限公司', '440606', '太平洋财险广东分公司', 'AQ2026061003', 2400.00, '2026-04-01', '2026-05-25', '3', 8, 12.00, 288.00, 280.00, 8.00, 'AQSTUB96003', 'SUCCESS', '安责险保单 Stub 同步成功', '2026-06-03 16:10:00', '{}', '0', 'admin', '2026-06-03 16:10:00', 'admin', '2026-06-03 16:10:00', '佛山已过期保单'),
(96004, '2026-06', 1004, '广东应保安全服务中心', '440000', '广东人保财险', 'AQ2026061004', 1860.00, '2026-07-01', '2027-06-30', '0', 6, 15.00, 279.00, 0.00, 279.00, 'AQSTUB96004', 'SUCCESS', '安责险保单 Stub 同步成功', '2026-06-03 16:15:00', '{}', '0', 'admin', '2026-06-03 16:15:00', 'admin', '2026-06-03 16:15:00', '省级未生效保单');

insert into t_prevention_fund (fund_id, policy_id, stat_month, enterprise_id, enterprise_name, region_code, accrued_amount, used_amount, remaining_amount, fund_status, usage_purpose, evidence_url, last_settle_time, source_mode, del_flag, create_by, create_time, update_by, update_time, remark) values
(97001, 96001, '2026-06', 1001, '广州南粤人力资源有限公司', '440106', 492.00, 160.00, 332.00, '2', '焊工安全培训与防护用品采购', 'stub://aqins/fund/97001', '2026-06-03 16:20:00', 'stub', '0', 'admin', '2026-06-03 16:20:00', 'admin', '2026-06-03 16:20:00', '广州资金池'),
(97002, 96002, '2026-06', 1002, '深圳鹏城机电工程有限公司', '440305', 442.50, 390.00, 52.50, '2', '高危设备巡检与应急演练', 'stub://aqins/fund/97002', '2026-06-03 16:25:00', 'stub', '0', 'admin', '2026-06-03 16:25:00', 'admin', '2026-06-03 16:25:00', '深圳资金池'),
(97003, 96003, '2026-06', 1003, '佛山顺德智造服务有限公司', '440606', 288.00, 280.00, 8.00, '2', '事故隐患整改和班组培训', 'stub://aqins/fund/97003', '2026-06-03 16:30:00', 'stub', '0', 'admin', '2026-06-03 16:30:00', 'admin', '2026-06-03 16:30:00', '佛山资金池'),
(97004, 96004, '2026-06', 1004, '广东应保安全服务中心', '440000', 279.00, 0.00, 279.00, '1', '', '', null, 'stub', '0', 'admin', '2026-06-03 16:35:00', 'admin', '2026-06-03 16:35:00', '省级资金池');
