package com.yuegongbao.ygb.compliance.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 考勤归集对象 t_attendance_monthly
 *
 * @author yuegongbao
 */
public class YgbAttendanceMonthly extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "归集ID")
    private Long monthlyId;

    @Excel(name = "统计月份")
    private String statMonth;

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

    @Excel(name = "出勤天数")
    private Integer attendanceDays;

    @Excel(name = "缺勤天数")
    private Integer absenceDays;

    @Excel(name = "迟到天数")
    private Integer lateDays;

    @Excel(name = "早退天数")
    private Integer earlyLeaveDays;

    @Excel(name = "加班工时")
    private BigDecimal overtimeHours;

    @Excel(name = "累计工时")
    private BigDecimal totalHours;

    @Excel(name = "工资核验", readConverterExp = "0=不通过,1=通过")
    private String attCheck;

    @Excel(name = "归集状态", readConverterExp = "1=待归集,2=已归集,3=已核验,4=已发薪")
    private String summaryStatus;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "最后考勤日", width = 18, dateFormat = "yyyy-MM-dd")
    private Date lastAttendanceDate;

    public Long getMonthlyId()
    {
        return monthlyId;
    }

    public void setMonthlyId(Long monthlyId)
    {
        this.monthlyId = monthlyId;
    }

    @NotBlank(message = "统计月份不能为空")
    @Size(max = 7, message = "统计月份格式应为yyyy-MM")
    public String getStatMonth()
    {
        return statMonth;
    }

    public void setStatMonth(String statMonth)
    {
        this.statMonth = statMonth;
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

    public Integer getAttendanceDays()
    {
        return attendanceDays;
    }

    public void setAttendanceDays(Integer attendanceDays)
    {
        this.attendanceDays = attendanceDays;
    }

    public Integer getAbsenceDays()
    {
        return absenceDays;
    }

    public void setAbsenceDays(Integer absenceDays)
    {
        this.absenceDays = absenceDays;
    }

    public Integer getLateDays()
    {
        return lateDays;
    }

    public void setLateDays(Integer lateDays)
    {
        this.lateDays = lateDays;
    }

    public Integer getEarlyLeaveDays()
    {
        return earlyLeaveDays;
    }

    public void setEarlyLeaveDays(Integer earlyLeaveDays)
    {
        this.earlyLeaveDays = earlyLeaveDays;
    }

    public BigDecimal getOvertimeHours()
    {
        return overtimeHours;
    }

    public void setOvertimeHours(BigDecimal overtimeHours)
    {
        this.overtimeHours = overtimeHours;
    }

    public BigDecimal getTotalHours()
    {
        return totalHours;
    }

    public void setTotalHours(BigDecimal totalHours)
    {
        this.totalHours = totalHours;
    }

    public String getAttCheck()
    {
        return attCheck;
    }

    public void setAttCheck(String attCheck)
    {
        this.attCheck = attCheck;
    }

    public String getSummaryStatus()
    {
        return summaryStatus;
    }

    public void setSummaryStatus(String summaryStatus)
    {
        this.summaryStatus = summaryStatus;
    }

    public Date getLastAttendanceDate()
    {
        return lastAttendanceDate;
    }

    public void setLastAttendanceDate(Date lastAttendanceDate)
    {
        this.lastAttendanceDate = lastAttendanceDate;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("monthlyId", getMonthlyId())
            .append("statMonth", getStatMonth())
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
            .append("attendanceDays", getAttendanceDays())
            .append("absenceDays", getAbsenceDays())
            .append("lateDays", getLateDays())
            .append("earlyLeaveDays", getEarlyLeaveDays())
            .append("overtimeHours", getOvertimeHours())
            .append("totalHours", getTotalHours())
            .append("attCheck", getAttCheck())
            .append("summaryStatus", getSummaryStatus())
            .append("lastAttendanceDate", getLastAttendanceDate())
            .append("remark", getRemark())
            .toString();
    }
}
