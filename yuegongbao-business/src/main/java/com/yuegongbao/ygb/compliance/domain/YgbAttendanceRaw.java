package com.yuegongbao.ygb.compliance.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 考勤上报对象 t_attendance_raw
 *
 * @author yuegongbao
 */
public class YgbAttendanceRaw extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "考勤ID")
    private Long attendanceId;

    @Excel(name = "考勤编号")
    private String attendanceNo;

    @NotNull(message = "关联合同不能为空")
    private Long contractId;

    @Excel(name = "合同编号")
    private String contractNo;

    private Long dispatchEnterpriseId;

    @Excel(name = "派遣单位")
    private String dispatchEnterpriseName;

    private Long employerEnterpriseId;

    @Excel(name = "用工单位")
    private String employerEnterpriseName;

    private Long personId;

    @Excel(name = "劳动者")
    private String personName;

    @Excel(name = "身份证号")
    private String idCard;

    @Excel(name = "区域编码")
    private String regionCode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "考勤日期", width = 18, dateFormat = "yyyy-MM-dd")
    private Date attendanceDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "上班打卡", width = 22, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date clockInTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "下班打卡", width = 22, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date clockOutTime;

    @Excel(name = "班次")
    private String shiftName;

    @Excel(name = "出勤工时")
    private BigDecimal attendanceHours;

    @Excel(name = "加班工时")
    private BigDecimal overtimeHours;

    @Excel(name = "考勤状态", readConverterExp = "0=缺勤,1=正常,2=迟到,3=早退,4=加班,5=异常")
    private String attendanceStatus;

    @Excel(name = "上报来源", readConverterExp = "1=闸机,2=人脸,3=APP,4=补录,5=第三方")
    private String sourceType;

    @Excel(name = "归集状态", readConverterExp = "0=未归集,1=已归集")
    private String collectStatus;

    @Excel(name = "工资核验", readConverterExp = "0=不通过,1=通过")
    private String attCheck;

    @Excel(name = "设备编码")
    private String deviceCode;

    @Excel(name = "异常说明", width = 28)
    private String anomalyRemark;

    public Long getAttendanceId()
    {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId)
    {
        this.attendanceId = attendanceId;
    }

    @NotBlank(message = "考勤编号不能为空")
    @Size(max = 64, message = "考勤编号长度不能超过64个字符")
    public String getAttendanceNo()
    {
        return attendanceNo;
    }

    public void setAttendanceNo(String attendanceNo)
    {
        this.attendanceNo = attendanceNo;
    }

    public Long getContractId()
    {
        return contractId;
    }

    public void setContractId(Long contractId)
    {
        this.contractId = contractId;
    }

    public String getContractNo()
    {
        return contractNo;
    }

    public void setContractNo(String contractNo)
    {
        this.contractNo = contractNo;
    }

    public Long getDispatchEnterpriseId()
    {
        return dispatchEnterpriseId;
    }

    public void setDispatchEnterpriseId(Long dispatchEnterpriseId)
    {
        this.dispatchEnterpriseId = dispatchEnterpriseId;
    }

    public String getDispatchEnterpriseName()
    {
        return dispatchEnterpriseName;
    }

    public void setDispatchEnterpriseName(String dispatchEnterpriseName)
    {
        this.dispatchEnterpriseName = dispatchEnterpriseName;
    }

    public Long getEmployerEnterpriseId()
    {
        return employerEnterpriseId;
    }

    public void setEmployerEnterpriseId(Long employerEnterpriseId)
    {
        this.employerEnterpriseId = employerEnterpriseId;
    }

    public String getEmployerEnterpriseName()
    {
        return employerEnterpriseName;
    }

    public void setEmployerEnterpriseName(String employerEnterpriseName)
    {
        this.employerEnterpriseName = employerEnterpriseName;
    }

    public Long getPersonId()
    {
        return personId;
    }

    public void setPersonId(Long personId)
    {
        this.personId = personId;
    }

    public String getPersonName()
    {
        return personName;
    }

    public void setPersonName(String personName)
    {
        this.personName = personName;
    }

    public String getIdCard()
    {
        return idCard;
    }

    public void setIdCard(String idCard)
    {
        this.idCard = idCard;
    }

    public String getRegionCode()
    {
        return regionCode;
    }

    public void setRegionCode(String regionCode)
    {
        this.regionCode = regionCode;
    }

    @NotNull(message = "考勤日期不能为空")
    public Date getAttendanceDate()
    {
        return attendanceDate;
    }

    public void setAttendanceDate(Date attendanceDate)
    {
        this.attendanceDate = attendanceDate;
    }

    public Date getClockInTime()
    {
        return clockInTime;
    }

    public void setClockInTime(Date clockInTime)
    {
        this.clockInTime = clockInTime;
    }

    public Date getClockOutTime()
    {
        return clockOutTime;
    }

    public void setClockOutTime(Date clockOutTime)
    {
        this.clockOutTime = clockOutTime;
    }

    @Size(max = 64, message = "班次名称长度不能超过64个字符")
    public String getShiftName()
    {
        return shiftName;
    }

    public void setShiftName(String shiftName)
    {
        this.shiftName = shiftName;
    }

    @DecimalMin(value = "0", message = "出勤工时不能小于0")
    public BigDecimal getAttendanceHours()
    {
        return attendanceHours;
    }

    public void setAttendanceHours(BigDecimal attendanceHours)
    {
        this.attendanceHours = attendanceHours;
    }

    @DecimalMin(value = "0", message = "加班工时不能小于0")
    public BigDecimal getOvertimeHours()
    {
        return overtimeHours;
    }

    public void setOvertimeHours(BigDecimal overtimeHours)
    {
        this.overtimeHours = overtimeHours;
    }

    @NotBlank(message = "考勤状态不能为空")
    public String getAttendanceStatus()
    {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus)
    {
        this.attendanceStatus = attendanceStatus;
    }

    @NotBlank(message = "上报来源不能为空")
    public String getSourceType()
    {
        return sourceType;
    }

    public void setSourceType(String sourceType)
    {
        this.sourceType = sourceType;
    }

    public String getCollectStatus()
    {
        return collectStatus;
    }

    public void setCollectStatus(String collectStatus)
    {
        this.collectStatus = collectStatus;
    }

    public String getAttCheck()
    {
        return attCheck;
    }

    public void setAttCheck(String attCheck)
    {
        this.attCheck = attCheck;
    }

    @Size(max = 64, message = "设备编码长度不能超过64个字符")
    public String getDeviceCode()
    {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode)
    {
        this.deviceCode = deviceCode;
    }

    @Size(max = 255, message = "异常说明长度不能超过255个字符")
    public String getAnomalyRemark()
    {
        return anomalyRemark;
    }

    public void setAnomalyRemark(String anomalyRemark)
    {
        this.anomalyRemark = anomalyRemark;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("attendanceId", getAttendanceId())
            .append("attendanceNo", getAttendanceNo())
            .append("contractId", getContractId())
            .append("contractNo", getContractNo())
            .append("dispatchEnterpriseId", getDispatchEnterpriseId())
            .append("dispatchEnterpriseName", getDispatchEnterpriseName())
            .append("employerEnterpriseId", getEmployerEnterpriseId())
            .append("employerEnterpriseName", getEmployerEnterpriseName())
            .append("personId", getPersonId())
            .append("personName", getPersonName())
            .append("idCard", getIdCard())
            .append("regionCode", getRegionCode())
            .append("attendanceDate", getAttendanceDate())
            .append("clockInTime", getClockInTime())
            .append("clockOutTime", getClockOutTime())
            .append("shiftName", getShiftName())
            .append("attendanceHours", getAttendanceHours())
            .append("overtimeHours", getOvertimeHours())
            .append("attendanceStatus", getAttendanceStatus())
            .append("sourceType", getSourceType())
            .append("collectStatus", getCollectStatus())
            .append("attCheck", getAttCheck())
            .append("deviceCode", getDeviceCode())
            .append("anomalyRemark", getAnomalyRemark())
            .append("remark", getRemark())
            .toString();
    }
}
