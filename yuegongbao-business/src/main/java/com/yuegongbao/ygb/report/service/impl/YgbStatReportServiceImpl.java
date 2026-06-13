package com.yuegongbao.ygb.report.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.report.domain.YgbStatReport;
import com.yuegongbao.ygb.report.domain.YgbStatReportGenerateRequest;
import com.yuegongbao.ygb.report.domain.YgbStatReportItem;
import com.yuegongbao.ygb.report.domain.YgbStatReportSummary;
import com.yuegongbao.ygb.report.mapper.YgbStatReportMapper;
import com.yuegongbao.ygb.report.service.IYgbStatReportService;
import com.yuegongbao.ygb.util.YgbRegionHelper;
import com.yuegongbao.ygb.util.YgbRegionScopeHelper;

@Service
public class YgbStatReportServiceImpl implements IYgbStatReportService
{
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Autowired
    private YgbStatReportMapper statReportMapper;

    @Autowired
    private YgbRegionScopeHelper regionScopeHelper;

    @Override
    public List<YgbStatReport> selectStatReportList(YgbStatReport report)
    {
        return statReportMapper.selectStatReportList(report);
    }

    @Override
    public YgbStatReportSummary selectStatReportSummary(YgbStatReport report)
    {
        List<YgbStatReport> list = selectStatReportList(report);
        YgbStatReportSummary summary = new YgbStatReportSummary();
        summary.setTotalCount(list.size());

        int generatedCount = 0;
        int draftCount = 0;
        int warningOverviewCount = 0;
        int injuryRateCount = 0;
        int socialTaxCount = 0;
        int employmentCount = 0;
        int attendanceCount = 0;
        int socialCount = 0;
        int taxCount = 0;
        int salaryCount = 0;
        int aqInsuranceCount = 0;
        int newformCount = 0;
        int occupationCount = 0;
        int unionCount = 0;
        int customCount = 0;
        for (YgbStatReport item : list)
        {
            String code = normalizeReportCode(item.getReportCode());
            if ("1".equals(item.getReportStatus()))
            {
                generatedCount++;
            }
            if ("0".equals(item.getReportStatus()))
            {
                draftCount++;
            }
            switch (code)
            {
                case "WARNING_OVERVIEW":
                    warningOverviewCount++;
                    break;
                case "INJURY_RATE":
                    injuryRateCount++;
                    break;
                case "SOCIAL_TAX":
                    socialTaxCount++;
                    break;
                case "EMPLOYMENT":
                    employmentCount++;
                    break;
                case "ATTENDANCE":
                    attendanceCount++;
                    break;
                case "SOCIAL":
                    socialCount++;
                    break;
                case "TAX":
                    taxCount++;
                    break;
                case "SALARY":
                case "SALARY_PAYMENT":
                    salaryCount++;
                    break;
                case "AQ_INSURANCE":
                    aqInsuranceCount++;
                    break;
                case "NEWFORM":
                    newformCount++;
                    break;
                case "OCCUPATION":
                    occupationCount++;
                    break;
                case "UNION_SUPERVISION":
                    unionCount++;
                    break;
                case "CUSTOM":
                    customCount++;
                    break;
                default:
                    break;
            }
        }

        summary.setGeneratedCount(generatedCount);
        summary.setDraftCount(draftCount);
        summary.setWarningOverviewCount(warningOverviewCount);
        summary.setInjuryRateCount(injuryRateCount);
        summary.setSocialTaxCount(socialTaxCount + employmentCount + attendanceCount + socialCount + taxCount);
        summary.setSalaryCount(salaryCount);
        summary.setEmploymentCount(employmentCount);
        summary.setAttendanceCount(attendanceCount);
        summary.setSocialCount(socialCount);
        summary.setTaxCount(taxCount);
        summary.setAqInsuranceCount(aqInsuranceCount);
        summary.setNewformCount(newformCount);
        summary.setOccupationCount(occupationCount);
        summary.setUnionCount(unionCount);
        summary.setCustomCount(customCount);
        summary.setYgbExplanation(buildYgbExplanation(report, summary));
        summary.setAzbExplanation(buildAzbExplanation(report, summary));
        return summary;
    }

    @Override
    public YgbStatReport selectStatReportById(Long reportId)
    {
        YgbStatReport report = statReportMapper.selectStatReportById(reportId);
        if (report == null)
        {
            throw new ServiceException("统计报表不存在");
        }
        return report;
    }

