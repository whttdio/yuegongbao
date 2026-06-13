package com.yuegongbao.ygb.compliance.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.compliance.domain.YgbAttendanceRaw;

/**
 * 考勤上报Mapper接口
 *
 * @author yuegongbao
 */
public interface YgbAttendanceRawMapper
{
    public List<YgbAttendanceRaw> selectAttendanceRawList(YgbAttendanceRaw attendanceRaw);

    public YgbAttendanceRaw selectAttendanceRawById(Long attendanceId);

    public YgbAttendanceRaw selectAttendanceRawByPersonAndDate(@Param("personId") Long personId,
        @Param("attendanceDate") java.util.Date attendanceDate);

    public List<YgbAttendanceRaw> selectAttendanceRawByPersonAndMonth(@Param("personId") Long personId,
        @Param("month") String month);

    public YgbAttendanceRaw checkAttendanceNoUnique(String attendanceNo);

    public int insertAttendanceRaw(YgbAttendanceRaw attendanceRaw);

    public int updateAttendanceRaw(YgbAttendanceRaw attendanceRaw);

    public int deleteAttendanceRawByIds(@Param("attendanceIds") Long[] attendanceIds, @Param("updateBy") String updateBy);

    public int updateCollectStatusByMonth(@Param("statMonth") String statMonth,
        @Param("dispatchEnterpriseId") Long dispatchEnterpriseId, @Param("updateBy") String updateBy);
}
