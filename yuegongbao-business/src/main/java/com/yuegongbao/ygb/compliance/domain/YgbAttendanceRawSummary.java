package com.yuegongbao.ygb.compliance.domain;

import java.util.List;
import java.util.Map;

public class YgbAttendanceRawSummary
{
    private int totalCount;

    private int uncollectedCount;

    private int collectedCount;

    private int abnormalCount;

    private int manualFillCount;

    private int checkFailedCount;

    private List<Map<String, Object>> ygbExplanation;

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

    public List<Map<String, Object>> getYgbExplanation()
    {
        return ygbExplanation;
    }

    public void setYgbExplanation(List<Map<String, Object>> ygbExplanation)
    {
        this.ygbExplanation = ygbExplanation;
    }
}
