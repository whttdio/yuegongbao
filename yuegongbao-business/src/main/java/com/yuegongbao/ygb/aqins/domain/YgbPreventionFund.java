package com.yuegongbao.ygb.aqins.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;

/**
 * 事故预防资金池对象 t_prevention_fund。
 *
 * @author yuegongbao
 */
public class YgbPreventionFund extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "资金池ID")
    private Long fundId;

    private Long policyId;

    @Excel(name = "统计月份")
    private String statMonth;

    private Long enterpriseId;

    @Excel(name = "企业名称")
    private String enterpriseName;

    @Excel(name = "区域编码")
    private String regionCode;

    @Excel(name = "计提金额")
    private BigDecimal accruedAmount;

    @Excel(name = "已使用金额")
    private BigDecimal usedAmount;

    @Excel(name = "可用余额")
    private BigDecimal remainingAmount;

    @Excel(name = "资金状态", readConverterExp = "0=待计提,1=可使用,2=使用中,3=已核销")
    private String fundStatus;

    @Excel(name = "使用用途", width = 30)
    private String usagePurpose;

    @Excel(name = "凭证地址", width = 30)
    private String evidenceUrl;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最近结算时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date lastSettleTime;

    private String sourceMode;

    public Long getFundId()
    {
        return fundId;
    }

    public void setFundId(Long fundId)
    {
        this.fundId = fundId;
    }

    public Long getPolicyId()
    {
        return policyId;
    }

    public void setPolicyId(Long policyId)
    {
        this.policyId = policyId;
    }

    public String getStatMonth()
    {
        return statMonth;
    }

    public void setStatMonth(String statMonth)
    {
        this.statMonth = statMonth;
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

    public BigDecimal getAccruedAmount()
    {
        return accruedAmount;
    }

    public void setAccruedAmount(BigDecimal accruedAmount)
    {
        this.accruedAmount = accruedAmount;
    }

    public BigDecimal getUsedAmount()
    {
        return usedAmount;
    }

    public void setUsedAmount(BigDecimal usedAmount)
    {
        this.usedAmount = usedAmount;
    }

    public BigDecimal getRemainingAmount()
    {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount)
    {
        this.remainingAmount = remainingAmount;
    }

    public String getFundStatus()
    {
        return fundStatus;
    }

    public void setFundStatus(String fundStatus)
    {
        this.fundStatus = fundStatus;
    }

    public String getUsagePurpose()
    {
        return usagePurpose;
    }

    public void setUsagePurpose(String usagePurpose)
    {
        this.usagePurpose = usagePurpose;
    }

    public String getEvidenceUrl()
    {
        return evidenceUrl;
    }

    public void setEvidenceUrl(String evidenceUrl)
    {
        this.evidenceUrl = evidenceUrl;
    }

    public Date getLastSettleTime()
    {
        return lastSettleTime;
    }

    public void setLastSettleTime(Date lastSettleTime)
    {
        this.lastSettleTime = lastSettleTime;
    }

    public String getSourceMode()
    {
        return sourceMode;
    }

    public void setSourceMode(String sourceMode)
    {
        this.sourceMode = sourceMode;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("fundId", getFundId())
            .append("policyId", getPolicyId())
            .append("statMonth", getStatMonth())
            .append("enterpriseName", getEnterpriseName())
            .append("accruedAmount", getAccruedAmount())
            .append("usedAmount", getUsedAmount())
            .append("remainingAmount", getRemainingAmount())
            .append("fundStatus", getFundStatus())
            .toString();
    }
}
