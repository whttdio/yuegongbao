set names utf8mb4;

-- =============================================================================
-- Phase 43: 530 menu gap repair (P0 + P1)
-- =============================================================================

-- 1) Fix operation / worker-activity menu_id collision (4300)
delete from sys_role_menu
where menu_id in (
  select menu_id from (
    select menu_id from sys_menu
    where menu_id in (4300, 4301, 4302)
      and (component = 'ygb/workerActivity/index' or perms like 'ygb:workerActivity:%')
  ) t
);

delete from sys_menu
where menu_id in (4301, 4302)
  and perms like 'ygb:workerActivity:%';

update sys_menu
set menu_name = '运营后台',
    parent_id = 0,
    order_num = 30,
    path = 'operation',
    component = null,
    query = '',
    route_name = '',
    menu_type = 'M',
    visible = '0',
    status = '0',
    perms = '',
    icon = 'operation',
    portal_scope = 'ygb',
    remark = 'P3 运营后台菜单'
where menu_id = 4300
  and (component = 'ygb/workerActivity/index' or path = 'workerActivity');

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  4300, '运营后台', 0, 30, 'operation', null, '', '',
  1, 0, 'M', '0', '0', '', 'operation',
  'admin', sysdate(), 'admin', sysdate(), 'P3 运营后台菜单', 'ygb'
from dual
where not exists (select 1 from sys_menu where menu_id = 4300);

-- 2) Worker service menus under operation (5801-5805)
insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
) values
  (5801, '投诉举报', 4300, 7, 'workerComplaint', 'ygb/workerComplaint/index', '', 'YgbWorkerComplaint', 1, 0, 'C', '0', '0', 'ygb:workerMessage:list', 'message', 'admin', sysdate(), 'admin', sysdate(), '劳动者投诉举报管理', 'ygb'),
  (5802, '法律咨询', 4300, 8, 'workerLegalConsult', 'ygb/workerLegalConsult/index', '', 'YgbWorkerLegalConsult', 1, 0, 'C', '0', '0', 'ygb:workerMessage:list', 'guide', 'admin', sysdate(), 'admin', sysdate(), '劳动者法律咨询管理', 'ygb'),
  (5803, '意见反馈', 4300, 9, 'workerFeedback', 'ygb/workerFeedback/index', '', 'YgbWorkerFeedback', 1, 0, 'C', '0', '0', 'ygb:workerMessage:list', 'edit', 'admin', sysdate(), 'admin', sysdate(), '劳动者意见反馈管理', 'ygb'),
  (5804, '上传归档', 4300, 10, 'workerUploadRecord', 'ygb/workerUploadRecord/index', '', 'YgbWorkerUploadRecord', 1, 0, 'C', '0', '0', 'ygb:workerMessage:list', 'upload', 'admin', sysdate(), 'admin', sysdate(), '劳动者上传归档管理', 'ygb'),
  (5805, '劳动者活动', 4300, 11, 'workerActivity', 'ygb/workerActivity/index', '', 'YgbWorkerActivity', 1, 0, 'C', '0', '0', 'ygb:workerActivity:list', 'present', 'admin', sysdate(), 'admin', sysdate(), '劳动者福利活动管理', 'ygb')
