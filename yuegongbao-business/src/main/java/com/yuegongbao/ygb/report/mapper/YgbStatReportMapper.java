package com.yuegongbao.ygb.report.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.report.domain.YgbStatReport;
import com.yuegongbao.ygb.report.domain.YgbStatReportItem;

public interface YgbStatReportMapper
{
    List<YgbStatReport> selectStatReportList(YgbStatReport report);

    YgbStatReport selectStatReportById(Long reportId);

    YgbStatReport selectStatReportByScope(@Param("reportCode") String reportCode, @Param("statMonth") String statMonth,
        @Param("regionCode") String regionCode);

    List<YgbStatReportItem> selectStatReportItemList(Long reportId);

    int insertStatReport(YgbStatReport report);

    int updateStatReport(YgbStatReport report);

    int deleteStatReportItemsByReportId(Long reportId);

    int insertStatReportItem(YgbStatReportItem item);

    List<YgbStatReportItem> selectInjuryRateItems(@Param("statMonth") String statMonth,
        @Param("regionPrefix") String regionPrefix);

    List<YgbStatReportItem> selectWarningOverviewItems(@Param("statMonth") String statMonth,
        @Param("regionPrefix") String regionPrefix);

    List<YgbStatReportItem> selectSalaryPaymentItems(@Param("statMonth") String statMonth,
        @Param("regionPrefix") String regionPrefix);

    List<YgbStatReportItem> selectEmploymentItems(@Param("statMonth") String statMonth,
        @Param("regionPrefix") String regionPrefix);

    List<YgbStatReportItem> selectAttendanceItems(@Param("statMonth") String statMonth,
        @Param("regionPrefix") String regionPrefix);

    List<YgbStatReportItem> selectSocialItems(@Param("statMonth") String statMonth,
        @Param("regionPrefix") String regionPrefix);

    List<YgbStatReportItem> selectTaxItems(@Param("statMonth") String statMonth,
        @Param("regionPrefix") String regionPrefix);

    List<YgbStatReportItem> selectAqInsuranceItems(@Param("statMonth") String statMonth,
        @Param("regionPrefix") String regionPrefix);

    List<YgbStatReportItem> selectNewformItems(@Param("statMonth") String statMonth,
        @Param("regionPrefix") String regionPrefix);

    List<YgbStatReportItem> selectOccupationItems(@Param("statMonth") String statMonth,
        @Param("regionPrefix") String regionPrefix);

    List<YgbStatReportItem> selectUnionSupervisionItems(@Param("statMonth") String statMonth,
        @Param("regionPrefix") String regionPrefix);

    List<YgbStatReportItem> selectCustomItems(@Param("statMonth") String statMonth,
        @Param("regionPrefix") String regionPrefix);

    List<YgbStatReportItem> selectDeviceStatsItems(@Param("statMonth") String statMonth,
        @Param("regionPrefix") String regionPrefix);

    List<YgbStatReportItem> selectExpansionReductionItems(@Param("statMonth") String statMonth,
        @Param("regionPrefix") String regionPrefix);

    List<YgbStatReportItem> selectSpecialRectificationItems(@Param("statMonth") String statMonth,
        @Param("regionPrefix") String regionPrefix);

    Integer countSocialCompareTotal(@Param("statMonth") String statMonth, @Param("regionPrefix") String regionPrefix);

    Integer countSocialCompareAbnormal(@Param("statMonth") String statMonth,
        @Param("regionPrefix") String regionPrefix);

    Integer countTaxCompareTotal(@Param("statMonth") String statMonth, @Param("regionPrefix") String regionPrefix);

    Integer countTaxCompareAbnormal(@Param("statMonth") String statMonth, @Param("regionPrefix") String regionPrefix);

    Integer countUninsured(@Param("statMonth") String statMonth, @Param("regionPrefix") String regionPrefix);

    Integer countEmploymentTotal(@Param("statMonth") String statMonth, @Param("regionPrefix") String regionPrefix);

    Integer countEmploymentByLevel(@Param("statMonth") String statMonth, @Param("regionPrefix") String regionPrefix,
        @Param("warningLevel") String warningLevel);

    Integer countFakeOutsourcingTotal(@Param("statMonth") String statMonth, @Param("regionPrefix") String regionPrefix);

    Integer countFakeOutsourcingSuspected(@Param("statMonth") String statMonth,
        @Param("regionPrefix") String regionPrefix);
}
