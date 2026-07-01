package com.yuegongbao.ygb.cockpit.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.yuegongbao.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.aqins.domain.YgbAqInsurance;
import com.yuegongbao.ygb.aqins.domain.YgbPreventionFund;
import com.yuegongbao.ygb.aqins.domain.YgbAqInsuranceSummary;
import com.yuegongbao.ygb.aqins.domain.YgbPreventionFundSummary;
import com.yuegongbao.ygb.aqins.service.IYgbAqInsuranceService;
import com.yuegongbao.ygb.aqins.service.IYgbPreventionFundService;
import com.yuegongbao.ygb.cockpit.domain.YgbAzbCockpitDashboard;
import com.yuegongbao.ygb.cockpit.domain.YgbWorkbenchDashboard;
import com.yuegongbao.ygb.cockpit.domain.YgbCockpitDistribution;
import com.yuegongbao.ygb.cockpit.domain.YgbCockpitIndicator;
import com.yuegongbao.ygb.cockpit.domain.YgbCockpitMapFeature;
import com.yuegongbao.ygb.cockpit.domain.YgbCockpitSnapshot;
import com.yuegongbao.ygb.cockpit.domain.YgbGeoJsonFeatureCollection;
import com.yuegongbao.ygb.cockpit.mapper.YgbCockpitMapper;
import com.yuegongbao.ygb.cockpit.service.IYgbCockpitService;
import com.yuegongbao.ygb.compliance.domain.YgbAttendanceMonthly;
import com.yuegongbao.ygb.compliance.domain.YgbAttendanceRaw;
import com.yuegongbao.ygb.compliance.domain.YgbAttendanceRawSummary;
import com.yuegongbao.ygb.compliance.domain.YgbContract;
import com.yuegongbao.ygb.compliance.domain.YgbContractSummary;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryBatch;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryBatchSummary;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryDetail;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryDetailSummary;
import com.yuegongbao.ygb.compliance.service.IYgbAttendanceMonthlyService;
import com.yuegongbao.ygb.compliance.service.IYgbAttendanceRawService;
import com.yuegongbao.ygb.compliance.service.IYgbContractService;
import com.yuegongbao.ygb.compliance.service.IYgbSalaryBatchService;
import com.yuegongbao.ygb.compliance.service.IYgbSalaryDetailService;
import com.yuegongbao.ygb.credit.domain.YgbCreditScore;
import com.yuegongbao.ygb.credit.domain.YgbCreditScoreSummary;
import com.yuegongbao.ygb.credit.service.IYgbCreditScoreService;
import com.yuegongbao.ygb.foundation.domain.YgbEnterpriseSummary;
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;
import com.yuegongbao.ygb.foundation.domain.YgbPersonSummary;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.foundation.service.IYgbEnterpriseService;
import com.yuegongbao.ygb.foundation.service.IYgbPersonService;
import com.yuegongbao.ygb.newform.domain.YgbNewformWorker;
import com.yuegongbao.ygb.newform.service.IYgbNewformWorkerService;
import com.yuegongbao.ygb.occupation.domain.YgbOccupationMonitor;
import com.yuegongbao.ygb.occupation.service.IYgbOccupationMonitorService;
import com.yuegongbao.ygb.regulation.domain.YgbEmploymentRatio;
import com.yuegongbao.ygb.regulation.domain.YgbFakeOutsourcingRecord;
import com.yuegongbao.ygb.regulation.domain.YgbSocialBaseCompare;
import com.yuegongbao.ygb.regulation.domain.YgbSocialBaseCompareSummary;
import com.yuegongbao.ygb.regulation.domain.YgbSocialPayment;
import com.yuegongbao.ygb.regulation.domain.YgbSocialPaymentSummary;
import com.yuegongbao.ygb.regulation.domain.YgbTaxCompare;
import com.yuegongbao.ygb.regulation.domain.YgbTaxCompareSummary;
import com.yuegongbao.ygb.regulation.domain.YgbUninsuredList;
import com.yuegongbao.ygb.regulation.domain.YgbUninsuredListSummary;
import com.yuegongbao.ygb.regulation.service.IYgbEmploymentRatioService;
import com.yuegongbao.ygb.regulation.service.IYgbFakeOutsourcingService;
import com.yuegongbao.ygb.regulation.service.IYgbSocialBaseCompareService;
import com.yuegongbao.ygb.regulation.service.IYgbSocialPaymentService;
import com.yuegongbao.ygb.regulation.service.IYgbTaxCompareService;
import com.yuegongbao.ygb.regulation.service.IYgbUninsuredListService;
import com.yuegongbao.ygb.report.domain.YgbStatReport;
import com.yuegongbao.ygb.report.domain.YgbStatReportSummary;
import com.yuegongbao.ygb.report.service.IYgbStatReportService;
import com.yuegongbao.ygb.safety.domain.YgbDevice;
import com.yuegongbao.ygb.safety.domain.YgbInjuryEvent;
import com.yuegongbao.ygb.safety.domain.YgbDeviceSummary;
import com.yuegongbao.ygb.safety.domain.YgbInjuryEventSummary;
import com.yuegongbao.ygb.safety.service.IYgbDeviceService;
import com.yuegongbao.ygb.safety.service.IYgbInjuryEventService;
import com.yuegongbao.ygb.techdefense.domain.YgbHeightWorkReport;
import com.yuegongbao.ygb.techdefense.domain.YgbHeightWorkReportSummary;
import com.yuegongbao.ygb.techdefense.service.IYgbHeightWorkReportService;
import com.yuegongbao.ygb.util.YgbRegionHelper;
import com.yuegongbao.ygb.util.YgbRegionScopeHelper;
import com.yuegongbao.ygb.warning.domain.YgbWarning;
import com.yuegongbao.ygb.warning.domain.YgbWarningSummary;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;

@Service
public class YgbCockpitServiceImpl implements IYgbCockpitService
{
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private YgbCockpitMapper cockpitMapper;

    @Autowired
    private IYgbContractService contractService;

    @Autowired
    private IYgbAttendanceRawService attendanceRawService;

    @Autowired
    private IYgbAttendanceMonthlyService attendanceMonthlyService;

    @Autowired
    private IYgbSalaryBatchService salaryBatchService;

    @Autowired
    private IYgbSalaryDetailService salaryDetailService;

    @Autowired
    private IYgbSocialPaymentService socialPaymentService;

    @Autowired
    private IYgbSocialBaseCompareService socialBaseCompareService;

    @Autowired
    private IYgbTaxCompareService taxCompareService;

    @Autowired
    private IYgbEmploymentRatioService employmentRatioService;

    @Autowired
    private IYgbFakeOutsourcingService fakeOutsourcingService;

    @Autowired
    private IYgbNewformWorkerService newformWorkerService;

    @Autowired
    private IYgbOccupationMonitorService occupationMonitorService;

    @Autowired
    private IYgbWarningService warningService;

    @Autowired
    private IYgbAqInsuranceService aqInsuranceService;

    @Autowired
    private IYgbPreventionFundService preventionFundService;

    @Autowired
    private IYgbCreditScoreService creditScoreService;

    @Autowired
    private IYgbDeviceService deviceService;

    @Autowired
    private IYgbInjuryEventService injuryEventService;

    @Autowired
    private IYgbHeightWorkReportService heightWorkReportService;

    @Autowired
    private IYgbUninsuredListService uninsuredListService;

    @Autowired
    private IYgbStatReportService statReportService;

    @Autowired
    private IYgbEnterpriseService enterpriseService;

    @Autowired
    private IYgbPersonService personService;

    @Autowired
    private YgbRegionScopeHelper regionScopeHelper;

    @Override
    public YgbCockpitIndicator getIndicators(String regionCode, String statDate)
    {
        String dashboardRegion = resolveDashboardRegion(regionCode);
        String regionPrefix = YgbRegionHelper.toRegionPrefix(dashboardRegion);
        String queryDate = normalizeDate(statDate);
        YgbCockpitSnapshot snapshot = cockpitMapper.selectLatestSnapshot(dashboardRegion, queryDate);

        YgbCockpitIndicator indicator = new YgbCockpitIndicator();
        indicator.setRegionCode(dashboardRegion);
        indicator.setRegionName(YgbRegionHelper.resolveRegionName(dashboardRegion));
        indicator.setStatDate(queryDate);
        if (snapshot != null)
        {
            indicator.setDispatchCompanyCount(defaultNumber(snapshot.getDispatchCompanyCount()));
            indicator.setEmployerCount(defaultNumber(snapshot.getEmployerCount()));
            indicator.setDispatchedWorkerCount(defaultNumber(snapshot.getDispatchedWorkerCount()));
            indicator.setHighRiskEnterpriseCount(defaultNumber(snapshot.getHighRiskEnterpriseCount()));
            indicator.setInsuranceRate(defaultDecimal(snapshot.getInsuranceRate()));
            indicator.setAqInsuranceRate(defaultDecimal(snapshot.getAqInsuranceRate()));
            indicator.setExpandCompletionRate(defaultDecimal(snapshot.getExpandCompletionRate()));
            indicator.setNewInjuryRate(defaultDecimal(snapshot.getNewInjuryRate()));
        }
        else
        {
            indicator.setDispatchCompanyCount(0);
            indicator.setEmployerCount(0);
            indicator.setDispatchedWorkerCount(0);
            indicator.setHighRiskEnterpriseCount(0);
            indicator.setInsuranceRate(BigDecimal.ZERO);
            indicator.setAqInsuranceRate(BigDecimal.ZERO);
            indicator.setExpandCompletionRate(BigDecimal.ZERO);
            indicator.setNewInjuryRate(BigDecimal.ZERO);
        }
        indicator.setTodayWarningCount(defaultNumber(cockpitMapper.countWarningsByDate(regionPrefix, queryDate)));
        indicator.setPendingWarningCount(defaultNumber(cockpitMapper.countPendingWarnings(regionPrefix)));
        indicator.setOnlineDeviceCount(defaultNumber(cockpitMapper.countOnlineDevices(regionPrefix)));
        indicator.setOverdueInjuryCount(defaultNumber(cockpitMapper.countOverdueInjuries(regionPrefix)));
        return indicator;
    }

    @Override
    public List<YgbCockpitSnapshot> listTrend(String regionCode, String statDate, Integer days)
    {
        String dashboardRegion = resolveDashboardRegion(regionCode);
        String queryDate = normalizeDate(statDate);
        int actualDays = (days == null || days < 3) ? 7 : Math.min(days, 31);
        LocalDate endDate = LocalDate.parse(queryDate, DATE_FORMATTER);
        LocalDate startDate = endDate.minus(actualDays - 1L, ChronoUnit.DAYS);
        return cockpitMapper.selectSnapshotTrend(dashboardRegion, startDate.format(DATE_FORMATTER),
            endDate.format(DATE_FORMATTER));
    }

    @Override
    public List<YgbCockpitDistribution> listWarningDistribution(String regionCode, String statMonth)
    {
        String dashboardRegion = resolveDashboardRegion(regionCode);
        String regionPrefix = YgbRegionHelper.toRegionPrefix(dashboardRegion);
        String queryMonth = normalizeMonth(statMonth);
        List<YgbCockpitDistribution> list = cockpitMapper.selectWarningDistribution(regionPrefix, queryMonth);
        if (list.isEmpty())
        {
            return list;
        }

        int total = list.stream().map(YgbCockpitDistribution::getMetricCount).filter(count -> count != null)
            .mapToInt(Integer::intValue).sum();
        for (YgbCockpitDistribution item : list)
        {
            item.setDimensionName(resolveSourceName(item.getDimensionCode()));
            if (total <= 0)
            {
                item.setMetricRate(BigDecimal.ZERO);
            }
            else
            {
                item.setMetricRate(BigDecimal.valueOf(defaultNumber(item.getMetricCount()) * 100D / total)
                    .setScale(2, RoundingMode.HALF_UP));
            }
        }
        return list;
    }

    @Override
    public YgbGeoJsonFeatureCollection getMapFeatures(String regionCode, String statDate)
    {
        String dashboardRegion = resolveDashboardRegion(regionCode);
        String regionPrefix = YgbRegionHelper.toRegionPrefix(dashboardRegion);
        String queryDate = normalizeOptionalDate(statDate);
        List<YgbCockpitMapFeature> list = cockpitMapper.selectMapFeatureList(regionPrefix, queryDate);

        YgbGeoJsonFeatureCollection collection = new YgbGeoJsonFeatureCollection();
        collection.setStatDate(StringUtils.isEmpty(queryDate) ? normalizeDate(null) : queryDate);
        List<Map<String, Object>> features = buildGeoJsonFeatures(list);
        enrichEnterpriseMapProperties(features, dashboardRegion, normalizeMonth(null));
        collection.setFeatures(features);
        return collection;
    }

