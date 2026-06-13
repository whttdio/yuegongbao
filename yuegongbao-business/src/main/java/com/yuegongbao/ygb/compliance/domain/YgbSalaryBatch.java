package com.yuegongbao.ygb.compliance.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 工资批次对象 t_salary_batch
 *
 * @author yuegongbao
 */
public class YgbSalaryBatch extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "批次ID")
    private Long batchId;

    @Excel(name = "批次编号")
    private String batchNo;

    @Excel(name = "统计月份")
    private String statMonth;

    @NotNull(message = "派遣单位不能为空")
    private Long dispatchEnterpriseId;

    @Excel(name = "派遣单位")
    private String dispatchEnterpriseName;

    @Excel(name = "区域编码")
    private String regionCode;

    @Excel(name = "人数")
    private Integer totalPersonCount;

    @Excel(name = "应发合计")
    private BigDecimal totalPayableAmount;

    @Excel(name = "实发合计")
    private BigDecimal totalPaidAmount;

    @Excel(name = "到账金额")
    private BigDecimal accountReceivedAmount;

    @Excel(name = "批次状态", readConverterExp = "0=草稿,1=待确认,2=待入账,3=待生成明细,4=待代发,5=代发中,6=已发放,7=发放失败")
    private String batchStatus;

    @Excel(name = "到账状态", readConverterExp = "0=未到账,1=已到账")
    private String accountStatus;

    @Excel(name = "监管账户名")
    private String regulatorAccountName;

    @Excel(name = "监管账号")
    private String regulatorAccountNo;

    @Excel(name = "银行流水号")
    private String bankSerialNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "提交时间", width = 22, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "发放时间", width = 22, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date paidTime;

    public Long getBatchId()
    {
        return batchId;
    }

    public void setBatchId(Long batchId)
    {
        this.batchId = batchId;
    }

    @NotBlank(message = "批次编号不能为空")
    @Size(max = 64, message = "批次编号长度不能超过64个字符")
    public String getBatchNo()
    {
        return batchNo;
    }

    public void setBatchNo(String batchNo)
    {
        this.batchNo = batchNo;
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

    @NotBlank(message = "批次状态不能为空")
    public String getBatchStatus()
    {
        return batchStatus;
    }

    public void setBatchStatus(String batchStatus)
    {
        this.batchStatus = batchStatus;
    }

    public String getAccountStatus()
    {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus)
    {
        this.accountStatus = accountStatus;
    }

    @Size(max = 64, message = "监管账户名长度不能超过64个字符")
    public String getRegulatorAccountName()
    {
        return regulatorAccountName;
    }

    public void setRegulatorAccountName(String regulatorAccountName)
    {
        this.regulatorAccountName = regulatorAccountName;
    }

    @Size(max = 64, message = "监管账号长度不能超过64个字符")
    public String getRegulatorAccountNo()
    {
        return regulatorAccountNo;
    }

    public void setRegulatorAccountNo(String regulatorAccountNo)
    {
        this.regulatorAccountNo = regulatorAccountNo;
    }

    @Size(max = 64, message = "银行流水号长度不能超过64个字符")
    public String getBankSerialNo()
    {
        return bankSerialNo;
    }

    public void setBankSerialNo(String bankSerialNo)
    {
        this.bankSerialNo = bankSerialNo;
    }

    public Date getSubmitTime()
    {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime)
    {
        this.submitTime = submitTime;
    }

    public Date getPaidTime()
    {
        return paidTime;
    }

    public void setPaidTime(Date paidTime)
    {
        this.paidTime = paidTime;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
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
            .append("batchStatus", getBatchStatus())
            .append("accountStatus", getAccountStatus())
            .append("regulatorAccountName", getRegulatorAccountName())
            .append("regulatorAccountNo", getRegulatorAccountNo())
            .append("bankSerialNo", getBankSerialNo())
            .append("submitTime", getSubmitTime())
            .append("paidTime", getPaidTime())
            .append("remark", getRemark())
            .toString();
    }
}
