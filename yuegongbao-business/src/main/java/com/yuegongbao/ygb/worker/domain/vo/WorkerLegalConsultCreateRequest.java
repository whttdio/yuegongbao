package com.yuegongbao.ygb.worker.domain.vo;

public class WorkerLegalConsultCreateRequest
{
    private String consultType;

    private String title;

    private String content;

    private String contactMobile;

    private String attachments;

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
}
