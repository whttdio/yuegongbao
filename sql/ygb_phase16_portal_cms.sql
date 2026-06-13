SET NAMES utf8mb4;

-- 粤工保/安责保门户网站 CMS 内容表（530 官网模块统一承载）
CREATE TABLE IF NOT EXISTS ygb_portal_content (
  content_id      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '内容ID',
  portal_code     VARCHAR(16)  NOT NULL DEFAULT 'ygb' COMMENT '门户编码(ygb/azb)',
  section_code    VARCHAR(32)  NOT NULL COMMENT '栏目编码',
  category_code   VARCHAR(32)  NOT NULL DEFAULT '' COMMENT '分类编码',
  title           VARCHAR(255) NOT NULL DEFAULT '' COMMENT '标题',
  summary         VARCHAR(1000) NOT NULL DEFAULT '' COMMENT '摘要',
  content         TEXT         NULL COMMENT '正文/HTML/JSON',
  cover_url       VARCHAR(500) NOT NULL DEFAULT '' COMMENT '封面/二维码',
  link_url        VARCHAR(500) NOT NULL DEFAULT '' COMMENT '跳转链接',
  source_name     VARCHAR(128) NOT NULL DEFAULT '' COMMENT '来源',
  publish_time    DATETIME     NULL COMMENT '发布时间',
  sort_order      INT          NOT NULL DEFAULT 0 COMMENT '排序',
  status          CHAR(1)      NOT NULL DEFAULT '0' COMMENT '状态(0发布 1停用)',
  extra_json      TEXT         NULL COMMENT '扩展JSON',
  create_by       VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '创建者',
  create_time     DATETIME     NULL COMMENT '创建时间',
  update_by       VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '更新者',
  update_time     DATETIME     NULL COMMENT '更新时间',
  remark          VARCHAR(255) NOT NULL DEFAULT '' COMMENT '备注',
  del_flag        CHAR(1)      NOT NULL DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (content_id),
  KEY idx_portal_section (portal_code, section_code, category_code, status, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门户网站CMS内容';

-- 后台菜单：门户内容管理
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, remark, portal_scope
)
SELECT
  4100, '门户内容管理', 0, 12, 'portalContent', 'ygb/portalContent/index', 'YgbPortalContent',
  1, 0, 'C', '0', '0', 'ygb:portalContent:list', 'documentation',
  'admin', NOW(), '粤工保官网CMS内容管理', 'ygb'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 4100);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 4100 FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 4100);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 2, 4100 FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 2 AND menu_id = 4100);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark, portal_scope)
SELECT 4101, '门户内容查询', 4100, 1, '#', '', '', 1, 0, 'F', '0', '0', 'ygb:portalContent:query', '#', 'admin', NOW(), '', 'ygb'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 4101);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark, portal_scope)
SELECT 4102, '门户内容新增', 4100, 2, '#', '', '', 1, 0, 'F', '0', '0', 'ygb:portalContent:add', '#', 'admin', NOW(), '', 'ygb'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 4102);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark, portal_scope)
SELECT 4103, '门户内容修改', 4100, 3, '#', '', '', 1, 0, 'F', '0', '0', 'ygb:portalContent:edit', '#', 'admin', NOW(), '', 'ygb'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 4103);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark, portal_scope)
SELECT 4104, '门户内容删除', 4100, 4, '#', '', '', 1, 0, 'F', '0', '0', 'ygb:portalContent:remove', '#', 'admin', NOW(), '', 'ygb'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 4104);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu WHERE menu_id IN (4101, 4102, 4103, 4104)
AND NOT EXISTS (SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = 1 AND rm.menu_id = sys_menu.menu_id);

-- 种子数据（ygb 官网）
DELETE FROM ygb_portal_content WHERE portal_code = 'ygb' AND create_by = 'portal_seed';

