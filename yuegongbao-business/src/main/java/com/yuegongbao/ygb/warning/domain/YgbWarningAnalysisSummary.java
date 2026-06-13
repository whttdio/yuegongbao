package com.yuegongbao.ygb.warning.domain;

import java.util.ArrayList;
import java.util.List;

public class YgbWarningAnalysisSummary
{
    private Integer overduePendingCount;

    private Integer closedWithin72hCount;

    private List<YgbWarningDimensionCount> levelStats = new ArrayList<>();

    private List<YgbWarningDimensionCount> sourceStats = new ArrayList<>();

    private List<YgbWarningDimensionCount> regionStats = new ArrayList<>();

    private List<YgbWarningDimensionCount> statusStats = new ArrayList<>();

    public Integer getOverduePendingCount()
    {
        return overduePendingCount;
    }

    public void setOverduePendingCount(Integer overduePendingCount)
    {
        this.overduePendingCount = overduePendingCount;
    }

    public Integer getClosedWithin72hCount()
    {
        return closedWithin72hCount;
    }

    public void setClosedWithin72hCount(Integer closedWithin72hCount)
    {
        this.closedWithin72hCount = closedWithin72hCount;
    }

    public List<YgbWarningDimensionCount> getLevelStats()
    {
        return levelStats;
    }

    public void setLevelStats(List<YgbWarningDimensionCount> levelStats)
    {
        this.levelStats = levelStats;
    }

    public List<YgbWarningDimensionCount> getSourceStats()
    {
        return sourceStats;
    }

    public void setSourceStats(List<YgbWarningDimensionCount> sourceStats)
    {
        this.sourceStats = sourceStats;
    }

    public List<YgbWarningDimensionCount> getRegionStats()
    {
        return regionStats;
    }

    public void setRegionStats(List<YgbWarningDimensionCount> regionStats)
    {
        this.regionStats = regionStats;
    }

    public List<YgbWarningDimensionCount> getStatusStats()
    {
        return statusStats;
    }

    public void setStatusStats(List<YgbWarningDimensionCount> statusStats)
    {
        this.statusStats = statusStats;
    }
}
