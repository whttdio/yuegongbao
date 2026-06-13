package com.yuegongbao.ygb.worker.domain.vo;

public class WorkerMessageHandleRequest
{
    private String status;

    private String replyContent;

    private String handleTimeText;

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
}
