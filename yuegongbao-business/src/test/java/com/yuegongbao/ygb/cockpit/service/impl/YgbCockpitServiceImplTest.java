package com.yuegongbao.ygb.cockpit.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import com.yuegongbao.ygb.aqins.domain.YgbAqInsuranceSummary;
import com.yuegongbao.ygb.cockpit.domain.YgbAzbCockpitDashboard;
import com.yuegongbao.ygb.cockpit.domain.YgbCockpitDistribution;
import com.yuegongbao.ygb.cockpit.domain.YgbCockpitIndicator;
import com.yuegongbao.ygb.cockpit.domain.YgbCockpitSnapshot;
import com.yuegongbao.ygb.cockpit.domain.YgbWorkbenchDashboard;
import com.yuegongbao.ygb.cockpit.mapper.YgbCockpitMapper;
import com.yuegongbao.ygb.compliance.domain.YgbContractSummary;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryDetailSummary;
import com.yuegongbao.ygb.credit.domain.YgbCreditScoreSummary;
import com.yuegongbao.ygb.foundation.domain.YgbEnterpriseSummary;
import com.yuegongbao.ygb.regulation.domain.YgbSocialBaseCompareSummary;
import com.yuegongbao.ygb.regulation.domain.YgbTaxCompareSummary;
import com.yuegongbao.ygb.regulation.domain.YgbUninsuredListSummary;
import com.yuegongbao.ygb.report.domain.YgbStatReportSummary;
import com.yuegongbao.ygb.safety.domain.YgbDeviceSummary;
import com.yuegongbao.ygb.techdefense.domain.YgbHeightWorkReportSummary;
import com.yuegongbao.ygb.warning.domain.YgbWarningSummary;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YgbCockpitServiceImplTest
{
    @Mock
    private YgbCockpitMapper cockpitMapper;

    @InjectMocks
    private YgbCockpitServiceImpl service;

    @Test
    void getIndicatorsMergesSnapshotAndRealtimeCounts()
    {
        YgbCockpitSnapshot snapshot = new YgbCockpitSnapshot();
        snapshot.setDispatchCompanyCount(1);
        snapshot.setEmployerCount(2);
        snapshot.setDispatchedWorkerCount(8);
        snapshot.setHighRiskEnterpriseCount(3);
        snapshot.setInsuranceRate(new BigDecimal("84.50"));
        snapshot.setAqInsuranceRate(new BigDecimal("73.60"));
        snapshot.setExpandCompletionRate(new BigDecimal("89.80"));
        snapshot.setNewInjuryRate(new BigDecimal("0.85"));

        when(cockpitMapper.selectLatestSnapshot(eq("440000"), eq("2026-06-03"))).thenReturn(snapshot);
        when(cockpitMapper.countWarningsByDate(eq("44"), eq("2026-06-03"))).thenReturn(18);
        when(cockpitMapper.countPendingWarnings(eq("44"))).thenReturn(11);
        when(cockpitMapper.countOnlineDevices(eq("44"))).thenReturn(2);
        when(cockpitMapper.countOverdueInjuries(eq("44"))).thenReturn(1);

        YgbCockpitIndicator indicator = service.getIndicators("440000", "2026-06-03");

        assertEquals("广东省", indicator.getRegionName());
        assertEquals(1, indicator.getDispatchCompanyCount());
        assertEquals(2, indicator.getEmployerCount());
        assertEquals(8, indicator.getDispatchedWorkerCount());
        assertEquals(18, indicator.getTodayWarningCount());
        assertEquals(11, indicator.getPendingWarningCount());
        assertEquals(2, indicator.getOnlineDeviceCount());
        assertEquals(1, indicator.getOverdueInjuryCount());
        assertEquals(new BigDecimal("84.50"), indicator.getInsuranceRate());
    }

    @Test
    void listWarningDistributionCalculatesPercentages()
    {
        YgbCockpitDistribution social = new YgbCockpitDistribution();
        social.setDimensionCode("SOCIAL");
        social.setMetricCount(2);
        YgbCockpitDistribution tax = new YgbCockpitDistribution();
        tax.setDimensionCode("TAX");
        tax.setMetricCount(3);

        when(cockpitMapper.selectWarningDistribution(eq("44"), eq("2026-06"))).thenReturn(List.of(social, tax));

        List<YgbCockpitDistribution> result = service.listWarningDistribution("440000", "2026-06");

        assertEquals(2, result.size());
        assertEquals("社保监管", result.get(0).getDimensionName());
        assertEquals(new BigDecimal("40.00"), result.get(0).getMetricRate());
        assertEquals(new BigDecimal("60.00"), result.get(1).getMetricRate());
    }

    @Test
    void homeExplanationsKeepPortalSpecificOrderAndDrilldowns()
    {
        YgbWorkbenchDashboard ygbDashboard = new YgbWorkbenchDashboard();
        YgbContractSummary contractSummary = new YgbContractSummary();
        contractSummary.setPendingCount(4);
        ygbDashboard.setContractSummary(contractSummary);
        YgbSalaryDetailSummary salaryDetailSummary = new YgbSalaryDetailSummary();
        salaryDetailSummary.setFailedCount(2);
        ygbDashboard.setSalaryDetailSummary(salaryDetailSummary);
        YgbSocialBaseCompareSummary socialBaseCompareSummary = new YgbSocialBaseCompareSummary();
        socialBaseCompareSummary.setAbnormalCount(3);
        ygbDashboard.setSocialBaseCompareSummary(socialBaseCompareSummary);
        YgbTaxCompareSummary taxCompareSummary = new YgbTaxCompareSummary();
        taxCompareSummary.setAbnormalCount(1);
        ygbDashboard.setTaxCompareSummary(taxCompareSummary);
        YgbUninsuredListSummary uninsuredListSummary = new YgbUninsuredListSummary();
        uninsuredListSummary.setPendingCount(5);
        ygbDashboard.setUninsuredListSummary(uninsuredListSummary);
        YgbWarningSummary ygbWarningSummary = new YgbWarningSummary();
        ygbWarningSummary.setPendingCount(6);
        ygbDashboard.setWarningSummary(ygbWarningSummary);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> ygbExplanation = (List<Map<String, Object>>) ReflectionTestUtils.invokeMethod(service,
            "buildYgbHomeExplanation", ygbDashboard, "440000", "2026-06");

        YgbAzbCockpitDashboard azbDashboard = new YgbAzbCockpitDashboard();
        YgbAqInsuranceSummary aqInsuranceSummary = new YgbAqInsuranceSummary();
        aqInsuranceSummary.setCoverageRate(new BigDecimal("91.20"));
        aqInsuranceSummary.setRiskCount(4);
        azbDashboard.setAqInsuranceSummary(aqInsuranceSummary);
        YgbDeviceSummary deviceSummary = new YgbDeviceSummary();
        deviceSummary.setLockedOrFaultCount(3);
        deviceSummary.setUnauthorizedCount(2);
        azbDashboard.setDeviceSummary(deviceSummary);
        YgbHeightWorkReportSummary heightWorkReportSummary = new YgbHeightWorkReportSummary();
        heightWorkReportSummary.setActiveCount(7);
        azbDashboard.setHeightWorkReportSummary(heightWorkReportSummary);
        YgbWarningSummary azbWarningSummary = new YgbWarningSummary();
        azbWarningSummary.setPendingCount(5);
        azbDashboard.setWarningSummary(azbWarningSummary);
        YgbStatReportSummary statReportSummary = new YgbStatReportSummary();
        statReportSummary.setGeneratedCount(2);
        azbDashboard.setStatReportSummary(statReportSummary);
        YgbCreditScoreSummary creditScoreSummary = new YgbCreditScoreSummary();
        creditScoreSummary.setRedCount(3);
        azbDashboard.setCreditScoreSummary(creditScoreSummary);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> azbExplanation = (List<Map<String, Object>>) ReflectionTestUtils.invokeMethod(service,
            "buildAzbHomeExplanation", azbDashboard, "emergency", "440000", "2026-06");

        assertEquals(5, ygbExplanation.size());
        assertEquals(6, azbExplanation.size());
        assertEquals("contract", ygbExplanation.get(0).get("recommendModule"));
        assertEquals("salaryDetail", ygbExplanation.get(1).get("recommendModule"));
        assertEquals("aqInsurance", azbExplanation.get(0).get("recommendModule"));
        assertEquals("device", azbExplanation.get(1).get("recommendModule"));
        assertEquals("heightWorkReport", azbExplanation.get(2).get("recommendModule"));
        assertEquals("warning", azbExplanation.get(3).get("recommendModule"));
        assertEquals("statReport", azbExplanation.get(4).get("recommendModule"));
        assertEquals("creditScore", azbExplanation.get(5).get("recommendModule"));
        assertEquals("1", castMap(ygbExplanation.get(0).get("defaultQuery")).get("contractStatus"));
        assertEquals("3", castMap(ygbExplanation.get(1).get("defaultQuery")).get("payStatus"));
        assertEquals("2", castMap(azbExplanation.get(0).get("defaultQuery")).get("policyStatus"));
        assertEquals("0", castMap(azbExplanation.get(1).get("defaultQuery")).get("authStatus"));
        assertNotEquals(ygbExplanation.get(0).get("recommendModule"), azbExplanation.get(0).get("recommendModule"));
    }

    @Test
    void homeAggregatesKeepPortalSpecificCardsQuickActionsAndQueues()
    {
        YgbWorkbenchDashboard ygbDashboard = new YgbWorkbenchDashboard();
        YgbEnterpriseSummary enterpriseSummary = new YgbEnterpriseSummary();
        enterpriseSummary.setSyncErrorCount(1);
        ygbDashboard.setEnterpriseSummary(enterpriseSummary);
        YgbWarningSummary ygbWarningSummary = new YgbWarningSummary();
        ygbWarningSummary.setPendingCount(6);
        ygbDashboard.setWarningSummary(ygbWarningSummary);
        YgbStatReportSummary ygbStatReportSummary = new YgbStatReportSummary();
        ygbStatReportSummary.setDraftCount(3);
        ygbDashboard.setStatReportSummary(ygbStatReportSummary);
        YgbSalaryDetailSummary ygbSalaryDetailSummary = new YgbSalaryDetailSummary();
        ygbSalaryDetailSummary.setFailedCount(2);
        ygbDashboard.setSalaryDetailSummary(ygbSalaryDetailSummary);
        com.yuegongbao.ygb.regulation.domain.YgbSocialPaymentSummary ygbSocialPaymentSummary =
            new com.yuegongbao.ygb.regulation.domain.YgbSocialPaymentSummary();
        ygbSocialPaymentSummary.setOverdueCount(4);
        ygbDashboard.setSocialPaymentSummary(ygbSocialPaymentSummary);
        YgbTaxCompareSummary ygbTaxCompareSummary = new YgbTaxCompareSummary();
        ygbTaxCompareSummary.setAbnormalCount(1);
        ygbDashboard.setTaxCompareSummary(ygbTaxCompareSummary);
        YgbUninsuredListSummary ygbUninsuredListSummary = new YgbUninsuredListSummary();
        ygbUninsuredListSummary.setPendingCount(5);
        ygbDashboard.setUninsuredListSummary(ygbUninsuredListSummary);
        YgbContractSummary ygbContractSummary = new YgbContractSummary();
        ygbContractSummary.setPendingCount(4);
        ygbDashboard.setContractSummary(ygbContractSummary);
        com.yuegongbao.ygb.compliance.domain.YgbSalaryBatchSummary ygbSalaryBatchSummary =
            new com.yuegongbao.ygb.compliance.domain.YgbSalaryBatchSummary();
        ygbSalaryBatchSummary.setPendingGenerateCount(2);
        ygbDashboard.setSalaryBatchSummary(ygbSalaryBatchSummary);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> ygbCards = (List<Map<String, Object>>) ReflectionTestUtils.invokeMethod(service,
            "buildYgbSummaryCards", ygbDashboard, "2026-06");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> ygbQuickActions = (List<Map<String, Object>>) ReflectionTestUtils.invokeMethod(service,
            "buildYgbQuickActions", "440000", "2026-06-03", "2026-06");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> ygbQueueSections = (List<Map<String, Object>>) ReflectionTestUtils.invokeMethod(service,
            "buildYgbQueueSections", ygbDashboard, "440000", "2026-06-03", "2026-06");

        assertEquals(List.of("enterprisePending", "enterpriseRisk", "monthStatus", "priorityQueue"),
            ygbCards.stream().map(item -> String.valueOf(item.get("key"))).toList());
        assertEquals(6, ygbCards.get(0).get("value"));
        assertEquals(6, ygbCards.get(1).get("value"));
        assertEquals(3, ygbCards.get(2).get("value"));
        assertEquals(7, ygbCards.get(3).get("value"));

        Map<String, Object> ygbContractAction = findByKey(ygbQuickActions, "contract");
        Map<String, Object> ygbSalaryBatchAction = findByKey(ygbQuickActions, "salaryBatch");
        assertEquals("/ygb/contract", ygbContractAction.get("path"));
        assertEquals("/ygb/salaryBatch", ygbSalaryBatchAction.get("path"));
        assertEquals("1", castMap(ygbContractAction.get("defaultQuery")).get("contractStatus"));
        assertEquals("3", castMap(ygbSalaryBatchAction.get("defaultQuery")).get("batchStatus"));

        assertEquals(List.of("enterprise", "month", "priority"),
            ygbQueueSections.stream().map(item -> String.valueOf(item.get("key"))).toList());
        Map<String, Object> ygbEnterpriseSection = findByKey(ygbQueueSections, "enterprise");
        Map<String, Object> ygbMonthSection = findByKey(ygbQueueSections, "month");
        Map<String, Object> ygbPrioritySection = findByKey(ygbQueueSections, "priority");
        assertTrue(castList(ygbEnterpriseSection.get("items")).size() >= 2);
        assertTrue(castList(ygbEnterpriseSection.get("items")).stream()
            .anyMatch(item -> "contract".equals(String.valueOf(item.get("moduleCode")))));
        assertTrue(castList(ygbEnterpriseSection.get("items")).stream()
            .anyMatch(item -> "warning".equals(String.valueOf(item.get("moduleCode")))));
        assertTrue(castList(ygbMonthSection.get("items")).size() >= 3);
        assertTrue(castList(ygbMonthSection.get("items")).stream()
            .anyMatch(item -> "salaryBatch".equals(String.valueOf(item.get("moduleCode")))));
        assertTrue(castList(ygbMonthSection.get("items")).stream()
            .anyMatch(item -> "socialPayment".equals(String.valueOf(item.get("moduleCode")))));
        assertTrue(castList(ygbPrioritySection.get("items")).size() >= 2);

        YgbAzbCockpitDashboard azbDashboard = new YgbAzbCockpitDashboard();
        YgbAqInsuranceSummary aqInsuranceSummary = new YgbAqInsuranceSummary();
        aqInsuranceSummary.setCoverageRate(new BigDecimal("91.20"));
        aqInsuranceSummary.setRiskCount(4);
        azbDashboard.setAqInsuranceSummary(aqInsuranceSummary);
        YgbDeviceSummary azbDeviceSummary = new YgbDeviceSummary();
        azbDeviceSummary.setOnlineCount(9);
        azbDeviceSummary.setLockedOrFaultCount(3);
        azbDeviceSummary.setUnauthorizedCount(2);
        azbDashboard.setDeviceSummary(azbDeviceSummary);
        YgbHeightWorkReportSummary azbHeightWorkSummary = new YgbHeightWorkReportSummary();
        azbHeightWorkSummary.setActiveCount(7);
        azbDashboard.setHeightWorkReportSummary(azbHeightWorkSummary);
        YgbWarningSummary azbWarningSummary = new YgbWarningSummary();
        azbWarningSummary.setPendingCount(5);
        azbDashboard.setWarningSummary(azbWarningSummary);
        YgbCreditScoreSummary azbCreditScoreSummary = new YgbCreditScoreSummary();
        azbCreditScoreSummary.setRedCount(3);
        azbDashboard.setCreditScoreSummary(azbCreditScoreSummary);
        YgbStatReportSummary azbStatReportSummary = new YgbStatReportSummary();
        azbStatReportSummary.setGeneratedCount(2);
        azbDashboard.setStatReportSummary(azbStatReportSummary);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> azbCards = (List<Map<String, Object>>) ReflectionTestUtils.invokeMethod(service,
            "buildAzbSummaryCards", azbDashboard, "emergency");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> azbQuickActions = (List<Map<String, Object>>) ReflectionTestUtils.invokeMethod(service,
            "buildAzbQuickActions", "emergency", "440000", "2026-06-03", "2026-06");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> azbQueueSections = (List<Map<String, Object>>) ReflectionTestUtils.invokeMethod(service,
            "buildAzbQueueSections", azbDashboard, "emergency", "440000", "2026-06-03", "2026-06");

        assertEquals(List.of("warningPending", "deviceOnline", "heightActive", "coverageRate"),
            azbCards.stream().map(item -> String.valueOf(item.get("key"))).toList());
        assertEquals(5, azbCards.get(0).get("value"));
        assertEquals(9, azbCards.get(1).get("value"));
        assertEquals(7, azbCards.get(2).get("value"));
        assertEquals("91.20", azbCards.get(3).get("value"));

        assertEquals(List.of("warning", "device", "heightWorkReport", "aqInsurance", "creditScore", "statReport"),
            azbQuickActions.stream().map(item -> String.valueOf(item.get("key"))).toList());
        Map<String, Object> azbWarningAction = findByKey(azbQuickActions, "warning");
        Map<String, Object> azbCreditAction = findByKey(azbQuickActions, "creditScore");
        assertEquals("/azb/warning", azbWarningAction.get("path"));
        assertEquals("/azb/creditScore", azbCreditAction.get("path"));
        assertEquals("0", castMap(azbWarningAction.get("defaultQuery")).get("warnStatus"));
        assertEquals("RED", castMap(azbCreditAction.get("defaultQuery")).get("colorCode"));

        assertEquals(List.of("focusObject", "governance"),
            azbQueueSections.stream().map(item -> String.valueOf(item.get("key"))).toList());
        Map<String, Object> azbFocusObjectSection = findByKey(azbQueueSections, "focusObject");
        Map<String, Object> azbGovernanceSection = findByKey(azbQueueSections, "governance");
        assertEquals(List.of("warning", "device", "aqInsurance"),
            castList(azbFocusObjectSection.get("items")).stream().map(item -> String.valueOf(item.get("moduleCode"))).toList());
        assertTrue(castList(azbGovernanceSection.get("items")).stream()
            .anyMatch(item -> "creditScore".equals(String.valueOf(item.get("moduleCode")))));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> castMap(Object value)
    {
        return (Map<String, Object>) value;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> castList(Object value)
    {
        return (List<Map<String, Object>>) value;
    }

    private static Map<String, Object> findByKey(List<Map<String, Object>> list, String key)
    {
        return list.stream()
            .filter(item -> key.equals(String.valueOf(item.get("key"))))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Missing key: " + key));
    }
}
