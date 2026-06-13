package com.yuegongbao.ygb.occupation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YgbOccupationMonitorSummary
{
    private int totalCount;

    private int yellowCount;

    private int redCount;

    private int totalCaseCount;

    private int totalHighRiskEnterpriseCount;

    private BigDecimal averageIncidence = BigDecimal.ZERO;

    private List<Map<String, Object>> ygbExplanation = new ArrayList<>();

    public int getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }

    public int getYellowCount()
    {
        return yellowCount;
    }

    public void setYellowCount(int yellowCount)
    {
        this.yellowCount = yellowCount;
    }

    public int getRedCount()
    {
        return redCount;
    }

    public void setRedCount(int redCount)
    {
        this.redCount = redCount;
    }

    public int getTotalCaseCount()
    {
        return totalCaseCount;
    }

    public void setTotalCaseCount(int totalCaseCount)
    {
        this.totalCaseCount = totalCaseCount;
    }

    public int getTotalHighRiskEnterpriseCount()
    {
        return totalHighRiskEnterpriseCount;
    }

    public void setTotalHighRiskEnterpriseCount(int totalHighRiskEnterpriseCount)
    {
        this.totalHighRiskEnterpriseCount = totalHighRiskEnterpriseCount;
    }

    public BigDecimal getAverageIncidence()
    {
        return averageIncidence;
    }

    public void setAverageIncidence(BigDecimal averageIncidence)
    {
        this.averageIncidence = averageIncidence;
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
