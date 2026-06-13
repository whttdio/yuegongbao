package com.yuegongbao.ygb.aireport.domain;

public class YgbAiReportTaskSummary
{
    private Integer totalCount;

    private Integer pendingCount;

    private Integer processingCount;

    private Integer closedCount;

    private Integer overdueCount;

    public Integer getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount)
    {
        this.totalCount = totalCount;
    }

    public Integer getPendingCount()
    {
        return pendingCount;
    }

    public void setPendingCount(Integer pendingCount)
    {
        this.pendingCount = pendingCount;
    }

    public Integer getProcessingCount()
    {
        return processingCount;
    }

    public void setProcessingCount(Integer processingCount)
    {
        this.processingCount = processingCount;
    }

    public Integer getClosedCount()
    {
        return closedCount;
    }

    public void setClosedCount(Integer closedCount)
    {
        this.closedCount = closedCount;
    }

    public Integer getOverdueCount()
    {
        return overdueCount;
    }

    public void setOverdueCount(Integer overdueCount)
    {
        this.overdueCount = overdueCount;
    }
}
