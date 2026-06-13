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
 * 合同备案对象 t_labor_contract
 *
 * @author yuegongbao
 */
public class YgbContract extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "合同ID")
    private Long contractId;

    @Excel(name = "合同编号")
    private String contractNo;

    @NotNull(message = "派遣单位不能为空")
    private Long dispatchEnterpriseId;

    @Excel(name = "派遣单位")
    private String dispatchEnterpriseName;

    @NotNull(message = "用工单位不能为空")
    private Long employerEnterpriseId;

    @Excel(name = "用工单位")
    private String employerEnterpriseName;

    @NotNull(message = "所属人员不能为空")
    private Long personId;

    @Excel(name = "劳动者姓名")
    private String personName;

    @Excel(name = "身份证号")
    private String idCard;

    @Excel(name = "区域编码")
    private String regionCode;

    @Excel(name = "合同类型", readConverterExp = "1=劳动合同,2=派遣协议,3=用工协议")
    private String contractType;

    @Excel(name = "备案状态", readConverterExp = "0=草稿,1=待备案,2=已备案,3=驳回,4=已到期,5=已解除")
    private String contractStatus;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "签订日期", width = 18, dateFormat = "yyyy-MM-dd")
    private Date signDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生效日期", width = 18, dateFormat = "yyyy-MM-dd")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "终止日期", width = 18, dateFormat = "yyyy-MM-dd")
    private Date endDate;

    @Excel(name = "月工资标准")
    private BigDecimal monthlyWage;

    @Excel(name = "备案编号")
    private String filingNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "备案时间", width = 22, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date filingTime;

    @Excel(name = "OCR状态", readConverterExp = "0=未识别,1=识别成功,2=识别失败")
    private String ocrStatus;

    @Excel(name = "条款校验", readConverterExp = "0=未校验,1=通过,2=缺失")
    private String clauseCheckStatus;

    @Excel(name = "区块链哈希", width = 24)
    private String blockchainHash;

    private String contractFileUrl;

    public Long getContractId()
    {
        return contractId;
    }

    public void setContractId(Long contractId)
    {
        this.contractId = contractId;
    }

    @NotBlank(message = "合同编号不能为空")
    @Size(max = 64, message = "合同编号长度不能超过64个字符")
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

    @NotBlank(message = "合同类型不能为空")
    public String getContractType()
    {
        return contractType;
    }

    public void setContractType(String contractType)
    {
        this.contractType = contractType;
    }

    @NotBlank(message = "备案状态不能为空")
    public String getContractStatus()
    {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus)
    {
        this.contractStatus = contractStatus;
    }

    public Date getSignDate()
    {
        return signDate;
    }

    public void setSignDate(Date signDate)
    {
        this.signDate = signDate;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    @DecimalMin(value = "0", inclusive = false, message = "月工资标准必须大于0")
    public BigDecimal getMonthlyWage()
    {
        return monthlyWage;
    }

    public void setMonthlyWage(BigDecimal monthlyWage)
    {
        this.monthlyWage = monthlyWage;
    }

    @Size(max = 64, message = "备案编号长度不能超过64个字符")
    public String getFilingNo()
    {
        return filingNo;
    }

    public void setFilingNo(String filingNo)
    {
        this.filingNo = filingNo;
    }

    public Date getFilingTime()
    {
        return filingTime;
    }

    public void setFilingTime(Date filingTime)
    {
        this.filingTime = filingTime;
    }

    public String getOcrStatus()
    {
        return ocrStatus;
    }

    public void setOcrStatus(String ocrStatus)
    {
        this.ocrStatus = ocrStatus;
    }

    public String getClauseCheckStatus()
    {
        return clauseCheckStatus;
    }

    public void setClauseCheckStatus(String clauseCheckStatus)
    {
        this.clauseCheckStatus = clauseCheckStatus;
    }

    @Size(max = 128, message = "区块链哈希长度不能超过128个字符")
    public String getBlockchainHash()
    {
        return blockchainHash;
    }

    public void setBlockchainHash(String blockchainHash)
    {
        this.blockchainHash = blockchainHash;
    }

    @Size(max = 255, message = "合同文件地址长度不能超过255个字符")
    public String getContractFileUrl()
    {
        return contractFileUrl;
    }

    public void setContractFileUrl(String contractFileUrl)
    {
        this.contractFileUrl = contractFileUrl;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
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
            .append("contractType", getContractType())
            .append("contractStatus", getContractStatus())
            .append("signDate", getSignDate())
            .append("startDate", getStartDate())
            .append("endDate", getEndDate())
            .append("monthlyWage", getMonthlyWage())
            .append("filingNo", getFilingNo())
            .append("filingTime", getFilingTime())
            .append("ocrStatus", getOcrStatus())
            .append("clauseCheckStatus", getClauseCheckStatus())
            .append("blockchainHash", getBlockchainHash())
            .append("contractFileUrl", getContractFileUrl())
            .append("remark", getRemark())
            .toString();
    }
}
