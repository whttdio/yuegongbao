package com.yuegongbao.ygb.worker.domain.vo;

public class WorkerComplaintCreateRequest
{
    private String complaintType;

    private String title;

    private String content;

    private String contactMobile;

    private Boolean anonymous;

    private Boolean syncUnion;

    private String attachments;

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

    public Boolean getAnonymous()
    {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous)
    {
        this.anonymous = anonymous;
    }

    public Boolean getSyncUnion()
    {
        return syncUnion;
    }

    public void setSyncUnion(Boolean syncUnion)
    {
        this.syncUnion = syncUnion;
    }

    public String getAttachments()
    {
        return attachments;
    }

    public void setAttachments(String attachments)
    {
        this.attachments = attachments;
    }
}
