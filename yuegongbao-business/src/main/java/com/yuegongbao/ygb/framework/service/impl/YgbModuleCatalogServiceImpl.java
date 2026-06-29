package com.yuegongbao.ygb.framework.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.yuegongbao.ygb.domain.vo.YgbModuleInfo;
import com.yuegongbao.ygb.framework.service.IYgbModuleCatalogService;

@Service
public class YgbModuleCatalogServiceImpl implements IYgbModuleCatalogService
{
    @Override
    public List<YgbModuleInfo> listModules()
    {
        return List.of(
            module("enterprise", "企业主数据", "企业档案、企业分类和基础归属信息。", 1, "foundation", "基础主数据", 2000L,
                "com.yuegongbao.ygb.foundation", "ygb-foundation-service", "shared"),
            module("person", "人员主数据", "人员档案、证件和用工基础信息。", 1, "foundation", "基础主数据", 2000L,
                "com.yuegongbao.ygb.foundation", "ygb-foundation-service", "shared"),
            module("contract", "合同备案", "劳动合同、派遣协议和备案材料闭环。", 1, "compliance", "合规主链", 2020L,
                "com.yuegongbao.ygb.compliance", "ygb-compliance-service", "medium"),
            module("attendanceRaw", "考勤上报", "原始考勤记录采集与校验。", 1, "compliance", "合规主链", 2020L,
                "com.yuegongbao.ygb.compliance", "ygb-compliance-service", "medium"),
            module("attendanceMonthly", "考勤归集", "月度考勤汇总与工资联动。", 1, "compliance", "合规主链", 2020L,
                "com.yuegongbao.ygb.compliance", "ygb-compliance-service", "medium"),
            module("salaryBatch", "工资批次", "工资批次、到账确认与提交发放。", 1, "compliance", "合规主链", 2020L,
                "com.yuegongbao.ygb.compliance", "ygb-compliance-service", "large"),
            module("salaryDetail", "工资明细", "工资明细校验、净额调整和发放状态跟踪。", 1, "compliance", "合规主链", 2020L,
                "com.yuegongbao.ygb.compliance", "ygb-compliance-service", "large"),
            module("socialPayment", "社保缴费监控", "社保缴费同步、缴费状态监控和来源回写。", 2, "regulation", "监管联动", 2040L,
                "com.yuegongbao.ygb.regulation", "ygb-regulation-service", "large"),
            module("socialBaseCompare", "社保基数比对", "工资实发与社保基数差异识别。", 2, "regulation", "监管联动", 2040L,
                "com.yuegongbao.ygb.regulation", "ygb-regulation-service", "large"),
            module("taxCompare", "税务监管", "个税申报同步与收入比对。", 2, "regulation", "监管联动", 2040L,
                "com.yuegongbao.ygb.regulation", "ygb-regulation-service", "large"),
            module("uninsuredList", "扩面减损", "漏保识别、催缴处置和监管闭环。", 2, "regulation", "监管联动", 2040L,
                "com.yuegongbao.ygb.regulation", "ygb-regulation-service", "large"),
            module("employmentRatio", "用工比例监控", "派遣比例监测和阈值预警。", 2, "regulation", "监管联动", 2040L,
                "com.yuegongbao.ygb.regulation", "ygb-regulation-service", "large"),
            module("fakeOutsourcing", "假外包识别", "四维评分识别疑似假外包企业。", 2, "regulation", "监管联动", 2040L,
                "com.yuegongbao.ygb.regulation", "ygb-regulation-service", "large"),
            module("device", "设备管理", "设备台账、授权、事件和指令日志。", 2, "safety", "设备工伤", 2060L,
                "com.yuegongbao.ygb.safety", "ygb-safety-service", "large"),
            module("injuryEvent", "工伤监管", "工伤事件上报、流转和超期预警。", 2, "safety", "设备工伤", 2060L,
                "com.yuegongbao.ygb.safety", "ygb-safety-service", "large"),
            module("preventionProject", "预防项目", "工伤预防项目管理与成效评估。", 2, "safety", "设备工伤", 2060L,
                "com.yuegongbao.ygb.safety", "ygb-safety-service", "medium"),
            module("warning", "预警中心", "统一预警工单列表、处置和闭环。", 1, "warning", "预警治理", 2080L,
                "com.yuegongbao.ygb.warning", "ygb-warning-service", "large"),
            module("warningRule", "预警规则", "预警规则配置和升级治理参数。", 1, "warning", "预警治理", 2080L,
                "com.yuegongbao.ygb.warning", "ygb-warning-service", "medium"),
            module("aqInsurance", "安责险投保监管", "保单同步、到期监控和预防费计提跟踪。", 1, "aqins", "安责险管理", 3960L,
                "com.yuegongbao.ygb.aqins", "ygb-aqins-service", "large"),
            module("preventionFund", "事故预防资金池", "预防费使用、余额和核销闭环。", 1, "aqins", "安责险管理", 3960L,
                "com.yuegongbao.ygb.aqins", "ygb-aqins-service", "medium"),
            module("creditScore", "企业信用评价", "企业信用评分、等级和红黄绿码治理。", 1, "credit", "信用评价", 3980L,
                "com.yuegongbao.ygb.credit", "ygb-credit-service", "large"),
            module("newformWorker", "新业态人员库", "平台企业、从业类型和职业伤害参保状态监管。", 1, "newform", "新业态监管", 4000L,
                "com.yuegongbao.ygb.newform", "ygb-newform-service", "large"),
            module("occupationMonitor", "职业病监测", "卫健职业病发病人数按行业和区域同步监测。", 1, "occupation", "职业病监管", 4020L,
                "com.yuegongbao.ygb.occupation", "ygb-occupation-service", "large"),
            module("heightWorkReport", "高处作业申报报备", "PC 端报备、证书核验、电子凭证和第三方导入接口。", 1, "techdefense", "技术防范", 4040L,
                "com.yuegongbao.ygb.techdefense", "ygb-techdefense-service", "medium"),
            module("aiReport", "AI监测报告", "自动评分、风险分级、区域排名和整改建议。", 1, "aireport", "AI监测报告", 3940L,
                "com.yuegongbao.ygb.aireport", "ygb-aireport-service", "large"),
            module("aiReportConfig", "评分模型配置", "维度权重、目标值和生效版本配置。", 1, "aireport", "AI监测报告", 3940L,
                "com.yuegongbao.ygb.aireport", "ygb-aireport-service", "medium"),
            module("cockpit", "综合驾驶舱", "监管指标总览、趋势分布和地图点位展示。", 1, "cockpit", "综合驾驶舱", 3900L,
                "com.yuegongbao.ygb.cockpit", "ygb-cockpit-service", "large"),
            module("statReport", "统计报表", "工伤、预警、工资和社保税务联动月报。", 1, "report", "统计报表", 3920L,
                "com.yuegongbao.ygb.report", "ygb-report-service", "large"));
    }

    private YgbModuleInfo module(String code, String name, String description, Integer priority, String categoryCode,
        String categoryName, Long menuRootId, String backendPackage, String futureServiceName, String splitLevel)
    {
        return new YgbModuleInfo(code, name, description, priority, categoryCode, categoryName, menuRootId,
            backendPackage, futureServiceName, splitLevel);
    }
}
