package com.yuegongbao.ygb.occupation.service;

import java.util.List;
import com.yuegongbao.ygb.occupation.domain.YgbOccupationMonitor;
import com.yuegongbao.ygb.occupation.domain.YgbOccupationMonitorSummary;

public interface IYgbOccupationMonitorService
{
    List<YgbOccupationMonitor> selectOccupationMonitorList(YgbOccupationMonitor query);

    YgbOccupationMonitorSummary selectOccupationMonitorSummary(YgbOccupationMonitor query);

    int syncOccupationMonitor(String statMonth, String operator);
}
