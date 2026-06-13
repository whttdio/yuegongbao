package com.yuegongbao.ygb.regulation.domain;

public class YgbSocialBaseCompareSummary
{
    private int totalCount;

    private int normalCount;

    private int abnormalCount;

    private int warnedCount;

    public int getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }

    public int getNormalCount()
    {
        return normalCount;
    }

    public void setNormalCount(int normalCount)
    {
        this.normalCount = normalCount;
    }

    public int getAbnormalCount()
    {
        return abnormalCount;
    }

    public void setAbnormalCount(int abnormalCount)
    {
        this.abnormalCount = abnormalCount;
    }

    public int getWarnedCount()
    {
        return warnedCount;
    }

    public void setWarnedCount(int warnedCount)
    {
        this.warnedCount = warnedCount;
    }
}
