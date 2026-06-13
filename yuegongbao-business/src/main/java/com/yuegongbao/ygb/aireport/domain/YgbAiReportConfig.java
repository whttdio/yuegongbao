package com.yuegongbao.ygb.aireport.domain;

import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;

public class YgbAiReportConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "配置ID")
    private Long configId;

    @Excel(name = "区域编码")
    private String regionCode;

    @Excel(name = "区域")
    private String regionName;

    @Excel(name = "版本")
    private String version;

    @Excel(name = "配置状态")
    private String configStatus;

    @Excel(name = "维度权重", width = 40)
    private String dimensionWeights;

    @Excel(name = "目标值", width = 40)
    private String targetValues;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生效日期", width = 16, dateFormat = "yyyy-MM-dd")
    private Date effectiveDate;

    private String sourceMode;

    public Long getConfigId()
    {
        return configId;
    }

    public void setConfigId(Long configId)
    {
        this.configId = configId;
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

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getConfigStatus()
    {
        return configStatus;
    }

    public void setConfigStatus(String configStatus)
    {
        this.configStatus = configStatus;
    }

    public String getDimensionWeights()
    {
        return dimensionWeights;
    }

    public void setDimensionWeights(String dimensionWeights)
    {
        this.dimensionWeights = dimensionWeights;
    }

    public String getTargetValues()
    {
        return targetValues;
    }

    public void setTargetValues(String targetValues)
    {
        this.targetValues = targetValues;
    }

    public Date getEffectiveDate()
    {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate)
    {
        this.effectiveDate = effectiveDate;
    }

    public String getSourceMode()
    {
        return sourceMode;
    }

    public void setSourceMode(String sourceMode)
    {
        this.sourceMode = sourceMode;
    }
}
