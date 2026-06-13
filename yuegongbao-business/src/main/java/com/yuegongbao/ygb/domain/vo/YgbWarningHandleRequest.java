package com.yuegongbao.ygb.domain.vo;

import jakarta.validation.constraints.NotBlank;

/**
 * 预警处置请求。
 *
 * @author yuegongbao
 */
public class YgbWarningHandleRequest
{
    @NotBlank(message = "处置动作不能为空")
    private String action;

    private String opinion;

    private String attachmentUrls;

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public String getOpinion()
    {
        return opinion;
    }

    public void setOpinion(String opinion)
    {
        this.opinion = opinion;
    }

    public String getAttachmentUrls()
    {
        return attachmentUrls;
    }

    public void setAttachmentUrls(String attachmentUrls)
    {
        this.attachmentUrls = attachmentUrls;
    }
}
