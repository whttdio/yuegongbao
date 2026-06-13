package com.yuegongbao.ygb.techdefense.service;

import java.util.List;
import java.util.Map;
import com.yuegongbao.ygb.techdefense.domain.YgbHeightWorkReport;
import com.yuegongbao.ygb.techdefense.domain.YgbHeightWorkReportSummary;
import com.yuegongbao.ygb.techdefense.domain.vo.YgbHeightWorkFinishRequest;

public interface IYgbHeightWorkReportService
{
    List<YgbHeightWorkReport> selectHeightWorkReportList(YgbHeightWorkReport query);

    YgbHeightWorkReportSummary selectHeightWorkReportSummary(YgbHeightWorkReport query);

    YgbHeightWorkReport selectHeightWorkReportById(Long reportId);

    int insertHeightWorkReport(YgbHeightWorkReport report, String operator);

    int updateHeightWorkReport(YgbHeightWorkReport report, String operator);

    int finishHeightWorkReport(Long reportId, YgbHeightWorkFinishRequest request, String operator);

    int importHeightWorkReports(List<YgbHeightWorkReport> reports, String operator);

    Map<String, Object> buildVoucher(Long reportId);
}