on duplicate key update
  menu_name = values(menu_name),
  parent_id = values(parent_id),
  order_num = values(order_num),
  path = values(path),
  component = values(component),
  route_name = values(route_name),
  perms = values(perms),
  icon = values(icon),
  portal_scope = values(portal_scope),
  remark = values(remark);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark, portal_scope) values
  (5811, '消息查询', 5801, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:workerMessage:list', '#', 'admin', sysdate(), '', 'ygb'),
  (5814, '流程处理', 5801, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:workerMessage:flow', '#', 'admin', sysdate(), '劳动者投诉处理权限', 'ygb'),
  (5815, '流程处理', 5802, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:workerMessage:flow', '#', 'admin', sysdate(), '劳动者法律咨询处理权限', 'ygb'),
  (5816, '流程处理', 5803, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:workerMessage:flow', '#', 'admin', sysdate(), '劳动者反馈处理权限', 'ygb'),
  (5812, '活动查询', 5805, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:workerActivity:query', '#', 'admin', sysdate(), '', 'ygb'),
  (5813, '活动处理', 5805, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:workerActivity:handle', '#', 'admin', sysdate(), '', 'ygb')
on duplicate key update menu_name = values(menu_name), parent_id = values(parent_id), perms = values(perms), portal_scope = values(portal_scope);

-- 3) AZB symmetric menus (5820-5823)
insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
) values
  (5820, '驾驶舱配置', 5630, 2, 'cockpitConfig', 'azb/cockpitConfig/index', '', 'AzbCockpitConfig', 1, 0, 'C', '0', '0', 'ygb:cockpitConfig:list', 'system', 'admin', sysdate(), 'admin', sysdate(), 'AZB 驾驶舱配置', 'azb'),
  (5821, '建议任务', 5640, 3, 'aiReportTask', 'azb/aiReportTask/index', '', 'AzbAiReportTask', 1, 0, 'C', '0', '0', 'ygb:aiReportTask:list', 'list', 'admin', sysdate(), 'admin', sysdate(), 'AZB AI建议任务', 'azb'),
  (5822, '订阅管理', 5640, 4, 'aiReportSubscription', 'azb/aiReportSubscription/index', '', 'AzbAiReportSubscription', 1, 0, 'C', '0', '0', 'ygb:aiReportSubscription:list', 'email', 'admin', sysdate(), 'admin', sysdate(), 'AZB AI订阅管理', 'azb'),
  (5823, '赔付率监控', 5650, 4, 'claim', 'azb/aqInsuranceClaim/index', '', 'AzbAqInsuranceClaim', 1, 0, 'C', '0', '0', 'ygb:aqInsuranceClaim:list', 'data-analysis', 'admin', sysdate(), 'admin', sysdate(), 'AZB 赔付率监控', 'azb')
on duplicate key update
  menu_name = values(menu_name),
  parent_id = values(parent_id),
  order_num = values(order_num),
  path = values(path),
  component = values(component),
  route_name = values(route_name),
  perms = values(perms),
  icon = values(icon),
  portal_scope = values(portal_scope),
  remark = values(remark);

-- 4) YGB stat report menus (5830-5836) under 3920
insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
) values
  (5830, '安责险月报', 3920, 9, 'statReport/aqInsurance', 'ygb/statReport/aqInsurance/index', '', 'YgbStatReportAqInsurance', 1, 0, 'C', '0', '0', 'ygb:statReport:aqInsurance:query', 'table', 'admin', sysdate(), 'admin', sysdate(), 'YGB 安责险月报', 'ygb'),
  (5831, '新业态月报', 3920, 10, 'statReport/newform', 'ygb/statReport/newform/index', '', 'YgbStatReportNewform', 1, 0, 'C', '0', '0', 'ygb:statReport:newform:query', 'table', 'admin', sysdate(), 'admin', sysdate(), 'YGB 新业态月报', 'ygb'),
  (5832, '职业病月报', 3920, 11, 'statReport/occupation', 'ygb/statReport/occupation/index', '', 'YgbStatReportOccupation', 1, 0, 'C', '0', '0', 'ygb:statReport:occupation:query', 'table', 'admin', sysdate(), 'admin', sysdate(), 'YGB 职业病月报', 'ygb'),
  (5833, '工会监督月报', 3920, 12, 'statReport/union', 'ygb/statReport/union/index', '', 'YgbStatReportUnion', 1, 0, 'C', '0', '0', 'ygb:statReport:union:query', 'table', 'admin', sysdate(), 'admin', sysdate(), 'YGB 工会监督月报', 'ygb'),
  (5834, '自定义报表', 3920, 13, 'statReport/custom', 'ygb/statReport/custom/index', '', 'YgbStatReportCustom', 1, 0, 'C', '0', '0', 'ygb:statReport:custom:query', 'table', 'admin', sysdate(), 'admin', sysdate(), 'YGB 自定义报表', 'ygb'),
  (5835, '设备月报', 3920, 14, 'statReport/device', 'ygb/statReport/device/index', '', 'YgbStatReportDevice', 1, 0, 'C', '0', '0', 'ygb:statReport:device:query', 'table', 'admin', sysdate(), 'admin', sysdate(), 'YGB 设备月报', 'ygb'),
  (5836, '扩面减损月报', 3920, 15, 'statReport/expansion', 'ygb/statReport/expansion/index', '', 'YgbStatReportExpansion', 1, 0, 'C', '0', '0', 'ygb:statReport:expansion:query', 'table', 'admin', sysdate(), 'admin', sysdate(), 'YGB 扩面减损月报', 'ygb')
on duplicate key update
  menu_name = values(menu_name),
  parent_id = values(parent_id),
  order_num = values(order_num),
  path = values(path),
  component = values(component),
  perms = values(perms),
  portal_scope = values(portal_scope),
  remark = values(remark);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark, portal_scope) values
  (5840, '查询', 5830, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:aqInsurance:query', '#', 'admin', sysdate(), '', 'ygb'),
  (5841, '生成', 5830, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:aqInsurance:generate', '#', 'admin', sysdate(), '', 'ygb'),
  (5842, '导出', 5830, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:aqInsurance:export', '#', 'admin', sysdate(), '', 'ygb'),
  (5843, '查询', 5831, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:newform:query', '#', 'admin', sysdate(), '', 'ygb'),
  (5844, '生成', 5831, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:newform:generate', '#', 'admin', sysdate(), '', 'ygb'),
  (5845, '导出', 5831, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:newform:export', '#', 'admin', sysdate(), '', 'ygb'),
  (5846, '查询', 5832, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:occupation:query', '#', 'admin', sysdate(), '', 'ygb'),
  (5847, '生成', 5832, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:occupation:generate', '#', 'admin', sysdate(), '', 'ygb'),
  (5848, '导出', 5832, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:occupation:export', '#', 'admin', sysdate(), '', 'ygb'),
  (5849, '查询', 5833, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:union:query', '#', 'admin', sysdate(), '', 'ygb'),
  (5850, '生成', 5833, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:union:generate', '#', 'admin', sysdate(), '', 'ygb'),
  (5851, '导出', 5833, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:union:export', '#', 'admin', sysdate(), '', 'ygb'),
  (5852, '查询', 5834, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:custom:query', '#', 'admin', sysdate(), '', 'ygb'),
  (5853, '生成', 5834, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:custom:generate', '#', 'admin', sysdate(), '', 'ygb'),
  (5854, '导出', 5834, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:custom:export', '#', 'admin', sysdate(), '', 'ygb'),
  (5855, '查询', 5835, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:device:query', '#', 'admin', sysdate(), '', 'ygb'),
  (5856, '生成', 5835, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:device:generate', '#', 'admin', sysdate(), '', 'ygb'),
  (5857, '导出', 5835, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:device:export', '#', 'admin', sysdate(), '', 'ygb'),
  (5858, '查询', 5836, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:expansion:query', '#', 'admin', sysdate(), '', 'ygb'),
  (5859, '生成', 5836, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:expansion:generate', '#', 'admin', sysdate(), '', 'ygb'),
  (5860, '导出', 5836, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'ygb:statReport:expansion:export', '#', 'admin', sysdate(), '', 'ygb')
on duplicate key update menu_name = values(menu_name), parent_id = values(parent_id), perms = values(perms), portal_scope = values(portal_scope);

-- 5) AZB stat report menus (5861-5862) under 4920
insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
) values
  (5861, '安责险治理月报', 4920, 5, 'statReport/aqInsurance', 'azb/statReport/aqInsurance/index', '', 'AzbStatReportAqInsurance', 1, 0, 'C', '0', '0', 'azb:statReport:aqInsurance:query', 'table', 'admin', sysdate(), 'admin', sysdate(), 'AZB 安责险治理月报', 'azb'),
  (5862, '设备治理月报', 4920, 6, 'statReport/device', 'azb/statReport/device/index', '', 'AzbStatReportDevice', 1, 0, 'C', '0', '0', 'azb:statReport:device:query', 'table', 'admin', sysdate(), 'admin', sysdate(), 'AZB 设备治理月报', 'azb')
on duplicate key update
  menu_name = values(menu_name),
  parent_id = values(parent_id),
  order_num = values(order_num),
  path = values(path),
  component = values(component),
  perms = values(perms),
  portal_scope = values(portal_scope),
  remark = values(remark);

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark, portal_scope) values
  (5863, '查询', 5861, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'azb:statReport:aqInsurance:query', '#', 'admin', sysdate(), '', 'azb'),
  (5864, '生成', 5861, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'azb:statReport:aqInsurance:generate', '#', 'admin', sysdate(), '', 'azb'),
  (5865, '导出', 5861, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'azb:statReport:aqInsurance:export', '#', 'admin', sysdate(), '', 'azb'),
  (5866, '查询', 5862, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'azb:statReport:device:query', '#', 'admin', sysdate(), '', 'azb'),
  (5867, '生成', 5862, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'azb:statReport:device:generate', '#', 'admin', sysdate(), '', 'azb'),
  (5868, '导出', 5862, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'azb:statReport:device:export', '#', 'admin', sysdate(), '', 'azb')
on duplicate key update menu_name = values(menu_name), parent_id = values(parent_id), perms = values(perms), portal_scope = values(portal_scope);

-- 6) Ensure YGB credit directory exists
insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
)
select
  5885, '信用评价', 0, 14, 'ygb-credit', null, '', '',
  1, 0, 'M', '0', '0', '', 'chart',
  'admin', sysdate(), 'admin', sysdate(), '粤工保信用评价目录', 'ygb'
from dual
where not exists (select 1 from sys_menu where path = 'ygb-credit' and menu_type = 'M' and portal_scope = 'ygb');

update sys_menu
set menu_name = '信用评价',
    parent_id = 0,
    order_num = 14,
    path = 'ygb-credit',
    component = null,
    menu_type = 'M',
    portal_scope = 'ygb'
where menu_id = 5885;

-- 7) P1 ledger menus under regulation (2040) and credit (5885 or existing)
insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
) values
  (5870, '参保率统计', 2040, 7, 'socialEnrollment', 'ygb/socialEnrollment/index', '', 'YgbSocialEnrollment', 1, 0, 'C', '0', '0', 'ygb:socialEnrollment:list', 'data-analysis', 'admin', sysdate(), 'admin', sysdate(), '参保率统计台账', 'ygb'),
  (5871, '社保补缴跟踪', 2040, 8, 'socialSupplement', 'ygb/socialSupplement/index', '', 'YgbSocialSupplement', 1, 0, 'C', '0', '0', 'ygb:socialSupplement:list', 'money', 'admin', sysdate(), 'admin', sysdate(), '社保补缴跟踪台账', 'ygb'),
  (5872, '发票比对', 2040, 9, 'taxInvoice', 'ygb/taxInvoice/index', '', 'YgbTaxInvoice', 1, 0, 'C', '0', '0', 'ygb:taxInvoice:list', 'example', 'admin', sysdate(), 'admin', sysdate(), '发票比对台账', 'ygb'),
  (5873, '资金流穿透', 2040, 10, 'taxFundFlow', 'ygb/taxFundFlow/index', '', 'YgbTaxFundFlow', 1, 0, 'C', '0', '0', 'ygb:taxFundFlow:list', 'connection', 'admin', sysdate(), 'admin', sysdate(), '资金流穿透台账', 'ygb'),
  (5874, '参保补贴管理', 2040, 11, 'expansionSubsidy', 'ygb/expansionSubsidy/index', '', 'YgbExpansionSubsidy', 1, 0, 'C', '0', '0', 'ygb:expansionSubsidy:list', 'wallet', 'admin', sysdate(), 'admin', sysdate(), '参保补贴管理台账', 'ygb'),
  (5875, '三性岗位审核', 2040, 12, 'threeNaturePost', 'ygb/threeNaturePost/index', '', 'YgbThreeNaturePost', 1, 0, 'C', '0', '0', 'ygb:threeNaturePost:list', 'form', 'admin', sysdate(), 'admin', sysdate(), '三性岗位审核台账', 'ygb'),
  (5876, '专项整治台账', 2040, 13, 'specialRectification', 'ygb/specialRectification/index', '', 'YgbSpecialRectification', 1, 0, 'C', '0', '0', 'ygb:specialRectification:list', 'documentation', 'admin', sysdate(), 'admin', sysdate(), '专项整治台账', 'ygb')
on duplicate key update
  menu_name = values(menu_name),
  parent_id = values(parent_id),
  order_num = values(order_num),
  path = values(path),
  component = values(component),
  perms = values(perms),
  portal_scope = values(portal_scope),
  remark = values(remark);

insert into sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark, portal_scope
) values
  (5880, '信用评分规则', 5885, 2, 'creditRule', 'ygb/creditRule/index', '', 'YgbCreditRule', 1, 0, 'C', '0', '0', 'ygb:creditRule:list', 'edit', 'admin', sysdate(), 'admin', sysdate(), '信用评分规则台账', 'ygb'),
  (5881, '信用修复申请', 5885, 3, 'creditRepair', 'ygb/creditRepair/index', '', 'YgbCreditRepair', 1, 0, 'C', '0', '0', 'ygb:creditRepair:list', 'refresh', 'admin', sysdate(), 'admin', sysdate(), '信用修复申请台账', 'ygb'),
  (5882, '联合惩戒推送', 5885, 4, 'creditSanction', 'ygb/creditSanction/index', '', 'YgbCreditSanction', 1, 0, 'C', '0', '0', 'ygb:creditSanction:list', 'warning', 'admin', sysdate(), 'admin', sysdate(), '联合惩戒推送台账', 'ygb')
on duplicate key update
  menu_name = values(menu_name),
  parent_id = values(parent_id),
  order_num = values(order_num),
  path = values(path),
  component = values(component),
  perms = values(perms),
  portal_scope = values(portal_scope),
  remark = values(remark);

-- ledger + azb button permissions
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark, portal_scope)
select 5890 + n.id, n.btn_name, n.parent_id, n.order_num, '', '', '', '', 1, 0, 'F', '0', '0', n.perms, '#', 'admin', sysdate(), '', 'ygb'
from (
  select 0 as id, 5870 as parent_id, 1 as order_num, '查询' as btn_name, 'ygb:socialEnrollment:query' as perms union all
  select 1, 5870, 2, '新增', 'ygb:socialEnrollment:add' union all
  select 2, 5870, 3, '修改', 'ygb:socialEnrollment:edit' union all
  select 3, 5870, 4, '删除', 'ygb:socialEnrollment:remove' union all
  select 4, 5870, 5, '导出', 'ygb:socialEnrollment:export' union all
  select 5, 5871, 1, '查询', 'ygb:socialSupplement:query' union all
  select 6, 5871, 2, '新增', 'ygb:socialSupplement:add' union all
  select 7, 5871, 3, '修改', 'ygb:socialSupplement:edit' union all
  select 8, 5871, 4, '删除', 'ygb:socialSupplement:remove' union all
  select 9, 5871, 5, '导出', 'ygb:socialSupplement:export' union all
  select 10, 5872, 1, '查询', 'ygb:taxInvoice:query' union all
  select 11, 5872, 2, '新增', 'ygb:taxInvoice:add' union all
  select 12, 5872, 3, '修改', 'ygb:taxInvoice:edit' union all
  select 13, 5872, 4, '删除', 'ygb:taxInvoice:remove' union all
  select 14, 5872, 5, '导出', 'ygb:taxInvoice:export' union all
  select 15, 5873, 1, '查询', 'ygb:taxFundFlow:query' union all
  select 16, 5873, 2, '新增', 'ygb:taxFundFlow:add' union all
  select 17, 5873, 3, '修改', 'ygb:taxFundFlow:edit' union all
  select 18, 5873, 4, '删除', 'ygb:taxFundFlow:remove' union all
  select 19, 5873, 5, '导出', 'ygb:taxFundFlow:export' union all
  select 20, 5874, 1, '查询', 'ygb:expansionSubsidy:query' union all
  select 21, 5874, 2, '新增', 'ygb:expansionSubsidy:add' union all
  select 22, 5874, 3, '修改', 'ygb:expansionSubsidy:edit' union all
  select 23, 5874, 4, '删除', 'ygb:expansionSubsidy:remove' union all
  select 24, 5874, 5, '导出', 'ygb:expansionSubsidy:export' union all
  select 25, 5875, 1, '查询', 'ygb:threeNaturePost:query' union all
  select 26, 5875, 2, '新增', 'ygb:threeNaturePost:add' union all
  select 27, 5875, 3, '修改', 'ygb:threeNaturePost:edit' union all
  select 28, 5875, 4, '删除', 'ygb:threeNaturePost:remove' union all
  select 29, 5875, 5, '导出', 'ygb:threeNaturePost:export' union all
  select 30, 5876, 1, '查询', 'ygb:specialRectification:query' union all
  select 31, 5876, 2, '新增', 'ygb:specialRectification:add' union all
  select 32, 5876, 3, '修改', 'ygb:specialRectification:edit' union all
  select 33, 5876, 4, '删除', 'ygb:specialRectification:remove' union all
  select 34, 5876, 5, '导出', 'ygb:specialRectification:export' union all
  select 35, 5880, 1, '查询', 'ygb:creditRule:query' union all
  select 36, 5880, 2, '新增', 'ygb:creditRule:add' union all
  select 37, 5880, 3, '修改', 'ygb:creditRule:edit' union all
  select 38, 5880, 4, '删除', 'ygb:creditRule:remove' union all
  select 39, 5880, 5, '导出', 'ygb:creditRule:export' union all
  select 40, 5881, 1, '查询', 'ygb:creditRepair:query' union all
  select 41, 5881, 2, '新增', 'ygb:creditRepair:add' union all
  select 42, 5881, 3, '修改', 'ygb:creditRepair:edit' union all
  select 43, 5881, 4, '删除', 'ygb:creditRepair:remove' union all
  select 44, 5881, 5, '导出', 'ygb:creditRepair:export' union all
  select 45, 5882, 1, '查询', 'ygb:creditSanction:query' union all
  select 46, 5882, 2, '新增', 'ygb:creditSanction:add' union all
  select 47, 5882, 3, '修改', 'ygb:creditSanction:edit' union all
  select 48, 5882, 4, '删除', 'ygb:creditSanction:remove' union all
  select 49, 5882, 5, '导出', 'ygb:creditSanction:export'
) n
where not exists (select 1 from sys_menu where menu_id = 5890 + n.id);

-- Ensure creditScore stays under credit directory when 3981 exists
update sys_menu
set parent_id = (select menu_id from (select menu_id from sys_menu where path = 'ygb-credit' and menu_type = 'M' and portal_scope = 'ygb' limit 1) t),
    portal_scope = 'ygb'
where menu_id = 3981
  and component = 'ygb/creditScore/index';

-- 8) Role bindings
insert ignore into sys_role_menu (role_id, menu_id)
select r.role_id, m.menu_id
from sys_role r
join sys_menu m on m.menu_id in (
  5801, 5802, 5803, 5804, 5805, 5811, 5812, 5813,
  5830, 5831, 5832, 5833, 5834, 5835, 5836,
  5840, 5841, 5842, 5843, 5844, 5845, 5846, 5847, 5848, 5849, 5850, 5851, 5852, 5853, 5854, 5855, 5856, 5857, 5858, 5859, 5860,
  5870, 5871, 5872, 5873, 5874, 5875, 5876,
  5880, 5881, 5882, 5885
)
where r.role_id in (1, 2, 101, 102, 103, 104, 105);

insert ignore into sys_role_menu (role_id, menu_id)
select r.role_id, m.menu_id
from sys_role r
join sys_menu m on m.menu_id between 5890 and 5939
where r.role_id in (1, 2, 101, 102, 103, 104, 105);

insert ignore into sys_role_menu (role_id, menu_id)
select r.role_id, m.menu_id
from sys_role r
join sys_menu m on m.menu_id in (5820, 5821, 5822, 5823, 5861, 5862, 5863, 5864, 5865, 5866, 5867, 5868)
where r.role_id in (1, 2, 107, 108, 109);

-- 9) module_record seed samples
insert into ygb_module_record (
  record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code,
  enterprise_id, enterprise_name, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag
)
select 921010, 'SOCIAL_ENROLLMENT', '广州区域派遣单位参保率汇总', 'enrollment', '2026-06', 'ygb', 'closed', '0', '440100',
  1001, '广州南粤人力资源有限公司', 'SE-202606-001', 1, '社保监管',
  '{"enrollmentRate":92.6,"insuredCount":2860,"targetCount":3088}',
  '参保率统计样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921010);

insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921011, 'SOCIAL_SUPPLEMENT', '深圳某派遣单位断缴补缴跟踪', 'supplement', '2026-06', 'ygb', 'processing', '0', '440300',
  1002, '深圳鹏城机电工程有限公司', 'SS-202606-001', 1, '社保监管',
  '{"arrearsMonths":2,"supplementAmount":18600.00}',
  '社保补缴跟踪样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921011);

insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921012, 'TAX_INVOICE', '佛山用工单位发票偏离核查', 'invoice', '2026-06', 'ygb', 'pending', '0', '440600',
  1003, '佛山顺德智造服务有限公司', 'TI-202606-001', 1, '税务监管',
  '{"invoiceTotal":5200000,"salaryTotal":4680000,"deviationRate":11.1}',
  '发票比对样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921012);

insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921013, 'TAX_FUND_FLOW', '疑似只过账派遣单位资金流', 'fund_flow', '2026-06', 'ygb', 'pending', '0', '440100',
  1004, '广州安通劳务派遣有限公司', 'TF-202606-001', 1, '税务监管',
  '{"passThroughFlag":true,"flowCount":18}',
  '资金流穿透样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921013);

insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921014, 'EXPANSION_SUBSIDY', '新增参保补贴申请批次', 'subsidy', '2026-06', 'ygb', 'processing', '0', '440100',
  1001, '广州南粤人力资源有限公司', 'ES-202606-001', 1, '扩面减损',
  '{"subsidyAmount":120000,"personCount":120}',
  '参保补贴样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921014);

insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921015, 'THREE_NATURE_POST', '辅助性岗位民主程序审核', 'three_nature', '2026-06', 'ygb', 'pending', '0', '440300',
  1002, '深圳鹏城机电工程有限公司', 'TN-202606-001', 1, '专项治理',
  '{"postType":"auxiliary","expireDate":"2026-12-31"}',
  '三性岗位审核样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921015);

insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921016, 'SPECIAL_RECTIFICATION', '劳务派遣专项整治问题登记', 'rectification', '2026-06', 'ygb', 'processing', '0', '440600',
  1003, '佛山顺德智造服务有限公司', 'SR-202606-001', 1, '专项治理',
  '{"issueType":"ratio","deadline":"2026-07-15"}',
  '专项整治台账样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921016);

insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921017, 'CREDIT_RULE', '未备案合同扣分规则', 'rule', '2026-06', 'ygb', 'closed', '0', '440000',
  null, '', 'CR-202606-001', 1, '信用评价',
  '{"deductScore":5,"enabled":true}',
  '信用规则样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921017);

insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921018, 'CREDIT_REPAIR', '企业信用修复申请', 'repair', '2026-06', 'ygb', 'pending', '0', '440100',
  1001, '广州南粤人力资源有限公司', 'CP-202606-001', 1, '信用评价',
  '{"beforeLevel":"C","targetLevel":"B"}',
  '信用修复样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921018);

insert into ygb_module_record (record_id, record_type, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, related_code, sort_order, source_label, payload_json, remark, create_by, create_time, del_flag)
select 921019, 'CREDIT_SANCTION', 'D级企业联合惩戒推送', 'sanction', '2026-06', 'ygb', 'processing', '0', '440300',
  1004, '广州安通劳务派遣有限公司', 'CS-202606-001', 1, '信用评价',
  '{"pushStatus":"sent","platform":"guangdong_credit"}',
  '联合惩戒样例', 'admin', sysdate(), '0'
from dual where not exists (select 1 from ygb_module_record where record_id = 921019);
