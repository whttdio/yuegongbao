package com.yuegongbao.ygb.aireport.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YgbAiReportConfigSummary
{
    private Integer totalCount;
    private Integer activeCount;
    private Integer upcomingCount;
    private Integer coveredRegionCount;
    private String activeVersion;
    private BigDecimal activeWarningCloseRate;
    private Integer averageWeightSum;
    private List<Map<String, Object>> ygbExplanation = new ArrayList<>();
    private List<Map<String, Object>> azbExplanation = new ArrayList<>();

    public Integer getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount)
    {
        this.totalCount = totalCount;
    }

    public Integer getActiveCount()
    {
        return activeCount;
    }

    public void setActiveCount(Integer activeCount)
    {
        this.activeCount = activeCount;
    }

    public Integer getUpcomingCount()
    {
        return upcomingCount;
    }

    public void setUpcomingCount(Integer upcomingCount)
    {
        this.upcomingCount = upcomingCount;
    }

    public Integer getCoveredRegionCount()
    {
        return coveredRegionCount;
    }

    public void setCoveredRegionCount(Integer coveredRegionCount)
    {
        this.coveredRegionCount = coveredRegionCount;
    }

    public String getActiveVersion()
    {
        return activeVersion;
    }

    public void setActiveVersion(String activeVersion)
    {
        this.activeVersion = activeVersion;
    }

    public BigDecimal getActiveWarningCloseRate()
    {
        return activeWarningCloseRate;
    }

    public void setActiveWarningCloseRate(BigDecimal activeWarningCloseRate)
    {
        this.activeWarningCloseRate = activeWarningCloseRate;
    }

    public Integer getAverageWeightSum()
    {
        return averageWeightSum;
    }

    public void setAverageWeightSum(Integer averageWeightSum)
    {
        this.averageWeightSum = averageWeightSum;
    }

    public List<Map<String, Object>> getYgbExplanation()
    {
        return ygbExplanation;
    }

    public void setYgbExplanation(List<Map<String, Object>> ygbExplanation)
    {
        this.ygbExplanation = ygbExplanation;
    }

    public List<Map<String, Object>> getAzbExplanation()
    {
        return azbExplanation;
    }

    public void setAzbExplanation(List<Map<String, Object>> azbExplanation)
    {
        this.azbExplanation = azbExplanation;
    }
}
