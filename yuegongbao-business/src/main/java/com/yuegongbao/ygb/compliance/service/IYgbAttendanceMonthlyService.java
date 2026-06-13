package com.yuegongbao.ygb.compliance.service;

import java.util.List;
import com.yuegongbao.ygb.compliance.domain.YgbAttendanceMonthly;
import com.yuegongbao.ygb.compliance.domain.YgbAttendanceMonthlySummary;

/**
 * 考勤归集服务接口。
 *
 * @author yuegongbao
 */
public interface IYgbAttendanceMonthlyService
{
    public List<YgbAttendanceMonthly> selectAttendanceMonthlyList(YgbAttendanceMonthly attendanceMonthly);

    public YgbAttendanceMonthlySummary selectAttendanceMonthlySummary(YgbAttendanceMonthly attendanceMonthly);

    public int aggregateMonthly(String statMonth, Long dispatchEnterpriseId, String operator);
}