    @Override
    public List<YgbStatReportItem> selectStatReportItems(Long reportId)
    {
        return statReportMapper.selectStatReportItemList(reportId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generateReport(YgbStatReportGenerateRequest request, String operator)
    {
        String reportCode = normalizeReportCode(request.getReportCode());
        String statMonth = normalizeMonth(request.getStatMonth());
        String regionCode = regionScopeHelper.resolveAuthorizedRegionCode(request.getRegionCode());
        String regionPrefix = YgbRegionHelper.toRegionPrefix(regionCode);

        List<YgbStatReportItem> items = buildReportItems(reportCode, statMonth, regionPrefix);
        YgbStatReport report = statReportMapper.selectStatReportByScope(reportCode, statMonth, regionCode);
        boolean isNew = report == null;
        if (isNew)
        {
            report = new YgbStatReport();
            report.setReportCode(reportCode);
            report.setStatMonth(statMonth);
            report.setRegionCode(regionCode);
            report.setCreateBy(operator);
            report.setCreateTime(new Date());
        }
        report.setReportName(resolveReportName(reportCode));
        report.setReportStatus("1");
        report.setSourceMode("stub");
        report.setGeneratedTime(new Date());
        report.setAttachmentUrl("stub://report/" + reportCode.toLowerCase() + "/" + statMonth);
        fillSummary(report, items);
        report.setUpdateBy(operator);
        report.setUpdateTime(new Date());

        if (isNew)
        {
            statReportMapper.insertStatReport(report);
        }
        else
        {
            statReportMapper.updateStatReport(report);
            statReportMapper.deleteStatReportItemsByReportId(report.getReportId());
        }

        int sortNo = 1;
        for (YgbStatReportItem item : items)
        {
            item.setReportId(report.getReportId());
            item.setSortNo(sortNo++);
            statReportMapper.insertStatReportItem(item);
        }
        return report.getReportId();
    }

    private List<YgbStatReportItem> buildReportItems(String reportCode, String statMonth, String regionPrefix)
    {
        switch (reportCode)
        {
            case "INJURY_RATE":
            {
                List<YgbStatReportItem> items = statReportMapper.selectInjuryRateItems(statMonth, regionPrefix);
                items.forEach(item -> item.setItemName(YgbRegionHelper.resolveRegionName(item.getItemDimension())));
                return items;
            }
            case "WARNING_OVERVIEW":
            {
                List<YgbStatReportItem> items = statReportMapper.selectWarningOverviewItems(statMonth, regionPrefix);
                items.forEach(item -> item.setItemName(resolveWarningSourceName(item.getItemDimension())));
                return items;
            }
            case "SALARY_PAYMENT":
                return statReportMapper.selectSalaryPaymentItems(statMonth, regionPrefix);
            case "SALARY":
            {
                List<YgbStatReportItem> items = statReportMapper.selectSalaryPaymentItems(statMonth, regionPrefix);
                items.forEach(item -> item.setItemCategory("SALARY"));
                return items;
            }
            case "EMPLOYMENT":
                return statReportMapper.selectEmploymentItems(statMonth, regionPrefix);
            case "ATTENDANCE":
                return statReportMapper.selectAttendanceItems(statMonth, regionPrefix);
            case "SOCIAL":
                return statReportMapper.selectSocialItems(statMonth, regionPrefix);
            case "TAX":
                return statReportMapper.selectTaxItems(statMonth, regionPrefix);
            case "SOCIAL_TAX":
                return buildSocialTaxItems(statMonth, regionPrefix);
            case "AQ_INSURANCE":
                return statReportMapper.selectAqInsuranceItems(statMonth, regionPrefix);
            case "NEWFORM":
                return statReportMapper.selectNewformItems(statMonth, regionPrefix);
            case "OCCUPATION":
                return statReportMapper.selectOccupationItems(statMonth, regionPrefix);
            case "UNION_SUPERVISION":
                return statReportMapper.selectUnionSupervisionItems(statMonth, regionPrefix);
            case "CUSTOM":
                return statReportMapper.selectCustomItems(statMonth, regionPrefix);
            default:
                throw new ServiceException("暂不支持的统计报表类型");
        }
    }

    private List<YgbStatReportItem> buildSocialTaxItems(String statMonth, String regionPrefix)
    {
        List<YgbStatReportItem> items = new ArrayList<>();
        items.add(buildRiskItem("SOCIAL_TAX", "社保基数异常", "SOCIAL_ABNORMAL",
            statReportMapper.countSocialCompareAbnormal(statMonth, regionPrefix),
            rate(statReportMapper.countSocialCompareAbnormal(statMonth, regionPrefix),
                statReportMapper.countSocialCompareTotal(statMonth, regionPrefix))));
        items.add(buildRiskItem("SOCIAL_TAX", "个税比对异常", "TAX_ABNORMAL",
            statReportMapper.countTaxCompareAbnormal(statMonth, regionPrefix),
            rate(statReportMapper.countTaxCompareAbnormal(statMonth, regionPrefix),
                statReportMapper.countTaxCompareTotal(statMonth, regionPrefix))));
        items.add(buildRiskItem("SOCIAL_TAX", "漏保清单", "UNINSURED",
            statReportMapper.countUninsured(statMonth, regionPrefix),
            rate(statReportMapper.countUninsured(statMonth, regionPrefix),
                statReportMapper.countTaxCompareTotal(statMonth, regionPrefix))));
        items.add(buildRiskItem("SOCIAL_TAX", "用工比例黄警", "EMPLOYMENT_YELLOW",
            statReportMapper.countEmploymentByLevel(statMonth, regionPrefix, "1"),
            rate(statReportMapper.countEmploymentByLevel(statMonth, regionPrefix, "1"),
                statReportMapper.countEmploymentTotal(statMonth, regionPrefix))));
        items.add(buildRiskItem("SOCIAL_TAX", "用工比例红警", "EMPLOYMENT_RED",
            statReportMapper.countEmploymentByLevel(statMonth, regionPrefix, "2"),
            rate(statReportMapper.countEmploymentByLevel(statMonth, regionPrefix, "2"),
                statReportMapper.countEmploymentTotal(statMonth, regionPrefix))));
        items.add(buildRiskItem("SOCIAL_TAX", "假外包疑似", "FAKE_OUTSOURCING",
            statReportMapper.countFakeOutsourcingSuspected(statMonth, regionPrefix),
            rate(statReportMapper.countFakeOutsourcingSuspected(statMonth, regionPrefix),
                statReportMapper.countFakeOutsourcingTotal(statMonth, regionPrefix))));
        return items;
    }

    private YgbStatReportItem buildRiskItem(String category, String itemName, String dimension, Integer metricCount,
        BigDecimal metricRate)
    {
        YgbStatReportItem item = new YgbStatReportItem();
        item.setItemCategory(category);
        item.setItemName(itemName);
        item.setItemDimension(dimension);
        item.setMetricCount(defaultNumber(metricCount));
        item.setMetricValue(BigDecimal.valueOf(defaultNumber(metricCount)));
        item.setMetricRate(metricRate);
        return item;
    }

    private void fillSummary(YgbStatReport report, List<YgbStatReportItem> items)
    {
        int totalCount = items.stream().map(YgbStatReportItem::getMetricCount).filter(item -> item != null)
            .mapToInt(Integer::intValue).sum();
        BigDecimal totalValue = items.stream().map(YgbStatReportItem::getMetricValue).filter(item -> item != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal weightedRate = weightedRate(items);
        report.setMetricCount(totalCount);
        report.setMetricAmount(totalValue);
        report.setMetricRate(weightedRate);

        switch (normalizeReportCode(report.getReportCode()))
        {
            case "INJURY_RATE":
            {
                BigDecimal injuryRate = totalValue.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO
                    : BigDecimal.valueOf(totalCount * 1000D).divide(totalValue, 2, RoundingMode.HALF_UP);
                report.setMetricRate(injuryRate);
                report.setReportSummary("本月工伤事件 " + totalCount + " 起，参保样本 " + totalValue.intValue()
                    + " 人，工伤发生率 " + injuryRate + "‰");
                return;
            }
            case "WARNING_OVERVIEW":
                report.setReportSummary("本月预警 " + totalCount + " 条，闭环处置 " + totalValue.intValue()
                    + " 条，闭环率 " + weightedRate + "%");
                return;
            case "SALARY_PAYMENT":
            case "SALARY":
                report.setReportSummary("本月工资月报覆盖 " + totalCount + " 人次，实发金额 " + totalValue
                    + " 元，发放成功率 " + weightedRate + "%");
                return;
            case "EMPLOYMENT":
                report.setReportSummary("本月用工月报覆盖 " + items.size() + " 家企业，用工人数 " + totalCount
                    + " 人，平均比例 " + weightedRate + "%");
                return;
            case "ATTENDANCE":
                report.setReportSummary("本月考勤月报覆盖 " + items.size() + " 家企业，归集人数 " + totalCount
                    + " 人，通过率 " + weightedRate + "%");
                return;
            case "SOCIAL":
                report.setReportSummary("本月社保月报覆盖 " + items.size() + " 家企业，缴费人数 " + totalCount
                    + " 人，正常率 " + weightedRate + "%");
                return;
            case "TAX":
                report.setReportSummary("本月税务月报覆盖 " + items.size() + " 家企业，比对人数 " + totalCount
                    + " 人，异常率 " + weightedRate + "%");
                return;
            case "AQ_INSURANCE":
                report.setReportSummary("本月安责险月报覆盖 " + items.size() + " 家企业，保费 " + totalValue
                    + " 元，有效覆盖率 " + weightedRate + "%");
                return;
            case "NEWFORM":
                report.setReportSummary("本月新业态月报覆盖 " + totalCount + " 名人员，参保率 " + weightedRate + "%");
                return;
            case "OCCUPATION":
                report.setReportSummary("本月职业病月报汇总病例 " + totalCount + " 例，平均发病率 " + weightedRate + "%");
                return;
            case "UNION_SUPERVISION":
                report.setReportSummary("本月工会监督月报汇总 " + totalCount + " 条记录，闭环率 " + weightedRate + "%");
                return;
            case "CUSTOM":
                report.setReportSummary("本月自定义月报汇总 " + items.size() + " 个维度，综合值 " + totalValue
                    + "，综合率 " + weightedRate + "%");
                return;
            default:
                report.setReportSummary("本月联动风险项 " + totalCount + " 条，综合风险率 " + weightedRate + "%");
        }
    }

    private BigDecimal weightedRate(List<YgbStatReportItem> items)
    {
        BigDecimal totalWeight = BigDecimal.ZERO;
        BigDecimal totalRate = BigDecimal.ZERO;
        for (YgbStatReportItem item : items)
        {
            BigDecimal weight = BigDecimal.valueOf(defaultNumber(item.getMetricCount()));
            if (weight.compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            totalWeight = totalWeight.add(weight);
            totalRate = totalRate.add(defaultDecimal(item.getMetricRate()).multiply(weight));
        }
        if (totalWeight.compareTo(BigDecimal.ZERO) <= 0)
        {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return totalRate.divide(totalWeight, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal rate(Integer numerator, Integer denominator)
    {
        int actualDenominator = defaultNumber(denominator);
        if (actualDenominator <= 0)
        {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(defaultNumber(numerator) * 100D / actualDenominator)
            .setScale(2, RoundingMode.HALF_UP);
    }

    private Integer defaultNumber(Integer value)
    {
        return value == null ? 0 : value;
    }

    private BigDecimal defaultDecimal(BigDecimal value)
    {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String normalizeMonth(String statMonth)
    {
        if (StringUtils.isEmpty(statMonth))
        {
            return LocalDate.now().format(MONTH_FORMATTER);
        }
        if (!statMonth.matches("^\\d{4}-\\d{2}$"))
        {
            throw new ServiceException("统计月份格式错误，应为 yyyy-MM");
        }
        return statMonth;
    }

    private String normalizeReportCode(String reportCode)
    {
        if (StringUtils.isEmpty(reportCode))
        {
            return "";
        }
        return reportCode.trim().toUpperCase();
    }

    private String resolveReportName(String reportCode)
    {
        switch (reportCode)
        {
            case "INJURY_RATE":
                return "工伤发生率月报";
            case "WARNING_OVERVIEW":
                return "预警治理月报";
            case "SALARY_PAYMENT":
            case "SALARY":
                return "工资月报";
            case "EMPLOYMENT":
                return "用工月报";
            case "ATTENDANCE":
                return "考勤月报";
            case "SOCIAL":
                return "社保月报";
            case "TAX":
                return "税务月报";
            case "SOCIAL_TAX":
                return "社保税务联动月报";
            case "AQ_INSURANCE":
                return "安责险月报";
            case "NEWFORM":
                return "新业态月报";
            case "OCCUPATION":
                return "职业病月报";
            case "UNION_SUPERVISION":
                return "工会监督月报";
            case "CUSTOM":
                return "自定义月报";
            default:
                return reportCode;
        }
    }

    private String resolveWarningSourceName(String sourceCode)
    {
        if ("SOCIAL".equals(sourceCode))
        {
            return "社保监管";
        }
        if ("TAX".equals(sourceCode))
        {
            return "税务监管";
        }
        if ("EXPANSION".equals(sourceCode))
        {
            return "扩面减损";
        }
        if ("SPECIAL".equals(sourceCode))
        {
            return "专项治理";
        }
        if ("DEVICE".equals(sourceCode))
        {
            return "设备预警";
        }
        if ("INJURY".equals(sourceCode))
        {
            return "工伤监管";
        }
        return StringUtils.isEmpty(sourceCode) ? "其他" : sourceCode;
    }

    private List<Map<String, Object>> buildYgbExplanation(YgbStatReport report, YgbStatReportSummary summary)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> query = buildExplanationQuery(report);
        list.add(explanationItem("办理月报", summary.getGeneratedCount(), 1,
            "530.1 口径优先解释办理月报是否已形成正式结果。", "statReport", "statReport", "530.1 办理链解释", query));
        list.add(explanationItem("归档进度", summary.getDraftCount(), 0,
            "草稿越多，越需要继续承接归档链和办理闭环。", "statReport", "statReport", "530.1 办理链解释", query));
        list.add(explanationItem("联动结果", summary.getSocialTaxCount(), 0,
            "统计解释突出工资、社保税务联动和整改闭环结果。", "socialTax", "socialBaseCompare", "530.1 办理链解释", query));
        list.add(explanationItem("P4专题月报",
            defaultNumber(summary.getAqInsuranceCount()) + defaultNumber(summary.getNewformCount())
                + defaultNumber(summary.getOccupationCount()) + defaultNumber(summary.getUnionCount())
                + defaultNumber(summary.getCustomCount()),
            0, "P4 扩展月报已纳入同一 typed report 主链。", "statReport", "statReport", "530.1 办理链解释", query));
        return list;
    }

    private List<Map<String, Object>> buildAzbExplanation(YgbStatReport report, YgbStatReportSummary summary)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> query = buildExplanationQuery(report);
        list.add(explanationItem("治理月报", summary.getGeneratedCount(), 1,
            "6.1 口径优先解释治理月报与区域复核覆盖情况。", "statReport", "statReport", "6.1 治理解释", query));
        list.add(explanationItem("区域态势", summary.getWarningOverviewCount(), 0,
            "治理月报突出预警态势、工伤变化和重点对象分布。", "warning", "warning", "6.1 治理解释", query));
        list.add(explanationItem("压降结果",
            defaultNumber(summary.getInjuryRateCount()) + defaultNumber(summary.getSocialTaxCount())
                + defaultNumber(summary.getOccupationCount()),
            0, "压降结果按区域治理成效、隐患收敛和月报复盘顺序组织解释。", "injuryEvent", "statReport", "6.1 治理解释", query));
        return list;
    }

    private Map<String, Object> buildExplanationQuery(YgbStatReport report)
    {
        Map<String, Object> query = new LinkedHashMap<>();
        if (report == null)
        {
            query.put("regionCode", "440000");
            query.put("statMonth", normalizeMonth(null));
            return query;
        }
        query.put("regionCode", StringUtils.defaultIfEmpty(report.getRegionCode(), "440000"));
        query.put("statMonth", normalizeMonth(report.getStatMonth()));
        if (StringUtils.isNotEmpty(report.getReportCode()))
        {
            query.put("reportCode", normalizeReportCode(report.getReportCode()));
        }
        return query;
    }

    private Map<String, Object> explanationItem(String dimensionName, Object currentValue, Object targetValue,
        String summary, String evidenceModule, String recommendModule, String sourceLabel,
        Map<String, Object> defaultQuery)
    {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("dimensionName", dimensionName);
        item.put("currentValue", currentValue);
        item.put("targetValue", targetValue);
        item.put("thresholdValue", targetValue);
        item.put("summary", summary);
        item.put("explanationSummary", summary);
        item.put("evidenceModule", evidenceModule);
        item.put("evidenceSourceModule", evidenceModule);
        item.put("recommendModule", recommendModule);
        item.put("recommendedModule", recommendModule);
        item.put("defaultQuery", defaultQuery);
        item.put("sourceLabel", sourceLabel);
        item.put("sourceDescription", summary);
        return item;
    }
}
