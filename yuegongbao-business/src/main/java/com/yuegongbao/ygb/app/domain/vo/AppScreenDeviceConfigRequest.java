package com.yuegongbao.ygb.app.domain.vo;

public class AppScreenDeviceConfigRequest
{
    private String wifiName;

    private String workTime;

    private String faceThreshold;

    private String deviceCode;

    public String getWifiName()
    {
        return wifiName;
    }

    public void setWifiName(String wifiName)
    {
        this.wifiName = wifiName;
    }

    public String getWorkTime()
    {
        return workTime;
    }

    public void setWorkTime(String workTime)
    {
        this.workTime = workTime;
    }

    public String getFaceThreshold()
    {
        return faceThreshold;
    }

    public void setFaceThreshold(String faceThreshold)
    {
        this.faceThreshold = faceThreshold;
    }

    public String getDeviceCode()
    {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode)
    {
        this.deviceCode = deviceCode;
    }
}
