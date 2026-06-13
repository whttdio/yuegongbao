package com.yuegongbao.ygb.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 外部 Stub 统一返回。
 *
 * @author yuegongbao
 */
public class YgbStubResponse
{
    private String externalSerialNo;

    private String sourceStatus;

    private String sourceMessage;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date callbackTime;

    private String rawPayload;

    public String getExternalSerialNo()
    {
        return externalSerialNo;
    }

    public void setExternalSerialNo(String externalSerialNo)
    {
        this.externalSerialNo = externalSerialNo;
    }

    public String getSourceStatus()
    {
        return sourceStatus;
    }

    public void setSourceStatus(String sourceStatus)
    {
        this.sourceStatus = sourceStatus;
    }

    public String getSourceMessage()
    {
        return sourceMessage;
    }

    public void setSourceMessage(String sourceMessage)
    {
        this.sourceMessage = sourceMessage;
    }

    public Date getCallbackTime()
    {
        return callbackTime;
    }

    public void setCallbackTime(Date callbackTime)
    {
        this.callbackTime = callbackTime;
    }

    public String getRawPayload()
    {
        return rawPayload;
    }

    public void setRawPayload(String rawPayload)
    {
        this.rawPayload = rawPayload;
    }
}
