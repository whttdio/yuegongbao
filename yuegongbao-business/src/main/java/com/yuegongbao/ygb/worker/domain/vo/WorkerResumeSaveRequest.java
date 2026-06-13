package com.yuegongbao.ygb.worker.domain.vo;

public class WorkerResumeSaveRequest
{
    private String expectedJob;

    private String expectedCity;

    private String expectedSalary;

    private String skillTags;

    private String certificateText;

    private String intro;

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
}
