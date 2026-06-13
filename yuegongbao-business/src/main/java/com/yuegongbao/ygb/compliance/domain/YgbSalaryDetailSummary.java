package com.yuegongbao.ygb.compliance.domain;

public class YgbSalaryDetailSummary
{
    private int totalCount;

    private int pendingPayCount;

    private int payingCount;

    private int paidCount;

    private int failedCount;

    private int checkFailedCount;

    public int getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }

    public int getPendingPayCount()
    {
        return pendingPayCount;
    }

    public void setPendingPayCount(int pendingPayCount)
    {
        this.pendingPayCount = pendingPayCount;
    }

    public int getPayingCount()
    {
        return payingCount;
    }

    public void setPayingCount(int payingCount)
    {
        this.payingCount = payingCount;
    }

    public int getPaidCount()
    {
        return paidCount;
    }

    public void setPaidCount(int paidCount)
    {
        this.paidCount = paidCount;
    }

    public int getFailedCount()
    {
        return failedCount;
    }

    public void setFailedCount(int failedCount)
    {
        this.failedCount = failedCount;
    }

    public int getCheckFailedCount()
    {
        return checkFailedCount;
    }

    public void setCheckFailedCount(int checkFailedCount)
    {
        this.checkFailedCount = checkFailedCount;
    }
}
