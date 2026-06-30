import json
from datetime import datetime
from pathlib import Path

UI_DIR = Path(r'D:\JAVA\YueGongBao-Vue\YueGongBao-Vue\yuegongbao-ui\src\views')

# 23 target modules
MODULES = [
    {"id": 7001, "name": "驾驶舱", "path": "cockpit", "icon": "dashboard", "order": 1},
    {"id": 7002, "name": "预警中心", "path": "warning-center", "icon": "bell", "order": 2},
    {"id": 7003, "name": "AI监测报告", "path": "ai-report", "icon": "data-analysis", "order": 3},
    {"id": 7004, "name": "合同备案", "path": "contract-filing", "icon": "document", "order": 4},
    {"id": 7005, "name": "用工考勤", "path": "attendance", "icon": "calendar", "order": 5},
    {"id": 7006, "name": "工资监管", "path": "salary-supervision", "icon": "money", "order": 6},
    {"id": 7007, "name": "社保监管", "path": "social-insurance", "icon": "first-aid-kit", "order": 7},
    {"id": 7008, "name": "工伤监管", "path": "injury-supervision", "icon": "warning", "order": 8},
    {"id": 7009, "name": "专项治理", "path": "special-rectification", "icon": "files", "order": 9},
    {"id": 7010, "name": "税务监管", "path": "tax-supervision", "icon": "coin", "order": 10},
    {"id": 7011, "name": "安责险管理", "path": "aq-insurance", "icon": "umbrella", "order": 11},
    {"id": 7012, "name": "设备管理", "path": "device-management", "icon": "cpu", "order": 12},
    {"id": 7013, "name": "扩面减损", "path": "expansion-reduction", "icon": "trend-charts", "order": 13},
    {"id": 7014, "name": "信用评价", "path": "credit-evaluation", "icon": "medal", "order": 14},
    {"id": 7015, "name": "统计报表", "path": "statistical-report", "icon": "histogram", "order": 15},
    {"id": 7016, "name": "人员管理", "path": "personnel-management", "icon": "user", "order": 16},
    {"id": 7017, "name": "单位管理", "path": "enterprise-management", "icon": "office-building", "order": 17},
    {"id": 7018, "name": "系统管理", "path": "system-management", "icon": "setting", "order": 18},
    {"id": 7019, "name": "运营后台", "path": "operation-backend", "icon": "shopping-bag", "order": 19},
    {"id": 7020, "name": "企业后台", "path": "enterprise-portal", "icon": "briefcase", "order": 20},
    {"id": 7021, "name": "便民服务", "path": "citizen-service", "icon": "service", "order": 21},
    {"id": 7022, "name": "新业态监管", "path": "newform-regulation", "icon": "cloudy", "order": 22},
    {"id": 7023, "name": "职业病监管", "path": "occupational-disease", "icon": "first-aid-kit", "order": 23},
]

