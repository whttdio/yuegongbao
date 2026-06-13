package com.yuegongbao.ygb.worker.domain.vo;

public class WorkerPushTestRequest
{
    private String title;

    private String content;

    private String jumpPath;

    private Object jumpQuery;

    private String actionLabel;

    private String sourceLabel;

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

    public String getJumpPath()
    {
        return jumpPath;
    }

    public void setJumpPath(String jumpPath)
    {
        this.jumpPath = jumpPath;
    }

    public Object getJumpQuery()
    {
        return jumpQuery;
    }

    public void setJumpQuery(Object jumpQuery)
    {
        this.jumpQuery = jumpQuery;
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
}
