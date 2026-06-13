package com.yuegongbao.ygb.occupation.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;

/**
 * 职业病监测对象 t_occupation_monitor。
 *
 * @author yuegongbao
 */
public class YgbOccupationMonitor extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "监测ID")
    private Long monitorId;

    @Excel(name = "统计月份")
    private String statMonth;

    @Excel(name = "区域编码")
    private String regionCode;

    @Excel(name = "区域")
    private String regionName;

    @Excel(name = "行业")
    private String industryType;

    @Excel(name = "企业数量")
    private Integer enterpriseCount;

    @Excel(name = "从业人数")
    private Integer workerCount;

    @Excel(name = "发病人数")
    private Integer caseCount;

    @Excel(name = "高风险企业数")
    private Integer highRiskEnterpriseCount;

    @Excel(name = "千人发病率")
    private BigDecimal incidenceRate;

    @Excel(name = "预警级别", readConverterExp = "0=正常,1=黄色,2=红色")
    private String warningLevel;

    @Excel(name = "预警状态", readConverterExp = "0=未预警,1=已预警")
    private String warningStatus;

    @Excel(name = "来源渠道")
    private String sourceChannel;

    private String sourceSerialNo;

    private String sourceStatus;

    private String sourceMessage;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date callbackTime;

    private String rawPayload;

    public Long getMonitorId()
    {
        return monitorId;
    }

    public void setMonitorId(Long monitorId)
    {
        this.monitorId = monitorId;
    }

    public String getStatMonth()
    {
        return statMonth;
    }

    public void setStatMonth(String statMonth)
    {
        this.statMonth = statMonth;
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

    public String getIndustryType()
    {
        return industryType;
    }

    public void setIndustryType(String industryType)
    {
        this.industryType = industryType;
    }

    public Integer getEnterpriseCount()
    {
        return enterpriseCount;
    }

    public void setEnterpriseCount(Integer enterpriseCount)
    {
        this.enterpriseCount = enterpriseCount;
    }

    public Integer getWorkerCount()
    {
        return workerCount;
    }

    public void setWorkerCount(Integer workerCount)
    {
        this.workerCount = workerCount;
    }

    public Integer getCaseCount()
    {
        return caseCount;
    }

    public void setCaseCount(Integer caseCount)
    {
        this.caseCount = caseCount;
    }

    public Integer getHighRiskEnterpriseCount()
    {
        return highRiskEnterpriseCount;
    }

    public void setHighRiskEnterpriseCount(Integer highRiskEnterpriseCount)
    {
        this.highRiskEnterpriseCount = highRiskEnterpriseCount;
    }

    public BigDecimal getIncidenceRate()
    {
        return incidenceRate;
    }

    public void setIncidenceRate(BigDecimal incidenceRate)
    {
        this.incidenceRate = incidenceRate;
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

    public String getSourceChannel()
    {
        return sourceChannel;
    }

    public void setSourceChannel(String sourceChannel)
    {
        this.sourceChannel = sourceChannel;
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
            .append("monitorId", getMonitorId())
            .append("statMonth", getStatMonth())
            .append("regionCode", getRegionCode())
            .append("industryType", getIndustryType())
            .append("caseCount", getCaseCount())
            .append("incidenceRate", getIncidenceRate())
            .append("warningLevel", getWarningLevel())
            .toString();
    }
}