# Mapping of existing menu_id to new parent module_id
MENU_TO_MODULE_MAP = {
    # 驾驶舱 7001
    3901: 7001,  # 驾驶舱总览
    4008: 7001,  # 驾驶舱配置
    6071: 7001,  # 趋势分析

    # 预警中心 7002
    2017: 7002,  # 预警中心
    2018: 7002,  # 预警规则
    6061: 7002,  # 预警统计分析

    # AI监测报告 7003
    5741: 7003,  # AI报告中心
    5742: 7003,  # 评分模型配置
    4014: 7003,  # 建议任务
    5827: 7003,  # 订阅管理

    # 合同备案 7004
    2003: 7004,  # 合同备案

    # 用工考勤 7005
    2004: 7005,  # 考勤上报
    2005: 7005,  # 考勤归集

    # 工资监管 7006
    2006: 7006,  # 工资批次
    2007: 7006,  # 工资明细

    # 社保监管 7007
    2008: 7007,  # 社保缴费监控
    2009: 7007,  # 社保基数比对
    5870: 7007,  # 参保率统计
    5871: 7007,  # 社保补缴跟踪

    # 工伤监管 7008
    2015: 7008,  # 工伤事件
    2016: 7008,  # 工伤预防项目
    5951: 7008,  # 工伤人员监测
    5952: 7008,  # 单位工伤预防监测
    5953: 7008,  # 地区工伤预防监测
    5954: 7008,  # 职业病危害监测
    5955: 7008,  # 新业态伤害监测
    5956: 7008,  # 工伤事故预警
    6051: 7008,  # 工伤预防宣传
    6052: 7008,  # 工伤预防培训
    6053: 7008,  # AI工伤预防
    6054: 7008,  # 工伤预防费用

    # 专项治理 7009
    2012: 7009,  # 用工比例监控
    5875: 7009,  # 三性岗位审核
    2013: 7009,  # 假外包识别
    5876: 7009,  # 专项整治台账

    # 税务监管 7010
    2010: 7010,  # 个税比对
    5872: 7010,  # 发票比对
    5873: 7010,  # 资金流穿透

    # 安责险管理 7011
    5761: 7011,  # 投保监管
    4460: 7011,  # 赔付率监控
    5762: 7011,  # 事故预防资金池

    # 设备管理 7012
    2014: 7012,  # 设备管理
    4526: 7012,  # 考勤设备
    4527: 7012,  # 芯片设备
    4528: 7012,  # AI设备
    4529: 7012,  # 物联卡
    4530: 7012,  # 芯片库存
    4531: 7012,  # 电子围栏
    4532: 7012,  # 拆卸报警
    5975: 7012,  # 芯片库存（运营后台重复）
    5976: 7012,  # 物联卡运维（运营后台重复）

    # 扩面减损 7013
    2011: 7013,  # 漏保清单
    5874: 7013,  # 参保补贴管理

    # 信用评价 7014
    3981: 7014,  # 企业信用评分
    5880: 7014,  # 信用评分规则
    5881: 7014,  # 信用修复申请
    5882: 7014,  # 联合惩戒推送

    # 统计报表 7015
    3921: 7015,  # 工伤发生率月报
    3922: 7015,  # 预警治理月报
    3923: 7015,  # 工资发放月报
    3924: 7015,  # 社保税务联动月报
    5830: 7015,  # 安责险月报
    5831: 7015,  # 新业态月报
    5832: 7015,  # 职业病月报
    5833: 7015,  # 工会监督月报
    5834: 7015,  # 自定义报表
    5835: 7015,  # 设备月报
    5836: 7015,  # 扩面减损月报

    # 人员管理 7016
    2002: 7016,  # 人员管理
    4490: 7016,  # 特证管理
    4491: 7016,  # 黑名单
    4492: 7016,  # 培训监督
    4493: 7016,  # 高危岗位库
    4494: 7016,  # 风险岗位库
    4495: 7016,  # 专家库

    # 单位管理 7017
    2001: 7017,  # 单位管理
    4533: 7017,  # 监管单位
    4534: 7017,  # 劳务派遣公司
    4535: 7017,  # 用工单位
    4536: 7017,  # 高危企业库
    6151: 7017,  # 工会组织信息
    6152: 7017,  # 工会监督台账
    6154: 7017,  # 法律援助服务
    6155: 7017,  # 集体协商管理
    6156: 7017,  # 工伤预防监督
    6157: 7017,  # 职工权益宣传

    # 运营后台 7019
    4301: 7019,  # 运营总览
    4302: 7019,  # 企业入驻审核
    4303: 7019,  # 岗位审核
    4304: 7019,  # 简历管理
    4305: 7019,  # 广告轮播
    4306: 7019,  # 消息推送
    4100: 7019,  # 门户内容管理
    4200: 7019,  # 招聘岗位管理
    5801: 7019,  # 投诉举报
    5802: 7019,  # 法律咨询
    5803: 7019,  # 意见反馈
    5804: 7019,  # 上传归档
    5805: 7019,  # 劳动者活动
    6072: 7019,  # 职位分类管理
    6073: 7019,  # 招聘数据统计
    5971: 7019,  # 设备安装工单
    5972: 7019,  # 设备维修工单
    5973: 7019,  # 设备巡检计划
    5974: 7019,  # 芯片领用台账
    5977: 7019,  # 运维统计分析

    # 企业后台 7020
    6171: 7020,  # 企业仪表盘
    6172: 7020,  # 企业人员管理
    6173: 7020,  # 企业设备管理
    6174: 7020,  # 企业工资管理
    6175: 7020,  # 企业作业管理
    6176: 7020,  # 企业保险管理
    6177: 7020,  # 企业培训管理
    6178: 7020,  # 企业招聘管理
    6179: 7020,  # 企业财务管理
    6180: 7020,  # 企业信用报告

    # 便民服务 7021
    6191: 7021,  # 便民服务总览
    6192: 7021,  # 暖新地图
    6193: 7021,  # 培训课程
    6194: 7021,  # 法规库
    6195: 7021,  # 互助圈
    6196: 7021,  # 招聘用工市场

    # 新业态监管 7022
    4001: 7022,  # 新业态人员库
    4470: 7022,  # 平台企业聚合
    4471: 7022,  # 职业伤害监测
    4472: 7022,  # 培训管理

    # 职业病监管 7023
    4021: 7023,  # 职业病监测
    4482: 7023,  # 职业病预防项目
    4483: 7023,  # 职业健康档案
}

