package com.yuegongbao.ygb.newform.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YgbNewformWorkerSummary
{
    private int totalCount;

    private int insuredCount;

    private int uninsuredCount;

    private int stoppedCount;

    private int warningCount;

    private BigDecimal averageIncome = BigDecimal.ZERO;

    private List<Map<String, Object>> ygbExplanation = new ArrayList<>();

    public int getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }

    public int getInsuredCount()
    {
        return insuredCount;
    }

    public void setInsuredCount(int insuredCount)
    {
        this.insuredCount = insuredCount;
    }

    public int getUninsuredCount()
    {
        return uninsuredCount;
    }

    public void setUninsuredCount(int uninsuredCount)
    {
        this.uninsuredCount = uninsuredCount;
    }

    public int getStoppedCount()
    {
        return stoppedCount;
    }

    public void setStoppedCount(int stoppedCount)
    {
        this.stoppedCount = stoppedCount;
    }

    public int getWarningCount()
    {
        return warningCount;
    }

    public void setWarningCount(int warningCount)
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

    public List<Map<String, Object>> getYgbExplanation()
    {
        return ygbExplanation;
    }

    public void setYgbExplanation(List<Map<String, Object>> ygbExplanation)
    {
        this.ygbExplanation = ygbExplanation;
    }
}
