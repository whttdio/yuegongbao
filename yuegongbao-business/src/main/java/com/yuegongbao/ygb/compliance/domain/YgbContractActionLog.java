package com.yuegongbao.ygb.compliance.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.core.domain.BaseEntity;

public class YgbContractActionLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long logId;

    private Long contractId;

    private String contractNo;

    private Long templateId;

    private String actionType;

    private String actionStatus;

    private String actionResult;

    private String portalScope;

    private Long warningId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actionTime;

    public Long getLogId()
    {
        return logId;
    }

    public void setLogId(Long logId)
    {
        this.logId = logId;
    }

    public Long getContractId()
    {
        return contractId;
    }

    public void setContractId(Long contractId)
    {
        this.contractId = contractId;
    }

    public String getContractNo()
    {
        return contractNo;
    }

    public void setContractNo(String contractNo)
    {
        this.contractNo = contractNo;
    }

    public Long getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(Long templateId)
    {
        this.templateId = templateId;
    }

    public String getActionType()
    {
        return actionType;
    }

    public void setActionType(String actionType)
    {
        this.actionType = actionType;
    }

    public String getActionStatus()
    {
        return actionStatus;
    }

    public void setActionStatus(String actionStatus)
    {
        this.actionStatus = actionStatus;
    }

    public String getActionResult()
    {
        return actionResult;
    }

    public void setActionResult(String actionResult)
    {
        this.actionResult = actionResult;
    }

    public String getPortalScope()
    {
        return portalScope;
    }

    public void setPortalScope(String portalScope)
    {
        this.portalScope = portalScope;
    }

    public Long getWarningId()
    {
        return warningId;
    }

    public void setWarningId(Long warningId)
    {
        this.warningId = warningId;
    }

    public Date getActionTime()
    {
        return actionTime;
    }

    public void setActionTime(Date actionTime)
    {
        this.actionTime = actionTime;
    }
}
