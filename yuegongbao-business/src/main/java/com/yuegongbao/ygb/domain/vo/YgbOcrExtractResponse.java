package com.yuegongbao.ygb.domain.vo;

import java.math.BigDecimal;

/**
 * OCR 合同提取返回。
 *
 * @author yuegongbao
 */
public class YgbOcrExtractResponse extends YgbStubResponse
{
    private String contractNo;

    private String personName;

    private String idCard;

    private BigDecimal monthlyWage;

    public String getContractNo()
    {
        return contractNo;
    }

    public void setContractNo(String contractNo)
    {
        this.contractNo = contractNo;
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

    public BigDecimal getMonthlyWage()
    {
        return monthlyWage;
    }

    public void setMonthlyWage(BigDecimal monthlyWage)
    {
        this.monthlyWage = monthlyWage;
    }
}
