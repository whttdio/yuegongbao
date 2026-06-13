package com.yuegongbao.ygb.aireport.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;

public class YgbAiReportSubscription extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "Subscription ID")
    private Long subscriptionId;

    @Excel(name = "Subscription Name")
    private String subscriptionName;

    @Excel(name = "Report Type")
    private String reportType;

    @Excel(name = "Region Code")
    private String regionCode;

    private String regionName;

    @Excel(name = "Cycle Type")
    private String cycleType;

    @Excel(name = "Receive Type")
    private String receiveType;

    @Excel(name = "Receiver", width = 40)
    private String receiver;

    @Excel(name = "Version Scope")
    private String versionScope;

    @Excel(name = "Status")
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "Last Send Time", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date lastSendTime;

    @Excel(name = "Source Mode")
    private String sourceMode;

    public Long getSubscriptionId()
    {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId)
    {
        this.subscriptionId = subscriptionId;
    }

    public String getSubscriptionName()
    {
        return subscriptionName;
    }

    public void setSubscriptionName(String subscriptionName)
    {
        this.subscriptionName = subscriptionName;
    }

    public String getReportType()
    {
        return reportType;
    }

    public void setReportType(String reportType)
    {
        this.reportType = reportType;
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

    public String getCycleType()
    {
        return cycleType;
    }

    public void setCycleType(String cycleType)
    {
        this.cycleType = cycleType;
    }

    public String getReceiveType()
    {
        return receiveType;
    }

    public void setReceiveType(String receiveType)
    {
        this.receiveType = receiveType;
    }

    public String getReceiver()
    {
        return receiver;
    }

    public void setReceiver(String receiver)
    {
        this.receiver = receiver;
    }

    public String getVersionScope()
    {
        return versionScope;
    }

    public void setVersionScope(String versionScope)
    {
        this.versionScope = versionScope;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getLastSendTime()
    {
        return lastSendTime;
    }

    public void setLastSendTime(Date lastSendTime)
    {
        this.lastSendTime = lastSendTime;
    }

    public String getSourceMode()
    {
        return sourceMode;
    }

    public void setSourceMode(String sourceMode)
    {
        this.sourceMode = sourceMode;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("subscriptionId", getSubscriptionId())
            .append("subscriptionName", getSubscriptionName())
            .append("reportType", getReportType())
            .append("regionCode", getRegionCode())
            .append("cycleType", getCycleType())
            .append("receiveType", getReceiveType())
            .append("receiver", getReceiver())
            .append("versionScope", getVersionScope())
            .append("status", getStatus())
            .append("lastSendTime", getLastSendTime())
            .append("sourceMode", getSourceMode())
            .append("remark", getRemark())
            .toString();
    }
}
