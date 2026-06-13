package com.yuegongbao.ygb.worker.domain;

import com.yuegongbao.common.core.domain.BaseEntity;

public class WorkerLegalConsult extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long consultId;

    private Long userId;

    private Long personId;

    private String personName;

    private Long enterpriseId;

    private String enterpriseName;

    private String consultType;

    private String title;

    private String content;

    private String contactMobile;

    private String attachments;

    private String status;

    private String replyContent;

    private String replyTimeText;

    private String delFlag;

    public Long getConsultId()
    {
        return consultId;
    }

    public void setConsultId(Long consultId)
    {
        this.consultId = consultId;
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

    public String getConsultType()
    {
        return consultType;
    }

    public void setConsultType(String consultType)
    {
        this.consultType = consultType;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContactMobile()
    {
        return contactMobile;
    }

    public void setContactMobile(String contactMobile)
    {
        this.contactMobile = contactMobile;
    }

    public String getAttachments()
    {
        return attachments;
    }

    public void setAttachments(String attachments)
    {
        this.attachments = attachments;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getReplyContent()
    {
        return replyContent;
    }

    public void setReplyContent(String replyContent)
    {
        this.replyContent = replyContent;
    }

    public String getReplyTimeText()
    {
        return replyTimeText;
    }

    public void setReplyTimeText(String replyTimeText)
    {
        this.replyTimeText = replyTimeText;
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
