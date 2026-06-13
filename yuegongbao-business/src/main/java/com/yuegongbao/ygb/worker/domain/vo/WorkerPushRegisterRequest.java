package com.yuegongbao.ygb.worker.domain.vo;

public class WorkerPushRegisterRequest
{
    private String pushClientId;

    private String notificationPermission;

    private String pushPlatform;

    public String getPushClientId()
    {
        return pushClientId;
    }

    public void setPushClientId(String pushClientId)
    {
        this.pushClientId = pushClientId;
    }

    public String getNotificationPermission()
    {
        return notificationPermission;
    }

    public void setNotificationPermission(String notificationPermission)
    {
        this.notificationPermission = notificationPermission;
    }

    public String getPushPlatform()
    {
        return pushPlatform;
    }

    public void setPushPlatform(String pushPlatform)
    {
        this.pushPlatform = pushPlatform;
    }
}
