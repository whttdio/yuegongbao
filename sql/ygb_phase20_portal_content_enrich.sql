SET NAMES utf8mb4;

-- 粤工保 / 安责保 门户 CMS 内容 enrichment（正式文稿 + 富文本 HTML）
-- 按 portal_code + section_code + title 匹配更新，不覆盖 link_url / cover_url
-- 执行：mysql -u root -p yuegongbao < sql/ygb_phase20_portal_content_enrich.sql

/* ===================== 粤工保 ygb ===================== */

-- 平台简介
UPDATE ygb_portal_content SET
  summary = '粤工保平台是广东省人力资源和社会保障厅主导建设的省级劳务派遣行业数字化监管与用工保障平台，围绕“合规用工、风险防控、扩面减损”目标，构建全省统一的数据底座与协同监管体系。',
  content = '<p><strong>一、平台定位</strong></p><p>粤工保平台面向全省各级人社监管部门、劳务派遣企业、用工单位及广大劳动者，提供合同备案、考勤归集、工资监管、社保工伤比对、高危岗位防控、扩面减损跟踪等一体化服务，是广东省劳务派遣行业规范发展的基础性、枢纽性平台。</p><p><strong>二、建设原则</strong></p><ul><li>统一标准：全省统一数据接口、业务规则与编码规范；</li><li>分级监管：省、市、县三级联动，权限分级、数据分级展示；</li><li>闭环处置：预警发现、签收分派、整改反馈、复核销号全流程留痕；</li><li>安全可控：敏感信息脱敏展示，关键操作全程审计。</li></ul><p><strong>三、服务对象</strong></p><p>平台现已服务全省 21 个地市人社监管部门，归集劳务派遣企业 3800 余家，覆盖派遣劳动者 28 万余人，并与应急、税务、工会、保险等机构建立数据协同机制。</p>',
  extra_json = '{"pillars":[{"label":"平台定位","value":"省级监管门户"},{"label":"服务地市","value":"21个"},{"label":"核心价值","value":"合规·防控·减损"}]}',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'intro' AND category_code = 'main';

-- 轮播图正文（点击可查看详情）
UPDATE ygb_portal_content SET
  content = '<p>截至 2026 年 5 月，全省通过粤工保平台完成合规备案的劳务派遣企业已突破 <strong>3800 家</strong>，劳务派遣劳动者工伤参保扩面率达到 <strong>92.6%</strong>。平台通过合同、考勤、工资、社保四类核心数据贯通，为监管部门提供“一企一档、一人一档”的数字化监管视图。</p><p>下一步，省人社厅将继续推进未接入企业清单化管理，确保应备尽备、应接尽接。</p>',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'banner' AND title LIKE '全省劳务派遣企业合规备案突破3800家%';

UPDATE ygb_portal_content SET
  content = '<p>2026 年以来，全省通过漏保企业清单排查、催缴闭环和扩面减损专项督导，新增工伤参保 <strong>2.1 万人</strong>，漏保整改闭环率提升至 <strong>96.8%</strong>。平台已建立“发现—催办—复核—销号”四步工作机制，并按月通报各地扩面进展。</p>',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'banner' AND title LIKE '工伤参保扩面新增2.1万人%';

UPDATE ygb_portal_content SET
  content = '<p>深圳市人力资源和社会保障局创新实施“合同备案 + 考勤归集 + 工资监管”三位一体监管模式，实现劳务派遣全链条数据比对与闭环管理。该模式事故发生率同比下降 <strong>42%</strong>，已作为省级典型案例在全省推广。</p>',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'banner' AND title LIKE '深圳%三位一体监管模式%';

