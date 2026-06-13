package com.yuegongbao.ygb.worker.domain;

import com.yuegongbao.common.core.domain.BaseEntity;

public class WorkerComplaint extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long complaintId;

    private Long userId;

    private Long personId;

    private String personName;

    private Long enterpriseId;

    private String enterpriseName;

    private String complaintType;

    private String title;

    private String content;

    private String contactMobile;

    private String anonymousFlag;

    private String syncUnionFlag;

    private String attachments;

    private String status;

    private String replyContent;

    private String handleTimeText;

    private String delFlag;

    public Long getComplaintId()
    {
        return complaintId;
    }

    public void setComplaintId(Long complaintId)
    {
        this.complaintId = complaintId;
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

    public String getComplaintType()
    {
        return complaintType;
    }

    public void setComplaintType(String complaintType)
    {
        this.complaintType = complaintType;
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

    public String getAnonymousFlag()
    {
        return anonymousFlag;
    }

    public void setAnonymousFlag(String anonymousFlag)
    {
        this.anonymousFlag = anonymousFlag;
    }

    public String getSyncUnionFlag()
    {
        return syncUnionFlag;
    }

    public void setSyncUnionFlag(String syncUnionFlag)
    {
        this.syncUnionFlag = syncUnionFlag;
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

    public String getHandleTimeText()
    {
        return handleTimeText;
    }

    public void setHandleTimeText(String handleTimeText)
    {
        this.handleTimeText = handleTimeText;
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
