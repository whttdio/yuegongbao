SET NAMES utf8mb4;

-- 劳动者端内容中心补充种子
-- 执行：mysql -u root -p yuegongbao < sql/ygb_phase24_worker_portal_content.sql

DELETE FROM ygb_portal_content
WHERE portal_code = 'ygb'
  AND section_code IN ('worker_activity', 'worker_video', 'worker_ai_training', 'worker_training_quiz', 'worker_training_course', 'union_contract', 'legal_faq')
  AND create_by = 'portal_seed';

INSERT INTO ygb_portal_content (
    portal_code, section_code, category_code, title, summary, content, cover_url, link_url, source_name,
    publish_time, sort_order, status, extra_json, create_by, create_time, del_flag
) VALUES
(
    'ygb', 'worker_activity', 'promotion', '月月抽最高 188 元立减金',
    '完成培训、保持良好用工记录即可参与，每月活动入口统一从劳动者端首页展示。',
    '活动面向劳动者端实名用户开放，完成本月培训任务并保持良好用工记录后即可参与抽奖。',
    NULL, '/activity/monthly-discount.html', 'worker_seed',
    NOW(), 10, '0',
    '{"activityKey":"monthly-discount","ruleList":["完成本月培训任务。","每个账号每月可报名 1 次。","活动结果以平台公告为准。"]}',
    'portal_seed', NOW(), '0'
),
(
    'ygb', 'worker_video', 'video-1', '工伤预防基础知识',
    '围绕入场、作业、防护和报案流程的基础培训。',
    '重点覆盖入场准备、基础防护、现场留痕和工伤报案前置动作。',
    'https://images.unsplash.com/photo-1581092580497-e0d23cbdf1dc?auto=format&fit=crop&w=1200&q=80',
    NULL, 'worker_seed',
    NOW(), 10, '0',
    '{"videoKey":"video-1","durationSeconds":96,"videoUrl":"https://samplelib.com/lib/preview/mp4/sample-20s.mp4","posterUrl":"https://images.unsplash.com/photo-1581092580497-e0d23cbdf1dc?auto=format&fit=crop&w=1200&q=80","keyPoints":["入场前先核对培训、考勤和劳动合同状态。","作业中佩戴好基础防护用品，发现异常先停工。","发生工伤后第一时间留痕并补齐病历、照片和考勤。"],"fallbackTips":["如视频暂时无法播放，可先阅读下方关键学习点。","完成学习后建议保存进度，并结合培训题目巩固要点。","如现场存在实际风险，请优先停工并通过投诉、工会或法律咨询链路留痕。"]}',
    'portal_seed', NOW(), '0'
),
(
    'ygb', 'worker_video', 'video-2', '高处作业安全提示',
    '重点提示安全带、脚手架和现场监护要求。',
    '适合高处作业、脚手架作业和临边作业人员学习复训。',
    'https://images.unsplash.com/photo-1504307651254-35680f356dfd?auto=format&fit=crop&w=1200&q=80',
    NULL, 'worker_seed',
    NOW(), 20, '0',
    '{"videoKey":"video-2","durationSeconds":118,"videoUrl":"https://samplelib.com/lib/preview/mp4/sample-30s.mp4","posterUrl":"https://images.unsplash.com/photo-1504307651254-35680f356dfd?auto=format&fit=crop&w=1200&q=80","keyPoints":["高处作业前检查安全带、安全绳和脚手架状态。","现场必须有监护措施，严禁单人冒险作业。","发现围栏、支撑或坠落防护异常时立即停工上报。"],"fallbackTips":["视频无法播放时，先学习关键提示并完成本月题目。","保存学习进度后再回看视频，可减少重复学习。","如现场确有隐患，先停工并通过平台留痕。"]}',
    'portal_seed', NOW(), '0'
),
(
    'ygb', 'worker_video', 'video-3', '设备作业前自检',
    '开工前先检查电源、围栏、设备外观和作业记录。',
    '适合设备操作、巡检和值守岗位的班前自检学习。',
    'https://images.unsplash.com/photo-1565008447742-97f6f38c985c?auto=format&fit=crop&w=1200&q=80',
    NULL, 'worker_seed',
    NOW(), 30, '0',
    '{"videoKey":"video-3","durationSeconds":84,"videoUrl":"https://samplelib.com/lib/preview/mp4/sample-15s.mp4","posterUrl":"https://images.unsplash.com/photo-1565008447742-97f6f38c985c?auto=format&fit=crop&w=1200&q=80","keyPoints":["开工前先看设备电源、围栏和外观是否异常。","核对点检记录和当日作业授权，再进行开机。","发现设备异响、报警或防护缺失时不得强行作业。"],"fallbackTips":["可先阅读关键学习点，再回放视频核对操作顺序。","建议在暂停时及时保存进度。","发现真实设备异常时必须先停机留痕。"]}',
    'portal_seed', NOW(), '0'
),
(
    'ygb', 'worker_ai_training', 'default', '工伤 AI 培训',
    '结合个人工种和常见风险，给出训练建议。',
    'AI 培训页面聚合提问、案例和行动建议，供劳动者按岗位风险快速学习。',
    NULL, NULL, 'worker_seed',
    NOW(), 10, '0',
    '{"questionList":["进入焊接或高处作业前，最先确认哪些防护条件？","遇到设备异常停机时，第一步应该做什么？","发生轻微工伤后，哪些记录应立即补齐？"],"caseList":["案例一：未完成培训直接上岗，导致工资查询与打卡被锁定。","案例二：设备未自检即作业，产生现场风险。","案例三：工伤后未及时留痕，影响后续认定。"],"actionTips":["先完成本月培训题目。","结合岗位查看工伤预防视频。","遇到争议及时进入法律咨询和投诉举报。"]}',
    'portal_seed', NOW(), '0'
),
(
    'ygb', 'worker_training_quiz', 'Q01', '高温天气室外作业前，首先应该确认什么？',
    '围绕班前安全准备的培训题目。',
    '',
    NULL, NULL, 'worker_seed',
    NOW(), 10, '0',
    '{"questionId":"Q01","options":["是否穿了新工服","防暑用品和补水条件是否到位","当天工资是否到账","手机电量是否充足"],"answerIndex":1}',
    'portal_seed', NOW(), '0'
),
(
    'ygb', 'worker_training_quiz', 'Q02', '发现安全隐患后，正确做法是什么？',
    '围绕隐患上报与停工留痕的培训题目。',
    '',
    NULL, NULL, 'worker_seed',
    NOW(), 20, '0',
    '{"questionId":"Q02","options":["继续作业，稍后再说","拍照发朋友圈","立即上报并做好现场留痕","只口头提醒同事"],"answerIndex":2}',
    'portal_seed', NOW(), '0'
),
(
    'ygb', 'worker_training_quiz', 'Q03', '进入施工现场时，最基本的个人防护要求是？',
    '围绕个人防护用品佩戴要求的培训题目。',
    '',
    NULL, NULL, 'worker_seed',
    NOW(), 30, '0',
    '{"questionId":"Q03","options":["按规定佩戴安全防护用品","只要带工牌即可","穿便装也可以","由同事代替佩戴"],"answerIndex":0}',
    'portal_seed', NOW(), '0'
),
(
    'ygb', 'worker_training_quiz', 'Q04', '工伤预防培训的直接目的是什么？',
    '围绕工伤预防目标的培训题目。',
    '',
    NULL, NULL, 'worker_seed',
    NOW(), 40, '0',
    '{"questionId":"Q04","options":["增加考勤天数","方便拍照打卡","提高工资标准","降低事故风险和违规操作"],"answerIndex":3}',
    'portal_seed', NOW(), '0'
),
(
    'ygb', 'worker_training_quiz', 'Q05', '发现考勤记录缺失时，应优先做什么？',
    '围绕考勤异常留痕与补录的培训题目。',
    '',
    NULL, NULL, 'worker_seed',
    NOW(), 50, '0',
    '{"questionId":"Q05","options":["删除当天记录","直接投诉企业","及时补录并说明原因","等月底统一处理"],"answerIndex":2}',
    'portal_seed', NOW(), '0'
),
(
    'ygb', 'worker_training_quiz', 'Q06', '工资条中出现异常扣款，第一步应如何处理？',
    '围绕工资异常核对流程的培训题目。',
    '',
    NULL, NULL, 'worker_seed',
    NOW(), 60, '0',
    '{"questionId":"Q06","options":["自行修改金额","先核对工资条明细并保留证据","忽略不管","更换银行卡"],"answerIndex":1}',
    'portal_seed', NOW(), '0'
),
(
    'ygb', 'worker_training_quiz', 'Q07', '社保缴费记录异常时，劳动者可以通过什么方式留痕？',
    '围绕社保争议举证的培训题目。',
    '',
    NULL, NULL, 'worker_seed',
    NOW(), 70, '0',
    '{"questionId":"Q07","options":["截图保存并通过平台反馈","删除历史记录","停用账号","更换手机号"],"answerIndex":0}',
    'portal_seed', NOW(), '0'
),
(
    'ygb', 'worker_training_quiz', 'Q08', '工伤事故发生后，最重要的事项之一是什么？',
    '围绕工伤留痕与报案的培训题目。',
    '',
    NULL, NULL, 'worker_seed',
    NOW(), 80, '0',
    '{"questionId":"Q08","options":["先删除现场照片","只通知同事","及时固定证据并按流程上报","等企业统一处理"],"answerIndex":2}',
    'portal_seed', NOW(), '0'
),
(
    'ygb', 'worker_training_quiz', 'Q09', '打卡时启用定位的主要目的是什么？',
    '围绕考勤真实性核验的培训题目。',
    '',
    NULL, NULL, 'worker_seed',
    NOW(), 90, '0',
    '{"questionId":"Q09","options":["增加流量消耗","确认作业地点与考勤真实性","提升手机性能","生成更多通知"],"answerIndex":1}',
    'portal_seed', NOW(), '0'
),
(
    'ygb', 'worker_training_quiz', 'Q10', '法律咨询模块最适合处理哪类问题？',
    '围绕法律咨询适用场景的培训题目。',
    '',
    NULL, NULL, 'worker_seed',
    NOW(), 100, '0',
    '{"questionId":"Q10","options":["外卖点单","天气查询","娱乐活动报名","劳动争议、欠薪、社保等维权问题"],"answerIndex":3}',
    'portal_seed', NOW(), '0'
),
(
    'ygb', 'worker_training_course', 'course-1', '入场安全基础课',
    '围绕入场、劳保用品和现场纪律的基础培训。',
    '<p>入场前核验身份与工种。</p><p>按工种佩戴安全帽、反光衣和劳保用品。</p><p>现场发现异常立即留痕并上报。</p>',
    'https://images.unsplash.com/photo-1581092580497-e0d23cbdf1dc?auto=format&fit=crop&w=1200&q=80',
    NULL, 'worker_seed',
    NOW(), 10, '0',
    '{"courseKey":"course-1","durationSeconds":420,"videoUrl":"https://www.w3schools.com/html/mov_bbb.mp4","posterUrl":"https://images.unsplash.com/photo-1581092580497-e0d23cbdf1dc?auto=format&fit=crop&w=1200&q=80","outlineList":["入场前核验身份与工种。","按工种佩戴安全帽、反光衣和劳保用品。","现场发现异常立即留痕并上报。"]}',
    'portal_seed', NOW(), '0'
),
(
    'ygb', 'worker_training_course', 'course-2', '高处作业风险提示',
    '重点提示安全带、临边防护和现场监护要求。',
    '<p>作业前先检查安全带与挂点。</p><p>临边、洞口区域必须设置围栏。</p><p>高处作业应有现场监护。</p>',
    'https://images.unsplash.com/photo-1504307651254-35680f356dfd?auto=format&fit=crop&w=1200&q=80',
    NULL, 'worker_seed',
    NOW(), 20, '0',
    '{"courseKey":"course-2","durationSeconds":560,"videoUrl":"https://www.w3schools.com/html/movie.mp4","posterUrl":"https://images.unsplash.com/photo-1504307651254-35680f356dfd?auto=format&fit=crop&w=1200&q=80","outlineList":["作业前先检查安全带与挂点。","临边、洞口区域必须设置围栏。","高处作业应有现场监护。"]}',
    'portal_seed', NOW(), '0'
),
(
    'ygb', 'worker_training_course', 'course-3', '工伤报案与留痕',
    '发生工伤后，如何固定证据并启动报案流程。',
    '<p>第一时间就医并保留病历。</p><p>补齐现场照片、考勤和证人信息。</p><p>通过平台发起法律咨询或投诉。</p>',
    'https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?auto=format&fit=crop&w=1200&q=80',
    NULL, 'worker_seed',
    NOW(), 30, '0',
    '{"courseKey":"course-3","durationSeconds":360,"videoUrl":"https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4","posterUrl":"https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?auto=format&fit=crop&w=1200&q=80","outlineList":["第一时间就医并保留病历。","补齐现场照片、考勤和证人信息。","通过平台发起法律咨询或投诉。"]}',
    'portal_seed', NOW(), '0'
),
(
    'ygb', 'union_contract', 'collective-contract-1', '劳务派遣集体合同',
    '在岗人员工资发放、加班规则与休息休假约定。',
    '<p>工资应按月足额发放，遇节假日应提前支付。</p><p>加班安排需提前告知，并按约定标准支付加班工资。</p><p>劳动者可通过工会服务页提交工资争议和证据材料。</p>',
    NULL, NULL, 'worker_seed',
    NOW(), 10, '0',
    '{"enterpriseName":"广东阳光劳务有限公司","period":"2026-01-01 至 2026-12-31","clauses":["工资应按月足额发放，遇节假日应提前支付。","加班安排需提前告知，并按约定标准支付加班工资。","劳动者可通过工会服务页提交工资争议和证据材料。"]}',
    'portal_seed', NOW(), '0'
),
(
    'ygb', 'union_contract', 'collective-contract-2', '灵活用工权益协商备忘',
    '社保缴纳、工伤留痕和争议协商流程说明。',
    '<p>灵活用工人员应按月核对社保缴纳状态。</p><p>发生工伤或事故时，应在平台及时留痕并补充病历材料。</p><p>协商不成时可先经工会协调，再走劳动争议处理渠道。</p>',
    NULL, NULL, 'worker_seed',
    NOW(), 20, '0',
    '{"enterpriseName":"珠海蓝海用工服务中心","period":"2026-03-01 至 2027-02-28","clauses":["灵活用工人员应按月核对社保缴纳状态。","发生工伤或事故时，应在平台及时留痕并补充病历材料。","协商不成时可先经工会协调，再走劳动争议处理渠道。"]}',
    'portal_seed', NOW(), '0'
),
(
    'ygb', 'union_contract', 'collective-contract-3', '新业态劳动者集体协商要点',
    '派单收入、申诉时效与安全培训责任约定。',
    '<p>派单计价、奖励扣减应提前公开说明。</p><p>培训、投诉、申诉流程应保留线上留痕记录。</p><p>出现争议时，劳动者可直接向工会提交平台流水和沟通记录。</p>',
    NULL, NULL, 'worker_seed',
    NOW(), 30, '0',
    '{"enterpriseName":"广州城配服务联盟","period":"2026-04-01 至 2027-03-31","clauses":["派单计价、奖励扣减应提前公开说明。","培训、投诉、申诉流程应保留线上留痕记录。","出现争议时，劳动者可直接向工会提交平台流水和沟通记录。"]}',
    'portal_seed', NOW(), '0'
),
(
    'ygb', 'legal_faq', 'faq-salary-evidence', '欠薪后先保留哪些证据',
    '优先保留工资条、银行流水、考勤记录与沟通截图，形成完整举证链。',
    '<p>先保留工资条、银行流水和考勤记录，证明实际出勤与发薪差异。</p><p>如通过微信、支付宝或现金支付，也要保留转账截图、收款凭证或聊天承诺。</p><p>与企业协商过程中尽量保留聊天记录、录音或书面回复，便于后续投诉和法律咨询。</p>',
    NULL, NULL, 'worker_seed',
    NOW(), 40, '0',
    '{"typeLabel":"工资维权"}',
    'portal_seed', NOW(), '0'
),
(
    'ygb', 'legal_faq', 'faq-social-dispute', '社保断缴后怎么办',
    '先确认断缴月份和险种，再准备劳动关系、工资发放和在岗证明材料。',
    '<p>先确认断缴的具体月份和险种，避免后续补缴范围不清。</p><p>准备劳动合同、工资发放记录、在岗证明或企业通知等材料，用于证明劳动关系持续存在。</p><p>可先进入法律咨询或工会服务页面，通过协同渠道推动补缴和争议处理。</p>',
    NULL, NULL, 'worker_seed',
    NOW(), 50, '0',
    '{"typeLabel":"社保争议"}',
    'portal_seed', NOW(), '0'
),
(
    'ygb', 'legal_faq', 'faq-contract-probation', '试用期需要签劳动合同吗',
    '试用期属于劳动合同期限的一部分，原则上也应签书面劳动合同。',
    '<p>试用期属于劳动合同期限的一部分，不能脱离劳动合同单独存在。</p><p>即使在试用期，也应签订书面劳动合同，并明确岗位、工资、试用期限等核心条款。</p><p>如未签合同，应尽快固定入职时间、岗位安排和工资标准等证据，避免后续举证困难。</p>',
    NULL, NULL, 'worker_seed',
    NOW(), 60, '0',
    '{"typeLabel":"劳动合同"}',
    'portal_seed', NOW(), '0'
),
(
    'ygb', 'legal_faq', 'faq-injury-material', '工伤后平台上要补什么材料',
    '优先补齐病历、现场照片、考勤信息和证人说明，保证工伤证据链完整。',
    '<p>先上传现场照片、就诊病历、费用票据等基础材料，保留事故发生后的第一手记录。</p><p>同步补齐考勤记录、岗位信息和证人说明，证明事故发生时的工作场景与身份关系。</p><p>如已出现争议，可同步发起法律咨询和投诉举报，确保后续认定、协商和理赔链路可追溯。</p>',
    NULL, NULL, 'worker_seed',
    NOW(), 70, '0',
    '{"typeLabel":"工伤赔付"}',
    'portal_seed', NOW(), '0'
);
