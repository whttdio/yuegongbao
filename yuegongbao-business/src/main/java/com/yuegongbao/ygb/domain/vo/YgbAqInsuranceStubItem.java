package com.yuegongbao.ygb.domain.vo;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 安责险 Stub 明细。
 *
 * @author yuegongbao
 */
public class YgbAqInsuranceStubItem extends YgbStubResponse
{
    private Long enterpriseId;

    private String enterpriseName;

    private String regionCode;

    private String insurerName;

    private String policyNo;

    private BigDecimal premium;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    private String policyStatus;

    private Integer insuredPersonCount;

    private BigDecimal preventionFundRatio;

    private BigDecimal preventionFundAmount;

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

    public String getRegionCode()
    {
        return regionCode;
    }

    public void setRegionCode(String regionCode)
    {
        this.regionCode = regionCode;
    }

    public String getInsurerName()
    {
        return insurerName;
    }

    public void setInsurerName(String insurerName)
    {
        this.insurerName = insurerName;
    }

    public String getPolicyNo()
    {
        return policyNo;
    }

    public void setPolicyNo(String policyNo)
    {
        this.policyNo = policyNo;
    }

    public BigDecimal getPremium()
    {
        return premium;
    }

    public void setPremium(BigDecimal premium)
    {
        this.premium = premium;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    public String getPolicyStatus()
    {
        return policyStatus;
    }

    public void setPolicyStatus(String policyStatus)
    {
        this.policyStatus = policyStatus;
    }

    public Integer getInsuredPersonCount()
    {
        return insuredPersonCount;
    }

    public void setInsuredPersonCount(Integer insuredPersonCount)
    {
        this.insuredPersonCount = insuredPersonCount;
    }

    public BigDecimal getPreventionFundRatio()
    {
        return preventionFundRatio;
    }

    public void setPreventionFundRatio(BigDecimal preventionFundRatio)
    {
        this.preventionFundRatio = preventionFundRatio;
    }

    public BigDecimal getPreventionFundAmount()
    {
        return preventionFundAmount;
    }

    public void setPreventionFundAmount(BigDecimal preventionFundAmount)
    {
        this.preventionFundAmount = preventionFundAmount;
    }
}
