package com.yuegongbao.ygb.worker.domain;

import com.yuegongbao.common.core.domain.BaseEntity;

public class WorkerPushTestRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long recordId;

    private Long userId;

    private Long personId;

    private String personName;

    private String traceId;

    private String pushClientIdMasked;

    private String pushPlatform;

    private String notificationPermission;

    private String notifyEnabled;

    private String gatewayProvider;

    private String gatewayUrl;

    private String targetPath;

    private String targetQueryText;

    private String actionLabel;

    private String sourceLabel;

    private String requestBody;

    private String responseBody;

    private String testStatus;

    private String statusMessage;

    private String delFlag;

    public Long getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
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

    public String getTraceId()
    {
        return traceId;
    }

    public void setTraceId(String traceId)
    {
        this.traceId = traceId;
    }

    public String getPushClientIdMasked()
    {
        return pushClientIdMasked;
    }

    public void setPushClientIdMasked(String pushClientIdMasked)
    {
        this.pushClientIdMasked = pushClientIdMasked;
    }

    public String getPushPlatform()
    {
        return pushPlatform;
    }

    public void setPushPlatform(String pushPlatform)
    {
        this.pushPlatform = pushPlatform;
    }

    public String getNotificationPermission()
    {
        return notificationPermission;
    }

    public void setNotificationPermission(String notificationPermission)
    {
        this.notificationPermission = notificationPermission;
    }

    public String getNotifyEnabled()
    {
        return notifyEnabled;
    }

    public void setNotifyEnabled(String notifyEnabled)
    {
        this.notifyEnabled = notifyEnabled;
    }

    public String getGatewayProvider()
    {
        return gatewayProvider;
    }

    public void setGatewayProvider(String gatewayProvider)
    {
        this.gatewayProvider = gatewayProvider;
    }

    public String getGatewayUrl()
    {
        return gatewayUrl;
    }

    public void setGatewayUrl(String gatewayUrl)
    {
        this.gatewayUrl = gatewayUrl;
    }

    public String getTargetPath()
    {
        return targetPath;
    }

    public void setTargetPath(String targetPath)
    {
        this.targetPath = targetPath;
    }

    public String getTargetQueryText()
    {
        return targetQueryText;
    }

    public void setTargetQueryText(String targetQueryText)
    {
        this.targetQueryText = targetQueryText;
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

    public String getRequestBody()
    {
        return requestBody;
    }

    public void setRequestBody(String requestBody)
    {
        this.requestBody = requestBody;
    }

    public String getResponseBody()
    {
        return responseBody;
    }

    public void setResponseBody(String responseBody)
    {
        this.responseBody = responseBody;
    }

    public String getTestStatus()
    {
        return testStatus;
    }

    public void setTestStatus(String testStatus)
    {
        this.testStatus = testStatus;
    }

    public String getStatusMessage()
    {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage)
    {
        this.statusMessage = statusMessage;
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
