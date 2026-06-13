package com.yuegongbao.ygb.regulation.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;

/**
 * 用工比例监控对象 t_employment_proportion。
 *
 * @author yuegongbao
 */
public class YgbEmploymentRatio extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "记录ID")
    private Long recordId;

    @Excel(name = "统计月份")
    private String statMonth;

    private Long employerEnterpriseId;

    @Excel(name = "用工单位")
    private String employerEnterpriseName;

    @Excel(name = "区域编码")
    private String regionCode;

    @Excel(name = "派遣人数")
    private Integer dispatchCount;

    @Excel(name = "正式工人数")
    private Integer formalCount;

    @Excel(name = "派遣比例")
    private BigDecimal ratioValue;

    @Excel(name = "预警级别", readConverterExp = "0=正常,1=黄警,2=红警")
    private String warningLevel;

    @Excel(name = "预警状态", readConverterExp = "0=未预警,1=已预警")
    private String warningStatus;

    public Long getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    public String getStatMonth()
    {
        return statMonth;
    }

    public void setStatMonth(String statMonth)
    {
        this.statMonth = statMonth;
    }

    public Long getEmployerEnterpriseId()
    {
        return employerEnterpriseId;
    }

    public void setEmployerEnterpriseId(Long employerEnterpriseId)
    {
        this.employerEnterpriseId = employerEnterpriseId;
    }

    public String getEmployerEnterpriseName()
    {
        return employerEnterpriseName;
    }

    public void setEmployerEnterpriseName(String employerEnterpriseName)
    {
        this.employerEnterpriseName = employerEnterpriseName;
    }

    public String getRegionCode()
    {
        return regionCode;
    }

    public void setRegionCode(String regionCode)
    {
        this.regionCode = regionCode;
    }

    public Integer getDispatchCount()
    {
        return dispatchCount;
    }

    public void setDispatchCount(Integer dispatchCount)
    {
        this.dispatchCount = dispatchCount;
    }

    public Integer getFormalCount()
    {
        return formalCount;
    }

    public void setFormalCount(Integer formalCount)
    {
        this.formalCount = formalCount;
    }

    public BigDecimal getRatioValue()
    {
        return ratioValue;
    }

    public void setRatioValue(BigDecimal ratioValue)
    {
        this.ratioValue = ratioValue;
    }

    public String getWarningLevel()
    {
        return warningLevel;
    }

    public void setWarningLevel(String warningLevel)
    {
        this.warningLevel = warningLevel;
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
            .append("recordId", getRecordId())
            .append("statMonth", getStatMonth())
            .append("employerEnterpriseName", getEmployerEnterpriseName())
            .append("dispatchCount", getDispatchCount())
            .append("formalCount", getFormalCount())
            .append("ratioValue", getRatioValue())
            .append("warningLevel", getWarningLevel())
            .toString();
    }
}
