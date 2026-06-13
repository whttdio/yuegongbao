package com.yuegongbao.ygb.compliance.service;

import java.util.List;
import com.yuegongbao.ygb.compliance.domain.YgbAttendanceRaw;
import com.yuegongbao.ygb.compliance.domain.YgbAttendanceRawOverview;
import com.yuegongbao.ygb.compliance.domain.YgbAttendanceRawSummary;

/**
 * 考勤上报服务接口。
 *
 * @author yuegongbao
 */
public interface IYgbAttendanceRawService
{
    public List<YgbAttendanceRaw> selectAttendanceRawList(YgbAttendanceRaw attendanceRaw);

    public YgbAttendanceRawSummary selectAttendanceRawSummary(YgbAttendanceRaw attendanceRaw);

    public YgbAttendanceRawOverview selectAttendanceRawOverview(YgbAttendanceRaw attendanceRaw);

    public YgbAttendanceRaw selectAttendanceRawById(Long attendanceId);

    public boolean checkAttendanceNoUnique(YgbAttendanceRaw attendanceRaw);

    public int insertAttendanceRaw(YgbAttendanceRaw attendanceRaw);

    public int updateAttendanceRaw(YgbAttendanceRaw attendanceRaw);

    public int deleteAttendanceRawByIds(Long[] attendanceIds, String updateBy);
}

