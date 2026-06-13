package com.yuegongbao.ygb.worker.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.core.domain.BaseEntity;

public class WorkerJobPost extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long jobId;

    private Long enterpriseId;

    private String enterpriseName;

    private String title;

    private String jobType;

    private String workAddress;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;

    private String salaryText;

    private Integer recruitCount;

    private String contactName;

    private String contactMobile;

    private String description;

    private String requirementText;

    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;

    public Long getJobId()
    {
        return jobId;
    }

    public void setJobId(Long jobId)
    {
        this.jobId = jobId;
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

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getJobType()
    {
        return jobType;
    }

    public void setJobType(String jobType)
    {
        this.jobType = jobType;
    }

    public String getWorkAddress()
    {
        return workAddress;
    }

    public void setWorkAddress(String workAddress)
    {
        this.workAddress = workAddress;
    }

    public BigDecimal getLongitude()
    {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude)
    {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude()
    {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude)
    {
        this.latitude = latitude;
    }

    public BigDecimal getSalaryMin()
    {
        return salaryMin;
    }

    public void setSalaryMin(BigDecimal salaryMin)
    {
        this.salaryMin = salaryMin;
    }

    public BigDecimal getSalaryMax()
    {
        return salaryMax;
    }

    public void setSalaryMax(BigDecimal salaryMax)
    {
        this.salaryMax = salaryMax;
    }

    public String getSalaryText()
    {
        return salaryText;
    }

    public void setSalaryText(String salaryText)
    {
        this.salaryText = salaryText;
    }

    public Integer getRecruitCount()
    {
        return recruitCount;
    }

    public void setRecruitCount(Integer recruitCount)
    {
        this.recruitCount = recruitCount;
    }

    public String getContactName()
    {
        return contactName;
    }

    public void setContactName(String contactName)
    {
        this.contactName = contactName;
    }

    public String getContactMobile()
    {
        return contactMobile;
    }

    public void setContactMobile(String contactMobile)
    {
        this.contactMobile = contactMobile;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getRequirementText()
    {
        return requirementText;
    }

    public void setRequirementText(String requirementText)
    {
        this.requirementText = requirementText;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getPublishTime()
    {
        return publishTime;
    }

    public void setPublishTime(Date publishTime)
    {
        this.publishTime = publishTime;
    }
}
