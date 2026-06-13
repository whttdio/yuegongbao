package com.yuegongbao.ygb.aqins.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class YgbAqInsuranceSummary
{
    private Integer totalCount;

    private Integer validCount;

    private Integer riskCount;

    private BigDecimal remainingFundAmountTotal;

    private Integer soonExpireCount;

    private Integer expiredCount;

    private Integer insurerCount;

    private BigDecimal coverageRate;

    private List<Map<String, Object>> ygbExplanation;

    private List<Map<String, Object>> azbExplanation;

    public Integer getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount)
    {
        this.totalCount = totalCount;
    }

    public Integer getValidCount()
    {
        return validCount;
    }

    public void setValidCount(Integer validCount)
    {
        this.validCount = validCount;
    }

    public Integer getRiskCount()
    {
        return riskCount;
    }

    public void setRiskCount(Integer riskCount)
    {
        this.riskCount = riskCount;
    }

    public BigDecimal getRemainingFundAmountTotal()
    {
        return remainingFundAmountTotal;
    }

    public void setRemainingFundAmountTotal(BigDecimal remainingFundAmountTotal)
    {
        this.remainingFundAmountTotal = remainingFundAmountTotal;
    }

    public Integer getSoonExpireCount()
    {
        return soonExpireCount;
    }

    public void setSoonExpireCount(Integer soonExpireCount)
    {
        this.soonExpireCount = soonExpireCount;
    }

    public Integer getExpiredCount()
    {
        return expiredCount;
    }

    public void setExpiredCount(Integer expiredCount)
    {
        this.expiredCount = expiredCount;
    }

    public Integer getInsurerCount()
    {
        return insurerCount;
    }

    public void setInsurerCount(Integer insurerCount)
    {
        this.insurerCount = insurerCount;
    }

    public BigDecimal getCoverageRate()
    {
        return coverageRate;
    }

    public void setCoverageRate(BigDecimal coverageRate)
    {
        this.coverageRate = coverageRate;
    }

    public List<Map<String, Object>> getYgbExplanation()
    {
        return ygbExplanation;
    }

    public void setYgbExplanation(List<Map<String, Object>> ygbExplanation)
    {
        this.ygbExplanation = ygbExplanation;
    }

    public List<Map<String, Object>> getAzbExplanation()
    {
        return azbExplanation;
    }

    public void setAzbExplanation(List<Map<String, Object>> azbExplanation)
    {
        this.azbExplanation = azbExplanation;
    }
}
