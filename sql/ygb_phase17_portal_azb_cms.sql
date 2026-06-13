SET NAMES utf8mb4;

DELETE FROM ygb_portal_content WHERE portal_code = 'azb' AND create_by = 'portal_seed';

INSERT INTO ygb_portal_content (portal_code, section_code, category_code, title, summary, content, source_name, publish_time, sort_order, status, extra_json, create_by, create_time, del_flag) VALUES
('azb', 'site_config', 'brand', '安责保 · 广东省安全生产风险防范AI监管平台', '安责险覆盖 · 设备智控 · 风险减量 · 隐患闭环', '', '', NOW(), 1, '0', '{"copyright":"© 2026 广东省应急管理厅 版权所有 粤ICP备12345678号"}', 'portal_seed', NOW(), '0'),
('azb', 'site_config', 'contact', '安责保平台服务支持', '面向省、市、县监管部门以及企业、保险、服务机构提供统一接入与咨询支持。', '', '', NOW(), 2, '0', '{"phone":"020-83312345","email":"abj@guangdong.gov.cn","address":"广州市越秀区建设大马路19号"}', 'portal_seed', NOW(), '0'),
('azb', 'intro', 'main', '省级安责保监管门户', '依托粤工保框架延伸建设面向公众展示、政策发布、方案宣介和数据开放的官网门户，统一展示平台能力、接入成果、重点资讯与下载资料。', '', '', NOW(), 1, '0', '{"pillars":[{"label":"监管部门接入","value":"21个"},{"label":"高危企业归集","value":"2380家"},{"label":"设备在线监管","value":"15600+"}]}', 'portal_seed', NOW(), '0'),
('azb', 'banner', 'slide', '全省安责险投保率突破90%，事故预防服务费投入超2亿元', '围绕高危行业企业安责险投保、设备赋码接入和风险减量服务，平台持续沉淀统一监管数据。', '', '省应急管理厅', '2026-05-28 00:00:00', 1, '0', '{"tag":"平台动态","meta":"来源：省应急管理厅","background":"linear-gradient(135deg, #0f2b3d 0%, #244c72 58%, #3d6f94 100%)"}', 'portal_seed', NOW(), '0'),
('azb', 'banner', 'slide', '千机万码设备安装突破1.5万台，违规开机率下降67%', '芯片授权、远程锁机、AI事件采集和设备日志闭环已形成标准化监管能力。', '', '平台运营中心', '2026-05-25 00:00:00', 2, '0', '{"tag":"设备智控","meta":"来源：平台运营中心","background":"linear-gradient(135deg, #163a57 0%, #21557f 52%, #2d7aa5 100%)"}', 'portal_seed', NOW(), '0'),
('azb', 'banner', 'slide', '东莞"AI+安责险"模式赔付案件下降58%，入选国家级案例', '通过隐患预警、事故预防项目和保险服务协同，推动安全生产治理从事后补偿转向事前减量。', '', '东莞市应急管理局', '2026-05-30 00:00:00', 3, '0', '{"tag":"示范案例","meta":"来源：东莞市应急管理局","background":"linear-gradient(135deg, #1d405d 0%, #355b7a 55%, #4f7ea4 100%)"}', 'portal_seed', NOW(), '0'),
('azb', 'news', 'policy', '省应急管理厅推广"安责保"平台，要求高危行业年底前全面接入', '平台将统一承接安责险投保、事故预防、设备授权、AI预警等核心业务链路。', '', '省应急管理厅', '2026-05-29 00:00:00', 1, '0', '{"categoryLabel":"政策发布"}', 'portal_seed', NOW(), '0'),
('azb', 'news', 'announcement', '平台完成新一轮广东测试数据沉淀，支持分级监管演示', '覆盖省、市、企业多类角色账户，支撑官网展示与后台监管联动说明。', '', '平台运营中心', '2026-05-26 00:00:00', 2, '0', '{"categoryLabel":"平台公告"}', 'portal_seed', NOW(), '0'),
('azb', 'policy', 'national', '安全生产责任保险实施办法', '明确高危行业企业安责险投保责任、服务要求及监管职责。', '', '', '2026-04-18 00:00:00', 1, '0', '{"typeLabel":"法规"}', 'portal_seed', NOW(), '0'),
('azb', 'policy', 'national', '关于推进事故预防服务数字化监管的工作通知', '要求统一归集事故预防项目、服务过程和设备监管数据。', '', '', '2026-03-30 00:00:00', 2, '0', '{"typeLabel":"通知"}', 'portal_seed', NOW(), '0'),
('azb', 'policy', 'guangdong', '高危作业设备授权与监测接入规范', '规范设备身份标识、授权流程、日志留存和AI事件上报接口。', '', '', '2026-02-22 00:00:00', 1, '0', '{"typeLabel":"指引"}', 'portal_seed', NOW(), '0'),
('azb', 'solution', 'main', '安责险全流程监管', '在线投保、预防服务委托、资金监管、赔付分析和费率浮动统一闭环。', '', '', NOW(), 1, '0', '{"icon":"🔐","features":["在线投保与保单核验","预防服务资金监管","赔付分析与费率浮动"]}', 'portal_seed', NOW(), '0'),
('azb', 'solution', 'main', '千机万码设备智控', '物联网芯片、电子围栏、远程锁机和授权日志统一纳入平台监管。', '', '', NOW(), 2, '0', '{"icon":"⚙️","features":["设备赋码与授权管理","远程锁机与心跳监测","AI事件与日志闭环"]}', 'portal_seed', NOW(), '0'),
('azb', 'solution', 'main', 'AI风险监测预警', '明火、离岗、未佩戴防护等行为实时识别，并同步写入预警中心。', '', '', NOW(), 3, '0', '{"icon":"🤖","features":["AI行为识别","实时预警推送","预警工单闭环"]}', 'portal_seed', NOW(), '0'),
('azb', 'solution', 'main', '企业风险画像与信用评价', '基于投保、设备、隐患整改与事故预防数据形成差异化监管依据。', '', '', NOW(), 4, '0', '{"icon":"📊","features":["企业风险画像","红黄绿码分级","信用评价与差异化监管"]}', 'portal_seed', NOW(), '0'),
('azb', 'download', 'manual', '平台操作手册', '官网门户、监管后台和角色入口使用说明。', '', '', NOW(), 1, '0', '{"fileType":"PDF"}', 'portal_seed', NOW(), '0'),
('azb', 'download', 'manual', '设备接入指南', '设备授权、心跳、AI事件与日志上报规范说明。', '', '', NOW(), 2, '0', '{"fileType":"DOC"}', 'portal_seed', NOW(), '0'),
('azb', 'download', 'manual', '制度模板包', '安责险服务、事故预防、设备运维等模板文件。', '', '', NOW(), 3, '0', '{"fileType":"ZIP"}', 'portal_seed', NOW(), '0'),
('azb', 'quick_entry', 'login', '管理端登录', '监管与企业后台', '', '', NOW(), 1, '0', '{"icon":"🔑","entryType":"login"}', 'portal_seed', NOW(), '0');