    @Override
    public YgbWorkbenchDashboard getYgbDashboard(String regionCode, String statDate, String statMonth, Integer days)
    {
        String dashboardRegion = resolveDashboardRegion(regionCode);
        String queryDate = normalizeDate(statDate);
        String queryMonth = normalizeMonth(statMonth);

        YgbWorkbenchDashboard dashboard = new YgbWorkbenchDashboard();
        dashboard.setIndicators(getIndicators(dashboardRegion, queryDate));
        dashboard.setTrend(listTrend(dashboardRegion, queryDate, days));
        dashboard.setDistribution(listWarningDistribution(dashboardRegion, queryMonth));
        dashboard.setMap(getMapFeatures(dashboardRegion, queryDate));
        dashboard.setContractSummary(contractService.selectContractSummary(buildContractQuery(dashboardRegion)));
        dashboard.setAttendanceRawSummary(attendanceRawService.selectAttendanceRawSummary(buildAttendanceRawQuery(dashboardRegion)));
        dashboard.setAttendanceMonthlySummary(
            attendanceMonthlyService.selectAttendanceMonthlySummary(buildAttendanceMonthlyQuery(dashboardRegion, queryMonth)));
        dashboard.setSalaryBatchSummary(salaryBatchService.selectSalaryBatchSummary(buildSalaryBatchQuery(dashboardRegion, queryMonth)));
        dashboard.setSalaryDetailSummary(
            salaryDetailService.selectSalaryDetailSummary(buildSalaryDetailQuery(dashboardRegion, queryMonth)));
        dashboard.setSocialPaymentSummary(
            socialPaymentService.selectSocialPaymentSummary(buildSocialPaymentQuery(dashboardRegion, queryMonth)));
        dashboard.setSocialBaseCompareSummary(
            socialBaseCompareService.selectSocialBaseCompareSummary(buildSocialBaseCompareQuery(dashboardRegion, queryMonth)));
        dashboard.setTaxCompareSummary(taxCompareService.selectTaxCompareSummary(buildTaxCompareQuery(dashboardRegion, queryMonth)));
        dashboard.setEmploymentRatioSummary(
            employmentRatioService.selectEmploymentRatioSummary(buildEmploymentRatioQuery(dashboardRegion, queryMonth)));
        dashboard.setFakeOutsourcingSummary(
            fakeOutsourcingService.selectFakeOutsourcingSummary(buildFakeOutsourcingQuery(dashboardRegion, queryMonth)));
        dashboard.setNewformWorkerSummary(
            newformWorkerService.selectNewformWorkerSummary(buildNewformWorkerQuery(dashboardRegion, queryMonth)));
        dashboard.setOccupationMonitorSummary(
            occupationMonitorService.selectOccupationMonitorSummary(buildOccupationMonitorQuery(dashboardRegion, queryMonth)));
        dashboard.setWarningSummary(warningService.selectWarningSummary(buildWarningQuery(dashboardRegion)));
        dashboard.setHeightWorkReportSummary(
            heightWorkReportService.selectHeightWorkReportSummary(buildHeightWorkReportQuery(dashboardRegion, queryDate)));
        dashboard.setUninsuredListSummary(
            uninsuredListService.selectUninsuredSummary(buildUninsuredListQuery(dashboardRegion, queryMonth)));
        dashboard.setStatReportSummary(statReportService.selectStatReportSummary(buildStatReportQuery(dashboardRegion, queryMonth)));
        dashboard.setEnterpriseSummary(enterpriseService.selectEnterpriseSummary(buildEnterpriseQuery(dashboardRegion)));
        dashboard.setPersonSummary(personService.selectPersonSummary(buildPersonQuery(dashboardRegion)));
        dashboard.setCreditScoreSummary(creditScoreService.selectCreditScoreSummary(buildCreditScoreQuery(dashboardRegion, queryMonth)));
        dashboard.setCreditRanking(listTopRiskCreditScores(dashboardRegion, queryMonth));
        dashboard.setDefaultQuery(buildPortalDefaultQuery(dashboardRegion, queryDate, queryMonth));
        dashboard.setSourceLabel("ygb-home");
        dashboard.setSourceDescription("首页办理聚合");
        List<Map<String, Object>> ygbQueueSections = buildYgbQueueSections(dashboard, dashboardRegion, queryDate, queryMonth);
        List<Map<String, Object>> ygbExplanation = buildYgbHomeExplanation(dashboard, dashboardRegion, queryMonth);
        dashboard.setSummaryCards(buildYgbSummaryCards(dashboard, queryMonth));
        dashboard.setFocusQueues(buildYgbFocusQueues(dashboard, dashboardRegion, queryDate, queryMonth));
        dashboard.setQuickActions(buildYgbQuickActions(dashboardRegion, queryDate, queryMonth));
        dashboard.setQuickSections(buildQuickSections(
            dashboard.getQuickActions(),
            "门户快捷入口",
            "首页快捷入口已按来源上下文完成门户聚合。"));
        dashboard.setHintTags(buildYgbHintTags(dashboard));
        dashboard.setQueueSections(ygbQueueSections);
        dashboard.setWorkflowSteps(buildYgbWorkflowSteps(ygbQueueSections, ygbExplanation));
        dashboard.setYgbExplanation(ygbExplanation);
        dashboard.setFocusPanels(buildYgbFocusPanels(ygbExplanation));
        dashboard.setHomeSummary(buildYgbHomeSummary(dashboard));
        return dashboard;
    }

    @Override
    public YgbWorkbenchDashboard getYgbHomeAggregate(String regionCode, String statDate, String statMonth, Integer days)
    {
        return getYgbDashboard(regionCode, statDate, statMonth, days);
    }

    @Override
    public YgbAzbCockpitDashboard getAzbDashboard(String regionCode, String statDate, String statMonth, Integer days)
    {
        String dashboardRegion = resolveDashboardRegion(regionCode);
        String queryDate = normalizeDate(statDate);
        String queryMonth = normalizeMonth(statMonth);
        String roleView = resolveAzbRoleView();

        YgbAzbCockpitDashboard dashboard = new YgbAzbCockpitDashboard();
        dashboard.setIndicators(getIndicators(dashboardRegion, queryDate));
        dashboard.setTrend(listTrend(dashboardRegion, queryDate, days));
        dashboard.setDistribution(listWarningDistribution(dashboardRegion, queryMonth));
        dashboard.setMap(getMapFeatures(dashboardRegion, queryDate));
        dashboard.setRoleView(roleView);
        dashboard.setRoleLabel(resolveAzbRoleLabel(roleView));
        dashboard.setRoleDescription(resolveAzbRoleDescription(roleView));
        dashboard.setAqInsuranceSummary(aqInsuranceService.selectAqInsuranceSummary(buildAqInsuranceQuery(dashboardRegion, queryMonth)));
        dashboard.setPreventionFundSummary(preventionFundService.selectPreventionFundSummary(buildPreventionFundQuery(dashboardRegion, queryMonth)));
        dashboard.setCreditScoreSummary(creditScoreService.selectCreditScoreSummary(buildCreditScoreQuery(dashboardRegion, queryMonth)));
        dashboard.setCreditRanking(listTopRiskCreditScores(dashboardRegion, queryMonth));
        dashboard.setWarningSummary(warningService.selectWarningSummary(buildWarningQuery(dashboardRegion)));
        dashboard.setHeightWorkReportSummary(heightWorkReportService.selectHeightWorkReportSummary(buildHeightWorkReportQuery(dashboardRegion, queryDate)));
        dashboard.setDeviceSummary(deviceService.selectDeviceSummary(buildDeviceQuery(dashboardRegion)));
        dashboard.setInjuryEventSummary(injuryEventService.selectInjuryEventSummary(buildInjuryEventQuery(dashboardRegion)));
        dashboard.setStatReportSummary(statReportService.selectStatReportSummary(buildStatReportQuery(dashboardRegion, queryMonth)));
        dashboard.setEnterpriseSummary(enterpriseService.selectEnterpriseSummary(buildEnterpriseQuery(dashboardRegion)));
        dashboard.setPersonSummary(personService.selectPersonSummary(buildPersonQuery(dashboardRegion)));
        dashboard.setSummaryCards(buildAzbSummaryCards(dashboard, roleView));
        dashboard.setFocusQueues(buildAzbFocusQueues(dashboard, roleView));
        dashboard.setWorkflowSteps(buildAzbWorkflowSteps(roleView));
        dashboard.setQuickActions(buildAzbQuickActions(roleView, dashboardRegion, queryDate, queryMonth));
        dashboard.setQuickSections(buildQuickSections(
            dashboard.getQuickActions(),
            "门户快捷入口",
            "首页快捷入口已按重点对象和治理来源完成门户聚合。"));
        dashboard.setHintTags(buildAzbHintTags(dashboard, roleView));
        dashboard.setDefaultQuery(buildPortalDefaultQuery(dashboardRegion, queryDate, queryMonth));
        dashboard.setSourceLabel("azb-home");
        dashboard.setSourceDescription("首页治理聚合");
        dashboard.setQueueSections(buildAzbQueueSections(dashboard, roleView, dashboardRegion, queryDate, queryMonth));
        dashboard.setAzbExplanation(buildAzbHomeExplanation(dashboard, roleView, dashboardRegion, queryMonth));
        dashboard.setFocusPanels(buildAzbFocusPanels(dashboard, roleView, dashboardRegion, queryMonth));
        dashboard.setHomeSummary(buildAzbHomeSummary(dashboard, roleView));
        return dashboard;
    }

    @Override
    public YgbAzbCockpitDashboard getAzbHomeAggregate(String regionCode, String statDate, String statMonth, Integer days)
    {
        return getAzbDashboard(regionCode, statDate, statMonth, days);
    }

    private String resolveAzbRoleView()
    {
        if (SecurityUtils.hasRole("ygb_bank"))
        {
            return "bank";
        }
        if (SecurityUtils.hasRole("ygb_insurer"))
        {
            return "insurer";
        }
        if (SecurityUtils.hasRole("ygb_emergency_supervisor"))
        {
            return "emergency";
        }
        return "azb-default";
    }

    private String resolveAzbRoleLabel(String roleView)
    {
        if ("bank".equals(roleView))
        {
            return "银行协同";
        }
        if ("insurer".equals(roleView))
        {
            return "保险机构";
        }
        if ("emergency".equals(roleView))
        {
            return "应急监管员";
        }
        return "安责保工作台";
    }

    private String resolveAzbRoleDescription(String roleView)
    {
        if ("bank".equals(roleView))
        {
            return "聚焦信用画像、区域报表和重点企业变化，保持只读协同视角。";
        }
        if ("insurer".equals(roleView))
        {
            return "围绕安责险覆盖、事故预防资金和信用评分形成日常只读协同。";
        }
        if ("emergency".equals(roleView))
        {
            return "先看区域风险面，再盯设备在线、高处作业和预警闭环。";
        }
        return "当前门户以现场治理、投保协同和风险压降为主。";
    }

    private List<Map<String, Object>> buildYgbSummaryCards(YgbWorkbenchDashboard dashboard, String statMonth)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        YgbEnterpriseSummary enterpriseSummary = dashboard.getEnterpriseSummary();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbStatReportSummary statReportSummary = dashboard.getStatReportSummary();
        YgbSalaryDetailSummary salaryDetailSummary = dashboard.getSalaryDetailSummary();
        YgbSocialPaymentSummary socialPaymentSummary = dashboard.getSocialPaymentSummary();
        YgbTaxCompareSummary taxCompareSummary = dashboard.getTaxCompareSummary();
        YgbUninsuredListSummary uninsuredListSummary = dashboard.getUninsuredListSummary();

