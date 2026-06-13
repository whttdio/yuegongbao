package com.yuegongbao.ygb.domain.vo;

/**
 * 设备 AI 事件请求。
 *
 * @author yuegongbao
 */
public class YgbDeviceAiEventRequest
{
    private Long deviceId;

    private String deviceCode;

    private String eventCode;

    private String eventContent;

    private String evidenceUrl;

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

    public String getEventCode()
    {
        return eventCode;
    }

    public void setEventCode(String eventCode)
    {
        this.eventCode = eventCode;
    }

    public String getEventContent()
    {
        return eventContent;
    }

    public void setEventContent(String eventContent)
    {
        this.eventContent = eventContent;
    }

    public String getEvidenceUrl()
    {
        return evidenceUrl;
    }

    public void setEvidenceUrl(String evidenceUrl)
    {
        this.evidenceUrl = evidenceUrl;
    }
}
