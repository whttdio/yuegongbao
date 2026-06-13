package com.yuegongbao.ygb.cockpit.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;

public class YgbCockpitSnapshot extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long snapshotId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "统计日期", width = 18, dateFormat = "yyyy-MM-dd")
    private Date statDate;

    @Excel(name = "区域编码")
    private String regionCode;

    @Excel(name = "派遣企业数")
    private Integer dispatchCompanyCount;

    @Excel(name = "用工单位数")
    private Integer employerCount;

    @Excel(name = "派遣员工数")
    private Integer dispatchedWorkerCount;

    @Excel(name = "高危企业数")
    private Integer highRiskEnterpriseCount;

    @Excel(name = "工伤参保率")
    private BigDecimal insuranceRate;

    @Excel(name = "安责险覆盖率")
    private BigDecimal aqInsuranceRate;

    @Excel(name = "当日预警数")
    private Integer todayWarningCount;

    @Excel(name = "扩面完成率")
    private BigDecimal expandCompletionRate;

    @Excel(name = "新业态职业伤害发生率")
    private BigDecimal newInjuryRate;

    @Excel(name = "在线设备数")
    private Integer onlineDeviceCount;

    @Excel(name = "待处置预警数")
    private Integer pendingWarningCount;

    @Excel(name = "超期工伤事件数")
    private Integer overdueInjuryCount;

    private String sourceMode;

    public Long getSnapshotId()
    {
        return snapshotId;
    }

    public void setSnapshotId(Long snapshotId)
    {
        this.snapshotId = snapshotId;
    }

    public Date getStatDate()
    {
        return statDate;
    }

    public void setStatDate(Date statDate)
    {
        this.statDate = statDate;
    }

    public String getRegionCode()
    {
        return regionCode;
    }

    public void setRegionCode(String regionCode)
    {
        this.regionCode = regionCode;
    }

    public Integer getDispatchCompanyCount()
    {
        return dispatchCompanyCount;
    }

    public void setDispatchCompanyCount(Integer dispatchCompanyCount)
    {
        this.dispatchCompanyCount = dispatchCompanyCount;
    }

    public Integer getEmployerCount()
    {
        return employerCount;
    }

    public void setEmployerCount(Integer employerCount)
    {
        this.employerCount = employerCount;
    }

    public Integer getDispatchedWorkerCount()
    {
        return dispatchedWorkerCount;
    }

    public void setDispatchedWorkerCount(Integer dispatchedWorkerCount)
    {
        this.dispatchedWorkerCount = dispatchedWorkerCount;
    }

    public Integer getHighRiskEnterpriseCount()
    {
        return highRiskEnterpriseCount;
    }

    public void setHighRiskEnterpriseCount(Integer highRiskEnterpriseCount)
    {
        this.highRiskEnterpriseCount = highRiskEnterpriseCount;
    }

    public BigDecimal getInsuranceRate()
    {
        return insuranceRate;
    }

    public void setInsuranceRate(BigDecimal insuranceRate)
    {
        this.insuranceRate = insuranceRate;
    }

    public BigDecimal getAqInsuranceRate()
    {
        return aqInsuranceRate;
    }

    public void setAqInsuranceRate(BigDecimal aqInsuranceRate)
    {
        this.aqInsuranceRate = aqInsuranceRate;
    }

    public Integer getTodayWarningCount()
    {
        return todayWarningCount;
    }

    public void setTodayWarningCount(Integer todayWarningCount)
    {
        this.todayWarningCount = todayWarningCount;
    }

    public BigDecimal getExpandCompletionRate()
    {
        return expandCompletionRate;
    }

    public void setExpandCompletionRate(BigDecimal expandCompletionRate)
    {
        this.expandCompletionRate = expandCompletionRate;
    }

    public BigDecimal getNewInjuryRate()
    {
        return newInjuryRate;
    }

    public void setNewInjuryRate(BigDecimal newInjuryRate)
    {
        this.newInjuryRate = newInjuryRate;
    }

    public Integer getOnlineDeviceCount()
    {
        return onlineDeviceCount;
    }

    public void setOnlineDeviceCount(Integer onlineDeviceCount)
    {
        this.onlineDeviceCount = onlineDeviceCount;
    }

    public Integer getPendingWarningCount()
    {
        return pendingWarningCount;
    }

    public void setPendingWarningCount(Integer pendingWarningCount)
    {
        this.pendingWarningCount = pendingWarningCount;
    }

    public Integer getOverdueInjuryCount()
    {
        return overdueInjuryCount;
    }

    public void setOverdueInjuryCount(Integer overdueInjuryCount)
    {
        this.overdueInjuryCount = overdueInjuryCount;
    }

    public String getSourceMode()
    {
        return sourceMode;
    }

    public void setSourceMode(String sourceMode)
    {
        this.sourceMode = sourceMode;
    }
}
