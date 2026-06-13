package com.yuegongbao.ygb.compliance.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YgbAttendanceMonthlySummary
{
    private int totalCount;

    private int pendingAggregateCount;

    private int aggregatedCount;

    private int verifiedCount;

    private int paidCount;

    private int checkFailedCount;

    private int workHourAbnormalCount;

    private List<YgbAttendanceDimensionCount> regionStats = new ArrayList<>();

    private List<YgbAttendanceDimensionCount> summaryStatusStats = new ArrayList<>();

    private List<Map<String, Object>> ygbExplanation = new ArrayList<>();

    public int getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }

    public int getPendingAggregateCount()
    {
        return pendingAggregateCount;
    }

    public void setPendingAggregateCount(int pendingAggregateCount)
    {
        this.pendingAggregateCount = pendingAggregateCount;
    }

    public int getAggregatedCount()
    {
        return aggregatedCount;
    }

    public void setAggregatedCount(int aggregatedCount)
    {
        this.aggregatedCount = aggregatedCount;
    }

    public int getVerifiedCount()
    {
        return verifiedCount;
    }

    public void setVerifiedCount(int verifiedCount)
    {
        this.verifiedCount = verifiedCount;
    }

    public int getPaidCount()
    {
        return paidCount;
    }

    public void setPaidCount(int paidCount)
    {
        this.paidCount = paidCount;
    }

    public int getCheckFailedCount()
    {
        return checkFailedCount;
    }

    public void setCheckFailedCount(int checkFailedCount)
    {
        this.checkFailedCount = checkFailedCount;
    }

    public int getWorkHourAbnormalCount()
    {
        return workHourAbnormalCount;
    }

    public void setWorkHourAbnormalCount(int workHourAbnormalCount)
    {
        this.workHourAbnormalCount = workHourAbnormalCount;
    }

    public List<YgbAttendanceDimensionCount> getRegionStats()
    {
        return regionStats;
    }

    public void setRegionStats(List<YgbAttendanceDimensionCount> regionStats)
    {
        this.regionStats = regionStats;
    }

    public List<YgbAttendanceDimensionCount> getSummaryStatusStats()
    {
        return summaryStatusStats;
    }

    public void setSummaryStatusStats(List<YgbAttendanceDimensionCount> summaryStatusStats)
    {
        this.summaryStatusStats = summaryStatusStats;
    }

    public List<Map<String, Object>> getYgbExplanation()
    {
        return ygbExplanation;
    }

    public void setYgbExplanation(List<Map<String, Object>> ygbExplanation)
    {
        this.ygbExplanation = ygbExplanation;
    }
}
