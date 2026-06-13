package com.yuegongbao.ygb.safety.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 设备台账对象 t_device。
 *
 * @author yuegongbao
 */
public class YgbDevice extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "设备ID")
    private Long deviceId;

    @Excel(name = "设备编码")
    private String deviceCode;

    @Excel(name = "设备名称")
    private String deviceName;

    @Excel(name = "设备类型", readConverterExp = "1=考勤机,2=芯片设备,3=AI摄像头")
    private String deviceType;

    @NotNull(message = "所属企业不能为空")
    private Long enterpriseId;

    @Excel(name = "所属企业")
    private String enterpriseName;

    @Excel(name = "区域编码")
    private String regionCode;

    @Excel(name = "芯片ID")
    private String chipId;

    @Excel(name = "物联卡号")
    private String simCardNo;

    @Excel(name = "设备状态", readConverterExp = "0=离线,1=在线,2=锁定,3=故障")
    private String deviceStatus;

    @Excel(name = "授权状态", readConverterExp = "0=未授权,1=授权通过,2=授权拒绝")
    private String authStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最后心跳", width = 22, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date lastHeartbeat;

    @Excel(name = "安装位置", width = 30)
    private String installLocation;

    @Excel(name = "固件版本")
    private String firmwareVersion;

    public Long getDeviceId()
    {
        return deviceId;
    }

    public void setDeviceId(Long deviceId)
    {
        this.deviceId = deviceId;
    }

    @NotBlank(message = "设备编码不能为空")
    public String getDeviceCode()
    {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode)
    {
        this.deviceCode = deviceCode;
    }

    public String getDeviceName()
    {
        return deviceName;
    }

    public void setDeviceName(String deviceName)
    {
        this.deviceName = deviceName;
    }

    public String getDeviceType()
    {
        return deviceType;
    }

    public void setDeviceType(String deviceType)
    {
        this.deviceType = deviceType;
    }

    public Long getEnterpriseId()
    {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }

    public String getEnterpriseName()
    {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName)
    {
        this.enterpriseName = enterpriseName;
    }

    public String getRegionCode()
    {
        return regionCode;
    }

    public void setRegionCode(String regionCode)
    {
        this.regionCode = regionCode;
    }

    public String getChipId()
    {
        return chipId;
    }

    public void setChipId(String chipId)
    {
        this.chipId = chipId;
    }

    public String getSimCardNo()
    {
        return simCardNo;
    }

    public void setSimCardNo(String simCardNo)
    {
        this.simCardNo = simCardNo;
    }

    public String getDeviceStatus()
    {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus)
    {
        this.deviceStatus = deviceStatus;
    }

    public String getAuthStatus()
    {
        return authStatus;
    }

    public void setAuthStatus(String authStatus)
    {
        this.authStatus = authStatus;
    }

    public Date getLastHeartbeat()
    {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(Date lastHeartbeat)
    {
        this.lastHeartbeat = lastHeartbeat;
    }

    public String getInstallLocation()
    {
        return installLocation;
    }

    public void setInstallLocation(String installLocation)
    {
        this.installLocation = installLocation;
    }

    public String getFirmwareVersion()
    {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion)
    {
        this.firmwareVersion = firmwareVersion;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("deviceId", getDeviceId())
            .append("deviceCode", getDeviceCode())
            .append("deviceName", getDeviceName())
            .append("deviceType", getDeviceType())
            .append("enterpriseName", getEnterpriseName())
            .append("deviceStatus", getDeviceStatus())
            .append("authStatus", getAuthStatus())
            .toString();
    }
}