        list.add(summaryCard("enterprisePending", "企业待办", defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()), "条",
            "按企业维度汇总待办、异常与联动入口。", "ygb-summary-card--warning"));
        list.add(summaryCard("enterpriseRisk", "企业异常", defaultNumber(uninsuredListSummary == null ? null : uninsuredListSummary.getPendingCount())
            + defaultNumber(taxCompareSummary == null ? null : taxCompareSummary.getAbnormalCount()), "条",
            "联动工资、社保、个税与扩面减损异常。", "ygb-summary-card--danger"));
        list.add(summaryCard("monthStatus", statMonth + "月归档", defaultNumber(statReportSummary == null ? null : statReportSummary.getDraftCount()), "条",
            "按统计月输出工资、社保、个税与月报状态。", "ygb-summary-card--success"));
        list.add(summaryCard("priorityQueue", "首页优先队列", defaultNumber(salaryDetailSummary == null ? null : salaryDetailSummary.getFailedCount())
            + defaultNumber(socialPaymentSummary == null ? null : socialPaymentSummary.getOverdueCount())
            + defaultNumber(enterpriseSummary == null ? null : enterpriseSummary.getSyncErrorCount()), "条",
            "接口已返回首页排序依据。", ""));
        return list;
    }

    private List<Map<String, Object>> buildYgbQuickActions(String regionCode, String statDate, String statMonth)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(quickAction("contract", "合同备案", "办理链", "进入合同待备案列表，继续承接用工合规办理。",
            modulePath("ygb", "contract"),
            buildSourceQuery(regionCode, statDate, statMonth, "contractStatus", "1"),
            "首页企业办理", "来自首页企业待办聚合"));
        list.add(quickAction("salaryBatch", "工资批次", "月度办理", "进入工资批次，处理当月待生成与待提交数据。",
            modulePath("ygb", "salaryBatch"),
            buildSourceQuery(regionCode, statDate, statMonth, "batchStatus", "3"),
            "首页月份聚合", "来自首页月份维度聚合"));
        list.add(quickAction("socialPayment", "社保缴纳", "月度比对", "进入社保欠费与整改列表，保持月份来源条件。",
            modulePath("ygb", "socialPayment"),
            buildSourceQuery(regionCode, statDate, statMonth, "paymentStatus", "2"),
            "首页月份聚合", "来自首页月份维度聚合"));
        list.add(quickAction("taxCompare", "个税比对", "联动异常", "进入个税异常列表，核查工资与申报差异。",
            modulePath("ygb", "taxCompare"),
            buildSourceQuery(regionCode, statDate, statMonth, "compareResult", "2"),
            "首页月份聚合", "来自首页月份维度聚合"));
        list.add(quickAction("uninsuredList", "扩面减损", "整改名单", "进入漏保清单，继续按来源条件整改闭环。",
            modulePath("ygb", "uninsuredList"),
            buildSourceQuery(regionCode, statDate, statMonth, "disposalStatus", "0"),
            "首页整改闭环", "来自首页扩面减损聚合"));
        list.add(quickAction("warning", "预警中心", "闭环处置", "进入预警列表，继续处理首页优先队列。",
            modulePath("ygb", "warning"),
            buildSourceQuery(regionCode, statDate, statMonth, "warnStatus", "0"),
            "首页优先队列", "来自首页预警闭环聚合"));
        list.addAll(buildYgbSupplementQuickActions(regionCode, statDate, statMonth));
        return list;
    }

    private List<Map<String, Object>> buildYgbFocusQueues(YgbWorkbenchDashboard dashboard, String regionCode, String statDate,
        String statMonth)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        YgbContractSummary contractSummary = dashboard.getContractSummary();
        YgbAttendanceRawSummary attendanceRawSummary = dashboard.getAttendanceRawSummary();
        YgbSalaryBatchSummary salaryBatchSummary = dashboard.getSalaryBatchSummary();
        YgbSalaryDetailSummary salaryDetailSummary = dashboard.getSalaryDetailSummary();
        YgbSocialPaymentSummary socialPaymentSummary = dashboard.getSocialPaymentSummary();
        YgbSocialBaseCompareSummary socialBaseCompareSummary = dashboard.getSocialBaseCompareSummary();
        YgbTaxCompareSummary taxCompareSummary = dashboard.getTaxCompareSummary();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbHeightWorkReportSummary heightWorkReportSummary = dashboard.getHeightWorkReportSummary();
        YgbUninsuredListSummary uninsuredListSummary = dashboard.getUninsuredListSummary();

        list.add(focusQueue("contract", "合同备案链路", defaultNumber(contractSummary == null ? null : contractSummary.getPendingCount()), "条",
            "合同若未备案，将继续阻断考勤归集和工资闭环，建议优先核对条款和备案状态。", modulePath("ygb", "contract"), "补合同材料或推进备案",
            buildSourceQuery(regionCode, statDate, statMonth, "contractStatus", "1"), "驾驶舱焦点/ 合同备案链路", "来自驾驶舱企业办理焦点"));
        list.add(focusQueue("attendance", "考勤归集链路", defaultNumber(attendanceRawSummary == null ? null : attendanceRawSummary.getUncollectedCount()), "条",
            "未归集或异常考勤会直接影响工资核验与发放，建议先处理补录和校验失败对象。", modulePath("ygb", "attendanceRaw"), "处理未归集和异常考勤",
            buildSourceQuery(regionCode, statDate, statMonth, "collectStatus", "0"), "驾驶舱焦点/ 考勤归集链路", "来自驾驶舱考勤归集焦点"));
        list.add(focusQueue("salaryBatch", "工资发放链路",
            defaultNumber(salaryBatchSummary == null ? null : salaryBatchSummary.getPendingGenerateCount())
                + defaultNumber(salaryBatchSummary == null ? null : salaryBatchSummary.getPendingSubmitCount())
                + defaultNumber(salaryDetailSummary == null ? null : salaryDetailSummary.getFailedCount()),
            "条", "工资链当前重点在待生成明细、待提交发放和失败明细，建议先判断卡点在到账、明细生成还是银行回写。",
            modulePath("ygb", "salaryBatch"), "推进发薪闭环", buildSourceQuery(regionCode, statDate, statMonth, "batchStatus", "3"),
            "驾驶舱焦点/ 工资发放链路", "来自驾驶舱工资发放焦点"));
        list.add(focusQueue("socialTax", "社保税务整改",
            defaultNumber(socialPaymentSummary == null ? null : socialPaymentSummary.getOverdueCount())
                + defaultNumber(socialBaseCompareSummary == null ? null : socialBaseCompareSummary.getAbnormalCount())
                + defaultNumber(taxCompareSummary == null ? null : taxCompareSummary.getAbnormalCount()),
            "条", "当前财务整改重点在社保欠费、社保基数异常和个税差异，建议先锁定影响范围再决定补缴、回查还是继续预警承接。",
            modulePath("ygb", "socialPayment"), "联动财务整改", buildSourceQuery(regionCode, statDate, statMonth, "paymentStatus", "2"),
            "驾驶舱焦点/ 社保税务整改", "来自驾驶舱社保税务整改焦点"));
        list.add(focusQueue("warning", "预警处置链路",
            defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount())
                + defaultNumber(warningSummary == null ? null : warningSummary.getProcessingCount()),
            "条", "预警中心当前承接社保、税务、扩面、现场和工伤联动异常，建议先消化待处置和处理中工单。", modulePath("ygb", "warning"),
            "进入预警中心", buildSourceQuery(regionCode, statDate, statMonth, "warnStatus", "0"), "驾驶舱焦点/ 预警处置链路",
            "来自驾驶舱预警处置焦点"));
        list.add(focusQueue("heightWork", "高处作业闭环",
            defaultNumber(heightWorkReportSummary == null ? null : heightWorkReportSummary.getActiveCount())
                + defaultNumber(heightWorkReportSummary == null ? null : heightWorkReportSummary.getInvalidWorkerCount()),
            "条", "高处作业当前重点在进行中作业、异常证书和结束留痕，建议优先核对未结束和证书异常对象。", modulePath("ygb", "heightWorkReport"),
            "处理作业闭环", buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "0"), "驾驶舱焦点/ 高处作业闭环",
            "来自驾驶舱高处作业焦点"));
        list.add(focusQueue("uninsured", "扩面减损链路",
            defaultNumber(uninsuredListSummary == null ? null : uninsuredListSummary.getPendingCount())
                + defaultNumber(uninsuredListSummary == null ? null : uninsuredListSummary.getUnwarnedCount()),
            "条", "漏保对象当前重点在待核查、未预警和高暴露对象，建议优先推进催缴和补缴闭环。", modulePath("ygb", "uninsuredList"),
            "推进扩面整改", buildSourceQuery(regionCode, statDate, statMonth, "disposalStatus", "0"), "驾驶舱焦点 / 扩面减损链路",
            "来自驾驶舱扩面减损焦点"));
        return list;
    }

    private List<Map<String, Object>> buildYgbHintTags(YgbWorkbenchDashboard dashboard)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbSalaryDetailSummary salaryDetailSummary = dashboard.getSalaryDetailSummary();
        YgbSocialPaymentSummary socialPaymentSummary = dashboard.getSocialPaymentSummary();
        YgbHeightWorkReportSummary heightWorkReportSummary = dashboard.getHeightWorkReportSummary();
        YgbUninsuredListSummary uninsuredListSummary = dashboard.getUninsuredListSummary();

        if (defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()) > 0)
        {
            list.add(hintTag("当前仍有 " + defaultNumber(warningSummary.getPendingCount()) + " 条待处置预警，建议优先承接预警闭环。", "warning"));
        }
        if (defaultNumber(salaryDetailSummary == null ? null : salaryDetailSummary.getFailedCount()) > 0)
        {
            list.add(hintTag("工资明细存在 " + defaultNumber(salaryDetailSummary.getFailedCount()) + " 条发放失败记录，建议优先核对失败原因和回写状态。", "danger"));
        }
        if (defaultNumber(socialPaymentSummary == null ? null : socialPaymentSummary.getOverdueCount()) > 0)
        {
            list.add(hintTag("当前存在 " + defaultNumber(socialPaymentSummary.getOverdueCount()) + " 条社保欠费对象，建议联动社保缴费台账和漏保整改链处置。", "warning"));
        }
        if (defaultNumber(heightWorkReportSummary == null ? null : heightWorkReportSummary.getInvalidWorkerCount()) > 0)
        {
            list.add(hintTag("高处作业存在 " + defaultNumber(heightWorkReportSummary.getInvalidWorkerCount()) + " 名证书异常人员，建议优先核对作业人员资格和结束留痕。", "danger"));
        }
        if (defaultNumber(uninsuredListSummary == null ? null : uninsuredListSummary.getPendingCount()) > 0)
        {
            list.add(hintTag("漏保整改仍有 " + defaultNumber(uninsuredListSummary.getPendingCount()) + " 条待核查对象，建议结合扩面减损和统计报表同步推进。", "info"));
        }
        return list;
    }

    private List<Map<String, Object>> buildYgbQueueSections(YgbWorkbenchDashboard dashboard, String regionCode, String statDate,
        String statMonth)
    {
        List<Map<String, Object>> sections = new ArrayList<>();
        YgbContractSummary contractSummary = dashboard.getContractSummary();
        YgbSalaryBatchSummary salaryBatchSummary = dashboard.getSalaryBatchSummary();
        YgbSalaryDetailSummary salaryDetailSummary = dashboard.getSalaryDetailSummary();
        YgbSocialPaymentSummary socialPaymentSummary = dashboard.getSocialPaymentSummary();
        YgbTaxCompareSummary taxCompareSummary = dashboard.getTaxCompareSummary();
        YgbUninsuredListSummary uninsuredListSummary = dashboard.getUninsuredListSummary();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbStatReportSummary statReportSummary = dashboard.getStatReportSummary();

        List<Map<String, Object>> enterpriseItems = new ArrayList<>();
        enterpriseItems.add(queueItem("contractPending", "合同待备案", "企业办理待办", defaultNumber(contractSummary == null ? null : contractSummary.getPendingCount()),
            "条", "锁定企业办理链仍未归档的合同对象。", "待承接", "contract", "ygb",
            buildSourceQuery(regionCode, statDate, statMonth, "contractStatus", "1"), "首页企业办理", "来自首页企业维度聚合"));
        enterpriseItems.add(queueItem("warningPending", "预警闭环", "企业异常待继续处置", defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()),
            "条", "按企业维度聚合待办量和异常量。", "优先处理", "warning", "ygb",
            buildSourceQuery(regionCode, statDate, statMonth, "warnStatus", "0"), "首页企业办理", "来自首页企业维度聚合"));
        sections.add(queueSection("enterprise", "企业维度办理队列", "按企业输出待办量、异常量、月报状态与联动来源。", enterpriseItems));

        List<Map<String, Object>> monthItems = new ArrayList<>();
        monthItems.add(queueItem("salaryBatch", "工资批次待生成", "月份办理摘要", defaultNumber(salaryBatchSummary == null ? null : salaryBatchSummary.getPendingGenerateCount()),
            "批", "按统计月承接工资发放办理链。", "继续办理", "salaryBatch", "ygb",
            buildSourceQuery(regionCode, statDate, statMonth, "batchStatus", "3"), "首页月份聚合", "来自首页月份维度聚合"));
        monthItems.add(queueItem("socialPayment", "社保欠费整改", "月份风险摘要", defaultNumber(socialPaymentSummary == null ? null : socialPaymentSummary.getOverdueCount()),
            "条", "按统计月继续处理社保欠费与补缴情形。", "继续整改", "socialPayment", "ygb",
            buildSourceQuery(regionCode, statDate, statMonth, "paymentStatus", "2"), "首页月份聚合", "来自首页月份维度聚合"));
        monthItems.add(queueItem("taxCompare", "个税异常比对", "月份联动摘要", defaultNumber(taxCompareSummary == null ? null : taxCompareSummary.getAbnormalCount()),
            "条", "按统计月核验工资与个税申报差异。", "继续比对", "taxCompare", "ygb",
            buildSourceQuery(regionCode, statDate, statMonth, "compareResult", "2"), "首页月份聚合", "来自首页月份维度聚合"));
        sections.add(queueSection("month", "月份维度办理队列", "按统计月输出工资、社保、个税、扩面减损与预警摘要。", monthItems));

        List<Map<String, Object>> priorityItems = new ArrayList<>();
        priorityItems.add(queueItem("salaryDetailFail", "工资发放失败", "优先级排序字段已由接口给出", defaultNumber(salaryDetailSummary == null ? null : salaryDetailSummary.getFailedCount()),
            "条", "优先级最高的失败明细，前端不再重排业务语义。", "优先回查", "salaryDetail", "ygb",
            buildSourceQuery(regionCode, statDate, statMonth, "payStatus", "3"), "首页优先队列", "来自首页优先级维度聚合"));
        priorityItems.add(queueItem("uninsuredPending", "漏保整改名单", "扩面减损优先项", defaultNumber(uninsuredListSummary == null ? null : uninsuredListSummary.getPendingCount()),
            "条", "按整改链优先级输出待核查名单。", "优先整改", "uninsuredList", "ygb",
            buildSourceQuery(regionCode, statDate, statMonth, "disposalStatus", "0"), "首页优先队列", "来自首页优先级维度聚合"));
        priorityItems.add(queueItem("statDraft", "月报待归档", "办理月报归档队列", defaultNumber(statReportSummary == null ? null : statReportSummary.getDraftCount()),
            "条", "优先处理草稿与归档闭环。", "继续归档", "statReport", "ygb",
            buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "0"), "首页优先队列", "来自首页优先级维度聚合"));
        sections.add(queueSection("priority", "优先级维度队列", "输出首页队列排序字段和默认查询条件。", priorityItems));
        appendYgbSupplementQueueSections(sections, dashboard, regionCode, statDate, statMonth);
        return sections;
    }

    private List<Map<String, Object>> buildYgbHomeExplanation(YgbWorkbenchDashboard dashboard, String regionCode, String statMonth)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        YgbContractSummary contractSummary = dashboard.getContractSummary();
        YgbSalaryDetailSummary salaryDetailSummary = dashboard.getSalaryDetailSummary();
        YgbSocialBaseCompareSummary socialBaseCompareSummary = dashboard.getSocialBaseCompareSummary();
        YgbTaxCompareSummary taxCompareSummary = dashboard.getTaxCompareSummary();
        YgbUninsuredListSummary uninsuredListSummary = dashboard.getUninsuredListSummary();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();

        list.add(explanationItem("用工合规", defaultNumber(contractSummary == null ? null : contractSummary.getPendingCount()), "0",
            "530.1 办理链优先承接合同备案与用工合规待办。", "contract", "contract",
            buildSourceQuery(regionCode, null, statMonth, "contractStatus", "1"), "首页办理解释", "来源于合同备案办理链"));
        list.add(explanationItem("工资发放", defaultNumber(salaryDetailSummary == null ? null : salaryDetailSummary.getFailedCount()), "0",
            "先看工资发放失败明细，再回到月度办理链闭环。", "salaryDetail", "salaryDetail",
            buildSourceQuery(regionCode, null, statMonth, "payStatus", "3"), "首页办理解释", "来源于工资发放办理链"));
        list.add(explanationItem("社保税务比对", defaultNumber(socialBaseCompareSummary == null ? null : socialBaseCompareSummary.getAbnormalCount())
            + defaultNumber(taxCompareSummary == null ? null : taxCompareSummary.getAbnormalCount()), "0",
            "共享底数按 530.1 办理链组织，优先解释社保基数和个税异常。", "socialBaseCompare", "socialBaseCompare",
            buildSourceQuery(regionCode, null, statMonth, "compareResult", "2"), "首页办理解释", "来源于社保税务比对链"));
        list.add(explanationItem("扩面减损", defaultNumber(uninsuredListSummary == null ? null : uninsuredListSummary.getPendingCount()), "0",
            "漏保清单继续沿整改链承接，不混入治理链排序。", "uninsuredList", "uninsuredList",
            buildSourceQuery(regionCode, null, statMonth, "disposalStatus", "0"), "首页办理解释", "来源于扩面减损整改链"));
        list.add(explanationItem("预警闭环", defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()), "0",
            "首页摘要以预警闭环结果为主，不再让静态文案兜底业务语义。", "warning", "warning",
            buildSourceQuery(regionCode, null, statMonth, "warnStatus", "0"), "首页办理解释", "来源于预警闭环链"));
        return list;
    }

    private List<Map<String, Object>> buildYgbWorkflowSteps(List<Map<String, Object>> queueSections,
        List<Map<String, Object>> explanationItems)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map<String, Object> section : queueSections)
        {
            list.add(workflowStep(String.valueOf(section.get("title")), String.valueOf(section.get("desc"))));
            if (list.size() >= 4)
            {
                return list;
            }
        }
        for (Map<String, Object> item : explanationItems)
        {
            list.add(workflowStep(String.valueOf(item.get("dimensionName")), String.valueOf(item.get("summary"))));
            if (list.size() >= 4)
            {
                break;
            }
        }
        return list;
    }

    private List<Map<String, Object>> buildYgbFocusPanels(List<Map<String, Object>> explanationItems)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map<String, Object> item : explanationItems)
        {
            list.add(focusPanel(String.valueOf(item.get("dimensionName")), String.valueOf(item.get("summary")),
                String.valueOf(item.get("recommendModule")), mapValue(item.get("defaultQuery")), String.valueOf(item.get("sourceLabel")),
                String.valueOf(item.get("sourceDescription"))));
            if (list.size() >= 3)
            {
                break;
            }
        }
        return list;
    }

    private String buildYgbHomeSummary(YgbWorkbenchDashboard dashboard)
    {
        String enhancedSummary = buildEnhancedYgbHomeSummary(dashboard);
        if (StringUtils.isNotEmpty(enhancedSummary))
        {
            return enhancedSummary;
        }
        YgbContractSummary contractSummary = dashboard.getContractSummary();
        YgbSalaryDetailSummary salaryDetailSummary = dashboard.getSalaryDetailSummary();
        YgbSocialPaymentSummary socialPaymentSummary = dashboard.getSocialPaymentSummary();
        YgbTaxCompareSummary taxCompareSummary = dashboard.getTaxCompareSummary();
        return "当前重点：合同待备案 " + defaultNumber(contractSummary == null ? null : contractSummary.getPendingCount())
            + " 份，工资失败 " + defaultNumber(salaryDetailSummary == null ? null : salaryDetailSummary.getFailedCount())
            + " 条，社保欠费 " + defaultNumber(socialPaymentSummary == null ? null : socialPaymentSummary.getOverdueCount())
            + " 条，个税异常 " + defaultNumber(taxCompareSummary == null ? null : taxCompareSummary.getAbnormalCount()) + " 条。";
    }

    private List<Map<String, Object>> buildAzbSummaryCards(YgbAzbCockpitDashboard dashboard, String roleView)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        YgbAqInsuranceSummary aqInsuranceSummary = dashboard.getAqInsuranceSummary();
        YgbPreventionFundSummary preventionFundSummary = dashboard.getPreventionFundSummary();
        YgbCreditScoreSummary creditScoreSummary = dashboard.getCreditScoreSummary();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbHeightWorkReportSummary heightWorkReportSummary = dashboard.getHeightWorkReportSummary();
        YgbDeviceSummary deviceSummary = dashboard.getDeviceSummary();
        YgbStatReportSummary statReportSummary = dashboard.getStatReportSummary();

        if ("bank".equals(roleView))
        {
            list.add(summaryCard("creditTotal", "信用对象", defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getTotalCount()), "条",
                "当前区域已沉淀信用评分企业数量。", ""));
            list.add(summaryCard("redCount", "红码企业", defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getRedCount()), "条",
                "需要重点复核授信和协同对象。", "azb-summary-card--danger"));
            list.add(summaryCard("avgScore", "平均分", formatDecimalText(creditScoreSummary == null ? null : creditScoreSummary.getAverageScore()), "条",
                "用来观察区域信用面是否持续走弱。", ""));
            list.add(summaryCard("reportCount", "区域报表", defaultNumber(statReportSummary == null ? null : statReportSummary.getGeneratedCount()), "条",
                "当前月份可直接协同复核的统计成果。", "azb-summary-card--warning"));
            return list;
        }

        if ("insurer".equals(roleView))
        {
            list.add(summaryCard("coverageRate", "安责险覆盖率", formatDecimalText(aqInsuranceSummary == null ? null : aqInsuranceSummary.getCoverageRate()), "%",
                "围绕投保覆盖先判断保单池是否存在明显缺口。", "azb-summary-card--success"));
            list.add(summaryCard("riskCount", "到期风险", defaultNumber(aqInsuranceSummary == null ? null : aqInsuranceSummary.getRiskCount()), "条",
                "即将到期和已过期保单合计。", "azb-summary-card--danger"));
            list.add(summaryCard("fundBalance", "预防费余额", formatDecimalText(preventionFundSummary == null ? null : preventionFundSummary.getRemainingAmountTotal()), "条",
                "快速判断资金池是否还能支撑现场治理动作。", "azb-summary-card--warning"));
            list.add(summaryCard("redCredit", "红码企业", defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getRedCount()), "条",
                "便于筛出需要重点保险服务的风险企业。", ""));
            return list;
        }

        list.add(summaryCard("warningPending", "待处置预警", defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()), "条",
            "需要继续压实闭环的风险工单。", "azb-summary-card--warning"));
        list.add(summaryCard("deviceOnline", "在线设备", defaultNumber(deviceSummary == null ? null : deviceSummary.getOnlineCount()), "条",
            "现场在线设备是应急侧第一道感知入口。", ""));
        list.add(summaryCard("heightActive", "高处作业进行中", defaultNumber(heightWorkReportSummary == null ? null : heightWorkReportSummary.getActiveCount()), "条",
            "用于锁定当前仍在现场进行的高风险作业。", "azb-summary-card--danger"));
        list.add(summaryCard("coverageRate", "安责险覆盖率", formatDecimalText(aqInsuranceSummary == null ? null : aqInsuranceSummary.getCoverageRate()), "%",
            "帮助判断现场企业投保覆盖面是否到位。", "azb-summary-card--success"));
        return list;
    }

    private List<Map<String, Object>> buildAzbFocusQueues(YgbAzbCockpitDashboard dashboard, String roleView)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbDeviceSummary deviceSummary = dashboard.getDeviceSummary();
        YgbHeightWorkReportSummary heightWorkReportSummary = dashboard.getHeightWorkReportSummary();
        YgbInjuryEventSummary injuryEventSummary = dashboard.getInjuryEventSummary();
        YgbAqInsuranceSummary aqInsuranceSummary = dashboard.getAqInsuranceSummary();
        YgbPreventionFundSummary preventionFundSummary = dashboard.getPreventionFundSummary();
        YgbCreditScoreSummary creditScoreSummary = dashboard.getCreditScoreSummary();
        YgbStatReportSummary statReportSummary = dashboard.getStatReportSummary();

        if ("bank".equals(roleView))
        {
            list.add(focusQueue("creditScore", "红码与尾部对象", countCreditTailRisk(creditScoreSummary), "条",
                "先锁定红码和 D 级企业，再结合报表复核变化趋势。", "/azb/creditScore", "查看信用画像",
                buildSourceQuery(null, null, null, "colorCode", "RED"), "驾驶舱焦点/ 红码与低分企业", "来自驾驶舱信用分层焦点"));
            list.add(focusQueue("statReport", "区域报表复核", defaultNumber(statReportSummary == null ? null : statReportSummary.getGeneratedCount()), "条",
                "当前月份已生成报表，可直接查看区域变化。", "/azb-report/statReport/warning", "查看区域报表",
                buildSourceQuery(null, null, null, "reportStatus", "1"), "驾驶舱焦点 / 区域报表复核", "来自驾驶舱区域报表焦点"));
            list.add(focusQueue("aqInsurance", "安责险风险对象", defaultNumber(aqInsuranceSummary == null ? null : aqInsuranceSummary.getRiskCount()), "条",
                "协同复核时同步看保单到期与失效对象，补足风险证据。", "/azb/aqInsurance", "查看安责险风险",
                buildSourceQuery(null, null, null, "policyStatus", "2"), "驾驶舱焦点/ 安责险风险对象", "来自驾驶舱协同复核焦点"));
            list.add(focusQueue("warning", "待处置预警", defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()), "条",
                "高等级预警作为银行协同复核的风险证据，不直接承接治理动作。", "/azb/warning", "查看预警证据",
                buildSourceQuery(null, null, null, "warnStatus", "0"), "驾驶舱焦点/ 待处置预警", "来自驾驶舱风险证据焦点"));
            list.add(focusQueue("device", "设备异常证据", countDeviceRisk(deviceSummary), "条",
                "离线、故障和未授权设备用于补足现场风险证据。", "/azb/device", "查看设备证据",
                buildSourceQuery(null, null, null, "authStatus", "0"), "驾驶舱焦点/ 设备异常证据", "来自驾驶舱风险证据焦点"));
            list.add(focusQueue("heightWorkReport", "进行中高处作业", defaultNumber(heightWorkReportSummary == null ? null : heightWorkReportSummary.getActiveCount()), "条",
                "进行中高处作业用于补足技术防范证据，不混入企业办理语义。", "/azb/heightWorkReport", "查看作业证据",
                buildSourceQuery(null, null, null, "reportStatus", "0"), "驾驶舱焦点/ 进行中高处作业", "来自驾驶舱风险证据焦点"));
            return list;
        }

        if ("insurer".equals(roleView))
        {
            list.add(focusQueue("aqInsurance", "保单到期与失效", defaultNumber(aqInsuranceSummary == null ? null : aqInsuranceSummary.getRiskCount()), "条",
                "优先消化到期风险，避免保单断档。", "/azb/aqInsurance", "进入投保监管",
                buildSourceQuery(null, null, null, "policyStatus", "2"), "驾驶舱焦点/ 保单到期与失效", "来自驾驶舱安责险覆盖焦点"));
            list.add(focusQueue("preventionFund", "资金协同异常", countFundCoordinationRisk(preventionFundSummary), "条",
                "余额偏低的资金池会直接影响治理动作落地。", "/azb/preventionFund", "查看资金池",
                buildSourceQuery(null, null, null, "fundStatus", "1"), "驾驶舱焦点/ 低余额资金池", "来自驾驶舱事故预防资金焦点"));
            list.add(focusQueue("creditScore", "红码企业", defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getRedCount()), "条",
                "需要结合评分判断保险协同重点。", "/azb/creditScore", "查看信用评分",
                buildSourceQuery(null, null, null, "colorCode", "RED"), "驾驶舱焦点/ 红码企业", "来自驾驶舱信用分层焦点"));
            list.add(focusQueue("statReport", "已生成报表", defaultNumber(statReportSummary == null ? null : statReportSummary.getGeneratedCount()), "条",
                "结合区域报表做月度复盘。", "/azb-report/statReport/warning", "查看报表",
                buildSourceQuery(null, null, null, "reportStatus", "1"), "驾驶舱焦点/ 已生成报表", "来自驾驶舱区域治理焦点"));
            list.add(focusQueue("warning", "待处置预警", defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()), "条",
                "承保协同时同步回看隐患处置时效和压降压力。", "/azb/warning", "查看预警处置",
                buildSourceQuery(null, null, null, "warnStatus", "0"), "驾驶舱焦点/ 待处置预警", "来自驾驶舱风险证据焦点"));
            list.add(focusQueue("device", "设备异常", countDeviceRisk(deviceSummary), "条",
                "设备异常用于识别现场感知链路是否完整。", "/azb/device", "查看设备风险",
                buildSourceQuery(null, null, null, "authStatus", "0"), "驾驶舱焦点/ 设备异常", "来自驾驶舱风险证据焦点"));
            list.add(focusQueue("heightWorkReport", "进行中高处作业", defaultNumber(heightWorkReportSummary == null ? null : heightWorkReportSummary.getActiveCount()), "条",
                "进行中高处作业补足技术防范与证书合规证据链。", "/azb/heightWorkReport", "查看高处作业",
                buildSourceQuery(null, null, null, "reportStatus", "0"), "驾驶舱焦点/ 进行中高处作业", "来自驾驶舱技术防范焦点"));
            return list;
        }

        list.add(focusQueue("warning", "红警与升级处置", defaultNumber(warningSummary == null ? null : warningSummary.getRedCount())
            + defaultNumber(warningSummary == null ? null : warningSummary.getUpgradedCount()), "条",
            "先处理高等级工单，防止风险在现场继续扩大。", "/azb/warning", "进入预警中心",
            null, "驾驶舱焦点/ 红警与升级处置", "来自驾驶舱预警处置焦点"));
        list.add(focusQueue("device", "离线/故障设备", defaultNumber(deviceSummary == null ? null : deviceSummary.getLockedOrFaultCount())
            + defaultNumber(deviceSummary == null ? null : deviceSummary.getUnauthorizedCount()), "条",
            "设备离线会直接削弱现场感知能力。", "/azb/device", "查看设备台账",
            buildSourceQuery(null, null, null, "authStatus", "0"), "驾驶舱焦点/ 离线故障设备", "来自驾驶舱设备安全焦点"));
        list.add(focusQueue("heightWorkReport", "进行中高处作业", defaultNumber(heightWorkReportSummary == null ? null : heightWorkReportSummary.getActiveCount()), "条",
            "需要持续盯防仍在现场进行的高风险作业。", "/azb/heightWorkReport", "查看高处作业",
            buildSourceQuery(null, null, null, "reportStatus", "0"), "驾驶舱焦点/ 进行中高处作业", "来自驾驶舱技术防范焦点"));
        list.add(focusQueue("injuryEvent", "超期工伤事件", defaultNumber(injuryEventSummary == null ? null : injuryEventSummary.getOverdueCount()), "条",
            "超期事件需要回到事故处理链路继续压降。", "/azb/injuryEvent", "查看工伤事件",
            null, "驾驶舱焦点/ 超期工伤事件", "来自驾驶舱工伤处置焦点"));
        return list;
    }

    private List<Map<String, Object>> buildAzbWorkflowSteps(String roleView)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        if ("bank".equals(roleView))
        {
            list.add(workflowStep("先看信用分层", "优先筛出红码、D 级和平均分持续走低的对象。"));
            list.add(workflowStep("再看区域报表", "用区域报表验证重点企业变化是否具备持续性。"));
            list.add(workflowStep("最后做协同复核", "回到重点企业和报表明细做授信协同判断。"));
            return list;
        }
        if ("insurer".equals(roleView))
        {
            list.add(workflowStep("先看投保覆盖", "先判断保单池是否存在到期断档和覆盖不足。"));
            list.add(workflowStep("再看事故预防资金", "对低余额和缺少凭证的资金池优先复核。"));
            list.add(workflowStep("最后联动信用与报表", "结合信用评分和区域报表判断本月重点服务企业。"));
            return list;
        }
        list.add(workflowStep("先锁定重点区域和企业", "通过总览指标和焦点队列判断当前风险压力最大的位置。"));
        list.add(workflowStep("再盯设备与高危作业", "优先处理离线设备、进行中高处作业和现场异常点位。"));
        list.add(workflowStep("最后回到预警与事故闭环", "压实红警、升级工单和超期工伤事件的处置时效。"));
        return list;
    }

    private List<Map<String, Object>> buildAzbQuickActions(String roleView, String regionCode, String statDate, String statMonth)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        if ("bank".equals(roleView))
        {
            return buildAzbBankQuickActions(regionCode, statDate, statMonth);
        }
        if ("insurer".equals(roleView))
        {
            return buildAzbInsurerQuickActions(regionCode, statDate, statMonth);
        }
        if ("bank".equals(roleView))
        {
            list.add(quickAction("creditScore", "信用评分", "只读看板", "查看红黄绿码、等级和区域信用面。",
                "/azb/creditScore", buildSourceQuery(regionCode, statDate, statMonth, "colorCode", "RED"),
                "首页重点对象", "来自首页信用重点对象聚合"));
            list.add(quickAction("statReport", "统计报表", "区域复核", "从月报快速进入区域和企业变化明细。",
                "/azb-report/statReport/warning", buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "1"),
                "首页区域治理", "来自首页区域治理聚合"));
            return list;
        }
        if ("insurer".equals(roleView))
        {
            list.add(quickAction("aqInsurance", "安责险监管", "保单池", "查看覆盖率、到期风险和保单回写状态。",
                "/azb/aqInsurance", buildSourceQuery(regionCode, statDate, statMonth, "policyStatus", "2"),
                "首页重点对象", "来自首页安责险覆盖聚合"));
            list.add(quickAction("preventionFund", "事故预防资金", "资金池", "跟踪余额、使用状态和凭证留痕。",
                "/azb/preventionFund", buildSourceQuery(regionCode, statDate, statMonth, "fundStatus", "1"),
                "首页重点对象", "来自首页事故预防资金聚合"));
            list.add(quickAction("creditScore", "信用评分", "风险画像", "查看红码企业和平均分走势。",
                "/azb/creditScore", buildSourceQuery(regionCode, statDate, statMonth, "colorCode", "RED"),
                "首页重点对象", "来自首页信用画像聚合"));
            list.add(quickAction("statReport", "统计报表", "月度复盘", "汇总区域投保和治理结果。",
                "/azb-report/statReport/warning", buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "1"),
                "首页区域治理", "来自首页区域治理聚合"));
            return list;
        }
        list.add(quickAction("warning", "预警中心", "闭环处置", "直接进入风险工单，继续做处置和升级。",
            "/azb/warning", buildSourceQuery(regionCode, statDate, statMonth, "warnStatus", "0"),
            "首页重点对象", "来自首页预警处置聚合"));
        list.add(quickAction("device", "设备管理", "在线治理", "查看离线、故障、未授权设备。",
            "/azb/device", buildSourceQuery(regionCode, statDate, statMonth, "authStatus", "0"),
            "首页设备安全", "来自首页设备安全聚合"));
        list.add(quickAction("heightWorkReport", "高处作业", "现场作业", "盯防进行中作业和证书异常人员。",
            "/azb/heightWorkReport", buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "0"),
            "首页技术防范", "来自首页技术防范聚合"));
        list.add(quickAction("aqInsurance", "安责险监管", "覆盖复核", "交叉判断现场企业投保覆盖情况。",
            "/azb/aqInsurance", buildSourceQuery(regionCode, statDate, statMonth, "policyStatus", "2"),
            "首页安责险覆盖", "来自首页安责险覆盖聚合"));
        list.add(quickAction("creditScore", "信用评分", "风险分层", "查看红码、D 级和尾部对象，判断当前重点压降项目。",
            "/azb/creditScore", buildSourceQuery(regionCode, statDate, statMonth, "colorCode", "RED"),
            "首页信用分层", "来自首页信用分层聚合"));
        list.add(quickAction("statReport", "统计报表", "区域治理", "从首页直接进入治理月报和区域态势复核。",
            "/azb-report/statReport/warning", buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "1"),
            "首页区域治理", "来自首页区域治理聚合"));
        return list;
    }

    private List<Map<String, Object>> buildAzbHintTags(YgbAzbCockpitDashboard dashboard, String roleView)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbDeviceSummary deviceSummary = dashboard.getDeviceSummary();
        YgbHeightWorkReportSummary heightWorkReportSummary = dashboard.getHeightWorkReportSummary();
        YgbAqInsuranceSummary aqInsuranceSummary = dashboard.getAqInsuranceSummary();
        YgbPreventionFundSummary preventionFundSummary = dashboard.getPreventionFundSummary();
        YgbCreditScoreSummary creditScoreSummary = dashboard.getCreditScoreSummary();
        YgbStatReportSummary statReportSummary = dashboard.getStatReportSummary();

        if (defaultNumber(warningSummary == null ? null : warningSummary.getRedCount()) > 0)
        {
            list.add(hintTag("存在 " + defaultNumber(warningSummary.getRedCount()) + " 条红警，优先压实高等级工单。", "danger"));
        }
        if (defaultNumber(deviceSummary == null ? null : deviceSummary.getUnauthorizedCount()) > 0)
        {
            list.add(hintTag("仍有 " + defaultNumber(deviceSummary.getUnauthorizedCount()) + " 台未授权设备，现场感知链路不完整。", "warning"));
        }
        if (defaultNumber(heightWorkReportSummary == null ? null : heightWorkReportSummary.getInvalidWorkerCount()) > 0)
        {
            list.add(hintTag("高处作业存在 " + defaultNumber(heightWorkReportSummary.getInvalidWorkerCount()) + " 名证书异常人员。", "warning"));
        }
        if (defaultNumber(aqInsuranceSummary == null ? null : aqInsuranceSummary.getRiskCount()) > 0 && !"bank".equals(roleView))
        {
            list.add(hintTag("保单池中有 " + defaultNumber(aqInsuranceSummary.getRiskCount()) + " 单到期风险。", "info"));
        }
        if (defaultNumber(preventionFundSummary == null ? null : preventionFundSummary.getLowBalanceCount()) > 0 && "insurer".equals(roleView))
        {
            list.add(hintTag("事故预防资金存在 " + defaultNumber(preventionFundSummary.getLowBalanceCount()) + " 条低余额记录。", "warning"));
        }
        if (defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getRedCount()) > 0)
        {
            list.add(hintTag("信用画像中有 " + defaultNumber(creditScoreSummary.getRedCount()) + " 家红码企业。", "danger"));
        }
        if (defaultNumber(statReportSummary == null ? null : statReportSummary.getDraftCount()) > 0)
        {
            list.add(hintTag("当前月份仍有 " + defaultNumber(statReportSummary.getDraftCount()) + " 份草稿报表未形成正式结果。", "info"));
        }
        return list;
    }

    private List<Map<String, Object>> buildAzbQueueSections(YgbAzbCockpitDashboard dashboard, String roleView, String regionCode,
        String statDate, String statMonth)
    {
        List<Map<String, Object>> sections = new ArrayList<>();
        if ("bank".equals(roleView))
        {
            return buildAzbBankQueueSections(dashboard, regionCode, statDate, statMonth);
        }
        if ("insurer".equals(roleView))
        {
            return buildAzbInsurerQueueSections(dashboard, regionCode, statDate, statMonth);
        }
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbDeviceSummary deviceSummary = dashboard.getDeviceSummary();
        YgbHeightWorkReportSummary heightWorkReportSummary = dashboard.getHeightWorkReportSummary();
        YgbAqInsuranceSummary aqInsuranceSummary = dashboard.getAqInsuranceSummary();
        YgbCreditScoreSummary creditScoreSummary = dashboard.getCreditScoreSummary();
        YgbStatReportSummary statReportSummary = dashboard.getStatReportSummary();

        List<Map<String, Object>> objectItems = new ArrayList<>();
        objectItems.add(queueItem("warning", "预警待处置对象", "重点对象聚合", defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()),
            "条", "同一对象可从首页进入 warning 常规列表态。", "继续处置", "warning", "azb",
            buildSourceQuery(regionCode, statDate, statMonth, "warnStatus", "0"), "首页重点对象", "来自首页重点对象聚合"));
        objectItems.add(queueItem("device", "设备异常对象", "设备安全聚合", defaultNumber(deviceSummary == null ? null : deviceSummary.getLockedOrFaultCount())
            + defaultNumber(deviceSummary == null ? null : deviceSummary.getUnauthorizedCount()), "条", "支持对象级下钻到 device 常规列表态。", "继续核查", "device", "azb",
            buildSourceQuery(regionCode, statDate, statMonth, "authStatus", "0"), "首页重点对象", "来自首页设备安全聚合"));
        objectItems.add(queueItem("aqInsurance", "安责险风险对象", "安责险覆盖聚合", defaultNumber(aqInsuranceSummary == null ? null : aqInsuranceSummary.getRiskCount()),
            "条", "支持对象级下钻到 aqInsurance 常规列表态。", "继续复核", "aqInsurance", "azb",
            buildSourceQuery(regionCode, statDate, statMonth, "policyStatus", "2"), "首页重点对象", "来自首页安责险覆盖聚合"));
        sections.add(queueSection("focusObject", "重点对象队列", "输出风险对象摘要、对象类型、风险标签和下钻目标。", objectItems));

        List<Map<String, Object>> governanceItems = new ArrayList<>();
        governanceItems.add(queueItem("heightWork", "高处作业进行中", "技术防范聚合", defaultNumber(heightWorkReportSummary == null ? null : heightWorkReportSummary.getActiveCount()),
            "条", "从首页直接进入高处作业常规列表态。", "继续盯防", "heightWorkReport", "azb",
            buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "0"), "首页技术防范", "来自首页技术防范聚合"));
        governanceItems.add(queueItem("creditScore", "红码与尾部对象", "信用分层聚合", defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getRedCount())
            + defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getDCount()), "条", "支持从首页进入 creditScore 常规列表态。", "继续分层", "creditScore", "azb",
            buildSourceQuery(regionCode, statDate, statMonth, "colorCode", "RED"), "首页信用分层", "来自首页信用分层聚合"));
        governanceItems.add(queueItem("statReport", "区域治理月报", "区域态势聚合", defaultNumber(statReportSummary == null ? null : statReportSummary.getGeneratedCount()),
            "条", "从首页直接进入 statReport 常规列表态。", "继续复核", "statReport", "azb",
            buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "1"), "首页区域治理", "来自首页区域治理聚合"));
        sections.add(queueSection("governance", "治理维度队列", "同一对象支持下钻 warning、device、aqInsurance、statReport、creditScore。", governanceItems));

        if ("bank".equals(roleView))
        {
            sections.add(queueSection("bankOnly", "协同复核队列", "保留只读协同口径，不混入企业办理语义。", governanceItems));
        }
        return sections;
    }

    private List<Map<String, Object>> buildAzbHomeExplanation(YgbAzbCockpitDashboard dashboard, String roleView, String regionCode,
        String statMonth)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        if ("insurer".equals(roleView))
        {
            return buildAzbInsurerHomeExplanation(dashboard, regionCode, statMonth);
        }
        YgbAqInsuranceSummary aqInsuranceSummary = dashboard.getAqInsuranceSummary();
        YgbDeviceSummary deviceSummary = dashboard.getDeviceSummary();
        YgbHeightWorkReportSummary heightWorkReportSummary = dashboard.getHeightWorkReportSummary();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbCreditScoreSummary creditScoreSummary = dashboard.getCreditScoreSummary();
        YgbStatReportSummary statReportSummary = dashboard.getStatReportSummary();

        if ("bank".equals(roleView))
        {
            list.add(explanationItem("信用分层",
                defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getRedCount())
                    + defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getDCount()),
                "0",
                "银行只读协同优先看红码、D 级和尾部对象，再决定是否继续回到区域月报复核变化趋势。",
                "creditScore",
                "creditScore",
                buildSourceQuery(regionCode, null, statMonth, "colorCode", "RED"),
                "首页协同复核解释",
                "来源于信用分层聚合"));
            list.add(explanationItem("区域治理成效", defaultNumber(statReportSummary == null ? null : statReportSummary.getGeneratedCount()), ">=1",
                "银行协同复核按区域月报和生成结果组织，不混入企业办理优先描述。",
                "statReport",
                "statReport",
                buildSourceQuery(regionCode, null, statMonth, "reportStatus", "1"),
                "首页协同复核解释",
                "来源于区域治理月报"));
            list.add(explanationItem("风险复核顺序", defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getRedCount()), "0",
                "同一企业先看信用尾部对象，再看月报态势，保持只读协同复核顺序。",
                "creditScore",
                "creditScore",
                buildSourceQuery(regionCode, null, statMonth, "colorCode", "RED"),
                "首页协同复核解释",
                "来源于银行只读协同口径"));
            return list;
        }

        list.add(explanationItem("安责险覆盖", formatDecimalText(aqInsuranceSummary == null ? null : aqInsuranceSummary.getCoverageRate()), "100",
            "6.1 口径优先解释安责险覆盖与到期风险，不出现企业办理链优先描述。", "aqInsurance", "aqInsurance",
            buildSourceQuery(regionCode, null, statMonth, "policyStatus", "2"), "首页治理解释", "来源于安责险覆盖聚合"));
        list.add(explanationItem("设备安全", defaultNumber(deviceSummary == null ? null : deviceSummary.getLockedOrFaultCount())
            + defaultNumber(deviceSummary == null ? null : deviceSummary.getUnauthorizedCount()), "0",
            "先看离线、故障与未授权设备，再看治理链闭环动作。", "device", "device",
            buildSourceQuery(regionCode, null, statMonth, "authStatus", "0"), "首页治理解释", "来源于设备安全聚合"));
        list.add(explanationItem("技术防范", defaultNumber(heightWorkReportSummary == null ? null : heightWorkReportSummary.getActiveCount()), "0",
            "高处作业解释沿技术防范链组织，不混用企业办理摘要。", "heightWorkReport", "heightWorkReport",
            buildSourceQuery(regionCode, null, statMonth, "reportStatus", "0"), "首页治理解释", "来源于技术防范聚合"));
        list.add(explanationItem("预警处置", defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()), "0",
            "首页摘要突出预警处置与隐患整改时效。", "warning", "warning",
            buildSourceQuery(regionCode, null, statMonth, "warnStatus", "0"), "首页治理解释", "来源于预警闭环聚合"));
        list.add(explanationItem("区域治理成效", defaultNumber(statReportSummary == null ? null : statReportSummary.getGeneratedCount()), ">=1",
            "治理月报和区域态势按 6.1 监管解释链固定排序。", "statReport", "statReport",
            buildSourceQuery(regionCode, null, statMonth, "reportStatus", "1"), "首页治理解释", "来源于区域治理月报"));
        if (!"bank".equals(roleView))
        {
            list.add(explanationItem("信用分层", defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getRedCount()), "0",
                "尾部对象和风险等级继续落到治理链与只读协同模块。", "creditScore", "creditScore",
                buildSourceQuery(regionCode, null, statMonth, "colorCode", "RED"), "首页治理解释", "来源于信用分层聚合"));
        }
        return list;
    }

    private List<Map<String, Object>> buildAzbFocusPanels(YgbAzbCockpitDashboard dashboard, String roleView, String regionCode,
        String statMonth)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map<String, Object> item : buildAzbHomeExplanation(dashboard, roleView, regionCode, statMonth))
        {
            list.add(focusPanel(String.valueOf(item.get("dimensionName")), String.valueOf(item.get("summary")),
                String.valueOf(item.get("recommendModule")), mapValue(item.get("defaultQuery")), String.valueOf(item.get("sourceLabel")),
                String.valueOf(item.get("sourceDescription"))));
            if (list.size() >= 3)
            {
                break;
            }
        }
        return list;
    }

    private String buildAzbHomeSummary(YgbAzbCockpitDashboard dashboard, String roleView)
    {
        String roleSummary = resolveAzbRoleHomeSummary(dashboard, roleView);
        if (StringUtils.isNotEmpty(roleSummary))
        {
            return roleSummary;
        }
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbDeviceSummary deviceSummary = dashboard.getDeviceSummary();
        YgbAqInsuranceSummary aqInsuranceSummary = dashboard.getAqInsuranceSummary();
        YgbCreditScoreSummary creditScoreSummary = dashboard.getCreditScoreSummary();
        if ("bank".equals(roleView))
        {
            return "当前重点：红码企业" + defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getRedCount())
                + " 家，区域月报 " + defaultNumber(dashboard.getStatReportSummary() == null ? null : dashboard.getStatReportSummary().getGeneratedCount()) + " 份。";
        }
        return "当前重点：待处置预警 " + defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount())
            + " 条，设备异常 " + (defaultNumber(deviceSummary == null ? null : deviceSummary.getLockedOrFaultCount())
            + defaultNumber(deviceSummary == null ? null : deviceSummary.getUnauthorizedCount()))
            + " 台，安责险风险" + defaultNumber(aqInsuranceSummary == null ? null : aqInsuranceSummary.getRiskCount()) + " 单。";
    }

    private List<Map<String, Object>> buildYgbSupplementQuickActions(String regionCode, String statDate, String statMonth)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(quickAction("statReport", "统计报表", "月报归档", "直接进入办理月报与归档结果列表，保持首页月份来源条件。",
            modulePath("ygb", "statReport"),
            buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "0"),
            "首页月报归档", "来自首页月份维度聚合"));
        return list;
    }

    private void appendYgbSupplementQueueSections(List<Map<String, Object>> sections, YgbWorkbenchDashboard dashboard,
        String regionCode, String statDate, String statMonth)
    {
        if (sections == null || dashboard == null)
        {
            return;
        }
        YgbStatReportSummary statReportSummary = dashboard.getStatReportSummary();
        YgbUninsuredListSummary uninsuredListSummary = dashboard.getUninsuredListSummary();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();

        Map<String, Object> enterpriseSection = findQueueSection(sections, "enterprise");
        appendQueueItemIfMissing(enterpriseSection, "enterpriseReportDraft",
            queueItem("enterpriseReportDraft", "月报待归档", "企业维度联动来源",
                defaultNumber(statReportSummary == null ? null : statReportSummary.getDraftCount()), "条",
                "企业维度需要同步带出办理月报状态，避免首页只剩待办和异常。", "继续归档", "statReport", "ygb",
                buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "0"),
                "首页企业办理", "来自首页企业维度聚合"));

        Map<String, Object> monthSection = findQueueSection(sections, "month");
        appendQueueItemIfMissing(monthSection, "monthUninsuredPending",
            queueItem("monthUninsuredPending", "扩面减损整改", "月份扩面摘要",
                defaultNumber(uninsuredListSummary == null ? null : uninsuredListSummary.getPendingCount()), "条",
                "按统计月输出扩面减损整改对象，保持首页到整改链的来源透传。", "继续整改", "uninsuredList", "ygb",
                buildSourceQuery(regionCode, statDate, statMonth, "disposalStatus", "0"),
                "首页月份聚合", "来自首页月份维度聚合"));
        appendQueueItemIfMissing(monthSection, "monthWarningPending",
            queueItem("monthWarningPending", "预警待处。", "月份预警摘要",
                defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()), "条",
                "按统计月输出预警待处置量，首页不再自行猜测月份风险优先项。", "继续处置", "warning", "ygb",
                buildSourceQuery(regionCode, statDate, statMonth, "warnStatus", "0"),
                "首页月份聚合", "来自首页月份维度聚合"));
    }

    private String buildEnhancedYgbHomeSummary(YgbWorkbenchDashboard dashboard)
    {
        if (dashboard == null)
        {
            return "";
        }
        YgbContractSummary contractSummary = dashboard.getContractSummary();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbStatReportSummary statReportSummary = dashboard.getStatReportSummary();
        YgbSalaryDetailSummary salaryDetailSummary = dashboard.getSalaryDetailSummary();
        YgbSocialPaymentSummary socialPaymentSummary = dashboard.getSocialPaymentSummary();
        YgbTaxCompareSummary taxCompareSummary = dashboard.getTaxCompareSummary();
        YgbUninsuredListSummary uninsuredListSummary = dashboard.getUninsuredListSummary();

        return "企业维度：合同待备案 " + defaultNumber(contractSummary == null ? null : contractSummary.getPendingCount())
            + " 份、待处置预警 " + defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount())
            + " 条、月报待归档 " + defaultNumber(statReportSummary == null ? null : statReportSummary.getDraftCount())
            + " 份；月份维度：工资失败" + defaultNumber(salaryDetailSummary == null ? null : salaryDetailSummary.getFailedCount())
            + " 条、社保欠费" + defaultNumber(socialPaymentSummary == null ? null : socialPaymentSummary.getOverdueCount())
            + " 条、个税异常" + defaultNumber(taxCompareSummary == null ? null : taxCompareSummary.getAbnormalCount())
            + " 条、扩面减损" + defaultNumber(uninsuredListSummary == null ? null : uninsuredListSummary.getPendingCount())
            + " 条；优先级维度：预警闭环和月报归档继续按接口队列顺序承接。";
    }

    private List<Map<String, Object>> buildAzbBankQuickActions(String regionCode, String statDate, String statMonth)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(quickAction("creditScore", "信用评分", "协同复核", "优先查看红码、D 级和尾部对象，保持银行协同只读口径。",
            modulePath("azb", "creditScore"),
            buildSourceQuery(regionCode, statDate, statMonth, "colorCode", "RED"),
            "首页协同复核", "来自首页协同复核聚合"));
        list.add(quickAction("statReport", "统计报表", "区域复核", "直接进入区域态势与治理月报常规列表，保持首页来源条件。",
            modulePath("azb", "statReport"),
            buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "1"),
            "首页协同复核", "来自首页协同复核聚合"));
        list.add(quickAction("aqInsurance", "安责险监管", "风险证据", "查看保单到期与失效对象，作为协同复核的证据入口。",
            modulePath("azb", "aqInsurance"),
            buildSourceQuery(regionCode, statDate, statMonth, "policyStatus", "2"),
            "首页安责险覆盖", "来自首页风险证据聚合"));
        list.add(quickAction("warning", "预警中心", "风险证据", "直接进入待处置预警列表，承接首页高风险证据。",
            modulePath("azb", "warning"),
            buildSourceQuery(regionCode, statDate, statMonth, "warnStatus", "0"),
            "首页风险证据", "来自首页风险证据聚合"));
        list.add(quickAction("device", "设备管理", "技术防范", "查看离线、故障与未授权设备，补足银行视角证据链。",
            modulePath("azb", "device"),
            buildSourceQuery(regionCode, statDate, statMonth, "authStatus", "0"),
            "首页风险证据", "来自首页风险证据聚合"));
        list.add(quickAction("heightWorkReport", "高处作业", "技术防范", "查看进行中高处作业，保持首页证据入口可追溯。",
            modulePath("azb", "heightWorkReport"),
            buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "0"),
            "首页风险证据", "来自首页风险证据聚合"));
        return list;
    }

    private List<Map<String, Object>> buildAzbInsurerQuickActions(String regionCode, String statDate, String statMonth)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(quickAction("aqInsurance", "安责险监管", "覆盖复核", "查看覆盖率、到期风险和保单池状态。",
            modulePath("azb", "aqInsurance"),
            buildSourceQuery(regionCode, statDate, statMonth, "policyStatus", "2"),
            "首页安责险覆盖", "来自首页安责险覆盖聚合"));
        list.add(quickAction("preventionFund", "事故预防资金", "资金协同", "查看低余额、缺凭证和在用资金状态。",
            modulePath("azb", "preventionFund"),
            buildSourceQuery(regionCode, statDate, statMonth, "fundStatus", "1"),
            "首页资金协同", "来自首页资金协同聚合"));
        list.add(quickAction("creditScore", "信用评分", "风险分层", "直接查看红码和尾部对象，为承保协同提供分层证据。",
            modulePath("azb", "creditScore"),
            buildSourceQuery(regionCode, statDate, statMonth, "colorCode", "RED"),
            "首页风险分层", "来自首页风险分层聚合"));
        list.add(quickAction("statReport", "统计报表", "治理月报", "查看区域治理月报和压降结果，不混入办理链说明。",
            modulePath("azb", "statReport"),
            buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "1"),
            "首页治理月报", "来自首页治理月报聚合"));
        list.add(quickAction("warning", "预警中心", "风险证据", "进入待处置预警列表，承接首页隐患处置语境。",
            modulePath("azb", "warning"),
            buildSourceQuery(regionCode, statDate, statMonth, "warnStatus", "0"),
            "首页风险证据", "来自首页风险证据聚合"));
        list.add(quickAction("device", "设备管理", "风险证据", "查看离线、故障与未授权设备，补足承保证据链。",
            modulePath("azb", "device"),
            buildSourceQuery(regionCode, statDate, statMonth, "authStatus", "0"),
            "首页风险证据", "来自首页风险证据聚合"));
        list.add(quickAction("heightWorkReport", "高处作业", "技术防范", "查看进行中高处作业和证书异常对象。",
            modulePath("azb", "heightWorkReport"),
            buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "0"),
            "首页技术防范", "来自首页技术防范聚合"));
        return list;
    }

    private List<Map<String, Object>> buildAzbBankQueueSections(YgbAzbCockpitDashboard dashboard, String regionCode,
        String statDate, String statMonth)
    {
        List<Map<String, Object>> sections = new ArrayList<>();
        YgbCreditScoreSummary creditScoreSummary = dashboard.getCreditScoreSummary();
        YgbStatReportSummary statReportSummary = dashboard.getStatReportSummary();
        YgbAqInsuranceSummary aqInsuranceSummary = dashboard.getAqInsuranceSummary();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbDeviceSummary deviceSummary = dashboard.getDeviceSummary();
        YgbHeightWorkReportSummary heightWorkReportSummary = dashboard.getHeightWorkReportSummary();

        List<Map<String, Object>> collaborationItems = new ArrayList<>();
        collaborationItems.add(queueItem("creditTailRisk", "红码与尾部对象", "协同复核队列",
            countCreditTailRisk(creditScoreSummary), "条",
            "银行口径先看红码、D 级和尾部对象，再决定是否下钻治理证据。", "继续复核", "creditScore", "azb",
            buildSourceQuery(regionCode, statDate, statMonth, "colorCode", "RED"),
            "首页协同复核", "来自首页协同复核聚合"));
        collaborationItems.add(queueItem("regionalReport", "区域治理月报", "协同复核队列",
            defaultNumber(statReportSummary == null ? null : statReportSummary.getGeneratedCount()), "条",
            "银行口径通过治理月报确认区域变化，不混入企业办理语义。", "继续复核", "statReport", "azb",
            buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "1"),
            "首页协同复核", "来自首页协同复核聚合"));
        collaborationItems.add(queueItem("insuranceEvidence", "安责险风险对象", "协同复核队列",
            defaultNumber(aqInsuranceSummary == null ? null : aqInsuranceSummary.getRiskCount()), "条",
            "安责险风险对象作为银行协同复核的补充证据入口。", "查看证据", "aqInsurance", "azb",
            buildSourceQuery(regionCode, statDate, statMonth, "policyStatus", "2"),
            "首页协同复核", "来自首页协同复核聚合"));
        sections.add(queueSection("collaboration", "协同复核队列", "银行门户只输出协同复核与只读证据，不混入办理链动作。", collaborationItems));

        List<Map<String, Object>> evidenceItems = new ArrayList<>();
        evidenceItems.add(queueItem("warningEvidence", "待处置预警", "风险证据队列",
            defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()), "条",
            "高风险工单作为银行协同复核的证据来源。", "查看预警", "warning", "azb",
            buildSourceQuery(regionCode, statDate, statMonth, "warnStatus", "0"),
            "首页风险证据", "来自首页风险证据聚合"));
        evidenceItems.add(queueItem("deviceEvidence", "设备异常", "风险证据队列",
            countDeviceRisk(deviceSummary), "条",
            "离线、故障和未授权设备用于补足现场风险证据。", "查看设备", "device", "azb",
            buildSourceQuery(regionCode, statDate, statMonth, "authStatus", "0"),
            "首页风险证据", "来自首页风险证据聚合"));
        evidenceItems.add(queueItem("heightEvidence", "进行中高处作业", "风险证据队列",
            defaultNumber(heightWorkReportSummary == null ? null : heightWorkReportSummary.getActiveCount()), "条",
            "进行中高处作业维持银行视角的技术防范证据入口。", "查看作业", "heightWorkReport", "azb",
            buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "0"),
            "首页风险证据", "来自首页风险证据聚合"));
        sections.add(queueSection("evidence", "风险证据队列", "银行门户保留预警、设备和高处作业的只读证据链。", evidenceItems));
        return sections;
    }

    private List<Map<String, Object>> buildAzbInsurerQueueSections(YgbAzbCockpitDashboard dashboard, String regionCode,
        String statDate, String statMonth)
    {
        List<Map<String, Object>> sections = new ArrayList<>();
        YgbAqInsuranceSummary aqInsuranceSummary = dashboard.getAqInsuranceSummary();
        YgbPreventionFundSummary preventionFundSummary = dashboard.getPreventionFundSummary();
        YgbCreditScoreSummary creditScoreSummary = dashboard.getCreditScoreSummary();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbDeviceSummary deviceSummary = dashboard.getDeviceSummary();
        YgbHeightWorkReportSummary heightWorkReportSummary = dashboard.getHeightWorkReportSummary();
        YgbStatReportSummary statReportSummary = dashboard.getStatReportSummary();

        List<Map<String, Object>> coverageItems = new ArrayList<>();
        coverageItems.add(queueItem("policyRisk", "保单到期与失效", "安责险覆盖队列",
            defaultNumber(aqInsuranceSummary == null ? null : aqInsuranceSummary.getRiskCount()), "条",
            "承保视角先看覆盖率、到期风险和保单失效对象。", "继续复核", "aqInsurance", "azb",
            buildSourceQuery(regionCode, statDate, statMonth, "policyStatus", "2"),
            "首页安责险覆盖", "来自首页安责险覆盖聚合"));
        coverageItems.add(queueItem("fundCoordination", "事故预防资金", "资金协同队列",
            countFundCoordinationRisk(preventionFundSummary), "条",
            "低余额、缺凭证和在用资金异常需要继续承保协同。", "继续协同", "preventionFund", "azb",
            buildSourceQuery(regionCode, statDate, statMonth, "fundStatus", "1"),
            "首页资金协同", "来自首页资金协同聚合"));
        coverageItems.add(queueItem("creditLayering", "红码与尾部对象", "风险分层队列",
            countCreditTailRisk(creditScoreSummary), "条",
            "红码和尾部对象为承保复核提供分层优先级。", "继续分层", "creditScore", "azb",
            buildSourceQuery(regionCode, statDate, statMonth, "colorCode", "RED"),
            "首页风险分层", "来自首页风险分层聚合"));
        sections.add(queueSection("coverage", "安责险覆盖队列", "围绕安责险覆盖、资金协同和风险分层组织承保首页入口。", coverageItems));

        List<Map<String, Object>> governanceItems = new ArrayList<>();
        governanceItems.add(queueItem("warningDisposal", "待处置预警", "承保治理队列",
            defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()), "条",
            "预警待处置量是承保协同压降的第一优先项。", "查看预警", "warning", "azb",
            buildSourceQuery(regionCode, statDate, statMonth, "warnStatus", "0"),
            "首页风险证据", "来自首页风险证据聚合"));
        governanceItems.add(queueItem("deviceRisk", "设备异常", "承保治理队列",
            countDeviceRisk(deviceSummary), "条",
            "设备异常用于识别现场感知链路是否完整。", "查看设备", "device", "azb",
            buildSourceQuery(regionCode, statDate, statMonth, "authStatus", "0"),
            "首页风险证据", "来自首页风险证据聚合"));
        governanceItems.add(queueItem("heightActive", "进行中高处作业", "承保治理队列",
            defaultNumber(heightWorkReportSummary == null ? null : heightWorkReportSummary.getActiveCount()), "条",
            "进行中高处作业需要继续跟踪技术防范与证书合规。", "查看作业", "heightWorkReport", "azb",
            buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "0"),
            "首页技术防范", "来自首页技术防范聚合"));
        governanceItems.add(queueItem("regionalResult", "区域治理月报", "承保治理队列",
            defaultNumber(statReportSummary == null ? null : statReportSummary.getGeneratedCount()), "条",
            "区域治理月报用于复盘压降结果与治理态势。", "查看月报", "statReport", "azb",
            buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "1"),
            "首页治理月报", "来自首页治理月报聚合"));
        sections.add(queueSection("insurerGovernance", "承保治理队列", "承保门户只下钻治理链与只读协同模块，保持 6.1 监管语义。", governanceItems));
        return sections;
    }

    private List<Map<String, Object>> buildAzbInsurerHomeExplanation(YgbAzbCockpitDashboard dashboard, String regionCode,
        String statMonth)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        YgbAqInsuranceSummary aqInsuranceSummary = dashboard.getAqInsuranceSummary();
        YgbPreventionFundSummary preventionFundSummary = dashboard.getPreventionFundSummary();
        YgbDeviceSummary deviceSummary = dashboard.getDeviceSummary();
        YgbHeightWorkReportSummary heightWorkReportSummary = dashboard.getHeightWorkReportSummary();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbStatReportSummary statReportSummary = dashboard.getStatReportSummary();
        YgbCreditScoreSummary creditScoreSummary = dashboard.getCreditScoreSummary();

        list.add(explanationItem("安责险覆盖", formatDecimalText(aqInsuranceSummary == null ? null : aqInsuranceSummary.getCoverageRate()), "100",
            "6.1 监管解释优先看安责险覆盖、到期风险和失效对象，不使用企业办理口径兜底。", "aqInsurance", "aqInsurance",
            buildSourceQuery(regionCode, null, statMonth, "policyStatus", "2"),
            "首页安责险覆盖", "来源于安责险覆盖聚合"));
        list.add(explanationItem("事故预防资金", countFundCoordinationRisk(preventionFundSummary), "0",
            "优先解释低余额、缺凭证和在用资金异常，承保协同只调整解释顺序与推荐动作。", "preventionFund", "preventionFund",
            buildSourceQuery(regionCode, null, statMonth, "fundStatus", "1"),
            "首页资金协同", "来源于事故预防资金聚合"));
        list.add(explanationItem("设备安全", countDeviceRisk(deviceSummary), "0",
            "设备离线、故障和未授权对象先作为承保证据，再决定是否继续区域压降。", "device", "device",
            buildSourceQuery(regionCode, null, statMonth, "authStatus", "0"),
            "首页风险证据", "来源于设备安全聚合"));
        list.add(explanationItem("技术防范", defaultNumber(heightWorkReportSummary == null ? null : heightWorkReportSummary.getActiveCount()), "0",
            "高处作业解释保持技术防范语义，不混用企业办理摘要。", "heightWorkReport", "heightWorkReport",
            buildSourceQuery(regionCode, null, statMonth, "reportStatus", "0"),
            "首页技术防范", "来源于技术防范聚合"));
        list.add(explanationItem("预警处置", defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()), "0",
            "预警待处置量体现隐患整改和压降时效，首页只保留一个主下钻入口。", "warning", "warning",
            buildSourceQuery(regionCode, null, statMonth, "warnStatus", "0"),
            "首页风险证据", "来源于预警处置聚合"));
        list.add(explanationItem("区域治理成效", defaultNumber(statReportSummary == null ? null : statReportSummary.getGeneratedCount()), ">=1",
            "治理月报、区域态势和压降结果固定按 6.1 监管解释排序输出。", "statReport", "statReport",
            buildSourceQuery(regionCode, null, statMonth, "reportStatus", "1"),
            "首页治理月报", "来源于区域治理月报"));
        list.add(explanationItem("信用分层", countCreditTailRisk(creditScoreSummary), "0",
            "信用分层解释突出红码、D 级和尾部对象，为承保复核提供风险排序。", "creditScore", "creditScore",
            buildSourceQuery(regionCode, null, statMonth, "colorCode", "RED"),
            "首页风险分层", "来源于信用分层聚合"));
        return list;
    }

    private String resolveAzbRoleHomeSummary(YgbAzbCockpitDashboard dashboard, String roleView)
    {
        if (dashboard == null)
        {
            return "";
        }
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbDeviceSummary deviceSummary = dashboard.getDeviceSummary();
        YgbAqInsuranceSummary aqInsuranceSummary = dashboard.getAqInsuranceSummary();
        YgbPreventionFundSummary preventionFundSummary = dashboard.getPreventionFundSummary();
        YgbCreditScoreSummary creditScoreSummary = dashboard.getCreditScoreSummary();
        YgbStatReportSummary statReportSummary = dashboard.getStatReportSummary();

        if ("bank".equals(roleView))
        {
            return "当前重点：协同复核红码与尾部对象 " + countCreditTailRisk(creditScoreSummary)
                + " 家，区域治理月报 " + defaultNumber(statReportSummary == null ? null : statReportSummary.getGeneratedCount())
                + " 份，安责险风险证据" + defaultNumber(aqInsuranceSummary == null ? null : aqInsuranceSummary.getRiskCount()) + " 单。";
        }
        if ("insurer".equals(roleView))
        {
            return "当前重点：安责险风险 " + defaultNumber(aqInsuranceSummary == null ? null : aqInsuranceSummary.getRiskCount())
                + " 单，资金协同异常 " + countFundCoordinationRisk(preventionFundSummary)
                + " 条，红码与尾部对象" + countCreditTailRisk(creditScoreSummary)
                + " 家，治理月报 " + defaultNumber(statReportSummary == null ? null : statReportSummary.getGeneratedCount()) + " 份。";
        }
        return "当前重点：待处置预警 " + defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount())
            + " 条，设备异常 " + countDeviceRisk(deviceSummary)
            + " 台，安责险风险" + defaultNumber(aqInsuranceSummary == null ? null : aqInsuranceSummary.getRiskCount()) + " 单。";
    }

    private Map<String, Object> findQueueSection(List<Map<String, Object>> sections, String key)
    {
        if (sections == null || StringUtils.isEmpty(key))
        {
            return null;
        }
        for (Map<String, Object> section : sections)
        {
            if (section != null && key.equals(String.valueOf(section.get("key"))))
            {
                return section;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void appendQueueItemIfMissing(Map<String, Object> section, String itemKey, Map<String, Object> item)
    {
        if (section == null || StringUtils.isEmpty(itemKey) || item == null)
        {
            return;
        }
        Object itemsValue = section.get("items");
        List<Map<String, Object>> items;
        if (itemsValue instanceof List)
        {
            items = (List<Map<String, Object>>) itemsValue;
        }
        else
        {
            items = new ArrayList<>();
            section.put("items", items);
        }
        for (Map<String, Object> current : items)
        {
            if (current != null && itemKey.equals(String.valueOf(current.get("key"))))
            {
                return;
            }
        }
        items.add(item);
    }

    private Integer countDeviceRisk(YgbDeviceSummary deviceSummary)
    {
        return defaultNumber(deviceSummary == null ? null : deviceSummary.getLockedOrFaultCount())
            + defaultNumber(deviceSummary == null ? null : deviceSummary.getUnauthorizedCount())
            + defaultNumber(deviceSummary == null ? null : deviceSummary.getAuthDeniedCount());
    }

    private Integer countCreditTailRisk(YgbCreditScoreSummary creditScoreSummary)
    {
        return defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getRedCount())
            + defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getDCount());
    }

    private Integer countFundCoordinationRisk(YgbPreventionFundSummary preventionFundSummary)
    {
        return defaultNumber(preventionFundSummary == null ? null : preventionFundSummary.getLowBalanceCount())
            + defaultNumber(preventionFundSummary == null ? null : preventionFundSummary.getMissingEvidenceCount())
            + defaultNumber(preventionFundSummary == null ? null : preventionFundSummary.getNonStubCount());
    }

    private Map<String, Object> summaryCard(String key, String label, Object value, String unit, String note, String cardClass)
    {
        Map<String, Object> item = new HashMap<>();
        item.put("key", key);
        item.put("label", label);
        item.put("value", value);
        item.put("unit", unit);
        item.put("note", note);
        item.put("cardClass", cardClass);
        return item;
    }

    private Map<String, Object> focusQueue(String key, String title, Object count, String unit, String desc, String path, String actionText)
    {
        return focusQueue(key, title, count, unit, desc, path, actionText, null, null, null);
    }

    private Map<String, Object> focusQueue(String key, String title, Object count, String unit, String desc, String path,
        String actionText, Map<String, Object> query, String sourceLabel, String sourceDescription)
    {
        Map<String, Object> item = new HashMap<>();
        item.put("key", key);
        item.put("title", title);
        item.put("count", count);
        item.put("unit", unit);
        item.put("desc", desc);
        item.put("path", path);
        item.put("actionText", actionText);
        item.put("moduleCode", key);
        item.put("query", query);
        item.put("defaultQuery", query);
        item.put("sourceLabel", sourceLabel);
        item.put("sourceDescription", sourceDescription);
        return item;
    }

    private Map<String, Object> workflowStep(String label, String desc)
    {
        Map<String, Object> item = new HashMap<>();
        item.put("label", label);
        item.put("desc", desc);
        return item;
    }

    private Map<String, Object> quickAction(String key, String label, String badge, String desc, String path)
    {
        return quickAction(key, label, badge, desc, path, null, null, null);
    }

    private Map<String, Object> quickAction(String key, String label, String badge, String desc, String path,
        Map<String, Object> query, String sourceLabel, String sourceDescription)
    {
        Map<String, Object> item = new HashMap<>();
        item.put("key", key);
        item.put("moduleCode", key);
        item.put("label", label);
        item.put("badge", badge);
        item.put("desc", desc);
        item.put("path", path);
        item.put("query", query);
        item.put("defaultQuery", query);
        item.put("sourceLabel", sourceLabel);
        item.put("sourceDescription", sourceDescription);
        return item;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> buildQuickSections(List<Map<String, Object>> quickActions, String fallbackTitle,
        String fallbackDescription)
    {
        if (quickActions == null || quickActions.isEmpty())
        {
            return new ArrayList<>();
        }
        Map<String, Map<String, Object>> grouped = new LinkedHashMap<>();
        int sectionIndex = 1;
        for (Map<String, Object> action : quickActions)
        {
            if (action == null)
            {
                continue;
            }
            String sourceLabel = stringValue(action.get("sourceLabel"));
            String sourceDescription = stringValue(action.get("sourceDescription"));
            String groupKey = sourceLabel + "__" + sourceDescription;
            Map<String, Object> section = grouped.get(groupKey);
            if (section == null)
            {
                section = new HashMap<>();
                section.put("key", StringUtils.isNotEmpty(sourceLabel) ? normalizeAggregateKey(sourceLabel) : "portalSection" + sectionIndex);
                section.put("title", StringUtils.isNotEmpty(sourceLabel) ? sourceLabel : fallbackTitle);
                section.put("desc", StringUtils.isNotEmpty(sourceDescription) ? sourceDescription : fallbackDescription);
                section.put("actions", new ArrayList<Map<String, Object>>());
                grouped.put(groupKey, section);
                sectionIndex++;
            }
            ((List<Map<String, Object>>) section.get("actions")).add(action);
        }
        return new ArrayList<>(grouped.values());
    }

    private Map<String, Object> queueSection(String key, String title, String desc, List<Map<String, Object>> items)
    {
        Map<String, Object> item = new HashMap<>();
        item.put("key", key);
        item.put("title", title);
        item.put("desc", desc);
        item.put("items", items);
        return item;
    }

    private Map<String, Object> queueItem(String key, String title, String desc, Object count, String unit, String summary,
        String actionText, String moduleCode, String portalCode, Map<String, Object> defaultQuery, String sourceLabel,
        String sourceDescription)
    {
        Map<String, Object> item = new HashMap<>();
        item.put("key", key);
        item.put("title", title + " " + count + unit);
        item.put("desc", desc + " 路 " + summary);
        item.put("status", actionText);
        item.put("hint", summary);
        item.put("count", count);
        item.put("unit", unit);
        item.put("moduleCode", moduleCode);
        item.put("path", modulePath(portalCode, moduleCode));
        item.put("defaultQuery", defaultQuery);
        item.put("query", defaultQuery);
        item.put("sourceLabel", sourceLabel);
        item.put("sourceDescription", sourceDescription);
        return item;
    }

    private Map<String, Object> explanationItem(String dimensionName, Object currentValue, Object targetValue, String summary,
        String evidenceModule, String recommendModule, Map<String, Object> defaultQuery, String sourceLabel,
        String sourceDescription)
    {
        Map<String, Object> item = new HashMap<>();
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
        item.put("sourceDescription", sourceDescription);
        return item;
    }

    private Map<String, Object> focusPanel(String title, String desc, String moduleCode, Map<String, Object> defaultQuery,
        String sourceLabel, String sourceDescription)
    {
        Map<String, Object> item = new HashMap<>();
        item.put("title", title);
        item.put("desc", desc);
        item.put("moduleCode", moduleCode);
        item.put("defaultQuery", defaultQuery);
        item.put("sourceLabel", sourceLabel);
        item.put("sourceDescription", sourceDescription);
        return item;
    }

    private String normalizeAggregateKey(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            return "";
        }
        return value.replaceAll("[^0-9A-Za-z\\u4e00-\\u9fa5]+", "").trim();
    }

    private String stringValue(Object value)
    {
        return value == null ? "" : String.valueOf(value);
    }

    private Map<String, Object> buildPortalDefaultQuery(String regionCode, String statDate, String statMonth)
    {
        Map<String, Object> query = new HashMap<>();
        query.put("regionCode", regionCode);
        query.put("statDate", statDate);
        query.put("statMonth", statMonth);
        return query;
    }

    private Map<String, Object> buildSourceQuery(String regionCode, String statDate, String statMonth, String field,
        Object value)
    {
        Map<String, Object> query = buildPortalDefaultQuery(regionCode, statDate, statMonth);
        if (value != null)
        {
            query.put(field, value);
        }
        return query;
    }

    private String modulePath(String portalCode, String moduleCode)
    {
        return "/" + portalCode + "/" + moduleCode;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> mapValue(Object value)
    {
        if (value instanceof Map)
        {
            return (Map<String, Object>) value;
        }
        return new HashMap<>();
    }

    private Map<String, Object> hintTag(String label, String type)
    {
        Map<String, Object> item = new HashMap<>();
        item.put("label", label);
        item.put("type", type);
        return item;
    }

    private List<Map<String, Object>> buildGeoJsonFeatures(List<YgbCockpitMapFeature> list)
    {
        if (list == null || list.isEmpty())
        {
            return Collections.emptyList();
        }

        List<Map<String, Object>> features = new ArrayList<>();
        for (YgbCockpitMapFeature item : list)
        {
            Map<String, Object> geometry = new HashMap<>();
            geometry.put("type", item.getGeometryType());
            geometry.put("coordinates", readJsonValue(item.getGeometryJson()));

            Map<String, Object> properties = readJsonMap(item.getPropertiesJson());
            properties.putIfAbsent("featureType", item.getFeatureType());
            properties.putIfAbsent("featureName", item.getFeatureName());
            properties.putIfAbsent("featureStatus", item.getFeatureStatus());
            properties.putIfAbsent("regionCode", item.getRegionCode());
            properties.putIfAbsent("sourceMode", item.getSourceMode());

            Map<String, Object> feature = new HashMap<>();
            feature.put("type", "Feature");
            feature.put("id", item.getFeatureId());
            feature.put("geometry", geometry);
            feature.put("properties", properties);
            features.add(feature);
        }
        return features;
    }

    private List<YgbCreditScore> listTopRiskCreditScores(String regionCode, String statMonth)
    {
        List<YgbCreditScore> list = creditScoreService.selectCreditScoreList(buildCreditScoreQuery(regionCode, statMonth));
        if (list == null || list.isEmpty())
        {
            return Collections.emptyList();
        }
        return list.stream()
            .sorted(Comparator.comparing(YgbCreditScore::getTotalScore, Comparator.nullsLast(Comparator.naturalOrder())))
            .limit(10)
            .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private void enrichEnterpriseMapProperties(List<Map<String, Object>> features, String regionCode, String statMonth)
    {
        if (features == null || features.isEmpty())
        {
            return;
        }
        List<YgbCreditScore> scores = creditScoreService.selectCreditScoreList(buildCreditScoreQuery(regionCode, statMonth));
        Map<Long, YgbCreditScore> byEnterpriseId = new HashMap<>();
        Map<String, YgbCreditScore> byEnterpriseName = new HashMap<>();
        if (scores != null)
        {
            for (YgbCreditScore score : scores)
            {
                if (score.getEnterpriseId() != null)
                {
                    byEnterpriseId.put(score.getEnterpriseId(), score);
                }
                if (StringUtils.isNotEmpty(score.getEnterpriseName()))
                {
                    byEnterpriseName.put(score.getEnterpriseName(), score);
                }
            }
        }
        for (Map<String, Object> feature : features)
        {
            Object propertiesObject = feature.get("properties");
            if (!(propertiesObject instanceof Map))
            {
                continue;
            }
            Map<String, Object> properties = (Map<String, Object>) propertiesObject;
            if (!"ENTERPRISE".equals(String.valueOf(properties.get("featureType"))))
            {
                continue;
            }
            YgbCreditScore score = resolveCreditScoreForFeature(properties, byEnterpriseId, byEnterpriseName);
            if (score == null)
            {
                continue;
            }
            properties.putIfAbsent("enterpriseId", score.getEnterpriseId());
            properties.putIfAbsent("colorCode", score.getColorCode());
            properties.putIfAbsent("riskLevel", score.getColorCode());
            properties.putIfAbsent("insuranceRate", score.getSocialTaxScore());
            properties.putIfAbsent("codeRate", score.getGovernanceScore());
            properties.putIfAbsent("accidentRate", score.getSafetyScore());
            properties.putIfAbsent("violationCount", resolveViolationCount(score));
            properties.putIfAbsent("warningStatus", score.getWarningStatus());
            properties.putIfAbsent("warningStatusText",
                "1".equals(String.valueOf(score.getWarningStatus())) ? "预警中" : "正常");
            if (!properties.containsKey("deviceCount"))
            {
                properties.put("deviceCount", 0);
            }
        }
    }

    private YgbCreditScore resolveCreditScoreForFeature(Map<String, Object> properties,
        Map<Long, YgbCreditScore> byEnterpriseId, Map<String, YgbCreditScore> byEnterpriseName)
    {
        Object enterpriseId = properties.get("enterpriseId");
        if (enterpriseId != null)
        {
            try
            {
                YgbCreditScore score = byEnterpriseId.get(Long.valueOf(String.valueOf(enterpriseId)));
                if (score != null)
                {
                    return score;
                }
            }
            catch (NumberFormatException ignored)
            {
            }
        }
        Object featureName = properties.get("featureName");
        if (featureName != null)
        {
            return byEnterpriseName.get(String.valueOf(featureName));
        }
        return null;
    }

    private Integer resolveViolationCount(YgbCreditScore score)
    {
        if (score == null || StringUtils.isEmpty(score.getFactorJson()))
        {
            return 0;
        }
        try
        {
            Map<String, Object> factors = OBJECT_MAPPER.readValue(score.getFactorJson(),
                new TypeReference<Map<String, Object>>()
                {
                });
            Object governance = factors.get("governance");
            if (governance instanceof Map)
            {
                Object count = ((Map<String, Object>) governance).get("numerator");
                if (count instanceof Number)
                {
                    return ((Number) count).intValue();
                }
            }
        }
        catch (Exception ignored)
        {
        }
        return 0;
    }

    private Object readJsonValue(String json)
    {
        if (StringUtils.isEmpty(json))
        {
            return Collections.emptyList();
        }
        try
        {
            return OBJECT_MAPPER.readValue(json, Object.class);
        }
        catch (Exception e)
        {
            throw new ServiceException("驾驶舱地图要素数据格式错误");
        }
    }

    private Map<String, Object> readJsonMap(String json)
    {
        if (StringUtils.isEmpty(json))
        {
            return new HashMap<>();
        }
        try
        {
            return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>()
            {
            });
        }
        catch (Exception e)
        {
            throw new ServiceException("驾驶舱地图属性数据格式错误");
        }
    }

    private String normalizeDate(String statDate)
    {
        if (StringUtils.isEmpty(statDate))
        {
            return LocalDate.now().format(DATE_FORMATTER);
        }
        try
        {
            return LocalDate.parse(statDate, DATE_FORMATTER).format(DATE_FORMATTER);
        }
        catch (Exception ex)
        {
            throw new ServiceException("统计日期格式错误，应为 yyyy-MM-dd");
        }
    }

    private String normalizeOptionalDate(String statDate)
    {
        if (StringUtils.isEmpty(statDate))
        {
            return null;
        }
        return normalizeDate(statDate);
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

    private Integer defaultNumber(Integer value)
    {
        return value == null ? 0 : value;
    }

    private String resolveDashboardRegion(String regionCode)
    {
        if (regionScopeHelper == null)
        {
            return YgbRegionHelper.defaultDashboardRegion(regionCode);
        }
        return regionScopeHelper.resolveAuthorizedRegionCode(regionCode);
    }

    private BigDecimal defaultDecimal(BigDecimal value)
    {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String resolveSourceName(String sourceCode)
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

    private YgbContract buildContractQuery(String regionCode)
    {
        YgbContract query = new YgbContract();
        query.setRegionCode(regionCode);
        return query;
    }

    private YgbAttendanceRaw buildAttendanceRawQuery(String regionCode)
    {
        YgbAttendanceRaw query = new YgbAttendanceRaw();
        query.setRegionCode(regionCode);
        return query;
    }

    private YgbAttendanceMonthly buildAttendanceMonthlyQuery(String regionCode, String statMonth)
    {
        YgbAttendanceMonthly query = new YgbAttendanceMonthly();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbSalaryBatch buildSalaryBatchQuery(String regionCode, String statMonth)
    {
        YgbSalaryBatch query = new YgbSalaryBatch();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbSalaryDetail buildSalaryDetailQuery(String regionCode, String statMonth)
    {
        YgbSalaryDetail query = new YgbSalaryDetail();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbSocialPayment buildSocialPaymentQuery(String regionCode, String statMonth)
    {
        YgbSocialPayment query = new YgbSocialPayment();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbSocialBaseCompare buildSocialBaseCompareQuery(String regionCode, String statMonth)
    {
        YgbSocialBaseCompare query = new YgbSocialBaseCompare();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbTaxCompare buildTaxCompareQuery(String regionCode, String statMonth)
    {
        YgbTaxCompare query = new YgbTaxCompare();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbEmploymentRatio buildEmploymentRatioQuery(String regionCode, String statMonth)
    {
        YgbEmploymentRatio query = new YgbEmploymentRatio();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbFakeOutsourcingRecord buildFakeOutsourcingQuery(String regionCode, String statMonth)
    {
        YgbFakeOutsourcingRecord query = new YgbFakeOutsourcingRecord();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbNewformWorker buildNewformWorkerQuery(String regionCode, String statMonth)
    {
        YgbNewformWorker query = new YgbNewformWorker();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbOccupationMonitor buildOccupationMonitorQuery(String regionCode, String statMonth)
    {
        YgbOccupationMonitor query = new YgbOccupationMonitor();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbWarning buildWarningQuery(String regionCode)
    {
        YgbWarning query = new YgbWarning();
        query.setRegionCode(YgbRegionHelper.toRegionPrefix(regionCode));
        return query;
    }

    private YgbAqInsurance buildAqInsuranceQuery(String regionCode, String statMonth)
    {
        YgbAqInsurance query = new YgbAqInsurance();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbPreventionFund buildPreventionFundQuery(String regionCode, String statMonth)
    {
        YgbPreventionFund query = new YgbPreventionFund();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbCreditScore buildCreditScoreQuery(String regionCode, String statMonth)
    {
        YgbCreditScore query = new YgbCreditScore();
        query.setRegionCode(YgbRegionHelper.toRegionPrefix(regionCode));
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbDevice buildDeviceQuery(String regionCode)
    {
        YgbDevice query = new YgbDevice();
        query.setRegionCode(regionCode);
        return query;
    }

    private YgbInjuryEvent buildInjuryEventQuery(String regionCode)
    {
        YgbInjuryEvent query = new YgbInjuryEvent();
        query.setRegionCode(regionCode);
        return query;
    }

    private String formatDecimalText(BigDecimal value)
    {
        return defaultDecimal(value).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private YgbHeightWorkReport buildHeightWorkReportQuery(String regionCode, String statDate)
    {
        YgbHeightWorkReport query = new YgbHeightWorkReport();
        query.setRegionCode(regionCode);
        query.setStartTimeBegin(statDate + " 00:00:00");
        query.setStartTimeEnd(statDate + " 23:59:59");
        return query;
    }

    private YgbUninsuredList buildUninsuredListQuery(String regionCode, String statMonth)
    {
        YgbUninsuredList query = new YgbUninsuredList();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbStatReport buildStatReportQuery(String regionCode, String statMonth)
    {
        YgbStatReport query = new YgbStatReport();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbEnterprise buildEnterpriseQuery(String regionCode)
    {
        YgbEnterprise query = new YgbEnterprise();
        query.setRegionCode(regionCode);
        return query;
    }

    private YgbPerson buildPersonQuery(String regionCode)
    {
        YgbPerson query = new YgbPerson();
        query.setRegionCode(regionCode);
        return query;
    }
}
