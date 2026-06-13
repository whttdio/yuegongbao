package com.yuegongbao.ygb.cockpit.domain;

import java.util.List;
import java.util.Map;
import com.yuegongbao.ygb.compliance.domain.YgbAttendanceMonthlySummary;
import com.yuegongbao.ygb.compliance.domain.YgbAttendanceRawSummary;
import com.yuegongbao.ygb.compliance.domain.YgbContractSummary;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryBatchSummary;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryDetailSummary;
import com.yuegongbao.ygb.foundation.domain.YgbEnterpriseSummary;
import com.yuegongbao.ygb.foundation.domain.YgbPersonSummary;
import com.yuegongbao.ygb.newform.domain.YgbNewformWorkerSummary;
import com.yuegongbao.ygb.occupation.domain.YgbOccupationMonitorSummary;
import com.yuegongbao.ygb.regulation.domain.YgbEmploymentRatioSummary;
import com.yuegongbao.ygb.regulation.domain.YgbFakeOutsourcingSummary;
import com.yuegongbao.ygb.regulation.domain.YgbSocialBaseCompareSummary;
import com.yuegongbao.ygb.regulation.domain.YgbSocialPaymentSummary;
import com.yuegongbao.ygb.regulation.domain.YgbTaxCompareSummary;
import com.yuegongbao.ygb.regulation.domain.YgbUninsuredListSummary;
import com.yuegongbao.ygb.report.domain.YgbStatReportSummary;
import com.yuegongbao.ygb.techdefense.domain.YgbHeightWorkReportSummary;
import com.yuegongbao.ygb.warning.domain.YgbWarningSummary;

public class YgbWorkbenchDashboard
{
    private YgbCockpitIndicator indicators;

    private List<YgbCockpitSnapshot> trend;

    private List<YgbCockpitDistribution> distribution;

    private YgbGeoJsonFeatureCollection map;

    private YgbContractSummary contractSummary;

    private YgbAttendanceRawSummary attendanceRawSummary;

    private YgbAttendanceMonthlySummary attendanceMonthlySummary;

    private YgbSalaryBatchSummary salaryBatchSummary;

    private YgbSalaryDetailSummary salaryDetailSummary;

    private YgbSocialPaymentSummary socialPaymentSummary;

    private YgbSocialBaseCompareSummary socialBaseCompareSummary;

    private YgbTaxCompareSummary taxCompareSummary;

    private YgbEmploymentRatioSummary employmentRatioSummary;

    private YgbFakeOutsourcingSummary fakeOutsourcingSummary;

    private YgbNewformWorkerSummary newformWorkerSummary;

    private YgbOccupationMonitorSummary occupationMonitorSummary;

    private YgbWarningSummary warningSummary;

    private YgbHeightWorkReportSummary heightWorkReportSummary;

    private YgbUninsuredListSummary uninsuredListSummary;

    private YgbStatReportSummary statReportSummary;

    private YgbEnterpriseSummary enterpriseSummary;

    private YgbPersonSummary personSummary;

    private List<Map<String, Object>> summaryCards;

    private List<Map<String, Object>> focusQueues;

    private List<Map<String, Object>> quickActions;

    private List<Map<String, Object>> quickSections;

    private List<Map<String, Object>> hintTags;

    private List<Map<String, Object>> queueSections;

    private List<Map<String, Object>> focusPanels;

    private List<Map<String, Object>> workflowSteps;

    private List<Map<String, Object>> ygbExplanation;

    private List<Map<String, Object>> azbExplanation;

    private Map<String, Object> defaultQuery;

    private String sourceLabel;

    private String sourceDescription;

    private String homeSummary;

    public YgbCockpitIndicator getIndicators()
    {
        return indicators;
    }

    public void setIndicators(YgbCockpitIndicator indicators)
    {
        this.indicators = indicators;
    }

    public List<YgbCockpitSnapshot> getTrend()
    {
        return trend;
    }

    public void setTrend(List<YgbCockpitSnapshot> trend)
    {
        this.trend = trend;
    }

    public List<YgbCockpitDistribution> getDistribution()
    {
        return distribution;
    }

