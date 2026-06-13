package com.yuegongbao.ygb.domain.vo;

import jakarta.validation.constraints.NotNull;

/**
 * 设备授权请求。
 *
 * @author yuegongbao
 */
public class YgbDeviceAuthorizeRequest
{
    @NotNull(message = "设备不能为空")
    private Long deviceId;

    @NotNull(message = "人员不能为空")
    private Long personId;

    private String certNo;

    public Long getDeviceId()
    {
        return deviceId;
    }

    public void setDeviceId(Long deviceId)
    {
        this.deviceId = deviceId;
    }

    public Long getPersonId()
    {
        return personId;
    }

    public void setPersonId(Long personId)
    {
        this.personId = personId;
    }

    public String getCertNo()
    {
        return certNo;
    }

    public void setCertNo(String certNo)
    {
        this.certNo = certNo;
    }
}