-- 最新动态
UPDATE ygb_portal_content SET
  summary = '明确全省劳务派遣行业数字化监管数据标准、接口规范与接入时限，推动“应接尽接、应传尽传”。',
  content = '<p><strong>【文件背景】</strong></p><p>为进一步落实《劳务派遣暂行规定》及广东省劳务派遣行业规范发展要求，省人力资源和社会保障厅近日正式印发《劳务派遣行业数字化监管指导意见》。</p><p><strong>【主要内容】</strong></p><ul><li>统一合同备案、考勤归集、工资发放、社保工伤比对的数据标准与字段规范；</li><li>明确企业接入平台的技术路线、接口版本及验收要求；</li><li>建立省、市、县三级数据质量通报与考核机制；</li><li>对未按时接入、数据造假等行为明确监管措施。</li></ul><p><strong>【工作要求】</strong></p><p>各地市人社部门应于 2026 年 12 月底前完成辖区内劳务派遣企业平台接入督导，并通过粤工保平台按月报送扩面减损工作进展。</p>',
  source_name = '广东省人力资源和社会保障厅',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'news' AND category_code = 'policy';

UPDATE ygb_portal_content SET
  summary = '粤工保平台 V5.1 正式发布，官网门户全面改版，新增招聘市场、工会服务等模块。',
  content = '<p><strong>【版本概述】</strong></p><p>粤工保平台 V5.1 版本于 2026 年 5 月 26 日正式上线。本次升级聚焦“门户公开服务 + 后台协同监管”双轮驱动，全面提升用户体验与内容运营能力。</p><p><strong>【主要更新】</strong></p><ul><li>官网门户改版：政策法规、解决方案、工会服务、招聘市场等八大栏目公开访问；</li><li>门户 CMS：支持富文本内容维护、附件下载与多门户（粤工保/安责保）统一管理；</li><li>招聘市场：公开岗位信息发布与检索，对接劳动者小程序；</li><li>数据看板：核心指标脱敏展示，支持趋势分析。</li></ul><p><strong>【后续计划】</strong></p><p>平台将持续优化移动端体验，拓展 AI 监测报告与信用评价等能力，欢迎各接入单位通过官网“服务指南”栏目获取操作手册。</p>',
  source_name = '粤工保平台运营中心',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'news' AND category_code = 'announcement';

UPDATE ygb_portal_content SET
  summary = '全省劳务派遣行业代表齐聚广州，共商合规用工与行业自律发展路径。',
  content = '<p><strong>【会议概况】</strong></p><p>2026 年 5 月 22 日，广东省劳务派遣行业协会年会在广州召开。省人社厅相关处室负责同志、21 个地市协会代表及重点劳务派遣企业负责人约 300 人参加会议。</p><p><strong>【议题聚焦】</strong></p><ul><li>劳务派遣行业合规用工与同工同酬落实；</li><li>粤工保平台接入经验与数据质量提升；</li><li>工伤参保扩面与漏保整改闭环；</li><li>行业自律公约修订与信用评价衔接。</li></ul><p><strong>【会议成果】</strong></p><p>会议发布了《广东省劳务派遣行业 2026 年度自律倡议》，并选举产生新一届协会理事会，将进一步发挥桥梁纽带作用，促进行业规范健康发展。</p>',
  source_name = '南方日报',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'news' AND category_code = 'industry';

-- 政策法规
UPDATE ygb_portal_content SET
  summary = '我国劳动领域的基础性法律，规范劳动合同订立、履行、变更、解除和终止，全面保护劳动者合法权益。',
  content = '<p>《中华人民共和国劳动合同法》于 2008 年 1 月 1 日起施行，是规范劳动关系、维护职工合法权益、构建和谐劳动关系的重要法律依据。</p><p><strong>与劳务派遣相关要点：</strong></p><ul><li>明确劳动合同应当以书面形式订立，并载明法定必备条款；</li><li>规范试用期、工时休息、劳动报酬及解除终止条件；</li><li>强调同工同酬，对被派遣劳动者与本单位同类岗位劳动者实行相同的劳动报酬分配办法；</li><li>为劳务派遣用工形式提供法律框架，与《劳务派遣暂行规定》配套适用。</li></ul>',
  source_name = '全国人民代表大会常务委员会',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'policy' AND category_code = 'national' AND title = '中华人民共和国劳动合同法';

