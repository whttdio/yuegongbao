package com.yuegongbao.ygb.aqins.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class YgbPreventionFundSummary
{
    private Integer totalCount;

    private BigDecimal remainingAmountTotal;

    private Integer lowBalanceCount;

    private Integer inUseCount;

    private Integer nonStubCount;

    private Integer missingEvidenceCount;

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

    public BigDecimal getRemainingAmountTotal()
    {
        return remainingAmountTotal;
    }

    public void setRemainingAmountTotal(BigDecimal remainingAmountTotal)
    {
        this.remainingAmountTotal = remainingAmountTotal;
    }

    public Integer getLowBalanceCount()
    {
        return lowBalanceCount;
    }

    public void setLowBalanceCount(Integer lowBalanceCount)
    {
        this.lowBalanceCount = lowBalanceCount;
    }

    public Integer getInUseCount()
    {
        return inUseCount;
    }

    public void setInUseCount(Integer inUseCount)
    {
        this.inUseCount = inUseCount;
    }

    public Integer getNonStubCount()
    {
        return nonStubCount;
    }

    public void setNonStubCount(Integer nonStubCount)
    {
        this.nonStubCount = nonStubCount;
    }

    public Integer getMissingEvidenceCount()
    {
        return missingEvidenceCount;
    }

    public void setMissingEvidenceCount(Integer missingEvidenceCount)
    {
        this.missingEvidenceCount = missingEvidenceCount;
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
