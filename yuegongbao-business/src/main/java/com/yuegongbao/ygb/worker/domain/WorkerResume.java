package com.yuegongbao.ygb.worker.domain;

import com.yuegongbao.common.core.domain.BaseEntity;

public class WorkerResume extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long resumeId;

    private Long userId;

    private Long personId;

    private String personName;

    private String mobile;

    private String jobType;

    private String expectedJob;

    private String expectedCity;

    private String expectedSalary;

    private String skillTags;

    private String certificateText;

    private String intro;

    private String delFlag;

    public Long getResumeId()
    {
        return resumeId;
    }

    public void setResumeId(Long resumeId)
    {
        this.resumeId = resumeId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
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

    public String getMobile()
    {
        return mobile;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public String getJobType()
    {
        return jobType;
    }

    public void setJobType(String jobType)
    {
        this.jobType = jobType;
    }

    public String getExpectedJob()
    {
        return expectedJob;
    }

    public void setExpectedJob(String expectedJob)
    {
        this.expectedJob = expectedJob;
    }

    public String getExpectedCity()
    {
        return expectedCity;
    }

    public void setExpectedCity(String expectedCity)
    {
        this.expectedCity = expectedCity;
    }

    public String getExpectedSalary()
    {
        return expectedSalary;
    }

    public void setExpectedSalary(String expectedSalary)
    {
        this.expectedSalary = expectedSalary;
    }

    public String getSkillTags()
    {
        return skillTags;
    }

    public void setSkillTags(String skillTags)
    {
        this.skillTags = skillTags;
    }

    public String getCertificateText()
    {
        return certificateText;
    }

    public void setCertificateText(String certificateText)
    {
        this.certificateText = certificateText;
    }

    public String getIntro()
    {
        return intro;
    }

    public void setIntro(String intro)
    {
        this.intro = intro;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }
}