    public void setDistribution(List<YgbCockpitDistribution> distribution)
    {
        this.distribution = distribution;
    }

    public YgbGeoJsonFeatureCollection getMap()
    {
        return map;
    }

    public void setMap(YgbGeoJsonFeatureCollection map)
    {
        this.map = map;
    }

    public YgbContractSummary getContractSummary()
    {
        return contractSummary;
    }

    public void setContractSummary(YgbContractSummary contractSummary)
    {
        this.contractSummary = contractSummary;
    }

    public YgbAttendanceRawSummary getAttendanceRawSummary()
    {
        return attendanceRawSummary;
    }

    public void setAttendanceRawSummary(YgbAttendanceRawSummary attendanceRawSummary)
    {
        this.attendanceRawSummary = attendanceRawSummary;
    }

    public YgbAttendanceMonthlySummary getAttendanceMonthlySummary()
    {
        return attendanceMonthlySummary;
    }

    public void setAttendanceMonthlySummary(YgbAttendanceMonthlySummary attendanceMonthlySummary)
    {
        this.attendanceMonthlySummary = attendanceMonthlySummary;
    }

    public YgbSalaryBatchSummary getSalaryBatchSummary()
    {
        return salaryBatchSummary;
    }

    public void setSalaryBatchSummary(YgbSalaryBatchSummary salaryBatchSummary)
    {
        this.salaryBatchSummary = salaryBatchSummary;
    }

    public YgbSalaryDetailSummary getSalaryDetailSummary()
    {
        return salaryDetailSummary;
    }

    public void setSalaryDetailSummary(YgbSalaryDetailSummary salaryDetailSummary)
    {
        this.salaryDetailSummary = salaryDetailSummary;
    }

    public YgbSocialPaymentSummary getSocialPaymentSummary()
    {
        return socialPaymentSummary;
    }

    public void setSocialPaymentSummary(YgbSocialPaymentSummary socialPaymentSummary)
    {
        this.socialPaymentSummary = socialPaymentSummary;
    }

    public YgbSocialBaseCompareSummary getSocialBaseCompareSummary()
    {
        return socialBaseCompareSummary;
    }

    public void setSocialBaseCompareSummary(YgbSocialBaseCompareSummary socialBaseCompareSummary)
    {
        this.socialBaseCompareSummary = socialBaseCompareSummary;
    }

    public YgbTaxCompareSummary getTaxCompareSummary()
    {
        return taxCompareSummary;
    }

    public void setTaxCompareSummary(YgbTaxCompareSummary taxCompareSummary)
    {
        this.taxCompareSummary = taxCompareSummary;
    }

    public YgbEmploymentRatioSummary getEmploymentRatioSummary()
    {
        return employmentRatioSummary;
    }

    public void setEmploymentRatioSummary(YgbEmploymentRatioSummary employmentRatioSummary)
    {
        this.employmentRatioSummary = employmentRatioSummary;
    }

    public YgbFakeOutsourcingSummary getFakeOutsourcingSummary()
    {
        return fakeOutsourcingSummary;
    }

    public void setFakeOutsourcingSummary(YgbFakeOutsourcingSummary fakeOutsourcingSummary)
    {
        this.fakeOutsourcingSummary = fakeOutsourcingSummary;
    }

    public YgbNewformWorkerSummary getNewformWorkerSummary()
    {
        return newformWorkerSummary;
    }

    public void setNewformWorkerSummary(YgbNewformWorkerSummary newformWorkerSummary)
    {
        this.newformWorkerSummary = newformWorkerSummary;
    }

    public YgbOccupationMonitorSummary getOccupationMonitorSummary()
    {
        return occupationMonitorSummary;
    }

    public void setOccupationMonitorSummary(YgbOccupationMonitorSummary occupationMonitorSummary)
    {
        this.occupationMonitorSummary = occupationMonitorSummary;
    }

    public YgbWarningSummary getWarningSummary()
    {
        return warningSummary;
    }

    public void setWarningSummary(YgbWarningSummary warningSummary)
    {
        this.warningSummary = warningSummary;
    }

    public YgbHeightWorkReportSummary getHeightWorkReportSummary()
    {
        return heightWorkReportSummary;
    }