UPDATE ygb_portal_content SET
  summary = '明确劳务派遣用工比例、经营许可、同工同酬、工伤保障等关键监管要求。',
  content = '<p>《劳务派遣暂行规定》自 2014 年 3 月 1 日起施行，人力资源和社会保障部令第 22 号发布，是规范劳务派遣用工的重要部门规章。</p><p><strong>核心条款摘要：</strong></p><ul><li>用工单位应当严格控制劳务派遣用工数量，使用的被派遣劳动者数量不得超过其用工总量的 10%；</li><li>劳务派遣单位应当与被派遣劳动者订立二年以上的固定期限劳动合同；</li><li>被派遣劳动者享有与用工单位的劳动者同工同酬的权利；</li><li>劳务派遣单位应当依法为被派遣劳动者参加社会保险，并承担用人单位的法定义务。</li></ul>',
  source_name = '人力资源和社会保障部',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'policy' AND category_code = 'national' AND title = '劳务派遣暂行规定';

UPDATE ygb_portal_content SET
  summary = '规定工伤认定、劳动能力鉴定、工伤保险待遇及用人单位责任等制度安排。',
  content = '<p>《工伤保险条例》是保障因工作遭受事故伤害或患职业病职工获得医疗救治和经济补偿的行政法规，适用于各类用人单位，包括劳务派遣单位。</p><p><strong>监管关注要点：</strong></p><ul><li>用人单位应当按时足额缴纳工伤保险费，未参保职工发生工伤的由用人单位承担待遇；</li><li>工伤认定、劳动能力鉴定程序及待遇标准；</li><li>劳务派遣中工伤责任分担与保险衔接；</li><li>与粤工保平台社保工伤数据比对、漏保预警机制相衔接。</li></ul>',
  source_name = '国务院',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'policy' AND category_code = 'national' AND title = '工伤保险条例';

UPDATE ygb_portal_content SET
  summary = '规范广东省劳务派遣服务流程、信息报送、档案管理与服务质量要求。',
  content = '<p>《广东省劳务派遣服务规范》由广东省人力资源和社会保障厅组织制定，旨在提升劳务派遣服务标准化、规范化水平。</p><p><strong>主要规范内容：</strong></p><ul><li>劳务派遣单位服务流程与岗位管理规范；</li><li>劳动者档案、劳动合同、社保缴费凭证管理要求；</li><li>向监管部门信息报送的频次、内容与格式；</li><li>服务质量评价与投诉处理机制。</li></ul>',
  source_name = '广东省人力资源和社会保障厅',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'policy' AND category_code = 'guangdong' AND title = '广东省劳务派遣服务规范';

UPDATE ygb_portal_content SET
  summary = '部署全省高危行业工伤预防培训、隐患排查治理与参保扩面重点任务。',
  content = '<p>《广东省工伤预防五年行动计划（2026—2030 年）》由省人社厅、应急管理厅、卫生健康委员会联合印发，明确未来五年工伤预防工作路线图。</p><p><strong>重点任务：</strong></p><ul><li>面向危化、建筑、制造等高危行业开展工伤预防培训；</li><li>建立隐患排查治理台账，推动重大隐患闭环整改；</li><li>将工伤预防与粤工保平台预警中心、扩面减损模块深度衔接；</li><li>完善预防项目资金监管与绩效评估机制。</li></ul>',
  source_name = '广东省人力资源和社会保障厅',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'policy' AND category_code = 'guangdong' AND title = '广东省工伤预防五年行动计划';

