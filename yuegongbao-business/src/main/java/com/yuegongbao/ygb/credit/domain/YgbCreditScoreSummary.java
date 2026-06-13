package com.yuegongbao.ygb.credit.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YgbCreditScoreSummary
{
    private Integer totalCount;
    private BigDecimal averageScore;
    private Integer redCount;
    private Integer highGradeCount;
    private Integer dCount;
    private Integer yellowCount;
    private List<Map<String, Object>> ygbExplanation = new ArrayList<>();
    private List<Map<String, Object>> azbExplanation = new ArrayList<>();

    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    public BigDecimal getAverageScore() { return averageScore; }
    public void setAverageScore(BigDecimal averageScore) { this.averageScore = averageScore; }
    public Integer getRedCount() { return redCount; }
    public void setRedCount(Integer redCount) { this.redCount = redCount; }
    public Integer getHighGradeCount() { return highGradeCount; }
    public void setHighGradeCount(Integer highGradeCount) { this.highGradeCount = highGradeCount; }
    public Integer getDCount() { return dCount; }
    public void setDCount(Integer dCount) { this.dCount = dCount; }
    public Integer getYellowCount() { return yellowCount; }
    public void setYellowCount(Integer yellowCount) { this.yellowCount = yellowCount; }
    public List<Map<String, Object>> getYgbExplanation() { return ygbExplanation; }
    public void setYgbExplanation(List<Map<String, Object>> ygbExplanation) { this.ygbExplanation = ygbExplanation; }
    public List<Map<String, Object>> getAzbExplanation() { return azbExplanation; }
    public void setAzbExplanation(List<Map<String, Object>> azbExplanation) { this.azbExplanation = azbExplanation; }
}