# Old top-level directory IDs to hide (visible='1')
OLD_TOP_LEVEL_IDS = [
    2000, 2020, 2040, 2060, 2080, 3900, 3920, 3940, 3960,
    4000, 4020, 4040, 4300, 4400, 5885, 6150, 6170, 6190, 3980
]

# Missing secondary modules to add as placeholder menus
# component paths should use existing file patterns where possible
MISSING_SECONDARY_MODULES = {
    7001: [  # 驾驶舱
        {"name": "地图可视化", "path": "mapVisualization", "component": "ygb/cockpit/map/index", "icon": "map-location", "perms": "ygb:cockpit:list"},
        {"name": "实时预警流", "path": "warningStream", "component": "ygb/cockpit/warningStream/index", "icon": "bell", "perms": "ygb:cockpit:list"},
        {"name": "红黄绿码企业分类", "path": "enterpriseCode", "component": "ygb/cockpit/enterpriseCode/index", "icon": "medal", "perms": "ygb:cockpit:list"},
    ],
    7003: [  # AI监测报告
        {"name": "风险评分与排名", "path": "riskScoreRanking", "component": "ygb/aiReport/ranking/index", "icon": "trophy", "perms": "ygb:aiReport:list"},
        {"name": "多维筛选与穿透", "path": "multiFilter", "component": "ygb/aiReport/filter/index", "icon": "filter", "perms": "ygb:aiReport:list"},
    ],
    7004: [  # 合同备案
        {"name": "合同到期提醒", "path": "contractExpiry", "component": "ygb/contract/expiry/index", "icon": "timer", "perms": "ygb:contract:list"},
        {"name": "未备案合同预警", "path": "unfiledWarning", "component": "ygb/contract/unfiled/index", "icon": "warning", "perms": "ygb:contract:list"},
    ],
    7005: [  # 用工考勤
        {"name": "考勤总览", "path": "attendanceOverview", "component": "ygb/attendance/overview/index", "icon": "data-board", "perms": "ygb:attendanceRaw:list"},
        {"name": "异常考勤统计", "path": "abnormalStatistics", "component": "ygb/attendance/abnormal/index", "icon": "warning", "perms": "ygb:attendanceRaw:list"},
        {"name": "设备在线率报表", "path": "deviceOnlineRate", "component": "ygb/attendance/deviceOnline/index", "icon": "monitor", "perms": "ygb:attendanceRaw:list"},
    ],
    7006: [  # 工资监管
        {"name": "工资发放监控", "path": "paymentMonitor", "component": "ygb/salary/paymentMonitor/index", "icon": "money", "perms": "ygb:salaryBatch:list"},
        {"name": "监管账户监控", "path": "accountMonitor", "component": "ygb/salary/accountMonitor/index", "icon": "bank-card", "perms": "ygb:salaryBatch:list"},
        {"name": "拖欠工资预警", "path": "overdueWarning", "component": "ygb/salary/overdueWarning/index", "icon": "warning", "perms": "ygb:salaryArrears:list"},
        {"name": "银行代发结果监控", "path": "bankDistribution", "component": "ygb/salary/bankDistribution/index", "icon": "credit-card", "perms": "ygb:salaryBatch:list"},
    ],
    7008: [  # 工伤监管
        {"name": "工伤认定辅助", "path": "recognitionAssist", "component": "ygb/injury/recognitionAssist/index", "icon": "timer", "perms": "ygb:injuryEvent:list"},
        {"name": "工伤统计分析", "path": "statisticalAnalysis", "component": "ygb/injury/statisticalAnalysis/index", "icon": "histogram", "perms": "ygb:injuryEvent:list"},
    ],
    7011: [  # 安责险管理
        {"name": "事故预防服务", "path": "preventionService", "component": "ygb/aqInsurance/preventionService/index", "icon": "first-aid-kit", "perms": "ygb:aqInsurance:list"},
    ],
    7012: [  # 设备管理
        {"name": "阳光劳务屏管理", "path": "sunlightScreen", "component": "ygb/device/sunlightScreen/index", "icon": "monitor", "perms": "ygb:device:list"},
        {"name": "设备台账", "path": "deviceLedger", "component": "ygb/device/ledger/index", "icon": "notebook", "perms": "ygb:device:list"},
        {"name": "批量操作", "path": "batchOperation", "component": "ygb/device/batchOperation/index", "icon": "operation", "perms": "ygb:device:list"},
    ],
    7013: [  # 扩面减损
        {"name": "催缴跟踪", "path": "collectionTracking", "component": "ygb/expansion/collectionTracking/index", "icon": "phone", "perms": "ygb:uninsuredList:list"},
        {"name": "工伤预防培训管理", "path": "preventionTraining", "component": "ygb/expansion/preventionTraining/index", "icon": "reading", "perms": "ygb:uninsuredList:list"},
        {"name": "培训课程与学时管理", "path": "courseHours", "component": "ygb/expansion/courseHours/index", "icon": "timer", "perms": "ygb:uninsuredList:list"},
        {"name": "培训效果评估", "path": "trainingEvaluation", "component": "ygb/expansion/trainingEvaluation/index", "icon": "trend-charts", "perms": "ygb:uninsuredList:list"},
    ],
    7014: [  # 信用评价
        {"name": "信用总览", "path": "creditOverview", "component": "ygb/credit/overview/index", "icon": "data-board", "perms": "ygb:creditScore:list"},
        {"name": "企业信用档案", "path": "enterpriseArchive", "component": "ygb/credit/enterpriseArchive/index", "icon": "folder", "perms": "ygb:creditScore:list"},
        {"name": "信用报告导出", "path": "reportExport", "component": "ygb/credit/reportExport/index", "icon": "download", "perms": "ygb:creditScore:list"},
    ],
    7015: [  # 统计报表
        {"name": "用工报表", "path": "employmentReport", "component": "ygb/statReport/employment/index", "icon": "user-filled", "perms": "ygb:statReport:employment:query"},
        {"name": "考勤报表", "path": "attendanceReport", "component": "ygb/statReport/attendance/index", "icon": "calendar", "perms": "ygb:statReport:attendance:query"},
        {"name": "专项整治报表", "path": "rectificationReport", "component": "ygb/statReport/rectification/index", "icon": "files", "perms": "ygb:statReport:rectification:query"},
    ],
    7016: [  # 人员管理
        {"name": "新业态人员库", "path": "newformPersonnel", "component": "ygb/person/newform/index", "icon": "cloudy", "perms": "ygb:person:list"},
    ],
    7017: [  # 单位管理
        {"name": "派遣/用工关联关系", "path": "dispatchRelation", "component": "ygb/enterprise/relation/index", "icon": "connection", "perms": "ygb:enterprise:list"},
        {"name": "新业态平台企业管理", "path": "newformEnterprise", "component": "ygb/enterprise/newform/index", "icon": "cloudy", "perms": "ygb:enterprise:list"},
        {"name": "企业自主服务", "path": "enterpriseSelfService", "component": "ygb/enterprise/selfService/index", "icon": "service", "perms": "ygb:enterprise:list"},
    ],
    7018: [  # 系统管理
        {"name": "接口与数据交换监管", "path": "interfaceMonitor", "component": "ygb/platform/exchange/index", "icon": "connection", "perms": "ygb:platformExchange:list"},
        {"name": "运行监控", "path": "runtimeMonitor", "component": "ygb/platform/overview/index", "icon": "cpu", "perms": "ygb:platformRuntime:query"},
    ],
    7019: [  # 运营后台
        {"name": "运营数据总览", "path": "operationDataOverview", "component": "ygb/operation/dataOverview/index", "icon": "data-board", "perms": "ygb:operation:overview"},
        {"name": "数据统计与分析", "path": "dataAnalysis", "component": "ygb/operation/dataAnalysis/index", "icon": "data-analysis", "perms": "ygb:operationRecruitStats:list"},
    ],
}


