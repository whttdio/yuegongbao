package com.yuegongbao.ygb.regulation.domain;

public class YgbSocialPaymentSummary
{
    private int totalCount;

    private int normalCount;

    private int unpaidCount;

    private int overdueCount;

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

    public int getUnpaidCount()
    {
        return unpaidCount;
    }

    public void setUnpaidCount(int unpaidCount)
    {
        this.unpaidCount = unpaidCount;
    }

    public int getOverdueCount()
    {
        return overdueCount;
    }

    public void setOverdueCount(int overdueCount)
    {
        this.overdueCount = overdueCount;
    }
}
