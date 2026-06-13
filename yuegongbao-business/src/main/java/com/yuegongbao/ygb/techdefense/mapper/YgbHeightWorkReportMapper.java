package com.yuegongbao.ygb.techdefense.mapper;

import java.util.List;
import com.yuegongbao.ygb.techdefense.domain.YgbHeightWorkReport;

public interface YgbHeightWorkReportMapper
{
    List<YgbHeightWorkReport> selectHeightWorkReportList(YgbHeightWorkReport query);

    YgbHeightWorkReport selectHeightWorkReportById(Long reportId);

    int insertHeightWorkReport(YgbHeightWorkReport report);

    int updateHeightWorkReport(YgbHeightWorkReport report);

    int finishHeightWorkReport(YgbHeightWorkReport report);
}