UPDATE ygb_portal_content SET
  summary = '图解劳务派遣企业从注册入驻、资料提交、审核备案到日常维护的全流程操作指引。',
  content = '<p><strong>第一步：企业注册</strong> — 访问粤工保管理端，使用统一社会信用代码完成企业账号注册与实名认证。</p><p><strong>第二步：资料提交</strong> — 上传营业执照、劳务派遣经营许可证、法人身份证明等必备材料。</p><p><strong>第三步：审核备案</strong> — 属地人社部门在线审核，通过后开通数据接入权限。</p><p><strong>第四步：数据接入</strong> — 按接口规范推送合同、考勤、工资、社保等首批数据，完成验收。</p><p><strong>第五步：日常维护</strong> — 按月更新业务数据，及时处置平台预警工单，参加年度合规评价。</p>',
  source_name = '粤工保平台运营中心',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'policy' AND category_code = 'interpretation';

-- 解决方案
UPDATE ygb_portal_content SET
  summary = '以合同备案为牵引，串联考勤、工资、社保、税务数据，构建劳务派遣全链条合规监管闭环。',
  content = '<p><strong>方案概述</strong></p><p>针对劳务派遣行业“用工链条长、数据分散、监管难”的痛点，粤工保平台提供覆盖“备案—归集—比对—预警—处置”的全流程监管解决方案。</p><p><strong>核心能力</strong></p><ul><li><strong>合同备案与区块链存证：</strong>劳动合同在线备案，关键条款结构化存储，支持存证溯源；</li><li><strong>考勤与工资强绑定：</strong>考勤记录与工资发放数据自动关联校验，异常自动预警；</li><li><strong>个税社保碰撞比对：</strong>对接税务、社保数据，识别漏保、少缴、虚报等风险；</li><li><strong>预警工单闭环：</strong>分级预警、限时签收、整改复核、销号归档全程留痕。</li></ul>',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'solution' AND title = '劳务派遣监管';

UPDATE ygb_portal_content SET
  summary = '融合千机万码、AI 监控、安责险与扩面减损，构筑高危岗位立体防控体系。',
  content = '<p><strong>方案概述</strong></p><p>面向建筑施工、危化、制造等高危行业派遣岗位，粤工保平台与安责保平台协同，实现“设备可管、人员可溯、风险可预警、保险可衔接”。</p><p><strong>核心能力</strong></p><ul><li><strong>设备赋码与远程锁机：</strong>特种设备及关键机具“一机一码”，未授权人员无法开机；</li><li><strong>AI 行为识别与预警：</strong>明火、离岗、未佩戴防护用品等行为实时识别；</li><li><strong>安责险投保跟踪：</strong>高危岗位安责险投保状态与平台人员库联动；</li><li><strong>扩面减损闭环：</strong>漏保清单、催缴、复核、销号一体化管理。</li></ul>',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'solution' AND title = '高危岗位保障';

UPDATE ygb_portal_content SET
  summary = '统一归集企业库、人员库、预警中心与信用评价，打造省级劳务派遣监管“一张图”。',
  content = '<p><strong>方案概述</strong></p><p>粤工保平台汇聚全省劳务派遣企业、派遣劳动者、合同考勤工资社保、预警处置、信用评价等多维数据，为各级监管部门提供数据融合分析与决策支持能力。</p><p><strong>核心能力</strong></p><ul><li><strong>企业风险画像：</strong>基于合规、参保、预警、信用等维度生成企业风险评分；</li><li><strong>红黄绿码分级：</strong>直观展示企业风险等级，支持差异化监管策略；</li><li><strong>预警中心：</strong>多源预警统一汇聚、分派、跟踪、销号；</li><li><strong>统计报表：</strong>支持按区域、行业、时间维度导出监管报表。</li></ul>',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'solution' AND title = '数据融合监管';

