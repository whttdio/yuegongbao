package com.yuegongbao.ygb.warning.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YgbWarningRuleSummary
{
    private Integer totalCount;
    private Integer enabledCount;
    private Integer redCount;
    private Integer deviceInjuryCount;
    private BigDecimal enabledRatio;
    private Integer deviceCount;
    private Integer expansionCount;
    private Integer averageTimeoutMinutes;
    private List<Map<String, Object>> ygbExplanation = new ArrayList<>();
    private List<Map<String, Object>> azbExplanation = new ArrayList<>();

    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    public Integer getEnabledCount() { return enabledCount; }
    public void setEnabledCount(Integer enabledCount) { this.enabledCount = enabledCount; }
    public Integer getRedCount() { return redCount; }
    public void setRedCount(Integer redCount) { this.redCount = redCount; }
    public Integer getDeviceInjuryCount() { return deviceInjuryCount; }
    public void setDeviceInjuryCount(Integer deviceInjuryCount) { this.deviceInjuryCount = deviceInjuryCount; }
    public BigDecimal getEnabledRatio() { return enabledRatio; }
    public void setEnabledRatio(BigDecimal enabledRatio) { this.enabledRatio = enabledRatio; }
    public Integer getDeviceCount() { return deviceCount; }
    public void setDeviceCount(Integer deviceCount) { this.deviceCount = deviceCount; }
    public Integer getExpansionCount() { return expansionCount; }
    public void setExpansionCount(Integer expansionCount) { this.expansionCount = expansionCount; }
    public Integer getAverageTimeoutMinutes() { return averageTimeoutMinutes; }
    public void setAverageTimeoutMinutes(Integer averageTimeoutMinutes) { this.averageTimeoutMinutes = averageTimeoutMinutes; }
    public List<Map<String, Object>> getYgbExplanation() { return ygbExplanation; }
    public void setYgbExplanation(List<Map<String, Object>> ygbExplanation) { this.ygbExplanation = ygbExplanation; }
    public List<Map<String, Object>> getAzbExplanation() { return azbExplanation; }
    public void setAzbExplanation(List<Map<String, Object>> azbExplanation) { this.azbExplanation = azbExplanation; }
}
