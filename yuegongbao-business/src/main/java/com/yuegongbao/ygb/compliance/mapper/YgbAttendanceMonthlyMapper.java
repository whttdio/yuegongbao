package com.yuegongbao.ygb.compliance.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.compliance.domain.YgbAttendanceMonthly;

/**
 * 考勤归集Mapper接口
 *
 * @author yuegongbao
 */
public interface YgbAttendanceMonthlyMapper
{
    public List<YgbAttendanceMonthly> selectAttendanceMonthlyList(YgbAttendanceMonthly attendanceMonthly);

    public List<YgbAttendanceMonthly> selectMonthlyAggregateRows(@Param("statMonth") String statMonth,
        @Param("dispatchEnterpriseId") Long dispatchEnterpriseId);

    public int updateSummaryStatusByScope(@Param("statMonth") String statMonth,
        @Param("dispatchEnterpriseId") Long dispatchEnterpriseId, @Param("summaryStatus") String summaryStatus,
        @Param("updateBy") String updateBy);

    public int insertAttendanceMonthly(YgbAttendanceMonthly attendanceMonthly);

    public int deleteMonthlyByScope(@Param("statMonth") String statMonth,
        @Param("dispatchEnterpriseId") Long dispatchEnterpriseId, @Param("updateBy") String updateBy);
}