-- 工会服务
UPDATE ygb_portal_content SET
  summary = '广东省总工会联合各级人社部门，依托粤工保平台为劳务派遣劳动者提供法律援助、集体协商和权益维护服务。',
  content = '<p>广东省总工会与省人力资源和社会保障厅建立协作机制，在粤工保平台设立工会服务专区，为劳务派遣劳动者提供以下服务：</p><ul><li><strong>12351 职工维权热线：</strong>7×24 小时受理咨询与投诉；</li><li><strong>法律援助：</strong>为符合条件的劳动者提供免费法律咨询与代理；</li><li><strong>集体协商指导：</strong>推动派遣员工参与集体合同协商；</li><li><strong>困难帮扶：</strong>衔接工会帮扶资源，关爱困难职工群体。</li></ul><p>服务热线：<strong>12351</strong>　法律援助：<strong>020-83123456</strong></p>',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'union' AND category_code = 'intro';

UPDATE ygb_portal_content SET
  summary = '劳动者可通过 12351 热线或粤工保平台在线提交法律援助申请，工会将在 3 个工作日内响应。',
  content = '<p><strong>申请条件</strong></p><p>劳务派遣劳动者因劳动权益受到侵害，且经济困难无力承担法律服务费用的，可申请工会法律援助。</p><p><strong>办理流程</strong></p><ol><li>拨打 12351 热线或在粤工保劳动者小程序提交申请；</li><li>工会工作人员 3 个工作日内联系核实；</li><li>符合条件的，指派律师提供法律咨询或代理；</li><li>案件办结后反馈结果并归档。</li></ol>',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'union' AND category_code = 'guide';

UPDATE ygb_portal_content SET
  summary = '通过集体协商确定工资增长机制和工时安排，保障 1200 余名派遣员工合法权益的典型案例。',
  content = '<p><strong>案例背景</strong></p><p>某大型制造企业使用劳务派遣员工 1200 余人，涉及多个生产班组。2025 年以来，企业工会与劳务派遣单位、用工单位三方开展集体协商。</p><p><strong>协商成果</strong></p><ul><li>确定年度工资增长机制，不低于企业同类岗位平均水平；</li><li>规范加班审批与补休制度，保障休息休假权利；</li><li>建立派遣员工职业发展通道与培训计划；</li><li>协商结果通过粤工保平台备案存证。</li></ul>',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'union' AND category_code = 'case';

-- 服务指南
UPDATE ygb_portal_content SET
  summary = '指导劳务派遣企业完成粤工保平台注册、资质审核、账号开通及首批数据接入的全流程说明。',
  content = '<p>本指南适用于在广东省行政区域内依法取得劳务派遣经营许可的企业。</p><p><strong>入驻流程</strong></p><ol><li>准备材料：营业执照、劳务派遣经营许可证、法人身份证、对接联系人信息；</li><li>在线注册：访问管理端登录页，选择“企业入驻”完成账号注册；</li><li>提交审核：上传资质材料，等待属地人社部门审核（一般 5 个工作日）；</li><li>开通权限：审核通过后获取 API 密钥与操作手册；</li><li>数据接入：按接口规范完成合同、人员、考勤等首批数据推送并通过验收。</li></ol>',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'guide' AND category_code = 'enterprise';

UPDATE ygb_portal_content SET
  summary = '介绍劳动者通过粤工保小程序完成实名认证、绑定企业、日常打卡及权益查询的操作方法。',
  content = '<p>粤工保劳动者小程序面向劳务派遣从业人员，提供实名认证、考勤打卡、工资查询、法律援助等便捷服务。</p><p><strong>使用步骤</strong></p><ol><li>微信扫描官网“下载中心”二维码进入小程序；</li><li>使用手机号注册并完成实名认证与人脸核验；</li><li>输入企业邀请码或扫描企业二维码绑定所在派遣单位；</li><li>日常可通过小程序完成打卡、查看工资条、提交维权咨询。</li></ol>',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'guide' AND category_code = 'worker';

