package com.yuegongbao.ygb.regulation.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YgbUninsuredListSummary
{
    private Integer totalCount;
    private Integer pendingCount;
    private Integer unwarnedCount;
    private Integer completedCount;
    private Integer highSalaryCount;
    private Integer enterpriseCount;
    private List<Map<String, Object>> ygbExplanation = new ArrayList<>();
    private List<Map<String, Object>> azbExplanation = new ArrayList<>();

    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    public Integer getPendingCount() { return pendingCount; }
    public void setPendingCount(Integer pendingCount) { this.pendingCount = pendingCount; }
    public Integer getUnwarnedCount() { return unwarnedCount; }
    public void setUnwarnedCount(Integer unwarnedCount) { this.unwarnedCount = unwarnedCount; }
    public Integer getCompletedCount() { return completedCount; }
    public void setCompletedCount(Integer completedCount) { this.completedCount = completedCount; }
    public Integer getHighSalaryCount() { return highSalaryCount; }
    public void setHighSalaryCount(Integer highSalaryCount) { this.highSalaryCount = highSalaryCount; }
    public Integer getEnterpriseCount() { return enterpriseCount; }
    public void setEnterpriseCount(Integer enterpriseCount) { this.enterpriseCount = enterpriseCount; }
    public List<Map<String, Object>> getYgbExplanation() { return ygbExplanation; }
    public void setYgbExplanation(List<Map<String, Object>> ygbExplanation) { this.ygbExplanation = ygbExplanation; }
    public List<Map<String, Object>> getAzbExplanation() { return azbExplanation; }
    public void setAzbExplanation(List<Map<String, Object>> azbExplanation) { this.azbExplanation = azbExplanation; }
}
