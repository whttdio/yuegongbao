package com.yuegongbao.ygb.newform.domain;

import java.math.BigDecimal;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;

public class YgbNewformPlatformStat extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "统计月份")
    private String statMonth;

    @Excel(name = "区域编码")
    private String regionCode;

    private String regionName;

    private Long enterpriseId;

    @Excel(name = "平台企业")
    private String platformName;

    @Excel(name = "人员数")
    private Integer workerCount;

    @Excel(name = "已参保人数")
    private Integer insuredCount;

    @Excel(name = "职业伤害已参保人数")
    private Integer injuryInsuredCount;

    @Excel(name = "职业伤害未参保人数")
    private Integer injuryUninsuredCount;

    @Excel(name = "职业伤害停保人数")
    private Integer injuryStoppedCount;

    @Excel(name = "预警人数")
    private Integer warningCount;

    @Excel(name = "平均收入")
    private BigDecimal averageIncome;

    private BigDecimal incomeTotal;

    private Integer incomeRecordCount;

    public String getStatMonth()
    {
        return statMonth;
    }

    public void setStatMonth(String statMonth)
    {
        this.statMonth = statMonth;
    }

    public String getRegionCode()
    {
        return regionCode;
    }

    public void setRegionCode(String regionCode)
    {
        this.regionCode = regionCode;
    }

    public String getRegionName()
    {
        return regionName;
    }

    public void setRegionName(String regionName)
    {
        this.regionName = regionName;
    }

    public Long getEnterpriseId()
    {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }

    public String getPlatformName()
    {
        return platformName;
    }

    public void setPlatformName(String platformName)
    {
        this.platformName = platformName;
    }

    public Integer getWorkerCount()
    {
        return workerCount;
    }

    public void setWorkerCount(Integer workerCount)
    {
        this.workerCount = workerCount;
    }

    public Integer getInsuredCount()
    {
        return insuredCount;
    }

    public void setInsuredCount(Integer insuredCount)
    {
        this.insuredCount = insuredCount;
    }

    public Integer getInjuryInsuredCount()
    {
        return injuryInsuredCount;
    }

    public void setInjuryInsuredCount(Integer injuryInsuredCount)
    {
        this.injuryInsuredCount = injuryInsuredCount;
    }

    public Integer getInjuryUninsuredCount()
    {
        return injuryUninsuredCount;
    }

    public void setInjuryUninsuredCount(Integer injuryUninsuredCount)
    {
        this.injuryUninsuredCount = injuryUninsuredCount;
    }

    public Integer getInjuryStoppedCount()
    {
        return injuryStoppedCount;
    }

    public void setInjuryStoppedCount(Integer injuryStoppedCount)
    {
        this.injuryStoppedCount = injuryStoppedCount;
    }

    public Integer getWarningCount()
    {
        return warningCount;
    }

    public void setWarningCount(Integer warningCount)
    {
        this.warningCount = warningCount;
    }

    public BigDecimal getAverageIncome()
    {
        return averageIncome;
    }

    public void setAverageIncome(BigDecimal averageIncome)
    {
        this.averageIncome = averageIncome;
    }

    public BigDecimal getIncomeTotal()
    {
        return incomeTotal;
    }

    public void setIncomeTotal(BigDecimal incomeTotal)
    {
        this.incomeTotal = incomeTotal;
    }

    public Integer getIncomeRecordCount()
    {
        return incomeRecordCount;
    }

    public void setIncomeRecordCount(Integer incomeRecordCount)
    {
        this.incomeRecordCount = incomeRecordCount;
    }
}