UPDATE ygb_portal_content SET
  summary = '面向各级人社监管人员的平台登录、驾驶舱查看、预警处置与报表导出操作指引。',
  content = '<p>监管端用户由上级人社部门统一开通账号，按属地与权限分级管理。</p><p><strong>常用功能</strong></p><ol><li>登录监管后台，查看辖区驾驶舱核心指标；</li><li>进入预警中心，签收并分派预警工单；</li><li>跟踪企业整改进度，复核后销号；</li><li>按需导出企业合规、参保扩面等统计报表；</li><li>使用监管端 APP 实现移动办公与现场核查。</li></ol>',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'guide' AND category_code = 'supervisor';

-- 关于我们
UPDATE ygb_portal_content SET
  summary = '为贯彻落实广东省劳务派遣行业规范发展要求，整合多方资源建设省级用工保障与智能监管平台。',
  content = '<p>近年来，广东省劳务派遣行业规模持续扩大，在促进就业、满足企业灵活用工需求方面发挥重要作用。与此同时，合同备案不规范、考勤工资脱节、社保工伤漏保等问题仍较突出，传统监管手段难以适应数字化治理需要。</p><p>在此背景下，广东省人力资源和社会保障厅牵头建设粤工保平台，联合应急管理、税务、工会、保险、银行等部门，构建全省统一的劳务派遣行业数字化监管体系，实现“数据多跑路、监管更精准、服务更高效”。</p>',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'about' AND category_code = 'background';

UPDATE ygb_portal_content SET
  summary = '平台已实现企业归集、数据贯通、预警闭环、AI 报告等核心能力，持续深化扩面减损与信用评价应用。',
  content = '<p><strong>建设目标</strong></p><ul><li>建成全省劳务派遣行业统一数据底座；</li><li>实现合同、考勤、工资、社保工伤数据全链条贯通；</li><li>建立分级预警与闭环处置机制；</li><li>形成企业合规信用评价体系。</li></ul><p><strong>阶段成果</strong></p><p>截至 2026 年 5 月，平台已归集劳务派遣企业 3800 余家，接入 21 个地市监管部门，预警闭环率达 96.8%，并上线 AI 监测报告、招聘市场、工会服务等模块，门户公开服务能力显著增强。</p>',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'about' AND category_code = 'goal';

-- 求职指南
UPDATE ygb_portal_content SET
  summary = '帮助劳务派遣从业人员制作规范、专业的求职简历，突出技能证书与工作经验。',
  content = '<p><strong>简历撰写要点</strong></p><ul><li>基本信息真实完整，联系方式保持畅通；</li><li>突出与目标岗位匹配的技能证书（如特种作业操作证、职业资格证）；</li><li>按时间倒序列明工作经历，注明派遣单位与用工单位；</li><li>可使用粤工保劳动者小程序“一键生成简历”功能；</li><li>避免虚假陈述，确保与平台实名信息一致。</li></ul>',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'job_guide' AND title = '简历制作技巧';

UPDATE ygb_portal_content SET
  summary = '提醒劳动者面试前做好企业背景了解、证件准备及劳动合同条款审核等事项。',
  content = '<p><strong>面试前</strong></p><ul><li>通过粤工保平台或企业信用信息公示系统了解派遣单位、用工单位资质；</li><li>准备身份证、技能证书、健康证明等材料；</li><li>确认工作地点、岗位内容、薪资结构与社保缴纳主体。</li></ul><p><strong>签约时</strong></p><ul><li>认真阅读劳动合同，重点关注合同期限、工作地点、工时、报酬、社保工伤条款；</li><li>保留合同原件或电子版，可通过平台查询备案状态；</li><li>遇有疑问可拨打 12333 或 12351 咨询。</li></ul>',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'job_guide' AND title = '面试注意事项';

