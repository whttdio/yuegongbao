package com.yuegongbao.ygb.aireport.domain;

import java.util.List;
import jakarta.validation.constraints.NotBlank;

public class YgbAiReportGenerateRequest
{
    @NotBlank(message = "报告类型不能为空")
    private String reportType;

    private String regionCode;

    private String startDate;

    private String endDate;

    private List<String> dimensions;

    private String enterpriseType;

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

    public String getStartDate()
    {
        return startDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public List<String> getDimensions()
    {
        return dimensions;
    }

    public void setDimensions(List<String> dimensions)
    {
        this.dimensions = dimensions;
    }

    public String getEnterpriseType()
    {
        return enterpriseType;
    }

    public void setEnterpriseType(String enterpriseType)
    {
        this.enterpriseType = enterpriseType;
    }
}
