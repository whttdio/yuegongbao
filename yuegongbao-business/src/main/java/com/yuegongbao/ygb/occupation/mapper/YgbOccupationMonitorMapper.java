package com.yuegongbao.ygb.occupation.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.occupation.domain.YgbOccupationMonitor;

public interface YgbOccupationMonitorMapper
{
    List<YgbOccupationMonitor> selectOccupationMonitorList(YgbOccupationMonitor query);

    int insertOccupationMonitor(YgbOccupationMonitor monitor);

    int deleteByScope(@Param("statMonth") String statMonth, @Param("regionCode") String regionCode);
}
