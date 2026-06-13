package com.yuegongbao.ygb.domain.vo;

import jakarta.validation.constraints.NotEmpty;

public class YgbDeviceBatchActionRequest
{
    @NotEmpty(message = "deviceIds cannot be empty")
    private Long[] deviceIds;

    private Long personId;

    private String certNo;

    public Long[] getDeviceIds()
    {
        return deviceIds;
    }

    public void setDeviceIds(Long[] deviceIds)
    {
        this.deviceIds = deviceIds;
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
