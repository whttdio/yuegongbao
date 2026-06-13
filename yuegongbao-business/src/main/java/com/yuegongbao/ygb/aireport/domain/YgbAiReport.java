package com.yuegongbao.ygb.aireport.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;

public class YgbAiReport extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "报告ID")
    private Long reportId;

    @Excel(name = "报告类型")
    private String reportType;

    @Excel(name = "区域编码")
    private String regionCode;

    @Excel(name = "区域")
    private String regionName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始日期", width = 16, dateFormat = "yyyy-MM-dd")
    private Date periodStart;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束日期", width = 16, dateFormat = "yyyy-MM-dd")
    private Date periodEnd;

    @Excel(name = "企业类型")
    private String enterpriseType;

    @Excel(name = "维度选择", width = 20)
    private String selectedDimensions;

    @Excel(name = "综合得分")
    private BigDecimal totalScore;

    @Excel(name = "风险等级")
    private String riskLevel;

    @Excel(name = "区域排名")
    private Integer rankingNo;

    @Excel(name = "模型版本")
    private String configVersion;

    @Excel(name = "结论摘要", width = 40)
    private String reportSummary;

    private String reportPdfUrl;

    private String reportHtml;

    private String sourceMode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "生成时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date generatedTime;

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

    public Date getPeriodStart()
    {
        return periodStart;
    }

    public void setPeriodStart(Date periodStart)
    {
        this.periodStart = periodStart;
    }

    public Date getPeriodEnd()
    {
        return periodEnd;
    }

    public void setPeriodEnd(Date periodEnd)
    {
        this.periodEnd = periodEnd;
    }

    public String getEnterpriseType()
    {
        return enterpriseType;
    }

    public void setEnterpriseType(String enterpriseType)
    {
        this.enterpriseType = enterpriseType;
    }

    public String getSelectedDimensions()
    {
        return selectedDimensions;
    }

    public void setSelectedDimensions(String selectedDimensions)
    {
        this.selectedDimensions = selectedDimensions;
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

    public String getConfigVersion()
    {
        return configVersion;
    }

    public void setConfigVersion(String configVersion)
    {
        this.configVersion = configVersion;
    }

    public String getReportSummary()
    {
        return reportSummary;
    }

    public void setReportSummary(String reportSummary)
    {
        this.reportSummary = reportSummary;
    }

    public String getReportPdfUrl()
    {
        return reportPdfUrl;
    }

    public void setReportPdfUrl(String reportPdfUrl)
    {
        this.reportPdfUrl = reportPdfUrl;
    }

    public String getReportHtml()
    {
        return reportHtml;
    }

    public void setReportHtml(String reportHtml)
    {
        this.reportHtml = reportHtml;
    }

    public String getSourceMode()
    {
        return sourceMode;
    }

    public void setSourceMode(String sourceMode)
    {
        this.sourceMode = sourceMode;
    }

    public Date getGeneratedTime()
    {
        return generatedTime;
    }

    public void setGeneratedTime(Date generatedTime)
    {
        this.generatedTime = generatedTime;
    }
}
