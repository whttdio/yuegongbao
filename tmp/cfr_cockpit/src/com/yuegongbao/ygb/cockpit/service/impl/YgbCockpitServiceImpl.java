/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.type.TypeReference
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.yuegongbao.common.exception.ServiceException
 *  com.yuegongbao.common.utils.SecurityUtils
 *  com.yuegongbao.common.utils.StringUtils
 *  com.yuegongbao.ygb.aqins.domain.YgbAqInsurance
 *  com.yuegongbao.ygb.aqins.domain.YgbAqInsuranceSummary
 *  com.yuegongbao.ygb.aqins.domain.YgbPreventionFund
 *  com.yuegongbao.ygb.aqins.domain.YgbPreventionFundSummary
 *  com.yuegongbao.ygb.aqins.service.IYgbAqInsuranceService
 *  com.yuegongbao.ygb.aqins.service.IYgbPreventionFundService
 *  com.yuegongbao.ygb.cockpit.domain.YgbAzbCockpitDashboard
 *  com.yuegongbao.ygb.cockpit.domain.YgbCockpitDistribution
 *  com.yuegongbao.ygb.cockpit.domain.YgbCockpitIndicator
 *  com.yuegongbao.ygb.cockpit.domain.YgbCockpitMapFeature
 *  com.yuegongbao.ygb.cockpit.domain.YgbCockpitSnapshot
 *  com.yuegongbao.ygb.cockpit.domain.YgbGeoJsonFeatureCollection
 *  com.yuegongbao.ygb.cockpit.domain.YgbWorkbenchDashboard
 *  com.yuegongbao.ygb.cockpit.mapper.YgbCockpitMapper
 *  com.yuegongbao.ygb.cockpit.service.IYgbCockpitService
 *  com.yuegongbao.ygb.compliance.domain.YgbAttendanceMonthly
 *  com.yuegongbao.ygb.compliance.domain.YgbAttendanceRaw
 *  com.yuegongbao.ygb.compliance.domain.YgbAttendanceRawSummary
 *  com.yuegongbao.ygb.compliance.domain.YgbContract
 *  com.yuegongbao.ygb.compliance.domain.YgbContractSummary
 *  com.yuegongbao.ygb.compliance.domain.YgbSalaryBatch
 *  com.yuegongbao.ygb.compliance.domain.YgbSalaryBatchSummary
 *  com.yuegongbao.ygb.compliance.domain.YgbSalaryDetail
 *  com.yuegongbao.ygb.compliance.domain.YgbSalaryDetailSummary
 *  com.yuegongbao.ygb.compliance.service.IYgbAttendanceMonthlyService
 *  com.yuegongbao.ygb.compliance.service.IYgbAttendanceRawService
 *  com.yuegongbao.ygb.compliance.service.IYgbContractService
 *  com.yuegongbao.ygb.compliance.service.IYgbSalaryBatchService
 *  com.yuegongbao.ygb.compliance.service.IYgbSalaryDetailService
 *  com.yuegongbao.ygb.credit.domain.YgbCreditScore
 *  com.yuegongbao.ygb.credit.domain.YgbCreditScoreSummary
 *  com.yuegongbao.ygb.credit.service.IYgbCreditScoreService
 *  com.yuegongbao.ygb.foundation.domain.YgbEnterprise
 *  com.yuegongbao.ygb.foundation.domain.YgbEnterpriseSummary
 *  com.yuegongbao.ygb.foundation.domain.YgbPerson
 *  com.yuegongbao.ygb.foundation.service.IYgbEnterpriseService
 *  com.yuegongbao.ygb.foundation.service.IYgbPersonService
 *  com.yuegongbao.ygb.newform.domain.YgbNewformWorker
 *  com.yuegongbao.ygb.newform.service.IYgbNewformWorkerService
 *  com.yuegongbao.ygb.occupation.domain.YgbOccupationMonitor
 *  com.yuegongbao.ygb.occupation.service.IYgbOccupationMonitorService
 *  com.yuegongbao.ygb.regulation.domain.YgbEmploymentRatio
 *  com.yuegongbao.ygb.regulation.domain.YgbFakeOutsourcingRecord
 *  com.yuegongbao.ygb.regulation.domain.YgbSocialBaseCompare
 *  com.yuegongbao.ygb.regulation.domain.YgbSocialBaseCompareSummary
 *  com.yuegongbao.ygb.regulation.domain.YgbSocialPayment
 *  com.yuegongbao.ygb.regulation.domain.YgbSocialPaymentSummary
 *  com.yuegongbao.ygb.regulation.domain.YgbTaxCompare
 *  com.yuegongbao.ygb.regulation.domain.YgbTaxCompareSummary
 *  com.yuegongbao.ygb.regulation.domain.YgbUninsuredList
 *  com.yuegongbao.ygb.regulation.domain.YgbUninsuredListSummary
 *  com.yuegongbao.ygb.regulation.service.IYgbEmploymentRatioService
 *  com.yuegongbao.ygb.regulation.service.IYgbFakeOutsourcingService
 *  com.yuegongbao.ygb.regulation.service.IYgbSocialBaseCompareService
 *  com.yuegongbao.ygb.regulation.service.IYgbSocialPaymentService
 *  com.yuegongbao.ygb.regulation.service.IYgbTaxCompareService
 *  com.yuegongbao.ygb.regulation.service.IYgbUninsuredListService
 *  com.yuegongbao.ygb.report.domain.YgbStatReport
 *  com.yuegongbao.ygb.report.domain.YgbStatReportSummary
 *  com.yuegongbao.ygb.report.service.IYgbStatReportService
 *  com.yuegongbao.ygb.safety.domain.YgbDevice
 *  com.yuegongbao.ygb.safety.domain.YgbDeviceSummary
 *  com.yuegongbao.ygb.safety.domain.YgbInjuryEvent
 *  com.yuegongbao.ygb.safety.domain.YgbInjuryEventSummary
 *  com.yuegongbao.ygb.safety.service.IYgbDeviceService
 *  com.yuegongbao.ygb.safety.service.IYgbInjuryEventService
 *  com.yuegongbao.ygb.techdefense.domain.YgbHeightWorkReport
 *  com.yuegongbao.ygb.techdefense.domain.YgbHeightWorkReportSummary
 *  com.yuegongbao.ygb.techdefense.service.IYgbHeightWorkReportService
 *  com.yuegongbao.ygb.util.YgbRegionHelper
 *  com.yuegongbao.ygb.warning.domain.YgbWarning
 *  com.yuegongbao.ygb.warning.domain.YgbWarningSummary
 *  com.yuegongbao.ygb.warning.service.IYgbWarningService
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Service
 */
