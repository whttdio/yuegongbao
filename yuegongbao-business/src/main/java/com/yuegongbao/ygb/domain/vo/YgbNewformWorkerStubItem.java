package com.yuegongbao.ygb.domain.vo;

import java.math.BigDecimal;

/**
 * 新业态平台 Stub 明细。
 *
 * @author yuegongbao
 */
public class YgbNewformWorkerStubItem extends YgbStubResponse
{
    private Long enterpriseId;

    private String enterpriseName;

    private Long personId;

    private String personName;

    private String idCard;

    private String regionCode;

    private String platformName;

    private String employmentType;

    private String insuranceStatus;

    private String injuryInsuranceStatus;

    private BigDecimal monthlyIncome;

    public Long getEnterpriseId()
    {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }

    public String getEnterpriseName()
    {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName)
    {
        this.enterpriseName = enterpriseName;
    }

    public Long getPersonId()
    {
        return personId;
    }

    public void setPersonId(Long personId)
    {
        this.personId = personId;
    }

    public String getPersonName()
    {
        return personName;
    }

    public void setPersonName(String personName)
    {
        this.personName = personName;
    }

    public String getIdCard()
    {
        return idCard;
    }

    public void setIdCard(String idCard)
    {
        this.idCard = idCard;
    }

    public String getRegionCode()
    {
        return regionCode;
    }

    public void setRegionCode(String regionCode)
    {
        this.regionCode = regionCode;
    }

    public String getPlatformName()
    {
        return platformName;
    }

    public void setPlatformName(String platformName)
    {
        this.platformName = platformName;
    }

    public String getEmploymentType()
    {
        return employmentType;
    }

    public void setEmploymentType(String employmentType)
    {
        this.employmentType = employmentType;
    }

    public String getInsuranceStatus()
    {
        return insuranceStatus;
    }

    public void setInsuranceStatus(String insuranceStatus)
    {
        this.insuranceStatus = insuranceStatus;
    }

    public String getInjuryInsuranceStatus()
    {
        return injuryInsuranceStatus;
    }

    public void setInjuryInsuranceStatus(String injuryInsuranceStatus)
    {
        this.injuryInsuranceStatus = injuryInsuranceStatus;
    }

    public BigDecimal getMonthlyIncome()
    {
        return monthlyIncome;
    }

    public void setMonthlyIncome(BigDecimal monthlyIncome)
    {
        this.monthlyIncome = monthlyIncome;
    }
}
