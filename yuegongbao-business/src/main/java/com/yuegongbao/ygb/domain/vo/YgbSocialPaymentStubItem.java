package com.yuegongbao.ygb.domain.vo;

import java.math.BigDecimal;

/**
 * 社保缴费 Stub 明细。
 *
 * @author yuegongbao
 */
public class YgbSocialPaymentStubItem extends YgbStubResponse
{
    private Long enterpriseId;

    private String enterpriseName;

    private Long personId;

    private String personName;

    private String idCard;

    private String regionCode;

    private BigDecimal baseAmount;

    private BigDecimal paidAmount;

    private String paymentStatus;

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

    public BigDecimal getBaseAmount()
    {
        return baseAmount;
    }

    public void setBaseAmount(BigDecimal baseAmount)
    {
        this.baseAmount = baseAmount;
    }

    public BigDecimal getPaidAmount()
    {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount)
    {
        this.paidAmount = paidAmount;
    }

    public String getPaymentStatus()
    {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus)
    {
        this.paymentStatus = paymentStatus;
    }
}