package com.yuegongbao.ygb.cockpit.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.SecurityUtils;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.aqins.domain.YgbAqInsurance;
import com.yuegongbao.ygb.aqins.domain.YgbAqInsuranceSummary;
import com.yuegongbao.ygb.aqins.domain.YgbPreventionFund;
import com.yuegongbao.ygb.aqins.domain.YgbPreventionFundSummary;
import com.yuegongbao.ygb.aqins.service.IYgbAqInsuranceService;
import com.yuegongbao.ygb.aqins.service.IYgbPreventionFundService;
import com.yuegongbao.ygb.cockpit.domain.YgbAzbCockpitDashboard;
import com.yuegongbao.ygb.cockpit.domain.YgbCockpitDistribution;
import com.yuegongbao.ygb.cockpit.domain.YgbCockpitIndicator;
import com.yuegongbao.ygb.cockpit.domain.YgbCockpitMapFeature;
import com.yuegongbao.ygb.cockpit.domain.YgbCockpitSnapshot;
import com.yuegongbao.ygb.cockpit.domain.YgbGeoJsonFeatureCollection;
import com.yuegongbao.ygb.cockpit.domain.YgbWorkbenchDashboard;
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
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;
import com.yuegongbao.ygb.foundation.domain.YgbEnterpriseSummary;
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
import com.yuegongbao.ygb.safety.domain.YgbDeviceSummary;
import com.yuegongbao.ygb.safety.domain.YgbInjuryEvent;
import com.yuegongbao.ygb.safety.domain.YgbInjuryEventSummary;
import com.yuegongbao.ygb.safety.service.IYgbDeviceService;
import com.yuegongbao.ygb.safety.service.IYgbInjuryEventService;
import com.yuegongbao.ygb.techdefense.domain.YgbHeightWorkReport;
import com.yuegongbao.ygb.techdefense.domain.YgbHeightWorkReportSummary;
import com.yuegongbao.ygb.techdefense.service.IYgbHeightWorkReportService;
import com.yuegongbao.ygb.util.YgbRegionHelper;
import com.yuegongbao.ygb.warning.domain.YgbWarning;
import com.yuegongbao.ygb.warning.domain.YgbWarningSummary;
import com.yuegongbao.ygb.warning.service.IYgbWarningService;
import java.lang.invoke.CallSite;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class YgbCockpitServiceImpl
implements IYgbCockpitService {
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

    public YgbCockpitIndicator getIndicators(String regionCode, String statDate) {
        String dashboardRegion = YgbRegionHelper.defaultDashboardRegion((String)regionCode);
        String regionPrefix = YgbRegionHelper.toRegionPrefix((String)dashboardRegion);
        String queryDate = this.normalizeDate(statDate);
        YgbCockpitSnapshot snapshot = this.cockpitMapper.selectLatestSnapshot(dashboardRegion, queryDate);
        YgbCockpitIndicator indicator = new YgbCockpitIndicator();
        indicator.setRegionCode(dashboardRegion);
        indicator.setRegionName(YgbRegionHelper.resolveRegionName((String)dashboardRegion));
        indicator.setStatDate(queryDate);
        if (snapshot != null) {
            indicator.setDispatchCompanyCount(this.defaultNumber(snapshot.getDispatchCompanyCount()));
            indicator.setEmployerCount(this.defaultNumber(snapshot.getEmployerCount()));
            indicator.setDispatchedWorkerCount(this.defaultNumber(snapshot.getDispatchedWorkerCount()));
            indicator.setHighRiskEnterpriseCount(this.defaultNumber(snapshot.getHighRiskEnterpriseCount()));
            indicator.setInsuranceRate(this.defaultDecimal(snapshot.getInsuranceRate()));
            indicator.setAqInsuranceRate(this.defaultDecimal(snapshot.getAqInsuranceRate()));
            indicator.setExpandCompletionRate(this.defaultDecimal(snapshot.getExpandCompletionRate()));
            indicator.setNewInjuryRate(this.defaultDecimal(snapshot.getNewInjuryRate()));
        } else {
            indicator.setDispatchCompanyCount(Integer.valueOf(0));
            indicator.setEmployerCount(Integer.valueOf(0));
            indicator.setDispatchedWorkerCount(Integer.valueOf(0));
            indicator.setHighRiskEnterpriseCount(Integer.valueOf(0));
            indicator.setInsuranceRate(BigDecimal.ZERO);
            indicator.setAqInsuranceRate(BigDecimal.ZERO);
            indicator.setExpandCompletionRate(BigDecimal.ZERO);
            indicator.setNewInjuryRate(BigDecimal.ZERO);
        }
        indicator.setTodayWarningCount(this.defaultNumber(this.cockpitMapper.countWarningsByDate(regionPrefix, queryDate)));
        indicator.setPendingWarningCount(this.defaultNumber(this.cockpitMapper.countPendingWarnings(regionPrefix)));
        indicator.setOnlineDeviceCount(this.defaultNumber(this.cockpitMapper.countOnlineDevices(regionPrefix)));
        indicator.setOverdueInjuryCount(this.defaultNumber(this.cockpitMapper.countOverdueInjuries(regionPrefix)));
        return indicator;
    }

    public List<YgbCockpitSnapshot> listTrend(String regionCode, String statDate, Integer days) {
        String dashboardRegion = YgbRegionHelper.defaultDashboardRegion((String)regionCode);
        String queryDate = this.normalizeDate(statDate);
        int actualDays = days == null || days < 3 ? 7 : Math.min(days, 31);
        LocalDate endDate = LocalDate.parse(queryDate, DATE_FORMATTER);
        LocalDate startDate = endDate.minus((long)actualDays - 1L, ChronoUnit.DAYS);
        return this.cockpitMapper.selectSnapshotTrend(dashboardRegion, startDate.format(DATE_FORMATTER), endDate.format(DATE_FORMATTER));
    }

    public List<YgbCockpitDistribution> listWarningDistribution(String regionCode, String statMonth) {
        String queryMonth;
        String dashboardRegion = YgbRegionHelper.defaultDashboardRegion((String)regionCode);
        String regionPrefix = YgbRegionHelper.toRegionPrefix((String)dashboardRegion);
        List list = this.cockpitMapper.selectWarningDistribution(regionPrefix, queryMonth = this.normalizeMonth(statMonth));
        if (list.isEmpty()) {
            return list;
        }
        int total = list.stream().map(YgbCockpitDistribution::getMetricCount).filter(count -> count != null).mapToInt(Integer::intValue).sum();
        for (YgbCockpitDistribution item : list) {
            item.setDimensionName(this.resolveSourceName(item.getDimensionCode()));
            if (total <= 0) {
                item.setMetricRate(BigDecimal.ZERO);
                continue;
            }
            item.setMetricRate(BigDecimal.valueOf((double)this.defaultNumber(item.getMetricCount()).intValue() * 100.0 / (double)total).setScale(2, RoundingMode.HALF_UP));
        }
        return list;
    }

    public YgbGeoJsonFeatureCollection getMapFeatures(String regionCode, String statDate) {
        String dashboardRegion = YgbRegionHelper.defaultDashboardRegion((String)regionCode);
        String regionPrefix = YgbRegionHelper.toRegionPrefix((String)dashboardRegion);
        String queryDate = this.normalizeOptionalDate(statDate);
        List list = this.cockpitMapper.selectMapFeatureList(regionPrefix, queryDate);
        YgbGeoJsonFeatureCollection collection = new YgbGeoJsonFeatureCollection();
        collection.setStatDate(StringUtils.isEmpty((String)queryDate) ? this.normalizeDate(null) : queryDate);
        collection.setFeatures(this.buildGeoJsonFeatures(list));
        return collection;
    }

    public YgbWorkbenchDashboard getYgbDashboard(String regionCode, String statDate, String statMonth, Integer days) {
        String dashboardRegion = YgbRegionHelper.defaultDashboardRegion((String)regionCode);
        String queryDate = this.normalizeDate(statDate);
        String queryMonth = this.normalizeMonth(statMonth);
        YgbWorkbenchDashboard dashboard = new YgbWorkbenchDashboard();
        dashboard.setIndicators(this.getIndicators(dashboardRegion, queryDate));
        dashboard.setTrend(this.listTrend(dashboardRegion, queryDate, days));
        dashboard.setDistribution(this.listWarningDistribution(dashboardRegion, queryMonth));
        dashboard.setMap(this.getMapFeatures(dashboardRegion, queryDate));
        dashboard.setContractSummary(this.contractService.selectContractSummary(this.buildContractQuery(dashboardRegion)));
        dashboard.setAttendanceRawSummary(this.attendanceRawService.selectAttendanceRawSummary(this.buildAttendanceRawQuery(dashboardRegion)));
        dashboard.setAttendanceMonthlySummary(this.attendanceMonthlyService.selectAttendanceMonthlySummary(this.buildAttendanceMonthlyQuery(dashboardRegion, queryMonth)));
        dashboard.setSalaryBatchSummary(this.salaryBatchService.selectSalaryBatchSummary(this.buildSalaryBatchQuery(dashboardRegion, queryMonth)));
        dashboard.setSalaryDetailSummary(this.salaryDetailService.selectSalaryDetailSummary(this.buildSalaryDetailQuery(dashboardRegion, queryMonth)));
        dashboard.setSocialPaymentSummary(this.socialPaymentService.selectSocialPaymentSummary(this.buildSocialPaymentQuery(dashboardRegion, queryMonth)));
        dashboard.setSocialBaseCompareSummary(this.socialBaseCompareService.selectSocialBaseCompareSummary(this.buildSocialBaseCompareQuery(dashboardRegion, queryMonth)));
        dashboard.setTaxCompareSummary(this.taxCompareService.selectTaxCompareSummary(this.buildTaxCompareQuery(dashboardRegion, queryMonth)));
        dashboard.setEmploymentRatioSummary(this.employmentRatioService.selectEmploymentRatioSummary(this.buildEmploymentRatioQuery(dashboardRegion, queryMonth)));
        dashboard.setFakeOutsourcingSummary(this.fakeOutsourcingService.selectFakeOutsourcingSummary(this.buildFakeOutsourcingQuery(dashboardRegion, queryMonth)));
        dashboard.setNewformWorkerSummary(this.newformWorkerService.selectNewformWorkerSummary(this.buildNewformWorkerQuery(dashboardRegion, queryMonth)));
        dashboard.setOccupationMonitorSummary(this.occupationMonitorService.selectOccupationMonitorSummary(this.buildOccupationMonitorQuery(dashboardRegion, queryMonth)));
        dashboard.setWarningSummary(this.warningService.selectWarningSummary(this.buildWarningQuery(dashboardRegion)));
        dashboard.setHeightWorkReportSummary(this.heightWorkReportService.selectHeightWorkReportSummary(this.buildHeightWorkReportQuery(dashboardRegion, queryDate)));
        dashboard.setUninsuredListSummary(this.uninsuredListService.selectUninsuredSummary(this.buildUninsuredListQuery(dashboardRegion, queryMonth)));
        dashboard.setStatReportSummary(this.statReportService.selectStatReportSummary(this.buildStatReportQuery(dashboardRegion, queryMonth)));
        dashboard.setEnterpriseSummary(this.enterpriseService.selectEnterpriseSummary(this.buildEnterpriseQuery(dashboardRegion)));
        dashboard.setPersonSummary(this.personService.selectPersonSummary(this.buildPersonQuery(dashboardRegion)));
        dashboard.setDefaultQuery(this.buildPortalDefaultQuery(dashboardRegion, queryDate, queryMonth));
        dashboard.setSourceLabel("ygb-home");
        dashboard.setSourceDescription("\u9996\u9875\u529e\u7406\u805a\u5408");
        List<Map<String, Object>> ygbQueueSections = this.buildYgbQueueSections(dashboard, dashboardRegion, queryDate, queryMonth);
        List<Map<String, Object>> ygbExplanation = this.buildYgbHomeExplanation(dashboard, dashboardRegion, queryMonth);
        dashboard.setSummaryCards(this.buildYgbSummaryCards(dashboard, queryMonth));
        dashboard.setFocusQueues(this.buildYgbFocusQueues(dashboard, dashboardRegion, queryDate, queryMonth));
        dashboard.setQuickActions(this.buildYgbQuickActions(dashboardRegion, queryDate, queryMonth));
        dashboard.setQuickSections(this.buildQuickSections(dashboard.getQuickActions(), "\u95e8\u6237\u5feb\u6377\u5165\u53e3", "\u9996\u9875\u5feb\u6377\u5165\u53e3\u5df2\u6309\u6765\u6e90\u4e0a\u4e0b\u6587\u5b8c\u6210\u95e8\u6237\u805a\u5408\u3002"));
        dashboard.setHintTags(this.buildYgbHintTags(dashboard));
        dashboard.setQueueSections(ygbQueueSections);
        dashboard.setWorkflowSteps(this.buildYgbWorkflowSteps(ygbQueueSections, ygbExplanation));
        dashboard.setYgbExplanation(ygbExplanation);
        dashboard.setFocusPanels(this.buildYgbFocusPanels(ygbExplanation));
        dashboard.setHomeSummary(this.buildYgbHomeSummary(dashboard));
        return dashboard;
    }

    public YgbWorkbenchDashboard getYgbHomeAggregate(String regionCode, String statDate, String statMonth, Integer days) {
        return this.getYgbDashboard(regionCode, statDate, statMonth, days);
    }

    public YgbAzbCockpitDashboard getAzbDashboard(String regionCode, String statDate, String statMonth, Integer days) {
        String dashboardRegion = YgbRegionHelper.defaultDashboardRegion((String)regionCode);
        String queryDate = this.normalizeDate(statDate);
        String queryMonth = this.normalizeMonth(statMonth);
        String roleView = this.resolveAzbRoleView();
        YgbAzbCockpitDashboard dashboard = new YgbAzbCockpitDashboard();
        dashboard.setIndicators(this.getIndicators(dashboardRegion, queryDate));
        dashboard.setTrend(this.listTrend(dashboardRegion, queryDate, days));
        dashboard.setDistribution(this.listWarningDistribution(dashboardRegion, queryMonth));
        dashboard.setMap(this.getMapFeatures(dashboardRegion, queryDate));
        dashboard.setRoleView(roleView);
        dashboard.setRoleLabel(this.resolveAzbRoleLabel(roleView));
        dashboard.setRoleDescription(this.resolveAzbRoleDescription(roleView));
        dashboard.setAqInsuranceSummary(this.aqInsuranceService.selectAqInsuranceSummary(this.buildAqInsuranceQuery(dashboardRegion, queryMonth)));
        dashboard.setPreventionFundSummary(this.preventionFundService.selectPreventionFundSummary(this.buildPreventionFundQuery(dashboardRegion, queryMonth)));
        dashboard.setCreditScoreSummary(this.creditScoreService.selectCreditScoreSummary(this.buildCreditScoreQuery(dashboardRegion, queryMonth)));
        dashboard.setWarningSummary(this.warningService.selectWarningSummary(this.buildWarningQuery(dashboardRegion)));
        dashboard.setHeightWorkReportSummary(this.heightWorkReportService.selectHeightWorkReportSummary(this.buildHeightWorkReportQuery(dashboardRegion, queryDate)));
        dashboard.setDeviceSummary(this.deviceService.selectDeviceSummary(this.buildDeviceQuery(dashboardRegion)));
        dashboard.setInjuryEventSummary(this.injuryEventService.selectInjuryEventSummary(this.buildInjuryEventQuery(dashboardRegion)));
        dashboard.setStatReportSummary(this.statReportService.selectStatReportSummary(this.buildStatReportQuery(dashboardRegion, queryMonth)));
        dashboard.setEnterpriseSummary(this.enterpriseService.selectEnterpriseSummary(this.buildEnterpriseQuery(dashboardRegion)));
        dashboard.setPersonSummary(this.personService.selectPersonSummary(this.buildPersonQuery(dashboardRegion)));
        dashboard.setSummaryCards(this.buildAzbSummaryCards(dashboard, roleView));
        dashboard.setFocusQueues(this.buildAzbFocusQueues(dashboard, roleView));
        dashboard.setWorkflowSteps(this.buildAzbWorkflowSteps(roleView));
        dashboard.setQuickActions(this.buildAzbQuickActions(roleView, dashboardRegion, queryDate, queryMonth));
        dashboard.setQuickSections(this.buildQuickSections(dashboard.getQuickActions(), "\u95e8\u6237\u5feb\u6377\u5165\u53e3", "\u9996\u9875\u5feb\u6377\u5165\u53e3\u5df2\u6309\u91cd\u70b9\u5bf9\u8c61\u548c\u6cbb\u7406\u6765\u6e90\u5b8c\u6210\u95e8\u6237\u805a\u5408\u3002"));
        dashboard.setHintTags(this.buildAzbHintTags(dashboard, roleView));
        dashboard.setDefaultQuery(this.buildPortalDefaultQuery(dashboardRegion, queryDate, queryMonth));
        dashboard.setSourceLabel("azb-home");
        dashboard.setSourceDescription("\u9996\u9875\u6cbb\u7406\u805a\u5408");
        dashboard.setQueueSections(this.buildAzbQueueSections(dashboard, roleView, dashboardRegion, queryDate, queryMonth));
        dashboard.setAzbExplanation(this.buildAzbHomeExplanation(dashboard, roleView, dashboardRegion, queryMonth));
        dashboard.setFocusPanels(this.buildAzbFocusPanels(dashboard, roleView, dashboardRegion, queryMonth));
        dashboard.setHomeSummary(this.buildAzbHomeSummary(dashboard, roleView));
        return dashboard;
    }

    public YgbAzbCockpitDashboard getAzbHomeAggregate(String regionCode, String statDate, String statMonth, Integer days) {
        return this.getAzbDashboard(regionCode, statDate, statMonth, days);
    }

    private String resolveAzbRoleView() {
        if (SecurityUtils.hasRole((String)"ygb_bank")) {
            return "bank";
        }
        if (SecurityUtils.hasRole((String)"ygb_insurer")) {
            return "insurer";
        }
        if (SecurityUtils.hasRole((String)"ygb_emergency_supervisor")) {
            return "emergency";
        }
        return "azb-default";
    }

    private String resolveAzbRoleLabel(String roleView) {
        if ("bank".equals(roleView)) {
            return "\u94f6\u884c\u534f\u540c";
        }
        if ("insurer".equals(roleView)) {
            return "\u4fdd\u9669\u673a\u6784";
        }
        if ("emergency".equals(roleView)) {
            return "\u5e94\u6025\u76d1\u7ba1\u5458";
        }
        return "\u5b89\u8d23\u4fdd\u5de5\u4f5c\u53f0";
    }

    private String resolveAzbRoleDescription(String roleView) {
        if ("bank".equals(roleView)) {
            return "\u805a\u7126\u4fe1\u7528\u753b\u50cf\u3001\u533a\u57df\u62a5\u8868\u548c\u91cd\u70b9\u4f01\u4e1a\u53d8\u5316\uff0c\u4fdd\u6301\u53ea\u8bfb\u534f\u540c\u89c6\u89d2\u3002";
        }
        if ("insurer".equals(roleView)) {
            return "\u56f4\u7ed5\u5b89\u8d23\u9669\u8986\u76d6\u3001\u4e8b\u6545\u9884\u9632\u8d44\u91d1\u548c\u4fe1\u7528\u8bc4\u5206\u5f62\u6210\u65e5\u5e38\u53ea\u8bfb\u534f\u540c\u3002";
        }
        if ("emergency".equals(roleView)) {
            return "\u5148\u770b\u533a\u57df\u98ce\u9669\u9762\uff0c\u518d\u76ef\u8bbe\u5907\u5728\u7ebf\u3001\u9ad8\u5904\u4f5c\u4e1a\u548c\u9884\u8b66\u95ed\u73af\u3002";
        }
        return "\u5f53\u524d\u95e8\u6237\u4ee5\u73b0\u573a\u6cbb\u7406\u3001\u6295\u4fdd\u534f\u540c\u548c\u98ce\u9669\u538b\u964d\u4e3a\u4e3b\u3002";
    }

    private List<Map<String, Object>> buildYgbSummaryCards(YgbWorkbenchDashboard dashboard, String statMonth) {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        YgbEnterpriseSummary enterpriseSummary = dashboard.getEnterpriseSummary();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbStatReportSummary statReportSummary = dashboard.getStatReportSummary();
        YgbSalaryDetailSummary salaryDetailSummary = dashboard.getSalaryDetailSummary();
        YgbSocialPaymentSummary socialPaymentSummary = dashboard.getSocialPaymentSummary();
        YgbTaxCompareSummary taxCompareSummary = dashboard.getTaxCompareSummary();
        YgbUninsuredListSummary uninsuredListSummary = dashboard.getUninsuredListSummary();
        list.add(this.summaryCard("enterprisePending", "\u4f01\u4e1a\u5f85\u529e", this.defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()), "\u6761", "\u6309\u4f01\u4e1a\u7ef4\u5ea6\u6c47\u603b\u5f85\u529e\u3001\u5f02\u5e38\u4e0e\u8054\u52a8\u5165\u53e3\u3002", "ygb-summary-card--warning"));
        list.add(this.summaryCard("enterpriseRisk", "\u4f01\u4e1a\u5f02\u5e38", this.defaultNumber(uninsuredListSummary == null ? null : uninsuredListSummary.getPendingCount()) + this.defaultNumber(taxCompareSummary == null ? null : Integer.valueOf(taxCompareSummary.getAbnormalCount())), "\u6761", "\u8054\u52a8\u5de5\u8d44\u3001\u793e\u4fdd\u3001\u4e2a\u7a0e\u4e0e\u6269\u9762\u51cf\u635f\u5f02\u5e38\u3002", "ygb-summary-card--danger"));
        list.add(this.summaryCard("monthStatus", statMonth + "\u6708\u5f52\u6863", this.defaultNumber(statReportSummary == null ? null : statReportSummary.getDraftCount()), "\u4efd", "\u6309\u7edf\u8ba1\u6708\u8f93\u51fa\u5de5\u8d44\u3001\u793e\u4fdd\u3001\u4e2a\u7a0e\u4e0e\u6708\u62a5\u72b6\u6001\u3002", "ygb-summary-card--success"));
        list.add(this.summaryCard("priorityQueue", "\u9996\u9875\u4f18\u5148\u961f\u5217", this.defaultNumber(salaryDetailSummary == null ? null : Integer.valueOf(salaryDetailSummary.getFailedCount())) + this.defaultNumber(socialPaymentSummary == null ? null : Integer.valueOf(socialPaymentSummary.getOverdueCount())) + this.defaultNumber(enterpriseSummary == null ? null : enterpriseSummary.getSyncErrorCount()), "\u9879", "\u63a5\u53e3\u5df2\u8fd4\u56de\u9996\u9875\u6392\u5e8f\u4f9d\u636e\u3002", ""));
        return list;
    }

    private List<Map<String, Object>> buildYgbQuickActions(String regionCode, String statDate, String statMonth) {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        list.add(this.quickAction("contract", "\u5408\u540c\u5907\u6848", "\u529e\u7406\u94fe", "\u8fdb\u5165\u5408\u540c\u5f85\u5907\u6848\u5217\u8868\uff0c\u7ee7\u7eed\u627f\u63a5\u7528\u5de5\u5408\u89c4\u529e\u7406\u3002", this.modulePath("ygb", "contract"), this.buildSourceQuery(regionCode, statDate, statMonth, "contractStatus", "1"), "\u9996\u9875\u4f01\u4e1a\u529e\u7406", "\u6765\u81ea\u9996\u9875\u4f01\u4e1a\u5f85\u529e\u805a\u5408"));
        list.add(this.quickAction("salaryBatch", "\u5de5\u8d44\u6279\u6b21", "\u6708\u5ea6\u529e\u7406", "\u8fdb\u5165\u5de5\u8d44\u6279\u6b21\uff0c\u5904\u7406\u5f53\u6708\u5f85\u751f\u6210\u4e0e\u5f85\u63d0\u4ea4\u6570\u636e\u3002", this.modulePath("ygb", "salaryBatch"), this.buildSourceQuery(regionCode, statDate, statMonth, "batchStatus", "3"), "\u9996\u9875\u6708\u4efd\u805a\u5408", "\u6765\u81ea\u9996\u9875\u6708\u4efd\u7ef4\u5ea6\u805a\u5408"));
        list.add(this.quickAction("socialPayment", "\u793e\u4fdd\u7f34\u7eb3", "\u6708\u5ea6\u6bd4\u5bf9", "\u8fdb\u5165\u793e\u4fdd\u6b20\u8d39\u4e0e\u6574\u6539\u5217\u8868\uff0c\u4fdd\u6301\u6708\u4efd\u6765\u6e90\u6761\u4ef6\u3002", this.modulePath("ygb", "socialPayment"), this.buildSourceQuery(regionCode, statDate, statMonth, "paymentStatus", "2"), "\u9996\u9875\u6708\u4efd\u805a\u5408", "\u6765\u81ea\u9996\u9875\u6708\u4efd\u7ef4\u5ea6\u805a\u5408"));
        list.add(this.quickAction("taxCompare", "\u4e2a\u7a0e\u6bd4\u5bf9", "\u8054\u52a8\u5f02\u5e38", "\u8fdb\u5165\u4e2a\u7a0e\u5f02\u5e38\u5217\u8868\uff0c\u6838\u67e5\u5de5\u8d44\u4e0e\u7533\u62a5\u5dee\u5f02\u3002", this.modulePath("ygb", "taxCompare"), this.buildSourceQuery(regionCode, statDate, statMonth, "compareResult", "2"), "\u9996\u9875\u6708\u4efd\u805a\u5408", "\u6765\u81ea\u9996\u9875\u6708\u4efd\u7ef4\u5ea6\u805a\u5408"));
        list.add(this.quickAction("uninsuredList", "\u6269\u9762\u51cf\u635f", "\u6574\u6539\u540d\u5355", "\u8fdb\u5165\u6f0f\u4fdd\u6e05\u5355\uff0c\u7ee7\u7eed\u6309\u6765\u6e90\u6761\u4ef6\u6574\u6539\u95ed\u73af\u3002", this.modulePath("ygb", "uninsuredList"), this.buildSourceQuery(regionCode, statDate, statMonth, "disposalStatus", "0"), "\u9996\u9875\u6574\u6539\u95ed\u73af", "\u6765\u81ea\u9996\u9875\u6269\u9762\u51cf\u635f\u805a\u5408"));
        list.add(this.quickAction("warning", "\u9884\u8b66\u4e2d\u5fc3", "\u95ed\u73af\u5904\u7f6e", "\u8fdb\u5165\u9884\u8b66\u5217\u8868\uff0c\u7ee7\u7eed\u5904\u7406\u9996\u9875\u4f18\u5148\u961f\u5217\u3002", this.modulePath("ygb", "warning"), this.buildSourceQuery(regionCode, statDate, statMonth, "warnStatus", "0"), "\u9996\u9875\u4f18\u5148\u961f\u5217", "\u6765\u81ea\u9996\u9875\u9884\u8b66\u95ed\u73af\u805a\u5408"));
        list.addAll(this.buildYgbSupplementQuickActions(regionCode, statDate, statMonth));
        return list;
    }

    private List<Map<String, Object>> buildYgbFocusQueues(YgbWorkbenchDashboard dashboard, String regionCode, String statDate, String statMonth) {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
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
        list.add(this.focusQueue("contract", "\u5408\u540c\u5907\u6848\u94fe\u8def", this.defaultNumber(contractSummary == null ? null : Integer.valueOf(contractSummary.getPendingCount())), "\u4efd", "\u5408\u540c\u82e5\u672a\u5907\u6848\uff0c\u5c06\u7ee7\u7eed\u963b\u65ad\u8003\u52e4\u5f52\u96c6\u548c\u5de5\u8d44\u95ed\u73af\uff0c\u5efa\u8bae\u4f18\u5148\u6838\u5bf9\u6761\u6b3e\u548c\u5907\u6848\u72b6\u6001\u3002", this.modulePath("ygb", "contract"), "\u8865\u5408\u540c\u6750\u6599\u6216\u63a8\u8fdb\u5907\u6848", this.buildSourceQuery(regionCode, statDate, statMonth, "contractStatus", "1"), "\u9a7e\u9a76\u8231\u7126\u70b9 / \u5408\u540c\u5907\u6848\u94fe\u8def", "\u6765\u81ea\u9a7e\u9a76\u8231\u4f01\u4e1a\u529e\u7406\u7126\u70b9"));
        list.add(this.focusQueue("attendance", "\u8003\u52e4\u5f52\u96c6\u94fe\u8def", this.defaultNumber(attendanceRawSummary == null ? null : Integer.valueOf(attendanceRawSummary.getUncollectedCount())), "\u6761", "\u672a\u5f52\u96c6\u6216\u5f02\u5e38\u8003\u52e4\u4f1a\u76f4\u63a5\u5f71\u54cd\u5de5\u8d44\u6838\u9a8c\u4e0e\u53d1\u653e\uff0c\u5efa\u8bae\u5148\u5904\u7406\u8865\u5f55\u548c\u6821\u9a8c\u5931\u8d25\u5bf9\u8c61\u3002", this.modulePath("ygb", "attendanceRaw"), "\u5904\u7406\u672a\u5f52\u96c6\u548c\u5f02\u5e38\u8003\u52e4", this.buildSourceQuery(regionCode, statDate, statMonth, "collectStatus", "0"), "\u9a7e\u9a76\u8231\u7126\u70b9 / \u8003\u52e4\u5f52\u96c6\u94fe\u8def", "\u6765\u81ea\u9a7e\u9a76\u8231\u8003\u52e4\u5f52\u96c6\u7126\u70b9"));
        list.add(this.focusQueue("salaryBatch", "\u5de5\u8d44\u53d1\u653e\u94fe\u8def", this.defaultNumber(salaryBatchSummary == null ? null : Integer.valueOf(salaryBatchSummary.getPendingGenerateCount())) + this.defaultNumber(salaryBatchSummary == null ? null : Integer.valueOf(salaryBatchSummary.getPendingSubmitCount())) + this.defaultNumber(salaryDetailSummary == null ? null : Integer.valueOf(salaryDetailSummary.getFailedCount())), "\u9879", "\u5de5\u8d44\u94fe\u5f53\u524d\u91cd\u70b9\u5728\u5f85\u751f\u6210\u660e\u7ec6\u3001\u5f85\u63d0\u4ea4\u53d1\u653e\u548c\u5931\u8d25\u660e\u7ec6\uff0c\u5efa\u8bae\u5148\u5224\u65ad\u5361\u70b9\u5728\u5230\u8d26\u3001\u660e\u7ec6\u751f\u6210\u8fd8\u662f\u94f6\u884c\u56de\u5199\u3002", this.modulePath("ygb", "salaryBatch"), "\u63a8\u8fdb\u53d1\u85aa\u95ed\u73af", this.buildSourceQuery(regionCode, statDate, statMonth, "batchStatus", "3"), "\u9a7e\u9a76\u8231\u7126\u70b9 / \u5de5\u8d44\u53d1\u653e\u94fe\u8def", "\u6765\u81ea\u9a7e\u9a76\u8231\u5de5\u8d44\u53d1\u653e\u7126\u70b9"));
        list.add(this.focusQueue("socialTax", "\u793e\u4fdd\u7a0e\u52a1\u6574\u6539\u94fe", this.defaultNumber(socialPaymentSummary == null ? null : Integer.valueOf(socialPaymentSummary.getOverdueCount())) + this.defaultNumber(socialBaseCompareSummary == null ? null : Integer.valueOf(socialBaseCompareSummary.getAbnormalCount())) + this.defaultNumber(taxCompareSummary == null ? null : Integer.valueOf(taxCompareSummary.getAbnormalCount())), "\u6761", "\u5f53\u524d\u8d22\u52a1\u6574\u6539\u91cd\u70b9\u5728\u793e\u4fdd\u6b20\u8d39\u3001\u793e\u4fdd\u57fa\u6570\u5f02\u5e38\u548c\u4e2a\u7a0e\u5dee\u5f02\uff0c\u5efa\u8bae\u5148\u9501\u5b9a\u5f71\u54cd\u8303\u56f4\u518d\u51b3\u5b9a\u8865\u7f34\u3001\u56de\u67e5\u8fd8\u662f\u7ee7\u7eed\u9884\u8b66\u627f\u63a5\u3002", this.modulePath("ygb", "socialPayment"), "\u8054\u52a8\u8d22\u52a1\u6574\u6539", this.buildSourceQuery(regionCode, statDate, statMonth, "paymentStatus", "2"), "\u9a7e\u9a76\u8231\u7126\u70b9 / \u793e\u4fdd\u7a0e\u52a1\u6574\u6539\u94fe", "\u6765\u81ea\u9a7e\u9a76\u8231\u793e\u4fdd\u7a0e\u52a1\u6574\u6539\u7126\u70b9"));
        list.add(this.focusQueue("warning", "\u9884\u8b66\u5904\u7f6e\u94fe\u8def", this.defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()) + this.defaultNumber(warningSummary == null ? null : warningSummary.getProcessingCount()), "\u6761", "\u9884\u8b66\u4e2d\u5fc3\u5f53\u524d\u627f\u63a5\u793e\u4fdd\u3001\u7a0e\u52a1\u3001\u6269\u9762\u3001\u73b0\u573a\u548c\u5de5\u4f24\u8054\u52a8\u5f02\u5e38\uff0c\u5efa\u8bae\u5148\u6d88\u5316\u5f85\u5904\u7f6e\u548c\u5904\u7406\u4e2d\u5de5\u5355\u3002", this.modulePath("ygb", "warning"), "\u8fdb\u5165\u9884\u8b66\u4e2d\u5fc3", this.buildSourceQuery(regionCode, statDate, statMonth, "warnStatus", "0"), "\u9a7e\u9a76\u8231\u7126\u70b9 / \u9884\u8b66\u5904\u7f6e\u94fe\u8def", "\u6765\u81ea\u9a7e\u9a76\u8231\u9884\u8b66\u5904\u7f6e\u7126\u70b9"));
        list.add(this.focusQueue("heightWork", "\u9ad8\u5904\u4f5c\u4e1a\u95ed\u73af\u94fe", this.defaultNumber(heightWorkReportSummary == null ? null : heightWorkReportSummary.getActiveCount()) + this.defaultNumber(heightWorkReportSummary == null ? null : heightWorkReportSummary.getInvalidWorkerCount()), "\u9879", "\u9ad8\u5904\u4f5c\u4e1a\u5f53\u524d\u91cd\u70b9\u5728\u8fdb\u884c\u4e2d\u4f5c\u4e1a\u3001\u5f02\u5e38\u8bc1\u4e66\u548c\u7ed3\u675f\u7559\u75d5\uff0c\u5efa\u8bae\u4f18\u5148\u6838\u5bf9\u672a\u7ed3\u675f\u548c\u8bc1\u4e66\u5f02\u5e38\u5bf9\u8c61\u3002", this.modulePath("ygb", "heightWorkReport"), "\u5904\u7406\u4f5c\u4e1a\u95ed\u73af", this.buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "0"), "\u9a7e\u9a76\u8231\u7126\u70b9 / \u9ad8\u5904\u4f5c\u4e1a\u95ed\u73af\u94fe", "\u6765\u81ea\u9a7e\u9a76\u8231\u9ad8\u5904\u4f5c\u4e1a\u7126\u70b9"));
        list.add(this.focusQueue("uninsured", "\u6269\u9762\u51cf\u635f\u94fe\u8def", this.defaultNumber(uninsuredListSummary == null ? null : uninsuredListSummary.getPendingCount()) + this.defaultNumber(uninsuredListSummary == null ? null : uninsuredListSummary.getUnwarnedCount()), "\u6761", "\u6f0f\u4fdd\u5bf9\u8c61\u5f53\u524d\u91cd\u70b9\u5728\u5f85\u6838\u67e5\u3001\u672a\u9884\u8b66\u548c\u9ad8\u66b4\u9732\u5bf9\u8c61\uff0c\u5efa\u8bae\u4f18\u5148\u63a8\u8fdb\u50ac\u7f34\u548c\u8865\u7f34\u95ed\u73af\u3002", this.modulePath("ygb", "uninsuredList"), "\u63a8\u8fdb\u6269\u9762\u6574\u6539", this.buildSourceQuery(regionCode, statDate, statMonth, "disposalStatus", "0"), "\u9a7e\u9a76\u8231\u7126\u70b9 / \u6269\u9762\u51cf\u635f\u94fe\u8def", "\u6765\u81ea\u9a7e\u9a76\u8231\u6269\u9762\u51cf\u635f\u7126\u70b9"));
        return list;
    }

    private List<Map<String, Object>> buildYgbHintTags(YgbWorkbenchDashboard dashboard) {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbSalaryDetailSummary salaryDetailSummary = dashboard.getSalaryDetailSummary();
        YgbSocialPaymentSummary socialPaymentSummary = dashboard.getSocialPaymentSummary();
        YgbHeightWorkReportSummary heightWorkReportSummary = dashboard.getHeightWorkReportSummary();
        YgbUninsuredListSummary uninsuredListSummary = dashboard.getUninsuredListSummary();
        if (this.defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()) > 0) {
            list.add(this.hintTag("\u5f53\u524d\u4ecd\u6709 " + this.defaultNumber(warningSummary.getPendingCount()) + " \u6761\u5f85\u5904\u7f6e\u9884\u8b66\uff0c\u5efa\u8bae\u4f18\u5148\u627f\u63a5\u9884\u8b66\u95ed\u73af\u3002", "warning"));
        }
        if (this.defaultNumber(salaryDetailSummary == null ? null : Integer.valueOf(salaryDetailSummary.getFailedCount())) > 0) {
            list.add(this.hintTag("\u5de5\u8d44\u660e\u7ec6\u5b58\u5728 " + this.defaultNumber(salaryDetailSummary.getFailedCount()) + " \u6761\u53d1\u653e\u5931\u8d25\u8bb0\u5f55\uff0c\u5efa\u8bae\u4f18\u5148\u6838\u5bf9\u5931\u8d25\u539f\u56e0\u548c\u56de\u5199\u72b6\u6001\u3002", "danger"));
        }
        if (this.defaultNumber(socialPaymentSummary == null ? null : Integer.valueOf(socialPaymentSummary.getOverdueCount())) > 0) {
            list.add(this.hintTag("\u5f53\u524d\u5b58\u5728 " + this.defaultNumber(socialPaymentSummary.getOverdueCount()) + " \u6761\u793e\u4fdd\u6b20\u8d39\u5bf9\u8c61\uff0c\u5efa\u8bae\u8054\u52a8\u793e\u4fdd\u7f34\u8d39\u53f0\u8d26\u548c\u6f0f\u4fdd\u6574\u6539\u94fe\u5904\u7f6e\u3002", "warning"));
        }
        if (this.defaultNumber(heightWorkReportSummary == null ? null : heightWorkReportSummary.getInvalidWorkerCount()) > 0) {
            list.add(this.hintTag("\u9ad8\u5904\u4f5c\u4e1a\u5b58\u5728 " + this.defaultNumber(heightWorkReportSummary.getInvalidWorkerCount()) + " \u540d\u8bc1\u4e66\u5f02\u5e38\u4eba\u5458\uff0c\u5efa\u8bae\u4f18\u5148\u6838\u5bf9\u4f5c\u4e1a\u4eba\u5458\u8d44\u683c\u548c\u7ed3\u675f\u7559\u75d5\u3002", "danger"));
        }
        if (this.defaultNumber(uninsuredListSummary == null ? null : uninsuredListSummary.getPendingCount()) > 0) {
            list.add(this.hintTag("\u6f0f\u4fdd\u6574\u6539\u4ecd\u6709 " + this.defaultNumber(uninsuredListSummary.getPendingCount()) + " \u6761\u5f85\u6838\u67e5\u5bf9\u8c61\uff0c\u5efa\u8bae\u7ed3\u5408\u6269\u9762\u51cf\u635f\u548c\u7edf\u8ba1\u62a5\u8868\u540c\u6b65\u63a8\u8fdb\u3002", "info"));
        }
        return list;
    }

    private List<Map<String, Object>> buildYgbQueueSections(YgbWorkbenchDashboard dashboard, String regionCode, String statDate, String statMonth) {
        ArrayList<Map<String, Object>> sections = new ArrayList<Map<String, Object>>();
        YgbContractSummary contractSummary = dashboard.getContractSummary();
        YgbSalaryBatchSummary salaryBatchSummary = dashboard.getSalaryBatchSummary();
        YgbSalaryDetailSummary salaryDetailSummary = dashboard.getSalaryDetailSummary();
        YgbSocialPaymentSummary socialPaymentSummary = dashboard.getSocialPaymentSummary();
        YgbTaxCompareSummary taxCompareSummary = dashboard.getTaxCompareSummary();
        YgbUninsuredListSummary uninsuredListSummary = dashboard.getUninsuredListSummary();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbStatReportSummary statReportSummary = dashboard.getStatReportSummary();
        ArrayList<Map<String, Object>> enterpriseItems = new ArrayList<Map<String, Object>>();
        enterpriseItems.add(this.queueItem("contractPending", "\u5408\u540c\u5f85\u5907\u6848", "\u4f01\u4e1a\u529e\u7406\u5f85\u529e", this.defaultNumber(contractSummary == null ? null : Integer.valueOf(contractSummary.getPendingCount())), "\u4efd", "\u9501\u5b9a\u4f01\u4e1a\u529e\u7406\u94fe\u4ecd\u672a\u5f52\u6863\u7684\u5408\u540c\u5bf9\u8c61\u3002", "\u5f85\u627f\u63a5", "contract", "ygb", this.buildSourceQuery(regionCode, statDate, statMonth, "contractStatus", "1"), "\u9996\u9875\u4f01\u4e1a\u529e\u7406", "\u6765\u81ea\u9996\u9875\u4f01\u4e1a\u7ef4\u5ea6\u805a\u5408"));
        enterpriseItems.add(this.queueItem("warningPending", "\u9884\u8b66\u95ed\u73af", "\u4f01\u4e1a\u5f02\u5e38\u5f85\u7ee7\u7eed\u5904\u7f6e", this.defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()), "\u6761", "\u6309\u4f01\u4e1a\u7ef4\u5ea6\u805a\u5408\u5f85\u529e\u91cf\u548c\u5f02\u5e38\u91cf\u3002", "\u4f18\u5148\u5904\u7406", "warning", "ygb", this.buildSourceQuery(regionCode, statDate, statMonth, "warnStatus", "0"), "\u9996\u9875\u4f01\u4e1a\u529e\u7406", "\u6765\u81ea\u9996\u9875\u4f01\u4e1a\u7ef4\u5ea6\u805a\u5408"));
        sections.add(this.queueSection("enterprise", "\u4f01\u4e1a\u7ef4\u5ea6\u529e\u7406\u961f\u5217", "\u6309\u4f01\u4e1a\u8f93\u51fa\u5f85\u529e\u91cf\u3001\u5f02\u5e38\u91cf\u3001\u6708\u62a5\u72b6\u6001\u4e0e\u8054\u52a8\u6765\u6e90\u3002", enterpriseItems));
        ArrayList<Map<String, Object>> monthItems = new ArrayList<Map<String, Object>>();
        monthItems.add(this.queueItem("salaryBatch", "\u5de5\u8d44\u6279\u6b21\u5f85\u751f\u6210", "\u6708\u4efd\u529e\u7406\u6458\u8981", this.defaultNumber(salaryBatchSummary == null ? null : Integer.valueOf(salaryBatchSummary.getPendingGenerateCount())), "\u6279", "\u6309\u7edf\u8ba1\u6708\u627f\u63a5\u5de5\u8d44\u53d1\u653e\u529e\u7406\u94fe\u3002", "\u7ee7\u7eed\u529e\u7406", "salaryBatch", "ygb", this.buildSourceQuery(regionCode, statDate, statMonth, "batchStatus", "3"), "\u9996\u9875\u6708\u4efd\u805a\u5408", "\u6765\u81ea\u9996\u9875\u6708\u4efd\u7ef4\u5ea6\u805a\u5408"));
        monthItems.add(this.queueItem("socialPayment", "\u793e\u4fdd\u6b20\u8d39\u6574\u6539", "\u6708\u4efd\u98ce\u9669\u6458\u8981", this.defaultNumber(socialPaymentSummary == null ? null : Integer.valueOf(socialPaymentSummary.getOverdueCount())), "\u6761", "\u6309\u7edf\u8ba1\u6708\u7ee7\u7eed\u5904\u7406\u793e\u4fdd\u6b20\u8d39\u4e0e\u8865\u7f34\u60c5\u5f62\u3002", "\u7ee7\u7eed\u6574\u6539", "socialPayment", "ygb", this.buildSourceQuery(regionCode, statDate, statMonth, "paymentStatus", "2"), "\u9996\u9875\u6708\u4efd\u805a\u5408", "\u6765\u81ea\u9996\u9875\u6708\u4efd\u7ef4\u5ea6\u805a\u5408"));
        monthItems.add(this.queueItem("taxCompare", "\u4e2a\u7a0e\u5f02\u5e38\u6bd4\u5bf9", "\u6708\u4efd\u8054\u52a8\u6458\u8981", this.defaultNumber(taxCompareSummary == null ? null : Integer.valueOf(taxCompareSummary.getAbnormalCount())), "\u6761", "\u6309\u7edf\u8ba1\u6708\u6838\u9a8c\u5de5\u8d44\u4e0e\u4e2a\u7a0e\u7533\u62a5\u5dee\u5f02\u3002", "\u7ee7\u7eed\u6bd4\u5bf9", "taxCompare", "ygb", this.buildSourceQuery(regionCode, statDate, statMonth, "compareResult", "2"), "\u9996\u9875\u6708\u4efd\u805a\u5408", "\u6765\u81ea\u9996\u9875\u6708\u4efd\u7ef4\u5ea6\u805a\u5408"));
        sections.add(this.queueSection("month", "\u6708\u4efd\u7ef4\u5ea6\u529e\u7406\u961f\u5217", "\u6309\u7edf\u8ba1\u6708\u8f93\u51fa\u5de5\u8d44\u3001\u793e\u4fdd\u3001\u4e2a\u7a0e\u3001\u6269\u9762\u51cf\u635f\u4e0e\u9884\u8b66\u6458\u8981\u3002", monthItems));
        ArrayList<Map<String, Object>> priorityItems = new ArrayList<Map<String, Object>>();
        priorityItems.add(this.queueItem("salaryDetailFail", "\u5de5\u8d44\u53d1\u653e\u5931\u8d25", "\u4f18\u5148\u7ea7\u6392\u5e8f\u5b57\u6bb5\u5df2\u7531\u63a5\u53e3\u7ed9\u51fa", this.defaultNumber(salaryDetailSummary == null ? null : Integer.valueOf(salaryDetailSummary.getFailedCount())), "\u6761", "\u4f18\u5148\u7ea7\u6700\u9ad8\u7684\u5931\u8d25\u660e\u7ec6\uff0c\u524d\u7aef\u4e0d\u518d\u91cd\u6392\u4e1a\u52a1\u8bed\u4e49\u3002", "\u4f18\u5148\u56de\u67e5", "salaryDetail", "ygb", this.buildSourceQuery(regionCode, statDate, statMonth, "payStatus", "3"), "\u9996\u9875\u4f18\u5148\u961f\u5217", "\u6765\u81ea\u9996\u9875\u4f18\u5148\u7ea7\u7ef4\u5ea6\u805a\u5408"));
        priorityItems.add(this.queueItem("uninsuredPending", "\u6f0f\u4fdd\u6574\u6539\u540d\u5355", "\u6269\u9762\u51cf\u635f\u4f18\u5148\u9879", this.defaultNumber(uninsuredListSummary == null ? null : uninsuredListSummary.getPendingCount()), "\u6761", "\u6309\u6574\u6539\u94fe\u4f18\u5148\u7ea7\u8f93\u51fa\u5f85\u6838\u67e5\u540d\u5355\u3002", "\u4f18\u5148\u6574\u6539", "uninsuredList", "ygb", this.buildSourceQuery(regionCode, statDate, statMonth, "disposalStatus", "0"), "\u9996\u9875\u4f18\u5148\u961f\u5217", "\u6765\u81ea\u9996\u9875\u4f18\u5148\u7ea7\u7ef4\u5ea6\u805a\u5408"));
        priorityItems.add(this.queueItem("statDraft", "\u6708\u62a5\u5f85\u5f52\u6863", "\u529e\u7406\u6708\u62a5\u5f52\u6863\u961f\u5217", this.defaultNumber(statReportSummary == null ? null : statReportSummary.getDraftCount()), "\u4efd", "\u4f18\u5148\u5904\u7406\u8349\u7a3f\u4e0e\u5f52\u6863\u95ed\u73af\u3002", "\u7ee7\u7eed\u5f52\u6863", "statReport", "ygb", this.buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "0"), "\u9996\u9875\u4f18\u5148\u961f\u5217", "\u6765\u81ea\u9996\u9875\u4f18\u5148\u7ea7\u7ef4\u5ea6\u805a\u5408"));
        sections.add(this.queueSection("priority", "\u4f18\u5148\u7ea7\u7ef4\u5ea6\u961f\u5217", "\u8f93\u51fa\u9996\u9875\u961f\u5217\u6392\u5e8f\u5b57\u6bb5\u548c\u9ed8\u8ba4\u67e5\u8be2\u6761\u4ef6\u3002", priorityItems));
        this.appendYgbSupplementQueueSections(sections, dashboard, regionCode, statDate, statMonth);
        return sections;
    }

    private List<Map<String, Object>> buildYgbHomeExplanation(YgbWorkbenchDashboard dashboard, String regionCode, String statMonth) {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        YgbContractSummary contractSummary = dashboard.getContractSummary();
        YgbSalaryDetailSummary salaryDetailSummary = dashboard.getSalaryDetailSummary();
        YgbSocialBaseCompareSummary socialBaseCompareSummary = dashboard.getSocialBaseCompareSummary();
        YgbTaxCompareSummary taxCompareSummary = dashboard.getTaxCompareSummary();
        YgbUninsuredListSummary uninsuredListSummary = dashboard.getUninsuredListSummary();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        list.add(this.explanationItem("\u7528\u5de5\u5408\u89c4", this.defaultNumber(contractSummary == null ? null : Integer.valueOf(contractSummary.getPendingCount())), "0", "530.1 \u529e\u7406\u94fe\u4f18\u5148\u627f\u63a5\u5408\u540c\u5907\u6848\u4e0e\u7528\u5de5\u5408\u89c4\u5f85\u529e\u3002", "contract", "contract", this.buildSourceQuery(regionCode, null, statMonth, "contractStatus", "1"), "\u9996\u9875\u529e\u7406\u89e3\u91ca", "\u6765\u6e90\u4e8e\u5408\u540c\u5907\u6848\u529e\u7406\u94fe"));
        list.add(this.explanationItem("\u5de5\u8d44\u53d1\u653e", this.defaultNumber(salaryDetailSummary == null ? null : Integer.valueOf(salaryDetailSummary.getFailedCount())), "0", "\u5148\u770b\u5de5\u8d44\u53d1\u653e\u5931\u8d25\u660e\u7ec6\uff0c\u518d\u56de\u5230\u6708\u5ea6\u529e\u7406\u94fe\u95ed\u73af\u3002", "salaryDetail", "salaryDetail", this.buildSourceQuery(regionCode, null, statMonth, "payStatus", "3"), "\u9996\u9875\u529e\u7406\u89e3\u91ca", "\u6765\u6e90\u4e8e\u5de5\u8d44\u53d1\u653e\u529e\u7406\u94fe"));
        list.add(this.explanationItem("\u793e\u4fdd\u7a0e\u52a1\u6bd4\u5bf9", this.defaultNumber(socialBaseCompareSummary == null ? null : Integer.valueOf(socialBaseCompareSummary.getAbnormalCount())) + this.defaultNumber(taxCompareSummary == null ? null : Integer.valueOf(taxCompareSummary.getAbnormalCount())), "0", "\u5171\u4eab\u5e95\u6570\u6309 530.1 \u529e\u7406\u94fe\u7ec4\u7ec7\uff0c\u4f18\u5148\u89e3\u91ca\u793e\u4fdd\u57fa\u6570\u548c\u4e2a\u7a0e\u5f02\u5e38\u3002", "socialBaseCompare", "socialBaseCompare", this.buildSourceQuery(regionCode, null, statMonth, "compareResult", "2"), "\u9996\u9875\u529e\u7406\u89e3\u91ca", "\u6765\u6e90\u4e8e\u793e\u4fdd\u7a0e\u52a1\u6bd4\u5bf9\u94fe"));
        list.add(this.explanationItem("\u6269\u9762\u51cf\u635f", this.defaultNumber(uninsuredListSummary == null ? null : uninsuredListSummary.getPendingCount()), "0", "\u6f0f\u4fdd\u6e05\u5355\u7ee7\u7eed\u6cbf\u6574\u6539\u94fe\u627f\u63a5\uff0c\u4e0d\u6df7\u5165\u6cbb\u7406\u94fe\u6392\u5e8f\u3002", "uninsuredList", "uninsuredList", this.buildSourceQuery(regionCode, null, statMonth, "disposalStatus", "0"), "\u9996\u9875\u529e\u7406\u89e3\u91ca", "\u6765\u6e90\u4e8e\u6269\u9762\u51cf\u635f\u6574\u6539\u94fe"));
        list.add(this.explanationItem("\u9884\u8b66\u95ed\u73af", this.defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()), "0", "\u9996\u9875\u6458\u8981\u4ee5\u9884\u8b66\u95ed\u73af\u7ed3\u679c\u4e3a\u4e3b\uff0c\u4e0d\u518d\u8ba9\u9759\u6001\u6587\u6848\u515c\u5e95\u4e1a\u52a1\u8bed\u4e49\u3002", "warning", "warning", this.buildSourceQuery(regionCode, null, statMonth, "warnStatus", "0"), "\u9996\u9875\u529e\u7406\u89e3\u91ca", "\u6765\u6e90\u4e8e\u9884\u8b66\u95ed\u73af\u94fe"));
        return list;
    }

    private List<Map<String, Object>> buildYgbWorkflowSteps(List<Map<String, Object>> queueSections, List<Map<String, Object>> explanationItems) {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> section : queueSections) {
            list.add(this.workflowStep(String.valueOf(section.get("title")), String.valueOf(section.get("desc"))));
            if (list.size() < 4) continue;
            return list;
        }
        for (Map<String, Object> item : explanationItems) {
            list.add(this.workflowStep(String.valueOf(item.get("dimensionName")), String.valueOf(item.get("summary"))));
            if (list.size() < 4) continue;
            break;
        }
        return list;
    }

    private List<Map<String, Object>> buildYgbFocusPanels(List<Map<String, Object>> explanationItems) {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> item : explanationItems) {
            list.add(this.focusPanel(String.valueOf(item.get("dimensionName")), String.valueOf(item.get("summary")), String.valueOf(item.get("recommendModule")), this.mapValue(item.get("defaultQuery")), String.valueOf(item.get("sourceLabel")), String.valueOf(item.get("sourceDescription"))));
            if (list.size() < 3) continue;
            break;
        }
        return list;
    }

    private String buildYgbHomeSummary(YgbWorkbenchDashboard dashboard) {
        String enhancedSummary = this.buildEnhancedYgbHomeSummary(dashboard);
        if (StringUtils.isNotEmpty((String)enhancedSummary)) {
            return enhancedSummary;
        }
        YgbContractSummary contractSummary = dashboard.getContractSummary();
        YgbSalaryDetailSummary salaryDetailSummary = dashboard.getSalaryDetailSummary();
        YgbSocialPaymentSummary socialPaymentSummary = dashboard.getSocialPaymentSummary();
        YgbTaxCompareSummary taxCompareSummary = dashboard.getTaxCompareSummary();
        return "\u5f53\u524d\u91cd\u70b9\uff1a\u5408\u540c\u5f85\u5907\u6848 " + this.defaultNumber(contractSummary == null ? null : Integer.valueOf(contractSummary.getPendingCount())) + " \u4efd\uff0c\u5de5\u8d44\u5931\u8d25 " + this.defaultNumber(salaryDetailSummary == null ? null : Integer.valueOf(salaryDetailSummary.getFailedCount())) + " \u6761\uff0c\u793e\u4fdd\u6b20\u8d39 " + this.defaultNumber(socialPaymentSummary == null ? null : Integer.valueOf(socialPaymentSummary.getOverdueCount())) + " \u6761\uff0c\u4e2a\u7a0e\u5f02\u5e38 " + this.defaultNumber(taxCompareSummary == null ? null : Integer.valueOf(taxCompareSummary.getAbnormalCount())) + " \u6761\u3002";
    }

    private List<Map<String, Object>> buildAzbSummaryCards(YgbAzbCockpitDashboard dashboard, String roleView) {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        YgbAqInsuranceSummary aqInsuranceSummary = dashboard.getAqInsuranceSummary();
        YgbPreventionFundSummary preventionFundSummary = dashboard.getPreventionFundSummary();
        YgbCreditScoreSummary creditScoreSummary = dashboard.getCreditScoreSummary();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbHeightWorkReportSummary heightWorkReportSummary = dashboard.getHeightWorkReportSummary();
        YgbDeviceSummary deviceSummary = dashboard.getDeviceSummary();
        YgbStatReportSummary statReportSummary = dashboard.getStatReportSummary();
        if ("bank".equals(roleView)) {
            list.add(this.summaryCard("creditTotal", "\u4fe1\u7528\u5bf9\u8c61", this.defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getTotalCount()), "\u5bb6", "\u5f53\u524d\u533a\u57df\u5df2\u6c89\u6dc0\u4fe1\u7528\u8bc4\u5206\u4f01\u4e1a\u6570\u91cf\u3002", ""));
            list.add(this.summaryCard("redCount", "\u7ea2\u7801\u4f01\u4e1a", this.defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getRedCount()), "\u5bb6", "\u9700\u8981\u91cd\u70b9\u590d\u6838\u6388\u4fe1\u548c\u534f\u540c\u5bf9\u8c61\u3002", "azb-summary-card--danger"));
            list.add(this.summaryCard("avgScore", "\u5e73\u5747\u5206", this.formatDecimalText(creditScoreSummary == null ? null : creditScoreSummary.getAverageScore()), "\u5206", "\u7528\u6765\u89c2\u5bdf\u533a\u57df\u4fe1\u7528\u9762\u662f\u5426\u6301\u7eed\u8d70\u5f31\u3002", ""));
            list.add(this.summaryCard("reportCount", "\u533a\u57df\u62a5\u8868", this.defaultNumber(statReportSummary == null ? null : statReportSummary.getGeneratedCount()), "\u4efd", "\u5f53\u524d\u6708\u4efd\u53ef\u76f4\u63a5\u534f\u540c\u590d\u6838\u7684\u7edf\u8ba1\u6210\u679c\u3002", "azb-summary-card--warning"));
            return list;
        }
        if ("insurer".equals(roleView)) {
            list.add(this.summaryCard("coverageRate", "\u5b89\u8d23\u9669\u8986\u76d6\u7387", this.formatDecimalText(aqInsuranceSummary == null ? null : aqInsuranceSummary.getCoverageRate()), "%", "\u56f4\u7ed5\u6295\u4fdd\u8986\u76d6\u5148\u5224\u65ad\u4fdd\u5355\u6c60\u662f\u5426\u5b58\u5728\u660e\u663e\u7f3a\u53e3\u3002", "azb-summary-card--success"));
            list.add(this.summaryCard("riskCount", "\u5230\u671f\u98ce\u9669", this.defaultNumber(aqInsuranceSummary == null ? null : aqInsuranceSummary.getRiskCount()), "\u5355", "\u5373\u5c06\u5230\u671f\u548c\u5df2\u8fc7\u671f\u4fdd\u5355\u5408\u8ba1\u3002", "azb-summary-card--danger"));
            list.add(this.summaryCard("fundBalance", "\u9884\u9632\u8d39\u4f59\u989d", this.formatDecimalText(preventionFundSummary == null ? null : preventionFundSummary.getRemainingAmountTotal()), "\u5143", "\u5feb\u901f\u5224\u65ad\u8d44\u91d1\u6c60\u662f\u5426\u8fd8\u80fd\u652f\u6491\u73b0\u573a\u6cbb\u7406\u52a8\u4f5c\u3002", "azb-summary-card--warning"));
            list.add(this.summaryCard("redCredit", "\u7ea2\u7801\u4f01\u4e1a", this.defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getRedCount()), "\u5bb6", "\u4fbf\u4e8e\u7b5b\u51fa\u9700\u8981\u91cd\u70b9\u4fdd\u9669\u670d\u52a1\u7684\u98ce\u9669\u4f01\u4e1a\u3002", ""));
            return list;
        }
        list.add(this.summaryCard("warningPending", "\u5f85\u5904\u7f6e\u9884\u8b66", this.defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()), "\u6761", "\u9700\u8981\u7ee7\u7eed\u538b\u5b9e\u95ed\u73af\u7684\u98ce\u9669\u5de5\u5355\u3002", "azb-summary-card--warning"));
        list.add(this.summaryCard("deviceOnline", "\u5728\u7ebf\u8bbe\u5907", this.defaultNumber(deviceSummary == null ? null : deviceSummary.getOnlineCount()), "\u53f0", "\u73b0\u573a\u5728\u7ebf\u8bbe\u5907\u662f\u5e94\u6025\u4fa7\u7b2c\u4e00\u9053\u611f\u77e5\u5165\u53e3\u3002", ""));
        list.add(this.summaryCard("heightActive", "\u9ad8\u5904\u4f5c\u4e1a\u8fdb\u884c\u4e2d", this.defaultNumber(heightWorkReportSummary == null ? null : heightWorkReportSummary.getActiveCount()), "\u5355", "\u7528\u4e8e\u9501\u5b9a\u5f53\u524d\u4ecd\u5728\u73b0\u573a\u8fdb\u884c\u7684\u9ad8\u98ce\u9669\u4f5c\u4e1a\u3002", "azb-summary-card--danger"));
        list.add(this.summaryCard("coverageRate", "\u5b89\u8d23\u9669\u8986\u76d6\u7387", this.formatDecimalText(aqInsuranceSummary == null ? null : aqInsuranceSummary.getCoverageRate()), "%", "\u5e2e\u52a9\u5224\u65ad\u73b0\u573a\u4f01\u4e1a\u6295\u4fdd\u8986\u76d6\u9762\u662f\u5426\u5230\u4f4d\u3002", "azb-summary-card--success"));
        return list;
    }

    private List<Map<String, Object>> buildAzbFocusQueues(YgbAzbCockpitDashboard dashboard, String roleView) {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbDeviceSummary deviceSummary = dashboard.getDeviceSummary();
        YgbHeightWorkReportSummary heightWorkReportSummary = dashboard.getHeightWorkReportSummary();
        YgbInjuryEventSummary injuryEventSummary = dashboard.getInjuryEventSummary();
        YgbAqInsuranceSummary aqInsuranceSummary = dashboard.getAqInsuranceSummary();
        YgbPreventionFundSummary preventionFundSummary = dashboard.getPreventionFundSummary();
        YgbCreditScoreSummary creditScoreSummary = dashboard.getCreditScoreSummary();
        YgbStatReportSummary statReportSummary = dashboard.getStatReportSummary();
        if ("bank".equals(roleView)) {
            list.add(this.focusQueue("creditScore", "\u7ea2\u7801\u4e0e\u5c3e\u90e8\u5bf9\u8c61", this.countCreditTailRisk(creditScoreSummary), "\u5bb6", "\u5148\u9501\u5b9a\u7ea2\u7801\u548c D \u7ea7\u4f01\u4e1a\uff0c\u518d\u7ed3\u5408\u62a5\u8868\u590d\u6838\u53d8\u5316\u8d8b\u52bf\u3002", "/azb/creditScore", "\u67e5\u770b\u4fe1\u7528\u753b\u50cf", this.buildSourceQuery(null, null, null, "colorCode", "RED"), "\u9a7e\u9a76\u8231\u7126\u70b9 / \u7ea2\u7801\u4e0e\u4f4e\u5206\u4f01\u4e1a", "\u6765\u81ea\u9a7e\u9a76\u8231\u4fe1\u7528\u5206\u5c42\u7126\u70b9"));
            list.add(this.focusQueue("statReport", "\u533a\u57df\u62a5\u8868\u590d\u6838", this.defaultNumber(statReportSummary == null ? null : statReportSummary.getGeneratedCount()), "\u4efd", "\u5f53\u524d\u6708\u4efd\u5df2\u751f\u6210\u62a5\u8868\uff0c\u53ef\u76f4\u63a5\u67e5\u770b\u533a\u57df\u53d8\u5316\u3002", "/azb/statReport", "\u67e5\u770b\u533a\u57df\u62a5\u8868", this.buildSourceQuery(null, null, null, "reportStatus", "1"), "\u9a7e\u9a76\u8231\u7126\u70b9 / \u533a\u57df\u62a5\u8868\u590d\u6838", "\u6765\u81ea\u9a7e\u9a76\u8231\u533a\u57df\u62a5\u8868\u7126\u70b9"));
            list.add(this.focusQueue("aqInsurance", "\u5b89\u8d23\u9669\u98ce\u9669\u5bf9\u8c61", this.defaultNumber(aqInsuranceSummary == null ? null : aqInsuranceSummary.getRiskCount()), "\u5355", "\u534f\u540c\u590d\u6838\u65f6\u540c\u6b65\u770b\u4fdd\u5355\u5230\u671f\u4e0e\u5931\u6548\u5bf9\u8c61\uff0c\u8865\u8db3\u98ce\u9669\u8bc1\u636e\u3002", "/azb/aqInsurance", "\u67e5\u770b\u5b89\u8d23\u9669\u98ce\u9669", this.buildSourceQuery(null, null, null, "policyStatus", "2"), "\u9a7e\u9a76\u8231\u7126\u70b9 / \u5b89\u8d23\u9669\u98ce\u9669\u5bf9\u8c61", "\u6765\u81ea\u9a7e\u9a76\u8231\u534f\u540c\u590d\u6838\u7126\u70b9"));
            list.add(this.focusQueue("warning", "\u5f85\u5904\u7f6e\u9884\u8b66", this.defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()), "\u6761", "\u9ad8\u7b49\u7ea7\u9884\u8b66\u4f5c\u4e3a\u94f6\u884c\u534f\u540c\u590d\u6838\u7684\u98ce\u9669\u8bc1\u636e\uff0c\u4e0d\u76f4\u63a5\u627f\u63a5\u6cbb\u7406\u52a8\u4f5c\u3002", "/azb/warning", "\u67e5\u770b\u9884\u8b66\u8bc1\u636e", this.buildSourceQuery(null, null, null, "warnStatus", "0"), "\u9a7e\u9a76\u8231\u7126\u70b9 / \u5f85\u5904\u7f6e\u9884\u8b66", "\u6765\u81ea\u9a7e\u9a76\u8231\u98ce\u9669\u8bc1\u636e\u7126\u70b9"));
            list.add(this.focusQueue("device", "\u8bbe\u5907\u5f02\u5e38\u8bc1\u636e", this.countDeviceRisk(deviceSummary), "\u53f0", "\u79bb\u7ebf\u3001\u6545\u969c\u548c\u672a\u6388\u6743\u8bbe\u5907\u7528\u4e8e\u8865\u8db3\u73b0\u573a\u98ce\u9669\u8bc1\u636e\u3002", "/azb/device", "\u67e5\u770b\u8bbe\u5907\u8bc1\u636e", this.buildSourceQuery(null, null, null, "authStatus", "0"), "\u9a7e\u9a76\u8231\u7126\u70b9 / \u8bbe\u5907\u5f02\u5e38\u8bc1\u636e", "\u6765\u81ea\u9a7e\u9a76\u8231\u98ce\u9669\u8bc1\u636e\u7126\u70b9"));
            list.add(this.focusQueue("heightWorkReport", "\u8fdb\u884c\u4e2d\u9ad8\u5904\u4f5c\u4e1a", this.defaultNumber(heightWorkReportSummary == null ? null : heightWorkReportSummary.getActiveCount()), "\u5355", "\u8fdb\u884c\u4e2d\u9ad8\u5904\u4f5c\u4e1a\u7528\u4e8e\u8865\u8db3\u6280\u672f\u9632\u8303\u8bc1\u636e\uff0c\u4e0d\u6df7\u5165\u4f01\u4e1a\u529e\u7406\u8bed\u4e49\u3002", "/azb/heightWorkReport", "\u67e5\u770b\u4f5c\u4e1a\u8bc1\u636e", this.buildSourceQuery(null, null, null, "reportStatus", "0"), "\u9a7e\u9a76\u8231\u7126\u70b9 / \u8fdb\u884c\u4e2d\u9ad8\u5904\u4f5c\u4e1a", "\u6765\u81ea\u9a7e\u9a76\u8231\u98ce\u9669\u8bc1\u636e\u7126\u70b9"));
            return list;
        }
        if ("insurer".equals(roleView)) {
            list.add(this.focusQueue("aqInsurance", "\u4fdd\u5355\u5230\u671f\u4e0e\u5931\u6548", this.defaultNumber(aqInsuranceSummary == null ? null : aqInsuranceSummary.getRiskCount()), "\u5355", "\u4f18\u5148\u6d88\u5316\u5230\u671f\u98ce\u9669\uff0c\u907f\u514d\u4fdd\u5355\u65ad\u6863\u3002", "/azb/aqInsurance", "\u8fdb\u5165\u6295\u4fdd\u76d1\u7ba1", this.buildSourceQuery(null, null, null, "policyStatus", "2"), "\u9a7e\u9a76\u8231\u7126\u70b9 / \u4fdd\u5355\u5230\u671f\u4e0e\u5931\u6548", "\u6765\u81ea\u9a7e\u9a76\u8231\u5b89\u8d23\u9669\u8986\u76d6\u7126\u70b9"));
            list.add(this.focusQueue("preventionFund", "\u8d44\u91d1\u534f\u540c\u5f02\u5e38", this.countFundCoordinationRisk(preventionFundSummary), "\u6761", "\u4f59\u989d\u504f\u4f4e\u7684\u8d44\u91d1\u6c60\u4f1a\u76f4\u63a5\u5f71\u54cd\u6cbb\u7406\u52a8\u4f5c\u843d\u5730\u3002", "/azb/preventionFund", "\u67e5\u770b\u8d44\u91d1\u6c60", this.buildSourceQuery(null, null, null, "fundStatus", "1"), "\u9a7e\u9a76\u8231\u7126\u70b9 / \u4f4e\u4f59\u989d\u8d44\u91d1\u6c60", "\u6765\u81ea\u9a7e\u9a76\u8231\u4e8b\u6545\u9884\u9632\u8d44\u91d1\u7126\u70b9"));
            list.add(this.focusQueue("creditScore", "\u7ea2\u7801\u4f01\u4e1a", this.defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getRedCount()), "\u5bb6", "\u9700\u8981\u7ed3\u5408\u8bc4\u5206\u5224\u65ad\u4fdd\u9669\u534f\u540c\u91cd\u70b9\u3002", "/azb/creditScore", "\u67e5\u770b\u4fe1\u7528\u8bc4\u5206", this.buildSourceQuery(null, null, null, "colorCode", "RED"), "\u9a7e\u9a76\u8231\u7126\u70b9 / \u7ea2\u7801\u4f01\u4e1a", "\u6765\u81ea\u9a7e\u9a76\u8231\u4fe1\u7528\u5206\u5c42\u7126\u70b9"));
            list.add(this.focusQueue("statReport", "\u5df2\u751f\u6210\u62a5\u8868", this.defaultNumber(statReportSummary == null ? null : statReportSummary.getGeneratedCount()), "\u4efd", "\u7ed3\u5408\u533a\u57df\u62a5\u8868\u505a\u6708\u5ea6\u590d\u76d8\u3002", "/azb/statReport", "\u67e5\u770b\u62a5\u8868", this.buildSourceQuery(null, null, null, "reportStatus", "1"), "\u9a7e\u9a76\u8231\u7126\u70b9 / \u5df2\u751f\u6210\u62a5\u8868", "\u6765\u81ea\u9a7e\u9a76\u8231\u533a\u57df\u6cbb\u7406\u7126\u70b9"));
            list.add(this.focusQueue("warning", "\u5f85\u5904\u7f6e\u9884\u8b66", this.defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()), "\u6761", "\u627f\u4fdd\u534f\u540c\u65f6\u540c\u6b65\u56de\u770b\u9690\u60a3\u5904\u7f6e\u65f6\u6548\u548c\u538b\u964d\u538b\u529b\u3002", "/azb/warning", "\u67e5\u770b\u9884\u8b66\u5904\u7f6e", this.buildSourceQuery(null, null, null, "warnStatus", "0"), "\u9a7e\u9a76\u8231\u7126\u70b9 / \u5f85\u5904\u7f6e\u9884\u8b66", "\u6765\u81ea\u9a7e\u9a76\u8231\u98ce\u9669\u8bc1\u636e\u7126\u70b9"));
            list.add(this.focusQueue("device", "\u8bbe\u5907\u5f02\u5e38", this.countDeviceRisk(deviceSummary), "\u53f0", "\u8bbe\u5907\u5f02\u5e38\u7528\u4e8e\u8bc6\u522b\u73b0\u573a\u611f\u77e5\u94fe\u8def\u662f\u5426\u5b8c\u6574\u3002", "/azb/device", "\u67e5\u770b\u8bbe\u5907\u98ce\u9669", this.buildSourceQuery(null, null, null, "authStatus", "0"), "\u9a7e\u9a76\u8231\u7126\u70b9 / \u8bbe\u5907\u5f02\u5e38", "\u6765\u81ea\u9a7e\u9a76\u8231\u98ce\u9669\u8bc1\u636e\u7126\u70b9"));
            list.add(this.focusQueue("heightWorkReport", "\u8fdb\u884c\u4e2d\u9ad8\u5904\u4f5c\u4e1a", this.defaultNumber(heightWorkReportSummary == null ? null : heightWorkReportSummary.getActiveCount()), "\u5355", "\u8fdb\u884c\u4e2d\u9ad8\u5904\u4f5c\u4e1a\u8865\u8db3\u6280\u672f\u9632\u8303\u4e0e\u8bc1\u4e66\u5408\u89c4\u8bc1\u636e\u94fe\u3002", "/azb/heightWorkReport", "\u67e5\u770b\u9ad8\u5904\u4f5c\u4e1a", this.buildSourceQuery(null, null, null, "reportStatus", "0"), "\u9a7e\u9a76\u8231\u7126\u70b9 / \u8fdb\u884c\u4e2d\u9ad8\u5904\u4f5c\u4e1a", "\u6765\u81ea\u9a7e\u9a76\u8231\u6280\u672f\u9632\u8303\u7126\u70b9"));
            return list;
        }
        list.add(this.focusQueue("warning", "\u7ea2\u8b66\u4e0e\u5347\u7ea7\u5904\u7f6e", this.defaultNumber(warningSummary == null ? null : warningSummary.getRedCount()) + this.defaultNumber(warningSummary == null ? null : warningSummary.getUpgradedCount()), "\u6761", "\u5148\u5904\u7406\u9ad8\u7b49\u7ea7\u5de5\u5355\uff0c\u9632\u6b62\u98ce\u9669\u5728\u73b0\u573a\u7ee7\u7eed\u6269\u5927\u3002", "/azb/warning", "\u8fdb\u5165\u9884\u8b66\u4e2d\u5fc3", null, "\u9a7e\u9a76\u8231\u7126\u70b9 / \u7ea2\u8b66\u4e0e\u5347\u7ea7\u5904\u7f6e", "\u6765\u81ea\u9a7e\u9a76\u8231\u9884\u8b66\u5904\u7f6e\u7126\u70b9"));
        list.add(this.focusQueue("device", "\u79bb\u7ebf/\u6545\u969c\u8bbe\u5907", this.defaultNumber(deviceSummary == null ? null : deviceSummary.getLockedOrFaultCount()) + this.defaultNumber(deviceSummary == null ? null : deviceSummary.getUnauthorizedCount()), "\u53f0", "\u8bbe\u5907\u79bb\u7ebf\u4f1a\u76f4\u63a5\u524a\u5f31\u73b0\u573a\u611f\u77e5\u80fd\u529b\u3002", "/azb/device", "\u67e5\u770b\u8bbe\u5907\u53f0\u8d26", this.buildSourceQuery(null, null, null, "authStatus", "0"), "\u9a7e\u9a76\u8231\u7126\u70b9 / \u79bb\u7ebf\u6545\u969c\u8bbe\u5907", "\u6765\u81ea\u9a7e\u9a76\u8231\u8bbe\u5907\u5b89\u5168\u7126\u70b9"));
        list.add(this.focusQueue("heightWorkReport", "\u8fdb\u884c\u4e2d\u9ad8\u5904\u4f5c\u4e1a", this.defaultNumber(heightWorkReportSummary == null ? null : heightWorkReportSummary.getActiveCount()), "\u5355", "\u9700\u8981\u6301\u7eed\u76ef\u9632\u4ecd\u5728\u73b0\u573a\u8fdb\u884c\u7684\u9ad8\u98ce\u9669\u4f5c\u4e1a\u3002", "/azb/heightWorkReport", "\u67e5\u770b\u9ad8\u5904\u4f5c\u4e1a", this.buildSourceQuery(null, null, null, "reportStatus", "0"), "\u9a7e\u9a76\u8231\u7126\u70b9 / \u8fdb\u884c\u4e2d\u9ad8\u5904\u4f5c\u4e1a", "\u6765\u81ea\u9a7e\u9a76\u8231\u6280\u672f\u9632\u8303\u7126\u70b9"));
        list.add(this.focusQueue("injuryEvent", "\u8d85\u671f\u5de5\u4f24\u4e8b\u4ef6", this.defaultNumber(injuryEventSummary == null ? null : injuryEventSummary.getOverdueCount()), "\u8d77", "\u8d85\u671f\u4e8b\u4ef6\u9700\u8981\u56de\u5230\u4e8b\u6545\u5904\u7406\u94fe\u8def\u7ee7\u7eed\u538b\u964d\u3002", "/azb/injuryEvent", "\u67e5\u770b\u5de5\u4f24\u4e8b\u4ef6", null, "\u9a7e\u9a76\u8231\u7126\u70b9 / \u8d85\u671f\u5de5\u4f24\u4e8b\u4ef6", "\u6765\u81ea\u9a7e\u9a76\u8231\u5de5\u4f24\u5904\u7f6e\u7126\u70b9"));
        return list;
    }

    private List<Map<String, Object>> buildAzbWorkflowSteps(String roleView) {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if ("bank".equals(roleView)) {
            list.add(this.workflowStep("\u5148\u770b\u4fe1\u7528\u5206\u5c42", "\u4f18\u5148\u7b5b\u51fa\u7ea2\u7801\u3001D \u7ea7\u548c\u5e73\u5747\u5206\u6301\u7eed\u8d70\u4f4e\u7684\u5bf9\u8c61\u3002"));
            list.add(this.workflowStep("\u518d\u770b\u533a\u57df\u62a5\u8868", "\u7528\u533a\u57df\u62a5\u8868\u9a8c\u8bc1\u91cd\u70b9\u4f01\u4e1a\u53d8\u5316\u662f\u5426\u5177\u5907\u6301\u7eed\u6027\u3002"));
            list.add(this.workflowStep("\u6700\u540e\u505a\u534f\u540c\u590d\u6838", "\u56de\u5230\u91cd\u70b9\u4f01\u4e1a\u548c\u62a5\u8868\u660e\u7ec6\u505a\u6388\u4fe1\u534f\u540c\u5224\u65ad\u3002"));
            return list;
        }
        if ("insurer".equals(roleView)) {
            list.add(this.workflowStep("\u5148\u770b\u6295\u4fdd\u8986\u76d6", "\u5148\u5224\u65ad\u4fdd\u5355\u6c60\u662f\u5426\u5b58\u5728\u5230\u671f\u65ad\u6863\u548c\u8986\u76d6\u4e0d\u8db3\u3002"));
            list.add(this.workflowStep("\u518d\u770b\u4e8b\u6545\u9884\u9632\u8d44\u91d1", "\u5bf9\u4f4e\u4f59\u989d\u548c\u7f3a\u5c11\u51ed\u8bc1\u7684\u8d44\u91d1\u6c60\u4f18\u5148\u590d\u6838\u3002"));
            list.add(this.workflowStep("\u6700\u540e\u8054\u52a8\u4fe1\u7528\u4e0e\u62a5\u8868", "\u7ed3\u5408\u4fe1\u7528\u8bc4\u5206\u548c\u533a\u57df\u62a5\u8868\u5224\u65ad\u672c\u6708\u91cd\u70b9\u670d\u52a1\u4f01\u4e1a\u3002"));
            return list;
        }
        list.add(this.workflowStep("\u5148\u9501\u5b9a\u91cd\u70b9\u533a\u57df\u548c\u4f01\u4e1a", "\u901a\u8fc7\u603b\u89c8\u6307\u6807\u548c\u7126\u70b9\u961f\u5217\u5224\u65ad\u5f53\u524d\u98ce\u9669\u538b\u529b\u6700\u5927\u7684\u4f4d\u7f6e\u3002"));
        list.add(this.workflowStep("\u518d\u76ef\u8bbe\u5907\u4e0e\u9ad8\u5371\u4f5c\u4e1a", "\u4f18\u5148\u5904\u7406\u79bb\u7ebf\u8bbe\u5907\u3001\u8fdb\u884c\u4e2d\u9ad8\u5904\u4f5c\u4e1a\u548c\u73b0\u573a\u5f02\u5e38\u70b9\u4f4d\u3002"));
        list.add(this.workflowStep("\u6700\u540e\u56de\u5230\u9884\u8b66\u4e0e\u4e8b\u6545\u95ed\u73af", "\u538b\u5b9e\u7ea2\u8b66\u3001\u5347\u7ea7\u5de5\u5355\u548c\u8d85\u671f\u5de5\u4f24\u4e8b\u4ef6\u7684\u5904\u7f6e\u65f6\u6548\u3002"));
        return list;
    }

    private List<Map<String, Object>> buildAzbQuickActions(String roleView, String regionCode, String statDate, String statMonth) {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if ("bank".equals(roleView)) {
            return this.buildAzbBankQuickActions(regionCode, statDate, statMonth);
        }
        if ("insurer".equals(roleView)) {
            return this.buildAzbInsurerQuickActions(regionCode, statDate, statMonth);
        }
        if ("bank".equals(roleView)) {
            list.add(this.quickAction("creditScore", "\u4fe1\u7528\u8bc4\u5206", "\u53ea\u8bfb\u770b\u677f", "\u67e5\u770b\u7ea2\u9ec4\u7eff\u7801\u3001\u7b49\u7ea7\u548c\u533a\u57df\u4fe1\u7528\u9762\u3002", "/azb/creditScore", this.buildSourceQuery(regionCode, statDate, statMonth, "colorCode", "RED"), "\u9996\u9875\u91cd\u70b9\u5bf9\u8c61", "\u6765\u81ea\u9996\u9875\u4fe1\u7528\u91cd\u70b9\u5bf9\u8c61\u805a\u5408"));
            list.add(this.quickAction("statReport", "\u7edf\u8ba1\u62a5\u8868", "\u533a\u57df\u590d\u6838", "\u4ece\u6708\u62a5\u5feb\u901f\u8fdb\u5165\u533a\u57df\u548c\u4f01\u4e1a\u53d8\u5316\u660e\u7ec6\u3002", "/azb/statReport", this.buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "1"), "\u9996\u9875\u533a\u57df\u6cbb\u7406", "\u6765\u81ea\u9996\u9875\u533a\u57df\u6cbb\u7406\u805a\u5408"));
            return list;
        }
        if ("insurer".equals(roleView)) {
            list.add(this.quickAction("aqInsurance", "\u5b89\u8d23\u9669\u76d1\u7ba1", "\u4fdd\u5355\u6c60", "\u67e5\u770b\u8986\u76d6\u7387\u3001\u5230\u671f\u98ce\u9669\u548c\u4fdd\u5355\u56de\u5199\u72b6\u6001\u3002", "/azb/aqInsurance", this.buildSourceQuery(regionCode, statDate, statMonth, "policyStatus", "2"), "\u9996\u9875\u91cd\u70b9\u5bf9\u8c61", "\u6765\u81ea\u9996\u9875\u5b89\u8d23\u9669\u8986\u76d6\u805a\u5408"));
            list.add(this.quickAction("preventionFund", "\u4e8b\u6545\u9884\u9632\u8d44\u91d1", "\u8d44\u91d1\u6c60", "\u8ddf\u8e2a\u4f59\u989d\u3001\u4f7f\u7528\u72b6\u6001\u548c\u51ed\u8bc1\u7559\u75d5\u3002", "/azb/preventionFund", this.buildSourceQuery(regionCode, statDate, statMonth, "fundStatus", "1"), "\u9996\u9875\u91cd\u70b9\u5bf9\u8c61", "\u6765\u81ea\u9996\u9875\u4e8b\u6545\u9884\u9632\u8d44\u91d1\u805a\u5408"));
            list.add(this.quickAction("creditScore", "\u4fe1\u7528\u8bc4\u5206", "\u98ce\u9669\u753b\u50cf", "\u67e5\u770b\u7ea2\u7801\u4f01\u4e1a\u548c\u5e73\u5747\u5206\u8d70\u52bf\u3002", "/azb/creditScore", this.buildSourceQuery(regionCode, statDate, statMonth, "colorCode", "RED"), "\u9996\u9875\u91cd\u70b9\u5bf9\u8c61", "\u6765\u81ea\u9996\u9875\u4fe1\u7528\u753b\u50cf\u805a\u5408"));
            list.add(this.quickAction("statReport", "\u7edf\u8ba1\u62a5\u8868", "\u6708\u5ea6\u590d\u76d8", "\u6c47\u603b\u533a\u57df\u6295\u4fdd\u548c\u6cbb\u7406\u7ed3\u679c\u3002", "/azb/statReport", this.buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "1"), "\u9996\u9875\u533a\u57df\u6cbb\u7406", "\u6765\u81ea\u9996\u9875\u533a\u57df\u6cbb\u7406\u805a\u5408"));
            return list;
        }
        list.add(this.quickAction("warning", "\u9884\u8b66\u4e2d\u5fc3", "\u95ed\u73af\u5904\u7f6e", "\u76f4\u63a5\u8fdb\u5165\u98ce\u9669\u5de5\u5355\uff0c\u7ee7\u7eed\u505a\u5904\u7f6e\u548c\u5347\u7ea7\u3002", "/azb/warning", this.buildSourceQuery(regionCode, statDate, statMonth, "warnStatus", "0"), "\u9996\u9875\u91cd\u70b9\u5bf9\u8c61", "\u6765\u81ea\u9996\u9875\u9884\u8b66\u5904\u7f6e\u805a\u5408"));
        list.add(this.quickAction("device", "\u8bbe\u5907\u7ba1\u7406", "\u5728\u7ebf\u6cbb\u7406", "\u67e5\u770b\u79bb\u7ebf\u3001\u6545\u969c\u3001\u672a\u6388\u6743\u8bbe\u5907\u3002", "/azb/device", this.buildSourceQuery(regionCode, statDate, statMonth, "authStatus", "0"), "\u9996\u9875\u8bbe\u5907\u5b89\u5168", "\u6765\u81ea\u9996\u9875\u8bbe\u5907\u5b89\u5168\u805a\u5408"));
        list.add(this.quickAction("heightWorkReport", "\u9ad8\u5904\u4f5c\u4e1a", "\u73b0\u573a\u4f5c\u4e1a", "\u76ef\u9632\u8fdb\u884c\u4e2d\u4f5c\u4e1a\u548c\u8bc1\u4e66\u5f02\u5e38\u4eba\u5458\u3002", "/azb/heightWorkReport", this.buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "0"), "\u9996\u9875\u6280\u672f\u9632\u8303", "\u6765\u81ea\u9996\u9875\u6280\u672f\u9632\u8303\u805a\u5408"));
        list.add(this.quickAction("aqInsurance", "\u5b89\u8d23\u9669\u76d1\u7ba1", "\u8986\u76d6\u590d\u6838", "\u4ea4\u53c9\u5224\u65ad\u73b0\u573a\u4f01\u4e1a\u6295\u4fdd\u8986\u76d6\u60c5\u51b5\u3002", "/azb/aqInsurance", this.buildSourceQuery(regionCode, statDate, statMonth, "policyStatus", "2"), "\u9996\u9875\u5b89\u8d23\u9669\u8986\u76d6", "\u6765\u81ea\u9996\u9875\u5b89\u8d23\u9669\u8986\u76d6\u805a\u5408"));
        list.add(this.quickAction("creditScore", "\u4fe1\u7528\u8bc4\u5206", "\u98ce\u9669\u5206\u5c42", "\u67e5\u770b\u7ea2\u7801\u3001D \u7ea7\u548c\u5c3e\u90e8\u5bf9\u8c61\uff0c\u5224\u65ad\u5f53\u524d\u91cd\u70b9\u538b\u964d\u9879\u76ee\u3002", "/azb/creditScore", this.buildSourceQuery(regionCode, statDate, statMonth, "colorCode", "RED"), "\u9996\u9875\u4fe1\u7528\u5206\u5c42", "\u6765\u81ea\u9996\u9875\u4fe1\u7528\u5206\u5c42\u805a\u5408"));
        list.add(this.quickAction("statReport", "\u7edf\u8ba1\u62a5\u8868", "\u533a\u57df\u6cbb\u7406", "\u4ece\u9996\u9875\u76f4\u63a5\u8fdb\u5165\u6cbb\u7406\u6708\u62a5\u548c\u533a\u57df\u6001\u52bf\u590d\u6838\u3002", "/azb/statReport", this.buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "1"), "\u9996\u9875\u533a\u57df\u6cbb\u7406", "\u6765\u81ea\u9996\u9875\u533a\u57df\u6cbb\u7406\u805a\u5408"));
        return list;
    }

    private List<Map<String, Object>> buildAzbHintTags(YgbAzbCockpitDashboard dashboard, String roleView) {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbDeviceSummary deviceSummary = dashboard.getDeviceSummary();
        YgbHeightWorkReportSummary heightWorkReportSummary = dashboard.getHeightWorkReportSummary();
        YgbAqInsuranceSummary aqInsuranceSummary = dashboard.getAqInsuranceSummary();
        YgbPreventionFundSummary preventionFundSummary = dashboard.getPreventionFundSummary();
        YgbCreditScoreSummary creditScoreSummary = dashboard.getCreditScoreSummary();
        YgbStatReportSummary statReportSummary = dashboard.getStatReportSummary();
        if (this.defaultNumber(warningSummary == null ? null : warningSummary.getRedCount()) > 0) {
            list.add(this.hintTag("\u5b58\u5728 " + this.defaultNumber(warningSummary.getRedCount()) + " \u6761\u7ea2\u8b66\uff0c\u4f18\u5148\u538b\u5b9e\u9ad8\u7b49\u7ea7\u5de5\u5355\u3002", "danger"));
        }
        if (this.defaultNumber(deviceSummary == null ? null : deviceSummary.getUnauthorizedCount()) > 0) {
            list.add(this.hintTag("\u4ecd\u6709 " + this.defaultNumber(deviceSummary.getUnauthorizedCount()) + " \u53f0\u672a\u6388\u6743\u8bbe\u5907\uff0c\u73b0\u573a\u611f\u77e5\u94fe\u8def\u4e0d\u5b8c\u6574\u3002", "warning"));
        }
        if (this.defaultNumber(heightWorkReportSummary == null ? null : heightWorkReportSummary.getInvalidWorkerCount()) > 0) {
            list.add(this.hintTag("\u9ad8\u5904\u4f5c\u4e1a\u5b58\u5728 " + this.defaultNumber(heightWorkReportSummary.getInvalidWorkerCount()) + " \u540d\u8bc1\u4e66\u5f02\u5e38\u4eba\u5458\u3002", "warning"));
        }
        if (this.defaultNumber(aqInsuranceSummary == null ? null : aqInsuranceSummary.getRiskCount()) > 0 && !"bank".equals(roleView)) {
            list.add(this.hintTag("\u4fdd\u5355\u6c60\u4e2d\u6709 " + this.defaultNumber(aqInsuranceSummary.getRiskCount()) + " \u5355\u5230\u671f\u98ce\u9669\u3002", "info"));
        }
        if (this.defaultNumber(preventionFundSummary == null ? null : preventionFundSummary.getLowBalanceCount()) > 0 && "insurer".equals(roleView)) {
            list.add(this.hintTag("\u4e8b\u6545\u9884\u9632\u8d44\u91d1\u5b58\u5728 " + this.defaultNumber(preventionFundSummary.getLowBalanceCount()) + " \u6761\u4f4e\u4f59\u989d\u8bb0\u5f55\u3002", "warning"));
        }
        if (this.defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getRedCount()) > 0) {
            list.add(this.hintTag("\u4fe1\u7528\u753b\u50cf\u4e2d\u6709 " + this.defaultNumber(creditScoreSummary.getRedCount()) + " \u5bb6\u7ea2\u7801\u4f01\u4e1a\u3002", "danger"));
        }
        if (this.defaultNumber(statReportSummary == null ? null : statReportSummary.getDraftCount()) > 0) {
            list.add(this.hintTag("\u5f53\u524d\u6708\u4efd\u4ecd\u6709 " + this.defaultNumber(statReportSummary.getDraftCount()) + " \u4efd\u8349\u7a3f\u62a5\u8868\u672a\u5f62\u6210\u6b63\u5f0f\u7ed3\u679c\u3002", "info"));
        }
        return list;
    }

    private List<Map<String, Object>> buildAzbQueueSections(YgbAzbCockpitDashboard dashboard, String roleView, String regionCode, String statDate, String statMonth) {
        ArrayList<Map<String, Object>> sections = new ArrayList<Map<String, Object>>();
        if ("bank".equals(roleView)) {
            return this.buildAzbBankQueueSections(dashboard, regionCode, statDate, statMonth);
        }
        if ("insurer".equals(roleView)) {
            return this.buildAzbInsurerQueueSections(dashboard, regionCode, statDate, statMonth);
        }
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbDeviceSummary deviceSummary = dashboard.getDeviceSummary();
        YgbHeightWorkReportSummary heightWorkReportSummary = dashboard.getHeightWorkReportSummary();
        YgbAqInsuranceSummary aqInsuranceSummary = dashboard.getAqInsuranceSummary();
        YgbCreditScoreSummary creditScoreSummary = dashboard.getCreditScoreSummary();
        YgbStatReportSummary statReportSummary = dashboard.getStatReportSummary();
        ArrayList<Map<String, Object>> objectItems = new ArrayList<Map<String, Object>>();
        objectItems.add(this.queueItem("warning", "\u9884\u8b66\u5f85\u5904\u7f6e\u5bf9\u8c61", "\u91cd\u70b9\u5bf9\u8c61\u805a\u5408", this.defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()), "\u6761", "\u540c\u4e00\u5bf9\u8c61\u53ef\u4ece\u9996\u9875\u8fdb\u5165 warning \u5e38\u89c4\u5217\u8868\u6001\u3002", "\u7ee7\u7eed\u5904\u7f6e", "warning", "azb", this.buildSourceQuery(regionCode, statDate, statMonth, "warnStatus", "0"), "\u9996\u9875\u91cd\u70b9\u5bf9\u8c61", "\u6765\u81ea\u9996\u9875\u91cd\u70b9\u5bf9\u8c61\u805a\u5408"));
        objectItems.add(this.queueItem("device", "\u8bbe\u5907\u5f02\u5e38\u5bf9\u8c61", "\u8bbe\u5907\u5b89\u5168\u805a\u5408", this.defaultNumber(deviceSummary == null ? null : deviceSummary.getLockedOrFaultCount()) + this.defaultNumber(deviceSummary == null ? null : deviceSummary.getUnauthorizedCount()), "\u53f0", "\u652f\u6301\u5bf9\u8c61\u7ea7\u4e0b\u94bb\u5230 device \u5e38\u89c4\u5217\u8868\u6001\u3002", "\u7ee7\u7eed\u6838\u67e5", "device", "azb", this.buildSourceQuery(regionCode, statDate, statMonth, "authStatus", "0"), "\u9996\u9875\u91cd\u70b9\u5bf9\u8c61", "\u6765\u81ea\u9996\u9875\u8bbe\u5907\u5b89\u5168\u805a\u5408"));
        objectItems.add(this.queueItem("aqInsurance", "\u5b89\u8d23\u9669\u98ce\u9669\u5bf9\u8c61", "\u5b89\u8d23\u9669\u8986\u76d6\u805a\u5408", this.defaultNumber(aqInsuranceSummary == null ? null : aqInsuranceSummary.getRiskCount()), "\u5355", "\u652f\u6301\u5bf9\u8c61\u7ea7\u4e0b\u94bb\u5230 aqInsurance \u5e38\u89c4\u5217\u8868\u6001\u3002", "\u7ee7\u7eed\u590d\u6838", "aqInsurance", "azb", this.buildSourceQuery(regionCode, statDate, statMonth, "policyStatus", "2"), "\u9996\u9875\u91cd\u70b9\u5bf9\u8c61", "\u6765\u81ea\u9996\u9875\u5b89\u8d23\u9669\u8986\u76d6\u805a\u5408"));
        sections.add(this.queueSection("focusObject", "\u91cd\u70b9\u5bf9\u8c61\u961f\u5217", "\u8f93\u51fa\u98ce\u9669\u5bf9\u8c61\u6458\u8981\u3001\u5bf9\u8c61\u7c7b\u578b\u3001\u98ce\u9669\u6807\u7b7e\u548c\u4e0b\u94bb\u76ee\u6807\u3002", objectItems));
        ArrayList<Map<String, Object>> governanceItems = new ArrayList<Map<String, Object>>();
        governanceItems.add(this.queueItem("heightWork", "\u9ad8\u5904\u4f5c\u4e1a\u8fdb\u884c\u4e2d", "\u6280\u672f\u9632\u8303\u805a\u5408", this.defaultNumber(heightWorkReportSummary == null ? null : heightWorkReportSummary.getActiveCount()), "\u5355", "\u4ece\u9996\u9875\u76f4\u63a5\u8fdb\u5165\u9ad8\u5904\u4f5c\u4e1a\u5e38\u89c4\u5217\u8868\u6001\u3002", "\u7ee7\u7eed\u76ef\u9632", "heightWorkReport", "azb", this.buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "0"), "\u9996\u9875\u6280\u672f\u9632\u8303", "\u6765\u81ea\u9996\u9875\u6280\u672f\u9632\u8303\u805a\u5408"));
        governanceItems.add(this.queueItem("creditScore", "\u7ea2\u7801\u4e0e\u5c3e\u90e8\u5bf9\u8c61", "\u4fe1\u7528\u5206\u5c42\u805a\u5408", this.defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getRedCount()) + this.defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getDCount()), "\u5bb6", "\u652f\u6301\u4ece\u9996\u9875\u8fdb\u5165 creditScore \u5e38\u89c4\u5217\u8868\u6001\u3002", "\u7ee7\u7eed\u5206\u5c42", "creditScore", "azb", this.buildSourceQuery(regionCode, statDate, statMonth, "colorCode", "RED"), "\u9996\u9875\u4fe1\u7528\u5206\u5c42", "\u6765\u81ea\u9996\u9875\u4fe1\u7528\u5206\u5c42\u805a\u5408"));
        governanceItems.add(this.queueItem("statReport", "\u533a\u57df\u6cbb\u7406\u6708\u62a5", "\u533a\u57df\u6001\u52bf\u805a\u5408", this.defaultNumber(statReportSummary == null ? null : statReportSummary.getGeneratedCount()), "\u4efd", "\u4ece\u9996\u9875\u76f4\u63a5\u8fdb\u5165 statReport \u5e38\u89c4\u5217\u8868\u6001\u3002", "\u7ee7\u7eed\u590d\u6838", "statReport", "azb", this.buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "1"), "\u9996\u9875\u533a\u57df\u6cbb\u7406", "\u6765\u81ea\u9996\u9875\u533a\u57df\u6cbb\u7406\u805a\u5408"));
        sections.add(this.queueSection("governance", "\u6cbb\u7406\u7ef4\u5ea6\u961f\u5217", "\u540c\u4e00\u5bf9\u8c61\u652f\u6301\u4e0b\u94bb\u5230 warning\u3001device\u3001aqInsurance\u3001statReport\u3001creditScore\u3002", governanceItems));
        if ("bank".equals(roleView)) {
            sections.add(this.queueSection("bankOnly", "\u534f\u540c\u590d\u6838\u961f\u5217", "\u4fdd\u7559\u53ea\u8bfb\u534f\u540c\u53e3\u5f84\uff0c\u4e0d\u6df7\u5165\u4f01\u4e1a\u529e\u7406\u8bed\u4e49\u3002", governanceItems));
        }
        return sections;
    }

    private List<Map<String, Object>> buildAzbHomeExplanation(YgbAzbCockpitDashboard dashboard, String roleView, String regionCode, String statMonth) {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if ("insurer".equals(roleView)) {
            return this.buildAzbInsurerHomeExplanation(dashboard, regionCode, statMonth);
        }
        YgbAqInsuranceSummary aqInsuranceSummary = dashboard.getAqInsuranceSummary();
        YgbDeviceSummary deviceSummary = dashboard.getDeviceSummary();
        YgbHeightWorkReportSummary heightWorkReportSummary = dashboard.getHeightWorkReportSummary();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbCreditScoreSummary creditScoreSummary = dashboard.getCreditScoreSummary();
        YgbStatReportSummary statReportSummary = dashboard.getStatReportSummary();
        if ("bank".equals(roleView)) {
            list.add(this.explanationItem("\u4fe1\u7528\u5206\u5c42", this.defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getRedCount()) + this.defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getDCount()), "0", "\u94f6\u884c\u53ea\u8bfb\u534f\u540c\u4f18\u5148\u770b\u7ea2\u7801\u3001D \u7ea7\u548c\u5c3e\u90e8\u5bf9\u8c61\uff0c\u518d\u51b3\u5b9a\u662f\u5426\u7ee7\u7eed\u56de\u5230\u533a\u57df\u6708\u62a5\u590d\u6838\u53d8\u5316\u8d8b\u52bf\u3002", "creditScore", "creditScore", this.buildSourceQuery(regionCode, null, statMonth, "colorCode", "RED"), "\u9996\u9875\u534f\u540c\u590d\u6838\u89e3\u91ca", "\u6765\u6e90\u4e8e\u4fe1\u7528\u5206\u5c42\u805a\u5408"));
            list.add(this.explanationItem("\u533a\u57df\u6cbb\u7406\u6210\u6548", this.defaultNumber(statReportSummary == null ? null : statReportSummary.getGeneratedCount()), ">=1", "\u94f6\u884c\u534f\u540c\u590d\u6838\u6309\u533a\u57df\u6708\u62a5\u548c\u751f\u6210\u7ed3\u679c\u7ec4\u7ec7\uff0c\u4e0d\u6df7\u5165\u4f01\u4e1a\u529e\u7406\u4f18\u5148\u63cf\u8ff0\u3002", "statReport", "statReport", this.buildSourceQuery(regionCode, null, statMonth, "reportStatus", "1"), "\u9996\u9875\u534f\u540c\u590d\u6838\u89e3\u91ca", "\u6765\u6e90\u4e8e\u533a\u57df\u6cbb\u7406\u6708\u62a5"));
            list.add(this.explanationItem("\u98ce\u9669\u590d\u6838\u987a\u5e8f", this.defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getRedCount()), "0", "\u540c\u4e00\u4f01\u4e1a\u5148\u770b\u4fe1\u7528\u5c3e\u90e8\u5bf9\u8c61\uff0c\u518d\u770b\u6708\u62a5\u6001\u52bf\uff0c\u4fdd\u6301\u53ea\u8bfb\u534f\u540c\u590d\u6838\u987a\u5e8f\u3002", "creditScore", "creditScore", this.buildSourceQuery(regionCode, null, statMonth, "colorCode", "RED"), "\u9996\u9875\u534f\u540c\u590d\u6838\u89e3\u91ca", "\u6765\u6e90\u4e8e\u94f6\u884c\u53ea\u8bfb\u534f\u540c\u53e3\u5f84"));
            return list;
        }
        list.add(this.explanationItem("\u5b89\u8d23\u9669\u8986\u76d6", this.formatDecimalText(aqInsuranceSummary == null ? null : aqInsuranceSummary.getCoverageRate()), "100", "6.1 \u53e3\u5f84\u4f18\u5148\u89e3\u91ca\u5b89\u8d23\u9669\u8986\u76d6\u4e0e\u5230\u671f\u98ce\u9669\uff0c\u4e0d\u51fa\u73b0\u4f01\u4e1a\u529e\u7406\u94fe\u4f18\u5148\u63cf\u8ff0\u3002", "aqInsurance", "aqInsurance", this.buildSourceQuery(regionCode, null, statMonth, "policyStatus", "2"), "\u9996\u9875\u6cbb\u7406\u89e3\u91ca", "\u6765\u6e90\u4e8e\u5b89\u8d23\u9669\u8986\u76d6\u805a\u5408"));
        list.add(this.explanationItem("\u8bbe\u5907\u5b89\u5168", this.defaultNumber(deviceSummary == null ? null : deviceSummary.getLockedOrFaultCount()) + this.defaultNumber(deviceSummary == null ? null : deviceSummary.getUnauthorizedCount()), "0", "\u5148\u770b\u79bb\u7ebf\u3001\u6545\u969c\u4e0e\u672a\u6388\u6743\u8bbe\u5907\uff0c\u518d\u770b\u6cbb\u7406\u94fe\u95ed\u73af\u52a8\u4f5c\u3002", "device", "device", this.buildSourceQuery(regionCode, null, statMonth, "authStatus", "0"), "\u9996\u9875\u6cbb\u7406\u89e3\u91ca", "\u6765\u6e90\u4e8e\u8bbe\u5907\u5b89\u5168\u805a\u5408"));
        list.add(this.explanationItem("\u6280\u672f\u9632\u8303", this.defaultNumber(heightWorkReportSummary == null ? null : heightWorkReportSummary.getActiveCount()), "0", "\u9ad8\u5904\u4f5c\u4e1a\u89e3\u91ca\u6cbf\u6280\u672f\u9632\u8303\u94fe\u7ec4\u7ec7\uff0c\u4e0d\u6df7\u7528\u4f01\u4e1a\u529e\u7406\u6458\u8981\u3002", "heightWorkReport", "heightWorkReport", this.buildSourceQuery(regionCode, null, statMonth, "reportStatus", "0"), "\u9996\u9875\u6cbb\u7406\u89e3\u91ca", "\u6765\u6e90\u4e8e\u6280\u672f\u9632\u8303\u805a\u5408"));
        list.add(this.explanationItem("\u9884\u8b66\u5904\u7f6e", this.defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()), "0", "\u9996\u9875\u6458\u8981\u7a81\u51fa\u9884\u8b66\u5904\u7f6e\u4e0e\u9690\u60a3\u6574\u6539\u65f6\u6548\u3002", "warning", "warning", this.buildSourceQuery(regionCode, null, statMonth, "warnStatus", "0"), "\u9996\u9875\u6cbb\u7406\u89e3\u91ca", "\u6765\u6e90\u4e8e\u9884\u8b66\u95ed\u73af\u805a\u5408"));
        list.add(this.explanationItem("\u533a\u57df\u6cbb\u7406\u6210\u6548", this.defaultNumber(statReportSummary == null ? null : statReportSummary.getGeneratedCount()), ">=1", "\u6cbb\u7406\u6708\u62a5\u548c\u533a\u57df\u6001\u52bf\u6309 6.1 \u76d1\u7ba1\u89e3\u91ca\u94fe\u56fa\u5b9a\u6392\u5e8f\u3002", "statReport", "statReport", this.buildSourceQuery(regionCode, null, statMonth, "reportStatus", "1"), "\u9996\u9875\u6cbb\u7406\u89e3\u91ca", "\u6765\u6e90\u4e8e\u533a\u57df\u6cbb\u7406\u6708\u62a5"));
        if (!"bank".equals(roleView)) {
            list.add(this.explanationItem("\u4fe1\u7528\u5206\u5c42", this.defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getRedCount()), "0", "\u5c3e\u90e8\u5bf9\u8c61\u548c\u98ce\u9669\u7b49\u7ea7\u7ee7\u7eed\u843d\u5230\u6cbb\u7406\u94fe\u4e0e\u53ea\u8bfb\u534f\u540c\u6a21\u5757\u3002", "creditScore", "creditScore", this.buildSourceQuery(regionCode, null, statMonth, "colorCode", "RED"), "\u9996\u9875\u6cbb\u7406\u89e3\u91ca", "\u6765\u6e90\u4e8e\u4fe1\u7528\u5206\u5c42\u805a\u5408"));
        }
        return list;
    }

    private List<Map<String, Object>> buildAzbFocusPanels(YgbAzbCockpitDashboard dashboard, String roleView, String regionCode, String statMonth) {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> item : this.buildAzbHomeExplanation(dashboard, roleView, regionCode, statMonth)) {
            list.add(this.focusPanel(String.valueOf(item.get("dimensionName")), String.valueOf(item.get("summary")), String.valueOf(item.get("recommendModule")), this.mapValue(item.get("defaultQuery")), String.valueOf(item.get("sourceLabel")), String.valueOf(item.get("sourceDescription"))));
            if (list.size() < 3) continue;
            break;
        }
        return list;
    }

    private String buildAzbHomeSummary(YgbAzbCockpitDashboard dashboard, String roleView) {
        String roleSummary = this.resolveAzbRoleHomeSummary(dashboard, roleView);
        if (StringUtils.isNotEmpty((String)roleSummary)) {
            return roleSummary;
        }
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbDeviceSummary deviceSummary = dashboard.getDeviceSummary();
        YgbAqInsuranceSummary aqInsuranceSummary = dashboard.getAqInsuranceSummary();
        YgbCreditScoreSummary creditScoreSummary = dashboard.getCreditScoreSummary();
        if ("bank".equals(roleView)) {
            return "\u5f53\u524d\u91cd\u70b9\uff1a\u7ea2\u7801\u4f01\u4e1a " + this.defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getRedCount()) + " \u5bb6\uff0c\u533a\u57df\u6708\u62a5 " + this.defaultNumber(dashboard.getStatReportSummary() == null ? null : dashboard.getStatReportSummary().getGeneratedCount()) + " \u4efd\u3002";
        }
        return "\u5f53\u524d\u91cd\u70b9\uff1a\u5f85\u5904\u7f6e\u9884\u8b66 " + this.defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()) + " \u6761\uff0c\u8bbe\u5907\u5f02\u5e38 " + (this.defaultNumber(deviceSummary == null ? null : deviceSummary.getLockedOrFaultCount()) + this.defaultNumber(deviceSummary == null ? null : deviceSummary.getUnauthorizedCount())) + " \u53f0\uff0c\u5b89\u8d23\u9669\u98ce\u9669 " + this.defaultNumber(aqInsuranceSummary == null ? null : aqInsuranceSummary.getRiskCount()) + " \u5355\u3002";
    }

    private List<Map<String, Object>> buildYgbSupplementQuickActions(String regionCode, String statDate, String statMonth) {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        list.add(this.quickAction("statReport", "\u7edf\u8ba1\u62a5\u8868", "\u6708\u62a5\u5f52\u6863", "\u76f4\u63a5\u8fdb\u5165\u529e\u7406\u6708\u62a5\u4e0e\u5f52\u6863\u7ed3\u679c\u5217\u8868\uff0c\u4fdd\u6301\u9996\u9875\u6708\u4efd\u6765\u6e90\u6761\u4ef6\u3002", this.modulePath("ygb", "statReport"), this.buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "0"), "\u9996\u9875\u6708\u62a5\u5f52\u6863", "\u6765\u81ea\u9996\u9875\u6708\u4efd\u7ef4\u5ea6\u805a\u5408"));
        return list;
    }

    private void appendYgbSupplementQueueSections(List<Map<String, Object>> sections, YgbWorkbenchDashboard dashboard, String regionCode, String statDate, String statMonth) {
        if (sections == null || dashboard == null) {
            return;
        }
        YgbStatReportSummary statReportSummary = dashboard.getStatReportSummary();
        YgbUninsuredListSummary uninsuredListSummary = dashboard.getUninsuredListSummary();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        Map<String, Object> enterpriseSection = this.findQueueSection(sections, "enterprise");
        this.appendQueueItemIfMissing(enterpriseSection, "enterpriseReportDraft", this.queueItem("enterpriseReportDraft", "\u6708\u62a5\u5f85\u5f52\u6863", "\u4f01\u4e1a\u7ef4\u5ea6\u8054\u52a8\u6765\u6e90", this.defaultNumber(statReportSummary == null ? null : statReportSummary.getDraftCount()), "\u4efd", "\u4f01\u4e1a\u7ef4\u5ea6\u9700\u8981\u540c\u6b65\u5e26\u51fa\u529e\u7406\u6708\u62a5\u72b6\u6001\uff0c\u907f\u514d\u9996\u9875\u53ea\u5269\u5f85\u529e\u548c\u5f02\u5e38\u3002", "\u7ee7\u7eed\u5f52\u6863", "statReport", "ygb", this.buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "0"), "\u9996\u9875\u4f01\u4e1a\u529e\u7406", "\u6765\u81ea\u9996\u9875\u4f01\u4e1a\u7ef4\u5ea6\u805a\u5408"));
        Map<String, Object> monthSection = this.findQueueSection(sections, "month");
        this.appendQueueItemIfMissing(monthSection, "monthUninsuredPending", this.queueItem("monthUninsuredPending", "\u6269\u9762\u51cf\u635f\u6574\u6539", "\u6708\u4efd\u6269\u9762\u6458\u8981", this.defaultNumber(uninsuredListSummary == null ? null : uninsuredListSummary.getPendingCount()), "\u6761", "\u6309\u7edf\u8ba1\u6708\u8f93\u51fa\u6269\u9762\u51cf\u635f\u6574\u6539\u5bf9\u8c61\uff0c\u4fdd\u6301\u9996\u9875\u5230\u6574\u6539\u94fe\u7684\u6765\u6e90\u900f\u4f20\u3002", "\u7ee7\u7eed\u6574\u6539", "uninsuredList", "ygb", this.buildSourceQuery(regionCode, statDate, statMonth, "disposalStatus", "0"), "\u9996\u9875\u6708\u4efd\u805a\u5408", "\u6765\u81ea\u9996\u9875\u6708\u4efd\u7ef4\u5ea6\u805a\u5408"));
        this.appendQueueItemIfMissing(monthSection, "monthWarningPending", this.queueItem("monthWarningPending", "\u9884\u8b66\u5f85\u5904\u7f6e", "\u6708\u4efd\u9884\u8b66\u6458\u8981", this.defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()), "\u6761", "\u6309\u7edf\u8ba1\u6708\u8f93\u51fa\u9884\u8b66\u5f85\u5904\u7f6e\u91cf\uff0c\u9996\u9875\u4e0d\u518d\u81ea\u884c\u731c\u6d4b\u6708\u4efd\u98ce\u9669\u4f18\u5148\u9879\u3002", "\u7ee7\u7eed\u5904\u7f6e", "warning", "ygb", this.buildSourceQuery(regionCode, statDate, statMonth, "warnStatus", "0"), "\u9996\u9875\u6708\u4efd\u805a\u5408", "\u6765\u81ea\u9996\u9875\u6708\u4efd\u7ef4\u5ea6\u805a\u5408"));
    }

    private String buildEnhancedYgbHomeSummary(YgbWorkbenchDashboard dashboard) {
        if (dashboard == null) {
            return "";
        }
        YgbContractSummary contractSummary = dashboard.getContractSummary();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbStatReportSummary statReportSummary = dashboard.getStatReportSummary();
        YgbSalaryDetailSummary salaryDetailSummary = dashboard.getSalaryDetailSummary();
        YgbSocialPaymentSummary socialPaymentSummary = dashboard.getSocialPaymentSummary();
        YgbTaxCompareSummary taxCompareSummary = dashboard.getTaxCompareSummary();
        YgbUninsuredListSummary uninsuredListSummary = dashboard.getUninsuredListSummary();
        return "\u4f01\u4e1a\u7ef4\u5ea6\uff1a\u5408\u540c\u5f85\u5907\u6848 " + this.defaultNumber(contractSummary == null ? null : Integer.valueOf(contractSummary.getPendingCount())) + " \u4efd\u3001\u5f85\u5904\u7f6e\u9884\u8b66 " + this.defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()) + " \u6761\u3001\u6708\u62a5\u5f85\u5f52\u6863 " + this.defaultNumber(statReportSummary == null ? null : statReportSummary.getDraftCount()) + " \u4efd\uff1b\u6708\u4efd\u7ef4\u5ea6\uff1a\u5de5\u8d44\u5931\u8d25 " + this.defaultNumber(salaryDetailSummary == null ? null : Integer.valueOf(salaryDetailSummary.getFailedCount())) + " \u6761\u3001\u793e\u4fdd\u6b20\u8d39 " + this.defaultNumber(socialPaymentSummary == null ? null : Integer.valueOf(socialPaymentSummary.getOverdueCount())) + " \u6761\u3001\u4e2a\u7a0e\u5f02\u5e38 " + this.defaultNumber(taxCompareSummary == null ? null : Integer.valueOf(taxCompareSummary.getAbnormalCount())) + " \u6761\u3001\u6269\u9762\u51cf\u635f " + this.defaultNumber(uninsuredListSummary == null ? null : uninsuredListSummary.getPendingCount()) + " \u6761\uff1b\u4f18\u5148\u7ea7\u7ef4\u5ea6\uff1a\u9884\u8b66\u95ed\u73af\u548c\u6708\u62a5\u5f52\u6863\u7ee7\u7eed\u6309\u63a5\u53e3\u961f\u5217\u987a\u5e8f\u627f\u63a5\u3002";
    }

    private List<Map<String, Object>> buildAzbBankQuickActions(String regionCode, String statDate, String statMonth) {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        list.add(this.quickAction("creditScore", "\u4fe1\u7528\u8bc4\u5206", "\u534f\u540c\u590d\u6838", "\u4f18\u5148\u67e5\u770b\u7ea2\u7801\u3001D \u7ea7\u548c\u5c3e\u90e8\u5bf9\u8c61\uff0c\u4fdd\u6301\u94f6\u884c\u534f\u540c\u53ea\u8bfb\u53e3\u5f84\u3002", this.modulePath("azb", "creditScore"), this.buildSourceQuery(regionCode, statDate, statMonth, "colorCode", "RED"), "\u9996\u9875\u534f\u540c\u590d\u6838", "\u6765\u81ea\u9996\u9875\u534f\u540c\u590d\u6838\u805a\u5408"));
        list.add(this.quickAction("statReport", "\u7edf\u8ba1\u62a5\u8868", "\u533a\u57df\u590d\u6838", "\u76f4\u63a5\u8fdb\u5165\u533a\u57df\u6001\u52bf\u4e0e\u6cbb\u7406\u6708\u62a5\u5e38\u89c4\u5217\u8868\uff0c\u4fdd\u6301\u9996\u9875\u6765\u6e90\u6761\u4ef6\u3002", this.modulePath("azb", "statReport"), this.buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "1"), "\u9996\u9875\u534f\u540c\u590d\u6838", "\u6765\u81ea\u9996\u9875\u534f\u540c\u590d\u6838\u805a\u5408"));
        list.add(this.quickAction("aqInsurance", "\u5b89\u8d23\u9669\u76d1\u7ba1", "\u98ce\u9669\u8bc1\u636e", "\u67e5\u770b\u4fdd\u5355\u5230\u671f\u4e0e\u5931\u6548\u5bf9\u8c61\uff0c\u4f5c\u4e3a\u534f\u540c\u590d\u6838\u7684\u8bc1\u636e\u5165\u53e3\u3002", this.modulePath("azb", "aqInsurance"), this.buildSourceQuery(regionCode, statDate, statMonth, "policyStatus", "2"), "\u9996\u9875\u5b89\u8d23\u9669\u8986\u76d6", "\u6765\u81ea\u9996\u9875\u98ce\u9669\u8bc1\u636e\u805a\u5408"));
        list.add(this.quickAction("warning", "\u9884\u8b66\u4e2d\u5fc3", "\u98ce\u9669\u8bc1\u636e", "\u76f4\u63a5\u8fdb\u5165\u5f85\u5904\u7f6e\u9884\u8b66\u5217\u8868\uff0c\u627f\u63a5\u9996\u9875\u9ad8\u98ce\u9669\u8bc1\u636e\u3002", this.modulePath("azb", "warning"), this.buildSourceQuery(regionCode, statDate, statMonth, "warnStatus", "0"), "\u9996\u9875\u98ce\u9669\u8bc1\u636e", "\u6765\u81ea\u9996\u9875\u98ce\u9669\u8bc1\u636e\u805a\u5408"));
        list.add(this.quickAction("device", "\u8bbe\u5907\u7ba1\u7406", "\u6280\u672f\u9632\u8303", "\u67e5\u770b\u79bb\u7ebf\u3001\u6545\u969c\u4e0e\u672a\u6388\u6743\u8bbe\u5907\uff0c\u8865\u8db3\u94f6\u884c\u89c6\u89d2\u8bc1\u636e\u94fe\u3002", this.modulePath("azb", "device"), this.buildSourceQuery(regionCode, statDate, statMonth, "authStatus", "0"), "\u9996\u9875\u98ce\u9669\u8bc1\u636e", "\u6765\u81ea\u9996\u9875\u98ce\u9669\u8bc1\u636e\u805a\u5408"));
        list.add(this.quickAction("heightWorkReport", "\u9ad8\u5904\u4f5c\u4e1a", "\u6280\u672f\u9632\u8303", "\u67e5\u770b\u8fdb\u884c\u4e2d\u9ad8\u5904\u4f5c\u4e1a\uff0c\u4fdd\u6301\u9996\u9875\u8bc1\u636e\u5165\u53e3\u53ef\u8ffd\u6eaf\u3002", this.modulePath("azb", "heightWorkReport"), this.buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "0"), "\u9996\u9875\u98ce\u9669\u8bc1\u636e", "\u6765\u81ea\u9996\u9875\u98ce\u9669\u8bc1\u636e\u805a\u5408"));
        return list;
    }

    private List<Map<String, Object>> buildAzbInsurerQuickActions(String regionCode, String statDate, String statMonth) {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        list.add(this.quickAction("aqInsurance", "\u5b89\u8d23\u9669\u76d1\u7ba1", "\u8986\u76d6\u590d\u6838", "\u67e5\u770b\u8986\u76d6\u7387\u3001\u5230\u671f\u98ce\u9669\u548c\u4fdd\u5355\u6c60\u72b6\u6001\u3002", this.modulePath("azb", "aqInsurance"), this.buildSourceQuery(regionCode, statDate, statMonth, "policyStatus", "2"), "\u9996\u9875\u5b89\u8d23\u9669\u8986\u76d6", "\u6765\u81ea\u9996\u9875\u5b89\u8d23\u9669\u8986\u76d6\u805a\u5408"));
        list.add(this.quickAction("preventionFund", "\u4e8b\u6545\u9884\u9632\u8d44\u91d1", "\u8d44\u91d1\u534f\u540c", "\u67e5\u770b\u4f4e\u4f59\u989d\u3001\u7f3a\u51ed\u8bc1\u548c\u5728\u7528\u8d44\u91d1\u72b6\u6001\u3002", this.modulePath("azb", "preventionFund"), this.buildSourceQuery(regionCode, statDate, statMonth, "fundStatus", "1"), "\u9996\u9875\u8d44\u91d1\u534f\u540c", "\u6765\u81ea\u9996\u9875\u8d44\u91d1\u534f\u540c\u805a\u5408"));
        list.add(this.quickAction("creditScore", "\u4fe1\u7528\u8bc4\u5206", "\u98ce\u9669\u5206\u5c42", "\u76f4\u63a5\u67e5\u770b\u7ea2\u7801\u548c\u5c3e\u90e8\u5bf9\u8c61\uff0c\u4e3a\u627f\u4fdd\u534f\u540c\u63d0\u4f9b\u5206\u5c42\u8bc1\u636e\u3002", this.modulePath("azb", "creditScore"), this.buildSourceQuery(regionCode, statDate, statMonth, "colorCode", "RED"), "\u9996\u9875\u98ce\u9669\u5206\u5c42", "\u6765\u81ea\u9996\u9875\u98ce\u9669\u5206\u5c42\u805a\u5408"));
        list.add(this.quickAction("statReport", "\u7edf\u8ba1\u62a5\u8868", "\u6cbb\u7406\u6708\u62a5", "\u67e5\u770b\u533a\u57df\u6cbb\u7406\u6708\u62a5\u548c\u538b\u964d\u7ed3\u679c\uff0c\u4e0d\u6df7\u5165\u529e\u7406\u94fe\u8bf4\u660e\u3002", this.modulePath("azb", "statReport"), this.buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "1"), "\u9996\u9875\u6cbb\u7406\u6708\u62a5", "\u6765\u81ea\u9996\u9875\u6cbb\u7406\u6708\u62a5\u805a\u5408"));
        list.add(this.quickAction("warning", "\u9884\u8b66\u4e2d\u5fc3", "\u98ce\u9669\u8bc1\u636e", "\u8fdb\u5165\u5f85\u5904\u7f6e\u9884\u8b66\u5217\u8868\uff0c\u627f\u63a5\u9996\u9875\u9690\u60a3\u5904\u7f6e\u8bed\u5883\u3002", this.modulePath("azb", "warning"), this.buildSourceQuery(regionCode, statDate, statMonth, "warnStatus", "0"), "\u9996\u9875\u98ce\u9669\u8bc1\u636e", "\u6765\u81ea\u9996\u9875\u98ce\u9669\u8bc1\u636e\u805a\u5408"));
        list.add(this.quickAction("device", "\u8bbe\u5907\u7ba1\u7406", "\u98ce\u9669\u8bc1\u636e", "\u67e5\u770b\u79bb\u7ebf\u3001\u6545\u969c\u4e0e\u672a\u6388\u6743\u8bbe\u5907\uff0c\u8865\u8db3\u627f\u4fdd\u8bc1\u636e\u94fe\u3002", this.modulePath("azb", "device"), this.buildSourceQuery(regionCode, statDate, statMonth, "authStatus", "0"), "\u9996\u9875\u98ce\u9669\u8bc1\u636e", "\u6765\u81ea\u9996\u9875\u98ce\u9669\u8bc1\u636e\u805a\u5408"));
        list.add(this.quickAction("heightWorkReport", "\u9ad8\u5904\u4f5c\u4e1a", "\u6280\u672f\u9632\u8303", "\u67e5\u770b\u8fdb\u884c\u4e2d\u9ad8\u5904\u4f5c\u4e1a\u548c\u8bc1\u4e66\u5f02\u5e38\u5bf9\u8c61\u3002", this.modulePath("azb", "heightWorkReport"), this.buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "0"), "\u9996\u9875\u6280\u672f\u9632\u8303", "\u6765\u81ea\u9996\u9875\u6280\u672f\u9632\u8303\u805a\u5408"));
        return list;
    }

    private List<Map<String, Object>> buildAzbBankQueueSections(YgbAzbCockpitDashboard dashboard, String regionCode, String statDate, String statMonth) {
        ArrayList<Map<String, Object>> sections = new ArrayList<Map<String, Object>>();
        YgbCreditScoreSummary creditScoreSummary = dashboard.getCreditScoreSummary();
        YgbStatReportSummary statReportSummary = dashboard.getStatReportSummary();
        YgbAqInsuranceSummary aqInsuranceSummary = dashboard.getAqInsuranceSummary();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbDeviceSummary deviceSummary = dashboard.getDeviceSummary();
        YgbHeightWorkReportSummary heightWorkReportSummary = dashboard.getHeightWorkReportSummary();
        ArrayList<Map<String, Object>> collaborationItems = new ArrayList<Map<String, Object>>();
        collaborationItems.add(this.queueItem("creditTailRisk", "\u7ea2\u7801\u4e0e\u5c3e\u90e8\u5bf9\u8c61", "\u534f\u540c\u590d\u6838\u961f\u5217", this.countCreditTailRisk(creditScoreSummary), "\u5bb6", "\u94f6\u884c\u53e3\u5f84\u5148\u770b\u7ea2\u7801\u3001D \u7ea7\u548c\u5c3e\u90e8\u5bf9\u8c61\uff0c\u518d\u51b3\u5b9a\u662f\u5426\u4e0b\u94bb\u6cbb\u7406\u8bc1\u636e\u3002", "\u7ee7\u7eed\u590d\u6838", "creditScore", "azb", this.buildSourceQuery(regionCode, statDate, statMonth, "colorCode", "RED"), "\u9996\u9875\u534f\u540c\u590d\u6838", "\u6765\u81ea\u9996\u9875\u534f\u540c\u590d\u6838\u805a\u5408"));
        collaborationItems.add(this.queueItem("regionalReport", "\u533a\u57df\u6cbb\u7406\u6708\u62a5", "\u534f\u540c\u590d\u6838\u961f\u5217", this.defaultNumber(statReportSummary == null ? null : statReportSummary.getGeneratedCount()), "\u4efd", "\u94f6\u884c\u53e3\u5f84\u901a\u8fc7\u6cbb\u7406\u6708\u62a5\u786e\u8ba4\u533a\u57df\u53d8\u5316\uff0c\u4e0d\u6df7\u5165\u4f01\u4e1a\u529e\u7406\u8bed\u4e49\u3002", "\u7ee7\u7eed\u590d\u6838", "statReport", "azb", this.buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "1"), "\u9996\u9875\u534f\u540c\u590d\u6838", "\u6765\u81ea\u9996\u9875\u534f\u540c\u590d\u6838\u805a\u5408"));
        collaborationItems.add(this.queueItem("insuranceEvidence", "\u5b89\u8d23\u9669\u98ce\u9669\u5bf9\u8c61", "\u534f\u540c\u590d\u6838\u961f\u5217", this.defaultNumber(aqInsuranceSummary == null ? null : aqInsuranceSummary.getRiskCount()), "\u5355", "\u5b89\u8d23\u9669\u98ce\u9669\u5bf9\u8c61\u4f5c\u4e3a\u94f6\u884c\u534f\u540c\u590d\u6838\u7684\u8865\u5145\u8bc1\u636e\u5165\u53e3\u3002", "\u67e5\u770b\u8bc1\u636e", "aqInsurance", "azb", this.buildSourceQuery(regionCode, statDate, statMonth, "policyStatus", "2"), "\u9996\u9875\u534f\u540c\u590d\u6838", "\u6765\u81ea\u9996\u9875\u534f\u540c\u590d\u6838\u805a\u5408"));
        sections.add(this.queueSection("collaboration", "\u534f\u540c\u590d\u6838\u961f\u5217", "\u94f6\u884c\u95e8\u6237\u53ea\u8f93\u51fa\u534f\u540c\u590d\u6838\u4e0e\u53ea\u8bfb\u8bc1\u636e\uff0c\u4e0d\u6df7\u5165\u529e\u7406\u94fe\u52a8\u4f5c\u3002", collaborationItems));
        ArrayList<Map<String, Object>> evidenceItems = new ArrayList<Map<String, Object>>();
        evidenceItems.add(this.queueItem("warningEvidence", "\u5f85\u5904\u7f6e\u9884\u8b66", "\u98ce\u9669\u8bc1\u636e\u961f\u5217", this.defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()), "\u6761", "\u9ad8\u98ce\u9669\u5de5\u5355\u4f5c\u4e3a\u94f6\u884c\u534f\u540c\u590d\u6838\u7684\u8bc1\u636e\u6765\u6e90\u3002", "\u67e5\u770b\u9884\u8b66", "warning", "azb", this.buildSourceQuery(regionCode, statDate, statMonth, "warnStatus", "0"), "\u9996\u9875\u98ce\u9669\u8bc1\u636e", "\u6765\u81ea\u9996\u9875\u98ce\u9669\u8bc1\u636e\u805a\u5408"));
        evidenceItems.add(this.queueItem("deviceEvidence", "\u8bbe\u5907\u5f02\u5e38", "\u98ce\u9669\u8bc1\u636e\u961f\u5217", this.countDeviceRisk(deviceSummary), "\u53f0", "\u79bb\u7ebf\u3001\u6545\u969c\u548c\u672a\u6388\u6743\u8bbe\u5907\u7528\u4e8e\u8865\u8db3\u73b0\u573a\u98ce\u9669\u8bc1\u636e\u3002", "\u67e5\u770b\u8bbe\u5907", "device", "azb", this.buildSourceQuery(regionCode, statDate, statMonth, "authStatus", "0"), "\u9996\u9875\u98ce\u9669\u8bc1\u636e", "\u6765\u81ea\u9996\u9875\u98ce\u9669\u8bc1\u636e\u805a\u5408"));
        evidenceItems.add(this.queueItem("heightEvidence", "\u8fdb\u884c\u4e2d\u9ad8\u5904\u4f5c\u4e1a", "\u98ce\u9669\u8bc1\u636e\u961f\u5217", this.defaultNumber(heightWorkReportSummary == null ? null : heightWorkReportSummary.getActiveCount()), "\u5355", "\u8fdb\u884c\u4e2d\u9ad8\u5904\u4f5c\u4e1a\u7ef4\u6301\u94f6\u884c\u89c6\u89d2\u7684\u6280\u672f\u9632\u8303\u8bc1\u636e\u5165\u53e3\u3002", "\u67e5\u770b\u4f5c\u4e1a", "heightWorkReport", "azb", this.buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "0"), "\u9996\u9875\u98ce\u9669\u8bc1\u636e", "\u6765\u81ea\u9996\u9875\u98ce\u9669\u8bc1\u636e\u805a\u5408"));
        sections.add(this.queueSection("evidence", "\u98ce\u9669\u8bc1\u636e\u961f\u5217", "\u94f6\u884c\u95e8\u6237\u4fdd\u7559\u9884\u8b66\u3001\u8bbe\u5907\u548c\u9ad8\u5904\u4f5c\u4e1a\u7684\u53ea\u8bfb\u8bc1\u636e\u94fe\u3002", evidenceItems));
        return sections;
    }

    private List<Map<String, Object>> buildAzbInsurerQueueSections(YgbAzbCockpitDashboard dashboard, String regionCode, String statDate, String statMonth) {
        ArrayList<Map<String, Object>> sections = new ArrayList<Map<String, Object>>();
        YgbAqInsuranceSummary aqInsuranceSummary = dashboard.getAqInsuranceSummary();
        YgbPreventionFundSummary preventionFundSummary = dashboard.getPreventionFundSummary();
        YgbCreditScoreSummary creditScoreSummary = dashboard.getCreditScoreSummary();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbDeviceSummary deviceSummary = dashboard.getDeviceSummary();
        YgbHeightWorkReportSummary heightWorkReportSummary = dashboard.getHeightWorkReportSummary();
        YgbStatReportSummary statReportSummary = dashboard.getStatReportSummary();
        ArrayList<Map<String, Object>> coverageItems = new ArrayList<Map<String, Object>>();
        coverageItems.add(this.queueItem("policyRisk", "\u4fdd\u5355\u5230\u671f\u4e0e\u5931\u6548", "\u5b89\u8d23\u9669\u8986\u76d6\u961f\u5217", this.defaultNumber(aqInsuranceSummary == null ? null : aqInsuranceSummary.getRiskCount()), "\u5355", "\u627f\u4fdd\u89c6\u89d2\u5148\u770b\u8986\u76d6\u7387\u3001\u5230\u671f\u98ce\u9669\u548c\u4fdd\u5355\u5931\u6548\u5bf9\u8c61\u3002", "\u7ee7\u7eed\u590d\u6838", "aqInsurance", "azb", this.buildSourceQuery(regionCode, statDate, statMonth, "policyStatus", "2"), "\u9996\u9875\u5b89\u8d23\u9669\u8986\u76d6", "\u6765\u81ea\u9996\u9875\u5b89\u8d23\u9669\u8986\u76d6\u805a\u5408"));
        coverageItems.add(this.queueItem("fundCoordination", "\u4e8b\u6545\u9884\u9632\u8d44\u91d1", "\u8d44\u91d1\u534f\u540c\u961f\u5217", this.countFundCoordinationRisk(preventionFundSummary), "\u6761", "\u4f4e\u4f59\u989d\u3001\u7f3a\u51ed\u8bc1\u548c\u5728\u7528\u8d44\u91d1\u5f02\u5e38\u9700\u8981\u7ee7\u7eed\u627f\u4fdd\u534f\u540c\u3002", "\u7ee7\u7eed\u534f\u540c", "preventionFund", "azb", this.buildSourceQuery(regionCode, statDate, statMonth, "fundStatus", "1"), "\u9996\u9875\u8d44\u91d1\u534f\u540c", "\u6765\u81ea\u9996\u9875\u8d44\u91d1\u534f\u540c\u805a\u5408"));
        coverageItems.add(this.queueItem("creditLayering", "\u7ea2\u7801\u4e0e\u5c3e\u90e8\u5bf9\u8c61", "\u98ce\u9669\u5206\u5c42\u961f\u5217", this.countCreditTailRisk(creditScoreSummary), "\u5bb6", "\u7ea2\u7801\u548c\u5c3e\u90e8\u5bf9\u8c61\u4e3a\u627f\u4fdd\u590d\u6838\u63d0\u4f9b\u5206\u5c42\u4f18\u5148\u7ea7\u3002", "\u7ee7\u7eed\u5206\u5c42", "creditScore", "azb", this.buildSourceQuery(regionCode, statDate, statMonth, "colorCode", "RED"), "\u9996\u9875\u98ce\u9669\u5206\u5c42", "\u6765\u81ea\u9996\u9875\u98ce\u9669\u5206\u5c42\u805a\u5408"));
        sections.add(this.queueSection("coverage", "\u5b89\u8d23\u9669\u8986\u76d6\u961f\u5217", "\u56f4\u7ed5\u5b89\u8d23\u9669\u8986\u76d6\u3001\u8d44\u91d1\u534f\u540c\u548c\u98ce\u9669\u5206\u5c42\u7ec4\u7ec7\u627f\u4fdd\u9996\u9875\u5165\u53e3\u3002", coverageItems));
        ArrayList<Map<String, Object>> governanceItems = new ArrayList<Map<String, Object>>();
        governanceItems.add(this.queueItem("warningDisposal", "\u5f85\u5904\u7f6e\u9884\u8b66", "\u627f\u4fdd\u6cbb\u7406\u961f\u5217", this.defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()), "\u6761", "\u9884\u8b66\u5f85\u5904\u7f6e\u91cf\u662f\u627f\u4fdd\u534f\u540c\u538b\u964d\u7684\u7b2c\u4e00\u4f18\u5148\u9879\u3002", "\u67e5\u770b\u9884\u8b66", "warning", "azb", this.buildSourceQuery(regionCode, statDate, statMonth, "warnStatus", "0"), "\u9996\u9875\u98ce\u9669\u8bc1\u636e", "\u6765\u81ea\u9996\u9875\u98ce\u9669\u8bc1\u636e\u805a\u5408"));
        governanceItems.add(this.queueItem("deviceRisk", "\u8bbe\u5907\u5f02\u5e38", "\u627f\u4fdd\u6cbb\u7406\u961f\u5217", this.countDeviceRisk(deviceSummary), "\u53f0", "\u8bbe\u5907\u5f02\u5e38\u7528\u4e8e\u8bc6\u522b\u73b0\u573a\u611f\u77e5\u94fe\u8def\u662f\u5426\u5b8c\u6574\u3002", "\u67e5\u770b\u8bbe\u5907", "device", "azb", this.buildSourceQuery(regionCode, statDate, statMonth, "authStatus", "0"), "\u9996\u9875\u98ce\u9669\u8bc1\u636e", "\u6765\u81ea\u9996\u9875\u98ce\u9669\u8bc1\u636e\u805a\u5408"));
        governanceItems.add(this.queueItem("heightActive", "\u8fdb\u884c\u4e2d\u9ad8\u5904\u4f5c\u4e1a", "\u627f\u4fdd\u6cbb\u7406\u961f\u5217", this.defaultNumber(heightWorkReportSummary == null ? null : heightWorkReportSummary.getActiveCount()), "\u5355", "\u8fdb\u884c\u4e2d\u9ad8\u5904\u4f5c\u4e1a\u9700\u8981\u7ee7\u7eed\u8ddf\u8e2a\u6280\u672f\u9632\u8303\u4e0e\u8bc1\u4e66\u5408\u89c4\u3002", "\u67e5\u770b\u4f5c\u4e1a", "heightWorkReport", "azb", this.buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "0"), "\u9996\u9875\u6280\u672f\u9632\u8303", "\u6765\u81ea\u9996\u9875\u6280\u672f\u9632\u8303\u805a\u5408"));
        governanceItems.add(this.queueItem("regionalResult", "\u533a\u57df\u6cbb\u7406\u6708\u62a5", "\u627f\u4fdd\u6cbb\u7406\u961f\u5217", this.defaultNumber(statReportSummary == null ? null : statReportSummary.getGeneratedCount()), "\u4efd", "\u533a\u57df\u6cbb\u7406\u6708\u62a5\u7528\u4e8e\u590d\u76d8\u538b\u964d\u7ed3\u679c\u4e0e\u6cbb\u7406\u6001\u52bf\u3002", "\u67e5\u770b\u6708\u62a5", "statReport", "azb", this.buildSourceQuery(regionCode, statDate, statMonth, "reportStatus", "1"), "\u9996\u9875\u6cbb\u7406\u6708\u62a5", "\u6765\u81ea\u9996\u9875\u6cbb\u7406\u6708\u62a5\u805a\u5408"));
        sections.add(this.queueSection("insurerGovernance", "\u627f\u4fdd\u6cbb\u7406\u961f\u5217", "\u627f\u4fdd\u95e8\u6237\u53ea\u4e0b\u94bb\u6cbb\u7406\u94fe\u4e0e\u53ea\u8bfb\u534f\u540c\u6a21\u5757\uff0c\u4fdd\u6301 6.1 \u76d1\u7ba1\u8bed\u4e49\u3002", governanceItems));
        return sections;
    }

    private List<Map<String, Object>> buildAzbInsurerHomeExplanation(YgbAzbCockpitDashboard dashboard, String regionCode, String statMonth) {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        YgbAqInsuranceSummary aqInsuranceSummary = dashboard.getAqInsuranceSummary();
        YgbPreventionFundSummary preventionFundSummary = dashboard.getPreventionFundSummary();
        YgbDeviceSummary deviceSummary = dashboard.getDeviceSummary();
        YgbHeightWorkReportSummary heightWorkReportSummary = dashboard.getHeightWorkReportSummary();
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbStatReportSummary statReportSummary = dashboard.getStatReportSummary();
        YgbCreditScoreSummary creditScoreSummary = dashboard.getCreditScoreSummary();
        list.add(this.explanationItem("\u5b89\u8d23\u9669\u8986\u76d6", this.formatDecimalText(aqInsuranceSummary == null ? null : aqInsuranceSummary.getCoverageRate()), "100", "6.1 \u76d1\u7ba1\u89e3\u91ca\u4f18\u5148\u770b\u5b89\u8d23\u9669\u8986\u76d6\u3001\u5230\u671f\u98ce\u9669\u548c\u5931\u6548\u5bf9\u8c61\uff0c\u4e0d\u4f7f\u7528\u4f01\u4e1a\u529e\u7406\u53e3\u5f84\u515c\u5e95\u3002", "aqInsurance", "aqInsurance", this.buildSourceQuery(regionCode, null, statMonth, "policyStatus", "2"), "\u9996\u9875\u5b89\u8d23\u9669\u8986\u76d6", "\u6765\u6e90\u4e8e\u5b89\u8d23\u9669\u8986\u76d6\u805a\u5408"));
        list.add(this.explanationItem("\u4e8b\u6545\u9884\u9632\u8d44\u91d1", this.countFundCoordinationRisk(preventionFundSummary), "0", "\u4f18\u5148\u89e3\u91ca\u4f4e\u4f59\u989d\u3001\u7f3a\u51ed\u8bc1\u548c\u5728\u7528\u8d44\u91d1\u5f02\u5e38\uff0c\u627f\u4fdd\u534f\u540c\u53ea\u8c03\u6574\u89e3\u91ca\u987a\u5e8f\u4e0e\u63a8\u8350\u52a8\u4f5c\u3002", "preventionFund", "preventionFund", this.buildSourceQuery(regionCode, null, statMonth, "fundStatus", "1"), "\u9996\u9875\u8d44\u91d1\u534f\u540c", "\u6765\u6e90\u4e8e\u4e8b\u6545\u9884\u9632\u8d44\u91d1\u805a\u5408"));
        list.add(this.explanationItem("\u8bbe\u5907\u5b89\u5168", this.countDeviceRisk(deviceSummary), "0", "\u8bbe\u5907\u79bb\u7ebf\u3001\u6545\u969c\u548c\u672a\u6388\u6743\u5bf9\u8c61\u5148\u4f5c\u4e3a\u627f\u4fdd\u8bc1\u636e\uff0c\u518d\u51b3\u5b9a\u662f\u5426\u7ee7\u7eed\u533a\u57df\u538b\u964d\u3002", "device", "device", this.buildSourceQuery(regionCode, null, statMonth, "authStatus", "0"), "\u9996\u9875\u98ce\u9669\u8bc1\u636e", "\u6765\u6e90\u4e8e\u8bbe\u5907\u5b89\u5168\u805a\u5408"));
        list.add(this.explanationItem("\u6280\u672f\u9632\u8303", this.defaultNumber(heightWorkReportSummary == null ? null : heightWorkReportSummary.getActiveCount()), "0", "\u9ad8\u5904\u4f5c\u4e1a\u89e3\u91ca\u4fdd\u6301\u6280\u672f\u9632\u8303\u8bed\u4e49\uff0c\u4e0d\u6df7\u7528\u4f01\u4e1a\u529e\u7406\u6458\u8981\u3002", "heightWorkReport", "heightWorkReport", this.buildSourceQuery(regionCode, null, statMonth, "reportStatus", "0"), "\u9996\u9875\u6280\u672f\u9632\u8303", "\u6765\u6e90\u4e8e\u6280\u672f\u9632\u8303\u805a\u5408"));
        list.add(this.explanationItem("\u9884\u8b66\u5904\u7f6e", this.defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()), "0", "\u9884\u8b66\u5f85\u5904\u7f6e\u91cf\u4f53\u73b0\u9690\u60a3\u6574\u6539\u548c\u538b\u964d\u65f6\u6548\uff0c\u9996\u9875\u53ea\u4fdd\u7559\u4e00\u4e2a\u4e3b\u4e0b\u94bb\u5165\u53e3\u3002", "warning", "warning", this.buildSourceQuery(regionCode, null, statMonth, "warnStatus", "0"), "\u9996\u9875\u98ce\u9669\u8bc1\u636e", "\u6765\u6e90\u4e8e\u9884\u8b66\u5904\u7f6e\u805a\u5408"));
        list.add(this.explanationItem("\u533a\u57df\u6cbb\u7406\u6210\u6548", this.defaultNumber(statReportSummary == null ? null : statReportSummary.getGeneratedCount()), ">=1", "\u6cbb\u7406\u6708\u62a5\u3001\u533a\u57df\u6001\u52bf\u548c\u538b\u964d\u7ed3\u679c\u56fa\u5b9a\u6309 6.1 \u76d1\u7ba1\u89e3\u91ca\u6392\u5e8f\u8f93\u51fa\u3002", "statReport", "statReport", this.buildSourceQuery(regionCode, null, statMonth, "reportStatus", "1"), "\u9996\u9875\u6cbb\u7406\u6708\u62a5", "\u6765\u6e90\u4e8e\u533a\u57df\u6cbb\u7406\u6708\u62a5"));
        list.add(this.explanationItem("\u4fe1\u7528\u5206\u5c42", this.countCreditTailRisk(creditScoreSummary), "0", "\u4fe1\u7528\u5206\u5c42\u89e3\u91ca\u7a81\u51fa\u7ea2\u7801\u3001D \u7ea7\u548c\u5c3e\u90e8\u5bf9\u8c61\uff0c\u4e3a\u627f\u4fdd\u590d\u6838\u63d0\u4f9b\u98ce\u9669\u6392\u5e8f\u3002", "creditScore", "creditScore", this.buildSourceQuery(regionCode, null, statMonth, "colorCode", "RED"), "\u9996\u9875\u98ce\u9669\u5206\u5c42", "\u6765\u6e90\u4e8e\u4fe1\u7528\u5206\u5c42\u805a\u5408"));
        return list;
    }

    private String resolveAzbRoleHomeSummary(YgbAzbCockpitDashboard dashboard, String roleView) {
        if (dashboard == null) {
            return "";
        }
        YgbWarningSummary warningSummary = dashboard.getWarningSummary();
        YgbDeviceSummary deviceSummary = dashboard.getDeviceSummary();
        YgbAqInsuranceSummary aqInsuranceSummary = dashboard.getAqInsuranceSummary();
        YgbPreventionFundSummary preventionFundSummary = dashboard.getPreventionFundSummary();
        YgbCreditScoreSummary creditScoreSummary = dashboard.getCreditScoreSummary();
        YgbStatReportSummary statReportSummary = dashboard.getStatReportSummary();
        if ("bank".equals(roleView)) {
            return "\u5f53\u524d\u91cd\u70b9\uff1a\u534f\u540c\u590d\u6838\u7ea2\u7801\u4e0e\u5c3e\u90e8\u5bf9\u8c61 " + this.countCreditTailRisk(creditScoreSummary) + " \u5bb6\uff0c\u533a\u57df\u6cbb\u7406\u6708\u62a5 " + this.defaultNumber(statReportSummary == null ? null : statReportSummary.getGeneratedCount()) + " \u4efd\uff0c\u5b89\u8d23\u9669\u98ce\u9669\u8bc1\u636e " + this.defaultNumber(aqInsuranceSummary == null ? null : aqInsuranceSummary.getRiskCount()) + " \u5355\u3002";
        }
        if ("insurer".equals(roleView)) {
            return "\u5f53\u524d\u91cd\u70b9\uff1a\u5b89\u8d23\u9669\u98ce\u9669 " + this.defaultNumber(aqInsuranceSummary == null ? null : aqInsuranceSummary.getRiskCount()) + " \u5355\uff0c\u8d44\u91d1\u534f\u540c\u5f02\u5e38 " + this.countFundCoordinationRisk(preventionFundSummary) + " \u6761\uff0c\u7ea2\u7801\u4e0e\u5c3e\u90e8\u5bf9\u8c61 " + this.countCreditTailRisk(creditScoreSummary) + " \u5bb6\uff0c\u6cbb\u7406\u6708\u62a5 " + this.defaultNumber(statReportSummary == null ? null : statReportSummary.getGeneratedCount()) + " \u4efd\u3002";
        }
        return "\u5f53\u524d\u91cd\u70b9\uff1a\u5f85\u5904\u7f6e\u9884\u8b66 " + this.defaultNumber(warningSummary == null ? null : warningSummary.getPendingCount()) + " \u6761\uff0c\u8bbe\u5907\u5f02\u5e38 " + this.countDeviceRisk(deviceSummary) + " \u53f0\uff0c\u5b89\u8d23\u9669\u98ce\u9669 " + this.defaultNumber(aqInsuranceSummary == null ? null : aqInsuranceSummary.getRiskCount()) + " \u5355\u3002";
    }

    private Map<String, Object> findQueueSection(List<Map<String, Object>> sections, String key) {
        if (sections == null || StringUtils.isEmpty((String)key)) {
            return null;
        }
        for (Map<String, Object> section : sections) {
            if (section == null || !key.equals(String.valueOf(section.get("key")))) continue;
            return section;
        }
        return null;
    }

    private void appendQueueItemIfMissing(Map<String, Object> section, String itemKey, Map<String, Object> item) {
        ArrayList<Map<String, Object>> items;
        if (section == null || StringUtils.isEmpty((String)itemKey) || item == null) {
            return;
        }
        Object itemsValue = section.get("items");
        if (itemsValue instanceof List) {
            items = (ArrayList<Map<String, Object>>)itemsValue;
        } else {
            items = new ArrayList<Map<String, Object>>();
            section.put("items", items);
        }
        for (Map map : items) {
            if (map == null || !itemKey.equals(String.valueOf(map.get("key")))) continue;
            return;
        }
        items.add(item);
    }

    private Integer countDeviceRisk(YgbDeviceSummary deviceSummary) {
        return this.defaultNumber(deviceSummary == null ? null : deviceSummary.getLockedOrFaultCount()) + this.defaultNumber(deviceSummary == null ? null : deviceSummary.getUnauthorizedCount()) + this.defaultNumber(deviceSummary == null ? null : deviceSummary.getAuthDeniedCount());
    }

    private Integer countCreditTailRisk(YgbCreditScoreSummary creditScoreSummary) {
        return this.defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getRedCount()) + this.defaultNumber(creditScoreSummary == null ? null : creditScoreSummary.getDCount());
    }

    private Integer countFundCoordinationRisk(YgbPreventionFundSummary preventionFundSummary) {
        return this.defaultNumber(preventionFundSummary == null ? null : preventionFundSummary.getLowBalanceCount()) + this.defaultNumber(preventionFundSummary == null ? null : preventionFundSummary.getMissingEvidenceCount()) + this.defaultNumber(preventionFundSummary == null ? null : preventionFundSummary.getNonStubCount());
    }

    private Map<String, Object> summaryCard(String key, String label, Object value, String unit, String note, String cardClass) {
        HashMap<String, Object> item = new HashMap<String, Object>();
        item.put("key", key);
        item.put("label", label);
        item.put("value", value);
        item.put("unit", unit);
        item.put("note", note);
        item.put("cardClass", cardClass);
        return item;
    }

    private Map<String, Object> focusQueue(String key, String title, Object count, String unit, String desc, String path, String actionText) {
        return this.focusQueue(key, title, count, unit, desc, path, actionText, null, null, null);
    }

    private Map<String, Object> focusQueue(String key, String title, Object count, String unit, String desc, String path, String actionText, Map<String, Object> query, String sourceLabel, String sourceDescription) {
        HashMap<String, Object> item = new HashMap<String, Object>();
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

    private Map<String, Object> workflowStep(String label, String desc) {
        HashMap<String, Object> item = new HashMap<String, Object>();
        item.put("label", label);
        item.put("desc", desc);
        return item;
    }

    private Map<String, Object> quickAction(String key, String label, String badge, String desc, String path) {
        return this.quickAction(key, label, badge, desc, path, null, null, null);
    }

    private Map<String, Object> quickAction(String key, String label, String badge, String desc, String path, Map<String, Object> query, String sourceLabel, String sourceDescription) {
        HashMap<String, Object> item = new HashMap<String, Object>();
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

    private List<Map<String, Object>> buildQuickSections(List<Map<String, Object>> quickActions, String fallbackTitle, String fallbackDescription) {
        if (quickActions == null || quickActions.isEmpty()) {
            return new ArrayList<Map<String, Object>>();
        }
        LinkedHashMap grouped = new LinkedHashMap();
        int sectionIndex = 1;
        for (Map<String, Object> action : quickActions) {
            String sourceDescription;
            if (action == null) continue;
            String sourceLabel = this.stringValue(action.get("sourceLabel"));
            String groupKey = sourceLabel + "__" + (sourceDescription = this.stringValue(action.get("sourceDescription")));
            HashMap<String, Object> section = (HashMap<String, Object>)grouped.get(groupKey);
            if (section == null) {
                section = new HashMap<String, Object>();
                section.put("key", StringUtils.isNotEmpty((String)sourceLabel) ? this.normalizeAggregateKey(sourceLabel) : "portalSection" + sectionIndex);
                section.put("title", StringUtils.isNotEmpty((String)sourceLabel) ? sourceLabel : fallbackTitle);
                section.put("desc", StringUtils.isNotEmpty((String)sourceDescription) ? sourceDescription : fallbackDescription);
                section.put("actions", new ArrayList());
                grouped.put((CallSite)((Object)groupKey), section);
                ++sectionIndex;
            }
            ((List)section.get("actions")).add(action);
        }
        return new ArrayList<Map<String, Object>>(grouped.values());
    }

    private Map<String, Object> queueSection(String key, String title, String desc, List<Map<String, Object>> items) {
        HashMap<String, Object> item = new HashMap<String, Object>();
        item.put("key", key);
        item.put("title", title);
        item.put("desc", desc);
        item.put("items", items);
        return item;
    }

    private Map<String, Object> queueItem(String key, String title, String desc, Object count, String unit, String summary, String actionText, String moduleCode, String portalCode, Map<String, Object> defaultQuery, String sourceLabel, String sourceDescription) {
        HashMap<String, Object> item = new HashMap<String, Object>();
        item.put("key", key);
        item.put("title", title + " " + String.valueOf(count) + unit);
        item.put("desc", desc + " \u00b7 " + summary);
        item.put("status", actionText);
        item.put("hint", summary);
        item.put("count", count);
        item.put("unit", unit);
        item.put("moduleCode", moduleCode);
        item.put("path", this.modulePath(portalCode, moduleCode));
        item.put("defaultQuery", defaultQuery);
        item.put("query", defaultQuery);
        item.put("sourceLabel", sourceLabel);
        item.put("sourceDescription", sourceDescription);
        return item;
    }

    private Map<String, Object> explanationItem(String dimensionName, Object currentValue, Object targetValue, String summary, String evidenceModule, String recommendModule, Map<String, Object> defaultQuery, String sourceLabel, String sourceDescription) {
        HashMap<String, Object> item = new HashMap<String, Object>();
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

    private Map<String, Object> focusPanel(String title, String desc, String moduleCode, Map<String, Object> defaultQuery, String sourceLabel, String sourceDescription) {
        HashMap<String, Object> item = new HashMap<String, Object>();
        item.put("title", title);
        item.put("desc", desc);
        item.put("moduleCode", moduleCode);
        item.put("defaultQuery", defaultQuery);
        item.put("sourceLabel", sourceLabel);
        item.put("sourceDescription", sourceDescription);
        return item;
    }

    private String normalizeAggregateKey(String value) {
        if (StringUtils.isEmpty((String)value)) {
            return "";
        }
        return value.replaceAll("[^0-9A-Za-z\\u4e00-\\u9fa5]+", "").trim();
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private Map<String, Object> buildPortalDefaultQuery(String regionCode, String statDate, String statMonth) {
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("regionCode", regionCode);
        query.put("statDate", statDate);
        query.put("statMonth", statMonth);
        return query;
    }

    private Map<String, Object> buildSourceQuery(String regionCode, String statDate, String statMonth, String field, Object value) {
        Map<String, Object> query = this.buildPortalDefaultQuery(regionCode, statDate, statMonth);
        if (value != null) {
            query.put(field, value);
        }
        return query;
    }

    private String modulePath(String portalCode, String moduleCode) {
        return "/" + portalCode + "/" + moduleCode;
    }

    private Map<String, Object> mapValue(Object value) {
        if (value instanceof Map) {
            return (Map)value;
        }
        return new HashMap<String, Object>();
    }

    private Map<String, Object> hintTag(String label, String type) {
        HashMap<String, Object> item = new HashMap<String, Object>();
        item.put("label", label);
        item.put("type", type);
        return item;
    }

    private List<Map<String, Object>> buildGeoJsonFeatures(List<YgbCockpitMapFeature> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Map<String, Object>> features = new ArrayList<Map<String, Object>>();
        for (YgbCockpitMapFeature item : list) {
            HashMap<String, Object> geometry = new HashMap<String, Object>();
            geometry.put("type", item.getGeometryType());
            geometry.put("coordinates", this.readJsonValue(item.getGeometryJson()));
            Map<String, Object> properties = this.readJsonMap(item.getPropertiesJson());
            properties.putIfAbsent("featureType", item.getFeatureType());
            properties.putIfAbsent("featureName", item.getFeatureName());
            properties.putIfAbsent("featureStatus", item.getFeatureStatus());
            properties.putIfAbsent("regionCode", item.getRegionCode());
            properties.putIfAbsent("sourceMode", item.getSourceMode());
            HashMap<String, Object> feature = new HashMap<String, Object>();
            feature.put("type", "Feature");
            feature.put("id", item.getFeatureId());
            feature.put("geometry", geometry);
            feature.put("properties", properties);
            features.add(feature);
        }
        return features;
    }

    private Object readJsonValue(String json) {
        if (StringUtils.isEmpty((String)json)) {
            return Collections.emptyList();
        }
        try {
            return OBJECT_MAPPER.readValue(json, Object.class);
        }
        catch (Exception e) {
            throw new ServiceException("\u9a7e\u9a76\u8231\u5730\u56fe\u8981\u7d20\u6570\u636e\u683c\u5f0f\u9519\u8bef");
        }
    }

    private Map<String, Object> readJsonMap(String json) {
        if (StringUtils.isEmpty((String)json)) {
            return new HashMap<String, Object>();
        }
        try {
            return (Map)OBJECT_MAPPER.readValue(json, (TypeReference)new TypeReference<Map<String, Object>>(){});
        }
        catch (Exception e) {
            throw new ServiceException("\u9a7e\u9a76\u8231\u5730\u56fe\u5c5e\u6027\u6570\u636e\u683c\u5f0f\u9519\u8bef");
        }
    }

    private String normalizeDate(String statDate) {
        if (StringUtils.isEmpty((String)statDate)) {
            return LocalDate.now().format(DATE_FORMATTER);
        }
        try {
            return LocalDate.parse(statDate, DATE_FORMATTER).format(DATE_FORMATTER);
        }
        catch (Exception ex) {
            throw new ServiceException("\u7edf\u8ba1\u65e5\u671f\u683c\u5f0f\u9519\u8bef\uff0c\u5e94\u4e3a yyyy-MM-dd");
        }
    }

    private String normalizeOptionalDate(String statDate) {
        if (StringUtils.isEmpty((String)statDate)) {
            return null;
        }
        return this.normalizeDate(statDate);
    }

    private String normalizeMonth(String statMonth) {
        if (StringUtils.isEmpty((String)statMonth)) {
            return LocalDate.now().format(MONTH_FORMATTER);
        }
        if (!statMonth.matches("^\\d{4}-\\d{2}$")) {
            throw new ServiceException("\u7edf\u8ba1\u6708\u4efd\u683c\u5f0f\u9519\u8bef\uff0c\u5e94\u4e3a yyyy-MM");
        }
        return statMonth;
    }

    private Integer defaultNumber(Integer value) {
        return value == null ? 0 : value;
    }

    private BigDecimal defaultDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String resolveSourceName(String sourceCode) {
        if ("SOCIAL".equals(sourceCode)) {
            return "\u793e\u4fdd\u76d1\u7ba1";
        }
        if ("TAX".equals(sourceCode)) {
            return "\u7a0e\u52a1\u76d1\u7ba1";
        }
        if ("EXPANSION".equals(sourceCode)) {
            return "\u6269\u9762\u51cf\u635f";
        }
        if ("SPECIAL".equals(sourceCode)) {
            return "\u4e13\u9879\u6cbb\u7406";
        }
        if ("DEVICE".equals(sourceCode)) {
            return "\u8bbe\u5907\u9884\u8b66";
        }
        if ("INJURY".equals(sourceCode)) {
            return "\u5de5\u4f24\u76d1\u7ba1";
        }
        return StringUtils.isEmpty((String)sourceCode) ? "\u5176\u4ed6" : sourceCode;
    }

    private YgbContract buildContractQuery(String regionCode) {
        YgbContract query = new YgbContract();
        query.setRegionCode(regionCode);
        return query;
    }

    private YgbAttendanceRaw buildAttendanceRawQuery(String regionCode) {
        YgbAttendanceRaw query = new YgbAttendanceRaw();
        query.setRegionCode(regionCode);
        return query;
    }

    private YgbAttendanceMonthly buildAttendanceMonthlyQuery(String regionCode, String statMonth) {
        YgbAttendanceMonthly query = new YgbAttendanceMonthly();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbSalaryBatch buildSalaryBatchQuery(String regionCode, String statMonth) {
        YgbSalaryBatch query = new YgbSalaryBatch();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbSalaryDetail buildSalaryDetailQuery(String regionCode, String statMonth) {
        YgbSalaryDetail query = new YgbSalaryDetail();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbSocialPayment buildSocialPaymentQuery(String regionCode, String statMonth) {
        YgbSocialPayment query = new YgbSocialPayment();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbSocialBaseCompare buildSocialBaseCompareQuery(String regionCode, String statMonth) {
        YgbSocialBaseCompare query = new YgbSocialBaseCompare();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbTaxCompare buildTaxCompareQuery(String regionCode, String statMonth) {
        YgbTaxCompare query = new YgbTaxCompare();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbEmploymentRatio buildEmploymentRatioQuery(String regionCode, String statMonth) {
        YgbEmploymentRatio query = new YgbEmploymentRatio();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbFakeOutsourcingRecord buildFakeOutsourcingQuery(String regionCode, String statMonth) {
        YgbFakeOutsourcingRecord query = new YgbFakeOutsourcingRecord();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbNewformWorker buildNewformWorkerQuery(String regionCode, String statMonth) {
        YgbNewformWorker query = new YgbNewformWorker();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbOccupationMonitor buildOccupationMonitorQuery(String regionCode, String statMonth) {
        YgbOccupationMonitor query = new YgbOccupationMonitor();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbWarning buildWarningQuery(String regionCode) {
        YgbWarning query = new YgbWarning();
        query.setRegionCode(regionCode);
        return query;
    }

    private YgbAqInsurance buildAqInsuranceQuery(String regionCode, String statMonth) {
        YgbAqInsurance query = new YgbAqInsurance();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbPreventionFund buildPreventionFundQuery(String regionCode, String statMonth) {
        YgbPreventionFund query = new YgbPreventionFund();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbCreditScore buildCreditScoreQuery(String regionCode, String statMonth) {
        YgbCreditScore query = new YgbCreditScore();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbDevice buildDeviceQuery(String regionCode) {
        YgbDevice query = new YgbDevice();
        query.setRegionCode(regionCode);
        return query;
    }

    private YgbInjuryEvent buildInjuryEventQuery(String regionCode) {
        YgbInjuryEvent query = new YgbInjuryEvent();
        query.setRegionCode(regionCode);
        return query;
    }

    private String formatDecimalText(BigDecimal value) {
        return this.defaultDecimal(value).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private YgbHeightWorkReport buildHeightWorkReportQuery(String regionCode, String statDate) {
        YgbHeightWorkReport query = new YgbHeightWorkReport();
        query.setRegionCode(regionCode);
        query.setStartTimeBegin(statDate + " 00:00:00");
        query.setStartTimeEnd(statDate + " 23:59:59");
        return query;
    }

    private YgbUninsuredList buildUninsuredListQuery(String regionCode, String statMonth) {
        YgbUninsuredList query = new YgbUninsuredList();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbStatReport buildStatReportQuery(String regionCode, String statMonth) {
        YgbStatReport query = new YgbStatReport();
        query.setRegionCode(regionCode);
        query.setStatMonth(statMonth);
        return query;
    }

    private YgbEnterprise buildEnterpriseQuery(String regionCode) {
        YgbEnterprise query = new YgbEnterprise();
        query.setRegionCode(regionCode);
        return query;
    }

    private YgbPerson buildPersonQuery(String regionCode) {
        YgbPerson query = new YgbPerson();
        query.setRegionCode(regionCode);
        return query;
    }
}
