package com.yuegongbao.ygb.domain.vo;

import java.math.BigDecimal;

/**
 * 个税申报 Stub 明细。
 *
 * @author yuegongbao
 */
public class YgbTaxRecordStubItem extends YgbStubResponse
{
    private Long enterpriseId;

    private String enterpriseName;

    private Long personId;

    private String personName;

    private String idCard;

    private String regionCode;

    private BigDecimal declaredAmount;

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

    public BigDecimal getDeclaredAmount()
    {
        return declaredAmount;
    }

    public void setDeclaredAmount(BigDecimal declaredAmount)
    {
        this.declaredAmount = declaredAmount;
    }
}
