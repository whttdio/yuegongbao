package com.yuegongbao.ygb.worker.domain;

import com.yuegongbao.common.core.domain.BaseEntity;

public class WorkerSetting extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long settingId;

    private Long userId;

    private Long personId;

    private String notifyEnabled;

    private String pushClientId;

    private String notificationPermission;

    private String pushPlatform;

    private String delFlag;

    public Long getSettingId()
    {
        return settingId;
    }

    public void setSettingId(Long settingId)
    {
        this.settingId = settingId;
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

    public String getNotifyEnabled()
    {
        return notifyEnabled;
    }

    public void setNotifyEnabled(String notifyEnabled)
    {
        this.notifyEnabled = notifyEnabled;
    }

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

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }
}
