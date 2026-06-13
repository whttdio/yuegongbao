package com.yuegongbao.ygb.regulation.domain;

public class YgbEmploymentRatioSummary
{
    private int totalCount;

    private int normalCount;

    private int yellowCount;

    private int redCount;

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

    public int getYellowCount()
    {
        return yellowCount;
    }

    public void setYellowCount(int yellowCount)
    {
        this.yellowCount = yellowCount;
    }

    public int getRedCount()
    {
        return redCount;
    }

    public void setRedCount(int redCount)
    {
        this.redCount = redCount;
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
