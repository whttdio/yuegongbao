package com.yuegongbao.ygb.cockpit.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;

public class YgbCockpitConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "Config ID")
    private Long configId;

    @Excel(name = "Config Code")
    private String configCode;

    @Excel(name = "Config Name")
    private String configName;

    @Excel(name = "Status")
    private String status;

    @Excel(name = "Region Code")
    private String regionCode;

    private String regionName;

    @Excel(name = "Default Region")
    private String defaultRegionCode;

    private String defaultRegionName;

    @Excel(name = "Summary Cards", width = 50)
    private String summaryCardConfig;

    @Excel(name = "Focus Queue", width = 50)
    private String focusQueueConfig;

    @Excel(name = "Rotate Seconds")
    private Integer rotateSeconds;

    @Excel(name = "Refresh Seconds")
    private Integer refreshSeconds;

    @Excel(name = "Map Center Lng")
    private BigDecimal mapCenterLng;

    @Excel(name = "Map Center Lat")
    private BigDecimal mapCenterLat;

    @Excel(name = "Map Zoom")
    private Integer mapZoom;

    @Excel(name = "Default Focus")
    private String defaultFocusKey;

    @Excel(name = "Source Mode")
    private String sourceMode;

    public Long getConfigId()
    {
        return configId;
    }

    public void setConfigId(Long configId)
    {
        this.configId = configId;
    }

    public String getConfigCode()
    {
        return configCode;
    }

    public void setConfigCode(String configCode)
    {
        this.configCode = configCode;
    }

    public String getConfigName()
    {
        return configName;
    }

    public void setConfigName(String configName)
    {
        this.configName = configName;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getRegionCode()
    {
        return regionCode;
    }

    public void setRegionCode(String regionCode)
    {
        this.regionCode = regionCode;
    }

    public String getRegionName()
    {
        return regionName;
    }

    public void setRegionName(String regionName)
    {
        this.regionName = regionName;
    }

    public String getDefaultRegionCode()
    {
        return defaultRegionCode;
    }

    public void setDefaultRegionCode(String defaultRegionCode)
    {
        this.defaultRegionCode = defaultRegionCode;
    }

    public String getDefaultRegionName()
    {
        return defaultRegionName;
    }

    public void setDefaultRegionName(String defaultRegionName)
    {
        this.defaultRegionName = defaultRegionName;
    }

    public String getSummaryCardConfig()
    {
        return summaryCardConfig;
    }

    public void setSummaryCardConfig(String summaryCardConfig)
    {
        this.summaryCardConfig = summaryCardConfig;
    }

    public String getFocusQueueConfig()
    {
        return focusQueueConfig;
    }

    public void setFocusQueueConfig(String focusQueueConfig)
    {
        this.focusQueueConfig = focusQueueConfig;
    }

    public Integer getRotateSeconds()
    {
        return rotateSeconds;
    }

    public void setRotateSeconds(Integer rotateSeconds)
    {
        this.rotateSeconds = rotateSeconds;
    }

    public Integer getRefreshSeconds()
    {
        return refreshSeconds;
    }

    public void setRefreshSeconds(Integer refreshSeconds)
    {
        this.refreshSeconds = refreshSeconds;
    }

    public BigDecimal getMapCenterLng()
    {
        return mapCenterLng;
    }

    public void setMapCenterLng(BigDecimal mapCenterLng)
    {
        this.mapCenterLng = mapCenterLng;
    }

    public BigDecimal getMapCenterLat()
    {
        return mapCenterLat;
    }

    public void setMapCenterLat(BigDecimal mapCenterLat)
    {
        this.mapCenterLat = mapCenterLat;
    }

    public Integer getMapZoom()
    {
        return mapZoom;
    }

    public void setMapZoom(Integer mapZoom)
    {
        this.mapZoom = mapZoom;
    }

    public String getDefaultFocusKey()
    {
        return defaultFocusKey;
    }

    public void setDefaultFocusKey(String defaultFocusKey)
    {
        this.defaultFocusKey = defaultFocusKey;
    }

    public String getSourceMode()
    {
        return sourceMode;
    }

    public void setSourceMode(String sourceMode)
    {
        this.sourceMode = sourceMode;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("configId", getConfigId())
            .append("configCode", getConfigCode())
            .append("configName", getConfigName())
            .append("status", getStatus())
            .append("regionCode", getRegionCode())
            .append("defaultRegionCode", getDefaultRegionCode())
            .append("summaryCardConfig", getSummaryCardConfig())
            .append("focusQueueConfig", getFocusQueueConfig())
            .append("rotateSeconds", getRotateSeconds())
            .append("refreshSeconds", getRefreshSeconds())
            .append("mapCenterLng", getMapCenterLng())
            .append("mapCenterLat", getMapCenterLat())
            .append("mapZoom", getMapZoom())
            .append("defaultFocusKey", getDefaultFocusKey())
            .append("sourceMode", getSourceMode())
            .append("remark", getRemark())
            .toString();
    }
}
