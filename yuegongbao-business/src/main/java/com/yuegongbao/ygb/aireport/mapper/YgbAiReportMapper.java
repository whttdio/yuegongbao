package com.yuegongbao.ygb.aireport.mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.aireport.domain.YgbAiReport;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportConfig;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportItem;

public interface YgbAiReportMapper
{
    List<YgbAiReport> selectAiReportList(YgbAiReport report);

    YgbAiReport selectAiReportById(Long reportId);

    YgbAiReport selectAiReportByScope(@Param("reportType") String reportType, @Param("regionCode") String regionCode,
        @Param("periodStart") Date periodStart, @Param("periodEnd") Date periodEnd,
        @Param("enterpriseType") String enterpriseType);

    List<YgbAiReportItem> selectAiReportItemList(Long reportId);

    int insertAiReport(YgbAiReport report);

    int updateAiReport(YgbAiReport report);

    int updateAiReportRankingSummary(@Param("reportId") Long reportId, @Param("rankingNo") Integer rankingNo,
        @Param("reportSummary") String reportSummary);

    int deleteAiReportItemsByReportId(Long reportId);

    int insertAiReportItem(YgbAiReportItem item);

    List<YgbAiReportConfig> selectAiReportConfigList(YgbAiReportConfig config);

    YgbAiReportConfig selectAiReportConfigById(Long configId);

    YgbAiReportConfig selectActiveConfigByRegion(@Param("regionCode") String regionCode,
        @Param("effectiveDate") Date effectiveDate);

    int insertAiReportConfig(YgbAiReportConfig config);

    int updateAiReportConfig(YgbAiReportConfig config);

    int deactivateConfigsByRegion(@Param("regionCode") String regionCode, @Param("excludeConfigId") Long excludeConfigId,
        @Param("updateBy") String updateBy);

    int countHigherScoreReports(@Param("reportType") String reportType, @Param("periodStart") Date periodStart,
        @Param("periodEnd") Date periodEnd, @Param("totalScore") BigDecimal totalScore,
        @Param("excludeReportId") Long excludeReportId);

    int countContractTotal(@Param("regionPrefix") String regionPrefix, @Param("periodStart") Date periodStart,
        @Param("periodEnd") Date periodEnd);

    int countContractFiled(@Param("regionPrefix") String regionPrefix, @Param("periodStart") Date periodStart,
        @Param("periodEnd") Date periodEnd);

    int countAttendanceTotal(@Param("startMonth") String startMonth, @Param("endMonth") String endMonth,
        @Param("regionPrefix") String regionPrefix);

    int countAttendancePassed(@Param("startMonth") String startMonth, @Param("endMonth") String endMonth,
        @Param("regionPrefix") String regionPrefix);

    int countSalaryTotal(@Param("startMonth") String startMonth, @Param("endMonth") String endMonth,
        @Param("regionPrefix") String regionPrefix);

    int countSalarySuccess(@Param("startMonth") String startMonth, @Param("endMonth") String endMonth,
        @Param("regionPrefix") String regionPrefix);

    int countDeviceTotal(@Param("regionPrefix") String regionPrefix);

    int countDeviceOnline(@Param("regionPrefix") String regionPrefix);

    int countInjuryTotal(@Param("regionPrefix") String regionPrefix, @Param("periodStart") Date periodStart,
        @Param("periodEnd") Date periodEnd);

    int countInsuredPerson(@Param("regionPrefix") String regionPrefix);

    int countWarningTotal(@Param("regionPrefix") String regionPrefix, @Param("periodStart") Date periodStart,
        @Param("periodEnd") Date periodEnd);

    int countWarningClosed(@Param("regionPrefix") String regionPrefix, @Param("periodStart") Date periodStart,
        @Param("periodEnd") Date periodEnd);
}