INSERT INTO ygb_portal_content (portal_code, section_code, category_code, title, summary, content, source_name, publish_time, sort_order, status, extra_json, create_by, create_time, del_flag) VALUES
('ygb', 'site_config', 'brand', '粤工保 · 用工保障与智能监管平台', '劳务派遣·高危防控、合规用工·扩面减损', '', '', NOW(), 1, '0', '{"copyright":"© 2026 广东省人力资源和社会保障厅 版权所有 粤ICP备12345678号"}', 'portal_seed', NOW(), '0'),
('ygb', 'site_config', 'contact', '粤工保平台服务支持', '面向省、市、县人社监管部门以及劳务派遣企业、劳动者提供统一接入与咨询支持。', '', '', NOW(), 2, '0', '{"phone":"020-12333","email":"ygb@guangdong.gov.cn","address":"广州市越秀区教育路 88 号"}', 'portal_seed', NOW(), '0'),
('ygb', 'intro', 'main', '省级用工保障与智能监管门户', '粤工保平台面向劳务派遣行业，整合合同备案、考勤归集、工资监管、社保工伤、高危岗位防控与扩面减损等能力。', '', '', NOW(), 1, '0', '{"pillars":[{"label":"平台定位","value":"省级监管门户"},{"label":"核心价值","value":"合规·防控·减损"},{"label":"政策依据","value":"劳务派遣条例"}]}', 'portal_seed', NOW(), '0'),
('ygb', 'banner', 'slide', '全省劳务派遣企业合规备案突破3800家，参保扩面率达92.6%', '围绕合同备案、考勤强绑定、工资监管和社保工伤数据贯通，平台持续沉淀统一监管数据底座。', '', '省人力资源和社会保障厅', '2026-05-28 00:00:00', 1, '0', '{"tag":"平台动态","meta":"来源：省人力资源和社会保障厅","background":"linear-gradient(135deg, #0a3d7a 0%, #0f5ea8 58%, #2b7fd4 100%)"}', 'portal_seed', NOW(), '0'),
('ygb', 'banner', 'slide', '工伤参保扩面新增2.1万人，漏保整改闭环率提升至96.8%', '通过漏保企业清单、催缴闭环和扩面减损跟踪，推动劳务派遣行业参保水平持续提升。', '', '平台运营中心', '2026-05-25 00:00:00', 2, '0', '{"tag":"扩面减损","meta":"来源：平台运营中心","background":"linear-gradient(135deg, #0c4589 0%, #1568b5 52%, #2a82cc 100%)"}', 'portal_seed', NOW(), '0'),
('ygb', 'banner', 'slide', '深圳"合同+考勤+工资"三位一体监管模式入选省级典型案例', '以数据融合监管为核心，实现劳务派遣全链条合规闭环，事故率同比下降42%。', '', '深圳市人力资源和社会保障局', '2026-05-30 00:00:00', 3, '0', '{"tag":"示范案例","meta":"来源：深圳市人力资源和社会保障局","background":"linear-gradient(135deg, #0e4a92 0%, #1a6cb8 55%, #3589d0 100%)"}', 'portal_seed', NOW(), '0'),
('ygb', 'news', 'policy', '省人社厅印发劳务派遣行业数字化监管指导意见', '明确合同备案、考勤归集、工资监管和社保比对的数据标准与接入时限。', '省人力资源和社会保障厅近日印发《劳务派遣行业数字化监管指导意见》，围绕劳务派遣企业合同备案、考勤归集、工资发放监管、社保工伤数据比对等关键环节，明确全省统一的数据标准、接口规范和接入时限要求。', '省人力资源和社会保障厅', '2026-05-29 00:00:00', 1, '0', '{"categoryLabel":"政策发布"}', 'portal_seed', NOW(), '0'),
('ygb', 'news', 'announcement', '粤工保平台 V5.1 版本上线，新增招聘市场与工会服务模块', '官网门户全面改版，支持政策法规、解决方案、服务指南等八大栏目公开访问。', '粤工保平台 V5.1 版本正式上线，官网门户完成八大栏目改版，新增招聘市场、工会服务等模块，并支持后台 CMS 统一维护公开内容。', '平台运营中心', '2026-05-26 00:00:00', 2, '0', '{"categoryLabel":"平台公告"}', 'portal_seed', NOW(), '0'),
('ygb', 'news', 'industry', '广东省劳务派遣行业协会年会在广州召开', '与会代表就合规用工、扩面减损和行业自律等议题展开深入交流。', '广东省劳务派遣行业协会年会在广州召开，来自全省各地的协会代表、企业负责人和监管部门同志参会，围绕合规用工、扩面减损、行业自律等议题开展深入交流。', '南方日报', '2026-05-22 00:00:00', 3, '0', '{"categoryLabel":"行业新闻"}', 'portal_seed', NOW(), '0'),
('ygb', 'policy', 'national', '中华人民共和国劳动合同法', '规范劳动合同订立、履行、变更、解除和终止，保护劳动者合法权益。', '', '', '2026-04-18 00:00:00', 1, '0', '{"typeLabel":"法律"}', 'portal_seed', NOW(), '0'),
('ygb', 'policy', 'national', '劳务派遣暂行规定', '明确劳务派遣用工比例、同工同酬要求和派遣单位经营许可条件。', '', '', '2026-03-30 00:00:00', 2, '0', '{"typeLabel":"规定"}', 'portal_seed', NOW(), '0'),
('ygb', 'policy', 'national', '工伤保险条例', '规定工伤认定、劳动能力鉴定和工伤保险待遇等制度。', '', '', '2026-02-22 00:00:00', 3, '0', '{"typeLabel":"条例"}', 'portal_seed', NOW(), '0'),
('ygb', 'policy', 'guangdong', '广东省劳务派遣服务规范', '明确劳务派遣服务流程、信息报送和档案管理要求。', '', '', '2026-04-10 00:00:00', 1, '0', '{"typeLabel":"规范"}', 'portal_seed', NOW(), '0'),
('ygb', 'policy', 'guangdong', '广东省工伤预防五年行动计划', '部署高危行业工伤预防培训、隐患排查和扩面参保任务。', '', '', '2026-03-15 00:00:00', 2, '0', '{"typeLabel":"计划"}', 'portal_seed', NOW(), '0'),
('ygb', 'policy', 'interpretation', '一图读懂：劳务派遣合规备案全流程', '以图解方式说明企业注册、资料提交、审核备案和日常维护步骤。', '', '', '2026-05-20 00:00:00', 1, '0', '{"typeLabel":"图文"}', 'portal_seed', NOW(), '0'),
('ygb', 'solution', 'main', '劳务派遣监管', '合同备案、工资监管、考勤强绑定、税务比对全链条闭环。', '', '', NOW(), 1, '0', '{"icon":"📋","features":["合同备案与区块链存证","考勤与工资强绑定校验","个税社保数据碰撞比对"]}', 'portal_seed', NOW(), '0'),
('ygb', 'solution', 'main', '高危岗位保障', '千机万码、AI 监控、安责险、扩面减损协同防控。', '', '', NOW(), 2, '0', '{"icon":"🛡️","features":["设备赋码与远程锁机","AI 行为识别与预警","安责险投保与扩面跟踪"]}', 'portal_seed', NOW(), '0'),
('ygb', 'solution', 'main', '数据融合监管', '企业库、人员库、预警中心、信用评价统一归集。', '', '', NOW(), 3, '0', '{"icon":"🔗","features":["企业风险画像与红黄绿码","预警工单闭环处置","信用评价与差异化监管"]}', 'portal_seed', NOW(), '0'),
('ygb', 'union', 'intro', '工会组织架构', '广东省总工会联合各级人社部门，依托粤工保平台为劳务派遣劳动者提供法律援助、集体协商和权益维护服务。', '', '', NOW(), 1, '0', '{"hotline":"12351","legalAid":"020-83123456"}', 'portal_seed', NOW(), '0'),
('ygb', 'union', 'guide', '法律援助申请流程', '劳动者可通过 12351 热线或平台提交法律援助申请，工会将在 3 个工作日内响应。', '', '', NOW(), 2, '0', '{"typeLabel":"维权指南"}', 'portal_seed', NOW(), '0'),
('ygb', 'union', 'case', '某制造企业集体合同协商经验', '通过集体协商确定工资增长机制和工时安排，保障 1200 余名派遣员工权益。', '', '', NOW(), 3, '0', '{"typeLabel":"协商案例"}', 'portal_seed', NOW(), '0'),
('ygb', 'guide', 'enterprise', '企业入驻指南', '劳务派遣企业注册入驻平台的完整流程。', '', '', NOW(), 1, '0', '{"icon":"🏢","steps":["准备营业执照与许可证","在线提交入驻申请","等待审核并开通账号","完成首批数据接入"]}', 'portal_seed', NOW(), '0'),
('ygb', 'guide', 'worker', '劳动者使用指南', '小程序下载、实名认证与日常操作说明。', '', '', NOW(), 2, '0', '{"icon":"👷","steps":["微信扫码下载小程序","完成实名认证与人脸核验","绑定所在企业与项目","日常打卡/扫码操作"]}', 'portal_seed', NOW(), '0'),
('ygb', 'guide', 'supervisor', '监管端操作指南', '监管部门登录、预警处置与报表查看。', '', '', NOW(), 3, '0', '{"icon":"🖥️","steps":["使用监管账号登录后台","查看驾驶舱与预警中心","签收并处置预警工单","导出统计报表"]}', 'portal_seed', NOW(), '0'),
('ygb', 'about', 'background', '平台建设背景', '为贯彻落实广东省劳务派遣行业规范发展要求，粤工保平台整合人社、应急、税务、工会、保险、银行等多方资源。', '', '', NOW(), 1, '0', '', 'portal_seed', NOW(), '0'),
('ygb', 'about', 'goal', '建设目标与成果', '已实现全省劳务派遣企业归集、合同考勤工资社保数据贯通、预警中心闭环处置、AI 监测报告生成等核心能力。', '', '', NOW(), 2, '0', '', 'portal_seed', NOW(), '0'),
('ygb', 'partner', 'main', '广东省人社厅', '行业监管与政策指导', '', '', NOW(), 1, '0', '{"icon":"🏛️"}', 'portal_seed', NOW(), '0'),
('ygb', 'partner', 'main', '广东省应急管理厅', '高危岗位安全治理', '', '', NOW(), 2, '0', '{"icon":"🚨"}', 'portal_seed', NOW(), '0'),
('ygb', 'partner', 'main', '广东省总工会', '劳动者权益维护', '', '', NOW(), 3, '0', '{"icon":"✊"}', 'portal_seed', NOW(), '0'),
('ygb', 'download', 'miniprogram', '劳动者小程序', '打卡考勤、工资查询、法律援助', '', '', NOW(), 1, '0', '{"codeLabel":"劳动者端"}', 'portal_seed', NOW(), '0'),
('ygb', 'download', 'miniprogram', '企业端小程序', '人员管理、合同备案、预警处置', '', '', NOW(), 2, '0', '{"codeLabel":"企业端"}', 'portal_seed', NOW(), '0'),
('ygb', 'download', 'app', '劳务屏 APP', '工地现场信息展示与考勤终端', '', '', NOW(), 3, '0', '{"icon":"📺"}', 'portal_seed', NOW(), '0'),
('ygb', 'download', 'app', '监管端 APP', '移动监管、预警处置与报表查看', '', '', NOW(), 4, '0', '{"icon":"📊"}', 'portal_seed', NOW(), '0'),
('ygb', 'download', 'manual', '粤工保平台操作手册', '官网门户、监管后台和角色入口使用说明。', '', '', NOW(), 5, '0', '{"fileType":"PDF"}', 'portal_seed', NOW(), '0'),
('ygb', 'job_guide', 'main', '简历制作技巧', '突出技能证书与工作经验，使用平台简历模板可一键生成。', '', '', NOW(), 1, '0', '', 'portal_seed', NOW(), '0'),
('ygb', 'job_guide', 'main', '面试注意事项', '提前了解企业背景，准备相关证件，注意劳动合同条款审核。', '', '', NOW(), 2, '0', '', 'portal_seed', NOW(), '0'),
('ygb', 'quick_entry', 'login', '管理端登录', '监管与企业后台', '', '', NOW(), 1, '0', '{"icon":"🔑","entryType":"login"}', 'portal_seed', NOW(), '0'),
('ygb', 'quick_entry', 'miniprogram', '劳动者小程序', '扫码即用', '', '', NOW(), 2, '0', '{"icon":"📱","entryType":"anchor","anchor":"download"}', 'portal_seed', NOW(), '0'),
('ygb', 'quick_entry', 'app', 'APP 下载', '劳务屏/监管端', '', '', NOW(), 3, '0', '{"icon":"📲","entryType":"anchor","anchor":"download"}', 'portal_seed', NOW(), '0');
