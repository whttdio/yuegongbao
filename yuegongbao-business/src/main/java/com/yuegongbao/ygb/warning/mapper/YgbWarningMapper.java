package com.yuegongbao.ygb.warning.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.warning.domain.YgbWarningDimensionCount;
import com.yuegongbao.ygb.warning.domain.YgbWarning;
import com.yuegongbao.ygb.warning.domain.YgbWarningSummary;

/**
 * 预警工单 Mapper。
 *
 * @author yuegongbao
 */
public interface YgbWarningMapper
{
    List<YgbWarning> selectWarningList(YgbWarning warning);

    YgbWarningSummary selectWarningSummary(YgbWarning warning);

    List<YgbWarningDimensionCount> selectWarningLevelStats(YgbWarning warning);

    List<YgbWarningDimensionCount> selectWarningSourceStats(YgbWarning warning);

    List<YgbWarningDimensionCount> selectWarningRegionStats(YgbWarning warning);

    List<YgbWarningDimensionCount> selectWarningStatusStats(YgbWarning warning);

    Integer countOverduePendingWarnings(YgbWarning warning);

    Integer countClosedWithin72hWarnings(YgbWarning warning);

    YgbWarning selectWarningById(Long warnId);

    int insertWarning(YgbWarning warning);

    int updateWarningStatus(@Param("warnId") Long warnId, @Param("warnStatus") String warnStatus,
        @Param("assignName") String assignName, @Param("updateBy") String updateBy,
        @Param("resolveFlag") boolean resolveFlag);

    int countActiveWarning(@Param("sourceModule") String sourceModule, @Param("warnType") String warnType,
        @Param("targetObjectId") Long targetObjectId);
}
