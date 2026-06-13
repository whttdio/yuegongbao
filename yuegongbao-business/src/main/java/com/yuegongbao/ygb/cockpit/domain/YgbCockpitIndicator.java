package com.yuegongbao.ygb.cockpit.domain;

import java.math.BigDecimal;

public class YgbCockpitIndicator
{
    private String statDate;

    private String regionCode;

    private String regionName;

    private Integer dispatchCompanyCount;

    private Integer employerCount;

    private Integer dispatchedWorkerCount;

    private Integer highRiskEnterpriseCount;

    private BigDecimal insuranceRate;

    private BigDecimal aqInsuranceRate;

    private Integer todayWarningCount;

    private BigDecimal expandCompletionRate;

    private BigDecimal newInjuryRate;

    private Integer onlineDeviceCount;

    private Integer pendingWarningCount;

    private Integer overdueInjuryCount;

    public String getStatDate()
    {
        return statDate;
    }

    public void setStatDate(String statDate)
    {
        this.statDate = statDate;
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

    public Integer getDispatchCompanyCount()
    {
        return dispatchCompanyCount;
    }

    public void setDispatchCompanyCount(Integer dispatchCompanyCount)
    {
        this.dispatchCompanyCount = dispatchCompanyCount;
    }

    public Integer getEmployerCount()
    {
        return employerCount;
    }

    public void setEmployerCount(Integer employerCount)
    {
        this.employerCount = employerCount;
    }

    public Integer getDispatchedWorkerCount()
    {
        return dispatchedWorkerCount;
    }

    public void setDispatchedWorkerCount(Integer dispatchedWorkerCount)
    {
        this.dispatchedWorkerCount = dispatchedWorkerCount;
    }

    public Integer getHighRiskEnterpriseCount()
    {
        return highRiskEnterpriseCount;
    }

    public void setHighRiskEnterpriseCount(Integer highRiskEnterpriseCount)
    {
        this.highRiskEnterpriseCount = highRiskEnterpriseCount;
    }

    public BigDecimal getInsuranceRate()
    {
        return insuranceRate;
    }

    public void setInsuranceRate(BigDecimal insuranceRate)
    {
        this.insuranceRate = insuranceRate;
    }

    public BigDecimal getAqInsuranceRate()
    {
        return aqInsuranceRate;
    }

    public void setAqInsuranceRate(BigDecimal aqInsuranceRate)
    {
        this.aqInsuranceRate = aqInsuranceRate;
    }

    public Integer getTodayWarningCount()
    {
        return todayWarningCount;
    }

    public void setTodayWarningCount(Integer todayWarningCount)
    {
        this.todayWarningCount = todayWarningCount;
    }

    public BigDecimal getExpandCompletionRate()
    {
        return expandCompletionRate;
    }

    public void setExpandCompletionRate(BigDecimal expandCompletionRate)
    {
        this.expandCompletionRate = expandCompletionRate;
    }

    public BigDecimal getNewInjuryRate()
    {
        return newInjuryRate;
    }

    public void setNewInjuryRate(BigDecimal newInjuryRate)
    {
        this.newInjuryRate = newInjuryRate;
    }

    public Integer getOnlineDeviceCount()
    {
        return onlineDeviceCount;
    }

    public void setOnlineDeviceCount(Integer onlineDeviceCount)
    {
        this.onlineDeviceCount = onlineDeviceCount;
    }

    public Integer getPendingWarningCount()
    {
        return pendingWarningCount;
    }

    public void setPendingWarningCount(Integer pendingWarningCount)
    {
        this.pendingWarningCount = pendingWarningCount;
    }

    public Integer getOverdueInjuryCount()
    {
        return overdueInjuryCount;
    }

    public void setOverdueInjuryCount(Integer overdueInjuryCount)
    {
        this.overdueInjuryCount = overdueInjuryCount;
    }
}
