package com.yuegongbao.ygb.warning.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;

/**
 * 预警处置日志对象 t_warning_handle_log。
 *
 * @author yuegongbao
 */
public class YgbWarningHandleLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "日志ID")
    private Long logId;

    private Long warnId;

    @Excel(name = "操作动作")
    private String actionType;

    @Excel(name = "处置意见", width = 30)
    private String opinion;

    private String attachmentUrls;

    @Excel(name = "前置状态")
    private String beforeStatus;

    @Excel(name = "后置状态")
    private String afterStatus;

    @Excel(name = "处理人")
    private String handlerName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "处理时间", width = 22, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date handleTime;

    public Long getLogId()
    {
        return logId;
    }

    public void setLogId(Long logId)
    {
        this.logId = logId;
    }

    public Long getWarnId()
    {
        return warnId;
    }

    public void setWarnId(Long warnId)
    {
        this.warnId = warnId;
    }

    public String getActionType()
    {
        return actionType;
    }

    public void setActionType(String actionType)
    {
        this.actionType = actionType;
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

    public String getBeforeStatus()
    {
        return beforeStatus;
    }

    public void setBeforeStatus(String beforeStatus)
    {
        this.beforeStatus = beforeStatus;
    }

    public String getAfterStatus()
    {
        return afterStatus;
    }

    public void setAfterStatus(String afterStatus)
    {
        this.afterStatus = afterStatus;
    }

    public String getHandlerName()
    {
        return handlerName;
    }

    public void setHandlerName(String handlerName)
    {
        this.handlerName = handlerName;
    }

    public Date getHandleTime()
    {
        return handleTime;
    }

    public void setHandleTime(Date handleTime)
    {
        this.handleTime = handleTime;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("logId", getLogId())
            .append("warnId", getWarnId())
            .append("actionType", getActionType())
            .append("opinion", getOpinion())
            .append("attachmentUrls", getAttachmentUrls())
            .append("beforeStatus", getBeforeStatus())
            .append("afterStatus", getAfterStatus())
            .append("handlerName", getHandlerName())
            .append("handleTime", getHandleTime())
            .toString();
    }
}
