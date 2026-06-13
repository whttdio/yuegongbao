package com.yuegongbao.ygb.aireport.domain;

import java.math.BigDecimal;

public class YgbAiReportDashboardRankItem
{
    private Long reportId;

    private String reportType;

    private String regionCode;

    private String regionName;

    private String enterpriseType;

    private BigDecimal totalScore;

    private String riskLevel;

    private Integer rankingNo;

    private String reportSummary;

    private String highlight;

    private String riskReason;

    private String advice;

    public Long getReportId()
    {
        return reportId;
    }

    public void setReportId(Long reportId)
    {
        this.reportId = reportId;
    }

    public String getReportType()
    {
        return reportType;
    }

    public void setReportType(String reportType)
    {
        this.reportType = reportType;
    }

    public String getRegionCode()
    {
        return regionCode;
    }

    public void setRegionCode(String regionCode)
    {
        this.regionCode = regionCode;
    }

    public String getRegionName()
    {
        return regionName;
    }

    public void setRegionName(String regionName)
    {
        this.regionName = regionName;
    }

    public String getEnterpriseType()
    {
        return enterpriseType;
    }

    public void setEnterpriseType(String enterpriseType)
    {
        this.enterpriseType = enterpriseType;
    }

    public BigDecimal getTotalScore()
    {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore)
    {
        this.totalScore = totalScore;
    }

    public String getRiskLevel()
    {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel)
    {
        this.riskLevel = riskLevel;
    }

    public Integer getRankingNo()
    {
        return rankingNo;
    }

    public void setRankingNo(Integer rankingNo)
    {
        this.rankingNo = rankingNo;
    }

    public String getReportSummary()
    {
        return reportSummary;
    }

    public void setReportSummary(String reportSummary)
    {
        this.reportSummary = reportSummary;
    }

    public String getHighlight()
    {
        return highlight;
    }

    public void setHighlight(String highlight)
    {
        this.highlight = highlight;
    }

    public String getRiskReason()
    {
        return riskReason;
    }

    public void setRiskReason(String riskReason)
    {
        this.riskReason = riskReason;
    }

    public String getAdvice()
    {
        return advice;
    }

    public void setAdvice(String advice)
    {
        this.advice = advice;
    }
}
