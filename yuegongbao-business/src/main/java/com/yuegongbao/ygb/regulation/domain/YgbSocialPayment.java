package com.yuegongbao.ygb.regulation.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;

/**
 * 社保缴费监控对象 t_social_payment。
 *
 * @author yuegongbao
 */
public class YgbSocialPayment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "记录ID")
    private Long paymentId;

    @Excel(name = "统计月份")
    private String statMonth;

    private Long enterpriseId;

    @Excel(name = "企业名称")
    private String enterpriseName;

    private Long personId;

    @Excel(name = "人员姓名")
    private String personName;

    @Excel(name = "身份证号")
    private String idCard;

    @Excel(name = "区域编码")
    private String regionCode;

    @Excel(name = "缴费基数")
    private BigDecimal baseAmount;

    @Excel(name = "缴费金额")
    private BigDecimal paidAmount;

    @Excel(name = "缴费状态", readConverterExp = "0=未缴费,1=正常,2=欠费")
    private String paymentStatus;

    private String sourceSerialNo;

    private String sourceStatus;

    private String sourceMessage;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date callbackTime;

    private String rawPayload;

    public Long getPaymentId()
    {
        return paymentId;
    }

    public void setPaymentId(Long paymentId)
    {
        this.paymentId = paymentId;
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
            .append("paymentId", getPaymentId())
            .append("statMonth", getStatMonth())
            .append("enterpriseId", getEnterpriseId())
            .append("enterpriseName", getEnterpriseName())
            .append("personId", getPersonId())
            .append("personName", getPersonName())
            .append("idCard", getIdCard())
            .append("regionCode", getRegionCode())
            .append("baseAmount", getBaseAmount())
            .append("paidAmount", getPaidAmount())
            .append("paymentStatus", getPaymentStatus())
            .toString();
    }
}