-- 下载说明（不覆盖 link_url）
UPDATE ygb_portal_content SET
  summary = '涵盖官网门户浏览、监管后台操作、企业端与劳动者端入口使用的完整说明文档。',
  content = '<p>本手册适用于粤工保平台各类用户，包括监管部门工作人员、劳务派遣企业管理员及劳动者。内容包括：账号登录、菜单导航、数据查询、预警处置、报表导出及常见问题解答等章节。请在下载中心获取最新版本。</p>',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'ygb' AND section_code = 'download' AND category_code = 'manual' AND title = '粤工保平台操作手册';


/* ===================== 安责保 azb ===================== */

UPDATE ygb_portal_content SET
  summary = '安责保平台是广东省应急管理厅主导建设的省级安全生产风险防范与安责险数字化监管平台，实现安责险、设备智控、风险减量、隐患闭环一体化管理。',
  content = '<p><strong>一、平台定位</strong></p><p>安责保平台面向全省各级应急监管部门、高危行业企业、保险机构及技术服务机构，提供安责险投保监管、事故预防项目管理、千机万码设备智控、AI 风险监测预警等能力，推动安全生产治理模式向事前预防转型。</p><p><strong>二、建设成效</strong></p><p>平台已接入 21 个地市监管部门，归集高危企业 2380 余家，加芯赋码设备 15600 余台，安责险投保率达 94.2%，事故预防服务费累计投入超 2 亿元。</p>',
  extra_json = '{"pillars":[{"label":"监管地市","value":"21个"},{"label":"高危企业","value":"2380家"},{"label":"设备在线","value":"15600+"}]}',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'azb' AND section_code = 'intro' AND category_code = 'main';

UPDATE ygb_portal_content SET
  summary = '省应急管理厅发文推广安责保平台，要求高危行业企业 2026 年底前全面完成平台接入。',
  content = '<p><strong>【通知要点】</strong></p><p>省应急管理厅近日印发通知，要求矿山、危化、烟花爆竹、建筑施工、交通运输等重点行业领域企业，于 2026 年 12 月底前完成安责保平台接入，实现安责险投保、事故预防、设备授权、AI 预警等数据统一归集。</p><p><strong>【接入内容】</strong></p><ul><li>安责险保单信息与投保状态；</li><li>事故预防服务项目、资金使用情况；</li><li>高危设备赋码、授权与运行日志；</li><li>AI 监测事件与隐患整改闭环数据。</li></ul>',
  source_name = '广东省应急管理厅',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'azb' AND section_code = 'news' AND category_code = 'policy';

UPDATE ygb_portal_content SET
  summary = '平台完成省、市、县三级监管及企业多角色测试数据沉淀，支撑官网展示与分级监管演示。',
  content = '<p>为更好支撑全省分级监管演示与门户内容运营，安责保平台于 2026 年 5 月完成新一轮广东测试数据沉淀，覆盖省级、市级、区县级监管账户及典型高危企业样本，可用于驾驶舱展示、预警流转演示和官网各栏目内容联调。</p>',
  source_name = '安责保平台运营中心',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'azb' AND section_code = 'news' AND category_code = 'announcement';

UPDATE ygb_portal_content SET
  summary = '明确高危行业企业安责险投保责任、事故预防服务要求及监管部门职责分工。',
  content = '<p>《安全生产责任保险实施办法》由国家安全监管总局、财政部、工业和信息化部等多部门联合印发，旨在发挥安责险在事故预防、风险管控和理赔补偿方面的制度功能。</p><p><strong>重点要求：</strong>高危行业领域企业应当投保安责险；保险机构应按规定提取事故预防服务费用；监管部门加强投保情况监督检查。</p>',
  source_name = '国家安全生产监督管理总局等',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'azb' AND section_code = 'policy' AND title = '安全生产责任保险实施办法';

UPDATE ygb_portal_content SET
  summary = '推进事故预防服务全过程数字化监管，统一项目、过程与成效数据归集标准。',
  content = '<p>本通知要求各级应急管理部门依托安责保平台，统一归集事故预防服务项目立项、实施过程、资金使用、成效评估等数据，实现预防服务可查询、可监督、可评价。</p>',
  source_name = '广东省应急管理厅',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'azb' AND section_code = 'policy' AND title LIKE '关于推进事故预防服务数字化监管%';

