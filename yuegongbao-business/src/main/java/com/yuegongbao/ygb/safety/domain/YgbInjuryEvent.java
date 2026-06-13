package com.yuegongbao.ygb.safety.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;
import jakarta.validation.constraints.NotNull;

/**
 * 工伤事件对象 t_injury_event。
 *
 * @author yuegongbao
 */
public class YgbInjuryEvent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "事件ID")
    private Long eventId;

    @NotNull(message = "人员不能为空")
    private Long personId;

    @Excel(name = "人员姓名")
    private String personName;

    @NotNull(message = "企业不能为空")
    private Long enterpriseId;

    @Excel(name = "企业名称")
    private String enterpriseName;

    @Excel(name = "区域编码")
    private String regionCode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "事故日期", width = 18, dateFormat = "yyyy-MM-dd")
    private Date eventDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "上报时间", width = 22, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date reportTime;

    @Excel(name = "事故地点", width = 30)
    private String injuryLocation;

    @Excel(name = "受伤部位")
    private String injuryPart;

    private String diagnosisUrl;

    @Excel(name = "事件状态", readConverterExp = "0=已报告,1=认定中,2=已认定,3=待遇申领中,4=已完结")
    private String injuryStatus;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审批截止日", width = 18, dateFormat = "yyyy-MM-dd")
    private Date approvalDeadline;

    @Excel(name = "剩余天数")
    private Integer remainingDays;

    @Excel(name = "审批结果", width = 24)
    private String approvalResult;

    @Excel(name = "预警状态", readConverterExp = "0=未预警,1=已预警")
    private String warningStatus;

    public Long getEventId()
    {
        return eventId;
    }

    public void setEventId(Long eventId)
    {
        this.eventId = eventId;
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

    public Date getEventDate()
    {
        return eventDate;
    }

    public void setEventDate(Date eventDate)
    {
        this.eventDate = eventDate;
    }

    public Date getReportTime()
    {
        return reportTime;
    }

    public void setReportTime(Date reportTime)
    {
        this.reportTime = reportTime;
    }

    public String getInjuryLocation()
    {
        return injuryLocation;
    }

    public void setInjuryLocation(String injuryLocation)
    {
        this.injuryLocation = injuryLocation;
    }

    public String getInjuryPart()
    {
        return injuryPart;
    }

    public void setInjuryPart(String injuryPart)
    {
        this.injuryPart = injuryPart;
    }

    public String getDiagnosisUrl()
    {
        return diagnosisUrl;
    }

    public void setDiagnosisUrl(String diagnosisUrl)
    {
        this.diagnosisUrl = diagnosisUrl;
    }

    public String getInjuryStatus()
    {
        return injuryStatus;
    }

    public void setInjuryStatus(String injuryStatus)
    {
        this.injuryStatus = injuryStatus;
    }

    public Date getApprovalDeadline()
    {
        return approvalDeadline;
    }

    public void setApprovalDeadline(Date approvalDeadline)
    {
        this.approvalDeadline = approvalDeadline;
    }

    public Integer getRemainingDays()
    {
        return remainingDays;
    }

    public void setRemainingDays(Integer remainingDays)
    {
        this.remainingDays = remainingDays;
    }

    public String getApprovalResult()
    {
        return approvalResult;
    }

    public void setApprovalResult(String approvalResult)
    {
        this.approvalResult = approvalResult;
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
            .append("eventId", getEventId())
            .append("personName", getPersonName())
            .append("enterpriseName", getEnterpriseName())
            .append("injuryStatus", getInjuryStatus())
            .append("remainingDays", getRemainingDays())
            .toString();
    }
}
