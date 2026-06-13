package com.yuegongbao.ygb.cockpit.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSON;
import com.yuegongbao.common.constant.UserConstants;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.cockpit.domain.YgbCockpitConfig;
import com.yuegongbao.ygb.cockpit.domain.YgbCockpitConfigSummary;
import com.yuegongbao.ygb.cockpit.mapper.YgbCockpitConfigMapper;
import com.yuegongbao.ygb.cockpit.service.IYgbCockpitConfigService;
import com.yuegongbao.ygb.util.YgbRegionHelper;
import com.yuegongbao.ygb.util.YgbRegionScopeHelper;

@Service
public class YgbCockpitConfigServiceImpl implements IYgbCockpitConfigService
{
    @Autowired
    private YgbCockpitConfigMapper cockpitConfigMapper;

    @Autowired
    private YgbRegionScopeHelper regionScopeHelper;

    @Override
    public List<YgbCockpitConfig> selectCockpitConfigList(YgbCockpitConfig config)
    {
        List<YgbCockpitConfig> list = cockpitConfigMapper.selectCockpitConfigList(config);
        list.forEach(this::hydrateRegionNames);
        return list;
    }

    @Override
    public YgbCockpitConfigSummary selectCockpitConfigSummary(YgbCockpitConfig config)
    {
        List<YgbCockpitConfig> list = selectCockpitConfigList(config);
        YgbCockpitConfigSummary summary = new YgbCockpitConfigSummary();
        summary.setTotalCount(list.size());

        int activeCount = 0;
        int customCenterCount = 0;
        int customCardCount = 0;
        int autoRefreshCount = 0;
        for (YgbCockpitConfig item : list)
        {
            if ("1".equals(item.getStatus()))
            {
                activeCount++;
            }
            if (item.getMapCenterLng() != null && item.getMapCenterLat() != null)
            {
                customCenterCount++;
            }
            if (StringUtils.isNotEmpty(item.getSummaryCardConfig()) || StringUtils.isNotEmpty(item.getFocusQueueConfig()))
            {
                customCardCount++;
            }
            if (defaultInt(item.getRefreshSeconds()) > 0 || defaultInt(item.getRotateSeconds()) > 0)
            {
                autoRefreshCount++;
            }
        }
        summary.setActiveCount(activeCount);
        summary.setCustomCenterCount(customCenterCount);
        summary.setCustomCardCount(customCardCount);
        summary.setAutoRefreshCount(autoRefreshCount);
        return summary;
    }

    @Override
    public YgbCockpitConfig selectCockpitConfigById(Long configId)
    {
        YgbCockpitConfig config = cockpitConfigMapper.selectCockpitConfigById(configId);
        if (config != null)
        {
            hydrateRegionNames(config);
        }
        return config;
    }

    @Override
    public YgbCockpitConfig selectCurrentConfig(String regionCode)
    {
        String authorizedRegionCode = regionScopeHelper.resolveAuthorizedRegionCode(regionCode);
        YgbCockpitConfig config = cockpitConfigMapper.selectCurrentConfig(authorizedRegionCode);
        if (config == null && !"440000".equals(authorizedRegionCode))
        {
            config = cockpitConfigMapper.selectCurrentConfig("440000");
        }
        if (config == null)
        {
            config = buildDefaultConfig(authorizedRegionCode);
        }
        hydrateRegionNames(config);
        return config;
    }

    @Override
    public boolean checkConfigCodeUnique(YgbCockpitConfig config)
    {
        Long configId = config.getConfigId() == null ? -1L : config.getConfigId();
        YgbCockpitConfig info = cockpitConfigMapper.checkConfigCodeUnique(config.getConfigCode());
        if (info != null && info.getConfigId().longValue() != configId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public int insertCockpitConfig(YgbCockpitConfig config)
    {
        fillDefaultFields(config);
        config.setCreateTime(new Date());
        config.setUpdateTime(new Date());
        return cockpitConfigMapper.insertCockpitConfig(config);
    }

    @Override
    public int updateCockpitConfig(YgbCockpitConfig config)
    {
        fillDefaultFields(config);
        config.setUpdateTime(new Date());
        return cockpitConfigMapper.updateCockpitConfig(config);
    }

    @Override
    public int deleteCockpitConfigByIds(Long[] configIds, String updateBy)
    {
        return cockpitConfigMapper.deleteCockpitConfigByIds(configIds, updateBy);
    }

    private void fillDefaultFields(YgbCockpitConfig config)
    {
        if (config == null)
        {
            throw new ServiceException("Cockpit config cannot be null");
        }
        if (StringUtils.isEmpty(config.getConfigCode()))
        {
            throw new ServiceException("Config code cannot be empty");
        }
        if (StringUtils.isEmpty(config.getConfigName()))
        {
            throw new ServiceException("Config name cannot be empty");
        }
        if (StringUtils.isEmpty(config.getRegionCode()))
        {
            config.setRegionCode(YgbRegionHelper.defaultDashboardRegion(null));
        }
        if (StringUtils.isEmpty(config.getDefaultRegionCode()))
        {
            config.setDefaultRegionCode(config.getRegionCode());
        }
        if (StringUtils.isEmpty(config.getStatus()))
        {
            config.setStatus("1");
        }
        if (config.getRotateSeconds() == null)
        {
            config.setRotateSeconds(0);
        }
        if (config.getRefreshSeconds() == null)
        {
            config.setRefreshSeconds(30);
        }
        if (config.getMapZoom() == null)
        {
            config.setMapZoom(11);
        }
        if (StringUtils.isEmpty(config.getSourceMode()))
        {
            config.setSourceMode("manual");
        }
        validateJson(config.getSummaryCardConfig(), "summaryCardConfig");
        validateJson(config.getFocusQueueConfig(), "focusQueueConfig");
    }

    private void validateJson(String jsonText, String fieldLabel)
    {
        if (StringUtils.isEmpty(jsonText))
        {
            return;
        }
        try
        {
            JSON.parse(jsonText);
        }
        catch (Exception ex)
        {
            throw new ServiceException(fieldLabel + " is not valid JSON");
        }
    }

    private void hydrateRegionNames(YgbCockpitConfig config)
    {
        config.setRegionName(YgbRegionHelper.resolveRegionName(config.getRegionCode()));
        config.setDefaultRegionName(YgbRegionHelper.resolveRegionName(config.getDefaultRegionCode()));
    }

    private YgbCockpitConfig buildDefaultConfig(String regionCode)
    {
        YgbCockpitConfig config = new YgbCockpitConfig();
        config.setConfigCode("DEFAULT-COCKPIT");
        config.setConfigName("Default Cockpit Config");
        config.setStatus("1");
        config.setRegionCode(regionCode);
        config.setDefaultRegionCode(regionCode);
        config.setRotateSeconds(0);
        config.setRefreshSeconds(30);
        config.setMapZoom(11);
        config.setSourceMode("stub");
        config.setSummaryCardConfig("[]");
        config.setFocusQueueConfig("[]");
        config.setRemark("Built-in fallback cockpit config");
        return config;
    }

    private int defaultInt(Integer value)
    {
        return value == null ? 0 : value.intValue();
    }
}
