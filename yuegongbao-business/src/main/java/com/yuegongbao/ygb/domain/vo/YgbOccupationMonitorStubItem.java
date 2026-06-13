package com.yuegongbao.ygb.domain.vo;

import java.math.BigDecimal;

/**
 * 职业病监测 Stub 明细。
 *
 * @author yuegongbao
 */
public class YgbOccupationMonitorStubItem extends YgbStubResponse
{
    private String statMonth;

    private String regionCode;

    private String industryType;

    private Integer enterpriseCount;

    private Integer workerCount;

    private Integer caseCount;

    private Integer highRiskEnterpriseCount;

    private BigDecimal incidenceRate;

    private String sourceChannel;

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

    public String getIndustryType()
    {
        return industryType;
    }

    public void setIndustryType(String industryType)
    {
        this.industryType = industryType;
    }

    public Integer getEnterpriseCount()
    {
        return enterpriseCount;
    }

    public void setEnterpriseCount(Integer enterpriseCount)
    {
        this.enterpriseCount = enterpriseCount;
    }

    public Integer getWorkerCount()
    {
        return workerCount;
    }

    public void setWorkerCount(Integer workerCount)
    {
        this.workerCount = workerCount;
    }

    public Integer getCaseCount()
    {
        return caseCount;
    }

    public void setCaseCount(Integer caseCount)
    {
        this.caseCount = caseCount;
    }

    public Integer getHighRiskEnterpriseCount()
    {
        return highRiskEnterpriseCount;
    }

    public void setHighRiskEnterpriseCount(Integer highRiskEnterpriseCount)
    {
        this.highRiskEnterpriseCount = highRiskEnterpriseCount;
    }

    public BigDecimal getIncidenceRate()
    {
        return incidenceRate;
    }

    public void setIncidenceRate(BigDecimal incidenceRate)
    {
        this.incidenceRate = incidenceRate;
    }

    public String getSourceChannel()
    {
        return sourceChannel;
    }

    public void setSourceChannel(String sourceChannel)
    {
        this.sourceChannel = sourceChannel;
    }
}
