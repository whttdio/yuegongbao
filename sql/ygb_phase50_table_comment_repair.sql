set names utf8mb4;
-- ygb_phase50_table_comment_repair.sql
-- 修复 Navicat/非 UTF-8 连接执行 DDL 导致的表注释乱码（显示为 ????）
-- 用法: source sql/ygb_phase50_table_comment_repair.sql;

-- phase1 基础主数据 / 合规主链
alter table t_enterprise comment '企业主数据表';
alter table t_person comment '人员主数据表';
alter table t_labor_contract comment '合同备案表';
alter table t_attendance_raw comment '考勤上报表';
alter table t_attendance_monthly comment '考勤归集表';
alter table t_salary_batch comment '工资批次表';
alter table t_salary_detail comment '工资明细表';

-- phase2 监管联动 / 设备工伤 / 预警
alter table t_social_payment comment '社保缴费监控表';
alter table t_social_base_compare comment '社保基数比对表';
alter table t_tax_compare comment '税务监管比对表';
alter table t_uninsured_list comment '扩面减损清单表';
alter table t_employment_proportion comment '用工比例监控表';
alter table t_fake_outsourcing_record comment '假外包识别记录表';
alter table t_device comment '设备管理表';
alter table t_device_command_log comment '设备指令日志表';
alter table t_device_event comment '设备事件表';
alter table t_injury_event comment '工伤事件表';
alter table t_prevention_project comment '工伤预防项目表';
alter table t_warning_rule comment '预警规则表';
alter table t_warning comment '预警工单表';
alter table t_warning_handle_log comment '预警处置日志表';

-- phase3 驾驶舱 / 报表
alter table t_cockpit_snapshot comment '驾驶舱指标快照表';
alter table t_cockpit_map_feature comment '驾驶舱地图要素表';
alter table t_stat_report comment '统计报表主表';
alter table t_stat_report_item comment '统计报表明细表';

-- phase4 AI 报告
alter table t_ai_report_config comment 'AI监测报告评分模型配置表';
alter table t_ai_report comment 'AI监测报告表';
alter table t_ai_report_item comment 'AI监测报告明细表';

-- phase5 安责险
alter table t_aq_insurance comment '安责险投保监管表';
alter table t_prevention_fund comment '事故预防资金池表';

-- phase6 信用
alter table t_credit_score comment '企业信用评分表';

-- phase7 新业态 / 职业病（截图乱码表）
alter table t_newform_worker comment '新业态人员库';
alter table t_occupation_monitor comment '职业病监测';

-- phase8 高处作业（截图乱码表）
alter table t_height_work_report comment '高处作业申报报备';
alter table t_height_work_report_worker comment '高处作业作业人员明细';

-- phase16 门户 CMS
alter table ygb_portal_content comment '门户网站CMS内容';

-- phase22 / 25 劳动者端
alter table ygb_worker_push_test_record comment '劳动者端推送测试记录';
alter table ygb_worker_realname_apply comment '劳动者实名认证申请';

-- phase28 缺口补齐
alter table t_contract_template comment '合同模板库';
alter table t_salary_arrears_handle comment '工资拖欠处置台账';

-- phase29 驾驶舱配置
alter table t_cockpit_config comment '驾驶舱配置';

-- phase30 AI 任务 / 订阅
alter table t_ai_report_task comment 'AI报告建议任务';
alter table t_ai_report_subscription comment 'AI报告订阅';

-- phase38 扩展台账
alter table ygb_module_record comment '粤工保扩展模块记录台账';
