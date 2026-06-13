package com.yuegongbao.ygb.cockpit.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.cockpit.domain.YgbCockpitConfig;

public interface YgbCockpitConfigMapper
{
    List<YgbCockpitConfig> selectCockpitConfigList(YgbCockpitConfig config);

    YgbCockpitConfig selectCockpitConfigById(Long configId);

    YgbCockpitConfig selectCurrentConfig(@Param("regionCode") String regionCode);

    YgbCockpitConfig checkConfigCodeUnique(@Param("configCode") String configCode);

    int insertCockpitConfig(YgbCockpitConfig config);

    int updateCockpitConfig(YgbCockpitConfig config);

    int deleteCockpitConfigByIds(@Param("configIds") Long[] configIds, @Param("updateBy") String updateBy);
}
