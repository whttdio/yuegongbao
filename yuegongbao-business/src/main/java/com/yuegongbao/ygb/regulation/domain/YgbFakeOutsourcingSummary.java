package com.yuegongbao.ygb.regulation.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YgbFakeOutsourcingSummary
{
    private int totalCount;

    private int normalCount;

    private int suspectedCount;

    private int warnedCount;

    private int lowScoreCount;

    private List<Map<String, Object>> ygbExplanation = new ArrayList<>();

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

    public int getSuspectedCount()
    {
        return suspectedCount;
    }

    public void setSuspectedCount(int suspectedCount)
    {
        this.suspectedCount = suspectedCount;
    }

    public int getWarnedCount()
    {
        return warnedCount;
    }

    public void setWarnedCount(int warnedCount)
    {
        this.warnedCount = warnedCount;
    }

    public int getLowScoreCount()
    {
        return lowScoreCount;
    }

    public void setLowScoreCount(int lowScoreCount)
    {
        this.lowScoreCount = lowScoreCount;
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
