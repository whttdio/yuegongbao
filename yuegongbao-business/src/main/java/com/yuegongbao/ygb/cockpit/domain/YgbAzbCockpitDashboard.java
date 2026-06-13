package com.yuegongbao.ygb.cockpit.domain;

import java.util.Map;
import java.util.List;
import com.yuegongbao.ygb.aqins.domain.YgbAqInsuranceSummary;
import com.yuegongbao.ygb.aqins.domain.YgbPreventionFundSummary;
import com.yuegongbao.ygb.credit.domain.YgbCreditScoreSummary;
import com.yuegongbao.ygb.foundation.domain.YgbEnterpriseSummary;
import com.yuegongbao.ygb.foundation.domain.YgbPersonSummary;
import com.yuegongbao.ygb.report.domain.YgbStatReportSummary;
import com.yuegongbao.ygb.safety.domain.YgbDeviceSummary;
import com.yuegongbao.ygb.safety.domain.YgbInjuryEventSummary;
import com.yuegongbao.ygb.techdefense.domain.YgbHeightWorkReportSummary;
import com.yuegongbao.ygb.warning.domain.YgbWarningSummary;

public class YgbAzbCockpitDashboard
{
    private YgbCockpitIndicator indicators;

    private List<YgbCockpitSnapshot> trend;

    private List<YgbCockpitDistribution> distribution;

    private YgbGeoJsonFeatureCollection map;

    private String roleView;

    private String roleLabel;

    private String roleDescription;

    private List<Map<String, Object>> summaryCards;

    private List<Map<String, Object>> focusQueues;

    private List<Map<String, Object>> workflowSteps;

    private List<Map<String, Object>> quickActions;

    private List<Map<String, Object>> quickSections;

    private List<Map<String, Object>> hintTags;

    private List<Map<String, Object>> queueSections;

    private List<Map<String, Object>> focusPanels;

    private List<Map<String, Object>> azbExplanation;

    private Map<String, Object> defaultQuery;

    private String sourceLabel;

    private String sourceDescription;

    private String homeSummary;

    private YgbAqInsuranceSummary aqInsuranceSummary;

    private YgbPreventionFundSummary preventionFundSummary;

    private YgbCreditScoreSummary creditScoreSummary;

    private YgbWarningSummary warningSummary;

    private YgbHeightWorkReportSummary heightWorkReportSummary;

    private YgbDeviceSummary deviceSummary;

    private YgbInjuryEventSummary injuryEventSummary;

    private YgbStatReportSummary statReportSummary;

    private YgbEnterpriseSummary enterpriseSummary;

    private YgbPersonSummary personSummary;

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

    public String getRoleView()
    {
        return roleView;
    }

    public void setRoleView(String roleView)
    {
        this.roleView = roleView;
    }

    public String getRoleLabel()
    {
        return roleLabel;
    }

    public void setRoleLabel(String roleLabel)
    {
        this.roleLabel = roleLabel;
    }

    public String getRoleDescription()
    {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription)
    {
        this.roleDescription = roleDescription;
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

    public List<Map<String, Object>> getWorkflowSteps()
    {
        return workflowSteps;
    }

    public void setWorkflowSteps(List<Map<String, Object>> workflowSteps)
    {
        this.workflowSteps = workflowSteps;
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

    public YgbAqInsuranceSummary getAqInsuranceSummary()
    {
        return aqInsuranceSummary;
    }

    public void setAqInsuranceSummary(YgbAqInsuranceSummary aqInsuranceSummary)
    {
        this.aqInsuranceSummary = aqInsuranceSummary;
    }

    public YgbPreventionFundSummary getPreventionFundSummary()
    {
        return preventionFundSummary;
    }

    public void setPreventionFundSummary(YgbPreventionFundSummary preventionFundSummary)
    {
        this.preventionFundSummary = preventionFundSummary;
    }

    public YgbCreditScoreSummary getCreditScoreSummary()
    {
        return creditScoreSummary;
    }

    public void setCreditScoreSummary(YgbCreditScoreSummary creditScoreSummary)
    {
        this.creditScoreSummary = creditScoreSummary;
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

    public YgbDeviceSummary getDeviceSummary()
    {
        return deviceSummary;
    }

    public void setDeviceSummary(YgbDeviceSummary deviceSummary)
    {
        this.deviceSummary = deviceSummary;
    }

    public YgbInjuryEventSummary getInjuryEventSummary()
    {
        return injuryEventSummary;
    }

    public void setInjuryEventSummary(YgbInjuryEventSummary injuryEventSummary)
    {
        this.injuryEventSummary = injuryEventSummary;
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
}
