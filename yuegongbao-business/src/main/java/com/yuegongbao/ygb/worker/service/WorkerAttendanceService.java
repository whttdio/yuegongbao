package com.yuegongbao.ygb.worker.service;

import java.util.Map;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.worker.domain.vo.WorkerAttendanceCheckRequest;

public interface WorkerAttendanceService
{
    Map<String, Object> checkIn(YgbPerson worker, String username, WorkerAttendanceCheckRequest request);

    Map<String, Object> checkOut(YgbPerson worker, String username, WorkerAttendanceCheckRequest request);

    Map<String, Object> getMonthlyView(YgbPerson worker, String month);

    Map<String, Object> getDayDetail(YgbPerson worker, String date);
}
