package com.yuegongbao.ygb.regulation.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;

/**
 * 漏保清单对象 t_uninsured_list。
 *
 * @author yuegongbao
 */
public class YgbUninsuredList extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "清单ID")
    private Long listId;

    @Excel(name = "批次号")
    private String batchNo;

    @Excel(name = "统计月份")
    private String statMonth;

    @Excel(name = "清单类型", readConverterExp = "1=应参未参,2=欠费超三个月,3=在建工程漏保")
    private String listType;

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

    @Excel(name = "工资金额")
    private BigDecimal salaryAmount;

    @Excel(name = "识别原因", width = 30)
    private String detectedReason;

    @Excel(name = "处置状态", readConverterExp = "0=待核查,1=核查中,2=已催缴,3=已补缴,4=强制执行,5=误报")
    private String disposalStatus;

    @Excel(name = "预警状态", readConverterExp = "0=未预警,1=已预警")
    private String warningStatus;

    public Long getListId()
    {
        return listId;
    }

    public void setListId(Long listId)
    {
        this.listId = listId;
    }

    public String getBatchNo()
    {
        return batchNo;
    }

    public void setBatchNo(String batchNo)
    {
        this.batchNo = batchNo;
    }

    public String getStatMonth()
    {
        return statMonth;
    }

    public void setStatMonth(String statMonth)
    {
        this.statMonth = statMonth;
    }

    public String getListType()
    {
        return listType;
    }

    public void setListType(String listType)
    {
        this.listType = listType;
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

    public String getDetectedReason()
    {
        return detectedReason;
    }

    public void setDetectedReason(String detectedReason)
    {
        this.detectedReason = detectedReason;
    }

    public String getDisposalStatus()
    {
        return disposalStatus;
    }

    public void setDisposalStatus(String disposalStatus)
    {
        this.disposalStatus = disposalStatus;
    }

    public String getWarningStatus()
    {
        return warningStatus;
    }

    public void setWarningStatus(String warningStatus)
    {
        this.warningStatus = warningStatus;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("listId", getListId())
            .append("batchNo", getBatchNo())
            .append("statMonth", getStatMonth())
            .append("enterpriseName", getEnterpriseName())
            .append("personName", getPersonName())
            .append("detectedReason", getDetectedReason())
            .append("disposalStatus", getDisposalStatus())
            .toString();
    }
}
