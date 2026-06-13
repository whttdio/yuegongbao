package com.yuegongbao.ygb.compliance.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class YgbAttendanceRawOverview
{
    private int totalCount;

    private int uncollectedCount;

    private int collectedCount;

    private int abnormalCount;

    private int manualFillCount;

    private int checkFailedCount;

    private BigDecimal deviceOnlineRate;

    private int onlineDeviceCount;

    private int offlineDeviceCount;

    private List<YgbAttendanceDimensionCount> regionStats = new ArrayList<>();

    private List<YgbAttendanceDimensionCount> sourceStats = new ArrayList<>();

    public int getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }

    public int getUncollectedCount()
    {
        return uncollectedCount;
    }

    public void setUncollectedCount(int uncollectedCount)
    {
        this.uncollectedCount = uncollectedCount;
    }

    public int getCollectedCount()
    {
        return collectedCount;
    }

    public void setCollectedCount(int collectedCount)
    {
        this.collectedCount = collectedCount;
    }

    public int getAbnormalCount()
    {
        return abnormalCount;
    }

    public void setAbnormalCount(int abnormalCount)
    {
        this.abnormalCount = abnormalCount;
    }

    public int getManualFillCount()
    {
        return manualFillCount;
    }

    public void setManualFillCount(int manualFillCount)
    {
        this.manualFillCount = manualFillCount;
    }

    public int getCheckFailedCount()
    {
        return checkFailedCount;
    }

    public void setCheckFailedCount(int checkFailedCount)
    {
        this.checkFailedCount = checkFailedCount;
    }

    public BigDecimal getDeviceOnlineRate()
    {
        return deviceOnlineRate;
    }

    public void setDeviceOnlineRate(BigDecimal deviceOnlineRate)
    {
        this.deviceOnlineRate = deviceOnlineRate;
    }

    public int getOnlineDeviceCount()
    {
        return onlineDeviceCount;
    }

    public void setOnlineDeviceCount(int onlineDeviceCount)
    {
        this.onlineDeviceCount = onlineDeviceCount;
    }

    public int getOfflineDeviceCount()
    {
        return offlineDeviceCount;
    }

    public void setOfflineDeviceCount(int offlineDeviceCount)
    {
        this.offlineDeviceCount = offlineDeviceCount;
    }

    public List<YgbAttendanceDimensionCount> getRegionStats()
    {
        return regionStats;
    }

    public void setRegionStats(List<YgbAttendanceDimensionCount> regionStats)
    {
        this.regionStats = regionStats;
    }

    public List<YgbAttendanceDimensionCount> getSourceStats()
    {
        return sourceStats;
    }

    public void setSourceStats(List<YgbAttendanceDimensionCount> sourceStats)
    {
        this.sourceStats = sourceStats;
    }
}
