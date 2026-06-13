set names utf8mb4;

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4300, '运营后台', 0, 30, 'operation', '', '', '',
  1, 0, 'M', '0', '0', '', 'operation',
  'admin', sysdate(), 'admin', sysdate(), 'P3 运营后台菜单', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4300);

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4301, '运营总览', 4300, 1, 'overview', 'ygb/operation/overview/index', '', 'YgbOperationOverview',
  1, 0, 'C', '0', '0', 'ygb:operation:overview', 'dashboard',
  'admin', sysdate(), 'admin', sysdate(), '运营后台总览', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4301);

insert into sys_menu select 4302, '企业入驻审核', 4300, 2, 'enterpriseReview', 'ygb/operation/enterpriseReview/index', '', 'YgbOperationEnterpriseReview', 1, 0, 'C', '0', '0', 'ygb:operationEnterpriseReview:list', 'check', 'admin', sysdate(), 'admin', sysdate(), '企业入驻审核台账', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4302);

insert into sys_menu select 4303, '岗位审核', 4300, 3, 'jobReview', 'ygb/operation/jobReview/index', '', 'YgbOperationJobReview', 1, 0, 'C', '0', '0', 'ygb:operationJobReview:list', 'tickets', 'admin', sysdate(), 'admin', sysdate(), '岗位审核台账', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4303);

insert into sys_menu select 4304, '简历管理', 4300, 4, 'resume', 'ygb/operation/resume/index', '', 'YgbOperationResume', 1, 0, 'C', '0', '0', 'ygb:operationResume:list', 'user', 'admin', sysdate(), 'admin', sysdate(), '简历管理台账', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4304);

insert into sys_menu select 4305, '广告轮播', 4300, 5, 'banner', 'ygb/portalContent/index', 'portalCode=ygb&sectionCode=banner', 'YgbOperationBanner', 1, 0, 'C', '0', '0', 'ygb:portalContent:list', 'picture', 'admin', sysdate(), 'admin', sysdate(), '广告轮播内容管理', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4305);

insert into sys_menu select 4306, '消息推送', 4300, 6, 'message', 'ygb/operation/message/index', '', 'YgbOperationMessage', 1, 0, 'C', '0', '0', 'ygb:operationMessage:list', 'message', 'admin', sysdate(), 'admin', sysdate(), '运营消息台账', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4306);

insert into sys_menu select 4311, '查询', 4302, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:operationEnterpriseReview:query', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4311);
insert into sys_menu select 4312, '新增', 4302, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:operationEnterpriseReview:add', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4312);
insert into sys_menu select 4313, '修改', 4302, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:operationEnterpriseReview:edit', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4313);
insert into sys_menu select 4314, '删除', 4302, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:operationEnterpriseReview:remove', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4314);
insert into sys_menu select 4315, '导出', 4302, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:operationEnterpriseReview:export', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4315);

insert into sys_menu select 4321, '查询', 4303, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:operationJobReview:query', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4321);
insert into sys_menu select 4322, '导出', 4303, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:operationJobReview:export', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4322);

insert into sys_menu select 4331, '查询', 4304, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:operationResume:query', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4331);
insert into sys_menu select 4332, '导出', 4304, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:operationResume:export', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4332);

insert into sys_menu select 4341, '查询', 4306, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:operationMessage:query', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4341);
insert into sys_menu select 4342, '新增', 4306, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:operationMessage:add', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4342);
insert into sys_menu select 4343, '修改', 4306, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:operationMessage:edit', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4343);
insert into sys_menu select 4344, '删除', 4306, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:operationMessage:remove', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4344);
insert into sys_menu select 4345, '导出', 4306, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:operationMessage:export', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4345);

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4400, '平台治理', 0, 31, 'platform', '', '', '',
  1, 0, 'M', '0', '0', '', 'monitor',
  'admin', sysdate(), 'admin', sysdate(), 'P4 平台治理菜单', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4400);

