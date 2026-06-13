package com.yuegongbao.ygb.aireport.service;

import java.util.List;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportDashboard;
import com.yuegongbao.ygb.aireport.domain.YgbAiReport;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportGenerateRequest;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportItem;

public interface IYgbAiReportService
{
    List<YgbAiReport> selectAiReportList(YgbAiReport report);

    YgbAiReport selectAiReportById(Long reportId);

    List<YgbAiReportItem> selectAiReportItems(Long reportId);

    YgbAiReportDashboard selectDashboard(YgbAiReport report, Long activeReportId);

    Long generateReport(YgbAiReportGenerateRequest request, String operator);
}
