package com.yuegongbao.ygb.worker.domain;

import java.util.Date;
import com.yuegongbao.common.core.domain.BaseEntity;

public class WorkerNoticeMessage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long messageId;

    private Long userId;

    private Long personId;

    private String personName;

    private String messageType;

    private String title;

    private String summary;

    private String content;

    private String bizType;

    private String bizId;

    private String jumpPath;

    private String jumpQueryText;

    private String actionLabel;

    private String sourceLabel;

    private String readFlag;

    private Date readTime;

    private String delFlag;

    public Long getMessageId()
    {
        return messageId;
    }

    public void setMessageId(Long messageId)
    {
        this.messageId = messageId;
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

    public String getMessageType()
    {
        return messageType;
    }

    public void setMessageType(String messageType)
    {
        this.messageType = messageType;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getBizType()
    {
        return bizType;
    }

    public void setBizType(String bizType)
    {
        this.bizType = bizType;
    }

    public String getBizId()
    {
        return bizId;
    }

    public void setBizId(String bizId)
    {
        this.bizId = bizId;
    }

    public String getJumpPath()
    {
        return jumpPath;
    }

    public void setJumpPath(String jumpPath)
    {
        this.jumpPath = jumpPath;
    }

    public String getJumpQueryText()
    {
        return jumpQueryText;
    }

    public void setJumpQueryText(String jumpQueryText)
    {
        this.jumpQueryText = jumpQueryText;
    }

    public String getActionLabel()
    {
        return actionLabel;
    }

    public void setActionLabel(String actionLabel)
    {
        this.actionLabel = actionLabel;
    }

    public String getSourceLabel()
    {
        return sourceLabel;
    }

    public void setSourceLabel(String sourceLabel)
    {
        this.sourceLabel = sourceLabel;
    }

    public String getReadFlag()
    {
        return readFlag;
    }

    public void setReadFlag(String readFlag)
    {
        this.readFlag = readFlag;
    }

    public Date getReadTime()
    {
        return readTime;
    }

    public void setReadTime(Date readTime)
    {
        this.readTime = readTime;
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