    public void setHeightWorkReportSummary(YgbHeightWorkReportSummary heightWorkReportSummary)
    {
        this.heightWorkReportSummary = heightWorkReportSummary;
    }

    public YgbUninsuredListSummary getUninsuredListSummary()
    {
        return uninsuredListSummary;
    }

    public void setUninsuredListSummary(YgbUninsuredListSummary uninsuredListSummary)
    {
        this.uninsuredListSummary = uninsuredListSummary;
    }

    public YgbStatReportSummary getStatReportSummary()
    {
        return statReportSummary;
    }

    public void setStatReportSummary(YgbStatReportSummary statReportSummary)
    {
        this.statReportSummary = statReportSummary;
    }

    public YgbEnterpriseSummary getEnterpriseSummary()
    {
        return enterpriseSummary;
    }

    public void setEnterpriseSummary(YgbEnterpriseSummary enterpriseSummary)
    {
        this.enterpriseSummary = enterpriseSummary;
    }

    public YgbPersonSummary getPersonSummary()
    {
        return personSummary;
    }

    public void setPersonSummary(YgbPersonSummary personSummary)
    {
        this.personSummary = personSummary;
    }

    public List<Map<String, Object>> getSummaryCards()
    {
        return summaryCards;
    }

    public void setSummaryCards(List<Map<String, Object>> summaryCards)
    {
        this.summaryCards = summaryCards;
    }

    public List<Map<String, Object>> getFocusQueues()
    {
        return focusQueues;
    }

    public void setFocusQueues(List<Map<String, Object>> focusQueues)
    {
        this.focusQueues = focusQueues;
    }

    public List<Map<String, Object>> getQuickActions()
    {
        return quickActions;
    }

    public void setQuickActions(List<Map<String, Object>> quickActions)
    {
        this.quickActions = quickActions;
    }

    public List<Map<String, Object>> getQuickSections()
    {
        return quickSections;
    }

    public void setQuickSections(List<Map<String, Object>> quickSections)
    {
        this.quickSections = quickSections;
    }

    public List<Map<String, Object>> getHintTags()
    {
        return hintTags;
    }

    public void setHintTags(List<Map<String, Object>> hintTags)
    {
        this.hintTags = hintTags;
    }

    public List<Map<String, Object>> getQueueSections()
    {
        return queueSections;
    }

    public void setQueueSections(List<Map<String, Object>> queueSections)
    {
        this.queueSections = queueSections;
    }

    public List<Map<String, Object>> getFocusPanels()
    {
        return focusPanels;
    }

    public void setFocusPanels(List<Map<String, Object>> focusPanels)
    {
        this.focusPanels = focusPanels;
    }

    public List<Map<String, Object>> getYgbExplanation()
    {
        return ygbExplanation;
    }

    public List<Map<String, Object>> getWorkflowSteps()
    {
        return workflowSteps;
    }

    public void setWorkflowSteps(List<Map<String, Object>> workflowSteps)
    {
        this.workflowSteps = workflowSteps;
    }

    public void setYgbExplanation(List<Map<String, Object>> ygbExplanation)
    {
        this.ygbExplanation = ygbExplanation;
    }

    public List<Map<String, Object>> getAzbExplanation()
    {
        return azbExplanation;
    }

    public void setAzbExplanation(List<Map<String, Object>> azbExplanation)
    {
        this.azbExplanation = azbExplanation;
    }

    public Map<String, Object> getDefaultQuery()
    {
        return defaultQuery;
    }

    public void setDefaultQuery(Map<String, Object> defaultQuery)
    {
        this.defaultQuery = defaultQuery;
    }

    public String getSourceLabel()
    {
        return sourceLabel;
    }

    public void setSourceLabel(String sourceLabel)
    {
        this.sourceLabel = sourceLabel;
    }

    public String getSourceDescription()
    {
        return sourceDescription;
    }

    public void setSourceDescription(String sourceDescription)
    {
        this.sourceDescription = sourceDescription;
    }

    public String getHomeSummary()
    {
        return homeSummary;
    }

    public void setHomeSummary(String homeSummary)
    {
        this.homeSummary = homeSummary;
    }
}
