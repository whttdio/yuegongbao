package com.yuegongbao.ygb.report.domain;

import jakarta.validation.constraints.NotBlank;

public class YgbStatReportGenerateRequest
{
    @NotBlank(message = "报表编码不能为空")
    private String reportCode;

    @NotBlank(message = "统计月份不能为空")
    private String statMonth;

    private String regionCode;

    public String getReportCode()
    {
        return reportCode;
    }

    public void setReportCode(String reportCode)
    {
        this.reportCode = reportCode;
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
}
