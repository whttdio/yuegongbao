package com.yuegongbao.ygb.newform.domain;

import java.math.BigDecimal;

public class YgbNewformPlatformSummary
{
    private Integer platformCount;

    private Integer workerCount;

    private Integer insuredCount;

    private Integer injuryInsuredCount;

    private Integer injuryUninsuredCount;

    private Integer injuryStoppedCount;

    private Integer warningCount;

    private BigDecimal averageIncome = BigDecimal.ZERO;

    public Integer getPlatformCount()
    {
        return platformCount;
    }

    public void setPlatformCount(Integer platformCount)
    {
        this.platformCount = platformCount;
    }

    public Integer getWorkerCount()
    {
        return workerCount;
    }

    public void setWorkerCount(Integer workerCount)
    {
        this.workerCount = workerCount;
    }

    public Integer getInsuredCount()
    {
        return insuredCount;
    }

    public void setInsuredCount(Integer insuredCount)
    {
        this.insuredCount = insuredCount;
    }

    public Integer getInjuryInsuredCount()
    {
        return injuryInsuredCount;
    }

    public void setInjuryInsuredCount(Integer injuryInsuredCount)
    {
        this.injuryInsuredCount = injuryInsuredCount;
    }

    public Integer getInjuryUninsuredCount()
    {
        return injuryUninsuredCount;
    }

    public void setInjuryUninsuredCount(Integer injuryUninsuredCount)
    {
        this.injuryUninsuredCount = injuryUninsuredCount;
    }

    public Integer getInjuryStoppedCount()
    {
        return injuryStoppedCount;
    }

    public void setInjuryStoppedCount(Integer injuryStoppedCount)
    {
        this.injuryStoppedCount = injuryStoppedCount;
    }

    public Integer getWarningCount()
    {
        return warningCount;
    }

    public void setWarningCount(Integer warningCount)
    {
        this.warningCount = warningCount;
    }

    public BigDecimal getAverageIncome()
    {
        return averageIncome;
    }

    public void setAverageIncome(BigDecimal averageIncome)
    {
        this.averageIncome = averageIncome;
    }
}