UPDATE ygb_portal_content SET
  summary = '规范高危作业设备身份标识、授权流程、运行日志留存及 AI 事件上报接口标准。',
  content = '<p>本规范适用于纳入“千机万码”监管的高危作业设备，明确设备赋码规则、人员授权流程、心跳监测频率、日志留存期限及 AI 事件上报接口格式，是设备接入安责保平台的技术依据。</p>',
  source_name = '广东省应急管理厅',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'azb' AND section_code = 'policy' AND title = '高危作业设备授权与监测接入规范';

UPDATE ygb_portal_content SET
  summary = '实现在线投保、预防服务委托、资金监管、赔付分析与费率浮动联动的全链条闭环管理。',
  content = '<p>安责保平台为安责险业务提供从投保到理赔的全流程数字化支撑，监管部门可实时掌握投保覆盖率，保险机构可规范开展事故预防服务，企业可在线查询保单与预防项目进展。</p>',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'azb' AND section_code = 'solution' AND title = '安责险全流程监管';

UPDATE ygb_portal_content SET
  summary = '将物联网芯片、电子围栏、远程锁机与授权日志统一纳入平台监管，实现设备全生命周期管理。',
  content = '<p>“千机万码”工程为每台高危设备赋予唯一身份标识，结合人员授权、远程锁机、运行心跳与 AI 事件采集，构建设备“可识别、可授权、可监控、可追溯”的智控体系。</p>',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'azb' AND section_code = 'solution' AND title = '千机万码设备智控';

UPDATE ygb_portal_content SET
  summary = '对明火、离岗、未佩戴防护用品等行为进行 AI 实时识别，并同步推送至预警中心闭环处置。',
  content = '<p>平台接入前端 AI 分析能力，对重点场景进行 7×24 小时智能监测，识别结果实时写入预警中心，支持分级推送、限时处置与复核销号，有效提升隐患发现效率。</p>',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'azb' AND section_code = 'solution' AND title = 'AI风险监测预警';

UPDATE ygb_portal_content SET
  summary = '综合投保、设备、隐患整改与事故预防数据，形成企业风险画像并支撑差异化监管。',
  content = '<p>平台基于多源数据构建企业安全生产风险画像，实行红黄绿码分级管理，并与信用评价、执法检查频次等监管措施联动，推动“无事不扰、有事必究”。</p>',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'azb' AND section_code = 'solution' AND title = '企业风险画像与信用评价';

UPDATE ygb_portal_content SET
  summary = '提供安责保官网、监管后台及各角色入口的完整操作说明。',
  content = '<p>手册包含平台登录、功能导航、安责险业务查询、设备管理、预警处置、报表导出等内容，请从下载中心获取 PDF 版本。</p>',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'azb' AND section_code = 'download' AND title = '平台操作手册';

UPDATE ygb_portal_content SET
  summary = '说明设备授权、心跳上报、AI 事件与日志留存的技术规范与接入步骤。',
  content = '<p>指南面向设备厂商与技术服务单位，详细说明设备接入安责保平台的网络要求、接口协议、认证方式、测试验收流程及常见问题排查方法。</p>',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'azb' AND section_code = 'download' AND title = '设备接入指南';

UPDATE ygb_portal_content SET
  summary = '收录安责险服务、事故预防、设备运维等相关制度与文档模板，便于企业规范使用。',
  content = '<p>模板包包含：安责险投保承诺书、事故预防服务方案、设备日常点检记录、隐患整改通知书等常用文档，支持 Word/PDF 格式下载使用。</p>',
  update_by = 'portal_enrich', update_time = NOW()
WHERE portal_code = 'azb' AND section_code = 'download' AND title = '制度模板包';
