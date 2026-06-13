package com.yuegongbao.ygb.aireport.domain;

public class YgbAiReportSubscriptionSummary
{
    private Integer totalCount;

    private Integer activeCount;

    private Integer monthlyCount;

    private Integer multiChannelCount;

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

    public Integer getMonthlyCount()
    {
        return monthlyCount;
    }

    public void setMonthlyCount(Integer monthlyCount)
    {
        this.monthlyCount = monthlyCount;
    }

    public Integer getMultiChannelCount()
    {
        return multiChannelCount;
    }

    public void setMultiChannelCount(Integer multiChannelCount)
    {
        this.multiChannelCount = multiChannelCount;
    }
}