insert into sys_menu select 4401, '治理总览', 4400, 1, 'overview', 'ygb/platform/overview/index', '', 'YgbPlatformOverview', 1, 0, 'C', '0', '0', 'ygb:platformRuntime:query', 'dashboard', 'admin', sysdate(), 'admin', sysdate(), '平台治理总览', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4401);
insert into sys_menu select 4402, '文档管理', 4400, 2, 'document', 'ygb/platform/document/index', '', 'YgbPlatformDocument', 1, 0, 'C', '0', '0', 'ygb:platformDocument:list', 'documentation', 'admin', sysdate(), 'admin', sysdate(), '文档管理台账', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4402);
insert into sys_menu select 4403, '交换监控', 4400, 3, 'exchange', 'ygb/platform/exchange/index', '', 'YgbPlatformExchange', 1, 0, 'C', '0', '0', 'ygb:platformExchange:list', 'connection', 'admin', sysdate(), 'admin', sysdate(), '接口与数据交换监控', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4403);
insert into sys_menu select 4404, '安全审计', 4400, 4, 'securityAudit', 'ygb/platform/securityAudit/index', '', 'YgbPlatformSecurityAudit', 1, 0, 'C', '0', '0', 'ygb:platformSecurityAudit:list', 'lock', 'admin', sysdate(), 'admin', sysdate(), '安全审计台账', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4404);
insert into sys_menu select 4405, '备份恢复', 4400, 5, 'backup', 'ygb/platform/backup/index', '', 'YgbPlatformBackup', 1, 0, 'C', '0', '0', 'ygb:platformBackup:list', 'refresh', 'admin', sysdate(), 'admin', sysdate(), '备份恢复台账', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4405);

insert into sys_menu select 4411, '查询', 4402, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:platformDocument:query', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4411);
insert into sys_menu select 4412, '新增', 4402, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:platformDocument:add', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4412);
insert into sys_menu select 4413, '修改', 4402, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:platformDocument:edit', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4413);
insert into sys_menu select 4414, '删除', 4402, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:platformDocument:remove', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4414);
insert into sys_menu select 4415, '导出', 4402, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:platformDocument:export', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4415);

insert into sys_menu select 4421, '查询', 4403, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:platformExchange:query', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4421);
insert into sys_menu select 4422, '新增', 4403, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:platformExchange:add', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4422);
insert into sys_menu select 4423, '修改', 4403, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:platformExchange:edit', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4423);
insert into sys_menu select 4424, '删除', 4403, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:platformExchange:remove', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4424);
insert into sys_menu select 4425, '导出', 4403, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:platformExchange:export', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4425);

insert into sys_menu select 4431, '查询', 4404, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:platformSecurityAudit:query', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4431);
insert into sys_menu select 4432, '新增', 4404, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:platformSecurityAudit:add', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4432);
insert into sys_menu select 4433, '修改', 4404, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:platformSecurityAudit:edit', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4433);
insert into sys_menu select 4434, '删除', 4404, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:platformSecurityAudit:remove', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4434);
insert into sys_menu select 4435, '导出', 4404, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:platformSecurityAudit:export', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4435);

insert into sys_menu select 4441, '查询', 4405, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:platformBackup:query', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4441);
insert into sys_menu select 4442, '新增', 4405, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:platformBackup:add', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4442);
insert into sys_menu select 4443, '修改', 4405, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:platformBackup:edit', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4443);
insert into sys_menu select 4444, '删除', 4405, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:platformBackup:remove', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4444);
insert into sys_menu select 4445, '导出', 4405, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:platformBackup:export', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4445);

