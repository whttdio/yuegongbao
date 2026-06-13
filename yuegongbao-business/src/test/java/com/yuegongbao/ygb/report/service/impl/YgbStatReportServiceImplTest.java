package com.yuegongbao.ygb.report.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.yuegongbao.ygb.report.domain.YgbStatReport;
import com.yuegongbao.ygb.report.domain.YgbStatReportGenerateRequest;
import com.yuegongbao.ygb.report.domain.YgbStatReportItem;
import com.yuegongbao.ygb.report.domain.YgbStatReportSummary;
import com.yuegongbao.ygb.report.mapper.YgbStatReportMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YgbStatReportServiceImplTest
{
    @Mock
    private YgbStatReportMapper statReportMapper;

    @InjectMocks
    private YgbStatReportServiceImpl service;

    @Test
    void generateReportCreatesWarningOverviewWithSummaryAndItems()
    {
        YgbStatReportItem item = new YgbStatReportItem();
        item.setItemDimension("TAX");
        item.setMetricCount(10);
        item.setMetricValue(new BigDecimal("2.00"));
        item.setMetricRate(new BigDecimal("20.00"));

        when(statReportMapper.selectWarningOverviewItems(eq("2026-06"), eq("44"))).thenReturn(List.of(item));
        when(statReportMapper.selectStatReportByScope(eq("WARNING_OVERVIEW"), eq("2026-06"), eq("440000")))
            .thenReturn(null);
        doAnswer(invocation -> {
            YgbStatReport report = invocation.getArgument(0);
            report.setReportId(91099L);
            return 1;
        }).when(statReportMapper).insertStatReport(any(YgbStatReport.class));

        YgbStatReportGenerateRequest request = new YgbStatReportGenerateRequest();
        request.setReportCode("warning_overview");
        request.setStatMonth("2026-06");
        request.setRegionCode("440000");

        Long reportId = service.generateReport(request, "tester");

        assertEquals(91099L, reportId);
        ArgumentCaptor<YgbStatReport> reportCaptor = ArgumentCaptor.forClass(YgbStatReport.class);
        verify(statReportMapper).insertStatReport(reportCaptor.capture());
        assertEquals("预警治理月报", reportCaptor.getValue().getReportName());
        assertEquals(10, reportCaptor.getValue().getMetricCount());
        assertEquals(new BigDecimal("2.00"), reportCaptor.getValue().getMetricAmount());
        assertTrue(reportCaptor.getValue().getReportSummary().contains("闭环率 20.00%"));

        verify(statReportMapper, times(1)).insertStatReportItem(any(YgbStatReportItem.class));
    }

    @Test
    void generateReportBuildsSocialTaxRiskItems()
    {
        when(statReportMapper.selectStatReportByScope(eq("SOCIAL_TAX"), eq("2026-05"), eq("440000"))).thenReturn(null);
        when(statReportMapper.countSocialCompareAbnormal(eq("2026-05"), eq("44"))).thenReturn(1);
        when(statReportMapper.countSocialCompareTotal(eq("2026-05"), eq("44"))).thenReturn(3);
        when(statReportMapper.countTaxCompareAbnormal(eq("2026-05"), eq("44"))).thenReturn(4);
        when(statReportMapper.countTaxCompareTotal(eq("2026-05"), eq("44"))).thenReturn(6);
        when(statReportMapper.countUninsured(eq("2026-05"), eq("44"))).thenReturn(2);
        when(statReportMapper.countEmploymentByLevel(eq("2026-05"), eq("44"), eq("1"))).thenReturn(1);
        when(statReportMapper.countEmploymentByLevel(eq("2026-05"), eq("44"), eq("2"))).thenReturn(1);
        when(statReportMapper.countEmploymentTotal(eq("2026-05"), eq("44"))).thenReturn(2);
        when(statReportMapper.countFakeOutsourcingSuspected(eq("2026-05"), eq("44"))).thenReturn(1);
        when(statReportMapper.countFakeOutsourcingTotal(eq("2026-05"), eq("44"))).thenReturn(2);
        doAnswer(invocation -> {
            YgbStatReport report = invocation.getArgument(0);
            report.setReportId(91100L);
            return 1;
        }).when(statReportMapper).insertStatReport(any(YgbStatReport.class));

        YgbStatReportGenerateRequest request = new YgbStatReportGenerateRequest();
        request.setReportCode("SOCIAL_TAX");
        request.setStatMonth("2026-05");
        request.setRegionCode("440000");

        service.generateReport(request, "tester");

        verify(statReportMapper, times(6)).insertStatReportItem(any(YgbStatReportItem.class));
        ArgumentCaptor<YgbStatReport> reportCaptor = ArgumentCaptor.forClass(YgbStatReport.class);
        verify(statReportMapper).insertStatReport(reportCaptor.capture());
        assertEquals("社保税务联动月报", reportCaptor.getValue().getReportName());
        assertEquals(10, reportCaptor.getValue().getMetricCount());
    }

    @Test
    void selectStatReportSummaryBuildsDifferentPortalExplanationsForSameScope()
    {
        YgbStatReport socialTax = new YgbStatReport();
        socialTax.setReportCode("SOCIAL_TAX");
        socialTax.setReportStatus("1");
        socialTax.setRegionCode("440000");
        socialTax.setStatMonth("2026-06");

        YgbStatReport warningOverview = new YgbStatReport();
        warningOverview.setReportCode("WARNING_OVERVIEW");
        warningOverview.setReportStatus("0");
        warningOverview.setRegionCode("440000");
        warningOverview.setStatMonth("2026-06");

        YgbStatReport injuryRate = new YgbStatReport();
        injuryRate.setReportCode("INJURY_RATE");
        injuryRate.setReportStatus("1");
        injuryRate.setRegionCode("440000");
        injuryRate.setStatMonth("2026-06");

        when(statReportMapper.selectStatReportList(any(YgbStatReport.class))).thenReturn(List.of(socialTax, warningOverview, injuryRate));

        YgbStatReport query = new YgbStatReport();
        query.setReportCode("SOCIAL_TAX");
        query.setRegionCode("440000");
        query.setStatMonth("2026-06");

        YgbStatReportSummary summary = service.selectStatReportSummary(query);

        assertEquals(3, summary.getYgbExplanation().size());
        assertEquals(3, summary.getAzbExplanation().size());
        assertEquals("statReport", summary.getYgbExplanation().get(0).get("recommendModule"));
        assertEquals("socialBaseCompare", summary.getYgbExplanation().get(2).get("recommendModule"));
        assertEquals("warning", summary.getAzbExplanation().get(1).get("recommendModule"));
        assertEquals("statReport", summary.getAzbExplanation().get(2).get("recommendModule"));
        assertTrue(String.valueOf(summary.getYgbExplanation().get(0).get("sourceLabel")).contains("530.1"));
        assertTrue(String.valueOf(summary.getAzbExplanation().get(0).get("sourceLabel")).contains("6.1"));
        assertEquals("SOCIAL_TAX", castMap(summary.getYgbExplanation().get(0).get("defaultQuery")).get("reportCode"));
        assertEquals("2026-06", castMap(summary.getAzbExplanation().get(0).get("defaultQuery")).get("statMonth"));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> castMap(Object value)
    {
        return (Map<String, Object>) value;
    }
}