def generate_sql():
    lines = []
    lines.append("-- YGB PC 菜单按文档 23 模块全面重构脚本")
    lines.append("-- Generated automatically")
    lines.append(f"-- Time: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    lines.append("")
    lines.append("-- 1. Backup current sys_menu")
    lines.append("CREATE TABLE IF NOT EXISTS sys_menu_backup_pre_restructure_v2 AS SELECT * FROM sys_menu;")
    lines.append("")

    # Insert or update 23 module directories
    lines.append("-- 2. Insert 23 document module directories")
    lines.append("INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)")
    lines.append("VALUES")
    vals = []
    for m in MODULES:
        vals.append(f"  ({m['id']}, '{m['name']}', 0, {m['order']}, '{m['path']}', NULL, '', '', 1, 0, 'M', '0', '0', 'ygb', '', '{m['icon']}', 'admin', NOW(), 'admin', NOW(), '文档模块：{m['name']}')")
    lines.append(",\n".join(vals) + "")
    lines.append("ON DUPLICATE KEY UPDATE")
    lines.append("  menu_name = VALUES(menu_name),")
    lines.append("  order_num = VALUES(order_num),")
    lines.append("  path = VALUES(path),")
    lines.append("  icon = VALUES(icon),")
    lines.append("  visible = VALUES(visible),")
    lines.append("  status = VALUES(status),")
    lines.append("  remark = VALUES(remark);")
    lines.append("")

    # Move existing menus to new parents
    lines.append("-- 3. Move existing functional menus to new document module parents")
    for menu_id, parent_id in MENU_TO_MODULE_MAP.items():
        lines.append(f"UPDATE sys_menu SET parent_id = {parent_id}, portal_scope = 'ygb', update_time = NOW() WHERE menu_id = {menu_id};")
    lines.append("")

    # Hide old top-level directories
    lines.append("-- 4. Hide old business-domain top-level directories")
    for tid in OLD_TOP_LEVEL_IDS:
        lines.append(f"UPDATE sys_menu SET visible = '1', update_time = NOW() WHERE menu_id = {tid};")
    lines.append("")

    # Add missing secondary module placeholder menus
    lines.append("-- 5. Add missing secondary module placeholder menus")
    next_id = 8000
    for module_id, submodules in MISSING_SECONDARY_MODULES.items():
        for idx, sub in enumerate(submodules, 1):
            menu_id = next_id
            next_id += 1
            route_name = sub['path'][0].upper() + sub['path'][1:]
            lines.append(f"INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)")
            lines.append(f"VALUES ({menu_id}, '{sub['name']}', {module_id}, {idx}, '{sub['path']}', '{sub['component']}', '', 'Ygb{route_name}', 1, 0, 'C', '0', '0', 'ygb', '{sub['perms']}', '{sub['icon']}', 'admin', NOW(), 'admin', NOW(), '文档二级模块：{sub['name']}')")
            lines.append("ON DUPLICATE KEY UPDATE")
            lines.append("  menu_name = VALUES(menu_name),")
            lines.append("  parent_id = VALUES(parent_id),")
            lines.append("  order_num = VALUES(order_num),")
            lines.append("  path = VALUES(path),")
            lines.append("  component = VALUES(component),")
            lines.append("  route_name = VALUES(route_name),")
            lines.append("  perms = VALUES(perms),")
            lines.append("  icon = VALUES(icon),")
            lines.append("  remark = VALUES(remark);")
            lines.append("")

    return "\n".join(lines)


def component_file_exists(component):
    if not component:
        return False
    candidates = [
        UI_DIR / f'{component}.vue',
        UI_DIR / component / 'index.vue',
    ]
    return any(p.exists() for p in candidates)


def create_placeholders():
    template = '''<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">{module_name}</p>
        <h1 class="ygb-page__title">{page_title}</h1>
        <p class="ygb-page__desc">{desc}</p>
      </div>
    </section>

    <el-card shadow="never">
      <el-empty description="该功能页面正在建设中，敬请期待。">
        <template #image>
          <div style="font-size: 64px">🚧</div>
        </template>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup name="{route_name}">
// TODO: 按文档要求实现 {module_name} - {page_title}
</script>
'''
    missing = []
    for module_id, submodules in MISSING_SECONDARY_MODULES.items():
        module_name = next((m['name'] for m in MODULES if m['id'] == module_id), '')
        for sub in submodules:
            component = sub['component']
            if not component_file_exists(component):
                # Create at views/<component>/index.vue
                file_path = UI_DIR / component / 'index.vue'
                file_path.parent.mkdir(parents=True, exist_ok=True)
                route_name = f"Ygb{sub['path'][0].upper()}{sub['path'][1:]}"
                desc = f"{module_name} 模块下的 {sub['name']} 功能页面。"
                content = template.format(
                    module_name=module_name,
                    page_title=sub['name'],
                    desc=desc,
                    route_name=route_name
                )
                with open(file_path, 'w', encoding='utf-8') as f:
                    f.write(content)
                missing.append(file_path)
    return missing


def main():
    sql = generate_sql()
    output_path = r'D:\JAVA\YueGongBao-Vue\YueGongBao-Vue\sql\ygb_phase50_menu_restructure_by_document.sql'
    with open(output_path, 'w', encoding='utf-8') as f:
        f.write(sql)
    print(f"SQL written to {output_path}")

    placeholders = create_placeholders()
    print(f"Created {len(placeholders)} placeholder pages")


if __name__ == '__main__':
    main()
