package com.yuegongbao.ygb.compliance.domain;

import java.math.BigDecimal;

public class YgbSalaryArrearsSummary
{
    private Integer totalArrears;

    private BigDecimal totalArrearsAmount = BigDecimal.ZERO;

    private Integer unhandledCount;

    private Integer processingCount;

    private Integer handledCount;

    private Integer overdueCount;

    public Integer getTotalArrears()
    {
        return totalArrears;
    }

    public void setTotalArrears(Integer totalArrears)
    {
        this.totalArrears = totalArrears;
    }

    public BigDecimal getTotalArrearsAmount()
    {
        return totalArrearsAmount;
    }

    public void setTotalArrearsAmount(BigDecimal totalArrearsAmount)
    {
        this.totalArrearsAmount = totalArrearsAmount;
    }

    public Integer getUnhandledCount()
    {
        return unhandledCount;
    }

    public void setUnhandledCount(Integer unhandledCount)
    {
        this.unhandledCount = unhandledCount;
    }

    public Integer getProcessingCount()
    {
        return processingCount;
    }

    public void setProcessingCount(Integer processingCount)
    {
        this.processingCount = processingCount;
    }

    public Integer getHandledCount()
    {
        return handledCount;
    }

    public void setHandledCount(Integer handledCount)
    {
        this.handledCount = handledCount;
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
