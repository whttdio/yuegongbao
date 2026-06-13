package com.yuegongbao.ygb.regulation.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;

/**
 * 个税比对对象 t_tax_compare。
 *
 * @author yuegongbao
 */
public class YgbTaxCompare extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "比对ID")
    private Long compareId;

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

    @Excel(name = "工资实发")
    private BigDecimal salaryAmount;

    @Excel(name = "申报收入")
    private BigDecimal declaredAmount;

    @Excel(name = "差异率")
    private BigDecimal diffRatio;

    @Excel(name = "比对结果", readConverterExp = "1=正常,2=异常")
    private String compareResult;

    @Excel(name = "预警状态", readConverterExp = "0=未预警,1=已预警")
    private String warningStatus;

    private String sourceSerialNo;

    private String sourceStatus;

    private String sourceMessage;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date callbackTime;

    private String rawPayload;

    public Long getCompareId()
    {
        return compareId;
    }

    public void setCompareId(Long compareId)
    {
        this.compareId = compareId;
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

    public BigDecimal getSalaryAmount()
    {
        return salaryAmount;
    }

    public void setSalaryAmount(BigDecimal salaryAmount)
    {
        this.salaryAmount = salaryAmount;
    }

    public BigDecimal getDeclaredAmount()
    {
        return declaredAmount;
    }

    public void setDeclaredAmount(BigDecimal declaredAmount)
    {
        this.declaredAmount = declaredAmount;
    }

    public BigDecimal getDiffRatio()
    {
        return diffRatio;
    }

    public void setDiffRatio(BigDecimal diffRatio)
    {
        this.diffRatio = diffRatio;
    }

    public String getCompareResult()
    {
        return compareResult;
    }

    public void setCompareResult(String compareResult)
    {
        this.compareResult = compareResult;
    }

    public String getWarningStatus()
    {
        return warningStatus;
    }

    public void setWarningStatus(String warningStatus)
    {
        this.warningStatus = warningStatus;
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
            .append("compareId", getCompareId())
            .append("statMonth", getStatMonth())
            .append("enterpriseName", getEnterpriseName())
            .append("personName", getPersonName())
            .append("salaryAmount", getSalaryAmount())
            .append("declaredAmount", getDeclaredAmount())
            .append("diffRatio", getDiffRatio())
            .append("compareResult", getCompareResult())
            .toString();
    }
}
