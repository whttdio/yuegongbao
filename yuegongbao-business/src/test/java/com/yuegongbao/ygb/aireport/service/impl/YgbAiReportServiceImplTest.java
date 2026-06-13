package com.yuegongbao.ygb.aireport.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.alibaba.fastjson2.JSON;
import com.yuegongbao.ygb.aireport.domain.YgbAiReport;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportConfig;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportDashboard;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportGenerateRequest;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportItem;
import com.yuegongbao.ygb.aireport.mapper.YgbAiReportMapper;
import com.yuegongbao.ygb.aireport.service.IYgbAiReportConfigService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YgbAiReportServiceImplTest
{
    @Mock
    private YgbAiReportMapper aiReportMapper;

    @Mock
    private IYgbAiReportConfigService aiReportConfigService;

    @InjectMocks
    private YgbAiReportServiceImpl service;

    @Test
    void selectDashboardRecalculatesRankingAndConclusionFromCurrentDataset()
    {
        YgbAiReport topReport = buildReport(101L, "440000", new BigDecimal("95.00"), "LOW", 8);
        YgbAiReport activeReport = buildReport(102L, "440106", new BigDecimal("81.50"), null, 99);
        YgbAiReport riskReport = buildReport(103L, "440305", new BigDecimal("62.00"), "HIGH", 3);

        when(aiReportMapper.selectAiReportList(any(YgbAiReport.class))).thenReturn(List.of(activeReport, riskReport, topReport));
        when(aiReportMapper.selectAiReportItemList(101L)).thenReturn(List.of(
            buildItem("A", "合同备案合规", "备案率", "95.00", "继续保持高覆盖率。", "LOW", 1),
            buildItem("B", "考勤归集合规", "校验通过率", "92.00", "保持校验质量。", "LOW", 2)));
        when(aiReportMapper.selectAiReportItemList(102L)).thenReturn(List.of(
            buildItem("A", "合同备案合规", "备案率", "84.00", "补齐续签与备案记录。", "MEDIUM", 1),
            buildItem("D", "设备工伤安全", "在线率", "73.00", "提升设备在线率并核查授权。", "HIGH", 2)));
        when(aiReportMapper.selectAiReportItemList(103L)).thenReturn(List.of(
            buildItem("C", "工资发放合规", "发放成功率", "68.00", "优先复核银行回盘失败记录。", "HIGH", 1),
            buildItem("E", "预警闭环治理", "闭环率", "59.00", "压降超时未办结预警。", "HIGH", 2)));

        YgbAiReport query = new YgbAiReport();
        query.setRegionCode("440000");

        YgbAiReportDashboard dashboard = service.selectDashboard(query, 102L);

        assertNotNull(dashboard);
        assertEquals(3L, dashboard.getTotalCount());
        assertEquals(new BigDecimal("79.50"), dashboard.getAverageScore());
        assertEquals(1, dashboard.getHighRiskCount());
        assertEquals(102L, dashboard.getActiveReport().getReportId());
        assertEquals(2, dashboard.getActiveReport().getRankingNo());
        assertTrue(dashboard.getConclusion().getSummary().contains("区域排名第 2"));
        assertTrue(dashboard.getConclusion().getWeakness().contains("设备工伤安全"));
        assertEquals(1, dashboard.getTopRankingList().get(0).getRankingNo());
        assertEquals(3, dashboard.getBottomRankingList().get(0).getRankingNo());
        assertEquals(3, dashboard.getHighRiskList().get(0).getRankingNo());
        assertEquals(2, dashboard.getTrendPoints().get(1).getRankingNo());
        assertEquals(3, dashboard.getYgbExplanation().size());
        assertEquals(4, dashboard.getAzbExplanation().size());
        assertEquals("warning", dashboard.getYgbExplanation().get(1).get("recommendModule"));
        assertEquals("creditScore", dashboard.getAzbExplanation().get(2).get("recommendModule"));
        assertEquals("statReport", dashboard.getAzbExplanation().get(3).get("recommendModule"));
        assertTrue(String.valueOf(dashboard.getYgbExplanation().get(0).get("sourceLabel")).contains("530.1"));
        assertTrue(String.valueOf(dashboard.getAzbExplanation().get(0).get("sourceLabel")).contains("6.1"));
        assertEquals("440106", castMap(dashboard.getAzbExplanation().get(0).get("defaultQuery")).get("regionCode"));
        assertEquals("MONTHLY", castMap(dashboard.getYgbExplanation().get(0).get("defaultQuery")).get("reportType"));
    }

    @Test
    void generateMonthlyReportCalculatesWeightedScoreAndRanking()
    {
        YgbAiReportConfig config = new YgbAiReportConfig();
        config.setVersion("V202606");
        config.setDimensionWeights(JSON.toJSONString(YgbAiReportConfigServiceImpl.defaultDimensionWeights()));
        config.setTargetValues(JSON.toJSONString(YgbAiReportConfigServiceImpl.defaultTargetValues()));
        YgbAiReport persistedReport = buildReport(94099L, "440000", new BigDecimal("91.74"), "LOW", 2);

        when(aiReportConfigService.selectCurrentConfig(eq("440000"), any(Date.class))).thenReturn(config);
        when(aiReportMapper.countContractTotal(any(), any(), any())).thenReturn(10);
        when(aiReportMapper.countContractFiled(any(), any(), any())).thenReturn(9);
        when(aiReportMapper.countAttendanceTotal(any(), any(), any())).thenReturn(20);
        when(aiReportMapper.countAttendancePassed(any(), any(), any())).thenReturn(18);
        when(aiReportMapper.countSalaryTotal(any(), any(), any())).thenReturn(16);
        when(aiReportMapper.countSalarySuccess(any(), any(), any())).thenReturn(15);
        when(aiReportMapper.countDeviceTotal(any())).thenReturn(8);
        when(aiReportMapper.countDeviceOnline(any())).thenReturn(7);
        when(aiReportMapper.countInjuryTotal(any(), any(), any())).thenReturn(0);
        when(aiReportMapper.countInsuredPerson(any())).thenReturn(80);
        when(aiReportMapper.countWarningTotal(any(), any(), any())).thenReturn(12);
        when(aiReportMapper.countWarningClosed(any(), any(), any())).thenReturn(9);
        when(aiReportMapper.countHigherScoreReports(any(), any(), any(), any(), any())).thenReturn(1);
        when(aiReportMapper.selectAiReportByScope(any(), any(), any(), any(), any())).thenReturn(null);
        when(aiReportMapper.selectAiReportList(any(YgbAiReport.class))).thenReturn(List.of(persistedReport));
        doAnswer(invocation -> {
            YgbAiReport report = invocation.getArgument(0);
            report.setReportId(94099L);
            persistedReport.setTotalScore(report.getTotalScore());
            persistedReport.setRiskLevel(report.getRiskLevel());
            persistedReport.setRankingNo(report.getRankingNo());
            persistedReport.setReportSummary(report.getReportSummary());
            persistedReport.setGeneratedTime(report.getGeneratedTime());
            return 1;
        }).when(aiReportMapper).insertAiReport(any(YgbAiReport.class));

        YgbAiReportGenerateRequest request = new YgbAiReportGenerateRequest();
        request.setReportType("monthly");
        request.setRegionCode("440000");
        request.setStartDate("2026-06-01");
        request.setEndDate("2026-06-30");

        Long reportId = service.generateReport(request, "tester");

        assertEquals(94099L, reportId);
        ArgumentCaptor<YgbAiReport> reportCaptor = ArgumentCaptor.forClass(YgbAiReport.class);
        verify(aiReportMapper).insertAiReport(reportCaptor.capture());
        assertEquals("MONTHLY", reportCaptor.getValue().getReportType());
        assertEquals("LOW", reportCaptor.getValue().getRiskLevel());
        assertEquals("V202606", reportCaptor.getValue().getConfigVersion());
        assertEquals(2, reportCaptor.getValue().getRankingNo());
        assertTrue(reportCaptor.getValue().getReportSummary().contains("区域排名第 2"));
        verify(aiReportMapper, times(5)).insertAiReportItem(any(YgbAiReportItem.class));
        verify(aiReportMapper).updateAiReportRankingSummary(eq(94099L), eq(1), any(String.class));
    }

    private YgbAiReport buildReport(Long reportId, String regionCode, BigDecimal score, String riskLevel, Integer rankingNo)
    {
        YgbAiReport report = new YgbAiReport();
        report.setReportId(reportId);
        report.setReportType("MONTHLY");
        report.setRegionCode(regionCode);
        report.setPeriodStart(java.sql.Date.valueOf("2026-06-01"));
        report.setPeriodEnd(java.sql.Date.valueOf("2026-06-30"));
        report.setEnterpriseType("ALL");
        report.setTotalScore(score);
        report.setRiskLevel(riskLevel);
        report.setRankingNo(rankingNo);
        report.setConfigVersion("V202606");
        report.setGeneratedTime(new Date(reportId));
        report.setReportSummary("旧摘要");
        return report;
    }

    private YgbAiReportItem buildItem(String dimensionCode, String dimensionName, String metricLabel, String score,
        String suggestion, String riskLevel, Integer sortNo)
    {
        YgbAiReportItem item = new YgbAiReportItem();
        item.setDimensionCode(dimensionCode);
        item.setDimensionName(dimensionName);
        item.setMetricLabel(metricLabel);
        item.setDimensionScore(new BigDecimal(score));
        item.setSuggestionText(suggestion);
        item.setRiskLevel(riskLevel);
        item.setSortNo(sortNo);
        return item;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> castMap(Object value)
    {
        return (Map<String, Object>) value;
    }
}
