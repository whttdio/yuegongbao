package com.yuegongbao.ygb.aqins.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;

/**
 * 安责险投保监管对象 t_aq_insurance。
 *
 * @author yuegongbao
 */
public class YgbAqInsurance extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "保单ID")
    private Long policyId;

    @Excel(name = "统计月份")
    private String statMonth;

    private Long enterpriseId;

    @Excel(name = "企业名称")
    private String enterpriseName;

    @Excel(name = "区域编码")
    private String regionCode;

    @Excel(name = "保险机构")
    private String insurerName;

    @Excel(name = "保单号")
    private String policyNo;

    @Excel(name = "保费")
    private BigDecimal premium;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "起保日期", width = 16, dateFormat = "yyyy-MM-dd")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "止保日期", width = 16, dateFormat = "yyyy-MM-dd")
    private Date endDate;

    @Excel(name = "保单状态", readConverterExp = "0=未生效,1=有效,2=即将到期,3=已过期")
    private String policyStatus;

    @Excel(name = "参保人数")
    private Integer insuredPersonCount;

    @Excel(name = "预防费比例")
    private BigDecimal preventionFundRatio;

    @Excel(name = "预防费计提金额")
    private BigDecimal preventionFundAmount;

    @Excel(name = "已使用金额")
    private BigDecimal usedFundAmount;

    @Excel(name = "可用余额")
    private BigDecimal remainingFundAmount;

    @Excel(name = "到期剩余天数")
    private Integer expireInDays;

    private String sourceSerialNo;

    private String sourceStatus;

    private String sourceMessage;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date callbackTime;

    private String rawPayload;

    public Long getPolicyId()
    {
        return policyId;
    }

    public void setPolicyId(Long policyId)
    {
        this.policyId = policyId;
    }

    public String getStatMonth()
    {
        return statMonth;
    }

    public void setStatMonth(String statMonth)
    {
        this.statMonth = statMonth;
    }

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

    public BigDecimal getUsedFundAmount()
    {
        return usedFundAmount;
    }

    public void setUsedFundAmount(BigDecimal usedFundAmount)
    {
        this.usedFundAmount = usedFundAmount;
    }

    public BigDecimal getRemainingFundAmount()
    {
        return remainingFundAmount;
    }

    public void setRemainingFundAmount(BigDecimal remainingFundAmount)
    {
        this.remainingFundAmount = remainingFundAmount;
    }

    public Integer getExpireInDays()
    {
        return expireInDays;
    }

    public void setExpireInDays(Integer expireInDays)
    {
        this.expireInDays = expireInDays;
    }

    public String getSourceSerialNo()
    {
        return sourceSerialNo;
    }

    public void setSourceSerialNo(String sourceSerialNo)
    {
        this.sourceSerialNo = sourceSerialNo;
    }

    public String getSourceStatus()
    {
        return sourceStatus;
    }

    public void setSourceStatus(String sourceStatus)
    {
        this.sourceStatus = sourceStatus;
    }

    public String getSourceMessage()
    {
        return sourceMessage;
    }

    public void setSourceMessage(String sourceMessage)
    {
        this.sourceMessage = sourceMessage;
    }

    public Date getCallbackTime()
    {
        return callbackTime;
    }

    public void setCallbackTime(Date callbackTime)
    {
        this.callbackTime = callbackTime;
    }

    public String getRawPayload()
    {
        return rawPayload;
    }

    public void setRawPayload(String rawPayload)
    {
        this.rawPayload = rawPayload;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("policyId", getPolicyId())
            .append("statMonth", getStatMonth())
            .append("enterpriseId", getEnterpriseId())
            .append("enterpriseName", getEnterpriseName())
            .append("policyNo", getPolicyNo())
            .append("premium", getPremium())
            .append("policyStatus", getPolicyStatus())
            .append("preventionFundAmount", getPreventionFundAmount())
            .append("usedFundAmount", getUsedFundAmount())
            .append("remainingFundAmount", getRemainingFundAmount())
            .toString();
    }
}
