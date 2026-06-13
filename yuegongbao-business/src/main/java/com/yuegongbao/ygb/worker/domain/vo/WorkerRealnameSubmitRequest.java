package com.yuegongbao.ygb.worker.domain.vo;

public class WorkerRealnameSubmitRequest
{
    private String personName;

    private String mobile;

    private String idCard;

    private String idCardFrontUrl;

    private String idCardBackUrl;

    private String selfieUrl;

    private String sourceModule;

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

    public String getIdCard()
    {
        return idCard;
    }

    public void setIdCard(String idCard)
    {
        this.idCard = idCard;
    }

    public String getIdCardFrontUrl()
    {
        return idCardFrontUrl;
    }

    public void setIdCardFrontUrl(String idCardFrontUrl)
    {
        this.idCardFrontUrl = idCardFrontUrl;
    }

    public String getIdCardBackUrl()
    {
        return idCardBackUrl;
    }

    public void setIdCardBackUrl(String idCardBackUrl)
    {
        this.idCardBackUrl = idCardBackUrl;
    }

    public String getSelfieUrl()
    {
        return selfieUrl;
    }

    public void setSelfieUrl(String selfieUrl)
    {
        this.selfieUrl = selfieUrl;
    }

    public String getSourceModule()
    {
        return sourceModule;
    }

    public void setSourceModule(String sourceModule)
    {
        this.sourceModule = sourceModule;
    }
}
