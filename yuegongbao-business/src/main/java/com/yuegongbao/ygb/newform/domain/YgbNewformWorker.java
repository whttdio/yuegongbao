package com.yuegongbao.ygb.newform.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;

/**
 * 新业态人员库对象 t_newform_worker。
 *
 * @author yuegongbao
 */
public class YgbNewformWorker extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "记录ID")
    private Long workerRecordId;

    @Excel(name = "统计月份")
    private String statMonth;

    private Long enterpriseId;

    @Excel(name = "企业名称")
    private String enterpriseName;

    private Long personId;

    @Excel(name = "姓名")
    private String personName;

    @Excel(name = "身份证号")
    private String idCard;

    @Excel(name = "区域编码")
    private String regionCode;

    @Excel(name = "区域")
    private String regionName;

    @Excel(name = "平台企业")
    private String platformName;

    @Excel(name = "从业类型")
    private String employmentType;

    @Excel(name = "参保状态", readConverterExp = "0=未参保,1=已参保,2=停保")
    private String insuranceStatus;

    @Excel(name = "职业伤害参保", readConverterExp = "0=未参保,1=已参保,2=停保")
    private String injuryInsuranceStatus;

    @Excel(name = "月收入")
    private BigDecimal monthlyIncome;

    @Excel(name = "预警状态", readConverterExp = "0=未预警,1=已预警")
    private String warningStatus;

    private String sourceSerialNo;

    private String sourceStatus;

    private String sourceMessage;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date callbackTime;

    private String rawPayload;

    public Long getWorkerRecordId()
    {
        return workerRecordId;
    }

    public void setWorkerRecordId(Long workerRecordId)
    {
        this.workerRecordId = workerRecordId;
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

    public String getRegionName()
    {
        return regionName;
    }

    public void setRegionName(String regionName)
    {
        this.regionName = regionName;
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
            .append("workerRecordId", getWorkerRecordId())
            .append("statMonth", getStatMonth())
            .append("enterpriseName", getEnterpriseName())
            .append("personName", getPersonName())
            .append("platformName", getPlatformName())
            .append("employmentType", getEmploymentType())
            .append("injuryInsuranceStatus", getInjuryInsuranceStatus())
            .append("monthlyIncome", getMonthlyIncome())
            .toString();
    }
}
