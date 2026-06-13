package com.yuegongbao.ygb.compliance.domain;

import java.math.BigDecimal;

public class YgbSalaryBatchSummary
{
    private int totalCount;

    private int pendingGenerateCount;

    private int pendingSubmitCount;

    private int payingCount;

    private int paidCount;

    private int failedCount;

    private int awaitingAccountCount;

    private int totalPersonCount;

    private int arrearsEnterpriseCount;

    private int failedBatchCount;

    private BigDecimal regulatorAccountBalance;

    private BigDecimal accountGapAmount;

    public int getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }

    public int getPendingGenerateCount()
    {
        return pendingGenerateCount;
    }

    public void setPendingGenerateCount(int pendingGenerateCount)
    {
        this.pendingGenerateCount = pendingGenerateCount;
    }

    public int getPendingSubmitCount()
    {
        return pendingSubmitCount;
    }

    public void setPendingSubmitCount(int pendingSubmitCount)
    {
        this.pendingSubmitCount = pendingSubmitCount;
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

    public int getAwaitingAccountCount()
    {
        return awaitingAccountCount;
    }

    public void setAwaitingAccountCount(int awaitingAccountCount)
    {
        this.awaitingAccountCount = awaitingAccountCount;
    }

    public int getTotalPersonCount()
    {
        return totalPersonCount;
    }

    public void setTotalPersonCount(int totalPersonCount)
    {
        this.totalPersonCount = totalPersonCount;
    }

    public int getArrearsEnterpriseCount()
    {
        return arrearsEnterpriseCount;
    }

    public void setArrearsEnterpriseCount(int arrearsEnterpriseCount)
    {
        this.arrearsEnterpriseCount = arrearsEnterpriseCount;
    }

    public int getFailedBatchCount()
    {
        return failedBatchCount;
    }

    public void setFailedBatchCount(int failedBatchCount)
    {
        this.failedBatchCount = failedBatchCount;
    }

    public BigDecimal getRegulatorAccountBalance()
    {
        return regulatorAccountBalance;
    }

    public void setRegulatorAccountBalance(BigDecimal regulatorAccountBalance)
    {
        this.regulatorAccountBalance = regulatorAccountBalance;
    }

    public BigDecimal getAccountGapAmount()
    {
        return accountGapAmount;
    }

    public void setAccountGapAmount(BigDecimal accountGapAmount)
    {
        this.accountGapAmount = accountGapAmount;
    }
}
