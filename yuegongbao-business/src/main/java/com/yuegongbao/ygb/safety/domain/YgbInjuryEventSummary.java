package com.yuegongbao.ygb.safety.domain;

import java.util.List;
import java.util.Map;

public class YgbInjuryEventSummary
{
    private Integer totalCount;

    private Integer pendingCount;

    private Integer warningCount;

    private Integer overdueCount;

    private Integer recognizingCount;

    private Integer claimingCount;

    private Integer finishedCount;

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

    public Integer getPendingCount()
    {
        return pendingCount;
    }

    public void setPendingCount(Integer pendingCount)
    {
        this.pendingCount = pendingCount;
    }

    public Integer getWarningCount()
    {
        return warningCount;
    }

    public void setWarningCount(Integer warningCount)
    {
        this.warningCount = warningCount;
    }

    public Integer getOverdueCount()
    {
        return overdueCount;
    }

    public void setOverdueCount(Integer overdueCount)
    {
        this.overdueCount = overdueCount;
    }

    public Integer getRecognizingCount()
    {
        return recognizingCount;
    }

    public void setRecognizingCount(Integer recognizingCount)
    {
        this.recognizingCount = recognizingCount;
    }

    public Integer getClaimingCount()
    {
        return claimingCount;
    }

    public void setClaimingCount(Integer claimingCount)
    {
        this.claimingCount = claimingCount;
    }

    public Integer getFinishedCount()
    {
        return finishedCount;
    }

    public void setFinishedCount(Integer finishedCount)
    {
        this.finishedCount = finishedCount;
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
