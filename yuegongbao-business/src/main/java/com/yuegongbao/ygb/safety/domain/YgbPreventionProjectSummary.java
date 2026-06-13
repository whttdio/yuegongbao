package com.yuegongbao.ygb.safety.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class YgbPreventionProjectSummary
{
    private Integer totalCount;
    private BigDecimal totalBudget;
    private Integer activeProjectCount;
    private Integer acceptancePendingCount;
    private Integer lowScoreCount;
    private Integer trainingAiProjectCount;
    private Integer highBudgetCount;
    private List<Map<String, Object>> ygbExplanation;
    private List<Map<String, Object>> azbExplanation;

    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    public BigDecimal getTotalBudget() { return totalBudget; }
    public void setTotalBudget(BigDecimal totalBudget) { this.totalBudget = totalBudget; }
    public Integer getActiveProjectCount() { return activeProjectCount; }
    public void setActiveProjectCount(Integer activeProjectCount) { this.activeProjectCount = activeProjectCount; }
    public Integer getAcceptancePendingCount() { return acceptancePendingCount; }
    public void setAcceptancePendingCount(Integer acceptancePendingCount) { this.acceptancePendingCount = acceptancePendingCount; }
    public Integer getLowScoreCount() { return lowScoreCount; }
    public void setLowScoreCount(Integer lowScoreCount) { this.lowScoreCount = lowScoreCount; }
    public Integer getTrainingAiProjectCount() { return trainingAiProjectCount; }
    public void setTrainingAiProjectCount(Integer trainingAiProjectCount) { this.trainingAiProjectCount = trainingAiProjectCount; }
    public Integer getHighBudgetCount() { return highBudgetCount; }
    public void setHighBudgetCount(Integer highBudgetCount) { this.highBudgetCount = highBudgetCount; }
    public List<Map<String, Object>> getYgbExplanation() { return ygbExplanation; }
    public void setYgbExplanation(List<Map<String, Object>> ygbExplanation) { this.ygbExplanation = ygbExplanation; }
    public List<Map<String, Object>> getAzbExplanation() { return azbExplanation; }
    public void setAzbExplanation(List<Map<String, Object>> azbExplanation) { this.azbExplanation = azbExplanation; }
}
