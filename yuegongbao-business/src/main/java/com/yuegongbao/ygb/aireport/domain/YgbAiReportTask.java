package com.yuegongbao.ygb.aireport.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;

public class YgbAiReportTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "Task ID")
    private Long taskId;

    @Excel(name = "Report ID")
    private Long reportId;

    @Excel(name = "Report Type")
    private String reportType;

    @Excel(name = "Task Type")
    private String taskType;

    @Excel(name = "Task Name")
    private String taskName;

    @Excel(name = "Region Code")
    private String regionCode;

    private String regionName;

    @Excel(name = "Risk Level")
    private String riskLevel;

    @Excel(name = "Report Period")
    private String reportPeriod;

    @Excel(name = "Report Summary", width = 40)
    private String reportSummary;

    @Excel(name = "Suggestion", width = 40)
    private String suggestionText;

    @Excel(name = "Receiver")
    private String receiveUser;

    @Excel(name = "Receive Dept")
    private String receiveDept;

    @Excel(name = "Handle Status")
    private String handleStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "Due Date", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date dueDate;

    @Excel(name = "Feedback", width = 40)
    private String feedbackText;

    @Excel(name = "Source Mode")
    private String sourceMode;

    public Long getTaskId()
    {
        return taskId;
    }

    public void setTaskId(Long taskId)
    {
        this.taskId = taskId;
    }

    public Long getReportId()
    {
        return reportId;
    }

    public void setReportId(Long reportId)
    {
        this.reportId = reportId;
    }

    public String getReportType()
    {
        return reportType;
    }

    public void setReportType(String reportType)
    {
        this.reportType = reportType;
    }

    public String getTaskType()
    {
        return taskType;
    }

    public void setTaskType(String taskType)
    {
        this.taskType = taskType;
    }

    public String getTaskName()
    {
        return taskName;
    }

    public void setTaskName(String taskName)
    {
        this.taskName = taskName;
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

    public String getRiskLevel()
    {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel)
    {
        this.riskLevel = riskLevel;
    }

    public String getReportPeriod()
    {
        return reportPeriod;
    }

    public void setReportPeriod(String reportPeriod)
    {
        this.reportPeriod = reportPeriod;
    }

    public String getReportSummary()
    {
        return reportSummary;
    }

    public void setReportSummary(String reportSummary)
    {
        this.reportSummary = reportSummary;
    }

    public String getSuggestionText()
    {
        return suggestionText;
    }

    public void setSuggestionText(String suggestionText)
    {
        this.suggestionText = suggestionText;
    }

    public String getReceiveUser()
    {
        return receiveUser;
    }

    public void setReceiveUser(String receiveUser)
    {
        this.receiveUser = receiveUser;
    }

    public String getReceiveDept()
    {
        return receiveDept;
    }

    public void setReceiveDept(String receiveDept)
    {
        this.receiveDept = receiveDept;
    }

    public String getHandleStatus()
    {
        return handleStatus;
    }

    public void setHandleStatus(String handleStatus)
    {
        this.handleStatus = handleStatus;
    }

    public Date getDueDate()
    {
        return dueDate;
    }

    public void setDueDate(Date dueDate)
    {
        this.dueDate = dueDate;
    }

    public String getFeedbackText()
    {
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText)
    {
        this.feedbackText = feedbackText;
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
            .append("taskId", getTaskId())
            .append("reportId", getReportId())
            .append("reportType", getReportType())
            .append("taskType", getTaskType())
            .append("taskName", getTaskName())
            .append("regionCode", getRegionCode())
            .append("riskLevel", getRiskLevel())
            .append("reportPeriod", getReportPeriod())
            .append("reportSummary", getReportSummary())
            .append("suggestionText", getSuggestionText())
            .append("receiveUser", getReceiveUser())
            .append("receiveDept", getReceiveDept())
            .append("handleStatus", getHandleStatus())
            .append("dueDate", getDueDate())
            .append("feedbackText", getFeedbackText())
            .append("sourceMode", getSourceMode())
            .append("remark", getRemark())
            .toString();
    }
}
