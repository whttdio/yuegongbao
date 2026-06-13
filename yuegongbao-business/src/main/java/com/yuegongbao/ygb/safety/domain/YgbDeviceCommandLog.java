package com.yuegongbao.ygb.safety.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;

/**
 * 设备指令日志对象 t_device_command_log。
 *
 * @author yuegongbao
 */
public class YgbDeviceCommandLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "日志ID")
    private Long logId;

    private Long deviceId;

    @Excel(name = "设备编码")
    private String deviceCode;

    @Excel(name = "指令类型", readConverterExp = "1=锁机,2=解锁,3=授权")
    private String commandType;

    private String commandPayload;

    @Excel(name = "执行结果", readConverterExp = "0=待处理,1=成功,2=失败")
    private String commandResult;

    @Excel(name = "结果说明", width = 30)
    private String resultMessage;

    private String sourceSerialNo;

    private String sourceStatus;

    private String sourceMessage;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date callbackTime;

    private String rawPayload;

    @Excel(name = "操作人")
    private String operatorName;

    public Long getLogId()
    {
        return logId;
    }

    public void setLogId(Long logId)
    {
        this.logId = logId;
    }

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

    public String getCommandType()
    {
        return commandType;
    }

    public void setCommandType(String commandType)
    {
        this.commandType = commandType;
    }

    public String getCommandPayload()
    {
        return commandPayload;
    }

    public void setCommandPayload(String commandPayload)
    {
        this.commandPayload = commandPayload;
    }

    public String getCommandResult()
    {
        return commandResult;
    }

    public void setCommandResult(String commandResult)
    {
        this.commandResult = commandResult;
    }

    public String getResultMessage()
    {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage)
    {
        this.resultMessage = resultMessage;
    }

    public String getSourceSerialNo()
    {
        return sourceSerialNo;
    }

    public void setSourceSerialNo(String sourceSerialNo)
    {
        this.sourceSerialNo = sourceSerialNo;
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

    public String getOperatorName()
    {
        return operatorName;
    }

    public void setOperatorName(String operatorName)
    {
        this.operatorName = operatorName;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("logId", getLogId())
            .append("deviceCode", getDeviceCode())
            .append("commandType", getCommandType())
            .append("commandResult", getCommandResult())
            .append("resultMessage", getResultMessage())
            .toString();
    }
}
