package com.yuegongbao.ygb.aireport.domain;

import java.math.BigDecimal;

public class YgbAiReportItem
{
    private Long itemId;

    private Long reportId;

    private String dimensionCode;

    private String dimensionName;

    private String metricLabel;

    private BigDecimal metricValue;

    private BigDecimal targetValue;

    private BigDecimal dimensionWeight;

    private BigDecimal dimensionScore;

    private String riskLevel;

    private String suggestionText;

    private String detailJson;

    private Integer sortNo;

    public Long getItemId()
    {
        return itemId;
    }

    public void setItemId(Long itemId)
    {
        this.itemId = itemId;
    }

    public Long getReportId()
    {
        return reportId;
    }

    public void setReportId(Long reportId)
    {
        this.reportId = reportId;
    }

    public String getDimensionCode()
    {
        return dimensionCode;
    }

    public void setDimensionCode(String dimensionCode)
    {
        this.dimensionCode = dimensionCode;
    }

    public String getDimensionName()
    {
        return dimensionName;
    }

    public void setDimensionName(String dimensionName)
    {
        this.dimensionName = dimensionName;
    }

    public String getMetricLabel()
    {
        return metricLabel;
    }

    public void setMetricLabel(String metricLabel)
    {
        this.metricLabel = metricLabel;
    }

    public BigDecimal getMetricValue()
    {
        return metricValue;
    }

    public void setMetricValue(BigDecimal metricValue)
    {
        this.metricValue = metricValue;
    }

    public BigDecimal getTargetValue()
    {
        return targetValue;
    }

    public void setTargetValue(BigDecimal targetValue)
    {
        this.targetValue = targetValue;
    }

    public BigDecimal getDimensionWeight()
    {
        return dimensionWeight;
    }

    public void setDimensionWeight(BigDecimal dimensionWeight)
    {
        this.dimensionWeight = dimensionWeight;
    }

    public BigDecimal getDimensionScore()
    {
        return dimensionScore;
    }

    public void setDimensionScore(BigDecimal dimensionScore)
    {
        this.dimensionScore = dimensionScore;
    }

    public String getRiskLevel()
    {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel)
    {
        this.riskLevel = riskLevel;
    }

    public String getSuggestionText()
    {
        return suggestionText;
    }

    public void setSuggestionText(String suggestionText)
    {
        this.suggestionText = suggestionText;
    }

    public String getDetailJson()
    {
        return detailJson;
    }

    public void setDetailJson(String detailJson)
    {
        this.detailJson = detailJson;
    }

    public Integer getSortNo()
    {
        return sortNo;
    }

    public void setSortNo(Integer sortNo)
    {
        this.sortNo = sortNo;
    }
}
