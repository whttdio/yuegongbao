package com.yuegongbao.ygb.cockpit.service;

import java.util.List;
import com.yuegongbao.ygb.cockpit.domain.YgbAzbCockpitDashboard;
import com.yuegongbao.ygb.cockpit.domain.YgbWorkbenchDashboard;
import com.yuegongbao.ygb.cockpit.domain.YgbCockpitDistribution;
import com.yuegongbao.ygb.cockpit.domain.YgbCockpitIndicator;
import com.yuegongbao.ygb.cockpit.domain.YgbCockpitSnapshot;
import com.yuegongbao.ygb.cockpit.domain.YgbGeoJsonFeatureCollection;

public interface IYgbCockpitService
{
    YgbCockpitIndicator getIndicators(String regionCode, String statDate);

    List<YgbCockpitSnapshot> listTrend(String regionCode, String statDate, Integer days);

    List<YgbCockpitDistribution> listWarningDistribution(String regionCode, String statMonth);

    YgbGeoJsonFeatureCollection getMapFeatures(String regionCode, String statDate);

    YgbWorkbenchDashboard getYgbHomeAggregate(String regionCode, String statDate, String statMonth, Integer days);

    YgbAzbCockpitDashboard getAzbHomeAggregate(String regionCode, String statDate, String statMonth, Integer days);

    YgbWorkbenchDashboard getYgbDashboard(String regionCode, String statDate, String statMonth, Integer days);

    YgbAzbCockpitDashboard getAzbDashboard(String regionCode, String statDate, String statMonth, Integer days);
}
