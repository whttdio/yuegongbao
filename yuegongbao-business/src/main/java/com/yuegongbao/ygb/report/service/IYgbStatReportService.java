package com.yuegongbao.ygb.report.service;

import java.util.List;
import com.yuegongbao.ygb.report.domain.YgbStatReport;
import com.yuegongbao.ygb.report.domain.YgbStatReportGenerateRequest;
import com.yuegongbao.ygb.report.domain.YgbStatReportItem;
import com.yuegongbao.ygb.report.domain.YgbStatReportSummary;

public interface IYgbStatReportService
{
    List<YgbStatReport> selectStatReportList(YgbStatReport report);

    YgbStatReportSummary selectStatReportSummary(YgbStatReport report);

    YgbStatReport selectStatReportById(Long reportId);

    List<YgbStatReportItem> selectStatReportItems(Long reportId);

    Long generateReport(YgbStatReportGenerateRequest request, String operator);
}