insert into sys_menu select 4110, '暖新地图', 4100, 10, 'warmMap', 'ygb/portalContent/index', 'portalCode=ygb&sectionCode=warm_map', 'YgbWarmMapContent', 1, 0, 'C', '0', '0', 'ygb:portalContent:list', 'map-location', 'admin', sysdate(), 'admin', sysdate(), '暖新地图内容管理', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4110);
insert into sys_menu select 4111, '培训课程', 4100, 11, 'trainingCourse', 'ygb/portalContent/index', 'portalCode=ygb&sectionCode=training_course', 'YgbTrainingCourseContent', 1, 0, 'C', '0', '0', 'ygb:portalContent:list', 'reading', 'admin', sysdate(), 'admin', sysdate(), '培训课程内容管理', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4111);
insert into sys_menu select 4112, '法规库', 4100, 12, 'lawLibrary', 'ygb/portalContent/index', 'portalCode=ygb&sectionCode=law_library', 'YgbLawLibraryContent', 1, 0, 'C', '0', '0', 'ygb:portalContent:list', 'document', 'admin', sysdate(), 'admin', sysdate(), '法规库内容管理', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4112);
insert into sys_menu select 4113, '互助区', 4100, 13, 'mutualHelp', 'ygb/portalContent/index', 'portalCode=ygb&sectionCode=mutual_help', 'YgbMutualHelpContent', 1, 0, 'C', '0', '0', 'ygb:portalContent:list', 'chat-line-square', 'admin', sysdate(), 'admin', sysdate(), '互助区内容管理', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4113);
insert into sys_menu select 4114, '招聘市场', 4100, 14, 'recruitMarket', 'ygb/portalContent/index', 'portalCode=ygb&sectionCode=recruit_market', 'YgbRecruitMarketContent', 1, 0, 'C', '0', '0', 'ygb:portalContent:list', 'briefcase', 'admin', sysdate(), 'admin', sysdate(), '招聘市场内容管理', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4114);

insert into sys_menu select 4460, '赔付率监控', 3961, 4, 'claim', 'ygb/aqInsuranceClaim/index', '', 'YgbAqInsuranceClaim', 1, 0, 'C', '0', '0', 'ygb:aqInsuranceClaim:list', 'data-analysis', 'admin', sysdate(), 'admin', sysdate(), '安责险赔付率监控', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4460);
insert into sys_menu select 4461, '查询', 4460, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:aqInsuranceClaim:query', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4461);
insert into sys_menu select 4462, '新增', 4460, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:aqInsuranceClaim:add', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4462);
insert into sys_menu select 4463, '修改', 4460, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:aqInsuranceClaim:edit', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4463);
insert into sys_menu select 4464, '删除', 4460, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:aqInsuranceClaim:remove', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4464);
insert into sys_menu select 4465, '导出', 4460, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:aqInsuranceClaim:export', '#', 'admin', sysdate(), 'admin', sysdate(), '', 'ygb'
from dual where not exists (select 1 from sys_menu where menu_id = 4465);

insert into sys_role_menu (role_id, menu_id, portal_scope)
select r.role_id, m.menu_id, 'ygb'
from sys_role r
join sys_menu m on m.menu_id in (
  4300, 4301, 4302, 4303, 4304, 4305, 4306,
  4311, 4312, 4313, 4314, 4315,
  4321, 4322,
  4331, 4332,
  4341, 4342, 4343, 4344, 4345,
  4400, 4401, 4402, 4403, 4404, 4405,
  4411, 4412, 4413, 4414, 4415,
  4421, 4422, 4423, 4424, 4425,
  4431, 4432, 4433, 4434, 4435,
  4441, 4442, 4443, 4444, 4445,
  4110, 4111, 4112, 4113, 4114
)
where r.role_key in ('admin', 'ygb_hrss_supervisor')
on duplicate key update portal_scope = values(portal_scope);

insert into sys_role_menu (role_id, menu_id, portal_scope)
select r.role_id, m.menu_id, 'ygb'
from sys_role r
join sys_menu m on m.menu_id in (4460, 4461, 4462, 4463, 4464, 4465)
where r.role_key in ('admin', 'ygb_emergency_supervisor', 'ygb_hrss_supervisor')
on duplicate key update portal_scope = values(portal_scope);
