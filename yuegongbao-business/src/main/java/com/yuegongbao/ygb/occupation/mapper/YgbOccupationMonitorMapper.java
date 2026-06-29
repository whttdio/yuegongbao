package com.yuegongbao.ygb.occupation.mapper;

import java.util.List;
import com.yuegongbao.ygb.occupation.domain.YgbOccupationMonitor;

public interface YgbOccupationMonitorMapper
{
    List<YgbOccupationMonitor> selectOccupationMonitorList(YgbOccupationMonitor query);

    int insertOccupationMonitor(YgbOccupationMonitor monitor);

    int deleteByScope(YgbOccupationMonitor query);
}
