package com.yuegongbao.ygb.warning.domain;

import java.util.List;
import java.util.Map;

/**
 * 预警汇总视图。
 *
 * @author yuegongbao
 */
public class YgbWarningSummary
{
    private Integer totalCount;

    private Integer pendingCount;

    private Integer processingCount;

    private Integer closedCount;

    private Integer misreportCount;

    private Integer upgradedCount;

    private Integer tipCount;

    private Integer yellowCount;

    private Integer redCount;

    private Integer socialCount;

    private Integer taxCount;

    private Integer expansionCount;

    private Integer specialCount;

    private Integer deviceCount;

    private Integer injuryCount;

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

    public Integer getProcessingCount()
    {
        return processingCount;
    }

    public void setProcessingCount(Integer processingCount)
    {
        this.processingCount = processingCount;
    }

    public Integer getClosedCount()
    {
        return closedCount;
    }

    public void setClosedCount(Integer closedCount)
    {
        this.closedCount = closedCount;
    }

    public Integer getMisreportCount()
    {
        return misreportCount;
    }

    public void setMisreportCount(Integer misreportCount)
    {
        this.misreportCount = misreportCount;
    }

    public Integer getUpgradedCount()
    {
        return upgradedCount;
    }

    public void setUpgradedCount(Integer upgradedCount)
    {
        this.upgradedCount = upgradedCount;
    }

    public Integer getTipCount()
    {
        return tipCount;
    }

    public void setTipCount(Integer tipCount)
    {
        this.tipCount = tipCount;
    }

    public Integer getYellowCount()
    {
        return yellowCount;
    }

    public void setYellowCount(Integer yellowCount)
    {
        this.yellowCount = yellowCount;
    }

    public Integer getRedCount()
    {
        return redCount;
    }

    public void setRedCount(Integer redCount)
    {
        this.redCount = redCount;
    }

    public Integer getSocialCount()
    {
        return socialCount;
    }

    public void setSocialCount(Integer socialCount)
    {
        this.socialCount = socialCount;
    }

    public Integer getTaxCount()
    {
        return taxCount;
    }

    public void setTaxCount(Integer taxCount)
    {
        this.taxCount = taxCount;
    }

    public Integer getExpansionCount()
    {
        return expansionCount;
    }

    public void setExpansionCount(Integer expansionCount)
    {
        this.expansionCount = expansionCount;
    }

    public Integer getSpecialCount()
    {
        return specialCount;
    }

    public void setSpecialCount(Integer specialCount)
    {
        this.specialCount = specialCount;
    }

    public Integer getDeviceCount()
    {
        return deviceCount;
    }

    public void setDeviceCount(Integer deviceCount)
    {
        this.deviceCount = deviceCount;
    }

    public Integer getInjuryCount()
    {
        return injuryCount;
    }

    public void setInjuryCount(Integer injuryCount)
    {
        this.injuryCount = injuryCount;
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
