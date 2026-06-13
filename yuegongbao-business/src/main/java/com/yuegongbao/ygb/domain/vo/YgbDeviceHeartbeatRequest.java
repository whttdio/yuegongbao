package com.yuegongbao.ygb.domain.vo;

/**
 * 设备心跳请求。
 *
 * @author yuegongbao
 */
public class YgbDeviceHeartbeatRequest
{
    private Long deviceId;

    private String deviceCode;

    private String heartbeatStatus;

    public Long getDeviceId()
    {
        return deviceId;
    }

    public void setDeviceId(Long deviceId)
    {
        this.deviceId = deviceId;
    }

    public String getDeviceCode()
    {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode)
    {
        this.deviceCode = deviceCode;
    }

    public String getHeartbeatStatus()
    {
        return heartbeatStatus;
    }

    public void setHeartbeatStatus(String heartbeatStatus)
    {
        this.heartbeatStatus = heartbeatStatus;
    }
}
