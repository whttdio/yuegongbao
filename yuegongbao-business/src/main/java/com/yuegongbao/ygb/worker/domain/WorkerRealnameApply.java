package com.yuegongbao.ygb.worker.domain;

import com.yuegongbao.common.core.domain.BaseEntity;

public class WorkerRealnameApply extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long applyId;

    private Long userId;

    private Long personId;

    private String personName;

    private String mobile;

    private String idCard;

    private String applyStatus;

    private String rejectReason;

    private String idCardFrontUrl;

    private String idCardBackUrl;

    private String selfieUrl;

    private String sourceModule;

    private String delFlag;

    public Long getApplyId()
    {
        return applyId;
    }

    public void setApplyId(Long applyId)
    {
        this.applyId = applyId;
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

    public String getIdCard()
    {
        return idCard;
    }

    public void setIdCard(String idCard)
    {
        this.idCard = idCard;
    }

    public String getApplyStatus()
    {
        return applyStatus;
    }

    public void setApplyStatus(String applyStatus)
    {
        this.applyStatus = applyStatus;
    }

    public String getRejectReason()
    {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason)
    {
        this.rejectReason = rejectReason;
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

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }
}
