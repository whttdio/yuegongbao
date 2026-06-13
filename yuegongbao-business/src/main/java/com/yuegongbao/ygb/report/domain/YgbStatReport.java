package com.yuegongbao.ygb.report.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;

public class YgbStatReport extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long reportId;

    @Excel(name = "报表编码")
    private String reportCode;

    @Excel(name = "报表名称")
    private String reportName;

    @Excel(name = "统计月份")
    private String statMonth;

    @Excel(name = "区域编码")
    private String regionCode;

    @Excel(name = "状态", readConverterExp = "0=草稿,1=已生成,2=已归档")
    private String reportStatus;

    @Excel(name = "核心数量")
    private Integer metricCount;

    @Excel(name = "核心数值")
    private BigDecimal metricAmount;

    @Excel(name = "核心比率")
    private BigDecimal metricRate;

    @Excel(name = "摘要", width = 40)
    private String reportSummary;

    private String attachmentUrl;

    private String sourceMode;

    private String keyword;

    private String itemDimension;

    private String sourceModule;

    private Integer minMetricCount;

    private Integer maxMetricCount;

    private BigDecimal minMetricRate;

    private BigDecimal maxMetricRate;

    private String riskCategory;

    private String batchNo;

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

    public String getReportCode()
    {
        return reportCode;
    }

    public void setReportCode(String reportCode)
    {
        this.reportCode = reportCode;
    }

    public String getReportName()
    {
        return reportName;
    }

    public void setReportName(String reportName)
    {
        this.reportName = reportName;
    }

    public String getStatMonth()
    {
        return statMonth;
    }

    public void setStatMonth(String statMonth)
    {
        this.statMonth = statMonth;
    }

    public String getRegionCode()
    {
        return regionCode;
    }

    public void setRegionCode(String regionCode)
    {
        this.regionCode = regionCode;
    }

    public String getReportStatus()
    {
        return reportStatus;
    }

    public void setReportStatus(String reportStatus)
    {
        this.reportStatus = reportStatus;
    }

    public Integer getMetricCount()
    {
        return metricCount;
    }

    public void setMetricCount(Integer metricCount)
    {
        this.metricCount = metricCount;
    }

    public BigDecimal getMetricAmount()
    {
        return metricAmount;
    }

    public void setMetricAmount(BigDecimal metricAmount)
    {
        this.metricAmount = metricAmount;
    }

    public BigDecimal getMetricRate()
    {
        return metricRate;
    }

    public void setMetricRate(BigDecimal metricRate)
    {
        this.metricRate = metricRate;
    }

    public String getReportSummary()
    {
        return reportSummary;
    }

    public void setReportSummary(String reportSummary)
    {
        this.reportSummary = reportSummary;
    }

    public String getAttachmentUrl()
    {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl)
    {
        this.attachmentUrl = attachmentUrl;
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

    public String getKeyword()
    {
        return keyword;
    }

    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }

    public String getItemDimension()
    {
        return itemDimension;
    }

    public void setItemDimension(String itemDimension)
    {
        this.itemDimension = itemDimension;
    }

    public String getSourceModule()
    {
        return sourceModule;
    }

    public void setSourceModule(String sourceModule)
    {
        this.sourceModule = sourceModule;
    }

    public Integer getMinMetricCount()
    {
        return minMetricCount;
    }

    public void setMinMetricCount(Integer minMetricCount)
    {
        this.minMetricCount = minMetricCount;
    }

    public Integer getMaxMetricCount()
    {
        return maxMetricCount;
    }

    public void setMaxMetricCount(Integer maxMetricCount)
    {
        this.maxMetricCount = maxMetricCount;
    }

    public BigDecimal getMinMetricRate()
    {
        return minMetricRate;
    }

    public void setMinMetricRate(BigDecimal minMetricRate)
    {
        this.minMetricRate = minMetricRate;
    }

    public BigDecimal getMaxMetricRate()
    {
        return maxMetricRate;
    }

    public void setMaxMetricRate(BigDecimal maxMetricRate)
    {
        this.maxMetricRate = maxMetricRate;
    }

    public String getRiskCategory()
    {
        return riskCategory;
    }

    public void setRiskCategory(String riskCategory)
    {
        this.riskCategory = riskCategory;
    }

    public String getBatchNo()
    {
        return batchNo;
    }

    public void setBatchNo(String batchNo)
    {
        this.batchNo = batchNo;
    }
}
