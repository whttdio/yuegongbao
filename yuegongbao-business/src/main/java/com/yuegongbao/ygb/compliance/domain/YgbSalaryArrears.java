package com.yuegongbao.ygb.compliance.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;

public class YgbSalaryArrears extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "拖欠ID")
    private Long arrearsId;

    @Excel(name = "批次ID")
    private Long batchId;

    @Excel(name = "批次号")
    private String batchNo;

    @Excel(name = "统计月份")
    private String statMonth;

    @Excel(name = "派遣企业ID")
    private Long dispatchEnterpriseId;

    @Excel(name = "派遣企业")
    private String dispatchEnterpriseName;

    @Excel(name = "区域编码")
    private String regionCode;

    @Excel(name = "应发人数")
    private Integer totalPersonCount;

    @Excel(name = "应发金额")
    private BigDecimal totalPayableAmount;

    @Excel(name = "已发金额")
    private BigDecimal totalPaidAmount;

    @Excel(name = "到账金额")
    private BigDecimal accountReceivedAmount;

    @Excel(name = "拖欠金额")
    private BigDecimal arrearsAmount;

    @Excel(name = "到账缺口")
    private BigDecimal accountGapAmount;

    @Excel(name = "逾期天数")
    private Integer overdueDays;

    @Excel(name = "处置状态")
    private String handleStatus;

    @Excel(name = "预警ID")
    private Long warningId;

    @Excel(name = "跟进人")
    private String followUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "跟进时间", width = 22, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date followTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "下次跟进", width = 22, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date nextFollowTime;

    @Excel(name = "处理结果", width = 30)
    private String handleResult;

    public Long getArrearsId()
    {
        return arrearsId;
    }

    public void setArrearsId(Long arrearsId)
    {
        this.arrearsId = arrearsId;
    }

    public Long getBatchId()
    {
        return batchId;
    }

    public void setBatchId(Long batchId)
    {
        this.batchId = batchId;
    }

    public String getBatchNo()
    {
        return batchNo;
    }

    public void setBatchNo(String batchNo)
    {
        this.batchNo = batchNo;
    }

    public String getStatMonth()
    {
        return statMonth;
    }

    public void setStatMonth(String statMonth)
    {
        this.statMonth = statMonth;
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

    public String getRegionCode()
    {
        return regionCode;
    }

    public void setRegionCode(String regionCode)
    {
        this.regionCode = regionCode;
    }

    public Integer getTotalPersonCount()
    {
        return totalPersonCount;
    }

    public void setTotalPersonCount(Integer totalPersonCount)
    {
        this.totalPersonCount = totalPersonCount;
    }

    public BigDecimal getTotalPayableAmount()
    {
        return totalPayableAmount;
    }

    public void setTotalPayableAmount(BigDecimal totalPayableAmount)
    {
        this.totalPayableAmount = totalPayableAmount;
    }

    public BigDecimal getTotalPaidAmount()
    {
        return totalPaidAmount;
    }

    public void setTotalPaidAmount(BigDecimal totalPaidAmount)
    {
        this.totalPaidAmount = totalPaidAmount;
    }

    public BigDecimal getAccountReceivedAmount()
    {
        return accountReceivedAmount;
    }

    public void setAccountReceivedAmount(BigDecimal accountReceivedAmount)
    {
        this.accountReceivedAmount = accountReceivedAmount;
    }

    public BigDecimal getArrearsAmount()
    {
        return arrearsAmount;
    }

    public void setArrearsAmount(BigDecimal arrearsAmount)
    {
        this.arrearsAmount = arrearsAmount;
    }

    public BigDecimal getAccountGapAmount()
    {
        return accountGapAmount;
    }

    public void setAccountGapAmount(BigDecimal accountGapAmount)
    {
        this.accountGapAmount = accountGapAmount;
    }

    public Integer getOverdueDays()
    {
        return overdueDays;
    }

    public void setOverdueDays(Integer overdueDays)
    {
        this.overdueDays = overdueDays;
    }

    public String getHandleStatus()
    {
        return handleStatus;
    }

    public void setHandleStatus(String handleStatus)
    {
        this.handleStatus = handleStatus;
    }

    public Long getWarningId()
    {
        return warningId;
    }

    public void setWarningId(Long warningId)
    {
        this.warningId = warningId;
    }

    public String getFollowUser()
    {
        return followUser;
    }

    public void setFollowUser(String followUser)
    {
        this.followUser = followUser;
    }

    public Date getFollowTime()
    {
        return followTime;
    }

    public void setFollowTime(Date followTime)
    {
        this.followTime = followTime;
    }

    public Date getNextFollowTime()
    {
        return nextFollowTime;
    }

    public void setNextFollowTime(Date nextFollowTime)
    {
        this.nextFollowTime = nextFollowTime;
    }

    public String getHandleResult()
    {
        return handleResult;
    }

    public void setHandleResult(String handleResult)
    {
        this.handleResult = handleResult;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("arrearsId", getArrearsId())
            .append("batchId", getBatchId())
            .append("batchNo", getBatchNo())
            .append("statMonth", getStatMonth())
            .append("dispatchEnterpriseId", getDispatchEnterpriseId())
            .append("dispatchEnterpriseName", getDispatchEnterpriseName())
            .append("regionCode", getRegionCode())
            .append("totalPersonCount", getTotalPersonCount())
            .append("totalPayableAmount", getTotalPayableAmount())
            .append("totalPaidAmount", getTotalPaidAmount())
            .append("accountReceivedAmount", getAccountReceivedAmount())
            .append("arrearsAmount", getArrearsAmount())
            .append("accountGapAmount", getAccountGapAmount())
            .append("overdueDays", getOverdueDays())
            .append("handleStatus", getHandleStatus())
            .append("warningId", getWarningId())
            .append("followUser", getFollowUser())
            .append("followTime", getFollowTime())
            .append("nextFollowTime", getNextFollowTime())
            .append("handleResult", getHandleResult())
            .append("remark", getRemark())
            .toString();
    }
}
