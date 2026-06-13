package com.yuegongbao.ygb.cockpit.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.cockpit.domain.YgbCockpitDistribution;
import com.yuegongbao.ygb.cockpit.domain.YgbCockpitMapFeature;
import com.yuegongbao.ygb.cockpit.domain.YgbCockpitSnapshot;

public interface YgbCockpitMapper
{
    YgbCockpitSnapshot selectLatestSnapshot(@Param("regionCode") String regionCode, @Param("statDate") String statDate);

    List<YgbCockpitSnapshot> selectSnapshotTrend(@Param("regionCode") String regionCode,
        @Param("startDate") String startDate, @Param("endDate") String endDate);

    List<YgbCockpitDistribution> selectWarningDistribution(@Param("regionPrefix") String regionPrefix,
        @Param("statMonth") String statMonth);

    List<YgbCockpitMapFeature> selectMapFeatureList(@Param("regionPrefix") String regionPrefix,
        @Param("statDate") String statDate);

    Integer countWarningsByDate(@Param("regionPrefix") String regionPrefix, @Param("statDate") String statDate);

    Integer countPendingWarnings(@Param("regionPrefix") String regionPrefix);

    Integer countOnlineDevices(@Param("regionPrefix") String regionPrefix);

    Integer countOverdueInjuries(@Param("regionPrefix") String regionPrefix);
}
