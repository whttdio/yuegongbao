package com.yuegongbao.ygb.cockpit.domain;

import java.math.BigDecimal;

public class YgbCockpitDistribution
{
    private String dimensionCode;

    private String dimensionName;

    private Integer metricCount;

    private BigDecimal metricRate;

    public String getDimensionCode()
    {
        return dimensionCode;
    }

    public void setDimensionCode(String dimensionCode)
    {
        this.dimensionCode = dimensionCode;
    }

    public String getDimensionName()
    {
        return dimensionName;
    }

    public void setDimensionName(String dimensionName)
    {
        this.dimensionName = dimensionName;
    }

    public Integer getMetricCount()
    {
        return metricCount;
    }

    public void setMetricCount(Integer metricCount)
    {
        this.metricCount = metricCount;
    }

    public BigDecimal getMetricRate()
    {
        return metricRate;
    }

    public void setMetricRate(BigDecimal metricRate)
    {
        this.metricRate = metricRate;
    }
}
