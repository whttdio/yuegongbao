package com.yuegongbao.ygb.compliance.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 工资明细对象 t_salary_detail
 *
 * @author yuegongbao
 */
public class YgbSalaryDetail extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "明细ID")
    private Long detailId;

    @NotNull(message = "工资批次不能为空")
    private Long batchId;

    @Excel(name = "批次编号")
    private String batchNo;

    @Excel(name = "统计月份")
    private String statMonth;

    private Long monthlyId;

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

    @Excel(name = "累计工时")
    private BigDecimal totalHours;

    @Excel(name = "工资核验", readConverterExp = "0=不通过,1=通过")
    private String attCheck;

    @Excel(name = "应发工资")
    private BigDecimal payableAmount;

    @Excel(name = "扣减金额")
    private BigDecimal deductionAmount;

    @Excel(name = "实发工资")
    private BigDecimal netAmount;

    @Excel(name = "开户名")
    private String bankAccountName;

    @Excel(name = "银行卡号")
    private String bankAccountNo;

    @Excel(name = "发放状态", readConverterExp = "0=待发放,1=发放中,2=已发放,3=发放失败")
    private String payStatus;

    @Excel(name = "失败原因", width = 24)
    private String failReason;

    public Long getDetailId()
    {
        return detailId;
    }

    public void setDetailId(Long detailId)
    {
        this.detailId = detailId;
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

    public Long getMonthlyId()
    {
        return monthlyId;
    }

    public void setMonthlyId(Long monthlyId)
    {
        this.monthlyId = monthlyId;
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

    @DecimalMin(value = "0", message = "应发工资不能小于0")
    public BigDecimal getPayableAmount()
    {
        return payableAmount;
    }

    public void setPayableAmount(BigDecimal payableAmount)
    {
        this.payableAmount = payableAmount;
    }

    @DecimalMin(value = "0", message = "扣减金额不能小于0")
    public BigDecimal getDeductionAmount()
    {
        return deductionAmount;
    }

    public void setDeductionAmount(BigDecimal deductionAmount)
    {
        this.deductionAmount = deductionAmount;
    }

    @DecimalMin(value = "0", message = "实发工资不能小于0")
    public BigDecimal getNetAmount()
    {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount)
    {
        this.netAmount = netAmount;
    }

    @Size(max = 64, message = "开户名长度不能超过64个字符")
    public String getBankAccountName()
    {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName)
    {
        this.bankAccountName = bankAccountName;
    }

    @Size(max = 64, message = "银行卡号长度不能超过64个字符")
    public String getBankAccountNo()
    {
        return bankAccountNo;
    }

    public void setBankAccountNo(String bankAccountNo)
    {
        this.bankAccountNo = bankAccountNo;
    }

    public String getPayStatus()
    {
        return payStatus;
    }

    public void setPayStatus(String payStatus)
    {
        this.payStatus = payStatus;
    }

    @Size(max = 255, message = "失败原因长度不能超过255个字符")
    public String getFailReason()
    {
        return failReason;
    }

    public void setFailReason(String failReason)
    {
        this.failReason = failReason;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("detailId", getDetailId())
            .append("batchId", getBatchId())
            .append("batchNo", getBatchNo())
            .append("statMonth", getStatMonth())
            .append("monthlyId", getMonthlyId())
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
            .append("totalHours", getTotalHours())
            .append("attCheck", getAttCheck())
            .append("payableAmount", getPayableAmount())
            .append("deductionAmount", getDeductionAmount())
            .append("netAmount", getNetAmount())
            .append("bankAccountName", getBankAccountName())
            .append("bankAccountNo", getBankAccountNo())
            .append("payStatus", getPayStatus())
            .append("failReason", getFailReason())
            .append("remark", getRemark())
            .toString();
    }
}
