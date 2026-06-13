package com.yuegongbao.ygb.cockpit.service;

import java.util.List;
import com.yuegongbao.ygb.cockpit.domain.YgbCockpitConfig;
import com.yuegongbao.ygb.cockpit.domain.YgbCockpitConfigSummary;

public interface IYgbCockpitConfigService
{
    List<YgbCockpitConfig> selectCockpitConfigList(YgbCockpitConfig config);

    YgbCockpitConfigSummary selectCockpitConfigSummary(YgbCockpitConfig config);

    YgbCockpitConfig selectCockpitConfigById(Long configId);

    YgbCockpitConfig selectCurrentConfig(String regionCode);

    boolean checkConfigCodeUnique(YgbCockpitConfig config);

    int insertCockpitConfig(YgbCockpitConfig config);

    int updateCockpitConfig(YgbCockpitConfig config);

    int deleteCockpitConfigByIds(Long[] configIds, String updateBy);
}
