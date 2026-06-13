SET NAMES utf8mb4;

-- 为最新动态补充正文（种子数据原先仅有摘要）
UPDATE ygb_portal_content SET content = '省人力资源和社会保障厅近日印发《劳务派遣行业数字化监管指导意见》，围绕劳务派遣企业合同备案、考勤归集、工资发放监管、社保工伤数据比对等关键环节，明确全省统一的数据标准、接口规范和接入时限要求。文件要求各地市按照“应接尽接、应传尽传”原则，推动劳务派遣企业尽快完成平台接入，并通过数据碰撞比对及时发现漏保、欠薪等风险。'
WHERE portal_code = 'ygb' AND section_code = 'news' AND title = '省人社厅印发劳务派遣行业数字化监管指导意见' AND (content IS NULL OR content = '');

UPDATE ygb_portal_content SET content = '粤工保平台 V5.1 版本正式上线，官网门户完成八大栏目改版，新增招聘市场、工会服务等模块。本次升级优化了公开访问体验，支持政策法规、解决方案、服务指南等栏目统一由后台 CMS 维护，并完善了数据看板脱敏展示与岗位招聘公开查询能力。'
WHERE portal_code = 'ygb' AND section_code = 'news' AND title LIKE '粤工保平台 V5.1%' AND (content IS NULL OR content = '');

UPDATE ygb_portal_content SET content = '广东省劳务派遣行业协会年会在广州召开，来自全省各地的协会代表、企业负责人和监管部门同志参会，围绕合规用工、扩面减损、行业自律等议题开展深入交流，并就粤工保平台应用推广经验进行分享。'
WHERE portal_code = 'ygb' AND section_code = 'news' AND title = '广东省劳务派遣行业协会年会在广州召开' AND (content IS NULL OR content = '');

UPDATE ygb_portal_content SET content = '省应急管理厅推广“安责保”平台，要求高危行业企业年底前全面接入。平台将统一承接安责险投保、事故预防、设备授权、AI 预警等核心业务链路，推动安全生产监管由事后处置向事前预防转变。'
WHERE portal_code = 'azb' AND section_code = 'news' AND title LIKE '省应急管理厅推广%' AND (content IS NULL OR content = '');

UPDATE ygb_portal_content SET content = '平台完成新一轮广东测试数据沉淀，覆盖省、市、企业多类角色账户，支撑官网展示与后台监管联动演示，为分级监管和门户内容维护提供完整样例数据。'
WHERE portal_code = 'azb' AND section_code = 'news' AND title LIKE '平台完成新一轮%' AND (content IS NULL OR content = '');
